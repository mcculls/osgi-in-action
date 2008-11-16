package org.foo.shell;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public abstract class BasicCommand implements Command {
  protected volatile BundleContext m_context;
  private volatile String m_help;

  BasicCommand setContext(BundleContext context) {
    m_context = context;
    return this;
  }

  BasicCommand setHelp(String help) {
    m_help = help;
    return this;
  }

  public Bundle getBundle(String id) {
    Bundle bundle = null;
    if (id != null) {
      bundle = m_context.getBundle(Long.parseLong(id.trim()));
    }
    if (bundle == null) {
      throw new IllegalArgumentException("No such bundle: " + id);
    }
    return bundle;
  }

  public String toString() {
    String result = m_help;
    if (result == null) {
      result = super.toString();
    }
    return result;
  }
}
