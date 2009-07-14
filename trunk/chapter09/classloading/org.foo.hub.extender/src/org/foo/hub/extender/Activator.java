package org.foo.hub.extender;

import java.lang.reflect.Constructor;
import java.util.*;
import org.foo.hub.Message;
import org.foo.hub.api.Hub;
import org.foo.hub.spi.Spoke;
import org.osgi.framework.*;

public class Activator implements BundleActivator, Hub {

  BundleTracker spokeTracker;

  Map<Bundle, Spoke> spokes = new HashMap<Bundle, Spoke>();

  public void start(final BundleContext ctx) {
    spokeTracker = new BundleTracker(ctx) {

      public void addedBundle(Bundle bundle) {

        Dictionary headers = bundle.getHeaders();
        String clazzName = (String) headers.get("Spoke-Class");
        String spokeName = (String) headers.get("Spoke-Name");

        if (clazzName != null && spokeName != null) {
          try {
            System.out.println("START ADDING SPOKE " + spokeName);

            Constructor ctor = bundle.loadClass(clazzName).getConstructor(String.class);
            spokes.put(bundle, (Spoke) ctor.newInstance(spokeName));

          } catch (Throwable e) {
            e.printStackTrace();
          } finally {
            System.out.println("DONE ADDING SPOKE " + spokeName);
          }
        }
      }

      public void removedBundle(Bundle bundle) {
        spokes.remove(bundle);
      }
    };

    spokeTracker.open();

    ctx.registerService(Hub.class.getName(), this, null);
  }

  public int send(Message message) {
    int count = 0;
    for (Spoke s : spokes.values()) {
      if (s.receive(message)) {
        count++;
      }
    }
    return count;
  }

  public void stop(BundleContext ctx) {
    spokeTracker.close();
    spokeTracker = null;
    spokes.clear();
  }
}
