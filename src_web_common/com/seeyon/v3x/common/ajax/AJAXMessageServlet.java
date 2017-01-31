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

import com.seeyon.v3x.common.ServerState;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-6-9
 */
public class AJAXMessageServlet extends HttpServlet {

	private static final long serialVersionUID = -8096904944719448242L;
	
	private static final Log logc = LogFactory.getLog("capability");
	private static final Log log = LogFactory.getLog(AJAXMessageServlet.class);
	
	private transient UserMessageManager userMessageManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
		
		HttpSession session = request.getSession();
		
		CurrentUserToSeeyonApp.set(session);
		
		long startTime = System.currentTimeMillis();
		
		User user = CurrentUser.get();
		
		try {
			// 在线状态
			String message1 = CurrentUserToSeeyonApp.getUserOnlineMessage();

			if(message1 != null){
				//记录退出日志
				if(user != null){
					OnlineRecorder.logoutUser(user);
				}
				
				PrintWriter out = response.getWriter();
				out.println("[LOGOUT]" + message1);
				out.close();
				
				return;
			}
			
			ServerState serverState = ServerState.getInstance();
			if(serverState.isShutdown()){
				PrintWriter out = response.getWriter();
				if(serverState.isForceLogout()){
					if(user != null){
						OnlineRecorder.logoutUser(user);
					}
					
					//强制下线
					String _message = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", 
							"ServerState.shutdown", serverState.getComment());
					out.println("[LOGOUT]" + _message);
					out.close();
					
					return;
				}
				else if(serverState.isShutdownWarn(user.getId())){
					//给出警告
					int second = serverState.getShutdownTime();
					String _message = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", 
							"ServerState.shutdown.warn", second /60, second % 60, serverState.getComment());
					out.println("[LOGWARN]" + _message);
					out.close();
					return;
				}
			}
			
			if(userMessageManager == null){
				userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
			}
			
			PrintWriter out = response.getWriter();
			String o = userMessageManager.getNewMessagesAndOnlineSize();
			out.print(o);
			out.close();
		}
		catch (Exception e) {
			log.error("", e);
		}
		finally {
			if(logc.isDebugEnabled()){
				if(user != null){
					logc.debug(request.getRemoteAddr() + "," + user.getLoginName() + ",ReadMessage," + (System.currentTimeMillis() - startTime));
				}
				else{
					logc.debug(request.getRemoteAddr() + ",,ReadMessage," + (System.currentTimeMillis() - startTime));
				}
			}
			
			ThreadLocalUtil.removeThreadLocal();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
