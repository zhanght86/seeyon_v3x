/**
 * 
 */
package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;

/**
 * 直接输出HTML代码片断
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-8
 */
public class HtmlTemplete extends BaseSectionTemplete implements Serializable {

	private static final long serialVersionUID = -1149557318578101854L;

	/**
	 * HTML的输出模式，默认<code>block</code>
	 */
	public static enum ModelType {
		/**
		 * 直接输出，采用<i>innerHTML</i>模式
		 */
		block,

		/**
		 * 采用<i>Iframe</i>内嵌模式，所有链接采用<i>_blank</i>
		 */
		inner,
	}

	private String html;

	private String model = ModelType.inner.name();
	
	private String height = "208";

	@Override
	public String getResolveFunction() {
		return "htmlTemplete";
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public String getModel() {
		return model;
	}

	public void setModel(HtmlTemplete.ModelType model) {
		this.model = model.name();
	}

	public String getHeight() {
		return height;
	}

	/**
	 * 页面高度，默认234，不要加px
	 * @param height
	 */
	public void setHeight(String height) {
		this.height = height;
	}
	
}
