package org.foo.shell.tty;

import java.io.*;

import org.foo.shell.Binding;
import org.foo.shell.Command;
import org.foo.shell.Shell;

public class TtyBinding implements Binding {
  private final Command m_command;
  private Shell shell;
  private Thread m_thread;

  public TtyBinding(Command command) {
    m_command = command;
  }

  public void start() {
    shell = new Shell(m_command, new BufferedReader(new InputStreamReader(System.in)),
        System.out, System.err);
    m_thread = new Thread(shell);
    m_thread.start();
  }

  public void stop() throws InterruptedException {
    m_thread.interrupt();
    shell = null;
  }
}
