package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description: 附件</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;

import com.seeyon.v3x.webmail.manager.LocalMailCfg;

public class Affix implements Serializable
{
  private String fileName=null;        //文件的显示名称，存放时修改文件名称放置重名覆盖
  private String realPath=null;        //文件的存放路径和文件名称
  private long length=0;
  
  private String contentId="";
  private String contentDisposition="";
  public Affix(String fileName,String realPath,String contentId,String contentDiscription) throws Exception
  {
	  this.fileName=fileName;
	  this.realPath=realPath;
	  this.setContentId(contentId);
	  this.setContentDisposition(contentDiscription);
	  if(checkFile()==false) throw new Exception("附件不存在（"+realPath+"）");
  }
  public String getContentId() {
		return contentId;
	}
  
	public void setContentId(String contentId) {	
		this.contentId = contentId;
		if(this.contentId!=null)
		{
			if(this.contentId.startsWith("<"))
			{
				this.contentId=this.contentId.substring(1);
			}
			if(this.contentId.endsWith(">"))
			{
				this.contentId=this.contentId.substring(0,this.contentId.length()-1);
			}
		}
		if(this.contentId==null){this.contentId="";}
	}
	public String getContentDisposition() {
		return contentDisposition;
	}
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
		if(this.contentDisposition==null){this.contentDisposition="";}
	}
	
  public Affix() {
  }
  public Affix(String fileName,String realPath) throws Exception
  {
    this.fileName=fileName;
    this.realPath=realPath;
    if(checkFile()==false) throw new Exception("附件不存在（"+realPath+"）");
  }
  public void setLength(long length)
  {
    this.length=length;
  }
  public long getLength()
  {
    return this.length;
  }
  public void setFileName(String fileName)
  {
    this.fileName=fileName;
  }
  /**
   * 得到附件名称
   * @return
   */
  public String getFileName()
  {
    return this.fileName;
  }
  public void setRealPath(String realPath)
  {
    this.realPath=realPath;
  }
  /**
   * 得到附件存储的全部路径（存储路径）
   * @return
   */
  public String getRealPath()
  {
    //zhangh 2005-3-21 add 校验文件是否存在,如果不存在可能是由于重新安装系统,安装路径发生变化,进行调整
    if(FileUtil.isFile(this.realPath)==false){this.realPath=LocalMailCfg.adjustAffixPath(this.realPath);}
    return this.realPath;
  }
  /**
   * 返回存储的文件的名称，不包含扩展名
   * @return
   */
  public String getRealFileName()
  {
    int iPos=-1;
    if((iPos=realPath.lastIndexOf("/"))!=-1 && iPos<realPath.length())
    {
      return realPath.substring(iPos+1);
    }
    else
    {
      return realPath;
    }
  }
  //得到附件得名称，不包括扩展名
  public String getDispName()
  {
    int iPos=-1;
    if((iPos=fileName.indexOf("."))!=-1){return fileName.substring(0,iPos);}
    else {return fileName;}
  }
  public String getDispExtName()
  {
    int iPos=-1;
    if((iPos=fileName.lastIndexOf("."))!=-1 && iPos<fileName.length()){return fileName.substring(iPos+1);}
    else {return "";}
  }
  /**
   * 去掉附件得根路径
   * @return
   */
  public String getDownPath()
  {
    String temp="";
    int len=LocalMailCfg.getMailBasePath().length();
    if(realPath.length()>len)
    {
      temp=realPath.substring(len);
    }
    return temp;
  }
  /**
   * 克隆文件
   * @param path
   * @param coloType:新文件得命名方式,copy:文件名称不变,delext:去扩展名称,
   * @return
   * @throws java.lang.Exception
   */
  public Affix colog(String path,String coloType) throws Exception
  {
    Affix affix=new Affix();
    affix.setFileName(this.fileName);
    int pos=-1;
    String newFileName=UniqueCode.generate();
    if(!"delext".equals(coloType)){newFileName+=this.getDispExtName();}
    realPath=getRealPath();
    File fs=new File(realPath);
    if(!fs.exists()){throw new Exception("找不到原文件（"+realPath+"）");}
    File fo=new File(path+newFileName);
    FileUtil.copy(fs,fo);
    affix.setRealPath(path+newFileName);
    affix.setLength(this.length);
    return affix;
  }
  public boolean moveTo(String path) throws Exception
  {
    boolean bRet=false;
    String fileName="";
    String errMsg="移动附件错误：从（"+realPath+"）到（"+path+"）：";
    realPath=getRealPath();
    try{
      bRet = FileUtil.moveFile(realPath,path);
    }catch(Exception e)
    {
          throw new Exception(errMsg+System14.FormatForJs(e.getMessage()));
    }
    if(bRet){fileName=new File(realPath).getName();realPath=path+fileName;}
    else{throw new Exception(errMsg);}
    return bRet;
  }
  public boolean del()
  {
    File f=new File(realPath);
    if(f.exists())
    {
      return f.delete();
    }
    return true;
  }
  /**
   * 校验附件
   * @return
   */
  private boolean checkFile()
  {
    File f=new File(realPath);
    if(f.exists() && f.isFile())
    {
       try{length=f.length();}catch(Exception e){}
       return true;
    }
    else return false;
  }
  public void readBaseObject(java.io.ObjectInputStream in,Double fileVer) throws IOException
  {
    this.length=in.readLong();
    this.fileName=in.readUTF();
    this.realPath=in.readUTF();
    if(fileVer>Double.parseDouble("2.41"))
    {
    	this.contentId=in.readUTF();
    	this.contentDisposition=in.readUTF();
    }
  }
  public void writeBaseObject(java.io.ObjectOutputStream out) throws IOException
  {
    out.writeLong(length);
    out.writeUTF(fileName);
    out.writeUTF(realPath);
    out.writeUTF(contentId);
    out.writeUTF(contentDisposition);
  }
  public String toOutString()
  {
    return "(fileName="+fileName+")(length="+length+")(realPath="+realPath+")";
  }
  public static void main(String[] args) throws Exception
  {
    Affix ax = new Affix("测试.emai","c:\\upload\\zhangh\\zhangh 测试微机");

    /*net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg(ax.toOutString());
    net.btdz.oa.common.TDebugOutMsg.outMsg("=================");*/

    File f=new File("c:\\test.txt");
    FileOutputStream fos = new FileOutputStream(f);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    ax.writeBaseObject(oos);
    oos.flush();oos.close();
    fos.flush();fos.close();

    FileInputStream fis = new FileInputStream(f);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Affix nax=new Affix();
    nax.readBaseObject(ois,2.41D);
    /*net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg(nax.toOutString());
    net.btdz.oa.common.TDebugOutMsg.outMsg("=================");
    net.btdz.oa.common.TDebugOutMsg.outMsg("run OVER");*/
  }

}