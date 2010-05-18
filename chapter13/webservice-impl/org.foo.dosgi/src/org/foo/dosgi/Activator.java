package org.foo.dosgi;

import java.util.HashMap;

import java.util.Map;

import org.foo.dosgi.helper.LogUtil;
import org.foo.dosgi.helper.PropertyUtil;
import org.foo.dosgi.helper.RegistryWatcher;
import org.foo.dosgi.hooks.ExportedServiceTracker;
import org.foo.dosgi.hooks.ImportedServiceFindHook;
import org.foo.dosgi.hooks.ImportedServiceListenerHook;
import org.foo.dosgi.registry.Registry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  class RegistryTracker extends ServiceTracker {
    public RegistryTracker(BundleContext ctx) {
      super(ctx, Registry.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference reference) {
      Registry registry = (Registry) super.addingService(reference);
      String[] intents = PropertyUtil.toStringArray(reference
          .getProperty("remote.intents.supported"));
      String[] configs = PropertyUtil.toStringArray(reference
          .getProperty("remote.configs.supported"));
      bindRegistry(registry, intents, configs);
      return registry;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
      unbindRegistry((Registry) service);
      super.removedService(reference, service);
    }
  }

  private volatile BundleContext ctx;
  private volatile RegistryTracker tracker;

  private Map<Registry, ServiceRegistration[]> serviceRegs = new HashMap<Registry, ServiceRegistration[]>();
  private Map<Registry, RegistryWatcher> watchers = new HashMap<Registry, RegistryWatcher>();
  private Map<Registry, ServiceTracker> trackers = new HashMap<Registry, ServiceTracker>();

  public void start(BundleContext ctx) throws Exception {
    this.ctx = ctx;
    LogUtil.ctx = ctx;
    tracker = new RegistryTracker(ctx);
    tracker.open();
  }

  public void stop(BundleContext ctx) throws Exception {
    tracker.close();
    this.ctx = null;
  }

  private synchronized void bindRegistry(Registry registry,
      String[] intents, String[] configs) {
    RegistryWatcher watcher = new RegistryWatcher(ctx, registry);
    watchers.put(registry, watcher);

    ServiceRegistration[] regs = new ServiceRegistration[2];

    ExportedServiceTracker export = new ExportedServiceTracker(ctx,
        registry, intents, configs);
    export.open();

    trackers.put(registry, export);

    ImportedServiceFindHook find = new ImportedServiceFindHook(watcher);
    regs[0] = ctx.registerService(FindHook.class.getName(), find, null);

    ImportedServiceListenerHook listener = new ImportedServiceListenerHook(
        watcher);
    regs[1] = ctx.registerService(ListenerHook.class.getName(), listener,
        null);

    serviceRegs.put(registry, regs);
  }

  private synchronized void unbindRegistry(Registry service) {
    ServiceTracker tracker = trackers.remove(service);
    if (tracker != null) {
      tracker.close();
    }

    RegistryWatcher watcher = watchers.remove(service);
    if (watcher != null) {
      watcher.destroy();
    }

    ServiceRegistration[] regs = serviceRegs.remove(service);
    if (regs != null) {
      for (ServiceRegistration reg : regs) {
        reg.unregister();
      }
    }
  }
}
