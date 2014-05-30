package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMessageProtocal编码
 * 
 * @author arts
 * 
 */
public class JMessageProtocalEncoder extends ProtocolEncoderAdapter {
	private Logger log = LoggerFactory.getLogger(JMessageProtocalEncoder.class);

	private Charset charset;

	public JMessageProtocalEncoder(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 编码
	 */
	public void encode(IoSession session, Object object,
			ProtocolEncoderOutput out) throws Exception {
		if (object != null && session.isConnected()) {
			// 响应：消息协议版本[4]数据长度[4]功能函数[4] 数据内容[根据数据长度而定]
			// 请求：消息协议版本[4]数据长度[4]功能函数[4]uuid长度[4]uuid[根据uuid长度而定]数据内容[根据数据长度而定]
			// new buf
			IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
			// object --> AbsMP
			if (object instanceof JMessageProtocalRequest) {// 请求协议
				JMessageProtocalRequest mpReq = (JMessageProtocalRequest) object;
				buf.putInt(mpReq.getLength());
				buf.putInt(mpReq.getVersion());
				buf.putInt(mpReq.getMethodCode());
				log.debug(String.format("%1$#1x", mpReq.getMethodCode()));
				int uuid_length = mpReq.getUUIDLength();
				buf.putInt(uuid_length);
				if (uuid_length > 0) {
					buf.putString(mpReq.getUuid(), charset.newEncoder());
				}
				buf.putString(mpReq.getContent(), charset.newEncoder());
			} else if (object instanceof JMessageProtocalResponse) {// 响应协议
				JMessageProtocalResponse mpRes = (JMessageProtocalResponse) object;
				buf.putInt(mpRes.getLength());
				buf.putInt(mpRes.getVersion());

				log.debug("methodCode-->:"
						+ String.format("%1$#1x", mpRes.getMethodCode()));
				int methodCode = (mpRes.getMethodCode() & 0xffffff00)
						+ 0x80000000 + mpRes.getResultCode();
				buf.putInt(methodCode);

				log.debug("methodCode-->:"
						+ String.format("%1$#1x", methodCode));
				buf.putString(mpRes.getContent(), charset.newEncoder());
			}
			buf.flip();
			out.write(buf);
		}
	}
}
