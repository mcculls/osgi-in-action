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
 * Correct code example showing lookup of a service just before it will be used.
 * The client can tell when the service is removed, and automatically switches
 * to the current top-ranked LogService implementation. It copes with services
 * disappearing at any time and remembers to "unget" the service when it's not
 * being used, like when the test is sleeping.
 * 
 * To run this example:
 * 
 *   ./chapter04/dynamics/PICK_EXAMPLE 4
 * 
 * Expected result:
 * 
 *   <3> thread="LogService Tester", bundle=2 : logging ON
 *   <3> thread="LogService Tester", bundle=2 : ping
 *   <3> thread="LogService Tester", bundle=2 : logging OFF
 *   ...
 * 
 * Note the "unget" call tells the service that we're not using it when sleeping.
 * 
 * If you stop the simple LogService with "stop 1" you should see the following:
 * 
 *   <--> thread="LogService Tester", bundle=2 : LogService has gone
 *   ...
 * 
 * Which shows the client bundle knows the LogService it was using has now gone.
 * 
 * When the LogService is restarted with "start 1" the client will use it again:
 * 
 *   <5> thread="LogService Tester", bundle=2 : logging ON
 *   <5> thread="LogService Tester", bundle=2 : ping
 *   <5> thread="LogService Tester", bundle=2 : logging OFF
 *   ...
 * 
 * Note that the new LogService has an increased "service.id" property of 5.
 **/
public class Activator implements BundleActivator {

  BundleContext m_context;

  /**
   * START LOG TEST
   **/
  public void start(BundleContext context) {

    // this time we just store the current bundle context in the shared field
    m_context = context;

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

        // lookup the current "best" LogService each time, just before we need to use it
        ServiceReference logServiceRef = m_context.getServiceReference(LogService.class.getName());

        // if the service reference is null then we know there's no log service available
        if (logServiceRef != null) {
          try {

            LogService logService = (LogService) m_context.getService(logServiceRef);

            // if the dereferenced instance is null then we know the service has been removed
            if (logService != null) {
              logService.log(LogService.LOG_INFO, "ping");
            } else {
              alternativeLog("LogService has gone");
            }

          } catch (RuntimeException re) {

            // any problems that occur when using or accessing the service are now reported
            alternativeLog("error in LogService " + re);

          } finally {

            // we should also remember to unget a service when we're done with it for a while
            m_context.ungetService(logServiceRef);

          }
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
