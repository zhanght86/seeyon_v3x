package com.seeyon.v3x.edoc.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;
import www.seeyon.com.v3x.form.utils.CharReplace;
import www.seeyon.com.v3x.form.utils.StringUtils;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.dao.EdocFormAclDao;
import com.seeyon.v3x.edoc.dao.EdocFormDao;
import com.seeyon.v3x.edoc.dao.EdocFormElementDao;
import com.seeyon.v3x.edoc.dao.EdocFormExtendInfoDao;
import com.seeyon.v3x.edoc.dao.EdocFormFlowPermBoundDao;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.edoc.domain.EdocFormFlowPermBound;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.util.XMLConverter;
import com.seeyon.v3x.edoc.webmodel.EdocFormModel;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionDisplayConfig;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.util.FileUtil;

/**
 * EdocFormManagerImpl.java
 * 
 * @author 韩冬佑
 *
 */
public class EdocFormManagerImpl implements EdocFormManager {

	private static final Log log = LogFactory.getLog(EdocFormManagerImpl.class);
	 private String baseFileFolder;
	 private static String formFolder = "/form";
	 private static String templateFolder = "/template";
	 private static String orgFolder = "/orgnization";
	 
	private EdocFormDao edocFormDao;
	private EdocFormElementDao edocFormElementDao;
	private EdocFormAclDao edocFormAclDao;
	private EdocFormExtendInfoDao edocFormExtendInfoDao;


	private EdocElementManager edocElementManager;	
	private XMLConverter xmlConverter;
	private FileManager fileManager;
	private AttachmentManager attachmentManager;
	private EdocFormFlowPermBoundDao edocFormFlowPermBoundDao;
	private OrgManager orgManager;
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setEdocFormExtendInfoDao(EdocFormExtendInfoDao edocFormExtendInfoDao) {
		this.edocFormExtendInfoDao = edocFormExtendInfoDao;
	}
	private static Hashtable <String,EdocForm>defaultEdocForm=new Hashtable<String,EdocForm>();	
	
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public XMLConverter getXmlConverter() {
		return xmlConverter;
	}

	public void setXmlConverter(XMLConverter xmlConverter) {
		this.xmlConverter = xmlConverter;
	}

	public EdocFormManagerImpl() {		
	}
	
	
	public EdocFormDao getEdocFormDao() {
        return edocFormDao;
    }

    public void setEdocFormDao(EdocFormDao edocFormDao) {
        this.edocFormDao = edocFormDao;
    }  
    
