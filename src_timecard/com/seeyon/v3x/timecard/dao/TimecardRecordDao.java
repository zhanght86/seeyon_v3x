package com.seeyon.v3x.timecard.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.timecard.domain.TimecardRecord;

import java.util.List;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: new
 * Date: 2007-1-30
 * Time: 16:11:06
 * To change this template use File | Settings | File Templates.
 */

public class TimecardRecordDao extends BaseHibernateDao {
	public List getTimecardRecordByMonth(String currentMonth, Long memberId){
		Session session = getSession();
		List list = null;
		try{
			list = session.createQuery("from com.seeyon.v3x.timecard.domain.TimecardRecord t " +
        		"where t.memberId = ? and t.workDate like '" + currentMonth + "%'").setLong(0,memberId).list();
		}catch(Throwable ex){
			ex.printStackTrace();
		}
		return list;
    }
	public TimecardRecord getTimecardRecordByDate(String currentDate, Long memberId){
		Session session = getSession();
		List<TimecardRecord> list = session.createQuery("from com.seeyon.v3x.timecard.domain.TimecardRecord t " +
        		"where t.memberId = " + memberId + " and t.workDate = '" + currentDate + "'").list();
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
    }
    public void save(TimecardRecord timecardRecord){
    	try{
        super.save(timecardRecord);
    	}catch(Throwable ex){
    		ex.printStackTrace();
    	}
    }
    public void update(TimecardRecord timecardRecord){
        super.update(timecardRecord);
    }
}