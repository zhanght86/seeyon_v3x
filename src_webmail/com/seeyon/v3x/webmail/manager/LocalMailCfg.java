package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description:
 * 得到本地邮件存放的相关信息
 *  </p>
 * 所有得文件路径分割法全部用反斜杠，路径以反斜杠结束
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.webmail.domain.MailInfo;
import com.seeyon.v3x.webmail.domain.MailInfoList;
import com.seeyon.v3x.webmail.util.Affix;
import com.seeyon.v3x.webmail.util.AffixList;
import com.seeyon.v3x.webmail.util.FileUtil;
import com.seeyon.v3x.webmail.util.System14;

public class LocalMailCfg {
  private static String mailInfoVersion="3.0";//邮件信息存储格式版本号
  private static String mailBoxCfgVersion="2.41";//邮箱配置信息格式版本号
  private static String mailIdVersion="2.41";//接收邮件ID存储文件版本

  private static String mailCfgFile="mail.cfg";
  private static String inFileName="in.ind";
  private static String mailSendFile="mailsend.lis";
  private static String mailCurFile="mailcur.lis";
  private static String mailDraftFile="maildraft.lis";
  private static String mailTempFile="mailtemp.lis";
  
  private final static Log logger = LogFactory.getLog(LocalMailCfg.class);

  private static WebMailManager webMailManager = (WebMailManager) ApplicationContextHolder.getBean("webMailManager");

  /**
   * 本地邮件存放根路径
   */
  //private static String localMailBasePath="D:/upload/";
  private static String localMailBasePath = SystemProperties.getInstance().getProperty("webmail.savePath");
  private static String mailDir="~maildata/";//邮件配置信息,列表信息相对跟路径的存放目录
  private static String attachmentDir="~attachment/";//邮件附件相对跟路径的存放目录
  static{
    try{
      //localMailBasePath = Standard.getHandel().getUploadPath();
      localMailBasePath = SystemProperties.getInstance().getProperty("webmail.savePath");
      localMailBasePath = System14.ReplaceStr(localMailBasePath, "\\", "/");
      if (localMailBasePath.length() < 4) {
        localMailBasePath = SystemProperties.getInstance().getProperty("webmail.savePath");
      }
      if (localMailBasePath.charAt(localMailBasePath.length() - 1) != '/') {
        localMailBasePath += "/";
      }
    }catch(Exception e)
    {
      localMailBasePath="D:/upload/";
      logger.fatal("Err:得到邮件路径错误", e);
    }
  }

  public LocalMailCfg() {
  }

  public static String getMailInfoVersion()
  {
    return mailInfoVersion;
  }
  public static void setMailInfoVersion(String mailInfoVersion)
  {
    mailInfoVersion=mailInfoVersion;
  }

  public static String getMailBoxCfgVersion()
  {
    return mailBoxCfgVersion;
  }
  public static void setMailBoxCfgVersion(String mailBoxCfgVersion)
  {
    mailBoxCfgVersion=mailBoxCfgVersion;
  }

  public static String getMailIdVersion()
  {
    return mailIdVersion;
  }
  public static void setMailIdVersion(String mailIdVersion)
  {
    mailIdVersion=mailIdVersion;
  }

  /**
   * 得到用户邮件存放路径
   * @param userId
   * 用户ID
   * @return
   * 用户邮件存放路径
   */
  public static String getUserMailPath(String userId)
  {
    String path=localMailBasePath+mailDir+userId+"/";
    checkDir(path);
    return path;
  }
  
