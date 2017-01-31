package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description: 文件目录操作</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;
import java.util.zip.*;
import com.seeyon.v3x.webmail.manager.LocalMailCfg;

public class FileUtil {
  private static boolean disMsg=false;
  public FileUtil() {
  }
  private static void outMsg(String msg)
  {
    if(disMsg) {
    	//System.out.println(msg);
    }
  }
  /**
   * 删除目录或者文件（不管目录是否为空）
   * @param path
   * @return
   */
  public static boolean delDirectory(String path)
  {
    File file=new File(path);
    if(file.exists())
    {
      if(file.isDirectory())
      {
        File [] files =file.listFiles();
        for(int i=0;i<files.length;i++)
        {
          if(delDirectory(files[i].getPath())==false) return false;
        }
        return file.delete();
      }
      else
      {
        return file.delete();
      }
    }
    return true;
  }
  /**
   * 把修改目录中文件的扩展名
   * @param path
   * @param extName
   * @return
   */
  public static boolean setFileExtName(String path,String extName)
  {
    File f=new File(path);
    if(f.exists() && f.isDirectory())
    {
      File tf=null;
      File files[]=f.listFiles();
      for(int i=0;i<files.length;i++)
      {
        if(files[i].exists() && files[i].isFile()&&files[i].getName().indexOf(".eml")==-1)
        {
          tf=new File(files[i].getPath()+"."+extName);
          files[i].renameTo(tf);
        }
      }
    }
    return true;
  }
  /**
   * 压缩目录下的文件
   * @param zipFileName
   * @param inputFile 目录
   * @throws java.lang.Exception
   */
  public static void zip(String zipFileName, String inputFile) throws Exception {
    zip(zipFileName, new File(inputFile));
  }

  /**
   * 压缩文件
   * @param zipFileName
   * @param inputFile 文件
   * @throws java.lang.Exception
   */
  public static void zip(String zipFileName, File inputFile) throws Exception {
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
    zip(out, inputFile, "");
    outMsg("压缩成功!");
    out.close();
  }

  private static void zip(ZipOutputStream out, File f, String base) throws Exception {
    outMsg("正在压缩  " + f.getName());
    if (f.isDirectory()) {
      File[] fl = f.listFiles();
      out.putNextEntry(new ZipEntry(base + "/"));
      base = base.length() == 0 ? "" : base + "/";
      for (int i = 0; i < fl.length; i++) {
        zip(out, fl[i], base + fl[i].getName());
      }
    }
    else {
      out.putNextEntry(new ZipEntry(base));
      FileInputStream in = new FileInputStream(f);
      int b;
      while ( (b = in.read()) != -1)
        out.write(b);
      in.close();
    }

  }
  /**
   * 解压缩
   * @param zipFileName
   * @param outputDirectory 输出目录
   * @throws java.lang.Exception
   */
  public void unzip(String zipFileName, String outputDirectory) throws
      Exception {
    ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
    ZipEntry z;
    while ( (z = in.getNextEntry()) != null) {
      outMsg("正在解压 " + z.getName());
      if (z.isDirectory()) {
        String name = z.getName();
        name = name.substring(0, name.length() - 1);
        File f = new File(outputDirectory + File.separator + name);
        f.mkdir();
        outMsg("创建目录 " + outputDirectory + File.separator + name);
      }
      else {
        File f = new File(outputDirectory + File.separator + z.getName());
        f.createNewFile();
        FileOutputStream out = new FileOutputStream(f);
        int b;
        while ( (b = in.read()) != -1)
          out.write(b);
        out.close();
      }
    }
    in.close();
  }
  public static void copy(String inFileName,String outFileName) throws Exception
  {
    File in=new File(inFileName);
    File out=new File(outFileName);
    copy(in,out);
  }
  public static void copy(File in,File out) throws Exception
  {
    int readNum=0;
    byte [] data=new byte[1024*128];
    FileInputStream fis=new FileInputStream(in);
    FileOutputStream fos=new FileOutputStream(out);
    while((readNum=fis.read(data))!=-1)
    {
      fos.write(data,0,readNum);
    }
    fos.flush();
    fos.close();
    fis.close();
  }
  /**
   * 移动文件
   * @param fromPathName:移动文件的完整路径（路径加文件名称）
   * @param toPath：移动到的路径
   * @return
   * @throws java.lang.Exception
   */
  public static boolean moveFile(String fromPathName,String toPath) throws Exception
  {
    String toPathName=toPath;
    File fs=new File(fromPathName);
    toPathName+=fs.getName();
    File fd=new File(toPathName);
    if(fs.getPath().equals(fd.getPath())){return true;}
    if(fs.isFile()==false){return false;}
    //原文件可以写，不可以删除，线程等待
    //start modify by liusg
    String path1 = LocalMailCfg.getMailBasePath() + LocalMailCfg.getMailDir();
    String path2 = LocalMailCfg.getMailBasePath() + LocalMailCfg.getAttachmentDir();
    if(!(fromPathName.startsWith(path1) || fromPathName.startsWith(path2)))
    {
    	copy(fs, fd);
    	return true;
    }
    //end modify by liusg
    return fs.renameTo(fd);
  }
  public static boolean isFile(String filePath)
  {
    File f=new File(filePath);
    if(f.isFile() && f.exists()){return true;}
    else {return false;}
  }
  public static void main(String[] args) {
    FileUtil fileUtil1 = new FileUtil();
    try{
      //FileUtil.zip("c:/upload/zhangh.zip", "c:/upload/zhangh/");
      FileUtil.setFileExtName("C:\\upload\\zhangh\\","eml");
    }catch(Exception e)
    {
      System.out.println("ERR:"+e.getMessage());
    }
  }

}