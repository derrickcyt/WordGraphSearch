package ltpTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ltp {
	public static class LTPDATA{
		String id;
		String content;
		String pos;
		String parent;
		String relate;
		public LTPDATA(String lid,String lcontent,String lpos,String lparent,String lrelate)
		{
			id=lid;
			content = lcontent;
			pos = lpos;
			parent = lparent;
			relate = lrelate;
		}
		
		public void printLtp()
		{
			System.out.println("id="+id+"  ;content="+content+"  ;pos="+pos+"  ;parent="+parent+"  ;relate="+relate);
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getPos() {
			return pos;
		}
		public void setPos(String pos) {
			this.pos = pos;
		}
		public String getParent() {
			return parent;
		}
		public void setParent(String parent) {
			this.parent = parent;
		}
		public String getRelate() {
			return relate;
		}
		public void setRelate(String relate) {
			this.relate = relate;
		}
	}
	
    public static Map<String,LTPDATA> doltp(String title) throws IOException {
    	System.out.println("doltp:"+title);
        String api_key = "m3P3G6G8fqrETodlsqiQMeaOwqYQSjgXWgQI7wng";
        String pattern = "all";
        String format  = "xml";
        String text = URLEncoder.encode(title, "utf-8");

        URL url     = new URL("http://ltpapi.voicecloud.cn/analysis/?"
                              + "api_key=" + api_key + "&"
                              + "text="    + text    + "&"
                              + "format="  + format  + "&"
                              + "pattern=" + pattern);
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader innet = new BufferedReader(new InputStreamReader(
                                conn.getInputStream(),
                                "utf-8"));
        String line;
		Map<String,LTPDATA> map = new HashMap<String,LTPDATA>();
        while ((line = innet.readLine())!= null) {
//        	System.out.println(line);
            if(line.contains("<word id"))
            {
            	line = line.trim();
            	if(line.endsWith(" />"))line = line.substring(0, line.length()-3);
            	else if(line.endsWith(">"))line = line.substring(0,line.length()-1);
            	String[] temparray = line.split("\\s+");
            	String str1 = temparray[1].split("=")[1];
            	if(str1.length()>1)
            	str1 = str1.substring(1, str1.length()-1);
            	String str2 = temparray[2].split("=")[1];
            	if(str2.length()>1)
            	str2 = str2.substring(1, str2.length()-1);
            	String str3 = temparray[3].split("=")[1];
            	if(str3.length()>1)
            	str3= str3.substring(1, str3.length()-1);
            	String str5 = temparray[5].split("=")[1];
            	if(str5.length()>1)
            	str5 = str5.substring(1, str5.length()-1);
            	String str6 = temparray[6].split("=")[1];
            	if(str6.length()>1)
            	str6 = str6.substring(1, str6.length()-1);
            	LTPDATA templtp = new LTPDATA(str1,str2,str3,str5,str6);
            	map.put(str1, templtp);
            }
        }
        innet.close();
        return map;
    }
    
    public static String ltpAnalysis(Map<String,LTPDATA>map)
    {
    	for(String ss:map.keySet())
    	{
    		if(map.get(ss).content.equals("就")&&map.get(ss).relate.equals("ADV"))return "false";
    	}
    	String rootid="";
    	String result="";
    	boolean findroot=false;
    	boolean lastsuccess = false;
    	boolean firstsuccess = false;
    	int i = 0;
    	int end = map.size();
    	while(i<end)
    	{
    		String si = String.valueOf(i);
    		if(!findroot)
    		{
	    		if(map.get(si).parent.equals("-1"))
	    		{
	    			rootid = si;
	    			result = map.get(si).content;
	    			findroot=true;
	    			continue;
	    		}
    		}
    		else
    		{
    			String tempresult = findltpAfter(result,rootid,rootid,map);
    			//System.out.println("here"+tempresult);
    			if(!tempresult.equals(result))
        		{
        			result = tempresult;
        			lastsuccess=true;
        			break;
        		}
//    			if(map.get(si).relate.equals("VOB")&&map.get(si).parent.equals(rootid))
//    			{
//    				result = result+map.get(si).content;
//    				//System.out.println(result);
//    				lastsuccess = true;
//    				break;
//    			}
    		}
    		i++;
    	}
    	
    	if(lastsuccess)
		{
    		String tempresult = findltpbegin(result,rootid,rootid,map);
    		//System.out.println("here"+tempresult);
    		if(!tempresult.equals(result))
    		{
    			result = tempresult;
    			firstsuccess=true;
    		}
		}
		if(lastsuccess&&firstsuccess)
		{
			if(result.length()<4)
				return "false";
			else
			{
				for(int l=0;l<map.size();l++)
				{
					map.get(String.valueOf(l)).printLtp();
				}
				return result;
			}
		}
		else
		{
			return "false";
		}
    }
    
    public static String findltpbegin(String str,String end, String parent,Map<String,LTPDATA>map)
    {
    	int endint = Integer.valueOf(end);
    	int i=endint-1;
    	while(i>-1)
    	{
    		String si =String.valueOf(i) ;
    		//if(map.get(si).parent.equals(parent)&&(map.get(si).relate.equals("SBV")||map.get(si).relate.equals("ADV")))
    		if(map.get(si).parent.equals(parent)&&(map.get(si).relate.equals("SBV")))
    		{
    			//if(map.get(si).content.equals("灏�))return str;
    			str = getAtt(map,si)+map.get(si).content+str;
    			str = findltpbegin(str,si,si,map);
    			//return str;
    		}
    		i--;
    	}
    	return str;
    }
    
    public static String findltpAfter(String str,String begin, String parent,Map<String,LTPDATA>map)
    {
    	int endint = Integer.valueOf(begin);
    	int i=endint+1;
    	while(map.containsKey(String.valueOf(i)))
    	{
    		String si =String.valueOf(i) ;
    		//System.out.println("here"+map.get(si).content);
    		if(map.get(si).parent.equals(parent)&&(map.get(si).relate.equals("VOB")||map.get(si).relate.equals("COO")))
    		{
    			str = str+getAtt(map,si)+map.get(si).content;
    			str = findltpAfter(str,si,si,map);
//    			if(str.equals(temp)&&map.get(si).content.length()==1)
//    			{
//    				str=str+"###";
//    			}
//    			else
//    			{
//    				str = temp;
//    			}
    			//return str;
    		}
    		i++;
    	}
    	return str;
    }
    
    public static String getAtt(Map<String,LTPDATA>map,String id)
    {
    	String result="";
    	int iid = Integer.valueOf(id);
    	for(int i=0;i<iid;i++)
    	{
    		if(map.get(String.valueOf(i)).parent.equals(id)&&(map.get(String.valueOf(i)).relate.equals("ATT")||map.get(String.valueOf(i)).relate.equals("POB")))
    		{
    			if(map.get(String.valueOf(i)).pos.equals("q"))
    			{
    				if((i-1)>-1)
    				{
    					if(map.get(String.valueOf(i-1)).pos.equals("m")&&map.get(String.valueOf(i-1)).relate.equals("ATT"))
    						result = result+map.get(String.valueOf(i-1)).content+map.get(String.valueOf(i)).content;
    				}
    			}
    			else
    			result = result+map.get(String.valueOf(i)).content;
    		}
    	}
    	return result;
    }
    
//    public static String special beforeAtt(String words)
//    {
//    	
//    }
    
    public static void main(String[]args) throws Exception
    {
    	String bb = "";
    	
    	Map<String,LTPDATA>map2 = doltp(bb);
    	System.out.println(ltpAnalysis(map2));
    }
}
