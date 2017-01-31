package com.seeyon.v3x.edoc.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Projections;

import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.EdocEnum.MarkCategory;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocElementFlowPermAcl;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocElementFlowPermAclManager;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocMarkDefinitionManager;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class XMLConverter {
	
	private static final Log log = LogFactory.getLog(XMLConverter.class);
	
	private EdocElementFlowPermAclManager edocElementFlowPermAclManager;	
	
	private MetadataManager metadataManager;
	
	private EdocMarkDefinitionManager edocMarkDefinitionManager;
	
	private EdocElementManager edocElementManager;
	
	
	private OrgManager orgManager;
	private EdocFormManager edocFormManager = null;
	private TempleteManager templeteManager;
	
	
	public EdocFormManager getEdocFormManager(){
		if(edocFormManager == null){
			 edocFormManager = (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		}
		return edocFormManager;
	}
	public TempleteManager getTempleteManager() {
		return templeteManager;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public EdocMarkDefinitionManager getEdocMarkDefinitionManager() {
		return edocMarkDefinitionManager;
	}
	public void setEdocMarkDefinitionManager(
			EdocMarkDefinitionManager edocMarkDefinitionManager) {
		this.edocMarkDefinitionManager = edocMarkDefinitionManager;
	}
	public EdocElementFlowPermAclManager getEdocElementFlowPermAclManager()
	{
		return this.edocElementFlowPermAclManager;
	}
	public void setEdocElementFlowPermAclManager(EdocElementFlowPermAclManager edocElementFlowPermAclManager)
	{
		this.edocElementFlowPermAclManager=edocElementFlowPermAclManager;
	}
	
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	/**
	 * 根据EdocSummary数据,公文单上元素组合XML数据
	 * @param elements  :公文单中包含的公文元素
	 * @param edocSummary:公文数据
	 * @param actorId:公文处理权限ID,如果actorId<0,默认为编辑
	 * @return
	 */
	public StringBuffer convert(List <EdocFormElement>elements,EdocSummary edocSummary,long actorId, int edocType) {
		return convert(elements,edocSummary,actorId,edocType,false,false);
	}

	/**
	 * 根据EdocSummary数据,公文单上元素组合XML数据
	 * @param elements  :公文单中包含的公文元素
	 * @param edocSummary:公文数据
	 * @param actorId:公文处理权限ID,如果actorId<0,默认为编辑
	 * @param isTemplete : 是否是公文模板新建或者修改页面
	 * @param isNoShowOriginalDocMark : 是否不显示当前问号，调用模板的时候不显示
	 * @return
	 */
	public StringBuffer convert(List <EdocFormElement>elements,EdocSummary edocSummary,long actorId, int edocType,boolean isTemplete,boolean isNoShowOriginalDocMark) {
		 /** 
		 * 这个方法可能用到的地方:
		 * 拟文;
		 * 客户端处理公文的时候公文单的展现页面;
		 * 单位管理员,公文应用设置,公文模板列表,点开列表中一条数据的详细页面;
		 * 单位管理员,公文应用设置,新建/修改公文模板;
		 * 单位管理员,公文应用设置,上传公文单;
		 * 修改这个方法的时候一定要测试以上五个地方.*/
		Hashtable <Long,EdocElementFlowPermAcl> actorsAcc=edocElementFlowPermAclManager.getEdocElementFlowPermAclsHs(actorId);
		//V3xOrgMember user = edocSummary.getStartMember() ;
		Long orgAccountId = null ;
		Long templeteAccountId  =  null;
		Long fromAccountId = null;
		if(edocSummary != null) {
			Long edocFormId = edocSummary.getFormId();
			if(edocFormId!=null){
				EdocForm ef  = getEdocFormManager().getEdocForm(edocFormId);
				if(ef!=null){
					fromAccountId = ef.getDomainId();
				}
			}
			orgAccountId = edocSummary.getOrgAccountId() ;
			templeteAccountId  = EdocHelper.getFlowPermAccountId(edocSummary, orgAccountId, templeteManager);
		}
		if(fromAccountId==null){
			fromAccountId = templeteAccountId;
		}
		EdocFormElement formElement = null;
		//动态拼接XML第一部分,如果是文本框,在第一部分赋值,如果类型为下拉列表,在第二部分赋值
		StringBuffer xml = new StringBuffer("");
		String topHeader = "&&&&&&&&  xsl_start  &&&&&&&& Url=view1.xsl &&&&&&&&  data_start  &&&&&&&&";
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String bodyHeader = "<my:myFields xmlns:my=\"www.seeyon.com/form/2007\">";
		String bodyEnder   = "</my:myFields>";
		String leftBracket = "<";
		String domainName  = "my:";
		String rightBracket = ">";
		String bias  = "/";
		String fieldName = "";
		String fieldValue = "";
		String fieldType="text";
		StringBuffer xmlBody = new StringBuffer("");
		
		//动态拼接XML第二部分,所有元素的格式在这里定义
		StringBuffer inputBody = new StringBuffer(""); 
		
		String inputHeader = "&&&&&&&&  input_start  &&&&&&&&";
		String fieldStart = "<FieldInputList>";
		String fieldEnd   = "</FieldInputList>";
		String fieldInput = "<FieldInput name=\"";
		String fieldInputEnd = "</FieldInput>";		
		String display = "<Input display=\"";
		String displayValue = "\" value=\"";
		String displayEnd = "\"/>";
		String name = "";
		String dbFieldType="varchar";
		
		EdocElementFlowPermAcl elementAcl=null;
		for(int i=0;i<elements.size();i++){
			fieldName="";
			fieldType="text";
			fieldValue="";
			dbFieldType="";
			MetadataItem metaDataItem = null;
			List<MetadataItem> metaItem = null;
			formElement = elements.get(i);
//          判断是否是签报单，是否有行文类型字段，有则跳出！			
			/*if(edocType == Constants.EDOC_FORM_TYPE_SIGN && formElement.getElementId() == 3){
				continue;
			}			*/
//			判断是否为处理意见
			if((formElement.getElementId()>=203 && formElement.getElementId()<=207) || (formElement.getElementId()>=281 && formElement.getElementId()<=290)){
				continue;
			}
			String access="browse";
			//权限actorId<0时,对公文元素的操作权限默认为编辑,用于建立模版
			//公文单中的元素没用早到对应的权限,默认为只读
			elementAcl=actorsAcc.get(formElement.getElementId());
			if(((actorId<0 && actorId>-100) 
					|| (elementAcl!=null && elementAcl.getAccess()==EdocElementFlowPermAcl.ACCESS_STATE.edit.ordinal()))
				&& formElement.getElementId()!=311){	//311附件公文元素只读
				access=EdocElementFlowPermAcl.ACCESS_STATE.edit.name();
			}
			String text = "\" type=\"text\" access=\""+access+"\" allowprint=\"true\" allowtransmit=\"true\" />";
			String select = "\" type=\"select\" access=\""+access+"\" allowprint=\"true\" allowtransmit=\"true\">";
			String textarea="\" type=\"textarea\" access=\""+access+"\" allowprint=\"true\" allowtransmit=\"true\" />";
			DecimalFormat df =  new DecimalFormat("###########0.####");
			if(formElement.getElementId()==1){
				fieldName = "subject";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getSubject()){
					if(edocSummary.getSubject().length()>30){
						fieldType="textarea";
						dbFieldType="longtext";						
					}
					fieldValue = edocSummary.getSubject();
				}
			}
			else if(formElement.getElementId()==2){
				fieldType="select";				
				fieldName = "doc_type";
				if(null!=edocSummary && !"".equals(edocSummary.getDocType())){
					fieldValue = edocSummary.getDocType();
				}
						metaItem =  metadataManager.getMetadataItems("edoc_doc_type");
						inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
						boolean bool = false;
						for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getDocType())&& (metaDataItem.getValue()).equals(edocSummary.getDocType())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
								bool = true;
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
						if(bool == false && null!=edocSummary && edocSummary.getDocType()!=null){
							MetadataItem newItem = metadataManager.getMetadataItem("edoc_doc_type", edocSummary.getDocType());
							if(null!=newItem){
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", newItem.getLabel());
							inputBody.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
							}
						}
					inputBody.append(fieldInputEnd);
			}
			else if(formElement.getElementId()==3){
				fieldType="select";
				fieldName = "send_type";
				if(null!=edocSummary && !"".equals(edocSummary.getSendType())){
					fieldValue = edocSummary.getSendType();
				}
		//		if( null!=edocSummary.getSendType()){
					metaItem =  metadataManager.getMetadataItems("edoc_send_type");
					inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					boolean bool = false;
					for(int x=0;x<metaItem.size();x++){
						metaDataItem = (MetadataItem)metaItem.get(x);
						if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
						if(null!=edocSummary && !"".equals(edocSummary.getSendType())&& (metaDataItem.getValue()).equals(edocSummary.getSendType())){
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							bool = true;
						}else{
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
						}
					}
					if(bool == false && null!=edocSummary && edocSummary.getSendType()!=null){
						MetadataItem newItem = metadataManager.getMetadataItem("edoc_send_type", edocSummary.getSendType());
						if(null!=newItem){
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", newItem.getLabel());
						inputBody.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
						}
					}
					inputBody.append(fieldInputEnd);
		//		}
			}
			else if(formElement.getElementId()==4){
				fieldName = "doc_mark";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getDocMark()){
					fieldValue = edocSummary.getDocMark();
				}
				
				if(null!=edocSummary && !"".equals(edocSummary.getEdocType()) && (EdocEnum.edocType.sendEdoc.ordinal() == edocSummary.getEdocType() || EdocEnum.edocType.signReport.ordinal() == edocSummary.getEdocType()))
				{
					addDocMarkListList(inputBody,fieldName,fieldValue,access,EdocEnum.MarkType.edocMark.ordinal(),orgAccountId,edocSummary,isTemplete,isNoShowOriginalDocMark);
				}
			}
			else if(formElement.getElementId()==21){
				fieldName = "doc_mark2";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getDocMark2()){
					fieldValue = edocSummary.getDocMark2();
				}
				
				if(null!=edocSummary && !"".equals(edocSummary.getEdocType()) && (EdocEnum.edocType.sendEdoc.ordinal() == edocSummary.getEdocType() || EdocEnum.edocType.signReport.ordinal() == edocSummary.getEdocType()))
				{			
					addDocMarkListList(inputBody,fieldName,fieldValue,access,EdocEnum.MarkType.edocMark.ordinal(),orgAccountId,edocSummary,isTemplete,isNoShowOriginalDocMark);
				}
			}
			else if(formElement.getElementId()==5){
				fieldName = "serial_no";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getSerialNo()){
					fieldValue = edocSummary.getSerialNo();
				}
				if( null!=edocSummary ){
					addDocMarkListList(inputBody,fieldName,fieldValue,access,EdocEnum.MarkType.edocInMark.ordinal(),orgAccountId,edocSummary,isTemplete,isNoShowOriginalDocMark);	
				}
			}
			else if(formElement.getElementId()==6){
				fieldType="select";
				fieldName = "secret_level";
				if(null!=edocSummary && !"".equals(edocSummary.getSecretLevel())){
				fieldValue = edocSummary.getSecretLevel();
				}
			//	if( null!=edocSummary.getSecretLevel()){
					metaItem =  metadataManager.getMetadataItems("edoc_secret_level");
					inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					boolean bool = false;//判断是否初始化过默认值，就是说是否是查看状态（非新建）
					for(int x=0;x<metaItem.size();x++){
						metaDataItem = (MetadataItem)metaItem.get(x);
						if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
						if(null!=edocSummary && !"".equals(edocSummary.getSecretLevel())&& (metaDataItem.getValue()).equals(edocSummary.getSecretLevel())){
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							bool = true;
						}else{
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
						}
					}
					//如果其中的字段不为空，并且没有处在新建状态的话
					if(bool == false && null!=edocSummary && edocSummary.getSecretLevel()!=null){
						MetadataItem newItem = metadataManager.getMetadataItem("edoc_secret_level", edocSummary.getSecretLevel());
						if(null!=newItem){
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", newItem.getLabel());
						inputBody.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
						}
					}
					inputBody.append(fieldInputEnd);
			//	}
			}
			else if(formElement.getElementId()==7){
				fieldType="select";
				fieldName = "urgent_level";
				if(null!=edocSummary && !"".equals(edocSummary.getUrgentLevel())){
					fieldValue = edocSummary.getUrgentLevel();
				}
		//		if( null!=edocSummary.getUrgentLevel()){
					metaItem =  metadataManager.getMetadataItems("edoc_urgent_level");
					inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					boolean bool = false;
					for(int x=0;x<metaItem.size();x++){
						metaDataItem = (MetadataItem)metaItem.get(x);
						if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
						if(null!=edocSummary && !"".equals(edocSummary.getUrgentLevel())&& (metaDataItem.getValue()).equals(edocSummary.getUrgentLevel())){
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							bool = true;
						}else{
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
						}
					}
					if(bool == false && null!=edocSummary && edocSummary.getUrgentLevel()!=null){
						MetadataItem newItem = metadataManager.getMetadataItem("edoc_urgent_level", edocSummary.getUrgentLevel());
						if(null!=newItem){
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", newItem.getLabel());
						inputBody.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
						}
					}
					inputBody.append(fieldInputEnd);
		//		}
			}
			else if(formElement.getElementId()==8){
				fieldType="select";
				fieldName = "keep_period";
				if(null!=edocSummary && edocSummary.getKeepPeriod()!=null){
					fieldValue = String.valueOf(edocSummary.getKeepPeriod());
				}
		//		if( null!=edocSummary.getKeepPeriod()){
					metaItem =  metadataManager.getMetadataItems("edoc_keep_period");
					inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					boolean bool = false;
					for(int x=0;x<metaItem.size();x++){
						metaDataItem = (MetadataItem)metaItem.get(x);
						if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
						if(null!=edocSummary && edocSummary.getKeepPeriod()!=null && edocSummary.getKeepPeriod().toString().equals(metaDataItem.getValue())){
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							bool = true;
						}else{
							inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
						}
					}
					if(bool == false && null!=edocSummary && edocSummary.getKeepPeriod()!=null){
						MetadataItem newItem = metadataManager.getMetadataItem("edoc_keep_period", edocSummary.getKeepPeriod().toString());
						if(null!=newItem){
						name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", newItem.getLabel());
						inputBody.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
						}
					}
					inputBody.append(fieldInputEnd);
		//		}
			}
			else if(formElement.getElementId()==9){
				fieldName = "create_person";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getCreatePerson()){
					fieldValue = edocSummary.getCreatePerson();
				}
			}
			else if(formElement.getElementId()==10){
				fieldName = "send_unit";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getSendUnit()){
					if(edocSummary.getSendUnit().length()>15){
						fieldType="textarea";
						dbFieldType="longtext";						
					}
					fieldValue = edocSummary.getSendUnit();
				}
			}
			else if(formElement.getElementId()==26){
				fieldName = "send_unit2";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getSendUnit2()){
					if(edocSummary.getSendUnit2().length()>15){
						fieldType="textarea";
						dbFieldType="longtext";						
					}
					fieldValue = edocSummary.getSendUnit2();
				}
			}
			else if(formElement.getElementId()==312){
				fieldType="textarea";
				fieldName = "send_department";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getSendDepartment()){
					fieldValue = edocSummary.getSendDepartment();
				}
			}
			else if(formElement.getElementId()==313){
				fieldType="textarea";
				fieldName = "send_department2";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getSendDepartment2()){
					fieldValue = edocSummary.getSendDepartment2();
				}
			}
			else if(formElement.getElementId()==11){
				fieldName = "issuer";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getIssuer()){
					fieldValue = edocSummary.getIssuer();
				}
			}
			else if(formElement.getElementId()==12){
				fieldName = "signing_date";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getSigningDate()){
					fieldValue = Datetimes.formatDate(edocSummary.getSigningDate());					
				}
			}
			else if(formElement.getElementId()==13){
				fieldType="textarea";
				fieldName = "send_to";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getSendTo()){
					fieldValue = edocSummary.getSendTo();
				}
			}
			else if(formElement.getElementId()==23){
				fieldType="textarea";
				fieldName = "send_to2";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getSendTo2()){
					fieldValue = edocSummary.getSendTo2();
				}
			}
			else if(formElement.getElementId()==14){
				fieldType="textarea";
				fieldName = "copy_to";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getCopyTo()){
					fieldValue = edocSummary.getCopyTo();
				}
			}
			else if(formElement.getElementId()==24){
				fieldType="textarea";
				fieldName = "copy_to2";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getCopyTo2()){
					fieldValue = edocSummary.getCopyTo2();
				}
			}
			else if(formElement.getElementId()==15){
				fieldType="textarea";
				fieldName = "report_to";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getReportTo()){
					fieldValue = edocSummary.getReportTo();
				}
			}
			else if(formElement.getElementId()==25){
				fieldType="textarea";
				fieldName = "report_to2";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getReportTo2()){
					fieldValue = edocSummary.getReportTo2();
				}
			}
			else if(formElement.getElementId()==16){
				fieldName = "keyword";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getKeywords()){
					fieldValue = edocSummary.getKeywords();
				}
			}
			else if(formElement.getElementId()==17){
				fieldName = "print_unit";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getPrintUnit()){
					fieldValue = edocSummary.getPrintUnit();
				}
			}
			else if(formElement.getElementId()==18){
				fieldName = "copies";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getCopies()){
					fieldValue = String.valueOf(edocSummary.getCopies());
				}
			}
			else if(formElement.getElementId()==22){
				fieldName = "copies2";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getCopies2()){
					fieldValue = String.valueOf(edocSummary.getCopies2());
				}
			}
			else if(formElement.getElementId()==19){
				fieldName = "printer";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getPrinter()){
					fieldValue = edocSummary.getPrinter();
				}
			}
			else if(formElement.getElementId()==201){
				fieldName = "createdate";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getStartTime()){
					fieldValue = Datetimes.formatDate(edocSummary.getStartTime());
				}
			}
			else if(formElement.getElementId()==202){
				fieldName = "packdate";
				fieldValue = "";				
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getPackTime()){
					fieldValue = Datetimes.formatDate(edocSummary.getPackTime());					
				}
			}
			else if(formElement.getElementId()==311){
				fieldType="textarea";
				fieldName = "attachments";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getAttachments()){
					fieldValue = edocSummary.getAttachments();
				}
			}
			/*
			if(formElement.getElementId()==31){
				fieldName = "shenhe";
				fieldValue = "";
			}
			if(formElement.getElementId()==32){
				fieldName = "shenpi";
				fieldValue = "";
			}
			if(formElement.getElementId()==33){
				fieldName = "huiqian";
				fieldValue = "";
			}
			if(formElement.getElementId()==34){
				fieldName = "qianfa";
				fieldValue = "";
			}
			if(formElement.getElementId()==35){
				fieldName = "fuhe";
				fieldValue = "";
			}
			if(formElement.getElementId()==36){
				fieldName = "yuedu";
				fieldValue = "";
			}
			if(formElement.getElementId()==37){
				fieldName = "niban";
				fieldValue = "";
			}
			if(formElement.getElementId()==38){
				fieldName = "piban";
				fieldValue = "";
			}
			if(formElement.getElementId()==39){
				continue;
				//fieldName = "banli";
				//fieldValue = "";
			}
			*/			
			else if(formElement.getElementId()==51){
				fieldName = "string1";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar1()){
					fieldValue = edocSummary.getVarchar1();
				}
			}
			else if(formElement.getElementId()==52){
				fieldName = "string2";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar2()){
					fieldValue = edocSummary.getVarchar2();
				}
			}
			else if(formElement.getElementId()==53){
				fieldName = "string3";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar3()){
					fieldValue = edocSummary.getVarchar3();
				}
			}
			else if(formElement.getElementId()==54){
				fieldName = "string4";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar4()){
					fieldValue = edocSummary.getVarchar4();
				}
			}
			else if(formElement.getElementId()==55){
				fieldName = "string5";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar5()){
					fieldValue = edocSummary.getVarchar5();
				}
			}
			else if(formElement.getElementId()==56){
				fieldName = "string6";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar6()){
					fieldValue = edocSummary.getVarchar6();
				}
			}
			else if(formElement.getElementId()==57){
				fieldName = "string7";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar7()){
					fieldValue = edocSummary.getVarchar7();
				}
			}
			else if(formElement.getElementId()==58){
				fieldName = "string8";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar8()){
					fieldValue = edocSummary.getVarchar8();
				}
			}
			else if(formElement.getElementId()==59){
				fieldName = "string9";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar9()){
					fieldValue = edocSummary.getVarchar9();
				}
			}
			else if(formElement.getElementId()==60){
				fieldName = "string10";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar10()){
					fieldValue = edocSummary.getVarchar10();
				}
			}
			else if(formElement.getElementId()==241){
				fieldName = "string11";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar11()){
					fieldValue = edocSummary.getVarchar11();
				}
			}
			else if(formElement.getElementId()==242){
				fieldName = "string12";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar12()){
					fieldValue = edocSummary.getVarchar12();
				}
			}
			else if(formElement.getElementId()==243){
				fieldName = "string13";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar13()){
					fieldValue = edocSummary.getVarchar13();
				}
			}
			else if(formElement.getElementId()==244){
				fieldName = "string14";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar14()){
					fieldValue = edocSummary.getVarchar14();
				}
			}
			else if(formElement.getElementId()==245){
				fieldName = "string15";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar15()){
					fieldValue = edocSummary.getVarchar15();
				}
			}
			else if(formElement.getElementId()==246){
				fieldName = "string16";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar16()){
					fieldValue = edocSummary.getVarchar16();
				}
			}
			else if(formElement.getElementId()==247){
				fieldName = "string17";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar17()){
					fieldValue = edocSummary.getVarchar17();
				}
			}
			else if(formElement.getElementId()==248){
				fieldName = "string18";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar18()){
					fieldValue = edocSummary.getVarchar18();
				}
			}
			else if(formElement.getElementId()==249){
				fieldName = "string19";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar19()){
					fieldValue = edocSummary.getVarchar19();
				}
			}
			else if(formElement.getElementId()==250){
				fieldName = "string20";
				fieldValue = "";
				dbFieldType="varchar";
				if( null!=edocSummary && null!=edocSummary.getVarchar20()){
					fieldValue = edocSummary.getVarchar20();
				}
			}
			else if(formElement.getElementId()==61){
				fieldType="textarea";
				fieldName = "text1";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText1()){
					fieldValue = edocSummary.getText1();
				}
			}
			else if(formElement.getElementId()==62){
				fieldType="textarea";
				fieldName = "text2";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText2()){
					fieldValue = edocSummary.getText2();
				}
			}
			else if(formElement.getElementId()==63){
				fieldType="textarea";
				fieldName = "text3";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText3()){
					fieldValue = edocSummary.getText3();
				}
			}
			else if(formElement.getElementId()==64){
				fieldType="textarea";
				fieldName = "text4";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText4()){
					fieldValue = edocSummary.getText4();
				}
			}
			else if(formElement.getElementId()==65){
				fieldType="textarea";
				fieldName = "text5";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText5()){
					fieldValue = edocSummary.getText5();
				}
			}
			else if(formElement.getElementId()==66){
				fieldType="textarea";
				fieldName = "text6";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText6()){
					fieldValue = edocSummary.getText6();
				}
			}
			else if(formElement.getElementId()==67){
				fieldType="textarea";
				fieldName = "text7";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText7()){
					fieldValue = edocSummary.getText7();
				}
			}
			else if(formElement.getElementId()==68){
				fieldType="textarea";
				fieldName = "text8";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText8()){
					fieldValue = edocSummary.getText8();
				}
			}
			else if(formElement.getElementId()==69){
				fieldType="textarea";
				fieldName = "text9";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText9()){
					fieldValue = edocSummary.getText9();
				}
			}
			else if(formElement.getElementId()==70){
				fieldType="textarea";
				fieldName = "text10";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText10()){
					fieldValue = edocSummary.getText10();
				}
			}
			else if(formElement.getElementId()==71){
				fieldName = "integer1";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger1()){
					fieldValue = String.valueOf(edocSummary.getInteger1());
				}
			}
			else if(formElement.getElementId()==72){
				fieldName = "integer2";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger2()){
					fieldValue = String.valueOf(edocSummary.getInteger2());
				}
			}
			else if(formElement.getElementId()==73){
				fieldName = "integer3";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger3()){
					fieldValue = String.valueOf(edocSummary.getInteger3());
				}
			}
			else if(formElement.getElementId()==74){
				fieldName = "integer4";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger4()){
					fieldValue = String.valueOf(edocSummary.getInteger4());
				}
			}
			else if(formElement.getElementId()==75){
				fieldName = "integer5";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger5()){
					fieldValue = String.valueOf(edocSummary.getInteger5());
				}
			}
			else if(formElement.getElementId()==76){
				fieldName = "integer6";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger6()){
					fieldValue = String.valueOf(edocSummary.getInteger6());
				}
			}
			else if(formElement.getElementId()==77){
				fieldName = "integer7";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger7()){
					fieldValue = String.valueOf(edocSummary.getInteger7());
				}
			}
			else if(formElement.getElementId()==78){
				fieldName = "integer8";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger8()){
					fieldValue = String.valueOf(edocSummary.getInteger8());
				}
			}
			else if(formElement.getElementId()==79){
				fieldName = "integer9";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger9()){
					fieldValue = String.valueOf(edocSummary.getInteger9());
				}
			}
			else if(formElement.getElementId()==80){
				fieldName = "integer10";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger10()){
					fieldValue = String.valueOf(edocSummary.getInteger10());
				}
			}
			else if(formElement.getElementId()==231){
				fieldName = "integer11";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger11()){
					fieldValue = String.valueOf(edocSummary.getInteger11());
				}
			}
			else if(formElement.getElementId()==232){
				fieldName = "integer12";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger12()){
					fieldValue = String.valueOf(edocSummary.getInteger12());
				}
			}
			else if(formElement.getElementId()==233){
				fieldName = "integer13";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger13()){
					fieldValue = String.valueOf(edocSummary.getInteger13());
				}
			}
			else if(formElement.getElementId()==234){
				fieldName = "integer14";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger14()){
					fieldValue = String.valueOf(edocSummary.getInteger14());
				}
			}
			else if(formElement.getElementId()==235){
				fieldName = "integer15";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger15()){
					fieldValue = String.valueOf(edocSummary.getInteger15());
				}
			}
			else if(formElement.getElementId()==236){
				fieldName = "integer16";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger16()){
					fieldValue = String.valueOf(edocSummary.getInteger16());
				}
			}
			else if(formElement.getElementId()==237){
				fieldName = "integer17";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger17()){
					fieldValue = String.valueOf(edocSummary.getInteger17());
				}
			}
			else if(formElement.getElementId()==238){
				fieldName = "integer18";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger18()){
					fieldValue = String.valueOf(edocSummary.getInteger18());
				}
			}
			else if(formElement.getElementId()==239){
				fieldName = "integer19";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger19()){
					fieldValue = String.valueOf(edocSummary.getInteger19());
				}
			}
			else if(formElement.getElementId()==240){
				fieldName = "integer20";
				fieldValue = "";
				dbFieldType="int";
				if(null!=edocSummary && null!=edocSummary.getInteger20()){
					fieldValue = String.valueOf(edocSummary.getInteger20());
				}
			}
			
			else if(formElement.getElementId()==81){
				fieldName = "decimal1";	
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal1()){
					fieldValue = df.format(edocSummary.getDecimal1());
				}
			}
			else if(formElement.getElementId()==82){
				fieldName = "decimal2";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal2()){
					fieldValue = df.format(edocSummary.getDecimal2());
				}
			}
			else if(formElement.getElementId()==83){
				fieldName = "decimal3";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal3()){
					fieldValue = df.format(edocSummary.getDecimal3());
				}
			}
			else if(formElement.getElementId()==84){
				fieldName = "decimal4";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal4()){
					fieldValue = df.format(edocSummary.getDecimal4());
				}
			}
			else if(formElement.getElementId()==85){
				fieldName = "decimal5";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal5()){
					fieldValue = df.format(edocSummary.getDecimal5());
				}
			}
			else if(formElement.getElementId()==86){
				fieldName = "decimal6";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal6()){
					fieldValue = df.format(edocSummary.getDecimal6());
				}
			}
			else if(formElement.getElementId()==87){
				fieldName = "decimal7";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal7()){
					fieldValue = df.format(edocSummary.getDecimal7());
				}
			}
			else if(formElement.getElementId()==88){
				fieldName = "decimal8";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal8()){
					fieldValue = df.format(edocSummary.getDecimal8());
				}
			}
			else if(formElement.getElementId()==89){
				fieldName = "decimal9";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal9()){
					fieldValue = df.format(edocSummary.getDecimal9());
				}
			}
			else if(formElement.getElementId()==90){
				fieldName = "decimal10";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal10()){
					fieldValue = df.format(edocSummary.getDecimal10());
				}
			}
			else if(formElement.getElementId()==251){
				fieldName = "decimal11";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal11()){
					fieldValue = df.format(edocSummary.getDecimal11());
				}
			}
			else if(formElement.getElementId()==252){
				fieldName = "decimal12";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal12()){
					fieldValue = df.format(edocSummary.getDecimal12());
				}
			}
			else if(formElement.getElementId()==253){
				fieldName = "decimal13";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal13()){
					fieldValue = df.format(edocSummary.getDecimal13());
				}
			}
			else if(formElement.getElementId()==254){
				fieldName = "decimal14";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal14()){
					fieldValue = df.format(edocSummary.getDecimal14());
				}
			}
			else if(formElement.getElementId()==255){
				fieldName = "decimal15";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal15()){
					fieldValue = df.format(edocSummary.getDecimal15());
				}
			}
			else if(formElement.getElementId()==256){
				fieldName = "decimal16";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal16()){
					fieldValue = df.format(edocSummary.getDecimal16());
				}
			}
			else if(formElement.getElementId()==257){
				fieldName = "decimal17";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal17()){
					fieldValue = df.format(edocSummary.getDecimal17());
				}
			}
			else if(formElement.getElementId()==258){
				fieldName = "decimal18";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal18()){
					fieldValue = df.format(edocSummary.getDecimal18());
				}
			}
			else if(formElement.getElementId()==259){
				fieldName = "decimal19";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal19()){
					fieldValue = df.format(edocSummary.getDecimal19());
				}
			}
			else if(formElement.getElementId()==260){
				fieldName = "decimal20";
				fieldValue = "";
				dbFieldType="decimal";
				if(null!=edocSummary && null!=edocSummary.getDecimal20()){
					fieldValue = df.format(edocSummary.getDecimal20());
				}
			}
			else if(formElement.getElementId()==91){
				fieldName = "date1";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate1()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate1());
				}
			}
			else if(formElement.getElementId()==92){
				fieldName = "date2";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate2()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate2());
				}
			}
			else if(formElement.getElementId()==93){
				fieldName = "date3";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate3()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate3());					
				}
			}
			else if(formElement.getElementId()==94){
				fieldName = "date4";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate4()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate4());
				}
			}
			else if(formElement.getElementId()==95){
				fieldName = "date5";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate5()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate5());
				}
			}
			else if(formElement.getElementId()==96){
				fieldName = "date6";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate6()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate6());
				}
			}
			else if(formElement.getElementId()==97){
				fieldName = "date7";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate7()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate7());
				}
			}
			else if(formElement.getElementId()==98){
				fieldName = "date8";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate8()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate8());
				}
			}
			else if(formElement.getElementId()==99){
				fieldName = "date9";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate9()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate9());
				}
			}
			else if(formElement.getElementId()==100){
				fieldName = "date10";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate10()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate10());
				}
			}
			else if(formElement.getElementId()==271){
				fieldName = "date11";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate11()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate11());
				}
			}
			else if(formElement.getElementId()==272){
				fieldName = "date12";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate12()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate12());
				}
			}
			else if(formElement.getElementId()==273){
				fieldName = "date13";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate13()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate13());
				}
			}
			else if(formElement.getElementId()==274){
				fieldName = "date14";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate14()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate14());
				}
			}
			else if(formElement.getElementId()==275){
				fieldName = "date15";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate15()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate15());
				}
			}
			else if(formElement.getElementId()==276){
				fieldName = "date16";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate16()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate16());
				}
			}
			else if(formElement.getElementId()==277){
				fieldName = "date17";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate17()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate17());
				}
			}
			else if(formElement.getElementId()==278){
				fieldName = "date18";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate18()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate18());
				}
			}
			else if(formElement.getElementId()==279){
				fieldName = "date19";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate19()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate19());
				}
			}
			else if(formElement.getElementId()==280){
				fieldName = "date20";
				fieldValue = "";
				dbFieldType="date";
				if(null!=edocSummary && null!=edocSummary.getDate20()){
					fieldValue = Datetimes.formatDate(edocSummary.getDate20());
				}
			}
			else if(formElement.getElementId()==101){				
				fieldType="select";
				fieldName = "list1";
				if(null!=edocSummary && !"".equals(edocSummary.getList1())){
					fieldValue = edocSummary.getList1();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==102){				
				fieldType="select";
				fieldName = "list2";
				if(null!=edocSummary && !"".equals(edocSummary.getList2())){
					fieldValue = edocSummary.getList2();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==103){				
				fieldType="select";
				fieldName = "list3";
				if(null!=edocSummary && !"".equals(edocSummary.getList3())){
					fieldValue = edocSummary.getList3();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==104){				
				fieldType="select";
				fieldName = "list4";
				if(null!=edocSummary && !"".equals(edocSummary.getList4())){
					fieldValue = edocSummary.getList4();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==105){				
				fieldType="select";
				fieldName = "list5";
				if(null!=edocSummary && !"".equals(edocSummary.getList5())){
					fieldValue = edocSummary.getList5();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==106){				
				fieldType="select";
				fieldName = "list6";
				if(null!=edocSummary && !"".equals(edocSummary.getList6())){
					fieldValue = edocSummary.getList6();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==107){				
				fieldType="select";
				fieldName = "list7";
				if(null!=edocSummary && !"".equals(edocSummary.getList7())){
					fieldValue = edocSummary.getList7();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==108){				
				fieldType="select";
				fieldName = "list8";
				if(null!=edocSummary && !"".equals(edocSummary.getList8())){
					fieldValue = edocSummary.getList8();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==109){				
				fieldType="select";
				fieldName = "list9";
				if(null!=edocSummary && !"".equals(edocSummary.getList9())){
					fieldValue = edocSummary.getList9();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==110){				
				fieldType="select";
				fieldName = "list10";
				if(null!=edocSummary && !"".equals(edocSummary.getList10())){
					fieldValue = edocSummary.getList10();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==261){				
				fieldType="select";
				fieldName = "list11";
				if(null!=edocSummary && !"".equals(edocSummary.getList11())){
					fieldValue = edocSummary.getList11();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==262){				
				fieldType="select";
				fieldName = "list12";
				if(null!=edocSummary && !"".equals(edocSummary.getList12())){
					fieldValue = edocSummary.getList12();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==263){				
				fieldType="select";
				fieldName = "list13";
				if(null!=edocSummary && !"".equals(edocSummary.getList13())){
					fieldValue = edocSummary.getList13();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==264){				
				fieldType="select";
				fieldName = "list14";
				if(null!=edocSummary && !"".equals(edocSummary.getList14())){
					fieldValue = edocSummary.getList14();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==265){				
				fieldType="select";
				fieldName = "list15";
				if(null!=edocSummary && !"".equals(edocSummary.getList15())){
					fieldValue = edocSummary.getList15();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==266){				
				fieldType="select";
				fieldName = "list16";
				if(null!=edocSummary && !"".equals(edocSummary.getList16())){
					fieldValue = edocSummary.getList16();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==267){				
				fieldType="select";
				fieldName = "list17";
				if(null!=edocSummary && !"".equals(edocSummary.getList17())){
					fieldValue = edocSummary.getList17();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==268){				
				fieldType="select";
				fieldName = "list18";
				if(null!=edocSummary && !"".equals(edocSummary.getList18())){
					fieldValue = edocSummary.getList18();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==269){				
				fieldType="select";
				fieldName = "list19";
				if(null!=edocSummary && !"".equals(edocSummary.getList19())){
					fieldValue = edocSummary.getList19();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}
			else if(formElement.getElementId()==270){				
				fieldType="select";
				fieldName = "list20";
				if(null!=edocSummary && !"".equals(edocSummary.getList20())){
					fieldValue = edocSummary.getList20();
				}
				addListStr(inputBody,fieldName,fieldValue,access,fromAccountId);
			}else if(formElement.getElementId()==291){				
				fieldName = "string21";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar21()){
					fieldValue = edocSummary.getVarchar21();
				}
			}else if(formElement.getElementId()==292){				
				fieldName = "string22";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar22()){
					fieldValue = edocSummary.getVarchar22();
				}
			}else if(formElement.getElementId()==293){				
				fieldName = "string23";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar23()){
					fieldValue = edocSummary.getVarchar23();
				}
			}else if(formElement.getElementId()==294){				
				fieldName = "string24";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar24()){
					fieldValue = edocSummary.getVarchar24();
				}
			}else if(formElement.getElementId()==295){				
				fieldName = "string25";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar25()){
					fieldValue = edocSummary.getVarchar25();
				}
			}else if(formElement.getElementId()==296){				
				fieldName = "string26";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar26()){
					fieldValue = edocSummary.getVarchar26();
				}
			}else if(formElement.getElementId()==297){				
				fieldName = "string27";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar27()){
					fieldValue = edocSummary.getVarchar27();
				}
			}else if(formElement.getElementId()==298){				
				fieldName = "string28";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar28()){
					fieldValue = edocSummary.getVarchar28();
				}
			}else if(formElement.getElementId()==299){				
				fieldName = "string29";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar29()){
					fieldValue = edocSummary.getVarchar29();
				}
			}else if(formElement.getElementId()==300){				
				fieldName = "string30";
				fieldValue = "";
				dbFieldType="varchar";
				if(null!=edocSummary && null!=edocSummary.getVarchar30()){
					fieldValue = edocSummary.getVarchar30();
				}
			}else if(formElement.getElementId()==301){
				fieldType="textarea";
				fieldName = "text11";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText11()){
					fieldValue = edocSummary.getText11();
				}
			}else if(formElement.getElementId()==302){
				fieldType="textarea";
				fieldName = "text12";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText12()){
					fieldValue = edocSummary.getText12();
				}
			}else if(formElement.getElementId()==303){
				fieldType="textarea";
				fieldName = "text13";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText13()){
					fieldValue = edocSummary.getText13();
				}
			}else if(formElement.getElementId()==304){
				fieldType="textarea";
				fieldName = "text14";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText14()){
					fieldValue = edocSummary.getText14();
				}
			}else if(formElement.getElementId()==305){
				fieldType="textarea";
				fieldName = "text15";
				fieldValue = "";
				dbFieldType="longtext";
				if(null!=edocSummary && null!=edocSummary.getText15()){
					fieldValue = edocSummary.getText15();
				}
			}
			/*
			if(formElement.getElementId()==102){
				fieldType="select";
				fieldName = "list2";
				if(null!=edocSummary && !"".equals(edocSummary.getList2())){
					fieldValue = edocSummary.getList2();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList2())&& (metaDataItem.getValue()).equals(edocSummary.getList2())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
					}
				}
			}
			if(formElement.getElementId()==103){
				fieldType="select";
				fieldName = "list3";
				if(null!=edocSummary && !"".equals(edocSummary.getList3())){
					fieldValue = edocSummary.getList3();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList3())&& (metaDataItem.getValue()).equals(edocSummary.getList3())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
					}
				}
			}
			if(formElement.getElementId()==104){
				fieldType="select";
				fieldName = "list4";
				if(null!=edocSummary && !"".equals(edocSummary.getList4())){
					fieldValue = edocSummary.getList4();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList4())&& (metaDataItem.getValue()).equals(edocSummary.getList4())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==105){
				fieldType="select";
				fieldName = "list5";
				if(null!=edocSummary && !"".equals(edocSummary.getList5())){
					fieldValue = edocSummary.getList5();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList5())&& (metaDataItem.getValue()).equals(edocSummary.getList5())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==106){
				fieldType="select";
				fieldName = "list6";
				if(null!=edocSummary && !"".equals(edocSummary.getList6())){
					fieldValue = edocSummary.getList6();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList6())&& (metaDataItem.getValue()).equals(edocSummary.getList6())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==107){
				fieldType="select";
				fieldName = "list7";
				if(null!=edocSummary && !"".equals(edocSummary.getList7())){
					fieldValue = edocSummary.getList7();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList7())&& (metaDataItem.getValue()).equals(edocSummary.getList7())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==108){
				fieldType="select";
				fieldName = "list8";
				if(null!=edocSummary && !"".equals(edocSummary.getList8())){
					fieldValue = edocSummary.getList8();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList8())&& (metaDataItem.getValue()).equals(edocSummary.getList8())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==109){
				fieldType="select";
				fieldName = "list9";
				if(null!=edocSummary && !"".equals(edocSummary.getList9())){
					fieldValue = edocSummary.getList9();
				}
				EdocElement element = edocElementManager.getByFieldName(fieldName);
				
				if(null!=element && element.getStatus()!=0){
				Long metadataId = element.getMetadataId();
				if(null!=metadataId){
				Metadata metadata = metadataManager.getMetadata(metadataId);
				metaItem =  metadataManager.getMetadataItems(metadata.getName());
				inputBody.append(fieldInput).append(domainName).append(fieldName).append(select);
					
					for(int x=0;x<metaItem.size();x++){
							metaDataItem = (MetadataItem)metaItem.get(x);
							name = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", metaDataItem.getLabel());
							if(null!=edocSummary && !"".equals(edocSummary.getList9())&& (metaDataItem.getValue()).equals(edocSummary.getList9())){
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
							}else{
								inputBody.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
							}
						}
					inputBody.append(fieldInputEnd);
				}
				}
			}
			if(formElement.getElementId()==110){
				fieldType="select";
				fieldName = "list10";
				if(null!=edocSummary && !"".equals(edocSummary.getList10())){
					fieldValue = edocSummary.getList10();
				}

			}
			*/	
			if(!StringUtils.isBlank(fieldName)){
				if(fieldValue==null){fieldValue="";}				
				xmlBody.append(leftBracket);
				xmlBody.append(domainName);
				xmlBody.append(fieldName);
				xmlBody.append(rightBracket);
				/*
				if("textarea".equals(fieldType))
				{
					fieldValue=fieldValue.replaceAll("\r\n", "\\\\n");
				}
				*/
				//多行文本中的回车特殊处理
				if("textarea".equals(fieldType))
					xmlBody.append(Strings.toHTML(Strings.toXmlStr(fieldValue),false).replaceAll("<br/>", "&amp;lt;br&amp;gt;"));
				else
					xmlBody.append(Strings.toHTML(Strings.toXmlStr(fieldValue),false));
				xmlBody.append(leftBracket);
				xmlBody.append(bias);
				xmlBody.append(domainName);
				xmlBody.append(fieldName);
				xmlBody.append(rightBracket);
				//增加数据库字段类型属性，用于数据校验				
				//下拉列表已经在上面单独初始化
				if("text".equals(fieldType))
				{
					inputBody.append(fieldInput).append(domainName).append(fieldName)
					.append("\" fieldtype=\"").append(dbFieldType)
					.append(text);
				}
				else if("textarea".equals(fieldType))
				{
					inputBody.append(fieldInput).append(domainName).append(fieldName)
					.append("\" fieldtype=\"").append(dbFieldType)
					.append(textarea);					
				}else{
					//used temporarily, optimize in the future
					inputBody.append(fieldInput).append(domainName).append(fieldName).append(text);
				}	
			}
		}
		xml.append(topHeader).append(xmlHeader).append(bodyHeader).append(xmlBody).append(bodyEnder);
		xml.append(inputHeader).append(fieldStart).append(inputBody).append(fieldEnd);
		
		return xml;
		
		/*
		StringBuffer xml = new StringBuffer("");
		String topHeader = "&&&&&&&&  xsl_start  &&&&&&&& Url=view1.xsl &&&&&&&&  data_start  &&&&&&&&";
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String bodyHeader = "<my:myFields xmlns:my=\"www.seeyon.com/form/2007\">";
		String bodyEnder   = "</my:myFields>";
		
		String xmlBody = "<my:shenhe>同意</my:shenhe><my:printer>张华</my:printer><my:keyword>工资上调15%</my:keyword><my:print_unit>工商银行</my:print_unit><my:copies>10</my:copies><my:issuer>张总</my:issuer><my:send_unit>新华社</my:send_unit><my:copy_to>用友上海公司</my:copy_to><my:report_to>用友北京公司</my:report_to><my:create_user>张华</my:create_user><my:urgent_level/><my:secret_level/><my:doc_mark/><my:serial_no/><my:send_type/><my:doc_type/><my:subject>关于工资上调的通知</my:subject><my:send_to>新华社</my:send_to><my:keep_period/>";
		
		String inputHeader = "&&&&&&&&  input_start  &&&&&&&&";
		String fieldStart = "<FieldInputList>";
		String fieldEnd   = "</FieldInputList>";
		
		xml.append(topHeader).append(xmlHeader).append(bodyHeader).append(xmlBody).append(bodyEnder);
		
		String inputBody = "<FieldInput name=\"my:subject\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:doc_type\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
			"<Input display=\"公报\" value=\"1\"/>"+
			"<Input display=\"决议\" value=\"2\"/>"+
			"<Input display=\"决定\" value=\"3\"/>"+
			"<Input display=\"指示\" value=\"4\"/>"+
			"<Input display=\"条例\" value=\"5\"/>"+
			"<Input display=\"规定\" value=\"6\"/>"+
			"<Input display=\"通知\" value=\"7\"/>"+
			"<Input display=\"通报\" value=\"8\"/>"+
			"<Input display=\"请示\" value=\"9\"/>"+
			"<Input display=\"报告\" value=\"11\"/>"+
			"<Input display=\"批复\" value=\"12\"/>"+
			"<Input display=\"会议记要\" value=\"13\"/>"+
			"<Input display=\"函\" value=\"14\"/>"+
			"<Input display=\"签报\" value=\"15\"/>"+
			"<Input display=\"电传明文\" value=\"16\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:send_type\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
			"<Input display=\"上行文\" value=\"1\"/>"+
			"<Input display=\"下行文\" value=\"2\"/>"+
			"<Input display=\"平行文\" value=\"3\"/>"+
			"<Input display=\"内部行文\" value=\"4\"/>"+
			"<Input display=\"外部行文\" value=\"5\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:doc_mark\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
				"<Input display=\"请选择公文文号\" value=\"\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:serial_no\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:secret_level\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
			"<Input display=\"普通\" value=\"1\"/>"+
			"<Input display=\"秘密\" value=\"2\"/>"+
			"<Input display=\"机密\" value=\"3\"/>"+
			"<Input display=\"绝密\" value=\"4\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:urgent_level\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
			"<Input display=\"普通\" value=\"1\"/>"+
			"<Input display=\"紧急\" value=\"2\"/>"+
			"<Input display=\"特急\" value=\"3\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:create_user\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:keep_period\" type=\"select\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\">"+
			"<Input display=\"无期限\" value=\"1\"/>"+
			"<Input display=\"1天\" value=\"2\"/>"+
			"<Input display=\"3天\" value=\"3\"/>"+
			"<Input display=\"5天\" value=\"5\"/>"+
			"<Input display=\"7天\" value=\"7\"/>"+
			"<Input display=\"10天\" value=\"10\"/>"+
			"<Input display=\"15天\" value=\"15\"/>"+
			"<Input display=\"1月\" value=\"30\"/>"+
			"<Input display=\"2月\" value=\"60\"/>"+
			"<Input display=\"3月\" value=\"90\"/>"+
			"<Input display=\"6月\" value=\"180\"/>"+
			"<Input display=\"9月\" value=\"270\"/>"+
			"<Input display=\"1年\" value=\"365\"/>"+
			"<Input display=\"2年\" value=\"730\"/>"+
			"<Input display=\"3年\" value=\"1095\"/>"+
			"<Input display=\"5年\" value=\"1825\"/>"+
			"<Input display=\"8年\" value=\"2920\"/>"+
			"<Input display=\"10年\" value=\"3650\"/>"+
			"<Input display=\"15年\" value=\"5475\"/>"+
			"<Input display=\"20年\" value=\"7300\"/>"+
			"<Input display=\"30年\" value=\"10950\"/>"+
		"</FieldInput>"+
		"<FieldInput name=\"my:send_to\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:report_to\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:copy_to\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:send_unit\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:issuer\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:print_unit\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:print_unit\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:keyword\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:printer\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:shenhe\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />"+
		"<FieldInput name=\"my:copies\" type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />";

		xml.append(inputHeader).append(fieldStart).append(inputBody).append(fieldEnd);

		return xml;
		*/
	}
	
	public String uploadXMLConvert(List<String> fieldList,String xml){
		
		String f_xml = xml;
		  
		String xmlHeader = "&&&&&&&&  xsl_start  &&&&&&&& Url=view1.xsl &&&&&&&&  data_start  &&&&&&&&";
		String inputHeader = "&&&&&&&&  input_start  &&&&&&&&";
		
		String fieldStart = "<FieldInputList>";
		String fieldEnd   = "</FieldInputList>";
		String fieldInput = "<FieldInput name=\"";
		String fieldInputEnd = "</FieldInput>";	
		String property = "type=\"text\" access=\"edit\" allowprint=\"true\" allowtransmit=\"true\" />";
		String display = "<Input display=\"";
		String displayValue = "\" value=\"";
		String displayEnd = "\"/>";
		String name = "";
		
		f_xml = xmlHeader + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + f_xml + "</my:myFields>" + inputHeader+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + fieldStart;
		
		String inputBody = "";
		inputBody += fieldStart;
		
		StringBuffer sBuffer = new StringBuffer("");
		
		for(String s:fieldList){
			sBuffer.append(fieldInput).append("my:").append(s).append("\" ").append(property);			
		}
		f_xml = f_xml +sBuffer+ fieldEnd;
		
		
		return f_xml;
	}
	
	public String uploadXSLConverter(String xsl){
		
		return "";
	}
	
	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}
	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}
	private void addListStr(StringBuffer sb,String fieldName,String fieldValue,String access,Long userAccountId)
	{
		String domainName  = "my:";
		String fieldInput = "<FieldInput name=\"";
		String fieldInputEnd = "</FieldInput>";		
		String display = "<Input display=\"";
		String displayValue = "\" value=\"";
		String displayEnd = "\"/>";		
		String name = "";
		List<MetadataItem> metaItem = null;
		MetadataItem metaDataItem = null;
		/**
		boolean flag = false ;
		try{
			flag = this.orgManager.isGroupAdmin(user.getLoginName()) ;
		}catch(Exception e) {
			
		}
		**/
		Long accountId = userAccountId ;
		if(accountId == null) {
			accountId = CurrentUser.get().getLoginAccount() ;
		}
		EdocElement element = edocElementManager.getByFieldName(fieldName,accountId);
		
		if(null!=element && element.getStatus()!=0){
		Long metadataId = element.getMetadataId();
		if(null!=metadataId){
		Metadata metadata = metadataManager.getMetadata(metadataId);
		metaItem =  metadataManager.getMetadataItems(metadata.getName());
		String select = "\" type=\"select\" access=\""+access+"\" allowprint=\"true\" allowtransmit=\"true\">";
		sb.append(fieldInput).append(domainName).append(fieldName).append(select);
			boolean bool = false;
			for(int x=0;x<metaItem.size();x++){
					metaDataItem = (MetadataItem)metaItem.get(x);
					if(metaDataItem.getState().intValue() == com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE)continue;
					name = ResourceBundleUtil.getString(metadata.getResourceBundle(), metaDataItem.getLabel());
					if(null!=fieldValue && !"".equals(fieldValue)&& (metaDataItem.getValue()).equals(fieldValue)){
						sb.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append("\" select=\"true\" ").append("/>");								
						bool = true;
					}else{
						sb.append(display).append(name).append(displayValue).append(metaDataItem.getValue()).append(displayEnd);
					}
				}
			//如果其中的字段不为空，并且没有处在新建状态的话
			if(bool == false && fieldValue!=null){
				MetadataItem newItem = metadataManager.getMetadataItem(metadata.getName(), fieldValue);
				if(null!=newItem){
				name = ResourceBundleUtil.getString(metadata.getResourceBundle(), newItem.getLabel());
				sb.append(display).append(name).append(displayValue).append(newItem.getValue()).append("\" select=\"true\" ").append("/>");						
				}
			}
			sb.append(fieldInputEnd);
		}
		}
		
	}
	/**
	 * 公文单页面文号显示列表
	 * @param sb
	 * @param fieldName
	 * @param fieldValue
	 * @param access
	 * @param markType
	 * @param orgAccountId
	 * @param summary
	 * @param isTemplete : 是否是公文的模板的新建或者编辑显示之用
	 * @param isShowOriginalDocMark ： 是否不显示当前的文号值（调用模板的时候不显示模板设置的文号值）
	 */
	private void addDocMarkListList(StringBuffer sb,String fieldName,String fieldValue,String access,int markType,Long orgAccountId,EdocSummary summary,boolean isTemplete,boolean isNoShowOriginalDocMark)
	{
		String domainName  = "my:";
		String fieldInput = "<FieldInput name=\"";
		String fieldInputEnd = "</FieldInput>";		
		String display = "<Input display=\"";
		String displayValue = "\" value=\"";
		String displayEnd = "\"/>";		
		String name = "";
		String fieldType="select";										
		
		//是否是调用公文模板，并且公文模板绑定了字号
		Long templeteId = summary.getTempleteId();
		EdocMarkModel model = null;
		if(templeteId != null){
			if("serial_no".equals(fieldName)) {
				model = edocMarkDefinitionManager.getEdocMarkByTempleteId(templeteId, MarkCategory.serialNo);
			}else if("doc_mark".equals(fieldName)) {
				model = edocMarkDefinitionManager.getEdocMarkByTempleteId(templeteId, MarkCategory.docMark);
			}else if("doc_mark2".equals(fieldName)){
				model = edocMarkDefinitionManager.getEdocMarkByTempleteId(templeteId, MarkCategory.docMark2);
			}
		}
		List<EdocMarkModel> list = null;
		try {
			//添加一层防护，避免如果orgAccountId为空的时候取不到文号。
			Long _orgAccountId=V3xOrgEntity.VIRTUAL_ACCOUNT_ID;
			if(model != null){
				list = new ArrayList<EdocMarkModel>();
				list.add(model);
			}else{
				User user = CurrentUser.get();
				String deptIds = orgManager.getUserIDDomain(user.getId(),_orgAccountId, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
				if(isTemplete){
					//是单位管理员新建或者修改模板的话显示授权给这个单位下面部门的文号
					String allDepIds = getAllDepartmentIdsByAccountId(user.getLoginAccount());
					if(Strings.isNotBlank(allDepIds))deptIds += ","+allDepIds;
				}
				list = edocMarkDefinitionManager.getEdocMarkDefinitions(deptIds,markType);
			}
		}
		catch (Exception e) {
			log.error("读取公文文号时出现错误!" + e.toString());
		}
		EdocMarkModel markModel = null;
		String select = "\" type=\"select\" access=\""+access+"\" allowprint=\"true\" allowtransmit=\"true\">";
		sb.append(fieldInput).append(domainName).append(fieldName).append(select);
		
		//----如果fieldValue不为空或不等于"",拼接成<Input display=... "0|文号||0(Constants.EDOC_MARK_EDIT_NONE)".... select="true" />
		if(null!=fieldValue && !"".equals(fieldValue)){
			//切换文单时候，录入的临时文号需要解析
			EdocMarkModel emTemp=null;
			try{emTemp=EdocMarkModel.parse(fieldValue);}catch(Exception e){}
			if(emTemp==null){
				sb.append(display);
				sb.append(Strings.toHTML(Strings.toXmlStr(fieldValue),false));
				sb.append(displayValue);
				sb.append("0|" + Strings.toHTML(Strings.toXmlStr(fieldValue),false) + "||" + Constants.EDOC_MARK_EDIT_NONE);//
				sb.append("\" select=\"true " + displayEnd);
			}else{
				if(isNoShowOriginalDocMark){  
					//当前操作时调用模板的时候不显示模板设置的字号
				}else{
					sb.append(display);
					sb.append(Strings.toHTML(Strings.toXmlStr(emTemp.getMark()),false));
					sb.append(displayValue);				
					sb.append(Strings.toHTML(Strings.toXmlStr(fieldValue),false));				
					sb.append("\" select=\"true " + displayEnd);
				}
			}
		}//else{//如果fieldValue没有默认值,显示下面的提示：<请选择公文文号>,修改问号的时候要能取消原来的问号，所以每个下拉列表都显示一个<..请选择..>
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
			String value="";
			if(markType==EdocEnum.MarkType.edocMark.ordinal()){
				value = ResourceBundleUtil.getString(r, "edoc.mark.empty.label");
			}else if(markType==EdocEnum.MarkType.edocInMark.ordinal()){
				value = ResourceBundleUtil.getString(r, "edoc.inmark.empty.label");
			}
			sb.append(display);
			sb.append(Strings.toHTML(Strings.toXmlStr(value),false));
			sb.append(displayValue);
			sb.append("");//
			sb.append("\" select=\"true " + displayEnd);								
		//}
		
		String strTemp="";
		//如果文号定义的列表中存在数据,用列表的形式保存
		if (list != null && list.size()>0) {
			for(int x=0;x<list.size();x++){
				markModel= (EdocMarkModel)list.get(x);
				if (null != markModel) {					
					Long definitionId = markModel.getMarkDefinitionId();
					strTemp=definitionId + "|" + markModel.getMark() + "|" + markModel.getCurrentNo() + "|" + Constants.EDOC_MARK_EDIT_SELECT_NEW;
					if(strTemp.equals(fieldValue)){continue;}
					sb.append(display);
					if(isTemplete){
						//新建或者修改模板的时候，只显示字号，不显示流水号
						sb.append(Strings.toHTML(Strings.toXmlStr(markModel.getWordNo()),false));
						sb.append(displayValue);
						strTemp = definitionId + "|" + markModel.getWordNo() + "|" + markModel.getCurrentNo() + "|" + Constants.EDOC_MARK_EDIT_SELECT_NEW;
						sb.append(Strings.toHTML(Strings.toXmlStr(strTemp),false));
					}else{
						sb.append(Strings.toHTML(Strings.toXmlStr(markModel.getMark()),false));
						sb.append(displayValue);
						sb.append(Strings.toHTML(Strings.toXmlStr(strTemp),false));
					}
					
					
					sb.append(displayEnd);
				}
			}	
		}
		sb.append(fieldInputEnd);			
	}
//	private Set<EdocMarkModel> fiterDuplicateEdocMark(List<EdocMarkModel> list ){
//		
//		if(list == null || list.isEmpty()) return null;
//		
//		List<EdocMarkModel> flist = new ArrayList<EdocMarkModel>();
//		
//		for(EdocMarkModel model : list){
//			if()
//		}
//	}
	private String getAllDepartmentIdsByAccountId(Long _orgAccountId)
			throws BusinessException {
		StringBuilder sd = new StringBuilder();
		List<V3xOrgDepartment> depts = orgManager.getAllDepartments(_orgAccountId);
		for(V3xOrgDepartment dep : depts){
			if(sd.length()<=0)sd.append(dep.getId());
			else sd.append(",").append(dep.getId());
		}
		return sd.toString();
	}
}
