package org.foo.hello.client;

import org.foo.hello.Greeting;
import org.osgi.framework.*;

public class Client implements BundleActivator {

  public void start(BundleContext ctx) {
    ServiceReference ref =
        ctx.getServiceReference(Greeting.class.getName());

    ((Greeting) ctx.getService(ref)).sayHello();
  }

  public void stop(BundleContext ctx) {}
}
