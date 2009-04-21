package org.foo.shell;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.foo.shell.BasicCommand;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigAdminCommand extends BasicCommand {

	public void exec(String args, PrintStream out, PrintStream err)
			throws Exception {
		ConfigurationAdmin admin = getConfigurationAdmin();
		if (admin != null) {
			args = args.trim();
			if (args.startsWith("list")) {
				Configuration[] configurations = null;
				if (args.equals("list")) {
				     configurations = admin.listConfigurations(null);
				}
				else {
					configurations = admin.listConfigurations(args.substring("list".length()).trim());
				}
				if (configurations != null) {
					for (Configuration configuration : configurations) {
						Dictionary properties = configuration.getProperties();
						for (Enumeration e = properties.keys();e.hasMoreElements();) {
							Object key = e.nextElement();
							out.println(key + "=" + properties.get(key));
						}
					}
				}
			}
			else if (args.startsWith("add-factory")) {
				String pid = args.substring("add-factory".length(), args.indexOf(" ", "add-factory ".length())).trim();
				Configuration conf = admin.createFactoryConfiguration(pid, null);
				createConfiguration(args.substring(("add-factory " + pid).length()).trim(), pid, conf);
			}
			else if (args.startsWith("add")) {
				String pid = args.substring("add".length(), args.indexOf(" ", "add ".length())).trim();
				Configuration conf = admin.getConfiguration(pid, null);
				createConfiguration(args.substring(("add " + pid).length()).trim(), pid, conf);
			}
			else if (args.startsWith("remove-factory")) {
				Configuration[] configurations = admin.listConfigurations("(service.pid=" + args.substring("remove-factory".length()).trim() + ")");
				configurations[0].delete();
				
			}
			else if (args.startsWith("remove")) {
				String pid = args.substring("remove".length()).trim();
				Configuration conf = admin.getConfiguration(pid);
				conf.delete();
			}
		}
		else
		{
			out.println("No ConfigurationAdmin service found!");
		}
	}

	private void createConfiguration(String args, String pid, Configuration conf)
			throws IOException {
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
	
	private ConfigurationAdmin getConfigurationAdmin() {
		ServiceReference ref = m_context.getServiceReference(ConfigurationAdmin.class.getName());
		if (ref != null) {
			return (ConfigurationAdmin) m_context.getService(ref);
		}
		return null;
	}

}
