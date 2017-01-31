<%@page import="com.seeyon.v3x.common.SystemEnvironment"%>
<%@page import="com.seeyon.v3x.common.flag.SysFlag"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.seeyon.v3x.common.taglibs.functions.*" %>
<%@ include file="../common/INC/noCache.jsp"%>
<%
	//branches_a8_v350_r_gov GOV-2506  唐桂林修改政务收文默认节点权限为审批 start
	boolean isGovEdoc = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("edoc");
	request.setAttribute("isGovEdoc", isGovEdoc);
	//branches_a8_v350_r_gov GOV-2506  唐桂林修改政务收文默认节点权限为审批 end
%>
<html>
<head>
<%-- 
流程图配置参数清单：
| 名称                  |类型    |默认值 |说明
1. unallowSetBrach   |boolean|false |不允许设置分支

--%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="Collaborationheader.jsp"%>
<title><fmt:message key="monitor.title" /></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/workflow.css"/>">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/workflow/flash_patch.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/monitor.js${v3x:resSuffix()}" />"></script>
<link rel="STYLESHEET" type="text/css" href="<c:url value="/apps_res/form/css/form.css${v3x:resSuffix()}" />">
<script type="text/javascript" src="<c:url value="/common/js/monitor/workflow.js${v3x:resSuffix()}"/>"></script>
<script type="text/javascript" src="<c:url value="/common/workflow/workflowDesigner_decode.js${v3x:resSuffix()}"/>"></script>
<c:set value="${v3x:currentUser().id}" var="currentUserId"/>
<script type="text/javascript">
<!--
var _parent = parent;	//正常显示
var currentUserId = "${currentUserId}";
var _comm="${param.comm}";
var isFormTemplate = false;
var isNew = "${param.isNew}";
var isPublicReadState = false;
var isTempleteSupervise = "${param.isTempleteSupervise}";
var jsReady = false;
var theResizeFlag = 'false';
<c:if test="${param.isNew ne 'true'}">
	isPublicReadState = ${v3x:isEnableSwitch('ReadState')};
</c:if>
if(window.dialogArguments && window.dialogArguments.isFormTemplate){
	isFormTemplate = window.dialogArguments.isFormTemplate;
	if(isFormTemplate){
		_parent = window.dialogArguments;
	}
}
if(isFormTemplate){
	_parent = window.dialogArguments;
}
var moreOptionI18N = _('collaborationLang.moreOption');
if(_comm == "toxml" || (window.dialogArguments && window.dialogArguments.contentFrame)||isFormTemplate) 
{
	if(_comm!="toxml"&& !isFormTemplate)
	{
		<c:if test="${!empty param.frameNames}">
			<c:set value=".${param.frameNames}" var="frameNames" />
		</c:if>
		//流程编辑
		var isDetail = "${param.isDetail}";
		var temObj = window.dialogArguments.contentFrame.mainFrame${frameNames};
   	    if(temObj.editWorkFlowFlag == 'true' && (_parent!=null || _parent!=undefined) && !_parent.hasDiagram && isDetail!= '1')
   	    {
   		    _parent = temObj;
   	    }
    }
    <%--不同应用的节点属性id,name默认值--%>
    var defaultPolicyId = "";
    var defaultPolicyName = "";
	if(_parent.appName == "collaboration"){
		defaultPolicyId = "collaboration";
		defaultPolicyName = _('collaborationLang.collaboration');
	}else if(_parent.appName == "sendEdoc" || _parent.appName == "signReport"){
		defaultPolicyId = "shenpi";
		defaultPolicyName = _('collaborationLang.edoc_sendEdocPolicyName');
	}else if(_parent.appName == "recEdoc"){
		//branches_a8_v350_r_gov GOV-2143  唐桂林修改政务收文默认节点权限为审批 start
		if(${isGovEdoc}) {
			defaultPolicyId = "shenpi";
			defaultPolicyName = _('collaborationLang.edoc_sendEdocPolicyName');
		} else {
			defaultPolicyId = "yuedu";
			defaultPolicyName = _('collaborationLang.edoc_recEdocPolicyName');
		}
		//branches_a8_v350_r_gov GOV-2143  唐桂林修改政务收文默认节点权限为审批 end
	} else if(_parent.appName == "sendInfo"){
		defaultPolicyId = "shenhe";
		defaultPolicyName = _('collaborationLang.info_sendInfoPolicyName');
	} 
	<%--菜单的国际化--%>
	var addNodeI18N = _('collaborationLang.addNode');
	var deleteNodeI18n = _('collaborationLang.deleteNode');
	var replaceNodeI18n = _('collaborationLang.replaceNode');
	var nodePropI18N = _('collaborationLang.nodeProp');
	var newWorkflowI18N = _('collaborationLang.newWorkflow');
	var submitI18N = _('collaborationLang.submit');
	var cancelI18N = _('collaborationLang.cancel');
	var autoConditionI18N = _('collaborationLang.autoCondition');
	var handworkConditionI18N = _('collaborationLang.handworkCondition');
	
	var deleteConditionI18N = _('collaborationLang.deleteCondition');
	var mainMenuItem = new Array(addNodeI18N, deleteNodeI18n, replaceNodeI18n, nodePropI18N);
	if(_parent.formApp){
		mainMenuItem = new Array(addNodeI18N, deleteNodeI18n, replaceNodeI18n, nodePropI18N, newWorkflowI18N);
	}
	var lineMenuItem = new Array(autoConditionI18N, handworkConditionI18N, moreOptionI18N, deleteConditionI18N);
	var buttonName = new Array(submitI18N, cancelI18N);
	var formApp = "";
	var formName = "";
	var operationName = "";
	if(isFormTemplate){
		if(_parent.formApp){
	    	formApp = _parent.formApp;
	    }
	    if(_parent.defaultForm){
	    	formName = _parent.defaultForm;
	    }
	    if(_parent.defaultOtherNodeOperationId){
	    	operationName = _parent.defaultOtherNodeOperationId;
	    }
	}
	var unallowSetBrach = false;
	if(_parent.unallowSetBrach){
		unallowSetBrach = _parent.unallowSetBrach;
	}
    var data = [_parent.appName, defaultPolicyId, defaultPolicyName, _parent.isTemplete, mainMenuItem, lineMenuItem, buttonName, isFormTemplate, formApp, formName, operationName, unallowSetBrach];
}

var isInternetExplorer = navigator.appName.indexOf("Microsoft") != -1;
var currentUser = [currentUserId, "${v3x:escapeJavascript(v3x:currentUser().name)}", "${v3x:getAccount(v3x:currentUser().loginAccount).shortname}"];
//政务默认发起者
if(${isGovEdoc}) {
	if(_parent!=null) {
		if(_parent.agentToId!=null && _parent.agentToId!="" && _parent.agentToId!='undefined' && _parent.agentToId!=-1 && _parent.appName=='recEdoc') {
			currentUser = [_parent.agentToId, _parent.agentToName, _parent.agentToAccountShortName];	
		}	
	}
}
var keys = _parent.keys;
var branchs = new Array();
if(_parent.branchs){
	if(_parent.hasKeys && keys){
		for(i=0;i<keys.length;i++){
			var k = keys[i];
			if(_parent.branchs[k]){
				branch = new ColBranch();
				branch.id = _parent.branchs[k].id;
				branch.conditionType = _parent.branchs[k].conditionType;
				branch.formCondition = _parent.branchs[k].formCondition;
				branch.conditionTitle = _parent.branchs[k].conditionTitle;
				branch.conditionDesc = _parent.branchs[k].conditionDesc;
				branch.isForce = _parent.branchs[k].isForce;
				branch.conditionBase = _parent.branchs[k].conditionBase;
				branchs[k] = branch;
			}
		}
	}
	else{
		branchs = _parent.branchs;		
	}
}

var policys = new Array();
var nodes = new Array();

var depLabel = "<fmt:message key='org.department.label' bundle="${v3xMainI18N}" />".replace(/ /gi,"_");
var postLabel = "<fmt:message key='org.post.label'  bundle="${v3xMainI18N}"/>".replace(/ /gi,"_");
var levelLabel = "<fmt:message key='org.level.label'  bundle="${v3xMainI18N}"/>".replace(/ /gi,"_");
var accountLabel = "<fmt:message key='org.account.label'  bundle="${v3xMainI18N}"/>".replace(/ /gi,"_");
var checkLabel = "<fmt:message key='col.branch.check'/>";
var uncheckLabel = "<fmt:message key='col.branch.uncheck'/>";
var depSys = "sys:dep";
var postSys = "sys:post";
var levelSys = "sys:level";
var accountSys = "sys:account";
var checkedForm = "form:check";
var uncheckedForm = "form:uncheck";

