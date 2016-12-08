package dataProcess;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import polarityCalculation.PolarityDemo;

import params.Params;

@Document(collection = "mghotentitiesx")
public class EntityDAO {
	
	private int ENTITY_ID;
	private List<ArticleDAO> T_ARTICLE;
	private String ENTITY_NAME;
	private double ENTITY_POLARITY;
	
	public EntityDAO(int id,String name,Map<String, Map<String,String>> entityInfo,List<String> ids,double polarity) {
		setENTITY_ID(id);
		setENTITY_NAME(name);
		initAritlces(entityInfo,ids);
		setENTITY_POLARITY(polarity);
	}
	
	private void initAritlces(Map<String, Map<String,String>> entityInfo,List<String> ids) {
		setT_ARTICLE(new ArrayList<ArticleDAO>());
		ArticleDAO tmpArticleObj;
		for (String id : ids) {
			String title = entityInfo.get(id).get(Params.HTABLE_QUAL_ATITLE);
			String text = entityInfo.get(id).get(Params.HTABLE_QUAL_ATEXT);
			if(text.length()>120){
				text = text.substring(0,120);
			}
			//double tempdouble=0;
			double tempdouble = PolarityDemo.calXinwen(title,text);
			int tempdd = (int)(tempdouble*1000);
			tempdouble =  ((double)tempdd)/1000;
			tmpArticleObj = new ArticleDAO(title,text,entityInfo.get(id).get(Params.HTABLE_QUAL_AURL),tempdouble,entityInfo.get(id).get(Params.HTABLE_QUAL_ACRAWLTIME));
			getT_ARTICLE().add(tmpArticleObj);
		}
	}

	public void setENTITY_ID(int eNTITY_ID) {
		ENTITY_ID = eNTITY_ID;
	}

	public int getENTITY_ID() {
		return ENTITY_ID;
	}

	public void setT_ARTICLE(List<ArticleDAO> t_ARTICLE) {
		T_ARTICLE = t_ARTICLE;
	}

	public List<ArticleDAO> getT_ARTICLE() {
		return T_ARTICLE;
	}

	public void setENTITY_NAME(String eNTITY_NAME) {
		ENTITY_NAME = eNTITY_NAME;
	}

	public String getENTITY_NAME() {
		return ENTITY_NAME;
	}
	
	public double getENTITY_POLARITY() {
		return ENTITY_POLARITY;
	}

	public void setENTITY_POLARITY(double eNTITY_POLARITY) {
		ENTITY_POLARITY = eNTITY_POLARITY;
	}
}
