package com.seeyon.v3x.system.signet.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.TextEncoder;
import com.seeyon.v3x.util.annotation.SetContentType;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class SignetController extends BaseController {

	private static final Log log = LogFactory.getLog(SignetController.class);
	
	private SignetManager signetManager;
	private OrgManagerDirect orgManagerDirect;
    private AppLogManager appLogManager;
	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }
    
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 印章管理的进入方法 主界面的首要方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView signetFrame(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView("sysMgr/signet/signetFrame");
	}

	/**
	 * 读取全部数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listSignet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/signet/listSignet");
		List<V3xSignet> signetList = null;
		List<V3xSignet> newList = new ArrayList<V3xSignet>();
		try {
			signetList = signetManager.findAllByAccountId(CurrentUser.get().getLoginAccount());
			for (V3xSignet signet : signetList) {
				signet.setMarkName(signet.getMarkName());
				newList.add(signet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.addObject("signetList", pagenate(newList));
		return result;
	}

	/**
	 * @param request
	 * @param response
	 */
	@SetContentType
	public ModelAndView signetPicture(HttpServletRequest request,
			HttpServletResponse response) {
		Long id = Long.valueOf(request.getParameter("id"));

		V3xSignet signet = signetManager.getSignet(id);
		OutputStream out = null;
		try {
			byte[] b = signet.getMarkBodyByte();
			
			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=\"file.jpg\"");

			out = response.getOutputStream();
			out.write(b);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals("ClientAbortException")) {
			}
			else{
				log.error("", e);
			}
		}
		finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}

		return null;
	}

	/**
	 * 进入印章管理的修改界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView modifySignet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/signet/signet");
		String printId=request.getParameter("id");
		Long id = Long.valueOf(printId);
		V3xSignet signet = signetManager.getSignet(id);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		String password = null;
		if(StringUtils.isNotBlank(signet.getPassword())){
			password = TextEncoder.decode(signet.getPassword());
		}
		result.addObject("password", password);
		result.addObject("signetUserId", signet.getUserName());
		result.addObject("showImg", 1);
		result.addObject("signet", signet);
		result.addObject("signetManagerMethod", "editSignet");
		return result;
	}

	/**
	 * 进入印章管理的添加界面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSignet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/signet/signet");
		result.addObject("showImg", 0);
		result.addObject("signetManagerMethod", "createSignet");
		return result;
	}

	/**
	 * 完成印章管理的添加功能
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView createSignet(HttpServletRequest request,HttpServletResponse response){
		V3xSignet signet = new V3xSignet();
		User user = CurrentUser.get();
		String imgType = null;
		int type = Integer.parseInt(request.getParameter("signetSelect"));
		try {
			// 读入文件
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator fileNames = multipartRequest.getFileNames();
			if (fileNames == null) {
				return null;
			}
			Blob bl = null;
			byte[] bClear = null;
			byte[] b = null;
			while (fileNames.hasNext()) {
				Object name = fileNames.next();
				if (name == null || "".equals(name)) {
					continue;
				}
				MultipartFile fileItem = multipartRequest.getFile(String.valueOf(name));
				if (fileItem.getSize() < 1024*1024 ){
					bClear = fileItem.getBytes();
					b = TextEncoder.encodeBytes(bClear);
					bl = Hibernate.createBlob(b);
				}else{
					PrintWriter out = response.getWriter();
					out.println("<script>");
					// out.println("alert('文件不能超过50k!')");
					out.println("alert(parent.v3x.getMessage('sysMgrLang.system_signet_option_error'));");
					out.println("</script>");
					return null;
				}
				imgType = fileItem.getOriginalFilename();
			}
			imgType = imgType.substring(imgType.length()-4, imgType.length());
			bind(request, signet);
			signet.setIdIfNew();
			signet.setUserName(request.getParameter("signetauto"));
			signet.setPassword(TextEncoder.encode(request.getParameter("password")));
			signet.setMarkBody(bl);
			signet.setMarkBodyByte(bClear);
			signet.setMarkType(type);
			signet.setImgType(imgType);
			signet.setMarkDate(new Date());
			signet.setOrgAccountId(user.getAccountId());
			signetManager.save(signet);
            appLogManager.insertLog(user, AppLogAction.Signet_New, user.getName(), signet.getMarkName());
            //安全日志
            appLogManager.insertLog(user, AppLogAction.SignetAuth_New, orgManagerDirect.getAccountById(user.getAccountId()).getName(), signet.getMarkName(),request.getParameter("signetName"));
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'))");
			out.println("parent.parent.location.href=parent.parent.location;");
			out.println("</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		//return super.refreshWorkspace();
	}
	/**
	 * 删除印章管理记录的方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView removeSignet(HttpServletRequest request,
			HttpServletResponse response) {
		try {
            User user = CurrentUser.get();
			String[] ids = request.getParameterValues("id");
            StringBuffer bf = new StringBuffer();
			if (ids != null && ids.length > 0) {
			    String[] signetNames = request.getParameterValues("signetNames");
				Long l = null;
				for (int i = 0; i < ids.length; i++) {
					l = Long.valueOf(ids[i].toString());
					signetManager.delete(l);
                    if(i > 0){
                        bf.append(",");
                    }
                    bf.append("《").append(signetNames[i]).append("》");
				}
                appLogManager.insertLog(user, AppLogAction.Signet_Delete, user.getName(), bf.toString());
			}
            return super.refreshWorkspace();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对印章管理进行修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView editSignet(HttpServletRequest request,
			HttpServletResponse response) {
		V3xSignet signet = new V3xSignet();
		try {
			signet = signetManager.getSignet(Long.parseLong(request.getParameter("id")));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String imgType = null;
		int type = Integer.parseInt(request.getParameter("signetSelect"));
			try {
				// 读入文件
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				Iterator fileNames = multipartRequest.getFileNames();
				if (fileNames == null) {
					return null;
				}
				Blob bl = null;
				byte[] bClear = null;
				byte[] b = null;
				while (fileNames.hasNext()) {
					Object name = fileNames.next();
					if (name == null || "".equals(name)) {
						continue;
					}
					MultipartFile fileItem = multipartRequest.getFile(String.valueOf(name));
					if (fileItem.getSize() < 1024*1024 ){
						bClear = fileItem.getBytes();
						b = TextEncoder.encodeBytes(bClear);
						bl = Hibernate.createBlob(b);
					}else{
						PrintWriter out = response.getWriter();
						out.println("<script>");
						// out.println("alert('文件不能超过50k!')");
						out.println("alert(parent.v3x.getMessage('sysMgrLang.system_signet_option_error'));");
						out.println("</script>");
						return null;
					}
					imgType = fileItem.getOriginalFilename();
				}
				String oldUser = signet.getUserName();
				bind(request, signet);
				signet.setIdIfNew();
				signet.setUserName(request.getParameter("signetauto"));
				signet.setPassword(TextEncoder.encode(request.getParameter("password")));
				if (!"".equals(imgType)){
					signet.setImgType(imgType.substring(imgType.length()-4, imgType.length()));
					signet.setMarkBody(bl);
					signet.setMarkBodyByte(bClear);
				}
				signet.setMarkType(type);
				//修改印章不改变印章的排列顺序，修改bug17644
				//signet.setMarkDate(new Date());
                User user = CurrentUser.get();
				signet.setOrgAccountId(user.getAccountId());
				
				signetManager.update(signet);
                appLogManager.insertLog(user, AppLogAction.Signet_Update, user.getName(), signet.getMarkName());
                if(!oldUser.equals(signet.getUserName())){
                	appLogManager.insertLog(user, AppLogAction.SignetAuthModify, orgManagerDirect.getAccountById(user.getAccountId()).getName(), signet.getMarkName(),request.getParameter("signetName"));
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		return super.refreshWorkspace();
	}

	/**
	 * 进入修改印章密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView modifyPasswordSignet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/signet/modifySignet");
		User user = CurrentUser.get();
		List<V3xSignet> signetList = signetManager.findSignetByMemberId(user.getId());
		result.addObject("signetList", signetList);
		result.addObject("signetManagerMethod", "editSignet");
		return result;
	}

	/**
	 * 进行印章密码修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView editPassword(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView result = new ModelAndView("sysMgr/signet/modifySignet");
		PrintWriter out = response.getWriter();
		Long markid = Long.valueOf(request.getParameter("markid"));
		String oldword = request.getParameter("password");
		String newword = request.getParameter("newSignetword");
		String validword = request.getParameter("validateSignetword");
		
		V3xSignet signet = signetManager.getSignet(markid);
		if(!TextEncoder.decode(signet.getPassword()).equals(oldword)){//输入的原密码不等于原来的密码
			//ignore
		}
		else { // 修改
			if (newword.equals(validword)) {
				User user = CurrentUser.get();
				signet.setPassword(TextEncoder.encode(newword));
				signetManager.update(signet);
				appLogManager.insertLog(user, AppLogAction.Update_Signet_Password, user.getName(), signet.getMarkName());
				
				List<V3xSignet> signetList = signetManager.findSignetByMemberId(user.getId());
				
				result.addObject("signetList", signetList);
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
				out.println("</script>");
				
				return result;
			}
		}
		return result;
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
	 * 根据条件查询数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listSerachSignet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/signet/listSignet");
		List<V3xSignet> signetList = null;
		List<V3xSignet> newList = new ArrayList<V3xSignet>();
		String condition = request.getParameter("condition");
        String keyWord = request.getParameter("textfield");
		try {
			signetList = signetManager.findAllByAccountId(CurrentUser.get().getLoginAccount());
			if( signetList != null ){
				if(keyWord!=null&&!keyWord.trim().equals("")){
					int intcondition=Integer.parseInt(condition.trim());
					switch (intcondition)
					{
					    case 0: 
					    	for (V3xSignet signet : signetList) {
								signet.setMarkName(signet.getMarkName());
								newList.add(signet);
							}
				            break;
					    case 1: 
							for (V3xSignet signet : signetList) {
								int nameIndex=signet.getMarkName().indexOf(keyWord.trim());
								if(nameIndex!=-1){
									signet.setMarkName(signet.getMarkName());
									newList.add(signet);
								}
							}
					        break;
					    case 2:
					    	for (V3xSignet signet : signetList) {
								if(signet.getMarkType()==Integer.parseInt(keyWord.trim())){
									signet.setMarkName(signet.getMarkName());
									newList.add(signet);
								}
							}
					        break;
					    case 3:
					    	for (V3xSignet signet : signetList) {
					    		if (Strings.isNotBlank(signet.getUserName())) {
						    		V3xOrgMember vm = orgManagerDirect.getMemberById(Long.valueOf(signet.getUserName()));
						    		int nameIndex=vm.getName().indexOf(keyWord.trim());
									if(nameIndex!=-1){
										signet.setMarkName(signet.getMarkName());
										newList.add(signet);
									}
					    		} else {
					    			continue;
					    		}
							}
					        break;
					}
				}else{
					for (V3xSignet signet : signetList) {
						signet.setMarkName(signet.getMarkName());
						newList.add(signet);
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.addObject("signetList", pagenate(newList));
		return result;
	}
}
