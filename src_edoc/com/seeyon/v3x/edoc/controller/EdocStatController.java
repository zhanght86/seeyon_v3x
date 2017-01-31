/**
 * 
 */
package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocStat;
import com.seeyon.v3x.edoc.domain.EdocStatCondObj;
import com.seeyon.v3x.edoc.domain.EdocStatDisObj;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.domain.WebEdocStat;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocMarkHistoryManager;
import com.seeyon.v3x.edoc.manager.EdocMarkManager;
import com.seeyon.v3x.edoc.manager.EdocPermissionControlManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocStatManager;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.exchange.manager.RecieveEdocManager;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 类描述：
 * 创建日期：
 *
 * @author kangyutao
 * @version 1.0 
 * @since JDK 5.0
 */
@CheckRoleAccess(roleTypes={RoleType.AccountEdocExchange,RoleType.DepartmentEdocExchange})
public class EdocStatController extends BaseController{
	
	private final String resource_common_baseName = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
	private MetadataManager metadataManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private EdocManager edocManager;
	private UpdateIndexManager updateIndexManager;
	private AffairManager affairManager;
	private EdocPermissionControlManager edocPermissionControlManager;
	private EdocFormManager edocFormManager;
	private TempleteManager templeteManager;
	private FlowPermManager flowPermManager;
	private PermissionManager permissionManager;
	private RecieveEdocManager recieveEdocManager;
	private EdocMarkManager edocMarkManager;
	private EdocMarkHistoryManager edocMarkHistoryManager;
	private EdocStatManager edocStatManager;
	private FileToExcelManager fileToExcelManager;
	private LocalizationContext bundleAttrValue;
	private DocHierarchyManager docHierarchyManager;
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	/**
	 * 先从指定的资源中查找，再查找默认的
	 * 
	 * @param locCtxt
	 * @throws JspTagException
	 */
    public void setBundle(LocalizationContext locCtxt) throws JspTagException {
        this.bundleAttrValue = locCtxt;
    }
	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public EdocMarkHistoryManager getEdocMarkHistoryManager() {
		return edocMarkHistoryManager;
	}

	public void setEdocMarkHistoryManager(
			EdocMarkHistoryManager edocMarkHistoryManager) {
		this.edocMarkHistoryManager = edocMarkHistoryManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public EdocFormManager getEdocFormManager() {
		return edocFormManager;
	}

	public EdocManager getEdocManager() {
		return edocManager;
	}

	public EdocPermissionControlManager getEdocPermissionControlManager() {
		return edocPermissionControlManager;
	}

	public FlowPermManager getFlowPermManager() {
		return flowPermManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public TempleteManager getTempleteManager() {
		return templeteManager;
	}

	public EdocStatManager getEdocStatManager() {
		return edocStatManager;
	}

	public void setEdocStatManager(EdocStatManager edocStatManager) {
		this.edocStatManager = edocStatManager;
	}

	public RecieveEdocManager getRecieveEdocManager() {
		return recieveEdocManager;
	}

	public EdocMarkManager getEdocMarkManager() {
		return edocMarkManager;
	}

	public void setEdocMarkManager(EdocMarkManager edocMarkManager) {
		this.edocMarkManager = edocMarkManager;
	}

	public void setRecieveEdocManager(RecieveEdocManager recieveEdocManager) {
		this.recieveEdocManager = recieveEdocManager;
	}

    public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	
	public void setFlowPermManager(FlowPermManager flowPermManager)
	{
		this.flowPermManager=flowPermManager;
	}
	
	public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }
	
	public void setEdocFormManager(EdocFormManager edocFormManager)
	{
		this.edocFormManager=edocFormManager;
	}
	
	public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }
	
	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	
	public void setEdocManager(EdocManager edocManager)
	{
		this.edocManager=edocManager;
	}
	
