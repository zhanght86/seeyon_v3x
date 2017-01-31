package com.seeyon.v3x.collaboration.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * User: lius
 * Date: 2006-10-27
 * Time: 13:18:11
 */
public class FlowData {
	private static final Log log = LogFactory.getLog(FlowData.class);
    //"选人界面模式"==1, "Flash编辑流程模式"==2
    //默认是"选人界面模式"
    String desc_by = DESC_BY_PEOPLE;

    //desc_by的可选值
    public static final String DESC_BY_PEOPLE = "people";
    public static final String DESC_BY_XML = "xml";

    //"选人界面模式"中的type字段可选值
    public static final int FLOWTYPE_SERIAL = 1;
    public static final int FLOWTYPE_PARALLEL = 2;
    public static final int FLOWTYPE_MULTIPLE = 3;
    public static final int FLOWTYPE_COLASSIGN = 4;
    public static final int FLOWTYPE_NEXTPARALLEL = 5;

    //"选人界面模式"中的成员
    List<Party> people;
    int type;
    
    //数据来源类型
    private String fromType;
    
    //节点属性seeyonPolicy
    BPMSeeyonPolicy seeyonPolicy;

    //"Flash编辑流程模式"中的成员
    String xml;

    //套用模板时有可能进行选人，可能为null或 一个HashMap 对象。
    Map addition;
    
    //是否显示单位缩写
    String isShowShortName = "false";
    
    Map condition;
    Map<String,String> usedPolicy;


    public Map getCondition() {
		return condition;
	}

	public void setCondition(Map condition) {
		this.condition = condition;
	}

	public List<Party> getPeople() {
        return people;
    }

    public void setPeople(List<Party> people) {
        this.people = people;
        resetIsShowShortName(this);
    }
    /**
     * 根据FlowData的people设置FlowData是否显示单位简称。
     * @param data
     */
    private void resetIsShowShortName(FlowData data)
    {
    	if("true".equals(data.getIsShowShortName()))return;
    	User user = CurrentUser.get();
    	String sAccountId = String.valueOf(user.getAccountId());
    	List<Party> people = data.getPeople();
    	for(Party p:people)
    	{
    		if(!sAccountId.equals(p.getAccountId()))
    		{
    			data.setIsShowShortName("true");
    			break;
    		}
    	}
    }    

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getDesc_by() {
        return desc_by;
    }

    public void setDesc_by(String desc_by) {
        this.desc_by = desc_by;
    }

    public String getXml() {
        return xml;
    }
    
    public BPMProcess toBPMProcess(){
    	return BPMProcess.fromXML(xml);
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public boolean isEmpty() {
        return ((DESC_BY_PEOPLE.equals(desc_by) && (people == null || people.isEmpty()))
        		|| (DESC_BY_XML.equals(desc_by) && (xml==null || "".equals(xml)))
        		);
    }

    public Map getAddition() {
        return addition;
    }

    public void setAddition(Map addition) {
        this.addition = addition;
    }
    
    public static FlowData getFlowdata(Map parameterMap ) {
        FlowData flowData = new FlowData();
        String desc_by = FlowData.DESC_BY_PEOPLE;
        flowData.setDesc_by(desc_by);
        //�?选人界面" - 简单模�?
        // people中的每个元素是一个String型的loginId
        List<Party> people = new ArrayList<Party>();
        String[] types = (String[])parameterMap.get("userType");
        String[] ids = (String[])parameterMap.get("userId");
        String[] names =(String[]) parameterMap.get("userName");
        String[] accountIds = (String[]) parameterMap.get("accountId");
        String[] accountShortNames = (String[]) parameterMap.get("accountShortname");
        String[] activityIds = (String[])parameterMap.get("activityId");
        String flowType = (String)parameterMap.get("flowType");
        String showShortName = (String)parameterMap.get("isShowShortName");
        if(names.length<ids.length) names=getElementNames(ids, types);
            
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                String type = getUserTypeByField(types[i]);
                String name = names[i];
                String accountId = accountIds[i];
                String accountShortName = accountShortNames[i];
                    
                Party party = new Party(type, id, name, accountId, accountShortName);
                
                if(activityIds != null && activityIds.length > 0){
                	party.setActivityId(activityIds[i]);
                }
                
                people.add(party);
            }
         }
            
