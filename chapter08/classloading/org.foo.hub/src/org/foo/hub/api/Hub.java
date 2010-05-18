package org.foo.hub.api;

import org.foo.hub.Message;

/**
 * A hub sends messages to its spokes.
 */
public interface Hub {
  int send(Message message);
}
