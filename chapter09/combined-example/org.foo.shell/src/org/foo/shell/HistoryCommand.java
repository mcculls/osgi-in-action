package org.foo.shell;

import java.io.PrintStream;
import java.util.List;

public class HistoryCommand extends BasicCommand {
  private final History m_history;

  public HistoryCommand(History history) {
    m_history = history;
  }

  public void exec(String args, PrintStream out, PrintStream err) throws Exception {
    List<String> history = m_history.get();
    int count = (args != null) ? Integer.parseInt(args.trim()) : history.size();

    if (count > history.size()) {
      count = history.size();
    }

    for (int i = count; i > 0; i--) {
      out.println(history.remove(history.size() - 1));
    }
  }
}
