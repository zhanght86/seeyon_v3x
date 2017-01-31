package com.seeyon.v3x.edoc.domain;

import java.sql.Timestamp;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.util.Constants;
/**
 * 公文签收收据实体
 * @author muj
 */
public class EdocSignReceipt {
	/**
	 * 签收人姓名
	 */
	private String receipient;
	/**
	 * 签收单位名称，如果是部门签收，则为部门名称
	 */
	private String signUnit;
	/**
	 * 签收意见
	 */
	private String opinion;
	/**
	 * 签收时间
	 */
	private long signTime ;
	
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public String getReceipient() {
		return receipient;
	}
	public void setReceipient(String receipient) {
		this.receipient = receipient;
	}
	public long getSignTime() {
		return signTime;
	}
	public void setSignTime(long signTime) {
		this.signTime = signTime;
	}
	public String getSignUnit() {
		return signUnit;
	}
	public void setSignUnit(String signUnit) {
		this.signUnit = signUnit;
	}
}
