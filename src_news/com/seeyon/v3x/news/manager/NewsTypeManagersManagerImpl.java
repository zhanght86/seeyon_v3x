package com.seeyon.v3x.news.manager;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import com.seeyon.v3x.news.dao.NewsTypeManagersDao;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.domain.NewsTypeManagers;

/**
 * 主要对新闻类型与新闻管理员、新闻发起员的关联进行操作
 * @author wolf
 *
 */
public class NewsTypeManagersManagerImpl extends BaseNewsManager implements NewsTypeManagersManager {
	private NewsTypeManagersDao newsTypeManagersDao;

	public NewsTypeManagersDao getNewsTypeManagersDao() {
		return newsTypeManagersDao;
	}

	public void setNewsTypeManagersDao(NewsTypeManagersDao newsTypeManagersDao) {
		this.newsTypeManagersDao = newsTypeManagersDao;
	}
	

//	/* (non-Javadoc)
//	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#deletes(com.seeyon.v3x.news.domain.NewsType)
//	 */
//	public void deletes(NewsType type){
//		//String hql="from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
//		//Object[] values=new Object[]{type.getId(),Constants.MANAGER_FALG};
//		//应该删除该类型的新闻管理员和新闻发起员，而不是仅仅删除新闻管理员
//		String hql="from NewsTypeManagers as tm where tm.type.id=?";
//		Object[] values=new Object[]{type.getId()};
//		this.newsTypeManagersDao.delete(hql, values);
//		
//	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#saves(com.seeyon.v3x.news.domain.NewsType, java.lang.String[])
	 */
//	public void saves(NewsType type,String[] userIds){
//		this.deletesManager(type);
//		
//		Set<NewsTypeManagers> set=type.getNewsTypeManagers();
//		if(set==null){
//			set=new HashSet<NewsTypeManagers>();
//		}
//		set.clear();	
//		
//		for(String userId:userIds){
//			NewsTypeManagers tm=new NewsTypeManagers();
//			tm.setIdIfNew();
//			tm.setType(type);
//			tm.setManagerId(Long.valueOf(userId));
//			tm.setExt1(Constants.MANAGER_FALG);
//			newsTypeManagersDao.save(tm);
//			set.add(tm);
//		}
//		type.setNewsTypeManagers(set);
//	}
	/**
	 * 保存板块类型的管理员--授权
	 */
	public void saveAclByType(NewsType type, String[][] userIds, String extFlag){
		if(type == null || userIds == null || extFlag == null)
			return;
		
		this.deleteByType(type, extFlag);
		
		Set<NewsTypeManagers> set=type.getNewsTypeManagers();
		if(set==null){
			set=new HashSet<NewsTypeManagers>();
		}else{
			Set<NewsTypeManagers> set2 = new HashSet<NewsTypeManagers>();
			set2.addAll(set);
			for(NewsTypeManagers btm : set2){
				if(extFlag.equals(btm.getExt1()))
					set.remove(btm);
			}
		}			
		
//		for(String[] userId:userIds){
		for(int i=0;i<userIds.length;i++) { 
			NewsTypeManagers tm=new NewsTypeManagers();
			tm.setIdIfNew();
			tm.setType(type);
			//tm.setManagerId(new Long(userId[1]));
			tm.setManagerId(new Long(userIds[i][1]));
			tm.setExt1(extFlag);
			//tm.setExt2(userId[0]);
			tm.setExt2(userIds[i][0]);
			// 增加排序设定，便于之后保持顺序 added by Meng Yang at 2009-06-30
			tm.setOrderNum(Integer.valueOf(i));
			newsTypeManagersDao.save(tm);
			set.add(tm);
		}
		type.setNewsTypeManagers(set);
	}
	
	/**
	 * 保存板块类型的管理员
	 */
	public void saveAclByTypeManager(NewsType type, String[] userIds, String extFlag){
		if(type == null || extFlag == null)
			return;
		
		this.deleteByType(type, extFlag);
		
		Set<NewsTypeManagers> set=type.getNewsTypeManagers();
		if(set==null){
			set=new HashSet<NewsTypeManagers>();
		}else{
			Set<NewsTypeManagers> set2 = new HashSet<NewsTypeManagers>();
			set2.addAll(set);
			for(NewsTypeManagers btm : set2){
				if(extFlag.equals(btm.getExt1()))
					set.remove(btm);
			}
		}			
		
		/*for(String userId:userIds){
			NewsTypeManagers tm=new NewsTypeManagers();
			tm.setIdIfNew();
			tm.setType(type);
			tm.setManagerId(new Long(userId));
			tm.setExt1(extFlag);
			tm.setExt2("Member");
			newsTypeManagersDao.save(tm);
			set.add(tm);
		}*/
		
		for(int i=0;i<userIds.length;i++) { 
			NewsTypeManagers tm=new NewsTypeManagers();
			tm.setIdIfNew();
			tm.setType(type);
			//tm.setManagerId(new Long(userId[1]));
			tm.setManagerId(new Long(StringUtils.isBlank(userIds[i])?"-1":userIds[i]));
			tm.setExt1(extFlag);
			//tm.setExt2(userId[0]);
			tm.setExt2("Member");
			// 增加排序设定，便于之后保持顺序 added by Meng Yang at 2009-06-30
			tm.setOrderNum(Integer.valueOf(i));
			newsTypeManagersDao.save(tm);
			set.add(tm);
		}
		type.setNewsTypeManagers(set);
	}
	
