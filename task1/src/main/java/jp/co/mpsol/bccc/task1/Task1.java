package jp.co.mpsol.bccc.task1;

import static java.lang.System.out;
import static org.bitcoinj.core.Utils.HEX;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.function.Function;

import org.bitcoin.Secp256k1Context;
import org.bitcoin.NativeSecp256k1Util.AssertFailException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.util.encoders.Hex;

public class Task1 {
	public static void main(String[] args) throws NoSuchAlgorithmException, AssertFailException, SignatureException,
			UnsupportedEncodingException {
		String d = "12345613gjelkjge";
		String privKeyHex = "66d63116c23f4c34dab4d074377b7852540587031ce7905953344d006a08e3fe";

		Function<byte[], String> base58check = b -> {
			byte[] ret = Arrays.copyOf(b, b.length + 4);
			System.arraycopy(Sha256Hash.hashTwice(b), 0, ret, b.length, 4);
			return Base58.encode(ret);
		};

		byte[] input = d.getBytes("UTF-8");

		println("D", d);

		println("sha1(d)", MessageDigest.getInstance("SHA-1").digest(input));
		println("sha256(d)", Sha256Hash.hash(input));
		println("sha512(d)", MessageDigest.getInstance("SHA-512").digest(input));
		println("sha256(sha256(d))", Sha256Hash.hashTwice(input));
		println("ripemd160(d)", ripemd160(input));
		println("ripemd160(sha256(d))", ripemd160(Sha256Hash.hash(input)));
		println("base58check(d)", base58check.apply(input));
		println("base58decodeChecked", Base58.decodeChecked(base58check.apply(input)));

		// RFC6979を使っているかどうかについて
		// https://github.com/mycelium-com/wallet/issues/141

		// TODO: Secp256k1の有効化
		println("Secp256k1 enabled", Secp256k1Context.isEnabled());

		ECKey eckey = ECKey.fromPrivate(new BigInteger(Hex.decode(privKeyHex)));

		println("privateKey", eckey.getPrivateKeyAsHex());
		println("publicKey", eckey.getPublicKeyAsHex());

		String signatureBase64 = eckey.signMessage(d);
		println("signatureBase64", signatureBase64);

		println("verify w/ same-key", checkSign(eckey, d, signatureBase64));
		println("verify w/ same-key(pubOnly)", checkSign(ECKey.fromPublicOnly(eckey.getPubKey()), d, signatureBase64));
		println("verify w/ different-key", checkSign(new ECKey(), d, signatureBase64));
		println("verify w/ different-message", checkSign(eckey, d + "x", signatureBase64));
		println("verify w/ different-signature", checkSign(eckey, d,//
				"ICU6V4DTwmLQxuLisHAUrPc8baMxRlrqNh+HqIArHzVsKELGBUFFsAWBdmCpmVHUy1wmliFNhARtDpSZOoJogIx="));
	}

	protected static byte[] ripemd160(byte[] b) {
		byte[] ret = new byte[20];
		RIPEMD160Digest digest = new RIPEMD160Digest();
		digest.update(b, 0, b.length);
		digest.doFinal(ret, 0);
		return ret;
	}

	protected static boolean checkSign(ECKey eckey, String message, String signatureBase64) {
		try {
			ECKey.fromPublicOnly(Hex.decode(eckey.getPublicKeyAsHex())).verifyMessage(message, signatureBase64);
			return true;
		} catch (SignatureException e) {
			return false;
		}
	}

	protected static void println(String label, byte[] b) {
		out.println(String.join(" ", label, HEX.encode(b)));
	}

	protected static void println(String label, String b) {
		out.println(String.join(" ", label, b));
	}

	protected static void println(String label, boolean b) {
		out.println(String.join(" ", label, "" + b));
	}
}
