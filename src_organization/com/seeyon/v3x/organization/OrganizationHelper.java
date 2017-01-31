/**
 * 
 */
package com.seeyon.v3x.organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.login.principal.domain.JetspeedPrincipal;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.principal.NoSuchPrincipalException;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-10
 */
public class OrganizationHelper {
	private static Log log = LogFactory.getLog(OrganizationHelper.class);
	
	/**
	 * 人员搜索
	 * 
	 * @param condition 查询条件
	 * @param textfield 查询值       
	 * @param searchManager     
	 * @param orgManager
	 * @param includeDisabled 是否包含停用帐号
	 * @return
	 */	
	
	/**
	 * 人员搜索
	 * 
	 * @param condition
	 *            查询条件
	 * @param textfield
	 *            查询值
	 * @param searchManager
	 * @param orgManager
	 * @param includeDisabled
	 *            是否包含停用帐号
	 * @param allMember
	 *            是否查询所有人员
	 *            
	 * @see 查询所有人员时，系统管理员和审计管理员无法查出，请注意
	 */
	public static List<V3xOrgMember> searchMember(String type, String condition, String textfield, SearchManager searchManager, OrgManagerDirect orgManager, boolean isPaginate, boolean isInternal, boolean includeDisabled, boolean allMember){
		PrincipalManager  principalManager = (PrincipalManager)ApplicationContextHolder.getBean("principalManager");		
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
		Long accountId = CurrentUser.get().getLoginAccount();
		List<V3xOrgMember> sublist = new ArrayList<V3xOrgMember>();
		StringBuilder strbuf = new StringBuilder();
		Map<String, Object> param = new HashMap<String, Object>();
		strbuf.append("select max(a), max(a.id),max(a.sortId) from " + V3xOrgMember.class.getName() + " as a ");
		strbuf.append(" where a.isAssigned='1' and a.isDeleted='0' ");
		if(!allMember){
			strbuf.append(" and a.isAdmin = '0' ");
			if(!isInternal){
				strbuf.append(" and a.isInternal = '0' ");
			}else{
				strbuf.append(" and a.isInternal = '1' ");
			}
		}
		if(!CurrentUser.get().isSystemAdmin()){
		    strbuf.append(" and a.orgAccountId=:orgAccountId ");
		    param.put("orgAccountId",accountId);
		}
		
		if(!includeDisabled){
			strbuf.append("and a.enabled=:enabled");
			param.put("enabled", Boolean.TRUE);
		}
		
		parseQueryCondition(type, condition, textfield, principalManager, strbuf, param);
		strbuf.append(" group by a.id, a.sortId order by a.sortId asc");
		
		List<Object[]> list = searchManager.searchByHql(strbuf.toString(), param, isPaginate);
		final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
	      for (Object[] objects : list) {
	        V3xOrgMember m = (V3xOrgMember)objects[0];
/*			try {*/
	        
			final String loginName = idLoginNameMap.get(m.getId());
			if(loginName!=null){
				m.setLoginName(loginName);
			}else{
				log.error("人员的登录名不存在。"+m.getId());
				continue;
			}
/*			} catch (NoSuchPrincipalException e) {
				log.error("人员的登录名不存在。"+m.getId());
				continue;
			}*/
	        memberlist.add(m);
	      }
		sublist = memberlist;
		Collections.sort(sublist,CompareSortEntity.getInstance());
		
		return sublist;		
	}
	public static List<V3xOrgMember> searchMember(String condition, String textfield, SearchManager searchManager, OrgManagerDirect orgManager, boolean isPaginate, boolean isInternal, boolean includeDisabled){
		return searchMember(null, condition, textfield, searchManager, orgManager, isPaginate, isInternal, includeDisabled);
	}
	public static List<V3xOrgMember> searchMember(String type, String condition, String textfield, SearchManager searchManager, OrgManagerDirect orgManager, boolean isPaginate, boolean isInternal, boolean includeDisabled){
		return searchMember(type, condition, textfield, searchManager, orgManager, isPaginate, isInternal, includeDisabled, false);
	}
	private static void parseQueryCondition(String type,String condition, String textfield,
			PrincipalManager principalManager, StringBuilder strbuf, Map<String, Object> param) {
		
		if (Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
			if (condition.equals("name")) {
				strbuf.append(" and a.name like :textfield ");
				param.put("textfield", "%" + textfield + "%");
			}
			else if (condition.equals("orgPostId")) {
				strbuf.append(" and a.orgPostId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}
			else if (condition.equals("orgLevelId")) {
				strbuf.append(" and a.orgLevelId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}
			else if (condition.equals("orgDepartmentId")) {
				strbuf.append(" and a.orgDepartmentId=:textfield");
				param.put("textfield", Long.valueOf(textfield)); 
			}
			else if (condition.equals("secondPostId")) {
				strbuf.append(" and a.orgLevelId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}
			else if (condition.equals("code")) {
				strbuf.append(" and a.code like :textfield");
				param.put("textfield", "%" + textfield + "%");
			}
			else if (condition.equals("loginName")) {
				/*
				List<String> loginNames = principalManager.getAllLoginNames();
				List<Long> memberIds= new ArrayList<Long>();
				if(loginNames!=null){
					for (String loginName : loginNames) {
						if(loginName.contains(textfield)){
							try {
								long memberId = principalManager.getMemberIdByLoginName(loginName);
								memberIds.add(memberId);
							} catch (NoSuchPrincipalException e) {
								log.error("人员不存在:"+loginName);
							}
						}
					}
				}
				if(memberIds.size()==0){
					memberIds.add(V3xOrgEntity.DEFAULT_NULL_ID);
				}*/
				//修改AEIGHT-3004按照登录名查询hql中in的参数超过1000的问题
				strbuf.append(" and a.id in ( select sp.entityId from "+JetspeedPrincipal.class.getName()+" sp where sp.fullPath like :textfield ) ");
				param.put("textfield","%/user/%"+textfield+"%");
			}
			else if (condition.equals("typeId")) {
				strbuf.append(" and a.type=:textfield");
				param.put("textfield", Byte.valueOf(textfield));
			}
			else if (condition.equals("state") || condition.equals("stateId")) {
				strbuf.append(" and a.state=:textfield");
				param.put("textfield",Byte.valueOf(textfield));
			}
			else if (condition.equals("enabled")) {
				strbuf.append(" and a.enabled=:textfield");
				param.put("textfield",Boolean.valueOf(textfield));
			}
			if(type!=null){
				  if (("incumbency").equals(type)) {
						strbuf.append(" and a.state=1");
					}
					else if (("dimission").equals(type)) {
						strbuf.append(" and a.state=2");
					}
					else if (("enable").equals(type)) {
						strbuf.append(" and a.enabled = true");
					}
					else if (("disable").equals(type)) {
						strbuf.append(" and a.enabled= false ");
					}
			}
		  
			
			
		}else if(condition!=null&&condition.equals("all")){
			//isPaginate = true;
		}else{
			strbuf.append(" and a.enabled = '1' ");	
			//isPaginate = true;
		}
	}
	
	
	
	/**
	 * 部门下所有人员搜索
	 * @param path 部门路径
	 * @param searchManager
	 * @param orgManager
	 * @param isPaginate
	 * @param isInternal
	 * @return
	 */
	public static List<V3xOrgMember> searchDepartmentMember(String path,SearchManager searchManager, OrgManagerDirect orgManagerDirect,PrincipalManager principalManager){
		Long accountId = CurrentUser.get().getLoginAccount();
		List<V3xOrgMember> sublist = new ArrayList<V3xOrgMember>();
		List<Long> memberIdList = new ArrayList<Long>();
		StringBuffer strbuf = new StringBuffer();
		V3xOrgMember member =null;
		Map<String, Object> param = new HashMap<String, Object>();
		strbuf.append("select max(a), max(a.id), max(a.sortId) from "
				+ V3xOrgMember.class.getName() + " a" 
				+ "  where a.isAssigned='1' and a.enabled='1'  and a.isInternal = '1' and a.isDeleted='0' and a.state='1' and a.isAdmin = '0' and a.orgAccountId=:orgAccountId ")
				.append("and a.orgDepartmentId in (select d.id from ")
				.append(V3xOrgDepartment.class.getName())
				.append(" d where (d.path='")
				.append(path)
				.append("' or d.path like '")
				.append(path)
				.append(".%'))  group by a.id, a.sortId order by a.sortId asc");
		param.put("orgAccountId",accountId);
		List<Object[]> list = searchManager.searchByHql(strbuf.toString(), param, false);
		final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
		for (Object[] objects : list) {
			V3xOrgMember m = (V3xOrgMember)objects[0];
//			m.setLoginName(UserPrincipalUtil.getPrincipalNameFromFullPath(((String)objects[2])));
/*			try {*/
			final String loginName = idLoginNameMap.get(m.getId());
			if(loginName!=null){
				m.setLoginName(loginName);
			}else{
				log.error("人员的登录名不存在。"+m.getId());
				continue;				
			}
/*			} catch (NoSuchPrincipalException e) {
				log.error("人员的登录名不存在。"+m.getId());
				continue;
			}*/
			sublist.add(m);
			memberIdList.add(m.getId());
		}
		
		V3xOrgDepartment vt=new V3xOrgDepartment();
		//取得本部门下的兼职人员
			List<V3xOrgRelationship> cntPosts = null;
			StringBuffer hql = new StringBuffer("from V3xOrgRelationship where ");
			hql.append("type='")
			.append(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST)
			.append("' and objectiveId in (select d.id from ")
			.append(V3xOrgDepartment.class.getName())
			.append(" d where d.orgAccountId=:orgAccountId and (d.path='")
			.append(path)
			.append("' or d.path like '")
			.append(path)
			.append(".%'))");
			
			cntPosts = (ArrayList<V3xOrgRelationship>)searchManager.searchByHql(hql.toString(), param, false);
			
			if(cntPosts!=null&&cntPosts.size()>0){
				for(V3xOrgRelationship rel:cntPosts){
					if(!memberIdList.contains(rel.getSourceId())){
						 try {
							member = (V3xOrgMember)orgManagerDirect.getMemberById(rel.getSourceId());
						} catch (BusinessException e) {
							log.error(e.getMessage(), e);
						}
						if(member!=null&&member.getEnabled()){	
							if(sublist==null)
								sublist=new ArrayList<V3xOrgMember>();
							sublist.add(member);
							memberIdList.add(rel.getSourceId());
						}
				}
				}
			}
			
			//取得本部门下的副岗人员
			List<V3xOrgRelationship> memberPosts = null;
			StringBuffer postshql = new StringBuffer("from V3xOrgRelationship where ");
			postshql.append("type='")
			.append(V3xOrgEntity.ORGREL_TYPE_MEMBER_POST)
			.append("' and objectiveId in (select d.id from ")
			.append(V3xOrgDepartment.class.getName())
			.append(" d where d.orgAccountId=:orgAccountId and (d.path='")
			.append(path)
			.append("' or d.path like '")
			.append(path)
			.append(".%'))");
			memberPosts = (ArrayList<V3xOrgRelationship>)searchManager.searchByHql(postshql.toString(), param, false);
			if(memberPosts!=null&&memberPosts.size()>0){
				for(V3xOrgRelationship rel:memberPosts){
					if(!memberIdList.contains(rel.getSourceId())){
						 try {
							member = (V3xOrgMember)orgManagerDirect.getMemberById(rel.getSourceId());
						} catch (BusinessException e) {
							log.error(e.getMessage(), e);
						}
						if(member!=null&&member.getEnabled()){
							if(sublist==null)
								sublist=new ArrayList<V3xOrgMember>();
							sublist.add(member);
							memberIdList.add(rel.getSourceId());
						}
					}
				}
			}
		Collections.sort(sublist,CompareSortEntity.getInstance());
		return sublist;		
	}
	
	public static DataRecord exportAccount(List<WebV3xOrgAccount> resultlist,
			HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager){
		DataRecord dataRecord = new DataRecord();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		
		String permission_all = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.all");
		String permission_up = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.up");
		String permission_upAnddown = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.upAnddown");
		String permission_upAndpar = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.upAndpar");
		String permission_par = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.par");
		String permission_paranddown = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.parAnddown");
		String permission_down = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.down");
		String permission_no = ResourceBundleUtil.getString(resource, local, "org.metadata.access_permission.no");
		
		String company_name = ResourceBundleUtil.getString(resource, local, "org.account_form.name.label");
		String company_shotName = ResourceBundleUtil.getString(resource, local, "org.account_form.shortname.label");
		String company_secondName = ResourceBundleUtil.getString(resource, local, "org.account_form.secondName.label");
		String company_sortId = ResourceBundleUtil.getString(resource, local, "org.account_form.sortId.label");
		String company_code = ResourceBundleUtil.getString(resource, local, "org.account_form.code.label");
		String company_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String company_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
		String company_superior = ResourceBundleUtil.getString(resource, local, "org.account_form.superior.label");
		String company_alias = ResourceBundleUtil.getString(resource, local, "org.account_form.alias.label");
		String company_permission = ResourceBundleUtil.getString(resource, local, "org.account_form.permission.label");
		String company_type = ResourceBundleUtil.getString(resource, local, "org.account_form.type.label");
		String company_level = ResourceBundleUtil.getString(resource, local, "org.account_form.level.label");
		String company_kind = ResourceBundleUtil.getString(resource, local, "org.account_form.kind.label");
		String company_manager = ResourceBundleUtil.getString(resource, local, "org.account_form.manager.label");
		String company_address = ResourceBundleUtil.getString(resource, local, "org.account_form.address.label");
		String company_zipCode = ResourceBundleUtil.getString(resource, local, "org.account_form.zipCode.label");
		String company_telephone = ResourceBundleUtil.getString(resource, local, "org.account_form.telephone.label");
		String company_fax = ResourceBundleUtil.getString(resource, local, "org.account_form.fax.label");
		String company_ipAddress = ResourceBundleUtil.getString(resource, local, "org.account_form.ipAddress.label");
		String company_accountMail = ResourceBundleUtil.getString(resource, local, "org.account_form.accountMail.label");
		String company_decription = ResourceBundleUtil.getString(resource, local, "org.account_form.decription.label");
		String company_adminName = ResourceBundleUtil.getString(resource, local, "org.account_form.adminName.label");
		String company_passWord = ResourceBundleUtil.getString(resource, local, "org.account_form.adminPass.label");
		String company_role_assign = ResourceBundleUtil.getString(resource, local, "org.account_form.role.assign");
		String company_list = ResourceBundleUtil.getString(resource, local, "org.account_form.list");
		String company_isRoot_yes = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.yes");
		String company_isRoot_no = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.no");
		String commpany_isRoot = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.label");
		String commpany_GroupShortname = ResourceBundleUtil.getString(resource, local, "org.account_form.groupshortname.label");
		
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);

		if (null != resultlist && resultlist.size() > 0) {
			DataRow[] datarow = new DataRow[resultlist.size()];
			for (int i = 0; i < resultlist.size(); i++) {
				WebV3xOrgAccount account = resultlist.get(i);
				DataRow row = new DataRow();
//				单位名称
				row.addDataCell(account.getV3xOrgAccount().getName(), 1);
//				单位简称
				row.addDataCell(account.getV3xOrgAccount().getShortname(), 1);
//				外文名称
				row.addDataCell(account.getV3xOrgAccount().getSecondName(), 1);
//				别名
				row.addDataCell(account.getV3xOrgAccount().getAlias(), 1);
//				排序号
				row.addDataCell(account.getV3xOrgAccount().getSortId().toString(), 1);
//				代码
				row.addDataCell(account.getV3xOrgAccount().getCode(), 1);
//				创建时间
				row.addDataCell(Datetimes.format(account.getV3xOrgAccount().getCreateTime(), "yyyy-MM-dd"), 6);
//				状态
				if (account.getV3xOrgAccount().getEnabled() == true) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
//				是否为集团根单位
				if(account.getV3xOrgAccount().getIsRoot()){
					row.addDataCell(company_isRoot_yes, 1);
				}else{
					row.addDataCell(company_isRoot_no, 1);
				}
//				集团简写名称
				row.addDataCell(account.getV3xOrgAccount().getGroupShortname(), 1);
//				上级单位
				row.addDataCell(account.getSuperiorName(), 1);
//				被访问权限
				if(account.getV3xOrgAccount().getAccessPermission()!=null){
					if(account.getV3xOrgAccount().getAccessPermission()==0){
						row.addDataCell(permission_all, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==1){
						row.addDataCell(permission_up, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==2){
						row.addDataCell(permission_upAndpar, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==3){
						row.addDataCell(permission_upAnddown, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==4){
						row.addDataCell(permission_par, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==5){
						row.addDataCell(permission_paranddown, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==6){
						row.addDataCell(permission_down, 1);
					}else if(account.getV3xOrgAccount().getAccessPermission()==7){
						row.addDataCell(permission_no, 1);
					}else{
						row.addDataCell("", 1);
					}
				}else{
					row.addDataCell("", 1);
				}
				if(account.getAccountCategory() != null){
					Metadata mdcate = orgMeta.get("org_property_account_category");
					List<MetadataItem> mditelst = mdcate.getItems();
					String level = null;
					for(MetadataItem mi:mditelst){
						if(account.getAccountCategory().equals(mi.getValue())){
	//						单位类型
							level = ResourceBundleUtil.getString(resource, local, mi.getLabel());
						}
					}					
					if(level != null){
						row.addDataCell(level, 1);
					}else{
						row.addDataCell("", 1);
					}
				}else{
					row.addDataCell("", 1);
				}
				if(account.getAccountLevel() != null){
					Metadata levelmdcate = orgMeta.get("org_property_account_level");
					List<MetadataItem> levelmditelst = levelmdcate.getItems();
					String level = null;
					for(MetadataItem mi:levelmditelst){
						if(account.getAccountLevel().equals(mi.getValue())){
	//						单位类型
							level = ResourceBundleUtil.getString(resource, local, mi.getLabel());
						}
					}	
					if(level != null){
						row.addDataCell(level, 1);
					}else{
						row.addDataCell("", 1);
					}
				}else{
					row.addDataCell("", 1);
				}
				
				if(account.getAccountNature() != null){
					Metadata naturemdcate = orgMeta.get("org_property_account_nature");
					List<MetadataItem> naturemditelst = naturemdcate.getItems();
					String level = null;
					for(MetadataItem mi:naturemditelst){
						if(account.getAccountNature().equals(mi.getValue())){
							//单位性质
							level = ResourceBundleUtil.getString(resource, local, mi.getLabel());
						}
					}		
					if(level != null){
						row.addDataCell(level, 1);
					}else{
						row.addDataCell("", 1);
					}				
				}else{
					row.addDataCell("", 1);
				}

//				负责人
				row.addDataCell(account.getChiefLeader(), 1);
//				地址
				row.addDataCell(account.getAddress(), 1);
//				邮编
				row.addDataCell(account.getZipCode(), 1);
//				电话
				row.addDataCell(account.getTelephone(), 1);
//				传真
				row.addDataCell(account.getFax(), 1);
//				网址
				row.addDataCell(account.getIpAddress(), 1);
//				邮件地址
				row.addDataCell(account.getAccountMail(), 1);
//				单位描述
				row.addDataCell(account.getV3xOrgAccount().getDecription(), 1);
//				管理员登录名
				row.addDataCell(account.getV3xOrgAccount().getAdminName(), 1);
////				管理员登录密码
//				row.addDataCell("", 1);
////				单位角色指派
//				List<V3xOrgRole> roleList = orgManagerDirect.getAccountRole(account.getV3xOrgAccount().getId());
//				List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
//				for(V3xOrgRole role : roleList){
//					String members = "";
//					ml = orgManagerDirect.getMemberByRole(role.getBond(), role.getOrgAccountId(), role.getId());
//					if(ml!=null&&ml.size()!=0){
//						for(V3xOrgMember vom : ml){
//							if(vom!=null&&!vom.equals("")){
//								members+= vom.getName()+",";
//							}
//						}
//						if(members!=null&&!members.equals("")){
//							row.addDataCell(role.getName(), 1);
//							row.addDataCell(members.substring(0, members.length()-1),1);
//						}
//					}
////					else{
////						row.addDataCell(role.getName(), 1);
////						row.addDataCell(members, 1);
////					}
//				}
				
				
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}		
//		String[] columnName = { company_name , company_shotName ,company_secondName ,company_alias,company_sortId, company_code ,company_createDate,
//				company_state,commpany_isRoot,commpany_GroupShortname,company_superior,company_permission,company_type,company_level,
//				company_kind,company_manager,company_address,company_zipCode,company_telephone,company_fax,company_ipAddress,company_accountMail,company_decription,company_adminName,company_passWord,company_role_assign };
		String[] columnName = { company_name , company_shotName ,company_secondName ,company_alias,company_sortId, company_code ,company_createDate,
				company_state,commpany_isRoot,commpany_GroupShortname,company_superior,company_permission,company_type,company_level,
				company_kind,company_manager,company_address,company_zipCode,company_telephone,company_fax,company_ipAddress,company_accountMail,company_decription,company_adminName};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(company_list);
		dataRecord.setSheetName(company_list);

		return dataRecord;
	}
	
	public static DataRecord exportDept(List<WebV3xOrgDepartment> resultlist,
			HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect,SpaceManager spaceManager
			,V3xOrgAccount account) throws Exception{
		User user = CurrentUser.get();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		DataRecord dataRecord = new DataRecord();
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String dept_inner = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label.inner");
		String dept_out = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label.out");
		String dept_name = ResourceBundleUtil.getString(resource, local, "org.dept_form.name.label");
		String dept_code = ResourceBundleUtil.getString(resource, local, "org.dept_form.code.label");
		String dept_sortId = ResourceBundleUtil.getString(resource, local, "org.account_form.sortId.label");
		String dept_superDepartment = ResourceBundleUtil.getString(resource, local, "org.dept_form.superDepartment.label");
		String dept_kind = ResourceBundleUtil.getString(resource, local, "org.dept_form.isInternal.label");
		String dept_state = ResourceBundleUtil.getString(resource, local, "org.dept_form.enable.label");
		String dept_post = ResourceBundleUtil.getString(resource, local, "org.dept_form.post_fieldset.label");
		String dept_account = ResourceBundleUtil.getString(resource, local, "org.dept_form.account");
		String dept_descript = ResourceBundleUtil.getString(resource, local, "org.dept_form.descript_fieldset.label");
		String dept_manage_info = ResourceBundleUtil.getString(resource, local, "org.dept_form.manager_fieldset.label");
		String dept_list = ResourceBundleUtil.getString(resource, local, "org.dept_form.list");
//		是、否
		String yes = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.yes");
		String no = ResourceBundleUtil.getString(resource, local, "org.account_form.isRoot.no");
		String isCreateDeptSpace = ResourceBundleUtil.getString(resource, local, "org.dept_form.isCreateDeptSpace.label");
//		List<V3xOrgDepartment> deptlist = null;
//		String pid = request.getParameter("pId");
//		if (null != pid && !pid.equals("")) {
//			Long _pid = Long.parseLong(pid);
//			deptlist = orgManagerDirect.getChildDepartments(_pid, false);
//		} else {
//			deptlist = orgManagerDirect.getAllDepartments(true);
//		}
		if (null != resultlist && resultlist.size() > 0) {
			DataRow[] datarow = new DataRow[resultlist.size()];
			for (int i = 0; i < resultlist.size(); i++) {
				WebV3xOrgDepartment department = resultlist.get(i);
				StringBuffer strBuffPostNames = new StringBuffer();
				String deptKind = "";
				DataRow row = new DataRow();
				row.addDataCell(department.getV3xOrgDepartment().getName(), 1);
				row.addDataCell(department.getV3xOrgDepartment().getCode(), 1);
				row.addDataCell(department.getV3xOrgDepartment().getSortId()
						.toString(), 1);
				row.addDataCell(department.getParentName(), 1);
				if(department.getV3xOrgDepartment().getIsInternal()){
					deptKind = dept_inner;
				}else{
					deptKind = dept_out;
				}
				row.addDataCell(deptKind, 1);
				if(department.getV3xOrgDepartment().getEnabled()){
					row.addDataCell(state_Enabled, 1);
				}else{
					row.addDataCell(state_Disabled, 1);
				}
//				是否创建了部门空间
				if(spaceManager.isCreateDepartmentSpace(department.getV3xOrgDepartment().getId())){
					row.addDataCell(yes, 1);
				}else{
					row.addDataCell(no, 1);
				}
				
//				 获得部门岗位
				List<V3xOrgPost> listPost = orgManagerDirect.getDepartmentPost(department.getV3xOrgDepartment().getId());
				if (null != listPost && listPost.size() > 0) {
					for (V3xOrgPost post : listPost) {
						strBuffPostNames.append(post.getName());
						strBuffPostNames.append(",");
					}
				}
				if (strBuffPostNames.length() > 0) {
					row.addDataCell(strBuffPostNames.substring(0, strBuffPostNames.lastIndexOf(",")), 1);
				}else{
					row.addDataCell("", 1);
				}
				
				row.addDataCell(account.getName(), 1);
				row.addDataCell(department.getV3xOrgDepartment().getDescription(), 1);
//-------------------------------------------------部门管理信息（动态获取输出）---------------------------------------------------
				
//				List<V3xOrgRole> roleList = orgManagerDirect.getDepartmentRole(department.getV3xOrgDepartment().getId());
//				List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
//				String dynamic_column = "";
//				for(V3xOrgRole role : roleList){
//					String members = "";
//					dynamic_column+=role.getName()+",";
//					ml = orgManagerDirect.getMemberByRole(role.getBond(), role.getOrgAccountId(), role.getId());
//					if(ml!=null&&ml.size()!=0){
//						for(V3xOrgMember vom : ml){
//							members+= vom.getName()+",";
//						}
//						row.addDataCell(role.getName(), 1);
//						row.addDataCell(members.substring(0, members.length()-1),1);
//					}
//				}
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		//String[] columnName = { dept_name , dept_code, dept_sortId , dept_superDepartment ,dept_kind,dept_state,isCreateDeptSpace,dept_post,dept_account,dept_descript,dept_manage_info};
		String[] columnName = { dept_name , dept_code, dept_sortId , dept_superDepartment ,dept_kind,dept_state,isCreateDeptSpace,dept_post,dept_account,dept_descript};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(dept_list);
		dataRecord.setSheetName(dept_list);
		
		return dataRecord;
	}
	public static DataRecord exportLevel(
			HttpServletRequest request,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
//		ModelAndView result = new ModelAndView("organization/level/listLevel");
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String level_name = ResourceBundleUtil.getString(resource, local, "org.level_form.name.label");
		String level_code = ResourceBundleUtil.getString(resource, local, "org.level_form.code.label");
		String level_sortId = ResourceBundleUtil.getString(resource, local, "org.level_form.levelId.label");
		String level_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
		String level_account = ResourceBundleUtil.getString(resource, local, "org.account.lable");
		String level_descrption = ResourceBundleUtil.getString(resource, local, "org.level_form.description.label");
		String level_list = ResourceBundleUtil.getString(resource, local, "org.level_form.list");
		Pagination.setNeedCount(false);
		List<V3xOrgLevel> levellist = orgManagerDirect.getAllLevels(user.getLoginAccount(),false);
		if (null != levellist && levellist.size() > 0) {
			DataRow[] datarow = new DataRow[levellist.size()];
			for (int i = 0; i < levellist.size(); i++) {
				V3xOrgLevel level = levellist.get(i);
				DataRow row = new DataRow();
				row.addDataCell(level.getName(), 1);
				row.addDataCell(level.getCode(), 1);
				row.addDataCell(level.getLevelId().toString(), 1);
				if (level.getEnabled() == true) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
				V3xOrgAccount account = orgManagerDirect.getAccountById(level.getOrgAccountId());
				row.addDataCell(account.getName(), 1);
				row.addDataCell(level.getDescription(), 1);
				
				datarow[i] = row;
			}
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		String[] columnName = { level_name,level_code, level_sortId,level_state,level_account, level_descrption };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(level_list);
		dataRecord.setSheetName(level_list);
	
		return dataRecord;
	}
	
	/**
	 * 导出人员
	 * @param request
	 * @param orgManagerDirect 组织模型管理类
	 * @param searchManager 按条件查询工具类
	 * @param condition 查询条件属性
	 * @param textfield 查询条件值
	 * @param isInternal true为导出内部人员，false 为导出外部人员
	 * @return
	 * @throws Exception
	 */
	public static DataRecord exportMember(HttpServletRequest request,OrgManagerDirect orgManagerDirect,
			SearchManager searchManager, String condition, String textfield,boolean isInternal) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();		
		List<V3xOrgEntity> entList = new ArrayList<V3xOrgEntity>();
		
		if(searchManager!=null){
			memberlist = searchMember(condition, textfield, searchManager, orgManagerDirect, false, isInternal,true);
		}else{
			entList = orgManagerDirect.getEntityList(V3xOrgMember.class.getSimpleName(), "isInternal", isInternal, user.getLoginAccount(), false);
		}
		
		for(V3xOrgEntity ent:entList){
			memberlist.add((V3xOrgMember)ent);
		}	

		V3xOrgAccount account = null;
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;
		V3xOrgLevel level = null;
		Locale locale = LocaleContext.getLocale(request);
        //导出excel文件的国际化
		String member_name = ResourceBundleUtil.getString(resource, locale, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, locale, "org.member_form.loginName.label");
		String member_code = ResourceBundleUtil.getString(resource, locale, "org.member_form.code");
		String member_deptName = null;
		if(isInternal){
			member_deptName = ResourceBundleUtil.getString(resource, locale, "org.member_form.deptName.label");
		}
		else{
			member_deptName = ResourceBundleUtil.getString(resource, locale, "org.external.member.form.dept");
		}
		
		String member_primaryPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryPost.label");
		//branches_a8_v350_r_gov GOV-1924 杨帆 修改导出excel时职务的版本区别  start
		String member_levelName = ResourceBundleUtil.getString(resource, locale, "org.member_form.levelName.label"+Functions.suffix());
		//branches_a8_v350_r_gov GOV-1924 杨帆 修改导出excel时职务的版本区别  end
		String member_tel = ResourceBundleUtil.getString(resource, locale, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, locale, "org.member_form.account");
		String member_email = ResourceBundleUtil.getString(resource, locale, "org.member.emailaddress");
		String member_gender= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.sexe");
		String member_birthday= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.birthday");
		String member_officeNumber= ResourceBundleUtil.getString(resource, locale, "member.office.number");
		String member_list = ResourceBundleUtil.getString(resource, locale, "org.member_form.list");
		
		if (null != memberlist && memberlist.size() > 0) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {
				member = memberlist.get(i);
				if(log.isDebugEnabled())
					log.debug(member.getName());

				DataRow row = new DataRow();
 
				row.addDataCell(member.getName(), 1);
				row.addDataCell(member.getLoginName(), 1);
				row.addDataCell(member.getCode(), 1);

				account = orgManagerDirect.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);		//所属单位

				dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
				String deptCode=null;
				if(dept!=null){
					deptName = dept.getName();
					deptCode=dept.getCode();
				}
				if(StringUtils.hasText(deptCode)){
					try{
						deptName+="("+deptCode+")";
					}catch(Exception e){
						
					}
				}
				row.addDataCell(deptName, 1);	//所属部门
				
				//获取扩展属性
				orgManagerDirect.loadEntityProperty(member);
				if(isInternal){
					if(member.getOrgPostId()==-1){
						row.addDataCell(null, 1);	//主要岗位tanglh
					}else{
						post = orgManagerDirect.getPostById(member.getOrgPostId());
						
						if (null != post ) {
							String ppostName = post.getName();    //
							String ppostCode=post.getCode();
							if(StringUtils.hasText(ppostCode)){
								try{
									ppostName+="("+ppostCode+")";
								}catch(Exception e){
									
								}
							}
								row.addDataCell(ppostName, 1);	//主要岗位tanglh
						}else{
							row.addDataCell(null, 1);	//主要岗位tanglh
						}
					}
					
					if(member.getOrgLevelId()==-1){
						row.addDataCell(null, 1);	//职务级别
					}else{
						level = orgManagerDirect.getLevelById(member.getOrgLevelId());
						if (null != level ) {
							String levName = level.getName();
							String levelCode=level.getCode();
							if(StringUtils.hasText(levelCode)){
								try{
									levName+="("+levelCode+")";
								}catch(Exception e){
									
								}
							}
								row.addDataCell(levName, 1);	//职务级别
						}else{
							row.addDataCell(null, 1);	//职务级别
						}
					}

				}	
				else{
					
					row.addDataCell(member.getProperty("postName"), 1);
					row.addDataCell(member.getProperty("levelName"), 1);
				}

				
				row.addDataCell(member.getTelNumber(), 1);	//移动电话号码

				row.addDataCell(member.getEmailAddress(), 1);//email   
				String gender ="";
				if(member.getGender()!=null){
					if(V3xOrgEntity.MEMBER_GENDER_MALE==member.getGender())
					{
						gender="男";
					}
					else if(V3xOrgEntity.MEMBER_GENDER_FEMALE==member.getGender())
					{
						gender="女";
					}					
				}
				row.addDataCell(gender, 1);	// 性别
				
				String birthday = "";
				if(member.getBirthday()!=null)birthday=Datetimes.format(member.getBirthday(), "yyyy-MM-dd");
				row.addDataCell(birthday, 1);	// 生日
				V3xOrgMember memMember = orgManagerDirect.getMemberById(member.getId());
				String officeNum = "";
				if (memMember!=null) officeNum = memMember.getProperty("officeNum");
				row.addDataCell(officeNum, 1);	// 办公电话
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error("eeror",e);
			}
		}
		String[] columnName = {member_name,member_loginName,member_code,member_account,member_deptName,member_primaryPost
				           ,member_levelName,member_tel,member_email,member_gender,member_birthday,member_officeNumber};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(member_list);
		dataRecord.setSheetName(member_list);
	
		return dataRecord;
	}
	
	/**
	 * @deprecated 导出人员请调用    exportMember(HttpServletRequest request,OrgManagerDirect orgManagerDirect,
			SearchManager searchManager, String condition, String textfield,boolean isInternal)
	 */
	public static DataRecord exportMember(HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect, SearchManager searchManager, String condition, String textfield) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();		
		List<V3xOrgEntity> entList = new ArrayList<V3xOrgEntity>();
		
		if(searchManager!=null){
			// 选择部门导出时，不导出离职人员
			memberlist = searchMember(condition, textfield, searchManager, orgManagerDirect, false, true,!"orgDepartmentId".equals(condition));
		}else{
			entList = orgManagerDirect.getEntityList(V3xOrgMember.class.getSimpleName(), "isInternal", true, user.getLoginAccount(), false);
		}
		
		for(V3xOrgEntity ent:entList){
			memberlist.add((V3xOrgMember)ent);
		}	

		V3xOrgAccount account = null;
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;
		V3xOrgLevel level = null;
		Locale locale = LocaleContext.getLocale(request);
        //导出excel文件的国际化
		String member_name = ResourceBundleUtil.getString(resource, locale, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, locale, "org.member_form.loginName.label");
		String member_code = ResourceBundleUtil.getString(resource, locale, "org.member_form.code");
		String member_deptName = ResourceBundleUtil.getString(resource, locale, "org.member_form.deptName.label");
		String member_primaryPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryPost.label");
		
		//branches_a8_v350sp1_r_gov  GOV-2506 常屹 添加
		//GOV-4896 【单位管理-人员管理】单位管理员登录系统，切换到人员管理那里，导出excel，页面是"职务"，但是导出excel是职务级别。
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
		String levelLabel = "";
		if(isGovVersion){
			levelLabel = "org.member_form.levelName.label.GOV";
		}else{
			levelLabel = "org.member_form.levelName.label";
		}
		
		String member_levelName = ResourceBundleUtil.getString(resource, locale, levelLabel);
		String member_tel = ResourceBundleUtil.getString(resource, locale, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, locale, "org.member_form.account");
		String member_email = ResourceBundleUtil.getString(resource, locale, "org.member.emailaddress");
		String member_gender= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.sexe");
		String member_birthday= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.birthday");
		String member_officeNumber= ResourceBundleUtil.getString(resource, locale, "member.office.number");
		String member_list = ResourceBundleUtil.getString(resource, locale, "org.member_form.list");
		
		if (null != memberlist && memberlist.size() > 0) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {
				member = memberlist.get(i);
				if(log.isDebugEnabled())
					log.debug(member.getName());

				DataRow row = new DataRow();
 
				row.addDataCell(member.getName(), 1);
				row.addDataCell(member.getLoginName(), 1);
				row.addDataCell(member.getCode(), 1);

				account = orgManagerDirect.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);		//所属单位

				dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
				String deptCode=null;
				if(dept!=null){
					deptName = dept.getName();
					deptCode=dept.getCode();
				}
				if(StringUtils.hasText(deptCode)){
					try{
						deptName+="("+deptCode+")";
					}catch(Exception e){
						
					}
				}
					row.addDataCell(deptName, 1);	//所属部门

				if(member.getOrgPostId()==-1){
					row.addDataCell(null, 1);	//主要岗位tanglh
				}else{
					post = orgManagerDirect.getPostById(member.getOrgPostId());
					
					if (null != post ) {
						String ppostName = post.getName();    //
						String ppostCode=post.getCode();
						if(StringUtils.hasText(ppostCode)){
							try{
								ppostName+="("+ppostCode+")";
							}catch(Exception e){
								
							}
						}
							row.addDataCell(ppostName, 1);	//主要岗位tanglh
					}else{
						row.addDataCell(null, 1);	//主要岗位tanglh
					}
				}

				if(member.getOrgLevelId()==-1){
					row.addDataCell(null, 1);	//职务级别
				}else{
					level = orgManagerDirect.getLevelById(member.getOrgLevelId());
					if (null != level ) {
						String levName = level.getName();
						String levelCode=level.getCode();
						if(StringUtils.hasText(levelCode)){
							try{
								levName+="("+levelCode+")";
							}catch(Exception e){
								
							}
						}
							row.addDataCell(levName, 1);	//职务级别
					}else{
						row.addDataCell(null, 1);	//职务级别
					}
				}

				row.addDataCell(member.getTelNumber(), 1);	//移动电话号码

				row.addDataCell(member.getEmailAddress(), 1);//email   
				String gender ="";
				if(member.getGender()!=null){
					if(V3xOrgEntity.MEMBER_GENDER_MALE==member.getGender())
					{
						gender="男";
					}
					else if(V3xOrgEntity.MEMBER_GENDER_FEMALE==member.getGender())
					{
						gender="女";
					}					
				}
				row.addDataCell(gender, 1);	// 性别
				
				String birthday = "";
				if(member.getBirthday()!=null)birthday=Datetimes.format(member.getBirthday(), "yyyy-MM-dd");
				row.addDataCell(birthday, 1);	// 生日
				V3xOrgMember memMember = orgManagerDirect.getMemberById(member.getId());
				String officeNum = "";
				if (memMember!=null) officeNum = memMember.getProperty("officeNum");
				row.addDataCell(officeNum, 1);	// 办公电话
				if(member.getSecretLevel() == Constant.SecretLevel.noSecret.ordinal()){
				    row.addDataCell("内部", 1);
                }else if(member.getSecretLevel() == Constant.SecretLevel.secret.ordinal()){
                    row.addDataCell("秘密", 1);
                } else if(member.getSecretLevel() == Constant.SecretLevel.secretMore.ordinal()){
                    row.addDataCell("机密",1);
                }
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error("eeror",e);
			}
		}
		String[] columnName = {member_name,member_loginName,member_code,member_account,member_deptName,member_primaryPost
				           ,member_levelName,member_tel,member_email,member_gender,member_birthday,member_officeNumber,"密级"};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(member_list);
		dataRecord.setSheetName(member_list);
	
		return dataRecord;
	}
	
	public static DataRecord exportMember(HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect) throws Exception{
		return exportMember(request,metadataManager,response,fileToExcelManager, orgManagerDirect, null, "", "");
	}
	public static DataRecord exportMember1(HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
//		Locale locale = user.getLocale();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		List<V3xOrgMember> memberlist = orgManagerDirect.getAllMembers(user.getLoginAccount(),false);
		V3xOrgAccount account = null;
		Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
		MetadataItem memberTypeItem = null;
		MetadataItem memberStateItem = null;
		String memberType = "";
		String memberState = "";
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;
		V3xOrgPost secondPost = null;
		String postName = "";
		String secondPostName = "";
		V3xOrgLevel level = null;
		String levelName = "";
		V3xOrgRole role = null;
		String roleNames = "";
		List<V3xOrgEntity> roleList = null;
		String primaryLanguange = "";
		Locale locale = LocaleContext.getLocale(request);
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.unuse");
		String member_primaryLanguange_zh_CN = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh_CN");
		String member_primaryLanguange_zh = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh");
		String member_primaryLanguange_en = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.en");
		String member_type_inner = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.inner");
		String member_type_out = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.out");
		String member_name = ResourceBundleUtil.getString(resource, locale, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, locale, "org.member_form.loginName.label");
		String member_password = ResourceBundleUtil.getString(resource, locale, "org.member_form.password.label");
		String member_primaryLanguange = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange");
		String member_kind = ResourceBundleUtil.getString(resource, locale, "org.member_form.kind");
		String member_state = ResourceBundleUtil.getString(resource, locale, "org.state.lable");
		String member_code = ResourceBundleUtil.getString(resource, locale, "org.member_form.code");
		String member_sortId = ResourceBundleUtil.getString(resource, locale, "org.member_form.sort");
		String member_deptName = ResourceBundleUtil.getString(resource, locale, "org.member_form.deptName.label");
		String member_primaryPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryPost.label");
		String member_secondPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.secondPost.label");
		String member_levelName = ResourceBundleUtil.getString(resource, locale, "org.member_form.levelName.label");
		String member_type = ResourceBundleUtil.getString(resource, locale, "org.member_form.type");
		String member_memberState = ResourceBundleUtil.getString(resource, locale, "org.member_form.member.state");
		String member_roles = ResourceBundleUtil.getString(resource, locale, "org.member_form.roles");
		String member_tel = ResourceBundleUtil.getString(resource, locale, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, locale, "org.member_form.account");
		String member_description = ResourceBundleUtil.getString(resource, locale, "org.member_form.description");
		String member_email = ResourceBundleUtil.getString(resource, locale, "org.member.emailaddress");
		String member_list = ResourceBundleUtil.getString(resource, locale, "org.member_form.list");
//		boolean flag = true;
		if (null != memberlist && memberlist.size() > 0) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {
				member = memberlist.get(i);
				log.info(member.getName());
				log.info(member.getIsInternal());
				primaryLanguange = "";
				DataRow row = new DataRow();
//				row.addDataCell("", 1);		//系统信息
				//tanglh
				row.addDataCell(member.getName(), 1);
				row.addDataCell(member.getLoginName(), 1);
				
//				登录密码	默认置为空	可以手动改
//				row.addDataCell("", 1);
				if(member.getPrimaryLanguange() != null){
					if(member.getPrimaryLanguange().equals("zh_CN")){
						primaryLanguange = member_primaryLanguange_zh_CN;
					}else if(member.getPrimaryLanguange().equals("en")){
						primaryLanguange = member_primaryLanguange_en;
					}else if(member.getPrimaryLanguange().equals("zh")){
						primaryLanguange = member_primaryLanguange_zh;
					}
				}else{
					primaryLanguange = "";
				}
				row.addDataCell(primaryLanguange, 1);
				if(member.getIsInternal()){
					row.addDataCell(member_type_inner, 1);
				}else{
					row.addDataCell(member_type_out, 1);
				}
				
				if (member.getEnabled()) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
						
//				row.addDataCell("", 1);		//组织信息
				row.addDataCell(member.getCode(), 1);
				row.addDataCell(member.getSortId().toString(), 1);		//排序号
				dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
				if(dept!=null){
					deptName = dept.getName();
				}
				row.addDataCell(deptName, 1);	//所属部门

				if(member.getOrgPostId()==-1){
					row.addDataCell(null, 1);	//主要岗位tanglh
				}else{
					post = orgManagerDirect.getPostById(member.getOrgPostId());
					if (null != post ) {
						postName = post.getName();    //
						row.addDataCell(postName, 1);	//主要岗位tanglh
					}else{
						row.addDataCell(null, 1);	//主要岗位tanglh
					}
				}
				
//				tanglh  secondPost
				/*
				Map map = member.getSecond_post();
				if(map.size() != 0){
					if(map.get(member.getOrgAccountId()) != null){
						secondPost = orgManagerDirect.getPostById((Long)map.get(member.getOrgAccountId()));
					}
				}
				
				List<WebV3xOrgModel> secondPostList = new ArrayList<WebV3xOrgModel>(); 
				StringBuffer deptpostbuffer = new StringBuffer();
				if (null != map && !map.isEmpty()) {
					Iterator it = map.entrySet().iterator();
					
					while (it.hasNext()) {
						StringBuffer sbuffer = new StringBuffer();
						Map.Entry<Long, Long> entry = (Entry<Long, Long>) it.next();
						Long deptid = entry.getKey();
						V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(deptid);
						sbuffer.append(v3xdept.getName());
						sbuffer.append("-");
						Long postid = entry.getValue();
						V3xOrgPost v3xpost = orgManagerDirect.getPostById(postid);
						sbuffer.append(v3xpost.getName());
						deptpostbuffer.append(sbuffer.toString());
						deptpostbuffer.append(",");
					}
				}
				
				if (null != deptpostbuffer) {
					secondPostName = deptpostbuffer.toString();
					row.addDataCell(secondPostName, 1);	//副岗tanglh
				}else{
					row.addDataCell(null, 1);	//副岗
				}				
				*/
				
				if(member.getOrgLevelId()==-1){
					row.addDataCell(null, 1);	//职务级别
				}else{
					level = orgManagerDirect.getLevelById(member.getOrgLevelId());
					if (null != level ) {
						levelName = level.getName();
						row.addDataCell(levelName, 1);	//职务级别
					}else{
						row.addDataCell(null, 1);	//职务级别
					}
				}
				
				memberTypeItem = orgMeta.get("org_property_member_type").getItem(member.getType().toString());
				if (null != memberTypeItem) {
//					memberType = memberTypeItem.getDescription();
					memberType = ResourceBundleUtil.getString(resource, locale, memberTypeItem.getLabel(), "");
					row.addDataCell(memberType, 1);	//人员类型tanglh
				}else{
					row.addDataCell(null, 1);	//人员类型
				}
				
				memberStateItem = orgMeta.get("org_property_member_state").getItem(member.getState().toString());
				if (null != memberStateItem) {
//					memberState = memberStateItem.getDescription();
					memberState = ResourceBundleUtil.getString(resource, locale, memberStateItem.getLabel(), "");
				}
				row.addDataCell(memberState, 1);	//人员状态
				
//				获取个人角色信息---------xut
/*TODO!!!				roleList = orgManagerDirect.getUserDomain(member.getId(), V3xOrgEntity.ORGENT_TYPE_ROLE);
				if(roleList!=null){
					for(int j = 0;j<roleList.size();j++){
						role = (V3xOrgRole)roleList.get(j);
						roleNames+=role.getName()+",";
					}
				}*/
				
				row.addDataCell(roleNames, 1);		//个人角色
				if(member.getTelNumber()!=null&&!member.getTelNumber().equals("")){
					row.addDataCell(member.getTelNumber(), 1);	//移动电话号码
				}else{
					row.addDataCell("", 1);
				}
				
				account = orgManagerDirect.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);		//所属单位
				row.addDataCell(member.getDescription(), 1);

				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error("error",e);
			}
		}
		String[] columnName = {member_name,member_loginName,member_code,member_account,member_deptName,member_primaryPost
				           ,member_levelName,member_tel,member_email};
		/*{ member_name, member_loginName ,member_primaryLanguange , member_kind , member_state ,member_code
				,member_sortId,member_deptName,member_primaryPost,member_secondPost,member_levelName,member_type
				,member_memberState,member_roles,member_tel,member_account,member_description };*/
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(member_list);
		dataRecord.setSheetName(member_list);
	
		return dataRecord;
	}
	public static DataRecord exportPost(
			HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
		Pagination.setNeedCount(false);
		List<V3xOrgPost> postlist = orgManagerDirect.getAllPosts(user.getLoginAccount(),false);
		Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
		MetadataItem item = null;
		V3xOrgPost post = null;
		V3xOrgAccount account = null;
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String post_name = ResourceBundleUtil.getString(resource, local, "org.post_form.name");
		String post_type = ResourceBundleUtil.getString(resource, local, "org.post_form.type");
		String post_code = ResourceBundleUtil.getString(resource, local, "org.post_form.type.code");
		String post_sortId = ResourceBundleUtil.getString(resource, local, "org.post_form.type.sort");
		String post_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
		String post_account = ResourceBundleUtil.getString(resource, local, "org.account.lable");
		String post_description = ResourceBundleUtil.getString(resource, local, "org.post_form.description");
		String post_list = ResourceBundleUtil.getString(resource, local, "org.post_form.list");
		String post_deptName = ResourceBundleUtil.getString(resource, local, "org.member_form.deptName.label");
		if (null != postlist && postlist.size() > 0) {
			DataRow[] datarow = new DataRow[postlist.size()];
			for (int i = 0; i < postlist.size(); i++) {
				String typeName = "";
				post = postlist.get(i);
				DataRow row = new DataRow();
				//获取岗位类别
				item = orgMeta.get("organization_post_types").getItem(post.getTypeId().toString());
				if(item!=null){
					typeName = ResourceBundleUtil.getString(resource, local, item.getLabel());
				}
				
				row.addDataCell(post.getName(), 1);
				row.addDataCell(post.getCode(), 1);
				row.addDataCell(typeName, 1);
				
				account = orgManagerDirect.getAccountById(post.getOrgAccountId());
				
				row.addDataCell(account.getName(), 1);		
				
				if (post.getEnabled() == true) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
				/*
				row.addDataCell(post.getSortId().toString(), 1);
				row.addDataCell(post.getDesciption(), 1);
				
						
				
				List<V3xOrgDepartment> deps=getDeptmentsByPost(post);
				String depStr=deptsToString(deps);
				row.addDataCell(depStr, 1);	
				*/
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error("error", e);
			}
		}
		String[] columnName = {post_name,  post_code, post_type, post_account,post_state};
		//{   post_name ,post_code , post_type ,post_sortId , post_state ,post_description, post_account,post_deptName  };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(post_list);
		dataRecord.setSheetName(post_list);
		return dataRecord;
	}
	public static DataRecord exportTeam(HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
		List<V3xOrgTeam> teamlist = orgManagerDirect.getAllTeams(user.getLoginAccount(),false);
		V3xOrgTeam team = null;
		V3xOrgAccount account = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		String teamType = "";
		List<Long> memberIds = null;
		List<Long> leaderIds = null;
		List<Long> supervisors = null;
		List<Long> relatives = null;
		V3xOrgMember vm = null;
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, local, "org.account_form.enable.unuse");
		String team_type_personal = ResourceBundleUtil.getString(resource, local, "org.team_form.personalteam");
		String team_type_system = ResourceBundleUtil.getString(resource, local, "org.team_form.systemteam");
		String team_type_project = ResourceBundleUtil.getString(resource, local, "org.team_form.projectteam");
		String team_pro_public = ResourceBundleUtil.getString(resource, local, "org.team_form.openteam");
		String team_pro_private = ResourceBundleUtil.getString(resource, local, "org.team_form.privateteam");
		String team_name = ResourceBundleUtil.getString(resource, local, "org.team_form.name");
		String team_type = ResourceBundleUtil.getString(resource, local, "org.team_form.type");
		String team_dept = ResourceBundleUtil.getString(resource, local, "org.team_form.deptName.label");
		String team_createDate = ResourceBundleUtil.getString(resource, local, "org.account_form.createdtime.label");
		String team_state = ResourceBundleUtil.getString(resource, local, "org.state.lable");
//		权限属性
		String team_purview = ResourceBundleUtil.getString(resource, local, "team.level");
		String team_charge = ResourceBundleUtil.getString(resource, local, "team.charge");
		String team_member = ResourceBundleUtil.getString(resource, local, "team.leaguer");
		String team_leader = ResourceBundleUtil.getString(resource, local, "team.lead");
		String team_relateMember = ResourceBundleUtil.getString(resource, local, "team.correlation.people");
		String team_account = ResourceBundleUtil.getString(resource, local, "team.account");
		String team_description = ResourceBundleUtil.getString(resource, local, "team.description");
		String team_list = ResourceBundleUtil.getString(resource, local, "team.list");
	
		if (null != teamlist && teamlist.size() > 0) {
			DataRow[] datarow = new DataRow[teamlist.size()];
			for (int i = 0; i < teamlist.size(); i++) {
				teamType = "";
				deptName = "";
				team = teamlist.get(i);
				DataRow row = new DataRow();
				row.addDataCell(team.getName(), 1);
				if(team.getType()==V3xOrgEntity.TEAM_TYPE_PERSONAL){
					teamType = team_type_personal;
				}else if(team.getType()==V3xOrgEntity.TEAM_TYPE_SYSTEM){
					teamType = team_type_system;
				}else if(team.getType()==V3xOrgEntity.TEAM_TYPE_PROJECT){
					teamType = team_type_project;
				}
				row.addDataCell(teamType, 1);
				dept = orgManagerDirect.getDepartmentById(team.getDepId());
				if(dept!=null){
					deptName = dept.getName();
				}
				row.addDataCell(deptName, 1);
				row.addDataCell(Datetimes.format(team.getCreateTime(), "yyyy-MM-dd"), 6);	//创建时间
				if (team.getEnabled()) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}

				if (team.getIsPrivate()) {
					row.addDataCell(team_pro_private, 1);
				} else {
					row.addDataCell(team_pro_public, 1);
				}
				// 组主管
				leaderIds = team.getLeaders();
				String teamLeaderNames = "";
				if(leaderIds!=null){
					for (Long id: leaderIds) {
						vm = orgManagerDirect.getMemberById(id);
						teamLeaderNames+=vm.getName()+",";
					}
				}
				row.addDataCell(teamLeaderNames, 1);
				//取得组的成员
				memberIds = team.getMembers();
				String teamMemberNames = "";
				if(memberIds!=null){
					for (Long id : memberIds) {
						vm = orgManagerDirect.getMemberById(id);
						teamMemberNames+=vm.getName()+",";
					}
				}
				row.addDataCell(teamMemberNames, 1);
				//组领导
				supervisors = team.getSupervisors();
				String teamSupervisorsNames = "";
				if(supervisors!=null){
					for (Long id : supervisors) {
						vm = orgManagerDirect.getMemberById(id);
						teamSupervisorsNames+=vm.getName()+",";					
					}
				}
				row.addDataCell(teamSupervisorsNames, 1);
				// 关联人员的解析
				relatives = team.getRelatives();		
				String teamRelativeNames = "";
				if(relatives!=null){
					for (Long id : relatives) {
						vm = orgManagerDirect.getMemberById(id);
						teamRelativeNames+=vm.getName()+",";
					}
				}
				row.addDataCell(teamRelativeNames, 1);
				account = orgManagerDirect.getAccountById(team.getOrgAccountId());
				row.addDataCell(account.getName(), 1);
				row.addDataCell(team.getDescription(), 1);

				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}

		String[] columnName = { team_name , team_type, team_dept , team_createDate , team_state , team_purview ,team_charge,team_member,team_leader,team_relateMember,team_account,team_description };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(team_list);
		dataRecord.setSheetName(team_list);
		return dataRecord;
	}
	public static void exportToExcel(HttpServletRequest request,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,String title,DataRecord dataRecord) throws Exception{
		try {
			log.info("exportToExcel");
			fileToExcelManager.save(request, response,title,dataRecord);//tanglh
		} catch (Exception e) {
			log.error("error",e);
		}		
	}
	
	//tanglh
	public static List<V3xOrgDepartment>  getDeptmentsByPost(V3xOrgPost post){
		if(post==null || post.getId()==null)
			return null;
		return getDeptmentsByPost(post.getId());
	}
	public static List<V3xOrgDepartment>  getDeptmentsByPost(long PostId){
		return null;//todo
	}
	
	public static String deptsToString(List<V3xOrgDepartment> deps){
		StringBuffer sb=new StringBuffer();
		
		try{
			for(V3xOrgDepartment dep:deps){
				if(dep==null)
					continue;
				if(sb.length()>0)
					sb.append(",");
				sb.append(dep.getName());
				sb.append("(");
				if(StringUtils.hasText(dep.getCode())){
					sb.append(dep.getCode());
				}
				sb.append(")");
			}
		}catch(Exception e){
			log.error("error", e);
		}
		
		
		return sb.toString();
	}
	/**
	 * 导出部门下的所有人员信息
	 * @param request
	 * @param metadataManager
	 * @param response
	 * @param fileToExcelManager
	 * @param orgManagerDirect
	 * @param searchManager
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static DataRecord exportDepartmentMember(HttpServletRequest request,MetadataManager metadataManager,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,OrgManagerDirect orgManagerDirect,PrincipalManager principalManager, SearchManager searchManager,String path) throws Exception{
		User user = CurrentUser.get();
		DataRecord dataRecord = new DataRecord();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();		
		 memberlist = searchDepartmentMember(path,searchManager, orgManagerDirect,principalManager);
		
			
		V3xOrgAccount account = null;
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;
		V3xOrgLevel level = null;
		Locale locale = LocaleContext.getLocale(request);
        //导出excel文件的国际化
		String member_name = ResourceBundleUtil.getString(resource, locale, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, locale, "org.member_form.loginName.label");
		String member_code = ResourceBundleUtil.getString(resource, locale, "org.member_form.code");
		String member_deptName = ResourceBundleUtil.getString(resource, locale, "org.member_form.deptName.label");
		String member_primaryPost = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryPost.label");
		String member_levelName = ResourceBundleUtil.getString(resource, locale, "org.member_form.levelName.label");
		String member_tel = ResourceBundleUtil.getString(resource, locale, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, locale, "org.member_form.account");
		String member_email = ResourceBundleUtil.getString(resource, locale, "org.member.emailaddress");
		String member_gender= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.sexe");
		String member_birthday= ResourceBundleUtil.getString(resource, locale, "org.memberext_form.base_fieldset.birthday");
		String member_officeNumber= ResourceBundleUtil.getString(resource, locale, "member.office.number");
		String member_list = ResourceBundleUtil.getString(resource, locale, "org.member_form.list");
		
		if (null != memberlist && memberlist.size() > 0) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {
				member = memberlist.get(i);
				if(log.isDebugEnabled())
					log.debug(member.getName());

				DataRow row = new DataRow();
				row.addDataCell(member.getName(), 1);
				row.addDataCell(member.getLoginName(), 1);
				row.addDataCell(member.getCode(), 1);

				account = orgManagerDirect.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);		//所属单位

				dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
				String deptCode=null;
				if(dept!=null){
					deptName = dept.getName();
					deptCode=dept.getCode();
				}
				if(StringUtils.hasText(deptCode)){
					try{
						deptName+="("+deptCode+")";
					}catch(Exception e){
						
					}
				}
					row.addDataCell(deptName, 1);	//所属部门

				if(member.getOrgPostId()==-1){
					row.addDataCell(null, 1);	//主要岗位tanglh
				}else{
					post = orgManagerDirect.getPostById(member.getOrgPostId());
					
					if (null != post ) {
						String ppostName = post.getName();    //
						String ppostCode=post.getCode();
						if(StringUtils.hasText(ppostCode)){
							try{
								ppostName+="("+ppostCode+")";
							}catch(Exception e){
								
							}
						}
							row.addDataCell(ppostName, 1);	//主要岗位tanglh
					}else{
						row.addDataCell(null, 1);	//主要岗位tanglh
					}
				}

				if(member.getOrgLevelId()==-1){
					row.addDataCell(null, 1);	//职务级别
				}else{
					level = orgManagerDirect.getLevelById(member.getOrgLevelId());
					if (null != level ) {
						String levName = level.getName();
						String levelCode=level.getCode();
						if(StringUtils.hasText(levelCode)){
							try{
								levName+="("+levelCode+")";
							}catch(Exception e){
								
							}
						}
							row.addDataCell(levName, 1);	//职务级别
					}else{
						row.addDataCell(null, 1);	//职务级别
					}
				}

				row.addDataCell(member.getTelNumber(), 1);	//移动电话号码

				row.addDataCell(member.getEmailAddress(), 1);//email   
				String gender ="";
				if(member.getGender()!=null){
					if(V3xOrgEntity.MEMBER_GENDER_MALE==member.getGender())
					{
						gender="男";
					}
					else if(V3xOrgEntity.MEMBER_GENDER_FEMALE==member.getGender())
					{
						gender="女";
					}					
				}
				row.addDataCell(gender, 1);	// 性别
				
				String birthday = "";
				if(member.getBirthday()!=null)birthday=Datetimes.format(member.getBirthday(), "yyyy-MM-dd");
				row.addDataCell(birthday, 1);	// 生日
				V3xOrgMember memMember = orgManagerDirect.getMemberById(member.getId());
				String officeNum = "";
				if (memMember!=null) officeNum = memMember.getProperty("officeNum");
				row.addDataCell(officeNum, 1);	// 办公电话
				datarow[i] = row;
			}

			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error("eeror",e);
			}
		}
		String[] columnName = {member_name,member_loginName,member_code,member_account,member_deptName,member_primaryPost
				           ,member_levelName,member_tel,member_email,member_gender,member_birthday,member_officeNumber};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(member_list);
		dataRecord.setSheetName(member_list);
	
		return dataRecord;
	}
	  public static List<V3xOrgMember> searchMemberInGroup(String condition, String textfield, SearchManager searchManager, OrgManagerDirect orgManager, boolean isPaginate) {
		PrincipalManager  principalManager = (PrincipalManager)ApplicationContextHolder.getBean("principalManager");
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
	    List sublist = new ArrayList();
	    if ((Strings.isNotBlank(condition)) && (Strings.isNotBlank(textfield))) {
	      StringBuffer strbuf = new StringBuffer();
	      Map param = new HashMap();
	      strbuf.append("select max(a), max(a.id) from " + V3xOrgMember.class.getName() + " a where a.isAssigned='1' and a.isDeleted='0' and a.isAdmin = '0'");

	      if (condition.equals("name")) {
	        strbuf.append(" and a.name like :textfield ");
	        param.put("textfield", "%" + textfield + "%");
	      }
	      else if (condition.equals("orgPostId")) {
	        strbuf.append(" and a.orgPostId=:textfield");
	        param.put("textfield", Long.valueOf(textfield));
	      }
	      else if (condition.equals("orgLevelId")) {
	        strbuf.append(" and a.orgLevelId=:textfield");
	        param.put("textfield", Long.valueOf(textfield));
	      }
	      else if (condition.equals("orgDepartmentId")) {
	        strbuf.append(" and a.orgDepartmentId=:textfield");
	        param.put("textfield", Long.valueOf(textfield));
	      }
	      else if (condition.equals("secondPostId")) {
	        strbuf.append(" and a.orgLevelId=:textfield");
	        param.put("textfield", Long.valueOf(textfield));
	      }
	      else if (condition.equals("code")) {
	        strbuf.append(" and a.code like :textfield");
	        param.put("textfield", "%" + textfield + "%");
	      }
/*	      else if (condition.equals("loginName")) {
	        strbuf.append(" and b.fullPath like :textfield");
	        param.put("textfield", "/user/%" + textfield + "%");
	      }*/

	      strbuf.append(" group by a.id");

	      List<Object[]> list= searchManager.searchByHql(strbuf.toString(), param, isPaginate);

	      boolean filterByLoginName = "loginName".equals(condition);
	      final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
	      for (Object[] objects : list) {
	        V3xOrgMember m = (V3xOrgMember)objects[0];
/*			try {*/
	        
			final String loginName = idLoginNameMap.get(m.getId());
			if(loginName!=null){
				m.setLoginName(loginName);
			}else{
				log.error("人员的登录名不存在。"+m.getId());
				continue;
			}
/*			} catch (NoSuchPrincipalException e) {
				log.error("人员的登录名不存在。"+m.getId());
				continue;
			}*/
			if(filterByLoginName){
				if(m.getLoginName().indexOf(textfield)==-1)continue;
			}
	        memberlist.add(m);
	      }
	      sublist = memberlist;
	      Collections.sort(sublist, CompareSortEntity.getInstance());
	    }
	    else {
	      try {
	        sublist = orgManager.getAllMembersInGroup();
	        return sublist;
	      }
	      catch (BusinessException e) {
	        log.error("", e);
	      }
	    }
	    return sublist;
	  }
	  
		public static List<V3xOrgMember> getMemberByAccountId(long accountId, SearchManager searchManager, OrgManagerDirect orgManager, boolean isPaginate, boolean isInternal, boolean includeDisabled){
			PrincipalManager  principalManager = (PrincipalManager)ApplicationContextHolder.getBean("principalManager");		
			List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
			List<V3xOrgMember> sublist = new ArrayList<V3xOrgMember>();
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select max(a), max(a.id),max(a.sortId) from " + V3xOrgMember.class.getName() + " a where a.isAssigned='1' and a.isDeleted='0' and a.isAdmin = '0' and a.orgAccountId=:orgAccountId ");
			if(!isInternal)
				strbuf.append(" and a.isInternal = '0' ");
			else
				strbuf.append(" and a.isInternal = '1' ");
			
			if(!includeDisabled){
				strbuf.append("and a.enabled=:enabled");
				param.put("enabled", Boolean.TRUE);
			}
						
			param.put("orgAccountId",accountId);
			strbuf.append(" and a.enabled = '1' ");	
			strbuf.append(" group by a.id, a.sortId order by a.sortId asc");
			List<Object[]> list = searchManager.searchByHql(strbuf.toString(), param, isPaginate);
			final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
		      for (Object[] objects : list) {
		        V3xOrgMember m = (V3xOrgMember)objects[0];
				final String loginName = idLoginNameMap.get(m.getId());
				if(loginName!=null){
					m.setLoginName(loginName);
				}else{
					log.error("人员的登录名不存在。"+m.getId());
					continue;
				}
		        memberlist.add(m);
		      }
			sublist = memberlist;
			Collections.sort(sublist,CompareSortEntity.getInstance());
			
			return sublist;		
		}
}