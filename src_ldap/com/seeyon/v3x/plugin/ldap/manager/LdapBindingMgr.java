package com.seeyon.v3x.plugin.ldap.manager;

import java.util.List;

import com.seeyon.v3x.common.ldap.domain.EntryValueBean;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgProperty;
import com.seeyon.v3x.plugin.ldap.domain.*;

/**
 * LDAP/AD业务实现类
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-5
 */
public interface LdapBindingMgr
{
    /**
     * 批量绑定A8用户账号
     * @param orgManagerDirect 
     * @param list ldif或ldf文件内容
     * @return void
     */
    public void batchBinding(OrgManagerDirect orgManagerDirect,List<String> list,List<V3xOrgMember> memberList,int option) throws Exception;
    /**
     * 手工单用户账号绑定
     * @param memberId 人员ID
     * @param loginName 用户账号
     * @param binding 绑定的LDAP/AD相对条目
     * @param enabled 用户账号是否可用
     * @return String 结果
     */
    public String[] handBinding(long memberId,String loginName,String binding,boolean enabled) throws Exception;
    /**
     * 修改用户密码
     * @param dn 条目
     * @param oldPassWord 旧密码
     * @param newPassword 新密码
     * @return void
     */
    public void  modifyUserPassWord(String dn,String oldPassWord,String newPassword) throws Exception;
    /**
     * 绑定前清空
     * @param orgManagerDirect
     * @param memberList
     *        登录管理员管理的所有用户List
     * @return void
     */
    public void  deleteAllBinding(OrgManagerDirect orgManagerDirect,List<V3xOrgMember> memberList) throws Exception;
    
     
    public void userTreeView(List<EntryValueBean> list) throws Exception;
    
    public List<EntryValueBean>  ouTreeView(boolean isRoot) throws Exception;
    /**
     * 从LDAP上查询
     * @param dn 
     * @return String[] 从LDAP上查询出用户帐号，姓名，密码，手机号码等信息
     */
    public String[] getUserAttributes(String dn) throws Exception;
    
    public void saveOrUpdateLdapSet(V3xLdapRdn value) throws Exception;
    
    public V3xLdapRdn findLdapSet(Long orgAccountId) throws Exception;
    
    public boolean createNode(V3xOrgMember member,String selectOU) throws Exception;
    
    public V3xLdapSwitchBean viewLdapSwitch() throws Exception;
    public V3xLdapSwitchBean saveLdapSwitch(V3xLdapSwitchBean ldapSwitchBean) throws Exception;
    public void deleteLdapSet(Long orgAccountId) throws Exception;
    public V3xOrgProperty getDefaultOU(long accoutId);
}
