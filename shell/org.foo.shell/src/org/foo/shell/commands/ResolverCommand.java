package org.foo.shell.commands;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.foo.shell.BasicCommand;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.*;

public class ResolverCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    RepositoryAdmin admin = getRepositoryAdmin();
    Resolver resolver = admin.resolver();
    Resource[] resources = admin.discoverResources(args);
    if ((resources != null) && (resources.length > 0)) {
      resolver.add(resources[0]);
      if (resolver.resolve()) {
        List<Bundle> bundles = new ArrayList<Bundle>();
        for (Resource res : resolver.getRequiredResources()) {
          out.println("Deploying dependency: " + res.getPresentationName() + 
            " (" + res.getSymbolicName() + ") " + res.getVersion());
          bundles.add(m_context.installBundle(res.getURL().toURI().toString()));
        }
        bundles.add(m_context.installBundle(resources[0].getURL().toURI().toString()));
        for (Bundle bundle : bundles) {
          try {
            bundle.start();
          } catch (Exception ex) {
            ex.printStackTrace(out);
          }
        }
      } else {
        out.println("Can not resolve " + resources[0].getId() + " reason: ");
        for (Requirement req : resolver.getUnsatisfiedRequirements()) {
          out.println("missing " + req.getName() + " " + req.getFilter());
        }
      }
    } else {
      out.println("No such resource");
    }
  }

  private RepositoryAdmin getRepositoryAdmin() {
    ServiceReference ref = m_context.getServiceReference(RepositoryAdmin.class.getName());
    if (ref != null) {
      return (RepositoryAdmin) m_context.getService(ref);
    }
    return null;
  }

}
