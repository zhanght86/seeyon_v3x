package com.seeyon.v3x.exchange.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.exchange.util.Constants;

public class EdocSendDetail extends BaseModel implements Serializable, Comparable<EdocSendDetail>{
	private static final long serialVersionUID = 1L;
	
	private Long sendRecordId;
	private String recOrgId; // 外部单位ID\内部单位ID|内部部门ID
	private int recOrgType; // 外部单位\单位|部门("department","org","external")
	private String recOrgName; //外部单位\单位\部门名称("用友财务,金融,工程")
	private int sendType; // "send","copy","report"	
	private String content; // 签收回执内容
	private String recNo; // 签收编号	
	private String recUserName; // 签收人	
	private Timestamp recTime; // 签收时间
	private int status;	// 状态：待签收，已签收（已回执）
	
	public Long getSendRecordId() {
		return sendRecordId;
	}
	
	public void setSendRecordId(Long sendRecordId) {
		this.sendRecordId = sendRecordId;
	}
	
	public String getRecOrgId() {
		return recOrgId;
	}
	
	public void setRecOrgId(String recOrgId) {
		this.recOrgId = recOrgId;
	}
	
	public int getRecOrgType() {
		return recOrgType;
	}
	
	public void setRecOrgType(int recOrgType) {
		this.recOrgType = recOrgType;
	}
	
	public int getSendType() {
		return sendType;
	}
	
	public void setSendType(int sendType) {
		this.sendType = sendType;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}	
	
	public String getRecNo() {
		return recNo;
	}
	
	public void setRecNo(String recNo) {
		this.recNo = recNo;
	}
		
	public String getRecUserName() {
		return recUserName;
	}
	
	public void setRecUserName(String recUserName) {
		this.recUserName = recUserName;
	}
	
	public Timestamp getRecTime() {
		return recTime;
	}
	
	public void setRecTime(Timestamp recTime) {
		this.recTime = recTime;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public String getRecOrgName() {
		return recOrgName;
	}

	public void setRecOrgName(String recOrgName) {
		this.recOrgName = recOrgName;
	}

	public int compareTo(EdocSendDetail o) {
		if(this.status == Constants.C_iStatus_Recieved){
			if(o.status == Constants.C_iStatus_Recieved){
				return this.recTime.compareTo(o.recTime);
			}else{
				return -1;
			}
				
		}else{
			if(o.status == Constants.C_iStatus_Recieved){
				return 1;
			}else{
				if(this.id <= o.id)
					return -1;
				else
					return 1;
			}
		}
	}	
	
}
