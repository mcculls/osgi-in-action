package org.foo.leaky;

import org.osgi.framework.*;

public class Activator implements BundleActivator {

  static class Data {
    StringBuffer data = new StringBuffer(2 * 1024 * 1024);
  }

  static ThreadLocal leak = new ThreadLocal() {
    protected Object initialValue() {
      return new Data();
    };
  };

  public void start(BundleContext ctx) {
    leak.get();
  }

  public void stop(BundleContext ctx) {
    // leak.remove();
  }
}
