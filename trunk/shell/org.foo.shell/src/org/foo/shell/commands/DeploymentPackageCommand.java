package org.foo.shell.commands;

import java.io.PrintStream;
import java.net.URL;

import org.foo.shell.BasicCommand;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;

public class DeploymentPackageCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    DeploymentAdmin admin = getDeploymentAdmin();

    if (admin == null) {
      out.println("No DeploymentAdmin service found.");
      return;
    }
    if (args != null) {
      if (args.trim().equalsIgnoreCase("list")) {
        for (DeploymentPackage dp : admin.listDeploymentPackages()) {
          out.println(dp.getName() + " " + dp.getVersion());
        }
      } else if (args.trim().startsWith("uninstall ")) {
        DeploymentPackage dp = admin.getDeploymentPackage(args.trim().substring("uninstall ".length()));
        if (dp != null) {
          dp.uninstall();
        } else {
          out.println("No such package");
        }
      } else if (args.trim().startsWith("install ")) {
        DeploymentPackage dp = admin.installDeploymentPackage(new URL(args.trim().substring("install ".length())).openStream());
        out.println(dp.getName() + " " + dp.getVersion());
      }
    } else {
      out.println("Use {list|install <url>|uninstall <name>}");
    }
  }

  private DeploymentAdmin getDeploymentAdmin() {
    ServiceReference ref = m_context.getServiceReference(DeploymentAdmin.class.getName());
    if (ref != null) {
      return (DeploymentAdmin) m_context.getService(ref);
    }
    return null;
  }
}
