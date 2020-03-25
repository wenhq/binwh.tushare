package waditu.tushare.stock;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import waditu.tushare.exception.IOError;

/**
 * Created by Raymond on 24/12/2016.
 */
public class TradingTest {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGetHistData() {
		int count = Trading.getHistData("300118", "2020-02-21", "2020-03-21", "D", 3, 100).size();
		assertTrue(count > 0);
		assertTrue(count < 30);
	}

	@Test
	public void testGetHistData_index() {
		int count = Trading.getHistData("cyb", "2020-02-21", "2020-03-21", "w", 3, 100).size();
		assertTrue(count > 0);
		assertTrue(count < 30);
	}

	@Test
	public void testGetHistData_min() {
		int count = Trading.getHistData("cyb", "2020-02-21", "2020-03-21", "15", 3, 100).size();
		assertTrue(count > 0);
		assertTrue(count < 350);
	}

	@Test
	@Ignore
	public void testGetTradeList() {
		try {
			Date startDate = dateFormat.parse("2016-12-21");
			Date endDate = dateFormat.parse("2016-12-23");
			Trading.getTradeList("300118", "D", startDate, endDate);
//            Trading.getTradeList("sh", "D", startDate, endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetTickData_SN(){
        thrown.expect(IOError.class);
        thrown.expectMessage("服务已下线");
		Trading.getTickData("300118", "2020-02-21", 3, 100, "sn");
	}

	@Test
	/**
	 * 该功能为实现
	 */
	public void testGetTickData_TT(){
        thrown.expect(IOError.class);
        thrown.expectMessage("获取失败，请检查网络");
		Trading.getTickData("300118", "2020-02-21", 3, 100, "tt");
	}

	@Test
	@Ignore
	public void testGetTickData() {
		try {
			Date date = dateFormat.parse("2016-12-21");
			Trading.getTickData("300118", date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetSinaDD() {
		int count = Trading.getSinaDD("600118", "2020-03-25", 400, 3, 100).size();
		assertTrue(count > 50);
	}
	
	@Test
	@Ignore
	public void testGetQuotesData() {
		try {
//            System.out.println(Utility.generateRandom(1000_0000, 10_0000_0000));
			Trading.getRealtimeQuotes("000581");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