var replace = false;
var monitorObj = isInternetExplorer ? document.all.monitor : document.monitor;
function dataToFlash(elements){
	if(!replace){
		callFlashAction("selectAddNodePeople", elements);
	}
	else{
		callFlashAction("replaceHumenNode", elements);
	}
}
function falshSelectOk(args)
{
	args= decodeArgsFromFlash(args);
	var xmlValue;
	if(typeof args == 'object'){
		xmlValue = args;
	}
	else if(typeof args == 'string'){
		xmlValue = args.split(",");
	}
    _parent.document.getElementsByName("sendForm")[0].process_desc_by.value= xmlValue[0];
    _parent.document.getElementsByName("sendForm")[0].process_xml.value = xmlValue[1];
    _parent.caseProcessXML = xmlValue[1];
    _parent.hasWorkflow = true;
    _parent.document.getElementsByName("sendForm")[0].workflowInfo.value = xmlValue[2];
}

var superviseEelments = null;
function dataToEdocFlash(elements){
	superviseEelments = elements;
}

<%-- JS回调Flash，将其独立出来，用setTimeout机制解决回调时的脚本运行缓慢问题。--%>
var flashAction = null;
var flashActionArgs = null;
function callFlashAction(action, args){
	lockSubmit(true);
	flashAction = action;
	flashActionArgs = args;
	setTimeout("flashActionDispatcher()", 200);
}
function lockSubmit(lock){
	var submitButton = document.getElementById('submitButton') || document.getElementById('confirmButton');
	if(submitButton){
		submitButton.disabled = lock;
	}
}
function flashActionDispatcher(){
	if(!jsReady || !monitorObj){
		monitorObj = isInternetExplorer ? document.all.monitor : document.monitor;
	}
	var result = flashActionArgs;
	if(flashAction == "selectAddNodePeople"){
		monitorObj.selectAddNodePeople(result);
	}
	else if(flashAction == "replaceHumenNode"){
		monitorObj.replaceHumenNode(result);
	}
	else if(flashAction == "setProp"){
		//alert(result[0]+";"+result[1]+";"+result[2]+";"+result[3]+";"+result[4]+";"+result[5]+";"+result[6]+";"+result[7]+";"+result[9]+";"+result[10]+";"+result[11]+";"+result[12]+";"+result[13]+";"+result[14]+";"+result[15]+";"+result[16]+";"+result[17]);
		monitorObj.setProp(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7], result[9], result[10], result[11],result[12],result[13],result[14],result[15],result[16],result[17]);
	}
	else if(flashAction == "handCondition"){
		monitorObj.handCondition(result[0], result[1], result[2], result[3]);
	}
	else if(flashAction == "autoCondition"){
		monitorObj.autoCondition(result[0], result[1], result[2], result[3], result[4], result[5]);
	}
	else if(flashAction == "edocRefresh_Flash"){
		monitorObj.edocRefresh_Flash(result[0], result[1], result[2]);
	}
	else if(flashAction == "updateNewWorkflow"){
		monitorObj.updateNewWorkflow(result[0]);
	}
	flashAction = null;
	flashActionArgs = null;
	lockSubmit(false);
}
var workFlow = null;
<%-- 从Flash中调用的JS方法 --%>
function getXML(args) {
	var monitorObj = isInternetExplorer ? document.all.monitor : document.monitor;
	
    var currentLocale = "${v3x:getLanguage(pageContext.request)}";
    var hurryButton = _('collaborationLang.hurryBotton');
    var startUser = _('collaborationLang.startUser');
    var workflowRule = _('collaborationLang.workflowRule');
    var splitCondition = _('collaborationLang.splitCondition');
    
    //人员状态国际化
	var personDelete = _('collaborationLang.person_delete');
	var personDimission = _('collaborationLang.person_dimission');
	var personUnAssign = _('collaborationLang.person_unAssign');
	var personTruce = _('collaborationLang.person_truce');
	var personStatus = new Array(personDelete, personDimission, personUnAssign, personTruce);
	var viewMainFlowI18N = _('collaborationLang.viewMainFlow');
	var viewNewFlowI18N = _('collaborationLang.viewNewFlow');
	//flash帮助国际化
	var operationHelpText = _('V3XLang.common_workflow_editHelp');
	var informName = _("collaborationLang.inform");
	var colon = _("collaborationLang.colon"); //冒号
	if(monitorObj){
		try{
			if(!monitorObj.getLocale){
				setTimeout("getXML()",100);
				return;
			}
		    monitorObj.getLocale(currentLocale, hurryButton, startUser, workflowRule, splitCondition, personStatus, operationHelpText, viewMainFlowI18N, viewNewFlowI18N,moreOptionI18N+colon,informName);
		}catch(e){
			alert(e);
		}
	    var isFromEdoc = false;
		if(_parent.isFromEdoc){
			isFromEdoc = _parent.isFromEdoc;
		}
		var sendForm = _parent.document.getElementsByName("sendForm");
	    if((sendForm.length>0 && sendForm[0].process_desc_by.value!='xml')||${param.isNew=='true'}){
	        monitorObj.initData(data2Str(data));
	        monitorObj.asFun(null,null,null,_parent.showMode,currentUser,_parent.currentNodeId,_parent.showHastenButton,
	        	_parent.isCheckTemplete,_parent.isShowWorkflowRuleLink, isFromEdoc, _parent.newflowNodeIdsStr, isPublicReadState);
	        monitorObj.initPeople(_parent.selectedElements);
	    }
	    else{
			if(${param.isFormBind != "true"} && _parent.isTempleteFromMake && _parent.isTempleteFromMake!=undefined && _parent.isTempleteFromMake=="true"){//表单制作页面弹出的关联文档中的流程图
		    	if(parent.caseProcessXML == null || parent.caseProcessXML == ""){
					parent.initCaseProcessXML();
		    	}
		    	data[3]=false;//关联文档不是模板
				monitorObj.initData(data2Str(data)); 
		   		monitorObj.asFun(parent.caseProcessXML, parent.caseLogXML, parent.caseWorkItemLogXML, parent.showMode,currentUser,
				   		parent.currentNodeId,parent.showHastenButton,parent.isCheckTemplete,parent.isShowWorkflowRuleLink, isFromEdoc, parent.newflowNodeIdsStr, isPublicReadState);
			}else{
				if((_parent.caseProcessXML == null || _parent.caseProcessXML == "") && !_parent.isTemplete){
		     		_parent.initCaseProcessXML();
		    	}
				monitorObj.initData(data2Str(data));   
		   		monitorObj.asFun(_parent.caseProcessXML, _parent.caseLogXML, _parent.caseWorkItemLogXML, _parent.showMode,currentUser,
		   		_parent.currentNodeId,_parent.showHastenButton,_parent.isCheckTemplete,_parent.isShowWorkflowRuleLink, isFromEdoc, _parent.newflowNodeIdsStr, isPublicReadState);
			}
	    }
	    if(_comm=="toxml"){
	  		monitorObj.getDataXML();
		}
	}else{
		try{
		//HTML5展现
		if(workFlow){
			return ;
		}
		if((_parent.caseProcessXML == null || _parent.caseProcessXML == "") && !_parent.isTemplete){
	     	_parent.initCaseProcessXML();
	    }
	    workFlow = WorkFlowDraw.initWork(_parent.caseProcessXML, _parent.caseLogXML, _parent.caseWorkItemLogXML);
	    if(workFlow){
	    	workFlow.init(v3x.baseURL);
		}
		}catch(e){}
	}
}
function data2Str(theDate){
	if(!theDate)return "";
	var result = "";
	for(var i = 0;i< theDate.length;i++){
		if(result.length != 0){
			result += "|";
		}
		if(theDate[i] instanceof Array){
			result+=theDate[i].join(",");
		}else{
			result += theDate[i];
		}
	}
	return result;
}
function submit(args){
	args= decodeArgsFromFlash(args);
	var xml;
	if(typeof args == 'object'){
		xml = args;
	}
	else if(typeof args == 'string'){
		xml = args.split(",");
	}
    if(!xml || !xml[1] || xml[1]=="null"){
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
    }
    else if(document.getElementById("ruleContent") && document.getElementById("ruleContent").value.length>2000){
    	alert(v3x.getMessage("collaborationLang.collaboration_ruleContentOverflow"));
    }
    else{
        falshSelectOk(args);
        if(_parent.isTemplete){
        	_parent.branchs = branchs;
        	_parent.keys = keys;
        }
        if(nodes.length>0){
			_parent.policys = policys;
			_parent.nodes = nodes;
        }
        window.returnValue=true;
    	window.close();
    }
}

