package com.seeyon.v3x.plugin.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.event.CollaborationFinishEvent;
import com.seeyon.v3x.collaboration.event.CollaborationProcessEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStartEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStepBackEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStopEvent;
import com.seeyon.v3x.collaboration.event.CollaborationTakeBackEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class CollaborationEvent {
	private static final Log log = LogFactory.getLog(CollaborationEvent.class);
	private ColSummary findSummary(Long summaryId){
		ColManager colManager = (ColManager)ApplicationContextHolder.getBean("colManager");
		try {
			return colManager.getSimpleColSummaryById(summaryId);
		} catch (ColException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	@ListenEvent(event = CollaborationStartEvent.class)
	public void onStartProcess(CollaborationStartEvent event){
		String summarySubject = findSummary(event.getSummaryId()).getSubject();
		String form = event.getFrom();
		log.info("监听发起流程:《"+summarySubject+"》,来自:"+form);
	}
	
	@ListenEvent(event = CollaborationFinishEvent.class)
	public void onFinishProcess(CollaborationFinishEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();
		log.info("监听到流程结束:《"+subject+"》");
	}
	
	@ListenEvent(event = CollaborationCancelEvent.class)
	public void onCancelProcess(CollaborationCancelEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();
		String member = Functions.showMemberName(event.getUserId());
		log.info("监听到流程取消:《"+subject+"》操作人："+member);
	}
	
	@ListenEvent(event = CollaborationProcessEvent.class)
	public void onProProcess(CollaborationProcessEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();

		log.info("监听流程正常处理《"+subject+"》,操作人:");
	}
	
	@ListenEvent(event = CollaborationStepBackEvent.class)
	public void onStepProcess(CollaborationStepBackEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();
		log.info("监听流程《"+subject+"》回退");
	}
	
	@ListenEvent(event = CollaborationStopEvent.class)
	public void onStopProcess(CollaborationStopEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();
		Long userId = event.getUserId();
		String member = "";
		if(userId != null){
			member = Functions.showMemberName(userId);
		}
		log.info("监听流程《"+subject+"》终止,操作人:"+member);
	}
	
	@ListenEvent(event = CollaborationTakeBackEvent.class)
	public void onTakeBackProcess(CollaborationTakeBackEvent event){
		String subject = findSummary(event.getSummaryId()).getSubject();
		Long userId = event.getUserId();
		String member = "";
		if(userId != null){
			member = Functions.showMemberName(userId);
		}
		log.info("监听流程《"+subject+"》取回,操作人:"+member);
	}
}
