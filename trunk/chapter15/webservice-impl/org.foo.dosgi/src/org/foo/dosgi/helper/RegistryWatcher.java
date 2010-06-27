package org.foo.dosgi.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.felix.sigil.common.osgi.LDAPExpr;
import org.apache.felix.sigil.common.osgi.LDAPParser;
import org.foo.dosgi.registry.Registry;
import org.foo.dosgi.registry.RegistryEvent;
import org.foo.dosgi.registry.RegistryListener;
import org.foo.dosgi.registry.RegistryServiceReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class RegistryWatcher implements RegistryListener {

  static class Watch {
    final String clazz;
    final String filter;
    private final int hash;
    private LDAPExpr ldap;

    Watch(String clazz, String filter) {
      this.clazz = clazz;
      this.filter = filter;
      int h = 2683;
      if (clazz != null) {
        h ^= clazz.hashCode();
      }
      if (filter != null) {
        h ^= filter.hashCode();
      }
      hash = h;
    }

    public boolean equals(Object o) {
      if (o == this)
        return true;
      if (o == null)
        return false;
      try {
        Watch w = (Watch) o;
        return clazz == null ? w.clazz == null : clazz.equals(w.clazz)
            && filter == null ? w.filter == null : filter.equals(w.filter);
      } catch (ClassCastException e) {
        return false;
      }
    }

    public int hashCode() {
      return hash;
    }

    public boolean matches(RegistryServiceReference ref) {
      if (ldap == null) {
        ldap = LDAPParser.parseExpression(filter);
      }
      return clazz == null || clazz.equals(ref.getInterface())
          && ldap.eval(ref.getProperties());
    }
  }

  class Registration implements Callable<ServiceRegistration> {
    private final RegistryServiceReference ref;

    public Registration(RegistryServiceReference ref) {
      this.ref = ref;
    }

    public ServiceRegistration call() throws Exception {
      Hashtable props = new Hashtable(ref.getProperties());
      return ctx.registerService(ref.getInterface(), new ServiceFactory() {
        public Object getService(Bundle bundle, ServiceRegistration reg) {
          return ref.getService();
        }
        
        public void ungetService(Bundle bundle, ServiceRegistration reg,
            Object service) {
        }        
      },props);
    }
  }

  static class Unregister implements Callable<Void> {
    private final Future<ServiceRegistration> future;

    public Unregister(Future<ServiceRegistration> future) {
      this.future = future;
    }

    public Void call() throws Exception {
      future.get().unregister();
      return null;
    }
  }

  private final BundleContext ctx;
  private final Registry registry;
  private final ExecutorService exec;

  private Map<RegistryServiceReference, Future<ServiceRegistration>> regs = new HashMap<RegistryServiceReference, Future<ServiceRegistration>>();

  private HashMap<Watch, Integer> watches = new HashMap<Watch, Integer>();

  public RegistryWatcher(BundleContext ctx, Registry registry) {
    this.ctx = ctx;
    this.registry = registry;
    exec = Executors.newSingleThreadExecutor();
    registry.addListener(this);
  }

  public void destroy() {
    exec.shutdownNow();
    registry.removeListener(this);
  }

  public void addWatch(String clazz, String filter) {
    Watch watch = new Watch(clazz, filter);
    synchronized (watches) {
      Integer count = watches.get(watch);
      if (count == null) {
        LogUtil.info("Adding watch " + clazz + " -> " + filter);
        Collection<RegistryServiceReference> services = registry
            .findServices(clazz, filter);
        for (RegistryServiceReference ref : services) {
          if (!regs.containsKey(ref)) {
            Future<ServiceRegistration> future = exec
                .submit(new Registration(ref));
            regs.put(ref, future);
          }
        }
      } else {
        watches.put( watch, count + 1 );
      }
    }
  }

  public void findServices(String clazz, String filter) {
    synchronized (watches) {
      if (!watches.containsKey(new Watch(clazz, filter))) {
        LogUtil.info("Finding services " + clazz + " -> " + filter);

        Collection<RegistryServiceReference> services = registry
            .findServices(clazz, filter);
        for (RegistryServiceReference ref : services) {
          if (!regs.containsKey(ref)) {
            Future<ServiceRegistration> future = exec
                .submit(new Registration(ref));
            regs.put(ref, future);
          }
        }
      }
    }
  }

  public void removeWatch(String clazz, String filter) {
    Watch removed = new Watch(clazz, filter);

    synchronized (watches) {
      Integer count = watches.get(removed);
      count = count - 1;
      if ( count == 0 ) {
        watches.remove(removed);
        LogUtil.info("Removing watch " + clazz + " -> " + filter);
        for (Iterator<RegistryServiceReference> iter = regs.keySet()
            .iterator(); iter.hasNext();) {
          RegistryServiceReference ref = iter.next();
          if (removed.matches(ref)) {
            boolean found = false;
            for (Watch w : watches.keySet()) {
              if (w.matches(ref)) {
                found = true;
                break;
              }
            }

            if (!found) {
              Future<ServiceRegistration> f = regs.get(ref);
              exec.submit(new Unregister(f));
              iter.remove();
            }
          }
        }
      }
      else {
        watches.put(removed, count);
      }
    }
  }

  public void handleEvent(RegistryEvent event) {
    RegistryServiceReference ref = event.getReference();

    synchronized (watches) {
      switch (event.getType()) {
        case ADDED:
          handleAdd(ref);
          break;
        case REMOVED:
          handleRemove(ref);
          break;
      }
    }
  }

  private void handleAdd(RegistryServiceReference ref) {
    if (!regs.containsKey(ref)) {
      for (Watch w : watches.keySet()) {
        if (w.matches(ref)) {
          Future<ServiceRegistration> future = exec
              .submit(new Registration(ref));
          regs.put(ref, future);
          break;
        }
      }
    }
  }

  private void handleRemove(RegistryServiceReference ref) {
    Future<ServiceRegistration> future = regs.remove(ref);
    if (future != null) {
      exec.submit(new Unregister(future));
    }
  }
}
