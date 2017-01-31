/**
 * 
 */
package com.seeyon.v3x.inquiry.webmdoel;

import java.util.ArrayList;
import java.util.List;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * @author lin tian
 * 
 * 2007-3-2
 */
public class SurveyBasicCompose {
	
	private InquirySurveybasic inquirySurveybasic;

	private List<SubsurveyAndItemsCompose> subsurveyAndICompose = new ArrayList<SubsurveyAndItemsCompose>();// 调查项与问题

	private List<V3xOrgEntity> entity;// 发布对象

	private V3xOrgMember sender; // 发布人
	
	private V3xOrgMember conser; // 审核人
	
	private V3xOrgDepartment deparmentName;
	
	private int questionsize = 0;
	
	private SurveyTypeCompose surveyTypeCompose;
	

	public SurveyTypeCompose getSurveyTypeCompose() {
		return surveyTypeCompose;
	}

	public void setSurveyTypeCompose(SurveyTypeCompose surveyTypeCompose) {
		this.surveyTypeCompose = surveyTypeCompose;
	}

	/**
	 * @return the questionsize
	 */
	public int getQuestionsize() {
		 try{
		     return subsurveyAndICompose.size();
		 }catch(Exception e){
			 return questionsize;
		 }
	}

	/**
	 * @return the deparmentName
	 */
	public V3xOrgDepartment getDeparmentName() {
		return deparmentName;
	}

	/**
	 * @param deparmentName the deparmentName to set
	 */
	public void setDeparmentName(V3xOrgDepartment deparmentName) {
		this.deparmentName = deparmentName;
	}

	/**
	 * @return the conser
	 */
	public V3xOrgMember getConser() {
		return conser;
	}

	/**
	 * @param conser the conser to set
	 */
	public void setConser(V3xOrgMember conser) {
		this.conser = conser;
	}

	public List<V3xOrgEntity> getEntity() {
		return entity;
	}

	public void setEntity(List<V3xOrgEntity> entity) {
		this.entity = entity;
	}

	public InquirySurveybasic getInquirySurveybasic() {
		return inquirySurveybasic;
	}

	public void setInquirySurveybasic(InquirySurveybasic inquirySurveybasic) {
		this.inquirySurveybasic = inquirySurveybasic;
	}

	public V3xOrgMember getSender() {
		return sender;
	}

	public void setSender(V3xOrgMember sender) {
		this.sender = sender;
	}

	public List<SubsurveyAndItemsCompose> getSubsurveyAndICompose() {
		return subsurveyAndICompose;
	}

	public void setSubsurveyAndICompose(
			List<SubsurveyAndItemsCompose> subsurveyAndICompose) {
		this.subsurveyAndICompose = subsurveyAndICompose;
	}


}
