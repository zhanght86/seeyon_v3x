<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<%@page import="com.seeyon.v3x.common.constants.Constants" %>	
<%@ include file="Collaborationheader.jsp" %>
<html>
<head>
<%@ include file="../common/INC/noCache.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="newColl.label"/></title>
<%@ include file="../doc/pigeonholeHeader.jsp" %>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/templete.js${v3x:resSuffix()}" />"></script>

<link href="<c:url value="/apps_res/form/css/SeeyonForm.css${v3x:resSuffix()}"/>" rel="stylesheet" type="text/css"/>

<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>

<c:set value="${v3x:currentUser()}" var="currentUser" />
<c:set value="${'resend' eq param.from ? '' : summary.id}" var="ColSummaryId" />
<c:set value="${templete.type != 'text'}" var="nonTextTemplete" />
<c:set value="${templete!=null && templete.isSystem}" var="isSystemTemplete" />
<c:set value="${isFromSystemTemplete &&  templete ne null && templete.projectId ne null  && templete.projectId ne ''}" var="disabledProjectId" />
<%--表单无论类型是系统模板还是个人模板，都不能修改流程 ;--%>
<c:set value="${templete != null && templete.type != 'text' && isSystemTemplete || isForm eq true || isParentColTemplete || isParentWrokFlowTemplete}" var="isOnlyViewFlow" />
<script type="text/javascript"><!--

var currentPage="newColl";
var isNewColl = true ;
var mObject = false;
var hasDiagram = <c:out value="${hasWorkflow}" default="false" />;        
var caseProcessXML = '${process_xml}';
var caseLogXML = "";
var caseWorkItemLogXML = "";
var showMode = 1;
<c:if test="${isOnlyViewFlow}">
	showMode = 0;
</c:if>
var currentNodeId = null;
var showHastenButton = "";
var isTemplete = false;
var appName = "collaboration";
var isFromTemplate = <c:out value="${isFromTemplate}" default="false" />
var editWorkFlowFlag = "true";
var policys = null;
var nodes = null;
var summaryId = "${ColSummaryId}";
var templateFlag = <c:out value="${isSystemTemplete}" default="false" />;
var currentUser = {
	id : "${currentUser.id}",
	name : "${v3x:escapeJavascript(currentUser.name)}"
}

var isForm = "${isForm}";
var noDepManager = "${noDepManager}";
if(noDepManager == "true"){
	alert(_("collaborationLang.col_supervise_nodepartmentManager"));
}
hasWorkflow = hasDiagram;
<c:if test="${from!='a8genius'}">
	if('${param.flag}'=='formBizConfig') {
		if('${param.type}'=='menu' || '${param.type}'=='')
			getA8Top().showLocation("${menuId}", "<fmt:message key='menu.collaboration.new' bundle='${v3xMainI18N}' />");
		else 
			getA8Top().showLocation(null,"<v3x:out value='${bizConfig.name}' escapeJavaScript='true' />", "<v3x:out value='${templete.subject}' escapeJavaScript='true' />", "<fmt:message key='menu.collaboration.new' bundle='${v3xMainI18N}' />");
	
		 if(getA8Top().contentFrame.document.getElementById("LeftRightFrameSet").cols == "0,*"){
			getA8Top().contentFrame.document.getElementById("LeftRightFrameSet").cols = "142,*";
		 }
	} else {
		getA8Top().showLocation(101);
	}
</c:if>
 
var selectedElements = null;

var showOriginalElement_wf = false;
var showAccountShortname_wf = "yes";
var unallowedSelectEmptyGroup_wf = true;
var hiddenColAssignRadio_wf = true;
var hiddenRootAccount_wf = false;
//不让编辑流程的浏览器不让显示多层
if(!v3x.getBrowserFlag('newFlash')){
	var hiddenMultipleRadio_wf = true;
}
<c:if test="${alertMsg!=null}">
	alert("<fmt:message key='${alertMsg}'/>");
</c:if>

