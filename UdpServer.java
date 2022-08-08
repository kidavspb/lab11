package com.company;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpServer extends Service {
	private static final int DEFAULT_LISTEN_PORT = 9876;
	private int listenPort;

	public UdpServer(String[] args) {
		super(args);
	}

	@Override
	public void setup() throws SocketException {
		getListenPort();
		socket = new DatagramSocket(listenPort);
		sender = new Sender(socket, console, destination, history);
		receiver = new ServerReceiver(socket, console, sender, destination, history);
	}

	private void getListenPort() {
		if (args == null || args.length != 1) {
			listenPort = DEFAULT_LISTEN_PORT;
			return;
		}
		listenPort = Integer.parseInt(args[0]);
	}
}