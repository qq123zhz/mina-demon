package org.iteam.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.iteam.mina.utils.EUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MINA 客户端消息处理
 * 
 * @author Simple
 * 
 */
public class MinaClientHandler extends IoHandlerAdapter {

	private Logger log = LoggerFactory.getLogger(MinaClientHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error(String.format("Client产生异常!"));
		log.error(EUtils.getExceptionStack(cause));
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug(String.format("来自Server[%s]的消息:%s",
				session.getRemoteAddress(), message.toString()));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.debug(String.format("向Server[%s]发送消息:%s",
				session.getRemoteAddress(), message.toString()));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug(String.format("与Server[%s]断开连接!", session.getRemoteAddress()));
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.debug(String.format("与Server[%s]建立连接!", session.getRemoteAddress()));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug(String.format("与Server[%s]打开连接!", session.getRemoteAddress()));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug(String.format("Client进入空闲状态!"));
	}
}
