package org.foo.stockprovider.server;

import java.util.Dictionary;
import java.util.Hashtable;

import org.foo.stockprovider.StockProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

  public void start(BundleContext ctx) throws Exception {
    Dictionary props = new Hashtable(); 

    props.put("service.exported.interfaces","*");
    props.put("service.exported.intents","SOAP");
    props.put("service.exported.configs","org.apache.cxf.ws");
    props.put("org.apache.cxf.ws.address","http://localhost:9090/stockprovider");

    ctx.registerService(StockProvider.class.getName(), new StockProviderImpl(), props); 
  }

  public void stop(BundleContext ctx) throws Exception {
  }

}
