package com.seeyon.v3x.videoconference.util;

public abstract class Constants {
	
	/**
	 * 提前参会时间
	 */
	public static final String BEFORE_HAND_TIME = "120";
	/**
	 * 会议类型,视频会议
	 */
	public static final String VIDEO_MEETING_TITLE="[视频会议]";
	/**
	 * 会议类型,视频会议
	 */
	public static final String VIDEO_MEETING="2";
	
	/**
	 * 会议类型,普通会议
	 */
	public static final String ORID_MEETING="1";
	/**
	 * 尚未发送，也就是暂存
	 */
	public static final int DATA_STATE_SAVE=0;
	/**
	 * 已经发送，但未开始
	 */
	public static final int DATA_STATE_SEND=10;
	/**
	 * 已经发送，但未开始
	 */
	public static final int DATA_STATE_WILL_START=15;
	/**
	 * 会议已经开始
	 */
	public static final int DATA_STATE_START=20;
	/**
	 * 会议已经结束
	 */
	public static final int DATA_STATE_FINISH=30;
	/**
	 * 会议已经总结
	 */
	public static final int DATA_STATE_SUMMARY=40;
	/**
	 * 已经归档
	 */	
	public static final int DATA_STATE_PIGEONHOLE=-10;      //12-17 修改为-10   为了查询的时候少加个过滤条件  100;
	
