package com.seeyon.v3x.news.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.news.dao.NewsTemplateDao;
import com.seeyon.v3x.news.domain.NewsTemplate;
import com.seeyon.v3x.news.manager.NewsTemplateManager;
import com.seeyon.v3x.news.util.NewsUtils;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 新闻版面的Controller
 * @author wolf
 *
 */
public class NewsTemplateController extends BaseController {
	private NewsTemplateManager newsTemplateManager;
	private NewsTemplateDao newsTemplateDao;
	
	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}


	public void setNewsTemplateManager(NewsTemplateManager newsTemplateManager) {
		this.newsTemplateManager = newsTemplateManager;
	}

	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public NewsTemplateDao getNewsTemplateDao() {
		return newsTemplateDao;
	}


	public void setNewsTemplateDao(NewsTemplateDao newsTemplateDao) {
		this.newsTemplateDao = newsTemplateDao;
	}

	/**
	 * 创建新闻版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/admin/template_create");
		NewsTemplate bean=new NewsTemplate();
		bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		
		
		mav.addObject("bean", bean);
		return mav;
	}
	
	/**
	 * 编辑新闻版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		NewsTemplate bean;
		if(StringUtils.isBlank(idStr)){
			bean=new NewsTemplate();	
			bean.setUsedFlag(true);
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		}else
			bean=newsTemplateManager.getById(Long.valueOf(idStr));
		
		ModelAndView mav = new ModelAndView("news/admin/template_create");
		mav.addObject("bean", bean);
				
		return mav;
	}
	
	/**
	 * 保存新闻版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		NewsTemplate bean=null;
		String idStr=request.getParameter("id");
		Long UserId = CurrentUser.get().getId();
		Long accountId = CurrentUser.get().getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
        
        try { 
        	if(StringUtils.isBlank(idStr)){
    			bean=new NewsTemplate();		
    		}else{
    			bean=newsTemplateManager.getById(Long.valueOf(idStr));
    		}
                
        	NewsTemplate temp=new NewsTemplate(); 
                super.bind(request, temp); 
                temp.setAccountId(bean.getAccountId()); 
                if(temp.getAccountId()==null){ 
                	temp.setAccountId(accountId); 
                } 
                
                if(newsTemplateDao.isNotUnique(temp, "templateName,accountId")){ 
                        throw new BulletinException("news_alreay_exists",temp.getTemplateName()); 
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
        
                newsTemplateManager.save(bean); 
        } catch (BusinessException e) {                 
//        	ModelAndView mav = new ModelAndView("news/admin/template_create");
//			mav.addObject("bean", bean);
					
			request.getSession().setAttribute("_my_exception", e);
//			return mav;
			return this.redirectModelAndView("/newsTemplate.do?method=create");
        } 

        return this.redirectModelAndView("/newsTemplate.do?method=listMain");

	}
	
	
	/**
	 * 删除新闻版面，支持批量删除
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
				newsTemplateManager.deletes(ids);
		}
		
		return this.redirectModelAndView("/newsTemplate.do?method=listMain");
	}
	
	/**
	 * 新闻版面列表主页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/admin/template_list_main");
		return mav;
	}

	/**
	 * 新闻版面列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<NewsTemplate> list=null;
		
		Long accountId = CurrentUser.get().getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)){
			Object value=NewsUtils.getPropertyObject(NewsTemplate.class, condition, textfield);
			if(account.getIsRoot()){
				list=newsTemplateManager.findGroupByProperty(condition, value);//集团版面
        	}else{
        		list=newsTemplateManager.findByProperty(condition, value);//单位版面
        	}
			
		}else{
			if(account.getIsRoot()){
				list=newsTemplateManager.findGroupAll();//集团版面
        	}else{
        		list=newsTemplateManager.findAll();//单位版面
        	}
			
		}
		
		
		ModelAndView mav = new ModelAndView("news/admin/template_list_iframe");
		mav.addObject("list", list);
		return mav;
	}
	
	/**
	 * 显示新闻版面详细页面，或预览新闻版面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		NewsTemplate bean=null;
		if(StringUtils.isBlank(idStr)){
			bean=new NewsTemplate();
		}else{
			bean=newsTemplateManager.getById(Long.valueOf(idStr));
		}
		
		String view="news/admin/template_list_detail_iframe";
		if(request.getParameter("preview")!=null)
			view="news/admin/template_preview";
		
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("bean", bean);
		return mav;
	}
	
}
