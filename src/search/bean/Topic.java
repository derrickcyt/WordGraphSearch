package search.bean;

import java.util.List;

public class Topic {

	private List<String> titles; // ԭ����
	private int num; // ��������
	private int topicId;// ��ȡ���

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