  /**
   * 得到用户邮件所占硬盘大小
   * 方法描述：
   *
   */
  public static long getMailSpaceSize(String userId){
	   String path=getUserAffixPath(userId);	  
	   File file = new File(path);
	   long size = 0;
	   
	   if(null!=file){
		   List<File> fileList = new ArrayList<File>();
		   MailInfoList mlis = new MailInfoList();
		   MailInfoList mlisend;
		   MailInfoList mliscur;
		   MailInfoList mlisdraft;
		   MailInfoList mlistrash;
			try {
				mlisend = webMailManager.getMailBoxFolder(userId, MailBoxFolder.FOLDER_SEND).getMailList();
				mliscur = webMailManager.getMailBoxFolder(userId, MailBoxFolder.FOLDER_CUR).getMailList();
				mlisdraft = webMailManager.getMailBoxFolder(userId, MailBoxFolder.FOLDER_DRAFT).getMailList();
				mlistrash = webMailManager.getMailBoxFolder(userId, MailBoxFolder.FOLDER_TRASH).getMailList();
				mlis.add(mlisend);
				mlis.add(mliscur);
				mlis.add(mlisdraft);
				mlis.add(mlistrash);
				//遍历出不在列表中的垃圾文件，删除掉。
				for (int i = 0, j = mlis.size(); i < j; i++) {
					MailInfo ml = mlis.get(i);
					AffixList al = ml.getAffixList();
					for (int k = 0, l = al.size(); k < l; k++) {
						Affix affix = al.get(k);
						File tempf = new File(affix.getRealPath());
						fileList.add(tempf);
					}
					File f = new File(ml.getContentFile());
					fileList.add(f);
				}
				File[] childFiles = file.listFiles();
				if (null != childFiles && childFiles.length > 0) {
					for (int i = 0; i < childFiles.length; i++) {
						if (!fileList.contains(childFiles[i])) {
							//删除垃圾文件
							childFiles[i].delete();
						} else {
							size += childFiles[i].length();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		   return size;
		}
		return 0L;
  }
  //协同附件存放路径
  public static String getUserInfoPath(String userId)
 {
   String path=localMailBasePath+userId+"/";
   checkDir(path);
   return path;
 }
  public static String getUserUploadTempPath(String userId)
  {
    String path=getUserTempPath(userId)+"tempUpload/";
    checkDir(path);
    return path;
  }
  public static String getUserTempPath(String userId)
  {
    String path=localMailBasePath+attachmentDir+userId+"/atttemp/";
    checkDir(path);
    return path;
  }
  public static String getUserAffixPath(String userId)
  {
    String path=localMailBasePath+attachmentDir+userId+"/";
    checkDir(path);
    return path;
  }
  /**
   * 得到用户邮箱配置的文件路径
   * 原来存放在数据库中的，现在存放到用户邮件目录中
   * @param userId
   * @return
   */
  public static String getUserCfgFile(String userId)
  {
    return getUserMailPath(userId)+mailCfgFile;
  }
  /**
   * 得到用户接收邮件的ID列表文件，接收邮件时需要根据该文件记录的邮件ID判断是否接收
   * 原来存放在数据库中的现在存放到用户邮件目录中
   * @param userId
   * @return
   */
  public static String getMailIndexFile(String userId)
  {
    return getUserMailPath(userId)+inFileName;
  }
  /**
   * 得到用户邮件列表文件
   * @param userId
   * @param folderType：邮件目录、见MailBoxFolder定义
   * @return
   */
  public static String getMailListFile(String userId,int folderType)
  {
    String fn="";
    switch(folderType)
    {
      case MailBoxFolder.FOLDER_SEND :fn=mailSendFile;break;
      case MailBoxFolder.FOLDER_CUR  :fn=mailCurFile;break;
      case MailBoxFolder.FOLDER_DRAFT:fn=mailDraftFile;break;
      case MailBoxFolder.FOLDER_TRASH :fn=mailTempFile;break;
    }
    return getUserMailPath(userId)+fn;
  }

  public static String delBasePath(String path)
  {
    int pos=localMailBasePath.length();
    if(path.length()>pos) return path.substring(pos);
    else return path;
  }

  private static boolean checkDir(String path)
  {
    File f=new File(path);
    if(f.exists()==false) return f.mkdirs();
    else return true;
  }
  public static String getMailBasePath()
  {
    return localMailBasePath;
  }
  /**
   * 修改用户名称的时候需要修改用户
   * @param oldUser
   * @param newUser
   * @return
   */
  public static boolean updMailStorePath(String oldUser,String newUser)
  {
    /*邮件里面记录附件绝对路径，修改文件后修改目录导致旧邮件正文附件无法找到，不在修改目录，建立新目录，拷贝配置文件到新目录
    File of=new File(localMailBasePath+mailDir+oldUser);
    File nf=new File(localMailBasePath+mailDir+newUser);
    if(of.exists() && of.renameTo(nf)==false) {return false;}
    of=new File(localMailBasePath+attachmentDir+oldUser);
    nf=new File(localMailBasePath+attachmentDir+newUser);
    if(of.exists() && of.renameTo(nf)==false) {return false;}
    */
    String oldMailPath=localMailBasePath+mailDir+oldUser+"/";
    File of=new File(oldMailPath+mailCfgFile);
    //如果不存在邮件设置
    if(of.exists()==false){return true;}
    String newMailPath=localMailBasePath+mailDir+newUser+"/";
    File nf=new File(newMailPath);
    if(nf.exists()==false)
    {
      if(nf.mkdirs()==false){return false;}
    }
    boolean isOk=false;
    File tempFile=null;
    try{
      tempFile=new File(oldMailPath+mailCfgFile);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+mailCfgFile, newMailPath+mailCfgFile);}
      tempFile=new File(oldMailPath+inFileName);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+inFileName, newMailPath+inFileName);}
      tempFile=new File(oldMailPath+mailSendFile);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+mailSendFile, newMailPath+mailSendFile);}
      tempFile=new File(oldMailPath+mailCurFile);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+mailCurFile, newMailPath+mailCurFile);}
      tempFile=new File(oldMailPath+mailDraftFile);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+mailDraftFile, newMailPath+mailDraftFile);}
      tempFile=new File(oldMailPath+mailTempFile);
      if(tempFile.exists()==true){FileUtil.copy(oldMailPath+mailTempFile, newMailPath+mailTempFile);}
    }catch(Exception e)
    {
      return false;
    }
    tempFile=new File(oldMailPath+mailCfgFile);tempFile.delete();
    tempFile=new File(oldMailPath+inFileName);tempFile.delete();
    tempFile=new File(oldMailPath+mailSendFile);tempFile.delete();
    tempFile=new File(oldMailPath+mailCurFile);tempFile.delete();
    tempFile=new File(oldMailPath+mailDraftFile);tempFile.delete();
    tempFile=new File(oldMailPath+mailTempFile);tempFile.delete();
    return true;
  }
  /**
   * 调整邮件附件存放路径;邮件的正文附件是以文件形式存储,记录文件的绝对路径;
   * 路径生成规则是OA安装路径加attachmentDir,加用户名称;当OA安装路径变化后,
   * 找不到原来邮件的附件;通过该函数调整旧存放邮件的存储路径;
   * @param attPath
   * @return
   */
  public static String adjustAffixPath(String attPath)
  {
    String nf=localMailBasePath+attachmentDir;
    int i,iPos=attPath.indexOf(attachmentDir);
    if(iPos==-1){return attPath;}
    nf+=attPath.substring(iPos+attachmentDir.length());
    return nf;
  }
  public static void main(String[] args) {
    /*
    LocalMailCfg localMailCfg1 = new LocalMailCfg();
    net.btdz.oa.common.TDebugOutMsg.outMsg("updPath:"+Standard.getHandel().getUploadPath());
    net.btdz.oa.common.TDebugOutMsg.outMsg("updPath:"+System.getProperty("tomcat.home"));
    net.btdz.oa.common.TDebugOutMsg.outMsg(System14.ReplaceStr("c:\\zhangh\\upld\\","\\","/"));
    */
   //net.btdz.oa.common.TDebugOutMsg.outMsg(LocalMailCfg.adjustAffixPath("c:/uploadOld/~attachment/zhangh/aa.txt"));
   }

	public static String getMailDir() {
		return mailDir;
	}
	
	public static void setMailDir(String mailDir) {
		LocalMailCfg.mailDir = mailDir;
	}

	public static String getAttachmentDir() {
		return attachmentDir;
	}

	public static void setAttachmentDir(String attachmentDir) {
		LocalMailCfg.attachmentDir = attachmentDir;
	}

}