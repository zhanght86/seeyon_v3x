package com.seeyon.v3x.edoc.dao;

import java.util.List;

import org.hibernate.Query;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocSuperviseDetail;
import com.seeyon.v3x.edoc.util.Constants;

public class EdocSuperviseDetailDao extends BaseHibernateDao<EdocSuperviseDetail> {

	public List<EdocSuperviseDetail> findToBeProcessedDetailBySupervisor(Long supervisorId){
		
		String queryString = "from EdocSuperviseDetail as detail where detail.edocSupervisors.supervisorId = ? and detail.status = ?";
		Object[] values = new Object[]{supervisorId, Constants.EDOC_SUPERVISE_PROGRESSING};
		return super.find(queryString, values);
		/*
		Query query = getSession().createQuery(queryString);
		query.setLong(0, supervisorId);
		List<EdocSuperviseDetail> list = query.list();
		if(null!=list && list.size()>0){
			return list;
		}else{
			return null;
		}
		*/
	} 
	public List<EdocSuperviseDetail> findProcessedDetailBySupervisor(Long supervisorId){
		
		String queryString = "from EdocSuperviseDetail as detail where detail.edocSupervisors.supervisorId = ? and detail.status = ?";
		Object[] values = new Object[]{supervisorId, Constants.EDOC_SUPERVISE_TERMINAL};
		return super.find(queryString, values);
		/*
		Query query = getSession().createQuery(queryString);
		query.setLong(0, supervisorId);
		List<EdocSuperviseDetail> list = query.list();
		if(null!=list && list.size()>0){
			return list;
		}else{
			return null;
		}
		*/
	} 
	
	/**
	 * 根据公文id查找所有的detail记录,每一条公文只可能对应一条督办记录
	 * @param summaryId
	 * @return
	 */
	public EdocSuperviseDetail findEdocSuperviseDetailBySummaryId(Long summaryId){
		
		String queryString = "from EdocSuperviseDetail as de where de.edocId = ?";
		List <EdocSuperviseDetail> list = super.find(queryString, summaryId);
		if(null!=list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public void saveOrUpdateDetail(EdocSuperviseDetail detail){
		super.getHibernateTemplate().saveOrUpdate(detail);
	}
	
}
