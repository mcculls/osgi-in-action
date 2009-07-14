package org.foo.hub.test;

import org.foo.hub.api.Hub;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  ServiceTracker hubTracker;

  public void start(final BundleContext ctx) {
    hubTracker = new ServiceTracker(ctx, Hub.class.getName(), null) {

      public Object addingService(ServiceReference reference) {

        // ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        try {

          // Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

          Hub hub = (Hub) ctx.getService(reference);
          hub.send(new TextMessage(".*", "Testing Testing 1, 2, 3..."));

        } catch (Throwable e) {
          e.printStackTrace();
        } finally {

          // Thread.currentThread().setContextClassLoader(tccl);

        }

        return null;
      }
    };

    hubTracker.open();
  }

  public void stop(BundleContext ctx) {
    hubTracker.close();
    hubTracker = null;
  }
}
