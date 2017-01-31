package com.seeyon.v3x.organization.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.domain.BulTypeManagers;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.collaboration.domain.ManagementSetAcl;
import com.seeyon.v3x.collaboration.manager.WorkStatManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.common.CustomOrgRole;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocAlertManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.flowperm.util.FlowPermHelper;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexExtPropertiesConfig;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.inquiry.domain.InquiryAuthority;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.domain.SecurityMember;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.domain.NewsTypeManagers;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.organization.dao.OrgCommonDao;
import com.seeyon.v3x.organization.dao.OrgHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CallbackAddInitialData;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.manager.OrganizationMessage;
import com.seeyon.v3x.organization.manager.OrganizationMessage.OrgMessage;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.webmail.util.DateUtil;

public class OrganizationServicesImpl implements OrganizationServices,IndexEnable {

	private OrgManagerDirect orgManagerDirect;		
	private BbsBoardManager bbsBoardManager;
	private BulTypeManager bulTypeManager;	
	private DocLibManager docLibManager;	
	private BlogManager blogManager;	
	private AffairManager affairManager;
	private MenuManager menuManager;
	private OrgManager orgManager;
	private DocHierarchyManager docHierarchyManager;
	private DocAlertManager docAlertManager;
	private DocAclManager docAclManager;
	private InquiryManager inquiryManager;
	private NewsTypeManager newsTypeManager;
	private AdminManager officeAdminManager;
	private SignetManager signetManager;
	private TempleteConfigManager templeteConfigManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private PrincipalManager principalManager;
	private WorkStatManager workStatManager;
	private StaffInfoManager staffInfoManager;
	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public WorkStatManager getWorkStatManager() {
		return workStatManager;
	}

	public void setWorkStatManager(WorkStatManager workStatManager) {
		this.workStatManager = workStatManager;
	}

	private static final Log log = LogFactory.getLog(OrganizationServicesImpl.class);
	
	public void addAccount(V3xOrgAccount account) throws BusinessException {
		if(account.getCode()==null){
			throw new BusinessException("error add account for null code");
		}else if(account.getName()==null){
			throw new BusinessException("error add account for null name");
		}else if(account.getShortname()==null){
			throw new BusinessException("error add account for null short name");
		}else{				
			//单位重名的校验
			if(orgManagerDirect.getAccountByName(account.getName())!=null)
				throw new BusinessException("error add account for same name already existed");
			//单位编码重复的校验
			List accountList = orgManagerDirect.getEntityList(V3xOrgAccount.class.getSimpleName(), "code", account.getCode(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			if(accountList!=null&&accountList.size()!=0)
				throw new BusinessException("error add account for same code already existed");
			
			//设置单位的排序号
			Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgAccount.class.getSimpleName(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			account.setSortId(maxSortNum+1);
			account.setAccessPermission(V3xOrgEntity.ACCOUNT_ACC_ALL);
			account.setAdminName(account.getCode()+"-admin");
			setLoadData(false);
			//增加单位
			orgManagerDirect.addAccount(account);
			//更新单位
			orgManagerDirect.updateEntity(account);
			//增加管理员
			V3xOrgMember member = new V3xOrgMember();
			String adminNameValue = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources","org.account_form.adminName.value", "");
			member.setLoginName(account.getCode()+"-admin");
			member.setPassword(V3xOrgEntity.DEFAULT_PASSWORD);
			member.setName(adminNameValue);
			member.setIsAdmin(true);
			member.setOrgAccountId(account.getId());
			member.setOrgDepartmentId(account.getId());
			orgManagerDirect.addMember(member);				
            //增加单位文档
			try{				
				docLibManager.addSysDocLibs(account.getId());				
			}catch(Exception ex){
				log.error("error add account doc lib!",ex);
			}
			//添加公文及协同的节点权限
			try{
				FlowPermHelper.generateFlowPermByAccountId(account.getId());
			}catch(Exception ex){
				log.error("error add account flow perm!",ex);
			}
			//复制一套公文单
			try{
				EdocHelper.generateEdocFormByAccountId(account.getId());
			}catch(Exception ex){
				log.error("error add account edoc!",ex);
			}
			setLoadData(true);
			//添加插件角色
			try {
				CustomOrgRole.getInstance().writeAccountData(account.getId());
			}catch(Exception ex){
				log.error("error add account plugin role!",ex);
			}
			
		}		
	}

	public void addDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException {
		if(dept.getCode()==null){
			throw new BusinessException("添加部门出错:部门编码为空");
		}else if(dept.getName()==null){
			throw new BusinessException("添加部门出错:部门名称为空");
		}else if(orgManagerDirect.getAccountById(dept.getOrgAccountId())==null){
			throw new BusinessException("添加部门出错:部门所在单位为空");
		}else if(parentId==null){
			throw new BusinessException("添加部门出错:上级组织id为空");
		}else{
			//校验父部门
			V3xOrgDepartment parentDept = orgManagerDirect.getDepartmentById(parentId);	
			V3xOrgAccount account=null;
			if(parentDept==null){
				account = orgManagerDirect.getAccountById(parentId);
				if(account==null){
					throw new BusinessException("添加部门出错:上级组织为空");
				}				
			}
			//同级部门重名的校验
			List<V3xOrgDepartment> depts = orgManagerDirect.getChildDepartments(parentId, true);
			for(V3xOrgDepartment orgDept:depts){
				if(orgDept.getName().equals(dept.getName())){
					throw new BusinessException("添加部门出错:同一级上已经存在相同名称的部门"+":"+dept.getName()+(parentDept==null?account.getName():parentDept.getName()));
				}
			}			
			//获得最大排序号
			Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgDepartment.class.getSimpleName(), dept.getOrgAccountId());
			dept.setSortId(maxSortNum+1);		

			dept.init((CallbackAddInitialData)orgManagerDirect);
			
			//设置部门路径
//			String fPath = V3xOrgEntity.ORGACCOUNT_PATH;
//			if (!parentId.equals(dept.getOrgAccountId())) {
//				V3xOrgDepartment pDep = orgManagerDirect.getDepartmentById(parentId);
//				if (pDep == null)
//					throw new BusinessException("父部门的ID错误");
//				fPath = pDep.getPath();
//			}
//			dept.setPath(OrgHelper.getDepartmentPath(fPath,orgManagerDirect.getChildDepartments(parentId, true)));		
			orgManagerDirect.addDepartment(dept,parentId);			
		}

	}

	public void addMember(V3xOrgMember member) throws BusinessException {
		if(member.getCode()==null){
			throw new BusinessException("error add member for null code");
		}else if(member.getName()==null){
			throw new BusinessException("error add member for null name");
		}else if(orgManagerDirect.getAccountById(member.getOrgAccountId())==null){
			throw new BusinessException("error add member for null account");
		}else{
			//人员密码为空时设置默认密码
			if(StringUtils.isEmpty(member.getPassword()))
				member.setPassword(V3xOrgEntity.DEFAULT_PASSWORD);
			//人员帐号重复验证
			V3xOrgMember orgMem = orgManagerDirect.getMemberByLoginName(member.getLoginName());
			if(orgMem!=null)
				throw new BusinessException("error add member for the same loginname already existed!");
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
			V3xOrgPost post = orgManagerDirect.getPostById(member.getOrgPostId()); 
			V3xOrgLevel level = orgManagerDirect.getLevelById(member.getOrgLevelId());
			if(dept==null){
				member.setEnabled(false);
				member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null department");
			}
			if(post==null){
				// member.setEnabled(false);
				member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null post");
			}
			if(level==null){
				// member.setEnabled(false);
				member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null level");
			}
			//获得当前排序号的最大值
			Integer maxSortNum = orgManagerDirect.getMaxMemberSortNum(member.getOrgAccountId());
			member.setSortId(maxSortNum+1);
			orgManagerDirect.addMember(member);
			// 添加个人文档库
			try {
				docLibManager.addDocLib(member.getId());
			} catch (Exception e) {
				log.error("error add member doc lib!",e);
			}			
			//添加个人博客记录
			try {
				blogManager.createEmployee(member.getId(), member.getOrgAccountId());
			} catch (Exception e) {
				log.error("error add member blog!",e);
			}
            //添加个人菜单权限
			try {
				List<Long> securityIdsList = new ArrayList<Long>();
				List<Security> defaultSecurities = this.menuManager.getDefaultSecurities();
				for(Security security : defaultSecurities){
					securityIdsList.add(security.getId());					
				}
				if(!securityIdsList.isEmpty())
					menuManager.saveMemberSecurity(member.getId(), member.getOrgAccountId(), securityIdsList);
				else{
					log.error("error find the system menu list.");
				}					
			} catch (Exception e){
				log.error("error add member menu!",e);
			}

		}
		
	}
	
	public void addPost(V3xOrgPost post) throws BusinessException {
		if(post.getCode()==null){
			throw new BusinessException("error add post for null code");
		}else if(post.getName()==null){
			throw new BusinessException("error add post for null name");
		}else if(orgManagerDirect.getAccountById(post.getOrgAccountId())==null){
			throw new BusinessException("error add post for null account");
		}else{
			//获得当前排序号的最大值
			Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgPost.class.getSimpleName(), post.getOrgAccountId());
			post.setSortId(maxSortNum+1);
			orgManagerDirect.addPost(post);			
		}
	} 
	
	public void addLevel(V3xOrgLevel level) throws BusinessException {
		if(level.getCode()==null){
			throw new BusinessException("error add level for null code");
		}else if(level.getName()==null){
			throw new BusinessException("error add level for null name");
		}else if(orgManagerDirect.getAccountById(level.getOrgAccountId())==null){
			throw new BusinessException("error add level for null account");
		}else{
            //职务级别不存在序号见缝插针
            if(level.getLevelId()==null){
                int i=1;
                List<V3xOrgLevel> listLevel=orgManagerDirect.getAllLevels(level.getOrgAccountId(), false);
                if(listLevel==null||listLevel.isEmpty())
                {
                    level.setLevelId(1);
                }
                for (V3xOrgLevel level2 : listLevel) {
                    if (level2.getLevelId()!= i)
                        i++;
                    if (level2.getLevelId()!= i)
                    {
                        level.setLevelId(i);
                        break;
                    }
                }
                if(level.getLevelId()==null)
                {
                    level.setLevelId(i+1);
                }
            }
			//获得当前排序号的最大值
			Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgLevel.class.getSimpleName(), level.getOrgAccountId());
			level.setSortId(maxSortNum+1);
			orgManagerDirect.addLevel(level);
		}
	}

