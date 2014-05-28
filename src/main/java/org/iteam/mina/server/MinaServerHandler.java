package org.iteam.mina.server;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.iteam.mina.pool.SessionPool;
import org.iteam.mina.utils.EUtils;
import org.iteam.mina.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * MINA 服务器消息处理
 * 
 * @author Simple
 * 
 */
public class MinaServerHandler extends IoHandlerAdapter {

	private Logger log = LoggerFactory.getLogger(MinaServerHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error(String.format("Server产生异常!"));
		log.error(EUtils.getExceptionStack(cause));
		Jedis jedis = RedisUtil.getResource();
		jedis.del(String.valueOf("session_id:" + session.getId()));
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug(String.format("来自Client[%s]的消息:%s",
				session.getRemoteAddress(), message.toString()));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		InetSocketAddress address = (InetSocketAddress) session
				.getRemoteAddress();
		log.debug(String.format("向Client[%s]发送消息:%s", address.getAddress(),
				message.toString()));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// session创建以后将 session放入静态变量中
		if (SessionPool.idSessions.containsKey(session.getId())) {
			SessionPool.idSessions.remove(session.getId());
		}
		Jedis jedis = RedisUtil.getResource();
		jedis.del(String.valueOf("session_id:" + session.getId()));
		log.debug(String.format("Client[%s]与Server断开连接!" + session.getId(),
				session.getRemoteAddress()));
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Jedis jedis = RedisUtil.getResource();
		jedis.setex(String.valueOf("session_id:" + session.getId()), 30,
				session.getRemoteAddress().toString());
		log.debug(String.format("Client[%s]与Server建立连接!",
				session.getRemoteAddress()));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug(String.format("Server进入空闲状态!"));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug(String.format("Client[%s]与Server打开连接!",
				session.getRemoteAddress()));
	}
}
