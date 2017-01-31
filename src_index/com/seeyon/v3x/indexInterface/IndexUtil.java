package com.seeyon.v3x.indexInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.index.convert.Convertor;
import com.seeyon.v3x.index.share.datamodel.Accessory;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexManager;

public class IndexUtil {
	private final static String fileSeparator = System.getProperty("file.separator");
	
	private final static Log log = LogFactory.getLog(IndexUtil.class);
	
	public static String getFileSeparator() {
		return fileSeparator;
	}

	public static void convertToAccessory(IndexInfo info){
		try{
		AttachmentManager attachmentManager = (AttachmentManager) ApplicationContextHolder.getBean("attachmentManager");
		List<Attachment> attachments=attachmentManager.getByReference(info.getEntityID());
		List<Accessory> accessories=attToAccessory(attachments);
	    info.setAccessories(accessories);	
		}catch(Exception e){
			log.error("error when get Accessory!", e);
		}
	}
	
	public static List<Accessory> attToAccessory(List<Attachment> attachments ){
		List<Accessory> accessories=null; 
			if(attachments!=null&&attachments.size()>0){
			accessories=new ArrayList<Accessory>();
			int position = -1;
			for(int i=0;i<attachments.size();i++){
				// 关联文档
//				if(attachments.get(i).getFileUrl() == null)
//					continue;
				if(attachments.get(i).getType()==Constants.ATTACHMENT_TYPE.DOCUMENT.ordinal()||attachments.get(i).getType()== Constants.ATTACHMENT_TYPE.FormDOCUMENT.ordinal()){
					Accessory accessory=new Accessory();
					accessory.setTitle(attachments.get(i).getFilename());
					accessory.setType(IndexInfo.DOCUMENT);
					accessories.add(accessory);
					continue;
				}
				long fileUrl=attachments.get(i).getFileUrl();
				String strFileType=attachments.get(i).getMimeType();
				position = attachments.get(i).getFilename().lastIndexOf(".");
				String extName = position==-1?"":attachments.get(i).getFilename().substring(position+1);
				int fileType=Convertor.getContentType(strFileType,extName);
				
				String title=attachments.get(i).getFilename();
				java.util.Date date=attachments.get(i).getCreatedate();
		
				FileManager fileManager = (FileManager) ApplicationContextHolder.getBean("fileManager");
				String folderPath = "";
				try {
					folderPath = fileManager.getFolder(date, false);
					folderPath = folderPath.substring(folderPath.length()-11);
				} catch (BusinessException e1) {
					folderPath = "";
				}
				String attPath = folderPath + fileSeparator + attachments.get(i).getFileUrl();
				
				try {
//					File file=fileManager.getFile(fileUrl, date);
					//TODO kuanghs
					Accessory accessory=new Accessory();
					accessory.setEntityID(fileUrl);
					accessory.setTitle(title);
					accessory.setCreateDate(date);
					accessory.setType(fileType);
					accessory.setAttAreaId(getAttAreaId(date));
					accessory.setAttPath(attPath);
					accessories.add(accessory);
				} catch (Exception e) {
					log.error("error,when handle col attachment!",e);
				}
			}	
		}
			return accessories;
		
	}

	private static String getAttAreaId(java.util.Date date) {
		//附件路径
		PartitionManager partitionManager = (PartitionManager) ApplicationContextHolder
				.getBean("partitionManager");
		Partition partition = partitionManager.getPartition(date, true);
		return partition.getId().toString();
	}
	
	public static void getRMIClientProxy(IndexManager realManager) throws Exception
	{
		Properties prop =IndexPropertiesUtil.getInstance().readProperties();
		String indexAddr=getIndexAddress(prop);
		log.info("远程模式连接 " +indexAddr+ "......");
		
		RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
		proxyFactory.setServiceInterface(com.seeyon.v3x.index.share.interfaces.IndexManager.class);
		proxyFactory.setServiceUrl(getIndexAddress(prop));
		proxyFactory.setRefreshStubOnConnectFailure(true);
		proxyFactory.afterPropertiesSet();
		IndexManager object = (IndexManager) proxyFactory.getObject();
		if(object==null)
		{
			log.warn("远程全文检索服务异常... 请检查配置"+object);
			throw new Exception("远程全文检索服务异常... 请检查配置");
		}
		((ProxyManager)realManager).setRealManager(object);
	
	}
	/**
	 * 根据prop得到形如 “rmi://ip：端口/服务名” 的字符串
	 * 
	 * @param prop
	 * @return
	 */
	private static String getIndexAddress(Properties prop) {
		return "rmi://" + prop.getProperty("indexIp") + ":"
				+ prop.getProperty("indexPort") + "/"
				+ prop.getProperty("indexServiceName");
	}
}
