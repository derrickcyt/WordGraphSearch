package search.algorithm;

import java.util.ArrayList;
import java.util.List;

import wGroup.Edge;
import wGroup.WordGraph;

public abstract class Algorithm {

	public abstract List<List<Integer>> search(WordGraph wg, int pathNum);

	protected List<Integer> findOutNode(WordGraph wg, int nodeId) {
		List<Edge> edges = wg.edges;
		List<Integer> result = new ArrayList<>();
		for (Edge e : edges) {
			if (e.getStartNode() == nodeId)
				result.add(e.getEndNode());
		}
		return result;
	}

}
