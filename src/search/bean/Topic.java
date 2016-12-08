package search.bean;

import java.util.List;

public class Topic {

	private List<String> titles; // 原标题
	private int num; // 标题数量
	private int topicId;// 读取后的

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public Topic(List<String> titles, int num, int topicId) {
		super();
		this.titles = titles;
		this.num = num;
		this.topicId = topicId;
	}

}
