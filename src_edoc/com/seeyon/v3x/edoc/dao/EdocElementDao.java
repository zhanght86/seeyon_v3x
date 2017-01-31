/**
 * EdocElementDao.java
 * Created on 2007-4-19
 */
package com.seeyon.v3x.edoc.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocElement;
import java.util.List;

/**
 *
 * @author <a href="mailto:handy@seeyon.com">Han Dongyou</a>
 *
 */
public class EdocElementDao extends BaseHibernateDao<EdocElement>
{     
    
    public List<EdocElement> getAllEdocElements()
    {
        String hsql = "from EdocElement as a order by a.isSystem desc, a.type, a.elementId ";
        //return super.find(hsql);
        List ls=null;
        try{
        ls=super.find(hsql);
        }catch(Exception e)
        {
			logger.error(e.getMessage(), e);
        }
        return ls;
    }
    
    public EdocElement getEdocElementsById(long id){
    	return super.get(id);
    }
    
    public void deleteEdocElementsByDomainId(Long domainId){
    	 String hsql = "delete from EdocElement where domainId = ? ";
    	 super.bulkUpdate(hsql, null, new Object[]{domainId});
    }
}