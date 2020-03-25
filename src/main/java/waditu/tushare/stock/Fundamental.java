package waditu.tushare.stock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import waditu.tushare.common.HTTParty;
import waditu.tushare.entity.CashFlowData;
import waditu.tushare.entity.DebtpayingData;
import waditu.tushare.entity.GrowthData;
import waditu.tushare.entity.OperationData;
import waditu.tushare.entity.ProfitData;
import waditu.tushare.entity.ReportData;
import waditu.tushare.entity.StockBasicsData;
import waditu.tushare.exception.IOError;
import waditu.tushare.exception.TypeError;

public class Fundamental {

	/**
	 * 获取沪深上市公司基本情况
	 * 
	 * @param date 日期YYYY-MM-DD，默认为上一个交易日，目前只能提供2016-08-09之后的历史数据
	 * @return
	 */
	public static List<StockBasicsData> getStockBasics(String date) {
		date = date.replaceAll("-", "");
		if (Integer.valueOf(date) < 20160809) {
			return null;
		}
		String datepre = date.equals("") ? "" : date.substring(0, 6) + "/";
		String url = String.format(Cons.URL.ALL_STOCK_BASICS_FILE.value, datepre, date);
		String content = HTTParty.get(url, "GBK");
//		System.out.println(content);
		if (content.equals("") || content.equals(null)) {
			return null;
		}

		List<StockBasicsData> result = new ArrayList<StockBasicsData>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
		try {
			String line = reader.readLine();// 标题行
			String item[] = line.split(",");
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i < item.length; i++) {
				map.put(item[i], i);
			}

			while ((line = reader.readLine()) != null) {
				item = line.split(",");
				StockBasicsData basics = new StockBasicsData();
				basics.code = item[map.get("code")];
				basics.name = item[map.get("name")];
				basics.industry = item[map.get("industry")];
				basics.area = item[map.get("area")];
				basics.pe = Double.valueOf(item[map.get("pe")]);
				basics.outstanding = Double.valueOf(item[map.get("outstanding")]);
				basics.totals = Double.valueOf(item[map.get("totals")]);
				basics.totalAssets = Double.valueOf(item[map.get("totalAssets")]);
				basics.liquidAssets = Double.valueOf(item[map.get("liquidAssets")]);
				basics.fixedAssets = Double.valueOf(item[map.get("fixedAssets")]);
				basics.reserved = Double.valueOf(item[map.get("reserved")]);
				basics.reservedPerShare = Double.valueOf(item[map.get("reservedPerShare")]);
				basics.esp = Double.valueOf(item[map.get("esp")]);
				basics.bvps = Double.valueOf(item[map.get("bvps")]);
				basics.pb = Double.valueOf(item[map.get("pb")]);
				basics.timeToMarket = item[map.get("timeToMarket")];
				basics.undp = Double.valueOf(item[map.get("undp")]);
				basics.perundp = Double.valueOf(item[map.get("perundp")]);
				basics.rev = Double.valueOf(item[map.get("rev")]);
				basics.profit = Double.valueOf(item[map.get("profit")]);
				basics.gpr = Double.valueOf(item[map.get("gpr")]);
				basics.npr = Double.valueOf(item[map.get("npr")]);
				basics.holders = Double.valueOf(item[map.get("holders")]).intValue();
				result.add(basics);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取业绩报表数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度 说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @return
	 * @throws TypeError
	 */
	public static List<ReportData> getReportData(int year, int quarter) throws TypeError {
		List<ReportData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getReportData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取业绩报表数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<ReportData> getReportData(int year, int quarter, int pageNo, int retryCount, int pause)
			throws IOError {
		List<ReportData> result = new ArrayList<ReportData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.REPORT_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");
//				text = text.replace("--", "");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
					ReportData reportData = new ReportData();
					List<JXNode> items = node.sel("//td//text()");
					reportData.code = items.get(0).toString();
					reportData.name = items.get(1).toString();
					reportData.eps = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					reportData.epsrate = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					reportData.navps = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					reportData.roe = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					reportData.cps = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					reportData.net_profits = items.get(7).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(7).toString());
					reportData.net_profit_rate = items.get(8).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(8).toString());
					reportData.date = items.get(10).toString();
					result.add(reportData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}

	/**
	 * 获取盈利能力数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度
	 * @return
	 */
	public static List<ProfitData> getProfitData(int year, int quarter) {
		List<ProfitData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getProfitData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取盈利能力数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<ProfitData> getProfitData(int year, int quarter, int pageNo, int retryCount, int pause) {
		List<ProfitData> result = new ArrayList<ProfitData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.PROFIT_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
//					System.out.println(node.toString());
					ProfitData profitData = new ProfitData();
					List<JXNode> items = node.sel("//td//text()");
					profitData.code = items.get(0).toString();
					profitData.name = items.get(1).toString();
					profitData.roe = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					profitData.net_profit_ratio = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					profitData.gross_profit_rate = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					profitData.net_profits = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					profitData.eps = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					profitData.business_income = items.get(7).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(7).toString());
					profitData.bips = items.get(8).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(8).toString());
					result.add(profitData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}

	/**
	 * 获取营运能力数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度
	 * @return
	 */
	public static List<OperationData> getOperationData(int year, int quarter) {
		List<OperationData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getOperationData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取营运能力数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<OperationData> getOperationData(int year, int quarter, int pageNo, int retryCount, int pause) {
		List<OperationData> result = new ArrayList<OperationData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.OPERATION_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
//					System.out.println(node.toString());
					OperationData operationData = new OperationData();
					List<JXNode> items = node.sel("//td//text()");
					operationData.code = items.get(0).toString();
					operationData.name = items.get(1).toString();
					operationData.arturnover = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					operationData.arturndays = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					operationData.inventory_turnover = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					operationData.inventory_days = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					operationData.currentasset_turnover = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					operationData.currentasset_days = items.get(7).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(7).toString());
					result.add(operationData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}

	/**
	 * 获取成长能力数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度
	 * @return
	 */
	public static List<GrowthData> getGrowthData(int year, int quarter) {
		List<GrowthData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getGrowthData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取成长能力数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<GrowthData> getGrowthData(int year, int quarter, int pageNo, int retryCount, int pause) {
		List<GrowthData> result = new ArrayList<GrowthData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.GROWTH_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
//					System.out.println(node.toString());
					GrowthData growData = new GrowthData();
					List<JXNode> items = node.sel("//td//text()");
					growData.code = items.get(0).toString();
					growData.name = items.get(1).toString();
					growData.mbrg = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					growData.nprg = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					growData.nav = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					growData.targ = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					growData.epsg = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					growData.seg = items.get(7).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(7).toString());
					result.add(growData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}

	/**
	 * 获取偿债能力数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度 说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @return
	 */
	public static List<DebtpayingData> getDebtpayingData(int year, int quarter) {
		List<DebtpayingData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getDebtpayingData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取偿债能力数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<DebtpayingData> getDebtpayingData(int year, int quarter, int pageNo, int retryCount, int pause) {
		List<DebtpayingData> result = new ArrayList<DebtpayingData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.DEBTPAYING_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
//					System.out.println(node.toString());
					DebtpayingData debtpayingData = new DebtpayingData();
					List<JXNode> items = node.sel("//td//text()");
					debtpayingData.code = items.get(0).toString();
					debtpayingData.name = items.get(1).toString();
					debtpayingData.currentRatio = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					debtpayingData.quickRatio = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					debtpayingData.cashRatio = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					debtpayingData.icRatio = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					debtpayingData.sheqRatio = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					debtpayingData.adRatio = items.get(7).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(7).toString());
					result.add(debtpayingData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}

	/**
	 * 获取现金流量数据，不支持递归抓取
	 * 
	 * @param year    int 年度 e.g:2014
	 * @param quarter int 季度 :1、2、3、4，只能输入这4个季度 说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @return
	 */
	public static List<CashFlowData> getCashFlowData(int year, int quarter) {
		List<CashFlowData> result = null;
		if (Cons.checkInput(year, quarter)) {
			result = getCashFlowData(year, quarter, 1, 3, 100);
		}
		return result;
	}

	/**
	 * 获取现金流量数据，不支持递归抓取
	 * 
	 * @param year       int 年度 e.g:2014
	 * @param quarter    int 季度 :1、2、3、4，只能输入这4个季度
	 *                   说明：由于是从网站获取的数据，需要一页页抓取，速度取决于您当前网络速度
	 * @param pageNo     int 第几页
	 * @param retryCount int 重试次数
	 * @param pause      抓取间隔时间（毫秒）
	 * @return 返回数据列表
	 * @throws IOError
	 */
	public static List<CashFlowData> getCashFlowData(int year, int quarter, int pageNo, int retryCount, int pause) {
		List<CashFlowData> result = new ArrayList<CashFlowData>();
		for (int i = 0; i < retryCount; i++) {
			try {
				String url = String.format(Cons.URL.CASHFLOW_URL.value, Cons.P_TYPE.http.value,
						Cons.DOMAINS.vsf.value, Cons.PAGES.fd.value, year, quarter, pageNo,
						Cons.PAGE_NUM[1]);
				String text = HTTParty.get(url, "GBK");

				JXDocument doc = JXDocument.create(text);
				List<JXNode> nodes = doc.selN("//table[@class=\"list_table\"]//tr");
				nodes.remove(0); // 去除标题列
				for (JXNode node : nodes) {
//					System.out.println(node.toString());
					CashFlowData cashFlowData = new CashFlowData();
					List<JXNode> items = node.sel("//td//text()");
					cashFlowData.code = items.get(0).toString();
					cashFlowData.name = items.get(1).toString();
					cashFlowData.cfSales = items.get(2).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(2).toString());
					cashFlowData.rateOfReturn = items.get(3).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(3).toString());
					cashFlowData.cfNm = items.get(4).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(4).toString());
					cashFlowData.cfLiabilities = items.get(5).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(5).toString());
					cashFlowData.cashFlowRatio = items.get(6).toString().equals("--") ? Double.NaN
							: Double.parseDouble(items.get(6).toString());
					result.add(cashFlowData);
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
		throw new IOError(Cons.Msg.NETWORK_URL_ERROR_MSG.toString());
	}
}
