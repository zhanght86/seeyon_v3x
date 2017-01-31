package com.seeyon.v3x.indexInterface.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
/**
 * 全文检索数据持久化
 * @author zhangyong
 *
 */
public class UpdateIndexDAO extends BaseHibernateDao<V3xUpdateIndex>  {
	private final static Log log = LogFactory.getLog(V3xUpdateIndex.class);
	/*
	 * 取得所有的记录
	 */
	public List<V3xUpdateIndex> records(){
		return getHibernateTemplate().find("from V3xUpdateIndex");
	}
	/*
	 * 删除某一条特定的记录
	 */
	public void delete(final Long entityId){
		HibernateTemplate ht=getHibernateTemplate();
		ht.execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
				Criteria criteria=session.createCriteria(V3xUpdateIndex.class);
				criteria.add(Restrictions.eq("entityId", entityId));
				criteria.addOrder(Order.asc("createDate"));
				criteria.setFirstResult(0).setMaxResults(1);
				V3xUpdateIndex result=(V3xUpdateIndex)criteria.uniqueResult();
				session.delete(result);
				return null;
			  }
		     }
		 );
	}
	/*
	 * 保存一个记录进入数据库
	 */
	public void save(Long entityId,Integer type){
		V3xUpdateIndex index=this.getV3xIndexByEntityId(entityId);
		if(index==null)
		{
			V3xUpdateIndex updateIndex=new V3xUpdateIndex();
			updateIndex.setEntityId(entityId);
			updateIndex.setIdIfNew();
			updateIndex.setType(type);
			updateIndex.setCreateDate(new Date());
			save(updateIndex);
		}
		
	}
	/**
	 * 
	 * @param entityId
	 * @return
	 */
	private V3xUpdateIndex getV3xIndexByEntityId(long entityId)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass(this.entityClass);
		criteria.add(Restrictions.eq("entityId", entityId));
	    List l = super.executeCriteria(criteria,-1,-1);
		if(l==null || l.isEmpty()){return null;}
		return (V3xUpdateIndex)l.get(0);
	}
	/**
	 * 批量删除
	 * @param entityList
	 */
   public void deleteIndex(List<Long> entityList){
	   if(entityList==null||entityList.isEmpty())
		   return;
	   String sql = "delete from "+V3xUpdateIndex.class.getName()+" where entityId in (:ids) ";
	   Map<String, Object> nameParameters = new HashMap<String, Object>();
	   nameParameters.put("ids", entityList);
	   super.bulkUpdate(sql, nameParameters);	
   }
}
