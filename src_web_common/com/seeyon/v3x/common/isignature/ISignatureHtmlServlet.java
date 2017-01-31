package com.seeyon.v3x.common.isignature;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.seeyon.v3x.common.isignature.domain.ISignatureHtml;
import com.seeyon.v3x.common.office.HandWriteManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ISignatureHtmlServlet extends HttpServlet{
	
	private static Log log = LogFactory.getLog(ISignatureHtmlServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		CurrentUserToSeeyonApp.set(request.getSession());
        try{
			ApplicationContext ctx = (ApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			ISignatureHtmlManager iSignatureHtmlManager = (ISignatureHtmlManager) ctx.getBean("iSignatureHtmlManager");
			
			///String iSignatureId = new String(request.getParameter("SIGNATUREID").getBytes("8859_1"));
			String mCommand = request.getParameter("COMMAND");
			PrintWriter  out = response.getWriter(); 
			String documentId = new String(request.getParameter("DOCUMENTID").getBytes("8859_1"));
			if("SAVESIGNATURE".equalsIgnoreCase(mCommand)){
				//保存
				ISignatureHtml iSignatureHtml = getISignatureHtmlFormRequest(request);
				ISignatureHtml old = iSignatureHtmlManager.get(iSignatureHtml.getId());
				if(old == null){
					iSignatureHtmlManager.save(iSignatureHtml);
				}else{
					iSignatureHtmlManager.update(iSignatureHtml);
				}
				
				
				
				out.print("SIGNATUREID="+iSignatureHtml.getId()+"\r\n");
				out.print("RESULT=OK");
				out.flush();
			}else if("SHOWSIGNATURE".equalsIgnoreCase(mCommand)){
				
				String ids = iSignatureHtmlManager.LoadISignatureByDocumentId(Long.valueOf(documentId));
				
				//调出文档的所有的签章数据
				out.print("SIGNATURES="+ids+"\r\n");
				out.print("RESULT=OK");
				out.flush();
			}else if("GETNOWTIME".equals(mCommand)){
				
				Calendar cal  = Calendar.getInstance();
				String mDateTime=Datetimes.formatDatetime(cal.getTime());
				out.print("NOWTIME="+mDateTime+"\r\n");
				out.print("RESULT=OK");
				out.flush();
			}else if("LOADSIGNATURE".equals(mCommand)){
				String isid =  request.getParameter("SIGNATUREID");
				ISignatureHtml iSignatureHtml =  iSignatureHtmlManager.get(Long.valueOf(isid));
				byte[] b = IOUtils.toByteArray(iSignatureHtml.getSignature().getBinaryStream());
				String signature = new String(b);
				out.print(signature+"\r\n");
				out.print("RESULT=OK");
				out.flush();
			}else if("DELESIGNATURE".equalsIgnoreCase(mCommand)){   //删除签章数据信息
				String isid =  request.getParameter("SIGNATUREID");
				if(Strings.isNotBlank(isid)){
					iSignatureHtmlManager.delete(Long.valueOf(isid));
				}
				out.print("RESULT=OK");
				out.flush();
			}
        }catch(Exception e){
			log.error("网页专业签章：",e);
		}finally{
			ThreadLocalUtil.removeThreadLocal();
		}
	}
	public ISignatureHtml getISignatureHtmlFormRequest(HttpServletRequest request) throws UnsupportedEncodingException{
	
		//String mUserName = new String(request.getParameter("USERNAME").getBytes("8859_1"));
		String mExtParam = new String(request.getParameter("EXTPARAM").getBytes("8859_1"));
		String documentId = new String(request.getParameter("DOCUMENTID").getBytes("8859_1"));
		String isid =  request.getParameter("SIGNATUREID");
		Long iSignatureId = UUIDLong.longUUID();
		if(Strings.isNotBlank(isid)){
			String str  = new String(request.getParameter("SIGNATUREID").getBytes("8859_1"));
			iSignatureId = Long.parseLong(str);
		}
		String mSignature = new String(request.getParameter("SIGNATURE").getBytes("8859_1"));
		
		ISignatureHtml iSignatureHtml = new ISignatureHtml();
		iSignatureHtml.setId(iSignatureId);
		iSignatureHtml.setDocumentId(Long.valueOf(documentId));
		Blob bl = Hibernate.createBlob(mSignature.getBytes());;
		iSignatureHtml.setSignature(bl);
		iSignatureHtml.setUserId(CurrentUser.get().getId());
		iSignatureHtml.setSignDate(new Date());
		iSignatureHtml.setHostName(request.getRemoteAddr());
		return iSignatureHtml;
	}
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
