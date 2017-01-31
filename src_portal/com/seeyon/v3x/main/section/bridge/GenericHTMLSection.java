/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.util.Map;

import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;

/**
 * 通用的HTMl栏目
 * 
 * <pre>
 *  &lt;bean id=&quot;BaiduNewsSection2&quot; class=&quot;com.seeyon.v3x.main.section.bridge.GenericHTMLSection&quot; init-method=&quot;init&quot;&gt;
 *  	&lt;property name=&quot;id&quot; value=&quot;BaiduNewsSection2&quot; /&gt;
 *  	&lt;property name=&quot;name&quot; value=&quot;财经新闻&quot; /&gt;
 *  	&lt;property name=&quot;html&quot;&gt;
 *  		&lt;value&gt;&lt;![CDATA[
 *  		&lt;style type=text/css&gt; div{font-size:12px;font-family:arial}.baidu{font-size:14px;line-height:24px;font-family:arial} a,a:link{color:#0000cc;}
 *  		.baidu span{color:#6f6f6f;font-size:12px} a.more{color:#008000;}a.blk{color:#000;font-weight:bold;}&lt;/style&gt;
 * 		 &lt;script language=&quot;JavaScript&quot; type=&quot;text/JavaScript&quot; src=&quot;http://news.baidu.com/n?cmd=1&amp;class=finannews&amp;pn=1&amp;tn=newsbrofcu&amp;rn=5&quot;&gt;&lt;/script&gt;
 *  		]]&gt;&lt;/value&gt;
 *  	&lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-11
 */
public class GenericHTMLSection extends BaseSection {

	private HtmlTemplete h = new HtmlTemplete();

	private String id;

	private String icon;

	private String name;
	
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
	 * 要显示的HTML代码
	 * 
	 * @param html
	 */
	public void setHtml(String html) {
		this.h.setHtml(html);
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
		h.setShowBottomButton(false);
		h.setModel(HtmlTemplete.ModelType.inner);
		
		return h;
	}

}
