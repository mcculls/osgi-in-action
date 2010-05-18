package org.foo.dosgi.registry;

import java.util.Collection;

import org.osgi.framework.ServiceReference;

public interface Registry {
  void registerService(ServiceReference ref, String iface, Object svc);

  void unregisterService(ServiceReference ref);

  void addListener(RegistryListener listener);

  void removeListener(RegistryListener listener);

  Collection<RegistryServiceReference> findServices(String clazz,
      String filter);
}
