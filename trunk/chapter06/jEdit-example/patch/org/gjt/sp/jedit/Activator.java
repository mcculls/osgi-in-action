package org.gjt.sp.jedit;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;

import org.gjt.sp.jedit.proto.jeditresource.Handler;

public class Activator implements BundleActivator
{
  private static class JEditResourceHandlerService
    extends AbstractURLStreamHandlerService
  {
    private Handler jEditResourceHandler;

    public synchronized URLConnection openConnection(URL url)
      throws IOException
    {
      if (null == jEditResourceHandler) {
        jEditResourceHandler = new Handler();
      }
      return jEditResourceHandler.openConnection(url);
    }
  }

  public void start(BundleContext context)
  {
    Properties properties = new Properties();
    properties.setProperty(URLConstants.URL_HANDLER_PROTOCOL, "jeditresource");

    context.registerService(
      URLStreamHandlerService.class.getName(),
      new JEditResourceHandlerService(),
      properties);
  }

  public void stop(BundleContext context) {}
}