var isNodeAfterHasCondition= false;
/**
 * 设置为自动跳过时要先进行的校验
 */
function verifyTermSkip(args){
	args= decodeArgsFromFlash(args);
	var xml;
	if(typeof args == 'object'){
		xml = args;
	}
	else if(typeof args == 'string'){
		xml = args.split(",");
	}
    if(!xml || !xml[1] || xml[1]=="null"){
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
    }else{
    	try {
	    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasConditionAfterSelectNode", false, "POST");
			requestCaller.addParameter(1, "String", xml[1]);
			requestCaller.addParameter(2, "String", xml[3]);
			var rs= requestCaller.serviceRequest();
			if(rs=="TRUE" || rs=="true"){//当前节点之后有设置分支
				isNodeAfterHasCondition= true;
			}else{
				isNodeAfterHasCondition= false;
			}
    	}catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}
    }
}
var isAutoSkipNodeBeforeCondition= false;
var currentAutoSkipNodeName="";
/**
 * 设置为分支条件时要先进行的校验：分支前面是否有可以自动跳过的节点
 */
function verifyTermSkipBeforeBranch(args){
	args= decodeArgsFromFlash(args);
	var xml;
	if(typeof args == 'object'){
		xml = args;
	}
	else if(typeof args == 'string'){
		xml = args.split(",");
	}
    if(!xml || !xml[1] || xml[1]=="null"){
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
    }else{
    	try {
	    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasAutoSkipNodeBeforeSetCondition", false, "POST");
			requestCaller.addParameter(1, "String", xml[1]);
			requestCaller.addParameter(2, "String", xml[3]);
			var rs= requestCaller.serviceRequest();
			if(rs[0]=="TRUE" || rs[0]=="true"){//当前分支之前有设置自动跳过节点
				isAutoSkipNodeBeforeCondition= true;
				currentAutoSkipNodeName= rs[1];
			}else{
				isAutoSkipNodeBeforeCondition= false;
				currentAutoSkipNodeName= "";
			}
    	}catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}
    }
}

function cancel(args) {
	window.close();
}

function workflowRule(args) {
	showWorkflowRule();
}

function addNodeItem(args) {
	args= decodeArgsFromFlash(args);
	var nodeType;
	if(typeof args == 'object'){
		nodeType = args;
	}
	else if(typeof args == 'string'){
		nodeType = args.split(",");
	}
	if(nodeType[0] == "StartNode" || nodeType[0] == "SynNode"){
		hiddenColAssignRadio_newColl = true;
		hiddenColAssignRadio_newTemplete = true;
	}else{
		hiddenColAssignRadio_newColl = false;
		hiddenColAssignRadio_newTemplete = false;
	}
		
	var isTemplete = _parent.isTemplete;
	flowSecretLevel_newColl = "${param.secretLevel}";
	flowSecretLevel_newTemplete = "${param.secretLevel}";
	if(isTemplete){
		selectPeopleFun_newTemplete();
	}else{
		selectPeopleFun_newColl();
	}
}

function replaceNode(args){
	args= decodeArgsFromFlash(args);
	replace = true;
	hiddenFlowTypeRadio_newTemplete = true;
	hiddenFlowTypeRadio_newColl = true;
	flowSecretLevel_newColl = "${param.secretLevel}";
	flowSecretLevel_newTemplete = "${param.secretLevel}";
	var isTemplete = _parent.isTemplete;
	if(isTemplete){
		selectPeopleFun_newTemplete();
	}else{
		selectPeopleFun_newColl();
	}
	hiddenFlowTypeRadio_newTemplete = false;
	hiddenFlowTypeRadio_newColl = false;
	replace = false;
}

var _isNewColl = false ;
var secretLevel = '${param.secretLevel}';
function propertyItem(args) {
	args= decodeArgsFromFlash(args);
	try{
		var isTemplete = _parent.isTemplete;
		var dealTerm = "";
		var remindTime = "";
		var excuteMode = "";
		var propArr;
		if(typeof args == 'object'){
			propArr = args;
		}
		else if(typeof args == 'string'){
			propArr = args.split(",");
		}
		if(_parent.isNewColl && _parent.isNewColl == true ){
			_isNewColl = _parent.isNewColl ;
		}
		
		var nodeName = propArr[0];
		var policyName = propArr[1];
		
		var formAppName = "";                       //表单应用id
		if(isFormTemplate)
		  formAppName = _parent.formApp;
		var formApp = "";                           //表单应用id
		var form = "";                              //单据id
		var operationName = "";                     //操作id
		
		if(propArr[2] != null && propArr[2] != "null"){
			dealTerm = propArr[2];
		}else{
			dealTerm = 0;
		}
		if(propArr[3] != null && propArr[3] != "null"){
			remindTime = propArr[3];
		}else{
			remindTime = -1;
		}
		processMode = propArr[4];
		if(propArr[5] != null && propArr[5] != "null" && propArr[5] != "undefined"){
			formApp = propArr[5];
		}else{
		    formApp = formAppName;
		}
		form = propArr[6];
		operationName = propArr[7];
		var nodeType = propArr[8];
		var isEditor = propArr[9];
		var nodeId=propArr[10];		
		var partyId = propArr[11];
		var partyType = propArr[12];
		var matchScope = propArr[13];
		var stateStr = propArr[14];

		var hasNewflow = propArr[15];
		var hasBranch = propArr[16];
		var formfiled =  propArr[17] || "";
		<%--发起节点显示当前用户名还是‘发起者’，用于表单绑定时显示用--%>
		var desc = propArr[18];
		var dealTermType= propArr[19];
		var dealTermUserId= propArr[20];
		var dealTermUserName= propArr[21];
		var showSenderName = _parent.showSenderName;

	}
	catch(e){alert(e)}
	var result = selectPolicy(partyId, partyType, nodeName,policyName,dealTerm,remindTime,processMode,matchScope,isTemplete,_parent.appName,
	formApp, form, operationName, nodeType, isEditor, defaultPolicyId, showSenderName, stateStr, hasNewflow, hasBranch,
	${param.isFromSupervise ne 'true' && param.endFlag ne 'true'},formfiled,_parent.summaryId,nodeId,desc,dealTermType,dealTermUserId,dealTermUserName,secretLevel);
	//for(var i=0;i<result.length;i++){
    	//alert("result["+i+"]:="+result[i]);
    //}
	if(result){
       	nodes[nodes.length] = propArr[10];
		eval("policys['"+propArr[10]+"']=new NameAndValue('"+result[0]+"','"+result[8]+"')");
        callFlashAction("setProp", result);
    }
}

<%-- Flash国际化，Flash提示信息待清理 --%>
function showAlert(args) {
	args= decodeArgsFromFlash(args);
	try{
		var message = _("collaborationLang.FlashTip_" + args) || args;
		alert(message);
	}
	catch(e){alert(e)}
}

function hurry(args) {
	var arr = [];
	if(typeof args == 'object'){
		arr = args;
	}
	else if(typeof args == 'string'){
		arr = args.split(",");
	}
    var activityId = arr[0];
    var processId = arr[1];
    var memberIds = arr[2];
    var superviseId = "${param.superviseId}";
    preHasten(parent.summary_id, memberIds, activityId,superviseId);
}

