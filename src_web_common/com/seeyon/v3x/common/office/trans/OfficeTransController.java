/**
 * 
 */
package com.seeyon.v3x.common.office.trans;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.util.Datetimes;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-4-6
 */
public final class OfficeTransController extends BaseController {
	
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception{
		super.noCache(response);
		
		ModelAndView mv = new ModelAndView("common/officeTrans/view");
		
		Date createDate = Datetimes.parseDate(request.getParameter("createDate"));
		String filename = request.getParameter("filename");
		long fileId = Long.valueOf(request.getParameter("fileId"));
		
		String url = OfficeTransHelper.buildCacheUrl(createDate, fileId, filename, true);
		mv.addObject("url", url);
		
		return mv;
	}
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception{
		super.noCache(response);
		
		ModelAndView mv = new ModelAndView("common/officeTrans/index");
		
		return mv;
	}
	
	public ModelAndView wait(HttpServletRequest request, HttpServletResponse response) throws Exception{
		super.noCache(response);
		
		ModelAndView mv = new ModelAndView("common/officeTrans/wait");
		
		return mv;
	}

}
