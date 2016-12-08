package ltpTools;

import edu.hit.ir.ltp4j.NER;
import edu.hit.ir.ltp4j.Parser;
import edu.hit.ir.ltp4j.Postagger;
import edu.hit.ir.ltp4j.SRL;
import edu.hit.ir.ltp4j.Segmentor;

public class LtpParser {
	private static String segmentModel;
	private static String postagModel;
	private static String nerModel;
	private static String parserModel;
	private static String srlModel;
	private static String userDic;
	
	static{
		segmentModel = "ltp_data/cws.model";
		postagModel = "ltp_data/pos.model";
		parserModel = "ltp_data/parser.model";
		nerModel = "ltp_data/ner.model";
		srlModel = "ltp_data/srl";
		userDic = "ltp_data/userDic.txt";
	}
	
	public boolean loadSegmentModel(){
		if (segmentModel == null) {
			throw new IllegalArgumentException("");
		}
		if(Segmentor.create(segmentModel,userDic)<0){
			System.err.println("load cws.model failed");
			return false;
		}
		return true;
	}
	
	public boolean loadPostagModel(){
		if (postagModel == null) {
			throw new IllegalArgumentException("");
		}
		if(Postagger.create(postagModel)<0){
			System.err.println("load pos.model failed");
			return false;
		}
		return true;
	}
	
	public boolean loadParserModel(){
		if (parserModel == null) {
			throw new IllegalArgumentException("");
		}
		if(Parser.create(parserModel)<0){
			System.err.println("load parser.model failed");
			return false;
		}
		return true;
	}
	
	public boolean loadNerModel(){
		if (nerModel == null) {
			throw new IllegalArgumentException("");
		}
		if(NER.create(nerModel)<0){
			 System.err.println("load ner.model failed");
			return false;
		}
		return true;
	}
	
	public boolean loadSrlModel(){
		if (srlModel == null) {
			throw new IllegalArgumentException("");
		}
		if(SRL.create(srlModel)<0){
			 System.err.println("load srl.model failed");
			return false;
		}
		return true;
	}
	
	public void release(){
	   Segmentor.release();
	   Postagger.release();
	   NER.release();
	   Parser.release();
	   SRL.release();
	}
}
