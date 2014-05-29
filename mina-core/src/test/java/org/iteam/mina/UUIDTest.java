package org.iteam.mina;

import java.nio.charset.Charset;
import java.util.UUID;

public class UUIDTest {

	public static void main(String[] args) {
		String uuid=UUID.randomUUID().toString().replaceAll("-", "");
		System.out.println(uuid);
		System.out.println(uuid.length());
		System.out.println(uuid.getBytes(Charset.forName("UTF-8")).length);
	}

}
