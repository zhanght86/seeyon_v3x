package com.seeyon.v3x.office.book.controller;

import java.io.File;
import java.util.ArrayList;
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
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.book.domain.TBookApplyinfo;
import com.seeyon.v3x.office.book.domain.TBookDepartinfo;
import com.seeyon.v3x.office.book.manager.BookManager;
import com.seeyon.v3x.office.book.util.Constants;
import com.seeyon.v3x.office.book.util.str;
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
 * 图书管理相关操作
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @author liusg
 * @version 2008-04-09
 */
public class BookController extends BaseManageController
{
	private static final Log log = LogFactory.getLog(BookController.class);

    private BookManager bookManager;
    
    private AutoManager autoManager; // 车辆管理类

    private OrgManager orgManager;

    private MyApplyManager myApplyManager;

    private AdminManager officeAdminManager;

    private OfficeApplyManager officeApplyManager;

    private UserMessageManager userMessageManager;
    
    private AdminManager adminManager;
    
    // 类别管理Manager
    private OfficeCommonManager officeCommonManager;
    
    public OfficeCommonManager getOfficeCommonManager()
    {
        return officeCommonManager;
    }

    public void setOfficeCommonManager(OfficeCommonManager officeCommonManager)
    {
        this.officeCommonManager = officeCommonManager;
    }

    public void setBookManager(BookManager bookManager)
    {
        this.bookManager = bookManager;
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

    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/book/index");
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 3);
        mav.addObject("admin", checkAdmin);
        return mav;
    }

    public ModelAndView jumpUrl(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String url = request.getParameter("url");
        ModelAndView mav = new ModelAndView(url);
        return mav;
    }

    public ModelAndView regList(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 3);
        if(checkAdmin < 1){
        	return refreshWorkspace();
        }
       /* String countSql = "Select count(*) as " + Constants.Total_Count_Field
                + " From m_book_info Where del_flag = "
                + Constants.Del_Flag_Normal;
        String listSql = "Select * From m_book_info Where del_flag = "
                + Constants.Del_Flag_Normal;
        countSql += " and book_mge = " + user.getId() + " ";
        listSql += " and book_mge = " + user.getId() + " ";*/
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
        /*Map map = null;
        if (!Strings.isBlank(condition) && !Strings.isBlank(keyWord))
        {
        	Object[] o = this.createSearchSql(Integer.parseInt(condition), SQLWildcardUtil.escape(keyWord));
            String searchSql = (String)o[0];
            map = (Map)o[1];
            countSql += " And " + searchSql;
            listSql += " And " + searchSql;
        }
        // 翻页
        // 按日期排序
//        countSql += " order by  create_date desc";
        listSql += " order by  create_date desc";
        
        int size = this.bookManager.getCount(countSql,map);

//        if (!Pagination.isNeedCount())
//        {
            Pagination.setRowCount(size);
//        }
        SQLQuery query = this.bookManager.find(listSql,map);
        query.setFirstResult(Pagination.getFirstResult());
        query.setMaxResults(size);
        query.addEntity(MBookInfo.class);*/
        ModelAndView mav = new ModelAndView("office/book/list_reg");
        
        List permList = this.bookManager.getBookRegList(condition, keyWord, user.getId());
        
        
        mav.addObject("list", permList);
//      Edit for "type of book" by BIANTENG start
        List typeList = this.officeCommonManager.getModelTypes(com.seeyon.v3x.office.common.OfficeModelType.book_type+"", user
                .getLoginAccount());
        mav.addObject("typeList", typeList);
