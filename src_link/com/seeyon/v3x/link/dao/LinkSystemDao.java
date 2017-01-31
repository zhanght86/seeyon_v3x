package com.seeyon.v3x.link.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.link.domain.LinkAcl;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkMember;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.util.Constants;

public class LinkSystemDao extends BaseHibernateDao<LinkSystem> {
	public List findLinkSystem(String name,long categoryId){
		String hsql="from LinkSystem as link where link.name=? and link.linkCategoryId=?";
		List list=super.find(hsql, name,categoryId);
		return list;
	}
	
	public int getMaxOrder(long categoryId){
		String hsql=" from LinkSystem as link where link.linkCategoryId=?";
		List<LinkSystem> list=super.find(hsql,categoryId );
		if(list == null || list.isEmpty()){
			return 0;
		}else{
			int number=0;
			for(int i=0;i<list.size();i++){
				LinkSystem link=list.get(i);
				if(number < link.getOrderNum()){
					number=link.getOrderNum();
				}
			}
			return number;
		}
	}

	public List<LinkSystem> getSystemIdByCategoryId(List<Long> categoryId){
		if(categoryId == null || categoryId.isEmpty()==true)return null;
		StringBuffer buffer=new StringBuffer();
		buffer.append("from LinkSystem as link where link.linkCategoryId in (:ids)");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", categoryId);		
		List<LinkSystem> list=super.find(buffer.toString(),-1,-1,map);
		return list;
	}
	
	public List<LinkSystem> getLinkSystems(Collection<Long> ids){
		String hql = "from LinkSystem where id in(:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return super.find(hql, -1, -1, map);
	}
	
