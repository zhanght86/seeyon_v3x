package com.seeyon.v3x.exchange.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.edoc.domain.EdocObjTeamMember;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManagerImpl;
import com.seeyon.v3x.edoc.manager.EdocObjTeamManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.exchange.dao.EdocSendDetailDao;
import com.seeyon.v3x.exchange.dao.EdocSendRecordDao;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.exception.ExchangeException;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;


public class SendEdocManagerImpl implements SendEdocManager {
	private final static Log log = LogFactory.getLog(SendEdocManagerImpl.class);
	private EdocSendRecordDao edocSendRecordDao;
	private EdocSendDetailDao edocSendDetailDao;
	private EdocObjTeamManager edocObjTeamManager;
	
	private OrgManager orgManager;
	private AffairManager affairManager;
	private UserMessageManager userMessageManager;
	
	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public EdocSendRecordDao getEdocSendRecordDao() {
		return edocSendRecordDao;
	}
	
	public void setEdocSendRecordDao(EdocSendRecordDao edocSendRecordDao) {
		this.edocSendRecordDao = edocSendRecordDao;
	}
	
	public EdocSendDetailDao getEdocSendDetailDao() {
		return edocSendDetailDao;
	}
	
	public void setEdocSendDetailDao(EdocSendDetailDao edocSendDetailDao) {
		this.edocSendDetailDao = edocSendDetailDao;
	}
		
	public void add(){
		
	}
	
