package com.seeyon.v3x.collaboration.manager.cap;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.collaboration.domain.ColSummaryCAP;
import com.seeyon.cap.collaboration.manager.ColManagerCAP;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.utils.BeanUtils;

public class ColManagerCAPImpl implements ColManagerCAP {

	private static final Log logger = LogFactory.getLog(ColManagerCAPImpl.class);
	private CollaborationController collaborationController;
    private ColManager colManager;


	
	public ModelAndView appToColl(String subject, String bodyType, Date bodyCreateDate, String bodyContent, List<Attachment> atts, boolean attsNeedCopy) {
		return collaborationController.appToColl(subject, bodyType, bodyCreateDate, bodyContent, atts, attsNeedCopy);
	}

	/**
     * 通过id查找对应的ColSummary
     *
     * @param summaryId
     * @param needBody  默认false
     * @return
     * @throws ColException
     */
    public ColSummaryCAP getColSummaryById(long summaryId, boolean needBody){
    	ColSummaryCAP colSummaryCAP = new ColSummaryCAP();
    	try {
			BeanUtils.convert(colSummaryCAP, colManager.getColSummaryById(summaryId, needBody));
		} catch (ColException e) {
			logger.error(e);
		}
    	return colSummaryCAP;
    }
    
	
	
	
	
	
	
	
	public void setCollaborationController(CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
}