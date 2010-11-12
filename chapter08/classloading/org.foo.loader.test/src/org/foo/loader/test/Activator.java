package org.foo.loader.test;

import org.example.api.Base;
import org.foo.loader.SimpleLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  ServiceTracker loaderTracker;

  public void start(final BundleContext ctx) {
    loaderTracker = new ServiceTracker(ctx, SimpleLoader.class.getName(), null) {
      public Object addingService(ServiceReference reference) {
        SimpleLoader loader = (SimpleLoader) ctx.getService(reference);

        try {
          System.out.println("START LOAD REQUEST");
          ((Base) loader.loadClass("org.example.Target").newInstance()).test();
        } catch (Throwable e) {
          e.printStackTrace();
        } finally {
          System.out.println("END LOAD REQUEST");
        }

        return null;
      }
    };
    loaderTracker.open();
  }

  public void stop(BundleContext ctx) {
    loaderTracker.close();
    loaderTracker = null;
  }
}
