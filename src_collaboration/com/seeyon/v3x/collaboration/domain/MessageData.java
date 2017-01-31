package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.edoc.domain.EdocSummary;

/**
 * User: jincm Date: 2008-3-10 Time: 13:57:19
 */
public class MessageData implements Serializable{
	private static final long serialVersionUID = 5237586756723851140L;

	//操作类型
	public String operationType;

	//消息接收者
	public List<String> partyNames;

	//消息发送者
	public Long handlerId;
	
	//消息对应的协同
	public ColSummary summary;
	
	//消息对应的事项
	public Affair affair;
	
	//消息对应的公文
	public EdocSummary edocSummary;
	
	//应用类型
	public ApplicationCategoryEnum appEnum;
	
	//消息接收者
	public MessageReceiver receiver;
	
	//标题
	public String subject;
	
	public int importLevel;

	public List<String[]> processLogParam = new ArrayList<String[]>();
	
	public MessageData() {
	}

	public MessageData(String operationType, Long handlerId, List<String> partyNames, ColSummary summary, EdocSummary edocSummary,
			Affair affair, ApplicationCategoryEnum appEnum, MessageReceiver receiver, String subject, int importLevel) {
		this.operationType = operationType;
		this.handlerId = handlerId;
		this.partyNames = partyNames;
		this.summary = summary;
		this.edocSummary = edocSummary;
		this.appEnum = appEnum;
		this.receiver = receiver;
		this.subject = subject;
		this.importLevel = importLevel;
	}

	public Affair getAffair() {
		return affair;
	}

	public void setAffair(Affair affair) {
		this.affair = affair;
	}

	public Long getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(Long handlerId) {
		this.handlerId = handlerId;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public List<String> getPartyNames() {
		return partyNames;
	}

	public void setPartyNames(List<String> partyNames) {
		this.partyNames = partyNames;
	}

	public ColSummary getSummary() {
		return summary;
	}

	public void setSummary(ColSummary summary) {
		this.summary = summary;
	}

	public EdocSummary getEdocSummary() {
		return edocSummary;
	}

	public void setEdocSummary(EdocSummary edocSummary) {
		this.edocSummary = edocSummary;
	}

	public ApplicationCategoryEnum getAppEnum() {
		return appEnum;
	}

	public void setAppEnum(ApplicationCategoryEnum appEnum) {
		this.appEnum = appEnum;
	}

	public MessageReceiver getReceiver() {
		return receiver;
	}

	public void setReceiver(MessageReceiver receiver) {
		this.receiver = receiver;
	}

	public int getImportLevel() {
		return importLevel;
	}

	public void setImportLevel(int importLevel) {
		this.importLevel = importLevel;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String[]> getProcessLogParam() {
		return processLogParam;
	}

	public void setProcessLogParam(List<String[]> processLogParam) {
		this.processLogParam = processLogParam;
	}
	
	public void addProcessLogParam(String... param){
		if(this.processLogParam == null){
			this.processLogParam = new ArrayList<String[]>();
		}
		processLogParam.add(param);
	}
}
