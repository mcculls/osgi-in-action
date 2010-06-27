package org.foo.example;

import java.util.LinkedList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

  public class BarImpl implements Bar {

  }

  public class FooImpl implements Foo {
    private Bar bar;

    public void setBar(Bar bar) {
      this.bar = bar;
    }

    public Bar getBar() {
      return bar;
    }
  }

  class BarTracker extends ServiceTracker {
    private final FooImpl foo;
    private final BundleContext ctx;
    private LinkedList<Bar> found = new LinkedList<Bar>();
    private ServiceRegistration reg;
    
    BarTracker(FooImpl foo, BundleContext ctx) {
      super(ctx, Bar.class.getName(), null);
      this.foo = foo;
      this.ctx = ctx;
    }

    @Override
    public Object addingService(ServiceReference reference) {
      Bar bar = (Bar) super.addingService(reference);
      found.add(bar);
      if ( foo.getBar() == null ) {
        foo.setBar(bar);
        reg = ctx.registerService(Foo.class.getName(), foo, null);
      }
      return bar;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
      found.remove(service);
      if ( foo.getBar() == service ) {
        if ( found.isEmpty() ) {
          reg.unregister();
          foo.setBar(null);
          reg = null;
        }
        else {
          foo.setBar(found.getFirst());
        }
      }
      super.removedService(reference, service);
    }
  }
  
  public void start(BundleContext ctx) throws Exception {
    FooImpl foo = new FooImpl();
    
    BarTracker barTracker = new BarTracker(foo, ctx);
    barTracker.open();
    
    ServiceReference ref = ctx.getServiceReference(Foo.class.getName());
    
    if ( ref != null ) {
      throw new IllegalStateException( "Unexpected foo service" );
    }
    
    ServiceRegistration reg1 = ctx.registerService(Bar.class.getName(), new BarImpl(), null);
    
    ref = ctx.getServiceReference(Foo.class.getName());
    
    if ( ref == null ) {
      throw new IllegalStateException( "Expected foo service" );
    }
    
    ServiceRegistration reg2 = ctx.registerService(Bar.class.getName(), new BarImpl(), null);
    
    ref = ctx.getServiceReference(Foo.class.getName());
    
    if ( ref == null ) {
      throw new IllegalStateException( "Expected foo service" );
    }
    
    reg1.unregister();
    
    ref = ctx.getServiceReference(Foo.class.getName());
    
    if ( ref == null ) {
      throw new IllegalStateException( "Expected foo service" );
    }
    
    reg2.unregister();
    
    ref = ctx.getServiceReference(Foo.class.getName());
    
    if ( ref != null ) {
      throw new IllegalStateException( "Unexpected foo service" );
    }
  }

  public void stop(BundleContext ctx) throws Exception {
  }

}