function checkPropertyItem(args) {
	args= decodeArgsFromFlash(args);
	var nodeInfo = [];
	if(typeof args == 'object'){
		nodeInfo = args;
	}
	else if(typeof args == 'string'){
		nodeInfo = args.split(",");
	}
	var stateStr  = nodeInfo[0];
	var nodeName = nodeInfo[1];
	var nodePolicy = nodeInfo[2];
	var receiveTime = "";
	var dealTime = "";
	var overtopTime = "";
	var processMode = "";
	var remindTime = "";
	if(!nodeInfo[3] || nodeInfo[3]=="null"){
		receiveTime = "";
	}else{
		receiveTime = nodeInfo[3];
	}
	if(!nodeInfo[4]|| nodeInfo[4] == "null"){
		completeTime = "";
	}else{
		completeTime = nodeInfo[4];
	}
	if(!nodeInfo[5] || nodeInfo[5] == "null"){
		overtopTime = "";
	}else{
		overtopTime = nodeInfo[5];
	}
	if(!nodeInfo[6]){
		processMode = "";
	}else{
		processMode = nodeInfo[6];
	}
	if(!nodeInfo[7]){
		policyId = "";
	}else{
		policyId = nodeInfo[7];
	}
	if(!nodeInfo[8] || nodeInfo[8] == "null"){
		dealTime = "";
	}else{
		dealTime = nodeInfo[8];
	}
	if(!nodeInfo[9] || nodeInfo[9] == "null"){
		remindTime = "-1";
	}else{
		remindTime = nodeInfo[9];
	}
	var partyId = nodeInfo[10];
	var partyType = nodeInfo[11];
	var matchScope = nodeInfo[12];
	var nodeId = nodeInfo[13];
	var desc = nodeInfo[14];
	var formApp = nodeInfo[15];
	var formId = nodeInfo[16];
	var operationId = nodeInfo[17];
	
	var templeteId ="${param.templeteId}";//主要为了草稿以及管理员页面
	if(templeteId==''&&_parent.document.getElementById("templeteId")!=null){
		templeteId = _parent.document.getElementById("templeteId").value; 
	}
	var affairId = "${param.affairId}";
	if( affairId==undefined || affairId==""){
		affairId = _parent.affair_id;
	}
	var summaryId = "${param.summaryId}"
	var random = Math.random();//主要为了表单绑定模态窗口的缓存
	var dealTermType= nodeInfo[18];
	var dealTermUserId= nodeInfo[19];
	var dealTermUserName= nodeInfo[20];
	if(nodeId== null || nodeId=="null" || nodeId=="undefined"){//例如从部门节点下来的人员中单击时，就不弹出属性页面了
		return;
	}else{//正常显示属性
		checkPolicy(stateStr,nodeName,nodePolicy,receiveTime,completeTime,overtopTime,_parent.isTemplete,
				processMode,policyId,dealTime,remindTime,_parent.appName,affairId, partyId, partyType, 
				matchScope,templeteId,nodeId,summaryId,random,desc,formApp,formId,operationId,dealTermType,dealTermUserId,dealTermUserName);
	}
}
<%--调用flash的getDataXML()方法后回调此方法--%>
function toXML(args) {
	args= decodeArgsFromFlash(args);
	var xml = args;
	falshSelectOk(xml);
	_parent.processing=false;
}
<%--分支自动条件--%>
function autoOption(args){
	args= decodeArgsFromFlash(args);
	//分支之前是否有设置自动跳过的节点，如果有，则提示不能设置分支
	verifyTermOfSkipBeforeBranch(args[0]);
	if(isAutoSkipNodeBeforeCondition){
		alert(v3x.getMessage("collaborationLang.dealterm_skip_node_before_branch",currentAutoSkipNodeName));
		isAutoSkipNodeBeforeCondition= false;
		currentAutoSkipNodeName= "";
		return;
	}
    var branch = branchs[args[0]];
    var condition = setCondition(isFormTemplate?1:0,branch,args[0],_parent.appName);
	if(condition!=null){
		eval("branchs["+args[0]+"]=new ColBranch()");
        var existKey = false;
		for(var i=0;i<keys.length;i++){
			if(keys[i]==args[0])
				existKey = true;
		}
		if(!existKey){
        	keys[keys.length] = args[0];
		}
		branchs[args[0]].id = condition[0];
		branchs[args[0]].formCondition = condition[1];
		branchs[args[0]].conditionTitle = condition[2];
		branchs[args[0]].conditionType = 1;
		branchs[args[0]].isForce = condition[4];
		branchs[args[0]].conditionBase = condition[3];
		if(branch && branch.conditionDesc){
			branchs[args[0]].conditionDesc = branch.conditionDesc;
		}
		var callbackResult = [1, condition[1], condition[0], condition[4], condition[3],condition[2]];
		callFlashAction("autoCondition", callbackResult);
	}
}
<%--分支手动条件--%>
function handOption(args){
	args= decodeArgsFromFlash(args);
	verifyTermOfSkipBeforeBranch(args[0]);
	if(isAutoSkipNodeBeforeCondition){
		alert(v3x.getMessage("collaborationLang.dealterm_skip_node_before_branch",currentAutoSkipNodeName));
		isAutoSkipNodeBeforeCondition= false;
		currentAutoSkipNodeName= "";
		return;
	}
	var branch = branchs[args[0]];
	if(branch==undefined){
		eval("branchs["+args[0]+"]=null");
		eval("branchs["+args[0]+"]=new ColBranch()");
        var existKey = false;
		for(var i=0;i<keys.length;i++){
			if(keys[i]==args[0])
				existKey = true;
		}
		if(!existKey)
        	keys[keys.length] = args[0];
	}
	var id = "" + getUUID();
	branchs[args[0]].id = id;
	branchs[args[0]].formCondition = null;
	branchs[args[0]].conditionTitle = _('collaborationLang.handworkChoice');
	branchs[args[0]].conditionType = 2;
	branchs[args[0]].isForce = "0";
	var callbackResult = [2, "", id, ""];
	callFlashAction("handCondition", callbackResult);
}

<%--删除分支条件--%>
function delOption(args){
	args= decodeArgsFromFlash(args);
	var branch = branchs[args[0]];
	if(branch){
		var confirmTip = v3x.getMessage("collaborationLang.branch_confirmdelcondition");
		if(args.length > 1 && args[1] == "OnlyDelete"){<%-- 兼容旧数据，支持删除不合理分支. --%>
			confirmTip = v3x.getMessage("collaborationLang.branch_confirm_delete_irrationality");
		}
	   	if(confirm(confirmTip)){
			eval("branchs["+args[0]+"]=null");
			monitorObj.delCondition(3, "", "", "", "");
		}
	}
	else{
		alert(v3x.getMessage("collaborationLang.branch_firstsetcondition"));
        return;
	}
}

<%--更多条件--%>
function moreOption(args){
	args= decodeArgsFromFlash(args);
	var branch = branchs[args[0]];
	var moreInfo = "";
	if(branch==undefined){
        alert(v3x.getMessage("collaborationLang.branch_firstsetcondition"));
        return;
	}
	else{
		moreInfo = branch.conditionDesc;
	}
	moreInfo = moreInfo==null||moreInfo==undefined?"":moreInfo;
	moreInfo = moreCondition(moreInfo);
	if(moreInfo!=null&&moreInfo!=undefined){
		branchs[args[0]].conditionDesc = moreInfo;
	}
}

