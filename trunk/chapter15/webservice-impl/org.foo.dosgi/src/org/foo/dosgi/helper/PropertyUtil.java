package org.foo.dosgi.helper;

public class PropertyUtil {
  public static String[] toStringArray(Object val) {
    if (val == null) {
      return null;
    } else if (val instanceof String) {
      String str = (String) val;
      return new String[] { str };
    } else if (val instanceof String[]) {
      return (String[]) val;
    } else {
      throw new IllegalArgumentException("Invalid property type "
          + val.getClass());
    }
  }
}
