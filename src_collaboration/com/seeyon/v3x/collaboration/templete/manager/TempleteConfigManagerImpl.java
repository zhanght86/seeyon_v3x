package com.seeyon.v3x.collaboration.templete.manager;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class TempleteConfigManagerImpl extends BaseHibernateDao<TempleteConfig> implements TempleteConfigManager
{
    private static Log log = LogFactory.getLog(TempleteConfigManagerImpl.class);
    private TempleteManager templeteManager;
    private OrgManager orgManager;

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }

    public void cancelPush(List<Long> configIds)
    {
        if(configIds==null || configIds.isEmpty())return ;
        String hql = "delete from " + TempleteConfig.class.getName() + " as c where (c.id in (:ids))";
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        nameParameters.put("ids", configIds);

        super.bulkUpdate(hql, nameParameters);
    }

    public List<TempleteConfig> getConfigTempletes(long memeberId,String type,String value,String value1, Object... isNotPagination)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        return this.getMyTempletes(memeberId, "", nameParameters, -1,true,type,value,value1, isNotPagination);
    }
    
    //成发集团项目 重写getConfigTempletes
    public List<TempleteConfig> getConfigTempletes(long memeberId,String type,String value,String value1,String secretLevel,Object... isNotPagination)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        return this.getMyTempletes(memeberId, "", nameParameters, -1,true,type,value,value1,secretLevel,isNotPagination);
    }
    //成发集团项目 重写getConfigTempletes
    public List<TempleteConfig> getConfigTempletes(long memeberId,String secretLevel,Object... isNotPagination)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        return this.getMyTempletes(memeberId, "", nameParameters, -1,true,secretLevel ,isNotPagination);
    }

    public List<TempleteConfig> getConfigTempletes(long memeberId, Object... isNotPagination)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        return this.getMyTempletes(memeberId, "", nameParameters, -1,true, isNotPagination);
    }
    public List<TempleteConfig> getConfigTempletes(long memeberId, int count)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();

		List<TempleteConfig> result =  this.getMyTempletes(memeberId, "", nameParameters, count,true);

		return result;
    }

	public List<TempleteConfig> getConfigTempletesByCategory(long memeberId, int count, String category) {
		return this.getMyTempletesByCategory(memeberId, "", null, count, true, category);
	}
	//成发集团项目 程炯 重写getConfigTempletesByCategory
	public List<TempleteConfig> getConfigTempletesByCategory(long memeberId, int count, String category,String secretLevel) {
		return this.getMyTempletesByCategory(memeberId, "", null, count, true, category,secretLevel);
	}

    public void pushTempletesToMain(Long memberId, Long[] templeteIds, int[] types)
    {
        if(templeteIds.length==0)   return;
        List<TempleteConfig> templeteConfigList = new ArrayList<TempleteConfig>();
        for(int i=0; i<templeteIds.length; i++){
           TempleteConfig templeteConfig = new TempleteConfig();
           templeteConfig.setIdIfNew();
           templeteConfig.setMemberId(memberId);
           templeteConfig.setType(types[i]);
           templeteConfig.setTempleteId(templeteIds[i]);
           templeteConfig.setSort(999);
           templeteConfigList.add(templeteConfig);
        }
        super.saveAll(templeteConfigList);
    }


    public void sortTemplete(Long[] configIds)
    {
        if(configIds.length ==0 )return ;

        for(int i=0; i<configIds.length; i++)
        {
            TempleteConfig templeteConfig = this.getTempleteConfig(configIds[i]);
            if(templeteConfig!=null) {
                templeteConfig.setSort(i);
                super.update(templeteConfig);
            }
        }
    }

    public void pushThisTempleteToMain4Member(Long memberId, Long templeteId, int type)
    {
        TempleteConfig templeteConfig = new TempleteConfig();
        templeteConfig.setIdIfNew();
        templeteConfig.setMemberId(memberId);
        templeteConfig.setTempleteId(templeteId);
        templeteConfig.setType(type);
        templeteConfig.setSort(999); //默认排序号
        super.save(templeteConfig);
    }

    public void pushThisTempleteToMain4Members(List<Long> memberIdsList, Long templeteId, int type){

    	List<TempleteConfig> list =  this.getConfigTempletes(templeteId);
    	this.clearConfigByTempleteId(templeteId);
    	Iterator<TempleteConfig> iterator = null;
    	if(list != null){
    		iterator = list.iterator();
    	}
        //过滤重复人员
        Set<Long> memberIdsSet = new HashSet<Long>(memberIdsList);
        List<TempleteConfig> templeteConfigList = new ArrayList<TempleteConfig>();
    	 for(Long memberId : memberIdsSet){
         	TempleteConfig templeteConfig = new TempleteConfig();
             templeteConfig.setIdIfNew();
             templeteConfig.setMemberId(memberId);
             templeteConfig.setTempleteId(templeteId);
             templeteConfig.setType(type);
             boolean flag = true;
             if(list != null){
            	 while(iterator.hasNext()){
            		 TempleteConfig temp = (TempleteConfig)iterator.next();
            		 if(memberId.equals(temp.getMemberId())){
            			 templeteConfig.setSort(temp.getSort());
            			 flag = false;
            			 iterator.remove();
            			 break;
            		 }
            	 }
             }
             if(flag){
            	 templeteConfig.setSort(999); //默认排序号
             }
             templeteConfigList.add(templeteConfig);
         }
        super.savePatchAll(templeteConfigList);
    }

    public void clearConfigByTempleteId(Long templeteId){
        super.delete(TempleteConfig.class, new Object[][]{{"templeteId", templeteId}});
    }

    public TempleteConfig getTempleteConfig(long id)
    {
        return (TempleteConfig) super.findUniqueBy("id", id);
    }

    public void save(TempleteConfig templeteConfig)
    {
        super.save(templeteConfig);
    }

    public List<TempleteConfig> getConfigTempletesOfColl(long memberId)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        String addCondition = " and (c.type=:collType or c.type=-1) ";
        nameParameters.put("collType", TempleteCategory.TYPE.collaboration_templete.ordinal());
        return this.getMyTempletes(memberId, addCondition, nameParameters, -1, true,true);
    }

    public List<TempleteConfig> getConfigTempletesOfEdoc(long memberId, long orgAccountId)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        String addCondition = " and (c.type in(:edocTypes)) and t.orgAccountId=:orgAccountId";

        List<Integer> edcoTypes = new ArrayList<Integer>();
        edcoTypes.add(TempleteCategory.TYPE.edoc.ordinal());
        edcoTypes.add(TempleteCategory.TYPE.edoc_rec.ordinal());
        edcoTypes.add(TempleteCategory.TYPE.edoc_send.ordinal());
        edcoTypes.add(TempleteCategory.TYPE.sginReport.ordinal());

        nameParameters.put("edocTypes", edcoTypes);
        nameParameters.put("orgAccountId", orgAccountId);
        return this.getMyTempletes(memberId, addCondition, nameParameters, -1,false, true);
    }

    public List<TempleteConfig> getConfigTempletesOfForm(long memberId)
    {
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        String addCondition = " and (c.type=:formType) ";
        nameParameters.put("formType", TempleteCategory.TYPE.form.ordinal());
        return this.getMyTempletes(memberId, addCondition, nameParameters, -1,false, true);
    }
    
    //成发集团项目 重写getMyTempletes
    @SuppressWarnings("unchecked")
    private List<TempleteConfig> getMyTempletes(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal, String type,String value,String value1,String secretLevel,Object... isNotPagination)
    {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        if(secretLevel != null && !secretLevel.equals("")){
        	hqlBuffer.append(" and (t.secretLevel <="+secretLevel+" or t.secretLevel is null)");
        }
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

		if(type!=null && !"".equals(type)){
			if("name".equals(type)){
				if(value!=null && !"".equals(value)){
					hqlBuffer.append(" and t.subject like :subject ");
					nameParameters.put("subject", "%"+SQLWildcardUtil.escape(value)+"%");
				}
			}
		}
        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }

        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }

    @SuppressWarnings("unchecked")
    private List<TempleteConfig> getMyTempletes(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal, String type,String value,String value1, Object... isNotPagination)
    {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

		if(type!=null && !"".equals(type)){
			if("name".equals(type)){
				if(value!=null && !"".equals(value)){
					hqlBuffer.append(" and t.subject like :subject ");
					nameParameters.put("subject", "%"+SQLWildcardUtil.escape(value)+"%");
				}
			}
		}
        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }

        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }
    //成发集团项目 程炯 重写getMyTempletes
    @SuppressWarnings("unchecked")
    private List<TempleteConfig> getMyTempletes(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal,String secretLevel,Object... isNotPagination) {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        if(secretLevel != null && !secretLevel.equals("")){
        	hqlBuffer.append(" and (t.secretLevel <="+secretLevel+" or t.secretLevel is null)");
        }
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }

        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }

    @SuppressWarnings("unchecked")
    private List<TempleteConfig> getMyTempletes(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal, Object... isNotPagination) {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }

        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }
    //重写getMyTempletesByCategory
    @SuppressWarnings("unchecked")
    public List<TempleteConfig> getMyTempletesByCategory(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal, String category,String secretLevel,Object... isNotPagination) {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate,t.isSystem ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        if(secretLevel != null && !secretLevel.equals("")){
        	hqlBuffer.append(" and (t.secretLevel <="+secretLevel+" or t.secretLevel is null)");
        }
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
        hqlBuffer.append("and c.type in (:categoryType)");//按照分类
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

        List<Integer> categoryList = new ArrayList<Integer>();
        String[] cList = category.split(",");
        for(String c : cList) {
        	categoryList.add(new Integer(c));
        }
        nameParameters.put("categoryType", categoryList);

        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }
        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                templeteConfig.setIsSystem(String.valueOf(objects[12]));
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }
    
    @SuppressWarnings("unchecked")
    public List<TempleteConfig> getMyTempletesByCategory(long memberId, String add_onsCondition, Map<String, Object> nameParameters, int count,boolean hasPersonal, String category,Object... isNotPagination) {
        StringBuffer hqlBuffer = new StringBuffer();
        hqlBuffer.append("select distinct c.templeteId, c.id, c.memberId, c.type,  c.sort, t.subject, t.type, t.memberId ,t.categoryType,t.categoryId,t.orgAccountId,t.createDate,t.isSystem ");
        hqlBuffer.append("from " + TempleteConfig.class.getName() + " c, " + Templete.ENTITY_NAME + " t ");
        hqlBuffer.append("where (c.templeteId=t.id) and (c.memberId=:memberId) ");
        //取得个人模板，修改如下：
    	/* 1.先根据Templete TempleteAuth 得到用户能访问的Templete.id
    	 * 2.从TempleteConfig取得模版配置信息
    	 * 3.(1,2)关联用一个条语句查询
    	*/
        hqlBuffer.append("and c.type in (:categoryType)");//按照分类
    	List<Long> domainIds = new ArrayList<Long>();
    	try {
			V3xOrgMember member = orgManager.getMemberById(memberId);
			//外部人员防护，外部人员访问模板只能访问到组和部门。不具备单位、集团范围内的权限
			if(member.getIsInternal()){
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}else{
				domainIds = orgManager.getUserDomainIDs(memberId, member.getOrgAccountId(), ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_TEAM);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		hqlBuffer.append(" and( ");
		if(hasPersonal){
			hqlBuffer.append(" c.type=-1 or c.type=2 or ");
		}
		hqlBuffer.append(" exists (");
		hqlBuffer.append("     select auth.objectId from " + TempleteAuth.class.getName() + " auth where  t.id=auth.objectId and (auth.authId in(:domainIds))");
		hqlBuffer.append("))");
        if(nameParameters == null){
            nameParameters = new HashMap<String, Object>();
        }
        nameParameters.put("memberId", memberId);
        nameParameters.put("domainIds", domainIds);

        List<Integer> categoryList = new ArrayList<Integer>();
        String[] cList = category.split(",");
        for(String c : cList) {
        	categoryList.add(new Integer(c));
        }
        nameParameters.put("categoryType", categoryList);

        if(Strings.isNotBlank(add_onsCondition)){
            hqlBuffer.append(add_onsCondition);
        }
        hqlBuffer.append(" order by c.sort asc,t.createDate");


        List<TempleteConfig> myTempleteList = new ArrayList<TempleteConfig>();
        List<Object[]> result = null;
        if(isNotPagination != null && isNotPagination.length > 0){
            result = super.find(hqlBuffer.toString(), -1, -1, nameParameters);
        }
        else if(count != -1){
            result = super.find(hqlBuffer.toString(), 0, count, nameParameters);
        }
        else{
            List<Object> indexParameter = null;
            result = super.find(hqlBuffer.toString(), nameParameters, indexParameter);
        }

        if(result != null){
            for (Object[] objects : result) {
                TempleteConfig templeteConfig = new  TempleteConfig();
                templeteConfig.setTempleteId((Long)objects[0]);
                templeteConfig.setId((Long)objects[1]);
                templeteConfig.setMemberId((Long)objects[2]);
                templeteConfig.setType((Integer)objects[3]);
                templeteConfig.setSort((Integer)objects[4]);
                templeteConfig.setSubject((String)objects[5]);
                templeteConfig.setTempleteType((String)objects[6]);
                templeteConfig.setCreatorId((Long)objects[7]);
                if(objects.length==9 && objects[8] != null && !"".equals(objects[8].toString())){
                	templeteConfig.setType((Integer)objects[8]);
                }
                templeteConfig.setCategoryId((Long)objects[9]);
                templeteConfig.setAccountId((Long)objects[10]);
                templeteConfig.setIsSystem(String.valueOf(objects[12]));
                myTempleteList.add(templeteConfig);
            }
        }

        return myTempleteList;
    }

    public void clearTempleteConfig(Long memberId) {
        String hql = "delete from " + TempleteConfig.class.getName() + " as c where (c.memberId=:memberId) and c.type!=-1";
        Map<String, Object> nameParameters = new HashMap<String, Object>();
        nameParameters.put("memberId", memberId);
        super.bulkUpdate(hql, nameParameters);
    }

    public void pushAvailabileTemplete4Member(Long memberId) {
		Integer[] categoryTypes = this.getTempleteTypeArray();
		List<Templete> allTempletes = templeteManager.getSystemTempletesByMemberId(memberId, null, categoryTypes);
		List<TempleteConfig> myTempletes = getMyTempletes(memberId,null,null,-1,false,true);
		//推送所有系统模板
		if(allTempletes != null && !allTempletes.isEmpty()){
			int index = 1;
			List<TempleteConfig> templeteConfigList = new ArrayList<TempleteConfig>();
			for (Templete templete : allTempletes) {
				if (hasExistsTemplete(templete, myTempletes)) {
					continue;
				}
				TempleteConfig templeteConfig = new TempleteConfig();
				templeteConfig.setIdIfNew();
				templeteConfig.setMemberId(memberId);
				templeteConfig.setType(templete.getCategoryType());
				templeteConfig.setTempleteId(templete.getId());
				templeteConfig.setSort(index++);
				templeteConfigList.add(templeteConfig);
			}
			super.saveAll(templeteConfigList);
		}
	}

    private boolean hasExistsTemplete(Templete source,List<TempleteConfig> myTempletes){
		if(myTempletes == null || myTempletes.isEmpty()){
			return false;
		}
		for(TempleteConfig config : myTempletes){
			if(source.getType().equals(config.getTempleteType()) && source.getId().equals(config.getTempleteId())){
				return true;
			}
		}
		return false;
	}

    /*
     * 清理-我已配置但目前没有权限访问的系统模板
     * 给新注册人员添加模板配置
     */
    public void redressalTempleteConfig(Long memberId){
    	DetachedCriteria criteria = DetachedCriteria.forClass(TempleteConfig.class);
		criteria.add(Expression.eq("memberId", memberId));
		criteria.add(Expression.ne("type", -1));
		int count = super.getCountByCriteria(criteria);
		if(count > 0){
			//过滤 我已配置到首页但现在没有权限访问的 系统模板
			List<Long> domainIds = new ArrayList<Long>();
			try {
				domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
			}
			catch (BusinessException e) {
				log.error("", e);
			}
			Map<String, Object> nameParameters = new HashMap<String, Object>();
			StringBuffer sb = new StringBuffer();
			sb.append("select c.id from " + TempleteConfig.class.getName() + " c where c.memberId=:memberId and c.type!=-1 and c.templeteId not in(");
			sb.append("     select t.id from " + Templete.class.getName() + " t," + TempleteAuth.class.getName() + " auth where t.id=auth.objectId and (auth.authId in(:domainIds))");
			sb.append(")");
			nameParameters.put("memberId", memberId);
			nameParameters.put("domainIds", domainIds);
			List<Long> templeteIds = super.find(sb.toString(), -1, -1, nameParameters);
			if(templeteIds != null && !templeteIds.isEmpty()){
				nameParameters.clear();
				String deleteHQL = "delete from " + TempleteConfig.class.getName() + " c where c.id in(:templeteIds)";
				nameParameters.put("templeteIds", templeteIds);
				super.bulkUpdate(deleteHQL, nameParameters);
			}
		}
		else{
			//如果该用户没有任何配置信息，则视为新入职人员，将所有模板推送给他
			this.pushAvailabileTemplete4Member(memberId);
		}
    }

    /**
	 * 组织模型调整后，推送授权给新组织实体的模板到某人的个人首页
	 * @param memberId
	 * @param entityId
	 */
	public void pushNewOrgEntityTemplete4Member(String orgType, Long memberId, Long entityId){
		Integer[] typeArray = this.getTempleteTypeArray();
		List<Templete> allTempletes = templeteManager.getSystemTempletesByOrgEntity(orgType, entityId, typeArray);
		Set<Long> templeteIds = getMemberTempleteConfigIds(getConfigTempletes(memberId,true)) ;
		//推送所有系统模板
		if(allTempletes != null && !allTempletes.isEmpty()){
			List<TempleteConfig> templeteConfigList = new ArrayList<TempleteConfig>();

			for (Templete templete : allTempletes) {
				if(templeteIds != null && templeteIds.contains(templete.getId())){
					continue ;
				}
			   TempleteConfig templeteConfig = new TempleteConfig();
	           templeteConfig.setIdIfNew();
	           templeteConfig.setMemberId(memberId);
	           templeteConfig.setType(templete.getCategoryType());
	           templeteConfig.setTempleteId(templete.getId());
	           templeteConfig.setSort(999);
	           templeteConfigList.add(templeteConfig);
			}
			super.saveAll(templeteConfigList);
		}
	}

	private Set<Long> getMemberTempleteConfigIds(List<TempleteConfig> list){
		Set<Long> templeteIds = new HashSet<Long>() ;
		if(list != null) {
			for(TempleteConfig templeteConfig : list){
				templeteIds.add(templeteConfig.getTempleteId()) ;
			}
		}
		return templeteIds ;
	}

	private Integer[] getTempleteTypeArray(){
		Integer[] typeArray = {
				TempleteCategory.TYPE.collaboration_templete.ordinal(),
				TempleteCategory.TYPE.form.ordinal(),
				TempleteCategory.TYPE.edoc.ordinal(),
				TempleteCategory.TYPE.edoc_rec.ordinal(),
				TempleteCategory.TYPE.edoc_send.ordinal(),
				TempleteCategory.TYPE.sginReport.ordinal()
			};
		return typeArray;
	}

	public List<TempleteConfig> getPersonTempleteConfig(Long userId,boolean isPage){
		DetachedCriteria criteria = DetachedCriteria.forClass(TempleteConfig.class);
		criteria.add(Expression.eq("type", "-1"));
		criteria.add(Expression.eq("memberId", userId));
		criteria.addOrder(Order.desc("sort"));
		if(isPage){
			return super.executeCriteria(criteria);
		}else{
			return super.executeCriteria(criteria, -1, -1);
		}
	}
	public List<TempleteConfig> getConfigTempletes(Long templeteId){
		DetachedCriteria criteria = DetachedCriteria.forClass(TempleteConfig.class);
		criteria.add(Expression.eq("type", 4));
		criteria.add(Expression.eq("templeteId", templeteId));
		return super.executeCriteria(criteria, -1, -1);
	}
}
