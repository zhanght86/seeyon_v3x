package com.seeyon.v3x.indexInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.IndexContext;
import com.seeyon.v3x.index.IndexOptimize;
import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.index.manager.IndexManagerImpl;
import com.seeyon.v3x.index.queue.IndexJob;
import com.seeyon.v3x.index.queue.JobQueue;
import com.seeyon.v3x.index.queue.ParseJob;
import com.seeyon.v3x.index.share.datamodel.AreaMappingInfo;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.indexInterface.TimeJob.IndexDBJob;
import com.seeyon.v3x.indexInterface.TimeJob.UpdateJob;
import com.seeyon.v3x.indexInterface.ProxyManager;
public class IndexLuceneInit implements SystemInitialitionInterface {
	private static final Log log = LogFactory.getLog(IndexLuceneInit.class);
	private static Properties prop = null;
	/**
	 * 初始化类完成以下四件任务: 
	 * 1.确定是否信息入库（否定的话就不用入库） 
	 * 2.确定是远程接口还是本地接口
	 * 3.确定更新索引的时间，并启动两个线程（i）完成解析文档任务的线程（ii）完成更新索引任务的线程 
	 * 4.它还应负责索引的建立，初始化
	 */
	public void initialized(ServletContextEvent arg0) {
		// 如果插件没有启动，则不启动后台线程
		if (!IndexInitConfig.hasLuncenePlugIn())
			return;
		// 从配置文件刷新参数
		if (prop == null) {
			prop = refreshArgsByProp();
		}
		//调用本地服务入队列
		IndexManagerImpl manager = new IndexManagerImpl();
		manager.setIndexContext(getIndexContext());
		manager.setJobQueue(getIndexContext().getJobQueue());
		getIndexManager().setRealManager(manager);
		// 根据配置参数 启动远程模式 或 本地模式
		if ("remote".equals(prop.getProperty("modelName"))) 
		{
			try{
				// 启动远程模式 ----- 连接远程全文检索服务器,通过Rmi设置远程服务器，并使远程服务器可以回连本机。
				ProxyManager proxyMgr=getIndexManager();
				connectRemoteIndexServer(proxyMgr);
				// 同步所有分区信息
				List<Partition> partitionList = getPartitionManager().getAllPartitions(); // 区全部分区信息
				ArrayList<AreaMappingInfo> areaMappingInfoList = new ArrayList<AreaMappingInfo>();
				for (Partition partition : partitionList) {
					AreaMappingInfo areaMappingInfo = new AreaMappingInfo();
					areaMappingInfo.setAreaId(partition.getId().toString());
					areaMappingInfo.setMappingPath("");
					areaMappingInfo.setSharePath(partition.getSharePath());
					areaMappingInfo.setShareUsername(""); //TODO 为以后自动映射预留参数
					areaMappingInfo.setSharePassword(""); //TODO 为以后自动映射预留参数
					areaMappingInfoList.add(areaMappingInfo);
				}
				proxyMgr.syncAttAreasInfo(areaMappingInfoList);
				
				// 同步全文检索配置信息
				proxyMgr.setA8Info(prop.getProperty("a8Ip"), prop
						.getProperty("indexParseTimeSlice"), prop.getProperty("indexUpdateTimeSlice"),
						prop.getProperty("indexPort"), prop
						.getProperty("indexServiceName"));
			}catch (Exception e) {
				System.out.println("\n----------远程模式全文检索初始化错误！请检查是否配置正确。");
				System.out.println("----------如果远程全文检索服务器未配置，请在全文检索服务器端配置相关配置文件,或者在A8服务器端用系统管理员登陆进行全文检索配置。\n");
				log.error("远程模式全文检索初始化错误",e);
			}
		} 
		else 
		{
			// 启动本地模式
			if(NotificationManager.getInstance().isEnabled()){
				// 启用集群时全文检索必须使用远程模式
				log.warn("全文检索配置错误：启用集群时全文检索必须使用远程模式！");
			}
			try{
				// 本地模式在此初始化
				log.info("启动全文检索本地模式 ");
				localIndexInit(getIndexContext().getJobQueue(), getIndexManager(),
						getParseTimeSlice(prop), getUpdateTimeSlice(prop));
			}catch (Exception e) {
				System.out.println("\n----------本地模式全文检索初始化错误！请检查是否配置正确。");
				System.out.println("----------如果本地全文检索服务器未配置，请配置相关配置文件。\n");
				log.error("本地模式全文检索初始化错误",e);
			}
		}
		getIndexManager().setLocalIndexManager(manager);
		startParseJob(getIndexManager(),getParseTimeSlice(prop),prop.getProperty("modelName"));
		startUpdateJob(getIndexManager(),getUpdateTimeSlice(prop),prop.getProperty("modelName"));
//		getUpdateIndexManager().resumeDBIndexInfo();
		Thread thread4 = new Thread(new IndexDBJob(getUpdateIndexManager(),Integer.parseInt(prop.getProperty("indexUpdateTimeSlice"))));
		thread4.setName("Index-DBJob");
		thread4.start();
	}
	/**
	 * 远程模式 ----- 连接远程全文检索服务器,通过Rmi设置远程服务器，并使远程服务器可以回连本机。
	 * 
	 * @param indexAddress
	 * @param refreshStubOnConnectFailure
	 * @param proxyManager
	 * @param updateContext
	 * @param indexUpdateTimeSlice
	 * @throws Exception
	 */
	private void connectRemoteIndexServer(ProxyManager proxyManager)
			throws Exception {
		log.info("启动全文检索远程模式：" + "......");
		// 取远程Rmi对象
		IndexUtil.getRMIClientProxy(proxyManager);
	}
	/**
	 * 全文检索 本地模式
	 * 
	 * @param indexContext
	 * @param jobQueue
	 * @param indexManager
	 * @param indexParseTimeSlice
	 * @param indexUpdateTimeSlice
	 * @throws Exception
	 */
	
