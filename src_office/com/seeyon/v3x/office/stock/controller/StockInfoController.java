package com.seeyon.v3x.office.stock.controller;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.*;
import com.seeyon.v3x.office.common.OfficeModelType;
/**
 * 办公用品详细信息相关操作控制类
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-21
 */
public class StockInfoController extends BaseManageController
{

    private static Logger log = Logger.getLogger(StockInfoController.class);

    private StockManager stockManager; // 办公用品管理类

    private OrgManager orgManager;

    private OfficeCommonManager officeCommonManager;

    private String indexView;

    private String contentView;

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setIndexView(String indexView)
    {
        this.indexView = indexView;
    }

    public void setContentView(String contentView)
    {
        this.contentView = contentView;
    }

    public void setStockManager(StockManager stockManager)
    {
        this.stockManager = stockManager;
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
        ModelAndView mav = new ModelAndView("office/stock/stockInfoFrame");

        return mav;
    }

    public ModelAndView export(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView modelView = new ModelAndView("office/stock/stockExcel");

        List stockList = this.stockManager.getStockSummay(false);
        List stockListOfDep = stockManager.getStockSummayByDep(false);
       

        request.setAttribute("stockList", stockList);
        request.setAttribute("stockListOfDep", stockListOfDep);
        String isIn = request.getParameter("isIn");
        request.setAttribute("isIn", isIn);
        return modelView;
    }

