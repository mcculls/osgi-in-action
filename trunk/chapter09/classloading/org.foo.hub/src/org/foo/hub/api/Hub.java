package org.foo.hub.api;

import org.foo.hub.Message;

public interface Hub {

  int send(Message message);
}
