package org.iteam.mina.client;

import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.iteam.mina.protocal.JMessageProtocalCodecFactory;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.iteam.mina.utils.GUtils;
import org.iteam.mina.utils.JConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA 客户端[异步]
 * 
 * @author arts
 * 
 */
public class MainClient {

	private static Logger log = LoggerFactory.getLogger(MainClient.class);

	private static final int PORT = JConstant.PORT;
	private static final String IP = "192.168.12.31";

	private static ConnectFuture cf = null;

	public static void main(String[] args) {
		NioSocketConnector connector = new NioSocketConnector();

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("keep-alive", new ClientKeepAliveFilterInMina());// 心跳
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(
				new JMessageProtocalCodecFactory(JConstant.CHARSET)));
		connector.setHandler(new MinaClientHandler());
		
		connector.setConnectTimeoutMillis(JConstant.CONNECT_TIMEOUT_MILLIS);
		connector.getSessionConfig().setReadBufferSize(
				JConstant.READ_BUFFER_SIZE);// 发送缓冲区10M
		connector.getSessionConfig().setReceiveBufferSize(
				JConstant.RECEIVE_BUFFER_SIZE);// 接收缓冲区10M
		cf = connector.connect(new InetSocketAddress(IP, PORT));
		
		log.info("等待连接创建完成......");
		cf.awaitUninterruptibly();// 等待连接创建完成
		log.info("连接创建完成-->" + IP + ":" + PORT);
		JMessageProtocalRequest req = new JMessageProtocalRequest(
				JConstant.CHARSET);
		req.setVersion(0x1111000);
		req.setMethodCode(0x10100140);
		req.setUuid(GUtils.UUID());
		req.setContent("hello world!!!");
		log.info("发送数据.....");
		cf.getSession().write(req);
		log.info("发送数据成功.....等待连接断开");
		new Thread(new Runnable() {

			@Override
			public void run() {
				Random random = new Random();
				try {
					int time = random.nextInt(20 * 1000);
					Thread.sleep(time);

					JMessageProtocalRequest req = new JMessageProtocalRequest(JConstant.CHARSET);
					req.setVersion(0x1111000);
					req.setMethodCode(0x10100140);
					req.setUuid(GUtils.UUID());
					req.setContent("hello world!!!" + time);
					log.info("发送数据.....");
					cf.getSession().write(req);
					log.info("发送数据成功.....等待连接断开");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();  
		cf.getSession().getCloseFuture().awaitUninterruptibly();// 等待连接断开
		connector.dispose();
		log.info("连接断开");

	}
}
