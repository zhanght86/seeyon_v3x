package com.seeyon.v3x.plugin.ldap.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.ldap.config.LDAPProperties;
import com.seeyon.v3x.common.ldap.dao.AbstractLdapDao;
import com.seeyon.v3x.common.ldap.dao.AdDaoImp;
import com.seeyon.v3x.common.ldap.dao.LdapDao;
import com.seeyon.v3x.common.ldap.domain.EntryValueBean;
import com.seeyon.v3x.common.usermapper.dao.UserMapperDao;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.dao.OrgManageDao;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgProperty;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapRdn;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.v3x.util.TextEncoder;

/**
 * LDAP/AD业务实现类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-11-6
 */
public class LdapBindingMgrImp implements LdapBindingMgr
{
    private static final Log log = LogFactory.getLog(LdapBindingMgrImp.class);

    private static final String PARSEFLAGA1 = "a8";

    private static final String PARSEFLAGA2 = "A8";

    private static final String NOTE = "#";

    private static final String DNFLAG = "dn";

    private static final String LDAPFLAG = "uid";

    private static final String ADFLAG1 = "cn";

    private static final String ADFLAG2 = "CN";
    /**
     * LDAP/AD键值对区分标识
     */
    private static final String ATTRIBUTEFLAG = ":";

    private static final String DCFLAG1 = "DC=";

    private static final String DCFLAG2 = "dc=";
    /**
     * LDAP/AD配置文件存放目录 base/ldap
     */
    private static final String LDAP_SWITCH_FOLDER = "/conf";
    
    /**
     * LDAP/AD配置信息文件
     */
    private static final String LDAP_SWITCH_FILE = "/ldap.properties";

    private UserMapperDao userMapperDao;

    private static LdapDao ldapDao = null;
    private OrgManagerDirect om=null;
//    private LDAPSetDao ldapSetDao;
    private OrgManageDao orgManageDao;
	public OrgManagerDirect getOm() {
		return om;
	}
	public void setOm(OrgManagerDirect val) {
		this.om = val;
	}
    public LdapBindingMgrImp()
    {
    }

    private synchronized void init()
    {
            if (LDAPConfig.getInstance().getType().indexOf(LDAPConfig.LDAPFlag) != -1)
            {
            	if(LDAPConfig.getInstance().getType().indexOf(LdapServerMap.getOPENLDAP()) != -1)
            	{
            		AbstractLdapDao ldapDao1 = (AbstractLdapDao) ApplicationContextHolder
                    .getBean("openldapDao");
                ldapDao1.setLog(log);
                ldapDao = (LdapDao) ldapDao1;
            	}
            	else 
            	{
            		AbstractLdapDao ldapDao1 = (AbstractLdapDao) ApplicationContextHolder
                    .getBean("ldapDao");
                ldapDao1.setLog(log);
                ldapDao = (LdapDao) ldapDao1;
            	}
            }
            else
            {
                AbstractLdapDao ldapDao1 = (AbstractLdapDao) ApplicationContextHolder
                        .getBean("adDao");
                ldapDao1.setLog(log);
                ldapDao = (LdapDao) ldapDao1;
            }
    }

    public void deleteAllBinding(OrgManagerDirect orgManagerDirect, List<V3xOrgMember> memberList)
            throws Exception
    {
        if (memberList == null)
        {
            return;
        }
        for (V3xOrgMember member : memberList)
        {
            List<V3xOrgUserMapper> list = userMapperDao.getExLoginNames(member.getLoginName(),
                    LDAPConfig.getInstance().getType());

            for (V3xOrgUserMapper mapper : list)
            {
                userMapperDao.deleteUserMapper(mapper);
                log.info("删除人员绑定: "+mapper.getLoginName());
            }
        }
    }

