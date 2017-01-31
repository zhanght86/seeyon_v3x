package com.seeyon.v3x.doc.controller;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.DocSpaceManager;
import com.seeyon.v3x.doc.webmodel.DocSpaceVO;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.manager.LocalMailCfg;

/**
 * 个人存储空间controller
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class DocSpaceController extends BaseController {
	private OrgManager orgManager;	
	private DocSpaceManager docSpaceManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setDocSpaceManager(DocSpaceManager docSpaceManager) {
		this.docSpaceManager = docSpaceManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("doc/spacemanager/index");
	}
	
	/**  个人空间管理菜单界面  */
	public ModelAndView spaceMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("doc/spacemanager/spaceMenu");
	}
	
	/**  个人空间管理树型结构界面  */
	public ModelAndView spaceTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("doc/spacemanager/spaceTree");
		V3xOrgAccount account = orgManager.getAccountById(CurrentUser.get().getLoginAccount());
		List<V3xOrgDepartment> deptlist = orgManager.getAllDepartments();
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (V3xOrgDepartment dept : deptlist) {
			V3xOrgDepartment parent = orgManager.getParentDepartment(dept.getId());
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			}
			resultlist.add(webdept);
		}
		mav.addObject("account", account);
		mav.addObject("deptlist", resultlist);
		return mav;
	}
	
	/** 个人空间管理列表页面  */
	public ModelAndView spaceList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("doc/spacemanager/spaceList");
		Long deptId = RequestUtils.getLongParameter(request, "deptId", 0);		
		List<DocStorageSpace> list = null;
		if (deptId != 0) {
			list = docSpaceManager.getStorageSpacesByDeptId(deptId);
		}
		else {
			list = docSpaceManager.getDocStorageSpacesByAccount(CurrentUser.get().getLoginAccount());
		}
		List<DocSpaceVO> the_list = new ArrayList<DocSpaceVO>();
		
		if(list != null) {
			for(DocStorageSpace docSpace : list){
				DocSpaceVO vo = new DocSpaceVO(docSpace,request);
				if (orgManager.getMemberById(docSpace.getUserId()) == null)
					continue;
				vo.setUserName(orgManager.getMemberById(docSpace.getUserId()).getName());
				the_list.add(vo);
			}
		}
		
		mav.addObject("docSpace", the_list);
		mav.addObject("deptId",deptId);
		return mav;
	}		
	
	/** 进入个人空间修改页面  */
	public ModelAndView getSpaceModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("doc/spacemanager/spaceModify");
		this.spaceModifyHelp(request, modelView, true);
		return modelView;
	}
	
	/**
	 * 辅助修改单个用户的存储空间信息或批量修改多个用户的存储空间信息对应的方法，消除重复代码
	 * @param single	是否修改单个用户的存储空间信息
	 */
	private void spaceModifyHelp(HttpServletRequest request, ModelAndView modelView, boolean single) {
		String dbClick = request.getParameter("dbClick");
		String spaceIds = request.getParameter("spaceId");		//要修改的空间ID
		String spaceId = null ;
		if(Strings.isNotBlank(spaceIds)) {
			if(!spaceIds.contains(",")){
				spaceId = spaceIds ;
			} else {
				String[] idArray = spaceIds.split(",");
				spaceId = idArray[0];
			}
		}
		
		DocStorageSpace docSpace = docSpaceManager.getDocSpaceById(Long.valueOf(spaceId));
		long temp = new Integer(1024*1024).longValue();
		long docSize = docSpace.getTotalSpaceSize() / temp;		//MB
		long mailSize = docSpace.getMailSpace() / temp;
		long blogSize = docSpace.getBlogSpace() / temp;
		long docUsedSize = docSpace.getUsedSpaceSize() / temp;
		long blogUsedSize = docSpace.getBlogUsedSpace() / temp;

		NumberFormat format = this.getNumberFormat4SpaceModify();
		
		DocSpaceVO vo = new DocSpaceVO(docSpace);
		if(single) {
			long mailOcuppiedSize = LocalMailCfg.getMailSpaceSize(String.valueOf(docSpace.getUserId()));
			vo.setMailUsed(Strings.formatFileSize(mailOcuppiedSize, true));	
			
			long mailUsedSize = mailOcuppiedSize/temp;
			modelView.addObject("mailUsedSize", mailUsedSize);
		}
		else {
			long mailUsedSize = docSpace.getMailUsedSpace()/temp;
			modelView.addObject("mailUsedSize", format.format(mailUsedSize));
		}
		
		modelView.addObject("docSize", docSize);
		modelView.addObject("mailSize", mailSize);
		modelView.addObject("blogSize", blogSize);
		modelView.addObject("docUsedSize", format.format(docUsedSize));
		modelView.addObject("blogUsedSize", format.format(blogUsedSize));
		modelView.addObject("dbClick", dbClick);
		modelView.addObject("spaceVo", vo);
		modelView.addObject("spaceIds",spaceIds);
	}
	
	/** 在修改存储空间信息时，获取所需的数字显示格式 */
	private NumberFormat getNumberFormat4SpaceModify() {
		Locale locale = Locale.getDefault();
		
		User user = CurrentUser.get();
		if (user != null) {
			locale = user.getLocale();
		}
		
		NumberFormat format = NumberFormat.getInstance(locale);
		format.setMaximumFractionDigits(0);				//设置最高小数位的精确度
		format.setMinimumFractionDigits(0);
		return format;
	}
	
	/** 个人空间修改 */
	public ModelAndView SpaceModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.modifySpace(request);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.parent.main.location.href=parent.parent.main.location.href;");
		out.println("parent.parent.bottom.location.href=\"/seeyon/common/detail.jsp\";");
		out.println("</script>");
		return super.refreshWorkspace();
	}	
	
	/** 进入批量修改空间页面  */
	public ModelAndView getSpacesModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("doc/spacemanager/spacesModify");
		this.spaceModifyHelp(request, modelView, false);
		return modelView;
	}
	
	/** 批量空间修改 */
	public ModelAndView SpacesModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.modifySpace(request);
		super.rendJavaScript(response, "window.returnValue = '" + RequestUtils.getLongParameter(request, "deptId", 0) + "';" +
									   "window.close();");
		return null;
	}
	
	/** 修改存储空间设置信息 */
	private void modifySpace(HttpServletRequest request) throws DocException {
		String spaceIds = request.getParameter("spaceIds");
		String[] IdArray = spaceIds.split(",");
		Long docSize = RequestUtils.getLongParameter(request, "docSize", 0);
		Long mailSize = RequestUtils.getLongParameter(request, "mailSize", 0);
		Long blogSize = -1L;
		if(request.getParameter("blogSize") != null){
			blogSize = RequestUtils.getLongParameter(request, "blogSize", 0);
		}
		for (int i = 0; i < IdArray.length; i++) {
			String spaceIdr = IdArray[i];
			Long spaceId = Long.valueOf(spaceIdr);
			docSpaceManager.modifyDocSpace(spaceId, docSize, mailSize, blogSize);
		}
	}	

}
