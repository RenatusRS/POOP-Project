import java.util.ArrayList;

public class Candle {
	private final int timestamp;
	private final double open;
	private final double close;
	private final double low;
	private final double high;

	public Candle(int timestamp, double open, double close, double low, double high) {
		this.timestamp = timestamp;
		this.open = open;
		this.close = close;
		this.low = low;
		this.high = high;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public double getOpen() {
		return open;
	}

	public double getClose() {
		return close;
	}

	public double getLow() {
		return low;
	}

	public double getHigh() {
		return high;
	}

	public static void main(String[] args) {
		ArrayList<Candle> candles = new ArrayList<Candle>() {
			{
				add(new Candle(1, 1, 1, 1,1));
				add(new Candle(2, 2, 2, 2,2));
				add(new Candle(3, 3, 3, 3,3));
				add(new Candle(4, 4, 4, 4,4));
				add(new Candle(5, 5, 5, 5,5));
				add(new Candle(6, 6, 6, 6,6));
				add(new Candle(7, 7, 7, 7,7));
				add(new Candle(8, 8, 8, 8,8));
			}
		};

		ArrayList <Double> result = new Trader().EMAcalculate(candles,3,3+1);

		System.out.println(result);
	}
}
