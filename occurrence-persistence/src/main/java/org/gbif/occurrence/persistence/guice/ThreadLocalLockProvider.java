package org.gbif.occurrence.persistence.guice;

import org.gbif.occurrence.persistence.zookeeper.ZookeeperLockManager;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.curator.framework.CuratorFramework;

/**
 * A provider that will issue a new instance per thread.
 * Importantly, this is a singleton or Guice will create one factory per thread.
 */
@Singleton
public class ThreadLocalLockProvider implements Provider<ZookeeperLockManager> {

  private final ThreadLocal<ZookeeperLockManager> threadLocalManager;

  public ThreadLocalLockProvider(final CuratorFramework curator) {
    threadLocalManager = new ThreadLocal<ZookeeperLockManager>() {
      @Override
      protected ZookeeperLockManager initialValue() {
        return new ZookeeperLockManager(curator);
      }
    };
  }

  @Override
  public ZookeeperLockManager get() {
    return threadLocalManager.get();
  }
}
