package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;

/**
 * 文档历史版本信息业务逻辑接口，主要包括文档历史版本的添加、查询、修改、删除及恢复等操作
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-22
 */
public interface DocVersionInfoManager {

	/**
	 * 获取某一文档的所有（分页情况下）历史版本信息
	 * @param docResId	文档ID
	 * @return	所有历史版本信息
	 */
	public List<DocVersionInfo> getAllDocVersion(Long docResId);
	
	/**
	 * 获取某一文档的符合指定搜索条件的所有（分页情况下）历史版本信息
	 * @param docResId	文档ID
	 * @param sm	搜索类型及值
	 * @return	符合条件的历史版本信息记录
	 */
	public List<DocVersionInfo> getAllDocVersion(Long docResId, SearchModel sm);
	
	/**
	 * 判定某一文档是否具备历史版本信息<br>
	 * 主要用于在进入历史版本信息列表页面时，做必要性判断：<br>
	 * 如果该文档根本未经任何修改，也因此没有至少一个历史版本信息，那么此列表页面也无需打开<br>
	 * @param docResId	文档ID
	 */
	public boolean hasDocVersion(Long docResId);
	
	/**
	 * 根据文档历史版本信息ID获取对应记录
	 * @param docVersionId	文档历史版本信息ID
	 * @return 	文档历史版本信息记录
	 */
	public DocVersionInfo getDocVersion(Long docVersionId);
	
	/**
	 * 修改文档历史版本信息的版本注释内容，供前端进行AJAX调用
	 * @param docVersionId		文档历史版本信息ID
	 * @param versionComment	版本注释
	 */
	public void updateVersionComment(Long docVersionId, String versionComment);
	
	/**
	 * 将选中的某一版本记录恢复为最新记录，供前端进行AJAX调用<br>
	 * 其策略是：<br>
	 * 1.以目前最新记录生成一份新的历史版本信息记录；<br>
	 * 2.将选中的历史版本相关信息（基本信息、正文、附件、元数据等）复制到对应的文档，以实现恢复。<br>
	 * @param docVersionId		选中的文档历史版本信息ID
	 * @param docResId			主文档ID
	 */
	public Boolean[] replaceVersion2Latest(Long docVersionId, Long docResId);
	
	/**
	 * 删除选中的文档历史版本信息
	 * @param ids	选中的文档历史版本信息ID以","拼接起来的字符串	
	 */
	public void delete(String ids);

	/**
	 * 保存文档历史版本信息，包含了基本文档信息、正文信息、附件信息、元数据信息等
	 * @param dvi
	 */
	public void saveDocVersionInfo(DocVersionInfo dvi);
	
	/**
	 * 根据版本管理启用情况决定是否保存历史版本信息
	 * @param versionComment	启用版本管理时的版本注释
	 * @param originalFileId		office正文保存时，需先备份原先版本，此为备份文件的fileId	
	 * @param drs	主文档
	 */
	public void saveDocVersionInfo(String versionComment, String originalFileId, DocResource drs);
	
	/**
	 * 根据版本管理启用情况决定是否保存历史版本信息
	 * @param versionComment	启用版本管理时的版本注释
	 * @param drs	主文档
	 */
	public void saveDocVersionInfo(String versionComment, DocResource drs);
	
	/**
	 * 校验指定的文档历史版本是否存在，供前端进行AJAX调用
	 * @param docVersionId	文档历史版本信息ID
	 * @return	是否存在
	 */
	public boolean isDocVersionExist(Long docVersionId);
	
	/**
	 * 根据文档ID删除其对应的所有历史版本信息记录(包括历史版本信息对应的附件、元数据信息等)
	 * @param drId	文档ID
	 */
	public void deleteByDocResourceId(Long drId);

}
