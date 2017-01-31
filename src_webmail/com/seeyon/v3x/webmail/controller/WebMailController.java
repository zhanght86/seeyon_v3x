package com.seeyon.v3x.webmail.controller;

// Imports
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.NoSuchPartitionException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.mail.manager.MessageMailManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.NeedlessCheckLogin;
import com.seeyon.v3x.util.annotation.SetContentType;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.domain.SearchStruct;
import com.seeyon.v3x.webmail.manager.LocalMailCfg;
import com.seeyon.v3x.webmail.manager.MailBoxFolder;
import com.seeyon.v3x.webmail.manager.MailBoxManager;
import com.seeyon.v3x.webmail.manager.MailManager;
import com.seeyon.v3x.webmail.manager.MbcList;
import com.seeyon.v3x.webmail.manager.WebMailManager;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.AffixList;
import com.seeyon.v3x.webmail.util.MailTools;
import com.seeyon.v3x.webmail.util.UniqueCode;

public class WebMailController extends BaseManageController {
	private final static Log logger = LogFactory
			.getLog(WebMailController.class);

	// Fields
	private WebMailManager webMailManager;

	private static String errorUrl = "webmail/error";

	private CollaborationController collaborationController;
	
	private FileManager fileManager;
	
	private OrgManager orgManager;
	
	private AddressBookManager addressBookManager;
	
    private MessageMailManager messageMailManager;
    
    private AttachmentManager attachmentManage ;
    
	public void setAttachmentManage(AttachmentManager attachmentManage) {
		this.attachmentManage = attachmentManage;
	}

	// Constructors
	public WebMailController() {
	}

	// Methods
	public void setWebMailManager(WebMailManager webMailManager) {
		this.webMailManager = webMailManager;
	}
    
