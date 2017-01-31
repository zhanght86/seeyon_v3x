package com.seeyon.v3x.edoc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocFormElement;

public class EdocFormElementDao extends BaseHibernateDao<EdocFormElement> {
	  public List<EdocFormElement> getAllEdocFormElements()
	    {
	        String hsql = "from EdocFormElement as a";
	        return super.find(hsql);        
	    }
	  public List<EdocFormElement> getEdocFormElementByFormId(long formId){
		  	String hsql = "from EdocFormElement as a where a.formId = ? order by a.elementId";
		  	return super.find(hsql, formId);
	  }
}
