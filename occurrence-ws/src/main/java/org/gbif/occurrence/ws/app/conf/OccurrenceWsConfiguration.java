package org.gbif.occurrence.ws.app.conf;

import org.gbif.dropwizard.conf.JdbcConfiguration;
import org.gbif.dropwizard.conf.PropertiesKey;
import org.gbif.dropwizard.conf.PropertyKeyUtils;
import org.gbif.dropwizard.conf.SolrConfiguration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.io.Resources;
import io.dropwizard.Configuration;


public class OccurrenceWsConfiguration extends Configuration {

  @PropertiesKey("registry.ws.url")
  private String registryWsUrl;

  @PropertiesKey("checklistbank.ws.url")
  private String checklistbankWsUrl;

  @PropertiesKey("checklistbank.match.ws.url")
  private String checklistbankMatchWsUrl;

  @PropertiesKey("drupal")
  @Valid
  private JdbcConfiguration drupal;

  @PropertiesKey("occurrence.search")
  @Valid
  private SolrConfiguration solr;

  // file with all application keys & secrets
  @PropertiesKey("appkeys.file")
  private String appkeysFile;

  @PropertiesKey("occurrence.db")
  @Valid
  private OccurrenceDbConfiguration db;

  @PropertiesKey("occurrence.download")
  @Valid
  private OccurrenceDownloadConfiguration download;

  @JsonProperty
  public String getRegistryWsUrl() {
    return registryWsUrl;
  }


  public void setRegistryWsUrl(String registryWsUrl) {
    this.registryWsUrl = registryWsUrl;
  }


  @JsonProperty
  public String getChecklistbankWsUrl() {
    return checklistbankWsUrl;
  }


  public void setChecklistbankWsUrl(String checklistbankWsUrl) {
    this.checklistbankWsUrl = checklistbankWsUrl;
  }


  @JsonProperty
  public String getChecklistbankMatchWsUrl() {
    return checklistbankMatchWsUrl;
  }


  public void setChecklistbankMatchWsUrl(String checklistbankMatchWsUrl) {
    this.checklistbankMatchWsUrl = checklistbankMatchWsUrl;
  }


  @JsonProperty
  public String getAppkeysFile() {
    return appkeysFile;
  }


  public void setAppkeysFile(String appkeysFile) {
    this.appkeysFile = appkeysFile;
  }

  public JdbcConfiguration getDrupal() {
    return drupal;
  }


  public void setDrupal(JdbcConfiguration drupal) {
    this.drupal = drupal;
  }


  public SolrConfiguration getSolr() {
    return solr;
  }


  @JsonProperty
  public void setSolr(SolrConfiguration solr) {
    this.solr = solr;
  }


  @JsonProperty
  public OccurrenceDbConfiguration getDb() {
    return db;
  }


  public void setDb(OccurrenceDbConfiguration db) {
    this.db = db;
  }


  @JsonProperty
  public OccurrenceDownloadConfiguration getDownload() {
    return download;
  }


  public void setDownload(OccurrenceDownloadConfiguration download) {
    this.download = download;
  }

  // TODO: java.lang.SecurityException: class "javax.servlet.Filter"'s signer information does not match signer
// information of other classes in the same package
  public static void main(String[] args) throws Exception {
    Yaml yaml = new Yaml();
    OccurrenceWsConfiguration occurrenceConfiguration =
      yaml.loadAs(Resources.getResource("occurrence.yaml").openStream(), OccurrenceWsConfiguration.class);
    System.out.println(PropertyKeyUtils.toProperties(occurrenceConfiguration));
  }
}
