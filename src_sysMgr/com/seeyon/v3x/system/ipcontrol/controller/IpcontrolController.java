package com.seeyon.v3x.system.ipcontrol.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.ipcontrol.domain.V3xIpcontrol;
import com.seeyon.v3x.system.ipcontrol.manager.IpcontrolManager;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.GroupAdmin,RoleType.Administrator})
public class IpcontrolController extends BaseController {
	
	private OrgManagerDirect orgManagerDirect;
	private IpcontrolManager ipcontrolManager;

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setIpcontrolManager(IpcontrolManager ipcontrolManager) {
		this.ipcontrolManager = ipcontrolManager;
	}

	public ModelAndView index(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    return new ModelAndView("sysMgr/ipcontrol/index");
	}
	
	public ModelAndView showMenu(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("sysMgr/ipcontrol/topMenu");
        modelAndView.addObject("groupAccountId", orgManagerDirect.getRootAccount().getId());
	    return modelAndView;
	}
	
	public ModelAndView showTree(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("sysMgr/ipcontrol/tree");
        
        List<V3xOrgEntity> accountlist = orgManagerDirect.getEntityList(V3xOrgAccount.class.getSimpleName(), "isDeleted", false, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, false);
        List<WebV3xOrgAccount> resultlist = new ArrayList<WebV3xOrgAccount>();
        Long groupAccountId = null; 
        for (V3xOrgEntity accountEnt : accountlist) {
        	V3xOrgAccount account = (V3xOrgAccount)accountEnt;
            if(account.getIsRoot()){
                groupAccountId = account.getId();
            }
            WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
            webaccount.setV3xOrgAccount(account);
            Long superId = account.getSuperior();
            if (null != superId && superId != 0) {
                V3xOrgAccount superaccount = orgManagerDirect.getAccountById(superId);
                if (null != superaccount){
                    webaccount.setSuperiorName(superaccount.getShortname());
                }
            }
            resultlist.add(webaccount);
        }
        modelAndView.addObject("accountlist", resultlist);
        modelAndView.addObject("groupAccountId", groupAccountId);
		return modelAndView;
	}
	
	public ModelAndView listFrame(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
		 return new ModelAndView("sysMgr/ipcontrol/list_iframe");
	}
	
	public ModelAndView list(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
		 ModelAndView mav = new ModelAndView("sysMgr/ipcontrol/listIpcontrol");
		 String accountId = request.getParameter("id");
		 String name = request.getParameter("name");
		 String accountId2 = request.getParameter("accountId");
		 String type = request.getParameter("type");
		 List<V3xIpcontrol> ipcontrols = null;
		 
		 if("search".equals(request.getParameter("search"))){
				ipcontrols = ipcontrolManager.findIpcontrolBy(name, type, accountId, accountId2);
		 } else {
			 if(Strings.isNotBlank(accountId)){
				 ipcontrols = ipcontrolManager.findIpcontrolByAccount(Long.parseLong(accountId));
			 } else {
				 ipcontrols = ipcontrolManager.findAllIpcontrol();
			 }
		 }
		 mav.addObject("ipcontrols", pagenate(ipcontrols));
		 return mav;
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
	public ModelAndView create(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("sysMgr/ipcontrol/ipcontrol_create");
		String readonly = request.getParameter("readonly");
		String accountId = request.getParameter("accountId");
		if (Strings.isNotBlank(accountId)) {
			V3xOrgAccount account = orgManagerDirect.getAccountById(Long.parseLong(accountId));
			if (account.getIsRoot()) {
				mav.addObject("root", "true");
			} else {
				mav.addObject("root", "false");
			}
		}
		String id = request.getParameter("id");
		if(Strings.isNotBlank(id)){
			if(Strings.isNotBlank(readonly)){
				mav.addObject("readonly", readonly);
			}
			V3xIpcontrol ipcontrol = ipcontrolManager.getIpcontrol(Long.parseLong(id));
			mav.addObject("ipcontrol", ipcontrol);
		}
		return mav;
	}
	public ModelAndView save(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
		String accountId = request.getParameter("accountId");
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		String selectPeopleStr = request.getParameter("selectPeopleStr");
		String ips = request.getParameter("ips");
		String id = request.getParameter("id");
		V3xIpcontrol ipcontrol = null;
		if(Strings.isBlank(id)){
			ipcontrol = new V3xIpcontrol();
			ipcontrol.setIdIfNew();
		} else {
			ipcontrol = ipcontrolManager.getIpcontrol(Long.parseLong(id));
		}
		ipcontrol.setType(Integer.parseInt(type));
		ipcontrol.setName(name);
		ipcontrol.setUsers(selectPeopleStr);
		if(Strings.isNotBlank(ips)){
			ipcontrol.setAddress(ips);
		}
		if(Strings.isBlank(id)){
			ipcontrol.setAccountId(Long.parseLong(accountId));
			ipcontrol.setCreateTime(new Date());
			ipcontrol.setCreateUser(CurrentUser.get().getId());
			ipcontrolManager.save(ipcontrol);
		} else {
			ipcontrol.setModifyTime(new Date());
			ipcontrolManager.update(ipcontrol);
		}
		return super.redirectModelAndView("/ipcontrol.do?method=listFrame&accountId=" + accountId,"parent");
	}
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String accountId = request.getParameter("accountId");
		String[] ids = request.getParameterValues("id");
		List<Long> list = new ArrayList<Long>();
		if (ids != null) {
			for(String idStr : ids){
				list.add(Long.parseLong(idStr));
			}
		}
		ipcontrolManager.delete(list);
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"
				+ Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();
		String url = null;
		if(Strings.isNotBlank(accountId)){
			url = "/ipcontrol.do?method=listFrame&accountId=" + accountId;
		} else {
			url = "/ipcontrol.do?method=listFrame";
		}
		return super.redirectModelAndView(url,"parent");
	}
}
