package com.seeyon.v3x.indexresume.manager;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.IndexOptimize;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.indexInterface.ProxyManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.indexresume.dao.IndexResumeDao;
import com.seeyon.v3x.indexresume.domain.IndexResumeInfo;
import com.seeyon.v3x.indexresume.domain.IndexResumeInfo.resumeInfo;
import com.seeyon.v3x.indexresume.util.IndexResumeConstants;
import com.seeyon.v3x.util.Datetimes;

/**
 * @author zhangyong
 * @since V3.20 恢复索引业务操作
 */
public class IndexResumeManagerImp implements IndexResumeManager {
	private final static Log logger = LogFactory.getLog(IndexResumeManagerImp.class);
	private ProxyManager proxyManager=getIndexManager();
	private IndexResumeDao indexResumeDao;
	private UpdateIndexManager updateIndexManager=this.getUpdateIndexManager();
	private boolean stopFlag=false;
	private IndexResumeTaskManager indexResumeTaskManager;
	private static final String DATE_TYPE="startDate";
    public void setIndexResumeTaskManager(
			IndexResumeTaskManager indexResumeTaskManager) {
		this.indexResumeTaskManager = indexResumeTaskManager;
	}

	public void resumeStar(IndexResumeInfo info) {
		List<resumeInfo> list=info.getResumeList();
		if(list==null)
		{
			logger.warn("无可恢复模块");
			return;
		}
		for (resumeInfo resumeInfo : list) {
			try {
				logger.info("恢复开始: "+ApplicationCategoryEnum.valueOf(resumeInfo.getAppType()).name());
				if(resumeInfo.getEndDate4Resume().equalsIgnoreCase(IndexResumeConstants.RESUME_OVER_FLAG))
				{
					logger.info("此类型已经完成恢复，不再执行恢复任务: "+ApplicationCategoryEnum.valueOf(resumeInfo.getAppType()).name());
					continue;
				}
				resume(resumeInfo.getAppType(),processResumeDateTime(DATE_TYPE,resumeInfo.getStartDate4Resume()),processResumeDateTime("q",resumeInfo.getEndDate4Resume()));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		IndexOptimize.indexOptimize();
	}
	public void setIndexResumeDao(IndexResumeDao indexResumeDao) {
			this.indexResumeDao = indexResumeDao;
		}
	public ProxyManager getIndexManager() {
		return (ProxyManager)ApplicationContextHolder.getBean("indexManager");
	}
	private UpdateIndexManager getUpdateIndexManager() {
		return (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
		
	}
	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}
	public	void resume(int appType,String starDate,String endDate){
		if(isStopFlag()){return;}
		long start=System.currentTimeMillis();
		int tatal=indexResumeDao.findPageCount(appType,starDate,endDate);
		logger.info("满足恢复条件的总个数: "+tatal);
		int[] fromArray=IndexResumeConstants.getFromIndex(tatal);
		if(fromArray!=null){
			for (int i = 0; i < fromArray.length; i++) {
				if(isStopFlag())
				{
					logger.info("恢复任务已经被终止: "+i);
					break;
				}
				List<Long> ids=indexResumeDao.findAppTypeIdList(appType,starDate,endDate,fromArray[i]);
				_resume(appType, ids);
			}
		}
		  if(!isStopFlag())
		  {
			  indexResumeTaskManager.taskEndWork(IndexResumeConstants.RESUME_OVER_FLAG, appType);
			  logger.info("结束: "+(System.currentTimeMillis()-start)+"MS");
		  }
		}

      private void _resume(int appType, List<Long> ids) {
          if(ids==null)return;
//          logger.info("开始恢复数量: "+ids.size());
    	  int i=0;
          for (Long colId : ids) {
	          try {
					if(updateIndexManager==null)
					{
						updateIndexManager=getUpdateIndexManager();
					}
				
					if(proxyManager==null)
					{
						proxyManager=getIndexManager();
					}
					if(isStopFlag())
					{
						logger.info("清理退出: "+colId);
						recordResumeDateTime(appType, colId);
//						setStopFlag(false);
						break;
					}
					updateIndexManager.index(colId,appType);
					i++;
					if(i>=IndexResumeConstants.PAGE_SIZE)
					{
						recordResumeDateTime(appType, colId);
					}
					
				}catch(Exception e) {
					logger.error("",e);
//					setStopFlag(false);
				}
          }
}

	private void recordResumeDateTime(int appType, Long colId) {
		try {
			IndexInfo info=updateIndexManager.getIndexInfo(colId,appType,false);
			indexResumeTaskManager.taskEndWork(Datetimes.formatDatetime(info.getCreateDate()), appType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String processResumeDateTime(String type,String dateTime)
	{
		if(StringUtils.isBlank(dateTime)||StringUtils.isBlank(type)){
			return null;
		}
		if(type.equals(DATE_TYPE))
		{
			return dateTime.length()<=10?dateTime+" 00:00:00":dateTime;
		}
		return dateTime.length()<=10?dateTime+" 23:59:59":dateTime;
		
		
	}
}
