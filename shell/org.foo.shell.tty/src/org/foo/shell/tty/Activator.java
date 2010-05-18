package org.foo.shell.tty;

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
        startTty(context, command);
        return command;
      }
      
      public void modifiedService(ServiceReference ref, Object service) {
      }
      
      public void removedService(ServiceReference ref, Object service) {
        stopTty();
        context.ungetService(ref);
      }
      
    });

    tracker.open();
  }

  public void stop(BundleContext context) throws Exception {
    tracker.close();
  }

  private void startTty(BundleContext context, Command command) {
    System.out.println("Bundle: " + context.getBundle().getSymbolicName() + " started with bundle id " +
        context.getBundle().getBundleId());
    m_binding = getTtyBinding(command);
    m_binding.start();
  }

  private void stopTty() {
    if ( m_binding != null ) {
      try {
        m_binding.stop();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Binding getTtyBinding(Command execute) {
    return new TtyBinding(execute);
  }
}
