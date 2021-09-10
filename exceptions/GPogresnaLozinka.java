package StockView.exceptions;

public class GPogresnaLozinka extends Exception {
	public GPogresnaLozinka () {
		super("Uneli ste pogresno korisnicko ime ili lozinku!");
	}
}
