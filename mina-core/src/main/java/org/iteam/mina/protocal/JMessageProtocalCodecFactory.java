package org.iteam.mina.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * JMessageProtocal解码编码工厂
 * 
 * @author Simple
 * 
 */
public class JMessageProtocalCodecFactory implements ProtocolCodecFactory {

	private final JMessageProtocalDecoder decoder;

	private final JMessageProtocalEncoder encoder;

	public JMessageProtocalCodecFactory(Charset charset) {
		this.decoder = new JMessageProtocalDecoder(charset);
		this.encoder = new JMessageProtocalEncoder(charset);
	}

	public ProtocolDecoder getDecoder(IoSession paramIoSession)
			throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession paramIoSession)
			throws Exception {
		return encoder;
	}
}
