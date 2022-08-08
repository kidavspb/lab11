package com.company;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class ServerReceiver extends Receiver {
	private final Service.Destination destination;

	public ServerReceiver(DatagramSocket socket, Service.Console console, Sender sender, Service.Destination destination, ArrayList<String> history) {
		super(socket, console, sender, history);
		this.destination = destination;
	}

	@Override
	protected void finish() {
		destination.set(receivePacket.getAddress(), receivePacket.getPort());
	}
}