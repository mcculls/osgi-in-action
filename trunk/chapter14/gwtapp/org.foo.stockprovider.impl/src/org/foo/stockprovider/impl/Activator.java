package org.foo.stockprovider.impl;

import org.foo.stockprovider.StockProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  public void start(BundleContext ctx) throws Exception {
    ctx.registerService( StockProvider.class.getName(), new StockProviderImpl(), null );
  }

  public void stop(BundleContext ctx) throws Exception {
    // TODO Auto-generated method stub
    
  }

}
