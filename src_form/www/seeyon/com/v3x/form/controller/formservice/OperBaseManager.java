package www.seeyon.com.v3x.form.controller.formservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMEnd;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStart;
import net.joinwork.bpm.definition.BPMTransition;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.xml.sax.SAXException;

import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.TUserInfo;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormHBConfiguration;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputExtendManager;
import www.seeyon.com.v3x.form.base.inputextend.inf.IInputRelation;
import www.seeyon.com.v3x.form.base.systemvalue.UserFlowId;
import www.seeyon.com.v3x.form.base.systemvalue.inf.ISystemValueManager;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.SeeyonFormCheckException;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.EventTemplateObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormAppAuthObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormOperAuthObject;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.Matchdata;
import www.seeyon.com.v3x.form.controller.pageobject.Operation;
import www.seeyon.com.v3x.form.controller.pageobject.Operation_BindEvent;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.controller.pageobject.TemplateObject;
import www.seeyon.com.v3x.form.controller.query.QueryHelper;
import www.seeyon.com.v3x.form.controller.query.QueryObject;
import www.seeyon.com.v3x.form.controller.report.ReportHelper;
import www.seeyon.com.v3x.form.controller.report.ReportObject;
import www.seeyon.com.v3x.form.dao.FormAccessDao;
import www.seeyon.com.v3x.form.dao.FormTableValueDao;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormAppResource;
import www.seeyon.com.v3x.form.domain.FormFlowid;
import www.seeyon.com.v3x.form.domain.FormOwnerList;
import www.seeyon.com.v3x.form.domain.FormPropertList;
import www.seeyon.com.v3x.form.domain.FormTableValueSign;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathParseException;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Calculate;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DataSource;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Enum;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FieldInput;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_FormView;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Operation;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Operation.IOperationField;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_SlaveTable;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_Submit;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_ViewBindEventBind;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_xsd;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_xsl;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputCheckbox;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputExtend;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputExternalwriteAhead;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputHandwrite;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputOutwrite;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputRadio;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputRelation;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputSelect;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputTLable;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputText;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputTextArea;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.UserDefineXMLInfoImpl;
import www.seeyon.com.v3x.form.manager.define.bind.SeeyonFormBindImpl;
import www.seeyon.com.v3x.form.manager.define.bind.auth.FormAppAuth;
import www.seeyon.com.v3x.form.manager.define.bind.auth.OperationAuth;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormField;
import www.seeyon.com.v3x.form.manager.define.data.base.FormIndex;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource.IDataGroup;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.define.form.dataformat.FormatManager;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TAppBindType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TviewType;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonInputExtend;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonSystemValue;
import www.seeyon.com.v3x.form.manager.define.query.ConditionInput;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.Description;
import www.seeyon.com.v3x.form.manager.define.query.OrderByColum;
import www.seeyon.com.v3x.form.manager.define.query.ParseUserCondition;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QueryUserConditionDefin;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.ShowDetail;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.QueryResultImpl;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.ReportDataColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportHeadColum;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.IConditionList_Report;
import www.seeyon.com.v3x.form.manager.define.report.reportresult.inf.IReportResult;
import www.seeyon.com.v3x.form.manager.define.trigger.EventAction;
import www.seeyon.com.v3x.form.manager.define.trigger.EventCalculate;
import www.seeyon.com.v3x.form.manager.define.trigger.EventCondition;
import www.seeyon.com.v3x.form.manager.define.trigger.EventEntity;
import www.seeyon.com.v3x.form.manager.define.trigger.EventMapping;
import www.seeyon.com.v3x.form.manager.define.trigger.EventRelatedForm;
import www.seeyon.com.v3x.form.manager.define.trigger.EventTask;
import www.seeyon.com.v3x.form.manager.define.trigger.EventTemplate;
import www.seeyon.com.v3x.form.manager.define.trigger.EventValue;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.IConditionList;
import www.seeyon.com.v3x.form.manager.inf.IFormResoureProvider;
import www.seeyon.com.v3x.form.manager.inf.IFormResoureProvider.IPropertyInfo;
import www.seeyon.com.v3x.form.manager.inf.IFormResoureProvider.IResourceInfo;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import www.seeyon.com.v3x.form.manager.resoureprovider.CabFileResourceProvider;
import www.seeyon.com.v3x.form.manager.resoureprovider.ResourceInfoImpl;
import www.seeyon.com.v3x.form.manager.resoureprovider.ResourceInfoImpl_String;
import www.seeyon.com.v3x.form.manager.trigger.EventTriggerForHistoryData;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.CreateTableNumber;
import www.seeyon.com.v3x.form.utils.FormHelper;
import www.seeyon.com.v3x.form.utils.LogUtil;
import www.seeyon.com.v3x.form.utils.StringUtils;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FormBody;
import com.seeyon.v3x.collaboration.domain.FormContent;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.flow.model.FlowBean;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class OperBaseManager implements IOperBase{ 
	private FormDaoManager formDaoManager;// = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	private static RuntimeCharset fCurrentCharSet = SeeyonForm_Runtime.getInstance().getCharset();
	private BindHelper  bindHelper;
	private static Log log = LogFactory.getLog(OperBaseManager.class);
    private TempleteCategoryManager templeteCategoryManager;
	
	
	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}
	public BindHelper getBindHelper() {
		return bindHelper;
	}

	public void setBindHelper(BindHelper bindHelper) {
		this.bindHelper = bindHelper;
	}

	public FormDaoManager getFormDaoManager() {
		return formDaoManager;
	}

	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
    private List addtablelist = new ArrayList();
    
    private List deletablelist = new ArrayList();


	/********************baseinfo****************************/

	public static RuntimeCharset getFCurrentCharSet() {
		return fCurrentCharSet;
	}

	public static void setFCurrentCharSet(RuntimeCharset currentCharSet) {
		fCurrentCharSet = currentCharSet;
	}

	/**
	 * 用于装配应用名称,所属人名称
	 * @param categorylst
	 * @return
	 * @throws DataDefineException 
	 * @throws BusinessException 
	 */
	public List assignCategory(List categorylst) throws DataDefineException, BusinessException{
		if(categorylst == null) return null;
		List formlist = new ArrayList();
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		if(templeteCategoryManager==null)
		{
	      templeteCategoryManager = (TempleteCategoryManager)ApplicationContextHolder.getBean("templeteCategoryManager");
		}
		for(int i=0;i<categorylst.size();i++){
            if(categorylst.get(i) instanceof FormAppMain){
            	FormAppMain fam = (FormAppMain)categorylst.get(i);
				String catename = "";
				if(templeteCategoryManager.get(fam.getCategory()) !=null)
					catename = templeteCategoryManager.get(fam.getCategory()).getName();
				catename = Constantform.getString4CurrentUser(catename);			
			    fam.setCategoryName(catename);
			    
			    FormOwnerList fol = new FormOwnerList();
				fol.setAppmainId(fam.getId());
				List formownerlst = getFormDaoManager().queryOwnerListByCondition(fol);
				StringBuffer sbid = new StringBuffer();
				StringBuffer sbname = new StringBuffer();
				for(int j=0;j<formownerlst.size();j++){
					FormOwnerList formowner = (FormOwnerList) formownerlst.get(j);					
					V3xOrgMember vom = orgManager.getMemberById(formowner.getOwnerId());
					if(vom != null){
						sbid = sbid.append(vom.getId());
						sbname = sbname.append(vom.getName());
						if(j != formownerlst.size()-1){
							sbid.append(",");
							sbname.append("、");
						}
					}
				}
				fam.setUserids(sbid.toString());
				fam.setUsernames(sbname.toString());
				formlist.add(fam);	
			}else{
				Object[] arr = (Object[])categorylst.get(i);
				
				FormAppMain fam = new FormAppMain();
				StringBuffer sbid = new StringBuffer();
				StringBuffer sbname = new StringBuffer();
				for(int a=0;a<arr.length;a++){
					if(arr[a] instanceof FormAppMain){
						fam = (FormAppMain)arr[1];
						String catename = "";
						if(templeteCategoryManager.get(fam.getCategory()) !=null)
							catename = templeteCategoryManager.get(fam.getCategory()).getName();
						catename = Constantform.getString4CurrentUser(catename);			
//						ApplicationCategoryEnum ae = getEnum(value);
//						if(ae != null){
							fam.setCategoryName(catename);
//						}
					}else{
						FormOwnerList formowner = (FormOwnerList) arr[a];
						V3xOrgMember vom = orgManager.getMemberById(formowner.getOwnerId());			
						if(vom != null){
							sbid = sbid.append(vom.getId());
							sbname = sbname.append(vom.getName());

							if(a != arr.length-2){
								sbid.append(",");
								sbname.append("、");
							}
						}
					}				
				}
				fam.setUserids(sbid.toString());
				fam.setUsernames(sbname.toString());
				formlist.add(fam);
//				
//				FormOwnerList fol = (FormOwnerList)arr[0];
////				fol.setAppmainId(fam.getId());
////				List formownerlst = getFormDaoManager().queryOwnerListByCondition(fol);
//				StringBuffer sbid = new StringBuffer();
//				StringBuffer sbname = new StringBuffer();
//				for(int j=0;j<formownerlst.size();j++){
//					FormOwnerList formowner = (FormOwnerList) formownerlst.get(j);
//					OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
//					V3xOrgMember vom = orgManager.getMemberById(formowner.getOwnerId());			
//					sbid = sbid.append(vom.getId());
//					sbname = sbname.append(vom.getName());
	//
//					if(j != formownerlst.size()-1){
//						sbid.append(",");
//						sbname.append("、");
//					}
//				}
//				fam.setUserids(sbid.toString());
//				fam.setUsernames(sbname.toString());
			}
			
		}
		return formlist;
	}
	private ApplicationCategoryEnum getEnum(int key) {
		ApplicationCategoryEnum[] enums = ApplicationCategoryEnum.values();

		if (enums != null) {
			for (ApplicationCategoryEnum enum1 : enums) {
				if (enum1.getKey() == key) {
					return enum1;
				}
			}
		}

		return null;
	}

	/**
	 * 对接收上传文件方法的封装
	 * @param fileManager
	 * @param urls
	 * @param createDates
	 * @param mimeTypes
	 * @param names
	 * @return
	 * @throws SeeyonFormException
	 */
	public  String getXSNSaveDirectory(FileManager fileManager,String[] urls,
			String[] createDates,String[] mimeTypes,String[] names) throws SeeyonFormException{
		String directry = "";
		//		System.out.println("Here  is begin index!"+urls.length+createDates.length);
		if(urls == null){
			//throw new SeeyonFormException(1,"文件不存在");
			throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.fileisinexistence.label"));
		}
		File file = null;
		String path = null;
		//long userId = CurrentUser.get().getId();
		for(int i=0;i<urls.length;i++){
			Long fileId=Long.parseLong(urls[i]);
			Date createDate=null;
			//createDate = format.parse(createDates[i]);
			createDate = Datetimes.parseDate(createDates[i]);;
			//System.out.println("createDate:"+createDate);
			try {
				file=fileManager.getFile(fileId, createDate);
				//System.out.println("The URL:"+file.getPath()+" "+file.getName()+" "+file.length()+" Type:"+mimeTypes[i]+" name:"+names[i]);
				path = file.getPath();
				//log.warn("第["+i+"]个urls下path的值:" +path);
				  Date   dateCurrStart   =   new   Date();   
				  //String   startTime   =   dateFormatStart.format(dateCurrStart).substring(0,4); 
				  String   startTime   =   Datetimes.formatDate(dateCurrStart).substring(0,4);
				if(directry.equals("")){
					int position = path.indexOf(String.valueOf(startTime));
					directry = path.substring(0, position);
				}
				//文件名为：ex   dhd75894379743954.xsn
				//String filename = OperHelper.addUserId(names[i], userId);
				directry = path;
				directry = path+".xsn";
				File realfile = new File(directry);
				OperHelper.CopyFile(file,realfile);
			}catch(Exception e){
				log.error("对接收上传文件方法的封装出错",e);
				}
			}
		//System.out.println("=====" + directry);
		return directry;
	}
	/**
	 * 解析XSN文件
	 * @param so
	 * @param directry
	 * @throws SeeyonFormException
	 */
	public  void parseXSN(SessionObject so,String directry,FileManager fileManager) throws SeeyonFormException{	
		//System.out.println("=====2" + directry);
		if(log.isDebugEnabled()){
			log.debug("OperBaseManager().parseXSN: " + so + "," + directry);
		}
		log.warn("OperBaseManager().parseXSN: " + so + "," + directry);
		ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(
				StringUtils.readFileData(directry));
		IFormResoureProvider fResourceProvider = new CabFileResourceProvider(
				fInfopathxsn);
		Long id =so.getFormid();
		HashMap oldlogo = new HashMap();
		if(id !=null)
		  oldlogo = getlogo(id);
		InfoPathObject xsf = new InfoPathObject();
		xsf.setResourceProvider(fResourceProvider);
		//xsf.setFileManager(fileManager);
		xsf.setOldlogo(oldlogo);
		xsf.loadFormXSNFile();
		so.setXsf(xsf);
	}
	
	private HashMap getlogo(Long id) throws SeeyonFormException{
		HashMap oldlogo = new HashMap();
		FormAppResource fare = new FormAppResource();
		fare.setSheetName(id);
		List appresourcelist = getFormDaoManager().queryResourceByAppid(fare);
		for(int i = 0;i<appresourcelist.size();i++){
			FormAppResource resource = (FormAppResource)appresourcelist.get(i);
			if(resource.getName().indexOf("/extend/OutViewFile/") !=-1){
				String ftem = resource.getContent();
				Document document = null;
				SAXReader sr = new SAXReader();
				try {
					sr.setXMLReaderClassName("org.gjt.xpp.sax2.Driver");
					document = sr.read(chgToReader(chgNameSpace(ftem)));			
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					log.error("infopath中带有图片，读取outviewfile文件错误", e);
					//e.printStackTrace();
				}catch (DocumentException e) {
					// TODO Auto-generated catch block
					log.error("infopath中带有图片，读取outviewfile文件错误", e);
					//e.printStackTrace();
				}		
				Element root = document.getRootElement();
				List imgList = root.selectNodes("//img");
				List img1List = new ArrayList();
				for(int img = 0; img < imgList.size(); img++){//所有img节点
					Element span = (Element)imgList.get(img);
					if(span.attribute("srcId") !=null){
						String img_srcid = span.attribute("srcId").getValue();
						oldlogo.put(img_srcid, img_srcid);
					}					
				}
			}
		}
		return oldlogo;
	}
	
	private String chgNameSpace(String content) {
		int start = content.indexOf("<xsl:stylesheet");
		int end = content.indexOf("<xsl:output");
		content = content.substring(0, start) + NAMESPACE
				+ content.substring(end);
		return content;
	}
	/**
	 * 把字符串转换为Reader
	 * 
	 * @param aString
	 *            字符串
	 * 
	 * @return Reader
	 * @throws SeeyonFormException
	 */
	private Reader chgToReader(String aStr) throws SeeyonFormException {		
		Reader reader = null;
		try {
			reader = new InputStreamReader(new ByteArrayInputStream(aStr
					.getBytes()));
		} catch (Exception uee) {
			throw new InfoPathParseException(
					InfoPathParseException.C_iParseErrode_UnsupportedEncoding,
					uee);
		}
		return reader;
	}
	private static final String NAMESPACE = "<xsl:stylesheet version=\"1.0\""
		+ " xmlns:my=\"www.seeyon.com/form/2007\""
		+ " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\""
		+ " xmlns:msxsl=\"www.seeyon.com/form/2007\""
		+ " xmlns:xd=\"www.seeyon.com/form/2007\""
		+ " xmlns:x=\"urn:schemas-microsoft-com:office:excel\""
		+ " xmlns:xdExtension=\"www.seeyon.com/form/2007\""
		+ " xmlns:xdXDocument=\"www.seeyon.com/form/2007\""
		+ " xmlns:xdSolution=\"www.seeyon.com/form/2007\""
		+ " xmlns:xdFormatting=\"www.seeyon.com/form/2007\""
		+ " xmlns:xdImage=\"www.seeyon.com/form/2007\">";
	/**
	 * 装配一个新的显示字段lst
	 * @param tablenumber
	 * @param masterlst
	 * @param slavelst
	 * @param tablst
	 * @return
	 * @throws DataDefineException 
	 */
	public synchronized List parseFieldName(List masterlst,List slavelst,List tablst){
		List returnlst = new ArrayList();
		//主表先增加一次
		String tableNumber = null;
        try {
            tableNumber = incrementAndGetBiggestValueSign();
        } catch (DataDefineException e) {
            log.error("更新tablevaluesign表错误", e);
        }
        for(int i=0;i<masterlst.size();i++){
        	String masterfieldname = (String)masterlst.get(i);
        	TableFieldDisplay tfd = new TableFieldDisplay();
        	tfd.setName(masterfieldname);   	
        	tfd.setTablename(IPagePublicParam.tablename + tableNumber);
        	tfd.setTablenumber(Long.valueOf(tableNumber));
        	returnlst.add(tfd);
    	}
        String table = null;
        //用于控制子表的编号
        try {
            tableNumber = incrementAndGetBiggestValueSign();
        } catch (DataDefineException e) {
            log.error("更新tablevaluesign表错误", e);
        }
        for(int i=0;i<slavelst.size();i++){
        	Map fieldnamemap = (Map)slavelst.get(i);
            Set set = fieldnamemap.entrySet();
            Iterator it = set.iterator();
            for(;it.hasNext();){
            	Map.Entry me = (Map.Entry)it.next();
            	TableFieldDisplay tfd = new TableFieldDisplay();
                String mapname = (String)me.getKey();
                String mapvalue = (String)me.getValue();
                for(int j=0;j<tablst.size();j++){
                	Map tablenamemap = (Map)tablst.get(i);
                    Set tablenameset = tablenamemap.entrySet();
                    Iterator tablenameit = tablenameset.iterator();
                    for(;tablenameit.hasNext();){
                    	Map.Entry tablenameme = (Map.Entry)tablenameit.next();
                        String tablenamemapname = (String)tablenameme.getKey();
                        String tablenamemapvalue = (String)tablenameme.getValue();
                        if(mapname.indexOf(tablenamemapname) != -1){
                        	if(i==0){
                        		table = tablenamemapvalue;
                        	}
                            if(!table.equals(tablenamemapvalue)){
                            	table = tablenamemapvalue;
                            	try {
                            	    tableNumber = incrementAndGetBiggestValueSign();
                                } catch (DataDefineException e) {
                                    log.error("更新tablevaluesign表错误", e);
                                }
                            }
                        }
                    }
                }
                //注入原来的值
                tfd.setEditablename(table);
            	tfd.setName(mapvalue);
            	tfd.setTablename(IPagePublicParam.tableson + tableNumber); 
            	tfd.setTablenumber(Long.valueOf(tableNumber));
            	returnlst.add(tfd);
            }
    	}      
		return returnlst;
	}
	/********************inputdata****************************/
	
	/********************operconfig***************************/
	/********************finish**************************/
	/********************************************************/
	/**
	 * 表单填写完成后，保存表单相应的xml及生成对应的数据库对象

	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 */
	public void LoadFromCab(HttpSession session, FileManager fileManager) throws SeeyonFormException{
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().createApplication(); 
		PoCheckManager pom = (PoCheckManager)SeeyonForm_Runtime.getInstance().getPoCheckManager();
        SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
        List tablefieldlst = sessionobject.getTableFieldList();
		//组织xsn
		String path = sessionobject.getXsnpath();
		if(path == null){
        //	throw new SeeyonFormException(1,"路径为空！");
			throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.pathisnull.label"));
		}
		RuntimeCharset fCharset=SeeyonForm_Runtime.getInstance().getCharset();
		ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(StringUtils.readFileData(path));
//		if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue())
//			sessionobject.setOrgAllMenu(BindHelper.getMenuMap());
//		SeeyonFormImpl sfi = new SeeyonFormImpl();
		UserDefineXMLInfoImpl fxml=new UserDefineXMLInfoImpl();
		ChangeObjXml cox = new ChangeObjXml();
		
		//执行验证程序
//		if(!pom.doCheck(sessionobject)){
//			throw new DataDefineException(1,"","验证sessionobject出错！");
//		}
		
		List<SeeyonFormException> exceptionList = pom.doCheck(sessionobject);
		if(exceptionList.size() != 0){
			SeeyonFormCheckException sce = new SeeyonFormCheckException(1);
			sce.setList(exceptionList);
			throw sce;
		}
		//组织all.xml
		Map map = new HashMap();
//		08-05-19修改增加id
		map.put("FormId", sessionobject.getFormid());
		map.put("FormName", sessionobject.getFormName());
		map.put("DataDefine", sessionobject.getData());
		map.put("FormList", sessionobject.getFormLst());
		if( sessionobject.getTemplateobj() != null){
			map.put("TemplateObject", sessionobject.getTemplateobj());	
		}
		if(sessionobject.getReportConditionList().size()!=0){
			map.put("ReportList",sessionobject.getReportConditionList());
		}
		if(sessionobject.getQueryConditionList().size()!=0){
			map.put("QueryList",sessionobject.getQueryConditionList());
		}
		String ftemp = cox.createSeeyonDataDefineXml(2,map,sessionobject);
		ftemp=fCharset.SystemDefault2SelfXML(ftemp);
		fxml.setSeeyonFomDefineXML(ftemp);
		
		//组织Operation_001.xml
		List formlst = sessionobject.getFormLst();
		for(int i=0;i<formlst.size();i++){
			FormPage formpage = (FormPage)formlst.get(i);
			for(int j=0;j<formpage.getOperlst().size();j++){
				Operation operation = (Operation)formpage.getOperlst().get(j);
				//如果客户没有在页面进行操作，注入默认值

				int regflag = 1;
				if(operation.getOperlst() == null){
					operation.setOperlst(OperHelper.getFixOperlst(tablefieldlst,operation.getType()));
				}
				if(operation.isEditflag()){
					regflag = 1;
				}else{
					regflag = 2;
				}
				Map formmap = new HashMap();
				formmap.put("Operation", operation);
				ftemp=fCharset.SystemDefault2SelfXML(cox.createOperationXml(regflag,operation.getType(),tablefieldlst, formmap,sessionobject));
				fxml.addResource(operation.getFilename(), ftemp);
			}
		}		
		//组织bindschema.xml
		Map binschemamap = new HashMap();
		binschemamap.put("TableFieldList", sessionobject.getTableFieldList());
		ftemp=fCharset.SystemDefault2SelfXML(cox.creatBindschemaXml(2, binschemamap));
		fxml.addResource("bindschema.xml",ftemp);
		
		//组织bindAppData.xml
		
		//组织defaultInput.xml
		List inputlst = sessionobject.getFieldInputList();
		Map defaultmap = new HashMap();
		defaultmap.put("FieldInputList", inputlst);
		ftemp=getFCurrentCharSet().SystemDefault2SelfXML(cox.createDefaultInputXml(2, defaultmap));
		fxml.addResource("defaultInput.xml",ftemp);		
		
		//组织submitData_new.xml
		fapp.setCategory(sessionobject.getFormsort());
		fapp.setFormType(sessionobject.getFormType());
		//fapp.loadFromCAB(fInfopathxsn, fxml);
		fapp.loadFromCAB(fInfopathxsn, fxml,sessionobject,fileManager);
		Document doc = null;
		doc  = dom4jxmlUtils.paseXMLToDoc(fxml.getSeeyonFomDefineXML());	
		Element root = doc.getRootElement();
		// 准备资源文件
		IFormResoureProvider fResourceProvider = fapp.getFResourceProvider();
		Element dataroot = OperHelper.loadDataDefine(fapp,getFCurrentCharSet(),root, fResourceProvider);
		/**组织表单数据**/
		FormAppMain fam = new FormAppMain();
		//注入表单ID
		fam.setId(sessionobject.getFormid());
		//注入表单名称
		fam.setName(getFCurrentCharSet().JDK2DBIn(fapp.getAppName()));
		//注入表单状态：草稿
		fam.setState(Integer.parseInt(sessionobject.getFormstate()));
		//注入应用分类
		fam.setCategory(fapp.getCategory());
		//表单类型
		fam.setFormType(sessionobject.getFormType());
		
        //保存绑定信息			
		if(sessionobject.getTemplateobj() != null){
            HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
			getBindHelper().save(sessionobject.getTemplateobj(),sessionobject, currentFormNewflow);
		}
		
		if(!BindHelper.checkFormCode(String.valueOf(sessionobject.getFormid()), sessionobject.getFormCode(), sessionobject.getFormType())){
			throw new DataDefineException(1,"表单编号已经有重名！");
		}
		
        //保存主要信息
		try{
			newpostdata(root,fResourceProvider,fam,sessionobject);
			
		}catch(Exception e){
			//throw new DataDefineException(1,"事物实例失败，数据库保存出错","事物实例失败，数据库保存出错");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
		}	
		updateFlowIdstate(sessionobject);
		    //保存主要信息		
		    //fam = saveCreateInfoToDB(root, fResourceProvider,fam);		
			//把table名与其表号存入数据库
			//insertNewestTableName(sessionobject,fam);
			//TODO 把使用人存入数据库	
			//insertFormAppAttachMan(sessionobject,fam);           
			//把AccessObject存入数据库(统计、查询、菜单)
			/*if(sessionobject.getReportConditionList().size()!=0 ||sessionobject.getQueryConditionList().size() !=0){
				insertAccessObject(sessionobject);
			}*/						
			//throw new DataDefineException(1,"好","好");
			//最后一步执行建表的操作
			fapp.getDataDefine().createStorage(dataroot, fResourceProvider);

	}
	/**
	 * 保存主要的相关信息
	 * @param fapp
	 * @param aInput
	 * @param aUserDefineXML
	 * @throws SeeyonFormException
	 */
	private synchronized FormAppMain saveCreateInfoToDB(Element aRoot, IFormResoureProvider aProvider,FormAppMain fam,Session fnewSession) throws SeeyonFormException {
		isExistsThisForm(fam.getName());
		FormAppMain queapp = new FormAppMain();
		queapp.setName(fam.getName());
		//根据查询数据库中是否有此纪录,新增时只增一条
		Long  queryid = (Long)getFormDaoManager().isExistsAppMain(queapp);
		List<FormAppResource> returnfarlst = new ArrayList<FormAppResource>();
		List<FormPropertList> returnfplst = new ArrayList<FormPropertList>();
		if(queryid == null){
			String ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(IXmlNodeName.Bind).asXML());
			fam.setBindInfo(getFCurrentCharSet().JDK2DBIn(ftemp));
	
			ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(
					IXmlNodeName.Define).asXML());
			fam.setDataStructure(getFCurrentCharSet().JDK2DBIn(ftemp));
			
			ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(
					IXmlNodeName.Trigger).asXML());
			fam.setTriggerConfig(getFCurrentCharSet().JDK2DBIn(ftemp));
	
			List<FormPropertList> fpllst = new ArrayList<FormPropertList>();
			FormPropertList fPropertyInfoDB;
			for (IPropertyInfo fInfo : aProvider.getFormPropertyList()) {
				fPropertyInfoDB = new FormPropertList();
				fPropertyInfoDB.setName(getFCurrentCharSet().JDK2DBIn(fInfo
						.getPropertyName()));
				fPropertyInfoDB.setType(fInfo.getPropertyType());
				fPropertyInfoDB.setValue(getFCurrentCharSet().JDK2DBIn(fInfo
						.getPropertyValue()));
				fPropertyInfoDB.setId(UUIDLong.longUUID());
				fPropertyInfoDB.setSheetName(fam.getId());
				fnewSession.save(fPropertyInfoDB);
				returnfplst.add(fPropertyInfoDB);
			}
			fam.setPropertlst(returnfplst);
			// 子表FormAppResource赋值	
			List<FormAppResource> farlst = new ArrayList<FormAppResource>();
			FormAppResource fResourceDB = new FormAppResource();
			for (IResourceInfo resource : aProvider.getResourceList()) {
				fResourceDB = new FormAppResource();
				fResourceDB.setName(getFCurrentCharSet().SystemDefault2Dbin(resource
						.getResourceName()));
				fResourceDB.setContent(getFCurrentCharSet().SystemDefault2Dbin(resource
						.getResourceInfo()));
				fResourceDB.setId(UUIDLong.longUUID());
				fResourceDB.setSheetName(fam.getId());
				fnewSession.save(fResourceDB);
				returnfarlst.add(fResourceDB);
			}
			fam.setResourcelst(returnfarlst);	
			fam.setSystemdatetime(SystemdataTime());
			//新建表单时默认为启用状态。
			fam.setFormstart(1);
			fnewSession.save(fam);						
			//return (FormAppMain)getFormDaoManager().saveAppMain(fam);
			return fam;
		}else{
			return null;
		}
	}

	/**
	 * 新增时把table名与其表号存入数据库
	 * @param tablefieldlst
	 * @param fdm
	 * @throws DataDefineException
	 */
	private void insertNewestTableName(SessionObject sessionobject, FormAppMain fam) throws DataDefineException{
		/*
		List tablefieldlst = sessionobject.getTableFieldList();
		String table = null;
		for(int i=0;i<tablefieldlst.size();i++){
			TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
			if(i==0){
			FormTableValue ftv = new FormTableValue();
			table = tfd.getTablename();
			ftv.setName(tfd.getTablename());
			ftv.setValue(tfd.getTablenumber());
			ftv.setSheetName(fam.getId());
			getFormDaoManager().insertTableValue(ftv);
			}
			if(!tfd.getTablename().equals(table)){
				FormTableValue ftv = new FormTableValue();
				table = tfd.getTablename();
				ftv.setSheetName(fam.getId());
				ftv.setName(tfd.getTablename());
				ftv.setValue(tfd.getTablenumber());
				getFormDaoManager().insertTableValue(ftv);
			}
		}
//		FormTableValue ft = new FormTableValue();
//		getFormDaoManager().queryTableValue(ft);
		//if(true) throw new DataDefineException(1,"");
		 */
	}
	
	/**
	 * 新增时把AccessObject存入数据库
	 * @param tablefieldlst
	 * @param fdm
	 * @throws SeeyonFormException 
	 */
	private void insertAccessObject(SessionObject sessionobject,Session fnewSession) throws SeeyonFormException{
		List reportlist = sessionobject.getReportConditionList();
		List querylist = sessionobject.getQueryConditionList();
		Collection<FormAppAuthObject> aboList = sessionobject.getFormAppAuthObjectMap().values();
		FormAppAuthObject appAuthObject = sessionobject.getAppAuthObject();
		//基础数据
		FormOperAuthObject operAuthObject = appAuthObject.getAppOperAuthObjectMap().get(sessionobject.getFormName());
		if(operAuthObject != null){
			List<FomObjaccess> list = operAuthObject.getObjAccessList();
			for (FomObjaccess obja : list) {
			     obja.setId(UUIDLong.longUUID());
			     fnewSession.save(obja);
			}
		}
		//应用授权
		for (FormAppAuthObject abo : aboList) {
			for (FormOperAuthObject aao : abo.getAppOperAuthObjectMap().values()) {
				List<FomObjaccess> list = aao.getObjAccessList();
				for (FomObjaccess obja : list) {
				     obja.setId(UUIDLong.longUUID());
				     fnewSession.save(obja);
				}
			}
		}
		//查询
		for(int i = 0;i<querylist.size();i++){
			QueryObject query = (QueryObject)querylist.get(i);
			List objaccesslist = query.getObjAccessList(sessionobject);
				for(int j =0;j<objaccesslist.size();j++){
				     FomObjaccess obja = (FomObjaccess)objaccesslist.get(j);
				     obja.setId(UUIDLong.longUUID());				    
				     fnewSession.save(obja);
				}
		}
		//统计
		for(int i = 0;i<reportlist.size();i++){
			ReportObject report = (ReportObject)reportlist.get(i);
			List objaccesslist = report.getObjaccesslist(sessionobject);
				for(int j =0;j<objaccesslist.size();j++){
				     FomObjaccess obja = (FomObjaccess)objaccesslist.get(j);
				     obja.setId(UUIDLong.longUUID());
				     fnewSession.save(obja);				   
				}
		}
        //菜单未完成
	}
	
	private void updataAccessObject(SessionObject sessionobject) throws SeeyonFormException{
		List reportlist = sessionobject.getReportConditionList();
		List querylist = sessionobject.getQueryConditionList();
		FomObjaccess fo = new FomObjaccess();
		fo.setRefAppmainId(sessionobject.getFormid());
		List accessList = getFormDaoManager().queObjAccessByCondition(fo);
//		//List accessList = fSession.find(" from FomObjaccess foa where foa.refAppmainId ="+sessionobject.getFormid());
		for(int i = 0;i<accessList.size();i++){
			FomObjaccess access = (FomObjaccess)accessList.get(i);
			fSession.delete(access);
		}
		FormAppAuthObject appAuthObject = sessionobject.getAppAuthObject();
		//基础数据
		String formName = sessionobject.getFormName();
		String formEditName = sessionobject.getFormEditName();
		if(!formName.equals(formEditName)){
			formName = formEditName;
		}
		FormOperAuthObject operAuthObject = appAuthObject.getAppOperAuthObjectMap().get(formName);
		if(operAuthObject != null){
			List<FomObjaccess> list = operAuthObject.getObjAccessList();
			for (FomObjaccess obja : list) {
			     obja.setId(UUIDLong.longUUID());
			     fSession.save(obja);
			}
		}
		
		//业务表单应用授权
		Collection<FormAppAuthObject> collection =  sessionobject.getFormAppAuthObjectMap().values();
		for (FormAppAuthObject abo : collection) {
			for (FormOperAuthObject aao : abo.getAppOperAuthObjectMap().values()) {
				List<FomObjaccess> list = aao.getObjAccessList();
				for (FomObjaccess obja : list) {
				     obja.setId(UUIDLong.longUUID());
				     fSession.save(obja);
				}
			}
		}
		
		//fSession.createQuery("delete from FomObjaccess fo where fo.refAppmainId =?").setLong(0, sessionobject.getFormid()).executeUpdate();
		//fSession.delete("from FomObjaccess fo where fo.refAppmainId =?",new Object[]{sessionobject.getFormid()});
		//查询
		for(int i = 0;i<querylist.size();i++){
			QueryObject query = (QueryObject)querylist.get(i);
			List objaccesslist = query.getObjAccessList(sessionobject);
				for(int j =0;j<objaccesslist.size();j++){
				     FomObjaccess obja = (FomObjaccess)objaccesslist.get(j);
				     obja.setId(UUIDLong.longUUID());
				     fSession.save(obja);
				}
		}
		//统计
		for(int i = 0;i<reportlist.size();i++){
			ReportObject report = (ReportObject)reportlist.get(i);
			List objaccesslist = report.getObjaccesslist(sessionobject);
				for(int j =0;j<objaccesslist.size();j++){
				     FomObjaccess obja = (FomObjaccess)objaccesslist.get(j);
				     obja.setId(UUIDLong.longUUID());
				     fSession.save(obja);
				}
		}
        //菜单未完成
	}
	
	
	/**
	 * 保存所属人数据信息
	 * @param sessionobject
	 * @param fdm
	 * @throws DataDefineException
	 * @throws BusinessException 
	 */
	private void insertFormAppAttachMan(SessionObject sessionobject,FormAppMain fam,Session fnewSession) throws DataDefineException, BusinessException{
		String manid = sessionobject.getAttachManId();
		User user = CurrentUser.get();
		long orgAccountId = user.getLoginAccount();
		if(manid.indexOf(",") != -1){
			String[]manidarray = manid.split(",");
			for(int i=0;i<manidarray.length;i++){
				FormOwnerList fol = new FormOwnerList();
				fol.setAppmainId(fam.getId());
				fol.setOwnerId(Long.valueOf(manidarray[i]));
				fol.setOrg_account_id(orgAccountId);
				//getFormDaoManager().insertOwnerList(fol);
				fol.setId(UUIDLong.longUUID());
				fnewSession.save(fol);
			}
		}else{
			FormOwnerList fol = new FormOwnerList();
			fol.setAppmainId(fam.getId());
			fol.setOwnerId(Long.valueOf(manid));
			fol.setOrg_account_id(orgAccountId);
			//getFormDaoManager().insertOwnerList(fol);
			fol.setId(UUIDLong.longUUID());
			fnewSession.save(fol);
		}
		//if(true) throw new DataDefineException(1,"");
	}
	

	/********************************************************/
	/**
	 * 
	 */
	public List parsenewName(List masterlst,List slavelst,List tablst,List newtablst){
		List returnlst = new ArrayList();
		String mastername=null;	
		 for(int i=0;i<tablst.size();i++){ 	   	
	        	Map fieldnamemap = (Map)tablst.get(i);
	            Set set = fieldnamemap.entrySet();
	            Iterator it = set.iterator();
	            for(;it.hasNext();){
	            	Map.Entry me = (Map.Entry)it.next();
	            	//后台的表名
	                String tablename = (String)me.getKey();
                    //后台的Infopath中的子表名
	                String groupname = (String)me.getValue();
	                if("".equals(groupname) || "null".equals(groupname) || groupname ==null){
	                	mastername = tablename;
	                }
	            }            
	    	} 
        for(int i=0;i<masterlst.size();i++){
        	String masterfieldname = (String)masterlst.get(i);
        	TableFieldDisplay tfd = new TableFieldDisplay();
        	tfd.setName(masterfieldname);
        	tfd.setTablename(mastername);
        	returnlst.add(tfd);
    		}
           	
        String slavename1 = null;    
        for(int i=0;i<slavelst.size();i++){ 	   	
        	Map fieldnamemap = (Map)slavelst.get(i);
            Set set = fieldnamemap.entrySet();
            Iterator it = set.iterator();
            for(;it.hasNext();){
            	Map.Entry me = (Map.Entry)it.next();
            	TableFieldDisplay tfd = new TableFieldDisplay();
                String mapname = (String)me.getKey();
                String mapvalue = (String)me.getValue();
                for(int j=0;j<newtablst.size();j++){
                	Map tablenamemap = (Map)newtablst.get(i);
                    Set tablenameset = tablenamemap.entrySet();
                    Iterator tablenameit = tablenameset.iterator();
                    for(;tablenameit.hasNext();){
                    	Map.Entry tablenameme = (Map.Entry)tablenameit.next();
                        String tablenamemapname = (String)tablenameme.getKey();
                        String tablenamemapvalue = (String)tablenameme.getValue();
                        if(mapname.indexOf(tablenamemapname) != -1){
                        	if(i==0){
                        		slavename1 = tablenamemapvalue;
                        	}
                            if(!slavename1.equals(tablenamemapvalue)){
                            	slavename1 = tablenamemapvalue;
                            }
                        }
                    }
                }
            	tfd.setName(mapvalue);
            	tfd.setTablename(slavename1); 
            	returnlst.add(tfd);
            }
    	}       
		return returnlst;
	}
	
	/**
	 * 本方法只限于查当前用户的，没有扩展多个用户查询
	 * @param bm
	 * @return List
	 */
	public List queryAllData(BaseModel bm) throws DataDefineException{
		//加入权限
		FormAppMain fam = (FormAppMain)bm;
		// 只查当前用户的，只有一个id
		String userid = fam.getUserids();
		User user = CurrentUser.get();
		long orgAccountId = user.getLoginAccount();
		/*if(userid == null || "".equals(userid) ||"null".equals(userid)){
//			throw new DataDefineException(1,"userid为空！","userid为空！");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.useridisnull.label"),Constantform.getString4CurrentUser("form.base.useridisnull.label"));
		}*/
		FormOwnerList fol = new FormOwnerList();
		if(org.apache.commons.lang.StringUtils.isNotBlank(userid))
			fol.setOwnerId(Long.valueOf(userid));
		fol.setOrg_account_id(orgAccountId);
		List<FormAppMain> returnlst = getFormDaoManager().queryAppByOwnerId(fam,fol);
		return returnlst;
	}
	
	
	public List queryAllOther(BaseModel bm) throws DataDefineException{
		//加入权限
		FormAppMain fam = (FormAppMain)bm;
		// 只查当前用户的，只有一个id
		String userid = fam.getUserids();
		User user = CurrentUser.get();
		long orgAccountId = user.getLoginAccount();
//		if(userid == null || "".equals(userid) ||"null".equals(userid)){
////			throw new DataDefineException(1,"userid为空！","userid为空！");
//			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.useridisnull.label"),Constantform.getString4CurrentUser("form.base.useridisnull.label"));
//		}
		FormOwnerList fol = new FormOwnerList();
		if(org.apache.commons.lang.StringUtils.isNotBlank(userid))
			fol.setOwnerId(Long.valueOf(userid));
		fol.setOrg_account_id(orgAccountId);
		List<FormAppMain> returnlst = getFormDaoManager().queryOwnerIdByOther(fam,fol,user.getId());
		return returnlst;
	}
	
	public List queryAllAccess(List<Long> formobjlist,List<Long> appidlist,int objtype) throws DataDefineException{
		//StringBuffer sb = new StringBuffer();
		List<Long> returnlst = new ArrayList<Long>();
		List<FormAppMain> appmainlist = new ArrayList<FormAppMain>();
		//List returnlist = new ArrayList();
//		for(int i=0;i<formobjlist.size();i++){
//			FomObjaccess fam = (FomObjaccess)formobjlist.get(i);
//			Long userid = fam.getUserid();
//			if(userid == null || "".equals(userid) ||"null".equals(userid)){
////				throw new DataDefineException(1,"userid为空！","userid为空！");
//				throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.useridisnull.label"),Constantform.getString4CurrentUser("form.base.useridisnull.label"));
//			}
//			returnlst  = getFormDaoManager().queryByUserid(fam.getUserid(),fam.getObjecttype());	
//			if(returnlst !=null &&returnlst.size() !=0){
//				for(int f = 0;f<returnlst.size();f++){
//					FomObjaccess fob = (FomObjaccess)returnlst.get(f);
//					sb.append(fob.getRefAppmainId());
//					if(f !=returnlst.size()){
//						sb.append(",");
//					}
//				}
//		    }
//		}
		
		returnlst  = getFormDaoManager().queryByUserid(formobjlist,appidlist,objtype);
		
		appmainlist = getFormDaoManager().getFormappmianList(returnlst);
	
//		List newlist = new ArrayList();
//		String idlst = sb.toString();
//		if(!"".equals(idlst) && !"null".equals(idlst) && idlst !=null){
//			String[] idlist = idlst.split(",");
//			Set   s=   new   java.util.HashSet();   
//			for (int i=0;i<idlist.length;i++){   
//			  s.add(idlist[i]);   
//			}   
//			  idlist   =   new   String[s.size()];   
//			  s.toArray(idlist);   
//			for(int i=0;i<idlist.length;i++){ 
//				  newlist.add(idlist[i]);
//			}
//			for(int i=0;i<newlist.size();i++){
//				FormAppMain fa= new FormAppMain();
//				String id = (String)newlist.get(i);
//				fa.setId(Long.parseLong(id));
//				List falst = getFormDaoManager().queryApp(fa);
//				if(falst != null){
//					returnlist.addAll(falst);
//				}
//			}
//		}	
		return appmainlist;
	}
	
	/**
	 * 专用于跳转页面时，判断此名是否已经在数据库存在

	 * @param name
	 * @return
	 * @throws DataDefineException
	 */
	public boolean isExistsThisForm(String name) throws DataDefineException{
		FormAppMain queapp = new FormAppMain();
		queapp.setName(name);
		//根据查询数据库中是否有此纪录,新增时只增一条

		Long  queryid = (Long)getFormDaoManager().isExistsAppMain(queapp);
		if(queryid != null){
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.tablenameisexist.label",name),Constantform.getString4CurrentUser("form.base.tablenameisexist.label",name));
		}else{
			return true;
		}

	}
	
	public BaseModel findBiggestValue() throws DataDefineException{
		/*
		return getFormDaoManager().findBiggestValue();
		*/
		return null;
	}
	
	public synchronized BaseModel findBiggestValueSign() throws DataDefineException{
		return getFormDaoManager().findBiggestValueSign();
	}
	
	public synchronized String incrementAndGetBiggestValueSign() throws DataDefineException{
	    FormTableValueSign formTableValueSign = getFormDaoManager().findBiggestValueSign();
	    Long tableNumber = formTableValueSign.getTableid();
        if(tableNumber == null){
            tableNumber = 0L;
        }
        formTableValueSign.setTableid(tableNumber+1);
	    getFormDaoManager().updateBiggestValueSign(formTableValueSign);
		return CreateTableNumber.createTableNumber(tableNumber.toString());
	}
	public boolean delelctTableValueSign(Long id) throws DataDefineException{	
		 getFormDaoManager().delelctTableValueSign(id);	
    	return true;
	}
	/**
	 * 把table名与其表号存入数据库
	 * @param tablefieldlst
	 * @throws DataDefineException
	 */
	public void saveTableValue(List tablefieldlst) throws DataDefineException{
		//把table名与其表号存入数据库
		/*
		String table = null;
		for(int i=0;i<tablefieldlst.size();i++){
			TableFieldDisplay tfd = (TableFieldDisplay)tablefieldlst.get(i);
			if(i==0){
			FormTableValue ftv = new FormTableValue();
			table = tfd.getTablename();
			ftv.setName(tfd.getTablename());
			ftv.setValue(tfd.getTablenumber());
			getFormDaoManager().insertTableValue(ftv);
			}
			if(!tfd.getTablename().equals(table)){
				FormTableValue ftv = new FormTableValue();
				table = tfd.getTablename();
				ftv.setName(tfd.getTablename());
				ftv.setValue(tfd.getTablenumber());
				getFormDaoManager().insertTableValue(ftv);
			}
		}
		*/
	}
	
	private SessionObject regroupReportObject(List reportlist,SessionObject sessionobj,String id,String formname) throws SeeyonFormException, BusinessException{
		FomObjaccess fo = new FomObjaccess();
		fo.setRefAppmainId(Long.parseLong(id));
		fo.setObjecttype(IPagePublicParam.C_iObjecttype_Report);
		List objacclist = getFormDaoManager().queObjAccessByCondition(fo);	
		for(int i = 0 ;i <reportlist.size();i++){
			SeeyonReportImpl report = (SeeyonReportImpl)reportlist.get(i);
		    ReportObject reportobject = new ReportObject();
		    reportobject.setReport(report);
			reportobject.setObjaccesslist(objacclist);
			reportobject.setFormname(formname);
			reportobject.setChartInfos(report.getChartInfos());
			sessionobj.getReportConditionList().add(reportobject);		
		}		
		return sessionobj;	
	}
	
	
	private SessionObject regroupQueryObject(List querylist,SessionObject sessionobj,String id,String formname) throws SeeyonFormException, BusinessException{
    	FomObjaccess fo = new FomObjaccess();
		fo.setRefAppmainId(Long.parseLong(id));
		fo.setObjecttype(IPagePublicParam.C_iObjecttype_Query);
		List objacclist = getFormDaoManager().queryObjAccessByCondition(fo);	
		for(int i = 0 ;i <querylist.size();i++){
			SeeyonQueryImpl query = (SeeyonQueryImpl)querylist.get(i);
			QueryObject queryobject = new QueryObject();
			queryobject.setQuery(query);
			queryobject.setObjAccessList(objacclist);
			queryobject.setFormname(formname);
			sessionobj.getQueryConditionList().add(queryobject);			
		}		
		return sessionobj;	
	}
	
	/**
	 * 没有进行上传InfoPath的修改
	 * @param iapp
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 * @throws BusinessException 
	 */
	public SessionObject loadFromnoInfoPath(ISeeyonForm_Application iapp,String id) throws SeeyonFormException, DocumentException, BusinessException {
		SessionObject sessionobj = new SessionObject();
		SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) iapp;
		FormAppMain fam = new FormAppMain();
		fam =getFormDaoManager().findApplicationById(Long.valueOf(id));
		List<FormAppMain> famlst = new ArrayList<FormAppMain>();
		famlst.add(fam);
		assignCategory(famlst);
		fam = (FormAppMain)famlst.get(0);
		sessionobj.setFormid(Long.valueOf(id));
		sessionobj.setFormType(fam.getFormType());
		sessionobj.setAttachManId(fam.getUserids());
		sessionobj.setAttachManName(fam.getUsernames());
		sessionobj.setFormsort(fam.getCategory());
        sessionobj.setFormstate(String.valueOf(fam.getState()));
        sessionobj.setFormCode(sapp.getFormCode());
        
		//对查询、统计赋值
        List querylist = sapp.getQueryList();
        List reportlist = sapp.getReportList();
        if(reportlist.size() !=0){
          sessionobj = regroupReportObject(reportlist,sessionobj,id,sessionobj.getFormName());  
        }
        if(querylist.size() !=0){
          sessionobj = regroupQueryObject(querylist,sessionobj,id,sessionobj.getFormName());
        }
        QueryHelper qh = new QueryHelper();
        qh.updateQueryObject(sessionobj.getQueryConditionList(), sessionobj.getDelTablename(), sessionobj.getFieldMap(), sessionobj.getDeletevalue(), sessionobj.getNamespace());
        SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
		sessionobj.setSeedatadefine(seedade);
		sessionobj.setData(seedade.getDataDefine());
	    SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
	    //加防护,看发布的表单数据是否已经存在了.
	    boolean sign = false;
	    for(int i =0;i<sessionobj.getData().getTableLst().size();i++){
	    	FormTable table = (FormTable)sessionobj.getData().getTableLst().get(i);
	    	if(sign == false)
	    	   sign = sessionobj.getData().hasDataRecords(sapp.getSessionFactory(),table.getName());
	    }
	    sessionobj.setIsdatavalue(sign);
	    HashMap flowMap = new LinkedHashMap();
	    for(int i =0;i< seeformbind.getFlowTempletList().size();i++){
	       FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(i);
	       flowMap.put(flow.getId(), flow);
	    }
	    if(seeformbind.getFlowTempletList().size()!=0){
	        TemplateObject temobj= new TemplateObject();
	        temobj.setFlowMap(flowMap);
	        sessionobj.setTemplateobj(temobj);
	    }
		IFormResoureProvider ifrp = sapp.getFResourceProvider();
		// 利用传进来的新的xsf进行组织对象
		
		// 利用DataDefine组出tablefieldlst
		List newtablist = new ArrayList();
		List newtabe = new ArrayList();
		newtabe = fielddatanoInfoPath(sessionobj,seedade,newtabe);
		
		//组织Formlst及operlist
		newtabe = formlistnoInfoPath(sessionobj,newtabe,iapp);
		// 组织defaultInput
		newtabe = defaultinputnoInfoPath(sessionobj,newtabe,sapp,ifrp);
		sessionobj = formqueryenum(sessionobj);
		sessionobj.setTableFieldList(newtabe);
		sessionobj.setTablefieldsize(newtabe.size());
		
		//设置转换SessionObj对象
		applicationToSessionObj(iapp,sessionobj);
		return sessionobj;
	}
	
	private List fielddatanoInfoPath(SessionObject sessionobj,SeeyonDataDefine seedade,List newtabe) throws SeeyonFormException{
		List fieldlist = new ArrayList();
		String namespace = null;
		
		for(int i=0;i<seedade.getDataSource().getDataAreaList().size();i++){
			TableFieldDisplay tfd = new TableFieldDisplay();
			String tablename=seedade.getDataSource().getDataAreaList().get(i).getDBTableName();
			tfd.setTablename(tablename);
			String areaname=seedade.getDataSource().getDataAreaList().get(i).getAreaName();
			tfd.setName(areaname);
			namespace = seedade.getDataSource().getDataAreaList().get(i).getHead();
			fieldlist.add(tfd);
		}
		sessionobj.setNamespace(namespace+":");
		List tablelst = sessionobj.getData().getTableLst();
		String table = null;
		String groupname = null;
		String tablename = null;
		for (int i = 0; i < tablelst.size(); i++) {
			FormTable ft = (FormTable) tablelst.get(i);
            //通过数据库存的子表名找到infopath中的子节点名称
			InfoPath_DataSource datasource = (InfoPath_DataSource)seedade.getDataSource();
			IDataGroup name =datasource.findGroupByTableName(ft.getName());
			if(i==0){
				table =ft.getName();
				groupname = ft.getName();
			}
			if(name !=null){
				groupname = name.getGroupName();
				tablename = name.getTableName();
			}
			List fieldlst = ft.getFieldLst();
			for (int j = 0; j < fieldlst.size(); j++) {
				FormField ff = (FormField) fieldlst.get(j);
				for (int m = 0; m < fieldlist.size(); m++) {
					TableFieldDisplay tfd = (TableFieldDisplay) fieldlist.get(m);
					TableFieldDisplay tfdisplay = new TableFieldDisplay();		
					if (tfd.getName().equals(ff.getDisplay()) && ff.getId().length()>3 && ff.isIs_primary() !=true) {	
					   tfdisplay.setName(tfd.getName());
					   tfdisplay.setBindname(sessionobj.getNamespace() + ff.getDisplay());
					   tfdisplay.setFieldtype(ff.getFieldtype());
					   //tfdisplay.setLength(ff.getFieldlength());
					   if (ff.getFieldlength() != null && !"".equals(ff.getFieldlength()) && !"null".equals(ff.getFieldlength())){			   
						   if(ff.getFieldlength().indexOf(",") == -1){
								 tfdisplay.setLength(ff.getFieldlength());  
						   }else{
								   OperHelper oper = new OperHelper();
								   String length = ff.getFieldlength();
								   tfdisplay.setLength(oper.splitFieldlength(length));
								   tfdisplay.setDigits(oper.splitFieldscale(length));
						   } 
					   }else{  
						   tfdisplay.setLength(""); 
					   }			
					   tfdisplay.setTablename(ft.getName());
					   if(groupname !=null){
						   tfdisplay.setEditablename(groupname);   
					   }					   
					   tfdisplay.setTablenumber(Long.valueOf(ft.getName().substring(
								ft.getName().length() - 4)));
					   tfdisplay.setFieldname(ff.getName());
					   tfdisplay.setId(ff.getId());
					   tfdisplay.setIsnull(ff.isIs_null() == true ? "N" : "Y");	
					   newtabe.add(tfdisplay);
					}
				}		
			}
		}
	  return newtabe;
	}
	
	private List defaultinputnoInfoPath(SessionObject sessionobj,List newtabe,SeeyonForm_ApplicationImpl sapp,IFormResoureProvider ifrp) throws SeeyonFormException, DocumentException{
		MetadataManager metadataManager  = (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		//修改成从缓存里读取，增加防护，modify by wusb at 2011-10-31
		InfoPath_Inputtypedefine inpointy = FormHelper.getInfoPathInputtypedefine(sapp,ifrp);
		List definputlist = new ArrayList();		
		for (int i = 0; i < inpointy.getInputList().size(); i++) {
			if(inpointy.getInputList().get(i) instanceof TIP_InputText) {
				TIP_InputText tiptext = (TIP_InputText)inpointy.getInputList().get(i);			
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setFcalculate(tiptext.getFIpcal());
					ifipinput.setName(tiptext.getDataAreaName());
					ifipinput.setFInputType(tiptext.getInputType());
					ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptext.getStageCalculateXml()));
					ifipinput.setFormatType(tiptext.getFormatType()) ;
					ifipinput.setUnique(tiptext.isUnique() );
					definputlist.add(ifipinput);		
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputTextArea) {
				TIP_InputTextArea tiptextarea = (TIP_InputTextArea)inpointy.getInputList().get(i);			
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
				ifipinput.setFcalculate(tiptextarea.getFIpcal());
				ifipinput.setName(tiptextarea.getDataAreaName());
				ifipinput.setFInputType(tiptextarea.getInputType());
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptextarea.getStageCalculateXml()));
				ifipinput.setFormatType(tiptextarea.getFormatType()) ;
				
				definputlist.add(ifipinput);		
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputTLable) {
				TIP_InputTLable tiplable = (TIP_InputTLable)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
				ifipinput.setFcalculate(tiplable.getFIpcal());
				ifipinput.setName(tiplable.getDataAreaName());
				ifipinput.setFInputType(tiplable.getInputType());
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiplable.getStageCalculateXml()));
				ifipinput.setFormatType(tiplable.getFormatType()) ;
				ifipinput.setUnique(tiplable.isUnique() );
				definputlist.add(ifipinput);		
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputHandwrite) {
				TIP_InputHandwrite tiptext = (TIP_InputHandwrite)inpointy.getInputList().get(i);			
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();					
					ifipinput.setName(tiptext.getDataAreaName());
					ifipinput.setFInputType(tiptext.getInputType());
					definputlist.add(ifipinput);		
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputRadio ){
				TIP_InputRadio tipradio = (TIP_InputRadio)inpointy.getInputList().get(i);
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setFcalculate(tipradio.getFIpcal());
					ifipinput.setName(tipradio.getDataAreaName());
					ifipinput.setFInputType(tipradio.getInputType());					
					InfoPath_Enum ienum = new InfoPath_Enum();
					ienum.setFname(tipradio.getFEnumName());
					ienum.setEnumid(tipradio.getFEnumId());
					ienum.setAppsort(tipradio.getFenumsort());
					ienum.setEnumtype(tipradio.getFenumtype());
					ifipinput.setFenum(ienum);
					ifipinput.setUnique(tipradio.isUnique());
					ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipradio.getStageEnumXml()));	
					ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipradio.getStageCalculateXml()));
					definputlist.add(ifipinput);			
			}if(inpointy.getInputList().get(i) instanceof TIP_InputSelect ){
				TIP_InputSelect tipselect = (TIP_InputSelect)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();	
				ifipinput.setFcalculate(tipselect.getFIpcal());
				ifipinput.setName(tipselect.getDataAreaName());
				InfoPath_Enum ienum = new InfoPath_Enum();
				ienum.setFname(tipselect.getFEnumName());
				ienum.setEnumid(tipselect.getFEnumId());
				ienum.setEnumtype(tipselect.getFenumtype());
				ienum.setAppsort(tipselect.getFenumsort());
				ienum.setFinalChild(tipselect.isFinChild());
				ienum.setLevel(tipselect.getLevel());
				ifipinput.setFenum(ienum);							
				ifipinput.setFInputType(tipselect.getInputType());
				ifipinput.setUnique(tipselect.isUnique());
				ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipselect.getStageEnumXml()));	
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipselect.getStageCalculateXml()));
				definputlist.add(ifipinput);			
			}if(inpointy.getInputList().get(i) instanceof TIP_InputExtend ){
				TIP_InputExtend tipextend = (TIP_InputExtend)inpointy.getInputList().get(i);
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setFcalculate(tipextend.getFIpcal());
                    ifipinput.setName(tipextend.getDataAreaName());
					ifipinput.setFInputType(tipextend.getInputType());
					ifipinput.setStageRSXml(tipextend.getStageRSXml());
					ifipinput.setFormatType(tipextend.getFormatType()) ;
					ifipinput.setRefParams(tipextend.getRefParams());
					ifipinput.setSelectType(tipextend.getSelectType());
					ifipinput.setRelationConditionId(tipextend.getRelationConditionId());
					ifipinput.setRefInputAtt(tipextend.getRefInputAtt());
					ifipinput.setDisplayRelated(tipextend.isDisplayRelated());
					ifipinput.setDisplayBaseForm(tipextend.isDisplayBaseForm());
					ifipinput.setUnique(tipextend.isUnique()) ;
					ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipextend.getStageCalculateXml()));
					ifipinput.setDeeTask(tipextend.getDeeTask());
					definputlist.add(ifipinput);	
			}if(inpointy.getInputList().get(i) instanceof TIP_InputCheckbox) {
				TIP_InputCheckbox tipcheckbox = (TIP_InputCheckbox)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
				ifipinput.setFcalculate(tipcheckbox.getFIpcal());
				ifipinput.setName(tipcheckbox.getDataAreaName());
				ifipinput.setFInputType(tipcheckbox.getInputType());
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipcheckbox.getStageCalculateXml()));
				definputlist.add(ifipinput);		
			}if(inpointy.getInputList().get(i) instanceof TIP_InputRelation ){
				TIP_InputRelation tipRelation = (TIP_InputRelation)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();	
				ifipinput.setFcalculate(tipRelation.getFIpcal());
				ifipinput.setName(tipRelation.getDataAreaName());
				ifipinput.setFInputType(tipRelation.getInputType());
				ifipinput.setFormatType(tipRelation.getFormatType());
				ifipinput.setRefInputName(tipRelation.getRefInputName());
				ifipinput.setRefInputType(tipRelation.getRefInputType());
				ifipinput.setRefInputAtt(tipRelation.getRefInputAtt());
				ifipinput.setUnique(tipRelation.isUnique() );
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipRelation.getStageCalculateXml()));
				definputlist.add(ifipinput);			
			}if(inpointy.getInputList().get(i) instanceof TIP_InputOutwrite ){
				TIP_InputOutwrite tipOutWrite = (TIP_InputOutwrite)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
				ifipinput.setFcalculate(tipOutWrite.getFIpcal());
				ifipinput.setName(tipOutWrite.getDataAreaName());
				ifipinput.setFInputType(tipOutWrite.getInputType());
				ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipOutWrite.getStageCalculateXml()));
				ifipinput.setFormatType(tipOutWrite.getFormatType()) ;
				ifipinput.setUnique(tipOutWrite.isUnique() );
				definputlist.add(ifipinput);			
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputExternalwriteAhead){
				TIP_InputExternalwriteAhead tipExternalwriteAhead = (TIP_InputExternalwriteAhead)inpointy.getInputList().get(i);
				InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
				ifipinput.setName(tipExternalwriteAhead.getDataAreaName());
				ifipinput.setFInputType(tipExternalwriteAhead.getInputType());
				ifipinput.setFormatType(tipExternalwriteAhead.getFormatType());
				ifipinput.setUnique(tipExternalwriteAhead.isUnique());
				ifipinput.setRefInputName(tipExternalwriteAhead.getRefInputName());
				definputlist.add(ifipinput);
			}
		}
		 //把definputlist中的数据装配到newtabe中。
		Set<String> refInputNames = new LinkedHashSet<String>();
		Set<String> outwriteInputNames = new LinkedHashSet<String>();
		Map<String,Map<String,String>> refInputAtts = new LinkedHashMap<String, Map<String,String>>();
		 for(int i = 0;i<newtabe.size();i++){
				TableFieldDisplay tfdi = (TableFieldDisplay)newtabe.get(i);
				for(int a = 0;a<definputlist.size();a++){
					InfoPath_FieldInput ifipinput = (InfoPath_FieldInput)definputlist.get(a);
		    	  if(tfdi.getBindname().equals(ifipinput.getName())){
		    		  if(ifipinput.getFInputType() !=null){
			    		  tfdi.setInputtype(ifipinput.OperationType2str(ifipinput.getFInputType()));
			    		  tfdi.setCompute(ifipinput.getStageCalculateXml());
			    		  tfdi.setEnumtype(ifipinput.getStageEnumXml());
			    		  tfdi.setExtend(ifipinput.getStageRSXml());
			    		  tfdi.setFormatType(ifipinput.getFormatType()) ;
			    		  tfdi.setRefInputName(ifipinput.getRefInputName());
			    		  tfdi.setRefInputType(ifipinput.getRefInputType());
			    		  tfdi.setRefParams(ifipinput.getRefParams());
			    		  tfdi.setSelectType(ifipinput.getSelectType());
			    		  tfdi.setRelationConditionId(ifipinput.getRelationConditionId());
			    		  tfdi.setRefInputAtt(ifipinput.getRefInputAtt());
			    		  tfdi.setDisplayRelated(ifipinput.isDisplayRelated());
			    		  tfdi.setDisplayBaseForm(ifipinput.isDisplayBaseForm());
			    		  tfdi.setDeeTask(ifipinput.getDeeTask());
			    		  if(ifipinput.getFenum()!= null){
			    			  tfdi.setDivenumtype(ifipinput.getFenum().getEnumid().toString());
			    			  Metadata mta = (Metadata)metadataManager.getUserMetadata(ifipinput.getFenum().getEnumid());			    			  
			    			  tfdi.setDivenumname(mta.getLabel());
			    			  tfdi.setDivenumlevel(ifipinput.getFenum().getLevel());
			    			  tfdi.setFinalChild(ifipinput.getFenum().isFinalChild());
			    			  sessionobj.getOldenumnamemap().put(ifipinput.getFenum().getEnumid(), ifipinput.getFenum().getEnumid());
			    		  }
			    		  if(ifipinput.getcalculate() != null){
			    			  String[] array = ifipinput.getcalculate().getFormula(IXmlNodeName.Name,sessionobj);
			    			  tfdi.setFormula(array[0]);
			    			  tfdi.setDisplayFormat(array[1]);
			    		  }	
			  			  if(Strings.isNotBlank(tfdi.getRefInputName())){
			  				  if(IXmlNodeName.C_sVluae_externalwrite_ahead.equalsIgnoreCase(tfdi.getInputtype())){
			  					outwriteInputNames.add(OperHelper.noNamespace(tfdi.getRefInputName()));
			  				  } else {
			  					  refInputNames.add(OperHelper.noNamespace(tfdi.getRefInputName()));
			  				  }
						  }
			  			  if(IXmlNodeName.C_sVluae_outwrite.equalsIgnoreCase(tfdi.getInputtype())){
			  				  outwriteInputNames.add(OperHelper.noNamespace(tfdi.getName()));
			  			  } else {
				  			  if(IXmlNodeName.C_sVluae_extend.equalsIgnoreCase(tfdi.getInputtype())){
								  IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
								  ISeeyonInputExtend extend = fextendmanager.findByName(tfdi.getExtend());
								  if(extend != null && extend instanceof IInputRelation && !tfdi.getExtend().equals(Constantform.EXTEND_SEARCH_DEE_TASK_LABEL)){
									  refInputNames.add(OperHelper.noNamespace(tfdi.getName()));
								  }
				  			  }
				  			  if((IXmlNodeName.C_sVluae_select.equalsIgnoreCase(tfdi.getInputtype())
										&& !tfdi.isFinalChild() && tfdi.getDivenumlevel() > 1)){
									refInputNames.add(OperHelper.noNamespace(tfdi.getName()));
							  }
			  			  }
		    		  }    		 
		    	  }
				}
		    }
		sessionobj.setFieldInputList(definputlist);
		sessionobj.setRefInputNames(refInputNames);
		sessionobj.setOutwriteInputNames(outwriteInputNames);
		return newtabe;
	}
	
	private List<Operation_BindEvent> converBindEvent(List<InfoPath_ViewBindEventBind> sourceBindEventList){
		List<Operation_BindEvent> targetBindEventList = new ArrayList<Operation_BindEvent>();
		if(CollectionUtils.isNotEmpty(sourceBindEventList)){
			for (InfoPath_ViewBindEventBind eventBind : sourceBindEventList) {
				Operation_BindEvent bindEvent = new Operation_BindEvent();
				bindEvent.setId(eventBind.getId());
				bindEvent.setName(eventBind.getName());
				bindEvent.setOperationType(eventBind.getOperationType());
				bindEvent.setEventTriger(eventBind.getEventTriger());
				bindEvent.setModel(eventBind.getModel());
				bindEvent.setTaskType(eventBind.getTaskType());
				bindEvent.setTaskName(eventBind.getTaskName());
				bindEvent.setTaskId(eventBind.getTaskId());
				targetBindEventList.add(bindEvent);
			}
		}
		return targetBindEventList;
	}
	private List formlistnoInfoPath(SessionObject sessionobj,List newtabe,ISeeyonForm_Application iapp) throws SeeyonFormException{
		List formlst = iapp.getFormList();
		String namespace = sessionobj.getNamespace();
		List formlist = sessionobj.getFormLst();
		for (int j = 0; j < formlst.size(); j++) {
			SeeyonFormImpl sf = (SeeyonFormImpl) formlst.get(j);
			//formlist.clear();	
			for (int i = 0; i < sf.getFviewList().size(); i++) {		
				InfoPath_FormView xsl = (InfoPath_FormView) sf.getFviewList().get(i);
				sessionobj.getViewWidthvalue().put(i, xsl.getValue());
				SeeyonFormImpl see = new SeeyonFormImpl();
				InfoPath_FormView iformview = new InfoPath_FormView(see);
				iformview.setFViewfile(xsl.getFViewfile());		
				iformview.setViewtype(TviewType.vtHtml);
				// 拼formpage所需的viewlst,剩下部分由方法inputdata组装
				List viewlst = new ArrayList();
				viewlst.add(iformview);
				FormPage fp = new FormPage();
				fp.setViewlst(viewlst);
				fp.setEngine("infopath");
				fp.setName(sf.getFormName());
				fp.setFormPageId(String.valueOf(sf.getFormId()));
				formlist.add(fp);
			}
		}
		sessionobj.setFormLst(formlist);
		for (int j = 0; j < formlst.size(); j++) {
			SeeyonFormImpl sf = (SeeyonFormImpl) formlst.get(j);
			List formoperationlst = new ArrayList();
		 	List operfielist = new ArrayList();
			List operationlst = sf.getFOperationList();
			for (int i = 0; i < operationlst.size(); i++) {
				InfoPath_Operation ioper = (InfoPath_Operation) operationlst
						.get(i);
				Operation oper = new Operation();
				ioper.getFOperationFileName();
				oper.setName(ioper.getOperationName());
				oper.setFilename(OperHelper.AddOperationName(ioper.getFOperationFileName()));
				oper.setType(OperHelper.OperationType2str(ioper.getOperationType()));
				oper.setOperationId(String.valueOf(ioper.getId()));
				String substr = ioper.getSubmitlststr();
				String slavestr = ioper.getSlavetablestr();
				String viewbindstr = ioper.getViewbindstr();
				String oninitstr = ioper.getOninitstr();
				if(ioper.getViewBind() !=null){
					String evenxml = ioper.getViewBind().getHighevenxml();
					String initxml = ioper.getViewBind().getInitxml();
					if("null".equals(evenxml) || evenxml ==null)
						evenxml = "";					   
					if("null".equals(initxml) || initxml ==null)
						initxml = "";
					oper.setNewinitxml(initxml);
					oper.setNewhighevenxml(evenxml);
					oper.setBindEventList(converBindEvent(ioper.getViewBind().getfEventBindList()));
					oper.setDeeTakEventList(ioper.getViewBind().getInfoPath_DeeTask());
				}	
				oper.setNewsubmitxml(substr);
				if("null".equals(slavestr) || slavestr ==null)
					slavestr = "";
				if("null".equals(oninitstr) || oninitstr ==null)
					oninitstr = "";
				oper.setNewhighinitxml(oninitstr);
				oper.setNewrepeatxml(slavestr);
				if(slavestr == null || "null".equals(slavestr)){
					slavestr = "";
				}
				if(oninitstr ==null ||"null".equals(oninitstr)){
					oninitstr="";
				}
				if("".equals(viewbindstr) || viewbindstr == null || "null".equals(viewbindstr)){
					viewbindstr = "";
				}
				if(!"".equals(viewbindstr)){
					if(oninitstr.equals("")){
					  oper.setViewbindstr(viewbindstr+slavestr+substr);
					}if(!"".equals(oninitstr)){	
					  oper.setViewbindstr(viewbindstr+oninitstr+slavestr+substr);
					}
				}if(viewbindstr.equals("")){
					if(oninitstr.equals("")){
					   oper.setSubmitlststr(slavestr+substr);
					}if(!"".equals(oninitstr)){	
					   oper.setSubmitlststr(oninitstr+slavestr+substr);
					}
				}
				List operlst = new ArrayList();				
				List inputlst = ioper.getFieldList();
				for (int m = 0; m < newtabe.size(); m++) {
					TableFieldDisplay tfd = (TableFieldDisplay) newtabe.get(m);
					TableFieldDisplay tf = new TableFieldDisplay();
				for (int l = 0; l < inputlst.size(); l++) {
					IOperationField iof = (IOperationField) inputlst.get(l);
						if(iof.getName().equals(tfd.getBindname())){			
								Map map = new HashMap();				
								map.put("bindname" + m, iof.getName());
								map.put("formoper" + m, iof.OperationType2str(iof.getAccess()));
								map.put("formprint" + m, iof.getAllowprint() == true ? "Y": "N");
								map.put("formtransmit" + m,iof.getAllowtransmit() == true ? "Y" : "N");
								tf.setBindname(iof.getName());
								tf.setFormoper(iof.OperationType2str(iof.getAccess()));
								tf.setFormprint(iof.getAllowprint() == true ? "Y": "N");
								tf.setFormtransmit(iof.getAllowtransmit() == true ? "Y" : "N");
								operlst.add(map);
								operfielist.add(tf);	
						}
					}			
				}
				oper.setOperlst(operlst);
				formoperationlst.add(oper);
			}
			FormPage fp = (FormPage)sessionobj.getFormLst().get(j);
			fp.setOperlst(formoperationlst);	
		}
		return newtabe;
	} 

	/**
	 * 
	 * @param iapp
	 * @param xsf
	 *            新生成的xsf
	 * @param tablefieldlst
	 *            原有的tablefieldlst
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 * @throws BusinessException 
	 */
	public SessionObject loadFromDb(ISeeyonForm_Application iapp, InfoPathObject xsf,
			List tablefieldlst,String[] addfield,String id,SessionObject sessionobj) throws SeeyonFormException, DocumentException, BusinessException {
		SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) iapp;
		SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
        SeeyonFormBindImpl  seeformbind= (SeeyonFormBindImpl)sapp.getSeeyonFormBind();
        HashMap flowMap = new LinkedHashMap();
        for(int i =0;i< seeformbind.getFlowTempletList().size();i++){
        	FlowTempletImp flow = (FlowTempletImp)seeformbind.getFlowTempletList().get(i);
        	flowMap.put(flow.getId(), flow);
        }
        if(seeformbind.getFlowTempletList().size()!=0){
	        TemplateObject temobj= new TemplateObject();
	        temobj.setFlowMap(flowMap);
	        sessionobj.setTemplateobj(temobj);
	    }
        // 对查询、统计赋值
        List querylist = sapp.getQueryList();
        List reportlist = sapp.getReportList();
        if(reportlist.size() !=0){
          sessionobj = regroupReportObject(reportlist,sessionobj,id,sessionobj.getFormName());  
        }
        if(querylist.size() !=0){
          sessionobj = regroupQueryObject(querylist,sessionobj,id,sessionobj.getFormName());
        }
        QueryHelper qh = new QueryHelper();
        qh.updateQueryObject(sessionobj.getQueryConditionList(), sessionobj.getDelTablename(), sessionobj.getFieldMap(), sessionobj.getDeletevalue(), sessionobj.getNamespace());
        
        ReportHelper rh = new ReportHelper();
        rh.updateReportObject(sessionobj.getReportConditionList(), sessionobj.getDelTablename(), sessionobj.getFieldMap(), sessionobj.getDeletevalue(), sessionobj.getNamespace());
		
        sessionobj.setSeedatadefine(seedade);
		sessionobj.setData(seedade.getDataDefine());
		sessionobj.setTableFieldList(tablefieldlst);
		 //加防护,看发布的表单数据是否已经存在了.
	    boolean sign = false;
	    for(int i =0;i<sessionobj.getData().getTableLst().size();i++){
	    	FormTable table = (FormTable)sessionobj.getData().getTableLst().get(i);
	    	if(sign == false)
	    	   sign = sessionobj.getData().hasDataRecords(sapp.getSessionFactory(),table.getName());
	    }
	    sessionobj.setIsdatavalue(sign);
		IFormResoureProvider ifrp = sapp.getFResourceProvider();
		// 利用传进来的新的xsf进行组织对象
		//InfoPath_xsd xsd = xsf.getIntoxsd();
		
		// 利用DataDefine组出tablefieldlst
		List newtablist = new ArrayList();
		List newtabe = new ArrayList();
		newtabe = fielddata(newtablist,newtabe,sessionobj,addfield,tablefieldlst,seedade);
		
		//组织Formlst及operlist
		newtabe = formlist(sessionobj,addfield,newtabe,tablefieldlst,iapp,xsf);
		//如果新上传的infopath中含有新视图
		HashMap formmap = new HashMap();
		for(int i =0;i<sessionobj.getFormLst().size();i++){
              formmap.put(sessionobj.getFormLst().get(i).getName(), sessionobj.getFormLst().get(i).getName());
		}
		if(sessionobj.getFormLst().size() < xsf.getViewList().size()){
			for(int i=0;i<xsf.getViewList().size();i++){
				InfoPath_xsl xsl = (InfoPath_xsl)xsf.getViewList().get(i);
				if(formmap.get(xsf.getViewFileCaption(xsl.getFileName())) == null){
					sessionobj.getViewWidthvalue().put(i, xsl.getValue());
					SeeyonFormImpl see = new SeeyonFormImpl();
					InfoPath_FormView iformview = new InfoPath_FormView(see);
					iformview.setFViewfile(xsl.getFileName());
					iformview.setViewtype(TviewType.vtHtml);
					List viewlst = new ArrayList();
					//拼formpage所需的viewlst,剩下部分由方法inputdata组装
					viewlst.add(iformview);
					FormPage fp = new FormPage();
					fp.setName(xsf.getViewFileCaption(xsl.getFileName()));
					fp.setViewlst(viewlst);
					fp.setEngine("infopath");
					Long formpageid = UUIDLong.longUUID();
					fp.setFormPageId(formpageid.toString());
					OperHelper.addDefaultOperLst(fp, i,sessionobj);
					for(int j=0;j<fp.getOperlst().size();j++){
						Operation operation = (Operation)fp.getOperlst().get(j);
						if(operation.getOperlst() == null){
							operation.setOperlst(OperHelper.getFixOperlst(newtabe,operation.getType()));
						}				
					}
					sessionobj.getFormLst().add(fp);
				}				
			}
		}
		// 组织defaultInput
		newtabe = defaultinput(sessionobj,addfield,newtabe,tablefieldlst,sapp,ifrp);
		sessionobj = formqueryenum(sessionobj);
		sessionobj.setTableFieldList(newtabe);
		sessionobj.setTablefieldsize(newtabe.size());
		return sessionobj;
	}

	private String AddTableName(String aName) {
		int findex = aName.indexOf("↗");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);
	}

	private String AddFieldName(String aName) {
			int findex = aName.indexOf("↗");
			if (findex < 0)
				return aName;
			return aName.substring(findex+1);
		}
	public  String newtableName(String aName){
		int findex = aName.indexOf("/");
		if (findex < 0)
			return aName;
		return aName.substring(0, findex);		
	}
	public  String oldtableName(String aName){
		int findex = aName.indexOf("/");
		int findex1 = aName.indexOf("↗");
		if (findex < 0)
			return aName;
		return aName.substring(findex+1, findex1);		
	}
	private synchronized List fielddata(List newtablist,List newtabe,SessionObject sessionobj,String[] addfield,List tablefieldlst,SeeyonDataDefine newseedate) throws DataDefineException, InfoPathParseException{
		List<FormTable> tablelist = sessionobj.getData().getTableLst();
		List<FormTable> tablelst = new ArrayList();
		FormTable newformtable  = null;
		for (FormTable formtable : tablelist) {
			newformtable = new FormTable(null);
			try {
				BeanUtils.copyProperties(newformtable,formtable);
			} catch (IllegalAccessException e) {
				throw new InfoPathParseException(
						InfoPathParseException.C_iParseErrode_IllegalAccess);
			} catch (InvocationTargetException e) {
				throw new InfoPathParseException(
						InfoPathParseException.C_iParseErrode_InvocationTarget);
			}
			newformtable.setFName(formtable.getName());
			tablelst.add(newformtable);

		}
		List oldtablelst = new ArrayList();
		String namespace = sessionobj.getNamespace();
		String table = null;
		String id =null;
		String groupname = null;
		String tablename = null;
		List oldtemtalist =new ArrayList();
		List newtemtalist = new ArrayList();
		String temtable = null;
		int newtablenum = 0;
		newtablenum = sessionobj.getTableFieldList().size();
		for (int i = 0; i < tablelst.size(); i++) {		
			FormTable ft = (FormTable) tablelst.get(i);
			temtable =ft.getName();
			oldtablelst.add(temtable);
			//通过数据库存的子表名找到infopath中的子节点名称
			InfoPath_DataSource datasource = (InfoPath_DataSource)newseedate.getDataSource();
			IDataGroup name =datasource.findGroupByTableName(ft.getName());
			if(i==0){
				table =ft.getName();
				oldtemtalist.add(table);
			}		
			if(name !=null){
			   groupname = name.getGroupName();
			   tablename = name.getTableName();
			   oldtemtalist.add(groupname);
			}
			List fieldlst = ft.getFieldLst();
			for (int j = 0; j < fieldlst.size(); j++) {
				FormField ff = (FormField) fieldlst.get(j);
				boolean modifiedName = false;
				for (int m = 0; m < tablefieldlst.size(); m++) {
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(m);
					TableFieldDisplay tfdisplay = new TableFieldDisplay();				
					if (tfd.getName().equals(ff.getDisplay()) && ff.getId().length()>3 && ff.isIs_primary() !=true) {
						if(tfd.getEditname() == null){
							tfdisplay.setName(tfd.getName());
							tfdisplay.setBindname(namespace + ff.getDisplay());
						}
					   if(tfd.getEditname() !=null){
						   tfdisplay.setName(tfd.getEditname());
						   tfdisplay.setBindname(namespace + tfd.getEditname());
						   tfdisplay.setEditbindname(namespace + tfd.getEditname());
						   ff.setDisplay(tfd.getEditname());
						   modifiedName = true;
					   }
					   tfdisplay.setFieldtype(ff.getFieldtype());
					   //若长度有小数位的用","分隔,分别存到length和digits中
					   if (ff.getFieldlength() != null && !"".equals(ff.getFieldlength()) && !"null".equals(ff.getFieldlength())){						 
						   if(ff.getFieldlength().indexOf(",") == -1){
							   tfdisplay.setLength(ff.getFieldlength());  
						   }else{
							   OperHelper oper = new OperHelper();
							   String length = ff.getFieldlength();
							   tfdisplay.setLength(oper.splitFieldlength(length));
							   tfdisplay.setDigits(oper.splitFieldscale(length));
						   }
					   }else{  
						   tfdisplay.setLength(""); 
				      }
					   if(groupname==null){
					      tfdisplay.setTablename(ft.getName());				      
					   }else{
						  tfdisplay.setTablename(tablename);
						  tfdisplay.setEditablename(groupname);
					   }
					   tfdisplay.setTablenumber(Long.valueOf(ft.getName().substring(
								ft.getName().length() - 4)));
					   tfdisplay.setFieldname(ff.getName());
					   tfdisplay.setId(ff.getId());
					   id = ff.getId();
					   tfdisplay.setIsnull(ff.isIs_null() == true ? "N" : "Y");	
					   newtablist.add(tfdisplay);	
					   if(modifiedName){
					       break;
					   }
					}
				}		
			}
		}		
		List oldtemptablist = new ArrayList();
		for(int i=0;i<oldtemtalist.size();i++){
			String oldtablename = (String)oldtemtalist.get(i);
			if(sessionobj.getEdittableMap().size()!=0){
				if(sessionobj.getEdittableMap().get(oldtemtalist.get(i)) !=null){
					oldtablename = (String) sessionobj.getEdittableMap().get(oldtemtalist.get(i));
				}
			}
			oldtemptablist.add(oldtablename);
		}
		//新上传字段
		List temnewtable =new ArrayList();
		if(addfield !=null){
		 for(int i=0;i<addfield.length;i++){
	        	String name = AddTableName(addfield[i]);      	
	        	temnewtable.add(name);
	     }
		 int addid=0;
		 int matchsid = 0;
	     String addtablename=null;
	     String matchsfirst=null;
         //	把新增表的字段属性存到newtablist中
	     int tablenum = 0;
		 for(int a=0;a<temnewtable.size();a++){
				Object newcreate1 = temnewtable.get(a);
				Matchdata obj = new Matchdata();
				if(oldtemptablist.contains(newcreate1)== false){
					 String matchs= (String) newcreate1;			 
                     matchsid++;
                     if(matchsid == 1){
                    	 matchsfirst = matchs;
                     }
					 TableFieldDisplay tfdisplay = new TableFieldDisplay();
			        	tfdisplay.setName(AddFieldName(addfield[a]));
			        	if(!matchsfirst.equals(matchs) || matchsid ==1){
			        		String tableNumber = incrementAndGetBiggestValueSign();
      			        	tablenum = Integer.parseInt(tableNumber);   
  	   			            if(matchsid !=1){
  	   			            	matchsfirst = matchs;
  	   			            }
			        	}
                        if(id == null){
							 id="0";
						}			 			        	 
			        	tfdisplay.setTablename(IPagePublicParam.tableson + CreateTableNumber.createNormalNumber(tablenum));
			        	tfdisplay.setEditablename(matchs);
			        	tfdisplay.setBindname(namespace + AddFieldName(addfield[a]));		        	
			        	addid = Integer.parseInt(CreateTableNumber.createNormalNumber(newtablenum+matchsid));
				        addtablename = "field"+CreateTableNumber.createNormalNumber(newtablenum+matchsid);
				        tfdisplay.setId(CreateTableNumber.createNormalNumber(newtablenum+matchsid));
				        tfdisplay.setFieldname("field"+CreateTableNumber.createNormalNumber(newtablenum+matchsid));		        	        	
				        tfdisplay.setLength("255");		//修改新增字段默认长度为255，起因为与HR字段长度匹配。
			        	tfdisplay.setIsnull("N");
			        	tfdisplay.setFieldtype("VARCHAR");
			        	tfdisplay.setAddfieldsign("true");
			        	newtablist.add(tfdisplay);
				}			
			}
		//去掉新上传文件中新增表中的重复的表名
		String[] table1=null;
		StringBuffer sb = new StringBuffer();
        for(int i=0;i<addfield.length;i++){
        	String name = AddTableName(addfield[i]);
        	sb.append(name);
        	if(i !=addfield.length-1){
        	sb.append(",");
        	}
        }
        String tableapp=sb.toString();
        table1 = tableapp.split(",");
	  	  Set   s=   new   java.util.HashSet();   
		  for   (int   i=0   ;i<table1.length;i++){   
		  s.add(table1[i]);   
		  }   
		  table1   =   new   String[s.size()];   
		  s.toArray(table1);   
		  for   (int   i=0;i<table1.length;i++){ 
			  newtemtalist.add(table1[i]);
		  }
    	List newlist = new ArrayList ();
        //新上传的表和旧表相比，若有新的表则装配tablelst的属性和fieldlst 	
    	for(int a=0;a<newtemtalist.size();a++){
    		String firsttablename = null;
			Object newcreate1 = newtemtalist.get(a);
			Matchdata obj = new Matchdata();
			if(oldtemptablist.contains(newcreate1)== false){
				 String matchs= (String) newcreate1;
				 String tablegroup = null;
				 String addtable = null;
				 String ownertable = null;
				 String ownerfield = null;
				 for(int i =0;i<tablelst.size();i++){
					 FormTable ftable = (FormTable)tablelst.get(i);
					 ownertable = ftable.getOnwertable();
					 ownerfield = ftable.getOnwerfield();
					 if(i==0){
						 firsttablename = ftable.getName();
					 }
				 }
				 FormTable ft = new FormTable(null);
				 List fielst = ft.getFieldLst();				 
				 for(int v=0;v<newtablist.size();v++){ 
					 TableFieldDisplay tfdi =(TableFieldDisplay)newtablist.get(v);
					 if(tfdi.getEditablename()!=null){
					 if(tfdi.getEditablename().equals(matchs)){
						 FormField ffield = new FormField(ft);
						 ffield.setDisplay(tfdi.getName());
						 ffield.setFieldlength(tfdi.getLength());
						 ffield.setFieldtype(tfdi.getFieldtype());
						 ffield.setFName(tfdi.getFieldname());
						 ffield.setId(tfdi.getId());
						 ffield.setIs_null(tfdi.getIsnull()== "Y" ? false : true);
						 ffield.setIs_primary(false);
						 fielst.add(ffield);
						 tablegroup = tfdi.getTablename();
						 addtable = tfdi.getEditablename();
					 }
				 }
				 }
				 ft.setFName(tablegroup);
				 ft.setTabletype("slave");
				 ft.setOnwerfield(ownerfield);
				 ft.setOnwertable(ownertable);
				 ft.setId(String.valueOf(tablelst.size()+1));
				 tablelst.add(ft);
				 sessionobj.getAddtablename().add(addtable+"↗"+tablegroup);
			}			
		}
    	//把存在的原表新增字段存到newtablist和tablelst中
    	int oldaddid = 0;
    	newtablenum = newtablist.size();
		for (int i = 0; i < tablelst.size(); i++) {
			FormTable ft = (FormTable) tablelst.get(i);
			String newtable =ft.getName();
			InfoPath_DataSource datasource = (InfoPath_DataSource)newseedate.getDataSource();
			IDataGroup name =datasource.findGroupByTableName(ft.getName());
			groupname = "";
			if(name !=null){
			   groupname = name.getGroupName();
			   if(sessionobj.getEdittableMap().size()!=0){
					if(sessionobj.getEdittableMap().get(groupname) !=null){
						groupname = (String) sessionobj.getEdittableMap().get(groupname);
					}
				}
			   newtable = name.getTableName();
			}
			if(i==0){
				table =ft.getName();
				groupname=table;
			}			
			List fieldlst = ft.getFieldLst();		
			 if(addfield !=null){
				 for(int a=0;a<addfield.length ; a++){
					 if(AddTableName(addfield[a]).equals(groupname)){
						 FormField ff = new FormField(ft);
						 ff.setDisplay(AddFieldName(addfield[a]));						 
						 oldaddid++;
						 //ff.setFieldlength("20");
						 ff.setFieldlength("255");	//修改新增字段默认长度为255，起因为与HR字段长度匹配。
						 ff.setFieldtype("VARCHAR");
						 if(addtablename ==null){
							 ff.setId(CreateTableNumber.createNormalNumber(newtablenum+oldaddid));
							 ff.setFName("field"+CreateTableNumber.createNormalNumber(newtablenum+oldaddid));
						 }else{
							 ff.setId(CreateTableNumber.createNormalNumber(addid+oldaddid));
							 ff.setFName("field"+CreateTableNumber.createNormalNumber(addid+oldaddid)); 
						 }						 
						 ff.setIs_null(true);
						 ff.setIs_primary(false);
						 TableFieldDisplay tfdisplay = new TableFieldDisplay();
				        	tfdisplay.setName(AddFieldName(addfield[a]));
				        	tfdisplay.setTablename(newtable);
				        	tfdisplay.setEditablename(groupname);
				        	tfdisplay.setBindname(namespace + AddFieldName(addfield[a]));
				        	if(addtablename ==null){
				        		tfdisplay.setId(CreateTableNumber.createNormalNumber(newtablenum+oldaddid));
					        	tfdisplay.setFieldname("field"+CreateTableNumber.createNormalNumber(newtablenum+oldaddid));
				        	}else{
				        		tfdisplay.setId(CreateTableNumber.createNormalNumber(addid+oldaddid));
					        	tfdisplay.setFieldname("field"+CreateTableNumber.createNormalNumber(addid+oldaddid));
				        	}	        	
				        	//tfdisplay.setLength("20");
				        	tfdisplay.setLength("255");	//修改新增字段默认长度为255，起因为与HR字段长度匹配。
				        	tfdisplay.setIsnull("N");
				        	tfdisplay.setFieldtype("VARCHAR");
				        	tfdisplay.setAddfieldsign("true");
				        	newtablist.add(tfdisplay);	
					 }				
					 }
				 }
		}
	}
		newtabe = loadnewTableField(newtablist,table,newtabe,sessionobj,tablelst);		
		sessionobj = deletablename(oldtablelst,sessionobj,newseedate,tablelst);
	   return newtabe;
	}
    
	private SessionObject deletablename(List oldtablelst,SessionObject sessionobj,SeeyonDataDefine seedade,List<FormTable> tablelst){
		List newtablename = new ArrayList();
		for(int i = 0 ; i<tablelst.size(); i++){
			FormTable ft = tablelst.get(i);
			newtablename.add(ft.getName());
		}
		for(int j=0;j<oldtablelst.size();j++){
			Object deltablename = oldtablelst.get(j); 
			if(newtablename.contains(deltablename) == false){            
			    String matchs= (String) deltablename;
                //通过数据库存的子表名找到infopath中的子节点名称
				InfoPath_DataSource datasource = (InfoPath_DataSource)seedade.getDataSource();
				IDataGroup name =datasource.findGroupByTableName(matchs);
	            sessionobj.getDelTablename().add(name.getGroupName()+"↗"+matchs);
			}
		}		
		return sessionobj;
	}
	
	//重新装配tablefieldlst，保证匹配数据中主表字段在第一位
	private List loadnewTableField(List newtablist,String table,List newtabe,SessionObject sessionobj,List<FormTable> tablelst){
		int sign=0;
		String tablename=null;
		DataDefine datadefine = sessionobj.getData();
		for(int j=0;j<tablelst.size();j++){
			FormTable ftable = tablelst.get(j);
			int tablesign = 0;
			if(j==0){
				tablename = ftable.getName();
			}
			 for(int i=0;i<newtablist.size();i++){
				 TableFieldDisplay tf = new TableFieldDisplay();
				 TableFieldDisplay tfdi = (TableFieldDisplay)newtablist.get(i);
				 sign = i+1;
				 if(tfdi.getTablename().equals(ftable.getName())){
					 tablesign++;
					 tf.setName(tfdi.getName());
					 tf.setTablename(tfdi.getTablename());
					 tf.setEditablename(tfdi.getEditablename());
					 tf.setBindname(tfdi.getBindname());
					 tf.setFieldname(tfdi.getFieldname());
					 tf.setId(tfdi.getId());
					 tf.setFieldtype(tfdi.getFieldtype());
					 tf.setLength(tfdi.getLength());
					 if(tfdi.getDigits() !=null){
					 tf.setDigits(tfdi.getDigits());
					 }
					 tf.setIsnull(tfdi.getIsnull());
					 tf.setTablenumber(tfdi.getTablenumber());
					 tf.setAddfieldsign(tfdi.getAddfieldsign());
					 newtabe.add(tf);
				 }if(sign == newtablist.size()){
					 if(tablesign ==0){
						 datadefine.getTableLst().remove(j);
						 datadefine.getSlaveTableList(tablename).remove(j-1);					 
					 }
				 }
			 }	
		}
		sessionobj.setData(datadefine);
		return newtabe;
	} 
	
	private List defaultinput(SessionObject sessionobj,String[] addfield,List newtabe,List tablefieldlst,SeeyonForm_ApplicationImpl sapp,IFormResoureProvider ifrp) throws SeeyonFormException, DocumentException{
		MetadataManager metadataManager  = (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		InfoPath_Inputtypedefine inpointy = FormHelper.getInfoPathInputtypedefine(sapp,ifrp);
//		InfoPath_Inputtypedefine inpointy = new InfoPath_Inputtypedefine(sapp);	
//		inpointy.getInputList().clear();
//		String defauliname = sapp.getFormProperty("defaultInput.xml",sapp.C_iPropertyType_UserDefineXML);
//		inpointy.loadFromXml(DocumentHelper.parseText(ifrp.loadResource(defauliname)).getRootElement());
		List definputlist = new ArrayList();
		String namespace = sessionobj.getNamespace();
		for (int i = 0; i < inpointy.getInputList().size(); i++) {
			if(inpointy.getInputList().get(i) instanceof TIP_InputText) {
				TIP_InputText tiptext = (TIP_InputText)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tiptext.getFIpcal());
					ifipinput.setFormatType(tiptext.getFormatType()) ;
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);		
					if (tfd.getBindname().equals(tiptext.getDataAreaName())) {
						ifipinput.setUnique(tiptext.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tiptext.getDataAreaName());
							ifipinput.setFInputType(tiptext.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptext.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tiptext.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptext.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputTextArea) {
				TIP_InputTextArea tiptextarea = (TIP_InputTextArea)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tiptextarea.getFIpcal());
					ifipinput.setFormatType(tiptextarea.getFormatType()) ;
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tiptextarea.getDataAreaName())) {
//						ifipinput.setUnique(tiptextarea.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tiptextarea.getDataAreaName());
							ifipinput.setFInputType(tiptextarea.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptextarea.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tiptextarea.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiptextarea.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputTLable) {
				TIP_InputTLable tiplable = (TIP_InputTLable)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tiplable.getFIpcal());
					ifipinput.setFormatType(tiplable.getFormatType()) ;
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tiplable.getDataAreaName())) {
						ifipinput.setUnique(tiplable.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tiplable.getDataAreaName());
							ifipinput.setFInputType(tiplable.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiplable.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tiplable.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tiplable.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputHandwrite) {
				TIP_InputHandwrite tiplable = (TIP_InputHandwrite)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tiplable.getDataAreaName())) {
//						ifipinput.setUnique(tiplable.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tiplable.getDataAreaName());
							ifipinput.setFInputType(tiplable.getInputType());
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tiplable.getInputType());
							definputlist.add(ifipinput);
						}
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputRadio ){
				TIP_InputRadio tipradio = (TIP_InputRadio)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tipradio.getFIpcal());
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipradio.getDataAreaName())) {
						ifipinput.setUnique(tipradio.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tipradio.getDataAreaName());
							ifipinput.setFInputType(tipradio.getInputType());							
							InfoPath_Enum ienum = new InfoPath_Enum();
							ienum.setFname(tipradio.getFEnumName());
							ienum.setEnumid(tipradio.getFEnumId());
							ienum.setAppsort(tipradio.getFenumsort());
							ienum.setEnumtype(tipradio.getFenumtype());		
							ifipinput.setFenum(ienum);											
							ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipradio.getStageEnumXml()));	
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipradio.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tipradio.getInputType());							
							InfoPath_Enum ienum = new InfoPath_Enum();
							ienum.setFname(tipradio.getFEnumName());
							ienum.setEnumid(tipradio.getFEnumId());
							ienum.setAppsort(tipradio.getFenumsort());
							ienum.setEnumtype(tipradio.getFenumtype());
							ifipinput.setFenum(ienum);										
							ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipradio.getStageEnumXml()));	
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipradio.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
						
					}
				}
			}if(inpointy.getInputList().get(i) instanceof TIP_InputSelect ){
				TIP_InputSelect tipselect = (TIP_InputSelect)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tipselect.getFIpcal());
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipselect.getDataAreaName())) {
						ifipinput.setUnique(tipselect.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tipselect.getDataAreaName());
							ifipinput.setFInputType(tipselect.getInputType());							
							InfoPath_Enum ienum = new InfoPath_Enum();
							ienum.setFname(tipselect.getFEnumName());
							ienum.setEnumid(tipselect.getFEnumId());
							ienum.setAppsort(tipselect.getFenumsort());
							ienum.setEnumtype(tipselect.getFenumtype());		
							ienum.setFinalChild(tipselect.isFinChild());
							ienum.setLevel(tipselect.getLevel());
							ifipinput.setFenum(ienum);											
							ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipselect.getStageEnumXml()));	
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipselect.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tipselect.getInputType());							
							InfoPath_Enum ienum = new InfoPath_Enum();
							ienum.setFname(tipselect.getFEnumName());
							ienum.setEnumid(tipselect.getFEnumId());
							ienum.setAppsort(tipselect.getFenumsort());
							ienum.setEnumtype(tipselect.getFenumtype());
							ienum.setFinalChild(tipselect.isFinChild());
							ienum.setLevel(tipselect.getLevel());
							ifipinput.setFenum(ienum);				
							ifipinput.setStageEnumXml(OperHelper.parseQuotationMark(tipselect.getStageEnumXml()));	
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipselect.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
					}
				}
			}if(inpointy.getInputList().get(i) instanceof TIP_InputExtend ){
				TIP_InputExtend tipextend = (TIP_InputExtend)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tipextend.getFIpcal());
					ifipinput.setFormatType(tipextend.getFormatType()) ;
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipextend.getDataAreaName())) {
						ifipinput.setUnique(tipextend.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tipextend.getDataAreaName());
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
						}	
						ifipinput.setRefParams(tipextend.getRefParams());
						ifipinput.setRefInputAtt(tipextend.getRefInputAtt());
						ifipinput.setDisplayRelated(tipextend.isDisplayRelated());
						ifipinput.setDisplayBaseForm(tipextend.isDisplayBaseForm());
						ifipinput.setFInputType(tipextend.getInputType());
						ifipinput.setStageRSXml(tipextend.getStageRSXml());
						ifipinput.setSelectType(tipextend.getSelectType());
						ifipinput.setRelationConditionId(tipextend.getRelationConditionId());
						ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipextend.getStageCalculateXml()));
						ifipinput.setDeeTask(tipextend.getDeeTask());
						definputlist.add(ifipinput);
					}
				}
			}if(inpointy.getInputList().get(i) instanceof TIP_InputCheckbox) {
				TIP_InputCheckbox tipcheckbox = (TIP_InputCheckbox)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setcalculate(tipcheckbox.getFIpcal());
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipcheckbox.getDataAreaName())) {
//						ifipinput.setUnique(tipcheckbox.isUnique());
						if(tfd.getEditname() ==null){
							ifipinput.setName(tipcheckbox.getDataAreaName());
							ifipinput.setFInputType(tipcheckbox.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipcheckbox.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
							ifipinput.setFInputType(tipcheckbox.getInputType());
							ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipcheckbox.getStageCalculateXml()));
							definputlist.add(ifipinput);
						}
					}
				}
			}if(inpointy.getInputList().get(i) instanceof TIP_InputRelation ){
				TIP_InputRelation tipRelation = (TIP_InputRelation)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
					ifipinput.setFcalculate(tipRelation.getFIpcal());
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipRelation.getDataAreaName())) {
						ifipinput.setUnique(tipRelation.isUnique());
						ifipinput.setName(tipRelation.getDataAreaName());
						ifipinput.setFInputType(tipRelation.getInputType());
						ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipRelation.getStageCalculateXml()));
						ifipinput.setRefInputName(tipRelation.getRefInputName());
						ifipinput.setRefInputType(tipRelation.getRefInputType());
						ifipinput.setRefInputAtt(tipRelation.getRefInputAtt());
						if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
						}
						definputlist.add(ifipinput);
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputOutwrite ){
				TIP_InputOutwrite tipOutWrite = (TIP_InputOutwrite)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipOutWrite.getDataAreaName())) {
						InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
						ifipinput.setFcalculate(tipOutWrite.getFIpcal());
						ifipinput.setName(tipOutWrite.getDataAreaName());
						ifipinput.setFInputType(tipOutWrite.getInputType());
						ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(tipOutWrite.getStageCalculateXml()));
						ifipinput.setFormatType(tipOutWrite.getFormatType()) ;
						ifipinput.setUnique(tipOutWrite.isUnique());
						if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
						}
						definputlist.add(ifipinput);
					}
				}
			}
			if(inpointy.getInputList().get(i) instanceof TIP_InputExternalwriteAhead){
				TIP_InputExternalwriteAhead tipExternalwriteAhead = (TIP_InputExternalwriteAhead)inpointy.getInputList().get(i);
				for (int j = 0; j < tablefieldlst.size(); j++) {
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(j);
					if (tfd.getBindname().equals(tipExternalwriteAhead.getDataAreaName())) {
						InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
						ifipinput.setName(tipExternalwriteAhead.getDataAreaName());
						ifipinput.setFInputType(tipExternalwriteAhead.getInputType());
						ifipinput.setFormatType(tipExternalwriteAhead.getFormatType());
						ifipinput.setUnique(tipExternalwriteAhead.isUnique());
						ifipinput.setRefInputName(tipExternalwriteAhead.getRefInputName());
						if(tfd.getEditname() !=null){
							ifipinput.setName(namespace+tfd.getEditname());
						}
						definputlist.add(ifipinput);
					}
				}
			}
		}
		
		//添加更新关联属性名称字段
		FormHelper.updateCurFormRelationInfo(sessionobj,definputlist);
		
         // 新上传字段
         if(addfield !=null){
		 for(int i=0;i<addfield.length ; i++){
			 InfoPath_FieldInput ifipinput = new InfoPath_FieldInput();
			      ifipinput.setName(namespace+AddFieldName(addfield[i]));	
			      ifipinput.setFInputType(TFieldInputType.fitText);
		          definputlist.add(ifipinput);   		 	
	        }
         }
		 //把definputlist中的数据装配到newtabe中。
         Set<String> refInputNames = new LinkedHashSet<String>();
         Set<String> outwriteInputNames = new LinkedHashSet<String>();
 		 Map<String,Map<String,String>> refInputAtts = new LinkedHashMap<String, Map<String,String>>();
		 for(int i = 0;i<newtabe.size();i++){
			TableFieldDisplay tfdi = (TableFieldDisplay)newtabe.get(i);
			for(int a = 0;a<definputlist.size();a++){
				InfoPath_FieldInput ifipinput = (InfoPath_FieldInput)definputlist.get(a);
	    	    if(tfdi.getBindname().equals(ifipinput.getName())){
	    		  if(ifipinput.getFInputType() !=null){
		    		  tfdi.setInputtype(ifipinput.OperationType2str(ifipinput.getFInputType()));
		    		  tfdi.setCompute(ifipinput.getStageCalculateXml());
		    		  tfdi.setEnumtype(ifipinput.getStageEnumXml());
		    		  tfdi.setExtend(ifipinput.getStageRSXml());
		    		  tfdi.setFormatType(ifipinput.getFormatType()) ;
		    		  tfdi.setUnique(ifipinput.isUnique());
		    		  tfdi.setRefInputName(ifipinput.getRefInputName());
		    		  tfdi.setRefInputType(ifipinput.getRefInputType());
		    		  tfdi.setRefParams(ifipinput.getRefParams());
		    		  tfdi.setSelectType(ifipinput.getSelectType());
		    		  tfdi.setRelationConditionId(ifipinput.getRelationConditionId());
		    		  tfdi.setRefInputAtt(ifipinput.getRefInputAtt());
		    		  tfdi.setDisplayRelated(ifipinput.isDisplayRelated());
		    		  tfdi.setDisplayBaseForm(ifipinput.isDisplayBaseForm());
		    		  tfdi.setDeeTask(ifipinput.getDeeTask());
		    		  if(ifipinput.getFenum() != null){
		    			  tfdi.setDivenumtype(ifipinput.getFenum().getEnumid().toString());
		    			  Metadata mta = (Metadata)metadataManager.getUserMetadata(ifipinput.getFenum().getEnumid());			    			  
		    			  tfdi.setDivenumname(mta.getLabel());
		    			  tfdi.setDivenumlevel(ifipinput.getFenum().getLevel());
		    			  tfdi.setFinalChild(ifipinput.getFenum().isFinalChild());
		    			  sessionobj.getOldenumnamemap().put(ifipinput.getFenum().getEnumid(),ifipinput.getFenum().getEnumid());
		    		  }
		    		  InfoPath_Calculate icc = ifipinput.getcalculate();
		    		  if(icc != null){
		    			  String[] array = ifipinput.getcalculate().getFormula(IXmlNodeName.Name,sessionobj);
		    			  tfdi.setFormula(array[0]);
		    			  tfdi.setDisplayFormat(array[1]);
		    			  if(!"edit".equals(sessionobj.getXsnpath()) && !"".equals(sessionobj.getNoequalvalue())){
		    				  tfdi.setCompute(OperHelper.parseQuotationMark(icc.createxml(sessionobj,1)));
			    			  ifipinput.setStageCalculateXml(OperHelper.parseQuotationMark(icc.createxml(sessionobj,1)));
		  				  }	 
		    		  }
		  			  if(Strings.isNotBlank(tfdi.getRefInputName())){
		  				  if(IXmlNodeName.C_sVluae_externalwrite_ahead.equalsIgnoreCase(tfdi.getInputtype())){
		  					  outwriteInputNames.add(OperHelper.noNamespace(tfdi.getRefInputName()));
		  				  } else {
		  					  refInputNames.add(OperHelper.noNamespace(tfdi.getRefInputName()));
		  				  }
					  }
		  			  if(IXmlNodeName.C_sVluae_outwrite.equalsIgnoreCase(tfdi.getInputtype())){
		  				  outwriteInputNames.add(OperHelper.noNamespace(tfdi.getName()));
		  			  } else {
			  			  if(IXmlNodeName.C_sVluae_extend.equalsIgnoreCase(tfdi.getInputtype())){
							  IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
							  ISeeyonInputExtend extend = fextendmanager.findByName(tfdi.getExtend());
							  if(extend != null && extend instanceof IInputRelation){
								  refInputNames.add(OperHelper.noNamespace(tfdi.getName()));
							  }
			  			  }
			  			  if((IXmlNodeName.C_sVluae_select.equalsIgnoreCase(tfdi.getInputtype())
									&& !tfdi.isFinalChild() && tfdi.getDivenumlevel() > 1)){
								refInputNames.add(OperHelper.noNamespace(tfdi.getName()));
						  }
		  			  }
	    		 }    		 
	    	  }
			}
	    }
		sessionobj.setRefInputNames(refInputNames);
		sessionobj.setFieldInputList(definputlist);
		sessionobj.setOutwriteInputNames(outwriteInputNames);
		return newtabe;
	}
	
	private List formlist(SessionObject sessionobj,String[] addfield,List newtabe,List tablefieldlst,ISeeyonForm_Application iapp, InfoPathObject xsf) throws SeeyonFormException{
		List formlst = iapp.getFormList();
		String namespace = sessionobj.getNamespace();
		List formlist = sessionobj.getFormLst();
		HashMap formmap = new HashMap();
		for(int i = 0; i<formlist.size();i++){
			if( formlist.get(i) instanceof SeeyonFormImpl){
				SeeyonFormImpl sf = (SeeyonFormImpl) formlist.get(i);
				formmap.put(sf.getFormName(), sf.getFormName());
			}else if(formlist.get(i) instanceof FormPage){
				FormPage newseeform = (FormPage) formlist.get(i);
				formmap.put(newseeform.getName(), newseeform.getName());
			}
			
		}
		for (int j = 0; j < formlst.size(); j++) {
			SeeyonFormImpl sf = (SeeyonFormImpl) formlst.get(j);
			for (int i = 0; i < sf.getFviewList().size(); i++) {		
				InfoPath_FormView xsl = (InfoPath_FormView) sf.getFviewList().get(i);		    
				sessionobj.getViewWidthvalue().put(i, xsl.getValue());
				SeeyonFormImpl see = new SeeyonFormImpl();
				InfoPath_FormView iformview = new InfoPath_FormView(see);
				iformview.setFViewfile(xsl.getFViewfile());				
				iformview.setViewtype(TviewType.vtHtml);
				// 拼formpage所需的viewlst,剩下部分由方法inputdata组装
				List viewlst = new ArrayList();
				viewlst.add(iformview);
				FormPage fp = new FormPage();
				fp.setViewlst(viewlst);
				fp.setEngine("infopath");
				fp.setName(sf.getFormName());
				fp.setFormPageId(String.valueOf(sf.getFormId()));
				if(formmap.get(sf.getFormName()) ==null)
				  formlist.add(fp);
		   }
		}
		sessionobj.setFormLst(formlist);
		for (int j = 0; j < formlst.size(); j++) {
			SeeyonFormImpl sf = (SeeyonFormImpl) formlst.get(j);
			List formoperationlst = new ArrayList();	
			List operationlst = sf.getFOperationList();
			for (int i = 0; i < operationlst.size(); i++) {
				InfoPath_Operation ioper = (InfoPath_Operation) operationlst
						.get(i);
				Operation oper = new Operation();
				List operfielist = new ArrayList();
				ioper.getFOperationFileName();
				oper.setName(ioper.getOperationName());
				oper.setFilename(OperHelper.AddOperationName(ioper.getFOperationFileName()));
				oper.setType(OperHelper.OperationType2str(ioper.getOperationType()));
				oper.setOperationId(String.valueOf(ioper.getId()));
				String substr = ioper.getSubmitlststr();
				String slavestr = ioper.getSlavetablestr();
				String viewbindstr = ioper.getViewbindstr();
				String oninitstr = ioper.getOninitstr();

				if(ioper.getViewBind() !=null){
					String evenxml = ioper.getViewBind().getHighevenxml();
					String initxml = ioper.getViewBind().getInitxml();
					if("null".equals(evenxml) || evenxml ==null)
						evenxml = "";					   
					if("null".equals(initxml) || initxml ==null)
						initxml = "";
					oper.setNewinitxml(initxml);
					oper.setNewhighevenxml(evenxml);
					oper.setBindEventList(converBindEvent(ioper.getViewBind().getfEventBindList()));
					oper.setDeeTakEventList(ioper.getViewBind().getInfoPath_DeeTask());
				}	
				oper.setNewsubmitxml(substr);
				if("null".equals(slavestr) || slavestr ==null)
					slavestr = "";
				if("null".equals(oninitstr) || oninitstr ==null)
					oninitstr = "";
				oper.setNewhighinitxml(oninitstr);
				oper.setNewrepeatxml(slavestr);
				
				if(slavestr == null || "null".equals(slavestr)){
					slavestr = "";
				}
				if(oninitstr ==null || "null".equals(oninitstr)){
					oninitstr="";
				}
				if(viewbindstr == null || "null".equals(viewbindstr)){
					viewbindstr = "";
				}
				if(!"".equals(viewbindstr)){
					if(oninitstr.equals("")){
					  oper.setViewbindstr(viewbindstr+slavestr+substr);
					}if(!"".equals(oninitstr)){	
					  oper.setViewbindstr(viewbindstr+oninitstr+slavestr+substr);
					}
				}if(viewbindstr.equals("")){
					if(oninitstr.equals("")){
					   oper.setSubmitlststr(slavestr+substr);
					}if(!"".equals(oninitstr)){	
					   oper.setSubmitlststr(oninitstr+slavestr+substr);
					}
				}
				List operlst = new ArrayList();				
				List inputlst = ioper.getFieldList();
				//如果有字段名被修改
				StringBuffer sb = new StringBuffer();
				if(!"edit".equals(sessionobj.getXsnpath()) && !"".equals(sessionobj.getNoequalvalue())){
					if(ioper.getViewBind()!=null){	
							sb.append(ioper.getViewBind().creatViewBindXml(2,sessionobj));
							oper.setNewinitxml(ioper.getViewBind().creatViewBindfieldXml(2, sessionobj));
							StringBuffer sbslave = new StringBuffer();
							for(int a=0;a<ioper.getSlaveTableList().size();a++){
								InfoPath_SlaveTable slave = (InfoPath_SlaveTable)ioper.getSlaveTableList().get(a);
								sb.append(slave.creatSlaveTableXml(0, sessionobj));
								sbslave.append(slave.creatSlaveTableXml(0, sessionobj));
							}
							oper.setNewrepeatxml(sbslave.toString());
							StringBuffer sbsubmit = new StringBuffer();
							for(int b=0;b<ioper.getInfoPath_SubmitList().size();b++){
								InfoPath_Submit submit = (InfoPath_Submit)ioper.getInfoPath_SubmitList().get(b);
								sb.append(submit.creatSubmitXml(0));
								sbsubmit.append(submit.creatSubmitXml(0));
							}
							oper.setNewsubmitxml(sbsubmit.toString());
							oper.setViewbindstr(sb.toString());	
					}
				}
				if(sessionobj.getEdittableMap().size() !=0 || sessionobj.getDelTablename().size() !=0){
					StringBuffer sbuf = new StringBuffer();
					StringBuffer sbsubmit = new StringBuffer();
					StringBuffer sbslave = new StringBuffer();
					for(int a=0;a<ioper.getSlaveTableList().size();a++){
						InfoPath_SlaveTable slave = (InfoPath_SlaveTable)ioper.getSlaveTableList().get(a);
						sbuf.append(slave.creatSlaveTableXml(0, sessionobj));
						sbslave.append(slave.creatSlaveTableXml(0, sessionobj));
					}
					for(int b=0;b<ioper.getInfoPath_SubmitList().size();b++){
						InfoPath_Submit submit = (InfoPath_Submit)ioper.getInfoPath_SubmitList().get(b);
						sbuf.append(submit.creatSubmitXml(0));
						sbsubmit.append(submit.creatSubmitXml(0));
					}
					oper.setNewsubmitxml(sbsubmit.toString());
					oper.setNewrepeatxml(sbslave.toString());
					oper.setSubmitlststr(sbuf.toString());
				}
				int id =0;
				for (int m = 0; m < tablefieldlst.size(); m++) {
					TableFieldDisplay tfd = (TableFieldDisplay) tablefieldlst.get(m);
					TableFieldDisplay tf = new TableFieldDisplay();
				for (int l = 0; l < inputlst.size(); l++) {
					IOperationField iof = (IOperationField) inputlst.get(l);
						if(iof.getName().equals(tfd.getBindname())){
							if(tfd.getEditname() == null){
								/*Map map = new HashMap();				
								map.put("bindname" + m, iof.getName());
								map.put("formoper" + m, iof.OperationType2str(iof.getAccess()));
								map.put("formprint" + m, iof.getAllowprint() == true ? "Y": "N");
								map.put("formtransmit" + m,iof.getAllowtransmit() == true ? "Y" : "N");*/
								tf.setBindname(iof.getName());
								tf.setFormoper(iof.OperationType2str(iof.getAccess()));
								tf.setFormprint(iof.getAllowprint() == true ? "Y": "N");
								tf.setFormtransmit(iof.getAllowtransmit() == true ? "Y" : "N");
								//operlst.add(map);
								operfielist.add(tf);
								id=m;
							}if(tfd.getEditname()!=null){
								/*Map map = new HashMap();
								map.put("bindname" +m, namespace+tfd.getEditname());
								map.put("formoper" + m, iof.OperationType2str(iof.getAccess()));
								map.put("formprint" + m, iof.getAllowprint() == true ? "Y": "N");
								map.put("formtransmit" + m,iof.getAllowtransmit() == true ? "Y" : "N");*/
								tf.setBindname(namespace+tfd.getEditname());
								tf.setFormoper(iof.OperationType2str(iof.getAccess()));
								tf.setFormprint(iof.getAllowprint() == true ? "Y": "N");
								tf.setFormtransmit(iof.getAllowtransmit() == true ? "Y" : "N");
								//operlst.add(map);
								operfielist.add(tf);
								id=m;
							}					
						}
					}			
				}
				 // 新上传字段
                 if(addfield !=null){
					 String beforeRepeatTableName = "";
				 for(int v=0;v<addfield.length ; v++){
					String opertype =  OperHelper.OperationType2str(ioper.getOperationType());
					 String formoper =null;
					 String formprint = null;
					 String formtransmit = null;
					 formoper = IXmlNodeName.C_sVluae_edit;
					 formprint = "Y";
					 formtransmit = "Y";
					 if(opertype.equals(IXmlNodeName.C_sVluae_add)){
						 formoper = IXmlNodeName.C_sVluae_edit;
						 formprint = "Y";
						 formtransmit = "Y";
					 }if(opertype.equals(IXmlNodeName.C_sVluae_update)){
						 formoper = IXmlNodeName.C_sVluae_browse;
						 formprint = "N";
						 formtransmit = "N";
					 }if(opertype.equals(IXmlNodeName.C_sVluae_readonly)){
						 formoper = IXmlNodeName.C_sVluae_browse;
						 formprint = "N";
						 formtransmit = "N";
					 }
					 
					 String newRepeatTableName = addfield[v].split("↗")[0];
					 String newField = addfield[v].split("↗")[1];
					 if(newRepeatTableName.indexOf("group") != -1) {
						 if(!newRepeatTableName.equalsIgnoreCase(beforeRepeatTableName)) {
							 beforeRepeatTableName = newRepeatTableName;
							 StringBuffer buffer = new StringBuffer();
							 buffer.append("<SlaveTable name=\"");
							 buffer.append(namespace + newRepeatTableName + "\" ");
							 if(opertype.equals(IXmlNodeName.C_sVluae_add)){
								 buffer.append("allowadd=\"true\" allowdelete=\"true\"");
							 }if(opertype.equals(IXmlNodeName.C_sVluae_update)){
								 buffer.append("allowadd=\"false\" allowdelete=\"false\"");
							 }if(opertype.equals(IXmlNodeName.C_sVluae_readonly)){
								 buffer.append("allowadd=\"false\" allowdelete=\"false\"");
							 }
							 buffer.append(" />");
							 oper.setNewrepeatxml(oper.getNewrepeatxml() + buffer.toString());
						 }
					 }
					    /*Map map = new HashMap();
					    int op =id+v+1;
					    map.put("bindname" + op, namespace+AddFieldName(addfield[v]));
					    map.put("formoper" + op, formoper);
						map.put("formprint" + op, formprint);
						map.put("formtransmit" + op,formtransmit);
						operlst.add(map);	*/	
					 TableFieldDisplay tf = new TableFieldDisplay();
					 tf.setBindname(namespace+AddFieldName(addfield[v]));
					 tf.setFormoper(formoper);
					 tf.setFormprint(formprint);
					 tf.setFormtransmit(formtransmit);
					 operfielist.add(tf);
			        }
                 }
             	for (int x = 0; x < newtabe.size(); x++) {
					TableFieldDisplay tfd = (TableFieldDisplay) newtabe.get(x);
					TableFieldDisplay tf = new TableFieldDisplay();
				for (int y = 0; y < operfielist.size(); y++) {
					TableFieldDisplay iof = (TableFieldDisplay) operfielist.get(y);
						if(iof.getBindname().equals(tfd.getBindname())){			
								Map map = new HashMap();				
								map.put("bindname" + x, iof.getBindname());
								map.put("formoper" + x, iof.getFormoper());
								map.put("formprint" + x, iof.getFormprint());
								map.put("formtransmit" + x,iof.getFormtransmit());
								//tfd.setBindname(iof.getName());
								tfd.setFormoper(iof.getFormoper());
								tfd.setFormprint(iof.getFormprint());
								tfd.setFormtransmit(iof.getFormtransmit());
								operlst.add(map);	
						}
					}			
				}
				oper.setOperlst(operlst);
				formoperationlst.add(oper);
			}
			FormPage fp = (FormPage)sessionobj.getFormLst().get(j);
			if(formmap.get(sf.getFormName()) ==null)
			   fp.setOperlst(formoperationlst);	
		}
		return newtabe;
	} 
	/**
	 * 
	 * @param request
	 * @throws SeeyonFormException
	 * @throws DocumentException
	 * @throws BusinessException 
	 * @throws BusinessException 
	 */
	public void editBaseInfo(HttpServletRequest request) throws SeeyonFormException, DocumentException, BusinessException{
		HttpSession session = request.getSession();
		SessionObject sessionobject = (SessionObject)session.getAttribute("SessionObject");
		String formname = sessionobject.getFormName();
		String path = sessionobject.getXsnpath();
		String[] newfield = null;
		String[] addfield= null;
		String[] delefield= null;
		String[] noequalfield =null;

        //已匹配数据列表中相等字段的(新表名/旧表名↗新字段名)
		String matchdatavalue = (String)request.getParameter("matchdatavalue");
		if(!"".equals(matchdatavalue) && matchdatavalue !=null){
		   newfield = matchdatavalue.split(",");
		}
		//新增字段中数据（新表名↗新字段）
		String addvalue = (String)request.getParameter("addvalue");
		if(!"".equals(addvalue) && addvalue !=null){
		   addfield = addvalue.split(",");
		}
		//删除字段中数据（删除字段名）
		String delevalue = (String)request.getParameter("delevalue");
		if(!"".equals(delevalue) && delevalue !=null){
		   delefield = delevalue.split(",");
		}
		List tablelist = new ArrayList();
		InfoPathObject xsf = sessionobject.getXsf();
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(sessionobject.getFormid());
	    
		//已匹配数据列表中不相等字段的(新表名↖旧表名/新字段名↗旧字段名)
		String noequalvalue = (String)request.getParameter("noequalvalue");
		if(!"".equals(noequalvalue) && noequalvalue !=null){
		   noequalfield = noequalvalue.split(",");  
		}
		HashMap map = new HashMap();
		HashMap edittablemap = new HashMap();
		if(!"".equals(noequalvalue) && noequalvalue !=null){
			for(int i = 0;i<noequalfield.length;i++){			
		      map.put(OperHelper.AddFieldName(noequalfield[i]), OperHelper.noEqualfieldName(noequalfield[i]));
		      if(!OperHelper.noEqualfieldoldtableName(noequalfield[i]).equals( OperHelper.noEqualfieldnewtableName(noequalfield[i]))){
		    	  edittablemap.put(OperHelper.noEqualfieldoldtableName(noequalfield[i]), OperHelper.noEqualfieldnewtableName(noequalfield[i]));
		      }     
			}
		}
		if(!"".equals(matchdatavalue) && matchdatavalue !=null){
			for(int i=0;i<newfield.length;i++){			
				if(!OperHelper.noEqualName(newfield[i]).equals(OperHelper.noEqualfieldName(newfield[i]))){	
					edittablemap.put(OperHelper.noEqualfieldName(newfield[i]), OperHelper.noEqualName(newfield[i]));
				}					
			}		
		}	
		InfoPath_xsd xsd = xsf.getIntoxsd();
		String namespace = xsd.getNamespace();	
		if(newfield !=null){
	    for(int i=0; i<newfield.length;i++){
	    	TableFieldDisplay tfd = new  TableFieldDisplay();
	    	tfd.setName(AddFieldName(newfield[i]));
	    	tfd.setBindname(namespace + AddFieldName(newfield[i]));
	    	tfd.setTablename(oldtableName(newfield[i]));
	    	tfd.setEditablename(newtableName(newfield[i]));
	    	tablelist.add(tfd);
	    }
		}
		if(noequalfield!=null){
	    for(int i=0;i<noequalfield.length ; i++){
	    	TableFieldDisplay tfd = new  TableFieldDisplay();
	    	tfd.setName(AddFieldName(noequalfield[i]));
	    	tfd.setEditname(OperHelper.noEqualfieldName(noequalfield[i]));
	    	tfd.setBindname(namespace + AddFieldName(noequalfield[i]));
	    	tfd.setTablename(OperHelper.noEqualfieldoldtableName(noequalfield[i]));
	    	tfd.setEditablename(OperHelper.noEqualfieldnewtableName(noequalfield[i]));
	    	tablelist.add(tfd);   	
	    }
		}
		String id = (String)request.getParameter("id");
		sessionobject.setPageflag(IPagePublicParam.BASEINFO);
		sessionobject.setEditflag("edit");
		sessionobject.setNamespace(namespace);
		sessionobject.setFieldMap(map);
		sessionobject.setEdittableMap(edittablemap);
		sessionobject.setNoequalvalue(noequalvalue);
		sessionobject.setMatchdatavalue(matchdatavalue);
		sessionobject.setDeletevalue(delevalue);
		sessionobject.setFormName(formname);
		sessionobject.setXsnpath(path);
		sessionobject.setXsf(xsf);
		if(afapp!=null){
			sessionobject = loadFromDb(afapp, xsf, tablelist,addfield,id,sessionobject);
			session.setAttribute("SessionObject", sessionobject);
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(sessionobject.getFormName());
              //08-05-14修改
	    	  afapp.setFId(sessionobject.getFormid());
	    	  try{
	    		  afapp.loadFromDB();
				  sessionobject = loadFromDb(afapp, xsf, tablelist,addfield,id,sessionobject);
			  } catch(Exception e){
  				log.error("infopath上传修改表单错误", e);
 			  }finally{
 				 afapp.unloadAppHibernatResorece();
 			  }
			  session.setAttribute("SessionObject", sessionobject);
	    }
        //向session对象中塞入系统变量和扩展绑定,系统枚举,应用枚举,表单枚举  		
		sessionobject = systemenum(sessionobject);		
		//注入id
		sessionobject.setFormid(Long.valueOf(id));		
		FormAppMain fam = new FormAppMain();
		fam = getFormDaoManager().findApplicationById(Long.valueOf(id));
		List<FormAppMain> famlst = new ArrayList<FormAppMain>();
		famlst.add(fam);
		assignCategory(famlst);
		fam = (FormAppMain)famlst.get(0);
		sessionobject.setFormType(fam.getFormType());
		sessionobject.setAttachManId(fam.getUserids());
		sessionobject.setAttachManName(fam.getUsernames());
		sessionobject.setFormsort(fam.getCategory());
		sessionobject.setFormstate(String.valueOf(fam.getState()));
		sessionobject.setOldformsort(sessionobject.getFormsort());
		afapp.unloadAppHibernatResorece();
		afapp.loadFromDB();
		//设置转换SessionObj对象
		applicationToSessionObj(afapp,sessionobject);
		afapp.clearUselessMemory();
	}
	/**
	 * 表单修改时，修改表单中生成的相应xml及对应的数据库中字符串
	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 * @throws SQLException 
	 */
	public void editSave(SessionObject sessionobject,HttpServletRequest request,HttpServletResponse response,FileManager fileManager) throws SeeyonFormException, SQLException{
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl)fmanager.findById(sessionobject.getFormid());
		RuntimeCharset fCharset=SeeyonForm_Runtime.getInstance().getCharset();	
		PoCheckManager pom = (PoCheckManager)SeeyonForm_Runtime.getInstance().getPoCheckManager();		
		//此处为旧的session中的datadefine的element
		DataDefine dd = sessionobject.getData();
		if(fapp == null ){
			fapp = (SeeyonForm_ApplicationImpl)fmanager.createApplication();
			fapp.setAppName(sessionobject.getFormName());
            //08-05-14修改
			fapp.setFId(sessionobject.getFormid());
			try{
				fapp.loadFromDB();
			}catch(Exception e){
				log.error("修改表单定义解析错误", e);
			}finally{
				fapp.unloadAppHibernatResorece();
			}
		}else{
			fapp.reloadFormResourceInfo();
		}
		//索引
		SeeyonDataDefine seedade =(SeeyonDataDefine) fapp.getDataDefine();	
		DataDefine define = seedade.getDataDefine();
		for(int x=0;x<define.getTableLst().size();x++){
			FormTable ftable = define.getTableLst().get(x);
			String tablename =ftable.getName();		
			for(int y =0;y<sessionobject.getData().getTableLst().size();y++){
				FormTable objecttable = sessionobject.getData().getTableLst().get(y);
				if(objecttable.getName().equals(tablename)){
					objecttable.setIndexLst(ftable.getIndexLst());
				}
			}
		}

		List<FormEvent> oldFormEvent = fapp.getTriggerConfigList();
		Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)fapp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
		String thispage = (String)request.getParameter("thispage");
//		if(sessionobject.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue())
//			sessionobject.setOrgAllMenu(BindHelper.getMenuMap());
		//此处插入收集数据代码
		if(!sessionobject.getFormName().equals(sessionobject.getFormEditName()))
			isExistsThisForm(sessionobject.getFormEditName());
		if(thispage.equalsIgnoreCase(IPagePublicParam.BASEINFO)){
			OperHelper.baseInfoCollectData(request, sessionobject);
		}else if(thispage.equalsIgnoreCase(IPagePublicParam.INPUTDATA)){
//			增加防护
			try{
			OperHelper.inputDataCollectData(request, sessionobject);
			}catch(SeeyonFormException e){
				log.error("保存录入定义页面信息时出错", e);
				List<String> lst = new ArrayList<String>();
				lst.add(e.getToUserMsg());
				OperHelper.creatformmessage(request,response,lst);
			}
		}else if(thispage.equalsIgnoreCase(IPagePublicParam.OPERCONFIG)){
			//页面上没有什么需要填的东西
		}else if(thispage.equalsIgnoreCase(IPagePublicParam.BINDINFO)){
			//添加信息管理绑定信息 by wusb at 2010-03-17
			BindHelper.systemSaveAppBindMain(request, sessionobject);
		}
		//判断追加和文本域的匹配情况
		HashMap inputmap = new HashMap();
		for(int i=0 ; i<sessionobject.getFieldInputList().size();i++){
			InfoPath_FieldInput infopathput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(i);
			  inputmap.put(infopathput.getName(), infopathput.OperationType2str(infopathput.getInputType()));
		}
		
		for(int i=0;i<sessionobject.getFormLst().size();i++){
			FormPage formpage = (FormPage)sessionobject.getFormLst().get(i);
			for(int j=0;j<formpage.getOperlst().size();j++){
				Operation operation = (Operation)formpage.getOperlst().get(j);
				Map formmap = new HashMap();
				formmap.put("Operation", operation);
				 Operation oper = (Operation) formmap.get("Operation");
				 //2009年02月27日修改（保证操作文件名称不重复）

					 if(oper.getFilename().length() <25)
						 oper.setFilename("Operation_"+UUIDLong.absLongUUID() +".xml");

					for(int t=0;t<oper.getOperlst().size(); t++){
						Map opermap=(Map)oper.getOperlst().get(t);
						if("add".equalsIgnoreCase((String) opermap.get("formoper"+t))){
							if(!IXmlNodeName.C_sVluae_textArea.equalsIgnoreCase((String) inputmap.get(opermap.get("bindname"+t))))
								throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.fieldname.label")+" "+opermap.get("bindname"+t)+" "+Constantform.getString4CurrentUser("form.input.error"),Constantform.getString4CurrentUser("form.base.fieldname.label")+" "+opermap.get("bindname"+t)+" "+Constantform.getString4CurrentUser("form.input.error"));
								//throw new DataDefineException(1,"字段 "+opermap.get("bindname"+t)+" 的操作设置为追加类型,请到录入定义页面将其数据域输入类型设置为文本域","字段 "+opermap.get("bindname"+t)+" 的操作设置为追加类型,请到录入定义页面将其数据域输入类型设置为文本域");
						}		
					}
			}
		}
		
		List tablefieldlst = sessionobject.getTableFieldList();
		SeeyonFormImpl sfi = new SeeyonFormImpl();
		UserDefineXMLInfoImpl fxml=new UserDefineXMLInfoImpl();
		ChangeObjXml cox = new ChangeObjXml();	
		//执行验证程序

//		if(!pom.doCheck(sessionobject)){
//			throw new DataDefineException(1,"","验证sessionobject出错！");
//		}		
		List<SeeyonFormException> exceptionList = pom.doCheck(sessionobject);
		if(exceptionList.size() != 0){
			SeeyonFormCheckException sce = new SeeyonFormCheckException(1);
			sce.setList(exceptionList);
			throw sce;
		}
		
		//修改时，从新设置基础数据中的name值
		modifyBaseData(sessionobject, fapp);
		//组织all.xml
		Map map = new HashMap();
//		08-05-19修改增加id
		map.put("FormId", sessionobject.getFormid());
		map.put("FormName", sessionobject.getFormName());
		map.put("DataDefine", sessionobject.getData());
		map.put("FormList", sessionobject.getFormLst());
		if( sessionobject.getTemplateobj() != null){
			map.put("TemplateObject", sessionobject.getTemplateobj());	
		}
		if(sessionobject.getReportConditionList().size()!=0){
			map.put("ReportList",sessionobject.getReportConditionList());
		}
		if(sessionobject.getQueryConditionList().size()!=0){
			map.put("QueryList",sessionobject.getQueryConditionList());
		}
		String ftemp = cox.createSeeyonDataDefineXml(2,map,sessionobject);		
		ftemp=fCharset.SystemDefault2SelfXML(ftemp);
		fxml.setSeeyonFomDefineXML(ftemp);
		
		//组织Operation_001.xml
		List formlst = sessionobject.getFormLst();
		for(int i=0;i<formlst.size();i++){
			FormPage formpage = (FormPage)formlst.get(i);
			for(int j=0;j<formpage.getOperlst().size();j++){
				Operation operation = (Operation)formpage.getOperlst().get(j);
				//如果客户没有在页面进行操作，注入默认值
				int regflag = 1;
				//修改时operation.getOperlst肯定不为空,即没有默认值的操作,即regflag = 1
//				if(operation.getOperlst() == null){
//					//regflag为2的时候为调用调用默认程序
//					operation.setOperlst(OperHelper.getFixOperlst(tablefieldlst,operation.getType()));
//				}
//				if(operation.isEditflag()){
//					regflag = 1;
//				}else{
//					regflag = 2;
//				}
				Map formmap = new HashMap();
				formmap.put("Operation", operation);
				ftemp=fCharset.SystemDefault2SelfXML(cox.createOperationXml(regflag,operation.getType(),tablefieldlst, formmap,sessionobject));
				fxml.addResource(operation.getFilename(), ftemp);
			}
		}		
		//组织bindschema.xml
		Map binschemamap = new HashMap();
		binschemamap.put("TableFieldList", sessionobject.getTableFieldList());
		ftemp=fCharset.SystemDefault2SelfXML(cox.creatBindschemaXml(2, binschemamap));
		fxml.addResource("bindschema.xml",ftemp);
		
		//组织bindAppData.xml
		
		//组织defaultInput.xml
		List inputlst = sessionobject.getFieldInputList();
		Map defaultmap = new HashMap();
		defaultmap.put("FieldInputList", inputlst);
		ftemp=fCharset.SystemDefault2SelfXML(cox.createDefaultInputXml(2, defaultmap));		
		fxml.addResource("defaultInput.xml",ftemp);
		//组织xsn
		String path = sessionobject.getXsnpath();
		if(path == null){
			//throw new SeeyonFormException(1,"路径为空！");
			throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.pathisnull.label"));
		}
		IFormResoureProvider fResourceProvider =  null;
		//分逻辑：从infopath导入文件的修改  直接进行修改
		if("edit".equals(path)){
			fResourceProvider = fapp.getFResourceProvider();	
			for (IResourceInfo fInfo : fxml.getResourceList()) {
					fResourceProvider.addResource(ISeeyonForm_Application.C_sResourceDir_UserDefineXML
							+ fInfo.getResourceName(), fInfo.getResourceInfo());
					fResourceProvider.addFormProperty(fInfo.getResourceName(),fapp.C_iPropertyType_UserDefineXML, ISeeyonForm_Application.C_sResourceDir_UserDefineXML
							+ fInfo.getResourceName());						
			}
			fapp.setCategory(sessionobject.getFormsort());
		}else{
			ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(StringUtils.readFileData(path));
			fapp = (SeeyonForm_ApplicationImpl)fmanager.createApplication();		
			//组织submitData_new.xml
			fapp.setCategory(sessionobject.getFormsort());
			//fapp.loadFromCAB(fInfopathxsn, fxml);
			fapp.loadFromCAB(fInfopathxsn, fxml,sessionobject,fileManager);
			fResourceProvider = fapp.getFResourceProvider();
		}
			Document doc = null;
			doc  = dom4jxmlUtils.paseXMLToDoc(fxml.getSeeyonFomDefineXML());	
			Element root = doc.getRootElement();
			// 准备资源文件
			//组织新的datadefine  xml文件
			Element dataroot = fapp.loadDataDefine(root, fResourceProvider);
            //8月17号添加的逻辑，增加hbm文件
			fapp.getDataDefine().loadFromXml(dataroot);		
			SeeyonDataDefine hbmdade=(SeeyonDataDefine)fapp.getDataDefine();
			String fHbmXml = hbmdade.getDataDefine().getHbmString(0);			
			if("edit".equals(path)){
				fxml.addResource(SeeyonDataDefine.C_sResourceFileName_Hbm,fHbmXml);
				for (IResourceInfo fInfo : fxml.getResourceList()) {
					if(SeeyonDataDefine.C_sResourceFileName_Hbm.equals(fInfo.getResourceName())){
						fResourceProvider.addResource(SeeyonDataDefine.C_sResourceDir_Hibernate
								+ fInfo.getResourceName(), fInfo.getResourceInfo());
						fResourceProvider.addFormProperty(fInfo.getResourceName(),SeeyonDataDefine.C_iPropertyType_HBMFile, SeeyonDataDefine.C_sResourceDir_Hibernate
								+ fInfo.getResourceName());		
					}			
				}
			}
			FormAppMain fam = (FormAppMain)getFormDaoManager().findApplicationById(sessionobject.getFormid());
	        String sysdatetime = fam.getSystemdatetime();
		    fam = new FormAppMain();
			if(!(sessionobject.getFormEditName() == null 
					|| "".equals(sessionobject.getFormEditName())
					||"null".equals(sessionobject.getFormEditName())))
			fam.setName(sessionobject.getFormEditName());
			else fam.setName(sessionobject.getFormName());
			//根据查询数据库中是否有此纪录
			fam.setId(sessionobject.getFormid());
			fam.setState(Integer.valueOf(sessionobject.getFormstate()));
			fam.setSystemdatetime(sysdatetime);
            //修改表单时为启用状态。
			fam.setFormstart(1);
			fam.setFormType(sessionobject.getFormType());
			 //保存绑定信息			
			if(sessionobject.getTemplateobj() != null){
                HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) request.getSession().getAttribute("currentFormNewflow");
				getBindHelper().save(sessionobject.getTemplateobj(),sessionobject, currentFormNewflow);				
			}
			
			if(!BindHelper.checkFormCode(String.valueOf(sessionobject.getFormid()), sessionobject.getFormCode(), sessionobject.getFormType())){
				throw new DataDefineException(1,"表单编号已经有重名！");
			}
			
			//保存主要信息
			try{
				editpostdata(fapp,fam,root, fResourceProvider,sessionobject);
				
			}catch(Exception e){
				
				//throw new DataDefineException(1,"事物实例失败，数据库保存出错","事物实例失败，数据库保存出错");
				throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
			}
			updateFlowIdstate(sessionobject);
//			modifyInfoToDB(fapp,fam,root, fResourceProvider,sessionobject);	
			//TODO 修改使用人存入数据库	
//			updateFormAppAttachMan(sessionobject,fam);	
					
			//最后一步执行修改表的操作
			//			boolean bPublish;//app是否已发布
//			if(sessionobject.getFormstate().equals("0"))
//				bPublish = false;
//			else
//			    bPublish = true;
//			只有基础信息修改时更新数据表
			//if(thispage.equals("baseInfo"))
		    fapp.getDataDefine().updateStorage(dataroot,oldroot, fapp.getSessionFactory(), sessionobject.getFormstate(), sessionobject);
		    
			//8月17号添加的逻辑，修改表单重新加载app
			if(fam.getState()==1 ||fam.getState()==2){
				//08-05-21修改
				if(fmanager.findById(sessionobject.getFormid(),true) != null){
					//if(fmanager.findById(sessionobject.getFormid()).getAppName() !=null)
					   fmanager.unRegApp(sessionobject.getFormid());
				}
				editregApp(fam);
			}
			
            //根据查询条件比较索引
			
//			if(oldroot.asXML().indexOf("表索引") !=-1 && !sessionobject.getFormstate().equals("0")){
//			fapp = (SeeyonForm_ApplicationImpl) fmanager.findById(fam.getId());
//			if(fam.getState()==1 ||fam.getState()==2)
////				更改唯一标识为id（08年5月14号）
//			    //fapp = (SeeyonForm_ApplicationImpl) fmanager.findByName(fam.getName());				
//			    formcompareindex(fapp,sessionobject,fam);
//			}
			List<Long> formflowidlist = new ArrayList<Long>();
			
			if(CurrentUser.get().getId() !=Long.parseLong(sessionobject.getAttachManId())){
				for(int j =0;j< fapp.getSeeyonFormBind().getFlowTempletList().size();j++){
		        	FlowTempletImp flow = (FlowTempletImp)fapp.getSeeyonFormBind().getFlowTempletList().get(j);
		        	formflowidlist.add(flow.getId());
		        }
				if(formflowidlist.size()>0){
					TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
					templeteManager.updateMemberId(formflowidlist, Long.parseLong(sessionobject.getAttachManId()));
				}
					   
			}
			if(!(sessionobject.getFormEditName() == null 
					|| "".equals(sessionobject.getFormEditName())
					||"null".equals(sessionobject.getFormEditName())))
			sessionobject.setFormName(sessionobject.getFormEditName());

			//根据新老数据 判断是否触发 新增 修改 删除 表单触发设置
//			Map<Long, FormEvent> newFormTrigger = ((SeeyonForm_ApplicationImpl)fmanager.findById(sessionobject.getFormid())).getTriggerConfigMap();
			EventTriggerForHistoryData.checkModifyTrigger(sessionobject.getFormid(),sessionobject.getTriggerConfigMap(),oldFormEvent);
			//更新其他相关联的表单相关信息
			FormHelper.updateOtherFormRelationInfo(sessionobject);
				
	}
	
	/**
	 * 修改表单时，更新基础数据中的name值
	 * @param sessionobject
	 * @throws SeeyonFormException 
	 */
	private void modifyBaseData(SessionObject sessionobject, SeeyonForm_ApplicationImpl fapp) throws SeeyonFormException{
		if(!sessionobject.getFormName().equals(sessionobject.getFormEditName())){
			String newFormName = sessionobject.getFormEditName();
			FormAppAuthObject appAuthObject = sessionobject.getAppAuthObject();
			appAuthObject.setName(newFormName);
			String xmlString = appAuthObject.getXmlString();
			if(Strings.isNotBlank(xmlString)){
				Document doc = dom4jxmlUtils.paseXMLToDoc(xmlString);
				Element root = doc.getRootElement();
				FormAppAuth appAuth = new FormAppAuth(fapp);
				appAuth.loadFromXml(root);
				appAuth.setName(newFormName);
				appAuth.getQuery().setQueryName(newFormName);
				Map<String, OperationAuth> operationAuths = appAuth.getOperationAuths();
				//基础数据中operationAuths只有1条
				OperationAuth operAuth = operationAuths.get(sessionobject.getFormName());
				operAuth.setName(newFormName);
				operationAuths.clear();
				operationAuths.put(newFormName, operAuth);
				//授权
				Map<String,FormOperAuthObject> appOperAuthObjectMap = appAuthObject.getAppOperAuthObjectMap();
				FormOperAuthObject operAuthObject = appOperAuthObjectMap.get(sessionobject.getFormName());
				operAuthObject.setName(newFormName);
				List<FomObjaccess> objAccessList = operAuthObject.getObjAccessList();
				for (int i = 0; i < objAccessList.size(); i++) {
        			FomObjaccess fobj = (FomObjaccess)objAccessList.get(i);
        			fobj.setObjectname(newFormName);
    			}	
				appOperAuthObjectMap.clear();
				appOperAuthObjectMap.put(newFormName, operAuthObject);
				sessionobject.getAppAuthObject().setXmlString(appAuth.getXmlString(4));
			}
		}
	}
	
	 
	/**
	 * 修改信息到数据库
	 * @param fapp
	 * @param aRoot
	 * @param aProvider
	 * @throws SeeyonFormException
	 * @throws SQLException 
	 */
	private synchronized void modifyInfoToDB(ISeeyonForm_Application fapp,FormAppMain fam,Element aRoot, IFormResoureProvider aProvider,SessionObject sessionobject) throws SeeyonFormException, SQLException {
		if(fam.getId() == null){
			//throw new DataDefineException(1,"主表ID没有传入！","主表ID没有传入！");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.mastertableidnotimport.label"),Constantform.getString4CurrentUser("form.base.mastertableidnotimport.label"));
		}
		if(!(sessionobject.getFormEditName() == null 
				|| "".equals(sessionobject.getFormEditName())
				||"null".equals(sessionobject.getFormEditName()))){
			if(!sessionobject.getFormEditName().equals(sessionobject.getFormName())){
				isExistsThisForm(fam.getName());
			}	
		}
		// 组织FormAppMain
		String ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(IXmlNodeName.Bind).asXML());
		fam.setBindInfo(getFCurrentCharSet().JDK2DBIn(ftemp));
		
		ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(IXmlNodeName.Define).asXML());
		fam.setDataStructure(getFCurrentCharSet().JDK2DBIn(ftemp));
		
		ftemp = getFCurrentCharSet().SelfXML2JDK(aRoot.element(IXmlNodeName.Trigger).asXML());
		fam.setTriggerConfig(getFCurrentCharSet().JDK2DBIn(ftemp));
		
		fam.setCategory(fapp.getCategory());
		
		//先删除appresource和propertylist中的值
		
		FormAppResource far = new FormAppResource();
		far.setSheetName(fam.getId());
		List appresourcelist = getFormDaoManager().queryResourceByAppid(far);
        for(int i = 0;i<appresourcelist.size();i++){
			FormAppResource resource = (FormAppResource)appresourcelist.get(i);
			fSession.delete(resource);
		}
		//fSession.createQuery("delete from FormAppResource fo where fo.sheetName =?").setLong(0, fam.getId()).executeUpdate();
		
		FormPropertList fpl = new FormPropertList();
		fpl.setSheetName(fam.getId());
		List propertList = getFormDaoManager().queryProperListByAppid(fpl);
 		for(int i = 0;i<propertList.size();i++){
			FormPropertList proper = (FormPropertList)propertList.get(i);
			fSession.delete(proper);
		}
		//fSession.createQuery("delete from FormPropertList fo where fo.sheetName =?").setLong(0, fam.getId()).executeUpdate();
		
		//fSession.createQuery("delete FormPropertList fp where fp.sheetName=" + fam.getId());
//		getFormDaoManager().delPropertyListByCondition(fpl);
		
		//分别插入子表信息，以达到更新的目的

		//插入子表FormPropertList的信息

		List<FormPropertList> fpllst = new ArrayList<FormPropertList>();
		FormPropertList fPropertyInfoDB;
		for (IPropertyInfo fInfo : aProvider.getFormPropertyList()) {
			fPropertyInfoDB = new FormPropertList();
			fPropertyInfoDB.setName(getFCurrentCharSet().JDK2DBIn(fInfo
					.getPropertyName()));
			fPropertyInfoDB.setType(fInfo.getPropertyType());
			fPropertyInfoDB.setValue(getFCurrentCharSet().JDK2DBIn(fInfo
					.getPropertyValue()));
			fPropertyInfoDB.setSheetName(fam.getId());
			fPropertyInfoDB.setId(UUIDLong.longUUID());
			fSession.save(fPropertyInfoDB);
			fpllst.add(fPropertyInfoDB);
//			getFormDaoManager().savePropertList(fPropertyInfoDB);
		}
		
		//插入子表FormAppResource的信息
		List<FormAppResource> farlst = new ArrayList<FormAppResource>();
		FormAppResource fResourceDB = new FormAppResource();
		for (IResourceInfo resource : aProvider.getResourceList()) {
			fResourceDB = new FormAppResource();
			fResourceDB.setName(getFCurrentCharSet().SystemDefault2Dbin(resource
					.getResourceName()));
			fResourceDB.setContent(getFCurrentCharSet().SystemDefault2Dbin(resource
					.getResourceInfo()));
			fResourceDB.setSheetName(fam.getId());
			fResourceDB.setId(UUIDLong.longUUID());
			fSession.save(fResourceDB);
			farlst.add(fResourceDB);
//			getFormDaoManager().saveAppResource(fResourceDB);
		}

		//插入主表信息
		fSession.merge(fam);
		fam.setResourcelst(farlst);
		fam.setPropertlst(fpllst);
//		getFormDaoManager().updateAppMain(fam);
		
        //删除objaccess中的值
//		FomObjaccess acc = new FomObjaccess();
//		acc.setRefAppmainId(fam.getId());
		//fSession.createQuery("delete FomObjaccess where refAppmainId =" + fam.getId());
//		getFormDaoManager().delelctObjAccessByCondition(acc);
		
        // 把AccessObject存入数据库(统计、查询、菜单)
		//if(sessionobject.getReportConditionList().size()!=0 ||sessionobject.getQueryConditionList().size() !=0){
			updataAccessObject(sessionobject);
		//}
		//上传infopath修改时若有删除的子表
		//deletalbesql(sessionobject);
        //上传infopath修改时若有新增的子表
		//addtalbesql(sessionobject,fapp);
	}
	private void deletalbesql(SessionObject sessionobject) throws SeeyonFormException, SQLException{
		List deltablelist = new ArrayList(); 

		if(sessionobject.getDelTablename().size()!=0){
			for(int i=0;i<sessionobject.getDelTablename().size();i++){
				String name = (String)sessionobject.getDelTablename().get(i);
				//OperHelper.AddFieldName(name);
				//FormTableValue ftv = new FormTableValue(); 
				//ftv.setName(OperHelper.AddFieldName(name));
				deltablelist.add(OperHelper.AddFieldName(name));
			}
		}
		if(sessionobject.getDelTablename().size()!=0){
			List<String> strBatch = new ArrayList<String>();
			for(int i=0;i<deltablelist.size();i++){
				//FormTableValue ftvv = (FormTableValue)deltablelist.get(i);
				StringBuffer sb = new StringBuffer();
				sb.append("drop table ");
				sb.append(deltablelist.get(i));
				//sb.append(";");
				strBatch.add(sb.toString());
				//getFormDaoManager().delTableValueByTablename(ftvv);
			}
			getFormDaoManager().execSQLList(strBatch);
		}
	 }
	
	private void addtalbesql(SessionObject sessionobject,ISeeyonForm_Application fapp) throws SeeyonFormException, SQLException{
		List<TableFieldDisplay> tablefieldlist = new ArrayList<TableFieldDisplay>();
		/*
		Long tablenumber = 0L;
		FormTableValue fv = (FormTableValue)findBiggestValue();
		if(fv.getValue() != null){
			tablenumber = fv.getValue();
		}*/
		
		if(sessionobject.getAddtablename().size()!=0){
			List<String> strBatchs = new ArrayList<String>();
			for(int i=0;i<sessionobject.getAddtablename().size();i++){
				String name = (String)sessionobject.getAddtablename().get(i);
				SeeyonDataDefine seeyon =(SeeyonDataDefine)fapp.getDataDefine();			
				for(int j = 0;j<seeyon.getDataDefine().getTableLst().size();j++){
					FormTable tfd = (FormTable)seeyon.getDataDefine().getTableLst().get(j);
					if(tfd.getName().equals(OperHelper.AddFieldName(name))){
						/*
						FormTableValue ftv = new FormTableValue();
						ftv.setName(tfd.getName());
						ftv.setValue(tablenumber);
						ftv.setSheetName(sessionobject.getFormid());
						getFormDaoManager().insertTableValue(ftv);*/
						SeeyonForm_Runtime.getInstance().getDBAdapter().getCreateTableSQL(tfd);
						//ForMysql fomy = new ForMysql();
						StringBuffer sb = new StringBuffer();
						//fomy.getCreateTableSQL(tfd);
						sb.append(SeeyonForm_Runtime.getInstance().getDBAdapter().getCreateTableSQL(tfd));
						strBatchs.add(sb.toString());
					}
				}			
			}
			getFormDaoManager().execSQLList(strBatchs);
		}
	}
	
	/**
	 * 更新所属人数据信息
	 * @param sessionobject
	 * @param fdm
	 * @throws DataDefineException
	 */
	private void updateFormAppAttachMan(SessionObject sessionobject,FormAppMain fam) throws DataDefineException{
		if(fam.getId() == null){
			//throw new DataDefineException(1,"主表ID没有传入！","主表ID没有传入！");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.mastertableidnotimport.label"),Constantform.getString4CurrentUser("form.base.mastertableidnotimport.label"));
		}
		User user = CurrentUser.get();
		long orgAccountId = user.getLoginAccount();
		Long id = fam.getId();
		FormOwnerList fol = new FormOwnerList();
		fol.setAppmainId(id);
		String manid = sessionobject.getAttachManId();
		//先删除旧的，然后再增加新的纪录
		//fSession.createQuery("delete FormOwnerList fo where fo.appmainId =" + id);
//		getFormDaoManager().delOwnerListByCondition(fol);
		if(manid.indexOf(",") != -1){
//			List ownerlist= fSession.find("from FormOwnerList fo where fo.appmainId =" + id);
//			for(int i =0;i<ownerlist.size();i++){
//				FormOwnerList foltemp =(FormOwnerList)ownerlist.get(i);
//				fSession.delete(foltemp);
//			}
			fSession.createQuery("delete from FormOwnerList fo where fo.appmainId =?").setLong(0, id).executeUpdate();
            //fSession.delete("from FormOwnerList fo where fo.appmainId =?",new Object[]{id});
			String[]manidarray =  manid.split(",");
			for(int i=0;i<manidarray.length;i++){
				FormOwnerList foltemp = new FormOwnerList();
				foltemp.setAppmainId(id);
				foltemp.setOwnerId(Long.valueOf(manidarray[i]));
				foltemp.setOrg_account_id(orgAccountId);
				foltemp.setId(UUIDLong.longUUID());
//				getFormDaoManager().insertOwnerList(foltemp);
				fSession.save(foltemp);
			}
		}else{
			//List ownerlist= fSession.find("from FormOwnerList fo where fo.appmainId =" + id);
			fol = (FormOwnerList)getFormDaoManager().queryOwnerListByCondition(fol).get(0);
			//FormOwnerList oldfoltemp = (FormOwnerList) ownerlist.get(0);
			FormOwnerList foltemp = new FormOwnerList();
			foltemp.setAppmainId(id);
			foltemp.setOwnerId(Long.valueOf(manid));
			foltemp.setOrg_account_id(orgAccountId);
			foltemp.setId(fol.getId());
//			getFormDaoManager().insertOwnerList(foltemp);
			fSession.merge(foltemp);
		}	
	}

	
	
	/**
	 * 执行删除操作
	 * @param id
	 * @throws DataDefineException,SQLException
	 */
	public void delForm(Long id,String aAppName,FileManager fileManager) throws SeeyonFormException, SQLException{
        // 删除模板
		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(id);
		List ids = new ArrayList();
//		List menuIds = new ArrayList();
		List tablelst = new ArrayList();
		if(afapp!=null){
			SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
			tablelst = seeyon.getDataDefine().getTableLst();
			if(afapp.getSeeyonFormBind().getFlowTempletList()!=null){
				for(int i = 0;i<afapp.getSeeyonFormBind().getFlowTempletList().size();i++){
					Long temid = afapp.getSeeyonFormBind().getFlowTempletList().get(i).getId();
					ids.add(temid.toString());
				}
			}
			
			/*List<IBindMenuItem> bindMenuItemList = afapp.getSeeyonFormBind().getMenuList();
			if(bindMenuItemList!=null){
				for (IBindMenuItem menu : bindMenuItemList) {					
					String bindMenuId = menu.getGroupId();	
					if(org.apache.commons.lang.StringUtils.isNotBlank(bindMenuId)){
						menuIds.add(new Long(bindMenuId));
						menuIds.add(new Long(menu.getId()));
					}
				}
			}*/
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(aAppName);
              //08-05-14修改
	    	  afapp.setFId(id);
	    	  try{
	    		  afapp.loadFromDB();
				  SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
				  tablelst = seeyon.getDataDefine().getTableLst();
				  if(afapp.getSeeyonFormBind().getFlowTempletList()!=null){
						for(int i = 0;i<afapp.getSeeyonFormBind().getFlowTempletList().size();i++){
							Long temid = afapp.getSeeyonFormBind().getFlowTempletList().get(i).getId();
							ids.add(temid.toString());
						}
				  }
				  
				 /* List<IBindMenuItem> bindMenuItemList = afapp.getSeeyonFormBind().getMenuList();
					if(bindMenuItemList!=null){
						for (IBindMenuItem menu : bindMenuItemList) {					
							String bindMenuId = menu.getGroupId();	
							if(org.apache.commons.lang.StringUtils.isNotBlank(bindMenuId)){
								menuIds.add(new Long(bindMenuId));
								menuIds.add(new Long(menu.getId()));
							}
						}
					}*/
			  }catch(Exception e){
	  			  log.error("表单定义解析出错", e);
	 		  }finally{
	 			  afapp.unloadAppHibernatResorece();
			  } 			 
	    }
		List<FormFlowid> flowidlist= getFlowidListbyformid(id.toString());
		List<FormFlowid> inputData_flowidlist = getFlowidListbyformid("inputData_" + id.toString());
		if(CollectionUtils.isNotEmpty(inputData_flowidlist)){
			flowidlist.addAll(inputData_flowidlist);
		}
	    for(int i=0;i<flowidlist.size();i++){
	    	FormFlowid formflowid = flowidlist.get(i);
	    	if("Y".equalsIgnoreCase(formflowid.getState())){
	    		formflowid.setAppname(null);
	    	    formflowid.setState("N");
	    		updateFlowId(formflowid);  
	    	}    		
	    }
		HashMap oldlogo = new HashMap();
		oldlogo = getlogo(id);
		//删除表号中的表纪录
		//FormTableValue ftv = new FormTableValue();
		//ftv.setSheetName(id);
		//用于删除数据库中存在的表
		//List tablelst = getFormDaoManager().queryTableValue(ftv);			
		//删除appresource和propertylist中的值
		FormAppResource far = new FormAppResource();
		far.setSheetName(id);
		getFormDaoManager().delAppResourceByCondition(far);
		//删除objaccess中的值
		FomObjaccess acc = new FomObjaccess();
		acc.setRefAppmainId(id);
		getFormDaoManager().delelctObjAccessByCondition(acc);
		
		FormPropertList fpl = new FormPropertList();
		fpl.setSheetName(id);
		getFormDaoManager().delPropertyListByCondition(fpl);		
		//删除所属人表
		FormOwnerList fol = new FormOwnerList();
		fol.setAppmainId(id);
		getFormDaoManager().delOwnerListByCondition(fol);	
		//getFormDaoManager().delTableValueByCondition(ftv);
		//删除主表的数据
		getFormDaoManager().deleteAppMain(id);		
		//执行删除操作
		List<String> strBatch = new ArrayList<String>();
		for(int i=0;i<tablelst.size();i++){
			FormTable ftvv = (FormTable)tablelst.get(i);
			StringBuffer sb = new StringBuffer();
			sb.append("drop table ");
			sb.append(ftvv.getName());
			//sb.append(";");
			strBatch.add(sb.toString());
		}
		getFormDaoManager().execSQLList(strBatch);
		
		//如果上传的Infopath中带有图片,则删除表单时要把存储的图片删除。	
		if(oldlogo.size()!=0){
			Iterator it = oldlogo.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				Object imgid = entry.getValue();
				try {
					fileManager.deleteFile(Long.parseLong(imgid.toString()), true);
				} catch (BusinessException e) {
					log.error("删除带有图片的表单时，删除图片错误", e);
					//e.printStackTrace();
				}
			}	
		}
		
		BindHelper.deleteTemplate(ids);
		//删除挂接菜单
//		BindHelper.deleteBindMenu(null,menuIds);
		
		//从session中删除app
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		if(fmanager.findById(id,true) != null)
			fmanager.unRegApp(id);
	}
	
	/***************************************************************************/
	/**
	 * 传给页面需要的字符串
	 * @return
	 * @throws SeeyonFormException
	 */
	public String returnViewStr(ISeeyonForm_Application iapp,HttpServletRequest request,String formname,String fillcode) throws SeeyonFormException {
		try {
			SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) iapp;
			IFormResoureProvider ifrp = sapp.getFResourceProvider();

			StringWriter fSw = new StringWriter(1000);
			SeeyonFormImpl fform = (SeeyonFormImpl) sapp.findFromByName(formname);
			String ftemp;
			FileOutputStream fout;
			// 生成数据文件
			fSw = new StringWriter(1000);
			//是否从权限中取出
			long userId = CurrentUser.get().getId();
			String username = CurrentUser.get().getName();
			String userloginname = CurrentUser.get().getLoginName();
			TUserInfo fUserinfo = new TUserInfo(userId, username, userloginname);
			//通过SeeyonForm_ApplicationImpl找到资源文件
			String fBindAppData = ifrp.loadResource(IPagePublicParam.defaultinputxml);
			fBindAppData = getFCurrentCharSet().SystemDefault2JDK(fBindAppData);
			Document fdoc = dom4jxmlUtils.paseXMLToDoc(fBindAppData);
			fform.getView_Design(TviewType.vtHtml, fSw);
			ftemp = fSw.getBuffer().toString();
			return ftemp;
		} catch (Exception e) {
			//return SeeyonFormImpl.ERR_MESSEAGE;
            //throw new SeeyonFormException(1, e);
			//DataDefineException ss = (DataDefineException)e;
			//return String.format(SeeyonFormImpl.ERR_MESSEAGE, ss.getToUserMsg());
			log.error("获取表单运行时样式权限时发生错误", e);
	    	if(e.getCause() instanceof SeeyonFormException){
	    		  DataDefineException ss = (DataDefineException)e;
	    		  return String.format(StringUtils.Java2JavaScriptStr(SeeyonFormImpl.ERR_MESSEAGE), ss.getToUserMsg());
	    	}else{
	    		  String errorFileName = LogUtil.getFileName();//取得错误日志文件名	
	  			  LogUtil.writeErrorMsg(e, errorFileName);//写错误信息
	    		  return String.format(StringUtils.Java2JavaScriptStr(SeeyonFormImpl.ERR_MESSEAGE), Constantform.getString4CurrentUser("DataDefine.DefineErrorLookLog")+errorFileName);
	        }
		}
	}
	/**
	 * 执行发布操作
	 * @param id
	 * @throws DataDefineException,SQLException
	 */
	public void publishForm(Long id,int state,String aAppName,boolean isDelete) throws SeeyonFormException{
		//更新数据库状态
		SeeyonFormCheckException sce = null;
		FormAppMain fam = getFormDaoManager().findApplicationById(id);
		if(state > 0 && state == fam.getState() ){
			sce = new SeeyonFormCheckException(1);
			//sce.getList().add(new DataDefineException(1,"表单只能从预发布到发布并且不能重复操作！","表单只能从预发布到发布并且不能重复操作！"));
			sce.getList().add(new DataDefineException(1,Constantform.getString4CurrentUser("form.base.onlyperpublishtopublish.label"),Constantform.getString4CurrentUser("form.base.onlyperpublishtopublish.label")));
			throw sce;
		}
		if(state > 0 && state < fam.getState()){
			sce = new SeeyonFormCheckException(1);
			//sce.getList().add( new DataDefineException(1,"表单只能从预发布到发布并且不能重复操作！","表单只能从预发布到发布并且不能重复操作！"));
			sce.getList().add(new DataDefineException(1,Constantform.getString4CurrentUser("form.base.onlyperpublishtopublish.label"),Constantform.getString4CurrentUser("form.base.onlyperpublishtopublish.label")));
			throw sce;
		}
		
		ISeeyonForm_Application afapp = SeeyonForm_Runtime.getInstance().getAppManager().findById(id);
		List tablelst = new ArrayList();
		if(afapp!=null){
			SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
			tablelst = seeyon.getDataDefine().getTableLst();
		}else if(afapp == null){
			  afapp = new SeeyonForm_ApplicationImpl();
			  afapp.setAppName(aAppName);
              //08-05-14修改
	    	  afapp.setFId(fam.getId());
	    	  try{
	    		  afapp.loadFromDB();
				  SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
				  tablelst = seeyon.getDataDefine().getTableLst();
			  } catch(Exception e){
  				  log.error("修改表单定义解析错误", e);
 			  }finally{
 				 afapp.unloadAppHibernatResorece();
 			  }
			  //为查询统计校验用，先更新工厂
			  String fHbmXmlFielname = afapp.getFormProperty(SeeyonDataDefine.C_sResourceFileName_Hbm, SeeyonDataDefine.C_iPropertyType_HBMFile);
			  String fHbmXml = afapp.getFormResource(fHbmXmlFielname);
			  afapp.getConfiguration().setIsInvaild(true);
			  afapp.getConfiguration().setIsInvaild(false);
			  SeeyonFormHBConfiguration fconfig= afapp.getConfiguration();
			  fconfig.addXML(fHbmXml);
			  
	    }
		
		SeeyonFormCheckException fsce = new SeeyonFormCheckException(2);
		List<String> flist = null;
		flist = this.checkQueryList(afapp, null);//查询设置校验
		if(flist.size() != 0){
			for(String str : flist){
				fsce.getList().add(new DataDefineException(1,str,str));
			}
		}
		flist = this.checkReportList(afapp, null);//查询设置校验
		if(flist.size() != 0){
			for(String str : flist){
				fsce.getList().add(new DataDefineException(1,str,str));
			}
		}
		if(fsce.getList().size() != 0)
			throw fsce;
		fam.setState(state);
		afapp.unloadAppHibernatResorece();
		FomObjaccess fo = new FomObjaccess();
		fo.setRefAppmainId(id);
		List objacclist = getFormDaoManager().queObjAccessByCondition(fo);
		for(int i=0;i<objacclist.size();i++){
			fo = (FomObjaccess)objacclist.get(i);
			fo.setState(state);
			getFormDaoManager().updateObjAccess(fo);
		}
		
		//ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(id);
//		if(afapp!=null){
//			SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
//			tablelst = seeyon.getDataDefine().getTableLst();
//		}else if(afapp == null){
//			  afapp = new SeeyonForm_ApplicationImpl();
//			  afapp.setAppName(aAppName);
//              //08-05-14修改
//	    	  afapp.setFId(fam.getId());
//
//			  afapp.loadFromDB();
//			  SeeyonDataDefine seeyon = (SeeyonDataDefine)afapp.getDataDefine();
//			  tablelst = seeyon.getDataDefine().getTableLst();
//			  afapp.unloadAppHibernatResorece();
//	    }
		//是否清空运行数据
		/*
		if(isDelete){
			FormTableValue ftv = new FormTableValue();
			ftv.setSheetName(id);
			//用于清空数据库中存在的表所有数据
			tablelst = getFormDaoManager().queryTableValue(ftv);				
		}*/
		Document doc = null;
		doc  = dom4jxmlUtils.paseXMLToDoc(fam.getBindInfo());
		//执行更新状态的操作
		updateAppState(fam);
		
		//是否清空运行数据
		if(isDelete){
			deletealldata(tablelst);
		}
		//执行绑定信息的更改
		try{
			if(doc.getRootElement().hasMixedContent() && fam.getFormType() == TAppBindType.FLOWTEMPLATE.getValue()){
				BindHelper bh = new BindHelper();
				bh.updateTemplateState(parseBindinfo(doc.getRootElement()), String.valueOf(state));
			}
		}catch(Exception e){
			//throw new DataDefineException(1,"绑定信息更新状态失败！","绑定信息更新状态失败！");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.bindinfoupdatefailure.label"),Constantform.getString4CurrentUser("form.base.bindinfoupdatefailure.label"));
		}
		
		
		//加载到app 当预发布时 加载，发布时不再重复加载（不考虑直接发布的情况）
		//2007-7-28康宇涛修改逻辑：加直接发布逻辑
		//if(state == 1)
		regApp(fam.getName(),fam.getId());
		
		/*//添加信息管理绑定信息 by wusb at 2010-04-25
		if(fam.getFormType()==ISeeyonForm.TAppBindType.INFOMANAGE.getValue()){
			BindHelper.saveMenuProfile(fam.getId());
		}*/
	}	
	/**
	 * 解析bindinfo方法
	 * @param bindinfo
	 * @return
	 * @throws DataDefineException
	 */
	private List parseBindinfo(Element bindinfo) throws DataDefineException{
		List idslst = new ArrayList();
		if (!bindinfo.getName().equalsIgnoreCase(IXmlNodeName.Bind))
			//throw new DataDefineException(1,"bind信息不完整！","bind信息不完整！");
			throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.bindinfonotfull.label"),Constantform.getString4CurrentUser("form.base.bindinfonotfull.label"));
		Element ftemp = bindinfo.element(IXmlNodeName.NodeName_FlowTempletList);
		List templatelist = ftemp.elements(IXmlNodeName.NodeName_FlowTemplet);
		for (Object fitem : templatelist) {
			Element tempele= (Element) fitem;
			Attribute id = tempele.attribute(IXmlNodeName.id);
			idslst.add(Long.valueOf(id.getValue()));
		}
		return idslst;
	}
	/**
	 * 加载app到管理器
	 * 返回绑定信息，供绑定设置用
	 * @param appName
	 * @throws SeeyonFormException
	 */
	private void regApp(String appName,Long appId) throws SeeyonFormException {
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		SeeyonForm_ApplicationImpl sapp = new SeeyonForm_ApplicationImpl();
		sapp.setAppName(appName);
        //08-05-14修改
		sapp.setFId(appId);

		sapp.loadFromDB();
		//如果是从预发布到发布,不往app中注册
//		if(fmanager.findByName(appName) == null)
//		sapp.unloadAppHibernatResorece();
//			fmanager.regApp(sapp);
		if(fmanager.findById(appId,true) == null)
//			sapp.unloadAppHibernatResorece();
				fmanager.regApp(sapp);
	}
	
	
	private void editregApp(FormAppMain fAppMain) throws SeeyonFormException {
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		SeeyonForm_ApplicationImpl sapp = new SeeyonForm_ApplicationImpl();
		sapp.setAppName(fAppMain.getName());
		sapp.loadFromDB(fAppMain);
		SeeyonFormCheckException fsce = new SeeyonFormCheckException(2);
		List<String> flist = null;
		Map<String, String> queryListMap = new HashMap<String, String>();
		Map<String, String> reportListMap = new HashMap<String, String>();
		Element dataStructure = null;
		flist = this.checkQueryList(sapp, queryListMap);//查询设置校验
		if(flist.size() != 0){
			for(String str : flist){
				fsce.getList().add(new DataDefineException(1,str,str));
			}
			if(dataStructure==null)
				dataStructure = getDataStructure(fAppMain);
			List queryElementList = dataStructure.element("QueryList").elements("Query");
			
			for(Object item : queryElementList){
				
				Element felement = (Element)item;
				if(queryListMap.get(felement.attribute("name").getValue()) != null){//查询校验错误
					felement.addAttribute("valid", "false");
				}
			}
			
		}
		flist = this.checkReportList(sapp, reportListMap);//统计设置校验
		if(flist.size() != 0){
			for(String str : flist){
				fsce.getList().add(new DataDefineException(1,str,str));
			}
			if(dataStructure==null)
				dataStructure = getDataStructure(fAppMain);
			List reportElementList = dataStructure.element("ReportList").elements("Report");
			
			for(Object item : reportElementList){
				
				Element felement = (Element)item;
				if(reportListMap.get(felement.attribute("name").getValue()) != null){//统计校验错误
					felement.addAttribute("valid", "false");
				}
			}
			
		}
		
		if(fsce.getList().size() != 0){
			sapp.unloadAppHibernatResorece();
			FormAppMain appMain = getFormDaoManager().findApplicationById(sapp.getId());
			appMain.setDataStructure(dataStructure.asXML());
			getFormDaoManager().updateAppMain(appMain);
			sapp = new SeeyonForm_ApplicationImpl();
			sapp.setAppName(appMain.getName());
            sapp.setFId(appMain.getId());
            boolean sign = false;
            try{
            	sapp.loadFromDB();
    			if(fmanager.findById(fAppMain.getId(),true) == null){
    				fmanager.regApp(sapp);
    				sign = true;
    			}else{
    			  sapp.unloadAppHibernatResorece();
    			  sign = true;
    			}
			  } catch(Exception e){
				log.error("修改表单定义解析错误", e);
			  }finally{
				  if(!sign)
				    sapp.unloadAppHibernatResorece();
			  }
			
			throw fsce;
		}
		//如果是从预发布到发布,不往app中注册
		//if(fmanager.findByName(fAppMain.getName()) == null)
		//更改唯一标识为id
		if(fmanager.findById(fAppMain.getId(),true) == null)
//		sapp.unloadAppHibernatResorece();
			fmanager.regApp(sapp);
	}
	
	private Element getDataStructure(FormAppMain appMain)throws SeeyonFormException{
		String dataStructure = appMain.getDataStructure();
		String ftemp = SeeyonForm_Runtime.getInstance().getCharset().DBOut2JDK(dataStructure);
		String fXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"+ftemp;
		Document fdocDataStructure = dom4jxmlUtils.paseXMLToDoc(fXml);
		return fdocDataStructure.getRootElement();
	}
	
	/**
	 * 更新formmainapp纪录的状态
	 * @param fam
	 * @throws SeeyonFormException
	 */
	private void updateAppState(FormAppMain fam) throws SeeyonFormException{
		getFormDaoManager().updateAppMain(fam);
	}
	/**
	 * 删除一条表单纪录
	 * @param tablelst
	 * @throws SeeyonFormException
	 */
	private void deletealldata(List tablelst) throws SeeyonFormException{	
		if(tablelst != null){
			//执行删除操作
			List<String> strBatch = new ArrayList<String>();
			for(int i=0;i<tablelst.size();i++){
				FormTable ftvv = (FormTable)tablelst.get(i);
				StringBuffer sb = new StringBuffer();
				sb.append("delete from ");
				sb.append(ftvv.getName());
				//oracle中加;有错误，暂时去掉
				//sb.append(";");
				strBatch.add(sb.toString());
			}
			try {
				getFormDaoManager().execSQLList(strBatch);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//throw new DataDefineException(1,"执行数据删除出错！","执行数据删除出错！");			
				throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.executedatawrong.label"),Constantform.getString4CurrentUser("form.base.executedatawrong.label"));
			}		
		}
	}
	
	public String nonamespacereplaceXml(String xml,SessionObject sessionobject){
		String[] noequalfield =null;
        //已匹配数据列表中不相等字段的(新表名↖旧表名/新字段名↗旧字段名)
		if(!"".equals(sessionobject.getNoequalvalue())){
			noequalfield = sessionobject.getNoequalvalue().split(",");
	    }
		for(int i=0;i<noequalfield.length ; i++){ 
			//FIXME FindBug: Dead Return Value.
	    	xml.replace("\""+AddFieldName(noequalfield[i])+"\"","\""+OperHelper.noEqualfieldName(noequalfield[i])+"\""); 	
	    }		
		return xml;	
	}
	public String replaceXml(String xml,SessionObject sessionobject){
		String[] noequalfield =null;
        //已匹配数据列表中不相等字段的(新表名↖旧表名/新字段名↗旧字段名)
		String namespace = sessionobject.getNamespace();
		String ftemp = null;
		noequalfield = sessionobject.getNoequalvalue().split(",");	
		for(int i=0;i<noequalfield.length ; i++){
			ftemp = xml.replace("\""+namespace+AddFieldName(noequalfield[i])+"\"","\""+namespace+OperHelper.noEqualfieldName(noequalfield[i])+"\""); 	
		}	
		return ftemp;	
	}
	
	private SessionObject query(SessionObject sessionobject,ISeeyonForm_Application afapp,FormAppMain fam,User user,List<FormAppMain> formapplist){
		try{
			for(int j=0;j<afapp.getQueryList().size();j++){
				  FormAppMain ftemfam = new FormAppMain();
				  ftemfam.setId(fam.getId());
				  ftemfam.setName(fam.getName());
				  ftemfam.setCategoryName(fam.getCategoryName());
				  ftemfam.setCategory(fam.getCategory());
				  ftemfam.setFormstart(fam.getFormstart());
				  SeeyonQueryImpl seyonquery =(SeeyonQueryImpl)afapp.getQueryList().get(j); 
				  ftemfam.setQueryname(seyonquery.getQueryName());
				  ftemfam.setQueryId(seyonquery.getId());
				  Description descrip=seyonquery.getDescription();
				  if(descrip.getDescription()!=null){
					  ftemfam.setQuerydescription(descrip.getDescription().replace("\n", " "));
				  }		 
				  if(seyonquery.getValid().equals("true") && checkAccess(user, afapp.getId(), ftemfam.getQueryname(), 1)){//1为查询		
				  //暂时不校验是否有查询的权限
			     //if(seyonquery.getValid().equals("true")){//1为查询		
                    formapplist.add(ftemfam);	
                    seyonquery.setFormname(fam.getName());
				    sessionobject.getQuerylist().add(seyonquery);
				    QueryObject queryobject = new QueryObject();
					queryobject.setQuery(seyonquery);
					queryobject.setFormname(fam.getName());
					sessionobject.getQueryConditionList().add(queryobject);	
				 }
			  }
			  sessionobject.setFormName(fam.getName());
			  sessionobject.setFormid(fam.getId());
			  //sessionobject = regroupQueryObject(afapp.getQueryList(),sessionobject,String.valueOf(fam.getId()),fam.getName());

		}catch(Exception e){
			log.error("",e);
		}
		
		return sessionobject;
	}
	/**
	 * 用于装配应用名称,查询名称,查询描述
	 * @param categorylst
	 * @param user 当前登陆用户
	 * @return
	 * @throws BusinessException 
	 * @throws SeeyonFormException 
	 */
	public List assignQuery(List categorylst,SessionObject sessionobject, User user) throws BusinessException, SeeyonFormException{
		List <FormAppMain> formapplist = new ArrayList<FormAppMain>();
		sessionobject.getQueryConditionList().clear();
		sessionobject.getQuerylist().clear();
		sessionobject.getQueryseachlist().clear();
		if(categorylst == null) 
			return null;
		
		for(int i=0;i<categorylst.size();i++){
			FormAppMain fam = (FormAppMain)categorylst.get(i);	
			//int value =Integer.valueOf(fam.getCategory().toString()).intValue();
			//String values = fam.getCategory().toString();
			//MetadataManager metadataManager  = (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
			//String catename = metadataManager.getMetadataItem("form_affiliatedsort", values).getDescription();		
			String catename="";
			//表单业务配置所挂接的首页栏目在此处取不到templeteCategoryManager，抛空指针异常，加一个防护 added by Meng Yang at 2009-08-12
			if(templeteCategoryManager==null) {
				templeteCategoryManager = (TempleteCategoryManager)ApplicationContextHolder.getBean("templeteCategoryManager");
			}
			if(templeteCategoryManager.get(fam.getCategory()) !=null)
				catename = templeteCategoryManager.get(fam.getCategory()).getName();
			fam.setCategoryName(catename);
			sessionobject.setFormsort(fam.getCategory());
            if(fam.getDataStructure().indexOf("</Query>") !=-1){
            	if(fam.getState()==1 ||fam.getState() == 2){
            		 //ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(fam.getName());
            		//08年5月14日修改，注册唯一标识为id
            		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(fam.getId());  
            		 if(afapp!=null){
		            		 if(afapp.getQueryList().size()!=0){
		            			 try{
		            				 query(sessionobject,afapp,fam,user,formapplist); 
		            			 }
		            			 catch(Exception e){
		            				log.error("表单定义解析出错", e);
		            			 }
		            			
			   				  }		
            		 }else if(afapp == null){
	       				  afapp = new SeeyonForm_ApplicationImpl();
	    				  afapp.setAppName(fam.getName());
	                      //08-05-14修改
	    				  afapp.setFId(fam.getId());
	    				  try{
	    					  afapp.loadFromDB();  
	    					  if(afapp.getQueryList().size()!=0){
	        					 query(sessionobject,afapp,fam,user,formapplist);	
	    	   				  }	
	    				  }catch(Exception e){
	            				log.error("表单定义解析出错", e);
	           			  }finally{
	         				 afapp.unloadAppHibernatResorece();
	        			  } 
    		        }
	            }            	
    	   }
		}
		sessionobject.setQueryseachlist(formapplist);
		return formapplist;
	}
	
	
	/**
	 * 用于装配应用名称,统计名称,统计描述
	 * @param categorylst
	 * @param user 当前登陆用户
	 * @return
	 * @throws BusinessException 
	 * @throws SeeyonFormException 
	 */
	public List assignReport(List categorylst,SessionObject sessionobject, User user) throws BusinessException, SeeyonFormException{
		List <FormAppMain> formapplist = new ArrayList<FormAppMain>();
		sessionobject.getReportlist().clear();
		sessionobject.getReportConditionList().clear();
		sessionobject.getReportseachlist().clear();
		if(categorylst == null) return null;
		for(int i=0;i<categorylst.size();i++){
			FormAppMain fam = (FormAppMain)categorylst.get(i);
			//int value =Integer.valueOf(fam.getCategory().toString()).intValue();
			//String values = fam.getCategory().toString();
			//MetadataManager metadataManager  = (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
			//String catename = metadataManager.getMetadataItem("form_affiliatedsort", values).getDescription();		
			String catename="";
			//业务表单中统计按钮，偶尔会报空指针，这里做防护
			if(templeteCategoryManager==null) {
				templeteCategoryManager = (TempleteCategoryManager)ApplicationContextHolder.getBean("templeteCategoryManager");
			}
			if(templeteCategoryManager.get(fam.getCategory()) !=null)
				catename = templeteCategoryManager.get(fam.getCategory()).getName();
			fam.setCategoryName(catename);
			sessionobject.setFormsort(fam.getCategory());
			if(fam.getDataStructure().indexOf("</Report>") !=-1){
				if(fam.getState()==1 ||fam.getState() == 2){
				  //ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(fam.getName());
            		//08年5月14日修改，注册唯一标识为id
            		ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(fam.getId());  
					if(afapp!=null){	  
				        if(afapp.getReportList().size()!=0){
				        	try{
				        		report(sessionobject,afapp,fam,user,formapplist);
				        	} catch(Exception e){
		          				log.error("表单定义解析出错", e);
		         			 }	
						}
				  }else if(afapp == null){
					  afapp = new SeeyonForm_ApplicationImpl();
					  afapp.setAppName(fam.getName());
		              //08-05-14修改
			    	  afapp.setFId(fam.getId());
                      try{
                    	  afapp.loadFromDB(); 			 
					  if(afapp.getReportList().size()!=0)
						 report(sessionobject,afapp,fam,user,formapplist); 				 
                      } catch(Exception e){
	          				log.error("表单定义解析出错", e);
	         		  }	
                      finally{
          				 afapp.unloadAppHibernatResorece();
          			  }
			      }
				}
			}		  
		}	
		sessionobject.setReportseachlist(formapplist);
		return formapplist;
	}
	

	private SessionObject report(SessionObject sessionobject,ISeeyonForm_Application afapp,FormAppMain fam,User user,List<FormAppMain> formapplist) {
	
		try{
			for(int j=0;j<afapp.getReportList().size();j++){
				  FormAppMain ftemfam = new FormAppMain();
				  ftemfam.setId(fam.getId());
				  ftemfam.setName(fam.getName());
				  ftemfam.setCategoryName(fam.getCategoryName());
				  ftemfam.setCategory(fam.getCategory());
				  ftemfam.setFormstart(fam.getFormstart());
				  SeeyonReportImpl seyonreport =(SeeyonReportImpl)afapp.getReportList().get(j);
				  ftemfam.setReportname(seyonreport.getReportName());
				  ftemfam.setReportId(seyonreport.getId());
				  Description descrip=seyonreport.getDescription();
				  if(descrip.getDescription()!=null){
					  ftemfam.setReportdescription(descrip.getDescription().replace("\n", " ")); 
				  }			  
				  //权限验证
				 if(checkAccess(user, afapp.getId(), ftemfam.getReportname(), 2) && seyonreport.getValid().equals("true")){//2为统计
				  //if(seyonreport.getValid().equals("true")){//2为统计
				      formapplist.add(ftemfam);	
				      seyonreport.setFormname(fam.getName());
				  	  sessionobject.getReportlist().add(seyonreport);
				  	  ReportObject reportobject = new ReportObject();
				      reportobject.setReport(seyonreport);
					  reportobject.setFormname(fam.getName());
					  sessionobject.getReportConditionList().add(reportobject);	
				  }
			  }
			  sessionobject.setFormName(fam.getName());
			  sessionobject.setFormid(fam.getId());
			  //sessionobject = regroupReportObject(afapp.getReportList(),sessionobject,String.valueOf(fam.getId()),fam.getName());
		}
		catch(Exception e){
			log.error("", e);
		}
		
		return sessionobject;
	}
	
	public SessionObject systemenum(SessionObject sessionobject) throws SeeyonFormException{
		long accountId = CurrentUser.get().getLoginAccount();
		ISystemValueManager fsysvaluemanager = SeeyonForm_Runtime.getInstance().getSystemValueManager();
		List datesystemvaluelist = new ArrayList();
		List denglusystemlist = new ArrayList();
		List qitasystemlist = new ArrayList();
		List userFlowIdList = new ArrayList();
		List systemlist = new ArrayList();
		if(fsysvaluemanager.getNames() != null && fsysvaluemanager.getNames().size() !=0){
			for(int i =0;i<fsysvaluemanager.getNames().size();i++){
				ISeeyonSystemValue seeyon = fsysvaluemanager.findByName(fsysvaluemanager.getNames().get(i)); 
				if(fsysvaluemanager.getNames().get(i).indexOf("登录") >-1){					
					if(seeyon instanceof UserFlowId){
						addUserFlowIdToList(sessionobject,userFlowIdList,(UserFlowId)seeyon) ;                 	                		
					}else{
						denglusystemlist.add(fsysvaluemanager.getNames().get(i));
					}		
				}else if(seeyon.getValueType().equalsIgnoreCase("svtDate")){					
					if(seeyon instanceof UserFlowId){
						addUserFlowIdToList(sessionobject,userFlowIdList,(UserFlowId)seeyon) ;              	                	
					}else{
						datesystemvaluelist.add(fsysvaluemanager.getNames().get(i));
					}		
				}else{
					if(seeyon instanceof UserFlowId){
						addUserFlowIdToList(sessionobject,userFlowIdList,(UserFlowId)seeyon) ;         	
					}else{
						qitasystemlist.add(fsysvaluemanager.getNames().get(i));
					}				
				}
			}
		}
		
		if (fsysvaluemanager != null) {
			for (int i = 0; i < denglusystemlist.size(); i++) {
				systemlist.add(denglusystemlist.get(i));
			}
			for (int i = 0; i < datesystemvaluelist.size(); i++) {
				systemlist.add(datesystemvaluelist.get(i));
			}
			for (int i = 0; i < qitasystemlist.size(); i++) {
				systemlist.add(qitasystemlist.get(i));
			}
			for (int i = 0; i < userFlowIdList.size(); i++) {
				systemlist.add(userFlowIdList.get(i));
			}
			sessionobject.setSysVariable(systemlist);
		}
		IInputExtendManager fextendmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
		if(fextendmanager != null){
			//add by yuhj at 2008-9-20 如果是企业版和政务版不显示单位扩展控件
			List<String> names = fextendmanager.getsortNames();
			if(!FormHelper.showAccount()) {
				names.remove("选择单位");
			}
			sessionobject.setInputExtend(names);	
		}
		
		FormatManager formatManager = SeeyonForm_Runtime.getInstance().getFormatManager() ;
		if(formatManager != null){
			sessionobject.setDataFormat(formatManager.getAllFormatType()) ;
		}
		
		List enumlst = new ArrayList(); 
/*		List<FormEnumImpl> systemenumlist = new ArrayList<FormEnumImpl>(); 
		List<FormEnumImpl> appenumlist = new ArrayList<FormEnumImpl>();
		List<FormEnumImpl> formenumlist = new ArrayList<FormEnumImpl>();
*/		/**0:系统枚举;1:表单枚举;2:应用枚举**/
		//系统枚举
	/*	IFormEnumManager fformenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etSystem, Long.valueOf("0"));
		if(fformenummanager != null){
			for(int i=0;i<fformenummanager.getEnumList().size();i++){
				FormEnumImpl formenum = (FormEnumImpl)fformenummanager.getEnumList().get(i);
				if(formenum.getValueList().size()!=0)
					systemenumlist.add(formenum);
			}
			//enumlst.add(fformenummanager.getEnumList());	
			enumlst.add(systemenumlist);
		}else{
			//保证注入系统枚举
			enumlst.add(new ArrayList());
		}
		//表单枚举
		IFormEnumManager 	formmangerenum = null;	
		//应用枚举
		IFormEnumManager fenummanager = null;
		FormEnumManagerHashMap formenumhash = new FormEnumManagerHashMap();
		//fenummanager = SeeyonForm_Runtime.getInstance().getEnumManager(TFormEnumType.etApplication, Long.valueOf(sessionobject.getFormsort()));
			//应用枚举
			/*if(fenummanager != null){
				//enumlst.add(fenummanager.getEnumList());
				for(int i=0;i<fenummanager.getEnumList().size();i++){
					FormEnumImpl formenum = (FormEnumImpl)fenummanager.getEnumList().get(i);
					if(formenum.getValueList().size()!=0)
						appenumlist.add(formenum);
				}
				enumlst.add(appenumlist);
			}else{
				//保证注入应用枚举
				enumlst.add(new ArrayList());
			}*/
		/*
			ISeeyonForm_Application app = SeeyonForm_Runtime.getInstance().getAppManager().createApplication();
			if(app != null){
				//表单枚举
				formmangerenum = app.getFormEnumManager();
				if(formmangerenum != null){
					//enumlst.add(formmangerenum.getEnumList());
					for(int i=0;i<formmangerenum.getEnumList().size();i++){
						FormEnumImpl formenum = (FormEnumImpl)formmangerenum.getEnumList().get(i);
						if(formenum.getValueList().size()!=0)
							formenumlist.add(formenum);
					}
					enumlst.add(formenumlist);
				}else{
					//保证注入表单枚举			
					enumlst.add(new ArrayList());
				}
			}else{
				//保证注入表单枚举					
				enumlst.add(new ArrayList());
			}
				*/
	
	
	    sessionobject.setEnumlist(enumlst);
	
		return sessionobject;
	}
	
	private void addUserFlowIdToList(SessionObject sessionobject ,List list,UserFlowId seeyon )throws SeeyonFormException{
		if(seeyon instanceof UserFlowId){
			long accountId = CurrentUser.get().getLoginAccount();
			UserFlowId userflow = (UserFlowId) seeyon;
        	List<FormFlowid>  flowlist =  queryFlowIdByVariableName(userflow.getVariablename(),accountId);
        	if(flowlist != null && !flowlist.isEmpty()){	                		
        		for(FormFlowid formFlowid : flowlist){
        			if(formFlowid.getAccountId() != null && formFlowid.getAccountId().longValue() != accountId){
        				continue ;
        			}
        			String formFlowId =  formFlowid.getId().toString();
        			if(!list.contains(formFlowId)){
	                	if("edit".equals(sessionobject.getEditflag())){	 
	                		String appName = formFlowid.getAppname();
	                		
	                		if(Strings.isNotBlank(appName) && appName.startsWith("inputData_")){
	                			appName = appName.substring("inputData_".length());
	                		}
	                		if(Strings.isBlank(appName) || sessionobject.getFormid().equals(Long.parseLong(appName))){		                			
	                			list.add(formFlowId);
	                		}
	                	}else{
	                		if(!"Y".equalsIgnoreCase(formFlowid.getState())){
	                			list.add(formFlowId);
		                	}
	                	} 
        			}
        		}

        	}	
		}
	}
	
	
	/*
	 * 检验有无执行查询或统计条件的权限
	 * user 当前登陆用户
	 * appId 应用id
	 * objectName 查询或统计条件名称
	 * objectType 类型
	 */
	public boolean checkAccess(User user, Long appId, String objectName,int objectType ) throws DataDefineException, BusinessException{
		FormAccessDao fad = new FormAccessDao();
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List list = getFormDaoManager().queryObjAccessByCondition(appId.longValue(), objectName, objectType);
		FomObjaccess foa = null;	
		if(list != null){
			for(int i = 0; i < list.size(); i++){
				foa = (FomObjaccess)list.get(i);
				if(foa.getUserid() == null)
					return false;
				long userId = foa.getUserid().longValue();
				int userType = foa.getUsertype();
				if(userType == 0){//如果是memberid
					if(user.getId() == userId)
						return true;
				}else if(userType == 1){//如果是Department
					List<Long> departList =orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
					if(departList != null){
						for(Long item : departList){
							if(item.longValue() == userId)
								return true;
						}
					}
				}else if(userType == 2){//如果是Team
					//得到组list
					List<Long> teamIdList = orgManager.getUserDomainIDs(user.getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
					if(teamIdList != null){
						for(Long item : teamIdList){
							if(item.longValue() == userId)
								return true;
						}
					}
					
				}else if(userType == 3){//如果是Level
					List<Long> levelList = orgManager.getUserDomainIDs(user.getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_LEVEL);
					if(levelList != null){
						for(Long item : levelList){
							if(item.longValue() == userId)
								return true;
						}
					}
				}else if(userType == 4){//如果是Post
					List<Long> postList = orgManager.getUserDomainIDs(user.getId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_POST);
					if(postList != null){
						for(Long item : postList){
							if(item.longValue() == userId)
								return true;
						}
					}
				}else if(userType == 5){//如果是Account
					List<Long> accountList = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
					if(accountList != null){
						for(Long item : accountList){
							if(item.longValue() == userId)
								return true;
						}
					}
				}else if(userType == 6){//如果是不包含子部门的Department
					List<V3xOrgDepartment> departList =orgManager.getDepartmentsByUser(user.getId());
					if(departList != null){
						for(V3xOrgDepartment dept : departList){
							if(dept.getId() == userId)
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 从数据库中获取用户有权查看的表单查询或表单统计模板 added by Meng Yang at 2009-09-21
	 */
	public List<FomObjaccess> getFormQueryOrReportNamesByAppId4User(long refAppmainId, List<Long> domainIds, boolean isAdmin) throws DataDefineException {
		return getFormDaoManager().getFormQueryOrReportNamesByAppId4User(refAppmainId, domainIds, isAdmin);
	}
	
	/**
	 * 重载权限过滤方法，改为使用一条sql语句获取总数判断用户是否具有对特定表单查询或表单统计模板的权限 added by Meng Yang at 2009-09-21
	 */
	public boolean checkAccess4BizConfig(List<Long> domainIds, Long appId, String objectName,int objectType ) throws DataDefineException, BusinessException {
		int count = getFormDaoManager().getTotalCount4Access(appId.longValue(), objectName, objectType, domainIds);
		return count>0;
	}
	
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
	/**
	 * 获得系统日期
	 * @return
	 */
	public String Systemdata(){
		Date aDate = new Date();
		return Datetimes.formatDate(aDate);
	}
	
	public String SystemdataTime(){
		return Datetimes.formatDatetime(new java.util.Date());
	}
	
public String checkFormFields(SessionObject sessionobject,HttpServletRequest request) throws SeeyonFormException{
		
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		SeeyonForm_ApplicationImpl oldApp = (SeeyonForm_ApplicationImpl)fmanager.findById(sessionobject.getFormid());
		RuntimeCharset fCharset=SeeyonForm_Runtime.getInstance().getCharset();	
				
		//此处为旧的session中的datadefine的element
		DataDefine dd = sessionobject.getData();
		if(oldApp == null ){
			oldApp = (SeeyonForm_ApplicationImpl)fmanager.createApplication();
			oldApp.setAppName(sessionobject.getFormName());
            //08-05-14修改
			oldApp.setFId(sessionobject.getFormid());
			try{
				oldApp.loadFromDB();
			}catch(Exception e){
  				log.error("表单定义解析出错", e);
 		    }finally{
 				 oldApp.unloadAppHibernatResorece();
			} 	
		}
		Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)oldApp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
		String thispage = (String)request.getParameter("thispage");
		//此处插入收集数据代码
		if(thispage.equalsIgnoreCase(IPagePublicParam.BASEINFO)){
			OperHelper.baseInfoCollectData(request, sessionobject);
		}else if(thispage.equalsIgnoreCase(IPagePublicParam.INPUTDATA)){
			OperHelper.inputDataCollectData(request, sessionobject);
		}else if(thispage.equalsIgnoreCase(IPagePublicParam.BINDINFO)){
			//添加信息管理绑定信息 by wusb at 2010-03-17
			BindHelper.systemSaveAppBindMain(request, sessionobject);
		}
		List tablefieldlst = sessionobject.getTableFieldList();
		SeeyonFormImpl sfi = new SeeyonFormImpl();
		UserDefineXMLInfoImpl fxml=new UserDefineXMLInfoImpl();
		ChangeObjXml cox = new ChangeObjXml();	
		
		//组织all.xml
		Map map = new HashMap();
		//08-05-19修改增加id
		map.put("FormId", sessionobject.getFormid());
		map.put("FormName", sessionobject.getFormName());
		map.put("DataDefine", sessionobject.getData());
		map.put("FormList", sessionobject.getFormLst());

		String ftemp = cox.createSeeyonDataDefineXml(2,map,sessionobject);		
		ftemp=fCharset.SystemDefault2SelfXML(ftemp);
		fxml.setSeeyonFomDefineXML(ftemp);

		IFormResoureProvider fResourceProvider = null;
		
		Document doc = null;
		doc  = dom4jxmlUtils.paseXMLToDoc(fxml.getSeeyonFomDefineXML());
		Element root = doc.getRootElement();
		// 准备资源文件
		//组织新的datadefine  xml文件
		SeeyonForm_ApplicationImpl newApp = new SeeyonForm_ApplicationImpl();
		Element dataroot = newApp.loadDataDefine(root, fResourceProvider);
		
		//		boolean bPublish;//app是否已发布
//		if(sessionobject.getFormstate().equals("0"))
//			bPublish = false;
//		else
//		    bPublish = true;
		
		List<String> checkList = oldApp.getDataDefine().checkFormFields(dataroot, oldroot, oldApp.getSessionFactory(), sessionobject.getFormstate());
		String str = "";
		for(String fieldCheck : checkList){
			str += fieldCheck;
			str +=",";
		}
		if(str.length() != 0)
			str = str.substring(0, str.length() - 1);
		return str;
	}
	
   public List queryreportlist(List<Long> formobjlist,int objtype,Long userid) throws BusinessException{
	    long userId = CurrentUser.get().getId();
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List<Long> teamIdList = orgManager.getUserDomainIDs(userId,V3xOrgEntity.ORGENT_TYPE_TEAM);
		List<Long> departList =orgManager.getUserDomainIDs(userId,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
		List<Long> postList = orgManager.getUserDomainIDs(userId,V3xOrgEntity.ORGENT_TYPE_POST);	
		List<Long> levelList = orgManager.getUserDomainIDs(userId,V3xOrgEntity.ORGENT_TYPE_LEVEL);
		List<Long> accountList = orgManager.getUserDomainIDs(userId,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);	
//		FomObjaccess bm = new FomObjaccess();
//		bm.setUserid(userId);
//		bm.setObjecttype(objtype);
		formobjlist.add(userId);	
		
		if(departList != null){
			for(Long item : departList){
//				FomObjaccess bm1 = new FomObjaccess();
//				bm1.setUserid(item.longValue());
//				bm1.setObjecttype(objtype);
				formobjlist.add(item.longValue());
			}
	    }	
		if(postList != null){
			for(Long item : postList){
//				FomObjaccess bm2 = new FomObjaccess();
//				bm2.setUserid(item.longValue());
//				bm2.setObjecttype(objtype);
				formobjlist.add(item.longValue());	
			}
	    }	
		if(levelList != null){
			for(Long item : levelList){
//				FomObjaccess bm3 = new FomObjaccess();
//				bm3.setUserid(item.longValue());
//				bm3.setObjecttype(objtype);
				formobjlist.add(item.longValue());		
			}
	    }		
		List applst1 = null;			
		if(teamIdList != null){
			for(Long item : teamIdList){
//				FomObjaccess bm4 = new FomObjaccess();
//				bm4.setUserid(item.longValue());
//				bm4.setObjecttype(objtype);
				formobjlist.add(item.longValue());
			}
	    }
		
		if(accountList != null){
			for(Long item : accountList){
//				FomObjaccess bm5 = new FomObjaccess();
//				bm5.setUserid(item.longValue());
//				bm5.setObjecttype(objtype);
				formobjlist.add(item.longValue());
			}
		}
		return formobjlist;
   }
 
   public synchronized void formenumeditifuse(SessionObject sessionobject) throws DataDefineException{
	   MetadataManager metadataManager = (MetadataManager) ApplicationContextHolder.getBean("metadataManager");
	    sessionobject.getOldenumnamemap();
	    String enumid = "";
	    if(sessionobject.getEnumnamemap()!=null){
	    	if(sessionobject.getEnumnamemap().size()!=0){
	    		HashMap hash =  sessionobject.getEnumnamemap();
	    		Iterator it = hash.entrySet().iterator();
	    		while(it.hasNext()){	    			
	    			Map.Entry entry = (Map.Entry)it.next();
	    			enumid = entry.getKey().toString();
//	    			0代表用户自定义枚举
	    			metadataManager.refMetadata(Long.parseLong(enumid), 0);
	    		}
	    	}
	    }
	    
   }
   
   public synchronized void formenumnewifuse(SessionObject sessionobject) throws DataDefineException{
	   MetadataManager metadataManager = (MetadataManager) ApplicationContextHolder.getBean("metadataManager");
	    String enumid = "";	    
	    if(sessionobject.getEnumnamemap()!=null){
	    	if(sessionobject.getEnumnamemap().size()!=0){
	    		HashMap hash =  sessionobject.getEnumnamemap();
	    		Iterator it = hash.entrySet().iterator();
	    		while(it.hasNext()){
	    			Map.Entry entry = (Map.Entry)it.next();
	    			enumid = entry.getKey().toString();
	    			//0代表用户自定义枚举
	    			metadataManager.refMetadata(Long.parseLong(enumid), 0);
	    		}
	    	}
	    }
	    
  }
  
   private String xmlHead1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   private SessionObject formqueryenum(SessionObject sessionobject) throws SeeyonFormException{
	   List querylist = sessionobject.getQueryConditionList();
		for(int i = 0;i<querylist.size();i++){
			QueryObject query = (QueryObject)querylist.get(i);
			Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(query.getQueryConditionValue()));
			Element QueryConditionRoot = QueryConditionDoc.getRootElement();
			ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
			userConditionList.loadFromXml(QueryConditionRoot);
			List conditionList = userConditionList.getConditionList();
			ICondition fitem;
			QueryUserConditionDefin userconditon = null;
			if(conditionList !=null){
				for(int a= 0; a < conditionList.size(); a++){
					fitem = (ICondition)conditionList.get(a);
					if(fitem instanceof QueryUserConditionDefin){
						userconditon = (QueryUserConditionDefin)fitem;
						ConditionInput input = (ConditionInput)userconditon.getConditionInput();
						if(input!=null){
							if(input.getEnumName()!=null && !"null".equals(input.getEnumName()) && !"".equals(input.getEnumName()))
						        sessionobject.getOldenumnamemap().put(input.getEnumid(), input.getEnumid());
						}
					}
				}
			}						
		}
		List reportlist = sessionobject.getReportConditionList();
		for(int i = 0;i<reportlist.size();i++){
			ReportObject report = (ReportObject)reportlist.get(i);
			Document ReportConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(report.getconditionvalue()));
			Element ReportConditionRoot = ReportConditionDoc.getRootElement();
			ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
			userConditionList.loadFromXml(ReportConditionRoot);
			List conditionList = userConditionList.getConditionList();
			ICondition fitem;
			QueryUserConditionDefin userconditon = null;
			if(conditionList !=null){
				for(int a= 0; a < conditionList.size(); a++){
					fitem = (ICondition)conditionList.get(a);
					if(fitem instanceof QueryUserConditionDefin){
						userconditon = (QueryUserConditionDefin)fitem;
						ConditionInput input = (ConditionInput)userconditon.getConditionInput();
						if(input!=null){
							if(input.getEnumName()!=null && !"null".equals(input.getEnumName()) && !"".equals(input.getEnumName()))
						        sessionobject.getOldenumnamemap().put(input.getEnumid(), input.getEnumid());
						}
					}
				}
			}						
		}
	return sessionobject;	    
   }
   
   
//	去掉字符串最后的/r/n
	private String delTrailSection(String str){
		
		if(str.endsWith("/r/n"))
			return str.substring(0,str.length() - 4);
		else
			return str;
			
		
	}
	
	
	/**
	 * 流水号被调用后更新state和所属表单id 
	 * @param sessionobject
	 * @throws DataDefineException
	 */
	public void updateFlowIdstate(SessionObject sessionobject) throws DataDefineException{
		List delelist = new ArrayList();
		long accountId = CurrentUser.get().getLoginAccount();
		for(int j=0;j<sessionobject.getOldflowidlist().size();j++){
			Object delect = sessionobject.getOldflowidlist().get(j); 
			if(sessionobject.getFlowidlist().contains(delect) == false){            
			    String matchs= (String) delect;
			    delelist.add(matchs);
			}
		}
		for(int i = 0;i<delelist.size();i++){
			String flowname =(String)delelist.get(i);
			//List<FormFlowid>  flowlist =  queryFlowIdByVariableName(flowname);
			List<FormFlowid>  flowlist =  queryFlowIdByVariableName(flowname,accountId);
			for(int j=0;j<flowlist.size();j++){
				FormFlowid flowid = (FormFlowid)flowlist.get(j);
				flowid.setState("N");
				flowid.setAppname("");
				if(flowid.getAccountId() != null && flowid.getAccountId().longValue() == accountId){
					updateFlowId(flowid);
				}
			}
		}
		for(int i=0;i<sessionobject.getFlowidlist().size();i++){
			String flowname = (String) sessionobject.getFlowidlist().get(i);
			//List<FormFlowid>  flowlist =  queryFlowIdByVariableName(flowname);
			List<FormFlowid>  flowlist =  queryFlowIdByVariableName(flowname,accountId);
			for(int j=0;j<flowlist.size();j++){
				FormFlowid flowid = (FormFlowid)flowlist.get(j);
				flowid.setState("Y");
				flowid.setAppname(sessionobject.getFormid().toString());
				if(flowid.getAccountId() != null && flowid.getAccountId().longValue() == accountId){
					updateFlowId(flowid);
				}
			}
		}
		
		//以下是处理录入定义页面（inputData）中用到的流水号
		List<Long> delList = new ArrayList<Long>();
		
		List<Long> flowIdListForInputData = sessionobject.getFlowIdListForInputData();
		List<Long> oldFlowIdListForInputData = sessionobject.getOldFlowIdListForInputData();
		for(Long flowId : oldFlowIdListForInputData){
			if(!flowIdListForInputData.contains(flowId)){
				delList.add(flowId);
			}
		}
		
//		for(Long flowId : delList){
//			FormFlowid formFlowid =  queryFlowIdById(flowId);
//			formFlowid.setState("N");
//			formFlowid.setAppname("");
//			if(formFlowid.getAccountId() != null && formFlowid.getAccountId().longValue() == accountId){
//				updateFlowId(formFlowid);
//			}
//		}
		
		for(Long flowId : flowIdListForInputData){
			FormFlowid formFlowid =  queryFlowIdById(flowId);
			formFlowid.setState("Y");
			formFlowid.setAppname("inputData_" + sessionobject.getFormid().toString());
			if(formFlowid.getAccountId() != null && formFlowid.getAccountId().longValue() == accountId){
				updateFlowId(formFlowid);
			}
		}
	}
	
	/**
	 * 得到该表单流水号的列表
	 * accountId 表单id
	 * @throws DataDefineException 
	 *
	 */
	public List<FormFlowid> getFlowidListbyformid(String name) throws DataDefineException{
		return getFormDaoManager().queryFlowIdByformid(name);
	}
	
	/**
	 * 得到序列号的列表
	 * accountId 用户登陆的单位id
	 * @throws DataDefineException 
	 *
	 */
	public List<FormFlowid> getFlowidList(String accountId) throws DataDefineException{
		return getFlowidList(accountId,null,null,false);
	}
	
	public List<FormFlowid> getFlowidList(String accountId,String condition,String conditionValue) throws DataDefineException{
		return getFormDaoManager().getFlowidList(accountId,condition,conditionValue);
	}
	
	public List<FormFlowid> getFlowidList(String accountId,String condition,String conditionValue, boolean isNeedPage) throws DataDefineException{
		return getFormDaoManager().getFlowidList(accountId,condition,conditionValue,isNeedPage);
	}

	
	/**
	 * 查找系统变量名称
	 * @throws DataDefineException 
	 *
	 */
	public List queryFlowIdByVariableName(String name) throws DataDefineException{
		return getFormDaoManager().queryFlowIdByVariableName(name);
	}
	
	public List queryFlowIdByVariableName(String name,Long accountId) throws DataDefineException{
		return getFormDaoManager().queryFlowIdByVariableName(name,accountId);
	}
	
	/**
	 * 通过流水号id查询
	 * @throws DataDefineException 
	 *
	 */
	public FormFlowid queryFlowIdById(Long id) throws DataDefineException{
		return getFormDaoManager().queryFlowIdById(id);
	}
	
	/**
	 * 保存流水号
	 * @throws DataDefineException 
	 *
	 */
	public BaseModel saveFlowId(BaseModel bm) throws DataDefineException{
		return getFormDaoManager().saveFlowid(bm);
	}
	
	
	/**
	 * 更新流水号
	 * @throws DataDefineException 
	 *
	 */
	public void updateFlowId(BaseModel bm) throws DataDefineException{
		getFormDaoManager().updateFlowId(bm);
	}
	 
	/**
	 * 删除流水号
	 * @throws DataDefineException 
	 *
	 */
	public void deleteFlowId(String id) throws DataDefineException{
		getFormDaoManager().deleteFlowId(id);
	}
	 
	/**
	 * 更新流水号状态
	 */
	public void updateFlowIdState(String variableName) throws DataDefineException{
		getFormDaoManager().updateFlowIdState(variableName);
	}

	/**
	 * 保存流水号最新值
	 * @throws DataDefineException 
	 *
	 */
	public void updateFlowIdValue(String variableName, Long value) throws DataDefineException{
		getFormDaoManager().updateFlowIdValue(variableName, value);
	}
	
	public void updateFlowIdValue(String variableName,Long accountId, Long value) throws DataDefineException{
		getFormDaoManager().updateFlowIdValue(variableName, accountId,value);
	}
	/**
	 * 表单修改点击另存为时，修改表单中生成的相应xml及对应的数据库中字符串
	 * @param sessionobject
	 * @param fdm
	 * @throws SeeyonFormException
	 * @throws SQLException 
	 */
	public void othereditSave(SessionObject sessionobject,HttpServletRequest request,FileManager fileManager) throws SeeyonFormException, SQLException{
		SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
		ISeeyonFormAppManager fmanager=fruntime.getAppManager();
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl)fmanager.findById(sessionobject.getFormid());
		RuntimeCharset fCharset=SeeyonForm_Runtime.getInstance().getCharset();	
		PoCheckManager pom = (PoCheckManager)SeeyonForm_Runtime.getInstance().getPoCheckManager();
		if(fapp == null ){
			fapp = (SeeyonForm_ApplicationImpl)fmanager.createApplication();
			fapp.setAppName(sessionobject.getFormName());
            //08-05-14修改
			fapp.setFId(sessionobject.getFormid());
			try{
				fapp.loadFromDB();
			}catch(Exception e){
				log.error("修改表单定义解析错误", e);
			}finally{
				fapp.unloadAppHibernatResorece();
			} 
		}else{
			fapp.reloadFormResourceInfo();
		}
		sessionobject.setOthersave("true");
		int formType = sessionobject.getFormType();
		String thispage = (String)request.getParameter("thispage");
        //统计、查询、模板等新旧关系映射
		Map<String,String> mapping = new HashMap<String,String>();
		Map mapptemname = new HashMap();
		String newTemNames = request.getParameter("newTemNames");
	    String oldTemIds = request.getParameter("oldTemIds");
		String newTemNameLabel = "newTemName_";     
		String[] values;
		String[] ids;	
		TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
		if(newTemNames!=null && oldTemIds!=null){
			values = newTemNames.split("↗");
			ids = oldTemIds.split("↗");
			for(int i=0;i<ids.length;i++ ){	
				if(mapptemname.get(values[i]) !=null)
					throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.BindSubjectExist",values[i]),Constantform.getString4CurrentUser("DataDefine.BindSubjectExist",values[i]));
				List existTemplate = new ArrayList();
				if(formType==ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){//流程表单
					existTemplate = templeteManager.checkSubject4System(sessionobject.getFormsort(), values[i]);
				}else if(formType==ISeeyonForm.TAppBindType.INFOMANAGE.getValue()){//业务表单
					/*for (FormAppAuthObject ao : sessionobject.getAuthObjectMap().values()) {
						if(ao.getName().equals(values[i]))
							existTemplate.add(ao.getId());
					}*/
				}
				if(existTemplate!=null && existTemplate.size()>0)
					throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.BindSubjectExist",values[i]),Constantform.getString4CurrentUser("DataDefine.BindSubjectExist",values[i]));
				mapping.put(newTemNameLabel+ids[i], values[i]);     //新模板名，格式：newTemName_243210894321932
				mapptemname.put(values[i], values[i]);
			}
		}
		//此处插入收集数据代码
		OperHelper oper = new OperHelper();
		if(thispage.equalsIgnoreCase(IPagePublicParam.BASEINFO)){
			oper.otherbaseInfoCollectData(request, sessionobject,mapping);
		}
		isExistsThisForm(sessionobject.getFormEditName());
		
		List tablefieldlst = sessionobject.getTableFieldList();
		SeeyonFormImpl sfi = new SeeyonFormImpl();
		UserDefineXMLInfoImpl fxml=new UserDefineXMLInfoImpl();
		ChangeObjXml cox = new ChangeObjXml();	
		//执行验证程序	
		List<SeeyonFormException> exceptionList = pom.doCheck(sessionobject);
		if(exceptionList.size() != 0){
			SeeyonFormCheckException sce = new SeeyonFormCheckException(1);
			sce.setList(exceptionList);
			throw sce;
		}
		Long newFormId = UUIDLong.longUUID();
		
		// 另存视图和操作
		List formlst = othersaveOpertion(sessionobject, mapping);	
		    
		// 另存查询
		List<QueryObject> queryList = othersaveQuery(sessionobject, mapping, newFormId);
		
        // 另存统计
		List<ReportObject> reportList = othersaveReport(sessionobject, mapping, newFormId);
		
		
//		组织xsn
		String path = sessionobject.getXsnpath();
		if(path == null){
			//throw new SeeyonFormException(1,"路径为空！");
			throw new SeeyonFormException(1,Constantform.getString4CurrentUser("form.base.pathisnull.label"));
		}

		IFormResoureProvider fResourceProvider =  fapp.getFResourceProvider();
		//另存为时存储图片    
		othersaveImg(fileManager, path, fResourceProvider);
		
		//组织all.xml
		Map map = new HashMap();
//		08-05-19修改增加id
		map.put("FormId", sessionobject.getFormid());
		map.put("FormName", sessionobject.getFormName());
		map.put("DataDefine", sessionobject.getData());
		map.put("FormList", sessionobject.getFormLst());
		
		if(formType==ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue()){//流程表单
			// 另存模板
			HashMap flowMap = new HashMap();
			if(sessionobject.getTemplateobj() !=null)
			    flowMap = othersaveFlow(sessionobject, mapping,newFormId);		
			
			//增加流程绑定
			if(flowMap.size() >0){
				TemplateObject temobj = new TemplateObject();
				temobj.setFlowMap(flowMap);
				map.put("TemplateObject", temobj);	
			}
		}else if(formType==ISeeyonForm.TAppBindType.INFOMANAGE.getValue()){//业务表单
			if(sessionobject.getFormAppAuthObjectMap() !=null)
				otherSaveAppAuth(sessionobject, mapping, newFormId);		
		}
		
		//增加统计、查询
		if(queryList!=null){
			map.put("QueryList", queryList);
		}
		if(reportList!=null){
			map.put("ReportList", reportList);
		}
		String ftemp = cox.createSeeyonDataDefineXml(4,map,sessionobject);		
		ftemp=fCharset.SystemDefault2SelfXML(ftemp);
		fxml.setSeeyonFomDefineXML(ftemp);
		
		//组织Operation_001.xml
		
		for(int i=0;i<formlst.size();i++){
			FormPage formpage = (FormPage)formlst.get(i);
			for(int j=0;j<formpage.getOperlst().size();j++){
				Operation operation = (Operation)formpage.getOperlst().get(j);
				//如果客户没有在页面进行操作，注入默认值
				int regflag = 1;
				Map formmap = new HashMap();
				formmap.put("Operation", operation);
				ftemp=fCharset.SystemDefault2SelfXML(cox.createOperationXml(regflag,operation.getType(),tablefieldlst, formmap,sessionobject));
				fxml.addResource(operation.getFilename(), ftemp);
			}
		}		
		//组织bindschema.xml
		Map binschemamap = new HashMap();
		binschemamap.put("TableFieldList", sessionobject.getTableFieldList());
		ftemp=fCharset.SystemDefault2SelfXML(cox.creatBindschemaXml(2, binschemamap));
		fxml.addResource("bindschema.xml",ftemp);

		//组织bindAppData.xml
		
		//组织defaultInput.xml
		List inputlst = sessionobject.getFieldInputList();
		Map defaultmap = new HashMap();
		defaultmap.put("FieldInputList", inputlst);
		ftemp=fCharset.SystemDefault2SelfXML(cox.createDefaultInputXml(2, defaultmap));		
		fxml.addResource("defaultInput.xml",ftemp);	   	
		
		//分逻辑：从infopath导入文件的修改  直接进行修改
		if("edit".equals(path)){
			for (IResourceInfo fInfo : fxml.getResourceList()) {				
					fResourceProvider.addResource(ISeeyonForm_Application.C_sResourceDir_UserDefineXML
							+ fInfo.getResourceName(), fInfo.getResourceInfo());
					fResourceProvider.addFormProperty(fInfo.getResourceName(),fapp.C_iPropertyType_UserDefineXML, ISeeyonForm_Application.C_sResourceDir_UserDefineXML
							+ fInfo.getResourceName());												
			} 
			fapp.setCategory(sessionobject.getFormsort());
		}else{
			ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(StringUtils.readFileData(path));
			fapp = (SeeyonForm_ApplicationImpl)fmanager.createApplication();		
			//组织submitData_new.xml
			fapp.setCategory(sessionobject.getFormsort());
			fapp.loadFromCAB(fInfopathxsn, fxml,sessionobject,fileManager);
			fResourceProvider = fapp.getFResourceProvider();
		}

			
		    Document doc = null;
			doc  = dom4jxmlUtils.paseXMLToDoc(fxml.getSeeyonFomDefineXML());
			Element root = doc.getRootElement();
			// 准备资源文件
			//组织新的datadefine  xml文件
			Element dataroot = fapp.loadDataDefine(root, fResourceProvider);
            //8月17号添加的逻辑，增加hbm文件
			fapp.getDataDefine().loadFromXml(dataroot);			
			SeeyonDataDefine hbmdade=(SeeyonDataDefine)fapp.getDataDefine();
			String fHbmXml = hbmdade.getDataDefine().getHbmString(0);			
			if("edit".equals(path)){
				fxml.addResource(SeeyonDataDefine.C_sResourceFileName_Hbm,fHbmXml);
				for (IResourceInfo fInfo : fxml.getResourceList()) {
					if(SeeyonDataDefine.C_sResourceFileName_Hbm.equals(fInfo.getResourceName())){
						fResourceProvider.addResource(SeeyonDataDefine.C_sResourceDir_Hibernate
								+ fInfo.getResourceName(), fInfo.getResourceInfo());
						fResourceProvider.addFormProperty(fInfo.getResourceName(),SeeyonDataDefine.C_iPropertyType_HBMFile, SeeyonDataDefine.C_sResourceDir_Hibernate
								+ fInfo.getResourceName());		
					}			
				}
			}
			FormAppMain fam = new FormAppMain();
			if(!(sessionobject.getFormEditName() == null 
					|| "".equals(sessionobject.getFormEditName())
					||"null".equals(sessionobject.getFormEditName())))
			fam.setName(sessionobject.getFormEditName());
			else fam.setName(sessionobject.getFormName());
			//根据查询数据库中是否有此纪录
			fam.setId(newFormId);
			fam.setState(0);
			sessionobject.setFormstate("0");
			String formsort = (String)request.getParameter("formsort");
			fam.setCategory(Long.parseLong(formsort));
			fam.setFormType(formType);
			sessionobject.setFormid(newFormId);
			//保存主要信息
			try{
				otherpostdata(root,fResourceProvider,fam,sessionobject);
			}catch(Exception e){
				//throw new DataDefineException(1,"事物实例失败，数据库保存出错","事物实例失败，数据库保存出错");
				throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
			}						
			//fam = saveCreateInfoToDB(root, fResourceProvider,fam);
			/*if(fam != null){		
				//TODO 把使用人存入数据库	
				insertFormAppAttachMan(sessionobject,fam);
				
			}*/
            //最后一步执行建表的操作
			fapp.getDataDefine().createStorage(dataroot, fResourceProvider);
			if(!(sessionobject.getFormEditName() == null 
					|| "".equals(sessionobject.getFormEditName())
					||"null".equals(sessionobject.getFormEditName())))
			sessionobject.setFormName(sessionobject.getFormEditName());
			//另存为后为了保证原来的app不变
			fapp.unloadAppHibernatResorece();
			fapp.loadFromDB();
			fapp.clearUselessMemory();
	}
	private void othersaveImg(FileManager fileManager, String path, IFormResoureProvider fResourceProvider) throws SeeyonFormException, DataDefineException {
		//另存为的时候如果该表单存在图片，则重新存储图片id
		 for(int i = 0;i<fResourceProvider.getResourceList().size();i++){
         	//FormAppResource resource = (FormAppResource)fResourceProvider.getResourceList().get(i);
			 ResourceInfoImpl_String resource = null;
			 ResourceInfoImpl resourcempl = null;
			String resourcename = "";
			boolean resourcesign = false;
			boolean resourcemplsign = false;
			if(fResourceProvider.getResourceList().get(i) instanceof ResourceInfoImpl_String){
				resource = (ResourceInfoImpl_String)fResourceProvider.getResourceList().get(i);
         		resourcename = resource.getResourceName();
         		resourcesign = true;
			}else if(fResourceProvider.getResourceList().get(i) instanceof ResourceInfoImpl){
				resourcempl =(ResourceInfoImpl) fResourceProvider.getResourceList().get(i);
         		resourcename = resourcempl.getResourceName();
         		resourcemplsign = true;
			}
         	      		
         	if(resourcename.indexOf("/extend/OutViewFile/") !=-1){
 				String ftem = "";
 				if(resourcesign)
 					ftem = resource.getResourceInfo();
 	         	else
 	         		ftem = resourcempl.getResourceInfo();
					Document document = null;
					SAXReader sr = new SAXReader();
					try {
						sr.setXMLReaderClassName("org.gjt.xpp.sax2.Driver");
						document = sr.read(chgToReader(chgNameSpace(ftem)));			
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log.error("带图片的表单，SAXException", e);
					}catch (DocumentException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log.error("带图片的表单，SAXException", e);
					}		
					Element aRoot = document.getRootElement();
					List imgList = aRoot.selectNodes("//img");
					List img1List = new ArrayList();
					for(int img = 0; img < imgList.size(); img++){//所有img节点
						Element span = (Element)imgList.get(img);
						if(span.attribute("srcId") !=null){
							String img_srcid = span.attribute("srcId").getValue();
							V3XFile v3xfile = null;
							try {
								v3xfile = fileManager.getV3XFile(Long.parseLong(img_srcid));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								log.error("带图片的表单，NumberFormatException", e);
								//e.printStackTrace();
							} catch (BusinessException e) {
								// TODO Auto-generated catch block
								log.error("带图片的表单，BusinessException", e);
								//e.printStackTrace();
							}
							Date createDate = new Date();
							V3XFile newv3xfile = new V3XFile();
							try {
								newv3xfile = fileManager.save(fileManager.getFileInputStream(Long.parseLong(img_srcid)),ApplicationCategoryEnum.form,v3xfile.getFilename(),createDate, true);
								newv3xfile.getId();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								log.error("该表单图片不存在，请检查，BusinessException", e);
								//throw new DataDefineException(1,"该表单图片不存在，请检查","该表单图片不存在，请检查");
								throw new DataDefineException(1,Constantform.getString4CurrentUser("form.exception.notnull"),Constantform.getString4CurrentUser("form.exception.notnull"));
								
							}
							span.attribute("src").setValue("/FormImgView?imgId=" + newv3xfile.getId().toString());
							span.attribute("srcId").setValue(newv3xfile.getId().toString());
						}					
					}	
					
					if(resourcesign)
						resource.setFResourceInfo(document.asXML());
	 	         	else{ 
	 	         		ByteArrayOutputStream fout =  new ByteArrayOutputStream(300);
	 	         		try {
							fout.write(document.asXML().getBytes());
						} catch (IOException e) {
							//e.printStackTrace();
							log.error("带图片的表单，IOException", e);
						}
						resourcempl.setByteArrayOutputStream(fout);
	 	         	} 	         		
 			}
			}
	}
	private List othersaveOpertion(SessionObject sessionobject, Map<String, String> mapping) throws SeeyonFormException {
		String formLabel = "form_";
		String operationLabel = "operation_";
		//判断追加和文本域的匹配情况
		HashMap inputmap = new HashMap();
		for(int i=0 ; i<sessionobject.getFieldInputList().size();i++){
			InfoPath_FieldInput infopathput = (InfoPath_FieldInput)sessionobject.getFieldInputList().get(i);
			  inputmap.put(infopathput.getName(), infopathput.OperationType2str(infopathput.getInputType()));
		}
		List formlst = sessionobject.getFormLst();
		for(int i=0;i<formlst.size();i++){
			FormPage formpage = (FormPage)formlst.get(i);
			Long pageid = UUIDLong.longUUID();
            //保存视图映射关系
			mapping.put(formLabel+formpage.getFormPageId(), pageid.toString());
			formpage.setFormPageId(pageid.toString());
			//另存为的时候把表单初始值中为流水号的去掉。
			for(int j=0;j<formpage.getOperlst().size();j++){
				Long operationid = UUIDLong.longUUID();
				Operation operation = (Operation)formpage.getOperlst().get(j);
//				保存操作映射关系
				mapping.put(operationLabel+operation.getOperationId(), operationid.toString());
				Map formmap = new HashMap();
				formmap.put("Operation", operation);
				 Operation opertion = (Operation) formmap.get("Operation");
				 //2009年02月27日修改（保证操作文件名称不重复）

					if(opertion.getFilename().length() <25)
						 opertion.setFilename("Operation_"+UUIDLong.absLongUUID() +".xml");

					for(int t=0;t<opertion.getOperlst().size(); t++){
						Map opermap=(Map)opertion.getOperlst().get(t);
						if("add".equalsIgnoreCase((String) opermap.get("formoper"+t))){
							if(!IXmlNodeName.C_sVluae_textArea.equalsIgnoreCase((String) inputmap.get(opermap.get("bindname"+t))))
								throw new DataDefineException(1,Constantform.getString4CurrentUser("form.base.fieldname.label")+" "+opermap.get("bindname"+t)+" "+Constantform.getString4CurrentUser("form.input.error"),Constantform.getString4CurrentUser("form.base.fieldname.label")+" "+opermap.get("bindname"+t)+" "+Constantform.getString4CurrentUser("form.input.error"));
								//throw new DataDefineException(1,"字段 "+opermap.get("bindname"+t)+" 的操作设置为追加类型,请到录入定义页面将其数据域输入类型设置为文本域","字段 "+opermap.get("bindname"+t)+" 的操作设置为追加类型,请到录入定义页面将其数据域输入类型设置为文本域");
						}		
					}
				operation.setOperationId(operationid.toString());	
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
								List valuelist = span.selectNodes(".//Value");
								for(int ivalue = 0; ivalue < valuelist.size(); ivalue++){//所有Value节点
									Element sval = (Element)valuelist.get(ivalue);
									if(!"".equals(sval.getText()) && !"null".equals(sval.getText()) && sval.getText() !=null){
										if(ParseUserCondition.isSystemExtend(sval.getText()) == true){
											dataoldroot.remove(span);
										}
									}		
								}
							}
							newoninitxml = dataoldroot.asXML().replaceAll("\n", "");
            			}	
            			operation.setNewinitxml(newoninitxml.replaceAll("<OnInit>", "").replaceAll("</OnInit>", ""));	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log.error("表单另存为时如果有流水号，删除初始值错误", e);
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
								List valuelist = span.selectNodes(".//Value");
								for(int ivalue = 0; ivalue < valuelist.size(); ivalue++){//所有Value节点
									Element sval = (Element)valuelist.get(ivalue);
									if(!"".equals(sval.getText()) && !"null".equals(sval.getText()) && sval.getText() !=null){
										if(ParseUserCondition.isSystemExtend(sval.getText()) == true){
											dataoldroot.remove(span);
										}
									}
								}
							}
							oninitxml = dataoldroot.asXML().replaceAll("\n", "");
            			}	
            			operation.setViewbindstr(first+oninitxml+end);	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log.error("表单另存为时如果有流水号，删除初始值错误", e);
					}	
                }
			}
		}
		return formlst;
	}
	private List<QueryObject> othersaveQuery(SessionObject sessionobject, Map<String, String> mapping, Long newFormId) {
		String formLabel = "form_";
		String tableLabel = "table_";
		String operationLabel = "operation_";
		String separator = "\\|";
		List<QueryObject> queryList = sessionobject.getQueryConditionList();	
		StringBuffer buffer;
		if(queryList != null){
			for(QueryObject query:queryList){
				//query.setQueryName(mapping.get(newQueryNameLabel+query.getQueryId()));
				query.setQueryId(String.valueOf(UUIDLong.longUUID()));
                //设置新的operationid，格式：id|id
				String operationId = query.getOperationId();
				if(operationId != null && !"".equals(operationId)){
					String[] operationIds = operationId.split(separator);
					if(operationIds != null){
						buffer = new StringBuffer();
						for(String operId:operationIds){
							buffer.append(mapping.get(operationLabel+operId)+"|");
						}
						query.setOperationId(buffer.toString());
					}
				}
				//设置新的formid，格式：id|id
				String formId = query.getFormId();
				if(formId != null && !"".equals(formId)){
					String[] formIds = formId.split(separator);
					if(formIds != null){
						buffer = new StringBuffer();
						for(String form:formIds){
							buffer.append(mapping.get((formLabel)+form)+"|");
						}
						query.setFormId(buffer.toString());
					}
				}
				//设置新的mastername
				if(query.getMastername()!=null && !"".equals(query.getMastername()))
					query.setMastername(mapping.get(tableLabel+query.getMastername()));
				//设置新的slavename
				if(query.getSlavename()!=null && !"".equals(query.getSlavename()))
					query.setSlavename(mapping.get(tableLabel+query.getSlavename()));
				
				//设置新的访问对象
				List<FomObjaccess> accesses = query.getObjAccessList();
				if(accesses != null && accesses.size()>0){
					for(FomObjaccess access:accesses){
						access.setId(UUIDLong.longUUID());
						access.setRefAppmainId(newFormId);
//						if(access.getObjectname() != null && !"".equals(access.getObjectname()))
//							access.setObjectname(mapping.get(newQueryNameLabel+access.getObjectname()));
					}
				}
			}
		}
		return queryList;
	}
	private List<ReportObject> othersaveReport(SessionObject sessionobject, Map<String, String> mapping, Long newFormId) { 
		String formLabel = "form_";
		String tableLabel = "table_";
		String operationLabel = "operation_";
		String separator = "\\|";
		StringBuffer buffer;
		List<ReportObject> reportList = sessionobject.getReportConditionList();
		if(reportList != null){
			for(ReportObject report:reportList){
				//report.setReportName(mapping.get(newReportNameLabel+report.getReportid()));
				report.setReportid(String.valueOf(UUIDLong.longUUID()));
//				设置新的operationid，格式：id|id
				String operationId = report.getOperationId();
				if(operationId != null && !"".equals(operationId)){
					String[] operationIds = operationId.split(separator);
					if(operationIds!=null){
						buffer = new StringBuffer();
						for(String operId:operationIds){
							buffer.append(mapping.get(operationLabel+operId)+"|");
						}
						report.setOperationId(buffer.toString());
					}
				}
//				设置新的formid，格式：id|id
				String formId = report.getFormId();
				if(formId != null && !"".equals(formId)){
					String[] formIds = formId.split(separator);
					if(formIds != null){
						buffer = new StringBuffer();
						for(String form:formIds){
							buffer.append(mapping.get(formLabel+form)+"|");
						}
						report.setFormId(buffer.toString());
					}
				}
//				设置新的mastername
				if(report.getMastername()!=null && !"".equals(report.getMastername()))
					report.setMastername(mapping.get(tableLabel+report.getMastername()));
//				设置新的slavename
				if(report.getSlavename()!=null && !"".equals(report.getSlavename()))
					report.setSlavename(mapping.get(tableLabel+report.getSlavename()));
				
				//设置新的访问对象
				List<FomObjaccess> accesses = report.getObjaccesslist();
				if(accesses != null && accesses.size()>0){
					for(FomObjaccess access:accesses){
						access.setId(UUIDLong.longUUID());
						access.setRefAppmainId(newFormId);
//						if(access.getObjectname()!=null && !"".equals(access.getObjectname()))
//							access.setObjectname(mapping.get(newReportNameLabel+access.getObjectname()));
					}
				}
			}
		}
		return reportList;
	}
	final static String newTemNameLabel = "newTemName_";     
	final static String formLabel = "form_";
	final static String tableLabel = "table_";
	final static String operationLabel = "operation_";
	final static String branchLabel = "branch_";
	private HashMap othersaveFlow(SessionObject sessionobject, Map<String, String> mapping,Long newFormId) {
		TemplateObject temobject= sessionobject.getTemplateobj();
		HashMap newflowMap = new HashMap();
		HashMap hash = temobject.getFlowMap();
		HashMap addMap = temobject.getAddMap();
		Iterator it = hash.entrySet().iterator();
		Templete newtemplete = new Templete();
		TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
		FileManager fileManager = (FileManager)ApplicationContextHolder.getBean("fileManager");
		AttachmentManager attachmentManager = (AttachmentManager)ApplicationContextHolder.getBean("attachmentManager");
		ColSuperviseManager colSuperviseManager = (ColSuperviseManager)ApplicationContextHolder.getBean("colSuperviseManager");
		NewflowManager newflowManager = (NewflowManager)ApplicationContextHolder.getBean("newflowManager");
	    while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			FlowTempletImp fotemimp = (FlowTempletImp)entry.getValue();
			Long newtempleteid = UUIDLong.longUUID();
			String newtempletename = mapping.get(newTemNameLabel+fotemimp.getId());
			Templete templete = templeteManager.get(fotemimp.getId());
			if(templete !=null){
				Timestamp createDate = new Timestamp(System.currentTimeMillis());
				
				String summaryxml = templete.getSummary();
				String bodyxml = templete.getBody();
                // 重新装载summary
				ColSummary summary = (ColSummary)XMLCoder.decoder(templete.getSummary());
				if(mapping.get(newTemNameLabel+templete.getId()) !=null)
					summary.setSubject(mapping.get(newTemNameLabel+templete.getId()));
				if(!"".equals(summary.getArchiverFormid())&& !"null".equals(summary.getArchiverFormid()) && summary.getArchiverFormid() !=null){
					String archiverformid = "";
					for(int a=0;a<summary.getArchiverFormid().split("\\|").length ;a++){
				    	 String showDetailsArray[] = summary.getArchiverFormid().split("\\|");
				    	 String showDetails[] = showDetailsArray[a].split("\\.");
				    	 String formid  = showDetails[0];
				    	 String operationid = showDetails[1];   
				    	 if(mapping.get(formLabel+formid) !=null)
				    	     formid = mapping.get(formLabel+formid);
				    	 if(mapping.get(operationLabel+operationid) !=null)  
				    		 operationid = mapping.get(operationLabel+operationid);  
				    	 archiverformid +=  formid+"."+operationid+"|";
				    }
					summary.setArchiverFormid(archiverformid);
				}          
				summary.setQuoteformtemId(null);
				summary.setQuoteformtemName("");
				summary.setNewflowType(-1);
				summaryxml = XMLCoder.encoder(summary);
				
                // 重新装载body
				FormContent formContent = (FormContent)XMLCoder.decoder(templete.getBody());
				FormBody formBody = formContent.getForms().get(0);
				if(mapping.get(formLabel+formBody.getForm()) !=null)
					formBody.setForm(mapping.get(formLabel+formBody.getForm()));
				if(mapping.get(operationLabel+formBody.getOperationName()) !=null)
					formBody.setOperationName(mapping.get(operationLabel+formBody.getOperationName()));
				formBody.setFormApp(newFormId.toString());	
				formContent.setId(newtempleteid);
				bodyxml = XMLCoder.encoder(formContent);			
				
				// 重新装载分支信息
				List<ColBranch> branchs = null;
				List<ColBranch> newbranchs = new ArrayList<ColBranch>();
				ColSuperviseDetail detail = null;
				List<SuperviseTemplateRole> roles = null;
				List<SuperviseTemplateRole> newroles = new ArrayList<SuperviseTemplateRole>();
				List<Attachment> attachments = null;
				List<Attachment> newattachments = new ArrayList<Attachment>();
				Set<ColSupervisor> newcolsupervisor = new HashSet<ColSupervisor>();
				Set<TempleteAuth> newTempleteauth = new HashSet<TempleteAuth>();
				branchs = templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal());
				if(branchs !=null){
					for(ColBranch colbranch :branchs){
						ColBranch newcolbranch = null;
						try {
							newcolbranch = (ColBranch) colbranch.clone();
						} catch (CloneNotSupportedException e) {
							log.error("克隆分支对象出错", e);
						}
						Long branchid = UUIDLong.longUUID();
						mapping.put(branchLabel+colbranch.getId(), branchid.toString());
						newcolbranch.setId(branchid);
						newcolbranch.setTemplateId(newtempleteid);
						newbranchs.add(newcolbranch);
					}
				}
				
				// 重新装载督办信息
				detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templete.getId());
				roles = colSuperviseManager.findSuperviseRoleByTemplateId(templete.getId());
				
				if(detail != null) {
					ColSuperviseDetail newdetail = new ColSuperviseDetail();
					try {
						 newdetail = (ColSuperviseDetail) detail.clone();
					} catch (CloneNotSupportedException e) {
						log.error("克隆督办对象出错", e);
					}
					newdetail.setIdIfNew();
					newdetail.setEntityId(newtempleteid);
	            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
	            	if(supervisors != null && supervisors.size()>0) {
		            	for(ColSupervisor supervisor:supervisors){
		            		ColSupervisor newsupervisor = new ColSupervisor();
		            		newsupervisor.setIdIfNew();
		            		newsupervisor.setSuperviseId(newdetail.getId());
		            		newsupervisor.setSupervisorId(supervisor.getSupervisorId());
		            		newsupervisor.setPermission(supervisor.getPermission());
		            		newcolsupervisor.add(newsupervisor);
		            	}		
		            } 
	            	newdetail.setColSupervisors(newcolsupervisor);
	            	if(roles != null && roles.size()>0) {
		            	for(SuperviseTemplateRole role : roles){
		            		SuperviseTemplateRole newrole =  new SuperviseTemplateRole();
		            		newrole.setIdIfNew();
		            		newrole.setRole(role.getRole());
		            		newrole.setSuperviseTemplateId(newtempleteid);
		            		newroles.add(newrole);
		            	}
	            	}
	            	colSuperviseManager.saveForTemplate(newdetail, newroles);
	            }
                // 重新装载附件
				attachments = attachmentManager.getByReference(templete.getId(), templete.getId());              
				if(attachments !=null){
					for(Attachment attanchment :attachments){
						Attachment newattanchment = null;
						try {
							newattanchment = (Attachment) attanchment.clone();
						} catch (CloneNotSupportedException e) {
							log.error("克隆附件对象出错", e);
						}
						
						if(attanchment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal()){
							Long newFileId = UUIDLong.longUUID();
							try {
                                fileManager.clone(attanchment.getFileUrl(), attanchment.getCreatedate(), newFileId, createDate);
                            }
                            catch (Exception e) {
                                log.warn("转发复制原意见附件异常 [originalSummaryId = " + templete.getId() + "]", e);
                            }
                            newattanchment.setFileUrl(newFileId);
						}					
						Long attanchmentid = UUIDLong.longUUID();
						newattanchment.setId(attanchmentid);
						newattanchment.setSubReference(newtempleteid);
						newattanchment.setReference(newtempleteid);
						newattanchment.setCreatedate(createDate);
						
						newattachments.add(newattanchment);
					}										
				}				
                // 重新装载workflow中的Form、OperationName、FormApp属性
				BPMProcess process = BPMProcess.fromXML(templete.getWorkflow()); 

				BPMStart start = (BPMStart) process.getStart();
				BPMSeeyonPolicy startpolicy =(BPMSeeyonPolicy) start.getSeeyonPolicy();
				if(mapping.get(formLabel+startpolicy.getForm()) !=null)
					startpolicy.setForm(mapping.get(formLabel+startpolicy.getForm()));
				if(mapping.get(operationLabel+startpolicy.getOperationName()) !=null)
					startpolicy.setOperationName(mapping.get(operationLabel+startpolicy.getOperationName()));
				startpolicy.setFormApp(newFormId.toString());		
				
				List<BPMEnd> bpmend = process.getEnds();
				for(BPMEnd end:bpmend){
					BPMSeeyonPolicy endpolicy = (BPMSeeyonPolicy)end.getSeeyonPolicy();
					if(mapping.get(formLabel+endpolicy.getForm()) !=null)
						endpolicy.setForm(mapping.get(formLabel+endpolicy.getForm()));
					if(mapping.get(operationLabel+endpolicy.getOperationName()) !=null)
						endpolicy.setOperationName(mapping.get(operationLabel+endpolicy.getOperationName()));
					endpolicy.setFormApp(newFormId.toString());	
				}
				
				List<BPMAbstractNode> bpmlist = process.getActivitiesList();				
				for(BPMAbstractNode bpmnode : bpmlist){
					BPMSeeyonPolicy bpmpolicy = (BPMSeeyonPolicy)bpmnode.getSeeyonPolicy();
					if(mapping.get(formLabel+bpmpolicy.getForm()) !=null)
						bpmpolicy.setForm(mapping.get(formLabel+bpmpolicy.getForm()));
					if(mapping.get(operationLabel+bpmpolicy.getOperationName()) !=null)
						bpmpolicy.setOperationName(mapping.get(operationLabel+bpmpolicy.getOperationName()));
					bpmpolicy.setFormApp(newFormId.toString());	
                    //将新流程标志去掉
					bpmpolicy.setNF("0");
				}
				// 重新装载workflow中的分支id
				List<BPMTransition> links  = process.getLinks();
				if(links !=null){
					for(BPMTransition link:links) {
						if(mapping.get(branchLabel+link.getConditionId()) !=null)
							link.setConditionId(mapping.get(branchLabel+link.getConditionId()));
					}
				}
				try {
					newtemplete = (Templete) templete.clone();
				} catch (CloneNotSupportedException e) {
					log.error("克隆模板对象时出错,CloneNotSupportedException", e);
				}	
				// 重新装载授权对象
				if(templete.getTempleteAuths() !=null){
					List<Object[]> entities = new ArrayList<Object[]>();
					for(TempleteAuth auth: templete.getTempleteAuths()){
						TempleteAuth newauth = new TempleteAuth();
						try {
							newauth = (TempleteAuth) auth.clone();
						} catch (CloneNotSupportedException e) {
							log.error("克隆模板授权对象时出错,CloneNotSupportedException", e);
						}
						newauth.setIdIfNew();		
						newauth.setObjectId(newtempleteid);		
						newTempleteauth.add(newauth);					
					}
					newtemplete.setTempleteAuths(newTempleteauth);
				}							
				newtemplete.setWorkflow(process.toXML());
				newtemplete.setCreateDate(createDate);
				newtemplete.setSubject(newtempletename);
				newtemplete.setId(newtempleteid);
				newtemplete.setSummary(summaryxml);
				newtemplete.setBody(bodyxml);
				newtemplete.setFormParentId(null);
				newtemplete.setTempleteNumber("");
				newtemplete.setState(1);
				
				templeteManager.save(newtemplete);
				if(newbranchs !=null)
					templeteManager.saveBranch(newtemplete.getId(),newbranchs);
				if(attachments !=null)
					attachmentManager.create(newattachments);
				
			} else {
				String key = String.valueOf(fotemimp.getId());
				Templete addTemplete = (Templete)addMap.get(key);
				if(addTemplete != null){
					try {
						newtemplete = (Templete) addTemplete.clone();
					} catch (CloneNotSupportedException e) {
						log.error("克隆模板对象时出错,CloneNotSupportedException", e);
					}
					try{
						newtemplete.setCategoryId(sessionobject.getFormsort());
						newtemplete.setId(newtempleteid);
						newtemplete.setSubject(newtempletename);
						newtemplete.setFormParentId(null);
						newtemplete.setTempleteNumber("");
						newtemplete.setState(1);
						String summaryxml = newtemplete.getSummary();
						String bodyxml = newtemplete.getBody();
		                // 重新装载summary
						ColSummary summary = (ColSummary)XMLCoder.decoder(newtemplete.getSummary());
						summary.setSubject(newtempletename);
						if(!"".equals(summary.getArchiverFormid())&& !"null".equals(summary.getArchiverFormid()) && summary.getArchiverFormid() !=null){
							String archiverformid = "";
							for(int a=0;a<summary.getArchiverFormid().split("\\|").length ;a++){
						    	 String showDetailsArray[] = summary.getArchiverFormid().split("\\|");
						    	 String showDetails[] = showDetailsArray[a].split("\\.");
						    	 String formid  = showDetails[0];
						    	 String operationid = showDetails[1];   
						    	 if(mapping.get(formLabel+formid) !=null)
						    	     formid = mapping.get(formLabel+formid);
						    	 if(mapping.get(operationLabel+operationid) !=null)  
						    		 operationid = mapping.get(operationLabel+operationid);  
						    	 archiverformid +=  formid+"."+operationid+"|";
						    }
							summary.setArchiverFormid(archiverformid);
						}          
						summary.setQuoteformtemId(null);
						summary.setQuoteformtemName("");
						summary.setNewflowType(-1);
						summaryxml = XMLCoder.encoder(summary);
						newtemplete.setSummary(summaryxml);
						Set<TempleteAuth> newTempleteauth = new HashSet<TempleteAuth>();
						if(addTemplete.getTempleteAuths() !=null){
							List<Object[]> entities = new ArrayList<Object[]>();
							for(TempleteAuth auth: addTemplete.getTempleteAuths()){
								TempleteAuth newauth = new TempleteAuth();
								try {
									newauth = (TempleteAuth) auth.clone();
								} catch (CloneNotSupportedException e) {
									log.error("克隆模板授权对象时出错,CloneNotSupportedException", e);
								}
								newauth.setIdIfNew();		
								newauth.setObjectId(newtempleteid);		
								newTempleteauth.add(newauth);					
							}
							newtemplete.setTempleteAuths(newTempleteauth);
						}
						templeteManager.save(newtemplete);
						List<ColBranch> branchs = (List<ColBranch>)addMap.get(key+"branch");
						if(branchs != null && branchs.size()>0){
							templeteManager.saveBranch(newtemplete.getId(),branchs);
						}
						ColSuperviseDetail detail = (ColSuperviseDetail)addMap.get(key+"supervise");
						if(detail != null) {
							detail.setIdIfNew();
							Set<ColSupervisor> supervisors = detail.getColSupervisors();
							if(supervisors != null) {
								for(ColSupervisor colSupervisor:supervisors)
									colSupervisor.setSuperviseId(detail.getId());
							}
							List<SuperviseTemplateRole> roles = (List<SuperviseTemplateRole>)addMap.get(key+"role");
							colSuperviseManager.saveForTemplate(detail, roles);
						}
						
						List<Attachment> attachments = (List<Attachment>)addMap.get(key+"attachment");
						if(attachments != null) {
							attachmentManager.create(attachments);
						}
					}catch(Exception e){
						log.error("保存新增的表单模板时发生错误", e);
					}
				}
			}
			fotemimp.setId(newtempleteid);
			fotemimp.setName(newtempletename);
			newflowMap.put(fotemimp.getId(), fotemimp);
		}
	    /*
	    //调试信息
		Iterator map = mapping.entrySet().iterator();
		System.out.println("输出模板另存各对象的对应关系");
		while(map.hasNext()){
			Map.Entry entry = (Map.Entry)map.next();
			System.out.println(entry.getKey()+" = "+entry.getValue());
		}
		System.out.println("输出完成");
		*/
	    return newflowMap;
	}
	
	/**
	 * 
	 * 通过表单名称查询表单所属人名称
	 */
	 public String queryOwnerByAppname(String appName) throws DataDefineException{
		 return getFormDaoManager().queryOwnerByAppname(appName);
	 }
	 
	 

	 /**
	  *  执行查询时动态建立索引。
	  */
	 public void formindex(ISeeyonForm_Application fapp,String id) throws SeeyonFormException{
		 HashMap indexfieldmap = new HashMap();
		 try {
				List<String> result = new ArrayList<String>();  //查询条件中组成的所有sql
				HashMap tablefieldMap1 = new HashMap(); //查询条件中表名一下所有的字段名称
				HashMap tablefieldMap2 = new HashMap(); //查询条件中表名二下所有的字段名称
				List table1list = new ArrayList(); //查询条件中表名一下所有的字段名称
				List table2list = new ArrayList(); //查询条件中表名二下所有的字段名称
				String table1xml = ""; //查询条件中表名一所组成的索引xml
				String table2xml = ""; //查询条件中表名二所组成的索引xml
				List table2xmllist = new ArrayList();
				String table1 = "";   //查询条件中表名一
				String table2 = "";  //查询条件中表名二
				HashMap oldindequeryxmap = new HashMap();
				SeeyonDataDefine seedade = null;
				DataDefine  define = null;
				for(int i =0;i<fapp.getQueryList().size();i++){
					SeeyonQueryImpl seeyon = (SeeyonQueryImpl)fapp.getQueryList().get(i);
					String queryid = seeyon.getId();
						//新建索引
						if(id.equals(queryid)){
							ConditionListQueryImpl conditionimp = (ConditionListQueryImpl)seeyon.getUserConditionList();//用户条件
							ConditionListImpl condmpl =(ConditionListImpl)seeyon.getFilter();//限制条件
							//设置查询时拼的sql为用户条件在前，限制条件在后。
							if(conditionimp !=null){
								for(int j=0;j<conditionimp.getConditionList().size();j++){
									if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
										DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
										if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
											String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
											String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
											if(j==0){
												table1 = tablename;
											}if(table1.equals(tablename)){
												if(tablefieldMap1.get(fieldname)==null){
													tablefieldMap1.put(fieldname, fieldname);
													table1list.add(fieldname);
												}									
											}else{
												table2 = tablename;
												tablefieldMap2.put(fieldname, fieldname);
												table2list.add(fieldname);
											}
										}
									}
								}
							}
							if(condmpl !=null){
								//限制条件
								for(int j=0;j<condmpl.getConditionList().size();j++){
									if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
										DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
										if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
											String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
											String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
											if(table2.equals(tablename)){									
												if(tablefieldMap2.get(fieldname)==null){
													tablefieldMap2.put(fieldname, fieldname);
													table2list.add(fieldname);
												}	
											}else if(table1.equals(tablename)){									
												if(tablefieldMap1.get(fieldname)==null){
													tablefieldMap1.put(fieldname, fieldname);
													table1list.add(fieldname);
												}	
											}else if("".equals(table1)){
												if(j==0){
													table1 = tablename;
													tablefieldMap1.put(fieldname, fieldname);
													table1list.add(fieldname);
												}
											}else{
												if("".equals(table2))
												    table2 = tablename;
												tablefieldMap2.put(fieldname, fieldname);
												table2list.add(fieldname);
											}
										}
									}
								}
							}
							Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)fapp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
							define = new DataDefine();
							define.loadFromXml(oldroot);
							for(int x=0;x<define.getTableLst().size();x++){
								FormTable ftable = define.getTableLst().get(x);
								for(int a = 0;a<ftable.getIndexLst().size();a++){
									FormIndex findex1 = ftable.getIndexLst().get(a);
									indexfieldmap.put(findex1.getFieldList(), findex1.getFieldList());
								}
								if(ftable.getName().equals(table1) && table1list.size() !=0){
									FormIndex findex = new FormIndex(ftable);
									findex.setId(queryid+"_m");
									findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+queryid.substring(1));
									findex.setFOnwerTable(ftable);
									findex.setDisplay(ftable.getName()+"表索引");
									findex.setQueryid(queryid);
									findex.setReportid(null);
									String field = "";
//									如果条件中有多个字段，则只取前三个字段的索引。
									for(int z=0;z<table1list.size();z++){
										if(z<3){
											if(z<2 && z < table1list.size()-1)
												field +=table1list.get(z)+",";
											else
												field +=table1list.get(z);
										}					
									}
									if(indexfieldmap.get(field) == null){
										indexfieldmap.put(field, field);
										findex.setFieldList(field);
										ftable.getIndexLst().add(findex);
										table1xml = findex.creatDefineXml(2);
										result.addAll(findex.getCreateSql());
									}									
								}if(ftable.getName().equals(table2) && table2list.size() !=0){
									FormIndex findex = new FormIndex(ftable);
									findex.setId(queryid+"_s");
									findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+queryid.substring(1));
									findex.setFOnwerTable(ftable);
									findex.setDisplay(ftable.getName()+"表索引");
									findex.setQueryid(queryid);
									findex.setReportid(null);
									String field = "";
//									如果条件中有多个字段，则只取前三个字段的索引。
									for(int z=0;z<table2list.size();z++){
										if(z<3){
											if(z<2 && z < table2list.size()-1)
												field +=table2list.get(z)+",";
											else
												field +=table2list.get(z);
										}					
									}
									if(indexfieldmap.get(field) == null){
										indexfieldmap.put(field, field);
										findex.setFieldList(field);
										ftable.getIndexLst().add(findex);
										table2xml = findex.creatDefineXml(2);
										result.addAll(findex.getCreateSql());
									}
								}				
							}
						}
					
					}			
			seedade = (SeeyonDataDefine) fapp.getDataDefine();
			seedade.createIndex(result);
			seedade.setDataDefine(define);
			
			FormAppMain foapp = new FormAppMain();
			foapp.setId(fapp.getId());
			foapp = (FormAppMain) getFormDaoManager().queryAllData(foapp).get(0);
			Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(foapp.getDataStructure()).getRootElement();	
			//组datadefine中的xml
			List spanList = dataoldroot.selectNodes("//Table");
			for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Table节点
				Element span = (Element)spanList.get(ispan);
				String tablename = span.attribute("name").getValue();
				if(tablename.equals(table1)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					Element tableroot = dom4jxmlUtils.paseXMLToDoc(table1xml).getRootElement();	
					indexlist.add((Element)tableroot);
				}else if(tablename.equals(table2)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					Element tableroot = dom4jxmlUtils.paseXMLToDoc(table2xml).getRootElement();	
					indexlist.add((Element)tableroot);
				}
			}
			foapp.setDataStructure(dataoldroot.asXML());
			getFormDaoManager().updateAppMain(foapp);
			
			//加载到app中
			SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
			ISeeyonFormAppManager fmanager=fruntime.getAppManager();
//			08-05-21修改
			//fmanager.unRegApp(fapp.getAppName());
			fmanager.unRegApp(fapp.getId());
			fmanager.regApp(fapp);
			//sregApp(fapp.getAppName());
	      }catch(Exception e){
			//e.printStackTrace();
			log.error("执行查询时建立索引出错", e);
			//throw new DataDefineException(1,"事物实例失败，建立索引出错","事物实例失败，建立索引出错");
		  }
		}
	 
	 /**
	  *  执行统计时动态建立索引。
	  */
	 public void formindexbyreport(ISeeyonForm_Application fapp,String id) throws SeeyonFormException{
		 HashMap indexfieldmap = new HashMap();
		 try {
				List<String> resultreport = new ArrayList<String>();  //统计条件中组成的所有sql
				HashMap tablefieldMap3 = new HashMap(); //统计条件中表名一下所有的字段名称
				HashMap tablefieldMap4 = new HashMap(); //统计条件中表名二下所有的字段名称
				List table3list = new ArrayList(); //统计条件中表名一下所有的字段名称
				List table4list = new ArrayList(); //统计条件中表名二下所有的字段名称
				String table3xml = ""; //统计条件中表名一所组成的索引xml
				String table4xml = ""; //统计条件中表名二所组成的索引xml
				List table2xmllist = new ArrayList();
				String table3 = "";   //统计条件中表名一
				String table4 = "";  //统计条件中表名二
			    HashMap oldindexreportmap = new HashMap();
				SeeyonDataDefine seedade = null;
				DataDefine  define = null;
				for(int i =0;i<fapp.getReportList().size();i++){
					SeeyonReportImpl seeyon = (SeeyonReportImpl)fapp.getReportList().get(i);
					String reportid = seeyon.getId();
					if(id.equals(reportid)){
						ConditionListImpl condmpl =(ConditionListImpl)seeyon.getFilter();
						ConditionListReportImpl conditionimp = (ConditionListReportImpl)seeyon.getUserConditionList();
						if(conditionimp !=null){
							//用户自定义条件
							for(int j=0;j<conditionimp.getConditionList().size();j++){
								if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
									if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(j==0){
											table3 = tablename;
										}if(table3.equals(tablename)){
											if(tablefieldMap3.get(fieldname)==null){
												tablefieldMap3.put(fieldname, fieldname);
												table3list.add(fieldname);
											}									
										}else{
											table4 = tablename;
											tablefieldMap4.put(fieldname, fieldname);
											table4list.add(fieldname);
										}
									}
								}
							}
						}
						if(condmpl !=null){
							//限制条件
							for(int j=0;j<condmpl.getConditionList().size();j++){
								if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
									if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(table4.equals(tablename)){									
											if(tablefieldMap4.get(fieldname)==null){
												tablefieldMap4.put(fieldname, fieldname);
												table4list.add(fieldname);
											}	
										}else if(table3.equals(tablename)){									
											if(tablefieldMap3.get(fieldname)==null){
												tablefieldMap3.put(fieldname, fieldname);
												table3list.add(fieldname);
											}	
										}else if("".equals(table3)){
											if(j==0){
												table3 = tablename;
												tablefieldMap3.put(fieldname, fieldname);
												table3list.add(fieldname);
											}
										}else{
											if("".equals(table4))
											    table4 = tablename;
											tablefieldMap4.put(fieldname, fieldname);
											table4list.add(fieldname);
										}
									}
								}
							}
						}
						Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)fapp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
						define = new DataDefine();
						define.loadFromXml(oldroot);
						for(int x=0;x<define.getTableLst().size();x++){
							FormTable ftable = define.getTableLst().get(x);
							for(int a = 0;a<ftable.getIndexLst().size();a++){
								FormIndex findex1 = ftable.getIndexLst().get(a);
								indexfieldmap.put(findex1.getFieldList(), findex1.getFieldList());
							}
							if(ftable.getName().equals(table3) && table3list.size() !=0){
								FormIndex findex = new FormIndex(ftable);
								findex.setId(reportid+"_m");
								findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
								findex.setFOnwerTable(ftable);
								findex.setDisplay(ftable.getName()+"表索引");
								findex.setReportid(reportid);
								findex.setQueryid(null);
								String field = "";
//								如果条件中有多个字段，则只取前三个字段的索引。
								for(int z=0;z<table3list.size();z++){
									if(z<3){
										if(z<2 && z < table3list.size()-1)
											field +=table3list.get(z)+",";
										else
											field +=table3list.get(z);
									}					
								}
								if(indexfieldmap.get(field) ==null){
									indexfieldmap.put(field, field);
									findex.setFieldList(field);
									ftable.getIndexLst().add(findex);
									table3xml = findex.creatDefineXml(2);
									resultreport.addAll(findex.getCreateSql());
								}
								
							}if(ftable.getName().equals(table4) && table4list.size() !=0){
								FormIndex findex = new FormIndex(ftable);
								findex.setId(reportid+"_s");
								findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
								findex.setFOnwerTable(ftable);
								findex.setDisplay(ftable.getName()+"表索引");
								findex.setReportid(reportid);
								findex.setQueryid(null);
								String field = "";
								//如果条件中有多个字段，则只取前三个字段的索引。
								for(int z=0;z<table4list.size();z++){
									if(z<3){
										if(z<2 && z < table4list.size()-1)
											field +=table4list.get(z)+",";
										else
											field +=table4list.get(z);
									}					
								}
								if(indexfieldmap.get(field) ==null){
									indexfieldmap.put(field, field);
									findex.setFieldList(field);
									ftable.getIndexLst().add(findex);
									table4xml = findex.creatDefineXml(2);
									resultreport.addAll(findex.getCreateSql());
								}
							}				
						}
					}
					}
			seedade = (SeeyonDataDefine) fapp.getDataDefine();
			seedade.createIndex(resultreport);
			seedade.setDataDefine(define);
			
			FormAppMain foapp = new FormAppMain();
			foapp.setId(fapp.getId());
			foapp = (FormAppMain) getFormDaoManager().queryAllData(foapp).get(0);
			Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(foapp.getDataStructure()).getRootElement();	
			//组datadefine中的xml
			List spanList = dataoldroot.selectNodes("//Table");
			for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Table节点
				Element span = (Element)spanList.get(ispan);
				String tablename = span.attribute("name").getValue();
				if(tablename.equals(table3)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					Element tableroot = dom4jxmlUtils.paseXMLToDoc(table3xml).getRootElement();	
					indexlist.add((Element)tableroot);
				}else if(tablename.equals(table4)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					Element tableroot = dom4jxmlUtils.paseXMLToDoc(table4xml).getRootElement();	
					indexlist.add((Element)tableroot);
				}
			}
			foapp.setDataStructure(dataoldroot.asXML());
			getFormDaoManager().updateAppMain(foapp);		
			//加载到app中
			SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
			ISeeyonFormAppManager fmanager=fruntime.getAppManager();
//			08-05-21修改
			//fmanager.unRegApp(fapp.getAppName());
			fmanager.unRegApp(fapp.getId());
			fmanager.regApp(fapp);
			//sregApp(fapp.getAppName());
	      }catch(Exception e){
			//e.printStackTrace();
			log.error("执行统计时建立索引出错", e);
			//throw new DataDefineException(1,"事物实例失败，建立索引出错","事物实例失败，建立索引出错");
		  }
		}
	 
	 /**
	  * 修改表单时如果有索引则删除原来的索引，按照新的查询，统计条件建立索引。
	  * @param fapp
	  * @param sessionobject
	  * @param fam
	  * @throws SeeyonFormException
	  */
	 public void formcompareindex(ISeeyonForm_Application fapp,SessionObject sessionobject,FormAppMain fam) throws SeeyonFormException{
		 HashMap indexfieldmap = new HashMap();
		 HashMap oldindequeryxmap = new HashMap();
	     HashMap oldindexreportmap = new HashMap();
	     List<String> resultreport = new ArrayList<String>();  //统计条件中组成的所有sql
	        List table1xmllist = new ArrayList();
			List table2xmllist = new ArrayList();
			List table3xmllist = new ArrayList();
			List table4xmllist = new ArrayList();
			String table3 = "";   //统计条件中表名一
			String table4 = "";  //统计条件中表名二
	
			String table1 = "";   //查询条件中表名一
			String table2 = "";  //查询条件中表名二
			SeeyonDataDefine seedade = null;
			DataDefine  define = null;
		 List<String> result = new ArrayList<String>();	
		 List<String> resultdel = new ArrayList<String>();	
		 try{
			 
			 Element oldroot = null;
			 int findexfir = fam.getDataStructure().indexOf("<DataDefine");
	     	 int findexecd = fam.getDataStructure().indexOf("</DataDefine>");
	     	 if (findexfir > 0 && findexecd > 0){
				fam.getDataStructure().substring(findexfir, findexecd+13);
				oldroot = dom4jxmlUtils.paseXMLToDoc(fam.getDataStructure().substring(findexfir, findexecd+13)).getRootElement();
				define = new DataDefine();
				define.loadFromXml(oldroot);
			 } 
		 //把原来的旧索引删除
			for(int i=0;i<define.getTableLst().size();i++){
				FormTable fable = (FormTable)define.getTableLst().get(i);				
				for(int j=0;j<fable.getIndexLst().size();j++){
					FormIndex index = (FormIndex)fable.getIndexLst().get(j);
					resultdel.addAll(index.getDropSql());
					if(!"".equals(index.getQueryid()) && !"null".equals(index.getQueryid()) && index.getQueryid() !=null)
						oldindequeryxmap.put(index.getQueryid(), index.getQueryid());
					if(!"".equals(index.getReportid()) && !"null".equals(index.getReportid()) &&index.getReportid() !=null)
						oldindexreportmap.put(index.getReportid(),index.getReportid());
					fable.getIndexLst().remove(j);
					j--;
				}
			}
			 //把原来的旧索引xml删除
			Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(fam.getDataStructure()).getRootElement();						
			List spanList = dataoldroot.selectNodes("//Table");
			for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Table节点
				Element span = (Element)spanList.get(ispan);
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					List indexvaluelist = indexlist.selectNodes(".//Index");
					for(int i = 0;i<indexvaluelist.size() ; i++){
						Element indexvalue = (Element)indexvaluelist.get(i);
						indexlist.remove(indexvalue);
					}	
			}
			fam.setDataStructure(dataoldroot.asXML());
			for(int i =0;i<fapp.getQueryList().size();i++){
				HashMap tablefieldMap1 = new HashMap(); //查询条件中表名一下所有的字段名称
				HashMap tablefieldMap2 = new HashMap(); //查询条件中表名二下所有的字段名称
				List table1list = new ArrayList(); //查询条件中表名一下所有的字段名称
				List table2list = new ArrayList(); //查询条件中表名二下所有的字段名称
				String table1xml = ""; //查询条件中表名一所组成的索引xml
				String table2xml = ""; //查询条件中表名二所组成的索引xml
				SeeyonQueryImpl seeyon = (SeeyonQueryImpl)fapp.getQueryList().get(i);
				String queryid = seeyon.getId();
					//在表单制作中进行动态索引的新建
					if(oldindequeryxmap.get(queryid) !=null){
						//用户自定义的条件
						ConditionListImpl condmpl =(ConditionListImpl)seeyon.getFilter();
						ConditionListQueryImpl conditionimp = (ConditionListQueryImpl)seeyon.getUserConditionList();
						if(conditionimp !=null){
							for(int j=0;j<conditionimp.getConditionList().size();j++){
								if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
									if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(j==0){
											table1 = tablename;
										}if(table1.equals(tablename)){
											if(tablefieldMap1.get(fieldname)==null){
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
											}									
										}else{
											table2 = tablename;
											tablefieldMap2.put(fieldname, fieldname);
											table2list.add(fieldname);
										}
									}
								}
							}
						}
						if(condmpl !=null){
							//限制条件
							for(int j=0;j<condmpl.getConditionList().size();j++){
								if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
									if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(table2.equals(tablename)){									
											if(tablefieldMap2.get(fieldname)==null){
												tablefieldMap2.put(fieldname, fieldname);
												table2list.add(fieldname);
											}	
										}else if(table1.equals(tablename)){									
											if(tablefieldMap1.get(fieldname)==null){
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
											}	
										}else if("".equals(table1)){
											if(j==0){
												table1 = tablename;
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
											}
										}else{
											if("".equals(table2))
											    table2 = tablename;
											tablefieldMap2.put(fieldname, fieldname);
											table2list.add(fieldname);
										}
									}
								}
							}
						}
						//组define中的index对象
						for(int x=0;x<define.getTableLst().size();x++){
							FormTable ftable = define.getTableLst().get(x);
							if(ftable.getName().equals(table1) && table1list.size() !=0){
								FormIndex findex = new FormIndex(ftable);
								findex.setId(queryid+"_m");
								findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+queryid.substring(1));
								findex.setFOnwerTable(ftable);
								findex.setDisplay(ftable.getName()+"表索引");
								findex.setQueryid(queryid);
								findex.setReportid(null);
								String field = "";
								//条件中如果有多个字段，在建立索引的时候只取前三个字段
								for(int z=0;z<table1list.size();z++){
									if(z<3){
										if(z<2 && z < table1list.size()-1)
											field +=table1list.get(z)+",";
										else
											field +=table1list.get(z);
									}					
								}
								findex.setFieldList(field);
								if(indexfieldmap.get(field) == null){
									indexfieldmap.put(field, field);//字段名称
									ftable.getIndexLst().add(findex);
									table1xml = findex.creatDefineXml(2);
									table1xmllist.add(table1xml);
									result.addAll(findex.getCreateSql());
								}							
							}if(ftable.getName().equals(table2) && table2list.size() !=0){
								FormIndex findex = new FormIndex(ftable);
								findex.setId(queryid+"_s");
								findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+queryid.substring(1));
								findex.setFOnwerTable(ftable);
								findex.setDisplay(ftable.getName()+"表索引");
								findex.setQueryid(queryid);
								findex.setReportid(null);
								String field = "";
//								条件中如果有多个字段，在建立索引的时候只取前三个字段
								for(int z=0;z<table2list.size();z++){
									if(z<3){
										if(z<2 && z < table2list.size()-1)
											field +=table2list.get(z)+",";
										else
											field +=table2list.get(z);
									}					
								}
								if(indexfieldmap.get(field) == null){
								    indexfieldmap.put(field, field);//字段名称
									findex.setFieldList(field);
									ftable.getIndexLst().add(findex);
									table2xml = findex.creatDefineXml(2);
									table2xmllist.add(table2xml);
									result.addAll(findex.getCreateSql());
								}
							}				
						}
					}
				}
			//统计中的条件
			for(int i =0;i<fapp.getReportList().size();i++){
				HashMap tablefieldMap3 = new HashMap(); //统计条件中表名一下所有的字段名称
				HashMap tablefieldMap4 = new HashMap(); //统计条件中表名二下所有的字段名称
				List table3list = new ArrayList(); //统计条件中表名一下所有的字段名称
				List table4list = new ArrayList(); //统计条件中表名二下所有的字段名称
				String table3xml = ""; //统计条件中表名一所组成的索引xml
				String table4xml = ""; //统计条件中表名二所组成的索引xml
				SeeyonReportImpl seeyon = (SeeyonReportImpl)fapp.getReportList().get(i);
				String reportid = seeyon.getId();
				if(oldindexreportmap.get(reportid) !=null){
					ConditionListImpl condmpl =(ConditionListImpl)seeyon.getFilter();
					ConditionListReportImpl conditionimp = (ConditionListReportImpl)seeyon.getUserConditionList();
					if(conditionimp !=null){
						//用户自定义的条件
						for(int j=0;j<conditionimp.getConditionList().size();j++){
							if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
								DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
								if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
									String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
									String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
									if(j==0){
										table3 = tablename;
									}if(table3.equals(tablename)){
										if(tablefieldMap3.get(fieldname)==null){
											tablefieldMap3.put(fieldname, fieldname);
											table3list.add(fieldname);
										}									
									}else{
										table4 = tablename;
										tablefieldMap4.put(fieldname, fieldname);
										table4list.add(fieldname);
									}
								}
							}
						}
					}
					if(condmpl !=null){
						//限制条件
						for(int j=0;j<condmpl.getConditionList().size();j++){
							if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
								DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
								if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
									String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
									String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
									if(table4.equals(tablename)){									
										if(tablefieldMap4.get(fieldname)==null){
											tablefieldMap4.put(fieldname, fieldname);
											table4list.add(fieldname);
										}	
									}else if(table3.equals(tablename)){									
										if(tablefieldMap3.get(fieldname)==null){
											tablefieldMap3.put(fieldname, fieldname);
											table3list.add(fieldname);
										}	
									}else if("".equals(table3)){
										if(j==0){
											table3 = tablename;
											tablefieldMap3.put(fieldname, fieldname);
											table3list.add(fieldname);
										}
									}else{
										if("".equals(table4))
										    table4 = tablename;
										tablefieldMap4.put(fieldname, fieldname);
										table4list.add(fieldname);
									}
								}
							}
						}
					}
	                //组dedine中的index对象
					for(int x=0;x<define.getTableLst().size();x++){
						FormTable ftable = define.getTableLst().get(x);
						if(ftable.getName().equals(table3) && table3list.size() !=0){
							FormIndex findex = new FormIndex(ftable);
							findex.setId(reportid+"_m");
							findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
							findex.setFOnwerTable(ftable);
							findex.setDisplay(ftable.getName()+"表索引");
							findex.setReportid(reportid);
							findex.setQueryid(null);
							String field = "";
							for(int z=0;z<table3list.size();z++){
								if(z<3){
									if(z<2 && z < table3list.size()-1)
										field +=table3list.get(z)+",";
									else
										field +=table3list.get(z);
								}					
							}
							if(indexfieldmap.get(field) == null){
								indexfieldmap.put(field, field);
								findex.setFieldList(field);
								ftable.getIndexLst().add(findex);
								table3xml = findex.creatDefineXml(2);
								table3xmllist.add(table3xml);
								resultreport.addAll(findex.getCreateSql());
							}
						}if(ftable.getName().equals(table4) && table4list.size() !=0){
							FormIndex findex = new FormIndex(ftable);
							findex.setId(reportid+"_s");
							findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
							findex.setFOnwerTable(ftable);
							findex.setDisplay(ftable.getName()+"表索引");
							findex.setReportid(reportid);
							findex.setQueryid(null);
							String field = "";
							for(int z=0;z<table4list.size();z++){
								if(z<3){
									if(z<2 && z < table4list.size()-1)
										field +=table4list.get(z)+",";
									else
										field +=table4list.get(z);
								}					
							}
							if(indexfieldmap.get(field) == null){
								indexfieldmap.put(field, field);
								findex.setFieldList(field);
								ftable.getIndexLst().add(findex);
								table4xml = findex.creatDefineXml(2);
								table4xmllist.add(table4xml);
								resultreport.addAll(findex.getCreateSql());
							}
						}				
					}
				}
				}
			seedade = (SeeyonDataDefine) fapp.getDataDefine();
			//删除所有索引
			seedade.dropIndex(resultdel);
			//建立查询设置中的索引
			seedade.createIndex(result);
			//建立统计设置中的索引
			seedade.createIndex(resultreport);
			seedade.setDataDefine(define);		
			Element datanewroot = dom4jxmlUtils.paseXMLToDoc(dataoldroot.asXML()).getRootElement();	
			//组datadefine中的xml
			List spannewList = datanewroot.selectNodes("//Table");
			for(int ispan = 0; ispan < spannewList.size(); ispan++){//所有Table节点
				Element span = (Element)spannewList.get(ispan);
				String tablename = span.attribute("name").getValue();
				if(tablename.equals(table1)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					for(int i=0;i<table1xmllist.size();i++){
						Element tableroot = dom4jxmlUtils.paseXMLToDoc((String)table1xmllist.get(i)).getRootElement();	
						indexlist.add((Element)tableroot);
					}			
				}else if(tablename.equals(table2)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					for(int i=0;i<table2xmllist.size();i++){
						Element tableroot = dom4jxmlUtils.paseXMLToDoc((String)table2xmllist.get(i)).getRootElement();	
						indexlist.add((Element)tableroot);
					}	
				}
				if(tablename.equals(table3)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					for(int i=0;i<table3xmllist.size();i++){
						Element tableroot = dom4jxmlUtils.paseXMLToDoc((String)table3xmllist.get(i)).getRootElement();	
						indexlist.add((Element)tableroot);
					}	
				}else if(tablename.equals(table4)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					for(int i=0;i<table4xmllist.size();i++){
						Element tableroot = dom4jxmlUtils.paseXMLToDoc((String)table4xmllist.get(i)).getRootElement();	
						indexlist.add((Element)tableroot);
					}	
				}
			}
			fam.setDataStructure(datanewroot.asXML());
			getFormDaoManager().updateAppMain(fam);		
			//加载到app中
			SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
			ISeeyonFormAppManager fmanager=fruntime.getAppManager();
//			08-05-21修改
			//fmanager.unRegApp(fapp.getAppName());
			fmanager.unRegApp(fapp.getId());
			editregApp(fam);
		 }catch(Exception e){
				//e.printStackTrace();
				log.error("全部保存时时建立索引出错", e);
				//throw new DataDefineException(1,"事物实例失败，建立索引出错","事物实例失败，建立索引出错");
			  }	
	 }
	 /*
	 public void formindexcompare(ISeeyonForm_Application fapp,SessionObject sessionobject,FormAppMain fam) throws SeeyonFormException{

		 try {
				//对数据库操作
			     HashMap oldindequeryxmap = new HashMap();
			     HashMap oldindexreportmap = new HashMap();
				 List<String> result = new ArrayList<String>();
				 List<String> resultcompare = new ArrayList<String>();
				 HashMap queryindexmap = new HashMap();
				 SeeyonDataDefine seedade =(SeeyonDataDefine) fapp.getDataDefine();
				 DataDefine  define = null;				 
				 HashMap tablexmlmap = new HashMap();
				 List<FormIndex> addindexlist = new ArrayList<FormIndex>();
				 //把sessionobject中的所有查询数据范围和用户输入条件得到
				 for(int i =0;i<sessionobject.getQueryConditionList().size();i++){					
					 SeeyonQueryImpl seeyon = (SeeyonQueryImpl)fapp.getQueryList().get(0);
					 DBProviderImpl daprovider = seeyon.getDBProvider();
					 QueryObject queryobject = sessionobject.getQueryConditionList().get(i);
					 HashMap tablefieldMap1 = new HashMap();
					 HashMap tablefieldMap2 = new HashMap();
					 String table1 = "";
					 String table2 = "";
					 List table1list = new ArrayList();
					 List table2list = new ArrayList();					
					 Document QueryConditionDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(queryobject.getQueryConditionValue()));
					 Element QueryConditionRoot = QueryConditionDoc.getRootElement();
					 ConditionListQueryImpl conditionimp = new ConditionListQueryImpl();
					 conditionimp.loadFromXml(QueryConditionRoot);			 
					 if(conditionimp !=null){
							for(int j=0;j<conditionimp.getConditionList().size();j++){
								if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
									if(daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(j==0){
											table1 = tablename;
										}if(table1.equals(tablename)){
											if(tablefieldMap1.get(fieldname)==null){
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
											}									
										}else{
											table2 = tablename;
											tablefieldMap2.put(fieldname, fieldname);
											table2list.add(fieldname);
										}
									}
								}
							}
						}
					 Document QueryAreaDoc = dom4jxmlUtils.paseXMLToDoc(xmlHead1 + delTrailSection(queryobject.getQueryAreaValue()));
					 Element QueryAreaRoot = QueryAreaDoc.getRootElement();
					 ConditionListImpl condmpl = new ConditionListImpl();
					 condmpl.loadFromXml(QueryAreaRoot);
						if(condmpl !=null){
							for(int j=0;j<condmpl.getConditionList().size();j++){
								if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
									DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
									if(daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
										String fieldname = daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
										String tablename = daprovider.getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
										if(table2.equals(tablename)){									
											if(tablefieldMap2.get(fieldname)==null){
												tablefieldMap2.put(fieldname, fieldname);
												table2list.add(fieldname);
											}	
										}else if(table1.equals(tablename)){									
											if(tablefieldMap1.get(fieldname)==null){
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
											}	
										}else if("".equals(table1)){											
												table1 = tablename;
												tablefieldMap1.put(fieldname, fieldname);
												table1list.add(fieldname);
										}else{
											if("".equals(table2))
											    table2 = tablename;
											tablefieldMap2.put(fieldname, fieldname);
											table2list.add(fieldname);
										}
									}
								}
							}
						}
						String field = "";
						//每张表的查询条件中所有字段只建立前三个字段的索引。
						for(int z=0;z<table1list.size();z++){
							if(z<3){
								if(z<2 && z < table1list.size()-1)
									field +=table1list.get(z)+",";
								else
									field +=table1list.get(z);
							}					
						}
						
						String field2 = "";
						for(int z=0;z<table2list.size();z++){
							if(z<3){
								if(z<2 && z < table2list.size()-1)
									field2 +=table2list.get(z)+",";
								else
									field2 +=table2list.get(z);
							}					
						}
						Matchdata matchdata = new Matchdata();
						matchdata.setValue(field);
						matchdata.setValue2(field2);
						matchdata.setTableName(table1);
						matchdata.setTableName2(table2);
						queryindexmap.put(queryobject.getQueryId(), matchdata);	
				 }
				 
				 //将旧app中的索引和session中新的索引相比较，做相应的删除和修改
					Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)fapp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
					define = new DataDefine();
					define.loadFromXml(oldroot);			
					for(int x=0;x<define.getTableLst().size();x++){
						String flag = "flase";
						FormTable ftable = define.getTableLst().get(x);
						String tablename =ftable.getName();
						for(int y=0;y<ftable.getIndexLst().size();y++){
							FormIndex index = ftable.getIndexLst().get(y);
							if(queryindexmap.get(index.getQueryid()) == null){
								ftable.getIndexLst().remove(y);
								result.addAll(index.getDropSql());
								//seedade.dropIndex(result);
							}else{
								Matchdata matchdata = (Matchdata) queryindexmap.get(index.getQueryid());
								if(x == 0){
									FormIndex newindex = new FormIndex(null);
									newindex.setId(index.getId());
									newindex.setFName(index.getName());
									newindex.setQueryid(index.getQueryid());
									newindex.setDisplay(index.getDisplay());
									newindex.setFOnwerTable(index.getFOnwerTable());
									if(matchdata.getTableName().indexOf("formmain") !=-1){
										newindex.setFieldList(matchdata.getValue());
									}else{
										newindex.setFieldList(matchdata.getValue2());
									}									
									index.compareObjToSQLs(newindex);
									resultcompare.addAll(index.compareObjToSQLs(newindex));
									index.setFieldList(newindex.getFieldList());
								}else{
									FormIndex newindex = new FormIndex(null);
									newindex.setId(index.getId());
									newindex.setQueryid(index.getQueryid());
									newindex.setFOnwerTable(index.getFOnwerTable());
									if(matchdata.getTableName2().indexOf("formson") !=-1){
										if(tablename.equals(matchdata.getTableName2())){
											newindex.setFName(index.getName());
											newindex.setDisplay(index.getDisplay());								
										}else{											
											newindex.setFName("index"+changetablemainnumber(tablename)+"_"+index.getQueryid().substring(1));								
											newindex.setDisplay(tablename+"表索引");
											index.setFName("index"+changetablemainnumber(tablename)+"_"+index.getQueryid().substring(1));
											index.setDisplay(tablename+"表索引");
										}
										newindex.setFieldList(matchdata.getValue2());
									}else{
										if(tablename.equals(matchdata.getTableName())){
											newindex.setFName(index.getName());
											newindex.setDisplay(index.getDisplay());								
										}else{
											flag = "true";
											newindex.setFName("index"+changetablemainnumber(matchdata.getTableName())+"_"+index.getQueryid().substring(1));								
											newindex.setDisplay(matchdata.getTableName()+"表索引");											
											//index.setFName("index_"+matchdata.getTableName()+"_"+index.getQueryid().substring(1));
											//index.setDisplay(matchdata.getTableName()+"表索引");
											newindex.setFieldList(matchdata.getValue());
											for(int a=0;a<define.getTableLst().size();a++){
												FormTable ft = (FormTable)define.getTableLst().get(a);
												if(matchdata.getTableName().equals(ft.getName())){
													newindex.setFOnwerTable(ft);
													addindexlist.add(newindex);
													//ft.getIndexLst().add(newindex);
												}
											}
										}										
										newindex.setFieldList(matchdata.getValue());
									}									
									resultcompare.addAll(index.compareObjToSQLs(newindex));
									//index.setFieldList(newindex.getFieldList());
									if(flag.equals("true"))
										ftable.getIndexLst().remove(y);
								}						
							}
						}		 
					}
					for(int i=0;i<define.getTableLst().size();i++){
						FormTable ft = (FormTable)define.getTableLst().get(i);
						for(int j =0;j<addindexlist.size();j++){
							if(ft.getName().equals(addindexlist.get(j).getOwnerTable().getName()))
								ft.getIndexLst().add(addindexlist.get(j));
						}
					}
				
					//把<Define>中的旧索引删除，重新组索引的xml
					for(int i=0;i<define.getTableLst().size();i++){
						FormTable fable = (FormTable)define.getTableLst().get(i);
						StringBuffer sb = new StringBuffer(); 					
						for(int j=0;j<fable.getIndexLst().size();j++){
							FormIndex index = (FormIndex)fable.getIndexLst().get(j);
							sb.append(index.creatDefineXml(2));
						}
						if(fable.getIndexLst().size() !=0)
						   tablexmlmap.put(fable.getName(), sb.toString());
					}				
					Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(fam.getDataStructure()).getRootElement();						
					List spanList = dataoldroot.selectNodes("//Table");
					for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Table节点
						Element span = (Element)spanList.get(ispan);
						String tablename = span.attribute("name").getValue();
						if(tablexmlmap.get(tablename) !=null){
							List indexlistList = span.selectNodes(".//IndexList");
							Element indexlist = (Element)indexlistList.get(0);
							List indexvaluelist = indexlist.selectNodes(".//Index");
							for(int i = 0;i<indexvaluelist.size() ; i++){
								Element indexvalue = (Element)indexvaluelist.get(i);
								indexlist.remove(indexvalue);
							}
							//indexlist.addText((String) tablexmlmap.get(tablename));
							Element tableroot = dom4jxmlUtils.paseXMLToDoc((String)tablexmlmap.get(tablename)).getRootElement();	
							indexlist.add((Element)tableroot);
						}else{
							List indexlistList = span.selectNodes(".//IndexList");
							Element indexlist = (Element)indexlistList.get(0);
							List indexvaluelist = indexlist.selectNodes(".//Index");
							for(int i = 0;i<indexvaluelist.size(); i++){
								Element indexvalue = (Element)indexvaluelist.get(i);
								indexlist.remove(indexvalue);
							}
						}
					}
				fam.setDataStructure(dataoldroot.asXML());

				seedade.dropIndex(result);
				seedade.compareIndex(resultcompare);
				seedade =(SeeyonDataDefine) fapp.getDataDefine();
				seedade.setDataDefine(define);
				getFormDaoManager().updateAppMain(fam);	
				if(fam.getState()==1 ||fam.getState()==2){
                    //加载到app中
					SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
					ISeeyonFormAppManager fmanager=fruntime.getAppManager();
					fmanager.unRegApp(fapp.getAppName());
					fmanager.regApp(fapp);
				}
			}catch(Exception e){
				e.printStackTrace();			
				log.error("表单修改全部保存时建立索引出错", e);
				//throw new DataDefineException(1,"事物实例失败，建立索引出错","事物实例失败，建立索引出错");
			} 
	 }
	 */
	 /*
	 public void formreportindex(ISeeyonForm_Application fapp,String id) throws SeeyonFormException{

		 try {
			List<String> result = new ArrayList<String>();
			HashMap tablefieldMap1 = new HashMap();
			HashMap tablefieldMap2 = new HashMap();
			List table1list = new ArrayList();
			List table2list = new ArrayList();
			String table1xml = "";
			String table2xml = "";
			List table2xmllist = new ArrayList();
			String table1 = "";
			String table2 = "";
			SeeyonDataDefine seedade = null;
			DataDefine  define = null;
			for(int i =0;i<fapp.getReportList().size();i++){
				SeeyonReportImpl seeyon = (SeeyonReportImpl)fapp.getReportList().get(i);
				String reportid = seeyon.getId();
				if(id.equals(reportid)){
					ConditionListImpl condmpl =(ConditionListImpl)seeyon.getFilter();
					ConditionListReportImpl conditionimp = (ConditionListReportImpl)seeyon.getUserConditionList();
					if(conditionimp !=null){
						for(int j=0;j<conditionimp.getConditionList().size();j++){
							if(conditionimp.getConditionList().get(j) instanceof DataColumImpl){
								DataColumImpl querydefine = (DataColumImpl)conditionimp.getConditionList().get(j);
								if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
									String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
									String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
									if(j==0){
										table1 = tablename;
									}if(table1.equals(tablename)){
										if(tablefieldMap1.get(fieldname)==null){
											tablefieldMap1.put(fieldname, fieldname);
											table1list.add(fieldname);
										}									
									}else{
										table2 = tablename;
										table2list.add(fieldname);
									}
								}
							}
						}
					}
					if(condmpl !=null){
						for(int j=0;j<condmpl.getConditionList().size();j++){
							if(condmpl.getConditionList().get(j) instanceof DataColumImpl){
								DataColumImpl querydefine = (DataColumImpl)condmpl.getConditionList().get(j);
								if(seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())) !=null){
									String fieldname = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBFieldName();
									String tablename = seeyon.getDBProvider().getDataSource().findDataAreaByName(OperHelper.noNamespace(querydefine.getColumName())).getDBTableName();
									if("".equals(table1)){
										if(j==0){
											table1 = tablename;
										}
									}if(table1.equals(tablename)){									
										if(tablefieldMap1.get(fieldname)==null){
											tablefieldMap2.put(fieldname, fieldname);
											table1list.add(fieldname);
										}	
									}else{
										if("".equals(table2))
										    table2 = tablename;
										table2list.add(fieldname);
									}
								}
							}
						}
					}
					Element oldroot = dom4jxmlUtils.paseXMLToDoc(((SeeyonDataDefine)fapp.getDataDefine()).getDataDefine().creatDefineXml(0)).getRootElement();	
					define = new DataDefine();
					define.loadFromXml(oldroot);
					for(int x=0;x<define.getTableLst().size();x++){
						FormTable ftable = define.getTableLst().get(x);
						if(ftable.getName().equals(table1)){
							FormIndex findex = new FormIndex(ftable);
							findex.setId(reportid+"_m");
							findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
							findex.setFOnwerTable(ftable);
							findex.setDisplay(ftable.getName()+"表索引");
							findex.setReportid(reportid);
							findex.setQueryid("");
							String field = "";
							for(int z=0;z<table1list.size();z++){
								if(z<3){
									if(z<2 && z < table1list.size()-1)
										field +=table1list.get(z)+",";
									else
										field +=table1list.get(z);
								}					
							}
							findex.setFieldList(field);
							ftable.getIndexLst().add(findex);
							table1xml = findex.creatDefineXml(2);
							result.addAll(findex.getCreateSql());
						}if(ftable.getName().equals(table2)){
							FormIndex findex = new FormIndex(ftable);
							findex.setId(reportid+"_s");
							findex.setFName("index"+changetablemainnumber(ftable.getName())+"_"+reportid.substring(1));
							findex.setFOnwerTable(ftable);
							findex.setDisplay(ftable.getName()+"表索引");
							findex.setReportid(reportid);
							findex.setQueryid("");
							String field = "";
							for(int z=0;z<table2list.size();z++){
								if(z<3){
									if(z<2 && z < table2list.size()-1)
										field +=table2list.get(z)+",";
									else
										field +=table2list.get(z);
								}					
							}
							findex.setFieldList(field);
							ftable.getIndexLst().add(findex);
							table2xml = findex.creatDefineXml(2);
							result.addAll(findex.getCreateSql());
						}				
					}
				}
				}
			seedade = (SeeyonDataDefine) fapp.getDataDefine();
			seedade.createIndex(result);
			seedade.setDataDefine(define);
			
			FormAppMain foapp = new FormAppMain();
			foapp.setId(fapp.getId());
			foapp = (FormAppMain) getFormDaoManager().queryAllData(foapp).get(0);
			Element dataoldroot = dom4jxmlUtils.paseXMLToDoc(foapp.getDataStructure()).getRootElement();	
			
			List spanList = dataoldroot.selectNodes("//Table");
			for(int ispan = 0; ispan < spanList.size(); ispan++){//所有Table节点
				Element span = (Element)spanList.get(ispan);
				String tablename = span.attribute("name").getValue();
				if(tablename.equals(table1)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					indexlist.addText(table1xml);
				}else if(tablename.equals(table2)){
					List indexlistList = span.selectNodes(".//IndexList");
					Element indexlist = (Element)indexlistList.get(0);
					indexlist.addText(table2xml);
				}
			}
			foapp.setDataStructure(dataoldroot.asXML().replaceAll("&lt;", "<").replace("&gt;", ">"));
			getFormDaoManager().updateAppMain(foapp);
			
			//加载到app中
			SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
			ISeeyonFormAppManager fmanager=fruntime.getAppManager();
			fmanager.unRegApp(fapp.getAppName());
			fmanager.regApp(fapp);
			//sregApp(fapp.getAppName());
	      }catch(Exception e){
			e.printStackTrace();
			log.error("执行统计时建立索引出错", e);
			//throw new DataDefineException(1,"事物实例失败，建立索引出错","事物实例失败，建立索引出错");
		  }
		}
	 */
	 private Session fSession;
	 public Long editpostdata(ISeeyonForm_Application fapp,FormAppMain fam,Element aRoot, IFormResoureProvider aProvider,SessionObject sessionobject) throws SeeyonFormException {
			FormTableValueDao formTableValueDao = (FormTableValueDao)SeeyonForm_Runtime.getInstance().getBean("formTableValueDao");
			fSession = formTableValueDao.getSessionFactory().openSession();
			//SessionFactory fsession = formTableValueDao.getsession();
			//fSession = fsession.openSession();
		
			Transaction tx = null;
			try {
				tx = fSession.beginTransaction();
				//对数据库操作
				//fSession.connection().setAutoCommit(false);
				modifyInfoToDB(fapp,fam,aRoot,aProvider,sessionobject);
				updateFormAppAttachMan(sessionobject,fam);	
				fSession.flush();
				tx.commit();

			}catch(Exception e){
				//e.printStackTrace();
				if(tx != null){
					tx.rollback();
				}
				log.error("表单修改全部保存时数据库保存出错", e);
				//throw new DataDefineException(1,"事物实例失败，数据库保存出错。","事物实例失败，数据库保存出错。");
				throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
			} finally {
				fSession.close();
				
			}
			return null;
		}
	 public Long newpostdata(Element aRoot, IFormResoureProvider aProvider,FormAppMain fam,SessionObject sessionobject) throws SeeyonFormException {
			FormTableValueDao formTableValueDao = (FormTableValueDao)SeeyonForm_Runtime.getInstance().getBean("formTableValueDao");
			Session fnewSession = formTableValueDao.getSessionFactory().openSession();
			Transaction tx = null;
			try {
				
				tx = fnewSession.beginTransaction();
				//对数据库操作
				fam = saveCreateInfoToDB(aRoot, aProvider,fam,fnewSession);
				if(fam != null){				
					//TODO 把使用人存入数据库	
					insertFormAppAttachMan(sessionobject,fam,fnewSession);
		            
					//把AccessObject存入数据库(统计、查询、菜单)
					if(sessionobject.getReportConditionList().size()!=0 ||sessionobject.getQueryConditionList().size() !=0
							||sessionobject.getFormAppAuthObjectMap().values().size()!=0 || sessionobject.getAppAuthObject().getAppOperAuthObjectMap().values().size() != 0){
						insertAccessObject(sessionobject,fnewSession);
					}
				}
				fnewSession.flush();
				tx.commit();

			}catch(Exception e){
				//e.printStackTrace();
				if(tx != null){
					tx.rollback();
				}
				log.error("表单新建完成时数据库保存出错", e);
				//throw new DataDefineException(1,"事物实例失败，数据库保存出错。","事物实例失败，数据库保存出错。");
				throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
			} finally {
				fnewSession.close();
				
			}
			return null;
		}
	 
	 public Long otherpostdata(Element aRoot, IFormResoureProvider aProvider,FormAppMain fam,SessionObject sessionobject) throws SeeyonFormException {
			FormTableValueDao formTableValueDao = (FormTableValueDao)SeeyonForm_Runtime.getInstance().getBean("formTableValueDao");
			Session fnewSession = formTableValueDao.getSessionFactory().openSession();
			Transaction tx = null;
			try {
				
				tx = fnewSession.beginTransaction();
				//对数据库操作
				fam = saveCreateInfoToDB(aRoot, aProvider,fam,fnewSession);
				if(fam != null){				
					//TODO 把使用人存入数据库	
					insertFormAppAttachMan(sessionobject,fam,fnewSession);
		            
					//把AccessObject存入数据库(统计、查询、菜单)
					if(sessionobject.getReportConditionList().size()!=0 ||sessionobject.getQueryConditionList().size() !=0
							||sessionobject.getFormAppAuthObjectMap().values().size()!=0 || sessionobject.getAppAuthObject().getAppOperAuthObjectMap().values().size() != 0){
						insertAccessObject(sessionobject,fnewSession);
					}
				}
				fnewSession.flush();
				tx.commit();

			}catch(Exception e){
				//e.printStackTrace();
				if(tx != null){
					tx.rollback();
				}
				log.error("表单另存时数据库保存出错", e);				
				throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.InstanceError.database"),Constantform.getString4CurrentUser("DataDefine.InstanceError.database"));
			} finally {
				fnewSession.close();
				
			}
			return null;
		}
	 
	 public static  String changetablemainnumber(String aName) {
			int findex = aName.indexOf("_");
			if (findex < 0)
				return aName;
			return aName.substring(findex+1);
		}
	 
	 
	    /**
		 * 
		 * 通过当前人员查询是否有表单资源
		 */
		 public boolean queryOwnerListByownerid(Long ownerId) throws DataDefineException{
			 List ownerlist = new ArrayList(); 			 
			 FormOwnerList fo= new FormOwnerList();
			 fo.setOwnerId(ownerId);			 
			 ownerlist = getFormDaoManager().queryOwnerIdByCondition(fo);
			 if(ownerlist.size() !=0)
			    return true;
			 else
				return false; 
		 }
		  /**
			 * 
			 * 表单管理员一次性将所属表单移交
			 */
		 public void updateOwnerListByownerid(Long ownerId) throws DataDefineException{		 
			 FormOwnerList fo= new FormOwnerList();
			 fo.setOwnerId(ownerId);			 
			 getFormDaoManager().updateOwnerList(fo);
		 }
		 
		public void LoadFromCab(SessionObject sessionobject) throws SeeyonFormException {
			// TODO Auto-generated method stub
			
		}

		public void editSave(SessionObject sessionobject, HttpServletRequest request) throws SeeyonFormException, SQLException {
			// TODO Auto-generated method stub
			
		}

		public void othereditSave(SessionObject sessionobject, HttpServletRequest request) throws SeeyonFormException, SQLException {
			// TODO Auto-generated method stub
			
		}
		
		
		public StringBuffer categoryHTML(TempleteCategoryManager templeteCategoryManager){
			User user = CurrentUser.get();
			long memberId = user.getId();
			long orgAccountId = user.getLoginAccount();

			List<TempleteCategory> templeteCategories = templeteCategoryManager.getCategorys(orgAccountId, 0);
			templeteCategories.addAll(templeteCategoryManager.getCategorys(orgAccountId, 4));
			
			StringBuffer categoryHTML = new StringBuffer();
			List<Long> categorys = new ArrayList<Long>();
			categorys.add(new Long(4));
			categorys.add(new Long(0));
			category2HTML(templeteCategories, categoryHTML, categorys, 1);
			
			return categoryHTML;
			
		}
		public static StringBuffer category2HTML(List<TempleteCategory> categories, 
				StringBuffer categoryHTML, List<Long> currentNode, int level){
			for (TempleteCategory category : categories) {
				Long parentId = category.getParentId();
				if(currentNode.contains(parentId)){
					
					categoryHTML.append("<option value='" + category.getId() + "' title='"+Strings.toHTML(category.getName())+"'>");
					
					for (int i = 0; i < level; i++) {
						categoryHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
					}
					
					categoryHTML.append(Strings.toHTML(category.getName()) + "</option>\n");
					List<Long> categorys = new ArrayList<Long>();
					categorys.add(category.getId());
					category2HTML(categories, categoryHTML, categorys, level + 1);
				}
			}
			
			return categoryHTML;
		}
		
		public List categoryList(Long categoryid) throws DataDefineException{
		    ArrayList categorylist = new ArrayList();
		    FormAppMain appmain = new FormAppMain();
		    appmain.setCategory(categoryid);
		    categorylist = (ArrayList)getFormDaoManager().queryAllData(appmain);
			return categorylist;		
		}
		
		public void updateByformstart(Long id,int formstart,SessionObject sessionobject) throws DataDefineException{
			TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
			OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			TempleteConfigManager templeteConfigManager = (TempleteConfigManager)ApplicationContextHolder.getBean("templeteConfigManager");
			SeeyonForm_Runtime fruntime = SeeyonForm_Runtime.getInstance();
			ISeeyonFormAppManager fmanager=fruntime.getAppManager();
			FormAppMain fm = getFormDaoManager().findApplicationById(id);
			if(formstart == 0){				
				if(sessionobject.getTemplateobj() != null){
					HashMap hash = sessionobject.getTemplateobj().getFlowMap();
					Iterator it = hash.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						//Templete templete = templeteManager.get((Long)entry.getKey());						
						//templete.setState(Templete.State.invalidation.ordinal());
						try{
                            //当停用表单模版时判断如果存在个人子模版，则将所有子模版状态置为不可用。
							List formTemlist = templeteManager.getTemplateByformParentId((Long)entry.getKey());
							for(int i = 0;i <formTemlist.size();i++){
								Templete tem = (Templete)formTemlist.get(i);
								templeteManager.updateTempleteState(tem.getId(),Templete.State.invalidation);
								templeteConfigManager.clearConfigByTempleteId(tem.getId());
							}
							//templeteManager.update(templete);
                            //将模板状态置成无效
							templeteManager.updateTempleteState((Long)entry.getKey(),Templete.State.invalidation);
							//取消首页配置
							templeteConfigManager.clearConfigByTempleteId((Long)entry.getKey());
						}catch(Exception e){
							log.error("删除模板授权异常", e);
							//throw new DataDefineException(1,"模板保存异常","模板保存异常");
							throw new DataDefineException(1,Constantform.getString4CurrentUser("DataDefine.TemplateSaveError"),Constantform.getString4CurrentUser("DataDefine.TemplateSaveError"));
						}
					}
				}
//				//发布状态的表单停用后从内存中删除
//				if(fm.getState()==1 ||fm.getState()==2){
//					if(fmanager.findById(fm.getId(),true) != null){
//						try {
//							fmanager.unRegApp(fm.getId());
//						} catch (SeeyonFormException e) {
//							log.error("卸载内存对象失败", e);
//						}
//					}
//				}
			}else if(formstart == 1){
				//更新为启用状态
				if(sessionobject.getTemplateobj() != null){
					HashMap hash = sessionobject.getTemplateobj().getFlowMap();
					Iterator it = hash.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						Templete templete = templeteManager.get((Long)entry.getKey());
						if(fm.getState()==1 ||fm.getState()==2)
						   templete.setState(Templete.State.normal.ordinal());
						
						List<Long> authMemberIdsList = new ArrayList<Long>();
                        Set<TempleteAuth> authList = templeteManager.get(templete.getId()).getTempleteAuths();
                        for (TempleteAuth auth : authList) {
							try {
								 Set<V3xOrgMember> memberSet = orgManager.getMembersByType(auth.getAuthType(), auth.getAuthId());
								if(memberSet != null){
	                                for(V3xOrgMember member : memberSet){
	                                    authMemberIdsList.add(member.getId());
	                                }
	                            }
							} catch (BusinessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            
                        }

                        //发布到首页
                        if(fm.getState()==1 ||fm.getState()==2){
                        	if(authMemberIdsList!= null && !authMemberIdsList.isEmpty()){
                            	templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templete.getId(), templete.getCategoryType());
                            }
    						//templeteManager.update(templete);
    						templeteManager.updateTempleteState((Long)entry.getKey(),Templete.State.normal);
                            //个人模板要启用。
                            List formTemlist = templeteManager.getTemplateByformParentId((Long)entry.getKey());
    						int perSize = formTemlist.size();
                            for(int i = 0;i <perSize;i++){
    							Templete tem = (Templete)formTemlist.get(i);
    							templeteManager.updateTempleteState(tem.getId(),Templete.State.normal);
    							templeteConfigManager.pushThisTempleteToMain4Member(tem.getMemberId(), tem.getId(), -1);
    						}
                        }
					}
				}
////				发布状态的表单启用后加载到内存中
//				if(fm.getState()==1 ||fm.getState()==2){
//					if(fmanager.findById(fm.getId(),true) == null){	
//						try {
//							regApp(fm.getName(),fm.getId());
//						} catch (SeeyonFormException e) {
//							log.error("加载内存对象失败", e);
//						}
//					}
//				}
			}	
		
			fm.setFormstart(formstart);
			getFormDaoManager().updateAppMain(fm);
		}
		
		/**
		 * 表单查询设置校验
		 * @param aApp 表单
		 * @return
		 * @throws Exception
		 */
		public List<String> checkQueryList(ISeeyonForm_Application aApp, Map<String, String> aQueryListMap) {
		 	List<QueryUserConditionDefin> condlist = new ArrayList<QueryUserConditionDefin>();
			List<String> lst = new ArrayList<String>();
			try{
				for(int i = 0; i < aApp.getQueryList().size();i++){
					SeeyonQueryImpl query = (SeeyonQueryImpl)aApp.getQueryList().get(i);
					ConditionListImpl conditionList = (ConditionListQueryImpl)query.getUserConditionList().copy();
					ConditionListImpl filter = (ConditionListImpl)query.getFilter();
					if(conditionList.getConditionList().size() == 0 && filter.getConditionList().size() == 0)continue;

					QueryResultImpl resultData = null;
					try{
						String fcondtionValue = null;
						String fcolumName = "";
						String fcoltype = "";
						for(Object item : conditionList.getConditionList()){							
							if(item instanceof DataColumImpl){
								DataColumImpl datacolum = (DataColumImpl)item;
								fcolumName = datacolum.getColumName();
								SeeyonDataDefine seeyon = (SeeyonDataDefine)aApp.getDataDefine();					
								for(int a = 0;a<seeyon.getDataDefine().getTableLst().size();a++){
									FormTable ft = (FormTable)seeyon.getDataDefine().getTableLst().get(a);
									for(int b = 0;b<ft.getFieldLst().size();b++){
										FormField ffield = (FormField)ft.getFieldLst().get(b);
										if(OperHelper.noNamespace(fcolumName).equals(ffield.getDisplay()))
											fcoltype = ffield.getFieldtype();
									}
								}
							}else if(item instanceof QueryUserConditionDefin){
								QueryUserConditionDefin userConditionDefin = (QueryUserConditionDefin)item;
								//如果是扩展输入字段
								if(userConditionDefin.getConditionInput().getTypeValue().equals(IXmlNodeName.C_sVluae_extend)){
									IInputExtendManager fmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
									ISeeyonInputExtend fvalue = (ISeeyonInputExtend)fmanager.findByName(userConditionDefin.getConditionInput().getExtendClass());  
						            //如果是扩展输入日期
						            if(ParseUserCondition.isDateExtend(fvalue)){
						            	if(IPagePublicParam.TIMESTAMP.equals(fcoltype)){
						            		userConditionDefin.setValue("2010-01-01");
										}else if(IPagePublicParam.DATETIME.equals(fcoltype)){
											userConditionDefin.setValue("2010-01-01 12:00");
										}				  
						            }else{
						            	userConditionDefin.setValue("0");
						            }
								}else{
									if(IPagePublicParam.TIMESTAMP.equals(fcoltype)){
										userConditionDefin.setValue("2010-01-01");
									}else if(IPagePublicParam.DATETIME.equals(fcoltype)){
										userConditionDefin.setValue("2010-01-01 12:00");
									}else{
									   userConditionDefin.setValue("0");
									}
								}
								condlist.add(userConditionDefin);
							}
						}
						query.setPagination(true);
						Pagination.setFirstResult(0);
						Pagination.setMaxResults(1);
						resultData = query.getResultData(null, conditionList, null, null);
					}catch(Exception e){
						log.error("校验表单[" + query.getFormname() + "]的查询模板[" + query.getQueryName() + "]执行出错", e);
					}finally {
						//释放调用的数据库资源
						try{
							if (resultData!=null){
								resultData.unInit();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}catch(Exception e){
				log.error("校验表单查询执行出错", e);
			}
			return lst;

		}
		
		/**
		 * 表单统计的执行结果

		 * @param aApp 表单
		 * @return
		 * @throws Exception
		 */
		public List<String> checkReportList(ISeeyonForm_Application aApp, Map<String, String> aReportListMap){
			HashMap fieldtypemap = new HashMap();
			SeeyonDataDefine seeyon = (SeeyonDataDefine)aApp.getDataDefine();					
			for(int a = 0;a<seeyon.getDataDefine().getTableLst().size();a++){
				FormTable ft = (FormTable)seeyon.getDataDefine().getTableLst().get(a);
				for(int b = 0;b<ft.getFieldLst().size();b++){
					FormField ffield = (FormField)ft.getFieldLst().get(b);
					fieldtypemap.put(ffield.getDisplay(), ffield.getFieldtype());
				}
			}
			List<String> lst = new ArrayList<String>();
			try{
				for(int i = 0; i < aApp.getReportList().size();i++){
					SeeyonReportImpl report = (SeeyonReportImpl) aApp.getReportList().get(i);
					ConditionListImpl filter = (ConditionListImpl)report.getFilter().copy();
					ConditionListImpl conditionList = (ConditionListImpl)report.getUserConditionList().copy();
					if(conditionList.getConditionList().size() == 0 && filter.getConditionList().size() == 0)continue;
					try{
						List<ReportHeadColum> rowHeadList = report.getSchema().getRowHeadList();
						if(rowHeadList.size() != 0){		
							for(ReportHeadColum item : rowHeadList){
								if( fieldtypemap.get(OperHelper.noNamespace(item.getDataAreaName())) !=null){
									if(IPagePublicParam.LONGTEXT.equalsIgnoreCase(fieldtypemap.get(OperHelper.noNamespace(item.getDataAreaName())).toString())){
										lst.add(Constantform.getString4CurrentUser("form.stat.formreport.label")+"  "+report.getReportName()+ "  "+Constantform.getString4CurrentUser("form.report.error"));
										if(aReportListMap != null)//如果是修改校验
											aReportListMap.put(report.getReportName(), "false");
									}
								}						
							}
						}
						List<ReportDataColum> dataColumList = report.getSchema().getDataColumList();
						if(dataColumList.size() != 0){							
							for(ReportDataColum item : dataColumList){
								if( fieldtypemap.get(OperHelper.noNamespace(item.getDataAreaName())) !=null){
									if(IPagePublicParam.LONGTEXT.equalsIgnoreCase(fieldtypemap.get(OperHelper.noNamespace(item.getDataAreaName())).toString())){
										lst.add(Constantform.getString4CurrentUser("form.stat.formreport.label")+"  "+report.getReportName()+ "  "+Constantform.getString4CurrentUser("form.reportdatafield.error"));
										if(aReportListMap != null)//如果是修改校验
											aReportListMap.put(report.getReportName(), "false");
									}									
								}	
							}
						}
						String fcolumName = "";
						String fcoltype = "";
						for(Object item : conditionList.getConditionList()){
							if(item instanceof DataColumImpl){
								DataColumImpl datacolum = (DataColumImpl)item;
								fcolumName = datacolum.getColumName();
								if(fieldtypemap.get(OperHelper.noNamespace(fcolumName)) !=null)
								  fcoltype =	fieldtypemap.get(OperHelper.noNamespace(fcolumName)).toString();
							}else if(item instanceof QueryUserConditionDefin){
								QueryUserConditionDefin userConditionDefin = (QueryUserConditionDefin)item;
								//如果是扩展输入字段
								if(userConditionDefin.getConditionInput().getTypeValue().equals(IXmlNodeName.C_sVluae_extend)){
									IInputExtendManager fmanager = SeeyonForm_Runtime.getInstance().getInputExtendManager();
									ISeeyonInputExtend fvalue = (ISeeyonInputExtend)fmanager.findByName(userConditionDefin.getConditionInput().getExtendClass());  
						            //如果是扩展输入日期
						            if(ParseUserCondition.isDateExtend(fvalue)){
						            	if(IPagePublicParam.TIMESTAMP.equals(fcoltype)){
						            		userConditionDefin.setValue("2010-01-01");
										}else if(IPagePublicParam.DATETIME.equals(fcoltype)){
											userConditionDefin.setValue("2010-01-01 12:00");
										}				  
						            }else{
						            	userConditionDefin.setValue("0");
						            }
								 }else{
									 if(IPagePublicParam.TIMESTAMP.equals(fcoltype)){
										 userConditionDefin.setValue("2010-01-01");
									 }else if(IPagePublicParam.DATETIME.equals(fcoltype)){
										 userConditionDefin.setValue("2010-01-01 12:00");
									 }else{
										 userConditionDefin.setValue("0");
									 }
								 }
							 }
						 }
						IReportResult resultData = report.showReport(null, (IConditionList_Report)conditionList, null, null);
					}catch(Exception e){
						log.error("校验表单[" + report.getFormname() + "]的统计模板[" + report.getReportName() + "]执行出错", e);
					}
				}
			}catch(Exception e){
				log.error("校验表单统计执行出错", e);
			}
			return lst;
		}

		
		public void updateFinishedBySummaryId(ColSummary summary,Long formappid,Long recordid, Constant.flowState summaryState) throws SeeyonFormException, SQLException{
			ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(formappid);
			//来自关联表单的不对动态表中流程状态进行更新
			int finishedsign = 1;
			//协同summary表中1为终止，3为结束，表单状态中1为结束，3为终止。
			if(summary.getState() !=null){
				if(summary.getState() == 3)
					finishedsign = 1;
				if(summary.getState() == 1)
					finishedsign = 3;
			}		
			if(summary.getParentformSummaryId() == null && !(summary.getNewflowType() != null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
				if(afapp!=null){
					SeeyonDataDefine seedade =(SeeyonDataDefine) afapp.getDataDefine();
					DataDefine define = seedade.getDataDefine();
					String tablename = "";
					for(int x=0;x<define.getTableLst().size();x++){
						FormTable ftable = define.getTableLst().get(0);
						tablename =ftable.getName();						
					}
					List<String> strBatch = new ArrayList<String>();
					String sql = "update "+tablename+" set finishedflag = "+finishedsign+" where id = "+recordid+"";
					strBatch.add(sql);
					getFormDaoManager().execSQLList(strBatch);
				}
			}	
			formDaoManager.UpdateDataStateFinished(summary.getId(), finishedsign);
		}
		
		public void updateState4Form(Long formAppId, Long recordId, int state) throws SeeyonFormException, SQLException {
			ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
			if(afapp != null){
				SeeyonDataDefine seedade =(SeeyonDataDefine) afapp.getDataDefine();
				DataDefine define = seedade.getDataDefine();
				String tablename = "";
				for(int x=0;x<define.getTableLst().size();x++){
					FormTable ftable = define.getTableLst().get(0);
					tablename =ftable.getName();						
				}
				List<String> strBatch = new ArrayList<String>();
				String sql = "update "+tablename+" set state = "+state+" where id = "+recordId+"";
				log.info("更新表单动态表state:" + sql);
				strBatch.add(sql);
				getFormDaoManager().execSQLList(strBatch);
			}
			
		}
		
		public FormAppMain findAppbyId(Long formappid) throws DataDefineException{
			FormAppMain fm = getFormDaoManager().findApplicationById(formappid);
			return fm;
		} 
		
		public List<String> checkFormbindSystemValue(SessionObject sessionObject ){
			List<String> systemValue = new ArrayList<String>() ;
		
			try{
				ISystemValueManager fsysvaluemanager = SeeyonForm_Runtime.getInstance().getSystemValueManager();
				this.formbindSystemValue(fsysvaluemanager,systemValue) ;
			}catch(Exception e){
				log.error("checkFormbindSystemValue error：", e) ;
			}
			return systemValue ;
		}
		
		private void formbindSystemValue(ISystemValueManager fsysvaluemanager ,List<String> systemValue) throws Exception{
			if(fsysvaluemanager.getNames() == null){
				return ;
			}
			
			if(systemValue == null) {
				systemValue = new ArrayList<String>() ; 
			}else {
				systemValue.clear() ;
			}
			
			for(int i = 0 ; i < fsysvaluemanager.getNames().size() ; i++) {
				ISeeyonSystemValue seeyon = fsysvaluemanager.findByName(fsysvaluemanager.getNames().get(i));
				if(seeyon instanceof UserFlowId){
					continue ;
				}
				
				if(!fsysvaluemanager.getNames().get(i).contains("ID")){
					systemValue.add(fsysvaluemanager.getNames().get(i)) ;
				}
			}
		}
		
		private Operation getInptOpentionObj(SessionObject sessionObject,String from ,String operation,String defaultFirstNodeOperationId ) throws Exception{

			  Operation operationObj = getOperation(sessionObject,from,operation) ;
			  if(operationObj == null ){
				  return getOperation(sessionObject,from,defaultFirstNodeOperationId) ;
			  }
			  if(IXmlNodeName.C_sVluae_add.equals(operationObj.getType())){
				  return operationObj ;
			  }			  
			  return  getOperation(sessionObject,from,defaultFirstNodeOperationId) ; 
		}
		
		private Operation getOperation(SessionObject sessionObject,String from ,String operation){
			List<FormPage> formLst = sessionObject.getFormLst();
			  if(formLst!=null){
				  for(FormPage formPage : formLst){
					  if(formPage.getFormPageId().equals(from)){
						  List<Operation> operations = formPage.getOperlst();
						  if(operations!=null){
								for(Operation op:operations){
										if(op.getOperationId().equals(operation)){									
											return op;							
										}									
								}
						  }
					  }
				  }	
			  }
			 return null ;	
		}
		
		
		private Operation getInptOpentionObj(SessionObject sessionObject,HttpServletRequest request) throws Exception{
			String opentionId = request.getParameter("opentionId") ;
			String from = request.getParameter("from") ;
			String defaultFirstNodeOperationId = request.getParameter("defaultFirstNodeOperationId") ;
			if(Strings.isNotBlank(opentionId) && Strings.isNotBlank(from) &&Strings.isNotBlank(defaultFirstNodeOperationId) ){
				return getInptOpentionObj(sessionObject,from,opentionId,defaultFirstNodeOperationId) ;
			}
			return null ;
		}
		
		public List<String> checkFormbindField(SessionObject sessionObject ,HttpServletRequest request){			
			List<String> fields = new ArrayList<String>() ;			
			String namespace = sessionObject.getNamespace() ;
			List<TableFieldDisplay> tableFieldList = sessionObject.getTableFieldList() ;
			try{
				Operation operation = this.getInptOpentionObj(sessionObject,request) ;
				if(operation != null) {
					getField(operation, fields,namespace,tableFieldList) ;
				}				
			}catch(Exception e){
				log.error("checkFormbindField error", e) ;
			}

			return fields ;
		}
	   /**
	    * 判断是否主表数据 
	    * 如果是签章 ， 备注类型的同样进行过滤
	    * 绑定的是附件 关联文档 图片的都进行过滤
	    * @param bindname
	    * @param TableFieldList
	    * @return
	    */
		private boolean isFormAesssField(String bindname , List<TableFieldDisplay> TableFieldList){
			
			if(TableFieldList == null){
				return false ;
			}
			for(TableFieldDisplay tableFieldDisplay : TableFieldList){
				if(tableFieldDisplay.getBindname().equals(bindname)){
					if(isFormMainTableField(tableFieldDisplay)){
						if(tableFieldDisplay.getFieldtype().equals("HANDWRITE")){
							return false ;	
						}
						if(tableFieldDisplay.getFieldtype().equals("LONGTEXT")){
							return false ;	
						}
						if(tableFieldDisplay.getInputtype() != null && tableFieldDisplay.getInputtype().equals("checkbox")){
							return false ;	
						}						
						if(tableFieldDisplay.getExtend() != null 
								&& (tableFieldDisplay.getExtend().equals("插入附件") || 
										tableFieldDisplay.getExtend().equals("关联文档") || 
										tableFieldDisplay.getExtend().equals("插入图片"))){
							return false ;
						}						
						return true ;
					}
					break;
				}
			}			
			return false ;			
		}
		
		
		private void getField(Operation operation ,List<String> fields ,String namespace,List<TableFieldDisplay> tableFieldList) throws Exception{
			if(operation == null) {
				return  ;
			}
			if(fields != null) {
				fields.clear() ;				
			}else{
				fields = new  ArrayList<String>() ;
			}
			
			List list = operation.getOperlst() ;
			
			if(list != null) {
				for(int i = 0 ; i < list.size() ; i++) {
					Map map = (Map)list.get(i) ;
					if(map.get("formoper"+i).equals("edit") || map.get("formoper"+i).equals("browse")){
						if(isFormAesssField((String)map.get("bindname"+i),tableFieldList)){
							String putValue = ((String)map.get("bindname"+i)).replaceFirst(namespace, "") ;
							fields.add(putValue) ;							
						}
					}
				}				
			}
			return  ;
		}
		/**
		private Operation getInptOpentionObj(List<FormPage> formPages )throws Exception{
			if(formPages == null) {
				return null ;
			}			
			for(FormPage formPage : formPages) {
				  List<Operation> operList = formPage.getOperlst() ;
				  for(Operation operation : operList){
					  if(operation.getName().equals("填写")) {
						  return operation ;
					  }					  
				  }
				}	
			return null ;
		}
	   **/	
		
		private boolean isFormMainTableField(TableFieldDisplay tableFieldDisplay){
			if(tableFieldDisplay == null)
				return false ;
			if(tableFieldDisplay.getTablename()!=null && tableFieldDisplay.getTablename().indexOf("formmain") != -1){
				return true ;
			}
			return false ;
		}
		
		private boolean isInTheFieldExtend(String fieldExtendName ,String... extendsName ){
			if(extendsName == null)
				return true ;
			for(String  str : extendsName ){
				if(str != null && str.equals(fieldExtendName)){
					return true ;
				}
			}
			return false ;
		}
		
		public List<TableFieldDisplay> getSelectExtendField(SessionObject sessionObject , Boolean isMainTable , String... extendsName ) {
			if(sessionObject == null)
				return null ;
			
			if(extendsName == null && !isMainTable ){
				return sessionObject.getTableFieldList() ;
			}
			
			List<TableFieldDisplay> list = new ArrayList<TableFieldDisplay>() ;
			
			List<TableFieldDisplay> all =  sessionObject.getTableFieldList() ;
			if(all != null){
				if(isMainTable == null){
					for(TableFieldDisplay tableFieldDisplay : all){
						if(isInTheFieldExtend(tableFieldDisplay.getExtend(),extendsName)){
							list.add(tableFieldDisplay) ;
						}
					}
				}else if(isMainTable){
					for(TableFieldDisplay tableFieldDisplay : all){
						if(isFormMainTableField(tableFieldDisplay) && isInTheFieldExtend(tableFieldDisplay.getExtend(),extendsName)){
							list.add(tableFieldDisplay) ;
						}
					}
				}else if(!isMainTable){
					for(TableFieldDisplay tableFieldDisplay : all){
						if(!isFormMainTableField(tableFieldDisplay) && isInTheFieldExtend(tableFieldDisplay.getExtend(),extendsName)){
							list.add(tableFieldDisplay) ;
						}
					}				
				}
			}		
			return list ;			
		}
		
		public List<TableFieldDisplay> getSelectPeopleField(SessionObject sessionObject) {
			return getSelectExtendField(sessionObject,true,"选择人员");
		}
		
		public List<TableFieldDisplay> getSelectExtendField(SessionObject sessionObject , String... extendsName){
			return getSelectExtendField(sessionObject,true,extendsName);
		}
		
		
		public List<TableFieldDisplay> getSelectPeopleFieldIncludingRef(SessionObject sessionObject){
			List<TableFieldDisplay> list = new ArrayList<TableFieldDisplay>() ;
			
			List<TableFieldDisplay> fieldList =  sessionObject.getTableFieldList();
			for (TableFieldDisplay field : fieldList) {
				if(isFormMainTableField(field) && 
						(isInTheFieldExtend(field.getExtend(),Constantform.EXTEND_SELECT_USER_LABEL) || isRefSelectPeopleField(fieldList, field))){
					list.add(field);
				}
			}
			
			return list;
		}
		
		private boolean isRefSelectPeopleField(List<TableFieldDisplay> fieldList, TableFieldDisplay curField){
			if(Constantform.EXTEND_SELECT_RELATED_FORM_LABEL.equals(curField.getExtend()) 
					|| IPagePublicParam.RELATION.equals(curField.getInputtype())){
				String[] inputTypeArray = FormHelper.getRelationInfo(fieldList, curField);
				if(Constantform.EXTEND_SELECT_USER_LABEL.equals(inputTypeArray[4])){
					return true;
				}
			}
			return false;
		}

		
		public String getFormFileDisPlayValue(ColSummary summary,String fileName,Templete templete) throws Exception{		
			
			Map<String ,String[]> fieldMap = getFieldValueMap(summary,templete) ;
			
			if(fieldMap == null || fieldMap.isEmpty()){
				return null ;
			}
			
			String[] string = fieldMap.get(getfileNameName(fileName)) ;
			
			if(string != null){
				if(Strings.isNotBlank(string[0])) {
					return string[0] ;
				}else{
					return string[1] ;
				}
			}
			return null ;
		}
		
		public Map<String ,String[]> getFieldValueMap(ColSummary summary,Templete templete) throws Exception{
			if(summary == null || templete == null){
				return null ;
			}
			BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
			String[] string = FormHelper.getFormPolicy(process) ;
			
			return FormHelper.getFieldValueMap(string[0],string[1],string[2],summary.getFormRecordId().toString()) ;		
		}
		
		
		private  String getfileNameName(String fileName) {
			if(Strings.isBlank(fileName)){
				return fileName ;
			}
			int findex = fileName.indexOf(":");
			if (findex < 0)
				return fileName;
			fileName = fileName.substring(findex + 1);
			return fileName;
		}
		
		private void applicationToSessionObj(ISeeyonForm_Application iapp, SessionObject sessionobject) throws SeeyonFormException{
			TempleteManager templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
			SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) iapp;
			List<FormAppAuth> formAppAuthList = sapp.getFormAppAuthList();
			Map<String,FormAppAuthObject> tempMap = new LinkedHashMap<String, FormAppAuthObject>();
			for (FormAppAuth formAppAuth : formAppAuthList) {
				FormAppAuthObject abo = new FormAppAuthObject();
				abo.setId(formAppAuth.getId());
				abo.setName(formAppAuth.getName());
				abo.setXmlString(formAppAuth.getXmlString(2));
				SeeyonQueryImpl query = (SeeyonQueryImpl)formAppAuth.getQuery();  
		    	//输出数据项
		    	List<QueryColum> dataColumList = query.getDataColumList();
		    	BindHelper.setDataField(abo,dataColumList,sessionobject);
		    /*	StringBuilder dataField = new StringBuilder();
		    	StringBuilder dataFieldValue = new StringBuilder("<ShowDataList>");
		    	String title = "";
		    	String areaName = "";
		    	for(int k = 0; k < dataColumList.size(); k++){
		    		QueryColum queryColum = dataColumList.get(k);
		    		title = queryColum.getColumTitle(); 
		    		if(!SeeyonFormPojo.C_sFieldNames.contains(queryColum.getDataAreaName())){
		    			areaName = queryColum.getDataAreaName().substring(3);
		    		}else{
		    			areaName = queryColum.getDataAreaName();
		    		}
		    		if(!areaName.equals(title)) title = areaName+"("+title+")";
		    		if(k==0){
		    			dataField.append(title) ;
					}else{
						dataField.append(","+title) ;
					}
		    		dataFieldValue.append(queryColum.getXml());
		    	}
		    	dataFieldValue.append("</ShowDataList>");
		    	abo.setDataField(dataField.toString());
		    	abo.setDataFieldValue(dataFieldValue.toString());*/
		    	
		    	//排序方式
		    	List<OrderByColum> orderByList = query.getOrderByList();
		    	BindHelper.setSortField(abo,orderByList,sessionobject);
		    	/*StringBuilder resultSort = new StringBuilder();
		    	StringBuilder resultSortValue = new StringBuilder("<OrderBy>");
		    	for(int k = 0; k < orderByList.size(); k++){
		    		OrderByColum orderColum = orderByList.get(k);
		    		String orderColumName = orderColum.getColmunName();
		    		String orderColumDisplay = orderColumName.substring(orderColumName.indexOf(":") + 1, orderColumName.length());
		    		int orderType = orderColum.getType();
		    		if(orderType == 0) {
		    			orderColumDisplay += "↑";
		    		} else {
		    			orderColumDisplay += "↓";
		    		}
		    		if(k==0){
		    			resultSort.append(orderColumDisplay) ;
					}else{
						resultSort.append("," + orderColumDisplay) ;
					}
		    		resultSortValue.append(orderColum.getXml());
		    	}
		    	resultSortValue.append("</OrderBy>");
		    	abo.setResultSort(resultSort.toString());
		    	abo.setResultSortValue(resultSortValue.toString());*/
		    	
		    	//查询条件
	    		List<QueryColum> queryColList = query.getQueryColumList();
	    		BindHelper.setCustomQueryField(abo,queryColList,sessionobject);
	    		/*StringBuilder customQueryField = new StringBuilder() ;
	    		StringBuilder customQueryFieldValue = new StringBuilder("<CustomQueryList>") ;
	    		for (int k = 0; k < queryColList.size(); k++) {
	    			QueryColum col=queryColList.get(k);
					if(k==0){
						customQueryField.append(col.getColumTitle()) ;
					}else{
						customQueryField.append(","+col.getColumTitle()) ;
					}	
					customQueryFieldValue.append(col.getXml());
				}
	    		customQueryFieldValue.append("</CustomQueryList>");
		    	abo.setCustomQueryField(customQueryField.toString());
		    	abo.setCustomQueryFieldValue(customQueryFieldValue.toString());*/
		    	
		    	Map<String, OperationAuth> operAuthMap = formAppAuth.getOperationAuths();
		    	if(operAuthMap!=null){
		    		Map<String,FormOperAuthObject> appAuthObjectMap = new LinkedHashMap<String,FormOperAuthObject>();
			    	for (OperationAuth operationAuth : operAuthMap.values()) {
			    		FormOperAuthObject aao = new FormOperAuthObject();
			        	aao.setName(operationAuth.getName());
			        	
			        	//添加权限
			    		ShowDetail addShowDetail = operationAuth.getAddShowDetail();
			    		String viewId = addShowDetail.getFormName();
			    		String operId = addShowDetail.getOperName();
		    			Map<String,String> addmap = new HashMap<String,String>();
		    			addmap.put("formName",viewId);
		    			addmap.put("operName",operId);
			    		aao.setAddShowDetail(addmap);
			    		
			    		//修改权限
			    		ShowDetail updateShowDetail = operationAuth.getUpdateShowDetail();
			    		viewId = updateShowDetail.getFormName();
			    		operId = updateShowDetail.getOperName();
			    		Map<String,String> updatemap = new HashMap<String,String>();
			    		updatemap.put("formName",viewId);
			    		updatemap.put("operName",operId);
			    		aao.setUpdateShowDetail(updatemap);
			    		
			    		//浏览权限
				    	List<Map<String,String>> browseShowDetailList = new ArrayList<Map<String,String>>();
			    		ShowDetail browseShowDetail = operationAuth.getBrowseShowDetail();
			    		String[] viewIds = browseShowDetail.getFormName().split("\\|");
			    		String[] operIds = browseShowDetail.getOperName().split("\\|");
			    		for (int k = 0; k < viewIds.length; k++) {
			    			Map<String,String> browsemap = new HashMap<String,String>();
			    			browsemap.put("formName",viewIds[k]);
			    			browsemap.put("operName",operIds[k]);
			    			browseShowDetailList.add(browsemap);
						}
			    		aao.setBrowseShowDetail(browseShowDetailList);
			    		
				    	aao.setAllowlock(operationAuth.isAllowlock());
				    	aao.setAllowdelete(operationAuth.isAllowdelete());
				    	aao.setAllowexport(operationAuth.isAllowexport());
				    	aao.setAllowquery(operationAuth.isAllowquery());
				    	aao.setAllowstat(operationAuth.isAllowstat());
				    	aao.setAllowlog(operationAuth.isAllowlog());
				    	aao.setAllowprint(operationAuth.isAllowprint());
				    	
				    	//系统查询条件
				    	IConditionList filter = operationAuth.getFilter();
				    	aao.setXmlString(operationAuth.getXmlString(2));
				    	BindHelper.setQueryAreaField(aao,filter,sessionobject);
				    	/*aao.setQueryArea(filter.getDisplay());
				    	aao.setQueryAreaValue("<Filter>" + filter.getXML() + "</Filter>");*/
				    	
				    	// 授权信息
				    	List<FomObjaccess> objAccessList = new ArrayList<FomObjaccess>();
		    			List listObjAccess = formDaoManager.queryObjAccessByCondition(sessionobject.getFormid(), aao.getName(), IPagePublicParam.C_iObjecttype_bill);
		        		for (int k = 0; k < listObjAccess.size(); k++) {
		        			FomObjaccess fobj = (FomObjaccess)listObjAccess.get(k);
		    				objAccessList.add(fobj);
		    			}	
		        		aao.setObjAccessList(objAccessList);
		        		
		        		appAuthObjectMap.put(operationAuth.getName(), aao);
					}
			    	abo.setAppOperAuthObjectMap(appAuthObjectMap);
			    	BindHelper.updateFormAppAuthObjectXml(abo,sessionobject);
			    }
		    	if(sessionobject.getFormType() == TAppBindType.BASEDATA.getValue()){
					sessionobject.setAppAuthObject(abo);
					break;
				}
				tempMap.put(abo.getId(), abo);
				sessionobject.setFormAppAuthObjectMap(tempMap);
			}
			
			//设置菜单对象值
		  /*Map<String, FormAppBindObject> authMap = sessionobject.getAppBindObjectMap();
			Set<Long> oldAppBindMenuId = sessionobject.getOldAppBindMenuId();
			ISeeyonFormBind formBind = iapp.getSeeyonFormBind();
			String rootMenu = ((SeeyonFormBindImpl)formBind).getRootMenu();
			if(org.apache.commons.lang.StringUtils.isNotEmpty(rootMenu)){
				String[] rootMenuAry = rootMenu.split(",");
				for (String rootMId : rootMenuAry) {
					sessionobject.getRootMenus().add(rootMId);
				}
			}
			List<IBindMenuItem> bindMenuItemList = formBind.getMenuList();
			for (IBindMenuItem menu : bindMenuItemList) {
				FormAppBindObject abo = new FormAppBindObject();
				String id = String.valueOf(menu.getId());
				String type = menu.getBindtype();
				String bindSetId = menu.getRefObjId();
				String bindMenuId = menu.getGroupId();
				String bindMenuName = menu.getGroupName();
				abo.setId(id);
				abo.setType(type);
				abo.setBindSet(bindSetId);
				abo.setBindMenuId(bindMenuId);
				abo.setBindMenuName(bindMenuName);
				abo.setMenuName(menu.getMenuName());
				authMap.put(id, abo);
				if(String.valueOf(IPagePublicParam.C_iObjecttype_bill).equals(type)){
					abo.setAppAuthObject(tempMap.get(bindSetId));
				}
				if("2".equals(sessionobject.getFormstate()) && org.apache.commons.lang.StringUtils.isNotBlank(bindMenuId) && org.apache.commons.lang.StringUtils.isNotBlank(id)){
					oldAppBindMenuId.add(new Long(bindMenuId));
					oldAppBindMenuId.add(new Long(id));
				}
			}
			*/
			sessionobject.setLogFieldList(new ArrayList(sapp.getLogFieldList()));
			BindHelper.updateFormUniqueAndLogField(sessionobject.getLogFieldList(),sessionobject.getFieldMap());
			/*List logFieldList = sessionobject.getLogFieldList();
			String logfieldString = "<LogFieldList>  \r\n";
			if(logFieldList!=null){
				for (int i = 0; i < logFieldList.size(); i++) {
					logfieldString+="    <LogField  name=\""+ sessionobject.getNamespace() + logFieldList.get(i)+"\" />\r\n";
				}
			}
			logfieldString+="</LogFieldList>  \r\n";
			sessionobject.setLogfieldString(logfieldString);*/
			
			sessionobject.setUniqueFieldList(new ArrayList(sapp.getUniqueFieldList()));
			BindHelper.updateFormUniqueAndLogField(sessionobject.getUniqueFieldList(),sessionobject.getFieldMap());
			/*List uniqueFieldList = sessionobject.getUniqueFieldList();
			String uniqueFieldString = "";
			if(uniqueFieldList!=null){
				for (int i = 0; i < uniqueFieldList.size(); i++) {
					uniqueFieldString +="    <UniqueField name=\"" +sessionobject.getNamespace() + uniqueFieldList.get(i)+"\" />\r\n";
				}
			}
			sessionobject.setUniqueFieldString(uniqueFieldString);*/
			
			try {
				Map<Long,RelationCondition> relationConditionMap = new LinkedHashMap<Long,RelationCondition>();//关联条件
				List<RelationCondition> relationConditionList = sapp.getRelationConditionList();
				for (RelationCondition relationCondition : relationConditionList) {
					RelationCondition newRelationCondition = new RelationCondition();
					BeanUtils.copyProperties(newRelationCondition,relationCondition);
					newRelationCondition.setConditionList(newRelationCondition.getConditionList().copy());
					BindHelper.updateColumnNameOfRelationCondition(sessionobject.getFieldMap(), newRelationCondition);
					relationConditionMap.put(newRelationCondition.getId(), newRelationCondition);
				}
				sessionobject.setRelationConditionMap(relationConditionMap);
				
				Map<Long,FormEvent> triggerConfigMap = new LinkedHashMap<Long,FormEvent>();//触发设置
				List<FormEvent> triggerConfigList = sapp.getTriggerConfigList();
				for (FormEvent formEvent : triggerConfigList) {
					FormEvent newFormEvent = new FormEvent(formEvent.getOwnerApp());
					BeanUtils.copyProperties(newFormEvent,formEvent);
					
					List<EventCondition> conditionList = formEvent.getConditionList();
					List<EventCondition> newConditionList = new ArrayList<EventCondition>();
					for (EventCondition condition : conditionList) {
						EventCondition newCondition = new EventCondition();
						BeanUtils.copyProperties(newCondition,condition);
						EventValue value = condition.getValue();
						EventValue newValue = new EventValue();
						BeanUtils.copyProperties(newValue,value);
						newCondition.setValue(newValue);
						newConditionList.add(newCondition);
					}
					newFormEvent.setConditionList(newConditionList);
					
					List<EventAction> actionList = formEvent.getActionList();
					List<EventAction> newActionList = new ArrayList<EventAction>();
					for (EventAction action : actionList) {
						EventAction newAction = new EventAction();
						BeanUtils.copyProperties(newAction,action);
						if(action.isWithholding() != null){
							newAction.setWithholding(new Boolean(action.isWithholding()));
						}
						
						EventTemplate template = action.getTemplate();
						if(template != null){
							EventTemplateObject newTemplate = new EventTemplateObject();
							BeanUtils.copyProperties(newTemplate,template);
							newTemplate.setContent(BindHelper.updateExpression(sessionobject.getFieldMap(), newTemplate.getContent(), false));
							Templete tem = templeteManager.get(new Long(template.getId()));
							if(tem!=null) newTemplate.setFlowTemplateName(tem.getSubject());
							newAction.setTemplate(newTemplate);
						} else {
							newAction.setTemplate(null);
						}
						
						EventTask task = action.getTask();
						if(task!=null){
							EventTask eTask = new EventTask();
							eTask.setErrorToStop(task.getErrorToStop());
							eTask.setId(task.getId());
							FlowBean fb = DEEConfigService.getInstance().getFlow(task.getId());
							if(fb!=null){
								eTask.setTaskName(fb.getDIS_NAME());
							}else{
								eTask.setTaskName("");
							}
							newAction.setTask(eTask);
						}else{
							newAction.setTask(null);
						}
						
						EventRelatedForm relatedForm = action.getRelatedForm();
						if(relatedForm != null){
							EventRelatedForm newRelatedForm = new EventRelatedForm();
							BeanUtils.copyProperties(newRelatedForm,relatedForm);
							newAction.setRelatedForm(newRelatedForm);
						} else {
							newAction.setRelatedForm(null);
						}
						
						List<EventEntity> newEntityList = new ArrayList<EventEntity>();
						List<EventEntity> entityList = newAction.getEntityList();
						for (EventEntity entity : entityList) {
							EventEntity newEntity = new EventEntity();
							BeanUtils.copyProperties(newEntity,entity);
							newEntityList.add(newEntity);
						}
						newAction.setEntityList(newEntityList);
						
						List<EventMapping> newMappingList = new ArrayList<EventMapping>();
						List<EventMapping> mappingList = newAction.getMappingList();
						for (EventMapping mapping : mappingList) {
							EventMapping newMapping = new EventMapping();
							BeanUtils.copyProperties(newMapping, mapping);
							BindHelper.updateSourceFieldOfEventMapping(sessionobject.getFieldMap(), newMapping);
							newMappingList.add(newMapping);
						}
						newAction.setMappingList(newMappingList);
						
						List<EventCalculate> newCalculateList = new ArrayList<EventCalculate>();
						List<EventCalculate> calculateList = newAction.getCalculateList();
						for (EventCalculate calculate : calculateList) {
							EventCalculate newCalculate = new EventCalculate();
							BeanUtils.copyProperties(newCalculate, calculate);
							newCalculate.setValue(BindHelper.updateExpression(sessionobject.getFieldMap(), newCalculate.getValue(), true));
							newCalculateList.add(newCalculate);
						}
						newAction.setCalculateList(newCalculateList);
						newActionList.add(newAction);
					}
					newFormEvent.setActionList(newActionList);
					triggerConfigMap.put(newFormEvent.getId(), newFormEvent);
				}
				sessionobject.setTriggerConfigMap(triggerConfigMap);
			} catch (Exception e) {
				throw new SeeyonFormException(-1,e);
			}
		}

		private void otherSaveAppAuth(SessionObject sessionobject, Map<String,String> mapping, Long newFormId) throws SeeyonFormException {
			/*Map<String, FormAppAuthObject> authMap = sessionobject.getAuthObjectMap();
			Collection<FormAppAuthObject> authColl = new ArrayList(authMap.values());
			authMap.clear();
	        for (FormAppAuthObject abo : authColl) {
	        	String newId = String.valueOf(UUIDLong.longUUID());
	        	String subject = abo.getName();
	        	String newSubject = mapping.get(newTemNameLabel+abo.getId()); 
	        	//TODO
	        	FormAppAuthObject newao = BindHelper.copyAuthObject(abo,newFormId,newSubject);
    			newao.setId(newId);
    			authMap.put(newId, newao);
	        }*/
		}

		public List<FomObjaccess> getFomObjaccessByAppIds(List<Long> domainIds) throws Exception{
			return getFormDaoManager().getFomObjaccessByAppIds(domainIds) ;
		}
		
		public void getformAccess(List <FormAppMain> newAppList,
				Set<TempleteCategory> templeteCategories,
				Set<String> formNameList,
				List<Long> appidlist,
				int type )throws Exception {
			
			List<Long> formobjlist = FormBizConfigUtils.getUserDomainIds(CurrentUser.get().getId(), null) ;
			SessionObject sessionobject = new SessionObject() ;
			List <FormAppMain> formapplist = null ;
			List applst = queryAllAccess(formobjlist,appidlist,type);
			if(type == IPagePublicParam.C_iObjecttype_Query){							
				formapplist = assignQuery(applst,sessionobject, CurrentUser.get());
			}else if(IPagePublicParam.C_iObjecttype_Report == type){							
				formapplist = assignReport(applst,sessionobject, CurrentUser.get());
			}
			if(formapplist == null){
				return ;
			}
			
            int count = 1;
            
            for (FormAppMain app : formapplist) {
            	if(app.getFormstart() == 0)
            		continue;
            	app.setSystemdatetime(String.valueOf(System.currentTimeMillis() + (++count * 37)));
            	newAppList.add(app);
                formNameList.add(app.getName());
                TempleteCategory templete = templeteCategoryManager.get(app.getCategory());
                if(templete != null){
                	templeteCategories.add(templete);	
                	while(templete.getParentId() != null){
                		templete = templeteCategoryManager.get(templete.getParentId());
                		if(templete.getId() == 0 || templete.getId() == 4)continue;
                		templeteCategories.add(templete);	
                	}
                }
            }
		}
		
}