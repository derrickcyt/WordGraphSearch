package polarityCalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Semantic.Segmentation;


/**
 * ��м�������
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
	 * ���ı��ֳɾ��ӣ��־���Ϊ  [������;��?!()��,����]
	 * @param document
	 * @return sentences
	 */
	private List<String> documentToSentences(String document){
		List<String> sentences = new LinkedList<String>();
		sentences = Arrays.asList(document.split("[������;��?!()��,����\\s]"));
		return sentences;
	}
	
	/**
	 * Calculate one sentence's polarity
	 * ����һ�����ӵļ���ֵ
	 * @param sentence
	 * @return
	 */
	private double processOneSentence(String sentence){
//		System.out.println(sentence);
		HashMap<String, List<Integer>> wordPosition = new HashMap<String, List<Integer>>(); // ����+λ��
		ArrayList<WordNode> wordNode = new ArrayList<WordNode>();		
		sentenceWordNodeAndPositions(sentence, wordPosition, wordNode);
		List<Integer> positionOfFZC = new ArrayList<Integer>();
		List<Integer> positionOfCDFC = new ArrayList<Integer>();
		List<Integer> positionOfDT = new ArrayList<Integer>();
		
		getSpeWordPosition(wordNode, positionOfFZC, positionOfCDFC, positionOfDT);
		// ����̶ȸ���
		processDegreeADV(wordNode, positionOfCDFC);
		// ����ת��
		processFZC(wordNode, positionOfFZC);
		// ����̬��   �����  ���ܺ�+�ߡ�
		processDTBackWards(wordNode, positionOfDT);
		// ����̬��   ��ǰ�� ����+�ܺġ�
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
		HashMap<String, List<Integer>> wordPosition = new HashMap<String, List<Integer>>(); // ����+λ��
		ArrayList<WordNode> wordNode = new ArrayList<WordNode>();		
		sentenceWordNodeAndPositions(sentence, wordPosition, wordNode);
		List<Integer> positionOfFZC = new ArrayList<Integer>();
		List<Integer> positionOfCDFC = new ArrayList<Integer>();
		List<Integer> positionOfDT = new ArrayList<Integer>();
		getSpeWordPosition(wordNode, positionOfFZC, positionOfCDFC, positionOfDT);
		
		// ����̶ȸ���
		processDegreeADV(wordNode, positionOfCDFC);
		
		// ����ת��
		processFZC(wordNode, positionOfFZC);
		
		// ����̬��   �����  ���ܺ�+�ߡ�
		processDTBackWards(wordNode, positionOfDT);
		
		// ����̬��   ��ǰ�� ����+�ܺġ�
		processDTForWards(wordNode, positionOfDT);

		return wordNode;
	}
	
	/**
	 * ����ת�ʵ����
	 * @param wordNode ����
	 * @param positionOfFZC ��ת�ʵ�λ�ü�¼
	 */
	private void processFZC(ArrayList<WordNode> wordNode, List<Integer> positionOfFZC){
		// �����д��ڷ�ת��
		if(!positionOfFZC.isEmpty()){
			// ������ת��
			for(int i=0; i<positionOfFZC.size(); ++i){
				int position = positionOfFZC.get(i);
				// ȡ�÷�ת��
				WordNode wn = wordNode.get(position);
				
				for(int j=position+1; j<wordNode.size(); ++j){
					WordNode wn_j = wordNode.get(j);
					// �����һ����������
					if(wn_j.getPos().startsWith("u")){
						continue;
					}
					// ����Ƕ���
					else if(wn_j.getPos().startsWith("v")&&
							wn_j.getValue()!=0){
						//����������Ƕ��ʣ������������ƻ񡱵Ȳ��ܽӶ��ʣ� ��˸ö��ʱ�����
						if(wn.getWord().equals("����")||wn.getWord().equals("�ƻ�")){
							continue;// �����ö��ʣ� �����Ҵ�
						}
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// �����÷�ת�ʣ�ת����һ����ת��
					}
					// ���������
					else if(wn_j.getPos().startsWith("n")&&
							wn_j.getValue()!=0){
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// �����÷�ת�ʣ�ת����һ����ת��
					}
					// ��������ݴ�
					else if(wn_j.getPos().startsWith("a")&&
							wn_j.getValue()!=0){
						wordNode.get(j).setPro_pos("FZ+" + wn_j.getPos());
						wordNode.get(j).setPro_word(wn.getWord()+wn_j.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
							wordNode.get(j).setValue(-wn_j.getValue());
						}
						break;// �����÷�ת�ʣ�ת����һ����ת��
					}
				}
			}// End Of ������ת��
		}// End Of ��ת�ʵĴ���
	}
	
	/**
	 * ����̶ȸ��ʵ����
	 * @param wordNode ����
	 * @param positionOfCDFC �̶ȸ��ʵ�λ�ü�¼
	 */
	private void processDegreeADV(ArrayList<WordNode> wordNodes, List<Integer> positionOfCDFC){
		// �����д��ڸ���
		if(!positionOfCDFC.isEmpty()){
			// ��������
			for(int i = 0; i<positionOfCDFC.size(); ++i){
				int position = positionOfCDFC.get(i);
				WordNode wn = wordNodes.get(position);
				//����ø��������Ƕ���ĸ��ʱ�����
				if(this.wordMaterial.getDegreeAdvWordMap().containsKey(wn.getWord())){
					// ����ҵ��������εĴ�������Ǹ���(d)������(v)�����ݴ�(a)
					double value = this.wordMaterial.getDegreeAdvWordMap().get(wn.getWord());
					String pos = "d";
					String word = wn.getWord();
					for(int j=position+1; j<wordNodes.size(); ++j){
						WordNode wn_j = wordNodes.get(j);
						// ��������ʣ�ֱ��������ȡ��һ����
						if(wn_j.getPos().startsWith("u")){
							continue;
						}
						// ����Ǹ���, ��ôd+d
						else if(wn_j.getPos().equals("d") && 
								this.wordMaterial.getDegreeAdvWordMap().containsKey(wn_j.getWord())){
							pos = pos +"+d";
							value = value*this.wordMaterial.getDegreeAdvWordMap().get(wn_j.getWord());
							word = word+wn_j.getWord();
							++i;
						}
						// ����Ƕ��ʣ� ��ôd+v
						else if(wn_j.getPos().startsWith("v")&&
								wn_j.getValue()!=0){
							wordNodes.get(j).setPro_pos(pos+"+v");
							wordNodes.get(j).setPro_word(word+wordNodes.get(j).getWord());
							if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
								wordNodes.get(j).setValue(value*this.wordMaterial.getPolarityWordsMap().get(wn_j.getWord()));
							}
							break; // ������һ������d
						}
						// ��������ݴʣ���ôd+a
						else if(wn_j.getPos().startsWith("a")&&
								wn_j.getValue()!=0){
							wordNodes.get(j).setPro_pos(pos+"+a");
							wordNodes.get(j).setPro_word(word+wordNodes.get(j).getWord());
							if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_j.getWord())){
								wordNodes.get(j).setValue(value*this.wordMaterial.getPolarityWordsMap().get(wn_j.getWord()));
							}
							break; // ������һ������d
						}
					}
				}
				//������ڶ���ĸ��ʱ���ô�����ø���
				else{
					
				}
			}// End of ��������
		}// End Of �����д��ڸ���				
	}
		
	/**
	 * ����̬�ʵ����
	 * ����ң����ܺ� + �ߡ�
	 * @param wordNode ����
	 * @param positionOfDT ��̬���ھ����е�λ�ü�¼
	 */
	private void processDTBackWards(ArrayList<WordNode> wordNode, List<Integer> positionOfDT){
		// ����̬��, ����ҵ����
		if(!positionOfDT.isEmpty()){
			for(int i=0; i<positionOfDT.size(); ++i){
				int position = positionOfDT.get(i); 
				// ��̬���Դ�
				WordNode wn = wordNode.get(position);
				
				// ��ǰ�Ҷ�̬���Դʵ�����
				for(int j=position-1; j>=0; --j){
					WordNode j_wn = wordNode.get(j);
					// ����������ʣ�������ǰ��
					if(!j_wn.getPos().contains("n")){
						continue;
					}
					// ���������
					else{
						j_wn.setPro_pos("n+DT");
						j_wn.setPro_word(j_wn.getWord()+wn.getWord());
						if(this.wordMaterial.getPolarityWordsMap().containsKey(j_wn.getWord())){
							j_wn.setValue(this.wordMaterial.getDongTaiCi().get(wn.getWord())*j_wn.getValue());
						}
						break;
					}
				}
			}// End Of ������̬��
		}// End Of ����̬��, ����ҵ����
	}
	
	/**
	 * ����̬�ʵ����
	 * ��ǰ�ң����� + �ܺġ�
	 * @param wordNode ����
	 * @param positionOfDT ��̬���ھ����е�λ�ü�¼
	 */
	private void processDTForWards(ArrayList<WordNode> wordNode, List<Integer> positionOfDT){
		// ����̬��, ��ǰ�ҵ���� ��+�ܺ�
		if(!positionOfDT.isEmpty()){
			// ������̬��
			for(int i=0; i<positionOfDT.size(); ++i){
				int position = positionOfDT.get(i);
				// ��ȡ��̬��
				WordNode wn = wordNode.get(position);
				WordNode wn_n = null;
				for(int j=position+1; j<wordNode.size(); ++j){
					WordNode wn_j = wordNode.get(j);
					if(wn_n != null && !wn_j.getPos().contains("n")){
						break;
					}
					// ���������
					if(wn_j.getPos().startsWith("u")){
						continue; //����һ����
					}
					// ��������ݴ�
					else if(wn_j.getPos().startsWith("a") && !wn_j.getPos().contains("n")){
						continue;//����һ����
					}
					// ���������
					else if(wn_j.getPos().contains("n")){
						wn_n = wn_j;						
					}
				}
				// �ҵ��˶�̬�����ε�����
				if(wn_n != null){
					wn_n.setPro_pos("DT+"+wn_n.getPos());
					wn_n.setPro_word(wn.getWord()+wn_n.getWord());
					if(this.wordMaterial.getPolarityWordsMap().containsKey(wn_n.getWord())){
						wn_n.setValue(this.wordMaterial.getDongTaiCi().get(wn.getWord())*this.wordMaterial.getPolarityWordsMap().get(wn_n.getWord()));
					}					
				}
			}// End of ������̬��
		}// End Of ����̬���Դʣ���ǰ�ҵ����
	}
	
	/**
	 * ��ȡ���ӵķ�ת�ʣ����ʵ�λ��
	 * @param wordNode �������������
	 * @param positionOfFZC ������������ת�ʵ�λ��
	 * @param positionOfCDFC �������������ʵ�λ��
	 */
	private void getSpeWordPosition(
			ArrayList<WordNode> wordNode,
			List<Integer> positionOfFZC, 
			List<Integer> positionOfCDFC,
			List<Integer> positionOfDT){
		for(int i=0; i<wordNode.size(); ++i){
			WordNode wordnode = wordNode.get(i);
			// ����ǳ̶ȸ���
			if(wordnode.getPos().equals("d")
					&&this.wordMaterial.getAdvDegreeWordMap().containsKey(wordnode.getWord())){
				positionOfCDFC.add(i);
			}
			// ����Ƿ�ת��
			else if(this.wordMaterial.getFanZhuanCi().contains(wordnode.getWord())){
				positionOfFZC.add(i);
			}
			// ����Ƕ�̬��
			else if(this.wordMaterial.getDongTaiCi().containsKey(wordnode.getWord())){
				positionOfDT.add(i);
			}
		}
	}
	
	/**
	 * ������ӣ������ӱ����  ������+λ�á� �� ��wordNode������ʽ
	 * @param sentence ����ľ���
	 * @param wordPosition ����+λ��
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
					// ����
					if(this.wordMaterial.getPolarityWordsMap().containsKey(tmp[0])){
						value = this.wordMaterial.getPolarityWordsMap().get(tmp[0]);
						
					}
					// �ѳ̶ȸ��ʵ�ֵҲ���ȥ��������ʱ��Ҫ�����ǹ��˵�
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
		System.out.println(new Polarity().startCalPolarity("���������û�ױƷ������ױ��mm��Ҫ��עһ��Ŷ������Ӵ�Ƥ���Ļ�ױƷ��һ��Ҫ���ע���أ�[����]"));
	}
}

