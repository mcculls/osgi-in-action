package org.gjt.sp.jedit;

import java.io.IOException;
import java.net.*;
import java.util.Properties;

import org.osgi.framework.*;
import org.osgi.service.url.*;

import org.gjt.sp.jedit.proto.jeditresource.Handler;

public class Activator implements BundleActivator {
  private static class JEditResourceHandlerService
    extends AbstractURLStreamHandlerService {
    private Handler jEditResourceHandler = new Handler();

    public URLConnection openConnection(URL url)
      throws IOException {
      return jEditResourceHandler.openConnection(url);
    }
  }

  public void start(BundleContext context) {
    Properties properties = new Properties();
    properties.setProperty(URLConstants.URL_HANDLER_PROTOCOL,
        "jeditresource");

    context.registerService(
      URLStreamHandlerService.class.getName(),
      new JEditResourceHandlerService(),
      properties);
  }

  public void stop(BundleContext context) {}
}