    /**
     * 批量绑定A8用户账号
     * 
     * @param orgManagerDirect
     * @param list
     *            ldif或ldf文件内容
     * @param memberList
     *            登录管理员所在单位所有用户
     * @return void
     */
    public void batchBinding(OrgManagerDirect orgManagerDirect, List<String> list,
            List<V3xOrgMember> memberList, int option) throws Exception
    {
        try
        {
            Map<String, String> map = null;
            init();
            if (ldapDao instanceof AdDaoImp)
            {
                map = parseADLDIF(list);
            }
            else
            {
                map = parseLDIF(list);
            }

            if (map == null || map.size() < 0)
            {
                return;
            }
            Set<Map.Entry<String, String>> entry = map.entrySet();
            List<V3xOrgUserMapper> usersMapperList = new ArrayList<V3xOrgUserMapper>();
            String[] currentArray = new String[memberList.size()];
            int currentIndex = 0;
            for (Entry<String, String> element : entry)
            {
                String a8 = element.getValue().trim();
                if (a8.equals("-1") || StringUtils.isBlank(a8))
                {
                    continue;
                }
                String[] temArray = StringUtils.split(element.getKey().trim(), ATTRIBUTEFLAG);
                if (StringUtils.isNotBlank(temArray[1]))
                {
                    String uidArray = temArray[1].trim();
                    // init();
                    log.info("uid||cn: " + uidArray);
                    if (ldapDao.isUserExist(uidArray))
                    {
                        String exloginName = ldapDao.getLoginName(uidArray);

                        if (StringUtils.isNotBlank(exloginName))
                        {
                            String[] longNameArray = StringUtils.split(a8, ATTRIBUTEFLAG);
                            String loginName = "";
                            if (longNameArray != null && longNameArray.length == 2)
                            {
                                loginName = longNameArray[1].trim();
                            }
                            V3xOrgMember member = orgManagerDirect.getMemberByLoginName(loginName);
                            // 在A8中账号是启用状态并且是单位管理员所在单位用户才可以做绑定
                            if (member != null && member.getEnabled()
                                    && memberList.contains(member))
                            {
                                if (checkisExitExloginNameInDB(exloginName, userMapperDao))
                                {
                                    continue;
                                }
                                if (checkIsExitExloginName(exloginName, usersMapperList))
                                {
                                    continue;
                                }

                                List<V3xOrgUserMapper> temp = new ArrayList<V3xOrgUserMapper>();
                                log.info("ExloginName: " + exloginName + " | " + "  A8: "
                                        + loginName);
                                String ExUnitCode = createExUnitCode(uidArray);
                                V3xOrgUserMapper userMapper = new V3xOrgUserMapper();

                                currentArray[currentIndex] = loginName;
                                currentIndex++;
                                userMapper.setLoginName(loginName);
                                userMapper.setExLoginName(exloginName);
                                userMapper.setExUnitCode(ExUnitCode);
                                userMapper.setMemberId(member.getId());
                                userMapper.setType(LDAPConfig.getInstance().getType());
                                userMapper.setExPassword("null");
                                userMapper.setExId(member.getOrgAccountId().toString());
                                usersMapperList.add(userMapper);
                                userMapperDao.mapper(loginName, LDAPConfig.getInstance().getType(), temp);
                            }
                        }
                    }// LDAP/AD中如果不存在该账号并且A8中存在绑定关系则删除A8绑定关系
                    // else
                    // {
                    // uidArray;
                    // String[] longNameArray = StringUtils.split(a8,
                    // ATTRIBUTEFLAG);
                    // String loginName = "";
                    // if (longNameArray != null && longNameArray.length == 2)
                    // {
                    // loginName = longNameArray[1].trim();
                    // }
                    // List<V3xOrgUserMapper> userMapperlist =
                    // userMapperDao.getExLoginNames(
                    // loginName, LDAP_TYPE);
                    //
                    // for (V3xOrgUserMapper mapper : userMapperlist)
                    // {
                    // if (StringUtils.isNotBlank(mapper.getExUnitCode()))
                    // {
                    // if
                    // (!ldapDao.isUserExist(createDnString(mapper.getExUnitCode())))
                    // userMapperDao.deleteUserMapper(mapper);
                    // }
                    // }

                    // }
                }
            }
            userMapperDao.mapper("", LDAPConfig.getInstance().getType(), usersMapperList);
            if (option == BingdingEnum.coverAll.key())
            {
                this.coverBatchBinding(currentArray);
            }
        }
        catch (Exception e)
        {
            log.error("绑定人员账号发生错误：　", e);
            throw new Exception("绑定人员账号发生错误", e);
        }
    }