	public void delAccount(Long accountId) throws BusinessException {
		V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);
		if(account!=null){
			orgManagerDirect.deleteAccount(account);
		}else{
			throw new BusinessException("error delete account for null account");
		}		
	}

	public void delDepartment(Long deptId) throws BusinessException {
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		if(dept!=null){
			//检查部门下是否存在成员
			List<V3xOrgMember> members = orgManagerDirect.getMembersByDepartment(dept.getId(), false, dept.getOrgAccountId());
			boolean isAllMemberUnEnabled = true;						
			for(V3xOrgMember mem:members){
				if(mem.getEnabled()){
					isAllMemberUnEnabled = false;
				}
			}if(ListUtils.EMPTY_LIST.equals(members)||isAllMemberUnEnabled){
				//检查部门下是否存在组
				List<V3xOrgTeam> teams = orgManagerDirect.getDepartmentTeam(deptId);
				List<V3xOrgDepartment> childDeps = orgManagerDirect.getChildDepartments(deptId, false);
				for(V3xOrgDepartment child:childDeps){
					teams.addAll(orgManagerDirect.getDepartmentTeam(child.getId()));
				}
				boolean isAllTeamUnEnabled = true;						
				for(V3xOrgTeam team:teams){
					if(team.getEnabled()){
						isAllTeamUnEnabled = false;
					}
				}
				if(ListUtils.EMPTY_LIST.equals(teams)||isAllTeamUnEnabled){
					orgManagerDirect.deleteEntity(dept);	
					try {
						//删除部门空间的讨论
						this.bbsBoardManager.deleteV3xBbsBoard(deptId);
						//删除部门空间的公告
						bulTypeManager.delDept(deptId);						
					} catch (Exception ex) {
						throw new BusinessException("error del department for space");
					}		
				}else{
	                //部门下有组，不能删除
					throw new BusinessException("error del department for teams");
				}
			}else{
				// 部门下有有效成员，不能删除
				throw new BusinessException("error del department for members");
			}						
		}else{
			throw new BusinessException("error del department for null department");
		}	
	}

	public void delMember(Long memberId) throws BusinessException {		
		V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
		if(member!=null){
			Integer[] apps = new Integer[2];
			apps[0] = ApplicationCategoryEnum.collaboration.key();
			apps[1] = ApplicationCategoryEnum.edoc.key();
			if(affairManager.hasPending(apps, memberId)){
				throw new BusinessException("error del member for affair");
			}else{
				orgManagerDirect.deleteEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId);	
			}			
		}else{
			throw new BusinessException("error del member for null member");
		}

	}
	
	public void delPost(Long postId) throws BusinessException {		
		V3xOrgPost post = orgManagerDirect.getPostById(postId);
		if(post!=null){
            //判断岗位下是否有成员存在
			List<V3xOrgMember> members = orgManagerDirect.getMembersByPost(postId);
			boolean isAllMemberUnEnabled = true;					
			for(V3xOrgEntity mem:members){
				if(((V3xOrgMember)mem).getEnabled()){
					isAllMemberUnEnabled = false;
				}
			}					
			if(ListUtils.EMPTY_LIST.equals(members)||isAllMemberUnEnabled){
				orgManagerDirect.deleteEntity(post);
			}else{
				throw new BusinessException("error del post for members");
			}			
		}else{
			throw new BusinessException("error del post for null post");
		}		
	}
	
	public void delLevel(Long levelId) throws BusinessException {
		V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
		if(level!=null){
			//判断职务级别下是否有成员存在
			List<V3xOrgEntity> members = orgManagerDirect.getEntityList(V3xOrgMember.class.getSimpleName(), "orgLevelId", String.valueOf(levelId), level.getOrgAccountId());
			boolean isAllMemberUnEnabled = true;					
			for(V3xOrgEntity mem:members){
				if(((V3xOrgMember)mem).getEnabled()){
					isAllMemberUnEnabled = false;
				}
			}					
			if(ListUtils.EMPTY_LIST.equals(members)||isAllMemberUnEnabled){
				orgManagerDirect.deleteEntity(level);
			}else{
				throw new BusinessException("error del level for members");
			}				
		}else{
			throw new BusinessException("error del level for null level");
		}
	}

	public void updateAccount(V3xOrgAccount account) throws BusinessException {
		if(account.getCode()==null){
			throw new BusinessException("error update account for null code");
		}else if(account.getName()==null){
			throw new BusinessException("error update account for null name");
		}else if(account.getShortname()==null){
			throw new BusinessException("error update account for null short name");
		}else{
			//单位重名的校验
			V3xOrgAccount orgAccount = orgManagerDirect.getAccountByName(account.getName());
			if(orgAccount!=null&&!(orgAccount.getId()).equals(account.getId()))
				throw new BusinessException("error update account for same name already existed");
			//单位编码重复的校验
			List accountList = orgManagerDirect.getEntityList(V3xOrgAccount.class.getSimpleName(), "code", account.getCode(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			if(accountList!=null&&accountList.size()!=0){
				for(Object orgEntity : accountList){
					if(!((V3xOrgEntity)orgEntity).getId().equals(account.getId())){
						throw new BusinessException("error update account for same code already existed");
					}
				}
			}	
			//如果单位无插件角色则插入
			try {
				CustomOrgRole.getInstance().writeAccountData(account.getId());
			}catch(Exception ex){
				log.error("error add account plugin role!",ex);
			}
			//orgManagerDirect.updateEntity(account);
		}		
	}

	public void updateDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException {
		
		if(dept.getCode()==null){
			throw new BusinessException("修改部门出错:部门编码为空");
		}else if(dept.getName()==null){
			throw new BusinessException("修改部门出错:部门名称为空");
		}else if(orgManagerDirect.getAccountById(dept.getOrgAccountId())==null){
			throw new BusinessException("修改部门出错:部门所在单位为空");
		}else if(parentId==null){
			throw new BusinessException("修改部门出错:上级组织id为空");
		}else{
			//校验父部门
			V3xOrgDepartment parentDept = orgManagerDirect.getDepartmentById(parentId);			
			if(parentDept==null){
				V3xOrgAccount account = orgManagerDirect.getAccountById(parentId);
				if(account==null){
					throw new BusinessException("修改部门出错:上级组织为空");
				}				
			}
			//同级部门重名的校验
			List<V3xOrgDepartment> depts = orgManagerDirect.getChildDepartments(parentId, true);
			for(V3xOrgDepartment orgDept:depts){
				if(orgDept.getName().equals(dept.getName())&&!orgDept.getId().equals(dept.getId())){
					throw new BusinessException("修改部门出错:同一级上已经存在相同名称的部门");
				}
			}
			
			//更新父部门
			V3xOrgDepartment orgParent = orgManagerDirect.getParentDepartment(dept.getId());
			if(orgParent==null){
				if(orgManagerDirect.getDepartmentById(parentId)!=null){
					orgManagerDirect.setDepPath(dept, parentId);
				}else{//tanglh
					orgManagerDirect.updateEntity(dept);
				}
			}else{
		        Long orgParentId = orgParent.getId();
				if(parentId!=null){
					if(!orgParentId.equals(parentId)){
						orgManagerDirect.setDepPath(dept, parentId);
					}else{//tanglh
						orgManagerDirect.updateEntity(dept);
					}
				}else{//tanglh
					orgManagerDirect.updateEntity(dept);
				}
			}
		}
	}
	
	public void updateDepartment(V3xOrgDepartment dept) throws BusinessException {
		orgManagerDirect.updateEntity(dept);
	}

	public void updateMember(V3xOrgMember member) throws BusinessException {
		if(member.getCode()==null){
			throw new BusinessException("error update member for null code");
		}else if(member.getName()==null){
			throw new BusinessException("error update member for null name");
		}else if(orgManagerDirect.getAccountById(member.getOrgAccountId())==null){
			throw new BusinessException("error update member for null account");
		}else{
			//人员密码为空时设置默认密码
			if(StringUtils.isEmpty(member.getPassword()))
				member.setPassword(V3xOrgEntity.DEFAULT_PASSWORD);
			//人员帐号重复验证
			V3xOrgMember orgMem = orgManagerDirect.getMemberByLoginName(member.getLoginName());
			if(orgMem!=null&&!orgMem.getId().equals(member.getId()))
				throw new BusinessException("error update member for the same loginname already existed!");
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
			V3xOrgPost post = orgManagerDirect.getPostById(member.getOrgPostId()); 
			V3xOrgLevel level = orgManagerDirect.getLevelById(member.getOrgLevelId());
			if(dept==null){
				member.setEnabled(false);
				member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null department");
			}
			if(post==null){
				// member.setEnabled(false);
				member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null post");
			}
			if(level==null){
				// member.setEnabled(false);
				member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null level");
			}
			orgManagerDirect.updateEntity(member);
		}		
	}
	
	public void updatePost(V3xOrgPost post) throws BusinessException {
		if(post.getCode()==null){
			throw new BusinessException("error update post for null code");
		}else if(post.getName()==null){
			throw new BusinessException("error update post for null name");
		}else if(orgManagerDirect.getAccountById(post.getOrgAccountId())==null){
			throw new BusinessException("error update post for null account");
		}else{
			orgManagerDirect.updateEntity(post);
		}		
	}
	
	public void addUserCurrentPost(List<ConcurrentPost> currentPosts) throws BusinessException {
		if(currentPosts!=null){
			long zero = Long.parseLong("0000");
			for(ConcurrentPost cntPost:currentPosts){
				V3xOrgRelationship rel = new V3xOrgRelationship();
				rel.setId(cntPost.getId());
				rel.setSourceId(cntPost.getMemberId());
				rel.setExtend1(zero);
				rel.setOrgAccountId(cntPost.getCntAccountId());
				rel.setObjectiveId(cntPost.getCntDepId());
				rel.setBackupId(cntPost.getCntPostId());
				rel.setCreateTime(cntPost.getStartTime());
				orgManagerDirect.updateEntity(rel);
			}			
		}
	}
	
	public void delUserCurrentPost(Long userId) throws BusinessException {
		List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(userId);
		if(cntList!=null){
			for(ConcurrentPost cntPost:cntList){
				orgManagerDirect.deleteConcurrentPost(cntPost.getId());
			}			
		}
	}
	
	public void clearAllCurrentPosts() throws BusinessException {
		List<V3xOrgEntity> cntList = orgManagerDirect.getEntityList(V3xOrgRelationship.class.getSimpleName(), "type", V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if(cntList!=null){
			for(V3xOrgEntity cntPost:cntList){
				orgManagerDirect.deleteEntity(cntPost);
			}
		}
	}
	
	public boolean modifyMemberAccountCheck(Long memberId) throws BusinessException {
	    //这里只判断表单管理员的待办事项
		boolean hasPending = false;
	    try {
			hasPending = iOperBase.queryOwnerListByownerid(memberId);			
		} catch (DataDefineException e) {
			log.error("error in checking member's pending affair!");
		}	
		return hasPending;
	}
	
	class MemberSyncher
	{
//		private Map<Long,V3xOrgEntity> allDepartment ;
//		private Map<Long,V3xOrgEntity> allPost;
		private Map<Long,V3xOrgEntity> allRelationship;
		private Map<Long,V3xOrgEntity> allMember;
//		private Map<Long,V3xOrgEntity> allCredential;
		
        //部门岗位关系分类
		private List<V3xOrgRelationship> depPosts; 		
		
		private List<V3xOrgMember> addMemberQueue;
		private List<V3xOrgMember> updateMemberQueue;
//		private List<JetspeedPrincipal> updatePrincipalQueue;
//		private List<JetspeedCredential> updateCrendentialQueue;	
		private List<V3xOrgRelationship> addRelationshipQueue;
//		private List<JetspeedPrincipal> addPrincipalQueue;
//		private List<JetspeedCredential> addCrendentialQueue;		
//		private List<V3xOrgProperty> propertieQueue;	
		//操作结果
		private Map mapReport;
		
		//取得单位内的组织信息	
		private OrgCommonDao dao = orgManager.getOrgInstance().getDao();		
		private final long accountId;
		private final boolean rollback;
		private final boolean isNeedSecondPost;
		public MemberSyncher( boolean rollback, boolean isNeedSecondPost,Long accountId)
		{
			this.rollback = rollback;
			this.isNeedSecondPost = isNeedSecondPost;
			this.accountId = accountId;
		}
		@SuppressWarnings("unchecked")
		public synchronized Map<Long,String> synchMember(List<V3xOrgMember> members) throws Exception {
			//获得单位最大排序号
			int maxNum = orgManagerDirect.getMaxMemberSortNum(accountId);
			
			init();
			//校验准备数据
			for(V3xOrgMember member:members){
				try{
					check(member);				
					// 记录Properties
//					for (Map.Entry<String, String> entry : member.getProperties().entrySet()) {
//						V3xOrgProperty prp = new V3xOrgProperty();
//						prp.setSourceId(member.getId());
//						prp.setName(entry.getKey());
//						prp.setValue(entry.getValue());
//						prp.setOrgAccountId(member.getOrgAccountId());
//						addProperty(prp);
//					}
					//人员密码为空时设置默认密码
					if(StringUtils.isEmpty(member.getPassword()))
						member.setPassword(V3xOrgEntity.DEFAULT_PASSWORD);

					//判断人员是更新还是添加还是放弃
					V3xOrgEntity ent = allMember.get(member.getId());
					if(ent!=null&&!ent.getIsDeleted()){
						update(member);
					}else if(ent==null){
						maxNum++;
						add(maxNum, member);				
					}
			
				}catch(Exception e){
					if(rollback){
						mapReport.clear();
						throw e;
					}else{
						mapReport.put(member.getId(), "1|"+e.getMessage());
						log.error("nc同步出错： ",e);
						continue;
					}
				}
			}
			save();		
			return mapReport;
		}
		/**
		 * 取得所有Credential的PrincipalId-Credential Map。 
		 * @return
		 */
/*		private Map<Long,V3xOrgEntity> getPrincipalIdCredentialMap()
		{
			Map<Long,V3xOrgEntity> map =dao.findAllEntitys(JetspeedCredential.class);
			Map<Long,V3xOrgEntity> result = new HashMap<Long,V3xOrgEntity> ();
			for (V3xOrgEntity ent : map.values()) {
				JetspeedCredential crd = (JetspeedCredential) ent;
				result.put( new Long(crd.getPrincipalId()), crd);
			}
			return result;
		}*/
		private void init() throws BusinessException {
			//操作结果
			mapReport = new HashMap<Long,String>();

//			allDepartment = orgManager.getOrgInstance().getAllEntity(V3xOrgDepartment.class, accountId);
//			allPost = orgManager.getOrgInstance().getAllEntity(V3xOrgPost.class, accountId);
			allRelationship = orgManager.getOrgInstance().getAllEntity(V3xOrgRelationship.class, accountId);
			allMember = dao.findAllEntitys(V3xOrgMember.class);
//			allCredential = getPrincipalIdCredentialMap();
			//载入所有账号信息
//			dao.reloadPrincipal();

	        //部门岗位关系分类
			depPosts = new ArrayList<V3xOrgRelationship>();
			for(Iterator it = allRelationship.values().iterator(); it.hasNext();){
				V3xOrgRelationship rel = (V3xOrgRelationship)it.next();
				if(V3xOrgEntity.ORGREL_TYPE_DEP_POST.equals(rel.getType())){
					if(rel.getObjectiveId()==V3xOrgEntity.DEFAULT_NULL_ID||rel.getSourceId()==V3xOrgEntity.DEFAULT_NULL_ID)
					{
						continue;
					}
					depPosts.add(rel);
				}
			}	
			//实体操作列表
			addMemberQueue = new ArrayList<V3xOrgMember>();
			updateMemberQueue = new ArrayList<V3xOrgMember>();
//			updatePrincipalQueue = new ArrayList<JetspeedPrincipal>();
//			updateCrendentialQueue = new ArrayList<JetspeedCredential>();
			addRelationshipQueue = new ArrayList<V3xOrgRelationship>();
//			addPrincipalQueue = new ArrayList<JetspeedPrincipal>();
//			addCrendentialQueue = new ArrayList<JetspeedCredential>();		
//			propertieQueue = new ArrayList<V3xOrgProperty> ();
		}
		private void save() throws BusinessException {
//			List<V3xOrgEntity> addEntitys = new ArrayList<V3xOrgEntity> ();
//			List<V3xOrgEntity>  updateEntitys = new ArrayList<V3xOrgEntity> ();
//			addEntitys.addAll(addMemberQueue);
//			addEntitys.addAll(addPrincipalQueue);
//			addEntitys.addAll(addCrendentialQueue);
//			addEntitys.addAll(addRelationshipQueue);
//			addEntitys.addAll(getMembersSecurity(addMemberQueue,accountId));
//			updateEntitys.addAll(updateMemberQueue);
//			updateEntitys.addAll(updatePrincipalQueue);
//			updateEntitys.addAll(updateCrendentialQueue);
			//插入数据库
			try{
				//插入人员
//				orgManager.addEntitys(addEntitys);
				OrganizationMessage message=orgManager.addMembers(addMemberQueue);
				 if(!message.isSuccess())
	                {
	                	for (OrgMessage orgM : message.getErrorMsgs()) {
	                		log.warn("同步失败信息： "+orgM.getEnt().getId()+" "+orgM.getEnt().getName()+" "+orgM.getCode());
	                	}
	                }
//				orgManager.updateEntitys(updateEntitys);
				orgManager.updateMembers(updateMemberQueue);
				orgManager.updateEntitys(addRelationshipQueue);
				// 可提升性能点：property无法确定是insert还是update，使用saveOrUpdate
//				for (V3xOrgEntity prp : propertieQueue) {
//					orgManager.updateEntity(prp);
//				}
				
				//重新载入菜单一次
				menuManager.initAllMemberSecurity();
/*				for(V3xOrgMember mem : addMemberQueue){
					templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,mem.getId(),mem.getId());
				}*/
			}catch(Exception e){
				log.error("error insert member date!",e);
				throw new BusinessException("error insert member data!");
			}
		}
		private void addRelationship(V3xOrgRelationship dpRel) {
			addRelationshipQueue.add(dpRel);
		}
		/*		
		private void addPrincipal(JetspeedPrincipal prp) {
			addPrincipalQueue.add(prp);
		}
		private void updatePrincipal(JetspeedPrincipal prp) {
			updatePrincipalQueue.add(prp);
		}
		private void updateCrendential(JetspeedCredential crd) {
			updateCrendentialQueue.add(crd);
		}	*/		
//		private void addProperty(V3xOrgProperty prp) {
//			propertieQueue.add(prp);
//		}		
		private void add(int sortId, V3xOrgMember member)
				throws BusinessException {
			// 添加
			if (!addMemberQueue.contains(member)) {
				member.setSortId(sortId);
				addMemberQueue.add(member);
				// 添加账号及密码
//				JetspeedPrincipal prp = addPrincipal(member);
//				addCredential(member, prp);
				// 更新部门岗位关系
				boolean isContain = false;
				Long postId = member.getOrgPostId();
				Long departmentId = member.getOrgDepartmentId();
				if (member.getEnabled()
						&& orgManagerDirect.getPostById(postId) != null) {
					for (V3xOrgRelationship rel : depPosts) {
						if (departmentId.equals(
								rel.getSourceId())
								&& postId.equals(
										rel.getObjectiveId())) {
							isContain = true;
						}
					}
				}
				if (!isContain&&postId!=V3xOrgEntity.DEFAULT_NULL_ID&&departmentId!=V3xOrgEntity.DEFAULT_NULL_ID) {
					V3xOrgRelationship dpRel = new V3xOrgRelationship();
					dpRel.setSourceId(departmentId);
					dpRel.setObjectiveId(postId);
					dpRel.setOrgAccountId(accountId);
					dpRel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_POST);
					depPosts.add(dpRel);
					addRelationship(dpRel);
				}
				// 副岗
				if (member.getEnabled() && isNeedSecondPost) {
					List<MemberPost> secondPosts = member.getSecond_post();
					for (MemberPost sp : secondPosts) {
						addSecondPost(member, sp);
					}
				}
			}
			mapReport.put(member.getId(), "0");
		
		}
/*		private void addCredential(V3xOrgMember member, JetspeedPrincipal prp)
				throws BusinessException {
			// 持久化Credential
			JetspeedCredential crd = new JetspeedCredential();
			crd.setCredentialId(orgManager.generateCredentialID());
			crd.setOrgAccountId(member.getOrgAccountId());

			// 加密人员账号信息
			if (!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(member
					.getPassword())) {
				try {
					MessageEncoder encode = new MessageEncoder();
					crd.setColumnValue(encode.encode(member.getLoginName(),
							member.getPassword()));
				} catch (NoSuchAlgorithmException e) {
					log.error("error set member's password code!", e);
				} catch (SecurityException e) {
					log.error("error set member's password code!", e);
				}

			}
			crd.setIsEncoded(1);
			crd.setClassName(V3xOrgEntity.CLASS_NAME_CREDENTIAL);
			crd.setPrincipalId(prp.getPrincipalId());

			// 需要是有效的人员，才能够登录
			if (member.isValid())
				crd.setIsEnabled(1);
			else
				// 否则不允许登录
				crd.setIsEnabled(0);
			addCrendentialQueue.add(crd);
		}*/
/*		private JetspeedPrincipal addPrincipal(V3xOrgMember member)
				throws BusinessException {
			JetspeedPrincipal prp = new JetspeedPrincipal();
			prp.setPrincipalId(orgManager.generatePrincipalID());
			prp.setOrgAccountId(member.getOrgAccountId());
			prp.setId(member.getId());
			prp.setClassName(V3xOrgEntity.CLASS_NAME_USERPRINCIPAL);
			String fullPath = UserPrincipalUtil
					.getFullPathFromPrincipalName(member.getLoginName());
			prp.setFullPath(fullPath);
			addPrincipal(prp);
			return prp;
		}*/
		private void update(V3xOrgMember member) throws SecurityException,
				BusinessException {
			//更新
			if(updateMemberQueue.contains(member)) return;
			
//			JetspeedPrincipal prp = (JetspeedPrincipal)dao.findPrincipalByEntityId(member.getId());
			//是否更改了账号
//			String loginName = member.getLoginName();
//			Long myAccountId = member.getOrgAccountId();
//			boolean isLoginNameChanged = !loginName.equals(UserPrincipalUtil.getPrincipalNameFromFullPath(prp.getFullPath()));
//			V3xOrgMember memMember = (V3xOrgMember) allMember.get(member.getId());
//			boolean isAccountChanged = !myAccountId.equals(memMember.getOrgAccountId());
//			V3xOrgEntity ent1=allCredential.get(Long.valueOf(prp.getPrincipalId()));
//			boolean isChangePassWordState = isChangeState(ent1,member);
//			if(isLoginNameChanged||isAccountChanged||isChangePassWordState){
//				prp.setFullPath(UserPrincipalUtil.getFullPathFromPrincipalName(loginName));	
//				prp.setOrgAccountId(myAccountId);
//				updatePrincipal(prp);
				//生成密码
//				V3xOrgEntity ent1 = allCredential.get(Long.valueOf(prp.getPrincipalId()));
/*				if(ent1!=null)
				{
//					JetspeedCredential crd =(JetspeedCredential) ent1;
					//加密人员账号信息
					if(isChangePassWordState||!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(member.getPassword()))
					{
						try {
							if(!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(member.getPassword()))
							{
								MessageEncoder encode = new MessageEncoder();
								crd.setColumnValue(encode.encode(loginName, member.getPassword()));
							}
							if (member.isValid())
								crd.setIsEnabled(1);
							else	//否则不允许登录
								crd.setIsEnabled(0);
							log.warn(" "+loginName+" 密码改变");
						} catch (NoSuchAlgorithmException e) {
							log.error("error set member's password code!",e);
						} catch (SecurityException e) {
							log.error("error set member's password code!",e);
						}
//						updateCrendential(crd);	
					}
				}
				else
				{
					// 没有密码
					log.error("没有找到对应的Credential:PrincipalId="+ prp.getPrincipalId() +" loginName="+loginName);
				}*/
//			}
			//更新部门岗位关系
			boolean isContain = false;
			if(member.getEnabled()&&orgManagerDirect.getPostById(member.getOrgPostId())!=null){
				for(V3xOrgRelationship rel:depPosts){
					if(member.getOrgDepartmentId().equals(rel.getSourceId())&&member.getOrgPostId().equals(rel.getObjectiveId())){
						isContain = true;
					}
				}
			}
			if(member.getEnabled()&&!isContain&&member.getOrgPostId()!=V3xOrgEntity.DEFAULT_NULL_ID&&member.getOrgDepartmentId()!=V3xOrgEntity.DEFAULT_NULL_ID){
				V3xOrgRelationship dpRel = new V3xOrgRelationship();
				dpRel.setSourceId(member.getOrgDepartmentId());
				dpRel.setObjectiveId(member.getOrgPostId());
				dpRel.setOrgAccountId(accountId);
				dpRel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_POST);
				depPosts.add(dpRel);
				addRelationship(dpRel);
			}
			// 副岗
			if(member.getEnabled()&&isNeedSecondPost&&member.getSecond_post().size()>0)
			{
				invokeSecondPost(member);
			}
			
			updateMemberQueue.add(member);	
			mapReport.put(member.getId(), "0");
		}
		public boolean isChangeState(V3xOrgEntity ent1,V3xOrgMember member) {
			boolean isChange=false;
/*			if(ent1!=null)
			{
				JetspeedCredential crd =(JetspeedCredential) ent1;
				
				//密码是用的1或0来表示停启用的
				boolean isEnabled = crd.getIsEnabled() == 1?true:false;
				isChange =member.isValid() != isEnabled?true:false;
			}*/
			return isChange;
		}
		private void invokeSecondPost(V3xOrgMember member)
				throws BusinessException {
			boolean isContain = false;
			List<MemberPost> secondPosts = member.getSecond_post();
			V3xOrgMember orginMember = orgManager.getMemberById(member.getId());
			List<MemberPost> orginSecondPosts;
			if(orginMember!=null)
			{
				orginSecondPosts = orginMember.getSecond_post();
			}
			else
			{
				orginSecondPosts = new ArrayList<MemberPost>();
			}
			Set<MemberPost> toAdd = new HashSet<MemberPost>(secondPosts);
			Set<MemberPost> toRemove = new HashSet<MemberPost>(orginSecondPosts);								
			toAdd.removeAll(orginSecondPosts);
			toRemove.removeAll(secondPosts);
			for(MemberPost sp:toAdd)
			{
				addSecondPost(member, sp);
			}	
			for(MemberPost sp:toRemove)
			{						
				// 删除副岗
				V3xOrgRelationship rel = new V3xOrgRelationship();
				Long depId = sp.getDepId();
				rel.setSourceId(member.getId());
				rel.setObjectiveId(depId);
				rel.setBackupId(sp.getPostId());
				rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
				rel.setOrgAccountId(accountId);
				// TODO 优化，批量删除
				orgManager.deleteEntity(rel);
			}
		}
		private void addSecondPost(V3xOrgMember member,MemberPost sp) {
			boolean isContain = false;
			if(sp.getPostId()==V3xOrgEntity.DEFAULT_NULL_ID){return;}
			if(sp.getDepId()==V3xOrgEntity.DEFAULT_NULL_ID){return;}
			// 添加副岗
			V3xOrgRelationship rel = new V3xOrgRelationship();
			Long depId = sp.getDepId();
			rel.setSourceId(member.getId());
			rel.setObjectiveId(depId);
			rel.setBackupId(sp.getPostId());
			rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
			rel.setOrgAccountId(accountId);		
			addRelationship(rel);
			// 更新副岗部门岗位关系
			for(V3xOrgRelationship r:depPosts){
				if(depId.equals(r.getSourceId())&&sp.getPostId().equals(r.getObjectiveId())){
					isContain = true;
				}
			}			
			if(!isContain){
				V3xOrgRelationship dpRel = new V3xOrgRelationship();
				dpRel.setSourceId(depId);
				dpRel.setObjectiveId(sp.getPostId());
				dpRel.setOrgAccountId(accountId);
				dpRel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_POST);
				depPosts.add(dpRel);
				addRelationship(dpRel);
			}
		}
		// 检查Member的合法性
		private void check(V3xOrgMember member) throws BusinessException {
			if(member.getCode()==null){						
				throw new BusinessException("添加人员出错:人员编码为空");
			}
			if(member.getName()==null){
				throw new BusinessException("添加人员出错:人员姓名为空");
			}
			if(member.getOrgAccountId()==null){
				throw new BusinessException("添加人员出错:人员单位为空");
			}
			if(!member.getOrgAccountId().equals(accountId)){
				throw new BusinessException("添加人员出错:人员单位非同步单位");
			}				
			// 防护，修正错误登录数据
/*			if(member.getLoginName()==null){
				//保存principal
				if(member.getCode()!=null)
				{
					JetspeedPrincipal p = new JetspeedPrincipal();
					p.setPrincipalId(orgManager.generatePrincipalID());
					p.setOrgAccountId(member.getOrgAccountId());
					p.setId(member.getId());
					p.setClassName(V3xOrgEntity.CLASS_NAME_USERPRINCIPAL);
					p.setFullPath(UserPrincipalUtil.getFullPathFromPrincipalName(member.getCode()));
					addPrincipal(p);
					member.setLoginName(member.getCode());
					log.info(member!=null?"security_principal not loginName "+member.getLoginName()+" "+member.getName()+" "+member.getId()+"  "+member.getCode():"222nullmemberzy1");
				}else{
					throw new BusinessException("error add member for null login name");
				}
			}*/
			//人员帐号重复验证
//			JetspeedPrincipal jp = dao.findPrincipalByLoginName(member.getLoginName());
			
			if(principalManager.isExist(member.getLoginName())){
				if(principalManager.getMemberIdByLoginName(member.getLoginName())!=(member.getId())){
					throw new BusinessException("添加人员出错:登录名称 "+member.getLoginName()+" "+" 在系统中已经存在");
				}
			}
			
			// 检查部门
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
			if(dept==null){
				member.setEnabled(false);
				member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
				log.info("set member "+ member.getName()+" unable for null department");
			}
		}		
	}
	@SuppressWarnings("unchecked")
	public Map<Long,String> synchMember(List<V3xOrgMember> members, boolean rollback, boolean isNeedSecondPost,Long accountId) throws Exception {
		return new MemberSyncher(rollback, isNeedSecondPost,accountId).synchMember(members);
	}
	
	public void moveMember(Long memberId, Long deptId) throws BusinessException {
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
		
		if(dept!=null&&member!=null){
           //是否是本单位移动人员
			if(!member.getOrgAccountId().equals(dept.getOrgAccountId())){
				V3xOrgAccount orgAccount = orgManager.getAccountById(member.getOrgAccountId());
				//删除人员的副岗
				member.setSecond_post(new ArrayList<MemberPost>());
				//删除部门角色人员
				orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE, "sourceId", memberId);
				//删除单位角色人员
				orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE, "sourceId", memberId);
				//删除副岗
				orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_POST, "sourceId", memberId);
				//删除兼职
				orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST, "sourceId", memberId);
				//设置岗位
				V3xOrgPost memPost = (V3xOrgPost)orgAccount.getEntity(V3xOrgPost.class, member.getOrgPostId());
				if(memPost!=null){
					List<V3xOrgEntity> accountPost = orgManagerDirect.getEntityListNoRelation(V3xOrgPost.class.getSimpleName(), "name", memPost.getName(), dept.getOrgAccountId());
					if(accountPost!=null&&accountPost.size()>0){
						V3xOrgPost post = (V3xOrgPost)accountPost.get(0);
						//如果岗位停用则启用岗位
						if(!post.getEnabled()){
							post.setEnabled(true);
							orgManagerDirect.updateEntity(post);
						}
						member.setOrgPostId(post.getId());
					}else{
						//如果新单位不存在同名岗位则创建岗位
						V3xOrgPost newPost = new V3xOrgPost();
						newPost.setTypeId(memPost.getTypeId());
						newPost.setName(memPost.getName());
						newPost.setSortId(V3xOrgEntity.SORT_STEP_NUMBER);
						newPost.setOrgAccountId(dept.getOrgAccountId());
						orgManagerDirect.addPost(newPost);
						member.setOrgPostId(newPost.getId());
					}
				}else{
					member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
				}
				
				//设置职务级别
				V3xOrgLevel memLevel = (V3xOrgLevel)orgAccount.getEntity(V3xOrgLevel.class, member.getOrgLevelId());
				if(memLevel!=null){
					List<V3xOrgEntity> accountLevel = orgManagerDirect.getEntityListNoRelation(V3xOrgLevel.class.getSimpleName(), "name", memLevel.getName(), dept.getOrgAccountId());
					if(accountLevel!=null&&accountLevel.size()>0){
						member.setOrgLevelId(accountLevel.get(0).getId());
					}else{
						member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
					}
				}else{
					member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
				}
			}
			member.setOrgDepartmentId(deptId);
			member.setOrgAccountId(dept.getOrgAccountId());
			orgManager.updateEntity(member);
		}		
	}

	public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException {
		List<String[]> moveLogListStr = new ArrayList<String[]>();
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);	
		Long oldAccountId= dept.getOrgAccountId();
		List<V3xOrgMember> deptMems = orgManagerDirect.getMembersByDepartment(deptId, false, null,oldAccountId);
		//取所有用户所有的兼职单位
		Set<Long> accountIdSet = new HashSet<Long>();
		accountIdSet.add(oldAccountId);
		for(V3xOrgMember deptMem:deptMems){
			Map<Long, List<ConcurrentPost>> accountList = orgManager.getConcurentPostsByMemberId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID
					,deptMem.getId());
			for(List<ConcurrentPost> accountPostList:accountList.values())
				for(ConcurrentPost concurrent:accountPostList)
					accountIdSet.add(concurrent.getCntAccountId());
		}
		if(dept!=null&&account!=null){			
			log.info(DateUtil.formatDate(new Date())+"开始移动部门:"+dept.getName());
			V3xOrgAccount orgAccount = orgManager.getAccountById(dept.getOrgAccountId());
            //删除部门岗位关系
			orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_POST,"sourceId",dept.getId());
			//删除外部人员访问权限
			orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE,"objectiveId",dept.getId());
			//删除部门下的兼职
			List<V3xOrgRelationship> mp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",dept.getId());
			List<V3xOrgRelationship> deptMp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",dept.getId());
			List<V3xOrgRelationship> accountMp = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"orgAccountId",orgAccount.getId());
			mp.addAll(deptMp);
			accountMp.removeAll(deptMp);
			UniqueList<Long> memberIds = new UniqueList<Long>();
			UniqueList<Long> cntMemberIds = new UniqueList<Long>();
			for(V3xOrgRelationship rel:mp){
				memberIds.add(rel.getSourceId());
			}
			orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",dept.getId());
			orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",dept.getId());
			if(memberIds.size()>0){
				orgManager.deleteRelsInList(memberIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
			}
			cntMemberIds.addAll(memberIds);
			
			//重新设置部门角色关系
//			List<V3xOrgRelationship> deptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_ROLE,"sourceId",dept.getId());
			List<V3xOrgRelationship> memberDeptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId());
			List<V3xOrgRole> deptRoles= orgManagerDirect.getDepartmentRolesByAccount(accountId);
			List<V3xOrgRole> oldDeptRoles= orgManagerDirect.getDepartmentRolesByAccount(oldAccountId);
			if(deptRoles!=null){
				for(V3xOrgRole role:deptRoles)
					dept.addDepRole(role.getId());
			}
			//删除部门下的人员部门角色关系
			orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId(),"orgAccountId",oldAccountId);
			log.info(DateUtil.formatDate(new Date())+"更新部门关系完成");
			// 修改部门人员个人组的所属单位
			List<V3xOrgTeam> allTeams = orgManager.getAllTeams(dept.getOrgAccountId());
			for (V3xOrgTeam team : allTeams) {
				V3xOrgMember owner = orgManager.getMemberById(team.getOwnerId());
				if(owner!=null){
					if(deptId.equals(owner.getOrgDepartmentId())){
						// 部门人员建立的组
						team.setDepId(deptId);
						team.setOrgAccountId(accountId);
						orgManager.updateEntity(team); 
						List<V3xOrgRelationship> rels = orgManager.getRelationships("sourceId",team.getId());
						for (V3xOrgRelationship rel : rels) {
							// 从原单位移除
							//orgManager.deleteEntity(rel);
							// 添加到新单位
							//AEIGHT-5577 lilong 没有必要先删除再新增，这个Team_Leader或者Team_Member的关系直接更新单位id即可
							rel.setOrgAccountId(accountId); 
							orgManager.updateEntity(rel);
						}
					}
				}
			}				
			dept.setPosts(new ArrayList<Long>());
