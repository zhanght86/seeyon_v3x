package com.seeyon.v3x.bbs.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.util.SQLWildcardUtil;

public class BbsBoardDao extends BaseHibernateDao<V3xBbsBoard> {
	
	@SuppressWarnings("unchecked")
	public List<V3xBbsBoard> getAllV3xBbsBoard(){
		return super.getHibernateTemplate().loadAll(V3xBbsBoard.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<V3xBbsBoard> getByAccountId(long accountId){
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(V3xBbsBoard.class);
		detachedCriteria.add(Restrictions.eq("accountId", accountId));
		return super.executeCriteria(detachedCriteria, -1, -1);
	}
	
	/**
	 * 更新讨论版块排序
	 * @param surveyTypeId
	 * @param i
	 */
	public void updateBbsBoardSort(Long bbsBoardId,int i){
		try{
			String hql="update V3xBbsBoard board set sort=? where board.id=?";
//			int updNum=
			super.bulkUpdate(hql, null,new Object[]{i,bbsBoardId});
		}catch(Exception e){
			
		}
	}
	/**
	 * 按名称查询
	 * @param userID
	 * @param typename
	 * @param group
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsBoard> findAllBbsBoard (long userID , String typename , boolean group) {
		List<V3xBbsBoard> list = new ArrayList<V3xBbsBoard>() ;
		List<Object> params  = new ArrayList<Object>() ;
		try{
			String hql ="" ;
			hql = " from "+ V3xBbsBoard.class.getName()+" as v3xBbsBoard where v3xBbsBoard.name like ? and v3xBbsBoard.affiliateroomFlag =? " ;
			params.add("%" + SQLWildcardUtil.escape(typename) + "%") ;
			if(group){
                params.add(BbsConstants.BBS_BOARD_AFFILITER.GROUP.ordinal());
			}else {
			    params.add(BbsConstants.BBS_BOARD_AFFILITER.CORPORATION.ordinal());
			}
			list = this.find(hql,-1,-1,null,params) ;

		}catch(Exception e){
		  
		}
		
		return list ; 
	}
	
	/**
	 * 删除讨论板块
	 * @param boardIds
	 */
	public void deleteBoards(List<Long> boardIds) {
		String hql = "delete from " + V3xBbsBoard.class.getName() + " as b where b.id in (:boardIds)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("boardIds", boardIds);
		this.bulkUpdate(hql, params);
	}
	
	/**
	 * 更新讨论板块排序号
	 * @param boardId  版块ID
	 * @param sortNum  排序号
	 */
	public void updateSortNum(long boardId, int sortNum) {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("sort", sortNum);
		this.update(boardId, columns);
	}
	/**
	 * 获得指定人员的所管理的板块列表
	 * @param userid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<V3xBbsBoard> findBbsBoardListByUserId(String userid) {
		StringBuffer sbf= new StringBuffer("select b from V3xBbsBoard b,V3xBbsBoardAuth a ");
		sbf.append(" where a.boardId= b.id and a.moduleId= ?");
		List<V3xBbsBoard> list= super.find(sbf.toString(), new Long(userid));
		return list;
	}
}

