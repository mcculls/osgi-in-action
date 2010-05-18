package org.foo.shell.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.foo.shell.BasicCommand;

public class RunCommand extends BasicCommand {

  private ExecuteCommand exec;
  
  public void exec(String args, PrintStream out, PrintStream err)
      throws Exception {
    URL script = getScript(args.trim());
    InputStream in = script.openStream();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      for(;;) {
        String line = reader.readLine();
        if ( line == null ) {
          break;
        }
        out.println( line );
        if ( !line.startsWith("#") ) {
          exec.exec(line, out, err);
          out.flush();
          err.flush();
        }
      }
    }
    finally {
      in.close();
    }
  }

  private URL getScript(String args) throws MalformedURLException {
    try {
      return new URL(args);
    }
    catch (MalformedURLException e) {
      return new File(args).toURI().toURL();
    }
  }

  public void setExecuteCommand(ExecuteCommand exec) {
    this.exec = exec;
  }
}
