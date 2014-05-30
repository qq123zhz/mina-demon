package org.iteam.im.server.services;

import java.io.Serializable;

import org.apache.mina.core.session.IoSession;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.iteam.mina.protocal.JMessageProtocalResponse;

public interface BaseService extends Serializable {
	/**
	 * * 数据验证[第一步]
	 * 
	 * @param request
	 * @return
	 */
	public boolean validation(JMessageProtocalRequest request,
			StringBuffer errorMsg);

	/**
	 * 具体执行[第二步]
	 * 
	 * @param request
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public JMessageProtocalResponse exec(JMessageProtocalRequest request,
			IoSession session) throws Exception;
}
