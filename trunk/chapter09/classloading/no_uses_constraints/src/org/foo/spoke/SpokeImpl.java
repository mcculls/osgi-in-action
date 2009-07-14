package org.foo.spoke;

import java.util.regex.Pattern;
import org.foo.hub.Message;
import org.foo.hub.spi.Spoke;

public class SpokeImpl implements Spoke {

  String address;

  public SpokeImpl(String address) {
    this.address = address;
  }

  public boolean receive(Message message) {

    if (Pattern.compile(message.getAddress()).matcher(address).matches()) {
      System.out.println("SPOKE " + address + " RECEIVED " + message);
      return true;
    }

    return false;
  }
}
