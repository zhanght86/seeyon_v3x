/**
 * 
 */
package com.seeyon.v3x.main.section.sso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.util.HttpClientUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class SSOManagerImpl implements SSOManager {
	private static final Log log = LogFactory.getLog(SSOManagerImpl.class);

	private static Map<String, HttpClientObject> HttpClientObjects = new HashMap<String, HttpClientObject>();
	
	private OuterlinkManager outerlinkManager;
	
	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	private HttpClientObject getHttpClientObject(long memberId,
			long linkSystemId, int sessionTimeout)
			throws NoSuchLinkSystemException, LoginFailingException {
		String key = memberId + "::" + linkSystemId;
		HttpClientObject c = HttpClientObjects.get(key);
		
		if(c != null && (sessionTimeout < 0 || System.currentTimeMillis() - c.getLastAccessDatestamp().getTime() < sessionTimeout * 60 * 1000)){
			c.updateLastAccessDatestamp();
			return c;
		}
		
		Map<String, String> optionVos = new HashMap<String, String>();
		
		LinkSystem ls = outerlinkManager.getLinkSystemById(linkSystemId);
		if(ls == null){
			throw new NoSuchLinkSystemException(String.valueOf(linkSystemId));
		}
		
		Set<LinkOption> options = ls.getLinkOption();
		if(options != null && options.size() > 0){
			List<Long> idlist = new ArrayList<Long>();
			Map<Long, LinkOption> map = new HashMap<Long, LinkOption>();
			for(LinkOption lo : options){
				idlist.add(lo.getId());
				map.put(lo.getId(), lo);
			}
			List<LinkOptionValue> values = outerlinkManager.findOptionValueById(idlist, memberId);	
			
			if(values != null && !values.isEmpty()){
				for(LinkOptionValue value : values){
					optionVos.put(map.get(value.getLinkOptionId()).getParamSign(), Functions.decodeStr(value.getValue()));
				}
			}
		}
		
		if(optionVos == null || optionVos.isEmpty()){
			return null;
		}
		
		HttpClientUtil u = new HttpClientUtil();
		try {
			u.open(ls.getUrl(), ls.getMethod());
			
			for (Iterator<Map.Entry<String, String>> iter = optionVos.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> m = iter.next();
				u.addParameter(m.getKey(), m.getValue());
			}
			
			u.send();
			if(ls.isNeedContentCheck()){
			    String contentForCheck = ls.getContentForCheck();
			    String responseBodyAsString = u.getResponseBodyAsString(null);
			    if(contentForCheck != null && contentForCheck.trim().length() > 0){
			        if(!responseBodyAsString.contains(responseBodyAsString)){
			            throw new LoginFailingException();
			        }
			    }
			} else {
    			if(Strings.isNotBlank(u.getResponseHeader().get("LoginError"))){
    				throw new LoginFailingException();
    			}
			}
			
			c = new HttpClientObject(u);
			
			HttpClientObjects.put(key, c);
			
			return c;
		}
		catch (LoginFailingException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("", e);
		}
		finally{
			u.close();
		}
		
		return null;
	}
	
	public String useSSO(long memberId, long linkSystemId,
			int sessionTimeout, String pageURL, String pageCharset) throws NoSuchLinkSystemException, LoginFailingException{
		HttpClientObject httpClientObject = this.getHttpClientObject(memberId, linkSystemId, sessionTimeout);
		
		if(httpClientObject != null && Strings.isNotBlank(pageURL)){
			HttpClientUtil u = httpClientObject.getHttpClient();
			try {
				u.open(pageURL, "get");
				u.send();
				
		    	return u.getResponseBodyAsString(pageCharset);
			}
			catch (Exception e) {
				log.error("", e);
			}
			finally{
				u.close();
			}
		}
		
		return null;
	}
	
	public void clearSSO(long memberId, long linkSystemId){
		String key = memberId + "::" + linkSystemId;
		HttpClientObjects.remove(key);
	}
	
	public Map<String, String>  getCookies(long memberId, long linkSystemId) {
		String key = memberId + "::" + linkSystemId;
		HttpClientObject c = HttpClientObjects.get(key);
		
		if(c != null){
			return c.getHttpClient().getCookies();
		}
		
		return null;
	}
	
	private static class HttpClientObject {
		private HttpClientUtil httpClient;
		
		private Date lastAccessDatestamp;
		
		public HttpClientObject(HttpClientUtil httpClient) {
			super();
			this.httpClient = httpClient;
			this.lastAccessDatestamp = new Date();
		}

		public HttpClientUtil getHttpClient() {
			return httpClient;
		}

		public Date getLastAccessDatestamp() {
			return lastAccessDatestamp;
		}

		public void updateLastAccessDatestamp() {
			this.lastAccessDatestamp = new Date();
		}
	}
	
}
