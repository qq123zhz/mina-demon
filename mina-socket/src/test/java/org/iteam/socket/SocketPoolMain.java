package org.iteam.socket;

import org.mina.socket.SocketClient;

public class SocketPoolMain {

	public static void main(String[] args) {
		for (int i = 0; i < 900; i++) {
				SocketClient.main(args);
		}
	}

}
