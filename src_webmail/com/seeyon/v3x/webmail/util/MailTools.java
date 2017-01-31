package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description: 邮件处理工具</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.domain.SearchStruct;
import com.seeyon.v3x.webmail.manager.ConnMailhost;
import com.seeyon.v3x.webmail.manager.LocalMailCfg;

public class MailTools {
	public final static int FORSEND = 0;

	public final static int FORSAVE = 0;

	// 邮件克隆方式
	public final static String COLOG_COPY = "copy";// 附件重命名，扩展名不变

	public final static String COLOG_DELEXT = "delext";// 附件重命名，去掉扩展名

	public final static String COLOG_NOAFFIX = "noAffix";// 不拷贝附件

	private final static Log logger = LogFactory.getLog(MailTools.class);

	private final static BASE64Encoder enc = new BASE64Encoder();
	private static FileManager fileManager = (FileManager) ApplicationContextHolder.getBean("fileManager");
	/**
	 * 把接收到的邮件对象解析成MailInfo对象，邮件的正文、附件先放到用户附件临时目录
	 * 
	 * @param mailId:邮件ID，邮件服务器存放的编号，接收邮件的时候得到
	 * @param mail
	 * @param affixPath:附件存放路径
	 * @return
	 */
	public static MailInfo changFormat(String mailId, Message mail, String affixPath) throws Exception {
		MailInfo mi = new MailInfo();
		if (createPath(affixPath) == false)
			return null;
		MimeMessage mm = (MimeMessage) mail;
		try {
			mi.setMailNumber(UniqueCode.generate());
			mi.setMailId(mailId);
			mi.setSubject(getSubject(mm));
			mi.setFrom(getFrom(mm));
			mi.setTo(getTo(mm));
			mi.setCc(getCc(mm));
			mi.setBc(getBc(mm));
			mi.setReplyTo(getReplyTo(mm));
			mi.setSendDate(getSendDate(mm));
			mi.setPriority(getPriority(mm));
			setIsReply(mm, mi);
			// 邮件解析过程中，正文和附件得解析最容易出错误，放到最后解析
			boolean isHtmlContent = htmlFlag((Part) mm);
			if (isHtmlContent) {
				mi.setContentType("html");
			} else {
				mi.setContentType("nohtml");
			}
			String contentFilePath=getContent(mm, affixPath, isHtmlContent);
			mi.setContentFile(contentFilePath);
			AffixList affixList= dumpPart((Part) mm, affixPath, mm);
			mi.setSize(getSize(contentFilePath)+affixList.getLength());
			mi.setAffixList(affixList);
		} catch (Exception e) {
			logger.error("邮件《" + mi.getSubject() + "》" + e.getMessage(),e);
			throw new Exception("邮件《" + mi.getSubject() + "》" + e.getMessage());
		}
		return mi;
	}

	/**
	 * 本地邮件存储格式转换成标准的邮件格式
	 * 
	 * @param mbc：返回的Message发送时，传递发送的邮箱，保存时传递null
	 *            2004-9-28 邮件发送从客户端接收数据后先编码，发送时候不进行编码
	 * @param mi
	 * @return
	 * @throws java.lang.Exception
	 */
	public static MimeMessage changFormat(MailBoxCfg mbc, MailInfo mi) throws Exception {
		Session sess = null;
		if (mbc == null) {
//			throw new Exception("没有邮件配置信息");
		} else {
			sess = ConnMailhost.getSmtpSession(mbc);
			if (sess == null)
				throw new Exception("连接到邮件服务器失败，请检查网络是否连接、邮件配置是否正确");
		}
		return changFormat(sess, mi);
	}

