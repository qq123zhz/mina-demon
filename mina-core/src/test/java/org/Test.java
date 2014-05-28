package org;

public class Test {

	public static void main(String[] args) {

		int a = 0x10000001;
		a = a & 0xffffff00;
		System.out.println(String.format("%1$#8x", a));
		System.out.println(String.format("%1$#1x", a + 0x80000000 + 0x56));
		System.out.println(String.format("%1$#1x", (0x00000001 & 0xff)));

	}
}
//