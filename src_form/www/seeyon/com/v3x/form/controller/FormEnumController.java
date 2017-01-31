package www.seeyon.com.v3x.form.controller;

 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.formenum.FormEnumImpl;
import www.seeyon.com.v3x.form.base.formenum.FormEnumManagerHashMap;
import www.seeyon.com.v3x.form.base.formenum.FormEnumManagerImpl;
import www.seeyon.com.v3x.form.base.formenum.FormEnumManager_Application;
import www.seeyon.com.v3x.form.base.formenum.inf.IFormEnum;
import www.seeyon.com.v3x.form.base.formenum.inf.IFormEnumManager;
import www.seeyon.com.v3x.form.base.formenum.inf.IFormEnumManager.TFormEnumType;
import www.seeyon.com.v3x.form.controller.pageobject.EnumParent;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;

import www.seeyon.com.v3x.form.domain.FormTableValueSign;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;

public class FormEnumController extends BaseController {
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private MetadataManager metadataManager;
	
	public FormDaoManager getFormDaoManager() {
		return formDaoManager;
	}

	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
	
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
    
    /**
     * 系统枚举设置首页，
     * 需要将原FrameSet页面嵌套在页签中，所以增加这个方法
     * (added by Mazc 2007-12-10)
     */
    public ModelAndView showEnumIndex(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
            return new ModelAndView("form/formenum/enumIndex");
    }
    
	/**
	 * 显示页面左侧的树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showtree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
	    SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
	    if(sessionobject == null){
	    	sessionobject  = new SessionObject();
	    	session.setAttribute("SessionObject", sessionobject);
	    	
	    }
	    String enumtype = request.getParameter("enumtype");
		String enumid = request.getParameter("enumid");
		ModelAndView mav = new ModelAndView(
				"form/formenum/lefttree");
		List enumlist = new ArrayList();
		enumlist = assignEnum(enumlist,sessionobject);
		mav.addObject("deptlist",enumlist);
		mav.addObject("enumtype",enumtype);
		mav.addObject("enumid",enumid);
		return mav;
	}
	public ModelAndView showframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		    HttpSession session = request.getSession();
		    SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		    
			ModelAndView result = new ModelAndView("form/formenum/treeIndex");
			return result;	
	}

	public ModelAndView showmenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			ModelAndView result = new ModelAndView("form/formenum/treeMenu");
			return result;
	}

	public List assignEnum(List enumlist,SessionObject sessionobject) throws DataDefineException {
		// TODO Auto-generated method stub
		sessionobject.getEnumvaluemap().clear();
		sessionobject.getEnumlistmap().clear();
		List newenumlist = new ArrayList();
		newenumlist = getFormDaoManager().quefEnumlistByAll();
		Long systemid = Long.valueOf(UUIDLong.longUUID());
		Long appid = Long.valueOf(UUIDLong.longUUID());
		Long formid = Long.valueOf(UUIDLong.longUUID());
		//SeeyonForm_Runtime runtime=SeeyonForm_Runtime.getInstance();
		String systemname = Constantform.getString4CurrentUser("form.formenum.systemenum");	
		String appname = Constantform.getString4CurrentUser("form.formenum.appenum");	
		for(int i =0;i<newenumlist.size();i++){
			/*	FormEnumlist fe = (FormEnumlist)newenumlist.get(i);
			sessionobject.getEnumlistmap().put(fe.getEnumname(), fe);
			if(fe.getEnumtype().byteValue() == 0){
				EnumParent ep = new EnumParent();
				ep.setParentid(systemid);
				ep.setParentName(systemname);
				ep.setFenum(fe);
				enumlist.add(ep);
			}else if(fe.getEnumtype().byteValue() == 1){
				EnumParent ep = new EnumParent();
				ep.setParentid(appid);
				ep.setParentName(appname);
				ep.setFenum(fe);
				enumlist.add(ep);
			}*/
//			else if(fe.getEnumtype().byteValue() == 2){
//				EnumParent ep = new EnumParent();
//				ep.setParentid(formid);
//				ep.setParentName("表单枚举");
//				ep.setFenum(fe);
//				enumlist.add(ep);
//			}			
		}
