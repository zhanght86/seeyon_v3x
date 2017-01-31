package com.seeyon.v3x.hr.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.HqlSearchHelper;
import com.seeyon.v3x.hr.util.TempleteHelper;

public class HrFormController extends BaseController {
	private transient static final Log LOG = LogFactory
	.getLog(HrFormController.class); 
	
	private SearchManager searchManager;
	private String jsonView;
	
	public String getJsonView() {
		return jsonView;
	}
	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}
	public SearchManager getSearchManager() {
		return searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	//初始化
	public void initialized() {
//		TempleteHelper.getInstance().initialized(this.getServletContext());
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	//菜单跳转协同
	public ModelAndView callTemplete(HttpServletRequest request,
			HttpServletResponse response)throws Exception{ 
		String key = request.getParameter("key");
		String collURL = "/collaboration.do?method=newColl";
		Long templeteId = getTempleteId(key);
		if ( templeteId != -1)
			collURL += "&templeteId="+templeteId;
		else{
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources", "hr.form.notExist.label")+"\");");
				out.println("</script>");
			} catch (IOException e) {
				LOG.error("", e);
			}
			return null;
		}
		return super.redirectModelAndView(collURL);
	}
	
	//ajax get templete id
	public ModelAndView getTemplete(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");	
		String key = request.getParameter("key");

		//to json
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("templeteId", getTempleteId(key).toString());

		String view = null;
		if (isAjax) view = this.getJsonView();
		return new ModelAndView(view, Constants.AJAX_JSON, jsonObject);
	}
	
	//得到模板Id
	private Long getTempleteId(String key) {
		//得到模板名称
		String templeteName = TempleteHelper.getInstance().getName(key);
		
		//得到表单模板
		Templete templete = HqlSearchHelper.searchTemplete(templeteName, searchManager);
		
		//表单Id
		Long templeteId = new Long(-1);
		if (null != templete && null != templete.getId()) templeteId = templete.getId();
		
		return templeteId;
	}
}
