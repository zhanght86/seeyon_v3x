package com.seeyon.v3x.hr.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyCategory;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;

public interface UserDefinedManager {
	public Page getPageById(Long page_id);

	public List getAllPropertyByPageId(Long page_id);

	public List getPropertyLabelsByPropertyId(Long property_id);

	public List<PropertyCategory> getCategorysByRemove(int remove);

	public List<PropertyCategory> getCategorysByRemove(int remove, boolean sysFlag);

	public void addCategory(PropertyCategory category);

	public List getAllCategory();

	public PropertyCategory getCategoryById(Long category_id);

	public void deleteCategory(List<Long> ids);

	public void updateCategory(PropertyCategory category);

	public void addProperties(PageProperties properties);

	public void updateProperty(PageProperties properties);

	public void addPageLabel(PageLabel pageLabel);

	public void addPropertyLabel(PropertyLabel propertyLabel);

	public List getAllLanguage();

	public void addPageProperty(PageProperty property);

	public void updatePageProperty(PageProperty property);

	public void deletePageProperty(List<Long> ids);

	public List getAllProperty();

	public List getAllPropertyLabel();

	public List<PropertyLabel> getPropertyLabelByPropertyId(Long property_id);

	public List<PropertyLabel> getPropertyLabelByPropertyIds(List<Long> property_ids);

	public PageProperty getPropertyById(Long property_id);

	public void updatePropertyLabel(PropertyLabel label);

	public List getAllPage();

	public List getPageLabelByPageId(Long page_id);

	public void addPage(Page page);

	public void deletePage(List<Long> ids);

	public void updatePage(Page page);

	public void updatePageLabel(PageLabel pageLabel);

	public List<PageProperty> getPropertyByPageId(Long page_id);

	public List getPageByPropertyId(Long property_id);

	public void deletePageProperties(Long page_id);

	public void deletePageLabel(Long page_id);

	public void deletePropertyLabel(Long property_id);

	public List<Page> getPageByModelName(String pageName);

	public List<Page> getPageByModelName(String pageName, boolean containRemove, boolean containDisplay);

	public List getPageByRemove(int remove);

	public List<PageProperty> getPropertyByAccount(Long accountId);

	public List<PageProperty> getPropertyByRemove(int remove);

	public List<PageProperty> getPropertyByRemove(int remove, boolean sysFlag);

	public List getPropertyByRemove(int remove, int pageNo, int pageSize) throws BusinessException;

	public List getPropertyByCategoryId(Long category_id);

	public List getPropertyByCategoryId(Long category_id, int remove);

	public void deleteCategoryBYId(Long category_id);

	public void deletePagePropertiesByPropertyId(Long property_id);

	public void deleteRespository(Long page_id);

	public void addRepository(Repository repository);

	public List getRepostoryByOperationId(Long operation_id);

	public List getRepostoryByPageId(Long page_id);

	public void updateRepository(Repository repository);

	public Repository getRepositoryById(Long id);
	
	public void deleteRepositoryByOperationId(Long id);

	public void deleteRepositoryByOperationId(List<Long> ids);

	public List getRepositoryByMemberIdAndPropertyId(Long member_id, Long property_id);

	public void deleteRepositoryByIds(List<Long> ids);

	public Map<Long, Repository> getRepositoryByMemberIdAndPropertyIdAndPageId(Long memberId, Long property_id, Long page_id);

	public PropertyLabel getPropertyLabelByName(String labelName);

	/**
	 * 通过人员的Id operation_id的id 信息项的id 查找到Repository
	 */
	public Repository getRepositoryByMemberIdAndPropertyId(Long member_id, Long property_id, Long operation_id);

	public List<Repository> getRepositoryByMemberIdAndPropertyIds(Long member_id, List<Long> property_id);

	/**
	 * 找到这些信息项下的所有的值
	 */
	public List<Repository> getRepositoryPropertyId(List<Long> property_ids);

	/**
	 * 找到这些信息项下的所有的值
	 */
	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> property_ids);

	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> salaryIds, List<Long> property_ids);

	/**
	 * 保存页签排序
	 */
	public void updatePageOrder(String[] pageIds) throws Exception;

	/**
	 * 获取未使用的信息项
	 */
	public List<PageProperty> findUnUsePropertyByCategoryId(final Long category_id, final int remove) throws Exception;

	public void addAllProperties(List<PageProperties> properties);

	public void initHrData(Long accountId);

	public void addAllRepository(List<Repository> repositoryList) throws Exception;

	public void updateAllRepository(List<Repository> repositoryList) throws Exception;

}