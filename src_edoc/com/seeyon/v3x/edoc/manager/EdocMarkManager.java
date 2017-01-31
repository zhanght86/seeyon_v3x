/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.v3x.edoc.domain.EdocMark;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.webmodel.EdocMarkNoModel;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public interface EdocMarkManager {
	
	/**
     * 方法描述：保存公文文号
     */
    public void save(EdocMark edocMark);
    
    /**
     * 方法描述：保存公文文号，并更新当前值
     * @param edocMark  公文文号对象
     * @param catId     公文类别id
     * @param currentNo 提供给用户选择的公文文号的当前值
     */
    public void save(EdocMark edocMark,Long catId,int currentNo);
    
    /**
     * 根据ID返回EdocMark对象
     * @param edocMarkId  ID
     * @return
     */
    public EdocMark getEdocMark(Long edocMarkId);
    /**
     * 删除公文文号
     * @param id  公文文号
     */
    public void deleteEdocMark(long id);
    
    /**
     * @方法描述： 拟文时创建文号，并将文号类别当前值加一
     * @param definitionId 公文定义Id
     * @param currentNo 公文文号的序号
     * @param docMark 公文文号
     * @param edocId 公文Id
     */    
    public void createMark(Long definitionId, Integer currentNo, String docMark, Long edocId,int markNum);   
    
    /**
     * 创建手工输入的公文文号。
     * @param docMark 公文文号
     * @param edocId 公文id
     */
    public void createMark(String docMark, Long edocId,int markNum);
    
    /**
     * 判断文号是否被占用
     * @param edocId     公文id
     * @param edocMark   文号
     * @return   true 被占用 false 未占用
     */
    public boolean isUsed(Long edocId);
    
    /**
     * 判断文号是否被占用
     * @param markStr   文号字符串
     * @return   true 被占用 false 未占用
     */
    public boolean isUsed(String markStr,String edocId);
    
    /**
     * 按年度把公文文号归为最小值
     */
    
    public void turnoverCurrentNoAnnual();
    
    
    /**
     * 根据公文文号定义id查找断号
     */    
    public List<EdocMarkNoModel> getDiscontinuousMarkNos(Long edocMarkDefinitionId);
    
    /**
     * 方法描述：拟文/修改文单时，选择一个断号
     * @param edocMarkId 公文文号id
     * @param edocId 公文id
     */    
    public void createMarkByChooseNo(Long edocMarkId, Long edocId,int markNum);
    
    /**
     * 断开已经被调用但没有正式使用的公文文号与公文的连接;
     * 用于修改了文号,原来文号变成断号
     * @param edocSummaryId
     */
    public void disconnectionEdocSummary(long edocSummaryId,int markNum);
    
    public List<EdocMark> findByCategoryAndNo(Long categoryId,Integer docMarkNo);
    
    /**
     * 发起人撤销流程后，已经调用的文号（如果是最大号）可以恢复，下次发文时可继续调用。
     * @param summary 		公文对象
     * @return
     */
   
    public void edocMarkCategoryRollBack(EdocSummary summary);
}
