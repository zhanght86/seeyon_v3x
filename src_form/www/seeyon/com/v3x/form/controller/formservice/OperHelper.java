package www.seeyon.com.v3x.form.controller.formservice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.OperatorImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.condition.inf.IDataColum;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputExtendManager;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputRelation;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.Operation;
import www.seeyon.com.v3x.form.controller.pageobject.Operation_BindEvent;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathParseException;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeField;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeParam;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DeeTask;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FieldInput;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldDataType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.ToperationType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonInputExtend;
import www.seeyon.com.v3x.form.manager.inf.IFormResoureProvider;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.util.Strings;

public class OperHelper {
	public static final String CALCULATE_PATTERN = "(\\{[^\\}]*\\})";
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private static Log log = LogFactory.getLog(OperHelper.class);
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	/**
	 * 用于复制文件
	 */
	public static void CopyFile(File in, File out) throws Exception {   
		FileInputStream fis  = new FileInputStream(in);     
		FileOutputStream fos = new FileOutputStream(out);     
		byte[] buf = new byte[1024];     
		int i = 0;     
		while((i=fis.read(buf))!=-1) {       
			fos.write(buf, 0, i);       
			}     
		fis.close();     
		fos.close();  
	} 
	
	/**
	 * 对于文件名
	 * @param str
	 * @param userid
	 * @return
	 */
	public static String addUserId(String str,Long userid){
		String name[] = str.split(".xsn");
		StringBuffer sb = new StringBuffer();
//		for(int i=0;i<name.length;i++){
//			if(i==0){
//				sb.append(name[i]);
//				sb.append(String.valueOf(userid));
//				sb.append(".");
//			}else{
//				sb.append(name[i]);
//			}
//		}
		sb.append(name[0]+String.valueOf(userid));
		sb.append(".xsn");
		return sb.toString();
	}
	/**
	 * 读入文件，返回字符串
	 * @param file
	 * @return
	 */
	private  static  String readpathfile(File file){
		BufferedReader freader = null;
		try {
			freader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder fs=new StringBuilder(500);
		String ftemp = null;
		do{
		  try {
			ftemp=freader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  if (ftemp!=null)
			      fs.append(ftemp);
		}
		while (ftemp!=null);
		return fs.toString();
	}
	
	/**
	 * 将字符串转化为ToperationType
	 * 
	 * @param aStr
	 * @return
	 * @throws SeeyonFormException
	 */
	public  static  ToperationType str2OperationType(String aStr)
			throws SeeyonFormException {

		if (IXmlNodeName.C_sVluae_add.equalsIgnoreCase(aStr))
			return ToperationType.otAdd;
		else if (IXmlNodeName.C_sVluae_update.equalsIgnoreCase(aStr))
			return ToperationType.otUpdate;
		else if (IXmlNodeName.C_sVluae_delete.equalsIgnoreCase(aStr))
			return ToperationType.otDelete;
		else if (IXmlNodeName.C_sVluae_readonly.equalsIgnoreCase(aStr))
			return ToperationType.otReadOnly;
		else

			/*throw new DataDefineException(
					DataDefineException.C_iStorageErrode_AttributeDefineError,
					"Operation 的 type 属性定义不正确!");*/
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_AttributeDefineError,
					Constantform.getString4CurrentUser("form.base.opertypeiswrong.label"));

	}
	
	/**
	 * 将字符串转化为ToperationType
	 * 
	 * @param aStr
	 * @return
	 * @throws SeeyonFormException
	 */
	public  static   String OperationType2str(ToperationType aStr)	throws SeeyonFormException {

		if (ToperationType.otAdd.equals(aStr))
			return IXmlNodeName.C_sVluae_add;
		else if (ToperationType.otUpdate.equals(aStr))
			return IXmlNodeName.C_sVluae_update;
		else if (ToperationType.otDelete.equals(aStr))
			return IXmlNodeName.C_sVluae_delete;
		else if (ToperationType.otReadOnly.equals(aStr))
			return IXmlNodeName.C_sVluae_readonly;
		else

			/*throw new SeeyonFormException(
					DataDefineException.C_iStorageErrode_AttributeDefineError,
					"Field 的 type 属性定义不正确!");*/
			throw new SeeyonFormException(
					DataDefineException.C_iStorageErrode_AttributeDefineError,
					Constantform.getString4CurrentUser("form.base.fieldtypeiswrong.label"));

	}
	/**
	 * 组织前台数据
	 */
	public static  List getFixOperlst(List tablefieldlst,String opertype){
		/*List operlst = new ArrayList();
		int handwritesign=0;
		for(int i=0;i<tablefieldlst.size();i++){
			TableFieldDisplay td = (TableFieldDisplay)tablefieldlst.get(i);
			String fieldtype = td.getFieldtype();			
			if("add".equals(opertype)){
				Map map = new HashMap();
				if(IPagePublicParam.HANDWRITE.equals(td.getFieldtype()))
					handwritesign++;
                if(IPagePublicParam.HANDWRITE.equals(td.getFieldtype()) && handwritesign > 1){
                	map.put("bindname"+i,td.getBindname());
    				map.put("formoper"+i,"browse");
    				map.put("formprint"+i,"Y");
    				map.put("formtransmit"+i,"Y");
    				
    				td.setFormoper("browse");
    				td.setFormprint("Y");
    				td.setFormtransmit("Y");
                }else{
                	map.put("bindname"+i,td.getBindname());
    				map.put("formoper"+i,"edit");
    				map.put("formprint"+i,"Y");
    				map.put("formtransmit"+i,"Y");
    				
    				td.setFormoper("edit");
    				td.setFormprint("Y");
    				td.setFormtransmit("Y");
                }			
				operlst.add(map);
			}else if("update".equals(opertype)){
				Map map = new HashMap();
				map.put("bindname"+i,td.getBindname());
				map.put("formoper"+i,"browse");
				map.put("formprint"+i,"N");
				map.put("formtransmit"+i,"N");
				operlst.add(map);
			}else if("readonly".equals(opertype)){
				Map map = new HashMap();
				map.put("bindname"+i,td.getBindname());
				map.put("formoper"+i,"browse");
				map.put("formprint"+i,"N");
				map.put("formtransmit"+i,"N");
				operlst.add(map);
			}
		}
		return operlst;*/
		List operlst = new ArrayList();
		String tmpOperType = "";
		for(int i=0;i<tablefieldlst.size();i++){
			tmpOperType = opertype;
			TableFieldDisplay td = (TableFieldDisplay)tablefieldlst.get(i);
			if(IPagePublicParam.EXTERNALWRITE_AHEAD.equals(td.getInputtype()) || IPagePublicParam.OUTWRITE.equals(td.getInputtype())){
				tmpOperType = "readonly";
			}
			if("add".equals(tmpOperType)){
				Map map = new HashMap();
				map.put("bindname"+i,td.getBindname());
				map.put("formoper"+i,"edit");
				map.put("formprint"+i,"Y");
				map.put("formtransmit"+i,"Y");
				
				td.setFormoper("edit");
				td.setFormprint("Y");
				td.setFormtransmit("Y");
				operlst.add(map);
			}else if("update".equals(tmpOperType)){
				Map map = new HashMap();
				map.put("bindname"+i,td.getBindname());
				map.put("formoper"+i,"browse");
				map.put("formprint"+i,"N");
				map.put("formtransmit"+i,"N");
				operlst.add(map);
			}else if("readonly".equals(tmpOperType)){
				Map map = new HashMap();
				map.put("bindname"+i,td.getBindname());
				map.put("formoper"+i,"browse");
				map.put("formprint"+i,"N");
				map.put("formtransmit"+i,"N");
				operlst.add(map);
			}
		}
		return operlst;
	}	
	/**
	 * 	 
	 * @param str
	 * @return
	 */
	public static  String parseQuotationMark(String str){
		StringBuffer sb = new StringBuffer();
		if(str != null){
			String[]string = str.split("\"");
			for(int i=0;i<string.length;i++){
				sb.append(string[i]);
				if(i!=(string.length-1)){
					sb.append(";");
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public  static String parseSpecialMark(String str){
		StringBuffer sb = new StringBuffer();
		if(str != null){
			String[]string = str.split(";");
			for(int i=0;i<string.length;i++){
				sb.append(string[i]);
				if(i!=(string.length-1)){
					sb.append("\"");
				}
			}
		}
		return sb.toString();
	}
	
	public static InfoPath_FieldInput getInfoPathFieldInput(SessionObject sessionobject ,String bindFieldName){
		if(sessionobject == null || bindFieldName == null){
			return null ;
		}
		if(sessionobject.getFieldInputList() == null){
			return null ;
		}
		for(int i = 0 ;i < sessionobject.getFieldInputList().size() ; i++){
			InfoPath_FieldInput infoPath_FieldInput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(i) ;			
			if(infoPath_FieldInput.getFName().equals(bindFieldName)){
				return infoPath_FieldInput ;
			}
		}
		return null ;
	}
	
	/**
	 * baseinfo页面 数据收集
	 * @param request
	 * @param sessionobject
	 */
	public static void baseInfoCollectData(HttpServletRequest request,SessionObject sessionobject){
		List tablefieldlst = sessionobject.getTableFieldList();
		//组织DataDefine数据
		DataDefine data = new DataDefine();
		List ftablelst = new ArrayList();
		List ffieldlst = new ArrayList();
		data.setTableLst(ftablelst);
		FormTable ftable = null;
		String tablename = null;
		data.setType("seeyonform");
		//表单名称
		String formname = (String)request.getParameter("formname");
		//<SeeyonFomDefine name="产品销售">
		if(IPagePublicParam.EDIT.equals(sessionobject.getEditflag())){
			sessionobject.setFormEditName(formname);
		}else{
			sessionobject.setFormName(formname);
		}
		//所属分类
		String formsort = (String)request.getParameter("formsort");
		//所属人
		String man = (String)request.getParameter("attachman");
		String manid = (String)request.getParameter("manId");
		
		sessionobject.setAttachManId(manid);
		sessionobject.setAttachManName(man);
		sessionobject.setFormsort(Long.valueOf(formsort));
		
		String mastertablename = null;
		//组织formtable,formfield数据
		for(int i=0;i < tablefieldlst.size();i++){
			TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
			String table = (String)request.getParameter("tablevalue" + i);			
			//第一次进来的是主表

			if(i==0){
				tablename = table;
				mastertablename = table;
				ftable = new FormTable(data);
				ftable.setFName(tablename);
				ftable.setTabletype("master");
				//ftable.setDisplay("主表");
				ftable.setDisplay(Constantform.getString4CurrentUser("form.base.mastertable.label"));
				ftablelst.add(ftable);
				ftable.setFieldLst(ffieldlst);
			}
			//以后进来的是子表
			if(!tablename.equals(table)){
				tablename = table;
				ftable = new FormTable(data);
				ftable.setFName(tablename);
				//ftable.setDisplay("重复项表");
				ftable.setDisplay(Constantform.getString4CurrentUser("form.base.repeatitemtable.label"));
				ftable.setTabletype("slave");
				ftable.setOnwertable(mastertablename);
				ftablelst.add(ftable);
				//换表要换fieldlst
				ffieldlst = new ArrayList();
				ftable.setFieldLst(ffieldlst);
			}
			//组织页面数据生成datadefine
			FormField ff = new FormField(ftable);
			String id = (String)request.getParameter("idvalue" + i);
			String display = (String)request.getParameter("displayvalue" + i);
			String field = (String)request.getParameter("fieldvalue" + i);
			String fieldtype = (String)request.getParameter("fieldtype" + i);
			String length = (String)request.getParameter("length" + i);
			String digits = (String)request.getParameter("digits" + i);
			
			if(Strings.isNotBlank(length)){
				Integer len = Integer.parseInt(length);
				length = String.valueOf(len);
			}
			
			if(Strings.isNotBlank(digits)){
				Integer dig = Integer.parseInt(digits);
				digits = String.valueOf(dig);
			}
			
			String checkvalue  = null;
			if(request.getParameter("nulltype" + i) == null){
				checkvalue = "N";
			}else{
				checkvalue = (String)request.getParameter("nulltype" + i);
			}
			ff.setId(id);
			ff.setDisplay(display);
			ff.setFieldtype(fieldtype);
			tfd.setFieldtype(fieldtype);
			if(fieldtype.equals(IPagePublicParam.TIMESTAMP) && IXmlNodeName.C_sVluae_checkBox.equals(tfd.getInputtype())){
				tfd.setInputtype("text");
			}
			if(fieldtype.equals(IPagePublicParam.DATETIME) && IXmlNodeName.C_sVluae_checkBox.equals(tfd.getInputtype())){
				tfd.setInputtype("text");
			}
			if(tfd.getDigits() !=null && !"".equals(tfd.getDigits()) && !"null".equals(tfd.getDigits())){
				tfd.setDigits(digits);
			}
			
			InfoPath_FieldInput infoinput = getInfoPathFieldInput(sessionobject, tfd.getBindname()) ;
			if(infoinput != null){
				tfd.setFormatType(infoinput.getFormatType()) ;
				tfd.setUnique(infoinput.isUnique()) ;
			}
			//对“备注” 做特殊处理
			if(IPagePublicParam.LONGTEXT.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
				//如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					if(infoinput != null){
						infoinput.setFInputType(TFieldInputType.fitText);
					}
					/**
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}
					***/	
				}
//				对日期时间做处理
			}else if(IPagePublicParam.DATETIME.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
                //如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					if(infoinput != null){
						infoinput.setFInputType(TFieldInputType.fitText);
					}
					/**
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}**/	
				}
			//对日期做处理
			}else if(IPagePublicParam.TIMESTAMP.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
                //如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					if(infoinput != null)
						infoinput.setFInputType(TFieldInputType.fitText);
					/**
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}**/	
				}
            //对签章型做处理
			}else if(IPagePublicParam.HANDWRITE.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
				//如果修改表单前不是签章类型的，现在选择签章则自动把输入类型改为文本框，把计算字段，枚举，扩展控件和初始值删除。
				if(!"".equals(tfd.getInputtype()) && !"null".equals(tfd.getInputtype()) && tfd.getInputtype() !=null){
					if(!"handwrite".equals(tfd.getInputtype())){
						tfd.setInputtype("handwrite");
						
						sessionobject.getRefInputNames().remove(OperHelper.noNamespace(tfd.getName()));
						
						for(int a=0;a<sessionobject.getFieldInputList().size();a++){
							InfoPath_FieldInput allinfoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
							if(!"".equals(allinfoinput.getStageCalculateXml()) && !"null".equals(allinfoinput.getStageCalculateXml()) && allinfoinput.getStageCalculateXml() !=null){
								if(allinfoinput.getStageCalculateXml().indexOf(tfd.getBindname()) !=-1){
									allinfoinput.setStageCalculateXml(null);
									allinfoinput.setcalculate(null);
								}
							}
							if(allinfoinput.getFName().equals(tfd.getBindname())){
								allinfoinput.setFInputType(TFieldInputType.fitHandwrite);
								allinfoinput.getFEnumlist().clear();
								allinfoinput.getFFielExtendlist().clear();
								allinfoinput.setcalculate(null);
								allinfoinput.setFCacvalue(null);
								allinfoinput.setFCalulateclassname(null);
								allinfoinput.setFClassname(null);
								allinfoinput.setExtendInfo(null);
								allinfoinput.setStageRSXml(null);
								allinfoinput.setStageEnumXml(null);
							}
						}
						List formlst = sessionobject.getFormLst();
						for(int b=0;b<formlst.size();b++){
							FormPage formpage = (FormPage)formlst.get(b);
							for(int c=0;c<formpage.getOperlst().size();c++){
								Operation operation = (Operation)formpage.getOperlst().get(c);
								if(!"".equals(operation.getNewinitxml()) && !"null".equals(operation.getNewinitxml()) && operation.getNewinitxml() !=null){
		                        	String first="<OnInit>";
									String end="</OnInit>";
									String newoninitxml = first+operation.getNewinitxml() +end;                 			
		                    		try {
		                    			if(!"".equals(newoninitxml) && !"null".equals(newoninitxml) && newoninitxml !=null){
		                     				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(newoninitxml).getRootElement();
		    								List spanList = dataoldroot.selectNodes(".//Field");
		    								for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
		    									Element span = (Element)spanList.get(ispan);
		    									String initname = span.attribute("name").getValue();
		    									if(tfd.getBindname().equals(initname))
		    										dataoldroot.remove(span);
		    								}
		    								newoninitxml = dataoldroot.asXML().replaceAll("\n", "");
		                    			}	
		                    			operation.setNewinitxml(newoninitxml.replaceAll("<OnInit>", "").replaceAll("</OnInit>", ""));	
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}	
		                        }
								if(!"".equals(operation.getViewbindstr()) && !"null".equals(operation.getViewbindstr()) && operation.getViewbindstr() !=null){
		                        	String first="";
									String end="";
									String oninitxml = "";
									operation.getViewbindstr().split("</OnInit>");
		                        	int findexfir = operation.getViewbindstr().indexOf("<OnInit>");
		                        	int findexecd = operation.getViewbindstr().indexOf("</OnInit>");
		                    		if (findexfir > 0 && findexecd > 0){
		                    			first =  operation.getViewbindstr().substring(0, findexfir);	
		                    			oninitxml = operation.getViewbindstr().substring(findexfir, findexecd+9);
		                    			end = operation.getViewbindstr().substring(findexecd+9);
		                    		}                 			
		                    		try {
		                    			if(!"".equals(oninitxml) && !"null".equals(oninitxml) && oninitxml !=null){
		                     				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(oninitxml).getRootElement();
		    								List spanList = dataoldroot.selectNodes(".//Field");
		    								for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
		    									Element span = (Element)spanList.get(ispan);
		    									String initname = span.attribute("name").getValue();
		    									if(tfd.getBindname().equals(initname))
		    										dataoldroot.remove(span);
		    								}
		    								oninitxml = dataoldroot.asXML().replaceAll("\n", "");
		                    			}	
		                    			operation.setViewbindstr(first+oninitxml+end);	
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}	
		                        }
							}
						}
					}					
				}
				
			//对数字型做处理
			}else if(IPagePublicParam.DECIMAL.equals(fieldtype)){
					if(length != null && !"".equals(length) && !"null".equals(length)){
						if(digits != null && !"".equals(digits) && !"null".equals(digits)){
							ff.setFieldlength(length +","+digits);
							tfd.setDigits(digits);
						}else{
							tfd.setDigits("");
							ff.setFieldlength(length);
						}
						tfd.setLength(length);
					}else{
						tfd.setLength("");
					}//如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
					if("handwrite".equals(tfd.getInputtype())){
						tfd.setInputtype("text");
						if(infoinput != null){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
						/*****
						for(int a=0;a<sessionobject.getFieldInputList().size();a++){
							InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
							if(infoinput.getFName().equals(tfd.getBindname())){
								infoinput.setFInputType(TFieldInputType.fitText);
							}
						}	
						****/
					}
			}else{
				if(length == null && "null".equals(length)){
					ff.setFieldlength("");
					tfd.setLength("");
				}else{
					ff.setFieldlength(length);
					tfd.setLength(length);
				}
			}//如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
			if(IPagePublicParam.VARCHAR.equals(fieldtype)){
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					if(infoinput != null){
						infoinput.setFInputType(TFieldInputType.fitText);
					}
					/**
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}
					*****/	
				}
			}
			ff.setFName(field);

