package wGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import optimization.EventData;

import org.ansj.domain.Term;

import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

import Semantic.Segmentation;

public class WordGraph {
	public ArrayList<WordNode> nodes = new ArrayList<WordNode>();// 节点
	public ArrayList<Edge> edges = new ArrayList<Edge>();// 边
	Map<String, List<Integer>> repeatNodeMap = new HashMap<String, List<Integer>>();// 不能合并节点记录
	Map<String, String> symbolMap = new HashMap<String, String>();
	Map<Integer, Integer> endMap = new HashMap<Integer, Integer>();// 结束节点统计
	// 初始化词图

	public WordGraph() {
		WordNode start = new WordNode("STARTNODE", 0);
		WordNode end = new WordNode("ENDNODE", 0);
		this.nodes.add(start);
		this.nodes.add(end);
		// 一些分词错误的纠正
		symbolMap.put("\"", "\"");
		symbolMap.put("“/w", "”/w");
		symbolMap.put("《/w", "》/w");
	}

	public void addSent2Graph(String sentence) throws Exception {
		List<Term> terms = Segmentation.toAnalysis(sentence);

		Iterator<Term> iterator = terms.iterator();
		while (iterator.hasNext()) {
			Term term = iterator.next();
			if (term.toString().equals("/") || !term.toString().contains("/")) {
				iterator.remove();
			}
		}

		if (terms == null || terms.size() < 2) {// 长度小于2的剔除
			return;
		}

		List<Integer> path = new ArrayList<>();// 该句子生成的路径初始化
		path.add(0);// 加入开始节点STARTNODE
		nodes.get(0).addScore(1);
		// 处理每一个词
		for (int i = 0; i < terms.size(); i++) {
			String termStr = terms.get(i).toString();
			int preId = i;// 前一个已处理的节点在path的位置, i代表分词的当前位置
			// 定义下一个节点的id
			int perfectMatchNodeId = -1;
			if (repeatNodeMap.containsKey(termStr)) {// 如果之前有出现过这个词，且大于等于2次
				List<Integer> repeatNodeIds = repeatNodeMap.get(termStr);
				// 寻找最优合并节点，主要方法是找前或后一词有没有相同模式
				perfectMatchNodeId = getPerfectMatchNode(terms, i, repeatNodeIds, path);
				// 如果没有找到合并节点,或找到了但是出现环
				if (perfectMatchNodeId == -1 || LoopIsExist(path.get(preId), perfectMatchNodeId)) {
					nodes.add(new WordNode(termStr, 1));// 新建节点
					perfectMatchNodeId = nodes.size() - 1;// 新节点id
					// 添加到重复记录中
					repeatNodeIds.add(perfectMatchNodeId);
					// 插入边
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
					// 这是新节点，所以不可能有已有边
					edges.add(newE);
				} else {// 可合并
					nodes.get(perfectMatchNodeId).addScore(1);
					// 判断是否已经存在边
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);// 用于定位已有边
					if (edges.contains(newE)) {
						edges.get(edges.indexOf(newE)).addScore(1);
					} else {
						edges.add(newE);
					}
				}
			} else {// 没有重复出现过
				// 看之前有没有出现过
				perfectMatchNodeId = nodes.indexOf(new WordNode(termStr));
				if (perfectMatchNodeId != -1) {// 出现过
					// 判断是否在新句出现过，或者插入后是否产生环
					if (path.contains(perfectMatchNodeId)
							|| LoopIsExist(path.get(path.size() - 1), perfectMatchNodeId)) {// 符合则新建节点
						nodes.add(new WordNode(termStr, 1));// 新建节点
						// 记录重复节点
						List<Integer> tmpRepeatNodeIds = new ArrayList<>();
						tmpRepeatNodeIds.add(perfectMatchNodeId);
						perfectMatchNodeId = nodes.size() - 1;// 新节点id
						tmpRepeatNodeIds.add(perfectMatchNodeId);
						repeatNodeMap.put(termStr, tmpRepeatNodeIds);
						// 插入边
						Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
						// 这是新节点，所以不可能有已有边
						edges.add(newE);
					} else {// 都不满足，可合并
						nodes.get(perfectMatchNodeId).addScore(1);
						// 插入边
						Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
						if (!edges.contains(newE))
							edges.add(newE);
						else
							edges.get(edges.indexOf(newE)).addScore(1);
					}
				} else {// 没出现过，新建
					nodes.add(new WordNode(termStr, 1));// 新建节点
					perfectMatchNodeId = nodes.size() - 1;
					// 插入边
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
					// 这是新节点，所以不可能有已有边
					edges.add(newE);
				}
			}
			if (perfectMatchNodeId != -1)
				path.add(perfectMatchNodeId);
			else
				throw new Exception("找不到节点插入");
		}
		Edge newE = new Edge(path.get(path.size() - 1), 1);
		edges.add(newE);
		nodes.get(1).addScore(1);
	}

	// 往词图中添加句子
	public void AddCandidateToGraph(String sentence) {
		List<Term> newWordList = Segmentation.toAnalysis(sentence);// 分词
		if (newWordList == null || newWordList.size() < 2) {
			return;
		}
		List<Integer> candiPosition = new ArrayList<Integer>();
		// 添加开始节点0
		candiPosition.add(0);
		label1: for (int i = 0; i < newWordList.size(); i++) {
			String newWordStr = newWordList.get(i).toString();
			// 遇到分好书名号等进行合并
			// if(symbolMap.containsKey(newWordStr)){
			// System.out.println("#####################"+newWordStr);
			// String endSymbol = symbolMap.get(newWordStr);
			// int back =i;
			// i++;
			// while(!newWordList.get(i).toString().equals(endSymbol)){
			// newWordStr+=newWordList.get(i).toString();
			// if(i==newWordList.size()-1){
			// System.out.println("判断符号匹配时出错："+newWordStr);
			// i=back-1;
			// newWordList.remove(back);
			// continue label1 ;
			// }
			// i++;
			// }
			// newWordStr+=newWordList.get(i).toString();
			// System.out.println(newWordStr);
			// for(int j=0;j<i-back;j++){
			// newWordList.remove(back+1);
			// }
			// i=back;
			// }
			// 遇到分号书名号等进行合并结束

			// 初始化下一个节点的索引
			int perfectMatchNodeIndex = -1;
			// ######################
			// 遇到#号表示这句话没有连接开始节点，因此不能将其与开始节点相连
			if (newWordStr.equals("#")) {
				perfectMatchNodeIndex = nodes.indexOf(new WordNode(newWordStr));
				if (perfectMatchNodeIndex == -1) {
					nodes.add(new WordNode(newWordStr, -10));
					perfectMatchNodeIndex = nodes.size() - 1;
				}
				candiPosition.add(perfectMatchNodeIndex);
				Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
				if (!edges.contains(addEdge)) {
					edges.add(addEdge);
				}
				continue;
			}
			// ######################
			if (GraphOperation.importanceJudgement(newWordStr) || true) {
				if (repeatNodeMap.containsKey(newWordStr)) {// 判断这个新插入的词语是否在之前有出现重复出现的情况，重复的情况指的是该词语现在在词图中至少有两个节点与它相同
					List<Integer> repeatNodes = repeatNodeMap.get(newWordStr);// 取出所有重复的节点
					// 进行最优合并节点的判断
					perfectMatchNodeIndex = getPerfectMatchNode(newWordList, i, repeatNodes, candiPosition);// 找到这个新插入词语最合适的合并节点
					// 如果没有找到合并节点，或者判断出合并后出现环，就新建节点，否则合并节点
					if (perfectMatchNodeIndex == -1
							|| LoopIsExist(candiPosition.get(candiPosition.size() - 1), perfectMatchNodeIndex)) {// 如果没有找到合适合并的节点或者合并后产生环，则新建节点插入
						nodes.add(new WordNode(newWordStr, 1));// 新建节点
						// 添加新的重复节点
						List<Integer> tempRepeatMapList = repeatNodeMap.get(newWordStr);
						perfectMatchNodeIndex = nodes.size() - 1;
						tempRepeatMapList.add(perfectMatchNodeIndex);
						repeatNodeMap.put(newWordStr, tempRepeatMapList);
						// 插入边
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					} else {// 如果有可以合并的节点则进行节点的合并
						nodes.get(perfectMatchNodeIndex).addScore(1);
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					}
				} else {// 如果该节点在之前没有出现过重复出现的情况
					perfectMatchNodeIndex = nodes.indexOf(new WordNode(newWordStr));
					if (perfectMatchNodeIndex != -1) {// 这个节点之前有出现
						// 判断这个节点是否在本句话中出现过，或者插入后是否产生环
						if (candiPosition.contains(perfectMatchNodeIndex)
								|| LoopIsExist(candiPosition.get(candiPosition.size() - 1), perfectMatchNodeIndex)) {// 如果插入的节点是之前已经选择的节点则插入新的节点并更新map
							nodes.add(new WordNode(newWordStr, 1));
							List<Integer> tempRepeatMapList = new ArrayList<Integer>();
							tempRepeatMapList.add(perfectMatchNodeIndex);
							perfectMatchNodeIndex = nodes.size() - 1;
							tempRepeatMapList.add(perfectMatchNodeIndex);
							repeatNodeMap.put(newWordStr, tempRepeatMapList);
							Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
							if (!edges.contains(addEdge)) {
								edges.add(addEdge);
							}
						} else {// 否则则合并节点
							nodes.get(perfectMatchNodeIndex).addScore(1);
							Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
							if (!edges.contains(addEdge)) {
								edges.add(addEdge);
							}
						}
					} else {// 节点之前没有出现过，则新建节点
						nodes.add(new WordNode(newWordStr, 1));
						perfectMatchNodeIndex = nodes.size() - 1;
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					}
				}
				if (perfectMatchNodeIndex == -1) {
					System.out.println("这里不对id2");
				}
				candiPosition.add(perfectMatchNodeIndex);
			} else {// 暂时没用
				nodes.add(new WordNode(newWordStr, 1));
				perfectMatchNodeIndex = nodes.size() - 1;
				candiPosition.add(perfectMatchNodeIndex);
				Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
				if (!edges.contains(addEdge)) {
					edges.add(addEdge);
				}
			}
		}
		Edge addEdge = new Edge(candiPosition.get(candiPosition.size() - 1), 1);// 将最后一个节点与end节点相连，1表示结束节点的编号
		if (!edges.contains(addEdge)) {
			edges.add(addEdge);
		}
		if (endMap.containsKey(candiPosition.get(candiPosition.size() - 1))) {// 统计连接END节点的词语的信息
			endMap.put(candiPosition.get(candiPosition.size() - 1),
					endMap.get(candiPosition.get(candiPosition.size() - 1)) + 1);
		} else {
			endMap.put(candiPosition.get(candiPosition.size() - 1), 1);
		}
		// LinkNodeByEdge(candiPosition,1);
	}

	// public void LinkNodeByEdge(List<Integer> candiPosition,int type){
	// if(type==1){
	// for(int i=0;i<candiPosition.size()-1;i++){
	// Edge addEdge = new Edge(candiPosition.get(i),candiPosition.get(i+1));
	// if(!edges.contains(addEdge)){
	// edges.add(addEdge);
	// }
	// }
	// }else if(type==2){
	// for(int i=0;i<candiPosition.size()-1;i++){
	// for(int j=i+1;j<candiPosition.size();j++){
	// Edge addEdge = new Edge(candiPosition.get(i),candiPosition.get(j));
	// if(!edges.contains(addEdge)){
	// edges.add(addEdge);
	// }
	// }
	// }
	// }
	// }
	// 判断图中是否有环，这种判断算法可能时间复杂度较高，可以改进
	public boolean LoopIsExist(int start, int end) {
		if (start == end)
			return true;
		Queue<Integer> nodesq = new LinkedList<Integer>();
		nodesq.offer(end);
		ArrayList<Edge> tmp = new ArrayList<Edge>();
		for (int i = 0; i < this.edges.size(); i++) {
			Edge ee = new Edge(this.edges.get(i).startNode, this.edges.get(i).endNode);
			tmp.add(ee);
		}
		while (nodesq.isEmpty() == false) {
			Integer curNode = nodesq.poll();
			for (int kkk = 0; kkk < tmp.size();) {
				if (tmp.get(kkk).startNode == curNode) {
					if (tmp.get(kkk).endNode == start)
						return true;
					else {
						nodesq.offer(tmp.get(kkk).endNode);
						tmp.remove(kkk);
					}
				} else {
					kkk++;
				}
			}
		}
		return false;
	}

	// 对于新插入的一个节点，如果这个节点有多个合并选择，则进行判断
	public int getPerfectMatchNode(List<Term> newWordList, int newNodeindex, List<Integer> repeatNodes,
			List<Integer> candiPosition) {// judge if this word is real exist
		String new_preNodeStr;
		String new_nextNodeStr;
		if (newNodeindex == 0) {// 如果要插入的节点是第一个第一个单词
			new_nextNodeStr = newWordList.get(newNodeindex + 1).toString();// 取出新插入句子单词的下一个单词
			int nextNodeIndex = nodes.indexOf(new WordNode(new_nextNodeStr));// 判断新插入句子单词的下一个单词是否存在于原有节点
			if (nextNodeIndex != -1) {// 如果存在
				for (int ii : repeatNodes) {
					Edge tempEdge = new Edge(ii, nextNodeIndex);
					if (edges.contains(tempEdge)) {// 如果边已经存在
						if (candiPosition.contains(ii)) {
							return -1;
						} else {
							return ii;
						}
					}
				}
			}
		} else if (newNodeindex == newWordList.size() - 1) {// 如果要插入的节点是最后一个单词
			new_preNodeStr = newWordList.get(newNodeindex - 1).toString();// 取出新插入句子单词的上一个单词
			int preNodeIndex = nodes.indexOf(new WordNode(new_preNodeStr));// 判断新插入句子单词的上一个单词是否存在于原有节点
			if (preNodeIndex != -1) {// 如果存在
				for (int ii : repeatNodes) {
					Edge tempEdge = new Edge(preNodeIndex, ii);
					if (edges.contains(tempEdge)) {
						if (candiPosition.contains(ii)) {
							return -1;
						} else {
							return ii;
						}
					}
				}
			}
		} else {
			new_nextNodeStr = newWordList.get(newNodeindex + 1).toString();// 取出新插入句子单词的下一个单词
			int nextNodeIndex = nodes.indexOf(new WordNode(new_nextNodeStr));// 判断新插入句子单词的下一个单词是否存在于原有节点
			if (nextNodeIndex != -1) {// 如果存在
				for (int ii : repeatNodes) {
					Edge tempEdge = new Edge(ii, nextNodeIndex);
					if (edges.contains(tempEdge)) {
						if (candiPosition.contains(ii)) {
							return -1;
						} else {
							return ii;
						}
					}
				}
			}
			new_preNodeStr = newWordList.get(newNodeindex - 1).toString();// 取出新插入句子单词的上一个单词
			int preNodeIndex = nodes.indexOf(new WordNode(new_preNodeStr));// 判断新插入句子单词的上一个单词是否存在于原有节点
			if (preNodeIndex != -1) {// 如果存在
				for (int ii : repeatNodes) {
					Edge tempEdge = new Edge(preNodeIndex, ii);
					if (edges.contains(tempEdge)) {
						if (candiPosition.contains(ii)) {
							return -1;
						} else {
							return ii;
						}
					}
				}
			}
		}
		return repeatNodes.get(0);
	}

	// 设置集束参数为n，找到词图中最优的n条路径
	public List<List<Integer>> getBestNPath(int n) {
		if (n < 1) {
			System.out.println("这里不对id5");
		}
		List<List<Integer>> resultList = new ArrayList<List<Integer>>();
		List<Double> candidateScore = new ArrayList<Double>();
		// 初始化N个结果
		for (int i = 0; i < n; i++) {
			List<Integer> tempList = new ArrayList<Integer>();
			if (i == 0) {
				tempList.add(0);
			} else {
				tempList.add(-1);
			}
			candidateScore.add(0.0);
			resultList.add(tempList);
		}
		boolean flag = true;// 设置标记为true，当标记为false时，表示已经找到5个最优路径，或者已完成遍历，可以终止循环
		while (flag) {
			flag = false;
			List<List<Integer>> tempResultList = new ArrayList<List<Integer>>();// 边的集合
			List<Double> tempCandidateScore = new ArrayList<Double>();// 边的得分的集合
			// 对n条路径进行计算
			for (int i = 0; i < n; i++) {
				double basicScore = candidateScore.get(i);// 获取路径之前的得分
				List<Integer> beforeCandidate = resultList.get(i);// 找到路径之前的节点
				int lastIndex = beforeCandidate.get(beforeCandidate.size() - 1);// 找到路径最后一个节点
				// 如果最后一个节点是-1，表示这条路径错误
				if (lastIndex == -1) {
					continue;
				} else if (lastIndex == 1) {// 如果最后一个节点是1，表示该路径已经走到end节点，因此只需要复制其之前计算的结果
					tempResultList.add(beforeCandidate);
					tempCandidateScore.add(basicScore);
					// if(beforeCandidate.size()>11){
					//
					// }
					continue;
				} else {
					// 只要有一条路径不满足上述条件，就设flag为true，不能终止循环
					flag = true;
				}
				// 遍历所有的边，寻找这条路径的下一个节点
				for (int j = 0; j < edges.size(); j++) {
					if (edges.get(j).startNode == lastIndex) {// 如果找到了下一个节点，就计算加入该节点后的得分
						int newIndex = edges.get(j).endNode;
						// System.out.println(LoopIsExist(lastIndex,newIndex));
						if (beforeCandidate.contains(newIndex)) {// 如果找到路径之前出现过的节点，则不添加继续寻找节点
							System.out.println("here");
							continue;
						}
						List<Integer> newList = new ArrayList<Integer>();
						newList.addAll(beforeCandidate);
						newList.add(newIndex);
						tempResultList.add(newList);
						if (newIndex == 1) {// 如果新加入的节点是END节点
							if (newList.size() < 3) {
								tempCandidateScore.add(0.0);
							} else {
								tempCandidateScore.add(basicScore);
							}
							// if(newList.size()<5){
							// tempCandidateScore.add(basicScore);
							// }else{
							// tempCandidateScore.add(basicScore+endMap.get(lastIndex)*5);
							// }
						} else {
							if (newList.size() < 1) {
								System.out.println("abxxxx");
							}
							tempCandidateScore.add(
									(basicScore * (newList.size() - 1) + nodes.get(newIndex).score) / newList.size());
							// if(newList.size()>10){
							// tempCandidateScore.add(basicScore);
							// }else{
							// tempCandidateScore.add(basicScore+nodes.get(newIndex).score);
							// }
						}
					}
				}
			}
			if (!flag) {
				break;
			}
			if (tempResultList == null) {
				System.out.println("这里不对id3");
			}

			int left = n - tempResultList.size();
			if (left >= 0) {// 如果计算完的路径小于等于n，则直接输出结果
				resultList = tempResultList;
				candidateScore = tempCandidateScore;
				for (int i = 0; i < left; i++) {
					List<Integer> tempList = new ArrayList<Integer>();
					tempList.add(-1);
					resultList.add(tempList);
					candidateScore.add(0.0);
				}
			} else {// 如果计算完的路径条数大于n，则进行堆排序，选出top-n
				List<Integer> sortList = HeapSort.heapSort(tempCandidateScore, n);
				resultList.clear();
				candidateScore.clear();
				for (int i : sortList) {
					resultList.add(tempResultList.get(i));
					candidateScore.add(tempCandidateScore.get(i));
				}
			}
			// for(List<Integer>sis:resultList){
			// for(int si:sis){
			// System.out.print(nodes.get(si).wordString);
			// }
			// System.out.println();
			// }
			// System.out.println("dd");
		}
		return resultList;
	}

	public static void main(String[] args) {
		// WordNode aa = new WordNode("aa",1);
		// WordNode bb = new WordNode("bb",2);
		// ArrayList<WordNode> nodes2=new ArrayList<WordNode>();
		// nodes2.add(aa);
		// nodes2.add(bb);
		// nodes2.get(1).addScore(1);
		// if(nodes2.contains(new WordNode("bb"))){
		// System.out.println(nodes2.get(1).getScore());
		// }
		// Edge aa = new Edge(0,1);
		// Edge bb = new Edge(3,2);
		// ArrayList<Edge> nodes2=new ArrayList<Edge>();
		// nodes2.add(aa);
		// nodes2.add(bb);
		// if(nodes2.contains(new Edge(3,1))){
		// System.out.println(123);
		// }
		// #########################################################
		// #########################################################
		List<String> dataList2 = new ArrayList<String>();
		dataList2.add("习近平于9月份访问美国");
		dataList2.add("习近平访问英国");
		dataList2.add("今年习近平将访问美国");
		dataList2.add("习近平对美国进行国事访问");
		dataList2.add("奥巴马欢迎习近平访问美国");
		dataList2.add("习近平会见美国总统奥巴马");
		dataList2.add("国家主席习近平访问美国总统奥巴马");

		// dataList2.add("沙特东部一警察车队遭枪袭 两名警察身亡");
		// dataList2.add("沙特东部一警察车队遭枪袭 两名警察身亡");
		WordGraph god = new WordGraph();
		EventData OptEvent = new EventData();
		for (String ss : dataList2) {
			OptEvent.preImfoProcessing(ss);
		}
		for (String ss : OptEvent.getSplitSentences()) {
			god.AddCandidateToGraph(ss);
		}
		System.out.println("点：" + god.nodes.size());
		for (int i = 0; i < god.nodes.size(); i++) {
			System.out.println("节点" + i + ":" + god.nodes.get(i).wordString);
		}
		System.out.println("边：" + god.edges.size());
		for (int i = 0; i < god.edges.size(); i++) {
			System.out.println("边" + i + "(" + god.edges.get(i).startNode + "," + god.edges.get(i).endNode + ")");
		}
		for (int ss : god.endMap.keySet()) {
			System.out.println("ednMap：" + god.nodes.get(ss).wordString);
		}
		List<List<Integer>> testt = god.getBestNPath(2);
		for (int i = 0; i < testt.size(); i++) {
			System.out.print("第" + i + "句：");
			for (int j : testt.get(i)) {
				if (j == -1 || j == 0 || j == 1)
					continue;
				System.out.print(god.nodes.get(j).wordString);
			}
			System.out.println();
		}
		// ################################################
		// MongoDB mgdb = new MongoDB();

		// Long endTime = System.currentTimeMillis();
		// Long startTime = System.currentTimeMillis()-1000*3600*(24*4+14);
		// System.out.println("start");
		// List<HotTopic_BackEnd> dataList= mgdb.getArticleByDateL(startTime,
		// endTime);
		// System.out.println("end");
		// String id = "1_767f1757b8f7398094012eec5a8a7aaa";
		// System.out.println("start");
		// List<HotTopic_BackEnd> dataList= mgdb.getArticleById(id);
		// System.out.println("end");
		// int allcount = 0;
		// for(HotTopic_BackEnd hb:dataList){
		// WordGraph god=new WordGraph();
		// Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
		// if(articles.size()<10){
		// continue;
		// }
		// EventData OptEvent = new EventData();
		// System.out.println("id:"+hb.getTOPIC_ID());
		// for(String ss:articles.keySet()){
		// if(allcount++>10){
		// break;
		// }
		// System.out.println("########"+articles.get(ss).getA_TITLE());
		// OptEvent.preImfoProcessing(articles.get(ss).getA_TITLE());
		// }
		// for(String ss:OptEvent.getSplitSentences()){
		// god.AddCandidateToGraph(ss);
		// }
		// System.out.println("点："+god.nodes.size());
		// System.out.println("边："+god.edges.size());
		// List<List<Integer>> testt = god.getBestNPath(5);
		// for(int i=0;i<testt.size();i++){
		// System.out.print("第"+i+"句：");
		// for(int j:testt.get(i)){
		// if(j==-1||j==0||j==1)continue;
		// System.out.print(god.nodes.get(j).wordString);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// }
		// System.out.println("allcount:"+allcount);
		// ##########################################################
		// WordGraph god=new WordGraph();
		// god.nodes.add(new WordNode("2",1));
		// god.nodes.add(new WordNode("3",1));
		// god.nodes.add(new WordNode("4",1));
		// god.edges.add(new Edge(0,1));
		// god.edges.add(new Edge(1,2));
		// god.edges.add(new Edge(2,3));
		// System.out.println(god.LoopIsExist(4,0));
		// god.edges.add(new Edge(3,0));
		// System.out.println(god.LoopIsExist(3,0));
		// ########################################################
		// MongoDB mgdb = new MongoDB();
		// Long endTime = System.currentTimeMillis()-1000*3600*0;
		// Long startTime = System.currentTimeMillis()-1000*3600*24;
		// System.out.println("start");
		// List<HotTopic_BackEnd> dataList= mgdb.getArticleByDateL(startTime,
		// endTime);
		// System.out.println("end");
		// int count = 0;
		// for(HotTopic_BackEnd hb:dataList){
		// Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
		// if(articles.size()<5){
		// System.out.println(hb.getA_TITLE());
		// mgdb.removeBy_topicId(hb.getTOPIC_ID());
		// continue;
		// }
		// count++;
		// }
		// System.out.println("count:"+count);
	}
}