	public static MimeMessage changFormat(javax.mail.Session sess, MailInfo mi) throws Exception {
		String tempStr = null;
		String content = null;
		Message msg = new MimeMessage(sess);
		if (mi.getReply()) {
			msg.addHeader("Disposition-Notification-To", mi.getReplyTo());
		}
		msg.setHeader("Content-Type", "text/plain; charset=" + str.getChineseGBK());
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MimeUtility.decodeText(mi.getTo()), false));

		if ((tempStr = mi.getFrom()) != null && !tempStr.equals("")) {
			msg.setFrom(new InternetAddress(mi.getFromAdd(), mi.getFromName(), str.getChineseGBK()));
		}
		if ((tempStr = mi.getCc()) != null && !tempStr.equals("")) {
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(tempStr, false));
		}
		if ((tempStr = mi.getBc()) != null && !tempStr.equals("")) {
			msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(tempStr, false));
		}
		msg.setHeader("X-Priority", Integer.toString(mi.getPriority()));
		msg.setHeader("X-Mailer", "javamail");
		msg.setSubject("=?UTF-8?B?" + enc.encode(mi.getSubject().getBytes("UTF-8")).replaceAll("\\s", "") + "?=");

		msg.setSentDate(mi.getSendDate());
		content = mi.getContentText();
		MimeMultipart mmp =null;
		mmp = new MimeMultipart("mixed");
		MimeBodyPart mbp1 = null;
		mbp1 = new MimeBodyPart();
		Object[] makePictureContent = makePictureContent(mmp,mbp1,content);
		mmp.addBodyPart((MimeBodyPart)makePictureContent[0]);
		Map imgid = (Map)makePictureContent[1];
		if (mi.hasAffix() != false) {
			AffixList affixList = mi.getAffixList();
			if(affixList!=null)
			{
				for (int i = 0; i < affixList.size(); i++) {
					Affix affix = affixList.get(i);
					if(affix==null)
					{
						continue;
					}
					if (imgid.get(affix.getContentId()) != null){
						continue;
					}
					MimeBodyPart createAttachment = createAttachment(affix);
					mmp.addBodyPart(createAttachment);
				}
			}
		}
		if(mmp!=null)
		{
			msg.setContent(mmp);
		}
		msg.saveChanges();
		return (MimeMessage)msg;
	}

	private static Object[] makePictureContent(MimeMultipart mmp,MimeBodyPart mdp,String content)
	{	
		MimeBodyPart createContent = null;
		String newContent = content;
		Map imgid = new HashMap<Long, Boolean>();
		Matcher m = Pattern.compile("<\\s*img\\s+([^>]*)\\s*>", Pattern.CASE_INSENSITIVE).matcher(content);
		while(m.find())
			{
				Matcher srcMatcher = Pattern.compile("src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(m.group());
				if(srcMatcher.find())
				{
				 	try {
				 		 if(srcMatcher.group().indexOf("http:")==-1)
	                     {
				 			String t = srcMatcher.group().substring(
					 				srcMatcher.group().indexOf("fileId="));
					 		t = t.substring(0, t.indexOf("&amp;"));
					 		String[] arr=t.split("=");
					 		content = content.replace(srcMatcher.group(),"src=\"cid:"+arr[1]+"\"");
					 		imgid.put(arr[1], true);
						}
					} catch (Exception e) {
						logger.error("", e);
					}
				}
		}
		try {
			createContent = createContent(content,newContent);
		} catch (MessagingException e) {
			logger.error("",e);
		}
		return new Object[]{createContent,imgid};
	}

	private static String getFileAbstractPath(String fileId) {
		if (Strings.isBlank(fileId)) {
			return null;
		}
		File file = null;
		try {
			V3XFile v3xFile = fileManager.getV3XFile(Long.parseLong(fileId));
			file = fileManager.getFile(Long.parseLong(fileId), v3xFile
					.getCreateDate());
			if (file == null || !file.exists()) {
				return null;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return file.getAbsolutePath();
	}
	/**
	 * 邮件打包
	 * 
	 * @param ml
	 * @param tempPath：压缩文件存放路径（mailInfo转化成eml的临时文件也存放在此路径）
	 * @return：压缩文件全部路径
	 */
	public static String zipMail(MailInfoList ml, String tempPath) throws Exception {
		String mailPath=null;
		// 打包压缩
		String zipFile=null;
		try {
			MailBoxCfg mbc = null;
			ConnMailhost conn = new ConnMailhost();
			Store store = null;
			store = conn.getLocalStore(tempPath);
			if (!store.isConnected()) {
				store.connect();
			}
			/* 清空临时目录所有文件 */
			String path = tempPath + ".zip/";
			mailPath = path + "cur/";
			FileUtil.delDirectory(path);
			MimeMessage mm = null;
			if(ml==null)
			{
				return "";
			}
			Folder tempFolder = store.getFolder("zip");
			/* 转化邮件格式 */
			if (!tempFolder.isOpen()) {
				tempFolder.open(Folder.READ_WRITE);
			}
			for (int i = 0; i < ml.size(); i++) {
				mm = changFormat(mbc, ml.get(i));
				tempFolder.appendMessages(new Message[] { mm });
			}
			tempFolder.close(true);
			FileUtil.setFileExtName(mailPath, "eml");
			zipFile = tempPath + "mail.zip";
		} catch (Exception e1) {
			logger.error("邮件压缩错误",e1);
		}
		FileUtil.delDirectory(zipFile);
		try {
			FileUtil.zip(zipFile, mailPath);
		} catch (Exception e) {
			logger.error("",e);
			throw new Exception("邮件压缩错误：" ,e);
		}
		return zipFile;
	}

	/**
	 * 克隆邮件
	 * 
	 * @param omi
	 * @param path：新邮件正文、附件存放路径
	 * @param coloType:新文件得命名方式,copy:文件名称不变,delext:去扩展名称,noAffix,不克隆附件，用于回复
	 * @return
	 */
	public static MailInfo cologMail(MailInfo omi, String path, String coloType) throws Exception {
		MailInfo nmi = new MailInfo();
		nmi.setMailId(UniqueCode.generate());
		nmi.setFrom(omi.getFrom());
		nmi.setTo(omi.getTo());
		nmi.setBc(omi.getBc());
		nmi.setCc(omi.getCc());
		nmi.setSubject(omi.getSubject());
		nmi.setPriority(omi.getPriority());
		nmi.setSendDate(omi.getSendDate());
		nmi.setReply(omi.getReply());
		nmi.setReplyTo(omi.getReplyTo());
		nmi.setSize(nmi.getSize());
		nmi.setContentText(path, omi.getContentText(), null);
		if (MailTools.COLOG_NOAFFIX.equals(coloType)) {
		} else {
			nmi.setAffixList(omi.getAffixList().colog(path, coloType));
		}
		return nmi;
	}

	/**
	 * 去掉邮件列表中开始和结束',',';'字符；用，分割；等
	 * 
	 * @param mailList
	 * @return
	 */
	public static String adjustMailAdd(String mailList) {
		String str = mailList;
		boolean tempb = true;
		int j;
		while (tempb) {
			if (str.startsWith(",") || str.startsWith(";"))
				str = str.substring(1);
			else if (str.endsWith(",") || str.startsWith(";"))
				str = str.substring(0, str.length() - 1);
			else if (str.indexOf(",,") != -1 || str.indexOf(";;") != -1) {
				j = str.indexOf(",,");
				str = str.substring(0, j) + str.substring(j);
			} else {
				tempb = false;
			}
		}
		return str;
	}

	/**
	 * 删除用户临时文件，包括新建邮件时上传附件，后删除的，转发时生成的附件等
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean delTempFile(String userId) {
		String path = "";
		path = LocalMailCfg.getUserTempPath(userId);
		return FileUtil.delDirectory(path);
	}

	/**
	 * 邮件修改时，删除用户删除的附件
	 * 
	 * @param source
	 * @param curr
	 * @return
	 */
	public static boolean delAffix(AffixList source, AffixList curr) {
		int i, j, is, ic;
		String fileName = "";
		is = source.size();
		ic = curr.size();
		for (i = 0; i < is; i++) {
			fileName = source.get(i).getRealPath();
			for (j = 0; j < ic; j++) {
				if (fileName.equals(curr.get(j).getRealPath())) {
					break;
				}
			}
			FileUtil.delDirectory(fileName);
		}
		return true;
	}

	public static MailInfoList searchMail(SearchStruct sw, MailInfoList mils) {
		int i, len;
		MailInfo mi = null;
		MailInfoList tempMil = new MailInfoList(), mil = new MailInfoList();
		tempMil.add(mils);
		mil.add(mils);
		// 标题查询
		if (sw.subject != null && !sw.subject.equals("")) {
			len = tempMil.size();
			mil = new MailInfoList();
			for (i = 0; i < len; i++) {
				mi = tempMil.get(i);
				if (mi.getSubject().indexOf(sw.subject) != -1) {
					mil.add(mi);
				}
			}
			tempMil = mil;
		}

		// 发件人
		if (sw.from != null && !sw.from.equals("")) {
			len = tempMil.size();
			mil = new MailInfoList();
			for (i = 0; i < len; i++) {
				mi = tempMil.get(i);
				if (mi.getFrom().indexOf(sw.from) != -1) {
					mil.add(mi);
				}
			}
			tempMil = mil;
		}

		// 收件人
		if (sw.to != null && !sw.to.equals("")) {
			len = tempMil.size();
			mil = new MailInfoList();
			for (i = 0; i < len; i++) {
				mi = tempMil.get(i);
				if (mi.getTo().indexOf(sw.to) != -1) {
					mil.add(mi);
				}
			}
			tempMil = mil;
		}

		// 邮件发送日期
		if (sw.createDate != null) {
			len = tempMil.size();
			mil = new MailInfoList();
			for (i = 0; i < len; i++) {
				mi = tempMil.get(i);
				if (compareDate(sw.dateType, mi.getSendDate(), sw.createDate)) {
					mil.add(mi);
				}
			}
			tempMil = mil;
		}
		return mil;
	}

	/**
	 * 根据用户ID得到发送者的名称 不属于邮件发送部分
	 * 
	 * @param userId
	 * @return
	 */
	private static String getSenderName(String userId) {
		String trueName = "";
		try {
			// trueName = PersonFinder.findByUserName(userId).getTrueName();
			trueName = "liusg";
		} catch (Exception e) {
		}
		return trueName;
	}

	private static boolean createPath(String path) {
		boolean ret = false;
		File f = new File(path);
		if (!f.exists()) {
			ret = f.mkdirs();
		} else {
			ret = true;
		}
		return ret;
	}

	private static String iso2gb(String qs) throws IOException {
		if (qs == null)
			return "";
		else {
			// Used for TurboLinux
			return str.UnicodetoChinese(qs);
			// return qs;
		}
	}
	
	private static String utf2gb2312(String qs) throws IOException {
		if (qs == null)
			return "";
		else {
			// Used for TurboLinux
			return new String(qs.getBytes(str.getChineseCharset()),"GB2312");
			// return qs;
		}
	}

	/**
	 * 得到发件人
	 * 
	 * @param mail
	 * @return
	 * @throws java.lang.Exception
	 */
	protected static String getFrom(MimeMessage mail) throws Exception {
		String from = "";
		try {
			String fromStr = mail.getHeader("From", null);
			if (fromStr != null && fromStr.length() > 0) {
				InternetAddress[] ia = null;
				try {
					ia = InternetAddress.parse(fromStr);
					from = getPerNameMail(ia);
				} catch (Exception e) {// 对了邮件from地址为中文的不符合规则的编码异常处理
					from = getDecode(fromStr);
				}
			}
		} catch (Exception e) {
			logger.error("解析邮件发件人地址错误,错误码（" + e.hashCode() + "）错误（" + e.getMessage() + "）", e);
//			throw new Exception("解析邮件发件人地址错误,错误码（" + e.hashCode() + "）错误（" + e.getMessage() + "）",e);
		}
		return from;
	}

//	protected static int getSize(MimeMessage mail) {
//		try {
//			return mail.getSize();
//		} catch (Exception e) {
//			return 0;
//		}
//	}
	public static long getSize(String mail) {
		try {
			File f=new File(mail);
			if(f.exists() && f.isFile())
			{
				return f.length();
			}
			return 0l;
		} catch (Exception e) {
			return 0l;
		}
	}

	protected static void setIsReply(MimeMessage mail, MailInfo mi) {
		try {
			String[] needreply = mail.getHeader("Disposition-Notification-To");
			if (needreply != null) {
				mi.setNoteAddress(needreply[0].trim());
				mi.setReply(true);
			}
		} catch (Exception e) {
		}
	}

	protected static int getPriority(MimeMessage mail) {
		try {
			String ps = null;
			String[] apriority = mail.getHeader("X-Priority");
			if (apriority != null)
				ps = apriority[0];
			else
				ps = "2";
			return Integer.parseInt(ps);
		} catch (Exception e) {
			return 3;
		}
	}

	protected static String getReplyTo(MimeMessage mail) {
		String replyTo = "";
		try {
			InternetAddress[] ia = (InternetAddress[]) mail.getReplyTo();
			replyTo = getPerNameMail(ia);
		} catch (Exception e) {
			logger.error("",e);
		}
		return replyTo;
	}

	protected static Date getSendDate(MimeMessage mail) {
		Date td = null;
		try {
			td = mail.getSentDate();
			if (td == null) {
				td = new Date(System.currentTimeMillis());
			}
			return td;
		} catch (Exception e) {// 邮件发送日期发生异常时，设置为解析邮件的日期
			return new Date(System.currentTimeMillis());
		}
	}

	/**
	 * 得到收件人
	 * 
	 * @param mail
	 * @return
	 * @throws java.lang.Exception
	 */
	protected static String getTo(MimeMessage mail) throws Exception {
		String to = mail.getHeader("To", null);
		try {
			InternetAddress[] ia = (InternetAddress[]) mail.getRecipients(Message.RecipientType.TO);
			to = getPerNameMail(ia);
		} catch (Exception e) {
			logger.error("得到收件人",e);
		}
		return to;
	}

	/**
	 * 得到抄送
	 * 
	 * @param mail
	 * @return
	 * @throws java.lang.Exception
	 */
	protected static String getCc(MimeMessage mail) throws Exception {
		String cc = mail.getHeader("Cc", null);
		try {
			InternetAddress[] ia = (InternetAddress[]) mail.getRecipients(Message.RecipientType.CC);
			cc = getPerNameMail(ia);
		} catch (Exception e) {
			logger.error("得到抄送",e);
		}
		return cc;
	}

	/**
	 * 得到暗送
	 * 
	 * @param mail
	 * @return
	 * @throws java.lang.Exception
	 */
	protected static String getBc(MimeMessage mail) throws Exception {
		String bcc = mail.getHeader("Bcc", null);
		try {
			InternetAddress[] ia = (InternetAddress[]) mail.getRecipients(Message.RecipientType.BCC);
			bcc = getPerNameMail(ia);
		} catch (Exception e) {
			logger.error("得到暗送",e);
		}
		return bcc;
	}

	protected static String getSubject(MimeMessage mail) throws Exception {
		String subj = "";
		subj = mail.getSubject();
		if (subj == null) {
			subj = "(无)";
		} else {
			subj = mail.getHeader("subject", null);
			subj = getDecode(subj);
		}
		return subj;
	}

	/**
	 * 把邮件正文解析成文件保存
	 * 
	 * @param mail
	 * @param tempPath:解析的文件存放路径
	 * @return：返回邮件路径，错误返回NULL
	 * @throws java.lang.Exception
	 *             已知bug：163退回得邮件读取得正文不正确
	 */
	protected static String getContent(MimeMessage mail, String tempPath) throws Exception {
		/*
		 * String path=tempPath; path+=UniqueCode.generate(); path+=".main";
		 * try{ FileWriter f = new FileWriter(path); boolean htmlFlag =
		 * htmlFlag( (Part) mail); dispText( (Part) mail, f, htmlFlag);
		 * f.flush(); f.close(); } catch(Exception e) {
		 * net.btdz.oa.common.TDebugOutMsg.outMsg("得到邮件内容错误："+e.getMessage());
		 * path=null; } return path;
		 */
		boolean isHtmlContent = true;
		try {
			isHtmlContent = htmlFlag((Part) mail);
		} catch (Exception e) {
			logger.error("判断邮件正文类型异常：" + e.getMessage());
			return null;
		}
		return getContent(mail, tempPath, isHtmlContent);
	}

	/**
	 * 把邮件正文解析成文件保存
	 * 
	 * @param mail
	 * @param tempPath:解析的文件存放路径
	 * @param isHtmlContent:邮件正文是否为HTML
	 * @return：返回邮件路径，错误返回NULL
	 * @throws java.lang.Exception
	 *             已知bug：163退回得邮件读取得正文不正确
	 */
	public static String getContent(MimeMessage mail, String tempPath, boolean isHtmlContent) throws Exception {
		String path = tempPath;
		path += UniqueCode.generate();
		path += ".main";
		FileWriter f=null;
		try {
			f = new FileWriter(path);
			dispText((Part) mail, f, isHtmlContent);
			f.flush();
		} catch (Exception e) {
			logger.error("得到邮件内容错误：" + e.getMessage(), e);
			path = null;
		} finally {
             if(f!=null)
             {
            	 f.close();
             }
		}
		return path;
	}

	/**
	 * 解析附件
	 * 
	 * @param p
	 * @param attachFilePath
	 * @param out
	 * @throws java.lang.Exception
	 */
	protected static AffixList dumpPart(Part p, String attachFilePath ,MimeMessage mm) throws Exception {
		String realPath = null;
		AffixList al = new AffixList();
		String ct = p.getContentType().toLowerCase();
		String filename = "";
		// zhangh 2004-10-21 发现在163退回得邮件中调用下列方法会发生异常，附件是原始得邮件
		try {
			filename = p.getFileName();
		} catch (Exception e) {
			filename = "source.eml";
		}
		/*
		 * Using isMimeType to determine the content type avoids fetching the
		 * actual content data until we need it.
		 */
		if (p.isMimeType("text/plain") && ct.indexOf("name") < 0) {
		} else if (p.isMimeType("text/html") && ct.indexOf("name") < 0) {
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			int count = 0;
			try {
				count = mp.getCount();
			} catch (Exception e) {
				// ****修改号="20060526zhangh_001" 区域="2" *****************//
				// mp.getCount()函数发生异常后，无法继续继续，把整个部分作为email格式附件存取
				if (filename == null) {
					filename = MailTools.getSubject((MimeMessage) p);
				}
				if(!mm.getFolder().isOpen()){ //判断是否open,如果close，就重新open  
					mm.getFolder().open(Folder.READ_WRITE);
				}
				realPath = saveFileAsEmail(p, attachFilePath, filename);
				al.add(new Affix(filename, realPath));
				return al;
			}
			for (int i = 0; i < count; i++) {
				AffixList cal = dumpPart(mp.getBodyPart(i), attachFilePath ,mm);
				al.add(cal);
			}
		}
		// else if (p.isMimeType("message/rfc822"))
		// {dumpPart((Part)p.getContent(),attachFilePath,out);}
		else {
			// Datahandler dh = p.getDatahandler();
			if (filename == null && ct.equals("message/disposition-notification")) {// 这种情况在outlook中解析会得到一个txt附件，foxmail会把附件内容放到正文中；我们得的的是没有文件名称的附件，给附件命名
				filename = "noname.txt";
			}
			if (filename != null) {
				filename = getDecode(filename);
				filename = adjustFileName(filename);
				realPath = saveFile(p, attachFilePath, filename);
				
				String contentId="";
				String contentDisposition="";
				if(p instanceof MimeBodyPart)
				{
					MimeBodyPart mmTemp=(MimeBodyPart)p;					
					contentId=mmTemp.getContentID();
					contentDisposition=mmTemp.getDisposition();
				}
				if (!filename.equals("ATT00001.bin")) {
					al.add(new Affix(filename, realPath,contentId,contentDisposition));
				}
			} else {
				// out.println("<br>filename is null");
			}
		}
		return al;
	}

	/**
	 * 调整邮件附件的文件名称
	 * 
	 * @param fileName
	 * @return
	 */
	private static String adjustFileName(String fileName) {
		String str = fileName;
		str = System14.replace(str, "	", "");// 晕啊，替换的是什么字符都不知道,解析后的文件名称中包含这个字符，生产文件出错
		str = System14.replace(str, " ", "");
		str = System14.replace(str, "\r", "");
		str = System14.replace(str, "\n", "");
		return str;
	}

	/**
	 * mount无法解析时整天以邮件格式存取作为附件
	 * 
	 * @param p
	 * @param savePath
	 * @param fileName
	 * @return 文件存放路径
	 * @throws java.lang.Exception
	 */
	private static String saveFileAsEmail(Part p, String savePath, String fileName) throws Exception {
		String newFileName = savePath;
		newFileName += UniqueCode.generate();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			newFileName += fileName.substring(pos);
		}
		if (newFileName.length() < 4 || !".eml".equals(newFileName.toLowerCase().substring(newFileName.length() - 4))) {
			newFileName += ".eml";
		}
		File f = new File(newFileName);
		OutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		p.writeTo(bos);
		bos.flush();
		bos.close();
		return newFileName;
	}

	private static String saveFile(Part p, String savePath, String fileName) throws Exception {
		/*
		 * //测试代码 System.out.println("=================saveflie
		 * begin======================");
		 * System.out.println("p.getContentType="+p.getContentType());
		 * System.out.println("p.getFileName="+p.getFileName());
		 * System.out.println("p.getDisposition="+p.getDisposition());
		 * System.out.println("p.getDescription="+p.getDescription());
		 * System.out.println("p.getLineCount="+p.getLineCount());
		 * System.out.println("p.getSize="+p.getSize());
		 * System.out.println("p.ClassName="+p.getClass().getName());
		 * 
		 * byte b[] = new byte[1024]; File f = new
		 * File("E:/mywork/客户资料/错误邮件/问题邮件/source.jgp"); OutputStream bos = new
		 * BufferedOutputStream(new FileOutputStream(f)); p.writeTo(bos); //*
		 * InputStream is=null; is = ( (MimeBodyPart) p).getRawInputStream();
		 * int count=0; while((count=is.read(b))>0) { bos.write(b,0,count); }///
		 * bos.flush(); bos.close();
		 * System.out.println("=================saveflie
		 * end======================");
		 */
		return saveFile(p.getInputStream(), savePath, fileName);
	}

	/**
	 * 把从邮件得到的输入流存储到制订路径，文件扩展名称和附件名称扩展名称相同，文件名称自动生成
	 * 
	 * @param is
	 * @param savePath
	 * @param fileName
	 * @return
	 * @throws java.lang.Exception
	 */
	private static String saveFile(InputStream is, String savePath, String fileName) throws Exception {
		String newFileName = savePath;
		newFileName += UniqueCode.generate();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			newFileName += fileName.substring(pos);
		}
		File f = null;
		OutputStream bos = null;
		InputStream bis = null;
		try {
			f = new File(newFileName);
			bos = new BufferedOutputStream(new FileOutputStream(f));
			bis = new BufferedInputStream(is);
			byte b[] = new byte[64]; // 请保持64字节，测试用1024正常（最后1024错误读取异常，读取太多异常后解析出来文件错误），define
			// 1M buffer for save attachment.
			int len;
			while ((len = bis.read(b, 0, b.length)) != -1) {
				bos.write(b, 0, len);
				bos.flush();
			}
		} catch (Exception e) {
			// ****修改号="20060526zhangh_001" 区域="1" *****************开始//
			/**
			 * //zhangh 20060526在解析时发现由于最后缺少一两个字符发生异常，但解析出来的附件仍然正确；附件解析错误不作抛出异常
			 * StringBuffer str=new StringBuffer();
			 * str.append("附件").append(fileName).append("解析错误");
			 * TDebugOutMsg.outErr(str.toString()+System14.FormatForJs(e.getMessage()));
			 * throw new Exception(str.toString());
			 */
			// ****修改号="20060526zhangh_001" 区域="1" *****************结束//
		} finally {
			f = null;
			if (bos != null) {
				bos.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
		return newFileName;
	}

	/**
	 * 把邮件内容的javascript代码去掉
	 * 
	 * @param mail_file
	 * @return
	 */
	private static String screen(String mail_file) {

		String url_addr;
		String http_addr = "HTTP://";
		String href = "<A HREF=";
		String hrefTail = "</A>";
		String img = "<IMG ";
		String tail = ">";
		String blank = " ";
		String enter = "\r\n";
		String rightBracket = ")";
		String index;
		String jsHead = "<SCRIPT";
		String jsTail = "</SCRIPT>";
		StringBuffer temp = new StringBuffer();
		StringBuffer temp1 = new StringBuffer();
		boolean hrefTailFlag;
		int k, j, h, l, e, hrefIndex, hrefIndex1, hrefIndex2, rightBracketIndex, imgIndex, hrefJudge;
		// screen the javascript
		h = 0;
		try {
			h = mail_file.toUpperCase().indexOf(jsHead);
		} catch (Exception e0) {
		}
		while (h > 0) {
			String mailAppend = "";
			try {
				mailAppend = mail_file.substring(0, h);
				temp1.append(mailAppend);
				mail_file = mail_file.substring(h);
				l = mail_file.toUpperCase().indexOf(jsTail);
				mail_file = mail_file.substring(l + jsTail.length());
				h = mail_file.toUpperCase().indexOf(jsHead);
			} catch (Exception e2) {
				logger.error("",e2);
			}
		}
		mail_file = temp1.toString() + mail_file;
		return mail_file;
	}

	private static void dispText(Part p, FileWriter out, boolean htmlFlag) throws Exception {
		String mail_file = "";
		try {
			String contentType = p.getContentType();
			if ((p.isMimeType("text/plain") || p.isMimeType("text/html")) && contentType.indexOf("name") < 0) {
				try {
					if (p.getContent() == null || p.getContent().toString() == null) {
						mail_file = "无正文";
					} else {
						if (contentType.toUpperCase().indexOf("GB2312") > 0 || contentType.toUpperCase().indexOf("BIG5") > 0 || contentType.toUpperCase().indexOf("GBK") > 0||contentType.toUpperCase().indexOf("GB18030")>0) {
							mail_file = new String(p.getContent().toString());
						} else if (contentType.toUpperCase().indexOf("UTF-8") > 0) {
							mail_file = new String(p.getContent().toString());
						} else {
							mail_file = str.UnicodetoChinese(p.getContent().toString());
						}
						if (p.isMimeType("text/html")) {
							mail_file = screen(mail_file);
						}
						// 修正以混合模式发BIG5编码的邮件，第一行出现【？】的问题
						if(mail_file.getBytes("GBK").length>=1 && mail_file.getBytes("GBK")[0] == 63){
							mail_file = mail_file.substring(1);
						}
						out.write(mail_file);
					}
				} catch (Exception e) {
//					e.printStackTrace();
					logger.error("", e);
				}
			} else if (p.isMimeType("multipart/*")) {
				if (p.isMimeType("multipart/alternative")) { // /////////////
																// 2003-09-03-whj
					Multipart mp = (Multipart) p.getContent();
					int count = mp.getCount();
					for (int i = 1; i < count; i++) {
						dispText(mp.getBodyPart(i), out, htmlFlag);
					}
					if(count == 1){
						dispText(mp.getBodyPart(0), out, htmlFlag);
					};
				} else {
					Multipart mp = (Multipart) p.getContent();
					int count = mp.getCount();
					for (int i = 0; i < count; i++) { // liuxl at 2001-12-18
						dispText(mp.getBodyPart(i), out, htmlFlag);
					}
				}
			} else if (p.isMimeType("message/rfc822") && p.getContentType().indexOf("name") < 0) { // ////
																									// 2003-09-03-whj
				dispText((Part) p.getContent(), out, htmlFlag);
			} else {
			}
		} catch (Throwable e) {
			logger.error("",e);
		}
	}

	/**
	 * bug fixed for MimebodyPart include "text/html" . if MimeBodyPart include
	 * "text/html",content will show "text/html"'s "text/plain" format and
	 * "text/html" format . usage:check MimeBodyPart,if MimeBodyPart include
	 * "text/html",variable htmlFlag set to true; create by liuxl at 2001-12-27
	 * 
	 * @param mm
	 * @param out
	 * @return
	 * @throws java.lang.Exception
	 */
	public static boolean htmlFlag(Part mm) throws Exception {
		boolean htmlFlag = false;
		try {
			// zmc 2002-12-23 for string index
			int multipartPos = -1;
			try {
				multipartPos = mm.getContentType().toUpperCase().indexOf("MULTIPART");
			} catch (Exception e) {
				
			}
			if (multipartPos != -1) {
				Multipart mp=null;
				if(mm instanceof Multipart)
				{
					 mp = (Multipart) mm;
				}
				if(mp!=null)
				{
					int count = mp.getCount();
					for (int i = 0; i < count; i++) {
						int namePos = -1;
						try {
							namePos = mp.getBodyPart(i).getContentType().indexOf("name");
						} catch (Exception e) {
						}
						if (mp.getBodyPart(i).isMimeType("text/html") && namePos < 0) {
							htmlFlag = true;
							return htmlFlag;
						} else if (mp.getBodyPart(i).isMimeType("Multipart/*"))
							try {
								htmlFlag = htmlFlag(mp.getBodyPart(i));
							} catch (Exception e) {
							}
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return htmlFlag;
	}

	/**
	 * 解析邮件地址
	 * 
	 * @param ia
	 * @return
	 */
	private static String getPerNameMail(InternetAddress[] ia) {
		String from = "";
		String email = "";
		if (ia != null) {
			for (int j = 0; j < ia.length; j++) {
				String personal = (ia[j] != null) ? ia[j].toString() : "";
				email = (ia[j] != null) ? ia[j].getAddress() : "";
				personal = getDecode(personal);
				try {
					if (!personal.equals("")) {
						for (int i = 0; i < personal.length(); i++) 
						{
							if (personal.charAt(i) == '\"')
							{
								personal = personal.substring(0, i)+personal.substring(i+1);
							}
							if (personal.charAt(i) == ' ')
							{
								personal = personal.substring(0, i)+personal.substring(i+1);
							}
						}
					}
					if (personal.equals(email)) {
						personal = "";
						from += email;
					} else
						from += personal;
					if (j < ia.length - 1)
						from += ",";
				} catch (Exception e) {
					logger.error("邮箱地址解析错误" + e);
				}
			} // end of for (int j = 0; j< ia.length; j++) {
		} // end of if(ia!=null){
		return from;
	}

	/**
	 * 解析邮件中的编码信息（地址中的人员姓名、标题、附件名称） encoded-word = "=?" charset "?" encoding "?"
	 * encoded-text "?="
	 * 
	 * @param _name：未解码的人名信息字符串
	 * @return
	 */
	private static String getDecode(String _name) {
		String rightName = _name;
		String temp = "";
		String codeType = "";
		try {
			if (_name != null) {
				temp = _name.toUpperCase();
				codeType = MailTools.getMailEncode(temp);
				// 没有编码是默认iso-8859-1 转换成gbk
				if (codeType.equals("NOENCODE")) {
					rightName = MailTools.iso2gb(_name);
				} 
				else {// From: "=??B?x+/I1cu90+8=?=" <smilly_01@sina.com>
					// 对这个"错误编码的"特殊解决
					if (codeType.equals("DEFALT")) {
						_name = System14.replace(_name, "=??", "=?GBK?");
					}
					
					rightName = MimeUtility.decodeText(_name);
				}
			}
		} catch (java.io.UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException" + e,e);
		}
		/*catch (javax.mail.internet.ParseException e) {
			logger.error("ParseException" + e);
		}*/ catch (Exception e) {
			logger.error("Exception" + e,e);
		}
		// net.btdz.oa.common.TDebugOutMsg.outMsg("解码("+_name+")======("+rightName+")");
		int iPos = -1;
		iPos = rightName.indexOf("=?");
		temp = rightName;
		if (!codeType.equalsIgnoreCase("BIG5")&&!codeType.equalsIgnoreCase("GBK")&&!codeType.equalsIgnoreCase("UTF-8")&&!codeType.equalsIgnoreCase("GB2312")&&!codeType.equalsIgnoreCase("GB18030")) {
			if (iPos > 0) {
				rightName = rightName.substring(0, iPos);
			}
			rightName = toGbk(rightName, codeType);
		}
		if (iPos > 0) {
			rightName += getDecode(temp.substring(iPos));
		}
		return rightName;
	}

	private static String gb2iso(String qs) throws IOException {
		if (qs == null)
			return "";
		else {
			// Used for TurboLinux
			return str.ChinesetoUnicode(qs);
			// return qs;
		}
	}

	public static MimeMessage loadMailFromEmlFile(String emlFilePath) throws Exception {
		MimeMessage mm = null;
		File f = new File(emlFilePath);
		if (f == null || !f.isFile() || !f.exists()) {
			return mm;
		}
		mm = new MimeMessage(null, new FileInputStream(f));
		return mm;
	}

	public static boolean saveMimeMessageToFile(MimeMessage mm, String emlFilePath) throws Exception {
		InputStream is = mm.getRawInputStream();
		File f = new File(emlFilePath);
		if (f.exists()) {
			f.delete();
		}
		OutputStream os = new FileOutputStream(f);
		/*
		 * int num; byte [] data=new byte[1024]; while((num=is.read(data))!=-1) {
		 * os.write(data,0,num); } os.flush(); os.close();
		 */
		mm.writeTo(os);
		is.close();
		return true;
	}

	private static void test() {
		int i;
		String _mboxPath = "K:/eml/1.eml";
		ConnMailhost conn = new ConnMailhost();
		try {
			Store store = conn.getLocalStore(_mboxPath);
			if (!store.isConnected())
				store.connect();
			Folder mailbox_INBOX = store.getFolder("INBOX");
			if (!mailbox_INBOX.isOpen())
				mailbox_INBOX.open(Folder.READ_WRITE);
			Message[] msgsINBOX = mailbox_INBOX.getMessages();
			System.err.println(msgsINBOX.length);
			MimeMessage mm = (MimeMessage) msgsINBOX[0];
			// 测试MimeMessage对象直接写文件
			mm = loadMailFromEmlFile("C:\\upload\\~maildata\\zhangh\\cur\\asd.txt");
			MailInfo mi = changFormat("zhangh", mm, "C:/upload/~attachment/zhangh/atttemp/");
			logger.info(mi.getSubject());
			logger.info(mi.getContentText());
			AffixList al = mi.getAffixList();
			int len = al.size();
			for (i = 0; i < len; i++) {
				logger.info("fn" + i + ":" + al.get(i).getDispName() + "|||" + al.get(i).getDispExtName());
			}
			/*
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("MessageID:"+mm.getMessageID());
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("From:"+mm.getHeader("From",null));
			 * String from[]=mm.getHeader("From"); for(i=0;i<from.length;i++) {
			 * net.btdz.oa.common.TDebugOutMsg.outMsg(from[i]); }
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("to:"+mm.getHeader("to",null));
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * String to[]=mm.getHeader("to"); for(i=0;i<to.length;i++) {
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("to"+i+":"+to[i]); }
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg(Message.RecipientType.TO);
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("主题："+mm.getSubject());
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("主题header："+mm.getHeader("subject",null));
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("("+MailTools.getSubject(mm)+")");
			 */

		} catch (Exception e) {
			logger.error("err:" + e.getMessage());
		}
	}
	private static void test2() {
		int i;
		String _mboxPath = "c:/upload/~maildata/zhangh/";
//		ConnMailhost conn = new ConnMailhost();
		try {
			/*
			Store store = conn.getLocalStore(_mboxPath);
			if (!store.isConnected())
				store.connect();
			Folder mailbox_INBOX = store.getFolder("INBOX");
			if (!mailbox_INBOX.isOpen())
				mailbox_INBOX.open(Folder.READ_WRITE);
			Message[] msgsINBOX = mailbox_INBOX.getMessages();
			System.err.println(msgsINBOX.length);
			MimeMessage mm = (MimeMessage) msgsINBOX[0];
			*/
			// 娴嬭瘯MimeMessage瀵硅薄鐩存帴鍐欐枃浠?
			MimeMessage mm = loadMailFromEmlFile("K:/eml/1.eml");
			MailInfo mi = changFormat("zhangh", mm, "C:/upload/~attachment/zhangh/atttemp/");
			logger.info(mi.getSubject());
			
			logger.info(mi.getFrom());
			logger.info("\r\n==============================\r\n");
			
			logger.info(mi.getContentText2());
			
			logger.info("\r\n==============================\r\n");
			AffixList al = mi.getAffixList();
			int len = al.size();
			for (i = 0; i < len; i++) {
				logger.info("fn" + i + ":" + al.get(i).getDispName() + "|||" + al.get(i).getDispExtName()+"  ||| contentID="+al.get(i).getContentId());
				
				logger.info("\r\n path="+al.get(i).getRealPath()+"\r\n discript:"+al.get(i).getContentDisposition());
			}
			/*
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("MessageID:"+mm.getMessageID());
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("From:"+mm.getHeader("From",null));
			 * String from[]=mm.getHeader("From"); for(i=0;i<from.length;i++) {
			 * net.btdz.oa.common.TDebugOutMsg.outMsg(from[i]); }
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("to:"+mm.getHeader("to",null));
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * String to[]=mm.getHeader("to"); for(i=0;i<to.length;i++) {
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("to"+i+":"+to[i]); }
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg(Message.RecipientType.TO);
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("涓婚锛?+mm.getSubject());
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("===============================");
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("涓婚header锛?+mm.getHeader("subject",null));
			 * net.btdz.oa.common.TDebugOutMsg.outMsg("("+MailTools.getSubject(mm)+")");
			 */

		} catch (Exception e) {
			logger.error("err:" + e.getMessage());
		}
	}
	private static void test1() {
		int i;
		String _mboxPath = "c:/upload/~maildata/zhangh";
		ConnMailhost conn = new ConnMailhost();
		try {
			Store store = conn.getLocalStore(_mboxPath);
			if (!store.isConnected())
				store.connect();
			Folder mailbox_INBOX = store.getFolder("INBOX");
			if (!mailbox_INBOX.isOpen())
				mailbox_INBOX.open(Folder.READ_WRITE);
			Message[] msgsINBOX = mailbox_INBOX.getMessages();
			logger.info("Load OKKKKKK:" + msgsINBOX[0].getSubject());
			String tempPath = LocalMailCfg.getUserTempPath("zhangh");
			MailInfo mi = MailTools.changFormat("123", msgsINBOX[0], tempPath);
			logger.info("From:" + mi.getFrom());
			logger.info("subject:" + mi.getSubject());
			logger.info("======================Conten================");
			logger.info(mi.getContentText());

		} catch (Exception e) {
			logger.error("err:" + e.getMessage());
		}
	}

	protected static boolean compareDate(String wh, Date da, Date db) {
		if (wh.equals(">")) {
			return da.after(db);
		} else if (wh.equals("<")) {
			return da.before(db);
		} else if (wh.equals("=")) {
			return (da.getYear() == db.getYear() && da.getMonth() == db.getMonth() && da.getDate() == db.getDate());
		}
		return false;
	}

	private static String getMailEncode(String src) {
		String enType = "NOENCODE";
		int ib, ie;
		ib = src.indexOf("=?");
		if (ib > -1) {
			ie = src.indexOf("?", ib + 2);
			if (ie > -1) {
				enType = src.substring(ib + 2, ie);
			}
			if (ib + 2 == ie) {
				enType = "DEFALT";
			}
		}
		return enType;
	}

	protected static String toGbk(String src, String codeType) {
		try {
			if (src == null) {
				return null;
			}
			return new String(src.getBytes(codeType), "GBK");
		} catch (Exception e) {
			return src;
		}
	}

	private static String decodeSelf(java.io.InputStream is, String encoding) {
		// MimePart 这个接口判断编码,然后判断子类,调用mimeBodyPart,
		// MimeMessage相应的getRawInputStream得到原始的数据流
		StringBuffer dStr = new StringBuffer();
		try {
			int readNum = 0;
			byte[] data = new byte[1024];
			java.io.InputStream di = null;
			try {
				di = MimeUtility.decode(is, encoding);
			} catch (Exception e) {
			}
			if (di == null) {
				di = is;
			}
			while ((readNum = di.read(data)) != -1) {
				dStr.append(new String(data, 0, readNum));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return dStr.toString();
	}

	public static void main(String[] args) {
//		MailTools mailTools1 = new MailTools();
//		try {
//			test2();
//		} catch (Exception e) {
//			logger.error("err:" + e.getMessage());
//		}
//		logger.info("RUN OVER");
//		String encode="";
//		encode="=?gb2312?B?ycG158n9vLZWSVCjrM7StcTD+9fWztLX99b3o6E=?=";
//		System.out.println(encode);
//		System.out.println(MailTools.getDecode(encode));
//		String encode="=?gb2312?B?y9G6/FZJUNPK?=";
//		System.out.println(MailTools.getDecode(encode));
		test2();
	}
	
	
	private static MimeBodyPart createContent(String data,String newdata) throws MessagingException {
		// TODO Auto-generated method stub
		MimeBodyPart contentPart=new MimeBodyPart();
		MimeMultipart contentmultipart=new MimeMultipart("related");
		MimeBodyPart htmlBodyPart=new MimeBodyPart();
		htmlBodyPart.setContent(data, "text/html;charset=" + str.getChineseCharset());
		contentmultipart.addBodyPart(htmlBodyPart);
		Matcher m = Pattern.compile("<\\s*img\\s+([^>]*)\\s*>", Pattern.CASE_INSENSITIVE).matcher(newdata);
		while(m.find())
		{
			Matcher srcMatcher = Pattern.compile("src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(m.group());
			if(srcMatcher.find())
			{
			 	try {
			 		 if(srcMatcher.group().indexOf("http:")==-1)
                    {
	
				 		String t = srcMatcher.group().substring(
				 				srcMatcher.group().indexOf("fileId="));
				 		t = t.substring(0, t.indexOf("&amp;"));
				 		String[] arr=t.split("=");
				 		
						MimeBodyPart gifBodyPart=new MimeBodyPart();
						FileDataSource fds=new FileDataSource(getFileAbstractPath(arr[1]));
						gifBodyPart.setDataHandler(new DataHandler(fds));
						gifBodyPart.setContentID(arr[1]);
						contentmultipart.addBodyPart(gifBodyPart);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
		contentPart.setContent(contentmultipart);	
		return contentPart;
	}
	
	private static MimeBodyPart createAttachment(Affix affix) throws MessagingException {
		MimeBodyPart mbp = null;
		try{
			mbp = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(CoderFactory.getInstance().decryptFileToTemp(affix.getRealPath()));
			mbp.setDataHandler(new DataHandler(fds));
			mbp.setFileName("=?UTF-8?B?" + enc.encode(affix.getFileName().getBytes("UTF-8")).replaceAll("\\s", "") + "?=");
		}catch(Exception e){
			logger.error("", e);
		}
		return mbp;
	}

}

