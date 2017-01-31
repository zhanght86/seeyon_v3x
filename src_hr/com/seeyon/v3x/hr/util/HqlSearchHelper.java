/**
 * $Id: HqlSearchHelper.java,v 1.33 2011/04/19 08:11:16 leigf Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * StaffInfoHelper.java created by paul at 2007-8-10 下午05:47:10
 *
 */
package com.seeyon.v3x.hr.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * <tt>HqlSearchHelper</tt>支持前端简单的根据条件拼装Hql查询的Helper
 *
 * @author paul
 *
 */
@SuppressWarnings("deprecation")
public class HqlSearchHelper {
	private static Log LOG = LogFactory.getLog(HqlSearchHelper.class);
	
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
	//此方法合并到组织模型里的查询方法。
	/*	@SuppressWarnings("unchecked")
	public static List<V3xOrgMember> searchMember(String condition, String textfield, SearchManager searchManager, OrgManagerDirect orgManager,PrincipalManager principalManager,boolean includeDisabled){
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>(0);
		User user = CurrentUser.get();
		
		if (Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select max(a), max(a.id) from " + V3xOrgMember.class.getName() + " a where a.isAssigned='1' and a.isDeleted='0' and a.isAdmin = '0' and a.isInternal = '1' and a.orgAccountId='"+user.getAccountId()+"' ");
			if(!includeDisabled){
				strbuf.append("and a.enabled=:enabled");
				param.put("enabled", Boolean.TRUE);
			}
			
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
			else if (condition.equals("isInternal")) {
				strbuf.append(" and a.isInternal=:textfield");
				if(Integer.parseInt(textfield) == 1)
					param.put("textfield", Boolean.TRUE);
				else
					param.put("textfield", Boolean.FALSE);
			}
			else if (condition.equals("typeId")) {
				strbuf.append(" and a.type=:textfield");
				param.put("textfield", Byte.valueOf(textfield));
			}
			else if (condition.equals("stateId")) {
				strbuf.append(" and a.state=:textfield");
				param.put("textfield",Byte.valueOf(textfield));
			}
			else if (condition.equals("code")) {
				strbuf.append(" and a.code like :textfield");
				param.put("textfield", "%" + textfield + "%");
			}
			else if (condition.equals("loginName")) { //模糊查询 modified by paul at 2007-9-22
				strbuf.append(" and b.fullPath like :textfield");
				param.put("textfield", "/user/" +  "%" + textfield + "%");
			}
			
			strbuf.append(" group by a.id, a.sortId order by a.sortId asc ");
		    boolean filterByLoginName = "loginName".equals(condition);
			List<Object[]> list = searchManager.searchByHql(strbuf.toString(), param, true);
			for (Object[] objects : list) {
				V3xOrgMember m = (V3xOrgMember)objects[0];
//				m.setLoginName(UserPrincipalUtil.getPrincipalNameFromFullPath(((String)objects[2])));
				try {
					m.setLoginName(principalManager.getLoginNameByMemberId(m.getId()));
				} catch (NoSuchPrincipalException e) {
					LOG.error("人员的登录名不存在。"+m.getId());
					continue;
				}
				if(filterByLoginName){
					if(m.getLoginName().indexOf(textfield)==-1)continue;
				}				
				memberlist.add(m);
			}
			Collections.sort(memberlist,CompareSortEntity.getInstance());			
		}else {
			try {
				List<V3xOrgEntity> entList = orgManager.getEntityList(V3xOrgMember.class.getSimpleName(), "isInternal", true, user.getLoginAccount(), true);
				for(V3xOrgEntity ent:entList){
					memberlist.add((V3xOrgMember)ent);
				}
				return memberlist;
			}
			catch (BusinessException e) {
				LOG.error("", e);
			}
		}
		
		return memberlist;
	}*/
	
