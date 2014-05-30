package org.iteam.im.utils;

import java.nio.charset.Charset;

public interface JConstant {
	/** 服务器端口 */
	final int PORT = 9999;
	/** 15秒发送一次心跳包 */
	final int KEEP_ALIVE_SERVER_INTERVAL = 20;// in seconds
	/** 10秒心跳包超时时间 */
	final int KEEP_ALIVE_SERVER_TIMEOUT = 10; // in seconds
	/** 15秒发送一次心跳包 */
	final int KEEP_ALIVE_CLIENT_INTERVAL = 30;// in seconds
	/** 10秒心跳包超时时间 */
	final int KEEP_ALIVE_CLIENT_TIMEOUT = 10; // in seconds
	/** 30秒后超时 */
	final int IDEL_TIMEOUT = 30;
	/** 连接超时时间 单位：毫秒 */
	final int CONNECT_TIMEOUT_MILLIS = 30 * 1000;
	/** 发送缓冲区10M */
	final int READ_BUFFER_SIZE = 2048 * 5000;
	/** 接收缓冲区10M */
	final int RECEIVE_BUFFER_SIZE = 2048 * 5000;
	/** 字符编码 */
	final Charset CHARSET = Charset.forName("UTF-8");
	/** Redis服务器IP */
	final String REDIS_IP = "192.168.12.31";
	/** Redis服务器PORT */
	final int REDIS_PORT = 6379;
	/** MongoDB服务器IP */
	final String MongoDB_IP = "192.168.12.31";
	/** MongoDB服务器PORT */
	final int MongoDB_PORT = 27017;
	/** MongoDB服务器数据库名 */
	final String MongoDB_DBNAME = "IM";
}