	public void create(EdocSummary edocSummary, long exchangeOrgId,
			int exchangeType, String edocMangerID) throws Exception {
		
		User user =CurrentUser.get();
		String userName = "";
		if(null!=user){
			userName = user.getName();
		}
		String key = "exchange.send";
		String keyA = "exchange.send.a";
		String keyB = "exchange.send.b";
		
		keyA=key;
		keyB=key;
		
		
		boolean bool_isunion = edocSummary.getIsunit(); //判断是否为联合发文
		
		EdocSendRecord edocSendRecord = new EdocSendRecord();
		EdocSendRecord r_second = null;
		
		if(bool_isunion){
			r_second = new EdocSendRecord();
			r_second.setIdIfNew();
			r_second.setDocMark(edocSummary.getDocMark2());
			r_second.setSubject(edocSummary.getSubjectB());
			r_second.setDocType(edocSummary.getDocType());
			r_second.setSecretLevel(edocSummary.getSecretLevel());
			r_second.setUrgentLevel(edocSummary.getUrgentLevel());
		}
		
		
		
		edocSendRecord.setIdIfNew();
		edocSendRecord.setSubject(bool_isunion?edocSummary.getSubjectA():edocSummary.getSubject());
		edocSendRecord.setDocType(edocSummary.getDocType());
		edocSendRecord.setDocMark(edocSummary.getDocMark());
		edocSendRecord.setSecretLevel(edocSummary.getSecretLevel());
		edocSendRecord.setUrgentLevel(edocSummary.getUrgentLevel());
		
		String sendUnit=edocSummary.getSendUnit();
		String sendUnit_second = null;
		
		if(sendUnit==null)
		{
			OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			V3xOrgAccount curAcc= EdocRoleHelper.getAccountById(user.getLoginAccount());
			sendUnit=curAcc.getName();
		}		
		edocSendRecord.setSendUnit(sendUnit);
		edocSendRecord.setIssuer(edocSummary.getIssuer());
		edocSendRecord.setIssueDate(edocSummary.getSigningDate());
		edocSendRecord.setCopies(edocSummary.getCopies());		
		edocSendRecord.setEdocId(edocSummary.getId());
		edocSendRecord.setExchangeOrgId(exchangeOrgId);
		edocSendRecord.setExchangeType(exchangeType);
		long l = System.currentTimeMillis();
		edocSendRecord.setCreateTime(new Timestamp(l));
		edocSendRecord.setStatus(Constants.C_iStatus_Tosend);
		
		
		if(bool_isunion){
			sendUnit_second = edocSummary.getSendUnit2();
			if(sendUnit_second == null){
				OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
				V3xOrgAccount curAcc= orgManager.getAccountByLoginName(user.getLoginName());
				sendUnit_second=curAcc.getName();				
			}
		r_second.setSendUnit(sendUnit_second);
		r_second.setIssuer(edocSummary.getIssuer());
		r_second.setIssueDate(edocSummary.getSigningDate());
		r_second.setCopies(edocSummary.getCopies2());		
		r_second.setEdocId(edocSummary.getId());
		r_second.setExchangeOrgId(exchangeOrgId);
		r_second.setExchangeType(exchangeType);
		long l_s = System.currentTimeMillis();
		r_second.setCreateTime(new Timestamp(l_s));
		r_second.setStatus(Constants.C_iStatus_Tosend);
		}
		
		
		//todo:产生details记录
		//get a list from edocSummary,it contains rec id,type
		//use list to generate sendDetail records
		//except the rec is not internal org or dept,...
		
		//List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		Set<V3xOrgMember> memberSet = new HashSet<V3xOrgMember>();
		if(exchangeType == Constants.C_iExchangeType_Org){
			memberSet = getOrgExchangeMembers(edocSummary.getOrgAccountId(),edocMangerID);
			if(Strings.isNotBlank(edocMangerID)){
				edocSendRecord.setSendUserId(Long.parseLong(edocMangerID));
			}
		}else if(exchangeType == Constants.C_iExchangeType_Dept){
			if(Strings.isNotBlank(edocMangerID)){
				memberSet = getDeptExchangeMembers(Long.parseLong(edocMangerID));
			}else{
				memberSet = getOrgExchangeMembers(edocSummary.getOrgAccountId(),edocMangerID);
			}
		}
		
		Iterator<V3xOrgMember> it = memberSet.iterator();
		while(it.hasNext()){
			if(it.next().getSecretLevel() < 3){
				it.remove();
			}
		}
				//--
		for(V3xOrgMember m:memberSet){
			Affair affair = new Affair();
			affair.setIdIfNew();
			affair.setApp(ApplicationCategoryEnum.exSend.getKey());
			affair.setSubject(edocSummary.getSubject());
			
			if(bool_isunion){affair.setSubject(edocSummary.getSubjectA());}
			if(edocSummary.getUrgentLevel() !=null && !edocSummary.getUrgentLevel().equals("")){
				affair.setImportantLevel(Integer.valueOf(edocSummary.getUrgentLevel())) ;
			}
			affair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			affair.setMemberId(m.getId());
			affair.setSenderId(user.getId());
			affair.setObjectId(edocSummary.getId());
			affair.setSubObjectId(edocSendRecord.getId());
	        affair.setState(StateEnum.edoc_exchange_send.key());
	        affair.setIsTrack(null);	
	        affairManager.addAffair(affair);
	        
	        //消息
	        MessageReceiver receiver = new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.exchange.send", affair.getSubObjectId().toString(),affair.getId());
	        Long agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(), m.getId());
	        MessageReceiver agentReceiver =  null;
	        if(agentMemberId!=null){
        		agentReceiver = new MessageReceiver(affair.getId(), agentMemberId,"message.link.exchange.send", affair.getSubObjectId().toString(),affair.getId());
	        }
	        if(bool_isunion){
	        	if(agentMemberId!=null){
	        		userMessageManager.sendSystemMessage(new MessageContent(keyA,affair.getSubject(),userName).add("col.agent"), ApplicationCategoryEnum.exSend, user.getId(), agentReceiver);	
	        	}
	        	userMessageManager.sendSystemMessage(new MessageContent(keyA,affair.getSubject(),userName), ApplicationCategoryEnum.exSend, user.getId(), receiver);
	        }else{
	        	if(agentMemberId!=null){
	        		userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),userName).add("col.agent"), ApplicationCategoryEnum.exSend, user.getId(), agentReceiver);
	        	}
	        	userMessageManager.sendSystemMessage(new MessageContent(key,affair.getSubject(),userName), ApplicationCategoryEnum.exSend, user.getId(), receiver);		        	
	        }
	        //================
	        
	        if(bool_isunion){
				Affair affair_second = new Affair();
				affair_second.setIdIfNew();
				affair_second.setApp(ApplicationCategoryEnum.exSend.getKey());
				affair_second.setSubject(edocSummary.getSubjectB());
				affair_second.setCreateDate(new Timestamp(System.currentTimeMillis()));
				affair_second.setMemberId(m.getId());
				affair_second.setSenderId(user.getId());
				affair_second.setObjectId(edocSummary.getId());
				affair_second.setSubObjectId(r_second.getId());
		        affair_second.setState(StateEnum.edoc_exchange_send.key());
		        affair_second.setIsTrack(null);	
				if(edocSummary.getUrgentLevel() !=null && !edocSummary.getUrgentLevel().equals("")){
					affair.setImportantLevel(Integer.valueOf(edocSummary.getUrgentLevel())) ;
				}
		        affairManager.addAffair(affair_second);
		        MessageReceiver receiver_second = new MessageReceiver(affair_second.getId(), affair_second.getMemberId(),"message.link.exchange.send", affair_second.getSubObjectId().toString(),affair_second.getId());
		        userMessageManager.sendSystemMessage(new MessageContent(keyB,affair_second.getSubject(),userName), ApplicationCategoryEnum.exSend, user.getId(), receiver_second);
		        if(agentMemberId!=null){
		        	userMessageManager.sendSystemMessage(new MessageContent(keyB,affair_second.getSubject(),userName).add("col.agent"), ApplicationCategoryEnum.exSend, user.getId(), agentReceiver);
		        }
	        }
	    }
		
		edocSendRecord.setContentNo(Constants.EDOC_EXCHANGE_UNION_NORMAL);
		if(edocSummary.getBody(EdocBody.EDOC_BODY_PDF_ONE)!=null){
			edocSendRecord.setContentNo(Constants.EDOC_EXCHANGE_UNION_PDF_FIRST);
		}
		if(bool_isunion){
			//第一套正文
			if(edocSummary.getBody(Constants.EDOC_EXCHANGE_UNION_PDF_FIRST)!=null){
				edocSendRecord.setContentNo(Constants.EDOC_EXCHANGE_UNION_PDF_FIRST);
			}else{
				edocSendRecord.setContentNo(Constants.EDOC_EXCHANGE_UNION_FIRST);
			}
			//第二套正文
			if(edocSummary.getBody(Constants.EDOC_EXCHANGE_UNION_PDF_SECOND)!=null){
				r_second.setContentNo(Constants.EDOC_EXCHANGE_UNION_PDF_SECOND);
			}else{
				r_second.setContentNo(Constants.EDOC_EXCHANGE_UNION_SECOND);
			}
			edocSendRecordDao.save(r_second);
		}
		edocSendRecordDao.save(edocSendRecord);

	}
	/**
	 * 得到指定单位的部门收发员
	 * @param edocSummary
	 * @return
	 * @throws Exception
	 */
	private Set<V3xOrgMember> getDeptExchangeMembers(long deptId)
			throws Exception {
		//使用Set过滤重复的人员,如果一个人既在主部门充当收发员，又在副岗所在部门充当收发员，首页只显示一条待办事项。
		Set<V3xOrgMember> memberSet =new HashSet<V3xOrgMember>();
		memberSet.addAll(EdocRoleHelper.getDepartMentExchangeUsers(orgManager.getDepartmentById(deptId).getOrgAccountId(), deptId));
		return memberSet;
	}
	
	/**
	 * 得到指定单位的公文收发员。
	 * @param orgAccountId ： 单位ID
	 * @param selectMemberId ：选择的特定的公文收发员.
	 * @return
	 */
	private Set<V3xOrgMember> getOrgExchangeMembers(long orgAccountId,String selectMemberId){
			Set<V3xOrgMember> memberList = new HashSet<V3xOrgMember>();
			try{
				if (Strings.isBlank(selectMemberId)) {
					// 没有选中特定的公文收发员，沿用发给全部的公文收发员，竞争执行
					memberList.addAll( EdocRoleHelper.getAccountExchangeUsers(orgAccountId));
				} else {
					// 选中了特定的公文收发员，只发给特定的公文收发员
					V3xOrgMember member = orgManager.getMemberById(Long.parseLong(selectMemberId));
					memberList.add(member);
				}
			}catch(Exception e){
				log.error(e);
			}
			return memberList;
	}
	/**
	 * 再次发送
	 * @param edocSendRecord
	 * @param edocSummary
	 * @param exchangeOrgId
	 * @param exchangeType
	 * @throws Exception
	 */
	public void reSend(EdocSendRecord edocSendRecord, EdocSummary edocSummary) throws Exception {
		
		String sendUnit=edocSendRecord.getSendUnit();
		User user = CurrentUser.get();
		if(sendUnit==null)
		{
			OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			V3xOrgAccount curAcc= orgManager.getAccountByLoginName(user.getLoginName());
			sendUnit=curAcc.getName();
		}		
		edocSendRecord.setSendUnit(sendUnit);
		edocSendRecordDao.save(edocSendRecord);

	}
	/**
	 * 生成待发送公文要发送的详细信息
	 * @param sendRecordId
	 * @param typeAndIds
	 */
	public List<EdocSendDetail> createSendRecord(Long sendRecordId,String typeAndIds) throws ExchangeException
	{
		List<EdocSendDetail> sdList=new ArrayList<EdocSendDetail>();
		if(typeAndIds==null || "".equals(typeAndIds)){return sdList;}
		try
		{	
			boolean haveAnotherAccount = false; //为了和选人页面的形式保持一致,定义变量区分发送的部门/单位中是否含有外单位
			V3xOrgEntity tempEntity=null;			
			String[] items = typeAndIds.split(V3xOrgEntity.ORG_ID_DELIMITER);
			String data[]=null;
			
			String strTemp;
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
						strTemp=mem.getTeamType()+"|"+mem.getMemberId();
						if(strLs.contains(strTemp)==false){strLs.add(strTemp);}
					}
				}
				else
				{
					if(strLs.contains(temp)==false){strLs.add(temp);}
				}				
			}
			items=new String[strLs.size()];
			strLs.toArray(items);
			
			haveAnotherAccount = checkHaveAnotherAccount(items); 
			
			for(String item:items)
			{	
				String shortName = "";
				V3xOrgAccount account = null;
				EdocSendDetail detail = new EdocSendDetail();
				data = item.split("[|]");
				if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(data[0]))
				{
					detail.setRecOrgType(Constants.C_iAccountType_Org);	
					tempEntity = orgManager.getEntity(item);
				}
				else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(data[0]))
				{
					detail.setRecOrgType(Constants.C_iAccountType_Dept);
					tempEntity = orgManager.getEntity(item);
				}
				else
				{//交换中心外部单位暂时不做处理
					//detail.setRecOrgType(Constants.C_iAccountType_Default);
					tempEntity=null;
				}
				if(tempEntity==null){continue;}
				if(null!=tempEntity){
					account = orgManager.getAccountById(tempEntity.getOrgAccountId());
					if(null!=account){
						if(null!=account.getShortname() && !"".equals(account.getShortname())){
							shortName = "("+account.getShortname()+")"; //将单位的简称拼接成该形式 '(xxx)'
						}
					}
				}
				detail.setIdIfNew();
				if(haveAnotherAccount && (!V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(data[0]))){//如果有外单位/部门,全部加上单位简称,反之都不加
					detail.setRecOrgName(tempEntity.getName() + shortName);					
				}else{
					detail.setRecOrgName(tempEntity.getName());							
				}
				detail.setRecOrgId(data[1]);
				detail.setSendRecordId(sendRecordId);
				detail.setSendType(Constants.C_iStatus_Send);
				detail.setStatus(Constants.C_iStatus_Torecieve);
				edocSendDetailDao.save(detail);	
				sdList.add(detail);
			}	
			EdocSendRecord es= edocSendRecordDao.get(sendRecordId);
			if(null!=es){
				// 如果是回退的公文再次发送，设置此公文的发文记录的状态为未发送
				if (es.getStatus() == Constants.C_iStatus_Send_StepBacked) {
					es.setStatus(Constants.C_iStatus_Tosend);
					// 删除原来的收文回退时留下的detail
					List<EdocSendDetail> oldEdocSendDetails = edocSendDetailDao
							.findDetailListBySendId(sendRecordId);
					String[] columns = {"id"};
					List<Long> values = new ArrayList<Long>();
					for (EdocSendDetail edocSendDetail : oldEdocSendDetails) {
						values.add(edocSendDetail.getId());
					}
					edocSendDetailDao.delete(columns, values.toArray());
				}
			es.setSendedTypeIds(typeAndIds);
			edocSendRecordDao.update(es);
			}
		}
		catch(Exception e)
		{
			throw new ExchangeException(""+e);
		}
		return sdList;
		/*
		String tempIds = edocSummary.getSendToId();
		if(tempIds==null || "".equals(tempIds)){return;}
		
		String[] sendId = tempIds.split(",");
		String[] sendName = edocSummary.getSendTo().split("、");
		for(int i=0;i<sendId.length;i++){
			V3xOrgAccount account = null;
			V3xOrgDepartment department = null;
		//	if(!sendId[i].contains("ExchangeAccount")){
				String[] str = sendId[i].split("\\|");				
				EdocSendDetail detail = new EdocSendDetail();				
				if("Department".equals(str[0])){
					detail.setRecOrgType(Constants.C_iExchangeType_Dept);
					department = orgManager.getEntityByID(V3xOrgDepartment.class, Long.valueOf(str[1]));
				}else if("Account".equals(str[0])){
					detail.setRecOrgType(Constants.C_iExchangeType_Org);	
					account = orgManager.getEntityByID(V3xOrgAccount.class, Long.valueOf(str[1]));
				}else if("ExchangeAccount".equals(str[0])){
					detail.setRecOrgType(Constants.C_iAccountType_Default);
				}
				
				if(null!=department){
			//	detail.setRecOrgName(department.getName());
				detail.setIdIfNew();
				detail.setRecOrgName(sendName[i]);
				detail.setRecOrgId(str[1]);
				detail.setSendRecordId(edocSendRecord.getId());
				detail.setSendType(Constants.C_iStatus_Send);
				detail.setStatus(Constants.C_iStatus_Torecieve);
				edocSendDetailDao.save(detail);
				}else if(null!=account){
					detail.setRecOrgName(account.getName());
					detail.setIdIfNew();
				//	detail.setRecOrgName(sendName[i]);
					detail.setRecOrgId(str[1]);
					detail.setSendRecordId(edocSendRecord.getId());
					detail.setSendType(Constants.C_iStatus_Send);
					detail.setStatus(Constants.C_iStatus_Torecieve);
					edocSendDetailDao.save(detail);					
				}
	//		}
		}
		*/
		
	}
	
	public void update(EdocSendRecord edocSendRecord) throws Exception {
		edocSendRecordDao.update(edocSendRecord);
	}
		
	public EdocSendRecord getEdocSendRecord(long id) {
		return edocSendRecordDao.get(id);
	}
	/**
	 * ajax判断发文记录中是否包含指定的单位.
	 */
	public String ajaxCheckContainSpecialUnit(Long sendRecordId,String unitIds){
		if(sendRecordId == null || Strings.isBlank(unitIds)) return "N";
		
		String info = "";
		try{
			EdocSendRecord record = getEdocSendRecord(sendRecordId);
			String[] ids = unitIds.split("[,]");
			Map<Long,String> m  = new HashMap<Long,String>();
			for(EdocSendDetail detail : record.getSendDetails()){
				if(detail.getStatus() != Constants.C_iStatus_Send_StepBacked)
					m.put(Long.valueOf(detail.getRecOrgId()),detail.getRecOrgName());
			}
			Set<Long>  s = m.keySet();
			for(String id : ids){
				Long i = Long.valueOf(id.split("[|]")[1]);
				if(s.contains(i)){
					if(Strings.isBlank(info)) info = m.get(i);
					else info +=","+m.get(i);
				}
			}
		}catch(Exception e){
			log.error(e);
		}
		return "".equals(info)?"N":info;
	}
	public List<EdocSendRecord> getEdocSendRecords(int status) {
		
		List<EdocSendRecord> list = edocSendRecordDao.getEdocSendRecords(status);
		
		 return list;
	}
	
	public List<EdocSendRecord> findEdocSendRecords(String accountIds,String departIds,int status,String condition,String value) {
		return edocSendRecordDao.findEdocSendRecords(accountIds, departIds, status,condition,value);
	}	
	public void delete(long id) throws Exception {	
//		edocSendDetailDao.delete("from EdocSendDetail where sendRecordId = " + id);
//		Session s = edocSendDetailDao.getSessionFactory().getCurrentSession();
//		s.flush();
//		Session s2 = edocSendRecordDao.getSessionFactory().getCurrentSession();
		edocSendRecordDao.delete(id);
		
		
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}	
	
	private boolean checkHaveAnotherAccount(String[] items)throws Exception{
		User user = CurrentUser.get();
		boolean haveAnotherAccount = false; //为了和选人页面的形式保持一致,定义变量区分发送的部门/单位中是否含有外单位
		try
		{	
			V3xOrgEntity tempEntity=null;			
			String data[]=null;
			for(String item:items)
			{	
				V3xOrgAccount account = null;
				data = item.split("[|]");
				if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(data[0]))
				{
					tempEntity = orgManager.getEntity(item);
				}
				else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(data[0]))
				{
					tempEntity = orgManager.getEntity(item);
				}
				else
				{//交换中心外部单位暂时不做处理
					//detail.setRecOrgType(Constants.C_iAccountType_Default);
					tempEntity=null;
				}
				if(tempEntity==null){continue;}
				
				if(null!=tempEntity){
					account = orgManager.getAccountById(tempEntity.getOrgAccountId());
					if(null!=account){
						if(null!=account.getId() && account.getId() != user.getAccountId()){
							haveAnotherAccount = true;  //如果实体的单位ID与当前交换人的ID不属于同一个单位,生效
						}
					}
				}
			}		
		}catch(Exception e)
		{
			throw e;
		}		
		return haveAnotherAccount;
	}
	
	public EdocSendRecord getEdocSendRecordByDetailId(long detailId)
	{		
		EdocSendDetail esd=edocSendDetailDao.get(detailId);
		if(esd==null){return null;}
		if(null==esd.getSendRecordId())
		{
			return null;
		}
		EdocSendRecord er=edocSendRecordDao.get(esd.getSendRecordId());		
		return er;
	}
	
	public void deleteRecordDetailById(long id)throws Exception{
		edocSendDetailDao.delete(id);
	}

	public EdocObjTeamManager getEdocObjTeamManager() {
		return edocObjTeamManager;
	}

	public void setEdocObjTeamManager(EdocObjTeamManager edocObjTeamManager) {
		this.edocObjTeamManager = edocObjTeamManager;
	}
	
	public List<EdocSendDetail> getDetailBySendId(long sendId){
		return edocSendDetailDao.findDetailListBySendId(sendId);
	}
	
	
}