	private void localIndexInit(JobQueue jobQueue,
			ProxyManager indexManager, Integer indexParseTimeSlice,
			Integer indexUpdateTimeSlice)
			throws Exception {
		if (indexUpdateTimeSlice == null) {
			indexUpdateTimeSlice = getUpdateTimeSlice(refreshArgsByProp());
		}
		// init handle thread
		IndexJob job2 = new IndexJob(indexUpdateTimeSlice,getIndexContext());
		Thread thread2 = new Thread(job2);
		thread2.setDaemon(true);
		thread2.setName("Index-Job");
		thread2.start();
//		Runtime.getRuntime().addShutdownHook(new Thread("Index-ShutdownIndexThread"){
//			public void run(){
//				IndexOptimize.indexOptimize();
//				log.info("shut down......."+"ShutdownIndexThread");
//			}
//		}
//		);
		flushTimer();
	}
	/**
	 * 开启更新线程
	 * @param indexManager
	 * @param indexUpdateTimeSlice
	 */
	private void startUpdateJob(ProxyManager indexManager,
			Integer indexUpdateTimeSlice,String jobName) {
		UpdateJob job3 = new UpdateJob(indexUpdateTimeSlice, indexManager);
		Thread thread3 = new Thread(job3);
		thread3.setDaemon(true);
		thread3.setName("Index-UpdateJob"+"-"+jobName);
		thread3.start();
	}
	private void startParseJob(ProxyManager indexManager,
			Integer indexParseTimeSlice,String jobName) {
		ParseJob job1 = new ParseJob(indexParseTimeSlice,
				getIndexContext().getJobQueue(), indexManager);
		Thread thread1 = new Thread(job1);
		thread1.setDaemon(true);
		thread1.setName("Index-ParseJob"+"-"+jobName);
		thread1.start();
	}
	public void destroyed(ServletContextEvent arg0) {
//		if(!"remote".equals(prop.getProperty("modelName")))
//		{
//			IndexOptimize.indexOptimize();
//			log.info("退出OA前优化索引... OK");
//		}
		
	}
	
