package org.foo.shell;

import java.io.PrintStream;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class BundlesCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle[] bundles = m_context.getBundles();

    out.println("  ID      State      Name");

    for (Bundle bundle : bundles) {
      printBundle(bundle.getBundleId(), getStateString(bundle.getState()), (String) bundle.getHeaders().get(
        Constants.BUNDLE_NAME), bundle.getLocation(), bundle.getSymbolicName(), out);
    }
  }

  private String getStateString(int state) {
    switch (state) {
      case Bundle.INSTALLED:
        return "INSTALLED";
      case Bundle.RESOLVED:
        return "RESOLVED";
      case Bundle.STARTING:
        return "STARTING";
      case Bundle.ACTIVE:
        return "ACTIVE";
      case Bundle.STOPPING:
        return "STOPPING";
      default:
        return "UNKNOWN";
    }
  }

  private void printBundle(long id, String state, String name, String location, String symbolicName, PrintStream out) {
    out.print("[");
    String idString = Long.toString(id);
    while (idString.length() < 4) {
      idString = " " + idString;
    }
    out.print(idString);
    out.print("] [");
    while (state.length() < 9) {
      state = " " + state;
    }
    out.print(state);
    out.print("] ");
    out.println(name);
    out.println("                   Location: " + location);
    out.println("                   Symbolic Name: " + symbolicName);
  }

}
