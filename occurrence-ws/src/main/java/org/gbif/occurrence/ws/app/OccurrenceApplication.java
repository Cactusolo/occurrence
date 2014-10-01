package org.gbif.occurrence.ws.app;

import org.gbif.checklistbank.ws.client.guice.ChecklistBankWsClientModule;
import org.gbif.dropwizard.app.GbifBaseApplication;
import org.gbif.dropwizard.conf.PropertyKeyUtils;
import org.gbif.drupal.guice.DrupalMyBatisModule;
import org.gbif.occurrence.download.service.OccurrenceDownloadServiceModule;
import org.gbif.occurrence.persistence.guice.OccurrencePersistenceModule;
import org.gbif.occurrence.search.guice.OccurrenceSearchModule;
import org.gbif.occurrence.ws.app.conf.OccurrenceWsConfiguration;
import org.gbif.occurrence.ws.resources.DownloadResource;
import org.gbif.occurrence.ws.resources.FeaturedOccurrenceReader;
import org.gbif.occurrence.ws.resources.OccurrenceResource;
import org.gbif.occurrence.ws.resources.OccurrenceSearchResource;
import org.gbif.registry.ws.client.guice.RegistryWsClientModule;
import org.gbif.service.guice.PrivateServiceModule;
import org.gbif.ws.client.guice.SingleUserAuthModule;
import org.gbif.ws.server.guice.WsAuthModule;
import org.gbif.ws.server.interceptor.NullToNotFound;
import org.gbif.ws.server.interceptor.NullToNotFoundInterceptor;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import io.dropwizard.setup.Bootstrap;
import org.apache.bval.guice.ValidationModule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;


public class OccurrenceApplication extends GbifBaseApplication<OccurrenceWsConfiguration> {

  private static final String DOWNLOAD_USER_KEY = "occurrence.download.ws.username";
  private static final String DOWNLOAD_PASSWORD_KEY = "occurrence.download.ws.password";

  public OccurrenceApplication() {
    super(true);
  }

  @Override
  public Injector buildGuiceInjector(OccurrenceWsConfiguration configuration) {
    Properties properties = PropertyKeyUtils.toProperties(configuration);
    return Guice.createInjector(new SingleUserAuthModule(properties.getProperty(DOWNLOAD_USER_KEY),
      properties.getProperty(DOWNLOAD_PASSWORD_KEY)),
      new RegistryWsClientModule(properties),
      new ChecklistBankWsClientModule(properties, false, true, true),
      // others
      new WsAuthModule(properties),
      new ValidationModule(),
      new DrupalMyBatisModule(properties),
      new OccurrencePersistenceModule(properties),
      new OccurrenceSearchModule(properties),
      new OccurrenceDownloadServiceModule(properties),
      new OccurrenceWsModule(properties));
  }


  @Override
  public void initialize(Bootstrap<OccurrenceWsConfiguration> bootstrap) {
    // nothing to do
  }

  /**
   * Runs the application (required by Dropwizard).
   */
  public static void main(String[] args) throws Exception {
    new OccurrenceApplication().run(args);
  }


  @Override
  protected Class<?>[] getResourceClasses() {
    return new Class<?>[] {DownloadResource.class, OccurrenceResource.class, OccurrenceSearchResource.class};
  }
}
