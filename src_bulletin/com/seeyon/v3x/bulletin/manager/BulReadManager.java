package com.seeyon.v3x.bulletin.manager;

import java.util.List;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulRead;

public interface BulReadManager {
	
	/**
	 * 在一条公告状态更新为"已发布"之前，删除可能有的所有阅读记录
	 * @param data
	 */
	public void configReadByData(BulData data);
	
	/**
	 * 删除一条公告对应的所有阅读记录
	 * @param data
	 */
	public void deleteReadByData(BulData data);
	
	/**
	 * 根据主键id获取公告阅读记录
	 * @param id
	 * @return
	 */
	public BulRead getReadById(Long id);
	
	/**
	 * 设置用户对公告的阅读记录：先查看用户是否已经阅读了该公告，如已阅则不作任何操作，如未读，则插入一条阅读记录
	 * @param data
	 * @param userId
	 */
	public void setReadState(BulData data,Long userId) ;
	
	/**
	 * 获取用户对公告的阅读记录，如有则返回一条记录，如无则返回空值
	 * @param data
	 * @param userId
	 * @return
	 */
	public BulRead getReadState(BulData data,Long userId);
	
	/**
	 * 获取一条公告对应的所有阅读记录
	 * @param bulletinId	公告ID
	 */
	public List<BulRead> getReadListByData(Long bulletinId);
	
	/**
	 * 获取一个用户的所有公告阅读记录
	 * @param userId
	 * @return
	 */
	public List<BulRead> getReadListByUser(Long userId);
}