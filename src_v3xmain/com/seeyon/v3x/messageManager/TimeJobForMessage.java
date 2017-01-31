package com.seeyon.v3x.messageManager;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.RunInRightEvent;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.dao.HibernateQueryPlanCacheUtil;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.messageManager.domain.MessageDelset;
import com.seeyon.v3x.messageManager.manager.MessageDelsetManager;
import com.seeyon.v3x.util.Strings;

public class TimeJobForMessage extends RunInRightEvent implements QuartzJob,Job{
	private static final Log log = LogFactory.getLog(TimeJobForMessage.class);
	
	@Deprecated
	public void execute(Map<String, String> parameters) {
	}
	
	@Deprecated
	public void execute(JobExecutionContext datamap) throws JobExecutionException {
	}
	
	public void run(){
		long startTime = System.currentTimeMillis();
		
		gc();

		log.info("自动清理清理历史开始");
		
		MessageDelsetManager messageDelsetManager = (MessageDelsetManager)ApplicationContextHolder.getBean("messageDelsetManager");
		UserMessageManager userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
		try{
			//自动清理
			MessageDelset messageDelset = messageDelsetManager.getMessageDelset();
			
			int status = messageDelset.getStatus();
			if(Constant.Message_DELSET.values()[status].equals(Constant.Message_DELSET.DAY)){
				userMessageManager.removeMessage("day",messageDelset.getMessageDay());
			}
			else if(Constant.Message_DELSET.values()[status].equals(Constant.Message_DELSET.COUNT)){				
				userMessageManager.removeMessage("count",messageDelset.getMessageCount());
			}
			else if(Constant.Message_DELSET.values()[status].equals(Constant.Message_DELSET.ALL)){
				userMessageManager.removeMessage("day",messageDelset.getMessageDay());
				userMessageManager.removeMessage("count",messageDelset.getMessageCount());
			}	
		}
		catch(Exception e1){
			log.error("", e1);
		}
		
		try {
			userMessageManager.initMessageState();
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		log.info("自动清理清理历史结束. 耗时：" + (System.currentTimeMillis() - startTime) + " MS");
		
		try {
			clearTemporary();
		}
		catch (Throwable e) {
			log.error("", e);
		}
		
		gc();
		
		HibernateQueryPlanCacheUtil.clearInitQueryPlanCache();
	}
	
	private static void clearTemporary(){
		final File tempFolder = new File(SystemEnvironment.getSystemTempFolder());
		log.info("开始清理临时文件：" + tempFolder);
		
		if(!tempFolder.exists() || !tempFolder.isDirectory()){
			return;
		}
		
		clearTemporaryFile(tempFolder);
	}
	
	private static void clearTemporaryFile(File folder){
		File[] files = folder.listFiles();
		if(files == null || files.length == 0){
			return;
		}
		
		int c = 0;
		
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			
			if(file.isFile()){
				if(System.currentTimeMillis() - file.lastModified() > 1 * 3600 * 1000){
					try{
						file.delete();
						c++;
					}
					catch (Exception e) {
					}
				}
			}
			else if(file.isDirectory()){
				clearTemporaryFile(file);
			}
		}
		
		log.info("清理临时文件：" + c + " 个");
	}
	
	private static void gc(){
		try {
			//TODO 要移走
			Runtime rt = Runtime.getRuntime();
			long currentUsedMemory0 = rt.totalMemory() - rt.freeMemory();
			System.gc();
			long currentUsedMemory1 = rt.totalMemory() - rt.freeMemory();
			
			log.info("System.gc() : " + Strings.formatFileSize(currentUsedMemory0 - currentUsedMemory1, false));
		}
		catch (Throwable e) {
		}
	}

}
