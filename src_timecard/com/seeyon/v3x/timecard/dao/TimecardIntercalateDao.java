package com.seeyon.v3x.timecard.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.timecard.domain.TimecardIntercalate;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: new
 * Date: 2007-1-30
 * Time: 16:13:36
 * To change this template use File | Settings | File Templates.
 */

public class TimecardIntercalateDao extends BaseHibernateDao{
    public TimecardIntercalate getTimecardIntercalateByMemberID(Long memberId){
		TimecardIntercalate timecardIntercalate = new TimecardIntercalate();
		timecardIntercalate.setMemberId(memberId);
		List<TimecardIntercalate> rs = super.findByExample(timecardIntercalate);
		if(rs != null && !rs.isEmpty()){
			return rs.get(0);
		}
		return null;
	}
}
