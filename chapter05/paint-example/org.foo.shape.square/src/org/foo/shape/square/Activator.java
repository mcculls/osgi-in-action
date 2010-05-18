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
package org.foo.shape.square;

import java.util.Hashtable;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.foo.shape.SimpleShape;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This class implements a simple bundle activator for the square
 * <tt>SimpleShape</tt> service. This activator simply creates an instance of
 * the square service object and registers it with the service registry along
 * with the service properties indicating the service's name and icon.
 **/
public class Activator implements BundleActivator {
  public static final String SQUARE_NAME = "SQUARE_NAME";

  /**
   * Implements the <tt>BundleActivator.start()</tt> method, which registers the
   * square <tt>SimpleShape</tt> service.
   * 
   * @param context The context for the bundle.
   **/
  public void start(BundleContext context) {
    ResourceBundle rb = ResourceBundle.getBundle(
      "org.foo.shape.square.resource.Localize");
    Hashtable dict = new Hashtable();
    dict.put(SimpleShape.NAME_PROPERTY, rb.getString(SQUARE_NAME));
    dict.put(SimpleShape.ICON_PROPERTY, new ImageIcon(this.getClass().getResource("square.png")));
    context.registerService(SimpleShape.class.getName(), new Square(), dict);
  }

  /**
   * Implements the <tt>BundleActivator.start()</tt> method, which does nothing.
   * 
   * @param context The context for the bundle.
   **/
  public void stop(BundleContext context) {}
}
