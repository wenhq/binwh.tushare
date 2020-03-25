package waditu.tushare.stock;

import static java.util.Comparator.comparing;
import static waditu.tushare.common.Utility.DAY_PRICE_MIN_URL;
import static waditu.tushare.common.Utility.DAY_PRICE_URL;
import static waditu.tushare.common.Utility.DOMAINS;
import static waditu.tushare.common.Utility.K_LABELS;
import static waditu.tushare.common.Utility.K_MIN_LABELS;
import static waditu.tushare.common.Utility.K_TYPE;
import static waditu.tushare.common.Utility.LIVE_DATA_URL;
import static waditu.tushare.common.Utility.PAGES;
import static waditu.tushare.common.Utility.P_TYPE;
import static waditu.tushare.common.Utility.QueryDateFormat;
import static waditu.tushare.common.Utility.TICK_PRICE_URL;
import static waditu.tushare.common.Utility._codeToSymbol;
import static waditu.tushare.common.Utility._isBlank;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import waditu.tushare.common.HTTParty;
import waditu.tushare.common.Utility;
import waditu.tushare.entity.QuoteData;
import waditu.tushare.entity.SinaDDData;
import waditu.tushare.entity.TickData;
import waditu.tushare.entity.TradeData;
import waditu.tushare.exception.IOError;
import waditu.tushare.exception.TypeError;

/**
 * Created by Raymond on 26/11/2016.
 */
public class Trading {

