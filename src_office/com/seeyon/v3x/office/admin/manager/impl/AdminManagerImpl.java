package com.seeyon.v3x.office.admin.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.office.admin.dao.AdminSettingDAO;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.asset.dao.AssetApplyInfoDAO;
import com.seeyon.v3x.office.asset.dao.AssetInfoDAO;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.util.Constants;
import com.seeyon.v3x.office.book.dao.BookApplyInfoDAO;
import com.seeyon.v3x.office.book.domain.TBookApplyinfo;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;


public class AdminManagerImpl implements AdminManager {
	
	private static final Log log = LogFactory.getLog(AdminManagerImpl.class);
	
	private AdminSettingDAO adminSettingDAO;
	private OrgManager orgManager;
	private AssetInfoDAO assetInfoDAO;
	private BookApplyInfoDAO bookApplyDao ;
	private OrgManagerDirect orgDirect;
	private AssetApplyInfoDAO assetApplyDao;
	public void setAssetInfoDAO(AssetInfoDAO assetInfoDAO) {
		this.assetInfoDAO = assetInfoDAO;
	}

	public void setOrgDirect(OrgManagerDirect orgDirect) {
		this.orgDirect = orgDirect;
	}

	public AdminSettingDAO getAdminSettingDAO() {
		return adminSettingDAO;
	}

	public void setAdminSettingDAO(AdminSettingDAO adminSettingDAO) {
		this.adminSettingDAO = adminSettingDAO;
	}
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	
	public List findAdminSetting(Long domainId,String field,String fieldValue) {
		return this.adminSettingDAO.listAdminSetting(domainId,field,fieldValue);
	}

	public MAdminSetting getAdminSettingById(MAdminSettingId id) {
		return this.adminSettingDAO.load(id);
	}

	public int getAdminSettingCount(String sql,Map map) {
		return this.adminSettingDAO.getCount(sql,map);
	}

	public void saveAdminSetting(MAdminSetting admin) {
		this.adminSettingDAO.save(admin);
	}

	public void updateAdminSetting(MAdminSetting admin) {
		this.adminSettingDAO.update(admin);
	}
	
	
	public void handOverOffice(int officeType, Long nowAdminid, Long domainId) {
		switch(officeType){
		case OfficeModelType.auto_type:
			this.adminSettingDAO.updateAutoManager(nowAdminid,  domainId);
			break;
		case OfficeModelType.asset_type:
			this.adminSettingDAO.updateAssetManager(nowAdminid,  domainId);
			break;
		case OfficeModelType.stock_type:
			this.adminSettingDAO.updateStockManager(nowAdminid,  domainId);
			break;
		case OfficeModelType.book_type:
			this.adminSettingDAO.updateBookManager(nowAdminid,  domainId);
			break;
		case OfficeModelType.meeting_type:
			this.adminSettingDAO.updateMeetingManager(nowAdminid, domainId);
			break;
		}
	}
	
	public void updateAdminSetting(MAdminSetting admin, MAdminSettingId id)throws Exception{
		MAdminSetting temp = null;
		try{
			temp = this.getAdminSettingById(admin.getId());
			temp.getCreateDate();
			throw new Exception();
		}catch(org.hibernate.ObjectNotFoundException ex){
			this.deleteAdminSetting(this.getAdminSettingById(id));
			this.saveAdminSetting(admin);
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public void deleteAdminSetting(MAdminSetting admin){
		this.adminSettingDAO.delete(admin);
	}
	
	//专门为综合办公的修改增加的方法（用原有的delete会造成一个session中有两个相同标识的不同实体）
	public void deleteAdminSettingForUpdate(MAdminSetting admin) {
		this.adminSettingDAO.deleteForUpdate(admin);
	}
	/**
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return int 
     */
	public int checkAdmin(long memberId, int model){
		String likeStr = "";
		switch(model){
			case 1:{
				likeStr = "1____";break;
			}
			case 2:{
				likeStr = "_1___";break;
			}
			case 3:{
				likeStr = "__1__";break;
			}
			case 4:{
				likeStr = "___1_";break;
			}
//          =============================CaoFei 2008 - 9 - 17 Meeting Management add admin_model like '____1'========================================
			case 5:{
				likeStr = "____1";break;
			}
		}
		List list = adminSettingDAO.listAdminSettingById(null, memberId, null, likeStr, false);
		if(list != null && list.size() > 0){
			return 1;
		}
		return 0;
	}
	
	/**
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return String 
     */
	public String getDepartAdmins(long accountId, long departmentId, int model)throws Exception{
		String str = "1";
		List list = this.orgManager.getMembersByDepartment(departmentId, false);
		String likeSql = "";
		switch(model){
		case 1:
			likeSql = "1____";break;
		case 2:
			likeSql = "_1___";break;
		case 3:
			likeSql = "__1__";break;
		case 4:
			likeSql = "___1_";break;
//          =============================CaoFei 2008 - 9 - 17 Meeting Management add '____1'========================================
		case 5:
			likeSql = "____1";break;
		}
		
		List adminList = adminSettingDAO.listAdminSettingById(accountId, null, departmentId, likeSql, false);
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				V3xOrgMember member = (V3xOrgMember)list.get(i);
				boolean isAdmin = false;
				inside:for(int j = 0; j < adminList.size(); j++){
					MAdminSetting admin = (MAdminSetting)adminList.get(j);
					if(admin.getId().getAdmin().longValue() == member.getId()){
						isAdmin = true;
						break inside;
					}
				}
				if(isAdmin){
					if(str.length() > 0){
						str += ",";
					}
					str += member.getId();
				}
			}
		}
		return str;
	}
	