	public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
	
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
	
    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }
    public void setEdocPermissionControlManager(
            EdocPermissionControlManager edocPermissionControlManager) {
        this.edocPermissionControlManager = edocPermissionControlManager;
    }


	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView statEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		ModelAndView mav = new ModelAndView("edoc/docstat/edocQueryIndex");		
		return mav;
	}
	
	public ModelAndView edocQueryTopFrame(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("edoc/docstat/edocQueryTopFrame");
		return mav;		
	}
	/**
	 * 统计查询的结果
	 * @param request
	 * @param isExcelQuery  ：是否是来自Excel导出
	 * @return
	 * @throws Exception
	 */
	private List<EdocStat> query(HttpServletRequest request,boolean isExcelQuery)throws Exception{
		//构造查询参数
		String edocTypeParameter="edocType";
		String beginDateParameter="beginDate";
		String endDateParameter="endDate";
		String flowStateParameter="flowState";
		boolean needPagination=true;
		if(isExcelQuery){
			needPagination=false;
			edocTypeParameter="_oldEdocType";
			beginDateParameter="_oldBeginDate";
			endDateParameter="_oldEndDate";
			flowStateParameter="_oldFlowState";
		}
		
		long domainId = 0;							//如果当前用户担任单位收发员，则赋当前单位id
		List<Long> deptIds = new ArrayList<Long>(); //当前用户担任收发员的部门id
		List<EdocStat> results = new ArrayList<EdocStat>();
		User user=CurrentUser.get();
		boolean isAccountExchange = EdocRoleHelper.isAccountExchange();
		
		if (isAccountExchange) {//单位收发员：统计当前登录单位的。
			domainId =	user.getLoginAccount();
		}else {
			//部门收发员：统计当前用户在当前登录单位下的兼职部门中作为公文收发员时，兼职部门内的公文。
			//如果在兼职部门中同时担当两个兼职部门的公文收发员，则统计结果为两个个兼职部门公文之和。
			deptIds.addAll(EdocRoleHelper.getUserExchangeDepartmentIdsToList());
		}
		
		Integer flag = RequestUtils.getIntParameter(request, "flag", 0);
		
		if (flag == 1) {
			Integer edocType = RequestUtils.getIntParameter(request, edocTypeParameter, 0);
			String strBdate = request.getParameter(beginDateParameter);
			String strEdate = request.getParameter(endDateParameter);
			Date beginDate = null;
			if (strBdate != null && !strBdate.equals("")) {
				beginDate = Datetimes.getTodayFirstTime(strBdate);
			}
			Date endDate = null;
			if (strEdate != null && !strEdate.equals("")) {
				endDate = Datetimes.getTodayLastTime(strEdate);
			}	
			
			if (edocType == EdocEnum.edocType.sendEdoc.ordinal()) {//得到的是发文的数据
				Integer flowState = RequestUtils.getIntParameter(request, flowStateParameter, 0);			
				results = edocStatManager.querySentEdocStat(flowState, beginDate, endDate,  deptIds, domainId,needPagination);	
			}
			else if (edocType == EdocEnum.edocType.recEdoc.ordinal() ) {
				results = edocStatManager.queryEdocStat(edocType, beginDate, endDate, deptIds, domainId,needPagination);
			}else if( edocType == EdocEnum.edocType.signReport.ordinal()){
				results = edocStatManager.queryEdocStat(edocType, beginDate, endDate, deptIds, domainId,needPagination); 
			}
			else if (edocType == 999) { //查询归档公文
				results = edocStatManager.queryArchivedEdocStat(beginDate, endDate, deptIds, domainId,needPagination);
			}
			
		}
		return results;
	}
	
	public ModelAndView listQueryResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docstat/listQueryResult");
		Integer flag = RequestUtils.getIntParameter(request, "flag", 0);
		boolean isRecEdoc=false;
		if (flag == 1) {
			Integer edocType = RequestUtils.getIntParameter(request, "edocType", 0);
			if (edocType == EdocEnum.edocType.sendEdoc.ordinal()) {//得到的是发文的数据
				mav.addObject("resultType", "sendEdoc") ;
			}
			else if (edocType == EdocEnum.edocType.recEdoc.ordinal() ) {
				mav.addObject("resultType", "recEdoc") ;
				isRecEdoc=true;
			}else if( edocType == EdocEnum.edocType.signReport.ordinal()){
				mav.addObject("resultType", "signReport") ;
			}
			else if (edocType == 999) { //查询归档公文
				mav.addObject("resultType", "acchivedEdocStat") ;
			}
		}
		List<EdocStat> results = query(request,false);
		mav.addObject("edocStats", this.response(results,isRecEdoc));
		return mav;
	}	
	
	/**
	 *  显示统计条件－－统计入口
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView statCondition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("edoc/docstat/edocStatCondition");
		Calendar cal = Calendar.getInstance();
		int curYear = cal.get(Calendar.YEAR);		
		int curMonth = cal.get(Calendar.MONTH);
		int curSeason = 0;
		if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
			curSeason = 1;
		}
		else if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
			curSeason = 2;
		}
		else if (curMonth >= Calendar.JULY && curMonth <= Calendar.SEPTEMBER) {
			curSeason = 3;
		}
		else {
			curSeason = 4;
		}
		mav.addObject("curYear", curYear);			
		mav.addObject("curMonth", curMonth);
		mav.addObject("curSeason", curSeason);
		return mav;
	}
	
	
	/**
	 * 公文统计，返回统计结果。 
	 */
	public ModelAndView doStat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docstat/edocStatResult");		
		Integer year = Integer.valueOf(request.getParameter("year"));		
		Integer season = 0;
		Integer month = 0;		
		Integer periodType = 0; // 统计时间段类型：0-全年;1-指定季度;2-指定月份
		if (request.getParameter("morecond") != null) {
			periodType = Integer.valueOf(request.getParameter("morecondition"));			
			if (periodType == Constants.EDOC_STAT_PERIOD_TYPE_SEASON) {	
				season = Integer.valueOf(request.getParameter("season"));
				mav.addObject("sTitle", "第"+season+"季度");
			}
			else {
				month = Integer.valueOf(request.getParameter("month"));				
				mav.addObject("sTitle", "第"+month+"月");
			}
		}		
		int groupType = RequestUtils.getIntParameter(request, "groupType", 0);//分组条件：0-按部门分组;1-按公文种类分组
		
		List deptIds=new ArrayList<Long>(); //当前用户担任收发员的部门id
		long domainId = 0;//如果当前用户担任单位收发员，则赋当前单位id
		boolean isAccountExchange = EdocRoleHelper.isAccountExchange();
		if (isAccountExchange) {
			domainId = CurrentUser.get().getLoginAccount();
		}else {
			deptIds.addAll(EdocRoleHelper.getUserExchangeDepartmentIdsToList());
		}
		
		EdocStatCondObj esco = new EdocStatCondObj();
		esco.setYear(year);
		esco.setSeason(season);
		esco.setMonth(month);
		esco.setPeriodType(periodType);
		esco.setGroupType(groupType);
		esco.setDeptIds(deptIds);
		esco.setDomainId(domainId);		
		List<EdocStatDisObj> results = edocStatManager.statEdoc(esco, groupType);
		mav.addObject("year", year);
		mav.addObject("morecond", request.getParameter("morecond"));
		mav.addObject("morecondition", periodType);
		mav.addObject("season", season);
		mav.addObject("month", month);
		mav.addObject("groupType", groupType);
		mav.addObject("results", results);
		String groupName = "edoc.stat.group.dept.label";
		if (groupType == Constants.EDOC_STAT_GROUPBY_DOCTYPE) {
			groupName = "edoc.stat.group.doctype.label";
		}
		mav.addObject("groupName", groupName);
		
		return mav;
	}
	
    /**
     * 查询统计记录详细信息。
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView edocDetail(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Long id = Long.valueOf(request.getParameter("id"));
        EdocStat edocStat = edocStatManager.getEdocStat(id);
        Long edocId = edocStat.getEdocId();
        EdocSummary edocSummary = edocManager.getEdocSummaryById(edocId, false);
        User user=CurrentUser.get();
    	//SECURITY 访问安全控制
        if(!SecurityCheck.isHasAuthorityToStatDetail(request, response, edocSummary, user)){
        	return null;
        }
        ModelAndView mav = new ModelAndView("edoc/docstat/edocDetail");
        try{
        mav.addObject("edocStatObj", edocStat);
        mav.addObject("edocSummary", edocSummary);
        String docType = "";//公文种类
        String sendType = "";//行文类型
        String secretLevel = "";//文件密级
        String urgentLevel = "";//紧急程度
        String keepPeriod = "";//保密期限
        if(edocSummary!=null){
	        if (edocSummary.getDocType() != null && !edocSummary.getDocType().equals("")) {
	        	docType = metadataManager.getMetadataItemLabel("edoc_doc_type", edocSummary.getDocType());
	        	mav.addObject("docType", docType);
	        }   
	        if (edocSummary.getSendType() != null && !edocSummary.getSendType().equals("")) {
	        	sendType = metadataManager.getMetadataItemLabel("edoc_send_type", edocSummary.getSendType());
	        	mav.addObject("sendType", sendType);
	        }
	        if (edocSummary.getSecretLevel() != null && !edocSummary.getSecretLevel().equals("")) {
	        	secretLevel = metadataManager.getMetadataItemLabel("edoc_secret_level", edocSummary.getSecretLevel());
	        	mav.addObject("secretLevel", secretLevel);
	        }
	        if (edocSummary.getUrgentLevel() != null && !edocSummary.getUrgentLevel().equals("")) {
	        	urgentLevel = metadataManager.getMetadataItemLabel("edoc_urgent_level", edocSummary.getUrgentLevel());
	        	mav.addObject("urgentLevel", urgentLevel);
	        }
	        if (edocSummary.getKeepPeriod() != null) {
	        	keepPeriod = metadataManager.getMetadataItemLabel("edoc_keep_period", String.valueOf(edocSummary.getKeepPeriod()));
	        	mav.addObject("keepPeriod", keepPeriod);
	        }
        }
	}catch(Exception e){
			PrintWriter out = response.getWriter();        	
        	//super.printV3XJS(out);        	
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
        	out.println("if(window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = \"true\";");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  parent.getA8Top().reFlesh();");
        	out.println("}");
        	out.println("");
        	out.println("</script>");
        	
        	return null;
		}
        return mav;
    }
    
    // 保存公文备考信息
    public ModelAndView saveEdocRemark(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	Long id = Long.valueOf(request.getParameter("id"));
    	String remark = request.getParameter("remark");
    	//PrintWriter out = response.getWriter();
    	edocStatManager.saveEdocRemark(id, remark);    		
    	return super.refreshWindow("parent");
    	//return super.refreshWorkspace();
    }
    
    public ModelAndView exportToExcel(HttpServletRequest request,HttpServletResponse response)throws Exception{
    		
		Integer year = Integer.valueOf(request.getParameter("year"));		
		Integer season = 0;
		Integer month = 0;		
		Integer periodType = 0; // 统计时间段类型：0-全年;1-指定季度;2-指定月份
		int groupType = 0;
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String stat_title = "";//标题
		
		if (!Strings.isBlank(request.getParameter("morecond"))) {
			periodType = Integer.valueOf(request.getParameter("morecondition"));			
			if (periodType == Constants.EDOC_STAT_PERIOD_TYPE_SEASON) {	
				season = Integer.valueOf(request.getParameter("season"));	
				stat_title = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.stat.tables.label.yearseason",year.toString(),season); //标题
			}
			else {
				month = Integer.valueOf(request.getParameter("month"));
				stat_title = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.stat.tables.label.yearmonth",year.toString(),month); //标题
			}
		}
		else{
			stat_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.label.year", year.toString()); //标题
		}
		if(null!=request.getParameter("groupType")){
			groupType = Integer.valueOf(request.getParameter("groupType"));
		}

		List deptIds=new ArrayList<Long>(); //当前用户担任收发员的部门id
		long domainId = 0;//如果当前用户担任单位收发员，则赋当前单位id
		boolean isAccountExchange = EdocRoleHelper.isAccountExchange();
		if (isAccountExchange) {
			domainId = CurrentUser.get().getLoginAccount();
		}
		else {
			deptIds.addAll(EdocRoleHelper.getUserExchangeDepartmentIdsToList());
		}
		
		EdocStatCondObj esco = new EdocStatCondObj();
		esco.setYear(year);
		esco.setSeason(season);
		esco.setMonth(month);
		esco.setPeriodType(periodType);
		esco.setGroupType(groupType);
		esco.setDeptIds(deptIds);
		esco.setDomainId(domainId);		
		List<EdocStatDisObj> results = edocStatManager.statEdoc(esco, groupType);


		
    	DataRecord dataRecord = EdocHelper.exportStat(request, results, esco,stat_title);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager, stat_title, dataRecord);
    	
    	return null;
    }
    
    /**
     * 导出查询结果的excel表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView exportQueryToExcel(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
    	// --  用于输出excel的标题 －－
    	// --  start  --
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String stat_title = ResourceBundleUtil.getString(resource, local, "edoc.query.tables.label"); //标题
		String send_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.send.label"); //发文
		String sign_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.sign.label"); //签报
		String recieve_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.recieve.label"); //收文
		String archivie_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.archive.label"); //归档
    	// -- end --
		
		Integer flag = RequestUtils.getIntParameter(request, "flag", 0);
		Integer edocType = RequestUtils.getIntParameter(request, "_oldEdocType", 0);
		boolean isRecEdoc=false;
		if (flag == 1) {					
			if (edocType == EdocEnum.edocType.sendEdoc.ordinal()) {
				stat_title = send_title; //标题为发文
			}
			else if (edocType == EdocEnum.edocType.recEdoc.ordinal() || edocType == EdocEnum.edocType.signReport.ordinal()) {
				if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
					stat_title = recieve_title; //标题为收文
					isRecEdoc=true;
				}else{
					stat_title = sign_title; //标题为签报				
				}
			}
			else if (edocType == 999) { //查询归档公文
				stat_title = archivie_title; // 标题为归档
			}
		}
		List<EdocStat> results =query(request,true);
		
    	DataRecord dataRecord = EdocHelper.exportQuery(request, this.response(results,isRecEdoc),stat_title,edocType);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager, stat_title, dataRecord);
    	return null;
    }
    
	private String getLabel(String itemValue,Metadata metadata){
		MetadataItem itms = metadata.getItem(itemValue);
		
		if (itms==null) return null;
		String label = null;
		if(itemValue != null) {
			if(this.bundleAttrValue != null){ //指定语言
				label = ResourceBundleUtil.getString(bundleAttrValue, itms.getLabel());
			}
			else if(Strings.isNotBlank(metadata.getResourceBundle())){ //在原数据中定义了resourceBundle
				label = ResourceBundleUtil.getString(metadata.getResourceBundle(), itms.getLabel());
			}
			
			if(label == null){
				return itms.getLabel();
			}			
		}

		
		return label;
	}
    
    /**
    * 进行封装的方法，封装成发送到页面的数据
    * @param list
    * @return
    */
    private List<WebEdocStat> response(List<EdocStat> list,boolean isRecEdoc) throws Exception{
    	if(list == null) {
    		return null ;
    	}
    	List<WebEdocStat> webEdocStatList = new ArrayList<WebEdocStat>() ;
    	Metadata docTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_doc_type);//得到公文种类的枚举
		Metadata  secretLeveleMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_secret_level);//得到公文密级的枚举
		
		
		List<Long> ids=new ArrayList<Long>();
		for(EdocStat edocStat: list) {
			ids.add(edocStat.getEdocId());
		}		
		Hashtable<Long,EdocSummary> hs=new Hashtable<Long,EdocSummary>();
		if(list.size()>0)
		{
			hs=edocManager.queryBySummaryIds(ids);
		}
		String archiveIds="";
		EdocSummary summary= new EdocSummary();
		for(EdocStat edocStat: list) {
			summary = hs.get(edocStat.getEdocId());
		 
    		String createUser =  "" ;
    		if(edocStat.getCreateUserid() != null){ 
    			createUser = orgManager.getMemberById(edocStat.getCreateUserid()).getName() ;
    		}    
    		String accountName = "";
    		if(isRecEdoc){
    			accountName=summary.getSendUnit()==null?"":summary.getSendUnit();
    		}else{
	    		 if(edocStat.getAccountId() != null ){
	    			 accountName = orgManager.getAccountById(edocStat.getAccountId()).getName() ;
	    		 }
    		}
    		String docType = this.getLabel(edocStat.getDocType(), docTypeMetadata) ;//公文的种类
    		//EdocSummary edocSummary = this.edocManager.getEdocSummaryById(edocStat.getEdocId(), false) ;
    		String secretLevel="";
    		if(hs.get(edocStat.getEdocId())!=null){
    			secretLevel = this.getLabel(summary.getSecretLevel(), secretLeveleMetadata) ;
    		}else{
    			secretLevel = this.getLabel(null, secretLeveleMetadata) ;
    		}
    		WebEdocStat webEdocStat  = new WebEdocStat() ;
    		if(summary != null){
	    		Long archiveId = summary.getArchiveId() ;
			    if(archiveId != null ){
			    	if("".equals(archiveIds)) archiveIds=String.valueOf(archiveId);
	   				else archiveIds+=","+archiveId;
			    	webEdocStat.setArchiveId(archiveId);
	            }
    		}
    		webEdocStat.setId(edocStat.getId()) ;    		
    		webEdocStat.setDocType(docType) ;  		
    		webEdocStat.setAccount(accountName) ;
    		webEdocStat.setArchivedTime(edocStat.getArchivedTime()) ;
    		webEdocStat.setCreateDate(edocStat.getCreateDate()) ;
    		webEdocStat.setDocMark(edocStat.getDocMark()) ; 		
    		webEdocStat.setCreateUser(createUser) ;
    		webEdocStat.setIssUser(edocStat.getIssuer()) ;
    		webEdocStat.setSubject(edocStat.getSubject()) ;
    		webEdocStat.setRemark(edocStat.getRemark()) ;
    		webEdocStat.setSendTo(edocStat.getSendTo()) ;
    		webEdocStat.setSerialNo(edocStat.getSerialNo()) ;
    		webEdocStat.setSecretLevel(secretLevel) ;
    		webEdocStat.setRecviverDate(edocStat.getCreateDate()) ;//登记日期
    		String edocType = "" ; //公文的类型
    		if(edocStat.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()) {
    			edocType = ResourceBundleUtil.getString(resource_common_baseName, "edoc.docmark.inner.send") ;
    		}else if(edocStat.getEdocType() == EdocEnum.edocType.recEdoc.ordinal()) {
    			edocType = ResourceBundleUtil.getString(resource_common_baseName, "edoc.docmark.inner.receive") ;
    		}else if(edocStat.getEdocType() == EdocEnum.edocType.signReport.ordinal()){
    			edocType = ResourceBundleUtil.getString(resource_common_baseName, "edoc.docmark.inner.signandreport") ;
    		}
    		webEdocStat.setEdocType(edocType) ;
    		webEdocStatList.add(webEdocStat) ;
    	}   
        
      //查询DocResouce,获取归档路径。
        List<DocResource> docs=docHierarchyManager.getDocsByIds(archiveIds);
        for(WebEdocStat webEdocStat: webEdocStatList){
        	for(DocResource doc :docs){
        		if(doc.getId().equals(webEdocStat.getArchiveId())){
        			
        			String frName=doc.getFrName();
        			if (com.seeyon.v3x.doc.util.Constants.needI18n(doc.getFrType()));
        				frName = com.seeyon.v3x.doc.util.Constants.getDocI18nValue(frName);
        			
        			if(doc.getLogicalPath()!=null && doc.getLogicalPath().split("\\.").length>1)
        				frName=com.seeyon.v3x.edoc.util.Constants.Edoc_PAGE_SHOWPIGEONHOLE_SYMBOL+java.io.File.separator+frName;
        			
        			webEdocStat.setArchiveName(frName);
        			webEdocStat.setLogicalPath((String)doc.getLogicalPath());
        			break;
        		}
        	}
        }
    	return webEdocStatList ;   
    }
    private List<WebEdocStat> response(List<EdocStat> list) throws Exception{
    	return response(list,false);
    }
    
    
	public ModelAndView mainEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/docstat/edocStatMain");
		
		return mav;
	}
    
}