package com.seeyon.v3x.doc.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.increment.manager.AutoIncrementManager;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 文档的常量类，其中有一些工具方法
 */
public class Constants {
	private static final Log log = LogFactory.getLog(Constants.class);
	/**
	 * 国际化资源的路径
	 * */
	public static final String RESOURCE_BASENAME = "com.seeyon.v3x.doc.resources.i18n.DocResource";
	public static final String COMMON_RESOURCE_BASENAME = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	public static final String RESOURCE_BASENAME_INQUIRY = "com.seeyon.v3x.inquiry.resources.i18n.InquiryResources";
	public static final String RESOURCE_BASENAME_MEETING = "com.seeyon.v3x.meeting.resources.i18n.MeetingResources";
	public static final String RESOURCE_BASENAME_NEWS = "com.seeyon.v3x.news.resources.i18n.NewsResources";
	public static final String RESOURCE_BASENAME_PROJECT = "com.seeyon.v3x.project.resources.i18n.ProjectResources";
	public static final String RESOURCE_BASENAME_EDOC = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
	public static final String RESOURCE_BASENAME_BBS = "com.seeyon.v3x.bbs.resources.i18n.BBSResource";
		
	/**
	 * 文档库类型
	 */
	public static final Byte USER_CUSTOM_LIB_TYPE = 0;// 用户自定义文档库类型

	public static final Byte PERSONAL_LIB_TYPE = 1;// 个人文档库类型

	public static final Byte ACCOUNT_LIB_TYPE = 2;// 单位文档库类型
	
	public static final Byte EDOC_LIB_TYPE=3;	//公文类型
	
	public static final Byte PROJECT_LIB_TYPE=4;  //项目类型
	
	public static final Byte GROUP_LIB_TYPE=5; // 集团文档库
	// 空类型
	public static final Byte LIB_TYPE_NO = -1;
	
	// 2008.06.19 预置数据的id值
	public static final Long DOC_LIB_ID_GROUP = 10000L;
	public static final Long DOC_LIB_ROOT_ID_GROUP = 1000L;
	public static final Long DOC_LIB_ID_PROJECT = 9000L;
	public static final Long DOC_LIB_ROOT_ID_PROJECT = 900L;
	
	// ///////////////权限 DocAcl /////////////////////////
	public static final int ALLPOTENT = 0;// 全部权限

	public static final int EDITPOTENT = 1;// 编辑

	public static final int READONLYPOTENT = 2;// 只读

	public static final int BROWSEPOTENT = 3;// 浏览无下载

	public static final int LISTPOTENT = 4;// 查看列表

	public static final int DEPTBORROW = 5;// 单位借阅

	public static final int PERSONALBORROW = 6;// 个人借阅

	public static final int ADDPOTENT = 7;// 写入

	public static final int PERSONALSHARE = 8;// 个人共享

	public static final int NOPOTENT = 9;// 不继承标记

	// //////////////借阅共享////////////////////////
	public static final byte SHARETYPE_DEPTSHARE = 0;

	public static final byte SHARETYPE_DEPTBORROW = 1;

	public static final byte SHARETYPE_PERSSHARE = 2;

	public static final byte SHARETYPE_PERSBORROW = 3;
	
	/////////////////公文借阅查看权限///////////////////
	
	public static final byte LENPOTENT_ALL=1;
	public static final byte LENPOTENT_CONTENT=2;
	
	private static OrgManager orgManager;

	public static OrgManager getOrgManager() {
		if(orgManager == null){
			orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		}
		return orgManager;
	}

	// /////////////用户类型//////////////////////////
//	public static final int USERTYPE_USER = 0;
//
//	public static final int USERTYPE_DEPT = 1;
//
//	public static final int USERTYPE_WORK = 2;
//
//	public static final int USERTYPE_GROUP = 3;

//	public static final String ORG_MEMBER = "Member";
//
//	public static final String ORG_DEPT = "Department";
//
//	public static final String ORG_TEAM = "Team";
//
//	public static final String ORG_POST = "Post";
	
	/////////////////// 订阅用户类型 //////////////////////
	// 采用 V3xOrgEntity.ORGENT_XXX
	
	// 常用文档、学习文档的用户类型在系统的V3xOrgEntity.orgent_type 基础上增加集团
	public static final String ORGENT_TYPE_GROUP = "Group";

	/////////////////// 订阅操作类型 //////////////////////
	public static final byte ALERT_OPR_TYPE_ALL = 0;
	public static final String ALERT_OPR_TYPE_ALL_KEY = "doc.jsp.alert.all";
	public static final byte ALERT_OPR_TYPE_ADD = 1;
	public static final String ALERT_OPR_TYPE_ADD_KEY = "doc.jsp.alert.add";
	public static final byte ALERT_OPR_TYPE_EDIT = 2;
	public static final String ALERT_OPR_TYPE_EDIT_KEY = "doc.jsp.alert.edit";
	public static final byte ALERT_OPR_TYPE_DELETE = 3;
	public static final String ALERT_OPR_TYPE_DELETE_KEY = "doc.jsp.alert.delete";
	public static final byte ALERT_OPR_TYPE_FORUM = 4;
	public static final String ALERT_OPR_TYPE_FORUM_KEY = "doc.jsp.alert.forum";
	
	////////////////////是否通过共享订阅///////////////////////
	public static final byte IS_FROM_ACL= 1;

	// ////////////提醒消息///////////////////////////
	public static final String DOC_MESSAGE_ALERT_ADD_DOC = "doc.alert.add.doc";
	public static final String DOC_MESSAGE_ALERT_ADD_FOLDER = "doc.alert.add.folder";
	
	public static final String DOC_MESSAGE_ALERT_ADD_DOC_1 = "doc.alert.add.doc.1";
	public static final String DOC_MESSAGE_ALERT_ADD_FOLDER_1 = "doc.alert.add.folder.1";
	
	public static final String DOC_MESSAGE_ALERT_MODIFY_RENAME_DOC = "doc.alert.modify.rename.doc";
	public static final String DOC_MESSAGE_ALERT_MODIFY_RENAME_FOLDER = "doc.alert.modify.rename.folder";
	public static final String DOC_MESSAGE_ALERT_MODIFY_EDIT = "doc.alert.modify.edit";
	public static final String DOC_MESSAGE_ALERT_DELETE_DOC = "doc.alert.delete.doc";
	public static final String DOC_MESSAGE_ALERT_DELETE_FOLDER = "doc.alert.delete.folder";
	public static final String DOC_MESSAGE_ALERT_COMMENT = "doc.alert.comment";	
	public static final String DOC_MESSAGE_ALERT_MOVE_DOC = "doc.alert.move.doc";	
	public static final String DOC_MESSAGE_ALERT_MOVE_FOLDER = "doc.alert.move.folder";
	
	///////////// 格式类型 ////////////
	public static final long FORMAT_TYPE_SYSTEM_COL = 1L;
	public static final long FORMAT_TYPE_SYSTEM_ARCHIVES = 2L;
	public static final long FORMAT_TYPE_SYSTEM_PLAN = 3L;
	public static final long FORMAT_TYPE_SYSTEM_MEETING = 4L;
	public static final long FORMAT_TYPE_SYSTEM_NEWS = 5L;
	public static final long FORMAT_TYPE_SYSTEM_BULLETIN = 6L;
	public static final long FORMAT_TYPE_SYSTEM_INQUIRY = 7L;
	public static final long FORMAT_TYPE_SYSTEM_BBS = 8L;
	public static final long FORMAT_TYPE_SYSTEM_FORM = 9L;
	public static final long FORMAT_TYPE_SYSTEM_MAIL = 10L;
	public static final long FORMAT_TYPE_DOC_FILE = 21L;
	public static final long FORMAT_TYPE_DOC_A6 = 22L;
	public static final long FORMAT_TYPE_DOC_WORD = 23L;
	public static final long FORMAT_TYPE_DOC_EXCEL = 24L;
	public static final long FORMAT_TYPE_DOC_WORD_WPS = 25L;
	public static final long FORMAT_TYPE_DOC_EXCEL_WPS = 26L;
	public static final long FORMAT_TYPE_FOLDER_COMMON = 31L;
	public static final long FORMAT_TYPE_FOLDER_PLAN = 32L;
	public static final long FORMAT_TYPE_FOLDER_TEMPLET = 33L;
	public static final long FORMAT_TYPE_FOLDER_SHARE = 34L;
	public static final long FORMAT_TYPE_FOLDER_BORROW = 35L;
	public static final long FORMAT_TYPE_FOLDER_ARC_PRE = 36L;
	public static final long FORMAT_TYPE_FOLDER_ARC = 37L;
	public static final long FORMAT_TYPE_FOLDER_CASE = 38L;
	public static final long FORMAT_TYPE_FOLDER_CASE_PHASE = 39L;
	public static final long FORMAT_TYPE_FOLDER_MINE = 40L;
	public static final long FORMAT_TYPE_FOLDER_CORP = 41L;
	public static final long FORMAT_TYPE_FOLDER_PROJECT_ROOT = 42L;
	public static final long FORMAT_TYPE_FOLDER_PLAN_WEEK = 43L;
	public static final long FORMAT_TYPE_FOLDER_PLAN_MONTH = 44L;
	public static final long FORMAT_TYPE_FOLDER_PLAN_DAY = 45L;
	public static final long FORMAT_TYPE_FOLDER_PLAN_WORK = 46L;
	public static final long FORMAT_TYPE_ROOT_GROUP = 47L;
	public static final long FORMAT_TYPE_FOLDER_EDOC = 48L;
	public static final long FORMAT_TYPE_LINK = 51L;
	public static final long FORMAT_TYPE_LINK_FOLDER = 52L;
	
