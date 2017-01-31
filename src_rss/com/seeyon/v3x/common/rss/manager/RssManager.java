package com.seeyon.v3x.common.rss.manager;

import java.util.List;

import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.common.rss.domain.RssChannelInfo;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.domain.RssItemStatus;
import com.seeyon.v3x.common.rss.domain.RssSubscribe;
import com.sun.syndication.feed.synd.SyndFeed;

public interface RssManager {
//	/**
//	 * 添加一个目录
//	 * @param userType 用户类型
//	 * @param name	 目录名称
//	 * @throws Exception	目录重名，抛出的异常
//	 */
//	public void addDirectory(String userType,long userId,String name)throws Exception;
//
//	/**
//	 * 修改一个目录
//	 * @param id   			要修改的目录的ID
//	 * @param name			新的目录名
//	 * @throws Exception	目录重名，抛出的异常
//	 */
//	public void modifyDirectory(long id, String name) throws Exception;
//	
//	/**
//	 * 删除一个目录
//	 * @param id			要删除的目录的ID号
//	 */
//	public void deleteDirectory(long id);
//	
//	/**
//	 * 根据一个ID查找对应的目录
//	 * @param id			目录ID
//	 * @return				返回一个目录对象
//	 */
//	public RssDirectory getDirectoryById(long id);
	
//////////////////////////////////////new 
	/**
	 * 添加一个频道为订阅
	 * @param categoryChannelId
	 * @param userType
	 * @param userId
	 */
	public void addSubscribe(long categoryChannelId,String userType,long userId);

	/**
	 * 获取最大排序号
	 * @param userType
	 * @param userId
	 * @return
	 */
	public int getMaxOrderNumber(String userType,long userId);
	
	/**
	 * 删除订阅
	 * @param userId
	 * @param userType
	 * @param deleteIds			RssSubscribe  id
	 */
	public void deleteSubscribes(long userId,String userType,String deleteIds);

	
	/**
	 * 获取一个订阅的具体信息
	 * @param subscribeId
	 * @return
	 */
	public List<RssChannelItems> getSubscribeInfo(long categoryChannelId); 
	

	
	/**
	 * 获取用户订阅的所有频道
	 * @param userType
	 * @param userId
	 * @return
	 */
	public List<RssCategoryChannel> getAllSubscribeInfo(String userType,long userId);
	
	/**
	 * 根据频道ID获取一个用户订阅
	 * @param userType
	 * @param userId
	 * @param categoryChannelId
	 * @return
	 */
	public RssSubscribe getSubscribeById(String userType,long userId,long categoryChannelId);

	
	/**
	 * 根据ID获取一个对应的订阅
	 * @param id			订阅的ID号
	 * @return				返回一个订阅对象
	 */
	public  RssSubscribe getSubscribeById(long id);
		
	/**
	 * 标记一个订阅里面的已读项
	 * @param itemId
	 * @param subId
	 */
	public void readedItem(long itemId,long subId);
	/**
	 * 得到用户的一个订阅里面已经阅读的项
	 * @param subId
	 * @param userId
	 * @return
	 */
	public List<Long> getReadedItems(long subId,long userId);
	
	/**
	 * 获得当前用户所有已阅读的项。
	 * @param userId
	 * @return
	 */
	public List<Long> getReadedItems(long userId);
	
	/**
	 * 获取最新的size条RSS条目信息
	 * @param size
	 * @return
	 */
	public List<RssChannelItems> getMostNewItems(int size,String userType,long userId);
	
	/**
	 * 读取指定用户订阅的所有RSS频道的类别。
	 * @param userId
	 * @return
	 */
	public List<RssCategory> getMyCategories(long userId);
	
	/**
	 * 读取当前用户订阅的RSS频道列表。
	 * @param categoryId
	 * @param userId
	 * @return
	 */
	public List<RssCategoryChannel> getMySubscriptions(long userId);
	
	/**
	 * 读取当前用户订阅的最新RSS信息条目数。
	 * @param userId
	 * @return
	 */
	public int getMyRecentlyItemsCount(long userId);
	
	/**
	 * 读取当前用户订阅的最新RSS条目信息（分页显示）
	 * @param userId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<RssChannelItems> getMyRecentlyItems(long userId, int pageNo, int pageSize);
	
	/**
	 * 得到最新rss信息条数
	 */
	public int getTotalOfItems();
	
	/**
	 * 取得所有订阅的总数
	 * 
	 */
	public int getAllSubTotal();
	/**
	 * 根据ID 得到rss_categoryRSS类别表 的信息
	 * @param rssCategoryId
	 * @return
	 */
	public RssCategory getRssCategory(Long rssCategoryId) ;
	
	/**
	 * 根据rssCategoryId 得到该类别下的所有的内容
	 * @param rssCategoryId
	 * @return
	 */
	public List<RssChannelItems> getRssChannelItems(Long rssCategoryId ,long userId) ;
		
}