//		FormEnumlist fenum = new FormEnumlist();
//		EnumParent ep = new EnumParent();
//		fenum.setEnumname(systemname);
//		fenum.setId(systemid);
//		ep.setFenum(fenum);
//		enumlist.add(ep);
//		FormEnumlist fenum1 = new FormEnumlist();
//		EnumParent ep1 = new EnumParent();
//		fenum1.setEnumname(appname);
//		fenum1.setId(appid);
//		ep1.setFenum(fenum1);
//		enumlist.add(ep1);
//		FormEnumlist fenum2 = new FormEnumlist();
//		EnumParent ep2 = new EnumParent();
//		fenum2.setEnumname("表单枚举");
//		fenum2.setId(formid);
//		ep2.setFenum(fenum2);
//		enumlist.add(ep2);
		return enumlist;
	}
	
	public synchronized ModelAndView addenum(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			ModelAndView result = new ModelAndView("form/formenum/addenum");
			 // 获得所属分类下拉列表中的数据
			String name = request.getParameter("name");
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			 FormTableValueSign ftv = getFormDaoManager().findBiggestValueSign();
			 Long sequencenum = 0L;
			 sequencenum = ftv.getSequencenum();
		     if(sequencenum ==null){
		         sequencenum = 0L;
		         sequencenum++;
		     }else{
		         sequencenum++;
		     }  
		    FormTableValueSign newftv = new FormTableValueSign();
		    newftv.setSequencenum(sequencenum);
		    getFormDaoManager().updateBiggestEnumSign(newftv);
		    result.addObject("sequencenum", sequencenum);
			result.addObject("appMeta", appMeta);
			result.addObject("appname", name);
			return result;
	}
	
	public ModelAndView editenum(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		    HttpSession session = request.getSession();
	        SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
			ModelAndView result = new ModelAndView("form/formenum/editenum");
			String enumname = request.getParameter("name");
			//FormEnumlist formelist = (FormEnumlist)sessionobject.getEnumlistmap().get(enumname);
//			List<FormEnumlist> enumlist = new ArrayList<FormEnumlist>();
//			enumlist = getFormDaoManager().quefEnumlistByname(enumname);
//			FormEnumlist formelist = enumlist.get(0);
//			result.addObject("enumtype", formelist.getEnumtype());
//			result.addObject("enumid", formelist.getId());
//			//result.addObject("showtype", formelist.getShowtype());
//			String sortnumber = "";
//			if(formelist.getSortnumber() !=null){
//				sortnumber = formelist.getSortnumber().toString();
//			}
//			if(formelist.getSortnumber()==null){
//				sortnumber = "";
//			}
//			result.addObject("enumsortnum", sortnumber);
//			result.addObject("enumname", formelist.getEnumname());
//			result.addObject("apptype", formelist.getRefApptypeid());
//			result.addObject("ifuse", formelist.getIfuse());
//			result.addObject("enumstate", formelist.getEnumstate().toString());
			// 获得所属分类下拉列表中的数据
			Map<String, Metadata> appMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.form);
			result.addObject("appMeta", appMeta);
			return result;
	}
	/**
	 * 新增枚举
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	public synchronized ModelAndView addenummake(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		    String enumname = request.getParameter("enumname");
		    ArrayList enulist =(ArrayList) getFormDaoManager().quefEnumlistByname(enumname);
		    if(enulist.size()!=0){
				ModelAndView result = new ModelAndView("form/formenum/treeIndex");
				return result;
		    }	    	
		    String enumid  = String.valueOf(Long.valueOf(UUIDLong.longUUID()));
		    //String showtype = request.getParameter("showtype");
		    String enumtype = request.getParameter("enumtype");
		    String sortnumber = request.getParameter("sortnumber");
		    String enumstate = request.getParameter("enumstate");
		    String apptype = request.getParameter("apptype");
		    if(!enumtype.equals("1")){
		    	apptype = "0";
		    }
//			FormEnumlist fe = new FormEnumlist();
//			fe.setIfuse("N");
//			fe.setEnumname(enumname);
//			fe.setEnumtype(Byte.parseByte(enumtype));
//			fe.setShowtype(Byte.parseByte("1"));
//			if("".equals(enumstate) || "null".equals(enumstate) || enumstate == null)
//				enumstate = "1";
//			fe.setEnumstate(Byte.parseByte(enumstate));
//			if(!"".equals(sortnumber))
//			fe.setSortnumber(Integer.parseInt(sortnumber.trim()));
//			fe.setId(Long.parseLong(enumid));
//			if(enumtype.equals("1"))
//			fe.setRefApptypeid(Long.parseLong(apptype));
//			
//			FormEnumlist formenumlist = new FormEnumlist();
//			List<FormEnumvalue> formevaluelist = new ArrayList<FormEnumvalue>();
//			formenumlist = getFormDaoManager().insertfEnumlist(fe);	
            // 如果状态为启用则注册到枚举中
//			if("1".equals(enumstate))
//			   regenum(enumtype,formenumlist,formevaluelist,apptype);			
			ModelAndView result = new ModelAndView("form/formenum/treeIndex");	
			//ModelAndView result = new ModelAndView("form/formenum/listFrame");
			result.addObject("enumid", Long.parseLong(enumid));
			result.addObject("enumtype", enumtype);
			return result;
	}
	
	public ModelAndView addenummaketolist(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		 String enumtype = request.getParameter("enumtype");
		 String enumid = request.getParameter("enumid");
		ModelAndView result = new ModelAndView("form/formenum/listFrame");
		result.addObject("enumid", Long.parseLong(enumid));
		result.addObject("enumtype", enumtype);
		return result;
		
	}
	/**
	 * 注册到枚举中
	 * @param enumtype
	 * @param formenumlist
	 * @throws SeeyonFormException
	 */
	/*
	private void regenum(String enumtype,FormEnumlist formenumlist,List<FormEnumvalue> formevaluelist,String apptype) throws SeeyonFormException{
		HashMap<Long, FormEnumImpl> fIDHash=new HashMap<Long, FormEnumImpl>();
		FormEnumImpl fEnum = new FormEnumImpl();
		fEnum.readFromDomain(formenumlist);
		fIDHash.put(fEnum.getId(), fEnum);
		if(formevaluelist.size() !=0){
			for(int i=0;i<formevaluelist.size();i++){
				FormEnumvalue formevalue = formevaluelist.get(i);
				fIDHash.get(formevalue.getRefEnumid()).addValueFromDomain(formevalue);	
			}	
		}	
		FormEnumManagerHashMap formenumhash = new FormEnumManagerHashMap();
		IFormEnumManager fenummanager = null;
		if(!apptype.equals("0")){
			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(apptype));
			fenummanager.regEnum(fEnum);
		}else if(Integer.parseInt(enumtype) == formenumhash.C_sKey_Systemnum){
			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf(formenumhash.C_sKey_Systemnum));
			fenummanager.regEnum(fEnum);
		}else if(Integer.parseInt(enumtype) == formenumhash.C_sKey_Formnum){
			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etForm, Long.valueOf(formenumhash.C_sKey_Formnum));
			fenummanager.regEnum(fEnum);
		}	
	}
	*/
	private void unRegenum(String enumtype,String enumname,String apptype) throws SeeyonFormException{
		FormEnumManagerHashMap formenumhash = new FormEnumManagerHashMap();
		IFormEnumManager fenummanager = null;
//		if(!apptype.equals("0")){
//			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(apptype));
//			if(fenummanager.getEnumByName(enumname) !=null)
//			   fenummanager.unRegEnum(enumname);
//		}else if(Integer.parseInt(enumtype) == formenumhash.C_sKey_Systemnum){
//			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf(formenumhash.C_sKey_Systemnum));
//			if(fenummanager.getEnumByName(enumname) !=null)
//			  fenummanager.unRegEnum(enumname);
//		}else if(Integer.parseInt(enumtype) == formenumhash.C_sKey_Formnum){
//			fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etForm, Long.valueOf(formenumhash.C_sKey_Formnum));
//			if(fenummanager.getEnumByName(enumname) !=null)
//			   fenummanager.unRegEnum(enumname);
//		}	
	}
	
	/**
	 * 修改枚举
	 * @param request
	 * @param response
	 * @return
	 * @throws SeeyonFormException 
	 * @throws Exception
	 */
	public synchronized ModelAndView editenummake(HttpServletRequest request,
			HttpServletResponse response) throws SeeyonFormException{
		    String enumid  = request.getParameter("enumid");
		    // String showtype = request.getParameter("showtype");
		    String enumtype = request.getParameter("enumtype");
		    String sortnumber = request.getParameter("sortnumber");
		    String enumname = request.getParameter("enumname");
		    String ifuse = request.getParameter("ifuse");
		    String apptype = request.getParameter("apptype");
		    String enumstate = request.getParameter("enumstate");
		    if(!enumtype.equals("1")){
		    	apptype = "0";
		    }
		    String id = request.getParameter("id");
//			FormEnumlist fe = new FormEnumlist();
//			fe.setIfuse(ifuse);
//			fe.setEnumname(enumname);
//			fe.setEnumtype(Byte.parseByte(enumtype));
//			fe.setShowtype(Byte.parseByte("1"));
//			if("".equals(enumstate) || "null".equals(enumstate) || enumstate == null)
//				enumstate = "1";
//			fe.setEnumstate(Byte.parseByte(enumstate));
//			if(!"".equals(sortnumber))
//			fe.setSortnumber(Integer.parseInt(sortnumber.trim()));
//			fe.setId(Long.parseLong(enumid));	
//			if(enumtype.equals("1"))
//			fe.setRefApptypeid(Long.parseLong(apptype));
//	        //先注销，再注册
//			FormEnumlist formenumlist1 = new FormEnumlist();
//			String oldapptype = "0";				
//			formenumlist1 = getFormDaoManager().quefEnumlistById(Long.parseLong(enumid));
//			if(!apptype.equals("0")){	
//				oldapptype = formenumlist1.getRefApptypeid().toString();
//			}
//			enumname = formenumlist1.getEnumname();
//			//getFormDaoManager().delelctenumlistById(fe);
//			unRegenum(enumtype,enumname,oldapptype);
//			FormEnumlist formenumlist = new FormEnumlist();
//			List<FormEnumvalue> formevaluelist = new ArrayList<FormEnumvalue>();
//			formenumlist = getFormDaoManager().updatefEnumlist(fe);
//			formevaluelist = getFormDaoManager().queryByenumvalueId(Long.parseLong(enumid));
//			if(!oldapptype.equals(apptype)){
//				for(int i=0; i<formevaluelist.size();i++){
//					FormEnumvalue formvalue = formevaluelist.get(i);
//					formvalue.setRefApptypeid(Long.parseLong(apptype));
//					getFormDaoManager().updatefEnumvalue(formvalue);
//				}
//			}
//			enumname = fe.getEnumname();
//			if(fe.getRefApptypeid() !=null){
//				apptype = fe.getRefApptypeid().toString();
//			}
			//状态为启动时注册
//			if("1".equals(enumstate))
//			   regenum(enumtype,formenumlist,formevaluelist,apptype);			
			ModelAndView result = new ModelAndView("form/formenum/treeIndex");	
		    if(id.equals("1"))
		    	return result;
		    if(id.equals("2"))
		    	return null;
		    else return null;		
	}
	
	public ModelAndView showlistFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String enumid = request.getParameter("enumid");
		String enumtype = request.getParameter("enumtype");
		String ifuse = request.getParameter("ifuse");
	    ModelAndView result = new ModelAndView("form/formenum/listFrame");
	    result.addObject("enumid", enumid);
	    result.addObject("enumtype", enumtype);
	    result.addObject("ifuse", ifuse);
		return result;
	}
	/**
	 * 枚举值列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showenumvaluelist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List enumvaluelist = new ArrayList();
		String enumid = request.getParameter("enumid");
		String enumtype = request.getParameter("enumtype");
		String ifuse = request.getParameter("ifuse");
//		if("".equals(ifuse)|| "null".equals(ifuse) || ifuse == null){
//			FormEnumlist enumlist = new FormEnumlist();
//			enumlist = getFormDaoManager().quefEnumlistById( Long.parseLong(enumid));
//			ifuse = enumlist.getIfuse();
//		}	
//		enumvaluelist = (List) getFormDaoManager().queryByenumvalueId(Long.parseLong(enumid));	
	    ModelAndView result = new ModelAndView("form/formenum/enumvaluelist");
	    result.addObject("applst", pagenate(enumvaluelist));	
	    result.addObject("enumid", enumid);
	    result.addObject("enumtype", enumtype);
	    result.addObject("ifuse", ifuse);
		return result;
	}
	
	public synchronized ModelAndView addenumvalue(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    ModelAndView result = new ModelAndView("form/formenum/enumvaluemake");
	    FormTableValueSign ftv = getFormDaoManager().findBiggestValueSign();
	    Long sequencenumvalue = 0L;
	    sequencenumvalue = ftv.getSequencenumvalue();
        if(sequencenumvalue ==null ){
        	sequencenumvalue = 0L;
        	sequencenumvalue++;
        }else{
        	sequencenumvalue++;
        }  
        FormTableValueSign newftv = new FormTableValueSign();
        newftv.setSequencenumvalue(sequencenumvalue);
        getFormDaoManager().updateBiggestEnumvalueSign(newftv);
        result.addObject("sequencenumvalue", sequencenumvalue);
		return result;
	}
	/**
	 * 新增枚举值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public synchronized ModelAndView addenumvaluemake(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			Long id = Long.valueOf(UUIDLong.longUUID());
			String enumvalue = request.getParameter("enumvalue");
			String enumshowvalue = request.getParameter("enumvalueshow");
			String enumsortnumbers = request.getParameter("sortnumber");
			Integer enumsortnumber = null;
			if(!"".equals(enumsortnumbers)){
				enumsortnumber = Integer.parseInt(enumsortnumbers.trim());
			}
			Long refEnumid = Long.parseLong(request.getParameter("enumid"));
//			FormEnumlist formenumlist = new FormEnumlist();
//			formenumlist =  getFormDaoManager().quefEnumlistById(refEnumid);
//			FormEnumvalue fev = new FormEnumvalue();
//			fev.setEnumvalue(enumvalue);
//			fev.setShowValue(enumshowvalue);
//			fev.setSortnumber(enumsortnumber);
//			fev.setId(id);
//			fev.setRefEnumid(refEnumid);
//			if(formenumlist.getEnumtype() ==1)
//			fev.setRefApptypeid(formenumlist.getRefApptypeid());
//		    //注册到枚举中				
//			List<FormEnumvalue> formevaluelist = new ArrayList<FormEnumvalue>();	
//			FormEnumvalue formevalue = new FormEnumvalue();		
//			formevalue = getFormDaoManager().insertfEnumvalue(fev);
//			formevaluelist = getFormDaoManager().queryByenumvalueId(refEnumid);	
//			String enumtype = formenumlist.getEnumtype().toString();
//			String apptype = "";
//			if(formenumlist.getRefApptypeid() !=null){
//				apptype = formenumlist.getRefApptypeid().toString();
//			}else{
//				apptype="0";
//			}	
//			if("1".equals(formenumlist.getEnumstate().toString()))
//			   regenum(enumtype,formenumlist,formevaluelist,apptype);	
			ModelAndView result = new ModelAndView("form/formenum/listFrame");
//			result.addObject("enumid", refEnumid);
//			result.addObject("enumtype", enumtype);
			return result;
	}
	
	public ModelAndView editenumvalue(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    ModelAndView result = new ModelAndView("form/formenum/editenumvalue");
		String id = request.getParameter("id");
		String showValue = request.getParameter("showValue");
		String enumvalue = request.getParameter("enumvalue");
		String refEnumid = request.getParameter("refEnumid");
		String sortnumber = request.getParameter("sortnumber");
		result.addObject("id", id);
		result.addObject("showValue", showValue);
		result.addObject("enumvalue", enumvalue);
		result.addObject("refEnumid", refEnumid);
		result.addObject("sortnumber", sortnumber);
	    return result;
	}
	/**
	 * 修改枚举值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public synchronized ModelAndView editenumvaluemake(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long enumid = Long.parseLong(request.getParameter("enumid"));
		String enumvalue = request.getParameter("enumvalue");
		String enumshowvalue = request.getParameter("enumvalueshow");
		String enumsortnumbers = request.getParameter("sortnumber");
		Integer enumsortnumber = null;
		if(!"".equals(enumsortnumbers)){
			enumsortnumber = Integer.parseInt(enumsortnumbers.trim());
		}	
		Long id= Long.parseLong( request.getParameter("id"));
//		FormEnumlist formenumlist = new FormEnumlist();
//		formenumlist =  getFormDaoManager().quefEnumlistById(enumid);
//		FormEnumvalue fev = new FormEnumvalue();
//		fev.setEnumvalue(enumvalue);
//		fev.setShowValue(enumshowvalue);
//		fev.setSortnumber(enumsortnumber);
//		fev.setId(id);
//		fev.setRefEnumid(enumid);
//		if(formenumlist.getEnumtype()==1)
//		fev.setRefApptypeid(formenumlist.getRefApptypeid());
//		//getFormDaoManager().delelctenumvalueById(fev);
//		getFormDaoManager().updatefEnumvalue(fev);
//		//注册		
//		List<FormEnumvalue> formevaluelist = new ArrayList<FormEnumvalue>();		
//		formevaluelist = getFormDaoManager().queryByenumvalueId(enumid);
//		String enumtype = formenumlist.getEnumtype().toString();
//		String enumname = formenumlist.getEnumname();
//		String apptype = "";
//		if(formenumlist.getRefApptypeid() !=null){
//			apptype = formenumlist.getRefApptypeid().toString();
//		}else{
//			apptype="0";
//		}	
//		unRegenum(enumtype,enumname,apptype);
//		if("1".equals(formenumlist.getEnumstate().toString()))
//		   regenum(enumtype,formenumlist,formevaluelist,apptype);	
	    ModelAndView result = new ModelAndView("form/formenum/listFrame");
		result.addObject("enumid", enumid);
		return result;
	}
	/**
	 * 删除枚举
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delenumlist(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView result = null;
		Long enumid = Long.parseLong(request.getParameter("enumid"));
//		FormEnumlist fe = new FormEnumlist();
//		fe.setId(enumid);
	       	
		try {
//			fe = getFormDaoManager().quefEnumlistById(enumid);
//			getFormDaoManager().delelctenumlistById(fe);
			getFormDaoManager().delectByValuerefEnumid(enumid);
		} catch (DataDefineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(e.getErrCode() ==1007){
				result = new ModelAndView("form/formenum/treeIndex");
				return result;
			}
		}		
		
//		Long apptype = fe.getRefApptypeid();
//		if(apptype == null)
//			apptype =0l;
//		
//		//注销
//		try {
//			unRegenum(fe.getEnumtype().toString(),fe.getEnumname(),apptype.toString());
//		} catch (SeeyonFormException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		result= new ModelAndView("form/formenum/treeIndex");
		return result;
	}
	
	public synchronized ModelAndView judgeenumifuse(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter();
		String enumid = java.net.URLDecoder.decode(request.getParameter("enumid"), "UTF-8");
		boolean returnstr;	
//		FormEnumlist enumlist = new FormEnumlist();
//		enumlist = getFormDaoManager().quefEnumlistById( Long.parseLong(enumid));
//		if("Y".equals(enumlist.getIfuse())){
//			returnstr = false;
//	    	out.write(String.valueOf(returnstr));
//		}else{
//	    	returnstr = true;
//	    	out.write(String.valueOf(returnstr));
//	    }	
		return null;
	}
	
	
	/**
	 * 删除枚举值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delenumvalue(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		String id = request.getParameter("id");
		String enumid = request.getParameter("enumid");
		Long newenumid = null;
		String ids[] = id.split(",");
		String enumids[] = enumid.split(",");
//		for(int i = 0 ; i < ids.length; i++){
//			Long idtest = Long.parseLong(ids[i]);
//			FormEnumvalue fev = new FormEnumvalue();
//			fev.setId(idtest);
//			getFormDaoManager().delelctenumvalueById(fev);
//		}
//        //注销
//        for(int j = 0 ; j < enumids.length; j++){
//        	if(j==0){
//        		newenumid = Long.parseLong(enumids[j]);
//        	}
//        	Long enumidtest = Long.parseLong(enumids[j]);
//        	FormEnumlist formenumlist = new FormEnumlist();
//    		List<FormEnumvalue> formevaluelist = new ArrayList<FormEnumvalue>();
//    		formenumlist =  getFormDaoManager().quefEnumlistById(enumidtest);
//    		formevaluelist = getFormDaoManager().queryByenumvalueId(enumidtest);
//    		String enumtype = formenumlist.getEnumtype().toString();
//    		String enumname = formenumlist.getEnumname();
//    		String apptype = "0";
//    		if(formenumlist.getRefApptypeid()!=null)
//    		  apptype = formenumlist.getRefApptypeid().toString();
//    		unRegenum(enumtype,enumname,apptype);
//    		regenum(enumtype,formenumlist,formevaluelist,apptype);
//        }	
	    ModelAndView result = new ModelAndView("form/formenum/listFrame");
	    result.addObject("enumid", newenumid);
		return result;
	}
	
	public synchronized ModelAndView judgeenumname(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter();
		String enumname = java.net.URLDecoder.decode(request.getParameter("enumname"), "UTF-8");	 
		boolean returnstr;	
		List enumlist = new ArrayList();
		enumlist = getFormDaoManager().quefEnumlistByname(enumname);
	    if(enumlist.size() !=0){
	    	returnstr = false;
	    	out.write(String.valueOf(returnstr));
	    }else{
	    	returnstr = true;
	    	out.write(String.valueOf(returnstr));
	    }	
		return null;
	}
	
	public synchronized ModelAndView judgeenumshow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter();
		String enumvalue = java.net.URLDecoder.decode(request.getParameter("enumvalue"), "UTF-8");
		String enumid = java.net.URLDecoder.decode(request.getParameter("enumid"), "UTF-8");
		boolean returnstr;	
		List enumlist = new ArrayList();
		enumlist = getFormDaoManager().queryByenumIdName(Long.parseLong(enumid), enumvalue);
	    if(enumlist.size() !=0){
	    	returnstr = false;
	    	out.write(String.valueOf(returnstr));
	    }else{
	    	returnstr = true;
	    	out.write(String.valueOf(returnstr));
	    }	
		return null;
	}
	
	
	
	public ModelAndView showenum(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    ModelAndView result = new ModelAndView("form/formenum/enum");
		return result;
	}
	
	/**
	 * 分页
	 * @param list
	 * @return
	 */
	public  List pagenate(List list) {
		if (null == list || list.size() == 0)
			return null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
