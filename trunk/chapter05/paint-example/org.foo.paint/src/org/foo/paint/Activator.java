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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.osgi.framework.*;

/**
 * The activator of the host application bundle. The activator creates the main
 * application <tt>JFrame</tt> and starts tracking <tt>SimpleShape</tt>
 * services. All activity is performed on the Swing event thread to avoid
 * synchronization and repainting issues. Closing the application window will
 * result in <tt>Bundle.stop()</tt> being called on the system bundle, which
 * will cause the framework to shutdown and the JVM to exit.
 **/
public class Activator implements BundleActivator, Runnable {
  private BundleContext m_context = null;
  private PaintFrame m_frame = null;
  private ShapeTracker m_shapetracker = null;

  /**
   * Displays the applications window and starts service tracking; everything is
   * done on the Swing event thread to avoid synchronization and repainting
   * issues.
   * 
   * @param context The context of the bundle.
   **/
  public void start(BundleContext context) {
    m_context = context;
    if (SwingUtilities.isEventDispatchThread()) {
      run();
    } else {
      try {
        javax.swing.SwingUtilities.invokeAndWait(this);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Stops service tracking and disposes of the application window.
   * 
   * @param context The context of the bundle.
   **/
  public void stop(BundleContext context) {
    m_shapetracker.close();
    final PaintFrame frame = m_frame;
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        frame.setVisible(false);
        frame.dispose();
      }
    });
  }

  /**
   * This method actually performs the creation of the application window. It is
   * intended to be called by the Swing event thread and should not be called
   * directly.
   **/
  public void run() {
    m_frame = new PaintFrame();

    m_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    m_frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        try {
          m_context.getBundle(0).stop();
        } catch (BundleException ex) {
          ex.printStackTrace();
        }
      }
    });

    m_frame.setVisible(true);

    m_shapetracker = new ShapeTracker(m_context, m_frame);
    m_shapetracker.open();
  }
}