//分支
	var branchs = new Array();
	var keys = new Array();
	var team = new Array();
	var secondpost = new Array();
	<c:if test="${branchs != null}">
		var handworkCondition = _('collaborationLang.handworkCondition');
		<c:forEach items="${branchs}" var="branch" varStatus="status">
			var branch = new ColBranch();
			branch.id = ${branch.id};
			branch.conditionType = "${branch.conditionType}";
			branch.formCondition = "${v3x:escapeJavascript(branch.formCondition)}";
			branch.conditionTitle = "${v3x:escapeJavascript(branch.conditionTitle)}";
			branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
			branch.isForce = "${branch.isForce}";
			eval("branchs["+${branch.linkId}+"]=branch");
			keys[${status.index}] = ${branch.linkId};
		</c:forEach>
	</c:if>
	<c:if test="${teams != null}">
		<c:forEach items="${teams}" var="team">
			team["${team.id}"] = ${team.id};
		</c:forEach>
	</c:if>
	<c:if test="${secondPosts != null}">
		<c:forEach items="${secondPosts}" var="secondPost">
			secondpost["${secondPost.depId}_${secondPost.postId}"] = "${secondPost.depId}_${secondPost.postId}";
		</c:forEach>
	</c:if>	
	
	function setTrackRadiio(){
		var obj = document.getElementById("isTrack");
		if(obj!=null){
			var all = document.getElementById("trackRange_all");
			var part = document.getElementById("trackRange_part");
			if(obj.checked){

				all.disabled = false;
				part.disabled = false;
				
				 all.checked = true;
				 
			}else {

				all.disabled = true;
				part.disabled = true;
				
				all.checked = false;
				part.checked = false;
			}
		}
	}

	function setTrackCheckboxChecked(){
		var obj = document.getElementById("isTrack");
		if(obj!=null){
			obj.checked = true;
		}
	}

	function selectPeopleFunTrackNewCol(){
		setTrackCheckboxChecked();
		flowSecretLevel_track = document.getElementById("secretLevel").value;
		selectPeopleFun_track();
		activeOcx();
	}
	function setPeople(elements){
		var memeberIds = "";
		if(elements){
			var obj2 = getIdsString(elements,false);
			document.getElementById("trackMembers").value = obj2;
		}
	}
	var oldElementssv = [];
	function sv(elements){
		if(elements){
			var obj1 = getNamesString(elements);
			var obj2 = getIdsString(elements,false);
			
			var sVisorsFromTemplate = document.getElementById("sVisorsFromTemplate");
			var unCancelledVisor = document.getElementById("unCancelledVisor");
			if(sVisorsFromTemplate!=null && sVisorsFromTemplate.value=="true"){
				var uArray = unCancelledVisor.value.split(",");
				for(var i=0;i<uArray.length;i++){
					var have = obj2.search(uArray[i]);
					if(have == -1){
						alert("模板自带督办人员不允许删除");
						elements_sv = oldElementssv; 
						return;
					}
				}
			}
			document.getElementById("supervisors").value = obj1;
			document.getElementById("supervisorId").value = obj2;
		}
	}
	function selectDateTime(request,obj,width,height){
		var now = new Date();//当前系统时间
		whenstart(request,obj, width, height,'datetime');
		activeOcx();
		if(obj.value != ""){
			var days = obj.value.substring(0,obj.value.indexOf(" "));
			var hours = obj.value.substring(obj.value.indexOf(" "));
			var temp = days.split("-");
			var temp2 = hours.split(":");
			var d1 = new Date(parseInt(temp[0],10),parseInt(temp[1],10)-1,parseInt(temp[2],10),parseInt(temp2[0],10),parseInt(temp2[1],10));
			if(d1.getTime()<now.getTime()){
				if(!window.confirm(v3x.getMessage("collaborationLang.col_alertTimeIsOverDue"))){
					obj.value = "";
					activeOcx();
					return false;
					
				}
			}
		}
		activeOcx();
	}
	function showMoreInputs(flag){
		var moreInputsTr1 = document.getElementById("moreInputsTr1");
		var more_Div = document.getElementById("more_Div");
		
		if(moreInputsTr1.style.display == "none"){
			moreInputsTr1.style.display = "";
			more_Div.className = "more_up_new"
		}else{
			moreInputsTr1.style.display = "none";
			more_Div.className = "more_down_new"
		}
	}
	function init(){
	   	 if(!isFromTemplate){//不是模板流程
			 var deadline= document.getElementById("deadline").value;
			 var allow_auto_stop_flow = document.getElementById("allow_auto_stop_flow");
	         if(deadline<=0){
	        	 if(allow_auto_stop_flow){
	        		 allow_auto_stop_flow.checked = false;
	        		 allow_auto_stop_flow.disabled = true;
	        	 }
	         }else{
	        	 if(allow_auto_stop_flow){
	        			allow_auto_stop_flow.disabled = false;
	        	 }
	         }
		 }

		}
	
	function clickSupervisor() {
		oldElementssv = elements_sv;
		flowSecretLevel_sv = document.getElementById("secretLevel").value;
		selectPeopleFun_sv();
		activeOcx();
	}
	
	function setEnabledAndNoChecked(id){
		var obj = document.getElementById(id);
		obj.setAttribute("disabled",false);
		obj.setAttribute("checked",true);
	}

	function setDisabledAndNoChecked(id){
		var obj = document.getElementById(id);
		obj.setAttribute("disabled",true);
	}
	
	function onLoad(){
	   	var deadline= document.getElementById("deadline").value;
	   	if(deadline==0){
			var allow_auto_stop_flow=document.getElementById('allow_auto_stop_flow');
			allow_auto_stop_flow.disabled=true;
		}
	   	
	   	//if ("${templete.type}"=="workflow") {
	   		//setDisabledAndNoChecked("allow_chanage_flow");
	   		//setEnabledAndNoChecked("allow_transmit");
	   		//setEnabledAndNoChecked("allow_edit");
	   		//setEnabledAndNoChecked("allow_edit_attachment");
	   		//setEnabledAndNoChecked("allow_pipeonhole");
	   	//} 
	    if ("${templete.type}"=="text") {
	   		setEnabledAndNoChecked("allow_chanage_flow");
	   		//调用格式模板默认勾选‘改变流程’
	   		setDisabledAndNoChecked("allow_transmit");
	   		setDisabledAndNoChecked("allow_edit");
	   		setDisabledAndNoChecked("allow_edit_attachment");
	   		setDisabledAndNoChecked("allow_pipeonhole")
	   	}
	   	
	}
	//从待发获取流程密级
	//flowSecretLevel_wf = "${!empty secret ? secret : 1}";
	// 2017-01-11 诚佰公司
	flowSecretLevel_wf = "${!empty secret ? secret : ''}";
//
--></script>
</head>
<c:if test="${param.from eq 'resend'}">
	<c:set var="resentTime" value="${summary.resentTime + 1}" />
