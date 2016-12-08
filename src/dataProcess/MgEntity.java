package dataProcess;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="mghotentitiesx")
public class MgEntity {
	private int ENTITY_ID;
	private List<ARTICLE> T_ARTICLE;
	
	private String ENTITY_NAME;
	
	public int getENTITY_ID() {
		return ENTITY_ID;
	}
	
	public void setENTITY_ID(int eNTITYID) {
		ENTITY_ID = eNTITYID;
	}
	
	public List<ARTICLE> getT_ARTICLE() {
		return T_ARTICLE;
	}
	
	public void setT_ARTICLE(List<ARTICLE> tARTICLE) {
		T_ARTICLE = tARTICLE;
	}

	public String getENTITY_NAME() {
		return ENTITY_NAME;
	}

	public void setENTITY_NAME(String eNTITYNAME) {
		ENTITY_NAME = eNTITYNAME;
	}
}
