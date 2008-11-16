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
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Correct code example showing how to use a ServiceTracker to track LogServices.
 * Using a ServiceTracker gives you the power of listeners without the headaches.
 * 
 * To run this example:
 * 
 *   ./code/chapter04/dynamics/DEPLOY 8
 * 
 * Expected result:
 * 
 *   <21> thread="FelixStartLevel", bundle=2 : logging ON
 *   <21> thread="LogService Tester", bundle=2 : <<ping>>
 *   ...
 * 
 * If you stop the simple LogService with "stop 1" you should see the following:
 * 
 *   <21> thread="Felix Shell TUI", bundle=2 : logging OFF
 *   <--> thread="LogService Tester", bundle=2 : LogService has gone
 * 
 * Which shows the client bundle knows the LogService it was using has now gone.
 * 
 * When the LogService is restarted with "start 1" the client will use it again:
 * 
 *   <22> thread="Felix Shell TUI", bundle=2 : logging ON
 *   <22> thread="LogService Tester", bundle=2 : <<ping>>
 *   ...
 * 
 * Note that the new LogService has an increased "service.id" property of 22.
 **/
public class Activator implements BundleActivator {

  // these fields are used to communicate between the bundle and test thread, so must be volatile
  volatile BundleContext m_context;
  volatile ServiceTracker m_logTracker;

  /**
   * START LOG TEST
   **/
  public void start(BundleContext context) {

    // we still need to store the current bundle context in a shared field
    m_context = context;

    // we now use a customized ServiceTracker that watches for LogServices and decorates them
    m_logTracker = new ServiceTracker(context, LogService.class.getName(), new LogServiceDecorator());

    // we must remember to open the ServiceTracker so it can add a listener and start tracking
    m_logTracker.open();

    // start new thread to test LogService - remember to keep bundle activator methods short!
    startTestThread();
  }

  /**
   * STOP LOG TEST
   **/
  public void stop(BundleContext context) {

    // remember to explicitly close down the ServiceTracker so it can clear untracked services
    m_logTracker.close();

    // clear fields when we're done with the LogService, also tells the test thread to finish
    m_logTracker = null;
    m_context = null;

    stopTestThread();
  }

  // Decorate the LogService instances tracked by the ServiceTracker
  class LogServiceDecorator implements ServiceTrackerCustomizer {

    // this method will be called whenever a matching service is registered
    public Object addingService(final ServiceReference reference) {

      return new LogService() {

        // here we simply wrap some code around the original LogService to alter the message
        public void log(int level, String message) {
          ((LogService) m_context.getService(reference)).log(level, "<<" + message + ">>");
        }

        public void log(int level, String message, Throwable exception) {}

        public void log(ServiceReference sr, int level, String message) {}

        public void log(ServiceReference sr, int level, String message, Throwable exception) {}
      };
    }

    // this method will be called whenever some service metadata is modified
    public void modifiedService(ServiceReference reference, Object service) {}

    // this method will be called whenever a matching service is removed
    public void removedService(ServiceReference reference, Object service) {}
  }

  // Test LogService by periodically sending a message
  class LogServiceTest implements Runnable {
    public void run() {

      while (m_context != null) {
        // query the tracker to find the best matching service
        LogService logService = (LogService) m_logTracker.getService();

        // if the service instance is null then we know there is no LogService available
        if (logService != null) {
          try {
            logService.log(LogService.LOG_INFO, "ping");
          } catch (RuntimeException re) {
            alternativeLog("error in LogService " + re);
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

  Thread m_logTestThread;

  void startTestThread() {
    // start separate worker thread to run the actual tests, managed by the bundle lifecycle
    m_logTestThread = new Thread(new LogServiceTest(), "LogService Tester");
    m_logTestThread.setDaemon(true);
    m_logTestThread.start();
  }

  void stopTestThread() {
    // thread should cooperatively shutdown on the next iteration, because field is now null
    m_logTestThread.interrupt();
    m_logTestThread = null;
  }

  void pauseTestThread() {
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
