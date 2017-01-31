package com.seeyon.v3x.hr.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.domain.Language;
import com.seeyon.v3x.hr.domain.PropertyLabel;

public class PropertyLabelDao extends BaseHibernateDao<PropertyLabel>{
	private LanguageDao languageDao;
	private List<Language> languageList;

	public List<Language> getLanguageList() {
		//TODO 逻辑存在问题?
		this.languageDao.findAllLanguage();
		return languageList;
	}

	public void setLanguageList(List<Language> languageList) {
		this.languageList = languageList;
	}

	public LanguageDao getLanguageDao() {
		return languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}
	
	public void save(PropertyLabel propertyLabel){
		getHibernateTemplate().save(propertyLabel);
	}
	
	public void update(PropertyLabel propertyLabel){
		super.update(propertyLabel);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyLabel> findPropertyLabelByPropertyId(final Long property_id){
		return this.find("From PropertyLabel where property_id=?", property_id);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyLabel> findPropertyLabelByPropertyIds(List<Long> property_ids){
		return this.find("From PropertyLabel where property_id in (:property_ids)", -1, -1, FormBizConfigUtils.newHashMap("property_ids", property_ids));
	}
	
	public PropertyLabel findPropertyLabelByName(String propertyLabelValue){
		return findUniqueBy("propertyLabelValue",propertyLabelValue);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyLabel> findPropertyLabelByPropertyIdAndLanguage(final Long property_id, final int language){
		return this.find("From PropertyLabel where property_id=? and language=?", property_id, language);
	}
	
	public void delPropertyLabel(final Long property_id){
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PropertyLabel");
				sHql.append(" where property_id = :property_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("property_id", property_id);
				return query.executeUpdate();
			}
		});
	}
	
	public List<PropertyLabel> findAllPropertyLabel(){
		return this.getAll();
	}

}
