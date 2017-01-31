package com.seeyon.v3x.mail.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.util.MailTools;

import java.util.Arrays;

public class MessageMailManagerImpl implements MessageMailManager {
	
	private static Log log = LogFactory.getLog(MessageMailManagerImpl.class);

	// 系统邮箱设置 配置项
	private static final String SystemMailbox_ConfigCatrgory = "System_Mailbox_Setting";
	private static final String SystemMailbox_ConfigItem = "SystemMailAddress";
	private static final String Default_Suffer = "[A8]";
	private static boolean hasSystemMailbox = false;

	private InternetAddress systemMailAddress = null;
	private String MailAddress = null;
	private String SMTPHost = null;
	private String password = null;
	private String suffer = null;
	private String userName = null;
	private int pop3Port=110;
	private int smtpPort=25;

	// 邮件正文是否允许带链接以直接打开详细信息进行处理
	private boolean contentWithLink = true;
	// 邮件连接的有效期限 单位小时 默认24
	private int contentLinkValidity = 24;

	private Session session = null;
	private ConfigManager configManager;
	
	private final ExecutorService exec                         = Executors.newCachedThreadPool();
    //邮件发送超时次数
    private final int             timeOutAmount                = 3;
    private int                   timeOutIndex                 = 0;
    //等待邮件发送超时时间，单位为秒
    private final int             timeOut                      = 30*60;

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	/**
	 * 初始化方法
	 */
	public void init() {
		ConfigItem systemMailboxConfigItem = configManager.getConfigItem(SystemMailbox_ConfigCatrgory, SystemMailbox_ConfigItem);
		
		if (systemMailboxConfigItem != null) {
			String extConfigValue = systemMailboxConfigItem.getExtConfigValue();
			if (extConfigValue.startsWith("<")) {
				SystemEmailConfig sysMailConfig = (SystemEmailConfig) XMLCoder.decoder(extConfigValue);
				MailAddress = sysMailConfig.getEmailAddress();
				SMTPHost = sysMailConfig.getSmtpHost();
				password = sysMailConfig.getEmailPwd();
				contentWithLink = sysMailConfig.isAppendLink();
				contentLinkValidity = sysMailConfig.getAvailableTime();
				suffer = sysMailConfig.getSuffer();
				userName = sysMailConfig.getUserName();
				pop3Port = sysMailConfig.getPop3Port();
				smtpPort = sysMailConfig.getSmtpPort();
			} else {
				// 处理老用户的设置
				MailAddress = systemMailboxConfigItem.getConfigValue();
				String[] extItems = extConfigValue.split(",");
				SMTPHost = extItems[0];
				password = extItems[2];
				pop3Port = pop3Port==0?110:pop3Port;
				smtpPort = smtpPort==0?25:smtpPort;
				SystemEmailConfig emailConfig = new SystemEmailConfig(MailAddress, SMTPHost, password, contentWithLink, contentLinkValidity, Default_Suffer, MailAddress,pop3Port,smtpPort);
				String newExtConfigValue = XMLCoder.encoder(emailConfig);
				systemMailboxConfigItem.setExtConfigValue(newExtConfigValue);
				configManager.updateConfigItem(systemMailboxConfigItem);
				suffer = emailConfig.getSuffer();
				userName = emailConfig.getUserName();
			}
			
			// 对老数据的处理 suffer == null 表明不存在这个项 ，则更新数据库
			if (suffer == null) {
				SystemEmailConfig emailConfig = new SystemEmailConfig(MailAddress, SMTPHost, password, contentWithLink, contentLinkValidity, Default_Suffer, userName,pop3Port,smtpPort);
				String newExtConfigValue = XMLCoder.encoder(emailConfig);
				systemMailboxConfigItem.setExtConfigValue(newExtConfigValue);
				configManager.updateConfigItem(systemMailboxConfigItem);
				suffer = emailConfig.getSuffer();
				userName = emailConfig.getUserName();
			}

			if (userName == null) {
				SystemEmailConfig emailConfig = new SystemEmailConfig(MailAddress, SMTPHost, password, contentWithLink, contentLinkValidity, Default_Suffer, MailAddress,pop3Port,smtpPort);
				String newExtConfigValue = XMLCoder.encoder(emailConfig);
				systemMailboxConfigItem.setExtConfigValue(newExtConfigValue);
				configManager.updateConfigItem(systemMailboxConfigItem);
				userName = emailConfig.getUserName();
			}

//			session = Session.getInstance(this.getProps(SMTPHost), (Authenticator) new PasswordAuthenticator(userName, password));
			getSysMailSeeion();
			try {
				systemMailAddress = new InternetAddress(MailAddress);
				hasSystemMailbox = true;
			} catch (Exception e) {
				log.error(e.getMessage() + " 系统邮箱Email地址不合法，不能使用邮件发送系统消息!");
				hasSystemMailbox = false;
			}
		} else {
			hasSystemMailbox = false;
			contentWithLink = false;
		}
	}

