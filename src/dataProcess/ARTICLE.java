package dataProcess;

public class ARTICLE {
	private String A_URL;
	private String A_TITLE;
	private String A_CONTENT;
	private String A_CRAWLTIME;
	private double A_POLAR;
	private double A_T_SIM;
	private double A_POLARITY;

	private long CAL_TIME_KEY;

	public String getA_URL() {
		return A_URL;
	}
	
	public void setA_URL(String aURL) {
		A_URL = aURL;
	}
	
	public String getA_TITLE() {
		return A_TITLE;
	}
	
	public void setA_TITLE(String aTITLE) {
		A_TITLE = aTITLE;
	}
	
	public String getA_CONTENT() {
		return A_CONTENT;
	}
	
	public void setA_CONTENT(String aCONTENT) {
		A_CONTENT = aCONTENT;
	}
	
	
	public String getA_CRAWLTIME() {
		return A_CRAWLTIME;
	}

	public void setA_CRAWLTIME(String a_CRAWLTIME) {
		A_CRAWLTIME = a_CRAWLTIME;
	}

	public double getA_POLAR() {
		return A_POLAR;
	}
	
	public void setA_POLAR(double aPOLAR) {
		A_POLAR = aPOLAR;
	}
	
	public double getA_T_SIM() {
		return A_T_SIM;
	}
	
	public void setA_T_SIM(double aTSIM) {
		A_T_SIM = aTSIM;
	}

	public double getA_POLARITY() {
		return A_POLARITY;
	}

	public void setA_POLARITY(double aPOLARITY) {
		A_POLARITY = aPOLARITY;
	}

	public long getCAL_TIME_KEY() {
		return CAL_TIME_KEY;
	}

	public void setCAL_TIME_KEY(long CAL_TIME_KEY) {
		this.CAL_TIME_KEY = CAL_TIME_KEY;
	}
}