function offshootDesc(args){
	args= decodeArgsFromFlash(args);
	if(!branchs){
		return;
	}
	var branch = branchs[args[0]];
	var str = "";
	var conditionTitle = "";
	if(branch!=null && branch != undefined && (branch.conditionDesc != null || branch.conditionTitle != null)){
		str = branch.conditionDesc==null?"":branch.conditionDesc;
		if(branch.conditionType == 2)
			conditionTitle = _('collaborationLang.handworkChoice');
		else if(branch.conditionTitle){
			conditionTitle = transformTitle(branch.conditionTitle);
		}
	}
	else if(args.length > 1 && args[1] != null && args[1] != "null"){
		conditionTitle = transformTitle(args[1]);
	}
	monitorObj.getOffshootDesc(conditionTitle, str);

	function transformTitle(conditionTitle){
		var regDep = new RegExp("[\[]"+depSys+"[\]]","gi");
		var regPost = new RegExp("[\[]"+postSys+"[\]]","gi");
		var regLevel = new RegExp("[\[]"+levelSys+"[\]]","gi");
		var regAccount = new RegExp("[\[]"+accountSys+"[\]]","gi");
		var regChecked = new RegExp("[\"]"+checkedForm+"[\"]","gi");
		var regUnchecked = new RegExp("[\"]"+uncheckedForm+"[\"]","gi");
		var theConditionTitle = conditionTitle.replace(regDep,"["+depLabel+"]").replace(regPost,"["+postLabel+"]").replace(regLevel,"["+levelLabel+"]")
		.replace(regAccount,"["+accountLabel+"]").replace(/&quot;/gi,"\"").replace(/&#44;/gi,",")
		.replace(regChecked,"\""+checkLabel+"\"").replace(regUnchecked,"\""+uncheckLabel+"\"");
		return theConditionTitle;
	}
}
	
function edocSupervise(args) {
	args= decodeArgsFromFlash(args);
    var arr = [];
	if(typeof args == 'object'){
		arr = args;
	}
	else if(typeof args == 'string'){
		arr = args.split(",");
	}
    var activityId = arr[0];
    var processId = arr[1];
    var memberIds = arr[2];
    var superviseId = _parent.document.getElementById("superviseId");
    if(superviseId){
    	if(${param.isFromHasten eq 'edocSupervise'}){
			edocHasten(_parent.summaryId,processId,activityId,superviseId.value, memberIds);
		}
		else{
			colHasten(_parent.summaryId,superviseId.value, memberIds, activityId);
		}
  	}
}

function edocAddNodeItem(args) {
	args= decodeArgsFromFlash(args);
	var arr = [];
	if(typeof args == 'object'){
		arr = args;
	}
	else if(typeof args == 'string'){
		arr = args.split(",");
	}
	var _processId = arr[0];
	var _activityId = arr[1];
	var _operationType = arr[2];
	var _currentNodeStateStr = arr[3]; <%--当前节点的处理状态 1-为分配 2-待办--%>
	hiddenColAssignRadio_updateEdoc = false;
	flowSecretLevel_updateEdoc = "${param.secretLevel}";
	if(!selectPeopleFun_updateEdoc() || superviseEelments == null){
		return;
	}
	var flowType = superviseEelments[1]; //流程类型
	<%--当前是待办/暂存待办节点，会签一个节点，那么_operationType应该等同于在已办后面增加节点--%>
	if(flowType == 3 && (_currentNodeStateStr == 2 || _currentNodeStateStr == 7)){
		_operationType = 2;
	}
    var currentSummaryId = _parent.summaryId;
    var _result = false;
    var _result = null;
	var appName = "";
	if(_parent.appEnumStr){
		appName = _parent.appEnumStr;
	}else{
		appName = _parent.appName;
	}
	if(appName == "collaboration" || appName == "form"){
	    _result = _parent.updateFlash(_processId,_activityId,_operationType,superviseEelments,"addNode",null,null,currentSummaryId,null,null,true);
	}else{
		_result = _parent.updateFlash(_processId,_activityId,_operationType,superviseEelments,"addNode",null,null,currentSummaryId,null,null,false);
	}
	if(_result != null){
		_parent.document.getElementById("process_xml").value = _result[0];
		var callbackResult = [_result[0], _result[1], _result[2]];
		callFlashAction("edocRefresh_Flash", callbackResult);
	}
}

function edocDelNodeItem(args) {
	args= decodeArgsFromFlash(args);
	if(window.confirm(_("collaborationLang.supervise_confirmDeleteNode"))){
		edocDelNodeItemHelper(args);
	}
}

function edocReplaceNodeItem(args){
	args= decodeArgsFromFlash(args);
   	var arr = [];
	if(typeof args == 'object'){
		arr = args;
	}
	else if(typeof args == 'string'){
		arr = args.split(",");
	}
	var _processId = arr[0];
	var _activityId = arr[1];
	var _operationType = arr[2];
	hiddenFlowTypeRadio_replaceNodeEdoc = true;
	flowSecretLevel_replaceNodeEdoc = "${param.secretLevel}";
	if(!selectPeopleFun_replaceNodeEdoc() || superviseEelments == null){
		return;
	}
	
	var personList = superviseEelments[0] || [];
	<%--
	//for(var i=0; i<personList.length; i++){
		//var personId = personList[i].id;
		//var currentUserId = "${currentUserId}";
		//if(personId == currentUserId) return;
    //}
    --%>
    var currentSummaryId = _parent.summaryId;
    var _result = false;
    var _result = null;
    var appName = "";
	if(_parent.appEnumStr){
		appName = _parent.appEnumStr;
	}else{
		appName = _parent.appName;
	}
    if(appName == "collaboration" || appName == "form"){
       	_result = _parent.updateFlash(_processId,_activityId,_operationType,superviseEelments,"replaceNode",null,null,currentSummaryId,null,null,true);
    }else{
        _result = _parent.updateFlash(_processId,_activityId,_operationType,superviseEelments,"replaceNode",null,null,currentSummaryId,null,null,false);
    }
	if(_result != null){
		_parent.document.getElementById("process_xml").value = _result[0];
		var callbackResult = [_result[0], _result[1], _result[2]];
		callFlashAction("edocRefresh_Flash", callbackResult);
	}
}
<%--这个方法是什么调用的，知道的时候注释一下。老找不到调用的地方--%>
function setEdocPolicy(args) {
	args= decodeArgsFromFlash(args);
	var propArr = [];
	if(typeof args == 'object'){
		propArr = args;
	}
	else if(typeof args == 'string'){
		propArr = args.split(",");
	}
   	var result = null;
   	try{
		var newCollForm = _parent.document.getElementsByName("sendForm")[0];
		var workflowDataForm = _parent.document.getElementById("workflowDataForm");
		var listSentForm = _parent.document.getElementById("listForm");
		var advanceRemindTime = -1;
		var deadLineTime = 0;
		if(newCollForm){
			deadLineTime = newCollForm.deadline.options[newCollForm.deadline.selectedIndex].value;
			advanceRemindTime = newCollForm.advanceRemind.options[newCollForm.advanceRemind.selectedIndex].value;
		}else if(workflowDataForm){
			deadLineTime = workflowDataForm.deadline.value;
			advanceRemindTime = workflowDataForm.advanceRemind.value;
		}else if(listSentForm){
			deadLineTime = listSentForm.deadline.value;
			advanceRemindTime = listSentForm.advanceRemind.value;
		}
		var isTemplete = _parent.isTemplete;
		var dealTerm = "";
		var remindTime = "";
		var excuteMode = "";
		var isEditor = "";
		var nodeName = propArr[0];
		var policyName = propArr[1];
		processMode = propArr[4];
		isEditor = propArr[9];
		
		if(propArr[2] != null && propArr[2] != "null"){
			dealTerm = propArr[2];
		}else{
			//dealTerm = deadLineTime;
		}
		if(propArr[3] != null && propArr[3] != "null"){
			remindTime = propArr[3];
		}else{
			//remindTime = advanceRemindTime;
		}
		
		var filterInformPolicy = null;
		if(propArr[18] == "supervise_pending"){
			filterInformPolicy = "inform";
		}
			
		var appName = "";
		if(_parent.bodyType=="FORM"){
			appName = "form";
		}else if(_parent.appName){
			appName = _parent.appName;
		}else{
			appName = _parent.appEnumStr;
		}
			
		var partyId = propArr[14];
		var partyType = propArr[15];
		var matchScope = propArr[16];
		var nodeState = propArr[17]; //当前节点的状态
		var hasBranch = false;
		if(propArr[18] != "" && propArr[18] != null && propArr[18] != "null"){
			hasBranch = propArr[18];
		}
		var nodeId = propArr[10];
		var formId = propArr[6];
		var operationId = propArr[7];
		var desc = propArr[20];
		var dealTermType= propArr[21];
		var dealTermUserId= propArr[22];
		var dealTermUserName= propArr[23];
		result = selectPolicy(partyId, partyType, nodeName,policyName,dealTerm,remindTime,processMode,matchScope,isTemplete,appName,null,formId,operationId,null,isEditor,filterInformPolicy, false, nodeState, false, hasBranch,null,null,_parent.summaryId,nodeId,desc,dealTermType,dealTermUserId,dealTermUserName,secretLevel);
	}
	catch(e){alert(e)}
	if(result == null) return;
	var policyid = result[0];
	var policyName = result[1];
	var deadTerm = result[2];
	var remindTime = result[3];
	var processMode = result[4];
	var matchScope = result[9];
	var formName = result[6];
	var operationName = result[7];
	var desc = result[13];
	 
	var policyStr = new Array();
	policyStr.push(policyid);
	policyStr.push(policyName);
	policyStr.push(deadTerm);
	policyStr.push(remindTime);
	policyStr.push(processMode);
	policyStr.push(matchScope);
	policyStr.push(formName);
	policyStr.push(operationName);
	policyStr.push(desc);
	policyStr.push(result[14]);
	policyStr.push(result[15]);
	policyStr.push(result[16]);
	policyStr.push(result[17]);
	
	var _processId = propArr[11];
	var _activityId = propArr[12];
	var _operationType = propArr[13];
	
	var currentSummaryId = _parent.summaryId;
	var _result = false;
        
	var flowProp = new Array();
	flowProp.push(_processId);
	flowProp.push(_activityId);
	flowProp.push(_operationType);
	var _result = null;
	var appName = "";
	if(_parent.appEnumStr){
		appName = _parent.appEnumStr;
	}else{
		appName = _parent.appName;
	}
   if(appName == "collaboration" || appName == "form"){
      	_result = _parent.updateFlash1(flowProp,policyStr,currentSummaryId,true);
	}else{
       _result = _parent.updateFlash1(flowProp,policyStr,currentSummaryId,false);
	}
	if(_result != null){
		_parent.document.getElementById("process_xml").value = _result[0];
		var callbackResult = [_result[0], _result[1], _result[2]];
		callFlashAction("edocRefresh_Flash", callbackResult);
	}
}
<%-- 新流程设置 --%>
function callNewflowSetting(args){
	args= decodeArgsFromFlash(args);
	var nodeId = args[0];
	var processxml_from_flash= args[1];
	var isAutoSkipBeforeNewSetFlowOfNode= false;
	try {
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "isAutoSkipBeforeNewSetFlowOfNode", false, "POST");
		requestCaller.addParameter(1, "String", processxml_from_flash);
		requestCaller.addParameter(2, "String", nodeId);
		var rs= requestCaller.serviceRequest();
		if(rs=="TRUE" || rs=="true"){//当前节点設置成了自動跳過,則不能再設置新流程,提示用戶
			isAutoSkipBeforeNewSetFlowOfNode= true;
		}else{
			isAutoSkipBeforeNewSetFlowOfNode= false;
		}
	}catch (ex1) {
		alert("Exception : " + ex1);
		return;
	}
	if(isAutoSkipBeforeNewSetFlowOfNode == false ){
		//var ran = parseInt(Math.random()*10000);
		var isUpdate = "${param.isUpdate}";
		var rv = v3x.openWindow({
	        url: templeteURL + "?method=editNewflowSetting&nodeId=" + nodeId+"&random"+parseInt(Math.random()*10000) + "&isUpdate=" + isUpdate,
	        height: 480,
	        width: 560,
	        scrollbars:"no",
			dialogType: "modal",
	        resizable: "no"
	    });
	    if(!rv){
	    	return;
	    }
	    var callbackResult = [rv];
		callFlashAction("updateNewWorkflow", callbackResult);
	}else{
		alert(v3x.getMessage("collaborationLang.dealterm_skip_node_before_set_new_flow"));
		return;
	}
}
<%-- 查看关联流程 --%>
function viewRelateFlow(args){
	if(!args){
		//return;
	}
	var type = _parent.newflowType;
	var affairIdsArray = _parent.relateAffairIds;
	var newflowBaseSummaryId = _parent.summary_id;
	if(type == "main"){
		//从主流程查看子流程,如果只有一个，直接弹出，更多则弹出对话框。
		var relateNodeIds = _parent.relateNodeIds;
		var sum = 0;
		var refAffairId = "";
		var nodeId = args[1];
		if(relateNodeIds){
			for(var i = 0; i<relateNodeIds.length; i++){
				if(relateNodeIds[i] == nodeId){
					refAffairId = affairIdsArray[i];
					sum++;
				}
			}
			switch(sum){
				case 0: return;
				case 1:
						var rv = v3x.openWindow({
					        url: genericURL + "?method=detail&&from=Done&affairId=" + refAffairId + "&isQuote=true&newflowBaseSummaryId=" + newflowBaseSummaryId,
					        workSpace: 'yes'
					    });
					    if (rv == "true") {
					    	try{
					        	getA8Top().reFlesh();
					    	}
					    	catch(e){}
					    }
					    break;
				default:
						if(newflowBaseSummaryId && _parent.newflowTempleteId){
					    	var rv = v3x.openWindow({
						        url: genericURL + "?method=viewRelateColl&flowType="+ type +"&mainSummaryId=" + _parent.summary_id + "&mainTempleteId=" + _parent.newflowTempleteId + "&nodeId=" + nodeId,
						        width: 560,
						        height: 420,
						        scrollbars:"no",
								dialogType: "modal",
						        resizable: "no"
						    });		
						}
			}
		}
	}
	else if(type == "child"){
		if(affairIdsArray){
			v3x.openWindow({
		        url: genericURL + "?method=detail&&from=Done&&affairId=" + affairIdsArray[0] + "&isQuote=true&newflowBaseSummaryId=" + newflowBaseSummaryId,
		        workSpace: 'yes'
		    });
		}
	}
}

