package com.seeyon.v3x.webmail.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.manager.DocSpaceManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.domain.SearchStruct;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.MailTools;
import com.seeyon.v3x.webmail.util.System14;

public class WebMailManagerImpl implements WebMailManager
{
//	 定义是否使用session保存邮件列表，避免每次打开是读取里表文件
	private boolean useSession = true;
	private DocSpaceManager docSpaceManager;
	private final Log logger = LogFactory.getLog(WebMailManagerImpl.class);

	public WebMailManagerImpl()
	{
	}

	public boolean sendMail(String userId, MailInfo mi) throws Exception
	{
		try
		{
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(userId);
			return sendMail(mbc, mi);
		} catch (Exception e)
		{
			throw new Exception("发送失败:" + System14.FormatForJs(e.getMessage()));
		}
	}

	public boolean sendMail(String userId, String sendMailBoxName,
			MailInfo mi) throws Exception
	{
		try
		{
			MailBoxCfg mbc = MailBoxManager
					.findUserMbc(userId, sendMailBoxName);
			if (mbc == null)
			{
				mbc = MailBoxManager.findUserDefaultMbc(userId);
			}
			return sendMail(mbc, mi);
		} catch (Exception e)
		{
			throw new Exception(e.getMessage(),e);
		}
	}

	public boolean sendMail(MailBoxCfg mbc, MailInfo mi)
			throws Exception
	{			
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.webmail.resources.i18n.WebMailResources",CurrentUser.get().getLocale());		
		String errorMsg = "";
		try
		{
			javax.mail.Session sess = ConnMailhost.getSmtpSession(mbc);
			MimeMessage msg = MailTools.changFormat(sess, mi);
			sess.getTransport("smtp").send(msg);
			// 用下面发邮件的时候,总是使用默认邮箱发邮件,有时候出现无效用户,应该是那个信箱收到的那个发
			/*
			 * MimeMessage msg = MailTools.changFormat(mbc, mi);
			 * Transport.send(msg);
			 */
			mi.setMailBoxName(mbc.getEmail());
		}
		catch (javax.mail.SendFailedException e){
			errorMsg = ResourceBundleUtil.getString(r, "label.error.send.address");
			logger.error("发送邮件出错,请检查发送邮件的地址是否正确.",e);	
			throw new Exception(errorMsg);
		}
		catch (javax.mail.AuthenticationFailedException e){
			errorMsg = ResourceBundleUtil.getString(r, "label.error.send.password");
			logger.error("发送邮件出错 : 请检查邮箱的用户名或密码是否填写正确.",e);					
			throw new Exception(errorMsg);
		}
		catch(javax.mail.MessagingException e){
			if(null!=e && !Strings.isBlank(e.getMessage()) && e.getMessage().toLowerCase().contains("smtp")){
				errorMsg = ResourceBundleUtil.getString(r, "label.error.send.smtp");
				logger.error("发送邮件出错,请检查邮箱的SMTP地址是否填写正确 . 错误：",e);
			}else{
				errorMsg = ResourceBundleUtil.getString(r, "label.error.send.general");
				logger.error("发送邮件出错 : 请检查邮箱设置 . 错误：",e);
			}
			throw new Exception(errorMsg);			
		} 
		catch (Exception e){
			errorMsg = ResourceBundleUtil.getString(r, "label.error.send.general");
			logger.error("发送邮件出错 : 请检查邮箱设置 . 错误：",e);
			throw new Exception(MailErrMsg.formatErrMsg(e.getMessage()));
		}
		return true;
	}

