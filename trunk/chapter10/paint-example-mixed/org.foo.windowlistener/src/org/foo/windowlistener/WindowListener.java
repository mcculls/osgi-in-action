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
package org.foo.windowlistener;


import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.log.LogService;

@Component(immediate=true)
public class WindowListener extends WindowAdapter {

  private BundleContext m_context;
  
  @Requires(optional=true)
  private LogService m_log;

  public WindowListener(BundleContext context) {
    m_context = context;
    log( LogService.LOG_INFO, "Created " + this );
  }

  @Bind
  protected void bindWindow(Window window) {
    log( LogService.LOG_INFO, "Bind window" );
    window.addWindowListener(this);
  }
  
  @Unbind
  protected void unbindWindow(Window window) {
    log( LogService.LOG_INFO, "Unbind window" );
    window.removeWindowListener(this);
  }
  
  @Override
  public void windowClosed(WindowEvent evt) {
    log( LogService.LOG_INFO, "Window closed" );
    new Thread() {
      public void run() {
        try {
          m_context.getBundle(0).stop();
        } catch (BundleException e) {
        }
      }
    }.start();
  }
  
  private void log(int level, String msg) {
    m_log.log(level,msg);
  } 
}
