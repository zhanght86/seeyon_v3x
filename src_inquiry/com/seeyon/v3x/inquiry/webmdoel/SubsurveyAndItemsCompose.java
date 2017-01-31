/**
 * 
 */
package com.seeyon.v3x.inquiry.webmdoel;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.inquiry.domain.InquirySubsurvey;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;

/**
 * @author lin tian
 * 2007-3-9 
 */
public class SubsurveyAndItemsCompose {
    private InquirySubsurvey inquirySubsurvey;    
    private List<InquirySubsurveyitem> items = new ArrayList<InquirySubsurveyitem>();
	/**
	 * @return the inquirySubsurvey
	 */
	public InquirySubsurvey getInquirySubsurvey() {
		return inquirySubsurvey;
	}
	/**
	 * @param inquirySubsurvey the inquirySubsurvey to set
	 */
	public void setInquirySubsurvey(InquirySubsurvey inquirySubsurvey) {
		this.inquirySubsurvey = inquirySubsurvey;
	}
	/**
	 * @return the items
	 */
	public List<InquirySubsurveyitem> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<InquirySubsurveyitem> items) {
		this.items = items;
	}
    
}
