package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskInfo.TaskDateEnum;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 工作任务-我的任务栏目
 * <a href="mailto:yangm@seeyon.com">Rookie Young</a>
 * 2011-3-22
 */
public class MyTaskSection extends BaseSection {

	private static final Log logger = LogFactory.getLog(MyTaskSection.class);
	
	private String titleId = "myTaskSection";

	private TaskInfoManager taskInfoManager;
	
	private MetadataManager metadataManager;

	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}
	
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return titleId;
	}
	
	@Override
	public String getBaseName() {
		return "myTask";
	}
	
	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("myTask", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	private int[] getWidth(int propTotal) {
		int[] width = { 40, 18, 18, 14, 10 };
		switch (propTotal) {
		case 4:
			width = new int[] { 50, 18, 18, 14, 0 };
			break;
		case 3:
			width = new int[] { 60, 20, 20, 0, 0 };
			break;
		case 2:
			width = new int[] { 80, 20, 0, 0, 0 };
			break;
		case 1:
			width = new int[] { 100, 0, 0, 0, 0 };
			break;
		}
		return width;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String panel = SectionUtils.getPanel(ListType.Personal.name(), preference);
		String[] rows = SectionUtils.getRowList("subject,beginTime,managers,status", preference);
		int count = SectionUtils.getSectionCount(8, preference);

		List<String> rowList = new ArrayList<String>();
		for (String row : rows) {
			rowList.add(row);
		}

		User user = CurrentUser.get();
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		ListType listType = ListType.parseName(panel);
		MultiRowVariableColumnTemplete c = new MultiRowVariableColumnTemplete();
		try {
			List<TaskInfo> tasks = taskInfoManager.getTasks(listType, user.getId(), null);
			if (CollectionUtils.isNotEmpty(tasks)) {
				boolean containbeginTime = rowList.contains("beginTime");
				boolean containmanagers = rowList.contains("managers");
				boolean containstatus = rowList.contains("status");
				boolean containfinishRate = rowList.contains("finishRate");
				
				int propTotal = rows.length;
				int[] widths = this.getWidth(propTotal);
				int sectionWidth = NumberUtils.toInt(preference.get(PropertyName.width.name()));
				int limitLen = (sectionWidth + 5 - propTotal) * 10;
				int limitManagersLen = (sectionWidth + 5 - propTotal) * 3;

				for (TaskInfo t : tasks) {
					MultiRowVariableColumnTemplete.Row row = c.addRow();
					int index = 0;
					
					MultiRowVariableColumnTemplete.Cell cell0 = row.addCell();
					String riskImg = t.getRiskLevel() > 0 ? "<span class='risklevel_" + t.getRiskLevel() + " inline-block'></span>" : "";
					String attFlag = t.isHasAttachments() ? "<span class='attachment_table_true inline-block'></span>" : "";
					int minus = (t.isHasAttachments() ? 2 : 0) + (t.getRiskLevel() > 0 ? 4 : 0);
					cell0.setCellContentHTML(riskImg + Strings.toHTML(Strings.getLimitLengthString(t.getSubject(), limitLen - minus, "...")) + attFlag);
					String url = "/taskManage.do?method=viewTaskDetail&id=" + t.getId() + "&random=" + System.currentTimeMillis();
					cell0.setLinkURL("javascript:openDetailInDlg('" + url + "', '" + titleId + "', '700', '650');");
					cell0.setAlt(t.getSubject());
					cell0.setCellWidth(widths[index]);
					index++;

					if (containbeginTime) {
						MultiRowVariableColumnTemplete.Cell cell1 = row.addCell();
						cell1.setCellContent(t.getFormatDate(TaskDateEnum.PlannedStart));
						cell1.setCellWidth(widths[index]);
						index++;
					}

					if (containmanagers) {
						MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
						String managersName = Functions.showOrgEntities(t.getManagers(), V3xOrgEntity.ORGENT_TYPE_MEMBER, TaskUtils.getCommonI18n("common.separator.label"));
						cell2.setCellContent(Strings.getLimitLengthString(managersName, limitManagersLen, "..."));
						cell2.setAlt(managersName);
						cell2.setCellWidth(widths[index]);
						index++;
					}

					if (containstatus) {
						MultiRowVariableColumnTemplete.Cell cell3 = row.addCell();
						String i18nLabel = metadataManager.getMetadataItemLabel(MetadataNameEnum.task_status, t.getStatus().toString());
						cell3.setCellContent(TaskUtils.getI18n(i18nLabel));
						cell3.setCellWidth(widths[index]);
						index++;
					}
					
					if (containfinishRate) {
						MultiRowVariableColumnTemplete.Cell cell4 = row.addCell();
						cell4.setCellContent(Functions.showRate(t.getFinishRate(), true));
						cell4.setCellWidth(widths[index]);
					}
				}
			}
		} catch (Exception e) {
			logger.error("读取、填写我的任务栏目数据过程中出现异常：", e);
		}
		
		c.addBottomButton("new_task_label", "javascript:openDetailInDlg('/taskManage.do?method=addTaskPageFrame&from=timing', '" + titleId + "', 600, 500)");
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/taskManage.do?method=listTasksIndex&from=" + listType.name());
		return c;
	}

}