			//注入页面对象值
			if(checkvalue.equals("N") || "".equals(checkvalue)){
				ff.setIs_null(true);
				tfd.setIsnull("N");
			}
			else{
				ff.setIs_null(false);
				tfd.setIsnull("Y");
			}
			ffieldlst.add(ff);
		}
		//如果表只有一个，tabletype、display为空,不分主子表
		if(ftablelst.size() == 1){
			FormTable fmaintable = (FormTable)ftablelst.get(0);
			fmaintable.setTabletype("");
			fmaintable.setDisplay("");
		}
		//组装formlst中的viewlst中的sheetname
		List formlst = sessionobject.getFormLst();
		for(int i=0;i<formlst.size();i++){
			FormPage fp = (FormPage)formlst.get(i);
			//注入 form id号
			if(fp.getFormPageId() == null || "".equals(fp.getFormPageId()) || "null".equals(fp.getFormPageId())){
				fp.setFormPageId(String.valueOf(UUIDLong.longUUID()));
			}
			//注入值：<Form name="软件产品定货单" type="seeyonform">
			fp.setName((String)request.getParameter("sheetname" + i));
		}
		
		for(int i = 0;i<data.getTableLst().size();i++){
			FormTable fotab = (FormTable)data.getTableLst().get(i);
			if(i !=0){
				fotab.setOnwerfield(mastertablename+"Id");
				data.initSlaveTableList();
			}	   
		}
		sessionobject.setData(data);
	}

	/**
	 * 保存对表单的操作
	 * 涉及到的逻辑：操作新增、修改、删除
	 * @param request
	 * @param response
	 * @param fp
	 * @throws Exception
	 */
	public static FormPage saveOperLst(HttpServletRequest request,
			HttpServletResponse response,FormPage fp,int fpnum) throws Exception {
		List operlst = new ArrayList();
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		int size = sessionobject.getTablefieldsize();
		List tablefieldlst = sessionobject.getTableFieldList();
		String tmp ;
		for(int i=0;i<tablefieldlst.size();i++){
//			提交时增加防护，校验页面提交的数据是否有效
			tmp = request.getParameter("bindname" + i);
			if(tmp == null || "".equals(tmp) || tmp.toLowerCase().indexOf("null") != -1){
				SeeyonFormException e = new SeeyonFormException(1);
				e.setToUserMsg(Constantform.getString4CurrentUser("form.oper.errorData")+tmp);
				throw e;
			}
			TableFieldDisplay td = (TableFieldDisplay)tablefieldlst.get(i);
			Map map = new HashMap();
			map.put("bindname"+i,sessionobject.getNamespace()+(String)request.getParameter("bindname" + i));
			String formoper = (String)request.getParameter("formoper" + i);
			if(formoper == null)
				formoper = "edit";
			map.put("formoper"+i,formoper);
			map.put("formprint"+i,(String)request.getParameter("formprint" + i));
			map.put("formtransmit"+i,(String)request.getParameter("formtransmit" + i));
			map.put("initvalue"+i, (String)request.getParameter("initvalue" + i));
			map.put("displayvalue"+i, (String)request.getParameter("displayvalue" + i));
			map.put("initdisplay"+i, (String)request.getParameter("initdisplay" + i));
			
			td.setFormoper((String)request.getParameter("formoper" + i));
			td.setFormprint((String)request.getParameter("formprint" + i));
			td.setFormtransmit((String)request.getParameter("formtransmit" + i));
			operlst.add(map);
		}
		
		
		String operindex = request.getParameter("operindex");
		String[] eventBindIds = request.getParameterValues("bindEventId" + operindex);
		List<Operation_BindEvent> bindEventList = new ArrayList<Operation_BindEvent>();
		List<InfoPath_DeeTask> deeTakEventList = new ArrayList<InfoPath_DeeTask>();
		if(eventBindIds != null && eventBindIds.length > 0){
			for (int j = 0; j < eventBindIds.length; j++) {
				String eventBindId = eventBindIds[j];
				String name = request.getParameter("name" + eventBindId);
				String operationType = request.getParameter("operationType" + eventBindId);
				String eventTriger = request.getParameter("eventTriger" + eventBindId);
				String model = request.getParameter("model" + eventBindId);
				String taskType = request.getParameter("taskType" + eventBindId);
				String taskName = request.getParameter("taskName" + eventBindId);
				String taskId = request.getParameter("taskId" + eventBindId);
				if(StringUtils.isBlank(taskId)){
					taskId="";
				}
				Operation_BindEvent bindEvent = new Operation_BindEvent();
				bindEvent.setId(eventBindId);
				bindEvent.setName(name);
				bindEvent.setOperationType(operationType);
				bindEvent.setEventTriger(eventTriger);
				bindEvent.setModel(model);
				bindEvent.setTaskType(taskType);
				bindEvent.setTaskName(taskName);
				bindEvent.setTaskId(taskId);
				bindEventList.add(bindEvent);
				if(taskType.equals("dee")&&model.equals("block")){
					String taskResult = request.getParameter("taskResult" + eventBindId);
					String refFieldTable = request.getParameter("refFieldTable" + eventBindId);
					String taskParam = request.getParameter("taskParam" + eventBindId);
					String taskField = request.getParameter("taskField" + eventBindId);
					
					InfoPath_DeeTask deeTask = new InfoPath_DeeTask();
					deeTask.setId(taskId);
					deeTask.setName(taskName);
					deeTask.setRefResult(taskResult);
					deeTask.setTablename(refFieldTable);
					deeTask.setFormAppId(String.valueOf(sessionobject.getFormid()));
					List<InfoPath_DeeParam> taskParamList = new ArrayList<InfoPath_DeeParam>();
					if(taskParam!=null && taskParam.length()>0){
						String[] params = taskParam.split("[|]");
						if(params!=null&&params.length>0){
							for(int m=0;m<params.length;m++){
								String param = params[m];
								String[] paramAtt = param.split("[,]");
								InfoPath_DeeParam deeParam = new InfoPath_DeeParam();
								deeParam.setName(paramAtt[0]);
								String value = "";
								if(paramAtt.length>1){
									value = paramAtt[1];
								}
								deeParam.setValue(value);
								String description = "";
								if(paramAtt.length>2){
									description = paramAtt[2];
								}
								deeParam.setDescription(description);
								taskParamList.add(deeParam);
							}
						}
						deeTask.setTaskParamList(taskParamList);
					}
					
					List<InfoPath_DeeField> taskFieldList = new ArrayList<InfoPath_DeeField>();
					if(taskField!=null){
						String[] fields = taskField.split("[|]");
						if(fields!=null&&fields.length>0){
							for(int n=0;n<fields.length;n++){
								String field = fields[n];
								String[] fieldAtt = field.split("[,]");
								InfoPath_DeeField deeField = new InfoPath_DeeField();
								deeField.setName(fieldAtt[0]);
								deeField.setDisplay(fieldAtt[1]);
								deeField.setFieldtype(fieldAtt[2]);
								deeField.setFieldlength(fieldAtt[3]);
								deeField.setChecked(fieldAtt[4]);
								taskFieldList.add(deeField);
							}
						}
						deeTask.setTaskFieldList(taskFieldList);
					}
					deeTakEventList.add(deeTask);
				}
			}
		}
		

		Operation op = new Operation();
		op.setOperationId(String.valueOf(UUIDLong.longUUID()));
		op.setName((String)request.getParameter("opername"));
		op.setType((String)request.getParameter("opertype"));
		
		//op.setFilename("Operation_"+(String)request.getParameter("operindex")+ fpnum +"1.xml");
		
		String newviewbindstr = (String)request.getParameter("newviewbindstr");
		String newsubmitlststr = (String)request.getParameter("newsubmitlststr");
		if(newsubmitlststr == null || "".equals(newsubmitlststr) || "null".equals(newsubmitlststr)){
			op.setViewbindstr(OperHelper.parseQuotationMark((String)request.getParameter("viewbindstr"+(String)request.getParameter("operindex"))));
			op.setSubmitlststr(OperHelper.parseQuotationMark((String)request.getParameter("submitlststr"+(String)request.getParameter("operindex"))));
		}else{
			op.setViewbindstr(OperHelper.parseQuotationMark(newviewbindstr));
			op.setSubmitlststr(OperHelper.parseQuotationMark(newsubmitlststr));
		}
		
	    String newsubmitxml = (String)request.getParameter("newsubmitxml"+(String)request.getParameter("operindex"));
		String newrepeatxml = (String)request.getParameter("newrepeatxml" +(String)request.getParameter("operindex"));
		String newinitxml = (String)request.getParameter("newinitxml" +(String)request.getParameter("operindex"));
		String newhighinitxml = (String)request.getParameter("newhighinitxml" +(String)request.getParameter("operindex"));
		String newhighevenxml = (String)request.getParameter("newhighevenxml" +(String)request.getParameter("operindex"));
		if("null".equals(newsubmitxml) || newsubmitxml ==null){
			newsubmitxml = "";
		}if("null".equals(newrepeatxml) || newrepeatxml ==null){
			newrepeatxml = "";
		}if("null".equals(newinitxml) || newinitxml ==null){
			newinitxml = "";
		}if("null".equals(newhighinitxml) || newhighinitxml ==null){
			newhighinitxml = "";
		}if("null".equals(newhighevenxml) || newhighevenxml ==null){
			newhighevenxml = "";
		}
		op.setNewsubmitxml(newsubmitxml);
		op.setNewrepeatxml(newrepeatxml);
		op.setNewinitxml(newinitxml);
		op.setNewhighinitxml(newhighinitxml);
		op.setNewhighevenxml(newhighevenxml);
		
		//如果新增加的操作，没有保存到operlst中的，要从这边取值
		op.setOperlst(operlst);
		op.setBindEventList(bindEventList);
		op.setDeeTakEventList(deeTakEventList);
		//request.setAttribute("selenumattr", (String)request.getParameter("operindex"));
		String submitstr = op.getSubmitlststr();
		String bindstr = op.getViewbindstr();
		//如果进入这个保存，则editflag为true;
		if((submitstr == null || "".equals(submitstr) || "null".equals(submitstr))
			&&(bindstr == null || "".equals(bindstr) || "null".equals(bindstr))){
			op.setEditflag(false);
		}else{
			op.setEditflag(true);
		}
		List fpoper = fp.getOperlst();
		//控制如果新加一个属性	
		int flag = 0;
		//statexml 标识操作设置界面的某一权限操作是新增是不为空,修改是为空.
		String statexml = request.getParameter("statexml");
		String oldopername = request.getParameter("oldopername");
		String opername = op.getName();
		for(int i=0;i<fpoper.size();i++){
			Operation newop = (Operation)fpoper.get(i);
			if(statexml ==null || "".equals(statexml) || "null".equals(statexml)){
				opername = oldopername;
			}
			if(newop !=null){
				if(newop.getName().equals(opername)){
					if(newop.getOperationId() == null ||"".equals(newop.getOperationId())||"null".equals(newop.getOperationId())){
						newop.setOperationId(op.getOperationId());
					}
					if(newop.getFilename() == null ||"".equals(newop.getFilename())||"null".equals(newop.getFilename())){
						//newop.setFilename(op.getFilename());
						newop.setFilename("Operation_"+UUIDLong.absLongUUID() +".xml");
					}
					newop.setName(op.getName());
					newop.setOperlst(op.getOperlst());
					newop.setBindEventList(op.getBindEventList());
					newop.setDeeTakEventList(op.getDeeTakEventList());
					newop.setSubmitlststr(op.getSubmitlststr());
					newop.setType(op.getType());
					//newop.setFilename(op.getFilename());
					newop.setEditflag(op.isEditflag());
					newop.setViewbindstr(op.getViewbindstr());
					newop.setNewsubmitxml(op.getNewsubmitxml());
					newop.setNewrepeatxml(op.getNewrepeatxml());
					newop.setNewinitxml(op.getNewinitxml());
					newop.setNewhighinitxml(op.getNewhighinitxml());
					newop.setNewhighevenxml(op.getNewhighevenxml());
					flag++;
				}
			}			
		}
		if(flag == 0){
			//op.setFilename("Operation_"+(fpoper.size()+1)+ fpnum +"1.xml");
			op.setFilename("Operation_"+UUIDLong.absLongUUID() +".xml");
			fpoper.add(op);
		}
		return fp;
	}
	/**
	 * 删除对表单的操作
	 * @param request
	 * @param response
	 * @param fp
	 * @throws Exception
	 */
	public static void delOperation(HttpServletRequest request,
			FormPage fp,int fpnum) throws Exception {
		List operlst = fp.getOperlst();
		String operid = (String)request.getParameter("deltype");
		List dellist = new ArrayList();
		if(operid.indexOf("↗") > -1){
		   String[] idname = operid.split("↗");
		   for(int i = 0 ; i < idname.length; i++){
			   if(idname[i].indexOf("↖") > -1){
				   String[] idandname = idname[i].split("↖");				   
				   String num = idandname[0];
				   String operationid = idandname[1];
				   for(int j=0;j<operlst.size();j++){
						Operation oper = (Operation)operlst.get(j);
						if(oper.getOperationId().equals(operationid)){
							operlst.remove(j);
						}
					}
			    }
	           }
	         }
		
	}	

	/**
	 * inputdata页面数据收集
	 * @param request
	 * @param sessionobject
	 * @throws SeeyonFormException
	 */
	public static void inputDataCollectData(HttpServletRequest request,SessionObject sessionobject) throws SeeyonFormException{
		int fieldlstsize = sessionobject.getTablefieldsize();
		List tablefieldnamelst = sessionobject.getTableFieldList();
		List inputlst = sessionobject.getFieldInputList();
		inputlst = new ArrayList();
		HashMap  inputmap  = new HashMap();
		HashMap  inputtypemap  = new HashMap();
//		接收inputdata页面的值，存入session
		//唯一标示
		String uniquexml=(String)request.getParameter("uniquexml");
		String uniquefieldString=request.getParameter("uniquedatafield");
		List uniqueFieldList=new ArrayList();
		if( !"".equals(uniquefieldString) && uniquefieldString!=null){
			String[] uniqueArray=uniquefieldString.split(",");
			if(uniqueArray.length > 0){
				for(int i=0;i<uniqueArray.length;i++){
					uniqueFieldList.add(uniqueArray[i]);
				}
			}
			sessionobject.setUniqueFieldList(uniqueFieldList);
		}else{
			sessionobject.setUniqueFieldList(uniqueFieldList);
		}
	 /* if( !"".equals(uniquexml)){
			sessionobject.setUniqueFieldString(uniquexml);
		}else{
			sessionobject.setUniqueFieldString("");
		}*/
		String name ;
		Set<String> refInputNames = new LinkedHashSet<String>();
		Set<String> outwriteInputNames = new LinkedHashSet<String>();
		Map<String,Map<String,String>> refInputAtts = new LinkedHashMap<String, Map<String,String>>();
		for(int i=0;i<fieldlstsize;i++){
			name = request.getParameter("name"+i);
			if(name == null || "".equals(name) || name.toLowerCase().indexOf("null") != -1){
				SeeyonFormException e = new SeeyonFormException(1);
				e.setToUserMsg(Constantform.getString4CurrentUser("form.input.errorData"));
				throw e;
			}
			InfoPath_FieldInput ifip = new InfoPath_FieldInput();
			//赋值，以便inputdata显示的时候用
			TableFieldDisplay td = (TableFieldDisplay)tablefieldnamelst.get(i);
			ifip.setName(sessionobject.getNamespace() + name);
			/*ifip.setInputType(ifip.str2OperationType((String)request.getParameter("puttype"+i)));
			ifip.setStageCalculateXml((String)request.getParameter("compute"+i));
			ifip.setStageEnumXml((String)request.getParameter("enumtype"+i));
			ifip.setStageRSXml((String)request.getParameter("extend"+i));			
			*/
			String taskId = (String)request.getParameter("taskId"+i);
			String taskName = (String)request.getParameter("taskName"+i);
			String taskResult = (String)request.getParameter("taskResult"+i);
			String refTaskField = (String)request.getParameter("refTaskField"+i);
			String taskParam = (String)request.getParameter("taskParam"+i);
			String taskField = (String)request.getParameter("taskField"+i);
			String refFieldTable = (String)request.getParameter("refFieldTable"+i);

			String inputtype = (String) request
					.getParameter("selinputtypevalue"+i);
			String oldselinputtype = (String)request.getParameter("oldselinputtype"+i);
			if(!"".equals(oldselinputtype) && !"null".equals(oldselinputtype) && oldselinputtype !=null){
				if(!inputtype.equals(oldselinputtype)){
					inputmap.put(ifip.getName(), ifip.getName());
					inputtypemap.put(ifip.getName(), inputtype);
					if(!IPagePublicParam.TEXTAREA.equals(inputtype) && "add".equals(td.getFormoper())){
						td.setFormoper(IPagePublicParam.EDIT);
					}
				}
			}		
			if("".equals(inputtype) || "null".equals(inputtype) || inputtype ==null){
				if(!"".equals(oldselinputtype) && !"null".equals(oldselinputtype) && oldselinputtype !=null){
					inputtype = oldselinputtype;
				}else{
//					inputtype = "text";
				    log.error("----oldselinputtype------->"+oldselinputtype+"----name-->"+name+"----i->"+i);
					SeeyonFormException e = new SeeyonFormException(1);
					e.setToUserMsg(Constantform.getString4CurrentUser("form.input.errorData"));
					throw e;
				}				
			}
			String refInputName = request.getParameter("refInputNameh" + i);
			String refInputAtt = request.getParameter("refInputAtth" + i);
			String refParams = request.getParameter("refParams" + i);
			String refInputType = request.getParameter("refInputType" + i);
			String extendinputvalue = request.getParameter("extendinputvalue"+i);
			boolean isFinalChild = Boolean.valueOf(request.getParameter("isFinalChild" + i));
			boolean isDisplayRelated = Boolean.valueOf(request.getParameter("isDisplayRelated" + i));
			boolean isDisplayBaseForm = Boolean.valueOf(request.getParameter("isDisplayBaseForm" + i));
			String selectType = request.getParameter("selectType" + i);
			String relationConditionId = request.getParameter("relationConditionId" + i);
			if(Strings.isNotBlank(refInputName)){
				if(!refInputName.startsWith(sessionobject.getNamespace())){
					refInputName = sessionobject.getNamespace() + refInputName;
				}
				if(IXmlNodeName.C_sVluae_externalwrite_ahead.equalsIgnoreCase(inputtype)){
					outwriteInputNames.add(noNamespace(refInputName));
				} else {
					refInputNames.add(noNamespace(refInputName));
				}
			}
			if(IXmlNodeName.C_sVluae_outwrite.equalsIgnoreCase(inputtype)){
				String fieldType = request.getParameter("datatype" + i);
				if(Strings.isNotBlank(fieldType) && TFieldDataType.DECIMAL.name().equals(fieldType)){
					outwriteInputNames.add(name);
				}
			} else {
			    if(IXmlNodeName.C_sVluae_extend.equalsIgnoreCase(inputtype)){
				    IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
				    ISeeyonInputExtend extend = fextendmanager.findByName(extendinputvalue);
				    if(extend != null && extend instanceof IInputRelation && !extendinputvalue.equals(Constantform.EXTEND_SEARCH_DEE_TASK_LABEL)){
					    refInputNames.add(name);
				    }
			    }
				if((IXmlNodeName.C_sVluae_select.equalsIgnoreCase(inputtype)
						&& !isFinalChild && Integer.parseInt(request.getParameter("level" + i)) > 1)){
					refInputNames.add(name);
			}
			}
			if(Strings.isNotBlank(refInputAtt)){
				if(!refInputAtt.startsWith(sessionobject.getNamespace())){
					refInputAtt = sessionobject.getNamespace() + refInputAtt;
				}
			}
			ifip.setInputType(ifip.str2OperationType(inputtype));
			ifip.setStageCalculateXml((String)request.getParameter("compute"+i));
			ifip.setStageEnumXml((String)request.getParameter("enumtype"+i));
			ifip.setStageRSXml(extendinputvalue);
			ifip.setRefInputName(refInputName);
			ifip.setRefInputAtt(refInputAtt);
			ifip.setDisplayRelated(isDisplayRelated);
			ifip.setDisplayBaseForm(isDisplayBaseForm);
			ifip.setRefParams(refParams);
			ifip.setRefInputType(refInputType);
			if(taskId!=null&&taskId.length()>0){
				InfoPath_DeeTask deeTask = new InfoPath_DeeTask();
				deeTask.setId(taskId);
				deeTask.setName(taskName);
				deeTask.setRefInputName(refTaskField);
				ifip.setRefInputAtt(refTaskField);
				deeTask.setRefResult(taskResult);
				deeTask.setTablename(refFieldTable);
				deeTask.setFormAppId(String.valueOf(sessionobject.getFormid()));
				if(extendinputvalue.equals(Constantform.EXTEND_SEARCH_DEE_TASK_LABEL)){
					deeTask.setSelectType("search");
				}else{
					deeTask.setSelectType("select");
				}
				List<InfoPath_DeeParam> taskParamList = new ArrayList<InfoPath_DeeParam>();
				if(taskParam!=null && taskParam.length()>0){
					String[] params = taskParam.split("\\|");
					if(params!=null&&params.length>0){
						for(int m=0;m<params.length;m++){
							String param = params[m];
							String[] paramAtt = param.split(",");
							InfoPath_DeeParam deeParam = new InfoPath_DeeParam();
							deeParam.setName(paramAtt[0]);
							String value = "";
							if(paramAtt.length>1){
								value = paramAtt[1];
							}
							deeParam.setValue(value);
							String description = "";
							if(paramAtt.length>2){
								description = paramAtt[2];
							}
							deeParam.setDescription(description);
							taskParamList.add(deeParam);
						}
					}
				}
				deeTask.setTaskParamList(taskParamList);
				List<InfoPath_DeeField> taskFieldList = new ArrayList<InfoPath_DeeField>();
				if(taskField!=null){
					String[] fields = taskField.split("\\|");
					if(fields!=null&&fields.length>0){
						for(int n=0;n<fields.length;n++){
							String field = fields[n];
							String[] fieldAtt = field.split(",");
							InfoPath_DeeField deeField = new InfoPath_DeeField();
							deeField.setName(fieldAtt[0]);
							deeField.setDisplay(fieldAtt[1]);
							deeField.setFieldtype(fieldAtt[2]);
							deeField.setFieldlength(fieldAtt[3]);
							deeField.setChecked(fieldAtt[4]);
							taskFieldList.add(deeField);
						}
					}
				}
				deeTask.setTaskFieldList(taskFieldList);
				ifip.setDeeTask(deeTask);
			}
			ifip.setSelectType(selectType);
			ifip.setRelationConditionId(relationConditionId);
			String formatType = (String)request.getParameter("formattype" + i);
			String uniqueType = (String)request.getParameter("uniqueType" + i);
			if(Strings.isNotBlank(formatType))
				ifip.setFormatType(formatType) ;
			if(Strings.isNotBlank(uniqueType) && "on".equals(uniqueType)){
				ifip.setUnique(true) ;
			}else{
				ifip.setUnique(false) ;
			}
			/*td.setEnumtype(OperHelper.parseQuotationMark((String)request.getParameter("enumtype"+i)));
			//用于绑定页面显示
			td.setDivenumtype((String)request.getParameter("enumdisplay"+i));
			td.setInputtype((String)request.getParameter("puttype"+i));
			td.setExtend((String)request.getParameter("extend"+i));
			td.setCompute(OperHelper.parseQuotationMark((String)request.getParameter("compute"+i)));
			td.setFormula((String)request.getParameter("formula"+i));
			*/
			td.setEnumtype(OperHelper.parseQuotationMark((String)request.getParameter("enumtype"+i)));
			//用于绑定页面显示
			td.setDivenumtype((String)request.getParameter("bindenumvalue"+i));
			td.setDivenumname((String)request.getParameter("enumdisplayname"+i));
			td.setInputtype(inputtype);
			td.setExtend(extendinputvalue);
			td.setCompute(OperHelper.parseQuotationMark((String)request.getParameter("compute"+i)));
			td.setFormula((String)request.getParameter("computeformula"+i));
			td.setFormatType(formatType);
			td.setRefInputName(refInputName);
			td.setRefInputType(refInputType);
			td.setFinalChild(isFinalChild);
			td.setDisplayRelated(isDisplayRelated);
			td.setDisplayBaseForm(isDisplayBaseForm);
			td.setRefInputAtt(refInputAtt);
			td.setRefParams(refParams);
			td.setSelectType(selectType);
			td.setRelationConditionId(relationConditionId);
			if(Strings.isNotBlank(uniqueType) && "on".equals(uniqueType)){
				td.setUnique(true) ;
			}else{
				td.setUnique(false) ;
			}
			//如果设置一个字段的唯一标示，给赋值为唯一字段
			if(uniqueFieldList.size()>0 && uniqueFieldList.size() == 1  ){
				if(uniqueFieldList.get(0).equals(BindHelper.getColumName(ifip.getName()))){
					ifip.setUnique(true) ;
					td.setUnique(true) ;
				}
			}
			inputlst.add(ifip);
		}
 		sessionobject.setFieldInputList(inputlst);
		sessionobject.setRefInputNames(refInputNames);
		sessionobject.setOutwriteInputNames(outwriteInputNames);
		
		//设置跨表单计算的关联条件
		setRelationConditionList(request, sessionobject, RelationCondition.SOURCETYPE_QUERY);
		
		setFlowIdToSessionObject(request, sessionobject);
		
		//新增的时候默认为三项，新增、修改、只读,为每一个表单做默认操作列表
		for(int i=0 ;i<sessionobject.getFormLst().size();i++){
			FormPage formpage = (FormPage)sessionobject.getFormLst().get(i);
			if(formpage.getOperlst() == null){
				addDefaultOperLst(formpage, i,sessionobject);
				for(int j=0;j<formpage.getOperlst().size();j++){
					Operation operation = (Operation)formpage.getOperlst().get(j);
					if(operation.getOperlst() == null){
						operation.setOperlst(OperHelper.getFixOperlst(tablefieldnamelst,operation.getType()));
					}				
				}
			}else{
				//保存录入定义页面的时候如果该字段类型有变化则把该表单字段的初始值删除。
				if(inputmap.size()>0){
					for(int j=0;j<formpage.getOperlst().size();j++){
						Long operationid = UUIDLong.longUUID();
						Operation operation = (Operation)formpage.getOperlst().get(j);
						Map formmap = new HashMap();
						formmap.put("Operation", operation);
						Operation opertion = (Operation) formmap.get("Operation");
						
						if(!"".equals(operation.getNewinitxml()) && !"null".equals(operation.getNewinitxml()) && operation.getNewinitxml() !=null){
		                	String first="<OnInit>";
							String end="</OnInit>";
							String newoninitxml = first+operation.getNewinitxml() +end;                 			
		            		try {
		            			if(!"".equals(newoninitxml) && !"null".equals(newoninitxml) && newoninitxml !=null){
		             				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(newoninitxml).getRootElement();
									List spanList = dataoldroot.selectNodes(".//Field");
									for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
										Element span = (Element)spanList.get(ispan);
										String onintname = span.attribute("name").getValue();
										if(inputmap.get(onintname) !=null)
											dataoldroot.remove(span);									
									}
									newoninitxml = dataoldroot.asXML().replaceAll("\n", "");
		            			}	
		            			operation.setNewinitxml(newoninitxml.replaceAll("<OnInit>", "").replaceAll("</OnInit>", ""));	
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								//log.error("表单另存为时如果有流水号，删除初始值错误", e);
							}	
		                }
						if(!"".equals(operation.getViewbindstr()) && !"null".equals(operation.getViewbindstr()) && operation.getViewbindstr() !=null){
		                	String first="";
							String end="";
							String oninitxml = "";
							operation.getViewbindstr().split("</OnInit>");
		                	int findexfir = operation.getViewbindstr().indexOf("<OnInit>");
		                	int findexecd = operation.getViewbindstr().indexOf("</OnInit>");
		            		if (findexfir > 0 && findexecd > 0){
		            			first =  operation.getViewbindstr().substring(0, findexfir);	
		            			oninitxml = operation.getViewbindstr().substring(findexfir, findexecd+9);
		            			end = operation.getViewbindstr().substring(findexecd+9);
		            		}                 			
		            		try {
		            			if(!"".equals(oninitxml) && !"null".equals(oninitxml) && oninitxml !=null){
		             				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(oninitxml).getRootElement();
									List spanList = dataoldroot.selectNodes(".//Field");
									for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
										Element span = (Element)spanList.get(ispan);
										String onintname = span.attribute("name").getValue();
										if(inputmap.get(onintname) !=null)
											dataoldroot.remove(span);	
									}
									oninitxml = dataoldroot.asXML().replaceAll("\n", "");
		            			}	
		            			operation.setViewbindstr(first+oninitxml+end);	
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								//log.error("表单另存为时如果有流水号，删除初始值错误", e);
							}	
		                }
						
						List operlst = operation.getOperlst();
						for (int k = 0; k < operlst.size(); k++) {
							Map map = (Map)operlst.get(k);
							String bindname = (String)map.get("bindname"+k);
							String formoper = (String)map.get("formoper"+k);
							if(inputmap.get(bindname) != null){
								String inputtype = (String)inputtypemap.get(bindname);
								//录入定义选择文本域，在操作权限中，选中追加，修改录入定义的文本域时，强制将追加改为编辑
								if(!IPagePublicParam.TEXTAREA.equals(inputtype) && "add".equals(formoper)){
									map.put("formoper"+k, IPagePublicParam.EDIT);
								}
								//录入定义选择外部写入/外部预写，要把操作权限改为浏览状态
								if(IPagePublicParam.EXTERNALWRITE_AHEAD.equals(inputtype) || IPagePublicParam.OUTWRITE.equals(inputtype)){
									map.put("formoper"+k, "browse");
								}
							}
						}
					}
				}
		
			}
		}
	}
	
	/**
	 * 设置跨表单计算的关联条件
	 * @param request
	 * @param sessionobject
	 */
	public static void setRelationConditionList(HttpServletRequest request, SessionObject sessionobject, int sourceType){
		Map<Long, RelationCondition> relationConditionMap = sessionobject.getRelationConditionMap();
		//保存时，先删除，再保存
		List<Long> removeRelationConditionIdList = new ArrayList<Long>();
		Collection<RelationCondition> relationConditionList = relationConditionMap.values();
		for (RelationCondition relationCondition : relationConditionList) {
			if(relationCondition.getSourceType().intValue() == sourceType){
				removeRelationConditionIdList.add(relationCondition.getId());
			}
		}
		for (Long relationConditionId : removeRelationConditionIdList) {
			relationConditionMap.remove(relationConditionId);
		}
		String[] relationConditionIdValues = request.getParameterValues("relationConditionId");
		if(relationConditionIdValues != null){
			for (int i = 0; i < relationConditionIdValues.length; i++) {
				RelationCondition relationCondition = new RelationCondition();
				String relationConditionId = relationConditionIdValues[i];
				String relationConditionName = request.getParameter("relationConditionName" + relationConditionId);
				String relationConditionFormAppId = request.getParameter("relationConditionFormAppId" + relationConditionId);
				String relationConditionFormShortName = request.getParameter("relationConditionFormShortName" + relationConditionId);
				String relationConditionSourceType = request.getParameter("relationConditionSourceType" + relationConditionId);
				relationCondition.setId(Long.parseLong(relationConditionId));
				relationCondition.setName(relationConditionName);
				relationCondition.setFormAppId(Long.parseLong(relationConditionFormAppId));
				relationCondition.setFormShortName(relationConditionFormShortName);
				relationCondition.setSourceType(Integer.parseInt(relationConditionSourceType));
				ConditionListImpl conditionList = new ConditionListImpl();
				List<ICondition> conditions = conditionList.getConditionList();
				conditions.clear();
				String[] relationConditionColumnNames = request.getParameterValues("relationConditionColumnNames" + relationConditionId);
				String[] relationConditionComparisonOperator = request.getParameterValues("relationConditionComparisonOperator" + relationConditionId);
				String[] relationConditionRefColumNames = request.getParameterValues("relationConditionRefColumNames" + relationConditionId);
				String[] relationConditionLogicOperator = request.getParameterValues("relationConditionLogicOperator" + relationConditionId);
				int len = relationConditionColumnNames.length;
				DataColumImpl dataColum = null;
				OperatorImpl operator = null;
				for (int j = 0; j < len; j++) {
					dataColum = new DataColumImpl(null);
					dataColum.setColumName(relationConditionColumnNames[j]);
					dataColum.setValueType(IDataColum.C_iValueType_field);
	                conditions.add(dataColum);
					
	            	operator = new OperatorImpl(null);
					operator.setOperator(Integer.parseInt(relationConditionComparisonOperator[j]));
	            	conditions.add(operator);
	            	
	            	dataColum = new DataColumImpl(null);
					dataColum.setColumName(relationConditionRefColumNames[j]);
					dataColum.setValueType(IDataColum.C_iValueType_field);
	                conditions.add(dataColum);
	            	
	                if(j != len - 1){
		            	operator = new OperatorImpl(null);
						operator.setOperator(Integer.parseInt(relationConditionLogicOperator[j]));
		            	conditions.add(operator);
	                }
				}
				relationCondition.setConditionList(conditionList);
				relationConditionMap.put(relationCondition.getId(), relationCondition);
			}
		}
	}
	
	private static void setFlowIdToSessionObject(HttpServletRequest request, SessionObject sessionobject){
		String[] flowIds = request.getParameterValues("flowId");
		List<Long> flowIdList = new ArrayList<Long>();
		if(flowIds != null && flowIds.length > 0){
			for (String flowId : flowIds) {
				if(Strings.isBlank(flowId))
					continue;
				flowIdList.add(Long.parseLong(flowId));
			}
		}
		sessionobject.setFlowIdListForInputData(flowIdList);
	}
	
	/**
	 * 装配operconfig页面默认的三种操作
	 * @param aStr
	 * @return
	 */
	public static void addDefaultOperLst(FormPage fp,int selenum,SessionObject sessionobject){
		List applst = new ArrayList();
		List slalist = new ArrayList();
	    String tablename=null;
	    String slave =null;
	    String namespace = null;
		for(int i=0; i<sessionobject.getTableFieldList().size();i++ ){
			TableFieldDisplay tfd = (TableFieldDisplay)sessionobject.getTableFieldList().get(i);
			String name = tfd.getTablename();
			String bindname =tfd.getBindname();		
			String fieldtype = tfd.getFieldtype();
			if(i==0){
				tablename=tfd.getTablename();
				namespace = OperHelper.Namespace(bindname);
			}else if(!name.equals(tablename)){
				tablename=tfd.getTablename();
				slave = tfd.getTablename();
				String slavename = tfd.getEditablename();
				slalist.add(namespace+slavename);
			}
		}
		if(selenum == 0){
			Operation opadd = new Operation();
			opadd.setOperationId(String.valueOf(UUIDLong.longUUID()));
			//opadd.setName("填写");
			opadd.setName(Constantform.getString4CurrentUser("form.query.write.label"));
			opadd.setType("add");
			String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"1\" /> \r\n";			
			String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
			String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"0\" /> \r\n";
			StringBuffer sqlsb = new StringBuffer();
			for(int i =0;i<slalist.size();i++){
				 String slavename = (String) slalist.get(i);
				 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"true\"  allowdelete=\"true\"/> \r\n";
				 sqlsb.append(slavestr);
			}
			String newsubmitxml = substr+drostr+rolstr;
			String newrepeatxml = sqlsb.toString();
			opadd.setNewsubmitxml(newsubmitxml);
			opadd.setNewrepeatxml(newrepeatxml);			
			opadd.setFilename("Operation_"+selenum+"01.xml");
			applst.add(opadd);
		}
		Operation opaudit = new Operation();
		//opaudit.setName("审批");
		opaudit.setName(Constantform.getString4CurrentUser("form.base.approve.label"));
		StringBuffer sqlsb = new StringBuffer();
		String substr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"2\" /> \r\n";
		String drostr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
		String rolstr = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"1\" /> \r\n";
		for(int i =0;i<slalist.size();i++){
			 String slavename = (String) slalist.get(i);
			 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n";
			 sqlsb.append(slavestr);
		}
		String newsubmitxml = substr+drostr+rolstr;
		String newrepeatxml = sqlsb.toString();
		opaudit.setNewsubmitxml(newsubmitxml);
		opaudit.setNewrepeatxml(newrepeatxml);		
		opaudit.setOperationId(String.valueOf(UUIDLong.longUUID()));
		opaudit.setType("update");
		opaudit.setFilename("Operation_"+selenum+"02.xml");
		applst.add(opaudit);
		Operation opdis = new Operation();
		//opdis.setName("显示");
		opdis.setName(Constantform.getString4CurrentUser("form.query.show.label"));
		StringBuffer sqlsb1 = new StringBuffer();
		String substr1 = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.submit.label")+"\" type=\"submit\"  state=\"2\" /> \r\n";
		String drostr1 = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.drop.label")+"\" type=\"rollback\" /> \r\n";
		String rolstr1 = "<Submit name=\""+Constantform.getString4CurrentUser("form.operhigh.rollback.label")+"\" type=\"rollback\"  state=\"1\" /> \r\n";
		for(int i =0;i<slalist.size();i++){
			 String slavename = (String) slalist.get(i);
			 String slavestr = "<SlaveTable name=\""+slavename+"\" allowadd=\"false\"  allowdelete=\"false\"/> \r\n";
			 sqlsb1.append(slavestr);
		}
		String newsubmitxml1 = substr1+drostr1+rolstr1;
		String newrepeatxml1 = sqlsb1.toString();
		opdis.setNewsubmitxml(newsubmitxml1);
		opdis.setNewrepeatxml(newrepeatxml1);
		opdis.setOperationId(String.valueOf(UUIDLong.longUUID()));
		opdis.setType("readonly");
		opdis.setFilename("Operation_"+selenum+"03.xml");
		applst.add(opdis);		
		fp.setOperlst(applst);
	}
		
	/**
	 * 从用户定义的XML信息中装载出数据库定义对象.
	 * 
	 * @param aUserDefineXML
	 * @throws SeeyonFormException
	 */
	public static Element loadDataDefine(SeeyonForm_ApplicationImpl fapp,RuntimeCharset rCharset,Element aRoot, IFormResoureProvider aProvider)
			throws SeeyonFormException {
		if (!aRoot.getName().equalsIgnoreCase(IXmlNodeName.SeeyonFomDefine))

			/*throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NodeError,
					"表单定义的XML根节点不是SeeyonFomDefine!");*/
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NodeError,
					Constantform.getString4CurrentUser("form.input.xmlrootnodeisnotseeyonformdefine.label"));
		Attribute fatt = aRoot.attribute(IXmlNodeName.name);
		if (fatt == null)

			/*
			 throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NodeAbsenceAttribute,
					"SeeyonFomDefine节点没有name属性");
			 */
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NodeAbsenceAttribute,
					Constantform.getString4CurrentUser("form.input.seeyonformdefinenoname.label"));
		fapp.setAppName(rCharset.SelfXML2JDK(fatt.getValue()));

		Element ftemp = aRoot.element(IXmlNodeName.Define);
		if (ftemp == null)
			/*
			 throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NotsendNode,
					"表单定义的Define节点没有找到!");
			 */
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NotsendNode,
					Constantform.getString4CurrentUser("form.input.notfinddefinenode.label"));
		Element datadefine = ftemp.element(IXmlNodeName.DataDefine);
		if (datadefine == null)
			/*
			 throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NotsendNode,
					"表单定义的DataDefine节点没有找到!");	
			 */
			throw new DataDefineException(
					DataDefineException.C_iStorageErrode_NotsendNode,
					Constantform.getString4CurrentUser("form.input.notfinddatadefinenode.label"));		
		return datadefine;
	}	
	
	public static String noEqualfieldnewtableName(String aName){
		int findex = aName.indexOf("↖");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);		
	}
	
	public static String noEqualName(String aName){
		int findex = aName.indexOf("/");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);		
	}
	
	
	public static String noEqualfieldoldtableName(String aName){
		int findex = aName.indexOf("↖");
		int findex1 = aName.indexOf("/");
		if (findex < 0)
			return aName;
		return aName.substring(findex+1, findex1);		
	}
	public static String noEqualfieldName(String aName){
		int findex = aName.indexOf("/");
		int findex1 = aName.indexOf("↗");
		if (findex < 0)
			return aName;
		return aName.substring(findex+1, findex1);		
	}
	
	public static String AddTableName(String aName) {
		int findex = aName.indexOf("↗");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);
	}

	public static  String AddFieldName(String aName) {
			int findex = aName.indexOf("↗");
			if (findex < 0)
				return aName;
			return aName.substring(findex+1);
		}
	public static  String AddOperationName(String aName) {
		int findex = aName.indexOf("Operation");
		if (findex < 0)
			return aName;
		return aName.substring(findex);
	}
	
	public static  String changetableName(String aName) {
		int findex = aName.indexOf(IPagePublicParam.tableson);
		if (findex < 0)
			return aName;
		return aName.substring(findex+7);
	}
	
	public static String splitFieldscale(String aName) {
		int findex = aName.indexOf(",");
		if (findex < 0)
			return aName;
		return aName.substring(findex + 1);
	}

	public static String splitFieldlength(String aName) {
		int findex = aName.indexOf(",");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);
	}
	public static String noNamespace(String aName) {
		if("".equals(aName) || "null".equals(aName) || aName ==null)
			return aName;
		int findex = aName.indexOf(":");
		if (findex < 0)
			return aName;
		return aName.substring(findex+1);
	}
	public static String Namespace(String aName) {
		int findex = aName.indexOf(":");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex+1);
	}
	/**
	 * 用于页面控件中英文转换
	 * @param str
	 * @return
	 */
	public static String typeChangeChinese(String str){
		if(str.equals("text")){
			//return "文本框";
			return Constantform.getString4CurrentUser("form.input.inputtype.text.label");
		}else if(str.equals("lable")){
			//return "标签";
			return Constantform.getString4CurrentUser("form.input.inputtype.lable.label");
		}else if(str.equals("checkbox")){
			//return "复选按钮";
			return Constantform.getString4CurrentUser("form.input.inputtype.checkbox.label");
		}else if(str.equals("textarea")){
			//return "文本域";
			return Constantform.getString4CurrentUser("form.input.inputtype.textarea.label");
		}else if(str.equals("radio")){
			//return "单选按钮";
			return Constantform.getString4CurrentUser("form.input.inputtype.radio.label");
		}else if(str.equals("select")){
			//return "下拉列表框";
			return Constantform.getString4CurrentUser("form.input.inputtype.select.label");
		}else if(str.equals("comboedit")){
			//return "复合下拉列表框";
			return Constantform.getString4CurrentUser("form.input.inputtype.comboedit.label");
		}else if(str.equals("extend")){
			//return "扩展控件";
			return Constantform.getString4CurrentUser("form.input.inputtype.extend.label");
		}else if(str.equals("handwrite")){
			//return "签章";
			return Constantform.getString4CurrentUser("form.input.inputtype.handwrite.label");
		}else{
			//return "系统未定义控件！";
			return Constantform.getString4CurrentUser("form.input.inputtype.notdefinecontrol.label");
		}
	}
	/**
	 * 去掉文件的后缀名  
	 * 如：abcd.txt ---- abcd
	 *    员工纪录.txt --员工纪录
	 * @param str
	 * @return
	 */
	public static String subPostfix(String str){
		StringBuffer sb = new StringBuffer();
		if(str != null && !"".equals(str) && !"null".equals(str)){
			int position = str.lastIndexOf(".");
			if(position != -1){
				sb.append(str.subSequence(0, position));
			}else{
				sb.append(str);
			}
		}
		return sb.toString();
	}
	/**
	 * 用来做表单状态  存储与显示的转换
	 * @param str
	 * @return
	 */
	public static String changeFormState(String str){
		if(str != null && !"".equals(str) && !"null".equals(str)){
			if("0".equals(str)){
				//return IPagePublicParam.state0;
				return Constantform.getString4CurrentUser("form.query.draft.label");
			}else if("1".equals(str)){
				//return IPagePublicParam.state1;
				return Constantform.getString4CurrentUser("form.app.beforeissuance.label");
			}else if("2".equals(str)){
				//return IPagePublicParam.state2;
				return Constantform.getString4CurrentUser("form.app.issuance.label");
			}
		}
		//return "状态未知或为空";
		return Constantform.getString4CurrentUser("form.input.typeisnotknown.label");
	}
	
	/**
	 * 用来做表单状态  存储与显示的转换
	 * @param str
	 * @return
	 */
	public static String changeFormStart(String str){
		if(str != null && !"".equals(str) && !"null".equals(str)){
			if("0".equals(str)){
				//return IPagePublicParam.state0;
				return Constantform.getString4CurrentUser("form.query.disable.label");
			}else if("1".equals(str)){
				//return IPagePublicParam.state1;
				return Constantform.getString4CurrentUser("form.query.enable.label");
			}
		}
		//return "状态未知或为空";
		return Constantform.getString4CurrentUser("form.input.typeisnotknown.label");
	}
	
	/**
	 * 用来显示弹出信息
	 * @param request
	 * @param response
	 */
	public static void creatformmessage(HttpServletRequest request,HttpServletResponse response,List<String> messagelst){
    	response.setContentType("text/html; charset=UTF-8"); 
		try {
			request.setCharacterEncoding("UTF-8");
		StringBuffer sb = new StringBuffer();
		PrintWriter out = response.getWriter();
		sb.append("<script language='JavaScript'>");
		sb.append("alert(\"");
		if(messagelst != null){
			if(messagelst.size() != 0){
				for(String message:messagelst){
					// modify by huangfj message在警告前 先做 javascript转化
					sb.append(Functions.escapeJavascript(message) + " ");
				}
			}
		}
		sb.append("\");</script>");
		out.println(sb.toString());
		out.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	public static void main(String args[]){
//		OperManager op = new OperManager();
//		String str="<ViewBind>       <BeforeSubmit  classname=\"before\" />      <AfterSubmit  classname=\"before\" /></ViewBind> <OnInit  classname=\"before\"/><SlaveTable name=\"group20010\" allowadd=\"true\"  allowdelete=\"true\"/>      <Submit name=\"提交\" type=\"submit\"  state=\"1\" />       <Submit name=\"放弃\" type=\"rollback\" />      <Submit name=\"回退\" type=\"rollback\"  state=\"0\" /> ";
//		String strr = op.parseQuotationMark(str);
//		System.out.println(strr);
//		System.out.println(op.parseSpecialMark(strr));
//	}	
//	public static void main(String args[]){
//		String str = "员工纪录.txt";
//		System.out.println(subPostfix(str));
//	}	
//	public static void main(String args[]){
//		String str = "3";
//		System.out.println(changeFormState(str));
//	}	

	
	
	/**
	 * 另存为按钮的baseinfo页面 数据收集
	 * @param request
	 * @param sessionobject
	 * @throws DataDefineException 
	 */
	public synchronized void otherbaseInfoCollectData(HttpServletRequest request,SessionObject sessionobject,Map<String,String> mapping) throws DataDefineException{
		List tablefieldlst = sessionobject.getTableFieldList();
		//组织DataDefine数据
		DataDefine data = new DataDefine();
		List ftablelst = new ArrayList();
		List ffieldlst = new ArrayList();
		data.setTableLst(ftablelst);
		FormTable ftable = null;
		String tablename = null;
		data.setType("seeyonform");
		//表单名称
		String formname = (String)request.getParameter("formname");
		//<SeeyonFomDefine name="产品销售">
		if(IPagePublicParam.EDIT.equals(sessionobject.getEditflag())){
			sessionobject.setFormEditName(formname);
		}else{
			sessionobject.setFormName(formname);
		}
		//所属分类
		String formsort = (String)request.getParameter("formsort");
		//所属人
		String man = (String)request.getParameter("attachman");
		String manid = (String)request.getParameter("manId");
		
		sessionobject.setAttachManId(manid);
		sessionobject.setAttachManName(man);
		sessionobject.setFormsort(Long.valueOf(formsort));
		
		String mastertablename = null;
		//组织formtable,formfield数据
		for(int i=0;i < tablefieldlst.size();i++){
			TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
			String table = (String)request.getParameter("tablevalue" + i);			
			//第一次进来的是主表

			if(i==0){
			    String tableNumber = getIOperBase().incrementAndGetBiggestValueSign();
				tablename = table;			
				ftable = new FormTable(data);
				ftable.setFName(IPagePublicParam.tablename + tableNumber);
				mastertablename = ftable.getName();
				mapping.put("table_"+tfd.getTablename(), mastertablename);
				ftable.setTabletype("master");
				ftable.setDisplay(Constantform.getString4CurrentUser("form.base.mastertable.label"));
				ftablelst.add(ftable);
				ftable.setFieldLst(ffieldlst);
			}
			//以后进来的是子表
			if(!tablename.equals(table)){
			    String tableNumber = getIOperBase().incrementAndGetBiggestValueSign();
				tablename = table;
				ftable = new FormTable(data);
				ftable.setFName(IPagePublicParam.tableson + tableNumber);
				mapping.put("table_"+tfd.getTablename(), ftable.getName());
				ftable.setDisplay(Constantform.getString4CurrentUser("form.base.repeatitemtable.label"));
				ftable.setTabletype("slave");
				ftable.setOnwertable(mastertablename);
				ftablelst.add(ftable);
				//换表要换fieldlst
				ffieldlst = new ArrayList();
				ftable.setFieldLst(ffieldlst);
			}
			//组织页面数据生成datadefine
			FormField ff = new FormField(ftable);
			String id = (String)request.getParameter("idvalue" + i);
			String display = (String)request.getParameter("displayvalue" + i);
			String field = (String)request.getParameter("fieldvalue" + i);
			String fieldtype = (String)request.getParameter("fieldtype" + i);
			String length = (String)request.getParameter("length" + i);
			String digits = (String)request.getParameter("digits" + i);
			String checkvalue  = null;
			if(request.getParameter("nulltype" + i) == null){
				checkvalue = "N";
			}else{
				checkvalue = (String)request.getParameter("nulltype" + i);
			}
			ff.setId(id);
			ff.setDisplay(display);
			ff.setFieldtype(fieldtype);
			tfd.setTablename(ftable.getName());
			tfd.setFieldtype(fieldtype);
			if(tfd.getDigits() !=null && !"".equals(tfd.getDigits()) && !"null".equals(tfd.getDigits())){
				tfd.setDigits(digits);
			}
//			对“备注” 做特殊处理
			if(IPagePublicParam.LONGTEXT.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
				//如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}	
				}
				
			//对日期做处理
			}else if(IPagePublicParam.TIMESTAMP.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
                //如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}	
				}
            //对日期时间型做处理
			}else if(IPagePublicParam.DATETIME.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
                //如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}	
				}
            //对签章型做处理
			}
			else if(IPagePublicParam.HANDWRITE.equals(fieldtype)){
				ff.setFieldlength("");
				tfd.setLength("");
				//如果修改表单前不是签章类型的，现在选择签章则自动把输入类型改为文本框，把计算字段，枚举，扩展控件和初始值删除。
				if(!"".equals(tfd.getInputtype()) && !"null".equals(tfd.getInputtype()) && tfd.getInputtype() !=null){
					if(!"handwrite".equals(tfd.getInputtype())){
						tfd.setInputtype("handwrite");
						for(int a=0;a<sessionobject.getFieldInputList().size();a++){
							InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
							if(!"".equals(infoinput.getStageCalculateXml()) && !"null".equals(infoinput.getStageCalculateXml()) && infoinput.getStageCalculateXml() !=null){
								if(infoinput.getStageCalculateXml().indexOf(tfd.getBindname()) !=-1){
									infoinput.setStageCalculateXml(null);
									infoinput.setcalculate(null);
								}
							}
							if(infoinput.getFName().equals(tfd.getBindname())){
								infoinput.setFInputType(TFieldInputType.fitHandwrite);
								infoinput.getFEnumlist().clear();
								infoinput.getFFielExtendlist().clear();
								infoinput.setcalculate(null);
								infoinput.setFCacvalue(null);
								infoinput.setFCalulateclassname(null);
								infoinput.setFClassname(null);
								infoinput.setExtendInfo(null);
								infoinput.setStageRSXml(null);
								infoinput.setStageEnumXml(null);
							}
						}
						List formlst = sessionobject.getFormLst();
						for(int b=0;b<formlst.size();b++){
							FormPage formpage = (FormPage)formlst.get(b);
							for(int c=0;c<formpage.getOperlst().size();c++){
								Operation operation = (Operation)formpage.getOperlst().get(c);
								if(!"".equals(operation.getNewinitxml()) && !"null".equals(operation.getNewinitxml()) && operation.getNewinitxml() !=null){
		                        	String first="<OnInit>";
									String end="</OnInit>";
									String newoninitxml = first+operation.getNewinitxml() +end;                 			
		                    		try {
		                    			if(!"".equals(newoninitxml) && !"null".equals(newoninitxml) && newoninitxml !=null){
		                     				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(newoninitxml).getRootElement();
		    								List spanList = dataoldroot.selectNodes(".//Field");
		    								for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
		    									Element span = (Element)spanList.get(ispan);
		    									String initname = span.attribute("name").getValue();
		    									if(tfd.getBindname().equals(initname))
		    										dataoldroot.remove(span);
		    								}
		    								newoninitxml = dataoldroot.asXML().replaceAll("\n", "");
		                    			}	
		                    			operation.setNewinitxml(newoninitxml.replaceAll("<OnInit>", "").replaceAll("</OnInit>", ""));	
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}	
		                        }
								if(!"".equals(operation.getViewbindstr()) && !"null".equals(operation.getViewbindstr()) && operation.getViewbindstr() !=null){
		                        	String first="";
									String end="";
									String oninitxml = "";
									operation.getViewbindstr().split("</OnInit>");
		                        	int findexfir = operation.getViewbindstr().indexOf("<OnInit>");
		                        	int findexecd = operation.getViewbindstr().indexOf("</OnInit>");
		                    		if (findexfir > 0 && findexecd > 0){
		                    			first =  operation.getViewbindstr().substring(0, findexfir);	
		                    			oninitxml = operation.getViewbindstr().substring(findexfir, findexecd+9);
		                    			end = operation.getViewbindstr().substring(findexecd+9);
		                    		}                 			
		                    		try {
		                    			if(!"".equals(oninitxml) && !"null".equals(oninitxml) && oninitxml !=null){
		                     				Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(oninitxml).getRootElement();
		    								List spanList = dataoldroot.selectNodes(".//Field");
		    								for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Field节点
		    									Element span = (Element)spanList.get(ispan);
		    									String initname = span.attribute("name").getValue();
		    									if(tfd.getBindname().equals(initname))
		    										dataoldroot.remove(span);
		    								}
		    								oninitxml = dataoldroot.asXML().replaceAll("\n", "");
		                    			}	
		                    			operation.setViewbindstr(first+oninitxml+end);	
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}	
		                        }
							}
						}
					}					
				}
				
			//对数字型做处理
			}else if(IPagePublicParam.DECIMAL.equals(fieldtype)){
					if(length != null && !"".equals(length) && !"null".equals(length)){
						if(digits != null && !"".equals(digits) && !"null".equals(digits)){
							ff.setFieldlength(length +","+digits);
							tfd.setDigits(digits);
						}else{
							tfd.setDigits("");
							ff.setFieldlength(length);
						}
						tfd.setLength(length);
					}else{
						tfd.setLength("");
					}//如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
					if("handwrite".equals(tfd.getInputtype())){
						tfd.setInputtype("text");
						for(int a=0;a<sessionobject.getFieldInputList().size();a++){
							InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
							if(infoinput.getFName().equals(tfd.getBindname())){
								infoinput.setFInputType(TFieldInputType.fitText);
							}
						}	
					}
			}else{
				if(length == null && "null".equals(length)){
					ff.setFieldlength("");
					tfd.setLength("");
				}else{
					ff.setFieldlength(length);
					tfd.setLength(length);
				}
			}
             // 如果修改前的表单是签章类型的，则自动把输入类型改称文本框。
			if(IPagePublicParam.VARCHAR.equals(fieldtype)){
				if("handwrite".equals(tfd.getInputtype())){
					tfd.setInputtype("text");
					for(int a=0;a<sessionobject.getFieldInputList().size();a++){
						InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
						if(infoinput.getFName().equals(tfd.getBindname())){
							infoinput.setFInputType(TFieldInputType.fitText);
						}
					}	
				} else if(IPagePublicParam.TEXT.equals(tfd.getInputtype())){
					String calculateXML = "";
					String formula  = tfd.getFormula();
					if(formula!=null && formula.indexOf("流水号:") != -1){
						StringBuffer newFormula = new StringBuffer();
						Pattern p = Pattern.compile(CALCULATE_PATTERN);
						Matcher m = p.matcher(formula);
						String group = null;
						while (m.find()) {
							group = m.group();
							if(group.indexOf("流水号:") == -1){
								newFormula.append(group);
							}
						}
						String fString = newFormula.toString();
						tfd.setFormula(fString);
						if(Strings.isNotBlank(fString)){
							calculateXML = parseQuotationMark("<DataColum type=\"appendValue\" name=\"" + fString + "\" />");
						}
						tfd.setCompute(calculateXML);
						
						for(int a=0;a<sessionobject.getFieldInputList().size();a++){
							InfoPath_FieldInput infoinput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(a);					
							if(infoinput.getFName().equals(tfd.getBindname())){
								infoinput.setStageCalculateXml(calculateXML);
							}
						}
					}
				}
			}
			ff.setFName(field);

			//注入页面对象值
			if(checkvalue.equals("N") || "".equals(checkvalue)){
				ff.setIs_null(true);
				tfd.setIsnull("N");
			}
			else{
				ff.setIs_null(false);
				tfd.setIsnull("Y");
			}
			ffieldlst.add(ff);
		}
		//如果表只有一个，tabletype、display为空,不分主子表
		if(ftablelst.size() == 1){
			FormTable fmaintable = (FormTable)ftablelst.get(0);
			fmaintable.setTabletype("");
			fmaintable.setDisplay("");
		}
		//组装formlst中的viewlst中的sheetname
		List formlst = sessionobject.getFormLst();
		for(int i=0;i<formlst.size();i++){
			FormPage fp = (FormPage)formlst.get(i);
			//注入 form id号
			if(fp.getFormPageId() == null || "".equals(fp.getFormPageId()) || "null".equals(fp.getFormPageId())){
				fp.setFormPageId(String.valueOf(UUIDLong.longUUID()));
			}
			//注入值：<Form name="软件产品定货单" type="seeyonform">
			fp.setName((String)request.getParameter("sheetname" + i));
		}
		
		for(int i = 0;i<data.getTableLst().size();i++){
			FormTable fotab = (FormTable)data.getTableLst().get(i);
			if(i !=0){
				fotab.setOnwerfield(mastertablename+"Id");
				data.initSlaveTableList();
			}	   
		}
		sessionobject.setData(data);
	}
	
	public static void generateFormsortByAccountId(long accountId){
		TempleteCategoryManager templeteCategoryManager = (TempleteCategoryManager)ApplicationContextHolder.getBean("templeteCategoryManager");
		for(int i=0;i<3; i++){
			TempleteCategory templeteCategory = new TempleteCategory();
			if(i==0){
				templeteCategory.setId(UUIDLong.longUUID());
				//templeteCategory.setName("财务审批");
				templeteCategory.setName(Constantform.getString4CurrentUser("form.create.finance.label"));
			}else if(i==1){
				templeteCategory.setId(UUIDLong.longUUID());
				//templeteCategory.setName("行政审批");
				templeteCategory.setName(Constantform.getString4CurrentUser("form.create.administration.label"));
			}else if(i==2){
				templeteCategory.setId(UUIDLong.longUUID());
				//templeteCategory.setName("人事审批");
				templeteCategory.setName(Constantform.getString4CurrentUser("form.create.manpower.label"));
			}		
			templeteCategory.setType(4);
			templeteCategory.setParentId(4l);		
			templeteCategory.setSort(0);
			templeteCategory.setOrgAccountId(accountId);
			templeteCategoryManager.save(templeteCategory);
		}
		
	}
	private static Reader chgToReader(String aStr) throws SeeyonFormException {
		//long callid = log.debug_CallMethod("chgToReader", "aStr", aStr);
		Reader reader = null;
		try {
			reader = new InputStreamReader(new ByteArrayInputStream(aStr
					.getBytes()));
		} catch (Exception uee) {
			throw new InfoPathParseException(
					InfoPathParseException.C_iParseErrode_UnsupportedEncoding,
					uee);
		}
		//log.debug_Return(callid, reader);
		return reader;
	}
}
