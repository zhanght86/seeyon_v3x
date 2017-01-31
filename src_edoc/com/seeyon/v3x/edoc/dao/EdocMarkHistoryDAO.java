package com.seeyon.v3x.edoc.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.hibernate.LockMode;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
//import org.hibernate.criterion.Example;

import com.seeyon.v3x.edoc.domain.EdocMarkHistory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
/**
 * Data access object (DAO) for domain model class EdocMarkHistory.
 * @see .EdocMarkHistory
 * @author MyEclipse - Hibernate Tools
 */
public class EdocMarkHistoryDAO extends BaseHibernateDao<EdocMarkHistory> {

    private static final Log log = LogFactory.getLog(EdocMarkHistoryDAO.class);	

    /**
     * 方法描述：保存公文文号历史
     */
    public void save(EdocMarkHistory edocMarkHistory) {
        log.debug("saving EdocMarkHistory instance");
        super.save(edocMarkHistory);
//        try {
//            getSession().save(edocMarkHistory);
//            log.debug("save successful");
//        } catch (RuntimeException re) {
//            log.error("save failed", re);
//            throw re;
//        }
    }
    
    /**
     * 查询相同文号数
     * @param edocMark
     * @param edocId
     * @return
     */
     public int getCount(String edocMark,Long edocId, Long accountId) {
    	 StringBuffer hql=null;
     	List<Object> values = new ArrayList<Object>();
     	List<Type> typeList = new ArrayList<Type>();
     	if(accountId!=null){
     		hql = new StringBuffer("from EdocMarkHistory a,EdocSummary b where a.edocId=b.id and b.orgAccountId=? and a.docMark=?");
     		values.add(accountId);
         	typeList.add(Hibernate.LONG);
     	}else{
     		hql = new StringBuffer("from EdocMarkHistory where docMark=?");
     		
     	}
     	values.add(edocMark);
     	typeList.add(Hibernate.STRING);
     	if(edocId != null) {
     		if(accountId!=null){
     			hql.append(" and a.edocId<>?");
     		}else{
     			hql.append(" and edocId<>?");
     		}
     		values.add(edocId);
     		typeList.add(Hibernate.LONG);
     	}
     	Type[] types = new Type[typeList.size()];
     	int i = 0;
     	for(Type type: typeList) {
     		types[i] = type;
     		i++;
     	}
     	return super.getQueryCount(hql.toString(), values.toArray(), types);
    }
}