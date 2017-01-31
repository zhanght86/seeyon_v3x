package com.seeyon.v3x.common.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColQuoteformRecordManger;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocFavoriteManager;
import com.seeyon.v3x.doc.manager.DocLearningManager;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.util.Strings;

/**
 * 协同访问安全控制主程序
 * 
 * @author Mazc 2010-04-01 <br>
 *         合法的权限规则:
 *         <ul>
 *         <li>流程中的人员, 发布范围内的人</li>
 *         <li>代理人</li>
 *         <li>关联文档</li>
 *         <li>归档</li>
 *         <li>督办人</li>
 *         </ul>
 */
public class SecurityCheck {
	private static Log log = LogFactory.getLog(SecurityCheck.class);
	// SecurityControl Manager 实现类
	private static AttachmentManager attachmentManager;
	private static DocAclManager docAclManager;
	private static DocLearningManager docLearningManager;
	private static DocFavoriteManager docFavoriteManager;
	private static ColSuperviseManager colSuperviseManager;
	private static IOperBase iOperBase;
	private static ColQuoteformRecordManger colQuoteformRecordManger;
	private static ColManager colManager;

	private static AttachmentManager theAttachmentManager() {
		if (attachmentManager == null) {
			attachmentManager = (AttachmentManager) ApplicationContextHolder
					.getBean("attachmentManager");
		}
		return attachmentManager;
	}

	private static DocAclManager theDocAclManager() {
		if (docAclManager == null) {
			docAclManager = (DocAclManager) ApplicationContextHolder
					.getBean("docAclManager");
		}
		return docAclManager;
	}

	private static DocLearningManager theDocLearningManager() {
		if (docLearningManager == null) {
			docLearningManager = (DocLearningManager) ApplicationContextHolder
					.getBean("docLearningManager");
		}
		return docLearningManager;
	}

	private static DocFavoriteManager theDocFavoriteManager() {
		if (docFavoriteManager == null) {
			docFavoriteManager = (DocFavoriteManager) ApplicationContextHolder
					.getBean("docFavoriteManager");
		}
		return docFavoriteManager;
	}

	private static ColSuperviseManager theColSuperviseManager() {
		if (colSuperviseManager == null) {
			colSuperviseManager = (ColSuperviseManager) ApplicationContextHolder
					.getBean("colSuperviseManager");
		}
		return colSuperviseManager;
	}

	private static IOperBase getIOperBase() {
		if (iOperBase == null) {
			iOperBase = (IOperBase) SeeyonForm_Runtime.getInstance().getBean(
					"iOperBase");
		}
		return iOperBase;
	}
	
	private static ColManager getColManager() {
		if(colManager == null) {
			colManager = (ColManager)ApplicationContextHolder.getBean("colManager");
		}
		return colManager;
	}
	
	private static ColQuoteformRecordManger getColQuoteformRecordManger() {
		if(colQuoteformRecordManger == null) {
			colQuoteformRecordManger = (ColQuoteformRecordManger)ApplicationContextHolder.getBean("colQuoteformRecordManger");
		}
		return colQuoteformRecordManger;
	}

	private static Map<ApplicationCategoryEnum, SecurityControl> securityControlMap = new HashMap<ApplicationCategoryEnum, SecurityControl>();

	public void setSecurityCheckers(
			Map<String, SecurityControl> securityCheckers) {
		if (securityCheckers != null) {
			for (Iterator<String> iterator = securityCheckers.keySet()
					.iterator(); iterator.hasNext();) {
				String app = (String) iterator.next();
				ApplicationCategoryEnum appEnum = ApplicationCategoryEnum
						.valueOf(Integer.parseInt(app));
				SecurityCheck.securityControlMap.put(appEnum, securityCheckers
						.get(app));
			}
		}
	}

