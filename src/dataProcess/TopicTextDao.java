package dataProcess;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="topicTextDAO")
public class TopicTextDao {
    private String A_ID;
    private String A_URL;
    private String A_TITLE;
    private String A_CONTENT;
    private String A_MODEL;
    private String NE_MODEL;
    private long A_CRAWLTIME;
    private int A_CLASS_ID;
    private double A_POLAR;
    private double A_T_SIM;
    private long A_ADD_TIME;
    private String TOPIC_ID;
    private int T_CLASS_ID;
    private Map<String, Double> KEY_WORDS;

    
    public String getA_ID() {
		return A_ID;
	}

	public void setA_ID(String a_ID) {
		A_ID = a_ID;
	}

	public String getA_MODEL() {
		return A_MODEL;
	}

	public void setA_MODEL(String a_MODEL) {
		A_MODEL = a_MODEL;
	}

	public String getNE_MODEL() {
		return NE_MODEL;
	}

	public void setNE_MODEL(String nE_MODEL) {
		NE_MODEL = nE_MODEL;
	}

	public long getA_ADD_TIME() {
		return A_ADD_TIME;
	}

	public void setA_ADD_TIME(long a_ADD_TIME) {
		A_ADD_TIME = a_ADD_TIME;
	}

	public String getTOPIC_ID() {
		return TOPIC_ID;
	}

	public void setTOPIC_ID(String tOPIC_ID) {
		TOPIC_ID = tOPIC_ID;
	}

	public int getT_CLASS_ID() {
		return T_CLASS_ID;
	}

	public void setT_CLASS_ID(int t_CLASS_ID) {
		T_CLASS_ID = t_CLASS_ID;
	}

	public Map<String, Double> getKEY_WORDS() {
		return KEY_WORDS;
	}

	public void setKEY_WORDS(Map<String, Double> kEY_WORDS) {
		KEY_WORDS = kEY_WORDS;
	}

	public String getA_URL() {
        return A_URL;
    }

    public void setA_URL(String a_URL) {
        A_URL = a_URL;
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

    public long getA_CRAWLTIME() {
        return A_CRAWLTIME;
    }

    public void setA_CRAWLTIME(long a_CRAWLTIME) {
        A_CRAWLTIME = a_CRAWLTIME;
    }

    public double getA_POLAR() {
        return A_POLAR;
    }

    public void setA_POLAR(double a_POLAR) {
        A_POLAR = a_POLAR;
    }

    public int getA_CLASS_ID() {
        return A_CLASS_ID;
    }

    public void setA_CLASS_ID(int a_CLASS_ID) {
        A_CLASS_ID = a_CLASS_ID;
    }

    public double getA_T_SIM() {
        return A_T_SIM;
    }

    public void setA_T_SIM(double a_T_SIM) {
        A_T_SIM = a_T_SIM;
    }
}
