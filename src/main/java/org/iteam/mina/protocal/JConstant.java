package org.iteam.mina.protocal;

import java.nio.charset.Charset;

public interface JConstant {
	/** 消息协议类型：请求错误 */
	final int TYPE_REEOR = 0x00001;
	/** 字符编码 */
	final Charset charset = Charset.forName("UTF-8");
}
