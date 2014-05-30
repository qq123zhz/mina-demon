package org.iteam.im.core;

public interface IdsResp {
	/** 成功 */
	final int SUCCESS = 0x00;
	/** 未定义的服务 */
	final int UNDEFINED_SERVICE = 0x01;
	/** 发生异常 */
	final int EXCEPTION = 0x02;
	/** 数据验证未通过 */
	final int VALIDATION_ERROR = 0x03;
}
