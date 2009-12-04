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
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.log.LogService;

public class WindowListener extends WindowAdapter {

  private BundleContext m_context;
  private AtomicReference<LogService> logRef = new AtomicReference<LogService>();

  protected void bindLog(LogService log) {
    logRef.compareAndSet(null, log);
  }
  
  protected void unbindLog(LogService log) {
    logRef.compareAndSet(log, null);
  }
  
  protected void bindWindow(Window window) {
    log( LogService.LOG_INFO, "Bind window" );
    window.addWindowListener(this);
  }
  
  protected void unbindWindow(Window window) {
    log( LogService.LOG_INFO, "Unbind window" );
    window.removeWindowListener(this);
  }
  
  protected void activate(BundleContext context) {
    m_context = context;
    log( LogService.LOG_INFO, "Activated" );
  }
  
  protected void deactivate() {
    m_context = null;
    log( LogService.LOG_INFO, "Deactivated" );
  }
  
  @Override
  public void windowClosed(WindowEvent evt) {
    try {
      log( LogService.LOG_INFO, "Window closed" );
      m_context.getBundle(0).stop();
    } catch (BundleException e) {
    }
  }
  
  private void log(int level, String msg) {
    LogService log = logRef.get();
    if ( log != null ) {
      log.log(level,msg);
    }
  } 
}
