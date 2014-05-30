package org.iteam.mina.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonUtils {
	private static final Gson GSON = new Gson();

	public static String toJson(Object src) {
		return GSON.toJson(src);
	}

	public static <T> T fromJson(String json, Class<T> classOfT)
			throws JsonSyntaxException {
		return GSON.fromJson(json, classOfT);
	}
}
