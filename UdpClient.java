package com.company;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClient extends Service {
	private static final int DEFAULT_PORT = 9876;
	private InetAddress DEFAULT_ADDRESS;

	public UdpClient(String[] args) {
		super(args);
		try {
			DEFAULT_ADDRESS = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			close();
		}
	}

	@Override
	protected void setup() throws SocketException, UnknownHostException {
		getDestination();
		socket = new DatagramSocket();
		sender = new Sender(socket, console, destination, history);
		receiver = new ClientReceiver(socket, console, sender, history);
	}

	private void getDestination() throws UnknownHostException {
		if (args == null || args.length != 2) {
			destination.set(DEFAULT_ADDRESS, DEFAULT_PORT);
			return;
		}
		destination.set(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
	}
}