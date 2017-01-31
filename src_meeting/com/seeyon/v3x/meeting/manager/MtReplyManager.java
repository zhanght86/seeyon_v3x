package com.seeyon.v3x.meeting.manager;

import java.util.List;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.util.Constants;

/**
 * 会议回执的Manager接口
 * @author wolf
 *
 */
public interface MtReplyManager {

	/**
	 * 保存会议回执
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtReply save(MtReply template) throws BusinessException;
	
	/**
	 * 删除会议回执
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议回执
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 删除一条会议对应的全部回执记录
	 * @param meetingId
	 */
	public void deleteByMeetingId(Long meetingId); 
	
	/**
	 * 在修改会议时，删除被取消与会对象的会议回执记录
	 * @param meetingId 		会议ID
	 * @param reducedConferees	被取消的与会对象集合
	 */
	public void deleteByMeetingId(Long meetingId, List<Long> reducedConferees); 
	
	/**
	 * 查询所有会议回执，支持分页
	 * @return
	 */
	public List<MtReply> findAll();
	
	/**
	 * 查询符合条件的会议回执列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtReply> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议回执列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtReply> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 获取指定用户对指定会议的回执记录（包括亲自回执和代理人的回执）
	 */
	public List<MtReply> findByMeetingIdAndUserId(Long meetingId,Long userId) ;
	
	/**
	 * 获取指定用户对指定会议的回执记录（区分亲自回执和代理人回执）
	 * @param replyType 回复类型，包括三种情况：亲自回执、代理人回执、自己和代理人的回执
	 */
	public List<MtReply> findByMeetingIdAndUserId(Long meetingId, Long userId, Constants.ReplyType replyType);
	
	/**
	 * 根据版面Id获取会议回执
	 * @param id
	 * @return
	 */
	public MtReply getById(Long id);
	
	/**
	 * 回执
	 * @param meetingId
	 * @param uid
	 * @param opinion
	 * @param attitude
	 * @throws BusinessException
	 */
	public void reply(long meetingId, Long uid, String opinion, int attitude) throws BusinessException;
	
	
	/**
	 * 得到该用户各种回执的会议数目（不包括：不参加）
	 * @param memberId
	 * @param type
	 * @return
	 */
	public int getReplyTypeMtNum(Long memberId,int... type);
	
	/**
	 * 得到 affair 列表
	 * @param memberId
	 * @param searchType
	 * @return
	 */
	public List<Affair> getAffairBySearchType(Long memberId,Integer searchType);

}