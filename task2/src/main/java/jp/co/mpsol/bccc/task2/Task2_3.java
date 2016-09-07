package jp.co.mpsol.bccc.task2;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Random;

import org.bitcoinj.core.Sha256Hash;
import org.spongycastle.util.encoders.Hex;

public class Task2_3 {
	public static void main(String[] args) throws InterruptedException {
		final byte[] begin = "Blockchain Daigakko".getBytes(UTF_8);
		final byte[] adds = "1234567890".getBytes(UTF_8);

		System.out.println(adds.length);
		
		int randlen = 10000;
		byte[] buf = new byte[begin.length + randlen];
		System.arraycopy(begin, 0, buf, 0, begin.length);
		Random random = new Random();
		byte[] ret = null;
		do {
			for (int i = begin.length, len = buf.length; i < len; i++) {
				buf[i] = (byte) adds[random.nextInt(adds.length)];
			}
			ret = Sha256Hash.hash(buf);

			if (ret[0] == 0x00) {
				System.out.println(new String(buf));
				System.out.println(Hex.toHexString(ret));
				return;
			}
		} while (true);
	}

}
