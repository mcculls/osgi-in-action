package org.foo.shell;

import java.io.PrintStream;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

class StopCommand extends BasicCommand {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    Bundle bundle = getBundle(args);

    if (bundle == m_context.getBundle()) {
      new SelfStopThread(bundle).start();
    } else {
      bundle.stop();
    }
  }

  private static final class SelfStopThread extends Thread {
    private final Bundle m_self;

    public SelfStopThread(Bundle self) {
      super("SelfStopThread Bundle " + self.getBundleId());
      m_self = self;
    }

    public void run() {
      try {
        m_self.stop();
      } catch (BundleException e) {
        // Ignore
      }
    }
  }
}
