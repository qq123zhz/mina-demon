package org.iteam.im.utils;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoUtils {
	private static final String ip = JConstant.MongoDB_IP;
	private static final int port = JConstant.MongoDB_PORT;
	private static final String dbName = JConstant.MongoDB_DBNAME;

	private static MongoClient mongoClient;

	static {
		try {
			mongoClient = new MongoClient(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public static DB getDB() {
		return mongoClient.getDB(dbName);
	}

}
