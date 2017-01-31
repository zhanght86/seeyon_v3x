package com.seeyon.v3x.office.auto.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.auto.util.Constants;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 车辆管理员对车辆的出车/归车管理操作控制类
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-15
 */

public class AutoDepartBackController extends BaseManageController
{

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    private AdminManager adminManager;
    
    public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

	public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setAutoManager(AutoManager autoManager)
    {
        this.autoManager = autoManager;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public ModelAndView frame(HttpServletRequest request,
            HttpServletResponse response) throws Exception
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
                        + ResourceBundleUtil.getString(
                                Constants.AUTO_RESOURCE_NAME,
                                "auto.alert.depart.mgr") + "\");");
            }
            else
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(
                                Constants.AUTO_RESOURCE_NAME,
                                " auto.alert.depart.contact.mgr") + "\");");
            }
            return modelView;
        }

        ModelAndView mav = new ModelAndView("office/auto/autoDepartBackFrame");
        return mav;
    }

    /**
     * 根据“查询条件”检索表“申请单一览表”中通过审核的申请单列表 申请状态=2（审核通过）；
     */
    protected void onQuery(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
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

        Long managerId = CurrentUser.get().getId();
        Long accountId = CurrentUser.get().getAccountId();
        List auditList = this.autoManager.getAuditedApplyList(fieldName,fieldValue, managerId);
        
        List<Long> departmentId = this.adminManager.getAdminManageDepartments(managerId, accountId, "1____");
        Map<String,Boolean> depProxy = new HashMap<String,Boolean>();
        for(Object list : auditList){
        	Object[] o = (Object[]) list;
        	V3xOrgMember member = (V3xOrgMember)o[1];
        	Object[] depproxy = new Object[2];
        	depproxy = this.adminManager.getMemberDepProxy(member, CurrentUser.get().getLoginAccount(), managerId, "1____",departmentId);
        	depProxy.put(member.getId().toString(), (Boolean)depproxy[1]);
        }
        
        // 保存结果到视图模型中
        modelView.addObject("list", auditList);
        //兼职或者副职
        modelView.addObject("depProxy",depProxy);
/*        if (auditList.size() <= 0 && !"".equals(fieldName))
        {
            modelView.clear();
            modelView.setViewName(this.successView);
            modelView.addObject("script", "alert(\'"
                    + ResourceBundleUtil.getString(
                            Constants.AUTO_RESOURCE_NAME,
                            "auto.alert.apply.nofound.prompt")
                    + "\');window.history.back(-1);");
        }*/
    }

    /**
     * 根据车辆申请编号取得该申请的出车/归车信息
     */
    protected void onShow(HttpServletRequest request, HttpServletResponse arg1,
            ModelAndView modelView) throws Exception
    {

        // 参数取得
        String applyId = request.getParameter("applyId"); // 车俩申请编号

        // 根据车辆申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(
                applyId));
        if (officeApply != null)
        {
            V3xOrgMember applyMember = this.orgManager
                    .getMemberById(officeApply.getApplyUserName());
            
            modelView.addObject("applyUserName", applyMember);
            Long accountId = CurrentUser.get().getLoginAccount();
            Long adminId = CurrentUser.get().getId();
            List<Long> departmentId = this.adminManager.getAdminManageDepartments(adminId, accountId, "1____");
            Object[] object = this.adminManager.getMemberDepProxy(applyMember, accountId, adminId, "1____",departmentId);
            
            String departmentName = object[0].toString();
            boolean proxy = ((Boolean)object[1]).booleanValue();
            
            modelView.addObject("applyUserDepartName", departmentName);
            modelView.addObject("proxy", proxy);

        }

        // 格努车辆申请编号从“车辆管理申请单详细信息表”取得申请单详细信息
        AutoApplyInfo autoApplyInfo = this.autoManager.getAutoApply(new Long(
                applyId));

        // 根据车辆申请单详细信息的车辆编号从“车辆详细信息表”取得车辆详细信息
        AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoApplyInfo
                .getAutoId());

        AutoDepartInfo departInfo = this.autoManager
                .getAutoDepartInfo(officeApply.getApplyId());

        if (departInfo == null)
        {
            departInfo = new AutoDepartInfo();
        }
        else
        {
            if (departInfo.getAutoDriver() != null)
            {
                V3xOrgMember applyMember = this.orgManager
                        .getMemberById(departInfo.getAutoDriver());
                String userName = "";
                if (applyMember != null)
                {
                    userName = applyMember.getName();
                }

                modelView.addObject("autoDriverName", userName);
            }
        }

        // 保存结果到视图模型中

        modelView.addObject("autoInfo", autoInfo); // 保存车辆详细信息
        modelView.addObject("autoApplyInfo", autoApplyInfo); // 保存车辆申请单详细信息
        modelView.addObject("officeApply", officeApply); // 保存申请单情报
        modelView.addObject("departInfo", departInfo);

    }

    protected void onEdit(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {
        // 参数取得
        String applyId = request.getParameter("applyId"); // 车俩申请编号

        String opera = request.getParameter("opera");

        // 根据车辆申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(
                applyId));

        if (officeApply != null)
        {
            V3xOrgMember applyMember = this.orgManager
                    .getMemberById(officeApply.getApplyUserName());

            modelView.addObject("applyUserName", applyMember);
            Long accountId = CurrentUser.get().getLoginAccount();
            Long adminId = CurrentUser.get().getId();
            List<Long> departmentId = this.adminManager.getAdminManageDepartments(adminId, accountId, "1____");
            Object[] object = this.adminManager.getMemberDepProxy(applyMember, accountId, adminId, "1____",departmentId);
            
            String departmentName = object[0].toString();
            boolean proxy = ((Boolean)object[1]).booleanValue();
            
            modelView.addObject("applyUserDepartName", departmentName);
            modelView.addObject("proxy", proxy);

        }
        // 格努车辆申请编号从“车辆管理申请单详细信息表”取得申请单详细信息
        AutoApplyInfo autoApplyInfo = this.autoManager.getAutoApply(new Long(
                applyId));

        // 根据车辆申请单详细信息的车辆编号从“车辆详细信息表”取得车辆详细信息
        AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoApplyInfo
                .getAutoId());

        AutoDepartInfo departInfo = this.autoManager
                .getAutoDepartInfo(officeApply.getApplyId());

        if (departInfo == null)
        {
            departInfo = new AutoDepartInfo();
            departInfo.setAutoDriver(autoInfo.getAutoDriver());
            V3xOrgMember applyMember = this.orgManager.getMemberById(departInfo
                    .getAutoDriver());
            String userName = "";
            if (applyMember != null)
            {
                userName = applyMember.getName();
            }

            modelView.addObject("autoDriverName", userName);
        }
        else
        {
            if (departInfo.getAutoDriver() != null)
            {

                V3xOrgMember applyMember = this.orgManager
                        .getMemberById(departInfo.getAutoDriver());
                String userName = "";
                if (applyMember != null)
                {
                    userName = applyMember.getName();
                }

                modelView.addObject("autoDriverName", userName);
            }
        }

        // 保存结果到视图模型中

        modelView.addObject("autoInfo", autoInfo); // 保存车辆详细信息
        modelView.addObject("autoApplyInfo", autoApplyInfo); // 保存车辆申请单详细信息
        modelView.addObject("officeApply", officeApply); // 保存申请单情报
        modelView.addObject("departInfo", departInfo);
        modelView.addObject("opera", opera);

    }

    /**
     * 保存车辆出车/归车记录操作方法
     */
    protected void onSave(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView, boolean arg3)
            throws Exception
    {

        AutoDepartInfo departInfo = null;
        boolean bCreate = false;

        String applyId = request.getParameter("applyId");
        String autoId = request.getParameter("autoId");
        String opera = request.getParameter("opera");
        
        String departTime = request.getParameter("departTime");
        String backTime = request.getParameter("backTime");
        String autoDriver = request.getParameter("autoDriver");
        String autoMileAge = request.getParameter("autoMileAge");
        String autoFuel = request.getParameter("autoFuel");
        String fuelPrice = request.getParameter("fuelPrice");
        String roadPrice = request.getParameter("roadPrice");
        String otherPrice = request.getParameter("otherPrice");
        
        StringBuffer sb = new StringBuffer();
//        int auotStatus = this.autoManager.getAutoStatus(autoId);//出车/归车状态
        AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoId);
