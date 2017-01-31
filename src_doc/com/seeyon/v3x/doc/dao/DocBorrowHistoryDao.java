package com.seeyon.v3x.doc.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocBorrowHistory;

@Deprecated
public class DocBorrowHistoryDao extends BaseHibernateDao <DocBorrowHistory>{
//	/**
//	 * 是否已记录历史信息
//	 * @param userId
//	 * @param userType
//	 * @param docResourceId
//	 * @return
//	 */
//	public boolean isMarked(Long userId, String userType, Long docResourceId) {
////		String hsql = "from DocBorrowHistory where userId=? and userType=? and docResourceId = ? and " +
////				"edate >='"+new java.sql.Timestamp(System.currentTimeMillis())+"' and sdate<='"+
////				new java.sql.Timestamp(System.currentTimeMillis())+"'";
////		List<DocBorrowHistory> l = super.find(hsql, userId,userType,docResourceId);
////		if(l.size()>0){
////			return true;	
////		}
//		return false;
//	}
}
