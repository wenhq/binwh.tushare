package waditu.tushare.stock;

/**
 * 将ushare/stock/cons.py中的常量定义用接口文件定义。
 * 
 * @author wenhq
 * 
 */
public interface ICons {

	/**
	 * K线标签：日K，周K，月K
	 */
	public final static String[] K_LABELS = { "D", "W", "M" };

	/**
	 * 分钟线标签：5，15，30，60
	 */
	public final static String[] K_MIN_LABELS = { "5", "15", "30", "60" };

	/**
	 * K线标签类型，用于URL中
	 */
	public static enum K_TYPE {
		D("akdaily"), W("akweekly"), M("akmonthly");

		public final String value;

		private K_TYPE(String value) {
			this.value = value;
		}
	}

	public final static String[] INDEX_LABELS = { "sh", "sz", "hs300", "sz50", "cyb", "zxb", "zx300", "zh500" };

	/**
	 * URL中指数代码转换
	 *
	 */
	public static enum INDEX_LIST {
		sh("sh000001"), sz("sz399001"), hs300("sz399300"), sz50("sh000016"), zxb("sz399005"), cyb("sz399006"),
		zx300("sz399008"), zh500("sh000905");

		public final String value;

		private INDEX_LIST(String value) {
			this.value = value;
		}
	}

	public static enum P_TYPE {
		http("http://"), ftp("ftp://");

		public final String value;

		private P_TYPE(String value) {
			this.value = value;
		}
	}

	public final static int[] PAGE_NUM = { 40, 60, 80, 100 };

	public static enum DOMAINS {
		sina("sina.com.cn"), sinahq("sinajs.cn"), ifeng("ifeng.com"), sf("finance.sina.com.cn"),
		vsf("vip.stock.finance.sina.com.cn"), idx("www.csindex.com.cn"), netease("money.163.com"), em("eastmoney.com"),
		sseq("query.sse.com.cn"), sse("www.sse.com.cn"), szse("www.szse.cn"), oss("file.tushare.org"),
		idxip("115.29.204.48"), shibor("www.shibor.org"), mbox("www.cbooo.cn"), tt("gtimg.cn"), gw("gw.com.cn"),
		v500("value500.com"), sstar("stock.stockstar.com");

		public final String value;

		private DOMAINS(String value) {
			this.value = value;
		}
	}

	public static enum PAGES {
		sina("sina.com.cn"), fd("index.phtml"), dl("downxls.php"), jv("json_v2.php"), cpt("newFLJK.php"),
		ids("newSinaHy.php"), lnews("rollnews_ch_out_interface.php"), ntinfo("vCB_BulletinGather.php"),
		hs300b("000300cons.xls"), hs300w("000300closeweight.xls"), sz50b("000016cons.xls"), dp("all_fpya.php"),
		dp163("fpyg.html"), emxsg("JS.aspx"), fh163("jjcgph.php"), newstock("vRPD_NewStockIssue.php"),
		zz500b("000905cons.xls"), zz500wt("000905closeweight.xls"), t_ticks("vMS_tradedetail.php"), dw("downLoad.html"),
		qmd("queryMargin.do"), szsefc("ShowReport.szse"), ssecq("commonQuery.do"), sinadd("json_v2.php/CN_Bill.GetBillList"),
		ids_sw("SwHy.php"), idx("index.php"), index("index.html");

		public final String value;

		private PAGES(String value) {
			this.value = value;
		}
	}

	public static enum URL {
		TICK_PRICE_URL("%smarket.%s/%s?date=%s&symbol=%s"),
		TICK_PRICE_URL_TT("%sstock.%s/data/%s?appn=detail&action=download&c=%s&d=%s"),
		TICK_PRICE_URL_NT("%squotes.%s/cjmx/%s/%s/%s.xls"), DAY_PRICE_URL("%sapi.finance.%s/%s/?code=%s&type=last"),
		DAY_PRICE_MIN_URL("%sapi.finance.%s/akmin?scode=%s&type=%s"),
		REPORT_URL(
				"%s%s/q/go.php/vFinanceAnalyze/kind/mainindex/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		PROFIT_URL("%s%s/q/go.php/vFinanceAnalyze/kind/profit/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		OPERATION_URL(
				"%s%s/q/go.php/vFinanceAnalyze/kind/operation/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		GROWTH_URL("%s%s/q/go.php/vFinanceAnalyze/kind/grow/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		DEBTPAYING_URL(
				"%s%s/q/go.php/vFinanceAnalyze/kind/debtpaying/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		CASHFLOW_URL(
				"%s%s/q/go.php/vFinanceAnalyze/kind/cashflow/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		ALL_STOCK_BASICS_FILE(P_TYPE.http.value + DOMAINS.oss.value + "/tsdata/%sall%s.csv"),
		SINA_DD("%s%s/quotes_service/api/%s?symbol=%s&num=60&page=1&sort=ticktime&asc=0&volume=%s&amount=0&type=0&day=%s");

		public final String value;

		private URL(String value) {
			this.value = value;
		}
	}

	public final static String[] TICK_SRCS = { "sn", "tt", "nt" };

	
	public static enum SINA_DD_COLS {
		symbol, name, ticktime, price, volume, prev_price, kind
    }
	
	public static enum Msg {
		NETWORK_URL_ERROR_MSG("获取失败，请检查网络"), DATE_CHK_MSG("年度输入错误：请输入1989年以后的年份数字，格式：YYYY"),
		DATE_CHK_Q_MSG("季度输入错误：请输入1、2、3或4数字"), TICK_SRC_ERROR("数据源代码只能输入sn,tt,nt其中之一");

		public final String value;

		private Msg(String value) {
			this.value = value;
		}
	}
}
