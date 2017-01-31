package com.seeyon.v3x.office.stock.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.office.stock.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 办公用品申请相关操作类
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * 
 */
public class StockApplyController extends BaseManageController
{

    private StockManager stockManager; // 办公用品管理类

    private OrgManager orgManager;

    private UserMessageManager userMessageManager;
    
    private OfficeCommonManager officeCommonManager;

    public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
    }

    public void setStockManager(StockManager stockManager)
    {
        this.stockManager = stockManager;
    }

    public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }
    
	public void setOfficeCommonManager(OfficeCommonManager officeCommonManager) {
		this.officeCommonManager = officeCommonManager;
	}

    public ModelAndView frame(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("office/stock/stockApplyFrame");
        return mav;
    }

    /**
     * 办公用品详细信息查询列表操作
     */
    protected void onQuery(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {
        User curUser = CurrentUser.get();
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
        Long managerId = new Long(-1);

        List list = this.stockManager.getStockInfoApplyList(fieldName, fieldValue, managerId);

        // 保存结果到视图模型中
        modelView.addObject("list", list);
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.stock_type+"", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

    }

    /**
     * 办公用品申请
     */
    protected void onEdit(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得办公用品编号
         String stockId = request.getParameter("stockId"); // 办公用品编号

        // 根据办公用品编号取得办公用品详细信息
        StockInfo stockInfo = stockManager.getStockInfoById(new Long(stockId));
       /**   
        if(stockInfo.getDeleteFlag().intValue() == 1) {
        	return  ;
        }
          **/
        V3xOrgMember member = this.orgManager.getMemberById(stockInfo.getStockRes());
        String stockResName = "";
        if (member != null)
        {
            stockResName = member.getName();
        }
        // 保存到视图模型中
        modelView.addObject("bean", stockInfo);
        modelView.addObject("stockResName", stockResName);
    }

    protected void onShow(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得办公用品编号
        String stockId = request.getParameter("stockId"); // 办公用品编号

        // 根据办公用品编号取得办公用品详细信息
        StockInfo stockInfo = stockManager.getStockInfoById(new Long(stockId));

        V3xOrgMember member = this.orgManager.getMemberById(stockInfo.getStockRes());
        String stockResName = "";
        if (member != null)
        {
            stockResName = member.getName();
        }
        // 保存到视图模型中
        modelView.addObject("bean", stockInfo);
        modelView.addObject("stockResName", stockResName);
    }

	/**
	 * 保存办公用品申请
	 */
	protected void onSave(HttpServletRequest request, HttpServletResponse response, ModelAndView modelView, boolean arg3) throws Exception {
		String stockId = request.getParameter("stockId");
		String applyCount = request.getParameter("applyCount");
		String applyMemo = request.getParameter("applyMemo");

		StockInfo stockInfo = stockManager.getStockInfoById(new Long(stockId));

		Long applyManager = stockInfo.getStockRes(); // 管理员
		User curUser = CurrentUser.get();
		Long applyUserName = curUser.getId(); // 申请人ID
		Long applyDepId = curUser.getDepartmentId(); // 申请人部门ID

		OfficeApply officeApply = new OfficeApply(); // 申请单对象
		officeApply.setApplyDate(new Date()); // 申请日期
		officeApply.setApplyDepId(applyDepId); // 部门ID
		officeApply.setApplyManager(applyManager); // 管理者
		officeApply.setApplyState(new Integer(1)); // 待审状态 1
		officeApply.setApplyType("3"); // 申请类型
		officeApply.setApplyUserName(applyUserName); // 申请人
		officeApply.setDeleteFlag(new Integer(0)); // 删除表示
		officeApply.setApplyMemo(applyMemo);

		// 办公用品详细申请单信息
		StockApplyInfo stockApply = new StockApplyInfo();
		stockApply.setApplyCount(new Integer(applyCount));
		stockApply.setStockId(new Long(stockId));
		stockApply.setDeleteFlag(new Integer(0));
		this.stockManager.saveStockApply(officeApply, stockApply);
		
		// 审批人首页待办
		OfficeHelper.addPendingAffair(stockInfo.getStockName(), officeApply, ApplicationSubCategoryEnum.office_stock);
		
		modelView.addObject("script", "alert(\'" + ResourceBundleUtil.getString(Constants.STOCK_RESOURCE_NAME, "stock.alert.mgr", stockInfo.getStockName()) + "\');parent.parent.list.location.href=parent.parent.list.tempUrl;\n");
		
		// 给管理员发送消息
		try {
			userMessageManager.sendSystemMessage(MessageContent.get("office.work.apply", stockInfo.getStockName(), curUser.getName()), 
					ApplicationCategoryEnum.office, curUser.getId(), 
					MessageReceiver.get(officeApply.getApplyId(), applyManager, "message.link.office.stock", String.valueOf(officeApply.getApplyId())));
		} catch (MessageException e) {
			logger.error("办公用品申请失败：", e);
		}
	}

}