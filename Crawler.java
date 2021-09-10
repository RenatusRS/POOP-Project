public class Crawler {

	private native String crawler(String url);

	public String getData(String url) {
		System.loadLibrary("Crawler");
		return crawler(url);
	}

	public static void main(String[] args) {
		System.loadLibrary("Crawler");
		Crawler crawlerE = new Crawler();
		System.out.println(crawlerE.crawler("https://query1.finance.yahoo.com/v8/finance/chart/aapeewel?period1=1616072670&period2=1616075000&interval=1h"));
	}

}