	/**
	 * 判断管理员是否设置了系统邮箱
	 */
	public boolean hasSystemMailbox() {
		return hasSystemMailbox;
	}

	/**
	 * 通过邮件发送系统消息
	 */
	public boolean sendMessageByMail(String to, String subject, String content, Date sendDate) {
		List<String> toMailList = new ArrayList<String>(1);
		toMailList.add(to);

		return this.sendMessageByMail(toMailList, subject, content, sendDate);
	}

	public synchronized boolean sendMessageByMail(List<String> toMailList, String subject, String content, Date sendDate) {
		if (hasSystemMailbox()) {
			InternetAddress[] toMailArray = new InternetAddress[toMailList.size()];
			for (int i = 0; i < toMailList.size(); i++) {
				try {
					toMailArray[i] = new InternetAddress(toMailList.get(i));
				} catch (AddressException e) {
					log.error("邮件地址不合法:" + toMailList.get(i), e);
					continue;
				}
			}

			if (toMailArray.length == 0) {
				return false;
			}

			if (session == null) {
				getSysMailSeeion();
//				session = Session.getInstance(this.getProps(SMTPHost), new PasswordAuthenticator(userName, password));
			}

			// 创建新邮件：
			/*
			 Message msg = new MimeMessage(session); 
			try {
				msg.setHeader("Content-Type", "text/html; charset=UTF-8");
				msg.setFrom(systemMailAddress);
				msg.setRecipients(Message.RecipientType.TO, toMailArray);
				if (suffer != null) {
					msg.setSubject(this.suffer + subject);
				} else {
					msg.setSubject(subject);
				}
				msg.setContent(content, "text/html; charset=UTF-8");
				msg.setSentDate(sendDate);
				Transport.send(msg);
				return true;
			} */

			// 创建新邮件：修改将邮件信息转换成邮件格式    bug邮件图片不显示      jhl修改 2014-2-14
			try {
            //			    Message msg = null;
            //				MailInfo mi = new MailInfo();
            //				mi.getSendDate();
            //				mi.setContentText(content);
            //				if (suffer != null) {
            //					mi.setSubject(this.suffer+ subject);
            //				} else {
            //					mi.setSubject(subject);
            //				}
            //				msg = MailTools.changFormat(session,mi);
            //				msg.setFrom(systemMailAddress);
            //				msg.setRecipients(Message.RecipientType.TO, toMailArray);
            //				Transport.send(msg);
            //				return true;
			    //增加邮件超时重发功能，解决 BUG_紧急_A8_3.50sp1_易居中国_9月24日所有的电子邮件消息提醒又收不到了
			   return execMailTask(exec, timeOut,subject,content,toMailArray); 
			}catch (Exception e1) {
				log.error("验证失败，您的系统邮箱设置有误或者邮箱不提供SMTP服务。", e1);
				return false;
			}
		} else {
			return false;
		}
	}
	
