package com.seeyon.v3x.collaboration.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class ColTrackMemberDao extends BaseHibernateDao<ColSuperviseLog>{
	
	/**
	 * 被跟踪的人查找谁跟踪了他
	 * @param trackMemberId ： 被跟踪的人的ID
	 * @return
	 */
	public List<ColTrackMember> getColTrackMembersByObjectIdAndTrackMemberId(Long objectId,Long trackMemberId){
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("objectId", objectId);
		String hql =" from ColTrackMember where objectId = :objectId ";
		if(trackMemberId != null){
			hql += "and trackMemberId = :trackMemberId ";
			nameParameters.put("trackMemberId", trackMemberId);
		}
		return super.find(hql, -1, -1, nameParameters) ;
	}
	/**
	 * 查找当前事项设置了哪些跟踪
	 * @param affairId
	 * @return
	 */
	public List<ColTrackMember> getColTrackMembersByAffairId(Long affairId){
		String hql =" from ColTrackMember as t where t.affairId = :affairId ";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("affairId", affairId);
		return super.find(hql, -1, -1, nameParameters) ;
	}
	public void deleteColTrackMembersByAffairId(Long affairId){
		String hql="delete from ColTrackMember as ct where ct.affairId = ?";
	//	Map<String,Object> nameParameters = new HashMap<String,Object>() ;
	//	nameParameters.put("affairId", affairId);
		super.bulkUpdate(hql, null, new Object[]{affairId});
	}
	
	
	public void deleteColTrackMembersByObjectId(Long objectId){
		String hql="delete from ColTrackMember as ct where ct.objectId = ?";
		super.bulkUpdate(hql, null, new Object[]{objectId});
	}
}
