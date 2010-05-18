package org.foo.shell;

import java.io.PrintStream;
import java.io.Reader;

public interface ShellFactory {
  Shell createShell(Reader in, PrintStream out);
}
