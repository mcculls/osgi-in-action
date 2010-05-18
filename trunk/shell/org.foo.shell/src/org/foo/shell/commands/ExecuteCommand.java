package org.foo.shell.commands;

import java.io.PrintStream;
import java.util.Map;

import org.foo.shell.Command;

public class ExecuteCommand implements Command {
  private final Map<String, Command> m_commands;

  public ExecuteCommand(Map<String, Command> commands) {
    m_commands = commands;
  }

  public void exec(String args, PrintStream out, PrintStream err) {
    int idx = args.indexOf(' ');

    boolean found = false;

    Command command = m_commands.get((idx > 0) ? args.substring(0, idx) : args);

    if (command != null) {
      found = true;

      try {
        command.exec((idx > 0) ? args.substring(idx) : null, out, err);
      } catch (Exception ex) {
        ex.printStackTrace(err);
        out.println("Unable to execute: " + args);
      }
    }

    if (!found && !(args.trim().length() == 0)) {
      out.println("Unknown command: " + args);
    }
  }
}
