package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.hr.dao.StaffInfoDao;
import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
	

public class StaffInfoManagerImpl implements StaffInfoManager {
	private transient static final Log LOG = LogFactory.getLog(StaffInfoManagerImpl.class);
	private StaffInfoDao staffInfoDao;
	private FileManager fileManager;
	private AttachmentManager attachmentManager;
	private OrgManager orgManager;
    private OrgManagerDirect orgManagerDirect;
    private OperationlogManager operationlogManager;
    
	/**
	 * StaffInfo缓存 &lt;memberId, StaffInfo&gt;
	 */
	private static Map<Long, StaffInfo> StaffInfoByMemberIdCache = new ConcurrentHashMap<Long, StaffInfo>();
	
	/**
	 * 加载StaffInfo信息到缓存中
	 */
	public void init(){
		long time1 = System.currentTimeMillis();
		
		List<StaffInfo> staffInfos = staffInfoDao.getValidStaffInfos();
		if(CollectionUtils.isNotEmpty(staffInfos)) {
			for (StaffInfo staffInfo : staffInfos) {
				StaffInfoByMemberIdCache.put(staffInfo.getOrg_member_id(), staffInfo);
			}
			LOG.info("职员信息加载完成，耗时：" + (System.currentTimeMillis() - time1) + "MS");
		}
		
	}
	
	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	
	public StaffInfoDao getStaffInfoDao() {
		return staffInfoDao;
	}

	public void setStaffInfoDao(StaffInfoDao staffInfoDao) {
		this.staffInfoDao = staffInfoDao;
	}
	public OrgManager getOrgManager() {
		return orgManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	
	/**
	 * 方法描述：根据人员ID查询该人员基本信息
	 * 
	 * @return StaffInsfo
	 * @throws Exception
	 *
	 */
	public StaffInfo getStaffInfoById(Long memberId)throws Exception{
		StaffInfo staffInfo = StaffInfoByMemberIdCache.get(memberId);
		if(staffInfo == null){
			staffInfo =  this.getStaffInfoByIdFromDB(memberId);
			this.syncCache(staffInfo, null, Constants.ActionType.create);
		}
		
		return  staffInfo;
	}
	
	/**
	 * 直接从数据库中取对应记录，配合集群同步缓存之用
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public StaffInfo getStaffInfoByIdFromDB(Long memberId) throws Exception {
		return staffInfoDao.getStaffInfoById(memberId);
	}
	
	/**
	 * 根据人员ID查询该人员的联系信息
	 * 
	 */
	public ContactInfo getContactInfoById(Long staffid) throws Exception{
		return staffInfoDao.getContactInfoByStafferId(staffid.longValue());
	}
	
	public Map<Long, ContactInfo> getAllContactInfo() throws Exception {
		return staffInfoDao.getAllContactInfo();
	}
	
	/**
	 * 根据人员ID查询该人员家庭成员与社会关系信息
	 * 
	 */
	public List<Relationship> getRelationshipByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getRelationshipByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据人员ID查询该人员工作履历
	 * 
	 */
	public List<WorkRecord> getWorkRecordByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getWorkRecordByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据人员ID查询该人员教育培训经历
	 * 
	 */
	public List<EduExperience> getEduExperienceByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getEduExperienceByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据人员ID查询该人员职务变动
	 * 
	 */
	public List<PostChange> getPostChangeByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getPostChangeByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据人员ID查询该人员考核情况
	 * 
	 */
	public List<Assess> getAssessByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getAssessByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据人员ID查询该人员奖惩档案
	 * 
	 */
	public List<RewardsAndPunishment> getRewardsAndPunishmentByStafferId(Long staffid)throws Exception{
		return staffInfoDao.getRewardsAndPunishmentByStafferId(staffid.longValue());
	}
	
	/**
	 * 根据id查询一条家庭成员与社会关系信息记录
	 * 
	 */
	public Relationship getRelationshipById(Long id)throws Exception{
		return staffInfoDao.getRelationshipById(id.longValue());
	}
	
	/**
	 * 根据id查询一条工作履历记录
	 * 
	 */
	public WorkRecord getWorkRecordById(Long id)throws Exception{
		return staffInfoDao.getWorkRecordById(id.longValue());
	}
	
	/**
	 * 根据id查询一条教育培训经历记录
	 * 
	 */
	public EduExperience getEduExperienceById(Long id)throws Exception{
		return staffInfoDao.getEduExperienceById(id.longValue());
	}
	
	/**
	 * 根据id查询一条职务变动记录
	 * 
	 */
	public PostChange getPostChangeById(Long id)throws Exception{
		return staffInfoDao.getPostChangeById(id.longValue());
	}
	
	/**
	 * 根据id查询一条考核情况记录
	 * 
	 */
	public Assess getAssessById(Long id)throws Exception{
		return staffInfoDao.getAssessById(id.longValue());
	}
	
	/**
	 * 根据id查询一条奖惩档案记录
	 * 
	 */
	public RewardsAndPunishment getRewardsAndPunishmentById(Long id)throws Exception{
		return staffInfoDao.getRewardsAndPunishmentById(id.longValue());
	}

	/**
	 * 添加人员信息(只在导入时调用,不保存照片、附件信息)
	 * 
	 */
	public void addStaffInfo(StaffInfo staffinfo)throws Exception{
		staffinfo.setIdIfNew();
		staffInfoDao.save(staffinfo);
		syncCache(staffinfo, null, Constants.ActionType.create);
	}
	
	/**
	 * 添加人员信息
	 * 
	 */
	public void addStaffInfo(HttpServletRequest request,StaffInfo staffinfo)throws Exception{
		try{
			Map<String, V3XFile> v3xFiles=fileManager.uploadFiles(request,"gif,jpg,jpeg,bmp,png", Long.valueOf("102400"));
			if(null!=v3xFiles && !v3xFiles.isEmpty()){
				Iterator<String> it = v3xFiles.keySet().iterator();
				if (it.hasNext()) {
					String key = it.next();
		            V3XFile file = (V3XFile)v3xFiles.get(key);
		            if(file != null && StringUtils.isNotBlank(file.getFilename())){
		            	fileManager.save(file);
						staffinfo.setImage_id(file.getId());
						staffinfo.setImage_datetime(file.getCreateDate());
						staffinfo.setImage_name(file.getFilename());	
		            }
				}
			}
			attachmentManager.create(ApplicationCategoryEnum.hr, staffinfo.getId() , staffinfo.getId(), request);
			
			this.addStaffInfo(staffinfo);
		}
		catch(Exception e){
			LOG.error("", e);
		}
	}
	public void addStaffInfo(HttpServletRequest request,StaffInfo staffinfo,V3xOrgMember member)throws Exception{
		this.addStaffInfo(request, staffinfo);
        
		orgManagerDirect.addMember(member);
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.operation.add.label",
				"hr.staffInfo.info.add.label", 
				member.getName());
	}
	
