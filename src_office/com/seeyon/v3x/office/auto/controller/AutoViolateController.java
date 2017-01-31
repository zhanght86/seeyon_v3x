package com.seeyon.v3x.office.auto.controller;

/**
 * 管理员进行车辆违章登记相关操作控制类
 */
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.auto.domain.AutoOffense;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.auto.util.Constants;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class AutoViolateController extends BaseManageController
{

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public void setAutoManager(AutoManager autoManager)
    {
        this.autoManager = autoManager;
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
        ModelAndView mav = new ModelAndView("office/auto/autoViolateFrame");
        return mav;
    }

    /**
     * 根据“查询条件”检索表“车辆违章信息表” 取得结果并显示在违章列表中
     */
    protected void onQuery(HttpServletRequest request,
            HttpServletResponse arg1, ModelAndView modelView) throws Exception
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
        User curUser = CurrentUser.get();
        Long managerId = curUser.getId();// 当前用户ID
        // 取得当前管理员的所有管理的车辆违章一览列表
        List violateList = this.autoManager.getAutoViolateList(fieldName,fieldValue, curUser.getLoginAccount());
        // 保存结果到视图模型中
        modelView.addObject("list", violateList);

/*        if (violateList.size() <= 0 && !"".equals(fieldName))
        {
            modelView.clear();
            modelView.setViewName(this.successView);

            modelView.addObject("script", "alert(\'"
                    + ResourceBundleUtil.getString(
                            Constants.AUTO_RESOURCE_NAME,
                            "auto.alert.apply.nofound.prompt")
                    + "\');window.location.href = window.location.href;");
        }*/

    }

    /**
     * 转入到违章新增界面
     */
    protected void onCreate(HttpServletRequest request,
            HttpServletResponse arg1, ModelAndView modelView) throws Exception
    {

        modelView.addObject("actionType", "create");
        AutoOffense autoOffense = new AutoOffense();
        autoOffense.setRegSituation(new Integer(0));
        User curUser = CurrentUser.get();
        autoOffense.setAutoManager(new Long(curUser.getId()));
        modelView.addObject("bean", autoOffense);
        String autoManagerName = curUser.getName();
        modelView.addObject("autoManagerName", autoManagerName);

        modelView.addObject("curMgr", autoManagerName);
        // 取得所有的车辆列表 车辆状态=0

        List autoList = this.autoManager.getAllNormalAuto(curUser.getLoginAccount());
        modelView.addObject("autoList", autoList);

    }

    /**
     * 选择一条违章记录，进入修改界面
     */
    protected void onEdit(HttpServletRequest request, HttpServletResponse arg1,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String applyId = request.getParameter("applyId"); // 车辆违章编号

        // 根据违章编号取得违章车辆详细信息
        AutoOffense autoOffense = this.autoManager.getAutoViolate(new Long(
                applyId));

        V3xOrgMember member = this.orgManager.getMemberById(autoOffense
                .getAutoManager());
        if (member != null)
        {
            String autoManagerName = member.getName();
            Long autoManagerID = member.getId();
            modelView.addObject("autoManagerName", autoManagerName);
            modelView.addObject("autoManagerID", autoManagerID);
        }
        // 保存到视图模型中
        modelView.addObject("bean", autoOffense);
        modelView.addObject("actionType", "update");

        // 取得所有的车辆列表 车辆状态=0

        List autoList = this.autoManager.getAllNormalAuto(CurrentUser.get().getLoginAccount());
        modelView.addObject("autoList", autoList);
    }

    /**
     * 选择一条违章记录，进入浏览界面
     */
    protected void onShow(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {

        // 取得参数
        String applyId = request.getParameter("applyId"); // 车辆违章编号

        // 根据违章编号取得违章车辆详细信息
        AutoOffense autoOffense = this.autoManager.getAutoViolate(new Long(
                applyId));
        V3xOrgMember member = this.orgManager.getMemberById(autoOffense
                .getAutoManager());
        if (member != null)
        {
            String autoManagerName = member.getName();
            modelView.addObject("autoManagerName", autoManagerName);
        }
        // 保存到视图模型中
        modelView.addObject("bean", autoOffense);

    }

    /**
     * 新建或修改的违章记录信息写入数据库
     */
    protected void onSave(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView, boolean arg3)
            throws Exception
    {

        String actionType = request.getParameter("actionType");

        String applyId = request.getParameter("applyId");
        String autoId = request.getParameter("autoId");
        String regAddress = request.getParameter("regAddress");
        String regBehavior = request.getParameter("regBehavior");
        String regMemo = request.getParameter("regMemo");
        String regSituation = request.getParameter("regSituation");

        String regTime = request.getParameter("regTime");
        String managerId = request.getParameter("managerId"); // 管理员
        StringBuffer sb = new StringBuffer();
        Calendar  today=Calendar.getInstance();
        today.setTime(new Date(System.currentTimeMillis()));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date time=new Date();
        
        if(StringUtils.isNotBlank(regTime))
        {
            time=Datetimes.parseDate(regTime);
        }
        if(today.getTimeInMillis()<time.getTime())
        {
            sb.append("alert(\'"
                    + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                    "violate.alert.time") + "\');\n");
        }
        else
        {
            AutoOffense autoOffense = null;
            if ("create".equals(actionType))
            {
                // 新增操作
                autoOffense = new AutoOffense();
                autoOffense.setApplyId(this.autoManager.getMaxAutoLossNo());
                autoOffense.setAutoId(autoId);
                autoOffense.setAutoManager(new Long(managerId));
                autoOffense.setCreateDate(new Date());
                autoOffense.setDeleteFlag(new Integer(0));
                autoOffense.setRegAddress(regAddress);
                autoOffense.setRegBehavior(regBehavior);
                autoOffense.setRegMemo(regMemo);
                autoOffense.setRegSituation(new Integer(regSituation));
                autoOffense.setRegTime(regTime);
                this.autoManager.createAutoViolate(autoOffense);
                
            }
            else
            {
                // 修改操作
                autoOffense = this.autoManager.getAutoViolate(new Long(applyId));
                
                autoOffense.setAutoId(autoId);
                autoOffense.setAutoManager(new Long(managerId));
                autoOffense.setUpdateDate(new Date());
                autoOffense.setDeleteFlag(new Integer(0));
                autoOffense.setRegAddress(regAddress);
                autoOffense.setRegBehavior(regBehavior);
                autoOffense.setRegMemo(regMemo);
                autoOffense.setRegSituation(new Integer(regSituation));
                autoOffense.setRegTime(regTime);
                this.autoManager.updateAutoViolate(autoOffense);
            }
            
           
            sb.append("alert(\'"
                    + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                    "auto.alert.depart.success") + "\');\n");
            // sb.append("parent.detail.location.href=\"autoViolate.do?method=edit&applyId="+autoOffense.getApplyId()+"\";");
            
        }
        sb.append("parent.list.location.href=parent.list.tempUrl;\n");
        modelView.addObject("script", sb.toString());
    }

    /**
     * 选择多个违章记录,从数据库中删除
     */
    protected void onRemoveSelected(HttpServletRequest request,
            HttpServletResponse arg1, ModelAndView modelView) throws Exception
    {

        // 取得参数
        String applyIds = request.getParameter("applyIds"); // 车辆违章编号集
        if (applyIds == null || "".equals(applyIds))
        {
            // 不做操作
        }
        else
        {
            this.autoManager.removeAutoViolateByIds(applyIds);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("alert(\'"
                + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME,
                        "auto.alert.delete.success") + "\');\n");
        sb.append("parent.list.location.reload();\n");

        modelView.addObject("script", sb.toString());
    }

}
