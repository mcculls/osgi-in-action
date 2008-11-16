package org.foo.hello.service;

import org.foo.hello.Greeting;

public class GreetingImpl implements Greeting {
  public void arrive(Object something) {
    System.out.println("Hello, " + something + "!");
  }

  public void depart(Object something) {
    System.out.println("Goodbye, " + something + ".");
  }
}
