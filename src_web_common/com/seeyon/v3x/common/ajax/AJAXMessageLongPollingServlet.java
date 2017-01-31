package com.seeyon.v3x.common.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.oainterface.longpolling.SendEventException;
import com.seeyon.oainterface.longpolling.serverFactory;
import com.seeyon.oainterface.longpolling.tokenObject;
import com.seeyon.oainterface.longpolling.utils.JSONUtils;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.Constants;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.util.Datetimes;

/**
 * 在线IM消息,长轮询后端控制
 * @author rhy
 *
 */
public class AJAXMessageLongPollingServlet extends HttpServlet {

	private static final long serialVersionUID = -2164264636045832103L;

	private static final Log log = LogFactory.getLog(AJAXMessageLongPollingServlet.class);
	
	private transient UserMessageManager userMessageManager;
	
	private void doGetCurrentTime(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Date date = new Date();
		String str1 = Datetimes.formatDatetime(date);
		String str2 = Datetimes.format(date, "HH:mm:ss");
		PrintWriter out = response.getWriter();
		out.println(str1 + "," + str2);
		out.close();
	}

	/**
	 * 创建token
	 */
	private void doNewToken(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
		// 第一个用userId确保一个用户只上线一次,第二个用userId用来发消息,第三个是延迟时间
		serverFactory.getServer().newTokenObject(String.valueOf(user.getId()), user.getId(), 2000);
		log.info(user.getName() + "创建token：" + user.getId());
	}
	
	/**
	 * 判断当前用户是否已创建token
	 */
	private void doGetToken(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
		tokenObject token = serverFactory.getServer().getTokenObject(String.valueOf(user.getId()));
		PrintWriter out = response.getWriter();
        out.print(token != null && token.getWaitThread() != null);
        out.close();
	}
	
	/**
	 * 停止token
	 */
	private void doStopToken(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
		serverFactory.getServer().removeTokenObject(String.valueOf(user.getId()));
		log.info(user.getName() + "停止token：" + user.getId());
	}

	/**
	 * 发送消息
	 */
	private void doSendMessage(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
		int appId = ApplicationCategoryEnum.communication.ordinal();
		String eventName = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "message.header.person.label");

		long[] receiverIds = null;
		List<Long> allList = new ArrayList<Long>();
		List<Long> successList = new ArrayList<Long>();
		List<Long> failureList = new ArrayList<Long>();
		String idsStr = request.getParameter("receiverIds");
		if (idsStr != null && idsStr.length() > 0) {
			String[] ids = idsStr.split(",");
			receiverIds = new long[ids.length];
			for (int i = 0; i < ids.length; i++) {
				if (StringUtils.isNotBlank(ids[i])) {
					receiverIds[i] = NumberUtils.toLong(ids[i]);
					allList.add(NumberUtils.toLong(ids[i]));
				}
			}
		}

		String messageType = request.getParameter("messageType");
		String referenceId = request.getParameter("referenceId");
		
		String content = "";
		if ((Boolean) BrowserFlag.ImEncoding.getFlag(user)) {
			content = new String(request.getParameter("content").getBytes("8859_1"), "UTF-8");
		} else {
			content = request.getParameter("content");
		}
		
		String creationDate = request.getParameter("creationDate");
		int msgType = NumberUtils.toInt(messageType);
		Long refId = NumberUtils.toLong(referenceId);

		try {
			JSONUtils outObj = new JSONUtils();
			outObj.writeObjectStart();
			outObj.writeProperty("referenceId", referenceId, true);
			outObj.writeValueSplit();
			outObj.writeProperty("content", content, true);
			outObj.writeValueSplit();
			outObj.writeProperty("messageType", messageType, true);
			outObj.writeValueSplit();
			outObj.writeProperty("senderId", String.valueOf(user.getId()), true);
			outObj.writeValueSplit();
			outObj.writeProperty("senderName", user.getName(), true);
			outObj.writeValueSplit();
			outObj.writeProperty("creationDateTime", request.getParameter("showDate"), true);
			outObj.writeObjectEnd();
			
			long[] result = serverFactory.getServer().sendEvent(receiverIds, appId, eventName, outObj);
			
			if (result != null && result.length > 0) {
				for (int i = 0; i < result.length; i++) {
					failureList.add(result[i]);
				}
				successList = FormBizConfigUtils.getReducedCollection(allList, failureList);
			} else {
				successList = allList;
			}
			
			if(userMessageManager == null){
				userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
			}
			
			//IM发送成功保存历史消息
			try {
				userMessageManager.sendIMMessage(1, Constants.valueOf(msgType), refId, content, user.getId(), successList, creationDate);
			} catch (MessageException e) {
				log.error("IM保存历史消息失败：" + e + "。发送者：" +user.getName());
			}
			
			//IM发送不成功说明IM不在线，采用普通方式发送
			if (result != null && result.length > 0) {
				try {
					userMessageManager.sendPersonMessage(Constants.valueOf(msgType), refId, content, user.getId(), failureList, creationDate);
				} catch (MessageException e) {
					log.error("普通发送消息失败：" + e + "。发送者：" +user.getName());
				}
			}
		} catch (SendEventException e) {
			log.error("IM发送消息失败：" + e + "。发送者：" +user.getName());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			HttpSession session = request.getSession(false);
			if (session == null) {
				response.setHeader("Error", "Session is null");
				return;
			}

			CurrentUserToSeeyonApp.set(session);

			User user = CurrentUser.get();
			if (user == null) {
				return;
			}

			String calltype = request.getParameter("callType");
			if ("newToken".equalsIgnoreCase(calltype)) {
				this.doNewToken(request, response, user);
			} else if ("getToken".equalsIgnoreCase(calltype)) {
				this.doGetToken(request, response, user);
			} else if ("stopToken".equalsIgnoreCase(calltype)) {
				this.doStopToken(request, response, user);
			} else if ("sendMessage".equalsIgnoreCase(calltype)) {
				this.doSendMessage(request, response, user);
			} else if ("getTime".equalsIgnoreCase(calltype)) {
				this.doGetCurrentTime(request, response);
			}
		} catch (Exception e) {
			log.error("", e);
			response.sendError(505, e.getMessage());
		} finally {
			ThreadLocalUtil.removeThreadLocal();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