//      Edit for "type of book" by BIANTENG end
        mav.addObject("type", OfficeModelType.book_type);
        return mav;
    }

    public ModelAndView appList(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
    	ModelAndView mav = new ModelAndView("office/book/list_app");
    	User user = CurrentUser.get();
        String departStr = officeApplyManager.getUserModelManagersIds(
        		com.seeyon.v3x.office.common.OfficeModelType.book_type, user);
        List typeList = this.officeCommonManager.getModelTypes(com.seeyon.v3x.office.common.OfficeModelType.book_type+"", user
                .getLoginAccount());
        mav.addObject("typeList", typeList);
        List<Long> departList = null;
        if(Strings.isNotBlank(departStr)){
        	String[] departs = departStr.split(",");
        	if(departs != null ){
            	departList = new ArrayList<Long>();
            	for(String str : departs){
            		departList.add(Long.parseLong(str));
            	}
            }
        }else{
        	return mav;
        }
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
       
        
        List list = this.bookManager.getBookAppList(condition, keyWord, departList);
        mav.addObject("list", list);
        //Edit for "type of book" by BIANTENG start
        
        //Edit for "type of book" by BIANTENG end
        return mav;
    }

    public ModelAndView permList(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 3);
        if(checkAdmin < 1){
        	return refreshWorkspace();
        }
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
        
        Map<String,Boolean> proxy = new HashMap<String,Boolean>();
        
        List<Long> departmentId = this.adminManager.getAdminManageDepartments(user.getId(), user.getLoginAccount(), "___1_");
        
        List list = this.bookManager.getBookPermList(condition, keyWord, user.getId());
        ArrayList<TApplylist> arr = new ArrayList<TApplylist>();
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                TApplylist apply = (TApplylist) list.get(i);
				TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(apply.getApplyId());
				V3xOrgMember member = this.orgManager.getMemberById(apply.getApplyUsername());

				Object[] depproxy  = this.adminManager.getMemberDepProxy(member, user.getLoginAccount(), user.getId(), "___1_",departmentId);
				proxy.put(member.getId().toString(), (Boolean)depproxy[1]);
				apply.setDep_Name(depproxy[0].toString());
				
				apply.setOffice_Id(bookApply.getBookId());
				MBookInfo bookInfo = this.bookManager.getById(bookApply.getBookId());
				apply.setOffice_Name(bookInfo.getBookName());
				apply.setStart_date(bookApply.getBookStart());
				apply.setEnd_date(bookApply.getBookEnd());
				if (bookApply.getApplyCount() != null) {
					apply.setApplyCount(bookApply.getApplyCount());
				}
				switch (apply.getApplyState().intValue()) {
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow: {
					apply.setClassName("td_green");
					break;
				}
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_NotAllow: {
					apply.setClassName("td_red");
					break;
				}
				}
				arr.add(apply);
            }
        }
        ModelAndView mav = new ModelAndView("office/book/list_perm");
        mav.addObject("list", arr);
        mav.addObject("depProxy", proxy);
        return mav;
    }

    public ModelAndView storageList(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 3);
        if(checkAdmin < 1){
        	return refreshWorkspace();
        }
        String idSql = this.officeAdminManager.getInfoIds(user.getId(), 3);
        String[] ids = idSql.split(",");
        List<Long> applyId = null;
        if(ids != null){
        	applyId = new ArrayList<Long>();
        	for(String id : ids){
        		applyId.add(Long.parseLong(id));
        	}
        }
        String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
       
        List list = this.bookManager.getBookStorageList(condition, keyWord, applyId);
        ArrayList arr = new ArrayList();
        List<Long> departmentId = this.adminManager.getAdminManageDepartments(user.getId(), user.getLoginAccount(), "___1_");
        Map<String,Boolean> proxy = new HashMap<String,Boolean>();
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                TApplylist apply = (TApplylist) list.get(i);
                TBookApplyinfo bookApply = this.bookManager
                        .getApplyinfoById(apply.getApplyId());
                V3xOrgMember member = this.orgManager.getMemberById(apply
                        .getApplyUsername());
                
                Object[] depproxy  = this.adminManager.getMemberDepProxy(member, user.getLoginAccount(), user.getId(), "___1_",departmentId);
				proxy.put(member.getId().toString(), (Boolean)depproxy[1]);
				apply.setDep_Name(depproxy[0].toString());
                
                V3xOrgDepartment department = this.orgManager
                        .getDepartmentById(member.getOrgDepartmentId());
                apply.setDep_Name(department.getName());
                apply.setOffice_Id(bookApply.getBookId());
                MBookInfo bookInfo = this.bookManager.getById(bookApply
                        .getBookId());
                apply.setOffice_Name(bookInfo.getBookName());
                apply.setStart_date(bookApply.getBookStart());
                apply.setEnd_date(bookApply.getBookEnd());
                if (bookApply.getApplyCount() != null)
                {
                    apply.setApplyCount(bookApply.getApplyCount());
                }
                try
                {
                    TBookDepartinfo bookDepart = this.bookManager
                            .getDepartinfoById(apply.getApplyId());
                    if (bookDepart != null)
                    {
                        apply.setDepartCount(bookDepart.getBookDepartcount());
                        if (bookDepart.getBookBacktime() != null
                                && bookDepart.getBookBackcount() != null)
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back);
                            apply.setClassName("td_blue");
                        }
                        else if (bookDepart.getBookDeparttime() != null
                                && bookDepart.getBookDepartcount() != null)
                        {
                            apply
                                    .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart);
                            apply.setClassName("td_cyan");
                        }
                    }
                    else
                    {
                        apply
                                .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
                    }
                }
                catch (Exception ex)
                {
                    apply
                            .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
                }
                arr.add(apply);
            }
        }
        ModelAndView mav = new ModelAndView("office/book/list_storage");
        if (request.getAttribute("script") != null
                && ((String) request.getAttribute("script")).length() > 0)
        {
            mav.addObject("script", request.getAttribute("script"));
        }
        mav.addObject("list", arr);
        mav.addObject("depProxy", proxy);
        return mav;
    }

    public ModelAndView create_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        int checkAdmin = this.officeAdminManager.checkAdmin(user.getId(), 3);
        if(checkAdmin < 1){
        	return refreshWorkspace();
        }
        Integer field = 1;
        if (request.getParameter("field") != null
                && request.getParameter("field").length() > 0)
        {
            field = Integer.parseInt(request.getParameter("field"));
        }
        ModelAndView mav = new ModelAndView("office/book/create_reg");
        List typeList = this.officeCommonManager.getModelTypes(com.seeyon.v3x.office.common.OfficeModelType.book_type+"", user
                .getLoginAccount());
        /*SQLQuery query = this.officeAdminManager
                .findAdminSetting("Select * From m_admin_setting Where admin_model like '__1__' and domain_id="
                        + user.getLoginAccount() + " and del_flag = 0");
        // caofei make over like '__1__' 2008 - 9 - 17 
        query.addEntity(MAdminSetting.class);
        List list = query.list();*/
        List list = this.officeAdminManager.getAdminSettingByModelAdmin("__1__", null);
        ArrayList arrAdmin = new ArrayList();
        ArrayList temp = new ArrayList();
        V3xOrgAccount accountById = orgManager.getAccountById(user.getAccountId());
        Long accountid = user.getLoginAccount();
        for (int i = 0; i < list.size(); i++)
        {
            MAdminSetting admin = (MAdminSetting) list.get(i);
            if (temp.contains(admin.getId().getAdmin()))
            {
                continue;
            }
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId()
                    .getAdmin());
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
        //by Yongzhang 2008-05-13
        List fieldList =new ArrayList();
        if(field==2)
        {
            if(bookManager.findBookField()!=null)
            {
                fieldList= bookManager.findBookField();
            }
        }
        mav.addObject("adminList", arrAdmin);
        mav.addObject("typeList", typeList);
        mav.addObject("bookdate", new Date());
        mav.addObject("field", field);
        mav.addObject("fieldList", fieldList);
        if (request.getParameter("show") != null
                && request.getParameter("show").equals("1"))
        {
            mav.addObject("show", 1);
        }
        return mav;
    }

    public ModelAndView doCreate_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        MBookInfo mBookInfo = new MBookInfo();
        mBookInfo.setBookId(UUIDLong.longUUID());
        mBookInfo.setBookName(request.getParameter("book_name"));
        mBookInfo.setBookField(new Integer(Integer.parseInt(request
                .getParameter("book_field"))));
        if (mBookInfo.getBookField() == Constants.Field_Information)
        {
            mBookInfo.setBookCode(request.getParameter("book_code"));
        }
        if (request.getParameter("book_mge") != null
                && request.getParameter("book_mge").length() > 0)
        {
            mBookInfo.setBookMge(Long.parseLong(request
                    .getParameter("book_mge")));
        }
