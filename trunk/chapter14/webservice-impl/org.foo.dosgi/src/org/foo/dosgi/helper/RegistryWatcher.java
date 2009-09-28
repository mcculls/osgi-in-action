package org.foo.dosgi.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.felix.sigil.common.osgi.LDAPExpr;
import org.apache.felix.sigil.common.osgi.LDAPParser;
import org.foo.dosgi.registry.Registry;
import org.foo.dosgi.registry.RegistryEvent;
import org.foo.dosgi.registry.RegistryListener;
import org.foo.dosgi.registry.RegistryServiceReference;
import org.osgi.framework.BundleContext;
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
      return ctx.registerService(ref.getInterface(), ref.getService(),
          props);
    }
  }

  private final BundleContext ctx;
  private final Registry registry;
  private final ExecutorService exec;

  private Map<RegistryServiceReference, Future<ServiceRegistration>> regs = new HashMap<RegistryServiceReference, Future<ServiceRegistration>>();

  private HashSet<Watch> watches = new HashSet<Watch>();

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
    synchronized (watches) {
      if (watches.add(new Watch(clazz, filter))) {
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
      }
    }
  }

  public void removeWatch(String clazz, String filter) {
    LogUtil.info("Removing watch " + clazz + " -> " + filter);
    Watch removed = new Watch(clazz, filter);
    ArrayList<Future<ServiceRegistration>> gc = new ArrayList<Future<ServiceRegistration>>();

    synchronized (watches) {
      watches.remove(removed);

      for (Iterator<RegistryServiceReference> iter = regs.keySet()
          .iterator(); iter.hasNext();) {
        RegistryServiceReference ref = iter.next();
        if (removed.matches(ref)) {
          boolean found = false;
          for (Watch w : watches) {
            if (w.matches(ref)) {
              found = true;
              break;
            }
          }

          if (!found) {
            Future<ServiceRegistration> f = regs.get(ref);
            gc.add(f);
            iter.remove();
          }
        }
      }
    }

    for (Future<ServiceRegistration> f : gc) {
      try {
        f.get().unregister();
      } catch (InterruptedException e) {
        LogUtil.warn("Interrupted prior to service unregister", e);
      } catch (ExecutionException e) {
        LogUtil.info("Ignoring failed service registration", e);
      }
    }
  }

  public void handleEvent(RegistryEvent event) {
    RegistryServiceReference ref = event.getReference();

    synchronized (watches) {
      if (!regs.containsKey(ref)) {
        for (Watch w : watches) {
          if (w.matches(ref)) {
            Future<ServiceRegistration> future = exec
                .submit(new Registration(ref));
            regs.put(ref, future);
            break;
          }
        }
      }
    }
  }
}
