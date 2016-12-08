package dataProcess;

import java.util.List;

/**
 * Created by Chiu on 2015/11/2.
 */
public class HOT_TOPIC_ANALYSIS {
    private int T_DOC_COUNT;
    private int T_POS_DOC_COUNT;
    private int T_NEG_DOC_COUNT;
    private List<String> T_A_SAVEKEY;

    public int getT_DOC_COUNT() {
        return T_DOC_COUNT;
    }

    public void setT_DOC_COUNT(int t_DOC_COUNT) {
        T_DOC_COUNT = t_DOC_COUNT;
    }

    public int getT_POS_DOC_COUNT() {
        return T_POS_DOC_COUNT;
    }

    public void setT_POS_DOC_COUNT(int t_POS_DOC_COUNT) {
        T_POS_DOC_COUNT = t_POS_DOC_COUNT;
    }

    public int getT_NEG_DOC_COUNT() {
        return T_NEG_DOC_COUNT;
    }

    public void setT_NEG_DOC_COUNT(int t_NEG_DOC_COUNT) {
        T_NEG_DOC_COUNT = t_NEG_DOC_COUNT;
    }

    public List<String> getT_A_SAVEKEY() {
        return T_A_SAVEKEY;
    }

    public void setT_A_SAVEKEY(List<String> t_A_SAVEKEY) {
        T_A_SAVEKEY = t_A_SAVEKEY;
    }
}
