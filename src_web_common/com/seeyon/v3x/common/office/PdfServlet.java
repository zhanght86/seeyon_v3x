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

public class PdfServlet  extends HttpServlet{
	private static Log log = LogFactory.getLog(PdfServlet.class);
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 CurrentUserToSeeyonApp.set(request.getSession());
			
			ApplicationContext ctx = (ApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			HandWriteManager handWriteManager = (HandWriteManager) ctx.getBean("handWriteManager");
			PdfHandWriteManager PdfHandWriteManager=(PdfHandWriteManager)ctx.getBean("pdfHandWriteManager");
			DBstep.iMsgServer2000 msgObj = new DBstep.iMsgServer2000();
			try {
				handWriteManager.readVariant(request, msgObj);

				String option = msgObj.GetMsgByName("OPTION");
			
				if ("LOADFILE".equalsIgnoreCase(option)) {
					msgObj.Charset = "UTF-8";
					handWriteManager.LoadFile(msgObj);
				}
				if("SAVEFILE".equalsIgnoreCase(option)){
					handWriteManager.saveFile(msgObj);
				}
				handWriteManager.sendPackage(response, msgObj);
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
