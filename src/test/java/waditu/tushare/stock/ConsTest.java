package waditu.tushare.stock;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConsTest {

	@Test
	public void testCodeToSymbol() {
		assertEquals(Cons.codeToSymbol("hs300"), "sz399300");
		assertEquals(Cons.codeToSymbol(" hs300"), "sz399300");
		assertEquals(Cons.codeToSymbol("sz000001"), "sz000001");
		assertEquals(Cons.codeToSymbol("000001"), "sz000001");
		assertEquals(Cons.codeToSymbol("600001"), "sh600001");
		assertEquals(Cons.codeToSymbol("110001"), "sh110001");
	}

	@Test
	public void testCodeToSymbolDgt() {
		assertEquals(Cons.codeToSymbolDgt("hs300"), "sz399300");
		assertEquals(Cons.codeToSymbolDgt(" hs300"), "sz399300");
		assertEquals(Cons.codeToSymbolDgt("sz000001"), "sz000001");
		assertEquals(Cons.codeToSymbolDgt("000001"), "1000001");
		assertEquals(Cons.codeToSymbolDgt("600001"), "0600001");
	}

}
