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
package org.foo.log.service;

import java.lang.reflect.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

/**
 * Partial implementation of the OSGi LogService API.
 **/
public class Activator implements BundleActivator {

  /**
   * @param context The context of the bundle.
   **/
  public void start(BundleContext context) {
    context.registerService(LogService.class.getName(), new DummyLogServiceFactory(), null);
  }

  /**
   * @param context The context of the bundle.
   **/
  public void stop(BundleContext context) {}

  /**
   * Customize the LogService for each bundle that requests it.
   **/
  static class DummyLogServiceFactory implements ServiceFactory {

    // currently active loggers
    Map loggerMap = new HashMap();

    public Object getService(final Bundle bundle, final ServiceRegistration registration) {

      LogService service = new LogService() {
        public void log(int level, String message) {

          String tid = "thread=\"" + Thread.currentThread().getName() + "\"";
          String bid = "bundle=" + bundle.getBundleId();

          Object sid;
          try {
            sid = registration.getReference().getProperty(Constants.SERVICE_ID);
          } catch (RuntimeException re) {
            sid = "!!"; // this service is no longer valid and shouldn't be used
          }

          System.out.println("<" + sid + "> " + tid + ", " + bid + " : " + message);
        }

        public void log(int level, String message, Throwable exception) {}

        public void log(ServiceReference sr, int level, String message) {}

        public void log(ServiceReference sr, int level, String message, Throwable exception) {}
      };

      loggerMap.put(bundle, service); // this logger can now be used

      service.log(LogService.LOG_INFO, "logging ON");

      /*
       * Instead of the real service, return a proxy that checks to see if the service is still active
       */
      return Proxy.newProxyInstance(LogService.class.getClassLoader(), new Class[] { LogService.class },
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LogService cachedService = (LogService) loggerMap.get(bundle);
            if (cachedService != null) {
              return method.invoke(cachedService, args);
            }
            throw new IllegalStateException("LogService has been deactivated");
          }
        });
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
      ((LogService) service).log(LogService.LOG_INFO, "logging OFF");

      loggerMap.remove(bundle); // this logger shouldn't be used
    }
  }
}
