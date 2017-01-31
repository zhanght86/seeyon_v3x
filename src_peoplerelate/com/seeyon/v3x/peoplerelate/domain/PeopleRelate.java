package com.seeyon.v3x.peoplerelate.domain;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_Email;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_Mobile;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_id;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

public class PeopleRelate extends com.seeyon.v3x.common.domain.BaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String ENTITY_TYPE_RelatePeople = "RelatePeople";
	
    private Long relateMemberId;		//关联人员id 被设置的一方，被动的
    private Integer relateWsbs;			//握手标识
    private Long relatedMemberId;		//被关联人员id  实际主动的一方
    private Integer relateType;			//关联类型
    private String relateMemberName;
    private String relateMemberDept;
    private String relateMemberPost;
    private String relateMemberTel;
    private String relateMemberHandSet;
    private String relateMemberEmail;
    private String relateMemberAccount;
    private Integer orderNum ;
    private String typeProperty = V3xOrgMember.ORGENT_TYPE_MEMBER;	//parseElements（）这个方法中用到
//  握手标识  1已确认  2未确认  0未建立
	public static int wsbs_sure = 1;
	public static int wsbs_unsure = 2;
	public static int wsbs_no = 0;
	private Long relateImageId;
	private Date relateImageDate;
	private String selImgUrl;
    public String getSelImgUrl() {
		return selImgUrl;
	}
	public void setSelImgUrl(String selImgUrl) {
		this.selImgUrl = selImgUrl;
	}
	public Long getRelatedMemberId() {
		return relatedMemberId;
	}
	public void setRelatedMemberId(Long relatedMemberId) {
		this.relatedMemberId = relatedMemberId;
	}
	public Long getRelateMemberId() {
		return relateMemberId;
	}
	public void setRelateMemberId(Long relateMemberId) {
		this.relateMemberId = relateMemberId;
	}
	public Integer getRelateType() {
		return relateType;
	}
	public void setRelateType(Integer relateType) {
		this.relateType = relateType;
	}
	public Integer getRelateWsbs() {
		return relateWsbs;
	}
	public void setRelateWsbs(Integer relateWsbs) {
		this.relateWsbs = relateWsbs;
	}
	public String getTypeProperty() {
		return typeProperty;
	}
	public String getRelateMemberName() {
		return relateMemberName;
	}
	public void setRelateMemberName(String relateMemberName) {
		this.relateMemberName = relateMemberName;
	}
	public String getRelateMemberDept() {
		return relateMemberDept;
	}
	public void setRelateMemberDept(String relateMemberDept) {
		this.relateMemberDept = relateMemberDept;
	}
	public String getRelateMemberEmail() {
		return relateMemberEmail;
	}
	public void setRelateMemberEmail(String relateMemberEmail) {
		this.relateMemberEmail = relateMemberEmail;
	}
	public String getRelateMemberHandSet() {
		return relateMemberHandSet;
	}
	public void setRelateMemberHandSet(String relateMemberHandSet) {
		this.relateMemberHandSet = relateMemberHandSet;
	}
	public String getRelateMemberPost() {
		return relateMemberPost;
	}
	public void setRelateMemberPost(String relateMemberPost) {
		this.relateMemberPost = relateMemberPost;
	}
	public String getRelateMemberTel() {
		return relateMemberTel;
	}
	public void setRelateMemberTel(String relateMemberTel) {
		this.relateMemberTel = relateMemberTel;
	}
	public String getRelateMemberAccount() {
		return relateMemberAccount;
	}
	public void setRelateMemberAccount(String relateMemberAccount) {
		this.relateMemberAccount = relateMemberAccount;
	}
	
	
	/**
	 * 给选人界面用的，不要轻易修改
	 */
	public void toJsonString(StringBuffer o, long loginAccountId, OrgManager orgManager) {
		o.append("{");
		o.append(TOXML_PROPERTY_id).append(":\"").append(this.getRelateMemberId()).append("\"");
		o.append(",T:").append(this.getRelateType());
		
		try{
			V3xOrgMember member = orgManager.getMemberById(this.getRelateMemberId());
			if(member != null && loginAccountId != member.getOrgAccountId().longValue()){ //不是一个单位的

				o.append(",E:{");
				
				o.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");

				o.append(",A:\"").append(member.getOrgAccountId()).append("\"");
				
				if(Strings.isNotBlank(member.getEmailAddress())){
					o.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(member.getEmailAddress())).append("\"");
				}
				if(Strings.isNotBlank(member.getTelNumber())){
					o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(member.getTelNumber())).append("\"");
				}
				
				o.append("}");
			}
		}
		catch (Exception e) {
		}
		
		o.append("}");
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public Date getRelateImageDate() {
		return relateImageDate;
	}
	public void setRelateImageDate(Date relateImageDate) {
		this.relateImageDate = relateImageDate;
	}
	public Long getRelateImageId() {
		return relateImageId;
	}
	public void setRelateImageId(Long relateImageId) {
		this.relateImageId = relateImageId;
	}
}
