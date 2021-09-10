import java.util.ArrayList;

public class Trader {

	public double getCurrentPrice(String symbol) {
		long currentTime = System.currentTimeMillis() / 1000L;
		ArrayList<Candle> candle = getStocksInterval(symbol, currentTime - 604800, currentTime, "1m");
		if (candle.size() == 0) return -1;
		return candle.get(candle.size() - 1).getClose();
	}

	public ArrayList<Candle> getStocksInterval(String symbol, long time1, long time2, String interval) {
		return new Parser().parse(new Crawler().getData("https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?period1=" + time1 + "&period2=" + time2 + "&interval=" + interval));
	}

	public ArrayList<Double> MAcalculate(ArrayList<Candle> stocksN, int n, int originalSize) {
		ArrayList<Double> result = new ArrayList<>();
		double add;

		for (int i = originalSize; i > 0; i--) {
			add = 0;
			for (int j = 0; j < n; j++) add += stocksN.get(stocksN.size() - i - j).getClose();
			result.add(add / n);
		}

		return result;
	}


	public ArrayList<Double> EMAcalculate(ArrayList<Candle> stocksN, int n, int originalSize) {
		ArrayList<Double> result = new ArrayList<>();
		double curr;

		for (int i = originalSize; i > 0; i--) {
			curr = stocksN.get(stocksN.size() - i - n + 1).getClose();

			for (int j = n - 1; j > 0; j--) {
				curr = stocksN.get(stocksN.size() - i - j + 1).getClose() * 2.0 / (n + 1) + curr * (1 - 2.0 / (n + 1));

			}

			result.add(curr);
		}

		return result;
	}

	public int choiceConvert(String choice) {
		switch (choice) {
			case ("1h") -> {
				return 3600;
			}
			case ("1d") -> {
				return 86400;
			}
			case ("5d") -> {
				return 432000;
			}
			case ("1wk") -> {
				return 604800;
			}
			case ("1mo") -> {
				return 2629743;
			}
			case ("3mo") -> {
				return 7889229;
			}
			default -> throw new IllegalStateException("Unexpected value: " + choice);
		}
	}

	public static void main(String[] args) {
		Trader t = new Trader();
		System.out.println(t.getCurrentPrice("SBAC"));
		System.out.println(t.getCurrentPrice("PSCD"));
		System.out.println(t.getCurrentPrice("EGOV"));
	}
}
