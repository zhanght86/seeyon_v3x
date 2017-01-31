package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description:MailBoxCfg对象链表 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.*;
import java.io.*;

import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.util.Affix;

public class MbcList implements Serializable{
  private java.util.List list=new ArrayList();
  public MbcList() {
  }
  public boolean add(MailBoxCfg mbc)
  {
    return list.add(mbc);
  }
  public MailBoxCfg get(int i)
  {
    return (MailBoxCfg)list.get(i);
  }
  public void remove(String mail)
  {
    int i,len;
    len=list.size();
    for(i=0;i<len;i++)
    {
      if(((MailBoxCfg)list.get(i)).getEmail().equals(mail))
      {
        list.remove(i);
        return;
      }
    }
  }
  public boolean removeAll()
  {
    while(list.size()>0)
    {
      list.remove(0);
    }
    return true;
  }
  public int size()
  {
    return list.size();
  }
/*
  private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
 {
   int i,len;
   len=list.size();
   out.writeInt(len);
   out.writeObject(list);
 }
 private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
 {
   int i,len;
   len=in.readInt();
   list=(List)in.readObject();
 }
*/

public void readBaseObject(java.io.ObjectInputStream in) throws IOException
{
  MailBoxCfg mbc=null;
  int listSize=in.readInt();
  for(int i=0;i<listSize;i++)
  {
    mbc=new MailBoxCfg();
    mbc.readBaseObject(in);
    list.add(mbc);
  }
}
public void writeBaseObject(java.io.ObjectOutputStream out) throws IOException
{
  int listSize=list.size();
  out.writeInt(listSize);
  for(int i=0;i<listSize;i++)
  {
    ((MailBoxCfg)list.get(i)).writeBaseObject(out);
  }
}
public String toOutString()
{
 int i,len;
 StringBuffer sb=new StringBuffer();
 sb.append("链表MailBoxCfg信息");
 len=list.size();
 for(i=0;i<len;i++)
 {
   sb.append("\r\n");
   sb.append(((MailBoxCfg)list.get(i)).toOutString());
 }
 return sb.toString();
}


  public static void main(String[] args) throws Exception{
    MbcList ml = new MbcList();
    MbcList ml2 = new MbcList();
    MailBoxCfg mbc=new MailBoxCfg();
    MailBoxCfg mbc2=new MailBoxCfg();

    mbc.setAuthorCheck(true);
    mbc.setBackup(false);
    mbc.setDefaultBox(true);
    mbc.setEmail("zhanghua@seeyon.com");
    mbc.setPassword("pppppppasword");
    mbc.setPop3Host("pop3host");
    mbc.setSmtpHost("smtphost");
    mbc.setTimeOut(3334);
    mbc.setUserName("zzzzzzzzzzzzhangh");

    ml.add(mbc);

    mbc2.setAuthorCheck(true);
    mbc2.setBackup(false);
    mbc2.setDefaultBox(true);
    mbc2.setEmail("2222zhanghua@seeyon.com");
    mbc2.setPassword("222pppppppasword");
    mbc2.setPop3Host("222pop3host");
    mbc2.setSmtpHost("222smtphost");
    mbc2.setTimeOut(3334);
    mbc2.setUserName("222zzzzzzzzzzzzhangh");

    ml.add(mbc2);

    File f=new File("c:\\test.txt");
    FileOutputStream fos = new FileOutputStream(f);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    ml.writeBaseObject(oos);
    oos.flush();oos.close();
    fos.flush();fos.close();

    FileInputStream fis = new FileInputStream(f);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Affix nax=new Affix();
    ml2.readBaseObject(ois);
  }
public java.util.List getList()
{
	return list;
}

}