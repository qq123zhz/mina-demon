package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * JMessageProtocal编码
 * 
 * @author Simple
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
		// new buf
		IoBuffer buf = IoBuffer.allocate(2048).setAutoExpand(true);
		// object --> AbsMP
		if (object instanceof JMessageProtocalRequest) {// 请求协议
			JMessageProtocalRequest mpReq = (JMessageProtocalRequest) object;
			buf.putInt(mpReq.getVersion());
			buf.putInt(mpReq.getLength());
			buf.putInt(mpReq.getMethodCode());
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