//        if(autoInfo!=null){
//            if(!"back".equals(opera))
//            {
//                if(autoInfo.getAutoState()==1){
//                	
//                    sb.append("alert(\'"
//                            + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
//                                    "auto.alert.autodepart.fail") + "\');\n");
//                    sb.append("parent.list.location.href=parent.list.tempUrl;\n");
//                    modelView.addObject("script", sb.toString());
//                    return;
//                }
//            }
//            
//        }
    
        if (!"".equals(applyId))
        {
            // departInfo.setApplyId(new Long(applyId));
            try
            {
                departInfo = this.autoManager.getAutoDepartInfo(new Long(applyId));

            }
            catch (Exception e)
            {
            }

        }

        if (departInfo == null)
        {
            bCreate = true;

            departInfo = new AutoDepartInfo();
            departInfo.setApplyId(new Long(applyId));
        }
        else
        {
            bCreate = false;
        }

        departInfo.setAutoId(autoId);

       

        departInfo.setDeleteFlag(new Integer(0));

        if ("back".equals(opera))
        {
            departInfo.setAutoBackTime(backTime.trim());

            if (autoDriver == null)
            {
                autoDriver = "";
            }

            if (!"".equals(autoDriver.trim()))
            {
                departInfo.setAutoDriver(new Long(autoDriver.trim()));
            }

            if (autoMileAge == null)
                autoMileAge = "";
            if (!"".equals(autoMileAge))
            {
                departInfo.setAutoMileAge(new Float(autoMileAge));
            }

            if (autoFuel == null)
                autoFuel = "";
            if (!"".equals(autoFuel.trim()))
            {
                departInfo.setAutoFuel(new Float(autoFuel));
            }
            if (fuelPrice == null)
                fuelPrice = "";
            if (!"".equals(fuelPrice.trim()))
            {
                departInfo.setFuelPrice(new Float(fuelPrice));
            }

            if (roadPrice == null)
                roadPrice = "";
            if (!"".equals(roadPrice))
            {
                departInfo.setRoadPrice(new Float(roadPrice));
            }

            if (otherPrice == null)
                otherPrice = "";
            if (!"".equals(otherPrice))
            {
                departInfo.setOtherPrice(new Float(otherPrice));
            }

        }
        else
        {
            departInfo.setAutoDepartTime(departTime);
        }

        if (bCreate)
        {
            this.autoManager.createDepartAutoInfo(departInfo);
        }
        else
        {
            this.autoManager.updateDepartAutoInfo(departInfo);
        }

       
        
        if (autoInfo != null)
        {
           
            if ("back".equals(opera))
            {
                // 设置为未安排

                // 判断是否全部归车，如果车辆未归应该是1,所以修改getAutoStatus返回是1
                if (autoInfo.getAutoState() == 1)
                {
                    autoInfo.setAutoState(new Integer(0));
                    this.autoManager.updateAutoInfo(autoInfo);
                }

            }
            else
            {
                    // 设置为已安排
                    autoInfo.setAutoState(new Integer(1));
                    this.autoManager.updateAutoInfo(autoInfo);
            }
            sb.append("alert(\'"
                    + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                            "auto.alert.depart.success") + "\');\n");
        }
       
        sb.append("parent.list.location.href=parent.list.tempUrl;\n");
        modelView.addObject("script", sb.toString());
    }

}
