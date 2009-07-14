package org.foo.managed.service;

import java.util.Properties;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;

public class Activator implements BundleActivator {
	private ManagedServiceExample m_service;
	private ServiceRegistration m_reg;
	public void start(BundleContext context) {
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, "org.foo.managed.service");
		m_reg = context.registerService(ManagedService.class.getName(), m_service = new ManagedServiceExample(), props);
	}
    public void stop(BundleContext context) {
    	m_reg.unregister();
    	m_service.stop();
    }
}