	private void deleteByType(NewsType type, String extFlag){
		if(type == null || extFlag == null)
			return;
		
		String hql="delete from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
		Object[] values=new Object[]{type.getId(),extFlag};
		this.newsTypeManagersDao.bulkUpdate(hql, null, values);
	}
	
//	/* (non-Javadoc)
//	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#findByProperty(java.lang.String, java.lang.Object)
//	 */
//	public List<NewsTypeManagers> findByProperty(String property,Object value) throws Exception{
//		NewsTypeManagers example=new NewsTypeManagers();
//		example.setExt1(Constants.MANAGER_FALG);
//		try {
//			BeanUtils.setProperty(example, property, value);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		return this.newsTypeManagersDao.findByExample(example);
//	}
	
	
//	/* (non-Javadoc)
//	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#findWriteByType(com.seeyon.v3x.news.domain.NewsType)
//	 */
//	public List<NewsTypeManagers> findWriteByType(NewsType type){		
//		String hql="from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
//		Object[] values=new Object[]{type.getId(),Constants.WRITE_FALG};
//		List<NewsTypeManagers> list=this.newsTypeManagersDao.find(hql, values);
//		return list;
//	}
	
//	/* (non-Javadoc)
//	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#findTypeByWrite(java.lang.Long)
//	 */
//	public List<NewsTypeManagers> findTypeByWrite(Long userId){
//		String hql="from NewsTypeManagers as tm where tm.managerId=? and tm.ext1=?";
//		Object[] values=new Object[]{userId,Constants.WRITE_FALG};
//		List<NewsTypeManagers> list=this.newsTypeManagersDao.find(hql, values);
//		return list;
//	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#saveWriteByType(com.seeyon.v3x.news.domain.NewsType, java.lang.Long[])
	 */
//	public void saveWriteByType(NewsType type,Long[] writeIds){
//		this.deletesWriter(type);
//		
//		for(Long writeId:writeIds){
//			NewsTypeManagers tm=new NewsTypeManagers();
//			tm.setIdIfNew();
//			tm.setManagerId(writeId);
//			tm.setType(type);
//			tm.setExt1(Constants.WRITE_FALG);
//			this.newsTypeManagersDao.save(tm);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#deletesManager(com.seeyon.v3x.news.domain.NewsType)
//	 */
//	public void deletesManager(NewsType type) {
//		String hql="from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
//		Object[] values=new Object[]{type.getId(),Constants.MANAGER_FALG};
//		this.newsTypeManagersDao.delete(hql, values);
//	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTypeManagersManager#deletesWriter(com.seeyon.v3x.news.domain.NewsType)
	 */
//	public void deletesWriter(NewsType type) {
//		String hql="from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
//		Object[] values=new Object[]{type.getId(),Constants.WRITE_FALG};
//		this.newsTypeManagersDao.delete(hql, values);
//	}

//	public List<NewsTypeManagers> findTypeManagerId(Long typeId) throws Exception {
//		String hql="from NewsTypeManagers as tm where tm.type.id=? and tm.ext1=?";
//		Object[] values=new Object[]{typeId,Constants.WRITE_FALG};
//		List<NewsTypeManagers> list=this.newsTypeManagersDao.find(hql, values);
//		return list;
//	}
	
//	/**
//	 * 取得type集合的所有管理员
//	 */
//	public List<NewsTypeManagers> getTypeManagers(List<NewsType> types){
//		if(types == null || types.size() == 0)
//			return new ArrayList<NewsTypeManagers>();
//		
//		String typeIds = "";
//		for(NewsType t : types){
//			typeIds += "," + t.getId();
//		}
//		
//		String hql="from NewsTypeManagers as tm where tm.type.id in(" + typeIds.substring(1, typeIds.length()) + ") ";
//		
//		return newsTypeManagersDao.find(hql);
//	}
}
