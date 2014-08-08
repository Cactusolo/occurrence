package org.gbif.occurrence.ws.app.conf;

import org.gbif.dropwizard.conf.PropertiesKey;

import com.fasterxml.jackson.annotation.JsonProperty;


public class OccurrenceDbConfiguration {

  @PropertiesKey("table_name")
  private String tableName;// =${occurrence.env_prefix}_occurrence

  @PropertiesKey("featured_table_name")
  private String featuredTableName; // ${occurrence.env_prefix}_featured_occurrence

  @PropertiesKey("counter_table_name")
  private String counterTableName; // ${occurrence.env_prefix}_occurrence_counter

  @PropertiesKey("id_lookup_table_name")
  private String idLookupTableName; // ${occurrence.env_prefix}_occurrence_lookup

  @PropertiesKey("max_connection_pool")
  private String maxConnectionPool; // ${hbase.maxConnectionPool}

  @PropertiesKey("zookeeper.connection_string")
  private String zookeeperConnectionString; // ${zookeeper.quorum}


  @JsonProperty
  public String getTableName() {
    return tableName;
  }


  public void setTableName(String tableName) {
    this.tableName = tableName;
  }


  @JsonProperty
  public String getFeaturedTableName() {
    return featuredTableName;
  }


  public void setFeaturedTableName(String featuredTableName) {
    this.featuredTableName = featuredTableName;
  }


  @JsonProperty
  public String getCounterTableName() {
    return counterTableName;
  }


  public void setCounterTableName(String counterTableName) {
    this.counterTableName = counterTableName;
  }


  @JsonProperty
  public String getIdLookupTableName() {
    return idLookupTableName;
  }


  public void setIdLookupTableName(String idLookupTableName) {
    this.idLookupTableName = idLookupTableName;
  }


  @JsonProperty
  public String getMaxConnectionPool() {
    return maxConnectionPool;
  }


  public void setMaxConnectionPool(String maxConnectionPool) {
    this.maxConnectionPool = maxConnectionPool;
  }


  @JsonProperty
  public String getZookeeperConnectionString() {
    return zookeeperConnectionString;
  }


  public void setZookeeperConnectionString(String zookeeperConnectionString) {
    this.zookeeperConnectionString = zookeeperConnectionString;
  }


}
