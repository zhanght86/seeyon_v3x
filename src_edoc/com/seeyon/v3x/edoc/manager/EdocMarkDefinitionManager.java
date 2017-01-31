/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.edoc.EdocEnum.MarkCategory;
import com.seeyon.v3x.edoc.domain.EdocMarkCategory;
import com.seeyon.v3x.edoc.domain.EdocMarkDefinition;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public interface EdocMarkDefinitionManager {
	
	/**
	 * 根据id返回公文文号定义。
	 * @param id 公文文号定义id
	 * @return EdocMarkDefinition
	 */
	public EdocMarkDefinition getMarkDefinition(long id);

	/**
     * 方法描述：保存公文文号定义
     */
	public void saveMarkDefinition(EdocMarkDefinition edocMarkDefinition) ;
	
	/**
     * 方法描述：修改公文文号定义
     */
	public void updateMarkDefinition(EdocMarkDefinition edocMarkDefinition);
	
   	/**
     * 方法描述：删除公文文号定义
     */
	public void deleteMarkDefinition(EdocMarkDefinition edocMarkDefinition);
	
	/**
	 * 设置文号定义已经使用
	 * @param markDefId
	 */
	public void setEdocMarkDefinitionUsed(Long markDefId) ;
	/**
     * 方法描述：根据公文文号定义ID查询公文文号定义
     */
	public EdocMarkDefinition queryMarkDefinitionById(Long edocMarkDefinitionId);
	
//	/**
//     * 方法描述：根据公文字号查询公文文号定义
//     */
//	public List<String> queryMarksByWordNo (String wordNo);
	
//	/**
//     * 方法描述：查询全部公文文号
//     */
//	public List<EdocMarkModel> queryAllMarkDefinitions () throws BusinessException ;
	
	/**
	 * 方法描述：保存公文文号定义，同时保存公文文号类型和文号授权
	 */
	public void saveMarkDefinition(EdocMarkDefinition def,EdocMarkCategory cat);
	
	public List<EdocMarkModel> getEdocMarkDefs(Long domainId, String condition, String textfield) throws BusinessException;
	
	/**
	 * 根据授权部门查找公文文号定义。
	 * @param deptIds  文号授权部门id（以,号分隔）
	 * @return List<EdocMarkModel>
	 */
	public List<EdocMarkModel> getEdocMarkDefinitions(String deptIds,int markType);
	
	/**
	 * 查找模板是否绑定了某种的字号
	 * @param templeteId  ：公文模板
	 * @param category ： 分类
	 * @return
	 */
	public EdocMarkModel getEdocMarkByTempleteId(Long templeteId,MarkCategory category);
	public EdocMarkModel  getEdocMarkDefinitionById(Long definitionId);

	public Short judgeStreamType(Long definitionId)throws BusinessException;
	
//	/**
//	 * 根据文号定义和当前编号，返回该文号定义对应的当前文号。
//	 * @param definitionId
//	 * @param currentNo
//	 * @return String
//	 */
//	public String getEdocMark(long definitionId, Integer currentNo);
	/**
	 * 判断公文文号定义是否存在
	 */
	public int judgeEdocDefinitionExsit(Long definitionId);
	/**
	 * 是否包含指定字号的公文文号（新建公文文号时调用）。
	 * @param wordNo 字号
	 * @param domainId 单位id
	 * @return Boolean
	 */
	public Boolean containEdocMarkDefinition(String wordNo, long domainId,int markType);
	
	/**
	 * 是否包含指定字号的公文文号（修改公文文号时调用）。
	 * @param markDefId 公文文号定义id
	 * @param wordNo 字号
	 * @param domainId 单位id
	 * @return Boolean
	 */
	public Boolean containEdocMarkDefinition(long markDefId, String wordNo, long domainId,int markType);
	
	/**
	 * 判断在指定文号类别中是否包含公文文号定义。
	 * @param categoryId
	 * @return Boolean
	 */
	public boolean containEdocMarkDefInCategory(long categoryId);
	
	/**
	 * 逻辑删除文号定义记录（将状态置为已删除）
	 * @param defId  文号定义Id
	 * @param status 状态
	 */
	public void logicalDeleteMarkDefinition(long defId, short status);
	
	public List<EdocMarkDefinition> getEdocMarkDefinitionsByCategory(Long categoryId);
	
	public EdocMarkModel markDef2Mode(EdocMarkDefinition markDef,String yearNo,Integer currentNo);
	  /**
     * 将EdocMarkCategory自增长
     * @param markDefinitionId
     */
    public void setEdocMarkCategoryIncrement(Long markDefinitionId); 
	
}
