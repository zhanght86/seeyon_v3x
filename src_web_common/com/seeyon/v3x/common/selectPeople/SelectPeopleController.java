/**
 * 
 */
package com.seeyon.v3x.common.selectPeople;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-4-25
 */
public class SelectPeopleController extends BaseController {

	public static final String DEFAULT_ViewPage = "SelectPeopleCommon";

	private MetadataManager metadataManager;

	private OrgManager orgManager;

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ViewPage = request.getParameter("ViewPage");

		if (StringUtils.isBlank(ViewPage)) {
			ViewPage = DEFAULT_ViewPage;
		}

		ModelAndView mv = new ModelAndView("common/SelectPeople/" + ViewPage);
		User user = CurrentUser.get();

		Metadata postTypes = metadataManager.getMetadata(MetadataNameEnum.organization_post_types);

		List<V3xOrgAccount> allAccounts = this.orgManager.getAllAccounts();
		V3xOrgAccount rootAccount = this.orgManager.getRootAccount();
		List<V3xOrgAccount> accessableAccounts = null;
		Set<Long> accessableAccountIds  = new HashSet<Long>();
		Long accessableRootAccountId = null;

		if ("true".equals(request.getParameter("showAllAccount")) || user.isSystemAdmin() || user.isAuditAdmin()) {
			accessableAccounts = allAccounts;
			accessableRootAccountId = rootAccount.getId();
		}
		else {
			List<V3xOrgAccount> _accessableAccounts = this.orgManager.accessableAccounts(user.getId());
			for (V3xOrgAccount a : _accessableAccounts) {
				accessableAccountIds.add(a.getId());
			}

			boolean isAccountInGroup = this.orgManager.isAccountInGroupTree(user.getLoginAccount());
			
			accessableAccounts = new ArrayList<V3xOrgAccount>(_accessableAccounts.size());
			
			for (V3xOrgAccount a : _accessableAccounts) {
				V3xOrgAccount _account = new V3xOrgAccount(a);
				/*
				 * 如果上级单位不在我的访问范围内:
				 * 1. 如果是在集团树下面：直接挂在集团下面（设置父是集团ID）
				 * 2. 如果是独立单位，直接把自己作为根（设置父是-1）
				*/
				if(_account.getSuperior().longValue() != -1 && !accessableAccountIds.contains(_account.getSuperior())){
					_account.setSuperior(isAccountInGroup ? rootAccount.getId() : -1);
				}
				
				accessableAccounts.add(_account);
				
				if(_account.getSuperior().longValue() == -1){
					accessableRootAccountId = _account.getId();
				}
			}
			
			if (user.getLoginAccount() != 1 && isAccountInGroup) {
				accessableAccounts.add(new V3xOrgAccount(rootAccount));
				accessableRootAccountId = rootAccount.getId();
			}
		}

		if ((Boolean) SysFlag.selectPeople_showAccounts.getFlag()) {
			List<V3xOrgLevel> groupLevels = this.orgManager.getAllLevels(rootAccount.getId());

			mv.addObject("groupLevels", groupLevels);
		}
		
		//如果我的单位不能访问集团单位，就不在单位切换中显示
		List<V3xOrgAccount> accessableAccounts4Tree = new ArrayList<V3xOrgAccount>(accessableAccounts);
		boolean isGroupAccessable = false;
		if (user.isSystemAdmin() || user.isAuditAdmin() || user.isGroupAdmin()) {
			isGroupAccessable = true;
		} else {
			isGroupAccessable = Functions.isGroupAccessable(user.getLoginAccount());
		}
		if(!isGroupAccessable){
			accessableAccounts4Tree.remove(rootAccount);
		}
		
		Long firstAccountId = null;
		for (V3xOrgAccount account : accessableAccounts) {
			if (!account.getIsRoot() && firstAccountId == null) {
				firstAccountId = account.getId();
			}
		}
		
		mv.addObject("accessableRootAccountId", accessableRootAccountId);
		mv.addObject("firstAccountId", firstAccountId);
		mv.addObject("isGroupAccessable", isGroupAccessable);
		mv.addObject("postTypes", postTypes);
		mv.addObject("allAccounts", allAccounts);
		mv.addObject("accessableAccounts", accessableAccounts);
		mv.addObject("accessableAccounts4Tree", accessableAccounts4Tree);

		return mv;
	}

}
