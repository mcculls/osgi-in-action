package org.foo.shell.commands;

import java.io.PrintStream;

import org.foo.shell.BasicCommand;
import org.osgi.framework.Bundle;

public class InstallCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle bundle = m_context.installBundle(args);

    out.println("Bundle: " + bundle.getBundleId());
  }
}
