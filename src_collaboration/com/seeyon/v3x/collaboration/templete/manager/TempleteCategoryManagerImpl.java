/**
 * 
 */
package com.seeyon.v3x.collaboration.templete.manager;

import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.ENTITY_NAME;
import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.PROP_createDate;
import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.PROP_name;
import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.PROP_orgAccountId;
import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.PROP_parentId;
import static com.seeyon.v3x.collaboration.templete.domain.TempleteCategory.PROP_sort;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-3-27
 */
public class TempleteCategoryManagerImpl extends
		BaseHibernateDao<TempleteCategory> implements TempleteCategoryManager {
	private static Log log = LogFactory
			.getLog(TempleteCategoryManagerImpl.class);
	private final CacheAccessable cacheFactory = CacheFactory.getInstance(TempleteCategoryManagerImpl.class);
	private TempleteAuthManager templeteAuthManager; 
	
	private TempleteManager templeteManager;

//	private CacheMap<Long, ArrayList<TempleteCategory>> allTempleteCategory = cacheFactory.createMap("allTempleteCategory");
	
	private CacheMap<Long,TempleteCategory> templeteCategorys;

	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public void setTempleteAuthManager(TempleteAuthManager templeteAuthManager) {
		this.templeteAuthManager = templeteAuthManager;
	}

	public synchronized void init() {
		if(templeteCategorys==null){
			templeteCategorys = cacheFactory.createLinkedMap("templeteCategorys");
		}
		templeteCategorys.setDataLoader(new AbstractMapDataLoader<Long, TempleteCategory>(templeteCategorys) {
			@Override
			protected Map<Long, TempleteCategory> loadLocal() {
//				allTempleteCategory = new HashMap<Long, List<TempleteCategory>>();
//				Map<Long, ArrayList<TempleteCategory>> allCategory = new HashMap<Long, ArrayList<TempleteCategory>>();
				String hql = "from " + ENTITY_NAME + " as e order by e." + PROP_sort + ",e." + PROP_createDate;

//				templeteCategorys = super.find(hql);
				final List<TempleteCategory> categories = find(hql);
				Map<Long,TempleteCategory> idCategoryMap = new HashMap<Long,TempleteCategory>();
				if (categories != null) {
					for (TempleteCategory category : categories) {
						java.util.Set<TempleteAuth> auths = category.getCategoryAuths();
						//TODO 这种写法主要是为了取出数据，不要随意删除哦，除非找到正道
						log.debug(auths.size());
//						for (TempleteAuth auth : auths) {
//							try {
//								V3xOrgEntity orgEntity = orgManager.getEntity(auth.getAuthType(), auth.getAuthId());
//								auth.setOrgEntity(orgEntity);
//							}
//							catch (BusinessException e) {
//								log.error("", e);
//							}
//						}
						category.setCategoryAuths(auths);
		/*				Long key = category.getOrgAccountId();
						// 忽略预置的数据
						if(key==null) continue;
						ArrayList<TempleteCategory> value = allCategory.get(key);

						if (value == null) {
							value = new ArrayList<TempleteCategory>();
						}
						value.add(category);

						allCategory.put(key, value);*/
						idCategoryMap.put(category.getId(), category);
					}
				}
				return idCategoryMap;
			}

			@Override
			protected TempleteCategory loadLocal(Long k) {
				return get(k);
			}
		});

		templeteCategorys.reload();
//		allTempleteCategory.replaceAll(allCategory);
	}
	/**
	 * 取得指定单位的模板分类列表。
	 * @param accountId 单位Id
	 * @return 属于指定单位的模板分类。
	 */
	private List<TempleteCategory> getCategorys(final Long accountId){
		final ArrayList<TempleteCategory> result = new ArrayList<TempleteCategory>();
		for (TempleteCategory cat : templeteCategorys.values()) {
			if(accountId.equals(cat.getOrgAccountId())){
				result.add(cat);
			}
		}
		return result;
	}

	public List<TempleteCategory> getCategorys(Long orgAccountId, int type) {
/*		if (allTempleteCategory == null) {
			init();
		}*/

		List<TempleteCategory> result = new ArrayList<TempleteCategory>();
		List<TempleteCategory> all = getCategorys(orgAccountId);
		
		if (all != null && !all.isEmpty()) {
			int tempType=0;
			for (TempleteCategory category : all) {
				tempType=category.getType();
				if (tempType == type || ((type==1) &&((tempType==2)||(tempType==3) || (tempType==5)))) {
					result.add(category);
				}
			}
		}

		return result;
	}
	public List<TempleteCategory> getCategorys(Long orgAccountId, List<Integer> types){
	    List<TempleteCategory> result = new ArrayList<TempleteCategory>();
        List<TempleteCategory> all = getCategorys(orgAccountId);
  
        if (all != null && !all.isEmpty()) {
            int tempType=0;
            for (TempleteCategory category : all) {
                tempType=category.getType();
                if (types.contains(tempType) || ((types.contains(1)) &&((tempType==2)||(tempType==3) || (tempType==5)))) {
                    result.add(category);
                }
            }
        }

        return result;
	}
	public TempleteCategorysWebModel getCategorys(Long accountId, String searchType, String categoryIdStr, List<Templete> tempList) {
		TempleteCategorysWebModel result = new TempleteCategorysWebModel();
		
		if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(searchType)) {
			if(Strings.isNotBlank(categoryIdStr)){
				TempleteCategory category = this.get(Long.parseLong(categoryIdStr));
				List<TempleteCategory> parents = this.getParentCategorys(category);
				parents.add(category);

				result.setCategorys(parents);
			}
			result.setSearchByCategory(true);
		} 
		else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(searchType)) {
			Set<Long> categoryIds = new HashSet<Long>();
			if(CollectionUtils.isNotEmpty(tempList)) {
				boolean showOtherAccountCategory = false;
				for(Templete templete : tempList) {
					if(!templete.getOrgAccountId().equals(accountId)) 
						showOtherAccountCategory = true;
					
					categoryIds.add(templete.getCategoryId());
				}
				List<TempleteCategory> categorys = this.getCategorys(accountId, categoryIds);
				result.setCategorys(categorys);
				
				result.setShowOtherAccountCategory(showOtherAccountCategory);
			}			
		} 
		else {
			List<TempleteCategory> formAndColCategories = this.getFormAndColCategories(accountId);
			if(formAndColCategories != null){
				Collections.sort(formAndColCategories);
				result.setCategorys(formAndColCategories);
			}
			result.setShowOtherAccountCategory(true);
		}
		return result;
	}
	
	/**
	 * 获取当前登录单位下的全部表单、协同模板分类
	 */
	private List<TempleteCategory> getFormAndColCategories(Long accountId) {
		List<TempleteCategory> categories_coll= this.getCategorys(accountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		List<TempleteCategory> categories_form = this.getCategorys(accountId, TempleteCategory.TYPE.form.ordinal());
		return FormBizConfigUtils.getSumCollection(categories_coll, categories_form);
	}
	
	/**
	 * 解析出表单模板对应的所属应用类型及其父类型<br>
	 * 如果是某个应用类型下的子类型，则最终在前端展现时，父类型也需要加入以便树状展现<br>
	 * @param accountId   单位ID
	 * @param categoryIds 表单模板所属应用分类ID集合
	 */
	private List<TempleteCategory> getCategorys(Long accountId, Set<Long> categoryIds) {
		List<TempleteCategory> formAndColCategories = this.getFormAndColCategories(accountId);
		
		List<TempleteCategory> result = new UniqueList<TempleteCategory>();
		if(CollectionUtils.isNotEmpty(formAndColCategories)) {
			for(TempleteCategory category : formAndColCategories) {
				if(categoryIds.contains(category.getId())) {
					result.add(category);
					if(category.getParentId() != 4l && category.getParentId() != 0l) {
						result.addAll(this.getParentCategorys(category));
					}
				}
			}
		}
		
		Collections.sort(result);
		return result;
	}
	
	/**
	 * 获取某一表单模板应用分类的父级分类，追溯到根节点下为止
	 */
	private List<TempleteCategory> getParentCategorys(TempleteCategory category) {
		List<TempleteCategory> result = new UniqueList<TempleteCategory>();
		
		TempleteCategory parentCategory = this.get(category.getParentId());
		if(category.getParentId() != 4l && category.getParentId() != 0l) {
			result.add(parentCategory);
			result.addAll(this.getParentCategorys(parentCategory));
		}
		
		return result;
	}
	
	public TempleteCategory get(Long id){		
		return templeteCategorys.get(id);
		/*for (TempleteCategory templeteCategory : templeteCategorys.values()) {
			if(templeteCategory.getId().equals(id)){				
				return templeteCategory;
			}
		}*/

		//return null;
	}

	public TempleteCategory getByAccountAndId(Long orgAccountId,Long id){
		List<TempleteCategory> result = getCategorys(orgAccountId);
		for (TempleteCategory c : result) {
			if(c.getId() == id){
				return c;
			}
		}
		return null;
	}
	
	public void save(TempleteCategory templeteCategory) {
		super.save(templeteCategory);
		init();
	}

	public void update(TempleteCategory templeteCategory) {
		this.templeteAuthManager.delete(templeteCategory.getId());
		super.update(templeteCategory);
		
		init();
	}
	
	public void deleteCategory(long templeteCategoryId) throws BusinessException{
		List<TempleteCategory> tc = getChildrenCategory(templeteCategoryId);
		if(!tc.isEmpty()){
			throw new BusinessException();
		}
		
		long orgAccountId = CurrentUser.get().getLoginAccount();
		
		int c = this.templeteManager.countAllSystemTempletes(templeteCategoryId, orgAccountId);
		if(c > 0){
			throw new BusinessException();
		}
		
		try {
			super.delete(templeteCategoryId);
			this.templeteAuthManager.delete(templeteCategoryId);
		}
		catch (Exception e) {
			throw new BusinessException(e);
		}

		init();
	}
	
	private List<TempleteCategory> getChildrenCategory(Long parentId){
		List<TempleteCategory> result = new ArrayList<TempleteCategory>();
		
		for (TempleteCategory o : templeteCategorys.values()) {
			if(parentId.equals(o.getParentId())){				
				result.add(o);
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> checkName(Long parentId, String name) {
		User user = CurrentUser.get();
		DetachedCriteria criterca = DetachedCriteria.forClass(TempleteCategory.class);
		
		criterca.setProjection(Projections.id());
		
		criterca.add(Expression.eq(PROP_name, name));
		criterca.add(Expression.eq(PROP_orgAccountId, user.getLoginAccount()));
		
		if(parentId == null){ //根下面
			criterca.add(Expression.isNull(PROP_parentId));
		}
		else{
			if(parentId == 0l || parentId ==4l){
				criterca.add(Expression.or(Expression.eq(PROP_parentId, 0l), Expression.eq(PROP_parentId, 4l)));
			}else{
				criterca.add(Expression.eq(PROP_parentId, parentId));
			}
		}
		
		List<Long> list = super.executeCriteria(criterca, -1, -1);
		
		return list;
	}

	@SuppressWarnings("unchecked")
	public Long checkName(String name) {
		User user = CurrentUser.get();
		DetachedCriteria criterca = DetachedCriteria.forClass(TempleteCategory.class);
		
		criterca.setProjection(Projections.id());
		
		criterca.add(Expression.eq(PROP_name, name));
		criterca.add(Expression.eq(PROP_orgAccountId, user.getLoginAccount()));

		List<Long> list  = super.executeCriteria(criterca, 0, 1);
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	public boolean isTempleteManager(Long memberId, Long accountId){
/*		if (allTempleteCategory == null) {
			init();
		}*/
		for (TempleteCategory templeteCategory : templeteCategorys.values()) {
			if(templeteCategory.isCanManager(memberId, accountId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断模板分类是否存在
	 * @param templateCategoryId
	 * @return true 存在 false 不存在
	 */
	public boolean exist(Long templateCategoryId) {
		DetachedCriteria criterca = DetachedCriteria.forClass(TempleteCategory.class);
		criterca.setProjection(Projections.rowCount()).add(Expression.eq("id", templateCategoryId));
		Integer count = (Integer)super.executeUniqueCriteria(criterca);
		if(count == null)
			return false;
		return count>0;
	}
	
	/**
	 * 得到用户可以访问的所有模板分类（如果此模板分类下面有用户可以访问的模板，此模板分类用户就可以访问）
	 * @param memberId
	 * @return
	 */
	public List<TempleteCategory> getCategory4User(Long memberId,Integer... category){
		List<Long> domainIds = null;
		try {
			domainIds = orgManager.getUserDomainIDs(memberId, VIRTUAL_ACCOUNT_ID, ORGENT_TYPE_MEMBER, ORGENT_TYPE_DEPARTMENT, ORGENT_TYPE_ACCOUNT, ORGENT_TYPE_TEAM, ORGENT_TYPE_POST, ORGENT_TYPE_LEVEL);
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		String sql =" from "+TempleteConfig.class.getName()+" as config ,"+Templete.ENTITY_NAME+" as t ,"+ENTITY_NAME+" as c " +
				" where t.categoryId=c.id and t."+Templete.PROP_categoryType+" in(:categoryType) and t."+Templete.PROP_isSystem+"=true and "+Templete.PROP_state+"="+Templete.State.normal.ordinal() +
							" and config.templeteId = t.id and exists(" +
							" select a.objectId from "+TempleteAuth.ENTITY_NAME+" as a where a.objectId=t.id and a.authId in (:authId)  " +
									")" +
				"order by config.sort ";
		
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("authId", domainIds);
		parameter.put("categoryType", category);
		List<Object[]> templeteAndCategory = super.find(sql, -1, -1, parameter);
		return  categoryByName(templeteAndCategory);
	}

	private List<TempleteCategory> categoryByName(List<Object[]> templeteAndCategory) {
		Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
		TempleteCategory c;
		Templete t;
		for (Object[] objects : templeteAndCategory) {
			t = (Templete)objects[1];
			if(objects[1] != null){
				c = (TempleteCategory)objects[2];
				if(nameCategory.containsKey(c.getName())){
					c = nameCategory.get(c.getName());
				}else{
					nameCategory.put(c.getName(), c);
				}
				c.addTemplete(t);
			}
		}
		return new ArrayList<TempleteCategory>(nameCategory.values());
	}
	
}
