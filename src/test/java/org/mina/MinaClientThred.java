package org.mina;

import org.iteam.mina.client.MainClient;

public class MinaClientThred {

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					MainClient.main(null);
				}
			}).start();
		}

	}

}
