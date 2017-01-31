package com.seeyon.v3x.hr.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.PageProperties;

public class PagePropertiesDao extends BaseHibernateDao<PageProperties> {
	public void save(PageProperties properties){
		getHibernateTemplate().save(properties);
	}
	
	public void update(PageProperties properties){
		super.update(properties);
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperties> findPagePropertiesByPageId(final Long page_id){
		return this.find("From PageProperties p where p.page.id=? order by p.property_ordering", page_id);
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperties> findPagePropertiesByPropertyId(final Long property_id){
		return this.find("From PageProperties p where p.pageProperty.id=?", property_id);
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperties> findPagePropertiesByPages(List<Long> pageIds) {
		String hql = " from " + PageProperties.class.getName() + " p where p.page.id in (:pageIds)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageIds", pageIds);
		return this.find(hql, -1, -1, params);
	}
	
	public void delPageProperties(final Long page_id){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PageProperties");
				sHql.append(" where page_id = :page_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("page_id", page_id);
				return query.executeUpdate();
			}
		});
	}
	
	public void delPagePropertiesByPropertyId(final Long property_id){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PageProperties");
				sHql.append(" where property_id = :property_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("property_id", property_id);
				return query.executeUpdate();
			}
		});
	}
	
}
