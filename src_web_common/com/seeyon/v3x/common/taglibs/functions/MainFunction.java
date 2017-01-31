package com.seeyon.v3x.common.taglibs.functions;

import static com.seeyon.v3x.common.i18n.ResourceBundleUtil.getString;
import static com.seeyon.v3x.common.taglibs.functions.Functions.escapeJavascript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpace;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpaceManager;
import com.seeyon.v3x.common.usermessage.UserMessageManagerImpl;
import com.seeyon.v3x.common.usermessage.pipeline.MessagePipeline;
import com.seeyon.v3x.common.usermessage.pipeline.MessagePipelineManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.SectionProperty;
import com.seeyon.v3x.main.section.SectionReference;
import com.seeyon.v3x.main.section.SectionReferenceValueRange;
import com.seeyon.v3x.main.section.SectionRegisterManager;
import com.seeyon.v3x.main.section.SectionReference.ValueType;
import com.seeyon.v3x.main.section.panel.BaseSectionPanel;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgRoleDefinition;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.MenuLocation;
import com.seeyon.v3x.plugin.PluginMainMenu;
import com.seeyon.v3x.plugin.PluginMenu;
import com.seeyon.v3x.plugin.PluginSystemInit;
import com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-11
 * 
 */
@SuppressWarnings("serial")
public class MainFunction {
	
	private static Log log = LogFactory.getLog(MainFunction.class);

	private static final String PagePathPrefix = "";

	private static OrgManager orgManager = null;

	private static OrgManager getOrgManager() {
		if (orgManager == null) {
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		}

		return orgManager;
	}

	private static SpaceManager spaceManager = null;
	private static SpaceManager getSpaceManager() {
		if (spaceManager == null) {
			spaceManager = (SpaceManager) ApplicationContextHolder.getBean("spaceManager");
		}

		return spaceManager;
	}

	private static ProjectManager projectManager;
	private static ProjectManager getProjectManager() {
		if (projectManager == null) {
			projectManager = (ProjectManager) ApplicationContextHolder.getBean("projectManager");
		}

		return projectManager;
	}

	private static OuterlinkManager outerlinkManager;
	private static OuterlinkManager getOuterlinkManager() {
		if (outerlinkManager == null) {
			outerlinkManager = (OuterlinkManager) ApplicationContextHolder.getBean("outerlinkManager");
		}

		return outerlinkManager;
	}

	private static MenuManager menuManager;
	private static MenuManager getMenuManager() {
		if (menuManager == null) {
			menuManager = (MenuManager) ApplicationContextHolder.getBean("menuManager");
		}

		return menuManager;
	}

	private static FormBizConfigManager formBizConfigManager;
	private static FormBizConfigManager getFormBizConfigManager() {
		if (formBizConfigManager == null) {
			formBizConfigManager = (FormBizConfigManager) ApplicationContextHolder.getBean("formBizConfigManager");
		}

		return formBizConfigManager;
	}
	
	private static PortletEntityPropertyManager portletEntityPropertyManager;
	private static void initPortletEntityPropertyManager() {
		if (portletEntityPropertyManager == null) {
			portletEntityPropertyManager = (PortletEntityPropertyManager) ApplicationContextHolder.getBean("portletEntityPropertyManager");
		}
	}

	private static SectionRegisterManager sectionRegisterManager;
	private static void initSectionRegisterManager() {
		if (sectionRegisterManager == null) {
			sectionRegisterManager = (SectionRegisterManager) ApplicationContextHolder.getBean("sectionRegisterManager");
		}
	}
	
	public static String showSpaceMenu(Map<Constants.SpaceType, List<SpaceModel>> spacePath, List<String[]> spaceSort) {
		return showSpaceMenu(spacePath, spaceSort, null);
	}

