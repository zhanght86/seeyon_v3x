package com.seeyon.v3x.collaboration.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.oainterface.impl.V3xManagerFactory;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.his.manager.HisAffairManager;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseDealModel;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ColSuperviseController extends BaseController {

	private ColSuperviseManager colSuperviseManager;

	private AffairManager affairManager;
	
	private ColManager colManager;

	private MetadataManager metadataManager;

	private OrgManager orgManager;

	private EdocSummaryManager edocSummaryManager;
    
    private TempleteManager templeteManager;
    
    private PortletEntityPropertyManager portletEntityPropertyManager;
    
    private HisAffairManager hisAffairManager;
    
    private HisColManager hisColManager;
    
    private EdocManager edocManager;

    public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}

    public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}
    
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }
	
    public void setHisAffairManager(HisAffairManager hisAffairManager) {
		this.hisAffairManager = hisAffairManager;
	}

    public void setHisColManager(HisColManager hisColManager) {
		this.hisColManager = hisColManager;
	}

    /**
	 * 协同督办打开的督办窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindowEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseIframe");
		String isFromEdoc = request.getParameter("isFromEdoc");
		if (!Strings.isBlank(isFromEdoc)) {
			mav.addObject("title", "col.supervise.label");
		} else {
			mav.addObject("title", "col.supervise.label");
		}
		return mav;
	}

	public ModelAndView superviseWindowForEdocZCDB(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseWindow");

		mav.addObject("title", "edoc.supervise.label");

		mav.addObject("supervisorId", request.getParameter("supervisorId"));
		mav.addObject("supervisors", request.getParameter("supervisors"));
		mav.addObject("superviseTitle", request.getParameter("superviseTitle"));
		mav.addObject("awakeDate", request.getParameter("awakeDate"));
		mav.addObject("canModify", request.getParameter("canModify"));
		mav.addObject("unCancelledVisor", request
				.getParameter("unCancelledVisor"));
		mav.addObject("sVisorsFromTemplate", request
				.getParameter("sVisorsFromTemplate"));
		mav.addObject("temformParentId", request
				.getParameter("temformParentId"));
		return mav;
	}

	/**
	 * 协同督办选择窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseWindow");
		String isFromEdoc = request.getParameter("isFromEdoc");
		if (!Strings.isBlank(isFromEdoc)) {
			mav.addObject("title", "edoc.supervise.label");
			mav.addObject("isFromEdoc", "true");
		} else {
			mav.addObject("title", "col.supervise.label");
		}
		String summaryId = request.getParameter("summaryId");
		String currentPage=request.getParameter("currentPage");//当前页面
		if (summaryId != null && !"".equals(summaryId) && !"newEdoc".equals(currentPage)) {
			Set<String> idSets = new HashSet<String>();
			StringBuffer supervisorId = new StringBuffer();
			Set<String> tempIdSets = new HashSet<String>();	
			ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(
					Constant.superviseType.summary.ordinal(), Long
							.parseLong(summaryId));
			ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), false);
			//已結束的流程不能设置督办
			if(summary != null && summary.getFinishDate() != null){
			    super.rendJavaScript(response, "alert('"+ Constant.getString4CurrentUser("col.supervise.cannotSet.isFinished") +"');window.close();");
			    return super.refreshWorkspace();
			}
			if (detail != null) {
				if (null != summary && null != summary.getTempleteId()) {
					ColSuperviseDetail tempDetail = colSuperviseManager
							.getSupervise(Constant.superviseType.template
									.ordinal(), summary.getTempleteId());
					if (null != tempDetail) {
						Set<ColSupervisor> tempVisors = tempDetail
								.getColSupervisors();
						for (ColSupervisor ts : tempVisors) {
							idSets.add(ts.getSupervisorId().toString());
							tempIdSets.add(ts.getSupervisorId().toString());
						}
						List<SuperviseTemplateRole> roleList = colSuperviseManager
						.findSuperviseRoleByTemplateId(summary
								.getTempleteId());
						V3xOrgRole orgRole = null;
						User user = CurrentUser.get();

				for (SuperviseTemplateRole role : roleList) {
					if (null == role.getRole() || "".equals(role.getRole())) {
						continue;
					}
					if (role.getRole().toLowerCase().equals(
							V3xOrgEntity.ORGENT_META_KEY_SEDNER
									.toLowerCase())) {
						tempIdSets.add(String.valueOf(user.getId()));
					}
					if (role
							.getRole()
							.toLowerCase()
							.equals(
									V3xOrgEntity.ORGENT_META_KEY_SEDNER
											.toLowerCase()
											+ V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER
													.toLowerCase())) {
						orgRole = orgManager.getRoleByName(
								V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER,
								user.getLoginAccount());
						if (null != orgRole) {
							List<V3xOrgDepartment> depList = orgManager
									.getDepartmentsByUser(user.getId());
							for (V3xOrgDepartment dep : depList) {
								List<V3xOrgMember> managerList = orgManager
										.getMemberByRole(
												V3xOrgEntity.ROLE_BOND_DEPARTMENT,
												dep.getId(), orgRole
														.getId());
								for (V3xOrgMember mem : managerList) {
									tempIdSets.add(mem.getId().toString());
								}
							}
						}
					}
					if (role
							.getRole()
							.toLowerCase()
							.equals(
									V3xOrgEntity.ORGENT_META_KEY_SEDNER
											.toLowerCase()
											+ V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER
													.toLowerCase())) {
						orgRole = orgManager
								.getRoleByName(
										V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER,
										user.getLoginAccount());
						if (null != orgRole) {
							List<V3xOrgDepartment> depList = orgManager
									.getDepartmentsByUser(user.getId());
							for (V3xOrgDepartment dep : depList) {
								List<V3xOrgMember> superManagerList = orgManager
										.getMemberByRole(
												V3xOrgEntity.ROLE_BOND_DEPARTMENT,
												dep.getId(), orgRole
														.getId());
								for (V3xOrgMember mem : superManagerList) {
									tempIdSets.add(mem.getId().toString());
								}
							}
						}
					}
				}

				StringBuffer ids = new StringBuffer();
				for (String s : tempIdSets) {
					ids.append(s);
					ids.append(",");
				}

				if (ids.length() > 1) {
					mav.addObject("unCancelledVisor", ids.substring(0, ids
							.length() - 1));
				}
					}
				}

				Set<ColSupervisor> supervisors = detail.getColSupervisors();
				for (ColSupervisor supervisor : supervisors) {
					idSets.add(supervisor.getSupervisorId().toString());
				}
				for (String id : idSets) {
					supervisorId.append(id + ",");
				}
				if (supervisorId.length() > 0) {
					mav.addObject("supervisorId", supervisorId.substring(0,
							supervisorId.length() - 1));
				}
				mav.addObject("superviseId", detail.getId());
				mav.addObject("supervisors", detail.getSupervisors());
				mav.addObject("superviseTitle", detail.getTitle());
				mav.addObject("awakeDate", Datetimes.format(detail
						.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
				mav.addObject("sVisorsFromTemplate", "true");
				// mav.addObject("canModify", detail.isCanModify());
			}
			mav.addObject("submitIt", "1");
		} else {
			mav.addObject("supervisorId", request.getParameter("supervisorId"));
			mav.addObject("supervisors", request.getParameter("supervisors"));
			mav.addObject("superviseTitle", request
					.getParameter("superviseTitle"));
			mav.addObject("awakeDate", request.getParameter("awakeDate"));
			mav.addObject("canModify", request.getParameter("canModify"));
			mav.addObject("unCancelledVisor", request
					.getParameter("unCancelledVisor"));
			mav.addObject("sVisorsFromTemplate", request
					.getParameter("sVisorsFromTemplate"));
			mav.addObject("temformParentId", request
					.getParameter("temformParentId"));
		}
		return mav;
	}

	public ModelAndView edocSuperviseWindowEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseIframeForEdoc");
		return mav;
	}

	public ModelAndView edocSuperviseWindow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseWindowForEdoc");
		String summaryId = request.getParameter("summaryId");
		if (summaryId != null && !"".equals(summaryId)) {
			Set<String> idSets = new HashSet<String>();
			StringBuffer supervisorId = new StringBuffer(); // supervisorId :
															// all the ids of
															// supervise detail
			//StringBuffer tempIds = new StringBuffer(); // tempIds : all the ids
			Set<String> tempIdSets = new HashSet<String>();											// of superviseTemplate
			ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(
					Constant.superviseType.edoc.ordinal(), Long
							.parseLong(summaryId));
			if (detail != null) {
				EdocSummary summary = edocSummaryManager.findById(Long
						.valueOf(summaryId));
				if (null != summary && null != summary.getTempleteId()) {
					ColSuperviseDetail tempDetail = colSuperviseManager
							.getSupervise(Constant.superviseType.template
									.ordinal(), summary.getTempleteId());
					if (null != tempDetail) {
						Set<ColSupervisor> tempVisors = tempDetail
								.getColSupervisors();
						for (ColSupervisor ts : tempVisors) {
							idSets.add(ts.getSupervisorId().toString());
							tempIdSets.add(ts.getSupervisorId().toString());
						}
						List<SuperviseTemplateRole> roleList = colSuperviseManager
						.findSuperviseRoleByTemplateId(summary
								.getTempleteId());
						V3xOrgRole orgRole = null;
						User user = CurrentUser.get();

				for (SuperviseTemplateRole role : roleList) {
					if (null == role.getRole() || "".equals(role.getRole())) {
						continue;
					}
					if (role.getRole().toLowerCase().equals(
							V3xOrgEntity.ORGENT_META_KEY_SEDNER
									.toLowerCase())) {
						tempIdSets.add(String.valueOf(user.getId()));
					}
					if (role
							.getRole()
							.toLowerCase()
							.equals(
									V3xOrgEntity.ORGENT_META_KEY_SEDNER
											.toLowerCase()
											+ V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER
													.toLowerCase())) {
						orgRole = orgManager.getRoleByName(
								V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER,
								user.getLoginAccount());
						if (null != orgRole) {
							List<V3xOrgDepartment> depList = orgManager
									.getDepartmentsByUser(user.getId());
							for (V3xOrgDepartment dep : depList) {
								List<V3xOrgMember> managerList = orgManager
										.getMemberByRole(
												V3xOrgEntity.ROLE_BOND_DEPARTMENT,
												dep.getId(), orgRole
														.getId());
								for (V3xOrgMember mem : managerList) {
									tempIdSets.add(mem.getId().toString());
								}
							}
						}
					}
					if (role
							.getRole()
							.toLowerCase()
							.equals(
									V3xOrgEntity.ORGENT_META_KEY_SEDNER
											.toLowerCase()
											+ V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER
													.toLowerCase())) {
						orgRole = orgManager
								.getRoleByName(
										V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER,
										user.getLoginAccount());
						if (null != orgRole) {
							List<V3xOrgDepartment> depList = orgManager
									.getDepartmentsByUser(user.getId());
							for (V3xOrgDepartment dep : depList) {
								List<V3xOrgMember> superManagerList = orgManager
										.getMemberByRole(
												V3xOrgEntity.ROLE_BOND_DEPARTMENT,
												dep.getId(), orgRole
														.getId());
								for (V3xOrgMember mem : superManagerList) {
									tempIdSets.add(mem.getId().toString());
								}
							}
						}
					}
				}

				StringBuffer ids = new StringBuffer();
				for (String s : tempIdSets) {
					ids.append(s);
					ids.append(",");
				}

				if (ids.length() > 1) {
					mav.addObject("unCancelledVisor", ids.substring(0, ids
							.length() - 1));
				}
					}
				}

				Set<ColSupervisor> supervisors = detail.getColSupervisors();
				for (ColSupervisor supervisor : supervisors) {
					idSets.add(supervisor.getSupervisorId().toString());
				}
				for (String id : idSets) {
					supervisorId.append(id + ",");
				}
				if (supervisorId.length() > 0) {
					mav.addObject("supervisorId", supervisorId.substring(0,
							supervisorId.length() - 1));
				}
				mav.addObject("superviseId", detail.getId());
				mav.addObject("supervisors", detail.getSupervisors());
				mav.addObject("superviseTitle", detail.getTitle());
				mav.addObject("awakeDate", Datetimes.format(detail
						.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
				mav.addObject("sVisorsFromTemplate", "true");
				// mav.addObject("canModify", detail.isCanModify());
			}
			mav.addObject("submitIt", "1");
		} else {
			mav.addObject("supervisorId", request.getParameter("supervisorId"));
			mav.addObject("supervisors", request.getParameter("supervisors"));
			mav.addObject("superviseTitle", request
					.getParameter("superviseTitle"));
			mav.addObject("awakeDate", request.getParameter("awakeDate"));
			mav.addObject("canModify", request.getParameter("canModify"));
			mav.addObject("unCancelledVisor", request
					.getParameter("unCancelledVisor"));
			mav.addObject("sVisorsFromTemplate", request
					.getParameter("sVisorsFromTemplate"));
		}
		return mav;
	}

	/**
	 * 协同督办打开的督办窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindowEntryForTemplate(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseIframeForTemplate");
		return mav;
	}

	/**
	 * 协同督办选择窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindowForTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseWindowForTemplate");
		String isFromEdoc = request.getParameter("isFromEdoc");
		if (!Strings.isBlank(isFromEdoc)) {
			mav.addObject("title", "edoc.supervise.label");
		} else {
			mav.addObject("title", "col.supervise.label");
		}
		String summaryId = request.getParameter("summaryId");
		if (summaryId != null && !"".equals(summaryId)) {
			ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(
					Constant.superviseType.summary.ordinal(), Long
							.parseLong(summaryId));
			if (detail != null) {
				StringBuffer supervisorId = new StringBuffer();
				Set<ColSupervisor> supervisors = detail.getColSupervisors();
				for (ColSupervisor supervisor : supervisors)
					supervisorId.append(supervisor.getSupervisorId() + ",");
				if (supervisorId.length() > 0)
					mav.addObject("supervisorId", supervisorId.substring(0,
							supervisorId.length() - 1));
				mav.addObject("superviseId", detail.getId());
				mav.addObject("supervisors", detail.getSupervisors());
				mav.addObject("superviseTitle", detail.getTitle());
				mav.addObject("awakeDate", Datetimes.format(detail
						.getAwakeDate(), Datetimes.dateStyle));

				List<SuperviseTemplateRole> roleList = colSuperviseManager
						.findSuperviseRoleByTemplateId(detail.getId());
				for (SuperviseTemplateRole role : roleList) {
					mav.addObject(role.getRole(), role.getRole());
				}

				// mav.addObject("canModify", detail.isCanModify());
			}
			mav.addObject("submitIt", "1");
		} else {
			mav.addObject("supervisorId", request.getParameter("supervisorId"));
			mav.addObject("supervisors", request.getParameter("supervisors"));
			mav.addObject("superviseTitle", request
					.getParameter("superviseTitle"));
			mav.addObject("awakeDate", request.getParameter("awakeDate"));
			mav.addObject("canModify", request.getParameter("canModify"));

			String role = request.getParameter("role");
			if (!Strings.isBlank(role) && role.length() > 0) {
				String[] strs = role.split(",");
				for (int i = 0; i < strs.length; i++) {
					mav.addObject(strs[i], strs[i]);
				}
			}
		}
		return mav;
	}

	public ModelAndView mainEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
		"collaboration/supervise/superviseListIframe");
		mav.addObject("entry", "superviseList");
		return mav;
	}
	public ModelAndView frameSetEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
		"collaboration/supervise/superviseListMain");
		String status = request.getParameter("status");
		mav.addObject("entry", "superviseList");
		mav.addObject("status", status);
		return mav;
	}

	/**
	 * 协同督办列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("collaboration/supervise/superviseList");
		if(!FormBizConfigUtils.validate(mv, request, response, FormBizConfigConstants.MENU_SUPERWISE_AFFAIRS))
			return null;
		
		String statusStr = request.getParameter("status");

		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		String textfield1 = request.getParameter("textfield1");
		int status = Constant.superviseState.supervising.ordinal();
		String label = "col.supervise.transacted.without";
		if (Strings.isNotBlank(statusStr)) {
			status = Integer.parseInt(statusStr);
		}
		if (status == Constant.superviseState.supervised.ordinal()) {
			label = "col.supervise.transacted.done";
		}
		mv.addObject("label", label);

        List<Long> templeteIds = null;
        String templeteIdsStr = request.getParameter("tempIds");
        if(Strings.isNotBlank(templeteIdsStr)){
            templeteIds = new ArrayList<Long>();
            StringTokenizer token = new StringTokenizer(templeteIdsStr, ",");
            while(token.hasMoreTokens()){
                templeteIds.add(Long.parseLong(token.nextToken()));
            }
        }
        
		// List<ColSuperviseModel> list =
		// this.colSuperviseManager.getMySupervise(CurrentUser.get().getId(),
		// status);
        //成发集团项目 程炯 2012-9-3 增加通过人员密级筛选督办列表的功能
        V3xOrgMember member	= orgManager.getMemberById(CurrentUser.get().getId());
		List<ColSuperviseModel> list = this.colSuperviseManager
				.getSuperviseCollListByCondition(condition, textfield, 
						textfield1, CurrentUser.get().getId(), status, templeteIds,member.getSecretLevel());
		mv.addObject("status", status);
		mv.addObject("superviseDetails", list);
		Map<String, Metadata> colMetadata = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.collaboration);
		mv.addObject("colMetadata", colMetadata);

		Metadata comImportanceMetadata = metadataManager
				.getMetadata(MetadataNameEnum.common_importance);
		mv.addObject("comImportanceMetadata", comImportanceMetadata);
		return mv;
	}
	
	/**
	 * 返回的是协同的流程查看页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));
		
		Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId);
		try{
    		String msg = this.colSuperviseManager.checkColSupervisor(summaryId, affair);
    		if(Strings.isNotBlank(msg)){
    		    throw new ColException(msg);
		    }
		    
            V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
            ColSummary colsummary = colManager.getColSummaryById(summaryId, true);
            if(colsummary != null){
                if(member.getSecretLevel()< colsummary.getSecretLevel()){
                    throw new ColException("涉密等级不够，无法查看！");
                }
            }
            
		}catch(ColException e){
        	PrintWriter out = response.getWriter();
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
        	out.println("if(window.dialogArguments){");
        	out.println("  window.returnValue = \"true\";");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  parent.getA8Top().reFlesh();");
        	out.println("}");
        	out.println("");
        	out.println("</script>");
        	return null;
        }
		
		ModelAndView mv = new ModelAndView("collaboration/supervise/summaryDetail");
		mv.addObject("from", "Done");
		String openModal = request.getParameter("openModal");
		if("list".equals(openModal)){
			mv.addObject("openModal", "list");
		}else{
			mv.addObject("openModal", "popup");
		}
		mv.addObject("affairId", affair.getId());
		
		return mv;
	}

	/**
	 * 显示更改流程
	 * 此显示流程图方法不仅在协同督办部分被调用显示流程图，在流程分析中也调用此方法显示实例流程图
	 * isSupervise: false 		   ：表示只显示流程图，不进行督办验证
	 * 				true,null,'' ：督办显示流程图
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDigarm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.noCache(response);
		
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));
		// 是否是督办查看流程标志,流程分析中
		String isSuperviseStr = request.getParameter("isSupervise");
		Boolean isSupervise = Strings.isBlank(isSuperviseStr) || (Strings.isNotBlank(isSuperviseStr) && Boolean.parseBoolean(isSuperviseStr));
		
		Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId);
		
		if (isSupervise) {
			String m = this.colSuperviseManager.checkColSupervisor(summaryId, affair);
	        if(Strings.isNotBlank(m)){
	        	PrintWriter out = response.getWriter();
	        	out.println("<script>");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(m) + "\")");
	        	out.println("window.returnValue='"+REDIRECT_BACK+"';");
	        	out.println("top.close();");
	        	out.println("</script>");
	        	return null;
	        }
		} 
		
		ModelAndView mav = new ModelAndView("collaboration/supervise/showDiagram");
		mav.addObject("isSupervise", isSupervise);
		
		Boolean hasWorkflow = false; // 是否还存在流程
		String process_desc_by = ""; // 路程排序
		boolean hasDiagram = false;

		ColSummary summary = colManager.getColSummaryById(summaryId, false);
		if (summary != null && summary.getCaseId() != null) {
			
			if (isSupervise) {
				if (summary.getFinishDate() != null) {
					PrintWriter out = response.getWriter();
					String msg = Constant
							.getString4CurrentUser("col.process.finished");
					out.println("<script>");
					out.println("alert(\""
							+ StringEscapeUtils.escapeJavaScript(msg) + "\")");
					out.println("if(window.dialogArguments){"); // 弹出
					out.println("  window.returnValue = \"" + DATA_NO_EXISTS
							+ "\";");
					out.println("  window.close();");
					out.println("}else{");
					out.println("  parent.getA8Top().reFlesh();");
					out.println("}");
					out.println("</script>");
					out.close();
					return null;
				}
			}
			hasDiagram = true;
            //TODO 如果是表单协同，判断是否关联有新流程
            if("FORM".equals(summary.getBodyType())){
                
            }
            if(summary.getTempleteId() != null){
                List<ColBranch> branchs = templeteManager.getBranchsByTemplateId(summary.getTempleteId(), ApplicationCategoryEnum.collaboration.ordinal());
                if(branchs != null){
                    //显示分支条件使用流程中保留的，如果为空使用模板中的
                    branchs = ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
                }
                mav.addObject("branchs", ColHelper.transformBranch(branchs,true));
            }
		}
		ApplicationCategoryEnum app = ApplicationCategoryEnum.collaboration;

		FlowData flowData = EdocHelper.getRunningProcessPeople(summary
				.getProcessId());
		if (flowData != null)
			hasWorkflow = Boolean.TRUE;
		Map<String, Metadata> colMetadata = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.collaboration);
		Metadata comMetadata = metadataManager
				.getMetadata(MetadataNameEnum.common_remind_time);
		process_desc_by = FlowData.DESC_BY_XML;

		Metadata remindMetadata = metadataManager
				.getMetadata(MetadataNameEnum.common_remind_time);
		Metadata deadlineMetadata = metadataManager
				.getMetadata(MetadataNameEnum.collaboration_deadline);

		mav.addObject("remindMetadata", remindMetadata);
		mav.addObject("deadlineMetadata", deadlineMetadata);

		mav.addObject("appName", app.name());
		mav.addObject("affairId", affair.getId());
		mav.addObject("comMetadata", comMetadata);
		mav.addObject("colMetadata", colMetadata);
		mav.addObject("summary", summary);
		mav.addObject("isShowButton", false);
		mav.addObject("hasDiagram", hasDiagram);
		mav.addObject("process_desc_by", process_desc_by);
		mav.addObject("hasWorkflow", hasWorkflow);
		mav.addObject("caseId", summary.getCaseId());
		mav.addObject("processId", summary.getProcessId());
		if(summary != null){//成发集团
			mav.addObject("secretLevel", summary.getSecretLevel());
		}
		if(null!=affair.getTempleteId() && affair.getTempleteId().longValue()!=-1){//是模板流程
			mav.addObject("isFromTemplete", true);
		}else{
			mav.addObject("isFromTemplete", false);
		}

		return mav;
	}

	/**
	 * 用于在列表上只显示流程图(没有调用showDigram),去掉了内容和流程处理部分.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDigramOnly(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		super.noCache(response);
		
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));
		boolean isStoreFlag = false; //是否转储标记
		Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId);
		if(affair == null){
			affair = this.hisAffairManager.getCollaborationSenderAffair(summaryId);
			isStoreFlag = affair != null;
		}
		
		String m = this.colSuperviseManager.checkColSupervisor(summaryId, affair);
        if(Strings.isNotBlank(m)){
        	super.infoCloseOrFresh(request, response, m);
        	return null;
        }
        
		ModelAndView mav = new ModelAndView("collaboration/supervise/superviseDiagram");

		Boolean hasWorkflow = false; // 是否还存在流程
		String process_desc_by = ""; // 路程排序
		boolean hasDiagram = false;

		ColSummary summary = null;
		if(isStoreFlag){
			summary = hisColManager.getColSummaryById(summaryId, false);
		}
		else{
			summary = colManager.getColSummaryById(summaryId, false);
		}
		
		if (summary != null && summary.getCaseId() != null) {
			hasDiagram = true;
            if(summary.getTempleteId() != null){
                List<ColBranch> branchs = templeteManager.getBranchsByTemplateId(summary.getTempleteId(), ApplicationCategoryEnum.collaboration.ordinal());
                if(branchs != null){
                    //显示分支条件使用流程中保留的，如果为空使用模板中的
                    branchs = ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
                }
                mav.addObject("branchs", ColHelper.transformBranch(branchs,true));
            }
		}
		ApplicationCategoryEnum app = ApplicationCategoryEnum.collaboration;

		FlowData flowData = EdocHelper.getProcessPeople(summary.getProcessId());
		if (flowData != null)
			hasWorkflow = Boolean.TRUE;

		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(app);
		Metadata comMetadata = metadataManager
				.getMetadata(MetadataNameEnum.common_remind_time);
		process_desc_by = FlowData.DESC_BY_XML;

		Metadata remindMetadata = metadataManager
				.getMetadata(MetadataNameEnum.common_remind_time);
		Metadata deadlineMetadata = metadataManager
				.getMetadata(MetadataNameEnum.collaboration_deadline);

		mav.addObject("remindMetadata", remindMetadata);
		mav.addObject("deadlineMetadata", deadlineMetadata);

		mav.addObject("appName", app.name());
		mav.addObject("affairId", affair.getId());
		mav.addObject("comMetadata", comMetadata);
		mav.addObject("colMetadata", colMetadata);
		mav.addObject("summary", summary);
		mav.addObject("isShowButton", false);
		mav.addObject("hasDiagram", hasDiagram);
		mav.addObject("process_desc_by", process_desc_by);
		mav.addObject("hasWorkflow", hasWorkflow);
		mav.addObject("superviseId", request.getParameter("superviseId"));
		mav.addObject("caseId", summary.getCaseId());
		mav.addObject("processId", summary.getProcessId());

		return mav;
	}

	/**
	 * 督办人催办时，记录催办日志并增加催办次数。发起人催办时，不记录催办日志和增加催办次数。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView hasten(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String content = request.getParameter("content");
		String summaryId = request.getParameter("summaryId");
		String superviseId = request.getParameter("superviseId");
		
		if(Strings.isNotBlank(summaryId)) {
			ColSuperviseDetail detail = colSuperviseManager.getSuperviseDetailByEntityId(Long.parseLong(summaryId));
			if(detail!=null) {
				superviseId = detail.getId().toString();
			} else {
				// 协同催办
				ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), false);
				if (summary!=null) {
					long senderId[] = {summary.getStartMemberId()};
					colSuperviseManager.save(0, "", "", summary.getStartMemberId(), "", "", senderId, new Date(), 0, Long.parseLong(summaryId), 0, false, "");
				} else {
					// 公文催办
					EdocSummary edocSummary = edocManager.getEdocSummaryById(Long.parseLong(summaryId), false);
					if(edocSummary!=null) {
						long senderId[] = {edocSummary.getStartMember().getId()};
						colSuperviseManager.save(0, "", "", edocSummary.getStartMember().getId(), "", "", senderId, new Date(), 0, Long.parseLong(summaryId), 0, false, "");
					}
				}
				detail = colSuperviseManager.getSuperviseDetailByEntityId(Long.parseLong(summaryId));
				if (detail!=null)
					superviseId = detail.getId().toString();
			}
		}
		
		String[] people = request.getParameterValues("deletePeople");
		String message =  Constant.getString4CurrentUser("hasten.success.label");
        if(people != null && people.length > 0){
        	List<Long> receivers = new ArrayList<Long>(people.length);
        	for (String p : people) {
        		receivers.add(Long.parseLong(p));
        	}
        	
        	List<Long> notHas = colManager.hasten(summaryId, receivers, content);
        	if(notHas != null && !notHas.isEmpty()){
        		StringBuffer memberNames = new StringBuffer();
        		for(Long id:notHas){
        			if(memberNames.length() !=0){
        				memberNames.append(",");
        			}
        			memberNames.append(Functions.showMemberName(id));
        		}
        		message = Constant.getString4CurrentUser("hasten.fail.label",memberNames);
        	}
        	
        	//督办人进行督办时，才增加催办次数，并使列表中对应的督办次数在督办完成后同步更新
        	if(Strings.isNotBlank(superviseId)) {
				//增加的内容 = 新集合 - (新集合与旧集合的交集)
				Collection<Long> common = CollectionUtils.intersection(receivers, notHas);
				List<Long> result = new ArrayList<Long>(CollectionUtils.subtract(receivers, common));
        		Long id = Long.parseLong(superviseId);
        		this.colSuperviseManager.saveLog(id, CurrentUser.get().getId(), result, content);
        		super.rendJavaScript(response, "alert('" + message + "');" +
        									   "parent.setHastenTimesBack('" + this.colSuperviseManager.getHastenTimes(id)+ "');");
        		return null;
        	}
        }

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + message + "');");
		out.println("parent.close();");
		out.println("</script>");

		return null;
	}

	/**
	 * logEntry为superviseLog的入口,在superviseLog外边套一层框架
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView logEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		super.noCache(response);
		
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));
		
		Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId);
		if(affair == null){ //转储数据
			affair = this.hisAffairManager.getCollaborationSenderAffair(summaryId);
		}
		
		String m = this.colSuperviseManager.checkColSupervisor(summaryId, affair);
        if(Strings.isNotBlank(m)){
        	PrintWriter out = response.getWriter();
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(m) + "\")");
        	out.println("top.close();");
        	out.println("</script>");
        	return null;
        }
        
		String superviseId = request.getParameter("superviseId");
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseLogIframe");
		return mav.addObject("superviseId", superviseId);
	}

	/**
	 * 根据superviseId(督办的id)查出该督办下的所有日志
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showLog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"collaboration/supervise/superviseLog");
		String superviseId = request.getParameter("superviseId");
		List<ColSuperviseLog> logList = this.colSuperviseManager
				.getLogByDetailId(Long.valueOf(superviseId));

		return mav.addObject("logList", logList);
	}

	/**
	 * 修改督办的提醒时间
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView change(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String superviseId = request.getParameter("superviseId");
		String awakeDate = request.getParameter("awakeDate");
		String subject = request.getParameter("subject");
		if (null != superviseId && null != awakeDate) {
			this.colSuperviseManager.changeAwakeDate(Long
					.parseLong(superviseId), CurrentUser.get().getId(),
					Datetimes.parse(awakeDate, Datetimes.dateStyle), subject);
		}

		return super.refreshWorkspace();
	}	
	public ModelAndView changeSupervise(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String superviseId = request.getParameter("superviseId");
		String awakeDate = request.getParameter("awakeDate");
		String description = request.getParameter("description");
		ColSuperviseDetail detail = this.colSuperviseManager.get(Long.valueOf(superviseId));
		if(detail!=null){
			if(Strings.isNotBlank(awakeDate)){
				detail.setAwakeDate(Datetimes.parse(awakeDate, Datetimes.dateStyle));
			}
			if(Strings.isNotBlank(description)){
				detail.setDescription(description);
			}else{
				detail.setDescription("");
			}
		}
		this.colSuperviseManager.updateDetail(detail);
		return null;
	}
	/**
	 * 查看督办的内容摘要
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDescription(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		super.noCache(response);
		
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));
		
		Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId);
		String m = this.colSuperviseManager.checkColSupervisor(summaryId, affair);
        if(Strings.isNotBlank(m)){
        	PrintWriter out = response.getWriter();
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(m) + "\")");
        	out.println("top.close();");
        	out.println("</script>");
        	return null;
        }
        
		ModelAndView mv = new ModelAndView(
				"collaboration/supervise/superviseDescription");
		String superviseId = request.getParameter("superviseId");
		if (null != superviseId && !"".equals(superviseId)) {
			ColSuperviseDetail detail = colSuperviseManager.get(Long
					.parseLong(superviseId));
			if (null != detail) {
				mv.addObject("content", detail.getDescription());
				mv.addObject("title", detail.getTitle());
				mv.addObject("status", detail.getStatus());
			}
		}
		return mv;
	}

	/**
	 * 更改督办的内容摘要
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateContent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String content = request.getParameter("content");
		String superviseId = request.getParameter("superviseId");
		if (null != superviseId && null != content) {
			this.colSuperviseManager.updateContent(Long.parseLong(superviseId),
					content);
		}
		//String msg = Constant
		//		.getString4CurrentUser("col.supervise.setDescriptionOk");
		PrintWriter out = response.getWriter();
		out.println("<script>");
		/*
		out.println("alert('" + msg + "');");
		*/
		out.println("parent.closeWindow();");
		out.println("</script>");
		out.close();
		return null;
	}

	/**
	 * 删除已办结协同督办
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteSuperviseDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String ids = request.getParameter("id");

		if (null != ids && !"".equals(ids)) {
			this.colSuperviseManager.deleteSupervised(
					CurrentUser.get().getId(), ids);
		}

		return super.refreshWindow("parent");
	}

	/**
	 * 显示办理情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAffair(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String summaryId = request.getParameter("summaryId");
		List<ColSuperviseDealModel> models = this.colSuperviseManager
				.getAffairModel(Long.parseLong(summaryId));
		ModelAndView mv = new ModelAndView("collaboration/supervise/showAffair");
		mv.addObject("models", models);
		return mv;
	}

	/**
	 * 在已发列表中新建/修改督办
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveSupervise(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String superviseId = request.getParameter("superviseId");
		String supervisorNames = request.getParameter("supervisorNames");
		String superviseDate = request.getParameter("superviseDate");
		// String canModify = request.getParameter("canModify");
		String title = request.getParameter("title");
		String summaryId = request.getParameter("summaryId");
		ColSummary summary = this.colManager.getColSummaryById(Long
				.parseLong(summaryId), false);
		User user = CurrentUser.get();
		if("true".equals(request.getParameter("isDelete"))){
            colSuperviseManager.deleteSuperviseById(Long.parseLong(superviseId));
        }
        else{
            String supervisorMemberId = request.getParameter("supervisorMemberId");
            String[] ids = supervisorMemberId.split(",");
            long[] supervisorIds = null;
            if (ids != null) {
                supervisorIds = new long[ids.length];
                int i = 0;
                for (String id : ids) {
                    supervisorIds[i] = Long.parseLong(id);
                    i++;
                }
            }
    		if (superviseId != null && !"".equals(superviseId)){
    			this.colSuperviseManager.update(summary.getImportantLevel(),
    					summary.getSubject(), title, user.getId(), user.getName(),
    					supervisorNames, supervisorIds, Datetimes.parse(
    							superviseDate, Datetimes.dateStyle),
    					Constant.superviseType.summary.ordinal(), Long
    							.parseLong(summaryId),
    					Constant.superviseState.supervising.ordinal(), true, summary.getForwardMember());
            }
            else{
    			this.colSuperviseManager.save(summary.getImportantLevel(), summary
    					.getSubject(), title, user.getId(), user.getName(),
    					supervisorNames, supervisorIds, Datetimes.parse(
    							superviseDate, Datetimes.dateStyle),
    					Constant.superviseType.summary.ordinal(), Long
    							.parseLong(summaryId),
    					Constant.superviseState.supervising.ordinal(), true, summary.getForwardMember());
            }
        }
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("  top.window.close();");
		out.println("</script>");
		return null;
	}

	/**
	 * 查办理情况框架
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showAffairEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView(
				"collaboration/supervise/showAffairEntry");
		return mv;
	}

	/**
	 * @return the edocSummaryManager
	 */
	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	/**
	 * @param edocSummaryManager
	 *            the edocSummaryManager to set
	 */
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	/**
	 * 督办更多事项列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @modify by lilong 2012-02-08 增加栏目编辑设置点击更多的过滤条件
	 */
	public ModelAndView pendingMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("collaboration/supervise/pendingMore");
		User user = CurrentUser.get();
		Long userId = user.getId();
		int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
		String type = request.getParameter("type");
		if(Strings.isBlank(type)){
			type = "all";
		}
		//栏目编辑后传递过来的过滤条件获取
		String fragmentId = request.getParameter("fragmentId");
		Map<String,String> preference = null;
		if(StringUtils.isNotBlank(fragmentId)) {
			String ordinal = request.getParameter("ordinal");
			preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
			type = "panel";
			String panel = SectionUtils.getPanel("all", preference);
	    	String tempStr = preference.get(panel+"_value");
			if(StringUtils.isBlank(tempStr)) {
				type = "all";
			}
		}
		
		boolean col = true, edoc = true;
		Integer[] allType = new Integer[2];
		allType[0] = Constant.superviseType.summary.ordinal();
		allType[1] = Constant.superviseType.edoc.ordinal();
		
		List<ColSuperviseModel> list = new ArrayList<ColSuperviseModel>();
		String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
		if ("all".equals(type)) {
			list = colSuperviseManager.getMySupervise(userId, status, condition, textfield, textfield1, allType);
		} else if ("coll".equals(type)) {
			list = colSuperviseManager.getMySupervise(userId, status, condition, textfield, textfield1, Constant.superviseType.summary.ordinal());
		} else if ("edoc".equals(type)) {
			list = colSuperviseManager.getMySupervise(userId, status, condition, textfield, textfield1, Constant.superviseType.edoc.ordinal());
		} else if("panel".equals(type)){
			//栏目编辑过滤条件
			String panel = SectionUtils.getPanel("all", preference);
	    	String tempStr = preference.get(panel+"_value");
	    	List<Integer> entityType = new ArrayList<Integer>();

			Map<String,List<String>> m = new HashMap<String,List<String>>();
			List<String> l = new ArrayList<String>();
			l.add(textfield);
			l.add(textfield1);
			
			//更多页面查询条件
			//QueryCondtion qc = null;
			if(Strings.isNotBlank(condition)){
				m.put(condition, l);
			}
			
			//首页PORTLET查询条件
			String portletQc = null;
			if("importLevel".equals(panel)) portletQc = "importantLevel";
			if("track_catagory".equals(panel)) portletQc = "category";
			if(portletQc != null){
				l = new ArrayList<String>();
				l.add(tempStr);
				m.put(portletQc, l);
			}
			
			list = (List<ColSuperviseModel>)colSuperviseManager.getSuperviseModelList(userId, status, m, entityType, -1,false);
			
		} else {
			list = colSuperviseManager.getMyAllSuperviseForMorePending(userId, status);
		}

		//督办事项-更多-全部、协同、公文三个页签上的计数
		int countAll = 0;
		if (col) {
			int countColl = colSuperviseManager.getMySuperviseTotalCount(userId, status, Constant.superviseType.summary.ordinal());
			modelAndView.addObject("countColl", countColl);
			countAll = countColl;
		}
		if (edoc) {
			int countEdoc = colSuperviseManager.getMySuperviseTotalCount(userId, status, Constant.superviseType.edoc.ordinal());
			modelAndView.addObject("countEdoc", countEdoc);
			countAll += countEdoc;
		}
		modelAndView.addObject("countAll", countAll);

		modelAndView.addObject("col", col);
		modelAndView.addObject("edoc", edoc);
		modelAndView.addObject("list", list);
		
		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
		Metadata edocUrgentLevelMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_urgent_level);
        modelAndView.addObject("edocUrgentLevelMetadata", edocUrgentLevelMetadata);

		return modelAndView;
	}
	
}