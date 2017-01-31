package com.seeyon.v3x.main.section;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Datetimes;

/**
 * 部门要事管理栏目
 * 
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * 
 */
public class DepartmentImportanceEventSection extends BaseSection {
	
	private final int[] width = { 46, 28, 14, 12 }; // 宽度百分比

	private String titleId = "departmentImportanceEventSection";

	private CalEventManager calEventManager;

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
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
		return "departmentImportanceEvent";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return "departmentImportanceEvent";
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		User user = CurrentUser.get();
		Long departmentId = CurrentUser.get().getDepartmentId();
		String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
		String linkHead = "";
		String stateLinkHead = "";
		String theLink = "";
		String stateLink = "";
		if (ownerId != null) {
			departmentId = Long.parseLong(ownerId);
		}

		MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
		List<CalEvent> eventList = null;
		eventList = this.calEventManager.getEventListByDeptId(departmentId);
		if (eventList != null && !eventList.isEmpty()) {
			if (eventList.size() > 8)
				eventList = eventList.subList(0, 8);

			for (CalEvent calEvent : eventList) {
				MultiRowVariableColumnTemplete.Row row = t.addRow();

				String receiveId = calEvent.getReceiveMemberId();
				CharSequence c = String.valueOf(user.getId());

				if (calEvent.getCreateUserId() == user.getId() || (receiveId != null && receiveId.contains(c))) {
					linkHead = "/calEvent.do?method=editIframe&id=" + calEvent.getId();
					stateLinkHead = "/calEvent.do?method=editEventState&id=" + calEvent.getId();
				} else {
					linkHead = "/calEvent.do?method=editIframe&id=" + calEvent.getId();
					stateLinkHead = "/calEvent.do?method=editEventState&id=" + calEvent.getId() + "&readonly=true";
				}
				if (calEvent.getCreateUserId().equals(user.getId()) && StringUtils.isBlank(calEvent.getTranMemberIds()) && StringUtils.isBlank(calEvent.getReceiveMemberId())) {
					theLink = "javascript:openDetailInDlg('" + linkHead + "','" + titleId + "',530,480)";
					stateLink = "javascript:openDetailInDlg('" + stateLinkHead + "','" + titleId + "', 300, 200)";
				} else {
					theLink = "javascript:openDetailInDlg('" + linkHead + "','" + titleId + "',530,580)";
					stateLink = "javascript:openDetailInDlg('" + stateLinkHead + "','" + titleId + "', 300, 200)";
				}

				// 单元格1: 事件标题+附件图标　
				MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
				subjectCell.setCellContent(calEvent.getSubject());
				subjectCell.setCellWidth(width[0]);
				subjectCell.setLinkURL(theLink);
				subjectCell.setHasAttachments(calEvent.getAttachmentsFlag());

				// 单元格2: 事件执行人　
				MultiRowVariableColumnTemplete.Cell ownerCell = row.addCell();
				ownerCell.setCellContent(calEvent.getReceiverMember());
				ownerCell.setCellWidth(width[1]);

				// 单元格3：开始日期
				MultiRowVariableColumnTemplete.Cell dateCell = row.addCell();

				Date todayFirstTime = Datetimes.getTodayFirstTime();
				Date eventBeginDate = calEvent.getBeginDate();
				String time = null;
				if (eventBeginDate.getTime() < todayFirstTime.getTime()) {
					time = Datetimes.format(eventBeginDate, "MM-dd");
				} else {
					time = Datetimes.format(eventBeginDate, "HH:mm");
				}
				dateCell.setCellContent(time);
				dateCell.setCellWidth(width[2]);

				// 单元格4：完成率
				MultiRowVariableColumnTemplete.Cell rateCell = row.addCell();
				rateCell.setCellContent(NumberFormat.getIntegerInstance().format(calEvent.getCompleteRate()) + "%");
				rateCell.setCellWidth(width[3]);
				rateCell.setLinkURL(stateLink);

			}
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/calEvent.do?method=moreDeptImportentEvent&departmentId=" + departmentId);
		return t;
	}
}