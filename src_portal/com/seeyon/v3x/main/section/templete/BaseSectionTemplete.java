package com.seeyon.v3x.main.section.templete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * 栏目显示模板基础类，所有模板必须继承该类
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-11
 */
public abstract class BaseSectionTemplete extends ObjectToXMLBase implements
		Serializable {
	public static final String BOTTOM_BUTTON_LABEL_MORE = "common_more_label";
	public static final String BOTTOM_BUTTON_NEW = "new_doc";
	public static final String BOTTOM_BUTTON_LABEL_BOOKMANAGEMENT = "book_manage_label";
	public static final String BOTTOM_BUTTON_LABEL_AGENT = "common_agent_pending_label";
	
	public static enum OPEN_TYPE {
		openWorkSpace, //弹出 满工作区
		openWorkSpaceRight, //弹出 只占用右边工作区
		href, //直接超链
		href_blank, //直接超链，在新窗口打开
	}

	private List<BaseSectionTemplete.BottomButton> bottomButtons;
	
	/**
	 * 是否显示底部的更多按钮行
	 */
	private Boolean showBottomButton = true;
	
	private List<BaseSectionTemplete.Panel> panels;

	/**
	 * 取得解析该模板的JS方法
	 * 
	 * <pre>
	 *  function showMultiRowFourColumnTemplete(result){
	 *   ...
	 *  }
	 * </pre>
	 * 
	 * @return JS-function名，不要括号和参数 ,如：showMultiRowFourColumnTemplete
	 */
	public abstract String getResolveFunction();
	
	
	public boolean getShowBottomButton() {
		return showBottomButton;
	}

	/**
	 * 是否显示底部的更多按钮行
	 * 
	 * @param showBottomButton
	 */
	public void setShowBottomButton(boolean showBottomButton){
		this.showBottomButton = showBottomButton;
	}

	/**
	 * 添加页签 显示在栏目标题下面的页签
	 * @param id
	 *    页签的id，区别多个页签的唯一标记，并且当选中该页签后，使用
	 *    preference.get(PropertyName.panelId.name())取到的值为这个id。
	 * @param subject
	 *    页签名称。
	 * @param total
	 */
	public void addPanel(String id,String subject, Integer total){
		if(panels == null){
			panels = new ArrayList<BaseSectionTemplete.Panel>();
		}
		panels.add(new Panel(id, subject, total));
	}
	
	/**
	 * 增加底部按钮
	 *  
	 * @param label
	 *            显示的文字（国际化key），资源文件统一放在在/apps_res/v3xmain/js/i18n<br>
	 *            <i>"更多"</i>的key为<code>BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE</code>
	 * @param link
	 *            链接地址：直接是/*.do?method=...，如果是JS事件，请用javascript:开头
	 * @return
	 */
	public void addBottomButton(String label, String link) {
		this.addBottomButton(label, link, null);
	}
	public void addBottomButton(String label, String link, String target) {
		if (bottomButtons == null) {
			bottomButtons = new ArrayList<BottomButton>();
		}

		BaseSectionTemplete.BottomButton b = new BaseSectionTemplete.BottomButton(label, link, target);

		bottomButtons.add(b);
	}

	public List<BaseSectionTemplete.BottomButton> getBottomButtons() {
		return bottomButtons;
	}

	/**
	 * 栏目下面的按钮，如：更多等等
	 * 
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-5-15
	 */
	public class BottomButton extends ObjectToXMLBase implements Serializable {

		private static final long serialVersionUID = -2544446041074029469L;

		private String label;

		private String link;
		
		private String target;

		public BottomButton(String label, String link) {
			this.label = label;
			this.link = link;
		}
		
		public BottomButton(String label, String link, String target) {
			super();
			this.label = label;
			this.link = link;
			this.target = target;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getLabel() {
			return label;
		}

		public String getLink() {
			return link;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}
		
	}
	
	private List<SectionResource> sectionResources = null;
	
	/**
	 * 添加js文件。
	 * @param id js在页面中 唯一id
	 * @param url js地址 由于加载外部系统js会有跨域问题。这里只支持放到seeyon下的文件
	 */
	public void addJavaScript(String id,String url){
		addResource(id, url, ResourceType.JS);
	}
	
	/**
	 * 添加css文件
	 * @param id css 标签在页面中唯一的id。如果有相同的存在，就不加载了。
	 * @param url css 文件地址。
	 */
	public void addCss(String id,String url){
		addResource(id, url, ResourceType.CSS);
	}
	
	public void addResource(String id,String url,ResourceType type){
		if(sectionResources == null){
			sectionResources = new ArrayList<SectionResource>();
		}
		SectionResource res = new SectionResource(id,url,type.name());
		sectionResources.add(res);
	}
	
	public List<SectionResource> getSectionResources() {
		return sectionResources;
	}

	/**
	 * 加载栏目自己的资源
	 * @author dongyj
	 *
	 */
	public static enum ResourceType{
		JS,//js文件
		CSS,//CSS 文件
	};
	/**
	 * 栏目自己的资源加载<br>
	 * 加载js或css
	 */
	public class SectionResource extends ObjectToXMLBase implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -1763117605826193273L;
		
		public SectionResource(){	
		}
		
		public SectionResource(String id,String url,String type){
			this.id = id;
			this.url = url;
			this.type = type;
		}
		
		/**
		 * 资源的id
		 */
		private String id;
		
		/**
		 * 资源类型
		 */
		private String type;

		/**
		 * 地址
		 * 此处填写的地址为绝对路径
		 * 例如 :/apps_res/v3xmain/js/section.js
		 */
		private String url;

		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
	}
	/**
	 * 栏目页签。
	 * @author dongyj
	 *
	 */
	public class Panel extends ObjectToXMLBase implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7691569102308233128L;
		
		public Panel(String panelId,String subject, Integer total){
			this.subject = subject;
			this.panelId = panelId;
			this.total = total;
		}
		
		private String subject;
		
		private String panelId;
		
		private Integer total;

		public String getPanelId() {
			return panelId;
		}

		public void setPanelId(String panelId) {
			this.panelId = panelId;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public Integer getTotal() {
			return total;
		}

		public void setTotal(Integer total) {
			this.total = total;
		}
		
	}

	public List<BaseSectionTemplete.Panel> getPanels() {
		return panels;
	}

}