package com.seeyon.v3x.main.section;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.QueryResultImpl;
import www.seeyon.com.v3x.form.manager.define.query.queryresult.inf.IQueryResult.IQueryRecord;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Strings;
/**
 * 
 * @author xgghen  2011-03-24
 *
 */
public class SingleBoardFormQueryResultSection extends BaseSection{
	
	private static final Log log = LogFactory.getLog(SingleBoardFormQueryResultSection.class);
	private static final SeeyonForm_Runtime runtime = SeeyonForm_Runtime.getInstance();
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private static final String NAMESPACE = "my:";
	
	@Override
	public String getId() {
		return "singleBoardformqueryResultSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		String singleBoardId = preference.get(PortletEntityProperty.PropertyName.singleBoardId.name());
		String[] formAndQuery = getFormAndQuery(singleBoardId);
		
		if(formAndQuery == null ||  formAndQuery.length < 2 
				|| Strings.isBlank(formAndQuery[0]) || Strings.isBlank(formAndQuery[1])){
			log.info("表单的查询模板的解析出现问题");
			return null;
		}
		
		/*String showtitleName = preference.get("columnsName") ;
		
		if(Strings.isNotBlank(showtitleName)){
			return showtitleName ;
		}*/
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndQuery[1])) ;
		if(fapp == null){
			log.info("表单的查询模板的解析出现问题");
			return null;
		}
		try{
			ISeeyonQuery seeyonQuery = fapp.findQueryByName(formAndQuery[0]) ;
			if(seeyonQuery != null){
				return seeyonQuery.getQueryName() ;
			}
			if(NumberUtils.isNumber(formAndQuery[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndQuery[0])) ;
				if(formQueryPlan != null){
					return formQueryPlan.getPlanName() ;
				}
			}
			
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
		
		
		return null;
	}
	
	@Override
	public String getBaseName(Map<String, String> preference) {
		return this.getName(preference);
	}
	private String[] getFormAndQuery(String formAndQuery){
		if(Strings.isBlank(formAndQuery)){
			return null ;
		}
		if(formAndQuery.indexOf(",") == -1){
			return null ;
		}
		return formAndQuery.split(",") ;
	}
	
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
        HtmlTemplete ht = new HtmlTemplete();        
		String singleBoardId = preference.get(PortletEntityProperty.PropertyName.singleBoardId.name());
		String[] formAndQuery = getFormAndQuery(singleBoardId);
		
		if(formAndQuery == null ||  formAndQuery.length < 2 
				|| Strings.isBlank(formAndQuery[0]) || Strings.isBlank(formAndQuery[1])){
			log.info("表单的查询模板的解析出现问题");
			return null;
		}
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndQuery[1])) ;
		if(fapp == null){
			log.info("表单的查询模板的解析出现问题");
			return null;
		}
		String formname = null;
		formname = fapp.getAppName();
		boolean isFlow = true;
		isFlow = fapp.getFormType() == ISeeyonForm.TAppBindType.FLOWTEMPLATE.getValue();
        StringBuilder html = new StringBuilder();
        String queryname = formAndQuery[0];
        String planid = "";
        
        html.append("<div id='' class='scrollList position_relative'>");
       
       
		try{   
			SeeyonQueryImpl seeyonQuery = (SeeyonQueryImpl)fapp.findQueryByName(formAndQuery[0]); 			
			if(seeyonQuery != null){
				seeyonQuery.setPagination(true);
				Pagination.setFirstResult(0);
				Pagination.setMaxResults(50);
				QueryResultImpl resultData = seeyonQuery.getResultData(null) ;
				html.append(queryResultToHtml(resultData, null,Long.valueOf(formAndQuery[1]),queryname,formname,isFlow));
			}else{
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndQuery[0])) ;
				if(formQueryPlan != null){
					queryname = formQueryPlan.getQueryName();
					planid = formAndQuery[0];
					seeyonQuery = (SeeyonQueryImpl)fapp.findQueryByName(formQueryPlan.getQueryName());
					if(seeyonQuery != null){
						seeyonQuery.setPagination(true);
						Pagination.setFirstResult(0);
						Pagination.setMaxResults(50);
						Document doc  = dom4jxmlUtils.paseXMLToDoc(formQueryPlan.getPlanDefine());
						Element root = doc.getRootElement();
						List<QueryColum> dataColumList = new ArrayList<QueryColum>();
						Element showDataList = root.element(IXmlNodeName.ShowDataList);
			        	if(showDataList != null){
			    			int i = 0;
			    			List ShowColumList = showDataList.elements();
			    			for(Object item : ShowColumList){
			    				Element e = (Element)item;
			    				QueryColum queryColum = new QueryColum();
			    				queryColum.loadFromXml(e);
			    				dataColumList.add(queryColum);
			    				i++;
			    			}
			        	}
						Element userConditionListElement = root.element(IXmlNodeName.UserConditionList);
						if(userConditionListElement != null){
							ConditionListQueryImpl userConditionList = new ConditionListQueryImpl();
							userConditionList.loadFromXml(userConditionListElement);
							userConditionList.setProvider(seeyonQuery.getDBProvider()) ;
							QueryResultImpl resultData = seeyonQuery.getResultData(userConditionList.copy()) ;
							html.append(queryResultToHtml(resultData,dataColumList,Long.valueOf(formAndQuery[1]),queryname,formname,isFlow));
						}
					}
				}
			}			
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
        
        html.append("</div>");
        
        String height = "208";
		String value = preference.get("height");
		if (Strings.isNotBlank(value)) {
			height = value;
		}
		ht.setHeight(height);
		
		ht.setHtml(html.toString());
		ht.setModel(HtmlTemplete.ModelType.inner);
		ht.setShowBottomButton(true);
		ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/formquery.do?method=formQuery&formid=" + formAndQuery[1] + "&planid=" + planid + "&isWindow=true&queryname=%" + queryname + "%FormQuery");
        return ht ;
	}
	
	private String queryResultToHtml(QueryResultImpl resultData, List<QueryColum> dataColumList,Long formid,String queryname,String formname,boolean isFlow){
		if(resultData == null){
			return "" ;
		}
		StringBuilder str = new StringBuilder() ;
		String penetrate = "";
		
		try{
			List<QueryColum> list = resultData.getSchema() ;
			if(CollectionUtils.isNotEmpty(dataColumList)){
				list = dataColumList;
			}
			if(list != null){
				str.append("<form id=\"showQueryForm\" method=\"post\" name=\"showQueryForm\">");
				if(resultData.getRunner().getQuery().getShowDetail() != null){
					String resultFormname="";
					String opername="";
					String showdetail="";
					String appShowDetail="";
					if(resultData.getRunner().getQuery().getShowDetail() != null){
			            resultFormname= resultData.getRunner().getQuery().getShowDetail().getFormName(); 
			            opername = resultData.getRunner().getQuery().getShowDetail().getOperName();
			            showdetail = resultFormname+"."+opername;
			            appShowDetail = resultData.getRunner().getQuery().getShowDetail().getShowDetailStr();
			            penetrate="true";
			        }
					str.append("<input type=\"hidden\" name=\"showdetail\" id=\"showdetail\" value=\""+showdetail+"\"/>");
					str.append("<input type=\"hidden\" name=\"appShowDetail\" id=\"appShowDetail\" value=\""+appShowDetail+"\"/>");
					str.append("<input type=\"hidden\" name=\"formname\" id=\"formname\" value=\""+formname+"\"/>");
					str.append("<input type=\"hidden\" name=\"formid\" id=\"formid\" value=\""+formid+"\"/>");
					str.append("<input type=\"hidden\" name=\"queryname\" id=\"queryname\" value=\""+queryname+"\"/>");
					str.append("<input type=\"hidden\" name=\"bTransmit\" id=\"bTransmit\" value=\"0\"/>");
					str.append("<input type=\"hidden\" name=\"isFlow\" id=\"isFlow\" value=\""+isFlow+"\"/>");
				}
				str.append("<table width=\"100%\" style=\"border-top: solid 1px #D7D7D7;border-left: solid 1px #D7D7D7;background: url(../collaboration/images/manage.stat.bg3.gif) repeat-x;\" cellspacing=\"0\" cellpadding=\"0\">");
				str.append("<tr height = '20' style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\">");
				for (QueryColum queryColum : list) {
					str.append("<td align=\"center\" style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\" nowrap>" + Constantform.getString4OtherKey(queryColum.getColumTitle()) + "</td>");
				}
				str.append("</tr>");
			}
			str.append("<tbody>");
			for (int i = 0; i < resultData.getRecordCount(); i++) {
				IQueryRecord record = resultData.getRecord(i);
				str.append("<tr height=\"22\" ");
				if ("true".equals(penetrate)) {
					str.append(" style=\"cursor:hand;\" onclick=\"showQueryTable('" + String.valueOf(record.getId()) + "')\" ");
				}
				str.append(">");
				for (int j = 0; j < list.size(); j++) {
					String dataAreaName = list.get(j).getDataAreaName();
					if(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_member_id)).equals(dataAreaName)){
						dataAreaName = SeeyonFormPojo.C_sFieldName_Start_member_id;
					} else if(Constantform.getString4OtherKey(OperHelper.noNamespace(SeeyonFormPojo.C_sFieldName_Start_date)).equals(dataAreaName)){
						dataAreaName = SeeyonFormPojo.C_sFieldName_Start_date;
					}
					if(!dataAreaName.startsWith(NAMESPACE) && !SeeyonFormPojo.C_sFieldNames.contains(dataAreaName)){
						dataAreaName = NAMESPACE + dataAreaName;
					}
					String value = record.getValueByName(dataAreaName);
					String displayValue = "&nbsp;";
					if (Strings.isNotBlank(value)) {
						displayValue = Functions.getLimitLengthString(value, 20, "...");
					}
					str.append("<td align=\"center\" style=\"border-bottom: solid 1px #D7D7D7;border-right: solid 1px #D7D7D7;\" title=\"" + value + "\" nowrap>" + displayValue + "</td>");
				}
				str.append("</tr>");
			}
			str.append("</tbody>");
			str.append("</table>");
			str.append("</form>");
			
		}catch(Exception e){
			log.error("查询结果转成HTML出现问题", e) ;
		}finally{	
			try{
				if(resultData != null){
					resultData.unInit();
				}
			}catch(Exception e1){
				log.error("查询结果转成HTML出现问题", e1) ;
			}

		}
		return str.toString() ;
	}

	@Override
	public boolean isAllowUsed() {
		User user = CurrentUser.get();
		return !user.isAdmin();
	}

	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if(Strings.isBlank(singleBoardId)){
			return false;
		}
		String[] formAndQuery = getFormAndQuery(singleBoardId);
		
		if(formAndQuery == null ||  formAndQuery.length < 2 
				|| Strings.isBlank(formAndQuery[0]) || Strings.isBlank(formAndQuery[1])){
			return false;
		}
		
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) runtime.getAppManager().findById(Long.valueOf(formAndQuery[1])) ;
		if(fapp == null){
			return false;
		}
		try{
			ISeeyonQuery seeyonQuery = fapp.findQueryByName(formAndQuery[0]) ;
			if(seeyonQuery != null){
				try {
					return iOperBase.checkAccess(CurrentUser.get(), NumberUtils.toLong(formAndQuery[1]), formAndQuery[0], IPagePublicParam.C_iObjecttype_Query);
				} catch (Exception e) {
					log.error("表单查询授权校验失败", e);
					return false;
				}
			}
			if(NumberUtils.isNumber(formAndQuery[0])){
				FormDaoManager formDaoManager = (FormDaoManager)runtime.getBean("formDaoManager");
				FormQueryPlan formQueryPlan  = formDaoManager.getFormQueryPlanById(Long.valueOf(formAndQuery[0])) ;
				if(formQueryPlan != null){
					return true;
				}
			}
			
		}catch(SeeyonFormException e){
			log.error("", e) ;
		}
		return false;
	}
}
