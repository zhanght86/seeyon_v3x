package com.seeyon.v3x.exchange.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.flag.BrowserEnum;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.EactionType;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.manager.EdocExchangeManager;
import com.seeyon.v3x.exchange.manager.ExchangeAccountManager;
import com.seeyon.v3x.exchange.manager.SendEdocManager;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.Administrator,RoleType.AccountEdocAdmin,RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange})
public class ExchangeEdocController extends BaseController {
	
	private static final Log log = LogFactory.getLog(ExchangeEdocController.class);
	
	private ExchangeAccountManager exchangeAccountManager;
	private EdocExchangeManager edocExchangeManager;
	private SendEdocManager sendEdocManager;
	private MetadataManager metadataManager;
	private EdocSummaryManager edocSummaryManager;
	private OrgManager orgManager;
	private AffairManager affairManager;
	private UserMessageManager userMessageManager;
	private OperationlogManager operationlogManager;
	
	/**
	 * @return the sendEdocManager
	 */
	public SendEdocManager getSendEdocManager() {
		return sendEdocManager;
	}

	/**
	 * @param sendEdocManager the sendEdocManager to set
	 */
	public void setSendEdocManager(SendEdocManager sendEdocManager) {
		this.sendEdocManager = sendEdocManager;
	}

	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public EdocExchangeManager getEdocExchangeManager() {
		return edocExchangeManager;
	}

	public void setEdocExchangeManager(EdocExchangeManager edocExchangeManager) {
		this.edocExchangeManager = edocExchangeManager;
	}

	public void create(String accountId, String name, int accountType, String description, boolean isInternalAccount, long internalOrgId, long internalDeptId, long internalUserId, String exchangeServerId, int status) throws Exception {
		exchangeAccountManager.create(accountId, name, accountType, description, isInternalAccount, internalOrgId, internalDeptId, internalUserId, exchangeServerId, status);
	}

	public void create(String name, String description) throws Exception {
		exchangeAccountManager.create(name, description);
	}

	public void delete(long id) throws Exception {
		exchangeAccountManager.delete(id);
	}

	public ExchangeAccount getExchangeAccount(long id) {
		return exchangeAccountManager.getExchangeAccount(id);
	}

	public ExchangeAccount getExchangeAccountByAccountId(String accountId) {
		return exchangeAccountManager.getExchangeAccountByAccountId(accountId);
	}

	public List<ExchangeAccount> getExternalAccounts(Long domainId) {
		return exchangeAccountManager.getExternalAccounts(domainId);
	}

	public List<ExchangeAccount> getExternalOrgs(Long domainId) {
		return exchangeAccountManager.getExternalOrgs(domainId);
	}

	public List<ExchangeAccount> getInternalAccounts(Long domainId) {
		return exchangeAccountManager.getInternalAccounts(domainId);
	}

	public void update(ExchangeAccount exchangeAccount) throws Exception {
		exchangeAccountManager.update(exchangeAccount);
	}

	public ExchangeAccountManager getExchangeAccountManager() {
		return exchangeAccountManager;
	}

	public void setExchangeAccountManager(
			ExchangeAccountManager exchangeAccountManager) {
		this.exchangeAccountManager = exchangeAccountManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	/*------------------------------------ 外部单位管理 Start ---------------------------------------*/
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView addExchangeAccountPage(HttpServletRequest request,HttpServletResponse response)
		throws Exception {		
		ModelAndView mav = new ModelAndView("exchange/account/addExchangeAccount");	
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView addExchangeAccount(HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		String name = request.getParameter("name");
		String description = request.getParameter("description");
		
		Long domainId = CurrentUser.get().getLoginAccount();
		boolean flag = exchangeAccountManager.containExternalAccount(name, domainId);
		if (flag) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('ExchangeLang.outter_unit_name_used'));");
			out.println("history.go(-1);");
			out.println("</script>");
			return null;
		}
		exchangeAccountManager.create(name, description);			
		
		return super.refreshWindow("parent");
	}	
	
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView editExchangeAccountPage(HttpServletRequest request,HttpServletResponse response)
		throws Exception {
		ModelAndView mav = new ModelAndView("exchange/account/editExchangeAccount");
		Long id = Long.valueOf(request.getParameter("id"));
		ExchangeAccount account = exchangeAccountManager.getExchangeAccount(id);
		if(null != account) {
			mav.addObject("account", account);
		}
		return mav;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView updateExchangeAccount(HttpServletRequest request,HttpServletResponse response)
		throws Exception {
		PrintWriter out = response.getWriter();
		Long id = Long.valueOf(request.getParameter("id"));
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		Long domainId = CurrentUser.get().getLoginAccount();
		boolean flag = exchangeAccountManager.containExternalAccount(id, name, domainId);
		if (flag) {			
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('ExchangeLang.outter_unit_name_used'));");
			out.println("history.go(-1);");
			out.println("</script>");
			return null;
		}

		ExchangeAccount account = exchangeAccountManager.getExchangeAccount(id);
		
		account.setName(name);
		account.setDescription(description);
		exchangeAccountManager.update(account);
		out.print("<script>");
		out.print("parent.parent.location.reload(true)");
		out.print("</script>");
		return null;
	}
	
	/**
	 * 删除外部单位。
	 */
	@CheckRoleAccess(roleTypes=RoleType.Administrator)
	public ModelAndView deleteExchangeAccount(HttpServletRequest request,HttpServletResponse response)
		throws Exception {
		
		String id = request.getParameter("id");
	
		String[] ids = id.split(",");
	
		for(int i=0;i<ids.length;i++){
			exchangeAccountManager.delete(Long.valueOf(ids[i]));
		}
	
		return super.refreshWindow("parent");
	}
	
