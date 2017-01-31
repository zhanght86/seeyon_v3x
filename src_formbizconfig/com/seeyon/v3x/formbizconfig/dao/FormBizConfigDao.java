package com.seeyon.v3x.formbizconfig.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigShareScope;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 表单业务配置Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigDao extends BaseHibernateDao<FormBizConfig> {
	/**
	 * 获取当前用户自己创建和他人共享过来的、全部或按指定条件查询的表单业务配置记录
	 * @param memberId   		   当前用户ID
	 * @param searchModel  		   搜索条件模型，传入时不能为空
	 * @param domainIds			   当前用户对应各种组织模型实体ID集合
	 */
	@SuppressWarnings("unchecked")
	public List<FormBizConfig> searchAllDao(Long memberId, SearchModel model, List<Long> domainIds) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		String from = "from " + FormBizConfig.class.getName() + " c where ";
		//用户可以查看自己创建或别人共享给自己的业务配置记录
		String authCheck = " (c.createUser=:userId or c.id in (select distinct f.formBizConfigId from " + 
								  FormBizConfigShareScope.class.getName() + " f where f.scopeId in (:domainIds))) ";
		parameterMap.put("userId", memberId);
		parameterMap.put("domainIds", domainIds);
		
		StringBuffer hqlStr = new StringBuffer("");
		hqlStr.append(authCheck);
		
		if(SearchModel.SEARCH_BY_CREATOR.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			from = "select c from " + FormBizConfig.class.getName() + " c, " + V3xOrgMember.class.getName() + " m where ";
			hqlStr.append(" and c.createUser=m.id and m.name like :creatorName ");
			parameterMap.put("creatorName", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		} 
		else if(SearchModel.SEARCH_BY_NAME.equals(model.getSearchType()) && Strings.isNotBlank(model.getSearchValue1())) {
			hqlStr.append(" and c.name like :name ");
			parameterMap.put("name", "%" + SQLWildcardUtil.escape(model.getSearchValue1().trim()) + "%");
		} 
		else if(SearchModel.SEARCH_BY_CREATE_DATE.equals(model.getSearchType())) {
			if(Strings.isNotBlank(model.getSearchValue1())) {
				hqlStr.append(" and c.createDate >= :startDate ");
				parameterMap.put("startDate", Datetimes.getTodayFirstTime(model.getSearchValue1()));
			}
			if(Strings.isNotBlank(model.getSearchValue2())) {
				hqlStr.append(" and c.createDate <= :endDate ");
				parameterMap.put("endDate", Datetimes.getTodayLastTime(model.getSearchValue2()));
			}		
		} 
		else if(SearchModel.SEARCH_BY_CONFIG_TYPE.equals(model.getSearchType())) {
			int bizConfigType = Integer.valueOf(model.getSearchValue1());
			// 如果按照栏目挂接或菜单挂接进行查询，则具备两种挂接方式的业务配置也应查询出来，未挂接和栏目挂接菜单挂接两种情况直接匹配
			if(bizConfigType != FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU && bizConfigType != FormBizConfigConstants.CONFIG_TYPE_NO) {
				hqlStr.append(" and (c.bizConfigType=:configType or c.bizConfigType=:columnAndMenu) "); 
				parameterMap.put("columnAndMenu", FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU);
			} 
			else {
				hqlStr.append(" and c.bizConfigType=:configType ");
			}
			parameterMap.put("configType", bizConfigType);
		}
		hqlStr.append(" order by c.createDate desc");
		
		return model.isPagination() ? this.find(from + hqlStr, parameterMap) : this.find(from + hqlStr, -1, -1, parameterMap);
	}
	
	/**
	 * 根据当前用户ID获取其创建、共享的具备栏目挂接的表单业务配置记录
	 */
	public List<FormBizConfig> findAll4SpaceConfig(Long memberId, List<Long> domainIds) {
		SearchModel model = new SearchModel(SearchModel.SEARCH_BY_CONFIG_TYPE, String.valueOf(FormBizConfigConstants.CONFIG_TYPE_COLUMN), null);
		model.setPagination(false);
		return this.searchAllDao(memberId, model, domainIds);
	}
	
	/**
	 * 批量删除多条表单业务配置记录
	 */
	public void delete(List<Long> bizConfigIds) {
		String hql = "delete from " + FormBizConfig.class.getName() + " fbf where fbf.id in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}

}
