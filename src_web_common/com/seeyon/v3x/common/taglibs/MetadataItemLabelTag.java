/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import java.io.IOException;
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
 * 该标签库是为了显示被原数据管理的项的名称<br>
 * 
 * 如: 协同的重要程度，在列表中需要显示“重要、一般、非常重要“等文本，在modol中只是业务值（1,2,3等） <br>
 * 这要把业务值分别翻译成对应的文本
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-6
 */
public class MetadataItemLabelTag extends BodyTagSupport {

	private static final long serialVersionUID = -2777257688219756479L;

	private Metadata metadata;

	private String value;

	private LocalizationContext bundleAttrValue;

	public void init() {
		metadata = null;
		value = null;
		bundleAttrValue = null;
	}

	public int doEndTag() throws JspException {
		if (metadata != null && value != null) {
			List<MetadataItem> items = metadata.getItems();

			if (items != null) {
				for (MetadataItem item : items) {
					if (item.getValue().equals(value)) {
						String label = item.getLabel();

						if (this.bundleAttrValue != null) {
							label = ResourceBundleUtil.getString(
									bundleAttrValue, label);
						}
						else if(Strings.isNotBlank(this.metadata.getResourceBundle())){
							label = ResourceBundleUtil.getString(this.metadata.getResourceBundle(), label);
						}
						else if(this.bundleAttrValue == null || label == null){
							label = ResourceBundleUtil.getString(pageContext,
									label);
						}
						if(null!=item.getOutputSwitch() && item.getOutputSwitch().intValue() == Constants.METADATAITEM_SWITCH_DISABLE){
							label = "";
						}
						try {
							JspWriter out = pageContext.getOut();
							out.print(label);
						}
						catch (IOException e) {
							throw new JspTagException(e.toString(), e);
						}

						break;
					}
				}
			}
		}

		init();
		return super.doEndTag();
	}

	@Override
	public void release() {
		init();
		super.release();
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public void setValue(String value) {
		this.value = value;
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
}
