package org.foo.dosgi.hooks;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import org.foo.dosgi.helper.LogUtil;
import org.foo.dosgi.helper.PropertyUtil;
import org.foo.dosgi.registry.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ExportedServiceTracker extends ServiceTracker {

  private final BundleContext ctx;
  private final Registry registry;
  private final String[] intents;
  private final String[] configs;

  public ExportedServiceTracker(BundleContext ctx, Registry registry,
      String[] intents, String[] configs) {
    super(ctx, createFilter(ctx), null);
    this.ctx = ctx;
    this.registry = registry;
    this.intents = intents == null ? new String[0] : intents;
    this.configs = configs == null ? new String[0] : configs;
  }

  private static Filter createFilter(BundleContext ctx) {
    try {
      return ctx.createFilter("(service.exported.interfaces=*)");
    } catch (InvalidSyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public Object addingService(ServiceReference ref) {
    Object svc = super.addingService(ref);
    LogUtil.info("Found " + ref);

    if (isValidService(ref)) {
      String[] ifaces = findExportedInterfaces(ref);
      for (String iface : ifaces) {
        LogUtil.info("Registering " + iface + "->" + svc);
        registry.registerService(ref, iface, svc);
      }
    }

    return svc;
  }

  @Override
  public void removedService(ServiceReference ref, Object svc) {
    String[] ifaces = findExportedInterfaces(ref);
    if (ifaces != null && isValidService(ref)) {
      registry.unregisterService(ref);
      ctx.ungetService(ref);
    }
  }

  /**
   * Find interfaces exported by this service
   * 
   * @param ref
   * @return
   * @throws ClassNotFoundException
   */
  private String[] findExportedInterfaces(ServiceReference ref) {
    Object ifaces = ref.getProperty("service.exported.interfaces");
    if (ifaces == null) {
      return null;
    } else {
      String[] strs = PropertyUtil.toStringArray(ifaces);
      if (strs.length == 1 && "*".equals(strs[0])) {
        ifaces = ref.getProperty(Constants.OBJECTCLASS);
        strs = PropertyUtil.toStringArray(ifaces);
      }
      return strs;
    }
  }

  /**
   * Checks if reference contains any unsupported intents or references
   * 
   * @param ref
   * @return
   */
  private boolean isValidService(ServiceReference ref) {
    List<String> list = readIntents(ref);
    list.removeAll(Arrays.asList(intents));
    if (list.isEmpty()) {
      list = readConfigs(ref);
      list.removeAll(Arrays.asList(configs));
      return list.isEmpty();
    } else {
      return false;
    }
  }

  private List<String> readIntents(ServiceReference ref) {
    Object val = ref.getProperty("service.exported.intents");
    ArrayList<String> intents = new ArrayList<String>();
    if (val != null) {
      intents.addAll(Arrays.asList(PropertyUtil.toStringArray(val)));
    }

    val = ref.getProperty("service.exported.intents.extra");
    if (val != null) {
      intents.addAll(Arrays.asList(PropertyUtil.toStringArray(val)));
    }

    return intents;
  }

  private List<String> readConfigs(ServiceReference ref) {
    ArrayList<String> configs = new ArrayList<String>();

    Object val = ref.getProperty("service.exported.configs");

    if (val != null) {
      configs.addAll(Arrays.asList(PropertyUtil.toStringArray(val)));
    }

    return configs;
  }
}
