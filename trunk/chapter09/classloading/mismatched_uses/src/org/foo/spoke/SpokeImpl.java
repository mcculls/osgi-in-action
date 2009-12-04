package org.foo.spoke;

import org.foo.hub.Message;
import org.foo.hub.spi.Spoke;

/**
 * Simple spoke that just echoes matching messages to the console.
 */
public class SpokeImpl implements Spoke {

  String address;

  public SpokeImpl(String address) {
    this.address = address;
  }

  public boolean receive(Message message) {

    if (address.matches(message.getAddress())) {
      System.out.println("SPOKE " + address + " RECEIVED " + message);
      return true;
    }

    return false;
  }
}
