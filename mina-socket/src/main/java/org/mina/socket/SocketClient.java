package org.mina.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.iteam.mina.mode.JMessageProtocalRequest;
import org.iteam.mina.utils.JConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClient {
	private static Logger log = LoggerFactory.getLogger(SocketClient.class);
	private Socket socket = null;
	private Timer timer = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;

	private static final byte int_req = -1;
	private static final byte int_rep = -2;
	private static final IoBuffer KAMSG_REQ = IoBuffer
			.wrap(new byte[] { int_req });
	private static final IoBuffer KAMSG_REP = IoBuffer
			.wrap(new byte[] { int_rep });
	private List<IoBuffer> ioBuffers = Collections
			.synchronizedList(new ArrayList<IoBuffer>());

	public static void main(String[] args) {
		new SocketClient().init();
	}

	private void init() {
		try {
			socket = new Socket();
			log.debug("连接服务器：" + JConstant.IP + ":" + JConstant.PORT);
			socket.connect(new InetSocketAddress(JConstant.IP, JConstant.PORT),
					JConstant.CONNECT_TIMEOUT_MILLIS);
			log.debug("服务器连接成功");
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			thread.start();
			log.debug("启动接收数据线程");
			timer = new Timer(false);
			timer.schedule(task, JConstant.KEEP_ALIVE_CLIENT_TIMEOUT * 1000,
					JConstant.KEEP_ALIVE_CLIENT_INTERVAL * 1000);
			log.debug("启动心跳线程");

			sendMsg.start();
			log.debug("启动数据发送线程");

		} catch (Exception e) {
			log.error(EUtils.getExceptionStack(e));
		} finally {

		}
	}

	public synchronized void send(IoBuffer buffer) throws IOException {
		outputStream.write(buffer.array(), 0, buffer.remaining());
		outputStream.flush();
	}

	private Thread sendMsg = new Thread(new Runnable() {

		@Override
		public void run() {
			Random random = new Random();
			do {
				try {
					IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
					JMessageProtocalRequest mpReq = new JMessageProtocalRequest(
							JConstant.CHARSET);
					int time = random.nextInt(20 * 1000);
					mpReq.setVersion(0x1111000);
					mpReq.setMethodCode(0x00100140);
					mpReq.setUuid(GUtils.UUID());
					mpReq.setContent("hello world!!!" + time);
					buf.putInt(mpReq.getLength());

					buf.putInt(mpReq.getVersion());
					buf.putInt(mpReq.getMethodCode());
					int uuid_length = mpReq.getUUIDLength();
					buf.putInt(uuid_length);
					if (uuid_length > 0) {
						buf.putString(mpReq.getUuid(),
								JConstant.CHARSET.newEncoder());
					}
					buf.putString(mpReq.getContent(),
							JConstant.CHARSET.newEncoder());
					buf.flip();
					send(buf);
					log.debug("发送消息：" + mpReq.toString());
					Thread.sleep(time);
				} catch (Exception e) {
					log.error(EUtils.getExceptionStack(e));

				}
			} while (true);
		}
	});
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			try {
				send(KAMSG_REQ);
				log.debug("心跳发送------>：" + int_req);
			} catch (Exception e) {
				log.error(EUtils.getExceptionStack(e));
			}
		}
	};
	private Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			do {
				try {
					int i = 0;
					// 读取报体
					byte[] temp = new byte[4];
					i = inputStream.read(temp, 0, 4);
					// 如果长度为1 表示是 心跳报文
					if (i == 1) {
						byte data = temp[0];
						boolean result = (data == int_req);
						if (result) {
							log.debug("心跳请求接收------>" + data);
							log.debug("回应心跳响应包------>" + int_rep);
							send(KAMSG_REP);
						} else {
							log.debug("心跳接收返回------>" + data);
						}
					} else {
						// 完整报文
						IoBuffer temp_all = IoBuffer.allocate(100)
								.setAutoExpand(true);
						temp_all.put(temp);
						// 数据长度
						IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(
								true);
						buffer.put(temp);
						buffer.flip();
						// 得到 业务报文内容长度
						int length = buffer.getInt();
						byte[] buf = new byte[length];
						i = inputStream.read(buf, 0, length);
						// 数据长度
						IoBuffer body = IoBuffer.allocate(100).setAutoExpand(
								true);
						body.put(buf);
						temp_all.put(buf);
						temp_all.flip();
						body.flip();
						ioBuffers.add(temp_all);
						log.debug("接收服务器消息："
								+ DecodeUtils.decode(temp_all).toString());

					}
				} catch (Exception e) {
					log.error(EUtils.getExceptionStack(e));
					break;
				}
			} while (true);
			System.exit(0);
		}
	});

}
