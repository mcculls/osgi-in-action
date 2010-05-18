package org.apache.commons.httpclient.internal;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.osgi.framework.*;

public class Activator implements BundleActivator {
  public void start(BundleContext ctx) {}

  public void stop(BundleContext ctx) {
    MultiThreadedHttpConnectionManager.shutdownAll();
  }
}
