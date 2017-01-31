package com.seeyon.v3x.doc.webmodel;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.doc.domain.DocAlert;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 订阅管理vo
 */
public class DocAlertAdminVO implements Comparable<DocAlertAdminVO> {
	// 订阅记录对象
	private List<DocAlert> docAlerts;
	// 对应文档（夹）
	private DocResource docResource;
	// 文档（夹）图片
	private String icon;
	// 内容类型
	private String type;
	// 文档创建人
	private String docCreater;
	// 文档路径
	private String path;
	// 文档id
	private long docResId;
	// 是否有对应类型的订阅
	private boolean add = false;
	private boolean edit = false;
	private boolean delete = false;
	private boolean forum = false;	
	// 订阅记录创建人
	private String alertCreater;
	// 是否发送消息
	private boolean sendMessage = false;
	// 是否影响到子文档夹
	private boolean setSubFolder = false;
	// 订阅用户
	private String userType;
	private long userId;
	// 是否个人订阅
	private boolean personalAlert = false;
	// 订阅记录生成时间
	private Timestamp alertCreateTime;
	// 订阅类型（国际化）
	private String alertType;
	
	// DocAlertId串，逗号分割
	private String alertIds;
	
	public String getAlertIds() {
		return alertIds;
	}

	public void setAlertIds(String alertIds) {
		this.alertIds = alertIds;
	}

	public DocAlertAdminVO(List<DocAlert> alerts, DocResource dr) {
		docAlerts = alerts;
		docResource = dr;
		
		docResId = dr.getId();
		
		if(alerts != null && alerts.size() > 0){
			userId = alerts.get(0).getAlertUserId();
			userType = alerts.get(0).getAlertUserType();
			sendMessage = alerts.get(0).getSendMessage();
			setSubFolder = alerts.get(0).getSetSubFolder();
			personalAlert = (userType.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER));	
			alertCreateTime = alerts.get(0).getCreateTime();
		}
		
		alertType = "";
		alertIds = "";
		
		Set<Byte> alertTypes = new HashSet<Byte>();
		for(DocAlert d : alerts){
			alertIds += "," + d.getId();
			if((dr.getIsFolder() || d.getChangeType() != Constants.ALERT_OPR_TYPE_ADD)&&!alertTypes.contains(d.getChangeType()))
				alertTypes.add(d.getChangeType());
		}
		
		for(Byte d : alertTypes){
			if(d.byteValue() == Constants.ALERT_OPR_TYPE_ADD){
				add = true;				
				alertType += "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.add");
			}else if(d.byteValue() == Constants.ALERT_OPR_TYPE_DELETE){
				delete = true;
				alertType += "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.delete");
			}else if(d.byteValue() == Constants.ALERT_OPR_TYPE_EDIT){
				edit = true;
				alertType += "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.edit");
			}else if(d.byteValue() == Constants.ALERT_OPR_TYPE_FORUM){
				forum = true;
				alertType += "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.forum");	
			}
			else if(d.byteValue() == Constants.ALERT_OPR_TYPE_ALL){
				add = true;
				delete = true;
				edit = true;
				forum = true;
			
				String types = "";
				if(dr.getIsFolder()){
					types = "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.edit")
					 + "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.add")
					 + "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.delete")
					 + "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.forum");
				}else{
					types = "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.edit")
					 + "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.delete")
					 + "," + ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, "doc.jsp.alert.forum");
				}
				alertType += types;
			}
		}
		
		if(!alertType.equals(""))
			alertType = alertType.substring(1, alertType.length());
		
		if(!alertIds.equals(""))
			alertIds = alertIds.substring(1, alertIds.length());
	}
	
	public boolean getAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean getEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean getForum() {
		return forum;
	}

	public void setForum(boolean forum) {
		this.forum = forum;
	}
	public boolean getSetSubFolder(){
		return setSubFolder;
	}
	public void setSetSubFolder(boolean setSubFolder){
		this.setSubFolder = setSubFolder;
	}

	public boolean getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public DocResource getDocResource() {
		return docResource;
	}
	public void setDocResource(DocResource docResource) {
		this.docResource = docResource;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public boolean getPersonalAlert() {
		return personalAlert;
	}

	public void setPersonalAlert(boolean personalAlert) {
		this.personalAlert = personalAlert;
	}

	public String getAlertCreater() {
		return alertCreater;
	}

	public void setAlertCreater(String alertCreater) {
		this.alertCreater = alertCreater;
	}

	public List<DocAlert> getDocAlerts() {
		return docAlerts;
	}

	public void setDocAlerts(List<DocAlert> docAlerts) {
		this.docAlerts = docAlerts;
	}

	public String getDocCreater() {
		return docCreater;
	}

	public void setDocCreater(String docCreater) {
		this.docCreater = docCreater;
	}

	public Timestamp getAlertCreateTime() {
		return alertCreateTime;
	}

	public void setAlertCreateTime(Timestamp alertCreateTime) {
		this.alertCreateTime = alertCreateTime;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public long getDocResId() {
		return docResId;
	}

	public void setDocResId(long docResId) {
		this.docResId = docResId;
	}

	public int compareTo(DocAlertAdminVO o) {
		Timestamp t1 = this.getAlertCreateTime();
		Timestamp t2 = o.getAlertCreateTime();
		if(t1 != null && t2 != null) {
			return -t1.compareTo(t2);
		}
		else {
			return 0;
		}
	}
	

}
