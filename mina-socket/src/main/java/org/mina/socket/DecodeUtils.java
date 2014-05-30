package org.mina.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.iteam.mina.mode.JMessageProtocalRequest;
import org.iteam.mina.mode.JMessageProtocalResponse;
import org.iteam.mina.utils.JConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecodeUtils {
	static Logger log = LoggerFactory.getLogger(DecodeUtils.class);

	/**
	 * 解码
	 */
	public static Object decode(IoBuffer buf) throws Exception {
		if (buf.hasRemaining() && buf.prefixedDataAvailable(4)) {
			// 响应：数据长度[4]消息协议版本[4]功能函数[4] 数据内容[根据数据长度而定]
			// 请求：数据长度[4]消息协议版本[4]功能函数[4]uuid长度[4]uuid[根据uuid长度而定]数据内容[根据数据长度而定]
			// 获取数据长度
			int length = buf.getInt();
			log.debug("length:" + length);
			// 获取协议版本
			int version = buf.getInt();
			log.debug("version:" + String.format("%1$#1x", version));
			// 获取功能函数
			int methodCode = buf.getInt();
			log.debug("methodCode:" + String.format("%1$#1x", methodCode));
			// 获取协议类型
			int type = (methodCode & 0xf0000000) >>> 28;
			log.debug("type:" + type);

			// 请求协议
			if (0x0 <= type && type <= 0x7) {
				// 用户UUID
				int uuid_length = buf.getInt();
				String uuid = "";
				if (uuid_length > 0) {
					uuid = buf.getString(uuid_length,
							JConstant.CHARSET.newDecoder());
				}

				JMessageProtocalRequest request = new JMessageProtocalRequest(
						JConstant.CHARSET);
				request.setVersion(version);
				request.setMethodCode(methodCode);
				request.setUuid(uuid);
				length = length - 4 * 3 - uuid_length;
				if (length > 0) {
					byte[] data = new byte[length];
					buf.get(data);
					IoBuffer body = IoBuffer.allocate(100).setAutoExpand(true);
					body.put(data);
					body.flip();
					request.setContent(body.getString(JConstant.CHARSET
							.newDecoder()));
				}
				return request;
			}
			// 响应协议
			else if (0x8 <= type && type <= 0xf) {
				JMessageProtocalResponse response = new JMessageProtocalResponse(
						JConstant.CHARSET);
				response.setVersion(version);
				int resultCode = methodCode & 0xff;
				response.setResultCode(resultCode);
				response.setMethodCode(methodCode);
				length = length - 4 * 2;
				if (length > 0) {
					byte[] data = new byte[length];
					buf.get(data);
					IoBuffer body = IoBuffer.allocate(100).setAutoExpand(true);
					body.put(data);
					body.flip();
					response.setContent(body.getString(JConstant.CHARSET
							.newDecoder()));
				}
				return response;

			} else {
				log.error("未定义的协议类型");
			}
		}
		return "";
	}
}
