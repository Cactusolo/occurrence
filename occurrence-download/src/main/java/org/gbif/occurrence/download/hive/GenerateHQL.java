package org.gbif.occurrence.download.hive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates HQL scripts dynamically which are used to create the download HDFS tables, and querying when a user issues
 * a download request.
 * <p/>
 * Rather than generating HQL only at runtime, scripts are generated at build time using a maven
 * plugin, to aid testing, development and debugging.  Freemarker is used as a templating language
 * to allow rapid development, but the sections which are verbose, and subject to easy typos are controlled
 * by enumerations in code.  The same enumerations are used in many places in the codebase, including the
 * generation of HBase table columns themselves.
 */
public class GenerateHQL {

  public static void main(String[] args) {
    try {
      Preconditions.checkState(1 == args.length, "Output path for HQL files is required");

      File outDir = new File(args[0]);
      Preconditions.checkState(outDir.exists() && outDir.isDirectory(), "Output directory must exist");

      Configuration cfg = new Configuration();
      cfg.setTemplateLoader(new ClassTemplateLoader(GenerateHQL.class, "/hql-templates"));
      generateHBaseTableHQL(cfg, outDir);
      generateOccurrenceTableHQL(cfg, outDir);
      generateDownloadTablesHQL(cfg, outDir);

    } catch (Exception e) {
      // Hard exit for safety, and since this is used in build pipelines, any generation error could have
      // catastophic effects - e.g. partially complete scripts being run, and resulting in inconsistent
      // data.
      System.err.println("*** Aborting JVM ***");
      System.err.println("Unexpected error building the templated HQL files.  "
                         + "Exiting JVM as a precaution, after dumping technical details.");
      e.printStackTrace();
      System.exit(-1);
    }

  }

  /**
   * Generates HQL which can be used to create the actual tables queried at download time.  The downloads tables are
   * simplified versions of the occurrence HBase table snapshot, and thus optimized for runtime performance where less
   * data is scanned at each download.
   */
  private static void generateDownloadTablesHQL(Configuration cfg, File outDir) throws IOException, TemplateException {
    try (FileWriter out = new FileWriter(new File(outDir, "create-download-tables.q"))) {
      Template template = cfg.getTemplate("create-download-tables.ftl");
      Map<String, Object> data = ImmutableMap.<String, Object>of(
        "occurrence_hdfs", OccurrenceHDFSTableDefinition.definition()
      );

      template.process(data, out);
    }
  }

  /**
   * Generates HQL which create a Hive table on the HBase table.
   */
  private static void generateHBaseTableHQL(Configuration cfg, File outDir)
    throws IOException, TemplateException {
    try (FileWriter out = new FileWriter(new File(outDir, "create-occurrence-hbase.q"))) {
      Template template = cfg.getTemplate("create-occurrence-hbase.ftl");
      Map<String, Object> data = ImmutableMap.<String, Object>of(
        "occurrence_hbase", OccurrenceHBaseTableDefinition.definition()
      );
      template.process(data, out);
    }
  }

  /**
   * Generates HQL which is used to take snapshots of the HBase table, and creates an HDFS equivalent.
   */
  private static void generateOccurrenceTableHQL(Configuration cfg, File outDir)
    throws IOException, TemplateException {

    try (FileWriter out = new FileWriter(new File(outDir, "create-occurrence-hdfs.q"))) {
      Template template = cfg.getTemplate("create-occurrence-hdfs.ftl");
      Map<String, Object> data = ImmutableMap.<String, Object>of(
        "occurrence_hdfs", OccurrenceHDFSTableDefinition.definition()
      );
      template.process(data, out);
    }
  }
}
