package com.seeyon.v3x.common.office;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import DBstep.iMsgServer2000;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.barCode.manager.BarCodeManager;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.Util;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.signet.domain.V3xDocumentSignature;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;


/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-12
 */
public class HandWriteManager {
	
	private static OnLineManager onLineManager;
	private static OrgManager orgManager;
	
	private static Log log = LogFactory.getLog(HandWriteManager.class);
	
	private static String rc="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	
	
	private Long fileId;

	private Date createDate;
	
	private Long originalFileId;
	
	private Date originalCreateDate;
	
	private boolean needClone = false;
	
	private boolean needReadFile = false;

	private FileManager fileManager;
	
	private SignetManager signetManager;
	
	private OfficeTransManager officeTransManager;
	
	private BarCodeManager barCodeManager;

	public OfficeTransManager getOfficeTransManager() {
		return officeTransManager;
	}

	public void setOfficeTransManager(OfficeTransManager officeTransManager) {
		this.officeTransManager = officeTransManager;
	}	
	private synchronized void init() {
		if(onLineManager == null){
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			onLineManager = (OnLineManager)ApplicationContextHolder.getBean("onLineManager");
		}
	}
	public HandWriteManager()
	{
		init();		
	}
	
	public void setSignetManager(SignetManager signetManager)
	{		
		this.signetManager=signetManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public void setBarCodeManager(BarCodeManager barCodeManager) {
		this.barCodeManager = barCodeManager;
	}
	//调入用户有权使用的印章列表
	public boolean LoadSinatureList(iMsgServer2000 msgObj) throws BusinessException {
		List <V3xSignet>ls=null;
		Long userId=CurrentUser.get().getId();
		try{
			String mMarkList="";
			ls=signetManager.findSignetByMemberId(userId);
			for(V3xSignet signet:ls)
			{
				mMarkList+=signet.getMarkName()+"\r\n";
			}
			msgObj.SetMsgByName("MARKLIST",mMarkList);
			msgObj.SetMsgByName("SIGNATRUELIST",mMarkList);     //文单印章
		    msgObj.MsgError("");				//清除错误信息
		}catch(Exception e)
		{
			log.error(e);
			throw new BusinessException(e.getMessage());
		}
		return true;		
	}
	
	//根据印章名称，密码，调入印章图片
	public boolean LoadSinature(iMsgServer2000 msgObj) throws BusinessException {
		 String mMarkName=msgObj.GetMsgByName("IMAGENAME");	        //取得文档名		 
		 //String mUserName=msgObj.GetMsgByName("USERNAME");		//取得文档名
		 String mPassword=msgObj.GetMsgByName("PASSWORD");		//取得文档类型
		 msgObj.MsgTextClear();
		 V3xSignet signet=null;
		 try{
			 signet=signetManager.findByMarknameAndPassword(mMarkName, mPassword);		 
			 if(signet!=null)
			 {
				byte[] b = signet.getMarkBodyByte();
					
				 msgObj.SetMsgByName("IMAGETYPE",signet.getImgType());        //设置图片类型
				 msgObj.MsgFileBody(b);			//将文件信息打包
				 msgObj.SetMsgByName ("SIGNTYPE",Integer.toString(signet.getMarkType()));                     //（手写签名0，单位印章1）默认值1 
				 msgObj.SetMsgByName("STATUS",ResourceBundleUtil.getString(rc,"ocx.alert.opensucceed.label"));  	//设置状态信息
				 msgObj.SetMsgByName ("ZORDER","5");	//4:在文字上方 5:在文字下
				 msgObj.MsgError("");				//清除错误信息
			 }
			 else
			 {
				 msgObj.MsgError(ResourceBundleUtil.getString(rc,"ocx.alert.pwderr.label"));
			 }
		 }catch(Exception e)
		 {
			 throw new BusinessException(e.getMessage());
		 }
		return true;
	}
	
	/**
	 * 调用文档的签章记录
	 * @param msgObj
	 * @return
	 * @throws BusinessException
	 */
	public boolean LoadDocumentSinature(iMsgServer2000 msgObj) throws BusinessException {
		String mRecordId=msgObj.GetMsgByName("RECORDID");
		msgObj.MsgTextClear();
		List<V3xDocumentSignature> ls=null;
		try{			
			ls=signetManager.findDocumentSignatureByDocumentId(mRecordId);
			String mMarkName=ResourceBundleUtil.getString(rc,"ocx.signname.label")+"\r\n";
			String mUserName=ResourceBundleUtil.getString(rc,"ocx.signuser.label")+"\r\n";
			String mDateTime=ResourceBundleUtil.getString(rc,"ocx.signtime.label")+"\r\n";
			String mHostName=ResourceBundleUtil.getString(rc,"ocx.clientip.label")+"\r\n";
			String mMarkGuid=ResourceBundleUtil.getString(rc,"ocx.serialnumber.label")+"\r\n";
			//log.error("DEBUG INFO:签章记录列头数据：("+mMarkName+")("+mUserName+")("+mDateTime+")("+mHostName+")("+mMarkGuid+")");
			for(V3xDocumentSignature ds:ls)
			{
				mMarkName+=ds.getMarkname()+"\r\n";
				mUserName+=ds.getUsername()+"\r\n";
				mDateTime+=Datetimes.formatDatetime(ds.getSignDate())+"\r\n";
				mHostName+=ds.getHostname()+"\r\n";
				mMarkGuid+=ds.getMarkguid()+"\r\n";
			}
			msgObj.SetMsgByName("MARKNAME",mMarkName);
			msgObj.SetMsgByName("USERNAME",mUserName);
			msgObj.SetMsgByName("DATETIME",mDateTime);
			msgObj.SetMsgByName("HOSTNAME",mHostName);
			msgObj.SetMsgByName("MARKGUID",mMarkGuid);
			msgObj.SetMsgByName("STATUS","调入成功!");  	//设置状态信息
			msgObj.MsgError("");				//清除错误信息
		}catch(Exception e)
		{
			throw new BusinessException(e.getMessage());
		}
		return true;		
	}

	/**
	 * 调入文件之后,直接把数据放到控件服务器对象msgObj中
	 * 调入时查询备份文件ID组合后放如控件，提供花脸查看功能
	 * @return
	 * @throws BusinessException
	 */
	public boolean LoadFile(iMsgServer2000 msgObj) throws Exception {
		// 没有创建实践，说明是新建
		if (createDate == null) {
			msgObj.SetMsgByName("STATUS", "打开成功!");
			msgObj.MsgError("");
			return true;
		}
		
		Long loadFileId = originalFileId != null ? originalFileId : fileId;
		Date loadCreateDate = originalCreateDate != null ? originalCreateDate : createDate;
		String filePath = this.fileManager.getFolder(loadCreateDate, true) + File.separator ;
		
		
		V3XFile tempFile=null;
		File ftemp=new File(filePath+loadFileId);		
		if(!(ftemp.exists() && ftemp.isFile()))
		{//传入的createDate错误，没找到文件，重新查找数据库；			
			tempFile=fileManager.getV3XFile(loadFileId);
			if(tempFile!=null)
			{
				filePath = this.fileManager.getFolder(tempFile.getCreateDate(), true)+ File.separator ;
			}
		}
		
		filePath += loadFileId;
		
		if(needReadFile){
			String newfilePath = CoderFactory.getInstance().decryptFileToTemp(filePath);
			if (msgObj.MsgFileLoad(newfilePath)) {
				if(tempFile==null)
				{
					tempFile=fileManager.getV3XFile(loadFileId);
				}
				//检查备份
				String checkBack=msgObj.GetMsgByName("checkBack");
				if(!"false".equals(checkBack))
				{					
					if(tempFile!=null)
					{
				        //设置调入文档备份文档的IDs
						msgObj.SetMsgByName("backupIds",findBackFileIds(tempFile.getFilename()));
					}
				}
				if(tempFile!=null)
				{
					Date officeUpateTime=tempFile.getUpdateDate();
					if(officeUpateTime==null){officeUpateTime=tempFile.getCreateDate();}
					if(officeUpateTime!=null)
					{
						msgObj.SetMsgByName("OfficeUpdateTime",Long.toString(officeUpateTime.getTime()));
					}					
				}
				msgObj.SetMsgByName("STATUS", "打开成功!");
				msgObj.MsgError("");
				return true;
			}
			else{
				msgObj.MsgError("打开失败!");
				return false;
			}
		}
		else{
			msgObj.SetMsgByName("STATUS", "打开成功!");
			msgObj.MsgError("");
		}

		return true;
	}
	/**
	 * 保存文档签章记录
	 * @param msgObj
	 * @return
	 * @throws BusinessException
	 */
	public boolean saveDocumentSignatureRecord(iMsgServer2000 msgObj,HttpServletRequest request) throws BusinessException {
		V3xDocumentSignature ds=new V3xDocumentSignature();
		ds.setIdIfNew();
		ds.setRecordId(msgObj.GetMsgByName("RECORDID"));//取得模板编号
		ds.setMarkname(msgObj.GetMsgByName("MARKNAME"));//设置印章名称
		ds.setUsername(msgObj.GetMsgByName("USERNAME"));//盖章用户名称
		ds.setSignDate(new Timestamp(Datetimes.parseDatetime(msgObj.GetMsgByName("DATETIME")).getTime()));
		ds.setMarkguid(msgObj.GetMsgByName("MARKGUID"));
		ds.setHostname(request.getRemoteAddr());
		try{
		  signetManager.save(ds);
		}catch(Exception e)
		{
			throw new BusinessException(e);
		}
		return true;		
	}

	/**
	 * 向客户端插入图片
	 * @param msgObj
	 * @return
	 * @throws Exception
	 */
	public void insertImage(iMsgServer2000 msgObj,HttpServletRequest request)throws  Exception{
		
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		String path=SystemProperties.getInstance().getProperty(SystemProperties.CONFIG_APPLICATION_ROOT_KEY);
		path = Strings.getCanonicalPath(path);
		String url = MainDataLoader.getInstance().getLogoImagePath(accountId);
		msgObj.MsgFileLoad(path+url);
	}
	/**
	 * 保存文档，如果文档存在，则覆盖，不存在，则添加
	 * 清稿保存时，备份原文件，最多备份5份
	 * @return
	 * @throws BusinessException
	 */
	public boolean saveFile(iMsgServer2000 msgObj) throws Exception {
		if (createDate == null) { 
			createDate = new Date();
		}
		
		if(Strings.isNotBlank(msgObj.GetMsgByName("newPdfFileId")))//如果是Word转PDF，则需要新生成ID
			fileId=Long.parseLong(msgObj.GetMsgByName("newPdfFileId"));
		
		if(needClone){//需要clone
			//originalCreateDate空指针导致调用Office格式模板发送报错	Mazc 2009-11-24
			Date loadCreateDate = originalCreateDate;
			if(loadCreateDate == null){
				String _originalCreateDate = msgObj.GetMsgByName("originalCreateDate");
				if(Strings.isNotBlank(_originalCreateDate)){
					loadCreateDate = com.seeyon.v3x.util.Datetimes.parseDatetime(_originalCreateDate);
				}
				else{
					loadCreateDate = createDate;
				}
			}
			try {
				this.fileManager.clone(originalFileId, loadCreateDate, fileId, createDate);
			}
			catch (FileNotFoundException e) {
			}
		}
		
		String filePath = this.fileManager.getFolder(createDate, true)
			+ File.separator + fileId;

			//备份物理文件，防止文件丢失。
			bakPhysicalFile(filePath);
		
			
		boolean isSuccessSave = false;
		
		// 标准office的处理
		String stdOffice = msgObj.GetMsgByName("stdOffice");
		Integer category = new Integer(msgObj.GetMsgByName("CATEGORY"));
		String editType=msgObj.GetMsgByName("editType");
		if("clearDocument".equals(editType))
		{//清稿保存，进行备份
			V3XFile tempFile=null;
			List<V3XFile> fs=fileManager.findByFileName("copy"+fileId.toString());
			while(fs!=null && fs.size()>=5)
			{
				tempFile=fs.remove(0);
				fileManager.deleteFile(tempFile.getId(),true);
			}
			try{
				fileManager.clone(fileId);
			}
			catch(FileNotFoundException e){
				//ignore e
			}
			catch(Exception e){
				//直接新建，清稿没有原文件不需要备份
				//throw new BusinessException(e.getMessage());
				log.error("",e);
			}
		}
		
		String tempFile = SystemEnvironment.getSystemTempFolder() + File.separator + UUIDLong.absLongUUID();
		boolean isDraftTaoHong="draftTaoHong".equals(msgObj.GetMsgByName("draftTaoHong"));//拟文正文套红
		isSuccessSave = msgObj.MsgFileSave(tempFile);
		if(!isSuccessSave){
			log.error("office正文保存失败(msgObj.MsgFileSave),isSuccessSave:"+isSuccessSave+",tempFile:"+tempFile);
		}
		//如果需要转换成标准office正文，加密前先转换
		String notJinge2StandardOffice = msgObj.GetMsgByName("notJinge2StandardOffice");
		if(!"true".equals(notJinge2StandardOffice)){
			boolean toJingge =  Util.jinge2StandardOffice(tempFile, tempFile);
			if(isSuccessSave && !toJingge){
				log.error("office正文转为标准office的时候失败( Util.jinge2StandardOffice).isSuccessSave:"+isSuccessSave+",toJingge:"+toJingge);
			}
			isSuccessSave = toJingge;
		}
		CoderFactory.getInstance().encryptFile(tempFile, filePath);
		File f = new File(filePath);
		if(f != null && f.exists())
			msgObj.SetMsgByName("fileSize",f.length()+"");

		if (isSuccessSave) {
			// 先删除以保证能触发OFFICE转换
			officeTransManager.clean(fileId, Datetimes.format(createDate, "yyyyMMdd"));
			
			if(stdOffice != null && "true".equals(stdOffice)){
				//updateFileNameTo2003(fileId);
				//文档中心历史版本编辑需要插入数据。
				if(!"true".equals(msgObj.GetMsgByName("needInsertToV3XFile"))) return true;
			}
			
			V3XFile file = new V3XFile();
			file.setId(fileId);
			file.setCategory(category);
			file.setFilename(fileId.toString());
			file.setSize(new Long(msgObj.MsgFileSize()));
			if("pdf".equalsIgnoreCase(msgObj.GetMsgByName("toFileType"))){
				file.setMimeType("application/pdf");
			}else{
				 String realFileType = msgObj.GetMsgByName("realFileType");
				 String mimeType = "msoffice";
				 if (".docx".equals(realFileType))
					 mimeType ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			     else if (".doc".equals(realFileType))
			    	 mimeType="application/msword";
			     else if (".xls".equals(realFileType))
			    	 mimeType = "application/vnd.ms-excel";
			     else if (".xlsx".equals(realFileType))
			    	 mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
				 
				 file.setMimeType(mimeType);
			}
			
			file.setCreateDate(createDate);
			String noMillisecondTime = Datetimes.format(new Date(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss");
			file.setUpdateDate(Datetimes.parseDate(noMillisecondTime));
			
			if(!needClone||isDraftTaoHong){
				this.fileManager.deleteFile(fileId, false);
			}
			User user = CurrentUser.get();
			if(user != null){
				file.setCreateMember(user.getId());
				file.setAccountId(user.getAccountId());
			}
			if(this.fileManager.getV3XFile(file.getId())!=null){
				this.fileManager.update(file);
			}else{
				this.fileManager.save(file);
			}
			
			//更新锁信息
			UserUpdateObject os = useObjectList.get(String.valueOf(fileId));
			if(os!=null){
				if(os.getUserId() == user.getId()){
					//只能精确到秒，不能精确到毫秒，因为数据库字段保存不了毫秒的值
					os.setLastUpdateTime(file.getUpdateDate());
				}
			}
			msgObj.SetMsgByName("STATUS", "保存成功!");
			msgObj.MsgError("");
			
			// 为了适应是否支持转换的判断
			file.setType(Constants.ATTACHMENT_TYPE.FILE.ordinal());
			if (("msoffice").equals(file.getMimeType()))
				file.setFilename(file.getFilename() + ".doc");
			
			if (OfficeTransHelper.allowTrans(file)) {
				officeTransManager.generate(fileId, createDate, true);
			}
			return true;
		}
		
		msgObj.MsgError("saveFaile!");
		log.error("保存offiec正文,isSuccessSave:"+isSuccessSave);
		return false;
	}
	public String ajaxGetOfficeExtension(String fileId) {
	    String extension = "";
	    if ((Strings.isNotBlank(fileId)) && (NumberUtils.isNumber(fileId))) {
	      try {
	        V3XFile file = this.fileManager.getV3XFile(Long.valueOf(fileId));
	        String mimeType = "";
	        if(file!=null){
	        	mimeType = file.getMimeType();
	        	if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType))
	        		extension = "docx";
	        	else if ("application/msword".equals(mimeType))
	        		extension = "doc";
	        	else if ("application/vnd.ms-excel".equals(mimeType))
	        		extension = "xls";
	        	else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType))
	        		extension = "xlsx";
	        }
	      }
	      catch (NumberFormatException e) {
	        log.error("", e);
	      } catch (BusinessException e) {
	        log.error("", e);
	      }
	    }
	    return extension;
	  }
