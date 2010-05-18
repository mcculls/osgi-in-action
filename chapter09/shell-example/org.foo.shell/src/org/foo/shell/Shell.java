package org.foo.shell;

import java.io.*;

public class Shell implements Runnable {
  private final Command m_command;
  private final BufferedReader m_in;
  private final PrintStream m_out;
  private final PrintStream m_err;

  public Shell(Command command, BufferedReader in, PrintStream out, PrintStream err) {
    m_command = command;
    m_in = in;
    m_out = out;
    m_err = err;
  }

  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      m_out.print("-> ");

      String cmdLine;
      try {
        cmdLine = m_in.readLine();
      } catch (IOException ex) {
        if (!Thread.currentThread().isInterrupted()) {
          ex.printStackTrace(m_err);
          m_err.println("Unable to read from stdin - exiting now");
        }
        return;
      }

      if (cmdLine == null) {
        m_out.println("Bye bye");
        return;
      }

      try {
        m_command.exec(cmdLine, m_out, m_err);
      } catch (Throwable t) {
        t.printStackTrace(m_err);
      }
    }
  }
}
