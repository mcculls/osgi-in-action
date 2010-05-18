package org.foo.jedit.extender;

import java.io.File;
import org.gjt.sp.jedit.*;
import org.osgi.framework.*;

public class Activator implements BundleActivator {

  BundleTracker pluginTracker;

  public void start(final BundleContext ctx) {
    pluginTracker = new BundleTracker(ctx) {

      public void addedBundle(Bundle bundle) {
        String path = getBundlePath(bundle);
        if (path != null && bundle.getResource("actions.xml") != null) {
          jEdit.addPluginJAR(path);
        }
      }

      public void removedBundle(Bundle bundle) {
        String path = getBundlePath(bundle);
        if (path != null) {
          PluginJAR jar = jEdit.getPluginJAR(path);
          if (jar != null) {
            jEdit.removePluginJAR(jar, false);
          }
        }
      }
    };

    EditBus.addToBus(new EBComponent() {
      public void handleMessage(EBMessage message) {
        EditBus.removeFromBus(this);
        pluginTracker.open();
      }
    });
  }

  public void stop(BundleContext ctx) {
    pluginTracker.close();
    pluginTracker = null;
  }

  static String getBundlePath(Bundle bundle) {
    String location = bundle.getLocation().trim();

    File jar;
    if (location.startsWith("file:")) {
      jar = new File(location.substring(5));
    } else {
      jar = new File(location);
    }

    if (jar.isFile()) {
      return jar.getAbsolutePath();
    }

    return null;
  }
}
