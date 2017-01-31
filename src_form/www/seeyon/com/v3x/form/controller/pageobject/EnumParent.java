package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;

//import www.seeyon.com.v3x.form.domain.FormEnumlist;

public class EnumParent implements Serializable {
	private String parentName;

	private Long parentid;

	/*private FormEnumlist fenum;

	public FormEnumlist getFenum() {
		return fenum;
	}

	public void setFenum(FormEnumlist fenum) {
		this.fenum = fenum;
	}
*/
	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	
}
