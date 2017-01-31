package com.seeyon.v3x.common.rss.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.rss.dao.RssCategoryChannelDao;
import com.seeyon.v3x.common.rss.dao.RssCategoryDao;
import com.seeyon.v3x.common.rss.dao.RssChannelInfoDao;
import com.seeyon.v3x.common.rss.dao.RssChannelItemDao;
import com.seeyon.v3x.common.rss.dao.RssItemStatuDao;
import com.seeyon.v3x.common.rss.dao.RssSubscribeDao;
import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.common.rss.domain.RssChannelInfo;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.domain.RssItemStatus;
import com.seeyon.v3x.common.rss.domain.RssSubscribe;
import com.seeyon.v3x.common.web.login.CurrentUser;


public class RssManagerImpl implements RssManager {
	
	private RssSubscribeDao rssSubscribeDao;
	private RssChannelInfoDao rssChannelInfoDao;
	private RssChannelItemDao rssChannelItemDao;
	private RssItemStatuDao  rssItemStatuDao;
	private RssCategoryChannelDao  rssCategoryChannelDao; 	
	private RssCategoryDao rssCategoryDao;

	public RssSubscribeDao getRssSubscribeDao() {
		return rssSubscribeDao;
	}

	public void setRssSubscribeDao(RssSubscribeDao rssSubscribeDao) {
		this.rssSubscribeDao = rssSubscribeDao;
	}
	
	public RssCategoryChannelDao getRssCategoryChannelDao() {
		return rssCategoryChannelDao;
	}

	public void setRssCategoryChannelDao(RssCategoryChannelDao rssCategoryChannelDao) {
		this.rssCategoryChannelDao = rssCategoryChannelDao;
	}


	public RssChannelInfoDao getRssChannelInfoDao() {
		return rssChannelInfoDao;
	}

	public void setRssChannelInfoDao(RssChannelInfoDao rssChannelInfoDao) {
		this.rssChannelInfoDao = rssChannelInfoDao;
	}

	public RssChannelItemDao getRssChannelItemDao() {
		return rssChannelItemDao;
	}

	public void setRssChannelItemDao(RssChannelItemDao rssChannelItemDao) {
		this.rssChannelItemDao = rssChannelItemDao;
	}

	public RssItemStatuDao getRssItemStatuDao() {
		return rssItemStatuDao;
	}

	public void setRssItemStatuDao(RssItemStatuDao rssItemStatuDao) {
		this.rssItemStatuDao = rssItemStatuDao;
	}
	
	public RssCategoryDao getRssCategoryDao() {
		return rssCategoryDao;
	}
	
	public void setRssCategoryDao(RssCategoryDao rssCategoryDao) {
		this.rssCategoryDao = rssCategoryDao;
	}
	
	public RssSubscribe getSubscribeById(long id) {
		RssSubscribe subscrib=rssSubscribeDao.get(id);
		return subscrib;
	}	

	public void addSubscribe(long categoryChannelId, String userType, long userId) {
		List<RssSubscribe> list=rssSubscribeDao.getSubscribed(userType, userId, categoryChannelId);
		if(list != null && list.isEmpty()==false){
			return ;
		}
		User user=CurrentUser.get();
		RssCategoryChannel rssChannel=rssCategoryChannelDao.get(categoryChannelId);
		RssSubscribe rssSub=new RssSubscribe();
		rssSub.setIdIfNew();
		rssSub.setName(rssChannel.getName());
		rssSub.setUserId(userId);
		rssSub.setUserType(userType);
		rssSub.setCreateDate(new Date());
		rssSub.setCreateUserId(user.getId());
		rssSub.setLastUpdate(new Date());
		rssSub.setLastUserId(user.getId());		
		rssSub.setDeploy(false);
		rssSub.setOrderNum(this.getMaxOrderNumber(userType, userId));
		rssSub.setShowNum(5);
		rssSub.setUrl(rssChannel.getUrl());
		rssSub.setCategoryChannelId(categoryChannelId);
		rssSubscribeDao.save(rssSub);		//新建一个订阅
		
	}
	
	public void deleteSubscribes(long userId, String userType, String deleteIds) {
		
		/**
		rssItemStatuDao.deleteRssItemStatus(deleteIds) ;
	?**/
		
		rssSubscribeDao.deleteSubcribes(userType, userId, deleteIds);
		
	}
	

	public int getMaxOrderNumber(String userType, long userId) {
		
		return rssSubscribeDao.getMaxOrder(userType, userId);
	}

	//获取一个频道的具体信息
	public List<RssChannelItems> getSubscribeInfo(long categoryChannelId) {
		List<RssChannelInfo> list=rssChannelInfoDao.findBy("categoryChannelId", categoryChannelId);
		if(list != null && list.isEmpty()==false){
			RssChannelInfo info=list.get(0);
			List<RssChannelItems> theItem=rssChannelItemDao.findAllItems(info.getId());
			return theItem;
		}
		return null;
	}


	//获取用户订阅的所有频道
	public List<RssCategoryChannel> getAllSubscribeInfo(String userType, long userId) {
		List<RssSubscribe> list=rssSubscribeDao.getRssSubscribe(userType, userId);
		List<RssCategoryChannel> the_list=new ArrayList<RssCategoryChannel>();
		String channelIds="";
		if(list != null && list.isEmpty()==false){
			for(int i=0;i<list.size();i++){
				long theId=list.get(i).getCategoryChannelId();
				if(i != list.size()-1){
					channelIds+=theId;
					channelIds+=",";
				}else{
					channelIds+=theId;
				}
			}
			the_list=rssCategoryChannelDao.getCategoryChannelByIds(channelIds);	//获取用户所有的订阅对象			
		}

		return the_list;
	}

