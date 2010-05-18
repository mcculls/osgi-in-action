package org.foo.paint;

import javax.swing.SwingUtilities;

public class SwingUtils {
  static void invokeAndWait(Runnable task) {
    if (SwingUtilities.isEventDispatchThread()) {
      task.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(task);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public static void invokeLater(Runnable runnable) {
    SwingUtilities.invokeLater(runnable);
  }

}
