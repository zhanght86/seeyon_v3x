package com.seeyon.v3x.guestbook.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;

public class GuestbookManagerImpl extends BaseHibernateDao<LeaveWord> implements GuestbookManager
{

    @SuppressWarnings("unchecked")
    public List<LeaveWord> getAllLeaveWords(long departmentId) throws BusinessException 
    {
        DetachedCriteria dc = DetachedCriteria.forClass(LeaveWord.class);
        dc.add(Restrictions.eq("departmentId", departmentId));
        dc.addOrder(Order.desc("createTime"));
        List<LeaveWord> leaveWordList = (List<LeaveWord>) super.executeCriteria(dc);
        return leaveWordList;
    }

    @SuppressWarnings("unchecked")
    public List<LeaveWord> getLeaveWords4Space(long departmentId, int resultCount) throws BusinessException 
    {
        DetachedCriteria dc = DetachedCriteria.forClass(LeaveWord.class);
        dc.add(Restrictions.eq("departmentId", departmentId));
        dc.addOrder(Order.desc("createTime"));
        List<LeaveWord> leaveWordList = (List<LeaveWord>) super.executeCriteria(dc, 0, resultCount);
        return leaveWordList;
    }
    
    public List<LeaveWord> getLeaveWords4Project(long departmentId, int resultCount, Long phaseId) throws BusinessException {
    	StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" from " + LeaveWord.class.getName() + " as l ");
		hql.append("where l.departmentId=:departmentId ");
		params.put("departmentId", departmentId);
		if(phaseId != null && phaseId != 1){
			hql.append("and l.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.bbs.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by l.createTime desc");
		return this.find(hql.toString(), 0, resultCount, params);
    }

    public LeaveWord saveLeaveWord(long memberId, long departmentId, String content) throws BusinessException 
    {
       return this.saveLeaveWordNew(memberId, departmentId, content, null, null);
    }
    public LeaveWord saveLeaveWordNew(long memberId, long departmentId, String content,Long replyId,Long replyerId) throws BusinessException 
    {
       Timestamp now = new Timestamp(System.currentTimeMillis());
       LeaveWord guestbook = new LeaveWord();
       guestbook.setIdIfNew();
       guestbook.setDepartmentId(departmentId);
       guestbook.setCreatorId(memberId);
       guestbook.setContent(content);
       guestbook.setCreateTime(now);
       guestbook.setReplyId(replyId);
       guestbook.setReplyerId(replyerId);
       super.save(guestbook);
       return guestbook;
    }
    
    public void clearLeaveWord(long leaveWordId) throws BusinessException 
    {
       super.delete(leaveWordId);
    }
    public void clearSubLeaveWords(long leaveWordId) throws BusinessException 
    {
    	super.delete(leaveWordId);
    	super.delete(LeaveWord.class, new Object[][]{{"replyId", leaveWordId}});
    }
    public void clearBanchSubLeaveWords(String idStr) throws BusinessException 
    {
//    	Session session = super.getSession();
//    	String[] IdArray = idStr.split(",");
//    	List array = new ArrayList();
//    	for( int i=0;i<IdArray.length;i++){
//			  String Idr = IdArray[i];
//			  if(Idr!=null && "".equals(Idr)){
//				  array.add(Long.valueOf(Idr));
//			  }
////			  Long Id = Long.valueOf(Idr);
////			  this.clearSubLeaveWords(Id);
//    	}
//    	session.beginTransaction();
    	String   hql= "delete from LeaveWord where id in (:ids) or replyId in (:ids)"; 
    	this.bulkUpdate(hql, FormBizConfigUtils.newHashMap("ids", FormBizConfigUtils.parseStr2Ids(idStr)));
//    	Query query = session.createQuery(hql);
//    	query.setParameterList("ids", array);
//    	query.executeUpdate();
//    	session.getTransaction().commit();
//    	super.closeSessionIfNecessary(session);
    }
    public boolean clearLeaveWords(final String leaveWordIds)
    {
        try{
        	String[] IdArray = leaveWordIds.split(",");
        	
        	for( int i=0;i<IdArray.length;i++){
				  String Idr = IdArray[i];
				  Long Id = Long.valueOf(Idr);
				  super.delete(Id);
        	}
           // final String hql="delete LeaveWord where id in (:ids)";
//          super.bulkUpdate(hql, null,null);
            //by Yongzhang 2008-05-06
           // getHibernateTemplate().execute(new HibernateCallback() {
            //    public Object doInHibernate(Session s) throws HibernateException, SQLException {
            //      Query query = s.createQuery(hql);
            //      query.setString("ids", leaveWordIds);
            //      query.executeUpdate();
            //      return null;
               // }
            //  });
        }catch(Exception e)
        {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public int getLeaveWordsCount(long departmentId) throws BusinessException 
    {
        int count = 0;
        DetachedCriteria dc = DetachedCriteria.forClass(LeaveWord.class);
        dc.add(Restrictions.eq("departmentId", departmentId));
        count = (Integer)super.getCountByCriteria(dc);;
        
        return count;
    }
	public int getSubLeaveWordsCount(long departmentId)throws BusinessException {
		return this.getSubLeaveWordsCount(departmentId, null);
	}
	public int getSubLeaveWordsCount(long departmentId, Long phaseId)throws BusinessException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" from " + LeaveWord.class.getName() + " as l ");
		hql.append("where l.departmentId=:departmentId and l.replyId is null ");
		params.put("departmentId", departmentId);
		if(phaseId != null && phaseId != 1){
			hql.append("and l.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.bbs.key() + ") ");
			params.put("phaseId", phaseId);
		}
		return this.find(hql.toString(), -1, -1, params).size();
	}
    /**
     * 根据id取得具体留言
     * @return
     * by Yongzhang 2008-6-12
     */
    public LeaveWord getLeaveWordsById(long leaveWordId) throws BusinessException
    {
        return   (LeaveWord) super.get(leaveWordId);
    }

	public List<LeaveWord> getPageSizeLeaveWord(long departmentId,int beginRow, int pageSize) throws Exception {
		return this.getPageSizeLeaveWord(departmentId, null, beginRow, pageSize);
	}
	
	public List<LeaveWord> getPageSizeLeaveWord(long departmentId, Long phaseId, int beginRow, int pageSize) throws Exception {
	    StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" from " + LeaveWord.class.getName() + " as l ");
		hql.append("where l.departmentId=:departmentId and l.replyId is null ");
		params.put("departmentId", departmentId);
		if(phaseId != null && phaseId != 1){
			hql.append("and l.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.bbs.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by l.createTime desc");
		Pagination.setFirstResult(beginRow);
	    Pagination.setMaxResults(pageSize);
		return this.find(hql.toString(), params);
	}

	public List<LeaveWord> getReplyLeaveWord(long replyId) throws Exception {
		Session session = super.getSession();
		List<LeaveWord> leaveWordList = new ArrayList<LeaveWord>();
		try {
		    session.beginTransaction();
		    Query query = session.createQuery("from LeaveWord as reply where reply.replyId = ? order by reply.createTime asc ");
		    query.setLong(0,replyId);
		    leaveWordList = query.list();
		    session.getTransaction().commit();
		  }catch(Exception e) {
		    e.printStackTrace();
		    session.getTransaction().rollback();
		  }finally {
		    super.closeSessionIfNecessary(session);
		  }
		return leaveWordList;
	}

}
