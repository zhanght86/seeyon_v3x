package com.seeyon.v3x.meeting.dao;

import java.util.ArrayList;
import java.util.List;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.meeting.domain.MtConferee;
import com.seeyon.v3x.util.Strings;

/**
 * 会议与会对象Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-12-28
 */
public class MtConfereeDao extends BaseHibernateDao<MtConferee> {
	
	/**
	 * 将会议的与会者保存至mt_conferee表中
	 * @param meetingId     会议ID
	 * @param confereesStr  会议与会对象Type|Id...
	 */
	public void saveConferees(Long meetingId, String confereesStr) {
		List<MtConferee> conferees = new ArrayList<MtConferee>();
		String[][] typeAndIds = Strings.getSelectPeopleElements(confereesStr);
		for(int i=0; i<typeAndIds.length; i++) {
			conferees.add(new MtConferee(Long.parseLong(typeAndIds[i][1]), typeAndIds[i][0], i, meetingId));
		}
		this.savePatchAll(conferees);
	}
		
}
