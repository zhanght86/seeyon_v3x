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
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Page;


public class PageDao extends BaseHibernateDao<Page>{
	public List<Page> findAllPage(){
		return this.getAll();
	}
	
	public void save(Page page){
		getHibernateTemplate().save(page);
	}
	
	public void updatePage(Page page){
		getHibernateTemplate().update(page);
	}
	/**
	 * 根据ids删除多个页签
	 * @param ids
	 */
	public void delPage(final List<Long> ids){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete Page");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("ids", ids);
				return query.executeUpdate();
			}
		});
	}
	
	public Page findPageById(Long page_id){
		return this.get(page_id);
	}
	
	public List<Page> findPageByModelName(final String modelName) {
		return this.findPageByModelName(modelName, false, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Page> findPageByModelName(final String modelName, final boolean containRemove, final boolean containDisplay) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("from " + Page.class.getName() + " where accountId=:accountId and modelName=:modelName ");
		params.put("accountId", CurrentUser.get().getLoginAccount());
		params.put("modelName", modelName);
		if (!containRemove) {
			hql.append(" and remove=:remove ");
			params.put("remove", PagePropertyConstant.Page_Remove_No);
		}
		if (!containDisplay) {
			hql.append(" and pageDisplay=:pageDisplay ");
			params.put("pageDisplay", PagePropertyConstant.Page_Display_No);
		}
		hql.append(" order by sort");
		return this.find(hql.toString(), -1, -1, params);
	}
	
	/**
	 * Hr管理页签列表查询
	 * @param remove
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Page> findPageByRemove(final int remove){
		return this.find("From Page where accountId=? and remove=? order by sort", -1, -1, null, CurrentUser.get().getLoginAccount(), remove);
	}
	
	@SuppressWarnings("unchecked")
	public List<Page> findPageByAccount(Long accountId) {
		String hql = " from " + Page.class.getName() + " where accountId=?";
		return this.find(hql, -1, -1, null, accountId);
	}
	
	public Page getById(Long id) {
		return this.get(id);
	}
}
