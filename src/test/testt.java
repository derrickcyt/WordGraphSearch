package test;

public class testt {
	public static void main(String []args){
		String content = "asd!asdasd£º°¢Ë¹¶Ù¡£";
		System.out.println(content.replaceAll("!|a", ""));
	}
}
