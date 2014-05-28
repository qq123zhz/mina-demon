package org.iteam.mina.protocal;

/**
 * 消息协议-响应/请求
 * 
 * <pre>
 * 报头： 
 *    int 		version：消息协议版本    
 *    int   	length：数据长度 
 *    int   	methodCode：功能函数【0x00000001 的返回值  0x80000001】
 * 报体： 
 *    String  	content：数据内容
 * 报文格式：
 *  消息协议版本[4]数据长度[4]功能函数[4] 数据内容[根据数据长度而定]
 * 
 * 功能函数定义：
 *  1位：指令应答标志位 
 *  		0~7：表示发送的指令，正数  
 * 			8~F：表示指令应答消息，负数
 *  1位：协议安全方式 
 *  		0-8：不加密
 * 			1-9：对称加密
 * 			2-A：非对称加密
 * 			3-B：SSL安全通道
 * 			4-C：备用
 * 			5-D：备用
 * 			6-E：备用
 * 			7-F：备用
 *  2位：加密校验方式位
 *  	0：不加密，不校验，全明文方式			（0000=0）
 * 		1：不加密，通用校验				（0001=1）
 * 		2：不加密，动态校验				（0010=2）
 * 		3：备用
 * 		4：对称通用加密，不校验				（0100=4）
 * 		5：对称通用加密，通用校验			（0101=5）
 * 		6：对称通用加密，动态校验			（0110=6）
 * 		7：备用
 * 		8：对称动态加密，不校验				（1000=8）
 * 		9：对称动态加密，通用校验			（1001=9）
 * 		A：对称动态加密，动态校验			（1010=A）
 * 		B：备用
 * 		C：非对称通用加密，不签名校验			（1100-C）
 * 		D：非对称通用加密，签名校验			（1101-D）
 * 		E：非对称动态加密，不签名校验			（1110-E）
 * 		F：非对称动态加密，签名校验			（1111-F）
 *  3-4位：业务分类,参考分类码定义。
 *  5-6位：业务指令码,参考指令码定义。
 *  7-8位：发送分类
 *  		7位：客户端类型
 * 				1：Android
 * 				2：IOS
 * 				3：WinPhone
 * 				4:Windows
 * 				5:Liunx
 * 			8位：访问服务器类型
 * 	7-8位：返回状态位
 * </pre>
 * 
 * @author arts
 * 
 */
public class JMessageProtocal {
	/** 协议状态 */
	protected int resultCode;// 结果码

	public int getResultCode() {
		return resultCode;
	}

	/**
	 * 报头长度
	 * 
	 * @return
	 */
	public int getTitleLength() {
		return 4 * 3;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

}
