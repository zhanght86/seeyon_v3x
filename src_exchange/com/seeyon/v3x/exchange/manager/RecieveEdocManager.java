package com.seeyon.v3x.exchange.manager;

import java.util.List;
import java.util.Set;

import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;

public interface RecieveEdocManager {
	
	/**
	 * 内部接收公文调用接口。
	 * @param edocSendRecord 内部发文记录
	 * @param exchangeOrgId 交换单位或部门id
	 * @param exchangeType 交换类型
	 * @param replyId 回执id
	 * @param aRecUnit 收文单位[主送|抄送|抄报]
	 * @throws Exception
	 */
	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,EdocSummary edocSummary) throws Exception;
	
	public void update(EdocRecieveRecord edocRecieveRecord) throws Exception;
	
	public EdocRecieveRecord getEdocRecieveRecord(long id);	
	public EdocRecieveRecord getEdocRecieveRecordByReciveEdocId(long id);	
	
	public List<EdocRecieveRecord> getEdocRecieveRecords(int status);
	
	public List<EdocRecieveRecord> findEdocRecieveRecords(String accountId,String departIds,Set<Integer> statusSet,String condition,String value);
	/**
	 * 登记签收的公文，更新签收日期，给签收人发消息
	 * @param id
	 * @param reciveEdocId 
	 * @return
	 */
	public Boolean registerRecieveEdoc(Long id, Long reciveEdocId) throws Exception;
	
	public List<EdocRecieveRecord> getWaitRegisterEdocRecieveRecords(Long userId);
	
	public void delete(long id) throws Exception;

	//add by lindb
	
	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,String sender,EdocSummary edocSummary) throws Exception;
	/**
	 * 内部接收公文调用接口。
	 * @param edocSendRecord 内部发文记录
	 * @param exchangeOrgId 交换单位或部门id
	 * @param exchangeType 交换类型
	 * @param replyId 回执id
	 * @param aRecUnit 收文单位[主送|抄送|抄报]
	 * @param sender
	 * @param agentToId :被代理人ID
	 * @throws Exception
	 */
	public void create(EdocSendRecord edocSendRecord,
			long exchangeOrgId,
			int exchangeType,
			Object replyId,
			String[] aRecUnit,
			String sender,
			Long agentToId,
			EdocSummary edocSummary) throws Exception;
	
	/**
	 * 根据replayId（签收回执ID来删除待签收记录）
	 * @param replayId 回执ID
	 * @return
	 * @throws Exception
	 */
	public void deleteRecRecordByReplayId(long replayId)throws Exception;
	
	/**
	 * 根据回执ID查找公文待签收记录
	 * @param replyId
	 * @return
	 * @throws Exception
	 */
	public EdocRecieveRecord getReceiveRecordByReplyId(long replyId)throws Exception;
	
	public void delete(EdocRecieveRecord o)throws Exception;
}