	/**
	 * 高级人员搜索（部门，岗位，级别，性别）
	 * 
	 * @param condition 查询条件
	 * @param textfield 查询值
	 * @param searchManager 
	 * @param orgManager
	 * @param includeDisabled 是否包含停用帐号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<V3xOrgMember> highSearchMember(String se, String de, String le, String po, SearchManager searchManager, OrgManagerDirect orgManager,PrincipalManager principalManager, boolean includeDisabled){
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>(0);
		User user = CurrentUser.get();
		
		if (Strings.isNotBlank(se) || Strings.isNotBlank(de) || Strings.isNotBlank(le) || Strings.isNotBlank(po)) {
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select new com.seeyon.v3x.hr.util.V3xOrgMemberWithLoginName(a,'/user/tmp') from " + V3xOrgMember.class.getName() + " a where a.orgAccountId ='" + user.getAccountId() + "' and a.isDeleted!='1' and a.isAdmin='0' and a.isAssigned=1 and a.isInternal = '1' ");
			if(!includeDisabled){
				strbuf.append("and a.enabled=:enabled");
				param.put("enabled", Boolean.TRUE);
			}
	
			if (de!=null&&!de.equals("")) {
				strbuf.append(" and a.orgDepartmentId=:textfield");
				param.put("textfield", Long.valueOf(de));
			}
			if (le!=null&&!le.equals("")) {
				strbuf.append(" and a.orgLevelId=:textfield1");
				param.put("textfield1", Long.valueOf(le));
			}
			if (po!=null&&!po.equals("")) {
				strbuf.append(" and a.orgPostId=:textfield2");
				param.put("textfield2", Long.valueOf(po));
			}
			if(se!=null && !se.equals("")&& Integer.valueOf(se)!=-1){
				strbuf.append(" and a.gender=:textfield3");
				param.put("textfield3", Integer.valueOf(se));
			}			
			memberlist = searchManager.searchByHql(strbuf.toString(), param);
			final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
			for (V3xOrgMember m : memberlist) {
/*				try {*/
					final String loginName = idLoginNameMap.get(m.getId());
					if(loginName!=null){
						m.setLoginName(loginName);
					}else{
						LOG.error("人员的登录名不存在。"+m.getId());
						continue;						
					}
/*				} catch (NoSuchPrincipalException e) {
					LOG.error("人员的登录名不存在。"+m.getId());
					continue;
				}*/
			}			
		}
		else {
			try {
				memberlist = orgManager.getAllMembers(user.getLoginAccount());				
				return memberlist;
			}
			catch (BusinessException e) {
				LOG.error("", e);
			}
		}		
		return memberlist;
	}
	
	/**
	 * 高级人员综合搜索
	 * 
	 * @param condition 查询条件
	 * @param textfield 查询值
	 * @param searchManager 
	 * @param orgManager
	 * @param includeDisabled 是否包含停用帐号
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public static List<V3xOrgMember> highSearchMember(String de, String le, String po,
			String se, String st, String pol,String ma, String fT1, String tT1,String fT2, String tT2, 
			SearchManager searchManager, OrgManagerDirect orgManager,PrincipalManager principalManager,boolean includeDisabled){
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>(0);
		User user = CurrentUser.get();
		
		if (Strings.isNotBlank(de) || Strings.isNotBlank(le) || Strings.isNotBlank(po)||
				Strings.isNotBlank(se) || Strings.isNotBlank(st) || Strings.isNotBlank(pol)||
				Strings.isNotBlank(ma) || Strings.isNotBlank(fT1) || Strings.isNotBlank(tT1)||
				Strings.isNotBlank(fT2) || Strings.isNotBlank(tT2)) {
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select new com.seeyon.v3x.hr.util.V3xOrgMemberWithLoginName(a, '/user/tmp') from " + V3xOrgMember.class.getName() + " a,"
					+StaffInfo.class.getName()+" c  where a.id=c.org_member_id and  a.orgAccountId ='" + user.getAccountId() + "' and a.isDeleted!='1' and a.isAdmin='0' and a.isAssigned=1 and a.isInternal = '1' ");
			if(!includeDisabled){
				strbuf.append("and a.enabled=:enabled");
				param.put("enabled", Boolean.TRUE);
			}
	
			if (de!=null && !de.equals("")) {
				strbuf.append(" and a.orgDepartmentId=:textfield");
				param.put("textfield", Long.valueOf(de));
			}
			if (le!=null && !le.equals("")) {
				strbuf.append(" and a.orgLevelId=:textfield1");
				param.put("textfield1", Long.valueOf(le));
			}
			if (po!=null && !po.equals("")) {
				strbuf.append(" and a.orgPostId=:textfield2");
				param.put("textfield2", Long.valueOf(po));
			}
			//~~~~~~~~~
			if(se!=null && !se.equals("")&& Integer.valueOf(se)!=-1){
				strbuf.append(" and a.gender=:textfield3");
				param.put("textfield3", Integer.valueOf(se));
			}
			if(st!=null && !st.equals("")&& Integer.valueOf(st)!=-1){
				strbuf.append(" and c.edu_level=:textfield4");
				param.put("textfield4", Integer.valueOf(st));
			}
			if(pol!=null && !pol.equals("")&& Integer.valueOf(pol)!=-1){
				strbuf.append(" and c.political_position=:textfield5");
				param.put("textfield5", Integer.valueOf(pol));
			}
			if(ma!=null && !ma.equals("")&& Integer.valueOf(ma)!=-1){
				strbuf.append(" and c.marriage=:textfield6");
				param.put("textfield6", Integer.valueOf(ma));
			}
			//~~~~~~~~~//出生日期	
			Date fromTime = new Date();
			if(fT1 != null && !fT1.equals("")){
				fromTime = Datetimes.parse(fT1, "yyyy-MM-dd");
			}
			Date toTime = new Date();
			if(tT1 != null && !tT1.equals("")){
				toTime = Datetimes.parse(tT1, "yyyy-MM-dd");
			}
			if(fT1 != null && !fT1.equals("")&& tT1 != null && !tT1.equals("")){
				strbuf.append(" and ( a.birthday >= :textfield7 and a.birthday < :textfield8 )");
				param.put("textfield7", fromTime);
				param.put("textfield8", toTime);
			}else if(fT1 != null && !fT1.equals("")){
				strbuf.append(" and ( a.birthday = :textfield7 )");
				param.put("textfield7", fromTime);
			}else if(tT1 != null && !tT1.equals("")){
				strbuf.append(" and ( a.birthday = :textfield8 )");
				param.put("textfield8", toTime);
			}
			//~~~~~~~~~//入职时间
			Date fromTime2 = new Date();
			if(fT2 != null && !fT2.equals("")){
				fromTime2 = Datetimes.parse(fT2, "yyyy-MM-dd");
			}
			Date toTime2 = new Date();
			if(tT2 != null && !tT2.equals("")){
				toTime2 = Datetimes.parse(tT2, "yyyy-MM-dd");
			}
			
			if(fT2 != null && !fT2.equals("")&& tT2 != null && !tT2.equals("")){
				strbuf.append(" and ( c.work_starting_date >= :textfield9 and c.work_starting_date < :textfield0 )");
				param.put("textfield9", fromTime2);
				param.put("textfield0", toTime2);
			}else if(fT2 != null && !fT2.equals("")){
				strbuf.append(" and ( c.work_starting_date = :textfield9 )");
				param.put("textfield9", fromTime2);
			}else if(tT2 != null && !tT2.equals("")){
				strbuf.append(" and ( c.work_starting_date = :textfield0 )");
				param.put("textfield0", toTime2);
			}
			//~~~~~~~~~
						
			memberlist = searchManager.searchByHql(strbuf.toString(), param);
			final Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
			for (V3xOrgMember m : memberlist) {
/*				try {*/
					final String loginName = idLoginNameMap.get(m.getId());
					if(loginName!=null){
						m.setLoginName(loginName);
					}else{
						LOG.error("人员的登录名不存在。"+m.getId());
						continue;						
					}
