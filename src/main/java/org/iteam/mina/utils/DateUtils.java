package org.iteam.mina.utils;

import java.text.SimpleDateFormat;

public class DateUtils {
	public static String toDateString(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(time);
	}
}
