package com.seeyon.v3x.isearch.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ISearchAppObject;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.isearch.manager.ISearchManagerRegister;

/**
 * 2008.03.17 
 * @author lihf
 * 综合查询Controller
 */
public class ISearchController extends BaseController {
	private DocLibManager docLibManager;
	
	private HisColManager hisColManager;

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	
	public void setHisColManager(HisColManager hisColManager) {
		this.hisColManager = hisColManager;
	}

	/**
	 * 进入综合查询首页
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret =null;
		String page = request.getParameter("page");
		//判断是否有全文检索
		boolean flag = IndexInitConfig.hasLuncenePlugIn();
		if(!flag){
			//如果没有直接进入home页面
			page="content";
		}
		if("content".equals(page)){
			ret = new ModelAndView("isearch/home");
			// 1. 返回类型
			List<ISearchAppObject> appList = ISearchManagerRegister.getISearchAppObjectList();
			ret.addObject("appList", appList);
			User user = CurrentUser.get();
			List<DocLib> libs = docLibManager.getDocLibsByUserIdNav(user.getId(), user.getLoginAccount());
			ret.addObject("libs", libs);
			if(!flag){
				ret.addObject("search", false);
			}
		}else{
			ret = new ModelAndView("index/index");
			ret.addObject("searchType", "1");
		}
		return ret;
	}
	
	/**
	 * 综合查询主方法
	 */
	public ModelAndView iSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("isearch/dataList");
		// 1. 返回类型
//		List<ISearchAppObject> appList = ISearchManagerRegister.getISearchAppObjectList();
//		ret.addObject("appList", appList);
//		User user = CurrentUser.get();
//		List<DocLib> libs = docLibManager.getDocLibsByUserIdNav(user.getId(), user.getLoginAccount());
//		ret.addObject("libs", libs);
	
		// 1. 组装条件
		ConditionModel cm = new ConditionModel();
		cm.setUser(CurrentUser.get());
		super.bind(request, cm);
		
		if(cm.getEndDate() != null){
			cm.setEndDate(new Timestamp(cm.getEndDate().getTime() + 24 * 60 * 60 * 1000 - 1));
		}
		
		ret.addObject("cm", cm);
		
//		if(cm.getBeginDate() != null)
//			ret.addObject("beginDateValue", Datetimes.formatDate(cm.getBeginDate()));
//		if(cm.getEndDate() != null)
//			ret.addObject("endDateValue", Datetimes.formatDate(cm.getEndDate()));
		
		cm.setAppObj(ISearchManagerRegister.getAppObjByAppKey(cm.getAppKey()));		
		ISearchManager manager = ISearchManagerRegister.getISearchManagerByAppKey(cm.getAppKey());
		if(cm.getPigeonholedFlag()){//归档的单独处理
			manager = ISearchManagerRegister.getISearchManagerByAppKey(ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY);
		}
		
		List<ResultModel> list = null;
		if(cm.getAppKey().equals("1") && request.getParameterValues("dumpData") != null){ //转储数据
			list = this.hisColManager.iSearch(cm);
		}
		else{
			if(manager != null)
				list = manager.iSearch(cm);
		}
		ret.addObject("list", list);
		// 2. 区分类型，找到争取的manager
		// 3. 调用对应manager实现分页查询
		// 4. 数据返回
		return ret;
	}

}
