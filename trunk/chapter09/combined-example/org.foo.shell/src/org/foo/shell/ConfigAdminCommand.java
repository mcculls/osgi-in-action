package org.foo.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigAdminCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err)
    throws Exception {
    args=args.trim();
    if (args.startsWith("list")) {
      listConfigurations(args.substring("list".length()).trim(), out);
    } else if (args.startsWith("add-cfg")) {
      addConfiguration(args.substring("add-cfg".length()).trim());
    } else if (args.startsWith("remove-cfg")) {
      removeConfiguration(args.substring("remove-cfg".length()).trim());
    } else if (args.startsWith("add-factory-cfg")) {
      addFactoryConfiguration(args.substring("add-factory-cfg".length()).trim());
    } else if (args.startsWith("remove-factory-cfg")) {
      removeFactoryConfiguration(args.substring("remove-factory-cfg".length()).trim());
    }
  }
  
  private void listConfigurations(String filter, PrintStream out) throws IOException, InvalidSyntaxException {
    Configuration[] configurations = admin().listConfigurations(
      ((filter.length() == 0) ? null : filter));
    
    if (configurations != null) {
      for (Configuration configuration : configurations) {
        Dictionary properties = configuration.getProperties();
        for (Enumeration e = properties.keys(); e
          .hasMoreElements();) {
          Object key = e.nextElement();
            out.println(key + "=" + properties.get(key));
        }
        out.println();
      }
    }
  }
  
  private void addConfiguration(String args) throws IOException{
    String pid = args.substring(0, args.indexOf(" ")).trim();
    Configuration conf = admin().getConfiguration(pid, null);
    createConfiguration(args.substring(pid.length()).trim(), pid, conf);
  }
  
  private void removeConfiguration(String pid) throws IOException {
    Configuration conf = admin().getConfiguration(pid);
    conf.delete();
  }
  
  private void addFactoryConfiguration(String args) throws IOException {
    String pid = args.substring(0, args.indexOf(" ")).trim();
    Configuration conf = admin().createFactoryConfiguration(pid,
      null);
    createConfiguration(args.substring(pid.length()).trim(), pid, conf);
  }
  
  private void removeFactoryConfiguration(String pid) throws IOException, InvalidSyntaxException {
    Configuration[] configurations = admin().listConfigurations(
      "(service.pid=" + pid + ")");
    configurations[0].delete();
  }

  private void createConfiguration(String args, String pid,
    Configuration conf) throws IOException {
    conf.setBundleLocation(null);
    Dictionary dict = conf.getProperties();
    if (dict == null) {
      dict = new Properties();
    }
    StringTokenizer tok = new StringTokenizer(args, " ");
    while (tok.hasMoreTokens()) {
      String[] entry = tok.nextToken().split("=");
      dict.put(entry[0], entry[1]);
    }
    conf.update(dict);
  }

  private ConfigurationAdmin admin() {
    ServiceReference ref = m_context
      .getServiceReference(ConfigurationAdmin.class.getName());
    if (ref != null) {
      return (ConfigurationAdmin) m_context.getService(ref);
    }
    return null;
  }

}
