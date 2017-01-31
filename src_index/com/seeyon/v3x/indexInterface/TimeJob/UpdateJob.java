package com.seeyon.v3x.indexInterface.TimeJob;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.index.queue.UpdateMap;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexManager;


/*
 * 检查是否有更新的，如果有的话则批量删除、添加有更新的纪录
 */
public class UpdateJob implements Runnable {
	
	//private UpdateContext context;
	//private UpdateDataMap updateMap;
	private int min;
	private Map<String, IndexInfo> updateMap;
	private static final Log log = LogFactory.getLog(UpdateJob.class);
	private IndexManager indexManager;
	public	UpdateJob(int time,IndexManager manager){
		this.min = time;
		this.indexManager = manager;
	}

	public void run() {
		try{
			Thread.sleep(1*60*1000);//等待60秒，等待初始化过程结束再开始更新
		}catch(InterruptedException e){
			log.error("",e);
		}

		while(true){
			try {
				// 更新索引库
				// 1）拷贝接收map后立即清空接收map
				synchronized(UpdateMap.getReceiveMap()){
					updateMap =new HashMap<String, IndexInfo>(UpdateMap.getReceiveMap());
					UpdateMap.clearReceiveMap();
				}
				if(updateMap!=null)
				{
					if(log.isDebugEnabled() && updateMap.size()>0)log.info("UpdateIndex, Queue length,"+updateMap.size());
					
					for (Entry<String, IndexInfo> obj : updateMap.entrySet()) {
						IndexInfo info = obj.getValue();
						try {
							//indexManager.deleteFromIndex(info.getAppType(), info.getEntityID());
							//Convertor.contentConvertor(info);//在添加索引时再解析。
							
							indexManager.addToIndex(info);
							updateMap.put(obj.getKey(), null); //及时清理大对象 090904 leigf
						} catch (Throwable e) {
							log.error("分拣更新队列时：",e);
						}
					}
					if(log.isDebugEnabled() && updateMap.size()>0)log.info("UpdateIndex, Queue update done,"+updateMap.size());
					
					updateMap.clear();
				}
				// 2）对updateMap进行全文检索更新
			} catch (Exception e1) {
				log.error("",e1);
			}
			// 4）全部完成后，暂停 min*60*1000 or safeTime 毫秒继续 //其中safeTime 安全毫秒，防止 min为0产生高负载
			long newMin = min;
			long safeTime = 60*1000*3;
			long mills = newMin*60*1000;
			if (mills < safeTime) {
				mills = safeTime;
			}
			try {
				Thread.sleep(mills);
			} catch (InterruptedException e) {
				log.error("",e);
			}
		}
	}

	public void setMin(int min) {
		this.min = min;
	}
	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
}