	/**
	 * 获取个股历史交易记录
	 * 迁移自{@code def get_hist_data(code=None, start=None, end=None, ktype='D', 
	 * retry_count=3, pause=0.001)}方法。
	 * 
	 * @param code       股票代码 e.g. 600848
	 * @param start      开始日期 format：YYYY-MM-DD 为空时取到API所提供的最早日期数据
	 * @param end        结束日期 format：YYYY-MM-DD 为空时取到最近一个交易日数据
	 * @param ktype      数据类型，D=日k线 W=周 M=月 5=5分钟 15=15分钟 30=30分钟 60=60分钟，默认为D
	 * @param retryCount 默认 3 如遇网络等问题重复执行的次数
	 * @param pause      默认 0 重复请求数据过程中暂停的秒数，防止请求间隔时间太短出现的问题
	 * @return 属性:日期 ，开盘价， 最高价， 收盘价， 最低价， 成交量， 价格变动
	 *         ，涨跌幅，5日均价，10日均价，20日均价，5日均量，10日均量，20日均量，换手率
	 */
	public static List<TradeData> getHistData(String code, String start, String end, String ktype, int retryCount,
			int pause) {
		String symbolCode = Cons.codeToSymbol(code);
		String url;

		if (Arrays.asList(Cons.K_LABELS).contains(ktype.toUpperCase())) {
			url = String.format(Cons.URL.DAY_PRICE_URL.value, Cons.P_TYPE.http.value, Cons.DOMAINS.ifeng.value,
					Cons.K_TYPE.valueOf(ktype.toUpperCase()).value, symbolCode);
		} else if (Arrays.asList(Cons.K_MIN_LABELS).contains(ktype.toUpperCase())) {
			url = String.format(Cons.URL.DAY_PRICE_MIN_URL.value, Cons.P_TYPE.http.value, Cons.DOMAINS.ifeng.value,
					symbolCode, ktype);
		} else {
			throw new TypeError("ktype: " + ktype + " is invalid.");
		}

		List<TradeData> result = new ArrayList<TradeData>();
		for (int count = 0; count < retryCount; count++) {
			try {
				String respContent = HTTParty.get(url);

				JSONArray recordList = JSON.parseObject(respContent).getJSONArray("record");
				for (int i = 0; i < recordList.size(); i++) {
					JSONArray itemList = recordList.getJSONArray(i);
					TradeData item = new TradeData();
					item.date = (itemList.getDate(0));
					item.open = (itemList.getDouble(1));
					item.high = (itemList.getDouble(2));
					item.close = (itemList.getDouble(3));
					item.low = (itemList.getDouble(4));
					item.volume = (itemList.getDouble(5));
					item.price_change = (itemList.getDouble(6));
					item.p_change = (itemList.getDouble(7));
					item.ma5 = (itemList.getDouble(8));
					item.ma10 = (itemList.getDouble(9));
					item.ma20 = (itemList.getDouble(10));
					item.v_ma5 = (itemList.getDouble(11));
					item.v_ma10 = (itemList.getDouble(12));
					item.v_ma20 = (itemList.getDouble(13));
					if (itemList.size() > 14) {
						item.turnover = (itemList.getDouble(14));
					}
					result.add(item);
				}
				// 将列表倒叙排列
				result = result.stream().sorted(comparing(TradeData::getDate).reversed()).collect(Collectors.toList());
				SimpleDateFormat QueryDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				// 按时间过滤
				if (start != null) {
					long startlong = QueryDateFormat.parse(start).getTime();
					result = result.stream().filter(s -> s.date.getTime() >= startlong).collect(Collectors.toList());
				}
				if (end != null) {
					long endlong = QueryDateFormat.parse(end).getTime();
					result = result.stream().filter(s -> s.date.getTime() <= endlong).collect(Collectors.toList());
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
			return result;
		}
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.value);
	}

	@Deprecated
	public static List<TradeData> getTradeList(String code) {
		return getTradeList(code, "D");
	}

	/**
	 * 获取个股历史交易记录
	 * 
	 * @param code  股票代码
	 * @param ktype 数据类型，D=日k线 W=周 M=月 5=5分钟 15=15分钟 30=30分钟 60=60分钟，默认为D
	 * @return
	 * @deprecated 使用getHistData代替
	 */
	@Deprecated
	public static List<TradeData> getTradeList(String code, String ktype) {
		String symbolCode = _codeToSymbol(code);
		if (_isBlank(symbolCode))
			throw new RuntimeException("code is invalid.");
		String url;
		if (Arrays.asList(K_LABELS).contains(ktype.toUpperCase())) {
			url = String.format(DAY_PRICE_URL, P_TYPE.get("http"), DOMAINS.get("ifeng"),
					K_TYPE.get(ktype.toUpperCase()), symbolCode);
		} else if (Arrays.asList(K_MIN_LABELS).contains(ktype.toUpperCase())) {
			url = String.format(DAY_PRICE_MIN_URL, P_TYPE.get("http"), DOMAINS.get("ifeng"), symbolCode, ktype);
		} else {
			throw new RuntimeException("ktype: " + ktype + " is invalid.");
		}
		List<TradeData> list = new ArrayList<TradeData>();

		String respContent = HTTParty.get(url);
		if (respContent != null) {
			JSONArray recordList = JSON.parseObject(respContent).getJSONArray("record");
			for (int i = 0; i < recordList.size(); i++) {
				JSONArray itemList = recordList.getJSONArray(i);
				TradeData item = new TradeData();
				item.date = (itemList.getDate(0));
				item.open = (itemList.getDouble(1));
				item.high = (itemList.getDouble(2));
				item.close = (itemList.getDouble(3));
				item.low = (itemList.getDouble(4));
				item.volume = (itemList.getDouble(5));
				item.price_change = (itemList.getDouble(6));
				item.p_change = (itemList.getDouble(7));
				item.ma5 = (itemList.getDouble(8));
				item.ma10 = (itemList.getDouble(9));
				item.ma20 = (itemList.getDouble(10));
				item.v_ma5 = (itemList.getDouble(11));
				item.v_ma10 = (itemList.getDouble(12));
				item.v_ma20 = (itemList.getDouble(13));
				if (itemList.size() > 14) {
					item.turnover = (itemList.getDouble(14));
				}

				list.add(item);
			}
			// 将列表倒叙排列
			list = list.stream().sorted(comparing(TradeData::getDate).reversed()).collect(Collectors.toList());

		}

		return list;

	}

	/**
	 * @deprecated 使用getHistData代替
	 * @param code
	 * @param ktype
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Deprecated
	public static List<TradeData> getTradeList(String code, String ktype, Date startDate, Date endDate) {
		List<TradeData> list = getTradeList(code, ktype);
		if (list.size() > 0) {
			list = list.stream()
					.filter(s -> s.getDate().compareTo(startDate) >= 0 && s.getDate().compareTo(endDate) <= 0)
					.collect(Collectors.toList());
		}

		return list;
	}

	/**
	 * 获取分笔数据
	 * 迁移自{@code def get_tick_data(code=None, date=None, retry_count=3, pause=0.001,
                  src='sn')}方法。
	 * 
	 * @param code        string 股票代码 e.g. 600848
	 * @param date        string 日期 format: YYYY-MM-DD
	 * @param retry_count int, 默认 3 如遇网络等问题重复执行的次数
	 * @param pause       int, 默认 0 重复请求数据过程中暂停的秒数，防止请求间隔时间太短出现的问题
	 * @param src         数据源选择，可输入sn(新浪)、tt(腾讯)、nt(网易)，默认sn
	 * @return
	 */
	public static List<TickData> getTickData(String code, String date, int retryCount, int pause, String src) {
		List<TickData> result = new ArrayList<TickData>();
		if (!Arrays.asList(Cons.TICK_SRCS).contains(src.toLowerCase())) {
			throw new TypeError(Cons.Msg.TICK_SRC_ERROR.value);
		}
		if (src.toLowerCase().equals("sn") || src.toLowerCase().equals("nt")) {
			throw new IOError("服务已下线");
		}
		String symbol = Cons.codeToSymbol(code);
		String symbol_dgt = Cons.codeToSymbolDgt(code);
		String datestr = date.replace("-", "");
		HashMap<String, String> url = new HashMap<String, String>();
		url.put(Cons.TICK_SRCS[0], String.format(Cons.URL.TICK_PRICE_URL.value, Cons.P_TYPE.http.value,
				Cons.DOMAINS.sf.value, Cons.PAGES.dl.value, date, symbol));
		url.put(Cons.TICK_SRCS[1], String.format(Cons.URL.TICK_PRICE_URL_TT.value, Cons.P_TYPE.http.value,
				Cons.DOMAINS.tt.value, Cons.PAGES.idx.value, symbol, datestr));
		url.put(Cons.TICK_SRCS[2], String.format(Cons.URL.TICK_PRICE_URL_NT.value, Cons.P_TYPE.http.value,
				Cons.DOMAINS.netease.value, date.substring(0, 4), datestr, symbol_dgt));
		for (int count = 0; count < retryCount; count++) {
			try {
				if (src.equalsIgnoreCase(Cons.TICK_SRCS[1])) {
					throw new IOError("尚未实现");
				} else {
					// 服务已下线
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
			return result;
		}
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.value);
	}

	/**
	 * 获取分笔数据
	 * 
	 * @param code 股票编码
	 * @param date 日期
	 * @return 返回 tick data list
	 * @deprecated 重写getHistData方法
	 */
	@Deprecated
	public static List<TickData> getTickData(String code, Date date) {
		if (_isBlank(code) || code.trim().length() != 6 || date == null)
			return null;
		String symbolCode = _codeToSymbol(code);
		if (_isBlank(symbolCode))
			throw new RuntimeException("code is invalid.");
		String url = String.format(TICK_PRICE_URL, P_TYPE.get("http"), DOMAINS.get("sf"), PAGES.get("dl"),
				QueryDateFormat.format(date), symbolCode);
		String respContent = HTTParty.get(url, "GBK");
		if (respContent != null) {
			List<TickData> list = new ArrayList<>();

			try {
				CSVParser parser = CSVParser.parse(respContent,
						CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader());
				for (CSVRecord csvRecord : parser) {
					TickData data = new TickData();
					String timeStr = csvRecord.get("成交时间");
					data.time = (LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:m:s")));
					data.price = (csvRecord.get("成交价").equals("--") ? null : Double.parseDouble(csvRecord.get("成交价")));
					data.change = (csvRecord.get("价格变动").equals("--") ? null
							: Double.parseDouble(csvRecord.get("价格变动")));
					data.volume = (csvRecord.get("成交量(手)").equals("--") ? null
							: Double.parseDouble(csvRecord.get("成交量(手)")));
					data.amount = (csvRecord.get("成交额(元)").equals("--") ? null
							: Double.parseDouble(csvRecord.get("成交额(元)")));
					data.type = (csvRecord.get("性质"));
					list.add(data);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return list;

		} else {
			return null;
		}

	}

	/**
	 * 获取sina大单数据，本质上需要获取成交明细，可以成交量和成交额来判断大单。
	 * 迁移自{@code def get_sina_dd(code=None, date=None, vol=400, retry_count=3, pause=0.001)}方法。
	 * 
	 * @param code       股票代码 e.g. 600848
	 * @param date       日期 format：YYYY-MM-DD
	 * @param vol        成交量（手）大于等于判断大单
	 * @param retryCount 如遇网络等问题重复执行的次数
	 * @param pause      重复请求数据过程中暂停的秒数，防止请求间隔时间太短出现的问题
	 * 
	 */
	public static List<SinaDDData> getSinaDD(String code, String date, int vol, int retryCount, int pause) {
		// TODO 删除SinaDDData， 使用TickData

		String symbol = Cons.codeToSymbol(code);
		vol = vol * 100;
		String url = String.format(Cons.URL.SINA_DD.value, Cons.P_TYPE.http.value, Cons.DOMAINS.vsf.value,
				Cons.PAGES.sinadd.value, symbol, vol, date);

		List<SinaDDData> result = new ArrayList<>();
		for (int count = 0; count < retryCount; count++) {
			try {
				String content = HTTParty.get(url, "GBK");

				JSONArray list = JSONArray.parseArray(content);

				for (int i = 0; i < list.size(); i++) {
					JSONObject obj = list.getJSONObject(i);
					SinaDDData dd = new SinaDDData();
					dd.code = obj.getString("symbol").replaceAll("sh", "").replaceAll("sz", "");
					dd.name = obj.getString("name");
					dd.tickTime = Utility.DateTimeFormat.parse(date + " " + obj.getString("ticktime"));
					dd.price = obj.getDouble("price");
					dd.volume = obj.getDouble("volume");
					dd.amount = dd.price * dd.volume * 100;
					dd.prevPrice = obj.getDouble("prev_price");
					switch (obj.getString("kind")) {
					case "U":
						dd.kind = "买盘";
						break;
					case "D":
						dd.kind = "卖盘";
						break;
					case "E":
						dd.kind = "中性盘";
						break;
					default:
						dd.kind = "中性盘";
						break;
					}
					result.add(dd);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
			return result;
		}
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.value);
	}

	@Deprecated
	public static QuoteData getRealtimeQuotes(String code) {
		String symbolCode = _codeToSymbol(code);
		if (_isBlank(symbolCode))
			throw new RuntimeException("code is invalid.");

		int randNumber = Utility.generateRandom(1000_0000, 10_0000_0000);
		String url = String.format(LIVE_DATA_URL, P_TYPE.get("http"), DOMAINS.get("sinahq"), randNumber, symbolCode);
		String respContent = HTTParty.get(url, "GBK");
		String[] arr = respContent.split(",");
		QuoteData data = new QuoteData();
		data.name = ((arr[0].split("=")[1]).replace("\"", ""));
		data.open = Double.parseDouble(arr[1]);
		data.pre_close = Double.parseDouble(arr[2]);
		data.price = Double.parseDouble(arr[3]);
		data.high = Double.parseDouble(arr[4]);
		data.low = Double.parseDouble(arr[5]);
		data.bid = Double.parseDouble(arr[6]);
		data.ask = Double.parseDouble(arr[7]);
		data.volume = Double.parseDouble(arr[8]);
		data.amount = Double.parseDouble(arr[9]);
		data.b1_v = Double.parseDouble(arr[10]);
		data.b1_p = Double.parseDouble(arr[11]);
		data.b2_v = Double.parseDouble(arr[12]);
		data.b2_p = Double.parseDouble(arr[13]);
		data.b3_v = Double.parseDouble(arr[14]);
		data.b3_p = Double.parseDouble(arr[15]);
		data.b4_v = Double.parseDouble(arr[16]);
		data.b4_p = Double.parseDouble(arr[17]);
		data.b5_v = Double.parseDouble(arr[18]);
		data.b5_p = Double.parseDouble(arr[19]);
		data.a1_v = Double.parseDouble(arr[20]);
		data.a1_p = Double.parseDouble(arr[21]);
		data.a2_v = Double.parseDouble(arr[22]);
		data.a2_p = Double.parseDouble(arr[23]);
		data.a3_v = Double.parseDouble(arr[24]);
		data.a3_p = Double.parseDouble(arr[25]);
		data.a4_v = Double.parseDouble(arr[26]);
		data.a4_p = Double.parseDouble(arr[27]);
		data.a5_v = Double.parseDouble(arr[28]);
		data.a5_p = Double.parseDouble(arr[29]);
		data.date = LocalDate.parse(arr[30]);

		data.time = LocalTime.parse(arr[31]);
		data.code = code;

		return data;
	}

}