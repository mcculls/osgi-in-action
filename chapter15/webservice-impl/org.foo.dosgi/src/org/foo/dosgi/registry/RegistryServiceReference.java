package org.foo.dosgi.registry;

import java.util.Map;

public interface RegistryServiceReference {
  Object getService();

  String getInterface();

  Map getProperties();

  boolean equals(Object o);

  int hashCode();
}
