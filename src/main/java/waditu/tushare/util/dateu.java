package waditu.tushare.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import waditu.tushare.common.HTTParty;
import waditu.tushare.common.Utility;
import waditu.tushare.entity.TradeDateData;

public class dateu {

	/**
	 * 
	 * @param strDate yyyy-mm-dd "2019-02-12"
	 * @return ['2019', '1']
	 */
	public static List<String> yearQua(String strDate) {
		List<String> list = new ArrayList<String>();
		String year = strDate.substring(0, 4);
		String mon = strDate.substring(5, 7);
		list.add(year);
		list.add(quar(mon));
		return list;
	}

	private static String quar(String mon) {
		switch (mon) {
		case "01":
		case "02":
		case "03":
			return "1";
		case "04":
		case "05":
		case "06":
			return "2";
		case "07":
		case "08":
		case "09":
			return "3";
		case "10":
		case "11":
		case "12":
			return "4";
		default:
			return "";
		}
	}

	/**
	 * 
	 * @return '2019-12-15'
	 */
	public static String today() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	public static String getYear() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}

	public static String getMonth() {
		return new SimpleDateFormat("MM").format(new Date());
	}

	public static String getHour() {
		return new SimpleDateFormat("HH").format(new Date());
	}

	public static String todayLastYear() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -365);
		Date time = cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
	}

	public static String dayLastWeek() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		Date time = cal.getTime();
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
	}

	public static String getNow() {
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
	}

	public static String int2Time(long timestamp) {
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(timestamp));
	}

	public static long diffDay(String start, String end) {
		long count = 0;
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		Date endDate;
		try {
			beginDate = format.parse(start);
			endDate = format.parse(end);
			count = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return count;
	}

	public static List<TradeDateData> tradeCal() {
		List<TradeDateData> result = new ArrayList<TradeDateData>();

		String content = HTTParty.get(Utility.ALL_CAL_FILE, "GBK");

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
			reader.readLine(); // 标题行
			String line = null;
			SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
			while ((line = reader.readLine()) != null) {
				String item[] = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
				TradeDateData tradeDate = new TradeDateData();
				tradeDate.date = format.parse(item[0]);
				tradeDate.isOpen = item[1].equals("1") ? true : false;
				result.add(tradeDate);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isHoliday(String strDate) {
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format.parse(strDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				return true;
			} else {
				List<TradeDateData> list = tradeCal();
				List<TradeDateData> result = list.stream().filter(s -> !s.isOpen && s.date.equals(date))
						.collect(Collectors.toList());
				return result.size() > 0 ? true : false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

}
