package similarity;

import java.io.IOException;
import java.util.Collection;

/**
 * 刘群老师计算义原相似度的方法, 实现了SememeParser中定义的抽象方法
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验�?
 * 
 * @author <a href="xiat@ruc.edu.cn">xiatian</a>
 * @version 1.0
 */
public class LiuqunSememeParser extends BaseSememeParser {
		
	/** 计算义元相似度的可调节的参数，默认为1.6 */
	private final float alpha = 1.6f;	
	
	public LiuqunSememeParser() throws IOException {
		super();		
	}

	/**
	 * 计算两个义元之间的相似度，由于义元可能相同，计算结果为其中相似度�?���?
	 * <br/>similarity = alpha/(distance+alpha)
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 */
	@Override
	public double getSimilarity(String item1, String item2) {
		int pos;

		// 如果为空串，直接返回0
		if (item1 == null || item2 == null || item1.equals("")
				|| item2.equals(""))
			return 0.0;

		String key1 = item1.trim();
		String key2 = item2.trim();

		// 去掉()符号
		if ((key1.charAt(0) == '(') && (key1.charAt(key1.length() - 1) == ')')) {
			if (key2.charAt(0) == '(' && key2.charAt(key2.length() - 1) == ')') {
				key1 = key1.substring(1, key1.length() - 1);
				key2 = key2.substring(1, key2.length() - 1);
			} else {
				return 0.0;
			}
		}

		// 处理关系义元,即x=y的情�?
		if ((pos = key1.indexOf('=')) > 0) {
			int pos2 = key2.indexOf('=');
			// 如果是关系义元，则判断前面部分是否相同，如果相同，则转为计算后面部分的相似度，否则为0
			if ((pos == pos2)
					&& key1.substring(0, pos).equals(key2.substring(0, pos2))) {
				key1 = key1.substring(pos + 1);
				key2 = key2.substring(pos2 + 1);
			} else {
				return 0.0;
			}
		}

		// 处理符号义元,即前面有特殊符号的义�?
		String symbol1 = key1.substring(0, 1);
		String symbol2 = key2.substring(0, 1);

		for (int i = 0; i < Symbol_Descriptions.length; i++) {
			if (symbol1.equals(Symbol_Descriptions[i][0])) {
				if (symbol1.equals(symbol2)) {
					key1 = item1.substring(1);
					key2 = item2.substring(1);
					break;
				} else {
					return 0.0; // 如果不是同一关系符号，则相似度直接返�?
				}
			}
		}

		if ((pos = key1.indexOf("|")) >= 0) {
			key1 = key1.substring(pos + 1);
		}
		if ((pos = key2.indexOf("|")) >= 0) {
			key2 = key2.substring(pos + 1);
		}

		int distance = getMinDistance(key1, key2);
		return alpha / (distance + alpha);
	}

	/**
	 * 根据汉语定义计算义原之间的距�?Integer.MAX_VALUE代表两个义元之间的距离为无穷大，由于可能多个义元有相同的汉语词语�?
	 * 故计算结果为其中距离�?���?
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 */
	public int getMinDistance(String sememe1, String sememe2) {
		int distance = Integer.MAX_VALUE;

		// 如果两个字符串相等，直接返回距离�?
		if (sememe1.equals(sememe2)) {
			return 0;
		}

		Collection<String> sememeIds1 = SEMEMES.get(sememe1);
		Collection<String> sememeIds2 = SEMEMES.get(sememe2);
		
		// 如果sememe1或�?sememe2不是义元,则返回无穷大
		if (sememeIds1.size() == 0 || sememeIds1.size() == 0) {
			return Integer.MAX_VALUE;
		}

		for(String id1:sememeIds1){
			for(String id2:sememeIds2){
				int d = getDistance(id1, id2);
				if(d<distance){
					distance = d;
				}
			}
		}
		
		return distance;
	}

	/**
	 * 根据义原的具有层次的Id获取两个义原之间的语义距�?
	 * @param id1
	 * @param id2
	 * @return
	 */
	int getDistance(String id1, String id2) {
		// 两个Id相同的位置终止地�?
		int position = 0;
		String[] array1 = id1.split("-");
		String[] array2 = id2.split("-");
		for (position = 0; position < array1.length && position < array2.length; position++) {
			if (!array1[position].equals(array2[position])) {
				return array1.length + array2.length - position - position;
			}
		}

		if (array1.length == array2.length) {
			return 0;
		} else if (array1.length == position) {
			return array2.length - position;
		} else {
			return array1.length - position;
		}
	}
}
