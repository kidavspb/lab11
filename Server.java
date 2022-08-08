package com.company;

public class Server {

	public static void main(String[] args) {
		Service service = new UdpServer(args);
		service.run();
	}
}