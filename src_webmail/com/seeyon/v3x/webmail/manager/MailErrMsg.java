package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author zhangh
 * @version 1.0
 * 邮件发送错误的时候只能得到邮件SendFailedException异常,发送异常太多了无法提示
 * 但是从异常信息里面可以得到详细的错误信息,如验证错误,服务器设置错误等
 */

public class MailErrMsg {
  public MailErrMsg() {
  }
  static String formatErrMsg(String err)
  {
    String nStr="";
    if(err!=null)
    {
      if (err.indexOf("java.net.UnknownHostException") >= 0) {nStr="服务器设置错误";}
      else if (err.indexOf("Connection timed out") >= 0) {nStr="连接服务器超时,检查服务器设置是否正确";}//****修改号="20050607_0001"  区域="1"  *****************//
      else if (err.indexOf("javax.mail.AuthenticationFailedException") >= 0) {nStr="用户名或者密码设置错误";}
      else if (err.indexOf("too much recipient") >= 0){nStr="邮件接收人太多";}
      else{nStr+="("+err+")";}
    }
    return nStr;
  }
  public static void main(String[] args) {
    MailErrMsg mailErrMsg1 = new MailErrMsg();
  }

}