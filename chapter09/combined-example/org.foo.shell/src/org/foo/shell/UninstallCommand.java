package org.foo.shell;

import java.io.PrintStream;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class UninstallCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle bundle = getBundle(args);

    if (bundle == m_context.getBundle()) {
      new SelfUninstallThread(bundle).start();
    } else {
      bundle.uninstall();
    }
  }

  private static final class SelfUninstallThread extends Thread {
    private final Bundle m_self;

    public SelfUninstallThread(Bundle self) {
      super("SelfUpdateThread Bundle " + self.getBundleId());
      m_self = self;
    }

    public void run() {
      try {
        m_self.uninstall();
      } catch (BundleException e) {
        // Ignore
      }
    }
  }
}
