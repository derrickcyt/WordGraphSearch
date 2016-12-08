package ltpTools;

import java.io.Serializable;

public class LTPDATA implements Serializable {
	String id;
	String content;
	String pos;
	String parent;
	String relate;

	public LTPDATA(String lid, String lcontent, String lpos, String lparent,
			String lrelate) {
		id = lid;
		content = lcontent;
		pos = lpos;
		parent = lparent;
		relate = lrelate;
	}

	public void printLtp() {
		System.out.println("id=" + id + "  ;content=" + content + "  ;pos="
				+ pos + "  ;parent=" + parent + "  ;relate=" + relate);
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
