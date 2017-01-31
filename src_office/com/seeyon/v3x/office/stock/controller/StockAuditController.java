package com.seeyon.v3x.office.stock.controller;

/**
 * 办公用品申请审核操作类
 * 
 */
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
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class StockAuditController extends BaseManageController
{

	private static final Log log = LogFactory.getLog(StockAuditController.class);
	
    private StockManager stockManager; // 办公用品管理类

    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    private UserMessageManager userMessageManager;
    
    private AdminManager adminManager;
    

    public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
    }

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setStockManager(StockManager stockManager)
    {
        this.stockManager = stockManager;
    }

    public void setAutoManager(AutoManager autoManager)
    {
        this.autoManager = autoManager;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public ModelAndView frame(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelView = null;

        User user = CurrentUser.get();

        int iResult = this.officeCommonManager.checkAdminModel(4, user);

        if (iResult > 0)
        {
            modelView = new ModelAndView(this.successView);

            if (iResult == 1)
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(
                                com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
                                "stock.alert.mgr.nofound") + "\");");
            }
            else
            {
                modelView.addObject("script", "alert(\""
                        + ResourceBundleUtil.getString(
                                com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
                                "stock.alert.mgr.nopopedom") + "\");");
            }
            return modelView;
        }
        ModelAndView mav = new ModelAndView("office/stock/stockAuditFrame");
        return mav;
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
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
        // 管理者
        User curUser = CurrentUser.get();
        Long managerId = curUser.getId();
        List<Long> departId = this.adminManager.getAdminManageDepartments(managerId, curUser.getAccountId(), "___1_");
        
        List applyList = this.stockManager.getStockApplyListForAutdit(fieldName, fieldValue,managerId);
        Map<String,String> departmentNameList = new HashMap<String,String>();
        Map<String,Boolean> depProxy = new HashMap<String,Boolean>();
        for(Object list : applyList){
        	Object[] o = (Object[]) list;
        	V3xOrgMember member = (V3xOrgMember)o[4];
        	Object[] depproxy = this.adminManager.getMemberDepProxy(member, curUser.getAccountId(), managerId, "___1_", departId);
        	departmentNameList.put(member.getId().toString(), depproxy[0].toString());
        	depProxy.put(member.getId().toString(), (Boolean)depproxy[1]);
        }
        // 保存结果到视图模型中
        modelView.addObject("list", applyList);
        modelView.addObject("proxy", depProxy);
        modelView.addObject("departName", departmentNameList);
    }

    /**
     * 进入审核界面
     */
    protected void onEdit(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {
        // 取得参数
        String applyId = request.getParameter("applyId"); // 申请编号

        // 根据申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));

        if (officeApply != null  &&  officeApply.getDeleteFlag() == 0)
        {
            V3xOrgMember applyMember = this.orgManager
                    .getMemberById(officeApply.getApplyUserName());

            modelView.addObject("applyUserName", applyMember);

            User user = CurrentUser.get();
            List<Long> departId = this.adminManager.getAdminManageDepartments(user.getId(),user.getAccountId(), "___1_");
            Object[] o = this.adminManager.getMemberDepProxy(applyMember, user.getLoginAccount(), user.getId(), "____1_", departId);
            
            modelView.addObject("applyUserDepartName", o[0].toString());
            modelView.addObject("proxy", o[1]);

        }else{
        	StringBuffer sb = new StringBuffer();
        	sb.append("alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
            "book.alert.delete.app")+"');\n");
        	 sb.append("window.close();\n");
        	super.rendJavaScript(response,  sb.toString());
        }
        // 根据申请编号从“办公用品详细申请单一览表”取得申请单情报

        StockApplyInfo stockApply = this.stockManager.getStockApplyById(officeApply.getApplyId());

        // 根据办公用品编号取得办公用品品详细信息

        StockInfo stockInfo = this.stockManager.getStockInfoById(stockApply.getStockId());

        modelView.addObject("officeApply", officeApply);
        modelView.addObject("stockApply", stockApply);
        modelView.addObject("stockInfo", stockInfo);

    }

    protected void onShow(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {
        // 取得参数
        String applyId = request.getParameter("applyId"); // 申请编号

        // 根据申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));

        if (officeApply != null)
        {
            V3xOrgMember applyMember = this.orgManager
                    .getMemberById(officeApply.getApplyUserName());
            modelView.addObject("applyUserName", applyMember);
            User user = CurrentUser.get();
            List<Long> departId = this.adminManager.getAdminManageDepartments(user.getId(),user.getAccountId(), "___1_");
            Object[] o = this.adminManager.getMemberDepProxy(applyMember, user.getLoginAccount(), user.getId(), "____1_", departId);
            
            modelView.addObject("applyUserDepartName", o[0].toString());
            modelView.addObject("proxy", o[1]);
        }

        // 根据申请编号从“办公用品详细申请单一览表”取得申请单情报

        StockApplyInfo stockApply = this.stockManager.getStockApplyById(officeApply.getApplyId());

        // 根据办公用品编号取得办公用品品详细信息

        StockInfo stockInfo = this.stockManager.getStockInfoById(stockApply.getStockId());

        modelView.addObject("officeApply", officeApply);
        modelView.addObject("stockApply", stockApply);
        modelView.addObject("stockInfo", stockInfo);

    }

	/**
	 * 审批办公用品
	 */
	protected void onSave(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView, boolean arg3) throws Exception {
		boolean fromPortal = "portal".equals(request.getParameter("from"));
		
		String applyId = request.getParameter("applyId");
		String applyState = request.getParameter("applyState");
		String applyMemo = request.getParameter("applyMemo");
		String stockName = request.getParameter("stockName");
		User curUser = CurrentUser.get();
		Long managerId = curUser.getId();

		OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));

		// 如果审核通过，扣除办公用品的可申请数量，通过此验证就ispass=true
		boolean ispass = false;
		String ispassFlag = null;
		if ("2".equals(applyState)) {
			StockApplyInfo stockApply = this.stockManager.getStockApplyById(officeApply.getApplyId());
			StockInfo stockInfo = this.stockManager.getStockInfoById(stockApply.getStockId());
			// 可申请数量要大于申请数量
			if (stockInfo != null && stockApply != null) {
				int avaCount = stockInfo.getStockAvacount().intValue();
				int appCount = stockApply.getApplyCount().intValue();
				int result = avaCount - appCount;
				if (result >= 0) {
					stockInfo.setStockAvacount(new Integer(result));
					this.stockManager.updateStockInfo(stockInfo);
					ispass = true;
				} else {
					applyState = "3";
					ispassFlag = "no";
				}
			}
		}
		if (officeApply != null) {
			officeApply.setApplyState(new Integer(applyState));
			officeApply.setApplyMemo(applyMemo);
			officeApply.setAuditTime(new Date());
			officeApply.setApplyExam(managerId);
			this.autoManager.auditAutoApply(officeApply);
			// 删除审批人待办
			OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, officeApply.getApplyId());
		}
		StringBuffer sb = new StringBuffer();
		if ("2".equals(applyState) && ispass) {
			sb.append("alert(\'" + ResourceBundleUtil.getString(com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME, "stock.alert.apply.pass", stockName) + "\');\n");
			List<Long> auth = new ArrayList<Long>();
			auth.add(officeApply.getApplyUserName());
			Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("office.work.Audit", stockName, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
			} catch (MessageException e) {
				log.error("", e);
			}
		} else if ("3".equals(applyState) && "no".equals(ispassFlag)) {
			sb.append("alert(\'" + ResourceBundleUtil.getString(com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME, "stock.alert.nopass.lack", stockName) + "\');\n");
			List<Long> auth = new ArrayList<Long>();
			auth.add(officeApply.getApplyUserName());
			Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("office.work.NoAudit", stockName, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
			} catch (MessageException e) {
				log.error("", e);
			}
		} else if ("2".equals(applyState) && !ispass) {
			sb.append("alert(\'" + ResourceBundleUtil.getString(com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME, "stock.alert.apply.nopass", stockName) + "\');\n");
			List<Long> auth = new ArrayList<Long>();
			auth.add(officeApply.getApplyUserName());
			Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("office.work.NoAudit", stockName, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
			} catch (MessageException e) {
				log.error("", e);
			}
		} else if ("3".equals(applyState) && null == ispassFlag) {
			sb.append("alert(\'" + ResourceBundleUtil.getString(com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME, "stock.alert.apply.nopass", stockName) + "\');\n");
			List<Long> auth = new ArrayList<Long>();
			auth.add(officeApply.getApplyUserName());
			Collection<MessageReceiver> receivers = MessageReceiver.get(new Long(applyId), auth);
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("office.work.NoAudit", stockName, curUser.getName()), ApplicationCategoryEnum.office, curUser.getId(), receivers);
			} catch (MessageException e) {
				log.error("", e);
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
     * 删除多个选择的办公用品审核操作
     */
    protected void onRemoveSelected(HttpServletRequest request, HttpServletResponse arg1,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String applyIds = request.getParameter("applyIds");
        String sb = "";
        String sb1="";
        String[] applyArray = StringUtils.split(applyIds, ",");
        for (int i = 0; i < applyArray.length; i++)
        {
            OfficeApply officeApply = stockManager.getOfficeApplyById(new Long(applyArray[i]));
            if (officeApply != null)
            {
                if (officeApply.getApplyState() == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait
                        && officeApply.getApplyUserName() != CurrentUser.get().getId())
                {
                    sb="alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
                                    "book.alert.delete.apply")+"');";
                }
                else
                {
                    stockManager.deleteStockApplyById(applyArray[i]);
                    //删除审批人待办
    		        OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, Long.valueOf(applyArray[i]));
                    sb1="alert(\'"
                            + ResourceBundleUtil.getString(
                                    com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
                                    "stock.alert.delete.success") + "\');\n";
                }
            }
        }
     
        modelView.addObject("script", sb+sb1+"parent.list.location.reload();\n");
    }
}