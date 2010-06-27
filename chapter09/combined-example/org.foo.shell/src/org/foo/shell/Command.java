package org.foo.shell;

import java.io.PrintStream;

public interface Command {
  public void exec(String args, PrintStream out, PrintStream err) throws Exception;
}
