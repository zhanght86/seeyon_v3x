package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author shine shen
 * @version 1.0
 */

import java.util.*;
import java.security.Security;

import javax.mail.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.webmail.domain.MailBoxCfg;

public class ConnMailhost
{
	static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final Log log = LogFactory.getLog(ConnMailhost.class);
	public ConnMailhost()
	{
	}

	public Store getLocalStore(String mboxPath)
	{
		Properties props = new Properties();
		props.put("mail.store.maildir.autocreatedir", "true");
		props.put("mail.store.maildir.cachefolders", "true");
		Session session = Session.getInstance(props);
		session.setDebug(false);
		String URL = "maildir:" + mboxPath;
		Store store = null;
		try
		{
			store = session.getStore(new URLName(URL));
			store.connect();

		} catch (Exception e)
		{
			log.error(URL,e);
		}
		return store;
	}

	/**
	 * 直接生成smtp发送信息
	 * 
	 * @param smtphost
	 * @param username
	 * @param password
	 * @return
	 */
	public static Session getSmtpSession(String smtphost, String username,
			String password)
	{
		Session session = null;
		try
		{
			Properties props = new Properties();
			props.put("mail.smtp.host", smtphost);
			props.put("mail.smtp.auth", "true");
            //支持smtp端口，SMTP服务器端口号，默认为25 int
			props.put("mail.smtp.port", "25");
			myAuthenticator auth = new myAuthenticator();
			auth.setUserPass(username, password);
			session = Session.getInstance(props, (Authenticator) auth);
			session.setDebug(false); // session.setDebug(true); //test
		} catch (Exception e)
		{
			log.error("",e);
		}
		return session;
	}
	
