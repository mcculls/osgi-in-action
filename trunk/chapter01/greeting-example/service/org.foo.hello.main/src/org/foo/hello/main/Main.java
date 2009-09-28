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
      m_framework = new Felix(configMap);
      m_framework.init();

      final BundleContext context = m_framework.getBundleContext();

      Bundle provider = context.installBundle("file:bundles/provider-3.0.jar");
      Bundle consumer = context.installBundle("file:bundles/consumer-3.0.jar");

      m_framework.start();

      provider.start();
      consumer.start();
      consumer.stop();
      provider.stop();

      m_framework.stop();

    } catch (Exception ex) {
      System.err.println("Error starting program: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }
}