	  private void flushTimer()
	    {
			 Timer timer = new Timer();
			 int i=22;
			 int j=00;
			 int t=24;
			 try{
				  i=Integer.parseInt(prop.getProperty("indexFlush.daystart").split(":")[0]);
				  j=Integer.parseInt(prop.getProperty("indexFlush.daystart").split(":")[1]);
				  t=Integer.parseInt(prop.getProperty("indexFlush.interval"));
				  log.info("全文检索定时入库每天执行: "+i+":"+j+" 时间间隔: "+t);
			 }catch(Exception e)
			 {
				  i=22;
				  j=00;
				  t=24;
				 log.error("全文检索定时入库参数设置出错:",e);
			 }
			 Calendar localCalendar = Calendar.getInstance();
			localCalendar.setTimeInMillis(System.currentTimeMillis());
			localCalendar.set(Calendar.HOUR_OF_DAY, i);
			localCalendar.set(Calendar.MINUTE, j);
			try{
				timer.scheduleAtFixedRate(new TimerTask(){
					@Override
					public void run() {
						IndexOptimize.indexOptimize();
						log.info("全文检索定时入库完成");
					}}, localCalendar.getTime(), t*60*60*1000L);
			}catch(Exception e)
			{
				 log.error("全文检索定时入库定时器启动出错:",e);
			}
	    }
	
	/**
	 * 返回和刷新配置文件
	 */
	private Properties refreshArgsByProp() {
		return IndexPropertiesUtil.getInstance().readProperties();
	}
	

	
	/**
	 * 根据prop得到整型参数indexUpdateTimeSlice，默认值为3
	 * 
	 * @param prop
	 * @return
	 */
//	private Integer getUpdateTimeSlice() {
//		return propertyToIntege(prop, "indexUpdateTimeSlice", 3);
//	}
	
	/**
	 * 从配置文件中的参数转换成Integer，处理异常，设定默认值。
	 * @param prop
	 * @param key
	 * @param DefaultValue
	 * @return
	 */
	private Integer propertyToIntege(Properties prop, String key,
			Integer DefaultValue) {
		if (prop == null) {
			prop = refreshArgsByProp();
		}
		String value_str = prop.getProperty(key);
		Integer value_Integered = DefaultValue; // 设定默认值
		if (value_str != null) {
			try {
				value_Integered = Integer.parseInt(value_str.trim());
			} catch (Exception e) {
				log.error(e + "\n全文检索配置文件参数" + key + "有误，当前设置为默认值"
						+ DefaultValue);
			}
			return value_Integered;
		} else {
			return value_Integered;
		}
	}
	
	/**
	 * 根据prop得到整型参数indexParseTimeSlice，默认值为6000
	 * 
	 * @param prop
	 * @return
	 */
	private Integer getParseTimeSlice(Properties prop) {
		return propertyToIntege(prop, "indexParseTimeSlice", 6000);
	}
	
	/**
	 * 根据prop得到整型参数indexUpdateTimeSlice，默认值为3
	 * 
	 * @param prop
	 * @return
	 */
	private Integer getUpdateTimeSlice(Properties prop) {
		return propertyToIntege(prop, "indexUpdateTimeSlice", 3);
	}
	public ProxyManager getIndexManager() {
		return (ProxyManager)ApplicationContextHolder.getBean("indexManager");
	}

	public PartitionManager getPartitionManager() {
		return (PartitionManager)ApplicationContextHolder.getBean("partitionManager");
	}

	public IndexContext getIndexContext() {
		return (IndexContext)ApplicationContextHolder.getBean("indexContext");
		
	}
	private UpdateIndexManager getUpdateIndexManager() {
		return (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
		
	}
}
