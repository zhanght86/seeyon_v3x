package com.seeyon.v3x.office.asset.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.domain.TAssetDepartinfo;
import com.seeyon.v3x.office.asset.manager.AssetManager;
import com.seeyon.v3x.office.asset.util.Constants;
import com.seeyon.v3x.office.asset.util.str;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.manager.OfficeApplyManager;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.myapply.manager.MyApplyManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * <p>
 * Create Date: 2007-7-1modifide
 * </P>
 * 
 * @author liusg
 * @author maok
 * @author modified by YONGZHANG
 */
public class AssetController extends BaseManageController
{
	private static final Log log = LogFactory.getLog(AssetController.class);
	
    private AssetManager assetManager;
    
    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private MyApplyManager myApplyManager;

    private AdminManager officeAdminManager;

    private OfficeApplyManager officeApplyManager;

    private UserMessageManager userMessageManager;

    // 类别管理Manager
    private OfficeCommonManager officeCommonManager;

    private String applyStatus_Wait = "";

    public OfficeCommonManager getOfficeCommonManager()
    {
        return officeCommonManager;
    }

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setAssetManager(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    public void setAutoManager(AutoManager autoManager) {
		this.autoManager = autoManager;
	}

	public void setOrgManager(OrgManager orgManager)
    {
        this.orgManager = orgManager;
    }

    public void setMyApplyManager(MyApplyManager myApplyManager)
    {
        this.myApplyManager = myApplyManager;
    }

    public AdminManager getOfficeAdminManager()
    {
        return officeAdminManager;
    }

    public void setOfficeAdminManager(AdminManager officeAdminManager)
    {
        this.officeAdminManager = officeAdminManager;
    }

    public OfficeApplyManager getOfficeApplyManager()
    {
        return officeApplyManager;
    }

    public void setOfficeApplyManager(OfficeApplyManager officeApplyManager)
    {
        this.officeApplyManager = officeApplyManager;
    }

    public void setUserMessageManager(UserMessageManager userMessageManager)
    {
        this.userMessageManager = userMessageManager;
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("office/asset/index");
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        mav.addObject("admin", checkAdmin);

        return mav;
    }

    public ModelAndView jumpUrl(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String url = request.getParameter("url");
        ModelAndView mav = new ModelAndView(url);
        return mav;
    }
    //modifyed by Dong Yajie on 2009年4月9日15:50:30 符合a8开发规范
    public ModelAndView regList(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        String condition = request.getParameter("condition");
        String keyword = request.getParameter("textfield");
        
        ModelAndView mav = new ModelAndView("office/asset/list_reg");
        List list = assetManager.getAssetRegList(user.getId(), condition, keyword);
        mav.addObject("list", list);
        //类别
        List typelist = officeCommonManager.getModelTypes(new Integer(OfficeModelType.asset_type).toString(), user.getAccountId());
        mav.addObject("typeList",typelist);
        return mav;
    }

    public ModelAndView appList(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
    	ModelAndView mav = new ModelAndView("office/asset/list_app");
        User user = CurrentUser.get();
        List typeList = officeCommonManager.getModelTypes(new Integer(OfficeModelType.asset_type).toString(), user.getAccountId());
        mav.addObject("typeList",typeList);
        
        String departStr = officeApplyManager.getUserModelManagersIds(OfficeModelType.asset_type,
                user);
        Long[] depart =null;
        if(Strings.isNotBlank(departStr)){
        	 String[] departs = departStr.split(",");
        	 depart = new Long[departs.length];
             for (int i = 0; i < depart.length; i++) {
     			depart[i] = new Long(departs[i]);
     		}
        }else{
        	return mav;
        }
       
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
        List appList = assetManager.getAssetAppList(condition,keyWord,depart);
        
        mav.addObject("list", appList);
       
        return mav;
    }

    public ModelAndView permList(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        /*String idSql = this.officeAdminManager.getInfoIds(user.getId(), 2);
        String[] idstr = idSql.split(",");
        Long[] ids = new Long[idstr.length];
        for (int i = 0; i < ids.length; i++) {
			ids[i] = new Long(idstr[i]);
		}
        */
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
      
        List<Long> departId = this.officeAdminManager.getAdminManageDepartments(user.getId(), user.getAccountId(), "_1___");
        
        List applyList = assetManager.getAssetPermList(condition, keyWord, user.getId()) ;
       
        ArrayList arr = new ArrayList();
        
        Map<String,Boolean> proxy = new HashMap<String,Boolean>();
        if (applyList != null)
        {
            for (Object applyObject:applyList)
            {
                TApplylist apply = (TApplylist) applyObject;
                TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(apply.getApplyId());
                V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());
                apply.setPerson_Name(member.getName());
                Object[] o = this.officeAdminManager.getMemberDepProxy(member, user.getAccountId(), user.getId(), "_1___", departId);
                apply.setDep_Name(o[0].toString());
                proxy.put(apply.getApplyId().toString(), (Boolean)o[1]);
                
                apply.setOffice_Id(assetApply.getAssetId());
                MAssetInfo assetInfo = this.assetManager.getById(assetApply.getAssetId());
                apply.setOffice_Name(assetInfo.getAssetName());
                apply.setStart_date(assetApply.getAssetStart());
                apply.setEnd_date(assetApply.getAssetEnd());
                apply.setPurpose(assetApply.getAssetPurpose());
                if (assetApply.getApplyCount() != null)
                {
                    apply.setApplyCount(assetApply.getApplyCount());
                }
                switch (apply.getApplyState().intValue())
                {
                    case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow:
                    {
                        apply.setClassName("td_green");
                        break;
                    }
                    case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_NotAllow:
                    {
                        apply.setClassName("td_red");
                        break;
                    }
                }
                arr.add(apply);
            }
        }
        ModelAndView mav = new ModelAndView("office/asset/list_perm");
        if (request.getAttribute("script") != null
                && ((String) request.getAttribute("script")).length() > 0)
        {

            mav.addObject("script", request.getAttribute("script"));
        }
        if (!"".equals(applyStatus_Wait))
        {
            mav.addObject("script", applyStatus_Wait);
            applyStatus_Wait = "";
        }
        mav.addObject("list", arr);
        mav.addObject("proxy", proxy);
        return mav;
    }

