package polarityCalculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PolarityDemo {
	
	private static Polarity polarity;
	static {
		polarity = new Polarity();
	}
	
	public static double calWeibo(String content){
		return polarity.startCalPolarity(content);
	}
	
	 public static double calXinwen(String title,String content)
	{
		 List<WordNode> title_nodes=null;
		 List<WordNode> content_nodes=null;
		 
		List<List<WordNode>> wn_title=polarity.startCalPolarity2(title);
		List<List<WordNode>> wn_content=polarity.startCalPolarity2(content);
		double v_title = 0;
		double v_content = 0;
		title_nodes = new ArrayList<WordNode>();
		content_nodes = new ArrayList<WordNode>();
		for(int i=0; i<wn_title.size(); ++i){
			List<WordNode> wn = wn_title.get(i);
			title_nodes.addAll(wn);
			for(int j=0; j<wn.size(); ++j){
				v_title += wn.get(j).getValue();//将标题中极性词极性累加
			}
		}
		
		// 归一化title的值
		
		v_title = guiyi(v_title);
		
		for(int i=0; i<wn_content.size(); ++i){
			List<WordNode> wn = wn_content.get(i);
			content_nodes.addAll(wn);
			for(int j=0; j<wn.size(); ++j){
				v_content += wn.get(j).getValue();//将内容中极性词极性累加
			}
		}
		// 归一化content的值
		v_content = guiyi(v_content);
		
		double v_all=0;
		if(v_title != 0)
		{
			if((v_title > 0)&&(v_content < 0)||(v_title < 0)&&(v_content > 0))
			{
				v_all=v_title+0.15*v_content;
			}
			else
			{
			v_all = 0.5*v_title + 0.5*v_content;
			}
		}else
		{
			v_all = 0.2*v_content;
		}
		BigDecimal bg = new BigDecimal(v_all);
        double result = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
	}
		private static double guiyi(double param){
			double middle = Math.abs(param);
			if(middle<1){
				middle = 0.4*middle;
			}else if(middle>=1 && middle<3){
				middle = 0.1*middle+0.3;
			}else if(middle>=3 && middle<6){
				middle = 0.2*middle/3+0.4;
			}else if(middle>=6 && middle<10){
				middle = 0.1*middle/4 +0.65;
			}else{
				middle = 1-1/middle;
			}
			
			double result = 0;
			if(param<0){
				result = -middle;
			}else{
				result = middle;
			}
			return result;
		}
	
	public static void main(String[] args) {
//		demo.testDatabase();
		String sss = "【环球网综合报道】美国前国防部长哈格尔11日称，希望“大选”后台湾的新“政府”不要放掉一些能达成正确目标、适当协议的良好进展，这对两岸和整个区域都好，也不会让美国陷入是否支持“对台义务”的困境。 　　对此，香港“中评社”13日发表评论文.";
		System.out.println(PolarityDemo.calXinwen("港媒：忧心蔡英文 美国提醒台湾不能再做“麻烦制造者”", sss));
	}
	
}