    public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}

	public EdocFormElementDao getEdocFormElementDao() {
    	return edocFormElementDao;
    }
    
    public void setEdocFormElementDao(EdocFormElementDao edocFormElementDao) {
    	this.edocFormElementDao = edocFormElementDao;
    }
    
    public EdocFormAclDao getEdocFormAclDao() {
    	return edocFormAclDao;
    }
    
    public void setEdocFormAclDao(EdocFormAclDao edocFormAclDao) {
    	this.edocFormAclDao = edocFormAclDao;
    }
    
    public void createEdocForm(EdocForm edocForm,List<Long> elementIdList){
    	int eleNum=0;
    	for(Long eleId:elementIdList)
    	{
    		EdocElement ele=edocElementManager.getEdocElementsById(eleId);
    		if("doc_mark".equals(ele.getFieldName()) || "send_to".equals(ele.getFieldName()) || "doc_mark2".equals(ele.getFieldName()) || "send_to2".equals(ele.getFieldName()))
    		{
    			eleNum++;
    		}
    	}
    	if(eleNum==4){edocForm.setIsunit(true);}
    	else{edocForm.setIsunit(false);}
    	
    	edocFormDao.save(edocForm);
    	saveEdocXmlData(edocForm.getId(),elementIdList);
    }
    
    public void updateEdocForm(EdocForm edocForm) throws Exception
    {    	
    	edocFormDao.update(edocForm);
    }
    public void updateEdocFormExtendInfo(EdocFormExtendInfo efinfo) throws Exception
    {    	
    	edocFormExtendInfoDao.update(efinfo);
    }    
    public EdocForm getEdocForm(long id)
    {
    	return edocFormDao.get(id);
    }
        
    public List<EdocForm> getAllEdocForms(Long domainId)
    {
    	return edocFormDao.getAllEdocForms(domainId);    	
    }    
    
    public List<EdocForm> getAllEdocFormsForWeb(User user,Long domainId)
    {
    	String accountIds = "";
    	try{
    		accountIds = orgManager.getUserIDDomain(user.getId(), domainId, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
    	}catch(Exception e){
    		log.error("查询文单授权对象异常",e);
    	}
    	return edocFormDao.getAllEdocFormsForWeb(user.getLoginAccount(),accountIds,false);    	
    }  
    
    //通过模糊查询
    public List<EdocForm> getAllEdocFormsByName(User user ,Long domainId,String name)
    {
    	String accountIds = "";
    	try{
    		accountIds = orgManager.getUserIDDomain(user.getId(), domainId, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
    	}catch(Exception e){
    		log.error("查询文单授权对象异常",e);
    	}
    	return edocFormDao.getAllEdocFormsByName(user.getLoginAccount(), accountIds, name,false);
    	
    }
    
    public List<EdocForm> getAllEdocFormsByType(Long domainId,int type)
    {    	
    	return edocFormDao.getAllEdocFormsByType(domainId,type);    	
    }    
    
    public List<EdocForm> getAllEdocFormsByStatus(Long domainId,int status)
    {
    	return edocFormDao.getAllEdocFormsByStatus(domainId,status);
    }
    
    public List<EdocForm> getAllEdocFormsByTypeAndStatus(Long domainId,int type, int status)
    {
    	return edocFormDao.getAllEdocFormsByTypeAndStatus(domainId,type, status);
    }
    
    public List<EdocForm> getEdocForms(long domainId,String domainIds ,int type)
    {
    	return edocFormDao.getEdocForms(domainId,domainIds, type);
    }    
    
    public void removeEdocForm(long id) throws Exception
    {
    	edocFormDao.delete(id);
    }
    public List<EdocFormElement> getAllEdocFormElements(){
    	return edocFormElementDao.getAllEdocFormElements();
    }  
    public List<EdocFormElement> getEdocFormElementByFormId(long formId){
    	return edocFormElementDao.getEdocFormElementByFormId(formId);
    }
    public String getEdocFormXmlData(long formId,EdocSummary edocSummary,long actorId, int edocType)
    {
    	return getEdocFormXmlData(formId,edocSummary,actorId,edocType,false,false);
    }
    public String getEdocFormXmlData(long formId,EdocSummary edocSummary,long actorId, int edocType,boolean isTemplete,boolean isCallTemplete){
      	List <EdocFormElement>elements=getEdocFormElementByFormId(formId);
		StringBuffer sBuffer = xmlConverter.convert(elements,edocSummary,actorId, edocType,isTemplete ,isCallTemplete);
    	return sBuffer.toString();
    }
    public void saveEdocXmlData(long id,List<Long> elementIdList){
    	for(Long elementId:elementIdList){
    		
    		EdocFormElement eFormElement = new EdocFormElement();
    		eFormElement.setIdIfNew();
    		eFormElement.setFormId(id);
    		eFormElement.setElementId(elementId);
    		edocFormElementDao.save(eFormElement);
    		
    	}
    }
    public EdocFormModel getEdocFormModel(long formId,EdocSummary edocSummary,long actorId) throws EdocException{
    	return getEdocFormModel(formId,edocSummary,actorId,false,false);
    }

    public EdocFormModel getEdocFormModel(long formId,EdocSummary edocSummary,long actorId,boolean isTemplete,boolean isCallTemplete) throws EdocException
    {
    	EdocFormModel formModel=new EdocFormModel();
    	EdocForm ef=edocFormDao.get(formId);
    	User user=CurrentUser.get();    
    	String content = "";
    	if(ef==null)
    	{
    		ef=getDefaultEdocForm(user.getAccountId(),edocSummary.getEdocType());
    	}
    	if(null!=ef){
    	content = ef.getContent();
    	try{
    	byte[] tempByte_b = CharReplace.doReplace_Decode(content.getBytes("UTF-8"));
    	content = new String(tempByte_b,"UTF-8");		
    	}catch(Exception e){
    		log.error("getEdocFormModel得到公文单错误，formId="+formId,e);
    		throw new EdocException(e);
    	}
    	formModel.setXslt(content);
    	formModel.setXml(getEdocFormXmlData(formId,edocSummary,actorId, ef.getType().intValue(),isTemplete,isCallTemplete));
    	formModel.setEdocFormId(ef.getId());
    	formModel.setEdocSummary(edocSummary);
    	return formModel;
    	}else{
    		return null;
    	}
    }
    public EdocFormModel getEdocFormModel(long formId,long actorId) throws EdocException
    {
    	EdocSummary edocSummary=new EdocSummary();
    	return getEdocFormModel(formId,edocSummary,actorId);
    }
    
    public void deleteFormElementByFormId(long id){
    	
    	List<EdocFormElement> list = edocFormElementDao.findBy("formId", id);
    	for(EdocFormElement ele:list){
    		edocFormElementDao.delete(ele);
    	}
    }
    
    /**
     * ajax方法
     * 方法描述：是否引用
     *
     */
    public String ajaxIsReferenced(String id)throws Exception{
    	boolean bool = edocFormDao.isReferenced(Long.valueOf(id));
		 if(bool){
				return "TRUE";
		}else{
			return "FALSE";
		}
    }
    
    /**
     * 判断是否被引用
     * 是:弹出提示,不允许删除
     * 否:首先删除引用的公文元素,其次删除公文单样式文件
     */
	 public String deleteForm(long id) throws Exception{
		 
		 List<EdocForm> edocList = edocFormDao.findBy("id", id);
		 EdocForm ef=edocList.get(0);
		 Set<EdocFormExtendInfo> infos = ef.getEdocFormExtendInfo();
		 for(EdocFormExtendInfo info : infos){
			 if(info.getIsDefault())
			 {
				 removeDefaultEdocForm(info.getAccountId(),ef.getType());			 
			 }
		 }
		
		 boolean bool = edocFormDao.isReferenced(id);
		 if(bool){
			return "alert(parent._('edocLang.edoc_form_referenced'));";
		 }
		 this.deleteFormElementByFormId(id);
		 try{
		 attachmentManager.deleteByReference(id, id);
		 }catch(Exception e){
			 log.error("deleteForm函数，删除公文单错误，id="+id,e);
			 throw e;
		 }
		 		 
		 edocFormDao.delete(ef);
		 return null;
	 }
	  public void updateForm(EdocForm edocForm){
		  edocFormDao.update(edocForm);
	  }
	  
	  public String getDirectory(String[] urls,String[] createDates,String[] mimeTypes,String[] names)throws Exception{
		  		  
		  
			File file = null;
			String path = null;
			String directory = "";
		try{
		  for(int i=0;i<urls.length;i++){
				Long fileId=Long.parseLong(urls[i]);
				Date createDate=null;
				//try {
				if(createDates[i].length() > 10)
					createDate = Datetimes.parseDatetime(createDates[i]);
				else
					createDate = Datetimes.parseDate(createDates[i]);
				/*} catch (ParseException e1) {
						log.error("EdocFormManager.getDirectory(),日前解析错误，createDate="+createDates[i],e1);
						throw e1;
				};	*/			
				
					file=fileManager.getFile(fileId, createDate);
					if(null == file)return "";
					System.out.println("The URL:"+file.getPath()+" "+file.getName()+" "+file.length()+" Type:"+mimeTypes[i]+" name:"+names[i]);
					path = file.getPath();
					String startTime = Datetimes.format(new Date(), "yyyyMMddHHmmss").substring(0,4);
					if(directory.equals("")){
					int position = path.indexOf(String.valueOf(startTime));
					if(position == -1)return "";
					directory = path.substring(0, position);
					}
					//File realfile = new File(directory + names[i]);

				}
			}catch(Exception e){
				log.error("获取公文单目录出错",e);
				return "";
			}
			
			return file.getPath();
	  }
	  
	  public void updateDefaultEdocForm(long domainId,int type){
		//  edocFormDao.updateDefaultEdocForm(domainId, type);
		  EdocFormExtendInfo info = edocFormExtendInfoDao.getDefaultEdocFormExtendInfo(domainId,type);
		  if(info!= null){
			  edocFormExtendInfoDao.cancelDefaultEdocForm(info.getId());
		  }
	  }
	  public void initAccountEdocForm(long accountId) throws Exception
	  {
		  List <EdocForm> forms=edocFormDao.getAllEdocForms(0L);
		  for(EdocForm form :forms)
		  {
			  form=(EdocForm)form.clone();
			  form.resetId();
			  form.setDomainId(accountId);
			  edocFormDao.save(form);
		  }
	  }
	  public void importEdocForm(String formIds) throws Exception
	  {
		  List <EdocForm> forms=edocFormDao.getEdocForms(formIds);
		  for(EdocForm form :forms)
		  {
			  form=(EdocForm)form.clone();
			  form.resetId();
			  form.setIsDefault(false);
			  form.setDomainId(CurrentUser.get().getAccountId());
			  edocFormDao.save(form);
		  }
	  }
	  
	  public EdocForm getDefaultEdocForm(Long domainId,int edocType)
	  {
		  EdocForm tempForm=defaultEdocForm.get(domainId.toString()+"_"+edocType);
		  if(tempForm ==null)
		  {
			  tempForm=_getDefaultEdocForm(domainId,edocType);
			  if(tempForm!=null)
			  {
			    defaultEdocForm.put(domainId.toString()+"_"+edocType,tempForm);
			  }
		  }
		  return tempForm;		  
	  }
	  
	  public void setDefaultEdocForm(Long domainId,int edocType,EdocForm edocForm)
	  {
		  defaultEdocForm.put(domainId.toString()+"_"+edocType,edocForm);		  
	  }
	  public void removeDefaultEdocForm(Long domainId,int edocType)
	  {
		  defaultEdocForm.remove(domainId.toString()+"_"+edocType);		
		  NotificationManager.getInstance().send(NotificationType.DefaultEdocFormRemove, new Object[]{domainId,edocType});
	  }
	  /**
	   * 如果没有设置默认公文单，返回第一个公文单为默认公文单
	   * @param domainId
	   * @param edocType
	   * @return
	   */
	  private EdocForm _getDefaultEdocForm(Long domainId,String domainIds,int edocType)
	  {
		  EdocForm edocForm=null;
		  List<EdocForm> ls=edocFormDao.getEdocForms(domainId,domainIds, edocType);
		  for(EdocForm ef:ls)
		  {
			  if(ef.getIsDefault())
			  {
				  edocForm=ef;
				  break;
			  }
		  }
		  //没有设置默认公文单
		  if(edocForm==null)
		  {
			  if(ls!=null && ls.size()>0)
			  {
				  edocForm=ls.get(0);
			  }
		  }
		  return edocForm;
	  }
	  private EdocForm _getDefaultEdocForm(Long domainId, int edocType){
		  User user =CurrentUser.get();
		  String domainIds = "";
		  try {
			 domainIds= orgManager.getUserIDDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		  } catch (BusinessException e) {
			log.error("获取默认公文单的时候查找domain对象异常",e);
		  }
		  return _getDefaultEdocForm(domainId,domainIds,edocType);
	  }
	  /**
	   * 检查公文单是否有重名
	   * @param name 名称
	   * @param type 类型(发文,收文,签报)
	   * @param status 状态,是否为启用
	   * @param domainId 单位标识
	   * @return
	   */
	  public boolean checkHasName(String name,int type){
		  
		  boolean bool = false;
		  
		  User user = CurrentUser.get();
		  
		  return edocFormDao.getEdocFormByName(null,user.getLoginAccount(), name, type)>0;
		 
	  }
	  
	  /**
	   * 方法描述：ajax方法，动态判断是否重名
	   *
	   */
	  public boolean ajaxCheckDuplicatedName(String name, String type, String id){
		  boolean bool = false;
		  
		  User user = CurrentUser.get();
		  
		 return edocFormDao.getEdocFormByName(id,user.getLoginAccount(), name,  Integer.valueOf(type).intValue())>0;
		
	  }
	  
	  public void bound(String name, String boundName, String boundNameLabel, long edocFormId, String sortType,Long accountId)throws Exception{
	
		  List<EdocFormFlowPermBound> list = new ArrayList<EdocFormFlowPermBound>();
		  String[] boundList = boundName.split(",");
		  String[] boundNameList = boundNameLabel.split(",");
		  for(int i=0;i<boundList.length;i++){
			  EdocFormFlowPermBound bound = new EdocFormFlowPermBound();
			  bound.setIdIfNew();
			  bound.setEdocFormId(edocFormId);
			  bound.setFlowPermName(boundList[i]);
			  bound.setProcessName(name);
			  bound.setFlowPermNameLabel(boundNameList[i]);
			  bound.setSortType(sortType);
			  bound.setDomainId(accountId);
			  list.add(bound);
		  }
		  edocFormFlowPermBoundDao.saveAll(list);
	  }
	 
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId)throws Exception{
		  
		  DetachedCriteria criteria = DetachedCriteria.forClass(EdocFormFlowPermBound.class);	
		  criteria.add(Restrictions.eq("edocFormId", edocFormId));
		  
		  List<EdocFormFlowPermBound> list = edocFormFlowPermBoundDao.executeCriteria(criteria, -1, -1);	 
		  
		  return list;
	  }
	  
	  public List<EdocFormFlowPermBound> findBoundByFormIdAndDomainId(long edocFormId,Long accountId)throws Exception{
		  
		  DetachedCriteria criteria = DetachedCriteria.forClass(EdocFormFlowPermBound.class);	
		  criteria.add(Restrictions.eq("edocFormId", edocFormId));
		  criteria.add(Restrictions.eq("domainId", accountId));
		  
		  List<EdocFormFlowPermBound> list = edocFormFlowPermBoundDao.executeCriteria(criteria, -1, -1);	 
		  
		  return list;
	  }
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId, String processName)throws Exception{
		  
		  DetachedCriteria criteria = DetachedCriteria.forClass(EdocFormFlowPermBound.class);	
		  criteria.add(Restrictions.eq("edocFormId", edocFormId));
		  criteria.add(Restrictions.eq("processName", processName));
		  
		  List<EdocFormFlowPermBound> list = edocFormFlowPermBoundDao.executeCriteria(criteria, -1, -1);	 
		  
		  return list;
	  }
	  public List<EdocFormFlowPermBound> findBoundByFormId(long edocFormId, String processName,long accountId)throws Exception{
		  
		  DetachedCriteria criteria = DetachedCriteria.forClass(EdocFormFlowPermBound.class);	
		  criteria.add(Restrictions.eq("edocFormId", edocFormId));
		  criteria.add(Restrictions.eq("processName", processName));
		  criteria.add(Restrictions.eq("domainId", accountId));
		  List<EdocFormFlowPermBound> list = edocFormFlowPermBoundDao.executeCriteria(criteria, -1, -1);	 
		  
		  return list;
	  }
	  public void deleteEdocFormFlowPermBoundByFormId(long edocFormId)throws Exception{
		  
		  edocFormFlowPermBoundDao.deleteFormFlowPermBoundByFormId(edocFormId);
		  
	  }
	  public void deleteEdocFormFlowPermBoundByFormIdAndAccountId(long edocFormId,long accountId)throws Exception{
		  
		  edocFormFlowPermBoundDao.deleteFormFlowPermBoundByFormId(edocFormId,accountId);
		  
	  }

	public EdocFormFlowPermBoundDao getEdocFormFlowPermBoundDao() {
		return edocFormFlowPermBoundDao;
	}

	public void setEdocFormFlowPermBoundDao(
			EdocFormFlowPermBoundDao edocFormFlowPermBoundDao) {
		this.edocFormFlowPermBoundDao = edocFormFlowPermBoundDao;
	}

	public Hashtable<String,String> getOpinionLocation(Long edocFormId,Long accountId)
	{
		Hashtable hs=new Hashtable<String,String>();
		List <EdocFormFlowPermBound> ls=edocFormFlowPermBoundDao.find("from EdocFormFlowPermBound b where b.edocFormId=? and b.domainId=? ", new Object[]{edocFormId,accountId});
		for(EdocFormFlowPermBound eb:ls)
		{
			// hs.put(eb.getFlowPermName(),eb.getProcessName());
			// 公文元素名称为value_公文元素的排序方式
			hs.put(eb.getFlowPermName(), eb.getProcessName() + "_"
					+ eb.getSortType());
		}
		return hs;
	}
	
	public List<String> getOpinionElementLocationNames(Long edocFormId,Long aclAccountId)
	{
		
		Hashtable<String,String> fbs= getOpinionLocation(edocFormId,aclAccountId);
		List <String> ens=new ArrayList<String>();
		Enumeration en = fbs.keys();
		String szStr;
		while(en.hasMoreElements())
		{
			szStr=fbs.get(en.nextElement().toString());
			if(ens.contains(szStr)==false)
			{
				ens.add(szStr);
			}
		}		
		return ens;
	}
	public List<String> getOpinionElementLocationNames(Long edocFormId)
	{
		User user = CurrentUser.get();
		return getOpinionElementLocationNames(edocFormId,user.getLoginAccount());
	}
	public void initialize(){
		baseFileFolder = SystemProperties.getInstance().getProperty("edoc.folder");
		try{
		log.info("执行公文单,公文模板,岗位导入模板,人员导入模板文件检查与复制...");
		copyEdocFile();
		}catch(Exception e){
			log.error("复制公文单与公文模板,岗位导入模板,人员导入模板文件失败", e);
		}
        updateFormContentToDBOnlyForOracle();
	}
	
	/**
	 * 检查是否有公文单及套红模板文件存，如果不存在复制一份到指定分区
	 * @throws Exception
	 */
	private void copyEdocFile()throws Exception{
		
		String[] t_FileIds = new String[1];
		
		t_FileIds[0] = "-6001972826857714844"; //套红模板文件压缩包
		
		
		String[] f_FileIds = new String[3];
		// -- 公文单（签报，收文，发文）
		f_FileIds[0] = "-1766191165740134579"; 
		f_FileIds[1] = "-2921628185995099164";
		f_FileIds[2] = "6071519916662539448";		
		
		String[] o_FileIds = new String[2];
		//人员导入模板,岗位导入模板
		o_FileIds[0] = "43263267400010875";
		o_FileIds[1] = "-6777944130366976701";
		
		this.copyFile(t_FileIds, Constants.EDOC_FILE_TYPE_TEMPLATE);
		this.copyFile(f_FileIds, Constants.EDOC_FILE_TYPE_EDOCFORM);
		//不需要拷贝
		//1.物理目录下面文件名是member,post
		//2.模板下载的时候，文件名也是member,post.故不需要拷贝文件。
		//this.copyFile(o_FileIds, Constants.ORGNIZATION_FILE_TYPE);
		
	}
	
	private void copyFile(String[] fileIds, int type)throws Exception{
		
		String fileFolder = baseFileFolder;
		if(type == Constants.EDOC_FILE_TYPE_EDOCFORM){
			fileFolder += formFolder;
		}
		else if(type == Constants.EDOC_FILE_TYPE_TEMPLATE){
			fileFolder += templateFolder;
		}else if(type ==Constants.ORGNIZATION_FILE_TYPE){
			fileFolder += orgFolder; 
		}
		
		for(String id : fileIds){
			V3XFile v3xFile= fileManager.getV3XFile(Long.valueOf(id));
			if(null != v3xFile){
				File file = fileManager.getFile(v3xFile.getId()); 
					if(null == file){
						File tempFile = new File(fileFolder+ File.separator + id);
						if(null!=tempFile){
							String folder = fileManager.getFolder(new Date(), true);
							v3xFile.setUpdateDate(new Date());
							v3xFile.setCreateDate(new Date());
							fileManager.update(v3xFile);
							Attachment attachment  = attachmentManager.getAttachmentByFileURL(v3xFile.getId());
							attachment.setCreatedate(new Date());
							attachmentManager.update(attachment);
							try{
					    	FileUtil.copy(fileFolder+ File.separator + id, folder + File.separator + id);
							}catch(Exception e){
								log.info("复制文件失败 id = " + id);
							}
						}
					}
			}
		}
		
	}
	
	public void saveEdocForm(EdocForm form){
		edocFormDao.save(form);
	}
	public void saveEdocFormExtendInfo(EdocFormExtendInfo form){
		edocFormExtendInfoDao.save(form);
	}
	/**
	 * 公文单的Content字段相对较大，Oracle环境无法使用初始化的方式插入
	 * 现插入3条记录，然后读取xml中得样式文件，将对应的数据插入到edocForm 中的 Content 字段中去
	 */
	private void updateFormContentToDBOnlyForOracle(){
		String dbType = com.seeyon.v3x.doc.util.Constants.getDBType();
		if("oracle".equalsIgnoreCase(dbType) || "sqlserver".equalsIgnoreCase(dbType)){	
			try{
			log.info("为ORACLE-SqlServer预置公文单初始化数据......");

			byte[] byteArray = StringUtils.readFileData(baseFileFolder + File.separator +"all.xml");
			String path = IOUtils.toString(byteArray, "UTF-8");
			String sendXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_SEND_START)+Constants.EDOC_EDOCFORM_XSL_SEND_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_SEND_END));
			String recXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_REC_START)+Constants.EDOC_EDOCFORM_XSL_REC_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_REC_END));
			String signXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_SIGN_START)+Constants.EDOC_EDOCFORM_XSL_SIGN_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_SIGN_END));
			List<EdocForm> formList = new ArrayList<EdocForm>();
			/*
			ProductEditionEnum productEdition = ProductInfo.getEdition();
			if(productEdition.ordinal() == ProductEditionEnum.enterprise.ordinal() || productEdition.ordinal() == ProductEditionEnum.government.ordinal()){
				formList = this.getAllEdocForms(Long.valueOf("670869647114347"));//如果是企业版，复制此单位		
			}
			else if(productEdition.ordinal() == ProductEditionEnum.entgroup.ordinal() || productEdition.ordinal() == ProductEditionEnum.governmentgroup.ordinal()){
				formList= this.getAllEdocForms(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);	//集团版
			}				
			*/
			if(((Boolean)SysFlag.sys_isGroupVer.getFlag())==false){
				formList = this.getAllEdocForms(Long.valueOf("670869647114347"));//如果是企业版，复制此单位		
			}
			else {
				formList= this.getAllEdocForms(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);	//集团版
			}
			for(EdocForm form : formList){
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_SEND){
					if(Strings.isBlank(form.getContent())){
						form.setContent(sendXsl);
						log.info("初始化发文单......");
						this.updateForm(form);
					}
				}
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_REC){
					if(Strings.isBlank(form.getContent())){
						form.setContent(recXsl);
						log.info("初始化收文单......");
						this.updateForm(form);
					}
				}
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_SIGN){
					if(Strings.isBlank(form.getContent())){
						form.setContent(signXsl);
						log.info("初始化签报单......");
						this.updateForm(form);
					}
				}
			}
			}catch(Exception e){
				log.error("公文单初始化失败!",e);
			}
			log.info("公文单初始化完毕!");
		}
	}	
	
	/**
	 * 测试使用
	 */
	public void updateFormContentToDBOnlyForOracle1(){
		String dbType = com.seeyon.v3x.doc.util.Constants.getDBType();
		if("oracle".equalsIgnoreCase(dbType)){	
			try{
			log.info("为ORACLE预置公文单初始化数据......");

			//String sendXsl = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"send.txt");
			//String recXsl = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"rec.txt");
			//String signXsl = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"sign.txt");
			//String allXsl = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"xsl.txt");
			//String allXslXml = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"xml.txt");
			byte[] byteArray = StringUtils.readFileData(baseFileFolder + File.separator +"all.xml");
			String path = IOUtils.toString(byteArray, "UTF-8");
			//String path = StringUtils.readFileToString(baseFileFolder + Folder.PATH_SEPARATOR +"all.xml");
			String sendXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_SEND_START)+Constants.EDOC_EDOCFORM_XSL_SEND_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_SEND_END));
			String recXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_REC_START)+Constants.EDOC_EDOCFORM_XSL_REC_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_REC_END));
			String signXsl = path.substring(path.indexOf(Constants.EDOC_EDOCFORM_XSL_SIGN_START)+Constants.EDOC_EDOCFORM_XSL_SIGN_START.length(),path.indexOf(Constants.EDOC_EDOCFORM_XSL_SIGN_END));
			List<EdocForm> formList = new ArrayList<EdocForm>();
			boolean isEnterVer=((Boolean)(SysFlag.sys_isEnterpriseVer.getFlag()) || (Boolean)(SysFlag.sys_isGovVer.getFlag()));
			boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());
			if(isEnterVer){
				formList = this.getAllEdocForms(Long.valueOf("670869647114347"));//如果是企业版，复制此单位
			}
			else if(isGroupVer){
				formList= this.getAllEdocForms(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);				
			}
			for(EdocForm form : formList){
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_SEND){
					if(Strings.isBlank(form.getContent())){
						form.setContent(sendXsl);
						log.info("初始化发文单......");
						this.updateForm(form);
					}
				}
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_REC){
					if(Strings.isBlank(form.getContent())){
						form.setContent(recXsl);
						log.info("初始化收文单......");
						this.updateForm(form);
					}
				}
				if(form.getType().intValue() == Constants.EDOC_FORM_TYPE_SIGN){
					if(Strings.isBlank(form.getContent())){
						form.setContent(signXsl);
						log.info("初始化签报单......");
						this.updateForm(form);
					}
				}
			}
			}catch(Exception e){
				log.info("公文单初始化失败!");
			}
			log.info("公文单初始化完毕!");
		}
	}	
	/**
	 * 把公文单中的元素数据转换成表单对象,用于设置分支条件
	 * @param edocForm
	 * @return
	 */
	public SessionObject getElementByEdocForm(EdocForm edocForm)
	{
		SessionObject so=new SessionObject();
		List<TableFieldDisplay> tfList=new ArrayList<TableFieldDisplay>();
		Set<EdocFormElement> eles=null;
		try{
			eles=edocForm.getEdocFormElements();
		}catch(Exception e)
		{
			eles=edocFormDao.get(edocForm.getId()).getEdocFormElements();
		}
		for(EdocFormElement efe:eles)
		{
			try{
			TableFieldDisplay tf=new TableFieldDisplay();
			EdocElement ele=edocElementManager.getEdocElementsById(efe.getElementId());
			if(ele.getType()==EdocElement.C_iElementType_Date 
				|| ele.getType()==EdocElement.C_iElementType_Decimal 
				|| ele.getType()==EdocElement.C_iElementType_Integer
				|| ele.getType()==EdocElement.C_iElementType_List)
			{
				switch(ele.getType())
				{
					case EdocElement.C_iElementType_Date:tf.setFieldtype(IPagePublicParam.TIMESTAMP);break;
					case EdocElement.C_iElementType_Decimal:tf.setFieldtype(IPagePublicParam.DECIMAL);break;
					case EdocElement.C_iElementType_Integer:tf.setFieldtype(IPagePublicParam.DECIMAL);break;
					case EdocElement.C_iElementType_List:
						//如果是单位枚举，通过id+domain取公文元素
						if(!ele.getIsSystem())
							ele=edocElementManager.getEdocElement(efe.getElementId().toString());
						tf.setDivenumtype("enum");
						tf.setEnumtype(Long.toString(ele.getMetadataId()));
						tf.setTablename("main");
						break;
				}
				tf.setName(ele.getFieldName());	
				tfList.add(tf);
			}
			}catch(Exception e){
				log.debug("",e);
			}
		}
		so.setTableFieldList(tfList);
		so.setFormsort(0L);
		return so;
	}
	
	/**
	 * 方法描述: 二选一的参数
	 * ajaxCheckFormIsIdealy(String, isDefault, "") -- 是否默认公文单， is default form or not
	 * ajaxCheckFormIsIdealy(String, "", isEnabled) -- 是否是启用公文单，whether the form is enabled or disabled
	 */
	public boolean ajaxCheckFormIsIdealy(String edocFormStatusId, String isDefault, String isEnabled){
		if(!Strings.isBlank(edocFormStatusId)){
			EdocFormExtendInfo edocFormExtendInfo = this.getEdocFormExtendInfo(Long.valueOf(edocFormStatusId));
			if(null!=edocFormExtendInfo){
				if(!Strings.isBlank(isDefault)){
					return edocFormExtendInfo.getIsDefault();
				}
				if(!Strings.isBlank(isEnabled)){
					return edocFormExtendInfo.getStatus().intValue() == Constants.EDOC_DOCTEMPLATE_DISABLED ? false : true;	
				}
			}
		}
		return false;
	}
	/**
	 * 取得公文单意见显示设置
	 * @param formId  ： 公文单ID
	 * @param accountId ：单位ID
	 * @return
	 */
	public EdocOpinionDisplayConfig getEdocOpinionDisplayConfig(Long formId,Long accountId)
	{
		EdocOpinionDisplayConfig displayConfig = new EdocOpinionDisplayConfig();
		
		// 公文单显示格式
		String optionFormatSet = "0,0,0";
		EdocForm form = getEdocForm(formId);
		Set<EdocFormExtendInfo> infos = form.getEdocFormExtendInfo();
		for(EdocFormExtendInfo info : infos ){
			if(info.getAccountId().equals(accountId)){
				optionFormatSet = info.getOptionFormatSet();
				break;
			}
		} 
		if (!Strings.isBlank(optionFormatSet)) {
			String[] optionFormatSets = optionFormatSet.split(",");
			if ("1".equals(optionFormatSets[0])) {
				displayConfig.setOnlyShowLastOpinion(true);
			}
			if ("1".equals(optionFormatSets[1])) {
				displayConfig.setShowDeptName(true);
			}
			if ("1".equals(optionFormatSets[2])) {
				//日期
				displayConfig.setShowDate(EdocOpinionDisplayConfig.DateFormat.date.ordinal());
			} else {
				//日期时间
				displayConfig.setShowDate(EdocOpinionDisplayConfig.DateFormat.dateTime.ordinal());
			}
		}
		return displayConfig;
	}
	public Hashtable<String, String> getOpinionLocation(Long edocFormId) {
		User user =CurrentUser.get();
		
		return getOpinionLocation(edocFormId,user.getLoginAccount());
	}
	public  EdocFormExtendInfo getEdocFormExtendInfo(Long id){
		return edocFormExtendInfoDao.get(id);
	}
	
	public List<EdocForm> getEdocFormByAcl(String domainIds){
		return edocFormDao.getEdocFormByAcl(domainIds);
	}
	
	public boolean isExsit(Long formId){
		return edocFormDao.isExsit(formId);
	}


}