<%-- 废弃古老的实现
// Internet Explorer 的挂钩
if (navigator.appName && navigator.appName.indexOf("Microsoft") != -1 && navigator.userAgent.indexOf("Windows") != -1 && navigator.userAgent.indexOf("Windows 3.1") == -1) {
	document.write('<script language=\"VBScript\"\>\n');
	document.write('On Error Resume Next\n');
	document.write('Sub monitor_FSCommand(ByVal command, ByVal args)\n');
	document.write('Call monitor_DoFSCommand(command, args)\n');
	document.write('End Sub\n');
	document.write('</script\>\n');
}
--%>

function init(){
	<%-- 直接出来吧
	var flashContainerObj = document.getElementById("flashContainer");
	if(flashContainerObj){
		flashContainerObj.style.display = "";
	}
	--%>
	var isTempleteEditor = eval("_parent.isTempleteEditor");
	if(isTempleteEditor == true && ${param.isShowButton eq 'true'}){ //来自模板管理	
		document.getElementById("ruleApan").style.display = "";
		if(isNew!="true"){
			var ruleObj = _parent.document.getElementById("workflowRule");
			if(ruleObj){
				document.getElementById("ruleContent").value = ruleObj.value;
			}
		}
	}
	
	var isTempleteEditor = eval("_parent.isFromTemplate"); //来自调用模板
	var hasWorkflowRule = eval("_parent.document.getElementById('workflowRule')");
	if(isTempleteEditor == true && hasWorkflowRule != null && hasWorkflowRule.value != ""){
		document.getElementById("ruleApan").style.display = "";
		var rule = _parent.document.getElementById("workflowRule").value;
		if(rule){
			document.getElementById("ruleContent").value = rule;
			document.getElementById("ruleContent").readOnly = true;
			showRuleAndResetFlash();
		}
	}
	monitorObj = isInternetExplorer ? document.all.monitor : document.monitor;
	jsReady = true;
	<c:if test="${param.isFormBind eq 'true'}"><%-- 来自表单绑定，拷贝新流程信息 --%>
	var parentNFObj = _parent.document.getElementById("NewflowDIV");
	if(parentNFObj){
		document.getElementById("NewflowDIV").innerHTML = parentNFObj.innerHTML;
	}
	</c:if>
	
	if(${param.fromList eq 'list'}){
		resizeFlash();
	}else if(${param.fromList eq 'popup' && param.isHurryDlg ne 'true'} && _parent.colCheckAndupdateLock != false){
		modify();
	}
}

<%--选人时不回显原数据--%>
var showOriginalElement_newColl = false;
var hiddenMultipleRadio_newColl = true;
var hiddenColAssignRadio_newColl = false;
var flowSecretLevel_newColl =1;
/*var hiddenColAssignRadio_newColl = true;
if("${param.showColAssign}" == "false"){
	hiddenColAssignRadio_newColl = false;
}*/

var showAccountShortname_newColl = "yes";
var unallowedSelectEmptyGroup_newColl = true;

var hiddenMultipleRadio_updateEdoc = true;
var showOriginalElement_updateEdoc = false;
var unallowedSelectEmptyGroup_updateEdoc = true;
var flowSecretLevel_updateEdoc =1;

var showOriginalElement_replaceNodeEdoc = false;
var unallowedSelectEmptyGroup_replaceNodeEdoc = true;
var flowSecretLevel_replaceNodeEdoc =1;

var showOriginalElement_newTemplete = false;
var showAccountShortname_newTemplete = "yes";
var isNeedCheckLevelScope_newTemplete = false; //(_parent.templeteFrom != "SYS");
var showAllOuterDepartment_newTemplete = true;
var hiddenMultipleRadio_newTemplete = true;
var flowSecretLevel_newTemplete =1;
var isNeedCheckSecretScope_newTemplete = false;
/*var hiddenColAssignRadio_newTemplete = true;
if("${param.showColAssign}" == "false"){
	hiddenColAssignRadio_newTemplete = false;
}*/
var flowtype_newTemplete = "sequence";

