package com.seeyon.v3x.collaboration.templete.manager;

import static com.seeyon.v3x.collaboration.templete.domain.Templete.ENTITY_NAME;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_bodyType;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_categoryId;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_categoryType;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_createDate;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_description;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_isSystem;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_memberId;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_orgAccountId;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_projectId;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_sort;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_state;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_subject;
import static com.seeyon.v3x.collaboration.templete.domain.Templete.PROP_type;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.Templete.State;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.webmodel.SimpleTemplete;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;


/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-25
 */
public class TempleteManagerImpl extends BaseHibernateDao<Templete> implements
		TempleteManager {
	private static Log log = LogFactory.getLog(TempleteManagerImpl.class);

	private OrgManager orgManager;

	private TempleteAuthManager templeteAuthManager;
	
	private AttachmentManager attachmentManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setTempleteAuthManager(TempleteAuthManager templeteAuthManager) {
		this.templeteAuthManager = templeteAuthManager;
	}
	
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void save(Templete templete) {
		super.save(templete);
	}

	@SuppressWarnings("unchecked")
	public List<Templete> getAllSystemTempletes(Long categoryId,Integer categoryType, 
			String condition, String textfield, String textfield1) {
		long orgAccountId = CurrentUser.get().getLoginAccount();

		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		
		if(categoryId != null){
			criteria.add(Expression.eq(Templete.PROP_categoryId, categoryId));
		}
		else{
			criteria.add(Expression.isNull(Templete.PROP_categoryId));
		}
		
		criteria.add(Expression.eq(Templete.PROP_orgAccountId, orgAccountId));
		criteria.add(Expression.eq(Templete.PROP_isSystem, Boolean.TRUE));
		criteria.add(Expression.eq(Templete.PROP_categoryType, categoryType));
		
		if("subject".equals(condition)){
			criteria.add(Expression.like(Templete.PROP_subject, textfield, MatchMode.ANYWHERE));
		}
		else if("createDate".equals(condition)){
			java.util.Date stamp = null;
			java.util.Date stamp1 = null;
			
            if (StringUtils.isNotBlank(textfield)) {
                stamp = Datetimes.getTodayFirstTime(textfield);
            }
            if (StringUtils.isNotBlank(textfield1)) {
                stamp1 = Datetimes.getTodayLastTime(textfield1);
            }
            
            if(stamp != null && stamp1 == null){
            	criteria.add(Expression.ge(Templete.PROP_createDate, stamp));
            }
            else if(stamp == null && stamp1 != null){
            	criteria.add(Expression.le(Templete.PROP_createDate, stamp1));
            }
            else if(stamp != null && stamp1 != null){
            	criteria.add(Expression.between(Templete.PROP_createDate, stamp, stamp1));
            }
		}
		
		criteria.addOrder(Order.asc(Templete.PROP_sort));
		criteria.addOrder(Order.desc(Templete.PROP_createDate));

		List<Templete> templetes = super.executeCriteria(criteria);

		for (Templete templete : templetes) {
			Set<TempleteAuth> auths = templete.getTempleteAuths();
			//TODO 这种写法主要是为了取出数据，不要随意删除哦，除非找到正道
			log.debug(auths.size());
//			for (TempleteAuth auth : auths) {
//				try {
//					V3xOrgEntity orgEntity = orgManager.getEntity(auth
//							.getAuthType(), auth.getAuthId());
//					auth.setOrgEntity(orgEntity);
//				}
//				catch (BusinessException e) {
//				}
//			}
		}
		
		return templetes;
	}
	
	public List<Templete> getAllSystemTempletes(Long userId, Long accountId, Integer categoryType){
		
//		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
//		DetachedCriteria authCriteria =criteria.createCriteria("templeteAuths");
//		authCriteria.add(
//				Expression.or(Expression.(Expression.or(Expression.eq(TempleteAuth.PROP_authId, accountId),
//							  				Expression.eq(TempleteAuth.PROP_authId, VIRTUAL_ACCOUNT_ID))
//							  				,Expression.eq(TempleteAuth.PROP_authType, "Account")),
//							  Expression.eq(Templete.PROP_orgAccountId,accountId)
//							  )
//						);
//		criteria.add(Expression.eq(Templete.PROP_isSystem, Boolean.TRUE));
//		criteria.add(Expression.eq(Templete.PROP_categoryType, categoryType));
//		criteria.addOrder(Order.asc(Templete.PROP_sort));
//		criteria.addOrder(Order.asc(Templete.PROP_createDate));
		List<Long> domainIds = null;
		try{
			domainIds = this.orgManager.getUserDomainIDs(userId, VIRTUAL_ACCOUNT_ID,  ORGENT_TYPE_ACCOUNT);
		}catch(Exception e){
			log.info("查找模板信息异常",e);
		}
		
		List<Object[]> resultList = new ArrayList<Object[]>();
		if(Strings.isNotEmpty(domainIds)){
			List<Long>[]  arr = this.splitListCommon(domainIds, 1000);
			for(List<Long> domainId : arr){
				resultList.addAll(getAllSystemTempletesByAclAndSpecialAuthIDCommon(
						accountId, categoryType, domainId));
			}
		}
		return parseObjArray2Templetes(resultList);
	}

	private List<Object[]> getAllSystemTempletesByAclAndSpecialAuthIDCommon(
			Long accountId, Integer categoryType, List<Long> domainIds) {
		StringBuilder sb = new StringBuilder();
		sb.append(getSelectFieldsHqlStr().toString())
		  .append(" from Templete t,TempleteAuth auths ")
		  .append(" where t.id = auths.objectId and ")
		  .append(" (t.orgAccountId = :accountId or ")
		  .append(" (auths.authType = :authType and auths.authId in (:authId)))")
		  .append(" and t.isSystem = :isSystem ")
		  .append(" and t.categoryType = :categoryType ")
		  .append(" order by t.sort asc, t.createDate asc ");
		
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        namedParameterMap.put("accountId",accountId );
        namedParameterMap.put("authType", "Account");
        namedParameterMap.put("authId", domainIds);
        namedParameterMap.put("isSystem",Boolean.TRUE);
        namedParameterMap.put("categoryType",categoryType );
        
        List<Object[]> result = (List<Object[]>)super.find(sb.toString(), -1, -1, namedParameterMap);
		return result;
	}
	
	public int countAllSystemTempletes(Long categoryId, long orgAccountId){
		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		
		if(categoryId != null){
			criteria.add(Expression.eq(Templete.PROP_categoryId, categoryId));
		}
		else{
			criteria.add(Expression.isNull(Templete.PROP_categoryId));
		}
		
		criteria.add(Expression.eq(Templete.PROP_orgAccountId, orgAccountId));
		criteria.add(Expression.eq(Templete.PROP_isSystem, Boolean.TRUE));
		
		return super.getCountByCriteria(criteria);
	}
	
    public List<Templete> getSystemTempletesByMemberId(Long memberId, Long accountId, Integer... categoryType){
        //我能访问的所有Id
        List<Long> domainIds = null;
        if(accountId == null){
            accountId = VIRTUAL_ACCOUNT_ID;
        }
        try {
        	V3xOrgMember member = orgManager.getMemberById(memberId);
        	//对于外部人员，只寻找他在部门和组范围内的权限
        	if(member.getIsInternal()){
        		domainIds = this.orgManager.getUserDomainIDs(memberId, accountId, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
        	}else{
        		domainIds = this.orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
        	}
        }
        catch (BusinessException e) {
            log.error("", e);
        }

        List<Templete> result = systemTempleteQueryHelper(domainIds, categoryType);
        
        return result;
    }
    //重写getSystemTempletesByMemberId
    public List<Templete> getSystemTempletesByMemberId(Long memberId, Long accountId,String secretLevel,Integer... categoryType){
        //我能访问的所有Id
        List<Long> domainIds = null;
        if(accountId == null){
            accountId = VIRTUAL_ACCOUNT_ID;
        }
        try {
        	V3xOrgMember member = orgManager.getMemberById(memberId);
        	//对于外部人员，只寻找他在部门和组范围内的权限
        	if(member.getIsInternal()){
        		domainIds = this.orgManager.getUserDomainIDs(memberId, accountId, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
        	}else{
        		domainIds = this.orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
        	}
        }
        catch (BusinessException e) {
            log.error("", e);
        }

        List<Templete> result = systemTempleteQueryHelper(domainIds,secretLevel,categoryType);
        
        return result;
    }
    
    private List<Templete> systemTempleteQueryHelper(List<Long> domainIds, Integer... categoryType){
        List<Templete> result = new ArrayList<Templete>();
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        
        StringBuffer s = new StringBuffer();
        s.append("select DISTINCT")
            .append(" t.id")
            .append(",t.").append(PROP_categoryId)
            .append(",t.").append(PROP_createDate)
            .append(",t.").append(PROP_description)
            .append(",t.").append(PROP_orgAccountId)
            .append(",t.").append(PROP_sort)
            .append(",t.").append(PROP_state)
            .append(",t.").append(PROP_subject)
            .append(",t.").append(PROP_type)
            .append(",t.").append(PROP_bodyType)
            .append(",t.").append(PROP_categoryType)
            .append(",t.").append(PROP_memberId)
            //.append(",t.").append("summary")//成发集团项目 程炯 将协同或者公文对象返回给模版
            .append(" from ").append(ENTITY_NAME).append(" as t, ").append(TempleteAuth.ENTITY_NAME ).append( " as a ")
            .append(" where")
            .append(" (t.id=a." ).append( TempleteAuth.PROP_objectId ).append( ")");
	        
        	if(categoryType!=null && categoryType.length!=0){
	    		s.append(" and (t." ).append( PROP_categoryType ).append( " in (:categoryType))");
	    		namedParameterMap.put("categoryType", categoryType);
	        }
            s.append(" and (t." ).append( PROP_isSystem ).append( "=" + Boolean.TRUE + ")")
            .append(" and (t." ).append( PROP_state ).append( "=" + Templete.State.normal.ordinal() + ")")
            .append(" and (a." ).append( TempleteAuth.PROP_authId ).append( " in (:domainIds))")
            .append(" order by t." + PROP_categoryType + ",t." ).append( PROP_sort ).append( ",t." ).append( PROP_createDate);
        
       
        namedParameterMap.put("domainIds", domainIds);

        Pagination.setNeedCount(false);
        
        List<Object[]> list = super.find(s.toString(), -1, -1, namedParameterMap);
        if (list != null) {
            for (Object[] o : list) {
                Templete templete = new Templete();
                int n = 0;
                templete.setId((Long) o[n++]);
                templete.setCategoryId((Long) o[n++]);
                templete.setCreateDate((Timestamp) o[n++]);
                templete.setDescription((String) o[n++]);
                templete.setOrgAccountId((Long) o[n++]);
                templete.setSort((Integer) o[n++]);
                templete.setState((Integer) o[n++]);
                templete.setSubject((String) o[n++]);
                templete.setType((String) o[n++]);
                templete.setBodyType((String) o[n++]);
                templete.setCategoryType((Integer) o[n++]);
                templete.setMemberId((Long) o[n++]);
               // templete.setSummary((String)o[n++]);//成发集团项目 程炯 将协同或者公文对象返回给模版
                result.add(templete);
            }
        }
        return result;
    }
    //成发集团项目 重写systemTempleteQueryHelper
    private List<Templete> systemTempleteQueryHelper(List<Long> domainIds,String secretLevel,Integer... categoryType){
        List<Templete> result = new ArrayList<Templete>();
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        
        StringBuffer s = new StringBuffer();
        s.append("select DISTINCT")
            .append(" t.id")
            .append(",t.").append(PROP_categoryId)
            .append(",t.").append(PROP_createDate)
            .append(",t.").append(PROP_description)
            .append(",t.").append(PROP_orgAccountId)
            .append(",t.").append(PROP_sort)
            .append(",t.").append(PROP_state)
            .append(",t.").append(PROP_subject)
            .append(",t.").append(PROP_type)
            .append(",t.").append(PROP_bodyType)
            .append(",t.").append(PROP_categoryType)
            .append(",t.").append(PROP_memberId)
            .append(" from ").append(ENTITY_NAME).append(" as t, ").append(TempleteAuth.ENTITY_NAME ).append( " as a ")
            .append(" where")
            .append(" (t.id=a." ).append( TempleteAuth.PROP_objectId ).append( ")");
	        
        	if(categoryType!=null && categoryType.length!=0){
	    		s.append(" and (t." ).append( PROP_categoryType ).append( " in (:categoryType))");
	    		namedParameterMap.put("categoryType", categoryType);
	        }
        	if(secretLevel != null && !secretLevel.equals("")){
        		s.append(" and (t.secretLevel <="+secretLevel+" or t.secretLevel is null)");
        	}
            s.append(" and (t." ).append( PROP_isSystem ).append( "=" + Boolean.TRUE + ")")
            .append(" and (t." ).append( PROP_state ).append( "=" + Templete.State.normal.ordinal() + ")")
            .append(" and (a." ).append( TempleteAuth.PROP_authId ).append( " in (:domainIds))")
            .append(" order by t." + PROP_categoryType + ",t." ).append( PROP_sort ).append( ",t." ).append( PROP_createDate);
        
       
        namedParameterMap.put("domainIds", domainIds);

        Pagination.setNeedCount(false);
        
        List<Object[]> list = super.find(s.toString(), -1, -1, namedParameterMap);
        if (list != null) {
            for (Object[] o : list) {
                Templete templete = new Templete();
                int n = 0;
                templete.setId((Long) o[n++]);
                templete.setCategoryId((Long) o[n++]);
                templete.setCreateDate((Timestamp) o[n++]);
                templete.setDescription((String) o[n++]);
                templete.setOrgAccountId((Long) o[n++]);
                templete.setSort((Integer) o[n++]);
                templete.setState((Integer) o[n++]);
                templete.setSubject((String) o[n++]);
                templete.setType((String) o[n++]);
                templete.setBodyType((String) o[n++]);
                templete.setCategoryType((Integer) o[n++]);
                templete.setMemberId((Long) o[n++]);
                result.add(templete);
            }
        }
        return result;
    }
    
    private List<Long> getDepartmentIds(List<Long> domainIds, String path){
        try {
            V3xOrgDepartment parentDept = orgManager.getDepartmentByPath(path);
            if(parentDept != null){
                domainIds.add(parentDept.getId());
                if(parentDept.getParentPath().length() > 1){
                    getDepartmentIds(domainIds, parentDept.getParentPath());
                }
            }
        }
        catch (BusinessException e) {
            log.error("", e);
        }
        return domainIds;
    }
    
    public List<Templete> getSystemTempletesByOrgEntity(String orgEntityType, Long orgEntityId, Integer... categoryType){
        //当前部门相关的所有Id
        List<Long> domainIds = new ArrayList<Long>();
        if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgEntityType)){ //部门
	        try {
	            domainIds.add(orgEntityId);
	            V3xOrgDepartment department = this.orgManager.getDepartmentById(orgEntityId);
	            if(department != null){
	                String path = department.getParentPath();
	                getDepartmentIds(domainIds, path);
	                domainIds.add(department.getOrgAccountId());
	            }
	        }
	        catch (BusinessException e) {
	            log.error("", e);
	        }
        }else if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgEntityType)){
			return this.getSystemTempletesByMemberId(orgEntityId, VIRTUAL_ACCOUNT_ID, categoryType);
        }
        //其他组织实体在此追加
        else{
        	log.warn("传递参数没有得到正确匹配，请检查.");
        }
        List<Templete> result = systemTempleteQueryHelper(domainIds, categoryType);
        return result;
    }
    
	@SuppressWarnings("unchecked")
	public boolean hasAccSystemTempletes(Long tempId,Long userId) {
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(userId, VIRTUAL_ACCOUNT_ID,ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		
		return this.hasAccSystemTempletes(tempId, userId, domainIds);
	}
	
	public boolean hasAccSystemTempletes(Long tempId,Long userId, List<Long> domainIds) {
		if(tempId==null){return true;}
		if(domainIds==null || domainIds.isEmpty()){return false;}
		// 我能访问的所有Id

		String s = "";
		s += "select ";
		s += " t.id";
		s += " from " + ENTITY_NAME + " as t, " + TempleteAuth.ENTITY_NAME + " as a ";
		s += " where";		
		s += " (t.id=a." + TempleteAuth.PROP_objectId + ")";
		
		//s += " and (t." + PROP_categoryType + " in (" + StringUtils.join(categoryType, ",") + "))";
		s += " and (t." + PROP_isSystem + "=?)"; // 系统模板用true,个人模板为false
		s += " and (t." + PROP_state + "=?)";
		s += " and (t.id=?)";
		s += " and (a." + TempleteAuth.PROP_authId + " in (:domainIds))";
		

		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("domainIds", domainIds);

    	Pagination.setNeedCount(false);
		
		List<Object[]> list = super.find(s, -1, -1, namedParameterMap, Boolean.TRUE, Templete.State.normal.ordinal(), tempId);
		if(list!=null && list.size()>0){return true;}
		else {return false;}
	}

	@SuppressWarnings("unchecked")
	public List<Templete> getPersonalTemplete() {
		User currentUser = CurrentUser.get();

		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		//左侧树中个人模版不按单位区分，以人为中心。（孙老师暂时让如此修改）
		//criteria.add(Expression.eq(PROP_orgAccountId, currentUser.getLoginAccount()));
		criteria.add(Expression.eq(PROP_memberId, currentUser.getId()));
		criteria.add(Expression.eq(PROP_isSystem, Boolean.FALSE));
		//左侧树中个人模版只显示state为0的。
		criteria.add(Expression.eq(PROP_state, 0));
		return super.executeCriteria(criteria, -1, -1);
	}
	//成发集团项目 重写getPersonalTemplete
	@SuppressWarnings("unchecked")
	public List<Templete> getPersonalTemplete(String secretLevel) {
		User currentUser = CurrentUser.get();

		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		//左侧树中个人模版不按单位区分，以人为中心。（孙老师暂时让如此修改）
		//criteria.add(Expression.eq(PROP_orgAccountId, currentUser.getLoginAccount()));
		criteria.add(Expression.eq(PROP_memberId, currentUser.getId()));
		criteria.add(Expression.eq(PROP_isSystem, Boolean.FALSE));
		//左侧树中个人模版只显示state为0的。
		criteria.add(Expression.eq(PROP_state, 0));
		if(secretLevel != null && !secretLevel.equals(""))
		//criteria.add(Expression.le("secretLevel", Integer.parseInt(secretLevel)));
		criteria.add(Restrictions.sqlRestriction("(secret_level <="+secretLevel+" or secret_level is null)"));
		return super.executeCriteria(criteria, -1, -1);
	}
	
	public List<Templete> getPersonalTemplete(Integer[] categoryTypes) {
		User currentUser = CurrentUser.get();
		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		//左侧树中个人模版不按单位区分，以人为中心。（孙老师暂时让如此修改）
		//criteria.add(Expression.eq(PROP_orgAccountId, currentUser.getLoginAccount()));
		criteria.add(Expression.eq(PROP_memberId, currentUser.getId()));
		criteria.add(Expression.eq(PROP_isSystem, Boolean.FALSE));
		//左侧树中个人模版只显示state为0的。
		criteria.add(Expression.eq(PROP_state, 0));
		boolean isCol = false;
		for(int i=0; i<categoryTypes.length; i++) {
			if(0==categoryTypes[i] || 4==categoryTypes[i]) {
				isCol = true;
				break;
			}
		}
		if(!isCol) {//政务公文，信息
			criteria.add(Expression.in(PROP_categoryType, categoryTypes));
		} else {//政务协同
			criteria.add(Expression.or(Restrictions.isNull(PROP_categoryType), Expression.in(PROP_categoryType, categoryTypes)));
		}		
		return super.executeCriteria(criteria, -1, -1);
	}

	public List<Templete> getPersonalAllTemplete() {
		User currentUser = CurrentUser.get();

		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
		//criteria.add(Expression.eq(PROP_orgAccountId, currentUser.getLoginAccount()));
		//去掉单位判断，应该能查询来外单位模板
		criteria.add(Expression.eq(PROP_memberId, currentUser.getId()));
		criteria.add(Expression.eq(PROP_isSystem, Boolean.FALSE));
		criteria.addOrder(Order.desc(PROP_createDate));
		return super.executeCriteria(criteria, -1, -1);
	}
	
	public void update(Templete templete) {
		this.templeteAuthManager.delete(templete.getId());
		
		super.update(templete);
	}

	public void updateAuth(long templeteId, Set<TempleteAuth> templeteAuths) {
		this.templeteAuthManager.delete(templeteId);
		
		for (TempleteAuth auth : templeteAuths) {
			super.save(auth);
		}
	}

	public void updateCategoryId(long templeteId, Long newCategoryId) {
		Map<String, Object> columns = new HashMap<String, Object>();

		columns.put(Templete.PROP_categoryId, newCategoryId);

		super.update(templeteId, columns);
	}
	
	public void delete(long id){
		this.templeteAuthManager.delete(id);
    	getHibernateTemplate().bulkUpdate("delete from com.seeyon.v3x.collaboration.templete.domain.ColBranch where templateId=?",new Object[] {id});
		super.delete(id);
		try {
			this.attachmentManager.removeByReference(id);
		}
		catch (Exception e) {
			log.error("", e);
		}
	}

	public void updateTempleteState(long templeteId, State state) {
		Map<String, Object> columns = new HashMap<String, Object>();

		columns.put(Templete.PROP_state, state.ordinal());

		super.update(templeteId, columns);
	}

	@SuppressWarnings("unchecked")
	public List<Long> checkSubject4System(Long categoryId, String subject) {
		User user = CurrentUser.get();
		DetachedCriteria criterca = DetachedCriteria.forClass(Templete.class);
		
		criterca.setProjection(Projections.id());
		
		criterca.add(Expression.eq(PROP_orgAccountId, user.getLoginAccount()));
		criterca.add(Expression.eq(PROP_subject, subject));
		
		if(categoryId == null){
			criterca.add(Expression.isNull(PROP_categoryId));
		}
		else{
			criterca.add(Expression.eq(PROP_categoryId, categoryId));
		}
		
		criterca.add(Expression.eq(PROP_isSystem, true));

		List list = super.executeCriteria(criterca, -1, -1);

		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> checkSubject4Personal(String type, String subject) {
		User user = CurrentUser.get();
		DetachedCriteria criterca = DetachedCriteria.forClass(Templete.class);
		
		criterca.setProjection(Projections.id());
		
		criterca.add(Expression.eq(PROP_orgAccountId, user.getLoginAccount()));
		criterca.add(Expression.eq(PROP_memberId, user.getId()));
		criterca.add(Expression.eq(PROP_isSystem, Boolean.FALSE));
		criterca.add(Expression.eq(PROP_subject, subject));
		criterca.add(Expression.eq(PROP_type, type));		

		List list = super.executeCriteria(criterca, -1, -1);

		return list;
	}
	
	/**
	 * 批量删除
	 */
	public boolean delete(List<String> ids) {
		if(ids == null || ids.isEmpty())
			return true;
		for (String id : ids) {
			this.delete(Long.parseLong(id));
		}
		
		return true;
	}
	
	/**
     * 保存分支条件
     */
    public void saveBranch(long templateId,List<ColBranch> branchs) {
    	getHibernateTemplate().bulkUpdate("delete from com.seeyon.v3x.collaboration.templete.domain.ColBranch where templateId=?",new Object[] {templateId});
    	if(branchs==null || branchs.size()==0)
    		return;
    	for(ColBranch branch:branchs) {
    		super.save(branch);
    	}
    }
    
    /**
     * 获取分支条件
     * @param id
     * @return
     */
    public ColBranch getBranchById(Long id) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColBranch.class);
        criteria.add(Restrictions.eq("id", id));
        
        List list = super.executeCriteria(criteria, -1, -1);

        if (list != null && !list.isEmpty()) {
            return (ColBranch) list.get(0);
        }

        return null;
    }
    
    /**
     * 通过模板id取得分支条件
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<ColBranch> getBranchsByTemplateId(Long id,int appType){
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColBranch.class);
        criteria.add(Restrictions.eq("appType", appType)).add(Restrictions.eq("templateId", id));
        List<ColBranch> list = super.executeCriteria(criteria, -1, -1);

        return list;
    }
    
    /**
     * 通过模板id和link id取得分支条件
     * @param id
     * @return
     */
    public ColBranch getBranchByTemplateAndLink(int appType,long templateId,long linkId){
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColBranch.class);
    	criteria.add(Restrictions.eq("templateId", templateId)).add(Restrictions.eq("linkId", linkId));
    	return (ColBranch) super.executeUniqueCriteria(criteria);
    }
    
    @SuppressWarnings("unchecked")
	public List<Templete> getTempleteByPropectId(long projectId, int size){
    	User user = CurrentUser.get();
		long currentUserId = user.getId();

		// 我能访问的所有Id
		List<Long> domainIds = null;
		try {
			domainIds = this.orgManager.getUserDomainIDs(currentUserId,V3xOrgEntity.VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
		}
		catch (BusinessException e) {
			log.error("", e);
		}

		String s = "";
		s += "select DISTINCT";
		s += " t.id";
		s += ",t." + PROP_categoryId;
		s += ",t." + PROP_createDate;
		s += ",t." + PROP_description;
		s += ",t." + PROP_orgAccountId;
		s += ",t." + PROP_sort;
		s += ",t." + PROP_state;
		s += ",t." + PROP_subject;
		s += ",t." + PROP_type;
		s += ",t." + PROP_bodyType;

		s += " from " + ENTITY_NAME + " as t, " + TempleteAuth.ENTITY_NAME + " as a ";
		s += " where";		
		s += " (t.id=a." + TempleteAuth.PROP_objectId + ")";
		s += " and (t." + PROP_categoryType + "=?)";
		s += " and (t." + PROP_projectId + "=?)"; // 系统模板用true,个人模板为false
		s += " and (t." + PROP_isSystem + "=?)"; // 系统模板用true,个人模板为false
		s += " and (t." + PROP_state + "=?)";
		s += " and (a." + TempleteAuth.PROP_authId + " in (:domainIds))";
		s += " order by t." + PROP_sort + ",t." + PROP_createDate;

		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("domainIds", domainIds);
    	
		List<Templete> result = new ArrayList<Templete>();
		List<Object[]> list = super.find(s, 0, size, namedParameterMap, TempleteCategory.TYPE.collaboration_templete.ordinal(), projectId, Boolean.TRUE, Templete.State.normal.ordinal());
		if (list != null) {
			for (Object[] o : list) {

				Templete templete = new Templete();
				int n = 0;
				templete.setId((Long) o[n++]);
				templete.setCategoryId((Long) o[n++]);
				templete.setCreateDate((Timestamp) o[n++]);
				templete.setDescription((String) o[n++]);
				templete.setOrgAccountId((Long) o[n++]);
				templete.setSort((Integer) o[n++]);
				templete.setState((Integer) o[n++]);
				templete.setSubject((String) o[n++]);
				templete.setType((String) o[n++]);
				templete.setBodyType((String) o[n++]);

				templete.setMemberId(0L);

				result.add(templete);
			}
		}

		return result;
    }
    
    public boolean isTempleteUnique(String tempName, Long tempId){
    	User currentUser = CurrentUser.get();
    	Object o = super.findUnique("select id from Templete where id!=? and orgAccountId=? and memberId=? and isSystem=0 and subject=?", null,
				tempId, currentUser.getLoginAccount(), currentUser.getId(), tempName);
		
    	return o == null ? true : false ;
    }

	public List<Templete> getTemplateByformParentId(Long formParentId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Templete.class);
        criteria.add(Restrictions.eq("formParentId", formParentId));
        List<Templete> list = super.executeCriteria(criteria, -1, -1);

        return list;
	}
    
    /**
     * 校验模板是否存在，用于AJAX调用
     * @return
     */
    public boolean checkTempleteIsExist(long templeteId){
    	Object o = super.findUnique("select id from Templete where id=?", null, templeteId);
    	return o != null;
    }
    
    /**
     * 通过模板编号获取模板对象
     * @param templeteCode   模板编号
     * @return               模板对象
     */
    public Templete getTempleteByCode(String templeteCode) {
    	DetachedCriteria dc = DetachedCriteria.forClass(Templete.class);
    	dc.add(Restrictions.eq("templeteNumber", templeteCode));
    	return (Templete)super.executeUniqueCriteria(dc);
    }

    /**
     * 校验模板编号是否唯一
     */
    public boolean checkTempleteCodeIsUnique(String templeteIdStr, String templeteCode) {
        DetachedCriteria dc = DetachedCriteria.forClass(Templete.class);
        dc.add(Restrictions.eq("templeteNumber", templeteCode));
        if(Strings.isNotBlank(templeteIdStr)){
            dc.add(Restrictions.ne("id", Long.parseLong(templeteIdStr)));
        }
        Templete templete = (Templete)super.executeUniqueCriteria(dc);
        return templete==null;
    }
    
    /**
     * 修改表单所属人时同步修改模板的所属人
     * @param templeteId
     * @param newMemberId
     * @throws DataDefineException 
     */
	public void updateMemberId(List<Long> templeteIdlist, Long newMemberId) throws DataDefineException {
		try{		
	
			if(templeteIdlist.size() > 1)
			{
				String hql = "update from Templete fo set memberId =:fmemberId  where fo.id in (:templeteIdlist)";	
				Map<String, Object> nmap = new HashMap<String, Object>();
				nmap.put("fmemberId", newMemberId);
				nmap.put("templeteIdlist", templeteIdlist);
				super.bulkUpdate(hql,nmap);	
			}else{
				String hql = "update from Templete fo set memberId =? where fo.id=?";	
				super.bulkUpdate(hql,null,newMemberId,templeteIdlist.get(0));	
			}			
		}catch(Exception e){
			throw new DataDefineException(DataDefineException.C_iDbOperErrode_DelError,e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Templete> getTempletes4BizConfig(Long bizConfigId, List<Long> domainIds) {
		String hql = " select distinct new Templete(t.id, t.subject, r.sortId) from Templete as t, TempleteAuth as a, FormBizConfigTempletProfile as r " +
					 " where t.id=a.objectId and t.id=r.formTempletId and t.state=:state and " +
					 " (a.authId in (:domainIds)) and r.formBizConfigId=:bizConfigId order by r.sortId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		params.put("state", Templete.State.normal.ordinal());
		params.put("bizConfigId", bizConfigId);		
		return this.find(hql, -1, -1, params);
	}
	
	public boolean checkTempletes4BizConfigIsEmpty(Long bizConfigId){
		String hql =  "select count(*) " +
		  "from Templete as t, FormBizConfigTempletProfile as r where t.id=r.formTempletId and t.state=:state " +
		  " and r.formBizConfigId=:bizConfigId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Templete.State.normal.ordinal());
		params.put("bizConfigId", bizConfigId);
		int count = 0;
		List results = this.find(hql, -1, -1, params);
        if(results!=null && !results.isEmpty()){
            count = (Integer)results.get(0);
        }
		return count <= 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<Templete> getTempletes4BizConfigWithoutAuthCheck(Long bizConfigId) {
		String hql =  "select new Templete(t.id, t.subject, r.sortId) " +
					  "from Templete as t, FormBizConfigTempletProfile as r where t.id=r.formTempletId and t.state=:state " +
					  " and r.formBizConfigId=:bizConfigId order by r.sortId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", Templete.State.normal.ordinal());
		params.put("bizConfigId", bizConfigId);
		return this.find(hql, -1, -1, params);
	}
	
	private ISeeyonFormAppManager appManager = SeeyonForm_Runtime.getInstance().getAppManager();
	
	@SuppressWarnings("unchecked")
	public List<Templete> getTempletesWithFormInfo4BizConfig(Long bizConfigId, List<Long> domainIds) {
		String hql = "select distinct new Templete(t.id, t.subject, t.memberId, r.sortId) from Templete as t, TempleteAuth as a, FormBizConfigTempletProfile as r " +
					 " where t.id=a.objectId and t.id=r.formTempletId and t.state=:state and a.authId in (:domainIds) and r.formBizConfigId=:bizConfigId" + 
					 " order by r.sortId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		params.put("state", Templete.State.normal.ordinal());
		params.put("bizConfigId", bizConfigId);		
		List<Templete> result = super.find(hql, -1, -1, params);
		
		if(CollectionUtils.isNotEmpty(result)) {
			List<Long> ids = FormBizConfigUtils.getIds(result);
			Map<Long, String> map = getTempleteBodyMap(ids);
			for(Templete t : result) {
				try {
					String body = map.get(t.getId());
					Long formAppId = FormBizConfigUtils.getFormAppId(body);
					ISeeyonForm_Application app = appManager.findById(formAppId);
					t.setFormAppName(app.getAppName());
				}
				catch(Exception e) {
					logger.warn("表单模板[id=" + t.getId() + ", 名称=" + t.getSubject() + "]无法获取与之对应的表单信息，请检查该表单及模板是否正常!");
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Map<Long, String> getTempleteBodyMap(List<Long> ids) {
		Map<Long, String> map = new HashMap<Long, String>();
		String hql2 = "select id, body from Templete where id in (:ids)";
		List<Object[]> objs = super.find(hql2, -1, -1, FormBizConfigUtils.newHashMap("ids", ids));
		for(Object[] arr : objs) {
			map.put((Long)arr[0], (String)arr[1]);
		}
		return map;
	}
	
	/**
	 * 辅助书写Hql语句：所要查询的表单模板字段
	 */
	private StringBuffer getSelectFieldsHqlStr() {
    	StringBuffer s = new StringBuffer();
        s.append("select DISTINCT")
            .append(" t.id")
            .append(",t.").append(PROP_categoryId)
            .append(",t.").append(PROP_createDate)
            .append(",t.").append(PROP_description)
            .append(",t.").append(PROP_orgAccountId)
            .append(",t.").append(PROP_sort)
            .append(",t.").append(PROP_state)
            .append(",t.").append(PROP_subject)
            .append(",t.").append(PROP_type)
            .append(",t.").append(PROP_bodyType)
            .append(",t.").append(PROP_categoryType)
            .append(",t.").append(PROP_memberId)
            .append(",t.").append(Templete.PROP_standardDuration);
        return s;
    }
	
	/**
	 * 将查询所得数组结果组装为表单模板集合
	 */
	private List<Templete> parseObjArray2Templetes(List<Object[]> list) {
    	List<Templete> result = new ArrayList<Templete>();
    	if (list != null) {
    		Templete templete = null;
            for (Object[] o : list) {
            	templete = new Templete();
                int n = 0;
                templete.setId((Long) o[n++]);
                templete.setCategoryId((Long) o[n++]);
                templete.setCreateDate((Timestamp) o[n++]);
                templete.setDescription((String) o[n++]);
                templete.setOrgAccountId((Long) o[n++]);
                templete.setSort((Integer) o[n++]);
                templete.setState((Integer) o[n++]);
                templete.setSubject((String) o[n++]);
                templete.setType((String) o[n++]);
                templete.setBodyType((String) o[n++]);
                templete.setCategoryType((Integer) o[n++]);
                templete.setMemberId((Long) o[n++]);
                templete.setStandardDuration((Integer)o[n++]);
                result.add(templete);
            }
        }
        return result;
    }
	
	@SuppressWarnings("unchecked")
	public List<Templete> getTempletesByIds(List<Long> ids) {
		String hql = "from " + Templete.class.getName() + " as t where t.id in (:ids) and t.state=:state";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		params.put("state", Templete.State.normal.ordinal());
		return this.find(hql, -1, -1, params);
	}
	
	public List<Templete> getAllSystemTempletesInAccount(Long accountId, String condition, String textfield, Integer... type) {
        StringBuffer hqlStr = this.getSelectFieldsHqlStr();
        hqlStr.append(" from ").append(ENTITY_NAME).append(" as t ")
         	  .append(" where t.").append(PROP_orgAccountId).append( "= :accountId" );
        if(type == null) {
        	hqlStr.append(" and t." ).append( PROP_categoryType ).append( "= :categoryType" );
        } else {
        	hqlStr.append(" and t." ).append( PROP_categoryType ).append( " in (:categoryType)" );
        }
        
        hqlStr.append(" and t." ).append( PROP_isSystem ).append( "= :isSystem");
        hqlStr .append(" and (t." ).append( PROP_state ).append( "=" + Templete.State.normal.ordinal() + ")");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("accountId", accountId);
        if(type == null || type.length == 0){
        	params.put("categoryType", TempleteCategory.TYPE.form.ordinal());
        } else {
        	params.put("categoryType", Arrays.asList(type));
        }
        params.put("isSystem", Boolean.TRUE);
        
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
	        if(condition.equalsIgnoreCase("subject")) {
	        	hqlStr.append(" and t." ).append( PROP_subject ).append( " like :subject");
	        	params.put("subject", "%" + SQLWildcardUtil.escape(textfield.trim()) + "%");
	        } else if(condition.equalsIgnoreCase("category")) {
	        	hqlStr.append(" and t." ).append( PROP_categoryId ).append( "= :categoryId");
	        	params.put("categoryId", Long.parseLong(textfield));
	        }
        }        
    	hqlStr.append(" order by t." ).append( PROP_sort ).append( ",t." ).append( PROP_createDate);
    	List<Object[]> result = (List<Object[]>)super.find(hqlStr.toString(), -1, -1, params);
    	return this.parseObjArray2Templetes(result);
	}
	
    @SuppressWarnings("unchecked")
	public List<Templete> getSysFormTempsByMemberId(Long memberId, String condition, String textfield) {
        StringBuffer hqlStr = this.getSelectFieldsHqlStr();
        hqlStr.append(" from ").append(ENTITY_NAME).append(" as t, ").append(TempleteAuth.ENTITY_NAME ).append( " as a ")
            .append(" where (t.id=a." ).append( TempleteAuth.PROP_objectId ).append( ")")
            .append(" and (t." ).append( PROP_categoryType ).append( "= :categoryType)" )
            .append(" and (t." ).append( PROP_isSystem ).append( "= :isSystem)")
            .append(" and (t." ).append( PROP_state ).append( "=" + Templete.State.normal.ordinal() + ")");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("categoryType", TempleteCategory.TYPE.form.ordinal());
        params.put("isSystem", Boolean.TRUE);
        
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
	        if(condition.equalsIgnoreCase("subject")) {
	        	hqlStr.append(" and (t." ).append( PROP_subject ).append( " like :subject)");
	        	params.put("subject", "%" + SQLWildcardUtil.escape(textfield).trim() + "%");
	        } else if(condition.equalsIgnoreCase("category")) {
	        	hqlStr.append(" and (t." ).append( PROP_categoryId ).append( "= :categoryId)");
	        	params.put("categoryId", Long.parseLong(textfield));
	        }
        }
        
    	hqlStr.append(" and (a." ).append( TempleteAuth.PROP_authId ).append( " in (:domainIds))")
              .append(" order by t." ).append( PROP_sort ).append( ",t." ).append( PROP_createDate);        
        params.put("domainIds", FormBizConfigUtils.getUserDomainIds(memberId, orgManager));
        
        List<Object[]> result = (List<Object[]>)super.find(hqlStr.toString(), -1, -1, params);
        return this.parseObjArray2Templetes(result);
    }
    
    public List<Templete> getListByIds(List<Long> ids){
    	if(ids != null && ids.size() !=0){
    		StringBuilder hql = new StringBuilder("from Templete where ");
    		List<List<Long>> splitList = splitList(ids,200);
    		int size = splitList.size();
    		Map<String,Object> parameter = new HashMap<String,Object>();
    		for(int i = 0 ; i < size ;i++){
    			if(i != 0){
    				hql.append(" or ");
    			}
    			hql.append(" id in (:id"+i+")");
    			parameter.put("id"+i, splitList.get(i));
    		}
    		return super.find(hql.toString(), parameter);
    	}
    	return null;
    }
    
    private List<List<Long>> splitList(List<Long> list ,int count){
    	List<List<Long>> result = new ArrayList<List<Long>>();
    	if(list != null ){
    		if(list.size() <= count){
    			result.add(list);
    		}else{
    			int size = list.size();
    			for(int i = 0 ; ;){
    				if(i+count >= size){
    					result.add(list.subList(i, size));
    					break;
    				}else{
    					result.add(list.subList(i, count));
    				}
    				i+= count;
    			}
    		}
    	}
    	return result;
    }
    
    /**
	 * 通过事项查询对应的模板
	 * @param memberId  人员id
	 * @param app       应用分类
	 * @param state     事项状态
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Templete> getTemplatesByAffair(Long memberId,int app,int state,String searchType,String textfield) {
		StringBuilder hql = new StringBuilder("from Templete t where exists (select id from Affair a where t.id=a.templeteId and a.app=? and a.state=? and a.memberId=?) and t.isSystem=true");
		Map<String,Object> p = null;
		if(Strings.isNotBlank(searchType) && Strings.isNotBlank(textfield)) {
			if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(searchType)) {
				hql.append(" and t.subject like :subject");
				p = new HashMap<String,Object>();
				p.put("subject", "%" + SQLWildcardUtil.escape(textfield.trim()) + "%");
			}
			else if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(searchType)) {
				hql.append(" and t.categoryId=:categoryId");
				p = new HashMap<String,Object>();
				p.put("categoryId", NumberUtils.toLong(textfield));
			}
		}
		return super.find(hql.toString(), -1, -1, p, app,state,memberId);
	}
	
    public String checkTemplete(Long templeteId,Long userId){
    	Templete t = get(templeteId);
    	if(t != null){
    		//个人模板不判断
    		if(t.getIsSystem()){
    			boolean hasAcl = this.hasAccSystemTempletes(templeteId, userId);
            	if(!hasAcl){
            		return Constant.getString("templete.cannot.use");
            	}
    		}
    	}else{
    		return Constant.getString("templete.cannot.use");
    	}
    	
    	return null;
    }

	@Override
	public List getListByUserId(String userid) {
//		StringBuffer sql= new StringBuffer("select t from Templete t ");
//		sql.append("where t.workflow like '% partyId=\"").append(userid).append("\"%'");
//		sql.append(" and t.workflow not like '% =partyId=\"").append(userid).append("\"%'");
//		List list= super.getHibernateTemplate().find(sql.toString());
		StringBuffer sql= new StringBuffer("select t from Templete t ");
		sql.append(" where t.workflow like ? ");
		sql.append(" and t.workflow not like ? ");
		List<Templete> list= super.getHibernateTemplate().find(sql.toString(),
				new String[]{"% partyId=\""+userid+"\"%","%=partyId=\""+userid+"\"%"});
		List<Templete> returnList = new ArrayList<Templete>();
		if( list!=null && !list.isEmpty()){
			for (Templete templete : list) {
				String processXml= templete.getWorkflow();
				if(Strings.isNotBlank(processXml)){
					String tempProcessXml= processXml.replaceAll("<node.+?id=\\\"start\".+?>.+?</node>", "");
					if(tempProcessXml.indexOf("partyId=\"" + userid +"\"")!=-1){
                        returnList.add(templete);
                    }
				}
			}
		}
		return returnList;
	}

	public List<Templete> getAllSystemTempletesByAcl(Long accountId,
			Integer categoryType) {
		
		//得到本单位下面的部门ID，组ID，岗位ID，职务级别ID
		List<Long> ids = new UniqueList<Long>();
		try {
			//部门
			List<V3xOrgDepartment> depts = orgManager.getAllDepartments(accountId,false);
			if(Strings.isNotEmpty(depts)){
				for(V3xOrgDepartment dept : depts){
					ids.add(dept.getId());
				}
			}
			//岗位
			List<V3xOrgPost> posts = orgManager.getAllPosts(accountId, false, false);
			if(Strings.isNotEmpty(posts)){
				for(V3xOrgPost post : posts){
					ids.add(post.getId());
				}
			}
			List<V3xOrgTeam> teams = orgManager.getAllTeams(accountId, false, false);
			if(Strings.isNotEmpty(teams)){
				for(V3xOrgTeam team : teams){
					ids.add(team.getId());
				}
			}
			ids.add(accountId);
			
			V3xOrgAccount root = orgManager.getRootAccount();
			if(root != null){
				ids.add(root.getId());
			}
			
			List<V3xOrgPost> standardPost = orgManager.getAllBenchmarkPost(accountId);
			if(Strings.isNotEmpty(standardPost)){
				for(V3xOrgPost post: standardPost){
					ids.add(post.getId());
				}
			}
		} catch (BusinessException e) {
			log.error(e);
		}
		List<Templete> resultList = new ArrayList<Templete>();
		
		if(Strings.isNotEmpty(ids)){
			List<Long>[]  arr = Strings.splitList(ids, 1000);
			for(List<Long> idl : arr){
				resultList.addAll(getAllSystemTempletesByAclAndSpecialAuthID(accountId,categoryType, idl));
			}
		}
		//去掉重复的模板
		Set<Long> tempIds = new HashSet<Long>();
		List<Templete> resultList1 = new ArrayList<Templete>();
		for(Iterator<Templete> it = resultList.iterator();it.hasNext();){
			Templete templete=it.next();
			if(!tempIds.contains(templete.getId())){
				tempIds.add(templete.getId());
				resultList1.add(templete);
			}
		}
		return resultList1;
	}

	private List<Templete> getAllSystemTempletesByAclAndSpecialAuthID(
			Long accountId, Integer categoryType, List<Long> ids) {
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(getSelectFieldsHqlStr().toString())
		  .append(" from Templete as t left join t.templeteAuths as  auths")
		  .append(" where ")
		  .append(" t.isSystem = :isSystem and ")
		  .append(" (")
		  .append("	 	t.orgAccountId = :accountId  or ")
		  .append("     auths.authId in (:ids) ")
		  .append(" )")
		  .append(" and t.state=0 ");
		
		
		  // 一个单位下的部门ID+岗位ID +组ID应该不会超过1000吧，超过一千的话就得另外换个方式了。
		  if(categoryType!=null){
			  List<Integer> cs = new ArrayList<Integer>();
			  if(TempleteCategory.TYPE.edoc.ordinal() == categoryType.intValue()){
				  cs.add(TempleteCategory.TYPE.edoc.ordinal());
				  cs.add(TempleteCategory.TYPE.sginReport.ordinal());
				  cs.add(TempleteCategory.TYPE.edoc_rec.ordinal());
				  cs.add(TempleteCategory.TYPE.edoc_send.ordinal());
			  }else{
				  cs.add(categoryType);
			  }
			  sb.append(" and t.categoryType in (:categoryType) ");
			  namedParameterMap.put("categoryType", cs);
		  }
		
		  sb.append(" order by t.sort asc, t.createDate asc ");

	    namedParameterMap.put("accountId",accountId );
	    namedParameterMap.put("isSystem",Boolean.TRUE);
	    namedParameterMap.put("ids", ids);
	    
	    List<Object[]> result = (List<Object[]>)super.find(sb.toString(), -1, -1, namedParameterMap);
	
		return parseObjArray2Templetes(result);
	}
public List<Templete> getAllSystemTempletesByEntityIds(List<Long> templeteIds,Integer categoryType){
		if(Strings.isEmpty(templeteIds)){
			log.info("查询模板的时候，传入的参数为空[TempleteManagerImpl.getAllSystemTempletesByEntityIds]");
			return new ArrayList<Templete>();
		}
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(getSelectFieldsHqlStr().toString())
		  .append(" from Templete t ")
		  .append(" where ")
		  .append(" t.id in (:templeteIds)")  
		  .append(" and t.isSystem = :isSystem ");
		
		 if(categoryType!=null){
			  List<Integer> cs = new ArrayList<Integer>();
			  if(TempleteCategory.TYPE.edoc.ordinal() == categoryType.intValue()){
				  cs.add(TempleteCategory.TYPE.edoc.ordinal());
				  cs.add(TempleteCategory.TYPE.sginReport.ordinal());
				  cs.add(TempleteCategory.TYPE.edoc_rec.ordinal());
				  cs.add(TempleteCategory.TYPE.edoc_send.ordinal());
			  }else{
				  cs.add(categoryType);
			  }
			  sb.append(" and t.categoryType in (:categoryType) ");
			  namedParameterMap.put("categoryType", cs);
		  }
		sb.append(" order by t.sort asc, t.createDate asc ");
		
        
        namedParameterMap.put("templeteIds", templeteIds);
        namedParameterMap.put("isSystem",Boolean.TRUE);
        
        List<Object[]> result = (List<Object[]>)super.find(sb.toString(), -1, -1, namedParameterMap);

		return parseObjArray2Templetes(result);
	}

	@Override
	public Map<Long, SimpleTemplete> getSystemTempleteSimpleInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(" select t.id, ");
		sb.append(" t.subject ,");
		sb.append(" t.memberId,");
		sb.append(" t.standardDuration ");
		sb.append(" from ");
		sb.append(" Templete as t ");
		sb.append(" where ");
		sb.append(" isSystem = ? ");
		
		List l = super.find(sb.toString(),-1,-1, null,Boolean.TRUE);
		Map<Long,SimpleTemplete> m = new HashMap<Long,SimpleTemplete>();
		if(Strings.isNotEmpty(l)){
			for(Object o : l){
				Object[] a  = (Object[])o;
				Long id = ((Number)a[0]).longValue();
				String subject = (String)a[1];
				Long memberId = ((Number)a[2]).longValue();
				Integer sd = 0;
				if(a[3]!=null){
					sd = ((Number)a[3]).intValue();
				}
				SimpleTemplete st = new SimpleTemplete();
				st.setId(id);
				st.setMemberId(memberId);
				st.setSubject(subject);
				st.setStandardDuration(sd);
				m.put(id, st);
			}
		}
		return m;
	}
	
	public <T> List<T>[] splitListCommon(List<T> list, int num){
		if(Strings.isEmpty(list)){
			return new ArrayList[0];
		}
		if(num < 2){
			throw new IllegalArgumentException("Argument num [" + num + "] must greater then 2");
		}
		
		int length = (int)Math.ceil((double)list.size() / (double)num);
		List<T>[] result = new ArrayList[length];
		
		for (int i = 0; i < length; i++) {
			int first = i * num;
			int max = Math.min(list.size(), first + num);
			
			List<T> temp = list.subList(first, max);
			result[i] = new ArrayList<T>(temp);
		}
		
		return result;
	}
}