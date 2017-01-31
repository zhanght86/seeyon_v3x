package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.POSITION_TYPE;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

public class ProjectSection extends BaseSection {

	private static final Log log = LogFactory.getLog(ProjectSection.class);

	private ProjectManager projectManager;

	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "projectSection";
	}
	
	@Override
	public String getBaseName() {
		return "project";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("project", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		int count = SectionUtils.getSectionCount(16, preference);
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
		int column = 2;
		Integer newLineStr = newLine2Column.get(width);
		if (newLineStr != null) {
			column = newLineStr.intValue();
		}
		

		String panel = SectionUtils.getPanel("all", preference);

		ChessboardTemplete c = new ChessboardTemplete();
		c.setPosition(POSITION_TYPE.left);

		try {
			List<Byte> memberTypeList = null;
			List<Long> projectTypeList = null;

			if ("designatedRole".equals(panel)) {
				String roles = preference.get(panel + "_value");
				if (Strings.isNotBlank(roles)) {
					String[] strs = roles.split(",");
					if (strs != null && strs.length > 0) {
						memberTypeList = new ArrayList<Byte>(strs.length);
						for (String str : strs) {
							if (Strings.isNotBlank(str)) {
								memberTypeList.add(Byte.valueOf(str));
							}
						}
					}
				}
			} else if ("designatedType".equals(panel)) {
				String roles = preference.get(panel + "_value");
				projectTypeList = CommonTools.parseStr2Ids(roles);
			}

			List<ProjectSummary> plist = projectManager.getIndexProjectList(CurrentUser.get().getId(), count, memberTypeList, projectTypeList);
			
			if (plist.size() < count) {
				count = plist.size();
			}
			int row = count / column + ((count % column > 0) ? 1 : 0);
			if (row < 8) {
				row = 8;
			}
			c.setLayout(row, column);
			
			for (ProjectSummary projectSummary : plist) {
				ChessboardTemplete.Item item = c.addItem();
				item.setIcon("/apps_res/v3xmain/images/section/project.gif");
				item.setName(projectSummary.getProjectName());
				item.setLink("/project.do?method=projectInfo&projectId=" + projectSummary.getId());
			}
		} catch (Exception e) {
			log.error("", e);
		}

		c.addBottomButton("set_relate_project", "/project.do?method=getIndexProjectList");
		String moreLink = "/project.do?method=getAllProjectList&more=true";
		if ("designatedRole".equals(panel) || "designatedType".equals(panel)) {
			moreLink += "&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name()) + "&panel=" + panel;
		}
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);

		return c;
	}

}