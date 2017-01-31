package com.seeyon.v3x.common.office.trans.manager;

import java.util.Date;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * Office转换管理。
 * 
 * @author wangwenyou
 * 
 */
public interface OfficeTransManager {
	/**
	 * 生成指定Office文件（同步模式）。用于点击链接文件不存在时，需要等待。
	 * 
	 * @param id
	 *            文件Id。
	 * @param isImmediately 是否立即转换，以下情况优先处理：查看时、新建office正文保存
	 * @throws BusinessException
	 */
	void generate(long fileId, Date v3xFileCreateDate, boolean isImmediately);

	/**
	 * 访问转换文件，访问计数加1。
	 * 
	 * @param id
	 *            文件Id。
	 */
	void visit(long id);

	/**
	 * 清理不常访问的转换文件。 
	 */
	void clean();
	/**
	 * 清除指定的转换文件。
	 * @param id 文件Id。
	 * @param date yyyyMMdd
	 */
	void clean(long id,String date);	
	
	/**
	 * 判断指定的Office文件的Html缓存是否存在。
	 * @param id 文件Id。
	 * @param date yyyyMMdd
	 * @return 存在返回<tt>true</tt>.
	 */
	boolean isExist(long id, String date);
	/**
	 * 
	 * 生成原文件的下载链接。
	 */
	String buildSourceDownloadUrl(long id);
	
	public String getOutputPath();

	public long getFileMaxSize();
}
