package org.foo.managed.service;

import java.io.IOException;
import java.util.Dictionary;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class ManagedServiceExample implements ManagedService {
	private EchoServer m_server = null;

	public synchronized void updated(Dictionary properties)
			throws ConfigurationException {
		if (m_server != null) {
			m_server.stop();
			m_server = null;
		}
		if (properties != null) {
			String portString = (String) properties.get("port");
			if (portString == null) {
				throw new ConfigurationException(null, "Property missing");
			}
			int port;
			try {
				port = Integer.parseInt(portString);
			} catch (NumberFormatException ex) {
				throw new ConfigurationException(null,
						"Not a valid port number");
			}
			try {
				m_server = new EchoServer(port);
				m_server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	synchronized void stop() {
		if (m_server != null) {
			m_server.stop();
			m_server = null;
		}
	}
}