</c:if>
<c:set value="${body.bodyType=='FORM'?runtimeView:body.content}" var="thisContent" />
<body scroll="no" class="coll-body-padding" onload='onLoad()' onunload="getA8Top().endProc()" onclick="init()">
<form name="sendForm" id="sendForm" method="post">
<input type="hidden" id="saveAsTempleteSubject" name="saveAsTempleteSubject" value="">
<input type="hidden" id="formData" name="formData" value="">
<input type="hidden" id="formSubject_value" name="formSubject_value" value="">
<input type="hidden" id="masterId" name="masterId" value="${masterId }" />
<input type="hidden" name="draft" value="false" id="draft">
<input type="hidden" id="resentTime" name="resentTime" value="${resentTime}">
<input type="hidden" id="id" name="id" value="${ColSummaryId}">
<input type="hidden" id="formappid" name="formappid" value="${formappid}">
<input type="hidden" id="formid" name="formid" value="${formid}">
<input type="hidden" id="operationid" name="operationid" value="${operationid}">
<input type="hidden" id="quoteFromsign" name="quoteFromsign" value="${quoteFromsign}">
<input type="hidden" id="quoteformtemId" name="quoteformtemId" value="${quoteformtemId}">
<input type="hidden" id="parentformSummaryId" name="parentformSummaryId" value="${parentformSummaryId}">
<input type="hidden" name="process_desc_by" id="process_desc_by" value="${process_desc_by}" />
<input type="hidden" name="process_xml" id="process_xml" value="" />
<input type="hidden" id="archiveId" name="archiveId" value="${summary.archiveId}" />
<input type="hidden" id="archiverFormid" name="archiverFormid" value="${summary.archiverFormid}" />
<input type="hidden" id="prevArchiveId" name="prevArchiveId" value="${summary.archiveId}" />
<c:set value="${isSystemTemplete ? (not empty temformParentId) ? temformParentId : templete.id : ''}" var="tempId" />
<input type="hidden" id="templeteId" name="templeteId" value="${tempId}" />
<input type="hidden" id="temformParentId" name="temformParentId" value="${temformParentId}" />
<input type="hidden" id="tembodyType" name="tembodyType" value="${templete.bodyType}" />
<input type="hidden" id="formtitle" name="formtitle" value="<c:out value='${formtitle}' escapeXml='true' />" />
<input type="hidden" name="workflowRule" id="workflowRule" value="<c:out value='${workflowRule}' escapeXml='true' />" />
<input type="hidden" id="currentNodeId" name="currentNodeId" value="start" />
<input type="hidden" name="supervisorId" id="supervisorId" value="${colSupervisors }">
<input type="hidden" name="unCancelledVisor" id="unCancelledVisor" value="${unCancelledVisor }">
<input type="hidden" name="sVisorsFromTemplate" id="sVisorsFromTemplate" value="${sVisorsFromTemplate}">
<input type="hidden" name="pagefrom" id="pagefrom" value="${param.from}">
<input type="hidden" name="loginAccountId" id="loginAccountId" value="${currentUser.loginAccount}" >
<input type="hidden" name="userName" id="userName" value="${v3x:toHTML(currentUser.name)}" >
<input type="hidden" name="isSystemTemplete" id="isSystemTemplete" value="${isSystemTemplete}" >
<input type="hidden" name="from" id="from" value="${from==null?param.from:from}" >
<input type="hidden" name="referenceId" id="referenceId" value="${referenceId}" >
<input type="hidden" id="__ActionToken" name="__ActionToken" readonly value="SEEYON_A8" > <%-- post提交的标示，先写死，后续动态 --%>
<!-- 接收从弹出页面提交过来的数据 -->
<input type="hidden" name="popJsonId" id="popJsonId" value="">
<input type="hidden" name="popNodeSelected" id="popNodeSelected" value="">
<input type="hidden" name="popNodeCondition" id="popNodeCondition" value="">
<input type="hidden" name="popNodeNewFlow" id="popNodeNewFlow" value="">
<input type="hidden" name="allNodes" id="allNodes" value="">
<input type="hidden" name="nodeCount" id="nodeCount" value="">

<span id="people" style="display:none;"></span>
<c:if test="${col:isCanSendAccountColl()}">
	<c:set value="Account," var="accountStr"/>
</c:if>
<script type="text/javascript">
  isNeedCheckLevelScope_wf = true ; 
</script>
<script type="text/javascript">
	<!--
	var isConfirmExcludeSubDepartment_wf = true;
	//-->
</script>
<v3x:selectPeople id="wf" panels="Department,Team,Post,Outworker,RelatePeople" selectType="${accountStr}Department,Team,Post,Member" jsFunction="setPeopleFields(elements)" viewPage="selectNode4Workflow" />

<c:set value="${v3x:showOrgEntitiesOfIds(colSupervisors, 'Member', pageContext)}" var="supervisorIdStr" />
<c:set value="${v3x:parseElementsOfIds(colSupervisors, 'Member')}" var="supervisorIdEle" /> 
<v3x:selectPeople id="sv" panels="Department,Team" selectType="Member"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="sv(elements)"
                  originalElements="${supervisorIdEle}" minSize="0" maxSize="10" 
                  />

<div style="display:none;" id="showAtt">
         <v3x:attachmentDefine attachments="${attachments}" />
</div>

<div style="display:none;" id="saveColFieldSummary">
</div>
<c:if test="${isForm==true}">
<table width="100%" height="100%"  border="0" cellpadding="0" cellspacing="0" class="page-list-border">
	<tr class="bg-summary" height="65">
	<td colspan="9" nowrap="nowrap" width="100%">
