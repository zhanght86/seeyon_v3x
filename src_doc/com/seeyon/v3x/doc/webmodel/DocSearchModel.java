package com.seeyon.v3x.doc.webmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.util.Strings;

/**
 * 文档组合查询模型，包括简单属性以及关联属性的任意组合所得查询条件
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-12-16
 */
public class DocSearchModel {
	/**
	 * 基本属性（文档主表字段）
	 */
	private List<SimpleDocQueryModel> simplePropertyQueries;
	/**
	 * 扩展属性，通过元数据信息关联，数据存储在doc_meta_data表中
	 */
	private List<SimpleDocQueryModel> metaDataQueries;
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		if(CollectionUtils.isNotEmpty(simplePropertyQueries)) {
			ret.append("基本属性：" + simplePropertyQueries.toString());
		}
		
		if(CollectionUtils.isNotEmpty(metaDataQueries)) {
			ret.append("关联属性：" + metaDataQueries.toString());
		}
		return ret.toString();	
	}
	
	public List<SimpleDocQueryModel> getSimplePropertyQueries() {
		return simplePropertyQueries;
	}
	public void setSimplePropertyQueries(List<SimpleDocQueryModel> simplePropertyQueries) {
		this.simplePropertyQueries = simplePropertyQueries;
	}
	public List<SimpleDocQueryModel> getMetaDataQueries() {
		return metaDataQueries;
	}
	public void setMetaDataQueries(List<SimpleDocQueryModel> metaDataQueries) {
		this.metaDataQueries = metaDataQueries;
	}
	
	public DocSearchModel() {}

	public DocSearchModel(List<SimpleDocQueryModel> simplePropertyQueries, List<SimpleDocQueryModel> metaDataQueries) {
		super();
		this.simplePropertyQueries = simplePropertyQueries;
		this.metaDataQueries = metaDataQueries;
	}
	
	public DocSearchModel(SimpleDocQueryModel sdm) {
		super();
		if(sdm.isSimple())
			this.simplePropertyQueries = Arrays.asList(sdm);
		else
			this.metaDataQueries = Arrays.asList(sdm);
	}
	
	/**
	 * 判断组合查询模型是否有效：至少包含一个简单属性查询或关联属性查询
	 * @return	组合查询模型是否有效
	 */
	public boolean isValid() {
		return CollectionUtils.isNotEmpty(this.simplePropertyQueries) || CollectionUtils.isNotEmpty(this.metaDataQueries);
	}
	
	/**
	 * 将用户请求信息解析为简单属性、高级属性组合查询模型
	 * @param request	用户请求
	 */
	public static DocSearchModel parseRequest(HttpServletRequest request) {
		String propertyNameAndTypes = request.getParameter("propertyNameAndTypes");
		if(Strings.isNotBlank(propertyNameAndTypes)) {
			String[] nameAndTypes = StringUtils.split(propertyNameAndTypes, ',');
			if(nameAndTypes != null && nameAndTypes.length > 0) {
				List<SimpleDocQueryModel> simplePropertyDms = new ArrayList<SimpleDocQueryModel>();
				List<SimpleDocQueryModel> metaDataDms = new ArrayList<SimpleDocQueryModel>();
				for(String s : nameAndTypes) {
					SimpleDocQueryModel sdm = SimpleDocQueryModel.parseRequest(s, request);
					if(sdm.isSimple()) {
						simplePropertyDms.add(sdm);
					}
					else {
						metaDataDms.add(sdm);
					}
				}
				return new DocSearchModel(simplePropertyDms, metaDataDms);
			}
		}
		return null;
	}
	
}
