package com.seeyon.v3x.hr.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;

public class PagePropertyDao extends BaseHibernateDao<PageProperty>{
	public void save(PageProperty pageProperty){
		getHibernateTemplate().save(pageProperty);
	}
	
	public void update(PageProperty pageProperty){
		getHibernateTemplate().update(pageProperty);
	}
	
	public PageProperty findPropertyById(Long property_id){
		return this.get(property_id);
	}
	
	public List<PageProperty> findAllProperty(){
		return this.getAll();
	}

	/**
	 * 根据ids删除多个信息项
	 */
	public void delProperty(final List<Long> ids){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PageProperty");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("ids", ids);
				return query.executeUpdate();
			}
		});		
	}
	
	/**
	 *根据页签ID去查询关联的信息项
	 * @param page_id
	 * @return
	 */
	public List findPropertyByPageId(final Long page_id){
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				String hql = "From PageProperty where page_id = :page_id";
				Query query = session.createQuery(hql).setLong("page_id", page_id);
				return query.list();
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperty> findPropertyByAccount(Long accountId) {
		String hql = " from " + PageProperty.class.getName() + " where accountId=?";
		return this.find(hql, -1, -1, null, accountId);
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperty> findPropertyByRemove(final int remove) {
		String hql = " from " + PageProperty.class.getName() + " where accountId=? and remove=?";
		return this.find(hql, -1, -1, null, CurrentUser.get().getLoginAccount(), remove);
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperty> findPropertyByRemove(final int remove, final boolean sysFlag) {
		String hql = " from " + PageProperty.class.getName() + " where sysFlag=? and accountId=? and remove=?";
		return this.find(hql, -1, -1, null, sysFlag, CurrentUser.get().getLoginAccount(), remove);
	}
	
	/**
	 * 查询所有的未删除的信息项
	 * @param remove
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List findPropertyByRemove(final int remove, int pageNo, int pageSize){
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				String hql = "From PageProperty where remove = :remove";
				Query query = session.createQuery(hql).setInteger("remove", remove);
				return query.list();
			}
		});
	}
	/**
	 * 根据信息项类别ID来查询信息项
	 * @param category_id
	 * @return
	 */
	public List findPropertyByCategoryId(final Long category_id){
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				String hql = "From PageProperty where category_id = :category_id";
				Query query = session.createQuery(hql).setLong("category_id", category_id);
				return query.list();
			}
		});
		
	}
	/**
	 * 根据信息项类别ID和未删除标志来查询信息项
	 * @param category_id
	 * @param remove
	 * @return
	 */
	public List findPropertyByCategoryId(final Long category_id, final int remove){
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				String hql = "From PageProperty where category_id = :category_id and remove = :remove";
				Query query = session.createQuery(hql).setLong("category_id", category_id).setInteger("remove", remove);
				return query.list();
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PageProperty> findUnUsePropertyByCategoryId(final Long category_id, final int remove) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from " + PageProperty.class.getName()
				+ " p where p.category_id = :category_id and p.remove = :remove "
				+ "and p.id not in (select ps.pageProperty.id from " + PageProperties.class.getName() + " ps)";
		params.put("category_id", category_id);
		params.put("remove", remove);
		return super.find(hql, -1, -1, params);
	}
	
}
