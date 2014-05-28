package org.iteam.mina.logback;

import java.net.UnknownHostException;

import org.iteam.mina.utils.DateUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
	private String ip;
	private int port;
	private String dbName = "logback_logs";

	private MongoClient mongoClient;

	@Override
	public void start() {
		super.start();
		try {
			mongoClient = new MongoClient(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void stop() {
		super.stop();
		try {
			mongoClient.close();
		} catch (Exception e) {
		}
	}

	public DB getDB() {
		return mongoClient.getDB(dbName);
	}

	@Override
	protected void append(ILoggingEvent event) {
		DB db = getDB();
		DBCollection dc = db.getCollection("log_" + event.getLevel().levelStr.toLowerCase());
		DBObject jo = new BasicDBObject();
		jo.put("level", event.getLevel().levelStr);
		jo.put("threadName", event.getThreadName());
		jo.put("loggerName", event.getLoggerName());
		jo.put("message", event.getMessage());
		jo.put("create_time", DateUtils.toDateString(event.getTimeStamp()));
		dc.save(jo);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}