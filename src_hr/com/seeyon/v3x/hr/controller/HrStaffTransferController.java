package com.seeyon.v3x.hr.controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.hr.StaffTransferFlag;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.hr.manager.StaffTransferManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.TempleteHelper;
import com.seeyon.v3x.hr.webmodel.WebStaffTransfer;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.util.Datetimes;

public class HrStaffTransferController extends BaseController {
	private transient static final Log LOG = LogFactory
	.getLog(HrStaffTransferController.class); 
	private OrgManagerDirect orgManagerDirect;
	private StaffTransferManager staffTransferManager;
	private StaffInfoManager staffInfoManager;
	private MetadataManager metadataManager;
	private String jsonView;

	public String getJsonView() {
		return jsonView;
	}
	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}
	
	public MetadataManager getMetadataManager()
    {
        return metadataManager;
    }

    public void setMetadataManager(MetadataManager metadataManager)
    {
        this.metadataManager = metadataManager;
    }
	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public StaffTransferManager getStaffTransferManager() {
		return staffTransferManager;
	}


	public void setStaffTransferManager(StaffTransferManager staffTransferManager) {
		this.staffTransferManager = staffTransferManager;
	}


	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

//	初始化
	public void initialized() {
//		TempleteHelper.getInstance().initialized(this.getServletContext());
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
	 * 人员附加信息的页签框架
	 * @author lucx
	 *
	 */
	public ModelAndView initHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffTransfer/home");
		return mav;
	}
	/**
	 * 人员附加信息的页签框架
	 * @author lucx
	 *
	 */
	public ModelAndView homeEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffTransfer/homeEntry");
		return mav;
	}
	
	public ModelAndView initDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/detailHome");
		String id = request.getParameter("id");
		String staffid = request.getParameter("staffid");
		String isReadOnly = request.getParameter("isReadOnly");
		String isNew = request.getParameter("isNew");
		mav.addObject("id", id);
		mav.addObject("staffid", staffid);
		mav.addObject("isReadOnly", isReadOnly);
		mav.addObject("isNew", isNew);
		return mav;
	}
	
	public ModelAndView initList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/listTransfer");
		List<StaffTransfer> listTransfer = staffTransferManager.getStaffTransfer();
		
		if(listTransfer.size()==0){
			return mav;
		}
		List<WebStaffTransfer> list = this.translateList(listTransfer);	
		Calendar time = Calendar.getInstance();
        Date date = time.getTime();
		mav.addObject("list", list);
		mav.addObject("referTime", date);
		return mav;
	}

	//ajax get account 
	public ModelAndView getAccount(HttpServletRequest request,
		HttpServletResponse response) throws Exception {
		String paramType = RequestUtils.getStringParameter(request, "paramType");
		Long paramId = RequestUtils.getLongParameter(request, "paramId");
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");		
		
		Long accountId = -1L;
		if ("dept".equals(paramType)) {
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(paramId);
			accountId = dept.getOrgAccountId();
		} else if ("post".equals(paramType)) {
			V3xOrgPost post = orgManagerDirect.getPostById(paramId);
			accountId = post.getOrgAccountId();
		} else if ("level".equals(paramType)) {
			V3xOrgLevel level = orgManagerDirect.getLevelById(paramId);
			accountId = level.getOrgAccountId();
		}
		
		V3xOrgAccount account =	orgManagerDirect.getAccountById(accountId);
		//to json
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("accountId", account.getId());
		jsonObject.putOpt("accountName", "("+ account.getShortname() +")");
			
		String view = null;
		if (isAjax) view = this.getJsonView();
		return new ModelAndView(view, Constants.AJAX_JSON, jsonObject);
	}
	
	public ModelAndView editTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/editTransfer");
		String isNew = request.getParameter("isNew");
		String isReadOnly = request.getParameter("isReadOnly");
		if(null!=isNew && isNew.equals("new")){
			Long staffid = RequestUtils.getLongParameter(request,"staffid");
			LOG.debug(" staffid: "+ staffid);
			StaffTransfer staffTransfer = new StaffTransfer();
			V3xOrgMember member=orgManagerDirect.getMemberById(staffid);
			WebStaffTransfer webstaffTransfer = new WebStaffTransfer();
			StaffInfo staffinfo=staffInfoManager.getStaffInfoById(staffid);
			 
			staffTransfer.setFromDepartment_id(member.getOrgDepartmentId());
			staffTransfer.setFromLevel_id(member.getOrgLevelId());
			staffTransfer.setFromPost_id(member.getOrgPostId());
			staffTransfer.setFromMember_state(member.getState());
			staffTransfer.setFromMember_type(member.getType());
			staffTransfer.setMember_id(staffid);
			
			Calendar time = Calendar.getInstance();
	        Date date = time.getTime();
	        staffTransfer.setTransfer_time(date);
	        
			mav.addObject("staffTransfer", staffTransfer);
			mav.addObject("ReadOnly", false);
			
			mav.addObject("staffinfo", staffinfo);
			
			long deptId = member.getOrgDepartmentId();
			long levelId = member.getOrgLevelId();
			long postId = member.getOrgPostId();
					
			V3xOrgAccount account = orgManagerDirect.getAccountById(member.getOrgAccountId());
			if (null != account) {
				webstaffTransfer.setFromAccount_name("("+ account.getShortname() +")");;
			}
			
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
			if (dept != null) {
				webstaffTransfer.setFromDepartment_name(dept.getName());
			}
				 
			V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
			if (null != level) {
				webstaffTransfer.setFromLevel_name(level.getName());
			}
			
			V3xOrgPost post = orgManagerDirect.getPostById(postId);
			if (null != post) {		
				webstaffTransfer.setFromPost_name(post.getName());
			}
			
			webstaffTransfer.setFromMember_type(member.getType());
			webstaffTransfer.setFromMember_state(member.getState());
			mav.addObject("webStaffTransfer", webstaffTransfer);
			
			//获得单位类别下拉列表中的数据
	        Map<String, Metadata> orgMeta = metadataManager
	                .getMetadataMap(ApplicationCategoryEnum.organization);
	        mav.addObject("orgMeta", orgMeta);
	        mav.addObject("orgMember", member);
			return mav;			
		}
		
		Long id = RequestUtils.getLongParameter(request, "id");
		StaffTransfer staffTransfer = staffTransferManager.getStaffTransferById(id);
		StaffInfo staffinfo=staffInfoManager.getStaffInfoById(staffTransfer.getMember_id());
		V3xOrgMember member=orgManagerDirect.getMemberById(staffTransfer.getMember_id());
		
		WebStaffTransfer webStaffTransfer = this.translateToWebStaffTransfer(member, staffTransfer);	
		mav.addObject("staffTransfer", staffTransfer);
		mav.addObject("staffinfo", staffinfo);
		mav.addObject("webStaffTransfer", webStaffTransfer);
		mav.addObject("orgMember", member);
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		
        //获得单位类别下拉列表中的数据
        Map<String, Metadata> orgMeta = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.organization);
        mav.addObject("orgMeta", orgMeta);
		return mav;	
	}
	
	public ModelAndView updateTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String ID = request.getParameter("id");
		String isForm = request.getParameter("isForm");
		if(null==ID || ID.equals("")){
			StaffTransfer transfer = new StaffTransfer();
			int typeid = 0;
			Long staffid = RequestUtils.getLongParameter(request, "staffid");
			int state = 0;
            int type = 0;
            Long toDepartment_id = null;
            Long toPost_id = null;
            Long toLevel_id = null;
			if(null!=isForm && "ture".equals(isForm)){
				Long formid = RequestUtils.getLongParameter(request, "formid");
				String formname = this.getFormName();
				Object[] obj = staffTransferManager.getFormItemById(formname, formid);
				state = Integer.valueOf(obj[12].toString());
				type = Integer.valueOf(obj[16].toString());
				toDepartment_id = (Long)obj[3];
				toPost_id = (Long)obj[6];
				toLevel_id = (Long)obj[7];
			}
			else{
				typeid = RequestUtils.getIntParameter(request, "typeid");
			    bind(request,transfer);
			    state = RequestUtils.getIntParameter(request, "toMember_state");
	            type = RequestUtils.getIntParameter(request, "toMember_type");
	            toDepartment_id = RequestUtils.getLongParameter(request, "toDepartment_id");
	            toPost_id = RequestUtils.getLongParameter(request, "toPost_id");
	            toLevel_id = RequestUtils.getLongParameter(request, "toLevel_id");
			}
            StaffTransferType transferType = new StaffTransferType();
		    transferType.setId(typeid);
		    transfer.setType(transferType);
		    
		    transfer.setState(StaffTransferFlag.CONSENT);
		    
		    Calendar time = Calendar.getInstance();
	        Date date = time.getTime();
		    transfer.setRefer_time(date);
		    
		    transfer.setMember_id(staffid);
		    staffTransferManager.addTransfer(transfer);
		    
		    V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
            
            member.setState((byte)state);
            member.setType((byte)type);
            member.setOrgDepartmentId(toDepartment_id);
            member.setOrgLevelId(toLevel_id);
            member.setOrgPostId(toPost_id);
            orgManagerDirect.updateEntity(member);
		}
		else{
			Long id = Long.valueOf(ID);
			StaffTransfer transfer = staffTransferManager.getStaffTransferById(id);
			int typeid = RequestUtils.getIntParameter(request, "typeid");
			
			bind(request,transfer);
			StaffTransferType transferType = new StaffTransferType();
		    transferType.setId(typeid);
		    transfer.setType(transferType);
		    
		    staffTransferManager.updateTransfer(transfer);
		}
		return super.redirectModelAndView("/hrStaffTransfer.do?method=initHome", "parent.parent.parent");
	}
	
	public ModelAndView searchTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/listTransfer");
		List<Object[]> list = new ArrayList<Object[]>();
		String condition = request.getParameter("condition");
		String content = request.getParameter("content").trim();
		String fname = this.getFormName();
		if(fname!=null){
			if(condition.equals("name")){ //姓名
				list = staffTransferManager.getStaffTransferLikeByName(content, fname);
			}else if(condition.equals("transferType")){ //变动类型
	//			if(content.equals("transfer")){
	//				list=staffTransferManager.getTransferTypeStaffTransfer();
	//			}
	//			else if(content.equals("dimission")){
	//				list=staffTransferManager.getDimissionTypeStaffTransfer();
	//			}
				list = staffTransferManager.getStaffTransferByType(Integer.parseInt(content), fname);
			}else if(condition.equals("state")){ //处理结果
				if(content.equals("consent")){
					list=staffTransferManager.getStaffTransferByState(StaffTransferFlag.CONSENT, fname);
				}
				else if(content.equals("disaccord")){
					list=staffTransferManager.getStaffTransferByState(StaffTransferFlag.DISACCORD, fname);
				}
				else if(content.equals("unsettled")){
					list=staffTransferManager.getStaffTransferByState(StaffTransferFlag.UNSETTLED, fname);
				}
			}else if(condition.equals("referTime")){ //申请时间
	            Date date = Datetimes.parseDate(content);
	            list=staffTransferManager.getStaffTransferByReferTime(date, fname);
			}
		}
			
		//转换为WebStaffTransfer
		List<WebStaffTransfer> webList = this.translateObjectList(list);
		
		
		mav.addObject("formList", this.pagenate(webList));
		return mav;
	}
	
	private WebStaffTransfer translateToWebStaffTransfer(V3xOrgMember member,StaffTransfer stafftransfer)
	   throws Exception {
		WebStaffTransfer webStaffTransfer = new WebStaffTransfer();
        
		if(null!=stafftransfer.getFromDepartment_id()){
		    long fromDeptId = stafftransfer.getFromDepartment_id();
		    V3xOrgDepartment fromDept = orgManagerDirect.getDepartmentById(fromDeptId);
			if (fromDept != null) {
				webStaffTransfer.setFromDepartment_name(fromDept.getName());
				
				V3xOrgAccount account = orgManagerDirect.getAccountById(fromDept.getOrgAccountId());
				if (null != account) {
					webStaffTransfer.setFromAccount_name("("+ account.getShortname() +")");;
				}
			}
		}
		
		if(null!=stafftransfer.getFromLevel_id()){
		    long fromLevelId = stafftransfer.getFromLevel_id();
		    V3xOrgLevel fromLevel = orgManagerDirect.getLevelById(fromLevelId);
		    if (null != fromLevel) {
			   webStaffTransfer.setFromLevel_name(fromLevel.getName());
		    }
		}
		
		if(null!=stafftransfer.getFromPost_id()){
			long fromPostId = stafftransfer.getFromPost_id();
			V3xOrgPost fromPost = orgManagerDirect.getPostById(fromPostId);
			if (null != fromPost) {		
				webStaffTransfer.setFromPost_name(fromPost.getName());
			}
		}
		
		if(null!=stafftransfer.getToDepartment_id()){
			long toDeptId = stafftransfer.getToDepartment_id();
			V3xOrgDepartment toDept = orgManagerDirect.getDepartmentById(toDeptId);
			if (toDept != null) {
				webStaffTransfer.setToDepartment_name(toDept.getName());
				
				V3xOrgAccount account = orgManagerDirect.getAccountById(toDept.getOrgAccountId());
				if (null != account) {
					webStaffTransfer.setToAccount_name("("+ account.getShortname()+ ")");;
				}
			}
		}
		
		if(null!=stafftransfer.getToLevel_id()){
			long toLevelId = stafftransfer.getToLevel_id();
			V3xOrgLevel toLevel = orgManagerDirect.getLevelById(toLevelId);
			if (null != toLevel) {
				webStaffTransfer.setToLevel_name(toLevel.getName());
			}
		}
		
		if(null!=stafftransfer.getToPost_id()){
			long toPostId = stafftransfer.getToPost_id();	
			V3xOrgPost toPost = orgManagerDirect.getPostById(toPostId);
			if (null != toPost) {		
				webStaffTransfer.setToPost_name(toPost.getName());
			}
		}
		 
		
		webStaffTransfer.setFromMember_type(member.getType());
		webStaffTransfer.setFromMember_state(member.getState());
		
		webStaffTransfer.setId(stafftransfer.getId());
		webStaffTransfer.setName(member.getName());
		webStaffTransfer.setCode(member.getCode());
	
//		if(stafftransfer.getType().getId()==StaffTransferFlag.DIMISSION){
//			webStaffTransfer.setTransferType(StaffTransferFlag.DIMISSIONS);
//		}
//		else{
//			webStaffTransfer.setTransferType(StaffTransferFlag.TRANSFER);
//		}
		//设置变动类型
		webStaffTransfer.setStaffTransferType(stafftransfer.getType());
		
		webStaffTransfer.setState(stafftransfer.getState());
		webStaffTransfer.setRefer_time(stafftransfer.getRefer_time());
		webStaffTransfer.setDeal_time(stafftransfer.getDeal_time());
		 

		return webStaffTransfer;
	}
	
	private List<WebStaffTransfer> translateList(List<StaffTransfer> stafftransfers)throws Exception{
		List<WebStaffTransfer> results = new ArrayList<WebStaffTransfer>();
		if(null!=stafftransfers && stafftransfers.size()>0){
			for(StaffTransfer stafftransfer : stafftransfers){
				V3xOrgMember member=orgManagerDirect.getMemberById(stafftransfer.getMember_id());
				results.add(translateToWebStaffTransfer(member, stafftransfer));
			}
		}
		return results;
	}
	
	public String[] getIds(String strIds) {
		if (null != strIds && !strIds.equals("")) {
			strIds = strIds.substring(0, strIds.lastIndexOf(','));
			String[] arrIds = strIds.split(",");
			return arrIds;
		}
		return null;
	}

	
	public ModelAndView deleteTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffTransferManager.deleteTransfer(id);
			}
		}
		return super.redirectModelAndView("/hrStaffTransfer.do?method=initHome", "parent.parent");
	}

	
	/*---------------------------------------- 2007-09-12 ---------------------------------------------*/
	
	
	public ModelAndView initListForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/listTransfer");

		String name=this.getFormName();	
		List<Object[]> list = new ArrayList<Object[]>();
		if(name!=null){
			list = staffTransferManager.getFormByName(name);
		}
		mav.addObject("formList", this.pagenate(this.translateObjectList(list)));

		return mav;
	}
	
	private  List<WebStaffTransfer> translateObjectList(List<Object[]> list) throws Exception{
		List<WebStaffTransfer> results = new ArrayList<WebStaffTransfer>();
		
		if(null!=list && list.size()>0){
			for(Object[] obj : list){	
				WebStaffTransfer webStaffTransfer = new WebStaffTransfer();
				V3xOrgMember member=orgManagerDirect.getMemberById(Long.valueOf(obj[21].toString()));
				StaffTransferType staffTransferType = staffTransferManager.getStaffTransferTypeById(Integer.valueOf(obj[17].toString()));
				webStaffTransfer.setName(member.getName());
				webStaffTransfer.setStaffTransferType(staffTransferType);
				if(obj[14]!=null){
					webStaffTransfer.setState(Integer.valueOf(obj[14].toString()));
				}
				if(obj[18]!=null){
					webStaffTransfer.setTransfer_time(Datetimes.parse(obj[18].toString(), "yyyy-MM-dd"));
				}
				webStaffTransfer.setRefer_time(Datetimes.parse(obj[13].toString(), "yyyy-MM-dd"));
				webStaffTransfer.setId(Long.valueOf(obj[0].toString()));
				webStaffTransfer.setCode(member.getCode());
				results.add(webStaffTransfer);
			}
		}
		return results;
	}
	
	public ModelAndView detailTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffTransfer/detailTransfer");
		Long id = RequestUtils.getLongParameter(request, "id");
		String xml = staffTransferManager.getFormXMLById(id,this.getFormName());
		mav.addObject("formXML", xml);
		return mav;
	} 
	
	public ModelAndView dealFormItem(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		Long id = RequestUtils.getLongParameter(request, "id");
		String fname=this.getFormName();
		staffTransferManager.dealFormItemById(fname, id);
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage(\"HRLang.hr_staffInfo_operationSuccessful_label\"))");
			out.println("</script>");
		} catch (IOException e) {
			LOG.error("", e);
		}
		return super.refreshWorkspace();
	}
	
	private String getFormName(){
		String aAppName = TempleteHelper.getInstance().getFName("4");
		String name = null;
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(aAppName);
	    SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl)afapp;
	    if(sapp!=null){
	    	SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
	    	name=seedade.getDataDefine().getTableLst().get(0).getName();
	    }
		return name;
	}
	
	public ModelAndView dropTransfer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		for (String strID : arrIDs) {
			if (null != strID && !strID.equals("")) {
				String[] arrIdAndName = strID.split("_");
				Long id = Long.parseLong(arrIdAndName[0]);
				staffTransferManager.deleteFormItemById(this.getFormName(), id, arrIdAndName[1]);
			}	
		}
		return super.refreshWorkspace();//super.redirectModelAndView("/hrStaffTransfer.do?method=initHome", "parent.parent");
	}
	

	
	
}