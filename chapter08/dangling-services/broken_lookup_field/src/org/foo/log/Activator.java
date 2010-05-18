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
package org.foo.log;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;

/**
 * Broken code example showing why you shouldn't store a service instance in a
 * long-lived variable like a field. Firstly you can't tell if the service has
 * been removed, and should not be used anymore. Secondly the strong reference
 * to the instance will stop it from being GC'd until the client is GC'd which
 * could be a long time after the original service bundle is uninstalled.
 **/
public class Activator implements BundleActivator {

  LogService m_logService;

  /**
   * START LOG TEST
   **/
  public void start(BundleContext context) {

    // find a single LogService service - notice the refactor-friendly use of Class.getName()
    ServiceReference logServiceRef = context.getServiceReference(LogService.class.getName());

    // dereference handle to get the service instance and store it in a field (not a good idea)
    m_logService = (LogService) context.getService(logServiceRef);

    // start new thread to test LogService - remember to keep bundle activator methods short!
    startTestThread();
  }

  /**
   * STOP LOG TEST
   **/
  public void stop(BundleContext context) {

    stopTestThread();
  }

  // Test LogService by periodically sending a message
  class LogServiceTest implements Runnable {
    public void run() {

      while (Thread.currentThread() == m_logTestThread) {
        m_logService.log(LogService.LOG_INFO, "ping");
        pauseTestThread();
      }
    }
  }

  //------------------------------------------------------------------------------------------
  //  The rest of this is just support code, not meant to show any particular best practices
  //------------------------------------------------------------------------------------------

  volatile Thread m_logTestThread;

  void startTestThread() {
    // start separate worker thread to run the actual tests, managed by the bundle lifecycle
    m_logTestThread = new Thread(new LogServiceTest(), "LogService Tester");
    m_logTestThread.setDaemon(true);
    m_logTestThread.start();
  }

  void stopTestThread() {
    // thread should cooperatively shutdown on the next iteration, because field is now null
    Thread testThread = m_logTestThread;
    m_logTestThread = null;
    if (testThread != null) {
      testThread.interrupt();
      try {testThread.join();} catch (InterruptedException e) {}
    }
  }

  void pauseTestThread() {
    try {
      // sleep for a bit
      Thread.sleep(5000);
    } catch (InterruptedException e) {}
  }
}
