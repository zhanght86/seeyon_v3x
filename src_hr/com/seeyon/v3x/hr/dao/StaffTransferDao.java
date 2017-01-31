package com.seeyon.v3x.hr.dao;

import java.math.BigDecimal;
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
import org.hibernate.Query;
import org.hibernate.Session;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.hr.StaffTransferFlag;
import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;

public class StaffTransferDao extends BaseHibernateDao<StaffTransfer>{
	private transient static final Log LOG = LogFactory
			.getLog(StaffTransferDao.class);
	
	private <T> List<T> pagination(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}

	/**
	 * 获得所有调配记录
	 * 
	 */
	public List<StaffTransfer> getStaffTransfer() throws Exception {
		String hql = "From StaffTransfer order by refer_time desc";
		List<StaffTransfer> staffTransfers = (List<StaffTransfer>)getHibernateTemplate().find(hql);
		return this.pagination(staffTransfers);
	}

//	/**
//	 * 根据人员id查询调配记录
//	 * 
//	 */
//	public List<StaffTransfer> getStaffTransferByMemberId(long staffid)
//			throws Exception {
//		List<StaffTransfer> staffTransfers = (List<StaffTransfer>)getHibernateTemplate().find("From StaffTransfer where member_id = "+staffid+" order by refer_time desc");
//
//		return staffTransfers;
//	}

