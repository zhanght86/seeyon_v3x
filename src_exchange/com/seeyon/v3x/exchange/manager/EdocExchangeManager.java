package com.seeyon.v3x.exchange.manager;

import java.util.List;
import java.util.Set;

import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.domain.EdocSendDetail;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.exception.ExchangeException;

public interface EdocExchangeManager {
	
	/**
	 * 签收公文。
	 * @param id 公文记录id
	 * @param recUserId 签收人id
	 * @param registerUserId 登记人id
	 * @param recNo 签收编号
	 * @param remark 备考
	 * @param agentToId 被代理人ID
	 * @throws Exception
	 */
	public void recEdoc(long id, 
			long recUserId, 
			long registerUserId,
			String recNo,
			String remark,
			String keepPeriod,
			Long agentToId
			) throws Exception;
	
	/**
	 * 更改公文登记状态（待登记->已登记）,公文登记时调用此方法。
	 * @param id 公文记录id
	 */
	public void registerEdoc(long id) throws Exception;
	
	/**
	 * 发送公文，并将公文记录状态标记为已发。
	 * @param id 公文记录id
	 * @param sendUserId 发送用户id
	 * @throws Exception
	 */
	public void sendEdoc(long id, long sendUserId) throws Exception;
	
	/**
	 * 发送公文，并将公文记录状态标记为已发。
	 * @param edocSendRecord 公文待发送对象
	 * @param sendUserId 发送用户id
	 * @param sender 发文人姓名,直接保存到数据库中
	 * @throws Exception
	 */
	public void sendEdoc(EdocSendRecord edocSendRecord, long sendUserId, String sender, boolean reSend) throws Exception;
	/**
	 * 发送公文，并将公文记录状态标记为已发。
	 * @param edocSendRecord 公文待发送对象
	 * @param sendUserId 发送用户id
	 * @param sender 发文人姓名,直接保存到数据库中
	 * @param agentToId : 被代理人ID
	 * @param tempDetail 
	 * @throws Exception
	 */
	public void sendEdoc(EdocSendRecord edocSendRecord, long sendUserId, String sender,Long agentToId, boolean reSend, List<EdocSendDetail> tempDetail) throws Exception;
	
	/**
	 * 读取当前用户的待登记公文列表。
	 * @param userId 当前用户ID
	 * @return List
	 */
	
	public List<EdocRecieveRecord> getToRegisterEdocs(long userId);
	
	/**
	 * 读取当前用户的（待发送或已发送）公文列表。
	 * @param userId 当前用户id
	 * @param orgId 当前用户登录单位id
	 * @param status 状态（待发送或已发送）
	 * @return List
	 */
	public List<EdocSendRecord> getSendEdocs(long userId, long orgId, int status,String condition,String value)throws Exception;
	//成发项目 重写getSendEdocs
	public List<EdocSendRecord> getSendEdocs(long userId, long orgId, int status,String condition,String value,Integer secretLevel)throws Exception;
	
	/**
	 * 读取当前用户的（待签收或已签收）公文列表。
	 * @param userId 当前用户id
	 * @param orgId 当前用户登录单位id
	 * @param status 状态（待签收，已签收）
	 * @return List
	 */
	public List<EdocRecieveRecord> getRecieveEdocs(long userId, long orgId, Set<Integer> statusSet,String condition,String value)throws Exception;
	//成发集团项目 程炯  重写getRecieveEdocs
	public List<EdocRecieveRecord> getRecieveEdocs(long userId, long orgId, Set<Integer> statusSet,String condition,String value,Integer secretLevel)throws Exception;
	public EdocSendRecord getSendRecordById(long id);
	public EdocRecieveRecord getReceivedRecord(long id);
	
	public void deleteByType(String id,String type)throws Exception;
	/**
	 * 检查选择的单位部门是否有公文收发员
	 * @param objIds
	 * @param objNames
	 * @return
	 */
	public String checkExchangeRole(String typeAndIds);
	
	public List<EdocSendDetail> createSendRecord(Long sendRecordId,String typeAndIds) throws ExchangeException;
	
	public boolean isEdocCreateRole(Long userId);
	/**
	 * 判断是否具有指定单位下的收文登记权
	 * @param userId
	 * @param exchangeAccountId
	 * @return
	 */
	public boolean isEdocCreateRole(Long userId,Long exchangeAccountId);
	/**
	 * 检查用户是否有交换的待办事项,包括待发送,待签收,等登记
	 * @param userId
	 * @return
	 */
	public boolean hasExchangeItem(Long userId);
	
	public EdocSendRecord getEdocSendRecordByDetailId(long detailId);
	
	/**
	 * 撤销交换记录
	 * @param replyId
	 * @throws Exception
	 */
	public void withdraw(String replyId)throws Exception;
	
	public boolean canWithdraw(String replyId, String detailId)throws Exception;
	/**
	 * Ajax判断某个公文收发员是否有待交换和待签收的Affair事项。
	 * @param userId :用户ID
	 * @return
	 */
	public String checkEdocExchangeHasPendingAffair(Long userId);

	/**
	 * 回退公文
	 * 
	 * @param stepBackEdocId
	 *            被回退的公文发文记录ID
	 * @param stepBackInfo
	 *            回退说明
	 * @param memberId
	 *            公文发文人ID
	 * @param currentUserName
	 *            操作用户名
	 * @param stepBackEdocSummary
	 *            被回退的公文对象
	 * @throws Exception
	 */
	public void stepBackEdoc(Long stepBackEdocId, String stepBackInfo,
			Long memberId, String currentUserName,
			EdocSummary stepBackEdocSummary) throws Exception;

	/**
	 * 更改公文登记人
	 * 
	 * @param edocRecieveRecordId
	 *            被更改的公文的签收记录id
	 * @param newRegisterUserId
	 *            新的登记人id
	 * @param newRegisterUserName
	 *            新的登记人name
	 * @param changeOperUserName
	 *            操作人name
	 * @param changeOperUserID
	 *            操作人ID
	 * @throws Exception
	 */
	public void changeRegisterEdocPerson(String edocRecieveRecordId,
			String newRegisterUserId, String newRegisterUserName,
			String changeOperUserName, String changeOperUserID)
			throws Exception;
	/**
	 * 判断公文是否可以被登记
	 * 
	 * @param edocRecieveRecordId
	 *            公文签收记录id
	 * @return true 可以被登记 false 公文已经被登记，不可以被登记
	 */
	public boolean isBeRegistered(String edocRecieveRecordId);

	/**
	 * 待登记公文回退
	 * 
	 * @param id
	 *            待登记公文id
	 * @param referenceAffairId
	 *            待登记公文对应的affair事项id
	 * @param stepBackInfo
	 *            TODO 回退说明
	 * @throws Exception
	 */
	public void stepBackRecievedEdoc(long id, long referenceAffairId, String stepBackInfo)
			throws Exception;
	
	/**
	 * 查询指定类型公文的问号列表，返回问号格式如下:
	 * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ]
	 * @param edocType 应用类型
	 * @return
	 */
	public String queryMarkList(String modelType,long userId, long accountId);
}
