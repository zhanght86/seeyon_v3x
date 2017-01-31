package com.seeyon.v3x.common.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.ajax.impl.AJAXRequestImpl;
import com.seeyon.v3x.common.ajax.impl.AJAXResponseMobileWrapperImpl;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-16
 */
public class AJAXDataServlet extends HttpServlet {
	
	private static final long serialVersionUID = 3573767581315962977L;
	
	private static final String CONTENT_TYPE_XML = "text/xml; charset=UTF-8";
	private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
	
	private static final Log log = LogFactory.getLog(AJAXDataServlet.class);
	private static final Log logc = LogFactory.getLog("capability");

	private transient AJAXService ajaxService;

	// Initialize global variables
	public void init() throws ServletException {
	}

	// Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
		
		HttpSession session = request.getSession();
		
		CurrentUserToSeeyonApp.set(session);
		
		long startTime = System.currentTimeMillis();
			
		String serviceName = request.getParameter("S");
		String methodName = request.getParameter("M");
		String returnValueType = request.getParameter("RVT");
		
		if(Strings.isBlank(serviceName) || "null".equalsIgnoreCase(serviceName)
				|| Strings.isBlank(methodName) || "null".equalsIgnoreCase(methodName)){
			response.setContentType(CONTENT_TYPE_HTML);
			
			log.error("AJAX Service error. Http Request Parameter is empty!");
			response.sendError(500, "Http Request Parameter is empty!");
			return;
		}
		
		try {
			if (ajaxService == null) {
				ajaxService = (AJAXService) ApplicationContextHolder.getBean("AJAXService");
			}
			
			boolean needCheckLogin = Boolean.parseBoolean(request.getParameter("CL"));
			
			if(needCheckLogin){
				// 在线状态
				String message1 = CurrentUserToSeeyonApp.getUserOnlineMessage();

				if(message1 != null){
					//记录退出日志
					User user = CurrentUser.get();
					if(user != null){
						OnlineRecorder.logoutUser(user);						
					}
					
					response.setContentType(CONTENT_TYPE_HTML);
					
					PrintWriter out = response.getWriter();
					out.println("[LOGOUT]" + message1);
					out.close();
					
					return;
				}
			}
			
			if("XML".equals(returnValueType)){
				response.setContentType(CONTENT_TYPE_XML);
			}
			else{
				response.setContentType(CONTENT_TYPE_HTML);
			}
			
	        WebUtil.setRequest(request);
	        WebUtil.setResponse(response); 

			AJAXRequest ajaxRequest = new AJAXRequestImpl(request, response, serviceName, methodName);
			AJAXResponse ajaxResponse = ajaxService.processRequest(ajaxRequest);
			String callback = ajaxRequest.getServletRequest().getParameter("callback");
			if(callback != null){
				AJAXResponse wrapper = new AJAXResponseMobileWrapperImpl(ajaxRequest, ajaxResponse);
				wrapper.complete(returnValueType); 
			}else {
				ajaxResponse.complete(returnValueType);
			}
		}
		catch (Exception ex) {
			response.setContentType(CONTENT_TYPE_HTML);
			log.error("AJAX Service error.", ex);
			response.sendError(500, "AJAX Service error. Cause: " + ex.getMessage());
		}
		finally{
			if(logc.isDebugEnabled()){
				User user = CurrentUser.get();
				if(user != null){
					logc.debug(request.getRemoteAddr() + "," + user.getLoginName() + ",[AJAX]" + serviceName + "." + methodName + "," + (System.currentTimeMillis() - startTime));
				}
				else{
					logc.debug(request.getRemoteAddr() + ",,[AJAX]" + serviceName + "." + methodName + "," + (System.currentTimeMillis() - startTime));
				}
			}
			
			ThreadLocalUtil.removeThreadLocal();
		}
	}

	// Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// Clean up resources
	public void destroy() {
	}

}
