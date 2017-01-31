package com.seeyon.v3x.interfaces;

import java.net.URL;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.fault.XFireFault;

import com.seeyon.v3x.interfaces.dao.WebServiceDAO;
import com.seeyon.v3x.interfaces.domain.WebServiceRegister;
import com.seeyon.v3x.interfaces.domain.WebServiceResult;

/*
 * 
 */
public class WSCallBackService {
	
	private WebServiceDAO webServiceDAO;

	public void setWebServiceDAO(WebServiceDAO webServiceDAO) {
		this.webServiceDAO = webServiceDAO;
	}
	
	public void addCallBack(int appType,long objectId,String callbackUrl){
		WebServiceRegister callback=new WebServiceRegister();
		callback.setIdIfNew();
		callback.setAppType(appType);
		callback.setUserId(objectId);
		callback.setWsUrl(callbackUrl);
		webServiceDAO.save(callback);
	}
	/*
	 * 工作流流程程结束后执行此回调
	 */
	@SuppressWarnings("unchecked")
	public void callBackCheck(int appType,long objectId) throws Exception{

	}
	/*
	 * 在此执行回调
	 * 在此实现动态调用
	 */
	public void callBack(String webserviceCode,WebServiceResult result) throws Exception{
		try{
			//TODO:在此执行WebService调用
		long webserviceId=Long.parseLong(webserviceCode);
		WebServiceRegister wsRegister=(WebServiceRegister) webServiceDAO.load(WebServiceRegister.class, webserviceId);	
		String url=wsRegister.getWsUrl();
		Client client = new Client(new URL(url));
//		System.out.println("The::"+result.toXml());
		client.invoke("publishResult", new Object[]{result.toXml()});

		}catch(Exception e){
			e.printStackTrace();
			throw new XFireFault("回调时出错！",XFireFault.SENDER);
		}
		
		
	}

}
