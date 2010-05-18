package org.foo.httpclient.test;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.osgi.framework.*;

public class Activator implements BundleActivator {

  public void start(BundleContext ctx) {
    new Thread(new Runnable() {
      public void run() {
        ping(); // query google.com when the bundle starts
      }
    }).start();
  }

  public void stop(BundleContext ctx) {}

  void ping() {
    HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
    HttpMethod get = new GetMethod("http://www.google.com");
    try {

      System.out.println("GET " + get.getURI());
      client.executeMethod(get);
      byte[] buf = get.getResponseBody();
      System.out.println("GOT " + buf.length + " bytes");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      get.releaseConnection();
    }
  }
}
