/**
 * 
 */
package com.seeyon.v3x.common.office;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-12
 */
public class OfficeController extends BaseController {

	private HandWriteManager handWriteManager;

	public void setHandWriteManager(HandWriteManager handWriteManager) {
		this.handWriteManager = handWriteManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DBstep.iMsgServer2000 msgObj=new DBstep.iMsgServer2000();

		handWriteManager.readVariant(request, msgObj);

		String option = msgObj.GetMsgByName("OPTION");

		if ("LOADFILE".equalsIgnoreCase(option)) {
			handWriteManager.LoadFile(msgObj);
		}
		else if("SAVEFILE".equalsIgnoreCase(option)){
			handWriteManager.saveFile(msgObj);
		}

		handWriteManager.sendPackage(response, msgObj);

		return null;
	}

}
