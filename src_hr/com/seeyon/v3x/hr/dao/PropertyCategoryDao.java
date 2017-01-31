package com.seeyon.v3x.hr.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.PropertyCategory;

public class PropertyCategoryDao extends BaseHibernateDao<PropertyCategory> {
	
	public void save(PropertyCategory category){
		getHibernateTemplate().save(category);
	}
	
	public void update(PropertyCategory category){
		getHibernateTemplate().update(category);
	}
	/**
	 * 查询所有的信息项类别
	 * @return
	 */
	public List findAllCategory(){
		return (List)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long accountId = CurrentUser.get().getLoginAccount();
				String hql = "From PropertyCategory where accountId = :accountId";
				Query query = session.createQuery(hql).setLong("accountId", accountId);
				return query.list();
			}
		});
//修改前代码
//		Long accountId = CurrentUser.get().getLoginAccount();
//		return getHibernateTemplate().find("from PropertyCategory where accountId = "+ accountId);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyCategory> findCategoryByAccount(Long accountId) {
		String hql = " from " + PropertyCategory.class.getName() + " where accountId=?";
		return this.find(hql, -1, -1, null, accountId);
	}
	
	/**
	 * 删除多个信息项类别
	 * @param ids
	 */
	public void delCategory(final List<Long> ids){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PropertyCategory");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("ids", ids);
				return query.executeUpdate();
			}
		});
	}
	/**
	 * 根据信息项类别id查询一条数据
	 * @param id
	 * @return
	 */	
	public PropertyCategory findCategoryById(Long id){
		return this.get(id);
	}
	/**
	 * 根据信息项类别id删除一条数据
	 * @param category_id
	 */
	public void delCategoryById(final Long category_id){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PropertyCategory");
				sHql.append(" where id = :category_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("category_id", category_id);
				return query.executeUpdate();
			}
		});
	}	
	
	@SuppressWarnings("unchecked")
	public List<PropertyCategory> findCategorysByRemove(final int remove) {
		String hql = " from " + PropertyCategory.class.getName() + " where accountId=? and remove=?";
		return this.find(hql, -1, -1, null, CurrentUser.get().getLoginAccount(), remove);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyCategory> findCategorysByRemove(final int remove, final boolean sysFlag) {
		String hql = " from " + PropertyCategory.class.getName() + " where sysFlag=? and accountId=? and remove=?";
		return this.find(hql, -1, -1, null, sysFlag, CurrentUser.get().getLoginAccount(), remove);
	}
	
}
