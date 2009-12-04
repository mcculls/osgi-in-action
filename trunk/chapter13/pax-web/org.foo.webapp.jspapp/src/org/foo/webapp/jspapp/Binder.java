package org.foo.webapp.jspapp;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.http.HttpContext;

public class Binder {
  private volatile HttpContext http;

  protected void bindWebContainer(WebContainer c) {
    http = c.createDefaultHttpContext();
    c.registerJsps(null, http);
  }

  protected void unbindWebContainer(WebContainer c) {
    c.unregisterJsps(http);
    http = null;
  }
}