	//获取某个关联系统类别下的所有关联系统（size限制获取个数）
	@SuppressWarnings("unchecked")
	public List<LinkSystem> getLinkSystemByIds(List<Long> list, final int size, long CategoryId, long userId){
		List<LinkSystem> ret = new ArrayList<LinkSystem>();
		if(list == null || list.isEmpty()==true) {
			return ret;
		}
		
		Map<String, Object> p = new HashMap<String, Object>();
		String hsql = "from LinkMember as lm where lm.linkSystemId in (:linkSystemId) and lm.memberid=:userId";
		p.put("linkSystemId", list);
		p.put("userId", userId);
		List<LinkMember> linkMembers = super.find(hsql, -1, -1, p);
		final String sql; 
		if(CollectionUtils.isNotEmpty(linkMembers)){
			sql = "SELECT ls.* FROM (select distinct * from v3x_link_system vls where vls.id in (:linkSystemId) and vls.link_category_id = :linkCategoryId) ls LEFT OUTER JOIN v3x_link_member lm ON ls.id = lm.link_system_id and lm.member_id = :userId order by lm.user_link_sort";
		} else {
			sql = "SELECT ls.* FROM (select distinct * from v3x_link_system vls where vls.id in (:linkSystemId) and vls.link_category_id = :linkCategoryId) ls LEFT OUTER JOIN v3x_link_member lm ON ls.id = lm.link_system_id and lm.member_id = :userId order by ls.order_num";
		}

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("linkSystemId", list);
		params.put("linkCategoryId", CategoryId);
		params.put("userId", userId);
		
		return (List<LinkSystem>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql).addEntity(LinkSystem.class).setFirstResult(0).setMaxResults(size);
				setParameters(query, params);
				return query.list();
			}
		}, true);
	}
	
	public List<LinkSystem> getLinkSystems(long categoryId){
		return super.find("from LinkSystem as link where link.linkCategoryId =? order by link.orderNum", categoryId);
	}
	
	public LinkSystem getLinkSystemByName(String name){
		return super.findUniqueBy("name", name);
	}
	
	/**
	 * 获取当前用户可以访问的、允许配置为空间导航的全部内部、外部及自定义关联项目，按照所属分类、系统本身排序号升序排列<br>
	 * @param domainIds  当前用户对应的各种组织模型实体ID集合
	 */
	public List<LinkSystem> getLinkSystemsAllowedAsSpace(List<Long> domainIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domainIds", domainIds);
		String hql = "from " + LinkSystem.class.getName() + " as l where l.allowedAsSpace=true and " +
					 "(" +
					 "	(l.id in " +
					 "		(select distinct link.id from " + LinkSystem.class.getName() + " as link, " + LinkAcl.class.getName() + 
					 " 			as acl where link.id=acl.linkSystemId and acl.userId in (:domainIds) and " +
					 "					     link.linkCategoryId!="+ Constants.LINK_CATEGORY_COMMON_ID + ") " +
					 "	) or " +
					 "	(l.linkCategoryId in " +
					 "		(select distinct category.id from " + LinkCategory.class.getName() + " as category , " + LinkAcl.class.getName() + 
					 " 			as acl where category.id=acl.linkCategoryId and acl.userId in (:domainIds) and " +
					 "						 category.id!=" + Constants.LINK_CATEGORY_COMMON_ID+ ") " +
					 "	) " +
					 ") order by l.linkCategoryId asc, l.orderNum asc";
		return this.find(hql, -1, -1, params);
	}
	
	/**
	 * 校验当前用户是否能够继续使用关联系统
	 * @param systemId			关联系统ID
	 * @param systemCategoryId	关联系统所属系统分类ID
	 * @param domainIds			当前用户对应的各种组织模型实体ID集合
	 * @return
	 */
	public boolean canUseTheSystem(Long systemId, Long systemCategoryId, List<Long> domainIds) {
		String hql = "select count(distinct acl.id) from " + LinkAcl.class.getName() + " as acl where " +
					 "acl.userId in (:domainIds) and (acl.linkSystemId=:systemId or acl.linkCategoryId=:systemCategoryId)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("systemId", systemId);
		params.put("systemCategoryId", systemCategoryId);
		params.put("domainIds", domainIds);
		return (Integer)super.findUnique(hql, params) > 0;
	}
	
	//更新个人关联系统排序（针对某一条关联系统若表v3x_link_member中无此记录则要做保存操作）
	@SuppressWarnings("unchecked")
	public void updateUserLinkSort(Long linkCategoryId, Long linkSystemId, Long userId, int i) {
		try{
			String hsql = "from LinkMember as lm where lm.linkSystemId=? and lm.memberid=?";
			List<LinkMember> list = super.find(hsql, linkSystemId, userId);
			if(list == null || list.size() == 0) {
				LinkMember linkMember = new LinkMember();
				linkMember.setIdIfNew();
				linkMember.setMemberid(userId);
				linkMember.setUserLinkSort(i);
				linkMember.setLinkSystemId(linkSystemId);
				super.save(linkMember);
			} else {
				String hql = "update LinkMember lm set lm.userLinkSort=? where lm.linkSystemId=? and lm.memberid=?";
				super.bulkUpdate(hql, null,new Object[]{i, linkSystemId, userId});
			}
		} catch(Exception e) {
			
		}
	}

	// 获取个人所有的关联系统
	@SuppressWarnings("unchecked")
	public List<LinkSystem> getAllSystem(List<Long> linkSystemId, List<Long> linkCategoryId, long userId) {
		if (CollectionUtils.isEmpty(linkSystemId)) {
			return new ArrayList<LinkSystem>(0);
		}
		
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("linkSystemId", linkSystemId);
		params.put("userId", userId);
		String hsql = "from LinkMember as lm where lm.linkSystemId in (:linkSystemId) and lm.memberid=:userId";
		List<LinkMember> linkMembers = super.find(hsql, -1, -1, params);
		
		final String sql; 
		if(CollectionUtils.isNotEmpty(linkMembers)){
			sql = "SELECT * FROM (select distinct * from v3x_link_system vls where vls.id in (:linkSystemId)) ls LEFT OUTER JOIN v3x_link_member lm ON ls.id = lm.link_system_id and lm.member_id = :userId order by lm.user_link_sort";
		} else {
			sql = "SELECT * FROM (select distinct * from v3x_link_system vls where vls.id in (:linkSystemId)) ls LEFT OUTER JOIN v3x_link_member lm ON ls.id = lm.link_system_id and lm.member_id = :userId order by ls.order_num";
		}
		
		return (List<LinkSystem>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql).addEntity(LinkSystem.class);
				setParameters(query, params);
				return query.list();
			}
		}, true);
	}
	
}
