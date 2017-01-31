package com.seeyon.v3x.indexInterface.TimeJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.indexInterface.domain.V3xUpdateIndex;

/*
 * 这里保存被修改过的协同，或其它应用的id
 * 
 */
public class UpdateDataMap {

	private Map<Long, Integer> map=new HashMap<Long, Integer>();

	private UpdateContext updateContext;

	public UpdateDataMap() {
		
		
	}

	public synchronized void add(Long entityId, Integer type) {
		Object obj = map.get(entityId);
		if (obj == null) {
			map.put(entityId, type);
			save(entityId, type);
		}
	}

	public synchronized Map getMap() {
		Map updateMap = null;
		if (map.size() > 0) {
			updateMap = map;
			map = new HashMap<Long, Integer>();
		}
		return updateMap;
	}

	/*
	 * 持久化到数据库
	 * 
	 */
	public void save(Long entityId, Integer type) {
//		updateContext.getUpdateDAO().save(entityId, type);
	}

	/*
	 * 从数据库中取出
	 */
	public void getRecords(List<V3xUpdateIndex> list) {
//		List<V3xUpdateIndex> list = updateContext.getUpdateDAO().getAllRecord();
//		if (list != null && list.size() > 0) {
//			Map<Long, Integer> nmap = new HashMap<Long, Integer>();
//			for (int i = 0; i < list.size(); i++) {
//				V3xUpdateIndex updateIndex = list.get(i);
//				nmap.put(updateIndex.getEntityId(), updateIndex.getType());
//			}
//			map = nmap;
//		}

	}

	public void setUpdateContext(UpdateContext updateContext) {
		this.updateContext = updateContext;
	}

}
