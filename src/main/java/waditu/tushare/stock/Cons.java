package waditu.tushare.stock;

import java.util.Arrays;

import waditu.tushare.exception.TypeError;

/**
 * Cons类对应 ushare/stock/cons.py 文件。
 * 
 * @author wenhq
 *
 */
public class Cons implements ICons {

	/**
	 * 检查输入参数，判断年和季度范围，迁移自{@code def _check_input(year, quarter)}方法。
	 * 
	 * @param year
	 * @param quarter
	 * @return
	 * @throws TypeError
	 */
	public static boolean checkInput(int year, int quarter) throws TypeError {
		boolean result = false;
		if (year < 1989) {
			throw new TypeError(ICons.Msg.DATE_CHK_MSG.toString());
		} else if (quarter < 1 || quarter > 4) {
			throw new TypeError(ICons.Msg.DATE_CHK_Q_MSG.toString());
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * 生成symbol代码标志，对股指代码进行处理，转换，以便更好的获取数据。迁移自{@code def _code_to_symbol(code)}方法。
	 * 
	 * @param code 股指代码
	 * @return 格式处理后的股指代码
	 */
	public static String codeToSymbol(String code) {
		code = code.trim();
		if (Arrays.asList(INDEX_LABELS).contains(code)) {
			return INDEX_LIST.valueOf(code).value;
		} else if ("gb_".equalsIgnoreCase(code.substring(0, 3))) {
			return code;
		} else {
			if (code.trim().length() != 6) {
				return code;
			} else {
				String[] stockList = { "5", "6", "9", "11", "13" };
				String firstStr = code.substring(0, 1);
				String secondStr = code.substring(0,2);
				if (Arrays.asList(stockList).contains(firstStr) || Arrays.asList(stockList).contains(secondStr)) {
					return String.format("sh%s", code);
				} else {
					return String.format("sz%s", code);
				}
			}
		}
	}

	/**
	 * 生成symbol代码标志，对股指代码进行处理，转换，以便更好的获取数据。迁移自{@code def _code_to_symbol_dgt(code)}方法。
	 * 
	 * @param code 股指代码
	 * @return 格式处理后的股指代码
	 */
	public static String codeToSymbolDgt(String code) {
		code = code.trim();
		if (Arrays.asList(INDEX_LABELS).contains(code)) {
			return INDEX_LIST.valueOf(code).value;
		} else {
			if (code.trim().length() != 6) {
				return code;
			} else {
				String[] stockList = { "5", "6", "9" };
				String firstStr = code.substring(0, 1);
				if (Arrays.asList(stockList).contains(firstStr)) {
					return String.format("0%s", code);
				} else {
					return String.format("1%s", code);
				}
			}
		}
	}
}
