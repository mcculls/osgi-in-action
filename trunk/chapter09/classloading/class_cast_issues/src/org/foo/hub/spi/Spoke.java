package org.foo.hub.spi;

import org.foo.hub.Message;

/**
 * Duplicate definition to cause a ClassCastException.
 */
public interface Spoke {
  boolean receive(Message message);
}
