package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.seeyon.v3x.space.Constants.SpaceType;

public interface SectionRegisterManager {

	/**
	 * 提供给栏目选择器的方法
	 * 
	 * @param spaceType
	 * @param memberId
	 * @param loginAccountId
	 * @return
	 */
	public Map<String, List<String[]>> getSections(SpaceType spaceType, long memberId, long loginAccountId, boolean isNarrow, boolean showBanner);
	/**
	 * 提供给栏目选择器的方法,不判断窄栏目
	 * 
	 * @param spaceType
	 * @param memberId
	 * @param loginAccountId
	 * @return
	 */
	public List<String[]> getSections(SpaceType spaceType, long memberId,
			long loginAccountId, boolean showBanner);
	
	/**
	 * 根据section-id得到section-bean
	 * 
	 * @param sectionId
	 * @return
	 */
	public BaseSection getSection(String sectionId);
	
	/**
	 * 根据section-id得到section-bean-id
	 * 
	 * @param sectionId
	 * @return
	 */
	public String getSectionBeanId(String sectionId);
	
	/**
	 * 得到Portlet的标题
	 * 
	 * @param portletUniqueName 如: seeyon::departmentGuestbook
	 * @param locale
	 * @return
	 */
	public String getPortletTitle(String portletUniqueName, Locale locale);
	
	/**
	 * 得到Portlet的修饰
	 * 
	 * @param portletUniqueName
	 * @return
	 */
	public String getPortletDecorator(String portletUniqueName);
	
	/**
	 * 得到某栏的可配参数列表,js字符串。前端需要用eval来转换
	 * @param sectionId
	 * @return String
	 */
	public String getSectionPreferences(String sectionId,String spaceType);
	
	/**
	 * 首页直接修改，加载Fragment配置栏目的值和Fragment配置的栏目的参数
	 * @param entityId Fragment.id
	 * @param sectionId 空间类型
	 * @param spaceType 栏目id
	 * @param containProp 是否包含栏目参数，不用多次加载
	 * @return
	 */
	public String getFragmentProp(String entityId,String sectionId,String spaceType,Boolean containProp);
}