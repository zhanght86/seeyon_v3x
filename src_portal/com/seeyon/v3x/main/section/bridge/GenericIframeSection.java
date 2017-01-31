/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.thirdparty.UrlBuilder;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.util.Strings;

/**
 * 一个通用的<code>Iframe</code>栏目，不需要写任何Java代码，只需配置一个Bean 
 * <pre>
 *  &lt;bean id=&quot;BaiduNewsSection&quot; class=&quot;com.seeyon.v3x.main.section.bridge.GenericIframeSection&quot; init-method=&quot;init&quot;&gt;
 *    &lt;property name=&quot;id&quot; value=&quot;BaiduNewsSection&quot; /&gt;
 *    &lt;property name=&quot;name&quot; value=&quot;Baidu新闻&quot; /&gt;
 *    &lt;property name=&quot;url&quot; value=&quot;http://news.baidu.com&quot; /&gt;
 *    &lt;property name=&quot;frameborder&quot; value=&quot;1&quot; /&gt;
 *    &lt;property name=&quot;scrolling&quot; value=&quot;yes&quot; /&gt;
 *    &lt;property name=&quot;height&quot; value=&quot;438px&quot; /&gt;
 *    &lt;property name=&quot;spaceTypes&quot;&gt;
 *      &lt;list&gt;
 *        &lt;value&gt;personal&lt;/value&gt;
 *        &lt;value&gt;corporation&lt;/value&gt;
 *      &lt;/list&gt;
 *    &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-11
 */
public class GenericIframeSection extends BaseSection {
	
	private static final Log log = LogFactory.getLog(GenericIframeSection.class);

	private String id;

	private String icon;

	private String name;
	
	private String url;
	
	private String scrolling = "no";

	private String frameborder = "0";

	private String height = "100%";

	private UrlBuilder urlBuilder;

	/**
	 * 设置栏目显示的名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

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
	 * Iframe的地址
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

	/**
	 * 边框，默认为0
	 * 
	 * @param frameborder
	 */
	public void setFrameborder(String frameborder) {
		this.frameborder = frameborder;
	}

	/**
	 * 滚动条，默认“no”
	 * 
	 * @param scrolling
	 */
	public void setScrolling(String scrolling) {
		this.scrolling = (scrolling);
	}

	/**
	 * iframe的高度，默认100%,如果采用像素，最大是438px
	 * 
	 * @param height
	 */
	public void setHeight(String height) {
		this.height = (height);
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
		
		IframeTemplete h = new IframeTemplete();
		h.setFrameborder(frameborder);
		h.setHeight(height);
		h.setScrolling(scrolling);
		
		h.setUrl(this.getURL(user.getId(), user.getLoginAccount()));
		
		return h;
	}

	public class IframeTemplete extends BaseSectionTemplete implements
			Serializable {
		private static final long serialVersionUID = 4099640074509477521L;

		private String url;

		private String scrolling = "no";

		private String frameborder = "0";

		private String height = "100%";

		public String getResolveFunction() {
			return "iframeTemplete";
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public String getFrameborder() {
			return frameborder;
		}

		public void setFrameborder(String frameborder) {
			this.frameborder = frameborder;
		}

		public String getScrolling() {
			return scrolling;
		}

		public void setScrolling(String scrolling) {
			this.scrolling = scrolling;
		}

		public String getHeight() {
			return height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

	}

}
