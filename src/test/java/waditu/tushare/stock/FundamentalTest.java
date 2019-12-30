package waditu.tushare.stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class FundamentalTest {

	@Test
	@Ignore
	public void testGetStockBasics() {
		assertEquals(Fundamental.getStockBasics("2016-08-08"), null);
		assertTrue(Fundamental.getStockBasics("2019-12-13").size() > 1000);
	}

	@Test
	@Ignore
	public void testGetReportData() {
		assertTrue(Fundamental.getReportData(2019, 3, 1, 1, 100).size() > 10);
	}
	
	@Test
	@Ignore
	public void testGetProfitData() {
		assertTrue(Fundamental.getProfitData(2019, 3, 1, 1, 100).size() > 10);
	}
	
	@Test
	public void testGetOperationData() {
		assertTrue(Fundamental.getOperationData(2019, 3, 1, 1, 100).size() > 10);
	}
}
