package org.iteam.im.utils;

public class IdsUtils {
	public static String toServer(int ids) {
		int temp = (ids & 0x00ffff00) >>> 8;
		return String.format("%04X", temp);
	}
}
