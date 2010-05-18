/**
 * 
 */
package org.foo.http.tracker;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;

class HttpResourceTracker extends BundleTracker {

  private final HttpService http;

  public HttpResourceTracker(BundleContext context, HttpService http) {
    super(context, Bundle.RESOLVED | Bundle.STARTING | Bundle.ACTIVE, null );
    this.http = http;
  }

  @Override
  public Object addingBundle(Bundle bundle, BundleEvent event) {
    ArrayList<String> aliases = new ArrayList<String>();
    
    String[] resources = findResources(bundle);
    
    if ( resources != null ) {
      HttpContext ctx = new ProxyHttpContext(bundle);
      
      for ( String p : resources ) {
        String[] split = p.split("\\s*=\\s*");
        String alias = split[0];
        String file = split.length == 1 ? split[0] : split[1];
        try {
          System.out.println( "Registering " + alias + "->" + file );
          http.registerResources(alias, file, ctx);
          aliases.add( alias );
        } catch (NamespaceException e) {
          e.printStackTrace();
        }            
      }
    }
    
    return aliases.isEmpty() ? null : aliases.toArray(new String[aliases.size()]);
  }

  @Override
  public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
    String[] aliases = (String[]) object;
    for ( String alias : aliases ) {
      http.unregister(alias);
    }
  }

  public HttpService getHttp() {
    return http;
  }
  
  private String[] findResources(Bundle bundle) {
    String resources = (String) bundle.getHeaders().get( "HTTP_Resources" );
    if ( resources == null ) return null;
    ArrayList<String> ret = new ArrayList<String>();
    StringTokenizer tok = new StringTokenizer(resources, ",");
    while ( tok.hasMoreTokens() ) {
      ret.add( tok.nextToken() );
    }
    return ret.toArray( new String[ret.size()] );
  }
}