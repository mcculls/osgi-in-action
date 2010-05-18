package org.foo.shell.commands;

import java.io.PrintStream;
import java.util.Map;

import org.foo.shell.BasicCommand;
import org.foo.shell.Command;

public class HelpCommand extends BasicCommand {
  private final Map<String, Command> m_commands;

  public HelpCommand(Map<String, Command> commands) {
    m_commands = commands;
  }

  public void exec(String args, PrintStream out, PrintStream err) {
    for (Command command : m_commands.values()) {
      out.println(command);
    }
  }
}