	/**
	 * 根据 formatType 取得bodyType，用于上传文件的类型区分
	 * @reeturn null 说明不可以在线编辑
	 * 
	 */
	public static String getBodyType(long mimeTypeId){
		if(mimeTypeId == FORMAT_TYPE_ID_UPLOAD_DOC)
			return com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD;
		else if(mimeTypeId == FORMAT_TYPE_ID_UPLOAD_XLS)
			return com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL;
		else if(mimeTypeId == FORMAT_TYPE_ID_UPLOAD_WPS_DOC){
			return com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD;
		}
		else if(mimeTypeId == FORMAT_TYPE_ID_UPLOAD_WPS_XLS){
			return com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL;
		}
		else if(mimeTypeId == FORMAT_TYPE_ID_UPLOAD_JPG||mimeTypeId == FORMAT_TYPE_ID_UPLOAD_GIF||mimeTypeId == FORMAT_TYPE_ID_UPLOAD_PNG)
			return  com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML;
		else
			return null;
	}
	
	public static final long FORMAT_TYPE_ID_UPLOAD_DOC = 101L;
	public static final long FORMAT_TYPE_ID_UPLOAD_WPS_DOC = 121L;
	public static final long FORMAT_TYPE_ID_UPLOAD_XLS = 102L;
	public static final long FORMAT_TYPE_ID_UPLOAD_WPS_XLS = 120L;
	public static final long FORMAT_TYPE_ID_UPLOAD_JPG = 117L;
	public static final long FORMAT_TYPE_ID_UPLOAD_PNG = 112L;
	public static final long FORMAT_TYPE_ID_UPLOAD_GIF = 109L;
	public static final long FORMAT_TYPE_ID_UPLOAD_PDF = 103L;
	public static final long FORMAT_TYPE_ID_TXT = 105l;
	public static final long FORMAT_TYPE_ID_HTML = 107l;
	public static final long FORMAT_TYPE_ID_HTM = 108l;
	
	//新增wps类的。
	
	/////////////格式排序/////////////
	/*
	INSERT INTO doc_mime_types VALUES ('1', '协同', 'DoFlow_big_new_s.gif', '1', 							'41');
	INSERT INTO doc_mime_types VALUES ('2', '公文', 'exe_small.gif', '2', 									'42');
	INSERT INTO doc_mime_types VALUES ('3', '计划', 'exe_small.gif', '3', 									'49');
	INSERT INTO doc_mime_types VALUES ('4', '会议', 'exe_small.gif', '4', 									'48');
	INSERT INTO doc_mime_types VALUES ('5', '新闻', 'exe_small.gif', '5', 									'45');
	INSERT INTO doc_mime_types VALUES ('6', '公告', 'exe_small.gif', '6', 									'44');
	INSERT INTO doc_mime_types VALUES ('7', '调查', 'exe_small.gif', '7', 									'46');
	INSERT INTO doc_mime_types VALUES ('8', '讨论', 'exe_small.gif', '8', 									'47');
	INSERT INTO doc_mime_types VALUES ('9', '表单', 'exe_small.gif', '9', 									'43');
	INSERT INTO doc_mime_types VALUES ('10', '邮件', 'exe_small.gif', '10', 									'50');
	INSERT INTO doc_mime_types VALUES ('21', '文件', 'file.gif', '21', 										'91');
	INSERT INTO doc_mime_types VALUES ('22', 'A6文档', 'htm_small.gif', '22', 								'61');
	INSERT INTO doc_mime_types VALUES ('23', 'Word文档', 'doc_small.gif', '23', 								'62');
	INSERT INTO doc_mime_types VALUES ('24', 'Excel文档', 'xls_small.gif', '24', 							'63');
	INSERT INTO doc_mime_types VALUES ('25', 'WpsWord文档', 'wps_word.gif', '25', 							'65');
	INSERT INTO doc_mime_types VALUES ('26', 'WpsExcel文档', 'wps_excel.gif', '26', 							'66');
	INSERT INTO doc_mime_types VALUES ('31', '文档夹', 'folder_close.gif|folder_open.gif', '31', 				'31');
	INSERT INTO doc_mime_types VALUES ('32', '我的计划', 'orange_close.gif|orange_open.gif', '32',			'23');
	INSERT INTO doc_mime_types VALUES ('33', '我的模版', 'green_close.gif|green_open.gif', '33', 				'24');
	INSERT INTO doc_mime_types VALUES ('34', '他人文档', 'grey_close.gif|grey_open.gif', '34', 				'22');
	INSERT INTO doc_mime_types VALUES ('35', '借阅文档', 'purple_close.gif|purple_open.gif', '35', 			'21');
	INSERT INTO doc_mime_types VALUES ('36', '公文预归档目录', 'edoc_close.gif|edoc_open.gif', '36', 			'12');
	INSERT INTO doc_mime_types VALUES ('37', '公文档案(库)', 'edoc_close.gif|edoc_open.gif', '37', 			'4');
	INSERT INTO doc_mime_types VALUES ('38', '项目文档夹', 'folder_close.gif|folder_open.gif', '38', 			'13');
	INSERT INTO doc_mime_types VALUES ('39', '项目阶段文档夹', 'folder_close.gif|folder_open.gif', '39', 		'14');
	INSERT INTO doc_mime_types VALUES ('40', '我的文档', 'my_close.gif|my_open.gif', '40', 					'5');
	INSERT INTO doc_mime_types VALUES ('41', '单位文档', 'corp_close.gif|corp_open.gif', '41', 				'3');
	INSERT INTO doc_mime_types VALUES ('42', '项目文档', 'case_close.gif|case_open.gif', '42', 				'2');
	INSERT INTO doc_mime_types VALUES ('43', '周计划', 'folder_close.gif|folder_open.gif', '43', 				'28');
	INSERT INTO doc_mime_types VALUES ('44', '月计划', 'folder_close.gif|folder_open.gif', '44', 				'27');
	INSERT INTO doc_mime_types VALUES ('45', '日计划', 'folder_close.gif|folder_open.gif', '45', 				'26');
	INSERT INTO doc_mime_types VALUES ('46', '工作计划', 'folder_close.gif|folder_open.gif', '46', 			'25');
	INSERT INTO doc_mime_types VALUES ('47', '集团文档', 'group_close.gif|group_open.gif', '47', 				'1');
	INSERT INTO doc_mime_types VALUES ('48', '公文档案夹', 'edoc_close.gif|edoc_open.gif', '48', 				'11');
	INSERT INTO doc_mime_types VALUES ('51', '映射', 'link_file.gif', '51', 									'102');
	INSERT INTO doc_mime_types VALUES ('52', '文档夹映射', 'link_folder.gif', '52', 							'101');
	INSERT INTO doc_mime_types VALUES ('101', 'doc', 'doc_small.gif', '21', 								'62');
	INSERT INTO doc_mime_types VALUES ('102', 'xls', 'xls_small.gif', '21', 								'63');
	INSERT INTO doc_mime_types VALUES ('103', 'pdf', 'icon_pdf.gif', '21', 									'67');
	INSERT INTO doc_mime_types VALUES ('104', 'ppt', 'ppt_small.gif', '21', 								'64');
	INSERT INTO doc_mime_types VALUES ('105', 'txt', 'txt_small.gif', '21', 								'68');
	INSERT INTO doc_mime_types VALUES ('106', 'bmp', 'bmp.gif', '21', 										'69');
	INSERT INTO doc_mime_types VALUES ('107', 'html', 'htm_small.gif', '21', 								'61');
	INSERT INTO doc_mime_types VALUES ('108', 'htm', 'htm_small.gif', '21', 								'61');
	INSERT INTO doc_mime_types VALUES ('109', 'gif', 'icon_gif.gif', '21', 									'70');
	INSERT INTO doc_mime_types VALUES ('110', 'mpg', 'icon_mpg.gif', '21', 									'74');
	INSERT INTO doc_mime_types VALUES ('111', 'pcx', 'icon_pcx.gif', '21', 									'75');
	INSERT INTO doc_mime_types VALUES ('112', 'png', 'icon_png.gif', '21', 									'71');
	INSERT INTO doc_mime_types VALUES ('113', 'rm', 'icon_rm.gif', '21', 									'83');
	INSERT INTO doc_mime_types VALUES ('114', 'tga', 'icon_tga.gif', '21', 									'84');
	INSERT INTO doc_mime_types VALUES ('115', 'tif', 'icon_tif.gif', '21', 									'73');
	INSERT INTO doc_mime_types VALUES ('116', 'zip', 'icon_zip.gif', '21', 									'81');
	INSERT INTO doc_mime_types VALUES ('117', 'jpg', 'jpg_small.gif', '21', 								'72');
	INSERT INTO doc_mime_types VALUES ('118', 'jpeg', 'jpg_small.gif', '21', 								'72');
	INSERT INTO doc_mime_types VALUES ('119', 'rar', 'rar_small.gif', '21', 								'82');
	 */
	
	
	// ////////////内容类型///////////////////////
	public static final long SYSTEM_COL = 1L;
	public static final String SYSTEM_COL_KEY = "doc.contenttype.xietong";
	public static final long SYSTEM_ARCHIVES = 2L;
	public static final String SYSTEM_ARCHIVES_KEY = "doc.contenttype.gongwen";
	public static final long SYSTEM_PLAN = 3L;
	public static final String SYSTEM_PLAN_KEY = "doc.contenttype.jihua";
	public static final long SYSTEM_MEETING = 4L;
	public static final String SYSTEM_MEETING_KEY = "doc.contenttype.huiyi";
	public static final long SYSTEM_NEWS = 5L;
	public static final String SYSTEM_NEWS_KEY = "doc.contenttype.xinwen";
	public static final long SYSTEM_BULLETIN = 6L;
	public static final String SYSTEM_BULLETIN_KEY = "doc.contenttype.gonggao";
	public static final long SYSTEM_INQUIRY = 7L;
	public static final String SYSTEM_INQUIRY_KEY = "doc.contenttype.diaocha";
	public static final long SYSTEM_BBS = 8L;
	public static final String SYSTEM_BBS_KEY = "doc.contenttype.taolun";
	public static final long SYSTEM_FORM = 9L;
	public static final String SYSTEM_FORM_KEY = "doc.contenttype.biaodan";
	public static final long SYSTEM_MAIL = 10L;
	public static final String SYSTEM_MAIL_KEY = "doc.contenttype.mail";
	//信息报送  类型定义  郝后成  2012-3-6
	public static final long SYSTEM_INFO = 15L;
	public static final String SYSTEM_INFO_KEY = "doc.contenttype.xinxi";
	public static final long SYSTEM_INFOSTAT = 16L;
	public static final String SYSTEM_INFOSTAT_KEY = "doc.contenttype.xinxitongji";
	
