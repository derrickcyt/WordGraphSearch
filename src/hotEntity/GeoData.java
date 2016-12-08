package hotEntity;

public class GeoData {
	String name;
	String code;
	int level;
	String parent;
	String parentName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public GeoData(String name, String code, int level, String parent,String parentName) {
		this.name = name;
		this.code = code;
		this.level = level;
		this.parent = parent;
		this.parentName = parentName;
	}
	
//	@Override
//	public int hashCode(){
//		return this.name.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object o){
//		if(o instanceof GeoData){
//			GeoData pk = (GeoData)o;
//			if(this.code.equals(pk.getCode())&&this.level==pk.getLevel()){
//				return true;
//			}
//		}
//		return false;
//	}
}
