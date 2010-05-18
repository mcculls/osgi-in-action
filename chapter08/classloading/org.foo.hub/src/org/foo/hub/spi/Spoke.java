package org.foo.hub.spi;

import org.foo.hub.Message;

/**
 * A spoke receives messages from its hub.
 */
public interface Spoke {
  boolean receive(Message message);
}
