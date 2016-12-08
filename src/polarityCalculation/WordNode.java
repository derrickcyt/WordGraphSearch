package polarityCalculation;

/**
 * @author lizhixun
 *
 */
public class WordNode {
	private String word; // 词语
	private String pos;  // 原始词�?
	private String pro_pos; // 处理后的词�?，d+v, 或�?d+a等�?。�?
	private String pro_word; // 处理后的词语�?
	private Double value;   // 值，初始时存放原始�?，经过处理后存放的是处理后的�?
	
	public WordNode(String word, String pos, double value){
		this.word = word;
		this.pos = pos;
		this.value = value;
		this.pro_pos = pos;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getPro_pos() {
		return pro_pos;
	}
	public void setPro_pos(String pro_pos) {
		this.pro_pos = pro_pos;
	}
	public String getPro_word() {
		return pro_word;
	}
	public void setPro_word(String pro_word) {
		this.pro_word = pro_word;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