	/*------------------------------------ 外部单位管理 End ---------------------------------------*/
	//@CheckRoleAccess(roleTypes={RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange})
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView listMainEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = null;
		String modelType = request.getParameter("modelType");
		if(null==modelType || "".equals(modelType)){
			modelType = "toSend";
		}
		mav = new ModelAndView("exchange/edoc/edoc_list_mainEntry").addObject("modelType", modelType);
		return mav;
	}
	
	//@CheckRoleAccess(roleTypes={RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange,RoleType.Administrator})
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView listMain(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = null;
		
		String modelType = request.getParameter("modelType");
		
		if(null!=modelType && !"".equals(modelType)){
			
			mav = new ModelAndView("exchange/edoc/edoc_list_main");
			mav.addObject("modelType", modelType);
		
		}else{
			mav = new ModelAndView("exchange/account/account_list_main");
		}
				
		 if(null!=request.getParameter("id") && !"".equals(request.getParameter("id"))){
			mav.addObject("id", request.getParameter("id"));
		 }
		
		return mav;
	}
	
	//@CheckRoleAccess(roleTypes={RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange,RoleType.Administrator})
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response)throws Exception{
		ListSearchHelper.pickupExpression(request, null);
		
		ModelAndView mav = null;
		
		User user = CurrentUser.get();
		
		String modelType = request.getParameter("modelType");
		String condition = request.getParameter("condition");
		String value = request.getParameter("textfield");
		if ("sendUnit".equals(condition)) {
			request.setAttribute("sendUnit", value);
		} else if ("subject".equals(condition)) {
			request.setAttribute("subject", value);
		} else {
			request.setAttribute("textfield", value);
		}
		
		
		
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
		
		if(null!=modelType && !"".equals(modelType) && "sent".equals(modelType)){
			V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目
			mav = new ModelAndView("exchange/edoc/edoc_list_send_iframe");
			mav.addObject("colMetadata", colMetadata);
			List<EdocSendRecord> list = null;
			list = edocExchangeManager.getSendEdocs(user.getId(), user.getLoginAccount(), Constants.C_iStatus_Sent,condition,value,member.getSecretLevel());//成发集团项目
			
			mav.addObject("list", pagenate(list));
    		mav.addObject("exchangelabel", "exchange.edoc.sent");
    		
		}else if(null!=modelType && !"".equals(modelType) && "toSend".equals(modelType)){			
			V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目
			List<EdocSendRecord> list = null;
			list = edocExchangeManager.getSendEdocs(user.getId(), user.getLoginAccount(), Constants.C_iStatus_Tosend,condition,value,member.getSecretLevel());//成发集团项目
								
			mav = new ModelAndView("exchange/edoc/edoc_list_presend_iframe");
			
			mav.addObject("colMetadata", colMetadata);
			mav.addObject("list", pagenate(list));
    		mav.addObject("exchangelabel", "exchange.edoc.presend");	
    		
		}else if(null!=modelType && !"".equals(modelType) && "received".equals(modelType)){
			V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目
			List<EdocRecieveRecord> list = null;
			Set<Integer> statusSet = new HashSet<Integer>();
			statusSet.add(Constants.C_iStatus_Recieved);
			statusSet.add(Constants.C_iStatus_Registered);
			list = edocExchangeManager.getRecieveEdocs(user.getId(), user.getLoginAccount(), statusSet,condition,value,member.getSecretLevel());//成发集团项目
				
			/**
			 * 使用orgManager获取登记人的姓名,返回前台
			 */
			if(list != null)
			for(EdocRecieveRecord record:list){
				
				V3xOrgMember register = orgManager.getEntityById(V3xOrgMember.class, record.getRegisterUserId());
				if(register!=null){
					record.setRegisterName(register.getName());
				}else{
					record.setRegisterName("");
				}
			}
			
			mav = new ModelAndView("exchange/edoc/edoc_list_sign_iframe");
			mav.addObject("colMetadata", colMetadata);
			mav.addObject("list", pagenate(list));
		
    		mav.addObject("exchangelabel", "exchange.edoc.sign");
		}else if(null!=modelType && !"".equals(modelType) && "toReceive".equals(modelType)){
			V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目
			List<EdocRecieveRecord> list = null;
			Set<Integer> statusSet = new HashSet<Integer>();
			statusSet.add(Constants.C_iStatus_Torecieve);
			list = edocExchangeManager.getRecieveEdocs(user.getId(), user.getLoginAccount(), statusSet,condition,value,member.getSecretLevel());//成发集团项目	
			
			mav = new ModelAndView("exchange/edoc/edoc_list_presign_iframe");
			mav.addObject("colMetadata", colMetadata);
			mav.addObject("list", pagenate(list));
    		mav.addObject("exchangelabel", "exchange.edoc.presign");
    		
		}else{//单位管理员-公文应用设置-外部单位条件查询
			List<ExchangeAccount> list = null;
			String expressionType = request.getParameter("expressionType");
			String expressionValue = request.getParameter("expressionValue");
			if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
				list = exchangeAccountManager.getExternalAccountsByName(expressionValue, user.getLoginAccount());
			}else{
				list =  exchangeAccountManager.getExternalAccounts(user.getLoginAccount());
			}
			
			mav = new ModelAndView("exchange/account/account_list_iframe");
			mav.addObject("colMetadata", colMetadata);
			
			if(null!=list && list.size()>0){
				mav.addObject("list", pagenate(list));
			}
		}
		mav.addObject("condition", condition);	
		mav.addObject("modelType", modelType);
		return mav;
	}
	
