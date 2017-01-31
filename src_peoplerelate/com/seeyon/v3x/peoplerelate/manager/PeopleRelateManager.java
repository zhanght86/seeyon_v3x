package com.seeyon.v3x.peoplerelate.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;

/**
 * 关联人员设置操作
 * @author xut
 *
 */
public interface PeopleRelateManager {

	/**
	 * 添加关联人员
	 * @param pr
	 * @throws Exception
	 */
	void addPeopleRelate(PeopleRelate pr) throws Exception;
	
	public void updatePeopleRelate(PeopleRelate pr) throws Exception;
//	与登录人员关联的人员列表
	List<PeopleRelate> getPeopleRelateList(Long userId,int type) throws Exception;
	
//	取得关联人员是我的人员列表
	List<PeopleRelate> getPeopleRelateList(Long userId) throws Exception;
	
//	取得我的关联人员列表
	List<PeopleRelate> getPeopleRelatedList(Long userId) throws Exception;
	
	/**
	 * 判断该类型下是否存在该关联人员
	 * @param relateMemberId
	 * @param uid
	 * @param type  关联类型  1.上级    2.秘书    3.下级   4. 我的同事
	 * @return
	 * @throws Exception
	 */
	public boolean isRelateExist(Long relateMemberId , Long uid , int type ) throws Exception;
	
	/**
	 * 判断是否存在该关联人员
	 * @param relateMemberId
	 * @param uid
	 * @param type  关联类型  1.上级    2.秘书    3.下级   4. 我的同事
	 * @return
	 * @throws Exception
	 */
	public boolean isRelateExist(Long relateMemberId , Long uid ) throws Exception;
    /**
     * 是否有与我关联的人员(未确认的)
     */
     public boolean isRelateExistUnSure(Long relateMemberId , Long uid,int isSure) throws Exception;
	
	public void delRelateMembers(List deleteIds,Long relatedMemberId , int type ) throws Exception;
	
	public void delRelateMembers(Long relatedMemberId) throws Exception;
	
	public void updateWsbs(Long relateMemberId,Long relatedMemberId) throws Exception;
	
	public void updateWsbs(String deleteIds,Long relatedMemberId) throws Exception;
	//获取与当前人关联的某人员信息
	public PeopleRelate getPeopleRelate(Long relateId,Long relatedId) throws Exception;
	
	/**
	 * 取得我所有的关联人员
	 * 
	 * @param userId
	 * @return key - 关联人员类型， vale
	 * @throws Exception
	 */
	public Map<RelationType, List<V3xOrgMember>> getAllRelateMembers(Long userId) throws Exception;
	
	/**
	 * 取得我所有的关联人员信息
	 * @param userId 判断是否是更多操作
	 * @param fromMore
	 * @return
	 * @throws Exception
	 */
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore) throws Exception;

	/**
	 * 取得我所有的关联人员信息（栏目内容过滤）
	 * @param userId
	 * @param fromMore 判断是否是更多操作
	 * @param designated
	 * @return
	 * @throws Exception
	 */
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore, String designated) throws Exception;
	
	/**
	 * 取得我所有的关联人员信息（条数限制）
	 * @param userId
	 * @param fromMore 判断是否是更多操作
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public Map<RelationType, List<PeopleRelate>> getAllPeopleRelates(Long userId, boolean fromMore, int size) throws Exception;
	
	/**
	 * 取得我的关联人员的id
	 * 
	 * @param userId
	 * @return key - 关联人员类型， vale	这里只有leader(1)和otherEscapeLeader(5)
	 * @throws Exception
	 */
	public Map<RelationType, List<Long>> getAllRelateMembersId(Long userId) throws Exception;
	
	/**
	 * 取得我某一个类型的关联人员
	 * 
	 * @param userId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<V3xOrgMember> getRelateMembers(Long userId,int type) throws Exception;
	
	/**
	 * 根据被关联人员id与类型删除人员
	 * @param relatedMemberId
	 * @param type
	 * @throws Exception
	 */
	public void delRelateMembers(Long relatedMemberId , int type ) throws Exception;
	
    /**
     * 通过关联类型和被关联人员查询出所有主动关联人员集合
     * @param long relatedMemberId
     * @param int type  关联类型  1.上级    2.秘书    3.下级   4. 我的同事
     * @return List<Long>
     * @throws Exception
     */
    public List<Long> getRelateMemberIdList(long relatedMemberId ,int type ) throws Exception;
    /**
     * 通过关联类型和人员ID查询出所有主动和被动关联人员集合
     * @param long relatedMemberId
     * @param int type  关联类型  1.上级    2.秘书    3.下级   4. 我的同事
     * @return List<PeopleRelate>
     * @throws Exception
     */
    public List<PeopleRelate> getAllRelateMemberList(long relatedMemberId ,int type ) throws Exception;
    public void deleteRelatePeopleRepeat(Long relateId,Long relatedId,int flag) throws Exception;
    
    public boolean isRelateExistNotConfreres(Long relateMemberId, Long uid,int type) throws Exception;
    
    public void deletePeopleRelateByOne(final Long related, final Long relateId,final int type) throws Exception;
    
    public  List<PeopleRelate> getPeopleRelateIsExitRelate(Long relateMemberId, Long uid) throws Exception;
}