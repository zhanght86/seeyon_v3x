/**
 * 检查异常类
 * @author radishlee
 * @since 2011-12-26
 */
package com.seeyon.v3x.videoconference.util;

import org.apache.commons.lang.StringUtils;


public class InfoWareExceptionCheck {
	
	/**
	 * @describe 检查第三方返回消息
	 * @author radishlee
	 * @since 2011-12-13
	 * @param result
	 * @return String类型返回值
	 */
	public static String checkInfoWareParams(String result){
		//如果会议参数返回失败
		if(StringUtils.contains(result,"FAILURE")){
			String exceptionID = (String)ParseXML.parseXML(result).get("exceptionID");
		   
			if(Constants.XML_NULL_ERROR.equals(exceptionID)){//xml为空异常
				return Constants.XML_NULL_ERROR;
			}else if(Constants.UNKNOW_NULL_ERROR.equals(exceptionID)){//未知错误
				return Constants.UNKNOW_NULL_ERROR;
			}else if(Constants.INVALID_SITE_SERVICE_ERROR.equals(exceptionID)){//无效的site和service信息
				return Constants.INVALID_SITE_SERVICE_ERROR;
			}else if(Constants.INVALID_SERVICE_VERSION_OR_ACTION_ERROR.equals(exceptionID)){  //无效的版本或者action信息
				return Constants.INVALID_SERVICE_VERSION_OR_ACTION_ERROR;
			}else if(Constants.INVALID_XML_STYLE_ERROR.equals(exceptionID)){//无效得xml格式
				return Constants.INVALID_XML_STYLE_ERROR;
			}else if(Constants.INVALID_USERNAME_OR_PASSWORD_OR_SITENAME_ERROR.equals(exceptionID)){   //没有相关用户名或密码。
				return Constants.INVALID_USERNAME_OR_PASSWORD_OR_SITENAME_ERROR;
			}else if(Constants.REQUIRE_USERNAME_AND_PASSWORD_ERROR.equals(exceptionID)){  //需要用户名和密码
				return Constants.REQUIRE_USERNAME_AND_PASSWORD_ERROR;
			}else if(Constants.CHARACTER_ENCODING_ERROR.equals(exceptionID)){//字符集编码错误
				return Constants.CHARACTER_ENCODING_ERROR;
			}else if(Constants.SERVICE_EXCEPTION_ERROR.equals(exceptionID)){//服务异常
				return Constants.SERVICE_EXCEPTION_ERROR;
			}else if(Constants.NO_PERMISSION_ERROR.equals(exceptionID)){  //没有权限异常
				return Constants.NO_PERMISSION_ERROR;
			}else if(Constants.USER_PASSWORD_INVALID_ERROR.equals(exceptionID)){ //用户密码无效异常
				return Constants.USER_PASSWORD_INVALID_ERROR;
			}else if(Constants.USER_NOT_EXIST_ERROR.equals(exceptionID)){//用户不存在异常
				return Constants.USER_NOT_EXIST_ERROR;
			}else if(Constants.USER_EMAIL_INVALID_ERROR.equals(exceptionID)){//用户邮箱无效异常
				return Constants.USER_EMAIL_INVALID_ERROR;
			}else if(Constants.MAX_AMOUNT_NULL_OR_ZERO_ERROR.equals(exceptionID)){  //最大数量为空或者为0异常
				return Constants.MAX_AMOUNT_NULL_OR_ZERO_ERROR;
			}else if(Constants.USER_PHONE_INVALID_ERROR.equals(exceptionID)){ //用户电话无效异常
				return Constants.USER_PHONE_INVALID_ERROR;
			}else if(Constants.USER_MOBILEPHONE_INVALID_ERROR.equals(exceptionID)){ //用户移动电话无效异常
				return Constants.USER_MOBILEPHONE_INVALID_ERROR;
			}else if(Constants.CONFERENCE_OBJ_NULL_ERROR.equals(exceptionID)){ //会议对象为空异常
				return Constants.CONFERENCE_OBJ_NULL_ERROR;
			}else if(Constants.SITE_NOT_EXIST_ERROR.equals(exceptionID)){  //站点不存在异常
				return Constants.SITE_NOT_EXIST_ERROR;
			}else if(Constants.SITE_NOT_DISABLE_ERROR.equals(exceptionID)){//站点未启用异常
				return Constants.SITE_NOT_DISABLE_ERROR;
			}else if(Constants.SITE_INACTIVE_ERROR.equals(exceptionID)){   //站点无效异常
				return Constants.SITE_INACTIVE_ERROR;
			}else if(Constants.SITE_SERVICE_DELETE_ERROR.equals(exceptionID)){//站点无服务异常
				return Constants.SITE_SERVICE_DELETE_ERROR;
			}else if(Constants.CONFERENCE_ID_NULL_ERROR.equals(exceptionID)){ //会议ID为空异常
				return Constants.CONFERENCE_ID_NULL_ERROR;
			}else if(Constants.CONFERENCE_KEY_NULL_ERROR.equals(exceptionID)){  //会议KEY为空异常
				return Constants.CONFERENCE_KEY_NULL_ERROR;
			}else if(Constants.CONFERENCE_SUBJECT_NULL_ERROR.equals(exceptionID)){ //会议主题为空异常
				return Constants.CONFERENCE_SUBJECT_NULL_ERROR;
			}else if(Constants.CONFERENCE_START_TIME_NULL_ERROR.equals(exceptionID)){//会议开始时间为空异常
				return Constants.CONFERENCE_START_TIME_NULL_ERROR;
			}else if(Constants.CONFERENCE_DURING_TIME_NULL_ERROR.equals(exceptionID)){  //会议期间时间为空异常
				return Constants.CONFERENCE_DURING_TIME_NULL_ERROR;
			}else if(Constants.CONFERENCE_OPENTYPE_NULL_ERROR.equals(exceptionID)){//会议模式为空异常
				return Constants.CONFERENCE_OPENTYPE_NULL_ERROR;
			}else if(Constants.CONFERENCE_HOST_OR_CREATEID_NULL_ERROR.equals(exceptionID)){  //会议主持人或者创建者ID为空异常
				return Constants.CONFERENCE_HOST_OR_CREATEID_NULL_ERROR;
			}else if(Constants.CONFERENCE_PASSWORD_NULL_ERROR.equals(exceptionID)){//会议密码为空异常
				return Constants.CONFERENCE_PASSWORD_NULL_ERROR;
			}else if(Constants.CONFERENCE_JOIN_BEFORE_HOST_TIME_NULL_ERROR.equals(exceptionID)){//提前加会时间为空
				return Constants.CONFERENCE_JOIN_BEFORE_HOST_TIME_NULL_ERROR;
			}else if(Constants.CONFERENCE_REG_END_TIME_NULL_ERROR.equals(exceptionID)){  //会议注册结束时间为空
				return Constants.CONFERENCE_REG_END_TIME_NULL_ERROR;
			}else if(Constants.AMOUNT_CHECK_EXCEED_ESTIMATE_ERROR.equals(exceptionID)){//数量检查超过预置异常
				return Constants.AMOUNT_CHECK_EXCEED_ESTIMATE_ERROR;
			}else if(Constants.ATTENDEE_EXCEED_AMOUNT_ERROR.equals(exceptionID)){ //参会人员超过点数异常
				return Constants.ATTENDEE_EXCEED_AMOUNT_ERROR;
			}else if(Constants.ATTENDEE_CHECK_PLACE_AMOUNT_ERROR.equals(exceptionID)){//参会人员检查会场数量异常
				return Constants.ATTENDEE_CHECK_PLACE_AMOUNT_ERROR;
			}else if(Constants.AMOUNT_RESOURCE_CONFLICT_ERROR.equals(exceptionID)){ //数量资源冲突异常
				return Constants.AMOUNT_RESOURCE_CONFLICT_ERROR;
			}else if(Constants.PLACE_RESOURCE_CONFLICT_ERROR.equals(exceptionID)){ //地点资源冲突异常
				return Constants.PLACE_RESOURCE_CONFLICT_ERROR;
			}else if(Constants.STARTTIME_EARLY_THAN_CURRENT_ERROR.equals(exceptionID)){//会议启动时间过早异常
				return Constants.STARTTIME_EARLY_THAN_CURRENT_ERROR;
			}else if(Constants.CONFERENCE_SUBJECT_INVALID_ERROR.equals(exceptionID)){//会议主题无效异常
				return Constants.CONFERENCE_SUBJECT_INVALID_ERROR;
			}else if(Constants.JOIN_BEF_HOST_ERROR.equals(exceptionID)){ //会议参会顺序错误异常
				return Constants.JOIN_BEF_HOST_ERROR;
			}else if(Constants.ATTENDEE_EMAIL_DUPLICATED_ERROR.equals(exceptionID)){    //与会者电子邮件重复异常
				return Constants.ATTENDEE_EMAIL_DUPLICATED_ERROR;
			}else if(Constants.USER_ACCOUTN_BALANCE_SHORTAGE_ERROR.equals(exceptionID)){//用户账户余额不足异常
				return Constants.USER_ACCOUTN_BALANCE_SHORTAGE_ERROR;
			}else if(Constants.BILLING_SYSTEM_ERROR.equals(exceptionID)){//计费系统异常异常
				return Constants.BILLING_SYSTEM_ERROR;
			}else if(Constants.USER_FEE_DEDUCTION_ERROR.equals(exceptionID)){//用户每月费用扣除失败异常
				return Constants.USER_FEE_DEDUCTION_ERROR;
			}else if(Constants.ATTENDEE_EMAIL_NOT_ALLOW_ERROR.equals(exceptionID)){//与会者电子邮件不允许异常
				return Constants.ATTENDEE_EMAIL_NOT_ALLOW_ERROR;
			}else if(Constants.STARTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR.equals(exceptionID)){//开启会议的会议ID和conkey不允许异常
				return Constants.STARTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR;
			}else if(Constants.JOINTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR.equals(exceptionID)){//参加会议的会议ID和conkey不允许异常
				return Constants.JOINTMEETING_CONFID_AND_CONFKEY_NOT_ALLOW_ERROR;
			}else if(Constants.MEETING_DATE_FORMAT_WRONG_ERROR.equals(exceptionID)){ //传入会议时间格式错误异常
				return Constants.MEETING_DATE_FORMAT_WRONG_ERROR;
			}else if(Constants.MEETING_XML_HEAD_NULL_ERROR.equals(exceptionID)){ //传入会议xml头信息格式错误异常
				return Constants.MEETING_XML_HEAD_NULL_ERROR;
			}else if(Constants.ATTENDEE_EMAIL_PARAMTER_NULL_ERROR.equals(exceptionID)){ //传入会议xmlEMAIL信息异常
				return Constants.ATTENDEE_EMAIL_PARAMTER_NULL_ERROR;
			}else if(Constants.MEETING_XML_PARAMTER_ERROR.equals(exceptionID)){//传入会议xml参数信息错误异常
				return Constants.MEETING_XML_PARAMTER_ERROR;
			}else if(Constants.MEETING_XML_PARAMTER_ERROR2.equals(exceptionID)){ //传入会议xml参数信息错误异常
				return Constants.MEETING_XML_PARAMTER_ERROR2;
			}else if(Constants.REPEAT_KEY_NULL_ERROR.equals(exceptionID)){//重复类型主键不存在异常
				return Constants.REPEAT_KEY_NULL_ERROR;
			}else if(Constants.DALIY_REPEAT_TYPE_ERROR.equals(exceptionID)){ //需要每天重复类型的值[0-9]，最大长度为3
				return Constants.DALIY_REPEAT_TYPE_ERROR;
			}else if(Constants.WEEKLY_REPEAT_TYPE_ERROR.equals(exceptionID)){//每周重复类型的值需要长度为7[1|0]字符串
				return Constants.WEEKLY_REPEAT_TYPE_ERROR;
			}else if(Constants.MONTHLY_REPEAT_TYPE_ERROR.equals(exceptionID)){   //每月重复类型的值要求小于32的整数
				return Constants.MONTHLY_REPEAT_TYPE_ERROR;
			}else if(Constants.STARTHOUTMINUTE_AND_ENDHOUTMINUTE_FORMAT_ERROR.equals(exceptionID)){ //startHourMinute和endHourMinute格式错误异常
				return Constants.STARTHOUTMINUTE_AND_ENDHOUTMINUTE_FORMAT_ERROR;
			}else if(Constants.STARTTIME_EARLY_THAN_ENDTIME_ERROR.equals(exceptionID)){ //结束时间早于开始时间异常
				return Constants.STARTTIME_EARLY_THAN_ENDTIME_ERROR;
			}else if(Constants.ATTENDEE_AMOUNT_INTEGER_ERROR.equals(exceptionID)){  //与会者数额不为正整数异常
				return Constants.ATTENDEE_AMOUNT_INTEGER_ERROR;
			}else if(Constants.REPART_RANGEDATE_FORMAT_ERROR.equals(exceptionID)){  //重复范围的日期参数格式不正确异常
				return Constants.REPART_RANGEDATE_FORMAT_ERROR;
			}else if(Constants.MEETINGPWD_NULL_ERROR.equals(exceptionID)){ //会议密码为空异常
				return Constants.MEETINGPWD_NULL_ERROR;
			}else if(Constants.EMAIL_NULL_ERROR.equals(exceptionID)){//EMAIL为空异常
				return Constants.EMAIL_NULL_ERROR;
			}else if(Constants.WEBBASEURL_NULL_ERROR.equals(exceptionID)){//webBaseUrl为空异常
				return Constants.WEBBASEURL_NULL_ERROR;
			}else if(Constants.ATTENDEENAME_NULL_ERROR.equals(exceptionID)){//attendeeName为空异常
				return Constants.ATTENDEENAME_NULL_ERROR;
			}else if(Constants.HOSTNAME_NULL_ERROR.equals(exceptionID)){//hostName为空异常
				return Constants.HOSTNAME_NULL_ERROR;
			}else if(Constants.CONFKEY_NULL_ERROR.equals(exceptionID)){//confKey为空异常
				return Constants.CONFKEY_NULL_ERROR;
			}else if(Constants.REQUEST_BODY_NULL_ERROR.equals(exceptionID)){  //请求消息体为空异常
				return Constants.REQUEST_BODY_NULL_ERROR;
			}else if(Constants.EMAIL_SHARP_ERROR.equals(exceptionID)){///EMAIL形式错误异常
				return Constants.EMAIL_SHARP_ERROR;
			}else if(Constants.HOST_NOT_EXIST_ERROR.equals(exceptionID)){//主持人不存在异常
				return Constants.HOST_NOT_EXIST_ERROR;
			}else if(Constants.SUPER_ADMIN_CONFIG_NULL_ERROR.equals(exceptionID)){//会议超级管理员配置为空异常
				return Constants.SUPER_ADMIN_CONFIG_NULL_ERROR;
			}else if(Constants.SITE_ADMIN_CONFIG_NULL_ERROR.equals(exceptionID)){//会议站点管理员配置为空异常
				return Constants.SITE_ADMIN_CONFIG_NULL_ERROR;
			}else if(Constants.CONFERENCE_PLACE_CONFIG_NULL_ERROR.equals(exceptionID)){//会议现场配置为空异常
				return Constants.CONFERENCE_PLACE_CONFIG_NULL_ERROR;
			}else if(Constants.CONFERENCE_PLACE_TYPE_NULL_ERROR.equals(exceptionID)){//会议现场类型为空异常
				return Constants.CONFERENCE_PLACE_TYPE_NULL_ERROR;
			}else if(Constants.SUPER_ADMIN_CONFIG_TOTALNUM_NULL_ERROR.equals(exceptionID)){   //会议超级管理员配置总数为空异常
				return Constants.SUPER_ADMIN_CONFIG_TOTALNUM_NULL_ERROR;
			}else if(Constants.SUPER_ADMIN_CONFIG_PLACE_TYPE_LIST_NULL_ERROR.equals(exceptionID)){ //会议超级管理员配置地点类型列表为空异常
				return Constants.SUPER_ADMIN_CONFIG_PLACE_TYPE_LIST_NULL_ERROR;
			}else if(Constants.CONFERENCE_NOT_DELETE_ERROR.equals(exceptionID)){ //会议不允许删除异常
				return Constants.CONFERENCE_NOT_DELETE_ERROR;
			}else if(Constants.CONFERENCE_INFO_NOT_UPDATE_ERROR.equals(exceptionID)){//会议信息不允许更新异常
				return Constants.CONFERENCE_INFO_NOT_UPDATE_ERROR;
			}else if(Constants.CONFERENCE_NOT_START_ERROR.equals(exceptionID)){ //会议状态不允许开始异常
				return Constants.CONFERENCE_NOT_START_ERROR;
			}else if(Constants.CONFERENCE_NOT_JOIN_ERROR.equals(exceptionID)){//会议状态不允许参会异常
				return Constants.CONFERENCE_NOT_JOIN_ERROR;
			}else if(Constants.JOIN_FAIL_ERROR.equals(exceptionID)){ //参会失败异常
				return Constants.JOIN_FAIL_ERROR;
			}else if(Constants.ATTENDEE_NAME_NULL_ERROR.equals(exceptionID)){  //与会人员为空异常
				return Constants.ATTENDEE_NAME_NULL_ERROR;
			}else if(Constants.NOT_LOGIN_ERROR.equals(exceptionID)){//未登录异常
				return Constants.NOT_LOGIN_ERROR;
			}else if(Constants.HOST_NOT_LOGIN_ERROR.equals(exceptionID)){//主持人未登录异常
				return Constants.HOST_NOT_LOGIN_ERROR;
			}else if(Constants.CURRENTUSER_NOT_HOST_ERROR.equals(exceptionID)){ //当前用户不是主持人异常
				return Constants.CURRENTUSER_NOT_HOST_ERROR;
			}else if(Constants.CONFERENCE_PASSWORD_ERROR.equals(exceptionID)){//会议密码错误异常
				return Constants.CONFERENCE_PASSWORD_ERROR;
			}else if(Constants.CONFERENCE_PLACE_NOT_AVAILABLE_ERROR.equals(exceptionID)){//会议地点不可见异常
				return Constants.CONFERENCE_PLACE_NOT_AVAILABLE_ERROR;
			}else if(Constants.NOT_JOIN_BEF_HOST_ERROR.equals(exceptionID)){//参会顺序错误异常
				return Constants.NOT_JOIN_BEF_HOST_ERROR;
			}else if(Constants.CONFERENCE_OVER_ERROR.equals(exceptionID)){ //参会结束异常
				return Constants.CONFERENCE_OVER_ERROR;
			}else if(Constants.CONFERENCE_LOCK_ERROR.equals(exceptionID)){//参会上锁异常
				return Constants.CONFERENCE_LOCK_ERROR;
			}else if(Constants.STRING_BULL_ERROR.equals(exceptionID)){ //{0}字符串为空异常
				return Constants.STRING_BULL_ERROR;
			}else if(Constants.QUERY_RESULT_ERROR.equals(exceptionID)){//查询结果错误异常
				return Constants.QUERY_RESULT_ERROR;
			}else if(Constants.REMOTE_TRAINING_OBJECT_NULL_ERROR.equals(exceptionID)){//远程培训对象为空异常
				return Constants.REMOTE_TRAINING_OBJECT_NULL_ERROR;
			}else if(Constants.MAINTRAININFO_CODE_NOT_MATCH_ERROR.equals(exceptionID)){   //MainTrainingInfo验证码不匹配异常
				return Constants.MAINTRAININFO_CODE_NOT_MATCH_ERROR;
			}else if(Constants.CONF_TOKEN_CRYPTO_FAIL_ERROR.equals(exceptionID)){//会议令牌加密失败异常
				return Constants.CONF_TOKEN_CRYPTO_FAIL_ERROR;
			}else if(Constants.USER_SAVE_FAIL_ERROR.equals(exceptionID)){//用户保存失败异常
				return Constants.USER_SAVE_FAIL_ERROR;
			}else if(Constants.USER_QUERY_FAIL_ERROR.equals(exceptionID)){ //用户查询失败异常
				return Constants.USER_QUERY_FAIL_ERROR;
			}else if(Constants.USER_CHECK_FAIL_ERROR.equals(exceptionID)){//用户检查失败异常
				return Constants.USER_CHECK_FAIL_ERROR;
			}else if(Constants.ROMOTE_SERVER_NULL_ERROR.equals(exceptionID)){//远程服务器为空异常
				return Constants.ROMOTE_SERVER_NULL_ERROR;
			}else if(Constants.ROMOTE_USER_NULL_ERROR.equals(exceptionID)){//远程用户为空异常
				return Constants.ROMOTE_USER_NULL_ERROR;
			}else if(Constants.SERVICE_VERSIONID_NULL_ERROR.equals(exceptionID)){//服务版本号为空异常
				return Constants.SERVICE_VERSIONID_NULL_ERROR;
			}else if(Constants.SITE_ID_NULL_ERROR.equals(exceptionID)){//站点ID为空异常
				return Constants.SITE_ID_NULL_ERROR;
			}else if(Constants.USERNAME_IS_EXIST_ERROR.equals(exceptionID)){  //用户名已存在异常
				return Constants.USERNAME_IS_EXIST_ERROR;
			}else if(Constants.USERNAME_EMAIL_EXIST_ERROR.equals(exceptionID)){ //用户email已存在异常
				return Constants.USERNAME_EMAIL_EXIST_ERROR;
			}else if(Constants.SITENAME_NULL_ERROR.equals(exceptionID)){ //站点名称为空异常
				return Constants.SITENAME_NULL_ERROR;
			}else if(Constants.NO_PERMISSION_ERROR2.equals(exceptionID)){ //检测未通过异常
				return Constants.NO_PERMISSION_ERROR2;
			}else if(Constants.SESSION_KEY_NULL_ERROR.equals(exceptionID)){//training.webapp.schedule.invitor.session.key.isnull异常
				return Constants.SESSION_KEY_NULL_ERROR;
			}else if(Constants.SCHEDULE_NOT_PERM_ERROR.equals(exceptionID)){ //raining.webapp.schedule.noperm异常
				return Constants.SCHEDULE_NOT_PERM_ERROR;
			}else if(Constants.MAINTRAININGINFO_NULL_ERROR.equals(exceptionID)){//training.webapp.schedule.session.maintrininginfo.null异常
				return Constants.MAINTRAININGINFO_NULL_ERROR;
			}else if(Constants.MAINTRAININGINFO_NULL_ERROR2.equals(exceptionID)){
				return Constants.MAINTRAININGINFO_NULL_ERROR2;//training.webapp.schedule.session.maintrininginfo.null异常
			}else{
				return Constants.UNKNOW_ERROR+result; 
			}
		}else{
			return result;
		}
	}
}