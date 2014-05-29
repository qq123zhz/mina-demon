package org.mina.socket;

import java.util.UUID;

public class GUtils {
	public static String UUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String UUID(boolean b) {
		if (b)
			return UUID.randomUUID().toString();
		else
			return UUID();
	}

	public static String UUID(String str) {
		if (str == null) {
			return UUID();
		}
		return UUID.randomUUID().toString().replaceAll("-", str);

	}
}