//	@CheckRoleAccess(roleTypes={RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange})
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView edocDelete(HttpServletRequest request,HttpServletResponse resonse)throws Exception{
		
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		
		edocExchangeManager.deleteByType(id, type);
		//插入删除交换记录的日志
		log.info(CurrentUser.get().getName()+"刪除公文id="+id+"的"+type+"记录");
		return super.refreshWindow("parent");
	}
	
	/**
	 * 公文交换发送、签收、登记页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView change(HttpServletRequest request,HttpServletResponse response)throws Exception{
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		String reSend = request.getParameter("reSend");
		String fromlist = request.getParameter("fromlist");
		String sendKey = "exchange.sent";
		String userName = "";
		Boolean f = (Boolean)(BrowserFlag.PageBreak.getFlag(request));
		if(null!=user){
			userName = user.getName();
		}
		String id = request.getParameter("id");
		String affairId = request.getParameter("affairId");
		if(Strings.isBlank(id)){
			System.out.println("ID为空");
			return null;
		}
		
		Long agentToId = null;//被代理人ID
		String agentToName = "";
		if(Strings.isNotBlank(affairId)){
			Affair affair = affairManager.getById(Long.valueOf(affairId));
			if(!affair.getMemberId().equals(user.getId())){
				agentToId = affair.getMemberId();
				V3xOrgMember member = orgManager.getMemberById(agentToId);
				agentToName = member.getName();
			}
		}
		
		boolean isResend = false;
		String modelType = request.getParameter("modelType");
		if(null!=modelType && !"".equals(modelType) && "toSend".equals(modelType)){
			//公文交换时候,发送公文,给发送的detail表插入数据,同时给待签收表插入数据
			//String sendUserId = request.getParameter("sendUserId");
			//String sender = request.getParameter("sender");
			//读取发送时重新选择的发送单位
			String typeAndIds=request.getParameter("grantedDepartId");
			
			EdocSendRecord record = edocExchangeManager.getSendRecordById(Long.valueOf(id));
			
			if(!Strings.isBlank(reSend) && reSend.equals("true")){
				isResend = true;
				EdocSendRecord reRecord = new EdocSendRecord();
				reRecord.setIdIfNew();
				reRecord.setContentNo(record.getContentNo());
				reRecord.setCopies(record.getCopies());
				reRecord.setCreateTime(record.getCreateTime());
				reRecord.setDocMark(record.getDocMark());
				reRecord.setDocType(record.getDocType());
				reRecord.setEdocId(record.getEdocId());
				reRecord.setExchangeOrgId(record.getExchangeOrgId());
				reRecord.setExchangeOrgName(record.getExchangeOrgName());
				reRecord.setExchangeType(record.getExchangeType());
				reRecord.setIssueDate(record.getIssueDate());
				reRecord.setIssuer(record.getIssuer());
				reRecord.setSecretLevel(record.getSecretLevel());
				reRecord.setSendDetailList(new ArrayList());
				reRecord.setSendDetails(new HashSet());
				reRecord.setSendNames(userName);
				reRecord.setSendTime(new Timestamp(System.currentTimeMillis()));
				reRecord.setSendUnit(record.getSendUnit());
				reRecord.setSendUserId(user.getId());
				reRecord.setSendUserNames(user.getName());
				reRecord.setStatus(record.getStatus());
				reRecord.setSubject(record.getSubject());
				reRecord.setUrgentLevel(record.getUrgentLevel());
				reRecord.setSendedTypeIds(typeAndIds);
				
				record = reRecord; //对象指向新对象
				sendKey = "exchange.resend";
				modelType = "sent";
			}else{
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
			String alertNote = "";
				if (null != record
						&& !(record.getStatus() == Constants.C_iStatus_Tosend || record
								.getStatus() == Constants.C_iStatus_Send_StepBacked)) {
				alertNote = ResourceBundleUtil.getString(r, "exchange.sendRecord.send.already");
				try{
						out.println("<script>");
						out.println("alert('"+alertNote+"');");
						out.println("if(window.dialogArguments){window.returnValue='true';window.close();}else{");
						out.println("parent.parent.location.reload(true)");
						out.println("}");
						//out.println("history.go(-1);");
						out.println("</script>");
						return null;
					}catch(Exception e){
						
					}			
			}
			}
			
			List <EdocSendDetail> details=edocExchangeManager.createSendRecord(record.getId(), typeAndIds);
			
			// 如果是回退的公文再次发送，设置此公文的发文记录的状态为未发送
			if (record.getStatus() == Constants.C_iStatus_Send_StepBacked) {
				record.setStatus(Constants.C_iStatus_Tosend);
			}
			List<EdocSendDetail> tempDetail=sendEdocManager.getDetailBySendId(record.getId());
			for (EdocSendDetail edocSendDetail : tempDetail) {//将已经签收的记录增加到detail中
				if(edocSendDetail.getStatus()!=0){
					details.add(edocSendDetail);
				}
			}
			Set<EdocSendDetail> sendDetails = new HashSet<EdocSendDetail>(details);
			record.setSendDetails(sendDetails);
			edocExchangeManager.sendEdoc(record, user.getId() ,user.getName(),agentToId,isResend,tempDetail);
			//affair = affairManager.getBySubObject(ApplicationCategoryEnum.exSend, record.getId());

			Map conditions = new HashMap();
			conditions.put("app", ApplicationCategoryEnum.exSend.key());
            conditions.put("objectId", record.getEdocId());
            conditions.put("subObjectId", record.getId());
            List<Affair> affairList =affairManager.getByConditions(conditions);
        	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
			if(null!=affairList && affairList.size()>0){
				for(Affair af: affairList){
            		if(af.getMemberId().longValue() != user.getId() && (af.getIsDelete()!= true)){
            			receivers.add(new MessageReceiver(af.getId(), af.getMemberId()));
            		}
					af.setState(StateEnum.edoc_exchange_sent.getKey());
					af.setIsDelete(true);
					af.setIsFinish(true);
					affairManager.updateAffair(af);
				}
			}
        	if(null!=receivers && receivers.size()>0){
        		if(agentToId!=null){
        			userMessageManager.sendSystemMessage(new MessageContent(sendKey, affairList.get(0).getSubject(), agentToName,affairList.get(0).getApp()).add("edoc.agent.deal", user.getName()), ApplicationCategoryEnum.edocSend,agentToId, receivers);
        		}else{
        			userMessageManager.sendSystemMessage(new MessageContent(sendKey, affairList.get(0).getSubject(), userName,affairList.get(0).getApp()), ApplicationCategoryEnum.edocSend, user.getId(), receivers);
        		}
        	}
        	if(isResend){
        		operationlogManager.insertOplog(record.getId(), ApplicationCategoryEnum.exchange, EactionType.LOG_EXCHANGE_RDSEND, EactionType.LOG_EXCHANGE_RDSENDD_DESCRIPTION,user.getName(), record.getSubject());
        	}else{
        		operationlogManager.insertOplog(record.getId(), ApplicationCategoryEnum.exchange, EactionType.LOG_EXCHANGE_SEND, EactionType.LOG_EXCHANGE_SEND_DESCRIPTION,user.getName(), record.getSubject());        		
        	}
		}else if(null!=modelType && !"".equals(modelType) && "toReceive".equals(modelType)){
			//签收公文,签收时候,更新发送状态;
			
			String recUserId = request.getParameter("recUserId");
			String registerUserId = request.getParameter("registerUserId");
			String recNo = request.getParameter("recNo");
			String remark = request.getParameter("remark");
			String keepperiod = request.getParameter("keepperiod");
			EdocRecieveRecord record = edocExchangeManager.getReceivedRecord(Long.valueOf(id));
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
			String alertNote = "";
			if(null!=record && record.getStatus() != Constants.C_iStatus_Torecieve){
				alertNote = ResourceBundleUtil.getString(r, "exchange.receiveRecord.receive.already");
				try{
						out.println("<script>");
						out.println("alert('"+alertNote+"');");
						out.println("if(window.dialogArguments){window.returnValue='true';window.close();}else{");
						out.println("parent.parent.location.reload(true)");
						out.println("}");
						//out.println("history.go(-1);");
						out.println("</script>");
						return null;
					}catch(Exception e){
						
					}			
			}else if(null==record){
				alertNote = ResourceBundleUtil.getString(r, "exchange.send.withdrawed");
				out.println("<script>");
				out.println("alert('"+alertNote+"');");
				out.println("if(window.dialogArguments){");
				out.println("window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				//out.println("history.go(-1);");
				out.println("</script>");		
				return null;
			}
			edocExchangeManager.recEdoc(Long.valueOf(id), Long.valueOf(recUserId), Long.valueOf(registerUserId), recNo, remark,keepperiod,agentToId);
			//affair = affairManager.getBySubObject(ApplicationCategoryEnum.exSign, record.getId());
			
			Map conditions = new HashMap();
			conditions.put("app", ApplicationCategoryEnum.exSign.key());
            conditions.put("objectId", record.getEdocId());
            conditions.put("subObjectId", record.getId());
            List<Affair> affairList =affairManager.getByConditions(conditions);   
            
            if(null!=affairList && affairList.size()>0){
            	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
            	for(Affair af: affairList){
            		if(af.getMemberId().longValue() != user.getId() && (af.getIsDelete()!= true)){
            			receivers.add(new MessageReceiver(af.getId(), af.getMemberId()));
            		}
            		af.setState(StateEnum.edoc_exchange_received.getKey());
            		//af.setSubObjectId(Long.valueOf(id));
            		af.setIsFinish(true);
            		af.setIsDelete(true);
            		affairManager.updateAffair(af);
            	}
    	        /*
    	         *发消息给其他公文收发员，公文已签收 
    	         */
            	if(null!=receivers && receivers.size()>0){
            		if(agentToId != null){
            			userMessageManager.sendSystemMessage(new MessageContent("exchange.signed", affairList.get(0).getSubject(), agentToName,affairList.get(0).getApp()).add("edoc.agent.deal", user.getName()), ApplicationCategoryEnum.edocRec, agentToId, receivers);
            		}else{
            			userMessageManager.sendSystemMessage(new MessageContent("exchange.signed", affairList.get(0).getSubject(), userName,affairList.get(0).getApp()), ApplicationCategoryEnum.edocRec, user.getId(), receivers);
            		}
            	}
            }

			Affair reAffair = new Affair();  // 登记人代办事项
			reAffair.setIdIfNew();
			reAffair.setIdIfNew();
			reAffair.setApp(ApplicationCategoryEnum.edocRegister.getKey());
			reAffair.setSubject(record.getSubject());
			reAffair.setCreateDate(new Timestamp(System.currentTimeMillis()));
			reAffair.setMemberId(record.getRegisterUserId());
			reAffair.setSenderId(user.getId());
			reAffair.setObjectId(record.getEdocId());
			reAffair.setSubObjectId(record.getId());
			reAffair.setSenderId(user.getId());
			reAffair.setState(StateEnum.edoc_exchange_register.getKey());
			reAffair.setIsTrack(null);	
			if(record.getUrgentLevel()!=null && !"".equals(record.getUrgentLevel()))
				reAffair.setImportantLevel(Integer.parseInt(record.getUrgentLevel()));
	        affairManager.addAffair(reAffair);

	        operationlogManager.insertOplog(record.getId(), ApplicationCategoryEnum.exchange, EactionType.LOG_EXCHANGE_REC, EactionType.LOG_EXCHANGE_RECD_DESCRIPTION, user.getName(), record.getSubject());
	        
	        /*
	         * 发消息给待登记人
	         */
			sendMessageToRegister(user, agentToId, reAffair);
		}
		
		out.println("<script>");
		if(BrowserEnum.Safari == BrowserEnum.valueOf(request)){
    		out.println("  try{parent.getA8Top().opener.getA8Top().reFlesh();}catch(e){}");
    	}
		if (f.booleanValue()) {
			out.println("parent.doEndSign('" + modelType + "');");
		} else {
			out.println("window.top.close();");
		}
		out.println("</script>");
		
		return null;
	}

	private void sendMessageToRegister(User user, Long agentToId, Affair reAffair) throws MessageException {
		String key = "edoc.register";
		String userName = user.getName();
		//代理
		if(agentToId!=null){
			String agentToName= "";
			try{
				agentToName = orgManager.getMemberById(agentToId).getName();
			}catch(Exception e){
				log.error(e);
			}
			MessageReceiver receiver = new MessageReceiver(reAffair.getId(), reAffair.getMemberId(),"message.link.exchange.register.pending",com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href, String.valueOf(EdocEnum.edocType.recEdoc.ordinal()),reAffair.getSubObjectId().toString(),reAffair.getObjectId().toString());
			userMessageManager.sendSystemMessage(new MessageContent(key,reAffair.getSubject(),agentToName).add("edoc.agent.deal", user.getName()), 
					ApplicationCategoryEnum.edocRegister, agentToId, receiver);
			
			Long agentMemberId =  MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(), reAffair.getMemberId()); 
			if(agentMemberId != null){
				MessageReceiver agentReceiver = new MessageReceiver(reAffair.getId(), agentMemberId,"message.link.exchange.register.pending",com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href, String.valueOf(EdocEnum.edocType.recEdoc.ordinal()),reAffair.getSubObjectId().toString(),reAffair.getObjectId().toString());
				userMessageManager.sendSystemMessage(new MessageContent(key,reAffair.getSubject(),agentToName).add("edoc.agent.deal", user.getName()).add("col.agent")
						, ApplicationCategoryEnum.edocRegister, agentToId, agentReceiver);
			}
		}else{
			//非代理
			MessageReceiver receiver = new MessageReceiver(reAffair.getId(), reAffair.getMemberId(),"message.link.exchange.register.pending",com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href, String.valueOf(EdocEnum.edocType.recEdoc.ordinal()),reAffair.getSubObjectId().toString(),reAffair.getObjectId().toString());
			userMessageManager.sendSystemMessage(new MessageContent(key,reAffair.getSubject(),userName)
				, ApplicationCategoryEnum.edocRegister, user.getId(), receiver);
			
			Long agentMemberId =  MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(), reAffair.getMemberId()); 
			if(agentMemberId != null){
				MessageReceiver agentReceiver = new MessageReceiver(reAffair.getId(), agentMemberId,"message.link.exchange.register.pending",com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href, String.valueOf(EdocEnum.edocType.recEdoc.ordinal()),reAffair.getSubObjectId().toString(),reAffair.getObjectId().toString());
				userMessageManager.sendSystemMessage(new MessageContent(key,reAffair.getSubject(),userName).add("col.agent")
						, ApplicationCategoryEnum.edocRegister, user.getId(), agentReceiver);
			}
		}
		
	}	
	
	/**
	 * 公文交换列表页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = null; 

		
		String id = request.getParameter("id");
		String modelType = request.getParameter("modelType");
		
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
		Map<String, Metadata> exMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.exchange);
		EdocSummary summary = null;
		String affairId = request.getParameter("affairId");
		if(null!=modelType && !"".equals(modelType) && "sent".equals(modelType)){
			mav = new ModelAndView("exchange/edoc/edoc_list_modify_send");
			EdocSendRecord bean = edocExchangeManager.getSendRecordById(Long.valueOf(id));
			
			// 对EdocSendRecord的sendDetail进行排序
			/*
			Set<EdocSendDetail> sendDetails = bean.getSendDetails();
			List<EdocSendDetail> sendDetailList = new ArrayList<EdocSendDetail>();
			sendDetailList.addAll(sendDetails);
			Collections.sort(sendDetailList);
			*/
			List<EdocSendDetail> sendDetailList = sendEdocManager.getDetailBySendId(bean.getId());
			bean.setSendDetailList(sendDetailList);
			
			// excchangeOrgName
			String exName = "";
			int exType = bean.getExchangeType();
			long exId = bean.getExchangeOrgId();
			if(exType == Constants.C_iExchangeType_Dept){
				V3xOrgDepartment dept = orgManager.getDepartmentById(exId);
				if(dept != null)
					exName = dept.getName();
			}else if(exType == Constants.C_iExchangeType_Org){
				V3xOrgAccount account = orgManager.getAccountById(exId);
				if(account != null)
					exName = account.getName();
			}else if(exType == Constants.C_iExchangeType_ExternalOrg){
				ExchangeAccount exA = exchangeAccountManager.getExchangeAccount(exId);
				if(exA != null)
					exName = exA.getName();
			}
			bean.setExchangeOrgName(exName);
			
			summary = edocSummaryManager.findById(bean.getEdocId());
			mav.addObject("summary", summary);
			mav.addObject("elements", bean.getSendedTypeIds());
			if(bean.getSendedTypeIds()==null || "".equals(bean.getSendedTypeIds()))
			{
				mav.addObject("sendEntityName",bean.getSendEntityNames());
			}
			else
			{
				mav.addObject("sendEntityName",null);
			}
			if(null!=summary){
				bean.setKeywords(summary.getKeywords());
			}
			
			long undertakerId = bean.getSendUserId();
			V3xOrgMember member = orgManager.getMemberById(undertakerId);
			if(null!=member){
				bean.setSendUserNames(member.getName());
			}else{
				bean.setSendUserNames("");
			}
			if(summary != null){
					Integer currentNo = bean.getContentNo();
					if(null!=currentNo && (currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_FIRST 
						|| currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_NORMAL
						|| currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_PDF_FIRST)){
						bean.setSendNames(summary.getSendTo());
					}else if(null!=currentNo && (currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_SECOND
							||currentNo.intValue() == Constants.EDOC_EXCHANGE_UNION_PDF_SECOND)){
						bean.setSendNames(summary.getSendTo2());
					}
				}			
			
			mav.addObject("bean",bean);
		}else if(null!=modelType && !"".equals(modelType) && "toSend".equals(modelType)){
			mav = new ModelAndView("exchange/edoc/edoc_list_modify_send");
			EdocSendRecord bean = edocExchangeManager.getSendRecordById(Long.valueOf(id));

			if (bean.getStatus() == 2) {
				// 回退到待发送的公文
				List<EdocSendDetail> sendDetailList = sendEdocManager
						.getDetailBySendId(bean.getId());
				if (sendDetailList != null && sendDetailList.size() > 0) {
					bean.setSendDetailList(sendDetailList);
				}
				// excchangeOrgName
				String exName = "";
				int exType = bean.getExchangeType();
				long exId = bean.getExchangeOrgId();
				if (exType == Constants.C_iExchangeType_Dept) {
					V3xOrgDepartment dept = orgManager.getDepartmentById(exId);
					if (dept != null)
						exName = dept.getName();
				} else if (exType == Constants.C_iExchangeType_Org) {
					V3xOrgAccount account = orgManager.getAccountById(exId);
					if (account != null)
						exName = account.getName();
				} else if (exType == Constants.C_iExchangeType_ExternalOrg) {
					ExchangeAccount exA = exchangeAccountManager
							.getExchangeAccount(exId);
					if (exA != null)
						exName = exA.getName();
				}
				bean.setExchangeOrgName(exName);
			}

			summary = edocSummaryManager.findById(bean.getEdocId());
			mav.addObject("summary", summary);
			int isunion = bean.getContentNo();
			if(isunion == Constants.EDOC_EXCHANGE_UNION_FIRST 
				|| isunion == Constants.EDOC_EXCHANGE_UNION_NORMAL
				|| isunion == Constants.EDOC_EXCHANGE_UNION_PDF_FIRST){
				mav.addObject("elements", Strings.joinDelNull(V3xOrgEntity.ORG_ID_DELIMITER,summary.getSendToId(),summary.getCopyToId(),summary.getReportToId()));				
			}else if(isunion == Constants.EDOC_EXCHANGE_UNION_SECOND
				||isunion == Constants.EDOC_EXCHANGE_UNION_PDF_SECOND){
				mav.addObject("elements", Strings.joinDelNull(V3xOrgEntity.ORG_ID_DELIMITER,summary.getSendToId2(),summary.getCopyToId2(),summary.getReportToId2()));
			}
			if(null!=summary){
				bean.setKeywords(summary.getKeywords());
			}
			mav.addObject("bean",bean);
		}else if(null!=modelType && !"".equals(modelType) && "received".equals(modelType)){
			mav = new ModelAndView("exchange/edoc/edoc_list_modify_receive");
			EdocRecieveRecord bean = edocExchangeManager.getReceivedRecord(Long.valueOf(id));
			mav.addObject("isBeRegistered", bean.getStatus());
			mav.addObject("edocRecieveRecordID4ChgRegUser", id);
			EdocRecieveRecord newBean =  (EdocRecieveRecord)bean.clone();
			newBean.setRemark(Strings.escapeJavascript(bean.getRemark()));
			newBean.setRecNo(Strings.escapeJavascript(bean.getRecNo()));
			V3xOrgMember register = orgManager.getEntityById(V3xOrgMember.class, bean.getRegisterUserId());
			V3xOrgMember recUser = orgManager.getEntityById(V3xOrgMember.class, bean.getRecUserId());
			summary = edocSummaryManager.findById(bean.getEdocId());
			mav.addObject("summary", summary);
			mav.addObject("bean",newBean);
			mav.addObject("registerName",null==register ? "" : register.getName());
			mav.addObject("recUser", null==recUser ? "" : recUser.getName());
			String signedName = this.getSignedDep(bean);
			mav.addObject("signedName", signedName);
			EdocSendRecord beanSend = edocExchangeManager.getEdocSendRecordByDetailId(Long.parseLong(bean.getReplyId()));
			if(beanSend!=null)
			{	
			if(beanSend.getSendedTypeIds()==null || "".equals(beanSend.getSendedTypeIds()))
			{
			
			String sendEntityName = "";
			if(null==beanSend){
				long exchangeOrgId = bean.getExchangeOrgId();
				int exchangeType = bean.getExchangeType();
				V3xOrgEntity entity = null;
				if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept){
					entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, exchangeOrgId);
				}else if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Org){
					entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, exchangeOrgId);
				}
				
				if(null!=entity){
					sendEntityName = entity.getName();
					}	
			}else{
				sendEntityName = beanSend.getSendEntityNames();
			}
			
			mav.addObject("sendEntityName", sendEntityName);
			
			}
			}
			else
			{
				mav.addObject("sendEntityName", bean.getSendTo());
			}
			
			if(beanSend!=null){mav.addObject("elements", beanSend.getSendedTypeIds());}
			//获取签收单位或者签收部门所在的单位。
			mav.addObject("exchangeAccountId",getAccountIdOfRegisterByOrgIdAndOrgType(bean.getExchangeOrgId(),bean.getExchangeType()));	
		}else if(null!=modelType && !"".equals(modelType) && "toReceive".equals(modelType)){
			mav = new ModelAndView("exchange/edoc/edoc_list_modify_receive");
			User user = CurrentUser.get();
			EdocRecieveRecord bean = edocExchangeManager.getReceivedRecord(Long.valueOf(id));
			if(bean==null)
			{
				ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
				String infoMsg=ResourceBundleUtil.getString(r, "exchange.send.withdrawed");
				super.infoCloseOrFresh(request, response, infoMsg);
				return null;
			}
			if(bean.getStatus()!=Constants.C_iStatus_Torecieve)
			{
				ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
				String infoMsg=ResourceBundleUtil.getString(r, "exchange.send.hasSign");
				super.infoCloseOrFresh(request, response, infoMsg);
				return null;
			}
			summary = edocSummaryManager.findById(bean.getEdocId());
			mav.addObject("summary", summary);
			bean.setRecUser(user.getName());
			long l = System.currentTimeMillis();
			bean.setRecTime(new Timestamp(l));
			V3xOrgDepartment department = orgManager.getEntityById(V3xOrgDepartment.class, user.getDepartmentId());
			bean.setRecAccountName(department.getName());
			//bean.setCopies(summary.getCopies());
			mav.addObject("bean",bean);
			String signedName = this.getSignedDep(bean);
			mav.addObject("signedName", signedName);
			EdocSendRecord beanSend=null;
			if(null!=bean.getReplyId()&&bean.getFromInternal())
			{
				beanSend = edocExchangeManager.getEdocSendRecordByDetailId(Long.parseLong(bean.getReplyId()));
			}
			
			if(null!=beanSend){
			if(beanSend.getSendedTypeIds()==null || "".equals(beanSend.getSendedTypeIds()))
			{
			String sendEntityName = "";
			if(null==beanSend){
				long exchangeOrgId = bean.getExchangeOrgId();
				int exchangeType = bean.getExchangeType();
				V3xOrgEntity entity = null;
				if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept){
					entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, exchangeOrgId);
				}else if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Org){
					entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, exchangeOrgId);
				}
				
				if(null!=entity){
					sendEntityName = entity.getName();
					}	
			}else{
				sendEntityName = beanSend.getSendEntityNames();
			}
			
			mav.addObject("sendEntityName", sendEntityName);
			}
			mav.addObject("elements", beanSend.getSendedTypeIds());
			}
			else
			{
				mav.addObject("sendEntityName", bean.getSendTo());
				mav.addObject("elements", "");
			}
			//获取签收单位或者签收部门所在的单位。
			mav.addObject("exchangeAccountId",getAccountIdOfRegisterByOrgIdAndOrgType(bean.getExchangeOrgId(),bean.getExchangeType()));
		}
		
		/*
		String toNames = "";
		if(summary != null){
			if(summary.getSendTo() != null && !summary.getSendTo().trim().equals(""))
				toNames += summary.getSendTo();
			if(summary.getReportTo() != null && !summary.getReportTo().trim().equals(""))
				toNames += "," + summary.getReportTo();
			if(summary.getCopyTo() != null && !summary.getCopyTo().trim().equals(""))
				toNames += "," + summary.getCopyTo();
		}
		mav.addObject("toNames", toNames);
		*/
		
		if(null!=modelType && !"".equals(modelType)){
			mav.addObject("modelType", modelType);
			mav.addObject("colMetadata", colMetadata);
			mav.addObject("exMetadata", exMetadata);
		}
		mav.addObject("affairId", Strings.isBlank(affairId) ? "" : affairId);
		mav.addObject("operType", "change");
		return mav;
	}
	   /**
	    * 查找签收部门所属单位或者签收单位
	    * @param exchangeOrgId 签收ID（单位ID|部门ID）
	    * @param exchangeOrgType  签收类型（部门|单位）
	    * @return
	    */
	   private Long getAccountIdOfRegisterByOrgIdAndOrgType(Long exchangeOrgId,int exchangeOrgType){
	   	if(com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept==exchangeOrgType){
				V3xOrgDepartment dept;
				try {
					dept = orgManager.getDepartmentById(exchangeOrgId);
					return dept.getOrgAccountId();
				} catch (BusinessException e) {
					log.error("查找部门异常:",e);
				}
			}else {
				return exchangeOrgId;	
			}
	   	return 0L;
	   }
	private String getSignedDep(EdocRecieveRecord bean ){
		long exchangeOrgId = bean.getExchangeOrgId();
		int exchangeType = bean.getExchangeType();
		V3xOrgEntity entity = null;
		try{
		if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept){
			entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, exchangeOrgId);
		}else if(exchangeType == com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Org){
			entity=orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, exchangeOrgId);
		}
		}catch(Exception e){
			log.error("得到交换单位异常 : ", e);
		}
		if(null!=entity){
			return entity.getName();
		}		
		return "";
	}
	
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView sendDetail(HttpServletRequest request,HttpServletResponse response){
		String id = request.getParameter("id");
		String modelType = request.getParameter("modelType");
		String reSend = request.getParameter("reSend");
		String affairId =request.getParameter("affairId");
		ModelAndView mav = new ModelAndView("exchange/edoc/show_detail_modify");
		mav.addObject("modelType", modelType);
		mav.addObject("id", id);
		mav.addObject("reSend", reSend);
		mav.addObject("affairId", affairId);
		if(!Strings.isBlank(reSend) && reSend.equals("true")){
			return mav;
		}
		EdocSendRecord record = edocExchangeManager.getSendRecordById(Long
				.valueOf(id));
		// 状态不是待发或者回退
		if (null != record
				&& !(record.getStatus() == Constants.C_iStatus_Tosend || record
						.getStatus() == Constants.C_iStatus_Send_StepBacked)) {
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
			String alertNote = ResourceBundleUtil.getString(r, "exchange.sendRecord.send.already");
			mav.addObject("error", "ExchangeLang.exchange_sendRecord_send_already");
			try{
				PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('"+alertNote+"');");
					out.println("if(window.dialogArguments){");
					out.println("window.returnValue='true';window.close();}else{");
					out.println("parent.parent.location.reload(true)");
					out.println("}");
					//out.println("history.go(-1);");
					out.println("</script>");
					return null;
				}catch(Exception e){
					
				}	
		} else if (null == record
				|| record.getStatus() == Constants.C_iStatus_Receive_StepBacked) {
			// 因为取回封发的公文时，就把这条发文记录删除了，所以去发文表里查为空
			ResourceBundle r = ResourceBundle.getBundle(
					"com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",
					CurrentUser.get().getLocale());
			String alertNote = ResourceBundleUtil.getString(r,
					"exchange.stepBackRecord.takeBack.already");
			mav.addObject("error", "exchange_stepBackRecord_takeBack_already");
			try {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('" + alertNote + "');");
				out.println("if(window.dialogArguments){");
				out.println("window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				// out.println("history.go(-1);");
				out.println("</script>");
				return null;
			} catch (Exception e) {

			}
		}
		return mav;
	}

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView receiveDetail(HttpServletRequest request,HttpServletResponse response)throws Exception{
		PrintWriter out = response.getWriter();
		String id = request.getParameter("id");
		String modelType = request.getParameter("modelType");
		ModelAndView mav = new ModelAndView("exchange/edoc/show_detail_modify");
		mav.addObject("id", id);
		EdocRecieveRecord record = edocExchangeManager.getReceivedRecord(Long.valueOf(id));
		String affairId = request.getParameter("affairId");
		mav.addObject("affairId", affairId);
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",CurrentUser.get().getLocale());
		String alertNote = "";
		if (null != record
				&& record.getStatus() != Constants.C_iStatus_Torecieve
				&& record.getStatus() != Constants.C_iStatus_Receive_StepBacked) {
			alertNote = ResourceBundleUtil.getString(r, "exchange.receiveRecord.receive.already");
			mav.addObject("error", "ExchangeLang.exchange_receiveRecord_receive_already");	
			try{
					out.println("<script>");
					out.println("alert('"+alertNote+"');");
					out.println("if(window.dialogArguments){");
					out.println("window.returnValue='true';window.close();}else{");
					out.println("parent.parent.location.reload(true)");
					out.println("}");
					//out.println("history.go(-1);");
					out.println("</script>");
					return null;
				}catch(Exception e){
				}
		} else if (null != record
				&& record.getStatus() == Constants.C_iStatus_Receive_StepBacked) {
			// 已回退
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.stepBackRecord.stepBack.already");
			mav.addObject("error",
					"ExchangeLang.exchange_stepBackRecord_stepBack_already");
			try {
				out.println("<script>");
				out.println("alert('" + alertNote + "');");
				out.println("if(window.dialogArguments){");
				out.println("window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				// out.println("history.go(-1);");
				out.println("</script>");
				return null;
			} catch (Exception e) {
			}
		}

		else if (null == record) {
			alertNote = ResourceBundleUtil.getString(r, "exchange.send.withdrawed");
			out.println("<script>");
			out.println("alert('"+alertNote+"');");
			out.println("if(window.dialogArguments){");
			out.println("window.returnValue='true';window.close();}else{");
			out.println("parent.parent.location.reload(true)");
			out.println("}");
			//out.println("history.go(-1);");
			out.println("</script>");		
			return null;			
		}
		mav.addObject("modelType", modelType);
		return mav;
	}
	
	/**
	 * 提供公文增加用户的页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    @CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView addExchangeAccountFromEodc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"exchange/account/addExternalAccountFromEdocDlg");
		mav.addObject("domainId", CurrentUser.get().getLoginAccount());
		mav.addObject("currentMemberId", CurrentUser.get().getId());
		return mav;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if(first>=list.size()) return Collections.emptyList();
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView openStepBackDlg(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("exchange/edoc/stepBackInfo");
		String exchangeSendEdocId = request.getParameter("exchangeSendEdocId");
		String readOnlysString = request.getParameter("readOnly");
		if (!Strings.isBlank(readOnlysString) && "1".equals(readOnlysString)) {
			EdocSendRecord edocSendRecord = edocExchangeManager
					.getSendRecordById(Long
					.valueOf(Long.parseLong(exchangeSendEdocId)));
			mav.addObject("stepBackSendEdocId", exchangeSendEdocId);
			mav.addObject("readOnly", readOnlysString);
			mav.addObject("stepBackInfo", edocSendRecord.getStepBackInfo());
		} else {
			EdocRecieveRecord edocRecieveRecord = edocExchangeManager
					.getReceivedRecord(Long.valueOf(Long
							.parseLong(exchangeSendEdocId)));
			mav.addObject("stepBackSendEdocId", exchangeSendEdocId);
			mav.addObject("stepBackEdocId", edocRecieveRecord.getEdocId());
			mav.addObject("readOnly", readOnlysString);
		}
		mav.addObject("isResgistering", "0");
		return mav;
	}

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView openStepBackDlg4Resgistering(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView("exchange/edoc/stepBackInfo");
		String resgisteringEdocId = request.getParameter("resgisteringEdocId");
		String readOnlysString = request.getParameter("readOnly");
		EdocRecieveRecord edocRecieveRecord = edocExchangeManager
				.getReceivedRecord(Long.valueOf(Long
						.parseLong(resgisteringEdocId)));
		mav.addObject("stepBackSendEdocId", resgisteringEdocId);
		if (!Strings.isBlank(readOnlysString) && "1".equals(readOnlysString)) {
			mav.addObject("readOnly", readOnlysString);
			mav.addObject("stepBackInfo", edocRecieveRecord.getStepBackInfo());
		} else {
			mav.addObject("stepBackSendEdocId", resgisteringEdocId);
			mav.addObject("stepBackInfo", edocRecieveRecord.getStepBackInfo());
			mav.addObject("readOnly", readOnlysString);
		}
		mav.addObject("isResgistering", "1");
		return mav;
	}

	/**
	 * 回退
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView stepBack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		String userName = "";
		if (null != user) {
			userName = user.getName();
		}
		String stepBackSendEdocId = request.getParameter("stepBackSendEdocId");
		if (Strings.isBlank(stepBackSendEdocId)) {
			log.error("ID为空");
			return null;
		}
		EdocRecieveRecord record = edocExchangeManager.getReceivedRecord(Long
				.valueOf(Long.parseLong(stepBackSendEdocId)));
		ResourceBundle r = ResourceBundle.getBundle(
				"com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",
				CurrentUser.get().getLocale());
		String alertNote = "";
		if (null != record
				&& record.getStatus() != Constants.C_iStatus_Torecieve) {
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.receiveRecord.receive.already");
			try {
				out.println("<script>");
				out.println("alert('" + alertNote + "');");
				out
						.println("if(window.dialogArguments){window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				// out.println("history.go(-1);");
				out.println("</script>");
				return null;
			} catch (Exception e) {

			}
		} else if (null == record) {
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.send.withdrawed");
			out.println("<script>");
			out.println("alert('" + alertNote + "');");
			out.println("if(window.dialogArguments){");
			out.println("window.returnValue='true';window.close();}else{");
			out.println("parent.parent.location.reload(true)");
			out.println("}");
			// out.println("history.go(-1);");
			out.println("</script>");
			return null;
		}
		String stepBackInfo = request.getParameter("stepBackInfo");
		String stepBackEdocIdString = request.getParameter("stepBackEdocId");

		boolean isRelieveLock = true;
		try {
			// 检查同步锁
			if (!ColHelper.colOperationLock(Long.valueOf(Long
					.parseLong(stepBackSendEdocId)), user.getId(), user
					.getName(), ColLock.COL_ACTION.tackback, response,
					ApplicationCategoryEnum.edoc)) {
				isRelieveLock = false;
				return null;
			}

		EdocSummary stepBackEdocSummary = edocSummaryManager.findById(Long
				.valueOf(Long.parseLong(stepBackEdocIdString)));
		edocExchangeManager.stepBackEdoc(Long.valueOf(Long
				.parseLong(stepBackSendEdocId)),
				stepBackInfo, user.getId(), userName, stepBackEdocSummary);

		Map conditions = new HashMap();
		conditions.put("app", ApplicationCategoryEnum.exSign.key());
		conditions.put("objectId", record.getEdocId());
		conditions.put("subObjectId", record.getId());
		List<Affair> affairList = affairManager.getByConditions(conditions);

		if (null != affairList && affairList.size() > 0) {
			List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
			for (Affair af : affairList) {
				if (af.getMemberId().longValue() != user.getId()
						&& (af.getIsDelete() != true)) {
					receivers.add(new MessageReceiver(af.getId(), af
							.getMemberId()));
				}
			}
			// 批量更新,待签收公文变成已经发送
			Map<String, Object> columns = new Hashtable<String, Object>();
			columns.put("isFinish", true);
			columns.put("isDelete", true);
			columns.put("state", StateEnum.edoc_exchange_sent.getKey());
			affairManager.update(columns, new Object[][] {
					{ "app", ApplicationCategoryEnum.exSign.key() },
					{ "objectId", record.getEdocId() },
					{ "subObjectId", record.getId() } });
			/*
			 * （代签收的人员）发消息给其他公文收发员，公文已回退
			 */
			if (null != receivers && receivers.size() > 0) {
				userMessageManager.sendSystemMessage(new MessageContent(
							"exchange.stepback",
							affairList.get(0).getSubject(), userName,
							stepBackInfo),
						ApplicationCategoryEnum.edocRec, user.getId(),
						receivers);
			}
		}
			out.println("<script>");
			out.println("parent.doEndSign('toReceive');");
			out.println("</script>");
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (isRelieveLock)
				ColLock.getInstance().removeLock(
						Long.valueOf(Long.parseLong(stepBackSendEdocId)));
		}
		return null;
	}

	/**
	 * 待登记公文回退
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView stepBackRecievedEdoc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		// 待登记公文ID
		String stepBackToRegisterEdocId = request
				.getParameter("stepBackToRegisterEdocId");
		if (Strings.isBlank(stepBackToRegisterEdocId)) {
			log.error("ID为空");
			return null;
		}
		// 回退说明
		String stepBackInfo = request.getParameter("stepBackInfo");

		EdocRecieveRecord record = edocExchangeManager.getReceivedRecord(Long
				.valueOf(Long.parseLong(stepBackToRegisterEdocId)));
		ResourceBundle r = ResourceBundle.getBundle(
				"com.seeyon.v3x.exchange.resources.i18n.ExchangeResource",
				CurrentUser.get().getLocale());
		String alertNote = "";
		// 检查已经被登记
		if (null != record
				&& record.getStatus() == Constants.C_iStatus_Registered) {
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.registerRecord.register.already");
			try {
				out.println("<script>");
				out.println("alert('" + alertNote + "');");
				out
						.println("if(window.dialogArguments){window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				// out.println("history.go(-1);");
				out.println("</script>");
				return null;
			} catch (Exception e) {

			}
		}
		// 检查是否已经被回退
		else if (null != record
				&& record.getStatus() == Constants.C_iStatus_Torecieve) {
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.registerRecord.stepback.already");
			try {
				out.println("<script>");
				out.println("alert('" + alertNote + "');");
				out
						.println("if(window.dialogArguments){window.returnValue='true';window.close();}else{");
				out.println("parent.parent.location.reload(true)");
				out.println("}");
				// out.println("history.go(-1);");
				out.println("</script>");
				return null;
			} catch (Exception e) {

			}
		}
		// 检查是否被撤销
		else if (null == record) {
			alertNote = ResourceBundleUtil.getString(r,
					"exchange.send.withdrawed");
			out.println("<script>");
			out.println("alert('" + alertNote + "');");
			out.println("if(window.dialogArguments){");
			out.println("window.returnValue='true';window.close();}else{");
			out.println("parent.parent.location.reload(true)");
			out.println("}");
			// out.println("history.go(-1);");
			out.println("</script>");
			return null;
		}

		boolean isRelieveLock = true;
		try {
			// 检查同步锁
			if (!ColHelper.colOperationLock(Long.valueOf(Long
					.parseLong(stepBackToRegisterEdocId)), user.getId(), user
					.getName(), ColLock.COL_ACTION.tackback, response,
					ApplicationCategoryEnum.edoc)) {
				isRelieveLock = false;
				return null;
			}
			// 回退
			edocExchangeManager.stepBackRecievedEdoc(Long
					.parseLong(stepBackToRegisterEdocId), Long
				.parseLong(stepBackToRegisterEdocId), stepBackInfo);
			out.println("<script>");
			out.println("parent.location.reload(true);");
			out.println("</script>");
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (isRelieveLock)
				ColLock.getInstance().removeLock(
						Long.valueOf(Long.parseLong(stepBackToRegisterEdocId)));
		}
		return null;
	}
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
}