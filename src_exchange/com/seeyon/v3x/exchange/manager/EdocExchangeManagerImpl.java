package com.seeyon.v3x.exchange.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.edoc.domain.EdocObjTeamMember;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.event.EdocSignEvent;
import com.seeyon.v3x.edoc.domain.EdocSignReceipt;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.manager.EdocObjTeamManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.EactionType;
import com.seeyon.v3x.event.Event;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.exchange.dao.EdocRecieveRecordDao;
import com.seeyon.v3x.exchange.dao.EdocSendDetailDao;
import com.seeyon.v3x.exchange.dao.EdocSendRecordDao;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.exception.ExchangeException;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;

public class EdocExchangeManagerImpl implements EdocExchangeManager {

	private static final Log log = LogFactory.getLog(EdocExchangeManager.class);
	
	private SendEdocManager sendEdocManager;
	private EdocSendDetailDao edocSendDetailDao;
	private EdocRecieveRecordDao edocRecieveRecordDao;
	private RecieveEdocManager recieveEdocManager;
	private OrgManager orgManager;
	private AffairManager affairManager;
	private EdocSummaryManager edocSummaryManager;
	private UserMessageManager userMessageManager;
	private OperationlogManager operationlogManager;
	private EdocObjTeamManager edocObjTeamManager;
	private AppLogManager appLogManager;
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public EdocSendRecord getEdocSendRecordByDetailId(long detailId)
	{
		return sendEdocManager.getEdocSendRecordByDetailId(detailId);
	}

	public SendEdocManager getSendEdocManager() {
		return sendEdocManager;
	}

	public void setSendEdocManager(SendEdocManager sendEdocManager) {
		this.sendEdocManager = sendEdocManager;
	}

	public EdocSendDetailDao getEdocSendDetailDao() {
		return edocSendDetailDao;
	}

	public void setEdocSendDetailDao(EdocSendDetailDao edocSendDetailDao) {
		this.edocSendDetailDao = edocSendDetailDao;
	}

	public RecieveEdocManager getRecieveEdocManager() {
		return recieveEdocManager;
	}

	public void setRecieveEdocManager(RecieveEdocManager recieveEdocManager) {
		this.recieveEdocManager = recieveEdocManager;
	}

	public EdocRecieveRecordDao getEdocRecieveRecordDao() {
		return edocRecieveRecordDao;
	}

