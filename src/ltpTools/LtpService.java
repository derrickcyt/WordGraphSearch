package ltpTools;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import derrick.ltp.method.IParser;
import derrick.ltp.method.LTPRMIProp;

import ltpTools.Word;

import edu.hit.ir.ltp4j.Parser;
import edu.hit.ir.ltp4j.Postagger;
import edu.hit.ir.ltp4j.Segmentor;
import edu.hit.ir.ltp4j.SplitSentence;

public class LtpService {
	private static LtpParser ltpParser ;
	private static IParser rParser;
	
	static{
		initRMI();
	}
	
	public static boolean initRMI(){
		try {
			rParser = (IParser) Naming
					.lookup("rmi://"+LTPRMIProp.getIpAddr()+":"+LTPRMIProp.getPort()+"/"+LTPRMIProp.getName());
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean initLtpParser() {
		System.out.println("LTP初始化开始");
		ltpParser = new LtpParser();
		if(ltpParser.loadSegmentModel()&&ltpParser.loadPostagModel()&&ltpParser.loadParserModel()){
			System.out.println("LTP初始化成功");
			return true;
		}
		return false;
	}
	
	public static List<Word> dParseAnalyse(String sentence) {
//		if(ltpParser==null){
//			initLtpParser();
//		}
		List<Word> result = new ArrayList<Word>();
//		ArrayList<String> words = new ArrayList<String>();
//		ArrayList<String> postags = new ArrayList<String>();
//		ArrayList<Integer> heads = new ArrayList<Integer>();
//		ArrayList<String> deprels = new ArrayList<String>();
//	    Segmentor.segment(sentence, words);
//	    Postagger.postag(words,postags);
//		int size = Parser.parse(words,postags,heads,deprels);
//	    int j = 0;
//	    for(int i = 0;i<size;i++) {
//	    	j = heads.get(i)-1;
//	    	Word word = new Word(i,words.get(i),words.get(i),postags.get(i),j,deprels.get(i));
//        	//System.out.println("id="+i+",cont="+words.get(i)+",pos="+postags.get(i)+",parent="+j+",relate="+deprels.get(i));
//        	result.add(word);
//	    }
		
		if(rParser==null&&!initRMI()){
			return result;
		}
		
		try {
			result=rParser.dParseAnalyse(sentence);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Word> segmentation(String str){
//		if(ltpParser==null){
//			initLtpParser();
//		}
		List<Word> result = new ArrayList<Word>();
//	    List<String> words = new ArrayList<String>();
//	    List<String> postags = new ArrayList<String>();
//	    int size = Segmentor.segment(str,words);
//	    Postagger.postag(words,postags);
//	    for(int i = 0; i<size; i++) {
//	    	Word word = new Word(words.get(i),words.get(i),postags.get(i));
//	    	result.add(word);
//	    }
		
		if(rParser==null&&!initRMI()){
			return result;
		}
		try {
			result=rParser.segmentation(str);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	    return result;
	}
	
//	public static List<String> segmentation(String str){
//		if(ltpParser==null){
//			initLtpParser();
//		}
//		List<String> result = new ArrayList<String>();
//	    List<String> words = new ArrayList<String>();
//	    List<String> postags = new ArrayList<String>();
//	    int size = Segmentor.segment(str,words);
//	    Postagger.postag(words,postags);
//	    for(int i = 0; i<size; i++) {
//	      result.add(words.get(i)+"\t"+postags.get(i));
//	    }
//	    return result;
//	}
	
	public void releaseLtpParser() {
		if(ltpParser!=null){
			ltpParser.release();
		}
	}
	
	
	
	public static IParser getrParser() {
		return rParser;
	}

	public static void main(String []args){
		List<Word> afterSegment = LtpService.segmentation("微软发布windows10");
		for(Word ww:afterSegment){
			System.out.println(ww.getContent());
			System.out.println(ww.getContentByOne());
			System.out.println(ww.getPos());
		}
	}
}
