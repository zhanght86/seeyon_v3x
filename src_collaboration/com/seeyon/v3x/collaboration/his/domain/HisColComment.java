/**
 * 
 */
package com.seeyon.v3x.collaboration.his.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.utils.BeanUtils;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-1-7
 */
public class HisColComment extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3966240866502550008L;

	private String content;

	private java.sql.Timestamp createDate;

	private Boolean isHidden = false;
    
    private Boolean isHidden4Sender = false; //是否对发起者隐藏
	/**
	 * 该属性是ORM的字段
	 */
	private Long writeMemberId;

	private Long summaryId;

	private Long opinionId;
	
	//不写入数据库
	private Long memberId;
	
	private Long startMemberId;
	
	private String memberName;
	
	private String proxyName;

	private String showToId;
	
	public String getShowToId() {
		return showToId;
	}

	public void setShowToId(String showToId) {
		this.showToId = showToId;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getStartMemberId() {
		return startMemberId;
	}

	public void setStartMemberId(Long startMemberId) {
		this.startMemberId = startMemberId;
	}

	public HisColComment() {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public java.sql.Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean hidden) {
		isHidden = hidden;
	}
    
	public Long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public Long getOpinionId() {
		return opinionId;
	}

	public void setOpinionId(Long opinionId) {
		this.opinionId = opinionId;
	}

	public Long getWriteMemberId() {
		return writeMemberId;
	}

	public void setWriteMemberId(Long writeMemberId) {
		this.writeMemberId = writeMemberId;
	}

    public Boolean getIsHidden4Sender() {
        return isHidden4Sender;
    }

    public void setIsHidden4Sender(Boolean isHidden4Sender) {
        this.isHidden4Sender = isHidden4Sender;
    }
	
	public void clone(com.seeyon.v3x.collaboration.domain.ColComment c) {
		BeanUtils.convert(this, c);
	}
	
	public com.seeyon.v3x.collaboration.domain.ColComment toColComment(){
		com.seeyon.v3x.collaboration.domain.ColComment c = new com.seeyon.v3x.collaboration.domain.ColComment();
		BeanUtils.convert(c, this);
		return c;
	}
}