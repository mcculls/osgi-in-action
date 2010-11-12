package org.foo.webapp.servletapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component(immediate = true)
@Provides(specifications = Servlet.class)
public class HelloServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  @ServiceProperty(name = "Web-ContextPath")
  String ctx = "/hello";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String name = req.getParameter("name");
    if (name == null) {
      name = "World";
    }
    PrintWriter writer = resp.getWriter();
    writer.println("<html>");
    writer.println("<body>");
    writer.println("<p>");
    writer.println("Hello " + name);
    writer.println("</p>");
    writer.println("</body>");
    writer.println("</html>");
  }

}
