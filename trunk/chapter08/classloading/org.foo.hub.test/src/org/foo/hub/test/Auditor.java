package org.foo.hub.test;

import java.util.Date;
import org.foo.hub.Message;
import org.foo.hub.spi.Spoke;

/**
 * Quick'n'dirty utility class, used to demonstrate reflection issues in OSGi.
 */
public class Auditor {
  public static void audit(Spoke spoke, Message message) {
    System.out.println(new Date() + " - " + spoke + " RECEIVED " + message);
  }
}
