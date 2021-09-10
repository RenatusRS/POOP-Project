import StockView.exceptions.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class StockView extends Frame {

	private final SQL sql;
	private String username = "test";

	private final GridBagLayout gbl = new GridBagLayout();
	private final GridBagConstraints gbc = new GridBagConstraints();
	private final Panel control = new Panel(gbl);
	private final Chart pogled = new Chart(this);

	protected boolean MAstatus = true;
	protected boolean EMAstatus = true;
	private int n;

	protected Choice choiceInterval = new Choice();

	Label labelOpen = new Label("O: -,--");
	Label labelClose = new Label("C: -,--");
	Label labelHigh = new Label("H: -,--");
	Label labelLow = new Label("L: -,--");

	Label labelMA = new Label("MA: -,--");
	Label labelEMA = new Label("EMA: -,--");

	private void gridAdd(Component component, int gridy, int gridx, int gridwidth, int width, int height, int padding) {
		gbc.gridy = gridy;
		gbc.gridx = gridx;

		gbc.gridwidth = gridwidth;

		gbc.ipadx = width;
		gbc.ipady = height;

		gbc.weighty = 1;

		gbc.insets = new Insets(padding, 0, 0, 0);

		gbl.setConstraints(component, gbc);
		control.add(component);
	}

	private StockView() {
		setLocation(250, 250);
		setTitle("StockVIEW");

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.white);
		tabbedPane.add("Prikaz", populateViewPane());

		SQL sqlTemp = null;
		try {
			sqlTemp = new SQL("Berza.db");
		} catch (SQLException e) {
			System.out.println("Neuspesno vezivanje sa bazom: " + e.getMessage());
		}
		sql = sqlTemp;

		if (sql != null) {
			loginForm();
			if (!username.equals("test")) tabbedPane.add("Trgovina", populateTransactionPane());
		}

		add(tabbedPane, BorderLayout.CENTER);
		pack();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitConfirmation();
			}
		});

		setVisible(true);
	}

	private Panel populateViewPane() {
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 1;
		gbc.weightx = 1;

		Panel tabbedPanelMain = new Panel(new BorderLayout());

		control.setBackground(Color.white);

		pogled.setPreferredSize(new Dimension(800, 450));

		Label labelStock = new Label("/");
		Label labelCurrentPrice = new Label("-,--");

		setFont(new Font("Calibri", Font.PLAIN, 15));
		labelStock.setFont(new Font("Calibri", Font.BOLD, 25));
		labelCurrentPrice.setFont(new Font("Calibri", Font.BOLD, 25));

		labelStock.setAlignment(Label.CENTER);
		labelCurrentPrice.setAlignment(Label.CENTER);

		labelOpen.setAlignment(Label.CENTER);
		labelClose.setAlignment(Label.CENTER);
		labelHigh.setAlignment(Label.CENTER);
		labelLow.setAlignment(Label.CENTER);

		labelMA.setAlignment(Label.CENTER);
		labelEMA.setAlignment(Label.CENTER);

		TextField textStock = new TextField("TSLA");
		TextField textN = new TextField("5");

		Panel panelSymbolN = new Panel(new GridLayout(1, 2, 0, 0));

		Panel panelSymbol = new Panel();
		Panel panelN = new Panel();

		panelSymbol.add(new Label("Simbol:"));
		panelSymbol.add(textStock);

		panelN.add(new Label("N:"));
		panelN.add(textN);

		panelSymbolN.add(panelSymbol);
		panelSymbolN.add(panelN);

		Choice choiceFromYear = new Choice();
		Choice choiceToYear = new Choice();

		for (int i = 2021; i > 1970; i--) {
			choiceFromYear.add(String.valueOf(i));
			choiceToYear.add(String.valueOf(i));
		}

		Choice choiceFromMonth = new Choice();
		Choice choiceToMonth = new Choice();

		for (int i = 12; i > 0; i--) {
			choiceFromMonth.add(String.valueOf(i));
			choiceToMonth.add(String.valueOf(i));
		}

		Choice choiceFromDay = new Choice();
		Choice choiceToDay = new Choice();

		for (int i = 31; i > 0; i--) {
			choiceFromDay.add(String.valueOf(i));
			choiceToDay.add(String.valueOf(i));
		}

		Choice choiceFromHour = new Choice();
		Choice choiceToHour = new Choice();

		for (int i = 23; i > -1; i--) {
			choiceFromHour.add(String.valueOf(i));
			choiceToHour.add(String.valueOf(i));
		}

		Choice choiceFromMinute = new Choice();
		Choice choiceToMinute = new Choice();

		for (int i = 59; i > -1; i--) {
			choiceFromMinute.add(String.valueOf(i));
			choiceToMinute.add(String.valueOf(i));
		}

		Label labelPeriod = new Label("Period");
		labelPeriod.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

		Panel panelStartDate = new Panel();
		panelStartDate.add(choiceFromDay);
		panelStartDate.add(choiceFromMonth);
		panelStartDate.add(choiceFromYear);
		panelStartDate.add(choiceFromHour);
		panelStartDate.add(choiceFromMinute);

		Panel panelEndDate = new Panel();
		panelEndDate.add(choiceToDay);
		panelEndDate.add(choiceToMonth);
		panelEndDate.add(choiceToYear);
		panelEndDate.add(choiceToHour);
		panelEndDate.add(choiceToMinute);

		choiceInterval.add("1m");
		choiceInterval.add("2m");
		choiceInterval.add("5m");
		choiceInterval.add("15m");
		choiceInterval.add("30m");
		choiceInterval.add("1h");
		choiceInterval.add("1d");
		choiceInterval.add("5d");
		choiceInterval.add("1wk");
		choiceInterval.add("1mo");
		choiceInterval.add("3mo");
		choiceInterval.select(5);

		Label labelError = new Label();
		labelError.setForeground(Color.red);

		Button buttonPrikaz = new Button("Prikaz");

		gridAdd(labelStock, 0, 0, 2, 50, 50, 0);
		gridAdd(labelCurrentPrice, 0, 2, 2, 50, 50, 0);

		gridAdd(labelOpen, 1, 0, 1, 25, 30, 2);
		gridAdd(labelClose, 1, 1, 1, 25, 30, 2);
		gridAdd(labelLow, 1, 2, 1, 25, 30, 2);
		gridAdd(labelHigh, 1, 3, 1, 25, 30, 2);

		gridAdd(labelMA, 2, 0, 2, 50, 30, 0);
		gridAdd(labelEMA, 2, 2, 2, 50, 30, 0);

		panelSymbol.setPreferredSize(new Dimension(75, 25));
		panelN.setPreferredSize(new Dimension(25, 25));

		gridAdd(panelSymbolN, 3, 0, 4, 100, 20, 2);

		gridAdd(labelPeriod, 4, 0, 4, 50, 10, 2);
		gridAdd(panelStartDate, 5, 0, 4, 50, 10, 0);
		gridAdd(panelEndDate, 6, 0, 4, 50, 10, 0);

		Panel panelInterval = new Panel(new FlowLayout(FlowLayout.CENTER));
		panelInterval.add(new Label("Interval"));
		panelInterval.add(choiceInterval);

		gridAdd(panelInterval, 7, 0, 4, 50, 10, 2);

		gridAdd(labelError, 8, 0, 4, 100, 10, 2);

		gridAdd(buttonPrikaz, 9, 0, 4, 50, 20, 0);

		tabbedPanelMain.add(pogled, BorderLayout.CENTER);
		tabbedPanelMain.add(control, BorderLayout.EAST);

		Component[] components = control.getComponents();
		for (Component component : components) {
			component.setBackground(Util.gray);
		}

		labelMA.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (MAstatus ^= true) labelMA.setBackground(Util.gray);
				else {
					labelMA.setBackground(Color.gray);
					labelMA.setText("MA: -,--");
				}

				pogled.repaint();
				textN.setEnabled(MAstatus || EMAstatus);
			}
		});

		labelEMA.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (EMAstatus ^= true) labelEMA.setBackground(Util.gray);
				else {
					labelEMA.setBackground(Color.gray);
					labelEMA.setText("EMA: -,--");
				}

				pogled.repaint();
				textN.setEnabled(MAstatus || EMAstatus);
			}
		});

		buttonPrikaz.addActionListener((ae) -> {

			try {
				if (labelStock.getText().isBlank()) throw new GPrazanSimbol();

				if (MAstatus || EMAstatus) n = Integer.parseInt(textN.getText());

				long currTime = System.currentTimeMillis() / 1000L;
				long time1 = new SimpleDateFormat("yyyy MM dd HH mm").parse(choiceFromYear.getSelectedItem() + ' ' + choiceFromMonth.getSelectedItem() + ' ' + choiceFromDay.getSelectedItem() + ' ' + choiceFromHour.getSelectedItem() + ' ' + choiceFromMinute.getSelectedItem()).getTime() / 1000L;
				long time2 = new SimpleDateFormat("yyyy MM dd HH mm").parse(choiceToYear.getSelectedItem() + ' ' + choiceToMonth.getSelectedItem() + ' ' + choiceToDay.getSelectedItem() + ' ' + choiceToHour.getSelectedItem() + ' ' + choiceToMinute.getSelectedItem()).getTime() / 1000L;

				if (time2 < time1) {
					long temp = time1;
					time1 = time2;
					time2 = temp;
				}

				if (currTime < time1) throw new GVanSadasnjosti();

				if (choiceInterval.getSelectedItem().equals("1h") && currTime - 62200000 > time1 ||
						choiceInterval.getSelectedItem().equals("1m") && currTime - 2629000 > time1 && time2 - time1 > 604000 ||
						(currTime - 5259000 > time1 &&
								choiceInterval.getSelectedItem().equals("2m") ||
								choiceInterval.getSelectedItem().equals("5m") ||
								choiceInterval.getSelectedItem().equals("15m") ||
								choiceInterval.getSelectedItem().equals("30m"))
				) throw new GLosPeriod(choiceInterval.getSelectedItem());

				Trader mm = new Trader();
				ArrayList<Candle> candles;

				candles = mm.getStocksInterval(textStock.getText(), time1, time2, choiceInterval.getSelectedItem());

				if (candles.isEmpty()) throw new GSimbolNePostoji();
				if (candles.size() == 1) throw new GPrekratkoVreme();

				ArrayList<Double> MAlist = null, EMAlist = null;

				if (MAstatus || EMAstatus) {
					ArrayList<Candle> stocksN = new ArrayList<>();

					for (int i = 1; i < 4; i++) {
						time1 -= mm.choiceConvert(choiceInterval.getSelectedItem()) * n * 2L * i;
						stocksN = mm.getStocksInterval(textStock.getText(), time1, time2, choiceInterval.getSelectedItem());
						if (stocksN.size() >= candles.size() + n + 1) break;
					}

					if (stocksN.size() < candles.size() + n + 1) throw new GneuspesnoN();

					if (MAstatus) MAlist = mm.MAcalculate(stocksN, n, candles.size() + 1);
					if (EMAstatus) EMAlist = mm.EMAcalculate(stocksN, n, candles.size() + 1);
				}

				pogled.prikazi(candles, MAlist, EMAlist);
				labelError.setText("");
				labelStock.setText(textStock.getText());
				labelCurrentPrice.setText(String.format("%.2f", mm.getCurrentPrice(textStock.getText())));

			} catch (NumberFormatException e) {
				labelError.setText("Nepravilan N!");
			} catch (ParseException e) {
				labelError.setText("Nepravilan datum!");
			} catch (IllegalStateException e) {
				labelError.setText("Berza je zatvorena za ovaj period!");
			} catch (GVanSadasnjosti | GLosPeriod | GneuspesnoN | GSimbolNePostoji | GPrazanSimbol | GPrekratkoVreme e) {
				labelError.setText(e.getMessage());
			}
		});

		return tabbedPanelMain;
	}

	private Panel populateTransactionPane() {
		Panel transactionPane = new Panel(new BorderLayout());
		transactionPane.setBackground(Util.gray);

		JTable table = new JTable(new DefaultTableModel(new Object[]{"ID", "Akcija", "Stara cena", "Nova cena", "Razlika", "Procenat", "Preostalo"}, 0)) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int columnIndex) {
				JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);
				component.setBackground((Double) getValueAt(rowIndex, 4) < 0 ? Util.red : Util.green);

				return component;
			}
		};

		table.setEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);

		DefaultTableModel dm = (DefaultTableModel) table.getModel();

		HashMap<String, Double> hm = new HashMap<>();

		try {
			ResultSet rs = sql.getActiveStocks(username);
			double price;

			while (rs.next()) {
				if (!hm.containsKey(rs.getString(3))) {
					price = new Trader().getCurrentPrice(rs.getString(3));
					hm.put(rs.getString(3), price);
				}

				price = hm.get(rs.getString(3));
				if (price != -1) dm.addRow(new Object[]{rs.getString(1), rs.getString(3), rs.getDouble(4), price, (hm.get(rs.getString(3)) - rs.getDouble(4)), String.format("%.2f%%", 100 * hm.get(rs.getString(3)) / rs.getDouble(4)), rs.getInt(5)});
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		JScrollPane akcije = new JScrollPane(table);

		Panel panelUser = new Panel(new GridLayout(1, 2, 0, 0));

		Label labelUsername = new Label(username);
		labelUsername.setAlignment(Label.CENTER);
		labelUsername.setFont(new Font("Calibri", Font.PLAIN, Integer.min(transactionPane.getWidth(), transactionPane.getHeight()) / 15));

		Label labelMoney = new Label(String.format("%.2f$", sql.getCurrentMoney(username)));
		labelMoney.setAlignment(Label.CENTER);
		labelMoney.setFont(new Font("Calibri", Font.PLAIN, Integer.min(transactionPane.getWidth(), transactionPane.getHeight()) / 15));

		panelUser.add(labelUsername);
		panelUser.add(labelMoney);

		Panel panelTrgovina = new Panel(new GridLayout(2, 2, 0, 0));
		Panel panelKupovina = new Panel();
		Panel panelProdaja = new Panel();
		Panel panelKolicina = new Panel();
		Panel panelGreska = new Panel();

		panelKolicina.setLayout(new FlowLayout(FlowLayout.CENTER));

		Button buttonKupovina = new Button("Kupi");
		Button buttonProdaja = new Button("Prodaj");

		buttonKupovina.setPreferredSize(new Dimension(60, 20));
		buttonProdaja.setPreferredSize(new Dimension(60, 20));

		TextField textSimbol = new TextField(4);
		TextField textID = new TextField(4);
		TextField textKolicina = new TextField(4);

		Label labelError = new Label();
		Label labelKolicina = new Label("Kolicina");
		Label labelId = new Label("ID");
		Label labelSimbol = new Label("Simbol");

		labelId.setPreferredSize(new Dimension(60, 20));
		labelSimbol.setPreferredSize(new Dimension(60, 20));

		labelError.setForeground(Color.red);
		labelError.setPreferredSize(new Dimension(200, 20));

		panelKupovina.add(buttonKupovina);
		panelKupovina.add(labelSimbol);
		panelKupovina.add(textSimbol);

		panelProdaja.add(buttonProdaja);
		panelProdaja.add(labelId);
		panelProdaja.add(textID);

		panelGreska.add(labelError);

		panelKolicina.add(labelKolicina);
		panelKolicina.add(textKolicina);

		panelTrgovina.add(panelKupovina);
		panelTrgovina.add(panelGreska);
		panelTrgovina.add(panelProdaja);
		panelTrgovina.add(panelKolicina);

		transactionPane.add(panelUser, BorderLayout.NORTH);
		transactionPane.add(akcije, BorderLayout.CENTER);
		transactionPane.add(panelTrgovina, BorderLayout.SOUTH);

		buttonKupovina.addActionListener((ae) -> {
			try {
				double currPrice = new Trader().getCurrentPrice(textSimbol.getText());
				if (currPrice == -1) throw new GDelistovan();

				if (sql.getCurrentMoney(username) < Integer.parseInt(textKolicina.getText()) * currPrice)
					throw new GManjakPara();

				sql.updateMoney(username, Integer.parseInt(textKolicina.getText()) * -currPrice);
				sql.addStock(username, textSimbol.getText(), currPrice, Integer.parseInt(textKolicina.getText()));

				ResultSet rs = sql.getMostRecent();
				rs.next();

				if (!hm.containsKey(rs.getString(3)))
					hm.put(rs.getString(3), new Trader().getCurrentPrice(rs.getString(3)));
				dm.addRow(new Object[]{rs.getString(1), rs.getString(3), rs.getDouble(4), hm.get(rs.getString(3)), (hm.get(rs.getString(3)) - rs.getDouble(4)), String.format("%.2f%%", 100 * hm.get(rs.getString(3)) / rs.getDouble(4)), rs.getInt(5)});

				labelError.setText("");
				labelMoney.setText(String.format("%.2f$", sql.getCurrentMoney(username)));

			} catch (GManjakPara | GDelistovan e) {
				labelError.setText(e.getMessage());
			} catch (SQLException e) {
				labelError.setText("Nepravilan ID!");
			} catch (IllegalStateException e) {
				labelError.setText("Unesite simbol!");
			} catch (NumberFormatException e) {
				labelError.setText("Unesite kolicinu!");
			}

			revalidate();
		});

		buttonProdaja.addActionListener((ae) -> {
			try {
				ResultSet rs = sql.getStock(username, textID.getText());
				if (!rs.next()) throw new GNePosedujes();

				if (rs.getInt(5) < Integer.parseInt(textKolicina.getText())) throw new GManjakKolicine();

				double price = new Trader().getCurrentPrice(rs.getString(3));
				if (price == -1) throw new GDelistovan();

				sql.updateMoney(username, Integer.parseInt(textKolicina.getText()) * price);
				sql.updateStock(textID.getText(), -Integer.parseInt(textKolicina.getText()));

				for (int i = 0; i < dm.getRowCount(); i++) {
					if (dm.getValueAt(i, 0).equals(textID.getText())) {
						dm.setValueAt((int) dm.getValueAt(i, 6) - Integer.parseInt(textKolicina.getText()), i, 6);
						if ((int) dm.getValueAt(i, 6) == 0) dm.removeRow(i);
						table.repaint();
						break;
					}
				}

				labelError.setText("");
				labelMoney.setText(String.format("%.2f$", sql.getCurrentMoney(username)));

			} catch (SQLException e) {
				labelError.setText("Nepravilan ID!");
			} catch (GNePosedujes | GManjakKolicine | GDelistovan e) {
				labelError.setText(e.getMessage());
			} catch (NumberFormatException e) {
				labelError.setText("Nepravilna kolicina!");
			}
			revalidate();
		});

		return transactionPane;
	}

	public void updateLabels(Candle candle, double ma, double ema) {
		labelOpen.setText(String.format("O: %.2f", candle.getOpen()));
		labelClose.setText(String.format("C: %.2f", candle.getClose()));
		labelLow.setText(String.format("L: %.2f", candle.getLow()));
		labelHigh.setText(String.format("H: %.2f", candle.getHigh()));

		if (candle.getClose() > candle.getOpen()) {
			labelOpen.setForeground(Util.green);
			labelClose.setForeground(Util.green);
			labelLow.setForeground(Util.green);
			labelHigh.setForeground(Util.green);
		} else {
			labelOpen.setForeground(Util.red);
			labelClose.setForeground(Util.red);
			labelLow.setForeground(Util.red);
			labelHigh.setForeground(Util.red);
		}

		if (MAstatus) labelMA.setText(String.format("MA: %.2f", ma));
		if (EMAstatus) labelEMA.setText(String.format("EMA: %.2f", ema));
	}

	private void loginForm() {
		Dialog loginForm = new Dialog(this, Dialog.ModalityType.APPLICATION_MODAL);
		loginForm.setResizable(false);
		loginForm.setBounds(200, 300, 250, 350);

		loginForm.setTitle("Registracija");

		loginForm.setLayout(new GridLayout(0, 1, 10, 15));

		TextField textUsername = new TextField();
		TextField textPassword = new TextField();
		TextField textAmount = new TextField();
		Label labelError = new Label("", Label.CENTER);
		labelError.setForeground(Color.red);

		textAmount.setEnabled(false);

		textPassword.setEchoChar('*');

		Checkbox checkRegister = new Checkbox("Registracija?");

		checkRegister.addItemListener((ae) -> {
			textAmount.setEnabled(checkRegister.getState());
		});

		Button buttonPrijava = new Button("Prijava");

		buttonPrijava.addActionListener((ae) -> {
			try {
				if (textUsername.getText().isBlank()) throw new GNemaUsername();
				if (textPassword.getText().isEmpty()) throw new GNemaPassword();

				if (checkRegister.getState()) {
					Double.parseDouble(textAmount.getText());

					if (sql.checkExistence(textUsername.getText())) throw new GVecPostoji();

					sql.addUser(textUsername.getText(), textPassword.getText(), Double.parseDouble(textAmount.getText()));

				} else if (!sql.checkPassword(textUsername.getText(), textPassword.getText()))
					throw new GPogresnaLozinka();

				setTitle("StockVIEW - " + textUsername.getText());
				username = textUsername.getText();
				loginForm.dispose();

			} catch (SQLException e) {
				System.out.println(e.getMessage());
				labelError.setText("SQL greska!");
			} catch (NumberFormatException e) {
				labelError.setText("Unesite pocetnu sumu novca!");
			} catch (GVecPostoji | GPogresnaLozinka | GNemaUsername | GNemaPassword e) {
				labelError.setText(e.getMessage());
			}
		});

		loginForm.add(new Label("Korisničko ime"));
		loginForm.add(textUsername);
		loginForm.add(new Label("Lozinka"));
		loginForm.add(textPassword);
		loginForm.add(checkRegister);
		loginForm.add(new Label("Početna suma"));
		loginForm.add(textAmount);
		loginForm.add(labelError);
		loginForm.add(buttonPrijava);

		loginForm.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				loginForm.dispose();
				dispose();
			}
		});

		loginForm.setVisible(true);
	}

	private void quitConfirmation() {
		Dialog quitDialog = new Dialog(this, Dialog.ModalityType.APPLICATION_MODAL);
		quitDialog.setResizable(false);
		quitDialog.setTitle("Quit");

		quitDialog.setBounds((getX() + getWidth()) / 2, (getY() + getHeight()) / 2, 200, 100);

		quitDialog.setLayout(new GridLayout(2, 1, 0, 0));

		Label labelQuit = new Label("Zavrsiti sa radom?");
		labelQuit.setAlignment(Label.CENTER);

		quitDialog.add(labelQuit);

		Panel buttonPanel = new Panel();

		Button buttonDa = new Button("Da");
		Button buttonNe = new Button("Ne");

		buttonPanel.add(buttonNe);
		buttonPanel.add(buttonDa);

		quitDialog.add(buttonPanel);

		buttonDa.addActionListener((ae) -> {
			quitDialog.dispose();
			dispose();
		});

		buttonNe.addActionListener((ae) -> quitDialog.dispose());

		quitDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitDialog.dispose();
			}
		});

		quitDialog.setVisible(true);
	}


	public static void main(String[] args) {
		new StockView();
	}
}
