package org.foo.hello.client;

import org.foo.hello.Greeting;

public class Client {
  static {
    new Greeting("modularity").sayHello();
  }
}
