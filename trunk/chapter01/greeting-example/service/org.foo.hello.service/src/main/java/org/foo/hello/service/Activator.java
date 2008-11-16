package org.foo.hello.service;

import org.foo.hello.Greeting;
import org.osgi.framework.*;

public class Activator implements BundleActivator {
  ServiceRegistration registration;

  public void start(BundleContext bc) {
    registration = bc.registerService(Greeting.class.getName(), new GreetingImpl(), null);
  }

  public void stop(BundleContext bc) {
    registration.unregister();
    registration = null;
  }
}