	public RssSubscribe getSubscribeById(String userType, long userId, long categoryChannelId) {
		List<RssSubscribe> list=rssSubscribeDao.getSubscribed(userType, userId, categoryChannelId);
		if(list != null && list.isEmpty()==false){
			return list.get(0);
		}
		return null;
	}

	public void readedItem(long itemId, long subId) {
		User user=CurrentUser.get();
		List<RssItemStatus> list=rssItemStatuDao.getRssItemStatus(user.getId(), subId, itemId);
		if(list != null && list.isEmpty()==false)return ;
		RssItemStatus status=new RssItemStatus();
		status.setIdIfNew();
		status.setRssItemId(itemId);
		status.setRssSubscribeId(subId);
		status.setStatus(1);
		status.setUesrId(user.getId());
		rssItemStatuDao.save(status);				
	}

	public List<Long> getReadedItems(long subId, long userId) {
		List<RssItemStatus> list=rssItemStatuDao.getRssItemStatusById(userId, subId);
		List<Long> the_list=new ArrayList<Long>();
		for(int i=0;i<list.size();i++){
			the_list.add(list.get(i).getRssItemId());
		}
		return the_list;
	}
	
	public List<Long> getReadedItems(long userId) {
		List<RssItemStatus> list=rssItemStatuDao.getMyReadItems(userId);
		List<Long> the_list=new ArrayList<Long>();
		for(int i=0;i<list.size();i++){
			the_list.add(list.get(i).getRssItemId());
		}
		return the_list;
	}
	
	//获取用户订阅的条目中发布时间考前的条目
	public List<RssChannelItems> getMostNewItems(int size,String userType,long userId) {
		List<RssSubscribe> subscribe=rssSubscribeDao.getRssSubscribe(userType, userId);		//获取用户所有的订阅
		List<RssChannelItems> list=new ArrayList<RssChannelItems>();
		if(subscribe == null || subscribe.isEmpty())return list;
		String channelId="";
		for(int i=0;i<subscribe.size();i++){
			RssSubscribe sub=subscribe.get(i);
			if(i != subscribe.size()-1){
				channelId+=sub.getCategoryChannelId();
				channelId+=",";
			}else{
				channelId+=sub.getCategoryChannelId();
			}
		}
		
		list=rssChannelItemDao.getChannelItemsByIds(channelId);
		List<RssChannelItems> the_list=new ArrayList<RssChannelItems>();
		if(list != null && list.isEmpty()==false){
			if(size < list.size()) {
				for(int i = 0 ; i < size ; i++){
					the_list.add(list.get(i));
				}				
			}else{
				for(int i = 0 ; i < list.size() ; i++){
					the_list.add(list.get(i));
				}				
				
			}

		}
		return the_list;
	}


	public List<RssCategory> getMyCategories(long userId) {				
		return rssCategoryDao.findCategoriesByUserId(userId);
	}
	
	public List<RssCategoryChannel> getMySubscriptions(long userId) {
		return rssCategoryChannelDao.getMyCategoryChannels(userId);
	}
	
	/**
	 * 取得所有订阅的总数
	 * 
	 */
	public int getAllSubTotal(){
		return this.rssSubscribeDao.getQueryCount("from RssSubscribe", null, null);
	}
	
	public int getMyRecentlyItemsCount(long userId) {
		return rssChannelItemDao.getMyRecentlyItemsCount(userId);
	}
	
	public List<RssChannelItems> getMyRecentlyItems(long userId, int pageNo, int pageSize) {
		return rssChannelItemDao.getMyRecentlyItems(userId, pageNo, pageSize);
	}
	
	public List<RssCategory> getRssCategories() {
		return rssCategoryDao.findCategorys();
	}
	
	/**
	 * 得到最新rss信息条数
	 */
	public int getTotalOfItems(){
		String hql = "from RssChannelItems";
		int total = this.rssChannelItemDao.getQueryCount(hql, null, null);
		
		return total;
	}
	/**
	 * 
	 */	
	public RssCategory getRssCategory(Long rssCategoryId) {
		List<RssCategory> all = this.rssCategoryDao.findCategorys() ;
		for(RssCategory rssCategory : all){
			if(rssCategory.getId().intValue() == rssCategoryId.intValue()){
				return rssCategory ;
			}
		}
		return null ;
	}
	/**
	 * 根据类别ID 得到用户订阅的内容
	 */
	public List<RssChannelItems> getRssChannelItems(Long rssCategoryId,long userId){
		 List<RssCategoryChannel> channels = this.getMySubscriptions(userId) ; //得到改用户所有的频道
		 List<RssChannelItems> list = new ArrayList<RssChannelItems>() ;
		 if(channels != null){
			 for(RssCategoryChannel rssCategoryChannel : channels){
				 if(rssCategoryChannel.getCategoryId().intValue() == rssCategoryId.intValue()){
					 List<RssChannelItems> channelItems = this.getSubscribeInfo(rssCategoryChannel.getId()) ;
					 if(channelItems != null ){
						 list.addAll(channelItems) ;
					 }
				 }
			 }
		 }
		 
		return list ;
	}
}
