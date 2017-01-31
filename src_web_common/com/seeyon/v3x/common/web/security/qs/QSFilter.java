/**
 * 
 */
package com.seeyon.v3x.common.web.security.qs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.GenericFilterProxy;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.SettableHttpServletRequest;
import com.seeyon.v3x.product.util.QSEncoder;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2010-3-12 
 */
public class QSFilter extends GenericFilterProxy {
	
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uid = request.getParameter(QSEncoder.UID_KEY);
    	String[] uids = null;
    	if(Strings.isNotBlank(uid)){
    		uids = QSEncoder.decoder(uid);
    		
    		SettableHttpServletRequest req = null;
    		
    		if(request instanceof SettableHttpServletRequest){
    			req = (SettableHttpServletRequest)(request);
    		}
    		else{
    			req = new SettableHttpServletRequest(request);
    		}
    		
    		req.setParameters(uids[0]);
    		req.setParameters("___K=" + uids[1]);
    	}
		
		return true;
	}

    
    /**
     * <pre>
     * <code>
     * &lt;a href="collaboration.do?method=detail&${v3x:encoderQueryString('affairId=32943845945&summaryId=972834283423&from=pending')&...}"&gt;协同XXX&lt;/a&gt;
     * </code>
     * 在Controller中，跟传统方式一样，用<code>request.getParameter("***")</code>获取
     * <code>
     * public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
     *     String affairId = request.getParameter("getParameter");
     *     String summaryId = request.getParameter("summaryId");
     *     ....
     *     return mv;
     * }
     * </code>
     * </pre>
     * 
     * @param queryString URL后面的参数字符串，如：id=23452345&type=10984102491
     * @return ___UID=AS23452345JDfS2Sa345sdIO234a5sdfJSODS024593240asd2345
     */
    public static String encoderQueryString(String queryString){
    	if(Strings.isBlank(queryString)){
    		return null;
    	}
    	
    	User user = CurrentUser.get();
    	
    	long securityKey = user.getSecurityKey();
    	
    	return QSEncoder.UID_KEY + "=" + java.net.URLEncoder.encode(QSEncoder.encoder(queryString, String.valueOf(securityKey)));
    }
}
