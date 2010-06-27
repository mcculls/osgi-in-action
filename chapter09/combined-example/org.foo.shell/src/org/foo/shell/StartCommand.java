package org.foo.shell;

import java.io.PrintStream;
import org.osgi.framework.Bundle;

public class StartCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle bundle = getBundle(args);

    bundle.start();
  }
}
