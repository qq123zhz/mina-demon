package org.iteam.mina;

public class Test {
	public static void main(String[] args) {
		int temp = 0x10100140;
		System.out.println("methodCode-->:" + String.format("%1$#1x", temp));
		int methodCode = temp & 0xffffff00 + 0x80000000 + 0x01;

		System.out.println("methodCode-->:"
				+ String.format("%1$#1x", methodCode));
		int type = (0x80100140 & 0xf0000000) >> 28;
		System.out.println("type:" + ((0x80100140 & 0xf0000000) >> 28));
		System.out.println("type:" + ((0x80100140 & 0xf0000000) >>> 28));
		System.out.println("type:" + ((0x10100140 & 0xf0000000) >> 28));
		System.out.println("type:" + ((0x10100140 & 0xf0000000) >>> 28));

		System.out.println(0x0);
		System.out.println(0x7);
		System.out.println(0x8);
		System.out.println(0xf);
	}
}
