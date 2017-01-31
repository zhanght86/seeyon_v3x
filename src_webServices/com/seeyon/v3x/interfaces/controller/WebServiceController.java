package com.seeyon.v3x.interfaces.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.interfaces.WSCallBackService;
import com.seeyon.v3x.interfaces.dao.WebServiceDAO;
import com.seeyon.v3x.interfaces.domain.WebServiceRegister;
import com.seeyon.v3x.interfaces.domain.WebServiceResult;

public class WebServiceController extends BaseController{
	
	private WebServiceDAO webServiceDAO;
	
	private WSCallBackService wsCallBackService;
	
	private SearchManager searchManager;
	
	public void setWsCallBackService(WSCallBackService wsCallBackService) {
		this.wsCallBackService = wsCallBackService;
	}

	public void setWebServiceDAO(WebServiceDAO webServiceDAO) {
		this.webServiceDAO = webServiceDAO;
	}
	
	public ModelAndView toRegister(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv=new ModelAndView("webservice/add");
		return mv;
	}
	
	//注册
	//在调用web service之前首先必须注册回调地址，否则在发协同时就会报无回调地址的异常
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String typeStr=request.getParameter("appType");
		int appType=Integer.parseInt(typeStr);
		String wsUrl=request.getParameter("wsUrl");
		String remark=request.getParameter("remark");
		String loginId=request.getParameter("loginId");
		String password=request.getParameter("password");
		
		List list=webServiceDAO.find("from WebServiceRegister where loginId=?",loginId);
		if(list!=null&&list.size()>0){
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert("+"'重复的loginId！'"+")");
			out.println("</script>");
			return null;	
		}else{
		
		WebServiceRegister ws=new WebServiceRegister();
		ws.setAppType(appType);
		ws.setWsUrl(wsUrl);
		ws.setLoginId(loginId);
		ws.setPassword(password);
		ws.setStatus(1);
		ws.setRemark(remark);
		ws.setIdIfNew();
		webServiceDAO.save(ws);
		
		ModelAndView mv=new ModelAndView("webservice/add");
		mv.addObject("view", ws);
		return super.refreshWorkspace();
		}
	}

//	//查看所有记录 "from WebServiceRegister"
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv=new ModelAndView("webservice/view");
		List<WebServiceRegister> list=searchManager.searchByHql("from WebServiceRegister", null);

		mv.addObject("records", list);
		return mv;
	}
	//查看详细信息
	public ModelAndView viewDetial(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idstr=request.getParameter("registerId");
		long id=Long.parseLong(idstr);
//		System.out.println("The id in viewDetail be passed::"+id);
		WebServiceRegister ws=(WebServiceRegister) webServiceDAO.load(WebServiceRegister.class, id);
		ModelAndView mv=new ModelAndView("webservice/edit");
		mv.addObject("view", ws);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			mv.addObject("readOnly", readOnly);
		}
		return mv;
	}
	
//	edit操作
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		System.out.println("here in the update method!!");
		try {
			String idstr = request.getParameter("id");
			String loginId = request.getParameter("loginId");
			long id = Long.parseLong(idstr);
			WebServiceRegister ws = (WebServiceRegister) webServiceDAO.load(
					WebServiceRegister.class, id);
			if (!loginId.equals(ws.getLoginId())) {
				List list = webServiceDAO.find(
						"from WebServiceRegister where loginId=?", loginId);
				if (list != null && list.size() > 0) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(" + "'重复的loginId！'" + ")");
					out.println("</script>");
					return null;
				}
			}

			bind(request, ws);
			webServiceDAO.update(ws);
			ModelAndView mv = new ModelAndView("webservice/detail");
			mv.addObject("view", ws);
		} catch (RuntimeException e) {

			e.printStackTrace();
		}
		return super.refreshWorkspace();
	}
	/*
	 * 删除所选择的记录
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response){
		String idstr=request.getParameter("registerId");
		long id=Long.parseLong(idstr);
		WebServiceRegister ws=(WebServiceRegister) webServiceDAO.load(WebServiceRegister.class, id);
		webServiceDAO.delete(ws);
		
		return  super.refreshWorkspace();
	}
	/*
	 * 测试回调方式
	 */
	public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String webserviceCode="7165866786053900867";
		WebServiceResult result=new WebServiceResult();
		result.setApp("dddd");
		result.setSubject("ceshi!");
		result.setApplicant("lixin");
		Date date=new Date();
		result.setDate(date);
		result.setResult(" ok,pass!");
		wsCallBackService.callBack(webserviceCode, result);
		return null;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv=new ModelAndView("webservice/viewBorderFrame");
		
		return mv;
	}
	public ModelAndView indexBorder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv=new ModelAndView("webservice/listFrame");
		
		return mv;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	
	

}
