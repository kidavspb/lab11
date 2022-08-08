package com.company;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public abstract class Service implements Closeable {
	protected final Console console = new Console();
	protected final Destination destination = new Destination();
	protected final String[] args;
	protected DatagramSocket socket;
	protected Receiver receiver;
	protected Sender sender;
	ArrayList<String> history = new ArrayList<>();

	protected Service(String[] args) {
		this.args = args;
	}

	public void run() {
		try {
			greet();

			setup();

			Thread senderThread = new Thread(sender);
			senderThread.start();

			Thread receiverThread = new Thread(receiver);
			receiverThread.start();

			senderThread.join();

			receiver.stop();
			socket.close();
			receiverThread.join();
			socket.close();
		} catch (SocketException | UnknownHostException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	protected abstract void setup() throws SocketException, UnknownHostException;

	private void greet() {
		console.write("""
				Задать имя пользователя (@name Vasya)
				Послать текстовое сообщение (Hello)
				Выход (@quit)
				Сохранить историю чата (@dump filename)""");
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			console.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Console implements Closeable {
		private final BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));

		String read() throws IOException {
			return buf.readLine();
		}

		void write(String message) {
			System.out.println(message);
		}

		@Override
		public void close() throws IOException {
			buf.close();
		}
	}

	public static class Destination {
		private InetAddress address;
		private int port;
		private boolean isValid;

		public void set(InetAddress address, int port) {
			this.address = address;
			this.port = port;
			isValid = true;
		}

		public InetAddress getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}

		public boolean isValid() {
			return isValid;
		}
	}
}