package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTransition;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.dao.StaffInfoDao;
import com.seeyon.v3x.hr.dao.StaffTransferDao;
import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;
import com.seeyon.v3x.hr.log.StaffTransferLog;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
public class StaffTransferManagerImpl implements StaffTransferManager {

	private StaffTransferDao staffTransferDao;
	private StaffInfoDao staffInfoDao;
	private OperationlogManager operationlogManager;
	private OrgManagerDirect orgManagerDirect;
	private ColManager colManager;
	private SearchManager searchManager;
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public StaffInfoDao getStaffInfoDao() {
		return staffInfoDao;
	}

	public void setStaffInfoDao(StaffInfoDao staffInfoDao) {
		this.staffInfoDao = staffInfoDao;
	}

	public StaffTransferDao getStaffTransferDao() {
		return staffTransferDao;
	}

	public void setStaffTransferDao(StaffTransferDao staffTransferDao) {
		this.staffTransferDao = staffTransferDao;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}
	
	public SearchManager getSearchManager() {
		return searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	/**
	 * 获得所有调配记录
	 * 
	 */
	public List<StaffTransfer> getStaffTransfer() throws Exception {
		return staffTransferDao.getStaffTransfer();
	}

	/**
	 * 添加调配记录
	 * 
	 */
	public void addTransfer(StaffTransfer staffTransfer)throws Exception{
		staffTransfer.setIdIfNew();
		staffTransferDao.save(staffTransfer);
		
		//插入调配日志
		insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.add.desc");
	}

	/**
	 * 更新调配记录
	 * 
	 */
	public void updateTransfer(StaffTransfer staffTransfer) throws Exception {
		//插入调配日志
		insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.update.desc");
		
		staffTransferDao.update(staffTransfer);
	}
	
	/**
	 * 插入调配日志
	 * @param staffTransfer
	 * @param bundleName
	 * @throws BusinessException
	 */
	private void insertTransferLog(StaffTransfer staffTransfer, String bundleName) throws Exception {
		//取得调配类型
		StaffTransferType staffTransferType = new StaffTransferType(staffTransferDao.getStaffTransferType(staffTransfer.getType().getId()));
		
		//生成日志格式
		StaffTransferLog log = new StaffTransferLog();
		log.setStaffName(orgManagerDirect.getMemberById(staffTransfer.getMember_id()).getName());
		log.setStaffTransferType(staffTransferType);
		//插入业务日志
		operationlogManager.insertOplog(staffTransfer.getId(),
				com.seeyon.v3x.hr.util.Constants.MODULE_TRANSFER,
				ApplicationCategoryEnum.hr, 
				staffTransferType.getType_name(),
				bundleName, 
				log
		);		
	}

	/**
	 * 根据id查询调配记录
	 * 
	 */
	public StaffTransfer getStaffTransferById(Long id) throws Exception {
		return staffTransferDao.get(id.longValue());
	}

//	/**
//	 * 根据姓名查询调配记录
//	 * 
//	 */
//	public List<StaffTransfer> getStaffTransferByName(String name)
//			throws Exception {
//		List<V3xOrgMember> members = OrganizationHelper.searchMember("name", name, searchManager, orgManagerDirect, true);
//		List<Long> staffids = new ArrayList();
//		for(V3xOrgMember member :members){
//			staffids.add(member.getId());
//		}
//		return staffTransferDao.getStaffTransferByMemberId(staffids);
//	}

	public List<Object[]> getStaffTransferLikeByName(String match, String fname)
			throws Exception {
		return staffTransferDao.findStaffTransferLikeByName(match, fname);
	}

	/**
	 * 查询变动类型为调配的记录
	 * 
	 */
	public List<StaffTransfer> getTransferTypeStaffTransfer() throws Exception {
		return staffTransferDao.getTransferTypeStaffTransfer();
	}

	/**
	 * 查询变动类型为离职的记录
	 * 
	 */
	public List<StaffTransfer> getDimissionTypeStaffTransfer() throws Exception {
		return staffTransferDao.getDimissionTypeStaffTransfer();
	}
	
	public List<Object[]> getStaffTransferByType(int transferType, String fname)throws Exception {
		return staffTransferDao.getStaffTransferByType(transferType, fname);
	}

	/**
	 * 根据状态查询调配记录
	 * 
	 */
	public List<Object[]> getStaffTransferByState(int state, String fname)
			throws Exception {
		return staffTransferDao.getStaffTransferByState(state, fname);
	}

	/**
	 * 根据提交时间查询调配记录
	 * 
	 */
	public List<Object[]> getStaffTransferByReferTime(Date referTime, String fname)
			throws Exception {
		return staffTransferDao.getStaffTransferByReferTime(referTime, fname);
	}

	/**
	 * 删除一条调配记录
	 * 
	 */
	public void deleteTransfer(Long id) throws Exception {
		StaffTransfer staffTransfer = this.staffTransferDao.get(id.longValue());
		insertTransferLog(staffTransfer, "hr.staffTransfer.transfer.delete.desc");
		staffTransferDao.deleteTransfer(id.longValue());
	}
	
	
	/*---------------------------------------- 2007-09-12 ---------------------------------------------*/
	/**
	 * 根据调配类型id得到StaffTransferType对象
	 * 
	 */
	public StaffTransferType getStaffTransferTypeById(int id)throws Exception{
		return staffTransferDao.getStaffTransferType(id);
	}	
	
	/**
	 * 查询所有待处理调配表单
	 * 
	 */
	public List<Object[]> getFormByName(String fname)throws Exception{
		return staffTransferDao.getFormByName(fname);
	}
	
	/**
	 * 根据id在表单动态表中查询一条调配信息
	 * 
	 */
	public Object[] getFormItemById(String fname,Long id)throws Exception{
		return staffTransferDao.getFormItemById(fname, id);
	}
	
	/**
	 * 根据id在表单动态表中删除一条调配信息
	 * 
	 */
	public void deleteFormItemById(String fname,Long id,String name)throws Exception{
		staffTransferDao.deleteFormItemById(fname, id);
		//插入业务日志
		operationlogManager.insertOplog(id,
				com.seeyon.v3x.hr.util.Constants.MODULE_TRANSFER,
				ApplicationCategoryEnum.hr, 
				"delete",
				"hr.staffTransfer.transferlog.delete.label", 
				name
		);	
	}
	
	/**
	 * 处理一条调配信息
	 * 
	 */
	public void dealFormItemById(String fname,Long id)throws Exception{
		Object[] obj = this.getFormItemById(fname, id);
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.valueOf(obj[21].toString()));
		member.setOrgDepartmentId(Long.valueOf(obj[3].toString()));
		member.setOrgLevelId(Long.valueOf(obj[7].toString()));
		member.setOrgPostId(Long.valueOf(obj[6].toString()));
		member.setType(Byte.valueOf(obj[16].toString()));

		member.setState(Byte.valueOf(obj[12].toString()));
		
		orgManagerDirect.updateEntity(member);
		staffTransferDao.updateFormItemState(fname, id);
		

		//插入业务日志
		operationlogManager.insertOplog(id,
				com.seeyon.v3x.hr.util.Constants.MODULE_TRANSFER,
				ApplicationCategoryEnum.hr, 
				"deal",
				"hr.staffTransfer.transferlog.deal.label", 
				member.getName()
		);	
	}

	/**
	 * 获取表单显示的XML
	 * 
	 */
	public String getFormXMLById(Long formid, String fname)throws Exception{
		ColSummary summary = staffTransferDao.getColSummaryByFormId(formid,fname);
		Long caseId = summary.getCaseId();
		BPMProcess process = ColHelper.getRunningProcessByCaseId(caseId);
		List endList = process.getEnds();
		BPMStatus end = (BPMStatus)endList.get(0);
		List trans = end.getUpTransitions();
		BPMAbstractNode activity = ((BPMTransition)trans.get(0)).getFrom();
		BPMSeeyonPolicy policy = activity.getSeeyonPolicy();

		User user = CurrentUser.get();
		String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), policy.getFormApp(), policy.getForm(), policy.getOperationName(), formid.toString(), summary.getId().toString(), "111", policy.getName(),true);
		
		return formContent;
	}
}