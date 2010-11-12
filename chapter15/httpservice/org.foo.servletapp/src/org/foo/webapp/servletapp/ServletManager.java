package org.foo.webapp.servletapp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

@Component(immediate=true)
public class ServletManager {
  
  @Requires(optional=true)
  private LogService log;
  
  private LinkedList<HttpService> services = new LinkedList<HttpService>();  
  private Map<String, Servlet> servlets = new HashMap<String, Servlet>();
  
  @Bind(aggregate=true)
  void bindHttp(HttpService http) {
    Map<String, Servlet> snapshot;
    
    synchronized( servlets ) {
      snapshot = new HashMap<String, Servlet>(servlets);
      services.add( http );
    }
    
    for ( Map.Entry<String, Servlet> entry : snapshot.entrySet() ) {
      String ctx = entry.getKey();
      Servlet s = entry.getValue();
      try {
        http.registerServlet(ctx, s, null, null);
      } catch (ServletException e) {
        //log.log(LogService.LOG_WARNING, "Failed to registerServlet", e);
      } catch (NamespaceException e) {
        //log.log(LogService.LOG_WARNING, "Failed to registerServlet", e);
      }
    }
  }
  
  @Unbind
  void unbindHttp(HttpService http) {
    Map<String, Servlet> snapshot;
    
    synchronized( servlets ) {
      snapshot = new HashMap<String, Servlet>(servlets);
      services.remove(http);
    }
    
    for ( String ctx : snapshot.keySet() ) {
      http.unregister(ctx);
    }
  }
  
  @Bind(aggregate=true)
  void bindServlet(Servlet servlet, Map attrs) {
    String ctx = (String) attrs.get("Web-ContextPath");
    if ( ctx != null ) {
      LinkedList<HttpService> snapshot;
      
      synchronized( servlets ) {
        servlets.put(ctx, servlet);
        snapshot = new LinkedList<HttpService>(services);
      }
      
      for ( HttpService s : snapshot ) {
        try {
          s.registerServlet(ctx, servlet, null, null);
        } catch (ServletException e) {
          //log.log(LogService.LOG_WARNING, "Failed to registerServlet", e);
        } catch (NamespaceException e) {
          //log.log(LogService.LOG_WARNING, "Failed to registerServlet", e);
        }
      }
    }
  }
  
  @Unbind
  void unbindServlet(Servlet servlet, Map attrs) {
    String ctx = (String) attrs.get("Web-ContextPath");
    if ( ctx != null ) {
      LinkedList<HttpService> snapshot;
      synchronized( servlets ) {
        servlets.remove(ctx);
        snapshot = new LinkedList<HttpService>(services);
      }
      
      for ( HttpService s : snapshot ) {
        s.unregister(ctx);
      }
    }
  }
}
