package org.foo.shell.commands;

import java.io.PrintStream;

import org.foo.shell.BasicCommand;
import org.osgi.service.startlevel.StartLevel;

public class StartLevelCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    if (args == null) {
      out.println(getStartLevelService().getStartLevel());
    } else {
      getStartLevelService().setStartLevel(Integer.parseInt(args.trim()));
    }
  }

  private StartLevel getStartLevelService() {
    return (StartLevel) m_context.getService(m_context.getServiceReference(StartLevel.class.getName()));
  }
}
