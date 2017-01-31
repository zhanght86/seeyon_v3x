/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.taglibs.functions.MainFunction;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.Constants.SectionType;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-9-4
 */
public class SectionRegisterManagerImpl implements ApplicationContextAware, SectionRegisterManager {
	private final Log log = LogFactory.getLog(SectionRegisterManagerImpl.class);
	
	private static final String DEFAULT_DERACTOR = "A8Standard";
	
	/**
	 * 可以在任何空间配置的栏目
	 */
	private List<String> noInSpaceType = new ArrayList<String>();
	
	/**
	 * 允许在窄栏目配置的
	 */
	private List<String> allowedNarrowSection = new ArrayList<String>();

	/**
	 * 空间类型下的栏目
	 */
	private EnumMap<SpaceType, List<String>> spaceTypeOfSections = new EnumMap<SpaceType, List<String>>(SpaceType.class);

	/**
	 * key - section.id; value-beanId
	 */
	private Map<String, String> sectionId2Bean = new HashMap<String, String>();
	
	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		Map<String, BaseSection> sectionBeans = applicationContext.getBeansOfType(BaseSection.class);
		Set<Map.Entry<String, BaseSection>> enities = sectionBeans.entrySet();
		
		for (Map.Entry<String, BaseSection> entry : enities) {
			BaseSection section = entry.getValue();
			
			if(!Strings.isWord(section.getId())){
				log.warn("栏目id[" + section.getId() + "]不合法，必须是由数字、字母、下划线构成");
				continue;
			}
			
			sectionId2Bean.put(section.getId(), entry.getKey());
			add(section);
		}
		
	}

	private void add(BaseSection section) {
		String sectionId = section.getId();
		String[] spaceTypes = section.getSpaceTypes();

		if(spaceTypes != null){
			for (String spaceType : spaceTypes) {
				SpaceType type = SpaceType.valueOf(spaceType);
				if(type == null){
					log.warn("栏目[" + sectionId + "]的spaceTypes属性\"" + spaceType + "\"配置不正确.");
					break;
				}
				
				List<String> t = spaceTypeOfSections.get(type);
				if (t == null) {
					t = new ArrayList<String>();
					spaceTypeOfSections.put(type, t);
				}

				t.add(sectionId);
			}
		}
		else{ //所有空间都可以配置
			noInSpaceType.add(sectionId);
		}
		
		if(section.isAllowedNarrow()){
			allowedNarrowSection.add(sectionId);
		}
	}
	
	public BaseSection getSection(String sectionId){
		String beanId = sectionId2Bean.get(sectionId);
		if(beanId != null){
			try {
				return (BaseSection) applicationContext.getBean(beanId);
			}
			catch (Throwable e) {
			}
		}
		
		return null;
	}
	
	public String getSectionBeanId(String sectionId){
		return sectionId2Bean.get(sectionId);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<String[]>> getSections(SpaceType spaceType, long memberId, long loginAccountId, boolean allowedNarrow, boolean showBanner) {
		List<String> list = new ArrayList<String>();
		List<String> ss = spaceTypeOfSections.get(spaceType);
		if (ss != null) {
			list.addAll(ss);
		}
		list.addAll(noInSpaceType);

		if (list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		List<BaseSection> baseSections = new ArrayList<BaseSection>();
		boolean isInternal = CurrentUser.get().isInternal();
		log.error("-----------------------栏目分割线---------------------");
		for (String sectionId : list) {
			log.error(spaceType+":类型栏目列表："+sectionId);
			if (allowedNarrow && !allowedNarrowSection.contains(sectionId)) {
				continue;
			}

			BaseSection section = getSection(sectionId);
			if (section != null && (isInternal || !section.isFilterOut())) {
				try {
					if (section.isAllowUsed(spaceType.name())) {
						baseSections.add(section);
					}
				} catch (Exception e) {
					log.warn("", e);
				}
			}
		}
		
		Collections.sort(baseSections);
		log.error("-----------------------栏目分割线---------------------");
		Map<String, List<String[]>> map = new LinkedHashMap<String, List<String[]>>();
		for (BaseSection section : baseSections) {
			String sectionType = section.getSectionType();
			
			if (Strings.isBlank(sectionType)) {
				sectionType = String.valueOf(SectionType.common);
			}

			String name = null;
			if (section.isRegistrer()) { // 注册栏目才取显示名称
				try {
					name = section.getName(Collections.EMPTY_MAP);
				} catch (Throwable e) {
					log.error("", e);
				}
			}

			List<String[]> t = map.get(sectionType);
			if (t == null) {
				t = new ArrayList<String[]>();
				map.put(sectionType, t);
			}
			log.error(section.getId()+":栏目属性：Name："+name+":SectionCategory:"+section.getSectionCategory());
			t.add(new String[] { section.getId(), name, section.getSectionCategory() });
		}
		log.error("-----------------------栏目分割线---------------------");
		return map;
	}
	
	public String getPortletTitle(String portletUniqueName, Locale locale){
		return null;
	}
	
	public String getPortletDecorator(String portletUniqueName){
		return DEFAULT_DERACTOR;
	}
	
	public String getSectionPreferences(String sectionId,String spaceType){
		StringBuilder sb = new StringBuilder();
		if(Strings.isNotBlank(sectionId)){
			sb.append(MainFunction.printPortletParams(sectionId, spaceType));
		}
		return sb.toString();
	}
	
	public String getFragmentProp(String entityId,String sectionId,String spaceType,Boolean containSection){
		StringBuilder sb = new StringBuilder();
		if(containSection){
			sb.append(getSectionPreferences(sectionId,spaceType));
		}
		Map<String,String> properties = MainFunction.showSectionProperties(Long.parseLong(entityId), spaceType);
		if(properties != null){
			sb.append("var property = new Properties();\n");
			for(String key : properties.keySet()){
				sb.append("property.put(\""+key+"\",\""+Functions.escapeJavascript(properties.get(key))+"\");\n");
			}
		}
		return sb.toString();
	}

	@Override
	public List<String[]> getSections(SpaceType spaceType, long memberId,
			long loginAccountId, boolean showBanner) {
		if(spaceType == null){
			return Collections.EMPTY_LIST;
		}
		spaceType = Constants.parseDefaultSpaceType(spaceType);
		List<String> list = new ArrayList<String>();
		List<String> ss = spaceTypeOfSections.get(spaceType);
		if(ss != null){
			list.addAll(ss);		
		}
		list.addAll(noInSpaceType);
		
		if(list.isEmpty()){
			return Collections.EMPTY_LIST;
		}
		
		List<BaseSection> baseSections = new ArrayList<BaseSection>();
		boolean isInternal = CurrentUser.get().isInternal();
		for (String sectionId : list) {
			BaseSection section = getSection(sectionId);
			if(section != null && (isInternal || !section.isFilterOut()) && (!"banner".equals(sectionId) || showBanner)){
				try {
					if(section.isAllowUsed(spaceType.name())){
						baseSections.add(section);
					}
				}
				catch (Exception e) {
					log.warn("", e);
				}
			}
		}
		
		Collections.sort(baseSections);
		
		List<String[]> result = new ArrayList<String[]>();
		for (BaseSection section : baseSections) {
			String name = null;
			if(section.isRegistrer()){ //注册栏目才取显示名称
				try {
					name = section.getName(Collections.EMPTY_MAP);
				}
				catch (Throwable e) {
					log.error("", e);
				}
			}
			result.add(new String[]{section.getId(), name});
		}
		
		return result;
	}
}
