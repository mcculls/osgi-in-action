package org.foo.shell.telnet;

import java.io.IOException;

import java.net.ServerSocket;

import org.foo.shell.Binding;
import org.foo.shell.Command;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator {

  private volatile Binding m_binding;
  private volatile ServiceTracker tracker;

  public void start(final BundleContext context) throws Exception {
    tracker = new ServiceTracker(context, Command.class.getName(), new ServiceTrackerCustomizer() {
      
      public Object addingService(ServiceReference ref) {
        Command command = (Command) context.getService(ref);
        startTelnet(context, command);
        return command;
      }
      
      public void modifiedService(ServiceReference ref, Object service) {
        // TODO Auto-generated method stub
        
      }
      
      public void removedService(ServiceReference ref, Object service) {
        stopTelnet();
        context.ungetService(ref);
      }
      
    });

    tracker.open();
  }

  public void stop(BundleContext context) throws Exception {
    tracker.close();
  }

  private void startTelnet(BundleContext context, Command command) {
    final int port = getPort(context);

    final int max = getMaxConnections(context);

    try {
      m_binding = getTelnetBinding(command, port, max);
      m_binding.start();
      System.out.println("Bundle: " + context.getBundle().getSymbolicName() + " started with bundle id " +
          context.getBundle().getBundleId() + " - listening on port " + port);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void stopTelnet() {
    if ( m_binding != null ) {
      try {
        m_binding.stop();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Binding getTelnetBinding(Command execute, int port, int max) throws IOException {
    return new TelnetBinding(execute, new ServerSocket(port), max);
  }

  private int getMaxConnections(BundleContext context) {
    String maxConnectionsProperty = context.getProperty("org.foo.shell.connection.max");
    int maxConnections = 4;
    if (maxConnectionsProperty != null) {
      maxConnections = Integer.parseInt(maxConnectionsProperty);
    }
    return maxConnections;
  }

  private int getPort(BundleContext context) {
    String portProperty = context.getProperty("org.foo.shell.port");
    int port = 7070;
    if (portProperty != null) {
      port = Integer.parseInt(portProperty);
    }
    return port;
  }


}
