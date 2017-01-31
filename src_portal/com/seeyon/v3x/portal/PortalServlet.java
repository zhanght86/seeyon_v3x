package com.seeyon.v3x.portal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.LayoutConstants;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.portal.decorations.PortalDecoration;
import com.seeyon.v3x.portal.decorations.PortalDecorationManager;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpacePage;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author Dongyj
 * @version 1.0 2010-11-23
 */
public class PortalServlet extends HttpServlet{
	private static final long serialVersionUID = 1658072950847525441L;
	
	private static final Log log = LogFactory.getLog(PortalServlet.class);
	
	private static final Log logc = LogFactory.getLog("capability");
	
	private transient SpaceManager spaceManager;
	
	public void init() throws ServletException {
		super.init();
	}
	public static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	
	/**
	 * <pre>
	 * 输出
	 *	List<List<Fragment>>,
	 *  layoutType, 
	 *  decoration, 修饰
	 *  path,   psml的path
	 *	banner, 头部
	 *	slogn   口号
	 * </pre>
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		
		String contextPath = request.getContextPath();
		response.setContentType(CONTENT_TYPE);
		
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		HttpSession session = request.getSession();
		CurrentUserToSeeyonApp.set(session);	
		
		String message1 = CurrentUserToSeeyonApp.getUserOnlineMessage();
		
		if(message1 != null){
			PrintWriter out = response.getWriter();
            out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + contextPath + "/common/js/V3X.js\"></script>");
			out.println("<script>");
			out.println("getA8Top().showLogoutMsg(\"" + Strings.escapeJavascript(message1) + "\");");
			out.println("</script>");
			out.close();
			return;
		}
		
		String path = request.getRequestURI();
		String showState = request.getParameter("showState");
		/*
		 * 根据不同的用户设置首页展示状态，针对拖拽修改
		 */
		User user = CurrentUser.get();
		if(showState==null){
			if(user.isAdministrator()||user.isGroupAdmin()){
				showState = "show";
			}else{
				showState = "view";
			}
		}
		if(spaceManager == null){
			spaceManager = (SpaceManager)ApplicationContextHolder.getBean("spaceManager");
		}
		request.setAttribute("showState", showState);
		String editKeyId = request.getParameter("editKeyId");
		boolean isEdit = PortalConstants.isEdit(request);
		
		if(showState.equals("edit") ||showState.equals("personEdit")){
			if(Strings.isNotBlank(editKeyId)){
				request.setAttribute("editKeyId",editKeyId);
				spaceManager.addEditKeyCache(Long.valueOf(editKeyId), path, user.getId());
			}else{
				editKeyId = String.valueOf(UUIDLong.longUUID());
				spaceManager.addEditKeyCache(Long.valueOf(editKeyId), path, user.getId());
				request.setAttribute("editKeyId", editKeyId);
			}
		}else{
			spaceManager.removeEditKeyCache(user.getId());
		}
		String isChangedIndex = request.getParameter("isChangedIndex");
		if(Strings.isNotBlank(isChangedIndex)){
			request.setAttribute("isChangedIndex", isChangedIndex);
		}
		
		String toDefault = request.getParameter("toDefault");
		if(Strings.isNotBlank(toDefault)){
			request.setAttribute("toDefault", toDefault);
		}
		try{
			PortalConstants.initPortalData(request, path, spaceManager,editKeyId,user.getId());
			if(!isEdit){
				SpacePage page = (SpacePage)request.getAttribute(PortalConstants.PAGE);
				if(page == null){
					PrintWriter out = response.getWriter();
		            out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + contextPath + "/common/js/V3X.js\"></script>");
					out.println("<script>");
					//刷新空间
					out.println("alert(\"" + Strings.escapeJavascript(Constants.getValueOfKey("space.notExist.label")) + "\");");
					if(user != null){
						out.println("getA8Top().contentFrame.topFrame.realignAndBack('" + user.getLoginAccount() + "');\n");
					}
					out.println("</script>");
					out.close();
					return;
				}
			}
			PortalDecoration  decoration = (PortalDecoration) request.getAttribute(PortalConstants.DECORATION);
			RequestDispatcher dispatcher = request.getRequestDispatcher(decoration.getViewPath());
			
			request.setAttribute("allLayout", PortalDecorationManager.getAllLayoutType());
			request.setAttribute("layoutTypes", LayoutConstants.lagoutToDecorations);
			
			dispatcher.forward(request, response);
		}
		catch (SpaceException e) {
			log.error("首页，取得空间布局："+path,e);
		}
		finally{
			if(logc.isDebugEnabled()){
				if(user != null){
					logc.debug(request.getRemoteAddr() + "," + user.getLoginName() + ",PortalServlet," + (System.currentTimeMillis() - startTime));
				}
				else{
					logc.debug(request.getRemoteAddr() + ",PortalServlet," + (System.currentTimeMillis() - startTime));
				}
			}
			ThreadLocalUtil.removeThreadLocal();
		}
	}
	
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doGet(arg0, arg1);
	}
}