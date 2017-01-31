package com.seeyon.v3x.main.section.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.plan.Constant;
import com.seeyon.v3x.plan.PlanStatus;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.util.Datetimes;

public class PlanSectionUtil {

	public static BaseSectionTemplete setPlanSectionData(Map<String, String> preference, int[] width, List<Plan> planList, boolean isMyPlan, String titleId) {
		String[] rows = SectionUtils.getRowList("subject,time,sendUser,state", preference);
		String subjectLink = "/plan.do?method=initDetailHome&editType=reply&id=";
		String moreLink = "/planSystemMgr.do?method=planMgrHome";
		if (isMyPlan) {
			rows = SectionUtils.getRowList("subject,time,state", preference);
			subjectLink = "/plan.do?method=initDetailHome&editType=summary&id=";
			moreLink = "/planSystemMgr.do?method=myPlanHome";
		}
		
		List<String> rowList = new ArrayList<String>();
		for (String row : rows) {
			rowList.add(row);
		}
		
		MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
		if (CollectionUtils.isNotEmpty(planList)) {
			boolean containtime = rowList.contains("time");
			boolean containsendUser = rowList.contains("sendUser");
			boolean containstate = rowList.contains("state");
			boolean containfinishRate = rowList.contains("finishRate");
			
			String beforeBeginning = Constant.getPlanI18NValue("plan.status.beforeBeginning");
			String ongoing = Constant.getPlanI18NValue("plan.status.ongoing");
			String finished = Constant.getPlanI18NValue("plan.status.finished");
			String cancelled = Constant.getPlanI18NValue("plan.status.cancelled");
			String postponed = Constant.getPlanI18NValue("plan.status.postponed");
			
			for (Plan plan : planList) {
				if (plan != null) {
					MultiRowVariableColumnTemplete.Row row = t.addRow();
					
					// 单元格1: 标题
					MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
					subjectCell.setCellContent(plan.getTitle());
					subjectCell.setCellWidth(width[0]);
					subjectCell.setLinkURL(subjectLink + plan.getId(), OPEN_TYPE.openWorkSpaceRight);
					subjectCell.setHasAttachments(plan.isHasAttachments());
					
					if (containtime) {
						// 单元格2：开始时间
						MultiRowVariableColumnTemplete.Cell startDateCell = row.addCell();
						startDateCell.setCellContent(Datetimes.format(plan.getStartTime(), Datetimes.dateStyle));
						startDateCell.setCellWidth(width[1]);
						
						// 单元格3：结束时间
						MultiRowVariableColumnTemplete.Cell endDateCell = row.addCell();
						endDateCell.setCellContent(Datetimes.format(plan.getEndTime(), Datetimes.dateStyleWithoutYear));
						endDateCell.setCellWidth(width[2]);
					}

					if (containsendUser) {
						// 单元格4 : 发起人
						MultiRowVariableColumnTemplete.Cell createrCell = row.addCell();
						createrCell.setCellContent(Functions.showMemberName(plan.getCreateUserId()));
						createrCell.setCellWidth(width[3]);
					}
					
					if (containstate) {
						// 单元格5：计划状态
						MultiRowVariableColumnTemplete.Cell planStatusCell = row.addCell();
						if (PlanStatus.BEFOREBEGINNING.getValue().equals(plan.getPlanStatus())) {
							planStatusCell.setCellContent(beforeBeginning);
						} else if (PlanStatus.ONGOING.getValue().equals(plan.getPlanStatus())) {
							planStatusCell.setCellContent(ongoing);
						} else if (PlanStatus.FINISHED.getValue().equals(plan.getPlanStatus())) {
							planStatusCell.setCellContent(finished);
						} else if (PlanStatus.CANCELLED.getValue().equals(plan.getPlanStatus())) {
							planStatusCell.setCellContent(cancelled);
						} else if (PlanStatus.POSTPONED.getValue().equals(plan.getPlanStatus())) {
							planStatusCell.setCellContent(postponed);
						}
						
						if (isMyPlan) {
							planStatusCell.setLinkURL("javascript:openDetailInDlg('/plan.do?method=editState&id=" + plan.getId() + "', '" + titleId + "', 300, 200)");
							planStatusCell.setClassName("like-a");
						}
						
						planStatusCell.setCellWidth(width[4]);
					}
					
					if (containfinishRate) {
						// 单元格6 : 完成率
						MultiRowVariableColumnTemplete.Cell ratioCell = row.addCell();
						ratioCell.setCellContent(Functions.showRate(plan.getFinishRatio(), true));
						
						if (isMyPlan) {
							ratioCell.setLinkURL("javascript:openDetailInDlg('/plan.do?method=editState&id=" + plan.getId() + "', '" + titleId + "', 300, 200)");
							ratioCell.setClassName("like-a");
						}
						
						ratioCell.setCellWidth(width[5]);
					}
				}
			}
		}

		if (isMyPlan) {
			t.addBottomButton("new_plan_label", "/plan.do?method=initAdd&type=2");
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
		return t;
	}

}