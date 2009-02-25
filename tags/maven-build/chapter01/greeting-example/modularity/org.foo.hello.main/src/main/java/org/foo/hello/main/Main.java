package org.foo.hello.main;

import java.util.HashMap;
import java.util.Map;
import org.apache.felix.framework.Felix;
import org.osgi.framework.*;

public class Main {
  static Felix m_framework;

  public static void main(String[] args) throws Exception {
    try {

      final Map configMap = new HashMap();
      configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
      configMap.put("felix.cache.rootdir", "target");
      m_framework = new Felix(configMap);
      m_framework.init();

      final BundleContext context = m_framework.getBundleContext();

      Bundle api = context.installBundle("file:target/bundle/org.foo.hello.jar");
      Bundle client = context.installBundle("file:target/bundle/org.foo.hello.client.jar");

      m_framework.start();

      client.loadClass("org.foo.hello.client.Client").newInstance();

      m_framework.stop();

    } catch (Exception ex) {
      System.err.println("Error starting program: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }
}