//        mBookInfo.setBookType(request.getParameter("book_type"));
        mBookInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils.getLongParameter(request, "book_type")));
        if (request.getParameter("book_author") != null
                && request.getParameter("book_author").length() > 0)
        {
            mBookInfo.setBookAuthor(request.getParameter("book_author"));
        }
        if (request.getParameter("book_pub") != null
                && request.getParameter("book_pub").length() > 0)
        {
            mBookInfo.setBookPub(request.getParameter("book_pub"));
        }
        if (request.getParameter("book_sum") != null
                && request.getParameter("book_sum").length() > 0)
        {
            mBookInfo.setBookSum(request.getParameter("book_sum"));
        }
        mBookInfo.setBookState(new Integer(Integer.parseInt(request
                .getParameter("book_state"))));
        if (request.getParameter("book_date") != null
                && request.getParameter("book_date").length() > 0)
        {
            mBookInfo.setBookDate(str.strToDate(request
                    .getParameter("book_date")));
        }
        if (request.getParameter("book_price") != null
                && request.getParameter("book_price").length() > 0)
        {
            mBookInfo.setBookPrice(new Double(Double.parseDouble(request
                    .getParameter("book_price"))));
        }
        if (mBookInfo.getBookField() == Constants.Field_Book)
        {
            mBookInfo.setBookCount(Long.parseLong(request
                    .getParameter("book_count")));
        }
        else if (mBookInfo.getBookField() == Constants.Field_Information)
        {
            mBookInfo.setBookCount(Long.parseLong(request
                    .getParameter("book_total")));
        }

        if (request.getParameter("book_avacount") != null
                && request.getParameter("book_avacount").length() > 0)
        {
            mBookInfo.setBookAvacount(Long.parseLong(request
                    .getParameter("book_avacount")));
        }
        mBookInfo.setCreateDate(new Date());
        mBookInfo.setDelFlag(new Integer(Constants.Del_Flag_Normal));
        mBookInfo.setDomainId(user.getLoginAccount());
        ModelAndView mav = new ModelAndView("office/book/create_reg");
        mav.addObject("field", mBookInfo.getBookField());
        try
        {
            this.bookManager.save(mBookInfo);
            mav.addObject("script",
                    "alert(\""
                    + ResourceBundleUtil.getString("com.seeyon.v3x.office.asset.resources.i18n.AssetResources",
                            "asset.alert.success") + "\");parent.listFrame.listIframe.location.reload();parent.listFrame.listIframe.location.reload();");
        }
        catch (Exception ex)
        {
            mav.addObject("script", "alert(\"" + ex.getMessage() + "\");");
        }
        List typeList = this.officeCommonManager.getModelTypes(com.seeyon.v3x.office.common.OfficeModelType.book_type+"", user
                .getLoginAccount());

        /*SQLQuery query = this.officeAdminManager
                .findAdminSetting("Select * From m_admin_setting Where admin_model like '__1__' and domain_id="
                        + user.getLoginAccount() + " and del_flag = 0");
        // caofei make over like '__1__' 2008 - 9 - 17 
        query.addEntity(MAdminSetting.class);
        List list = query.list();*/
        List list = this.officeAdminManager.getAdminSettingByModelAdmin("__1__", user.getLoginAccount());
        ArrayList arrAdmin = new ArrayList();
        ArrayList temp = new ArrayList();
        for (int i = 0; i < list.size(); i++)
        {
            MAdminSetting admin = (MAdminSetting) list.get(i);
            if (temp.contains(admin.getId().getAdmin()))
            {
                continue;
            }
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId()
                    .getAdmin());
            admin.setAdminName(member.getName());
            arrAdmin.add(admin);
            temp.add(admin.getId().getAdmin());
        }
        mav.addObject("adminList", arrAdmin);
        mav.addObject("typeList", typeList);
        return mav;
    }

    public ModelAndView edit_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        String id = request.getParameter("id");
        MBookInfo mBookInfo = this.bookManager.getById(Long.parseLong(id));
        ModelAndView mav = new ModelAndView("office/book/edit_reg");
        if (mBookInfo.getBookMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mBookInfo
                    .getBookMge());
            mav.addObject("bookMge_Id", String.valueOf(mBookInfo.getBookMge()));
            mav.addObject("bookMge_Name", member.getName());
        }
        if (mBookInfo.getBookDate() != null)
        {
            mav.addObject("bookDate", str.dateToStr(mBookInfo.getBookDate()));
        }
        List typeList = this.officeCommonManager.getModelTypes(com.seeyon.v3x.office.common.OfficeModelType.book_type+"", user
                .getLoginAccount());

        /*SQLQuery query = this.officeAdminManager
                .findAdminSetting("Select * From m_admin_setting Where admin_model like '__1__' and domain_id="
                        + user.getLoginAccount() + " and del_flag = 0");
        // caofei make over like '__1__' 2008 - 9 - 17 
        query.addEntity(MAdminSetting.class);
        List list = query.list();*/
        List list = this.officeAdminManager.getAdminSettingByModelAdmin("__1__", null);
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
            V3xOrgMember member = this.orgManager.getMemberById(admin.getId()
                    .getAdmin());
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
        mav.addObject("bean", mBookInfo);
        return mav;
    }

    public ModelAndView doEdit_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        User user = CurrentUser.get();
        long bookId = Long.parseLong(request.getParameter("bookId"));
        MBookInfo mBookInfo = this.bookManager.getById(bookId);
        mBookInfo.setBookName(request.getParameter("book_name"));
        if (request.getParameter("book_mge") != null
                && request.getParameter("book_mge").length() > 0)
        {
            mBookInfo.setBookMge(Long.parseLong(request
                    .getParameter("book_mge")));
        }
        else
        {
            mBookInfo.setBookMge(null);
        }
