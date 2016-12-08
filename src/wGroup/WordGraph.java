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
	public ArrayList<WordNode> nodes = new ArrayList<WordNode>();// �ڵ�
	public ArrayList<Edge> edges = new ArrayList<Edge>();// ��
	Map<String, List<Integer>> repeatNodeMap = new HashMap<String, List<Integer>>();// ���ܺϲ��ڵ��¼
	Map<String, String> symbolMap = new HashMap<String, String>();
	Map<Integer, Integer> endMap = new HashMap<Integer, Integer>();// �����ڵ�ͳ��
	// ��ʼ����ͼ

	public WordGraph() {
		WordNode start = new WordNode("STARTNODE", 0);
		WordNode end = new WordNode("ENDNODE", 0);
		this.nodes.add(start);
		this.nodes.add(end);
		// һЩ�ִʴ���ľ���
		symbolMap.put("\"", "\"");
		symbolMap.put("��/w", "��/w");
		symbolMap.put("��/w", "��/w");
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

		if (terms == null || terms.size() < 2) {// ����С��2���޳�
			return;
		}

		List<Integer> path = new ArrayList<>();// �þ������ɵ�·����ʼ��
		path.add(0);// ���뿪ʼ�ڵ�STARTNODE
		nodes.get(0).addScore(1);
		// ����ÿһ����
		for (int i = 0; i < terms.size(); i++) {
			String termStr = terms.get(i).toString();
			int preId = i;// ǰһ���Ѵ���Ľڵ���path��λ��, i����ִʵĵ�ǰλ��
			// ������һ���ڵ��id
			int perfectMatchNodeId = -1;
			if (repeatNodeMap.containsKey(termStr)) {// ���֮ǰ�г��ֹ�����ʣ��Ҵ��ڵ���2��
				List<Integer> repeatNodeIds = repeatNodeMap.get(termStr);
				// Ѱ�����źϲ��ڵ㣬��Ҫ��������ǰ���һ����û����ͬģʽ
				perfectMatchNodeId = getPerfectMatchNode(terms, i, repeatNodeIds, path);
				// ���û���ҵ��ϲ��ڵ�,���ҵ��˵��ǳ��ֻ�
				if (perfectMatchNodeId == -1 || LoopIsExist(path.get(preId), perfectMatchNodeId)) {
					nodes.add(new WordNode(termStr, 1));// �½��ڵ�
					perfectMatchNodeId = nodes.size() - 1;// �½ڵ�id
					// ��ӵ��ظ���¼��
					repeatNodeIds.add(perfectMatchNodeId);
					// �����
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
					// �����½ڵ㣬���Բ����������б�
					edges.add(newE);
				} else {// �ɺϲ�
					nodes.get(perfectMatchNodeId).addScore(1);
					// �ж��Ƿ��Ѿ����ڱ�
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);// ���ڶ�λ���б�
					if (edges.contains(newE)) {
						edges.get(edges.indexOf(newE)).addScore(1);
					} else {
						edges.add(newE);
					}
				}
			} else {// û���ظ����ֹ�
				// ��֮ǰ��û�г��ֹ�
				perfectMatchNodeId = nodes.indexOf(new WordNode(termStr));
				if (perfectMatchNodeId != -1) {// ���ֹ�
					// �ж��Ƿ����¾���ֹ������߲�����Ƿ������
					if (path.contains(perfectMatchNodeId)
							|| LoopIsExist(path.get(path.size() - 1), perfectMatchNodeId)) {// �������½��ڵ�
						nodes.add(new WordNode(termStr, 1));// �½��ڵ�
						// ��¼�ظ��ڵ�
						List<Integer> tmpRepeatNodeIds = new ArrayList<>();
						tmpRepeatNodeIds.add(perfectMatchNodeId);
						perfectMatchNodeId = nodes.size() - 1;// �½ڵ�id
						tmpRepeatNodeIds.add(perfectMatchNodeId);
						repeatNodeMap.put(termStr, tmpRepeatNodeIds);
						// �����
						Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
						// �����½ڵ㣬���Բ����������б�
						edges.add(newE);
					} else {// �������㣬�ɺϲ�
						nodes.get(perfectMatchNodeId).addScore(1);
						// �����
						Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
						if (!edges.contains(newE))
							edges.add(newE);
						else
							edges.get(edges.indexOf(newE)).addScore(1);
					}
				} else {// û���ֹ����½�
					nodes.add(new WordNode(termStr, 1));// �½��ڵ�
					perfectMatchNodeId = nodes.size() - 1;
					// �����
					Edge newE = new Edge(path.get(preId), perfectMatchNodeId);
					// �����½ڵ㣬���Բ����������б�
					edges.add(newE);
				}
			}
			if (perfectMatchNodeId != -1)
				path.add(perfectMatchNodeId);
			else
				throw new Exception("�Ҳ����ڵ����");
		}
		Edge newE = new Edge(path.get(path.size() - 1), 1);
		edges.add(newE);
		nodes.get(1).addScore(1);
	}

	// ����ͼ����Ӿ���
	public void AddCandidateToGraph(String sentence) {
		List<Term> newWordList = Segmentation.toAnalysis(sentence);// �ִ�
		if (newWordList == null || newWordList.size() < 2) {
			return;
		}
		List<Integer> candiPosition = new ArrayList<Integer>();
		// ��ӿ�ʼ�ڵ�0
		candiPosition.add(0);
		label1: for (int i = 0; i < newWordList.size(); i++) {
			String newWordStr = newWordList.get(i).toString();
			// �����ֺ������ŵȽ��кϲ�
			// if(symbolMap.containsKey(newWordStr)){
			// System.out.println("#####################"+newWordStr);
			// String endSymbol = symbolMap.get(newWordStr);
			// int back =i;
			// i++;
			// while(!newWordList.get(i).toString().equals(endSymbol)){
			// newWordStr+=newWordList.get(i).toString();
			// if(i==newWordList.size()-1){
			// System.out.println("�жϷ���ƥ��ʱ����"+newWordStr);
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
			// �����ֺ������ŵȽ��кϲ�����

			// ��ʼ����һ���ڵ������
			int perfectMatchNodeIndex = -1;
			// ######################
			// ����#�ű�ʾ��仰û�����ӿ�ʼ�ڵ㣬��˲��ܽ����뿪ʼ�ڵ�����
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
				if (repeatNodeMap.containsKey(newWordStr)) {// �ж�����²���Ĵ����Ƿ���֮ǰ�г����ظ����ֵ�������ظ������ָ���Ǹô��������ڴ�ͼ�������������ڵ�������ͬ
					List<Integer> repeatNodes = repeatNodeMap.get(newWordStr);// ȡ�������ظ��Ľڵ�
					// �������źϲ��ڵ���ж�
					perfectMatchNodeIndex = getPerfectMatchNode(newWordList, i, repeatNodes, candiPosition);// �ҵ�����²����������ʵĺϲ��ڵ�
					// ���û���ҵ��ϲ��ڵ㣬�����жϳ��ϲ�����ֻ������½��ڵ㣬����ϲ��ڵ�
					if (perfectMatchNodeIndex == -1
							|| LoopIsExist(candiPosition.get(candiPosition.size() - 1), perfectMatchNodeIndex)) {// ���û���ҵ����ʺϲ��Ľڵ���ߺϲ�������������½��ڵ����
						nodes.add(new WordNode(newWordStr, 1));// �½��ڵ�
						// ����µ��ظ��ڵ�
						List<Integer> tempRepeatMapList = repeatNodeMap.get(newWordStr);
						perfectMatchNodeIndex = nodes.size() - 1;
						tempRepeatMapList.add(perfectMatchNodeIndex);
						repeatNodeMap.put(newWordStr, tempRepeatMapList);
						// �����
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					} else {// ����п��Ժϲ��Ľڵ�����нڵ�ĺϲ�
						nodes.get(perfectMatchNodeIndex).addScore(1);
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					}
				} else {// ����ýڵ���֮ǰû�г��ֹ��ظ����ֵ����
					perfectMatchNodeIndex = nodes.indexOf(new WordNode(newWordStr));
					if (perfectMatchNodeIndex != -1) {// ����ڵ�֮ǰ�г���
						// �ж�����ڵ��Ƿ��ڱ��仰�г��ֹ������߲�����Ƿ������
						if (candiPosition.contains(perfectMatchNodeIndex)
								|| LoopIsExist(candiPosition.get(candiPosition.size() - 1), perfectMatchNodeIndex)) {// �������Ľڵ���֮ǰ�Ѿ�ѡ��Ľڵ�������µĽڵ㲢����map
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
						} else {// ������ϲ��ڵ�
							nodes.get(perfectMatchNodeIndex).addScore(1);
							Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
							if (!edges.contains(addEdge)) {
								edges.add(addEdge);
							}
						}
					} else {// �ڵ�֮ǰû�г��ֹ������½��ڵ�
						nodes.add(new WordNode(newWordStr, 1));
						perfectMatchNodeIndex = nodes.size() - 1;
						Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
						if (!edges.contains(addEdge)) {
							edges.add(addEdge);
						}
					}
				}
				if (perfectMatchNodeIndex == -1) {
					System.out.println("���ﲻ��id2");
				}
				candiPosition.add(perfectMatchNodeIndex);
			} else {// ��ʱû��
				nodes.add(new WordNode(newWordStr, 1));
				perfectMatchNodeIndex = nodes.size() - 1;
				candiPosition.add(perfectMatchNodeIndex);
				Edge addEdge = new Edge(candiPosition.get(i), perfectMatchNodeIndex);
				if (!edges.contains(addEdge)) {
					edges.add(addEdge);
				}
			}
		}
		Edge addEdge = new Edge(candiPosition.get(candiPosition.size() - 1), 1);// �����һ���ڵ���end�ڵ�������1��ʾ�����ڵ�ı��
		if (!edges.contains(addEdge)) {
			edges.add(addEdge);
		}
		if (endMap.containsKey(candiPosition.get(candiPosition.size() - 1))) {// ͳ������END�ڵ�Ĵ������Ϣ
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
	// �ж�ͼ���Ƿ��л��������ж��㷨����ʱ�临�ӶȽϸߣ����ԸĽ�
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

	// �����²����һ���ڵ㣬�������ڵ��ж���ϲ�ѡ��������ж�
	public int getPerfectMatchNode(List<Term> newWordList, int newNodeindex, List<Integer> repeatNodes,
			List<Integer> candiPosition) {// judge if this word is real exist
		String new_preNodeStr;
		String new_nextNodeStr;
		if (newNodeindex == 0) {// ���Ҫ����Ľڵ��ǵ�һ����һ������
			new_nextNodeStr = newWordList.get(newNodeindex + 1).toString();// ȡ���²�����ӵ��ʵ���һ������
			int nextNodeIndex = nodes.indexOf(new WordNode(new_nextNodeStr));// �ж��²�����ӵ��ʵ���һ�������Ƿ������ԭ�нڵ�
			if (nextNodeIndex != -1) {// �������
				for (int ii : repeatNodes) {
					Edge tempEdge = new Edge(ii, nextNodeIndex);
					if (edges.contains(tempEdge)) {// ������Ѿ�����
						if (candiPosition.contains(ii)) {
							return -1;
						} else {
							return ii;
						}
					}
				}
			}
		} else if (newNodeindex == newWordList.size() - 1) {// ���Ҫ����Ľڵ������һ������
			new_preNodeStr = newWordList.get(newNodeindex - 1).toString();// ȡ���²�����ӵ��ʵ���һ������
			int preNodeIndex = nodes.indexOf(new WordNode(new_preNodeStr));// �ж��²�����ӵ��ʵ���һ�������Ƿ������ԭ�нڵ�
			if (preNodeIndex != -1) {// �������
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
			new_nextNodeStr = newWordList.get(newNodeindex + 1).toString();// ȡ���²�����ӵ��ʵ���һ������
			int nextNodeIndex = nodes.indexOf(new WordNode(new_nextNodeStr));// �ж��²�����ӵ��ʵ���һ�������Ƿ������ԭ�нڵ�
			if (nextNodeIndex != -1) {// �������
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
			new_preNodeStr = newWordList.get(newNodeindex - 1).toString();// ȡ���²�����ӵ��ʵ���һ������
			int preNodeIndex = nodes.indexOf(new WordNode(new_preNodeStr));// �ж��²�����ӵ��ʵ���һ�������Ƿ������ԭ�нڵ�
			if (preNodeIndex != -1) {// �������
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

	// ���ü�������Ϊn���ҵ���ͼ�����ŵ�n��·��
	public List<List<Integer>> getBestNPath(int n) {
		if (n < 1) {
			System.out.println("���ﲻ��id5");
		}
		List<List<Integer>> resultList = new ArrayList<List<Integer>>();
		List<Double> candidateScore = new ArrayList<Double>();
		// ��ʼ��N�����
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
		boolean flag = true;// ���ñ��Ϊtrue�������Ϊfalseʱ����ʾ�Ѿ��ҵ�5������·������������ɱ�����������ֹѭ��
		while (flag) {
			flag = false;
			List<List<Integer>> tempResultList = new ArrayList<List<Integer>>();// �ߵļ���
			List<Double> tempCandidateScore = new ArrayList<Double>();// �ߵĵ÷ֵļ���
			// ��n��·�����м���
			for (int i = 0; i < n; i++) {
				double basicScore = candidateScore.get(i);// ��ȡ·��֮ǰ�ĵ÷�
				List<Integer> beforeCandidate = resultList.get(i);// �ҵ�·��֮ǰ�Ľڵ�
				int lastIndex = beforeCandidate.get(beforeCandidate.size() - 1);// �ҵ�·�����һ���ڵ�
				// ������һ���ڵ���-1����ʾ����·������
				if (lastIndex == -1) {
					continue;
				} else if (lastIndex == 1) {// ������һ���ڵ���1����ʾ��·���Ѿ��ߵ�end�ڵ㣬���ֻ��Ҫ������֮ǰ����Ľ��
					tempResultList.add(beforeCandidate);
					tempCandidateScore.add(basicScore);
					// if(beforeCandidate.size()>11){
					//
					// }
					continue;
				} else {
					// ֻҪ��һ��·����������������������flagΪtrue��������ֹѭ��
					flag = true;
				}
				// �������еıߣ�Ѱ������·������һ���ڵ�
				for (int j = 0; j < edges.size(); j++) {
					if (edges.get(j).startNode == lastIndex) {// ����ҵ�����һ���ڵ㣬�ͼ������ýڵ��ĵ÷�
						int newIndex = edges.get(j).endNode;
						// System.out.println(LoopIsExist(lastIndex,newIndex));
						if (beforeCandidate.contains(newIndex)) {// ����ҵ�·��֮ǰ���ֹ��Ľڵ㣬����Ӽ���Ѱ�ҽڵ�
							System.out.println("here");
							continue;
						}
						List<Integer> newList = new ArrayList<Integer>();
						newList.addAll(beforeCandidate);
						newList.add(newIndex);
						tempResultList.add(newList);
						if (newIndex == 1) {// ����¼���Ľڵ���END�ڵ�
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
				System.out.println("���ﲻ��id3");
			}

			int left = n - tempResultList.size();
			if (left >= 0) {// ����������·��С�ڵ���n����ֱ��������
				resultList = tempResultList;
				candidateScore = tempCandidateScore;
				for (int i = 0; i < left; i++) {
					List<Integer> tempList = new ArrayList<Integer>();
					tempList.add(-1);
					resultList.add(tempList);
					candidateScore.add(0.0);
				}
			} else {// ����������·����������n������ж�����ѡ��top-n
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
		dataList2.add("ϰ��ƽ��9�·ݷ�������");
		dataList2.add("ϰ��ƽ����Ӣ��");
		dataList2.add("����ϰ��ƽ����������");
		dataList2.add("ϰ��ƽ���������й��·���");
		dataList2.add("�°���ӭϰ��ƽ��������");
		dataList2.add("ϰ��ƽ���������ͳ�°���");
		dataList2.add("������ϯϰ��ƽ����������ͳ�°���");

		// dataList2.add("ɳ�ض���һ���쳵����ǹϮ ������������");
		// dataList2.add("ɳ�ض���һ���쳵����ǹϮ ������������");
		WordGraph god = new WordGraph();
		EventData OptEvent = new EventData();
		for (String ss : dataList2) {
			OptEvent.preImfoProcessing(ss);
		}
		for (String ss : OptEvent.getSplitSentences()) {
			god.AddCandidateToGraph(ss);
		}
		System.out.println("�㣺" + god.nodes.size());
		for (int i = 0; i < god.nodes.size(); i++) {
			System.out.println("�ڵ�" + i + ":" + god.nodes.get(i).wordString);
		}
		System.out.println("�ߣ�" + god.edges.size());
		for (int i = 0; i < god.edges.size(); i++) {
			System.out.println("��" + i + "(" + god.edges.get(i).startNode + "," + god.edges.get(i).endNode + ")");
		}
		for (int ss : god.endMap.keySet()) {
			System.out.println("ednMap��" + god.nodes.get(ss).wordString);
		}
		List<List<Integer>> testt = god.getBestNPath(2);
		for (int i = 0; i < testt.size(); i++) {
			System.out.print("��" + i + "�䣺");
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
		// System.out.println("�㣺"+god.nodes.size());
		// System.out.println("�ߣ�"+god.edges.size());
		// List<List<Integer>> testt = god.getBestNPath(5);
		// for(int i=0;i<testt.size();i++){
		// System.out.print("��"+i+"�䣺");
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
