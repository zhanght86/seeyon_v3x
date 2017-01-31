package com.seeyon.v3x.formbizconfig.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.domain.V3xBizAuthority;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfig;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfigItem;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.domain.MenuProfile;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 业务生成器配置Dao
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */
public class V3xBizConfigDao extends BaseHibernateDao<V3xBizConfig> {
	
	public List<V3xBizConfig> getAllV3xBizConfig(){
		return super.getAll();
	}
	
	public List<V3xBizConfigItem> getAllV3xBizConfigItem(){
		return this.find("from V3xBizConfigItem ci order by ci.sortId asc", -1, -1, null);
	}
	
	public List<V3xBizAuthority> getAllV3xBizAuthority(){
		return this.find("from V3xBizAuthority", -1, -1, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xBizConfig> findAllByCondition(SearchModel model) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		Set<Long> creatorIdSet = Functions.getAllMembersId(V3xOrgEntity.ORGENT_TYPE_ACCOUNT,accountId);
		String from = "from " + V3xBizConfig.class.getName() + " c where 1=1 ";
		StringBuffer hqlStr = new StringBuffer("");
		if(SearchModel.SEARCH_BY_NAME.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			hqlStr.append(" and c.name like :name ");
			parameterMap.put("name", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		}else if(SearchModel.SEARCH_BY_CREATOR.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			from = "select c from " + V3xBizConfig.class.getName() + " c, " + V3xOrgMember.class.getName() + " m where ";
			hqlStr.append(" c.createUser=m.id and m.name like :creatorName ");
			parameterMap.put("creatorName", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		}else if(SearchModel.SEARCH_BY_CREATE_DATE.equals(model.getSearchType())) {
			if(Strings.isNotBlank(model.getSearchValue1())) {
				hqlStr.append(" and c.createDate >= :startDate ");
				parameterMap.put("startDate", Datetimes.getTodayFirstTime(model.getSearchValue1()));
			}
			if(Strings.isNotBlank(model.getSearchValue2())) {
				hqlStr.append(" and c.createDate <= :endDate ");
				parameterMap.put("endDate", Datetimes.getTodayLastTime(model.getSearchValue2()));
			}		
		}
		if(creatorIdSet.size()<=1000){
			hqlStr.append(" and c.createUser in(:creatorId) ");
			parameterMap.put("creatorId", creatorIdSet);
		}else{
			hqlStr.append(" and ( ");
			int k=0;
			Set<Long> newCreatorIdSet = null;
			for (Long creatorId : creatorIdSet) {
				if(k%1000==0){
					String key="creatorId"+k;
					newCreatorIdSet = new HashSet<Long>();
					if(k!=0)
						hqlStr.append(" or ");
					hqlStr.append(" c.createUser in(:"+key+") ");
					parameterMap.put(key, newCreatorIdSet);
				}
				newCreatorIdSet.add(creatorId);
				k++;
			}
			hqlStr.append(" ) ");
		}
		hqlStr.append(" order by c.createDate desc");
		return model.isPagination() ? this.find(from + hqlStr, parameterMap) : this.find(from + hqlStr, -1, -1, parameterMap);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xBizConfig> findAllByCondition(SearchModel model, List<Long> creatorIdList) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		String from = "from " + V3xBizConfig.class.getName() + " c where 1=1 ";
		StringBuffer hqlStr = new StringBuffer("");
		if(SearchModel.SEARCH_BY_NAME.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			hqlStr.append(" and c.name like :name ");
			parameterMap.put("name", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		}else if(SearchModel.SEARCH_BY_CREATOR.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			from = "select c from " + V3xBizConfig.class.getName() + " c, " + V3xOrgMember.class.getName() + " m where ";
			hqlStr.append(" c.createUser=m.id and m.name like :creatorName ");
			parameterMap.put("creatorName", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		}else if(SearchModel.SEARCH_BY_CREATE_DATE.equals(model.getSearchType())) {
			if(Strings.isNotBlank(model.getSearchValue1())) {
				hqlStr.append(" and c.createDate >= :startDate ");
				parameterMap.put("startDate", Datetimes.getTodayFirstTime(model.getSearchValue1()));
			}
			if(Strings.isNotBlank(model.getSearchValue2())) {
				hqlStr.append(" and c.createDate <= :endDate ");
				parameterMap.put("endDate", Datetimes.getTodayLastTime(model.getSearchValue2()));
			}		
		}
		if(creatorIdList != null && creatorIdList.size() != 0){
			hqlStr.append(" and c.createUser in(:creatorId) ");
			parameterMap.put("creatorId", creatorIdList);
		}
		hqlStr.append(" order by c.createDate desc");
		return model.isPagination() ? this.find(from + hqlStr, parameterMap) : this.find(from + hqlStr, -1, -1, parameterMap);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xBizConfigItem> findBizConfigItemByBizConfigId(Long bizConfigId) {
		return this.find("from V3xBizConfigItem as ci where ci.bizConfigId=? order by ci.sortId asc", -1, -1, null, bizConfigId);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xBizAuthority> findBizAuthorityByBizConfigId(Long bizConfigId) {
		return this.find("from V3xBizAuthority as au where au.bizConfigId=?", -1, -1, null, bizConfigId);
	}
	
	public V3xBizConfig findBizConfigByMenuId (Long menuId) {
		return (V3xBizConfig)this.findUnique("from V3xBizConfig as c where c.menuId=?", null, menuId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> findAccessMenuIdsByScopeIds(List<Long> entIdsList) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("entIdsList", entIdsList);
		return this.find("select distinct c.menuId from V3xBizConfig as c,V3xBizAuthority as a where c.id=a.bizConfigId and a.scopeId in (:entIdsList)", -1, -1, parameterMap);
	}
	
	/**
	 * 批量删除多条业务配置记录
	 */
	public void deleteBizConfig(List<Long> bizConfigIds) {
		String hql = "delete from " + V3xBizConfig.class.getName() + " fbf where fbf.id in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 批量删除多条业务配置明细记录
	 */
	public void deleteBizConfigItem(List<Long> bizConfigIds) {
		String hql = "delete from " + V3xBizConfigItem.class.getName() + " item where item.bizConfigId in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 批量删除多条业务配置授权记录
	 */
	public void deleteBizAuthority(List<Long> bizConfigIds) {
		String hql = "delete from " + V3xBizAuthority.class.getName() + " auth where auth.bizConfigId in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 批量删除多条业务配置菜单记录
	 */
	@SuppressWarnings("unchecked")
	public void deleteMenu4BizConfig(List<Long> bizConfigIds) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		List<Long> menuList = new ArrayList<Long>();
		List<Long> list = this.find("select c.menuId from V3xBizConfig as c where c.id in (:bizConfigIds)", -1, -1, parameterMap);
		if(list!=null) menuList.addAll(list);
		list = this.find("select ci.menuId from V3xBizConfigItem as ci where ci.bizConfigId in (:bizConfigIds)", -1, -1, parameterMap);
		if(list!=null) menuList.addAll(list);
		
		parameterMap = new HashMap<String, Object>();
		parameterMap.put("menuIds", menuList);
		String hql = "delete from " + Menu.class.getName() + " as m where m.id in (:menuIds)";
		this.bulkUpdate(hql, parameterMap);
		hql = "delete from " + MenuProfile.class.getName() + " as m where m.menuId in (:menuIds)";
		this.bulkUpdate(hql, parameterMap);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> findBizConfigsByName(String name, Long accountId, String bizConfigId) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		String hql = "select biz from V3xBizConfig biz, V3xOrgMember m where biz.createUser=m.id and biz.name=:name and m.orgAccountId=:accountId";
		parameterMap.put("name", name);
		parameterMap.put("accountId", accountId);
		if(Strings.isNotBlank(bizConfigId)){
			hql += " and biz.id<>:bizConfigId";
			parameterMap.put("bizConfigId", Long.parseLong(bizConfigId));
		}
		return this.find(hql, -1, -1, parameterMap);
	}
}