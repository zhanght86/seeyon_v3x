/**
 * 
 */
package com.seeyon.v3x.common.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.util.Cookies;
import com.seeyon.v3x.util.Strings;

/**
 * 通用的Servlet框架，当你需要servlet的时候可以用它（这种做法A8不推荐，可以使用Controller来实现），<b>不需要在web.xml去配置</b>，使用步骤：<br>
 * 
 * 1. 实现接口<code>com.seeyon.v3x.common.web.GenericServletInterface</code>，方法有doGet, doPost<br>
 * 2. 访问方法：http://<ip>:<port>/seeyon/genericServlet?class={你的class名称}[&needCheckLogin=true|false]<br>
 * 
 * URL的参数含义
 * 
 * <table border="1">
 * 	<tr>
 * 	  <td width="100">参数名</td>
 * 	  <td width="200">描述</td>
 * 	  <td width="100">必填</td>
 * 	  <td width="100">默认值</td>
 * 	</tr>
 * 	<tr>
 * 	  <td>class</td>
 * 	  <td>实现接口<code>com.seeyon.v3x.common.web.GenericServletInterface</code>的类的className</td>
 * 	  <td>是</td>
 * 	  <td>-</td>
 * 	</tr>
 * 	<tr>
 * 	  <td>needCheckLogin</td>
 * 	  <td>是否需要进行登录验证，如果没有登录，则提示，然后回到登录窗口</td>
 * 	  <td>否</td>
 * 	  <td>true</td>
 * 	</tr>
 * </table>
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-9-28
 */
public class GenericServlet extends HttpServlet {
	private static final long serialVersionUID = 7456172563977162614L;

	private static final Log log = LogFactory.getLog(GenericServlet.class);
	
	private static final Log logc = LogFactory.getLog("capability");
	
	private Map<String, GenericServletInterface> cache = new HashMap<String, GenericServletInterface>();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doSomething(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doSomething(request, response);
	}
	
	private void doSomething(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String className = request.getParameter("class");
		if(Strings.isBlank(className)){
			log.error("GenericServlet没有指定class，" + request.getParameterMap());
			return;
		}
		
		GenericServletInterface g = cache.get(className);
		if(g == null){
			try {
				Object temp = Class.forName(className).newInstance();
				if(temp instanceof GenericServletInterface){
					g = (GenericServletInterface)temp;
				}
				else{
					log.error(className + " 没有实现" + GenericServletInterface.class.getCanonicalName());
					return;
				}
			}
			catch (Throwable e) {
				log.error("newInstance : " + className, e);
				return;
			}
			
			cache.put(className, g);
		}
		
		long startTime = System.currentTimeMillis();
		
		String needCheckLogin = request.getParameter("needCheckLogin");
		
		HttpSession session = request.getSession();
		
		CurrentUserToSeeyonApp.set(session);
		
		if(!"false".equalsIgnoreCase(needCheckLogin)){
			// 在线状态
			String message1 = CurrentUserToSeeyonApp.getUserOnlineMessage();

			if(message1 != null){
				PrintWriter out = response.getWriter();
				User currentUser = CurrentUser.get();
				String fromCookies = Cookies.get(request, "u_login_from");				
				//手机登录
				if(Constants.login_useragent_from.mobile.name().equals(fromCookies) || (currentUser != null && Constants.login_useragent_from.mobile.name().equals(currentUser.getUserAgentFrom()))){
					out.println("<meta http-equiv='Refresh' content='0;url=" + request.getContextPath() + "/common/mobileprompt.jsp?message=" + java.net.URLEncoder.encode(message1, "UTF-8") + "' />");
				}
				else{
					out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + SystemEnvironment.getA8ContextPath() + "/common/js/V3X.js\"></script>");
					out.println("<script>");
					out.println("alert(\"" + Strings.escapeJavascript(message1) + "\");");
					out.println("self.close();");
					out.println("getA8Top().location.href = '/login/logout';");
					out.println("</script>");
				}
				
				out.close();
				return;
			}
		}
		
		try {
			g.doGet(super.getServletConfig(), super.getServletContext(), request, response);
		}
		catch (Throwable e) {
			log.error("", e);
		}
		finally{
			if(logc.isDebugEnabled()){
				User user = CurrentUser.get();
				if(user != null){
					logc.debug(user.getLoginName() + ", " + user.getName() +", GenericServlet[" + className + "] , " + (System.currentTimeMillis() - startTime));
				}
				else{
					logc.debug(" , , GenericServlet[" + className + "], " + (System.currentTimeMillis() - startTime));
				}
			}
			
			ThreadLocalUtil.removeThreadLocal();
		}
	}

}
