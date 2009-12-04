package org.foo.webapp.servletapp;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	private static final String LOGIN_CONTEXT = "/login";
	
	private String p_landingPage;
	private String p_loginPage;
	
	private LogService s_log;

	protected void addHttpService(HttpService service) {
		try {
			Dictionary props = new Hashtable();
			service.registerServlet(LOGIN_CONTEXT, this, props, null);
		} catch (NamespaceException e) {
			s_log.log(LogService.LOG_WARNING, "Failed to register login servlet", e);
		} catch (ServletException e) {
			s_log.log(LogService.LOG_WARNING, "Failed to register login servlet", e);
		}
	}
	
	protected void removeHttpService(HttpService service) {
		service.unregister(LOGIN_CONTEXT);
	}
	
	private String nextPage(HttpServletRequest req) {
		String next = req.getParameter("next");
		if ( next == null ) {
			next = p_landingPage;
		}
		return next;
	}
}
