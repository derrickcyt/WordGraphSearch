package dataProcess;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Chiu on 2015/11/2.
 */
@Document(collection="mghottopics")
public class HotTopic_BackEnd {
    private String TOPIC_ID;//话题Id号
    private Map<String, TEXT_ARTICLE> T_ARTICLE;//话题相关的文章
    private String A_TITLE;
    private String T_TEXTMODEL;
    private String T_CREATETIME;
    private String T_UPDATETIME;
    private double T_HOT;
    private int T_CLASS_ID;
    private double T_POLAR;

    private Map<Integer, HOT_TOPIC_ANALYSIS> T_ANALYSIS; //话题分析结果

    public String getTOPIC_ID() {
        return TOPIC_ID;
    }

    public void setTOPIC_ID(String TOPIC_ID) {
        this.TOPIC_ID = TOPIC_ID;
    }

    public Map<String, TEXT_ARTICLE> getT_ARTICLE() {
        return T_ARTICLE;
    }

    public void setT_ARTICLE(Map<String, TEXT_ARTICLE> t_ARTICLE) {
        T_ARTICLE = t_ARTICLE;
    }

    public String getA_TITLE() {
        return A_TITLE;
    }

    public void setA_TITLE(String a_TITLE) {
        A_TITLE = a_TITLE;
    }

    public String getT_TEXTMODEL() {
        return T_TEXTMODEL;
    }

    public void setT_TEXTMODEL(String t_TEXTMODEL) {
        T_TEXTMODEL = t_TEXTMODEL;
    }

    public String getT_CREATETIME() {
        return T_CREATETIME;
    }

    public void setT_CREATETIME(String t_CREATETIME) {
        T_CREATETIME = t_CREATETIME;
    }

    public String getT_UPDATETIME() {
        return T_UPDATETIME;
    }

    public void setT_UPDATETIME(String t_UPDATETIME) {
        T_UPDATETIME = t_UPDATETIME;
    }

    public double getT_HOT() {
        return T_HOT;
    }

    public void setT_HOT(double t_HOT) {
        T_HOT = t_HOT;
    }

    public int getT_CLASS_ID() {
        return T_CLASS_ID;
    }

    public void setT_CLASS_ID(int t_CLASS_ID) {
        T_CLASS_ID = t_CLASS_ID;
    }

    public double getT_POLAR() {
        return T_POLAR;
    }

    public void setT_POLAR(double t_POLAR) {
        T_POLAR = t_POLAR;
    }

    public Map<Integer, HOT_TOPIC_ANALYSIS> getT_ANALYSIS() {
        return T_ANALYSIS;
    }

    public void setT_ANALYSIS(Map<Integer, HOT_TOPIC_ANALYSIS> t_ANALYSIS) {
        T_ANALYSIS = t_ANALYSIS;
    }
}
