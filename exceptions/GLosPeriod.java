package StockView.exceptions;

public class GLosPeriod extends Exception {
	public GLosPeriod(String period) {
		super(period.equals("1h") ? "Period mora da bude u zadnje 2 godine!" : period.equals("1m") ? "Period mora da traje manje od nedelju dana i da bude u zadnjih mesec dana!" : "Period mora da bude u zadnja 2 meseca!");
	}
}
