package org.foo.managed.factory;

import java.io.IOException;
import java.util.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class ManagedServiceFactoryExample implements ManagedServiceFactory {
	private final Map<String, EchoServer> m_servers = new HashMap<String, EchoServer>();
	
	public synchronized void deleted(String pid) {
		EchoServer server = m_servers.remove(pid);
		if (server != null) {
			server.stop();
		}
	}

	public String getName() {
		return getClass().getName();
	}

	public synchronized void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		System.out.println(pid + properties);
		EchoServer server = m_servers.remove(pid);
		if (server != null) {
			server.stop();
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
				server = new EchoServer(port);
				server.start();
				m_servers.put(pid, server);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	synchronized void stop() {
		System.out.println(m_servers);
		for (EchoServer server : m_servers.values()) {
			server.stop();
		}
		m_servers.clear();
	}

}
