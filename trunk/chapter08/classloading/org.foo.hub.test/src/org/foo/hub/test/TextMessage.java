package org.foo.hub.test;

import org.foo.hub.Message;

/**
 * Simple text message implementation.
 */
public class TextMessage implements Message {

  String address;

  String contents;

  public TextMessage(String address, String contents) {
    this.address = address;
    this.contents = contents;
  }

  public String getAddress() {
    return address;
  }

  public String getSubject() {
    return "Unknown";
  }

  public String toString() {
    return contents;
  }
}
