package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.metadata.userdefined.domain.MetadataComparator;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.SetContentType;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-23
 */
/**
 * @author Administrator
 */
@CheckRoleAccess(roleTypes = {RoleType.Administrator, RoleType.AccountEdocAdmin, RoleType.FormAdmin, RoleType.SystemAdmin})
public class MetaDataMgrController extends BaseController {

    private static final Log log = LogFactory.getLog(MetaDataMgrController.class);

    private MetadataManager metadataManager;

    private OrgManagerDirect orgManagerDirect;

    private OrgManager orgManager;

    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/index");
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView systemMetadataIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/systemMetadataIndex");
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView mainIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/main");
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView systemMetadataMainIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/systemMetadataMain");
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView systemMetadataTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMtadataTree");
        List<Metadata> allMetadatasList = metadataManager.getAllSystemMetadatasForSystemOpera();
        mav.addObject("metadatasList", allMetadatasList);
        return mav;
    }

    @CheckRoleAccess(roleTypes = {RoleType.Administrator, RoleType.AccountEdocAdmin, RoleType.SystemAdmin})
    public ModelAndView systemMetadataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMtadataList");
        List<Metadata> metadatasList = metadataManager.getAllCanEditlSystemMetadatas();
        mav.addObject("metadatasList", pagenate(metadatasList));
        return mav;
    }

    @CheckRoleAccess(roleTypes = {RoleType.Administrator, RoleType.AccountEdocAdmin, RoleType.SystemAdmin})
    public ModelAndView querySystmeMetadataByNameLike(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String textfield = request.getParameter("textfield") == null ? "" : request.getParameter("textfield");
    	ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMtadataList");
        List<Metadata> metadatasList = metadataManager.getAllCanEditlSystemMetadatas();
        List<Metadata> resultList = new ArrayList<Metadata>() ;
        textfield = URLDecoder.decode(textfield);
        if (Strings.isNotBlank(textfield)) {
        	 for (Metadata data : metadatasList) {
             	data.setLabel(ResourceBundleUtil.getString(data.getResourceBundle(), data.getLabel())) ;
             	if (data.getLabel().contains(textfield)) 
             		resultList.add(data);
             }
        	 mav.addObject("metadatasList", pagenate(resultList));
        } else {
        	 mav.addObject("metadatasList", pagenate(metadatasList));
        }
        mav.addObject("textfield", textfield);
        return mav;
    }
    
    public ModelAndView systemMetadataListItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMtadataItemList");
        String idStr = request.getParameter("id");
        if(idStr != null && !"".equals(idStr)) {
            Long id = Long.parseLong(idStr);
            Metadata metadata = metadataManager.getMetadata(id);
            List<MetadataItem> metadataItemsList = metadata.getItems();
            mav.addObject("metadata", metadata);
            mav.addObject("metadataItemsList", pagenate(metadataItemsList));
        }
        return mav;
    }

    public ModelAndView querySystmeMetadataItemByNameLike(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMtadataItemList");
    	String textfield = request.getParameter("textfield") == null ? "" : request.getParameter("textfield");
        String metadataId = request.getParameter("metadataId");
        
        textfield = URLDecoder.decode(textfield);
        if(metadataId != null && !"".equals(metadataId)) {
            Long id = Long.parseLong(metadataId);
            Metadata metadata = metadataManager.getMetadata(id);
            List<MetadataItem> metadataItemsList = metadata.getItems();
            List<MetadataItem> resultList = new ArrayList<MetadataItem>() ;
            for (MetadataItem data : metadataItemsList) {
             	if (ResourceBundleUtil.getString(metadata.getResourceBundle(), data.getLabel()).contains(textfield)) 
             		resultList.add(data);
             }
            mav.addObject("metadata", metadata);
            mav.addObject("metadataItemsList", pagenate(resultList));
        }
        mav.addObject("textfield", textfield);
        return mav;
    }    
    
    public ModelAndView metadataItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/listFrame");
        String idStr = request.getParameter("id");
        if(idStr != null && !"".equals(idStr)) {
            Long id = Long.parseLong(idStr);
            Metadata metadata = metadataManager.getMetadata(id);
            List<MetadataItem> metadataItemsList = metadata.getItems();
            mav.addObject("metadata", metadata);
            mav.addObject("metadataItemsList", pagenate(metadataItemsList));
        }
        return mav;
    }

    /**
     * 添加单位的显示 显示左边元数据树
     */
    public ModelAndView metadataTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/treeFrame");
        boolean sysFlag = this.isNotGroup();
        Long account_id = null;
        List<Metadata> allMetadatasList = null;
        if(sysFlag) {
            account_id = this.getOrgId();
            allMetadatasList = metadataManager.getAllOrgAoMetadata(account_id); // 得到的是本单位的元数据
            mav.addObject("systemFlag", "ORG");
        } else {
            List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
            mav.addObject("categoryHTML", this.categoryHTML(accountlist));
            mav.addObject("systemFlag", "GROUP");
            String account_ids = request.getParameter("org_account_id");
            if(Strings.isNotBlank(account_ids)) {
                account_id = Long.valueOf(account_ids);
                allMetadatasList = metadataManager.getAllOrgAoMetadata(account_id); // 得到的是本单位的元数据
            }
        }
        List<MetadataItem> metadataItemList = metadataManager.getMetadataItemByMetadata(allMetadatasList);
        /*mav.addObject("condition", "choice");
        mav.addObject("metaData", "metaData");
        mav.addObject("metaDataDisplay", "metaDataDisplay");*/
        mav.addObject("account_id", account_id);
        mav.addObject("metadatasList", allMetadatasList);
        mav.addObject("metadataItemList", metadataItemList);
        return mav;
    }
    
    /**
     * @name : 模糊查询单位枚举 
     * @desc : 单位枚举中没有国际化
     */
    @SuppressWarnings("unchecked")
	public ModelAndView queryMetaDataLikeByCorp(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	 ModelAndView mav = new ModelAndView("sysMgr/metadata/treeFrame");
    	 String condition = request.getParameter("condition");
    	 String textfield = request.getParameter("textfield");
		 boolean sysFlag = this.isNotGroup();
         Long account_id = null;
         List<Metadata> allMetadatasList = null;
         List<MetadataItem> metadataItemList = null ;
         if(sysFlag) {
             account_id = this.getOrgId();
             allMetadatasList = metadataManager.getAllOrgAoMetadata(account_id); // 得到的是本单位的元数据
             mav.addObject("systemFlag", "ORG");
         } else {
        	 // 取得所有单位
             List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
             mav.addObject("categoryHTML", this.categoryHTML(accountlist));
             mav.addObject("systemFlag", "GROUP");
             String account_ids = request.getParameter("account_id");
             if(Strings.isNotBlank(account_ids)) {
                 account_id = Long.valueOf(account_ids);
                 allMetadatasList = metadataManager.getAllOrgAoMetadata(account_id); // 得到的是本单位的元数据
             }
         }
         
         metadataItemList = metadataManager.getMetadataItemByMetadata(allMetadatasList);
         
         if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)){
         	Object[] obj = queryMetadataByCondition(allMetadatasList, metadataItemList, condition, textfield);
         	allMetadatasList = (List<Metadata>)obj[0];
         	metadataItemList = (List<MetadataItem>)obj[1];
         } 
         Collections.sort(allMetadatasList);
         Collections.sort(metadataItemList);
         mav.addObject("condition", condition);
         mav.addObject("textfield", textfield);
         mav.addObject("metadataItemList", metadataItemList);
         mav.addObject("metadatasList", allMetadatasList);
         mav.addObject("account_id", account_id);
             
         return mav;
    }
    
    private StringBuffer categoryHTML(List<V3xOrgAccount> resultlist) {
        StringBuffer categoryHTML = new StringBuffer();
        boolean sysFlag = this.isNotGroup();
        if(sysFlag) {
            for(V3xOrgAccount v3xOrgAccount : resultlist) {
                if(v3xOrgAccount.getIsRoot()) {
                    continue;
                }
                categoryHTML.append("<option value='" + v3xOrgAccount.getId() + "'>");
                categoryHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                categoryHTML.append(Strings.toHTML(v3xOrgAccount.getName()) + "</option>\n");
            }
        } else {
            category2HTML(resultlist, Long.valueOf(-1), categoryHTML, 1);
        }
        return categoryHTML;
    }

    private StringBuffer category2HTML(List<V3xOrgAccount> resultlist, Long parentIds, StringBuffer categoryHTML2, int leave) {
        for(V3xOrgAccount webV3xOrgAccount : resultlist) {
            Long parentId = webV3xOrgAccount.getSuperior();
            if(parentId == parentIds || (parentId != null && parentId.equals(parentIds))) {
                if(!(webV3xOrgAccount.getSuperior().equals(Long.valueOf(-1)))) {
                    categoryHTML2.append("<option value='" + webV3xOrgAccount.getId() + "'>");
                    for(int i = 0; i < leave; i++) {
                        categoryHTML2.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                    categoryHTML2.append(Strings.toHTML(webV3xOrgAccount.getName()) + "</option>\n");
                }
                category2HTML(resultlist, webV3xOrgAccount.getId(), categoryHTML2, leave + 1);
            }
        }
        return categoryHTML2;
    }

    public ModelAndView editMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/editMetadata");
        String metadataId = request.getParameter("metadataId");
        Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
        mav.addObject("metadata", metadata);
        mav.addObject("metadataId", metadataId);
        return mav;
    }

    public ModelAndView addMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/editMetadata");
        Long mId = 0L;
        try {
            Metadata md = newMetadataFromRequset(request);
            mId = metadataManager.addMetadata(md, true);
        } catch(Exception e) {
            log.error("", e);
        }
        String szJs = "<script>parent.addMetadataOver('" + mId + "');</script>";
        response.getWriter().print(szJs);
        return mav;
    }

    private Metadata newMetadataFromRequset(HttpServletRequest request) {
        int app = Integer.parseInt(request.getParameter("app"));
        String metadataName = request.getParameter("metadataName");
        int sort = Integer.parseInt(request.getParameter("sort"));
        String metadataType = "1";
        String description = request.getParameter("description");
        Metadata md = new Metadata();
        md.setCanEdit(true);
        md.setCategory(app);
        md.setDescription(description);
        md.setExtend(true);
        md.setIdIfNew();
        md.setIsRef(1);
        md.setIsSystem(0);
        md.setLabel(metadataName);
        md.setName(metadataName);
        md.setResourceBundle(null);
        md.setRole(metadataType);
        md.setSort(sort);
        md.setState(1);
        md.setType(1);
        return md;
    }

    /**
     * 编辑元数据项
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView editMetadataItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/editFrame");
        String id = request.getParameter("id");
        String categoryId = request.getParameter("categoryId");
        Long metadataId = Long.parseLong(categoryId);
        Metadata metadata = metadataManager.getMetadata(metadataId);
        mav.addObject("metadata", metadata);
        if(Strings.isNotBlank(id)) {
            Long metadataItemId = Long.parseLong(id);
            MetadataItem metadataItem = metadataManager.getMetadataItem(metadata, metadataItemId);
            mav.addObject("metadataItem", metadataItem);
            EdocManager edocManager = (EdocManager)ApplicationContextHolder.getBean("edocManager");
            User user = CurrentUser.get();
            if(metadata.getCategory() == ApplicationCategoryEnum.edoc.getKey()) {
                boolean hasRefValue = true;
                if(metadataItem.getIsSystem() != 1) {
                    hasRefValue = edocManager.useMetadataValue(user.getLoginAccount(), metadataId, metadataItem.getValue());
                }
                mav.addObject("hasRefValue", hasRefValue);
            }
        } else {
            int sortNumber = getBiggestSortNumber(metadata, metadata.getName());
            int valueNumber = getBiggestValue(metadata, metadata.getName());
            mav.addObject("sortNumber", sortNumber);
            mav.addObject("valueNumber", valueNumber);
            mav.addObject("changeType", "add");
        }
        return mav;
    }

    /**
     * 更新元数据项
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView updateMetadataItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String isSystem = request.getParameter("isSystem");
        String parentId = request.getParameter("parentId");
        String parenType = request.getParameter("parenType");
        String label = request.getParameter("label");
        String value = request.getParameter("value");
        String orgItemValue = request.getParameter("orgItemValue"); // 元数据项的原值，用来和当前值做判断，如果二者相等
        String sortStr = request.getParameter("sort");
        String description = request.getParameter("description");
        Integer inputSwitch = RequestUtils.getIntParameter(request, "inputSwitch");
        Integer outputSwitch = RequestUtils.getIntParameter(request, "outputSwitch");
        if(null != inputSwitch && inputSwitch.intValue() == Constants.METADATAITEM_SWITCH_ENABLE) {
            outputSwitch = Constants.METADATAITEM_SWITCH_ENABLE;
        }
        Integer sort = 0;
        if(!Strings.isBlank(sortStr)) {
            sort = Integer.parseInt(sortStr);
        }
        MetadataItem metadataItem = null;
        if(Strings.isNotBlank(id)) {
            long metadataItemId = Long.parseLong(id);
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(parentId));
            if(metadata == null) {
                return null;
            }
            metadataItem = metadataManager.getMetadataItem(metadata, metadataItemId);
            if(metadataItem == null) {
                return null;
            }
            if(Strings.isBlank(value)) {
                value = metadataItem.getValue();
            }
            if(Strings.isBlank(label)) {
                label = metadataItem.getLabel();
            }
            if(!checkDupleValue(parentId, parenType, metadataItemId, new BigDecimal(value))) {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.error.dupleValue") + "');");
                out.println("history.back();");
                out.println("</script>");
                return null;
            }
            metadataItem.setLabel(label);
            metadataItem.setState(inputSwitch);
            metadataItem.setSort(sort);
            metadataItem.setOutputSwitch(outputSwitch);
            metadataItem.setValue(value);
            if("metadata".equals(parenType)) {
                // Metadata metadata =
                // metadataManager.getMetadata(Long.valueOf(parentId));
                metadataManager.updateMetadataItem(metadata, metadataItem, true);
            } else if("metadataItem".equals(parenType)) {
                metadataManager.updateMetadataItem(null, metadataItem, true);
            }
            /**
             * out.println("parent.toolbarFram.treeSelectId ='"+parentId+"';");
             * Long counterParentId = null ; if("metadata".equals(parenType)){
             * counterParentId =
             * metadataManager.getMetadata(Long.valueOf(parentId)).getParentid()
             * ; }else if("metadataItem".equals(parenType)){ counterParentId =
             * metadataManager
             * .getMetadataItemById(Long.valueOf(parentId)).getMetadataId() ;
             * if(counterParentId == null){ counterParentId =
             * metadataManager.getMetadataItemById
             * (Long.valueOf(parentId)).getParentId() ; } }
             * out.println("parent.toolbarFram.parentId ='"
             * +counterParentId+"';"); out.println(
             * "parent.treeFrame.location.href = parent.treeFrame.location.href"
             * ); out.println("parent.treeFrame.location.reload()");
             **/
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("parent.listFrame.location.reload(true)");
            out.println("</script>");
        } else {
            if(checkDupleValue(parentId, parenType, null, new BigDecimal(value))) {
                Metadata metadata = metadataManager.getMetadata(Long.valueOf(parentId));
                if(metadata == null) {
                    return null;
                }
                metadataItem = new MetadataItem();
                metadataItem.setLabel(label);
                metadataItem.setState(inputSwitch);
                metadataItem.setSort(sort);
                metadataItem.setOutputSwitch(outputSwitch);
                metadataItem.setValue(value);
                metadataItem.setParentId(Long.valueOf(parentId));
                metadataItem.setName(metadata.getName());
                metadataManager.addMetadataItem(Long.valueOf(parentId), parenType, metadataItem, true);
            } else {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.error.dupleValue") + "');");
                out.println("history.back();");
                out.println("</script>");
                return null;
            }
            /**
             * 判断是否连续添加
             */
            String isContinue = request.getParameter("continue");
            if(isContinue != null && isContinue.equals("true")) {
                super.rendJavaScript(response, "parent.doEnd('" + id + "','true')");
            } else {
                super.rendJavaScript(response, "parent.doEnd('" + id + "','false')");
            }
        }
        return null;
    }

    /**
     * 删除元数据项
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView deleteMetadataItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String categoryId = request.getParameter("categoryId");
        Long metadataId = Long.parseLong(categoryId);
        Metadata metadata = metadataManager.getMetadata(metadataId);
        // MetadataNameEnum metadataNameEnum =
        // MetadataNameEnum.valueOf(metadata.getName());
        String[] metadataItemIdsStr = request.getParameterValues("metadataItemIds");
        if(!metadataManager.checkDelAll(metadata, metadataItemIdsStr)) {
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataValue.all") + "');");
            out.println("parent.location.reload(true);");
            out.println("</script>");
            return null;
        }
        String szUseItemName = "";
        for(String idStr : metadataItemIdsStr) {
            long metadataItemId = Long.parseLong(idStr);
            MetadataItem mi = metadataManager.getMetadataItem(metadata, metadataItemId);
            if(mi.getIsSystem() != null && mi.getIsSystem().intValue() == Constants.METADATAITEM_ISSYSTEM_YES) {
                ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.system.resources.i18n.SysMgrResources", CurrentUser.get().getLocale());
                String szMsg = ResourceBundleUtil.getString(r, "metadata.manager.error.system");
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + szMsg + "')");
                out.println("parent.location.reload(true)");
                out.println("</script>");
                return null;
            }
            /*
             * EdocManager edocManager =
             * (EdocManager)ApplicationContextHolder.getBean("edocManager");
             * if(edocManager.useMetadataValue(-1L, metadataId, mi.getValue()))
             * { if(!"".equals(szUseItemName)){szUseItemName+=",";}
             * szUseItemName+=mi.getValue(); continue; }
             */
        }
        metadataManager.deleteMetadataItem("metadata", categoryId, metadataItemIdsStr, true);
        String szMsg = Constants.getString4CurrentUser("system.manager.ok");
        if(!"".equals(szUseItemName)) {
            szMsg = Constants.getString4CurrentUser("system.metadata.isuse", szUseItemName);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        if(!"".equals(szUseItemName)) {
            out.println("alert('" + szMsg + "')");
        }
        out.println("parent.location.reload(true)");
        out.println("</script>");
        return null;
    }

    /**
     * 返回新建元数据的排序号
     * @param name
     * @return
     * @deprecated
     */
    private int getBiggestSortNumber(Metadata m, String name) {
        List<MetadataItem> allmetalst = null;
        if(m != null) {
            allmetalst = m.getItems();
        } else {
            allmetalst = metadataManager.getMetadataItems(MetadataNameEnum.valueOf(name));
        }
        int maxNumber = -1;
        for(MetadataItem metadataItem : allmetalst) {
            Integer currentSort = metadataItem.getSort();
            if(null != currentSort) {
                int currentSortNumber = metadataItem.getSort().intValue();
                if(currentSortNumber > maxNumber) {
                    maxNumber = currentSortNumber;
                }
            }
        }
        return maxNumber + 1;
    }

    /**
     * 返回新建元数据的值
     * @param name
     * @return
     * @deprecated
     */
    private int getBiggestValue(Metadata m, String name) {
        List<MetadataItem> allmetalst = null;
        if(m != null) {
            allmetalst = m.getItems();
        } else {
            allmetalst = metadataManager.getMetadataItems(MetadataNameEnum.valueOf(name));
        }
        int maxValue = -1;
        for(MetadataItem metadataItem : allmetalst) {
            int currentValue = 0;
            try {
                currentValue = Integer.parseInt(metadataItem.getValue());
            } catch(Exception e) {
                continue;
            }
            if(currentValue > maxValue) {
                maxValue = currentValue;
            }
        }
        return maxValue + 1;
    }

    /**
     * 新建的时候得到最大的排序号
     * @return
     */
    private int getBiggestSortNumber(Long parentId) {
        List<Metadata> allMetadatasList = metadataManager.getAllUserDefinedMetadatasForSystemOpera();
        List<Metadata> allReturnMetadatasList = this.getMetadataForSortNum(allMetadatasList, parentId);
        int maxNumber = 0;
        for(Metadata metadata : allReturnMetadatasList) {
            Integer currentSort = metadata.getSort();
            if(null != currentSort) {
                int currentSortNumber = metadata.getSort().intValue();
                if(currentSortNumber > maxNumber) {
                    maxNumber = currentSortNumber;
                }
            }
        }
        return maxNumber + 1;
    }

    private List<Metadata> getMetadataForSortNum(List<Metadata> list, Long parentId) {
        User user = CurrentUser.get();
        List<Metadata> allReturnMetadatasList = new ArrayList<Metadata>();
        if(list == null) {
            return allReturnMetadatasList;
        }
        if(user.isSystemAdmin()) {
            for(Metadata metadata : list) {
                if(metadata.getOrg_account_id() == null && parentId.equals(metadata.getParentid()) && (metadata.getIs_formEnum().intValue() == 1)) {
                    allReturnMetadatasList.add(metadata);
                }
            }
        } else {
            for(Metadata metadata : list) {
                if(metadata.getOrg_account_id() != null && parentId.equals(metadata.getParentid()) && Long.valueOf(user.getLoginAccount()).equals(metadata.getOrg_account_id()) &&
                // (metadata.getIs_formEnum().intValue() == 0 ||
                // metadata.getIs_formEnum().intValue() == 1)){
                    (metadata.getIs_formEnum().intValue() == 1)) { // 限制是枚举而不是枚举类型：
                                                                   // metadata.getIs_formEnum==1，即只取枚举数据的最大排序号
                    allReturnMetadatasList.add(metadata);
                }
            }
        }
        return allReturnMetadatasList;
    }

    /**
     * 校验重复值
     * @param name
     * @param value
     * @return true合法 private boolean checkDupleValue(String name, int value){
     *         List<MetadataItem> allmetalst =
     *         metadataManager.getMetadataItems(MetadataNameEnum.valueOf(name));
     *         for(MetadataItem metadataItem : allmetalst){ String valueStr =
     *         String.valueOf(value); String currentValue =
     *         String.valueOf(metadataItem.getValue()); //int currentValue =
     *         Integer.parseInt(metadataItem.getValue());
     *         if(currentValue.equals(valueStr)){ return false; } } return true;
     *         }
     */
    /**
     * 校验重复值----新建的时候
     * @param name
     * @param value
     * @return true合法
     */
    @Deprecated
    private boolean checkDupleValue(Metadata m, int value) {
        List<MetadataItem> allmetalst = m.getItems();
        for(MetadataItem metadataItem : allmetalst) {
            String valueStr = String.valueOf(value);
            String currentValue = String.valueOf(metadataItem.getValue());
            // int currentValue = Integer.parseInt(metadataItem.getValue());
            if(currentValue.equals(valueStr)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验重复值
     * @param name
     * @param value
     * @return true无重值
     */
    private boolean checkDupleValue(Metadata m, Long metaItemId, BigDecimal value) {
        if(m == null) {
            return false;
        }
        List<MetadataItem> allmetalst = m.getItems();
        String valueStr = String.valueOf(value);
        for(MetadataItem metadataItem : allmetalst) {
            String currentValue = String.valueOf(metadataItem.getValue());
            if(currentValue.equals(valueStr)) {
                if(metaItemId != null && metadataItem.getId().longValue() == metaItemId.longValue()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkDupleValue(String parendId, String parenType, Long metaItemId, BigDecimal value) throws Exception {
        if(Strings.isBlank(parendId)) {
            return false;
        }
        if("metadata".equals(parenType)) {
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(parendId));
            if(metadata == null) {
                return false;
            }
            return checkDupleValue(metadata, metaItemId, value);
        }
        if("metadataItem".equals(parenType)) {
            MetadataItem parent = metadataManager.getMetadataItemById(Long.valueOf(parendId));
            if(parent == null) {
                return false;
            }
            List<MetadataItem> list = metadataManager.getChildItemByItemId(parent);
            if(list == null) {
                return true;
            }
            for(MetadataItem metadataItem : list) {
                if(metadataItem.getValue().equals(String.valueOf(value))) {
                    if(metaItemId != null && metaItemId.longValue() == metadataItem.getId().longValue()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 以下为自定义枚举部分（原表单枚举）
     */
    public ModelAndView userDefinedtoobar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/metadataToolbar");
        mav.addObject("from", request.getParameter("from"));
        mav.addObject("treeSelectId", request.getParameter("id"));
        return mav;
    }

    /**
     * 单位管理员的入口
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView orgShowMetdata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/accountMetadata");
        // mav.addObject("from", request.getParameter("from")) ;
        return mav;
    }

    public ModelAndView showOrgMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showOrgMetadata_index");
        mav.addObject("from", request.getParameter("from"));
        return new ModelAndView("sysMgr/metadata/showOrgMetadata_index");
    }
    
    @SetContentType
    public ModelAndView xmlForTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		
		StringBuilder xmlstr = new StringBuilder("");
		if(Metadata.REF.equalsIgnoreCase(type)){
			Long metadataId = Long.parseLong(id);
			Metadata metadata = metadataManager.getMetadata(metadataId);
			if(metadata.getIs_formEnum() != null && metadata.getIs_formEnum().intValue() == 0){
				List<Metadata> metadataList = metadataManager.getChildMetada(metadataId);
				Collections.sort(metadataList);
				for (Metadata meta : metadataList) {
					xmlstr.append(getMetadataXml(meta));
				}
			} else {
				List<MetadataItem> metadataItemList = metadataManager.getLevelItemOfMetadata(metadata,1);
				Collections.sort(metadataItemList);
				for (MetadataItem item : metadataItemList) {
					xmlstr.append(getMetadataItemXml(item));
				}
			}
		} else if(MetadataItem.REF.equalsIgnoreCase(type)){
			Long metadataItemId = Long.parseLong(id);
			MetadataItem metadataItem = metadataManager.getMetadataItemById(metadataItemId);
			List<MetadataItem> metadataItemList = metadataManager.getChildItemByItemId(metadataItem);
			Collections.sort(metadataItemList);
			for (MetadataItem item : metadataItemList) {
				xmlstr.append(getMetadataItemXml(item));
			}
		}
		
		out.println("<tree text=\"loaded\">");
		out.println(xmlstr.toString());
		out.println("</tree>");
		out.close();
		return null;
	}
    
    private String getMetadataXml(Metadata meta) throws Exception {
    	if(meta == null){
    		return "";
    	}
    	StringBuilder xmlstr = new StringBuilder("");
    	xmlstr.append("<tree businessId=\"" + meta.getId()
			+ "\" icon=\"/seeyon/common/js/xtree/images/foldericon.png\""
			+ " openIcon=\"/seeyon/common/js/xtree/images/foldericon.png\""
			+ " text=\"" + Strings.toXmlStr(meta.getLabel())
			+ "\" src=\"/seeyon/metadata.do?method=xmlForTree"
			+ "&amp;id=" + meta.getId()
			+ "&amp;type=Metadata\" " 
			+ "action=\"javascript:showValuesList('"+meta.getId()+"')\" "
			+ "extendAttributes=\"is_formEnum:"+meta.getIs_formEnum()+",sort:"+meta.getSort()+",type:metadata\""
			+ " />");
    	return xmlstr.toString();
    }
    
    private String getMetadataItemXml(MetadataItem item) throws Exception {
    	if(item == null){
    		return "";
    	}
    	StringBuilder xmlstr = new StringBuilder("");
    	//List<MetadataItem> metadataItemList = metadataManager.getChildItemByItemId(item);
    	String img = "foldericon.png";
//    	if(CollectionUtils.isEmpty(metadataItemList)){
//    		img = "file.png";
//    	}
    	xmlstr.append("<tree businessId=\"" + item.getId()
			+ "\" icon=\"/seeyon/common/js/xtree/images/" + img + "\""
			+ " openIcon=\"/seeyon/common/js/xtree/images/" + img + "\""
			+ " text=\"" + Strings.toXmlStr(item.getLabel())
			+ "\" src=\"/seeyon/metadata.do?method=xmlForTree"
			+ "&amp;id=" + item.getId()
			+ "&amp;type=MetadataItem\" "
			+ "action=\"javascript:showChildOfMetadataItem('"+item.getId()+"','metadataItem')\" "
			+ "extendAttributes=\"parentid:"+item.getParentId()+",metadataId:"+item.getMetadataId()+",type:metadataItem,isref:"+item.getIsRef()+"\""
			+" />");
    	return xmlstr.toString();
    }
    
    @SuppressWarnings("unchecked")
	public ModelAndView showOrgMetadataTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showOrgMetadata_tree");
        User user = CurrentUser.get();
        Long accountId = Long.valueOf(user.getLoginAccount());
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        List<Metadata> metadatasList = metadataManager.getAllOrgAoMetadata(accountId);
        List<MetadataItem> metadataItemList = metadataManager.getMetadataItemByMetadata(metadatasList);
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)){
        	Object[] obj = queryMetadataByCondition(metadatasList, metadataItemList, condition, textfield);
        	metadatasList = (List<Metadata>)obj[0];
        	metadataItemList = (List<MetadataItem>)obj[1];
        } 
        Collections.sort(metadatasList);
        Collections.sort(metadataItemList);
        mav.addObject("condition", condition);
        mav.addObject("textfield", textfield);
        mav.addObject("metadataItemList", metadataItemList);
        mav.addObject("metadatasList", metadatasList);
        mav.addObject("acconutId", accountId);
        return mav;
    }
    
    /**
     * 根据条件查询出枚举和枚举值
     * @param metadatasList 枚举List
     * @param metadataItemList 枚举值List
     * @param condition 条件
     * @param textfield 条件值
     * @return object[0] 枚举 ， object[1]  枚举值
     * @throws Exception
     */
    private Object[] queryMetadataByCondition(List<Metadata> metadatasList, List<MetadataItem> metadataItemList, 
    		String condition, String textfield)  throws Exception{
    	List<Metadata> metadatas = new ArrayList<Metadata>() ; 			// 存放枚举
    	List<MetadataItem> metadataItems = new ArrayList<MetadataItem>() ;	// 存放枚举值
    	if ("metaData".equals(condition)){
        	Set<Long> parentIdSet = new HashSet<Long>();
        	for (Metadata data : metadatasList) {
				if (data.getLabel().contains(textfield) && data.getIs_formEnum() == 1){
					metadatas.add(data);
					Long parentId = data.getParentid();
					if(parentId != 0){
						parentIdSet.add(parentId);
					}
				}
			}
        	for(Metadata metadata : metadatas){
        		metadataItems.addAll(metadataManager.getMetadataItemByMetadata(metadata));
        	}
        	
        	for(Long id : parentIdSet){
        		metadatas.add(metadataManager.getMetadata(id));
        	}
        } else if("metaDataDisplay".equals(condition)){
        	Map<Long, MetadataItem> metadataItemsMap = new HashMap<Long, MetadataItem>();
        	Set<Long> itemParentIdSet = new HashSet<Long>();
        	for (MetadataItem data : metadataItemList) {
				if (data.getLabel().contains(textfield)) {
					metadataItemsMap.put(data.getId(), data);
					Long itemParentId = data.getParentId();
					if(itemParentId != null){
						itemParentIdSet.add(itemParentId);
					}
				}  
			}
        	getAllParentMetadataItem(itemParentIdSet, metadataItemsMap);
        	
        	Collection<MetadataItem> c = metadataItemsMap.values();
            for (Iterator<MetadataItem> it = c.iterator(); it.hasNext();) {
            	metadataItems.add(it.next());
            }
        	
            Set<Long> metadataIdSet = new HashSet<Long>();
            for(MetadataItem item : metadataItems){
            	Long metadataId = item.getMetadataId();
				if(metadataId != 0){
					metadataIdSet.add(item.getMetadataId());
				}
			}
        	for(Long metadataId : metadataIdSet){
        		metadatas.add(metadataManager.getMetadata(metadataId));
        	}
        	Set<Long> parentIdSet = new HashSet<Long>();
        	for(Metadata metadata : metadatas){
        		Long parentId = metadata.getParentid();
				if(parentId != 0){
					parentIdSet.add(parentId);
				}
        	}
        	for(Long id : parentIdSet){
        		metadatas.add(metadataManager.getMetadata(id));
        	}
        }
    	return new Object[]{metadatas, metadataItems};
    }
    
    /**
     * 查找ids中所有父枚举值放到metadataItemsMap中（只查找有parentId的枚举值）
     * @param ids
     * @param metadataItemsMap
     * @throws Exception
     */
    private void getAllParentMetadataItem(Set<Long> ids, Map<Long, MetadataItem> metadataItemsMap) throws Exception{
    	if(ids != null && ids.size() != 0){
    		Set<Long> itemParentIdSet = new HashSet<Long>();
	    	for(Long itemId : ids){
	    		if(metadataItemsMap.get(itemId) == null){
	    			MetadataItem item = metadataManager.getMetadataItemById(itemId);
	    			metadataItemsMap.put(itemId, item);
	    			Long itemParentId = item.getParentId();
					if(itemParentId != null){
						itemParentIdSet.add(itemParentId);
					}
	    		}
	    	}
	    	getAllParentMetadataItem(itemParentIdSet, metadataItemsMap);
    	}
    }

    public ModelAndView showOrgMetadataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showOrgMetadata_listMetadata");
        User user = CurrentUser.get();
        Long accountId = Long.valueOf(user.getLoginAccount());
        List<Metadata> allMetadatasList = null;
        String metadataId = request.getParameter("metadataId");
        if(metadataId != null) {
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
            if(metadata == null) {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataType") + "');");
                out.println("history.back();");
                // out.println("window.parent.listFrame.location.reload(true)");
                out.println("window.parent.treeFrame.location.reload(true);");
                out.println("</script>");
                return null;
            }
        }
        // String metadataType = request.getParameter("metadataType") ;
        String isSystem = request.getParameter("isSystem");
        if(isSystem == null) {
            isSystem = "false";
        }
        if(user.isAdministrator()) {
            mav.addObject("userType", "accountAdmin");
        } else {
            mav.addObject("userType", "");
        }
        allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, accountId);
        // 增加排序功能
        if(allMetadatasList != null) {
            Collections.sort(allMetadatasList, new MetadataComparator());
        }
        /*
         * List<Metadata> list = new ArrayList<Metadata>(); for(Metadata meta :
         * allMetadatasList){ list.add(meta); }
         */
        mav.addObject("metadatasList", pagenate(allMetadatasList));
        mav.addObject("allmetadatasList", allMetadatasList);
        return mav;
    }

    public ModelAndView queryOrgUnitMetadataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("sysMgr/metadata/showOrgMetadata_listMetadata");
    	String textfield = request.getParameter("textfield") == null ? "" : request.getParameter("textfield");
        String metadataId = request.getParameter("metadataId");
        Long accountId = Long.valueOf(CurrentUser.get().getAccountId());
        List<Metadata> allMetadatasList = null;
        
        textfield = URLDecoder.decode(textfield);
        List<Metadata> result = new ArrayList<Metadata>();
        if(metadataId != null && !"".equals(metadataId)) {
            allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, accountId);
            if(Strings.isBlank(textfield)){
            	result = allMetadatasList;
            }else {
	            for(Metadata data : allMetadatasList){
	            	if(data.getLabel().contains(textfield))
	            		result.add(data);
	            }
            }
            
            if(CollectionUtils.isNotEmpty(result)) {
                Collections.sort(result, new MetadataComparator());
            }
            mav.addObject("metadatasList", pagenate(result));
            mav.addObject("allmetadatasList", result);
        }
        mav.addObject("textfield", textfield);
        return mav;
    }
    
    public ModelAndView showSystemMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showSystemMetadata");
        User user = CurrentUser.get();
        if(user.isAdministrator()) {
            mav.addObject("userType", "accountAdmin");
        } else {
            mav.addObject("userType", "");
        }
        return mav;
    }

    @CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
    public ModelAndView userDefinedindex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_index");
        mav.addObject("from", request.getParameter("from"));
        return new ModelAndView("sysMgr/metadata/userdefined_index");
    }

    public ModelAndView userDefinedmainIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/userdefined_main");
    }

    public ModelAndView userDefinedmetadataItemList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_listFrame");
        String parentId = request.getParameter("parentId");
        String type = request.getParameter("parentType");
        List<MetadataItem> metadataItemsList = null;
        if(Strings.isNotBlank(parentId) && "metadata".equals(type)) {
            Long id = Long.parseLong(parentId);
            Metadata metadata = metadataManager.getMetadata(id);
            if(metadata == null) {
                PrintWriter out = response.getWriter();
                out.println("<Script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadata") + "');");
                out.println("history.back();");
                out.println("window.parent.treeFrame.location.reload(true);");
                out.println("</Script>");
                return null;
            }
            metadataItemsList = metadata.getItems();
            mav.addObject("metadata", metadata);
            if(metadata.getOrg_account_id() != null) {
                mav.addObject("org_account_id", metadata.getOrg_account_id());
            }
        } else if(Strings.isNotBlank(parentId) && "metadataItem".equals(type)) {
            String org_account_id = request.getParameter("org_account_id");
            if(Strings.isNotBlank(org_account_id)) {
                mav.addObject("org_account_id", org_account_id);
            }
            MetadataItem metadataItem = metadataManager.getMetadataItemById(Long.valueOf(parentId));
            if(metadataItem == null) {
                PrintWriter out = response.getWriter();
                out.println("<Script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataValue") + "');");
                out.println("history.back();");
                out.println("window.parent.treeFrame.location.reload(true);");
                out.println("</Script>");
                return null;
            }
            metadataItemsList = metadataManager.getChildItemByItemId(metadataItem);
        }
        // 增加排序功能
        if(metadataItemsList != null) {
            Collections.sort(metadataItemsList);
        }
        User user = CurrentUser.get();
        if(user.isSystemAdmin()) {
            mav.addObject("userType", "SystemAdmin");
        } else {
            mav.addObject("org_account_id", user.getLoginAccount());
            mav.addObject("userType", "user");
        }
        mav.addObject("metadataItemsList", pagenate(metadataItemsList));
        mav.addObject("metadataItemsList2", metadataItemsList);
        mav.addObject("parentId", parentId);
        mav.addObject("parentType", type);
        return mav;
    }

    /**
     * 显示左边元数据树
     */
    public ModelAndView userDefinedmetadataTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_treeFrame");
        List<Metadata> metadataNamesList = null ;
        List<Metadata> metadatasList = metadataManager.getAllOrgAoMetadata(null);
        metadataNamesList = metadatasList ;
        List<MetadataItem> metadataItemList = metadataManager.getMetadataItemByMetadata(metadatasList);
        mav.addObject("condition", "choice");
        mav.addObject("metadataNamesList", metadataNamesList);
        mav.addObject("metadataItemList", metadataItemList);
        mav.addObject("metadatasList", metadatasList);
        return mav;
    }
    
    /**
     * 根据条件查看左边树的枚举值  
     * 条件： 枚举值、枚举显示值
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public ModelAndView queryMetaDataByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	ModelAndView mav = null;
    	Long accountId = null;
    	
    	//获得国际化文字
    	Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
		String tab = ResourceBundleUtil.getString(resource, local, "metadata.manager.account");//
    	
		//判断此时选项卡参数
    	String tabCategory = request.getParameter("tabCategory");
    	//公共枚举采用和系统管理员一样的方法
    	if(tab.equals(tabCategory)){//看选项卡，如果是公共枚举，就用文龙原来的，如果是单位枚举，就用设置accountId=user.getLoginAccount()
    		accountId = Long.valueOf(user.getLoginAccount());
    		mav = new ModelAndView("sysMgr/metadata/showOrgMetadata_tree");
    	}else{
    		mav = new ModelAndView("sysMgr/metadata/userdefined_treeFrame");
    	}
    	
    	List<Metadata> metadatasList = metadataManager.getAllOrgAoMetadata(accountId);
        List<MetadataItem> metadataItemList = metadataManager.getMetadataItemByMetadata(metadatasList);
        
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)){
        	Object[] obj = queryMetadataByCondition(metadatasList, metadataItemList, condition, textfield);
        	metadatasList = (List<Metadata>)obj[0];
        	metadataItemList = (List<MetadataItem>)obj[1];
        }
        
        Collections.sort(metadatasList);
        Collections.sort(metadataItemList);
        mav.addObject("condition", condition);
        mav.addObject("textfield", textfield);
        mav.addObject("metadataItemList", metadataItemList);
        mav.addObject("metadatasList", metadatasList);
    	
    	List<Metadata> metadataNamesList = metadataManager.getAllOrgAoMetadata(accountId) ;
    	mav.addObject("metadataNamesList", metadataNamesList);
    	
    	return mav ;
    }

    public ModelAndView editUserDefinedMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_editMetadata");
        String metadataId = request.getParameter("metadataId");
        Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
        if(metadata == null) {
            PrintWriter out = response.getWriter();
            out.println("<Script>");
            out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadata") + "');");
            out.println("history.back();");
            // out.println("window.parent.listFrame.location.reload(true)");
            out.println("window.parent.treeFrame.location.reload(true);");
            out.println("</Script>");
            return super.refreshWorkspace();
        }
        mav.addObject("metadata", metadata);
        mav.addObject("metadataId", metadataId);
        return mav;
    }

    /**
     * 删除系统分类 xgghen
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView deleetMetadataFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataId = request.getParameter("metadataId");
        String szUseItemName = "";
        long metadataID = Long.parseLong(metadataId);
        List<Metadata> allchildMetada = metadataManager.getChildMetada(metadataID);
        for(Metadata metada : allchildMetada) {
            metadataManager.deleteMetadata(metada);
        }
        // 删除这个分类
        Metadata metadata = metadataManager.getMetadata(metadataID);
        metadataManager.deleteMetadata(metadata);
        String szMsg = Constants.getString4CurrentUser("system.manager.ok");
        if(!"".equals(szUseItemName)) {
            szMsg = Constants.getString4CurrentUser("system.metadata.isuse", szUseItemName);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + szMsg + "')");
        out.println("parent.location.href = parent.location");
        out.println("</script>");
        return null;
    }

    public ModelAndView updateUserDefinedMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataId = request.getParameter("metadataId");
        String metadataName = request.getParameter("metadataName");
        String sort = request.getParameter("sort");
        String description = request.getParameter("description");
        String parentId = request.getParameter("parentId");
        System.out.println(parentId);
        Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
        if(null == metadata) {
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.deleted") + "');");
            out.println("history.back();");
            out.println("</script>");
            return null;
        }
        metadata.setDescription(description);
        metadata.setLabel(metadataName);
        metadata.setSort(Integer.valueOf(sort));
        metadataManager.updateMetadata(metadata, false);
        PrintWriter out = response.getWriter();
        out.println("<script>");
        // out.println("alert('" +
        // Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("window.parent.listFrame.location.reload(true)");
        out.println("var condition = parent.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.treeFrame.location.href = parent.treeFrame.location.href;");
        out.println("parent.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("</script>");
        return null;
    }

    public ModelAndView updateMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataId = request.getParameter("metadataId");
        String sort = request.getParameter("sort");
        String description = request.getParameter("description");
        Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
        metadata.setDescription(description);
        metadata.setSort(Integer.valueOf(sort));
        metadataManager.updateMetadata(metadata, true);
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("parent.listFrame.location.reload(true)");
        out.println("</script>");
        return null;
    }

    public ModelAndView userDefinedmetadataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_listMetadata");
        List<Metadata> allMetadatasList = null;
        String metadataId = request.getParameter("parentId");
        if(metadataId != null) {
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
            if(metadata == null) {
                PrintWriter out = response.getWriter();
                out.println("<Script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataType") + "');");
                out.println("history.back();");
                // out.println("window.parent.listFrame.location.reload(true)");
                out.println("window.parent.treeFrame.location.reload(true);");
                out.println("</Script>");
                return null;
            }
        }
        User user = CurrentUser.get();
        String isSystem = request.getParameter("isSystem");
        String org_account_id = request.getParameter("org_account_id");
        if(isSystem == null) {
            isSystem = "true";
        }
        if(Strings.isBlank(org_account_id) && user.isSystemAdmin()) {
            allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, null);
        } else if(Strings.isBlank(org_account_id) && !user.isSystemAdmin()) {
            allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, null);
        } else {
            allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, Long.valueOf(org_account_id));
            mav.addObject("account_id", org_account_id);
        }
        // 增加排序功能
        if(allMetadatasList != null) {
            Collections.sort(allMetadatasList, new MetadataComparator());
        }
        if(user.isSystemAdmin()) {
            mav.addObject("userType", "SystemAdmin");
        } else
            mav.addObject("userType", "user");
        if(this.isNotGroup()) {
            mav.addObject("systemFlag", "ORG");
        }
        mav.addObject("metadatasList", pagenate(allMetadatasList));
        mav.addObject("allmetadatasList", allMetadatasList);
        return mav;
    }

    public ModelAndView userDefinedmetadataListRep(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_listMetadata");
        List<Metadata> allMetadatasList = null;
        String metadataId = request.getParameter("metadataId");
        // String metadataType = request.getParameter("metadataType") ;
        if(metadataId != null) {
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataId));
            if(metadata == null) {
                PrintWriter out = response.getWriter();
                out.println("<Script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataType") + "');");
                out.println("history.back();");
                // out.println("window.parent.listFrame.location.reload(true)");
                out.println("window.parent.treeFrame.location.reload(true);");
                out.println("</Script>");
                return null;
            }
        }
        User user = CurrentUser.get();
        String isSystem = request.getParameter("isSystem");
        String org_account_id = request.getParameter("org_account_id");
        if(isSystem == null) {
            isSystem = "true";
        }
        if(org_account_id == null || org_account_id.equals("")) {
            // allMetadatasList = metadataManager.getAllRootMetadatas(metadataId
            // , null);
        } else {
            mav.addObject("account_id", org_account_id);
            allMetadatasList = metadataManager.getAllRootMetadatas(metadataId, Long.valueOf(org_account_id));
        }
        if(user.isSystemAdmin()) {
            mav.addObject("userType", "SystemAdmin");
        } else
            mav.addObject("userType", "user");
        /*
         * List<Metadata> list = new ArrayList<Metadata>(); for(Metadata meta :
         * allMetadatasList){ list.add(meta); }
         */
        mav.addObject("metadatasList", pagenate(allMetadatasList));
        mav.addObject("allmetadatasList", allMetadatasList);
        return mav;
    }

    /**
     * 判断当前的版本是否是集团版
     * @return
     */
    private boolean isNotGroup() {
        boolean flag = true;
        if(SysFlag.sys_isGroupVer.getFlag().equals(true)) {
            flag = false;
        }
        return flag;
    }

    /**
     * 用于得到是企业版的单位的ID
     * @return
     */
    private Long getOrgId() throws Exception {
        List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
        boolean sysFlag = this.isNotGroup();
        if(sysFlag) {
            for(V3xOrgAccount v3xOrgAccount : accountlist) {
                if(v3xOrgAccount.getIsRoot()) {
                    continue;
                }
                return v3xOrgAccount.getId();
            }
        }
        return null;
    }

    public ModelAndView metadataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/listMetadata");
        List<Metadata> allMetadatasList = new ArrayList<Metadata>();
        List<Metadata> returnList = new ArrayList<Metadata>();
        Long org_account_id = this.getOrgId();
        if(org_account_id != null) {
            allMetadatasList = this.metadataManager.getAllOrgAoMetadata(org_account_id);
            for(Metadata metadata : allMetadatasList) {
                if(metadata.getIs_formEnum().intValue() == 1 && metadata.getParentid().intValue() == 0)
                    returnList.add(metadata);
            }
            mav.addObject("systemFlag", "ORG");
        }
        mav.addObject("metadatasList", pagenate(returnList));
        return mav;
    }

    public ModelAndView newUserDefinededMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_newMetadata");
        String metadataId = request.getParameter("metadataId");
        String id = request.getParameter("id");
        String selectName = request.getParameter("selectName");
        String isSystem = request.getParameter("isSystem");
        mav.addObject("metadataId", metadataId);
        mav.addObject("isSystem", isSystem);
        mav.addObject("parentId", id);
        mav.addObject("selectName", selectName);
        mav.addObject("sortNUM", this.getBiggestSortNumber(Long.valueOf(id)));
        return mav;
    }

    public ModelAndView userDefinedaddMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_editMetadata");
        Long mId = 0L;
        try {
            Metadata md = newMetadataFromRequset(request);
            mId = metadataManager.addMetadata(md, true);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        String szJs = "<script>parent.addMetadataOver('" + mId + "');</script>";
        response.getWriter().print(szJs);
        return mav;
    }

    /**
     * 修改用户自定义元数据项
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView editUserDefinedItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_editFrame");
        String id = request.getParameter("itemId");
        String selectType = request.getParameter("selectType");
        String parentId = request.getParameter("parentId");
        if(Strings.isNotBlank(id)) {
            Long metadataItemId = Long.parseLong(id);
            MetadataItem metadataItem = metadataManager.getMetadataItemById(metadataItemId);
            if(metadataItem == null) {
                PrintWriter out = response.getWriter();
                out.println("<Script>");
                out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataValue") + "');");
                out.println("history.back();");
                out.println("window.parent.listFrame.location.reload(true)");
                out.println("</Script>");
                return null;
            }
            mav.addObject("metadataItem", metadataItem);
        } else {
            int sortNumber = 1;
            int valueNumber = 1;
            mav.addObject("sortNumber", sortNumber);
            mav.addObject("valueNumber", valueNumber);
        }
        mav.addObject("parentType", selectType);
        mav.addObject("parentId", parentId);
        return mav;
    }

    public ModelAndView updateUserDefinedItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String parentId = request.getParameter("parentId");
        String parentType = request.getParameter("parentType");
        String orgItemValue = request.getParameter("orgItemValue"); // 元数据项的原值，用来和当前值做判断，如果二者相等
        String label = request.getParameter("label");
        String value = request.getParameter("value");
        String sortStr = request.getParameter("sort");
        Integer inputSwitch = RequestUtils.getIntParameter(request, "inputSwitch");
        Integer outputSwitch = RequestUtils.getIntParameter(request, "outputSwitch");
        String isRef = request.getParameter("isRef");
        boolean bool = false;
        if(!Strings.isBlank(isRef) && Integer.valueOf(isRef).intValue() == com.seeyon.v3x.common.metadata.Constants.METADATATIEM_ISREF_YES) {
            bool = true;// 被引用
        }
        if(null != inputSwitch && inputSwitch.intValue() == Constants.METADATAITEM_SWITCH_ENABLE) {
            outputSwitch = Constants.METADATAITEM_SWITCH_ENABLE;
        }
        Integer sort = 0;
        if(Strings.isNotBlank(sortStr)) {
            sort = Integer.parseInt(sortStr);
        }
        /**
         * Long metadataId = Long.parseLong(categoryId); Metadata metadata =
         * metadataManager.getMetadata(metadataId); if(null == metadata){
         * PrintWriter out = response.getWriter(); out.println("<script>");
         * out.println("alert('" +
         * Constants.getString4CurrentUser("metadata.manager.deleted") + "');");
         * out.println("history.back();"); out.println("</script>"); return
         * null; }
         ****/
        if(Strings.isNotBlank(id)) {
            long metadataItemId = Long.parseLong(id);
            MetadataItem metadataItem = metadataManager.getMetadataItemById(metadataItemId);
            if(null == metadataItem) {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.deleted") + "');");
                out.println("history.back();");
                out.println("</script>");
                return null;
            }
            if(Strings.isBlank(value)) {
                value = metadataItem.getValue();
            }
            if(!checkDupleValue(parentId, parentType, metadataItemId, new BigDecimal(value))) {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.error.dupleValue") + "');");
                out.println("history.back();");
                out.println("</script>");
                return null;
            }
            if(Strings.isBlank(label)) {
                label = metadataItem.getLabel();
            }
            metadataItem.setLabel(label);
            metadataItem.setState(inputSwitch);
            metadataItem.setSort(sort);
            metadataItem.setOutputSwitch(outputSwitch);
            metadataItem.setValue(value);
            if("metadata".equals(parentType)) {
                Metadata metadata = metadataManager.getMetadata(Long.valueOf(parentId));
                metadataManager.updateMetadataItem(metadata, metadataItem, false);
            } else if("metadataItem".equals(parentType)) {
                metadataManager.updateMetadataItem(null, metadataItem, false);
            }
        }
        PrintWriter out = response.getWriter();
        Long counterParentId = null;
        if("metadata".equals(parentType)) {
            counterParentId = metadataManager.getMetadata(Long.valueOf(parentId)).getParentid();
        } else {
            counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getMetadataId();
            if(counterParentId == null || counterParentId.equals(0L)) {
                counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getParentId();
            }
        }
        out.println("<script>");
        out.println("parent.toolbarFram.treeSelectId ='" + parentId + "';");
        out.println("parent.toolbarFram.parentId ='" + counterParentId + "';");
        out.println("var condition = parent.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.treeFrame.location.href = parent.treeFrame.location.href;");
        out.println("parent.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("parent.listFrame.location.reload(true)");
        out.println("</script>");
        return null;
    }

    public ModelAndView userDefinedednewMetadataItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/userdefined_newItem");
        String parentType = request.getParameter("parentType");
        int sortNumber = 1;
        int valueNumber = 1;
        if(Strings.isNotBlank(parentType) && "metadata".equals(parentType)) {
            String parentId = request.getParameter("parentId");
            mav.addObject("parentId", parentId);
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(parentId));
            if(null != metadata) {
                mav.addObject("metadata", metadata);
                sortNumber = getMaxSortNumber(metadata.getItems());
                valueNumber = getMaxValue(metadata.getItems());
            }
        }
        if(Strings.isNotBlank(parentType) && "metadataItem".equals(parentType)) {
            String parentId = request.getParameter("parentId");
            mav.addObject("parentId", parentId);
            MetadataItem metadataItem = metadataManager.getMetadataItemById(Long.valueOf(parentId));
            if(metadataItem != null) {
                mav.addObject("metadataItem", metadataItem);
                List<MetadataItem> list = metadataManager.getChildItemByItemId(metadataItem);
                sortNumber = getMaxSortNumber(list);
                valueNumber = getMaxValue(list);
            }
        }
        mav.addObject("sortNumber", sortNumber);
        mav.addObject("valueNumber", valueNumber);
        mav.addObject("parentType", parentType);
        return mav;
    }

    private int getMaxSortNumber(List<MetadataItem> metadataItems) {
        int maxNumber = 0;
        if(metadataItems == null) {
            return 1;
        }
        for(MetadataItem metadataItem : metadataItems) {
            Integer currentSort = metadataItem.getSort();
            if(null != currentSort) {
                int currentSortNumber = metadataItem.getSort().intValue();
                if(currentSortNumber > maxNumber) {
                    maxNumber = currentSortNumber;
                }
            }
        }
        return maxNumber + 1;
    }

    private int getMaxValue(List<MetadataItem> metadataItems) {
        int maxValue = 0;
        if(metadataItems == null || metadataItems.isEmpty()) {
            return maxValue;
        }
        for(MetadataItem metadataItem : metadataItems) {
            try {
                if(Integer.parseInt(metadataItem.getValue()) > maxValue) {
                    maxValue = Integer.parseInt(metadataItem.getValue());
                }
            } catch(Exception e) {
                continue;
            }
        }
        return maxValue + 1;
    }

    /**
     * 增加用户自定义枚举项
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView addUdefinedItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String parentId = request.getParameter("parentId");
        String label = request.getParameter("label");
        String value = request.getParameter("value");
        String sortStr = request.getParameter("sort");
        String parentType = request.getParameter("parentType");
        Integer inputSwitch = RequestUtils.getIntParameter(request, "inputSwitch");
        Integer outputSwitch = RequestUtils.getIntParameter(request, "outputSwitch");
        if(null != inputSwitch && inputSwitch.intValue() == Constants.METADATAITEM_SWITCH_ENABLE) {
            outputSwitch = Constants.METADATAITEM_SWITCH_ENABLE;
        }
        Integer sort = 0;
        if(!Strings.isBlank(sortStr)) {
            sort = Integer.parseInt(sortStr);
        }
        MetadataItem item = new MetadataItem();
        item.setLabel(label);
        item.setValue(value);
        item.setState(inputSwitch);
        item.setOutputSwitch(outputSwitch);
        item.setSort(sort);
        item.setIsRef(com.seeyon.v3x.common.metadata.Constants.METADATAITEM_ISREF_NO);
        PrintWriter out = response.getWriter();
        Long metadataItemId = null;
        BigDecimal bigValue = new BigDecimal(value);
        if(checkDupleValue(parentId, parentType, null, bigValue)) {
            metadataItemId = metadataManager.addMetadataItem(Long.valueOf(parentId), parentType, item, false);
        } else {
            out.println("<script>");
            out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.error.dupleValue") + "');");
            out.println("history.back();");
            out.println("</script>");
            return null;
        }
        out.println("<script>");
        if(metadataItemId != null) {
            out.println("parent.toolbarFram.treeSelectId ='" + parentId + "';");
            Long counterParentId = null;
            if("metadata".equals(parentType)) {
                counterParentId = metadataManager.getMetadata(Long.valueOf(parentId)).getParentid();
            } else {
                counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getMetadataId();
                if(counterParentId == null || counterParentId == 0) {
                    counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getParentId();
                }
            }
            out.println("parent.toolbarFram.parentId ='" + counterParentId + "';");
        }
        out.println("var condition = parent.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.treeFrame.location.href = parent.treeFrame.location.href;");
        out.println("parent.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("</script>");
        String isContinue = request.getParameter("continue");
        if(Strings.isNotBlank(isContinue) && isContinue.equals("true")) {
            super.rendJavaScript(response, "parent.doEnd('" + parentId + "','true')");
        } else {
            super.rendJavaScript(response, "parent.doEnd('" + parentId + "','false')");
        }
        return null;
    }

    public ModelAndView deleteUserDefinedMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataIds = request.getParameter("metadataId");
        String parentId = request.getParameter("parentId");
        String[] metadataIdsStr = metadataIds.split(",");
        for(String idStr : metadataIdsStr) {
            long metadataId = Long.parseLong(idStr);
            Metadata metadata = metadataManager.getMetadata(metadataId);
            metadataManager.deleteMetadata(metadata);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("var condition = parent.mainIframe.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.mainIframe.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.mainIframe.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.mainIframe.treeFrame.location.href = parent.mainIframe.treeFrame.location.href;");
        out.println("parent.mainIframe.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.mainIframe.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("window.parent.mainIframe.listFrame.location.href = window.parent.mainIframe.listFrame.location.href");
        out.println("window.parent.mainIframe.listFrame.location.reload()");
        out.println("</script>");
        return null;
    }

    public ModelAndView deleteUserDefinedItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String parentType = request.getParameter("parentType");
        String parentId = request.getParameter("parentId");
        String metadataItemIdsStr = request.getParameter("metadataItemIds");
        String metadataItemIds[] = metadataItemIdsStr.split(",");
        String szUseItemName = "";
        if(!metadataManager.checkDelAll(parentType, parentId, metadataItemIds)) {
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadataValue.all") + "');");
            out.println("parent.mainIframe.listFrame.location.reload(true);");
            out.println("</script>");
            return null;
        }
        if("metadata".equals(parentType) && Strings.isNotBlank(parentId)) {
            Metadata metadata = metadataManager.getMetadata(Long.valueOf(parentId));
            for(String idStr : metadataItemIds) {
                long metadataItemId = Long.parseLong(idStr);
                if(metadata.getCategory() == ApplicationCategoryEnum.edoc.getKey()) {
                    MetadataItem mi = metadataManager.getMetadataItem(metadata, metadataItemId);
                    EdocManager edocManager = (EdocManager)ApplicationContextHolder.getBean("edocManager");
                    if(edocManager.useMetadataValue(-1L, metadata.getId(), mi.getValue())) {
                        if(!"".equals(szUseItemName)) {
                            szUseItemName += ",";
                        }
                        szUseItemName += mi.getValue();
                        continue;
                    }
                }
            }
        }
        String szMsg = Constants.getString4CurrentUser("system.manager.ok");
        if(!"".equals(szUseItemName)) {
            szMsg = Constants.getString4CurrentUser("system.metadata.isuse", szUseItemName);
        }
        metadataManager.deleteMetadataItem(parentType, parentId, metadataItemIds, false);
        PrintWriter out = response.getWriter();
        Long counterParentId = null;
        if("metadata".equals(parentType)) {
            counterParentId = metadataManager.getMetadata(Long.valueOf(parentId)).getParentid();
        } else {
            counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getMetadataId();
            if(counterParentId == null) {
                counterParentId = metadataManager.getMetadataItemById(Long.valueOf(parentId)).getParentId();
            }
        }
        out.println("<script>");
        if(!"".equals(szUseItemName)) {
            out.println("alert('" + szMsg + "')");
        }
        out.println("parent.mainIframe.toolbarFram.treeSelectId ='" + parentId + "';");
        out.println("parent.mainIframe.toolbarFram.parentId ='" + counterParentId + "';");
        out.println("var condition = parent.mainIframe.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.mainIframe.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.mainIframe.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.mainIframe.treeFrame.location.href = parent.mainIframe.treeFrame.location.href;");
        out.println("parent.mainIframe.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.mainIframe.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("parent.mainIframe.listFrame.location.href = parent.mainIframe.listFrame.location.href ");
        out.println("parent.mainIframe.listFrame.location.reload()");
        out.println("</script>");
        return null;
    }

    /**
     * 新增用户自定义枚举
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView addUserDefinedMeta(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataName = request.getParameter("metadataName");
        String sort = request.getParameter("sort");
        String description = request.getParameter("description");
        String parentId = request.getParameter("parentId");
        User user = CurrentUser.get();
        Metadata meta = new Metadata();
        meta.setIsSystem(Constants.METADATAITEM_ISSYSTEM_NO);
        meta.setLabel(metadataName);
        meta.setCategory(ApplicationCategoryEnum.global.getKey());
        meta.setSort(Integer.parseInt(sort.trim()));
        meta.setDescription(description);
        meta.setIsRef(1);
        if(Strings.isBlank(parentId)) {
            meta.setParentid(Long.valueOf("0"));
        } else {
            meta.setParentid(Long.valueOf(parentId));
        }
        meta.setState(1);
        meta.setIs_formEnum(Byte.parseByte("1"));
        if(user.isSystemAdmin()) {
            meta.setOrg_account_id(null);
        } else {
            meta.setOrg_account_id(user.getLoginAccount());
        }
        boolean isSystemMeatdata = false;
        metadataManager.addMetadata(meta, isSystemMeatdata);
        PrintWriter out = response.getWriter();
        out.println("<script>");
        // out.println("alert('" +
        // Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("parent.listFrame.location.href = parent.listFrame.location.href");
        out.println("parent.listFrame.location.reload()");
        out.println("var condition = parent.treeFrame.document.getElementById('condition');");
        out.println("var metadata = parent.treeFrame.document.getElementById('metadata');");
        out.println("var metadataItem = parent.treeFrame.document.getElementById('metadataItem');");
        out.println("if(condition != null && condition.value != '' && ((metadata != null && metadata.value != '') || (metadataItem != null && metadataItem.value != ''))){");
        out.println("parent.treeFrame.location.href = parent.treeFrame.location.href;");
        out.println("parent.treeFrame.location.reload();");
        out.println(" } else {");
        out.println("parent.refreshTree('" + parentId + "');");
        out.println("}");
        out.println("</script>");
        // request.setAttribute("selectId",parentId) ;
        return null;
    }

    /**
     * 修改枚举分类
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView editorNewMdata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/editorMetadaType");
        // String system = request.getParameter("isSystem") ;
        // //判断创建的是系统类型的还是单位类型的
        String from = request.getParameter("from");
        String num = request.getParameter("sortNum");
        String treeid = request.getParameter("id");
        String selectName = request.getParameter("selectName");
        // String ids[] = treeid.split("_") ;
        mav.addObject("sortNum", num);
        mav.addObject("metadataID", treeid);
        mav.addObject("from", from);
        mav.addObject("metadataName", selectName);
        Metadata matadata = this.metadataManager.getMetadata(Long.valueOf(treeid));
        mav.addObject("matadata", matadata);
        return mav;
    }

    /**
     * xgghen
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView createNewMdata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = null;
        String system = request.getParameter("isSystem"); // 判断创建的是系统类型的还是单位类型的
        String from = request.getParameter("from");
        if(from != null) {
            mav = new ModelAndView("sysMgr/metadata/createNewMdataType");
            // this.metadataManager.getMetadata(Long.parseLong(s)) ;
            String num = request.getParameter("sortNum");
            String treeid = request.getParameter("id");
            String selectName = request.getParameter("selectName");
            String ids[] = treeid.split("_");
            mav.addObject("sortNum", num);
            mav.addObject("metadataID", ids[1]);
            mav.addObject("from", from);
            mav.addObject("metadataName", selectName);
            Metadata matadata = this.metadataManager.getMetadata(Long.valueOf(ids[1]));
            mav.addObject("matadata", matadata);
        } else {
            mav = new ModelAndView("sysMgr/metadata/createNewMdataType");
        }
        mav.addObject("type", system);
        return mav;
    }

    /**
     * 创建新的类型 xgghen
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView createNewMetaDataType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // String system = request.getParameter("isSystem"); //
        // 判断创建的是系统类型的还是单位类型的
        String typeName = request.getParameter("typeName");
        String sortNUM = request.getParameter("sortNum");
        String description = request.getParameter("textarea");
        String from = request.getParameter("from");
        PrintWriter out = response.getWriter();
        if(Strings.isBlank(from)) {
            User user = CurrentUser.get();
            Metadata meta = new Metadata();
            meta.setIsSystem(Constants.METADATAITEM_ISSYSTEM_NO);
            meta.setLabel(typeName);
            meta.setCategory(ApplicationCategoryEnum.global.getKey());
            meta.setSort(Integer.parseInt(sortNUM.trim()));
            meta.setDescription(description);
            meta.setIsRef(1);
            meta.setParentid(Long.valueOf("0"));
            meta.setIs_formEnum(Byte.parseByte("0"));
            meta.setState(1);
            if(user.isSystemAdmin()) {
                meta.setOrg_account_id(null);
            } else {
                meta.setOrg_account_id(user.getLoginAccount());
            }
            boolean isSystemMeatdata = false;
            metadataManager.addMetadata(meta, isSystemMeatdata);
        } else {
            // 修改类别
            String metadataId = request.getParameter("metadataID");
            if(Strings.isBlank(metadataId)) {
                return null;
            }
            Long metadaId = Long.valueOf(metadataId);
            Metadata metadata = metadataManager.getMetadata(metadaId);
            if(metadata == null) {
                out.println("<script>");
                out.println("alert('" + Constants.getString4CurrentUser("metadata.manager.deleted") + "');");
                out.println("history.back();");
                out.println("</script>");
                return null;
            }
            metadata.setDescription(description);
            metadata.setLabel(typeName);
            metadata.setSort(Integer.parseInt(sortNUM.trim()));
            metadataManager.updateMetadata(metadata, false);
        }
        out.println("<script>");
        // out.println("alert('" +
        // Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("parent.window.returnValue ='false';");
        // out.println("parent.mainIframe.treeFrame.reload(true);") ;
        out.println("window.close();");
        out.println("</script>");
        return null;
    }

    /**
     * 移动枚举到相应的枚举分类下
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView moveMetada(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/moveMetadata");
        mav.addObject("isSystem", request.getParameter("isSystem"));
        String metadataType = request.getParameter("metadataTypeId"); // 为这个枚举的parentid
        String metadataIds = request.getParameter("metadataIds"); // 为所要移动的枚举的id的组合
        User user = CurrentUser.get();
        List<Metadata> allMetadataType = this.metadataManager.getAllMetadaType();
        List<Metadata> returnMetadataType = new ArrayList<Metadata>();
        if(user.isSystemAdmin()) {
            for(Metadata metadata : allMetadataType) {
                if(metadata.getOrg_account_id() == null) {
                    returnMetadataType.add(metadata);
                }
            }
            mav.addObject("userType", "systemAdmin");
        } else {
            String metadataId[] = metadataIds.split(",");
            for(String id : metadataId) {
                Metadata metadata = metadataManager.getMetadata(Long.valueOf(id));
                if(metadata == null) {
                    PrintWriter out = response.getWriter();
                    out.println("<Script>");
                    out.println("alert('" + Constants.getString4CurrentUser("metdata.manager.delete.metadata") + "');");
                    out.println("history.back();");
                    out.println("window.dialogArguments.parent.mainIframe.listFrame.location.reload(true);");
                    out.println("window.dialogArguments.parent.mainIframe.treeFrame.location.reload(true);");
                    out.println("window.close();");
                    out.println("</Script>");
                    return null;
                }
            }
            for(Metadata metadata : allMetadataType) {
                if(metadata.getOrg_account_id() == null) {
                    continue;
                }
                if(metadata.getOrg_account_id().longValue() == user.getLoginAccount()) {
                    returnMetadataType.add(metadata);
                }
            }
            mav.addObject("userType", "user");
        }
        mav.addObject("allMetadataType", returnMetadataType);
        mav.addObject("metadataIds", metadataIds);
        mav.addObject("parentId", metadataType);
        return mav;
    }

    /**
     * 得到某个单位下的所有的元数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showOrgAccountMeataData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/listMetadata");
        List<Metadata> returnList = new ArrayList<Metadata>();
        List<Metadata> allMetadatasList = new ArrayList<Metadata>();
        String org_account_id = request.getParameter("org_account_id");
        if(org_account_id != null && !org_account_id.equals("")) {
            Long account_id = Long.valueOf(org_account_id);
            allMetadatasList = this.metadataManager.getAllOrgAoMetadata(account_id);
        }
        for(Metadata metadata : allMetadatasList) {
            if(metadata.getIs_formEnum().intValue() == 1 && metadata.getParentid().intValue() == 0)
                returnList.add(metadata);
        }
        mav.addObject("metadatasList", pagenate(returnList));
        return mav;
    }

    /**
     * 转移枚举
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView savaMoveMetadata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String metadataIds = request.getParameter("metadataIds");
        String toMetadataId = request.getParameter("toMetadataId");
        String metadata[] = null;
        if(!Strings.isBlank(metadataIds)) {
            metadata = metadataIds.split(",");
        }
        for(String id : metadata) {
            Metadata metadataObj = metadataManager.getMetadata(Long.valueOf(id));
            if(metadataObj == null) {
                continue;
            }
            metadataObj.setParentid(Long.valueOf(toMetadataId));
            metadataManager.updateMetadata(metadataObj, false);
        }
        PrintWriter out = response.getWriter();
        out.println("<Script>");
        out.println("parent.window.returnValue ='false';");
        out.println("window.close();");
        out.println("</Script>");
        return null;
    }

    // 表单绑定的开始
    /**
     * 弹出绑定的主框架
     * @param request
     * @param response
     * @return
     * @throws Exception
     ***/
    public ModelAndView metadataMainIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/selectDataMainFrame");
        return mav;
    }

    /**
     * 弹出绑定的界面
     * @param request
     * @param response
     * @return
     * @throws Exception
     ***/
    public ModelAndView showSystemMetadataIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/metadataSelect");
        return mav;
    }

    /**
     * 绑定枚举 的时候系统显示树结构
     * @param request
     * @param response
     * @return
     * @throws Exception
     ***/
    public ModelAndView selectTreeFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/systemMetadataTree");
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        List<Metadata> allMetadatasList = metadataManager.getAllOrgAoMetadata(null);
        List<Metadata> treeData = new ArrayList<Metadata>();
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)){
        	Set<Long> parentIdSet = new HashSet<Long>();
        	for(Metadata metadata : allMetadatasList) {
                if(metadata.getIs_formEnum() != null && (metadata.getIs_formEnum().intValue() == 0 || metadata.getIs_formEnum().intValue() == 1)) {
                	if(metadata.getLabel().contains(textfield)){
                		treeData.add(metadata);
                		Long parentId = metadata.getParentid();
                    	if(parentId != 0){
                    		parentIdSet.add(parentId);
                    	}
                	}
                }
        	}
        	for (Long id : parentIdSet) {
        		treeData.add(metadataManager.getMetadata(id));
			}
        } else {
	        for(Metadata metadata : allMetadatasList) {
	            if(metadata.getIs_formEnum() != null && (metadata.getIs_formEnum().intValue() == 0 || metadata.getIs_formEnum().intValue() == 1)) {
	            	treeData.add(metadata);
	            }
	        }
        }
        mav.addObject("condition", condition);
        mav.addObject("textfield", textfield);
        mav.addObject("treeData", treeData);
        return mav;
    }

    /**
     * 单位枚举绑定的结构
     * @param request
     * @param response
     * @return
     * @throws Exception
     ***/
    public ModelAndView showOrgMetadataIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/selectOrgMetadata");
        return mav;
    }

    public ModelAndView selectOrgTreeFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = CurrentUser.get();
        ModelAndView mav = new ModelAndView("sysMgr/metadata/orgMetadataTree");
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        List<Metadata> allMetadatasList = metadataManager.getMetadatasForForm(false, user.getLoginAccount());
        // 增加排序功能
        if(allMetadatasList != null) {
            Collections.sort(allMetadatasList, new MetadataComparator());
        }
        List<Metadata> treeData = new ArrayList<Metadata>();
        if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)){
        	Set<Long> parentIdSet = new HashSet<Long>();
        	for(Metadata metadata : allMetadatasList) {
                if(metadata.getIs_formEnum().intValue() == 1 && metadata.getOrg_account_id().longValue() == user.getLoginAccount()) {
                	if(metadata.getLabel().contains(textfield)){
                		treeData.add(metadata);
                		Long parentId = metadata.getParentid();
                    	if(parentId != 0){
                    		parentIdSet.add(parentId);
                    	}
                	}
                }
        	}
        	for (Long id : parentIdSet) {
        		treeData.add(metadataManager.getMetadata(id));
			}
        } else {
        	for(Metadata metadata : allMetadatasList) {
        		if(metadata.getOrg_account_id().longValue() == user.getLoginAccount()) {
        			treeData.add(metadata);
        		}
        	}
        } 
        mav.addObject("condition", condition);
        mav.addObject("textfield", textfield);
        mav.addObject("treeData", treeData);
        return mav;
    }

    @CheckRoleAccess(roleTypes = {RoleType.NeedNoCheck})
    public ModelAndView showQueryTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showQueryTree");
        String metadataid = request.getParameter("metadataId");
        String level = request.getParameter("level");
        List<MetadataItem> list = new ArrayList<MetadataItem>();
        if(Strings.isBlank(metadataid) || Strings.isBlank(level)) {
            return mav;
        }
        Metadata metadata = metadataManager.getMetadata(Long.valueOf(metadataid));
        if(metadata == null) {
            return mav;
        }
        List<MetadataItem> allList = metadataManager.getLevelItemOfMetadata(metadata, Integer.valueOf(level));
        if(allList != null) {
            for(MetadataItem metadataItem : allList) {
                if(metadataItem.getOutputSwitch() != null && Constants.METADATAITEM_SWITCH_ENABLE == metadataItem.getOutputSwitch().intValue()) {
                    list.add(metadataItem);
                }
            }
        }
        mav.addObject("metadata", metadata);
        mav.addObject("metadataItem", list);
        return mav;
    }

    @CheckRoleAccess(roleTypes = {RoleType.NeedNoCheck})
    public ModelAndView showQueryTreeFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showQueryTreeFrame");
        mav.addObject("metadataId", request.getParameter("metadataId"));
        return mav;
    }

    /**
     * 公文管理员的入口
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showEdocMetadataIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/showEdocMetadataIndex");
    }

    public ModelAndView showEdocMetadataMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("sysMgr/metadata/showEdocMetadataMain");
    }

    public ModelAndView showEdocMetadataTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/metadata/showEdocMetadata_tree");
        User user = CurrentUser.get();
        Long accountId = Long.valueOf(user.getLoginAccount());
        // 单位枚举
        List<Metadata> orgMetadatasList = metadataManager.getAllOrgAoMetadata(accountId);
        List<MetadataItem> orgMetadataItemList = metadataManager.getMetadataItemByMetadata(orgMetadatasList);
        Collections.sort(orgMetadatasList);
        Collections.sort(orgMetadataItemList);
        // 系统枚举
        List<Metadata> allMetadatasList = metadataManager.getAllSystemMetadatasForSystemOpera();
        // 公共枚举
        List<Metadata> publicMetadatasList = metadataManager.getAllOrgAoMetadata(null);
        List<MetadataItem> publicMetadataItemList = metadataManager.getMetadataItemByMetadata(publicMetadatasList);
        mav.addObject("orgMetadataItemList", orgMetadataItemList);
        mav.addObject("orgMetadatasList", orgMetadatasList);
        mav.addObject("acconutId", accountId);
        mav.addObject("sysMetadatasList", allMetadatasList);
        mav.addObject("publicMetadataItemList", publicMetadataItemList);
        mav.addObject("publicMetadatasList", publicMetadatasList);
        return mav;
    }

    /*
     * public List<Metadata> filtrateForLiuNasOrganization(List listA){
     * List<Metadata> list = new ArrayList<Metadata>(); for }
     */
    private <T>List<T> pagenate(List<T> list) {
        if(null == list || list.size() == 0)
            return new ArrayList<T>();
        Integer first = Pagination.getFirstResult();
        Integer pageSize = Pagination.getMaxResults();
        Pagination.setRowCount(list.size());
        List<T> subList = null;
        if(first + pageSize > list.size()) {
            subList = list.subList(first, list.size());
        } else {
            subList = list.subList(first, first + pageSize);
        }
        return subList;
    }

    public OrgManagerDirect getOrgManagerDirect() {
        return orgManagerDirect;
    }

    public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }

    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
}