	/**
	 * 安全防护，校验是否有权限查看主题
	 * 
	 * @param request
	 * @param response
	 * @param appEnum
	 *            应用枚举
	 * @param user
	 *            CurrentUser
	 * @param objectId
	 *            主题对象的id
	 * @param affair
	 *            用于协同和公文，其他应用传null
	 * @param preArchiveId
	 *            预归档Id，用于协同，其他应用传null
	 * @return
	 */
	public static boolean isLicit(HttpServletRequest request, HttpServletResponse response, ApplicationCategoryEnum appEnum, User user, Long objectId, Affair affair, Long preArchiveId){
		SecurityControl control = SecurityCheck.securityControlMap.get(appEnum);
		if(control == null){
			log.error("未注册实现类的应用:" + appEnum);
			return false;
		}
		
		//1、缓存中有，直接return
		String cacheKey = String.valueOf(objectId);
		if(appEnum!=ApplicationCategoryEnum.doc&&AccessControlBean.getInstance().isAccess(appEnum, cacheKey, user.getId())){
			return true;
		}
		//2、来自关联文档， 统一校验
		String docResIdStr = request.getParameter("docResId");
		String preObjectIdStr = request.getParameter("baseObjectId");
		//关联表单的colSummaryId.
		String refColSummaryId = request.getParameter("refColSummaryId");
    	if(Strings.isNotBlank(preObjectIdStr)){ 
    		//检查是否是合法的关联文档，如果是，更新缓存校验前一协同权限， preObjectId 前一主题对象的id，用于关联文档的情况
    		ApplicationCategoryEnum preAppEnum = ApplicationCategoryEnum.collaboration;
    		if(Strings.isNotBlank(request.getParameter("baseApp"))){
    			preAppEnum = ApplicationCategoryEnum.valueOf(Integer.parseInt(request.getParameter("baseApp")));
    			if(preAppEnum == ApplicationCategoryEnum.edocRec || preAppEnum == ApplicationCategoryEnum.edocRegister
    					|| preAppEnum == ApplicationCategoryEnum.edocSend || preAppEnum == ApplicationCategoryEnum.edocSign
    					|| preAppEnum == ApplicationCategoryEnum.exchange || preAppEnum == ApplicationCategoryEnum.exSend
    					|| preAppEnum == ApplicationCategoryEnum.exSign){
    				preAppEnum = ApplicationCategoryEnum.edoc;
    			}
    		}
    		Long genesisId = objectId;
    		if(Strings.isNotBlank(docResIdStr)){ //如果是从文档中心转入的，优先以文档id判断
    			genesisId = Long.parseLong(docResIdStr);
    		}
    		else if(affair != null && appEnum == ApplicationCategoryEnum.collaboration || appEnum == ApplicationCategoryEnum.edoc){
    			genesisId = affair.getId();
    		}
    		String openerSummaryId = request.getParameter("openerSummaryId");
    		String noFlowRecordId = request.getParameter("noFlowRecordId");
    		
    		if(AccessControlBean.getInstance().isAccess(preAppEnum, preObjectIdStr, user.getId()) 
    				&& theAttachmentManager().checkIsLicitGenesis(Long.parseLong(preObjectIdStr), genesisId)){
				AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
				return true;    			
    		}
    		/**
    		 * 1、子流程表单中的关联文档检查通过条件：对子流程有权限，且其父流程id为关联文档真实数据
    		 * 2、转发的表单流程，对于其表单域中插入的关联文档，对新的协同有权限即通过
    		 * {TODO 如果此处加上关联文档checkIsLicitGenesis校验，会对转发的表单中插入的关联文档无能为力。
    		 * 原因为：表单域中插入的关联文档在转发时没有拷贝附件，只是复制了链接，而转发协同之间没有关联，所以无法传递权限。
    		 * V312暂且放宽此处，对新的协同有权限即通过，后续需要完善。}
    		 */
	    	else if(!preObjectIdStr.equals(openerSummaryId) && Strings.isNotBlank(openerSummaryId)
	    			&& AccessControlBean.getInstance().isAccess(preAppEnum, openerSummaryId, user.getId())){
	    		//&& theAttachmentManager().checkIsLicitGenesis(Long.parseLong(preObjectIdStr), genesisId)
    			AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
    			return true;    			
	    	} else if (Strings.isBlank(openerSummaryId) && Strings.isNotBlank(noFlowRecordId)&&theAttachmentManager().checkIsLicitGenesis(Long.parseLong(noFlowRecordId), genesisId)) {
	    		AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
	    		return true;
	    	// 判断关联表单权限
	    	} else if (Strings.isNotBlank(refColSummaryId) && formAssociation(preObjectIdStr, refColSummaryId)) {
	    		AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
	    		return true;
	    	//业务信息表单中插入关联问档,无权查看修改
	    	}else if(theAttachmentManager().checkIsLicitGenesis(Long.parseLong(preObjectIdStr), genesisId)){
	    		AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
	    		return true;
	    	}
    		else{
    			log.warn("非法的关联文档|" + user.getId() + "|" + objectId + "|" + preObjectIdStr);
    		}
    	}
    	//从文档直接打开的其他关联应用，如归档协同，如果文档权限校验通过，则有权访问。
    	else if(appEnum!=ApplicationCategoryEnum.doc&&Strings.isNotBlank(docResIdStr) && AccessControlBean.getInstance().isAccess(ApplicationCategoryEnum.doc, docResIdStr, user.getId())){
    		AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
			return true;
    	}
    	// 表单关联他人授权访问权限检查
    	String from = request.getParameter("from");
    	if(from != null && appEnum!=ApplicationCategoryEnum.doc && "Sent".equals(from)){
    		if(affair.getIsRelationAuthority()){
    			AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
    			return true;
    		}
    		if(Strings.isNotBlank(refColSummaryId) && affair!=null && formAssociation(String.valueOf(affair.getObjectId()), refColSummaryId)){
    			AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
    			return true;
    		}
    	}
    	//3、各应用的访问权限检查
		boolean result = control.check(request, appEnum, user.getId(), objectId, affair, preArchiveId);
		if(result){
			AccessControlBean.getInstance().addAccessControl(appEnum, cacheKey, user.getId());
			return true;
		}
		//M1调用检查时response为NULL
		if(response!=null){
			//记录非法访问日志
			printInbreakTrace(request, response, user, appEnum);	
		}
		return false;
	}

