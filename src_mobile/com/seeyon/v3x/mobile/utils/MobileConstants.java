package com.seeyon.v3x.mobile.utils;


import java.util.regex.Pattern;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.Strings;

public class MobileConstants {
	
	public static final int MY_PENDING_AFFAIR = 5;

	public static final int MY_TRACK = 2;

	public static final int MY_MEETING = 3;

	public static  int PAGE_COUNTER = 8;
	
	
	public static int PAGE_COUNT = 500;
	
	//这些是谭未增加的
	public static final int WAIT_SEND_STATE  = 1;
	
	public static final int SEND_STATE  = 2;
	
	public static final int PENDING_STATE  = 3;

	public static final int DONE_STATE = 4;
	
	public static final int DISPLAY_PAGE_NUMBER = 230;
	
	public static final int OPINION_NUMBER = 5;
	
	//监听器的延迟时间
	public static  long DELAY_TIME;
	
	//监听器的间隔时间
	public static  long SPACING_TIME ;
	
	//用在意见分页
	public static final int BEGINNUM = 0;
	
	public static final int ENDNUM = 7;
	
	public static final int FLOWCHART_1 =2;
	
	public static final int FLOWCHART_2 =3;
	
	public static final String CONCURRENCY_RELATION="c";
	
	public static final String STRING_RELATION="s";
	
	public static  String SERVER_IP_MESSAGE ;
	
	public static String SERVER_IP_WAPPUSH;
	
	public static int SMS_PORT;
	
	public static int WAPPUSH_PORT  ;
	
	public static String MARK_WAPPUSH = "/wappush";
	
	public static String MARK_SMS = "/sms";
	//所有宽屏的jsp页面所在的目录
	public static String MOBILE = "mobile";
	
	public static boolean EDOC ;
	
	public static int CAT;
	
	public static String htmlSuffix;
	
	public static Long supMaxSizeFile = 1024l;
    /*
     * 移动提示信息的key定义
     */
    /**
     * 未选择节点处理人
     */
    public static final String CAPTION_UnselectedNodeTransactor = "common.caption.UnselectedNodeTransactor";
    
    
    
	public static Long getCurrentDepartmentId() {
		return CurrentUser.get().getDepartmentId();
	}
	
	public static Long getCurrentAccountId(){
		return CurrentUser.get().getAccountId();
	}

	 public static Long getCurrentId(){
		return CurrentUser.get().getId();
	}
	
	public static Long getCurrentLevel(){
		return CurrentUser.get().getLevelId();
	}
    
	private static final String resource_mobile = "com.seeyon.v3x.mobile.resources.i18n.MobileResources";
    /**
     * 取得MobileResource国际化资源的值
     * @param key
     * @param parameters
     * @return
     */
    public static String getValueFromMobileRes(String key,String...parameter) {
    	if(Strings.isNotBlank(key)){
    		return ResourceBundleUtil.getString(resource_mobile, key,parameter);
    	}else{
    		return "";
    	}
    }
    
    public static boolean validateSuffix(Attachment attachment){
    	if(attachment == null )return false;
    	//判断附件大小，如果超过两M ，是坚决不能查看的。
    	if(attachment.getSize() > supMaxSizeFile && attachment.getType() != Constants.ATTACHMENT_TYPE.FILE.ordinal())
    		return false;
    	String fileName = attachment.getFilename();
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		fileSuffix = fileSuffix.toLowerCase();
		return Pattern.matches(MobileConstants.htmlSuffix, fileSuffix);
    }
    
    public static enum ATTACHMENT_TYPE {
		FILE, //本地上传的文件，将在附件区显示
		IMAGE, //正文中的图片，不在附件区显示
		DOCUMENT, //关联文档
	}
}