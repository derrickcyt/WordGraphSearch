package search.lm;

public interface LMReader {

	public float read(String w1, String w2, String w3);

	public float read(String w1, String w2);

	public float read(String w1);

}
