package search.algorithm;

import java.util.ArrayList;
import java.util.List;

import search.bean.Candidate;
import search.score.PathScore;
import wGroup.Edge;
import wGroup.WordGraph;

public class BeamSearch extends Algorithm {

	private int B = 20;// beam size

	private final int ENDNODE_INDEX = 1;
	private final int MIN_PATH_LEN = 6;

	private int ALL_SAVE_LEVELS = 2;

	public BeamSearch(int b, int aLL_SAVE_LEVELS) {
		super();
		B = b;
		ALL_SAVE_LEVELS = aLL_SAVE_LEVELS;
	}

	@Override
	public List<List<Integer>> search(WordGraph wg, int candiNum) {

		List<Candidate> candidates = new ArrayList<>();
		List<Integer> startPath = new ArrayList<>();
		startPath.add(0);
		Candidate startCandi = new Candidate(startPath, 0);
		candidates.add(startCandi);

		int level = 0;
		while (true) {
			level++;
			List<Candidate> beam = new ArrayList<>();

			for (Candidate candi : candidates) {
				List<Integer> path = candi.getNodeIds();
				int currentNodeId = path.get(path.size() - 1);
				if (currentNodeId == ENDNODE_INDEX) {
					if (path.size() - 2 < MIN_PATH_LEN)// 句子长度要大于等于5
						continue;
					add2Beam(beam, candi);
					// printCN(wg, candi);
					continue;
				}
				List<Integer> outNodeIds = findOutNode(wg, currentNodeId);
				for (int nextNodeId : outNodeIds) {
					List<Integer> nextPath = new ArrayList<>(path);
					nextPath.add(nextNodeId);
					double score = PathScore.calScore(wg, nextPath);
					Candidate nextCandi = new Candidate(nextPath, score);
					add2Beam(beam, nextCandi);
				}
			}
			if (level >= ALL_SAVE_LEVELS)
				candidates = topK(beam, B);
			else
				candidates = beam;

			if (ifAllEnd(candidates))
				break;
		}

		return findNBestPath(candidates, candiNum);
	}

	private void add2Beam(List<Candidate> beam, Candidate candi) {
		beam.add(candi);
	}

	private List<Candidate> topK(List<Candidate> beam, int bSize) {
		if (beam.size() <= 1) {
			return beam;
		}

		List<Candidate> result = new ArrayList<>(bSize);
		result.add(beam.get(0));
		int minCId = 0;
		double minS = beam.get(0).getScore();

		for (int i = 1; i < beam.size(); i++) {
			if (result.size() < bSize) {
				result.add(beam.get(i));
				minCId = findScoreMinScoreId(result);
				minS = result.get(minCId).getScore();
			} else {
				if (beam.get(i).getScore() > minS) {
					// replace
					result.set(minCId, beam.get(i));
					minCId = findScoreMinScoreId(result);
					minS = result.get(minCId).getScore();
				}
			}
		}

		return result;
	}

	private boolean ifAllEnd(List<Candidate> candidates) {
		for (Candidate candi : candidates) {
			List<Integer> path = candi.getNodeIds();
			if (path.get(path.size() - 1) != ENDNODE_INDEX)
				return false;
		}
		return true;
	}

	private int findScoreMinScoreId(List<Candidate> candidates) {
		int minId = 0;
		double minS = candidates.get(0).getScore();
		for (int i = 1; i < candidates.size(); i++) {
			if (candidates.get(i).getScore() < minS) {
				minId = i;
				minS = candidates.get(i).getScore();
			}
		}
		return minId;
	}

	private void printCN(WordGraph wg, Candidate candi) {
		List<Integer> path = candi.getNodeIds();
		if (path.size() - 2 < 4)
			return;

		String candidateSent = "";
		for (int nodeId : path) {
			if (nodeId == -1 || nodeId == 0 || nodeId == 1)
				continue;
			candidateSent += wg.nodes.get(nodeId).wordString.split("/")[0];
		}
		System.out.println(candi.getScore() + " " + candidateSent);

	}

	private List<List<Integer>> findNBestPath(List<Candidate> candidates,
			int candiNum) {
		List<Candidate> topNCandi = topK(candidates, candiNum);
		List<List<Integer>> result = new ArrayList<>();

		for (Candidate candi : topNCandi) {
			result.add(candi.getNodeIds());
		}

		return result;

	}

}
