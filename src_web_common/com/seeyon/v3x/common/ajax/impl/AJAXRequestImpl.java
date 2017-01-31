package com.seeyon.v3x.common.ajax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.ajax.AJAXException;
import com.seeyon.v3x.common.ajax.AJAXParameter;
import com.seeyon.v3x.common.ajax.AJAXRequest;

/**
 * AJAX Service Request ����Parameters
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-19
 */
public class AJAXRequestImpl implements AJAXRequest {
	protected static final Log log = LogFactory.getLog(AJAXRequest.class);

	private final HttpServletRequest request;

	private Class[] types;
	
	private Object[] values;

	private String serviceName;

	private String methodName;

	private HttpServletResponse response;

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param context
	 *            ServletContext
	 * @throws AJAXException
	 */
	public AJAXRequestImpl(HttpServletRequest request,
			HttpServletResponse response, String serviceName, String methodName) throws AJAXException {
		this.request = request;
		this.response = response;
		this.serviceName = serviceName;
		this.methodName = methodName;

		parseRequestArguments();
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	/**
	 * 
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getServletRequest() {
		return request;
	}

	/**
	 * 
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getServletResponse() {
		return response;
	}

	/**
	 * �������
	 * 
	 * @throws AJAXException
	 */
	private void parseRequestArguments() throws AJAXException {
		boolean isNeedEncoder = true;
		String requestBodyEncoding = this.request.getCharacterEncoding();
		//当前body data的编码已经是UTF-8的了，不需要再转码，否则就按照8859_1处理
		if("UTF-8".equalsIgnoreCase(requestBodyEncoding)){
			isNeedEncoder = false;
		}

		try {
			Enumeration e = request.getParameterNames();
			if (e != null) {
				Map<Integer, AJAXParameter> pm = new HashMap<Integer, AJAXParameter>();
				
				while (e.hasMoreElements()) {
					String name = (String)e.nextElement();
					
					if(name.startsWith("P_")){						
						String[] nameKeys = name.split("_");
						
						int index = Integer.parseInt(nameKeys[1]) - 1;
						String type = nameKeys[2];
						String[] values = null;
						String value = null;
						
						if(nameKeys.length == 3){
							value = request.getParameter(name);
						}
						else if(nameKeys.length == 4){
							type += "[]";
							values = request.getParameterValues(name);
						}
						else if(nameKeys.length == 5 && nameKeys[4].equals("N")){
							type += "[]";
							values = new String[0];
						}
						else{
							continue;
						}
						
						AJAXParameter ajaxParam = new AJAXParameterImpl(type, value, values, isNeedEncoder);

						pm.put(index, ajaxParam);
					}
				}
				
				List<Integer> indexs = new ArrayList<Integer>(pm.keySet());
				Collections.sort(indexs);
				
				int len = indexs.size();
				types = new Class[len];
				values = new Object[len];
				
				for (int i = 0; i < len; i++) {
					AJAXParameter p = pm.get(i);
					
					types[i] = p.getClassName();
					values[i] = p.getValue();
				}
			}
		}
		catch (Exception ex) {
			throw new AJAXException(
					"Errors were encountered parsing request parameters for the AJAX service "
							+ serviceName, ex);
		}
	}

	public Class[] getTypes() {
		return types;
	}

	public Object[] getValues() {
		return values;
	}	
	
}
