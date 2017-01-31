package com.seeyon.v3x.plugin.ldap.dao;

import java.util.*;

import org.hibernate.criterion.DetachedCriteria;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapRdn;
import org.hibernate.criterion.*;
public class LDAPSetDaoImp extends BaseHibernateDao implements LDAPSetDao
{

    public V3xLdapRdn findByAccountIdAndType(Long orgAccountId, String ldapType)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(V3xLdapRdn.class);
        
        criteria.add(Restrictions.eq("orgAccountId", orgAccountId));
        criteria.add(Restrictions.eq("ldapType", ldapType));
        List list=super.executeCriteria(criteria);
        if(list==null || list.isEmpty())
        {
            return null;
        }
        else
        {
           return (V3xLdapRdn)list.get(0);
        }
    }

    public void save(V3xLdapRdn transientInstance)
    {
           super.save(transientInstance);        
    }

    public void delete(V3xLdapRdn valueBean)
    {
        this.deleteObject(valueBean);
    }

}
