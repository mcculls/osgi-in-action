package org.foo.shell;

public interface Binding {
  public void start();

  public void stop() throws InterruptedException;
}
