package org.foo.http.tracker;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  private volatile HttpServiceTracker httpService;

  public void start(BundleContext ctx) throws Exception {
    httpService = new HttpServiceTracker(ctx);
    httpService.open();
  }

  public void stop(BundleContext ctx) throws Exception {
    httpService.close();
    httpService = null;
  }
}
