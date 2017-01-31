package com.seeyon.v3x.common.rss.manager;


import java.util.List;

import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.common.rss.domain.RssChannelInfo;

public interface RssChannelManager {
	/**
	 * 添加一个类别
	 * @param name		   类别名称
	 * @throws Exception   类别名称重复，抛出异常
	 */
	public  long addCategory(String name)throws Exception;
	
	/**
	 * 根据ID删除一个类别
	 * @param id		   要删除的类别的ID		
	 */
	public  void deleteCategory(long id);
	
	/**
	 * 根据ID修改一个类别
	 * @param id			需要修改的类别的ID
	 * @param name			新的类别名称
	 * @throws Exception	类别名称重名，抛出异常
	 */
	public void modifyCategory(long id,String name)throws Exception;
	
	/**
	 * 根据ID获取一个类别
	 * @param id			类别ID
	 * @return				返回一个类别对象
	 */
	public  RssCategory getCategory(long id);
	
	/**
	 * 添加一个频道信息
	 * @param url			频道的链接地址
	 * @param name			频道名称
	 * @param category_id	对应的类别ID
	 * @throws Exception	频道名称重名，抛出异常
	 */
	public void addRssChannel(String url,String name,int orderNum,String description,long category_id )throws Exception;
	
	/**
	 * AJAX验证名称是不是重复
	 * @param name
	 * @param category_id
	 * @return
	 */
	public boolean checkRepName(String name ,long category_id) ;
	/**
	 * AJAX验证URL是不是有效
	 * @param url
	 * @return
	 */
	public boolean checkURL(String url) ;
	/**
	 * 根据ID删除一个频道信息
	 * @param id			要删除的频道的ID
	 */
	public void deleteRssChannel(long id);
	
	/**
	 * 删除一组频道信息
	 * @param list
	 */
	public void deleteRssChannel(String deleteIds);
	
	/**
	 * 根据ID修改一个频道信息
	 * @param id			要修改的频道ID
	 * @param name			新的频道名称
	 * @param url			新的频道链接地址
	 * @param category_id	新的类别名称
	 * @throws Exception	频道名称重名，抛出异常
	 */
	public void modifyRssChannel(long id,String name,String url,int orderNum,String description,long category_id)throws Exception;
	
	/**
	 * 根据ID获取一个频道信息
	 * @param id			频道ID
	 * @return				返回一个频道对象
	 */
	public RssCategoryChannel getChannel(long id);
	
	/**
	 * 获取所有的频道信息及对应的类别
	 * @return				返回所有的频道及对应的类别
	 */
	public List<List> getRssChannels()throws Exception;
	
	/**
	 * 获取所有的频道信息
	 * @return				返回所有的频道对象
	 */
	public List<RssCategoryChannel> getChannels();
	
	/**
	 * 获取当前的所有类别
	 * @return
	 */
	public List<RssCategory> getRssCategorys();
	
	public int getMaxChannelOrder();
	
	/**
	 * 该方法提供给系统调用,用来定时更新所有的频道
	 *
	 */
	public void updateAllChannelInfo()throws Exception;
	
	public RssChannelInfo getRssChannelByCategoryChannelId(long categoryChannelId);
	
	/**
	 * 获取某用户在一个类别下已经订阅的频道
	 * @param categoryId
	 * @return
	 */
	public List<RssCategoryChannel> getSubscribedChannelById(long categoryId,String userType,long userId);
	
	/**
	 * 获取一个类别下的所有频道
	 * @param categoryId
	 * @return
	 */
	public List<RssCategoryChannel> getChannelByCategoryId(long categoryId);	
	public List<RssCategoryChannel> getChannelByCategoryIdByPage(long categoryId);	
	
	/**
	 * 读取所有RSS类别。
	 * @return
	 */
	public List<RssCategory> getAllRssCategories();
	
	/**
	 * 读取所有RSS频道。
	 * @return
	 */
	public List<RssCategoryChannel> getAllRssChannels();
	public List<RssCategoryChannel> getAllRssChannelsByPage();
	public boolean isSubscribe(String ids) ;

	
}
