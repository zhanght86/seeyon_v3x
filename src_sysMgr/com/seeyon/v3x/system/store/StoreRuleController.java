/**
 * 
 */
package com.seeyon.v3x.system.store;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2011-12-26
 */
@CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
public class StoreRuleController extends BaseController {
	
	private StoreRuleManager storeRuleManager;
	
	private AppLogManager appLogManager;
	
	public void setStoreRuleManager(StoreRuleManager storeRuleManager) {
		this.storeRuleManager = storeRuleManager;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("sysMgr/storeRule/index");
		
		return mv;
	}
	
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("sysMgr/storeRule/new");
		
		return mv;
	}
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("sysMgr/storeRule/list");
		
		List<StoreRule> all = this.storeRuleManager.listAll();
		
		mv.addObject("all", all);
		
		return mv;
	}
	
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception{
		StoreRule storeRule = new StoreRule();
		super.bind(request, storeRule);
		
		storeRule.setIdIfNew();
		storeRule.setStartTime(request.getParameter("startTime0") + ":" + request.getParameter("startTime1"));
		storeRule.setStopTime(request.getParameter("stopTime0") + ":" + request.getParameter("stopTime1"));
		
		this.storeRuleManager.save(storeRule);
		
		String date = Datetimes.formatDate(new Date());
		Date startTime = Datetimes.parseDatetimeWithoutSecond(date + " " + storeRule.getStartTime());
		
		Date stopTime = Datetimes.parseDatetimeWithoutSecond(date + " " + storeRule.getStopTime());
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", storeRule.getId().toString());
		parameters.put("Action", "Start");
		
		QuartzHolder.newQuartzJob("StoreRuleStart" + storeRule.getId(), startTime, 24 * 60 * 60 * 1000, "StoreJob", parameters);
		
		parameters.put("Action", "Stop");
		QuartzHolder.newQuartzJob("StoreRuleStop" + storeRule.getId(), stopTime, 24 * 60 * 60 * 1000, "StoreJob", parameters);
		
		String flowState = storeRule.getFlowState();
		String P2 = "";
		if(Strings.isNotBlank(flowState)){
			String[] flowStateStr = flowState.split("[,]");
			for (int i = 0; i < flowStateStr.length; i++) {
				if(i != 0){
					P2 += ",";
				}
				P2 += ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", "StoreRule.dataScorp." + flowStateStr[i] + ".label");
			}
		}
		
		String dataScorp = storeRule.getDataScorp();
		String P3 = "";
		if(Strings.isNotBlank(dataScorp)){
			String[] dataScorpStr = dataScorp.split("[,]");
			for (int i = 0; i < dataScorpStr.length; i++) {
				if(i != 0){
					P3 += ",";
				}
				P3 += ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", "StoreRule.flowState." + dataScorpStr[i] + ".label");
			}
		}
		
		appLogManager.insertLog(CurrentUser.get(), AppLogAction.DataDump, 
				Datetimes.formatDate(storeRule.getBeginDate()), 
				Datetimes.formatDate(storeRule.getEndDate()),
				P2,
				P3
		);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
		out.println("</script>");
		out.flush();

		return super.refreshWorkspace();
	}
	
}
