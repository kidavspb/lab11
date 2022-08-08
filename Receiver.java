package com.company;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class Receiver implements Runnable, Closeable {
	protected final AtomicBoolean isActive = new AtomicBoolean(true);
	protected final DatagramSocket socket;
	protected final Service.Console console;
	protected final Sender sender;
	protected DatagramPacket receivePacket;

	ArrayList<String> history;

	protected Receiver(DatagramSocket socket, Service.Console console, Sender sender, ArrayList<String> history) {
		this.socket = socket;
		this.console = console;
		this.sender = sender;
		this.history = history;
	}

	@Override
	public void run() {
		try {
			while (isActive.get()) {
				receiveMessage();
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	private void receiveMessage() throws IOException {
		byte[] receiveData = new byte[1024];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			socket.receive(receivePacket);
		} catch (SocketException e) {
			console.write("Socket closed");
			return;
		}

		String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
		parseMessage(message);
		finish();
	}

	private void parseMessage(String message) throws IOException {
		history.add(message);
		String name = message.substring(0, message.indexOf(':') + 2);
		message = message.substring(name.length());

		Scanner scanMessage = new Scanner(message);
		if (scanMessage.hasNext()) {
			String token = scanMessage.next();
			switch (token) {
				case "@quit" -> {
					console.write("---The interlocutor has left the chat---");
					stop();
				}
				default -> console.write(name + message);
			}
		}
	}

	protected abstract void finish();

	public void stop() {
		isActive.set(false);
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
}