package com.seeyon.v3x.common.ajax.impl;

import java.io.PrintWriter;

import com.seeyon.v3x.common.ObjectToXMLUtil;
import com.seeyon.v3x.common.ajax.AJAXResponse;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-19
 */
public class AJAXResponseImpl implements AJAXResponse {
	private Object result;

	private PrintWriter out;

	/**
	 * ����AJAXResponseʵ��
	 * 
	 * @param result
	 *            Object ���
	 * @param out
	 *            PrintWriter HTTP Servlet�������
	 */
	public AJAXResponseImpl(Object result, PrintWriter out) {
		this.result = result;
		this.out = out;
	}

	/**
	 * 完成JavaBean到XML的转换
	 */
	public void complete(String returnValueType) {
		if("XML".equals(returnValueType)){
			out.print(ObjectToXMLUtil.objectToXML(result));
		}
		else{
			out.print(String.valueOf(result));
		}
		out.close();
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}
}
