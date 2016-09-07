package jp.co.mpsol.bccc.task2;

import static java.lang.System.out;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Task2_1 {
	public static void main(String[] args) {
		out.println("1-1 ビッグエンディアン 符号付き絶対値");
		out.println(sigAbs(0, BIG_ENDIAN));
		out.println(sigAbs(1, BIG_ENDIAN));
		out.println(sigAbs(-1, BIG_ENDIAN));
		out.println(sigAbs(5, BIG_ENDIAN));
		out.println(sigAbs(-5, BIG_ENDIAN));
		out.println(sigAbs(5000, BIG_ENDIAN));
		out.println(sigAbs(-5000, BIG_ENDIAN));

		out.println();

		out.println("1-2 ビッグエンディアン 符号付き2の補数");
		out.println(twoComp(0, BIG_ENDIAN));
		out.println(twoComp(1, BIG_ENDIAN));
		out.println(twoComp(-1, BIG_ENDIAN));
		out.println(twoComp(5, BIG_ENDIAN));
		out.println(twoComp(-5, BIG_ENDIAN));
		out.println(twoComp(5000, BIG_ENDIAN));
		out.println(twoComp(-5000, BIG_ENDIAN));

		out.println();

		out.println("1-3 リトルエンディアン 符号付き絶対値");
		out.println(sigAbs(0, LITTLE_ENDIAN));
		out.println(sigAbs(1, LITTLE_ENDIAN));
		out.println(sigAbs(-1, LITTLE_ENDIAN));
		out.println(sigAbs(5, LITTLE_ENDIAN));
		out.println(sigAbs(-5, LITTLE_ENDIAN));
		out.println(sigAbs(5000, LITTLE_ENDIAN));
		out.println(sigAbs(-5000, LITTLE_ENDIAN));

		out.println();

		out.println("1-4 リトルエンディアン 符号付き2の補数");
		out.println(twoComp(0, LITTLE_ENDIAN));
		out.println(twoComp(1, LITTLE_ENDIAN));
		out.println(twoComp(-1, LITTLE_ENDIAN));
		out.println(twoComp(5, LITTLE_ENDIAN));
		out.println(twoComp(-5, LITTLE_ENDIAN));
		out.println(twoComp(5000, LITTLE_ENDIAN));
		out.println(twoComp(-5000, LITTLE_ENDIAN));
	}

	public static String hexStr(byte[] buf) {
		String[] sbuf = new String[Integer.BYTES];
		for (int i = 0; i < Integer.BYTES; i++) {
			sbuf[i] = String.format("%2s", Integer.toHexString(buf[i] & 0xFF)).replace(' ', '0');
		}
		return String.join(" ", sbuf);
	}

	public static String bitStr(byte[] buf) {
		String[] sbuf = new String[Integer.BYTES];
		for (int i = 0; i < Integer.BYTES; i++) {
			sbuf[i] = String.format("%8s", Integer.toBinaryString(buf[i] & 0xFF)).replace(' ', '0');
		}
		return String.join(" ", sbuf);
	}

	public static int twosComplement(int num) {
		return ~num + 1;
	}

	public static String twoComp(int num, ByteOrder order) {
		byte[] buf = ByteBuffer.allocate(Integer.BYTES).order(order).putInt(num).array();
		return String.format("%8s %s", num, bitStr(buf));
	}

	public static String sigAbs(int num, ByteOrder order) {
		int absNum = num < 0 ? Math.abs(num) | Integer.MIN_VALUE : num;
		byte[] buf = ByteBuffer.allocate(Integer.BYTES).order(order).putInt(absNum).array();
		return String.format("%8s %s", num, bitStr(buf));
	}

}
