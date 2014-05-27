package org.iteam.mina.protocal;

import java.nio.charset.Charset;

public interface JConstant {

	final int PORT = 9999;
	/** 30秒后超时 */
	final int IDELTIMEOUT = 30;
	/** 15秒发送一次心跳包 */
	final int HEARTBEATRATE = 15;
	/** Sets the connect timeout value in milliseconds. */
	final int ConnectTimeoutMillis = 3000;
	/** 发送缓冲区10M */
	final int ReadBufferSize = 2048 * 5000;
	/** 接收缓冲区10M */
	final int ReceiveBufferSize = 2048 * 5000;
	/** 消息协议类型：请求错误 */
	final int TYPE_REEOR = 0x00001;
	/** 字符编码 */
	final Charset charset = Charset.forName("UTF-8");
}
