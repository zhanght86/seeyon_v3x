package com.seeyon.v3x.webmail.util;
import java.util.Vector;
public class System14 {

  public System14() {
  }

  private static int IndexOfTag(String aSource,int APos,String[] aTag)
  {
    int Result=-1;
    String ff;
    for (int i=0;i<aTag.length;i++)
    {
      ff=aSource.substring(APos,APos+aTag[i].length());
      if (ff.equals(aTag[i]))
        return i;
    }
    return Result;

  }

  public static String ReplaceStr(String aSource,String aTag,String aReplace){
    return ReplaceStr(aSource,new String[]{aTag},new String[]{aReplace});
  }

  /**
   * 谁写的函数？？？？？？写这么一大堆，还只能替换长度相同的字符串
   * @param aSource
   * @param aTag
   * @param aReplace
   * @return
   */
  public static String ReplaceStr(String aSource,String[] aTag,String[] aReplace)
  {
    if (aTag==null) throw new StringIndexOutOfBoundsException("标记字符串为空！");
    if (aReplace==null) throw new StringIndexOutOfBoundsException("目标字符串为空！");
    if (aTag.length!=aReplace.length)
       throw new StringIndexOutOfBoundsException("aTag 和 aReplace 的字符串个数不匹配！");

    StringBuffer Result= new StringBuffer((int)(aSource.length()*1.5));
    int fLen=aSource.length();
    int fIndex;
    int i=0;
    while (i<fLen)
    {
       fIndex=IndexOfTag(aSource,i,aTag);
       if (fIndex>=0)
       {
         Result.append(aReplace[fIndex]);
         i+=aTag[fIndex].length();
       }
       else
       {
         Result.append(aSource.charAt(i));
         i++;
       }
    }

    return Result.toString();
  }



  public static String[]  splitStr(String aSource,String aTag)
  {
    String[] Result;
    Vector v=new Vector();
    String[] tag=new String[1];
    tag[0]=aTag;
    int fLen=aSource.length();
    int fIndex,fCurrent=0;
    int i=0;
    while (i<fLen)
    {
      fIndex=IndexOfTag(aSource,i,tag);
      if (fIndex>=0)
      {
        v.add(aSource.substring(fCurrent,i));
        i+=tag[fIndex].length();
        fCurrent=i;
      }
      else
      {
        i++;
      }
    }
    if (fCurrent!=i)
      v.add(aSource.substring(fCurrent,i));

    Result=new String[v.size()];
    for (i=0;i<v.size();i++)
      Result[i]=(String)v.get(i);
    return Result;
  }
  /**
   * 删除重复的字符串
   * @param strSource
   * @param delStr
   * @return
   */
  public static String DelRepeatChar(String strSource,String delStr)
  {
    String szResult=null;
    String szTemp=delStr+delStr;
    while((szResult=replace(strSource,szTemp,delStr)).indexOf(szTemp)>=0);
    return szResult;
  }

  /**
   * 替换任意长度字符串
   * @param strSource
   * @param strFrom
   * @param strTo
   * @return
   */
  static public String replace(String strSource, String strFrom, String strTo) {
    if(strSource==null){return null;}
    StringBuffer szDest = new StringBuffer();
    //StringBuffer szSource=new StringBuffer(strSource);
    String szSource = strSource;
    int intFromLen = strFrom.length();
    int intPos;
    while ( (intPos = szSource.indexOf(strFrom)) != -1) {
      szDest.append(szSource.substring(0, intPos));
      szDest.append(strTo);
      //szSource.delete(0,intPos+intFromLen);
      szSource = szSource.substring(intPos + intFromLen);
    }
    szDest.append(szSource);
    return szDest.toString();
  }
  static public String FormatForJs(String szStr)
  {
    if(szStr==null){return "null";}
    String szJs=szStr;
    szJs=System14.replace(szJs,"\\","\\\\");
    szJs=System14.replace(szJs,"'","‘");
    szJs=System14.replace(szJs,"\"","“");
    szJs=System14.replace(szJs,"\r","\\r");
    szJs=System14.replace(szJs,"\n","\\n");
    return szJs;
  }
    public static void main(String[] args)  throws Exception {
      String str="c:\\programe\\nyyoa\\index\r\n33333";
      System.out.println(FormatForJs(str));
    }
}