package com.seeyon.v3x.webmail.domain;

/**
 * <p>Title: </p>
 * <p>Description:记录邮件信息 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;
import java.util.*;

import com.seeyon.v3x.webmail.manager.LocalMailCfg;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.AffixList;
import com.seeyon.v3x.webmail.util.FileUtil;
import com.seeyon.v3x.webmail.util.System14;
import com.seeyon.v3x.webmail.util.UniqueCode;
import com.seeyon.v3x.common.utils.UUIDLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MailInfo  implements Serializable
{
	private static Log log = LogFactory.getLog(MailInfo.class);
  private String mailNumber="";//邮件本地存储编号,新建邮件时不能从服务器得到邮件唯一标示
  private String mailId="";//邮件id，从邮件服务器获得，每封邮件都有唯一ID，id为包含特殊字符得字符串
  private String mailBoxName="";//记录这个邮件是在那个邮箱里面,收到得邮件可能发给多人
  private String from="";//发件人
  private String to="";//收件人
  private String cc="";//抄送
  private String bc="";//暗送
  private String subject="";//标题
  private String replyTo="";//邮件回复地址
  private String contentType="html";//邮件正文类型
  private String contentFile="";//邮件正文对应得文件
  private AffixList affixList=new AffixList();//邮件附件列表
  private boolean reply=false;//邮件是否要求回复
  private long size=0;//邮件大小
  private int priority=0;//重要程度
  private Date sendDate=new Date(System.currentTimeMillis());//发送日期
  private boolean readFlag=false;//阅读标志
  private String noteAddress="";//要求读后回条的地址
  private String contentText="";//不生成临时文件,直接保存到string中
  
  private long mailLongId = 0l;
  
  private boolean hasAffix = false;
  
  private final static Log logger = LogFactory.getLog(MailInfo.class);

  public MailInfo() {
	  this.mailLongId = UUIDLong.longUUID();
  }
  
  public void setMailNumber(String mailNumber)
  {
    this.mailNumber=mailNumber;
  }
  public String getMailNumber()
  {
    return this.mailNumber;
  }
      public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  public String getMailBoxName()
  {
    return mailBoxName;
  }
  public void setMailBoxName(String mailBoxName)
  {
    this.mailBoxName=mailBoxName;
  }
  public String getContentType() {
    return this.contentType;
  }

  public void setMailId(String mailId)
  {
    this.mailId=mailId;
  }
  public String getMailId()
  {
    return this.mailId;
  }
  /**
   * 收条回复地址
   * @param noteAddress
   */
  public void setNoteAddress(String noteAddress)
  {
    this.noteAddress=noteAddress;
  }
  public String getNoteAddress()
  {
    return this.noteAddress;
  }
  public void setFrom(String from)
  {
    this.from=from;
  }
  /**
   * 发件人完整地址“发件人姓名<发件人邮件>”
   * @return
   */
  public String getFrom()
  {
    return this.from;
  }
  /**
   * 得到发件人名称
   * @return
   */
  public String getFromName()
  {
    int ipos=-1;
    if((ipos=from.indexOf('<'))!=-1 && ipos>0){return from.substring(0,ipos);}
    else{return from;}
  }
  /**
   * 得到邮箱地址
   * @return
   */
  public String getFromAdd()
  {
    int ib=-1,ie=-1;
    if((ib=from.indexOf('<'))!=-1 && (ie=from.indexOf('>'))!=-1)
    {
      return from.substring(ib+1,ie);
    }
    else
    {
      return from;
    }
  }
  public void setTo(String to)
  {
    this.to=to;
  }
  public String getTo()
  {
    return this.to;
  }
  public void setSubject(String subject)
  {
    this.subject=subject;
  }
  public String getSubject()
  {
    return this.subject;
  }

  public void setCc(String cc)
  {
    this.cc=cc;
  }
  public String getCc()
  {
    return this.cc;
  }
  public void setBc(String bc)
  {
    this.bc=bc;
  }
  public String getBc()
  {
    return this.bc;
  }
  public void setReplyTo(String replyTo)
  {
    this.replyTo=replyTo;
  }
  public String getReplyTo()
  {
    return this.replyTo;
  }

  public void setContentFile(String contentFile)
  {
    this.contentFile=contentFile;
  }
  public String getContentFile()
  {
    return this.contentFile;
  }
  public void setContentText(String conStr)
  {
    this.contentText=conStr;
  }
  /**
   * 得到邮件的正文内容
   * @return
   * @throws java.lang.Exception
   */
  public String getContentText() throws Exception
  {
    if(contentText!=null && contentText.length() > 0){return contentText;}
    int len;
    if(contentFile != null && contentFile.length() > 0){
    	File f=new File(contentFile);
        //zhangh 2005-3-21 检查邮件正文文件是否存在,不存在
        if(f.exists()==false)
        {
          contentFile=LocalMailCfg.adjustAffixPath(contentFile);
          f=new File(contentFile);
        }

        byte[] data = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(f);
			len=(int)f.length();
			data = new byte[len];
			len=fin.read(data);
		} catch (Exception e) {
			log.error("",e);
	    	return "";
		}finally{
			if(fin!=null)
				fin.close();
		}
        return new String(data);
    }
    else{
    	return "";
    }
  }
  
  /**
   * 得到邮件内容，替换图片引用路径
   * @return
   * @throws Exception
   */
  public String getContentText2() throws Exception
  {	  
	  Affix affix;
	  String strTemp=this.getContentText();
	  String downUrl="/seeyon/webmail.do?method=doDownloadAtt";
	  for(int i=0;i<affixList.size();i++)
	  {
		  affix=affixList.get(i);
		  if(affix.getContentId()!=null)
		  {
			  String fileUrl=downUrl+"&filename="+affix.getFileName() +"&filePath="+affix.getDownPath();
			  strTemp=strTemp.replace("cid:"+affix.getContentId(),fileUrl);
		  }
	  }
	  if("html".equals(this.contentType)){
		  return strTemp;
	  }else{
		  return "<pre>" + strTemp + "</pre>" ;
	  }
  }

  
  public boolean setContentText(String path,String content,String param) throws Exception
  {
    path+=UniqueCode.generate();
    path+=".main";
    File file =new File(path);

    FileWriter fw=new FileWriter(file);
    fw.write(content);
    fw.flush();
    fw.close();
    contentFile=path;
//    size+=file.length();
    return true;
  }
  public boolean setContentText(String userId,String content) throws Exception
  {
    String path=LocalMailCfg.getUserTempPath(userId);
    return this.setContentText(path,content,null);
  }
  public void setReply(boolean reply)
  {
    this.reply=reply;
  }
  public boolean getReply()
  {
    return this.reply;
  }
  public void setSize(long size)
  {
    this.size=size;
  }
  public long getSize()
  {//添加附件时候，有时不通过setAffixList，而是通过getAffixList().add()这样就无法统计附件大小
//    if(hasAffix())
//    {
//      this.size+=affixList.getLength();
//    }
    if(this.size<0){this.size=-this.size;}
    if(this.size==0)
    {
    	return getSize(this.contentFile)+affixList.getLength();
    }
    return this.size;
  }
  public void setPriority(int priority)
  {
    this.priority=priority;
  }
  public int getPriority()
  {
    return this.priority;
  }
  public void setSendDate(Date sendDate)
  {
    this.sendDate =sendDate;
  }
  public Date getSendDate()
  {
    return this.sendDate;
  }
  public void setRead(boolean readFlag)
  {
    this.readFlag=readFlag;
  }
  public boolean getRead()
  {
    return this.readFlag;
  }
  public void setReadFlag(boolean readFlag)
  {
    this.readFlag=readFlag;
  }
  public boolean getReadFlag()
  {
    return this.readFlag;
  }
  public void setAffixList(AffixList affixList)
  {
    this.affixList=affixList;
//    size+=affixList.getLength();
  }
  public AffixList getAffixList()
  {
    return this.affixList;
  }
  /**
   * 邮件是否包含附件
   * @return
   */
  public boolean hasAffix()
  {
    if(affixList==null || affixList.size()<1) return false;
    else return true;
  }
  
  public boolean getHasAffix()
  {
	  if(affixList==null || affixList.size()<1) return false;
	    else return true;
  }
  /**
   * 邮件删除，删除邮件的附件和正文
   * @return
   */
  public boolean del()
  {
    if(affixList.removeAll()==false) return false;
    File f=new File(this.getContentFile());
    if(f.exists())
    {
      return f.delete();
    }
    return true;
  }
  public boolean moveAffixsTo(String path) throws Exception
  {
    return (moveContentTo(path) && affixList.moveTo(path));
  }
  /**
   * 移动文件正文到知道路径（正文和附件一样存储）
   * @param path
   * @return
   */
  private boolean moveContentTo(String path) throws Exception
  {
    boolean bRet=false;
    String fileName="";
    String errMsg="移动正文错误：从（"+contentFile+"）到（"+path+"）：";
    //errMsg=System14.FormatForJs(errMsg);
    try{
      bRet = FileUtil.moveFile(contentFile,path);
    }catch(Exception e)
    {
      throw new Exception(errMsg+ System14.FormatForJs(e.getMessage()));
    }
    if(bRet){fileName=new File(contentFile).getName();contentFile=path+fileName;}
    else{throw new Exception(errMsg);}
    return bRet;
  }
  public void readBaseObject(java.io.ObjectInputStream in,Double fileVer) throws IOException{
      affixList.readBaseObject(in,fileVer);
      this.bc = in.readUTF();
      this.cc = in.readUTF();
      this.contentFile = in.readUTF();
      this.contentType = in.readUTF();
      this.from = in.readUTF();
      this.reply = in.readBoolean();
      this.mailBoxName = in.readUTF();
      this.mailId = in.readUTF();
      this.mailNumber = in.readUTF();
      this.noteAddress = in.readUTF();
      this.priority = in.readInt();
      this.readFlag = in.readBoolean();
      this.replyTo = in.readUTF();
      this.readFlag = in.readBoolean();
      this.replyTo = in.readUTF();
      this.subject = in.readUTF();
      this.sendDate = new Date(in.readLong());
      this.size = in.readLong();
      this.to = in.readUTF();
      this.mailLongId = in.readLong();
  }
  public void writeBaseObject(java.io.ObjectOutputStream out) throws IOException
  {
    this.affixList.writeBaseObject(out);
    out.writeUTF(this.bc);
    out.writeUTF(this.cc);
    out.writeUTF(this.contentFile);
    out.writeUTF(this.contentType);
    out.writeUTF(this.from);
    out.writeBoolean(this.reply);
    out.writeUTF(this.mailBoxName);
    out.writeUTF(this.mailId);
    out.writeUTF(this.mailNumber);
    out.writeUTF(this.noteAddress);
    out.writeInt(this.priority);
    out.writeBoolean(this.readFlag);
    out.writeUTF(this.replyTo);
    out.writeBoolean(this.readFlag);
    out.writeUTF(this.replyTo);
    out.writeUTF(this.subject);
    out.writeLong(this.sendDate.getTime());
    out.writeLong(this.size);
    out.writeUTF(this.to);
    out.writeLong(this.mailLongId);
  }
  public String toOutString()
  {
    String sb="(affixList="+affixList.toOutString()+")+(this.bc="+bc+")+(this.cc="+cc+")+(this.contentFile="+contentFile+")+(this.contentType="+contentType+")+(this.from="+from+")+(this.isReply="+reply+")+(this.mailBoxName="+mailBoxName+")+(this.mailId="+mailId+")+(this.mailNumber="+mailNumber+")+(this.noteAddress="+noteAddress+")+(this.priority="+priority+")+(this.readFlag="+readFlag+")+(this.replyTo="+replyTo+")+(this.readFlag="+readFlag+")+(this.replyTo="+replyTo+")+(this.sendDate="+sendDate+")+(this.size="+size+")+vthis.subject="+subject+")+(this.to="+to+")";
    return sb;
  }

  public static void main(String[] args) throws Exception {
    MailInfo mi = new MailInfo();
    MailInfo mi2 = new MailInfo();
    mi.setSubject("主题");
    mi.setBc("bbbbbcccccccc");
    mi.setTo("ttttttttoooooo");
    mi.setSize(123);
    mi.setContentText("zhangh","正文内容");
    mi.affixList.add(new Affix("测试1111","c:\\upload\\zhangh\\zhangh 测试微机"));
    mi.affixList.add(new Affix("测试2222","c:\\upload\\zhangh\\zhangh 测试微机"));
    mi.affixList.add(new Affix("测试3333","c:\\upload\\zhangh\\zhangh 测试微机"));

    File f=new File("c:\\test.txt");
    FileOutputStream fos = new FileOutputStream(f);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    mi.writeBaseObject(oos);
    oos.flush();oos.close();
    fos.flush();fos.close();

    FileInputStream fis = new FileInputStream(f);
    ObjectInputStream ois = new ObjectInputStream(fis);
    mi2.readBaseObject(ois,2.41D);
  }
public long getMailLongId()
{
	return mailLongId;
}
public void setMailLongId(long mailLongId)
{
	this.mailLongId = mailLongId;
}
private  static long getSize(String mail) {
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
}