package com.seeyon.v3x.plugin.ldap.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.fileupload.FileuploadManagerImpl;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.ldap.domain.EntryValueBean;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermapper.dao.UserMapperDao;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.v3x.plugin.ldap.manager.BingdingEnum;
import com.seeyon.v3x.plugin.ldap.manager.LdapBindingMgr;
import com.seeyon.v3x.plugin.ldap.manager.LdapServerMap;
import com.seeyon.v3x.plugin.ldap.manager.VerifyConnection;
import com.seeyon.v3x.util.TextEncoder;

/**
 * ldap/ad 相关操作控制层
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2009-1-6
 */
@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.GroupAdmin, RoleType.Administrator,RoleType.HrAdmin})
public class LDAPSynchronController extends BaseController
{
    private static final Log log = LogFactory.getLog(LDAPSynchronController.class);

    private static final String ENCODING = "UTF-8";

    private OrgManagerDirect orgManagerDirect;

    private OrgManager  orgManager;
    private LdapBindingMgr ldapBindingMgr;
    private AppLogManager appLogManager;
    
    private VerifyConnection verifyConnection;//目录服务器配置验证
    
	private UserMapperDao userMapperDao;

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return null;
    }

    @CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView setLdapSwitch(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav= new ModelAndView("plugin/ldap/ldapSwitch");
        V3xLdapSwitchBean ldapSwitchBean = ldapBindingMgr.viewLdapSwitch();
        //不能把密码传给前台应用DEFAULT_INTERNAL_PASSWORD="~`@%^*#?"
        ldapSwitchBean.setLdapPassword(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD);
        mav.addObject("v3xLdapSwitchBean", ldapSwitchBean);
        mav.addObject("ldapMap", LdapServerMap.getMap());
        return mav;
    }

    @CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView saveLdapSwitchParams(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav= new ModelAndView("plugin/ldap/ldapSwitch");
        User user = CurrentUser.get();
        V3xLdapSwitchBean bean= new V3xLdapSwitchBean();
        bind(request, bean);
        PrintWriter out = response.getWriter();
        String saveTip=ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.system.set");
        try
        {
        	//检查密码是否修改
        	if(bean.getLdapPassword().equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)){
        		String realPassword = TextEncoder.decode(ldapBindingMgr.viewLdapSwitch().getLdapPassword());
        		bean.setLdapPassword(realPassword);
        	}
        	
        	if(bean.getLdapEnabled().equals("1")){
        		boolean connect = verifyConnection.verify(bean);//验证目录服务器配置
                if(!connect){
                	saveTip=ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.set.error");
                }
        	}
        	ldapBindingMgr.saveLdapSwitch(bean);
            appLogManager.insertLog(user, AppLogAction.DirectoryConfig);
        }catch (Throwable e){
        	saveTip=ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.set.error");
            log.info(e.getMessage(),e);
        }
        out.println("<script>");
        out.println("alert(\""+saveTip+"\");");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/ldap.do?method=setLdapSwitch");
    }

    public ModelAndView importLDIF(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/fileUpload");
        String str = FileuploadManagerImpl.getMaxSizeStr();
        int deleteAll = com.seeyon.v3x.plugin.ldap.manager.BingdingEnum.deleteAll.key();
        int coverAll = com.seeyon.v3x.plugin.ldap.manager.BingdingEnum.coverAll.key();
        mav.addObject("deleteAll", deleteAll);
        mav.addObject("coverAll", coverAll);
        mav.addObject("maxSize", str);
        return mav;
    }

    public ModelAndView frameset(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return new ModelAndView("plugin/ldap/frameset");
    }
    public ModelAndView openHelp(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return new ModelAndView("plugin/ldap/help");
	}
    public ModelAndView uploadReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/report");

        String time = request.getParameter("parseTime");

//        List<V3xOrgUserMapper> usersList = userMapperDao.getAll(LDAPConfig.getInstance().getType());