    public ModelAndView storageList(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
      
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        String idSql = this.officeAdminManager.getInfoIds(user.getId(), 2);
      
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
        String ids[] = idSql.split(",");
        Long[] id = new Long[ids.length];
        for (int i = 0; i < id.length; i++) {
			id[i] = Long.parseLong(ids[i]);
		}
        List applyList = assetManager.getAssetStorageList(condition,keyWord,id);
        
       
        ArrayList arr = new ArrayList();
        Map<String,Boolean> proxy = new HashMap<String,Boolean>();
        List<Long> departId = this.officeAdminManager.getAdminManageDepartments(user.getId(), user.getAccountId(), "_1___");
        int size = applyList.size();
        if (applyList != null)
        {
            for (int i = 0; i < size; i++)
            {
                TApplylist apply = (TApplylist) applyList.get(i);
                TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(apply.getApplyId());
                V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());

                apply.setPerson_Name(member.getName());
                Object[] o = this.officeAdminManager.getMemberDepProxy(member, user.getAccountId(), user.getId(), "_1___", departId);
                apply.setDep_Name(o[0].toString());
                proxy.put(apply.getApplyId().toString(), (Boolean)o[1]);
                
                apply.setOffice_Id(assetApply.getAssetId());
                MAssetInfo assetInfo = this.assetManager.getById(assetApply.getAssetId());
                apply.setOffice_Name(assetInfo.getAssetName());
                apply.setStart_date(assetApply.getAssetStart());
                apply.setEnd_date(assetApply.getAssetEnd());
                apply.setPurpose(assetApply.getAssetPurpose());
                if (assetApply.getApplyCount() != null)
                {
                    apply.setApplyCount(assetApply.getApplyCount());
                }
                try
                {
                    TAssetDepartinfo assetDepart = this.assetManager.getDepartinfoById(apply
                            .getApplyId());
                    if (assetDepart != null)
                    {
                        if (assetDepart.getAssetBacktime() != null
                                && assetDepart.getAssetBackcount() != null)
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back);
                            apply.setClassName("td_blue");
                        }
                        else if (assetDepart.getAssetDeparttime() != null
                                && assetDepart.getAssetDepartcount() != null)
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart);
                            apply.setClassName("td_cyan");
                        }
                    }
                }
                catch (Exception ex)
                {
                }
                arr.add(apply);
            }
        }
        ModelAndView mav = new ModelAndView("office/asset/list_storage");
        if (request.getAttribute("script") != null
                && ((String) request.getAttribute("script")).length() > 0)
        {
            mav.addObject("script", request.getAttribute("script"));
        }
        mav.addObject("list", arr);
        mav.addObject("proxy", proxy);
        return mav;
    }

    /**
     * @author caofei 2008-9-17
     * @description Comprehensive Office Building ---[add Meeting Management
     *              update]
     * @param request
     * @param response
     * @return ModelAndView
     */

    public ModelAndView create_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        Long loginAccount = user.getLoginAccount();
        ModelAndView mav = new ModelAndView("office/asset/create_reg");
      
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.asset_type+"", loginAccount);
     
        List list = this.officeAdminManager.getAdminSettingByModelAdmin("_1___", null);
        V3xOrgAccount accountById = orgManager.getAccountById(user.getAccountId());
        Long accountid = user.getLoginAccount();
        ArrayList arrAdmin = new ArrayList();
        ArrayList temp = new ArrayList();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            MAdminSetting admin = (MAdminSetting) list.get(i);
            if (temp.contains(admin.getId().getAdmin()))
            {
                continue;
            }
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId().getAdmin());
        	if (accountid.equals(member.getOrgAccountId())) {
                admin.setAdminName(member.getName());
                arrAdmin.add(admin);
                temp.add(admin.getId().getAdmin());
        	} else {
             	List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(member.getId());
            	if (concurrentAccounts != null && concurrentAccounts.size() > 0) {
            		for (V3xOrgAccount account : concurrentAccounts) {
            			if (account.getId().equals(accountid)) {
                            admin.setAdminName(member.getName());
                            arrAdmin.add(admin);
                            temp.add(admin.getId().getAdmin());
            				break;
            			}
            		}
            	}
        	}
        }
        // 获取设备列表，前台编号唯一判断
       
        List deviceList = this.assetManager.getAllAssetInfo(user.getId());
        mav.addObject("deviceList", deviceList);

        mav.addObject("adminList", arrAdmin);
        mav.addObject("typeList", typeList);
        mav.addObject("buydate", new Date());
        if (request.getParameter("show") != null && request.getParameter("show").equals("1"))
        {
            mav.addObject("show", 1);
        }
        return mav;
    }

    /**
     * @author caofei 2008-9-17
     * @description Comprehensive Office Building ---[add Meeting Management
     *              update]
     * @param request
     * @param response
     * @return ModelAndView
     */
    public ModelAndView doCreate_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        MAssetInfo mAssetInfo = new MAssetInfo();
        mAssetInfo.setAssetId(UUIDLong.longUUID());
        mAssetInfo.setAssetName(request.getParameter("asset_name"));
        mAssetInfo.setAssetCode(request.getParameter("asset_code"));
        if (request.getParameter("asset_mge") != null
                && request.getParameter("asset_mge").length() > 0)
        {
            mAssetInfo.setAssetMge(Long.parseLong(request.getParameter("asset_mge")));
        }
        mAssetInfo.setAssetModel(request.getParameter("asset_model"));
        // mAssetInfo.setAssetType(request.getParameter("asset_type"));
        mAssetInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils
                .getLongParameter(request, "asset_type")));
        if (request.getParameter("asset_date") != null
                && request.getParameter("asset_date").length() > 0)
        {
            mAssetInfo.setAssetDate(str.strToDate(request.getParameter("asset_date")));
        }
        mAssetInfo
                .setAssetState(new Integer(Integer.parseInt(request.getParameter("asset_state"))));
        if (request.getParameter("asset_price") != null
                && request.getParameter("asset_price").length() > 0)
        {
            mAssetInfo.setAssetPrice(new Double(Double.parseDouble(request
                    .getParameter("asset_price"))));
        }
        mAssetInfo.setAssetCount(Long.parseLong(request.getParameter("asset_count")));
        if (request.getParameter("asset_avacount") != null
                && request.getParameter("asset_avacount").length() > 0)
        {
            mAssetInfo.setAssetAvacount(Long.parseLong(request.getParameter("asset_avacount")));
        }
        mAssetInfo.setCreateDate(new Date());
        mAssetInfo.setDomainId(user.getLoginAccount());
        mAssetInfo.setDelFlag(new Integer(Constants.Del_Flag_Normal));
        ModelAndView mav = new ModelAndView("office/asset/create_reg");
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.asset_type + "",
                user.getLoginAccount());
       
        List list = this.officeAdminManager.getAdminSettingById(null, null, user.getLoginAccount(), "_1___", false);

        ArrayList arrAdmin = new ArrayList();
        ArrayList temp = new ArrayList();
        for (int i = 0; i < list.size(); i++)
        {
            MAdminSetting admin = (MAdminSetting) list.get(i);
            if (temp.contains(admin.getId().getAdmin()))
            {
                continue;
            }
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId().getAdmin());
            admin.setAdminName(member.getName());
            arrAdmin.add(admin);
            temp.add(admin.getId().getAdmin());
        }
        mav.addObject("adminList", arrAdmin);
        mav.addObject("typeList", typeList);
        try
        {
            this.assetManager.save(mAssetInfo);
            rendJavaScript(response, "alert(\""
                    + ResourceBundleUtil.getString(Constants.ASSET_RESOURCE_NAME,
                            "asset.alert.success", new Object[0])
                    + "\");parent.listFrame.listIframe.location.reload();");
            super.refreshWorkspace();
        }
        catch (Exception ex)
        {
            mav.addObject("script", "alert(\"" + ex.getMessage() + "\");");
        }
        return mav;
    }

    /**
     * @author caofei 2008-9-17
     * @description Comprehensive Office Building ---[add Meeting Management
     *              update]
     * @param request
     * @param response
     * @return ModelAndView
     */
    public ModelAndView edit_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        String id = request.getParameter("id");
        MAssetInfo mAssetInfo = this.assetManager.getById(Long.parseLong(id));
        ModelAndView mav = new ModelAndView("office/asset/edit_reg");
        if (mAssetInfo.getAssetMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mAssetInfo.getAssetMge());
            mav.addObject("assetMge_Id", String.valueOf(mAssetInfo.getAssetMge()));
            mav.addObject("assetMge_Name", member.getName());
        }
        if (mAssetInfo.getAssetDate() != null)
        {
            mav.addObject("assetDate", str.dateToStr(mAssetInfo.getAssetDate()));
        }
        if (mAssetInfo.getAssetPrice() != null)
        {
            mav.addObject("assetPrice", mAssetInfo.getAssetPrice().doubleValue());
        }
        List typeList = this.officeCommonManager.getModelTypes(OfficeModelType.asset_type+"", user.getLoginAccount());
        
        List list = this.officeAdminManager.getAdminSettingByModelAdmin("_1___", null);
        //List list = query.list();
        ArrayList arrAdmin = new ArrayList();
        ArrayList temp = new ArrayList();
        Long accountid = user.getLoginAccount();
        for (int i = 0; i < list.size(); i++)
        {
            MAdminSetting admin = (MAdminSetting) list.get(i);
            if (temp.contains(admin.getId().getAdmin()))
            {
                continue;
            }
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId().getAdmin());
        	if (accountid.equals(member.getOrgAccountId())) {
                admin.setAdminName(member.getName());
                arrAdmin.add(admin);
                temp.add(admin.getId().getAdmin());
        	} else {
             	List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(member.getId());
            	if (concurrentAccounts != null && concurrentAccounts.size() > 0) {
            		for (V3xOrgAccount account : concurrentAccounts) {
            			if (account.getId().equals(accountid)) {
                            admin.setAdminName(member.getName());
                            arrAdmin.add(admin);
                            temp.add(admin.getId().getAdmin());
            				break;
            			}
            		}
            	}
        	}
        }
        mav.addObject("adminList", arrAdmin);
        mav.addObject("typeList", typeList);
        mav.addObject("bean", mAssetInfo);
        return mav;
    }

    public ModelAndView doEdit_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        long assetId = Long.parseLong(request.getParameter("assetId"));
        MAssetInfo mAssetInfo = this.assetManager.getById(assetId);
        mAssetInfo.setAssetName(request.getParameter("asset_name"));
        mAssetInfo.setAssetCode(request.getParameter("asset_code"));
        mAssetInfo.setAssetAvacount(new Long(request.getParameter("asset_avacount")));
        mAssetInfo.setAssetCount(new Long(request.getParameter("asset_count")));
        if (request.getParameter("asset_mge") != null
                && request.getParameter("asset_mge").length() > 0)
        {
            mAssetInfo.setAssetMge(Long.parseLong(request.getParameter("asset_mge")));
        }
        else
        {
            mAssetInfo.setAssetMge(null);
        }
        mAssetInfo.setAssetModel(request.getParameter("asset_model"));
        // mAssetInfo.setAssetType(request.getParameter("asset_type"));
        mAssetInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils
                .getLongParameter(request, "asset_type")));
        if (request.getParameter("asset_date") != null
                && request.getParameter("asset_date").length() > 0)
        {
            mAssetInfo.setAssetDate(str.strToDate(request.getParameter("asset_date")));
        }
        else
        {
            mAssetInfo.setAssetDate(null);
        }
        mAssetInfo
                .setAssetState(new Integer(Integer.parseInt(request.getParameter("asset_state"))));
        if (request.getParameter("asset_price") != null
                && request.getParameter("asset_price").length() > 0)
        {
            mAssetInfo.setAssetPrice(new Double(Double.parseDouble(request
                    .getParameter("asset_price"))));
        }
        else
        {
            mAssetInfo.setAssetPrice(null);
        }
        // 前台的可申请数量置灰了。
        mAssetInfo.setModifyDate(new Date());
        mAssetInfo.setDomainId(user.getLoginAccount());
        request.setAttribute("id", String.valueOf(assetId));
        ModelAndView mav = this.detail_reg(request, response);
        try
        {
            this.assetManager.update(mAssetInfo);
            mav.addObject("script", "parent.listFrame.listIframe.location.reload();");
        }
        catch (Exception ex)
        {
        	log.error(ex.getMessage(), ex);
            mav.addObject("script", "alert(\"" + ex.getMessage() + "\");");
        }
        return mav;
    }

    public ModelAndView detail_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String id = request.getParameter("id");
        if (id == null)
        {
            id = (String) request.getAttribute("id");
        }
        String fs = request.getParameter("fs");
        MAssetInfo mAssetInfo = this.assetManager.getById(Long.parseLong(id));
        ModelAndView mav = new ModelAndView("office/asset/detail_reg");
        if (mAssetInfo.getAssetMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mAssetInfo.getAssetMge());
            mav.addObject("assetMge_Id", String.valueOf(mAssetInfo.getAssetMge()));
            mav.addObject("assetMge_Name", member.getName());
        }
        if (mAssetInfo.getAssetDate() != null)
        {
            mav.addObject("assetDate", str.dateToStr(mAssetInfo.getAssetDate()));
        }
        if (mAssetInfo.getAssetPrice() != null)
        {
            mav.addObject("assetPrice", mAssetInfo.getAssetPrice().doubleValue());
        }
        mav.addObject("bean", mAssetInfo);
        if (fs != null && fs.length() > 0)
        {
            mav.addObject("fs", new Integer(1));
        }
        return mav;
    }

    public ModelAndView del_reg(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String[] ids = request.getParameterValues("id");
        for (int i = 0; i < ids.length; i++)
        {
            MAssetInfo mAssetInfo = this.assetManager.getById(Long.parseLong(ids[i]));
            mAssetInfo.setDelFlag(Constants.Del_Flag_Delete);
            this.assetManager.update(mAssetInfo);
        }
        return this.regList(request, response);
    }

    public ModelAndView create_app(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("office/asset/create_app");
        String id = request.getParameter("id");
        if (id == null)
        {
            id = (String) request.getAttribute("id");
        }
        if (id == null)
        {
            return mav;
        }
        String fs = request.getParameter("fs");
        MAssetInfo mAssetInfo = this.assetManager.getById(Long.parseLong(id));
        if (mAssetInfo.getAssetMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mAssetInfo.getAssetMge());
            mav.addObject("assetMge_Id", String.valueOf(mAssetInfo.getAssetMge()));
            mav.addObject("assetMge_Name", member.getName());
        }
        User user = CurrentUser.get();
        V3xOrgMember member = this.orgManager.getMemberById(user.getId());
        V3xOrgDepartment department = this.orgManager
                .getDepartmentById(member.getOrgDepartmentId());
        mav.addObject("member", member);
        mav.addObject("department", department);
        if (fs != null && fs.length() > 0)
        {
            mav.addObject("fs", new Integer(1));
        }
        mav.addObject("bean", mAssetInfo);
        return mav;
    }

	/**
	 * 保存办公设备申请
	 */
	public ModelAndView doCreate_app(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		long assetId = Long.parseLong(request.getParameter("assetId"));
		String apply_count = request.getParameter("apply_count");
		String asset_start = request.getParameter("asset_start");
		String asset_end = request.getParameter("asset_end");
		String asset_purpose = request.getParameter("asset_purpose");
		long apply_user = Long.parseLong(request.getParameter("apply_user"));
		long apply_usedep = Long.parseLong(request.getParameter("apply_usedep"));
		String long_flag = request.getParameter("long_flag");
		
		this.assetManager.createApply(assetId, user.getId(), user.getDepartmentId(), apply_user, apply_usedep, long_flag, apply_count, asset_start, asset_end, asset_purpose);
		
		ModelAndView mav = new ModelAndView("office/asset/create_app");
		mav.addObject("script", "alert(\"" + ResourceBundleUtil.getString(Constants.ASSET_RESOURCE_NAME, "asset.alert.asset.mgr", new Object[0]) + "\");parent.listFrame.listIframe.location.reload();");
		return mav;
	}

    public ModelAndView create_storage(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String applyId = request.getParameter("id");
        String flag = request.getParameter("flag");
        ModelAndView mav = new ModelAndView("office/asset/create_storage");
        if (flag == null || flag.length() == 0)
        {
            flag = "0";
        }
        mav.addObject("flag", new Integer(Integer.parseInt(flag)));
        if (applyId == null || applyId.length() == 0)
        {
            return mav;
        }
        else
        {
            TApplylist apply = this.myApplyManager.getById(Long.parseLong(applyId));
            TAssetApplyinfo assetApply = this.assetManager
                    .getApplyinfoById(Long.parseLong(applyId));
            MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
            V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());
            User user = CurrentUser.get();
            List<Long>	departId = this.officeAdminManager.getAdminManageDepartments(user.getId(), user.getAccountId(), "_1___");
            Object[] o = this.officeAdminManager.getMemberDepProxy(member, user.getAccountId(), user.getId(), "_1___", departId);
            try
            {
                TAssetDepartinfo assetDepart = this.assetManager.getDepartinfoById(Long
                        .parseLong(applyId));
                if (assetDepart != null)
                {
                    apply
                            .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
                    if (assetDepart.getAssetBacktime() != null
                            && assetDepart.getAssetBackcount() != null)
                    {
                        apply
                                .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back);
                    }
                    if (assetDepart.getAssetDeparttime() != null
                            && assetDepart.getAssetDepartcount() != null)
                    {
                        if (apply.getStorageStatus() == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back)
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Finish);
                        }
                        else
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart);
                        }
                    }
                    mav.addObject("assetDepartBean", assetDepart);
                }
            }
            catch (Exception ex)
            {
            }
            mav.addObject("applyBean", apply);
            mav.addObject("assetApplyBean", assetApply);
            mav.addObject("assetBean", asset);
            mav.addObject("personName", member);
            mav.addObject("depName", o[0]);
            mav.addObject("proxy", o[1]);
            String fs = request.getParameter("fs");
            if (fs != null && fs.length() > 0)
            {
                mav.addObject("fs", new Integer(1));
            }
        }
        return mav;
    }

    public ModelAndView create_perm(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String applyId = request.getParameter("id");
        ModelAndView mav = new ModelAndView("office/asset/create_perm");
        // 根据申请编号从“申请单一览表”取得申请单情报
        OfficeApply officeApply = this.autoManager.getOfficeApplyById(new Long(applyId));
        
        if (officeApply == null || officeApply.getDeleteFlag() != 0 || applyId == null || applyId.length() == 0)
        {
        	StringBuffer sb = new StringBuffer();
        	sb.append("alert('"+ ResourceBundleUtil.getString("com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources",
            "book.alert.delete.app")+"');\n");
        	 sb.append("window.close();\n");
        	super.rendJavaScript(response,  sb.toString());
            return null;
        }
        else
        {
            TApplylist apply = this.myApplyManager.getById(Long.parseLong(applyId));
            TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(apply.getApplyId());
            MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
            V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());
            User user = CurrentUser.get();
            List<Long>	departId = this.officeAdminManager.getAdminManageDepartments(user.getId(), user.getAccountId(), "_1___");
            Object[] o = this.officeAdminManager.getMemberDepProxy(member, user.getAccountId(), user.getId(), "_1___", departId);
            apply.setDep_Name(o[0].toString());
            
            mav.addObject("applyBean", apply);
            mav.addObject("assetApplyBean", assetApply);
            mav.addObject("assetBean", asset);
            mav.addObject("personName", member);
            mav.addObject("depName", o[0]);
            mav.addObject("proxy1", o[1]);
            if (apply.getApplyUser() != null && apply.getApplyUser() != 0)
            {
                V3xOrgMember useMember = this.orgManager.getMemberById(apply.getApplyUser());
                mav.addObject("useName", useMember);
            }
            if (apply.getApplyUsedep() != null && apply.getApplyUsedep() != 0)
            {
                V3xOrgMember userMember = (V3xOrgMember) mav.getModel().get("useName");
                Object[] o2 = this.officeAdminManager.getMemberDepProxy(userMember, user.getAccountId(), user.getId(), "_1___", departId);
                
                mav.addObject("useDepName", o2[0]);
                mav.addObject("proxy2", o2[1]);
            }
            String fs = request.getParameter("fs");
            if (fs != null && fs.length() > 0)
            {
                mav.addObject("fs", new Integer(1));
            }
        }
        return mav;
    }

    /**
     * 审批办公设备
     */
	public ModelAndView doCreate_perm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean fromPortal = "portal".equals(request.getParameter("from"));
		
		ModelAndView mav = new ModelAndView("office/asset/create_perm");
		StringBuffer sb = new StringBuffer();
		
		User user = CurrentUser.get();
		int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
		if (checkAdmin < 1) {
			if (fromPortal) {
				sb.append("parent.window.returnValue = \"true\";\n");
				sb.append("parent.window.close();\n");
				mav.addObject("script", sb.toString());
				return mav;
			} else {
				return super.refreshWorkspace();
			}
		}
		String applyId = request.getParameter("applyId");
		String apply_state = request.getParameter("apply_state");
		String apply_memo = request.getParameter("apply_memo");
		TApplylist apply = this.myApplyManager.getById(Long.parseLong(applyId));
		TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(Long.parseLong(applyId));
		MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
		apply.setApplyState(Integer.parseInt(apply_state));
		apply.setApplyMemo(apply_memo);
		apply.setAuditTime(new java.util.Date());
		this.myApplyManager.update(apply);
		List<Long> auth = new ArrayList<Long>();
		auth.add(apply.getApplyUser());
		String message = "";
		Collection<MessageReceiver> receivers = MessageReceiver.get(apply.getApplyDepId(), auth);
		if (Integer.parseInt(apply_state) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow) {
			if (asset.getAssetAvacount() > 0) {
				asset.setAssetAvacount(asset.getAssetAvacount() - assetApply.getApplyCount());
			}
			if (asset.getAssetAvacount() == 0) {
				asset.setAssetState(1);
			}
			// 通过审批
			message = "office.asset.audit.success";
		} else {
			// 未通过审批
			message = "office.asset.audit.nosuccess";
		}
		
		this.assetManager.update(asset);
		OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, apply.getApplyId());

		try {
			userMessageManager.sendSystemMessage(MessageContent.get(message, asset.getAssetName()), ApplicationCategoryEnum.office, CurrentUser.get().getId(), receivers);
		} catch (MessageException e) {
			log.error("", e);
		}
		
		if (fromPortal) {
			sb.append("parent.window.returnValue = \"true\";\n");
			sb.append("parent.window.close();\n");
		} else {
			sb.append("parent.listFrame.listIframe.location.reload();\n");
		}
		mav.addObject("script", sb.toString());
		return mav;
	}

    public ModelAndView doCreate_storage(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
    	User user = CurrentUser.get();
    	int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
        if(checkAdmin < 1){
        	return super.refreshWorkspace();
        }
        ModelAndView mav = new ModelAndView("office/asset/create_storage");
        long applyId = Long.parseLong(request.getParameter("applyId"));
        String asset_departtime = request.getParameter("asset_departtime");
        String asset_departcount = request.getParameter("asset_departcount");
        String asset_backtime = request.getParameter("asset_backtime");
        String asset_backcount = request.getParameter("asset_backcount");
        TAssetDepartinfo assetDepart = null;
        try
        {
            assetDepart = this.assetManager.getDepartinfoById(applyId);
            TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(applyId);
            MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
            if (request.getParameter("depart_memo") != null
                    && request.getParameter("depart_memo").length() > 0)
            {
                assetDepart.setDepartMemo(request.getParameter("depart_memo"));
            }
            if (asset_departtime != null && asset_departtime.length() > 0)
            {
                assetDepart.setAssetDeparttime(str.strToDate(asset_departtime));
            }
            if (asset_departcount != null && asset_departcount.length() > 0)
            {
                assetDepart.setAssetDepartcount(new Long(Long.parseLong(asset_departcount)));
            }
            if (asset_backtime != null && asset_backtime.length() > 0)
            {
                // by Yongzhang 2008-05-06
                Date departTime = assetDepart.getAssetDeparttime();
                if (departTime != null)
                {
                    if (departTime.compareTo(str.strToDate(asset_backtime)) <= 0)
                    {
                        assetDepart.setAssetBacktime(str.strToDate(asset_backtime));
                    }
                    else
                    {
                        mav.addObject("script", "alert(\""
                                + ResourceBundleUtil.getString(Constants.ASSET_RESOURCE_NAME,
                                        "asset.alert.backtime", new Object[0])
                                + "\");history.back(-1);");
                        return mav;
                    }
                }

            }
            if (asset_backcount != null && asset_backcount.length() > 0)
            {
                assetDepart.setAssetBackcount(new Long(Long.parseLong(asset_backcount)));
            }
            this.assetManager.update(assetDepart);
            asset.setAssetAvacount(new Long(asset.getAssetAvacount().longValue()
                    + Long.parseLong(asset_backcount)));
            // 归还时，库存也要增加2008-04-30 by Yongzhang
            asset.setAssetCount(new Long(asset.getAssetCount().longValue()
                    + Long.parseLong(asset_backcount)));
            
            asset.setAssetState(Constants.Asset_Status_Allow);
            this.assetManager.update(asset);
        }
        catch (org.hibernate.ObjectNotFoundException ex)
        {
            assetDepart = new TAssetDepartinfo();
            TAssetApplyinfo assetApply = this.assetManager.getApplyinfoById(applyId);
            MAssetInfo asset = this.assetManager.getById(assetApply.getAssetId());
            assetDepart.setApplyId(applyId);
            assetDepart.setAssetId(assetApply.getAssetId());
            assetDepart.setDelFlag(new Integer(Constants.Del_Flag_Normal));
            if (request.getParameter("depart_memo") != null
                    && request.getParameter("depart_memo").length() > 0)
            {
                assetDepart.setDepartMemo(request.getParameter("depart_memo"));
            }
            assetDepart.setApplyCount(assetApply.getApplyCount());
            if (asset_departtime != null && asset_departtime.length() > 0)
            {
                assetDepart.setAssetDeparttime(str.strToDate(asset_departtime));
            }
            if (asset_departcount != null && asset_departcount.length() > 0)
            {
                assetDepart.setAssetDepartcount(new Long(Long.parseLong(asset_departcount)));
            }
            if (asset_backtime != null && asset_backtime.length() > 0)
            {
                assetDepart.setAssetBacktime(str.strToDate(asset_backtime));
            }
            if (asset_backcount != null && asset_backcount.length() > 0)
            {
                assetDepart.setAssetBackcount(new Long(Long.parseLong(asset_backcount)));
            }
            this.assetManager.save(assetDepart);
            asset.setAssetAvacount(new Long(asset.getAssetAvacount().longValue()
                    + assetApply.getApplyCount().longValue() - Long.parseLong(asset_departcount)));
            // 借出时，库存也要减少2008-04-30 by Yongzhang
            asset.setAssetCount(new Long(asset.getAssetAvacount().longValue()
                    + assetApply.getApplyCount().longValue() - Long.parseLong(asset_departcount)));
            
            this.assetManager.update(asset);
        }

        mav.addObject("script", "parent.listFrame.listIframe.location.reload();");
        return mav;
    }

	/**
	 * 管理员删除办公设备申请
	 */
	public ModelAndView del_perm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] ids = request.getParameterValues("id");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				TApplylist apply = this.myApplyManager.getById(Long.parseLong(ids[i]));
				if (apply.getApplyState() == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait && apply.getApplyUsername() != CurrentUser.get().getId()) {
					applyStatus_Wait = "alert('" + ResourceBundleUtil.getString(Constants.ASSET_RESOURCE_NAME, "book.alert.delete.apply") + "');";
				} else {
					apply.setDelFlag(Constants.Del_Flag_Delete);
					this.myApplyManager.update(apply);
					//删除审批人待办
    		        OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, Long.valueOf(ids[i]));
					request.setAttribute("script", "alert(\"" + ResourceBundleUtil.getString(Constants.ASSET_RESOURCE_NAME, "asset.alert.delete.db", new Object[0]) + "\");");
				}
			}
		}
		return this.permList(request, response);
	}
    
    public ModelAndView tongji(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        // 去掉“我的申请”表里关于删除标志的约束2008-04-30 by Yongzhang
//        User user = CurrentUser.get();
//        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 2);
//        if(checkAdmin < 1){
//        	return super.refreshWorkspace();
//        }
//        String listSql = "Select * From t_applylist Where ";
//        // + Constants.Del_Flag_Normal;
//        listSql += " apply_type=2 and apply_state > 1";
//        SQLQuery query = this.assetManager.find(listSql,null);
//
//        query.addEntity(TApplylist.class);
//        List list = query.list();
//        ArrayList arr = new ArrayList();
//        if (list != null)
//        {
//            out: for (int i = 0; i < list.size(); i++)
//            {
//                TApplylist apply = (TApplylist) list.get(i);
//                for (int j = 0; j < arr.size(); j++)
//                {
//                    TongjiInfo temp = (TongjiInfo) arr.get(j);
//                    if (temp.getApplyUserNameId() == apply.getApplyUsername().longValue())
//                    {
//                        continue out;
//                    }
//                }
//                V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());
//                if (member.getOrgAccountId() == user.getLoginAccount())
//                {
//                    TongjiInfo info = new TongjiInfo();
//                    info.setApplyUserNameId(apply.getApplyUsername());
//                    info.setName(member.getName());
//                    info.setWeek(this.assetManager.getWeekCount(member.getId()));
//                    info.setMonth(this.assetManager.getMonthCount(member.getId()));
//                    info.setTotal(this.assetManager.getTotalCount(member.getId()));
//                    info.setTotalNoBack(this.assetManager.getTotalNoBackCount(member.getId()));
//                    arr.add(info);
//                }
//            }
//        }
//        int count = arr.size();
//        Pagination.setRowCount(count);
//        ArrayList pageArr = new ArrayList();
//        int endIndex = Pagination.getFirstResult() + Pagination.getMaxResults() > (count - 1) ? (count - 1)
//                : Pagination.getFirstResult() + Pagination.getMaxResults();
//        for (int i = Pagination.getFirstResult(); i <= endIndex; i++)
//        {
//            pageArr.add(arr.get(i));
//        }
        ModelAndView mav = new ModelAndView("office/asset/tongji");
        List memberList = new ArrayList();
        List departList = new ArrayList();
        String isIn = request.getParameter("isIn");
        if(Strings.isBlank(isIn) || "1".equals(isIn)){
        	isIn = "1";
        	memberList = assetManager.getAssetSummayByMember(true);
        } else {
        	departList =  assetManager.getAssetSummayByDep(true);
        }
        mav.addObject("isIn", isIn);
        //mav.addObject("list", pageArr);
        mav.addObject("departList", departList);
        mav.addObject("memberList",memberList);
        return mav;
    }

    public ModelAndView tongjiDownload(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
//        User user = CurrentUser.get();
//        String listSql = "Select * From t_applylist Where ";
//        // + Constants.Del_Flag_Normal;
//        listSql += " apply_type=2 and apply_state > 1";
//        SQLQuery query = this.assetManager.find(listSql,null);
//        query.addEntity(TApplylist.class);
//        List list = query.list();
//        ArrayList arr = new ArrayList();
//        if (list != null)
//        {
//            out: for (int i = 0; i < list.size(); i++)
//            {
//                TApplylist apply = (TApplylist) list.get(i);
//                for (int j = 0; j < arr.size(); j++)
//                {
//                    TongjiInfo temp = (TongjiInfo) arr.get(j);
//                    if (temp.getApplyUserNameId() == apply.getApplyUsername().longValue())
//                    {
//                        continue out;
//                    }
//                }
//                V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());
//                if (member.getOrgAccountId() == user.getLoginAccount())
//                {
//                    TongjiInfo info = new TongjiInfo();
//                    info.setApplyUserNameId(apply.getApplyUsername());
//                    info.setName(member.getName());
//                    info.setWeek(this.assetManager.getWeekCount(member.getId()));
//                    info.setMonth(this.assetManager.getMonthCount(member.getId()));
//                    info.setTotal(this.assetManager.getTotalCount(member.getId()));
//                    info.setTotalNoBack(this.assetManager.getTotalNoBackCount(member.getId()));
//                    arr.add(info);
//                }
//            }
//        }
        String isIn = request.getParameter("isIn");
        List departList = assetManager.getAssetSummayByDep(false);
        List memberList = assetManager.getAssetSummayByMember(false);
        String filepath = "c:\\excel.xls";
        jxl.write.WritableWorkbook wwb = null;
        try
        {
            wwb = Workbook.createWorkbook(new File(filepath));
            jxl.write.WritableSheet ws = wwb.createSheet(ResourceBundleUtil.getString(
                    Constants.ASSET_RESOURCE_NAME, "asset.tab.tongji", new Object[0]), 0);
            if("1".equals(isIn)){
            	ws.addCell(new jxl.write.Label(0, 0, ResourceBundleUtil.getString(
                    Constants.ASSET_RESOURCE_NAME, "asset.tongji.name", new Object[0])));
            	ws.addCell(new jxl.write.Label(1,0,ResourceBundleUtil.getString(
    					"com.seeyon.v3x.office.auto.resources.i18n.SeeyonAutoResources","auto.column.depart",new Object[0])));
            	ws.addCell(new jxl.write.Label(2, 0, ResourceBundleUtil.getString(
                        Constants.ASSET_RESOURCE_NAME, "asset.label.assetname", new Object[0])));
                ws.addCell(new jxl.write.Label(3, 0, ResourceBundleUtil.getString(
                        Constants.ASSET_RESOURCE_NAME, "asset.tongji.week", new Object[0])));
                ws.addCell(new jxl.write.Label(4, 0, ResourceBundleUtil.getString(
                        Constants.ASSET_RESOURCE_NAME, "asset.tongji.month", new Object[0])));
                ws.addCell(new jxl.write.Label(5, 0, ResourceBundleUtil.getString(
                        Constants.ASSET_RESOURCE_NAME, "asset.tongji.total", new Object[0])));
                ws.addCell(new jxl.write.Label(6, 0, ResourceBundleUtil.getString(
                        Constants.ASSET_RESOURCE_NAME, "asset.tongji.totalnoback", new Object[0])));
                for (int i = 0; i < memberList.size(); i++)
                {
                    Object[] obj = (Object[]) memberList.get(i);
                    if(obj[3] == null){
                    	obj[3] = 0;
                    }
                    if(obj[4] == null){
                    	obj[4] = 0;
                    }
                    if(obj[5] == null){
                    	obj[5] = 0;
                    }
                    if(obj[6] == null){
                    	obj[6] = 0;
                    }
                    ws.addCell(new jxl.write.Label(0, i + 1, String.valueOf(obj[0])));
                    ws.addCell(new jxl.write.Label(1, i + 1, String.valueOf(obj[1])));
                    ws.addCell(new jxl.write.Label(2, i + 1, String.valueOf(obj[2])));
                    ws.addCell(new jxl.write.Label(3, i + 1, String.valueOf(obj[3])));
                    ws.addCell(new jxl.write.Label(4, i + 1, String.valueOf(obj[4])));
                    ws.addCell(new jxl.write.Label(5, i + 1, String.valueOf(obj[5])));
                    ws.addCell(new jxl.write.Label(6, i + 1, String.valueOf(obj[6])));
                }
            }else if("0".equals(isIn)){
				ws.addCell(new jxl.write.Label(0,0,ResourceBundleUtil.getString(
					"com.seeyon.v3x.office.auto.resources.i18n.SeeyonAutoResources","auto.column.depart",new Object[0])));
				ws.addCell(new jxl.write.Label(1, 0, ResourceBundleUtil.getString(
	                    Constants.ASSET_RESOURCE_NAME, "asset.label.assetname", new Object[0])));
	            ws.addCell(new jxl.write.Label(2, 0, ResourceBundleUtil.getString(
	                    Constants.ASSET_RESOURCE_NAME, "asset.tongji.week", new Object[0])));
	            ws.addCell(new jxl.write.Label(3, 0, ResourceBundleUtil.getString(
	                    Constants.ASSET_RESOURCE_NAME, "asset.tongji.month", new Object[0])));
	            ws.addCell(new jxl.write.Label(4, 0, ResourceBundleUtil.getString(
	                    Constants.ASSET_RESOURCE_NAME, "asset.tongji.total", new Object[0])));
	            ws.addCell(new jxl.write.Label(5, 0, ResourceBundleUtil.getString(
	                    Constants.ASSET_RESOURCE_NAME, "asset.tongji.totalnoback", new Object[0])));
	            for (int i = 0; i < departList.size(); i++)
                {
                    Object[] obj = (Object[]) departList.get(i);
                    if(obj[2] == null){
                    	obj[2] = 0;
                    }
                    if(obj[3] == null){
                    	obj[3] = 0;
                    }
                    if(obj[4] == null){
                    	obj[4] = 0;
                    }
                    if(obj[5] == null){
                    	obj[5] = 0;
                    }
                    ws.addCell(new jxl.write.Label(0, i + 1, String.valueOf(obj[0])));
                    ws.addCell(new jxl.write.Label(1, i + 1, String.valueOf(obj[1])));
                    ws.addCell(new jxl.write.Label(2, i + 1, String.valueOf(obj[2])));
                    ws.addCell(new jxl.write.Label(3, i + 1, String.valueOf(obj[3])));
                    ws.addCell(new jxl.write.Label(4, i + 1, String.valueOf(obj[4])));
                    ws.addCell(new jxl.write.Label(5, i + 1, String.valueOf(obj[5])));
                }
            }
            wwb.write();
        }
        catch (Exception ex)
        {
        	log.error(ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                if (wwb != null)
                {
                    wwb.close();
                }
            }
            catch (Exception ex)
            {
            }
        }
        ModelAndView mav = new ModelAndView("office/asset/tongjidownload");
        mav.addObject("path", filepath);
        return mav;
    }
}