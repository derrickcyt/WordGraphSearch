package wGroup;

public class WordNode {
	public String wordString;
	double score;
	
	public WordNode(String word,double score){
		this.wordString=word;
		this.score=score;
	}
	
	public WordNode(String word){
		this.wordString=word;
	}
	
	public WordNode(){
		
	}
	
	
	public void addScore(double dd){
		this.score+=1;
	}
	
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getWordString() {
		return wordString;
	}

	public void setWordString(String wordString) {
		this.wordString = wordString;
	}

	@Override
	public int hashCode() {
		return this.wordString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WordNode) {
			WordNode node = (WordNode)obj;
			if(this.wordString.equals(node.getWordString())){
				return true;
			}
		}
		return false;
	}
}