	public static final long DOCUMENT = 21L;
	public static final String DOC_KEY = "doc.contenttype.wenjian";
	public static final long FOLDER_COMMON = 31L;
	public static final String FOLDER_COMMON_KEY = "doc.contenttype.folder";
	public static final long FOLDER_PLAN = 32L;
	public static final String FOLDER_PLAN_KEY = "doc.contenttype.myplan";
	public static final long FOLDER_TEMPLET = 33L;
	public static final String FOLDER_TEMPLET_KEY = "doc.contenttype.mytemplate";
	public static final long FOLDER_SHARE = 34L;
	public static final String FOLDER_SHARE_KEY = "doc.contenttype.myshare";
	public static final long FOLDER_BORROW = 35L;
	public static final String FOLDER_BORROW_KEY = "doc.contenttype.myborrow";
	public static final long FOLDER_SHAREOUT = 110L;
	public static final String FOLDER_SHAREOUT_KEY = "doc.contenttype.shareOut";
	public static final long FOLDER_BORROWOUT = 111L;
	public static final String FOLDER_BORROWOUT_KEY = "doc.contenttype.borrowOut";
	
	public static final long FOLDER_ARC_PRE = 36L;
	

	public static final String FOLDER_ARC_PRE_KEY = "doc.contenttype.gongwenyuguidang";
	public static final long ROOT_ARC = 37L;
	public static final String ROOT_ARC_KEY = "doc.contenttype.gongwendangan";
	public static final long FOLDER_CASE = 38L;
	public static final String FOLDER_CASE_KEY = "doc.contenttype.xiangmuwendang";
	public static final long FOLDER_CASE_PHASE = 39L;
	public static final String FOLDER_CASE_PHASE_KEY = "doc.contenttype.xiangmujieduan";
	public static final long FOLDER_MINE = 40L;
	public static final String FOLDER_MINE_KEY = "doc.contenttype.mydoc";
	public static final long FOLDER_CORP = 41L;
	public static final String FOLDER_CORP_KEY = "doc.contenttype.danweiwendang";
	public static final long FOLDER_PROJECT_ROOT = 42L;
	public static final String FOLDER_PROJECT_ROOT_KEY = "doc.contenttype.xiangmugen";
	public static final long FOLDER_PLAN_WEEK = 43L;
	public static final String FOLDER_PLAN_WEEK_KEY = "doc.contenttype.zhoujihua";
	public static final long FOLDER_PLAN_MONTH = 44L;
	public static final String FOLDER_PLAN_MONTH_KEY = "doc.contenttype.yuejihua";
	public static final long FOLDER_PLAN_DAY = 45L;
	public static final String FOLDER_PLAN_DAY_KEY = "doc.contenttype.rijihua";	
	public static final long FOLDER_PLAN_WORK = 46L;
	public static final String FOLDER_PLAN_WORK_KEY = "doc.contenttype.gongzuojihua";
	
	public static final long ROOT_GROUP = 47L;
	public static final String ROOT_GROUP_KEY = "doc.contenttype.root.group";
	public static final long FOLDER_EDOC = 48L;
	public static final String FOLDER_EDOC_KEY = "doc.contenttype.folder.edoc";
	
	public static final long LINK = 51L;
	public static final String LINK_KEY = "doc.contenttype.yingshe";
	public static final long LINK_FOLDER = 52L;
	public static final String LINK_FOLDER_KEY = "doc.contenttype.wendangjiayingshe";
	// 虚拟类型
	public static final long PERSON_SHARE = 101L;
	
	
	public static final long PERSON_BORROW = 102L;
	public static final long DEPARTMENT_BORROW = 103L;
	public static final String DEPARTMENT_BORROW_KEY = "doc.contenttype.publicBorrow";
	// 虚拟类型对应图标名称
	public static final String PERSON_ICON = "person.gif";
	
	//显示栏目名称
	public static final String DOC_COLUMN_NAME ="doc.column.name";
	

	// ////////////////////状态 ///////////////
	public static final byte DOC_STATUS = 1;

//	public static final byte DOC_ENABLE = 1;
//
//	public static final byte DOC_DISABLE = 0;

	// ////////////////////内容类型分类 ///////////////
	public static final byte CATEGORY_SYSTEM = 1;

	public static final byte CATEGORY_FILE = 2;

	public static final byte CATEGORY_DOC = 3;

	public static final byte CATEGORY_FOLDER = 4;

	public static final byte CATEGORY_LINK = 5;
	
	// 发起人def的id
	public static final long METADATA_SENDER_ID = 110L;
	// 发起时间def的id
	public static final long METADATA_SENDTIME_ID = 104L;
	
	//类别名称
//	public static final String CATEGORY="所有类别";									//所有得类别
	public static final String DEFAULT_CATEGORY="metadataDef.default_category";						//默认类别

	// 空间类型
//	public static final byte SPACE_ACCOUNT = 0;
//	public static final byte SPACE_DEPARTMENT = 1;
//	public static final byte SPACE_PROJECT = 2;
//	public static final byte SPACE_PERSONAL = 3;
	
	//空间状态
	public static final byte SPACE_NOT_ASSIGNED = 100; //未分配
	public static final byte SPACE_FREE=0;
	public static final byte SPACE_ALERT=1;
	public static final byte SPACE_FULL=2;
	
	/**
	 * 根据空间状态取得空间状态的提示key
	 */
	public static String getSpaceKey(byte key){
		if(key == Constants.SPACE_FREE){
			return "doc.space.free";
		}else if(key == Constants.SPACE_ALERT){
			return "doc.space.alert";
		}else if(key == Constants.SPACE_FULL){
			return "doc.space.full";
		}else {
			return "doc.space.nonassigned";
		}
	}
	
	
//	public static Long getContentTypeByAppEnum(ApplicationCategoryEnum appEnum) {
//		if (appEnum == ApplicationCategoryEnum.collaboration)
//			return SYSTEM_COL;
//		else if (appEnum == ApplicationCategoryEnum.edoc)
//			return SYSTEM_ARCHIVES;
//		return null;
//	}
	
