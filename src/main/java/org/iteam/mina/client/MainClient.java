package org.iteam.mina.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.iteam.mina.protocal.JConstant;
import org.iteam.mina.protocal.JMessageProtocalCodecFactory;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA 客户端
 * 
 * @author Simple
 * 
 */
public class MainClient {

	private static Logger log = LoggerFactory.getLogger(MainClient.class);

	private static final int PORT = 9999;
	private static final String IP = "127.0.0.1";

	public static void main(String[] args) {
		NioSocketConnector connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(
				new JMessageProtocalCodecFactory(JConstant.charset)));
		connector.setHandler(new MinaClientHandler());
		connector.setConnectTimeoutMillis(3000);
		ConnectFuture cf = connector.connect(new InetSocketAddress(IP, PORT));
		log.info("等待连接创建完成......");
		cf.awaitUninterruptibly();// 等待连接创建完成
		log.info("连接创建完成-->" + IP + ":" + PORT);
		JMessageProtocalRequest req = new JMessageProtocalRequest();
		req.setVersion(1000);
		req.setMethodCode(0x00100140);
		req.setContent("hello world!!!");
		log.info("发送数据.....");
		cf.getSession().write(req);
		log.info("发送数据成功.....等待连接断开");
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
		log.info("连接断开");

	}
}
