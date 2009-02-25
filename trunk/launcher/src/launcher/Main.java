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
package launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.felix.framework.Felix;
import org.osgi.framework.*;

public class Main {
  private static Felix m_framework;

  public static void main(String[] args) throws Exception {
    // Must specify a bundle directory.
    if (args.length != 1 || !new File(args[0]).isDirectory()) {
      System.out.println("Usage: <bundle-directory>");
      System.exit(0);
    }

    // Save arguments.
    final String bundleDir = args[0];

    // Look in the specified bundle directory to create a list
    // of all JAR files to install.
    File[] files = new File(bundleDir).listFiles();
    Arrays.sort(files);
    List bundleList = new ArrayList();
    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().endsWith(".jar")) {
        bundleList.add(files[i]);
      }
    }

    // If no bundle JAR files are in the directory, then exit.
    if (bundleList.isEmpty()) {
      System.out.println("No bundles to install.");
      System.exit(0);
    }

    // If there are bundle JAR files to install, then register a
    // shutdown hook to make sure the OSGi framework is cleanly
    // shutdown when the VM exits.
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

    // Record any bundle with a Main-Class.
    final List installedList = new ArrayList();

    // Create and start an OSGi framework instance, install
    // all bundle JAR files into it, and then start all bundles.
    Bundle mainBundle = null;
    try {
      // Configure and start the OSGi framework.
      final Map configMap = new HashMap();
      configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
      m_framework = new Felix(configMap);
      m_framework.start();

      // Install bundle JAR files and remember the bundle objects.
      final BundleContext context = m_framework.getBundleContext();
      for (int i = 0; i < bundleList.size(); i++) {
        Bundle b = context.installBundle(((File) bundleList.get(i)).toURI().toString());
        installedList.add(b);
        if (b.getHeaders().get("Main-Class") != null) {
          mainBundle = b;
        }
      }

      // Start all installed bundles.
      for (int i = 0; i < installedList.size(); i++) {
        try {
          ((Bundle) installedList.get(i)).start();
        } catch (BundleException ex) {
          System.err.println("Error starting bundle: " + ex);
          ex.printStackTrace();
        }
      }
    } catch (Exception ex) {
      System.err.println("Error starting framework: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }

    // If a bundle exists with a "Main-Class", then load the class and
    // invoke its static main method.
    if (mainBundle != null) {
      final String mainClassName = (String) mainBundle.getHeaders().get("Main-Class");
      if (mainClassName != null) {
        final Class mainClass = mainBundle.loadClass(mainClassName);
        try {
          Method method = mainClass.getMethod("main", new Class[] { String[].class });
          method.invoke(null, new Object[] { new String[0] });
        } catch (Exception ex) {
          System.err.println("Error invoking main method: " + ex + " cause = " + ex.getCause());
        }
      } else {
        System.err.println("Main class not found: " + mainClassName);
      }
    } else {
      m_framework.waitForStop(0);
      System.exit(0);
    }
  }
}
