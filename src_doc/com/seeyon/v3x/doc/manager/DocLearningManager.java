package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocLearningHistory;

public interface DocLearningManager {
	/**
	 * 推送到学习区
	 */
	public void sendToLearnCenter(long docResourceId, String orgType, long orgId);
	public void sendToLearnCenter(long docResourceId, String orgType, List<Long> orgIds);
	public void sendToLearnCenter(List<Long> docResourceIds, String orgType, long orgId);
	public void sendToLearnCenter(List<Long> docResourceIds, String orgType, List<Long> orgIds);
	
	/**
	 * 查询学习文档
	 */
	public List<DocLearning> getDocLearningsByCount(final String orgType, final long orgId, final int count);
	public List getDocLearningsByPage(String orgType, long orgId);
	public List getDocLearningsByPage(String orgType, long orgId,String type,String value);
	public List<DocLearning> getDocLearningsByOrgTypeAndDocId(String orgType, long docResourceId);
	public Map<Long, List<DocLearning>> getDocLearningsByOrgTypeAndDocIds(String orgType, List<Long> docResourceIds);
	
	/**
	 * 取消学习
	 */
	public void cancelLearn(List<Long> learnIds);
	public void cancelLearn(long learnId);
	public void cancelLearn(String learnIds);
	public void deleteLearnByDocId(long docId);
	
	/**
	 * 记录学习记录(当前用户)
	 */
	public void learnTheDoc(long docResourceId);
	
	/**
	 * 查看学习记录
	 */
	public List<DocLearningHistory> getTheLearnHistory(long docResourceId);
	public List<DocLearningHistory> getTheLearnHistoryByPage(long docResourceId);
	public List<DocLearningHistory> getTheLearnHistoryByDeptByPage(long docResourceId, long deptId);
	public DocLearningHistory getTheLearnHistoryOfCurrentUser(long docResourceId);
	
	/**
	 * 删除学习记录
	 */
	public void deleteLearningHistorys(String docResourceIds);
	public void deleteLearningHistoryOfCurrentUser(long docResourceId);
	/*
	 * 是否是学习文档
	 * 
	 */
	
	public boolean isLearnDoc(long docId);
}

