package waditu.tushare.stock;

import static org.junit.Assert.*;

import org.junit.Test;

public class fundamentalTest {

	@Test
	public void testGetStockBasics() {
		assertEquals(fundamental.getStockBasics("2016-08-08"), null);
		assertTrue(fundamental.getStockBasics("2019-12-13").size() > 1000);
	}

}