//			deptMems = orgManagerDirect.getMembersByDepartment(deptId, false, null,dept.getOrgAccountId());
			log.info("部门下的人员："+deptMems.size());
			
			//删除调整部门下人员的所有组织关联信息(组信息除外，批量删除以200为基数）
			List<Long> memIds = new ArrayList<Long>();
//			UniqueList<Long> memPostId = new UniqueList<Long>();
			if(deptMems!=null){
				int count = 0;
				for(V3xOrgMember deptMem : deptMems){
					if(count>=200){						
						//删除单位角色人员
						orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE);
						//删除副岗
						orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
						//删除兼职
						//orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
						memIds = new ArrayList<Long>();
						count = 0;
					}
					count++;
					memIds.add(deptMem.getId());
					//删除人员在调入单位的部门角色关系  by wusb at 2010-11-6
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"sourceId",deptMem.getId(),"orgAccountId",accountId);
//					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",deptMem.getId(),"orgAccountId",accountId);
				}
				if(memIds!=null&&memIds.size()>0){
					//删除单位角色人员
					orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE);
					//删除副岗
					orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);
					//删除兼职
					//orgManager.deleteRelsInList(memIds, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);	
				}
			}
			log.info(DateUtil.formatDate(new Date())+"更新部门人员关系完成");
			//删除部门与组之间的关系
			List<V3xOrgEntity> deptTeams = orgManagerDirect.getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(), "depId", deptId, dept.getOrgAccountId());
			if(deptTeams!=null&&deptTeams.size()>0){
				for(V3xOrgEntity deptTeam : deptTeams){
					((V3xOrgTeam)deptTeam).setDepId(dept.getOrgAccountId());
				}
			}
			//重新设置部门岗位关系
			
			//删除子部门与组之间的关系			
			List<V3xOrgDepartment> childDepts = orgManagerDirect.getChildDepartments(deptId, false);
			if(childDepts!=null&&childDepts.size()>0){
				for(V3xOrgDepartment childDept:childDepts){
					List<V3xOrgEntity> childDeptTeams = orgManagerDirect.getEntityListNoRelation(V3xOrgTeam.class.getSimpleName(), "depId", childDept.getId(), dept.getOrgAccountId());
					if(childDeptTeams!=null&&childDeptTeams.size()>0){
						for(V3xOrgEntity childDeptTeam : childDeptTeams){
							((V3xOrgTeam)childDeptTeam).setDepId(dept.getOrgAccountId());
						}
					}						
				}
			}
			//移动部门
			log.info(DateUtil.formatDate(new Date())+"开始设置部门路径");
			orgManagerDirect.setDepPath(dept, accountId);
			dept.setOrgAccountId(accountId);
			log.info(DateUtil.formatDate(new Date())+"开始持久化部门角色");
			orgManager.updateEntity(dept);
			log.info(DateUtil.formatDate(new Date())+"子部门的个数："+childDepts.size());
			List<Long> allDeptIds = new ArrayList<Long>();
			allDeptIds.add(deptId);
			if(childDepts!=null&&childDepts.size()>0){
				for(V3xOrgDepartment childDept:childDepts){
					allDeptIds.add(childDept.getId());
	                //删除部门岗位关系
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_POST,"sourceId",childDept.getId());
					//删除外部人员访问权限
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE,"objectiveId",childDept.getId());
					//删除部门下的兼职
					List<V3xOrgRelationship> mpc = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",childDept.getId());
					List<V3xOrgRelationship> cntMpc = orgManager.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
					mpc.addAll(cntMpc);
					accountMp.removeAll(cntMpc);					
					List<Long> memberIdsC = new ArrayList<Long>();
					for(V3xOrgRelationship rel:mpc){
						memberIdsC.add(rel.getSourceId());
					}
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,"objectiveId",childDept.getId());
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"objectiveId",childDept.getId());
					if(memberIdsC.size()>0){
						orgManager.deleteRelsInList(memberIdsC, V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
					}	
					cntMemberIds.addAll(memberIdsC);
					//重新设置部门角色关系
//					List<V3xOrgRelationship> childDeptRoleRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_DEP_ROLE,"sourceId",childDept.getId());
					List<V3xOrgRelationship> memberDeptRoleRelsChild = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",childDept.getId());
					/*List<V3xOrgEntity> childDeptRoleRelEnts = new ArrayList<V3xOrgEntity>();
					List<Long> childRoles = new ArrayList<Long>();
					if(childDeptRoleRels!=null){
						for(V3xOrgRelationship deptRel:childDeptRoleRels){
						    deptRel.setOrgAccountId(accountId);
						    V3xOrgRole role = orgManager.getRoleById(deptRel.getObjectiveId());
						    V3xOrgRole relRole = orgManager.getRoleByName(role.getName(),accountId);
						    deptRel.setObjectiveId(relRole.getId());
						    childRoles.add(relRole.getId());
						    childDeptRoleRelEnts.add((V3xOrgEntity)deptRel);
						}
					}*/
//					orgManager.updateEntitys(childDeptRoleRelEnts);
					deptRoles = orgManagerDirect.getDepartmentRolesByAccount(accountId);
					if(deptRoles!=null){
						for(V3xOrgRole role:deptRoles)
							childDept.addDepRole(role.getId());
					}
					childDept.setPosts(new ArrayList<Long>());
					childDept.setOrgAccountId(accountId);
					orgManager.updateEntity(childDept);
					
					//创建人员部门角色关系  wusb
					for(V3xOrgRelationship memberDepRole:memberDeptRoleRelsChild){
						if(!isDepLeader(memberDepRole.getBackupId(),oldDeptRoles)){
							V3xOrgRelationship rel = new V3xOrgRelationship();
							rel.setSourceId(memberDepRole.getSourceId());
							rel.setObjectiveId(childDept.getId());
							//原单位角色
							V3xOrgRole role = orgManager.getRoleById(memberDepRole.getBackupId());
							//新单位角色
						    V3xOrgRole relRole = orgManager.getRoleByName(role.getName(),accountId);
							rel.setBackupId(relRole.getId());
							rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
							rel.setOrgAccountId(accountId);
							orgManager.updateEntity(rel);
						}
					}
					
					//删除子部门人员部门角色关系
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,"objectiveId",dept.getId(),"orgAccountId",oldAccountId);
					
				}
			}
			log.info(DateUtil.formatDate(new Date())+"删除子部门关系完成");
			//移动人员
			if(deptMems!=null){		
				//部门岗位关系
				UniqueList<String> deptPost = new UniqueList<String>();
				List<Long> memberIdList = new ArrayList<Long>();
				for(V3xOrgMember deptMem : deptMems){
					deptMem.setOrgAccountId(accountId);
					memberIdList.add(deptMem.getId());
					//设置岗位
					V3xOrgPost memPost = (V3xOrgPost)orgAccount.getEntity(V3xOrgPost.class, deptMem.getOrgPostId());
					if(memPost!=null){
						List<V3xOrgEntity> accountPost = orgManagerDirect.getEntityListNoRelation(V3xOrgPost.class.getSimpleName(), "name", memPost.getName(), accountId);
						if(accountPost!=null&&accountPost.size()>0){
							V3xOrgPost post = (V3xOrgPost)accountPost.get(0);
							//如果岗位停用则启用岗位
							if(!post.getEnabled()){
								post.setEnabled(true);
								orgManager.updateEntity(post);
							}
							deptMem.setOrgPostId(post.getId());
							//更新部门岗位关系
							deptPost.add(deptMem.getOrgDepartmentId()+"&"+post.getId());
						}else{
							//如果新单位不存在同名岗位则创建岗位
							V3xOrgPost newPost = new V3xOrgPost();
							newPost.setTypeId(memPost.getTypeId());
							newPost.setName(memPost.getName());
							newPost.setSortId(V3xOrgEntity.SORT_STEP_NUMBER);
							newPost.setOrgAccountId(accountId);
							orgManager.updateEntity(newPost);
							//记录日志
							String[] addPostStr = new String[3];
							addPostStr[0] = "1";
							addPostStr[1] = memPost.getName();
							moveLogListStr.add(addPostStr);
							//如果是集团基准岗，需要创建关系
							List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST,"sourceId",memPost.getId());
							if(rels!=null&&rels.size()>0){
								//创建基准岗关系
								V3xOrgRelationship rel = new V3xOrgRelationship();
								rel.setType(V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST);
								rel.setOrgAccountId(accountId);
								rel.setSourceId(newPost.getId());
								V3xOrgRelationship bmRel = (V3xOrgRelationship)rels.get(0);
								rel.setObjectiveId(bmRel.getObjectiveId());
								orgManager.updateEntity(rel);
							}
							deptMem.setOrgPostId(newPost.getId());
							deptPost.add(deptMem.getOrgDepartmentId()+"&"+newPost.getId());
						}
					}else{
						deptMem.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
					}
					//设置职务级别
					V3xOrgLevel memLevel = (V3xOrgLevel)orgAccount.getEntity(V3xOrgLevel.class, deptMem.getOrgLevelId());
					if(memLevel!=null){
						List<V3xOrgEntity> accountLevel = orgManagerDirect.getEntityListNoRelation(V3xOrgLevel.class.getSimpleName(), "name", memLevel.getName(), accountId);
						if(accountLevel!=null&&accountLevel.size()>0){
							deptMem.setOrgLevelId(accountLevel.get(0).getId());
						}else{
							deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
						}
					}else{
						deptMem.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
					}
					//清除人员副岗
					deptMem.setSecond_post(new ArrayList<MemberPost>());
					orgManager.updateEntity(deptMem);
					
					//清除部门人员在调入单位的兼职
					orgManager.deleteRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",deptMem.getId(),"orgAccountId",accountId);
					log.info(DateUtil.formatDate(new Date())+"完成移动人员:"+deptMem.getName());
				}
				//创建部门岗位关系
				for(String depP:deptPost){
					String[] deptP = depP.split("&");
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setSourceId(Long.parseLong(deptP[0]));
					rel.setObjectiveId(Long.parseLong(deptP[1]));
					rel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_POST);
					rel.setOrgAccountId(accountId);
					orgManager.updateEntity(rel);
				}
				
				//创建人员部门角色关系 wusb 2010-09-01
				for(V3xOrgRelationship memberDepRole:memberDeptRoleRels){
					if(!isDepLeader(memberDepRole.getBackupId(),oldDeptRoles)){
						V3xOrgRelationship rel = new V3xOrgRelationship();
						rel.setSourceId(memberDepRole.getSourceId());
						rel.setObjectiveId(dept.getId());
						//原部门角色
						V3xOrgRole role = orgManager.getRoleById(memberDepRole.getBackupId());
						//新部门角色
					    V3xOrgRole relRole = orgManager.getRoleByName(role.getName(),accountId);
						rel.setBackupId(relRole.getId());
						rel.setType(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE);
						rel.setOrgAccountId(accountId);
						orgManager.updateEntity(rel);
					}
				}
				
				/*删除 
				     单位文档库管理员
				    公文档案库管理员
				    原单位公共信息管理员
				   综合办公各权限管理员
				   删除 工作管理设置人员 
				   印章
				 wusb 2010-09-01 
				 */  
				if(!memberIdList.isEmpty()){
					try {
						pushNewOrgEntityTemplete4Member(memberIdList);
						deleteDocLibManager(memberIdList,accountIdSet);
						deletePublicManager(memberIdList,oldAccountId);
						deleteWorkManager(memberIdList,allDeptIds,oldAccountId);
						deleteOfficeManager(memberIdList,allDeptIds,oldAccountId);
						deleteSignetManager(memberIdList,oldAccountId);
					} catch (Exception e) {
						log.error(e);
//						throw new BusinessException(e);
					}
				}
				
			}
			//如果部门下的人员在其他部门不存在兼职，则删除此人员的单位角色
			for(V3xOrgRelationship rel:accountMp){
				if(cntMemberIds.contains(rel.getSourceId())){
					cntMemberIds.remove(rel.getSourceId());
				}
			}
			if(cntMemberIds.size()>0){
				orgManager.deleteRelsInList(cntMemberIds, V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE);
			}
		}
		log.info(DateUtil.formatDate(new Date())+"完成移动部门");
		return moveLogListStr;
	}
	
	
	//删除文档库管理员(单位与公文) wusb
	private void deleteDocLibManager(List<Long> memberIds,Set<Long> accountIdSet) throws Exception{
		for(Long accountId : accountIdSet){
			//所有文档库集合
			List<DocLib> docLibList = docLibManager.getDocLibsWithoutGroupLib(accountId); // 获取所有的自定义文档库和公共文档库
			for(DocLib docLib:docLibList){
				List<Long> list = new ArrayList<Long>();
				Long docLibId=docLib.getId();
				long docResId = docHierarchyManager.getRootByLibId(docLibId).getId();
				//当前文档库修改前的所有管理员
				List<Long> oldOwners = docLibManager.getOwnersByDocLibId(docLibId);			
				List<Long> newOwnerSet = new ArrayList<Long>();//需要计算
				if(memberIds!=null){
					for(Long owner:oldOwners){
						if(!memberIds.contains(owner)){
							newOwnerSet.add(owner);
						}
					}
				}
				if(!newOwnerSet.equals(oldOwners)){
						for(Long o : oldOwners) {
							if(!newOwnerSet.contains(o)) 
								   docAclManager.deletePotentByUser(docResId,o, V3xOrgEntity.ORGENT_TYPE_MEMBER, docLib.getType(), docLibId);
								   this.clearAlertData(docResId, o);
						}
						int minOrder = docAclManager.getMaxOrder();
						for(Long userId:newOwnerSet){
							docAclManager.deletePotentByMaUser(docResId, userId, V3xOrgEntity.ORGENT_TYPE_MEMBER, docLib.getType(), docLibId);
							docAclManager.setDeptSharePotent(userId, V3xOrgEntity.ORGENT_TYPE_MEMBER, docResId, Constants.ALLPOTENT, false, null,minOrder++);
							list.add(userId);
						}
						docLibManager.deleteDocLibOwners(docLibId);	//删除已经授权的用户
						docLibManager.addDocLibOwners(docLibId, list);						
				}
				docLibManager.modifyDocLib(docLib,docLib.getName());  //修改文档库
			}
		}
	}
	
	//删除公共信息管理员  wusb 2010-09-01
	private void deletePublicManager(List<Long> memberIds,Long oldAccountId) throws Exception {
		//删除公告管理员
		deleteBulManager(memberIds,oldAccountId);
		//删除调查管理员
		deleteInquiryManager(memberIds,oldAccountId);
		//删除讨论管理员  
		deleteBbsManager(memberIds,oldAccountId);
		//删除新闻管理员  
		deleteNewsManager(memberIds,oldAccountId);
	}
	
	//删除综合办公权限管理员  wusb 2010-09-01
	private void deleteOfficeManager(List<Long> memberIds,List<Long> deptIds,Long oldAccountId) throws Exception {
		List<MAdminSetting> list = officeAdminManager.findAdminSetting(oldAccountId, null, null);
		if(list!=null){
			for(MAdminSetting admin:list){
				MAdminSettingId id = admin.getId();
				if(memberIds.contains(id.getAdmin()) ||
					(id.getMngdepId()!=null && deptIds.contains(Long.valueOf(id.getMngdepId())))){
					officeAdminManager.deleteAdminSetting(admin);
				}
			}
		}
	}
	
	//删除 工作管理设置人员  wusb 2010-12-21
	private void deleteWorkManager(List<Long> memberIds,List<Long> deptIds,Long oldAccountId) throws Exception {
		 List<ManagementSet> list = workStatManager.findSetListByDomainId(oldAccountId);
		if(list!=null){
			for(ManagementSet set:list){
				if(null!=set){
					String memberId = set.getMemberId();
					memberId = cutString(memberIds,memberId,",");
					if(Strings.isBlank(memberId)){
						workStatManager.deleteSetAndAcls(String.valueOf(set.getId()));
					}else{
						Set<ManagementSetAcl> acls = set.getManagementSetAcls();
						String aclsStr= "";
						int k=0;
						for(ManagementSetAcl acl : acls){
							if("Member".equals(acl.getAclType())){
								if(!memberIds.contains(acl.getAclId())){
									if(k==0){
										aclsStr=aclsStr+"Member|"+acl.getAclId();
									}else{
										aclsStr=aclsStr+",Member|"+acl.getAclId();
									}
									k++;
								}
							}else if("Department".equals(acl.getAclType())){
								if(!deptIds.contains(acl.getAclId())){
									if(k==0){
										aclsStr=aclsStr+"Department|"+acl.getAclId();
									}else{
										aclsStr=aclsStr+",Department|"+acl.getAclId();
									}
									k++;
								}
							}
						}
						if(Strings.isBlank(aclsStr)){
							workStatManager.deleteSetAndAcls(String.valueOf(set.getId()));
						}else{
							workStatManager.updateSetAndAcls(set.getId(), memberId, aclsStr, set.getManageRange());
						}
					}
				}
			}
		}
	}
	
	//删除印章管理员  wusb 2010-09-01
	private void deleteSignetManager(List<Long> memberIds,Long oldAccountId) throws Exception {
		List<V3xSignet> signetList = signetManager.findAllAccountID(oldAccountId);
		if(signetList!=null){
			for(V3xSignet signet:signetList){
				if(memberIds.contains(StringUtils.isBlank(signet.getUserName())?"":Long.parseLong(signet.getUserName()))){
					signet.setUserName("");
					signetManager.update(signet);
				}		
			}
		}
	}
	
	//删除公告管理员  wusb 2010-09-01
	private void deleteBulManager(List<Long> memberIds,Long oldAccountId) throws Exception {
		List<BulType> bulTypes =  bulTypeManager.boardFindAllByAccountId(oldAccountId);
		if(bulTypes!=null){
			for(BulType bean:bulTypes){
				String managerUserIds=bean.getManagerUserIds();
				String managerUserNames=bean.getManagerUserNames();
				String[] newManagers = getNewManagers(memberIds,new String[]{managerUserIds,managerUserNames},"、");
				boolean flag=false;
				if(!managerUserIds.equals(newManagers[0])){
					bean.setManagerUserIds(newManagers[0]);
					bean.setManagerUserNames(newManagers[1]);
					flag=true;
				}
				if(memberIds.contains(bean.getAuditUser())){
					bean.setAuditUser(-1L);
					bean.setAuditUserName("");
					flag=true;
				}	
				if(flag)
					bulTypeManager.save(bean);
			
				//删除授权人员
				Set<BulTypeManagers> oldSet = bean.getBulTypeManagers();
				if(oldSet!=null && oldSet.size()>0){
					Set<BulTypeManagers> newSet=new HashSet<BulTypeManagers>();
					for(BulTypeManagers btm : oldSet){
						if(com.seeyon.v3x.bulletin.util.Constants.WRITE_FALG.equals(btm.getExt1())
								&& !memberIds.contains(btm.getManagerId())){
							newSet.add(btm);
						}
					}
					String[][] writeIds=new String[newSet.size()][2];
					int k = 0;
					for (BulTypeManagers btm : newSet) {
						writeIds[k][0]=btm.getExt2();
						writeIds[k][1]= String.valueOf(btm.getManagerId());
						k ++;
					}							
					bulTypeManager.saveWriteByType(bean.getId(), writeIds);
				}
			}
		}
	}
	
	//删除调查管理员及授权人员
	private void deleteInquiryManager(List<Long> memberIds, Long oldAccountId) throws Exception {
		List<InquirySurveytype> typelist = inquiryManager.getAccountSurveyTypeList(oldAccountId);
		if (typelist != null) {
			boolean isReloadAllType = false;
			for (int k = 0; k < typelist.size(); k++) {
				InquirySurveytype surveytype = typelist.get(k);
				Set<InquirySurveytypeextend> managerSet = surveytype.getInquirySurveytypeextends();
				Set<InquirySurveytypeextend> oldSet = new HashSet<InquirySurveytypeextend>();
				if (CollectionUtils.isNotEmpty(managerSet)) {
					oldSet.addAll(managerSet);
					Set<InquirySurveytypeextend> temp = new HashSet<InquirySurveytypeextend>();
					for (InquirySurveytypeextend manager : oldSet) {
						if (memberIds.contains(manager.getManagerId())) {
							temp.add(manager);
						}
					}
					oldSet.removeAll(temp);
				}

				Set<InquiryAuthority> authSet = surveytype.getInquiryAuthorities();
				Set<InquiryAuthority> oldAuthSet = new HashSet<InquiryAuthority>();
				Set<InquiryAuthority> tempAuthSet = new HashSet<InquiryAuthority>();
				if (CollectionUtils.isNotEmpty(authSet)) {
					oldAuthSet.addAll(authSet);
					for (InquiryAuthority auth : oldAuthSet) {
						if (memberIds.contains(auth.getAuthId())) {
							tempAuthSet.add(auth);
						}
					}
				}

				if (k == typelist.size() - 1)
					isReloadAllType = true;
				inquiryManager.updateInquiryType(surveytype, oldSet, tempAuthSet, isReloadAllType);
			}
		}
	}
	
	//删除讨论管理员  wusb 2010-09-01
	private void deleteBbsManager(List<Long> memberIds,Long oldAccountId) throws Exception {
		List<V3xBbsBoard> bbsBoardList = bbsBoardManager.getAllCorporationBbsBoard(oldAccountId);
		if(bbsBoardList!=null){
			List<Long> temp = new ArrayList<Long>();
			for(V3xBbsBoard bbsBoard:bbsBoardList){
				List<Long> admins = bbsBoard.getAdmins();
				for(Long admin:admins){
					if(memberIds.contains(admin)){
						temp.add(admin);
					}
				}
				admins.removeAll(temp);
				bbsBoardManager.updateV3xBbsBoard(bbsBoard, admins);
			}
		}
	}
	
	//删除新闻管理员  wusb 2010-09-01
	private void deleteNewsManager(List<Long> memberIds,Long oldAccountId) throws Exception {
		List<NewsType> list = newsTypeManager.findAllByPage(oldAccountId);
		if(list!=null){
			for(NewsType bean:list){
				String managerUserIds = bean.getManagerUserIds();
				String managerUserNames = bean.getManagerUserNames();
				String[] newManagers = getNewManagers(memberIds,new String[]{managerUserIds,managerUserNames},",");
				boolean flag=false;
				if(!managerUserIds.equals(newManagers[0])){
					bean.setManagerUserIds(newManagers[0]);
					bean.setManagerUserNames(newManagers[1]);
					flag=true;
				}
				if(memberIds.contains(bean.getAuditUser())){
					bean.setAuditUser(-1L);
					bean.setAuditUserName("");
					flag=true;
				}	
				if(flag)
					newsTypeManager.save(bean);
				
				//删除授权人员
				Set<NewsTypeManagers> oldSet= bean.getNewsTypeManagers();
				if(oldSet!=null && oldSet.size()>0){
					Set<NewsTypeManagers> newSet=new HashSet<NewsTypeManagers>();
					for(NewsTypeManagers btm : oldSet){
						if(com.seeyon.v3x.bulletin.util.Constants.WRITE_FALG.equals(btm.getExt1())
								&& !memberIds.contains(btm.getManagerId())){
							newSet.add(btm);
						}
					}
					String[][] writeIds=new String[newSet.size()][2];
					int k = 0;
					for (NewsTypeManagers btm : newSet) {
						writeIds[k][0]=btm.getExt2();
						writeIds[k][1]= String.valueOf(btm.getManagerId());
						k ++;
					}	
					newsTypeManager.saveWriteByType(bean.getId(), writeIds);
				}
			}
		}
	}
	
	//部门移动时，不保留部门主管领导角色  wusb 2010-11-3
	private boolean isDepLeader(Long roleId,List<V3xOrgRole> oldDeptRoles){
		try {
			V3xOrgRole role = orgManager.getRoleById(roleId);
			if(role!=null){		
				if(V3xOrgEntity.ORGENT_META_KEY_DEPLEADER.equals(role.getName()))
					return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	//把个人所拥有的模板授权给他  wusb 2010-09-16
	private void pushNewOrgEntityTemplete4Member(List<Long> memberIds) throws Exception {
		for(Long memberId:memberIds){
			 templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,memberId,memberId);
		}
	}
	
	private String[] getNewManagers(List<Long> memberIds,String[] oldManagers,String sp) throws Exception{
		String[] newManagers=new String[2];
		Map<String,String> map = new LinkedHashMap<String,String>();
		String newId="";
		String newName="";
		if(StringUtils.isNotBlank(oldManagers[0])){
			String[] oldId=oldManagers[0].split(",");
			String[] oldName=null;
			boolean oldNameIsNull=false;
			if(StringUtils.isBlank(oldManagers[1])){
				oldNameIsNull=true;
			}else{
				oldName=oldManagers[1].split(sp);
			}
			for (int k = 0; k < oldId.length; k++) {
				if(!memberIds.contains(Long.valueOf(oldId[k]))){
					String tempName="";
					if(oldNameIsNull){
						tempName = orgManager.getMemberById(Long.parseLong(oldId[k])).getName();
					}else{
						tempName = oldName[k];
					}
					map.put(oldId[k],tempName);
				}
			}
			int a=0;
			for(String id:map.keySet()){
				if(a==0){
					newId+=id;
					newName+=map.get(id);
				}else{
					newId+=","+id;
					newName+=sp+map.get(id);
				}
				a++;
			}
			newManagers[0]=newId;
			newManagers[1]=newName;
			return newManagers;
		}
		return oldManagers;
	}
	
	private static String cutString(List<Long> ids, String contentStr, String sp) {
		if (contentStr != null && !"".equals(contentStr)) {
			String[] arr = contentStr.split(sp);
			List<String> temp=new ArrayList<String>();
			if (arr != null && arr.length > 0) {
				for (String str : arr) {
					if(str!=null && !"".equals(str) && !ids.contains(Long.valueOf(str))){
						temp.add(str);
					}
				}
			}
			String resultStr="";
			for (int k = 0; k < temp.size(); k++) {
				if(k==0){
					resultStr+=temp.get(k);
				}else{
					resultStr+=sp+temp.get(k);
				}
			}
			return resultStr;
		}
		return contentStr;

	}

	private void clearAlertData(long docResId, Long o) {
	    DocResource  doc = docHierarchyManager.getDocResourceById(docResId);
		docAlertManager.deleteAllAlertByDocResourceIdAndOrg(doc, V3xOrgEntity.ORGENT_TYPE_MEMBER, o);
	}

	
	private List getMembersSecurity(List<V3xOrgMember> members, long accountId){
		List<Long> securityIdsList = new ArrayList<Long>();
		if(members == null || members.isEmpty()){
			return securityIdsList;
		}
		
		
		List<Security> defaultSecurities = menuManager.getDefaultSecurities();
		for(Security security : defaultSecurities){
			securityIdsList.add(security.getId());					
		}
		
		if(securityIdsList.isEmpty()){
			return securityIdsList;
		}
		
		List<SecurityMember> os = new ArrayList<SecurityMember>();
		for (V3xOrgEntity member : members) {
			for (Long securityId : securityIdsList) {
				SecurityMember s = new SecurityMember(member.getId(), securityId, accountId);
                s.setIdIfNew();                
                os.add(s);
			}
		}
		
		return os;
	}

	
	public void updateLevel(V3xOrgLevel level) throws BusinessException {
		orgManagerDirect.updateEntity(level);
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setBlogManager(BlogManager blogManager) {
		this.blogManager = blogManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void addUnOrgMember(V3xOrgMember member) throws BusinessException {
		// TODO Auto-generated method stub
		orgManagerDirect.addUnOrganiseMember(member);
	}

	public void updateUnOrgMember(V3xOrgMember member) throws BusinessException {
		// TODO Auto-generated method stub
		orgManagerDirect.updateUnOrganiseMember(member);
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}
	
	public boolean isLoadData() {
		return orgManagerDirect.isLoadData();
	}

	public void setLoadData(boolean isLoadData) {
		orgManagerDirect.setLoadData(isLoadData);
	}
	
	public void reloadOrganizationModel() throws BusinessException{
		orgManagerDirect.reloadOrganizationModel();
	}
	
	public void reloadAccountData(Long accountId) throws BusinessException {
		orgManagerDirect.reloadAccountData(accountId);
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public BlogManager getBlogManager() {
		return blogManager;
	}

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public DocLibManager getDocLibManager() {
		return docLibManager;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public DocAlertManager getDocAlertManager() {
		return docAlertManager;
	}

	public void setDocAlertManager(DocAlertManager docAlertManager) {
		this.docAlertManager = docAlertManager;
	}

	public DocAclManager getDocAclManager() {
		return docAclManager;
	}

	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public AdminManager getOfficeAdminManager() {
		return officeAdminManager;
	}

	public void setOfficeAdminManager(AdminManager officeAdminManager) {
		this.officeAdminManager = officeAdminManager;
	}

	public SignetManager getSignetManager() {
		return signetManager;
	}

	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}

	public IOperBase getiOperBase() {
		return iOperBase;
	}

	public void setiOperBase(IOperBase iOperBase) {
		this.iOperBase = iOperBase;
	}

	public TempleteConfigManager getTempleteConfigManager() {
		return templeteConfigManager;
	}

	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}
	public IndexInfo getIndexInfo(long memberId) throws Exception {
		if(!IndexInitConfig.hasLuncenePlugIn()){
			return null;
		}
		IndexInfo info=new IndexInfo();
		try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			if(member==null){return null;}
			if(member.getIsAdmin()){return null;}
			info.setEntityID(memberId);
			info.setAppType(ApplicationCategoryEnum.organization);
			info.setStartMemberId(memberId);
			info.setTitle(member.getName());
			info.setAuthor(member.getName());
			info.setCreateDate(member.getCreateTime());
			info.setContentType(0);
			info.setUserCode(member.getCode());
			StringBuilder sb = new StringBuilder();
			
			IndexExtPropertiesConfig indexExtPro = (IndexExtPropertiesConfig)ApplicationContextHolder.getBean("extPropertiesConfig");
			String[] properties = indexExtPro.getField(ApplicationCategoryEnum.organization.name());
			
			processExtProperties(properties,info,member,sb);
			
			V3xOrgDepartment dept=orgManager.getDepartmentById(member.getOrgDepartmentId());
			info.addExtendProperties(properties[3], dept==null?"":dept.getName(), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			
			V3xOrgPost post=orgManager.getPostById(member.getOrgPostId());
			info.addExtendProperties(properties[4], post==null?"":post.getName(), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			
			V3xOrgLevel level= orgManager.getLevelById(member.getOrgLevelId());
			info.addExtendProperties(properties[5], level==null?"":level.getName(), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			
			ContactInfo contact = staffInfoManager.getContactInfoById(member.getId());
			if(contact!=null){
				sb.append(processNullInfo(contact.getAddress()));
				sb.append(processNullInfo(contact.getPostalcode()));
				sb.append(processNullInfo(contact.getWebsite()));
				sb.append(processNullInfo(contact.getBlog()));
				sb.append(processNullInfo(contact.getCommunication()));
			}
			 info.addExtendProperties(properties[6], processNullInfo(member.getDescription()), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			 
			 sb.append(dept==null?"":dept.getName()).append(" ");
			 sb.append(post==null?"":post.getName()).append(" ");
			 sb.append(level==null?"":level.getName()).append(" ");
			 sb.append(processNullInfo(member.getDescription()));
			 sb.append(processNullInfo(member.getCode()));
			 
			info.setContent(sb.toString());
		
			
			String picturePath ="/seeyon/apps_res/v3xmain/images/personal/pic.gif";
			StaffInfo staffInfo=staffInfoManager.getStaffInfoById(member.getId());
			if(staffInfo!=null){
				if (Strings.isNotBlank(staffInfo.getSelf_image_name())&&!staffInfo.getSelf_image_name().equals("pic.gif")) {
					if(staffInfo.getSelf_image_name().startsWith("fileId")&&staffInfo.getSelf_image_name().indexOf("createDate")!=-1){
//						picturePath="/seeyon/fileUpload.do?method=showRTE&fileId="+staffInfo.getSelf_image_name()+"&type=image&createDate="+Datetimes.formatDate(staffInfo.getImage_datetime());
						picturePath="/seeyon/fileUpload.do?method=showRTE&"+staffInfo.getSelf_image_name()+"&type=image";
					}else{
						picturePath="/seeyon/apps_res/v3xmain/images/personal/"+staffInfo.getSelf_image_name();
					}
				}else{
					if(staffInfo.getImage_id()!=null)
					{
						picturePath="/seeyon/fileUpload.do?method=showRTE&fileId="+staffInfo.getImage_id()+"&type=image&createDate="+Datetimes.formatDate(staffInfo.getImage_datetime());
					}
				}
			}
			info.setPicturePath(picturePath);
			AuthorizationInfo authorizationInfo=new AuthorizationInfo();
			List<String> owner=new ArrayList<String>();
			owner.add("Person");
			authorizationInfo.setOwner(owner);
			info.setAuthorizationInfo(authorizationInfo);
		} catch (Exception e) {
			log.error("人员入索引库异常",e);
		}
		return info;
	}
	private void processExtProperties(String[] properties,IndexInfo index,V3xOrgMember member,StringBuilder sb) throws BusinessException
	{
			index.addExtendProperties(properties[0], processNullInfo(member.getProperty("officeNum")), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			index.addExtendProperties(properties[1], processNullInfo(member.getTelNumber()), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			index.addExtendProperties(properties[2], processNullInfo(member.getEmailAddress()), IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal());
			
			sb.append(processNullInfo(member.getProperty("officeNum")));
			sb.append(processNullInfo(member.getTelNumber()));
			sb.append(processNullInfo(member.getEmailAddress()));
	}
	private String processNullInfo(String string)
	{
		return StringUtils.isBlank(string)?"":string+" ";
	}
	@Override
	public void addTeam(V3xOrgTeam team) throws BusinessException {
		if(team.getName() == null){
			throw new BusinessException("error add team for null name");
		}
		Integer maxSort = orgManagerDirect.getMaxSortNum(V3xOrgTeam.class.getSimpleName(), team.getOrgAccountId());
		team.setSortId(maxSort+1);
		orgManagerDirect.addTeam(team);
	}

	@Override
	public void updateTeam(V3xOrgTeam team) throws BusinessException {
		if(team.getName() == null){
			throw new BusinessException("error add team for null name");
		}
			orgManagerDirect.updateEntity(team);
	
	}
}