//        mBookInfo.setBookType(request.getParameter("book_type"));
        mBookInfo.setOfficeType(officeCommonManager.getOfficeTypeInfoById(RequestUtils.getLongParameter(request, "book_type")));
        if (request.getParameter("book_author") != null
                && request.getParameter("book_author").length() > 0)
        {
            mBookInfo.setBookAuthor(request.getParameter("book_author"));
        }
        else
        {
            mBookInfo.setBookAuthor(null);
        }
        if (request.getParameter("book_pub") != null
                && request.getParameter("book_pub").length() > 0)
        {
            mBookInfo.setBookPub(request.getParameter("book_pub"));
        }
        else
        {
            mBookInfo.setBookPub(null);
        }
        if (request.getParameter("book_sum") != null
                && request.getParameter("book_sum").length() > 0)
        {
            mBookInfo.setBookSum(request.getParameter("book_sum"));
        }
        else
        {
            mBookInfo.setBookSum(null);
        }
        if (request.getParameter("book_date") != null
                && request.getParameter("book_date").length() > 0)
        {
            mBookInfo.setBookDate(str.strToDate(request
                    .getParameter("book_date")));
        }
        else
        {
            mBookInfo.setBookDate(null);
        }
        mBookInfo.setBookState(new Integer(Integer.parseInt(request
                .getParameter("book_state"))));
        if (request.getParameter("book_price") != null
                && request.getParameter("book_price").length() > 0)
        {
            mBookInfo.setBookPrice(new Double(Double.parseDouble(request
                    .getParameter("book_price"))));
        }
        else
        {
            mBookInfo.setBookPrice(null);
        }
        if (mBookInfo.getBookField() == 1)
        {
            mBookInfo.setBookCount(Long.parseLong(request
                    .getParameter("book_count")));
        }
        else if (mBookInfo.getBookField() == 2)
        {
            mBookInfo.setBookCount(Long.parseLong(request
                    .getParameter("book_total")));
        }

        if (request.getParameter("book_avacount") != null
                && request.getParameter("book_avacount").length() > 0)
        {
            mBookInfo.setBookAvacount(Long.parseLong(request
                    .getParameter("book_avacount")));
        }
        else
        {
            mBookInfo.setBookAvacount(null);
        }
        mBookInfo.setModifyDate(new Date());
        mBookInfo.setDomainId(user.getLoginAccount());
        request.setAttribute("id", String.valueOf(bookId));
        ModelAndView mav = this.detail_reg(request, response);
        try
        {
            this.bookManager.update(mBookInfo);
            mav.addObject("script",
                    "parent.listFrame.listIframe.location.reload();");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            mav.addObject("script", "alert(\"" + ex.getMessage() + "\");");
        }
        return mav;
    }

    public ModelAndView detail_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String id = request.getParameter("id");
        if (id == null)
        {
            id = (String) request.getAttribute("id");
        }
        String fs = request.getParameter("fs");
        MBookInfo mBookInfo = this.bookManager.getById(Long.parseLong(id));
        ModelAndView mav = new ModelAndView("office/book/detail_reg");
        if (mBookInfo.getBookMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mBookInfo
                    .getBookMge());
            mav.addObject("bookMge_Id", String.valueOf(mBookInfo.getBookMge()));
            mav.addObject("bookMge_Name", member.getName());
        }
        if (fs != null && fs.length() > 0)
        {
            mav.addObject("fs", new Integer(1));
        }
        mav.addObject("bean", mBookInfo);
        return mav;
    }

    public ModelAndView del_reg(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String[] ids = request.getParameterValues("id");
        for (int i = 0; i < ids.length; i++)
        {
            MBookInfo mBookInfo = this.bookManager.getById(Long
                    .parseLong(ids[i]));
            mBookInfo.setDelFlag(Constants.Del_Flag_Delete);
            this.bookManager.update(mBookInfo);
        }
        return this.regList(request, response);
    }

    public ModelAndView create_app(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/book/create_app");
        String id = request.getParameter("id");
        String edit=request.getParameter("edit");
        if (id == null)
        {
            id = (String) request.getAttribute("id");
        }
        if (id == null)
        {
            return mav;
        }
        String fs = request.getParameter("fs");
        MBookInfo mBookInfo = this.bookManager.getById(Long.parseLong(id));
        if (mBookInfo.getBookMge() != null)
        {
            V3xOrgMember member = this.orgManager.getMemberById(mBookInfo
                    .getBookMge());
            mav.addObject("bookMge_Id", String.valueOf(mBookInfo.getBookMge()));
            mav.addObject("bookMge_Name", member.getName());
        }
        if (fs != null && fs.length() > 0)
        {
            mav.addObject("fs", new Integer(1));
        }
        if(edit!=null)
        {
            mav.addObject("edit", "true");
        }
        mav.addObject("bean", mBookInfo);
        return mav;
    }

	public ModelAndView doCreate_app(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long bookId = Long.parseLong(request.getParameter("bookId"));
		String apply_count = request.getParameter("apply_count");
		String book_start = request.getParameter("book_start");
		String book_end = request.getParameter("book_end");
		User user = CurrentUser.get();
		this.bookManager.createApply(bookId, user.getId(), user.getDepartmentId(), apply_count, book_start, book_end);
		ModelAndView mav = new ModelAndView("office/book/create_app");
		mav.addObject("script", "alert(parent.v3x.getMessage(\"officeLang.books_lend_succeed\"));parent.listFrame.listIframe.location.reload();");
		return mav;
	}

    public ModelAndView create_storage(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String applyId = request.getParameter("id");
        String flag = request.getParameter("flag");
        String view =request.getParameter("view");
        ModelAndView mav = new ModelAndView("office/book/create_storage");
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
        	User user = CurrentUser.get();
        	List<Long> departmentId = this.adminManager.getAdminManageDepartments(user.getId(), user.getLoginAccount(), "___1_");
            TApplylist apply = this.myApplyManager.getById(Long
                    .parseLong(applyId));
            TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(Long
                    .parseLong(applyId));
            MBookInfo book = this.bookManager.getById(bookApply.getBookId());
            V3xOrgMember member = this.orgManager.getMemberById(apply
                    .getApplyUsername());
            Object[] depproxy  = this.adminManager.getMemberDepProxy(member, user.getLoginAccount(), user.getId(), "___1_",departmentId);
            
            mav.addObject("depName", depproxy[0]);
            mav.addObject("proxy", depproxy[1]);
            try
            {
                TBookDepartinfo bookDepart = this.bookManager
                        .getDepartinfoById(Long.parseLong(applyId));
                if (bookDepart != null)
                {
                    if (bookDepart.getBookBacktime() != null
                            && bookDepart.getBookBackcount() != null)
                    {
                        apply
                                .setStorageStatus(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back);
                    }
                    if (bookDepart.getBookDeparttime() != null
                            && bookDepart.getBookDepartcount() != null)
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
                    mav.addObject("bookDepartBean", bookDepart);
                }
            }
            catch (Exception ex)
            {
            }
            if(view!=null)
            {
                mav.addObject("view", view);
            }
            mav.addObject("applyBean", apply);
            mav.addObject("bookApplyBean", bookApply);
            mav.addObject("bookBean", book);
            mav.addObject("personName", member.getName());
            String fs = request.getParameter("fs");
            if (fs != null && fs.length() > 0)
            {
                mav.addObject("fs", new Integer(1));
            }
        }
        return mav;
    }

    public ModelAndView create_perm(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String applyId = request.getParameter("id");
        ModelAndView mav = new ModelAndView("office/book/create_perm");
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
        	User user = CurrentUser.get();
            TApplylist apply = this.myApplyManager.getById(Long
                    .parseLong(applyId));
            TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(apply
                    .getApplyId());
            MBookInfo book = this.bookManager.getById(bookApply.getBookId());
            V3xOrgMember member = this.orgManager.getMemberById(apply
                    .getApplyUsername());
            List<Long> departmentId = this.adminManager.getAdminManageDepartments(user.getId(), user.getLoginAccount(), "___1_");
            Object[] ob = this.adminManager.getMemberDepProxy(member, user.getAccountId(), user.getId(), "___1_", departmentId);
            mav.addObject("proxy", ob[1]);
            mav.addObject("depName", ob[0]);
            mav.addObject("applyBean", apply);
            mav.addObject("bookApplyBean", bookApply);
            mav.addObject("bookBean", book);
            mav.addObject("personName", member);
            String fs = request.getParameter("fs");
            if (fs != null && fs.length() > 0)
            {
                mav.addObject("fs", new Integer(1));

            }
            String show = request.getParameter("show");
            if (null != show && show.length() > 0) mav.addObject("show", new Integer(1));
        }
        return mav;
    }

	/**
	 * 审批图书资料
	 */
	public ModelAndView doCreate_perm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean fromPortal = "portal".equals(request.getParameter("from"));
		
		String applyId = request.getParameter("applyId");
		String apply_state = request.getParameter("apply_state");
		String apply_memo = request.getParameter("apply_memo");
		TApplylist apply = this.myApplyManager.getById(Long.parseLong(applyId));
		TBookApplyinfo bookApply = this.bookManager.getApplyinfoById(Long.parseLong(applyId));
		MBookInfo book = this.bookManager.getById(bookApply.getBookId());
		apply.setApplyState(Integer.parseInt(apply_state));
		apply.setApplyMemo(apply_memo);
		apply.setAuditTime(new java.util.Date());
		this.myApplyManager.update(apply);
		OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, apply.getApplyId());
		String applyState = "";
		if (Integer.parseInt(apply_state) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow) {
			book.setBookAvacount(book.getBookAvacount() - bookApply.getApplyCount());
			applyState = ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.label.permallowed");
		} else {
			applyState = ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.label.permnotallowed");
		}
		this.bookManager.update(book);

		MessageReceiver receiver = MessageReceiver.get(new Long(-1), apply.getApplyUsername());
		MessageContent content = MessageContent.get("office.book.audit", book.getBookName(), applyState);
		try {
			userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.office, CurrentUser.get().getId(), receiver);
		} catch (MessageException e) {
			log.error("", e);
		}
		
		ModelAndView mav = new ModelAndView("office/book/create_perm");
		StringBuffer sb = new StringBuffer();
		if (fromPortal) {
			sb.append("parent.window.returnValue = \"true\";\n");
			sb.append("parent.window.close();\n");
		} else {
			sb.append("parent.listFrame.listIframe.location.reload();\n");
		}
		mav.addObject("script", sb.toString());
		return mav;
	}

    public ModelAndView doCreate_storage(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long applyId = Long.parseLong(request.getParameter("applyId"));
        String book_departtime = request.getParameter("book_departtime");
        String book_departcount = request.getParameter("book_departcount");
        String book_backtime = request.getParameter("book_backtime");
        String book_backcount = request.getParameter("book_backcount");
        TBookDepartinfo bookDepart = null;
        try
        {
            bookDepart = this.bookManager.getDepartinfoById(applyId);
            TBookApplyinfo bookApply = this.bookManager
                    .getApplyinfoById(applyId);
            MBookInfo book = this.bookManager.getById(bookApply.getBookId());
            if (request.getParameter("depart_memo") != null
                    && request.getParameter("depart_memo").length() > 0)
            {
                bookDepart.setDepartMemo(request.getParameter("depart_memo"));
            }
            if (book_departtime != null && book_departtime.length() > 0)
            {
                bookDepart.setBookDeparttime(str.strToDate(book_departtime));
            }
            if (book_departcount != null && book_departcount.length() > 0)
            {
                bookDepart.setBookDepartcount(new Long(Long
                        .parseLong(book_departcount)));
            }
            if (book_backtime != null && book_backtime.length() > 0)
            {
                bookDepart.setBookBacktime(str.strToDate(book_backtime));
            }
            if (book_backcount != null && book_backcount.length() > 0)
            {
                bookDepart.setBookBackcount(new Long(Long
                        .parseLong(book_backcount)));
            }
            this.bookManager.update(bookDepart);
            book.setBookAvacount(new Long(book.getBookAvacount().longValue()
                    + Long.parseLong(book_backcount)));
            this.bookManager.update(book);
        }
        catch (org.hibernate.ObjectNotFoundException ex)
        {
            bookDepart = new TBookDepartinfo();
            TBookApplyinfo bookApply = this.bookManager
                    .getApplyinfoById(applyId);
            MBookInfo book = this.bookManager.getById(bookApply.getBookId());
            bookDepart.setApplyId(applyId);
            bookDepart.setBookId(bookApply.getBookId());
            bookDepart.setDelFlag(new Integer(Constants.Del_Flag_Normal));
            if (request.getParameter("depart_memo") != null
                    && request.getParameter("depart_memo").length() > 0)
            {
                bookDepart.setDepartMemo(request.getParameter("depart_memo"));
            }
            if (book_departtime != null && book_departtime.length() > 0)
            {
                bookDepart.setBookDeparttime(str.strToDate(book_departtime));
            }
            if (book_departcount != null && book_departcount.length() > 0)
            {
                bookDepart.setBookDepartcount(new Long(Long
                        .parseLong(book_departcount)));
            }
            if (book_backtime != null && book_backtime.length() > 0)
            {
                bookDepart.setBookBacktime(str.strToDate(book_backtime));
            }
            if (book_backcount != null && book_backcount.length() > 0)
            {
                bookDepart.setBookBackcount(new Long(Long
                        .parseLong(book_backcount)));
            }
            this.bookManager.save(bookDepart);
            book.setBookAvacount(new Long(book.getBookAvacount().longValue()
                    + bookApply.getApplyCount().longValue()
                    - Long.parseLong(book_departcount)));
            this.bookManager.update(book);
        }
        ModelAndView mav = new ModelAndView("office/book/create_storage");
        mav.addObject("script",
                "parent.listFrame.listIframe.location.reload();");
        return mav;
    }

    /**
     * 管理员删除图书资料申请
     */
	public ModelAndView del_perm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] ids = request.getParameterValues("id");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				TApplylist apply = this.myApplyManager.getById(Long.parseLong(ids[i]));
				if (apply.getApplyState() == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait && apply.getApplyUsername() != CurrentUser.get().getId()) {
					request.setAttribute("script", "alert('" + ResourceBundleUtil.getString(com.seeyon.v3x.office.asset.util.Constants.ASSET_RESOURCE_NAME, "book.alert.delete.apply") + "');");
				} else {
					apply.setDelFlag(Constants.Del_Flag_Delete);
					this.myApplyManager.update(apply);
					//删除审批人待办
    		        OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, Long.valueOf(ids[i]));
					request.setAttribute("script", "alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.alert.delet.prompt") + "');");
				}
			}
		}
		return this.permList(request, response);
	}

    public ModelAndView tongji(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        ModelAndView mav = new ModelAndView("office/book/tongji");
        String isIn = request.getParameter("isIn");
        List departList = new ArrayList();
        List memberList = new ArrayList();
        if(Strings.isBlank(isIn) || "1".equals(isIn)){
        	isIn = "1";
        	memberList = bookManager.getBookSummayByMember(true);
        } else {
        	departList = bookManager.getBookSummayByDep(true);
        }
        mav.addObject("isIn", isIn);
        //mav.addObject("list", pageArr);
        mav.addObject("departList", departList);
        mav.addObject("memberList", memberList);
        return mav;
    }

    public ModelAndView tongjiDownload(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        //删除“我的申请”的关联，不受用户删除记录的控制 借出/归还 by 2008-04-30 by Yongzhang
//        User user = CurrentUser.get();
//        Map<String,Object> map = new HashMap<String,Object>();
//        String listSql = "Select * From t_applylist Where";
////      + Constants.Del_Flag_Normal;
//		listSql += " apply_type=:applyType and apply_state > :applyState ";
//		map.put("applyType", 4);
//		map.put("applyState", 1);
//		SQLQuery query = this.bookManager.find(listSql,map);
//        // query.setFirstResult(Pagination.getFirstResult());
//        // query.setMaxResults(Pagination.getMaxResults());
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
//                    if (temp.getApplyUserNameId() == apply.getApplyUsername()
//                            .longValue())
//                    {
//                        continue out;
//                    }
//                }
//                V3xOrgMember member = this.orgManager.getMemberById(apply
//                        .getApplyUsername());
//                if (member.getOrgAccountId() == user.getLoginAccount())
//                {
//                    TongjiInfo info = new TongjiInfo();
//                    info.setApplyUserNameId(apply.getApplyUsername());
//                    info.setName(member.getName());
//                    info.setWeek(this.bookManager.getWeekCount(member.getId()));
//                    info.setMonth(this.bookManager
//                            .getMonthCount(member.getId()));
//                    info.setTotal(this.bookManager
//                            .getTotalCount(member.getId()));
//                    info.setTotalNoBack(this.bookManager
//                            .getTotalNoBackCount(member.getId()));
//                    arr.add(info);
//                }
//            }
//        }
        List departList = bookManager.getBookSummayByDep(false);
        List memberList = bookManager.getBookSummayByMember(false);
        String isIn = request.getParameter("isIn");
        String filepath = "c:\\excel.xls";
        jxl.write.WritableWorkbook wwb = null;
        try
        {
            wwb = Workbook.createWorkbook(new File(filepath));
            jxl.write.WritableSheet ws = wwb.createSheet(ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tab.tongji"), 0);
            if(isIn.equals("1")){
            	ws.addCell(new jxl.write.Label(0, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.name")));
            	ws.addCell(new jxl.write.Label(1, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.auto.resources.i18n.SeeyonAutoResources", "auto.column.depart")));
            	ws.addCell(new jxl.write.Label(2, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.label.infoname")));
                ws.addCell(new jxl.write.Label(3, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.week")));
                ws.addCell(new jxl.write.Label(4, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.month")));
                ws.addCell(new jxl.write.Label(5, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.total")));
                ws.addCell(new jxl.write.Label(6, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.totalnoback")));
    				for (int i = 0; i < memberList.size(); i++) {
    					Object[] obj = (Object[])memberList.get(i);
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
            }else if(isIn.equals("0")){
            	 ws.addCell(new jxl.write.Label(0, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.auto.resources.i18n.SeeyonAutoResources", "auto.column.depart")));
            	 ws.addCell(new jxl.write.Label(1, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.label.infoname")));
                 ws.addCell(new jxl.write.Label(2, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.week")));
                 ws.addCell(new jxl.write.Label(3, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.month")));
                 ws.addCell(new jxl.write.Label(4, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.total")));
                 ws.addCell(new jxl.write.Label(5, 0, ResourceBundleUtil.getString("com.seeyon.v3x.office.book.resources.i18n.BookResources", "book.tongji.totalnoback")));
     				for (int i = 0; i < departList.size(); i++) {
     					Object[] obj = (Object[])departList.get(i);
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
            ex.printStackTrace();
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
        ModelAndView mav = new ModelAndView("office/book/tongjidownload");
        mav.addObject("path", filepath);
        return mav;
    }

	public void setAdminManager(AdminManager adminManager) {
		this.adminManager = adminManager;
	}

    /*private Object[] createSearchSql(int condition, String keyWord)
    {
        String searchSql = "";
        Object [] object = new Object[2];
        Map map = new HashMap();
        switch (condition)
        {
            case Constants.Search_Condition_BookName:
            {
                searchSql = "book_name like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookType:
            {
                searchSql = "book_type like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookStat:
            {
                searchSql = "book_state = :keyword " ;
                map.put("keyword", new Integer(keyWord));
                break;
            }
            case Constants.Search_Condition_BookAuthor:
            {
                searchSql = "book_author like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookPub:
            {
                searchSql = "book_pub like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_Department:
            {
                searchSql = "apply_depId = :keyword ";
                map.put("keyword", new Long(keyWord));
                break;
            }
            case Constants.Search_Condition_Member:
            {
                searchSql = "apply_username in(select mem.id from  v3x_org_member mem where mem.name like :keyword )";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_ApplyStat:
            {
                searchSql = "apply_state = :keyword ";
                map.put("keyword", new Integer(keyWord));
                break;
            }
            case Constants.Search_Condition_SorageStat:
            {
                if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart)
                {
                    searchSql = "apply_id in (Select apply_id From t_book_departinfo Where book_departtime is not null And book_backtime is null)";
                    break;
                }
                else if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back)
                {
                    searchSql = "apply_id in (Select apply_id From t_book_departinfo Where book_backtime is not null)";
                    break;
                }
                else if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow)
                {
                    searchSql = "apply_id not in (Select apply_id From t_book_departinfo)";
                    break;
                }
            }
            case Constants.Search_Condition_BookField:
            {
                searchSql = "book_field = " + keyWord;
                map.put("keyword", new Integer(keyWord));
                break;
            }
        }
        object[0] = searchSql;
        object[1] = map;
        return object;
    }

    // 审批时候的按条件查询
    private Object[] createSearchSqlForPer(int condition, String keyWord)
    {
        String searchSql = "";
        Object[] object = new Object[2];
        Map map = new HashMap();
        switch (condition)
        {
            case Constants.Search_Condition_BookName:
            {
                searchSql = " apply_id in (select apply_id from t_book_applyinfo Where book_id in "
                        + " ( Select book_id From m_book_info where book_name like :keyword ) ) ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookType:
            {
                searchSql = "book_type like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookStat:
            {
                searchSql = "book_state = " + keyWord;
                map.put("keyword", new Integer(keyWord));
                break;
            }
            case Constants.Search_Condition_BookAuthor:
            {
                searchSql = "book_author like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_BookPub:
            {
                searchSql = "book_pub like :keyword ";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_Department:
            {
                searchSql = "apply_depId = :keyword " ;
                map.put("keyword", new Long(keyWord));
                break;
            }
            case Constants.Search_Condition_Member:
            {
                searchSql = "apply_username in(select mem.id from  v3x_org_member mem where mem.name like :keyword )";
                map.put("keyword", "%"+keyWord+"%");
                break;
            }
            case Constants.Search_Condition_ApplyStat:
            {
                searchSql = "apply_state = :keyword ";
                map.put("keyword", new Integer(keyWord));
                break;
            }
            case Constants.Search_Condition_SorageStat:
            {
                if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart)
                {
                    searchSql = "apply_id in (Select apply_id From t_book_departinfo Where book_departtime is not null And book_backtime is null)";
                    break;
                }
                else if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back)
                {
                    searchSql = "apply_id in (Select apply_id From t_book_departinfo Where book_backtime is not null)";
                    break;
                }
                else if (Integer.parseInt(keyWord) == com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow)
                {
                    searchSql = "apply_id not in (Select apply_id From t_book_departinfo)";
                    break;
                }
            }
            case Constants.Search_Condition_BookField:
            {
                searchSql = "book_field = :keyword " ;
                map.put("keyword", new Integer(keyWord));
                break;
            }
        }
        object[0] = searchSql;
        object[1] = map;
        return object;
    }*/

}