package com.seeyon.v3x.main.shortcut;

import org.jfree.util.Log;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.DocLibManager;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-4-7
 */
public class ShortcutMenu4AccountDoc extends ShortcutMenu {

	private static final long serialVersionUID = -9187015155948843467L;
	
	private DocLibManager docLibManager;
	
	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	public String getName() {
    	long loginAccountId = CurrentUser.get().getLoginAccount();
    	
    	try {
			return this.docLibManager.getAccountDocLibName(loginAccountId);
		}
		catch (DocException e) {
			Log.error("", e);
		}
    	
        return "menu.doc.lib.account";
    }
}
