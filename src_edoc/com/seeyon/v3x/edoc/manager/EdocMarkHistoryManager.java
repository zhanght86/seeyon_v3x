/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.List;

import com.seeyon.v3x.edoc.domain.EdocMarkHistory;
import com.seeyon.v3x.edoc.domain.EdocMark;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocMarkHistoryExistException;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public interface EdocMarkHistoryManager {
	
	/**
     * 方法描述：保存公文文号历史
     */
    public void save(EdocMarkHistory edocMarkHistory);
    

    /**
     * 将公文文号保存到历史表，并删除此文号
     * @param edocMark  公文文号对象
     * @param userId  公文文号使用人id
     */
    public void seveEdocMarkHistory(EdocMark edocMark,Long userId);
    
    /**
     * @方法描述: 封发后将edocMark转移到edocMarkHistory
     *
     */
    public void afterSend(EdocSummary summary);
    
    /**
     * @方法描述: 根据公文id查找文号id
     * @param summaryId 公文Id
     */
    
    public Long findMarkIdBySummaryId(Long summaryId);
    
    public List<EdocMark> findMarkBySummaryId(Long summaryId);
    
    /**
     * 保存公文文号历史
     * @param edocId
     * @param edocMark
     * @param markDefinitionId
     * @param markNum
     * @param createUserId
     * @param lastUserId
     */
    public void save(Long edocId,String edocMark,Long markDefinitionId,int markNum,Long createUserId,Long lastUserId,boolean checkId,boolean autoIncrement) throws EdocMarkHistoryExistException;
    
    /**
     * 保存从断号中选择的文号，只用于签报
     * @param edocMarkId    断号id
     * @param edocId        公文id
     * @param markNum
     */
    public void saveMarkHistorySelectOld(Long edocMarkId,String edocMark,Long edocId,Long userId,boolean checkId) throws EdocMarkHistoryExistException;
    
    /**
     * 判断文号在历史表中是否使用
     * @param edocMark
     * @param edocId
     * @return true已使用；false未使用
     */
    public boolean isUsed(String edocMark,Long edocId);
}