	// 判断是否系统归档类型
	public static boolean isPigeonhole(long type){
		if(type == Constants.SYSTEM_ARCHIVES || type == Constants.SYSTEM_BBS 
				|| type == Constants.SYSTEM_BULLETIN || type == Constants.SYSTEM_COL 
				|| type == Constants.SYSTEM_FORM || type == Constants.SYSTEM_INQUIRY 
				|| type == Constants.SYSTEM_MEETING || type == Constants.SYSTEM_NEWS 
				|| type == Constants.SYSTEM_PLAN || type == Constants.SYSTEM_MAIL
				|| type == Constants.SYSTEM_INFO) 
			return true;
		else
			return false;

	}
	/**
	 * 得到appEnum
	 */
	public static ApplicationCategoryEnum getAppEnum(long type){
		if(type == Constants.SYSTEM_ARCHIVES)
			return ApplicationCategoryEnum.edoc;
		if(type == Constants.SYSTEM_COL)
			return ApplicationCategoryEnum.collaboration;
		if(type == Constants.SYSTEM_INQUIRY)
			return ApplicationCategoryEnum.inquiry;
		if(type == Constants.SYSTEM_MEETING)
			return ApplicationCategoryEnum.meeting;
		if(type == Constants.SYSTEM_NEWS)
			return ApplicationCategoryEnum.news;
		if(type == Constants.SYSTEM_PLAN)
			return ApplicationCategoryEnum.plan;
		if(type == Constants.SYSTEM_MAIL)
			return ApplicationCategoryEnum.mail;
		if(type == Constants.SYSTEM_BULLETIN)
			return ApplicationCategoryEnum.bulletin;
		if(type == Constants.SYSTEM_INFO)
			return ApplicationCategoryEnum.info;
		
		return null;

	}
	

	
	// 根据frType 得到 planType
	public static String getPlanTypeByFrType(long frType) {
		if(frType == Constants.FOLDER_PLAN_DAY)
			return com.seeyon.v3x.plan.PlanType.DAY_PLAN.getValue();
		else if(frType == Constants.FOLDER_PLAN_MONTH)
			return com.seeyon.v3x.plan.PlanType.MONTH_PLAN.getValue();
		else if(frType == Constants.FOLDER_PLAN_WEEK)
			return com.seeyon.v3x.plan.PlanType.WEEK_PLAN.getValue();
		else if(frType == Constants.FOLDER_PLAN_WORK)
			return com.seeyon.v3x.plan.PlanType.ANY_SCOPE_PLAN.getValue();
		
		return com.seeyon.v3x.plan.PlanType.ANY_SCOPE_PLAN.getValue();
	}
	
	// 关联项目
	// 项目文档夹的状态字段
	public static final String FOLDER_CASE_PHYSICAL_NAME_STATUS = "avarchar4";
	// 项目阶段文档夹的删除标记字段
	public static final String FOLDER_CASE_PHASE_PHYSICAL_NAME_DELETE = "avarchar8";	
	
	// 知识管理首页的显示条数
	public static final int DOC_HOMEPAGE_ALERT_COUNT = 7;
	// 知识管理首页的显示条数
	public static final int DOC_HOMEPAGE_SESSION_COUNT = 7;
	public static final int DOC_HOMEPAGE_BLOG_COUNT = 7;

	// MetadaDef 类型
	public static final byte TEXT_ONE_LINE = 1;	
	/** 整数类型 */
	public static final byte INTEGER = 2;
	/** 小数类型*/
	public static final byte FLOAT = 3;
	/** 日期类型 **/
	public static final byte DATE = 4; 
	/** 日期时间 */
	public static final byte DATETIME = 5;
	/** 多行文本 */
	public static final byte TEXT = 6;
	/** 布尔类型 */
	public static final byte BOOLEAN = 7; 
	/** 系统引用类型 */
	public static final byte REFERENCE = 0;//n
	/** 用户类型 */
	public static final byte USER_ID = 8;//与引用类型合并 y
	/** 部门类型 */
	public static final byte DEPT_ID = 9;//与引用类型合并 y
	/** 文件类型 */
	public static final byte CONTENT_TYPE = 10;
	/** 图标类型  */
	public static final byte IMAGE_ID = 11;
	/** 文件大小 */
	public static final byte SIZE = 12;
	/** 枚举类型 */
	public static final byte ENUM = 13;	
	
	// metadataDef 的系统预定义类型
	public static final String METADATA_DEF_CATEGORY_DEFAULT = "metadataDef.category.default";
	public static final String METADATA_DEF_CATEGORY_PROJECT = "metadataDef.category.project";
//	public static final String METADATA_DEF_CATEGORY_COLL = "collaboration";
//	public static final String METADATA_DEF_CATEGORY_EDOC = "edoc";
	public static final String METADATA_DEF_CATEGORY_INQUIRY = "metadataDef.category.inquiry";
	public static final String METADATA_DEF_CATEGORY_NEWS = "metadataDef.category.news";
//	public static final String METADATA_DEF_CATEGORY_BULLETIN = "bulletin";
//	public static final String METADATA_DEF_CATEGORY_PLAN = "plan";
//	public static final String METADATA_DEF_CATEGORY_MAIL = "mail";
	public static final String METADATA_DEF_CATEGORY_MEETING = "metadataDef.category.meeting";
	public static final String METADATA_DEF_CATEGORY_COMMON = "metadataDef.category.common";
	public static final String METADATA_DEF_CATEGORY_EDOC = "metadataDef.category.edoc";

	// 元数据页面显示组件类型
	public static final byte DOC_METADATA_SHOW_TYPE_CHECKBOX = 1;

	public static final byte DOC_METADATA_SHOW_TYPE_OPTION = 2;

	public static final byte DOC_METADATA_SHOW_TYPE_RADIO = 3;

	public static final byte DOC_METADATA_SHOW_TYPE_TEXT = 4;
	
	//内容类型的格式类别常量定义, add by handy,2007-8-8 16:28
	// parentType
	public final static byte CONTENT_CATEGORY_FOLDER = 0; // 文档夹
	public final static byte CONTENT_CATEGORY_DOCUMENT = 1; // 文档
	public final static byte CONTENT_CATEGORY_FORM = 2; // 表单
	
	//内容类型状态
	public final static byte CONTENT_TYPE_DRAFT = 0; // 编辑状态
	public final static byte CONTENT_TYPE_PUBLISHED = 1; //已使用
	public final static byte CONTENT_TYPE_DELETED = 2; //已删除
	
	// 元數據定義狀態
	public static final int DOC_METADATA_DEF_STATUS_DRAFT = 0;   // 草稿
	public static final int DOC_METADATA_DEF_STATUS_COLUMNED = 1;  // 被設為顯示欄目
	public static final int DOC_METADATA_DEF_STATUS_PUBLISHED = 2;  // 被真實數據引用
	public static final int DOC_METADATA_DEF_STATUS_DELETED = 3; // 邏輯刪除
	
	
	// ajax
	public final static String AJAX_JSON = "json";
	// 所有元数据类型
	public static List<Byte> getAllType(){		
		List<Byte> list = new ArrayList<Byte>();
		list.add(Constants.TEXT_ONE_LINE);
		list.add(Constants.TEXT);
		list.add(Constants.INTEGER);		
		list.add(Constants.FLOAT);	
		list.add(Constants.DATE);
		list.add(Constants.DATETIME);
		list.add(Constants.BOOLEAN);
		list.add(Constants.ENUM);
		list.add(Constants.USER_ID);
		list.add(Constants.DEPT_ID);
		return list;
	}
	
	/**
	 * 元数据类型转为国际化key
	 * 
	 */
	public static String getKeyByType(byte type){
		String theKey="";
		switch(type){
			case 0: theKey="metadataDef.type.reference";
					break;
			case 1: theKey="metadataDef.type.text_one_line";
					break;
			case 2: theKey="metadataDef.type.integer";
					break;
			case 3: theKey="metadataDef.type.float";
					break;
				
			case 4: theKey="metadataDef.type.date";
					break;
			case 5: theKey="metadataDef.type.datetime";
					break;
			case 6: theKey="metadataDef.type.text";
					break;
			case 7: theKey="metadataDef.type.boolean";
					break;
			case 8: theKey="metadataDef.type.user_id";
					break;
			case 9: theKey="metadataDef.type.dept_id";
					break;
			case 10: theKey="metadataDef.type.content_type";
					break;
			case 11: theKey="metadataDef.type.image_id";
					break;
			case 12: theKey="metadataDef.type.size";
					break;
			case 13: theKey="metadataDef.type.enum";
					break;
				
		}
		return theKey;
	}

//	public static String getShowType(byte type) {
//		switch (type) {
//		case 1:
//			return "checkbox";
//		case 2:
//			return "option";
//		case 3:
//			return "radio";
//		case 4:
//			return "text";
//		default:
//			return "text";
//		}
//	}

	/**
	 * 根据内容不同在页面的对齐方式 标准 left 文字 center 长短一致，如布尔 right 数字，时间
	 */
	public static String getAlign(byte type) {
		switch (type) {
		case 1:
			return "left";
		case 2:
			return "right";
		case 3:
			return "right";
		case 4:
			return "left";
		case 5:
			return "left";
		case 6:
			return "left";
		case 7:
			return "center";
		case 8:
			return "left";
		case 9:
			return "left";
		case 10:
			return "left";
		case 11:
			return "center";
		case 12:
			return "right";
		case 13:
			return "left";
		default:
			return "left";
		}
	}

