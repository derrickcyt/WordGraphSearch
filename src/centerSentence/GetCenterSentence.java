package centerSentence;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import similarity.LiuConceptParser;

import dataProcess.ReadWriteTxt;
import derrick.ltp.method.IParser;
import derrick.ltp.method.LTPRMIProp;

import ltpTools.LtpService;
import ltpTools.Word;

public class GetCenterSentence {
	public Map<Word, Integer> coreWords = new HashMap<Word, Integer>();
	public List<String> orgtitles = new ArrayList<String>();
	public List<List<Word>> ltpTitles = new ArrayList<List<Word>>();
	public Map<String, String> symilarity = new HashMap<String, String>();
	private String symiDictionaryPath = "synonym/syndict.txt";

	public GetCenterSentence(List<String> sentences) {
		this.orgtitles.addAll(sentences);
		getSymilarity();
		try {
			coreWordsGeneration();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void getSymilarity() {
		try {
			List<String> readLines = ReadWriteTxt.readLines(symiDictionaryPath);
			for (String ss : readLines) {
				String[] tempArray = ss.split("\\s+");
				for (int i = 0; i < tempArray.length - 1; i++) {
					symilarity.put(tempArray[i + 1], tempArray[0]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void coreWordsGeneration() throws MalformedURLException,
			RemoteException, NotBoundException {
		if (orgtitles == null) {
			System.out.println("no titles");
		} else {
			for (String ss : orgtitles) {
				List<Word> afterLtpParse = LtpService.dParseAnalyse(ss);
				for (int i = 0; i < afterLtpParse.size(); i++) {
					if (symilarity.containsKey(afterLtpParse.get(i)
							.getContent())) {
						afterLtpParse.get(i).setContentByOne(
								symilarity.get(afterLtpParse.get(i)
										.getContent()));
					}
				}
				ltpTitles.add(afterLtpParse);
				List<Word> coreWord = findCoreWords(afterLtpParse);
				for (Word ww : coreWord) {
					if (coreWords.containsKey(ww)) {
						int tempNum = coreWords.get(ww) + 1;
						coreWords.put(ww, tempNum);
					} else {
						coreWords.put(ww, 1);
					}
				}
			}
		}
	}

	private List<Word> findCoreWords(List<Word> afterLtpParse) {
		if (afterLtpParse == null) {
			return null;
		}
		List<Word> result = new ArrayList<Word>();
		// 找到根节点
		int root = -2;
		for (Word ww : afterLtpParse) {
			if (ww.getParserParent() == -1) {
				root = ww.getId();
				Word tempWord = new Word(ww.getContent(), ww.getContentByOne(),
						ww.getPos());
				result.add(tempWord);
			}
		}
		if (root == -2) {
			System.out.println("没有找到根节点");
		}
		for (int i = 0; i < root; i++) {
			if (afterLtpParse.get(i).getParserParent() == root
					&& afterLtpParse.get(i).getParserRelation().equals("SBV")) {
				Word tempWord = new Word(afterLtpParse.get(i).getContent(),
						afterLtpParse.get(i).getContentByOne(), afterLtpParse
								.get(i).getPos());
				result.add(tempWord);
				break;
			}
		}
		for (int i = root; i < afterLtpParse.size(); i++) {
			if (afterLtpParse.get(i).getParserParent() == root
					&& afterLtpParse.get(i).getParserRelation().equals("VOB")) {
				Word tempWord = new Word(afterLtpParse.get(i).getContent(),
						afterLtpParse.get(i).getContentByOne(), afterLtpParse
								.get(i).getPos());
				result.add(tempWord);
				break;
			}
		}
		return result;
	}

	// 获取句子集合中，与其他句子相似性最大的前topk个句子，type表示比较句子相似性的类别，目前有1，3这两种。1是判断句子中完全相同的词语，3是用知网hownet进行计算
	public static List<String> getTopKSentence(List<List<Word>> sentences,
			int topk, int type) {
		if (sentences.size() < topk) {
			topk = sentences.size();
		}
		List<String> result = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		for (int i = 0; i < sentences.size(); i++) {
			double scoreSUM = 0;
			for (int j = 0; j < sentences.size(); j++) {
				if (i == j)
					continue;
				if (type == 1) {
					scoreSUM += getSimilarityScoreBasic(sentences.get(i),
							sentences.get(j));
				} else if (type == 2) {

				} else if (type == 3) {
					scoreSUM += getSimilarityScoreByHownet(sentences.get(i),
							sentences.get(j));
				}
			}
			scores.add(scoreSUM);
		}
		List<Integer> sortId = HeapSort.heapSort(scores, topk);
		for (int ii : sortId) {
			String tempStr = "";
			for (Word ww : sentences.get(ii)) {
				tempStr += ww.getContent();
			}
			result.add(tempStr);
		}
		return result;
	}
	//最基本的相似性比较方法
	public static double getSimilarityScoreBasic(List<Word> first,
			List<Word> second) {
		double result = 0;
		for (int i = 0; i < first.size(); i++) {
			for (int j = 0; j < second.size(); j++) {
				if (first.equals(second)) {
					result += 1.0;
				}
			}
		}
		result = result / first.size();
		return result;
	}
	//使用知网HOWNET计算句子间相似性
	public static double getSimilarityScoreByHownet(List<Word> first,
			List<Word> second) {
		double result = 0;
		LiuConceptParser hownet = LiuConceptParser.getInstance();
		for (int i = 0; i < first.size(); i++) {
			double max = 0;
			for (int j = 0; j < second.size(); j++) {
				double simi = hownet.getSimilarity(first.get(i)
						.getContentByOne(), second.get(j).getContentByOne());
				if (simi > max) {
					max = simi;
				}
			}
			result += max;
		}
		result = result / first.size();
		return result;
	}

	public Map<Word, Integer> getCoreWords() {
		return coreWords;
	}

	public void setCoreWords(Map<Word, Integer> coreWords) {
		this.coreWords = coreWords;
	}

	public List<String> getOrgtitles() {
		return orgtitles;
	}

	public void setOrgtitles(List<String> orgtitles) {
		this.orgtitles = orgtitles;
	}

	public List<List<Word>> getLtpTitles() {
		return ltpTitles;
	}

	public void setLtpTitles(List<List<Word>> ltpTitles) {
		this.ltpTitles = ltpTitles;
	}
}
