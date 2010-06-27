package org.foo.shell;

import java.io.PrintStream;
import java.util.*;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;

public class ResolveCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    if (args == null) {
      getPackageAdminService().resolveBundles(null);
    } else {
      List<Bundle> bundles = new ArrayList<Bundle>();
      StringTokenizer tok = new StringTokenizer(args);
      while (tok.hasMoreTokens()) {
        bundles.add(getBundle(tok.nextToken()));
      }
      getPackageAdminService().resolveBundles(bundles.toArray(new Bundle[bundles.size()]));
    }
  }

  private PackageAdmin getPackageAdminService() {
    return (PackageAdmin) m_context.getService(m_context.getServiceReference(PackageAdmin.class.getName()));
  }
}
