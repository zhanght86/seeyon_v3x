package com.seeyon.v3x.common.office;

import DBstep.iMsgServer2000;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.signet.dao.*;
import com.seeyon.v3x.system.signet.domain.*;
import com.seeyon.v3x.util.Datetimes;
import java.sql.Timestamp;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;

public class HtmlHandWriteManager {
	
	private static String rc="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	
	//private static Map<String,UserUpdateObject> useObjectList=new Hashtable<String,UserUpdateObject>();
	private final static CacheAccessable cacheFactory = CacheFactory.getInstance(HtmlHandWriteManager.class);
	private static CacheMap<String,UserUpdateObject> useObjectList = cacheFactory.createMap("Flow");
	
	V3xHtmDocumentSignatureDao htmlSignDao;
	HtmlSignatureHistoryDao signHistoryDao;
	
	private static Log log = LogFactory.getLog(HtmlHandWriteManager.class);
	private static OnLineManager onLineManager;
	private static OrgManager orgManager;
	
	
	private synchronized void init() {
		if(onLineManager == null){
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			onLineManager = (OnLineManager)ApplicationContextHolder.getBean("onLineManager");
		}
	}
	
	public HtmlHandWriteManager()
	{
		init();		
	}
	
	public V3xHtmDocumentSignatureDao getHtmlSignDao()
	{
		return this.htmlSignDao;
	}
	
	public HtmlSignatureHistoryDao getSignHistoryDao()
	{
		return this.signHistoryDao;
	}
	
	public void setSignHistoryDao(HtmlSignatureHistoryDao signHistoryDao)
	{
		this.signHistoryDao=signHistoryDao;
	}
	
	public void setHtmlSignDao(V3xHtmDocumentSignatureDao htmlSignDao)
	{
		this.htmlSignDao=htmlSignDao;
	}
	
	public boolean loadDocumentSinature(iMsgServer2000 msgObj) throws BusinessException 
	{
		
		List <V3xHtmDocumentSignature> dsList=null;
		V3xHtmDocumentSignature ds=new V3xHtmDocumentSignature();
        ds.setSummaryId(Long.parseLong(msgObj.GetMsgByName("RECORDID")));//取得文档编号
        ds.setFieldName(msgObj.GetMsgByName("FIELDNAME"));//取得签章字段名称
        ds.setUserName(msgObj.GetMsgByName("USERNAME"));//取得用户名称
        msgObj.MsgTextClear();                                //清除SetMsgByName设置的值        
        dsList=htmlSignDao.findByIdAndPolicy(ds.getSummaryId(),ds.getFieldName());
        if(dsList!=null && dsList.size()>0)
        {
        	ds=dsList.get(0);
        	msgObj.SetMsgByName("FIELDVALUE",ds.getFieldValue());  	//设置签章数据
        	msgObj.SetMsgByName("STATUS","调入成功!");  	//设置状态信息
        	msgObj.MsgError("");				//清除错误信息
        }
        else
        {
        	msgObj.MsgError("load err!");		        //设置错误信息
        }        
		return true;
	}
	
	public boolean saveSignatureHistory(iMsgServer2000 msgObj) throws BusinessException {
		V3xHtmlSignatureHistory sh=new V3xHtmlSignatureHistory();
		sh.setIdIfNew();
		sh.setSummaryId(Long.parseLong(msgObj.GetMsgByName("RECORDID")));//取得文档编号
		sh.setFieldName(msgObj.GetMsgByName("FIELDNAME"));//取得签章字段名称
		sh.setMarkName(msgObj.GetMsgByName("MARKNAME"));//取得签章名称
		sh.setUserName(msgObj.GetMsgByName("USERNAME"));		//取得用户名称
		sh.setDateTime(new Timestamp(System.currentTimeMillis()));		//取得签章日期时间
		sh.setHostName(msgObj.GetMsgByName("CLIENTIP"));		        //取得客户端IP
		sh.setMarkGuid(msgObj.GetMsgByName("MARKGUID"));	        //取得序列号
		msgObj.MsgTextClear();                                //清除SetMsgByName设置的值
		
		try{
		  signHistoryDao.save(sh); //保存印章历史信息
		}catch(Exception e)
		{
			msgObj.MsgError("saveerr!");		//设置错误信息
			return false;
		}
        
		msgObj.SetMsgByName("MARKNAME",sh.getMarkName());		//将签章名称列表打包
        msgObj.SetMsgByName("USERNAME",sh.getUserName());		//将用户名列表打包
        msgObj.SetMsgByName("DATETIME",Datetimes.formatDatetime(sh.getDateTime()));		//将签章日期列表打包
        msgObj.SetMsgByName("HOSTNAME",sh.getHostName());		//将客户端IP列表打包
        msgObj.SetMsgByName("MARKGUID",sh.getMarkGuid());		//将序列号列表打包
        msgObj.SetMsgByName("STATUS","save ok!");  //设置状态信息
        msgObj.MsgError("");				//清除错误信息        
		return true;
	}
	
