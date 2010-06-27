package org.foo.dosgi.dummy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.foo.dosgi.registry.RegistryServiceReference;
import org.osgi.framework.ServiceReference;

public class RegistryServiceReferenceImpl implements
    RegistryServiceReference {

  private final String iface;
  private final Object svc;
  private final Map properties;

  public RegistryServiceReferenceImpl(ServiceReference ref, String iface, Object svc) {
    this.iface = iface;
    this.svc = svc;
    HashMap tmp = new HashMap();
    for ( String key : ref.getPropertyKeys() ) {
      if ( !key.equals( "service.exported.interfaces") ) {
        tmp.put(key, ref.getProperty(key));        
      }
    }
    tmp.put( "service.imported", true );
    properties = Collections.unmodifiableMap(tmp);
  }

  public String getInterface() {
    return iface;
  }

  public Map getProperties() {
    return properties;
  }

  public Object getService() {
    return svc;
  }

  public String toString() {
    return iface + ":" + properties + "->" + svc;
  }
}
