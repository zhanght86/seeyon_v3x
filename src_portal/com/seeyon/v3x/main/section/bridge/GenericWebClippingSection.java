/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.thirdparty.UrlBuilder;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.util.HttpClientUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 通用的WebClipping栏目
 * 
 * <pre>
 * <code>
 *  &lt;bean id=&quot;newsSection3&quot; class=&quot;com.seeyon.v3x.main.section.bridge.GenericWebClippingSection&quot; init-method=&quot;init&quot;&gt;
 *    &lt;property name=&quot;id&quot; value=&quot;newsSection3&quot; /&gt;
 *    &lt;property name=&quot;name&quot; value=&quot;Sohu博客图片&quot; /&gt;
 *    &lt;property name=&quot;url&quot; value=&quot;http://blog.sohu.com/&quot; /&gt;
 *    &lt;property name=&quot;contentCharSet&quot; value=&quot;gb2312&quot; /&gt;
 *    &lt;property name=&quot;startTag&quot;&gt;
 *      &lt;value&gt;&lt;![CDATA[&lt;div class=&quot;right&quot;&gt;]]&gt;&lt;/value&gt;
 *    &lt;/property&gt;
 *    &lt;property name=&quot;endTag&quot;&gt;
 *      &lt;value&gt;&lt;![CDATA[&lt;div class=&quot;bFoot&quot;&gt;&lt;a href=&quot;http://blog.sohu.com/vision/&quot; target=&quot;_blank&quot;&gt;]]&gt;&lt;/value&gt;
 *    &lt;/property&gt;
 *    &lt;property name=&quot;additionalPreHTML&quot;&gt;
 *      &lt;value&gt;&lt;![CDATA[
 *      &lt;link href=&quot;http://blog.sohu.com/styles/index.css&quot; rel=&quot;stylesheet&quot; type=&quot;text/css&quot; /&gt;
 *      &lt;link href=&quot;http://blog.sohu.com/styles/card.css&quot; rel=&quot;stylesheet&quot; type=&quot;text/css&quot; /&gt;
 *      ]]&gt;&lt;/value&gt;
 *    &lt;/property&gt;
 *    &lt;property name=&quot;additionalSufHTML&quot;&gt;
 *      &lt;value&gt;
 *      [更多]
 *      &lt;/value&gt;
 *    &lt;/property&gt;
 *  &lt;/bean&gt;
 * </code>
 * </pre>
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-20
 */
public class GenericWebClippingSection extends BaseSection {
	private static final Log log = LogFactory.getLog(GenericWebClippingSection.class);
	
	private static final int BLOCK_SIZE = 4096;
	
	/**
	 * 数据缓存对：key-链接地址 Value-内容
	 */
	private Map<String, String> cache = new Hashtable<String, String>();

	private HtmlTemplete h = new HtmlTemplete();

	private String id;

	private String icon;

	private String name;

	private String url;

	private UrlBuilder urlBuilder;

	private boolean useCache = true;
	
	private String startTag;
	
	private String endTag;
	
	private long period = -1;
	
	private String contentCharSet = "ISO-8859-1";
	
	private String additionalPreHTML = "";
	
	private String additionalSufHTML = "";
	
	private int timeoutInMilliseconds = 5000;
	
	/**
	 * 最后一次更新时间戳
	 */
	private long lastUpdateTime = 0L;

	/**
	 * 设置栏目显示的名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 设置栏目的图标
	 * 
	 * @param icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 设置栏目的唯一id，必须和Spring bean的id一致
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置页面的地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 设置页面地址的生成器，<code>url</code>优先
	 * 
	 * @param urlBuilder
	 */
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}
	
	/**
	 * 是否使用缓存，默认true
	 * 
	 * @param useCache
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	/**
	 * 设置页面内容截取的前标志，从第一个匹配到的开始截取
	 * 
	 * @param prefix
	 */
	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}

	/**
	 * 设置页面内容截取的后标志，从第一个匹配到的开始截取并在prefix之后
	 * 
	 * @param prefix
	 */
	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}
	
	/**
	 * 设置页面的编码
	 * 
	 * @param contentCharSet
	 */
	public void setContentCharSet(String contentCharSet) {
		this.contentCharSet = contentCharSet;
	}
	
	/**
	 * 当使用缓存时的更新周期，单位分钟，最小1
	 * 
	 * @param period
	 */
	public void setPeriod(int period) {
		this.period = Math.max(period, 1) * 60 * 1000;
	}
	
	/**
	 * 设置连接超时时间，单位毫秒，默认5000
	 * 
	 * @param timeoutInMilliseconds
	 */
	public void setTimeoutInMilliseconds(int timeoutInMilliseconds) {
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}

	/**
	 * 额外的代码，在clipping的前面
	 * 
	 * @param additionalHTML
	 */
	public void setAdditionalPreHTML(String additionalPreHTML) {
		this.additionalPreHTML = additionalPreHTML;
	}
	
	/**
	 * 额外的代码，在clipping的后面
	 * 
	 * @param additionalSufHTML
	 */
	public void setAdditionalSufHTML(String additionalSufHTML) {
		this.additionalSufHTML = additionalSufHTML;
	}

	private String getURL(long memberId, long loginAccountId) {
		if (Strings.isNotBlank(url)) {
			return url;
		}

		if (this.urlBuilder != null) {
			try {
				return this.urlBuilder.builder(memberId, loginAccountId);
			}
			catch (Exception e) {
				log.error("得到地址[" + this.name + "]", e);
			}
		}

		return null;
	}

	public String getIcon() {
		return icon;
	}

	public String getId() {
		return id;
	}

	protected String getName(Map<String, String> preference) {
		return name;
	}

	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	protected BaseSectionTemplete projection(Map<String, String> preference) {
		User user = CurrentUser.get();
		
		h.setShowBottomButton(false);
		h.setModel(HtmlTemplete.ModelType.inner);
		
		String content = "";
		
		String uri = this.getURL(user.getId(), user.getLoginAccount());
		if(Strings.isNotBlank(this.startTag) && Strings.isNotBlank(this.endTag)){
			//使用了缓存
			boolean isNeedUpdate = useCache && !cache.containsKey(uri); //当前缓存不包含
			
			//更新周期到了
			if(!isNeedUpdate && period > 0 && System.currentTimeMillis() - this.lastUpdateTime > this.period){
				isNeedUpdate = true;
			}
			
			if(isNeedUpdate){
				content = updateRemoteContent(uri);
				
		        if(this.useCache){
		        	cache.put(uri, content);
		        	lastUpdateTime = System.currentTimeMillis();
		        }
			}
			else{
				content = this.cache.get(uri);
			}
		}
		
		h.setHtml(this.additionalPreHTML + content + additionalSufHTML);

		return h;
	}

	/**
	 * 更新内容
	 * 
	 * @param uri
	 */
	private synchronized String updateRemoteContent(String uri) {
		if(Strings.isNotBlank(uri)){
			HttpClientUtil httpClientUtil = new HttpClientUtil(timeoutInMilliseconds);
			Reader reader = null;
			InputStream in = null;
			try {
				httpClientUtil.open(uri, "get");
				httpClientUtil.send();
				in = httpClientUtil.getResponseBodyAsStream();
				
				BufferedInputStream bis = new BufferedInputStream(in);
				bis.mark(BLOCK_SIZE);
				
				reader = new InputStreamReader(bis, contentCharSet);
		        BufferedReader br = new BufferedReader(reader);
		        StringBuffer sb = new StringBuffer();
		        String line = null;
		        while ((line = br.readLine()) != null) {
		        	sb.append(line);
		        }
		        
		        return substringBetween(sb.toString(), this.startTag, this.endTag);
			}
			catch (Exception e) {
				log.error("", e);
			}
			finally{
				if(in != null){
					try {
						in.close();
					}
					catch (IOException e) {
					}
				}
				
				if(reader != null){
					try {
						reader.close();
					}
					catch (IOException e) {
					}
				}
				
				httpClientUtil.close();
			}
		}
		
		return "";
	}
	
    private static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return "";
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1 && start < end) {
                return str.substring(start, end + close.length());
            }
        }
        
        return "";
    }

}
