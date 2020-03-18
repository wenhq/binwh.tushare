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
	}

}
