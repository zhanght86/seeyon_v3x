package com.seeyon.v3x.edoc.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.hibernate.LockMode;
import org.hibernate.Query;
//import org.hibernate.criterion.Example;

import com.seeyon.v3x.edoc.domain.EdocMarkAcl;

import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * Data access object (DAO) for domain model class EdocMarkAcl.
 * @see .EdocMarkAcl
 * @author MyEclipse - Hibernate Tools
 */
public class EdocMarkAclDAO extends BaseHibernateDao<EdocMarkAcl>{

    private static final Log log = LogFactory.getLog(EdocMarkAclDAO.class);

	//property constants
	public static final String DEPT_ID = "deptId";

    /**
     * 方法描述：保存公文文号使用授权
     */
    public void saveEdocMarkAcl(EdocMarkAcl edocMarkAcl) {
        log.debug("saving V3xEdocMarkAcl instance");
        try {
            super.save(edocMarkAcl);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
    /**
     * 方法描述：根据属性查询公文文号使用授权
     */
    @SuppressWarnings("unchecked")
	public List<EdocMarkAcl> findEdocMarkAclByProperty(String propertyName, Object value) {
        log.debug("finding EdocMarkAcl instance with property: " + propertyName
              + ", value: " + value);
        try {
           String queryString = "from EdocMarkAcl as model where model." 
           						+ propertyName + "= ? order by model.deptId desc";           
  		   return super.find(queryString, value);
        } catch (RuntimeException re) {
           log.error("find by property name failed", re);	
           throw re;
        }
  	}

    public void deleteEdocMarkAclByDefinitionId(Long definitionId){
    	try{
    		String hsql="delete from EdocMarkAcl as acl where acl.edocMarkDefinition.id = ?";    		
    		super.bulkUpdate(hsql,null,definitionId);
    	}catch(RuntimeException re){
    		throw re;
    	}
    }
    
}