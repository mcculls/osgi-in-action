package org.foo.shell;

import java.io.*;
import java.util.*;

import org.foo.shell.commands.BundleLevelCommand;
import org.foo.shell.commands.BundlesCommand;
import org.foo.shell.commands.ConfigAdminCommand;
import org.foo.shell.commands.DeploymentPackageCommand;
import org.foo.shell.commands.ExecuteCommand;
import org.foo.shell.commands.HelpCommand;
import org.foo.shell.commands.History;
import org.foo.shell.commands.HistoryCommand;
import org.foo.shell.commands.HistoryDecorator;
import org.foo.shell.commands.InstallCommand;
import org.foo.shell.commands.MetaDataCommand;
import org.foo.shell.commands.RefreshCommand;
import org.foo.shell.commands.RepositoryCommand;
import org.foo.shell.commands.ResolveCommand;
import org.foo.shell.commands.ResolverCommand;
import org.foo.shell.commands.RunCommand;
import org.foo.shell.commands.StartCommand;
import org.foo.shell.commands.StartLevelCommand;
import org.foo.shell.commands.StopCommand;
import org.foo.shell.commands.UninstallCommand;
import org.foo.shell.commands.UpdateCommand;
//import org.foo.shell.telnet.TelnetBinding;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
  private volatile History m_history;
  private volatile ServiceRegistration reg;

  public void start(final BundleContext context) throws Exception {
    Command execute = getExecuteCommand(context);
    reg = context.registerService(Command.class.getName(), execute, null);
  }

  public void stop(final BundleContext context) throws Exception {
    reg.unregister();
    writeHistory(m_history, context);
  }

  private void writeHistory(History history, BundleContext context) throws IOException {
    List<String> list = history.get();
    File log = context.getDataFile("log.txt");

    if (log.exists() && !log.delete()) {
      throw new IOException("Unable to delete previous log file!");
    }
    write(list, log);
  }

  private void write(List<String> list, File log) throws IOException {
    PrintWriter output = null;
    IOException original = null;
    try {
      output = new PrintWriter(new FileWriter(log));

      for (String entry : list) {
        output.println(entry);
      }
    } catch (IOException ex) {
      original = ex;
    } finally {
      try {
        if (output != null) {
          output.close();
        }
      } finally {
        if (original != null) {
          throw original;
        }
      }
    }
  }

  private Command getExecuteCommand(BundleContext context) throws IOException {
    Map<String, Command> commands = new HashMap<String, Command>();

    commands.put("help", new HelpCommand(commands).setContext(context).setHelp("help - display commands."));
    commands.put("install", new InstallCommand().setContext(context).setHelp(
      "install <url> - Install the bundle jar at the given url."));
    commands.put("start", new StartCommand().setContext(context).setHelp(
      "start <id> - Start the bundle with the given bundle id."));
    commands.put("stop", new StopCommand().setContext(context).setHelp(
      "stop <id> - Stop the bundle with the given bundle id."));
    commands.put("uninstall", new UninstallCommand().setContext(context).setHelp(
      "uninstall <id> - Uninstall the bundle with the given bundle id."));
    commands.put("update", new UpdateCommand().setContext(context).setHelp(
      "update <id> - Update the bundle with the given bundle id."));
    commands.put("startlevel", new StartLevelCommand().setContext(context).setHelp(
      "startlevel [<level>] - Get or set the framework startlevel."));
    commands.put("bundlelevel", new BundleLevelCommand().setContext(context).setHelp(
      "bundlelevel [-i] [<level>] <id> - Get or set (initial) bundle startlevel."));
    commands.put("refresh", new RefreshCommand().setContext(context).setHelp("refresh [<id> ...] - refresh bundles."));
    commands.put("resolve", new ResolveCommand().setContext(context).setHelp("resolve [<id> ...] - resolve bundles."));
    commands.put("bundles", new BundlesCommand().setContext(context).setHelp(
      "bundles - Print information about the currently installed bundles"));
    commands.put("obr-repository", new RepositoryCommand().setContext(context).setHelp(
      "obr-repository {list-urls|add-url <url> |remove-url <url>|list}"));
    commands.put("obr-resolver", new ResolverCommand().setContext(context).setHelp("obr-resolver <resource-filter>"));
    commands.put("dpa", new DeploymentPackageCommand().setContext(context).setHelp("dpa {list|install <url>|uninstall <name>}"));
    commands.put("cm", new ConfigAdminCommand().setContext(context).setHelp("cm {list|add <pid> [key=value ...]|add-factory <pid> [key=value]|remove-factory <pid>|remove <pid>}"));
    commands.put("type", new MetaDataCommand().setContext(context).setHelp("type <bundle-id>"));
    RunCommand run = new RunCommand();
    commands.put("run", run.setContext(context).setHelp("run <script-file>" ) );

    ExecuteCommand exec = new ExecuteCommand(commands);
    run.setExecuteCommand(exec);
    
    HistoryDecorator command = new HistoryDecorator(exec, readHistory(context));
    context.addFrameworkListener(command);
    context.addBundleListener(command);
    m_history = command;

    commands.put("history", new HistoryCommand(command).setContext(context).setHelp(
      "history {<n>} - Show the last commands (up to <n> if present)."));

    return command;
  }

  private List<String> readHistory(BundleContext context) throws IOException {
    File log = context.getDataFile("log.txt");
    List<String> result = new ArrayList<String>();

    if (log.isFile()) {
      read(log, result);
    }

    return result;
  }

  private void read(File log, List<String> result) throws IOException {
    BufferedReader input = null;
    IOException original = null;
    try {
      input = new BufferedReader(new FileReader(log));
      for (String line = input.readLine(); line != null; line = input.readLine()) {
        result.add(line);
      }
    } catch (IOException ex) {
      original = ex;
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } finally {
        if (original != null) {
          throw original;
        }
      }
    }
  }
}
