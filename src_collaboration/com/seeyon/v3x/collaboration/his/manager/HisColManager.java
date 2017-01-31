package com.seeyon.v3x.collaboration.his.manager;

import java.util.List;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-1-7
 */
public interface HisColManager {

	public void save(ColSummary summary);
	
	public ColSummary getColSummaryById(long summaryId, boolean needBody) throws ColException;
	
	public ColSummary getColAllById(long summaryId) throws ColException;
	
	public List<ResultModel> iSearch(ConditionModel cModel);
	
	public List<ColSummary> getSummaryIdByFormIdAndRecordId(Long formAppId, Long formId, Long formRecordId);

}