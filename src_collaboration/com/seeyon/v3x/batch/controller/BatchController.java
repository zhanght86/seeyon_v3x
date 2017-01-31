/**
 * 
 */
package com.seeyon.v3x.batch.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import com.seeyon.cap.info.domain.InfoOpinionCAP;
import com.seeyon.v3x.batch.BatchData;
import com.seeyon.v3x.batch.BatchResult;
import com.seeyon.v3x.batch.BatchState;
import com.seeyon.v3x.batch.manager.BatchManager;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.util.Strings;

/**
 * @author dongyj 批量处理
 */
public class BatchController extends BaseController {

	private BatchManager batchManager;

	private MetadataManager metadataManager;
	
	public void setBatchManager(BatchManager batchManager) {
		this.batchManager = batchManager;
	}

	public ModelAndView doBatch(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String[] affairId = request.getParameterValues("affairId");
		String[] summaryId = request.getParameterValues("summaryId");
		String[] category = request.getParameterValues("category");
		String[] parameter = request.getParameterValues("parameter");

		String attitude = request.getParameter("attitude");
		String opinion = request.getParameter("content");
		String track = request.getParameter("trace");
		List<BatchData> initData = new ArrayList<BatchData>();
		for(int i = 0 ; i< affairId.length;i++){
			Long affair = Long.parseLong(affairId[i]);
			Long summary = Long.parseLong(summaryId[i]);
			int cate = Integer.parseInt(category[i]);
			BaseModel op = createOpinion(cate,parameter[i],attitude,opinion,"true".equals(track),affair);
			BatchData data = new BatchData(affair,summary,cate,op);
			initData.add(data);
		}
		List<BatchResult> result = batchManager.doBatch(initData);
		PrintWriter out = response.getWriter();
//		out.write(resultListToJson(result));
		out.write("<script type=\"text/javascript\">");
		out.write("var resultVar = null;");
		out.write("try{");
		out.write("eval('resultVar = "+resultListToJson(result)+" ');");
		out.write("}catch(e){");
		out.write("alert(e);");
		out.write("}");
		out.write("parent.globalBatch.doBatchResult = resultVar;");
		out.write("parent.globalBatch.showResult();");
		out.write("</script>");
		out.flush();
		out.close();
		return null;
	}
	
	private String resultListToJson(List<BatchResult> list){
		JSONArray array = new JSONArray();
		for(BatchResult batch : list){
			if(batch.getResultCode() == BatchState.Normal.getCode()){
				continue;
			}
			JSONObject json = new JSONObject();
			try {
				json.put("affairId", batch.getAffairId());
				json.put("summaryId", batch.getSummaryId());
				json.put("subject", batch.getSubject());
				json.put("resultCode", batch.getResultCode());
				if(batch.getMessage().length != 0){
					JSONArray mL = new JSONArray();
					for(String message : batch.getMessage()){
						mL.put(message);
					}
					json.put("message", mL);
				}
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}
			array.put(json);
		}
		return array.toString();
	}
	
	private BaseModel createOpinion(int category,String parameter,String attitude,String opinionStr,
			boolean track,
			Long affair){
		if(Strings.isNotBlank(parameter)){
			String[] pas = parameter.split(",");
			int att = -1;
			if(Strings.isNotBlank(attitude)){
				att = Integer.parseInt(attitude);
			}
			if(category == 1){
				ColOpinion opinion = new ColOpinion();
				opinion.setIdIfNew();
				opinion.affairIsTrack = track;
				opinion.isDeleteImmediate = false;
				opinion.isPipeonhole = false;
				opinion.setAffairId(affair);
				
				Integer attit = getAttitude(Integer.parseInt(pas[0]), att);
				if(attit != null){
					opinion.setAttitude(attit);
				}
				if(!"2".equals(pas[1])){
					opinion.setContent(opinionStr);
				}
				return opinion;
			}else if(category == 32){//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 start
				InfoOpinionCAP opinion = new InfoOpinionCAP();
				opinion.setIdIfNew();
				opinion.affairIsTrack = track;
				opinion.isDeleteImmediate = false;
				opinion.isPipeonhole = false;
				
				Integer attit = getAttitude(Integer.parseInt(pas[0]), att);
				if(attit != null){
					opinion.setAttribute(attit);
				}
				if(!"2".equals(pas[1])){
					opinion.setContent(opinionStr);
				}
				return opinion;
			}else{//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 end
				EdocOpinion opinion = new EdocOpinion();
				opinion.setIdIfNew();
				opinion.affairIsTrack = track;
				opinion.isDeleteImmediate = false;
				opinion.isPipeonhole = false;
				opinion.setAffairId(affair);
				
				Integer attit = getAttitude(Integer.parseInt(pas[0]), att);
				if(attit != null){
					opinion.setAttribute(attit);
				}
				if(!"2".equals(pas[1])){
					opinion.setContent(opinionStr);
				}
				return opinion;
			}
		}
		return null;
	}
	
	private Integer getAttitude(int code,Integer att){
		if(code != 3){
			if(code ==2 && att ==1){
				return 2;
			}
			return att;
		}
		return null;
	}
	
	public ModelAndView batch(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("batch/batch");
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
		mav.addObject("colMetadata", colMetadata);
		
		return mav;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	
}