<%-- The Functions for Flash --%>
function getIEVersion(){
	var nAppName = navigator.appName;
	var nAppVersion = navigator.appVersion;
	if(nAppName=="Netscape"){
		nVersionNum  = nAppVersion.substring(0,2);
	}
	else{
		var startPoint = nAppVersion.indexOf("MSIE ")+5;
		nVersionNum = nAppVersion.substring(startPoint,startPoint+3);
	}
	return nVersionNum;
}
//var resizeTimer = null;
window.onresize = function(){
	resizeFlash();
	<%-- 最佳策略，解决onresize执行多次的问题，但是会有闪烁
	if(resizeTimer==null){
		resizeTimer = setTimeout("resizeFlash()", 200);
	}--%>
}
function resizeFlash(width, height){
	var monitorObj = isInternetExplorer ? document.all.monitor : document.monitor;
	if(monitorObj){
		if(!monitorObj.resizeScrollPane){
			setTimeout("resizeFlash("+width+","+height+")",100);
			return;
		}
		if(!width){
			width = document.body.offsetWidth;
			height = document.body.offsetHeight;
		}
		if(width > 0){
			if(${param.isShowButton eq 'true' || param.isFromSupervise eq 'true'}){
				 if(getIEVersion() < 7){
				 	width = width - 6;
				 	height = height - 6;
				 }
			}
			monitorObj.setAttribute("width", width);
			monitorObj.setAttribute("height", height);
			var result = monitorObj.resizeScrollPane(width, height);
			//safari 浏览器 可以返回值。
			<c:if test="${v3x:currentUser().browser == 'iPad' || v3x:currentUser().browser == 'Safari'}">
				if(result != "true"){
					setTimeout("resizeFlash("+width+","+height+")",100)
				}
			</c:if>
		}
		//resizeTimer = null;
	}else{
		getXML();
	}
}

function loadFlash(isDialog){
	if(isDialog == true){
		var obj = document.getElementById("flashContainer");
		var width = obj.clientWidth;
		var height = obj.clientHeight;
		if('${param.fromList}'=='list'){
			resizeFlash();		
		}else{
			resizeFlash(width, height);
		}
	}
	else{
		resizeFlash();		
	}
}

var beforHeight = 600;
function showRuleAndResetFlash(){
	var ruleTRObj = document.getElementById("ruleTR");
	var flashContainerObj = document.getElementById("flashContainer");
	if(ruleTRObj.style.display == "none"){
		if(beforHeight == 600){
			beforHeight = flashContainerObj.offsetHeight;
		}
		var height = flashContainerObj.offsetHeight - 110;
		resizeFlash(flashContainerObj.offsetWidth, height);
	}
	else{
		resizeFlash(flashContainerObj.offsetWidth, beforHeight);
	}
	showRule();
}
//-->
</script>
</head>
<c:set value="${param.isShowButton eq 'true' || param.isFromSupervise eq 'true' || param.isFromSupervise eq ''}" var="isShowButton" />
<c:set value="${param.isSupervise}" var="isSupervise" />
<body onload="init();loadFlash(${isShowButton})" onUnload="finish()" ${isShowButton ? 'scroll="no" style="overflow: hidden"' : '' } onkeydown="listenerKeyESC()">
<input id="fromHasten" name="fromHasten" type="hidden" value="false" />
<c:set value="${col:isCanSendAccountColl() ? ',Account' : ''}" var="canSendAccountColl" />
<%
  String defaultPanels="Department,Team,Post";
  int secretLevel = request.getParameter("secretLevel")==null?1:Integer.parseInt(request.getParameter("secretLevel"));
  String templetePanels="Department,Team,Post,Level,Role,Outworker" + (CollaborationFunction.isCanSendAccountColl() && secretLevel == 1? ",Account" : "");
  String templeteSelectType = "Department,Team,Post,Level,Role,Member" + (CollaborationFunction.isCanSendAccountColl() && secretLevel == 1? ",Account" : "") ;
  
  boolean isEdocApp=false;
  String appName=request.getParameter("appName");
  String formFormBind = request.getParameter("isFormBind");
  String viewPage="selectNode4Workflow";
  if("true".equals(formFormBind)){
	 templetePanels= templetePanels + ",FormField";
	 templeteSelectType="FormField,"+templeteSelectType ;
  }
  //branches_a8_v350_r_gov GOV-2599 王为  增加信息新建流程选择界面Panels,appName=infoSend    Start
  if("sendEdoc".equals(appName) || "signReport".equals(appName) || "recEdoc".equals(appName) || "edocSupervise".equals(appName) || "edocSend".equals(appName) || "edocRec".equals(appName)|| "edocSign".equals(appName) || "sendInfo".equals(appName))
	//branches_a8_v350_r_gov GOV-2599 王为  增加信息新建流程选择界面Panels,appName=infoSend   End
  {
  	viewPage="selectNode4EdocWorkflow";
  	isEdocApp=true;
  	//公文自建流程不允许选择组
  	defaultPanels="Department,Post,Team";
  	//公文都不允许选择外部单位
  	templetePanels="Department,Team,Post,Level,Role";
  }
  else{
    defaultPanels+=",Outworker,RelatePeople";
    out.println("<script>");
    //out.println("hiddenRootAccount_newColl=true;");
    out.println("</script>");
  }
  request.setAttribute("viewPage",viewPage);
%>
<script type="text/javascript">
	<!--
	var isConfirmExcludeSubDepartment_newColl = true;
	//-->
</script>
<v3x:selectPeople id="newColl" panels="<%=defaultPanels%>" selectType="Member,Department,Team,Post${canSendAccountColl}"
	jsFunction="dataToFlash(elements)" viewPage="${viewPage}" />
	
	<script type="text/javascript">
	<!--
	var isConfirmExcludeSubDepartment_updateEdoc = true;
	//-->
</script>
<v3x:selectPeople id="updateEdoc" panels="<%=defaultPanels%>" selectType="Member,Department,Team,Post${canSendAccountColl}"
	jsFunction="dataToEdocFlash(elements)" viewPage="${viewPage}" />
	
	<script type="text/javascript">
	<!--
	var isConfirmExcludeSubDepartment_replaceNodeEdoc = true;
	//-->
</script>
<v3x:selectPeople id="replaceNodeEdoc" panels="<%=defaultPanels%>" selectType="Member,Department,Team,Post${canSendAccountColl}"
	maxSize="1" jsFunction="dataToEdocFlash(elements)" viewPage="${viewPage}" />
	
	<script type="text/javascript">
	<!--
	var isConfirmExcludeSubDepartment_newTemplete = true;
	//-->
</script>
<v3x:selectPeople id="newTemplete" panels="<%=templetePanels%>" selectType="<%=templeteSelectType%>" jsFunction="dataToFlash(elements)" viewPage="${viewPage}"  />

<script type="text/javascript">
var isEdocApp=<%=isEdocApp%>;
if(isEdocApp){//公文选人界面不允许选择其它单位人员
	//hiddenRootAccount_newColl=true;
	//hiddenRootAccount_newTemplete=true;
	//hiddenRootAccount_replaceNodeEdoc=true; 
	//hiddenRootAccount_updateEdoc=true; 
	if(${v3x:currentUser().groupAdmin} && ${param.accountId != null}){
		accountId_replaceNodeEdoc = "${param.accountId}"; 
		accountId_updateEdoc = "${param.accountId}"; 
		var showAccountShortname_updateEdoc = "yes";
	}
}

var hiddenGroupLevel_newColl = true;
var hiddenGroupLevel_newTemplete = true;
var hiddenGroupLevel_replaceNodeEdoc = true;
var hiddenGroupLevel_newTemplete = true;

<c:if test="${param.isFromHasten eq 'edocSupervise' || param.isFromHasten eq 'colSupervise'}" >
	document.getElementById("fromHasten").value = "true";
