package Semantic;

//import main.NlpirLibrary.CLibraryNlpir;

import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

public class Segmentation {
	
	public static List<Term> baseAnalysis (String sentence) {
		return FilterModifWord.modifResult(BaseAnalysis.parse(sentence));
	}
	
	public static List<Term> toAnalysis (String sentence) {
		return FilterModifWord.modifResult(ToAnalysis.parse(sentence));
	}
	
	public static List<Term> nlpAnalysis (String sentence) {
		return NlpAnalysis.parse(sentence);
	}
	
	public static String nlpSegment (String sentence) throws Exception{
		StringBuilder sb=new StringBuilder();
		List<Term> terms=Segmentation.nlpAnalysis(sentence);
		for(Term term:terms)
			sb.append(term.toString());
		
		return sb.toString().trim();
	}
	
	public static String baseSegment (String sentence) throws Exception{
		StringBuilder sb=new StringBuilder();
		List<Term> terms=Segmentation.baseAnalysis(sentence);
		for(Term term:terms)
			sb.append(term.toString());
		
		return sb.toString().trim();
	}
	
	public static String segmentText (String text) throws Exception{
		StringBuilder sb=new StringBuilder();
		List<Term> terms=Segmentation.toAnalysis(text);
		for(Term term:terms)
			sb.append(term.toString());
		
		return sb.toString().trim();
	}
	
	public static void main(String[] args) throws Exception {
		String main2 = "蒋经国逝世28周年 马英九朱立伦赴陵寝谒灵";
		System.out.println(toAnalysis(main2).toString());
	}
}
