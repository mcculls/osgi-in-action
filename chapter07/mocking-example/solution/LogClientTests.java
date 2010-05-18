package org.foo.mock;

import static org.easymock.EasyMock.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import junit.framework.TestCase;
import org.foo.log.Activator;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

public class LogClientTests
    extends TestCase {

  public void testLogClientBehaviour()
      throws Exception {

    // MOCK - create prototype mock objects
    // ====================================

    // we want a strict mock context so we can test the ordering
    BundleContext context = createStrictMock(BundleContext.class);
    ServiceReference serviceRef = createMock(ServiceReference.class);
    LogService logService = createMock(LogService.class);

    // nice mocks return reasonable defaults
    Bundle bundle = createNiceMock(Bundle.class);

    // EXPECT - script the expected behavior
    // =====================================

    // expected behaviour when log service is available
    expect(context.getServiceReference(LogService.class.getName()))
        .andReturn(serviceRef);
    expect(context.getService(serviceRef))
        .andReturn(logService);
    logService.log(
        and(geq(LogService.LOG_ERROR), leq(LogService.LOG_DEBUG)),
        isA(String.class));

    // expected behaviour when log service is not available
    expect(context.getServiceReference(LogService.class.getName()))
        .andReturn(null);
    expect(context.getBundle())
        .andReturn(bundle).anyTimes();

    // race condition: log service is available but immediately goes away
    expect(context.getServiceReference(LogService.class.getName()))
        .andReturn(serviceRef);
    expect(context.getService(serviceRef))
        .andReturn(null);
    expect(context.getBundle())
        .andReturn(bundle).anyTimes();

    // REPLAY - prepare the mock objects
    // =================================

    replay(context, serviceRef, logService, bundle);

    // TEST - run code using the mock objects
    // ======================================
    
    // this latch limits the calls to the log service
    final CountDownLatch latch = new CountDownLatch(3);

    // override pause method to allow test synchronization
    BundleActivator logClientActivator = new Activator() {
      @Override protected void pauseTestThread() {

        // report log call
        latch.countDown();

        // nothing else left to do?
        if (latch.getCount() == 0) {
          LockSupport.park();
        }
      }
    };

    logClientActivator.start(context);

    // timeout in case test deadlocks
    if (!latch.await(5, TimeUnit.SECONDS)) {
      fail("Still expecting" + latch.getCount() + " calls");
    }

    logClientActivator.stop(context);

    // VERIFY - check the behavior matches
    // ===================================

    verify(context, serviceRef, logService);
  }
}
