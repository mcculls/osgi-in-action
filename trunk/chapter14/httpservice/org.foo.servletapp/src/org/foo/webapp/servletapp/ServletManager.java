package org.foo.webapp.servletapp;

import java.util.Dictionary;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public class ServletManager {
  private HttpTracker httpTracker;

  public ServletManager(final BundleContext ctx) {
    httpTracker = new HttpTracker(ctx);
    httpTracker.open();
  }
  
  public void dispose() {
    httpTracker.close();
    httpTracker = null;
  }
  
  protected HttpContext getHttpContext(HttpService http) {
    return null;
  }

  protected Dictionary getInitParams(HttpService http) {
    return null;
  }
  
  private static Filter buildServletFilter(BundleContext ctx) {
    String ldap = "(&(" + Constants.OBJECTCLASS + "=" + Servlet.class.getName() + ")(web-contextpath=*))";
    try {
      return ctx.createFilter(ldap);
    } catch (InvalidSyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  class HttpTracker extends ServiceTracker {
    private final BundleContext ctx;

    private Map<HttpService, ServletTracker> trackers = new IdentityHashMap<HttpService, ServletTracker>();
    
    public HttpTracker(BundleContext ctx) {
      super(ctx, HttpService.class.getName(), null);
      this.ctx = ctx;
    }
    
    public Object addingService(ServiceReference reference) {
      HttpService http = (HttpService)  super.addingService(reference);
      ServletTracker servletTracker = null;

      synchronized( trackers ) {
        if ( !trackers.containsKey(http) ) {
          servletTracker = new ServletTracker(ctx, http, getInitParams(http), getHttpContext(http));
          trackers.put( http, servletTracker );
        }
      }

      if ( servletTracker != null ) {
        servletTracker.open();
      }

      return http;
    }

    public void removedService(ServiceReference ref, Object service) {
      ServletTracker servletTracker = null;

      synchronized( trackers ) {
        servletTracker = trackers.remove(service);
      }

      if ( servletTracker != null ) {
        servletTracker.close();
      }
      super.removedService(ref, service);
    }      
  }

  static class ServletTracker extends ServiceTracker {
    private final Dictionary m_initParams;
    private final HttpContext m_httpContext;
    private final HttpService http;

    public ServletTracker(BundleContext ctx, HttpService http,
        Dictionary initParams, HttpContext httpContext) {
      super(ctx, buildServletFilter(ctx), null);
      this.http = http;
      m_initParams = initParams;
      m_httpContext = httpContext;
    }

    @Override
    public Object addingService(ServiceReference reference) {
      Servlet servlet = (Servlet) super.addingService(reference);
      String servletContext = (String) reference.getProperty("web-contextpath");
      try {
        http.registerServlet(servletContext, servlet, m_initParams, m_httpContext);
      } catch (ServletException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (NamespaceException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return servlet;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
      String servletContext = (String) reference.getProperty("web-contextpath");
      http.unregister(servletContext);
      super.removedService(reference, service);
    }    
  }
}
