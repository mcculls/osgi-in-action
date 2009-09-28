package org.foo.webapp.servletapp;

import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  private volatile ServletManager manager;

  public void start(BundleContext ctx) throws Exception {
    manager = new ServletManager(ctx);
    
    Hashtable attrs = new Hashtable();
    attrs.put( "web-contextpath", "/hello" );
    
    ctx.registerService( Servlet.class.getName(), new HelloServlet(), attrs );
  }

  public void stop(BundleContext ctx) throws Exception {
    manager.dispose();
  }

}
