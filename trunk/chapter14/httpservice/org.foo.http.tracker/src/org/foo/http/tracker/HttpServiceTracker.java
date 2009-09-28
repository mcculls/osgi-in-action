/**
 * 
 */
package org.foo.http.tracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

class HttpServiceTracker extends ServiceTracker {
  private final BundleContext context;

  public HttpServiceTracker(BundleContext context) {
    super(context, HttpService.class.getName(), null);
    this.context = context;
  }

  @Override
  public Object addingService(ServiceReference reference) {
    HttpService http = (HttpService) super.addingService(reference);
    HttpResourceTracker tracker = new HttpResourceTracker(context, http);
    tracker.open();
    return tracker;
  }

  @Override
  public void removedService(ServiceReference reference, Object service) {
    HttpResourceTracker tracker = (HttpResourceTracker) service;
    tracker.close();
    super.removedService(reference, tracker.getHttp());
  }
}