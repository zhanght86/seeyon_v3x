package www.seeyon.com.v3x.form.controller;

import java.util.List;

public class Operation {
	//填写
	private String name;
	//Operation_001.xml
	private String filename;
	//add
	private String type;
	//oper
	private List operlst;
	
	public List getOperlst() {
		return operlst;
	}
	public void setOperlst(List operlst) {
		this.operlst = operlst;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
