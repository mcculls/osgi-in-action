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
package org.foo.paint;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.foo.shape.SimpleShape;
import org.foo.shape.trapezoid.Trapezoid;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * This class is the launcher for the paint program. It creates an embedded
 * framework instance and deploys shape bundles into it, then creates a
 * paint frame and connects it to the framework instance using a ShapeTracker
 * and the BundleContext of the system bundle.
 */
public class Main {
  private static Framework fwk;
  private static PaintFrame frame = null;
  private static ShapeTracker shapeTracker = null;

  /**
   * The static main method launches the paint program.
   * @param args All command line arguments are ignored.
   * @throws Exception if any errors occur.
   */
  public static void main(String[] args) throws Exception {
    addShutdownHook();
    fwk = createFramework();
    publishTrapezoidService();
    createPaintFrame();
  }

  /**
   * Adds a shutdown hook to the JVM process to cleanly shutdown
   * the embedded framework instance.
   */
  private static void addShutdownHook() {
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
  }

  /**
   * Creates a framework instance using the standard META-INF/services approach.
   * The created instance is configured to start with a clean cache and for the
   * system bundle to export the shape API. All bundles located in the "bundles"
   * directory are installed and started.
   * @return the created framework instance.
   * @throws Exception if any error occurs.
   */
  private static Framework createFramework() throws Exception {
    // Look in the "bundles" directory to create a list
    // of all JAR files to install.
    File[] files = new File("bundles").listFiles();
    Arrays.sort(files);
    List jars = new ArrayList();
    for (int i = 0; i < files.length; i++)
      if (files[i].getName().toLowerCase().endsWith(".jar"))
        jars.add(files[i]);

    try {
      // Create, configure, and start an OSGi framework instance
      // using the ServiceLoader to get a factory.
      List bundleList = new ArrayList();
      Map m = new HashMap();
      m.putAll(System.getProperties());
      m.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
      m.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.foo.shape; version=\"4.0.0\"");
      fwk = getFrameworkFactory().newFramework(m);
      fwk.start();
      // Install bundle JAR files and remember the bundle objects.
      BundleContext ctxt = fwk.getBundleContext();
      for (int i = 0; i < jars.size(); i++) {
        Bundle b = ctxt.installBundle(((File) jars.get(i)).toURI().toString());
        bundleList.add(b);
      }
      // Start all installed bundles.
      for (int i = 0; i < bundleList.size(); i++) {
        ((Bundle) bundleList.get(i)).start();
      }
    } catch (Exception ex) {
      System.err.println("Error starting framework: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }

    return fwk;
  }

  /**
   * Publishes a trapezoid shape service implementation from the host
   * application into the embedded framework instance using the system
   * bundle context.
   */
  private static void publishTrapezoidService() {
    Hashtable dict = new Hashtable();
    dict.put(SimpleShape.NAME_PROPERTY, "Trapezoid");
    dict.put(SimpleShape.ICON_PROPERTY, new ImageIcon(Trapezoid.class.getResource("trapezoid.png")));
    fwk.getBundleContext().registerService(SimpleShape.class.getName(), new Trapezoid(), dict);
  }

  /**
   * Creates and displays the paint program's paint frame. The embedded
   * framework instance and the paint frame are wired together using a
   * ShapeTracker instance and the system bundle's BundleContext.
   * @throws Exception if any errors occur.
   */
  private static void createPaintFrame() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        frame = new PaintFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent evt) {
            try {
              fwk.stop();
              fwk.waitForStop(0);
            } catch (Exception ex) {
              System.err.println("Issue stopping framework: " + ex);
            }
            System.exit(0);
          }
        });
        frame.setVisible(true);

        shapeTracker = new ShapeTracker(fwk.getBundleContext(), frame);
        shapeTracker.open();
      }
    });
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