    /**
     * 手工单用户账号绑定
     * 
     * @param memberId
     *            人员ID
     * @param loginName
     *            用户账号
     * @param binding
     *            绑定的LDAP/AD相对条目
     * @param enabled
     *            用户账号是否可用
     * @return String 结果
     */
    public String[] handBinding(long memberId, String loginName, String binding, boolean enabled)
            throws Exception
    {
        if (!org.springframework.util.StringUtils.hasText(loginName))
        {
            throw new Exception(LdapBindingMgrImp.class.getName() + " 登录名称为null或空");
        }
        List<String> logList=new ArrayList<String>();
        
//        String[] bindingArrays = StringUtils.split(binding, "|");
        if (StringUtils.isBlank(binding) || !enabled)
        {
            userMapperDao.clearTypeLogin(LDAPConfig.getInstance().getType(), loginName,om);
            if (!enabled)
            {
            	logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.log.disable"));
            }
            logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME, "ldap.log.empty",loginName));
        }
        else
        {
            List<V3xOrgUserMapper> userMapList = userMapperDao
                    .getExLoginNames(loginName, LDAPConfig.getInstance().getType());

            if (userMapList != null && userMapList.size() > 0)
            {
                for (V3xOrgUserMapper mapper : userMapList)
                {
                    boolean flag = false;
//                    for (int i = 0; i < bindingArrays.length; i++)
//                    {
                        if (mapper.getExUnitCode().equals(binding))
                        {
                            flag = true;
                            continue;
                        }

//                    }
                    if (!flag)
                    {
                        userMapperDao.deleteUserMapper(mapper);
                        log.info("删除：" + "ExloginName: " + mapper.getExLoginName() + "  A8: "
                                + loginName);
                        logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                                "ldap.log.deletentry", mapper.getExLoginName(), binding.split("[,]")[0].split("[=]")[1]));
                    }
                }

            }
            // 新加
            this.init();
//            for (int i = 0; i < bindingArrays.length; i++)
//            {
                if (ldapDao.isUserExist(createDnString(binding)))
                {
                    // 其他A8用户没有占用此ldap用户账号才添加
                    String exLoginName = ldapDao.getLoginName(createDnString(binding));
                    bindingPerson(exLoginName, loginName, memberId, binding,logList);

                }
                else
                {
                    log.info("LDAP/AD中无此用户账号" + binding);
                    logList.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                            "ldap.log.notentry", binding));
                }