/*				} catch (NoSuchPrincipalException e) {
					LOG.error("人员的登录名不存在。"+m.getId());
					continue;
				}*/
			}
		}
		else {
			try {
				memberlist = orgManager.getAllMembers(user.getLoginAccount());				
				return memberlist;
			}
			catch (BusinessException e) {
				LOG.error("", e);
			}
		}		
		return memberlist;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<OperationLog> searchLog(String condition, String textfield,String textfield1, Long subObjectId, SearchManager searchManager){
		List<OperationLog> operationLogs = new ArrayList<OperationLog>();
		boolean isTextfield = Strings.isNotBlank(textfield) || Strings.isNotBlank(textfield1);
		if(Strings.isNotBlank(condition) && isTextfield){
			Long accountId = CurrentUser.get().getLoginAccount();
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select a from " + OperationLog.class.getName() + " a where ");
			strbuf.append("a.objectId=:accountId and ");
			param.put("accountId", accountId);
			if(condition.equals("memberId")){
				strbuf.append("a.memberId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}else if(condition.equals("moduleId")){
				strbuf.append("a.moduleId=:textfield");
				param.put("textfield", Integer.parseInt(textfield));
			}else if(condition.equals("objectId")){
				strbuf.append("a.objectId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}else if(condition.equals("subObjectId")){
				strbuf.append("a.subObjectId=:textfield");
				param.put("textfield", Long.valueOf(textfield));
			}else if(condition.equals("actionType")){
				if(textfield.equals("hr.staffInfo.operation.add.label")){
					strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
					param.put("textfield1", "hr.staffInfo.operation.add.label");
					param.put("textfield2", "hr.staffInfo.other.add.label");
				}else if(textfield.equals("hr.staffInfo.operation.delete.label")){
					strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
					param.put("textfield1", "hr.staffInfo.operation.delete.label");
					param.put("textfield2", "hr.staffInfo.other.delete.label");
				}else if(textfield.equals("hr.staffInfo.operation.update.label")){
					strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
					param.put("textfield1", "hr.staffInfo.operation.update.label");
					param.put("textfield2", "hr.staffInfo.other.update.label");
				}
				
			}else if(condition.equals("actionAll")){
				strbuf.append("a.actionType=:textfield1 or a.actionType=:textfield2 or a.actionType=:textfield3");
				param.put("textfield1", "hr.staffInfo.operation.add.label");
				param.put("textfield2", "hr.staffInfo.operation.delete.label");
				param.put("textfield3", "hr.staffInfo.operation.update.label");

			}else if(condition.equals("actionName")){
				strbuf.append("a.memberId=:textfield and (a.actionType=:textfield1 or a.actionType=:textfield2 or a.actionType=:textfield3 or a.actionType=:textfield4 or a.actionType=:textfield5 or a.actionType=:textfield6)");
				param.put("textfield", Long.valueOf(textfield));
				param.put("textfield1", "hr.staffInfo.operation.add.label");
				param.put("textfield2", "hr.staffInfo.operation.delete.label");
				param.put("textfield3", "hr.staffInfo.operation.update.label");
				
				param.put("textfield4", "hr.staffInfo.other.add.label");
				param.put("textfield5", "hr.staffInfo.other.delete.label");
				param.put("textfield6", "hr.staffInfo.other.update.label");
			}else if(condition.equals("actionTime")){
				//String[] date = textfield.split(",");
					Date fromTime = null;
					Date toTime = null;
					if(Strings.isNotBlank(textfield)){
						 fromTime = Datetimes.parse(textfield, "yyyy-MM-dd");
					}
					if(Strings.isNotBlank(textfield1)){
						 toTime = Datetimes.parse(textfield1, "yyyy-MM-dd");
					}
					
					Calendar toCal = Calendar.getInstance();
					if(toTime!=null && !toTime.equals("")){
						//查询时，需要把最后日期挪后一天
						toCal.setTime(toTime);
						toCal.add(Calendar.DAY_OF_MONTH, 1);
					}
					
					if(fromTime!=null && toTime!=null ){
						strbuf.append("(a.actionTime >= :fromTime and a.actionTime <= :toTime) and subObjectId=:subObjectId");
						param.put("fromTime", fromTime);
						param.put("toTime", toCal.getTime());
						param.put("subObjectId", subObjectId);
					}else if((fromTime!=null )&&(toTime==null)){
						Date from = Datetimes.parse(textfield+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
						Date end = Datetimes.addDate(from, 1);
						strbuf.append("(a.actionTime >= :fromTime and a.actionTime <= :toTime) and subObjectId=:subObjectId");
						param.put("fromTime", from);
						param.put("toTime", end);
						param.put("subObjectId", subObjectId);
					}else if((toTime!=null )&&(fromTime==null )){
						Date from = Datetimes.parse(textfield1+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
						Date end = Datetimes.addDate(from, 1);
						strbuf.append("(a.actionTime >= :fromTime and a.actionTime <= :toTime) and subObjectId=:subObjectId");
						param.put("fromTime", from);
						param.put("toTime", end);
						param.put("subObjectId", subObjectId);
					}
				
			}else if(condition.equals("remoteIp")){
				strbuf.append("a.remoteIp=:textfield");
				param.put("textfield", textfield);
			}
			if(condition!=null){
				strbuf.append(" order by a.actionTime desc");
			}
			operationLogs = searchManager.searchByHql(strbuf.toString(), param);
		}
		return operationLogs;
	}
	
	@SuppressWarnings("unchecked")
	public static Templete searchTemplete(String textfield, SearchManager searchManager){
		Templete templete = new Templete();
		if(Strings.isNotBlank(textfield)){
			List<Templete> templetes = new ArrayList<Templete>();
			DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Templete.class);
			detachedCriteria.add(Expression.eq(Templete.PROP_subject, textfield));
			detachedCriteria.add(Expression.eq(Templete.PROP_categoryId, new Long(TempleteCategory.TYPE.form.ordinal())));
			detachedCriteria.add(Expression.eq(Templete.PROP_state, Templete.State.normal.ordinal()));
			detachedCriteria.add(Expression.eq(Templete.PROP_isSystem, Boolean.TRUE));
			templetes = searchManager.searchByCriteria(detachedCriteria);
			if(null != templetes && !templetes.isEmpty()){
				templete = templetes.get(0);
			}
		}
		
		return templete;
	}
	
}
