package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description: 维护一个邮箱目录中得邮件列表</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.*;
import javax.mail.internet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.MailTools;
import com.seeyon.v3x.webmail.util.UniqueCode;

public class MailBoxFolder {

  public final static int FOLDER_SEND=0;//发件箱
  public final static int FOLDER_CUR=1;//收件箱
  public final static int FOLDER_DRAFT=2;//草稿箱
  public final static int FOLDER_TRASH=3;//垃圾箱
  
  private Map mailMap = new HashMap();
  private String obj = "";
  
  private final static Log logger = LogFactory.getLog(MailBoxFolder.class);

  private String getFolderName(int folderType)
  {
    String fn="";
    switch(folderType)
    {
      case MailBoxFolder.FOLDER_CUR  :
        fn="INBOX";
        break;
      case MailBoxFolder.FOLDER_SEND :
        fn="send";
        break;
      case MailBoxFolder.FOLDER_DRAFT:
        fn="draft";
        break;
      case MailBoxFolder.FOLDER_TRASH :
        fn="temp";
        break;
    }
    return fn;
  }
  private String userId="";
  private int folderType=0;
  private MailInfoList ml=new MailInfoList();

  public MailBoxFolder(String userId)
  {
	if(mailMap.containsValue(userId)){
		obj = (String)mailMap.get(userId);
	}else{
		obj = new String(userId);
		mailMap.put(userId, obj);
	}
  }

  public void finalize(){
	  mailMap.remove(obj);
  }


  /**
   * 调入用户指定邮箱的邮件列表
   * @param userId
   * @param folderType
   * @return
   * @throws java.lang.Exception
   */
  public boolean load(String userId,int folderType) throws Exception
  {
//	  synchronized(obj){
    this.userId=userId;
    this.folderType=folderType;
    String fn=getMailListFile();
    File f=new File(fn);
    if(f.length()>0)
    {
      FileInputStream fi = new FileInputStream(f);
      ObjectInputStream oi = new ObjectInputStream(fi);
      //ml=(MailInfoList)oi.readObject();
      //LocalMailCfg.setMailInfoVersion(oi.readUTF());//读取文件版本号
      //ml.readBaseObject(oi);
      
      //读取时候，不应该把原始邮件列表信息文件版本保存到当前代码版本里面
      //LocalMailCfg.setMailInfoVersion(oi.readUTF());//读取文件版本号      
      Double fileVer=Double.parseDouble(LocalMailCfg.getMailInfoVersion());
      try{fileVer=Double.parseDouble(oi.readUTF());}catch(Exception e){logger.error("Err读取版本号:"+e.getMessage());}
      ml.readBaseObject(oi,fileVer);
      if(oi!=null)
      {
    	  oi.close();
      }
      if(fi!=null)
      {
    	  fi.close();
      }
     }
//	  }
    return true;
  }
  
  public boolean backUpCurFile(String userId,int folderType)throws Exception{
	    this.userId=userId;
	    this.folderType=folderType;
	    String fn=LocalMailCfg.getMailListFile(userId,folderType);
	    File f=new File(fn);
	    if(f.length()>0)
	    {
	      FileInputStream fi = new FileInputStream(f);
	      ObjectInputStream oi = new ObjectInputStream(fi);
	      //ml=(MailInfoList)oi.readObject();
	      //LocalMailCfg.setMailInfoVersion(oi.readUTF());//读取文件版本号
	      
	      //ml.readBaseObject(oi);
	      
	      Double fileVer=Double.parseDouble(LocalMailCfg.getMailInfoVersion());
	      try{fileVer=Double.parseDouble(oi.readUTF());}catch(Exception e){}
	      ml.readBaseObject(oi,fileVer);

	      oi.close();
	      fi.close();
	     }
	    return true;	  
  }
  
