package params;

public class Params {

	public static final String CHARSET_DEFAULT = "UTF-8";
	public static final String CHARSET_UTF8 = "UTF-8";
	public static final String CHARSET_GB2312 = "GB2312";
	public static final String FILE_DF = "clusterrelate/df";
	public static final String FILE_STOP_WORDS = "clusterrelate/stopword.txt";
	public static final String CHARSET_GBK = "GBK";

	public static final String ZOOKEEPER_QUORUM = "ibm-hadoop-namenode,ibm-hadoop-datanode-001,ibm-hadoop-datanode-002,ibm-hadoop-datanode-003,ibm-hadoop-datanode-004";
	public static final String HTABLE_FAM_INFO = "INFO";
	public static final String HTABLE_QUAL_ATITLE = "A_TITLE";
	public static final String HTABLE_QUAL_ATEXT = "A_TEXT";
	public static final String HTABLE_QUAL_AURL = "A_URL";
	public static final String HTABLE_QUAL_ATYPEID = "A_TYPE_ID";
	public static final String HTABLE_QUAL_ACLASSID = "A_CLASS_ID";
	public static final String HTABLE_QUAL_ACRAWLTIME = "A_CRAWLTIME";
	public static final String HTABLE_TABLE_ARTICLE = "hbarticle";
	public static final String HTABLE_FAM_RES = "RES";
	public static final String NLPIR_JNI_PATH = ":/home/hadoop/workspace/newscluster";
	
	public static final int CLUSTER_CONVERGENCE = 1;
	public static final double CLUSTER_SIM_THRESHOLD_DEFAULT = 0.5;
	public static final double CLUSTER_HOT_THRESHOLD_DEFAULT = 1;
	public static final int CLUSTER_CATORGORY_DEFAULT = 0;
	public static final long CLUSTER_TIME_INTERVAL_HALF_DAY = 1000 * 60 * 60 * 12;
	public static final long CLUSTER_TIME_INTERVAL_ONE_DAY = 1000 * 60 * 60 * 24;
	public static final long CLUSTER_TIME_INTERVAL_HALF_HOUR = 1000 * 60 * 30;
	public static final long CLUSTER_TIME_UNIT_ANALYSIS_UPDATE = CLUSTER_TIME_INTERVAL_ONE_DAY;
	public static final int CLUSTER_ROUND_LIMIT = 6;
	
	public static final String MONGODB_IP = "125.216.227.100";
	public static final int MONGODB_PORT = 27017;
	public static final String MONGODB_DB_DEFAULT = "yqjk";
	public static final String MONGODB_TABLE_DEFAULT = "mghottopics";
	public static final String MONGODB_TABLE_FRONTEND = "mghottopics_front";
	public static final String MONGODB_COND_GTE = "$gte";
	public static final String MONGODB_COND_LTE = "$lte";
	public static final String MONGODB_COND_SET = "$set";
	public static final String MONGODB_KEY_T_ID = "_id";
	public static final String MONGODB_KEY_T_ARTICLE = "T_ARTICLE";
	public static final String MONGODB_KEY_A_ID = "A_ID";
	public static final String MONGODB_KEY_A_URL = "A_URL";
	public static final String MONGODB_KEY_A_CONTENT = "A_CONTENT";
	public static final String MONGODB_KEY_A_ADDTIME = "A_ADD_TIME";
	public static final String MONGODB_KEY_T_TITLE = "A_TITLE";
	public static final String MONGODB_KEY_A_ANALYSIS = "A_ANALYSIS";
	public static final String MONGODB_KEY_A_AT_ID = "A_AT_ID";
	public static final String MONGODB_KEY_A_CLASS_ID = "A_CLASS_ID";
	public static final String MONGODB_KEY_A_T_SIM = "A_T_SIM";
	public static final String MONGODB_KEY_A_T_ADD_TIME = "A_T_ADDTIME";
	public static final String MONGODB_KEY_T_TEXT_MODEL = "T_TEXTMODEL";
	public static final String MONGODB_KEY_T_ANALYSIS = "T_ANALYSIS";
	public static final String MONGODB_KEY_T_CREATETIME = "T_CREATETIME";
	public static final String MONGODB_KEY_T_UPDATETIME = "T_UPDATETIME";
	public static final String MONGODB_KEY_T_AT_ID = "T_AT_ID";
	public static final String MONGODB_KEY_T_ISOUT = "T_ISOUT";
	public static final String MONGODB_KEY_T_HOT = "T_HOT";
	public static final String MONGODB_KEY_T_ABSTRACT = "T_ABSTRACT";
	public static final String MONGODB_KEY_T_CLASS_ID = "T_CLASS_ID";
	public static final String MONGODB_KEY_T_PARENT = "T_PARENT";
	public static final String MONGODB_KEY_T_CHILDREN = "T_CHILDREN";
	
	public static final String CONF_FILE_PATH_DEFAULT = "conf/config.xml";
	public static final String TEXT_MODEL_PAIR_SPLIT = "\t";
	public static final String TEXT_MODEL_KEY_VALUE_SPLIT = "#";
	public static final double TEXT_POLARITY_THRESHOLD = 0.7;
	
	public static final int JOB_MANAGER_ID_BIT_COUNT = 4;
	public static final int JOB_MANAGER_ID_RANGE = 10000;
	public static final Integer JOB_ID_RANGE = 10000;
	public static final String MONGODB_KEY_T_DOC_COUNT = "T_DOC_COUNT";
	public static final String MONGODB_KEY_T_POS_DOC_COUNT = "T_POS_DOC_COUNT";
	public static final String MONGODB_KEY_T_NEG_DOC_COUNT = "T_NEG_DOC_COUNT";
	public static final String MONGODB_KEY_T_SITE_COUNT = "T_SITE_COUNT";
	public static final String TIME_PATTERN = "yyyy-MM-dd";
	public static final String MYSQL_DRIVER_DEFAULT = "com.mysql.jdbc.Driver";
	public static final String MYSQL_URL_DEFAULT = "jdbc:mysql://125.216.227.56:3306/monitorsys?&useUnicode=True&autoReconnect=true";
	public static final String MYSQL_USR_DEFAULT = "root";
	public static final String MYSQL_PASSWD_DEFAULT = "123456";
	public static final String DECIMAL_PATTERN = "0.000";
	public static final String CONF_KEY_COMBINE_WORKER = "combine.worker.ip";
	
}