	public boolean getSignatureHistory(iMsgServer2000 msgObj) throws BusinessException {
		
		V3xHtmlSignatureHistory dh=new V3xHtmlSignatureHistory();

        dh.setSummaryId(Long.parseLong(msgObj.GetMsgByName("RECORDID")));		//取得文档编号
        dh.setFieldName(msgObj.GetMsgByName("FIELDNAME"));		//取得签章字段名称
        dh.setUserName(msgObj.GetMsgByName("USERNAME"));		//取得用户名
        msgObj.MsgTextClear();                         //清除SetMsgByName设置的值
        
        dh=combStr(signHistoryDao.findByIdAndPolicy(dh.getSummaryId(),dh.getFieldName()));        

        if (dh!=null) 		        	        //调入印章历史信息
        {
          msgObj.SetMsgByName("MARKNAME",dh.getMarkName());		//将签章名称列表打包
          msgObj.SetMsgByName("USERNAME",dh.getUserName());		//将用户名列表打包
          msgObj.SetMsgByName("DATETIME",dh.getDateTimeStr());		//将签章日期列表打包
          msgObj.SetMsgByName("HOSTNAME",dh.getHostName());		//将客户端IP列表打包
          msgObj.SetMsgByName("MARKGUID",dh.getMarkGuid());		//将序列号列表打包
          msgObj.SetMsgByName("STATUS","load ok");   //设置状态信息
          msgObj.MsgError("");				//清除错误信息
        }else{
        	msgObj.SetMsgByName("STATUS","load false");	//设置状态信息
        	msgObj.MsgError("load fale");		//设置错误信息
        }  
		return true;
	}
	
	  /**
	   * 查询到的签章记录转变成控件要求格式
	   * @param ls
	   * @return
	   */
	  private V3xHtmlSignatureHistory combStr(List <V3xHtmlSignatureHistory>ls)
	  {
		V3xHtmlSignatureHistory temp,dh=new V3xHtmlSignatureHistory();
	    dh.setMarkName(ResourceBundleUtil.getString(rc,"ocx.signname.label")+"\r\n");
	    dh.setUserName(ResourceBundleUtil.getString(rc,"ocx.signuser.label")+"\r\n");
	    dh.setHostName(ResourceBundleUtil.getString(rc,"ocx.clientip.label")+"\r\n");
	    dh.setDateTimeStr(ResourceBundleUtil.getString(rc,"ocx.signtime.label")+"\r\n");
	    dh.setMarkGuid(ResourceBundleUtil.getString(rc,"ocx.serialnumber.label")+"\r\n");
	    int i,len=ls.size();

	    for(i=0;i<len;i++)
	    {
	      temp=ls.get(i);
	      dh.setMarkName(dh.getMarkName()+temp.getMarkName()+"\r\n");
	      dh.setUserName(dh.getUserName()+temp.getUserName()+"\r\n");
	      dh.setHostName(dh.getHostName()+temp.getHostName()+"\r\n");
	      dh.setDateTimeStr(dh.getDateTimeStr()+Datetimes.formatDatetime(temp.getDateTime())+"\r\n");
	      dh.setMarkGuid(dh.getMarkGuid()+temp.getMarkGuid()+"\r\n");
	    }
	    return dh;
	  }
	