  public boolean save() throws Exception
  {
	 
	synchronized(obj){ 
	  
    //保存之前对邮件列表进行排序
    this.sortBySendDate();
    String fp=LocalMailCfg.getMailListFile(userId,folderType);
    File f=new File(fp);
    FileOutputStream fo=new FileOutputStream(f);
    ObjectOutputStream oo=new ObjectOutputStream(fo);
    //oo.writeObject(ml);
    oo.writeUTF(LocalMailCfg.getMailInfoVersion());//写文件版本号
    ml.writeBaseObject(oo);
    oo.flush();
    oo.close();
    fo.flush();
    fo.close();
	}
    return true;
  }
  /**
   * 添加邮件到邮件列表
   * @param mi
   * @return
   */
  public boolean addMail(MailInfo mi)
  {
    return ml.add(mi);
  }
  public boolean addMail(MailInfoList mil) throws Exception
  {
    if(ml.add(mil)==false) return false;
    return save();
  }
  /**
   * 添加邮件到邮件列表
   * @param mi
   * @param isMoveAffix：是否移动附件
   * @return
   */
  public boolean addMail(MailInfo mi,boolean isMoveAffix) throws Exception
  {
    if(isMoveAffix)
    {
      String path=LocalMailCfg.getUserAffixPath(userId);
      if(mi.moveAffixsTo(path)==false) return false;
    }
    return ml.add(mi);
  }
  /**
   * 删除邮件
   * @param mailId
   * @return
   */
  public boolean delMail(String mailNumber,boolean isSave) throws Exception
  {
    MailInfo mi=ml.getMail(mailNumber);
    if(mi!=null)
    {
      if(mi.del()){ml.remove(mi);}
      else {return false;}
    }
    if(isSave) return save();
    else return true;
  }
  public boolean delMail(String [] mailIds) throws Exception
  {
    for(int i=0;i<mailIds.length;i++)
    {
      if(delMail(mailIds[i],false)==false)
      {
        save();
        return false;
      }
    }
    return save();
  }
  public boolean delAllMail() throws Exception
  {
    int i;
    String mailId="";
    while(ml.size()>0)
    {
      mailId=ml.get(0).getMailNumber();
      if(delMail(mailId,false)==false)
      {
        save();
        return false;
      }
    }
    return save();
  }
  public MailInfo getMail(String mailId)
  {
    return ml.getMail(mailId);
  }
  /**
   * 从目录中移出邮件
   * @param mailId
   */
  public MailInfo moveOutMail(String mailId)
  {
    return ml.remove(mailId);
  }
  public MailInfoList moveOutMail(String [] mailIds)
  {
    MailInfo mi=null;
    MailInfoList mil=new MailInfoList();
    for(int i=0;i<mailIds.length;i++)
    {
      mi=ml.remove(mailIds[i]);
      if(mi!=null) mil.add(mi);
    }
    return mil;
  }
  public MailInfoList findMail(String [] mailIds)
  {
    MailInfo mi=null;
    MailInfoList ml=new MailInfoList();
    for(int i=0;i<mailIds.length;i++)
    {
      mi=getMail(mailIds[i]);
      if(mi!=null){ml.add(mi);}
    }
    return ml;
  }
  private String getMailListFile()
  {
    String fn=LocalMailCfg.getMailListFile(userId,folderType);
    File f=new File(fn);
    if(!f.exists())
    {//版本升级的时候，此列表文件不存在，把旧邮件信息修改为现在的存储方式
      initMailInfo();
    }
    return fn;
  }
  /**
   * 得到目录中的邮件列表
   * @return
   */
  public MailInfoList getMailList()
  {
    return ml;
  }
  public boolean sortBySendDate()
  {
    return ml.sortBySendDate();
  }
  private boolean initMailInfo()
  {
    String _mboxPath=LocalMailCfg.getUserMailPath(userId);
    ConnMailhost conn = new ConnMailhost();
    Store store = null;
    try {
      store = conn.getLocalStore(_mboxPath);
      if (!store.isConnected()){store.connect();}
      Folder mailbox_INBOX = null;
      try {
        mailbox_INBOX = store.getFolder(getFolderName(folderType));
        if (!mailbox_INBOX.isOpen()){mailbox_INBOX.open(Folder.READ_WRITE);}
      }
      catch (FolderNotFoundException e) {
        logger.error("收件箱不存在！", e);
      }
      catch (MessagingException e) {
        logger.error("邮件系统出现异常，请稍后再试！", e);
      }
      catch (Exception e) {
        logger.error("ERR:" + e.toString());
      }
      Message[] msgsINBOX = mailbox_INBOX.getMessages();
      try {
        MailInfo mi=null;
        String mid="";
        String tempPath=LocalMailCfg.getUserTempPath(userId);
        for(int i=0;i<msgsINBOX.length;i++)
        {
          /*邮件ID是在接收邮件时，从邮件服务器得到的，已经接收到的邮件无法对应邮件ID*/
          mid=UniqueCode.generate();
          mi=MailTools.changFormat(mid,msgsINBOX[i],tempPath);
          addMail(mi,true);
        }
        save();
        /*删除原始邮件*/
        for(int i=0;i<msgsINBOX.length;i++){msgsINBOX[i].setFlag(Flags.Flag.DELETED,true);}
      }
      catch (Exception e) {
        System.err.println("Exception MailBoxFolder.initMailInfo :" + e);
      }
      if (store != null)
        store.close();
    }
    catch (Throwable e) {
      try {
        if (store != null)
          store.close();
      }
      catch (javax.mail.MessagingException ex) {}
      conn = null;
    }
    conn = null;
    return true;
  }
  private static void test()
  {
    String userId="";
    MailInfo mi=null;
    MailBoxFolder mbf = new MailBoxFolder("123");
    try{
      mbf.load("zhangh", MailBoxFolder.FOLDER_CUR);
      logger.info("conut:"+mbf.getMailList().size());
      mi=new MailInfo();
      mi.setTo("zhangh@seeyon.com");
      mi.setSubject("22222222222");
      mi.setContentText("zhangh","contebnt");
      MailManager.sendMail("zhangh",mi);
      mbf.addMail(mi,true);
      mbf.save();
      logger.info("conut:"+mbf.getMailList().size());
      logger.info(mbf.getMailList().get(2).getSubject());
      //mbf.get
    }catch(Exception e)
    {
      logger.error("ERR:"+e.getMessage());
    }
  }
  private static void testSaveMessage()
  {
    ConnMailhost conn = new ConnMailhost();
    Store store = null;
    try {
      store = conn.getLocalStore(LocalMailCfg.getUserTempPath("zhangh"));
      if (!store.isConnected()) {
        store.connect();
      }
      Folder mailbox_INBOX = null;
      try {
        mailbox_INBOX = store.getFolder("zip");
        if (!mailbox_INBOX.isOpen()) {
          mailbox_INBOX.open(Folder.READ_WRITE);
        }
        Message[] msg=new Message[1];
        MailInfo mi=new MailInfo();
        mi.setSubject("2222222subject");
        mi.setFrom("zhaghufrom@seeyon.com");
        mi.setTo("to@seeyon.com");
        mi.setContentText("zhangh","youjina  张华的邮件正文");
        mi.getAffixList().add(new Affix("附件.jpg","C:\\upload\\~attachment\\zhangh\\00ff1fbc652e000b.JPG"));
        mi.getAffixList().add(new Affix("附件666666.jpg","C:\\upload\\~attachment\\zhangh\\00ff1fbc64ac0009.JPG"));
        MailBoxCfg mbc=null;
        msg[0]=MailTools.changFormat(mbc,mi);
        /*直接得到邮件流写文件测试
        InputStream is=msg[0].getInputStream();
         File f=new File("C:\\upload\\~maildata\\zhangh\\.zip\\cur\\is.mail");
        FileOutputStream os=new FileOutputStream(f);
        BufferedOutputStream bos=new BufferedOutputStream(os);
        byte b[] = new byte[1024 * 1024]; //define 1M buffer for save attachment.
        int len;
        InputStream bis = new BufferedInputStream(is);
        while ( (len = bis.read(b, 0, b.length)) != -1) {
         bos.write(b, 0, len);
         bos.flush();
        }
        bos.flush();
        bos.close();
        */
        mailbox_INBOX.appendMessages(msg);
        mailbox_INBOX.close(true);
      }
      catch (FolderNotFoundException e) {
        logger.error("收件箱不存在！", e);
      }
      catch (MessagingException e) {
        logger.error("邮件系统出现异常，请稍后再试！", e);
      }
      catch (Exception e) {
        logger.error("ERR:" + e.toString());
      }
      Message[] msgsINBOX = mailbox_INBOX.getMessages();
    }catch(Exception e)
    {
      logger.error("Err:"+e.getMessage());
    }

  }
  public static void testRead()
  {
    try{
      MailBoxFolder mbf = new MailBoxFolder("123");
      mbf.load("zhangh", MailBoxFolder.FOLDER_CUR);
      MailInfoList mil=mbf.getMailList();
      int i,len;
      len=mil.size();
      for(i=0;i<len;i++)
      {
        logger.info(mil.get(i).getMailId()+"==========");
        logger.info(mbf.getMail(mil.get(i).getMailId()).getMailId());
      }
      logger.info(mbf.getMail("1tbiAQG90D+LdtH-7wAAmu").getMailId());
    }catch(Exception e)
    {
      logger.error("ERR:"+e.getMessage());
    }
  }
  public static void main(String[] args) {
    //testSaveMessage();
    //test();
    //testRead();
  }
}