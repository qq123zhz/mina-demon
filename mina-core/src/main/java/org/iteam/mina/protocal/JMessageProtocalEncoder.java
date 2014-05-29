package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * JMessageProtocal编码
 * 
 * @author arts
 * 
 */
public class JMessageProtocalEncoder extends ProtocolEncoderAdapter {

	private Charset charset;

	public JMessageProtocalEncoder(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 编码
	 */
	public void encode(IoSession session, Object object,
			ProtocolEncoderOutput out) throws Exception {
		// 响应：消息协议版本[4]数据长度[4]功能函数[4] 数据内容[根据数据长度而定]
		// 请求：消息协议版本[4]数据长度[4]功能函数[4]uuid长度[4]uuid[根据uuid长度而定]数据内容[根据数据长度而定]
		// new buf
		IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
		// object --> AbsMP
		if (object instanceof JMessageProtocalRequest) {// 请求协议
			JMessageProtocalRequest mpReq = (JMessageProtocalRequest) object;
			buf.putInt(mpReq.getVersion());
			buf.putInt(mpReq.getLength());
			buf.putInt(mpReq.getMethodCode());
System.out.println( String.format("%1$#1x", mpReq.getMethodCode()));
			if (StringUtils.isNotBlank(mpReq.getUuid())) {
				int uuid_length = mpReq.getUuid().getBytes(charset).length;
				buf.putInt(uuid_length);
				buf.putString(mpReq.getUuid(), charset.newEncoder());
			} else {
				buf.putInt(0);
			}
			buf.putString(mpReq.getContent(), charset.newEncoder());
		} else if (object instanceof JMessageProtocalResponse) {// 响应协议
			JMessageProtocalResponse mpRes = (JMessageProtocalResponse) object;
			buf.putInt(mpRes.getVersion());
			buf.putInt(mpRes.getLength());
			int methodCode = mpRes.getMethodCode() & 0xffffff00 + 0x80000000
					+ mpRes.getResultCode();
			// mpRes.setMethodCode(methodCode);
			buf.putInt(methodCode);
			buf.putString(mpRes.getContent(), charset.newEncoder());
		}
		buf.flip();
		out.write(buf);
	}
}
