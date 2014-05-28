package org.iteam.mina.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.iteam.mina.protocal.HachiKeepAliveFilterInMina;
import org.iteam.mina.protocal.JMessageProtocalCodecFactory;
import org.iteam.mina.utils.JConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA 服务器
 * 
 * @author Simple
 * 
 */
public class MainServer {

	private static Logger log = LoggerFactory.getLogger(MainServer.class);

	public static void main(String[] args) throws Exception {
		SocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors() + 1);// tcp/ip 接收者
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();// 过滤管道
		chain.addLast("keep-alive", new HachiKeepAliveFilterInMina());// 心跳
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(
				new JMessageProtocalCodecFactory(JConstant.CHARSET)));
		chain.addLast("threadPool",
				new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.getSessionConfig().setReadBufferSize(
				JConstant.READ_BUFFER_SIZE);// 发送缓冲区10M
		acceptor.getSessionConfig().setReceiveBufferSize(
				JConstant.RECEIVE_BUFFER_SIZE);// 接收缓冲区10M
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				JConstant.IDEL_TIMEOUT);// 读写通道10s内无操作进入空闲状态
		acceptor.setHandler(new MinaServerHandler());// 设置handler
		acceptor.bind(new InetSocketAddress(JConstant.PORT));// 设置端口
		log.debug(String.format("Server Listing on %s", JConstant.PORT));
	}
}
