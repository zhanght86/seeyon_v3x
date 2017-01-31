package com.seeyon.v3x.edoc.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.domain.EdocSuperviseDetail;
import com.seeyon.v3x.edoc.domain.EdocSupervisor;
import com.seeyon.v3x.edoc.manager.EdocSuperviseManager;

public class EdocSuperviseHelper {
	
	public static Object[] getSupervisorIdsBySummaryId(long summaryId){
		
		EdocSuperviseManager edocSuperviseManager= (EdocSuperviseManager)ApplicationContextHolder.getBean("edocSuperviseManager");
		
		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseBySummaryId(summaryId);
		
		if(null!=detail){
			
		Set<ColSupervisor> set = detail.getColSupervisors();
		
		StringBuffer superviseIds = new StringBuffer("");
		
		for(ColSupervisor sor: set){
			superviseIds.append(sor.getSupervisorId());
			superviseIds.append(",");
		}
		
		if(superviseIds.toString().endsWith(",")){
			superviseIds.deleteCharAt(superviseIds.length()-1);
		}
		
		Date endDate = detail.getAwakeDate();
		Object[] objcet = new Object[2];
		objcet[0] = superviseIds.toString();
		objcet[1] = endDate;
		return objcet;
		}else{
			return null;
		}
	}
	
	public static List<MessageReceiver> getRecieverBySummaryId(Long summaryId){

		EdocSuperviseManager edocSuperviseManager= (EdocSuperviseManager)ApplicationContextHolder.getBean("edocSuperviseManager");
		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseBySummaryId(summaryId);
		if(null!=detail){
			
			Set<ColSupervisor> set = detail.getColSupervisors();
			
			List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();			
			for(ColSupervisor sor: set){
				receivers.add(new MessageReceiver(detail.getId(), sor.getSupervisorId()));
			}
			return receivers;
		}else{
			return null;
		}
	}
	
}