 public boolean execMailTask(ExecutorService exec, int timeout, String subject, String content, InternetAddress[] toMailArray) {
        MailTask task = new MailTask(subject, content, toMailArray);
        Future<Boolean> future = exec.submit(task);
        Boolean taskResult = Boolean.FALSE;
        try {
            //等待计算结果，最长等待timeout秒，timeout秒后中止任务          
            taskResult = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("主线程在邮件发送时被中断！", e);
        } catch (ExecutionException e) {
            log.error("主线程等待邮件发送，但邮件发送抛出异常！", e);
        } catch (TimeoutException e) {
            timeOutIndex++;
            log.error("邮件第“"+timeOutIndex+"”次发送超时,因此中断任务线程！subject=" + subject + "  发送地址:" + Arrays.asList(toMailArray), e);
            //重新获取一次session，再次发送
            getSysMailSeeion();
            if (timeOutIndex < timeOutAmount) {
                execMailTask(exec, timeout, subject, content, toMailArray);
            }else{
                timeOutIndex = 0;
                taskResult = false;
            }
        }
        return taskResult;
    }
	    
    class MailTask implements Callable<Boolean> {
        private String            subject;
        private String            content;
        private InternetAddress[] toMailArray;

        public MailTask(String subject, String content, InternetAddress[] toMailArray) {
            this.subject = subject;
            this.content = content;
            this.toMailArray = toMailArray;
        }

        @Override
        public Boolean call() throws Exception {
            Message msg = null;
            MailInfo mi = new MailInfo();
            mi.getSendDate();
            mi.setContentText(content);
            if (suffer != null) {
                mi.setSubject(suffer + subject);
            } else {
                mi.setSubject(subject);
            }
            msg = MailTools.changFormat(session, mi);
            msg.setFrom(systemMailAddress);
            msg.setRecipients(Message.RecipientType.TO, toMailArray);
            Transport.send(msg);
            return Boolean.TRUE;
        }
    }


	public boolean sendMessageByMail(String toEmail, String subject, String content) {
		return this.sendMessageByMail(toEmail, subject, content, new Date());
	}

	class PasswordAuthenticator extends Authenticator {
		private String username;
		private String password;

		public PasswordAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}

	public void saveSystemMailboxSetting(String emailAddress, String smtpHost, String emailPwd, boolean isAppendLink, int availableTime, String suffer, String userName,int pop3Port,int smtpPort) {
		// 更新DB
		SystemEmailConfig emailConfig = new SystemEmailConfig(emailAddress, smtpHost, emailPwd, isAppendLink, availableTime, suffer, userName,pop3Port,smtpPort);
		String extConfigValue = XMLCoder.encoder(emailConfig);
		ConfigItem systemMailboxConfigItem = configManager.getConfigItem(SystemMailbox_ConfigCatrgory, SystemMailbox_ConfigItem);
		if (systemMailboxConfigItem != null) {
			systemMailboxConfigItem.setExtConfigValue(extConfigValue);
			configManager.updateConfigItem(systemMailboxConfigItem);
		} else {
			systemMailboxConfigItem = new ConfigItem();
			systemMailboxConfigItem.setIdIfNew();
			systemMailboxConfigItem.setConfigCategory(SystemMailbox_ConfigCatrgory);
			systemMailboxConfigItem.setConfigItem(SystemMailbox_ConfigItem);
			systemMailboxConfigItem.setExtConfigValue(extConfigValue);
			configManager.addConfigItem(systemMailboxConfigItem);
		}

		// 更新Session
		this.MailAddress = emailAddress;
		this.SMTPHost = smtpHost;
		this.password = emailPwd;
		this.contentWithLink = isAppendLink;
		this.contentLinkValidity = availableTime;
		this.suffer = suffer;
		this.userName = userName;
		this.pop3Port = pop3Port;
		this.smtpPort = smtpPort;

		try {
			systemMailAddress = new InternetAddress(MailAddress);
			hasSystemMailbox = true;
		} catch (Exception e) {
			log.error(e.getMessage() + " 系统邮箱Email地址不合法，不能使用邮件发送系统消息!", e);
			hasSystemMailbox = false;
		}
//		session = Session.getInstance(this.getProps(SMTPHost,smtpPort+""), new PasswordAuthenticator(userName, emailPwd));
		getSysMailSeeion();
	}

