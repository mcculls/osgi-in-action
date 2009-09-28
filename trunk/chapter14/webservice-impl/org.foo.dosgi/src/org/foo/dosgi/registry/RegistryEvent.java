package org.foo.dosgi.registry;

import java.util.EventObject;

public class RegistryEvent extends EventObject {

  public static enum Type {
    ADDED, MODIFIED, REMOVED
  }

  private static final long serialVersionUID = 1L;

  private final RegistryServiceReference ref;
  private final Type type;

  public RegistryEvent(Object source, RegistryServiceReference ref,
      Type type) {
    super(source);
    this.ref = ref;
    this.type = type;
  }

  public RegistryServiceReference getReference() {
    return ref;
  }

  public Type getType() {
    return type;
  }
}
