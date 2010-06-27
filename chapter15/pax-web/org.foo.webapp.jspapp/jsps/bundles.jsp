<%@ page import="java.util.*, org.osgi.framework.*" %>
<HTML>
 <HEAD>
  <TITLE>Bundles</TITLE>
 </HEAD>
 <BODY>
  <p>Today is: <%= new java.util.Date().toString() %></p>
  <h2>Bundles</h2>
  <% BundleContext ctx = (BundleContext) getServletContext().getAttribute( "osgi-bundlecontext" ); %>
  <ul>
  <% for ( Bundle b : ctx.getBundles() ) { %>
    <li><%= b.getSymbolicName() %></li>
  <%
  } %>
  </ul>
 </BODY>
</HTML>
