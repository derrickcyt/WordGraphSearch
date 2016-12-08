package wGroup;

public class Edge {
	int startNode;
	int endNode;
	double score=1.0d;

	public Edge(int start, int end) {
		this.startNode = start;
		this.endNode = end;
	}

	public Edge(int startNode, int endNode, double score) {
		super();
		this.startNode = startNode;
		this.endNode = endNode;
		this.score = score;
	}

	public int getStartNode() {
		return startNode;
	}

	public void setStartNode(int startNode) {
		this.startNode = startNode;
	}

	public int getEndNode() {
		return endNode;
	}

	public void setEndNode(int endNode) {
		this.endNode = endNode;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public void addScore(double add){
		this.score++;
	}

	@Override
	public int hashCode() {
		return this.startNode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge edg = (Edge) obj;
			if (this.startNode == edg.getStartNode() && this.endNode == edg.getEndNode()) {
				return true;
			}
		}
		return false;
	}
}
