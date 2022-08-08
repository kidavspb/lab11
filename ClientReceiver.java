package com.company;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class ClientReceiver extends Receiver {

	public ClientReceiver(DatagramSocket socket, Service.Console console, Sender sender, ArrayList<String> history) {
		super(socket, console, sender, history);
	}

	@Override
	protected void finish() {}
}