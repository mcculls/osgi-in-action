package org.foo.hello.modularity;

import org.foo.hello.Greeting;

public class Client {
  static {
    new Greeting().arrive("modularity");
  }
}
