 package com.seeyon.v3x.doc.manager;

import java.util.*;

import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;

/**
 * 
 * @author xcs
 * 内容类型Manager
 * 
 */
public interface ContentTypeManager {
	
	/**
	 * 初始化ContentTypeManager，将内容类型及相关信息装载到内存中。
	 */
	public void init();
	
	/**
	 * 新增内容类型
	 * 
	 * @param contentType内容类型基本信息
	 * @param contentTypeDetails内容类型元数据关系信息
	 */
	public void addContentType(DocType contentType,List<DocTypeDetail> contentTypeDetails);
	
	/**
	 * 删除内容类型
	 * 
	 * @param id内容类型id
	 */
	public void deleteContentType(Long id) ;
	/**
	 * 停用内容类型
	 * @param id内容类型id
	 */
	
	public void disableContentType(Long id);

	/**
	 * 根据id查找内容类型
	 * 
	 * @param id内容类型id
	 * @return
	 */
	public DocType getContentTypeById(Long id);

	/**
	 * 修改保存内容类型
	 * 
	 * @param contentType内容类型基本信息
	 * @param contentTypeDetails内容类型元数据关系信息
	 */
	public void updateContentType(DocType contentType,boolean newName ,String oldName);	
	
	/**
	 * 修改内容类型标记为使用状态
	 */
	public void setContentTypePublished(long id);

	/**
	 * 根据文档类型详细信息id取元数据id
	 * 
	 * @param id
	 * @return
	 */
	public Long getMetadataDefIdByDocDetailId(Long id);

	/**
	 * 获取所有内容类型(内容类型管理中调用)
	 * 
	 * @return List
	 */
	public List<DocType> getContentTypes();
	
	/**
	 * 获取内容类型列表（选择文档库内容类型时调用）
	 * 
	 * @return List
	 */
	public List<DocType> getContentTypesForNew();
	
	/**
	 * 获取所有可查询内容类型
	 * 
	 * @return
	 */
	public List<DocType> getAllSearchContentType();
	
	/**
	 * 根据DocTypeId获取对应的所有元数据定义
	 * @param docTypeId
	 * @return List
	 */
//	public List<DocMetadataDefinition> getMetadataDefinitionByDocTypeId(long docTypeId);
	
	/**
	 * 根据DocTypeId获取对应的所有元数据定义
	 * @param contentTypeId
	 * @return List
	 */
	public List<DocTypeDetail> getContentTypeDetails(long contentTypeId);
	
	/**
	 * 判断一种内容类型是否有扩展元数据
	 */
	public boolean hasExtendMetadata(long type);
	
	/**
	 * 验证给定名称的内容类型是否存在(新建内容类型时调用)。
	 * @param typeName
	 * @return true-存在;false-不存在
	 */
	public boolean containDocType(String typeName);
	
	/**
	 * 验证内容类型名称是否重复(修改用户自定义内容类型时调用)。
	 * @param typeName
	 * @param typeId
	 * @return true-存在;false-不存在
	 */
	public boolean containDocType(String typeName, long typeId);
	
	/**
	 * 取得综合查询需要的内容类型
	 */
	public List<DocType> getContentTypesForISearch();
	
	/**
	 * 判断一个内容类型是否已经使用
	 */
	public boolean isUsed(long docTypeId);

}
