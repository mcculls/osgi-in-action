package org.foo.hello.client;

import org.foo.hello.Greeting;

public class Client {
  static {
    Greeting.get().sayHello();
  }
}
