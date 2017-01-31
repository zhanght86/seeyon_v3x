package com.seeyon.v3x.webmail.domain;

/**
 * <p>Title: </p>
 * <p>Description: 用户邮箱配置参数</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.util.TextEncoder;
import com.seeyon.v3x.webmail.util.Affix;

public class MailBoxCfg implements Serializable
{
	private static final Log logger = LogFactory.getLog(MailBoxCfg.class);
	private static final long serialVersionUID = -1070279904971522311L;
	private String email = "";
	private String userName = "";
	private String password = "";
	private String pop3Host = "";
	private String smtpHost = "";
	private boolean backup = true;// 是否在邮件服务器保留备份
	private boolean defaultBox = false;// 是否为默认邮箱
	private int timeOut = 120;// 邮件连接超时时间
	private boolean authorCheck = true;

	private int pop3Port = 110;
	private int smtpPort = 25;
	private boolean pop3Ssl = false;
	private boolean smtpSsl = false;

	public MailBoxCfg()
	{
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserName()
	{
		return this.userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPop3Host(String pop3Host)
	{
		this.pop3Host = pop3Host;
	}

	public String getPop3Host()
	{
		return this.pop3Host;
	}

	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	public String getSmtpHost()
	{
		return this.smtpHost;
	}

	public void setBackup(boolean backup)
	{
		this.backup = backup;
	}

	public boolean getBackup()
	{
		return this.backup;
	}

	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}

	public void setAuthorCheck(boolean authorCheck)
	{
		this.authorCheck = authorCheck;
	}

	public boolean getAuthorCheck()
	{
		return this.authorCheck;
	}

	public void setDefaultBox(boolean defaultBox)
	{
		this.defaultBox = defaultBox;
	}

	public boolean getDefaultBox()
	{
		return this.defaultBox;
	}

	/*
	 * private void writeObject(java.io.ObjectOutputStream out) throws
	 * IOException { out.writeUTF(this.email); out.writeUTF(this.userName);
	 * out.writeUTF(this.password); out.writeUTF(this.pop3Host);
	 * out.writeUTF(this.smtpHost); out.writeBoolean(this.backup);
	 * out.writeBoolean(this.defaultBox); out.writeBoolean(this.authorCheck);
	 * out.writeInt(this.timeOut); } private void
	 * readObject(java.io.ObjectInputStream in) throws IOException,
	 * ClassNotFoundException { this.email = in.readUTF(); this.userName =
	 * in.readUTF(); this.password = in.readUTF(); this.pop3Host = in.readUTF();
	 * this.smtpHost = in.readUTF(); this.backup = in.readBoolean();
	 * this.defaultBox = in.readBoolean(); this.authorCheck = in.readBoolean();
	 * this.timeOut = in.readInt(); }
	 */
	public void readBaseObject(java.io.ObjectInputStream in) throws IOException
	{
		try {
			this.email = in.readUTF();
			this.userName = in.readUTF();
			this.password = TextEncoder.decode(in.readUTF());
			this.pop3Host = in.readUTF();
			this.smtpHost = in.readUTF();
			this.backup = in.readBoolean();
			this.defaultBox = in.readBoolean();
			this.authorCheck = in.readBoolean();
			this.timeOut = in.readInt();
			// 读端口
			this.pop3Port = in.readInt();
			this.smtpPort = in.readInt();
			this.pop3Ssl = in.readBoolean();
			this.smtpSsl = in.readBoolean();
		}
		catch (Exception e) {
			logger.error("", e);
		}

	}

	public void writeBaseObject(java.io.ObjectOutputStream out) throws IOException
	{
		try {
			out.writeUTF(this.email);
			out.writeUTF(this.userName);
			out.writeUTF(TextEncoder.encode(this.password));
			out.writeUTF(this.pop3Host);
			out.writeUTF(this.smtpHost);
			out.writeBoolean(this.backup);
			out.writeBoolean(this.defaultBox);
			out.writeBoolean(this.authorCheck);
			out.writeInt(this.timeOut);
			out.writeInt(pop3Port);
			out.writeInt(smtpPort);
			out.writeBoolean(pop3Ssl);
			out.writeBoolean(smtpSsl);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public String toOutString()
	{
		return "(email=" + email + ")(userName=" + userName + ")(password=" + password
				+ ")(pop3Host=" + pop3Host + ")(smtpHost=" + smtpHost + ")(backup=" + backup
				+ ")(defaultBox=" + defaultBox + ")(authorCheck=" + authorCheck + ")(timeOut="
				+ timeOut + ")";
	}

	public static void main(String[] args) throws Exception
	{
		MailBoxCfg mbc = new MailBoxCfg();
		MailBoxCfg mbc2 = new MailBoxCfg();

		mbc.setAuthorCheck(true);
		mbc.setBackup(false);
		mbc.setDefaultBox(true);
		mbc.setEmail("zdfsf@sohu.com");
		mbc.setPassword("pws");
		mbc.setPop3Host("p9p3host");
		mbc.setSmtpHost("stmtpppp");
		mbc.setTimeOut(243);
		mbc.setUserName("zhangh张华");
		mbc.setPop3Port(110);
		mbc.setSmtpPort(25);
		mbc.setPop3Ssl(true);
		mbc.setSmtpSsl(true);
		File f = new File("c:\\test.txt");
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		mbc.writeBaseObject(oos);
		oos.flush();
		oos.close();
		fos.flush();
		fos.close();

		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Affix nax = new Affix();
		mbc2.readBaseObject(ois);

		System.out.println(mbc2.getPop3Port());
	}

	public int getPop3Port()
	{
		return pop3Port;
	}

	public void setPop3Port(int pop3Port)
	{
		this.pop3Port = pop3Port;
	}

	public int getSmtpPort()
	{
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	public boolean isPop3Ssl()
	{
		return pop3Ssl;
	}

	public void setPop3Ssl(boolean pop3Ssl)
	{
		this.pop3Ssl = pop3Ssl;
	}

	public boolean isSmtpSsl()
	{
		return smtpSsl;
	}

	public void setSmtpSsl(boolean smtpSsl)
	{
		this.smtpSsl = smtpSsl;
	}

}