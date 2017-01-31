package com.seeyon.v3x.webmail.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

public class TestMail {
	
	public void testConnect(String pop3host,String username,String password, String isDisTitle)
	{
		System.out.println(" in param pop3host="+pop3host+",username="+username+",password="+password);
		
		Session session = null;
		Store pop3Store = null;
		try
		{
			Properties props = System.getProperties();
			//Properties props = System.getProperties();
			// props.put("mail.smtp.host", localMailHost);
			//Session session = Session.getDefaultInstance(props, null);
			myAuthenticator auth = new myAuthenticator();
			auth.setUserPass(username, password);
			session = Session.getInstance(props, (Authenticator) auth);
			session.setDebug(false); // session.setDebug(true);
			URLName connUrl = new URLName("pop3", pop3host, -1, "INBOX",username, password);
			pop3Store = session.getStore(connUrl);
			if (!pop3Store.isConnected())
			{
				pop3Store.connect();
			}
			else
			{
				System.out.println("pop3Store has connected");
			}
			com.sun.mail.pop3.POP3Folder pop3Folder = (com.sun.mail.pop3.POP3Folder) pop3Store.getDefaultFolder().getFolder("INBOX");
			if (!pop3Folder.isOpen())
			{
				pop3Folder.open(Folder.READ_WRITE);
			}
			else
			{
				System.out.println("pop3Folder has open ");
			}
			
			if(!pop3Folder.isOpen()){
				System.out.println("pop3Folder open false");
				return;
			}
			if("yes".equals(isDisTitle))
			{
				Message[] msgs = pop3Folder.getMessages();
				System.out.println("========mail subject begin=====");
				for(int i=0;i<msgs.length;i++)
				{	
					try{
						System.out.println("========mail"+i+" subject:"+msgs[i].getSubject()+"  from="+msgs[i].getFrom().toString());
					}catch(Exception e)
					{					
					}
				
				}
				System.out.println("========mail subject over=====");
			}
			int messageCounts = pop3Folder.getMessageCount();
			System.out.println("mail num："+messageCounts);
			System.out.println("run over");
			
		}catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestMail mm = new TestMail();
		mm.testConnect(args[0],args[1],args[2],args[3]);
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
