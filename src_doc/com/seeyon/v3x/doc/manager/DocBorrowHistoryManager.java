package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocBorrowHistory;
@Deprecated
public interface DocBorrowHistoryManager {
	/**
	 * 保存借阅历史
	 * 
	 * @param userId
	 * @param userType
	 * @param docResourceId
	 * @param sdate
	 * @param edate
	 */
	public void saveHistory(Long userId, String userType, Long docResourceId,
			java.sql.Timestamp sdate, java.sql.Timestamp edate);

//	/**
//	 * 修改借阅结束时间
//	 * 
//	 * @param userId
//	 * @param userType
//	 * @param docResourceId
//	 * @param edate
//	 */
//	public void updateHistory(Long userId, String userType, Long docResourceId,
//			java.sql.Timestamp edate);

	/**
	 * 根据用户、时间查找借阅历史纪录
	 * 
	 * @param userId
	 * @param userType
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<DocBorrowHistory> getHistoryByUser(Long userId,
			String userType, String beginTime, String endTime);
	/**
	 * 根据用户、时间查找借阅历史纪录（分页）
	 * @param userId
	 * @param userType
	 * @param beginTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<DocBorrowHistory> getHistoryByUserPage(Long userId,
			String userType, String beginTime, String endTime,int pageNo,int pageSize);
	/**
	 * 根据用户、时间查找借阅历史纪录数
	 * @param userId
	 * @param userType
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public int getHistoryByUserCount(Long userId,
			String userType, String beginTime, String endTime);

	/**
	 * 根据文档id查找借阅历史纪录
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocBorrowHistory> getHistoryByDocId(Long docResourceId);
	/**
	 * 根据文档id查找借阅历史纪录（分页）
	 * @param docResourceId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<DocBorrowHistory> getHistoryByDocIdPage(Long docResourceId,int pageNo,int pageSize);
	/**
	 * 根据文档id查找借阅历史纪录数
	 * @param docResourceId
	 * @return
	 */
	public int getHistoryByDocIdCount(Long docResourceId);

	/**
	 * 根据用户和文档id查找借阅中的历史纪录对象
	 * 
	 * @param userId
	 * @param userType
	 * @param docResourceId
	 * @return
	 */
	public DocBorrowHistory getBorrowingByuserAndDocId(Long userId,
			String userType, Long docResourceId);

	/**
	 * 定时任务调用（每天0：00调用）
	 * 
	 */
	public void borrowTask();

}
