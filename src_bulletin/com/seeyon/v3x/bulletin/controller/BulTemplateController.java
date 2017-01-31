package com.seeyon.v3x.bulletin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.dao.BulTemplateDao;
import com.seeyon.v3x.bulletin.domain.BulTemplate;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulTemplateManager;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 公告版面的Controller
 * @author wolf
 *
 */
public class BulTemplateController extends BaseController {
	private BulTemplateManager bulTemplateManager;
	private BulTemplateDao bulTemplateDao;
		
	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public BulTemplateDao getBulTemplateDao() {
		return bulTemplateDao;
	}


	public void setBulTemplateDao(BulTemplateDao bulTemplateDao) {
		this.bulTemplateDao = bulTemplateDao;
	}


	public void setBulTemplateManager(BulTemplateManager bulTemplateManager) {
		this.bulTemplateManager = bulTemplateManager;
	}

	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	

	/**
	 * 创建公告版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/admin/template_create");
		BulTemplate bean=new BulTemplate();
		bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		
		mav.addObject("bean", bean);
		return mav;
	}
	
	/**
	 * 编辑公告版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		BulTemplate bean;
		if(StringUtils.isBlank(idStr)){
			bean=new BulTemplate();	
			bean.setUsedFlag(true);
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		}else
			bean=bulTemplateManager.getById(Long.valueOf(idStr));
		
		ModelAndView mav = new ModelAndView("bulletin/admin/template_create");
		mav.addObject("bean", bean);
				
		return mav;
	}
	
	/**
	 * 保存公告版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		BulTemplate bean=null;
		String idStr=request.getParameter("id");
		Long UserId = CurrentUser.get().getId();
		Long accountId = CurrentUser.get().getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
        
        try { 
        	if(StringUtils.isBlank(idStr)){
    			bean=new BulTemplate();		
    		}else{
    			bean=bulTemplateManager.getById(Long.valueOf(idStr));
    		}
                
        	BulTemplate temp=new BulTemplate(); 
                super.bind(request, temp); 
                temp.setAccountId(bean.getAccountId()); 
                if(temp.getAccountId()==null){ 
                        temp.setAccountId(accountId); 
                } 
                
                if(bulTemplateDao.isNotUnique(temp, "templateName,accountId")){ 
                        throw new BulletinException("bulletin_Template_notAlreay",temp.getTemplateName()); 
                } 
                
                
                super.bind(request,bean); 
                
                if(bean.isNew()){ 
                        bean.setCreateDate(new Date()); 
                        bean.setCreateUser(UserId); 
                        if(account.getIsRoot()){
                            bean.setExt1("0");//用扩展字段设置集团标志
                    	}  
                } 
                bean.setUpdateDate(new Date()); 
                bean.setUpdateUser(UserId); 
                
                if(!bean.isUsedFlag()){ 
                        bean.setUsedFlag(true); 
                }               
        
                bulTemplateManager.save(bean); 
        } catch (BusinessException e) {                 
//        	ModelAndView mav = new ModelAndView("bulletin/admin/template_create");
//			mav.addObject("bean", bean);
					
			request.getSession().setAttribute("_my_exception", e);
//			return mav;
			return this.redirectModelAndView("/bulTemplate.do?method=create");
        } 

        return this.redirectModelAndView("/bulTemplate.do?method=listMain");

	}
	
	
	/**
	 * 删除公告版面，支持批量删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		if(StringUtils.isBlank(idStr)){
			idStr="";		
		}else{
			String[] idStrs=idStr.split(",");
			List<Long> ids=new ArrayList<Long>();
			for(String str:idStrs){
				if(StringUtils.isNotBlank(str)){
					ids.add(Long.valueOf(str));
				}
			}
			if(ids.size()>0)
				bulTemplateManager.deletes(ids);
		}
		
		return this.redirectModelAndView("/bulTemplate.do?method=listMain");
	}
	
	/**
	 * 公告版面列表主页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/admin/template_list_main");
		return mav;
	}

	/**
	 * 公告版面列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<BulTemplate> list=null;
		
		Long accountId = CurrentUser.get().getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)){
			Object value=BulletinUtils.getPropertyObject(BulTemplate.class, condition, textfield);
			if(account.getIsRoot()){
				list=bulTemplateManager.findGroupByProperty(condition, value);//集团版面
        	}else{
        		list=bulTemplateManager.findByProperty(condition, value);//单位版面
        	}
		}else{
			if(account.getIsRoot()){
				list=bulTemplateManager.findGroupAll();//集团版面
        	}else{
        		list=bulTemplateManager.findAll();//单位版面
        	}
			
		}
		
		
		ModelAndView mav = new ModelAndView("bulletin/admin/template_list_iframe");
		mav.addObject("list", list);
		return mav;
	}
	
	/**
	 * 显示公告版面详细页面，或预览公告版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		BulTemplate bean=null;
		if(StringUtils.isBlank(idStr)){
			bean=new BulTemplate();
		}else{
			bean=bulTemplateManager.getById(Long.valueOf(idStr));
		}
		
		String view="bulletin/admin/template_list_detail_iframe";
		if(request.getParameter("preview")!=null)
			view="bulletin/admin/template_preview";
		
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("bean", bean);
		return mav;
	}
	
	
}
