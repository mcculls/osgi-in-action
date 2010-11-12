package org.foo.httpservice.resourceapp;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

@Component
public class ResourceBinder {
  @Requires(optional=true)
	private LogService s_log;
  
  @Requires(id="http")
	private HttpService s_http;
  
  @Bind(id="http")
	protected void addHttpService(HttpService service) {
	  register(service);
	}
	
  @Unbind(id="http")
  protected void removeHttpService(HttpService service) {
    unregister(service);
	}
	
  @Validate
  protected void start() {
	  register(s_http);
	}
	
  @Invalidate
	protected void stop() {
    unregister(s_http);
	}

  private void register(HttpService service) {
    try {
      service.registerResources( "/", "/html", null );
      service.registerResources( "/images", "/images", null );
    } catch (NamespaceException e) {
      s_log.log(LogService.LOG_WARNING, "Failed to register static content", e);
    }
  }

  private void unregister(HttpService service) {
    service.unregister("/");
    service.unregister("/images");
  }
}
