package hotEntity;
import java.util.HashSet;
import java.util.Set;


public class EntityData {
	public String name;
	public int posNum;
	public int frequency;
	public Set<String> ids;
	public Set<String> titles;
	
	public EntityData(String NAME,int POSNUM,int FRE, String IDS,String TITLE){
		this.name=NAME;
		this.posNum =POSNUM;
		this.frequency=FRE;
		Set<String> newids = new HashSet<String>();
		newids.add(IDS);
		this.ids=newids;
		Set<String> newtitless = new HashSet<String>();
		newtitless.add(TITLE);
		this.titles=newtitless;
	}
	
	public int getPosNum() {
		return posNum;
	}

	public void setPosNum(int posNum) {
		this.posNum = posNum;
	}

//	public void addFrequency(){
//		this.frequency++;
//	}
	
	public void addIds(String id){
		this.ids.add(id);
	}
	
	public void addTitles(String title){
		this.titles.add(title);
	}
	
	public void updateFrequency(){
		this.frequency=this.ids.size();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public Set<String> getIds() {
		return ids;
	}
	public void setIds(Set<String> ids) {
		this.ids = ids;
	}
	
	public Set<String> getTitles() {
		return titles;
	}

	public void setTitles(Set<String> titles) {
		this.titles = titles;
	}

	public String toString(){
		String result = "name:"+this.name+"\n"+"fre:"+this.getFrequency()+"\nids:";
		for(String ss:this.getIds()){
			result+=ss+";";
		}
		return result;
	}
}
