package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.iteam.mina.utils.ProtocolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMessageProtocal解码
 * 
 * @author Simple
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
		JMessageProtocal jMessageProtocal = null;
		// 消息协议版本[4]数据长度[4]功能函数[4] 数据内容[根据数据长度而定]
		// 获取协议版本
		int version = buf.getInt();
		// 获取数据长度
		int length = buf.getInt();
		// 获取功能函数
		int methodCode = buf.getInt();
		// 取出协议体
		byte[] bodyData = new byte[length];
		// 获取协议类型
		int type = methodCode & 0xf000000 >> 28;

		buf.get(bodyData);
		// 为解析数据做准备
		// 检测协议
		IoBuffer tempBuf = IoBuffer.allocate(100).setAutoExpand(true);
		// tempBuf.putInt(version);
		tempBuf.putInt(length);
		tempBuf.putInt(methodCode);
		tempBuf.put(bodyData);
		tempBuf.flip();
		if (!canDecode(tempBuf)) {
			// 协议错误
			jMessageProtocal = new JMessageProtocal();
			jMessageProtocal.setResultCode(ProtocolType.TYPE_REEOR);
			// return;
		} else {
			// 协议体buf
			IoBuffer bodyBuf = IoBuffer.allocate(100).setAutoExpand(true);
			bodyBuf.put(bodyData);
			bodyBuf.flip();
			// // 整个协议buf
			// IoBuffer allBuf = IoBuffer.allocate(100).setAutoExpand(true);
			// allBuf.putInt(version);
			// allBuf.putInt(length);
			// allBuf.putInt(methodCode);
			// allBuf.put(bodyData);
			// allBuf.flip();
			//
			if (0x0 <= type && type <= 0x7) {
				JMessageProtocalRequest req = new JMessageProtocalRequest();
				String content = bodyBuf.getString(charset.newDecoder());
				req.setMethodCode(methodCode);
				req.setVersion(version);
				req.setContent(content);
				jMessageProtocal = req;

			} else if (0x8 <= type && type <= 0xf) {
				JMessageProtocalResponse res = new JMessageProtocalResponse();
				// 获取结果类型
				int resultCode = methodCode & 0xff;
				String content = bodyBuf.getString(charset.newDecoder());
				res.setResultCode(resultCode);
				res.setContent(content);
				res.setMethodCode(methodCode);
				res.setVersion(version);
				jMessageProtocal = res;

			} else {
				// 协议错误
				log.error("未定义的Type");
				jMessageProtocal = new JMessageProtocal();
				jMessageProtocal.setResultCode(ProtocolType.TYPE_REEOR);
			}
		}
		out.write(jMessageProtocal);

	}

	/**
	 * 是否可以解码
	 * 
	 * @param buf
	 * @return
	 */
	private boolean canDecode(IoBuffer buf) {
		int protocalHeadLength = 4 * 3;// 协议头长度
		int remaining = buf.remaining();
		if (remaining < protocalHeadLength) {
			log.error("错误，协议不完整，协议头长度小于" + protocalHeadLength);
			return false;
		} else {
			log.debug("协议完整");
			// 获取协议版本
			// int version = buf.getInt();
			// 获取数据长度
			int length = buf.getInt();
			// 获取功能函数
			int methodCode = buf.getInt();
			// 获取协议类型
			int type = methodCode & 0xf000000 >> 28;
			if (0x0 <= type && type <= 0xf) {
				log.debug("type=" + type);
			} else {
				log.error("错误，未定义的Type类型");
				return false;
			}
			if (buf.remaining() < length) {
				log.error("错误，真实协议体长度小于消息头中取得的值");
				return false;
			} else {
				log.debug("真实协议体长度:" + buf.remaining() + " = 消息头中取得的值:"
						+ length);
			}
		}
		return true;
	}
}
