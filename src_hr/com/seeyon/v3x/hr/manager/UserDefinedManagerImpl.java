package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.dao.LanguageDao;
import com.seeyon.v3x.hr.dao.PageDao;
import com.seeyon.v3x.hr.dao.PageLabelDao;
import com.seeyon.v3x.hr.dao.PagePropertiesDao;
import com.seeyon.v3x.hr.dao.PagePropertyDao;
import com.seeyon.v3x.hr.dao.PropertyCategoryDao;
import com.seeyon.v3x.hr.dao.PropertyLabelDao;
import com.seeyon.v3x.hr.dao.RepositoryDao;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyCategory;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class UserDefinedManagerImpl implements UserDefinedManager {
	
	private LanguageDao languageDao;
	private PageDao pageDao;
	private PageLabelDao pageLabelDao;
	private PagePropertyDao pagePropertyDao;
	private PropertyLabelDao propertyLabelDao;
	private RepositoryDao repositoryDao;
	private PropertyCategoryDao propertyCategoryDao;
	private PagePropertiesDao pagePropertiesDao;

	public PagePropertiesDao getPagePropertiesDao() {
		return pagePropertiesDao;
	}

	public void setPagePropertiesDao(PagePropertiesDao pagePropertiesDao) {
		this.pagePropertiesDao = pagePropertiesDao;
	}

	public PropertyCategoryDao getPropertyCategoryDao() {
		return propertyCategoryDao;
	}

	public void setPropertyCategoryDao(PropertyCategoryDao propertyCategoryDao) {
		this.propertyCategoryDao = propertyCategoryDao;
	}

	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public PageLabelDao getPageLabelDao() {
		return pageLabelDao;
	}

	public void setPageLabelDao(PageLabelDao pageLabelDao) {
		this.pageLabelDao = pageLabelDao;
	}

	public PagePropertyDao getPagePropertyDao() {
		return pagePropertyDao;
	}

	public void setPagePropertyDao(PagePropertyDao pagePropertyDao) {
		this.pagePropertyDao = pagePropertyDao;
	}

	public PropertyLabelDao getPropertyLabelDao() {
		return propertyLabelDao;
	}

	public void setPropertyLabelDao(PropertyLabelDao propertyLabelDao) {
		this.propertyLabelDao = propertyLabelDao;
	}

	public RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	public void setRepositoryDao(RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
	
	public List getPageLabelByPageId(Long page_id){
		return this.pageLabelDao.findPageLabelByPageId(page_id);
	}
	
	public List getAllLanguage(){
		return this.languageDao.findAllLanguage();
	}
	
	public Page getPageById(Long page_id){
		return this.pageDao.findPageById(page_id);
	}
	
	public PageProperty getPropertyById(Long property_id){
		return this.pagePropertyDao.findPropertyById(property_id);
	}
	
	public List getAllPropertyByPageId(Long page_id){
		return this.pagePropertyDao.findPropertyByPageId(page_id);
	}
	
	public List getPropertyLabelsByPropertyId(Long property_id){
		return this.propertyLabelDao.findPropertyLabelByPropertyId(property_id);
	}
	
	public void addCategory(PropertyCategory category){
		category.setIdIfNew();
		this.propertyCategoryDao.save(category);
	}
	
	public List getAllCategory(){
		return this.propertyCategoryDao.findAllCategory();
	}
	
	public List<PropertyCategory> getCategorysByRemove(int remove) {
		return this.propertyCategoryDao.findCategorysByRemove(remove);
	}

	public List<PropertyCategory> getCategorysByRemove(int remove, boolean sysFlag) {
		return this.propertyCategoryDao.findCategorysByRemove(remove, sysFlag);
	}
	
	public PropertyCategory getCategoryById(Long category_id){
		return this.propertyCategoryDao.findCategoryById(category_id);
	}
	
	public void deleteCategory(List<Long> ids){
		this.propertyCategoryDao.delCategory(ids);
	}
	
	public void deletePage(List<Long> ids){
		this.pageDao.delPage(ids);
	}
	
	public void updateCategory(PropertyCategory category){
		this.propertyCategoryDao.update(category);
	}
	
	public void updatePage(Page page){
		this.pageDao.update(page);
	}
	
	public void updatePageLabel(PageLabel pageLabel){
		this.pageLabelDao.update(pageLabel);
	}
	
	public void addProperties(PageProperties properties){
		properties.setIdIfNew();
		this.pagePropertiesDao.save(properties);
	}
	public void updateProperty(PageProperties properties){
		this.pagePropertiesDao.update(properties);
	}
	
	public void addPageLabel(PageLabel pageLabel){
		pageLabel.setIdIfNew();
		this.pageLabelDao.save(pageLabel);
	}
	
	public void addPage(Page page){
		page.setIdIfNew();
		this.pageDao.save(page);
	}
	public void updatePropertyLabel(PropertyLabel label){
		this.propertyLabelDao.update(label);
	}
	
	public void addPropertyLabel(PropertyLabel propertyLabel){
		propertyLabel.setIdIfNew();
		this.propertyLabelDao.save(propertyLabel);
	}
	
	public void addPageProperty(PageProperty property){
		property.setIdIfNew();
		this.pagePropertyDao.save(property);
	}
	
	public void addRepository(Repository repository){
		repository.setIdIfNew();
		this.repositoryDao.save(repository);
	}
	
	public void updatePageProperty(PageProperty property){
		this.pagePropertyDao.update(property);
	}
	public void deletePageProperty(List<Long> ids){
		this.pagePropertyDao.delProperty(ids);		
	}
	
	public List getAllProperty(){
		return this.pagePropertyDao.findAllProperty();
	}
	
	public List<PropertyLabel> getAllPropertyLabel(){
		return this.propertyLabelDao.findAllPropertyLabel();
	}	
	
	public List<Page> getAllPage(){
		return this.pageDao.findAllPage();
	}
	
	public List<PropertyLabel> getPropertyLabelByPropertyId(Long property_id){
		return this.propertyLabelDao.findPropertyLabelByPropertyId(property_id);
	}
	
	public List<PropertyLabel> getPropertyLabelByPropertyIds(List<Long> property_ids){
		return this.propertyLabelDao.findPropertyLabelByPropertyIds(property_ids);
	}

	@SuppressWarnings("unchecked")
	public List<PageProperty> getPropertyByPageId(Long page_id) {
		String hql = "select p from " + PageProperty.class.getName() + " p, " + PageProperties.class.getName() + " ps" + 
			" where p.id=ps.pageProperty.id and ps.page.id=? and p.remove=0 order by ps.property_ordering";
		return this.pagePropertyDao.find(hql, page_id);
	}
	@SuppressWarnings("unchecked")
	public List<Page> getPageByPropertyId(Long property_id){
		String hql = "from Page where id in (select ps.page.id from PageProperties ps where ps.pageProperty.id=?)";
		return this.pageDao.find(hql, property_id);
	}
	
	public void deletePageProperties(Long page_id){
		this.pagePropertiesDao.delPageProperties(page_id);
	}
	
	public void deletePageLabel(Long page_id){
		this.pageLabelDao.delPageLabel(page_id);
	}
	public void deletePropertyLabel(Long property_id){
		this.propertyLabelDao.delPropertyLabel(property_id);
	}
	
	public List<Page> getPageByModelName(String modelName){
		return this.pageDao.findPageByModelName(modelName);
	}
	
	public List<Page> getPageByModelName(String modelName, boolean containRemove, boolean containDisplay){
		return this.pageDao.findPageByModelName(modelName, containRemove, containDisplay);
	}
	
	public List<Page> getPageByRemove(int remove){
		return this.pageDao.findPageByRemove(remove);
	}
	
	public List<PageProperty> getPropertyByAccount(Long accountId) {
		return this.pagePropertyDao.findPropertyByAccount(accountId);
	}
	
	public List<PageProperty> getPropertyByRemove(int remove) {
		return this.pagePropertyDao.findPropertyByRemove(remove);
	}

	public List<PageProperty> getPropertyByRemove(int remove, boolean sysFlag) {
		return this.pagePropertyDao.findPropertyByRemove(remove, sysFlag);
	}
	
	public List<PageProperty> getPropertyByRemove(int remove, int pageNo, int pageSize) throws BusinessException {
		return this.pagePropertyDao.findPropertyByRemove(remove, pageNo, pageSize);
	}
	
	public List<PageProperty> getPropertyByCategoryId(Long category_id){
		return this.pagePropertyDao.findPropertyByCategoryId(category_id);
	}
	
	public void deleteCategoryBYId(Long category_id){
		this.propertyCategoryDao.delCategoryById(category_id);
	}
	
	public List getPropertyByCategoryId(Long category_id, int remove){
		return this.pagePropertyDao.findPropertyByCategoryId(category_id, remove);
	}
	
	public void deletePagePropertiesByPropertyId(Long property_id){
		this.pagePropertiesDao.delPagePropertiesByPropertyId(property_id);
	}
	
	public void deleteRespository(Long page_id){
		this.repositoryDao.delRepository(page_id);
	}
	
	public List getRepostoryByOperationId(Long operation_id){
		return this.repositoryDao.findRepositoryByOperation_id(operation_id);
	}
	
	public List getRepostoryByPageId(Long page_id){
		return this.repositoryDao.findRepositoryByPage_id(page_id);
	}
	
	public void updateRepository(Repository repository){
		this.repositoryDao.update(repository);
	}
	
	public Repository getRepositoryById(Long id){
		return this.repositoryDao.findById(id);
	}
	
	public void deleteRepositoryByOperationId(Long id){
		this.repositoryDao.delRepositoryByOperationId(id);
	}
	
	public void deleteRepositoryByOperationId(List<Long> ids){
		this.repositoryDao.delRepositoryByOperationId(ids);
	}
	
	public List getRepositoryByMemberIdAndPropertyId(Long member_id, Long property_id){
		return this.repositoryDao.findRepositoryByMemberIdAndPropertyId(member_id, property_id);
	}
	
	public void deleteRepositoryByIds(List<Long> ids){
		this.repositoryDao.delRepositoryByIds(ids);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Long, Repository> getRepositoryByMemberIdAndPropertyIdAndPageId(Long memberId, Long property_id, Long page_id) {
		List<Repository> repositorys = this.repositoryDao.findRepositoryByMemberIdAndPropertyIdAndpageId(memberId, property_id, page_id);
		Map<Long, Repository> map = new LinkedHashMap<Long, Repository>();
		if (CollectionUtils.isNotEmpty(repositorys)) {
			for (Repository repository : repositorys) {
				map.put(repository.getOperation_id(), repository);
			}
		}
		return map;
	}
	
	public PropertyLabel getPropertyLabelByName(String labelName){
		return this.propertyLabelDao.findPropertyLabelByName(labelName);
	}
	public Repository getRepositoryByMemberIdAndPropertyId(Long member_id, Long property_id,Long operation_id){
		List<Repository> list = getRepositoryByMemberIdAndPropertyId(member_id ,property_id );
		if(list == null){
			return null ;
		}
		for(Repository repository : list){
			if(repository.getOperation_id().equals(operation_id)){
				return repository ;
			}
		}
		return null ;
	}
	
	public List<Repository> getRepositoryByMemberIdAndPropertyIds(Long member_id, List<Long> property_id) {
		if(property_id == null || property_id.isEmpty()){
			return  new ArrayList<Repository>(0) ;
		}
		//TODO 分页支持，in :ids
		String hql =" from Repository repository where repository.memberId=:memberId and repository.property_id in(:property_id)" ;
		Map<String,Object> nameMap = new HashMap<String,Object>() ;
		nameMap.put("memberId", member_id) ;
		nameMap.put("property_id", property_id) ;
		List<Repository> list = repositoryDao.find(hql, -1, -1, nameMap);
		if(list == null){
			return new ArrayList<Repository>(0) ;
		}
		return list ;
	}
	
	public List<Repository> getRepositoryPropertyId(List<Long> property_ids){
		if(property_ids == null){
			return new ArrayList<Repository>(0);
		}
		return repositoryDao.getRepositoryPropertyId(property_ids) ;
	}
	
	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> property_ids){
		if(property_ids == null){
			return new ArrayList<Repository>(0);
		}
		Long accountId = CurrentUser.get().getLoginAccount();
		return repositoryDao.getSalaryAdminRepositoryPropertyId(property_ids,accountId) ;
	}
	
	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> salaryIds, List<Long> property_ids) {
		if(CollectionUtils.isEmpty(property_ids) || CollectionUtils.isEmpty(salaryIds)) {
			return new ArrayList<Repository>(0);
		}
		
		return repositoryDao.getSalaryAdminRepositoryPropertyId(salaryIds, property_ids, CurrentUser.get().getLoginAccount());
	}
	
	public void updatePageOrder(String[] pageIds) throws Exception {
		if (pageIds == null) {
			return;
		}
		
		int i = 1;
		List<Page> pages = new ArrayList<Page>();
		for (String pageId : pageIds) {
			Page page = this.pageDao.getById(NumberUtils.toLong(pageId));
			page.setSort(i++);
			pages.add(page);
		}
		this.pageDao.updatePatchAll(pages);
	}
	
	public List<PageProperty> findUnUsePropertyByCategoryId(final Long category_id, final int remove) throws Exception {
		return this.pagePropertyDao.findUnUsePropertyByCategoryId(category_id, remove);
	}
	
	public void addAllProperties(List<PageProperties> properties) {
		this.pagePropertiesDao.savePatchAll(properties);
	}
	
	@SuppressWarnings("unchecked")
	public void initHrData(Long accountId) {
		Map<Long, PageProperty> propertysMap = new HashMap<Long, PageProperty>();
		Map<Long, Page> pagesMap = new HashMap<Long, Page>();
		
		List<PropertyCategory> categorys = this.propertyCategoryDao.findCategoryByAccount(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if (CollectionUtils.isNotEmpty(categorys)) {
			Map<Long, List<PropertyLabel>> propertyLabelMaps = new HashMap<Long, List<PropertyLabel>>();
			for (PropertyCategory category : categorys) {
				Long categoryId = this.copyCategory(category, accountId);
				
				List<PageProperty> propertys = this.pagePropertyDao.findPropertyByCategoryId(category.getId(), PagePropertyConstant.Page_Remove_No);
				List<Long> propertyIds = FormBizConfigUtils.getIds(propertys);
				List<PropertyLabel> propertyLabels = this.propertyLabelDao.findPropertyLabelByPropertyIds(propertyIds);
				if (CollectionUtils.isNotEmpty(propertyLabels)) {
					for (PropertyLabel label : propertyLabels) {
						Long propertyId = label.getProperty_id();
						if (propertyLabelMaps.get(propertyId) == null) {
							List<PropertyLabel> temp1 = new ArrayList<PropertyLabel>();
							temp1.add(label);
							propertyLabelMaps.put(propertyId, temp1);
						} else {
							propertyLabelMaps.get(propertyId).add(label);
						}
					}
				}
				propertysMap.putAll(this.copyPropertys(propertys, propertyLabelMaps, categoryId, accountId));
			}
		}
		
		List<Page> pages = this.pageDao.findPageByAccount(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		List<Long> pageIds = FormBizConfigUtils.getIds(pages);
		List<PageLabel> pageLabels = this.pageLabelDao.findPageLabelByPageIds(pageIds);
		Map<Long, List<PageLabel>> pageLabelMaps = new HashMap<Long, List<PageLabel>>();
		if (CollectionUtils.isNotEmpty(pageLabels)) {
			for (PageLabel label : pageLabels) {
				Long pageId = label.getPage_id();
				if (pageLabelMaps.get(pageId) == null) {
					List<PageLabel> temp2 = new ArrayList<PageLabel>();
					temp2.add(label);
					pageLabelMaps.put(pageId, temp2);
				} else {
					pageLabelMaps.get(pageId).add(label);
				}
			}
		}
		pagesMap = this.copyPages(pages, pageLabelMaps, accountId);
		
		List<PageProperties> savePageProperties = new ArrayList<PageProperties>();
		List<PageProperties> pageProperties = this.pagePropertiesDao.findPagePropertiesByPages(pageIds);
		if (CollectionUtils.isNotEmpty(pageProperties)) {
			for (PageProperties pps : pageProperties) {
				PageProperties newPageProperties = new PageProperties();
				newPageProperties.setIdIfNew();
				newPageProperties.setPage(pagesMap.get(pps.getPage().getId()));
				newPageProperties.setPageProperty(propertysMap.get(pps.getPageProperty().getId()));
				newPageProperties.setProperty_ordering(pps.getProperty_ordering());
				savePageProperties.add(newPageProperties);
			}
		}
		this.pagePropertiesDao.savePatchAll(savePageProperties);
	}
	
	private Long copyCategory(PropertyCategory category, Long accountId) {
		PropertyCategory propertyCategory = new PropertyCategory();
		propertyCategory.setIdIfNew();
		propertyCategory.setName(category.getName());
		propertyCategory.setMemo(category.getMemo());
		propertyCategory.setAccountId(accountId);
		propertyCategory.setRemove(category.getRemove());
		propertyCategory.setSysFlag(category.isSysFlag());
		this.propertyCategoryDao.save(propertyCategory);
		return propertyCategory.getId();
	}

	private Map<Long, PageProperty> copyPropertys(List<PageProperty> propertys, Map<Long, List<PropertyLabel>> propertyLabelMaps, Long categoryId, Long accountId) {
		Map<Long, PageProperty> propertysMap = new HashMap<Long, PageProperty>();
		if (CollectionUtils.isNotEmpty(propertys)) {
			List<PageProperty> savePagePropertys = new ArrayList<PageProperty>();
			List<PropertyLabel> savePropertyLabels = new ArrayList<PropertyLabel>();
			
			for (PageProperty property : propertys) {
				PageProperty pageProperty = new PageProperty();
				pageProperty.setIdIfNew();
				pageProperty.setName(property.getName());
				pageProperty.setType(property.getType());
				pageProperty.setLength(property.getLength());
				pageProperty.setOrdering(property.getOrdering());
				pageProperty.setDefaultValue(property.getDefaultValue());
				pageProperty.setNot_null(property.getNot_null());
				pageProperty.setRemove(property.getRemove());
				pageProperty.setCategory_id(categoryId);
				pageProperty.setAccountId(accountId);
				pageProperty.setSysFlag(property.isSysFlag());
				savePagePropertys.add(pageProperty);
				propertysMap.put(property.getId(), pageProperty);

				List<PropertyLabel> propertyLabels = propertyLabelMaps.get(property.getId());
				for (PropertyLabel label : propertyLabels) {
					PropertyLabel propertyLabel = new PropertyLabel();
					propertyLabel.setIdIfNew();
					propertyLabel.setLanguage(label.getLanguage());
					propertyLabel.setPropertyLabelValue(label.getPropertyLabelValue());
					propertyLabel.setProperty_id(pageProperty.getId());
					savePropertyLabels.add(propertyLabel);
				}
			}
			
			this.pagePropertyDao.savePatchAll(savePagePropertys);
			this.propertyLabelDao.savePatchAll(savePropertyLabels);
		}
		return propertysMap;
	}
	
	private Map<Long, Page> copyPages(List<Page> pages, Map<Long, List<PageLabel>> pageLabelMaps, Long accountId) {
		Map<Long, Page> pagesMap = new HashMap<Long, Page>();
		if (CollectionUtils.isNotEmpty(pages)) {
			List<Page> savePages = new ArrayList<Page>();
			List<PageLabel> savePageLabels = new ArrayList<PageLabel>();
			
			for (Page page : pages) {
				Page newPage = new Page();
				newPage.setIdIfNew();
				newPage.setPageName(page.getPageName());
				newPage.setPageNo(page.getPageNo());
				newPage.setPageDisplay(page.getPageDisplay());
				newPage.setModelName(page.getModelName());
				newPage.setMemo(page.getMemo());
				newPage.setRemove(page.getRemove());
				newPage.setAccountId(accountId);
				newPage.setRepair(page.getRepair());
				newPage.setSort(page.getSort());
				newPage.setSysFlag(page.isSysFlag());
				savePages.add(newPage);
				pagesMap.put(page.getId(), newPage);

				List<PageLabel> PageLabels = pageLabelMaps.get(page.getId());
				for (PageLabel label : PageLabels) {
					PageLabel pageLabel = new PageLabel();
					pageLabel.setIdIfNew();
					pageLabel.setLanguage(label.getLanguage());
					pageLabel.setPageLabelValue(label.getPageLabelValue());
					pageLabel.setPage_id(newPage.getId());
					savePageLabels.add(pageLabel);
				}
			}
			
			this.pageDao.savePatchAll(savePages);
			this.pageLabelDao.savePatchAll(savePageLabels);
		}
		return pagesMap;
	}
	
	public void addAllRepository(List<Repository> repositoryList) throws Exception {
		this.repositoryDao.savePatchAll(repositoryList);
	}
	
	public void updateAllRepository(List<Repository> repositoryList) throws Exception {
		this.repositoryDao.updatePatchAll(repositoryList);
	}
	
}