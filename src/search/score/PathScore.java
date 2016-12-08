package search.score;

import java.util.List;

import search.lm.LMDBReader;
import search.lm.LMReader;

import wGroup.Edge;
import wGroup.WordGraph;

public class PathScore {

	public static double LAMBDA = 0.5;

	public static LMReader lmr;

	static {
		lmr = new LMDBReader();
	}

	private static double getLMProp(String w1, String w2) {

		return 0.0;
	}

	private static double calEdgeScore(WordGraph wg, int edgeId) {
		Edge edge = wg.edges.get(edgeId);
		double v1 = wg.nodes.get(edge.getStartNode()).getScore();
		double v2 = wg.nodes.get(edge.getEndNode()).getScore();
		double e_w = edge.getScore();
		return (v1 + v2) / e_w;
	}

	private static double calPathEdgeScore(WordGraph wg, List<Integer> path) {
		int pathLen = path.size() - 2;
		if (pathLen <= 0)
			return 0.0;
		double result = 0.0;
		for (int i = 0; i < path.size() - 1; i++) {
			Edge edge = new Edge(path.get(i), path.get(i + 1));
			int index = wg.edges.indexOf(edge);
			result += Math.log(calEdgeScore(wg, index));
		}
		return result / pathLen;
	}

	private static double calFlu(WordGraph wg, List<Integer> path) {
		int pathLen = path.size() - 2;
		if (pathLen <= 0)
			return -100;
		if (pathLen < 3)
			return 0;
		double result = 0.0;
		for (int i = 3; i < path.size() - 1; i++) {
			String w1 = wg.nodes.get(path.get(i - 2)).wordString.split("/")[0];
			String w2 = wg.nodes.get(path.get(i - 1)).wordString.split("/")[0];
			String w3 = wg.nodes.get(path.get(i)).wordString.split("/")[0];
			result += Math.log(lmr.read(w1, w2, w3));
		}

		return result / (pathLen - 2) + 10;
	}

	public static double calScore(WordGraph wg, List<Integer> path) {
		int pathLen = path.size() - 2;
		if (pathLen < 2) {
			return calPathEdgeScore(wg, path);
		}
		return calPathEdgeScore(wg, path) + LAMBDA * calFlu(wg, path);
	}

}
