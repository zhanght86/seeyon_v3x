package com.seeyon.v3x.main.section;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.plan.PlanStatus;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Datetimes;

public class DepartmentPlanSection extends BaseSection
{
    private final int[] width = {55, 15, 15, 15}; //宽度百分比

    private PlanManager planManager;
    
    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }
    
    @Override
    public String getIcon()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return "departmentPlanSection";
    }
    
    @Override
	public String getBaseName() {
		return "departmentPlan";
	}

    @Override
    public String getName(Map<String, String> preference)
    {
        return "departmentPlan";
    }

    @Override
    public Integer getTotal(Map<String, String> preference)
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BaseSectionTemplete projection(Map<String, String> preference)
    {  
        
        Long departmentId = CurrentUser.get().getDepartmentId();
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(ownerId != null){
            departmentId = Long.parseLong(ownerId);
        }
        
        MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
        
        List<Plan> planList = null;
        int pageSize = 8 ; 
        Date date1 = null;
        date1 = new java.sql.Timestamp(new java.util.Date().getTime());
        User user = CurrentUser.get();
        Long userId = user.getId();
        planList = planManager.getDeptNotDraftsmanPlanForPage(pageSize, departmentId, userId, date1);
        if(planList != null && !planList.isEmpty())
        {
            for(Plan plan : planList)
            {
                MultiRowVariableColumnTemplete.Row row = t.addRow(); 
                //单元格1: 计划标题+附件图标　
                MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
                subjectCell.setCellContent(plan.getTitle());
                subjectCell.setCellWidth(width[0]);
                String url = "";
                if(userId.intValue()==plan.getCreateUserId().intValue()){
                	url = "/plan.do?method=initDetailHome&editType=summary&id=" + plan.getId();                	
                }else{
                	url = "/plan.do?method=initDetailHome&editType=reply&id=" + plan.getId();
                }
                subjectCell.setLinkURL(url);
                subjectCell.setHasAttachments(plan.isHasAttachments());
                
                //单元格2: 开始时间　
                MultiRowVariableColumnTemplete.Cell startDateCell = row.addCell();
                
	            Date planStartDate = plan.getStartTime();
	            String strartTime = Datetimes.format(planStartDate, "MM-dd");
                
                startDateCell.setCellContent(strartTime);
                startDateCell.setCellWidth(width[1]);
    
                //单元格3：结束时间
                MultiRowVariableColumnTemplete.Cell rateCell = row.addCell();
                Date planEndDate = plan.getEndTime();
                String endTime = Datetimes.format(planEndDate, "MM-dd");
                rateCell.setCellContent(endTime);
                rateCell.setCellWidth(width[2]);
                
                //单元格4：状态
                MultiRowVariableColumnTemplete.Cell categoryCell = row.addCell();
                // 资源文件的path /V3XApp/src_plan/com/seeyon/v3x/plan/resource/i18n/PlanResources.properties
                ResourceBundle rb = ResourceBundle.getBundle("com.seeyon.v3x.plan.resource.i18n.PlanResources",CurrentUser.get().getLocale());
//                if(PublishStatus.DRAFT.getValue().equals(plan.getPublishStatus())){
//                	categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.draft"));                	
//                }else if(PublishStatus.ISSUED.getValue().equals(plan.getPublishStatus())){
//                	categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.issued"));     
//                }else if(PublishStatus.SUMMARY.getValue().equals(plan.getPublishStatus())){
//                	categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.summary"));     
//                }
                if(PlanStatus.BEFOREBEGINNING.getValue().equals(plan.getPlanStatus())){
                  categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.beforeBeginning"));                  
                }else if(PlanStatus.ONGOING.getValue().equals(plan.getPlanStatus())){
                  categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.ongoing"));     
                }else if(PlanStatus.CANCELLED.getValue().equals(plan.getPlanStatus())){
                  categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.cancelled"));     
                }
                else if(PlanStatus.FINISHED.getValue().equals(plan.getPlanStatus())){
                    categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.finished"));     
                  }
                else if(PlanStatus.POSTPONED.getValue().equals(plan.getPlanStatus())){
                    categoryCell.setCellContent(ResourceBundleUtil.getString(rb,"plan.status.postponed"));     
                  }
                categoryCell.setCellWidth(width[3]);                
            }
        }
        																			

        t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/planSystemMgr.do?method=planMoreHome&planDeptId="+departmentId+"&from=more");
    
        
        return t;
    }

}