import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Chart extends JPanel {
	private final StockView owner;

	private double Yzoom;
	private int Xzoom;
	private int Xpos;

	private int includedLeft;
	private int includedRight;

	private int nextAction = 1;
	private double prevLoc;

	private ArrayList<Candle> candles;
	private ArrayList<Double> ma;
	private ArrayList<Double> ema;

	private final NavigableMap<Integer, Integer> map = new TreeMap<>();

	public Chart(StockView owner) {
		this.owner = owner;
		setFocusable(true);
		setBackground(Color.WHITE);

		addMouseWheelListener(e -> {
			if (candles == null) return;

			int zoomAmount = Integer.max((includedRight - includedLeft) / 15, 1);
			if (e.getWheelRotation() < 0 && 7 < includedRight - includedLeft) {
				Xzoom += zoomAmount;
			} else if (e.getWheelRotation() > 0 && includedLeft - zoomAmount > 0 && includedRight + zoomAmount < candles.size()) {
				Xzoom -= zoomAmount;
			} else if (e.getWheelRotation() > 0 && includedLeft - zoomAmount < 0 && includedRight + zoomAmount < candles.size()) {
				Xzoom -= zoomAmount / 2;
				Xpos += zoomAmount / 2;
			} else if (e.getWheelRotation() > 0 && includedLeft - zoomAmount > 0 && includedRight + zoomAmount > candles.size()) {
				Xzoom -= zoomAmount / 2;
				Xpos -= zoomAmount / 2;
			}
			repaint();
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (candles != null) nextAction = 1;
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (candles != null && nextAction-- == 0) {
					nextAction = 350 / (includedRight - includedLeft);
					if (prevLoc < e.getX() && includedLeft - 1 > 0) Xpos--;
					else if (prevLoc > e.getX() && includedRight + 1 < candles.size()) Xpos++;
					repaint();
				}

				prevLoc = e.getX();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (map.isEmpty()) return;

				int id = map.floorEntry(e.getX()).getValue();
				double maVal = ma != null ? ma.get(id + 1) : -1;
				double emaVal = ema != null ? ema.get(id + 1) : -1;

				owner.updateLabels(candles.get(id), maVal, emaVal);
			}
		});

		addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				switch (Character.toUpperCase(e.getKeyChar())) {
					case KeyEvent.VK_A -> {
						if (Yzoom < 15) {
							Yzoom += 0.15;
							repaint();
						}
					}
					case KeyEvent.VK_D -> {
						if (Yzoom > -10) {
							Yzoom -= 0.15;
							repaint();
						}
					}
				}
			}
		});
	}


	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Util.gray);
		if (candles == null) {
			for (int i = 0; i < 10; i++)
				g.drawLine(0, i * getHeight() / 10, getWidth(), i * getHeight() / 10);

			for (int i = 0; i < 15; i++)
				g.drawLine(i * getWidth() / 15, 0, i * getWidth() / 15, getHeight());

			g.fillRect((int) (getWidth() * 0.95), 0, (int) (getWidth() * 0.05 - 2), getHeight());
			g.fillRect(0, (int) (getHeight() * 0.95), getWidth() - 2, (int) (getHeight() * 0.05));

			return;
		}

		super.paintComponent(g);
		requestFocus();

		includedLeft = Xzoom + Xpos;
		includedRight = candles.size() - 1 - Xzoom + Xpos;
		int shown = includedRight - includedLeft;

		double lowest = candles.get(includedLeft).getLow();
		double highest = candles.get(includedLeft).getHigh();

		for (int i = includedLeft + 1; i < includedRight; i++) {
			lowest = Double.min(lowest, candles.get(i).getLow());
			highest = Double.max(highest, candles.get(i).getHigh());
		}

		lowest = lowest * 0.98 - Yzoom;
		highest = highest * 1.02 + Yzoom;

		double pixelW = getWidth() * 0.95 / shown;
		double pixelH = getHeight() / (highest - lowest);

		map.clear();
		for (int i = 0; i < shown; i++) map.put((int) (i * pixelW), i + includedLeft);
		if (map.isEmpty()) map.put(0, includedLeft);

		for (int i = 0; i < (highest / lowest) * 10; i++)
			g.drawLine(0, (int) (i * getHeight() / ((highest / lowest) * 10)), getWidth(), (int) (i * getHeight() / ((highest / lowest) * 10)));

		for (int i = 0; i < (includedRight - includedLeft) / 2; i++)
			g.drawLine(i * getWidth() / (shown / 2), 0, i * getWidth() / (shown / 2), getHeight());

		for (int i = includedLeft; i < includedRight; i++) {
			if (candles.get(i).getOpen() > candles.get(i).getClose()) g.setColor(Util.red);
			else g.setColor(Util.green);

			g.fillRect((int) ((i - includedLeft) * pixelW), (int) ((highest - Math.max(candles.get(i).getOpen(), candles.get(i).getClose())) * pixelH), (int) (pixelW), (int) (pixelH * Math.abs(candles.get(i).getClose() - candles.get(i).getOpen())));
			g.fillRect((int) ((i - includedLeft + 0.5) * pixelW), (int) ((highest - candles.get(i).getHigh()) * pixelH), 1, (int) (pixelH * (candles.get(i).getHigh() - candles.get(i).getLow())));

			if (owner.MAstatus) {
				g.setColor(Util.blue);
				g.drawLine((int) ((i - 1 - includedLeft + 0.5) * pixelW), (int) ((highest - ma.get(i)) * pixelH), (int) ((i - includedLeft + 0.5) * pixelW), (int) ((highest - ma.get(i + 1)) * pixelH));
			}

			if (owner.EMAstatus) {
				g.setColor(Util.yellow);
				g.drawLine((int) ((i - 1 - includedLeft + 0.5) * pixelW), (int) ((highest - ema.get(i)) * pixelH), (int) ((i - includedLeft + 0.5) * pixelW), (int) ((highest - ema.get(i + 1)) * pixelH));
			}
		}

		if (owner.MAstatus && includedRight != candles.size()) {
			g.setColor(Util.blue);
			g.drawLine((int) ((shown - 1 + 0.5) * pixelW), (int) ((highest - ma.get(includedRight)) * pixelH), (int) (shown * pixelW), (int) ((highest - ma.get(includedRight + 1)) * pixelH));
		}

		if (owner.EMAstatus && includedRight != candles.size()) {
			g.setColor(Util.yellow);
			g.drawLine((int) ((shown - 1 + 0.5) * pixelW), (int) ((highest - ema.get(includedRight)) * pixelH), (int) (shown * pixelW), (int) ((highest - ema.get(includedRight + 1)) * pixelH));
		}

		g.setColor(Util.gray);
		g.fillRect((int) (getWidth() * 0.95), 0, (int) (getWidth() * 0.05 - 2), getHeight());
		g.fillRect(0, (int) (getHeight() * 0.95), getWidth() - 2, (int) (getHeight() * 0.05));

		g.setColor(Color.black);
		g.setFont(new Font("Calibri", Font.PLAIN, getWidth() / 70));

		for (int i = 1; i < 15; i++) {
			g.drawString(String.format("%.2f", highest - ((highest - lowest) / 15) * i), (int) (getWidth() * 0.955), (getHeight() / 15) * i);
		}

		SimpleDateFormat df;
		for (int i = 1; i < 9; i++) {
			switch (owner.choiceInterval.getSelectedItem()) {
				case ("1h") -> {
					if (shown > 1000) df = new SimpleDateFormat("dd. MMM ''yy");
					else if (shown > 100) df = new SimpleDateFormat("dd. MMM HH:mm");
					else df = new SimpleDateFormat("dd. HH:mm");
				}
				case ("1d") -> {
					if (shown > 60) df = new SimpleDateFormat("dd MMM ''yy");
					else df = new SimpleDateFormat("dd. MMM");
				}
				case ("5d") -> {
					if (shown > 25) df = new SimpleDateFormat("dd. MMM ''yy");
					else df = new SimpleDateFormat("dd. MMM");
				}
				case ("1wk") -> {
					if (shown > 20) df = new SimpleDateFormat("dd. MMM ''yy");
					else df = new SimpleDateFormat("dd. MMM");
				}
				case ("1mo"), ("3mo") -> df = new SimpleDateFormat("MMM ''yy");
				default -> throw new IllegalStateException("Unexpected value: " + owner.choiceInterval.getSelectedItem());
			}


			int xVal = (int) ((getWidth() * 1.07 / 9) * i - getWidth() * 0.1);
			g.drawString(df.format(candles.get(map.floorEntry(xVal).getValue()).getTimestamp() * 1000L), xVal, (int) (getHeight() * 0.98));
		}
	}

	public void prikazi(ArrayList<Candle> candles, ArrayList<Double> ma, ArrayList<Double> ema) {
		this.candles = candles;
		this.ma = ma;
		this.ema = ema;
		Yzoom = 0;

		if (candles.size() > 50) {
			Xzoom = (candles.size() - 50) / 2;
			Xpos = (candles.size() - 50) / 2 + 1;
		} else {
			Xzoom = 0;
			Xpos = 0;
		}

		repaint();
	}
}
