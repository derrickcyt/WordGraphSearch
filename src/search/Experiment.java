package search;

import java.io.IOException;
import java.util.List;

import derrick.io.file.TextWriter;
import ltpTools.LtpOnline;
import search.algorithm.Algorithm;
import search.algorithm.BFS;
import search.algorithm.BeamSearch;
import search.bean.Topic;
import search.score.PathScore;
import wGroup.Edge;
import wGroup.WordGraph;
import wGroup.WordNode;

public class Experiment {

	public static void saveDrawfile(String path, WordGraph wg) {
		TextWriter writer = new TextWriter(path, "UTF-8");
		writer.init();

		// write words
		for (WordNode wn : wg.nodes) {
			writer.write(wn.wordString + " ");
		}
		writer.write("\n");

		for (Edge e : wg.edges) {
			writer.write(e.getStartNode() + "-" + e.getEndNode() + " ");
		}
		writer.write("\n");

		writer.close();
	}

	static class Result {
		double avgScore;
		long avgTime;
		int BSize;
		int allLevelNum;

		public Result(double avgScore, long avgTime, int bSize, int allLevelNum) {
			super();
			this.avgScore = avgScore;
			this.avgTime = avgTime;
			BSize = bSize;
			this.allLevelNum = allLevelNum;
		}

		public double getAvgScore() {
			return avgScore;
		}

		public void setAvgScore(double avgScore) {
			this.avgScore = avgScore;
		}

		public long getAvgTime() {
			return avgTime;
		}

		public void setAvgTime(long avgTime) {
			this.avgTime = avgTime;
		}

		public int getBSize() {
			return BSize;
		}

		public void setBSize(int bSize) {
			BSize = bSize;
		}

		public int getAllLevelNum() {
			return allLevelNum;
		}

		public void setAllLevelNum(int allLevelNum) {
			this.allLevelNum = allLevelNum;
		}

		@Override
		public String toString() {
			return "Result [avgScore=" + avgScore + ", avgTime=" + avgTime
					+ ", BSize=" + BSize + ", allLevelNum=" + allLevelNum + "]";
		}

	}

	public static Result runExperiment(int bSize, int allLevelNum,
			List<Topic> testData, String outputPath) {
		TextWriter writer = new TextWriter(outputPath, "UTF-8");
		writer.init();

		Algorithm searchAlgorithm = new BeamSearch(bSize, allLevelNum);
		int startTopicId = 0;
		long allCostTime = 0;
		int topicCount = 0;
		double totalScore = 0;
		for (Topic t : testData) {
			if (t.getTopicId() < startTopicId)
				continue;
			writer.write("--topic " + t.getTopicId() + " start--\n");
			WordGraph wg = new WordGraph();
			for (String title : t.getTitles()) {
				try {
					wg.addSent2Graph(title);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// System.out.println("点：" + wg.nodes.size());
			// System.out.println("边：" + wg.edges.size());
			// List<List<Integer>> testt = wg.getBestNPath(10);//
			long startTime = System.currentTimeMillis();
			List<List<Integer>> testt = searchAlgorithm.search(wg, 1);
			long endTime = System.currentTimeMillis();
			long costTime = endTime - startTime;
			System.out.println("cost:" + costTime);
			topicCount++;
			allCostTime += costTime;
			for (List<Integer> path : testt) {
				String candidateSent = "";
				for (int nodeId : path) {
					if (nodeId == -1 || nodeId == 0 || nodeId == 1)
						continue;
					candidateSent += wg.nodes.get(nodeId).wordString.split("/")[0];
				}
				totalScore += PathScore.calScore(wg, path);
				System.out.println(candidateSent + " "
						+ PathScore.calScore(wg, path));
				writer.write(candidateSent + "\n");
			}
			// System.out.println("--topic " + t.getTopicId() + " end--");
			writer.write("--topic " + t.getTopicId() + " end--\n");
			// saveDrawfile("test_data/draw_file/"+t.getTopicId()+".txt", wg);
		}
		writer.close();

		return new Result(totalScore / topicCount, allCostTime / topicCount,
				bSize, allLevelNum);

	}

	public static void main(String[] args) throws IOException {
		List<Topic> testData = DataInput.readTestData();
		TextWriter writer = new TextWriter("test_data/result/statastic.txt",
				"UTF-8");
		writer.init();

		int[] bSizeArr = { 5, 10, 20, 30 };
		int[] allLevelNumArr = { 0, 1, 2, 3, 4 };

		// 全保留层数实验
		for (int allLevelNum : allLevelNumArr) {
			System.out.println("all level num:" + allLevelNum);
			Result result = runExperiment(10, allLevelNum, testData,
					"test_data/result/10_" + allLevelNum + ".txt");
			writer.write(result.toString() + "\n");
		}

		// 集束宽度实验
		for (int bSize : bSizeArr) {
			System.out.println("b size:" + bSize);
			Result result = runExperiment(bSize, 2, testData,
					"test_data/result/" + bSize + "_2.txt");
			writer.write(result.toString() + "\n");
		}
		writer.close();

	}
}
