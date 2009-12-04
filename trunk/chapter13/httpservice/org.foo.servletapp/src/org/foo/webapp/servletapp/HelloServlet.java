package org.foo.webapp.servletapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String name = req.getParameter( "name" );
    if ( name == null ) {
      name = "World";
    }
    PrintWriter writer = resp.getWriter();
    writer.println( "<html>" );
    writer.println( "<body>" );
    writer.println( "<p>" );
    writer.println( "Hello " + name );
    writer.println( "</p>" );
    writer.println( "</body>" );
    writer.println( "</html>" );
  }

}
