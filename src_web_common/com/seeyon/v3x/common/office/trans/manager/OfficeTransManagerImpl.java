package com.seeyon.v3x.common.office.trans.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.event.AttachmentSaveEvent;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.utils.RMIUtil;
import com.seeyon.v3x.office.rmi.OfficeTransService;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class OfficeTransManagerImpl implements OfficeTransManager {
	private static final String ERR_MSG_DECRYPT_ERROR = "解密文件出错。";
	private static final String ERR_MSG_GENERATE_ERROR = "转换文件出错。";
	private static final String ERR_MSG_OFFICE_IS_NOT_EXIST = "转换文件不存在：";
	private final static Log log = LogFactory.getLog(OfficeTransManagerImpl.class);
	
	private FileManager fileManager;
	// 转换文件输出位置
	private String outputPath;
	// 清理时保留文件的数量
	private int retainDay = 30;
	// 服务的IP和端口
	private String host = "127.0.0.1";
	
	private int port = 1097;
	
	//文件的大小限制
	private long fileMaxSize = 5242880;
		
	private File outputTempDir;
	// 服务名称
	private final static String serviceName = "officeTransService";
	
	/**
	 * 待转换的文档队列 Object: {v3xfile.Id，v3xfile.createDate}
	 */
	private final List<Long[]> firstFileQueue = new ArrayList<Long[]>();
	private final List<Long[]> lastFileQueue = new ArrayList<Long[]>();
	
	private final int queueMaxNumber = 10000;
	
	private Object lock = new Object();
	
	private GenerateThread generateThread;
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setRetainDay(int retainDay) {
		this.retainDay = retainDay;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setFileMaxSize(long fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}

	public void init(){
		outputTempDir = new File(outputPath + File.separator + "Temp");
		
		generateThread = new GenerateThread();
		generateThread.start();
	}
	
	public void destroy(){
		generateThread.stopThread();
	}
	
	public void generate(long fileId, Date v3xFileCreateDate, boolean isImmediately){
		Long[] o = new Long[]{fileId, v3xFileCreateDate.getTime()};
		synchronized (lock) {
			if(isImmediately){
				if(this.firstFileQueue.size() < this.queueMaxNumber){
					this.firstFileQueue.add(o);
				}
			}
			else{
				if(this.lastFileQueue.size() < this.queueMaxNumber){
					this.lastFileQueue.add(o);
				}
			}
		}
	}
	
	private Object[] getNext(){
		if(this.firstFileQueue.isEmpty() && this.lastFileQueue.isEmpty()){
			return null;
		}
		
		List<Long[]> f = null;
		List<Long[]> l = null;
		
		synchronized (lock) {
			if(!this.firstFileQueue.isEmpty()){
				f = new ArrayList<Long[]>(this.firstFileQueue);
				this.firstFileQueue.clear();
			}
			
			if(!this.lastFileQueue.isEmpty()){
				l = new ArrayList<Long[]>(this.lastFileQueue);
				this.lastFileQueue.clear();
			}
		}
		
		return new Object[]{f, l};
	}
	
	private final void doGenerate(List<Long[]> f, List<Long[]> l) throws BusinessException {
		try {
			OfficeTransService service = getService();
			if(service == null)
				return;
			
			List<String[]> firstOfficeURL = null;
			if(f != null && !f.isEmpty()){
				firstOfficeURL = new ArrayList<String[]>(f.size());
				for (Long[] o : f) {
					String filepath = getFilePath(o[0]);
					if(Strings.isNotBlank(filepath)){
						firstOfficeURL.add(new String[]{filepath, makeOutputFile(o[0], o[1])});
					}
				}
			}
			
			List<String[]> lastOfficeURL = null;
			if(l != null && !l.isEmpty()){
				lastOfficeURL = new ArrayList<String[]>(l.size());
				for (Long[] o : l) {
					String filepath = getFilePath(o[0]);
					if(Strings.isNotBlank(filepath)){
						lastOfficeURL.add(new String[]{filepath, makeOutputFile(o[0], o[1])});
					}
				}
			}
			
			service.officeToHtml(firstOfficeURL, lastOfficeURL);
			
			return;
		}
		catch (Throwable e) {
			log.error(ERR_MSG_GENERATE_ERROR, e);
		}
	}
	
	/**
	 * 
	 * @param fileId
	 * @param v3xFileCreateDate
	 * @return
	 */
	private String makeOutputFile(long v3xFileId, long v3xFileCreateDate){
		Date d = new Date(v3xFileCreateDate);
		return this.outputPath + File.separator + Datetimes.format(d, "yyyyMMdd") + File.separator + v3xFileId + File.separator + v3xFileId + ".html";
	}
		
	private String getFilePath(long id) throws Exception {
		V3XFile file = this.fileManager.getV3XFile(id);
		if(file == null){
			return null;
		}
		// 解密
		String newfilePath = decrypt(file);
		
		return newfilePath;
	}

	private String decrypt(V3XFile file) {
		long id = file.getId();
		String newfilePath = null;
		try {
			File f = this.fileManager.getFile(id, file.getCreateDate());
			if(f == null){
				return null;
			}
			
			boolean isJinge = "msoffice".equals(file.getMimeType());
			if (isJinge) {
				File officeFile = fileManager.getStandardOffice(id, file.getCreateDate());
				newfilePath = officeFile.getAbsolutePath();
			}
			else {
				newfilePath = CoderFactory.getInstance().decryptFileToTemp(f.getAbsolutePath());
			}
		}
		catch (Exception e1) {
			log.error(ERR_MSG_DECRYPT_ERROR, e1);
		}
		
		if(Strings.isNotBlank(newfilePath) && !"127.0.0.1".equals(host)){
			try {
				File srcFile = new File(newfilePath);
				File tempFile = new File(outputTempDir, srcFile.getName());
				FileUtils.copyFile(srcFile, tempFile);
				return tempFile.getPath();
			}
			catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		
		return newfilePath;
	}

	private OfficeTransService getService() {
		Class<OfficeTransService> serviceInterface = OfficeTransService.class;
		OfficeTransService service;
		try {
			// Spring自身做了缓存，此处不必缓存
			service = RMIUtil.getProxy(host, port, serviceName, serviceInterface);
		}
		catch (Throwable e) {
			final String msg = "访问文件转换服务出错：" + host + ":" + port + "/" + serviceName;
			log.error(msg, e);
			return null;
		}
		return service;
	}

//	private void addRecord(long id) {
//		remove(id);
//		OfficeTransRecord record = new OfficeTransRecord(id);
//		officeTransRecordDao.add(record);
//	}

//	private void remove(long id) {
//		officeTransRecordDao.remove(id);
//	}

	public void visit(long id) {
		//officeTransRecordDao.visit(id);
	}

	public void clean() {
		Date date = Datetimes.addDate(new Date(), -retainDay);
		String dateString = Datetimes.format(date, "yyyyMMdd");
		int d = Integer.parseInt(dateString);
		
		File outputDir = new File(this.outputPath);
		File[] dayDirs = outputDir.listFiles();
		if(dayDirs != null){
			for (File dayDir : dayDirs) {
				try {
					if(dayDir.isDirectory() && dayDir.getName().length() == 8){
						int f = Integer.parseInt(dayDir.getName());
						if(f < d){
							FileUtils.deleteDirectory(dayDir);
						}
					}
				}
				catch (Throwable e) {
					log.error("", e);
				}
			}
		}
		
		try {
			FileUtils.cleanDirectory(outputTempDir);
		}
		catch (Exception e) {
		}
	}

	@Override
	public void clean(long id, String date) {
		try {
			final File file = new File(outputPath + File.separator + date + File.separator + id);
			if(file.exists()){
				FileUtils.deleteDirectory(file);
			}
		} catch (Throwable e) {
			log.error("", e);
		}
	}	
	/**
	 * 附件保存监听。
	 * 
	 * @param event
	 */
	@ListenEvent(event = AttachmentSaveEvent.class)
	public void onAttachmentSave(AttachmentSaveEvent event) {
		Attachment attachment = event.getAttachment();
		
		if (OfficeTransHelper.allowTrans(attachment)) {
			try {
				generate(attachment.getFileUrl(), attachment.getCreatedate(), false);
			}
			catch (Exception e) {
				log.error("附件保存时生成HTML出错。", e);
			}
		}
	}

	class GenerateThread extends Thread {
		private boolean running = true;
		
		private long lastWorkTimestamp;
		
		public synchronized void start() {
			super.setName("OfficeTrans");
			super.start();
		}

		public void stopThread(){
			this.running = false;
			try {
				this.interrupt();
			}
			catch (Throwable e) {
			}
		}
		
		public void run() {
			while (this.running) {
				try {
					Object[] o = getNext();
					if (o != null) {
						doGenerate((List<Long[]>)o[0], (List<Long[]>)o[1]);
					}
				}
				catch (Throwable e) {
					if(log.isDebugEnabled()){
						log.error("", e);
					}
					log.error(e.getMessage()); //默认不记堆栈
				}
				
				try {
					Thread.sleep(2000L);
				}
				catch (Throwable e) {
				}
			}
		}
	}

	public String buildSourceDownloadUrl(long id) {
		StringBuffer url = new StringBuffer();
		V3XFile f;
		try {
			f = this.fileManager.getV3XFile(id);
			if (f != null) {
				url.append("/seeyon/fileUpload.do?method=download");
				url.append("&fileId=").append(id);
				url.append("&createDate=").append(Datetimes.formatDate(f.getCreateDate()));
				url.append("&viewMode=download");
				url.append("&filename=").append(Strings.escapeJavascript(f.getFilename()));

			}
		}
		catch (BusinessException e) {
			log.error(e);
		}
		return url.toString();
	}

	public boolean isExist(long id, String date) {
		return new File(outputPath + File.separator + date + File.separator + id + File.separator + "OK").exists();
	}

	public String getOutputPath() {
		return outputPath;
	}

	public long getFileMaxSize() {
		return fileMaxSize;
	}
}