	/**
	 * 根据内容不同在页面的显示宽度 
	 * 计算标准
	 *  1 -- 一个英文字符，一个数字的宽度 
	 *  2 -- 两个英文字符，一个汉字的宽度
	 */
	public static int getWidthByType(byte type) {
		switch (type) {
		case Constants.TEXT_ONE_LINE:
			return 10;
		case Constants.INTEGER:
			return 16;
		case Constants.FLOAT:
			return 16;
		case Constants.DATE:
			return 12;
		case Constants.DATETIME:
			return 16;
		case Constants.TEXT:
			return 16;
		case Constants.BOOLEAN:
			return 6;
		case Constants.USER_ID:
			return 10;
		case Constants.DEPT_ID:
			return 16;
		case Constants.CONTENT_TYPE:
			return 10;
		case Constants.IMAGE_ID:
			return 8;
		case Constants.SIZE:
			return 10;
		case Constants.ENUM:
			return 10;
		default:
			return 10;
		}
	}


//	// 标题栏的宽度
//	public static int DOC_TABLE_LIMITED_WIDTH_SUBJECT = 327;
	public static int DOC_TABLE_LIMITED_WIDTH_ICON = 34;
	public static int DOC_TABLE_LIMITED_WIDTH_CHECKBOX = 34;
	
//订阅类型状态
	public static byte DOC_ALERT_STATUS_ALL=4;
	public static byte DOC_ALERT_STATUS_MYSELF=0;
	public static byte DOC_ALERT_STATUS_OTHER=1;
	
//	/**
//	 * 根据内容不同在页面的显示宽度 
//	 * 计算标准
//	 *  1 -- 一个英文字符，一个数字的宽度 
//	 *  2 -- 两个英文字符，一个汉字的宽度
//	 */
//	public static int getWidthByType(byte type) {
//		int times = 7;
//		int width = 0;
//		switch (type) {
//		case Constants.TEXT_ONE_LINE:
//			width = 10;
//			break;
//		case Constants.INTEGER:
//			width = 16;
//			break;
//		case Constants.FLOAT:
//			width = 16;
//			break;
//		case Constants.DATE:
//			width = 22;
//			break;
//		case Constants.DATETIME:
//			width = 22;
//			break;
//		case Constants.TEXT:
//			width = 16;
//			break;
//		case Constants.BOOLEAN:
//			width = 6;
//			break;
//		case Constants.USER_ID:
//			width = 10;
//			break;
//		case Constants.DEPT_ID:
//			width = 16;
//			break;
//		case Constants.CONTENT_TYPE:
//			width = 16;
//			break;
//		case Constants.IMAGE_ID:
//			width = 4;
//			break;
//		case Constants.SIZE:
//			width = 16;
//			break;
//		case Constants.ENUM:
//			width = 10;
//			break;
//		default:
//			width = 10;
//			break;
//		}
//		
//		return width * times;
//	}

	/**
	 * 取得oracle下元数据类型对应的数据列类型
	 */
	public static String getDBTypeOfOracle(byte type) {
		switch (type) {
		case Constants.REFERENCE:
			return "NUMBER(19, 0)";
		case Constants.TEXT_ONE_LINE:
			return "VARCHAR2(200)";
		case Constants.INTEGER:
			return "NUMBER(38, 0)";
		case Constants.FLOAT:
			return "NUMBER(10, 2)";
		case Constants.DATE:
			return "DATE";
		case Constants.DATETIME:
			return "TIMESTAMP(6)";
		case Constants.TEXT:
			return "CLOB";
		case Constants.BOOLEAN:
			return "NUMBER(3, 0)";
		case Constants.USER_ID:
			return "NUMBER(19, 0)";
		case Constants.DEPT_ID:
			return "NUMBER(19, 0)";
		case Constants.ENUM:
			return "NUMBER(19, 0)";
		default:
			return null;
		}
	}
	/**
	 * 取得mysql下元数据类型对应的数据列类型
	 */
	public static String getDBTypeOfMySql(byte type) {
		switch (type) {
		case Constants.REFERENCE:
			return "bigint(20)";
		case Constants.TEXT_ONE_LINE:
			return "varchar(200)";
		case Constants.INTEGER:
			return "int(11)";
		case Constants.FLOAT:
			return "decimal(10,2)";
		case Constants.DATE:
			return "date";
		case Constants.DATETIME:
			return "datetime";
		case Constants.TEXT:
			return "longtext";
		case Constants.BOOLEAN:
			return "tinyint(4)";
		case Constants.USER_ID:
			return "bigint(20)";
		case Constants.DEPT_ID:
			return "bigint(20)";
		case Constants.ENUM:
			return "bigint(20)";
		default:
			return null;
		}
	}
	/**
	 * 取得sqlserver下元数据类型对应的数据列类型
	 */
	public static String getDBTypeOfSqlServer(byte type) {
		switch (type) {
		case Constants.REFERENCE:
			return "bigint";
		case Constants.TEXT_ONE_LINE:
			return "varchar(200)";
		case Constants.INTEGER:
			return "int";
		case Constants.FLOAT:
			return "numeric(12,2)";
		case Constants.DATE:
			return "smalldatetime";
		case Constants.DATETIME:
			return "datetime";
		case Constants.TEXT:
			return "text";
		case Constants.BOOLEAN:
			return "tinyint";
		case Constants.USER_ID:
			return "bigint";
		case Constants.DEPT_ID:
			return "bigint";
		case Constants.ENUM:
			return "bigint";
		default:
			return null;
		}
	}
	
	/**
	 * 得到 DocMetadata.hbm.xml 的添加串
	 */
	public static String getString2Add(byte type, String columnName, boolean oracle9){
		String ret = "";
		
		switch (type) {
		case Constants.REFERENCE:
			ret = " <property name=\"" + columnName + "\" type=\"long\" column=\"" + columnName + "\" length=\"20\" /> ";
			break;
		case Constants.TEXT_ONE_LINE:
			ret = " <property name=\"" + columnName + "\" type=\"string\" column=\"" + columnName + "\"	length=\"200\" /> ";
			break;
		case Constants.INTEGER:
			ret = " <property name=\"" + columnName + "\" type=\"integer\" column=\"" + columnName + "\"	length=\"11\" /> ";
			break;
		case Constants.FLOAT:
			ret = " <property name=\"" + columnName + "\" type=\"double\" column=\"" + columnName + "\"	length=\"10\" /> ";
			break;
		case Constants.DATE:
			ret = " <property name=\"" + columnName + "\" type=\"date\" column=\"" + columnName + "\"	length=\"10\" /> ";
			break;
		case Constants.DATETIME:
			ret = " <property name=\"" + columnName + "\" type=\"timestamp\" column=\"" + columnName + "\"	length=\"19\" /> ";
			break;
		case Constants.TEXT:{
			if(oracle9)
				ret = " <property name=\"" + columnName + "\" type=\"org.springframework.orm.hibernate3.support.ClobStringType\" column=\"" + columnName + "\"	 /> ";
			else
				ret = " <property name=\"" + columnName + "\" type=\"text\" column=\"" + columnName + "\"	length=\"65535\" /> ";
			break;
		}
		case Constants.BOOLEAN:
			ret = " <property name=\"" + columnName + "\" type=\"boolean\" column=\"" + columnName + "\"	length=\"4\" /> ";
			break;
		case Constants.USER_ID:
			ret = " <property name=\"" + columnName + "\" type=\"long\" column=\"" + columnName + "\"	length=\"20\" /> ";
			break;
		case Constants.DEPT_ID:
			ret = " <property name=\"" + columnName + "\" type=\"long\" column=\"" + columnName + "\"	length=\"20\" /> ";
			break;
		case Constants.ENUM:
			ret = " <property name=\"" + columnName + "\" type=\"long\" column=\"" + columnName + "\"	length=\"20\" /> ";
		}
		
		return ret;
	}

	/**
	 * 转换元数据类型为元数据表的列名
	 */
	public static String getTrueType(byte type) {
		switch (type) {
		case 1:
			return "avarchar";			
		case 2:
			return "integer";
		case 3:
			return "decimal";
		case 4:
			return "date";
		case 5:
			return "datetime";
		case 6:
			return "text";
		case 7:
			return "boolean";
		case 8:
			return "reference";
		case 9:
			return "reference";
		case 13: return "enum";
		default:
			return null;
		}
	}

	// ctrl中保存元数据之前进行类型转换
	@SuppressWarnings("unchecked")
	public static Comparable getTrueTypeValue(String name, String value)
			throws ParseException {
		if("".equals(value))
			return null;
		
		if (name.startsWith("avarchar") || name.startsWith("text")) {
			return value;
		} else if (name.startsWith("integer")) {
			return Integer.parseInt(value);
		} else if (name.startsWith("decimal")) {
			return Double.parseDouble(value);
		} else if (name.startsWith("date")) {
			if(!"".equals(value)){
				if (name.startsWith("datetime"))
					if("00".equals(value))
						return "";
					else
						return java.sql.Timestamp.valueOf(value.trim().length() > 16 ? value : (value + ":00"));
				else {
					return Datetimes.parseDate(value);
				}
			}
		} else if (name.startsWith("boolean")) {
			if (value.equals("1"))
				return true;
			else {
				return false;
			}
		}else if (name.startsWith("reference") || name.startsWith("enum")) {
			return Long.parseLong(value);
		}
		
		return value;
	}
	
