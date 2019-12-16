package waditu.tushare.stock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import waditu.tushare.common.HTTParty;
import waditu.tushare.common.Utility;
import waditu.tushare.entity.StockBasicsData;

public class fundamental {

	public static List<StockBasicsData> getStockBasics(String date) {
		date = date.replaceAll("-", "");
		if (Integer.valueOf(date) < 20160809) {
			return null;
		}
		String datepre = date.equals("") ? "" : date.substring(0, 6) + "/";
		String url = String.format(Utility.ALL_STOCK_BASICS_FILE, datepre, date);
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

}
