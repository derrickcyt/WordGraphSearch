package dataProcess;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import params.MongoParams;

import dataProcess.EntityDAO;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDB {

	private static ApplicationContext cxt = null;
	private static MongoTemplate mongoTemplate = null;
	
	private Mongo mg;
	private DB db;
	private DBCollection col;
	
	
	static {
		cxt = new ClassPathXmlApplicationContext("params/mongodb_application_context.xml");
		mongoTemplate = cxt.getBean("mongoTemplate", MongoTemplate.class);
	}
	
	public MongoDB() {
		try {
			mg = new Mongo(MongoParams.MONGODB_IP, MongoParams.MONGODB_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = mg.getDB(MongoParams.MONGODB_DB_DEFAULT);
		col = db.getCollection(MongoParams.MONGODB_TABLE_DEFAULT);
	}
	
	public MongoDB(String dbName, String tableName) {
		try {
			mg = new Mongo(MongoParams.MONGODB_IP, MongoParams.MONGODB_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = mg.getDB(dbName);
		col = db.getCollection(tableName);
	}
	
	public void close() {
		if ( mg!= null) {
			mg.close();
			mg = null;
			db = null;
			col = null;
			System.gc();
		}
	}
	
	public void useDB(String dbName, String tableName) {
		db = mg.getDB(dbName);
		useCollection(tableName);
	}
	
	public void useCollection(String tableName) {
		col = db.getCollection(tableName);
	}
	
	public void createCollection(String tableName) {
		db.createCollection(tableName, null);
	}
	
	public void dropCollection(String tableName) {
		col = db.getCollection(tableName);
		col.drop();
	}
	
	public void insert(DBObject obj) {
		col.insert(obj);
	}

	public void removeBy_id(String _id) {
		col.remove(new BasicDBObject("_id", _id));
	}
	
	public List<HotTopic_BackEnd> getArticleByDateL(Long startTime, Long endTime){
		Query query = new Query();
		query.addCriteria(Criteria.where("T_UPDATETIME").gte(startTime).lte(endTime));
		List<HotTopic_BackEnd> resultList = mongoTemplate.find(query, HotTopic_BackEnd.class);
		return resultList;
	}
	//根据话题id取话题内容
	public List<HotTopic_BackEnd> getTopicById(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("TOPIC_ID").is(id));
		List<HotTopic_BackEnd> resultList = mongoTemplate.find(query, HotTopic_BackEnd.class);
		return resultList;
	}
	
	public List<EntityDAO> getEntityById(int id){
		Query query = new Query();
		query.addCriteria(Criteria.where("ENTITY_ID").is(id));
		List<EntityDAO> resultList = mongoTemplate.find(query, EntityDAO.class);
		return resultList;
	}
	
	public void removeBy_topicId(String topicId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("TOPIC_ID").is(topicId));
		mongoTemplate.remove(query, HotTopic_BackEnd.class);
	}
	
	public void mongofilter(int id){
		Query query = new Query();
		query.addCriteria(Criteria.where("ENTITY_ID").is(id));
		mongoTemplate.remove(query, MgEntity.class);
	}
	
	public void mongofilter2(List<Integer> ids){
		Query query = new Query();
		query.addCriteria(Criteria.where("ENTITY_ID").nin(ids));
		mongoTemplate.remove(query, MgEntity.class);
	}
	
	public TopicTextDao getArticleById(String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("A_ID").is(id));
		TopicTextDao result = mongoTemplate.findOne(query, TopicTextDao.class);
		return result;
	}
	
	public void insertAll(List<EntityDAO> entityObjects) {
		mongoTemplate.insertAll(entityObjects);
	}
	
	public static void main(String[] args) {
		MongoDB xxx = new MongoDB();
		System.out.println("123");
		TopicTextDao ASD = xxx.getArticleById("2_9c5031b194ad3b9a8596eef801efe404");
		System.out.println(ASD.getA_CONTENT());
	}
}
