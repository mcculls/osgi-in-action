package org.foo.shell;

import java.io.PrintStream;
import java.util.*;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;

public class ResolveCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    boolean success;
    if (args == null) {
      success = getPackageAdminService().resolveBundles(null);
    } else {
      List<Bundle> bundles = new ArrayList<Bundle>();
      StringTokenizer tok = new StringTokenizer(args);
      while (tok.hasMoreTokens()) {
        bundles.add(getBundle(tok.nextToken()));
      }
      success =getPackageAdminService().resolveBundles(bundles.toArray(new Bundle[bundles.size()]));
    }
    out.println(success ? "Success" : "Failure");
  }

  private PackageAdmin getPackageAdminService() {
    return (PackageAdmin) m_context.getService(m_context.getServiceReference(PackageAdmin.class.getName()));
  }
}
