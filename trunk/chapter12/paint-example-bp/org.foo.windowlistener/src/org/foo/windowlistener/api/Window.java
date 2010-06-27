package org.foo.windowlistener.api;

import java.awt.event.WindowListener;

public interface Window {

  void addWindowListener(WindowListener listener);

  void removeWindowListener(WindowListener listener);

}
