package com.seeyon.v3x.main.section.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MonthCalendarTemplate;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.util.Datetimes;

public class CalendarSectionUtil {

	public static BaseSectionTemplete setCalendarSectionData(Map<String, String> preference, Long userId, List<CalEvent> eventList, boolean isMyCal, String titleId) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		String[] rows = SectionUtils.getRowList("subject,beginTime,status", preference);
		if (!isMyCal) {
			rows = SectionUtils.getRowList("subject,beginTime,sendUser,status", preference);
		}
		
		if ("list".equals(columnsStyle)) {// 列表
			List<String> rowList = new ArrayList<String>();
			for (String row : rows) {
				rowList.add(row);
			}
			
			MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
			if (CollectionUtils.isNotEmpty(eventList)) {
				boolean containbeginTime = rowList.contains("beginTime");
				boolean containsendUser = rowList.contains("sendUser");
				boolean containstate = rowList.contains("status");
				boolean containfinishRate = rowList.contains("finishRate");

				int propTotal = rows.length;
				int[] width = { 30, 30, 15, 15, 10 };
				switch (propTotal) {
				case 4:
					width = new int[] { 45, 30, 15, 15, 0 };
					break;
				case 3:
					width = new int[] { 50, 30, 20, 0, 0 };
					break;
				case 2:
					width = new int[] { 70, 30, 0, 0, 0 };
					break;
				case 1:
					width = new int[] { 100, 0, 0, 0, 0 };
					break;
				}
				
				for (CalEvent calEvent : eventList) {
					if (calEvent != null) {
						int index = 0;
						MultiRowVariableColumnTemplete.Row row = t.addRow();
						String periodicalId = calEvent.getPeriodicalId() == null ? "" : calEvent.getPeriodicalId().toString();
						String link = "";
						if (!isMyCal) {
							link = "javascript:openDetailInDlg('/calEvent.do?method=view&id=" + calEvent.getId() + "', '', 550, 555)";
						} else {
							if (calEvent.getCreateUserId().equals(userId) && StringUtils.isBlank(calEvent.getTranMemberIds()) && StringUtils.isBlank(calEvent.getReceiveMemberId())) {
								link = "javascript:openDetailInDlg('/calEvent.do?method=editIframe&from=section&id=" + calEvent.getId() + "&periodicalId=" + periodicalId + "&beginDate=" + Datetimes.formatDate(calEvent.getBeginDate()) + "', '" + titleId + "', 530, 480)";
							} else {
								link = "javascript:openDetailInDlg('/calEvent.do?method=editIframe&from=section&id=" + calEvent.getId() + "&periodicalId=" + periodicalId + "&beginDate=" + Datetimes.formatDate(calEvent.getBeginDate()) + "', '" + titleId + "', 530, 580)";
							}
						}
						
						// 单元格1: 标题
						MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
						subjectCell.setCellContent(calEvent.getSubject());
						subjectCell.setCellWidth(width[index]);
						subjectCell.setLinkURL(link);
						subjectCell.setHasAttachments(calEvent.getAttachmentsFlag());
						index ++;
						
						if (containbeginTime) {
							// 单元格3：日程时间
							MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();
							cell2.setCellContent(calEvent.getDateRangeType() + calEvent.getDateInfo());
							cell2.setCellWidth(width[index]);
							index ++;
						}

						if (containsendUser) {
							// 单元格4 : 所属人
							MultiRowVariableColumnTemplete.Cell createrCell = row.addCell();
							createrCell.setCellContent(calEvent.getReceiverMember());
							createrCell.setCellWidth(width[index]);
							index ++;
						}
						
						if (containstate) {
							// 单元格5：状态
							MultiRowVariableColumnTemplete.Cell planStatusCell = row.addCell();
							planStatusCell.setCellContent(Constants.getStateValue(calEvent.getStates().intValue()));
							if (isMyCal && calEvent.getCreateUserId().equals(userId)) {
								planStatusCell.setLinkURL("javascript:openDetailInDlg('/calEvent.do?method=editEventState&id=" + calEvent.getId() + "', '" + titleId + "', 300, 200)");
								planStatusCell.setClassName("like-a");
							}
							planStatusCell.setCellWidth(width[index]);
							index ++;
						}
						
						if (containfinishRate) {
							// 单元格6 : 完成率
							MultiRowVariableColumnTemplete.Cell ratioCell = row.addCell();
							ratioCell.setCellContent(Functions.showRate(calEvent.getCompleteRate(), true));
							if (isMyCal && calEvent.getCreateUserId().equals(userId)) {
								ratioCell.setLinkURL("javascript:openDetailInDlg('/calEvent.do?method=editEventState&id=" + calEvent.getId() + "', '" + titleId + "', 300, 200)");
								ratioCell.setClassName("like-a");
							}
							ratioCell.setCellWidth(width[index]);
							index ++;
						}
					}
				}
			}
			return t;
		}
		else if ("calendar".equals(columnsStyle)) {// 日历
			MonthCalendarTemplate mct = new MonthCalendarTemplate();
			if (CollectionUtils.isNotEmpty(eventList)) {
				Date d = new Date();
				Date beginDate = Datetimes.getFirstDayInMonth(d);
				Date endDate = Datetimes.getLastDayInMonth(d);
				
				for (CalEvent calEvent : eventList) {
					if (calEvent != null) {
						Date date1 = calEvent.getBeginDate();
						Date date2 = calEvent.getEndDate();
						
						Date sDate = date1.before(beginDate) ? beginDate : date1;
						Date eDate = date2.after(endDate) ? endDate : date2;
						
						while(sDate.compareTo(eDate) < 1){
							mct.addEvent(sDate, calEvent.getSubject());
							sDate = Datetimes.addDate(sDate, 1);
						}
					}
				}
			}
			
			return mct;
		}
		
		return null;
	}

}