    /**
     * 办公用品统计汇总
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
        ModelAndView mav = new ModelAndView("office/stock/stockCount");

        List stockList = new ArrayList();
        List stockListOfDep = new ArrayList();
        String isIn = request.getParameter("isIn");
        if(Strings.isBlank(isIn) || "1".equals(isIn)){
        	isIn = "1";
        	stockList = this.stockManager.getStockSummay(true);
        } else {
        	stockListOfDep = stockManager.getStockSummayByDep(true);
        }
        mav.addObject("isIn", isIn);
        mav.addObject("stockList", stockList);
        mav.addObject("stockListOfDep", stockListOfDep);
       
        return mav;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {

        ModelAndView mav = new ModelAndView(indexView);

        User user = CurrentUser.get();

        int iResult = this.officeCommonManager.checkAdminModel(4, user);
        if (iResult > 0)
        {
            // 普通用户
            mav = new ModelAndView("office/stock/stockApplyIndex");

        }
        else
        {
            // 管理员

        }

        return mav;
    }

    public ModelAndView content(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView(contentView);
        return mav;
    }

    /**
     * 登记办公用品详细信息相关操作
     */
    protected void onCreate(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {
        User curUser = CurrentUser.get();
        // 取得办公类别设置
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.stock_type+"", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockId(this.stockManager.getMaxStockNo());
        stockInfo.setStockRes(new Long(curUser.getId()));
        stockInfo.setStockDate(new Date());

        modelView.addObject("bean", stockInfo);

        String managerName = curUser.getName();
        modelView.addObject("stockResName", managerName);

        // 取得管理员列表
        List managerList = this.officeCommonManager.getModelManagers(OfficeModelType.stock_type, curUser);
        V3xOrgAccount accountById = orgManager.getAccountById(curUser.getAccountId());
        Map<Long,Boolean> accountId = new HashMap<Long, Boolean>();
        accountId.put(accountById.getId(), true);
        if (!accountById.getAccessPermission().equals(8)){
            List<V3xOrgAccount> accessableAccounts = accountById.getAccessableAccounts();
            for (V3xOrgAccount account : accessableAccounts) {
            	accountId.put(account.getId(), true);
            }
        }
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

        modelView.addObject("actionType", "create");
    }

    /**
     * 办公用品详细信息查询列表操作
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
        // 管理者
        User curUser = CurrentUser.get();
        Long managerId = curUser.getId();

        List list = this.stockManager.getStockInfoList(fieldName, fieldValue, managerId);

        // 保存结果到视图模型中
        modelView.addObject("list", list);

        // if (list.size() <= 0 && !"".equals(fieldName))
        // {
        // modelView.clear();
        // modelView.setViewName(this.successView);
        //
        // modelView
        // .addObject(
        // "script",
        // "alert(\'"
        // + ResourceBundleUtil
        // .getString(
        // com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
        // "stock.alert.nofound")
        // + "\');window.history.back(-1);");
        // }
        // 取得办公用品类别设置
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.stock_type+"", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);
    }

    /**
     * 办公用品详细信息修改
     */
    protected void onEdit(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得办公用品编号
        String stockId = request.getParameter("stockId"); // 办公用品编号

        // 根据办公用品编号取得办公用品详细信息
        StockInfo stockInfo = stockManager.getStockInfoById(new Long(stockId));

        V3xOrgMember member = this.orgManager.getMemberById(stockInfo.getStockRes());
        if (member != null)
        {
            String stockResName = member.getName();
            modelView.addObject("stockResName", stockResName);
        }
        if (stockInfo.getStockPrice().floatValue() <= 0.0)
        {
            modelView.addObject("stockPrice", "");// 如果取的价格为0就是创建没填写
        }
        else
        {
            modelView.addObject("stockPrice", stockInfo.getStockPrice());
        }

        // 保存到视图模型中
        modelView.addObject("bean", stockInfo);
        modelView.addObject("actionType", "update");

        // 取得办公用品类别设置
        User curUser = CurrentUser.get();
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.stock_type+"", curUser.getLoginAccount());
        modelView.addObject("typeList", typeList);

        // 取得管理员列表
        List managerList = this.officeCommonManager.getModelManagers(4, curUser);

        // 取得不重复的管理员列表
        Long accountid = curUser.getLoginAccount();
        Collection col = new HashSet();
        Iterator it = managerList.iterator();
        while (it.hasNext())
        {   Object next = it.next();
        	if (next == null) {
        		continue;
        	}
        	V3xOrgMember members = (V3xOrgMember) next;
        	if (accountid.equals(members.getOrgAccountId())) {
        		col.add(next);
        	} else {
             	List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(members.getId());
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

    /**
     * 删除所选的办公用品详细信息
     */
    protected void onRemoveSelected(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得删除的用品编号集
        String stockIds = request.getParameter("stockIds"); // 用品编号集

        if (stockIds == null || "".equals(stockIds))
        {
            // 如果没做选择，不作处理
        }
        else
        {
            // 对应办公用品编号集的删除标识改为1
            stockManager.deleteStockInfoByIds(stockIds);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("alert(\'"
                + ResourceBundleUtil.getString(
                        com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
                        "stock.alert.delete.success") + "\');\n");
        sb.append("parent.list.location.reload();\n");

        modelView.addObject("script", sb.toString());
    }

    /**
     * 登记办公用品详细信息结果保存相关操作
     */
    protected void onSave(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView, boolean arg3) throws Exception
    {
        // 参数取得
        String actionType = request.getParameter("actionType").trim(); // 操作类型
        // create:新增
        // update:修改

        String stockAvacount = request.getParameter("stockAvacount");
        String stockCount = request.getParameter("stockCount");
        String stockDate = request.getParameter("stockDate");
        String stockId = request.getParameter("stockId");
        String stockModel = request.getParameter("stockModel");
        String stockName = request.getParameter("stockName");
        String stockPrice = request.getParameter("stockPrice");
        if (Strings.isBlank(stockPrice))
        {
            stockPrice = "0";
        }
        String stockRes = request.getParameter("stockRes");
        String stockState = request.getParameter("stockState");
        String stockType = request.getParameter("stockType");
        String stockUnit = request.getParameter("stockUnit");

        StockInfo stockInfo = null;
        Long accountId = CurrentUser.get().getLoginAccount();
        if ("create".equals(actionType))
        {
            // 新建
            stockInfo = new StockInfo();
            stockInfo.setStockId(new Long(stockId));
            stockInfo.setCreateDate(new Date());
            stockInfo.setDeleteFlag(new Integer(0));

            stockInfo.setStockAvacount(new Integer(stockAvacount));
            stockInfo.setStockCount(new Integer(stockCount));
            stockInfo.setStockDate(Datetimes.parseDate(stockDate));
            stockInfo.setStockModel(stockModel);
            stockInfo.setStockName(stockName);
            stockInfo.setStockPrice(new Float(stockPrice));
            stockInfo.setStockRes(new Long(stockRes));
            stockInfo.setStockState(new Integer(stockState));
            stockInfo.setStockUnit(stockUnit);
            stockInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils.getLongParameter(request, "stockType")));
            stockInfo.setAccountId(accountId);
            this.stockManager.createStockInfo(stockInfo);
        }
        else
        {
            // 修改
            stockInfo = this.stockManager.getStockInfoById(new Long(stockId));
            stockInfo.setModifyDate(new Date());

            stockInfo.setStockAvacount(new Integer(stockAvacount));
            stockInfo.setStockCount(new Integer(stockCount));
            stockInfo.setStockDate(Datetimes.parseDate(stockDate));
            stockInfo.setStockModel(stockModel);
            stockInfo.setStockName(stockName);
            stockInfo.setStockPrice(new Float(stockPrice));
            stockInfo.setStockRes(new Long(stockRes));
            stockInfo.setStockState(new Integer(stockState));
            stockInfo.setStockUnit(stockUnit);
            stockInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils.getLongParameter(request, "stockType")));
            // 目前的业务逻辑：登陆人员可以申请本部门管理员管理的物品；不是申请本部门的办公用品
            stockInfo.setAccountId(accountId);

            if (stockInfo != null)
            {

                this.stockManager.updateStockInfo(stockInfo);
            }

        }
        StringBuffer sb = new StringBuffer();
        sb.append("alert(\'"
                + ResourceBundleUtil.getString(
                        com.seeyon.v3x.office.stock.util.Constants.STOCK_RESOURCE_NAME,
                        "stock.alert.oper.success") + "\');\n");
        sb.append("parent.list.location.href=parent.list.tempUrl;\n");

        modelView.addObject("script", sb.toString());
    }

    /**
     * 查看显示办公用品详细信息相关操作
     */
    protected void onShow(HttpServletRequest request, HttpServletResponse response,
            ModelAndView modelView) throws Exception
    {

        // 取得参数
        String stockId = request.getParameter("stockId"); // 办公用品编号

        StockInfo stockInfo = null;

        try
        {
            // 根据办公用品编号取得办公用品详细信息
            stockInfo = stockManager.getStockInfoById(new Long(stockId));
            if (stockInfo != null)
            {
                V3xOrgMember member = this.orgManager.getMemberById(stockInfo.getStockRes());
                if (member != null)
                {
                    String stockResName = member.getName();
                    modelView.addObject("stockResName", stockResName);
                }
                if (stockInfo.getStockPrice().floatValue() <= 0.0)
                {
                    modelView.addObject("stockPrice", "");// 如果取的加个为0就是创建没填写
                }
                else
                {
                    modelView.addObject("stockPrice", stockInfo.getStockPrice());
                }
            }
        }
        catch (Exception e)
        {
            log.error("根据编号取得办公用品详细信息错误：" + e.getMessage());
        }

        modelView.addObject("bean", stockInfo);

    }

}