        flowData.setPeople(people);
            
        BPMSeeyonPolicy seeyonPolicy = new BPMSeeyonPolicy("collaboration", "协同");
        String remindTime = (String)parameterMap.get("advanceRemind");
        String dealTerm = (String)parameterMap.get("deadline");
        seeyonPolicy.setdealTerm(dealTerm);
        seeyonPolicy.setRemindTime(remindTime);
        flowData.setSeeyonPolicy(seeyonPolicy);  
        int iFlowType = 1;
        if(flowType != null){
	        switch(Integer.parseInt(flowType)){
	            case 0 : iFlowType = FLOWTYPE_SERIAL;
	            break;
	            case 1 : iFlowType = FLOWTYPE_PARALLEL;
	            break;
	            case 2 : iFlowType = FLOWTYPE_MULTIPLE;
	            break;
	            case 3 : iFlowType = FLOWTYPE_COLASSIGN;
	            break;
	        }
        }
        flowData.setType(iFlowType);
        flowData.setIsShowShortName(showShortName);
        return flowData;
    }
    
    /**
     * 设置操作的默认权限
     * @param app ：应用分类key
     * @param flowcomm： add 加签，col 会签，chuanyue 传阅，zhihui 知会
     * @return
     */
    public static BPMSeeyonPolicy getDefaultPolicy(HttpServletRequest request)
    {
    	String appName=request.getParameter("appName");
        String comm=request.getParameter("flowcomm");
        if(comm==null || "".equals(comm)){comm="add";}
    	BPMSeeyonPolicy seeyonPolicy=null;
    	if(Integer.toString(ApplicationCategoryEnum.edoc.getKey()).equals(appName))
        {//公文加签时默认权限
        	String edocType=request.getParameter("edocType");
        	if("add".equals(comm))
        	{
        		if(Integer.toString(EdocEnum.edocType.sendEdoc.ordinal()).equals(edocType)
        		|| Integer.toString(EdocEnum.edocType.signReport.ordinal()).equals(edocType))
        		{seeyonPolicy = new BPMSeeyonPolicy("shenpi","审批");}
            	else {
            		boolean isGovEdoc = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("edoc");
            		if(isGovEdoc) {
            			seeyonPolicy = new BPMSeeyonPolicy("shenpi", "审批");
            		} else {
            			seeyonPolicy = new BPMSeeyonPolicy("yuedu", "阅读");
            		}
            	}
        	}
        	else if("col".equals(comm))
        	{
        		seeyonPolicy = new BPMSeeyonPolicy("huiqian","会签");
        	}
        	else if("chuanyue".equals(comm))
        	{
        		seeyonPolicy = new BPMSeeyonPolicy("yuedu","阅读");
        	}
        	else if("zhihui".equals(comm))
        	{
        		seeyonPolicy = new BPMSeeyonPolicy("zhihui","知会");
        	}
        }
        else
        {
        	//TODO节点权限需要从前台传入,目前在后台直接设置相应节点策略
        	if("inform".equals(comm))
        		seeyonPolicy = new BPMSeeyonPolicy("inform","知会");
        	else
        		seeyonPolicy = new BPMSeeyonPolicy("collaboration","协同");
        }
    	return seeyonPolicy;
    }
    
    //---------------------------
    //辅助代码
    //-----------------------------
    public static FlowData flowdataFromRequest() {
        FlowData flowData = new FlowData();
        HttpServletRequest request = WebUtil.getRequest();
        String _desc_by = request.getParameter("process_desc_by");
        String desc_by = FlowData.DESC_BY_XML.equals(_desc_by) ? FlowData.DESC_BY_XML : FlowData.DESC_BY_PEOPLE;
        flowData.setDesc_by(desc_by);

        //�Flash编辑" - 高级模式
        if (FlowData.DESC_BY_XML.equals(desc_by)) {
            String xml = request.getParameter("process_xml");
            xml = StringEscapeUtils.unescapeJavaScript(xml);
            flowData.setXml(xml);
        }
        //�选人界面" - 简单模式
        else {
            // people中的每个元素是一个String型的loginId
            List<Party> people = new ArrayList<Party>();
            String[] types = request.getParameterValues("userType");            
            String[] ids = request.getParameterValues("userId");
            String[] userExcludeChildDepartment = request.getParameterValues("userExcludeChildDepartment");
            String[] names = request.getParameterValues("userName");
            String[] accountIds = request.getParameterValues("accountId");
            String[] accountShortNames = request.getParameterValues("accountShortname");
            String[] policyId = request.getParameterValues("policyId");
            String[] policyName = request.getParameterValues("policyName");
            String[] activityIds = request.getParameterValues("activityId");
            String node_process_mode = request.getParameter("node_process_mode") ;
            
            if(names == null && ids != null && types != null){
            	names = getElementNames(ids, types);
            }
            String flowType = request.getParameter("flowType");
            String showShortName = request.getParameter("isShowShortName");
            
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    String id = ids[i];
                    String type = getUserTypeByField(types[i]);
                    String name = names[i];
                    String accountId = accountIds[i];
                    String accountShortName = accountShortNames[i];
                    Party party = new Party(type, id, name, accountId, accountShortName);
                    if(policyId!=null && policyId.length>i && policyId[i]!=null && !"".equals(policyId[i]))
                    {    
                    	BPMSeeyonPolicy seeyonPolicy =  new BPMSeeyonPolicy(policyId[i],policyName[i]) ; 
                    	if(Strings.isNotBlank(node_process_mode) && !"user".equals(type)){
                    		seeyonPolicy.setProcessMode(node_process_mode) ;
                    	}
                    	party.setSeeyonPolicy(seeyonPolicy);
                    }
                    
                    if(activityIds != null && activityIds.length > i){
                    	party.setActivityId(activityIds[i]);
                    }
                    if(userExcludeChildDepartment!=null && userExcludeChildDepartment.length>i && userExcludeChildDepartment[i]!=null && "true".equals(userExcludeChildDepartment[i]))
                    {    
                    	party.setIncludeChild(false);
                    }
                    people.add(party);
                }
            }
            
            flowData.setPeople(people);
            
            BPMSeeyonPolicy seeyonPolicy =null;            
            seeyonPolicy=getDefaultPolicy(request);  
            if(Strings.isNotBlank(node_process_mode)){
            	seeyonPolicy.setProcessMode(node_process_mode) ;
            }
            String remindTime = request.getParameter("advanceRemind");
            String dealTerm = request.getParameter("deadline");
            seeyonPolicy.setdealTerm(dealTerm);
            seeyonPolicy.setRemindTime(remindTime);
            flowData.setSeeyonPolicy(seeyonPolicy);
            
            int iFlowType = 1;
            if(flowType != null){
	            switch(Integer.parseInt(flowType)){
		            case 0 : iFlowType = FLOWTYPE_SERIAL;
		            break;
		            case 1 : iFlowType = FLOWTYPE_PARALLEL;
		            break;
		            case 2 : iFlowType = FLOWTYPE_MULTIPLE;
		            break;
		            case 3 : iFlowType = FLOWTYPE_COLASSIGN;
		            break;
		            case 4 : iFlowType = FLOWTYPE_NEXTPARALLEL;
		            break;
	            }
            }
            flowData.setType(iFlowType);
            flowData.setIsShowShortName(showShortName);
        }
        
        return flowData;
    }
    
    public static FlowData flowDataFromXML(String flowXML){
        FlowData flowData = new FlowData();
        flowData.setDesc_by(FlowData.DESC_BY_XML);
        flowXML = StringEscapeUtils.unescapeJavaScript(flowXML);
        flowData.setXml(flowXML);
        return flowData;
    }
    
    //---------------------------
    //辅助代码
    //-----------------------------
    public static FlowData flowdataCurUserFromRequest(User user,BPMSeeyonPolicy seeyonPolicy) {
        
    	FlowData flowData = new FlowData();
        HttpServletRequest request = WebUtil.getRequest();
        flowData.setDesc_by(FlowData.DESC_BY_PEOPLE);

        // people中的每个元素是一个String型的loginId
        List<Party> people = new ArrayList<Party>();        
        String showShortName = request.getParameter("isShowShortName");
        String id =Long.toString(user.getId());
        String type = "user";
        String name = user.getName();
        String accountId = Long.toString(user.getLoginAccount());
        String accountShortName = "";
        Party party = new Party(type, id, name, accountId, accountShortName);
        party.setSeeyonPolicy(seeyonPolicy);                   
        people.add(party);
        
        flowData.setPeople(people);
        
        String remindTime = request.getParameter("advanceRemind");
        String dealTerm = request.getParameter("deadline");
        seeyonPolicy.setdealTerm(dealTerm);
        seeyonPolicy.setRemindTime(remindTime);
        
        flowData.setSeeyonPolicy(seeyonPolicy);
        int iFlowType = FLOWTYPE_SERIAL;
        flowData.setType(iFlowType);
        flowData.setIsShowShortName(showShortName);        
        return flowData;
    }
    
    public static String[] getElementNames(String[] ids, String[] types){
    	String[] names = new String[ids.length];
    	OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
    	for(int i=0; i<ids.length; i++){
    		try {
				names[i] = orgManager.getEntity(types[i], Long.parseLong(ids[i])).getName();
			} catch (NumberFormatException e) {
				log.error("", e);
			} catch (BusinessException e) {
				log.error("", e);
			}
    	}
    	return names;
    }
    
    public static FlowData flowdataFromMemberIds(int flowType, List<Long> memberIds, OrgManager orgManager){
    	User user = CurrentUser.get();
    	return flowdataFromMemberIds(user.getId(), flowType, memberIds, orgManager);
    }
    
    /**
     * 
     * @param sendId 流程发起者的id
     * @param flowType 0 并发 1 串发
     * @param memberIds
     * @param orgManager
     * @return
     */
    public static FlowData flowdataFromMemberIds(long sendId, int flowType, List<Long> memberIds, OrgManager orgManager){
    	List<String> types = new ArrayList<String>();
    	for (int i = 0; i < memberIds.size(); i++) {
    		types.add("Member");
		}
    	
    	return flowdataFromTypeIds(sendId, flowType, types, memberIds, orgManager);
    }
    
    /**
     * 
     * @param sendId 流程发起者的id
     * @param flowType 0 并发 1 串发
     * @param orgEntityTypes 流程节点的类型：Member，Department,Post,Level,Account等
     * @param orgEntityIds 流程节点的Id，与<code>orgEntityTypes</code>一一对应
     * @param orgManager
     * @return
     */
    public static FlowData flowdataFromTypeIds(long sendId, int flowType, List<String> orgEntityTypes, List<Long> orgEntityIds, OrgManager orgManager){
    	FlowData flowData = new FlowData();
    	
    	List<Party> people = new ArrayList<Party>();
    	
    	V3xOrgMember sender = null;
		try {
			sender = orgManager.getMemberById(sendId);
		}
		catch (Exception e1) {
			log.error("获取发起者对象不存在", e1);
			return null;
		}
		
    	long senderAccountId = sender.getOrgAccountId();
    	
    	for (int i = 0; i < orgEntityIds.size(); i++) {
			Long orgEntityId = orgEntityIds.get(i);
			
    		String name = "";
    		String accountId = "";
    		String accountShortName = "";
    		
			try {
				V3xOrgEntity entity = orgManager.getEntity(orgEntityTypes.get(i), orgEntityId);
				if(entity != null){
                    name = entity.getName();
    				accountId = entity.getOrgAccountId().toString();
    				accountShortName = orgManager.getAccountById(entity.getOrgAccountId()).getShortname();
    				if(entity.getOrgAccountId() != senderAccountId){
    					flowData.setIsShowShortName("true");
    				}
    				Party party = new Party(getUserTypeByField(orgEntityTypes.get(i)), String.valueOf(orgEntityId), name, accountId, accountShortName);
    				people.add(party);                    
                }
			}
			catch (BusinessException e) {
				log.error("", e);
				continue;
			}
			
		}
    	
        flowData.setPeople(people);
        int iFlowType = 0;
        switch(flowType){
            case 0 : iFlowType = FLOWTYPE_SERIAL;
            break;
            case 1 : iFlowType = FLOWTYPE_PARALLEL;
            break;
            case 2 : iFlowType = FLOWTYPE_MULTIPLE;
            break;
            case 3 : iFlowType = FLOWTYPE_COLASSIGN;
            break;
	    }
        flowData.setType(iFlowType);
    	
    	return flowData;
    }
    
    
    public static String getUserTypeByField(String userTypeFieldName) {
        if (StringUtils.isBlank(userTypeFieldName) || V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(userTypeFieldName)) {
            return "user";
        }

        return userTypeFieldName;
    }

	public BPMSeeyonPolicy getSeeyonPolicy() {
		return seeyonPolicy;
	}

	public void setSeeyonPolicy(BPMSeeyonPolicy seeyonPolicy) {
		this.seeyonPolicy = seeyonPolicy;
	}
	
	
    //减签中人员列表，转化成js表示
    public static String peopleToJs(FlowData flowData) {    	
        if (flowData == null) {
            return "null";
        }
        List<Party> people = flowData.getPeople();
        if (people == null || people.isEmpty()) {
            return "null";
        }

        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < people.size(); i++) {
            Party party = people.get(i);
            arr.add("[\""+party.id+"\",\""+party.type+"\",\""+party.name+"\",\""+party.accountId+"\",\""+party.accountShortName+"\"]");                        
        }

        String str = StringUtils.join(arr.iterator(), ",");
        
        str= "["+str+"]";
        return str;
    }
    
    public static String peopleToForm(FlowData flowData) {
        if (flowData == null) {
            return "";
        }

        int flowType = flowData.getType();

        StringBuffer buffer = new StringBuffer();
        List<Party> people = flowData.getPeople();
        for (Party party : people) {
//            buffer.append("<input type=\"hidden\" name=\"userType\" value=\"user\" />\n");
            buffer.append("<input type=\"hidden\" name=\"userType\" value=\"" + party.type + "\" />\n");
            buffer.append("<input type=\"hidden\" name=\"userId\" value=\"").append(party.id).append("\" />\n");
        }

        buffer.append("<input type=\"hidden\" name=\"flowType\" value=\"").append(flowType + "").append("\" />\n");

        return buffer.toString();
    }

    public static String peopleToReadable(FlowData flowData) {
        if (flowData == null) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        List<Party> people = flowData.getPeople();
        for (Party party : people) {
            buffer.append(party.id).append(",");
        }
        return buffer.toString();
    }

	public String getIsShowShortName() {
		return isShowShortName;
	}

	public void setIsShowShortName(String isShowShortName) {
		this.isShowShortName = isShowShortName;
	}

	public Map<String,String> getUsedPolicy() {
		return usedPolicy;
	}

	public void setUsedPolicy(Map<String,String> usedPolicy) {
		this.usedPolicy = usedPolicy;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

}