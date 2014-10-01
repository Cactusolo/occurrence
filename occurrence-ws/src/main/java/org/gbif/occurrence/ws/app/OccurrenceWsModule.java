package org.gbif.occurrence.ws.app;

import org.gbif.api.service.occurrence.DownloadRequestService;
import org.gbif.api.service.occurrence.OccurrenceService;
import org.gbif.api.service.registry.OccurrenceDownloadService;
import org.gbif.occurrence.download.service.CallbackService;
import org.gbif.occurrence.ws.resources.DownloadResource;
import org.gbif.occurrence.ws.resources.FeaturedOccurrenceReader;
import org.gbif.occurrence.ws.resources.OccurrenceResource;
import org.gbif.occurrence.ws.resources.OccurrenceSearchResource;
import org.gbif.service.guice.PrivateServiceModule;
import org.gbif.ws.server.interceptor.NullToNotFound;
import org.gbif.ws.server.interceptor.NullToNotFoundInterceptor;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;

/**
 * Guice module to be used in Dropwizard applications.
 */
public class OccurrenceWsModule extends AbstractModule{

  private final Properties properties;

  public OccurrenceWsModule(Properties properties){
    this.properties = properties;
  }

  @Override
  protected void configure() {
    bind(OccurrenceSearchResource.class);
    install(new FeaturedModule(properties));
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(NullToNotFound.class), new NullToNotFoundInterceptor());
  }

  @Provides @Singleton
  protected OccurrenceResource providesOccurrenceResource(OccurrenceService occurrenceService, FeaturedOccurrenceReader featuredOccurrenceReader){
    return new OccurrenceResource(occurrenceService, featuredOccurrenceReader);
  }

  @Provides @Singleton
  protected DownloadResource providesDownloadResource(DownloadRequestService service, CallbackService callbackService, OccurrenceDownloadService occurrenceDownloadService){
    return new DownloadResource(service, callbackService, occurrenceDownloadService);
  }

  /**
   * Wires up the featured module to be able to access the HBase table.
   */
  private static class FeaturedModule extends PrivateServiceModule {

    private static final String PREFIX = "occurrence.db.";

    public FeaturedModule(Properties properties) {
      super(PREFIX, properties);
    }

    @Provides
    @Named("featured_table_pool")
    public HTablePool provideHTablePool(@Named("max_connection_pool") Integer maxConnectionPool) {
      return new HTablePool(HBaseConfiguration.create(), maxConnectionPool);
    }

    @Override
    protected void configureService() {
      bind(FeaturedOccurrenceReader.class);
      expose(FeaturedOccurrenceReader.class);
    }
  }
}
