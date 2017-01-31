package com.seeyon.v3x.mytemplate.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.manager.MtTemplateManagerCAP;
import com.seeyon.cap.meeting.util.Constants;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;


public class MyTemplateController extends BaseController {

	private MtTemplateManagerCAP mtTemplateManagerCAP;
	
	private TempleteManager templeteManager;
	
	private InquiryManager inquiryManager;
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView myTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/sMyTemplate");
		return modelAndView;
	}
	
	public ModelAndView initListTemplateEntry(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("v3xmain/mytemplate/listMyTemplateEntry");
		mav.addObject("type", request.getParameter("type"));
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView initListTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/listMyTemplate");
		String type = RequestUtils.getStringParameter(request, "type");
		List templateList = new ArrayList();
		//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询start
		//lijl添加,获取当前的版本是否是政务版本
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
		if(type.equals("col") || "edoc".equals(type) || "info".equals(type)){
			if(isGovVersion){
				templateList = templeteManager.getPersonalAllTemplete();//取个人（启用、停用）模版
			}else{
				templateList = pagenate(templeteManager.getPersonalAllTemplete());//取个人（启用、停用）模版
			}
		}
		if(type.equals("col") && !templateList.isEmpty()){
			List colTemplateList = new ArrayList();
			Templete template = null;
			for(int i=0;i<templateList.size();i++) {
				template = (Templete)templateList.get(i);
				if(template.getCategoryType() == null)
					colTemplateList.add(template);
			}
			templateList = colTemplateList;
		}else if("edoc".equals(type) && !templateList.isEmpty()) {
			List colTemplateList = new ArrayList();
			Templete template = null;
			for(int i=0;i<templateList.size();i++) {
				template = (Templete)templateList.get(i);
				if(template.getCategoryType() != null && template.getCategoryType()!=ApplicationCategoryEnum.info.getKey())
					colTemplateList.add(template);
			}
			templateList = colTemplateList;
		}else if("info".equals(type) && !templateList.isEmpty()) {
			List colTemplateList = new ArrayList();
			Templete template = null;
			for(int i=0;i<templateList.size();i++) {
				template = (Templete)templateList.get(i);
				if(template.getCategoryType() != null && template.getCategoryType()==ApplicationCategoryEnum.info.getKey())
					colTemplateList.add(template);
			}
			templateList = colTemplateList;
		}
		//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询end
		else if(type.equals("meeting")){
			templateList = mtTemplateManagerCAP.findAllWithoutInit(Constants.MEETING_TEMPLATE_TYPE_PERSON);
		}else if(type.equals("inquiry")){
			templateList = inquiryManager.getTemplateList();
			modelAndView.addObject("accountId", CurrentUser.get().getAccountId());
		}
		//lijl添加
		if(isGovVersion){
			modelAndView.addObject("templateList", pagenate(templateList));
		}else{
			modelAndView.addObject("templateList", templateList);
		}
		return modelAndView;
	}
	
	public ModelAndView initToolbar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/listToolBar");
		return modelAndView;
	}

	public ModelAndView deleteTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type = RequestUtils.getStringParameter(request, "type");
		String[] strIds = request.getParameterValues("id");
		//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询start
		if(type.equals("col") || "edoc".equals(type) || "info".equals(type)){
			//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询end
			//删除
			if (strIds != null && strIds.length != 0) {
				for (int i = 0; i < strIds.length; i++) {
					if (NumberUtils.isNumber(strIds[i])) {
						Long temId = new Long(strIds[i]);
						templeteManager.delete(temId);
					}
				}
			}
		}else if(type.equals("meeting")){
			//删除
			if (strIds != null && strIds.length != 0) {
				for (int i = 0; i < strIds.length; i++) {
					if (NumberUtils.isNumber(strIds[i])) {
						Long temId = new Long(strIds[i]);
						mtTemplateManagerCAP.delete(temId);
					}
				}
			}
		}else if(type.equals("inquiry")){
			//删除
			if (strIds != null && strIds.length != 0) {
				inquiryManager.removeTemplate(strIds);
			}
		}
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/sMyTemplate");
		return modelAndView;
	}
	
	public ModelAndView renameTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type = RequestUtils.getStringParameter(request, "type");
		String strId = RequestUtils.getStringParameter(request, "id");
		String newName = RequestUtils.getStringParameter(request, "newName");
        //更名
		//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询start
		if(type.equals("col") || "edoc".equals(type) || "info".equals(type)){		
			//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询end
			Templete templete = templeteManager.get(new Long(strId));
			templete.setSubject(newName);
			templeteManager.update(templete);
		}else if(type.equals("meeting")){
			Map<String, Object> colums = new HashMap<String, Object>();
			colums.put("title", newName);
			colums.put("templateName", newName);
			mtTemplateManagerCAP.update(new Long(strId), colums);
		}else if(type.equals("inquiry")){
			SurveyBasicCompose inquiryTemplate = inquiryManager.getTemplateListByID(new Long(strId),true);
			inquiryTemplate.getInquirySurveybasic().setSurveyName(newName);
			inquiryManager.updateSurveyBasic(inquiryTemplate.getInquirySurveybasic());
		}
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/sMyTemplate");
		return modelAndView;
	}
	
	public ModelAndView initRename(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("v3xmain/mytemplate/rename");
		String type = RequestUtils.getStringParameter(request, "type");	
		Long temId = new Long(RequestUtils.getStringParameter(request, "id"));
		String temName = null;
		//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询start
		if(type.equals("col") || "edoc".equals(type) || "info".equals(type)){
			//branches_a8_v350_r_gov GOV-2814 于荒津增加公文个人模版分类查询end
			temName = templeteManager.get(temId).getSubject();
		}else if(type.equals("meeting")){
			temName = mtTemplateManagerCAP.getById(temId).getTitle();
			modelAndView.addObject("personTemplateList",mtTemplateManagerCAP.findAllNoPaginate(Constants.MEETING_TEMPLATE_TYPE_PERSON));
		}else if(type.equals("inquiry")){
			temName = inquiryManager.getTemplateListByID(temId,true).getInquirySurveybasic().getSurveyName();
		}		
		modelAndView.addObject("temName",temName);
		return modelAndView;
	}
	
	public void setMtTemplateManagerCAP(MtTemplateManagerCAP mtTemplateManagerCAP) {
		this.mtTemplateManagerCAP = mtTemplateManagerCAP;
	}

	public TempleteManager getTempleteManager() {
		return templeteManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
}
