package com.seeyon.v3x.bulletin.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.bulletin.dao.BulDataDao;
import com.seeyon.v3x.bulletin.dao.BulLogDao;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulLog;

public class BulLogManagerImpl extends BaseBulletinManager implements BulLogManager {
	private BulLogDao bulLogDao;
	private BulDataDao bulDataDao;
	
	private List<BulLog> initList(List<BulLog> logList){
		for(BulLog log:logList){
			if(log.getUserId()!=null){
				log.setUserName(this.getBulletinUtils().getMemberNameByUserId(log.getUserId()));
			}
			if(log.getRecordId()!=null){
				BulData data=null;
				try{
					data=this.getBulDataDao().get(log.getRecordId());
				}catch(Exception e){
					//
				}
				if(data!=null)
					log.setRecordTitle(data.getTitle());
			}
		}
		return logList;
	}
	
	public List<BulLog> findAll() {
		return initList(bulLogDao.getAll());
	}

	
	@SuppressWarnings("unchecked")
	public List<BulLog> findByExample(BulLog log) {
		List<BulLog> list=null;
		DetachedCriteria dc=DetachedCriteria.forClass(BulLog.class);
		dc.add(Example.create(log));
		
		dc.addOrder(Order.desc("recordDate"));
		
		list=bulLogDao.paginate(
				dc.getExecutableCriteria(this.bulLogDao.getSessionFactory().getCurrentSession())		
			);
		
		return initList(list);
	}

	public void record(BulLog log) {
		log.setRecordDate(new Date());
		if(log.isNew()) log.setIdIfNew();
		bulLogDao.save(log);
	}

	public BulLogDao getBulLogDao() {
		return bulLogDao;
	}

	public void setBulLogDao(BulLogDao bulLogDao) {
		this.bulLogDao = bulLogDao;
	}

	public BulDataDao getBulDataDao() {
		return bulDataDao;
	}

	public void setBulDataDao(BulDataDao bulDataDao) {
		this.bulDataDao = bulDataDao;
	}

}
