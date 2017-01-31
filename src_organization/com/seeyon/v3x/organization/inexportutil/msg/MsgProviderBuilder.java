package com.seeyon.v3x.organization.inexportutil.msg;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

public class MsgProviderBuilder {
//	op
	public static final String ORG_IO_MSG_OP_OK="成功";
	public static final String ORG_IO_MSG_OP_FAILED="失败";
	public static final String ORG_IO_MSG_OP_IGNORED="跳过";
	
	//name
	public static final String ORG_IO_MSG_NAME_REG="注册";
	public static final String ORG_IO_MSG_NAME_LOGINNAME="登录名";
	
	//alert
	public static final String ORG_IO_MSG_ALERT_INACCOUNT="已经在公司";
	public static final String ORG_IO_MSG_ALERT_INOTHERACCOUNT="已经在其他公司";
	public static final String ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT
	                                ="不属于当前单位";
	public static final String ORG_IO_MSG_ALERT_IGNORED4DOUBLE="重复项，跳过";
	
	//ok
	public static final String ORG_IO_MSG_OK_ADD="添加成功";
	public static final String ORG_IO_MSG_OK_UPDATE="更新成功";
	
	//error
	public static final String ORG_IO_MSG_ERROR_EXCEPTION="底层异常：";
	public static final String ORG_IO_MSG_ERROR_FILEDATA="导入文件数据结构错误";
	
	public static final String ORG_IO_MSG_ERROR_MUST_ACCOUNT="请指定所属单位";
	
	public static final String ORG_IO_MSG_ERROR_MUST_DEP="请指定所属部门";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_DEP="无法匹配指定的部门";
	
	public static final String ORG_IO_MSG_ERROR_MUST_LEV="请指定职务级别";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_LEV="无法匹配指定的职务级别";
	
	public static final String ORG_IO_MSG_ERROR_MUST_PPOST="请指定主岗";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_PPOST="无法匹配指定的主岗";
	public static final String ORG_IO_MSG_ERROR_MUST_POSTNAME="岗位名称不能为空";
	public static final String ORG_IO_MSG_ERROR_MUST_POSTTYPE="必须指定岗位类别";
	public static final String ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE="无法匹配指定的岗位类别";
	public static final String ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME
	                                    ="导入文件中已包含同名岗位信息，岗位名：";

	public static final String ORG_IO_MSG_ERROR_MUST_MEMBERNAME="姓名不能为空";
	public static final String ORG_IO_MSG_ERROR_MUST_LOGINNAME
	                                    ="登录名不能为空";
	public static final String ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME
                                        ="导入文件中已经包含相同登录名的人员信息  登录名：";
	
	private static final Map<String,String>  msgs=new HashMap<String,String>();
	
	static{
		msgs.put(MsgContants.ORG_IO_MSG_OP_OK, ORG_IO_MSG_OP_OK);
		msgs.put(MsgContants.ORG_IO_MSG_OP_FAILED, ORG_IO_MSG_OP_FAILED);
		msgs.put(MsgContants.ORG_IO_MSG_OP_IGNORED, ORG_IO_MSG_OP_IGNORED);
		
		msgs.put(MsgContants.ORG_IO_MSG_NAME_REG, ORG_IO_MSG_NAME_REG);
		msgs.put(MsgContants.ORG_IO_MSG_NAME_LOGINNAME, ORG_IO_MSG_NAME_LOGINNAME);
		
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_INACCOUNT, ORG_IO_MSG_ALERT_INACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_INOTHERACCOUNT, ORG_IO_MSG_ALERT_INOTHERACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT, ORG_IO_MSG_ALERT_NOBELONGCURRENTACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ALERT_IGNORED4DOUBLE, ORG_IO_MSG_ALERT_IGNORED4DOUBLE);
		
		msgs.put(MsgContants.ORG_IO_MSG_OK_ADD, ORG_IO_MSG_OK_ADD);
		msgs.put(MsgContants.ORG_IO_MSG_OK_UPDATE, ORG_IO_MSG_OK_UPDATE);
		
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_EXCEPTION, ORG_IO_MSG_ERROR_EXCEPTION);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_FILEDATA, ORG_IO_MSG_ERROR_FILEDATA);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_ACCOUNT, ORG_IO_MSG_ERROR_MUST_ACCOUNT);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_DEP, ORG_IO_MSG_ERROR_MUST_DEP);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_DEP, ORG_IO_MSG_ERROR_NOMATCH_DEP);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_LEV, ORG_IO_MSG_ERROR_MUST_LEV);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_LEV, ORG_IO_MSG_ERROR_NOMATCH_LEV);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_PPOST, ORG_IO_MSG_ERROR_MUST_PPOST);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_PPOST, ORG_IO_MSG_ERROR_NOMATCH_PPOST);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_POSTTYPE, ORG_IO_MSG_ERROR_MUST_POSTTYPE);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE, ORG_IO_MSG_ERROR_NOMATCH_POSTTYPE);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME, ORG_IO_MSG_ERROR_DOUBLESAMEFILE_POSTNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_MEMBERNAME, ORG_IO_MSG_ERROR_MUST_MEMBERNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_MUST_LOGINNAME, ORG_IO_MSG_ERROR_MUST_LOGINNAME);
		msgs.put(MsgContants.ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME, ORG_IO_MSG_ERROR_DOUBLESAMEFILE_LOGINNAME);
	}
	
	static String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
	
	static MsgProviderBuilder mpb=new MsgProviderBuilder();
	
	static public MsgProviderBuilder getInstance(){
		return mpb;
	}
	
	public MsgProvider createMsgProvider(){
		return new MapMsgProvider();
	}
	public MsgProvider createMsgProvider(Locale local){
		if(local==null)
			return createMsgProvider();
		
		OrgResourceBundleMsgProvider rbmp=new OrgResourceBundleMsgProvider();
		rbmp.setLocal(local);
		
		return rbmp;
	}
	public MsgProvider createMsgProvider(String res,Locale local){
		if(StringUtils.hasText(res)){
			ResourceBundleMsgProvider rbmp=new ResourceBundleMsgProvider();
			rbmp.setRes(res);
			
			return rbmp;
		}
		
		return createMsgProvider(local);
	}
	
	public class MapMsgProvider implements MsgProvider{
		
		public String getMsg(String key){
			return msgs.get(key);
		}
	}
	
	public class OrgResourceBundleMsgProvider  implements MsgProvider{
		protected Locale local;
		
		public String getMsg(String key){
			String ret=null;
			try{
				ret= ResourceBundleUtil.getString(this.getResource(), local, key);
			}catch(Exception e){
				
			}
			if(null==ret){
				ret= msgs.get(key);
			}
			
			return ret;
		}

		public Locale getLocal() {
			return local;
		}
		public void setLocal(Locale local) {
			this.local = local;
		}

		public String getResource() {
			return resource;
		}
		
	}
	
	public class ResourceBundleMsgProvider  extends  OrgResourceBundleMsgProvider{
		private String res;

		public String getResource() {
			return this.res;
		}
		public String getRes() {
			return this.res;
		}
		public void setRes(String val) {
			this.res = val;
		}
		
	}
}//end class
