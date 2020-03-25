package waditu.tushare.entity;

import java.util.Date;

public class SinaDDData {
	/** 股票代码 */
	public String code;
	/** 股票名称 */
	public String name;
	/** 发生时间 */
	public Date tickTime;
	/** 成交价 */
	public Double price;
	/** 成交量(手) */
	public Double volume;
	/** 成交额(元) */
	public Double amount;
	/** 前一价格 */
	public Double prevPrice;
	/** 买卖盘性质 */
	public String kind;

}
