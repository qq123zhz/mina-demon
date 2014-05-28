package org.iteam.mina.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * IoSession池
 * 
 * @author arts
 * 
 */
public class SessionPool {
	/**
	 * IoSession与ID对应
	 */
	public static Map<Long, IoSession> idSessions = new HashMap<Long, IoSession>();
	/**
	 * IoSessionID与用户名
	 */
	public static Map<String, Long> nameSessions = new HashMap<String, Long>();
}
