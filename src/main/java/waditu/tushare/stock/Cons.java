package waditu.tushare.stock;

import java.util.Arrays;
import java.util.HashMap;

public class Cons {

	public final static String[] INDEX_LABELS = { "sh", "sz", "hs300", "sz50", "cyb", "zxb", "zx300", "zh500" };

	@SuppressWarnings("serial")
	public final static HashMap<String, String> INDEX_LIST = new HashMap<String, String>() {
		{
			put("sh", "sh000001");
			put("sz", "sz399001");
			put("hs300", "sz399300");
			put("sz50", "sh000016");
			put("zxb", "sz399005");
			put("cyb", "sz399006");
			put("zx300", "sz399008");
			put("zh500", "sh000905");
		}
	};

	/**
	 * 生成symbol代码标志，对股指代码进行处理，转换，以便更好的获取数据
	 * 
	 * @param code 股指代码
	 * @return 格式处理后的股指代码
	 */
	public static String codeToSymbol(String code) {
		code = code.trim();
		if (Arrays.asList(INDEX_LABELS).contains(code)) {
			return INDEX_LIST.get(code);
		} else if ("gb_".equalsIgnoreCase(code.substring(0, 3))) {
			return code;
		} else {
			if (code.trim().length() != 6) {
				return code;
			} else {
				String[] stockList = { "5", "6", "9" };
				String firstStr = code.substring(0, 1);
				return (Arrays.asList(stockList).contains(firstStr)) ? String.format("sh%s", code)
						: String.format("sz%s", code);
			}
		}
	}
}
