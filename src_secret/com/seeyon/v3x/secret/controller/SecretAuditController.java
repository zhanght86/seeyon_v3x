package com.seeyon.v3x.secret.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ExportHelper;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgDutyLevel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgModel;
import com.seeyon.v3x.secret.domain.SecretAudit;
import com.seeyon.v3x.secret.domain.WebSecretAudit;
import com.seeyon.v3x.secret.manager.SecretAuditManager;
import com.seeyon.v3x.system.signet.domain.WebAppLog;
import com.seeyon.v3x.util.Datetimes;

/**
 * 安全密保管理器
 * @author Yang.Yinghai
 * @date 2012-8-31下午12:27:51
 * @Copyright(c) Beijing Seeyon Software Co.,LTD
 */
@CheckRoleAccess(roleTypes = {RoleType.SecretAdmin})
public class SecretAuditController extends BaseController {

    private static final Log log = LogFactory.getLog(SecretAuditController.class);

    private OrgManagerDirect orgManagerDirect;

    private SecretAuditManager secretAuditManager;

    private MetadataManager metadataManager;

    private OrgManager orgManager;

    private AppLogManager appLogManager;

    private MenuManager menuManager;

    private StaffInfoManager staffInfoManager;

    private FileToExcelManager fileToExcelManager;

