package com.seeyon.v3x.collaboration.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.dao.ColSuperviseDetailDao;
import com.seeyon.v3x.collaboration.dao.ColSuperviseTemplateRoleDao;
import com.seeyon.v3x.collaboration.dao.ColSupervisorDao;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSuperviseReceiver;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseDealModel;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ColSuperviseManagerImpl implements
		ColSuperviseManager {


	static Log log = LogFactory.getLog(ColSuperviseManagerImpl.class);

	private ColSuperviseDetailDao colSuperviseDetailDao;

	private ColManager colManager;

	private OrgManager orgManager;

	private UserMessageManager userMessageManager;

	private AffairManager affairManager;

	private MetadataManager metadataManager;

	private EdocSummaryDao edocSummaryDao;

	private ColSupervisorDao colSupervisorDao;

	private ColSuperviseTemplateRoleDao colSuperviseTemplateRoleDao;

	private EdocSummaryManager edocSummaryManager;

	private SearchManager searchManager ;

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	/**
	 * @return the edocSummaryDao
	 */
	public EdocSummaryDao getEdocSummaryDao() {
		return edocSummaryDao;
	}
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
	/**
	 * @param edocSummaryDao the edocSummaryDao to set
	 */
	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}
	public ColSuperviseDetail getCurrentUserSupervise(int entityType,long entityId, long userId) {
		return (ColSuperviseDetail)colSuperviseDetailDao.getCurrentUserSupervise(entityType,entityId, userId);
	}
	public ColSuperviseDetail getSupervise(int entityType,long entityId) {
		return (ColSuperviseDetail)colSuperviseDetailDao.getSupervise(entityType,entityId);
	}

	public List<ColSupervisor> getColSupervisorList(int entityType,long entityId) {
		ColSuperviseDetail superviseDetail = (ColSuperviseDetail)colSuperviseDetailDao.getSupervise(entityType,entityId);
		if(superviseDetail==null)
			return null;
		Set<ColSupervisor> colSupervisors = superviseDetail.getColSupervisors();
		if(colSupervisors == null || colSupervisors.isEmpty())
			return null;
		List<ColSupervisor> supervisorList = new ArrayList<ColSupervisor>();
		for(ColSupervisor supervisor : colSupervisors){
			supervisorList.add(supervisor);
		}
		return supervisorList;
	}

	public long save(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames
			,long[] supervisorIds,Date awakeDate,int entityType,long entityId,int state,boolean sendMessage, String forwardMemberIdStr) {
		ColSuperviseDetail detail = new ColSuperviseDetail();
		detail.setIdIfNew();
		detail.setTitle(title);
		detail.setSenderId(senderId);
		detail.setStatus(state);
		detail.setSupervisors(supervisorNames);
		detail.setCount(0);
		detail.setAwakeDate(awakeDate);
		detail.setCreateDate(new Date());
		//detail.setCanModify(canModify);
		detail.setEntityType(entityType);
		detail.setEntityId(entityId);
		Set<ColSupervisor> supervisors = new HashSet<ColSupervisor>();
		for(Long supervisorId:supervisorIds) {
			ColSupervisor colSupervisor = new ColSupervisor();
			colSupervisor.setIdIfNew();
			colSupervisor.setSuperviseId(detail.getId());
			colSupervisor.setSupervisorId(supervisorId);
			supervisors.add(colSupervisor);
		}
		detail.setColSupervisors(supervisors);
		if(sendMessage)
			this.sendMessageAndJob(entityType,importantLevel, summarySubject, senderId, senderName, detail, null, forwardMemberIdStr);
		colSuperviseDetailDao.save(detail);
		return detail.getId();
	}

	public long saveForTemplate(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames
			,long[] supervisorIds,long superviseDate,int entityType,long entityId,boolean sendMessage) {
		ColSuperviseDetail detail = new ColSuperviseDetail();
		detail.setIdIfNew();
		detail.setTitle(title);
		detail.setSenderId(senderId);
		detail.setStatus(Constant.superviseState.supervising.ordinal());
		detail.setSupervisors(supervisorNames);
		detail.setCount(0);
		detail.setTemplateDateTerminal(superviseDate);
		//detail.setCanModify(canModify);
		detail.setEntityType(entityType);
		detail.setEntityId(entityId);
		if(supervisorIds != null) {
			Set<ColSupervisor> supervisors = new HashSet<ColSupervisor>();
			for(Long supervisorId:supervisorIds) {
				ColSupervisor colSupervisor = new ColSupervisor();
				colSupervisor.setIdIfNew();
				colSupervisor.setSuperviseId(detail.getId());
				colSupervisor.setSupervisorId(supervisorId);
				supervisors.add(colSupervisor);
			}
			detail.setColSupervisors(supervisors);
		}
		colSuperviseDetailDao.save(detail);
		return detail.getId();
	}

	public void update(int importantLevel, String summarySubject,String title,long senderId,String senderName
			,String supervisorNames,long[] supervisorIds,Date awakeDate,int entityType,long entityId,int state,boolean sendMessage, String forwardMemberIdStr) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail == null) {
			this.save(importantLevel, summarySubject, title, senderId, senderName, supervisorNames, supervisorIds, awakeDate, entityType, entityId,state , sendMessage, forwardMemberIdStr);
			return;
		}
	   //不能在这手动delete。update detail的时候，hibernate会自己delte,这里delete了导致detail.getColSupervisors()这个没有加载到内存,size =0,后面的判断失效.
	   //	colSuperviseDetailDao.delete(ColSupervisor.class, new Object[][]{{"superviseId", detail.getId()}});
		detail.setTitle(title);
		detail.setSenderId(senderId);
		detail.setSupervisors(supervisorNames);
		detail.setAwakeDate(awakeDate);
		//detail.setCanModify(canModify);
		detail.setStatus(state);
		detail.setEntityId(entityId);
		detail.setEntityType(entityType);

		List<Long> deletedPerson = new ArrayList<Long>();
		Set<ColSupervisor> add = new HashSet<ColSupervisor>();
		Map<Long,Long> hash = new HashMap<Long,Long>();
		if(supervisorIds != null) {
			for(Long supervisorId:supervisorIds) {
				hash.put(supervisorId, supervisorId);
			}
		}

		Set<Long> oldSuper = new HashSet<Long>();
		if(detail.getColSupervisors().size()>0) {
			Iterator<ColSupervisor> it = detail.getColSupervisors().iterator();
			while(it.hasNext()) {
				ColSupervisor supervisor = it.next();
				if(hash.get(supervisor.getSupervisorId())==null)
					deletedPerson.add(supervisor.getSupervisorId());
				it.remove();
				supervisor.setSuperviseId(null);
				oldSuper.add(supervisor.getSupervisorId());
			}
		}

		if(supervisorIds != null) {
			for(Long supervisorId:supervisorIds) {
				ColSupervisor colSupervisor = new ColSupervisor();
				colSupervisor.setIdIfNew();
				colSupervisor.setSuperviseId(detail.getId());
				colSupervisor.setSupervisorId(supervisorId);
				detail.getColSupervisors().add(colSupervisor);
				if(!oldSuper.contains(supervisorId)) add.add(colSupervisor);
			}
		}
		colSuperviseDetailDao.update(detail);
		if(sendMessage)
			this.sendMessageAndJob(entityType,importantLevel, summarySubject, senderId, senderName, detail, deletedPerson,add, forwardMemberIdStr);
	}

	public void updateForTemplate(int importantLevel, String summarySubject,String title,long senderId,String senderName
			,String supervisorNames,long[] supervisorIds,long superviseDate,int entityType,long entityId,boolean sendMessage) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail == null) {
			this.saveForTemplate(importantLevel, summarySubject, title, senderId, senderName, supervisorNames, supervisorIds, superviseDate, entityType, entityId, sendMessage);
			return;
		}
		colSuperviseDetailDao.delete(ColSupervisor.class, new Object[][]{{"superviseId", detail.getId()}});
		detail.setTitle(title);
		detail.setSenderId(senderId);
		detail.setSupervisors(supervisorNames);
		detail.setTemplateDateTerminal(superviseDate);
		//detail.setCanModify(canModify);
		detail.setStatus(Constant.superviseState.supervising.ordinal());
		detail.setEntityId(entityId);
		detail.setEntityType(entityType);

		List<Long> deletedPerson = new ArrayList<Long>();
		Map<Long,Long> hash = new HashMap<Long,Long>();
		if(supervisorIds != null) {
			for(Long supervisorId:supervisorIds) {
				hash.put(supervisorId, supervisorId);
			}
		}
		if(detail.getColSupervisors().size()>0) {
			Iterator<ColSupervisor> it = detail.getColSupervisors().iterator();
			while(it.hasNext()) {
				ColSupervisor supervisor = it.next();
				if(hash.get(supervisor.getSupervisorId())==null)
					deletedPerson.add(supervisor.getSupervisorId());
				it.remove();
				supervisor.setSuperviseId(null);
			}
		}

		if(supervisorIds != null) {
			for(Long supervisorId:supervisorIds) {
				ColSupervisor colSupervisor = new ColSupervisor();
				colSupervisor.setIdIfNew();
				colSupervisor.setSuperviseId(detail.getId());
				colSupervisor.setSupervisorId(supervisorId);
				detail.getColSupervisors().add(colSupervisor);
			}
		}
		colSuperviseDetailDao.update(detail);
	}

	public void updateDetail(ColSuperviseDetail colSuperviseDetail) {
		colSuperviseDetailDao.update(colSuperviseDetail);
	}

	public void updateOnlySendMessage(int importantLevel, String summarySubject,long userId,String userName,int entityType,long entityId, String forwardMemberIdStr) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail != null) {
			this.sendMessageAndJob(entityType,importantLevel, summarySubject, userId, userName, detail, null, forwardMemberIdStr);
		}
	}

	public Integer getMySuperviseCount(long userId, int status) {
		return colSuperviseDetailDao.getMySuperviseCount(userId, status);
	}


	public Integer getMySuperviseTotalCount(long userId, int status,Integer... types) {
		/*List<ColSuperviseModel> list = this
		.getMyAllSuperviseForMorePending(
				CurrentUser.get().getId(),
				com.seeyon.v3x.collaboration.Constant.superviseState.supervising
						.ordinal());
		if(null!= list){
			return list.size();
		}else{
			return 0;
		}*/
		return colSuperviseDetailDao.getMySuperviseTotalCount(userId, status,types);
	}

	public Integer getMySuperviseTotalCountByCateOrImportant(long userId, int status,List<Integer> types, List<Integer> importantList) {
		return colSuperviseDetailDao.getMySuperviseTotalCountByCateOrImportant(userId, status, types, importantList);
	}

	public List<ColSuperviseModel> getMyAllSuperviseForMorePending(long userId, int status) {
		List<ColSuperviseDetail> list = colSuperviseDetailDao.getAllSuperviseDetailListInMySuperviseForPendingMore(userId,status);
		if(list == null)
			return null;
		List<ColSuperviseModel> modelList = new ArrayList<ColSuperviseModel>();
		//int caseId = 0;
		//String caseLogXML = "";
		String caseProcessXML = "";
		//String caseWorkItemLogXML = "";
		boolean hasWorkflow = false;
		String processDescBy = "";
		for(ColSuperviseDetail detail:list) {
			ColSuperviseModel model = new ColSuperviseModel();
			try {
				if(detail!=null && (Constant.superviseType.summary.ordinal()==detail.getEntityType())){ // 如果督办事项是协同
					ColSummary colSummary = this.colManager.getColSummaryById(detail.getEntityId(), false);
					if(colSummary == null) {
						continue;
					}
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(colSummary.getSubject());
					model.setSender(colSummary.getStartMemberId());
					model.setSendDate(colSummary.getStartDate());
					model.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
				} else if(detail!=null && (Constant.superviseType.edoc.ordinal()==detail.getEntityType())){ // 如果督办事项是公文，  则EdocSummary getEdocSummaryById(long summaryId, boolean needBody)
					EdocSummary edocSummary = edocSummaryDao.get(detail.getEntityId());
					if(edocSummary == null) {
						continue;
					}
			        try {
			            OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			            V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, edocSummary.getStartUserId());
			            edocSummary.setStartMember(member);
			        }
			        catch (BusinessException e) {
			            log.error("读取公文主体属性时查询发起人错误", e);
			        }
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(edocSummary.getSubject());
					model.setSender(edocSummary.getStartMember().getId());
					model.setSendDate(edocSummary.getStartTime());
					int appType = ApplicationCategoryEnum.edoc.ordinal();
					if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
						appType = ApplicationCategoryEnum.edocSend.ordinal();
					}if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
						appType = ApplicationCategoryEnum.edocRec.ordinal();
					}if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN){
						appType = ApplicationCategoryEnum.edocSign.ordinal();
					}
					model.setAppType(appType);
				}
			}catch(Exception e) {
				log.error(e);
				continue;
			}
			Date now = new Date(System.currentTimeMillis());
			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
				model.setIsRed(true);
			}

			model.setId(detail.getId());
			model.setCount(detail.getCount());
			Set<ColSupervisor> colSupervisors = detail.getColSupervisors();
			StringBuffer ids = new StringBuffer();
			if(colSupervisors != null && colSupervisors.size()>0) {
				for(ColSupervisor colSupervisor:colSupervisors) {
					ids.append(colSupervisor.getSupervisorId() + ",");
				}
				model.setSupervisor(ids.substring(0, ids.length()-1));
			}
			model.setAwakeDate(detail.getAwakeDate());
			//model.setCanModify(detail.isCanModify());
			model.setSummaryId(detail.getEntityId());
			model.setContent(Strings.toHTML(detail.getDescription()));
			model.setStatus(detail.getStatus());

			if (StringUtils.isNotEmpty(caseProcessXML)) {
				hasWorkflow = true;
				processDescBy = FlowData.DESC_BY_XML;
			}
			model.setHasWorkflow(hasWorkflow);
			model.setProcessDescBy(processDescBy);
			// kuanghs 判断是类型公文还是协同
			model.setEntityType(detail.getEntityType());
			modelList.add(model);
		}
		//Pagination.setRowCount(modelList.size());
		return modelList;

	}
	public int countMyAllSuperviseWithoutTemplate(Long userId, int status, List<Integer> entityType) {
		return colSuperviseDetailDao.countMySuperviseWithoutTemplate(userId, status, entityType);
	}
	public List<ColSuperviseModel> getMyAllSuperviseWithoutTemplate(long userId, int status,List<Integer> entityType,int maxCount) {
		List<ColSuperviseModel> list = colSuperviseDetailDao.getAllSuperviseModelListInMySuperviseWithoutTemplate(userId, status, null, null, null, entityType, maxCount);
		return list;
	}

	/** 督办栏目 */
	public List<ColSuperviseModel> getMyAllSupervise4SectionByImportLevel(long userId, int status,List<Integer> entityType,int maxCount, List<Integer> importantList) {
		StringBuilder sb  = new StringBuilder();
		for(Integer c : importantList){
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(c);
		}
		List<ColSuperviseModel> list = colSuperviseDetailDao.getAllSuperviseModelListInMySuperviseWithoutTemplate(userId, status, "importantLevel", sb.toString(), null, entityType, maxCount);
		return list;
	}
	/** 督办栏目 */
	public List<ColSuperviseModel> getMyAllSupervise4SectionByCategory(long userId, int status,List<Integer> entityType,int maxCount, List<String> category) {
		StringBuilder sb  = new StringBuilder();
		for(String c : category){
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(c);
		}
		List<ColSuperviseModel> list = colSuperviseDetailDao.getAllSuperviseModelListInMySuperviseWithoutTemplate(userId, status, "category", sb.toString(), null, entityType, maxCount);
		return list;
	}
	public List<ColSuperviseModel> getMyAllSupervise(long userId, int status) {
		List<ColSuperviseDetail> list = colSuperviseDetailDao.getAllSuperviseDetailListInMySupervise(userId,status);
		if(list == null)
			return null;
		List<ColSuperviseModel> modelList = new ArrayList<ColSuperviseModel>();
		//int caseId = 0;
		//String caseLogXML = "";
		String caseProcessXML = "";
		//String caseWorkItemLogXML = "";
		boolean hasWorkflow = false;
		String processDescBy = "";
		for(ColSuperviseDetail detail:list) {
			ColSuperviseModel model = new ColSuperviseModel();
			try {
				if(detail!=null && (Constant.superviseType.summary.ordinal()==detail.getEntityType())){ // 如果督办事项是协同
					ColSummary colSummary = this.colManager.getColSummaryById(detail.getEntityId(), false);
					if(colSummary == null) {
						continue;
					}
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(colSummary.getSubject());
					model.setSender(colSummary.getStartMemberId());
					model.setSendDate(colSummary.getStartDate());
				} else if(detail!=null && (Constant.superviseType.edoc.ordinal()==detail.getEntityType())){ // 如果督办事项是公文，  则EdocSummary getEdocSummaryById(long summaryId, boolean needBody)
					EdocSummary edocSummary = edocSummaryDao.get(detail.getEntityId());
					if(edocSummary == null) {
						continue;
					}
			        try {
			            OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			            V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, edocSummary.getStartUserId());
			            edocSummary.setStartMember(member);
			        }
			        catch (BusinessException e) {
			            log.error("读取公文主体属性时查询发起人错误", e);
			        }
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(edocSummary.getSubject());
					model.setSender(edocSummary.getStartMember().getId());
					model.setSendDate(edocSummary.getStartTime());
				}
			}catch(Exception e) {
				log.error(e);
				continue;
			}

			model.setId(detail.getId());
			model.setCount(detail.getCount());
			Set<ColSupervisor> colSupervisors = detail.getColSupervisors();
			StringBuffer ids = new StringBuffer();
			if(colSupervisors != null && colSupervisors.size()>0) {
				for(ColSupervisor colSupervisor:colSupervisors) {
					ids.append(colSupervisor.getSupervisorId() + ",");
				}
				model.setSupervisor(ids.substring(0, ids.length()-1));
			}
			model.setAwakeDate(detail.getAwakeDate());
			//model.setCanModify(detail.isCanModify());
			model.setSummaryId(detail.getEntityId());
			model.setContent(Strings.toHTML(detail.getDescription()));
			model.setStatus(detail.getStatus());

			if (StringUtils.isNotEmpty(caseProcessXML)) {
				hasWorkflow = true;
				processDescBy = FlowData.DESC_BY_XML;
			}
			model.setHasWorkflow(hasWorkflow);
			model.setProcessDescBy(processDescBy);
			// kuanghs 判断是类型公文还是协同
			model.setEntityType(detail.getEntityType());
			modelList.add(model);
		}
		//Pagination.setRowCount(modelList.size());
		return modelList;

	}

	public List<ColSuperviseModel> getMySupervise(long userId,int status){
        List<Integer> entityType = new ArrayList<Integer>();
        entityType.add(Constant.superviseType.summary.ordinal());
		return colSuperviseDetailDao.getAllSuperviseModelListInMySuperviseWithoutTemplate(userId, status, null, null, null, entityType, -1);
	}

	public List<ColSuperviseModel> getMySupervise(long userId, int status, String condition, String textfield, String textfield1, Integer... superviseType){
	    List<Integer> entityType = new ArrayList<Integer>();
        if(superviseType != null && superviseType.length > 0){
            for (Integer integer : superviseType) {
                entityType.add(integer);
            }
        }
	    return colSuperviseDetailDao.getAllSuperviseModelListInMySuperviseWithoutTemplate(userId, status, condition, textfield, textfield1, entityType, -1);
	}


    public List<ColSuperviseModel> getSuperviseCollListByCondition(String condition, String field, String field1, long userId, int status, List<Long> templeteIds) {
        return colSuperviseDetailDao.getColSuperviseModelList(condition, field, field1, userId, status, templeteIds);
    }
    //重写getSuperviseCollListByCondition
    public List<ColSuperviseModel> getSuperviseCollListByCondition(String condition, String field, String field1, long userId, int status, List<Long> templeteIds,Integer secretLevel) {
        return colSuperviseDetailDao.getColSuperviseModelList(condition, field, field1, userId, status, templeteIds,secretLevel);
    }

    /**
     * 封装成ColSuperviseModel列表
     * @deprecated 存在循环SQL 废弃
     * @param list
     * @return
     */
    private List<ColSuperviseModel> encapsulationList(List<ColSuperviseDetail> list){
        List<ColSuperviseModel> modelList = new ArrayList<ColSuperviseModel>();
        //int caseId = 0;
        //String caseLogXML = "";
        String caseProcessXML = "";
        //String caseWorkItemLogXML = "";
        boolean hasWorkflow = false;
        String processDescBy = "";
        for(ColSuperviseDetail detail: list) {
            ColSuperviseModel model = new ColSuperviseModel();
			try {
				if(detail!=null && (Constant.superviseType.summary.ordinal()==detail.getEntityType())){ // 如果督办事项是协同
					//FIXME 这个做法很不妥，每条记录就是一条SQL
                    ColSummary colSummary = this.colManager.getColSummaryById(detail.getEntityId(), false);
					if(colSummary == null) {
						continue;
					}
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(colSummary.getSubject());
					model.setSender(colSummary.getStartMemberId());
					model.setSendDate(colSummary.getStartDate());
					model.setImportantLevel(colSummary.getImportantLevel());
					model.setDeadline(colSummary.getDeadline());
					//流程是否超期
					if(colSummary.getDeadline() != null && colSummary.getDeadline() != 0){
						Date startDate = colSummary.getStartDate();
						Date finishDate = colSummary.getFinishDate();
						Long deadline = colSummary.getDeadline()*60000;
						Date now = new Date();
						if(finishDate == null){
							if((now.getTime()-startDate.getTime()) > deadline){
								model.setWorkflowTimeout(true);
							}
						}else{
							Long expendTime = colSummary.getFinishDate().getTime() - colSummary.getStartDate().getTime();
							if((deadline-expendTime) < 0){
								model.setWorkflowTimeout(true);
							}
						}
					}
					model.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
                    model.setNewflowType(colSummary.getNewflowType());
				} else if(detail!=null && (Constant.superviseType.edoc.ordinal()==detail.getEntityType())){ // 如果督办事项是公文，  则EdocSummary getEdocSummaryById(long summaryId, boolean needBody)
					EdocSummary edocSummary = edocSummaryDao.get(detail.getEntityId());
					if(edocSummary == null) {
						continue;
					}
			        try {
			            OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			            V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, edocSummary.getStartUserId());
			            edocSummary.setStartMember(member);
			        }
			        catch (BusinessException e) {
			            log.error("读取公文主体属性时查询发起人错误", e);
			        }
					/*caseId = colSummary.getCaseId();
					caseLogXML =  this.colManager.getCaseLogXML(caseId);
					caseProcessXML = this.colManager.getCaseProcessXML(caseId);
					caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
					model.setCaseLogXML(Strings.toHTML(caseLogXML));
					model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
					model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
					model.setTitle(edocSummary.getSubject());
					model.setSender(edocSummary.getStartMember().getId());
					model.setSendDate(edocSummary.getStartTime());
					int appType = ApplicationCategoryEnum.edoc.ordinal();
					if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
						appType = ApplicationCategoryEnum.edocSend.ordinal();
					}if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
						appType = ApplicationCategoryEnum.edocRec.ordinal();
					}if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN){
						appType = ApplicationCategoryEnum.edocSign.ordinal();
					}
					model.setAppType(appType);
				}
			}catch(Exception e) {
				log.error(e);
				continue;
			}
                /*caseId = colSummary.getCaseId();
                caseLogXML =  this.colManager.getCaseLogXML(caseId);
                caseProcessXML = this.colManager.getCaseProcessXML(caseId);
                caseWorkItemLogXML = this.colManager.getCaseWorkItemLogXML(caseId);
                model.setCaseLogXML(Strings.toHTML(caseLogXML));
                model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
                model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));*/
			Date now = new Date(System.currentTimeMillis());
			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
				model.setIsRed(true);
			}
            model.setId(detail.getId());
            model.setCount(detail.getCount());
            Set<ColSupervisor> colSupervisors = detail.getColSupervisors();
            StringBuffer ids = new StringBuffer();
            if(colSupervisors != null && colSupervisors.size()>0) {
                for(ColSupervisor colSupervisor:colSupervisors) {
                    ids.append(colSupervisor.getSupervisorId() + ",");
                }
                model.setSupervisor(ids.substring(0, ids.length()-1));
            }
            model.setAwakeDate(detail.getAwakeDate());
            //model.setCanModify(detail.isCanModify());
            model.setSummaryId(detail.getEntityId());
            model.setContent(Strings.toHTML(detail.getDescription()));
            model.setStatus(detail.getStatus());

            if (StringUtils.isNotEmpty(caseProcessXML)) {
                hasWorkflow = true;
                processDescBy = FlowData.DESC_BY_XML;
            }
            model.setHasWorkflow(hasWorkflow);
            model.setProcessDescBy(processDescBy);
            // kuanghs 判断是类型公文还是协同
            model.setEntityType(detail.getEntityType());
            modelList.add(model);
        }
        //Pagination.setRowCount(modelList.size());
        return modelList;
    }
    private void sendMessageAndJob(int type,int importantLevel, String summarySubject,long userId,String userName,ColSuperviseDetail colSuperviseDetail,List<Long> deletedPerson, String forwardMemberIdStr) {
    	sendMessageAndJob(type, importantLevel, summarySubject, userId, userName, colSuperviseDetail, deletedPerson,  colSuperviseDetail.getColSupervisors(), forwardMemberIdStr);
    }
	private void sendMessageAndJob(int type,int importantLevel, String summarySubject,long userId,String userName,ColSuperviseDetail colSuperviseDetail,List<Long> deletedPerson,Set<ColSupervisor> addPeople,String forwardMemberIdStr) {
		Long summaryId = colSuperviseDetail.getEntityId();

		List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
		List<MessageReceiver> deleteReceivers = new ArrayList<MessageReceiver>();
    	ApplicationCategoryEnum app = ApplicationCategoryEnum.collaboration;
		String hastenType = "col.supervise.hasten";
		String deleteType = "col.supervise.delete";
		String linkType = "message.link.col.supervise";
    	if(type == Constant.superviseType.edoc.ordinal()){
    		app = ApplicationCategoryEnum.edoc;
    		hastenType = "edoc.supervise.hasten";
    		deleteType = "edoc.supervise.delete";
    		linkType = "message.link.edoc.supervise.detail";

    		try{
    		EdocManager edocManager = (EdocManager) ApplicationContextHolder.getBean("edocManager");
    		EdocSummary eSummary = edocManager.getEdocSummaryById(summaryId, false);
    		if(null!=eSummary){
    			if(eSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
    				app = ApplicationCategoryEnum.edocSend;
    			}else if(eSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
    				app = ApplicationCategoryEnum.edocRec;
    			}else{
    				app = ApplicationCategoryEnum.edocSign;
    			}
    		}
    		}catch(Exception e){
    			log.error("获得公文SUMMARY错误 " + e);
    		}
    	}
    	long colSuperviseId = colSuperviseDetail.getId();
    	StringBuffer supervisorMemberId = new StringBuffer();
    	MessageReceiver receiver = null;
    	for(ColSupervisor colSupervisor:addPeople) {
    		receiver = new MessageReceiver(colSuperviseId, colSupervisor.getSupervisorId(),linkType,summaryId);
    		receivers.add(receiver);
    		supervisorMemberId.append(colSupervisor.getSupervisorId() + ",");
    	}
    	if(deletedPerson != null) {
    		for(Long personId:deletedPerson) {
    			receiver = new MessageReceiver(colSuperviseDetail.getId(),personId);
    			deleteReceivers.add(receiver);
    		}
    	}
    	if(supervisorMemberId.length()>0)
    		supervisorMemberId.deleteCharAt(supervisorMemberId.length()-1);

    	int forwardMemberFlag = 0;
    	String forwardMember = null;
    	if(Strings.isNotBlank(forwardMemberIdStr)){
    		try {
    			forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberIdStr)).getName();
    			forwardMemberFlag = 1;
    		}
    		catch (Exception e) {
    		}
    	}
    	try {
    		if("col.supervise.hasten".equals(hastenType)){
    			userMessageManager.sendSystemMessage(new MessageContent(hastenType,summarySubject,userName, forwardMemberFlag, forwardMember), app, userId, receivers);
    		}
    		else{
    			userMessageManager.sendSystemMessage(new MessageContent(hastenType,summarySubject,userName,app.key()), app, userId, receivers);
    		}
    		if(deleteReceivers.size()>0){
    			if("col.supervise.delete".equals(deleteType)){
    				userMessageManager.sendSystemMessage(new MessageContent(deleteType,summarySubject,userName, forwardMemberFlag, forwardMember), app, userId, deleteReceivers);//给被删除的人发消息
    			}
    			else{
    				userMessageManager.sendSystemMessage(new MessageContent(deleteType,summarySubject,userName), app, userId, deleteReceivers);//给被删除的人发消息
    			}
    		}
    	}catch(MessageException e) {
    		log.error(e);
    	}

    	ColHelper.createQuarz4Supervise(summaryId, colSuperviseDetail, userId, supervisorMemberId.toString(), summarySubject);
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}


    public String[] changeProcess(String str, String[] idArr, String[] typeArr, String[] nameArr, String[] accountIdArr,
    		String[] accountShortNameArr, String[] selecteNodeIdArr, String[] _peopleArr, String summaryId,String[] condition,String[] nodes,boolean iscol, String[] userExcludeChildDepartmentArr){
    	User user = CurrentUser.get();
    	String[] strArr = str.split(",");
    	String processId = strArr[0];
    	String activityId = strArr[1];
    	String operationType = strArr[2];
    	String flowType = strArr[3];
    	String isShowShortName = strArr[4];
    	String desc_by = strArr[5];

    	FlowData flowData = new FlowData();
    	if(!"null".equals(desc_by) && !"null".equals(flowType)){
    		String _desc_by = FlowData.DESC_BY_XML.equals(desc_by) ? FlowData.DESC_BY_XML : FlowData.DESC_BY_PEOPLE;
    		flowData.setDesc_by(_desc_by);

    		String[] types = typeArr;
    		String[] ids = idArr;
    		String[] names = nameArr;
    		String[] accountIds = accountIdArr;
    		String[] userExcludeChildDepartments = userExcludeChildDepartmentArr;
    		String[] accountShortNames = accountShortNameArr;

    		if(names == null && ids != null && types != null){
    			names = FlowData.getElementNames(ids, types);
    		}

    		List<Party> people = new ArrayList<Party>();
    		if (ids != null) {
    			for (int i = 0; i < ids.length; i++) {
    				String id = ids[i];
    				String type = FlowData.getUserTypeByField(types[i] + "");
    				String name = names[i];
    				String accountId = accountIds[i];
    				String accountShortName = accountShortNames[i];

    				Party party = new Party(type, id, name, accountId, accountShortName);
    				if(userExcludeChildDepartments!=null && userExcludeChildDepartments.length>i && userExcludeChildDepartments[i]!=null && "true".equals(userExcludeChildDepartments[i]))
                    {    
                    	party.setIncludeChild(false);
                    }
    				if(iscol){
    					party.setSeeyonPolicy(new BPMSeeyonPolicy(BPMSeeyonPolicy.SEEYON_POLICY_COLLABORATE));
    				}else{
    					 switch(Integer.parseInt(flowType)){
	       				 case 0 :
	       				 case 1 :
	       				 case 2 :
		       				 party.setSeeyonPolicy(new BPMSeeyonPolicy(BPMSeeyonPolicy.EDOC_POLICY_SHENPI)) ;
		       				 break ;
	       				 case 3 :
	       					 BPMSeeyonPolicy policy = null ;
	       	                 if(flowData.getSeeyonPolicy() != null)
	       	                		policy = new BPMSeeyonPolicy(flowData.getSeeyonPolicy());
	       	                 else
	       	                		policy = new BPMSeeyonPolicy("huiqian","会签");
	       					 party.setSeeyonPolicy(policy) ;
	       				}
    				}
    				people.add(party);
    			}
    		}

    		flowData.setPeople(people);

    		int iFlowType = 0;
            switch(Integer.parseInt(flowType)){
	            case 0 : iFlowType = FlowData.FLOWTYPE_SERIAL;
	            break;
	            case 1 : iFlowType = FlowData.FLOWTYPE_PARALLEL;
	            break;
	            case 2 : iFlowType = FlowData.FLOWTYPE_MULTIPLE;
	            break;
	            case 3 : iFlowType = FlowData.FLOWTYPE_COLASSIGN;
            }
    		flowData.setType(iFlowType);
    		flowData.setIsShowShortName(isShowShortName);
    	}
    	if(nodes != null){
			Map<String,String> map = new HashMap<String,String>();
			for(int i=0;i<nodes.length;i++){
				if(!"".equals(nodes[i]) && !"".equals(condition[i]))
					map.put(nodes[i], condition[i]);
			}
			if(map.size()>0)
				flowData.setCondition(map);

		}
    	String[] xmlStr = new String[]{};
    	Long caseId = null;

    	if(iscol){
    		caseId = colManager.getSummaryByProcessId(processId).getCaseId();
    	}else{
    		caseId = edocSummaryManager.getSummaryByProcessId(processId).getCaseId();
    	}

    	try {
    		xmlStr = ColHelper.superviseUpdateProcess(processId, activityId, Integer.parseInt(operationType), flowData, null,
    				user, selecteNodeIdArr, _peopleArr, caseId);
    	} catch (BusinessException e) {
    		log.error("协同督办更新流程失败：changeProcess", e);
    	}
    	return xmlStr;
    }

    public String[] changeProcess1(String[] flowProp, String[] policyStr, String summaryId,boolean iscol){
    	String[] strArr = flowProp;
    	String processId = strArr[0];
    	String activityId = strArr[1];
    	String operationType = strArr[2];
    	BPMSeeyonPolicy policy = new BPMSeeyonPolicy();
    	String[] policyArr = policyStr;
    	policy.setId(policyArr[0]);
    	policy.setName(policyArr[1]);
    	policy.setdealTerm(policyArr[2]);
    	policy.setRemindTime(policyArr[3]);
    	policy.setProcessMode(policyArr[4]);
    	policy.setMatchScope(policyArr[5]);
    	policy.setDesc(policyArr[8]); 
    	policy.setDealTermType(policyArr[9]);
    	policy.setDealTermUserId(policyArr[10]);
    	policy.setDealTermUserName(policyArr[11]);

		FlowData flowData = new FlowData();
		String[] xmlStr = new String[]{};
		Long _summaryId = null;
		Long caseId = null;
		boolean isForm = false;
		if(iscol){
			ColSummary summary = colManager.getSummaryByProcessId(processId);
			_summaryId = summary.getId();
			caseId = summary.getCaseId();

			//表单绑定权限修改
			if("FORM".equals(summary.getBodyType())){
				policy.setForm(policyArr[6]);
		    	policy.setOperationName(policyArr[7]);
		    	isForm = true;
			}
		}else{
			EdocSummary summary = edocSummaryManager.getSummaryByProcessId(processId);
			_summaryId = summary.getId();
			caseId = summary.getCaseId();
		}
		try {
			xmlStr = ColHelper.superviseUpdateProcess(processId, activityId, Integer.parseInt(operationType), flowData,
					policy, CurrentUser.get(), null, null, caseId, isForm);
			 //更新affair中的node_policy,提前提醒时间,处理期限
			int deadlineDate=0;
			int remindDate=0;
			if( policyArr[2]!=null&&!"".equals( policyArr[2])) deadlineDate=Integer.parseInt(policyArr[2]);
			if( policyArr[3]!=null&&!"".equals(policyArr[3]))remindDate=Integer.parseInt(policyArr[3]);
			this.affairManager.updateAffairByObjectAndActivity(_summaryId, Long.parseLong(activityId), policy.getId(),deadlineDate,remindDate);
		} catch (BusinessException e) {
			log.error("协同督办更新流程失败：changeProcess", e);
		}
    	return xmlStr;
    }

    public void saveLog(long superviseId,long userId,List<Long> receivers,String content) {
    	if(receivers != null && receivers.isEmpty()){
    		return ;
    	}
    	// DB Log
    	colSuperviseDetailDao.saveDbLog(superviseId);
    	ColSuperviseLog superviseLog = new ColSuperviseLog();
    	superviseLog.setIdIfNew();
    	superviseLog.setSender(userId);
    	superviseLog.setSendTime(new Date());
    	superviseLog.setSuperviseId(superviseId);
    	superviseLog.setType(Constant.suerviseLogType.hasten.ordinal());
    	superviseLog.setContent(content);
    	Set<ColSuperviseReceiver> set = new HashSet<ColSuperviseReceiver>();
    	for(long receiverId:receivers) {
    		ColSuperviseReceiver receiver = new ColSuperviseReceiver();
    		receiver.setIdIfNew();
    		receiver.setLogId(superviseLog.getId());
    		receiver.setReceiver(receiverId);
    		set.add(receiver);
    	}

    	superviseLog.setReceivers(set);
    	colSuperviseDetailDao.save(superviseLog);
    }

    /**
     * 获取催办总次数
     * @param superviseId
     */
    public int getHastenTimes(long superviseId) {
    	return this.colSuperviseDetailDao.getHastenTimes(superviseId);
    }

    public List<ColSuperviseLog> getLogByDetailId(long superviseId){
    	return colSuperviseDetailDao.getLogByDetailId(superviseId);
    }

    public void changeAwakeDate(long superviseId,long userId,Date awakeDate,String summarySubject) {
    	ColSuperviseDetail detail = colSuperviseDetailDao.get(superviseId);
    	if(detail != null) {
    		String subject = summarySubject;
    		if(StringUtils.isEmpty(subject)) {
    			try {
    				ColSummary summary = colManager.getColSummaryById(detail.getEntityId(), false);
    				if(summary != null)
    					subject = summary.getSubject();
    			}catch(Exception e) {
    				log.error(e);
    			}
    		}

 			//--根据detail获取所有督办人的id,组成字符串传到后台
			StringBuilder ids = new StringBuilder();
			Set<ColSupervisor> s_list = detail.getColSupervisors();
			for(ColSupervisor s : s_list){
				ids.append(s.getSupervisorId()).append(",");
			}
			if(ids.toString().endsWith(",")){
				ids.deleteCharAt(ids.length()-1);
			}

			ColHelper.createQuarz4Supervise(detail.getEntityId(), detail, userId, ids.toString(), subject);

        	detail.setAwakeDate(awakeDate);
        	colSuperviseDetailDao.update(detail);
    	}
    }

    public ColSuperviseDetail get(long superviseId) {
    	return colSuperviseDetailDao.get(superviseId);
    }

    public void updateContent(long superviseId,String content) {
    	colSuperviseDetailDao.updateContent(superviseId,content);
    }

    /**
	 * 删除已办结督办
	 * @param userId
	 * @param superviseIds
	 */
    public void deleteSupervised(long userId,String superviseIds) {
    	if(superviseIds == null || "".equals(superviseIds))
    		return;
    	String[] ids = superviseIds.split(",");
    	if(ids != null) {
    		List<Long> longIds = new ArrayList<Long>();
    		for(String id:ids) {
    			longIds.add(Long.parseLong(id)) ;
    		}
    		Map<String, Object> nameParameters = new HashMap<String,Object>();
    		nameParameters.put("superviseIds", longIds);
    		colSuperviseDetailDao.deleteSupervised(userId,nameParameters);
    	}
    }

    public List<ColSuperviseDealModel> getAffairModel(long summaryId){
    	Map conditions = new HashMap();
    	conditions.put("app", ApplicationCategoryEnum.collaboration.key());
    	conditions.put("objectId", summaryId);
    	//conditions.put("state", StateEnum.col_done.key());
    	//conditions.put("isDelete", false);
    	List<Affair> affairs = affairManager.getByConditionsPagination(conditions);
    	if(affairs == null)
    		return null;
    	Metadata deadlineMeta = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
    	List<MetadataItem> itms = deadlineMeta.getItems();
    	Map<String,String> map = new HashMap<String,String>();
    	for(MetadataItem item:itms)
    		map.put(item.getValue(), item.getLabel());
    	String bundleName = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";;
    	String keyGood =  "col.supervise.dealline.good";
    	String keyBad = "col.supervise.dealline.bad";
    	List<ColSuperviseDealModel> models = new ArrayList<ColSuperviseDealModel>();
    	SeeyonPolicy seeyonPolicy = null;
    	String policyName = "";
    	for(Affair affair:affairs) {
    		ColSuperviseDealModel model = new ColSuperviseDealModel();
    		Date receiveDate = affair.getReceiveTime();
    		Date computeDate = affair.getCompleteTime();
    		Long deallineDate = affair.getDeadlineDate();
    		model.setDealUser(affair.getMemberId());
    		model.setReveiveDate(receiveDate);
    		model.setDealDate(computeDate);
    		model.setHastened(affair.getHastenTimes()==null?0:affair.getHastenTimes());
    		if(deallineDate != null)
    			model.setDealLine(map.get(deallineDate.toString()));
    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyGood));
    	    if(computeDate != null) {
    	    	long[] dates = Datetimes.detailInterval(receiveDate, computeDate);
    	    	model.setDealDays(ColHelper.timePatchwork(dates[0],dates[1],dates[2],dates[3],false));
    	    	if(deallineDate != null && deallineDate>0 && computeDate.getTime()-receiveDate.getTime()>deallineDate*60000) {
    	    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyBad));
    	    		model.setOverTime(true);
    	    	}
    	    }else {
    	    	Date today = new Date();
    	    	if(deallineDate != null && deallineDate>0 && today.getTime()-receiveDate.getTime()>deallineDate*60000) {
    	    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyBad));
    	    		model.setOverTime(true);
    	    	}
    	    }
    	    try {
    	    	seeyonPolicy = ColHelper.getPolicyByAffair(affair);
    	    	policyName = Constant.getCommonString("node.policy."+ColHelper.getPolicyByAffair(affair).getId());
    	    	if(policyName.length() > 12 && policyName.substring(0, 11).equals("node.policy")){
					policyName = seeyonPolicy.getName();
				}
    	    }catch(Exception e) {
    	    	policyName = Constant.getCommonString("node.policy.collaboration");
    	    }
    	    model.setPolicyName(policyName);
    	    models.add(model);
    	}
    	return models;
    }

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	/**
	 * 通过summaryId更新status
	 * @param summaryId
	 */
	public void updateStatusBySummaryId(long summaryId) {
		colSuperviseDetailDao.updateStatusBySummaryId(summaryId);
	}

	public ColSuperviseDetailDao getColSuperviseDetailDao() {
		return colSuperviseDetailDao;
	}

	public void setColSuperviseDetailDao(ColSuperviseDetailDao colSuperviseDetailDao) {
		this.colSuperviseDetailDao = colSuperviseDetailDao;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public List<SuperviseTemplateRole> findSuperviseRoleByTemplateId(long templateId){
		return colSuperviseTemplateRoleDao.findRoleByTemplateId(templateId);
	}

	/**
	 * @return the colSuperviseTemplateRoleDao
	 */
	public ColSuperviseTemplateRoleDao getColSuperviseTemplateRoleDao() {
		return colSuperviseTemplateRoleDao;
	}

	/**
	 * @param colSuperviseTemplateRoleDao the colSuperviseTemplateRoleDao to set
	 */
	public void setColSuperviseTemplateRoleDao(
			ColSuperviseTemplateRoleDao colSuperviseTemplateRoleDao) {
		this.colSuperviseTemplateRoleDao = colSuperviseTemplateRoleDao;
	}

	public void saveSuperviseTemplateRole(long templateId, String supervisors){
		String[] strs = supervisors.split(",");
		for(String str : strs){
			if(!Strings.isBlank(str)){
			SuperviseTemplateRole role = new SuperviseTemplateRole();
			role.setIdIfNew();
			role.setSuperviseTemplateId(templateId);
			role.setRole(str);
			colSuperviseTemplateRoleDao.save(role);
			}
		}
	}

	public void updateSuperviseTemplateRole(long templateId, String supervisors){
		colSuperviseTemplateRoleDao.deleteAllTemplateRole(templateId);
		if(supervisors != null && !"".equals(supervisors))
			this.saveSuperviseTemplateRole(templateId, supervisors);
	}

	public long saveForTemplate(ColSuperviseDetail detail,List<SuperviseTemplateRole> roles) {
		colSuperviseDetailDao.save(detail);
		if(roles != null) {
			for(SuperviseTemplateRole role:roles)
				colSuperviseTemplateRoleDao.save(role);
		}
		return detail.getId();
	}

	public void updateForTemplate(ColSuperviseDetail detail,List<SuperviseTemplateRole> roles) {
		colSuperviseDetailDao.delete(ColSupervisor.class, new Object[][]{{"superviseId", detail.getId()}});
		colSuperviseDetailDao.update(detail);
		colSuperviseTemplateRoleDao.deleteAllTemplateRole(detail.getEntityId());
		if(roles != null) {
			for(SuperviseTemplateRole role:roles)
				colSuperviseTemplateRoleDao.save(role);
		}
	}

	public void updateStatus(int importantLevel, String summarySubject,long userId,String userName,int entityType,long entityId,int status, String forwardMemberIdStr) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail != null) {
			detail.setStatus(status);
			this.sendMessageAndJob(entityType,importantLevel, summarySubject, userId, userName, detail, null, forwardMemberIdStr);
			this.colSuperviseDetailDao.update(detail);
		}
	}

	public void updateStatusAndNoticeSupervisor(long entityId,int entityType,ApplicationCategoryEnum app,String summarySubject,long userId,String userName,int status, String messageKey, String repealComment, String forwardMemberIdStr) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail != null) {
			if(Strings.isBlank(messageKey)){
			    messageKey = "col.cancel";
            }
			detail.setStatus(status);
			detail.setCount(0);
			detail.setDescription(null);
			detail.setScheduleProp(null);
			this.colSuperviseDetailDao.update(detail);
			this.colSuperviseDetailDao.deleteLogBySuperviseId(detail.getId());
			this.sendMessage(detail, app, summarySubject, messageKey, userId, userName, repealComment, forwardMemberIdStr);
		}
	}
	public void updateStatusAndNoticeSupervisorWithoutMes(long entityId,int entityType,int status) {
		ColSuperviseDetail detail = this.getSupervise(entityType, entityId);
		if(detail != null) {
		//	String messageKey = "col.cancel";
			detail.setStatus(status);
			detail.setCount(0);
			detail.setDescription(null);
			detail.setScheduleProp(null);
			this.colSuperviseDetailDao.update(detail);
			this.colSuperviseDetailDao.deleteLogBySuperviseId(detail.getId());
			//this.sendMessage(detail, app, summarySubject, messageKey, userId, userName);
		}
	}

	private void sendMessage(ColSuperviseDetail colSuperviseDetail,ApplicationCategoryEnum app,String summarySubject,String messageKey,long userId,String userName, String repealComment, String forwardMemberIdStr) {
		List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
    	Set<ColSupervisor> colSupervisors = colSuperviseDetail.getColSupervisors();
    	long colSuperviseId = colSuperviseDetail.getId();
    	MessageReceiver receiver = null;
    	for(ColSupervisor colSupervisor:colSupervisors) {
    		receiver = new MessageReceiver(colSuperviseId, colSupervisor.getSupervisorId());
    		receivers.add(receiver);
    	}
    	MessageContent msgContent = null;
    	/*if(Strings.isBlank(repealComment)){//只有回退和撤销流程调用此方法，这里回退加上了附言
    		msgContent = new MessageContent(messageKey, summarySubject, userName);
    	}
    	else{*/
		int forwardMemberFlag = 0;
		String forwardMember = null;
		if(Strings.isNotBlank(forwardMemberIdStr)){
			try {
				forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberIdStr)).getName();
				forwardMemberFlag = 1;
			}
			catch (Exception e) {
			}
		}
		if("col.cancel".equals(messageKey)){
			if(repealComment == null) repealComment = "";
			msgContent = new MessageContent(messageKey, summarySubject, userName, repealComment, forwardMemberFlag, forwardMember);
		}
		else if("col.stepback".equals(messageKey)){
        	int messageFlag = 0;
        	if(Strings.isNotBlank(repealComment)){
        		messageFlag = 1 ;
        	}
			msgContent = new MessageContent(messageKey, summarySubject, userName, forwardMemberFlag, forwardMember,repealComment,messageFlag);
		}
		else{
			msgContent = new MessageContent(messageKey, summarySubject, userName, repealComment);
		}
    	//}
    	try {
    		userMessageManager.sendSystemMessage(msgContent, app, userId, receivers);
    	}catch(MessageException e) {
    		log.error(e);
    	}
	}

	public void deleteSupervisorsBySupervisorIdAndDetailId(long detailId, long supervisorId){
		try{
			colSupervisorDao.deleteSupervisorsBySupervisorIdAndDetailId(detailId, supervisorId);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @return the colSupervisorDao
	 */
	public ColSupervisorDao getColSupervisorDao() {
		return colSupervisorDao;
	}

	/**
	 * @param colSupervisorDao the colSupervisorDao to set
	 */
	public void setColSupervisorDao(ColSupervisorDao colSupervisorDao) {
		this.colSupervisorDao = colSupervisorDao;
	}

	public int countMySupervise(long userId,int status ,Integer... superviseType){
		List<ColSuperviseDetail> list = colSuperviseDetailDao.getColSuperviseDetailListInMySupervise(userId,status,superviseType);
		if(list == null)
			return 0;
		List<ColSuperviseModel> mList = encapsulationList(list);
		return mList.size();
		//return colSuperviseDetailDao.getMySuperviseCount(userId, status, superviseType);
	}

	public String checkColSupervisor(Long summaryId){
		Affair senderAffair = this.affairManager.getCollaborationSenderAffair(summaryId);
		if(senderAffair == null || senderAffair.getState() == StateEnum.col_pending.key()){
			String m = Constant.getString4CurrentUser("col.delete.non.supervise");

			return m;
		}

		Long userId = CurrentUser.get().getId();
		boolean currentUserIdSupervisor = false;
		ColSuperviseDetail detail = this.getSupervise(Constant.superviseType.summary.ordinal(), summaryId);
		if(detail != null){
			Set<ColSupervisor> supervisors = detail.getColSupervisors();
			if(supervisors != null && !supervisors.isEmpty()){
				for (ColSupervisor supervisor : supervisors){
					if(userId.equals(supervisor.getSupervisorId())){
						currentUserIdSupervisor = true;
						break;
					}
				}
			}
		}

		if(!currentUserIdSupervisor){
			return Constant.getString4CurrentUser("col.delete.non.supervise.nome");
		}

		return null;
	}

	public String checkColSupervisor(Long summaryId, Affair senderAffair){
        if(senderAffair == null || senderAffair.getState() == StateEnum.col_waitSend.key()){
        	String m = Constant.getString4CurrentUser("col.delete.non.supervise");
        	return m;
        }

    	boolean currentUserIdSupervisor = false;
		ColSuperviseDetail detail = this.getSupervise(Constant.superviseType.summary.ordinal(), summaryId);
		if(detail != null){
			Set<ColSupervisor> supervisors = detail.getColSupervisors();
			Long userId = CurrentUser.get().getId();
			if(supervisors != null && !supervisors.isEmpty()){
				for (ColSupervisor supervisor : supervisors){
					if(userId.equals(supervisor.getSupervisorId())){
						currentUserIdSupervisor = true;
						break;
					}
		    	}
			}
		}

		if(!currentUserIdSupervisor){
			return Constant.getString4CurrentUser("col.delete.non.supervise.nome");
		}

		return null;
	}

    /* (non-Javadoc)
     * @see com.seeyon.v3x.collaboration.manager.ColSuperviseManager#copyAndSaveSuperviseFromTemplete()
     */
    public boolean copyAndSaveSuperviseFromTemplete(V3xOrgMember sender, ColSummary newSummary, Long templeteId) throws Exception{
//      private packingSuperviseFromTemplete
        ColSuperviseDetail detail = this.getSupervise(Constant.superviseType.template.ordinal(), templeteId);
        if(detail != null) {
            Date superviseDate = null;
            Long terminalDate = detail.getTemplateDateTerminal();
            if(null!=terminalDate){
                superviseDate = Datetimes.addDate(new Date(), terminalDate.intValue());
            }else if(detail.getAwakeDate() != null) {
                superviseDate = detail.getAwakeDate();
            }
            Set<ColSupervisor> supervisors = detail.getColSupervisors();
            Set<Long> sIdSet = new HashSet<Long>();
            for(ColSupervisor supervisor:supervisors){
                sIdSet.add(supervisor.getSupervisorId());
            }
            List<SuperviseTemplateRole> roleList = this.findSuperviseRoleByTemplateId(templeteId);
            V3xOrgRole orgRole = null;
            for(SuperviseTemplateRole role : roleList){
                if(null==role.getRole() || "".equals(role.getRole())){
                    continue;
                }
                if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase())){
                    sIdSet.add(sender.getId());
                }
                if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())){
                    orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, sender.getOrgAccountId());
                    if(null!=orgRole){
                        List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(sender.getId());
                        for(V3xOrgDepartment dep : depList){
                            List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
                            for(V3xOrgMember mem : managerList){
                                sIdSet.add(mem.getId());
                            }
                        }
                    }
                }
                if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER.toLowerCase())){
                    orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER, sender.getOrgAccountId());
                    if(null!=orgRole){
                        List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(sender.getId());
                        for(V3xOrgDepartment dep : depList){
                        List<V3xOrgMember> superManagerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
                            for(V3xOrgMember mem : superManagerList){
                                sIdSet.add(mem.getId());
                            }
                        }
                    }
                }
            }
            if(!sIdSet.isEmpty()){
                long[] ids = new long[sIdSet.size()];
                StringBuffer nameBf = new StringBuffer();
                int i = 0;
                for (Long id : sIdSet) {
                    V3xOrgMember mem = orgManager.getMemberById(id);
                    if(mem!=null){
                        ids[i++] = id;
                        if(nameBf.length() > 0){
                            nameBf.append(",");
                        }
                        nameBf.append(mem.getName());
                    }
                }
                this.save(newSummary.getImportantLevel(), newSummary.getSubject(), detail.getTitle(), sender.getId(), sender.getName(), nameBf.toString(), ids, superviseDate, Constant.superviseType.summary.ordinal(), newSummary.getId(), Constant.superviseState.supervising.ordinal(), true, newSummary.getForwardMember());
                return true;
            }
        }
        return false;
    }
    public List<Affair> getAffairByStatus(Long memberId, int state,int firstResult,int maxResults,List<Integer> entityType )
    {
    	return colSuperviseDetailDao.getAffairByStatus(memberId, state, firstResult, maxResults, entityType);
    }
    public void deleteSuperviseById(Long superviseId) {
        // TODO Auto-generated method stub
        colSuperviseDetailDao.deleteSupervised(superviseId);
    }
	public boolean isSupervisor(Long userId, Long summaryId) {
		return colSuperviseDetailDao.isSupervisor(userId, summaryId);
	}

	public List<ColSuperviseLog> getColSuperviseLogBySummaryId(long summaryId)
	{
		String hql = "select a from "+ColSuperviseDetail.class.getName()+" a where a.entityId=:summaryId";
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("summaryId", summaryId);

        List<ColSuperviseDetail> list = searchManager.searchByHql(hql, parameterMap,false);
        List<ColSuperviseLog> logList = null ;
        if (list.size() > 0) {
        	ColSuperviseDetail detail = list.get(0);
     		logList = this.getLogByDetailId(detail.getId());
        }
        return logList;
	}
	
	public ColSuperviseDetail getSuperviseDetailByEntityId(Long entityId) {
		return colSuperviseDetailDao.getSuperviseDetailByEntityId(entityId);
	}
	public Object getSuperviseModelList(long userId, int status, Map<String,List<String>> queryCondition, List<Integer> entityType, int maxCount,boolean isCount){
		return colSuperviseDetailDao.getSuperviseModelList(userId, status, queryCondition, entityType, maxCount,isCount);
	}
}