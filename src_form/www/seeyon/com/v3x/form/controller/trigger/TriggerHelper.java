package www.seeyon.com.v3x.form.controller.trigger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import www.seeyon.com.v3x.form.base.RuntimeCharset;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.pageobject.EventTemplateObject;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.domain.FormTriggerRunning;
import www.seeyon.com.v3x.form.manager.SeeyonFormAppManagerImpl;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.inf.IXmlNodeName;
import www.seeyon.com.v3x.form.manager.define.trigger.EventAction;
import www.seeyon.com.v3x.form.manager.define.trigger.EventCondition;
import www.seeyon.com.v3x.form.manager.define.trigger.EventEntity;
import www.seeyon.com.v3x.form.manager.define.trigger.EventMapping;
import www.seeyon.com.v3x.form.manager.define.trigger.EventTask;
import www.seeyon.com.v3x.form.manager.define.trigger.EventValue;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonFormAppManager;
import www.seeyon.com.v3x.form.utils.StringUtils;
import www.seeyon.com.v3x.form.utils.dom4jxmlUtils;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.util.Strings;

public class TriggerHelper {
	private static RuntimeCharset fCurrentCharSet = SeeyonForm_Runtime
			.getInstance().getCharset();
	private static Log log = LogFactory.getLog(TriggerHelper.class);
	
	private final static String C_sXML_Head="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

	/** 一级分隔符 **/
	private static String ONCESTEP_SPLIT = ",";
	/** 二级分隔符 **/
	private static String SECONDSTEP_SPLIT = "\\|";
	
