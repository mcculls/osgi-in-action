package org.foo.dosgi.hooks;

import java.util.Collection;

import org.foo.dosgi.helper.RegistryWatcher;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.FindHook;

public class ImportedServiceFindHook implements FindHook {

  private final RegistryWatcher watcher;

  public ImportedServiceFindHook(RegistryWatcher watcher) {
    this.watcher = watcher;
  }

  public void find(BundleContext ctx, java.lang.String name,
      java.lang.String filter, boolean allServices, Collection references) {
    watcher.findServices(name, filter);
  }

}