//            }
        }
        return  logList.toArray(new String[logList.size()]);
    }

    /**
     * 修改用户密码
     * 
     * @param dn
     *            条目
     * @param oldPassWord
     *            旧密码
     * @param newPassword
     *            新密码
     * @return void
     */
    public void modifyUserPassWord(String dn, String oldPassWord, String newPassword)
            throws Exception
    {
        if (StringUtils.isBlank(dn) || StringUtils.isBlank(oldPassWord)
                || StringUtils.isBlank(newPassword))
        {
            throw new Exception("modifyUserPassWord null String");
        }
        try
        {
            init();
            boolean isAuth = ldapDao.isUserExist(createDnString(dn));

            if (isAuth)
            {
                ldapDao.modifyUserPassWord(createDnString(dn), oldPassWord, newPassword);
                log.info(createDnString(dn) + "修改密码成功");
            }
            else
            {
                throw new Exception("此用户在LDAP/AD中不存在，修改LDAP/AD密码不成功！");
            }
        }
        catch (Exception e)
        {
            log.error("修改LDAP/AD密码不成功！", e);
            throw new Exception("修改LDAP/AD密码不成功！", e);
        }
    }

    private String createExUnitCode(String dn)
    {
        String baseDn = LDAPConfig.getInstance().getBaseDn();

        if (dn.indexOf(DCFLAG1) != -1)
        {
            dn = StringUtils.replace(dn, DCFLAG1, DCFLAG2);
        }
        if (baseDn.indexOf(DCFLAG1) != -1)
        {
            baseDn = StringUtils.replace(baseDn, DCFLAG1, DCFLAG2);
        }

        return StringUtils.replace(dn, "," + baseDn, "");

    }

    private String createDnString(String uerMapper)
    {
    	if(uerMapper.indexOf(LDAPConfig.getInstance().getBaseDn())!=-1)
    	{
    		return uerMapper;
    	}
        return uerMapper = uerMapper + "," + LDAPConfig.getInstance().getBaseDn();
    }

    /**
     * 检查ldap用户账号是否存在装载List中
     * 
     * @param exloginName
     *            LDAP/AD账号
     * @param list
     *            装载List
     * @return true 如果存在则返回true
     */
    private boolean checkIsExitExloginName(String exloginName, List<V3xOrgUserMapper> list)
    {
        if (StringUtils.isBlank(exloginName))
        {
            return false;
        }
        for (V3xOrgUserMapper mapper : list)
        {
            if (mapper.getExLoginName().equals(exloginName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查ldap用户账号是否存在A8数据库中
     * 
     * @param exloginName
     *            LDAP/AD账号
     * @param userMapperDao
     * @return true 如果存在则返回true
     */
    private boolean checkisExitExloginNameInDB(String exloginName, UserMapperDao userMapperDao)
    {
        if (StringUtils.isBlank(exloginName))
        {
            return true;
        }
        V3xOrgUserMapper userMapper = userMapperDao.getLoginName(exloginName, LDAPConfig
                .getInstance().getType());
//        V3xOrgUserMapper userMapper = userMapperDao.getExLoginNames(exloginName, LDAPConfig.getInstance().getType());
        if (userMapper == null)
        {
            return false;
        }
        else
        {
            try
            {
                userMapperDao.deleteUserMapper(userMapper);
                return false;
            }
            catch (Exception e)
            {
                log.error(e);
                return true;
            }
        }
    }

    private Map<String, String> parseLDIF(List<String> list)
    {
        Map<String, String> map = new HashMap<String, String>();

        if (list == null || list.size() < 0)
        {
            return map;
        }
        String tem = "";
        int i = 0;
        for (String string : list)
        {
            if (string.indexOf(NOTE) != -1)
            {
                continue;
            }
            else if (string.indexOf(DNFLAG) != -1 && string.indexOf(LDAPFLAG) != -1)
            {
                map.put(string, "-1");
                tem = string;
                i++;
            }
            else if (string.indexOf(PARSEFLAGA1) != -1 || string.indexOf(PARSEFLAGA2) != -1)
            {
                if (i == 1)
                {
                    if (!tem.equals(""))
                    {
                        map.put(tem, string);
                        tem = "";
                    }
                    i = 0;
                }
            }
            else if (i == 1)
            {
                i = 0;
                if (!tem.equals(""))
                {
                    map.remove(tem);
                    tem = "";
                }
            }
        }
        return map;
    }

    private Map<String, String> parseADLDIF(List<String> list)
    {
        Map<String, String> map = new HashMap<String, String>();

        if (list == null || list.size() < 0)
        {
            return map;
        }
        String tem = "";
        int i = 0;
        for (String string : list)
        {
            if (string.indexOf(NOTE) != -1)
            {
                continue;
            }
            else if (string.indexOf(DNFLAG) != -1
                    && (string.indexOf(ADFLAG1) != -1 || string.indexOf(ADFLAG2) != -1))
            {
                map.put(string, "-1");
                tem = string;
                i++;
            }
            else if (string.indexOf(PARSEFLAGA1) != -1 || string.indexOf(PARSEFLAGA2) != -1)
            {
                if (i == 1)
                {
                    if (!tem.equals(""))
                    {
                        map.put(tem, string);
                        tem = "";
                    }
                    i = 0;
                }
            }
            else if (i == 1)
            {
                i = 0;
                if (!tem.equals(""))
                {
                    map.remove(tem);
                    tem = "";
                }
            }
        }
        return map;

    }

    // public String isUserExistLdap(String bindingArray) throws Exception
    // {
    // if(!org.springframework.util.StringUtils.hasText(bindingArray))
    // {
    // return "";
    // }
    // String[] arrays=org.springframework.util.StringUtils.split(bindingArray,
    // ",");
    //        
    // for (int i = 0; i < arrays.length; i++)
    // {
    // this.init();
    // ldapDao.isUserExist(dn)
    // arrays[i];
    // }
    //        
    // return "用户不存在";
    // }
    private void coverBatchBinding(String[] currentArray) throws Exception
    {
        List<V3xOrgUserMapper> list = userMapperDao.getAllAndExId(LDAPConfig.getInstance().getType(), String
                .valueOf(CurrentUser.get().getLoginAccount()));

        for (V3xOrgUserMapper mapper : list)
        {
            boolean flag = false;
            for (int i = 0; i < currentArray.length; i++)
            {
                if (mapper.getLoginName().equals(currentArray[i]))
                {
                    flag = true;
                    break;
                }
            }
            if (!flag)
            {
                if (!ldapDao.isUserExist(createDnString(mapper.getExUnitCode())))
                {
                    userMapperDao.deleteUserMapper(mapper);
                }
            }
        }
    }

    public void setUserMapperDao(UserMapperDao userMapperDao)
    {
        this.userMapperDao = userMapperDao;
    }

    public void userTreeView(List<EntryValueBean> list) throws Exception
    {
        init();
        String baseDn = "";
        long accoutId = CurrentUser.get().getLoginAccount();
       V3xOrgProperty value = getDefaultOU(accoutId);
        if (value != null)
        {
            baseDn = value.getValue();
            if(baseDn.equalsIgnoreCase(LDAPConfig.getInstance().getBaseDn()))
            {
            	baseDn="";
            }
            log.debug("***" + baseDn + "***");
        }

        ldapDao.userTreeView(baseDn, list);
    }

    public List<EntryValueBean> ouTreeView(boolean isRoot) throws Exception
    {
        init();
        String baseDn = "";
        if (!isRoot)
        {
            long accoutId = CurrentUser.get().getLoginAccount();
            // 查询出登录人员单位下的basedn
            V3xOrgProperty value = getDefaultOU(accoutId);
            if (value != null)
            {
                baseDn = value.getValue();
                if(baseDn.equalsIgnoreCase(LDAPConfig.getInstance().getBaseDn()))
                {
                	isRoot=true;
                	baseDn="";
                }
                log.debug("***" + baseDn + "***");
            }
            else
            {
                return null;
            }

        }
        return ldapDao.ouTreeView(baseDn, isRoot);
    }

	public V3xOrgProperty getDefaultOU(long accoutId) {
		V3xOrgProperty value=orgManageDao.findLDAPOrgproperties(accoutId, LDAPConfig.getInstance().getType());
		return value;
	}

    public String[] getUserAttributes(String dn) throws Exception
    {
        init();
//        long accoutId = CurrentUser.get().getLoginAccount();
        // String baseDn="ou=people";
        // String uid=dn+","+baseDn;
        String[] userAttributs4 = ldapDao.getuserAttribute(dn);

        return userAttributs4;
    }

//    public void setLdapSetDao(LDAPSetDao ldapSetDao)
//    {
//        this.ldapSetDao = ldapSetDao;
//    }

    public void saveOrUpdateLdapSet(V3xLdapRdn value) throws Exception
    {
        V3xOrgProperty property=orgManageDao.findLDAPOrgproperties(value.getOrgAccountId(), LDAPConfig.getInstance().getType());
        List<V3xOrgProperty> prps = new ArrayList<V3xOrgProperty>(1);
        if(property!=null)
        {
            property.setSourceId(value.getOrgAccountId());
            property.setOrgAccountId(value.getOrgAccountId());
            property.setName(value.getLdapType());
            property.setValue(value.getRootAccountRdn());
            prps.add(property);
            orgManageDao.updateEntitys(prps);
        }
        else
        {
            property=new V3xOrgProperty();
            property.setSourceId(value.getOrgAccountId());
            property.setOrgAccountId(value.getOrgAccountId());
            property.setName(value.getLdapType());
            property.setValue(value.getRootAccountRdn());
            prps.add(property);
            orgManageDao.addEntitys(prps);
        }
    }

    public V3xLdapRdn findLdapSet(Long orgAccountId) throws Exception
    {
        V3xOrgProperty value=orgManageDao.findLDAPOrgproperties(orgAccountId, LDAPConfig.getInstance().getType());
//        return ldapSetDao.findByAccountIdAndType(orgAccountId, LDAP_TYPE);
        V3xLdapRdn value1=null;
        if(value!=null)
        {
             value1=new V3xLdapRdn();
            value1.setOrgAccountId(value.getOrgAccountId());
            value1.setRootAccountRdn(value.getValue());
            value1.setLdapType(LDAPConfig.getInstance().getType());
        }
        return value1;
    }

    public boolean createNode(V3xOrgMember member,String selectOU) throws Exception
    {
        String ou = "";
        String dn = "";
//        V3xLdapRdn value = ldapSetDao.findByAccountIdAndType(CurrentUser.get().getLoginAccount(),
//                LDAPConfig.getInstance().getType());
        V3xOrgProperty value=orgManageDao.findLDAPOrgproperties(CurrentUser.get().getLoginAccount(), LDAPConfig.getInstance().getType());
        if (value != null&&(StringUtils.isBlank(selectOU)))
        {
//            ou = value.getRootAccountRdn();
            ou = value.getValue();
        }
        else if(StringUtils.isNotBlank(selectOU))
        {
        	ou=selectOU;
        }
        else
        {
            return false;
        }
        init();
        if (ldapDao instanceof AdDaoImp)
        {
            dn = ADFLAG1 + "=" + member.getLoginName() + "," + ou;
        }
        else
        {
            dn = LDAPFLAG + "=" + member.getLoginName() + "," + ou;
        }
        if (ldapDao.isUserExist(createDnString(dn)))
        {
            return false;
        }
        else
        {
            String[] parameters = new String[3];

            parameters[0] = member.getLoginName();
            parameters[1] = member.getName();
            parameters[2] = member.getPassword();

            log.debug("***" + dn + "***");
            if (ldapDao.createNode(createDnString(dn), parameters))
            {
//                StringBuffer  sb=new StringBuffer();
                bindingPerson(parameters[0], parameters[0], member.getId(), dn,null);
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    private void bindingPerson(String exLoginName, String a8loginName, long memberId,
            String exUnitCode,List<String> sb)
    {
    	V3xOrgUserMapper userMapper=null;
        if (userMapperDao.getLoginName(exLoginName, LDAPConfig.getInstance().getType()) == null)
        {
             userMapper = new V3xOrgUserMapper();

            userMapper.setLoginName(a8loginName);
            userMapper.setMemberId(memberId);
            userMapper.setType(LDAPConfig.getInstance().getType());
            userMapper.setExPassword("null");
            userMapper.setExLoginName(exLoginName);
            userMapper.setExUnitCode(exUnitCode);
            userMapper.setExId(CurrentUser.get().getLoginAccount()+"");
            userMapperDao.saveUserMapper(userMapper);
            log.info("A8账号 " + a8loginName + " 成功绑定条目： " + exUnitCode);
//            if(sb!=null)
//            sb.append(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
//             "ldap.log.entrybinding", a8loginName, exUnitCode)
//             + "|");
        }
        else
        {
        	 userMapper=userMapperDao.getUserMapperByExId(exLoginName, CurrentUser.get().getLoginAccount()+"");
        	
        	if(userMapper!=null)
        	{
        		//同一单位就删除绑定
        		userMapperDao.deleteUserMapper(userMapper);
        		
        		    userMapper = new V3xOrgUserMapper();

                   userMapper.setLoginName(a8loginName);
                   userMapper.setMemberId(memberId);
                   userMapper.setType(LDAPConfig.getInstance().getType());
                   userMapper.setExPassword("null");
                   userMapper.setExLoginName(exLoginName);
                   userMapper.setExUnitCode(exUnitCode);
                   userMapper.setExId(CurrentUser.get().getLoginAccount()+"");
                   userMapperDao.saveUserMapper(userMapper);
                   log.info("A8账号 " + a8loginName + " 删除之前绑定后绑定条目： " + exUnitCode);
        	}
        	else
        	{
        		
            log.info("添加LDAP/AD用户账号已经绑定其他单位A8用户,不能再绑定本单位下A8账号");
            if(sb!=null)
            sb.add(ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.log.bindingmuch", exUnitCode));
        	}
        }
    }

    // 创建ldap/ad属性文件
    private V3xLdapSwitchBean createLdapProperties(File file, V3xLdapSwitchBean ldapSwitchBean)
            throws IOException
    {
        if (ldapSwitchBean == null)
        {
            ldapSwitchBean=new V3xLdapSwitchBean();
            ldapSwitchBean.setLdapUrl(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_URL,"128.2.3.123"));
            ldapSwitchBean.setLdapPort(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_PORT,"389"));
            ldapSwitchBean.setLdapBasedn(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_BASEDN,"dc=seeyon,dc=com"));
            ldapSwitchBean.setLdapAdmin(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_ADMIN,"cn=Manager"));
            //bug:37246 start by MENG
            String pwd=SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_PASSWORD,"secret");
            ldapSwitchBean.setLdapPassword(TextEncoder.encode(pwd));
            //bug:37246 end
            ldapSwitchBean.setLdapEnabled(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_ENABLED,"0"));
            ldapSwitchBean.setLdapAdEnabled(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_AD_ENABLED,"ldap"));
            ldapSwitchBean.setLdapServerType(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_SERVER_TYPE,"sun"));
            ldapSwitchBean.setLdapSSLEnabled(SystemProperties.getInstance().getProperty(LDAPProperties.LDAP_SSL_ENABLED,"0"));
            
            ldapSwitchBean.setHostName(SystemProperties.getInstance().getProperty(LDAPProperties.AD_HOST_NAME,""));
            ldapSwitchBean.setDomainName(SystemProperties.getInstance().getProperty(LDAPProperties.AD_DOMAIN_NAME,""));
            ldapSwitchBean.setPrincipal(SystemProperties.getInstance().getProperty(LDAPProperties.AD_PRINCIPAL,""));
        }
        FileOutputStream fileStream = new FileOutputStream(file, true);
        file.createNewFile();
        
        Properties properties = new Properties();
        
        properties.put(LDAPProperties.LDAP_URL, ldapSwitchBean.getLdapUrl());
        properties.put(LDAPProperties.LDAP_PORT, ldapSwitchBean.getLdapPort());
        properties.put(LDAPProperties.LDAP_BASEDN, ldapSwitchBean.getLdapBasedn());
        properties.put(LDAPProperties.LDAP_ADMIN, ldapSwitchBean.getLdapAdmin());
        properties.put(LDAPProperties.LDAP_PASSWORD, TextEncoder.encode(ldapSwitchBean.getLdapPassword()));
        properties.put(LDAPProperties.LDAP_ENABLED, ldapSwitchBean.getLdapEnabled());
        properties.put(LDAPProperties.LDAP_AD_ENABLED, ldapSwitchBean.getLdapAdEnabled());
//        properties.put(LDAPProperties.LDAP_LOCALAUTH, ldapSwitchBean.getLdapLocalAuth());
        properties.put(LDAPProperties.LDAP_AUTHENICATION, LDAPProperties.LDAP_SIMPLE);
        properties.put(LDAPProperties.LDAP_SERVER_TYPE, ldapSwitchBean.getLdapServerType());
        properties.put(LDAPProperties.LDAP_SSL_ENABLED, ldapSwitchBean.getLdapSSLEnabled());
        properties.put(LDAPProperties.AD_HOST_NAME, ldapSwitchBean.getHostName());
        properties.put(LDAPProperties.AD_DOMAIN_NAME, ldapSwitchBean.getDomainName());
        properties.put(LDAPProperties.AD_PRINCIPAL, ldapSwitchBean.getPrincipal());
        properties.store(fileStream, "");

        fileStream.close();

        return ldapSwitchBean;
    }

    private V3xLdapSwitchBean readLdapProperties(File file)
    {
        V3xLdapSwitchBean bean = new V3xLdapSwitchBean();
        FileInputStream fileInput = null;
        try
        {
            fileInput = new FileInputStream(file);
            Properties properties = new Properties();

            properties.load(fileInput);
            bean.setLdapUrl(properties.getProperty(LDAPProperties.LDAP_URL));
            bean.setLdapPort(properties.getProperty(LDAPProperties.LDAP_PORT));
            bean.setLdapBasedn(properties.getProperty(LDAPProperties.LDAP_BASEDN));
            bean.setLdapAdmin(properties.getProperty(LDAPProperties.LDAP_ADMIN));
            bean.setLdapPassword(properties.getProperty(LDAPProperties.LDAP_PASSWORD));
            bean.setLdapEnabled(properties.getProperty(LDAPProperties.LDAP_ENABLED));
            bean.setLdapAdEnabled(properties.getProperty(LDAPProperties.LDAP_AD_ENABLED));
            bean.setLdapServerType(properties.getProperty(LDAPProperties.LDAP_SERVER_TYPE));
            bean.setLdapSSLEnabled(properties.getProperty(LDAPProperties.LDAP_SSL_ENABLED));
            bean.setDomainName(properties.getProperty(LDAPProperties.AD_DOMAIN_NAME));
            bean.setHostName(properties.getProperty(LDAPProperties.AD_HOST_NAME));
            bean.setPrincipal(properties.getProperty(LDAPProperties.AD_PRINCIPAL));
//            bean.setLdapLocalAuth(properties.getProperty(LDAPProperties.LDAP_LOCALAUTH));

            fileInput.close();
        }
        catch (FileNotFoundException e)
        {
            log.error(e.getMessage(), e);
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
        return bean;
    }
    /**
     * 保存ldap配置信息
     * 
     * @param ldapSwitchBean
     *         配置信息Bean
     * @return V3xLdapSwitchBean 结果
     */
    public V3xLdapSwitchBean saveLdapSwitch(V3xLdapSwitchBean ldapSwitchBean) throws Exception
    {
        V3xLdapSwitchBean bean = null;
        String baseFolder = SystemEnvironment.getA8BaseFolder().replaceAll("\\\\", "/");
//        String LDAP_SWITCH_FOLDER = SystemProperties.getInstance().getProperty("ldap.savePath");
        String fileFolderPath = baseFolder + LDAP_SWITCH_FOLDER;

        String filePath = baseFolder + LDAP_SWITCH_FOLDER + LDAP_SWITCH_FILE;
        log.debug(filePath);
        File fileDir = new File(fileFolderPath);
        if (!fileDir.exists())
        {
            fileDir.mkdir();
        }
       
        File file = new File(filePath);
        if (file.exists())
        {
            if (!file.delete())
                throw new Exception("删除失败： " + filePath);
        }
        bean = createLdapProperties(file, ldapSwitchBean);
        LDAPProperties.loadProperties();
        LDAPConfig.createInstance();
        return bean;
    }

    public V3xLdapSwitchBean viewLdapSwitch() throws Exception
    {
        V3xLdapSwitchBean bean = null;
        String baseFolder = SystemEnvironment.getA8BaseFolder().replaceAll("\\\\", "/");
//        String LDAP_SWITCH_FOLDER = SystemProperties.getInstance().getProperty("ldap.savePath");
        String fileFolderPath = baseFolder + LDAP_SWITCH_FOLDER;
        String filePath = baseFolder + LDAP_SWITCH_FOLDER + LDAP_SWITCH_FILE;
//        log.info(filePath);
        File fileDir = new File(fileFolderPath);
        if (!fileDir.exists())
        {
            fileDir.mkdir();
        }
        File file = new File(filePath);
        if (!file.exists())
        {
            bean = createLdapProperties(file, null);
        }
        else
        {
            bean = this.readLdapProperties(file);
        }

        return bean;
    }

    public void deleteLdapSet(Long orgAccountId) throws Exception
    {
//       V3xLdapRdn ldapRdn= ldapSetDao.findByAccountIdAndType(orgAccountId, LDAP_TYPE);
//       ldapSetDao.delete(ldapRdn);
        V3xOrgProperty  orgproperty= new V3xOrgProperty();
        orgproperty.setOrgAccountId(orgAccountId);
        orgproperty.setName(LDAPConfig.getInstance().getType());
        orgManageDao.removeOrgproperty(orgproperty);
    }

    public void setOrgManageDao(OrgManageDao orgManageDao)
    {
        this.orgManageDao = orgManageDao;
    }
}
