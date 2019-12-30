package waditu.tushare.common;

public interface IConstants {

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
		qmd("queryMargin.do"), szsefc("ShowReport.szse"), ssecq("commonQuery.do"), sinadd("cn_bill_download.php"),
		ids_sw("SwHy.php"), idx("index.php"), index("index.html");

		public final String value;

		private PAGES(String value) {
			this.value = value;
		}
	}

	public static enum Url {
		REPORT_URL(
				"%s%s/q/go.php/vFinanceAnalyze/kind/mainindex/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s"),
		PROFIT_URL("%s%s/q/go.php/vFinanceAnalyze/kind/profit/%s?s_i=&s_a=&s_c=&reportdate=%s&quarter=%s&p=%s&num=%s");
		public final String value;

		private Url(String value) {
			this.value = value;
		}
	}

	public static enum Msg {
		NETWORK_URL_ERROR_MSG("获取失败，请检查网络"), DATE_CHK_MSG("年度输入错误：请输入1989年以后的年份数字，格式：YYYY"),
		DATE_CHK_Q_MSG("季度输入错误：请输入1、2、3或4数字");

		public final String value;

		private Msg(String value) {
			this.value = value;
		}
	}
}
