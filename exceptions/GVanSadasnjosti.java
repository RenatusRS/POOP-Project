package StockView.exceptions;

public class GVanSadasnjosti extends Exception{
	public GVanSadasnjosti () {
		super("Ceo opseg perioda je u buducnosti!");
	}
}
