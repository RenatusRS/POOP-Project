import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("SqlResolve")
public class SQL {
	private final Connection db;

	public SQL(String path) throws SQLException {
		db = DriverManager.getConnection("jdbc:sqlite:" + path);
	}

	public void updateMoney(String username, double amount) throws SQLException {
		db.createStatement().executeUpdate("UPDATE Korisnici SET amount = amount + %s WHERE username = '%s'".formatted(amount, username));
	}

	public void addStock(String username, String symbol, double cost, int amount) throws SQLException {
		db.createStatement().executeUpdate("INSERT INTO Kupovine (username, symbol, cost, left) VALUES ('%s','%s',%s,%d)".formatted(username, symbol.toUpperCase(), cost, amount));
	}

	public void updateStock(String ID, int amount) throws SQLException {
		db.createStatement().executeUpdate("UPDATE Kupovine SET left = left + %d WHERE ID = %s".formatted(amount, ID));
	}

	public ResultSet getMostRecent() throws SQLException {
		return db.createStatement().executeQuery("SELECT * FROM Kupovine ORDER BY ID DESC LIMIT 1");
	}

	public double getCurrentMoney(String username) {
		try {
			ResultSet rs = db.createStatement().executeQuery("SELECT * FROM Korisnici WHERE username = '%s'".formatted(username));
			return rs.next() ? rs.getDouble(3) : -1;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return -1;
	}

	public ResultSet getStock(String username, String ID) throws SQLException {
		return db.createStatement().executeQuery("SELECT * FROM Kupovine WHERE ID = %s AND username = '%s'".formatted(ID, username));
	}

	public ResultSet getActiveStocks(String username) throws SQLException {
		return db.createStatement().executeQuery("SELECT * FROM Kupovine WHERE username = '%s' AND left > 0".formatted(username));
	}

	public boolean checkExistence(String username) throws SQLException {
		return db.createStatement().executeQuery("SELECT * FROM Korisnici WHERE username = '%s'".formatted(username)).next();
	}

	public boolean checkPassword(String username, String password) throws SQLException {
		return db.createStatement().executeQuery("SELECT * FROM Korisnici WHERE username = '%s' AND password = '%s'".formatted(username, password)).next();
	}

	public void addUser(String username, String password, double amount) throws SQLException {
		db.createStatement().executeUpdate("INSERT INTO Korisnici VALUES ('%s', '%s', '%s')".formatted(username, password, amount));

	}
}
