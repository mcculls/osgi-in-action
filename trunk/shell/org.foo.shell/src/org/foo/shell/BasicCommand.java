package org.foo.shell;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public abstract class BasicCommand implements Command {
  protected volatile BundleContext m_context;
  private volatile String m_help;

  protected BasicCommand setContext(BundleContext context) {
    m_context = context;
    return this;
  }

  protected BasicCommand setHelp(String help) {
    m_help = help;
    return this;
  }

  public Bundle getBundle(String id) {
    Bundle bundle = null;
    if (id != null) {
      try {
        bundle = m_context.getBundle(Long.parseLong(id.trim()));
      }
      catch (NumberFormatException e) {
        for (Bundle b : m_context.getBundles()) {
          String uid = b.getSymbolicName() + ":" + b.getHeaders().get(Constants.BUNDLE_VERSION);
          if ( uid.equals( id.trim() ) ) {
            bundle = b;
            break;
          }
        }
      }
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
