package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeTask;

public class Operation implements Serializable {
	//填写
	private String name;
	//在operhelper.addDefaultOperLst()  和 operhelper.saveOperLst()方法中注入值
	private String operationId;
	//Operation_001.xml
	private String filename;
	//add
	private String type;
	//oper 放置map
	private List operlst;
	//
	private String submitlststr;
	//
	private String viewbindstr;
	//修改标志，如果修改过"true"
	private boolean editflag = false;
	
	private String operLstToFixString;
	
	private String newsubmitxml;
	private String newrepeatxml;
	private String newinitxml;
	private String newhighinitxml;
	private String newhighevenxml;
	
	/**
	 * 事件绑定列表
	 */
	private List<Operation_BindEvent> bindEventList = new ArrayList<Operation_BindEvent>(); 
	
	private List<InfoPath_DeeTask> deeTakEventList = new ArrayList<InfoPath_DeeTask>(); 
	
	public List<InfoPath_DeeTask> getDeeTakEventList() {
		return deeTakEventList;
	}
	public void setDeeTakEventList(List<InfoPath_DeeTask> deeTakEventList) {
		this.deeTakEventList = deeTakEventList;
	}
	/**
	 * 把operlst 返回为字符串，供前台调用
	 * 格式：名称-值:名称-值:名称-值;名称-值:名称-值:名称-值
	 * name-订货单位:-edit:formprint-Y:formtransmit-Y;name-订货人:formoper-edit:formprint-Y:formtransmit-Y
	 * 
	 * @return
	 */
	public String getOperLstToFixString() {
		StringBuffer sb = new StringBuffer();
		if(operlst != null){
			for(int i=0;i<operlst.size();i++){
				Map map = (Map)operlst.get(i);
				String name = (String)map.get("bindname"+i);
				sb.append("name↗"+ OperHelper.noNamespace(name)+"↖");
				sb.append("formoper↗"+ map.get("formoper"+i)+"↖");
				sb.append("formprint↗"+ map.get("formprint"+i)+"↖");
				sb.append("formtransmit↗"+ map.get("formtransmit"+i)+"↖");
				sb.append("initvalue↗"+ map.get("initvalue"+i)+"↖");
				sb.append("displayvalue↗"+ map.get("displayvalue"+i)+"↖");
				sb.append("initdisplay↗"+ map.get("initdisplay"+i));
				if(i != operlst.size()-1){
					sb.append(";");
				}
			}
		}
		return sb.toString();
	}
	public void setOperLstToFixString(String operLstToFixString) {
		this.operLstToFixString = operLstToFixString;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	public boolean isEditflag() {
		return editflag;
	}
	public void setEditflag(boolean editflag) {
		this.editflag = editflag;
	}
	public String getSubmitlststr() {
		return submitlststr;
	}
	public void setSubmitlststr(String submitlststr) {
		this.submitlststr = submitlststr;
	}
	public String getViewbindstr() {
		return viewbindstr;
	}
	public void setViewbindstr(String viewbindstr) {
		this.viewbindstr = viewbindstr;
	}
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
	public String getNewhighevenxml() {
		return newhighevenxml;
	}
	public void setNewhighevenxml(String newhighevenxml) {
		this.newhighevenxml = newhighevenxml;
	}
	public String getNewhighinitxml() {
		return newhighinitxml;
	}
	public void setNewhighinitxml(String newhighinitxml) {
		this.newhighinitxml = newhighinitxml;
	}
	public String getNewinitxml() {
		return newinitxml;
	}
	public void setNewinitxml(String newinitxml) {
		this.newinitxml = newinitxml;
	}
	public String getNewrepeatxml() {
		return newrepeatxml;
	}
	public void setNewrepeatxml(String newrepeatxml) {
		this.newrepeatxml = newrepeatxml;
	}
	public String getNewsubmitxml() {
		return newsubmitxml;
	}
	public void setNewsubmitxml(String newsubmitxml) {
		this.newsubmitxml = newsubmitxml;
	}
	public List<Operation_BindEvent> getBindEventList() {
		return bindEventList;
	}
	public void setBindEventList(List<Operation_BindEvent> bindEventList) {
		this.bindEventList = bindEventList;
	}
	
}