	public boolean saveSignature(iMsgServer2000 msgObj) throws BusinessException {
		V3xHtmDocumentSignature hd=new V3xHtmDocumentSignature();
		String clientVer=msgObj.GetMsgByName("Version");
        clientVer=clientVer.replace('.',',');
        if(msgObj.Version().equals(clientVer))
        {
          msgObj.MsgError("ver err");
          msgObj.MsgTextClear();
          msgObj.MsgFileClear();          
          return false;
        }
        boolean isUpdate=false;
        Long summaryId=Long.parseLong(msgObj.GetMsgByName("RECORDID"));
        String policy=msgObj.GetMsgByName("FIELDNAME");
        List <V3xHtmDocumentSignature> hsList=htmlSignDao.findByIdAndPolicy(summaryId, policy);
        if(hsList!=null && hsList.size()>0)
        {
        	hd=hsList.get(0);
        	isUpdate=true;
        }
        hd.setIdIfNew();
        hd.setSummaryId(summaryId);//取得文档编号
        hd.setFieldName(policy);//取得签章字段名称
        hd.setFieldValue(msgObj.GetMsgByName("FIELDVALUE"));//取得签章数据内容
        hd.setUserName(msgObj.GetMsgByName("USERNAME"));//取得用户名称
        hd.setDateTime(new Timestamp(System.currentTimeMillis()));//取得签章日期时间
        hd.setHostName(msgObj.GetMsgByName("CLIENTIP"));//取得客户端IP
        try{
        	if(isUpdate){htmlSignDao.update(hd);}
        	else{htmlSignDao.save(hd);}
        }catch(Exception e)
        {
			log.error(e.getMessage(), e);
        }
		return true;
	}
	//读取文单签章，转换成js
	public String getHandWritesJs(Long summaryId,String userName,List<String>opinionNames)
	{		
        StringBuffer sb = new StringBuffer("<Script language='JavaScript'>");
        int i, len;
        List <V3xHtmDocumentSignature>ls = htmlSignDao.findBy("summaryId",summaryId);
        V3xHtmDocumentSignature ds = null;
        len = ls.size();    
        
        sb.append("hwObjs=new Array();\r\n");
        for (i = 0; i < len; i++)
        {
            ds = ls.get(i);
            sb.append("hwObjs[").append(i).append("]=new hwObj('").append(summaryId).append("','").append(ds.getFieldName()).append("','").append(userName).append("','").append(ds.getDateTime().getTime()).append("');\r\n");
        }
        sb.append("</Script>\r\n");
        //添加控件菜单响应时间
        /*
        for (i = 0; i < len; i++)
        {
            ds = ls.get(i);
            sb.append(getHandWriteEventJs(ds.getFieldName()));
        }
        */
        if(opinionNames.contains("otherOpinion")==false){opinionNames.add("otherOpinion");}
        for (i = 0; i < opinionNames.size(); i++)
        {            
            sb.append(getHandWriteEventJs("hw"+opinionNames.get(i)));
        }
        return sb.toString();
	}
	
	/**
	 * 根据 summmaryId 得到回复该公文签章
	 * @param summaryId
	 * @return
	 */
	public List <V3xHtmDocumentSignature> getHandWrites(Long summaryId){
		if(summaryId!=null){
			return htmlSignDao.findBy("summaryId",summaryId);
		}else{
			return null;
		}
	}
	private String getHandWriteEventJs(String hwName)
    {
        StringBuffer hjen = new StringBuffer();
        hjen.append("<SCRIPT language=javascript for='").append(hwName).append(
            "' event=OnMenuClick(vIndex,vCaption)>\r\n");
        hjen.append("OnMenuHdClick(this,vIndex,vCaption);\r\n");
        hjen.append("</SCRIPT>\r\n");
        return hjen.toString();
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
				String []temp=objId.split("___");
				List <V3xHtmDocumentSignature> dsList=htmlSignDao.findByIdAndPolicy(Long.parseLong(temp[0]),temp[1]);
		        if(dsList!=null && dsList.size()>0)
		        {
		        	os.setLastUpdateTime(dsList.get(0).getDateTime());
		        }
		        else
		        {
		        	os.setLastUpdateTime(null);
		        	os.setCurEditState(false);
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
//			有用户修改时，要判断用户是否在线,如果用户不在线，删除修改状态
			boolean editUserOnline=true;
			V3xOrgMember member = null; //当前office控件编辑用户
			try{
				member = orgManager.getEntityById(V3xOrgMember.class, os.getUserId());
				editUserOnline=onLineManager.isOnline(member.getLoginName());
			}
			catch(Exception e1){
				log.warn("检查文档是否被编辑，文档编辑用户不存在[" + os.getUserId() + "]", e1);					
			}
			if(editUserOnline && os.getUserId()!=user.getId())
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
	public synchronized boolean deleteUpdateObj(String objId,Long userId){
		
		UserUpdateObject os=null;
		os=useObjectList.get(objId);
		if(os==null || userId==null){return true;}
		if(userId.equals(os.getUserId()))
		{
			useObjectList.remove(objId);
			//发送集群通知
			//NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectRomoveHtml, new Object[]{objId,userId});
		}
		return true;
	}
	public synchronized boolean deleteUpdateObj(String objId)
	{
		User user=CurrentUser.get();
		if(user == null) return true;
		Long userId = user.getId();
		return deleteUpdateObj(objId,userId);
	}
	public synchronized boolean addUpdateObj(UserUpdateObject uo)
	{		
		useObjectList.put(uo.getObjId(),uo);		
//		发送集群通知
		//NotificationManager.getInstance().send(NotificationType.EdocUserOfficeObjectAddHtml, uo);
		return true;
	}

	public static Map<String, UserUpdateObject> getUseObjectList() {
		return useObjectList.toMap();
	}

	public static void setUseObjectList(Map<String, UserUpdateObject> uo) {
		useObjectList.replaceAll(uo);
	}

}
