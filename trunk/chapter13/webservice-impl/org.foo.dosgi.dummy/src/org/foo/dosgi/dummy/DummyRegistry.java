package org.foo.dosgi.dummy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import org.foo.dosgi.registry.Registry;
import org.foo.dosgi.registry.RegistryEvent;
import org.foo.dosgi.registry.RegistryListener;
import org.foo.dosgi.registry.RegistryServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class DummyRegistry implements Registry {
  private HashSet<RegistryListener> listeners = new HashSet<RegistryListener>();
  private HashMap<ServiceReference, RegistryServiceReference> references = new HashMap<ServiceReference, RegistryServiceReference>();
  private final BundleContext ctx;

  public DummyRegistry(BundleContext ctx) {
    this.ctx = ctx;
  }
  
  public void addListener(RegistryListener listener) {
    listeners.add(listener);
  }

  public void removeListener(RegistryListener listener) {
    listeners.remove(listener);
  }

  public void registerService(ServiceReference ref, String iface, Object svc) {
    RegistryServiceReferenceImpl reg = new RegistryServiceReferenceImpl(
        ref, iface, svc);
    references.put(ref, reg);
    notify(new RegistryEvent(this, reg, RegistryEvent.Type.ADDED));
  }

  public void unregisterService(ServiceReference ref) {
    RegistryServiceReference reg = references.remove(ref);
    notify(new RegistryEvent(this, reg, RegistryEvent.Type.REMOVED));
  }

  public Collection<RegistryServiceReference> findServices(String clazz, String filter) {
    ArrayList<RegistryServiceReference> refs = new ArrayList<RegistryServiceReference>();
    try {
      Filter f = filter == null ? null : ctx.createFilter(filter);
      
      for ( RegistryServiceReference ref : references.values() ) {
        if ( clazz == null || clazz.equals( ref.getInterface() ) ) {
          if ( f == null || f.match( new Hashtable(ref.getProperties()) ) ) {
            refs.add( ref );
          }
        }
      }
    } catch (InvalidSyntaxException e) {
      throw new IllegalStateException(e);
    }
    return refs;
  }

  private void notify(RegistryEvent event) {
    for (RegistryListener listener : listeners) {
      listener.handleEvent(event);
    }
  }
}
