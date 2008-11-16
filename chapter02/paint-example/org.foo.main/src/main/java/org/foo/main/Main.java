/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.foo.main;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.framework.Felix;
import org.osgi.framework.*;

public class Main {
  private static Felix m_framework;

  /**
   * This method actually performs the creation of the application window. It is
   * intended to be called by the Swing event thread and should not be called
   * directly.
   **/
  public static void main(String[] args) throws Exception {
    // Add a shutdown hook to cleanly shutdown the framework
    // after the PaintFrame exits the VM when its window is closed.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          m_framework.stop();
          m_framework.waitForStop(0);
        } catch (Exception ex) {
          System.err.println("Error stopping framework: " + ex);
        }
      }
    });

    // Configure and create a framework instance, then install all required
    // bundles and start the framework.
    Bundle frameBundle = null;
    try {
      final Map configMap = new HashMap();
      configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
      configMap.put("felix.cache.rootdir", "target");
      m_framework = new Felix(configMap);
      m_framework.init();
      final BundleContext context = m_framework.getBundleContext();
      context.installBundle("file:target/bundle/org.foo.shape.jar");
      context.installBundle("file:target/bundle/org.foo.shape.circle.jar");
      context.installBundle("file:target/bundle/org.foo.shape.square.jar");
      context.installBundle("file:target/bundle/org.foo.shape.triangle.jar");
      frameBundle = context.installBundle("file:target/bundle/org.foo.paint.jar");
      m_framework.start();
    } catch (Exception ex) {
      System.err.println("Error starting framework: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }

    // Once the bundles are installed and the framework is started, load
    // the PaintFrame class and use reflection to invoke its static main,
    // which starts the application like normal.
    final Class frameClass = frameBundle.loadClass("org.foo.paint.PaintFrame");
    try {
      Method method = frameClass.getMethod("main", new Class[] { String[].class });
      method.invoke(null, new Object[] { new String[0] });
    } catch (Exception ex) {
      System.err.println("Error invoking main() method: " + ex + " cause = " + ex.getCause());
    }
  }
}
