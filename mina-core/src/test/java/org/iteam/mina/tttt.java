package org.iteam.mina;

public class tttt {

	public static void main(String[] args) {
		Short showData = 372;

		String hex = String.format("%04X", showData); // 以十六进制显示，格式化成十六进制的数据

		String str = Integer.toHexString(showData); // 转换成16进制

		System.out.println("thex:::" + hex); // 结果是：0x0174

		System.out.println("str:::" + str); // 结果是：174
	}

}
