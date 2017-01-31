package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import www.seeyon.com.v3x.form.domain.FomObjaccess;

public class FormOperAuthObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4642192120861232969L;

	/**
	 * 业务表单应用授权名称
	 */
	private String name;
	
	/**
	 * 业务表单应用授权增加权限视图明细
	 */
	private Map<String,String> addShowDetail = new HashMap<String,String>();
	
	/**
	 * 业务表单应用授权修改权限视图明细
	 */
	private Map<String,String> updateShowDetail = new HashMap<String,String>();
	
	/**
	 * 业务表单应用授权浏览权限视图明细
	 */
	private List<Map<String,String>> browseShowDetail = new ArrayList<Map<String,String>>();

	/**
	 * 业务表单应用授权是否允许删除（他人创建）记录
	 */
	private boolean allowdelete;
	
	/**
	 * 业务表单应用授权是否允许进行锁定与解锁操作
	 */
	private boolean allowlock;
	
	/**
	 * 业务表单应用授权是否允许导出操作
	 */
	private boolean allowexport;
	
	/**
	 * 业务表单应用授权是否允许查询操作
	 */
	private boolean allowquery;
	
	/**
	 * 业务表单应用授权是否允许统计操作
	 */
	private boolean allowstat;
	/**
	 * 业务表单应用授权是否允许查看日志
	 */
	private boolean allowlog;
	/**
	 * 业务表单应用授权是否允许打印
	 */
	private boolean allowprint;



	/**
	 * 系统查询条件
	 */
	private String queryArea;
	private String queryAreaValue;
	
	private String xmlString;
	
	/**
	 * 授权
	 */
	private List<FomObjaccess> objAccessList = new ArrayList<FomObjaccess>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAddShowDetail() {
		return addShowDetail;
	}

	public void setAddShowDetail(Map<String, String> addShowDetail) {
		this.addShowDetail = addShowDetail;
	}
	
	public Map<String, String> getUpdateShowDetail() {
		return updateShowDetail;
	}

	public void setUpdateShowDetail(Map<String, String> updateShowDetail) {
		this.updateShowDetail = updateShowDetail;
	}

	public List<Map<String, String>> getBrowseShowDetail() {
		return browseShowDetail;
	}

	public void setBrowseShowDetail(List<Map<String, String>> browseShowDetail) {
		this.browseShowDetail = browseShowDetail;
	}

	public boolean isAllowdelete() {
		return allowdelete;
	}

	public void setAllowdelete(boolean allowdelete) {
		this.allowdelete = allowdelete;
	}

	public boolean isAllowlock() {
		return allowlock;
	}

	public void setAllowlock(boolean allowlock) {
		this.allowlock = allowlock;
	}

	public boolean isAllowexport() {
		return allowexport;
	}

	public void setAllowexport(boolean allowexport) {
		this.allowexport = allowexport;
	}

	public String getQueryArea() {
		return queryArea;
	}
	
	public boolean isAllowquery() {
		return allowquery;
	}

	public void setAllowquery(boolean allowquery) {
		this.allowquery = allowquery;
	}

	public boolean isAllowstat() {
		return allowstat;
	}

	public void setAllowstat(boolean allowstat) {
		this.allowstat = allowstat;
	}

	public void setQueryArea(String queryArea) {
		this.queryArea = queryArea;
	}

	public String getQueryAreaValue() {
		return queryAreaValue;
	}

	public void setQueryAreaValue(String queryAreaValue) {
		this.queryAreaValue = queryAreaValue;
	}

	public List<FomObjaccess> getObjAccessList() {
		return objAccessList;
	}

	public void setObjAccessList(List<FomObjaccess> objAccessList) {
		this.objAccessList = objAccessList;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}	
	public boolean isAllowlog() {
		return allowlog;
	}

	public void setAllowlog(boolean allowlog) {
		this.allowlog = allowlog;
	}
	public boolean isAllowprint() {
		return allowprint;
	}

	public void setAllowprint(boolean allowprint) {
		this.allowprint = allowprint;
	}
}