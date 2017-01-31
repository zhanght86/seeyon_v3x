package www.seeyon.com.v3x.form.controller.menuexec;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;

public class MenuExecController extends BaseController {

	
	public ModelAndView execQueryMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String appName=request.getParameter("appName");
		String queryName=request.getParameter("queryName");
		ModelAndView mav = new ModelAndView("form/menuexec/execQueryMenu");
		mav.addObject("appName", appName);
		mav.addObject("queryName", queryName);
		return mav;
	}	
	
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	
	
	public ModelAndView execFormNewMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ExecNewFormObj frunObj=new ExecNewFormObj();
		frunObj.setAppName(request.getParameter("appName"));
		frunObj.setFormName(request.getParameter("formName"));
		frunObj.setOperationName(request.getParameter("operationName"));
		frunObj.init();
		ModelAndView mav = new ModelAndView("form/menuexec/execFormNewMenu");
		mav.addObject("execobj", frunObj);
		return mav;
	}
	
	public ModelAndView execFormOperateMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ExecOperateFormObj frunObj=new ExecOperateFormObj();
		frunObj.setAppName(request.getParameter("appName"));
		frunObj.setFormName(request.getParameter("formName"));
		frunObj.setOperationName(request.getParameter("operationName"));
		frunObj.init();
		ModelAndView mav = new ModelAndView("form/menuexec/execFormOperateMenu");
		mav.addObject("execobj", frunObj);
		return mav;
		
	}

}
