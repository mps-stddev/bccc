package jp.co.mpsol.bccc.task3;

import static org.bitcoinj.script.Script.executeScript;
import static org.bitcoinj.script.ScriptOpCodes.OP_1;
import static org.bitcoinj.script.ScriptOpCodes.OP_10;
import static org.bitcoinj.script.ScriptOpCodes.OP_2SWAP;
import static org.bitcoinj.script.ScriptOpCodes.OP_3;
import static org.bitcoinj.script.ScriptOpCodes.OP_3DUP;
import static org.bitcoinj.script.ScriptOpCodes.OP_4;
import static org.bitcoinj.script.ScriptOpCodes.OP_7;
import static org.bitcoinj.script.ScriptOpCodes.OP_8;
import static org.bitcoinj.script.ScriptOpCodes.OP_ADD;
import static org.bitcoinj.script.ScriptOpCodes.OP_BOOLAND;
import static org.bitcoinj.script.ScriptOpCodes.OP_EQUAL;
import static org.bitcoinj.script.ScriptOpCodes.OP_ROT;
import static org.bitcoinj.script.ScriptOpCodes.OP_SUB;
import static org.bitcoinj.script.ScriptOpCodes.OP_SWAP;
import static org.bitcoinj.script.ScriptOpCodes.OP_VERIFY;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.Script.VerifyFlag;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.junit.Before;
import org.junit.Test;

public class Task3 {
	private static final NetworkParameters PARAMS = TestNet3Params.get();
	private Transaction tx;
	private static final Map<String, Integer> nameCodeMap;
	private static final Map<Integer, String> codeNameMap;

	static {
		nameCodeMap = new LinkedHashMap<>();
		codeNameMap = new LinkedHashMap<>();
		for (Field f : ScriptOpCodes.class.getFields()) {
			final String name = f.getName();
			if (name.startsWith("OP_")) {
				if (name.equals("OP_TRUE")) {
					continue; // OP_1とかぶるので飛ばす
				}

				f.setAccessible(true);
				try {
					final int int1 = f.getInt(ScriptOpCodes.class);
					nameCodeMap.put(name, int1);
					codeNameMap.put(int1, name);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Before
	public void setup() {
		tx = new Transaction(PARAMS);
		tx.addInput(new TransactionInput(PARAMS, tx, new byte[] {}));
	}

	@Test
	public void test() {
		Script script = new ScriptBuilder().smallNum(0).build();
		LinkedList<byte[]> stack = new LinkedList<byte[]>();
		executeScript(tx, 0, script, stack, Script.ALL_VERIFY_FLAGS);
		assertEquals("OP_0 push length", 0, stack.get(0).length);
	}

	@Test
	public void test01() {
		run("test01", OP_3, OP_4, //
				OP_ADD, OP_7, OP_EQUAL);
	}

	@Test
	public void test02() {
		run("test02", OP_7, OP_4, //
				OP_SUB, OP_3, OP_EQUAL);
	}

	@Test
	public void test03() {
		run("test03", OP_4, OP_7, //
				OP_SWAP, OP_4, OP_EQUAL, OP_SWAP, OP_7, OP_EQUAL, OP_BOOLAND);
	}

	@Test
	public void test04() {

		run("test04", OP_3, OP_1, OP_7,
				//
				OP_3DUP, OP_ADD, OP_8, OP_EQUAL, OP_SWAP, OP_ROT, OP_ADD, OP_10, OP_EQUAL, OP_2SWAP, OP_ADD, OP_4,
				OP_EQUAL, OP_BOOLAND, OP_BOOLAND);
	}

	private LinkedList<byte[]> run(String label, int... opcodes) {
		System.out.println(label);
		for (int i = 1, n = opcodes.length + 1; i < n; i++) {
			final int[] stepScript = Arrays.copyOf(opcodes, i);
			System.out.println(i + "stepRun: " + stackStr(run(script(stepScript), stack())) + " " + dump(stepScript));
		}
		System.out.println("  TestRun: " + stackStr(run(script(opcodes), stack())));
		final LinkedList<byte[]> run = run(scriptWithVerify(opcodes), stack());
		System.out.println("      Run: " + stackStr(run));
		return run;
	}

	private LinkedList<byte[]> run(Script script, LinkedList<byte[]> stack) {
		Script.executeScript(tx, 0, //
				script, //
				stack, //
				EnumSet.of(VerifyFlag.STRICTENC));
		return stack;
	}

	private String dump(int... opcodes) {
		StringBuilder sb = new StringBuilder();
		for (int b : opcodes) {
			String name = codeNameMap.get(b);
			if (name == null) {
				throw new IllegalArgumentException("unkonwn " + b);
			}
			sb.append(name);
			sb.append(" ");
		}
		return sb.toString();
	}

	private String stackStr(LinkedList<byte[]> stack) {
		StringBuilder str = new StringBuilder();
		while (!stack.isEmpty()) {
			byte[] b = stack.pop();
			str.append(Arrays.toString(b));
		}
		return str.toString();
	}

	private Script script(int... opcodes) {
		ScriptBuilder script = new ScriptBuilder();
		for (int b : opcodes) {
			script.op(b);
		}
		return script.build();
	}

	private Script scriptWithVerify(int... opcodes) {
		final int[] copyOf = Arrays.copyOf(opcodes, opcodes.length + 1);
		copyOf[opcodes.length] = OP_VERIFY;
		return script(copyOf);
	}

	private LinkedList<byte[]> stack() {
		return new LinkedList<byte[]>();
	}
}