	/**
	 * 系统管理员测试邮件消息发送
	 */
	public boolean testEmailSend(String smtpHostName, String sysEmailAddress, String emailPassword, String recEmailAddress, String userName,String smtpPort) {
		Session testSession = null;
		//testSession = Session.getInstance(this.getProps(smtpHostName,smtpPort), new PasswordAuthenticator(userName, emailPassword));

		int smtpPortNum=Integer.valueOf(smtpPort);
		try {
			switch (smtpPortNum)
			{
			case 25:
				testSession = ConnMailhost.getSmtpSession(smtpHostName, userName, emailPassword);
				break;
			case 587:
				testSession=ConnMailhost.getSmtpSessionTLS(smtpHostName, userName, smtpPort, emailPassword);
				break;
			default:
				testSession = ConnMailhost.getSmtpSession(smtpHostName, userName, emailPassword, smtpPortNum, false);
				break;
			}
			Message testMsg = new MimeMessage(testSession);
			String testSubject = "系统管理员测试邮件消息发送";
			testMsg.setHeader("Content-Type", "text/html; charset=UTF-8");
			testMsg.setFrom(new InternetAddress(sysEmailAddress));
			testMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recEmailAddress, false));
			if (suffer != null) {
				testMsg.setSubject(this.suffer + testSubject);
			} else {
				testMsg.setSubject(testSubject);
			}
			testMsg.setContent(testSubject, "text/html; charset=UTF-8");
			testMsg.setSentDate(new Date());
			Transport.send(testMsg);
			return true;
		} catch (Exception e) {
			log.error("系统管理员测试邮件失败：", e);
			return false;
		}
	}
	private Properties getProps(String smtpHostName) {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHostName);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.connectiontimeout", "30000");
		props.put("mail.smtp.timeout", "30000");
		return props;
	}
	private Properties getProps(String smtpHostName,String smtpPort) {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHostName);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.connectiontimeout", "30000");
		props.put("mail.smtp.timeout", "30000");
		return props;
	}

	public String getMailAddress() {
		return this.MailAddress;
	}

	public String getPassword() {
		return this.password;
	}

	public String getSMTP() {
		return this.SMTPHost;
	}

	public int getContentLinkValidity() {
		return contentLinkValidity;
	}

	public boolean isContentWithLink() {
		return contentWithLink;
	}

	public SystemEmailConfig getSysEMailConfig() {
		return new SystemEmailConfig(MailAddress, SMTPHost, password, contentWithLink, contentLinkValidity, suffer, userName,pop3Port,smtpPort);
	}

	public void cancelSystemMailboxSetting() {
		ConfigItem systemMailboxConfigItem = configManager.getConfigItem(SystemMailbox_ConfigCatrgory, SystemMailbox_ConfigItem);
		if (systemMailboxConfigItem != null) {
			configManager.deleteConfigItem(SystemMailbox_ConfigCatrgory, SystemMailbox_ConfigItem);
		}
		
		hasSystemMailbox = false;
		systemMailAddress = null;
		MailAddress = null;
		SMTPHost = null;
		password = null;
		session = null;
		contentWithLink = false;//取消后不带链接
		suffer = null;
		userName = null;
	}
	public void getSysMailSeeion(){
		smtpPort = smtpPort==0?25:smtpPort;
		switch (smtpPort)
		{
		case 25:
			session = ConnMailhost.getSmtpSession(SMTPHost, userName, password);
			break;
		case 587:
			session=ConnMailhost.getSmtpSessionTLS(SMTPHost, userName, smtpPort+"", password);
			break;
		default:
			session = ConnMailhost.getSmtpSession(SMTPHost, userName, password, smtpPort, false);
			break;
		}
	}
}