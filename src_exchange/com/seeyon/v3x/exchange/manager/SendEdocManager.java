package com.seeyon.v3x.exchange.manager;

import java.util.List;

import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.exception.ExchangeException;

public interface SendEdocManager {

	/**
	 * 创建公文发送记录（公文封发时调用）。
	 * 
	 * @param edocSummary
	 *            公文记录id
	 * @param exchangeOrgId
	 *            交换单位id或交换部门id
	 * @param exchangeType
	 *            交换类型
	 * @param edocMangerID
	 *            封发时选择的单位公文管理员
	 * @throws Exception
	 */
	public void create(EdocSummary edocSummary, long exchangeOrgId,
			int exchangeType, String edocMangerID) throws Exception;
	
	/**
	 * 再次发送公文
	 * @param edocSendRecord
	 * @param edocSummary
	 * @param exchangeOrgId
	 * @param exchangeType
	 * @throws Exception
	 */
	public void reSend(EdocSendRecord edocSendRecord, EdocSummary edocSummary) throws Exception;
	
	public void update(EdocSendRecord edocSendRecord) throws Exception;	
	
	public EdocSendRecord getEdocSendRecord(long id);
	
	public List<EdocSendRecord> getEdocSendRecords(int status);
	
	public List<EdocSendRecord> findEdocSendRecords(String accountIds,String departIds,int status,String condition,String value);
	
	public void delete(long id) throws Exception;	
	
	/**
	 * 生成待发送公文要发送的详细信息
	 * @param sendRecordId
	 * @param typeAndIds
	 */
	public List<EdocSendDetail>  createSendRecord(Long sendRecordId,String typeAndIds) throws ExchangeException;
	
	public EdocSendRecord getEdocSendRecordByDetailId(long detailId);  

	/**
	 * 删除公文交换的回执记录
	 * @param id
	 * @throws Exception
	 */
	public void deleteRecordDetailById(long id)throws Exception;	
	public List<EdocSendDetail> getDetailBySendId(long sendId);	
}
