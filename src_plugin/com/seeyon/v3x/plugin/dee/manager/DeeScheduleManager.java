package com.seeyon.v3x.plugin.dee.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.common.base.page.Page;
import com.seeyon.v3x.dee.common.db.redo.model.RedoBean;
import com.seeyon.v3x.dee.common.db.redo.model.SyncBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;

import com.seeyon.v3x.plugin.dee.model.JDBCResourceBean;
import com.seeyon.v3x.plugin.dee.model.JNDIResourceBean;

/**
 * 功能说明：对Dee数据源的操作
 * @author XQ
 *
 */
public interface DeeScheduleManager {

	public List<ScheduleBean> findScheduleList() throws TransformException;
	
	public List<ScheduleBean> findScheduleList(String condition,String byDis_name,String byFlow_name)throws TransformException;
	
	public ScheduleBean findById(String id) throws TransformException;
	
	public void update(ScheduleBean drb) throws TransformException;
	
	public void delete(String[] ids) throws TransformException;
}
