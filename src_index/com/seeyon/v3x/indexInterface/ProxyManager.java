package com.seeyon.v3x.indexInterface;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.index.share.datamodel.AreaMappingInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.datamodel.SearchResultWapper;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.domain.UpdateIndexDAO;

public class ProxyManager implements IndexManager {
	private IndexManager localIndexManager;//添加本地接口，供远程，本地服务统一调用入队列
	private IndexManager realManager;
	private static final Log log = LogFactory.getLog(ProxyManager.class);
	private UpdateIndexDAO updateDAO;
	private int commitSize=1000;//每次提交总数
	public UpdateIndexDAO getUpdateDAO() {
		return updateDAO;
	}

	public void setUpdateDAO(UpdateIndexDAO updateDAO) {
		this.updateDAO = updateDAO;
	}

	public IndexManager getRealManager() {
		return realManager;
	}

	public void setRealManager(IndexManager realManager) {
		this.realManager = realManager;
	}

	public void addToIndex(IndexInfo record) throws Exception {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		if(isNullRealManager())
		{
			return;
		}
		try {
			realManager.addToIndex(record);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public void deleteFromIndex(ApplicationCategoryEnum type, Long id)
			throws IOException {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		if(isNullRealManager())
		{
			return;
		}
		try {
			realManager.deleteFromIndex(type, id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

	}

	public void index(IndexInfo indexInfo) {
		if(indexInfo==null)
		{
			return;
		}
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		updateDAO.save(indexInfo.getEntityID(), indexInfo.getAppType().getKey());
		if(isNullRealManager())
		{
			return;
		}
		try {
			localIndexManager.index(indexInfo);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

	}

	public SearchResultWapper search(String userInfoStr, Map keyMap,
			String[] indexLib, int startPoint)
			throws IOException {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return null;
		if(isNullRealManager())
		{
			return null;
		}
		return realManager.search(userInfoStr, keyMap, indexLib, startPoint);
	}

	public SearchResultWapper search(String userInfoStr, Map keyMap,
			ApplicationCategoryEnum indexLib, int startPoint)
			throws IOException {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return null;
		if(isNullRealManager())
		{
			return null;
		}
		return realManager.search(userInfoStr, keyMap, indexLib, startPoint);
	}

	public void updateToIndex(IndexInfo info) {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		if(isNullRealManager())
		{
			return;
		}
		try {
			realManager.updateToIndex(info);
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

	}

	public void setA8Info(String a8Ip, String indexParseTimeSlice, String indexUpdateTimeSlice,
			String indexPort, String indexServiceName){
		if(isNullRealManager())
		{
			return;
		}
		realManager.setA8Info(a8Ip, indexParseTimeSlice, indexUpdateTimeSlice, indexPort, indexServiceName);
	}

	public String getIndexSystemName() {
		return realManager.getIndexSystemName();
	}

	public void insertOrUpdateAttAreaInfo(AreaMappingInfo areaMappingInfo) throws Exception {
		if(isNullRealManager())
		{
			return;
		}
		realManager.insertOrUpdateAttAreaInfo(areaMappingInfo);
	}

	public void syncAttAreasInfo(List<AreaMappingInfo> areaMappingInfoList) throws Exception {
		if(isNullRealManager())
		{
			return;
		}
		
		realManager.syncAttAreasInfo(areaMappingInfoList);
	}
	
	private boolean isNullRealManager()
	{
		if(realManager==null)
		{
//			log.warn("全文检索服务异常，重新连接 ..."+realManager);
			try {
				IndexUtil.getRMIClientProxy(this);
			} catch (Throwable e) {
				return true;
			}
		}
		return false;
	}

	public List<Long> getAllNeedDeleteList() {
		if(realManager==null)
		{
			return null;
		}
		try{
			List<Long> jobList= realManager.getAllNeedDeleteList();
			if(jobList==null){return null;}
			int totalCount=jobList.size();
			// 总批次
			if(totalCount>1000)
			{
				int totalPage=2;
				totalPage = totalCount%commitSize==0? totalCount/commitSize:totalCount/commitSize + 1;
				for (int i = 1; i < totalPage+1; i++) {
					int fromIndex=(i-1)*commitSize;
					int toIndex=fromIndex+commitSize;
					if(toIndex>totalCount)
					{
						toIndex=totalCount;
					}
					List<Long> subLists=jobList.subList(fromIndex, toIndex);
					updateDAO.deleteIndex(subLists);
				}
			}else{
				updateDAO.deleteIndex(jobList);
			}
				jobList.clear();
		}catch(Throwable e){
			log.error("parse error!",e);
		}
		return null;
		
	}

	public boolean searchDateScope(String startDate,String endDate, String libType) throws Exception {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return false;
		if(isNullRealManager())
		{
			return false;
		}
		return realManager.searchDateScope(startDate, endDate, libType);
	}
	public IndexManager getLocalIndexManager() {
		return localIndexManager;
	}

	public void setLocalIndexManager(IndexManager localIndexManager) {
		this.localIndexManager = localIndexManager;
	}

	public boolean addIndexToRecord() throws Exception {
		if(!IndexInitConfig.hasLuncenePlugIn())
			return false;
		if(isNullRealManager())
		{
			return false;
		}
		return realManager.addIndexToRecord();
	}
	public boolean isToAdding() {
		if(isNullRealManager())
		{
			return true;
		}
		return realManager.isToAdding();
	}

	/**
	 * 查看是否存在全文检索权限库
	 * @return
	 */
	public boolean isExistIndexAuthor(){
		return realManager.isExistIndexAuthor();
	}
	/**
	 * 查看全文检索已升级标记
	 * @return
	 */
	 public boolean isIndexUpgraded(){
			return realManager.isIndexUpgraded();
	 }
}