//        int maxCount = usersList.size();
//        mav.addObject("maxCount", maxCount);
        if (StringUtils.isNotBlank(time))
        {
            mav.addObject("showTime", showTime(new Long(time)));
        }
        return mav;
    }

    public ModelAndView uploadProcess(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/fileUpload");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        multipartRequest.setCharacterEncoding(ENCODING);
        Iterator fileNames = multipartRequest.getFileNames();
        // Enumeration param = multipartRequest.getParameterNames();
        // if (param.hasMoreElements())
        // {
        // Object name = param.nextElement();
        // String[] dd
        // =multipartRequest.getParameterValues(String.valueOf(name));
        // }
        long maxSize = -1;
        long endTime = -1;
        try
        {
            long start = System.currentTimeMillis();
            while (fileNames.hasNext())
            {
                Object name = fileNames.next();

                if (name == null || "".equals(name))
                {
                    continue;
                }
                MultipartFile fileItem = multipartRequest.getFile(String.valueOf(name));

                log.info(fileItem.getOriginalFilename());
                String fileUpload_maxSize = SystemProperties.getInstance().getProperty(
                        "fileUpload.maxSize");
                if (fileUpload_maxSize != null && !"".equals(fileUpload_maxSize))
                {
                    maxSize = Long.parseLong(fileUpload_maxSize);
                }
                if (fileItem != null && fileItem.getSize() < maxSize)
                {
                    InputStream stream = fileItem.getInputStream();
                    List<String> list = IOUtils.readLines(stream);
                    if (list == null || list.size() == 0)
                    {
                        super.rendJavaScript(response, "parent.endProcess();");
                        return null;
                    }
//                    List<V3xOrgMember> memberlist = orgManagerDirect.getAllMembers(CurrentUser
//                            .get().getLoginAccount(),false);
                    List<V3xOrgMember> memberlist = orgManager.getAllMembers(CurrentUser.get().getLoginAccount());
                    if (memberlist == null || memberlist.isEmpty())
                    {
                        super.rendJavaScript(response, "alert('"
                                + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                                        "ldap.alert.nonemember") + "');parent.endProcess();");
                        return null;
                    }
                    String[] bindingOption = multipartRequest.getParameterValues("bindingOption");

                    int isCover = 0;
                    if(bindingOption!=null)
                    {
                    	for (int i = 0; i < bindingOption.length; i++)
                    	{
                    		if (bindingOption[i].equals(BingdingEnum.deleteAll.key() + ""))
                    		{
                    			ldapBindingMgr.deleteAllBinding(orgManagerDirect, memberlist);
                    		}
                    		if (bindingOption[i].equals(BingdingEnum.coverAll.key() + ""))
                    		{
                    			isCover = BingdingEnum.coverAll.key();
                    		}
                    	}
                    }
                    ldapBindingMgr.batchBinding(orgManagerDirect, list, memberlist, isCover);

                    endTime = System.currentTimeMillis() - start;
                    log.info("解析LDIF结束用时：" + endTime);
                }
                else
                {
                    super.rendJavaScript(response, "alert(\""
                            + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                            "ldap.alert.toomuch") + "\");"+"parent.endProcess();");
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            super.rendJavaScript(response, "alert(\""
                    + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                    "ldap.alert.exception") + ": " + e.getMessage() + "\");"+"parent.endProcess();");
            return null;
        }
        mav.addObject("parseTime", endTime);
        return mav;
        // return
        // super.redirectModelAndView("/ldap.do?method=uploadReport&parseTime="
        // + endTime);
    }

    public ModelAndView listUsers(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/listusers");
        List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();

        V3xOrgAccount account = orgManagerDirect
                .getAccountById(CurrentUser.get().getLoginAccount());
        if (account == null)
        {
            return mav;
        }

        List<V3xOrgMember> memberlistTemp = orgManagerDirect.getAllMembers(account.getId());
        if (memberlistTemp != null)
        {
            memberlist = memberlistTemp;
        }
        else
        {
            return mav;
        }
        // for (V3xOrgAccount account : accountList)
        // {
        // List<V3xOrgMember> memberlistTemp =
        // orgManagerDirect.getAllMembers(account.getId());
        // memberlist.addAll(memberlistTemp);
        // }
        String reload = request.getParameter("reload");
        String textfield = request.getParameter("textfield");
        if (StringUtils.isNotBlank(textfield))
        {
            for (Iterator it = memberlist.iterator(); it.hasNext();)
            {
                V3xOrgMember member = (V3xOrgMember) it.next();
                if (!member.getLoginName().matches(".*" + textfield + ".*"))
                {
                    it.remove();
                }
            }
        }
        Collections.sort(memberlist, CompareSortEntity.getInstance());
        List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
        long deptId = -1;
        long levelId = -1;
        long postId = -1;
        long accountId = -1;
        int first = Pagination.getFirstResult();
        int max = Pagination.getMaxResults();
        int rowCount = Pagination.getRowCount();
        if (null != memberlist)
        {
            for (V3xOrgMember member : memberlist)
            {
                deptId = member.getOrgDepartmentId();
                levelId = member.getOrgLevelId();
                postId = member.getOrgPostId();
                accountId = member.getOrgAccountId();
                WebV3xOrgMember webMember = new WebV3xOrgMember();
                webMember.setV3xOrgMember(member);
                V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                if (dept != null)
                {
                    webMember.setDepartmentName(dept.getName());
                }
                if ((Boolean)SysFlag.sys_isGroupVer.getFlag())
                {
                    // V3xOrgAccount account =
                    // orgManagerDirect.getAccountById(accountId);

                    if (account != null)
                    {
                        webMember.setAccountName(account.getName());
                    }
                }
                V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
                if (null != level)
                {
                    webMember.setLevelName(level.getName());
                }

                V3xOrgPost post = orgManagerDirect.getPostById(postId);
                if (null != post)
                {
                    webMember.setPostName(post.getName());
                }

                // 组装LDAP/AD字符串
                List<V3xOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member
                        .getLoginName(), LDAPConfig.getInstance().getType());
                String stateNames = "";
                for (V3xOrgUserMapper map : userMappers)
                {
                    stateNames += map.getExLoginName() + ",";
                }
                if (!StringUtils.isBlank(stateNames))
                {
                    stateNames = stateNames.substring(0, stateNames.length() - 1);
                }
                webMember.setStateName(stateNames);
                resultlist.add(webMember);
            }
        }
        Pagination.setNeedCount(true);
        Pagination.setFirstResult(first);
        Pagination.setMaxResults(max);
        Pagination.setRowCount(rowCount);
        mav.addObject("reload", reload);
        mav.addObject("userMapperList", resultlist);
        return mav;
    }

    public ModelAndView query(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/listusers");
        List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();

        String textfield = request.getParameter("textfield");
        List<V3xOrgAccount> accountList = orgManagerDirect.getAllAccounts();

        for (V3xOrgAccount account : accountList)
        {
            List<V3xOrgMember> memberlistTemp = orgManagerDirect.getAllMembers(account.getId(),
                    false);
            memberlist.addAll(memberlistTemp);
        }

        if (StringUtils.isNotBlank(textfield))
        {
            for (Iterator it = memberlist.iterator(); it.hasNext();)
            {
                V3xOrgMember member = (V3xOrgMember) it.next();
                if (!member.getLoginName().matches(".*" + textfield + ".*"))
                {
                    it.remove();
                }
            }
        }
        else
        {
            return super.redirectModelAndView("/ldap.do?method=listUsers");
        }
        Collections.sort(memberlist, CompareSortEntity.getInstance());
        List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
        long deptId = -1;
        long levelId = -1;
        long postId = -1;
        long accountId = -1;

        if (null != memberlist)
        {
            for (V3xOrgMember member : memberlist)
            {
                deptId = member.getOrgDepartmentId();
                levelId = member.getOrgLevelId();
                postId = member.getOrgPostId();
                accountId = member.getOrgAccountId();
                WebV3xOrgMember webMember = new WebV3xOrgMember();
                webMember.setV3xOrgMember(member);
                V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                if (dept != null)
                {
                    webMember.setDepartmentName(dept.getName());
                }
                V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);

                if (account != null)
                {
                    webMember.setAccountName(account.getName());
                }
                V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
                if (null != level)
                {
                    webMember.setLevelName(level.getName());
                }

                V3xOrgPost post = orgManagerDirect.getPostById(postId);
                if (null != post)
                {
                    webMember.setPostName(post.getName());
                }

                // 组装LDAP/AD字符串
                List<V3xOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member
                        .getLoginName(), LDAPConfig.getInstance().getType());
                String stateNames = "";
                for (V3xOrgUserMapper map : userMappers)
                {
                    stateNames += map.getExLoginName() + ",";
                }
                if (!StringUtils.isBlank(stateNames))
                {
                    stateNames = stateNames.substring(0, stateNames.length() - 1);
                }
                webMember.setStateName(stateNames);
                resultlist.add(webMember);
            }
        }
        mav.addObject("userMapperList", resultlist);
        return mav;
    }

    public ModelAndView editUserMapper(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/editUserMapper");
        Long id = Long.parseLong(request.getParameter("id"));
        V3xOrgMember member = orgManagerDirect.getMemberById(id);
        long deptId = member.getOrgDepartmentId();
        long levelId = member.getOrgLevelId();
        long postId = member.getOrgPostId();
        WebV3xOrgMember webMember = new WebV3xOrgMember();
        webMember.setV3xOrgMember(member);
        // 获取扩展属性
        orgManagerDirect.loadEntityProperty(member);
        webMember.setOfficeNum(member.getProperty("officeNum"));
        V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
        if (dept != null)
        {
            webMember.setDepartmentName(dept.getName());
        }

        V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
        if (null != level)
        {
            webMember.setLevelName(level.getName());
        }

        V3xOrgPost post = orgManagerDirect.getPostById(postId);
        if (null != post)
        {
            webMember.setPostName(post.getName());
        }

        // 组装LDAP/AD字符串
        List<V3xOrgUserMapper> userMappers = userMapperDao.getExLoginNames(member.getLoginName(),
                LDAPConfig.getInstance().getType());
        String stateNames = "";
        for (V3xOrgUserMapper map : userMappers)
        {
            stateNames += map.getExUnitCode() + "|";
        }
        if (!StringUtils.isBlank(stateNames))
        {
            stateNames = stateNames.substring(0, stateNames.length() - 1);
        }
        webMember.setStateName(stateNames);

        mav.addObject("member", webMember);
        mav.addObject("oper", request.getParameter("oper"));
        return mav;
    }

    public ModelAndView updateUserMapper(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/updateUserMapper");
        try
        {
            long id = RequestUtils.getLongParameter(request, "id");
            String loginName = request.getParameter("valideLogin");
            String ldapUserCodes = request.getParameter("ldapUserCodes");
            String[] resultArray = {};
            V3xOrgMember member = orgManagerDirect.getMemberByLoginName(loginName);
            if (member.getEnabled())
            {
                resultArray =ldapBindingMgr.handBinding(id, loginName,ldapUserCodes, true);
            }
            else
            {
                resultArray =ldapBindingMgr.handBinding(id, loginName,ldapUserCodes, false);
            }

            mav.addObject("resultArray", resultArray);
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }
        return mav;
    }

    public ModelAndView viewUserTree(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/userTree");

        try
        {
            List<EntryValueBean> list = ldapBindingMgr.ouTreeView(false);

            if (list == null)
            {
            	/*String title="";
                if ((Boolean)SysFlag.sys_isGroupVer.getFlag())
                {
                 title=ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,"ldap.alert.group");
                }*/
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert(\""+ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                "ldap.alert.setdn","")+"\");");
                out.println("window.close();");
                out.println("</script>");
                out.close();
                return null;
            }
            ldapBindingMgr.userTreeView(list);
            mav.addObject("userList", list);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
        }
        mav.addObject("rootDN", LDAPConfig.getInstance().getBaseDn());
        return mav;
    }

    public ModelAndView viewOuTree(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plugin/ldap/ouTree");

        try
        {
            List<EntryValueBean> list = ldapBindingMgr.ouTreeView(true);
            mav.addObject("userList", list);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
        }
        mav.addObject("rootDN", LDAPConfig.getInstance().getBaseDn());
        return mav;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect)
    {
        this.orgManagerDirect = orgManagerDirect;
    }

    public void setLdapBindingMgr(LdapBindingMgr ldapBindingMgr)
    {
        this.ldapBindingMgr = ldapBindingMgr;
    }

    public void setUserMapperDao(UserMapperDao userMapperDao)
    {
        this.userMapperDao = userMapperDao;
    }

    /**
     * @param long
     *            starTime 输入毫秒数，返回带有小时分秒的字符串
     * @return String 小时分秒的字符串
     */
    private String showTime(long starTime)
    {
        if (starTime > 0)
        {
            long endM = 0;
            long starMinute = 0;
            long endMinuteS = 0;
            long starHour = 0;
            long endHourMi = 0;
            if (starTime > 1000)
            {
                long starSecond = starTime / 1000;
                endM = starTime % 1000;

                starMinute = starSecond / 60;

                endMinuteS = starSecond % 60;

                starHour = starMinute / 60;

                endHourMi = starMinute % 60;
            }
            else
            {
                endM = starTime;
            }
            return ((starHour == 0 ? "" : starHour
                    + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                            "org.synchron.hour"))
                    + (endHourMi == 0 ? "" : endHourMi
                            + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                                    "org.synchron.minutes"))
                    + (endMinuteS == 0 ? "" : endMinuteS
                            + ResourceBundleUtil.getString(LDAPConfig.LDAP_RESOURCE_NAME,
                                    "org.synchron.second")) + endM + ResourceBundleUtil.getString(
                    LDAPConfig.LDAP_RESOURCE_NAME, "org.synchron.ms"));
        }
        return null;
    }

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setVerifyConnection(VerifyConnection verifyConnection) {
		this.verifyConnection = verifyConnection;
	}
}