	/**
	 * 增删改职员信息时，同步缓存与数据库内容，使之保持一致
	 * @param staffinfo		职员信息，可能为null
	 * @param memberId		职员ID，可能为null
	 * @param action		操作类型：CRUD之一
	 */
	public void syncCache(StaffInfo staffinfo, Long memberId, Constants.ActionType action) {
		switch(action) {
		case create :
		case update :
			if(staffinfo != null) {
				StaffInfoByMemberIdCache.put(staffinfo.getOrg_member_id(), staffinfo);
				//发送缓存同步通知
				NotificationManager.getInstance().send(NotificationType.HrAddOrUpdateStaffInfo, staffinfo.getOrg_member_id());
			}
			break;
		case delete :
			if(memberId != null) {
				StaffInfoByMemberIdCache.remove(memberId);
				//发送缓存同步通知
				NotificationManager.getInstance().send(NotificationType.HrDeleteStaffInfo, memberId);
			}
			break;
		}
	}
	
	/**
	 * 更新人员信息(只在导入时调用,照片、附件信息不更新)
     *   
	 */
	public void updateStaffInfo(StaffInfo staffinfo)throws Exception{
		List<StaffInfo> l = new ArrayList<StaffInfo>(1);
		l.add(staffinfo);
		staffInfoDao.updatePatchAll(l);//.update(staffinfo);
		syncCache(staffinfo, null, Constants.ActionType.update);
	}
	
