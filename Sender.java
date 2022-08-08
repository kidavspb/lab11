package com.company;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sender implements Runnable, Closeable {
	private final AtomicBoolean isActive = new AtomicBoolean(true);
	private final DatagramSocket socket;
	private final Service.Console console;
	private final Service.Destination destination;
	private String username = "unitilled";

	ArrayList<String> history;

	public Sender(DatagramSocket socket, Service.Console console, Service.Destination destination, ArrayList<String> history) {
		this.socket = socket;
		this.console = console;
		this.destination = destination;
		this.history = history;
	}

	@Override
	public void run() {
		try {
			while (isActive.get()) {
				parseCommand(console.read());
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	private void parseCommand(String command) throws IOException {
		Scanner scanCommand = new Scanner(command);
		if (scanCommand.hasNext()) {
			String token = scanCommand.next();
			switch (token) {
				case "@name" -> {
					if (scanCommand.hasNext()) {
						username = scanCommand.next();
					}
				}
				case "@quit" -> {
					sendMessage("@quit");
					console.write("---You have left the chat---");
					stop();
				}
				case "@dump" -> {
					if (scanCommand.hasNext()) {
						String filename = scanCommand.next();
						Writer writer = new FileWriter(filename);
						for (String s : history) {
							writer.write(s + "\n");
						}
						writer.close();
					}
				}
				default -> sendMessage(command);
			}
		}
	}

	public synchronized void sendMessage(String message) throws IOException {
		String str = username + ": " + message;
		history.add(str);
		if (destination.isValid()) {
			byte[] sendData = str.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destination.getAddress(), destination.getPort());
			socket.send(sendPacket);
		}
	}

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