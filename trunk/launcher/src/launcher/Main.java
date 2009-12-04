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

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.framework.launch.*;

public class Main {
  private static Framework fwk;

  public static void main(String[] args) throws Exception {
    // Must specify a bundle directory.
    if (args.length < 1 || !new File(args[0]).isDirectory()) {
      System.out.println("Usage: <bundle-directory>");
    } else {
      // Look in the specified bundle directory to create a list
      // of all JAR files to install.
      File[] files = new File(args[0]).listFiles();
      Arrays.sort(files);
      List jars = new ArrayList();
      for (int i = 0; i < files.length; i++)
        if (files[i].getName().endsWith(".jar"))
          jars.add(files[i]);

      // If no bundle JAR files are in the directory, then exit.
      if (jars.isEmpty()) {
        System.out.println("No bundles to install.");
      } else {
        // If there are bundle JAR files to install, then register a
        // shutdown hook to make sure the OSGi framework is cleanly
        // shutdown when the VM exits.
        Runtime.getRuntime().addShutdownHook(new Thread() {
          public void run() {
            try {
              if (fwk != null) {
                fwk.stop();
                fwk.waitForStop(0);
              }
            } catch (Exception ex) {
              System.err.println("Error stopping framework: " + ex);
            }
          }
        });

        // Record any bundle with a Main-Class.
        Bundle mainBundle = null;

        try {
          // Create, configure, and start an OSGi framework instance
          // using the ServiceLoader to get a factory.
          List bundleList = new ArrayList();
          Map m = new HashMap();
          m.putAll(System.getProperties());
          m.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
          fwk = getFrameworkFactory().newFramework(m);
          fwk.start();

          // Install bundle JAR files and remember the bundle objects.
          BundleContext ctxt = fwk.getBundleContext();
          for (int i = 0; i < jars.size(); i++) {
            Bundle b = ctxt.installBundle(((File) jars.get(i)).toURI().toString());
            bundleList.add(b);
            // Remember "main" bundle.
            if (b.getHeaders().get("Main-Class") != null) {
              mainBundle = b;
            }
          }

          // Start all installed non-fragment bundles.
          for (int i = 0; i < bundleList.size(); i++) {
            if (!isFragment((Bundle) bundleList.get(i))) {
              ((Bundle) bundleList.get(i)).start();
            }
          }

          // If a bundle exists with a "Main-Class", then load the class and
          // invoke its static main method.
          if (mainBundle != null) {
            final String mainClassName = (String) mainBundle.getHeaders().get("Main-Class");
            if (mainClassName != null) {
              final Class mainClass = mainBundle.loadClass(mainClassName);
              try {
                Method method = mainClass.getMethod("main", new Class[] { String[].class });
                String[] mainArgs = new String[args.length-1];
                System.arraycopy(args, 1, mainArgs, 0, mainArgs.length);
                method.invoke(null, new Object[] { mainArgs });
              } catch (Exception ex) {
                System.err.println("Error invoking main method: " + ex + " cause = " + ex.getCause());
              }
            } else {
              System.err.println("Main class not found: " + mainClassName);
            }
          }

          // Wait for framework to stop.
          fwk.waitForStop(0);
          System.exit(0);

        } catch (Exception ex) {
          System.err.println("Error starting framework: " + ex);
          ex.printStackTrace();
          System.exit(0);
        }
      }
    }
  }

  private static boolean isFragment(Bundle bundle) {
    return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
  }


  /**
   * Simple method to parse META-INF/services file for framework factory.
   * Currently, it assumes the first non-commented line is the class name
   * of the framework factory implementation.
   * @return The created <tt>FrameworkFactory</tt> instance.
   * @throws Exception if any errors occur.
  **/
  private static FrameworkFactory getFrameworkFactory() throws Exception {
    URL url = Main.class.getClassLoader().getResource(
      "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
    if (url != null) {
      BufferedReader br =
        new BufferedReader(new InputStreamReader(url.openStream()));
      try {
        for (String s = br.readLine(); s != null; s = br.readLine()) {
          s = s.trim();
          // Try to load first non-empty, non-commented line.
          if ((s.length() > 0) && (s.charAt(0) != '#')) {
            return (FrameworkFactory) Class.forName(s).newInstance();
          }
        }
      } finally {
        if (br != null) br.close();
      }
    }

    throw new Exception("Could not find framework factory.");
  }
}
