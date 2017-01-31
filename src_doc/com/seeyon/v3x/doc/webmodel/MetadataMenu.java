package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.util.Constants;

/**
 * 元数据定义vo
 */
public class MetadataMenu {
	private DocMetadataDefinition metadataDef;
	// 国际化key
	private String key;
	// 是否只读
	private boolean readOnly;
	private String name;
	// 内容类型关联
	private DocTypeDetail detail;
	
	 // 是否可空
	private boolean nullable;
	
	// 該元數據是否當前用戶所建
	private boolean creater;	
	//创建单位
	private String orgName;
	
	// 是否已经使用
	private boolean used;
	
	public boolean getUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public boolean getCreater() {
		return creater;
	}
	public void setCreater(boolean creater) {
		this.creater = creater;
	}
	public String getOrgName(){
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public DocMetadataDefinition getMetadataDef() {
		return metadataDef;
	}
	public void setMetadataDef(DocMetadataDefinition metadataDef) {
		this.metadataDef = metadataDef;
		if(metadataDef != null){
			this.name = metadataDef.getName();
//			String resName = Constants.getResourceNameOfMetadata(metadataDef.getCategory());
//			if(!"".equals(resName)){
//				this.name = ResourceBundleUtil.getString(resName, this.name);
//				if(this.name.equals(metadataDef.getName()) && !resName.equals(Constants.COMMON_RESOURCE_BASENAME))
//					this.name = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, this.name);
//			}
			
			this.name = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, this.name);
			if(this.name.equals(metadataDef.getName())){
				String resName = Constants.getResourceNameOfMetadata(metadataDef.getName(), "");
				if(!"".equals(resName))
					this.name = ResourceBundleUtil.getString(resName, this.name);
			}
		}

		this.creater = (metadataDef.getDomainId().longValue() == CurrentUser.get().getLoginAccount());
		this.used = (metadataDef.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_PUBLISHED);
	}
	public boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public DocTypeDetail getDetail() {
		return detail;
	}
	public void setDetail(DocTypeDetail detail) {
		this.detail = detail;
		this.nullable = detail.getNullable();
	}
	public boolean getNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
}
