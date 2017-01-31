package com.seeyon.v3x.plugin.dee.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;

/** 
 * @author 作者: zhanggong 
 * @version 创建时间：2012-5-30 上午04:54:43 
 * 类说明 
 */

public class DeeSynchronLogManagerImpl implements DeeSynchronLogManager{
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();
	
	public SyncBean findSynchronLog(String syncId){
		return configService.getSyncBySyncId(syncId);
	}
	public List<SyncBean> findSynchronLog(String flowId, String condition, String textfield, String textfield1){
		Map<String, Object> map = configService.getSyncLogList(flowId, 1, 1);
		Long totalCount = (Long) map.get(configService.MAP_KEY_TOTALCOUNT);
		List<SyncBean> syncBeans = (List<SyncBean>) configService.getSyncLogList("", 1, totalCount.intValue()).get(configService.MAP_KEY_RESULT);
		List<SyncBean> resultFlowBean = new ArrayList<SyncBean>();
		if(StringUtils.isNotBlank(condition) && (StringUtils.isNotBlank(textfield) || StringUtils.isNotBlank(textfield1))){
			if("byTaskName".equals(condition)){
				for(SyncBean bean : syncBeans){
					if((bean.getFlow_dis_name() == null?"":bean.getFlow_dis_name()).toLowerCase().contains(textfield.toLowerCase())){
						resultFlowBean.add(bean);
					}
				}
			}else if("byTime".equals(condition)){
				for(SyncBean bean : syncBeans){
					Date syncDate = Datetimes.parseDatetime(bean.getSync_time());
					if(StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
						if(syncDate.compareTo(Datetimes.getTodayFirstTime(textfield)) >0 && syncDate.compareTo(Datetimes.getTodayLastTime(textfield1))< 0){
							resultFlowBean.add(bean);
						}
					}else if(StringUtils.isNotBlank(textfield) && syncDate.compareTo(Datetimes.getTodayFirstTime(textfield)) >0){
						resultFlowBean.add(bean);
					}else if(StringUtils.isNotBlank(textfield1) && syncDate.compareTo(Datetimes.getTodayLastTime(textfield1)) <0){
						resultFlowBean.add(bean);
					}
				}
			}
		}else{
			resultFlowBean.addAll(syncBeans);
		}
		return resultFlowBean;
	}
	
	
	public List<RedoBean> findRedoList(String syncId, String[] redoStates, String condition, String textfield, String textfield1){
		List<RedoBean> resultList = new ArrayList<RedoBean>();
		List<RedoBean> redoBeans = new ArrayList<RedoBean>();
		for(String redoState : redoStates ){
			Map<String, Object> map = configService.getRedoList(syncId, redoState, 1, 1);
			Long totalCount = (Long) map.get(configService.MAP_KEY_TOTALCOUNT);
			List<RedoBean> tempList = new ArrayList<RedoBean>();
			tempList = (List<RedoBean>)configService.getRedoList(syncId, redoState, 1, totalCount.intValue()).get(configService.MAP_KEY_RESULT);
			for(RedoBean rb :tempList){
				redoBeans.add(rb);
			}
		}
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)){
			if("byTaskId".equals(condition)){
				for(RedoBean redoBean :redoBeans){
					if(redoBean.getRedo_id().contains(textfield)){
						resultList.add(redoBean);
					}
				}
			}else if("byStatus".equals(condition)){
				for(RedoBean redoBean :redoBeans){
					if(redoBean.getState_flag().equals(textfield)){
						resultList.add(redoBean);
					}
				}
			}
		}else{
			resultList.addAll(redoBeans);
		}
		
		return resultList;
	}
	
	public RedoBean findRedoById(String redoId){
		return configService.getRedoByRedoId(redoId);
	}
	
	public Boolean updateRedoBean(RedoBean rb){
		return configService.updateRedoBean(rb);
	}
	
	public void delSyncBySyncId(String syncId) throws TransformException{
		configService.delSyncBySyncId(syncId);
	}
	
	public void delSyncByRedoId(String syncId, String redoId) throws TransformException{
		configService.delSyncByRedoId(syncId, redoId);
	}
}
