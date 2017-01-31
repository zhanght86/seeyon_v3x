package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description: 附件列表</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.*;
import java.io.*;


public class AffixList implements Serializable
{
  private long length=0;//记录附件列表中文件得大小
  private List affixs=new ArrayList();
  public AffixList() {
  }
  /**
   * 添加附件到附件列表
   * @param affix
   */
  public long getLength()
  {
    return this.length;
  }
  public void add(Affix affix)
  {
    affixs.add(affix);
    length+=affix.getLength();
  }
  public void add(AffixList affixList)
  {
    int i,len;
    len=affixList.size();
    for(i=0;i<len;i++)
    {
      affixs.add(affixList.get(i));
    }
    this.length+=affixList.getLength();
  }
  public Affix get(int i)
  {
    return (Affix)affixs.get(i);
  }
  public boolean remove(int i)
  {
    Affix ax=(Affix)affixs.get(i);
    long axLength=ax.getLength();
    if(ax.del())
    {
      affixs.remove(i);
      this.length-=axLength;
      return true;
    }
    else
    {
      return false;
    }
  }
  /**
   * 删除所有附件
   * @return
   */
  public boolean removeAll()
  {
    int i,len;
    while(affixs.size()>0)
    {
      if(remove(0)==false) return false;
    }
    this.length=0;
    return true;
  }
  public boolean moveTo(String path) throws Exception
  {
    int i,len;
    len=affixs.size();
    for(i=0;i<len;i++)
    {
      if(get(i).moveTo(path)==false) return false;
    }
    return true;
  }
  public AffixList colog(String path,String coloType) throws Exception
  {
    int i,len;
    AffixList afl=new AffixList();
    len=affixs.size();
    for(i=0;i<len;i++)
    {
      afl.add(this.get(i).colog(path,coloType));
    }
    return afl;
  }
  public int size()
  {
    return affixs.size();
  }

  public void readBaseObject(java.io.ObjectInputStream in,Double fileVer) throws IOException
  {
    Affix ax=null;
    int listSize=in.readInt();
    for(int i=0;i<listSize;i++)
    {
      ax=new Affix();
      ax.readBaseObject(in,fileVer);
      affixs.add(ax);
    }
    this.length=in.readLong();
  }
  public void writeBaseObject(java.io.ObjectOutputStream out) throws IOException
  {
    int listSize=affixs.size();
    out.writeInt(listSize);
    for(int i=0;i<listSize;i++)
    {
      ((Affix)affixs.get(i)).writeBaseObject(out);
    }
    out.writeLong(length);
  }
 public String toOutString()
 {
   int i,len;
   StringBuffer sb=new StringBuffer();
   sb.append("(length="+this.length+")");
   sb.append("链表affix信息");
   len=affixs.size();
   for(i=0;i<len;i++)
   {
     sb.append("\r\n");
     sb.append(((Affix)affixs.get(i)).toOutString());
   }
   return sb.toString();
 }
  public static void main(String[] args) throws Exception {
    AffixList al = new AffixList();
    AffixList al2 = new AffixList();
    al.add(new Affix("测试.emai","c:\\upload\\zhangh\\zhangh 测试微机"));
    al.add(new Affix("测试2222","c:\\upload\\zhangh\\zhangh 测试微机"));
    al.add(new Affix("测试3333","c:\\upload\\zhangh\\zhangh 测试微机"));
//    Affix ax = new Affix("测试.emai","c:\\upload\\zhangh\\zhangh 测试微机");

    /*net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg(al.toOutString());
    net.btdz.oa.common.TDebugOutMsg.outMsg("=================");*/

    File f=new File("c:\\test.txt");
    FileOutputStream fos = new FileOutputStream(f);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    al.writeBaseObject(oos);
    oos.flush();oos.close();
    fos.flush();fos.close();

    FileInputStream fis = new FileInputStream(f);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Affix nax=new Affix();
    al2.readBaseObject(ois,2.41D);
    /*net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg(al2.toOutString());
    net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg("run OVER");*/

  }

}