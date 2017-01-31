package com.seeyon.v3x.collaboration.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.collaboration.domain.ColQuoteformRecord;


/**
 * 表单流转过程中的关联协同
 * @author Administrator xgghen
 *
 */
public interface ColQuoteformRecordManger {
	/**
	 * 创建关联记录
	 */
	public boolean create (HttpServletRequest request,Long summaryId, Long formAppId, Long masterId) throws Exception ;
	/**
	 * 删除关联记录
	 */
	public boolean delAll (Long colSummayId) throws Exception ;
	/**
	 * 
	 * @param colSummayId
	 * @param _selfColSummary:协同自身的id
	 * @param showTree:是否显示树
	 * <li>_selfColSummary == "" 或者等于null的时候表示的时候表示的是新建的时候</li>
	 * @param width 显示关联记录的宽度，用于字符串截取
	 * @return
	 * @throws Exception
	 */
	public String ajaxGetQuote(Long colSummayId,String _selfColSummary,boolean showTree,Integer width) throws Exception ;
	/**
	 * 查询相关的col_summayId
	 * @param colSummayId
	 * @return
	 * @throws Exception
	 */
	public List<Long> getQuoteIdListBySummayId(Long colSummayId) throws Exception;
	/**
	 * 查询相关的ColQuoteformRecord记录
	 * @param SummayId:ref_colsummary_id
	 * @param fieldName：关联字段的名称
	 * @return
	 * @throws Exception
	 */
	public ColQuoteformRecord getColQuoteformRecord(Long summaryId, String fieldName) throws Exception;
	
	/**
	 * 根据主记录ID和重复表ID查询ColQuoteformRecord记录
	 * @param summaryId 流程表单是协同ID、无流程表单是主记录ID
	 * @param subRecordId 重复表ID 可以为空,为空表示主表字段关联
	 * @param fieldName 关联字段的名称
	 * @return
	 * @throws Exception
	 */
	public ColQuoteformRecord getColQuoteformRecord(Long summaryId, Long subRecordId, String fieldName) throws Exception;
	/**
	 * 根据主记录ID和重复表ID查询ColQuoteformRecord记录中的refColSummaryId
	 * @param summaryId
	 * @param subRecordId
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public String getRefColSummaryId(Long summaryId, Long subRecordId, String fieldName) throws Exception;
	/**
	 * 删除点击相关的ColQuoteformRecord记录
	 * @param SummayId:协同ID
	 * @param fieldName：关联字段的名称
	 * @return
	 * @throws Exception
	 */
	public void delChangedColQuoteRecord(Long summaryId, String fieldName) throws Exception;
	
	/**
	 * 删除点击相关的ColQuoteformRecord记录
	 * @param summaryId 协同ID
	 * @param fieldName 关联字段的名称
	 * @param subRecordId 重复表ID 可以为null
	 * @throws Exception
	 */
	public void delChangedColQuoteRecord(Long summaryId, String fieldName, Long subRecordId) throws Exception;
}
