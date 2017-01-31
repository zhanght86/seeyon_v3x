package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 处理扩展元数据的页面输出
 */
public final class HtmlUtil {
	
	private static final Log log = LogFactory.getLog(HtmlUtil.class);
	
	private OrgManager orgManager;
	private ContentTypeManager contentTypeManager;
	private MetadataDefManager metadataDefManager;
	private DocMetadataManager docMetadataManager;
	private MetadataManager metadataManager;
	
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}	
	
	/**
	 * 取得某个内容类型的扩展元数据的新建页面
	 */
	public String getNewHtml(long contentTypeId) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<TABLE width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
		sb.append("align=\"center\" cellpadding=\"0\" class=\"ellipsis\">");
		
		List<DocTypeDetail> details = contentTypeManager.getContentTypeDetails(contentTypeId);		
		if (details != null && details.size() > 0) {
			for (int i = 0; i < details.size(); i++) {	
				DocTypeDetail detail = details.get(i);
				if (detail != null) {
					DocMetadataDefinition metadataDef = detail.getDocMetadataDefinition();		
					if (metadataDef == null) {						
						metadataDef = metadataDefManager.getMetadataDefById(detail.getMetadataDefId());
					}
					String s = getNewHtmlStr(metadataDef, detail);					
					sb.append(s);	
				}
			}
		}
		sb.append("</TABLE>");
		return sb.toString();
	}
	/**
	 * 取得某个内容类型的扩展元数据的修改页面
	 */
	public String getEditHtml(long docResId, boolean readonly) {
		StringBuffer sb = new StringBuffer();

		sb.append("<TABLE width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
		sb.append("align=\"center\" cellpadding=\"0\" style=\"word-break:break-all;word-wrap:break-word\">");
		DocResource docResource = docMetadataManager.getDocResourceDetail(docResId);		
		if (docResource != null) {
			long contentTypeId = docResource.getFrType();
			List<DocTypeDetail> details = contentTypeManager.getContentTypeDetails(contentTypeId);
			if (details != null && details.size() > 0) {
				for (int i = 0; i < details.size(); i++) {					
					DocMetadataDefinition metadataDef = details.get(i).getDocMetadataDefinition();
					Object value = docResource.getMetadataByDefId(metadataDef.getId());
					boolean _readonly = details.get(i).getReadOnly();
					_readonly = (readonly || _readonly);
					String s=getEditHtmlStr(metadataDef, value, _readonly, details.get(i), docResource.getFrType());
					if(i%5 == 0 && i != 0 && i != details.size()) {
						sb.append("<TR height=\"4\">");
						sb.append("<td colspan = \"3\">") ;
						sb.append("<hr></hr>") ;
						sb.append("<TR>") ;
					}
					sb.append(s);					
				}
			}
		}
		sb.append("<TR height=\"1\"><TD align=\"right\" width=\"23%\">&nbsp;</td><td width=\"2%\">&nbsp;</TD><TD>&nbsp;</TD></TR>");

		sb.append("</TABLE>");
		return sb.toString();
	}

	
	/**
	 * 取得某个内容类型的扩展元数据的查看页面
	 */
	public String getViewHtml(long docResId) {
		DocResource docResource = docMetadataManager.getDocResourceDetail(docResId);
		return this.getMetaDataHtml(docResource);
	}
	
	/**
	 * 取得文件历史版本某个内容类型的扩展元数据的查看页面
	 */
	public String getHistoryViewHtml(Long docVersionId) {
		DocResource dr = docMetadataManager.getDocVersionInfoDetail(docVersionId);
		return this.getMetaDataHtml(dr);
	}
	
	private String getMetaDataHtml(DocResource dr) {
		StringBuffer sb = new StringBuffer();
		sb.append("<TABLE width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
		sb.append(" style=\"word-break:break-all;word-wrap:break-word\">");
		
		if (dr != null) {
			long contentTypeId = dr.getFrType();
			List<DocTypeDetail> details = contentTypeManager.getContentTypeDetails(contentTypeId);
			if (details != null && details.size() > 0) {
				for (int i = 0; i < details.size(); i++) {	
					DocMetadataDefinition metadataDef = details.get(i).getDocMetadataDefinition();
					Object value = dr.getMetadataByDefId(metadataDef.getId());
					String s = getViewHtmlStr(metadataDef, value);
					sb.append(s);
				}
			}
		}
		sb.append("</TABLE>");
		return sb.toString();
		
	}
	
	private String getNewHtmlStr(DocMetadataDefinition metadataDef, DocTypeDetail detail) {
		StringBuffer sb = new StringBuffer();
		String showName = metadataDef.getName();
		String resourceName = Constants.getResourceNameOfMetadata(metadataDef.getName(), "");

		String fieldName = metadataDef.getPhysicalName();
		boolean nullable = detail.getNullable();
		boolean readonly = false;
		byte type = metadataDef.getType();
		String defaultValue = metadataDef.getDefaultValue()==null?"":metadataDef.getDefaultValue().trim();
		
		showName = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, showName);
		if(showName.equals(metadataDef.getName())){
			if(!"".equals(resourceName))
				showName = ResourceBundleUtil.getString(resourceName, showName);
		}
		
		if(type == Constants.DATE || type == Constants.DATETIME){
			sb.append(this.getNewHtmlOfDateAndDatetime(metadataDef, type == Constants.DATETIME, showName, detail));
		}
		else {					
			sb.append("<TR>");
			if (nullable) {
				sb.append("<TD align=\"right\" width=\"23%\">"+StringEscapeUtils.escapeHtml(showName)+":</td><td width=\"2%\">&nbsp;</TD>");
			}		
			else {
				sb.append("<TD align=\"right\" width=\"23%\"><font color=\"red\">*</font>"+StringEscapeUtils.escapeHtml(showName)+":</td><td width=\"2%\">&nbsp;</TD>");
			}	
			sb.append("<TD>");			
			switch (type) {
				case Constants.TEXT_ONE_LINE:
					sb.append("<input type=\"text\"  advance=\"docAdvance\"  size=\"52\" name=\""+fieldName+"\" value=\"" + defaultValue + "\"");
					if(!nullable)
						sb.append(" validate=\"notNull\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + StringEscapeUtils.escapeHtml(showName) + "\"");
					else
						sb.append(" validate=\"maxLength\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + StringEscapeUtils.escapeHtml(showName) + "\"");
					sb.append(">");
					break;
				case Constants.TEXT:
					sb.append("<textarea valign=\"top\" advance=\"docAdvance\"  rows=\"4\" cols=\"54\"  name=\""+fieldName+"\"");	
					if(!nullable)
						sb.append(" validate=\"notNull\" maxSize=\"10000\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + StringEscapeUtils.escapeHtml(showName) + "\"");
					else
						sb.append(" validate=\"maxLength\" maxSize=\"10000\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + StringEscapeUtils.escapeHtml(showName) + "\"");
					sb.append(">" + defaultValue + "</textarea>");	
					break;
				case Constants.INTEGER:
					sb.append("<input type=\"text\" advance=\"docAdvance\"  size=\"52\" name=\""+fieldName+"\" value=\""+defaultValue+"\"");
					if(!nullable)
						sb.append(" validate=\"notNull,isNumber\" integerDigits=\"11\" integerMax=\"" + Integer.MAX_VALUE + "\" integerMin=\"" + Integer.MIN_VALUE + "\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"isNumber\" integerDigits=\"11\" integerMax=\"" + Integer.MAX_VALUE + "\" integerMin=\"" + Integer.MIN_VALUE + "\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">");
					sb.append(metadataDef.getIsPercent() ? "%" : "");
					break;
				case Constants.FLOAT:
					sb.append("<input type=\"text\" advance=\"docAdvance\"  size=\"52\" name=\""+fieldName+"\" value=\""+defaultValue+"\"");
					if(!nullable)
						sb.append(" validate=\"notNull,isNumber\" decimalDigits=\"6\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"isNumber\" decimalDigits=\"6\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">");
					sb.append(metadataDef.getIsPercent() ? "%" : "");
					break;
				case Constants.BOOLEAN:
					Boolean value = false;
					if (defaultValue != null && !defaultValue.equals("")) {
						try {
							value = Boolean.valueOf(defaultValue);
						}
						catch (Exception e) {	
							log.error("新建页面的扩展元数据的boolean类型：", e);
						}
					}
					sb.append(getHtmlForBoolean(metadataDef, value, showName, nullable,readonly));
					break;
				case Constants.ENUM:
					Long value1 = 0L;
					sb.append(getHtmlForEnum(metadataDef, value1, showName, nullable));
					break;
				case Constants.USER_ID:
					sb.append(this.getNewHtmlOfOrgType(metadataDef, V3xOrgEntity.ORGENT_TYPE_MEMBER, detail, showName));
					break;
				case Constants.DEPT_ID:
					sb.append(this.getNewHtmlOfOrgType(metadataDef, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, detail, showName));
					break;
			}
			sb.append("</TD>");
			sb.append("</TR>");
		}		
		return sb.toString();
	}
	
	private String getEditHtmlStr(DocMetadataDefinition metadataDef, Object value, boolean readonly, DocTypeDetail detail, long docType) {
		String defaultValue = metadataDef.getDefaultValue();
		String resourceName = Constants.getResourceNameOfMetadata(detail.getName(), (value == null)?"":value.toString());
		StringBuffer sb = new StringBuffer();
		String showName = detail.getName();
		showName = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, showName);
		if(showName.equals(detail.getName())){
			if(!"".equals(resourceName))
				showName = ResourceBundleUtil.getString(resourceName, showName.trim());
		}		
		
		String fieldName = metadataDef.getPhysicalName();
		boolean nullable = detail.getNullable();
		byte type = metadataDef.getType();
		value = value == null?"":value;
		
		// 需要调用元数据组件
		MetadataNameEnum mne = Constants.getMetadataNameEnum(detail.getName(), value == null?"":value.toString(), docType);
		if(mne != null){
			value = metadataManager.getMetadataItemLabel(mne, value.toString());
			value = value == null?"":value;
		}
		
		
		
		if(!"".equals(resourceName)){
			value = ResourceBundleUtil.getString(resourceName, value.toString());
		}	
		
		value = value == null?"":value;
		
		sb.append("<TR>");
		if (nullable) {
			sb.append("<TD align=\"right\" width=\"23%\" valign=\"top\">"+StringEscapeUtils.escapeHtml(showName)+":</td><td width=\"2%\">&nbsp;</TD>");
		}		
		else {
			sb.append("<TD align=\"right\" width=\"23%\"><font color=\"red\">*</font>"+StringEscapeUtils.escapeHtml(showName)+":</td><td width=\"2%\">&nbsp;</TD>");
		}
		sb.append("<TD  valign=\"top\">");	
		
		if (readonly) {	
			if(type == Constants.INTEGER && metadataDef.getIsPercent()){
				if(value == null || value.toString().equals("")){
					
				}else{
					int intValue = 0;
					try {
						intValue = (Integer)value;
					} catch (Exception e) {
						log.error("文档扩展元数据的int型转换：", e);
					}
					sb.append(intValue);
				}
					
				sb.append("%");
				
			}else if(type == Constants.FLOAT && metadataDef.getIsPercent()){
				if(value == null || value.toString().equals("")){
					
				}else{
					double dValue = 0D;
					try {
						dValue = (Double)value;
					} catch (Exception e) {
						log.error("文档扩展元数据的double型转换：", e);
					}
					sb.append(dValue);
				}
					
				sb.append("%");
				
			}else if(type == Constants.DATETIME){
				if(value.toString().length() > 16)
				sb.append(value.toString().substring(0, 16));
			}else if(type == Constants.USER_ID){
				String name = "";
				try{
					if(Strings.isNotBlank(value.toString())){
					V3xOrgMember member = orgManager.getMemberById(NumberUtils.toLong(value.toString()));
					if(member != null)
						name = member.getName();}
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}finally{
					sb.append(name);
				}
			}else if(type == Constants.DEPT_ID){
				String name = "";
				try{
					if(Strings.isNotBlank(value.toString())){
					V3xOrgDepartment dept = orgManager.getDepartmentById(NumberUtils.toLong(value.toString()));
					if(dept != null)
						name = dept.getName();}
				}catch(Exception e){
					log.error("编辑扩展元数据的dept_id类型：", e);
				}finally{
					sb.append(name);
				}
			}else if(type == Constants.ENUM){
				Set<DocMetadataOption> options = metadataDef.getMetadataOption();
				if (options != null && !options.isEmpty()) {
					Iterator<DocMetadataOption> iterator = options.iterator();
					while (iterator.hasNext()) {
						DocMetadataOption option = iterator.next();
						if (option.getId().toString().equals(value.toString())) {
							sb.append(option.getOptionItem());
							break;
						}
					}
				}
			} else if(type==Constants.BOOLEAN) {
				sb.append(getHtmlForBoolean(metadataDef, value, showName, nullable ,true));
		    } else {
		    	value = Strings.toHTML(value.toString());
		    	sb.append(value);
		    }
			
			sb.append("<input type=\"hidden\" value=\"" + value + "\" name=\"" + metadataDef.getPhysicalName() +"\">");
				
		}else if ((type == Constants.DATE || type == Constants.DATETIME) && defaultValue != null && defaultValue.trim().equals("1")){
//			if(Strings.isNotBlank(value.toString())){
//				sb.append(Datetimes.formateToLocaleDate((Date)value));
//				sb.append("<input type=\"hidden\" value=\"" + value + "\" name=\"" + metadataDef.getPhysicalName() +"\">");
//			}else{
//				sb.append("");
//				sb.append("<input type=\"hidden\" value=\"" + new Date() + "\" name=\"" + metadataDef.getPhysicalName() +"\">");
//			}
			sb.append(this.getEditHtmlOfDateAndDatetime(metadataDef, type == Constants.DATETIME, value, showName, detail));
		}
//		else if (type == Constants.DATETIME && defaultValue != null && defaultValue.trim().equals("1")){
//			if(Strings.isBlank(value.toString())){
//				sb.append("");
//				sb.append("<input type=\"hidden\" value=\"" + new Timestamp(System.currentTimeMillis()).toString().substring(0, 16) + "\" name=\"" + metadataDef.getPhysicalName() +"\">");
//			}else{
//				sb.append(Datetimes.formateToLocaleDatetime((Date)value));
//				String temp = "";
//				if(value.toString().length() > 16)
//					temp = value.toString().substring(0, 16);
//				sb.append("<input type=\"hidden\" value=\"" + temp + "\" name=\"" + metadataDef.getPhysicalName() +"\">");
//			}
//		}
		else {
			switch (type) {
				case Constants.TEXT_ONE_LINE:				
					sb.append("<input type=\"text\" size=\"52\" name=\""+fieldName+"\" value=\"" + value + "\"");
					sb.append(" onchange=\"userChange('ucfProp')\"");
					if(!nullable)
						sb.append(" validate=\"notNull\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"maxLength\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">");
					break;
				case Constants.TEXT:				
					sb.append("<textarea valign=\"top\" rows=\"4\" cols=\"54\"  name=\""+fieldName+"\"");
					sb.append(" onchange=\"userChange('ucfProp')\"");
					if(!nullable)
						sb.append(" validate=\"notNull\" maxSize=\"10000\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"maxLength\" maxSize=\"10000\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">" + value + "</textarea>");				
					break;
				case Constants.INTEGER:				
					sb.append("<input type=\"text\" size=\"52\" name=\""+fieldName+"\" value=\"" 
							+ value + "\"");
					sb.append(" onchange=\"userChange('ucfProp')\"");
					if(!nullable)
						sb.append(" validate=\"notNull,isNumber\" integerDigits=\"11\" integerMax=\"" + Integer.MAX_VALUE + "\" integerMin=\"" + Integer.MIN_VALUE + "\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"isNumber\" integerDigits=\"11\" integerMax=\"" + Integer.MAX_VALUE + "\" integerMin=\"" + Integer.MIN_VALUE + "\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">");
					sb.append(metadataDef.getIsPercent() ? "%" : "");
					break;
				case Constants.FLOAT:
					sb.append("<input type=\"text\" size=\"52\" name=\""+fieldName+"\" value=\"" + value + "\"");
					sb.append(" onchange=\"userChange('ucfProp')\"");
					if(!nullable)
						sb.append(" validate=\"notNull,isNumber\" decimalDigits=\"6\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					else
						sb.append(" validate=\"isNumber\" decimalDigits=\"6\"  inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + showName + "\"");
					sb.append(">");
					sb.append(metadataDef.getIsPercent() ? "%" : "");
					break;
				case Constants.DATE:
					sb.append(this.getEditHtmlOfDateAndDatetime(metadataDef, false, value, showName, detail));
					break;
				case Constants.DATETIME:
					sb.append(this.getEditHtmlOfDateAndDatetime(metadataDef, true, value, showName, detail));
					break;
				case Constants.BOOLEAN:
					sb.append(getHtmlForBoolean(metadataDef, value, showName, nullable,false));				
					break;
				case Constants.ENUM:
					sb.append(getHtmlForEnum(metadataDef, value, showName, nullable));
					break;
				case Constants.USER_ID:
					sb.append(this.getEditHtmlOfOrgType(metadataDef, V3xOrgEntity.ORGENT_TYPE_MEMBER, value, showName, detail));
					break;
				case Constants.DEPT_ID:
					sb.append(this.getEditHtmlOfOrgType(metadataDef, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, value, showName, detail));
					break;
			}
		}
		sb.append("</TD>");
		sb.append("</TR>");		
		return sb.toString();
	}
	
	// lihf: 利用系统的时间组件实现Date、Datetime类型的Html组件
	/**
	 * @param includeTime 是否显示时间
	 */
	private String getNewHtmlOfDateAndDatetime(DocMetadataDefinition metadataDef, boolean includeTime, 
			String showName, DocTypeDetail detail){
		StringBuffer sb = new StringBuffer();
		
		String defaultValue = metadataDef.getDefaultValue();
		String value = "";
		if (defaultValue != null && defaultValue.trim().equals("1")) {	
			value = includeTime ? Datetimes.formatDatetime(new Date()) : Datetimes.formatDate(new Date());
		}
			// 定义为取当前时间
//			sb.append("<TR height=\"0\" class=\"hidden\"><td align=\"right\" width=\"23%\">&nbsp;</td><td width=\"2%\">&nbsp;</td><TD><input type=\"hidden\" value=\"" + Datetimes.formateToLocaleDatetime(new Date()) + "\" name=\"" 
//					+ metadataDef.getPhysicalName() +"\"></TD></TR>");
//		}else{
			//
			sb.append("<TR>");
			if (detail.getNullable())
				sb.append("<TD align=\"right\" width=\"23%\">" + showName 
						+ ":</td><td width=\"2%\">&nbsp;</TD>");
			else 
				sb.append("<TD align=\"right\" width=\"23%\"><font color=\"red\">*</font>" 
						+ showName + ":</td><td width=\"2%\">&nbsp;</TD>");

			sb.append("<TD>");	
			sb.append("<input type=\"text\"  readonly=\"readonly\" size=\"52\" name=\"" + metadataDef.getPhysicalName() 
					+ "\" value=\"" + value
					+ "\"	onclick=\"whenstart('" + "/seeyon" + "',this,300,200,'" 
					+ (includeTime ? "datetime" : "date") + "');\"");
			if(!detail.getNullable())
				sb.append(" validate=\"notNull\" advance=\"docAdvance\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
						+ ":" + showName + "\"");
			else
				sb.append(" validate=\"maxLength\" advance=\"docAdvance\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
						+ ":" + showName + "\"");
			sb.append("></TD></TR>");
//		}
	
		return sb.toString();
	}
	private String getEditHtmlOfDateAndDatetime(DocMetadataDefinition metadataDef, 
			boolean includeTime, Object value, String showName, DocTypeDetail detail){
		StringBuffer sb = new StringBuffer();
		
		String temp = "";
		if(value.toString().length() > 16)
			temp = value.toString().substring(0, 16);

		sb.append("<input type=\"text\" readonly=\"readonly\" size=\"52\" name=\"" + metadataDef.getPhysicalName() 
				+ "\" value=\"" 
				+ ((value == null || value.toString().equals("")) ? "" : (includeTime ? temp : value.toString()))
				+ "\" onclick=\"whenstart('" + "/seeyon" + "',this,300,200,'" 
				+ (includeTime ? "datetime" : "date") + "');userChangeCalendar(this,'" 
				+ ((value == null || value.toString().equals("")) ? "" : (includeTime ? temp : value.toString()))
				+ "',true)\"");
		if(!detail.getNullable())
			sb.append(" validate=\"notNull\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		else
			sb.append(" validate=\"maxLength\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		sb.append(">");
	
		return sb.toString();
	}
	
	/**
	 * lihf: 利用选人组件实现memberId,departmentId类型Html组件
	 */
	private String getEditHtmlOfOrgType(DocMetadataDefinition metadataDef, 
			String orgType, Object value, String showName, DocTypeDetail detail){
		StringBuffer sb = new StringBuffer();

		// 显示选人结果的input
		String oldValue = "";
		if(value != null && !value.toString().equals("")){
			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
				oldValue = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, NumberUtils.toLong(value.toString()), false);
			}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType)){
				oldValue = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, NumberUtils.toLong(value.toString()), false);
			}
		}
		
		sb.append("<input type=\"text\"  size=\"52\" readonly=\"readonly\" name=\"name_" + metadataDef.getPhysicalName() 
				+ "\" id=\"name_" + metadataDef.getPhysicalName() + "\" value='" + oldValue
				+ "' onclick=\"sp_fun('" + metadataDef.getPhysicalName() + "','" + orgType + "');\" ");
		if(!detail.getNullable())
			sb.append(" validate=\"notNull\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		else
			sb.append(" validate=\"maxLength\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		sb.append(" />");
		// 记录选人结果id的hidden
		sb.append("<input type=\"hidden\"  name=\"" + metadataDef.getPhysicalName() 
				+ "\" id=\"" + metadataDef.getPhysicalName() + "\" value=\"" + value + "\" />");
	
		return sb.toString();
	}
	private String getNewHtmlOfOrgType(DocMetadataDefinition metadataDef, String orgType,
			DocTypeDetail detail, String showName){
		StringBuffer sb = new StringBuffer();

		// 显示选人结果的input
		String defaultValue = metadataDef.getDefaultValue();
		try {
			if(defaultValue != null && !defaultValue.equals("")){
				if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
					V3xOrgMember member = orgManager.getMemberById(NumberUtils.toLong(defaultValue));
					if(member != null)
						defaultValue = member.getName();
					else
						defaultValue = "";
				}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType)){
					V3xOrgDepartment dept = orgManager.getDepartmentById(NumberUtils.toLong(defaultValue));
					if(dept != null)
						defaultValue = dept.getName();
					else
						defaultValue = "";
				}
			}else{
				defaultValue = "";
			}
		} catch (BusinessException e) {
			log.error("从orgManager取得实体：", e);
		}
		sb.append("<input type=\"text\"  size=\"52\" readonly=\"readonly\" name=\"name_" + metadataDef.getPhysicalName() 
				+ "\" id=\"name_" + metadataDef.getPhysicalName() + "\" value='" + defaultValue
				+ "' onclick=\"sp_fun('" + metadataDef.getPhysicalName() + "','" + orgType + "');\" ");
		if(!detail.getNullable())
			sb.append(" validate=\"notNull\" advance=\"docAdvance\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		else
			sb.append(" validate=\"maxLength\" advance=\"docAdvance\" maxSize=\"255\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") 
					+ ":" + showName + "\"");
		sb.append(" />");
		// 记录选人结果id的hidden
		sb.append("<input type=\"hidden\"  name=\"" + metadataDef.getPhysicalName() 
				+ "\" id=\"" + metadataDef.getPhysicalName() + "\" value=\"\" />");
	
		return sb.toString();
	}
	
	public String getViewHtmlStr(DocMetadataDefinition metadataDef, Object value) {
		StringBuffer sb = new StringBuffer();
		
		
		String resourceName = Constants.getResourceNameOfMetadata(metadataDef.getName(), (value == null)?"":value.toString());
		String showName = metadataDef.getName();		
		showName = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, showName);
		if(showName.equals(metadataDef.getName())){
			if(!"".equals(resourceName))
				showName = ResourceBundleUtil.getString(resourceName, showName);
		}
		
		byte type = metadataDef.getType();		
		sb.append("<TR>");
		sb.append("<TD align=\"right\" width=\"48%\" valign=\"top\">"+StringEscapeUtils.escapeHtml(showName)+":</td><td width=\"2%\">&nbsp;</td>");
		sb.append("<TD>");
		if (value != null) {
			String s = "";
			switch (type) {
				case Constants.TEXT_ONE_LINE:	
					sb.append(StringEscapeUtils.escapeHtml(value.toString()));
					break;
				case Constants.TEXT:	
					sb.append(StringEscapeUtils.escapeHtml(value.toString()));
					break;
				case Constants.INTEGER:				
					Integer i = 0;
					try {
						i = (Integer)value;
					}
					catch (Exception e1) {
						log.error("查看页面的扩展元数据的int类型：", e1);
					}
					if (metadataDef.getIsPercent()) {
						sb.append(i + "&nbsp;%");
					}
					else {
						sb.append(i);
					}			
					break;
				case Constants.FLOAT:	
					Double f = 0D;
					try {
						f = (Double)value;
					}
					catch (Exception e1) {
						log.error("查看页面的扩展元数据的double类型：", e1);
					}
					if (metadataDef.getIsPercent()) {
						sb.append(f + "&nbsp;%");
					}
					else {
						sb.append(f);
					}					
					break;
				case Constants.DATE:		
					try {
						s = Datetimes.formateToLocaleDate((Date)value);
					}
					catch (Exception e2) {
						log.error("查看页面的扩展元数据的date类型：", e2);
					}
					sb.append(s);				
					break;
				case Constants.DATETIME:		
					try {
						s = Datetimes.formateToLocaleDatetime((Date)value);
					}
					catch (Exception e3) {
						log.error("查看页面的扩展元数据的datetime类型：", e3);
					}
					sb.append(s);
					break;
				case Constants.BOOLEAN:
					Boolean flag = false;
					try {
						flag = (Boolean)value;
					}
					catch (Exception e4) {
						log.error("查看页面的扩展元数据的boolean类型：", e4);
					}
					if (flag) {
						s = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.yes");
					}
					else {
						s = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.no");
					}
					sb.append(s);
					break;
				case Constants.ENUM:
					Long enumId = 0L;
					try {
						enumId = NumberUtils.toLong(value.toString());
					}
					catch (Exception e5) {
						log.error("查看页面的扩展元数据的enum类型：", e5);
					}
					Set<DocMetadataOption> options = metadataDef.getMetadataOption();
					if (options != null && !options.isEmpty()) {
						Iterator<DocMetadataOption> iterator = options.iterator();
						while (iterator.hasNext()) {
							DocMetadataOption option = iterator.next();	
							if (option.getId().longValue() == enumId.longValue()) {
								s = option.getOptionItem();
								break;
							}
						}
					}
					sb.append(s);
					break;
				case Constants.USER_ID:
					Long memberId = 0L;
					try {		
						memberId = (Long)value;
						s = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId, false);
					}
					catch (Exception e6) {
						log.error("查看页面的扩展元数据的userid类型：", e6);
					}
					sb.append(s);
					break;
				case Constants.DEPT_ID:
					Long deptId = 0L;
					try {		
						deptId = (Long)value;
						s = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, deptId, false);
					}
					catch (Exception e7) {
						log.error("查看页面的扩展元数据的deptId类型：", e7);
					}
					sb.append(s);
					break;
			}
		}
		else {
			if(Constants.BOOLEAN == type){
				String s = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.no");		
				sb.append(s);
			}else
				sb.append("&nbsp;");
		}
		sb.append("</TD>");
		sb.append("</TR>");	
		return sb.toString();
	}
	
	private String getHtmlForEnum(DocMetadataDefinition metadataDef, Object value, String showName, boolean nullable) {
		StringBuffer sb = new StringBuffer();
		long _value = 0;
		if (value != null) {
			try {
				_value = NumberUtils.toLong(value.toString());
			}
			catch (Exception e) {					
				log.error("扩展元数据的enum类型：", e);
			}
		}
		sb.append("<select " + (!nullable ? "validate=\"notNull\"" : "") + " advance=\"docAdvance\" inputName=\"" + Constants.getDocI18nValue("doc.jsp.propEdit.title") + ":" + StringEscapeUtils.escapeHtml(showName) + "\" name=\""+metadataDef.getPhysicalName()+"\" style=\"width:292px\" onchange=\"userChange('ucfProp')\" >");
		if (metadataDef.getType() == Constants.ENUM) {
			Set<DocMetadataOption> options = metadataDef.getMetadataOption();
			if (options != null && !options.isEmpty()) {
				sb.append("<option value=\""+""+"\" title=\"" + "" + "\"></option>");
				Iterator<DocMetadataOption> iterator = options.iterator();
				while (iterator.hasNext()) {
					DocMetadataOption option = iterator.next();
					if (option.getId().longValue() == _value) {
						sb.append("<option value=\""+option.getId()+"\" title=\"" + Functions.toHTMLWithoutSpace(option.getOptionItem()) + "\" selected>" + option.getOptionItem() + "</option>");
					}
					else {
						sb.append("<option value=\""+option.getId()+"\" title=\"" + Functions.toHTMLWithoutSpace(option.getOptionItem()) + "\">" + option.getOptionItem() + "</option>");
					}
				}
			}
		}
		sb.append("</select>");
		return sb.toString();
	}
	
	private String getHtmlForBoolean(DocMetadataDefinition metadataDef, Object value, String showName, boolean nullable,boolean readonly) {
		StringBuffer sb = new StringBuffer();
		boolean b = false;
		if(!readonly){
			if(value != null && ("1".equals(value.toString()) || "true".equals(value.toString())))
				b = true;
			
			if (b) {
				
				sb.append("<select name=\""+metadataDef.getPhysicalName()
						+"\" width='200px' onchange=\"userChange('ucfProp')\""
						+">");
				sb.append("<option value = '1' checked>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.true")).append("</option>") ;
				sb.append("<option value = '0'>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.false")).append("</option>") ;
				sb.append("</select>") ;
			}
			else {
				sb.append("<select name=\""+metadataDef.getPhysicalName()
						+"\"  width='200px' onchange=\"userChange('ucfProp')\""
						+">");
				sb.append("<option value = '0' checked>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.false")).append("</option>") ;
				sb.append("<option value = '1'>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.true")).append("</option>") ;
				sb.append("</select>") ;
			}
		}else{
			if(value != null && ("1".equals(value.toString()) || "true".equals(value.toString())))
				b = true;
			if (b) {
				sb.append("<select disabled=\"disabled\" name=\""+metadataDef.getPhysicalName()
						+"\"  width='200px' onchange=\"userChange('ucfProp')\"" 
						+">");
				sb.append("<option value = '1' checked>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.true")).append("</option>") ;
				sb.append("<option value = '0'>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.false")).append("</option>") ;
				sb.append("</select>") ;
			}
			else {
				sb.append("<select disabled=\"disabled\" name=\""+metadataDef.getPhysicalName()
						+"\"  width='200px' onchange=\"userChange('ucfProp')\"" 
						+">");
				sb.append("<option value = '0' checked>").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.false")).append("</option>") ;
				sb.append("<option value = '1' >").append(ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.true")).append("</option>") ;			
				sb.append("</select>") ;
			}	
		}
	    sb.append("</TD>") ;
		return sb.toString();
	}
	
}