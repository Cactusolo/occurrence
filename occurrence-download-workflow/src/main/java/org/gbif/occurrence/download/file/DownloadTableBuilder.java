package org.gbif.occurrence.download.file;

import org.gbif.api.service.registry.DatasetOccurrenceDownloadUsageService;
import org.gbif.api.service.registry.DatasetService;
import org.gbif.common.search.inject.SolrModule;
import org.gbif.occurrence.common.download.DownloadUtils;
import org.gbif.occurrence.download.file.OccurrenceFileWriter.Configuration;
import org.gbif.occurrence.download.util.RegistryClientUtil;
import org.gbif.service.guice.PrivateServiceModule;
import org.gbif.utils.file.properties.PropertiesUtil;
import org.gbif.wrangler.lock.LockFactory;
import org.gbif.wrangler.lock.zookeeper.ZooKeeperLockFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;

import akka.dispatch.ExecutionContextExecutorService;
import akka.dispatch.ExecutionContexts;
import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that wraps the process of creating the occurrence and citation files.
 * This class can be executed as jvm application that receives the following arguments:
 * - occurrence data outputFile
 * - citationFileName
 * - solr query
 * - hadoop name node, required to access the hdfs.
 * - hadoop dfs output directory where the citation and data files will be copied
 */
public class DownloadTableBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(DownloadTableBuilder.class);

  /**
   * Private guice module that provides bindings the required Modules and dependencies.
   * The following class are exposed:
   * - CuratorFramework: this class is exposed only to close the zookeeper connections properly.
   * - OccurrenceFileWriter: class that creates the occurrence data and citations file.
   */
  private static final class DownloadTableBuilderModule extends PrivateServiceModule {

    private static final String LOCKING_PATH = "/runningJobs/";

    private final String downloadId;

    private final String registryWsUri;

    private static final String PROPERTIES_PREFIX = "occurrence.download.";

    /**
     * Default constructor.
     */
    public DownloadTableBuilderModule(Properties properties, String downloadId) {
      super(PROPERTIES_PREFIX, properties);
      this.downloadId = downloadId;
      this.registryWsUri = properties.getProperty("registry.ws.url");
    }

    @Override
    protected void configureService() {
      install(new SolrModule());
      bind(Configuration.class).in(Scopes.SINGLETON);
      bind(String.class).annotatedWith(Names.named("downloadId")).toInstance(downloadId);
      bind(OccurrenceFileWriter.class);
      bind(OccurrenceMapReader.class);
      expose(CuratorFramework.class);
      expose(OccurrenceFileWriter.class);
      expose(DatasetOccurrenceDownloadUsageService.class);
    }

    @Provides
    @Singleton
    CuratorFramework provideCuratorFramework(@Named("zookeeper.namespace") String zookeeperNamespace,
      @Named("zookeeper.quorum") String zookeeperConnection,
      @Named("zookeeper.sleep_time") Integer sleepTime, @Named("zookeeper.max_retries") Integer maxRetries)
      throws IOException {
      CuratorFramework curator = CuratorFrameworkFactory.builder()
        .namespace(zookeeperNamespace)
        .retryPolicy(new ExponentialBackoffRetry(sleepTime, maxRetries))
        .connectString(zookeeperConnection)
        .build();
      curator.start();
      return curator;
    }

    @Provides
    @Singleton
    DatasetOccurrenceDownloadUsageService provideDatasetOccurrenceDownloadUsageService() {
      RegistryClientUtil registryClientUtil = new RegistryClientUtil(getVerbatimProperties());
      return registryClientUtil.setupDatasetUsageService(registryWsUri);
    }

    @Provides
    @Singleton
    DatasetService provideDatasetService() {
      RegistryClientUtil registryClientUtil = new RegistryClientUtil(getVerbatimProperties());
      return registryClientUtil.setupDatasetService(registryWsUri);
    }

    @Provides
    ExecutionContextExecutorService provideExecutionContextExecutorService(
      @Named("job.max_threads") int maxThreads) {
      return ExecutionContexts.fromExecutorService(Executors.newFixedThreadPool(maxThreads));
    }

    @Provides
    HTablePool provideHTablePool(@Named("max_connection_pool") Integer maxConnectionPool) {
      return new HTablePool(HBaseConfiguration.create(), maxConnectionPool);
    }

    @Provides
    LockFactory provideLock(CuratorFramework curatorFramework,
      @Named("max_global_threads") Integer maxGlobalThreads) {
      return new ZooKeeperLockFactory(curatorFramework, maxGlobalThreads, LOCKING_PATH);
    }

  }

  private static final String CONF_FILE = "occurrence-download.properties";


  /**
   * Entry point, receives the following arguments:
   * - occurrence interpreted data output file
   * - occurrence verbatim data output file
   * - citationFileName
   * - solr query
   * - hadoop name node, required to access the hdfs.
   * - hadoop dfs output directory where the citation and data files will be copied
   */
  public static void main(String[] args) throws IOException {
    DownloadTableBuilder downloadTableBuilder = new DownloadTableBuilder();
    downloadTableBuilder.createFiles(args[0], args[1], args[2], args[3], args[4], args[5], args[6],
      DownloadUtils.workflowToDownloadId(args[7]));
  }

  /**
   * Executes the file creation process.
   * Citation and data files are created in the local file system and the moved to hadoop file system directory
   * 'hdfsPath'.
   */
  public void createFiles(String interpretedOutputFile, String verbatimOutputFile,
    String multimediaOutputFile, String citationFileName,
    String query, String nameNode, String hdfsPath, String downloadId)
    throws IOException {
    final Injector injector = createInjector(downloadId);
    CuratorFramework curator = injector.getInstance(CuratorFramework.class);
    OccurrenceFileWriter occurrenceFileWriter = injector.getInstance(OccurrenceFileWriter.class);
    occurrenceFileWriter.run(interpretedOutputFile, verbatimOutputFile, multimediaOutputFile, citationFileName, query);
    FileSystem fileSystem = getHadoopFileSystem(nameNode);
    copyDataFiles(hdfsPath, fileSystem, interpretedOutputFile, verbatimOutputFile, multimediaOutputFile,
      citationFileName);
    curator.close();
  }

  /**
   * Copies data files into the destination path.
   */
  private void copyDataFiles(String hdfsPath, FileSystem fileSystem, String... dataFiles) throws IOException {
    for (String dataFile : dataFiles) {
      if (new File(dataFile).exists()) {
        fileSystem.copyFromLocalFile(true, new Path(dataFile),
          buildDestinationPath(hdfsPath, dataFile));
      }
    }
  }

  /**
   * Build the destination path for occurrence data a citation files.
   * The built path will have the form: hdfsRootPath Path.SEPARATOR + dataFile + Path.SEPARATOR + dataFile; which means
   * that creates a directory with the same file name, this is the format expected later in the download workflow by the
   * ArchiveBuilder class since the output files can be generated by DownloadTableBuilder class or else using Hive.
   */
  private Path buildDestinationPath(String hdfsRootPath, String dataFile) {
    return new Path(hdfsRootPath + Path.SEPARATOR + dataFile + Path.SEPARATOR + dataFile);
  }

  /**
   * Utility method that creates the Guice injector.
   */
  private Injector createInjector(String jobId) {
    try {
      return Guice.createInjector(new DownloadTableBuilderModule(PropertiesUtil.loadProperties(CONF_FILE), jobId));
    } catch (IllegalArgumentException e) {
      LOG.error("Error initializing injection module", e);
      Throwables.propagate(e);
    } catch (IOException e) {
      LOG.error("Error initializing injection module", e);
      Throwables.propagate(e);
    }
    throw new IllegalStateException("Guice couldn't be initialized");
  }

  private FileSystem getHadoopFileSystem(String nameNode) {
    try {
      org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
      conf.set(CommonConfigurationKeys.FS_DEFAULT_NAME_KEY, nameNode);
      return FileSystem.get(conf);
    } catch (IOException e) {
      LOG.error("Error accessing hadoop HDFS", e);
      throw new IllegalArgumentException(e);
    }
  }

}
