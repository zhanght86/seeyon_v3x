package com.seeyon.v3x.mobile.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.mobile.message.StatisticAccount;
import com.seeyon.v3x.mobile.message.StatisticDepartment;
import com.seeyon.v3x.mobile.message.domain.MobileMessage;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;


public class MobileMessageDaoImpl extends BaseHibernateDao<MobileMessage>
		implements MobileMessageDao {
	private static final Log log = LogFactory
			.getLog(MobileMessageDaoImpl.class);
	
	private int deleteSpaceDate;
	
	private OrgManager orgManager;
	
	public void setDeleteSpaceDate(int deleteSpaceDate) {
		this.deleteSpaceDate = deleteSpaceDate;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MobileMessage getMessageById(Long id) {
		List<MobileMessage> list = super.find(
				"from MobileMessage as a where a.id=?", id);
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	public void saveMobileMessage(MobileMessage m) {
		super.save(m);

	}

	public void deleteMessageById(Long id) {
		String sql = "delete from MobileMessage as a where a.id=?";
		super.bulkUpdate(sql, null, id);
	}

	public void updateById(Long id,boolean send) {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("state", send ? MobileMessage.STATE.success.ordinal() : MobileMessage.STATE.failure.ordinal());
		
		super.update(MobileMessage.class, columns, new Object[][]{{"id", id}});
	}
	
	public void updateMobileMessageState() {
		Date currentDate = Datetimes.addDate(new Date(System.currentTimeMillis()), -deleteSpaceDate);
		String hsql = " update MobileMessage set state=? where time <= ?";
		super.bulkUpdate(hsql, null, MobileMessage.STATE.delete.ordinal(), currentDate);
	}
	
	public MobileMessage getMobileMessageByFeatureCode(String str) {
		String hsql = "from MobileMessage as a where a.featureCode=? and a.state=?";
		List<MobileMessage> list = super.find(hsql, new Object[]{str, MobileMessage.STATE.success.ordinal()});
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<StatisticAccount> statisticByAccount(Date startDate, Date toDate){
		List<StatisticAccount> result = new ArrayList<StatisticAccount>();
		
        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("accountId"));
        projections.add(Projections.groupProperty("smsType"));
        projections.add(Projections.count("smsType"));
        
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MobileMessage.class)
            .setProjection(projections)
        ;
        
        if(startDate != null){
            detachedCriteria.add(Expression.ge("time", startDate));
        }
        if(toDate != null){
            detachedCriteria.add(Expression.le("time", toDate));
        }
        
        List<Object[]> list = super.executeCriteria(detachedCriteria, -1, -1); 
		if(list != null){
			for (Object[] objects : list) {
				result.add(new StatisticAccount((Integer)objects[2], (Long)objects[0], (Integer)objects[1]));
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<StatisticDepartment> statisticByDepartment(long accountId, List<Long> departmentIds,
			Date startDate, Date toDate) {
		List<StatisticDepartment> result = new ArrayList<StatisticDepartment>();
		
		if(departmentIds.isEmpty()){
			return result;
		}
        
        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("departmentId"));
        projections.add(Projections.groupProperty("smsType"));
        projections.add(Projections.count("smsType"));
        
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MobileMessage.class)
            .setProjection(projections)
            .add(Expression.eq("accountId", accountId))
            .add(Expression.in("departmentId", departmentIds))
        ;
        
		if(startDate != null){
            detachedCriteria.add(Expression.ge("time", startDate));
		}
		if(toDate != null){
            detachedCriteria.add(Expression.le("time", toDate));
		}
		
		List<Object[]> list = super.executeCriteria(detachedCriteria, -1, -1);
		if(list != null){
			for (Object[] objects : list) {
				result.add(new StatisticDepartment((Integer)objects[2], (Long)objects[0], (Integer)objects[1]));
			}
		}
		
		return result;
	}

}
