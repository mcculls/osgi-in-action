package org.foo.spoke;

import java.lang.reflect.Method;
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

      Class msgClazz = message.getClass();
      String auditorName = msgClazz.getPackage().getName() + ".Auditor";

      try {

        // Class auditClazz = msgClazz.getClassLoader().loadClass(auditorName);
        Class auditClazz = Class.forName(auditorName);

        Method method = auditClazz.getDeclaredMethod("audit", Spoke.class, Message.class);
        method.invoke(null, this, message);

        return true;

      } catch (Throwable e) {
        e.printStackTrace();
        return false;
      }
    }

    return false;
  }
}
