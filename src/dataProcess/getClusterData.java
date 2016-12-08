package dataProcess;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

public class getClusterData {
	public static HotTopic_BackEnd getDatabyId(){
		org.springframework.data.mongodb.core.query.Query query=new org.springframework.data.mongodb.core.query.Query();
		query.addCriteria(Criteria.where("TOPIC_ID").is("5633291985ae35c3857fdad4"));
		MongoTemplate mongoTemplate = null;
		HotTopic_BackEnd hotTopic = mongoTemplate.findOne(query, HotTopic_BackEnd.class, "mghottopics");
		return hotTopic;
	}
	
	@SuppressWarnings("null")
	public static void main(String []args){
		org.springframework.data.mongodb.core.query.Query query=new org.springframework.data.mongodb.core.query.Query();
		Long endTime = System.currentTimeMillis();
		Long startTime = endTime-1000*3600*24;
		query.addCriteria(Criteria.where("T_CREATETIME").gte(startTime));
		query.addCriteria(Criteria.where("T_CREATETIME").lte(endTime));
		MongoTemplate mongoTemplate = null;
		List<HotTopic_BackEnd> hotTopic = mongoTemplate.find(query, HotTopic_BackEnd.class, "mghottopics");
		System.out.println(hotTopic.size());
	}
}