	public String getInfoIds(long memberId, int model){
		String str = "1";
		List list = null;
		if(model == 2){
			list = assetApplyDao.listAssetApplyByIds(memberId);
		}else if(model == 3){
			list = bookApplyDao.listBookApplyByIds(memberId);
		}
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				long id = 0;
				if(model == 2){
					TAssetApplyinfo apply = (TAssetApplyinfo)list.get(i);
					id = apply.getApplyId();
				}else if(model == 3){
					TBookApplyinfo apply = (TBookApplyinfo)list.get(i);
					id = apply.getApplyId();
				}
				if(str.length() > 0){
					str += ",";
				}
				str += String.valueOf(id);
			}
		}
		return str;
	}

	public List getAdminDepartments(long adminId, String adminModel) {
		List list = adminSettingDAO.listAdminSettingById(null, adminId, null, adminModel, true);
		ArrayList arr = new ArrayList();
		if(list != null){
			for(int i = 0; i < list.size(); i++){
				MAdminSetting admin = (MAdminSetting)list.get(i);
				try{
					String mngDeps = admin.getId().getMngdepId();
					//增加综合办公单位管理权限职能
					//start - 2008-12-10 -feicao
					String doMainId = String.valueOf(admin.getDomainId());
					String TempDep = null;
					if((mngDeps!=null&&doMainId!=null)&&(mngDeps.equals(doMainId))){
						V3xOrgAccount account = this.orgManager.getAccountById(Long.parseLong(mngDeps));
						TempDep = account.getName();
					}
					else{
						V3xOrgDepartment department = this.orgManager.getDepartmentById(Long.parseLong(mngDeps));
						/* branches_a8_v350sp1_r_gov 政务向凡 修改， 如果ID在部门为NULL时，就从单位查询  Start*/
						if(department == null){
							V3xOrgAccount account = this.orgManager.getAccountById(Long.parseLong(mngDeps));
							TempDep = account.getName();
						}else {
							TempDep = department.getName();
						}
						/* branches_a8_v350sp1_r_gov 政务向凡 修改， 如果ID在部门为NULL时，就从单位查询  End*/
					}
					//end
					arr.add(TempDep);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		return arr;
	}

	/**
	 * 判断当前用户是否具有管理权限
     * @author caofei 2008-9-17
     * @description  Comprehensive Office Building ---[add Meeting Management update]
     * @param request
     * @param response
     * @return boolean 
     */
	public boolean hasAdminInUse(long userId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mge", userId);
		map.put("deleteFlag", 0);
		String sql = "select count(*) as "+Constants.Total_Count_Field + " from m_asset_info where asset_mge=:mge  and del_flag =:deleteFlag ";
		int count = this.assetInfoDAO.getCount(sql,map);
		if(count>0)
			return true;
		sql = "select count(*) as "+Constants.Total_Count_Field + " from m_auto_info where auto_mge=:mge  and del_flag =:deleteFlag ";
		count = this.assetInfoDAO.getCount(sql,map);
		if(count>0)
			return true;
		sql = "select count(*) as "+Constants.Total_Count_Field + " from m_book_info where book_mge=:mge  and del_flag =:deleteFlag ";
		count = this.assetInfoDAO.getCount(sql,map);
		if(count>0)
			return true;
		sql = "select count(*) as "+Constants.Total_Count_Field + " from m_stock_info where stock_res=:mge  and del_flag =:deleteFlag ";
		count = this.assetInfoDAO.getCount(sql,map);
//      =============================CaoFei 2008 - 9 - 17 Meeting Management add meeting sql check ========================================
		/*if(count>0)
			return true;
		sql = "select count(*) as "+Constants.Total_Count_Field + " from m_stock_info where stock_res=" + userId +" and del_flag = '0'";
		count = this.assetInfoDAO.getCount(sql);*/
		
		if(count>0)
			return true;
		return false;
	}
	
	public List getAdminSettingByModelAdmin(String model, Long domainId) {
		return adminSettingDAO.findAdminSettingByModel(model, domainId);
	}
	
	public Object[] getAdminManageDepartment(Long adminId,Long accountId ,String model)throws BusinessException {
		return this.getAdminManageDepartments(adminId, accountId, model).toArray();
	}
	
	public List<Long> getAdminManageDepartments(Long adminId,Long accountId ,String model)throws BusinessException {
		List<MAdminSetting> adminSettingList = this.adminSettingDAO.findAdminManageDepartment(adminId, accountId, model);
		Set<Long> departmentId = new HashSet<Long>();
		Long depId;
		for(MAdminSetting adminSetting:adminSettingList){
			depId = Long.parseLong(adminSetting.getId().getMngdepId());
			//如果管理权限是单位
			if(depId == adminSetting.getDomainId()){
				List<V3xOrgDepartment> allDepartment = orgManager.getAllDepartments(adminSetting.getDomainId());
				for(V3xOrgDepartment dep : allDepartment){
					departmentId.add(dep.getId());
				}
				departmentId.add(depId);
			}else{
				//如果不是单位管理权限 那么取得部门 和 子部门
				departmentId.add(depId);
				List<V3xOrgDepartment> childDeparmentList = orgManager.getChildDepartments(depId, true);
				for(V3xOrgDepartment dep : childDeparmentList){
					departmentId.add(dep.getId());
				}
			}
		}
		return new ArrayList<Long>(departmentId);
	}
	
	public List<V3xOrgMember> getOutCntMemberByDepartment(Object[] depIds, Boolean filter, Long accountId) {
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		if(depIds == null || depIds.length == 0){
			return members;
		}
		for(Object dep : depIds){
			try {
				//副职
				List<V3xOrgMember> member = orgDirect.getMembersByDepartment((Long)dep, true, V3xOrgEntity.ORGREL_TYPE_MEMBER_POST, accountId);
				members.addAll(member);
				//兼职
				List<V3xOrgMember> cntMember = orgDirect.getMembersByDepartment((Long)dep, true, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST, accountId);
				members.addAll(cntMember);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
		return members;
	}
	
	public Object[] getMemberDepProxy(V3xOrgMember member,Long accountId,Long adminId,String model,List<Long> departmentId) throws BusinessException{
		Object[] depAndProxy = new Object[2];
		String departmentName = "";
		boolean proxy = false;
		log.info("auto...member.getId()="+member.getId());
		log.info("auto...member.getName()="+member.getName());
		log.info("auto...member.getOrgDepartmentId()="+member.getOrgDepartmentId());
		if(!member.getOrgAccountId().equals(accountId)){
        	Map<String, String> map = Functions.getPluralityInfo4User(member.getId().longValue(), accountId);
        	departmentName = map.get("departmentSimpleName");
        	proxy = true;
        }else{
        	//List<Long> departmentId = getAdminManageDepartments(adminId, accountId, model);
        	if(departmentId.contains(member.getOrgDepartmentId())){
        		 V3xOrgDepartment department = this.orgManager.getDepartmentById(member.getOrgDepartmentId());
	             if (department != null){
	                 departmentName = department.getName();
	             }
        	}else{
        		//得到副职
        		V3xOrgMember hrMember = orgManager.getMemberById(member.getId());
        		List<MemberPost> secondPost = hrMember.getSecond_post();
        		log.info("auto...secondPost="+secondPost);
        		for(MemberPost post : secondPost){
        			if(departmentId.contains(post.getDepId())){
        				V3xOrgDepartment department = orgManager.getDepartmentById(post.getDepId());
        				departmentName = department.getName();
        				log.info("auto...departmentName="+departmentName);
        				proxy = true;
        				break;
        			}
        		}
        	}
        }
		if(Strings.isBlank(departmentName)){
			V3xOrgDepartment department = orgManager.getDepartmentById(member.getOrgDepartmentId());
			if( department!=null ){
				departmentName = department.getName();
			}else{
				departmentName = "";
			}
		}
		depAndProxy[0] = departmentName;
		depAndProxy[1] = proxy	;
		return depAndProxy ;
	}
	
	public List getAdminSettingById(Long domainId, Long admin, Long depId, String adminModel, Boolean modelEqual) {
		return adminSettingDAO.listAdminSettingById(domainId, admin, depId, adminModel, modelEqual);
	}
	
	public boolean checkAdmin(Long id){
		return adminSettingDAO.checkAdmin(id);
	}
	
	public boolean checkAdmin(Long userId, Long loginAccountId) {
		return adminSettingDAO.checkAdmin(userId, loginAccountId);
	}
	
	public List getMyAdmin(List departmentId) {
		return adminSettingDAO.getMyAdmin(departmentId);
	}

	public void setBookApplyDao(BookApplyInfoDAO bookApplyDao) {
		this.bookApplyDao = bookApplyDao;
	}

	public void setAssetApplyDao(AssetApplyInfoDAO assetApplyDao) {
		this.assetApplyDao = assetApplyDao;
	}

	//branches_a8_v350_r_gov 向凡 添加接口，查询具体模块下的信息
	@Override
	public List<MAdminSetting> findAdminSetting(String model, Long domainId, String field, String keyword) {
		return this.adminSettingDAO.listAdminSetting(model, domainId, field, keyword);
	}
	
}