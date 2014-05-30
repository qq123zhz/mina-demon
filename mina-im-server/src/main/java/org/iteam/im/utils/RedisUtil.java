package org.iteam.im.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis工具类
 * 
 * @author wangwei
 * 
 */
public class RedisUtil {
	private static JedisPool pool;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxActive(500);
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(5);
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWait(1000 * 10);
		// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		pool = new JedisPool(config, JConstant.REDIS_IP, JConstant.REDIS_PORT);
	}

	/**
	 * 获取池化的Jedis
	 * 
	 * @return
	 */
	public static Jedis getResource() {
		return pool.getResource();
	}

	/**
	 * 释放连接池
	 * 
	 * @param jedis
	 */
	public static void returnResource(Jedis jedis) {
		pool.returnResource(jedis);
	}
}