	public boolean fetchMail(String userId, MailBoxCfg mbc, HttpSession session) throws Exception{
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.webmail.resources.i18n.WebMailResources",CurrentUser.get().getLocale());		
		String errMsg = "";
		java.util.List recMails = new java.util.ArrayList();
		Store pop3Store = null;
		com.sun.mail.pop3.POP3Folder pop3Folder = null;
		try
		{
			pop3Store = ConnMailhost.getPop3Store(mbc);
			if (!pop3Store.isConnected())
			{
				pop3Store.connect();
			}
		}
		catch (javax.mail.AuthenticationFailedException e)
		{
			if( null!=e  && !Strings.isBlank(e.getMessage())){
					String detailMessage = e.getMessage();
					if(detailMessage.toLowerCase().startsWith("authorization")){
						errMsg = ResourceBundleUtil.getString(r, "label.error.inbox.user");
					}else if(detailMessage.toLowerCase().startsWith("password")){
						errMsg = ResourceBundleUtil.getString(r, "label.error.inbox.password");
					}else if(detailMessage.toLowerCase().startsWith("eof")){
						// EOF ...
						errMsg = ResourceBundleUtil.getString(r, "label.error.inbox.serverbusy");
					}else{
						errMsg = ResourceBundleUtil.getString(r, "label.error.inbox.general");
					}
			}
			logger.error(errMsg+"  "+mbc.getEmail(), e);
			throw new Exception(errMsg);
		} catch (MessagingException e)
		{				
			errMsg = ResourceBundleUtil.getString(r, "label.error.inbox.pop");
			// ****修改号="20060427zhangh_001" 区域="1" *****************开始//
			logger.error("",e);
			throw new Exception(errMsg);
			// ****修改号="20060427zhangh_001" 区域="1" *****************结束//
		} catch (Exception e)
		{
			logger.error("无法连接远程POP邮件服务器，请检查网络连接、POP帐户设置是否正确(" + userId + ")"
					+ e.getMessage(),e);
			throw new Exception("无法连接远程POP邮件服务器，请检查网络连接、POP帐户设置是否正确");
		}
		
		pop3Folder = (com.sun.mail.pop3.POP3Folder) pop3Store
				.getDefaultFolder().getFolder("INBOX");
		if (!pop3Folder.isOpen())
		{
			pop3Folder.open(Folder.READ_WRITE);
		}
		if(!pop3Folder.isOpen()){
			throw new Exception("邮箱服务器忙，请稍后重新接收");			
		}
		// --------------获得邮件总数 wangwp added 2004-08-15
		Message[] msgs = pop3Folder.getMessages();
		int messageCounts = pop3Folder.getMessageCount();
		if (messageCounts > 0)
		{
			boolean isSaveMailId = false;
			// setSessionValue(session,"msgCount",new Integer(messageCounts));
			String affixPath = LocalMailCfg.getUserTempPath(userId);
			MailInfo mi = null;
			LocalMailIdManager lmm = new LocalMailIdManager();
			MailBoxFolder mbf = getMailBoxFolder(userId,
					MailBoxFolder.FOLDER_CUR);
			lmm.load(userId);
			//永远都是保留服务器备份邮件
//			mbc.setBackup(true);
			boolean remoteDel = !mbc.getBackup();
			int i;
			String mid = "";
			// 统计新邮件数量开始
			List newMailList = new ArrayList();
			for (i = 0; i < msgs.length; i++)
			{
				mid = ((MimeMessage)msgs[i]).getMessageID();
				if (mid == null) {
                    mid = msgs[i].getSubject().hashCode() + "";
                }
				if (!lmm.exist(mid))
				{
					newMailList.add(new Integer(i));
				}
			}
			messageCounts = newMailList.size();
			setSessionValue(session, "msgCount", new Integer(messageCounts));
			// 统计新邮件数量结束
			long mailUsedSize=0l;
			DocStorageSpace docSpace=docSpaceManager.getDocSpaceByUserId(Long.parseLong(userId));
			long mailSize=docSpace.getMailSpace();
			long mailOcuppiedSize = LocalMailCfg.getMailSpaceSize(String.valueOf(docSpace.getUserId()));
			for (i = 0; i < messageCounts; i++)
			{
				int curId = Integer.parseInt(newMailList.get(i).toString());
				try
				{
					mid = ((MimeMessage)msgs[curId]).getMessageID();
					if (mid == null) {
	                    mid = msgs[i].getSubject().hashCode() + "";
	                }
					msgs[curId].setFlag(Flags.Flag.RECENT, true);
					setSessionValue(session, "curMsgNo", new Integer(i + 1));
					try
					{
						mi = MailTools.changFormat(mid, msgs[curId], affixPath);
						//邮箱空间大小控制
						mailUsedSize+=mi.getSize();
						if(mailOcuppiedSize+mailUsedSize>=mailSize)
						{
							errMsg += "邮箱空间已经占满,请联系管理员!" + "\r\n";
							break;
						}
					} 
					catch (Exception me)
					{// 邮件解析错误时，继续接收后面得邮件
//						errMsg += me.getMessage() + "\r\n";
						logger.error("SetMailBoxError:" + me.getMessage(),me);
						continue;
					}
					recMails.add(msgs[curId]);// 解析成功后，添加到列表中，统一删除邮件服务器上得邮件
					mi.setMailBoxName(mbc.getEmail());
					mbf.addMail(mi, true);
					// 有可能mbf保存有问题，但lmm保存成功，导致有些邮件没有接收到，但再次接受又会被过滤
//					lmm.add(mid, false);
					if (isSaveMailId == false)
					{
						isSaveMailId = true;
					}
				} catch (Exception e)
				{// 邮件解析过程中发生异常，保存邮件列表和邮件id列表					
					if (isSaveMailId && saveMbf(userId,mbf,lmm) && lmm.save() && remoteDel)
					{
						setMailDelFlag(recMails, i - 1);
					}
					throw new Exception("邮件解析错误，邮件未接收完! 异常:"
							+ System14.FormatForJs(e.getMessage()));
				}
			}
			if (isSaveMailId && saveMbf(userId,mbf,lmm) && lmm.save() && remoteDel)
			{
				setMailDelFlag(recMails, i);
			}
		}
		if (pop3Folder != null)
			pop3Folder.close(true);
		if (pop3Store != null)
			pop3Store.close();
		if (!errMsg.equals(""))
		{
			throw new Exception(System14.FormatForJs(errMsg));
		}// 邮件解析过程中发生错误
		return true;
	}
	private boolean saveMbf(String userId,MailBoxFolder mbf,LocalMailIdManager lmm) throws Exception {
		boolean isMbfSaved = mbf.save();
		// 已经保存过的MailBoxFolder
		MailInfoList mil = getMailBoxFolder(userId,
				MailBoxFolder.FOLDER_CUR).getMailList();
		int len = mil.size();
		String mailId = null;
		for(int i = 0; i < len; i++) {
			mailId = mil.get(i).getMailId();
			if(!lmm.exist(mailId)) {
				lmm.add(mailId,false);
			}
		}
		return isMbfSaved;
	}
	public boolean setMailDelFlag(Message[] mail, int len)
	{
		boolean bRet = true;
		for (int i = 0; i < len; i++)
		{
			try
			{
				mail[i].setFlag(Flags.Flag.DELETED, true);
			} catch (Exception e)
			{
				bRet = false;
			}
		}
		return bRet;
	}

