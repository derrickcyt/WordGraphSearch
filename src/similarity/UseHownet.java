package similarity;

import similarity.LiuConceptParser;

public class UseHownet {
	public static void main(String[]args){
		LiuConceptParser liu = LiuConceptParser.getInstance();
		double xx = liu.getSimilarity("¿ªÐÄ", "¿ìÀÖ");
		System.out.println(xx);
	}
}