	/**
	 * 检查表单关联权限
	 * @param colSummaryId 主表单ID
	 * @param refColSummayID 被关联表单ID
	 * @return 是否通过
	 */
	private static boolean formAssociation(String colSummaryId,String refColSummaryId) {
		long cSummaryId = 0;
		long rCSummaryId = 0;
		try {
			cSummaryId = Long.parseLong(colSummaryId);
			rCSummaryId = Long.parseLong(refColSummaryId);
			
		}catch(Exception e){
			//ignore
		} 
		boolean result = false;
		try {
			List<Long> lists = getColQuoteformRecordManger().getQuoteIdListBySummayId(rCSummaryId);
			if(lists != null) {
				result = lists.contains(cSummaryId);
				if(!result){
					ColSummary colSummary = getColManager().getColSummaryById(cSummaryId, false);
					//可能存在主流程关联表单，子流程查看，或者子流程关联表单，主流程或其他子流程查看
					//由于summaryId不同，所以这里做个循环处理，查找所有主子流程
					if(colSummary != null && colSummary.getFormAppId() != null && colSummary.getFormRecordId() != null){
	                	List<ColSummary> summarys = (List<ColSummary>)colManager.getSummaryIdByFormIdAndRecordId(colSummary.getFormAppId(),null,colSummary.getFormRecordId());
	                	for(ColSummary col : summarys){
	                		if(!col.getId().equals(colSummary.getId())){
	                			result = lists.contains(col.getId());
	                			if(result)break;
	                		}
	                	}
					}
				}
			}
		} catch (Exception e) {
			log.error("formAssociation error!", e);
		}
		return result;
	}
	/**
	 * 记录非法访问日志
	 * 
	 * @param request
	 * @param user
	 * @param subject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static void printInbreakTrace(HttpServletRequest request,
			HttpServletResponse response, User user,
			ApplicationCategoryEnum appEnum) {
		StringBuffer msg = new StringBuffer();
		msg.append("用户[").append(user.getLoginName()).append(", ").append(
				user.getRemoteAddr()).append("]试图访问无权查看的主题:");
		if ("GET".equals(request.getMethod())) {
			msg.append(request.getQueryString());
		} else {
			Enumeration e = (Enumeration) request.getParameterNames();
			while (e.hasMoreElements()) {
				String parName = (String) e.nextElement();
				msg.append(parName + ":" + request.getParameter(parName) + "|");
			}
		}
		log.warn(msg.toString());
		showAlert(response, "您无权查看该主题!");
	}

	private static void showAlert(HttpServletResponse response, String msg) {
		try {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + msg + "')");
			out.println("try{if(window.dialogArguments){window.close();}");
			out.println("else{parent.getA8Top().reFlesh();}}catch(e){}");
			out.println("</script>");
		} catch (IOException e1) {
			log.error(e1);
		}
	}

	/*****************************************
	 * 其他应用访问控制辅助方法
	 **************************************** 
	 */

