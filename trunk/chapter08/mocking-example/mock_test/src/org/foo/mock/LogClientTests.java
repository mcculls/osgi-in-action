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

    // EXPECT - script the expected behavior
    // =====================================

    // REPLAY - prepare the mock objects
    // =================================

    // TEST - run code using the mock objects
    // ======================================

    // VERIFY - check the behavior matches
    // ===================================

  }
}
