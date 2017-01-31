package com.seeyon.v3x.common.ajax.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.seeyon.v3x.common.ajax.AJAXException;
import com.seeyon.v3x.common.ajax.AJAXRequest;
import com.seeyon.v3x.common.ajax.AJAXResponse;
import com.seeyon.v3x.common.ajax.AJAXService;
import com.seeyon.v3x.util.Strings;

/**
 *
 * <p>Title: AJAX Service ����ĺ��ģ�����AJAXRequest������һ�������(List���߼̳�AJAXBase)</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author <a href="mailto:tanmf@seeyon.com">tanmf</a>
 * @version 1.0
 */
public class AJAXServiceImpl implements AJAXService, BeanFactoryAware {
	protected static final Log LOG = LogFactory.getLog(AJAXServiceImpl.class);
	
    private Map<String, String> serviceToBeans;
    private BeanFactory beanFactory;

    public AJAXServiceImpl(Map<String, String> serviceToBeans) {
        this.serviceToBeans = serviceToBeans;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    public AJAXResponse processRequest(AJAXRequest request) throws
            AJAXException {
        final String serviceName = request.getServiceName();
        final String methodName = request.getMethodName();
        
        if(Strings.isBlank(serviceName) || Strings.isBlank(methodName)){
        	try {
				return new AJAXResponseImpl(null, request.getServletResponse().getWriter());
			}
			catch (IOException e) {
				throw new AJAXException(e);
			}
        }

        String beanId = serviceToBeans.get(serviceName); //从AjaxService.xml中找
        if(Strings.isBlank(beanId)){ //如果在影射中没有，直接用serviceName
        	beanId = serviceName;
        }
        Object targetService = null;
        try {
            targetService = beanFactory.getBean(beanId); //�õ�beanId
        }
        catch (Exception ex) {
            throw new AJAXException("AJAX Service " + serviceName + "." + methodName + "不存在。", ex);
        }
        
        if(null == targetService){
        	throw new AJAXException("AJAX Service " + serviceName + " instance is not exist.");
        }
        
        try{
            Method method = targetService.getClass().getMethod(methodName, request.getTypes());

            Object result = method.invoke(targetService, request.getValues());

            return new AJAXResponseImpl(result, request.getServletResponse().getWriter());
        }
        catch (Exception ex) {
            throw new AJAXException(ex);
        }
    }

}
