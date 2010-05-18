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
 * Broken code example showing a client listening for any service events that match
 * the chosen LDAP filter. The client receives events that occur after the listener
 * was added, but doesn't receive events that have already happened. This means the
 * client won't receive any registration events for already registered services.
 * 
 * To run this example:
 * 
 *   ./chapter04/dynamics/PICK_EXAMPLE 5
 * 
 * Expected result:
 * 
 *   <--> thread="LogService Tester", bundle=2 : LogService has gone
 *   ...
 * 
 * The client has not seen the active LogService, because it was already registered
 * before the client started, so the listener didn't receive the registration event.
 * 
 * If you restart the simple LogService with "stop 1" and "start 1" you should see:
 * 
 *   <5> thread="Thread-1", bundle=2 : logging ON
 *   <5> thread="LogService Tester", bundle=2 : ping
 *   ...
 * 
 * Which shows that the client has received the new LogService registration event.
 **/
public class Activator implements BundleActivator {

  BundleContext m_context;

  // this field is is updated by the listener and read by the test thread, so must be volatile
  volatile LogService m_logService;

  /**
   * START LOG TEST
   **/
  public void start(BundleContext context) throws Exception {

    // we still need to store the current bundle context in a shared field
    m_context = context;

    // LDAP filter that matches against any services that implement the LogService interface
    String filter = "(" + Constants.OBJECTCLASS + "=" + LogService.class.getName() + ")";

    // add the new listener for any service events that match the simple LogService filter 
    context.addServiceListener(new LogListener(), filter);

    // start new thread to test LogService - remember to keep bundle activator methods short!
    startTestThread();
  }

  /**
   * STOP LOG TEST
   **/
  public void stop(BundleContext context) {

    // when stopping a bundle the framework automatically removes any listeners it had added,
    // but you can explicitly remove a listener with m_context.removeServiceListener(listener)

    stopTestThread();
  }

  // Simple listener that always uses the latest registered LogService implementation
  class LogListener implements ServiceListener {
    public void serviceChanged(ServiceEvent event) {
      switch (event.getType()) {
        case ServiceEvent.REGISTERED:
          // we know this is still valid as service events are delivered synchronously
          m_logService = (LogService) m_context.getService(event.getServiceReference());
          break;
        case ServiceEvent.MODIFIED:
          // only the service metadata has changed, so no need to do anything here
          break;
        case ServiceEvent.UNREGISTERING:
          // stop using service as it has been removed (can you see a problem here?)
          m_logService = null;
          break;
        default:
          break;
      }
    }
  }

  // Test LogService by periodically sending a message
  class LogServiceTest implements Runnable {
    public void run() {

      while (Thread.currentThread() == m_logTestThread) {

        // if the service instance is null then we know there is no LogService available
        if (m_logService != null) {
          m_logService.log(LogService.LOG_INFO, "ping");
        } else {
          alternativeLog("LogService has gone");
        }

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

  protected void pauseTestThread() {
    try {
      // sleep for a bit
      Thread.sleep(5000);
    } catch (InterruptedException e) {}
  }

  void alternativeLog(String message) {
    // this provides similar style debug logging output for when the LogService disappears
    String tid = "thread=\"" + Thread.currentThread().getName() + "\"";
    String bid = "bundle=" + m_context.getBundle().getBundleId();
    System.out.println("<--> " + tid + ", " + bid + " : " + message);
  }
}
