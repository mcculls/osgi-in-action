package org.foo.shell;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TelnetBinding implements Runnable, Binding {
  private final ServerSocket m_socket;
  private final Command m_command;
  private final int m_maxConnections;
  private final List<StoppableShellThread> m_threads = new ArrayList<StoppableShellThread>();
  private Thread m_thread = new Thread(this);

  public TelnetBinding(Command command, ServerSocket socket, int maxConnections) {
    m_command = command;
    m_socket = socket;
    m_maxConnections = maxConnections;
  }

  public void run() {
    while (!Thread.interrupted()) {
      final Socket socket;
      try {
        socket = m_socket.accept();
      } catch (IOException ex) {
        if (Thread.interrupted()) {
          break;
        }
        throw new RuntimeException(ex);
      }

      synchronized (m_threads) {
        if (m_threads.size() >= m_maxConnections) {
          try {
            socket.close();
          } catch (IOException e) {
            // Ignore
          }
          continue;
        }
      }

      Shell shell;

      try {
        shell = new Shell(m_command, new BufferedReader(new InputStreamReader(socket.getInputStream())),
          new PrintStream(socket.getOutputStream()), new PrintStream(socket.getOutputStream()));
      } catch (IOException e) {
        e.printStackTrace();
        try {
          socket.close();
        } catch (IOException ex) {
          // Ignore
        }
        continue;
      }

      StoppableShellThread thread = new StoppableShellThread(shell, socket);

      thread.start();
    }

    List<StoppableShellThread> threads;

    synchronized (m_threads) {
      threads = new ArrayList<StoppableShellThread>(m_threads);
    }

    for (StoppableShellThread thread : threads) {
      try {
        thread.stopAndWait();
      } catch (InterruptedException e) {
        // Ignore
      }
    }
    Thread.currentThread().interrupt();
  }

  public void start() {
    m_thread.start();
  }

  public void stop() throws InterruptedException {
    m_thread.interrupt();
    try {
      m_socket.close();
    } catch (IOException e) {
      // Ignore
    }
    m_thread.join();
  }

  private final class StoppableShellThread extends Thread {
    private final Socket m_socket;

    public StoppableShellThread(Shell shell, Socket socket) {
      super(shell);
      m_socket = socket;
    }

    public void start() {
      synchronized (m_threads) {
        m_threads.add(this);
      }

      super.start();
    }

    void stopAndWait() throws InterruptedException {
      if (!isAlive()) {
        return;
      }
      interrupt();
      try {
        m_socket.close();
      } catch (IOException e) {
        // Ignore
      }
      join();
    }

    public void run() {
      try {
        super.run();
      } finally {
        synchronized (m_threads) {
          m_threads.remove(this);
        }
      }
    }
  }
}
