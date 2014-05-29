package org.mina.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.iteam.mina.mode.JMessageProtocalRequest;
import org.iteam.mina.utils.JConstant;

public class SocketClient {
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
			socket.connect(new InetSocketAddress(JConstant.IP, JConstant.PORT),
					JConstant.CONNECT_TIMEOUT_MILLIS);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			thread.start();
			timer = new Timer(false);
			timer.schedule(task, JConstant.KEEP_ALIVE_CLIENT_TIMEOUT * 1000,
					JConstant.KEEP_ALIVE_CLIENT_INTERVAL * 1000);
			sendMsg.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public synchronized void send(IoBuffer buffer) throws IOException {
		outputStream.write(buffer.array());
		outputStream.flush();
	}

	private Thread sendMsg = new Thread(new Runnable() {

		@Override
		public void run() {
			Random random = new Random();
			do {
				try {
					IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);
					JMessageProtocalRequest mpReq = new JMessageProtocalRequest();
					int time = random.nextInt(20 * 1000);
					mpReq.setVersion(1111000);
					mpReq.setMethodCode(0x00100140);
					mpReq.setUuid(GUtils.UUID());
					mpReq.setContent("hello world!!!" + time);
					buf.putInt(mpReq.getVersion());
					buf.putInt(mpReq.getLength());
					buf.putInt(mpReq.getMethodCode());
					if (StringUtils.isNotBlank(mpReq.getUuid())) {
						int uuid_length = mpReq.getUuid().getBytes(
								JConstant.CHARSET).length;
						buf.putInt(uuid_length);
						buf.putString(mpReq.getUuid(),
								JConstant.CHARSET.newEncoder());
					} else {
						buf.putInt(0);
					}
					buf.putString(mpReq.getContent(),
							JConstant.CHARSET.newEncoder());
					buf=buf.flip();
					System.out.println(buf.limit());
					send(buf);
					System.out.println(DateUtils.toDateString(System
							.currentTimeMillis())
							+ "发送消息：--》"
							+ mpReq.getContent());
					Thread.sleep(time);
				} catch (CharacterCodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (true);
		}
	});
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			try {
				send(KAMSG_REQ);
				System.out.println(DateUtils.toDateString(System
						.currentTimeMillis()) + ":心跳发送------>");
			} catch (IOException e) {
				e.printStackTrace();
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
					byte[] temp = new byte[12];
					i = inputStream.read(temp, 0, 12);
					// 如果长度为1 表示是 心跳报文
					if (i == 1) {
						byte data = temp[0];
						boolean result = (data == int_req);
						if (result) {
							System.out.println(DateUtils.toDateString(System
									.currentTimeMillis())
									+ ":心跳请求接收------>"
									+ data);
							send(KAMSG_REP);
						} else {
							System.out.println(DateUtils.toDateString(System
									.currentTimeMillis())
									+ ":心跳接收返回------>"
									+ data);
						}
					} else {
						// 业务报文
						IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(
								true);
						buffer.put(temp, 0, i);
						// 得到 业务报文内容长度
						int length = buffer.getInt(8);
						byte[] buf = new byte[length];
						i = inputStream.read(buf, 0, length);
						buffer.put(buf, 0, i);
						buffer = buffer.flip();
						ioBuffers.add(buffer);
						System.out.println(buffer.flip().limit());
						System.out.println("read:"
								+ buffer.getString(JConstant.CHARSET
										.newDecoder()));
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			} while (true);
			System.exit(0);
		}
	});

}