	private static FormDaoManager formDaoManager  = null;
	private static ColManager colManager = null;
	private static FormDaoManager getFormDaoManager(){
    	if(formDaoManager == null){
    		formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
    	}
    	return formDaoManager;
	}
	private static ColManager getColManager(){
		if(colManager == null){
			colManager = (ColManager)ApplicationContextHolder.getBean("colManager");
		}
		
		return colManager;
	}
	/**
	 * 添加触发器到sessionObject
	 * 
	 * @param request
	 * @param sessionobject
	 */
	public static void addTriggerObject(HttpServletRequest request,
			SessionObject sessionobject) {
		FormEvent formEvent = null;
		long triggerId = Long.parseLong(request.getParameter("triggerId"));
		if (triggerId == -1) {
			triggerId = Long.valueOf(UUIDLong.longUUID());
			ISeeyonFormAppManager iSeeyonFormAppManager = new SeeyonFormAppManagerImpl();
			formEvent = new FormEvent(
					iSeeyonFormAppManager.findById(sessionobject.getFormid()));
			sessionobject.getTriggerConfigMap().put(triggerId, formEvent);
			formEvent.setId(triggerId);
		} else {
			formEvent = sessionobject.getTriggerConfigMap().get(triggerId);
		}
		// 触发器名称 状态
		String triggerName = (String) request.getParameter("triggerName");
		int status = Integer.parseInt(request.getParameter("status"));
		int sourceType = Integer.parseInt(request.getParameter("sourceType"));
		formEvent.setName(triggerName);
		formEvent.setStatus(status);
		formEvent.setSourceType(sourceType);

		// 触发条件
		List<EventCondition> conditionList = new ArrayList<EventCondition>();
		formEvent.setConditionList(conditionList);
		EventCondition eCondition = new EventCondition();
		EventValue value = new EventValue();
		String triggerDot = request.getParameter("triggerDot");
		value.setValue(triggerDot);
		String triggerDotId = request.getParameter("triggerDotId");
		eCondition.setType(EventCondition.TYPE_TRIGGERDOT);
		eCondition.setValue(value);
		eCondition.setId(triggerDotId);
		conditionList.add(eCondition);

		String fieldValue = request.getParameter("fieldValue");
		if (!"".equals(fieldValue)) {
			eCondition = new EventCondition();
			conditionList.add(eCondition);
			value = new EventValue();
			value.setValue(fieldValue);
			eCondition.setType(EventCondition.TYPE_FIELDVALUE);
			eCondition.setValue(value);
			String fieldValueId = request.getParameter("fieldValueId");
			eCondition.setId(fieldValueId);
		}

		String timeQuartz = request.getParameter("timeQuartz");
		if (timeQuartz != null && !"".equals(timeQuartz)) {// 时间调度 需要处理下
			eCondition = new EventCondition();
			conditionList.add(eCondition);
			value = new EventValue();
			eCondition.setType(EventCondition.TYPE_TIMEQUARTZ);
			eCondition.setValue(value);
			String timeQuartzId = request.getParameter("timeQuartzId");
			eCondition.setId(timeQuartzId);
			String[] times = timeQuartz.split(ONCESTEP_SPLIT);
			value.setValue(times[0]);
			value.setDayType(times[1]);
			value.setTime(times[2]);
			value.setModeType(times[3]);
			value.setFrequency(times[4]);
			// value.setRemindTime(times[4]);
		}

		// 触发动作
		List<EventAction> actionList = new ArrayList<EventAction>();
		formEvent.setActionList(actionList);
		EventAction eventAction;
		EventTemplateObject template;
		List<EventEntity> entityList;
		List<EventMapping> mappingList;

		String[] types = request.getParameterValues("type");
		String[] ids = request.getParameterValues("actionId");
		String[] memTypes = request.getParameterValues("memType");
		// String[] entityIds = request.getParameterValues("entityId");
		String[] entitys = request.getParameterValues("entity");
		// 模板
		String[] templateIds = request.getParameterValues("templateId");
		String[] templateFormAppIds = request
				.getParameterValues("templateFormAppId");
		String[] contents = request.getParameterValues("content");
		// 拷贝数据
		String[] mappingIds = request.getParameterValues("mappingId");
		//触发交换引擎任务
		String[] taskIds = request.getParameterValues("taskId");
		String[] taskNames = request.getParameterValues("taskName");
		for (int i = 0; i < types.length; i++) {
			eventAction = new EventAction();
			entityList = new ArrayList<EventEntity>();
			mappingList = new ArrayList<EventMapping>();
			template = new EventTemplateObject();
			actionList.add(eventAction);
			template.setId(templateIds[i]);
			template.setFormAppId(templateFormAppIds[i]);
			eventAction.setType(types[i]);
			eventAction.setId(ids[i]);
			if (EventAction.TYPE_FLOW.equals(types[i])) {
				template.setFlowTemplateName(contents[i]);
			} else if (EventAction.TYPE_MESSAGE.equals(types[i])) {
				template.setContent(contents[i]);
			}
			if (EventAction.TYPE_TASK.equals(types[i])) {
				EventTask et = new EventTask();
				et.setId(taskIds[i]);
				et.setTaskName(taskNames[i]);
				et.setErrorToStop(EventTask.ERROETOSTOP_FALSE);//暂时取消掉
				eventAction.setTask(et);
			} else {
				eventAction.setEntityList(entityList);
				eventAction.setMappingList(mappingList);
				eventAction.setTemplate(template);
				if (!"".equals(memTypes[i])) {
					String[] entity = entitys[i].split(ONCESTEP_SPLIT);
					EventEntity eventEntity;
					for (int j = 0; j < entity.length; j++) {
						eventEntity = new EventEntity();
						entityList.add(eventEntity);
						eventEntity.setType(memTypes[i]);
						if ("".equals(entity[j])) {
							eventEntity.setEntityType("");
							eventEntity.setValue("");
						} else {
							String[] entis = entity[j].split(SECONDSTEP_SPLIT);
							eventEntity.setEntityType(entis[0]);
							eventEntity.setValue(entis[1]);
						}
					}
				}
				if (!"".equals(mappingIds[i])) {
					String[] mapping = mappingIds[i].split(ONCESTEP_SPLIT);
					EventMapping eventMapping;
					for (int j = 0; j < mapping.length; j++) {
						if (!"".equals(mapping[j])) {
							eventMapping = new EventMapping();
							mappingList.add(eventMapping);
							String[] mapContent = mapping[j]
									.split(SECONDSTEP_SPLIT);
							eventMapping.setSourceField(mapContent[0]);
							eventMapping.setDestField(mapContent[1]);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * 表单流程保存待发、直接发起、触发新流程、子流程发起操作拷贝触发设置
	 * wusb
	 */
	public static void copyTriggerInfo(Long summaryId, Long formAppId, Long masterId) throws Exception{
		SeeyonForm_ApplicationImpl formApp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
		if(formApp!=null){
			FormTriggerRunning formTriggerRunning = getFormDaoManager().queryTriggerRunning(summaryId);
			if(formTriggerRunning==null){
				List<FormEvent> triggerConfigList = formApp.getTriggerConfigList();
				StringBuilder triggerConfigSb = new StringBuilder();
				if(triggerConfigList!=null && !triggerConfigList.isEmpty()){
					triggerConfigSb.append(StringUtils.space(0) + "<Trigger> \r\n");
					triggerConfigSb.append(StringUtils.space(0+2) + "<EventList> \r\n");
					for (FormEvent formEvent : triggerConfigList) {
						triggerConfigSb.append(formEvent.getXmlString(0+4));
					}
					triggerConfigSb.append(StringUtils.space(0+2) + "</EventList> \r\n");
					triggerConfigSb.append("</Trigger> \r\n");
				}
				
				List<RelationCondition> relationConditionList = formApp.getRelationConditionList();
				StringBuilder relationConditionSb = new StringBuilder();
				if(relationConditionList!=null && !relationConditionList.isEmpty()){
					relationConditionSb.append(StringUtils.space(0) + "<RelationConditionList> \r\n");
					for (RelationCondition relationCondition : relationConditionList) {
						relationConditionSb.append(relationCondition.getXmlString(0 + 2));	
					}
					relationConditionSb.append(StringUtils.space(0) + "</RelationConditionList> \r\n");
				}
				
				if(Strings.isNotBlank(relationConditionSb.toString()) || Strings.isNotBlank(triggerConfigSb.toString())){
					formTriggerRunning = new FormTriggerRunning();
					formTriggerRunning.setNewId();
					formTriggerRunning.setSummaryId(summaryId);
					formTriggerRunning.setFormAppmainId(formAppId);
					formTriggerRunning.setRecordId(masterId);
					formTriggerRunning.setRelationCondition(relationConditionSb.toString());
					formTriggerRunning.setTriggerConfig(triggerConfigSb.toString());
					formTriggerRunning.setCreateDate(new Timestamp(System.currentTimeMillis()));
					getFormDaoManager().insertTriggerRunning(formTriggerRunning);
					
					//同时在协同表里加一个是否存在触发设置的标识
					getColManager().updateFormTriggerStatus(summaryId, true);
				}
			} else {
				//同时在协同表里加一个是否存在触发设置的标识
				getColManager().updateFormTriggerStatus(summaryId, true);
			}
		}
	}
	
	
	/**
	 * 表单运行时（运行时触发、回写时读取关联条件）读取触发设置
	 * wusb
	 */
	public static Map<Long,FormEvent> getTriggerConfigMap(Long summaryId, SeeyonForm_ApplicationImpl formApp){
		Map<Long,FormEvent> triggerConfigMap = new LinkedHashMap<Long,FormEvent>();//触发设置
		String triggerConfigXML = null;
		try {
			//同时在协同表里加一个是否存在触发设置的标识
			ColSummary summary = getColManager().getColSummaryById(summaryId, false);
			if(summary.isHasFormTrigger()){
				FormTriggerRunning formTriggerRunning = getFormDaoManager().queryTriggerRunning(summaryId);
				if(formTriggerRunning != null){
					//触发设置 
					triggerConfigXML = formTriggerRunning.getTriggerConfig();
					if(Strings.isNotBlank(triggerConfigXML)){
						triggerConfigXML = fCurrentCharSet.DBOut2JDK(triggerConfigXML);
						triggerConfigXML = C_sXML_Head+triggerConfigXML;
						Document fdocTriggerConfig = dom4jxmlUtils.paseXMLToDoc(triggerConfigXML);
						Element eventListEle = fdocTriggerConfig.getRootElement().element(IXmlNodeName.EventList);
						if (eventListEle != null){
							List<Element> eventListElements = eventListEle.elements(IXmlNodeName.Event);
							if(eventListElements!=null){
								for(Element element : eventListElements){
									FormEvent formEvent = new FormEvent(formApp);
									formEvent.loadFromXml(element);
									triggerConfigMap.put(formEvent.getId(),formEvent);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("错误的XML："+triggerConfigXML);
			log.error(e.getMessage(), e);
		}
		return triggerConfigMap;
	}
	
	/**
	 * 表单运行时（运行时触发、回写时读取关联条件）读取触发设置
	 * wusb
	 */
	public static Map<Long,RelationCondition> getRelationConditionMap(Long summaryId){
		Map<Long,RelationCondition> relationConditionMap = new LinkedHashMap<Long,RelationCondition>();//关联条件
		String relationConditionXML = null;
		try {
			//同时在协同表里加一个是否存在触发设置的标识
			ColSummary summary = getColManager().getColSummaryById(summaryId, false);
			if(summary.isHasFormTrigger()){
				FormTriggerRunning formTriggerRunning = getFormDaoManager().queryTriggerRunning(summaryId);
				if(formTriggerRunning != null){
					//关联条件
					relationConditionXML = formTriggerRunning.getRelationCondition();
					if(Strings.isNotBlank(relationConditionXML)){
						relationConditionXML = fCurrentCharSet.DBOut2JDK(relationConditionXML);
						relationConditionXML = C_sXML_Head+relationConditionXML;
						Document fdocRelationCondition = dom4jxmlUtils.paseXMLToDoc(relationConditionXML);
						Element relationConditionListEle = fdocRelationCondition.getRootElement();
						if (relationConditionListEle != null){
							List<Element> relationConditionListElements = relationConditionListEle.elements(IXmlNodeName.RelationCondition);
							for (Element element : relationConditionListElements) {
								if(element != null){
									RelationCondition relationCondition = new RelationCondition();
									relationCondition.loadFromXml(element);
									relationConditionMap.put(relationCondition.getId(),relationCondition);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("错误的XML："+relationConditionXML);
			log.error(e.getMessage(), e);
		}
		return relationConditionMap;
	}
	
	
	/**
	 * 协同待发删除、流程终止、流程结束操作删除拷贝触发设置
	 *  wusb
	 */
	public static void deleteTriggerInfo(Long summaryId) throws Exception{
		getFormDaoManager().deleteTriggerRunningBySummaryId(summaryId);
	}
}
