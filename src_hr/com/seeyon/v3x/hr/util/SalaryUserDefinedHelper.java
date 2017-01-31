package com.seeyon.v3x.hr.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class SalaryUserDefinedHelper {
	
	/**
	 * 得到工资页面的信息项
	 */
	public static Map<Long, List<PageProperty>> getPageProperties(UserDefinedManager userDefinedManager, List<Page> pages) {
		Map<Long, List<PageProperty>> pageProperties = new LinkedHashMap<Long, List<PageProperty>>();
		if (CollectionUtils.isNotEmpty(pages)) {
			for (Page page : pages) {
				List<PageProperty> properties = userDefinedManager.getPropertyByPageId(page.getId());
				if (CollectionUtils.isNotEmpty(properties)) {
					pageProperties.put(page.getId(), properties);
				}
			}
		}
		return pageProperties;
	}

	/**
	 * 得到工资页面的信息项
	 */
	public static List<PageProperty> getPageProperties(UserDefinedManager userDefinedManager) {
		List<Page> pages = userDefinedManager.getPageByModelName("salary");
		List<PageProperty> properties = new ArrayList<PageProperty>();
		if (CollectionUtils.isNotEmpty(pages)) {
			for (Page page : pages) {
				List<PageProperty> pageProperties = userDefinedManager.getPropertyByPageId(page.getId());
				if (CollectionUtils.isNotEmpty(pageProperties)) {
					properties.addAll(pageProperties);
				}
			}
		}
		return properties;
	}

	/**
	 * 得到工资页面的信息项名称
	 */
	public static Map<Long, String> getPropertyTypes(HttpServletRequest request, UserDefinedManager userDefinedManager, Map<Long, List<PageProperty>> pageProperties) {
		List<PageProperty> allProperties = SalaryUserDefinedHelper.getAllProperties(pageProperties);
		return SalaryUserDefinedHelper.getPropertyTypes(request, userDefinedManager, allProperties);
	}
	
	/**
	 * 得到工资页面的信息项名称
	 */
	public static Map<Long, String> getPropertyTypes(HttpServletRequest request, UserDefinedManager userDefinedManager, List<PageProperty> properties) {
		Locale locale = LocaleContext.getLocale(request);
		Map<Long, String> propertyTypes = new HashMap<Long, String>();
		if (CollectionUtils.isNotEmpty(properties)) {
			List<Long> property_ids = FormBizConfigUtils.getIds(properties);
			List<PropertyLabel> propertyLabels = userDefinedManager.getPropertyLabelByPropertyIds(property_ids);
			for (PropertyLabel label : propertyLabels) {
				if (locale.equals(Locale.ENGLISH)) {
					if (label.getLanguage().equals("en")) {
						propertyTypes.put(label.getProperty_id(), label.getPropertyLabelValue());
					}
				} else {
					if (label.getLanguage().equals("zh_CN")) {
						propertyTypes.put(label.getProperty_id(), label.getPropertyLabelValue());
					}
				}
			}
		}
		return propertyTypes;
	}
	
	public static Repository getRepository(Long memberId, Long pagePropertyId, Long salaryId, List<Repository> allRepository) {
		if (allRepository == null) {
			return null;
		}
		
		for (Repository repository : allRepository) {
			if (repository.getOperation_id().equals(salaryId) && repository.getProperty_id().equals(pagePropertyId)) {
				return repository;
			}
		}
		
		return null;
	}
	
	/**
	 * 工资管理员得到工资列表中扩展信息项结果
	 */
	public static Map<Long, Map<Long, String>> getSalaryAdminRepositoryPropertyId(List<Long> salaryIds, UserDefinedManager userDefinedManager, List<Salary> salarys, Map<Long, List<PageProperty>> pageProperties) {
		List<PageProperty> allProperties = SalaryUserDefinedHelper.getAllProperties(pageProperties);
		List<Repository> allRepository = userDefinedManager.getSalaryAdminRepositoryPropertyId(salaryIds, FormBizConfigUtils.getIds(allProperties));
		return SalaryUserDefinedHelper.setRepositoryValue(salarys, allProperties, allRepository);
	}
	
	/**
	 * 个人得到工资列表中扩展信息项结果
	 */
	public static Map<Long, Map<Long, String>> getPropertyValues(Long userId, UserDefinedManager userDefinedManager, List<Salary> salarys, Map<Long, List<PageProperty>> pageProperties) {
		List<PageProperty> allProperties = SalaryUserDefinedHelper.getAllProperties(pageProperties);
		List<Repository> allRepository = userDefinedManager.getRepositoryByMemberIdAndPropertyIds(userId, FormBizConfigUtils.getIds(allProperties));
		return SalaryUserDefinedHelper.setRepositoryValue(salarys, allProperties, allRepository);
	}
	
	public static Map<Long, Map<Long, String>> setRepositoryValue(List<Salary> salarys, List<PageProperty> allProperties, List<Repository> allRepository) {
		Map<Long, Map<Long, String>> propertyValues = new HashMap<Long, Map<Long, String>>();

		for (Salary salary : salarys) {
			if (salary == null) {
				continue;
			}
			
			Map<Long, String> salaryPropertyList = new HashMap<Long, String>();
			for (PageProperty property : allProperties) {
				Repository repository = getRepository(salary.getStaffId(), property.getId(), salary.getId(), allRepository);
				String value = "";
				if (repository != null) {
					if (property.getType() == PagePropertyConstant.Page_Property_Integer) {
						value = String.valueOf(repository.getF1());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Float) {
						value = String.valueOf(repository.getF2());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Date) {
						value = String.valueOf(repository.getF3());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Varchar) {
						value = String.valueOf(repository.getF4());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Text) {
						value = String.valueOf(repository.getF5());
					}
				}
				if (value == null || "null".equals(value)) {
					value = "";
				}
				salaryPropertyList.put(property.getId(), value);
			}
			propertyValues.put(salary.getId(), salaryPropertyList);
		}
		return propertyValues;
	}
	
	public static List<PageProperty> getAllProperties(Map<Long, List<PageProperty>> pageProperties) {
		List<PageProperty> allProperties = new ArrayList<PageProperty>();
		if (pageProperties != null) {
			for (List<PageProperty> properties : pageProperties.values()) {
				allProperties.addAll(properties);
			}
		}
		return allProperties;
	}
	
	/**
	 * 添加信息项数据
	 */
	public static void addRepository(int propertyType, String propertyValue, Repository repository) {
		boolean isNotBlank = Strings.isNotBlank(propertyValue);

		if (propertyType == PagePropertyConstant.Page_Property_Integer) {
			Long f1 = isNotBlank ? NumberUtils.toLong(propertyValue.replaceAll(",", "")) : 0L;
			repository.setF1(f1);
			repository.setF2(0D);
		} else if (propertyType == PagePropertyConstant.Page_Property_Float) {
			Double f2 = isNotBlank ? NumberUtils.toDouble(propertyValue.replaceAll(",", "")) : 0D;
			repository.setF1(0L);
			repository.setF2(f2);
		} else if (propertyType == PagePropertyConstant.Page_Property_Date) {
			Date f3 = isNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
			repository.setF1(0L);
			repository.setF2(0D);
			repository.setF3(f3);
		} else if (propertyType == PagePropertyConstant.Page_Property_Varchar) {
			String f4 = isNotBlank ? propertyValue : "";
			repository.setF1(0L);
			repository.setF2(0D);
			repository.setF4(f4);
		} else {
			String f5 = isNotBlank ? propertyValue : " ";
			repository.setF1(0L);
			repository.setF2(0D);
			repository.setF5(f5);
		}
	}
	
	/**
	 * 更新信息项数据
	 */
	public static void updateRepository(int propertyType, String propertyValue, Repository repository) {
		boolean fNotBlank = Strings.isNotBlank(propertyValue);
		if (propertyType == PagePropertyConstant.Page_Property_Integer) {
			Long f1 = fNotBlank ? NumberUtils.toLong(propertyValue) : 0L;
			repository.setF1(f1);
		} else if (propertyType == PagePropertyConstant.Page_Property_Float) {
			Double f2 = fNotBlank ? NumberUtils.toDouble(propertyValue) : 0D;
			repository.setF2(f2);
		} else if (propertyType == PagePropertyConstant.Page_Property_Date) {
			Date f3 = fNotBlank ? Datetimes.parse(propertyValue, "yyyy-MM-dd") : new Date();
			repository.setF3(f3);
		} else if (propertyType == PagePropertyConstant.Page_Property_Varchar) {
			String f4 = fNotBlank ? propertyValue : "";
			repository.setF4(f4);
		} else {
			String f5 = fNotBlank ? propertyValue : " ";
			repository.setF5(f5);
		}
	}

}