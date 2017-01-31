/**
 * 
 */
package com.seeyon.v3x.main.section.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * @author dongyj
 *
 */
public class DocSectionPanel extends BaseSectionPanel {
	private DocHierarchyManager docHierarchyManager;
	private DocLibManager docLibManager;
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.main.section.panel.BaseSectionPanel#doGetName(java.lang.String)
	 */
	@Override
	public Map<String,String> doGetName(String value) {
		Map<String,String> nameMap = new HashMap<String,String>();
        if(Strings.isNotBlank(value)){
        	List<DocResource> docList = docHierarchyManager.getDocsByIds(value);
        	if(docList != null){
        		for(DocResource res : docList){
        			String docName = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, res.getFrName());
        			DocLib lib = docLibManager.getDocLibById(res.getDocLibId());
                	User user = CurrentUser.get();
                	if(user != null && lib != null && lib.getDomainId() != 0){
                		if(lib.getDomainId() != user.getLoginAccount()){
                			String shortName = Functions.getAccountShortName(lib.getDomainId());
                			if(Strings.isNotBlank(shortName)){
                				docName = docName+"("+shortName+")";
                			}
                		}
                	}
                	nameMap.put(res.getId().toString(), docName);
        		}
        	}
        }
		return nameMap;
	}

}