	/**
	 * 根据人员名称模糊查询调配记录
	 * 
	 * @param match
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> findStaffTransferLikeByName(String match, String fname)
			throws Exception {
		Session session = super.getSession();
		List<Object[]> results = new ArrayList();
		try{
			String hql = "select id From com.seeyon.v3x.organization.domain.V3xOrgMember where name like :match";
			
			Query query = session.createQuery(hql.toString()).setString("match", "%" + match + "%");
			List<Long> memberIds = query.list();
			List<Object[]> transfers = this.getFormByName(fname);
			
			for(Long memberId : memberIds){
				for(Object[] transfer : transfers){
					if(memberId.toString().equals(transfer[21].toString()))
						results.add(transfer);
				}
			}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return results;
	}

	/**
	 * 查询变动类型为调配的记录
	 * 
	 */
	public List<StaffTransfer> getTransferTypeStaffTransfer() throws Exception {
		Session session = super.getSession();
//		String countHql = "select count(*) From StaffTransfer where type IN (:type1,:type2,:type3,:type4,:type5)";
//		Query countQuery = super.getSession().createQuery(countHql).setInteger("type1",
//				StaffTransferFlag.FULLMEMBER).setInteger("type2",
//				StaffTransferFlag.TRANSFERPOST).setInteger("type3",
//				StaffTransferFlag.UNSETTLED).setInteger("type4",
//				StaffTransferFlag.DEMOTION).setInteger("type5",
//				StaffTransferFlag.OTHER);
//		setPaginationRowCount(countQuery);
        
		List<StaffTransfer> staffTransfers = new ArrayList<StaffTransfer>();
		try{
			String hql = "From StaffTransfer where type IN (:type1,:type2,:type3,:type4,:type5) order by refer_time desc";
			Query query = session.createQuery(hql).setInteger("type1",
					StaffTransferFlag.FULLMEMBER).setInteger("type2",
					StaffTransferFlag.TRANSFERPOST).setInteger("type3",
					StaffTransferFlag.UNSETTLED).setInteger("type4",
					StaffTransferFlag.DEMOTION).setInteger("type5",
					StaffTransferFlag.OTHER);
			staffTransfers = (List<StaffTransfer>)query.list();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return this.pagination(staffTransfers);
	}

	/**
	 * 查询变动类型为离职的记录
	 * 
	 */
	public List<StaffTransfer> getDimissionTypeStaffTransfer() throws Exception {
		Session session = super.getSession();
		List<StaffTransfer> staffTransfers = new ArrayList<StaffTransfer>();
		try{
			String countHql = "select count(*) From StaffTransfer where type = :transferType";
			Query countQuery = session.createQuery(countHql).setInteger(
					"transferType", StaffTransferFlag.DIMISSION);
			setPaginationRowCount(countQuery);
			
			String hql = "From StaffTransfer where type = :transferType order by refer_time desc";
			Query query = session.createQuery(hql).setInteger(
					"transferType", StaffTransferFlag.DIMISSION);
			staffTransfers = (List<StaffTransfer>)query.list();
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}


		return this.pagination(staffTransfers);
	}
	
	/**
	 * 根据变动类型查询调配记录
	 * @param transferType
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getStaffTransferByType(int transferType, String fname)throws Exception {
		Session session = super.getSession();
		List<Object[]> results = new ArrayList<Object[]>();
		try{
			String sql = "select * from "+fname+" where field0016=:transferType and (field0019 <> '1' or field0019 is null) order by field0012 desc";     
			
			Query query = session.createSQLQuery(sql).setInteger("transferType", transferType);
	        
			List<Object[]> result = query.list();
			
			for(Object[] obj : result){
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),fname);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return this.pagination(results);		
	}
	
	/**
	 * 根据状态查询调配记录
	 * 
	 */
	public List<Object[]> getStaffTransferByState(int state, String fname)throws Exception {
		Session session = super.getSession();
		List<Object[]> results = new ArrayList<Object[]>();
		try{	
		    String sql = "select * from "+fname+" where field0013=:state and (field0019 <> '1' or field0019 is null) order by field0012 desc";
	
			Query query = session.createSQLQuery(sql).setInteger("state", state);
	        
			List<Object[]> result = query.list();
			for(Object[] obj : result){
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),fname);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return this.pagination(results);		
	}

	/**
	 * 根据提交时间查询调配记录
	 * 
	 */
	public List<Object[]> getStaffTransferByReferTime(Date referTime, String fname)
			throws Exception {
		Session session = super.getSession();
		List<Object[]> results = new ArrayList<Object[]>();
		try{
	        String sql = "select * from "+fname+" where (field0019 <> '1' or field0019 is null) and field0012=:referTime ";
			Query query = session.createSQLQuery(sql).setTimestamp("referTime", referTime);
	        
			List<Object[]> result = query.list();
			for(Object[] obj : result){
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),fname);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

	    return this.pagination(results);	
	}

	/**
	 * 删除一条调配记录
	 * 
	 */
	public void deleteTransfer(long id) throws Exception {
		super.delete(id);
	}

	/**
	 * 根据typeId得到StaffTransferType对象
	 * 
	 * @param typeId
	 * @return
	 */
	public StaffTransferType getStaffTransferType(int typeId) throws Exception{	
		Session session = super.getSession();
		StaffTransferType staffTransferType = new StaffTransferType();
		try{
			String hql = "From StaffTransferType where id = :typeId";
			Query query = super.getSession().createQuery(hql).setInteger("typeId",
					typeId);
			staffTransferType = (StaffTransferType)query.uniqueResult();
		}catch (Exception e) {
		    throw e;
		}finally {
		    super.releaseSession(session);
		}

		return staffTransferType;

	}
	
	/**
	 * 设置带参数Hql翻页时的数据总条数
	 * @param countQuery
	 */
	private void setPaginationRowCount(Query countQuery) {
		Pagination.setNeedCount(false);
		BigDecimal rowCount = (BigDecimal) countQuery.uniqueResult();
        Pagination.setRowCount(rowCount.intValue());		
	}
	
	
	/*---------------------------------------- 2007-09-12 ---------------------------------------------*/

	/**
	 * 查询所有待处理调配表单
	 * 
	 */
	public List<Object[]> getFormByName(String fname)throws Exception{
		Session session = super.getSession();
		List<Object[]> results = new ArrayList<Object[]>();
		try{
			String sql = "select * from "+fname+" where field0019 <> '1' or field0019 is null order by field0012 desc";
	
			Query query = session.createSQLQuery(sql);
	        
			List<Object[]> result = query.list();
			for(Object[] obj : result){
				ColSummary colSummary = this.getColSummaryByFormId(Long.valueOf(obj[0].toString()),fname);
				if(colSummary!=null && colSummary.getFinishDate()!=null){
					results.add(obj);
				}
			}
		}catch(Exception e){
			  throw e;
		}
		finally {
		    super.releaseSession(session);
		}

    return this.pagination(results);
	}
	
	/**
	 * 根据表单数据id得到协同的ColSummary对象
	 * 
	 */
	public ColSummary getColSummaryByFormId(Long formid, String fname)throws Exception{
		Session session = super.getSession();
		ColSummary colSummary = null;
		try{
			String hql1 = "from ColBody where content like :content and body_type = :form";
			String hql2 = "from ColSummary where id = :id";
			
			String name = "";
			Query query = session.createQuery(hql1)
			                                                 .setString("content", formid.toString())
			                                                 .setString("form", "FORM");
			List<ColBody> colBodys = query.list();
	 
	        for(ColBody colBody:colBodys){
	        	colSummary = (ColSummary)session.createQuery(hql2).setLong("id", colBody.getSummaryId()).uniqueResult();
	        	if(colSummary==null) continue; 
	        	Long caseId = colSummary.getCaseId();		
				if(caseId==null) continue; 
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
				if(!name.equals(fname)){
					colSummary = null;
					continue;
				}
				else{
					break;
				}
	        }
		}
		catch (Exception e) {
		    throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return colSummary;
	}
	
	/**
	 * 根据id在表单动态表中查询一条调配信息
	 * 
	 */
	public Object[] getFormItemById(String fname,Long id)throws Exception{
		Session session = super.getSession();
		Object[] obj = null;
		try{
			String sql = "select * from "+fname+" where id = :id";
			obj = (Object[])session.createSQLQuery(sql).setLong("id", id).uniqueResult();
		}
		catch (Exception e) {
		    throw e;
		}
		finally {
		    super.releaseSession(session);
		}

		return obj;
	}
	
	/**
	 * 根据id在表单动态表中删除一条调配信息
	 * 
	 */
	public void deleteFormItemById(String fname,Long id)throws Exception{
		String sql = "update "+fname+" set field0019 = '1' where id = ?";
		//super.getSession().createSQLQuery(sql).setLong("id", id).executeUpdate();
		Connection conn = null;
		PreparedStatement ps = null;
		Session session = super.getSession();
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
	 * 更改处理后的调配信息的处理状态
	 * 
	 */
	public void updateFormItemState(String fname,Long id)throws Exception{
		String sql = "update "+fname+" set field0019='1' where id = ?";
//		super.getSession().createSQLQuery(sql).setLong("id", id).executeUpdate();
		Connection conn = null;
		PreparedStatement ps = null;
		Session session = super.getSession();
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
}
