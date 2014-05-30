package org.iteam.im.server;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.iteam.im.core.IdsResp;
import org.iteam.im.server.services.BaseService;
import org.iteam.im.utils.IdsUtils;
import org.iteam.im.utils.JConstant;
import org.iteam.im.utils.RedisUtil;
import org.iteam.mina.pool.SessionPool;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.iteam.mina.protocal.JMessageProtocalResponse;
import org.iteam.mina.utils.EUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

/**
 * MINA 服务器消息处理
 * 
 * @author arts
 * 
 */
public class MinaServerHandler extends IoHandlerAdapter {

	private Logger log = LoggerFactory.getLogger(MinaServerHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error(String.format("[" + session.getRemoteAddress()
				+ "]Server产生异常!"));
		log.error(EUtils.getExceptionStack(cause));
		Jedis jedis = RedisUtil.getResource();
		jedis.del(String.valueOf("session_id:" + session.getId()));
		RedisUtil.returnResource(jedis);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug(String.format("来自Client[%s]的消息:%s",
				session.getRemoteAddress(), message.toString()));
		if (message instanceof JMessageProtocalRequest) {
			JMessageProtocalResponse resObject = null;
			JMessageProtocalRequest request = (JMessageProtocalRequest) message;
			try {
				String className = "org.iteam.im.server.services.impl.Service"
						+ IdsUtils.toServer(request.getMethodCode());
				Class<?> clsassType = Class.forName(className);
				Object servce = clsassType.newInstance();
				StringBuffer errorMsg = new StringBuffer();
				boolean validation = (boolean) BaseService.class.getMethod(
						"validation", JMessageProtocalRequest.class,
						StringBuffer.class).invoke(servce, request, errorMsg);
				if (validation) {
					resObject = (JMessageProtocalResponse)BaseService.class.getMethod("exec",
							JMessageProtocalRequest.class, IoSession.class)
							.invoke(servce, request, session);
					resObject.setMethodCode(request.getMethodCode());
					resObject.setVersion(request.getVersion());
				} else {
					JMessageProtocalResponse response = new JMessageProtocalResponse(
							JConstant.CHARSET);
					response.setContent(errorMsg.toString());
					response.setMethodCode(request.getMethodCode());
					response.setResultCode(IdsResp.VALIDATION_ERROR);
					response.setVersion(request.getVersion());
					resObject = response;
				}
			} catch (ClassNotFoundException e) {
				log.error(EUtils.getExceptionStack(e));
				JMessageProtocalResponse response = new JMessageProtocalResponse(
						JConstant.CHARSET);
				response.setContent("未定义的服务");
				response.setMethodCode(request.getMethodCode());
				response.setResultCode(IdsResp.UNDEFINED_SERVICE);
				response.setVersion(request.getVersion());
				resObject = response;
			} catch (Exception e) {
				log.error(EUtils.getExceptionStack(e));
				JMessageProtocalResponse response = new JMessageProtocalResponse(
						JConstant.CHARSET);
				response.setContent(e.getMessage());
				response.setMethodCode(request.getMethodCode());
				response.setResultCode(IdsResp.EXCEPTION);
				response.setVersion(request.getVersion());
				resObject = response;
			}
			if (resObject != null) {
				session.write(resObject);
				log.debug(String.format("回应Client[%s]的消息:%s",
						session.getRemoteAddress(), resObject.toString()));
			}
			// JMessageProtocalResponse response = new JMessageProtocalResponse(
			// JConstant.CHARSET);
			// response.setContent(request.getContent());
			// response.setMethodCode(request.getMethodCode());
			// response.setResultCode(0x11);
			// response.setVersion(request.getVersion());
			// session.write(response);
		} else if (message instanceof JMessageProtocalResponse) {

		} else {

		}
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
		RedisUtil.returnResource(jedis);

		log.debug(String.format("Client[%s]与Server断开连接!" + session.getId(),
				session.getRemoteAddress()));
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Jedis jedis = RedisUtil.getResource();
		jedis.setex(String.valueOf("session_id:" + session.getId()), 30,
				session.getRemoteAddress().toString());
		RedisUtil.returnResource(jedis);

		log.debug(String.format("Client[%s]与Server建立连接!",
				session.getRemoteAddress()));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug(String.format("[" + session.getRemoteAddress()
				+ "]Server进入空闲状态!"));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug(String.format("Client[%s]与Server打开连接!",
				session.getRemoteAddress()));
	}
}
