package com.seeyon.v3x.webmail.manager;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.webmail.*;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.domain.SearchStruct;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.MailTools;
import com.seeyon.v3x.webmail.util.System14;
import com.seeyon.v3x.common.filemanager.manager.*;
import com.seeyon.v3x.common.filemanager.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.*;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

public interface WebMailManager
{
	public boolean sendMail(String userId, MailInfo mi) throws Exception;

	public boolean sendMail(String userId, String sendMailBoxName, MailInfo mi) throws Exception;

	public boolean sendMail(MailBoxCfg mbc, MailInfo mi)throws Exception;

	public boolean fetchMail(String userId, MailBoxCfg mbc, HttpSession session) throws Exception;

	public boolean setMailDelFlag(Message[] mail, int len);

	public boolean setMailDelFlag(java.util.List mails, int len);

	public boolean fetchMail(String userId, String email,HttpSession session) throws Exception;

	public void setSessionValue(HttpSession session, String varName, java.lang.Object value);

	public MailBoxFolder getMailBoxFolder(String userId, int boxType) throws Exception;

	public int countNewMial(String userId, HttpSession session);

	public boolean moveMail(String userId, int form, int to, String mailId) throws Exception;

	/**
	 * 邮件查询
	 * 
	 * @param userId
	 * @param ss
	 * @param session
	 * @return
	 * @throws java.lang.Exception
	 */
	public MailInfoList search(String userId, SearchStruct ss) throws Exception;

	/**
	 * 邮件打包
	 * 
	 * @param userId
	 * @param mis
	 * @return
	 */
	public String createZip(String userId, String[] mis, int mailBox)throws Exception;

	public MailInfo cologMail(MailInfo omi, String userId)throws Exception;

	public MailInfo cologReply(MailInfo omi, String userId)throws Exception;

	/**
	 * 复制邮件，转发协同用，附件存储到协同附件目录，去掉文件扩展名
	 * 
	 * @param omi
	 * @param userId
	 * @return
	 * @throws java.lang.Exception
	 */
	public MailInfo cologInfo(MailInfo omi, String userId) throws Exception;

	public String createZip(String userId, MailInfoList mil)throws Exception;

	/*
	public void test() throws Exception;

	public void testFetch() throws Exception;

	public void testSend() throws Exception;

	public void testZipFolder() throws Exception;
	*/

	public boolean sendMail(String smtpServe, String userName, String pass, MailInfo mi) throws Exception;
	
	public MailInfo getMailInfoById(long userId, long id)throws Exception;
	
	public ModelAndView forwordMail(long referenceId, String subject, String bodyContent, List<Attachment> atts);

	/**
	 * 判断用户是否设置邮箱，用于协同转发邮件前ajax调用
	 * @param userId  用户Id，如果为currentUser，则取当前用户id
	 * @return 
	 */
	public String hasDefaultMailBox(String userId);
	/**
	 * 判断邮件大小是否超出当前用户的邮件空间大小
	 * @param userId
	 * @param mi
	 * @return true 没超出   false 超出
	 */
	public boolean checkMailSpace(String userId,MailInfo mi);
}