/**
 * 
 */
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

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-13
 */
public class OfficeServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(OfficeServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 CurrentUserToSeeyonApp.set(request.getSession());
		
		ApplicationContext ctx = (ApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		HandWriteManager handWriteManager = (HandWriteManager) ctx.getBean("handWriteManager");
		DBstep.iMsgServer2000 msgObj = new DBstep.iMsgServer2000();
		try {
			handWriteManager.readVariant(request, msgObj);

			String option = msgObj.GetMsgByName("OPTION");
		
			if ("LOADFILE".equalsIgnoreCase(option)) {
				handWriteManager.LoadFile(msgObj);
			}
			else if ("SAVEFILE".equalsIgnoreCase(option)) {
				//检查客户端控件版本跟服务期控件版本是否一致
				String clientVer=msgObj.GetMsgByName("clientVer");
			    clientVer=clientVer.replace('.',',');
			    log.info("保存office正文的时候，插件版本校验,服务器handwrite版本为:["+msgObj.Version()+"] ,客户端版本为：["+clientVer +"]");
			    if(!msgObj.Version().equals(clientVer))
			    {
			     // log.error("保存office正文的时候，插件版本校验,服务器handwrite版本为:["+msgObj.Version()+"] ,客户端版本为：["+clientVer +"]不一致");
			      msgObj.MsgError("versionError");
			      msgObj.MsgTextClear();
			      msgObj.MsgFileClear();			    	
			    }
			    else
			    {
				  handWriteManager.saveFile(msgObj);
			    }
			}
			else if("LOADSIGNATURE".equalsIgnoreCase(option))
			{
				handWriteManager.LoadDocumentSinature(msgObj);
			}
			else if("LOADMARKLIST".equalsIgnoreCase(option))
			{
				handWriteManager.LoadSinatureList(msgObj);
			}
			else if("LOADMARKIMAGE".equalsIgnoreCase(option))
			{
				handWriteManager.LoadSinature(msgObj);
			}
			else if("LOADTEMPLATE".equalsIgnoreCase(option))
			{
				handWriteManager.taoHong(msgObj);
			}
			else if("SAVESIGNATURE".equalsIgnoreCase(option))
			{
				handWriteManager.saveDocumentSignatureRecord(msgObj,request);
			}
			else if("INSERTFILE".equalsIgnoreCase(option))
			{
				handWriteManager.LoadFile(msgObj);
				msgObj.SetMsgByName("POSITION","Content");		//设置插入的位置[书签对象名]
				msgObj.SetMsgByName("STATUS","插入文件成功!");      //设置状态信息
				msgObj.MsgError("");		                //清
			}else if("INSERTIMAGE".equalsIgnoreCase(option)){
				handWriteManager.insertImage(msgObj,request);
			}else if("SAVEPDF".equalsIgnoreCase(option)){//WOrd转PDF
				handWriteManager.saveFile(msgObj);
			}else if("PUTFILE".equalsIgnoreCase(option)) {
				handWriteManager.saveClientFile(msgObj);
			}
			handWriteManager.sendPackage(response, msgObj);
		}
		catch (Exception e) {
			log.error("",e);
			msgObj = new DBstep.iMsgServer2000();
			msgObj.MsgError("saveFaile");
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
