package com.seeyon.v3x.bulletin.manager;

import java.util.Date;
import java.util.List;
import com.seeyon.v3x.bulletin.dao.BulReadDao;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulRead;

public class BulReadManagerImpl extends BaseBulletinManager implements BulReadManager{
	private BulReadDao bulReadDao;
	public void setBulReadDao(BulReadDao bulReadDao) {
		this.bulReadDao = bulReadDao;
	}
	
	public void configReadByData(BulData data) {
		deleteReadByData(data);
	}
	
	public BulRead getReadById(Long id) {
		return this.bulReadDao.get(id);
	}
	
	public BulRead getReadState(BulData data, Long userId) {
		String hsql="from BulRead as read where read.bulletin.id=? and read.managerId=?";
		Object[] values=new Object[]{data.getId(),userId};
		List<BulRead> list = this.bulReadDao.find(hsql, values);
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public void setReadState(BulData data,Long userId) {
		String hsql="from BulRead as read where read.bulletin.id=? and read.managerId=?";
		Object[] values=new Object[]{data.getId(),userId};
		
		List<BulRead> list = this.bulReadDao.find(hsql, values);
		if(list==null || list.size()==0) {
			BulRead read=new BulRead();
			read.setIdIfNew();
			read.setBulletin(data);
			read.setManagerId(userId);
			read.setReadDate(new Date());
			read.setReadFlag(true);
			read.setAccountId(data.getAccountId());
			this.bulReadDao.save(read);
		}
	}
	
	public void deleteReadByData(BulData data) {
		this.bulReadDao.bulkUpdate("delete from BulRead as read where read.bulletin.id = ?", null, data.getId());
	}
	
	public List<BulRead> getReadListByData(Long bulletinId) {			
		return this.bulReadDao.find("from BulRead as read where read.bulletin.id=?", bulletinId);	
	}
	
	public List<BulRead> getReadListByUser(Long userId) {
		return this.bulReadDao.find("from BulRead as read where read.managerId=?", userId);
	}	
}
