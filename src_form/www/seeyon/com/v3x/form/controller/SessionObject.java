package www.seeyon.com.v3x.form.controller;

import java.util.List;

import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FormView;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;

public class SessionObject {
	//baseinfo
	private DataDefine data;
	//baseinfo xsn附件存放路径
	private String xsnpath;
	//baseinfo  列表中所有字段的总数
	private int tablefieldsize;
	
	//baseinfo  列表中所有字段
	private List TableFieldList;
	//对象
	private SeeyonDataDefine seedatadefine;
	//例：my.
	private String namespace;
	//xsf对象
	private InfoPathObject xsf;
	//input list
	private List FieldInputList;
	//表单名称
	private String FormName;
	
	private String editflag;
	//存放allxml中的FormList
	private List formLst;
	
	
	
	
	public List getFormLst() {
		return formLst;
	}
	public void setFormLst(List formLst) {
		this.formLst = formLst;
	}
	public String getEditflag() {
		return editflag;
	}
	public void setEditflag(String editflag) {
		this.editflag = editflag;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public List getTableFieldList() {
		return TableFieldList;
	}
	public void setTableFieldList(List tableFieldList) {
		TableFieldList = tableFieldList;
	}
	public String getFormName() {
		return FormName;
	}
	public void setFormName(String formName) {
		FormName = formName;
	}
	public List getFieldInputList() {
		return FieldInputList;
	}
	public void setFieldInputList(List fieldInputList) {
		FieldInputList = fieldInputList;
	}
	public DataDefine getData() {
		return data;
	}
	public void setData(DataDefine data) {
		this.data = data;
	}
	public SeeyonDataDefine getSeedatadefine() {
		return seedatadefine;
	}
	public void setSeedatadefine(SeeyonDataDefine seedatadefine) {
		this.seedatadefine = seedatadefine;
	}
	public int getTablefieldsize() {
		return tablefieldsize;
	}
	public void setTablefieldsize(int tablefieldsize) {
		this.tablefieldsize = tablefieldsize;
	}
	public InfoPathObject getXsf() {
		return xsf;
	}
	public void setXsf(InfoPathObject xsf) {
		this.xsf = xsf;
	}
	public String getXsnpath() {
		return xsnpath;
	}
	public void setXsnpath(String xsnpath) {
		this.xsnpath = xsnpath;
	}
	
	
	
}
