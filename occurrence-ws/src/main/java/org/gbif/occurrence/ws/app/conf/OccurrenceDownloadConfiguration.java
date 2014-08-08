package org.gbif.occurrence.ws.app.conf;

import org.gbif.dropwizard.conf.MailConfiguration;
import org.gbif.dropwizard.conf.PropertiesKey;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;


public class OccurrenceDownloadConfiguration {

  @PropertiesKey("ws.username")
  private String wsUsername;

  @PropertiesKey("ws.password")
  private String wsPassword;

  @PropertiesKey("ws.url")
  private String wsUrl;

  @PropertiesKey("datause.url")
  private String datauseUrl;

  @PropertiesKey("ws.mount")
  private String wsMount;

  @PropertiesKey("oozie.url")
  private String oozieUrl;

  @PropertiesKey("oozie.workflow.path")
  private String oozieWorkflowPath;

  @PropertiesKey("hive.hdfs.out")
  private String hiveHdfsOut;

  @PropertiesKey
  @Valid
  private MailConfiguration mail;


  @JsonProperty
  public String getWsUsername() {
    return wsUsername;
  }


  public void setWsUsername(String wsUsername) {
    this.wsUsername = wsUsername;
  }


  @JsonProperty
  public String getWsPassword() {
    return wsPassword;
  }


  public void setWsPassword(String wsPassword) {
    this.wsPassword = wsPassword;
  }


  @JsonProperty
  public String getWsUrl() {
    return wsUrl;
  }


  public void setWsUrl(String wsUrl) {
    this.wsUrl = wsUrl;
  }


  @JsonProperty
  public String getDatauseUrl() {
    return datauseUrl;
  }


  public void setDatauseUrl(String datauseUrl) {
    this.datauseUrl = datauseUrl;
  }


  @JsonProperty
  public String getWsMount() {
    return wsMount;
  }


  public void setWsMount(String wsMount) {
    this.wsMount = wsMount;
  }


  @JsonProperty
  public String getOozieUrl() {
    return oozieUrl;
  }


  public void setOozieUrl(String oozieUrl) {
    this.oozieUrl = oozieUrl;
  }


  @JsonProperty
  public String getOozieWorkflowPath() {
    return oozieWorkflowPath;
  }


  public void setOozieWorkflowPath(String oozieWorkflowPath) {
    this.oozieWorkflowPath = oozieWorkflowPath;
  }


  @JsonProperty
  public String getHiveHdfsOut() {
    return hiveHdfsOut;
  }


  public void setHiveHdfsOut(String hiveHdfsOut) {
    this.hiveHdfsOut = hiveHdfsOut;
  }


  @JsonProperty
  public MailConfiguration getMail() {
    return mail;
  }


  public void setMail(MailConfiguration mail) {
    this.mail = mail;
  }


}
