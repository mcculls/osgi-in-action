package org.foo.http.tracker;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class ProxyHttpContext implements HttpContext {

  private final Bundle bundle;

  public ProxyHttpContext(Bundle bundle) {
    this.bundle = bundle;
  }
  
  public URL getResource(String name) {
    return bundle.getEntry(name);
  }

  public String getMimeType(String name) {
    return null;
  }

  public boolean handleSecurity(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    return true;
  }

}
