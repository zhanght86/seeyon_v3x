package www.seeyon.com.v3x.form.controller.menuexec;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.menuexec.menuObj;

import com.seeyon.v3x.common.web.BaseController;

public class MenuBindController extends BaseController{
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
//	菜单绑定
	public ModelAndView formMenuBind(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("form/menuexec/BindMenu");    
		return mav; 
		           
	} 
//列表数据域
	public ModelAndView listDataField(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/listDataField");    
		return mav; 
	}
//列表数据域标题
	public ModelAndView listDataFieldset(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/listDataFieldset");    
		return mav; 
	}
//	菜单组
	public ModelAndView menuGroups(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/menuGroups");    
		return mav; 
	}
//	提交按钮
	public ModelAndView submitButtons(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/submitButton");    
		return mav; 
	}
//	列表条件
	public ModelAndView listConditions(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/listCondition");    
		return mav; 
	}
//  排序
	public ModelAndView listSort(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/listSort");    
		return mav; 
	}
// 升序，降序设置
	public ModelAndView listSortSet(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/menuexec/listSortSet");    
		return mav; 
	}
	
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		menuObj menuObj = new menuObj();
		bind(request,menuObj);
		sessionobject.getMenulist().add(menuObj);		
		
		PrintWriter pw = response.getWriter();
		pw.println("<script language=\"javascript\">");
		pw.println("if(window.dialogArguments){");
		pw.println("window.dialogArguments.location.reload(window.dialogArguments.location.href);");
		pw.println("window.close();");
		pw.println("}");
		pw.println("</script>");
		return super.redirectModelAndView(null);
	}
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		int rowIndex = Integer.parseInt(request.getParameter("rowindex"));
		List<menuObj> menuList = sessionobject.getMenulist();
		for(int i = 0 ; i < menuList.size() ; i++){
		   menuObj menuobject = (menuObj)menuList.get(i);
		   if(i == rowIndex){
			   bind(request,menuobject);
		   }
		}		
		PrintWriter pw = response.getWriter();
		pw.println("<script language=\"javascript\">");
		pw.println("if(window.dialogArguments){");
		pw.println("window.dialogArguments.location.reload(window.dialogArguments.location.href);");
		pw.println("window.close();");
		pw.println("}");
		pw.println("</script>");
		return super.redirectModelAndView(null);	
	}
   public ModelAndView delete(HttpServletRequest request,HttpServletResponse reponse) throws Exception{
	   int id=Integer.parseInt(request.getParameter("id"));
		HttpSession httpSession = request.getSession();
		SessionObject sessionObj = (SessionObject)httpSession.getAttribute("SessionObject");
		sessionObj.getMenulist().remove(id);	   
	    
	   return super.redirectModelAndView("/bindForm.do?method=showTemplateBind");
   }
}
