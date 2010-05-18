package org.foo.shell.commands;

import java.io.PrintStream;
import java.net.URL;

import org.foo.shell.BasicCommand;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.*;

public class RepositoryCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    args = args.trim();
    RepositoryAdmin admin = getRepositoryAdmin();
    if (admin != null) {
      if ("list-urls".equalsIgnoreCase(args)) {
        for (Repository repo : admin.listRepositories()) {
          out.println(repo.getName() + " (" + repo.getURL() + ")");
        }
      } else if (args != null) {
        if (args.startsWith("add-url")) {
          admin.addRepository(new URL(args.substring("add-url".length())));
        } else if (args.startsWith("remove-url")) {
          admin.removeRepository(new URL(args.substring("remove-url".length())));
        } else if ("list".equalsIgnoreCase(args)) {
          for (Repository repo : admin.listRepositories()) {
            for (Resource res : repo.getResources()) {
              out.println(res.getPresentationName() + " (" + res.getSymbolicName() + ") " + res.getVersion());
            }
          }
        }
      } else {
        out.println("Unknown command - use {list-urls|add-url|remove-url|list}");
      }
    } else {
      out.println("No RepositoryAdmin service found...");
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
