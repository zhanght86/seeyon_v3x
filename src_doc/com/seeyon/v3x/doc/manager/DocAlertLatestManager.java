package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.exception.DocException;

public interface DocAlertLatestManager {	
	/**
	 * 更新订阅的文档
	 */
	public void addAlertLatest(DocResource dr, byte alertOprType, Long lastUserId, 
			Timestamp lastUpdate, String msgType, String oldName );
	
	public void addAlertLatest(Long drId, byte alertOprType, Long lastUserId, 
			Timestamp lastUpdate, String msgType, String oldName );
	
	/**
	 * 查询最新修改的订阅文档
	 */
	public List<DocAlertLatest> findAlertLatestsByUser(long alertUserId,byte status) throws DocException;
	public List<DocAlertLatest> findAlertLatestsByUserPaged(long alertUserId,byte status) throws DocException;
	public List<DocAlertLatest> findAlertLatestsByUserPaged(long alertUserId,byte status,String type,String value) throws DocException;
	/**
	 * 查询特定条数最新修改的订阅文档
	 */
	public List<DocAlertLatest> findAlertLatestsByUserByCount(long alertUserId,byte status, int count) ;
	/**
	 * 整理存储，删除规定条数以外的记录，保留最新的
	 * @param maxNum 每个订阅用户允许保留的最多记录数 
	 */
	public void tidyAlertLatests(int maxNum) throws DocException;
	
	/**
	 * 根据DocResource删除所有最新订阅
	 */
	public void deleteAlertLatestsByDoc(DocResource dr);
	
	/**
	 * 删除当前用户的指定最新订阅
	 */
	public void deleteAlertLatestByDrIdAndOprTypeOfCurrentUser(long docResId, Set<Byte> oprType);
	
	/**
	 * 删除给定id的所有记录
	 */
	public void deleteLatestByIds(String ids) ;
	
	/**
	 * 分页查找最新订阅
	 */
	public List<DocAlertLatest> pagedFindAlertLatest(long alertUserId ,byte status);
	
	/**
	 * 查找某个用户的最新订阅的总数adsfasd
	 */
	public int findAlertLatestTotal(long alertUserId,byte status);
}