	/****
	 * 是否是流程的督办人
	 * 
	 * @param currentUserId
	 * @param objectId
	 *            summaryId
	 */
	public static boolean isSupervisor(Long currentUserId, Long objectId) {
		return theColSuperviseManager().isSupervisor(currentUserId, objectId);
	}

	/**
	 * 文档权限判断
	 * 
	 * @param archiveId
	 * @return
	 */
	public static boolean isDocCanAccess(Long archiveId) {
		boolean result = theDocAclManager().hasOpenAcl(archiveId);
		if (!result) { // 不是归档，校验是否为学习区的
			result = theDocLearningManager().isLearnDoc(archiveId);
		}
		if (!result) {// 判断是否是发送到首页的知识文档
			result = theDocFavoriteManager().isFavorite(archiveId);
		}
		return result;
	}

	/**
	 * 是否有权限查看统计公文的详细信息 (公文统计的查看为不同的入口，暂且放在此类)<br>
	 * 只有部门收发员或单位收发员才具有此功能菜单
	 * 
	 * @param summary
	 * @param user
	 * @return
	 */
	public static boolean isHasAuthorityToStatDetail(
			HttpServletRequest request, HttpServletResponse response,
			EdocSummary summary, User user) {
		String accountIds = "";
		try {
			accountIds = EdocRoleHelper.getUserExchangeAccountIds();
		} catch (BusinessException e) {
			log.error("公文统计安全权限判断异常[checkHasAclToStatistics].", e);
		}
		String currentSummaryOrgAccountId = "";
		if (summary.getOrgAccountId() != null)
			currentSummaryOrgAccountId = Long.toString(summary
					.getOrgAccountId().longValue());
		if (accountIds.contains(currentSummaryOrgAccountId)) {
			AccessControlBean.getInstance().addAccessControl(
					ApplicationCategoryEnum.edoc,
					String.valueOf(summary.getId()), user.getId());
			return true;
		}
		String departmentIds = "";
		try {
			departmentIds = EdocRoleHelper.getUserExchangeDepartmentIds();
		} catch (BusinessException e) {
			log.error("公文统计安全权限判断异常[checkHasAclToStatistics].", e);
		}
		String summaryOrgDepartmentId = "";
		if (summary.getOrgDepartmentId() != null) {
			summaryOrgDepartmentId = Long.toString(summary.getOrgDepartmentId()
					.longValue());
		}
		if (departmentIds.contains(summaryOrgDepartmentId)) {
			AccessControlBean.getInstance().addAccessControl(
					ApplicationCategoryEnum.edoc,
					String.valueOf(summary.getId()), user.getId());
			return true;
		}
		printInbreakTrace(request, response, user, ApplicationCategoryEnum.edoc);
		return false;
	}

	/**
	 * 是否具有表单查询的权限
	 * 
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	public static boolean hasFormQueryPermission(HttpServletRequest request,
			HttpServletResponse response, User user, Long appId,
			String objectName, String summaryId) {
		boolean canAccess = false;
		try {
			canAccess = getIOperBase().checkAccess(user, appId, objectName, 1);
			if (!canAccess) { // 非查询 判断统计
				canAccess = getIOperBase().checkAccess(user, appId, objectName,
						2);
			}
		} catch (DataDefineException e) {
			log.error("访问控制校验DataDefineException:", e);
		} catch (BusinessException e) {
			log.error(e);
		}
		if (canAccess) {
			AccessControlBean.getInstance().addAccessControl(
					ApplicationCategoryEnum.collaboration, summaryId,
					user.getId());
			return true;
		}
		printInbreakTrace(request, response, user, ApplicationCategoryEnum.form);
		return false;
	}
}
