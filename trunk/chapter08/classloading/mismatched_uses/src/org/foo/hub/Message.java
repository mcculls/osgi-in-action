package org.foo.hub;

/**
 * Extended definition to show how "uses" constraints can detect mis-matched APIs.
 */
public interface Message {

  String getAddress();

  String getSubject();
}
