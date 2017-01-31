package com.seeyon.v3x.indexresume.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.IndexApplicationCategoryUtil;
import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexresume.domain.IndexResumeInfo;
import com.seeyon.v3x.indexresume.manager.IndexResumeTaskManager;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * @author zhangyong
 *
 */
public class IndexResumeController extends BaseController {
	private IndexResumeTaskManager indexResumeTaskManager;
	private List<String> startHourList;
	private List<String> startMinList;
	private IndexApplicationCategoryUtil indexApplicationCategoryUtil;
	public void setIndexApplicationCategoryUtil(
			IndexApplicationCategoryUtil indexApplicationCategoryUtil) {
		this.indexApplicationCategoryUtil = indexApplicationCategoryUtil;
	}
	public void setIndexResumeTaskManager(
			IndexResumeTaskManager indexResumeTaskManager) {
		this.indexResumeTaskManager = indexResumeTaskManager;
	}
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return super.index(request, response);
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView showSettingPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("indexresume/setResume");
		showPageInfo(mav);
		String indexUpgrade =null;
		IndexManager indexManager = (IndexManager) ApplicationContextHolder.getBean("indexManager");
		//查看是否存在全文检索权限库
		if(indexManager.isExistIndexAuthor()){
			//查看全文检索升级标记
			mav.addObject("indexUpgrade",indexManager.isIndexUpgraded());
		}
		mav.addObject("resumeInfo",indexResumeTaskManager.getResumeInfo());
		mav.addObject("isShowHistory","YES");
		return mav;
	}
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView saveConfig(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("indexresume/setResume");
		
		IndexResumeInfo resumeInfo=new IndexResumeInfo();
		
		super.bind(request, resumeInfo);
		
		indexResumeTaskManager.saveConfig(resumeInfo);
	
		mav.addObject("resumeInfo",resumeInfo);
		showPageInfo(mav);
		return super.refreshWorkspace();
	}
	private void showPageInfo(ModelAndView mav) {
		List<Integer> appLibs=new ArrayList<Integer>(indexApplicationCategoryUtil.getAllAppInt());
		mav.addObject("appLibs",appLibs);
//		List<String> starthourList=new  ArrayList<String>();
		//开始时间
//		for (int i = 0; i < 24; i++) {
//			starthourList.add(i+"");
//		}
		mav.addObject("starthourList",startHourList);
		mav.addObject("startMinList",startMinList);
	}
    public List<String> getStartHourList() {
		return startHourList;
	}
	public void setStartHourList(List<String> startHourList) {
		this.startHourList = startHourList;
	}
	public List<String> getStartMinList() {
		return startMinList;
	}
	public void setStartMinList(List<String> startMinList) {
		this.startMinList = startMinList;
	}
}
