package waditu.tushare.entity;

public class ReportData {
	/** 股票代码 */
	public String code;
	/** 股票名称 */
	public String name;
	/** 每股收益(元) */
	public double eps;
	/** 每股收益同比(%) */
	public double epsrate;
	/** 每股净资产(元) */
	public double navps;
	/** 净资产收益率(%) */
	public double roe;
	/** 每股现金流量(元) */
	public double cps;
	/** 净利润(万元) */
	public double net_profits;
	/** 净利润同比(%) */
	public double net_profit_rate;
	/** 发布日期 */
	public String date;
}
