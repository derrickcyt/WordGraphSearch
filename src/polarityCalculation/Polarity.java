package polarityCalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Semantic.Segmentation;


/**
 * 情感计算主类
 * @author lzx
 *
 */
public class Polarity {
//	private Sim sim = null;
	private WordMaterial wordMaterial = null;
	
	
	public Polarity(){
//		this.sim = new Sim();
		this.wordMaterial = new WordMaterial();
		
//		try {
////			sim.readHowNet();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Split document to sentences
	 * 将文本分成句子，分句标点为  [。？！;；?!()。,，、]
	 * @param document
	 * @return sentences
	 */
	private List<String> documentToSentences(String document){
		List<String> sentences = new LinkedList<String>();
		sentences = Arrays.asList(document.split("[。？！;；?!()。,，、\\s]"));
		return sentences;
	}
	
	/**
	 * Calculate one sentence's polarity
	 * 计算一个句子的极性值
	 * @param sentence
	 * @return
	 */
	private double processOneSentence(String sentence){
//		System.out.println(sentence);
		HashMap<String, List<Integer>> wordPosition = new HashMap<String, List<Integer>>(); // 单词+位置
		ArrayList<WordNode> wordNode = new ArrayList<WordNode>();		
		sentenceWordNodeAndPositions(sentence, wordPosition, wordNode);
		List<Integer> positionOfFZC = new ArrayList<Integer>();
		List<Integer> positionOfCDFC = new ArrayList<Integer>();
		List<Integer> positionOfDT = new ArrayList<Integer>();
		
		getSpeWordPosition(wordNode, positionOfFZC, positionOfCDFC, positionOfDT);
		// 处理程度副词
		processDegreeADV(wordNode, positionOfCDFC);
		// 处理反转词
		processFZC(wordNode, positionOfFZC);
		// 处理动态词   向后找  “能耗+高”
		processDTBackWards(wordNode, positionOfDT);
		// 处理动态词   向前找 “高+能耗”
		processDTForWards(wordNode, positionOfDT);
		double result = 0.0;
		for(int i=0; i<wordNode.size(); ++i){
			double v = wordNode.get(i).getValue();
			if(v!=0 && !wordNode.get(i).getPos().equals("d")){
				result += wordNode.get(i).getValue();
//				System.out.print("["+wordNode.get(i).getWord()+wordNode.get(i).getPro_pos()+":"+wordNode.get(i).getValue()+"]");
			}
		}
//		System.out.println("");
		return result;
	}
	
	private List<WordNode> processOneSentence2(String sentence){
//		System.out.println(sentence);
		HashMap<String, List<Integer>> wordPosition = new HashMap<String, List<Integer>>(); // 单词+位置
		ArrayList<WordNode> wordNode = new ArrayList<WordNode>();		
		sentenceWordNodeAndPositions(sentence, wordPosition, wordNode);
		List<Integer> positionOfFZC = new ArrayList<Integer>();
		List<Integer> positionOfCDFC = new ArrayList<Integer>();
		List<Integer> positionOfDT = new ArrayList<Integer>();
		getSpeWordPosition(wordNode, positionOfFZC, positionOfCDFC, positionOfDT);
		
		// 处理程度副词
		processDegreeADV(wordNode, positionOfCDFC);
		
		// 处理反转词
		processFZC(wordNode, positionOfFZC);
		
		// 处理动态词   向后找  “能耗+高”
		processDTBackWards(wordNode, positionOfDT);
		
		// 处理动态词   向前找 “高+能耗”
		processDTForWards(wordNode, positionOfDT);

		return wordNode;
	}
	
	/**
	 * 处理反转词的情况
	 * @param wordNode 句子
	 * @param positionOfFZC 反转词的位置记录
	 */
	private void processFZC(ArrayList<WordNode> wordNode, List<Integer> positionOfFZC){
		// 句子中存在反转词
		if(!positionOfFZC.isEmpty()){
			// 遍历反转词
			for(int i=0; i<positionOfFZC.size(); ++i){
				int position = positionOfFZC.get(i);
				// 取得反转词
				WordNode wn = wordNode.get(position);
				
				for(int j=position+1; j<wordNode.size(); ++j){
					WordNode wn_j = wordNode.get(j);
					// 如果下一个词是助词
					if(wn_j.getPos().startsWith("u")){
						continue;
					}
					// 如果是动词
					else if(wn_j.getPos().startsWith("v")&&
							wn_j.getValue()!=0){
						//如果遇到的是动词，“消除”“破获”等不能接动词， 因此该动词被跳过
						if(wn.getWord().equals("消除")||wn.getWord().equals("破获")){
							continue;// 跳过该动词， 继续找词
						}
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// 跳出该反转词，转到下一个反转词
					}
					// 如果是名词
					else if(wn_j.getPos().startsWith("n")&&
							wn_j.getValue()!=0){
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// 跳出该反转词，转到下一个反转词
					}
					// 如果是形容词
					else if(wn_j.getPos().startsWith("a")&&
							wn_j.getValue()!=0){
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// 跳出该反转词，转到下一个反转词
					}
				}
			}// End Of 遍历反转词
		}// End Of 反转词的处理
	}
	
	/**
	 * 处理程度副词的情况
	 * @param wordNode 句子
	 * @param positionOfCDFC 程度副词的位置记录
	 */
	private void processDegreeADV(ArrayList<WordNode> wordNodes, List<Integer> positionOfCDFC){
		// 句子中存在副词
		if(!positionOfCDFC.isEmpty()){
			// 遍历副词
			for(int i = 0; i<positionOfCDFC.size(); ++i){
				int position = positionOfCDFC.get(i);
				WordNode wn = wordNodes.get(position);
				//如果该副词在我们定义的副词表里面
				if(this.wordMaterial.getDegreeAdvWordMap().containsKey(wn.getWord())){
					// 向后找到副词修饰的词语，可能是副词(d)、动词(v)、形容词(a)
					double value = this.wordMaterial.getDegreeAdvWordMap().get(wn.getWord());
					String pos = "d";
					String word = wn.getWord();
					for(int j=position+1; j<wordNodes.size(); ++j){
						WordNode wn_j = wordNodes.get(j);
						// 如果是助词，直接跳过，取下一个词
						if(wn_j.getPos().startsWith("u")){
							continue;
						}
						// 如果是副词, 那么d+d
						else if(wn_j.getPos().equals("d") && 
								this.wordMaterial.getDegreeAdvWordMap().containsKey(wn_j.getWord())){
							pos = pos +"+d";
							value = value*this.wordMaterial.getDegreeAdvWordMap().get(wn_j.getWord());
							word = word+wn_j.getWord();
							++i;
						}
						// 如果是动词， 那么d+v
						else if(wn_j.getPos().startsWith("v")&&
								wn_j.getValue()!=0){
							wordNodes.get(j).setPro_pos(pos+"+v");
							wordNodes.get(j).setPro_word(word+wordNodes.get(j).getWord());
							if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
								wordNodes.get(j).setValue(value*this.wordMaterial.getPolarityWordsMap().get(wn_j.getWord()));
							}
							break; // 处理下一个副词d
						}
						// 如果是形容词，那么d+a
						else if(wn_j.getPos().startsWith("a")&&
								wn_j.getValue()!=0){
							wordNodes.get(j).setPro_pos(pos+"+a");
							wordNodes.get(j).setPro_word(word+wordNodes.get(j).getWord());
							if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
								wordNodes.get(j).setValue(value*this.wordMaterial.getPolarityWordsMap().get(wn_j.getWord()));
							}
							break; // 处理下一个副词d
						}
					}
				}
				//如果不在定义的副词表，那么跳过该副词
				else{
					
				}
			}// End of 遍历副词
		}// End Of 句子中存在副词				
	}
		
	/**
	 * 处理动态词的情况
	 * 向后找：“能耗 + 高”
	 * @param wordNode 句子
	 * @param positionOfDT 动态词在句子中的位置记录
	 */
	private void processDTBackWards(ArrayList<WordNode> wordNode, List<Integer> positionOfDT){
		// 处理动态词, 向后找的情况
		if(!positionOfDT.isEmpty()){
			for(int i=0; i<positionOfDT.size(); ++i){
				int position = positionOfDT.get(i); 
				// 动态极性词
				WordNode wn = wordNode.get(position);
				
				// 向前找动态极性词的名词
				for(int j=position-1; j>=0; --j){
					WordNode j_wn = wordNode.get(j);
					// 如果不是名词，继续向前找
					if(!j_wn.getPos().contains("n")){
						continue;
					}
					// 如果是名词
					else{
						j_wn.setPro_pos("n+DT");
						j_wn.setPro_word(j_wn.getWord()+wn.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(j_wn.getWord())){
							j_wn.setValue(this.wordMaterial.getDongTaiCi().get(wn.getWord())*j_wn.getValue());
						}
						break;
					}
				}
			}// End Of 遍历动态词
		}// End Of 处理动态词, 向后找的情况
	}
	
	/**
	 * 处理动态词的情况
	 * 向前找：“高 + 能耗”
	 * @param wordNode 句子
	 * @param positionOfDT 动态词在句子中的位置记录
	 */
	private void processDTForWards(ArrayList<WordNode> wordNode, List<Integer> positionOfDT){
		// 处理动态词, 向前找的情况 高+能耗
		if(!positionOfDT.isEmpty()){
			// 遍历动态词
			for(int i=0; i<positionOfDT.size(); ++i){
				int position = positionOfDT.get(i);
				// 获取动态词
				WordNode wn = wordNode.get(position);
				WordNode wn_n = null;
				for(int j=position+1; j<wordNode.size(); ++j){
					WordNode wn_j = wordNode.get(j);
					if(wn_n != null && !wn_j.getPos().contains("n")){
						break;
					}
					// 如果是助词
					if(wn_j.getPos().startsWith("u")){
						continue; //找下一个词
					}
					// 如果是形容词
					else if(wn_j.getPos().startsWith("a") && !wn_j.getPos().contains("n")){
						continue;//找下一个词
					}
					// 如果是名词
					else if(wn_j.getPos().contains("n")){
						wn_n = wn_j;						
					}
				}
				// 找到了动态词修饰的名词
				if(wn_n != null){
					wn_n.setPro_pos("DT+"+wn_n.getPos());
					wn_n.setPro_word(wn.getWord()+wn_n.getWord());
					if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_n.getWord())){
						wn_n.setValue(this.wordMaterial.getDongTaiCi().get(wn.getWord())*this.wordMaterial.getPolarityWordsMap().get(wn_n.getWord()));
					}					
				}
			}// End of 遍历动态词
		}// End Of 处理动态极性词，向前找的情况
	}
	
	/**
	 * 获取句子的反转词，副词的位置
	 * @param wordNode 出入参数，句子
	 * @param positionOfFZC 传出参数，反转词的位置
	 * @param positionOfCDFC 传出参数，副词的位置
	 */
	private void getSpeWordPosition(
			ArrayList<WordNode> wordNode,
			List<Integer> positionOfFZC, 
			List<Integer> positionOfCDFC,
			List<Integer> positionOfDT){
		for(int i=0; i<wordNode.size(); ++i){
			WordNode wordnode = wordNode.get(i);
			// 如果是程度副词
			if(wordnode.getPos().equals("d")
					&&this.wordMaterial.getAdvDegreeWordMap().containsKey(wordnode.getWord())){
				positionOfCDFC.add(i);
			}
			// 如果是反转词
			else if(this.wordMaterial.getFanZhuanCi().contains(wordnode.getWord())){
				positionOfFZC.add(i);
			}
			// 如果是动态词
			else if(this.wordMaterial.getDongTaiCi().containsKey(wordnode.getWord())){
				positionOfDT.add(i);
			}
		}
	}
	
	/**
	 * 处理句子，将句子保存成  “单词+位置” 和 “wordNode”的形式
	 * @param sentence 传入的句子
	 * @param wordPosition 单词+位置
	 * @param wordNodes 
	 */
	private void sentenceWordNodeAndPositions(String sentence, HashMap<String, List<Integer>> wordPosition, ArrayList<WordNode> wordNodes){
		String[] words;
		try {
			
			// original
//			words = nlpirUtil.splitString(sentence).split(" ");
			// zzf modify
			words = Segmentation.segmentText(sentence).split(" ");
			for(int i=0; i< words.length; ++i){
				if(words[i].contains("/")){
					String[] tmp = words[i].split("/");
					double value = 0.0;
					// 这里
					if(this.wordMaterial.getPolarityWordsMap().containsKey(tmp[0])){
						value = this.wordMaterial.getPolarityWordsMap().get(tmp[0]);
						
					}
					// 把程度副词的值也填进去，输出结果时需要把它们过滤掉
					/*else if(this.wordMaterial.getAdvDegreeWordMap().containsKey(tmp[0])&&
							tmp[1].equals("d")){
						value = this.wordMaterial.getAdvDegreeWordMap().get(tmp[0]);
					}*/
					wordNodes.add(new WordNode(tmp[0], tmp[1], value));
					
					if(!wordPosition.containsKey(tmp[0])){
						List<Integer> position = new ArrayList<Integer>();
						position.add(i);
						wordPosition.put(tmp[0], position);
					}else{
						wordPosition.get(tmp[0]).add(i);
					}
					
				}else{
					wordNodes.add(new WordNode(" "," ", 0.0));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * The entry of polarity
	 * @param document - the document you want to calculate
	 * @return the result
	 */
	public double startCalPolarity(String document){
		double result = 0.0;
		List<String> sents = documentToSentences(document);
		Iterator<String> iterator = sents.iterator();
		while(iterator.hasNext()){
			String sentence = (String)iterator.next();
				result += processOneSentence(sentence);
			
		}
		result = result/(0.5+Math.abs(result));
//		logger.info("Result: " + result);
		return result;
	}
	
	public List<List<WordNode>> startCalPolarity2(String document){
		List<List<WordNode>> re = new ArrayList<List<WordNode>>();
		List<String> sents = documentToSentences(document);
		
		Iterator<String> iterator = sents.iterator();
		while(iterator.hasNext()){
			String sentence = (String)iterator.next();
			re.add(processOneSentence2(sentence));
		}
		return re;
	}
	
	public static void main(String[] args) {
		System.out.println(new Polarity().startCalPolarity("【教你辨别常用化妆品】常化妆的mm们要关注一下哦！天天接触皮肤的化妆品可一定要多多注意呢！[美好]"));
	}
}

