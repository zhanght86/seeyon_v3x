package com.seeyon.v3x.webmail.domain;

/**
 * <p>Title: </p>
 * <p>Description: 存储邮件查询的条件</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.Date;

public class SearchStruct {
  public int FolderType=-1;
  public String subject=null;
  public String from=null;
  public String to=null;
  public String dateType=null;
  public Date createDate=null;

  public SearchStruct() {
  }
  public static void main(String[] args) {
    SearchStruct searchStruct1 = new SearchStruct();
  }

}