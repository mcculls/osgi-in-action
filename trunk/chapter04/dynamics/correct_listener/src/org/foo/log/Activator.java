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

import java.util.SortedSet;
import java.util.TreeSet;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

/**
 * Correct code example showing a client listening for any service events that match
 * the chosen LDAP filter. The client receives events that occur after the listener
 * was added and receives pseudo registration events for already registered services.
 * 
 * To run this example:
 * 
 *   ./chapter04/dynamics/PICK_EXAMPLE 6
 * 
 * Expected result:
 * 
 *   <3> thread="LogService Tester", bundle=2 : logging ON
 *   <3> thread="LogService Tester", bundle=2 : ping
 *   ...
 * 
 * If you stop the simple LogService with "stop 1" you should see the following:
 * 
 *   <3> thread="Thread-1", bundle=2 : logging OFF
 *   <--> thread="LogService Tester", bundle=2 : LogService has gone
 * 
 * Which shows the client bundle knows the LogService it was using has now gone.
 * 
 * When the LogService is restarted with "start 1" the client will use it again:
 * 
 *   <5> thread="LogService Tester", bundle=2 : logging ON
 *   <5> thread="LogService Tester", bundle=2 : ping
 *   ...
 * 
 * Note that the new LogService has an increased "service.id" property of 5.
 **/
public class Activator implements BundleActivator {

  BundleContext m_context;

  LogListener m_logListener;

  /**
   * START LOG TEST
   **/
  public void start(BundleContext context) throws Exception {

    // we still need to store the current bundle context in a shared field
    m_context = context;

    // this time we store our service listener in a field so the test thread can use it later on
    m_logListener = new LogListener();

    // lock the listener before enabling it, to make sure we process the pseudo events first 
    synchronized (m_logListener) {

      // LDAP filter that matches against any services that implement the LogService interface
      String filter = "(" + Constants.OBJECTCLASS + "=" + LogService.class.getName() + ")";

      // add the new listener for any service events that match the simple LogService filter 
      context.addServiceListener(m_logListener, filter);

      // after adding the listener check for any existing services that need pseudo events
      ServiceReference[] refs = context.getServiceReferences(null, filter);
      if (refs != null) {
        for (ServiceReference r : refs) {
          // send pseudo-registration events for each of the already registered LogServices
          m_logListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, r));
        }
      }
    }

    // start new thread to test LogService - remember to keep bundle activator methods short!
    startTestThread();
  }

  /**
   * STOP LOG TEST
   **/
  public void stop(BundleContext context) {

    // when stopping a bundle the framework automatically removes any listeners it had added,
    // but you can explicitly remove a listener with m_context.removeServiceListener(listener)
    m_context.removeServiceListener(m_logListener);

    stopTestThread();
  }

  // Simple listener that always uses the latest registered LogService implementation
  class LogListener implements ServiceListener {

    // the natural ordering of service references is according to service ranking
    SortedSet<ServiceReference> m_logServiceRefs = new TreeSet<ServiceReference>();

    // we must lock the listener before changing the internal state
    public synchronized void serviceChanged(ServiceEvent event) {
      switch (event.getType()) {
        case ServiceEvent.REGISTERED:
          m_logServiceRefs.add(event.getServiceReference());
          break;
        case ServiceEvent.MODIFIED:
          // only the service metadata has changed, so no need to do anything here
          break;
        case ServiceEvent.UNREGISTERING:
          m_logServiceRefs.remove(event.getServiceReference());
          break;
        default:
          break;
      }
    }

    // we must lock the listener before querying the internal state
    public synchronized LogService getLogService() {
      if (m_logServiceRefs.size() > 0) {
        // the last service reference should have the highest ranking
        return (LogService) m_context.getService(m_logServiceRefs.last());
      }
      return null;
    }
  }

  // Test LogService by periodically sending a message
  class LogServiceTest implements Runnable {
    public void run() {

      while (Thread.currentThread() == m_logTestThread) {

        // query the listener to find the best matching service
        LogService logService = m_logListener.getLogService();

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
