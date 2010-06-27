package org.foo.dosgi.dummy;

import org.foo.dosgi.registry.Registry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  public void start(BundleContext context) throws Exception {
    context.registerService(Registry.class.getName(), new DummyRegistry(context), null);
  }

  public void stop(BundleContext context) throws Exception {
  }

}
