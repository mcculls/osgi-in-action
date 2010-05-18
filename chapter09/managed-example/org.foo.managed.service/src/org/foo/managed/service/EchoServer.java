package org.foo.managed.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {
	private Thread m_current = null;
	private ServerSocket m_socket = null;
	private final int m_port;

	EchoServer(int port) {
		m_port = port;
	}

	synchronized void start() throws IOException {
		if (m_current != null) {
			return;
		}
		m_socket = new ServerSocket(m_port);

		m_current = new Thread(new Runnable() {
			public void run() {
				final List<Socket> sockets = new ArrayList<Socket>();
				while (!Thread.interrupted()) {
					try {
						final Socket socket = m_socket.accept();
						synchronized (sockets) {
							sockets.add(socket);
						}
						new Thread(new Runnable() {
							public void run() {
								try {
									BufferedReader input = new BufferedReader(
											new InputStreamReader(socket
													.getInputStream()));
									PrintStream output = new PrintStream(socket
											.getOutputStream());
									for (String in = input.readLine(); in != null; in = input
											.readLine()) {
										output.println(in);
										output.flush();
									}
								} catch (IOException ex) {

								}
								synchronized (sockets) {
									sockets.remove(socket);
								}
								try {
									socket.close();
								} catch (IOException ex) {

								}
							}
						}).start();
					} catch (IOException ex) {

					}
				}
				synchronized (sockets) {
					for (Socket socket : sockets) {
						try {
							socket.close();
						} catch (IOException e) {
						}
					}
				}
			}
		});
		m_current.start();
	}

	synchronized void stop() {
		if (m_current != null) {
			m_current.interrupt();
			try {
				m_socket.close();
			} catch (IOException e) {
			}
			try {
				m_current.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			m_current = null;
		}
	}
}