</c:if>
</script>
<div id="information" style="display:none;background-color:#FF0000" height="200px" width="100%"></div>
<c:choose>
	<c:when test="${isShowButton}">
	<table width="100%" height="100%" border="0" id="moniterTable" class="${param.fromList=='list'?'':'popupTitleRight'}" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="3" height="${param.fromList=='list'?'1':41}" class="${param.fromList=='list'?'':'PopupTitle'}" style="font-size: 12px;font-weight: normal;">
			<script type="text/javascript">
				var operationHelpText = _('V3XLang.common_workflow_editHelp');
				if(_parent.showMode == 1){
					document.write(operationHelpText);
					document.close();
				}
			</script>
			</td>
		</tr>
		<tr>
			<td colspan="3" height="100%" valign="top">
				<div id="flashContainer" style="width:100%; height:100%">
				<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" 
				<%-- 
				codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" 
				--%>
				width="860" height="560" id="monitor" align="middle">
			    <param name="allowScriptAccess" value="always" />
			    <param name="movie" value="<c:url value='/common/workflow/monitor.swf${v3x:resSuffix()}' />" />
			    <param name="menu" value="false" />
			    <param name="quality" value="low" />
			    <param name="bgcolor" value="#ffffff" />
			    <param name="wmode" value="transparent" />
			    <param name="allowScriptAccess" value="always" />
	            <!-- 非IE浏览器:开始 -->
	            <script type="text/javascript">
	                if(!document.all){
	                  document.write("<embed src=\"<c:url value='/common/workflow/monitor.swf${v3x:resSuffix()}' />\" quality=\"low\" bgcolor=\"#ffffff\" width=\"860\" height=\"600\" name=\"monitor\" wmode=\"transparent\" align=\"middle\" allowScriptAccess=\"always\" type=\"application/x-shockwave-flash\" />");
	                }
	            </script>
	            <!-- 非IE浏览器:结束 -->
			  	</object>
				</div>
			</td>
		</tr>
		<tr id="ruleTR" style="display: none;">
			<td colspan="3" height="64" class="bg-advance-middel">
				<div class="div-float-clear">
					<div class="div-float"><fmt:message key="common.instructions.for.use.label" /></div>
					<div class="div-float-right cursor-hand" onclick="showRuleAndResetFlash()"><img src="<c:url value='/common/images/attachmentICON/delete.gif' />"></div>
				</div>
				<div><textarea rows="5" cols="" id="ruleContent" inputName="<fmt:message key='common.instructions.for.use.label'/>" class="input-100per"></textarea></div>
			</td>
		</tr>
		<tr height="42" class="bg-advance-bottom">
			<td width="20%" align="right" height="42">&nbsp;<span class="like-a" style="display: none;" onclick="showRuleAndResetFlash()" id='ruleApan'><fmt:message key="common.instructions.for.use.label" /></span></td>
			<td height="35" align="center">
			<c:set value="${param.isFromHasten eq 'edocSupervise' || param.isFromHasten eq 'colSupervise' || param.endFlag eq 'true' }" var="isFromHasten" />
			<c:set value="${param.isFromSupervise eq 'true' || param.endFlag eq 'true'}" var="isFromSupervise" />
			<c:set value="${param.isFromWorkFlowManager eq 'true' }" var="isFromWorkFlowManager" />
			<c:set value="${param.from eq 'Sent'}" var="fromSentFlag" />
			<c:set value="${param.isFinishedFlag eq 'true' }" var="isFinishedFlag" />
			<%--某些浏览器不让编辑 --%>
			<c:if test="${v3x:getBrowserFlagByRequest('WorkFlowEdit', pageContext.request)}">
				<c:choose>
					<c:when test="${!isFromSupervise}">
						<c:choose>
							<c:when test="${isFromWorkFlowManager}">
								<input id="modifyButton" type="button" onclick="modify()" value="<fmt:message key='common.button.modify.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
								<input id="submitButton" type="button" onclick="ok('true', '${currentUserId}')" value="<fmt:message key='common.button.ok.label' bundle='${v3xCommonI18N}' />" style="display:none" class="button-style">
								<input id="repealButton" type="button" onclick="repealWorkflow('${param.appEnumStr }', '${param.newflowType}')" value="<fmt:message key='common.repeal.workflow.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
								<input id="stopButton" type="button" onclick="stopWorkflow('${param.appEnumStr }', '${param.newflowType}')" value="<fmt:message key='common.stop.workflow.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
								<input id="closeButton" type="button" onclick="window.close()" value="<fmt:message key='common.button.close.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
							</c:when>
							<c:when test="${param.isOnlyView eq 'true'}">
								<input type="button" onclick="window.close()" value="<fmt:message key='common.button.close.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
							</c:when>
							<c:otherwise>
								<input id="confirmButton" name="confirmButton" type="button" onclick="ok('${fromSentFlag}', '${currentUserId}')"
								 value="<fmt:message key='common.button.ok.label' bundle='${v3xCommonI18N}' />" class="button-style">
								<input id= cancelButton" id="cancelButton" type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
					<c:choose>
						<c:when test="${isFromHasten}">
							<input type="button" onclick="window.close()" value="<fmt:message key='common.button.close.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
						</c:when>
						<c:otherwise>
							<c:if test="${isSupervise }">
								<input id="modifyButton" type="button" onclick="modify()" value="<fmt:message key='common.button.modify.label' bundle='${v3xCommonI18N}' />" class="button-style">
								<input id="submitButton" type="button" onclick="ok('true', '${currentUserId}')" value="<fmt:message key='common.button.ok.label' bundle='${v3xCommonI18N}' />" style="display:none" class="button-style">								
							</c:if>
							<input type="button" onclick="finish()" value="<fmt:message key='common.button.close.label' bundle='${v3xCommonI18N}' />" class="button-style button-space">
						</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:if>
			</td>
			<td width="20%">&nbsp;</td>
		</tr>
	</table>
	</c:when>
	<c:otherwise>
		<div id="flashContainer" style="width:100%; height:100%">
		<c:if test="${v3x:currentUser().browser != 'iPad'}">
		<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" 
		<%--
		codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" 
		--%>
		width="860" height="600" id="monitor" align="middle">
	    <param name="allowScriptAccess" value="always" />
	    <param name="movie" value="<c:url value='/common/workflow/monitor.swf${v3x:resSuffix()}' />" />
	    <param name="menu" value="false" />
	    <param name="quality" value="low" />
	    <param name="bgcolor" value="#ffffff" />
	    <param name="wmode" value="transparent" />
	    <param name="allowScriptAccess" value="always" />
        <!-- 非IE浏览器:开始 -->
        <script type="text/javascript">
            if(!document.all){
              document.write("<embed src=\"<c:url value='/common/workflow/monitor.swf${v3x:resSuffix()}' />\" quality=\"low\" bgcolor=\"#ffffff\" width=\"860\" height=\"600\" name=\"monitor\" wmode=\"transparent\" align=\"middle\" allowScriptAccess=\"always\" type=\"application/x-shockwave-flash\" />");
            }
        </script>
        <!-- 非IE浏览器:结束 -->
	  	</object>
	  	</c:if>
	  	<c:if test="${v3x:currentUser().browser == 'iPad'}">
	  		<div style="width:100%;height:100%;overflow: auto;" id="continer">
			</div>
	  	</c:if>
		</div>
		
	</c:otherwise>
</c:choose>
<form id="monitorForm" name="monitorForm" action="" method="post" style='margin: 0px' onsubmit="return false">
	<input type="hidden" id="process_xml" name="process_xml" value=""/>
	<input type="hidden" id="process_desc_by" name="process_desc_by" value="xml" />
	<input type="hidden" id="currentNodeId" name="currentNodeId" value="" />
	<div style="display:none" id="processModeSelectorContainer">
    	<%@include file="processModeSelector.jsp" %>
	</div>
	<div id="NewflowDIV" style="display: none"></div>
</form>
<iframe name="colHasten" id="colHasten" width="0" height="0" marginheight="0" marginwidth="0"></iframe>
<script type="text/javascript">
<!--
if(_parent.colCheckAndupdateLock == false){
	var modifyButtonObj = document.getElementById("modifyButton");
	if(modifyButtonObj){
		modifyButtonObj.disabled = true; 
	}
	var submitButtonObj = document.getElementById("submitButton");
	if(submitButtonObj)
		submitButtonObj.disabled = true;
}
if(_parent.colCheckAndupdateLock == false || ${param.newflowType eq 1 || param.newflowType eq 2}){
	var repealButtonObj = document.getElementById("repealButton");
	if(repealButtonObj){
		repealButtonObj.disabled = true; 
	}
    var stopButtonObj = document.getElementById("stopButton");
    if(stopButtonObj){
    	stopButtonObj.disabled = true; 
    }
}
if(parseInt(window.screen.height)<768){
	try{
		document.getElementById('monitor').height='450px';
	}catch(e){}
}
if(v3x.isMSIE10){
	var _h = parseInt(document.body.clientHeight);
	if(document.getElementById('moniterTable'))document.getElementById('moniterTable').style.height=(_h)+'px';
}
//-->
</script>
</body>
</html>