//	private void  updateFileNameTo2003(Long fileId){
//		V3XFile file = null;
//		try {
//			file = fileManager.getV3XFile(fileId);
//		} catch (BusinessException e) {
//			log.error(e);
//		}
//		if(file!=null){
//			String fileName = file.getFilename();
//			if(Strings.isNotBlank(fileName)){
//				String[] suffix = fileName.split("[.]");
//				if(suffix!=null && suffix.length>1){
//					int len = suffix.length;
//					if("docx".equalsIgnoreCase(suffix[len-1])){
//						fileName = suffix[len-2]+".doc";
//					}else if("xlsx".equalsIgnoreCase(suffix[len-1])){
//						fileName = suffix[len-2]+".xls";
//					}else if("pptx".equalsIgnoreCase(suffix[len-1])){
//						fileName = suffix[len-2]+".ppt";
//					}
//				}
//			}
//			file.setFilename(fileName);
//			fileManager.update(file);
//		}
//	}
	private void bakPhysicalFile(String filePath) {
		// 公文正文备份
		//命名规则：原文件名_时刻（到秒）.bak
		//存放路径：Office正文原始文件路径下
		//比如原始office正文存放在e:\\ufseeyon\group\base\\upload\2010\01\20下，则备份文件也放到这个路径下，主要方便运维同事查找，存在的问题是不能做增量备份
		//todo:流程结束，备份文件删除
		try {
			String now = Datetimes.format(new Date(), "yyyyMMddHHmmss");
			String contentFileBak=filePath+"_"+now+".bak";
			File f=new File(filePath);
			if(f.exists()){
				FileUtils.copyFile(f, new File(contentFileBak));
			}
		}
		catch (Exception e) {
			log.error("公文正文内容备份异常 ：" + fileId, e);
		}
	}
	/**
	 * 发送处理后的数据包
	 * 
	 * @param response
	 */
	public void sendPackage(HttpServletResponse response, iMsgServer2000 msgObj) {
		/*ServletOutputStream out = null;
		try {
			out = response.getOutputStream();

			out.write(msgObj.MsgVariant());
			out.flush();
		}
		catch (Exception e) {
		}
		finally {
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException e) {
				}
			}
		}*/
		msgObj.SendPackage(response);
	}

	/**
	 * 从request中读取参数，并写道iMsgServer2000中去
	 * 
	 * @param request
	 *            由controller传过来
	 * @param iMsgServer2000
	 */
	public void readVariant(HttpServletRequest request, iMsgServer2000 msgObj) {
		/*byte[] bs = null;
		try {
			InputStream in = request.getInputStream();
			if (in != null) {
				bs = org.apache.commons.io.IOUtils.toByteArray(in);
			}
		}
		catch (IOException e1) {
		}

		if (bs != null) {
			msgObj.MsgVariant(bs);
		}
		*/
		msgObj.ReadPackage(request);

		fileId = new Long(msgObj.GetMsgByName("RECORDID"));
		createDate = com.seeyon.v3x.util.Datetimes.parseDatetime(msgObj.GetMsgByName("CREATEDATE"));
		
		String _originalFileId = msgObj.GetMsgByName("originalFileId");
		String _originalCreateDate = msgObj.GetMsgByName("originalCreateDate");
		needClone = _originalFileId != null && !"".equals(_originalFileId.trim());
		
		needReadFile = Boolean.parseBoolean(msgObj.GetMsgByName("needReadFile"));
		
		if(needClone){
			originalFileId = new Long(_originalFileId);
			originalCreateDate = com.seeyon.v3x.util.Datetimes.parseDatetime(_originalCreateDate);
		}
	}
	
	//=============================================避免office正文多人同时修改代码开始===================================
	//用office的处理文件ID做为key保存的修改记录
	//private static Map<String,UserUpdateObject> useObjectList=new Hashtable<String,UserUpdateObject>();
	private final static CacheAccessable cacheFactory = CacheFactory.getInstance(HandWriteManager.class);
	private static CacheMap<String,UserUpdateObject> useObjectList = cacheFactory.createMap("FlowId");
	
	public static Map<String, UserUpdateObject> getUseObjectList() {
		return useObjectList.toMap();
	}
	public static void setUseObjectList(Map<String, UserUpdateObject> omap) {
		useObjectList.replaceAll(omap);
	}
	//修改对象,放入对象修改列表
	public synchronized UserUpdateObject editObjectState(String objId)
	{
		if(objId==null || "".equals(objId)){return null;}
		User user=CurrentUser.get();
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		if(os==null)
		{//无人修改
			os=new UserUpdateObject();
			try{
				V3XFile file=fileManager.getV3XFile(Long.parseLong(objId));
				if(file!=null)
				{
					os.setLastUpdateTime(file.getUpdateDate());
				}
				else
				{
					os.setLastUpdateTime(null);
					//return os;
				}
				os.setObjId(objId);			
				os.setUserId(user.getId());
				os.setUserName(user.getName());
				addUpdateObj(os);
			}catch(Exception e)
			{				
			}			
		}
		else
		{
			if(os.getUserId()==user.getId())
			{
				os.setCurEditState(false);
			}
			else
			{
				//有用户修改时，要判断用户是否在线,如果用户不在线，删除修改状态
				boolean editUserOnline=true;
				V3xOrgMember member = null; //当前office控件编辑用户
				try{
					member = orgManager.getEntityById(V3xOrgMember.class, os.getUserId());
					editUserOnline=onLineManager.isOnline(member.getLoginName());
				}
				catch(Exception e1){
					log.warn("检查文档是否被编辑，文档编辑用户不存在[" + os.getUserId() + "]", e1);					
				}
				if(editUserOnline)
				{
					os.setCurEditState(true);
				}
				else
				{
					//编辑用户已经离线，修改文档编辑人为当前用户
					os.setUserId(user.getId());
					os.setCurEditState(false);					
				}
			}						
		}
		return os;
	}
	//检查对象是否被修改
	public synchronized UserUpdateObject checkObjectState(String objId)
	{
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		if(os==null){os=new UserUpdateObject();}
		return os;
	}
	
	public  synchronized boolean deleteUpdateObj(String objId ){
		User user=CurrentUser.get();
		if(user==null) return true;
		long userId = user.getId();
		return deleteUpdateObj(objId, String.valueOf(userId));
	}
	/**
	 * 解锁
	 * @param objId  解锁对象ID ，如公文正文的ID
	 * @param userId 当前用户的ID
	 * @return
	 */
	public synchronized boolean deleteUpdateObj(String objId,String userId )
	{
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		
		if(os==null || Strings.isBlank(userId)){return true;}
		if(Long.valueOf(userId).equals(os.getUserId()))
		{
			useObjectList.remove(objId);
			//发送集群通知
//			String[] so= new String[2];
//			so[0] = objId;
//			so[1] = userId;
//			NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectRomoveOffice, so);
		}
		return true;
	}
	public synchronized boolean addUpdateObj(UserUpdateObject uo)
	{		
		useObjectList.put(uo.getObjId(),uo);	
//		发送集群通知
//		NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectAddOffice, uo);
		return true;
	}
	//=============================================避免office正文多人同时修改代码结束===================================
	
	
	private String findBackFileIds(String fn)
	{
		int ic=0;
		String ids="";		
		List<V3XFile> fs=fileManager.findByFileName("copy"+fn);
		for(V3XFile tempFile:fs)
		{
			if(ic!=0){ids+=",";}
			ic++;
			ids+=tempFile.getId();
		}
		return ids;
	}
	
	public boolean taoHong(iMsgServer2000 msgObj) throws BusinessException {
		String path=msgObj.GetMsgByName("TEMPLATE");		//取得文档编号
		//本段处理是否调用文档时打开模版，
		//还是套用模版时打开模版。
		String mCommand=msgObj.GetMsgByName("COMMAND");
		//String templetType=msgObj.GetMsgByName("TEMPLATETYPE");
		if(mCommand.equalsIgnoreCase("INSERTFILE"))
		{
			msgObj.MsgTextClear();
			if(msgObj.MsgFileLoad(path))
			{
				msgObj.SetMsgByName("STATUS","打开模板成功!");		//设置状态信息
				msgObj.MsgError("");
			}
			else
			{
				msgObj.MsgError("打开模板失败!");		//设置错误信息
			}
		}
		return true;
	}
	
	public boolean saveClientFile(iMsgServer2000 msgObj) throws Exception {
		if (createDate == null) { 
			createDate = new Date();
		}

		Long fileName = UUIDLong.absLongUUID();
		String filePath = this.fileManager.getFolder(createDate, true)
		+ File.separator + fileName;

		boolean isSuccessSave = false;

		Integer category = new Integer(msgObj.GetMsgByName("CATEGORY"));

		String tempFile = SystemEnvironment.getSystemTempFolder() + File.separator + UUIDLong.absLongUUID();
		isSuccessSave = msgObj.MsgFileSave(tempFile);
		if(!isSuccessSave){
			log.error("office上传文件保存失败(msgObj.MsgFileSave),isSuccessSave:"+isSuccessSave+",tempFile:"+tempFile);
		}
		
		CoderFactory.getInstance().encryptFile(tempFile, filePath);
		File f = new File(filePath);
		if(f != null && f.exists())
			msgObj.SetMsgByName("fileSize",f.length()+"");

		String ext = msgObj.GetMsgByName("fileExt");
		if (isSuccessSave) {
			V3XFile file = new V3XFile();
			file.setId(fileName);
			file.setCategory(category);
			file.setFilename(fileName.toString());
			file.setSize(new Long(msgObj.MsgFileSize()));

			file.setCreateDate(createDate);
			String noMillisecondTime = Datetimes.format(new Date(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss");
			file.setUpdateDate(Datetimes.parseDate(noMillisecondTime));

			User user = CurrentUser.get();
			if(user != null){
				file.setCreateMember(user.getId());
				file.setAccountId(user.getAccountId());
			}

			this.fileManager.save(file);
		}
		
		//保存二维码文件信息到bar_code_info
		String saveCodeFile = msgObj.GetMsgByName("saveCodeFile");
		String objectId = msgObj.GetMsgByName("objectId");
		if("true".equals(saveCodeFile) && Strings.isNotBlank(objectId)) {
			this.barCodeManager.saveBarCode(Long.parseLong(objectId), fileName,ext, category);
		}

		msgObj.SetMsgByName("STATUS", "保存成功!");
		msgObj.MsgError("");
		return true;
	}
}