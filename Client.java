package com.company;

public class Client {

	public static void main(String[] args) {
		Service service = new UdpClient(args);
		service.run();
	}
}