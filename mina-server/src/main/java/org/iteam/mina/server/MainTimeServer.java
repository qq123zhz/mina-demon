package org.iteam.mina.server;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.iteam.mina.pool.SessionPool;
import org.iteam.mina.protocal.JMessageProtocalCodecFactory;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.iteam.mina.utils.GUtils;
import org.iteam.mina.utils.JConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA 服务器[定时向客户端发送消息]
 * 
 * @author Simple
 * 
 */
public class MainTimeServer {

	private static Logger log = LoggerFactory.getLogger(MainTimeServer.class);

	public static void main(String[] args) throws Exception {
		SocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors() + 1);// tcp/ip 接收者
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();// 过滤管道
		chain.addLast("keep-alive", new ServerKeepAliveFilterInMina());// 心跳
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(
				new JMessageProtocalCodecFactory(JConstant.CHARSET)));
		chain.addLast("threadPool",
				new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.getSessionConfig().setReadBufferSize(
				JConstant.READ_BUFFER_SIZE);// 发送缓冲区10M
		acceptor.getSessionConfig().setReceiveBufferSize(
				JConstant.RECEIVE_BUFFER_SIZE);// 接收缓冲区10M
		// acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,JConstant.IDEL_TIMEOUT);//
		// 读写通道10s内无操作进入空闲状态
		acceptor.setReuseAddress(true);
		acceptor.setHandler(new MinaTimeServerHandler());// 设置handler
		acceptor.bind(new InetSocketAddress(JConstant.PORT));// 设置端口
		log.debug(String.format("Server Listing on %s", JConstant.PORT));
		timer = new Timer();
		timer.schedule(timerTask, 10 * 1000, 10 * 1000);
		log.debug("定时任务启动");

	}

	private static Timer timer = new Timer();
	private static TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			int time = new Random().nextInt(20 * 1000);
			JMessageProtocalRequest req = new JMessageProtocalRequest(
					JConstant.CHARSET);
			req.setVersion(0x2111000);
			req.setMethodCode(0x20100140);
			req.setUuid(GUtils.UUID());
			for (long key : SessionPool.idSessions.keySet()) {
				IoSession session = SessionPool.idSessions.get(key);
				if (session.isConnected()) {
					req.setContent("我是服务器，我主动发送消息给你" + time);
					session.write(req);
					log.debug("定时任务向Client[" + session.getRemoteAddress()
							+ "]发送消息:" + req.toString());

				}
			}
		}
	};

}