	public boolean setMailDelFlag(java.util.List mails, int len)
	{
		Message mail = null;
		boolean bRet = true;
		for (int i = 0; i < len; i++)
		{
			try
			{
				mail = (Message) mails.get(i);
				mail.setFlag(Flags.Flag.DELETED, true);
			} catch (Exception e)
			{
				bRet = false;
			}
		}
		return bRet;
	}

	public boolean fetchMail(String userId, String email,
			HttpSession session) throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserMbc(userId, email);
		if (mbc == null)
			throw new Exception("邮箱设置错误");
		return fetchMail(userId, mbc, session);
	}

	public void setSessionValue(HttpSession session, String varName,
			java.lang.Object value)
	{
		if (session != null)
		{
			if (session.getAttribute(varName) != null)
			{
				session.removeAttribute(varName);
			}
			session.setAttribute(varName, value);
		} else
		{
			logger.info("SetSession:" + varName
					+ "=" + value.toString());
		}
	}

	public MailBoxFolder getMailBoxFolder(String userId, int boxType) throws Exception
	{
		MailBoxFolder mbf = new MailBoxFolder(userId);
		if (mbf.load(userId, boxType) == false)
		{
			logger.info("Load MailBoxFolder false");
			return null;
		}
		// net.btdz.oa.common.TDebugOutMsg.outMsg("LIST:"+mbf.getMailList().size());
		return mbf;

		/*
		 * MailBoxFolder mbf=null;
		 * mbf=(MailBoxFolder)session.getAttribute("MailFolder"+boxType);
		 * if(mbf!=null) { return mbf; } else { mbf=new MailBoxFolder();
		 * if(mbf.load(userId,boxType)==false) {
		 * net.btdz.oa.common.TDebugOutMsg.outMsg("Load MailBoxFolder false");
		 * return null; } session.setAttribute("MailFolder"+boxType,mbf);
		 * //net.btdz.oa.common.TDebugOutMsg.outMsg("LIST:"+mbf.getMailList().size());
		 * return mbf; }
		 */
	}

	public int countNewMial(String userId, HttpSession session)
	{
		int iNewMail = 0;
		try
		{
			iNewMail = MailManager.getMailBoxFolder(userId,
					MailBoxFolder.FOLDER_CUR).getMailList()
					.getNewCount();
		} catch (Exception e)
		{
			logger.error("统计新邮件错误:"
					+ System14.FormatForJs(e.getMessage()));
		}
		return iNewMail;
	}

	public boolean moveMail(String userId, int form, int to,
			String mailId) throws Exception
	{
		MailInfo mi = null;
		MailBoxFolder mbfFrom = getMailBoxFolder(userId, form);
		mi = mbfFrom.getMail(mailId);
		if (mi == null)
			return false;
		MailBoxFolder mbfTo = getMailBoxFolder(userId, to);
		mbfFrom.moveOutMail(mailId);
		if(mbfTo.addMail(mi))
		{
			mbfTo.save();
			mbfFrom.save();
			return true;
		}
		return false;
	}

	/**
	 * 邮件查询
	 * 
	 * @param userId
	 * @param ss
	 * @param session
	 * @return
	 * @throws java.lang.Exception
	 */
	public MailInfoList search(String userId, SearchStruct ss) throws Exception
	{
		MailInfoList mil = getMailBoxFolder(userId, ss.FolderType)
				.getMailList();
		return MailTools.searchMail(ss, mil);
	}

	/**
	 * 邮件打包
	 * 
	 * @param userId
	 * @param mis
	 * @return
	 */
	public String createZip(String userId, String[] mis, int mailBox)
			throws Exception
	{
		boolean bRet = false;
		MailBoxFolder mbf = new MailBoxFolder(userId);
		try
		{
			bRet = mbf.load(userId, mailBox);
		} catch (Exception e)
		{
			throw new Exception("邮件打开失败：",e);
		}
		if (!bRet)
			return null;
		MailInfoList mil = mbf.findMail(mis);
		return createZip(userId, mil);
	}

	public MailInfo cologMail(MailInfo omi, String userId)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		return MailTools.cologMail(omi, path, MailTools.COLOG_COPY);
	}

	public MailInfo cologReply(MailInfo omi, String userId)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		return MailTools.cologMail(omi, path, MailTools.COLOG_NOAFFIX);
	}

	/**
	 * 复制邮件，转发协同用，附件存储到协同附件目录，去掉文件扩展名
	 * 
	 * @param omi
	 * @param userId
	 * @return
	 * @throws java.lang.Exception
	 */
	public MailInfo cologInfo(MailInfo omi, String userId)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		MailInfo mi = MailTools.cologMail(omi, path, MailTools.COLOG_DELEXT);
		mi.getAffixList().moveTo(LocalMailCfg.getUserInfoPath(userId));
		return mi;
	}

	public String createZip(String userId, MailInfoList mil)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		return MailTools.zipMail(mil, path);
	}

	public void test() throws Exception
	{
		MailInfo mi = null;
		MailInfoList mil = new MailInfoList();
		mi = new MailInfo();
		mi.setMailId("1111111");
		mi.setSubject("2222222subject");
		mi.setFrom("zhaghufrom@seeyon.com");
		mi.setTo("to@seeyon.com");
		mi.setContentText("zhangh", "youjina  张华的邮件正文");
		mi
				.getAffixList()
				.add(
						new Affix("附件.jpg",
								"C:\\upload\\~attachment\\zhangh\\00ff205bc415000b.GIF"));
		mi
				.getAffixList()
				.add(
						new Affix("附件666666.jpg",
								"C:\\upload\\~attachment\\zhangh\\00ff205bc2f20009.JPG"));

		mil.add(mi);

		mi = new MailInfo();
		mi.setMailId("2222222222");
		mi.setSubject("张华邮件测试subject");
		mi.setFrom("8888888m@seeyon.com");
		mi.setTo("tokkkkkkkkk@seeyon.com");
		mi.setContentText("zhangh", "hello加大好这是     第而风邮件测试");
		mi
				.getAffixList()
				.add(
						new Affix("附件5.jpg",
								"C:\\upload\\~attachment\\zhangh\\00ff205bc14e0006.JPG"));
		mi
				.getAffixList()
				.add(
						new Affix("4.jpg",
								"C:\\upload\\~attachment\\zhangh\\00ff205bc05e0004.JPG"));

		mil.add(mi);
		logger.info("Mail Len:" + mil.size());
		logger.info(createZip("zhangh", mil));
	}
	
	/*
	public void testFetch() throws Exception
	{
		MailBoxCfg mbc = new MailBoxCfg();
		mbc.setEmail("zhangh@seeyon.com");
		mbc.setUserName("zhangh@seeyon.com");
		mbc.setPassword("123456");
		mbc.setPop3Host("pop3.sina.net");
		mbc.setSmtpHost("smtp.sian.net");
		mbc.setAuthorCheck(true);
		mbc.setBackup(true);
		fetchMail("wuxy", "wsh_huan@sina.com", null, );
		logger.info("Featch OVER");
	}

	public void testSend() throws Exception
	{
		MailInfo mi = new MailInfo();
		mi.setFrom("张华<zhangh@seeyon.com>");
		mi.setTo("zhangh@seeyon.com");
		mi.setSubject("邮件测试，没有附件");
		mi.setContentText("zhangh", "<br>邮件内容");
		MailBoxCfg mbc = new MailBoxCfg();
		mbc.setEmail("zhanghuabf@sohu.com");
		mbc.setUserName("zhanghuabf");
		mbc.setPassword("zhanghua");
		mbc.setPop3Host("pop3.sohu.com");
		mbc.setSmtpHost("smtp.sohu.com");
		mbc.setDefaultBox(false);
		mbc.setBackup(true);
		sendMail(mbc, mi);
	}

	public void testZipFolder() throws Exception
	{
		MailBoxFolder mbf = new MailBoxFolder();
		mbf.load("zhangh", MailBoxFolder.FOLDER_CUR);
		MailInfoList mil = mbf.getMailList();
		String zipFile = createZip("zhangh", mil);
		logger.info("zipFile:" + zipFile);
	}
	*/

	public boolean sendMail(String smtpServe, String userName,
			String pass, MailInfo mi) throws Exception
	{
		MailBoxCfg mbc = new MailBoxCfg();
		mbc.setUserName(userName);
		mbc.setEmail(mi.getFrom());
		mbc.setSmtpHost(smtpServe);
		mbc.setPassword(pass);
		mbc.setAuthorCheck(true);
		return MailManager.sendMail(mbc, mi);
	}
	
	public MailInfo getMailInfoById(long userId, long id)throws Exception
	{
		MailInfo mi = null;
		int[] folderType = {0,1,2,3};
		for(int i = 0; i < folderType.length; i++){
			MailBoxFolder mbf = null;
			try {
				mbf = MailManager.getMailBoxFolder(String.valueOf(userId), folderType[i]);
				MailInfoList mils = mbf.getMailList();
				for(int j = 0; j < mils.size(); j++){
					MailInfo temp = mils.get(j);
					if(temp.getMailLongId() == id){
						mi = temp;
						return mi;
					}
				}
			}catch(Exception ex){
				throw ex;
			}
		}
		return mi;
	}
	
	public ModelAndView forwordMail(long referenceId, String subject, String bodyContent, List<Attachment> atts){
		ModelAndView mav = new ModelAndView("webmail/new/send");
		mav.addObject("subject", subject);
		mav.addObject("contentText", bodyContent);
		mav.addObject("attachments", atts);
		mav.addObject("referenceId", referenceId);
		mav.addObject("originalAttsNeedClone", "true");
		return mav;
	}
	
	/**
	 * 判断用户是否设置邮箱，用于协同转发邮件前ajax调用
	 * @param userId  用户Id，如果为currentUser，则取当前用户id
	 * @return 
	 */
	public String hasDefaultMailBox(String userId) {
		boolean hasMailBox = false;
		String user = userId;
		if("currentUser".equals(userId))
			user = String.valueOf(CurrentUser.get().getId());
		try {
			hasMailBox = MailBoxManager.findUserDefaultMbc(user)!=null;
		}catch(Exception e) {
			//主要应用于ajax调用，所以不向上抛出异常
			logger.error("获取用户缺省邮箱时异常，用户id:"+userId, e);
		}
		return String.valueOf(hasMailBox);
	}
	/**
	 * 判断邮件大小是否超出当前用户的邮件空间大小
	 * @param userId
	 * @param mi
	 * @return true 没超出   false 超出
	 */
	public boolean checkMailSpace(String userId,MailInfo mi){
		try {
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(userId);
			String affixPath = LocalMailCfg.getUserTempPath(userId);
			MimeMessage mail = MailTools.changFormat(mbc,mi);
			boolean isHtmlContent = MailTools.htmlFlag((Part) mail);
			if (isHtmlContent) {
				mi.setContentType("html");
			} else {
				mi.setContentType("nohtml");
			}
			String contentFilePath=MailTools.getContent(mail, affixPath, isHtmlContent);
			mi.setSize(MailTools.getSize(contentFilePath) + mi.getAffixList().getLength());
			long mailUsedSize = mi.getSize();
			DocStorageSpace docSpace = docSpaceManager.getDocSpaceByUserId(Long.parseLong(userId));
			long mailSize = docSpace.getMailSpace();
			long mailOcuppiedSize = LocalMailCfg.getMailSpaceSize(String.valueOf(docSpace.getUserId()));
			if(mailOcuppiedSize + mailUsedSize >= mailSize){
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("查找用户默认邮箱出错" + e);
			return false;
		}
	}
	public DocSpaceManager getDocSpaceManager()
	{
		return docSpaceManager;
	}

	public void setDocSpaceManager(DocSpaceManager docSpaceManager)
	{
		this.docSpaceManager = docSpaceManager;
	}
}