    public void setMessageMailManager(MessageMailManager messageMailManager) {
        this.messageMailManager = messageMailManager;
    }

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return list(request, response);
	}
	
    /**
     * @function 增加车辆管理首页边线
	 * @author ：李飞
	 * @ Tag  : start ，结束为 end 
	 */	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String jsp = request.getParameter("jsp");
		/*
		if(null!=jsp && "inbox".equals(jsp)){
			User user = CurrentUser.get();
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String.valueOf(user.getId()));
			if(mbc == null){
				ModelAndView mav = new ModelAndView(errorUrl);
				mav.addObject("errorMsg", "2");
				mav.addObject("url", "?method=list&jsp=set");
				return mav;
			}					
		}*/
		if(null!=jsp && "search".equals(jsp)){
			ModelAndView mav = new ModelAndView("webmail/search/search");
			return mav;
		}
		
		String entryStr = "" ;		
		entryStr = request.getParameter("entry") ;
		if (entryStr != null && !entryStr.equals("") && entryStr.equals("indexEntry")){
			ModelAndView mav = new ModelAndView("webmail/listFrame");
			String method = "list_inbox";
			if (jsp != null) {
				method = "list_" + jsp;
			}			
			mav.addObject("method", method);
			return mav;
		}else{
			ModelAndView mav = new ModelAndView("webmail/listFrameEntry");
			mav.addObject("jsp",jsp);
			return mav ;
		}		
		
	}
    /**
     * @end  
     */
	public ModelAndView fetch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String url = "webmail/listFrame";
		ModelAndView mav = new ModelAndView();
		WebMailManager webMailManager = (WebMailManager) this
				.getApplicationContext().getBean("webMailManager");
		User user = CurrentUser.get();
		try {
			String email = request.getParameter("email");
			boolean flag = webMailManager.fetchMail(String.valueOf(user.getId()), email, request.getSession());
			mav.addObject("method", "list_inbox");
		} catch (Exception ex) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('"+ex.getMessage()+"');");
			out.println("</script>");
			logger.error("邮件接收异常",ex);
			url = errorUrl;
			//mav.addObject("errorMsg", "1");
			//mav.addObject("errorMsg", "null");//已经做出弹出提示，不需要再次弹出
			mav.addObject("errorMsg", "'"+ex.getMessage()+"'");
			mav.addObject("url", "?method=list&jsp=inbox");
		}
		mav.setViewName(url);
		return mav;
	}

	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String.valueOf(user.getId()));
		if(mbc == null){
			ModelAndView mav = new ModelAndView(errorUrl);
			mav.addObject("errorMsg", "2");
			mav.addObject("url", "?method=list&jsp=set");
			return mav;
		}
		String url = "webmail/new/new";
		MailInfo mi = new MailInfo();
		mi.setMailLongId(0);
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("bean", mi);
		String defaultaddr = request.getParameter("defaultaddr");
		if(defaultaddr != null && defaultaddr.length() > 0){
			mav.addObject("defaultaddr", defaultaddr);
		}
		return mav;
	}

	public ModelAndView send(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		
		FileManager fileManager = (FileManager)this.getApplicationContext().getBean("fileManager");
		MailInfo mi = new MailInfo();
		List file_List = new ArrayList();
		String mailLongId = request.getParameter("mailLongId");
		try{
			/****
			List tempList = null ;
			if(mailLongId != null && mailLongId.length() > 0 && !mailLongId.equals("0")){
				tempList = attachmentManager.getByReference(Long.parseLong(mailLongId));
				mi.setMailLongId(Long.parseLong(mailLongId));
				attachmentManager.deleteByReference(mi.getMailLongId());
			}
		
		String referenceId = request.getParameter("referenceId");
		if(referenceId != null && referenceId.length() > 0){
			try{
				attachmentManager.deleteByReference(Long.parseLong(referenceId));
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		
		
		if(tempList == null || tempList.size() == 0){
			
		}
		****/
		attachmentManage.create(ApplicationCategoryEnum.mail, mi.getMailLongId(), mi.getMailLongId(), request);
		List attList = attachmentManage.getByReference(mi.getMailLongId());
		for(int i = 0; i < attList.size(); i++)
		{
			Attachment att = (Attachment)attList.get(i);
			if(null == att.getFileUrl())continue;
			V3XFile vFile = fileManager.getV3XFile(att.getFileUrl());
			file_List.add(vFile);
		}
		}catch(Exception e)
		{
			logger.error("发送邮件时出现错误。",e);
			out.println("<script>");
			out.println("alert('邮件发送失败,请稍后重发!');");	
			out.println("</script>");
			return null;
		}
		
		User user = CurrentUser.get();

		String userid = String.valueOf(user.getId());
		String user_name = user.getName();
//		String[] fileName = null;
//		String[] filePath = null;
		
		AffixList al = new AffixList();
		if (file_List != null) {
			//fileName = new String[file_List.size()];
			//filePath = new String[file_List.size()];
			for (int i = 0; i < file_List.size(); i++) {
				V3XFile vFile = (V3XFile) file_List.get(i);
				File file = fileManager.getFile(vFile.getId());				
				String fileName = vFile.getFilename();
				Long fileId = vFile.getId();
				if(file != null) {
					String filePath = file.getAbsolutePath();
					if(filePath != null) {
						Affix af = new Affix(fileName, filePath);
						af.setContentId(String.valueOf(fileId));
						af.setLength(1l);
						al.add(af);
					}
				} else {
					logger.warn("文件[id=" + vFile.getId() + "]不存在！");
				}
						
			}
		}

		String smail = request.getParameter("smail");
		if (smail == null) {
			smail = "";
		}

		String from = "";
		// 如果是修改邮件内容，取得邮件唯一编号
		String mailId = request.getParameter("msgno");
		String to = request.getParameter("to");
		String cc = request.getParameter("cc");
		String bcc = request.getParameter("bc");
		String reply = request.getParameter("reply");
		// 得到发件箱地址
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(userid);
		if(mbc == null){
			ModelAndView mav = new ModelAndView(errorUrl);
			mav.addObject("errorMsg", "2");
//			mav.addObject("url", "?method=list_set");
			mav.addObject("url", "?method=list&jsp=set");
			return mav;
		}
		from = user_name + "<" + mbc.getEmail() + ">";
		if (reply == null)
			reply = "";

		try {
			if (to.indexOf(";") != -1) {
				to = to.replace(';', ',');
			}
			if (cc.indexOf(";") != -1) {
				cc = cc.replace(';', ',');
			}
			if (bcc.indexOf(";") != -1) {
				bcc = bcc.replace(';', ',');
			}
		} catch (NullPointerException npe) {
			ModelAndView mav = new ModelAndView("webmail/error");
			mav.addObject("errorMsg", "0");
			mav.addObject("url", "?method=create");
			return mav;
		}

		String subject = request.getParameter("subject");
		String text = request.getParameter("content");
		String priority = request.getParameter("priority");

		//subject = str.UnicodetoChinese(subject);
		//text = str.UnicodetoChinese(text);

		if (priority == null)
			priority = "3";
		if (text == null)
			text = "";
		if (subject == null)
			subject = "";

		mi.setMailNumber(UniqueCode.generate());
		mi.setFrom(from);
		mi.setTo(MimeUtility.encodeText(to));
		mi.setBc(bcc);
		mi.setCc(cc);
		mi.setSubject(subject);
		mi.setContentText(userid, text);
		mi.setPriority(Integer.parseInt(priority));
		if ("true".equals(reply)) {
			mi.setReply(true);
			mi.setReplyTo(mi.getFromAdd());
		}
//		AffixList al = new AffixList();
//		if (fileName != null) {
//			for (int i = 0; i < fileName.length; i++) {
////				fileName[i] = str.UnicodetoChinese(fileName[i]);
////				filePath[i] = str.UnicodetoChinese(filePath[i]);
//				if(filePath[i] != null) {
//					Affix af = new Affix(fileName[i], filePath[i]);
//					al.add(af);
//				}
//			}
//		}
		mi.setAffixList(al);
		boolean checkResult = webMailManager.checkMailSpace(userid, mi);
		if(!checkResult){
			 out.println("<script>");
			 out.println("try {parent.getA8Top().endProc();}catch(e) {}");
             out.println("alert('邮箱空间已经占满,请联系管理员!');");   
             out.println("</script>");
             out.flush();
             MailTools.delTempFile(userid);
             return super.redirectModelAndView("/webmail.do?method=create");
		}
		// 保存邮件到草稿箱
		String comm = request.getParameter("comm");
		request.getSession().setAttribute("tempMail", mi);// 先把邮件对象放到session，发送失败时，可以保存原邮件不丢�?
		if ("save".equals(comm)) {
			MailBoxFolder mbf = webMailManager.getMailBoxFolder(userid,
					MailBoxFolder.FOLDER_DRAFT);
			// update by chenl 20050324
			MailInfo mi_1 = mbf.getMailList().getMail(mailId);
			// 修改前先删除旧的邮件,在重新插入新的邮�?
			if (mi_1 != null) {
				mbf.delMail(mi_1.getMailNumber(), false);
			}
			mbf.addMail(mi, true);
			if (mbf.save()) {
				String url = "webmail/listFrame";
				ModelAndView mav = new ModelAndView(url);
				mav.addObject("method", "list_draft");
				mav.addObject("jsp", "draft");
				return mav;
			} else {
				ModelAndView mav = new ModelAndView("webmail/error");
				mav.addObject("errorMsg", "3");
				mav.addObject("url", "?method=create");
				return mav;
			}
		} else {
			try {
				if (webMailManager.sendMail(userid, smail, mi)) {// 发送成功保存到发件�?
					MailBoxFolder mbf = webMailManager.getMailBoxFolder(userid,
							MailBoxFolder.FOLDER_SEND);
					if (mbf.addMail(mi, true) == false || mbf.save() == false) {// 这种情况重新打开，用户可以存到草稿箱，或者重新发�?
						ModelAndView mav = new ModelAndView(errorUrl);
						mav.addObject("errorMsg", "4");
						mav.addObject("url", "?method=create");
						return mav;
						// outJs="<script>top.endProc();alert('邮件发送成功，保存到发件箱错误');location.href='newMail.jsp?comm=zf';</script>";
					} else {
						// outJs="<script>top.endProc();location.href='../sentMail/sentMail.htm';</script>";
					}
				} else {
					MailBoxFolder mbf = webMailManager.getMailBoxFolder(userid,
							MailBoxFolder.FOLDER_DRAFT);
					MailInfo mi_1 = mbf.getMailList().getMail(mailId);
					if (mi_1 != null) {
						mbf.delMail(mi_1.getMailNumber(), false);
					}
					mbf.addMail(mi, true);
					mbf.save();
					ModelAndView mav = new ModelAndView(errorUrl);
					mav.addObject("errorMsg", "5");
					mav.addObject("url", "?method=edit&folderType=2&id=" + mi.getMailNumber());
					return mav;
				}
			} catch (Exception ex) {
                out.println("<script>");
                out.println("try {parent.getA8Top().endProc();}catch(e) {}");
                out.println("alert('"+ex.getMessage()+"');");   
                out.println("</script>");
                out.flush();
                logger.error("WebMail send error:" + ex.getMessage());
                MailBoxFolder mbf = webMailManager.getMailBoxFolder(userid,
                        MailBoxFolder.FOLDER_DRAFT);
                MailInfo mi_1 = mbf.getMailList().getMail(mailId);
                if (mi_1 != null) {
                    mbf.delMail(mi_1.getMailNumber(), false);
                }
                mbf.addMail(mi, true);
                mbf.save();
                return super.redirectModelAndView("/webmail.do?method=list&jsp=set");
//				ModelAndView mav = new ModelAndView(errorUrl);
//				//mav.addObject("errorMsg", "5");
//				mav.addObject("errorMsg", "'"+ex.getMessage()+"'");
//				mav.addObject("url", "?method=edit&folderType=2&id=" + mi.getMailNumber());
//				return mav;
			}
		}
		// 清除session中的临时邮件
		if (request.getSession().getValue("tempMail") != null) {
			request.getSession().removeAttribute("tempMail");
		}
		// 删除临时文件
		MailTools.delTempFile(userid);
		// out.println(outJs);
		
		//如果是从草稿箱中发送邮件，则发送完成后删除
		if(request.getParameter("folderType") != null && request.getParameter("folderType").equals("2")){
			int folderType = Integer.parseInt(request.getParameter("folderType"));
			long tempMailLongId = Long.parseLong(request.getParameter("mailLongId"));
			MailBoxFolder mbf = webMailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
			MailInfoList mlis = mbf.getMailList();
			for(int i = 0; i < mlis.size(); i++){
				MailInfo tempMi = mlis.get(i);
				if(tempMi.getMailLongId() == tempMailLongId){
					mlis.remove(tempMi);
					mbf.save();
				}
			}
		}
		
		
		//String url = "webmail/listFrame";
		String url = "webmail/listFrameEntry";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("method", "list_sent");
		mav.addObject("jsp", "sent");
		return mav;
	}

	/*
	 *实现自动流程的代�?
	 */
	public ModelAndView autoToCol(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		ModelAndView modelAndView=new ModelAndView("webmail/autoToCol");
		
		User user = CurrentUser.get();
		String[] ids = request.getParameterValues("id");
		
		List<V3xOrgMember> members=null;
		List<V3xOrgMember> shows=new ArrayList<V3xOrgMember>();
		try {
			members = orgManager.getAllMembers();
			for(int i=0;i<8;i++){
				V3xOrgMember member=members.get(i);
				shows.add(members.get(i));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.debug("", e);
		}
		
		int folderType = -1;
		try{
			folderType = Integer.parseInt(request.getParameter("folderType"));
		}catch(Exception ex){}
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		StringBuffer content=new StringBuffer();
		List attachments=new ArrayList();//将所有附件加入这个List�
		String subject=null;
		try {
		for(String mailNumber:ids){
			if(folderType == -1){
				int[] folderTypes = {0,1,2,3};
				for(int i = 0; i < folderTypes.length; i++){
					mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderTypes[i]);
					if(mailNumber != null && mailNumber.length() > 0){
						MailInfoList mils = mbf.getMailList();
						mi = mils.getMailByLongId(Long.parseLong(mailNumber));
						if(mi == null){
							if(i == 3){
								throw new Exception("6");
							}else{
								continue;
							}
						}
						if(subject==null) subject=mi.getSubject();
						content.append(mi.getContentText2());
						content.append("<br></br>");
					}
					else{
						if(i == 3){
							throw new Exception("6");
						}else{
							continue;
						}
					}
					if(mi == null){
						if(i == 3){
							throw new Exception("6");
						}else{
							continue;
						}
					}
					List attList = affixToAttach(mi.getAffixList());
					if(attList.size()>0){
						attachments.addAll(attList);
					}
					break;
				}
			}else{
				mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
				if(mailNumber != null && mailNumber.length() > 0){
					MailInfoList mils = mbf.getMailList();
					mi = mils.getMail(mailNumber);
					
					if(subject==null) subject=mi.getSubject();
					content.append(mi.getContentText2());
					content.append("<br></br>");
				}
				else{
					throw new Exception("6");
				}
				if(mi == null){
					throw new Exception("6");
				}
				List attList = affixToAttach(mi.getAffixList());
				
				if(attList.size()>0){
					attachments.addAll(attList);
				}
			}
		  } 
		modelAndView.addObject("from", user.getName());
		modelAndView.addObject("subject", subject);
		modelAndView.addObject("attachments", attachments);
		modelAndView.addObject("content", content.toString());
		modelAndView.addObject("bodyType", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
     
		}catch (Exception ex) {ex.printStackTrace();
		logger.debug("error when fetch the mail content and affix!", ex);
		modelAndView.setViewName(errorUrl);
		modelAndView.addObject("errorMsg", "6");
		modelAndView.addObject("url", "/common/detail.jsp");
		}
		
		modelAndView.addObject("list", shows);

		return modelAndView;
	}
	/*
	 * 取得邮件内容，拼成一个文�
	 */
	public ModelAndView convertToCol(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		User user = CurrentUser.get();
		String id = request.getParameter("ids");
		
		String ids[] = id.split(",");
		
		//id不为空的情况下，根据id取得邮件的内容！�
		
		ModelAndView mav=null;
		
		int folderType = -1;
		try{
			folderType = Integer.parseInt(request.getParameter("folderType"));
		}catch(Exception ex){}
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		StringBuffer content=new StringBuffer();
		List attachments=new ArrayList();//将所有附件加入这个List�
		String subject=null;
		try {
		for(String mailNumber:ids){
		
			if(folderType == -1){
				int[] folderTypes = {0,1,2,3};
				inside:for(int i = 0; i < folderTypes.length; i++){
					mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderTypes[i]);
					MailInfoList mils = mbf.getMailList();
					mi = mils.getMailByLongId(Long.parseLong(mailNumber));
					if(mi == null){
						if(i == 3){
							throw new Exception("6");
						}else{
							continue;
						}
					}
					if(subject==null) subject=mi.getSubject();
					content.append(mi.getContentText2());
					content.append("<br>");
					break inside;
				}
			}else{
				mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
				if(mailNumber != null && mailNumber.length() > 0){
					MailInfoList mils = mbf.getMailList();
					mi = mils.getMail(mailNumber);
					
					if(subject==null) subject=mi.getSubject();
					content.append(mi.getContentText2());
					content.append("<br>");
				}
				else{
					throw new Exception("6");
				}
				if(mi == null){
					throw new Exception("6");
				}
				List attList = affixToAttach(mi.getAffixList());
				
				if(attList.size()>0){
					attachments.addAll(attList);
				}
			}
		  } 
		mav=collaborationController.appToHtmlColl(subject, content.toString(), attachments,false);
     
		}
		catch (Exception ex) {
			logger.error("", ex);
			mav = new ModelAndView();
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			mav.addObject("url", "/common/detail.jsp");
		}

		return mav;
	}
	/*
	 * 将Affix包装为Attachment然后返回
	 * 将被作为附件转协同有�
	 * 
	 */

	private List<Attachment> affixToAttach(AffixList affixList){
		if(affixList==null){
			return null;
		}
		int length=affixList.size();
		List<Attachment> files=new ArrayList<Attachment>();
		for(int i=0;i<length;i++){
			Affix affix=affixList.get(i);
			String fileName=affix.getFileName();
			File file=new File(affix.getRealPath());
			java.util.Date createDate=new Date();
			ApplicationCategoryEnum category=ApplicationCategoryEnum.collaboration;
			try {
				V3XFile v3xFile=fileManager.save(file, category, fileName, createDate, false);
				v3xFile.setType(com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal());
				Attachment attachment=new Attachment(v3xFile);

				files.add(attachment);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
		return files;
	}
	
	public ModelAndView autoToMail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String url = "webmail/new/new";
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		ModelAndView mav = new ModelAndView();
		String mailNumber = request.getParameter("id");
		User user = CurrentUser.get();
		int[] folderTypes = {0,1,2,3};
		int folderType;
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		try {
			for(int j = 0; j < folderTypes.length; j++){
				mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderTypes[j]);
				if(mailNumber != null && mailNumber.length() > 0){
					MailInfoList mils = mbf.getMailList();
					mi = mils.getMailByLongId(Long.parseLong(mailNumber));
					if(mi == null){
						if(j == 3){
							throw new Exception("6");
						}else{
							continue;
						}
					}
				}
				else{
					if(j == 3){
						throw new Exception("6");
					}else{
						continue;
					}
				}
				if(mi == null){
					if(j == 3){
						throw new Exception("6");
					}else{
						continue;
					}
				}
				folderType = folderTypes[j];
				mav.setViewName(url);
				mav.addObject("bean", mi);
				List attList = attachmentManager.getByReference(mi.getMailLongId());
				for(int i = 0; i < attList.size(); i++)
				{
					Attachment att = (Attachment)attList.get(i);
				}
				mav.addObject("attachments", attList);
				break;
			}
			
		} catch (Exception ex) {
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			mav.addObject("url", "/common/detail.jsp");
		}
		return mav;
	}

	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String url = "webmail/new/new";
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		ModelAndView mav = new ModelAndView();
		String mailNumber = request.getParameter("id");
		User user = CurrentUser.get();
		int folderType = Integer.parseInt(request.getParameter("folderType"));
		String fw = request.getParameter("Fw");
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		try {
			mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
			if(mailNumber != null && mailNumber.length() > 0){
				MailInfoList mils = mbf.getMailList();
				mi = mils.getMail(mailNumber);
			}
			else{
				throw new Exception("6");
			}
			if(mi == null){
				throw new Exception("6");
			}
			if(fw != null){
				mi.setSubject("Fw:" + mi.getSubject());
			}
			if(folderType != 2){
				mi.setTo("");
				mi.setCc("");
				mi.setBc("");
			}
			mav.setViewName(url);
			mav.addObject("bean", mi);
			
			//List attList = attachmentManager.getByReference(mi.getMailLongId());
			List attList = affixToAttach(mi.getAffixList());
			for(int i = 0; i < attList.size(); i++)
			{
				Attachment att = (Attachment)attList.get(i);
			}
			mav.addObject("attachments", attList);
			mav.addObject("comm", "save");
			mav.addObject("folderType", folderType);
		} catch (Exception ex) {
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			mav.addObject("url", "?method=list&jsp=draft");
		}
		return mav;
	}

	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String url = "webmail/listFrame";
		WebMailManager webMailManager = (WebMailManager)this.getApplicationContext().getBean("webMailManager");
		String[] ids = request.getParameterValues("id");
		String folder = request.getParameter("folder");
		if(ids != null){
			MailBoxFolder mbf = webMailManager.getMailBoxFolder(String.valueOf(user.getId()), Integer.parseInt(folder));
			
			for(int i = 0; i < ids.length; i++){
				mbf.delMail(ids[i], false);
			}
			mbf.save();
		}
		ModelAndView mav = new ModelAndView(url);
		String view = "inbox";
		switch(Integer.parseInt(folder)){
			case MailBoxFolder.FOLDER_CUR:
				view = "inbox";break;
			case MailBoxFolder.FOLDER_DRAFT:
				view = "draft";break;
			case MailBoxFolder.FOLDER_SEND:
				view = "sent";break;
			case MailBoxFolder.FOLDER_TRASH:
				view = "trash";break;
		}
		mav.addObject("method", "list_" + view);
		return mav;
	}
	
	public ModelAndView empty(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String url = "webmail/listFrame";
		WebMailManager webMailManager = (WebMailManager)this.getApplicationContext().getBean("webMailManager");
		int folder =3; 
		MailBoxFolder mbf = webMailManager.getMailBoxFolder(String.valueOf(user.getId()), folder);
		mbf.delAllMail();
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("method", "list_trash");
		return mav;
	}

	public ModelAndView show(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		String showType = request.getParameter("showType");
		String url = "webmail/detail";
		if(showType != null && showType.equals("1"))
		{
			url = "webmail/content";
		}
		ModelAndView mav = new ModelAndView();
		String mailId = request.getParameter("mailId");
		String mailNumber = request.getParameter("mailNumber");
		User user = CurrentUser.get();
		int folderType = Integer.parseInt(request.getParameter("folderType"));
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		try {
			mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
			
			if(mailId != null && mailId.length() > 0){
				mi = mbf.getMail(mailId);
				if(mi == null && mailNumber != null && mailNumber.length() > 0){
					MailInfoList mils = mbf.getMailList();
					mi = mils.getMail(mailNumber);
				}
			}
			else if(mailNumber != null && mailNumber.length() > 0){
				MailInfoList mils = mbf.getMailList();
				mi = mils.getMail(mailNumber);
			}
			else{
				throw new Exception("6");
			}
			if(mi == null){
				throw new Exception("6");
			}
			mav.setViewName(url);
			mi.setReadFlag(true);
			mbf.save();
			String to = mi.getTo();
			if(!Strings.isBlank(to) && to.length()>1){
				 int indexA =  to.indexOf("<");
				 if(indexA > 0 ){
					 to = to.substring(indexA, to.length());
				 }
			}
			mi.setTo(to);
			mav.addObject("bean", mi);
			mav.addObject("folderType", String.valueOf(folderType));
			List<Attachment> attList = attachmentManager.getByReference(mi.getMailLongId());

			AffixList al = mi.getAffixList();
			if(al != null && al.size() > 0 && (attList == null || attList.size() == 0)){
				List<Attachment> files=new ArrayList<Attachment>();
				for(int i=0;i<al.size();i++){
					Affix affix=al.get(i);
					String fileName=affix.getFileName();
					File file=new File(affix.getRealPath());
					java.util.Date createDate=new Date();
					ApplicationCategoryEnum category=ApplicationCategoryEnum.mail;
					try {
						V3XFile v3xFile=fileManager.save(file, category, fileName, createDate, false);
						v3xFile.setType(com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal());
						Attachment attachment=new Attachment(v3xFile);
						attachment.setReference(mi.getMailLongId());
						attachment.setSubReference(mi.getMailLongId());
						files.add(attachment);
					} catch (BusinessException e) {
						logger.error(e) ;
					}
				}
				attachmentManage.create(files) ;
				attList = files;
			}
			mav.addObject("attachments", attList);
		} catch (Exception ex) {ex.printStackTrace();
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			mav.addObject("url", "/common/detail.jsp");
		}
		return mav;
	}
	
	public ModelAndView showMail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//url:/webmail.do?method=showMail&id=
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		String showType = request.getParameter("showType");
		String url = "webmail/detail";
		if(showType != null && showType.equals("1"))
		{
			url = "webmail/content";
		}
		ModelAndView mav = new ModelAndView();
		long id = Long.parseLong(request.getParameter("id"));
		User user = CurrentUser.get();
		int[] folderType = {0,1,2,3};
		int tempFolderType = 1;
		MailInfo mi = null;
		try {
			for(int i = 0; i < folderType.length; i++){
				MailBoxFolder mbf = null;
				try {
					mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType[i]);
					MailInfoList mils = mbf.getMailList();
					for(int j = 0; j < mils.size(); j++){
						MailInfo temp = mils.get(j);
						if(temp.getMailLongId() == id){
							mi = temp;
							mi.setReadFlag(true);
							mbf.save();
							tempFolderType = folderType[i];
						}
					}
				}catch(Exception ex){
					throw ex;
				}
			}
			mav.setViewName(url);
			mav.addObject("bean", mi);
			mav.addObject("folderType", String.valueOf(tempFolderType));
			List attList = attachmentManager.getByReference(mi.getMailLongId());
			mav.addObject("attachments", attList);
		} catch (Exception ex) {ex.printStackTrace();
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			mav.addObject("url", "/common/detail.jsp");
		}
		return mav;
	}

	public ModelAndView reply(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		 String url = "webmail/new/new";
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		ModelAndView mav = new ModelAndView();
		String mailNumber = request.getParameter("id");
		User user = CurrentUser.get();
		int folderType = Integer.parseInt(request.getParameter("folderType"));
		MailInfo mi = null;
		MailBoxFolder mbf = null;
		try {
			mbf = MailManager.getMailBoxFolder(String.valueOf(user.getId()), folderType);
			
			if(mailNumber != null && mailNumber.length() > 0){
				MailInfoList mils = mbf.getMailList();
				mi = mils.getMail(mailNumber);
			}
			else{
				throw new Exception("6");
			}
			if(mi == null){
				throw new Exception("6");
			}
			String flag = request.getParameter("flag");
			String to = mi.getTo();
			String cc = mi.getCc();
			String bc = mi.getBc();
			
			if(flag.equals("1")){
				mi.setCc(cc);
				mi.setBc(bc);
				mi.setTo(getRepMembers(mi)) ;
			}else{
				mi.setCc("");
				mi.setBc("");
				mi.setTo(mi.getFrom());
			}
			mi.setSubject("Re:" + mi.getSubject());
			mav.setViewName(url);
			mav.addObject("bean", mi);
			List attList = new ArrayList();
			mav.addObject("attachments", attList);
			mav.addObject("comm", "save");
			
		} catch (Exception ex) {
			mav.setViewName(errorUrl);
			mav.addObject("errorMsg", "6");
			String view = "inbox";
			switch(folderType){
				case MailBoxFolder.FOLDER_CUR:
					view = "inbox";break;
				case MailBoxFolder.FOLDER_DRAFT:
					view = "draft";break;
				case MailBoxFolder.FOLDER_SEND:
					view = "sent";break;
				case MailBoxFolder.FOLDER_TRASH:
					view = "trash";break;
			}
			mav.addObject("url", "?method=list&jsp=" + view);
		}
		return mav;
	}
    /**
     * 
     * @param mi
     * @return
     */
	private String getRepMembers(MailInfo mi) throws Exception {
		if(mi == null) {
			return "" ;
		}	
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(CurrentUser.get().getId()+"");		 
		if( mi.getTo() != null && mi.getTo().contains(mbc.getEmail())){
			return mi.getTo().replace(mbc.getEmail(), mi.getFromAdd()) ;
		}
		return mi.getTo() ;
	}
	
	public ModelAndView list_inbox(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int count = 0;
		int newCount = 0;
		int folderType = MailBoxFolder.FOLDER_CUR;
		User user = CurrentUser.get();
		MailInfoList mlis = null;
		try {
			mlis = webMailManager.getMailBoxFolder(
					String.valueOf(user.getId()), folderType)
					.getMailList();
			if (folderType == MailBoxFolder.FOLDER_CUR) {
				newCount = mlis.getNewCount();

			}
		} catch (Exception e) {
				logger.error("接受邮件时出现错误。",e);
				logger.info("接受邮件时出现错误。",e);
				logger.debug("接受邮件时出现错误。",e);
		}
		if (mlis != null)
			count = mlis.size();
		
		ArrayList arr = new ArrayList();
		for(int i = 0; i < mlis.size(); i++){
			try{
				MailInfo mailInfo = mlis.get(i);
				AffixList affixList = mailInfo.getAffixList();
				long fileSize =  0;
				if (affixList != null  && affixList.size() > 0) {
					for (int j = 0 ; j < affixList.size() ; j ++) {
						Affix affix = affixList.get(j);
						V3XFile v3xFile = null;
						if(isLong(affix.getContentId())){
							v3xFile = fileManager.getV3XFile(Long.valueOf(affix.getContentId()));
						}
						if (v3xFile != null) {
							fileSize = fileSize + v3xFile.getSize();
						} else {
							fileSize = fileSize + affix.getLength();
						}
					}
				} 
				mailInfo.setSize(mailInfo.getSize() + fileSize);
				arr.add(mlis.get(i)); 
			}catch(Exception ex){}
		}
		
		MbcList mbcList = MailBoxManager.loadMailBoxs(String.valueOf(user.getId()));
		String url = "webmail/inbox/inbox";
		ModelAndView mav = new ModelAndView(url);
		List pagenate = CommonTools.pagenate(arr);
		mav.addObject("list", pagenate);
		mav.addObject("newCount", new Integer(arr.size()));
		mav.addObject("mbcList", mbcList.getList());
		return mav;
	}

	public ModelAndView list_trash(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int count = 0;
		int newCount = 0;
		int folderType = MailBoxFolder.FOLDER_TRASH;
		User user = CurrentUser.get();
		MailInfoList mlis = null;
		try {
			mlis = webMailManager.getMailBoxFolder(
					String.valueOf(user.getId()), folderType)
					.getMailList();
			if (folderType == MailBoxFolder.FOLDER_CUR) {
				newCount = mlis.getNewCount();

			}
		} catch (Exception e) {
		}
		if (mlis != null)
			count = mlis.size();
		Pagination.setRowCount(count);		
		
		ArrayList arr = new ArrayList();
		int endIndex = Pagination.getFirstResult()+Pagination.getMaxResults() > (count-1) ? (count-1) : Pagination.getFirstResult() + Pagination.getMaxResults();
		
		for(int i = Pagination.getFirstResult(); i <= endIndex; i++){
			try{
				mlis.get(i).setTo(MimeUtility.decodeText(mlis.get(i).getTo()));
				MailInfo mailInfo = mlis.get(i);
				AffixList affixList = mailInfo.getAffixList();
				long fileSize =  0;
				if (affixList != null  && affixList.size() > 0) {
					for (int j = 0 ; j < affixList.size() ; j ++) {
						Affix affix = affixList.get(j);
						V3XFile v3xFile = fileManager.getV3XFile(Long.valueOf(affix.getContentId()));
						if (v3xFile != null) {
							fileSize = fileSize + v3xFile.getSize();
						}
					}
				} 
				mailInfo.setSize(mailInfo.getSize() + fileSize);
				arr.add(mlis.get(i)); 
			}catch(Exception ex){}
		}
		MbcList mbcList = MailBoxManager.loadMailBoxs(String.valueOf(user
				.getId()));
		String url = "webmail/trash/trash";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("list", arr);
		mav.addObject("newCount", new Integer(newCount));
		return mav;
	}

	public ModelAndView list_sent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int count = 0;
		int newCount = 0;
		int folderType = MailBoxFolder.FOLDER_SEND;
		User user = CurrentUser.get();
		MailInfoList mlis = null;
		try {
			mlis = webMailManager.getMailBoxFolder(
					String.valueOf(user.getId()), folderType)
					.getMailList();
			if (folderType == MailBoxFolder.FOLDER_CUR) {
				newCount = mlis.getNewCount();

			}
		} catch (Exception e) {
		}
		if (mlis != null)
			count = mlis.size();
		Pagination.setRowCount(count);		
		
		ArrayList arr = new ArrayList();
		int endIndex = Pagination.getFirstResult()+Pagination.getMaxResults() > (count-1) ? (count-1) : Pagination.getFirstResult() + Pagination.getMaxResults();
		
		for(int i = Pagination.getFirstResult(); i <= endIndex; i++){
			try{
				mlis.get(i).setTo(MimeUtility.decodeText(mlis.get(i).getTo()));
				MailInfo mailInfo = mlis.get(i);
				AffixList affixList = mailInfo.getAffixList();
				long fileSize =  0;
				if (affixList != null  && affixList.size() > 0) {
					for (int j = 0 ; j < affixList.size() ; j ++) {
						Affix affix = affixList.get(j);
						V3XFile v3xFile = fileManager.getV3XFile(Long.valueOf(affix.getContentId()));
						if (v3xFile != null) {
							fileSize = fileSize + v3xFile.getSize();
						}
					}
				} 
				mailInfo.setSize(mailInfo.getSize() + fileSize);
				arr.add(mlis.get(i)); 
			}catch(Exception ex){}
		}
		MbcList mbcList = MailBoxManager.loadMailBoxs(String.valueOf(user
				.getId()));
		String url = "webmail/sent/sent";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("list", arr);
		mav.addObject("newCount", new Integer(newCount));
		return mav;
	}

	public ModelAndView list_draft(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int count = 0;
		int newCount = 0;
		int folderType = MailBoxFolder.FOLDER_DRAFT;
		User user = CurrentUser.get();
		MailInfoList mlis = null;
		try {
			mlis = webMailManager.getMailBoxFolder(
					String.valueOf(user.getId()), folderType)
					.getMailList();
			if (folderType == MailBoxFolder.FOLDER_DRAFT) {
				newCount = mlis.getNewCount();

			}
		} catch (Exception e) {
		}
		if (mlis != null)
			count = mlis.size();
		Pagination.setRowCount(count);		
		
		ArrayList arr = new ArrayList();
		int endIndex = Pagination.getFirstResult()+Pagination.getMaxResults() > (count-1) ? (count-1) : Pagination.getFirstResult() + Pagination.getMaxResults();
		
		for(int i = Pagination.getFirstResult(); i <= endIndex; i++){
			try{
				mlis.get(i).setTo(MimeUtility.decodeText(mlis.get(i).getTo()));
				MailInfo mailInfo = mlis.get(i);
				AffixList affixList = mailInfo.getAffixList();
				long fileSize =  0;
				if (affixList != null  && affixList.size() > 0) {
					for (int j = 0 ; j < affixList.size() ; j ++) {
						Affix affix = affixList.get(j);
						V3XFile v3xFile = fileManager.getV3XFile(Long.valueOf(affix.getContentId()));
						if (v3xFile != null) {
							fileSize = fileSize + v3xFile.getSize();
						}
					}
				}
				mailInfo.setSize(mailInfo.getSize() + fileSize);
				arr.add(mlis.get(i)); 
			}catch(Exception ex){}
		}

		String url = "webmail/draft/draft";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("list", arr);
		mav.addObject("newCount", new Integer(newCount));
		return mav;
	}

	public ModelAndView list_set(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String userId = String.valueOf(user.getId());
		String url = "webmail/set/set";
		MbcList ml = MailBoxManager.loadMailBoxs(userId);

		ModelAndView mav = new ModelAndView(url);
		mav.addObject("list", ml.getList());
		
		return mav;
	}

	public ModelAndView set(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String url = "webmail/set/edit";
		ModelAndView mav = new ModelAndView(url);
		String email = request.getParameter("email");
		if (email != null) {
			MailBoxCfg mbc = MailBoxManager.findUserMbc(String.valueOf(user
					.getId()), email);
			if (mbc != null) {
//				mbc.setBackup(true);
				mav.addObject("bean", mbc);
			}
		} else {
			MailBoxCfg mbc = new MailBoxCfg();
			mbc.setEmail("");
			mbc.setSmtpHost("");
			mbc.setPop3Host("");
			mbc.setUserName("");
			mbc.setPassword("");
			mbc.setBackup(true);
			mbc.setDefaultBox(false);
			mbc.setAuthorCheck(true);
			mav.addObject("bean", mbc);
		}
		return mav;
	}

	public ModelAndView doSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();

		String url = "webmail/listFrame";

		String userId = String.valueOf(user.getId());
		String id_email = request.getParameter("id_email");
		try {
			MailBoxCfg mbc = new MailBoxCfg();
			String email = request.getParameter("email");
			String smtpHost = request.getParameter("smtphost");
			String pop3Host = request.getParameter("pop3host");
			String userName = request.getParameter("username");
			String password = request.getParameter("password");
			String timeout = request.getParameter("timeout");
			String backup = request.getParameter("isBackup");
			String pop3Ssl = request.getParameter("pop3ssl");
			String smtpSsl = request.getParameter("smtpssl");

			int pop3Port=RequestUtils.getIntParameter(request, "pop3port",110);
			int smtpPort=RequestUtils.getIntParameter(request, "smtpport",25);
			mbc.setEmail(email);
			mbc.setSmtpHost(smtpHost);
			mbc.setPop3Host(pop3Host);
			mbc.setUserName(userName);
			mbc.setPassword(password);
			mbc.setTimeOut(Integer.parseInt(timeout));
			mbc.setBackup(backup.equals("1") ? true : false);
			mbc.setDefaultBox(false);
			mbc.setAuthorCheck(true);
			
			mbc.setPop3Port(pop3Port);
			mbc.setSmtpPort(smtpPort);
			mbc.setPop3Ssl(pop3Ssl!=null&&pop3Ssl.equals("1") ? true : false);
			mbc.setSmtpSsl(smtpSsl!=null&&smtpSsl.equals("1") ? true : false);
			
			MailBoxCfg tempMbc = null;

			if (id_email != null && id_email.length() > 0) {
				tempMbc = MailBoxManager.findUserMbc(userId, id_email);
				MailBoxManager.del(userId, id_email);
			}
			MbcList ml = MailBoxManager.loadMailBoxs(userId);
			for (int i = 0; i < ml.size(); i++) {
				if (ml.get(i).getEmail().equals(email)) {
					if (tempMbc != null) {
						MailBoxManager.add(userId, tempMbc);
					}
					ModelAndView mav = new ModelAndView(errorUrl);
					mav.addObject("errorMsg", "7");
					mav.addObject("url", "?method=set");
					return mav;
				}
			}
			if (ml.size() >= 16) {
				ModelAndView mav = new ModelAndView(errorUrl);
				mav.addObject("errorMsg", "8");
				mav.addObject("url", "?method=set");
				return mav;
			}
			String isDefault = request.getParameter("isDefault");
			if(isDefault != null && isDefault.equals("1")){
				for (int i = 0; i < ml.size(); i++) {
					MailBoxCfg temp = ml.get(i);
					if (temp.getDefaultBox()) {
						MailBoxManager.del(String.valueOf(user.getId()), temp
								.getEmail());
						temp.setDefaultBox(false);
						MailBoxManager.add(String.valueOf(user.getId()), temp);
					}
				}
				mbc.setDefaultBox(true);
			}
			if(ml!=null&&ml.size()==0)
			{
				mbc.setDefaultBox(true);
			}
			MailBoxManager.add(userId, mbc);
		} catch (Exception ex) {
			logger.error("SetMailBoxError:" + ex.getMessage(),ex);
			ModelAndView mav = new ModelAndView(errorUrl);
			mav.addObject("errorMsg", "9");
			mav.addObject("url", "?method=set");
			return mav;
		}
		boolean pageBreak = (Boolean)(BrowserFlag.PageBreak.getFlag(request));
		if(pageBreak){
			ModelAndView mav = new ModelAndView(url);
			mav.addObject("method", "list_set");
			return mav;
		}else{
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("window.close();");
			out.print("</script>");
			out.flush();
			return null;
		}
	}

	public ModelAndView setDefaultMailBox(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String[] ids = request.getParameterValues("id");
		if (ids != null && ids.length == 1) {
			MbcList ml = MailBoxManager.loadMailBoxs(String.valueOf(user
					.getId()));
			for (int i = 0; i < ml.size(); i++) {
				MailBoxCfg mbc = ml.get(i);
				if (mbc.getDefaultBox()) {
					MailBoxManager.del(String.valueOf(user.getId()), mbc
							.getEmail());
					mbc.setDefaultBox(false);
					MailBoxManager.add(String.valueOf(user.getId()), mbc);
				}
			}
			MailBoxCfg mbc = MailBoxManager.findUserMbc(String.valueOf(user
					.getId()), ids[0]);
			mbc.setDefaultBox(true);
			MailBoxManager.del(String.valueOf(user.getId()), ids[0]);
			MailBoxManager.add(String.valueOf(user.getId()), mbc);
		}
		String url = "webmail/listFrame";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("method", "list_set");
		return mav;
	}
	
	public ModelAndView delMailSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String userId = String.valueOf(user.getId());
		String[] ids = request.getParameterValues("id");
		try {
			MailBoxManager.del(userId, ids);
		} catch (Exception ex) {
			logger.error("Del mailBox error:" + ex.getMessage());
		}
		String url = "webmail/listFrame";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("method", "list_set");
		return mav;
	}
	
	public ModelAndView move(HttpServletRequest request, HttpServletResponse response) throws Exception {
		WebMailManager webMailManager = (WebMailManager)this.getApplicationContext().getBean("webMailManager");
		User user = CurrentUser.get();
		String[] ids = request.getParameterValues("id");
		String from = request.getParameter("fromFolder");
		String to = request.getParameter("toFolder");
		if(ids != null){
			for(int i = 0; i < ids.length; i++){
				boolean flag = webMailManager.moveMail(String.valueOf(user.getId()), Integer.parseInt(from), Integer.parseInt(to), ids[i]);
			}
		}
		String url = "webmail/listFrame";
		
		ModelAndView mav = new ModelAndView(url);
		String view = "inbox";
		switch(Integer.parseInt(to)){
			case MailBoxFolder.FOLDER_CUR:
				view = "inbox";break;
			case MailBoxFolder.FOLDER_DRAFT:
				//mav = new ModelAndView("webmail/listFrameEntry");
				view = "draft";
				//mav.addObject("jsp", view);
				break;
			case MailBoxFolder.FOLDER_SEND:
				view = "sent";break;
			case MailBoxFolder.FOLDER_TRASH:
				view = "trash";break;
		}
		mav.addObject("method", "list_" + view);
		return mav;
	}
	
	public ModelAndView list_Search(HttpServletRequest request, HttpServletResponse response)throws Exception
	{
		ModelAndView mav = new ModelAndView("webmail/search/searchlist");
		List list = (List)request.getSession().getAttribute("list");
		String folderType = String.valueOf(((Integer)request.getSession().getAttribute("folderType")).intValue());
		mav.addObject("list", list);
		mav.addObject("folderType", folderType);
		return mav;
	}
	
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response)throws Exception
	{
		String exec = request.getParameter("exec");
		if(exec == null || exec.length() == 0)
		{
			ModelAndView mav = new ModelAndView("webmail/search/search");
			return mav;
		}
		else
		{
			ModelAndView mav = new ModelAndView("webmail/search/searchlist");
			User user = CurrentUser.get();
			
			int count = 0;
			int newCount = 0;			
			String userid = String.valueOf(user.getId());
			//String submit=request.getParameter("searchFlag");
			MailInfoList mlis=null;
			int folderType=Integer.parseInt(request.getParameter("mailbox"));
			//if(submit!=null && !submit.equals(""))
			{
				SearchStruct ss=new SearchStruct();
				ss.FolderType=folderType;
				ss.from=request.getParameter("from");
				//ss.from=str.UnicodetoChinese(ss.from);
				ss.to=request.getParameter("to");
				//ss.to=str.UnicodetoChinese(ss.to);
				ss.subject=request.getParameter("subject");
				//ss.subject=str.UnicodetoChinese(ss.subject);
				
				String searchDate = request.getParameter("dateTime");
				String dType = request.getParameter("relation");
				
				if(!searchDate.trim().equals("")){
					try{
						//java.text.DateFormat df=java.text.DateFormat.getDateTimeInstance();
						if("=".equalsIgnoreCase(dType)) {
							ss.createDate = Datetimes.parse(searchDate, "yyyy-MM-d");
						} else {
							if(">".equalsIgnoreCase(dType)){
								searchDate += " 23:59:59";
							}
							if("<".equalsIgnoreCase(dType)) {
								searchDate += " 00:00:00";
							}
							ss.createDate = Datetimes.parse(searchDate, "yyyy-MM-d HH:mm:ss");
						}
					}catch(Exception e){
						e.printStackTrace();
						ss.createDate=null;
					}
				}
				ss.dateType = dType;
				mlis=MailManager.search(userid,ss);
				newCount = mlis.getNewCount();
				if (mlis != null)
					count = mlis.size();
				Pagination.setRowCount(count);					
				
				ArrayList arr = new ArrayList();
				int endIndex = Pagination.getFirstResult()+Pagination.getMaxResults() > (count-1) ? (count-1) : Pagination.getFirstResult() + Pagination.getMaxResults();
				
				for(int i = Pagination.getFirstResult(); i <= endIndex; i++){
					try{
						MailInfo mailInfo = mlis.get(i);
						AffixList affixList = mailInfo.getAffixList();
						long fileSize =  0;
						if (affixList != null  && affixList.size() > 0) {
							for (int j = 0 ; j < affixList.size() ; j ++) {
								Affix affix = affixList.get(j);
								V3XFile v3xFile = fileManager.getV3XFile(Long.valueOf(affix.getContentId()));
								if (v3xFile != null) {
									fileSize = fileSize + v3xFile.getSize();
								}
							}
						} 
						mailInfo.setSize(mailInfo.getSize() + fileSize);
						arr.add(mlis.get(i)); 
					}catch(Exception ex){}
				}	
				request.getSession().setAttribute("list", arr);
				request.getSession().setAttribute("folderType", new Integer(folderType));
			}
			if(mlis!=null)count = mlis.size();
			List list = (List)request.getSession().getAttribute("list");
			//String folderType = String.valueOf(((Integer)request.getSession().getAttribute("folderType")).intValue());
			mav.addObject("list", list);
			mav.addObject("newCount", new Integer(newCount));
			//mav.addObject("folderType", folderType);
				return mav;
		}
	}

	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		WebMailManager webMailManager = (WebMailManager)this.getApplicationContext().getBean("webMailManager");
		User user = CurrentUser.get();

		int folderType=Integer.parseInt(request.getParameter("folderType"));
		String[] amsgno = request.getParameterValues("id");
		String zipFile=webMailManager.createZip(String.valueOf(user.getId()),amsgno,folderType);
        //modify by yuhj at 2005.3.26
       	String filename = "";
        String path = "";
        if(zipFile != null && zipFile.lastIndexOf("/")>=0){
          path = zipFile.substring(0,zipFile.lastIndexOf("/")+1);
          filename = zipFile.substring(zipFile.lastIndexOf("/")+1);
        }
        //将请求直接发往下载文件的servlet/*zhangh 2005-06-24 修改download1路径*/
        ModelAndView mav = new ModelAndView("webmail/download");
        mav.addObject("path", path);
        mav.addObject("filename", filename);
		return mav;
	}

	public ModelAndView jumpUrl(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String url = request.getParameter("url");
		ModelAndView mav = new ModelAndView(url);
		return mav;
	}
	
	public void initOperate(ModelAndView mav) {
	}
	
	
	public ModelAndView sendMail(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("webmail/new/send");
		String subject = (String)request.getParameter("subject");
		String content = (String)request.getParameter("contentText");
		long reference = Long.parseLong(request.getParameter("reference"));
		long subReference = Long.parseLong(request.getParameter("subReference"));
		AttachmentManager attachmentManager = (AttachmentManager)this.getApplicationContext().getBean("attachmentManager");
		List attList = attachmentManager.getByReference(reference, subReference);
		mav.addObject("subject", subject);
		mav.addObject("contentText", content);
		mav.addObject("attachments", attList);
		mav.addObject("originalAttsNeedClone", "true");
		return mav;
	}

	public void setCollaborationController(
			CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public AddressBookManager getAddressBookManager() {
		return addressBookManager;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}
	
	public ModelAndView getAddressFrame(HttpServletRequest request, HttpServletResponse response)throws Exception{
		 ModelAndView mav = new ModelAndView("webmail/selectaddressFrame");
	     return mav;
	}
	
	public ModelAndView getAddress(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("webmail/selectaddress");
		User user = CurrentUser.get();
		long teamId = 0;
		List list = null;
		if(request.getParameter("teamId") != null && request.getParameter("teamId").length() > 0 && !request.getParameter("teamId").equals("-1")){
			teamId = Long.parseLong(request.getParameter("teamId"));
			list = this.addressBookManager.getMembersByTeamId(teamId);
		}else{
			list = this.addressBookManager.getMembersByCreatorId(user.getId());
		}
		List teamList = this.addressBookManager.getTeamsByCreatorId(user.getId());
		mav.addObject("list", list);
		mav.addObject("teamList", teamList);
		if(teamId != 0){
			mav.addObject("teamId", new Long(teamId));
		}
		return mav;
	}
	
	public ModelAndView getEmailForm(HttpServletRequest request, HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("webmail/processemail");
		String[] ids = request.getParameter("hidden_ids").split(",");
		ArrayList list = new ArrayList();
		for(int i = 0; i < ids.length; i++){
			long id = Long.parseLong(ids[i]);
			V3xOrgMember member = this.orgManager.getMemberById(id);
			list.add(member);
		}AddressBookMember a = null;
		mav.addObject("list", list);
		return mav;
	}
	
    /**
     * 系统邮箱设置 - 显示(设置系统级的邮箱，作为邮件转移时的发件人)
     * Mazc 2008-02-18
     */
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView systemMailboxSetting(HttpServletRequest request, HttpServletResponse response)throws Exception{
        ModelAndView modelAndView = new ModelAndView("webmail/systemMailboxSetting");
        modelAndView.addObject("sysMailConfig", messageMailManager.getSysEMailConfig());
        return modelAndView;
    }
    
    /**
     * 系统邮箱设置 - 更新
     * Mazc 2008-02-18
     */
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView updateSystemMailbox(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String MailAddress = request.getParameter("MailAddress");
        String SMTP = request.getParameter("SMTP");
        String Password = request.getParameter("Password");
        boolean isAppendLink = "true".equals(request.getParameter("isAppendLink"));
        String suffer = request.getParameter("Suffer") ;
        String userName = request.getParameter("userName") ;
        int pop3Port=Integer.parseInt(request.getParameter("pop3port"));
		int smtpPort=Integer.parseInt(request.getParameter("smtpport"));
        int availableTime = 24;        
        try{
            if(isAppendLink){
                availableTime = Integer.parseInt(request.getParameter("availTime"));
            }
            else{
                availableTime = Integer.parseInt(request.getParameter("oldAvailTime"));            
            }
        }
        catch(Exception e){}
        messageMailManager.saveSystemMailboxSetting(MailAddress, SMTP, Password, isAppendLink, availableTime,suffer,userName,pop3Port,smtpPort);
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + com.seeyon.v3x.system.Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/webmail.do?method=systemMailboxSetting");
    }
    
    /**
     * 
     */
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView cancelSystemMailbox(HttpServletRequest request, HttpServletResponse response)throws Exception{
        messageMailManager.cancelSystemMailboxSetting();
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + com.seeyon.v3x.system.Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/webmail.do?method=systemMailboxSetting");
    }
    
	private String contentTypeCharset = "UTF-8";
	/**
	 * 下载文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	@NeedlessCheckLogin
	public ModelAndView doDownloadAtt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(contentTypeCharset);
		
		Long fileId = null;
		Date createDate = null; 
		String filename = null;
		String filePath=null;
		
		filename = request.getParameter("filename");
//		filename=new String(filename.getBytes("iso8859-1"),"utf-8");		
		filePath = LocalMailCfg.getMailBasePath()+ request.getParameter("filePath");
//		filename=java.net.URLEncoder.encode(filename,"UTF-8");
		File downFile=new File(filePath);
		if(downFile.exists()==false){return null;}
		
		InputStream in = null;
		ServletOutputStream out = response.getOutputStream();
		try {
			in = new FileInputStream(downFile);
			response.setContentType("application/x-msdownload; charset=" + contentTypeCharset);
			response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");

			CoderFactory.getInstance().download(in, out);			
		}
		catch (NoSuchPartitionException e) {			
			return null;
		}
		catch (Exception e) {					
				return null;			
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		return null;
	}

	private static boolean isLong(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}