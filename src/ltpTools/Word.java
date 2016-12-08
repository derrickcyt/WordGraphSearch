package ltpTools;

import java.io.Serializable;

import wGroup.WordNode;

public class Word implements Serializable {
	private int id;                    //id
	private String content;            //词语内容
	private String contentByOne;       //词语内容(同义词统一)
	private String pos;                //词性
	private int parserParent;          //父节点
	private String parserRelation;     //依存关系
	
	public Word(int ID,String CONTENT,String CONTENTBYONE,String POS,int parserParent,String parserRelation){
		this.id = ID;
		this.content = CONTENT;
		this.contentByOne = CONTENTBYONE;
		this.pos = POS;
		this.parserParent = parserParent;
		this.parserRelation = parserRelation;
	}
	
	public Word(String CONTENT,String CONTENTBYONE,String POS){
		this.content = CONTENT;
		this.contentByOne = CONTENTBYONE;
		this.pos = POS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public int getParserParent() {
		return parserParent;
	}

	public void setParserParent(int parserParent) {
		this.parserParent = parserParent;
	}

	public String getParserRelation() {
		return parserRelation;
	}

	public void setParserRelation(String parserRelation) {
		this.parserRelation = parserRelation;
	}

	public String getContentByOne() {
		return contentByOne;
	}

	public void setContentByOne(String contentByOne) {
		this.contentByOne = contentByOne;
	}
	
	@Override
	public int hashCode() {
		return this.contentByOne.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Word) {
			Word word = (Word)obj;
			if(this.contentByOne.equals(word.getContentByOne())){
				return true;
			}
		}
		return false;
	}
	
}
