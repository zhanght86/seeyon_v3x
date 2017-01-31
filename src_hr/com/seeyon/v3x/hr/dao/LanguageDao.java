package com.seeyon.v3x.hr.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.Language;

public class LanguageDao extends BaseHibernateDao<Language> {
	
	/*
	 *查出语言的种类 
	 */
	public List<Language> findAllLanguage(){
		return this.getAll();
	}
	
}
