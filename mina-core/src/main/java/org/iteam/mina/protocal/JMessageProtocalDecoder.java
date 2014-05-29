package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMessageProtocal解码
 * 
 * @author arts
 * 
 */
public class JMessageProtocalDecoder extends ProtocolDecoderAdapter {

	private Logger log = LoggerFactory.getLogger(JMessageProtocalDecoder.class);

	private Charset charset;

	public JMessageProtocalDecoder(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 解码
	 */
	public void decode(IoSession session, IoBuffer buf,
			ProtocolDecoderOutput out) throws Exception {
		if (buf.hasRemaining() && buf.prefixedDataAvailable(4)) {
			// 响应：消息协议版本[4]数据长度[4]功能函数[4] 数据内容[根据数据长度而定]
			// 请求：消息协议版本[4]数据长度[4]功能函数[4]uuid长度[4]uuid[根据uuid长度而定]数据内容[根据数据长度而定]
			// 获取协议版本
			int version = buf.getInt();
			System.out.println("version:" + String.format("%1$#1x", version));
			// 获取数据长度
			int length = buf.getInt();
			System.out.println("length:" + length);
			// 获取功能函数
			int methodCode = buf.getInt();
			System.out.println("methodCode:"
					+ String.format("%1$#1x", methodCode));
			// 获取协议类型
			int type = methodCode & 0xf0000000 >> 28;
			// 请求协议
			if (0x0 <= type && type >= 0x7) {
				// 用户UUID
				int uuid_length = buf.getInt();
				String uuid = "";
				if (uuid_length > 0) {
					buf.getString(uuid_length, charset.newDecoder());
				}

				JMessageProtocalRequest request = new JMessageProtocalRequest(
						charset);
				request.setVersion(version);
				request.setMethodCode(methodCode);
				request.setUuid(uuid);
				if (length > 0) {
					byte[] data = new byte[length];
					buf.get(data);
					IoBuffer body = IoBuffer.allocate(100).setAutoExpand(true);
					body.put(data);
					body.flip();
					request.setContent(body.getString(charset.newDecoder()));
				}
				session.write(request);
			}
			// 响应协议
			else if (0x8 <= type && type >= 0xf) {
				JMessageProtocalResponse response = new JMessageProtocalResponse(
						charset);
				response.setVersion(version);
				int resultCode = methodCode & 0xff;
				response.setResultCode(resultCode);
				response.setMethodCode(methodCode);
				if (length > 0) {
					byte[] data = new byte[length];
					buf.get(data);
					IoBuffer body = IoBuffer.allocate(100).setAutoExpand(true);
					body.put(data);
					body.flip();
					response.setContent(body.getString(charset.newDecoder()));
				}
				session.write(response);

			} else {
				log.error("未定义的协议类型");
			}
		}
	}

	// /**
	// * 是否可以解码
	// *
	// * @param buf
	// * @return
	// */
	// private boolean canDecode(IoBuffer buf) {
	// int protocalHeadLength = 4 * 3;// 协议头长度
	// int remaining = buf.remaining();
	// if (remaining < protocalHeadLength) {
	// log.error("错误，协议不完整，协议头长度小于" + protocalHeadLength);
	// return false;
	// } else {
	// log.debug("协议完整");
	// // 获取协议版本
	// // int version = buf.getInt();
	// // 获取数据长度
	// int length = buf.getInt();
	// // 获取功能函数
	// int methodCode = buf.getInt();
	// // 获取协议类型
	// int type = methodCode & 0xf000000 >> 28;
	// if (0x0 <= type && type <= 0xf) {
	// log.debug("type=" + type);
	// } else {
	// log.error("错误，未定义的Type类型");
	// return false;
	// }
	// if (buf.remaining() < length) {
	// log.error("错误，真实协议体长度小于消息头中取得的值");
	// return false;
	// } else {
	// log.debug("真实协议体长度:" + buf.remaining() + " = 消息头中取得的值:"
	// + length);
	// }
	// }
	// return true;
	// }
}
