package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description:对本地邮件的管理 </p>
 * 本地邮件的基本信息（标题、发件人、附件信息等）存储到列表文件中，邮件的正文和附件都以文件形式存放到附件目录中
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import javax.mail.*;
import javax.servlet.http.HttpSession;
import javax.mail.internet.MimeMessage;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.domain.SearchStruct;
import com.seeyon.v3x.webmail.util.*;

public class MailManager
{
	// 定义是否使用session保存邮件列表，避免每次打开是读取里表文件
	private static boolean useSession = true;
	
	private final static Log logger = LogFactory.getLog(MailManager.class);

	public MailManager()
	{
	}

	public static boolean sendMail(String userId, MailInfo mi) throws Exception
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

	public static boolean sendMail(String userId, String sendMailBoxName,
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
			throw new Exception("发送失败:" + System14.FormatForJs(e.getMessage()));
		}
	}

	public static boolean sendMail(MailBoxCfg mbc, MailInfo mi)
			throws Exception
	{
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
		} catch (javax.mail.SendFailedException e)
		{
			throw new Exception(MailErrMsg.formatErrMsg(e.getMessage()));
		} catch (javax.mail.AuthenticationFailedException ea)
		{
			throw new Exception(MailErrMsg.formatErrMsg(ea.getMessage()));
		} catch (Exception es)
		{
			throw new Exception(MailErrMsg.formatErrMsg(es.getMessage()));
		}
		return true;
	}

	public static boolean fetchMail(String userId, MailBoxCfg mbc, HttpSession session) throws Exception
	{
		String errMsg = "";
		java.util.List recMails = new java.util.ArrayList();
		Store pop3Store = null;
		com.sun.mail.pop3.POP3Folder pop3Folder = null;
		pop3Store = ConnMailhost.getPop3Store(mbc);
		try
		{
			if (!pop3Store.isConnected())
			{
				pop3Store.connect();
			}
		} catch (javax.mail.AuthenticationFailedException e)
		{
			throw new Exception("用户名称或者密码错误,请重新设置");
		} catch (MessagingException e)
		{
			// ****修改号="20060427zhangh_001" 区域="1" *****************开始//
			logger.error("(" + userId + ")"
					+ e.getMessage());
			throw new Exception(MailErrMsg.formatErrMsg(e.getMessage()));
			// ****修改号="20060427zhangh_001" 区域="1" *****************结束//
		} catch (Exception e)
		{
			logger.error("(" + userId + ")"
					+ e.getMessage());
			throw new Exception("无法连接远程POP邮件服务器，请检查网络连接、POP帐户设置是否正确！");
		}
		pop3Folder = (com.sun.mail.pop3.POP3Folder) pop3Store
				.getDefaultFolder().getFolder("INBOX");
		if (!pop3Folder.isOpen())
		{
			pop3Folder.open(Folder.READ_WRITE);
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
			boolean remoteDel = !mbc.getBackup();
			int i;
			String mid = "";
			// 统计新邮件数量开始
			List newMailList = new ArrayList();
			for (i = 0; i < msgs.length; i++)
			{
				mid = pop3Folder.getUID(msgs[i]);
				if (!lmm.exist(mid))
				{
					newMailList.add(new Integer(i));
				}
			}
			messageCounts = newMailList.size();
			setSessionValue(session, "msgCount", new Integer(messageCounts));
			// 统计新邮件数量结束
			for (i = 0; i < messageCounts; i++)
			{
				int curId = Integer.parseInt(newMailList.get(i).toString());
				try
				{
					mid = pop3Folder.getUID(msgs[curId]);
					msgs[curId].setFlag(Flags.Flag.RECENT, true);
					setSessionValue(session, "curMsgNo", new Integer(i + 1));
					try
					{
						mi = MailTools.changFormat(mid, msgs[curId], affixPath);
					} catch (Exception me)
					{// 邮件解析错误时，继续接收后面得邮件
						errMsg += me.getMessage() + "\r\n";
						continue;
					}
					recMails.add(msgs[curId]);// 解析成功后，添加到列表中，统一删除邮件服务器上得邮件
					mi.setMailBoxName(mbc.getEmail());
					mbf.addMail(mi, true);
					lmm.add(mid, false);
					if (isSaveMailId == false)
					{
						isSaveMailId = true;
					}
				} catch (Exception e)
				{// 邮件解析过程中发生异常，保存邮件列表和邮件id列表
					if (isSaveMailId && mbf.save() && lmm.save() && remoteDel)
					{
						setMailDelFlag(recMails, i - 1);
					}
					throw new Exception("邮件解析错误，邮件未接收完:"
							+ System14.FormatForJs(e.getMessage()));
				}
			}
			if (isSaveMailId && mbf.save() && lmm.save() && remoteDel)
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

	private static boolean setMailDelFlag(Message[] mail, int len)
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

	private static boolean setMailDelFlag(java.util.List mails, int len)
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

	public static boolean fetchMail(String userId, String email,
			HttpSession session) throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserMbc(userId, email);
		if (mbc == null)
			throw new Exception("邮箱设置错误");
		return fetchMail(userId, mbc, session);
	}

	public static void setSessionValue(HttpSession session, String varName,
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

	public static MailBoxFolder getMailBoxFolder(String userId, int boxType) throws Exception
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

	public static int countNewMial(String userId)
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

	public static boolean moveMail(String userId, int form, int to,
			String mailId) throws Exception
	{
		MailInfo mi = null;
		MailBoxFolder mbfFrom = getMailBoxFolder(userId, form);
		mi = mbfFrom.getMail(mailId);
		if (mi == null)
			return false;
		MailBoxFolder mbfTo = getMailBoxFolder(userId, to);
		mbfFrom.moveOutMail(mailId);
		return mbfTo.addMail(mi);
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
	public static MailInfoList search(String userId, SearchStruct ss) throws Exception
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
	public static String createZip(String userId, String[] mis, int mailBox)
			throws Exception
	{
		boolean bRet = false;
		MailBoxFolder mbf = new MailBoxFolder(userId);
		try
		{
			bRet = mbf.load(userId, mailBox);
		} catch (Exception e)
		{
			throw new Exception("邮件打开失败：" + e.getMessage());
		}
		if (!bRet)
			return null;
		MailInfoList mil = mbf.findMail(mis);
		return createZip(userId, mil);
	}

	public static MailInfo cologMail(MailInfo omi, String userId)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		return MailTools.cologMail(omi, path, MailTools.COLOG_COPY);
	}

	public static MailInfo cologReply(MailInfo omi, String userId)
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
	public static MailInfo cologInfo(MailInfo omi, String userId)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		MailInfo mi = MailTools.cologMail(omi, path, MailTools.COLOG_DELEXT);
		mi.getAffixList().moveTo(LocalMailCfg.getUserInfoPath(userId));
		return mi;
	}

	public static String createZip(String userId, MailInfoList mil)
			throws Exception
	{
		String path = LocalMailCfg.getUserTempPath(userId);
		return MailTools.zipMail(mil, path);
	}

	private static void test() throws Exception
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

	public static void testFetch() throws Exception
	{
		MailBoxCfg mbc = new MailBoxCfg();
		mbc.setEmail("zhangh@seeyon.com");
		mbc.setUserName("zhangh@seeyon.com");
		mbc.setPassword("123456");
		mbc.setPop3Host("pop3.sina.net");
		mbc.setSmtpHost("smtp.sian.net");
		mbc.setAuthorCheck(true);
		mbc.setBackup(true);
		fetchMail("wuxy", "wsh_huan@sina.com", null);
		logger.info("Featch OVER");
	}

	public static void testSend() throws Exception
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

	public static void testZipFolder() throws Exception
	{
		MailBoxFolder mbf = new MailBoxFolder("123");
		mbf.load("zhangh", MailBoxFolder.FOLDER_CUR);
		MailInfoList mil = mbf.getMailList();
		String zipFile = createZip("zhangh", mil);
		logger.info("zipFile:" + zipFile);
	}

	public static void main(String[] args)
	{
		MailManager mailManager1 = new MailManager();
		try
		{
			/*
			 * SearchStruct ss=new SearchStruct();
			 * ss.FolderType=MailBoxFolder.FOLDER_CUR; ss.subject="";
			 * MailInfoList mil=search("zhangh",ss,null);
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("find count:"+mil.size());
			 */
			// MailManager.fetchMail("zhangh","zhangh@seeyon.com",null);
			testSend();
			// testFetch();
			/*
			 * MailInfo mi=new MailInfo(); mi.setTo("zhanghuabf@163.com");
			 * mi.setSubject("y邮件收条测试"); mi.setContentText("zhangh","you收条测试");
			 * mi.setReply(true); mi.setReplyTo("zhangh@seeyon.com");
			 * MailManager.sendMail("zhangh",mi);
			 */
			// MailManager.fetchMail("zhangh","zhangh@seeyon.com",null);
			/*
			 * MailBoxFolder
			 * mbf=MailManager.getMailBoxFolder("zhangh",MailBoxFolder.FOLDER_CUR,null);
			 * MailInfoList mil=mbf.getMailList(); mbf.sortBySendDate(); int
			 * i,len=mil.size(); for(i=0;i<len;i++) {
			 * net.btdz.oa.common.TDebugOutMsg.outMsg(DateUtil.formatDate(mil.get(i).getSendDate())); }
			 */
			logger.info("send OVER");
		} catch (Exception e)
		{
			System.err.println("err:" + e.getMessage());
		}
	}

	public static boolean sendMail(String smtpServe, String userName,
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

}