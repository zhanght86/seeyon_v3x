package com.seeyon.v3x.mobile.menu.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.mobile.menu.domain.MobileMenuSetting;

public class MobileMenuDao extends BaseHibernateDao<MobileMenuSetting> {
	
	/**
	 * 从内存中载入用户的菜单配置
	 * @param userId
	 * @return
	 */
	public List<MobileMenuSetting> loadMenuSetting(Long userId){
		DetachedCriteria criteria = DetachedCriteria.forClass(MobileMenuSetting.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.addOrder(Order.asc("sort"));
		return super.executeCriteria(criteria,-1,-1);
	}
	
	
	public void removeSeeting(Long userId){
		Object[][] where = new Object[][]{{"userId",userId}};
		super.delete(where);
	}
}
