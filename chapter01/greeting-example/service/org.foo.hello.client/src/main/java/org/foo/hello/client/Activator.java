package org.foo.hello.client;

import org.foo.hello.Greeting;
import org.osgi.framework.*;

public class Activator implements BundleActivator {
  ServiceReference handle;

  public void start(BundleContext bc) {
    handle = bc.getServiceReference(Greeting.class.getName());
    ((Greeting) bc.getService(handle)).arrive(bc.getBundle());
  }

  public void stop(BundleContext bc) {
    ((Greeting) bc.getService(handle)).depart(bc.getBundle());
    handle = null;
  }
}
