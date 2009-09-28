package org.foo.dosgi.test;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  public void start(BundleContext context) throws Exception {
    Hashtable props = new Hashtable();
    props.put("service.exported.interfaces","*");
    
    context.registerService(Foo.class.getName(), new FooImpl(), props);
    
    Filter filter = context.createFilter("(&(" + Constants.OBJECTCLASS + "=" + Foo.class.getName() + ")(service.imported=*))"); 
    ServiceTracker tracker = new ServiceTracker(context, filter, null) {
      @Override
      public Object addingService(ServiceReference reference) {
        System.out.println( "Found " + reference + " !!!!!!!" );
        return super.addingService(reference);
      }

      @Override
      public void removedService(ServiceReference reference, Object service) {
        System.out.println( "Lost " + reference + " !!!!!!!" );
        super.removedService(reference, service);
      }  
    };
    tracker.open();
  }

  public void stop(BundleContext context) throws Exception {
  }

}
