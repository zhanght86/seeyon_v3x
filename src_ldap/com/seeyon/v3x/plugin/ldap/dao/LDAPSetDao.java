package com.seeyon.v3x.plugin.ldap.dao;

import com.seeyon.v3x.plugin.ldap.domain.V3xLdapRdn;

public interface LDAPSetDao
{
    public void save(V3xLdapRdn transientInstance);
    public V3xLdapRdn findByAccountIdAndType(Long orgAccountId,String ldapType);
    public void delete(V3xLdapRdn valueBean);
}
