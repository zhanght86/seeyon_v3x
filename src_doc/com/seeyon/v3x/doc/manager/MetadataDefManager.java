package com.seeyon.v3x.doc.manager;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.util.Constants.OperEnum;
/**
 * 
 * @author xcs
 *
 */
public interface MetadataDefManager {
	
	/**
	 * 初始化MetadataDefManager，将文档属性信息装载到内存中。
	 *
	 */
	public void init();
	
	public  void initPart(OperEnum oper, List<DocMetadataDefinition> defs) ;
	/**
	 * 增加元数据定义
	 * 
	 * @param metadataDef 元数据定义基本信息
	 * @param metadataOptions 元数据定义选项信息
	 */
	public void addMetadataDef(DocMetadataDefinition metadataDef, List<DocMetadataOption>  metadataOptions);

	/**
	 * 删除元数据定义
	 * 
	 * @param id 元数据定义id
	 */
	public void deleteMetadataDef(Long id)throws Exception;


	/**
	 * 修改保存元数据定义
	 * 
	 * @param metadataDef 元数据定义基本信息
	 * @param metadataOptions 元数据定义选项信息
	 */
	public void updateMetadataDef(DocMetadataDefinition metadataDef, List<DocMetadataOption>  metadataOptions);

	/**
	 * 根据id查找元数据定义
	 * 
	 * @param id 元数据定义id
	 * @return
	 */
	public DocMetadataDefinition getMetadataDefById(Long id);

	/**
	 * 获取所有元数据定义
	 * 
	 * @return
	 */
	public List<DocMetadataDefinition> getAllMetadataDef();
	public List<DocMetadataDefinition> getAllUsableMetadataDef();

	/**
	 * 根据类别获取元数据定义
	 * 
	 * @param group 类别
	 * @return
	 */
	public List<DocMetadataDefinition> findMetadataDefByGroup(String group);
	
	/**
	 * 查找默认显示栏目的元数据定义
	 * @return
	 */
	public List<DocMetadataDefinition> findDefaultMetadataDef();
	
	/**
	 * 获得系统所有扩展文档属性定义。
	 * @return List
	 */
	public List <DocMetadataDefinition> getExtMetadataDefs();
	public List<DocMetadataDefinition> getUsableExtMetadataDefs();
	
	/**
	 * 根据类别获得扩展文档属性定义。
	 * @param group 类别名称
	 * @return List
	 */
	public List<DocMetadataDefinition> getExtMetadataDefsByGroup(String group);
	public List<DocMetadataDefinition> getUsableExtMetadataDefsByGroup(String group);
	public List<DocMetadataDefinition> getUsableExtMetadataDefsByGroupKeyList(List<String> keyList);
	
	/**
	 * 查找元数据的所有类别
	 * @return
	 */
	public List<String> findMetadataDefGroup();	
	
	/**
	 * 验证给定名称的文档属性是否存在(新建文档属性时调用)。
	 * @param name 文档属性名称
	 * @return true-存在;false-不存在
	 */
	public boolean containMetadataDef(String name);
	
	/**
	 * 验证文档属性名称是否重复(修改用户自定义文档属性时调用)。
	 * @param name 文档属性名称
	 * @param id 文档属性id
	 * @return true-存在;false-不存在
	 */
	public boolean containMetadataDef(String name, long id);
	
	/**
	 * 删除某个def对应的option
	 */
	public void deleteOptionsOfDef(long defId);
	
	/**
	 * 判断一个文档属性是否已经使用
	 */
	public boolean getUsedFlagOfDef(Long defId);
	
	/**
	 * 检查是否有重名的类别
	 */
	public boolean hasSameCategory(String name);

	public  Hashtable<Long, DocMetadataDefinition> getMetadataDefTable() ;
	
	public  List<DocMetadataDefinition> getMetadataDefs() ;
	
	public  Set<String> getMetadataDefNames() ;
	
	/**
	 * 更新文档类型对应的元数据定义
	 * @param contentType	文档类型
	 */
	public void updateMetadataDef4ContentType(DocType contentType);

	/**
	 * 获取所有能够用于查询的元数据定义
	 * @return	能够用于查询的元数据定义集合
	 */
	public List<DocMetadataDefinition> getAllSearchableMetadataDef();
	
	/**
	 * 获取枚举类型元数据定义对应的下拉列表HTML代码，用于ajax调用动态添加
	 * @param id	元数据定义ID
	 */
	public String getEnumOptionHtml(Long id);
}
