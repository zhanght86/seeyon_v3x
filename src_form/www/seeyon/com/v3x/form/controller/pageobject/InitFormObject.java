package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;

public class InitFormObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5382802677590492717L;
	public String formname ;
	public String sheetname ;
	public String viewfile;
	public String formid;
	public String viewHtml = "html";
	

	public String getViewHtml() {
		return viewHtml;
	}
	public String getFormid() {
		return formid;
	}
	public void setFormid(String formid) {
		this.formid = formid;
	}
	public String getFormname() {
		return formname;
	}
	public void setFormname(String formname) {
		this.formname = formname;
	}
	public String getSheetname() {
		return sheetname;
	}
	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}
	public String getViewfile() {
		return viewfile;
	}
	public void setViewfile(String viewfile) {
		this.viewfile = viewfile;
	}

}
