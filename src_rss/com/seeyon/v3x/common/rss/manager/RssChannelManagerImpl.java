package com.seeyon.v3x.common.rss.manager;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.seeyon.v3x.common.rss.domain.RssSubscribe;
import com.seeyon.v3x.common.timer.TimerHolder;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.util.HttpClientUtil;
import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssChannelManagerImpl implements RssChannelManager {
	
	private static final Log log = LogFactory.getLog(RssChannelManagerImpl.class);
	
	private RssCategoryDao rssCategoryDao;
	private RssCategoryChannelDao rssCategoryChannelDao;
	private RssChannelInfoDao   rssChannelInfoDao;
	private RssChannelItemDao   rssChannelItemDao;
	private RssSubscribeDao     rssSubscribeDao;
	private RssItemStatuDao     rssItemStatuDao;

	public RssSubscribeDao getRssSubscribeDao() {
		return rssSubscribeDao;
	}

	public void setRssSubscribeDao(RssSubscribeDao rssSubscribeDao) {
		this.rssSubscribeDao = rssSubscribeDao;
	}

	public RssItemStatuDao getRssItemStatuDao() {
		return rssItemStatuDao;
	}

	public void setRssItemStatuDao(RssItemStatuDao rssItemStatuDao) {
		this.rssItemStatuDao = rssItemStatuDao;
	}
	
	private int firstBeginTime=0;		//系统加载时,第一次执行的时间 
	
	private int intervalTime=60;		//第一次执行后,后续重复执行的时间间隔
	
	private int connTimeout=100;         // 连接时间限制
	
	
	public int getFirstBeginTime() {
		return firstBeginTime;
	}

	public void setFirstBeginTime(int firstBeginTime) {
		this.firstBeginTime = firstBeginTime;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
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

	public RssCategoryDao getRssCategoryDao() {
		return rssCategoryDao;
	}

	public void setRssCategoryDao(RssCategoryDao rssCategoryDao) {
		this.rssCategoryDao = rssCategoryDao;
	}
	
	
	public boolean startTimer(){
		// 服务器启动时更新一次
		try{
			TimerHolder.newTimer(new RssTasker(this), new Date(System.currentTimeMillis() + 2 * 60 * 1000));
		}catch(Exception e){
			log.warn(e.getMessage());
		}
		
		// 定时更新，下一个最近的定时时间开始
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = cal;
		cal2.set(Calendar.HOUR_OF_DAY, firstBeginTime);
		cal2.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		boolean next = (cal.before(cal2));
		if(!next)
			cal2.add(Calendar.DAY_OF_MONTH, 1);
		TimerHolder.newTimer(new RssTasker(this), cal2.getTime(), intervalTime*60*60*1000);
		
//		log.info("RSS任务监控打开--开始时间: " + cal2.getTime() + " 更新间隔：" + intervalTime + " 小时一次");
		
		return true;
	}
	
	
	public long addCategory(String name) throws Exception{
		List<RssCategory> list=rssCategoryDao.findBy("name", name);
		if(list != null && list.isEmpty()==false){
			throw new Exception("RssLang.rss_exception_name");
		}
		
		RssCategory category=new RssCategory();
		category.setIdIfNew();
		long theId=category.getId();
		category.setName(name);
		User user=CurrentUser.get();
		category.setCreateUserId(user.getId());
		category.setCreateDate(new Timestamp(new Date().getTime()));
		category.setLastUserId(user.getId());
		category.setLastUpdate(new Timestamp(new Date().getTime()));
		category.setOrderNum(0);
		rssCategoryDao.save(category);
		return theId;
	}

	public void deleteCategory(long id) {		
		RssCategory category=this.getCategory(id);
		rssCategoryDao.deleteObject(category);		//级联删除RssCategoryChannel
		
	}

	public RssCategory getCategory(long id) {
		RssCategory rssCategory=rssCategoryDao.get(id);
		return rssCategory;
	}
	
	public void modifyCategory(long id,String name)throws Exception{
		List<RssCategory> list=rssCategoryDao.findBy("name",name);
		if(list != null && list.isEmpty()==false){
			for(int i=0;i<list.size();i++){
				RssCategory rss=list.get(i);
				if(rss.getId() != id){
					throw new Exception("RssLang.rss_exception_name");
				}
			}
			
		}
		User user=CurrentUser.get();
		RssCategory category=this.getCategory(id);
		category.setName(name);
		category.setLastUserId(user.getId());
		category.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		
		rssCategoryDao.update(category);
		
	}
	public boolean checkRepName(String name ,long category_id){
		boolean flag = false ;
		List list = rssCategoryChannelDao.findCategoryChannel(name, category_id);
		if(list == null || list.isEmpty()) {
			flag = true;
		}
		return flag ;
	}

	public boolean checkURL(String url) {
		boolean flag = false ;
		try{
			SyndFeed feed=this.parserRss(url);		//解析URL
			if(feed != null) {
				flag = true ;
			}
		}catch(Exception e){
			flag = false ;
		}
		return flag ;
	}
	
	public void addRssChannel(String url,String name,int orderNum,String description,long category_id)throws Exception {	
		/**
		List list=rssCategoryChannelDao.findCategoryChannel(name, category_id);
		if(list.isEmpty()==false){
			throw new Exception("RssLang.rss_exception_name");
		}
		**/
		RssCategoryChannel channel=new RssCategoryChannel();
		channel.setIdIfNew();
		channel.setName(name);
		channel.setUrl(url);
		User user=CurrentUser.get();
		channel.setCreateUserId(user.getId());
		channel.setCategoryId(category_id);
		channel.setCreateDate(new java.sql.Timestamp(new Date().getTime()));
		channel.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		channel.setLastUserId(user.getId());
		channel.setOrderNum(orderNum);
		channel.setDescription(description);
		
		//解析RSS种子
//		try {
			SyndFeed feed=this.parserRss(url);		//解析URL
			if(feed == null )throw new Exception("RssLang.rss_exception_address") ;			//不能解析
			RssChannelInfo channelInfo = this.parseChannel(feed, channel.getId());
			List<RssChannelItems> the_list=this.parseItem(feed, channelInfo.getId(), channelInfo.getCategoryChannelId());
			rssCategoryChannelDao.save(channel);
			rssChannelInfoDao.save(channelInfo);
			rssChannelItemDao.saveAll(the_list);
			

//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.error("RSS地址输入错误", e);
//			throw new Exception("RssLang.rss_exception_address");
//		}
	}

	public void deleteRssChannel(long id){
		rssCategoryChannelDao.delete(id);
	}

	public void modifyRssChannel(long id,String name,String url,int orderNum,String description,long category_id)throws Exception {

		List<RssCategoryChannel> list=rssCategoryChannelDao.findCategoryChannel(name, category_id);
		if(list.isEmpty()==false){
			for(int i=0;i<list.size();i++){
				RssCategoryChannel channel=list.get(i);
				if(channel.getId() != id){
					throw new Exception("RssLang.rss_exception_name");
				}
			}
		}
		RssCategoryChannel category_channel=this.getChannel(id);
		User user=CurrentUser.get();
		category_channel.setCategoryId(category_id);
		category_channel.setLastUpdate(new Timestamp(new Date().getTime()));
		category_channel.setLastUserId(user.getId());
		category_channel.setName(name);
		category_channel.setOrderNum(orderNum);
		category_channel.setDescription(description);
		if(category_channel.getUrl().equals(url)){
		//	category_channel.setUrl(url);
			rssCategoryChannelDao.update(category_channel);	
		}else{
			category_channel.setUrl(url);
			SyndFeed feed=this.parserRss(url);		//解析URL
			if(feed == null ){
				throw new Exception("RssLang.rss_exception_address") ;			//不能解析
			}else{
				rssChannelInfoDao.deleteChannelInfoById(id);		//删除原来的数据
				RssChannelInfo channelInfo = this.parseChannel(feed, id);
				List<RssChannelItems> the_list=this.parseItem(feed, channelInfo.getId(), channelInfo.getCategoryChannelId());
				rssChannelInfoDao.save(channelInfo);
				rssChannelItemDao.saveAll(the_list);
			}
			rssCategoryChannelDao.update(category_channel);	
			
		}
			
	}


	public RssCategoryChannel getChannel(long id) {
		RssCategoryChannel channel=rssCategoryChannelDao.get(id);
		return channel;
	}

	public List<List> getRssChannels()throws Exception {
		List<List> the_list=new ArrayList<List>();
		List<RssCategory> list=rssCategoryDao.getAll();
		for(int i=0;i<list.size();i++){
			List temp=new ArrayList();
			RssCategory rssCategory=(RssCategory)list.get(i);
			temp.add(rssCategory);
			List<RssCategoryChannel> chen_list=new ArrayList<RssCategoryChannel>();
			Set set=rssCategory.getCategoryChannel();		//级联加载对应的频道信息
			if(set != null){
				Iterator it=set.iterator();
				while(it.hasNext()){
					RssCategoryChannel channel=(RssCategoryChannel)it.next();
					chen_list.add(channel);
				}
			}
			
			temp.add(chen_list);
			
			the_list.add(temp);
		}

		return the_list;
	}
	
	//该方法提供给系统调用
	public synchronized void updateAllChannelInfo() throws Exception{
		// 判断开关是否打开
		boolean rssEnabled = Constants.rssModuleEnabled();
		if(!rssEnabled){
			log.debug("RSS模块关闭，不用更新。");
			return;
		}
		
		List<RssCategoryChannel> list=rssCategoryChannelDao.findCategoryChannelByGrop();	
		if(list == null || list.isEmpty())return ;
		

		List<RssChannelItems> _list = new ArrayList<RssChannelItems>();
		List<RssChannelInfo> infoList = new ArrayList<RssChannelInfo>();
		String infoIds = "";
		for(int i=0;i<list.size();i++){
			RssCategoryChannel channel=list.get(i);
			RssChannelInfo channelInfo;

			try {
				SyndFeed feed=this.parserRss(channel.getUrl());
				if(feed == null){
					continue;
				}
				infoIds += "," + channel.getId();

				channelInfo = this.parseChannel(feed, channel.getId());
				List<RssChannelItems> _list2 = this.parseItem(feed, channelInfo.getId(), channelInfo.getCategoryChannelId());
				infoList.add(channelInfo);
				if(_list2 != null)
					_list.addAll(_list2);
//				log.info("正常解析RSS种子：" + _list2.size());
			}  catch (Exception e) {
//				log.info("RSS更新连接异常：" + e.getMessage());
				continue;
			}
			
			
		}
		
		
		if(infoIds.length() > 0){
			//	只要发生更新,就把所有的阅读记录删除		
			rssItemStatuDao.deleteRssItemStatus();
			rssChannelInfoDao.deleteChannelInfoByIds(infoIds.substring(1, infoIds.length()));		//删除原来的数据
	
//			this.saveAllCommit(infoList);
//			this.saveAllCommit(_list);
			this.rssChannelInfoDao.saveAll(infoList);
			this.rssChannelItemDao.saveAll(_list);
			
			log.info("RSS 更新完成：" + _list.size());
		}else{
			log.info("未取得最新RSS数据，不更新。");
		}
		
	}
	
	
	
	

	public List<RssCategoryChannel> getChannels() {
		return rssCategoryChannelDao.getAll();
	}


	public RssCategoryChannelDao getRssCategoryChannelDao() {
		return rssCategoryChannelDao;
	}

	public void setRssCategoryChannelDao(RssCategoryChannelDao rssCategoryChannelDao) {
		this.rssCategoryChannelDao = rssCategoryChannelDao;
	}

	public List<RssCategory> getRssCategorys() {
		
		return rssCategoryDao.findCategorys();
	}

	public int getMaxChannelOrder() {
		
		return rssCategoryChannelDao.getMaxNumber();
	}
	
	//删除一个频道要删除对应的订阅
	public void deleteRssChannel(String deleteIds) {
		if(this.isSubscribe(deleteIds)){
			return ;
		}
		rssChannelInfoDao.deleteChannelInfoByIds(deleteIds);
		rssCategoryChannelDao.deleteChannels(deleteIds);			//级联删除对应的频道
		
	}
	
	//读取种子  为解析做准备
	private SyndFeed parserRss(String url) throws Exception {
		SyndFeed feed =null;
//		long start = System.currentTimeMillis();
		HttpClientUtil h = null;
		try{
			h = new HttpClientUtil(connTimeout * 1000);
			h.open(url, "get");
			int status = h.send();
			if(status == 200){
				InputStream in = h.getResponseBodyAsStream();
			
				XmlReader reader = new XmlReader(in);
				SyndFeedInput input = new SyndFeedInput();
			
				feed = input.build(reader);
			}
			else{
				log.warn("读取RSS种子：[" + url + "]错误： " + status);
			}
		}
		catch (Exception e) {
			log.warn("读取RSS种子：[" + url + "]超时. ");
//			throw new Exception("RssLang.rss_exception_address");
			return null;
		}
		finally{
			if(h != null){
				h.close();
			}
		}
		
		return feed;
	}
	
	//解析一个频道信息
	private RssChannelInfo parseChannel(SyndFeed feed,long categoryChannelId) {
		WireFeed the_feed = feed.createWireFeed();
		Channel channel = (Channel) the_feed;
		RssChannelInfo channel_info = new RssChannelInfo();
		channel_info.setIdIfNew();
		channel_info.setCategoryChannelId(categoryChannelId);
		channel_info.setTitle(channel.getTitle());
		channel_info.setLink(channel.getLink());
		channel_info.setDescribse(channel.getDescription());
		channel_info.setLanguage(channel.getLanguage());
		channel_info.setCopyright(channel.getCopyright());
		Image image = channel.getImage();
		if (image != null) {
			channel_info.setImageLink(image.getLink());
			channel_info.setImageTitle(image.getTitle());
			channel_info.setImageUrl(image.getUrl());
		}else {
			channel_info.setImageLink(null);
			channel_info.setImageTitle(null);
			channel_info.setImageUrl(null);
		}
		if(channel.getPubDate() != null){
			channel_info.setPubDate(channel.getPubDate().toString());
		}
		if(channel.getLastBuildDate()!=null){
			channel_info.setLastBuildDate(channel.getLastBuildDate().toString());
		}
		
		channel_info.setManagingEditor(channel.getManagingEditor());
		channel_info.setWebmaster(channel.getWebMaster());
		channel_info.setTtl(channel.getTtl());
		channel_info.setGenerator(channel.getGenerator());
		if (channel.getCategories().size()!=0) {
			channel_info
					.setCategory(((Category) channel.getCategories().get(0))
							.getValue());
		}else {
			channel_info.setCategory(null);
		}	
//		channel_info.setRssChannelItem(this.parseItem(feed, channel_info.getId()));
		return channel_info;
	}
	
	//解析一个频道对应的栏目信息
	private List<RssChannelItems> parseItem(SyndFeed feed,long channel_id,long categoryChannelId) {
		List<RssChannelItems> list=new ArrayList<RssChannelItems>();
		List the_list=feed.getEntries();
		List the_items = ((Channel) feed.createWireFeed()).getItems();
		if(the_list == null){
			return null;
		}
		String type=feed.getFeedType();
		for(int i=0;i<the_items.size();i++){
			RssChannelItems rssChannel=new RssChannelItems();
			Item item=(Item)the_items.get(i);
			rssChannel.setIdIfNew();
				
			for(int k=0;k<the_list.size();k++){
				SyndEntry entry=(SyndEntry)the_list.get(k);
				if(item.getLink().equals(entry.getLink())){
					rssChannel.setAuthor(entry.getAuthor());
					if(!type.equals("rss_1.0")){
						rssChannel.setPubDate(entry.getPublishedDate()== null?null:new java.sql.Timestamp(entry.getPublishedDate().getTime()));
					}
//					rssChannel.setPubDate(entry.getPublishedDate()== null?"":entry.getPublishedDate().toString());
					if(entry.getCategories() != null && entry.getCategories().isEmpty()==false){
						rssChannel.setCategory(((SyndCategory)entry.getCategories().get(0)).getName());
					}
//					List list5=entry.getModules();
					
					break;
				}
			}
			rssChannel.setCategoryChannelId(categoryChannelId);
			rssChannel.setChannelInfoId(channel_id);
			rssChannel.setComments(item.getComments());
			if(item.getDescription() != null){
				rssChannel.setDescribse(item.getDescription().getValue());
			}
			if(item.getGuid() != null){
				rssChannel.setGuid(item.getGuid().getValue());
			}
			rssChannel.setLink(item.getLink());
			rssChannel.setTitle(item.getTitle());
			if(item.getSource() != null){
				rssChannel.setSource(item.getSource().getValue());
			}
//			if(rssChannel.getPubDate() != null){
//				rssChannel.setPubDate(item.getPubDate().toString());
//			}
			
			list.add(rssChannel);
			
		}

		return list;
	}
	
	public RssChannelInfo getRssChannelByCategoryChannelId(long categoryChannelId) {
		return rssChannelInfoDao.findByCategoryChannelId(categoryChannelId);
	}

	public List<RssCategoryChannel> getSubscribedChannelById(long categoryId,String userType,long userId) {
		//获取一个用户的所有订阅
		List<RssSubscribe> list=rssSubscribeDao.getRssSubscribe(userType, userId);
		String channel="";
		List<RssCategoryChannel> the_list=new ArrayList<RssCategoryChannel>();
		if(list != null && list.isEmpty()==false){
			for(int i=0;i<list.size();i++){
				long theId=list.get(i).getCategoryChannelId();
				if(i != list.size()-1){
					channel+=theId;
					channel+=",";
				}else{
					channel+=theId;
				}
			}
//			得到一个用户在一个类别下的订阅频道
			the_list=rssCategoryChannelDao.getCategoryChannelByIds(channel,categoryId);
		}
		
		return the_list;
	}


	public List<RssCategoryChannel> getChannelByCategoryId(long categoryId) {
		
		return rssCategoryChannelDao.getCategoryChannels(categoryId);
	}	
	public List<RssCategoryChannel> getChannelByCategoryIdByPage(long categoryId) {
		return rssCategoryChannelDao.getCategoryChannelsByPage(categoryId);
	}
	
	public List<RssCategory> getAllRssCategories() {
		return rssCategoryDao.findCategorys();
	}
	
	public List<RssCategoryChannel> getAllRssChannels() {
		return rssCategoryChannelDao.getAllChannels();
	}
	public List<RssCategoryChannel> getAllRssChannelsByPage() {
		return rssCategoryChannelDao.getAllChannelsByPage();
	}
	
	
	/**
	 * 保存多个对象到数据库中, 单独起事务提交，所以要注意不能有未提交的外键
	 * @param os
	 */
	public void saveAllCommit(Collection os){
		int batch_size = 30;//Integer.parseInt(SystemProperties.getInstance().getProperty("db.jdbc.batch_size"));
		if(os != null && os.size() > 0){
			Session session = null;
			Transaction tx = null;
			try {
				session = rssChannelInfoDao.getSessionFactory1().openSession();
				
				tx = session.beginTransaction();
			
				Iterator iter = os.iterator();
				for (int i = 0; i < os.size(); i++) {	
					session.save(iter.next());
					
					if(i > 0 && (i % batch_size == 0)){
						session.flush();
						session.clear();
					}
				}
				
				tx.commit();
			}
			catch (Exception e) {
				if(tx != null)
				tx.rollback();
				log.error("RSS更新出现异常：", e);
			}
			finally{
				if(session != null)
				session.close();
			}
		}
	}
	
	public boolean isSubscribe(String ids) {
		boolean flag = false ;
		List<RssSubscribe> rssSubscribeList = this.rssSubscribeDao.findSubscribeByChannelId(ids);
		if(rssSubscribeList != null && rssSubscribeList.size() != 0) {
			flag = true ;
		}
		return  flag ;
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
//		log.info("设置timeout值：" + connTimeout);
	}
}
