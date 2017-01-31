/**
 * Id: ColManagerFacade.java, v1.0 2012-4-7 wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.v3x.collaboration.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMProcess;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant.SendType;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.common.authenticate.domain.User;

/**
 * @Project/Product: 产品或项目名称（A8）
 * @Description: 类功能描述
 * @Copyright: Copyright (c) 2012 of Seeyon, Ltd.
 * @author: wangchw
 * @time: 2012-4-7 下午02:40:15
 * @version: v1.0
 */
public interface ColManagerFacade {
	/**
     * 启动一个流程(统一入口)
     * @param flowData
     * @param colSummary
     * @param body
     * @param senderOninion
     * @param sendType
     * @param options
     * @param isNew
     * @param senderId
     * @param track
     * @param trackMembers
     * @param newProcessId
     * @return
     * @throws ColException
     */
    public int runCaseFacade(FlowData flowData, ColSummary colSummary,
			ColBody body, ColOpinion senderOninion, SendType sendType,
			Map options, boolean isNew, Long senderId,boolean track,
			String trackMembers,List<NewflowSetting> newflowList,User user,
			String[] formInfo,long mId,String... newProcessId) throws Exception;
    
    /**
     * 立即启动一个流程(统一入口)
     * @param colSummary
     * @param flowData
     * @param _affairId
     * @param body
     * @param user
     * @param processId
     * @return
     * @throws Exception
     */
    public boolean runCaseImmediateFacade(
			ColSummary colSummary,FlowData flowData,
			String _affairId,ColBody body,User user,
			String processId) throws Exception;
    
    /**
     * 处理一个流程(统一入口)
     * @param affairId
     * @param signOpinion
     * @param manualMap
     * @param condition
     * @param processId
     * @param user
     * @param draftOpinionId
     * @param fieldName
     * @param summary
     * @param request
     * @param summaryId
     * @param formApp
     * @param _affairId
     * @param oldIsVouch
     * @throws Exception
     */
    public void finishWorkItemFacade(long affairId, ColOpinion signOpinion,
    		Map<String,String[]> manualMap, Map<String, String> condition, 
    		String processId, User user,String draftOpinionId,
    		String[] fieldName,ColSummary summary,HttpServletRequest request,
    		long summaryId,String formApp,String _affairId,Integer oldIsVouch) throws Exception;
    
    public void finishWorkItemFacade(Affair affair, ColOpinion signOpinion,
    		Map<String,String[]> manualMap, Map<String, String> condition, 
    		BPMProcess process , User user,String draftOpinionId,
    		String[] fieldName,ColSummary summary,HttpServletRequest request,
    		long summaryId,String formApp,Integer oldIsVouch) throws Exception;
    
}
