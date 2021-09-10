import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	public ArrayList<Candle> parse(String input) throws IllegalStateException {
		ArrayList<Candle> result = new ArrayList<>();

		if (input.charAt(19) == 'n') return result;

		Matcher matcher = Pattern.compile("\"(\\w+)\":\\[(\\d.*?)]").matcher(input);

		matcher.find();
		String[] timestamps = matcher.group(2).split(","), opens = new String[0], closes = new String[0], highs = new String[0], lows = new String[0];

		while (matcher.find()) {
			switch (matcher.group(1)) {
				case "open" -> opens = matcher.group(2).split(",");
				case "close" -> closes = matcher.group(2).split(",");
				case "high" -> highs = matcher.group(2).split(",");
				case "low" -> lows = matcher.group(2).split(",");
			}
		}

		for (int i = 0; i < timestamps.length; i++) {
			try {
				result.add(new Candle(Integer.parseInt(timestamps[i]), Double.parseDouble(opens[i]), Double.parseDouble(closes[i]), Double.parseDouble(lows[i]), Double.parseDouble(highs[i])));
			} catch (NumberFormatException ignored) {
			}
		}

		return result;
	}

	public static void main(String[] args) {
		new Parser().parse(new Crawler().getData("https://query1.finance.yahoo.com/v8/finance/chart/aapl?period1=1616072670&period2=1616075000&interval=1h"));
	}
}