	public static Session getSmtpSessionTLS(String smtphost, String username,String port,
			String password)
	{
		Session session = null;
		try
		{
			Properties props = new Properties();
			props.put("mail.smtp.host", smtphost);
			props.put("mail.smtp.auth", "true");
            //支持smtp端口，SMTP服务器端口号，默认为25 int
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.starttls.enable","true");
			myAuthenticator auth = new myAuthenticator();
			auth.setUserPass(username, password);
			session = Session.getInstance(props, (Authenticator) auth);
			session.setDebug(false); // session.setDebug(true); //test
		} catch (Exception e)
		{
			log.error("",e);
		}
		return session;
	}
	public static Session getSmtpSession(String smtphost, final String username,
			final String password,int smtpPort,boolean smtpSsl)
	{
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", smtphost);
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", smtpPort+"");
		props.setProperty("mail.smtp.socketFactory.port", smtpPort+"");
		props.put("mail.smtp.auth", "true");
		// 创建Session对象

		// Session session = Session.getInstance(props);
		// session.setDebug(true);
		Session session = Session.getInstance(props, new Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});
		return session;
	}

	/**
	 * 根据用户ID得到默认的smtp发送配置信息
	 * 
	 * @param userid
	 * @return
	 */
	public static Session getDefaultSmtpSession(String userId) throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(userId);
		String smtphost = mbc.getSmtpHost();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		return getSmtpSession(smtphost, username, password);
	}

	/**
	 * 根据用户名和email得到smtp发送配置信息
	 * 
	 * @param userid
	 * @param email
	 * @return
	 */
	public static Session getSmtpSession(String userId, String email)
			throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserMbc(userId, email);
		Session ms=null;
		String smtphost = mbc.getSmtpHost();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		int smtpPort = mbc.getSmtpPort();
		boolean smtpSsl = mbc.isSmtpSsl();
		switch (smtpPort)
		{
		case 25:
			ms = getSmtpSession(smtphost, username, password);
			if (ms == null) {
				ms = getDefaultSmtpSession(username);
			}
			break;
		case 587:
			if(smtpSsl)
			{
				ms=getSmtpSessionTLS(smtphost, username, smtpPort+"", password);
			}
			if (ms == null) {
				ms = getDefaultSmtpSession(username);
			}
			break;
		default:
			ms = getSmtpSession(smtphost, username, password, smtpPort, smtpSsl);
			break;
		}
		return ms;
	}
	/**
	 * 得到smtp发送配置信息
	 * 
	 * @param mbc
	 * @return
	 */
	public static Session getSmtpSession(MailBoxCfg mbc) throws Exception
	{
		Session ms=null;
		String smtphost = mbc.getSmtpHost();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		int smtpPort=mbc.getSmtpPort();
		boolean smtpSsl=mbc.isSmtpSsl();
		switch (smtpPort)
		{
		case 25:
			ms = getSmtpSession(smtphost, username, password);
			if (ms == null) {
				ms = getDefaultSmtpSession(username);
			}
			break;
		case 587:
			if(smtpSsl)
			{
				ms=getSmtpSessionTLS(smtphost, username, smtpPort+"", password);
			}
			if (ms == null) {
				ms = getDefaultSmtpSession(username);
			}
			break;
		default:
			ms = getSmtpSession(smtphost, username, password, smtpPort, smtpSsl);
			break;
		}
		return ms;
	}
	public static Store getDefaultPop3Store(String userId) throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(userId);
		String pop3host = mbc.getPop3Host();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		return getPop3Store(pop3host, username, password);
	}

	public static Store getPop3Store(String userId, String email)
			throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserMbc(userId, email);
		String pop3host = mbc.getPop3Host();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		return getPop3Store(pop3host, username, password);
	}

	public static Store getPop3Store(MailBoxCfg mbc) throws Exception
	{
		String pop3host = mbc.getPop3Host();
		String username = mbc.getUserName();
		String password = mbc.getPassword();
		int pop3Port=mbc.getPop3Port();
		switch (pop3Port)
		{
		case 110:
			return getPop3Store(pop3host, username, password);
		default:
			return getPop3Store(pop3host, username, password,pop3Port,true);
		}
	}

	public static boolean isDeleteMessage(String userId, String email)
			throws Exception
	{
		MailBoxCfg mbc = MailBoxManager.findUserMbc(userId, email);
		return !mbc.getBackup();
	}

	/**
	 * 生成pop3Store
	 * 
	 * @param pop3host
	 * @param username
	 * @param password
	 * @return
	 */
	public static Store getPop3Store(String pop3host, String username,
			String password)
	{
		Session session = null;
		Store pop3Store = null;
		try
		{
			Properties props = new Properties();
			//Properties props = System.getProperties();
			// props.put("mail.smtp.host", localMailHost);
			//Session session = Session.getDefaultInstance(props, null);
			myAuthenticator auth = new myAuthenticator();
			auth.setUserPass(username, password);
			session = Session.getInstance(props, (Authenticator) auth);
			session.setDebug(false); // session.setDebug(true);
			URLName connUrl = new URLName("pop3", pop3host, -1, "INBOX",
					username, password);
			pop3Store = session.getStore(connUrl);
		} catch (Exception e)
		{
			if(pop3Store!=null)
			{
				try {
					pop3Store.close();
				}
				catch (MessagingException e1) {
				}
			}
			log.error("",e);
		}
		return pop3Store;
	}
	public static Store getPop3Store(String pop3host, String username,
			String password,int pop3Port,boolean pop3Ssl) throws Exception
	{
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		//定义连接POP3服务器的属性信息
	    String protocol = "pop3";
	        
	    Properties prop = new Properties();
	    prop.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
	    prop.setProperty("mail.pop3.socketFactory.fallback", "false");
	    prop.setProperty("mail.store.protocol",protocol);
	    prop.setProperty("mail.pop3.host",pop3host); 
	    prop.setProperty("mail.pop3.port",pop3Port+""); 
	    prop.setProperty("mail.pop3.socketFactory.port", pop3Port+"");
	    Session mailSession = Session.getDefaultInstance(prop,null);
	    mailSession.setDebug(false);
//	    URLName urln = new URLName(protocol,pop3Server,995,null,username,password);
//	    Store store = mailSession.getStore(urln);

	    Store store = mailSession.getStore(protocol);
	    store.connect(pop3host,pop3Port,username,password); //POP3服务器的登陆认证
	    return store;
	}
	public static void main(String[] args)
	{
		ConnMailhost connMailhost1 = new ConnMailhost();
	}

}

class myAuthenticator extends Authenticator
{
	private String username = null;

	private String password = null;

	public void setUserPass(String user, String pass)
	{
		username = user;
		password = pass;
	}

	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(username, password);
	}
}