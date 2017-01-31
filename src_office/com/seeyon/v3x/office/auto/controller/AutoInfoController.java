package com.seeyon.v3x.office.auto.controller;

/**
 *	
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.auto.util.Constants;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
/**
 * 车辆管理员完成车辆的登记，修改及删除的操作控制类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-14
 */
public class AutoInfoController extends BaseManageController
{

    private static Logger log = Logger.getLogger(AutoInfoController.class);

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    private String indexView;

    private String contentView;

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setAutoManager(AutoManager autoManager)
    {
        this.autoManager = autoManager;
    }

    public void setIndexView(String indexView)
    {
        this.indexView = indexView;
    }

    public void setContentView(String contentView)
    {
        this.contentView = contentView;
    }

    /**
     * 车辆管理首页
     */
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView(indexView);
        User user = CurrentUser.get();

        int iResult = this.officeCommonManager.checkAdminModel(1, user);
        if (iResult > 0)
        {
            // 普通用户

            mav = new ModelAndView("office/auto/autoApplyIndex");
        }
        else
        {
            // 管理员

        }

        return mav;
    }

    public ModelAndView frame(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelView = null;
        User user = CurrentUser.get();

        int iResult = this.officeCommonManager.checkAdminModel(1, user);

        if (iResult > 0)
        {
            modelView = new ModelAndView(this.successView);

            if (iResult == 1)
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                                "auto.alert.depart.mgr") + "\");");
            }
            else
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                                " auto.alert.depart.contact.mgr") + "\");");
            }
            return modelView;
        }

        ModelAndView mav = new ModelAndView("office/auto/autoInfoFrame");
        return mav;
    }

    /**
     * 车辆汇总统计
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView count(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelView = null;
        User user = CurrentUser.get();

        int iResult = this.officeCommonManager.checkAdminModel(1, user);

        if (iResult > 0)
        {
            modelView = new ModelAndView(this.successView);

            if (iResult == 1)
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                                "auto.alert.depart.mgr") + "\");");
            }
            else
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                                " auto.alert.depart.contact.mgr") + "\");");
            }
            return modelView;
        }

        ModelAndView mav = new ModelAndView("office/auto/autoCount");

        List departList = new ArrayList();
        List memberList = new ArrayList();

        String isIn = request.getParameter("isIn");
        if(Strings.isBlank(isIn) || "1".equals(isIn)){
        	isIn = "1";
        	memberList = this.autoManager.getAutoSummayByDriver(user.getId(), true);
        } else {
        	departList = this.autoManager.getAutoSummayByDepart(user.getId(), true);
        }
        mav.addObject("isIn", isIn);
        mav.addObject("departList", departList);
        mav.addObject("memberList", memberList);

        return mav;
    }

    public ModelAndView export(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {

        ModelAndView mav = new ModelAndView("office/auto/autoExcel");

        List departList = null;
        List memberList = null;

        departList = this.autoManager.getAutoSummayByDepart(CurrentUser.get().getId(),false);
        memberList = this.autoManager.getAutoSummayByDriver(CurrentUser.get().getId(),false);
        // mav.addObject("departList", departList);
        // mav.addObject("memberList", memberList);
        request.setAttribute("departList", departList);
        request.setAttribute("memberList", memberList);
        String isIn = request.getParameter("isIn");
        if(Strings.isBlank(isIn)){
        	isIn = "1";
        }
        request.setAttribute("isIn", isIn);
        return mav;
    }

    public ModelAndView content(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView(contentView);
        return mav;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    /**
     * 根据“查询条件”检索表“车辆详细信息表” 结果显示在车辆列表中
     */
    protected void onQuery(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        String fieldName = request.getParameter("condition"); // 查询字段名
        if (fieldName == null)
        {
            fieldName = "";
        }

        String fieldValue = request.getParameter("textfield"); // 查询字段值

        if (fieldValue == null)
        {
            fieldValue = "";
        }

        // 取得当前管理员ID
        User curUser = CurrentUser.get();
        Long mangerId = curUser.getId();

        // 取得车辆详细信息列表
        List autoList = new ArrayList();
        // 设置检索条件
        Map keyMap = new HashMap();

        // 车辆信息列表中，全部显示
        // keyMap.put("del_flag",new Integer(0));
//        keyMap.put("autoManager", mangerId);

        autoList = autoManager.getAutoInfo(fieldName, fieldValue, keyMap,mangerId);

        // Pagination.setRowCount(autoList.size());

        // 取得车辆类别设置
        List typeList = this.officeCommonManager.getModelTypes("1", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

        // 保存结果到视图模型中
        modelView.addObject("list", autoList);
        //
        // if (autoList.size() <= 0 && !"".equals(fieldName))
        // {
        // modelView.clear();
        // modelView.setViewName(this.successView);
        //
        // modelView.addObject("script", "alert(\'"
        // + ResourceBundleUtil.getString(
        // Constants.AUTO_RESOURCE_NAME,
        // "auto.alert.apply.nofound.prompt")
        // + "\');window.location.href = window.location.href;");
        // }

    }

    /**
     * 车辆详细信息新增操作
     */
    protected void onCreate(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {
        AutoInfo autoInfo = new AutoInfo();
        // autoInfo.setAutoId(this.autoManager.getMaxAutoNo().toString());
        autoInfo.setAutoDate(new Date());
        autoInfo.setAutoStatus(new Integer(0));

        User curUser = CurrentUser.get();
        autoInfo.setAutoManager(new Long(curUser.getId()));

        String managerName = curUser.getName();
        modelView.addObject("managerName", managerName);

        // 取得车辆类别设置
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.auto_type+"", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

        // 取得管理员列表

        List managerList = this.officeCommonManager.getModelManagers(1, curUser);
        V3xOrgAccount accountById = orgManager.getAccountById(curUser.getAccountId());
        Long accountid = curUser.getLoginAccount();
        // 取得不重复的管理员列表
        Collection col = new HashSet();
        Iterator it = managerList.iterator();
        while (it.hasNext())
        {   Object next = it.next();
        	if (next == null) {
        		continue;
        	}
        	V3xOrgMember member = (V3xOrgMember) next;
        	if (accountid.equals(member.getOrgAccountId())) {
        		col.add(next);
        	} else {
             	List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(member.getId());
            	if (concurrentAccounts != null && concurrentAccounts.size() > 0) {
            		for (V3xOrgAccount account : concurrentAccounts) {
            			if (account.getId().equals(accountid)) {
            				col.add(next);
            				break;
            			}
            		}
            	}
        	}
        }
        modelView.addObject("managerList", col);
        modelView.addObject("bean", autoInfo);
        modelView.addObject("actionType", "create");
    }

    /**
     * 显示车辆详细信息
     */
    protected void onShow(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String autoId = request.getParameter("autoId");

        // 根据车辆编号从DB取得车辆详细信息
        AutoInfo autoInfo = autoManager.getAutoInfoById(autoId);

        // 取得驾驶员姓名
        if (autoInfo.getAutoDriver() != null)
        {
            V3xOrgMember driverMember = this.orgManager.getMemberById(autoInfo.getAutoDriver());

            String driverName = "";
            if (driverMember != null)
            {
                driverName = driverMember.getName();
            }
            modelView.addObject("driverName", driverName);
        }

        // 取得管理员姓名
        if (autoInfo.getAutoManager() != null)
        {
            V3xOrgMember manageMember = this.orgManager.getMemberById(autoInfo.getAutoManager());

            String managerName = "";
            if (manageMember != null)
            {
                managerName = manageMember.getName();
            }
            modelView.addObject("managerName", managerName);
        }

        // 保存对象到视图模型中
        modelView.addObject("bean", autoInfo);

    }

    /**
     * 车辆详细信息修改操作
     */
    protected void onEdit(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String autoId = request.getParameter("autoId");

        // 根据车辆编号从DB取得车辆详细信息
        AutoInfo autoInfo = autoManager.getAutoInfoById(autoId);

        // 取得驾驶员姓名
        if (autoInfo.getAutoDriver() != null)
        {
            V3xOrgMember driverMember = this.orgManager.getMemberById(autoInfo.getAutoDriver());

            String driverName = "";
            if (driverMember != null)
            {
                driverName = driverMember.getName();
            }
            modelView.addObject("driverName", driverName);
        }

        // 取得管理员姓名
        if (autoInfo.getAutoManager() != null)
        {
            V3xOrgMember manageMember = this.orgManager.getMemberById(autoInfo.getAutoManager());

            String managerName = "";
            if (manageMember != null)
            {
                managerName = manageMember.getName();
            }
            modelView.addObject("managerName", managerName);
        }

        // 保存对象到视图模型中
        modelView.addObject("bean", autoInfo);
        modelView.addObject("actionType", "update");

        // 取得车辆类别设置
        User curUser = CurrentUser.get();
        List typeList = this.officeCommonManager.getModelTypes("1", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

        // 取得管理员列表
        List managerList = this.officeCommonManager.getModelManagers(1, curUser);

        // 取得不重复的管理员列表
        Long accountid = curUser.getLoginAccount();
        Collection col = new HashSet();
        Iterator it = managerList.iterator();
        while (it.hasNext())
        {   Object next = it.next();
        	if (next == null) {
        		continue;
        	}
        	V3xOrgMember member = (V3xOrgMember) next;
        	if (accountid.equals(member.getOrgAccountId())) {
        		col.add(next);
        	} else {
             	List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(member.getId());
            	if (concurrentAccounts != null && concurrentAccounts.size() > 0) {
            		for (V3xOrgAccount account : concurrentAccounts) {
            			if (account.getId().equals(accountid)) {
            				col.add(next);
            				break;
            			}
            		}
            	}
        	}
        }
        modelView.addObject("managerList", col);
    }

    public ModelAndView removeSelected(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelView = new ModelAndView(listView);

        return removeSelected(request, response, modelView);
    }

    /**
     * 从列表中选择多个车辆，进行删除操作
     */
    protected ModelAndView removeSelected(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String autoIds = request.getParameter("autoIds"); // 车辆编号集 格式
        // 1000,1111
        StringBuffer script = new StringBuffer();
        if (autoIds == null || "".equals(autoIds))
        {
            // 如果没做选择，不作处理
        }
        else
        {
        	StringTokenizer st = new StringTokenizer(autoIds,",");
        	List<String> newAutoId = new ArrayList<String>();
        	StringBuffer alertString= new StringBuffer();//不能删除的 车辆
        	while(st.hasMoreTokens()){
        		String tokenizer = st.nextToken();
        		//如果存在没有审批的项目 不允许删除
        		if(this.autoManager.hasAutoApplyByAutoId(tokenizer)){
        			if(alertString.length()==0){
        				alertString.append(tokenizer);
        			}else{
        				alertString.append(","+tokenizer);
        			}
        		}else{
        			newAutoId.add(tokenizer);
        		}
        	}
        	// 对应车牌号集的删除标识改为1
        	if(newAutoId.size() != 0)
        		autoManager.removeAutoInfoByIds(newAutoId);
        	if(alertString.length() !=0){
        		script.append("alert('"+ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.hassomething",alertString.toString())+"');\n");
        	}
        }
        if(script.length() == 0)
        	script.append("alert('"+ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.depart.success")+"');\n");
        script.append("parent.list.location.href=parent.list.tempUrl;");
        modelView.addObject("script", script.toString());
        return modelView;

    }

    /**
     * 车辆详细信息新增、修改信息保存操作
     */
    protected void onSave(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView, boolean arg3) throws Exception
    {

        // 取得参数
        String actionType = request.getParameter("actionType"); // 操作类型
        // create:新增
        // update:修改
        actionType = actionType.trim();
        String oldautoId = request.getParameter("oldautoId"); // 旧车牌号
        String newautoId = request.getParameter("autoId"); // 新车牌号
        String autoName = request.getParameter("autoName"); // 车辆品牌
        String autoModel = request.getParameter("autoModel"); // 车辆型号
        String autoType = request.getParameter("autoType"); // 车辆类型
        //edit by bianteng for alert info of deleted auto class start 
        //取得车辆类别设置
        User curUser = CurrentUser.get();
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.auto_type+"", curUser
                .getLoginAccount());
        boolean isExist=false;
        
        for(Object o : typeList){
        	OfficeTypeInfo typeInfo = (OfficeTypeInfo) o;
        	if(autoType.equals(typeInfo.getTypeId()+"")){
        		isExist = true;
        		break;
        	}
        }
        if(!isExist){
        	 StringBuffer sb = new StringBuffer();
             sb.append("alert(\'"
                     + ResourceBundleUtil.getString(
                             Constants.AUTO_RESOURCE_NAME,
                             "auto.alert.autotype.noexist") + "\');\n");
             sb.append("parent.list.location.href=parent.list.tempUrl;\n");
             modelView.addObject("script", sb.toString());
        	return;
        }
        //end
        String autoEngine = request.getParameter("autoEngine"); // 发动机号
        String autoCode = request.getParameter("autoCode"); // 车辆识别号
        String autoStatus = request.getParameter("autoStatus"); // 车辆状态
        // 0：正常（默认）；1：检修中；2：扣押中；3：报废
        String autoDate = request.getParameter("autoDate"); // 购车日期
        String autoPrice = request.getParameter("autoPrice"); // 购车价格
        if (Strings.isBlank(autoPrice))
        {
            autoPrice = "0";
        }
        String autoDriver = request.getParameter("autoDriver"); // 默认司机

        String autoInsurer = request.getParameter("autoInsurer"); // 保险公司
        String autoInsurNo = request.getParameter("autoInsurNo"); // 保险证号
        String autoInsurDate = request.getParameter("autoInsurDate"); // 保险期间
        String autoInsurDetail = request.getParameter("autoInsurDetail"); // 保险内容
        String autoMemo = request.getParameter("autoMemo"); // 备注

        String autoManagerId = request.getParameter("autoManager"); // 管理员

        AutoInfo autoInfo = null;
        List<String> list = new ArrayList<String>();
        list.add(oldautoId);
        if ("create".equals(actionType))
        {

            autoInfo = new AutoInfo();
            autoInfo.setAutoState(new Integer(0));
            autoInfo.setDeleteFlag(new Integer(0));
            autoInfo.setCreateDate(new Date());
        }
        else
        {
            // 修改记录
            autoInfo = autoManager.getAutoInfoById(oldautoId);
            if(!oldautoId.equals(newautoId)){
            	int oldAutoState=autoInfo.getAutoState();
            	int oldDeleteFlag=autoInfo.getDeleteFlag();
            	Date oldCreateDate=autoInfo.getCreateDate();
            	autoManager.removeAutoInfoByIds(list);
            	autoInfo = new AutoInfo();
            	autoInfo.setAutoState(oldAutoState);
                autoInfo.setDeleteFlag(oldDeleteFlag);
                autoInfo.setCreateDate(oldCreateDate);
            }
            autoInfo.setModifyDate(new Date());

        }
        autoInfo.setAutoId(newautoId.trim());
        autoInfo.setAutoName(autoName);
        autoInfo.setAutoModel(autoModel);
//        autoInfo.setAutoType(autoType);
        autoInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils.getLongParameter(request, "autoType")));
        autoInfo.setAutoEngine(autoEngine);
        autoInfo.setAutoCode(autoCode);
        autoInfo.setAutoStatus(new Integer(autoStatus));
        autoInfo.setAutoDate(Datetimes.parseDate(autoDate));

        autoInfo.setAutoPrice(new Float(autoPrice));
        autoInfo.setAutoDriver(new Long(autoDriver));

        autoInfo.setAutoInsurer(autoInsurer);
        autoInfo.setAutoInsurNo(autoInsurNo);
        autoInfo.setAutoInsurDate(Datetimes.parseDate(autoInsurDate));
        autoInfo.setAutoMemo(autoMemo);
        autoInfo.setAutoInsurDetail(autoInsurDetail);

        autoInfo.setAutoManager(new Long(autoManagerId));
        autoInfo.setDomainId(CurrentUser.get().getLoginAccount());
        // 新加部门/ 数据是填写的这个车辆管理员的部门ID 。而不是当前登录着的部门ID
        if (autoManagerId != null && !autoManagerId.equals(""))
        {
            Long mgeId = Long.parseLong(autoManagerId);
            Long autoDept = orgManager.getMemberById(mgeId).getOrgDepartmentId();
            autoInfo.setAutoDept(autoDept);
        }

        if ("create".equals(actionType))
        {
            // 新增记录
            if (this.autoManager.getAutoInfoById(autoInfo.getAutoId()) != null)
            {
                StringBuffer sb = new StringBuffer();
                sb.append("alert(\'"
                        + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                                "auto.alert.auto.info.exit") + "\');\n");
                sb.append("window.history.back(-1);\n");

                modelView.addObject("script", sb.toString());
                return;
            }
            autoInfo = autoManager.createAutoInfo(autoInfo);
            // System.out.println("create");
        }
        else
        {
            // 修改记录
            // System.out.println("update");
            if(!oldautoId.equals(newautoId)){
            	autoInfo = autoManager.createAutoInfo(autoInfo);
            }
            else
            autoInfo = autoManager.updateAutoInfo(autoInfo);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("alert(\'"
                + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                        "auto.alert.depart.success") + "\');\n");
        sb.append("parent.list.location.href=parent.list.tempUrl;\n");
        // sb.append("parent.detail.location.href=\"autoInfo.do?method=edit&autoId="+autoInfo.getAutoId()+"\";");

        modelView.addObject("script", sb.toString());

    }

    private <T> List<T> pagenate(List<T> list)
    {
        if (null == list || list.size() == 0)
            return new ArrayList<T>();
        Integer first = Pagination.getFirstResult();
        Integer pageSize = Pagination.getMaxResults();
        Pagination.setRowCount(list.size());
        log.debug("first: " + first + ", pageSize: " + pageSize + ", size: " + list.size());
        List<T> subList = null;
        if (first + pageSize > list.size())
        {
            subList = list.subList(first, list.size());
        }
        else
        {
            subList = list.subList(first, first + pageSize);
        }
        return subList;
    }

}
