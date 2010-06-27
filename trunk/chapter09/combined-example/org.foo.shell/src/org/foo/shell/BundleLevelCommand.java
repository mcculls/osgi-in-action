package org.foo.shell;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.osgi.service.startlevel.StartLevel;

public class BundleLevelCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    StringTokenizer tok = new StringTokenizer(args);
    if (tok.countTokens() == 1) {
      out.println("Bundle " + args + " has level " +
        getStartLevelService().getBundleStartLevel(getBundle(tok.nextToken())));
    } else {
      String first = tok.nextToken();
      if ("-i".equals(first)) {
        getStartLevelService().setInitialBundleStartLevel(Integer.parseInt(tok.nextToken()));
      } else {
        getStartLevelService().setBundleStartLevel(getBundle(tok.nextToken()), Integer.parseInt(first));
      }
    }
  }

  private StartLevel getStartLevelService() {
    return (StartLevel) m_context.getService(m_context.getServiceReference(StartLevel.class.getName()));
  }

}
