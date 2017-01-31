/**
 * 
 */
package com.seeyon.v3x.system.store;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2011-12-26
 */
public class StoreJob implements QuartzJob {
	
	private static Log log = LogFactory.getLog(StoreJob.class);
	
	private CacheAccessable cacheFactory = CacheFactory.getInstance(StoreJob.class);
	private CacheMap<Long, Boolean> StoreRunState = null;
	
	private ColManager colManager;
	private StoreRuleManager storeRuleManager;
	private StoreJobManager storeJobManager;
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	
	public void setStoreRuleManager(StoreRuleManager storeRuleManager) {
		this.storeRuleManager = storeRuleManager;
	}
	
	public void setStoreJobManager(StoreJobManager storeJobManager) {
		this.storeJobManager = storeJobManager;
	}
	
	public void init(){
		StoreRunState = cacheFactory.createMap("StoreRunState");
	}

	public void execute(Map<String, String> p) {
		String action = p.get("Action");
		if(action.equals("Start")){
			this.Start(p);
		}
		else if(action.equals("Stop")){
			this.Stop(p);
		}
	}
	
	private void Start(Map<String, String> p){
		long id = Long.parseLong(p.get("id"));
		StoreRule storeRule = storeRuleManager.get(id);
		log.info("数据转储开始：" + storeRule);
		
		Date beginDate = Datetimes.getTodayFirstTime(storeRule.getBeginDate()); //数据发起事件
		Date endDate = Datetimes.getTodayLastTime(storeRule.getEndDate()); //塑聚结束事件
		
		String dataScorpStr = storeRule.getDataScorp(); //数据范围：0、1、0,1
		String flowStateStr = storeRule.getFlowState(); //流程类型：0、1、0,1
		
		if(Strings.isBlank(dataScorpStr) || Strings.isBlank(flowStateStr)){
			//完成
			finish(storeRule, StoreRule.State.end, 0);
			return;
		}
		
		Integer flowState[] = null;
		if(flowStateStr.contains("0") && flowStateStr.contains("1")){
			//ignore
			flowState = new Integer[]{Constant.flowState.run.ordinal(), Constant.flowState.finish.ordinal(), Constant.flowState.terminate.ordinal()};
		}
		else if(flowStateStr.contains("0")){
			flowState = new Integer[]{Constant.flowState.run.ordinal()};
		}
		else if(flowStateStr.contains("1")){
			flowState = new Integer[]{Constant.flowState.finish.ordinal(), Constant.flowState.terminate.ordinal()};
		}
		
		int count = 0;
		
		while(true){
			List<Long> summaryIds = colManager.queryByCondition4Store(beginDate, endDate, dataScorpStr, flowState);
			if(summaryIds.isEmpty()){
				//完成
				finish(storeRule, StoreRule.State.end, count);
				return;
			}
			
			for (Long summaryId : summaryIds) {
				try {
					int c = this.storeJobManager.doCut(summaryId);
					count += c;
				}
				catch (Throwable e) {
					log.error("", e);
				}
				try {
					Thread.currentThread().sleep(200);
				}
				catch (Throwable e) {
				}
			}
			
			//判断是否要暂停
			if(StoreRunState.contains(id)){
				finish(storeRule, StoreRule.State.running, count);
				return;
			}
		}
	}
	
	private void Stop(Map<String, String> p){
		long id = Long.parseLong(p.get("id"));
		StoreRunState.put(id, Boolean.TRUE);
	}
	
	private void finish(StoreRule storeRule, StoreRule.State state, int nowResult){
		storeRuleManager.updateState(storeRule.getId(), state, storeRule.getResult() + nowResult);
		if(StoreRule.State.end == state){
			QuartzHolder.deleteQuartzJob("StoreRuleStart" + storeRule.getId());
			QuartzHolder.deleteQuartzJob("StoreRuleStop" + storeRule.getId());
		}
		
		StoreRunState.remove(storeRule.getId());
		log.info("数据转储结束：" + storeRule.getId() + ", " + state + ", " +nowResult);
	}

}