	private static Map<Byte, Class> Metadata_Types = null;
	static {
		Metadata_Types = new HashMap<Byte, Class>();
		
		Metadata_Types.put((byte)1, String.class);
		Metadata_Types.put((byte)2, Integer.class);
		Metadata_Types.put((byte)3, Double.class);
		Metadata_Types.put((byte)4, Date.class);
		Metadata_Types.put((byte)5, Timestamp.class);
		Metadata_Types.put((byte)6, String.class);
		Metadata_Types.put((byte)7, Boolean.class);
		Metadata_Types.put((byte)0, Long.class);
		Metadata_Types.put((byte)10, Long.class);
		Metadata_Types.put((byte)11, String.class);
		Metadata_Types.put((byte)12, String.class);
		Metadata_Types.put((byte)13, Integer.class);
	}

	/**
	 * 根据元数据类型转换为java类名
	 */
	public static String getType4Ctrl(byte type) {
		Class clazz = Metadata_Types.get(type);
		return clazz == null ? String.class.getCanonicalName() : clazz.getCanonicalName();
	}
	
	public static Class getClazz4Ctrl(byte type) {
		Class clazz = Metadata_Types.get(type);
		return clazz == null ? String.class : clazz;
	}

	// 标题栏的宽度
	public static int DOC_TABLE_LIMITED_WIDTH_SUBJECT = 30;
	
//	public static String getLength(byte type) {
//
//		switch (type) {
//		case 1:
//			return "200";
//		case 2:
//			return "11";
//		case 3:
//			return "10";
//		case 4:
//			return "10";
//		case 5:
//			return "19";
//		case 6:
//			return "65535";
//		case 7:
//			return "4";
//		case 8:
//			return "20";
//		case 9:
//			return "20";
//		default:
//			return null;
//		}
//	}

	/**
	 * 文档库类型转换为国际化key
	 */
	public static String getDocLibType(byte the_type){
		String docLibType=null;
		switch (the_type){
			case 0 : docLibType="doc.lib.type.customer";	//自定义
						break;
			case 1 : docLibType="doc.lib.type.user";		//个人
						break;
			case 2 : docLibType="doc.lib.type.company";	//单位
						break;
			case 3 : docLibType="doc.lib.type.edoc";		//公文
						break;
			case 4 : docLibType="doc.lib.type.program";
						break;
			case 5 : 
			        //政务多组织版
                    if((Boolean)Functions.getSysFlag("sys_isGovVer")){            
                        docLibType = "doc.lib.type.group.GOV";
                    }
                    else{
                        docLibType="doc.lib.type.group";
                    }
                    break;
			default : docLibType=null;
		}
		return docLibType;
	}
	
	/**
	 * 得到提醒类型的key
	 */
	public static String getAlertTypeKey(byte changeType) {
		switch(changeType) {
			case Constants.ALERT_OPR_TYPE_FORUM:
				return Constants.ALERT_OPR_TYPE_FORUM_KEY;
			case Constants.ALERT_OPR_TYPE_ADD:
				return Constants.ALERT_OPR_TYPE_ADD_KEY;
			case Constants.ALERT_OPR_TYPE_EDIT:
				return Constants.ALERT_OPR_TYPE_EDIT_KEY;
			case Constants.ALERT_OPR_TYPE_DELETE:
				return Constants.ALERT_OPR_TYPE_DELETE_KEY;
			default:
				return Constants.ALERT_OPR_TYPE_EDIT_KEY;
		}
	}
	
