package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.dao.CalEventRelevancyDao;
import com.seeyon.v3x.calendar.domain.CalEventRelevancy;

public class CalEventRelevancyManagerImpl implements CalEventRelevancyManager {
	
	private CalEventRelevancyDao calEventRelevancyDao;
	
	private CalEventManager calEventManager;

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	public CalEventRelevancyDao getCalEventRelevancyDao() {
		return calEventRelevancyDao;
	}

	public void setCalEventRelevancyDao(CalEventRelevancyDao calEventRelevancyDao) {
		this.calEventRelevancyDao = calEventRelevancyDao;
	}

	public void deleteById(Long id) {
		this.calEventRelevancyDao.delete(id.longValue());
	}

	public CalEventRelevancy getCalEventRelevancyById(Long id) {
		return calEventRelevancyDao.get(id);
	}

	public CalEventRelevancy save(CalEventRelevancy calEventRelevancy) {
		boolean isNew = calEventRelevancy.isNew();
		if(isNew)
			calEventRelevancy.setIdIfNew();
		return this.save(calEventRelevancy, isNew);
	}

	public CalEventRelevancy save(CalEventRelevancy calEventRelevancy, boolean isNew) {
		if(isNew){
			this.calEventRelevancyDao.save(calEventRelevancy);
		}else{
			this.calEventRelevancyDao.update(calEventRelevancy);
		}
		return calEventRelevancy;
	}

	public void deleteByRelevancyId(Long id) {
		List<CalEventRelevancy>  list = this.calEventRelevancyDao.find("relevancyId", id);
		for (CalEventRelevancy relevancy : list) {
			calEventManager.deleteById(relevancy.getCalId());
			calEventRelevancyDao.deleteObject(relevancy);
		}
	}
	
}
