package org.foo.hub.spi;

import org.foo.hub.Message;

public interface Spoke {

  boolean receive(Message message);
}