	/**
	 * 得到发送学习区的在线消息key
	 * 
	 */
	public static String getLearingAddKey(String orgType){
		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType))
			return "doc.alert.learning.add.personal";
		else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType))
			return "doc.alert.learning.add.dept";
		else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(orgType))
			return "doc.alert.learning.add.account";
		else if(Constants.ORGENT_TYPE_GROUP.equals(orgType))
			return "doc.alert.learning.add.group";
		
		return "";
	}


	
	/**
	 * 得到扩展元数据的国际化资源文件
	 * 
	 * 07.09.05 
	 * 程序先从SeeyonCommonResources国际化，没有的时候调用这个方法重新国际化
	 */
	public static String getResourceNameOfMetadata(String defName, String value){
		if(defName == null || value == null)
			return "";		
		else if(defName.startsWith("doc.") || value.startsWith("doc."))
			return Constants.RESOURCE_BASENAME;
		else if(defName.startsWith("inquiry.") || value.startsWith("inquiry."))
			return Constants.RESOURCE_BASENAME_INQUIRY;
		else if(defName.startsWith("mt.") || value.startsWith("mt."))
			return Constants.RESOURCE_BASENAME_MEETING;
		else if(defName.startsWith("news.") || value.startsWith("news."))
			return Constants.RESOURCE_BASENAME_NEWS;
		else if(defName.startsWith("project.") || value.startsWith("project."))
			return Constants.RESOURCE_BASENAME_PROJECT;
		else if(defName.startsWith("edoc.") || value.startsWith("edoc."))
			return Constants.RESOURCE_BASENAME_EDOC;
		
		return "";
	}
	
	private static List<String> RESOURCES_METADATA = FormBizConfigUtils.newArrayList(
		Constants.RESOURCE_BASENAME, Constants.COMMON_RESOURCE_BASENAME, 
		Constants.RESOURCE_BASENAME_INQUIRY, Constants.RESOURCE_BASENAME_MEETING, Constants.RESOURCE_BASENAME_NEWS,
		Constants.RESOURCE_BASENAME_PROJECT, Constants.RESOURCE_BASENAME_EDOC);
	
	/**
	 * 得到所有DocMetadataDefinition 用到的资源文件
	 */
	public static List<String> getResourceNamesOfMetadataDef(){
		return RESOURCES_METADATA;
	}
	
	/**
	 * 得到某个用户所有的组织模型实体id
	 */
	public static String getOrgIdsOfUser(long userId){
		List<Long> ids = getOrgIdsOfUser1(userId);
		return StringUtils.join(ids, ',');
	}
	
	public static List<Long> getOrgIdsOfUser1(long userId){
		return FormBizConfigUtils.getUserDomainIds(userId, getOrgManager());
	}
	
	/**
	 * 根据String串解析为Set<Long>
	 */
	public static Set<Long> parseStrings2Longs(String sIds, String flag){
		Set<Long> set = new HashSet<Long>();
		if(Strings.isBlank(sIds))
			return set;
		String[] arr = sIds.split(flag);
		for(String s : arr){
			Long l = Long.valueOf(s);
			set.add(l);
		}
		return set;
	}
	
	/**
	 * 得到DocResource对象（仅包含id）
	 */
	public static Set<DocResource> getDocsByIds(String sIds, String flag){
		Set<DocResource> set = new HashSet<DocResource>();
		if(Strings.isBlank(sIds))
			return set;
		String[] arr = sIds.split(flag);
		for(String s : arr){
			Long l = Long.valueOf(s);
			set.add(new DocResource(l));
		}
		return set;
	}
	
	/**
	 * 得到组织模型实体的名称
	 */
	public static String getOrgEntityName(String orgType, long orgId, boolean needAccountShort){
		String name = "";
		try {
			V3xOrgEntity entity = getOrgManager().getEntity(orgType, orgId);
			boolean accFlag = needAccountShort;
			User user = CurrentUser.get();
			if(user != null){
				long accId = user.getAccountId();
				if(accId != entity.getOrgAccountId())
					accFlag = true;
			}
			if(entity != null){
				name = entity.getName();
				if(accFlag){
					V3xOrgAccount account = getOrgManager().getAccountById(entity.getOrgAccountId());
					if(account != null){
						name += "(" + account.getShortname() + ")";
					}						
				}
			}
		} catch(Exception e){
			
		}
		
		return name;
	}
	
	/**
	 * 根据应用枚举key得到知识管理中对应的类型
	 */
	public static Long getContentTypeIdByAppEnumKey(int key){
		if(key == ApplicationCategoryEnum.collaboration.key())
			return Constants.SYSTEM_COL;
		else if(key == ApplicationCategoryEnum.edoc.key())
			return Constants.SYSTEM_ARCHIVES;
		else if(key == ApplicationCategoryEnum.plan.key())
			return Constants.SYSTEM_PLAN;
		else if(key == ApplicationCategoryEnum.meeting.key())
			return Constants.SYSTEM_MEETING;
		else if(key == ApplicationCategoryEnum.bulletin.key())
			return Constants.SYSTEM_BULLETIN;
		else if(key == ApplicationCategoryEnum.news.key())
			return Constants.SYSTEM_NEWS;
		else if(key == ApplicationCategoryEnum.inquiry.key())
			return Constants.SYSTEM_INQUIRY;
		else if(key == ApplicationCategoryEnum.mail.key())
			return Constants.SYSTEM_MAIL;
		//branches_a8_v350_r_gov GOV-1399 魏俊彪 信息报送  得到类型定义 start
		else if(key == ApplicationCategoryEnum.info.key())
			return Constants.SYSTEM_INFO;
		//branches_a8_v350_r_gov GOV-1399 魏俊彪 信息报送  得到类型定义 end
		return null;
	}
	
	/**
	 * 判断某种类型是否可以归档
	 */
	public static boolean canPigeonholeByAppKey(int key){
		if(key == ApplicationCategoryEnum.collaboration.key())
			return true;
		else if(key == ApplicationCategoryEnum.edoc.key())
			return true;
		else if(key == ApplicationCategoryEnum.plan.key())
			return false;
		else if(key == ApplicationCategoryEnum.meeting.key())
			return true;
		else if(key == ApplicationCategoryEnum.bulletin.key())
			return true;
		else if(key == ApplicationCategoryEnum.news.key())
			return true;
		else if(key == ApplicationCategoryEnum.inquiry.key())
			return true;
		else if(key == ApplicationCategoryEnum.mail.key())
			return false;
		
		return false;
	}
	
	/**
	 * 判断一个组织模型实体的状态是否有效
	 * 涉及 删除、离职等状态
	 */
	public static boolean isValidOrgEntity(String orgType, long orgId){
		// 调用组织模型接口实现 isValid()
		try {
			V3xOrgEntity entity = getOrgManager().getEntity(orgType + "|" + orgId);
			return entity != null && entity.isValid();
		} catch(Exception e) {
			return false;
		}		

	}
	
	/**
	 * 从系统标志 SysFlag 取当前版本的参数值
	 * 是否显示集团文档库
	 */
	public static boolean isShowGroupLib(){
		try{
			// 版本
			boolean showGroupLib = (Boolean)(SysFlag.doc_showGroupLib.getFlag());
			// 外部人员
			boolean isInternal = false;
			User user = CurrentUser.get();
			if(user != null && user.isInternal())
				isInternal = true;
			
            //是否是集团下的单位
			boolean isAccountInGroup = false;

            if(getOrgManager() != null){  
                isAccountInGroup = MainHelper.isAccountInGroup(getOrgManager(), user.getLoginAccount());
            }
            
			return (showGroupLib && isInternal && isAccountInGroup);
		}catch(Exception e){
			return false;
		}
	}
	/**
	 * 从系统标志 SysFlag 取当前版本的参数值
	 * 单位管理员菜单是否显示RSS页签
	 */
	public static boolean showRssTagOnAccountAdmin(){
		try{
			boolean showRssTag = (Boolean)(SysFlag.doc_showRssTag.getFlag());
			boolean rssEnabled = Constants.rssModuleEnabled();
			return (showRssTag && rssEnabled);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 判断是否集团版
	 * 
	 */
	public static boolean isGroupVer(){
		return (Boolean)(SysFlag.sys_isGroupVer.getFlag());
	}
	
	/**
	 * 判断是否政务版
	 * 
	 */
	public static boolean isGovVer(){
		return (Boolean)(SysFlag.sys_isGovVer.getFlag());
	}
	
    private static SystemConfig systemConfig = null;
    private static SystemConfig getSystemConfig(){
        if(systemConfig == null){
        	systemConfig = (SystemConfig)ApplicationContextHolder.getBean("systemConfig");
        }
        return systemConfig;
    }
	
	/**
	 * 从系统开关处取得RSS模块是否启用标记
	 */
	public static boolean rssModuleEnabled(){
		boolean flag = false;
		try {
			String value = getSystemConfig().get(IConfigPublicKey.RSS_ENABLE);
			flag = ("enable".equals(value));
			//flag = (!Constants.isGovVer() && flag);
		} catch (Exception e) {
			
		}
		
		return flag;
	}
	
	/**
	 * 从系统开关处取得公文模块是否启用标记
	 */
	public static boolean edocModuleEnabled(ConfigManager... paramManager){
		return com.seeyon.v3x.common.taglibs.functions.Functions.isEnableEdoc();
//		boolean flag = false;
//		try {
//			ConfigManager configManager = null;
//			if(paramManager != null && paramManager.length > 0){
//				configManager = paramManager[0];
//			}else
//				configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
//			ConfigItem item = configManager.getConfigItem(Constants.SYSTEM_SWITCH_EDOC_ENABLED_CATEGORY, 
//					Constants.SYSTEM_SWITCH_EDOC_ENABLED_KEY);
//			String value = item.getConfigValue();
//			flag = ("enable".equals(value));
//		} catch (Exception e) {
//			
//		}
		
//		return flag;
	}
	
	/**
	 * 是否集团管理员登录
	 */
	public static boolean isGroupAdmin(){
		try {
			String loginName = CurrentUser.get().getLoginName();
			if(getOrgManager().isGroupAdmin(loginName))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 文件替换的时候页面组件的标记
	 */
	public static final String FILEUPLOAD_INPUT_NAME_fileUrl = "file_fileUrl";

	public static final String FILEUPLOAD_INPUT_NAME_mimeType = "file_mimeType";

	public static final String FILEUPLOAD_INPUT_NAME_size = "file_size";

	public static final String FILEUPLOAD_INPUT_NAME_createDate = "file_createDate";

	public static final String FILEUPLOAD_INPUT_NAME_filename = "file_filename";

	public static final String FILEUPLOAD_INPUT_NAME_type = "file_type";

	public static final String FILEUPLOAD_INPUT_NAME_needClone = "file_needClone";

	public static final String FILEUPLOAD_INPUT_NAME_description = "file_description";

	public static final String FILEUPLOAD_INPUT_NAME_GenesisId = "file_genesisId";
	
	/**
	 * 判断是否需要从元数据取数据
	 * @return null 说明不需要走元数据组件
	 */
	public static MetadataNameEnum getMetadataNameEnum(String detailName, String value, long docType){		
		if(detailName == null || value == null || docType != Constants.SYSTEM_ARCHIVES)
			return null;
		
		if("edoc.element.sendtype".equals(detailName))
			return MetadataNameEnum.edoc_send_type;
		if("edoc.element.urgentlevel".equals(detailName))
			return MetadataNameEnum.edoc_urgent_level;
		if("edoc.element.keepperiod".equals(detailName))
			return MetadataNameEnum.edoc_keep_period;
		if("edoc.element.secretlevel".equals(detailName))
			return MetadataNameEnum.edoc_secret_level;
		if("edoc.element.doctype".equals(detailName))
			return MetadataNameEnum.edoc_doc_type;
		
		return null;
	} 
	
	/**
	 * 数据库方言标准
	 */
	public static final String MySQLDialect = "org.hibernate.dialect.MySQLDialect";
	public static final String OracleDialect = "org.hibernate.dialect.OracleDialect";
	public static final String SQLServerDialect = "org.hibernate.dialect.SQLServerDialect";
	
	/**
	 * 返回数据库类型
	 */
	public static String getDBType(){
		SystemProperties sys = SystemProperties.getInstance();
		if(sys.getProperty("db.hibernateDialect").equalsIgnoreCase(Constants.MySQLDialect))
			return "mysql";
		if(sys.getProperty("db.hibernateDialect").equalsIgnoreCase(Constants.OracleDialect))
			return "oracle";
		if(sys.getProperty("db.hibernateDialect").equalsIgnoreCase(Constants.SQLServerDialect))
			return "sqlserver";
		return null;
	}
	
	public static final long MENU_ID_MY_PLAN = 601L;
	
	/**
	 * 判断用户是否有 我的计划 菜单
	 */
	public static boolean hasMenuMyPlanOfCurrentUser(){
		boolean ret = false;
		try{
			ret = MenuFunction.hasMenu(Constants.MENU_ID_MY_PLAN);
		}catch(Exception e){
		
		}
		
		return ret;
	}
	
	
	/**
	 * 取得可用的docResource的id
	 * @deprecated		文档ID不再使用自增，使用UUID
	 */
	@SuppressWarnings("unchecked")
	public static long getNewDocResourceId(DocResourceDao dao, AutoIncrementManager autoIncrementManager){
		if(autoIncrementManager == null)
			return 0;
		
		if(!autoIncrementManager.existClass(DocResource.class.getName())){
			String hql = "select max(id) from DocResource ";
			List list = dao.find(hql);
			long start = 1L;
			if(list == null || list.size() == 0){
				log.info("doc_resources 表没有数据");
			}else{
				start = (Long)(list.get(0)) + 1;
			}			

			autoIncrementManager.addOneRecord(DocResource.class.getName(), start);
		}
		
		Long ret = autoIncrementManager.getValue(DocResource.class.getName());
		if(ret == null)
			return 0;
		else
			return ret;
	}
	
	/**
	 * 操作类型枚举
	 */
	public static enum OperEnum {
		add,
		edit,
		delete
	}
	
	/**
	 * 判断一种内容类型的名称是否需要国际化，即是否预置类型 
	 */
	public static boolean needI18n(long type) {
		Set<Long> types = new HashSet<Long>();
		types.add(Constants.FOLDER_MINE);
		types.add(Constants.FOLDER_CORP);
		types.add(Constants.ROOT_ARC);
		types.add(Constants.FOLDER_ARC_PRE);
		types.add(Constants.FOLDER_PROJECT_ROOT);		
		types.add(Constants.FOLDER_PLAN);
		types.add(Constants.FOLDER_TEMPLET);
		types.add(Constants.FOLDER_SHARE);
		types.add(Constants.FOLDER_BORROW);
		types.add(Constants.FOLDER_PLAN_DAY);
		types.add(Constants.FOLDER_PLAN_MONTH);
		types.add(Constants.FOLDER_PLAN_WEEK);
		types.add(Constants.FOLDER_PLAN_WORK);
		types.add(Constants.DEPARTMENT_BORROW);
		types.add(Constants.ROOT_GROUP);
		types.add(Constants.FOLDER_EDOC);		
		
		return (types.contains(type));		
	}
	
	/**
	 * 特殊字符的特殊处理 	unCharactor		: "\"\\/|><:*?",
	 * 因为早期的特殊字符限制不够，所以导致下载的时候出现错误，需要特别处理
	 */
	public static String dealUnChar(String str){
		String ret = str;
		ret = str.replace("\"", "“");
		ret = ret.replace("\\", "_"); 
		ret = ret.replace("/", "_");
		ret = ret.replace("|", "_");
		ret = ret.replace(">", "》");
		ret = ret.replace("<", "《");
		ret = ret.replace(":", "：");
		ret = ret.replace("*", "_");
		ret = ret.replace("?", "？");
		
		return ret;
	}
	
	/**
	 * 是否上传的图片
	 */
	public static boolean isImgFile(Long id){
		if(id == null)
			return false;
		if(id == 106||id == 109 ||id ==110||id==111 ||id==112||id==117||id==118) {
			return true;
		}else
			return false;
		
	}
	 /**
	  * 是否是office格式文档
	  */
	
	public static boolean isOffice(Long id){
		if(id == null)
			return false;
		if(id == 23||id == 24 ||id ==25||id==26 ) {
			return true;
		}else
			return false;
		
	}
	
	// A8知识管理基础目录文档夹名称
	public static final String DOC_BASE_FOLDER = "doc"; 
	/**
	 * 得到A8知识管理基础目录C:\Program Files\UFseeyon\A8\Group\base\doc
	 */
	public static String getA8DocBaseFolder(){
		String sysBase = SystemEnvironment.getA8BaseFolder();
		String ret = Strings.getCanonicalPathAndCreate(sysBase + File.separator + Constants.DOC_BASE_FOLDER);
		return ret;
	}
	
	/**
	 * 得到DocMetadata.hbm.xml路径
	 */
	public static String getCanonicalPathOfDynamicHbm(String mappingName){
		String docBase = Constants.getA8DocBaseFolder();
		String ret = docBase + File.separator + mappingName;
		return ret;
	}
	
	/**
	 * 得到DocMetadata.hbm.xml 的头
	 */
	public static String getStartStringOfDocMetadata(){
		StringBuffer ret = new StringBuffer("<?xml version=\"1.0\"?>");
		ret.append("<!DOCTYPE hibernate-mapping PUBLIC"); 
		ret.append("    \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"");
		ret.append("    \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">");

		ret.append("<hibernate-mapping>");
		ret.append("	<class entity-name=\"DocMetadata\" table=\"doc_metadata\">");
		ret.append("		<id name=\"docResourceId\" type=\"long\" column=\"doc_resource_id\"");
		ret.append("				length=\"20\">");
		ret.append("			<generator class=\"assigned\" />");
		ret.append("		</id>");
		
		return ret.toString();
	}
	
	/**
	 * 得到DocMetadata.hbm.xml 的尾
	 */
	public static String getEndStringOfDocMetadata(){
		StringBuffer ret = new StringBuffer("<!-- edit flag,do not delete !!! -->");
		ret.append("</class>");
		ret.append("</hibernate-mapping>");		
		return ret.toString();
	}
	
	/**
	 * 判断元数据是否系统预置
	 */
	public static boolean isSystemMetaId(long id){
		return (id > 0L && id < 200L);
	} 
	
	/**
	 * 正文类型常量HashMap定义
	 */
	public static Map<String, String> EDITOR_TYPES = new HashMap<String, String>();
	static {
		EDITOR_TYPES.put("editorHtml", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		EDITOR_TYPES.put("editorWord", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD);
		EDITOR_TYPES.put("editorExcel", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL);
		EDITOR_TYPES.put("editorWpsWord", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD);
		EDITOR_TYPES.put("editorWpsExcel", com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL);
	}
	
	/**
	 * 正文类型为Office或WPS类型
	 */
	public static final List<String> EDITOR_OFFICE_WPS = Arrays.asList(
			com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD,
			com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL,
			com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD,
			com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL);
	
	/**
	 * 得到系统应用枚举类型的国际化名称
	 */
	public static String getAppEnumI18nValue(String key){
		return ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, "application." + key + ".label");
	}
	
	/**
	 * 得到文档I18N的值
	 * @param key	国际化key
	 */
	public static String getDocI18nValue(String key, Object... params){
		return ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, key, params);
	}
	
	public static String getCommonI18nValue(String key, Object... params){
		return ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, key, params);
	}
	
	/**
	 * 修改文档夹是否允许评论属性时，影响范围：仅自身，其值为1
	 */
	public static int SCOPE_SELF = 1;
	/**
	 * 修改文档夹是否允许评论属性时，影响范围：自身及其一级子文件，其值为2
	 */
	public static int SCOPE_LEV1_CHILDS = 2;
	/**
	 * 修改文档夹是否允许评论属性时，影响范围：自身及其所有层级的全部子文件(夹)，其值为3
	 */
	public static int SCOPE_ALL = 3;
	
	/** 是否查看历史版本标识，其值为："HistoryVersion" */
	public static final String VERSION_FLAG = "HistoryVersion";
	

	/**
	 * 
	 */
	public static enum PigeonholeType{
		edoc_account,  //单位归档公文
		edoc_dept     //部门归档公文
	}
	
	public static final byte DOC_LIB_ENABLED = 1;
	public static final byte DOC_LIB_DISABLED = 0;
	public static final byte DOC_LIB_ALL = -1;
	
	/**
	 * 判断当前文档库状态是否与所要判断的状态一致
	 * 如果不区分状态，则不做判断，直接定为一致
	 * @param lib		文档库
	 * @param status	状态
	 */
	public static boolean validateStatus(DocLib lib, byte status) {
		return lib != null && (status == DOC_LIB_ALL || lib.getStatus() == status);
	}
	
	public static Set<Integer> aclLevels4Index = new HashSet<Integer>();
	static {
		aclLevels4Index.add(Constants.ALLPOTENT);
		aclLevels4Index.add(Constants.EDITPOTENT);
		aclLevels4Index.add(Constants.READONLYPOTENT);
		aclLevels4Index.add(Constants.BROWSEPOTENT);
	}
	
	public static boolean blogEnabled() {
		SystemConfig systemConfig = (SystemConfig)ApplicationContextHolder.getBean("systemConfig");
		String enableBlog = systemConfig.get(IConfigPublicKey.BLOG_ENABLE);
		return enableBlog != null && "enable".equals(enableBlog);
	}
	
	/**
	 * 文档锁定状态枚举
	 * <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-5-19
	 */
	public static enum LockStatus {
		/**
		 * 文档应用锁：这种锁不依赖于具体的操作（比如编辑文档）。<br>
		 * 其操作方式一般是持有对文档全部权限的人员，<br>
		 * 直接选中文档并进行锁定操作，避免其他用户对其进行编辑或删除操作。<br>
		 */
		AppLock(0),
		/**
		 * 文档同步操作锁：这种锁依赖于于具体的操作（比如编辑文档）。<br>
		 * 当用户编辑文档时，进行加锁，完成编辑操作之后即自动解锁。<br>
		 * 对异常登出的情况，通过登录时间变化进行解锁与否的校验。<br>
		 */
		ActionLock(1),
		/**
		 * 无任何锁定
		 */
		None(2),
		/**
		 * 特别情况：文档已被删除
		 */
		DocInvalid(3);
		
		private int key;
		private LockStatus(int key) {
			this.key = key;
		}
		
		public int key() {
			return this.key;
		}
		
		public static LockStatus valueOf(int key) {
			LockStatus[] statuses = LockStatus.values();
			for(LockStatus status : statuses) {
				if(status.key() == key) {
					return status;
				}
			}
			throw new IllegalArgumentException("未定义的锁定状态枚举类型[key=" + key + "]");
		}
	}
	
	/**
	 * 文档未被锁定时的提示消息
	 */
	public static final String LOCK_MSG_NONE = "NotLocked";

}