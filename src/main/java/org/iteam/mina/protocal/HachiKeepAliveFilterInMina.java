package org.iteam.mina.protocal;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mina 心跳检测
 * 
 * @author arts
 * 
 */
public class HachiKeepAliveFilterInMina extends KeepAliveFilter {
	private static final int INTERVAL = 20;// in seconds
	private static final int TIMEOUT = 10; // in seconds
	private Logger logger = LoggerFactory
			.getLogger(HachiKeepAliveFilterInMina.class);

	public HachiKeepAliveFilterInMina(KeepAliveMessageFactory messageFactory) {
		super(messageFactory, IdleStatus.BOTH_IDLE, new ExceptionHandler(),
				INTERVAL, TIMEOUT);
	}

	public HachiKeepAliveFilterInMina() {
		super(new KeepAliveMessageFactoryImpl(), IdleStatus.BOTH_IDLE,
				new ExceptionHandler(), INTERVAL, TIMEOUT);
		this.setForwardEvent(false); // 此消息不会继续传递，不会被业务层看见
		logger.debug("KeepAliveFilter 启动");
	}
}

class ExceptionHandler implements KeepAliveRequestTimeoutHandler {

	private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	public void keepAliveRequestTimedOut(KeepAliveFilter filter,
			IoSession session) throws Exception {
		// System.out.println("Connection lost, session will be closed");
		logger.debug("连接丢失，会话将关闭");
		session.close(true);
	}
}

/**
 * 继承于KeepAliveMessageFactory，当心跳机制启动的时候，需要该工厂类来判断和定制心跳消息
 * 
 * @author arts
 * 
 */
class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	private Logger logger = LoggerFactory
			.getLogger(KeepAliveMessageFactoryImpl.class);
	private static final byte int_req = -1;
	private static final byte int_rep = -2;
	private static final IoBuffer KAMSG_REQ = IoBuffer
			.wrap(new byte[] { int_req });
	private static final IoBuffer KAMSG_REP = IoBuffer
			.wrap(new byte[] { int_rep });

	public Object getRequest(IoSession session) {
		logger.debug("心跳-getRequest,sessionid:" + session.getId());
		return KAMSG_REQ.duplicate();
	}

	public Object getResponse(IoSession session, Object request) {
		logger.debug("心跳-getResponse,sessionid:" + session.getId());
		return KAMSG_REP.duplicate();
	}

	public boolean isRequest(IoSession session, Object message) {
		if (!(message instanceof IoBuffer))
			return false;
		IoBuffer realMessage = (IoBuffer) message;
		if (realMessage.limit() != 1)
			return false;

		boolean result = (realMessage.get() == int_req);
		realMessage.rewind();
		logger.debug("心跳-isRequest,sessionid:" + session.getId() + ",result:"
				+ result);
		return result;
	}

	public boolean isResponse(IoSession session, Object message) {
		if (!(message instanceof IoBuffer))
			return false;
		IoBuffer realMessage = (IoBuffer) message;
		if (realMessage.limit() != 1)
			return false;

		boolean result = (realMessage.get() == int_rep);
		realMessage.rewind();
		logger.debug("心跳-isResponse,sessionid:" + session.getId() + ",result:"
				+ result);

		return result;
	}
}