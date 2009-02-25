package org.foo.hello;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  public void start(BundleContext ctx) {
    Greeting.instance = new Greeting("lifecycle");
  }

  public void stop(BundleContext ctx) {
    Greeting.instance = null;
  }
}
