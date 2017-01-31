package com.seeyon.v3x.plugin.dee.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.client.service.DEEConfigService;
import com.seeyon.v3x.dee.common.db.resource.model.DeeResourceBean;
import com.seeyon.v3x.dee.common.db.schedule.model.ScheduleBean;

public class DeeScheduleManagerImpl implements DeeScheduleManager {
	/**
	 * DEE实例化
	 */
	private static final DEEConfigService configService = DEEConfigService.getInstance();
	@Override
	public List<ScheduleBean> findScheduleList() throws TransformException {
		// TODO Auto-generated method stub
		return configService.getAllScheduleList();
	}
	
	public List<ScheduleBean> findScheduleList(String condition,String byDis_name,String byFlow_name) throws TransformException{
		List<ScheduleBean> scheduleList = configService.getAllScheduleList();
		List<ScheduleBean> resultList = new ArrayList<ScheduleBean>();
		if(StringUtils.isNotBlank(condition)){
			if(StringUtils.isNotBlank(byDis_name) || StringUtils.isNotBlank(byFlow_name)){
				if("byDis_name".equals(condition)){
					for(ScheduleBean scheduleBean :scheduleList){
						if((scheduleBean.getDis_name() == null?"":scheduleBean.getDis_name()).contains(byDis_name)){
							resultList.add(scheduleBean);
						}
					}
				}
				if("byFlow_name".equals(condition)){
					for(ScheduleBean scheduleBean :scheduleList){
						if((scheduleBean.getFlow_name() == null?"":scheduleBean.getFlow_name()).contains(byFlow_name)){
							resultList.add(scheduleBean);
						}
					}
				}
			}
		}
		
		return resultList;
	}
	
	@Override
	public ScheduleBean findById(String id) throws TransformException {
		// TODO Auto-generated method stub
		return configService.getScheduleByFlowId(id);
	}

	@Override
	public void update(ScheduleBean sdBean) throws TransformException {
		// TODO Auto-generated method stub
		configService.updateSchedule(sdBean);
	}

	@Override
	public void delete(String[] ids) throws TransformException {
		// TODO Auto-generated method stub

	}

}
