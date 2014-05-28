package org.iteam.mina.protocal;

import java.nio.charset.Charset;

public interface JConstant {

	final int PORT = 9999;
	/** 15秒发送一次心跳包 */
	final int KEEP_ALIVE_INTERVAL = 25;// in seconds
	/** 10秒心跳包超时时间 */
	final int KEEP_ALIVE_TIMEOUT = 10; // in seconds
	/** 30秒后超时 */
	final int IDEL_TIMEOUT = 30;
	/** Sets the connect timeout value in milliseconds. */
	final int CONNECT_TIMEOUT_MILLIS = 3000;
	/** 发送缓冲区10M */
	final int READ_BUFFER_SIZE = 2048 * 5000;
	/** 接收缓冲区10M */
	final int RECEIVE_BUFFER_SIZE = 2048 * 5000;
	/** 消息协议类型：请求错误 */
	final int TYPE_REEOR = 0x00001;
	/** 字符编码 */
	final Charset CHARSET = Charset.forName("UTF-8");
}
