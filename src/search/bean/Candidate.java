package search.bean;

import java.util.List;

public class Candidate {

	private List<Integer> nodeIds;
	private double score;

	public List<Integer> getNodeIds() {
		return nodeIds;
	}

	public void setNodeIds(List<Integer> nodeIds) {
		this.nodeIds = nodeIds;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Candidate(List<Integer> nodeIds, double score) {
		super();
		this.nodeIds = nodeIds;
		this.score = score;
	}

}