	/**
	 * 更新人员信息
     *   
	 */
	public void updateStaffInfo(HttpServletRequest request,StaffInfo staffinfo)throws Exception{
		try{
			Map<String, V3XFile> v3xFiles=fileManager.uploadFiles(request, "gif,jpg,jpeg,bmp,png", Long.valueOf("204800"));
			if(null!=v3xFiles && !v3xFiles.isEmpty()){
				Long imageId = staffinfo.getImage_id();				
				Iterator<String> it = v3xFiles.keySet().iterator();
				V3XFile file = new V3XFile();
				String key="";
				if (it.hasNext()) {
		            key=it.next();
		            file = (V3XFile)v3xFiles.get(key);
		            if(null!=file.getFilename() && !file.getFilename().equals("")){
		            	if(null!=imageId && !imageId.equals("")){
		            		try {
		            			fileManager.deleteFile(imageId, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
		            	fileManager.save(file);
						staffinfo.setImage_id(file.getId());
						staffinfo.setImage_datetime(file.getCreateDate());
						staffinfo.setImage_name(file.getFilename());	
		            }
				}	
			}
			attachmentManager.deleteByReference(staffinfo.getId(), staffinfo.getId());
			attachmentManager.create(ApplicationCategoryEnum.hr, staffinfo.getId()
					, staffinfo.getId(), request);
			
			this.updateStaffInfo(staffinfo);
		}
		catch(Exception e){
			LOG.error("", e);
		}
	}
	
	public void updateStaffInfo(HttpServletRequest request,StaffInfo staffinfo,V3xOrgMember member,boolean isNewStaffer)throws Exception{
		if(isNewStaffer==true){
			this.addStaffInfo(request,staffinfo);
		}
		else{
			this.updateStaffInfo(request,staffinfo);
		}
		orgManagerDirect.updateEntity(member);
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.operation.update.label",
				"hr.staffInfo.info.update.label", 
				member.getName());
	}
	
	/**
	 * 更新联系信息
	 * 
	 */
	public void updateContactInfo(ContactInfo contactInfo,V3xOrgMember member)throws Exception{
		orgManagerDirect.updateEntity(member);		
		staffInfoDao.update(contactInfo);
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.ContactInfo.update.label", 
				member.getName());
	}
	
	/**
	 * 添加联系信息
	 * 
	 */
	public void addContactInfo(ContactInfo contactInfo,V3xOrgMember member)throws Exception{
		contactInfo.setIdIfNew();
		orgManagerDirect.updateEntity(member);
		staffInfoDao.save(contactInfo);
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.ContactInfo.add.label", 
				member.getName());
	}
	
	/**
	 * 更新家庭成员与社会关系信息
	 * 
	 */
	public void updateRelationship(Relationship relationship)throws Exception{
		staffInfoDao.update(relationship);
		V3xOrgMember member = orgManagerDirect.getMemberById(relationship.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.Relationship.update.label",
				member.getName());
	}
	
	/**
	 * 添加家庭成员与社会关系信息
	 * 
	 */
	public void addRelationship(Relationship relationship)throws Exception{
		relationship.setIdIfNew();
		staffInfoDao.save(relationship);
		V3xOrgMember member = orgManagerDirect.getMemberById(relationship.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.Relationship.add.label", 
				member.getName());
	}
	
	/**
	 * 更新工作履历
	 * 
	 */
	public void updateWorkRecord(WorkRecord workRecord)throws Exception{
		staffInfoDao.update(workRecord);
		V3xOrgMember member = orgManagerDirect.getMemberById(workRecord.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.WorkRecord.update.label", 
				member.getName());
	}
	
	/**
	 * 添加工作履历
	 * 
	 */
	public void addWorkRecord(WorkRecord workRecord)throws Exception{
		workRecord.setIdIfNew();
		staffInfoDao.save(workRecord);
		V3xOrgMember member = orgManagerDirect.getMemberById(workRecord.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.WorkRecord.add.label", 
				member.getName());
	}
	
	/**
	 * 更新教育培训经历
	 * 
	 */
	public void updateEduExperience(EduExperience eduExperience)throws Exception{
		staffInfoDao.update(eduExperience);
		V3xOrgMember member = orgManagerDirect.getMemberById(eduExperience.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.EduExperience.update.label", 
				member.getName());
	}
	
	/**
	 * 添加教育培训经历
	 * 
	 */
	public void addEduExperience(EduExperience eduExperience)throws Exception{
		eduExperience.setIdIfNew();
		staffInfoDao.save(eduExperience);
		V3xOrgMember member = orgManagerDirect.getMemberById(eduExperience.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.EduExperience.add.label", 
				member.getName());
	}
	
	/**
	 * 更新职务变动记录
	 * 
	 */
	public void updatePostChange(PostChange postChange)throws Exception{
		staffInfoDao.update(postChange);
		V3xOrgMember member = orgManagerDirect.getMemberById(postChange.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.PostChange.update.label", 
				member.getName());
	}
	