</c:if>
	<table width="100%" height="65"  border="0" cellpadding="0" cellspacing="0" class="page-list-border">
		<tr class="bg-summary" height="65">
			<td colspan="9" nowrap="nowrap" width="100%">
  <table width="100%" height="100%"  border="0" cellpadding="0" cellspacing="0">
  <colgroup>
     <col style="width:1%"></col>
     <col style="width:4%"></col>
     <col style="width:4%"></col>
     <col style="width:35%"></col>
     <col style="width:8%"></col>
     <col style="width:9%"></col>
     <col style="width:9%"></col>
     <col style="width:9%"></col>
     <col style="width:8%"></col>
     <col style="width:10%"></col>
     <col style="width:4%"></col>
  </colgroup>
  <tr>
    <td colspan="11" height="30">
    	<fmt:message key="common.${isOnlyViewFlow? 'view':'design'}.workflow.label" bundle="${v3xCommonI18N}" var="workflowLable" />
		<script type="text/javascript">
		var myBar = new WebFXMenuBar("${pageContext.request.contextPath}");

		myBar.add(new WebFXMenuButton("save", "<fmt:message key='common.toolbar.savesend.label' bundle='${v3xCommonI18N}'/>", "save()", [1,5], "",null));		
        myBar.add(new WebFXMenuButton("saveDraft", "<fmt:message key='common.toolbar.saveDraftOpinion.label' bundle='${v3xCommonI18N}'/>", "saveDraft()", [3,4], "",null));
		if(v3x.getBrowserFlag('hideMenu')==true && v3x.getBrowserFlag('newFlash')==true){
			myBar.add(new WebFXMenuButton("saveAs", "<fmt:message key='common.toolbar.saveAs.label.rep' bundle='${v3xCommonI18N}'/>", "saveAsTemplete()", [3,5], ""));    	
			//myBar.add(new WebFXMenuButton("workflow", "${workflowLable}", "designWorkFlow('','${isOnlyViewFlow}')", [3,6], "", null));
		}
		myBar.add(new WebFXMenuButton("templete", "<fmt:message key='common.toolbar.templete.label' bundle='${v3xCommonI18N}' />", "openTemplete()", [3,7], "", null));
		
		var insert = new WebFXMenu;
		if(v3x.getBrowserFlag('hideMenu')==true ){
			insert.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.insert.localfile.label' bundle='${v3xCommonI18N}' />", "insertAttachmentAndActiveOcx()"));
		}
		insert.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.insert.mydocument.label' bundle='${v3xCommonI18N}' />", "quoteDocument()"));
		if("${quoteformtemId}" != "" && "${temformParentId}" ==""){
		  insert.add(new WebFXMenuItem("", "<fmt:message key='toolbar.insert.form.label' />", "quoteDocumentForm()"));
		}
		myBar.add(new WebFXMenuButton("insert", "<fmt:message key='common.toolbar.insert.label' bundle='${v3xCommonI18N}' />", null, [1,6], "", insert));

		<%--1.系统流程模板 可以选择正文类型--%>    
		<%--2.非来自模板的个人模板 可以选择正文类型--%>      
		<%--3.自由协同可以选择--%>
		
		<c:if test="${ templete!=null &&((templete.isSystem && templete.type == 'workflow') || (!templete.isSystem && !isParentTextTemplete && !isParentColTemplete)) ||  isFromTemplate != true}">		if(v3x.getBrowserFlag('hideMenu')==true){
			myBar.add(${v3x:bodyTypeSelector("v3x")});
		}
		</c:if>
		if(v3x.getBrowserFlag('hideMenu')==true){
			//myBar.add(new WebFXMenuButton("superviseSetup", "<fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' />", "openSuperviseWindow()", [0,1], "", null));
			myBar.add(new WebFXMenuButton("newPrint", "<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />", "try{newDoPrint()}catch(e){}", [1,8], "", null));
		}
		<v3x:showThirdMenus rootBarName="myBar" addinMenus="${AddinMenus}"/>
		document.write(myBar);
		document.close();
		</script>
	</td>
  </tr>
  <tr class="bg-summary lest-shadow" height="29">
      <td class="bg-gray" rowspan="2" nowrap="nowrap"></td> 
      <td class="bg-gray send_new" rowspan="2" nowrap="nowrap">
      <fmt:message key="common.${isOnlyViewFlow? 'view':'design'}.workflow.label" bundle="${v3xCommonI18N}" var="workflowLable" />
	  <div id="sendButton" class="newbtn" onclick="send()" onmouseover="javascript:this.className='newbtn-over';" onmouseout="javascript:this.className='newbtn';"><fmt:message key='common.toolbar.send.label' bundle='${v3xCommonI18N}' /></div>
   </td>
    <td class="bg-gray"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />:</td>
    <td><fmt:message key="common.default.subject.value" var="defSubject" bundle="${v3xCommonI18N}" />
		<c:choose>
		<c:when test="${collSubjectNotEdit =='true'}">
		 <input name="subject" type="text" id="subject" class="input-100per" maxlength="85" readonly="readonly"
        	deaultValue="${defSubject}" validate="isDeaultValue,notNull,notSpecChar"
            inputName="<fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />"
            value="<c:out value="${collSubject}" escapeXml="true" default='${defSubject}' />"
            ${v3x:outConditionExpression(readOnly, 'readonly', '')}
          >		
		</c:when>
		<c:otherwise>
		<input name="subject" type="text" id="subject" class="input-100per" maxlength="85"
        	deaultValue="${defSubject}" validate="isDeaultValue,notNull,notSpecChar"
            inputName="<fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" />"
            value="<c:out value="${fromTemplate !=null ? formtitle : summary.subject}" escapeXml="true" default='${defSubject}' />${col:getSubjectOfTemplate(param.templeteId)}${resendLabel}"
            ${v3x:outConditionExpression(readOnly, 'readonly', '')}
            onfocus='checkDefSubject(this, true)' onblur="checkDefSubject(this, false)">		
		</c:otherwise>
		</c:choose>
      </td>
    <td class="bg-gray" style="padding:0 0 0 6px;">
    <select name="importantLevel" class="input-100per">
    		<v3x:metadataItem metadata="${comImportanceMetadata}" showType="option" name="importantLevel" selected="${summary.importantLevel}" />
    	</select>
    </td>
    
    <%--流程密级--%>
    <%--
    <td nowrap="nowrap" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>
    <c:if test="${ templete!=null && templete.isSystem}">	
    <td>
		<select class="input-100per">
			<c:if test="${secret != null}">
    		<option value="${secret}" disabled="disabled">${flowSecretLevel}</option>
    		</c:if>
    		<c:if test="${secret == null}">
    		<option value="1" disabled="disabled"><fmt:message key="collaboration.secret.nosecret"/></option>
    		</c:if>
   		</select>
   		<input name="secretLevel" id="secretLevel" type="hidden" value="${secret}"/>
	</td>
	</c:if>--%>
	
	<!-- 2017-4-21 诚佰公司 添加流程密级隐藏字段和附件密级字段 -->
    <c:if test="${ templete!=null && templete.isSystem}">
    <td nowrap="nowrap" class="bg-gray"><font color="red">密级:</font></td>	
    <td>
    	<!-- 流程密级 -->
    	<input name="secretLevel" id="secretLevel" type="hidden" value="${secret}"/>
		<!-- 附件密级 -->    	
		<select name="attachLevel" id="attachLevel" class="input-100per">
	    	<!-- 2017-01-11 诚佰公司 -->
	    	<option value=""></option>
	    	<option value="1" ${attachLevel == 1? 'selected=selected':''}><fmt:message key="collaboration.secret.nosecret"/></option>
	    	<c:if test="${peopleSecretLevel >= 2 }">
	    		<option value="2" ${attachLevel == 2? 'selected=selected':''}><fmt:message key="collaboration.secret.secret"/></option>
	    	</c:if>
	    	<c:if test="${peopleSecretLevel >= 3 }">
	    		<option value="3" ${attachLevel == 3? 'selected=selected':''}><fmt:message key="collaboration.secret.secretmore"/></option>
	    	</c:if>
	    </select>
	</td>
	</c:if>
	<c:if test="${ templete!=null && templete.isSystem == false && isFromTemplate}">
		<td nowrap="nowrap" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>	
	    <td>
	    <select name="secretLevel" id="secretLevel" class="input-100per" onchange="changeSecretLevel(this);">
	    	<!-- 2017-01-11 诚佰公司 -->
	    	<option value=""></option>
	    	<option value="1" ${secret == 1? 'selected=selected':''}><fmt:message key="collaboration.secret.nosecret"/></option>
	    	<c:if test="${peopleSecretLevel >= 2 }">
	    	<option value="2" ${secret == 2? 'selected=selected':''}><fmt:message key="collaboration.secret.secret"/></option>
	    	</c:if>
	    	<c:if test="${peopleSecretLevel >= 3 }">
	    	<option value="3" ${secret == 3? 'selected=selected':''}><fmt:message key="collaboration.secret.secretmore"/></option>
	    	</c:if>
	    </select>
	    <!-- 附件密级 -->
	    <input name="attachLevel" id="attachLevel" type="hidden" value="${attachLevel}"/>
		</td>
	</c:if>
	<c:if test="${templete ==null && (secretFlag == 'wait'||secretFlag == 'resend')}">
		<td nowrap="nowrap" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>		
	    <td>
	    <select name="secretLevel" id="secretLevel" class="input-100per" onchange="changeSecretLevel(this);">
	    	<!-- 2017-01-11 诚佰公司 -->
	    	<option value=""></option>
	    	<option value="1" ${secret == 1? 'selected=selected':''}><fmt:message key="collaboration.secret.nosecret"/></option>
	    	<c:if test="${peopleSecretLevel >= 2 }">
	    	<option value="2" ${secret == 2? 'selected=selected':''}><fmt:message key="collaboration.secret.secret"/></option>
	    	</c:if>
	    	<c:if test="${peopleSecretLevel >= 3 }">
	    	<option value="3" ${secret == 3? 'selected=selected':''}><fmt:message key="collaboration.secret.secretmore"/></option>
	    	</c:if>
	    </select>
	    <!-- 附件密级 -->
	    <input name="attachLevel" id="attachLevel" type="hidden" value="${attachLevel}"/>
		</td>
	</c:if>
	<c:if test="${ (templete == null || isFromTemplate == false) && secretFlag != 'wait' && secretFlag != 'resend'}">
		<td nowrap="nowrap" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret"/>:</font></td>	
	    <td>
	    <select name="secretLevel" id="secretLevel" class="input-100per" onchange="changeSecretLevel(this);">
	    	<!-- 2017-01-11 诚佰公司 -->
	    	<option value=""></option>
	    	<option value="1"><fmt:message key="collaboration.secret.nosecret"/></option>
	    	<c:if test="${peopleSecretLevel >= 2 }">
	    	<option value="2"><fmt:message key="collaboration.secret.secret"/></option>
	    	</c:if>
	    	<c:if test="${peopleSecretLevel >= 3 }">
	    	<option value="3"><fmt:message key="collaboration.secret.secretmore"/></option>
	    	</c:if>
	    </select>
	    <!-- 附件密级 -->
	    <input name="attachLevel" id="attachLevel" type="hidden" value="${attachLevel}"/>
	    </td>
    </c:if>
    <%--关联项目--%>
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="project.label" />:</td> 
    <td>
    	<select name="projectIdSelect" onchange="document.getElementById('projectId').value=this.value" class="input-100per" ${disabledProjectId ? 'disabled=disabled' : ''}>
   			<option value=""><fmt:message key='project.nothing.label' /></option>
    		<c:forEach items="${projectList}" var="pro">
				<option value="${pro.id}" title="${v3x:toHTML(pro.projectName)}" ${pro.id == projectId? 'selected':''}>${v3x:toHTML(pro.projectName)}</option>
			</c:forEach>
    	</select>
    	<input type="hidden" id="projectId" name="projectId" value="${projectId}">
    </td>
    <%--流程期限--%>
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="process.cycle.label"/>:</td>     
    <td>
    	<!-- 
    		流程期限与提醒规则：如果模版中设置了流程期限和提醒，则前台调用模版时均不能编辑，设置流程期限，未设置提醒，则提醒可编辑，都未设置则都可以编辑，流程期限和提醒为绑定关系（设置提醒必须设置流程期限） 
    		1.新建：调用设置流程期限和提醒的模版不可编辑    		
    		2.待发：设置流程期限或者提醒的模版待发后进行编辑流程期限和提醒不可编辑
    		3.模版中流程期限和提醒均未设置，调用模版后设置流程期限和提醒后保存待发，在待发列表中进行编辑可以修改
    		4.自由：新建与待发均可以编辑
    	-->
    	<select name="deadline" id="deadline" ${isTempleteHasDeadline ? 'disabled=disabled' : ''} style="width:90px" onchange="javascript:compareTime()">
   			<v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" showType="option" name="deadline" selected="${summary.deadline}" />
	    </select>
	    <c:if test="${isTempleteHasDeadline}">
	    	<select name="deadline" id="deadline" style="display: none" class="input-100per" onchange="javascript:compareTime()">
	    		<v3x:metadataItem metadata="${colMetadata['collaboration_deadline']}" showType="option" name="deadline" selected="${summary.deadline}" />
	   	 	</select>
   	 	</c:if>
    </td>
  </tr>
  <tr class="bg-summary" height="24"> 
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="workflow.label" />:</td>
    <td nowrap="nowrap">
    	<fmt:message key='default.workflowInfo.value' var="dfwf" />
    	<c:set value="${col:getWorkflowInfo(workflowInfo, colFlowPermPolicyMetadata, pageContext)}" var="wfInfo" />
        <c:choose>
			<c:when test = "${wfInfo eq null || wfInfo eq ''}">
				<input name="workflowInfo" id="workflowInfo" class="input-100per cursor-hand" readonly value="<c:out value="${wfInfo}" default="${dfwf}" /> " onclick="doWorkFlow('new')"  ${isFromTemplate == true && nonTextTemplete && isSystemTemplete ?'disabled' :'' }>
			</c:when>
			<c:otherwise>
				<input name="workflowInfo" id="workflowInfo" class="input-100per cursor-hand" readonly value="<c:out value="${wfInfo}　" default="${dfwf}" /> " onclick="doWorkFlow('new')"  ${isFromTemplate == true && nonTextTemplete && isSystemTemplete ?'disabled' :'' }>
			</c:otherwise>
		</c:choose>
    </td>
    <%--流程编辑--%>
    <td nowrap="nowrap" class="bg-gray workflow_new" valign="top">
        <fmt:message key="common.${isOnlyViewFlow? 'view':'design'}.workflow.label" bundle="${v3xCommonI18N}" var="workflowLable" />
      	<c:if test="${v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request) || isOnlyViewFlow}">
      	<div class="editeflow" onclick="designWorkFlow('','${isOnlyViewFlow}')" onmouseover="javascript:this.className='editeflow-over';" onmouseout="javascript:this.className='editeflow';">${workflowLable}</div>
      	</c:if>
    </td>     
    <%--预归档--%>
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="prep-pigeonhole.label" />:</td>     
    <td colspan="1">
	    <select id="colPigeonhole" class="input-100per" onchange="pigeonholeEvent(this)" 
	    	${(archiveName ne null && archiveName ne '' && isFromTemplate == true  && (isSystemTemplete && templete.type == 'templete' || isParentColTemplete))?'disabled' :'' }>
	    	<option id="defaultOption" value="1"><fmt:message key="common.default" bundle="${v3xCommonI18N}"/></option>   
	    	<option id="modifyOption" value="2">${v3x:_(pageContext, 'click.choice')}</option>
	    	<c:if test="${archiveName ne null && archiveName ne ''}" >
	    		<option value="3" selected>${archiveName}</option>
	    	</c:if>
	    </select>
    </td>
   <%-- 提醒--%>
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="common.remind.time.label" bundle='${v3xCommonI18N}' />:</td>
    <td>
    	<select name="advanceRemind" id="advanceRemind" ${isTempleteHasRemind ? 'disabled=disabled' : ''} class="input-100per" onchange="javascript:compareTime()">
    		<v3x:metadataItem metadata="${comMetadata}" showType="option" name="advanceRemind" selected="${summary.advanceRemind}" />
    	</select>
    	<c:if test="${isTempleteHasRemind}">
    		<select name="advanceRemind" id="advanceRemind" style="display: none" class="input-100per" onchange="javascript:compareTime()">
	    		<v3x:metadataItem metadata="${comMetadata}" showType="option" name="advanceRemind" selected="${summary.advanceRemind}" />
	    	</select>
    	</c:if>
    </td>
    <td align="center">
	    <%-- 
	    <c:if test="${v3x:getBrowserFlagByUser('SelectPeople', v3x:currentUser())==true}">
	    	<div onclick="showAdvance()" id="advanceButton" class="like-a"><fmt:message key="common.advance.label" bundle="${v3xCommonI18N}" /></div>
	   	</c:if>
	   	--%>
	   	<div onclick="showMoreInputs()" class="like-a" style="display: inline;"><fmt:message key="common.more.label" bundle="${v3xCommonI18N}"/></div>
	   	<div onclick="showMoreInputs()" class="more_down_new" id="more_Div" style="display: inline;"></div>
     </td>  
  </tr>
  </table>
  </td>	  	</tr>
	</table>
	<c:if test="${isForm==true}">
	</td>
	</tr>
	</c:if>
	<c:if test="${isForm!=true}">
	<table width="100%" id="editorWrapper" border="0" cellpadding="0" cellspacing="0" class="page-list-border">
	</c:if>
	<tr id="moreInputsTr1" class="lest-shadow" height="94px" style="display:none">
  	 <td class="bg-summary-ad" width="1%">&nbsp;</td>
   	 <td colspan="8" class="bg-summary-ad" style="padding: 5px 0px;">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
        	 <tr>
        	 	<td nowrap="nowrap">
					<label for="isTrack">
			    		<input type="checkbox" name="isTrack" value="1" onclick="setTrackRadiio();"id="isTrack" ${v3x:outConditionExpression(affair.isTrack, 'checked', '')} >&nbsp;<fmt:message key="track.label" />:
			    		<label for="trackRange_all">
							<input type="radio" name="trackRange" id="trackRange_all" onclick="setTrackCheckboxChecked();" value="1" ${empty trackIds?'checked':''}/>&nbsp;<fmt:message key="col.track.all" bundle="${v3xCommonI18N}" />
						</label>
						<label for="trackRange_part">
							<c:set value="${v3x:parseElementsOfIds(trackIds, 'Member')}" var="mids"/>
							<input type="hidden" value="" name="trackMembers" id="trackMembers"/>
							<v3x:selectPeople id="track" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Member" jsFunction="setPeople(elements)" originalElements="${mids}"/>	
							<input type="radio" name="trackRange"  id="trackRange_part" onclick="selectPeopleFunTrackNewCol()" value="0" ${not empty trackIds?'checked':''}/>&nbsp;<fmt:message key="col.track.part" bundle="${v3xCommonI18N}" />
						</label>
			    	</label>
        	 	</td>
        	 	<td align="right" nowrap="nowrap">
        	 		<fmt:message key="col.supervise.staff" />:&nbsp;&nbsp;
        	 	</td>
        	 	<td width="15%">
        	 		<input type="text" id="supervisors" class="input-100per cursor-hand" name="supervisors" readonly="true" onclick="clickSupervisor()" value="${supervisorIdStr}" >
        	 	</td>
        	 	<td align="right" nowrap="nowrap">
        	 		<fmt:message key="col.supervise.title"/>:&nbsp;&nbsp;
        	 	</td>
        	 	<td rowspan="2" style="padding: 5px 10px 5px 0px;" width="15%">
    				<textarea name="superviseTitle" id="superviseTitle" rows="3" cols="100" class="input-100per">${colSupervise.title }</textarea>
        	 	</td>	
		     </tr>
		     <tr>
        	 	<td>
        	 		<label>
        	 			<fmt:message key="collaboration.allow.chanage.label" />:&nbsp;&nbsp;
        	 		</label>
					<label for="allow_transmit">
			    		<input type="checkbox" name="canForward" id="allow_transmit" ${v3x:outConditionExpression(summary.canForward, 'checked', '')} ${(isFromTemplate == true || isForm == true) && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.transmit.label" />
			    	</label>
			    	<label for="allow_chanage_flow">
			    		<input type="checkbox" name="canModify" id="allow_chanage_flow" ${v3x:outConditionExpression(summary.canModify, 'checked', '')} ${(isFromTemplate == true || isForm == true) && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.chanage.flow.label" />
			    	</label>
			    	<label for="allow_edit">
			    		<input type="checkbox" name="canEdit" id="allow_edit" ${v3x:outConditionExpression(summary.canEdit, 'checked', '')} ${isFromTemplate == true && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.edit.label" />
			    	</label>
			    	<label for="allow_edit_attachment">
			    		<input type="checkbox" name="canEditAttachment" id="allow_edit_attachment" ${v3x:outConditionExpression(summary.canEditAttachment, 'checked', '')} ${isFromTemplate == true && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.edit.attachment.label" />
			    	</label>
			    	<label for="allow_pipeonhole">
			    		<input type="checkbox" name="canArchive" id="allow_pipeonhole" ${v3x:outConditionExpression(summary.canArchive, 'checked', '')} ${(isFromTemplate == true || isForm == true) && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.pipeonhole.label" />
			    	</label>
        	 	</td>
        	 	<td align="right" nowrap="nowrap">
        	 		<fmt:message key="col.supervise.deadline" />:&nbsp;&nbsp;
        	 	</td>
        	 	<td>
			           	<input type="text" name="awakeDate" id="awakeDate" class="input-100per cursor-hand" value="${superviseDate}" readonly="true"
			           		onclick="selectDateTime('${pageContext.request.contextPath}',this,400,200);" />
        	 	</td>	
        	 	<td>
        	 	&nbsp;
        	 	</td>
		     </tr>
		     <tr>
			     <td style="padding-left: 60px;">
		     		<label for="allow_auto_stop_flow">
			    		<input type="checkbox" name="canAutoStopFlow" id="allow_auto_stop_flow" ${v3x:outConditionExpression(summary.canAutoStopFlow, 'checked', '')} ${(isFromTemplate == true || isForm == true) && nonTextTemplete && isSystemTemplete?'disabled' :'' }>
			    		<fmt:message key="collaboration.allow.autostopflow.label" />
			    	</label>
			     </td>
		     	<td nowrap="nowrap" height="23" align="right">
		     		<fmt:message key="common.reference.time.label" bundle="${v3xCommonI18N }"></fmt:message>:&nbsp;&nbsp;
		     	</td>
		     	<td colspan="3">
		     		<c:choose>
			     		<c:when test = "${empty standardDuration or standardDuration eq 0 }">
			     			<fmt:message key="time.no" bundle="${workflowI18N}"></fmt:message>
			     		</c:when>
			     		<c:otherwise>
			     			${v3x:showDateByNature(standardDuration)}
			     		</c:otherwise>
		     		</c:choose>
		     		<input type="hidden" name="standardDuration" id="standardDuration" value="${standardDuration}" />
		     	</td>
		     </tr>
	     </table>
   		</td>
  </tr> 

  <tr id="attachment2TR" class="bg-summary" style="display:none;">
      <td nowrap="nowrap" height="18" width="7%" class="bg-gray" valign="top"><fmt:message key="common.mydocument.label" bundle="${v3xCommonI18N}" />:</td>
      <td colspan="11" valign="top"><div class="div-float">(<span id="attachment2NumberDiv"></span>)</div>
      <div></div><div id="attachment2Area" style="height:18px;"></div></td>
  </tr>
  <tr id="attachmentTR" class="bg-summary" style="display:none;">
      <td nowrap="nowrap" height="18" width="7%" class="bg-gray" valign="top"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" />:</td>
      <td colspan="11" valign="top"><div class="div-float">(<span id="attachmentNumberDiv"></span>)</div>
		<div id="attachmentArea" style="height:18px;" ><v3x:fileUpload attachments="${attachments}" applicationCategory="<%=ApplicationCategoryEnum.collaboration.key()%>" canDeleteOriginalAtts="${canDeleteOriginalAtts}" originalAttsNeedClone="${cloneOriginalAtts}" />
		</div>
      </td>
  </tr>
 
  <tr>
  	<td colspan="20" height="6">
	  	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top">
			  <td width="10" class="td_bg"></td>
			  <td class="bg-b" height="6"></td>
			  <td width="10" class="td_bg"></td>
			</tr>
		</table>
  	</td>
  </tr>
	<tr>
		<td colspan="20" >
	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top">
		    <td width="10" class="td_bg"></td>
			<td style="border:1px solid #dadada;">
			
			  <c:if test="${isForm==true}">
			    <table border="0" width="100%" cellpadding="0" cellspacing="0" style="height: 100%; table-layout: fixed;">		
		          <tr style="height: 100%">	
			         <td valign="top">
						<div class="scrollList" id="scrollListDiv"><img width="1" height="1">
			  </c:if>
			  <v3x:editor htmlId="content" content="${thisContent}" summaryId="${ColSummaryId}" type="${body.bodyType}" createDate="${body.createDate}" originalNeedClone="${cloneOriginalAtts}" category="<%=ApplicationCategoryEnum.collaboration.getKey()%>" />
			  <c:if test="${isForm==true}">
						</div>
                      </td>
				   </tr>
			     </table>
			  </c:if>
			</td>
			<td width="45" id="noteAreaTd" nowrap="nowrap">
		    	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" class="noteArea">
		   			<tr>
				  		<td valign="top" class="sign-button-bg"><table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   			<tr>
				   				<td class="right-scroll-bg">
								<div id="noteMinDiv" style="height: 100%" class="sign-min-bg">
									
									<div class="sign-min-label-newcoll" onclick="changeLocation('senderNote');showNoteArea();initFFScrollAttach2()">
										<div class="more_btn"></div>
										<div class="span_text">&nbsp;<fmt:message key="sender.note.label" /></div>
									</div>
									<%-- 
									<div class="separatorDIV"></div>
                                    --%>
								</div>
				   				<table id="noteAreaTable" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   					<tr>
						   				<td height="25" class="sign-bg3">
											<div id="hiddenPrecessAreaDiv" onclick="hiddenNoteArea();initFFScrollAttach1()" title="<fmt:message key='common.display.hidden.label' bundle='${v3xCommonI18N}' />"></div>
											<script type="text/javascript">
											var panels = new ArrayList();
											panels.add(new Panel("senderNote", '<fmt:message key="sender.note.label" />'));
											showPanels(false);
											</script>
								  		</td>
						  			</tr>
									<tr>
										<td height="25" class="senderNode"><fmt:message key="sender.note.label"/>(<fmt:message key="common.charactor.limit.label" bundle="${v3xCommonI18N}"><fmt:param value="500" /></fmt:message>)<td>
									</tr>
									<tr id="senderNoteTR" style="display:none;">
										<td class="note-textarea-td">
											<textarea cols="" rows="" id="note" name="note" validate="maxLength" inputName="<fmt:message key='sender.note.label' />" maxSize="500" class="note-textarea wordbreak padding-5px"><c:out value='${senderOpinionContent}' escapeXml='true' /></textarea>
										</td>
									</tr>
				   				</table></td>
							</tr>
				    	</table></td>
				   </tr>
				</table>
			</td>
		</tr>
	</table></td>
  </tr>
</table>


<div style="display:none" id="processModeSelectorContainer">
</div>

</form>

<iframe name="personalTempleteIframe" scrolling="no" frameborder="0" height="0" width="0"></iframe>
<iframe name="toXmlFrame" scrolling="no" frameborder="0" height="0" width="0"></iframe>
<iframe name="a8geniusFrame" scrolling="no" frameborder="0" height="0" width="0"></iframe>

<script type="text/javascript">
initContentTypeState();
hiddenNoteArea();
initProcessXml();
<c:if test="${!empty senderOpinionContent}">
changeLocation('senderNote');
showNoteArea();
</c:if>

if((isForm==true || isForm == "true") && document.getElementById("allow_edit")){
	document.getElementById("allow_edit").checked = false;
	document.getElementById("allow_edit").disabled = true;
}

function pigeonholeEvent(obj){
	var theForm = document.getElementsByName("sendForm")[0];
	switch(obj.selectedIndex){
		case 0 :
			var oldArchiveId = theForm.archiveId.value;
			if(oldArchiveId != ""){
				theForm.archiveId.value = "";
			}
			theForm.archiveId.value = "";
			break;
		case 1 : 
			doPigeonholeWindow('new', '<%=ApplicationCategoryEnum.collaboration.key()%>', 'newColl',obj);
			break;
		default :
			theForm.archiveId.value = document.getElementById("prevArchiveId").value;
			return;
	}
}
<c:if test="${param.form=='WaitSend' && isForm==true}">
	isFromWaitSend = true;
</c:if>
<c:if test="${source=='outlook'}">
	try{
		var ufa = new ActiveXObject('UFIDA_IE_Addin.Assistance');
		document.getElementById("subject").value=ufa.GetSendTitle();
        document.getElementById("content").value=ufa.GetOulookContent();		
	}catch(e){
	}
</c:if>
if(v3x.isIpad){
	var oHtml = document.getElementById('scrollDiv'); 
	if(oHtml){
		oHtml.style.height = "460px";	
		oHtml.style.overflow = "auto";	
		touchScroll("scrollDiv");
	}
}


addScrollForDocument();
function initFFScrollAttach1(){
	var oHeight = parseInt(document.body.clientHeight)-87;
	var oWidth = parseInt(document.body.clientWidth)-60;
	initFFScroll('scrollListDiv',oHeight,oWidth);
}
function initFFScrollAttach2(){
	var oHeight2 = parseInt(document.body.clientHeight)-87;
	var oWidth2 = parseInt(document.body.clientWidth)-205;
	initFFScroll('scrollListDiv',oHeight2,oWidth2);
}
initFFScrollAttach1();
if("${isForm}" == "true"){
	initIe10AutoScroll('scrollListDiv',87);
}

function initScrollForAll(){
	var editorWrapperObj = document.getElementById( 'editorWrapper' );
	if(editorWrapperObj){
		var height = document.body.clientHeight - 98;
		if(height < 0){
			editorWrapperObj.style.height = 400;
		}else{
			editorWrapperObj.style.height = height;
		}
	}
}
initScrollForAll();
jQuery().ready(function(){//为签章控件设置右键“删除[XXX]签批”菜单
	if(isForm == "true"){
		var i;
		var objs=document.getElementsByTagName("OBJECT");
		for(i=0;i<objs.length;i++)
		{
			if(objs[i].Enabled || objs[i].Enabled =='1'){
				objs[i].SetFieldByName("DELUSERNAME","${currentUser.name}");
			}
		}
	}
});
</script>
</body>
</html>