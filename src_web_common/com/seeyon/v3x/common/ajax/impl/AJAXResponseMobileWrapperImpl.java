package com.seeyon.v3x.common.ajax.impl;

import java.io.PrintWriter;

import com.seeyon.v3x.common.ObjectToXMLUtil;
import com.seeyon.v3x.common.ajax.AJAXException;
import com.seeyon.v3x.common.ajax.AJAXRequest;
import com.seeyon.v3x.common.ajax.AJAXResponse;
/**
 * 
 * @author Jackie
 *
 */
public class AJAXResponseMobileWrapperImpl implements AJAXResponse {
	
	private AJAXResponse ajaxResponse;
	private AJAXRequest ajaxRequest;
	
	public AJAXRequest getAjaxRequest() {
		return ajaxRequest;
	}

	public void setAjaxRequest(AJAXRequest ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	public AJAXResponseMobileWrapperImpl(AJAXRequest ajaxRequest,AJAXResponse ajaxResponse) {
		super();
		this.ajaxRequest = ajaxRequest;
		this.ajaxResponse = ajaxResponse;
	}

	public AJAXResponse getAjaxResponse() {
		return ajaxResponse;
	}

	public void setAjaxResponse(AJAXResponse ajaxResponse) {
		this.ajaxResponse = ajaxResponse;
	}

	@Override
	public void complete(String returnValueType) throws AJAXException {
		if(ajaxResponse instanceof AJAXResponseImpl) {
			AJAXResponseImpl impl = (AJAXResponseImpl)ajaxResponse;
			PrintWriter out = impl.getOut();
			Object result = impl.getResult();
			String output = "";
			if("XML".equals(returnValueType)){
				output = ObjectToXMLUtil.objectToXML(result);
			}
			else{
				output = String.valueOf(result);
			}
			String callback = ajaxRequest.getServletRequest().getParameter("callback");
			if(callback != null) {
				String r = callback + "(" + "'" + output + "'" + ");";
				out.print(r);
			}else {
				out.print(output);
			}
			out.close();
		}
	}

}
