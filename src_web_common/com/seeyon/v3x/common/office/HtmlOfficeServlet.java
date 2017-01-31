package com.seeyon.v3x.common.office;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;

public class HtmlOfficeServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(HtmlOfficeServlet.class);
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		CurrentUserToSeeyonApp.set(request.getSession());
		
		ApplicationContext ctx = (ApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		HandWriteManager handWriteManager = (HandWriteManager) ctx.getBean("handWriteManager");
		HtmlHandWriteManager htmlHandWriteManager = (HtmlHandWriteManager) ctx.getBean("htmlHandWriteManager");

		DBstep.iMsgServer2000 msgObj = new DBstep.iMsgServer2000();
		try {
			handWriteManager.readVariant(request, msgObj);
		
			msgObj.SetMsgByName("CLIENTIP", request.getRemoteAddr());

			String option = msgObj.GetMsgByName("OPTION");
		
			if ("LOADFILE".equalsIgnoreCase(option)) {
				handWriteManager.LoadFile(msgObj);
			}			
			else if("LOADSIGNATURE".equalsIgnoreCase(option))
			{
				htmlHandWriteManager.loadDocumentSinature(msgObj);
			}
			else if("LOADMARKLIST".equalsIgnoreCase(option))
			{
				handWriteManager.LoadSinatureList(msgObj);
			}
			else if("SIGNATRUEIMAGE".equalsIgnoreCase(option))
			{
				handWriteManager.LoadSinature(msgObj);
			}			
			else if("SAVESIGNATURE".equalsIgnoreCase(option))
			{
				htmlHandWriteManager.saveSignature(msgObj);
			}
			else if("SAVEHISTORY".equalsIgnoreCase(option))
			{
				htmlHandWriteManager.saveSignatureHistory(msgObj);
			}
			else if("SIGNATRUELIST".equalsIgnoreCase(option))
			{//调入印章列表
				handWriteManager.LoadSinatureList(msgObj);
			}
			else if("SHOWHISTORY".equalsIgnoreCase(option))
			{
				htmlHandWriteManager.getSignatureHistory(msgObj);
			}

			handWriteManager.sendPackage(response, msgObj);
		}
		catch (Exception e) {
			log.error("",e);
			msgObj = new DBstep.iMsgServer2000();
			msgObj.MsgError("htmoffice operate err");
			handWriteManager.sendPackage(response, msgObj);
		}
		
		ThreadLocalUtil.removeThreadLocal();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
