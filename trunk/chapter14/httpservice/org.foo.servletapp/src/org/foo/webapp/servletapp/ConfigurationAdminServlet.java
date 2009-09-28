package org.foo.webapp.servletapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@SuppressWarnings("serial")
public class ConfigurationAdminServlet extends AbstractWebConsolePlugin {

  @Override
  public String getLabel() {
    return "Configuration Admin Title";
  }

  @Override
  public String getTitle() {
    return "Configuration Admin";
  }

  @Override
  protected void renderContent(HttpServletRequest req,
      HttpServletResponse resp) throws ServletException, IOException {
    BundleContext ctx = getBundleContext();
    ServiceReference ref = ctx.getServiceReference(ConfigurationAdmin.class
        .getName());
    if (ref != null) {
      ConfigurationAdmin admin = (ConfigurationAdmin) ctx.getService(ref);
      if (admin != null) {
        renderConfigs(req, resp, admin);
      } else {
        resp.getWriter().println("No config admin installed");
      }
    }
  }

  private void renderConfigs(HttpServletRequest req,
      HttpServletResponse resp, ConfigurationAdmin admin)
      throws IOException, ServletException {
    PrintWriter writer = resp.getWriter();
    try {
      Configuration[] configs = admin.listConfigurations(null);
      writer.println( "<table border=\"1\">" );
      row( writer, true, "service.pid", "factory.pid", "location", "properties" );
      if ( configs != null ) {
        for ( Configuration c : configs ) {
          String props = renderProps( c.getProperties() );
          row( writer, false, c.getPid(), c.getFactoryPid(), c.getBundleLocation(), props );
        }
      }
      else {
        writer.println( "<tr>" );
        writer.println( "<td colspan=\"4\">undefined</td>" );
        writer.println( "</tr>" );
      }
      writer.println( "</table>" );
    } catch (InvalidSyntaxException e) {
      throw new ServletException(e);
    }
  }

  private void row(PrintWriter writer, boolean header, String... cols) {
    writer.println( "<tr>"  );
    for ( String c : cols ) {
      writer.println( header ? "<th>" : "<td>" );
      writer.println(c);
      writer.println( header ? "</th>" : "</td>" );
    }
    writer.println( "</tr>" );
  }

  private String renderProps(Dictionary properties) {
    StringBuilder buf = new StringBuilder();
    for ( Enumeration e = properties.keys(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      String val = (String) properties.get(key);
      if ( buf.length() > 0 ) {
        buf.append( "<br />" );
      }
      buf.append( key );
      buf.append( "=" );
      buf.append( val );
    }
    return buf.toString();
  }
}