	/**
	 * 添加职务变动记录
	 * 
	 */
	public void addPostChange(PostChange postChange)throws Exception{
		postChange.setIdIfNew();
		staffInfoDao.save(postChange);
		V3xOrgMember member = orgManagerDirect.getMemberById(postChange.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.PostChange.add.label", 
				member.getName());
	}
	
	/**
	 * 更新考核情况记录
	 * 
	 */
	public void updateAssess(Assess assess)throws Exception{
		staffInfoDao.update(assess);
		V3xOrgMember member = orgManagerDirect.getMemberById(assess.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.Assess.update.label", 
				member.getName());
	}
	
	/**
	 * 添加考核情况记录
	 * 
	 */
	public void addAssess(Assess assess)throws Exception{
		assess.setIdIfNew();
		staffInfoDao.save(assess);
		V3xOrgMember member = orgManagerDirect.getMemberById(assess.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.Assess.add.label", 
				member.getName());
	}
	
	/**
	 * 更新奖惩档案
	 * 
	 */
	public void updateRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)throws Exception{
		staffInfoDao.update(rewardsAndPunishment);
		V3xOrgMember member = orgManagerDirect.getMemberById(rewardsAndPunishment.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.update.label",
				"hr.staffInfo.RewardsAndPunishment.update.label", 
				member.getName());
	}
	
	/**
	 * 添加奖惩档案
	 * 
	 */
	public void addRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)throws Exception{
		rewardsAndPunishment.setIdIfNew();
		staffInfoDao.save(rewardsAndPunishment);
		V3xOrgMember member = orgManagerDirect.getMemberById(rewardsAndPunishment.getMember_id());
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.other.add.label",
				"hr.staffInfo.RewardsAndPunishment.add.label", 
				member.getName());
	}
	
	/**
	 * 删除某人员的信息
	 * 
	 */
	public void deleteStaffInfo(Long staffid)throws Exception{
		long staffId = staffid.longValue();				
		StaffInfo staffinfo = this.getStaffInfoById(staffid);
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		
		if(null != staffinfo){
			Long imageId = staffinfo.getImage_id();
			if(null != imageId) {
				fileManager.deleteFile(imageId, true);
			}
		}
		operationlogManager.insertOplog(member.getOrgAccountId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
				ApplicationCategoryEnum.hr, 
				"hr.staffInfo.operation.delete.label",
				"hr.staffInfo.info.delete.label", 
				member.getName());
		orgManagerDirect.deleteEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, staffid);
		if(null!=staffinfo){
		   attachmentManager.deleteByReference(staffinfo.getId());
		}
		staffInfoDao.deleteStaffInfo(staffId);
		staffInfoDao.deleteContactInfoByStaffId(staffid);
		staffInfoDao.deleteRelationshipByStaffId(staffId);
		staffInfoDao.deleteWorkRecordByStaffId(staffId);
		staffInfoDao.deleteEduExperienceByStaffId(staffId);
		staffInfoDao.deletePostChangeByStaffId(staffId);
		staffInfoDao.deleteAssessByStaffId(staffId);
		staffInfoDao.deleteRewardsAndPunishmentByStaffId(staffId);
		
		syncCache(null, staffid, Constants.ActionType.delete);
	}
	
	/**
	 * 删除一条家庭成员与社会关系信息
	 * 
	 */
	public void deleteRelationship(Long id)throws Exception{
		staffInfoDao.deleteRelationship(id.longValue());
	}
	
	
	/**
	 * 删除一条工作履历
	 * 
	 */
	public void deleteWorkRecord(Long id)throws Exception{
		staffInfoDao.deleteWorkRecord(id.longValue());
	}
	
	/**
	 * 删除一条教育培训经历
	 * 
	 */
	public void deleteEduExperience(Long id)throws Exception{
		staffInfoDao.deleteEduExperience(id.longValue());
	}
	
	
	/**
	 * 删除一条职务变动记录
	 * 
	 */
	public void deletePostChange(Long id)throws Exception{
		staffInfoDao.deletePostChange(id.longValue());
	}
	
	
	/**
	 * 删除一条考核情况记录
	 * 
	 */
	public void deleteAssess(Long id)throws Exception{
		staffInfoDao.deleteAssess(id.longValue());
	}
	
	
	/**
	 * 删除一条奖惩档案
	 * 
	 */
	public void deleteRewardsAndPunishment(Long id)throws Exception{
		staffInfoDao.deleteRewardsAndPunishment(id.longValue());
	}
	public Map<Long, StaffInfo> getStaffInfos(List<Long> staffidList) throws Exception {
		return staffInfoDao.getStaffInfos(staffidList);
	}
}
