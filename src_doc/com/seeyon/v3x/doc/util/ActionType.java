package com.seeyon.v3x.doc.util;

/**
 * 文档日志的操作类型
 */
public class ActionType {

	//actionType
	public static final String LOG_DOC_ADD_LIB = "log.doc.add.lib";			//新建文档库
	public static final String LOG_DOC_EDIT_LIB = "log.doc.edit.lib";			//编辑文档库属性
	public static final String LOG_DOC_EDIT_LIB_OWNER = "log.doc.edit.lib.owner";	//修改文档库管理员	
	public static final String LOG_DOC_REMOVE_LIB = "log.doc.remove.lib";		//删除文档库
	public static final String LOG_DOC_ADD_FOLDER = "log.doc.add.folder";		//新建文档夹
	public static final String LOG_DOC_RENAME_FOLDER = "log.doc.rename.folder";	//重命名文档夹	
	public static final String LOG_DOC_EDIT_FOLDER = "log.doc.edit.folder";		//编辑文档夹属性
	public static final String LOG_DOC_MOVE_FOLDER_IN = "log.doc.move.folder.in";		//文档夹移进
	public static final String LOG_DOC_MOVE_FOLDER_OUT = "log.doc.move.folder.out";		//文档夹移出
	public static final String LOG_DOC_REMOVE_FOLDER = "log.doc.remove.folder";	//删除文档夹
	public static final String LOG_DOC_ADD_DOCUMENT = "log.doc.add.document";		//新增文档
	public static final String LOG_DOC_RENAME_DOCUMENT = "log.doc.rename.document";	//重命名文档
	public static final String LOG_DOC_EDIT_DOCUMENT = "log.doc.edit.document";	//编辑文档属性
	public static final String LOG_DOC_MOVE_DOCUMENT_IN = "log.doc.move.document.in";	//移进文档
	public static final String LOG_DOC_MOVE_DOCUMENT_OUT = "log.doc.move.document.out";	//移出文档
	public static final String LOG_DOC_REMOVE_DOCUMENT = "log.doc.remove.document";	//删除文档
	public static final String LOG_DOC_ADD_SHORTCUT = "log.doc.add.shortcut";		//创建快捷方式
	public static final String LOG_DOC_FORWARD = "log.doc.forward";			//转发文档
	public static final String LOG_DOC_DOWNLOAD = "log.doc.download";			//下载文档
	public static final String LOG_DOC_UPLOAD = "log.doc.upload";			//上传文档
	public static final String LOG_DOC_PRINT = "log.doc.print";			//打印文档
	public static final String LOG_DOC_SAVE = "log.doc.save";			//保存文档
	public static final String LOG_DOC_PUBLISH = "log.doc.publish";			//发布首页
	public static final String LOG_DOC_REPLACE = "log.doc.replace";			//文件替换
	public static final String LOG_DOC_SHARE = "log.doc.share";			//文档夹共享
	public static final String LOG_DOC_LEND = "log.doc.lend";				//文档借阅
	public static final String LOG_DOC_ADD_LINK = "log.doc.add.link";			//新增关联文档
	public static final String LOG_DOC_REMOVE_LINK = "log.doc.remove.link";		//删除关联文档
	public static final String LOG_DOC_EDIT_DOCUMENT_BODY = "log.doc.edit.document.body";		//修改正文
	public static final String LOG_DOC_VIEW = "log.doc.view";     // 查看文档
	
	public static final String LOG_DOC_PIGEONHOLE = "log.doc.pigeonhole";		//归档
	
	public static final String LOG_DOC_DELETE_VERSION = "log.doc.delete.version";		//删除历史版本信息
	public static final String LOG_DOC_REPLACE_VERSION = "log.doc.replace.version";		//恢复历史版本信息为最新版本
	
}