	/**
	 * 显示空间
	 * 
	 * @param spacePath 我能访问的所有空间的PagePath
	 * @param spaceSort 我设置的空间的排序 <code>{personal, department, corporation, /seeyon/custom/23459187189745.psml}</code>
	 * @return String[] ： 0-i18nkey 1-pagePath 2-类型
	 */
	public static String showSpaceMenu(Map<Constants.SpaceType, List<SpaceModel>> spacePath, List<String[]> spaceSort, String from) {
		if (spacePath == null || spacePath.isEmpty()) {
			return null;
		}

		User user = CurrentUser.get();
		StringBuilder sb = new StringBuilder();

		List<LinkSpace> linkSpaces = getOuterlinkManager().findLinkSpacesCanAccess(user.getId());
		List<LinkSystem> linkSystems = null;
		try {
			linkSystems = outerlinkManager.findAllRelatedSystemsAllowedAsSpace(user.getId());
		} catch (Exception e1) {
			log.error("获取人员"+user.getId()+"的可访问关联系统出错：",e1);
		}
		int i = 0;
		if (spaceSort != null && !spaceSort.isEmpty()) {
			for (String[] strings : spaceSort) {
				// 如果设置了不展示的空间
				if (strings.length == 4 && strings[3].equals("true")) {
					continue;
				}

				int type = Integer.parseInt(strings[0]);
				if (type == Constants.SpaceType.personal.ordinal()) {
					List<SpaceModel> personalPaths = spacePath.get(Constants.SpaceType.personal);
					if (personalPaths != null && !personalPaths.isEmpty()) {
						SpaceModel f = personalPaths.get(0);
						String p = PagePathPrefix + f.getSpacePath();
						sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'personal', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
						i++;
					}
				} else if (type == Constants.SpaceType.leader.ordinal()) {
					List<SpaceModel> leaderPaths = spacePath.get(Constants.SpaceType.leader);
					if (leaderPaths != null && !leaderPaths.isEmpty()) {
						SpaceModel f = leaderPaths.get(0);
						String p = PagePathPrefix + f.getSpacePath();
						sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'leader', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
						i++;
					}
				} else if (type == Constants.SpaceType.outer.ordinal()) {
					List<SpaceModel> leaderPaths = spacePath.get(Constants.SpaceType.outer);
					if (leaderPaths != null && !leaderPaths.isEmpty()) {
						SpaceModel f = leaderPaths.get(0);
						String p = PagePathPrefix + f.getSpacePath();
						sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'outer', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
						i++;
					}
				} else if (type == Constants.SpaceType.personal_custom.ordinal()) {
					List<SpaceModel> personalCustomPaths = spacePath.get(Constants.SpaceType.personal_custom);
					if (personalCustomPaths != null && !personalCustomPaths.isEmpty()) {
						for (SpaceModel f : personalCustomPaths) {
							if (String.valueOf(f.getId()).equals(strings[1]) || String.valueOf(f.getParentId()).equals(strings[1])) {
								String p = PagePathPrefix + f.getSpacePath();
								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'personal_custom', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.department.ordinal()) {
					List<SpaceModel> departPaths = spacePath.get(Constants.SpaceType.department);
					if (departPaths != null && !departPaths.isEmpty()) {
						Collections.sort(departPaths);
						for (SpaceModel model : departPaths) {
							if (String.valueOf(model.getEntityId()).equals(strings[1])) {
								String p = PagePathPrefix + model.getSpacePath() + "?depId=" + model.getEntityId();
								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(model.getSpaceName())).append("', '").append(p + "', 'department', '").append(escapeJavascript(model.getSlogan())).append("', false, 'workspace',''," + model.isAllowDefined() + "));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.corporation.ordinal()) {
					List<SpaceModel> corporationPaths = spacePath.get(Constants.SpaceType.corporation);
					if (corporationPaths != null && !corporationPaths.isEmpty()) {
						SpaceModel f = corporationPaths.get(0);
						String p = PagePathPrefix + f.getSpacePath();
						sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'corporation', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
						i++;
					}
				} else if (type == Constants.SpaceType.group.ordinal()) {
					if (user.isInternal()) {
						List<SpaceModel> groupPaths = spacePath.get(Constants.SpaceType.group);
						if (groupPaths != null && !groupPaths.isEmpty()) {
							SpaceModel f = groupPaths.get(0);
							String p = PagePathPrefix + f.getSpacePath();
							sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(f.getSpaceName())).append("', '").append(p).append("', 'group', '").append(escapeJavascript(f.getSlogan())).append("', false, 'workspace',''," + f.isAllowDefined() + "));\n");
							i++;
						}
					}
				} else if (type == Constants.SpaceType.custom.ordinal()) {
					List<SpaceModel> customPaths = spacePath.get(Constants.SpaceType.custom);
					if (customPaths != null && !customPaths.isEmpty()) {
						for (SpaceModel p : customPaths) {
							if (p.getId().equals(Long.parseLong(strings[1]))) {
								String path = PagePathPrefix + p.getSpacePath();
								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(p.getSpaceName())).append("', '").append(path + "', 'custom', '").append(escapeJavascript(p.getSlogan())).append("', false, 'workspace',''," + p.isAllowDefined() + "));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.public_custom.ordinal()) {
					List<SpaceModel> customPaths = spacePath.get(Constants.SpaceType.public_custom);
					if (customPaths != null && !customPaths.isEmpty()) {
						for (SpaceModel p : customPaths) {
							if (p.getId().equals(Long.parseLong(strings[1]))) {
								String path = PagePathPrefix + p.getSpacePath();
								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(p.getSpaceName())).append("', '").append(path + "', 'public_custom', '").append(escapeJavascript(p.getSlogan())).append("', false, 'workspace',''," + p.isAllowDefined() + "));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.public_custom_group.ordinal()) {
					List<SpaceModel> customPaths = spacePath.get(Constants.SpaceType.public_custom_group);
					if (customPaths != null && !customPaths.isEmpty()) {
						for (SpaceModel p : customPaths) {
							if (p.getId().equals(Long.parseLong(strings[1]))) {
								String path = PagePathPrefix + p.getSpacePath();
								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(p.getSpaceName())).append("', '").append(path + "', 'public_custom_group', '").append(escapeJavascript(p.getSlogan())).append("', false, 'workspace',''," + p.isAllowDefined() + "));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.thirdparty.ordinal()) {
					// 集成第三方系统
					List<ThirdpartySpace> thirdpartySpaces = ThirdpartySpaceManager.getInstance().getAccessSpaces(getOrgManager(), user.getId());
					if (thirdpartySpaces != null && !thirdpartySpaces.isEmpty()) {
						for (ThirdpartySpace s : thirdpartySpaces) {
							if (s.getId().equals(strings[1])) {
								// 被NC Portal集成隐藏ERP空间
								if (from != null && from.equals("nc") && s.getPluginId().equals("nc"))
									continue;

								sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(s.getNameOfResouceBundle(user.getLocale()))).append("', '/seeyon/thirdparty.do?method=show&id=").append(s.getId()).append("', 'thirdparty', '', ").append(s.isHiddenA8Menu()).append(", '").append(s.getOpenType()).append("'));\n");
								i++;
								break;
							}
						}
					}
				} else if (type == Constants.SpaceType.related_system.ordinal()) {
					// 关联系统
					if (CollectionUtils.isNotEmpty(linkSpaces)) {
						for (LinkSpace linkSpace : linkSpaces) {
							if (linkSpace.getId().equals(Long.valueOf(strings[1]))) {
								if (Strings.isNotBlank(linkSpace.getSpaceName())) {
									sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(linkSpace.getSpaceName())).append("', '/seeyon/linkManager.do?method=linkConnect&spaceFlag=spaceMenu&linkId=" + linkSpace.getId() + "', 'related_system', '', false, '" + (linkSpace.getOpenType() == LinkSystem.OPENTYPE_OPEN ? "open" : "workspace") + "'));\n");
									i++;
									break;
								}
							}
						}
					}
				} else if (type == Constants.SpaceType.related_project.ordinal()) {
					// 关联项目
					ProjectSummary project = null;
					try {
						Long projectId = NumberUtils.toLong(strings[1]);
						if (getProjectManager().canUserViewProject(projectId, user.getId()))
							project = getProjectManager().getProject(projectId);
					} catch (Exception e) {
						log.error("空间导航配置，获取关联项目出现异常：", e);
					}
					if (project == null || project.getProjectState() >= ProjectSummary.state_close) {
						MainFunction.getSpaceManager().deleteSpaceSortByTypeAndSpacePath(user.getId(), user.getLoginAccount(), strings[1], type);
					} else {
						sb.append("Constants_Panels.put('space_").append(i).append("', new TopPanel('space_").append(i).append("', '").append(escapeJavascript(project.getProjectName())).append("', '/seeyon/project.do?method=projectInfo&spaceFlag=spaceMenu&projectId=" + project.getId() + "', 'related_project', '', false, 'workspace'));\n");
						i++;
					}
				}
			}
		}

		return sb.toString();
	}
	
	private static String MessageLinkHTML = null;
	
	public static String showMessageLink(PageContext pageContext){
		if(MessageLinkHTML != null){
			return MessageLinkHTML;
		}
		
		Map<String, String> message_link_type = UserMessageManagerImpl.getMessageLinkType();
		if(message_link_type == null){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		Set<Map.Entry<String, String>> entries = message_link_type.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			String link = entry.getValue();
			if(link.indexOf(".do") > 0){
				try {
					String[] links = link.split("[?]");
					link = com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag.calculateURL(links[0], pageContext) + "?" + links[1];
					sb.append("messageLinkConstants.put(\"").append(entry.getKey()).append("\", \"").append(link).append("\");\n");
				}
				catch (Exception e) {
					log.warn("", e);
				}
			}
			else{
				sb.append("messageLinkConstants.put(\"").append(entry.getKey()).append("\", \"").append(link).append("\");\n");
			}
		}
		
		MessageLinkHTML = sb.toString();
		
		return MessageLinkHTML;
	}
	
	public static Map<String, String> showSectionProperties(Long fragmentId, String spaceType){
		initPortletEntityPropertyManager();
		initSectionRegisterManager();
		Map<String, String> props = portletEntityPropertyManager.getPropertys(fragmentId);
		Map<String,String> result = new HashMap<String,String>();
		for(String key : props.keySet()){
			result.put(key, (props.get(key)));
		}
		String sections = result.get("sections");
		if(sections != null){
			String[] _sections = sections.split(",");
			for (int i = 0; i < _sections.length; i++) {
				String id = _sections[i];
				BaseSection sectionMgr = sectionRegisterManager.getSection(id);
				if(sectionMgr != null){
					if(sectionMgr.isReadOnly(spaceType, "")){
						result.clear();
						result.put("sections", sections);
					}else{
						String columnName = result.get("columnsName:"+i);
						String singleBoardId = result.get(PropertyName.singleBoardId.name()+":"+i);
						if(Strings.isBlank(columnName)&&Strings.isNotBlank(singleBoardId)){
							String ownerId = result.get(PropertyName.ownerId.name()+":"+i);
							columnName = sectionMgr.doGetBaseName(String.valueOf(fragmentId), String.valueOf(i), spaceType, ownerId);
							if(Strings.isNotBlank(columnName)){
								result.put("columnsName:"+i,columnName);
							}
						}
					}
				}
				String singleBoardPanelId = result.get(PropertyName.singlePanel.name()+"_extends:"+i);
				if(Strings.isNotBlank(singleBoardPanelId)){
					String singleBoardPanelValue = result.get(PropertyName.singlePanel.name()+":"+i);
					BaseSectionPanel panel = (BaseSectionPanel)ApplicationContextHolder.getBean(singleBoardPanelId);
					if(panel != null){
						if(Strings.isNotBlank(singleBoardPanelValue)){
							String[] values = singleBoardPanelValue.split(",");
							StringBuilder jsonValue = new StringBuilder();
							Map<String,String> nameMap = panel.getName(getSinglePanelValue(singleBoardPanelValue));
							for(int j = 0 ;j<values.length;j++){
								String value = values[j];
								String name = nameMap.get(value);
								if(panel.isAllowUsed(value)){
									if(jsonValue.length()!=0){
										jsonValue.append(",");
									}
									jsonValue.append("{");
									if(name != null){
										jsonValue.append("value:\""+value+"\",subject:\""+Strings.escapeJavascript(name)+"\"");
									}else{
										jsonValue.append("value:\""+value+"\",isNormal:true");
									}
									jsonValue.append("}");
								}
							}
							result.put("singleBoardPanelValue:"+i, "["+jsonValue.toString()+"]");
						}
					}else{
						log.warn("不存在此页签:"+singleBoardPanelId);
					}
				}
			}
		}
		return result;
	}
	private static String getSinglePanelValue(String value){
		StringBuilder sb = new StringBuilder();
		if(Strings.isNotBlank(value)){
			String[] allValue = value.split(",");
			for(String v : allValue){
				if(NumberUtils.isNumber(v)){
					if(sb.length()!=0){
						sb.append(",");
					}
					sb.append(v);
				}
			}
		}
		return sb.toString();
	}
	public static Map<String, String> showSectionProperties(Fragment fragment, String spaceType){
		initPortletEntityPropertyManager();
		initSectionRegisterManager();
		Map<String, String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
		Map<String,String> result = new HashMap<String,String>();
		String sections = props.get("sections");
		if(sections != null){
			String[] _sections = sections.split(",");
			String sectionStr = "";
			for (int i = 0; i < _sections.length; i++) {
				String id = _sections[i];
				BaseSection sectionMgr = sectionRegisterManager.getSection(id);
				String singleBoardPanelId = props.get(PropertyName.singleBoardId.name()+":"+i);
				if(sectionMgr!=null&&sectionMgr.isAllowUserUsed(singleBoardPanelId)){
					sectionStr +=id;
					if(i!=_sections.length-1){
						sectionStr +=",";
					}
					for(String key : props.keySet()){
						if(key.equals("columnsName:"+i)||key.equals("singleBoardId:"+i)){
							result.put(key, (props.get(key)));
						}
					}
					result.put("ordinal:"+i, String.valueOf(i));
					result.put("entityId:"+i, String.valueOf(fragment.getId()));
				}
			}
			result.put("sections", sectionStr);
		}
		return result;
	}
	
	public static List<String> getSectionNames(Long entityId, String sections, String spaceType){
		List<String> sb = new ArrayList<String>(3);
		
		Locale locale = CurrentUser.get().getLocale();
		initSectionRegisterManager();
		initPortletEntityPropertyManager();
		
		Map<String,String> props = portletEntityPropertyManager.getPropertys(entityId);
		sections = props.get("sections");
		if(Strings.isNotBlank(sections)){
			String[] _sections = sections.split(",");

			for (int i = 0; i < _sections.length; i++) {
				String id = _sections[i];
				if(id.indexOf("::") > 0){
					sb.add(sectionRegisterManager.getPortletTitle(id, locale));
				}
				else{
					BaseSection sectionMgr = sectionRegisterManager.getSection(id);
					
					if(sectionMgr == null){
						//sb.add("");
						continue;
					}
					else{
						
						try {
							String singleBoardId = props.get("singleBoardId:"+i);
							if(!sectionMgr.isAllowUserUsed(singleBoardId)){ //不能访问该栏目
							//	sb.add("");
								continue;
							}
						}
						catch (Exception e) {
							//sb.add("");
							continue;
						}

						String label = sectionMgr.doGetBaseName(String.valueOf(entityId), String.valueOf(i), null, null);

						if (Strings.isBlank(label)) {
							label = sectionMgr.doGetName(String.valueOf(entityId), String.valueOf(i), null, null);
						}

						if(Strings.isBlank(label)){
							//sb.add("");
							continue;
						}
						else{
							sb.add(label);
						}
					}
				}
			}
		}
		
		return sb;
	}

	public static String printPortletParams(String sections,String spaceType){
		initSectionRegisterManager();
		StringBuilder out = new StringBuilder();
		User user = CurrentUser.get();
		if(Strings.isNotBlank(sections)){
			String[] _sections = sections.split(",");
			for(int i = 0; i < _sections.length; i++) {
				String sectionId = _sections[i];
				if(sectionId.indexOf("::") > 0){
                    out.append(" noParamSections[noParamSections.length] = '" + sectionId + "';\n");
					continue;
				}
				BaseSection sectionMgr = sectionRegisterManager.getSection(sectionId);
				if(sectionMgr != null){
					List<SectionProperty> properties = sectionMgr.getProperties();
					if(properties != null && !properties.isEmpty()){
						out.append("var _section_props = new SectionProperties(\""+sectionId+"\");\n");
						boolean isReadOnly = sectionMgr.isReadOnly(spaceType, String.valueOf(user.getId()));
						out.append("_section_props.isReadOnly="+isReadOnly+";\n");
						
						for(SectionProperty property : properties){
							out.append("var _section_prop = new SectionProperty();\n");
							for(SectionReference refs : property.getReference()){
								int valueType = refs.getValueTypeEnum().ordinal();
								String subject = ResourceBundleUtil.getString(sectionMgr.getResourceBundle(), refs.getSubject());
								String defaultValue = "";
								boolean read = refs.isReadOnly();
								if(refs.getDefaultValue() != null){
									defaultValue = ResourceBundleUtil.getString(sectionMgr.getResourceBundle(), refs.getDefaultValue());
								}
								String validate = refs.getValidate() != null? refs.getValidate():"";
								String validateStr =refs.getValidateValue() != null? refs.getValidateValue():"";
								
								SectionReference hidddenR = refs.getHiddenValue();
								String paramName = refs.getName();
								if(valueType == ValueType.singlePanel.ordinal()){
									paramName = PropertyName.singlePanel.name();
								}
								out.append("var _section_refs=new SectionReference('"+paramName+"','"+subject+"','"+valueType+"','"+defaultValue+"',"+read+",'"+validate+"','"+validateStr+"','"+refs.getPanelSetUrl()+"','"+refs.getSingleBeanId()+"','"+refs.getOnChange()+"','"+refs.getChangeValue()+"');\n");
								if(hidddenR != null){
									out.append("_section_refs.hiddenValue=new SectionReference('"+hidddenR.getName()+"','','','"+ResourceBundleUtil.getString(sectionMgr.getResourceBundle(), hidddenR.getDefaultValue())+"');\n");
								}
								if(refs.getValueRanges() != null){
									for(SectionReferenceValueRange range : refs.getValueRanges()){
										String rangeSubject = ResourceBundleUtil.getString(sectionMgr.getResourceBundle(), range.getSubject());
										
										out.append("_section_refs.addValueRange(\""+rangeSubject+"\",\""+range.getValue()+"\",\""+range.getPanelValue()+"\",\""+range.getPanelSetUrl()+"\","+range.isBackUp()+","+range.isReadOnly()+");\n");
									}
								}
								out.append("_section_prop.addReference(_section_refs);\n");
							}
							out.append("_section_props.addProperty(_section_prop);\n");
						}
						out.append("sectionPros.put(\""+sectionId+"\",_section_props);\n");
					}else{
						out.append(" noParamSections[noParamSections.length] = '" + sectionId + "';\n");
					}
				}		
				
			}
		}
		
		return out.toString();
	}
	
	public static boolean containSection(List<String[]> allSections, String sectionId){
		for (String[] sections : allSections) {
			if(sections[0].equals(sectionId)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 部门格言
	 * 
	 * @param spaceFix
	 * @return
	 */
	public static String getDepartmentMotto(Long departmentId){
		if(departmentId == null){ 
			return "";
		}
				
		try {
			SpaceFix spaceFix = getSpaceManager().getSpaceFix(Constants.SpaceType.department, departmentId, null);
			if(spaceFix != null && spaceFix.getMotto() != null) {
				return spaceFix.getMotto();
			}
			
			V3xOrgDepartment dep = getOrgManager().getDepartmentById(departmentId);
			spaceFix = getSpaceManager().getSpaceFix(Constants.SpaceType.Default_department, dep.getOrgAccountId(), null);
			if(spaceFix!= null) {
				return spaceFix.getMotto();
			}
		}
		catch (BusinessException e) {
		}
        
		return "";
	}
	
	// ipad隐藏的一级菜单：HR管理12 综合办公9业务生成器15
	private final static Set<Long> hiddenMainMenuOfIpad = new HashSet<Long>();
	static{
		hiddenMainMenuOfIpad.add(12L);
		hiddenMainMenuOfIpad.add(9L);
		hiddenMainMenuOfIpad.add(15L);
	}
	
	// ipad隐藏的二级菜单：表单制作(301)、枚举管理(305)、流水号管理(304)、业务配置(306)、日程事件(604)、模板管理(106)、写邮件(401)、邮箱设置(812)、万年历(1004)、便签(1001)、关于A8(1009)、个人考勤(803)、个人设置(801)
	private final static Set<Long> hiddenMenuOfIpad = new HashSet<Long>();
	static{
		hiddenMenuOfIpad.add(301L);
		hiddenMenuOfIpad.add(304L);
		hiddenMenuOfIpad.add(305L);
		hiddenMenuOfIpad.add(306L);
		hiddenMenuOfIpad.add(106L);
		hiddenMenuOfIpad.add(1004L);
		hiddenMenuOfIpad.add(1001L);
		hiddenMenuOfIpad.add(1009L);
		hiddenMenuOfIpad.add(803L);
		hiddenMenuOfIpad.add(801L);
	}
	//多浏览器隐藏的二级菜单：表单制作(301)、枚举管理(304)、 流水号管理(304)、业务配置(306)、信息管理(307)、基础数据(308)
	private final static Set<Long> hiddenMenuExceptIe = new HashSet<Long>();
	static{
		hiddenMenuExceptIe.add(301L);
		hiddenMenuExceptIe.add(305L);
		hiddenMenuExceptIe.add(304L);
		hiddenMenuExceptIe.add(306L);
		hiddenMenuExceptIe.add(307L);
		hiddenMenuExceptIe.add(308L);
	}
	
	/**
	 * 显示空间菜单
	 * @param pageContext
	 * @param path 空间路径
	 * @param type 空间类型
	 * @return
	 */
	public static String showMenu(PageContext pageContext, String path, String type) {
		User user = CurrentUser.get();
		Long memberId = user.getId();

		Collection<Menu> allSystemMenus = getMenuManager().getAllSystemMenus(false);
		Set<Long> accessMenus = user.getAccessSystemMenu();

		List<PluginMainMenu> accessPluginMenuOfMember = getMenuManager().getAccessPluginMenuOfMember(memberId, user.getLoginAccount());
		List<Long> menuProfile = getMenuManager().getMenuProfile(memberId); // 我的菜单个性化，以及菜单的排序

		StringBuilder out = new StringBuilder();
		List<Long> allMainMenuId = new ArrayList<Long>();

		// 将表单业务配置挂接的菜单：与系统菜单分开处理
		List<Long> domainIds = CommonTools.getUserDomainIds(user, getOrgManager());
		List<Menu> formBizConfigMenus = getMenuManager().getAccessMenusThroughFormBizConfig(memberId, domainIds);
		List<Menu> allMenus = new ArrayList<Menu>();

		boolean isHiddenMenuOfIpad = (Boolean) (BrowserFlag.HideOperation.getFlag(user));
		boolean isHiddenMenuOfExceptIE = (Boolean) (BrowserFlag.HideBrowsers.getFlag(user));

		for (Menu menu : allSystemMenus) {
			if ((!isHiddenMenuOfIpad || !isHiddenMenuOfExceptIE) && (hiddenMainMenuOfIpad.contains(menu.getId()) || hiddenMenuExceptIe.contains(menu.getId()))) {
				continue;
			}

			allMenus.add(menu);
		}
		
		if (CollectionUtils.isNotEmpty(formBizConfigMenus)) {
			allMenus.addAll(formBizConfigMenus);
		}

		// 所有业务表单绑定挂接菜单
		List<Menu> formBindMenus = getMenuManager().getFormBindMenus();
		if (CollectionUtils.isNotEmpty(formBindMenus)) {
			allMenus.addAll(formBindMenus);
		}
		
		// wangjingjing 插件配置文件加载的菜单参与排序 begin
		StringBuilder tmpsb = new StringBuilder();
		String pluginMenuSort = "";
		// 插件配置文件中加载的菜单中一级菜单的排序号起始值，参见PluginSystemInit.java的pluginMenuIdIndex
		int pluginMenuIdIndex = 2000;
		for (PluginMainMenu pluginMainMenu : accessPluginMenuOfMember) {
			PluginMenu[] children = pluginMainMenu.getChildren();
			if (children == null || children.length < 1) {
				continue;
			}
			Menu menuTmp = new Menu();
			menuTmp.setId(pluginMainMenu.getId());
			menuTmp.setName(pluginMainMenu.getName());
			menuTmp.setPluginId(pluginMainMenu.getPluginName());
			if (null != pluginMainMenu.getSortId()) {
				menuTmp.setSortId(pluginMainMenu.getSortId());
			} else {
				menuTmp.setSortId(++pluginMenuIdIndex);
			}
			// 从插件配置文件中加载的菜单在排序后，拼接菜单字符串时，按照二开预制插件菜单处理
			menuTmp.setType(Menu.TYPE.customPlugin.ordinal());
			tmpsb.append("#").append(pluginMainMenu.getPluginName()).append("#");

			for (PluginMenu pchild : children) {
				Menu childTmp = new Menu();
				childTmp.setAction(pchild.getUrl());
				childTmp.setParentId(pluginMainMenu.getId());
				childTmp.setName(pchild.getName());
				childTmp.setPluginId(pluginMainMenu.getPluginName());
				childTmp.setIcon(pchild.getIcon());
				childTmp.setId(pchild.getId());
				menuTmp.addChild(childTmp);
			}
			allMenus.add(menuTmp);
		}
		pluginMenuSort = tmpsb.toString();
		// 这个变量不用了，释放掉
		accessPluginMenuOfMember = null;
		tmpsb = null;
		// wangjingjing 插件配置文件加载的菜单参与排序 end
		
		Collections.sort(allMenus);

		List<Long> menuIds = null;
		boolean menuIdsNotEmpty = false;
		if ("department".equals(type) || "corporation".equals(type) || "group".equals(type) || "custom".equals(type) || "public_custom".equals(type) || "public_custom_group".equals(type)) {
			SpaceFix spaceFix = getSpaceManager().getSpaceFix(path);
			if (spaceFix != null && spaceFix.isSpaceMenuEnabled()) {
				menuIds = getMenuManager().getSpaceMenuIds(spaceFix.getId());
				menuIdsNotEmpty = CollectionUtils.isNotEmpty(menuIds);
				out.append("spaceSetting = false;");
			} else {
				out.append("spaceSetting = true;");
			}
		} else {
			out.append("spaceSetting = true;");
		}
		
		if (menuIdsNotEmpty) {
			menuProfile = new ArrayList<Long>();
		}
		
		Map<Long, Boolean> v3xBizMenuPurviewMap = FormBizConfigUtils.getV3xBizMenuPurviewMap(memberId, menuProfile);

		for (Menu menu : allMenus) {
			Long id = menu.getId();

			// 一级菜单是否显示，个人空间才受个人菜单设置约束
			boolean isShow = menuIdsNotEmpty ? true : (menuProfile.isEmpty() || menuProfile.contains(id));
			// 判断是否为当前空间的菜单（非个人空间自定义的空间菜单）
			boolean isShowSpaceMenu = !menuIdsNotEmpty ? true : menuIds.contains(id);
			isShow = isShow && isShowSpaceMenu;

			// 区分表单菜单与系统菜单是否显示的判断
			if (menu.getType() == Menu.TYPE.formBizConfig.ordinal()) {
				isShow = isShow && getFormBizConfigManager().isShowToUser(user.getId(), menu);
			} else if (menu.getType() == Menu.TYPE.formAppBindBizConfig.ordinal()) {
				isShow = v3xBizMenuPurviewMap.get(menu.getId()) == null ? false : v3xBizMenuPurviewMap.get(menu.getId()).booleanValue();
			} else {
				isShow = isShow && menu.isShow(accessMenus);
			}

			if (menu.getType() != Menu.TYPE.customPlugin.ordinal()) {
				List<Menu> children = menu.getChildren();
				// 表单业务配置所挂接的菜单
				if (menu.getType() == Menu.TYPE.formBizConfig.ordinal()) {
					out.append("var menu").append(id.toString().replaceAll("-", "_")).append(" = new Menu(").append("\"").append(id.toString()).append("\"").append(", \"").append(StringEscapeUtils.escapeJavaScript(menu.getName())).append("\", ").append(isShow).append(");");
					children = getMenuManager().getChildMenus(menu.getId());
				} else if (menu.getType() != Menu.TYPE.formAppBindBizConfig.ordinal()) {
					out.append("var menu").append(id).append(" = new Menu(").append("\"").append(id).append("\"").append(", \"").append(getString(pageContext, menu.getName())).append("\", ").append(isShow).append(");");
				}
				out.append("\n");
				boolean isNotPrint = true;
				for (Menu menu2 : children) {
					if ((!isHiddenMenuOfIpad && hiddenMenuOfIpad.contains(menu2.getId())) || (!isHiddenMenuOfExceptIE && hiddenMenuExceptIe.contains(menu2.getId()))) {
						continue;
					}

					// 二级菜单是否显示
					boolean isShow2 = isShow && menu2.isShow(accessMenus);

					// 表单业务配置所挂接菜单的二级菜单与一级菜单同时显示
					if (menu2.getType() == Menu.TYPE.formBizConfig.ordinal()) {
						isShow2 = isShow;
					}
					
					// 业务表单菜单绑定
					if (menu2.getType() == Menu.TYPE.formAppBindBizConfig.ordinal()) {
						isShow2 = v3xBizMenuPurviewMap.get(menu2.getId()) == null ? false : v3xBizMenuPurviewMap.get(menu2.getId()).booleanValue();
					}

					// 判断是否为当前空间的菜单（非个人空间自定义的空间菜单）
					boolean isShowSpaceMenu2 = !menuIdsNotEmpty ? true : menuIds.contains(menu2.getId());
					isShow2 = isShow2 && isShowSpaceMenu2;
					
					//外部人员去掉二级菜单中的个人考勤
					if ("outer".equals(type) && menu2.getId() == 803) {
						isShow2 = false;
					}

					String menuShowName = null;
					if (menu2.getType() != Menu.TYPE.customPlugin.ordinal()) {
						FormBizConfigUtils.trunMenuName(menu2);
						menuShowName = getString(pageContext, menu2.getName());
					} else {
						String r = PluginSystemInit.getInstance().getPluginI18NResource(menu2.getPluginId());
						menuShowName = getString(r, menu2.getName());
					}
					List<Menu> subChildren = menu2.getChildren();
					if (subChildren.size() == 0) {
						if (isNotPrint && menu.getType() == Menu.TYPE.formAppBindBizConfig.ordinal()) {
							isShow = isShowSpaceMenu && (v3xBizMenuPurviewMap.get(menu.getId()) == null ? false : v3xBizMenuPurviewMap.get(menu.getId()).booleanValue());
							out.append("var menu").append(id.toString().replaceAll("-", "_")).append(" = new Menu(").append("\"").append(id.toString()).append("\"").append(", \"").append(StringEscapeUtils.escapeJavaScript(menu.getName())).append("\", ").append(isShow).append(");");
							out.append("\n");
							isNotPrint = false;
						}

						if (menu2.getType() == Menu.TYPE.formBizConfig.ordinal() || menu2.getType() == Menu.TYPE.formAppBindBizConfig.ordinal()) {
							out.append("menu").append(id.toString().replaceAll("-", "_")).append(".add(new MenuItem(").append("\"").append(menu2.getId().toString()).append("\"").append(", \"").append(StringEscapeUtils.escapeJavaScript(menu2.getName())).append("\", \"").append(calculateURL(FormBizConfigUtils.trunAction(menu2.getId().toString(), menu2.getAction(), menu2.getType()), pageContext)).append("\", \"").append(menu2.getTarget()).append("\", \"").append(resolveUrl(menu2.getIcon(), pageContext)).append("\",'', ").append(isShow2).append("));");
						} else {
							out.append("menu").append(id).append(".add(new MenuItem(").append("\"").append(menu2.getId()).append("\"").append(", \"").append(menuShowName).append("\", \"").append(calculateURL(menu2.getAction(), pageContext)).append("\", \"").append(menu2.getTarget()).append("\", \"").append(resolveUrl(menu2.getIcon(), pageContext)).append("\",'', ").append(isShow2).append("));");
						}
						out.append("\n");
					} else {
						out.append("var menuItem").append(menu2.getId()).append("=new MenuItem(\"").append(menu2.getId()).append("\", \'").append(menuShowName).append("\', \"").append(calculateURL(menu2.getAction(), pageContext)).append("\", \"").append(menu2.getTarget()).append("\", \'").append(resolveUrl(menu2.getIcon(), pageContext)).append("\','', ").append(isShow2).append(");");
						out.append("\n");
						out.append("var subMenu").append(menu2.getId() * 10).append(" = new SubMenu(\"").append(menu2.getId() * 10).append("\", \'").append("\',").append(isShow2).append(",2);");
						out.append("\n");
						for (Menu menu3 : subChildren) {
							String menu3ShowName = null;
							if (menu3.getType() != Menu.TYPE.customPlugin.ordinal()) {
								menu3ShowName = getString(pageContext, menu3.getName());
							} else {
								String r = PluginSystemInit.getInstance().getPluginI18NResource(menu3.getPluginId());
								menu3ShowName = getString(r, menu3.getName());
							}

							// 三级菜单是否显示
							boolean isShow3 = isShow2 && menu3.isShow(accessMenus);
							// 判断是否为当前空间的菜单（非个人空间自定义的空间菜单）
							boolean isShowSpaceMenu3 = !menuIdsNotEmpty ? true : menuIds.contains(menu3.getId());
							isShow3 = isShow3 && isShowSpaceMenu3;

							out.append("var subMenuItem").append(menu3.getId()).append(" = new SubMenuItem(").append(menu3.getId()).append(",\'").append(menu3ShowName).append("\',\"").append(calculateURL(menu3.getAction(), pageContext)).append("\", \"").append(menu3.getTarget()).append("\", \'").append(resolveUrl(menu3.getIcon(), pageContext)).append("\','', ").append(isShow3).append(");");
							out.append("\n");
							out.append("subMenu").append(menu2.getId() * 10).append(".add(subMenuItem").append(menu3.getId()).append(");");
							out.append("\n");

						}
						out.append("menuItem").append(menu2.getId()).append(".add(subMenu").append(menu2.getId() * 10).append(");");
						out.append("\n");
						out.append("menu").append(id).append(".add(menuItem").append(menu2.getId()).append(");");
						out.append("\n");
					}
				}
			} else {
				String r = PluginSystemInit.getInstance().getPluginI18NResource(menu.getPluginId());
				if (pluginMenuSort.indexOf(menu.getPluginId()) == -1) {
					out.append("var menu").append(id).append(" = new Menu(\"").append(id).append("\", \'").append(getString(r, menu.getName())).append("\', ").append(isShow).append(");");
					out.append("\n");
					List<Menu> children = menu.getChildren();
					for (Menu menu2 : children) {
						boolean isShow2 = isShow && menu2.isShow(accessMenus);
						out.append("menu").append(id).append(".add(new MenuItem(").append(menu2.getId()).append(", \'").append(getString(r, menu2.getName())).append("\', \"").append(calculateURL(menu2.getAction(), pageContext)).append("\", \"").append(menu2.getTarget()).append("\", \'").append(resolveUrl(menu2.getIcon(), pageContext)).append("\','', ").append(isShow2).append("));");
						out.append("\n");
					}
				} else {
					List<Menu> children = menu.getChildren();
					id = menu.getId();
					isShow = menuProfile.isEmpty() || menuProfile.contains(id);
					isShow = isShow && isShowSpaceMenu;
					out.append("var menu").append(id).append(" = new Menu(").append(id).append(", \'").append(getString(r, menu.getName())).append("\', ").append(isShow).append(");");
					out.append("\n");
					for (Menu childMenu : children) {
						out.append("menu").append(id).append(".add(new MenuItem(").append(childMenu.getId()).append(", \'").append(getString(r, childMenu.getName())).append("\', \"").append(calculateURL(childMenu.getAction(), pageContext)).append("\", \"main\", \'").append(resolveUrl(childMenu.getIcon(), pageContext)).append("\', '', true));");
						out.append("\n");
					}
				}
			}
			allMainMenuId.add(id);
		}

		for (Long id : menuProfile) {
			if (allMainMenuId.contains(id)) {
				out.append("menuArray.add(menu").append(id.toString().replaceAll("-", "_")).append(");\n");
				allMainMenuId.remove(id);
			}
		}

		for (Long id : allMainMenuId) {
			out.append("menuArray.add(menu").append(id.toString().replaceAll("-", "_")).append(");\n");
		}
		
		return out.toString();
	}
	
	private static final String regExp  = "^[a-zA-Z0-9]+:(//|\\\\)";
	private static final String regExp1 = "^(javascript|mailto):";
	
	private static String calculateURL(String url, PageContext pageContext){
		if(matches(regExp, url.toLowerCase()) || matches(regExp1, url.toLowerCase())){
			return url;
		}
		
		try {
			return LinkTag.calculateURL(url, pageContext);
		}
		catch (JspException e) {
			log.error("", e);
		}
		
		return url;
	}
	
    private static String resolveUrl(String url, PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		if (url.startsWith("/") || url.startsWith("\\"))
			return (request.getContextPath() + url);
		else
			return url;
	}
    
    private static boolean matches(String regex, CharSequence input){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        
        return m.find();
    }
    
    public static String showMenuNameOfTree(Menu menu){
        if(menu.getType() == Menu.TYPE.customPlugin.ordinal()){
            String r = PluginSystemInit.getInstance().getPluginI18NResource(menu.getPluginId());
            return getString(r, menu.getName());
        }
        else{
            return com.seeyon.v3x.main.Constant.getValueFromMainRes(menu.getName());
        }
    }
    
	public static void showCustomMenuOfTree(List<Menu> children, Set<Long> excludeMenuIds, PageContext pageContext, boolean isSystem) {
		JspWriter out = pageContext.getOut();
		if (CollectionUtils.isNotEmpty(children)) {
			try {
				for (Menu menu : children) {
					if (excludeMenuIds != null && excludeMenuIds.contains(menu.getId())) {
						continue;
					}

					if (isSystem && menu.getType() == Menu.TYPE.formBizConfig.ordinal()) {
						continue;
					}

					String menuItemName = com.seeyon.v3x.main.Constant.getValueFromMainRes(menu.getName());
					String nodeId = String.valueOf(menu.getId()).replace("-", "_");
					String pNodeId = String.valueOf(menu.getParentId()).replace("-", "_");
					out.println("var menu" + nodeId + " = new WebFXCheckBoxTreeItem('" + menu.getId() + "', '" + menuItemName + "' , null, null, false, null, null, null, null);");
					out.println("menu" + pNodeId + ".add(menu" + nodeId + ");");

					List<Menu> _children = menu.getChildren();
					if (CollectionUtils.isNotEmpty(_children)) {
						showMenuOfTree(_children, excludeMenuIds, pageContext);
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public static void showMenuOfTree(List<Menu> children, Set<Long> excludeMenuIds, PageContext pageContext) {
		showCustomMenuOfTree(children, excludeMenuIds, pageContext, true);
	}
    
    /**
     * 菜单排序－系统菜单JSON数据
     * @param menuList
     * @param pageContext
     */
	public static void showSysMenuOfTree(List<Menu> menuList, PageContext pageContext) {
		if (CollectionUtils.isNotEmpty(menuList)) {
			try {
				JspWriter out = pageContext.getOut();
				StringBuilder sb = new StringBuilder();
				getMenuStr(menuList, sb);
				if (sb.length() > 0) {
					out.print(sb.substring(0, sb.length() - 1));
				}
			} catch (Exception e) {
				log.error("菜单排序：", e);
			}
		}
	}

	private static void getMenuStr(List<Menu> menuList, StringBuilder sb) {
		for (Menu menu : menuList) {
			sb.append("{"
					+ "'id':'" + menu.getId() + "',"
					+ "'pId':'" + (menu.getParentId() != null ? menu.getParentId() : 0L) + "',"
					+ "'name':'" + com.seeyon.v3x.main.Constant.getValueFromMainRes(menu.getName()) + "',"
					+ "'sortId':'" + menu.getSortId() + "',"
					+ "'childOuter':'" + (menu.getParentId() != null ? true : false) + "'"
					+ "},");
			List<Menu> _children = menu.getChildren();
			if (CollectionUtils.isNotEmpty(_children)) {
				getMenuStr(_children, sb);
			}
		}
	}
    
    /**
     * 显示插件的系统管理的菜单
     * 
     * @param location
     * @param pageContext
     * @return
     */
    public static String getPluginMenuOfSystem(MenuLocation l, PageContext pageContext){
    	List<PluginMainMenu> menus = PluginSystemInit.getInstance().getMenus(l);
    	
    	if(menus != null){
    		try{
    			JspWriter out = pageContext.getOut();
    			
	    		for (PluginMainMenu menu : menus) {
	    			long id = menu.getId();
	    			PluginMenu[] children = menu.getChildren();
	    			if(children == null || children.length == 0){
	    				continue;
	    			}
	    			
	    			String r = menu.getMenuI18NResource();
	    			
	    			out.println("var menu" + id + " = new Menu(" + id + ", \"" + getString(r, menu.getName()) + "\");");
	    			
	    			for (PluginMenu menu2 : children) {
	    				out.println("menu" + id + ".add(new MenuItem(" + menu2.getId() + ", \'" + getString(r, menu2.getName()) + "\', \"" + calculateURL(menu2.getUrl(), pageContext) + "\", \"main\", \'" + resolveUrl(menu2.getIcon(), pageContext) + "\', '', true));");
					}
	    			
	    			out.println("menuArray.add(menu" + id + ");");
				}
            }
            catch(Exception e){
                log.error("", e);
            }
    	}
    	
    	return null;
    }

    /**
     * 个人消息转移设置 是否勾选checkbox
     * @param congifItems
     * @param type
     * @param applicationCategory
     * @param item
     * @return
     */
    public static boolean isSelectedOfMessageSetting(
			Map<Integer, Set<String>> congifItems,
			String type, int applicationCategory, String item) {
    	MessagePipelineManager messagePipelineManager = (MessagePipelineManager)ApplicationContextHolder.getBean("messagePipelineManager");  
    	//用户没有配置过，采用系统默认情况
    	if(congifItems == null){
    		MessagePipeline messagePipeline = messagePipelineManager.getMessagePipeline(type);
    		if(messagePipeline.isDefaultSend()){
    		    //PC消息默认勾选全部父节点
    		    if(Strings.isBlank(item)){
    		        return true;
    		    }
                else{
                    //勾选除协同(只提醒重要)和公文(只提醒紧急) 之外的所有子项
                    return (applicationCategory!=1);
                }
            }
            else{
                return false;
            }
    	}
        else{    	
            if(!congifItems.containsKey(applicationCategory)){
                return false;
            }
            //是否勾选当前父节点
            if(Strings.isBlank(item)){
                return congifItems.get(applicationCategory)!=null && !congifItems.get(applicationCategory).isEmpty();
            }
            //是否勾选当前子节点
            if(congifItems.get(applicationCategory).contains("ALL")){
                if(applicationCategory == 1){
                    return congifItems.get(applicationCategory).contains(item);
                }
                else{
                    return true;
                }
            }

            return congifItems.get(applicationCategory).contains(item);                
        
        }
	}
    
    /**
     * 将单位list转换为树型结构显示
     * @param accountList 单位list
     * @param currentNode 树根节点ID
     * @param selectedNode 需要选中的节点ID
     * @param level 起始层级 
     * @param pageContext 
     * @return
     */
    public static String accountList2Tree(List<V3xOrgAccount> accountList, 
            Long currentNode, Long selectedNode, int level, PageContext pageContext){
        if(accountList == null || accountList.isEmpty()){
            return null;
        }
        try {
            for (V3xOrgAccount account : accountList) {
                JspWriter out = pageContext.getOut();
                StringBuilder treeHTML = new StringBuilder();
                Long parentId = account.getSuperior();
                if(parentId == currentNode || (parentId != null && parentId.equals(currentNode))){
                    String selectStr = selectedNode.equals(account.getId())? "selected" : "";
                    treeHTML.append("<option value='" + account.getId() + "' " + selectStr + ">");
                    for (int i = 0; i < level; i++) {
                        treeHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                    treeHTML.append("├ ");
                    treeHTML.append(Strings.toHTML(account.getName()) + "</option>\n");
                    out.println(treeHTML.toString());                    
                    accountList2Tree(accountList, account.getId(), selectedNode, level + 1, pageContext);
                }
            }
        }
        catch (IOException e) {
            log.error("", e);
        }
        
        return null;
    }

	private static Set<String> systemRoles = new HashSet<String>() {
		{
			add("AccountAdmin");
			add("AccountManager");
			add("DepAdmin");
			add("DepManager");
			add("DepLeader");
			add("FormAdmin");
			add("HrAdmin");
			add("ProjectBuild");
			add("BlankNode");

			// 公文角色
			add("account_edoccreate");
			add("account_exchange");
			add("department_exchange");
			add("AccountEdocAdmin");

			add("Sender");
			add("SenderDepManager");
			add("SenderDepLeader");
			add("SenderSuperManager");
			add("NodeUserDepManager");
			add("NodeUserDepLeader");
			add("NodeUserSuperManager");
		}
	};

    /**
     * 取得所有角色的名称（英文名称-中文显示名称Map）。
     * @param pageContext
     * @return 英文名称-中文显示名称Map
     */
    public static Map<String,String> getAllRoleNames(PageContext pageContext){
    	Map<String,String> map = new HashMap<String,String>();

    	try {
    		for (String name  : systemRoles) {
    			map.put(name, ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","sys.role.rolename."+name));
			}

			// 扩展角色
			Map<String, V3xOrgRoleDefinition> roleDefinitions = getOrgManager().getRoleDefinitions();
			for (V3xOrgRoleDefinition def : roleDefinitions.values()) {
				map.put(def.getId(), ResourceBundleUtil.getString(def.getI18NResource(),def.getName()));
			}
		} catch (Exception e) {
			 log.error(e.getMessage(), e);
		}
    	return map;
    }

	/**
	 * 显示指定的角色名称。解决下面的硬编码判断问题 <c:when test=
	 * "${rl.name=='SystemAdmin'||rl.name=='AccountAdmin'||rl.name=='DepAdmin'||rl.name=='AccountManager'||rl.name=='GroupAdmin'||rl.name=='HrAdmin'||rl.name=='DepManager'||rl.name=='SuperManager'||rl.name=='department_exchange'||rl.name=='account_exchange'||rl.name=='DepLeader'||rl.name=='AccountEdocAdmin'}"
	 * > <fmt:message key="sys.role.rolename.${rl.name}" bundle='${org}'
	 * />:</c:when> <c:otherwise>${rl.name}: </c:otherwise>
	 * 
	 * @param roleName
	 *            角色英文名称
	 * @param pageContext
	 * @return
	 */
    public static String showRoleName(String roleName,PageContext pageContext){
    	if(systemRoles.contains(roleName)){
    		return ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","sys.role.rolename."+roleName);
    	}
		// 扩展角色
		Map<String, V3xOrgRoleDefinition> roleDefinitions = getOrgManager().getRoleDefinitions();
		V3xOrgRoleDefinition def = roleDefinitions.get(roleName);
		if(def!=null){
			return ResourceBundleUtil.getString(def.getI18NResource(),def.getName());
		}
    	return roleName;
    }

	/**
	 * 在MainFunction中提供一个方法autocomplete，可以tag和程序两种方式调用，同时提供客户端的javascript基础支撑函数。<br/>
	 * tag提供相对自动和完整的方案，自动为调用者生成一系列的input和模拟的select的下拉button（1 text input + 1 hidden input + 1 dropdown button），并提供下面的特性：<br/>
	 * 调用者自己将所有数据加载到客户端，以数组方式提供给组件；也可以自己定制取数据的方法。组件根据用户输入的text查询数据的value域，自动筛选结果并呈现。<br/>
	 * 示例：${main:autocomplete("test","data")}<br/>
	 * 选择项目以后自动更新value和text <br/>
	 * 值改变触发事件，支持联动（事件监听）<br/>
	 * 数据缺省只有text（显示值）和value（隐藏，form实际提交的值），支持扩展属性值，可以取到当前选择项的指定属性值。<br/>
	 * 
	 * 注意：需要引用main.tld，<%@ taglib uri="http://v3x.seeyon.com/taglib/main" prefix="main"%><br/>
	 * 另外在jsp中需要加如下的代码：<br/>
	 * <code>
	 * &lt;link rel="stylesheet" type="text/css" href="&lt;c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" /&gt;"&gt;
	 * &lt;script type="text/javascript" charset="UTF-8" src="&lt;c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" /&gt;"&gt;&lt;/script&gt;
	 * &lt;script type="text/javascript" charset="UTF-8" src="&lt;c:url value="/common/jquery/jquery-ui.custom.min.js${v3x:resSuffix()}" /&gt;"&gt;&lt;/script&gt;
	 * </code>
	 * 
	 * javascript支撑简单包装jquery-ui的autocomplete，只提供我们需要的功能，并保证以后可以切换底层实现，为tag提供前台的支持。
	 * 
	 * @param inputName
	 *            要渲染为autocomplete的input的名称。存储id，隐藏；会自动生成一个名为inputName+
	 *            "_autocomplete"的inputDisplay，存储text，显示。
	 * @param data
	 *            可以是一个javascript变量名称，一个数组，也可以是一段闭包的脚本。输入值的text域是显示值，
	 *            value域是实际的id。输入数据格式如下
	 * 			<pre>
	 *            var data = [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ];
	 *           </pre>，用户录入根据label值进行匹配。选择后对应项目的value值设置到id为inputName的input的value域。
	 * @return <pre>
	 * &lt;input name="inputName" id="inputName" type="hidden"/&gt;
	 * &lt;input name="inputName_autocomplete" id="inputName_autocomplete" type="text"/&gt;
	 * &lt;input type="button" onclick="toggle_inputName_autocomplete();"/&gt;
	 * </pre>
	 */
	public static String autocomplete(final String inputName, final String data) {
		StringBuffer html = new StringBuffer();
		// 生成两个input和一个dropdown button
		final String inputDisplayName = inputName + "_autocomplete";
		html.append("<input ")
				.append(buildHtmlAttributes(new HashMap<String, String>() {
					{
						put("name", inputName);
						put("id", inputName);
						put("type", "hidden");
					}
				})).append(" />");
		html.append("<input ")
				.append(buildHtmlAttributes(new HashMap<String, String>() {
					{
						put("name", inputDisplayName);
						put("id", inputDisplayName);
						put("type", "text");
						put("class", "input_autocomplete");
						put("onclick", "v3xautocomplete.toggle('"+inputName+"');");
					}
				})).append(" />\n");
		/*
		html.append("<input ")
				.append(buildHtmlAttributes(new HashMap<String, String>() {
					{
						put("type", "button");
						put("id", "btn_" + inputDisplayName);
						put("onclick", "toggle_" + inputDisplayName + "();");
						put("value", "   ");
						put("class", "dropdown_autocomplete");
					}
				})).append(" />\n")
		*/
		// 生成脚本，调用js autocomplete组件，初始化input。
		html.append("<script>\n");

		html.append("v3xautocomplete.autocomplete(").append("'").append(inputName)
				.append("',").append(data).append(");\n");
		/*
		html.append("function ").append("toggle_")
				.append(inputDisplayName + "() {\n");
		html.append("    var input = $('#").append(inputDisplayName).append("');\n");
		html.append("    if ( input.autocomplete( 'widget' ).is( ':visible' ) ) {\n");
		html.append("        input.autocomplete( 'close' );\n");
		html.append("        return;\n");
		html.append("    }\n");
		html.append("    $( ").append("btn_" + inputDisplayName)
				.append(").blur();\n");
		html.append("    input.autocomplete( 'search', '' );\n");
		html.append("    input.focus();\n");
		html.append("};");
		*/
		html.append("</script>");
		
		return html.toString();
	}
    private static StringBuffer buildHtmlAttributes(Map<String,String> attributes){
    	StringBuffer html =  new StringBuffer(); 
    	for (Entry<String, String> entry : attributes.entrySet()) {
			html.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\" ");
		}
    	return html;
    }
    
	public static String[] getPendingCategoryLink(Affair affair) {
		Integer subApp = affair.getSubApp();
		Long objectId = affair.getObjectId();

		String link = null;
		String categoryLink = null;

		ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(affair.getApp());

		Integer spaceType = (Integer) affair.getExtProperty("spaceType");
		Long spaceId = (Long) affair.getExtProperty("spaceId");
		Long typeId = (Long) affair.getExtProperty("typeId");

		switch (appEnum) {
		case inquiry:
			if (affair.getExtProperties().isEmpty()) { // 说明是老数据，只有单位、集团调查，并且只是待审核调查
				link = "/inquirybasic.do?method=survey_check&affairId=" + affair.getId() + "&bid=" + objectId;
				categoryLink = "/inquirybasic.do?method=recent_or_check";
				break;
			}
			String group = InquirySurveytype.Space_Type_Group.equals(spaceType) ? "group" : "";
			String spaceTypes = "";
			String spaceIds = "";
			if (spaceType != InquirySurveytype.Space_Type_Group.intValue() 
					&& spaceType != InquirySurveytype.Space_Type_Account.intValue() 
					&& spaceType != InquirySurveytype.Space_Type_Department.intValue()) {
				spaceTypes = String.valueOf(spaceType);
				spaceIds = String.valueOf(spaceId);
			}

			if (ApplicationSubCategoryEnum.inquiry_audit.key() == subApp.intValue()) { // 调查审核
				link = "/inquirybasic.do?method=survey_check&affairId=" + affair.getId() + "&bid=" + objectId;
				categoryLink = "/inquirybasic.do?method=checkIndex&surveytypeid=" + typeId + "&group=" + group + "&spaceType=" + spaceTypes + "&spaceId=" + spaceIds;
			} else if (ApplicationSubCategoryEnum.inquiry_write.key() == subApp.intValue()) { // 调查填写
				link = "/inquirybasic.do?method=showInquiryFrame&bid=" + objectId + "&surveytypeid=" + typeId;
				categoryLink = "/inquirybasic.do?method=more_recent_or_check&from=section&typeId=" + typeId + "&group=" + group + "&spaceType=" + spaceTypes + "&spaceId=" + spaceIds;
			}
			break;
		case bulletin:
			if (affair.getExtProperties().isEmpty()) { // 说明是老数据，只有单位、集团公告
				boolean isGroup = (affair.getSubObjectId().intValue() == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.group.ordinal());
				link = "/bulData.do?method=audit&id=" + affair.getObjectId();
				categoryLink = "/bulData.do?method=auditListMain&spaceType=" + (isGroup ? 0 : 1) + "&spaceId=&bulTypeId=";
				break;
			}
			String bulSpaceId = "";
			if (spaceType == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.custom.ordinal() 
					|| spaceType == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.public_custom.ordinal() 
					|| spaceType == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.public_custom_group.ordinal()) {
				bulSpaceId = String.valueOf(spaceId);
			}
			link = "/bulData.do?method=audit&id=" + affair.getObjectId();
			categoryLink = "/bulData.do?method=auditListMain&spaceType=" + spaceType + "&spaceId=" + bulSpaceId + "&bulTypeId=" + typeId;
			break;
		case news:
			if (affair.getExtProperties().isEmpty()) { // 说明是老数据，只有单位、集团新闻
				boolean isGroupNews = (affair.getSubObjectId().intValue() == com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType.group.ordinal());
				link = "/newsData.do?method=audit&id=" + affair.getObjectId();
				categoryLink = "/newsData.do?method=auditListMain&spaceType=" + (isGroupNews ? 0 : 1) + "&spaceId=&type=";
				break;
			}

			String newsSpaceId = "";
			if (spaceType == com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType.custom.ordinal() 
					|| spaceType == com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType.public_custom.ordinal() 
					|| spaceType == com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType.public_custom_group.ordinal()) {
				newsSpaceId = String.valueOf(spaceId);
			}
			link = "/newsData.do?method=audit&id=" + affair.getObjectId();
			categoryLink = "/newsData.do?method=auditListMain&spaceType=" + spaceType + "&spaceId=" + newsSpaceId + "&type=" + typeId;
			break;
		}

		return new String[] { link, categoryLink };
	}

}