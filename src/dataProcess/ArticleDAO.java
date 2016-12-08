package dataProcess;

public class ArticleDAO {
	
	private String A_TITLE;
	private String A_CONTENT;
	private String A_URL;
	private double A_POLARITY;
	private String A_CRAWLTIME;
	
	public ArticleDAO(String title,String text,String url,double polarity,String crawltime) {
		A_TITLE = title;
		A_CONTENT = text;
		A_URL = url;
		A_POLARITY = polarity;
		A_CRAWLTIME = crawltime;
	}
	
	public String getA_CRAWLTIME() {
		return A_CRAWLTIME;
	}

	public void setA_CRAWLTIME(String a_CRAWLTIME) {
		A_CRAWLTIME = a_CRAWLTIME;
	}

	public String getA_TITLE() {
		return A_TITLE;
	}
	public void setA_TITLE(String a_TITLE) {
		A_TITLE = a_TITLE;
	}
	public String getA_CONTENT() {
		return A_CONTENT;
	}
	public void setA_CONTENT(String a_CONTENT) {
		A_CONTENT = a_CONTENT;
	}
	public String getA_URL() {
		return A_URL;
	}
	public void setA_URL(String a_URL) {
		A_URL = a_URL;
	}

	public double getA_POLARITY() {
		return A_POLARITY;
	}

	public void setA_POLARITY(double a_POLARITY) {
		A_POLARITY = a_POLARITY;
	}

	
}
