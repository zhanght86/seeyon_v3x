package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisManager;
/**
 * 表单统计表格栏目
 * @author xgghen 2011-03-24
 */
public class WorkFlowAnalysisSection extends BaseSection{
	
	private static final Log log = LogFactory.getLog(WorkFlowAnalysisSection.class);
	private OrgManager orgManager;
	private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;
	private WorkFlowAnalysisManager workFlowAnalysisManager;
	
	public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}
	public void setWorkFlowAnalysisManager(
			WorkFlowAnalysisManager workFlowAnalysisManager) {
		this.workFlowAnalysisManager = workFlowAnalysisManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	@Override
	public String getId() {
		return "workFlowAnalysisSection";
	}
	
	
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	private static final int DEFAULT_WIDTH = 380;
	private static final int DEFAULT_HEIGHT = 208;

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete(); 
        String templete = preference.get("source");
        String appType = preference.get("app");
        String width = preference.get("imagewidth");
        String height = preference.get("imageheight");
        String date = preference.get("date");
        List<Long> templeteIds = new ArrayList<Long>();
        String tids = preference.get("chooseTemplete_value");
        StringBuilder filterTempleteIds = new StringBuilder();
        if("chooseTemplete".equalsIgnoreCase(templete)){
        	if(Strings.isNotBlank(tids)){
        		String[] idAndTypes = tids.split(",");
            	for(String idAndType : idAndTypes){
            		String[] id = idAndType.split("_");
            		templeteIds.add(Long.valueOf(id[0]));
            		if(filterTempleteIds.length()<1){
            			filterTempleteIds.append(id[0]);
            		}else{
            			filterTempleteIds.append(",").append(id[0]);
            		}
            	}
        	}
        }else{
        	templeteIds.add(WorkFlowAnalysis.AllTemplete);
        	filterTempleteIds.append(WorkFlowAnalysis.AllTemplete);
        }
        //前台传过来的模板ID带有类型，需要先降类型过滤掉以后再赋值，传递到其他地方。
        tids = filterTempleteIds.toString();
        if(Strings.isBlank(appType)) 
			appType = String.valueOf(TempleteCategory.TYPE.form.ordinal());
        Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		//默认显示上个月
		String startDate ;
		String endDate;
		if(Strings.isBlank(date)||",".equals(date)){
			 startDate = year+"-"+month;
		     endDate = year+"-"+month;
		}else{
			String [] dates = date.split("[,]");
			if(dates.length == 1){ 
				 startDate = dates[0];
				 endDate = year+"-"+month;
			}else{
				if(Strings.isBlank(dates[0])){
					 startDate = year+"-"+month;
				}else{
					 startDate = dates[0];
				} 
				
				if(Strings.isBlank(dates[1])){
					 endDate = year+"-"+month;
				}else{
					 endDate = dates[1];
				}
			}
		}
        if(Strings.isBlank(width)){
        	width = String.valueOf(DEFAULT_WIDTH);
        }
        if(Strings.isBlank(height)){
        	height = String.valueOf(DEFAULT_HEIGHT);
        }
        StringBuilder html = new StringBuilder();
        boolean hasData = hasData(
        		appType, 
        		templeteIds, 
        		startDate, 
        		endDate);
        boolean hasAcl = hasAcl();
        html.append("<div id='' style='vertical-align: middle;text-align: center;overflow-y:hidden;' class='scrollList position_relative'>");
      	if(hasData && hasAcl){
        	html.append(getChartHtml(
        				appType, 
        				templete,
        				tids,
        				startDate, 
        				endDate, 
        				height,
        				width)); 
        }else{//没有数据
        	html.append(noDataHtml());
        }
      	if(hasAcl()){
	       	 ht.addBottomButton("comprehensive_analysis_label", 
	       	 	"/workFlowAnalysis.do?method=comprehensiveAnalysisHome");
      	}
        html.append("</div>");
        ht.setHtml(html.toString());
        ht.setHeight(height);
       
		return ht ;
	}
	private String getChartHtml(String appType,
			String templete,
			String templeteIds,
			String beginDate,
			String endDate,
			String height,
			String width){
		
		//日期：如果日期是3.5升级之前的日期则不统计
	    String installDateStr = Functions.getProductInstallDate4WF(); 
	    Date installDate = Datetimes.parse(installDateStr);
	    Date sDate = Datetimes.parse(beginDate,"yyyy-MM");
	    if(sDate.before(installDate)){
	    	beginDate = Datetimes.format(installDate, "yyyy-MM");
	    }
		if("all".equals(templete)) 
			templete = String.valueOf(WorkFlowAnalysis.AllTemplete);
		String imgUrl = "/seeyon/workFlowAnalysis.do?method=comprehensiveAnalysiszChart&appType="+appType
					+"&templete="+templete
					+"&templeteId="+templeteIds
					+"&beginDate="+beginDate
					+"&endDate="+endDate
					+"&height="+height
					+"&width="+width;
		String img = "<img align='middle' src='"+imgUrl+"'/>";	
		return img;
	}
   private String noDataHtml(){
	    String info =ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "workflowdata.null.warning");
	   	StringBuilder sb = new StringBuilder();
		sb.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#d2dae1\"><tr><td width=\"20%\">");
		sb.append("</td><td width=\"60%\" align=\"center\" style=\"background:url(/seeyon/apps_res/v3xmain/images/defaultNo.jpg) center no-repeat\">");
		sb.append("<br/><br/><br/><br/><br/><br/><br/><span style=\"color:#999999;\">"+info+"</span>");
		sb.append("</td><td width=\"20%\"></td>");
		sb.append("</td></tr></table>");
		return sb.toString();
   }
   private boolean hasAcl(){
	   User user = CurrentUser.get();
		boolean isAcl = workFlowAnalysisAclManager.getAnalysisAclsByUserId(user.getLoginAccount(),user.getId())!=null;
		if(!isAcl)
			return false;
		return true;
   }
   private boolean hasData(
		   String appType,
		   List<Long> tidl,
		   String beginDate,
		   String endDate){
	    //日期：如果日期是3.5升级之前的日期则不统计
	    String installDateStr = Functions.getProductInstallDate4WF(); 
	    Date installDate = Datetimes.parse(installDateStr);
	    
	    Date eDate = Datetimes.parse(endDate,"yyyy-MM");
	    
	    if(Datetimes.getLastDayInMonth(eDate).before(installDate)) 
	    	return false;
	    
	    //判断是否有授权
	    User user = CurrentUser.get();
		//判断时候有数据
		try {
			List<WorkFlowAnalysis> l = workFlowAnalysisManager.getWorkFlowList(
					appType, 
					tidl, 
					beginDate, 
					endDate, 
					user.getId(),
					user.getLoginAccount());
			if(Strings.isEmpty(l)){
				return false;
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
		return true;
   }
	@Override
	public boolean isAllowUsed() {
		return true;
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("workflowAnalysis", preference);
	}
	@Override
	public String getBaseName() {
		return "workflowAnalysis";
	}
}
