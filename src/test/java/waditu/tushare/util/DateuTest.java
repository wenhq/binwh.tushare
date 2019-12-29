package waditu.tushare.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import waditu.tushare.entity.TradeDateData;

public class DateuTest {

	@Test
	public void testYearQua() {
		List<String> result = Dateu.yearQua("2016-02-16");
		assertEquals(result.get(0), "2016");
		assertEquals(result.get(1), "1");
		
		result = Dateu.yearQua("2017-2-16");
		assertEquals(result.get(0), "2017");
		assertEquals(result.get(1), "");
	}

	@Test
	public void testToday() {
		String today = Dateu.today();
//		System.out.println(today);
		assertEquals(today.length(), 10);
	}

	@Test
	public void testGetYear() {
		String year = Dateu.getYear();
//		System.out.println(year);
		assertEquals(year.length(), 4);
		assertTrue(Integer.valueOf(year) > 2018);
	}

	@Test
	public void testGetMonth() {
		String month = Dateu.getMonth();
//		System.out.println(month);
		assertEquals(month.length(), 2);
		assertTrue(Integer.valueOf(month) > 0);
		assertTrue(Integer.valueOf(month) < 13);
	}

	@Test
	public void testGetHour() {
		String hour = Dateu.getHour();
//		System.out.println(hour);
		assertEquals(hour.length(), 2);
		assertTrue(Integer.valueOf(hour) > 0);
		assertTrue(Integer.valueOf(hour) < 25);
	}

	@Test
	public void testTodayLastYear() {
		String todayLastYear = Dateu.todayLastYear();
//		System.out.println(todayLastYear);
		assertEquals(todayLastYear.length(), 10);
	}

	@Test
	public void testDayLastWeek() {
		String dayLastWeek = Dateu.dayLastWeek();
//		System.out.println(dayLastWeek);
		assertEquals(dayLastWeek.length(), 10);
	}

	@Test
	public void testGetNow() {
		String now = Dateu.getNow();
//		System.out.println(now);
		assertEquals(now.length(), 19);
	}

	@Test
	public void testInt2Time() {
		long timestamp = System.currentTimeMillis();
//		System.out.println(timestamp);
		String date = Dateu.int2Time(timestamp);
//		System.out.println(date);
		assertEquals(date.length(), 19);
	}
	
	@Test
	public void testDiffDay() {
		long count = Dateu.diffDay("2000-01-01", "2020-01-01");
		assertEquals(count, 7305);
	}
	
	@Test
	public void testTradeCal() {
		List<TradeDateData> result =  Dateu.tradeCal();
		assertTrue(result.size() > 0);
	}
	
	@Test
	public void testIsHoliday() {
		assertTrue(!Dateu.isHoliday("2019-12-13"));
		assertTrue(Dateu.isHoliday("2019-12-15"));
	}
}
