package com.seeyon.v3x.exchange.manager;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.exchange.dao.EdocRecieveRecordDao;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.*;

public class RecieveEdocManagerImpl implements RecieveEdocManager {
	
	private EdocRecieveRecordDao edocRecieveRecordDao;	
	private UserMessageManager userMessageManager = null;
	private AffairManager affairManager;
	private OrgManager orgManager;
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
	
	public UserMessageManager getUserMessageManager()
	{
		return this.userMessageManager;
	}
	
	public EdocRecieveRecordDao getEdocRecievedRecordDao() {
		return edocRecieveRecordDao;
	}
	
	public void setEdocRecieveRecordDao(EdocRecieveRecordDao edocRecieveRecordDao) {
		this.edocRecieveRecordDao = edocRecieveRecordDao;
	}	

	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,EdocSummary edocSummary) throws Exception {
		
		User user = CurrentUser.get();
		String userName = "";
		if(null!=user){
			userName = user.getName();
		}
		String key = "exchange.sign";
		
		EdocRecieveRecord edocRecieveRecord = new EdocRecieveRecord();
		edocRecieveRecord.setIdIfNew();
		edocRecieveRecord.setSubject(edocSendRecord.getSubject());
		edocRecieveRecord.setDocType(edocSendRecord.getDocType());
		edocRecieveRecord.setDocMark(edocSendRecord.getDocMark());
		edocRecieveRecord.setSecretLevel(edocSendRecord.getSecretLevel());
		edocRecieveRecord.setUrgentLevel(edocSendRecord.getUrgentLevel());
		edocRecieveRecord.setSendUnit(edocSendRecord.getSendUnit());
		edocRecieveRecord.setIssuer(edocSendRecord.getIssuer());
		edocRecieveRecord.setIssueDate(edocSendRecord.getIssueDate());
		edocRecieveRecord.setSendTo(aRecUnit[0]);
		edocRecieveRecord.setCopyTo(aRecUnit[1]);
		edocRecieveRecord.setReportTo(aRecUnit[2]);
		edocRecieveRecord.setReplyId(String.valueOf(replyId));
		edocRecieveRecord.setEdocId(edocSendRecord.getEdocId());
		edocRecieveRecord.setExchangeOrgId(exchangeOrgId);
		edocRecieveRecord.setExchangeType(exchangeType);		
		edocRecieveRecord.setFromInternal(true);
		if(replyId instanceof String)
		{
			edocRecieveRecord.setFromInternal(false);
		}
		long l = System.currentTimeMillis();
		edocRecieveRecord.setCreateTime(new Timestamp(l));
		edocRecieveRecord.setStatus(Constants.C_iStatus_Torecieve);
		edocRecieveRecord.setContentNo(edocSendRecord.getContentNo());
		edocRecieveRecordDao.save(edocRecieveRecord);
		
        //MessageReceiver receiver = new MessageReceiver(edocSendRecord.getId(), edocSendRecord.getSendUserId());
        //userMessageManager.sendSystemMessage(new MessageContent(key,edocSendRecord.getSubject(),userName), ApplicationCategoryEnum.exSend, user.getId(), receiver);
		
		
		List<V3xOrgMember> member = EdocRoleHelper.getDepartMentExchangeUsers(user.getLoginAccount(), Long.valueOf(exchangeOrgId));
		Affair affair = null;
		for(V3xOrgMember m:member){
			affair = new Affair();
			affair.setIdIfNew();
			affair.setApp(ApplicationCategoryEnum.exSign.getKey());
			affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			affair.setSubject(edocSummary.getSubject());
			affair.setMemberId(m.getId());
			affair.setIsFinish(false);
			affair.setObjectId(edocSummary.getId());
			affair.setSubObjectId(edocRecieveRecord.getId());
			affair.setSenderId(user.getId());
			affair.setState(StateEnum.edoc_exchange_receive.key());
			
			affairManager.addAffair(affair);
			MessageReceiver receiver_a = new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.exchange.receive", affair.getSubObjectId().toString());
	        userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),userName), ApplicationCategoryEnum.exSign, user.getId(), receiver_a);	
			}
	}

	public void update(EdocRecieveRecord edocRecieveRecord) throws Exception {
		edocRecieveRecordDao.update(edocRecieveRecord);
	}
	
	public EdocRecieveRecord getEdocRecieveRecord(long id) {
		return edocRecieveRecordDao.get(id);
	}

	public List<EdocRecieveRecord> getEdocRecieveRecords(int status) {
		return edocRecieveRecordDao.getEdocRecieveRecords(status);
	}
	
	public List<EdocRecieveRecord> findEdocRecieveRecords(String accountIds,String departIds,Set<Integer> statusSet,String condition,String value) {
		return edocRecieveRecordDao.findEdocRecieveRecords(accountIds, departIds, statusSet,condition,value);
	}
	
	public List<EdocRecieveRecord> getWaitRegisterEdocRecieveRecords(Long userId)
	{
		return edocRecieveRecordDao.getToRegisterEdocs(userId);		
	}
		
	public void delete(long id) throws Exception {
		edocRecieveRecordDao.delete(id);
	}
	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,String sender,EdocSummary edocSummary) throws Exception {
		
			create(edocSendRecord, exchangeOrgId, exchangeType, replyId, aRecUnit,sender,null, edocSummary);
	
	}
	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,String sender,Long agentToId,EdocSummary edocSummary) throws Exception {
		
		String agentToName = "";//被代理人姓名
		if(agentToId != null){
			V3xOrgMember member = orgManager.getMemberById(agentToId);
			agentToName = member.getName();
		}
		User user = CurrentUser.get();
		String userName = "";
		if(null!=user){
			userName = user.getName();
		}
		String key = "exchange.sign";
				
		EdocRecieveRecord edocRecieveRecord = new EdocRecieveRecord();
		edocRecieveRecord.setIdIfNew();
		edocRecieveRecord.setSender(Strings.isBlank(agentToName)?sender:agentToName);
		edocRecieveRecord.setSubject(edocSummary.getSubject());
		edocRecieveRecord.setDocType(edocSendRecord.getDocType());
		edocRecieveRecord.setDocMark(edocSendRecord.getDocMark());
		edocRecieveRecord.setSecretLevel(edocSendRecord.getSecretLevel());
		edocRecieveRecord.setUrgentLevel(edocSendRecord.getUrgentLevel());
		edocRecieveRecord.setSendUnit(edocSendRecord.getSendUnit());
		edocRecieveRecord.setIssuer(edocSendRecord.getIssuer());
		edocRecieveRecord.setIssueDate(edocSendRecord.getIssueDate());
		edocRecieveRecord.setSendTo(aRecUnit[0]);
		edocRecieveRecord.setCopyTo(aRecUnit[1]);
		edocRecieveRecord.setReportTo(aRecUnit[2]);
		edocRecieveRecord.setReplyId(String.valueOf(replyId));
		edocRecieveRecord.setEdocId(edocSendRecord.getEdocId());
		edocRecieveRecord.setExchangeOrgId(exchangeOrgId);
		edocRecieveRecord.setExchangeType(exchangeType);		
		edocRecieveRecord.setFromInternal(true);
		if(replyId instanceof String)
		{
			edocRecieveRecord.setFromInternal(false);
		}
		edocRecieveRecord.setContentNo(edocSendRecord.getContentNo());
		long l = System.currentTimeMillis();
		edocRecieveRecord.setCreateTime(new Timestamp(l));
		edocRecieveRecord.setStatus(Constants.C_iStatus_Torecieve);
		edocRecieveRecordDao.save(edocRecieveRecord);
		
        //MessageReceiver receiver = new MessageReceiver(edocSendRecord.getId(), edocSendRecord.getSendUserId(),"message.link.exchange.receive", String.valueOf(edocSendRecord.getEdocId()));
        //userMessageManager.sendSystemMessage(new MessageContent(key,edocSendRecord.getSubject(),userName), ApplicationCategoryEnum.exSend, user.getId(), receiver);
		V3xOrgEntity entity = null;
		List<V3xOrgMember> member = null;
		if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept){
			entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, exchangeOrgId);
			member = EdocRoleHelper.getDepartMentExchangeUsers(entity.getOrgAccountId(), Long.valueOf(exchangeOrgId));			
		}else if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Org){
			//entity.getOrgAccountId() 和 exchangeOrgId不相等。导致查找单位角色的时候报错。直接取单位ID 29673
			member = EdocRoleHelper.getAccountExchangeUsers(exchangeOrgId);
		}
		
		for(int i = member.size()-1; i >= 0; i--){
			if(member.get(i).getSecretLevel() < edocSummary.getEdocSecretLevel()){
				member.remove(i);
			}
		}
		
		Affair affair = null;
		for(V3xOrgMember m:member){
			affair = new Affair();
			affair.setIdIfNew();
			affair.setApp(ApplicationCategoryEnum.exSign.getKey());
			affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			affair.setSubject(edocSummary.getSubject());
			affair.setMemberId(m.getId());
			affair.setIsFinish(false);
			affair.setObjectId(edocSummary.getId());
			affair.setSubObjectId(edocRecieveRecord.getId());
			affair.setSenderId(user.getId());
			affair.setState(StateEnum.edoc_exchange_receive.key());
			if(edocSummary.getUrgentLevel()!=null && !"".equals(edocSummary.getUrgentLevel()))
				affair.setImportantLevel(Integer.parseInt(edocSummary.getUrgentLevel()));
			if(user.getId()==-1)
			{
				affair.setExtProps(sender);
			}
			affairManager.addAffair(affair);
			MessageReceiver receiver_a = new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.exchange.receive", affair.getSubObjectId().toString());
			Long agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),m.getId());
			MessageReceiver agentReceiver  = null;
			if(agentMemberId!=null){
				agentReceiver = new MessageReceiver(affair.getId(), agentMemberId,"message.link.exchange.receive", affair.getSubObjectId().toString());
			}
			if(agentToId !=null){//当前处理人是代理人
				if(agentMemberId!=null){//当前查找出来的人还设置了代理人
					userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),agentToName).add("edoc.agent.deal", user.getName()).add("col.agent"), ApplicationCategoryEnum.exSign, agentToId, agentReceiver);
				}
	        	userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),agentToName).add("edoc.agent.deal", user.getName()), ApplicationCategoryEnum.exSign, agentToId, receiver_a);
	        }else{
	        	if(agentMemberId!=null){//当前查找出来的人还设置了代理人
	        		userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),userName).add("col.agent"), ApplicationCategoryEnum.exSign, user.getId(), agentReceiver);
	        	}
	        	userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),userName), ApplicationCategoryEnum.exSign, user.getId(), receiver_a);
			}
		}
	}
	public Boolean registerRecieveEdoc(Long id,Long reciveEdocId) throws Exception 
	{
		EdocRecieveRecord rec=getEdocRecieveRecord(id);
		rec.setRegisterTime(new Timestamp(System.currentTimeMillis()));
		rec.setStatus(Constants.C_iStatus_Registered);
		rec.setReciveEdocId(reciveEdocId);
		edocRecieveRecordDao.update(rec);
		
		Map<String, Object> columns=new Hashtable<String, Object>();
		columns.put("state",new Integer(StateEnum.col_done.getKey()));
	
		affairManager.update(columns, new Object[][]{{"objectId",new Long(rec.getEdocId())}, {"subObjectId",rec.getId()}});		
		User user = CurrentUser.get();
		Long regUserId = rec.getRegisterUserId();
		Long agentToId = null; //被代理人ID
		String agentToName= "";
		if(!Long.valueOf(user.getId()).equals(regUserId)){
			agentToId = regUserId;
			try{
				agentToName = orgManager.getMemberById(agentToId).getName();
			}catch(Exception e){
				
			}
		}
		if(agentToId != null){
			MessageContent msgContent=new MessageContent("exchange.edoc.register",agentToName,rec.getSubject()).add("edoc.agent.deal", user.getName());
			msgContent.setResource("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource");
			MessageReceiver receiver=new MessageReceiver(rec.getId(),rec.getRecUserId(),"message.link.exchange.register",rec.getId().toString());
			userMessageManager.sendSystemMessage(msgContent,ApplicationCategoryEnum.edoc,agentToId,receiver);
		}else{
			MessageContent msgContent=new MessageContent("exchange.edoc.register",user.getName(),rec.getSubject());
			msgContent.setResource("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource");
			MessageReceiver receiver=new MessageReceiver(rec.getId(),rec.getRecUserId(),"message.link.exchange.register",rec.getId().toString());
			userMessageManager.sendSystemMessage(msgContent,ApplicationCategoryEnum.edoc,user.getId(),receiver);	
		}
		return true;
	}
	
	public void delete(EdocRecieveRecord o)throws Exception{
		edocRecieveRecordDao.deleteObject(o);
	}

	public void deleteRecRecordByReplayId(long replayId)throws Exception{
		edocRecieveRecordDao.deleteReceiveRecordByReplayId(replayId);
	}

	public EdocRecieveRecord getReceiveRecordByReplyId(long replyId)throws Exception{
		return edocRecieveRecordDao.getRecRecordByReplayId(replyId);
	}	
	
	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public EdocRecieveRecord getEdocRecieveRecordByReciveEdocId(long id) {
		return edocRecieveRecordDao.getEdocRecieveRecordByReciveEdocId(id);
	}
}
