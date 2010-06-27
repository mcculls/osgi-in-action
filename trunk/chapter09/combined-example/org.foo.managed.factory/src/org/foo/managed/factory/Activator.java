package org.foo.managed.factory;

import java.util.Properties;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedServiceFactory;

public class Activator implements BundleActivator {
	private volatile ServiceRegistration m_reg;
	private volatile ManagedServiceFactoryExample m_factory;
	public void start(BundleContext context) {
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, "org.foo.managed.factory");
		m_reg = context.registerService(ManagedServiceFactory.class.getName(),
			m_factory = new ManagedServiceFactoryExample(), props);
	}
    public void stop(BundleContext context) {
    	m_reg.unregister();
    	m_factory.stop();
    }
}