	public void setEdocRecieveRecordDao(
			EdocRecieveRecordDao edocRecieveRecordDao) {
		this.edocRecieveRecordDao = edocRecieveRecordDao;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	/**
	 * 是否具有当前登录单位的收文登记权
	 */
	public boolean isEdocCreateRole(Long userId)
	{
		boolean ret=false;
		try{
			ret=EdocRoleHelper.isEdocCreateRole(userId,EdocEnum.edocType.recEdoc.ordinal());
		}catch(Exception e)
		{			
		}
		return ret;
	}
	/**
	 * 判断是否具有指定单位下的收文登记权
	 * @param userId
	 * @param exchangeAccountId
	 * @return
	 */
	public boolean isEdocCreateRole(Long userId,Long exchangeAccountId)
	{
		boolean ret=false;
		try{
			ret=EdocRoleHelper.isEdocCreateRole(exchangeAccountId,userId,EdocEnum.edocType.recEdoc.ordinal());
		}catch(Exception e)
		{			
		}
		return ret;
	}
	public String checkExchangeRole(String typeAndIds)
	{
		String msg="";
		try
		{
			List<V3xOrgMember> roles=null;
			V3xOrgEntity tempEntity=null;
			List<V3xOrgEntity> list = new ArrayList<V3xOrgEntity>();
			String[] items = typeAndIds.split(V3xOrgEntity.ORG_ID_DELIMITER);
			String data[]=null;
			
			//对机构组进行处理
			List <String>strLs=new ArrayList<String>();
			for(String temp:items)
			{
				data = temp.split("[|]");
				if(EdocObjTeam.ENTITY_TYPE_OrgTeam.equals(data[0]))
				{
					EdocObjTeam et=edocObjTeamManager.getById(Long.parseLong(data[1]));
					Set <EdocObjTeamMember>mems=et.getEdocObjTeamMembers();
					for(EdocObjTeamMember mem:mems)
					{
						strLs.add(mem.getTeamType()+"|"+mem.getMemberId());
					}
				}
				else
				{
					strLs.add(temp);
				}				
			}
			items=new String[strLs.size()];
			strLs.toArray(items);
			Long accountId = CurrentUser.get().getAccountId();
			for(String item:items)
			{
				data = item.split("[|]");
				if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(data[0]))
				{
					roles=EdocRoleHelper.getAccountExchangeUsers(Long.parseLong(data[1]));					
				}
				else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(data[0]))
				{
					tempEntity=orgManager.getEntity(item);
					roles=EdocRoleHelper.getDepartMentExchangeUsers(tempEntity.getOrgAccountId(),tempEntity.getId());					
				}
				else
				{	
					tempEntity=null;
					roles=null;
					continue;
				}				
				if(roles==null || roles.size()<=0)
				{
					if(tempEntity==null){tempEntity=orgManager.getEntity(item);}
					if(!"".endsWith(msg)){msg+=",";}
					if(tempEntity!=null)
					{
						msg+=tempEntity.getName();
						if(!tempEntity.getOrgAccountId().equals(accountId))
						{
							V3xOrgEntity acc = orgManager.getEntity(V3xOrgAccount.class,tempEntity.getOrgAccountId());
							if(acc!=null) msg+="("+((V3xOrgAccount)acc).getShortname()  +")";
						}
					}
				}
				//判断如果取得的单位或部门ID有误的情况
				Long id=Long.parseLong(data[1]);
				if("Account".equals(data[0])&&orgManager.getAccountById(id)==null){
					log.error("不存在Id="+id+"的单位");
					msg="no account";
				}else if("Department".equals(data[0])&&orgManager.getDepartmentById(id)==null){
					log.error("不存在Id="+id+"的部门");
					msg="no dept";
				}
				tempEntity=null;
				roles=null;
			}
			if("".equals(msg)){msg="check ok";}
		}catch(Exception e)
		{
			msg="unknow err";
			log.error("判断交换角色异常：",e);
		}
		return msg;
	}
	public void sendEdoc(EdocSendRecord edocSendRecord, long sendUserId,String sender, boolean reSend) throws Exception {
		sendEdoc(edocSendRecord, sendUserId, sender, null, reSend,null);
	}
	/**
	 * 交换-发送公文（签发）
	 */
	public void sendEdoc(EdocSendRecord edocSendRecord, long sendUserId,String sender,Long agentToId, boolean reSend, List<EdocSendDetail> tempDetail) throws Exception {
		//EdocSendRecord edocSendRecord = sendEdocManager.getEdocSendRecord(id);
		String sendKey = "exchange.sent";
		if (edocSendRecord.getStatus() != Constants.C_iStatus_Tosend && (reSend == false)) {
			return;
		}

		User user = CurrentUser.get();
		
		Set<EdocSendDetail> sendDetails = (Set<EdocSendDetail>) edocSendRecord
				.getSendDetails();
		
		EdocSummary summary = edocSummaryManager.findById(edocSendRecord.getEdocId());
		if (sendDetails != null && sendDetails.size() > 0) 
		{
	
			Iterator it = sendDetails.iterator();
			int type = edocSendRecord.getExchangeType();
			//待发送时，选择的发送单位要记录到接收记录里面，用于显示
			String[] aRecUnit = new String[3];
			aRecUnit[0] = edocSendRecord.getSendEntityNames();
			
			while (it.hasNext()) {
				EdocSendDetail sendDetail = (EdocSendDetail) it.next();
				String exchangeOrgId = sendDetail.getRecOrgId();
				int exchangeOrgType = sendDetail.getRecOrgType();
				long replyId = sendDetail.getId();			
				if(sendDetail.getStatus()!=0){
					continue;
				}
				// 内部交换
			//	if (exchangeOrgType == Constants.C_iAccountType_Dept) {
				recieveEdocManager.create(edocSendRecord, Long
						.valueOf(exchangeOrgId), exchangeOrgType, replyId,
						aRecUnit,sender,agentToId,summary);
	//			}else if(exchangeOrgType == Constants.C_iAccountType_Org){
	//				recieveEdocManager.create(edocSendRecord, Long
	//						.valueOf(exchangeOrgId), exchangeOrgType, replyId,
	//						aRecUnit,sender,summary);				
	//			} else {
	//				// 外部交换
	//				// Todo:
	//			}
			}
		}

		/*
		 * for (int i = 0; i < sendDetails.size(); i++) { EdocSendDetail
		 * sendDetail = sendDetails.get(i); String exchangeOrgId =
		 * sendDetail.getRecOrgId(); int exchangeOrgType =
		 * sendDetail.getRecOrgType(); long replyId = sendDetail.getId(); //
		 * 内部交换 if (exchangeOrgType != Constants.C_iExchangeType_ExternalOrg) {
		 * recieveEdocManager.create(edocSendRecord,
		 * Long.valueOf(exchangeOrgId), exchangeOrgType, replyId, aRecUnit); }
		 * else { // 外部交换 // Todo: } }
		 */

		edocSendRecord.setSendUserId(agentToId == null ? sendUserId : agentToId );
		long l = System.currentTimeMillis();
		edocSendRecord.setSendTime(new Timestamp(l));
		edocSendRecord.setStatus(Constants.C_iStatus_Sent);
		Set<EdocSendDetail> tempSendDetailList=edocSendRecord.getSendDetails();
		tempSendDetailList.addAll(tempDetail);
		edocSendRecord.setSendDetails(tempSendDetailList);
		if(reSend){
			sendEdocManager.reSend(edocSendRecord, summary);
			sendKey = "exchange.resend";
		}else{
			sendEdocManager.update(edocSendRecord);
		}
		
		/*
		MessageReceiver receiver = new MessageReceiver(edocSendRecord.getId(), edocSendRecord.getSendUserId());
        userMessageManager.sendSystemMessage(new MessageContent(sendKey, edocSendRecord.getSubject(),user.getName()), ApplicationCategoryEnum.exSend, user.getId(), receiver);
		*/
	}
	
	public void sendEdoc(long id, long sendUserId) throws Exception {
		User user = CurrentUser.get();
		EdocSendRecord edocSendRecord = sendEdocManager.getEdocSendRecord(id);
		if (edocSendRecord.getStatus() != Constants.C_iStatus_Tosend) {
			return;
		}

		//String[] aRecUnit = new String[3];
		// Todo: get aRecUnit()

		// List<EdocSendDetail> sendDetails = (List<EdocSendDetail>)
		// edocSendRecord.getSendDetails(); //Enable force Set to List,so use
		// Set.

		Set<EdocSendDetail> sendDetails = (Set<EdocSendDetail>) edocSendRecord
				.getSendDetails();
			
		EdocSummary summary = edocSummaryManager.findById(edocSendRecord.getEdocId());
		if (sendDetails != null && sendDetails.size() > 0) 
		{
		Iterator it = sendDetails.iterator();
		while (it.hasNext()) {
			EdocSendDetail sendDetail = (EdocSendDetail) it.next();
			String exchangeOrgId = sendDetail.getRecOrgId();
			int exchangeOrgType = sendDetail.getRecOrgType();
			long replyId = sendDetail.getId();

			String[] aRecUnit = new String[3];
			aRecUnit[0] = sendDetail.getRecOrgName();
			
			// 内部交换
			if (exchangeOrgType != Constants.C_iExchangeType_ExternalOrg) {
				recieveEdocManager.create(edocSendRecord, Long
						.valueOf(exchangeOrgId), exchangeOrgType, replyId,
						aRecUnit,summary);
			} else {
				// 外部交换
				// Todo:
			}
		}
		}
		/*
		 * for (int i = 0; i < sendDetails.size(); i++) { EdocSendDetail
		 * sendDetail = sendDetails.get(i); String exchangeOrgId =
		 * sendDetail.getRecOrgId(); int exchangeOrgType =
		 * sendDetail.getRecOrgType(); long replyId = sendDetail.getId(); //
		 * 内部交换 if (exchangeOrgType != Constants.C_iExchangeType_ExternalOrg) {
		 * recieveEdocManager.create(edocSendRecord,
		 * Long.valueOf(exchangeOrgId), exchangeOrgType, replyId, aRecUnit); }
		 * else { // 外部交换 // Todo: } }
		 */

		edocSendRecord.setSendUserId(sendUserId);
		long l = System.currentTimeMillis();
		edocSendRecord.setSendTime(new Timestamp(l));
		edocSendRecord.setStatus(Constants.C_iStatus_Sent);
		sendEdocManager.update(edocSendRecord);
		MessageReceiver receiver = new MessageReceiver(edocSendRecord.getId(), edocSendRecord.getSendUserId());
        userMessageManager.sendSystemMessage(new MessageContent("exchange.sent",edocSendRecord.getSubject(),user.getName()), ApplicationCategoryEnum.exSend, user.getId(), receiver);	

	}

	public void stepBackEdoc(Long stepBackEdocId, String stepBackInfo,
			Long currentUserId, String currentUserName,
			EdocSummary stepBackEdocSummary) throws Exception {
		// 取得待回退的公文对象（待签收公文）
		EdocRecieveRecord edocRecieveRecord = recieveEdocManager
				.getEdocRecieveRecord(stepBackEdocId);

		// 对此待签收公文进行回执
		Long replyId = Long.parseLong(edocRecieveRecord.getReplyId());
		EdocSendDetail edocSendDetail = edocSendDetailDao.get(replyId);
		// 回退步骤1更新待回退公文的detail
		if(edocSendDetail!=null)
		{//回退时，不要写签收编号
			// 也不写签收人和签收时间（?）
			edocSendDetail.setRecUserName(currentUserName);
			long l = System.currentTimeMillis();
			edocSendDetail.setRecTime(new Timestamp(l));
			edocSendDetail.setStatus(Constants.C_iStatus_Send_StepBacked);
			edocSendDetailDao.update(edocSendDetail);
		}

		// 回退步骤2，更新此待签收公文状态 3回退
		edocRecieveRecord.setStatus(Constants.C_iStatus_Receive_StepBacked);
		recieveEdocManager.update(edocRecieveRecord);
		
		// 取得待回退公文的recode
		long sendRecordId = 0L;
		if (edocSendDetail != null)
			sendRecordId = edocSendDetail.getSendRecordId().longValue();
		EdocSendRecord edocSendRecord = sendEdocManager
				.getEdocSendRecord(sendRecordId);
		
		// 回退步骤3更新待回退公文的record中的回退说明、回退状态
		edocSendRecord.setStepBackInfo(stepBackInfo);
		edocSendRecord.setStatus(Constants.C_iStatus_Send_StepBacked);
		sendEdocManager.update(edocSendRecord);

		// 回退步骤4 给公文的发文单位的公文交换员发消息
		Affair affair = null;
		affair = new Affair();
		affair.setIdIfNew();
		affair.setApp(ApplicationCategoryEnum.exSend.getKey());
		affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
		affair.setSubject(stepBackEdocSummary.getSubject());
		affair.setMemberId(edocSendRecord.getSendUserId());
		affair.setIsFinish(false);
		affair.setObjectId(stepBackEdocSummary.getId());
		affair.setSubObjectId(edocSendRecord.getId());
		affair.setSenderId(stepBackEdocSummary.getStartUserId());
		affair.setState(StateEnum.edoc_exchange_send.key());

		affairManager.addAffair(affair);
		MessageReceiver receiver = new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.exchange.send",affair.getSubObjectId(),affair.getId());
		userMessageManager.sendSystemMessage(new MessageContent(
				"exchange.stepback", edocSendRecord.getSubject(),
				currentUserName, stepBackInfo),
				ApplicationCategoryEnum.exSign, currentUserId, receiver);

	}

	// 签收公文时，对公文进行自动回执。
	private void replyEdoc(long replyId, String content, String recUserName,String recNo,Long agentToId)
			throws Exception {
		User user = CurrentUser.get();
		EdocSendDetail edocSendDetail = edocSendDetailDao.get(replyId);
		if(edocSendDetail!=null)
		{//签收时候，发送记录已经被删除，不需要更新签收信息
		edocSendDetail.setContent(content);
		edocSendDetail.setRecNo(recNo);
		edocSendDetail.setRecUserName(recUserName);
		long l = System.currentTimeMillis();
		edocSendDetail.setRecTime(new Timestamp(l));
		edocSendDetail.setStatus(Constants.C_iStatus_Recieved);
		edocSendDetailDao.update(edocSendDetail);
		}
		EdocSendRecord edocSendRecord = this.getEdocSendRecordByDetailId(replyId);
		if(null!=edocSendRecord && null!=edocSendRecord.getSubject()){
			MessageReceiver receiver = new MessageReceiver(replyId, edocSendRecord.getSendUserId());
			if(agentToId!=null){
				String agentToName= "";
				try{
					agentToName = orgManager.getMemberById(agentToId).getName();
				}catch(Exception e){
					log.error(e);
				}
				userMessageManager.sendSystemMessage(new MessageContent("exchange.signed", edocSendRecord.getSubject(),agentToName).add("edoc.agent.deal", user.getName()),
						ApplicationCategoryEnum.exSign, agentToId, receiver);
			}else{
				userMessageManager.sendSystemMessage(new MessageContent("exchange.signed", edocSendRecord.getSubject(),user.getName()), ApplicationCategoryEnum.exSign, user.getId(), receiver);
			}
		}
	}
	
	public void recEdoc(long id, long recUserId, long registerUserId,
			String recNo, String remark,String keepPeriod,Long agentToId) throws Exception {
		EdocRecieveRecord edocRecieveRecord = recieveEdocManager
				.getEdocRecieveRecord(id);
		if (edocRecieveRecord == null
				|| edocRecieveRecord.getStatus() != Constants.C_iStatus_Torecieve) {
			return;
		}

		// 签收公文
		edocRecieveRecord.setRemark(remark);
		if(agentToId!=null)
			recUserId = agentToId;
		edocRecieveRecord.setRecUserId(recUserId);
		long l = System.currentTimeMillis();
		edocRecieveRecord.setRecTime(new Timestamp(l));
		edocRecieveRecord.setRegisterUserId(registerUserId);
		edocRecieveRecord.setRecNo(recNo);
		edocRecieveRecord.setKeepPeriod(keepPeriod);
		edocRecieveRecord.setStatus(Constants.C_iStatus_Recieved);
		recieveEdocManager.update(edocRecieveRecord);

		// 回执公文
		String replyId = edocRecieveRecord.getReplyId();
		String recUserName = "";
		V3xOrgEntity member = orgManager.getEntity("Member", recUserId);
		if (member != null) {
			recUserName = member.getName();
		}
		// 来自内部交换的公文（同一套系统）
		if (edocRecieveRecord.getFromInternal()) {
			this.replyEdoc(Long.valueOf(replyId), remark, recUserName,recNo,agentToId);
		} else { // 来自外部系统的公文
			EdocSignEvent event = new EdocSignEvent(this);
			EdocSignReceipt esr = new EdocSignReceipt();
			esr.setOpinion(remark);
			esr.setReceipient(recUserName);
			long signTime = System.currentTimeMillis();
			esr.setSignTime(signTime);
			
			if(com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept==edocRecieveRecord.getExchangeType()){
				try {
					V3xOrgDepartment dept = orgManager.getDepartmentById(edocRecieveRecord.getExchangeOrgId());
					esr.setSignUnit(dept.getName());
				} catch (BusinessException e) {
					log.error("查找部门异常:",e);
				}
			}else if(com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Org==edocRecieveRecord.getExchangeType()){
				try {
					V3xOrgAccount account = orgManager.getAccountById(edocRecieveRecord.getExchangeOrgId());
					esr.setSignUnit(account.getName());
				} catch (BusinessException e) {
					log.error("查找单位异常:",e);
				}
			}
			event.setEdocSignReceipt(esr);
			event.setSendDetailId(Long.valueOf(replyId));
			EventDispatcher.fireEvent(event);
		}

		// 生成个人待办事项（待登记事项）
		// todo:
	}

	public List<EdocSendRecord> getSendEdocs(long userId, long orgId, int status) throws Exception{
		
		String accountIds=null;
		String depIds=null;
		User user=CurrentUser.get();		
		accountIds=EdocRoleHelper.getUserExchangeAccountIds();
		depIds=EdocRoleHelper.getUserExchangeDepartmentIds();
		return sendEdocManager.findEdocSendRecords(accountIds, depIds, status,null,null);
		//return sendEdocManager.getEdocSendRecords(status);
	}
	public List<EdocSendRecord> getSendEdocs(long userId, long orgId, int status,String condition,String value) throws Exception{
		
		String accountIds=null;
		String depIds=null;	
		//将兼职单位，兼职部门的也查找出来
		accountIds=EdocRoleHelper.getUserExchangeAccountIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		depIds=EdocRoleHelper.getUserExchangeDepartmentIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		List<EdocSendRecord> list = sendEdocManager.findEdocSendRecords(accountIds, depIds, status,condition,value);
		List<EdocSendRecord> nowUserList=new ArrayList<EdocSendRecord>();
		if(list != null)
			for(EdocSendRecord r : list){
				if(r.getSendUserId()==0||r.getSendUserId()==userId){
					nowUserList.add(r);
				}
				//存放交接人
				long undertakerId = r.getSendUserId();
				V3xOrgMember member = orgManager.getMemberById(undertakerId);
				if(null!=member){
					r.setSendUserNames(member.getName());
				}else{
					r.setSendUserNames("");
				}
				
				EdocSummary summary = edocSummaryManager.findById(r.getEdocId());
				if(summary != null){
						Integer currentNo = r.getContentNo();
						if(null!=currentNo && (currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_FIRST || currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_NORMAL)){
							r.setSendNames(summary.getSendTo());
						}else if(null!=currentNo && currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_SECOND){
							r.setSendNames(summary.getSendTo2());
						}
					}
				}
		
		return nowUserList;
		//return sendEdocManager.getEdocSendRecords(status);
	}
	//成发集团项目 重写getSendEdocs
	public List<EdocSendRecord> getSendEdocs(long userId, long orgId, int status,String condition,String value,Integer secretLevel) throws Exception{
		
		String accountIds=null;
		String depIds=null;	
		//将兼职单位，兼职部门的也查找出来
		accountIds=EdocRoleHelper.getUserExchangeAccountIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		depIds=EdocRoleHelper.getUserExchangeDepartmentIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		List<EdocSendRecord> list = sendEdocManager.findEdocSendRecords(accountIds, depIds, status,condition,value);
		List<EdocSendRecord> nowUserList=new ArrayList<EdocSendRecord>();
		List<EdocSendRecord> list1 = new ArrayList<EdocSendRecord>();//成发项目
		if(list != null)
			//成发项目 程炯 筛选send中的密级list begin
			for(EdocSendRecord e : list){
				EdocSummary summary = edocSummaryManager.findById(e.getEdocId());
				if(summary != null){
					if(summary.getEdocSecretLevel() == null || "".equals(summary.getEdocSecretLevel())){
						list1.add(e);
						continue;
					}
					if(secretLevel >= summary.getEdocSecretLevel()){
						list1.add(e);
					}
				}
			}
			//end
			for(EdocSendRecord r : list1){
				if(r.getSendUserId()==0||r.getSendUserId()==userId){
					nowUserList.add(r);
				}
				//存放交接人
				long undertakerId = r.getSendUserId();
				V3xOrgMember member = orgManager.getMemberById(undertakerId);
				if(null!=member){
					r.setSendUserNames(member.getName());
				}else{
					r.setSendUserNames("");
				}
				
				EdocSummary summary = edocSummaryManager.findById(r.getEdocId());
				if(summary != null){
						Integer currentNo = r.getContentNo();
						if(null!=currentNo && (currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_FIRST || currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_NORMAL)){
							r.setSendNames(summary.getSendTo());
						}else if(null!=currentNo && currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_SECOND){
							r.setSendNames(summary.getSendTo2());
						}
					}
				}
		
		return nowUserList;
		//return sendEdocManager.getEdocSendRecords(status);
	}

	public List<EdocRecieveRecord> getRecieveEdocs(long userId, long orgId,
			Set<Integer> statusSet,String condition,String value)throws Exception {		
		String accountIds=null;
		String depIds=null;
		User user=CurrentUser.get();	
		//将兼职单位，兼职部门的也查找出来
		accountIds=EdocRoleHelper.getUserExchangeAccountIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		depIds=EdocRoleHelper.getUserExchangeDepartmentIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);	
		List<EdocRecieveRecord> list = recieveEdocManager.findEdocRecieveRecords(accountIds, depIds, statusSet,condition,value);
	
		if(list != null)
			for(EdocRecieveRecord r : list){
				//根据签收记录的签收人ID，查找签收人姓名，用于前台显示
				V3xOrgMember member = orgManager.getMemberById(r.getRecUserId());
				if(null!=member){
					r.setRecUser(member.getName());
				}
				EdocSummary summary = edocSummaryManager.findById(r.getEdocId());
				r.setCopies(summary==null?0:summary.getCopies());
			}
		
		return list;
	//	return recieveEdocManager.getEdocRecieveRecords(status);
	}
	//成发集团项目  程炯 重写getRecieveEdocs
	public List<EdocRecieveRecord> getRecieveEdocs(long userId, long orgId,
			Set<Integer> statusSet,String condition,String value,Integer secretLevel)throws Exception {		
		String accountIds=null;
		String depIds=null;
		User user=CurrentUser.get();	
		//将兼职单位，兼职部门的也查找出来
		accountIds=EdocRoleHelper.getUserExchangeAccountIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		depIds=EdocRoleHelper.getUserExchangeDepartmentIds(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);	
		List<EdocRecieveRecord> list = recieveEdocManager.findEdocRecieveRecords(accountIds, depIds, statusSet,condition,value);
		List<EdocRecieveRecord> list1 = new ArrayList<EdocRecieveRecord>();//成发集团项目
		if(list != null)
			//成发集团项目 程炯 筛选EdocRecieveRecord list  begin
			for(EdocRecieveRecord l : list){
				EdocSummary summary = edocSummaryManager.findById(l.getEdocId());
				if(summary.getEdocSecretLevel() == null || "".equals(summary.getEdocSecretLevel())){
					list1.add(l);
					continue;
				}
				if(secretLevel >= summary.getEdocSecretLevel()){
					list1.add(l);
				}
			}
			//end
			for(EdocRecieveRecord r : list1){
				//根据签收记录的签收人ID，查找签收人姓名，用于前台显示
				V3xOrgMember member = orgManager.getMemberById(r.getRecUserId());
				if(null!=member){
					r.setRecUser(member.getName());
				}
				EdocSummary summary = edocSummaryManager.findById(r.getEdocId());
				r.setCopies(summary==null?0:summary.getCopies());
			}
		
		return list1;
	//	return recieveEdocManager.getEdocRecieveRecords(status);
	}

	public List<EdocRecieveRecord> getToRegisterEdocs(long userId) {
		return edocRecieveRecordDao.getToRegisterEdocs(userId);
	}

	public void registerEdoc(long id) throws Exception {
		EdocRecieveRecord edocRecieveRecord = recieveEdocManager
				.getEdocRecieveRecord(id);
		if (edocRecieveRecord == null
				|| edocRecieveRecord.getStatus() != Constants.C_iStatus_Recieved) {
			return;
		}
		edocRecieveRecord.setStatus(Constants.C_iStatus_Registered);
		recieveEdocManager.update(edocRecieveRecord);
	}

	public EdocSendRecord getSendRecordById(long id) {
		return sendEdocManager.getEdocSendRecord(id);
	}

	public EdocRecieveRecord getReceivedRecord(long id) {
		EdocRecieveRecord ret = recieveEdocManager.getEdocRecieveRecord(id);
		if(null==ret){
			return ret;
		}
		EdocSummary s = edocSummaryManager.findById(ret.getEdocId());
		ret.setCopies(s.getCopies());
		
		return ret;
	}
	
	public void deleteByType(String id,String type)throws Exception{
		String[] ids = id.split(",");
		User user=CurrentUser.get();
		if(null!=type && "send".equals(type)){
			for(int i=0;i<ids.length;i++){
				String edocSubject=sendEdocManager.getEdocSendRecord(Long.valueOf(ids[i])).getSubject();
				sendEdocManager.delete(Long.valueOf(ids[i]));
				appLogManager.insertLog(user, AppLogAction.Edoc_Sended_Record_Del,user.getName(),edocSubject);
			}
		}else{
			for(int i=0;i<ids.length;i++){
				String edocSubject=recieveEdocManager.getEdocRecieveRecord(Long.valueOf(ids[i])).getSubject();
				recieveEdocManager.delete(Long.valueOf(ids[i]));
				appLogManager.insertLog(user, AppLogAction.Edoc_Sign_Record_Del,user.getName(),edocSubject);
			}
		}
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	
	public List<EdocSendDetail> createSendRecord(Long sendRecordId,String typeAndIds) throws ExchangeException
	{
		return sendEdocManager.createSendRecord(sendRecordId, typeAndIds);
	}
	public boolean hasExchangeItem(Long userId)
	{
		boolean installEdoc=SystemEnvironment.hasPlugin("edoc");
		if(installEdoc==false){return false;}
		int icount=affairManager.countPendingOfExchange(userId);
		if(icount<=0){return false;}
		return true;
	}
	
	public void withdraw(String replyId)throws Exception{
		if(Strings.isBlank(replyId)){
			return;
		}
		User user = CurrentUser.get();
		EdocRecieveRecord record = recieveEdocManager.getReceiveRecordByReplyId(Long.valueOf(replyId).longValue());
		if(null==record)return;
		Map conditions = new HashMap();
		conditions.put("app", ApplicationCategoryEnum.exSign.key());
        conditions.put("objectId", record.getEdocId());
        conditions.put("subObjectId", record.getId());
        List<Affair> affairList =affairManager.getByConditions(conditions);
        
        recieveEdocManager.delete(record);//删除待签收记录

        //recieveEdocManager.deleteRecRecordByReplayId(Long.valueOf(replyId).longValue()); //删除待签收记录

    	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();//删除完毕后，更新每一个签收的待办事项
		if(null!=affairList && affairList.size()>0){
			for(Affair af: affairList){
        		if(af.getMemberId() != user.getId() && (af.getIsDelete()!= true)){
        			receivers.add(new MessageReceiver(af.getId(), af.getMemberId()));
        		}
				af.setState(StateEnum.edoc_exchange_withdraw.getKey()); //代办事项状态：被撤销
				af.setIsDelete(true);
				af.setIsFinish(true);
				affairManager.updateAffair(af);
			}
		}		
		
        operationlogManager.insertOplog(record.getId(), ApplicationCategoryEnum.exchange, EactionType.LOG_EXCHANGE_WITHDRAW, EactionType.LOG_EXCHANGE_WITHDRAWD_DESCRIPTION, user.getName(), record.getSubject());		
		
		//给每一个人发消息
    	if(null!=receivers && receivers.size()>0){
    		userMessageManager.sendSystemMessage(new MessageContent("exchange.withdraw", affairList.get(0).getSubject(), user.getName(),affairList.get(0).getApp()), ApplicationCategoryEnum.edocSign, user.getId(), receivers);
    	}
    	
    	//最后清楚回执记录
		sendEdocManager.deleteRecordDetailById(Long.valueOf(replyId).longValue()); //删除回执记录
	}

	/**
	 * 检查是否可以 撤销已经发送的公文交换记录 
	 * @param replyId ajax穿参:公文交换记录的ID
	 * @return
	 * @throws Exception
	 */
	public boolean canWithdraw(String replyId, String detailId)throws Exception{
		
		boolean bool = true;
		//设置默认变量 ，可以撤销true
		if(Strings.isBlank(replyId))return false;
		EdocSendRecord edocSendRecord = sendEdocManager.getEdocSendRecord(Long.valueOf(replyId));
		//查找出公文交换记录
		Set<EdocSendDetail> sendDetails = (Set<EdocSendDetail>) edocSendRecord.getSendDetails();
		//根据公文交换记录得到交换的回执记录
		if (sendDetails == null || sendDetails.size() == 0) {
			return false;
			//如果回执记录为空，返回false;
		}

		Iterator it = sendDetails.iterator();
		//迭代绘制记录集合
		while (it.hasNext()) {
			EdocSendDetail sendDetail = (EdocSendDetail) it.next();
			if(sendDetail.getId().longValue() == Long.valueOf(detailId).longValue() && sendDetail.getStatus() == Constants.C_iStatus_Recieved){
				bool = false;
				//如果其中状态为已签收的回执记录，bool -> false(不可撤销)
			}
		}
		
		return bool;
	}
	/**
	 * Ajax判断某个公文收发员是否有待交换和待签收的Affair事项。
	 * @param userId :用户ID
	 * @return
	 */
	public String checkEdocExchangeHasPendingAffair(Long userId){
		int countEdocSend=affairManager.countDoneOfEdocMobile(userId,ApplicationCategoryEnum.exSend,StateEnum.edoc_exchange_send.key());
		int countEdocRecieve=affairManager.countDoneOfEdocMobile(userId, ApplicationCategoryEnum.exSign, StateEnum.edoc_exchange_receive.key());
		if(countEdocSend+countEdocRecieve>0) return "1";
		else return "0";
	}
	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public EdocObjTeamManager getEdocObjTeamManager() {
		return edocObjTeamManager;
	}

	public void setEdocObjTeamManager(EdocObjTeamManager edocObjTeamManager) {
		this.edocObjTeamManager = edocObjTeamManager;
	}

	public void changeRegisterEdocPerson(String edocRecieveRecordId,
			String newRegisterUserId, String newRegisterUserName,
			String changeOperUserName, String changeOperUserID)
			throws Exception {
		EdocRecieveRecord edocRecieveRecord = recieveEdocManager
				.getEdocRecieveRecord(Long.parseLong(edocRecieveRecordId));
		// 旧的登记人，用来发送消息
		Long oldRegisterUserId = edocRecieveRecord.getRegisterUserId();
		// 结束旧的登记人的事项
		Map conditions = new HashMap();
		conditions.put("app", ApplicationCategoryEnum.edocRegister.key());
		conditions.put("objectId", edocRecieveRecord.getEdocId());
		conditions.put("subObjectId", edocRecieveRecord.getId());
		conditions.put("memberId", oldRegisterUserId);
		conditions.put("isFinish", false);
		conditions.put("isDelete", false);
		List<Affair> affairList = affairManager.getByConditions(conditions);
		if (null != affairList && affairList.size() > 0) {
			List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
			// 登记，只有一个登记人，所以第一位就是旧登记人
			Affair af = affairList.get(0);
			if (af.getMemberId().longValue() == oldRegisterUserId
					&& (af.getIsDelete() != true)) {
				Long senderId = af.getSenderId();
				receivers
						.add(new MessageReceiver(af.getId(), af.getMemberId()));
				af.setIsFinish(true);
				af.setIsDelete(true);
				affairManager.updateAffair(af);
				// 给旧的登记人发送消息
				if (null != receivers && receivers.size() > 0) {
					userMessageManager.sendSystemMessage(new MessageContent(
							"exchange.changeRegister", af.getSubject(),
							newRegisterUserName, af.getApp()),
							ApplicationCategoryEnum.edocRec, Long
									.parseLong(changeOperUserID),
							receivers);
				}
				// 设置新的登记人
				edocRecieveRecord.setRegisterUserId(Long
						.parseLong(newRegisterUserId));
				edocRecieveRecord.setRegisterName(newRegisterUserName);
				recieveEdocManager.update(edocRecieveRecord);
				// 为新的登记人生成事项
				Affair reAffair = new Affair();
				reAffair.setIdIfNew();
				reAffair.setApp(ApplicationCategoryEnum.edocRegister.getKey());
				reAffair.setSubject(edocRecieveRecord.getSubject());
				reAffair
						.setCreateDate(new Timestamp(System.currentTimeMillis()));
				reAffair.setMemberId(edocRecieveRecord.getRegisterUserId());
				reAffair.setObjectId(edocRecieveRecord.getEdocId());
				reAffair.setSubObjectId(edocRecieveRecord.getId());
				reAffair.setSenderId(senderId);
				reAffair.setState(StateEnum.edoc_exchange_register.getKey());
				reAffair.setIsTrack(null);
				if (edocRecieveRecord.getUrgentLevel() != null
						&& !"".equals(edocRecieveRecord.getUrgentLevel()))
					reAffair.setImportantLevel(Integer
							.parseInt(edocRecieveRecord.getUrgentLevel()));
				affairManager.addAffair(reAffair);

				/*
				 * 发消息给待登记人
				 */
				String key = "exchange.changeRegister.register";
				MessageReceiver receiver = new MessageReceiver(
						reAffair.getId(),
						reAffair.getMemberId(),
						"message.link.exchange.register.pending",
						com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href,
						String.valueOf(EdocEnum.edocType.recEdoc.ordinal()),
						reAffair.getSubObjectId().toString(), reAffair
								.getObjectId().toString());
				userMessageManager.sendSystemMessage(new MessageContent(key,
						reAffair.getSubject(), changeOperUserName),
						ApplicationCategoryEnum.edocRegister, Long
								.parseLong(changeOperUserID), receiver);
			}
		}
	}

	public boolean isBeRegistered(String edocRecieveRecordId) {
		boolean isCanBeRegisted = true;
		EdocRecieveRecord record = recieveEdocManager.getEdocRecieveRecord(Long
				.parseLong(edocRecieveRecordId));
		if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Registered) {// 公文已经登记
			isCanBeRegisted = false;
		}
		return isCanBeRegisted;
	}

	public void stepBackRecievedEdoc(long id, long referenceAffairId,
			String stepBackInfo)
			throws Exception {
		// 回退时，不区分内部外部系统公文，在待签收回退的时候，加入防护，当edocRecieveRecord.getFromInternal()为true时，内部公文才允许回退
		EdocRecieveRecord edocRecieveRecord = recieveEdocManager
				.getEdocRecieveRecord(id);
		if (edocRecieveRecord == null
				|| edocRecieveRecord.getStatus() != Constants.C_iStatus_Recieved) {
			return;
		}

		User user = CurrentUser.get();
		String senderName = edocRecieveRecord.getSender();
		// 1恢复exchange_recieve_edoc表中的记录，status为C_iStatus_Torecieve 待签收
		edocRecieveRecord.setRemark(null);
		// 签收人
		// edocRecieveRecord.setRecUserId();
		edocRecieveRecord.setRecTime(null);
		// 登记人，因为是回退，这里记录的是回退用户ID
		// edocRecieveRecord.setRegisterUserId(stepBackUserId);
		edocRecieveRecord.setRecNo(null);
		edocRecieveRecord.setKeepPeriod(null);
		edocRecieveRecord.setStatus(Constants.C_iStatus_Torecieve);
		edocRecieveRecord.setStepBackInfo(stepBackInfo);
		recieveEdocManager.update(edocRecieveRecord);

		// 2恢复exchange_send_edoc_detail表中的对应记录，status为C_iStatus_Torecieve 待签收
		String replyId = edocRecieveRecord.getReplyId();
		EdocSendDetail edocSendDetail = edocSendDetailDao.get(Long
				.valueOf(replyId));
		if (edocSendDetail != null) {
			edocSendDetail.setContent(null);
			edocSendDetail.setRecNo(null);
			edocSendDetail.setRecUserName(null);
			edocSendDetail.setStatus(Constants.C_iStatus_Torecieve);
			edocSendDetailDao.update(edocSendDetail);
		}
		// 3结束已经发送的affair（待登记）
		// 批量更新
		Map<String, Object> columns = new Hashtable<String, Object>();
		columns.put("isDelete", true);
		affairManager.update(columns, new Object[][]{
				{"app", ApplicationCategoryEnum.edocRegister.getKey()},
				{"state", StateEnum.edoc_exchange_register.key()},
				{"objectId", edocRecieveRecord.getEdocId()}});

		// 4发出消息，被回退（只发给签收人）

		MessageReceiver receiver = new MessageReceiver(referenceAffairId,
				edocRecieveRecord.getRecUserId());
		userMessageManager.sendSystemMessage(new MessageContent(
				"exchange.stepback", edocRecieveRecord.getSubject(), user
						.getName(), stepBackInfo),// 是否需要回退信息？
				ApplicationCategoryEnum.edocRegister, user.getId(), receiver);

		// 5增加操作人当前单位的公文收发员的待签收的affair
		String key = "exchange.sign";
		List<V3xOrgMember> member = null;
		if (edocSendDetail.getRecOrgType() == 1) {
			// 内部单位
			member = EdocRoleHelper.getAccountExchangeUsers(user
					.getLoginAccount());
		} else if (edocSendDetail.getRecOrgType() == 2) {
			// 内部部门
			member = EdocRoleHelper.getDepartMentExchangeUsers(user
					.getLoginAccount(), Long.valueOf(user.getDepartmentId()));
		}
		Affair affair = null;
		for (V3xOrgMember m : member) {
			affair = new Affair();
			affair.setIdIfNew();
			affair.setApp(ApplicationCategoryEnum.exSign.getKey());
			affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			affair.setSubject(edocRecieveRecord.getSubject());
			affair.setMemberId(m.getId());
			affair.setIsFinish(false);
			affair.setObjectId(edocRecieveRecord.getEdocId());
			affair.setSubObjectId(edocRecieveRecord.getId());
			affair.setSenderId(user.getId());
			affair.setState(StateEnum.edoc_exchange_receive.key());

			affairManager.addAffair(affair);
			MessageReceiver receiver_a = new MessageReceiver(affair.getId(),
					affair.getMemberId(), "message.link.exchange.receive",
					affair.getSubObjectId().toString());
			userMessageManager.sendSystemMessage(new MessageContent(key, affair
					.getSubject(), senderName), ApplicationCategoryEnum.exSign,
					user.getId(), receiver_a);
		}
	}
	
	public String queryMarkList(String modelType,long userId, long accountId) {
		List<String> markList = new ArrayList<String>();
		try {
			if(null!=modelType && !"".equals(modelType) && "sent".equals(modelType))
			{
				List<EdocSendRecord> list = this.getSendEdocs(userId, accountId, Constants.C_iStatus_Sent,"","");
				markList = this.getFormatEdocMarkSend(list);
				return markList.get(0);
			}
			
			else if(null!=modelType && !"".equals(modelType) && "toSend".equals(modelType))
			{			
				List<EdocSendRecord> list = this.getSendEdocs(userId, accountId, Constants.C_iStatus_Tosend,"","");
				markList = this.getFormatEdocMarkSend(list);
				return markList.get(0);
			}
			
			else if(null!=modelType && !"".equals(modelType) && "received".equals(modelType))
			{
				Set<Integer> statusSet = new HashSet<Integer>();
				statusSet.add(Constants.C_iStatus_Recieved);
				statusSet.add(Constants.C_iStatus_Registered);
				List<EdocRecieveRecord> list = this.getRecieveEdocs(userId, accountId, statusSet,"","");
				markList = this.getFormatEdocMarkSendRecieve(list);
				return markList.get(0);
			}
			
			else if(null!=modelType && !"".equals(modelType) && "toReceive".equals(modelType))
			{
				Set<Integer> statusSet = new HashSet<Integer>();
				statusSet.add(Constants.C_iStatus_Torecieve);
				List<EdocRecieveRecord> list = this.getRecieveEdocs(userId, accountId, statusSet,"","");
				markList = this.getFormatEdocMarkSendRecieve(list);
				return markList.get(0);
			}
		} catch (Exception e) {
			log.error("公文交换 菜单ajax 异步查询异常！");
		}
		return null ;
	}
	
	/**
     * 按指定格式得到文号(EdocSendRecord)，如：
     * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ];
     * @param list
     * @return
     */
	private List<String> getFormatEdocMarkSend(List<EdocSendRecord> list) {
		
    	List<String> resultList = new ArrayList<String>();
    	
    	// 公文文号
        String edocMark = "" ;
        if (list != null && list.size() > 0) {
        	for (int i  = 0 ; i < list.size() ; i ++) {
        		EdocSendRecord model = list.get(i) ;
            	if (Strings.isNotBlank(model.getDocMark()))
            		edocMark += "{value:'"+i+"',label:'"+model.getDocMark()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getDocMark())) 
            		edocMark += ",";
            }
            if (edocMark.length() > 1 && ",".equals(edocMark.substring(edocMark.length()-1, edocMark.length())))
            	resultList.add("["+edocMark.substring(0, edocMark.length()-1)+"]");
            else
            	resultList.add("["+edocMark+"]");
        }
    	return resultList;
    	
    }
	
	/**
     * 按指定格式得到文号(EdocRecieveRecord)，如：
     * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ];
     * @param list
     * @return
     */
	private List<String> getFormatEdocMarkSendRecieve(List<EdocRecieveRecord> list) {
		
		List<String> resultList = new ArrayList<String>();
		
		// 公文文号
        String edocMark = "" ;
        if (list != null && list.size() > 0) {
        	for (int i  = 0 ; i < list.size() ; i ++) {
        		EdocRecieveRecord model = list.get(i) ;
            	if (Strings.isNotBlank(model.getDocMark()))
            		edocMark += "{value:'"+i+"',label:'"+model.getDocMark()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getDocMark())) 
            		edocMark += ",";
            }
            if (edocMark.length() > 1 && ",".equals(edocMark.substring(edocMark.length()-1, edocMark.length())))
            	resultList.add("["+edocMark.substring(0, edocMark.length()-1)+"]");
            else
            	resultList.add("["+edocMark+"]");
        }
        return resultList;
	}
}