    /**
     * 显示组织树形结构
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 页面对象
     * @throws Exception 异常
     */
    public ModelAndView showtree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("secretAudit/listMember/lefttree");
        // 单位列表
        List<V3xOrgAccount> accountlist = orgManager.getAllAccounts();
        // 为了优化后面取父部门性能，此处取出的部门需要缓存到Map
        Map<String, V3xOrgDepartment> deptPathMap = null;
        List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
        for(V3xOrgAccount account : accountlist) {
            if(!account.getIsRoot()) {
                List<V3xOrgDepartment> tempList = orgManagerDirect.getAllDepartments(account.getId(),false);
                if(tempList != null && tempList.size() != 0) {
                    // 排序
                    Collections.sort(tempList, CompareSortEntity.getInstance());
                    deptPathMap = new HashMap<String, V3xOrgDepartment>();
                    for(V3xOrgDepartment dept : tempList) {
                        deptPathMap.put(dept.getPath(), dept);
                    }
                    for(V3xOrgDepartment dept : tempList) {
                        if(dept.getEnabled() == null || !dept.getEnabled()) {
                            continue; // 不含停用的
                        }
                        dept.getCode();
                        V3xOrgDepartment pdept = deptPathMap.get(dept.getParentPath());
                        if(pdept != null && (pdept.getEnabled() == null || !pdept.getEnabled())) {
                            continue; // 父部门停用的本部门也应该是停用的，这里是为了处理已有的错误数据
                        }
                        WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
                        webdept.setV3xOrgDepartment(dept);
                        if(null != pdept) {
                            webdept.setParentId(pdept.getId());
                            webdept.setParentName(pdept.getName());
                        }
                        resultlist.add(webdept);
                    }
                }
            } else {
                modelAndView.addObject("groupAccountId", account.getId());
            }
        }
        modelAndView.addObject("accountlist", accountlist);
        modelAndView.addObject("deptlist", resultlist);
        return modelAndView;
    }

    /**
     * 进入人员管理数据方法，对应角色：单位管理员、HR管理员
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 页面对象
     * @throws Exception 异常
     */
    public ModelAndView listMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("secretAudit/listMember/listMember");
        String accountIdStr = request.getParameter("selectAccountId");
        String departmentIdStr = request.getParameter("selectDepartmentId");
        List<Long> orgAccountIds = null;
        List<Long> orgDepartmentIds = null;
        if(StringUtils.isNotBlank(accountIdStr)) {
            Long accountId = Long.valueOf(accountIdStr);
            V3xOrgAccount account = orgManager.getAccountById(accountId);
            if(!account.getIsRoot()) {
                orgAccountIds = new ArrayList<Long>();
                // 添加当前选择单位
                orgAccountIds.add(accountId);
                // 添加当前选择单位的子单位
                List<V3xOrgAccount> list = orgManager.getChildAccount(accountId);
                if(list != null & list.size() != 0) {
                    for(V3xOrgAccount orgAccount : list) {
                        orgAccountIds.add(orgAccount.getId());
                    }
                }
            }
        }
        if(StringUtils.isNotBlank(departmentIdStr)) {
            Long departmentId = Long.valueOf(departmentIdStr);
            orgDepartmentIds = new ArrayList<Long>();
            // 添加当前选择部门
            orgDepartmentIds.add(departmentId);
            // 添加当前选择部门的子部门
            List<V3xOrgDepartment> list = orgManagerDirect.getChildDepartments(departmentId, true);
            if(list != null & list.size() != 0) {
                for(V3xOrgDepartment orgDepartment : list) {
                    orgDepartmentIds.add(orgDepartment.getId());
                }
            }
        }
        List<Integer> stateList = new ArrayList<Integer>();
        stateList.add(SecretAudit.STATE_WAIT);
        try {
            List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
            long deptId = -1;
            long levelId = -1;
            long postId = -1;
            List<SecretAudit> auditList = secretAuditManager.querySecretAuditUnits(null, orgAccountIds, orgDepartmentIds, null, null, stateList);;
            if(auditList != null) {
                for(SecretAudit audit : auditList) {
                    V3xOrgMember member = orgManager.getMemberById(audit.getOrgMemberId());
                    deptId = member.getOrgDepartmentId();
                    levelId = member.getOrgLevelId();
                    postId = member.getOrgPostId();
                    WebV3xOrgMember webMember = new WebV3xOrgMember();
                    webMember.setNewSecretLevel(audit.getSecretLevel());
                    webMember.setV3xOrgMember(member);
                    V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
                    if(dept != null) {
                        webMember.setDepartmentName(dept.getName());
                    }
                    V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
                    if(null != level) {
                        webMember.setLevelName(level.getName());
                    }
                    V3xOrgPost post = orgManagerDirect.getPostById(postId);
                    if(null != post) {
                        webMember.setPostName(post.getName());
                    }
                    resultlist.add(webMember);
                }
            }
            result.addObject("memberlist", resultlist);
            // 判断是什么版本
            boolean showAssign = (Boolean)(SysFlag.org_showGroupAccountAssign.getFlag());
            result.addObject("showAssign", showAssign);
            // 判断是否含有NC插件
            boolean hasNC = SystemEnvironment.hasPlugin("nc");
            result.addObject("hasNC", hasNC);
            // 获得单位类别下拉列表中的数据
            Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
            result.addObject("orgMeta", orgMeta);
            return result;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 进入修改人员管理界面方法
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 页面对象
     * @throws Exception 异常
     */
    public ModelAndView editMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView result = new ModelAndView("secretAudit/listMember/editMember");
        String id = request.getParameter("id");
        V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(id));
        StaffInfo staffInfo = staffInfoManager.getStaffInfoById(Long.parseLong(id));
        result.addObject("staff", staffInfo);
        if(null != staffInfo) {
            if(null != staffInfo.getImage_id() && !staffInfo.getImage_id().equals("")) {
                result.addObject("image", 0);
            }
        }
        // 不能把密码返回给界面
        member.setPassword(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD);
        long deptId = member.getOrgDepartmentId();
        long levelId = member.getOrgLevelId();
        long postId = member.getOrgPostId();
        WebV3xOrgMember webMember = new WebV3xOrgMember();
        webMember.setV3xOrgMember(member);
        // 获取扩展属性
        orgManagerDirect.loadEntityProperty(member);
        webMember.setOfficeNum(member.getProperty("officeNum"));
        V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
        if(dept != null) {
            webMember.setDepartmentName(dept.getName());
        }
        V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
        if(null != level) {
            if(level.getEnabled()) {
                webMember.setLevelName(level.getName());
            } else {
                member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
            }
        }
        V3xOrgPost post = orgManagerDirect.getPostById(postId);
        if(null != post) {
            if(post.getEnabled()) {
                webMember.setPostName(post.getName());
            } else {
                member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
            }
        }
        // 获取待审核记录
        SecretAudit audit = secretAuditManager.getWaitAuditByMemberId(member.getId());
        if(audit != null) {
            webMember.setNewSecretLevel(audit.getSecretLevel());
        }
        // 取得人员的副岗
        List<MemberPost> memberPosts = member.getSecond_post();
        List<WebV3xOrgModel> secondPostList = new ArrayList<WebV3xOrgModel>();
        if(null != memberPosts && !memberPosts.isEmpty()) {
            StringBuffer deptpostbuffer = new StringBuffer();
            StringBuffer deptpostbufferId = new StringBuffer();
            for(MemberPost memberPost : memberPosts) {
                WebV3xOrgModel webModel = new WebV3xOrgModel();
                StringBuffer sbuffer = new StringBuffer();
                StringBuffer sbufferId = new StringBuffer();
                Long deptid = memberPost.getDepId();
                V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(deptid);
                Long postid = memberPost.getPostId();
                V3xOrgPost v3xpost = orgManagerDirect.getPostById(postid);
                // 只有部门岗位都是有效的才显示副岗
                if(v3xdept != null && v3xdept.getEnabled() && v3xpost != null && v3xpost.getEnabled()) {
                    sbuffer.append(v3xdept.getName());
                    sbuffer.append("-");
                    sbufferId.append(v3xdept.getId());
                    sbufferId.append("_");
                    sbuffer.append(v3xpost.getName());
                    sbufferId.append(v3xpost.getId());
                    deptpostbuffer.append(sbuffer.toString());
                    deptpostbuffer.append(",");
                    deptpostbufferId.append(sbufferId.toString());
                    deptpostbufferId.append(",");
                    webModel.setSecondPostId(v3xdept.getId() + "_" + v3xpost.getId());
                    webModel.setSecondPostType("Department_Post");
                    secondPostList.add(webModel);
                }
            }
            if(deptpostbuffer.length() > 0) {
                String deptpostStr = deptpostbuffer.substring(0, deptpostbuffer.length() - 1);
                String deptpostStrId = deptpostbufferId.substring(0, deptpostbufferId.length() - 1);
                webMember.setSecondPosts(deptpostStr);
                result.addObject("secondPostM", deptpostStrId);
            }
        }
        result.addObject("secondPostList", secondPostList);
        result.addObject("member", webMember);
        // 取得是否是详细页面标志
        String isDetail = request.getParameter("isDetail");
        boolean readOnly = false;
        if(null != isDetail && isDetail.equals("readOnly")) {
            readOnly = true;
            result.addObject("readOnly", readOnly);
            result.addObject("preview", 0);
        } else {
            result.addObject("preview", 1);
        }
        // 取得人员兼职信息
        List<ConcurrentPost> cntList = orgManagerDirect.getAllConcurrentPostByMemberId(member.getId());
        if(!ListUtils.EMPTY_LIST.equals(cntList)) {
            result.addObject("cntList", cntList);
        } else {
            result.addObject("cntList", null);
        }
        // 取得个人角色
        List<String[]> roleNameList = new ArrayList<String[]>();
        List<V3xOrgRelationship> relList = orgManagerDirect.getRolesByMember(Long.parseLong(id));
        for(V3xOrgRelationship rel : relList) {
            String[] roleStr = new String[2];
            V3xOrgRole nowRole = orgManagerDirect.getRoleById(rel.getBackupId());
            if(rel.getType().equals(V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE)) {
                roleStr[0] = "";
                roleStr[1] = nowRole == null ? "" : nowRole.getName();
            } else if(rel.getType().equals(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE)) {
                roleStr[0] = orgManagerDirect.getDepartmentById(rel.getObjectiveId()).getName();
                roleStr[1] = nowRole == null ? "" : nowRole.getName();
            }
            roleNameList.add(roleStr);
        }
        result.addObject("roleNameList", roleNameList);
        // 获得单位类别下拉列表中的数据
        Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
        result.addObject("orgMeta", orgMeta);
        // 获取职务级别列表
        List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(member.getOrgAccountId(), false);
        List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
        // 过滤无效项
        for(V3xOrgLevel levelForPage : levels) {
            if(levelForPage.getEnabled())
                levelsForPage.add(levelForPage);
        }
        // 判断是否回显密码
        result.addObject("showPassword", 1);
        // 获取该用户菜单权限
        String securityIds = null;
        String securityNames = null;
        List<Security> defaultSecurities = this.menuManager.getSecurityOfMember(member.getId(), member.getOrgAccountId(), true);
        for(Security security : defaultSecurities) {
            if(securityIds == null) {
                securityIds = security.getId().toString();
                securityNames = security.getName();
            } else {
                securityIds += "," + security.getId();
                securityNames += "," + security.getName();
            }
        }
        result.addObject("securityIds", securityIds);
        result.addObject("securityNames", securityNames);
        result.addObject("levels", levelsForPage);
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();// 政务版标识
        // HR枚举
        Map<String, Metadata> hrMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.hr);
        result.addObject("hrMetadata", hrMetadata);
        // 政务版--职级--start
        // 获取职级列表
        if(isGovVersion) {
            List<V3xOrgDutyLevel> dutyLevels = orgManagerDirect.getAllDutyLevels(CurrentUser.get().getLoginAccount(), false);
            List<V3xOrgDutyLevel> dutylevelsForPage = new ArrayList<V3xOrgDutyLevel>();
            // 过滤无效项
            for(V3xOrgDutyLevel dutylevel : dutyLevels) {
                if(dutylevel.getEnabled()) {
                    dutylevelsForPage.add(dutylevel);
                }
            }
            result.addObject("dutyLevels", dutylevelsForPage);
        }
        // 政务版--职级--end
        return result;
    }

    /**
     * 审核人员密级
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 页面对象
     * @throws Exception 异常
     */
    public ModelAndView auditMemberSecretLevel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String state = request.getParameter("state");
        String id = request.getParameter("id");
        String resources = "com.seeyon.v3x.secret.resources.i18n.SecretAuditResources";
        SecretAudit audit = secretAuditManager.getWaitAuditByMemberId(Long.valueOf(id));
        V3xOrgMember member = orgManager.getMemberById(Long.valueOf(id));
        String auditResult = "";
        if("pass".equals(state)) {
            audit.setState(SecretAudit.STATE_PASS);
            member.setSecretLevel(audit.getSecretLevel());
            orgManager.updateMember(member);
            auditResult = ResourceBundleUtil.getString(resources, "audit.state.2");
        } else {
            audit.setState(SecretAudit.STATE_NOTPASS);
            auditResult = ResourceBundleUtil.getString(resources, "audit.state.3");
        }
        audit.setAuditTime(new Date());
        // 更新审核状态
        secretAuditManager.update(audit);
        User user = CurrentUser.get();
        user.setId(2L);
        // 记录审核日志
        appLogManager.insertLog(user, AppLogAction.SecretLevel_audit, user.getName(), member.getName(), auditResult);
        super.rendJavaScript(response, "parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
        return null;
    }

    /**
     * 系统管理员查询
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView querySecretAuditData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("secretAudit/auditLog/searchAuditLog");
        String fromDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        String selectPersonIds = request.getParameter("selectPersonIds");
        List<Long> userIds = null;
        if(StringUtils.isNotBlank(selectPersonIds)) {
            String allMembers[] = selectPersonIds.split(",");
            userIds = new ArrayList<Long>();
            for(int i = 0; i < allMembers.length; i++) {
                String user[] = allMembers[i].split("\\|");
                userIds.add(Long.valueOf(user[1]));
            }
        }
        List<Integer> stateList = new ArrayList<Integer>();
        stateList.add(SecretAudit.STATE_PASS);
        stateList.add(SecretAudit.STATE_NOTPASS);
        List<SecretAudit> list = secretAuditManager.querySecretAuditUnits(userIds, null, null, fromDate, endDate, stateList);
        mav.addObject("secretAuditList", getAllWebSecretAudit(list));
        return mav;
    }

    /**
     * 导出日志到Excel
     * @param request HTTP请求
     * @param response HTTP响应
     * @return 页面对象
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 不分页
        Pagination.withoutPagination(null);
        Pagination.setFirstResult(0);
        Pagination.setMaxResults(Integer.MAX_VALUE);
        ModelAndView mav = ExportHelper.excutePageMethod(this, request, response, "pageMethod");
        List<WebSecretAudit> webSecretAuditList = null;
        if(mav != null) {
            webSecretAuditList = (List<WebSecretAudit>)mav.getModel().get("secretAuditList");
        }
        String[] columnName = new String[6];
        String commonResource = "com.seeyon.v3x.secret.resources.i18n.SecretAuditResources";
        columnName[0] = ResourceBundleUtil.getString(commonResource, "secret.his.userName.lable");
        columnName[1] = ResourceBundleUtil.getString(commonResource, "secret.his.accountName.lable");
        columnName[2] = ResourceBundleUtil.getString(commonResource, "secret.his.depatmentName.lable");
        columnName[3] = ResourceBundleUtil.getString(commonResource, "secretLevel");
        columnName[4] = ResourceBundleUtil.getString(commonResource, "secret.his.auditResult.lable");
        columnName[5] = ResourceBundleUtil.getString(commonResource, "secret.his.auditTime.lable");
        List<Object[]> rows = new ArrayList<Object[]>();
        for(WebSecretAudit webSecretAudit : webSecretAuditList) {
            Object[] obj = new Object[7];
            obj[0] = webSecretAudit.getUser();
            obj[1] = webSecretAudit.getAccount();
            obj[2] = webSecretAudit.getDepment();
            obj[3] = ResourceBundleUtil.getString(commonResource, "secretLevel." + webSecretAudit.getSecretLevel());
            obj[4] = ResourceBundleUtil.getString(commonResource, "audit.state." + webSecretAudit.getState());
            if(webSecretAudit.getAuditTime() == null) {
                obj[5] = webSecretAudit.getAuditTime();
            } else {
                obj[5] = Datetimes.formatDatetimeWithoutSecond(webSecretAudit.getAuditTime());
            }
            rows.add(obj);
        }
        String fileName = ResourceBundleUtil.getString(commonResource, "secret.excel.download.fileName");
        String title = ResourceBundleUtil.getString(commonResource, "secret.excel.count.title");
        if(rows.size() == 0) {
            ColHelper.exportToExcel(request, response, fileToExcelManager, fileName, null, columnName, title, "sheet1");
        } else {
            ColHelper.exportToExcel(request, response, fileToExcelManager, fileName, rows, columnName, title, "sheet1");
        }
        return null;
    }

    /**
     * 对查询得到的数据进行封装,用于前台列表显示
     */
    private List<WebSecretAudit> getAllWebSecretAudit(List<SecretAudit> list) throws Exception {
        List<WebSecretAudit> webSecretAuditList = new ArrayList<WebSecretAudit>();
        if(null == list || list.size() == 0) {
            return webSecretAuditList;
        }
        for(SecretAudit secretAudit : list) {
            WebSecretAudit webSecretAudit = new WebSecretAudit();
            webSecretAudit.setId(secretAudit.getId());
            webSecretAudit.setUser(orgManager.getMemberById(secretAudit.getOrgMemberId()).getName());
            webSecretAudit.setAccount(orgManager.getAccountById(secretAudit.getOrgAccountId()).getName());
            webSecretAudit.setDepment(orgManager.getDepartmentById(secretAudit.getOrgDepartmentId()).getName());
            webSecretAudit.setSecretLevel(secretAudit.getSecretLevel());
            webSecretAudit.setState(secretAudit.getState());
            webSecretAudit.setAuditTime(secretAudit.getAuditTime());
            webSecretAudit.setAudit(secretAudit);
            webSecretAuditList.add(webSecretAudit);
        }
        return webSecretAuditList;
    }

    /**
     * 设置secretAuditManager
     * @param secretAuditManager secretAuditManager
     */
    public void setSecretAuditManager(SecretAuditManager secretAuditManager) {
        this.secretAuditManager = secretAuditManager;
    }

    /**
     * 设置orgManager
     * @param orgManager orgManager
     */
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    /**
     * 设置orgManagerDirect
     * @param orgManagerDirect orgManagerDirect
     */
    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    /**
     * 设置metadataManager
     * @param metadataManager metadataManager
     */
    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    /**
     * 设置appLogManager
     * @param appLogManager appLogManager
     */
    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }

    /**
     * 设置fileToExcelManager
     * @param fileToExcelManager fileToExcelManager
     */
    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }

    /**
     * 设置menuManager
     * @param menuManager menuManager
     */
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    /**
     * 设置staffInfoManager
     * @param staffInfoManager staffInfoManager
     */
    public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
        this.staffInfoManager = staffInfoManager;
    }
}
