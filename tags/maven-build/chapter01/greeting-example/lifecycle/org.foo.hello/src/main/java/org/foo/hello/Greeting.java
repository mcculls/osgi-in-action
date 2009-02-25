package org.foo.hello;

public class Greeting {
  static Greeting instance;

  final String m_name;

  Greeting(String name) {
    m_name = name;
  }

  public static Greeting get() {
    return instance;
  }

  public void sayHello() {
    System.out.println("Hello, " + m_name + "!");
  }
}
