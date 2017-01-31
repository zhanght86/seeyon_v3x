/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Constants;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.util.Strings;

/**
 * 元数据项
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-11-7
 */
public class MetadataItemTag extends BodyTagSupport {

	private static final long serialVersionUID = -5225713189568216263L;

	public static final String TAG_NAME = "metadataItem";

	private String showType;

	private Metadata metadata;

	private Object selected;
	
	private Object enclude;

	private String name;

	private Integer newline;
	
	private LocalizationContext bundleAttrValue;
	
	private String switchType; //启停设置类型（录入启停，查询启停）

	List<String> selectedList = new ArrayList<String>();

	List<String> encludeList = new ArrayList<String>();
	
	private List<MetadataItem> itemList;
	
	private Boolean optionValueUseId;
	
	public void setOptionValueUseId(Boolean optionValueUseId) {
		this.optionValueUseId = optionValueUseId;
	}

	public MetadataItemTag() {
		init();
	}

	public void init() {
		showType = null;
		metadata = null;
		selected = null;
		enclude = null;
		newline = 0;
		bundleAttrValue = null;
		switchType = null;
		optionValueUseId = false;
		selectedList.clear();
		encludeList.clear();
	}

	public void setItemList(List<MetadataItem> itemList) {
		this.itemList = itemList;
	}

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			if (Constants.METADATA_SHOW_TYPE.checkbox.name().equals(showType)) {
				out.println(this.toCheckbox());
			}
			else if (Constants.METADATA_SHOW_TYPE.option.name().equals(showType)) {
				out.println(this.toSelecetOptions());
			}
			else if (Constants.METADATA_SHOW_TYPE.radio.name().equals(showType)) {
				out.println(this.toRadio());
			}
		}
		catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}

		init();
		return super.doEndTag();
	}

	@Override
	public void release() {
		init();
		super.release();
	}

	/**
	 * 下拉选择框模式
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String toSelecetOptions() {
		if (metadata == null && itemList == null) {
			return "";
		}

		this.getSelected();
		
		StringBuffer sb = new StringBuffer();

		List<MetadataItem> itms = null;
		boolean showId = false;
		if(itemList != null){
			itms = itemList;
			showId = true;
		}else{
			itms = metadata.getItems();
		}

		for (MetadataItem item : itms) {
			//根据前台传回的类型，判断是否要过滤掉改枚举项
			if(Strings.isBlank(switchType) && (null!=item.getState() && item.getState().intValue() == Constants.METADATAITEM_SWITCH_DISABLE)){
				continue;
			}else if(!Strings.isBlank(switchType) && (null!=item.getOutputSwitch() && item.getOutputSwitch().intValue() == Constants.METADATAITEM_SWITCH_DISABLE)){
				continue;
			}
			String value = optionValueUseId?item.getId().toString():item.getValue();
			if(encludeList.contains(value)){
				continue;
			}
			String label = item.getLabel();

			label = getLabel(label);

			sb.append("<option value='" + value + "'");
			if (!selectedList.isEmpty() && selectedList.remove(value)) {
				sb.append(" selected");
			}
			sb.append(" itemName='" + item.getName() + "'");

			sb.append(">");
			sb.append(label);
			sb.append("</option>\n");
		}

		return sb.toString();
	}

	/**
	 * 转换成多选模式
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String toCheckbox() {
		if (metadata == null && itemList == null) {
			return "";
		}
		
		this.getSelected();

		StringBuffer sb = new StringBuffer();
		
		List<MetadataItem> itms = null;
		
		if(itemList != null){
			itms = itemList;
		}else{
			itms = metadata.getItems();
		}

		int i = 0;
		for (MetadataItem item : itms) {
			//根据前台传回的类型，判断是否要过滤掉改枚举项
			if(null!=item.getState() && item.getState().intValue() == Constants.METADATAITEM_SWITCH_DISABLE){
				continue;
			}
			String value = optionValueUseId?item.getId().toString():item.getValue();
			if(encludeList.contains(value)){
				continue;
			}
			String label = item.getLabel();

			label = getLabel(label);

			sb.append("<div class='metadataItemDiv'>");
			sb.append("<label for='" + name + i + "'>");
			sb.append("<input type='checkbox' name='" + name + "' value='"
					+ value + "' id='" + name + i + "'");

			if (!selectedList.isEmpty() && selectedList.remove(value)) {
				sb.append(" checked");
			}

			sb.append(">" + label + "</label>");
			sb.append("</div>\n");

			i++;

			if (newline > 0 && i % newline == 0) {
				sb.append("<br/>");
			}
		}

		return sb.toString();
	}

	/**
	 * 转换成radio模式
	 * 
	 * @return
	 */
	private String toRadio() {
		if (metadata == null && itemList == null) {
			return "";
		}
		this.getSelected();
		
		StringBuffer sb = new StringBuffer();

		List<MetadataItem> itms = null;
		if(itemList != null){
			itms = itemList;
		}else{
			itms = metadata.getItems();
		}

		int i = 0;
		for (MetadataItem item : itms) {
			//根据前台传回的类型，判断是否要过滤掉改枚举项
			if(null!=item.getState() && item.getState().intValue() == Constants.METADATAITEM_SWITCH_DISABLE){
				continue;
			}
			String value = optionValueUseId?item.getId().toString():item.getValue();
			if(encludeList.contains(value)){
				continue;
			}
			String label = item.getLabel();

			label = getLabel(label);

			sb.append("<div class='metadataItemDiv'>");
			sb.append("<label for='" + name + i + "'>");
			sb.append("<input type='radio' name='" + name + "' value='" + value
					+ "' id='" + name + i + "'");

			if (selected != null && value.equals(String.valueOf(selected))) {
				sb.append(" checked");
			}

			sb.append(">" + label + "</label>");
			sb.append("</div>\n");

			i++;

			if (newline > 0 && i % newline == 0) {
				sb.append("<br/>");
			}
		}

		return sb.toString();
	}

	/**
	 * 
	 * 
	 */
	private void getSelected() {
		getList(selected,selectedList);
		getEnclude();
	}
	private void getEnclude(){
		getList(enclude,encludeList);
	}
	private void getList(Object resource ,List<String> result){
		result.clear();
		if (resource != null) {
			if (resource instanceof Collection) {
				Collection c = (Collection) resource;
				for (Object object : c) {
					result.add(object.toString());
				}
			}
			else if (resource instanceof Object[]) {
				Object[] c = (Object[]) resource;

				for (Object object : c) {
					result.add(object.toString());
				}
			}
			else if (resource instanceof Object) {
				result.add(resource.toString());
			}
		}
	}
	
	private String getLabel(String key){
		if(!Strings.isI18NKey(key)){
			return key;
		}
		
		String label = null;
		if(this.bundleAttrValue != null){ //指定语言
			label = ResourceBundleUtil.getString(bundleAttrValue, key);
		}
		else if(this.metadata != null && Strings.isNotBlank(this.metadata.getResourceBundle())){ //在原数据中定义了resourceBundle
			label = ResourceBundleUtil.getString(this.metadata.getResourceBundle(), key);
		}

		if(label == null || key.equals(label)){ //采用当前资源
			label = ResourceBundleUtil.getString(pageContext, key);
		}
		
		if(label == null){
			return key;
		}
		
		return label;
	}

	public void setMetadata(Metadata _metadata) {
		this.metadata = _metadata;
	}

	public void setSelected(Object selected) {
		this.selected = selected;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public void setName(String _name) {
		this.name = _name;
	}

	public void setNewline(int newline) {
		this.newline = newline;
	}
	
	/**
	 * 先从指定的资源中查找，再查找默认的
	 * 
	 * @param locCtxt
	 * @throws JspTagException
	 */
    public void setBundle(LocalizationContext locCtxt) throws JspTagException {
        this.bundleAttrValue = locCtxt;
    }
    
    public void setSwitchType(String switchType){
    	this.switchType = switchType;
    }

	public void setEnclude(Object enclude) {
		this.enclude = enclude;
	}
}
