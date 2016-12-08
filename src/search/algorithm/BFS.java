package search.algorithm;

import java.util.ArrayList;
import java.util.List;

import search.score.PathScore;

import wGroup.Edge;
import wGroup.WordGraph;

public class BFS extends Algorithm {

	private final int ENDNODE_INDEX = 1;
	private final int MIN_PATH_LEN = 5;
	private final int MAX_PATH_LEN = 12;

	@Override
	public List<List<Integer>> search(WordGraph wg, int pathNum) {
		List<List<Integer>> globalOptPath = new ArrayList<>(10);// 保存全局前10最优
		List<Double> globalScore = new ArrayList<>(10);// 全局最优的得分
		List<List<Integer>> currentPath = new ArrayList<>();// 保存当前所有路径，到END判断是否能加入Global，后剔除

		// 初始化路径
		List<Integer> firstNodes = findOutNode(wg, 0);
		for (Integer fn : firstNodes) {
			List<Integer> path = new ArrayList<>();
			path.add(0);
			path.add(fn);
			currentPath.add(path);
		}

		double minSInG = Double.MIN_VALUE;
		int minIdInG = -1;
		int count = 0;
		// 开始搜索
		while (!ifAllEnd(currentPath)) {
			List<List<Integer>> addList = new ArrayList<>();

			for (List<Integer> path : currentPath) {
				// 为防止有环阻碍，这里限制一下长度
				if (path.size() - 2 > MAX_PATH_LEN) {
					continue;
				}

				int currentNodeId = path.get(path.size() - 1);
				List<Integer> nextNodes = findOutNode(wg, currentNodeId);
				for (Integer nextNode : nextNodes) {
					List<Integer> tmpPath = new ArrayList<>(path);
					tmpPath.add(nextNode);
					// printPath(tmpPath);
					if (nextNode == ENDNODE_INDEX) {
						if (path.size() - 1 < MIN_PATH_LEN)
							continue;
						// System.out.println(++count);
						// printCN(wg, tmpPath);
						double score = PathScore.calScore(wg, tmpPath);
						if (score > minSInG) {
							printCN(wg, tmpPath);

							if (globalOptPath.size() < 10) {
								globalOptPath.add(tmpPath);
								globalScore.add(score);
							} else {// 把最小的替換
								globalOptPath.set(minIdInG, tmpPath);
								globalScore.set(minIdInG, score);
							}
							// 找最小的Global
							minIdInG = findGlobalMinScoreId(globalScore);
							minSInG = globalScore.get(minIdInG);
						}

					} else {
						// 加入到当前路径集合
						addList.add(tmpPath);
					}
				}
			}
			currentPath.clear();
			currentPath.addAll(addList);
		}

		return globalOptPath;
	}

	private int findGlobalMinScoreId(List<Double> globalScore) {
		int minId = 0;
		double minS = globalScore.get(0);
		for (int i = 1; i < globalScore.size(); i++) {
			if (globalScore.get(i) < minS) {
				minId = i;
				minS = globalScore.get(i);
			}
		}
		return minId;
	}

	private boolean ifAllEnd(List<List<Integer>> currentOptPath) {
		for (List<Integer> path : currentOptPath) {
			if (path.get(path.size() - 1) != ENDNODE_INDEX) {
				return false;
			}
		}
		return true;
	}

	private void printPath(List<Integer> path) {
		for (int nodeId : path) {
			System.out.print(nodeId + " ");
		}
		System.out.println();
	}

	private void printCN(WordGraph wg, List<Integer> path) {
		if (path.size() - 2 < 4)
			return;

		String candidateSent = "";
		for (int nodeId : path) {
			if (nodeId == -1 || nodeId == 0 || nodeId == 1)
				continue;
			candidateSent += wg.nodes.get(nodeId).wordString.split("/")[0];
		}
		System.out.println(PathScore.calScore(wg, path) + " " + candidateSent);

	}

}
