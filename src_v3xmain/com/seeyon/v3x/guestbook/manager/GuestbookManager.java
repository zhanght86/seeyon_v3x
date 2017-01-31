package com.seeyon.v3x.guestbook.manager;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsArticleReply;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.guestbook.domain.LeaveWord;

public interface GuestbookManager
{
 /**
  * 保存留言
  * @param departmentId
  * @param content 留言内容
  */   
 public LeaveWord saveLeaveWord(long memberId, long departmentId, String content)  throws BusinessException;
 public LeaveWord saveLeaveWordNew(long memberId, long departmentId, String content,Long replyId,Long replyerId)  throws BusinessException;
 
 /**
  * 根据部门ID取得该部门的留言
  * @return
  */
 public List<LeaveWord> getAllLeaveWords(long departmentId)  throws BusinessException;
 
 /**
  * 部门空间显示的留言
  * @param departmentId 部门Id
  * @param resultCount 返回结果数量
  * @return
  */
 public List<LeaveWord> getLeaveWords4Space(long departmentId, int resultCount)  throws BusinessException;
 /**
  * 项目空间某阶段留言
  */
 public List<LeaveWord> getLeaveWords4Project(long departmentId, int resultCount, Long phaseId) throws BusinessException;
 public List<LeaveWord> getPageSizeLeaveWord(long departmentId , int beginRow , int pageSize) throws Exception;
 public List<LeaveWord> getPageSizeLeaveWord(long departmentId, Long phaseId, int beginRow, int pageSize) throws Exception;
 public List<LeaveWord> getReplyLeaveWord(long replyId) throws Exception;
 
 /**
  * 清除留言
  * @param leaveWordId 留言Id
  */
 public void clearLeaveWord(long leaveWordId)  throws BusinessException;
 public void clearSubLeaveWords(long leaveWordId)  throws BusinessException;
 public void clearBanchSubLeaveWords(String idStr)  throws BusinessException;
 
 /**
  * 清除留言
  * @param leaveWordId 留言Id
  */
 public boolean clearLeaveWords(final String leaveWordIds);
 
 
 
 
 
 /**
  * 取得留言总数
  * @return
  */
 public int getLeaveWordsCount(long departmentId)  throws BusinessException;
 public int getSubLeaveWordsCount(long departmentId)  throws BusinessException;
 public int getSubLeaveWordsCount(long departmentId, Long phaseId)throws BusinessException;
 
 /**
  * 根据id取得具体留言
  * @return
  * by Yongzhang 2008-6-12
  */
 
 public LeaveWord getLeaveWordsById(long leaveWordId)  throws BusinessException;
}
