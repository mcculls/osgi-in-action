package org.foo.shell.commands;

import java.io.PrintStream;

import org.foo.shell.BasicCommand;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class UpdateCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle bundle = getBundle(args);

    if (bundle == m_context.getBundle()) {
      new SelfUpdateThread(bundle).start();
    } else {
      bundle.update();
    }
  }

  private static final class SelfUpdateThread extends Thread {
    private final Bundle m_self;

    public SelfUpdateThread(Bundle self) {
      super("SelfUpdateThread Bundle " + self.getBundleId());
      m_self = self;
    }

    public void run() {
      try {
        m_self.update();
      } catch (BundleException e) {
        // Ignore
      }
    }
  }
}
