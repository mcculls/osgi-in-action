package org.foo.hello.lifecycle;

import org.foo.hello.Greeting;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
  Greeting greeting;

  public void start(BundleContext bc) {
    greeting = new Greeting();
    greeting.arrive(bc.getBundle());
  }

  public void stop(BundleContext bc) {
    greeting.depart(bc.getBundle());
    greeting = null;
  }
}
