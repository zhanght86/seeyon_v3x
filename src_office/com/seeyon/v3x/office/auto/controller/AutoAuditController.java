package com.seeyon.v3x.office.auto.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.auto.util.Constants;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 车辆审批相关操作控制类
 * 
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @author lindx
 * @version 2008-04-11
 */

public class AutoAuditController extends BaseManageController
{
	private static final Log log = LogFactory.getLog(AutoAuditController.class);

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    private UserMessageManager userMessageManager;
    
    private AdminManager adminManager;

	public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
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
                                "auto.alert.audit.mgr.prompt") + "\");");
            }
            else
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(
                                Constants.AUTO_RESOURCE_NAME,
                                "auto.alert.audit.org.prompt") + "\");");
            }
            return modelView;
        }
        ModelAndView mav = new ModelAndView("office/auto/autoAuditFrame");
        return mav;
    }

    /**
     * 查询条件”检索表“申请单一览表”和“车辆详细信息表” 结果显示在申请列表中
     */
    @SuppressWarnings("unchecked")
	protected void onQuery(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {
        String fieldName = request.getParameter("condition"); // 查询字段名
        String fieldValue = request.getParameter("textfield"); // 查询字段值
        // 取得当前登录用户
        Long autoManagerId = CurrentUser.get().getId();
        // 取得待审批的车辆申请列表
        List auditList = this.autoManager.getAutoAuditList(fieldName,fieldValue, autoManagerId);
        Long accountId = CurrentUser.get().getLoginAccount();
        List<Long> departmentId = this.adminManager.getAdminManageDepartments(autoManagerId, accountId, "1____");
        // 保存结果到视图模型中
        modelView.addObject("list", auditList);
        Map<String,String> departmentNameList = new HashMap<String,String>();
        Map<String,Boolean> depProxy = new HashMap<String,Boolean>();
        for(Object list : auditList){
        	Object[] o = (Object[]) list;
        	V3xOrgMember member = (V3xOrgMember)o[2];
        	Object[] depproxy  = this.adminManager.getMemberDepProxy(member, CurrentUser.get().getLoginAccount(), autoManagerId, "1____",departmentId);
        	departmentNameList.put(member.getId().toString(), depproxy[0].toString());
        	depProxy.put(member.getId().toString(), (Boolean)depproxy[1]);
        }
        modelView.addObject("departName",departmentNameList);
        //兼职或者副职
        modelView.addObject("depProxy",depProxy);
    }

    /**
     * 显示车辆申请的详细信息
     */
    protected void onShow(HttpServletRequest request, HttpServletResponse arg1,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String applyId = request.getParameter("applyId"); // 取得申请编号

        // 根据车辆申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(
                applyId));
        
        if (officeApply != null && officeApply.getDeleteFlag() == 0)
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

        }else{
        	StringBuffer sb = new StringBuffer();
        	sb.append("alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
            "book.alert.delete.apply")+"');\n");
        	 sb.append("parent.list.location.href=parent.list.tempUrl;\n");
        	super.rendJavaScript(arg1,  sb.toString());
        }

        // 格努车辆申请编号从“车辆管理申请单详细信息表”取得申请单详细信息
        AutoApplyInfo autoApplyInfo = this.autoManager.getAutoApply(officeApply
                .getApplyId());

        // 根据车辆申请单详细信息的车辆编号从“车辆详细信息表”取得车辆详细信息
        AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoApplyInfo
                .getAutoId());

        // 保存结果到视图模型中

        modelView.addObject("autoInfo", autoInfo); // 保存车辆详细信息
        modelView.addObject("autoApplyInfo", autoApplyInfo); // 保存车辆申请单详细信息
        modelView.addObject("officeApply", officeApply); // 保存申请单情报

    }

    protected void onEdit(HttpServletRequest request,
            HttpServletResponse response, ModelAndView modelView)
            throws Exception
    {

        // 取得参数
        String applyId = request.getParameter("applyId"); // 取得申请编号

        // 根据车辆申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));

        if (officeApply != null && officeApply.getDeleteFlag() == 0)
        {
            V3xOrgMember applyMember = this.orgManager.getMemberById(officeApply.getApplyUserName());

            modelView.addObject("applyUserName", applyMember);
            Long accountId = CurrentUser.get().getLoginAccount();
            Long adminId = CurrentUser.get().getId();
            List<Long> departmentId = this.adminManager.getAdminManageDepartments(adminId, accountId, "1____");
            Object[] o = this.adminManager.getMemberDepProxy(applyMember, accountId, CurrentUser.get().getId(), "1____",departmentId);
            modelView.addObject("applyUserDepartName", o[0]);
            modelView.addObject("proxy", o[1]);
        }else{
        	StringBuffer sb = new StringBuffer();
        	sb.append("alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
            "book.alert.delete.app")+"');\n");
        	 sb.append("window.close();\n");
        	super.rendJavaScript(response,  sb.toString());
        }

        // 格努车辆申请编号从“车辆管理申请单详细信息表”取得申请单详细信息
        AutoApplyInfo autoApplyInfo = this.autoManager.getAutoApply(officeApply.getApplyId());

        // 根据车辆申请单详细信息的车辆编号从“车辆详细信息表”取得车辆详细信息
        AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoApplyInfo.getAutoId());

        // 保存结果到视图模型中

        modelView.addObject("autoInfo", autoInfo); // 保存车辆详细信息
        modelView.addObject("autoApplyInfo", autoApplyInfo); // 保存车辆申请单详细信息
        modelView.addObject("officeApply", officeApply); // 保存申请单情报

    }

	/**
	 * 审批车辆
	 */
	protected void onSave(HttpServletRequest request, HttpServletResponse arg1, ModelAndView modelView, boolean arg3) throws Exception {
		boolean fromPortal = "portal".equals(request.getParameter("from"));

		String applyId = request.getParameter("applyId");
		String applyState = request.getParameter("applyState");
		String applyMemo = request.getParameter("applyMemo");
		String autoId = request.getParameter("autoId");
		String isDepart = request.getParameter("isDepart");
		if (isDepart == null) {
			isDepart = "0"; // 默认为不出车
		}
		User curUser = CurrentUser.get();
		Long managerId = new Long(curUser.getId());
		StringBuffer sb = new StringBuffer();
		// 判断有没有在相同的时间内审批成功的车辆
		if (!this.autoManager.hasSameTimeAutoApply(Long.parseLong(applyId), autoId) && "2".equals(applyState)) {
			sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.audit.sametime", autoId) + "\');\n");
		} else {
			AutoInfo autoInfo = this.autoManager.getAutoInfoById(autoId);
			// 添加车辆已经报废或丢失时不能再审核通过
			if (autoInfo != null) {
				if (autoInfo.getAutoStatus() == 3) {
					sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.audit.nocard", autoId) + "\');\n");
					applyState = "3";
				}
			} else {
				sb.append("alert(\'" + ResourceBundleUtil.getString(com.seeyon.v3x.office.myapply.util.Constants.MYAPPLY_RESOURCE_NAME, "alert.auto.deleted", autoId) + "\');\n");
				if (fromPortal) {
					sb.append("parent.window.returnValue = \"true\";\n");
					sb.append("parent.window.close();\n");
				} else {
					sb.append("parent.list.location.href = parent.list.tempUrl;\n");
				}
				modelView.addObject("script", sb.toString());
				return;
			}

			OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));
			if (officeApply.getDeleteFlag() != 0) {
				// 已经删除
				sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.audit.error") + "\');\n");
			} else {
				officeApply.setApplyId(new Long(applyId));
				officeApply.setApplyState(new Integer(applyState));
				officeApply.setApplyMemo(applyMemo);
				officeApply.setAuditTime(new Date());
				officeApply.setApplyExam(managerId);
				this.autoManager.auditAutoApply(officeApply);
				// 删除审批人待办
				OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, officeApply.getApplyId());
				if ("2".equals(applyState)) {
					sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.audit.success", autoId) + "\');\n");
					AutoDepartInfo departInfo = new AutoDepartInfo();
					departInfo.setApplyId(officeApply.getApplyId());
					departInfo.setAutoId(autoId);
					departInfo.setDeleteFlag(new Integer(0));
					departInfo.setAutoDriver(autoInfo.getAutoDriver());
					// 如果选择出车
					if ("1".equals(isDepart)) {
						// 做出是否出车的判断2008-04-30 by yongzhang
						if (autoInfo.getAutoState() == 1) {
							sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.autodepart.fail", autoId) + "\');\n");
							departInfo.setAutoDepartTime(null);
						} else {
							// 出车处理
							departInfo.setAutoDepartTime(Datetimes.formatDatetimeWithoutSecond(new Date()));
							// 变更车辆信息为已安排
							autoInfo.setAutoState(new Integer(1));
							this.autoManager.updateAutoInfo(autoInfo);
						}
					}
					this.autoManager.createDepartAutoInfo(departInfo);

					List<Long> auth = new ArrayList<Long>();
					auth.add(officeApply.getApplyUserName());
					Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
					try {
						userMessageManager.sendSystemMessage(MessageContent.get("office.car.audit", autoId, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
					} catch (MessageException e) {
						log.error("", e);
					}
				} else {
					sb.append("alert(\'" + ResourceBundleUtil.getString(Constants.AUTO_RESOURCE_NAME, "auto.alert.audit.fail", autoId) + "\');\n");
					List<Long> auth = new ArrayList<Long>();
					auth.add(officeApply.getApplyUserName());
					Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
					try {
						userMessageManager.sendSystemMessage(MessageContent.get("office.car.NoAudit", autoId, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
					} catch (MessageException e) {
						log.error("", e);
					}
				}
			}
		}

		if (fromPortal) {
			sb.append("parent.window.returnValue = \"true\";\n");
			sb.append("parent.window.close();\n");
		} else {
			sb.append("parent.list.location.href = parent.list.tempUrl;\n");
		}
		modelView.addObject("script", sb.toString());
	}

    /**
     * 删除多个选择的车辆申请操作
     */
    protected void onRemoveSelected(HttpServletRequest request,
            HttpServletResponse arg1, ModelAndView modelView) throws Exception
    {

        // 取得参数
        String applyIds = request.getParameter("applyIds"); // 选中的车辆申请编号集

        String sb = "";
        String sb1="";
        String[] applyArray = StringUtils.split(applyIds, ",");
        for (int i = 0; i < applyArray.length; i++)
        {
            OfficeApply officeApply = autoManager.getOfficeApplyById(new Long(applyArray[i]));
            if (officeApply != null)
            {
                if (officeApply.getApplyState() == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait
                        && officeApply.getApplyUserName() != CurrentUser.get().getId())
                {
                    sb="alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
                                    "book.alert.delete.apply")+"');";
                }
                //判断是否已经出车 已经出车 不允许删除
                else
                {
                	AutoDepartInfo departInfo = autoManager.getAutoDepartInfo(new Long(applyArray[i]));
                	if(departInfo != null && (Strings.isNotBlank(departInfo.getAutoDepartTime()) && Strings.isBlank(departInfo.getAutoBackTime()) )){
                		sb="alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
                        "apply.auto.notback",applyArray[i])+"');";
                	}else{
                		autoManager.deleteOfficeApplyById(applyArray[i]);
                		//删除审批人待办
        		        OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, Long.valueOf(applyArray[i]));
                        sb1="alert(\'"
                                + ResourceBundleUtil.getString(
                                        Constants.AUTO_RESOURCE_NAME,
                                        "auto.delete.apply.prompt") + "\');\n";
                	}
                }
            }
        }
        modelView.addObject("script", sb+sb1+"parent.list.location.href=parent.list.tempUrl\n");
    }

	public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

}