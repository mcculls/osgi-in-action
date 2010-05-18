package org.foo.hub.test;

import org.foo.hub.api.Hub;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Test bundle that sends a message whenever the hub comes online.
 */
public class Activator implements BundleActivator {

  ServiceTracker hubTracker;

  public void start(final BundleContext ctx) {
    hubTracker = new ServiceTracker(ctx, Hub.class.getName(), null) {

      /*
       * Uncomment A, B ,and C to use the bundle ClassLoader as the Thread Context ClassLoader.
       */
      public Object addingService(ServiceReference reference) {

        //A// ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();

        try {

          //B// Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

          Hub hub = (Hub) ctx.getService(reference);
          hub.send(new TextMessage(".*", "Testing Testing 1, 2, 3..."));

        } catch (Throwable e) {
          e.printStackTrace();
        } finally {

          //C// Thread.currentThread().setContextClassLoader(oldTCCL);

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