	//视频会议服务器链接
	public static String webBaseUrl = "";
	//视频会议服务器状态标示位
	public static String videoConfStatus = "";
	//视频会议服务器状态标示位
	public static String videoConfPoints = "";
	//心跳接口间隔检测时间
	public static final long period = 600000;
    //xml为空异常
    public static final String XML_NULL_ERROR = "XML_Error_10001";
    //未知错误
    public static final String UNKNOW_NULL_ERROR = "0x9999999";
    //无效的site和service信息
    public static final String INVALID_SITE_SERVICE_ERROR = "0x0000001";
    //无效的版本或者action信息
    public static final String INVALID_SERVICE_VERSION_OR_ACTION_ERROR = "0x0000006";
    //无效得xml格式
    public static final String INVALID_XML_STYLE_ERROR = "0x0000007";
    //没有相关用户名或密码。
    public static final String INVALID_USERNAME_OR_PASSWORD_OR_SITENAME_ERROR = "0x0000008";
    //需要用户名和密码
    public static final String REQUIRE_USERNAME_AND_PASSWORD_ERROR = "0x0000009";
    //字符集编码错误
    public static final String CHARACTER_ENCODING_ERROR = "0x00000010";
    //服务异常
    public static final String SERVICE_EXCEPTION_ERROR = "0x0600000";
    //没有权限异常
    public static final String NO_PERMISSION_ERROR = "0x0600002";
    //用户密码无效异常
    public static final String USER_PASSWORD_INVALID_ERROR = "0x0600010";
    //用户不存在异常
    public static final String USER_NOT_EXIST_ERROR = "0x0600011";
    //用户邮箱无效异常
    public static final String USER_EMAIL_INVALID_ERROR  = "0x0600012";
    //最大数量为空或者为0异常
    public static final String MAX_AMOUNT_NULL_OR_ZERO_ERROR  = "0x0600013";
    //用户电话无效异常
    public static final String USER_PHONE_INVALID_ERROR  = "0x0600014";
    //用户移动电话无效异常
    public static final String USER_MOBILEPHONE_INVALID_ERROR  = "0x0600015";
    //会议对象为空异常
    public static final String CONFERENCE_OBJ_NULL_ERROR  = "0x0600001";
    //站点不存在异常
    public static final String SITE_NOT_EXIST_ERROR  = "0x0600081";
    //站点未启用异常
    public static final String SITE_NOT_DISABLE_ERROR  = "0x0600082";
    //站点无效异常
    public static final String SITE_INACTIVE_ERROR  = "0x0600083";
    //站点无服务异常
    public static final String SITE_SERVICE_DELETE_ERROR  = "0x0600084";
    //会议ID为空异常
    public static final String CONFERENCE_ID_NULL_ERROR  = "0x0601001";	 
    //会议KEY为空异常
    public static final String CONFERENCE_KEY_NULL_ERROR  = "0x0601003";
    //会议主题为空异常
    public static final String CONFERENCE_SUBJECT_NULL_ERROR  = "0x0601004";
    //会议开始时间为空异常
    public static final String CONFERENCE_START_TIME_NULL_ERROR  = "0x0601005";
    //会议期间时间为空异常
    public static final String CONFERENCE_DURING_TIME_NULL_ERROR  = "0x0601006";
    //会议模式为空异常
    public static final String CONFERENCE_OPENTYPE_NULL_ERROR  = "0x0601007"; 
    //会议主持人或者创建者ID为空异常
    public static final String CONFERENCE_HOST_OR_CREATEID_NULL_ERROR  = "0x0601008"; 
    //会议密码为空异常
    public static final String CONFERENCE_PASSWORD_NULL_ERROR  = "0x0601009"; 
    //提前加会时间为空
    public static final String CONFERENCE_JOIN_BEFORE_HOST_TIME_NULL_ERROR  = "0x0601010"; 
    //会议注册结束时间为空
    public static final String CONFERENCE_REG_END_TIME_NULL_ERROR  = "0x0601011"; 
    //数量检查超过预置异常
    public static final String AMOUNT_CHECK_EXCEED_ESTIMATE_ERROR  = "0x0601012"; 
    //参会人员超过点数异常
    public static final String ATTENDEE_EXCEED_AMOUNT_ERROR  = "0x0601013"; 
    //参会人员检查会场数量异常
    public static final String ATTENDEE_CHECK_PLACE_AMOUNT_ERROR  = "0x0601014"; 
    //数量资源冲突异常
    public static final String AMOUNT_RESOURCE_CONFLICT_ERROR  = "0x0601015"; 
     //地点资源冲突异常
    public static final String PLACE_RESOURCE_CONFLICT_ERROR  = "0x0601016"; 
    //会议启动时间过早异常
    public static final String STARTTIME_EARLY_THAN_CURRENT_ERROR  = "0x0601017"; 
    //会议主题无效异常
    public static final String CONFERENCE_SUBJECT_INVALID_ERROR  = "0x0601018"; 
    //会议参会顺序错误异常
    public static final String JOIN_BEF_HOST_ERROR  = "0x0601019"; 
    //与会者电子邮件重复异常
    public static final String ATTENDEE_EMAIL_DUPLICATED_ERROR  = "0x0601020"; 
    //用户账户余额不足异常
    public static final String USER_ACCOUTN_BALANCE_SHORTAGE_ERROR  = "0x0601021"; 
    //计费系统异常异常
    public static final String BILLING_SYSTEM_ERROR  = "0x0601022"; 
    //用户每月费用扣除失败异常
    public static final String USER_FEE_DEDUCTION_ERROR  = "0x0601023"; 
    //与会者电子邮件不允许异常
    public static final String ATTENDEE_EMAIL_NOT_ALLOW_ERROR  = "0x0601024"; 
    //开启会议的会议ID和conkey不允许异常
    public static final String STARTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR  = "0x0601025"; 
    //参加会议的会议ID和conkey不允许异常
    public static final String JOINTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR  = "0x0601026";
    //传入会议时间格式错误异常
    public static final String MEETING_DATE_FORMAT_WRONG_ERROR  = "0x0601027";
    //传入会议xml头信息格式错误异常
    public static final String MEETING_XML_HEAD_NULL_ERROR  = "0x0601028";
    //传入会议xmlEMAIL信息异常
    public static final String ATTENDEE_EMAIL_PARAMTER_NULL_ERROR  = "0x0601029";
    //传入会议xml参数信息错误异常
    public static final String MEETING_XML_PARAMTER_ERROR  = "0x0601030";
    //传入会议xml参数信息错误异常
    public static final String MEETING_XML_PARAMTER_ERROR2  = "0x0601031";
    //重复类型主键不存在异常
    public static final String REPEAT_KEY_NULL_ERROR  = "0x0601032";
    //需要每天重复类型的值[0-9]，最大长度为3
    public static final String DALIY_REPEAT_TYPE_ERROR  = "0x0601033";
    //每周重复类型的值需要长度为7[1|0]字符串
    public static final String WEEKLY_REPEAT_TYPE_ERROR  = "0x0601034";
    //每月重复类型的值要求小于32的整数
    public static final String MONTHLY_REPEAT_TYPE_ERROR  = "0x0601035";
    //startHourMinute和endHourMinute格式错误异常
    public static final String STARTHOUTMINUTE_AND_ENDHOUTMINUTE_FORMAT_ERROR  = "0x0601036";
    //结束时间早于开始时间异常
    public static final String STARTTIME_EARLY_THAN_ENDTIME_ERROR  = "0x0601037";	
    //与会者数额不为正整数异常
    public static final String ATTENDEE_AMOUNT_INTEGER_ERROR  = "0x0601038";		
    //重复范围的日期参数格式不正确异常
    public static final String REPART_RANGEDATE_FORMAT_ERROR  = "0x0601039";		
    //会议密码为空异常
    public static final String MEETINGPWD_NULL_ERROR  = "0x0601040";	
    //EMAIL为空异常
    public static final String 	EMAIL_NULL_ERROR  = "0x0601041";
    //webBaseUrl为空异常
    public static final String 	WEBBASEURL_NULL_ERROR  = "0x0601042";
    //attendeeName为空异常
    public static final String 	ATTENDEENAME_NULL_ERROR  = "0x0601043";
    //hostName为空异常
    public static final String 	HOSTNAME_NULL_ERROR  = "0x0601044";
    //confKey为空异常
    public static final String 	CONFKEY_NULL_ERROR  = "0x0601045";
    //请求消息体为空异常
    public static final String 	REQUEST_BODY_NULL_ERROR  = "0x0601046";
    //EMAIL形式错误异常
    public static final String 	EMAIL_SHARP_ERROR  = "0x0601047";
    //主持人不存在异常
    public static final String 	HOST_NOT_EXIST_ERROR  = "0x0601048";
    //会议超级管理员配置为空异常
    public static final String 	SUPER_ADMIN_CONFIG_NULL_ERROR  = "0x0602001";
    //会议站点管理员配置为空异常
    public static final String 	SITE_ADMIN_CONFIG_NULL_ERROR  = "0x0602002";
    //会议现场配置为空异常
    public static final String 	CONFERENCE_PLACE_CONFIG_NULL_ERROR  = "0x0602003";
    //会议现场类型为空异常
    public static final String 	CONFERENCE_PLACE_TYPE_NULL_ERROR  = "0x0602004";
    //会议超级管理员配置总数为空异常
    public static final String 	SUPER_ADMIN_CONFIG_TOTALNUM_NULL_ERROR  = "0x0602005";
    //会议超级管理员配置地点类型列表为空异常
    public static final String 	SUPER_ADMIN_CONFIG_PLACE_TYPE_LIST_NULL_ERROR  = "0x0602006";
    //会议不允许删除异常
    public static final String 	CONFERENCE_NOT_DELETE_ERROR  = "0x0603001";
    //会议信息不允许更新异常
    public static final String 	CONFERENCE_INFO_NOT_UPDATE_ERROR  = "0x0603002";
    //会议状态不允许开始异常
    public static final String 	CONFERENCE_NOT_START_ERROR  = "0x0603003";
    //会议状态不允许参会异常
    public static final String 	CONFERENCE_NOT_JOIN_ERROR  = "0x0603004";
    //参会失败异常
    public static final String 	JOIN_FAIL_ERROR  = "0x0604001";
    //与会人员为空异常
    public static final String 	ATTENDEE_NAME_NULL_ERROR  = "0x0604002";
    //未登录异常
    public static final String 	NOT_LOGIN_ERROR  = "0x0604003";
    //主持人未登录异常
    public static final String 	HOST_NOT_LOGIN_ERROR  = "0x0604004";
    //当前用户不是主持人异常
    public static final String 	CURRENTUSER_NOT_HOST_ERROR  = "0x0604005";
    //会议密码错误异常
    public static final String 	CONFERENCE_PASSWORD_ERROR  = "0x0604006";
    //会议地点不可见异常
    public static final String 	CONFERENCE_PLACE_NOT_AVAILABLE_ERROR  = "0x0604007";
    //参会顺序错误异常
    public static final String 	NOT_JOIN_BEF_HOST_ERROR  = "0x0604008";
    //参会结束异常
    public static final String  CONFERENCE_OVER_ERROR  = "0x0604009";
    //参会上锁异常
    public static final String  CONFERENCE_LOCK_ERROR  = "0x0604010";
    //{0}字符串为空异常
    public static final String  STRING_BULL_ERROR  = "0x0901000";
    //查询结果错误异常
    public static final String  QUERY_RESULT_ERROR  = "0x0901001";
    //远程培训对象为空异常
    public static final String  REMOTE_TRAINING_OBJECT_NULL_ERROR  = "0x0902050";
    //MainTrainingInfo验证码不匹配异常
    public static final String  MAINTRAININFO_CODE_NOT_MATCH_ERROR  = "0x0902058";
    //会议令牌加密失败异常
    public static final String  CONF_TOKEN_CRYPTO_FAIL_ERROR  = "0x0902054";
    //用户保存失败异常
    public static final String  USER_SAVE_FAIL_ERROR  = "0x0902060";
    //用户查询失败异常
    public static final String  USER_QUERY_FAIL_ERROR  = "0x0902061";
    //用户检查失败异常
    public static final String  USER_CHECK_FAIL_ERROR  = "0x0902062";
    //远程服务器为空异常
    public static final String  ROMOTE_SERVER_NULL_ERROR  = "0x0902063";
    //远程用户为空异常
    public static final String  ROMOTE_USER_NULL_ERROR  = "0x0902064";
    //服务版本号为空异常
    public static final String  SERVICE_VERSIONID_NULL_ERROR  = "0x0902065";
    //站点ID为空异常
    public static final String  SITE_ID_NULL_ERROR  = "0x0903c01";
    //用户名已存在异常
    public static final String  USERNAME_IS_EXIST_ERROR  = "0x0302001";
    //用户email已存在异常
    public static final String  USERNAME_EMAIL_EXIST_ERROR  = "0x0302002";
    //站点名称为空异常
    public static final String  SITENAME_NULL_ERROR  = "0x0903c00";
    //检测未通过异常
    public static final String  NO_PERMISSION_ERROR2  = "0x0903c02";
    //training.webapp.schedule.invitor.session.key.isnull异常
    public static final String  SESSION_KEY_NULL_ERROR  = "0x0903001";
    //raining.webapp.schedule.noperm异常
    public static final String  SCHEDULE_NOT_PERM_ERROR  = "0x0903002";
    //training.webapp.schedule.session.maintrininginfo.null异常
    public static final String  MAINTRAININGINFO_NULL_ERROR  = "0x0903003";
    //training.webapp.schedule.session.maintrininginfo.null异常
    public static final String  MAINTRAININGINFO_NULL_ERROR2  = "0x0903004";
    //会议密码
    public static final String  MEETING_PWD  = "123456";
    //会议类型
    public static final String  MEETING_OPEN_TYPE  = "true";
    //会议模式
    public static final String  CONFERENCE_PATTERN  = "0";
    //未知错误
    public static final String  UNKNOW_ERROR  = "UnkownError";
	//第三方系统标识
    public static final String INFOWARE = "InfoWareLab";
    //用户名为空异常
    public static final String USERNAME_NULL_ERROR = "Null_Error_00001"; 
    //用户密码为空异常
    public static final String PASSWORD_NULL_ERROR = "Null_Error_00002"; 
    //会议主题为空异常
    public static final String SUBJECT_NULL_ERROR = "Null_Error_00003"; 
    //开始时间为空异常
    public static final String STARTTIME_NULL_ERROR = "Null_Error_00004"; 
    //开始时间格式错误异常
    public static final String STARTTIME_FORMAT_FAULT_ERROR = "Format_Fault_Error_00005"; 
    //结束时间为空异常
    public static final String ENDTIME_NULL_ERROR = "Null_Error_00006"; 
    //结束时间格式错误异常
    public static final String ENDTIME_FORMAT_FAULT_ERROR = "Format_Fault_Error_00007"; 
    //时区为空异常
    public static final String TIMEZONEID_NULL_ERROR = "Null_Error_00008"; 
    //会产点数为空异常
    public static final String ATTENDEEAMOUNT_NULL_ERROR = "Null_Error_00009"; 
    //会议创建者为空异常
    public static final String CREATER_NULL_ERROR = "Null_Error_00010";
    //会议类型为空异常
    public static final String OPENTYPE_NULL_ERROR = "Null_Error_00012";
    //会议密码为空异常
    public static final String PASSWD_NULL_ERROR = "Null_Error_00013";
    //会议模式为空异常
    public static final String CONFERENCEPATTERN_NULL_ERROR = "Null_Error_00014";
    //会议描述为空异常
    public static final String AGENDA_NULL_ERROR = "Null_Error_00015";
    //email語言为空异常
    public static final String MAILTEMPLATELOCAL_NULL_ERROR = "Null_Error_00016";
    //与会人员為空異常
    public static final String ATTENDEES_NULL_ERROR = "Null_Error_00018";
    //第三方视频会议插件判断為空異常
    public static final String VENDOR_NULL_ERROR = "Null_Error_00019";
    //第三方视频会议系统匹配错误异常
    public static final String VENDOR_MATCH_ERROR = "Match_Error_00021";
    //与会人员人员名称为空异常
    public static final String NAME_NULL_ERROR = "Name_Error_00022";
    //与会人员显示名称为空异常
    public static final String DISPLAYNAME_NULL_ERROR = "DisplayName_Error_00023";
	
    
}
