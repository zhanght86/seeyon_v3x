package com.seeyon.v3x.hr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTransition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class DynamicFormDao extends BaseHibernateDao<Object> {
	
	private transient static final Log LOG = LogFactory
	.getLog(DynamicFormDao.class);
	
	public List getDynamicFormbyName(String tableName)throws Exception{
		Session session = super.getSession();
		List list = new ArrayList();
		List<Object> results = new ArrayList<Object>();
		try{
			if(!tableName.equals(""))
				list = session.createSQLQuery("select * from "+tableName+" where field0013 <> '1' or field0013 is null").list();
			for(Object object : list){
				Object[] obj = (Object[])object;
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),tableName);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			if(session != null)
				super.releaseSession(session);
		}
		return results;
	}
	
	public List getOverTimeFormbyName(String tableName)throws Exception{
		Session session = super.getSession();
		List list = new ArrayList();
		List<Object> results = new ArrayList<Object>();
		try{
			if(!tableName.equals(""))
				list = session.createSQLQuery("select * from "+tableName+" where field0012 <> '1' or field0012 is null").list();
			for(Object object : list){
				Object[] obj = (Object[])object;
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),tableName);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception ex){
			throw ex;
		}finally{
				super.releaseSession(session);
		}
		return results;
	}
	
	public void updateLeaveAndEvectionForm(String tableName, Long id)throws Exception {
		String sql = "update "+tableName+" set field0013='1' where id = ?";
		Session session = super.getSession();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = session.connection();
		    ps = conn.prepareStatement(sql);
			ps.setLong(1, id);
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally {
			if(ps!=null){
			   try{
				   ps.close();   
			   }catch(Exception e){
					throw e;  
			   }
			   
			}
            if (conn != null) {
     		   try{
     			  conn.close();   
			   }catch(Exception e){
					throw e;  
			   }	          
	        }
            super.releaseSession(session);
        }
	}
	
	public void updateOverTimeForm(String tableName, Long id)throws Exception {
		String sql = "update "+tableName+" set field0012='1' where id = ?";
		Session session = super.getSession();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = session.connection();
		    ps = conn.prepareStatement(sql);
			ps.setLong(1, id);
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
	    }finally {
				if(ps!=null){
					   try{
						   ps.close();   
					   }catch(Exception e){
							throw e;  
					   }
					   
					}
		            if (conn != null) {
		     		   try{
		     			  conn.close();   
					   }catch(Exception e){
							throw e;  
					   }	          
			        }
            super.releaseSession(session);
        }
	}
	
	/**
	 * 根据表单数据id得到协同的ColSummary对象
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ColSummary getColSummaryByFormId(Long formid, String tableName)throws Exception{
		String hql1 = "from ColBody where content like :content and body_type = :form";
		String hql2 = "from ColSummary where id = :id";
		Session session = super.getSession();
		ColSummary colSummary = null;
		String name = "";
		try{
			List<ColBody> colBodys = session.createQuery(hql1)
			                                                 .setString("content", formid.toString())
			                                                 .setString("form", "FORM")
			                                                 .list();
			
			for(ColBody colBody:colBodys){
	        	colSummary = (ColSummary)session.createQuery(hql2). setLong("id", colBody.getSummaryId()).uniqueResult();
				Long caseId = colSummary.getCaseId();
				if(caseId == null) return null;
				BPMProcess process = ColHelper.getRunningProcessByCaseId(caseId);
				List endList = process.getEnds();
				BPMStatus end = (BPMStatus)endList.get(0);
				List trans = end.getUpTransitions();
				BPMAbstractNode activity = ((BPMTransition)trans.get(0)).getFrom();
				BPMSeeyonPolicy policy = activity.getSeeyonPolicy();
	
				ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(policy.getFormApp()));
			    SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl)afapp;
			    if(sapp!=null){
			    	SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
			    	name=seedade.getDataDefine().getTableLst().get(0).getName();
			    }
				if(!name.equals(tableName)){
					colSummary = null;
					continue;
				}
				else{
					break;
				}
	        }
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		
		return colSummary;
	}
	
	
	/**
	 * 根据姓名查询表单
	 */
	
	public List findOverTimeFormByMemberName(String name, String tableName)throws Exception{
		List list = this.getOverTimeFormbyName(tableName);
		List<Object> results = new ArrayList<Object>();
		for(Object object : list){
			Object[] obj = (Object[])object;
			if(obj[2].toString().contains(name))
				results.add(obj);
		}
		return results;
	}
	
	public List findDynamicFormByMemberName(String name, String tableName)throws Exception{
		List list = this.getDynamicFormbyName(tableName);
		List<Object> results = new ArrayList<Object>();
		for(Object object : list){
			Object[] obj = (Object[])object;
			if(obj[2].toString().contains(name))
				results.add(obj);
		}
		return results;
	}
	
	public List findLeaveFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		List list = new ArrayList();
		List<Object> results = new ArrayList<Object>();
		Session session = super.getSession();
		try{
			String sql = "select * from "+tableName+" where (field0013 <> '1' or field0013 is null) and (field0001 >= :fromTime and field0001 <= :toTime)";
			if(!tableName.equals(""))
				list = session.createSQLQuery(sql).setTimestamp("fromTime", fromTime).setTimestamp("toTime", toTime).list();
			for(Object object : list){
				Object[] obj = (Object[])object;
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),tableName);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		return results;
	}
	
	public List findOverTimeFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		List list = new ArrayList();
		List<Object> results = new ArrayList<Object>();
		Session session = super.getSession();
		try{
			String sql = "select * from "+tableName+" where (field0012 <> '1' or field0012 is null) and (field0001 >= :fromTime and field0001 <= :toTime)";
			if(!tableName.equals(""))
				list = session.createSQLQuery(sql).setTimestamp("fromTime", fromTime).setTimestamp("toTime", toTime).list();
			for(Object object : list){
				Object[] obj = (Object[])object;
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),tableName);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		return results;
	}
	
	public List findEvectionFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		List list = new ArrayList();
		List<Object> results = new ArrayList<Object>();
		Session session = super.getSession();
		try{
			String sql = "select * from "+tableName+" where (field0013 <> '1' or field0013 is null) and (field0001 >= :fromTime and field0001 <= :toTime)";
			if(!tableName.equals(""))
				list = session.createSQLQuery(sql).setTimestamp("fromTime", fromTime).setTimestamp("toTime", toTime).list();
			for(Object object : list){
				Object[] obj = (Object[])object;
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),tableName);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		return results;
	}
	
}
