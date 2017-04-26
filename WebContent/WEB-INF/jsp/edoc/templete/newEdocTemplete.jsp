<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<%@page import="com.seeyon.v3x.common.constants.Constants" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../edocHeader.jsp" %>
<%@ include file="../../doc/pigeonholeHeader.jsp" %>
<c:choose>
	<c:when test="${templete.id != null}">
		<c:set value="${templete.categoryId}" var="categoryId" />
	</c:when>
	<c:otherwise>
		<c:set value="${param.categoryId}" var="categoryId" />
	</c:otherwise>
</c:choose>
<%-- <script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script> --%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
<!--
var isTempleteEditor = true;
var selfCreateFlow=true;
var logoURL = "${logoURL}";
var edocTemplateSaveUrl="${edocTempleteURL}?method=systemSaveTemplete";
var actorId=-1;//建立模版的权限为全部可以操作
var processing=false;
var hasDiagram = <c:out value="${hasWorkflow}" default="false" />;        
var caseProcessXML = '${process_xml}';
var caseLogXML = "";
var caseWorkItemLogXML = "";
var showMode = 1;
var currentNodeId = null;
var showHastenButton = "";
var appName="${appName}";
var isTemplete = true;
var defaultPermName="<fmt:message key='${defaultPermLabel}' bundle='${v3xCommonI18N}'/>";

var hiddenColAssignRadio_wf = true;
var editWorkFlowFlag = "true"

//flowSecretLevel_wf = "${!empty secret ? secret : 1}";
//2017-01-11 诚佰公司
flowSecretLevel_wf = "${!empty secret ? secret : ''}";

/*
window.onbeforeunload = function(){
		unloadFun();
}
*/
hasWorkflow = <c:out value='${hasWorkflow}' default='false' />;

var selectedElements = null;

//设置office正文能进行本地保存。主要是为了后续进行复制粘贴控制的时候不将正文设置成只读了。
var officecanSaveLocal="true";

function edocFormDisplay(){
    		var xml = document.getElementById("xml");
			var xsl = document.getElementById("xslt");
						enableButton("save");	
			if(xml!=null && xml!="" && xsl!=null && xsl!=""){
			try{
    			initSeeyonForm(xml.value,xsl.value);
			}catch(e){
				alert(_("edocLang.edoc_form_xml_error") +e);
				disableButton("save");
				window.location.href=window.location.href;
				return false;
			 }
			}  		
    		setObjEvent();
    		adjustDivHeight();
    		initContentTypeState();
    		substituteLogo(logoURL);
    		
    		showEdocMark();
    		
    		return false;
    	}
    	
formOperation = "aa";

function adjustDivHeight()
{  
  //var formDivObj=document.getElementById("formAreaDiv");
  //formDivObj.style.height=(screen.availHeight-288)+"px";  
}

//////////////////模版调用JS函数///////////////////////
function doAuth(elements){
	if(elements){
		document.getElementById("auth").value = getIdsString(elements);
	}
}
/**
 * 保存模板
 */
function saveTemplete(_type) {	
	var theForm = document.getElementsByName("sendForm")[0];
  	if(!notSpecChar(theForm.templatename)) {
    	return;
    }
	var subObj=theForm.elements['templatename'];
    if(subObj==null)
    {
      alert("没找到标题段，请检查公文单中是否有标题");
      return;
    }
    if(compareTime()==false){return;}
	if(_type==null)
	{
	  var typeObj=document.getElementById("template_type");
	  _type=typeObj.options[typeObj.selectedIndex].value;	  
	}
    if(validFieldData()==false){return;}    
    theForm.action=edocTemplateSaveUrl;    
    var subject=theForm.elements['templatename'].value;
    if(subject=="")
    {
      alert(_('edocLang.edoc_inputTemplateName'));
      theForm.elements['templatename'].focus();
      return;
    }
    var from = getParameter("from");
    if(from == "TM" && !theForm.categoryId.value){
    	alert(_('edocLang.templete_alertNoCategory'));
    	return;    	
    } 
    
    //2017-01-11 诚佰公司 添加密级空值校验
    if (checkForm(theForm) && checkSelectSecret(theForm) && checkRepeatTempleteSubject(theForm, true)) {
    	if(_type != 'text' && !checkSelectWF()){ //协同正文，不用流程
    		return;
    	}
    	if (_type != "workflow") { //流程模板，不保存office正文	        
        	if(!saveOffice()){
            	return;
        	}
        }
        
        //分枝 开始
        if(branchs){
			for(var i=0,j=keys.length;i<j;i++){
				var branch = branchs[keys[i]];
				if(branch!=null){
					var str = "<input type=\"hidden\" name=\"branchs\" value=\""+keys[i]+"↗"+branch.id
					+"↗"+branch.conditionType+"↗"+branch.formCondition+"↗"+branch.conditionTitle+"↗"+branch.isForce+"↗"+(branch.conditionDesc?branch.conditionDesc.escapeQuot():"")
					+"↗"+branch.conditionBase+"\">";
					branchDiv.innerHTML += str;
				}
			}
		}
		
		if(nodes != null && nodes.length>0){
        	var hidden;
        	for(var i=0;i<nodes.length;i++){
		        hidden = document.createElement('<INPUT TYPE="hidden" name="policys" value="' + policys[nodes[i]].name + '" />');
		        theForm.appendChild(hidden);
		        hidden = document.createElement('<INPUT TYPE="hidden" name="itemNames" value="' + policys[nodes[i]].value + '" />');
		        theForm.appendChild(hidden);
	        }
        }
        //分枝 结束


        //节点权限引用更新
		if(!isCheckNodePolicyFlag){
			var policyArr = new Array();
		    var itemNameArr = new Array();
		    if(nodes != null && nodes.length>0){
		    	for(var i=0;i<nodes.length;i++){
		    		var policyName = policys[nodes[i]].name;
		    		var itemName = policys[nodes[i]].value;
		    		policyArr.push(policyName);
		    		itemNameArr.push(itemName);
		    	}
		    }
		   
			try {
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
				requestCaller.addParameter(1, "String[]", policyArr);
				requestCaller.addParameter(2, "String[]", itemNameArr);
				requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
				var rs = requestCaller.serviceRequest();
				if(rs == "1"){
					alert(_("collaborationLang.node_policy_not_existence"));
					return;
				}
			}
			catch (ex1) {
				alert("Exception : " + ex1);
				return;
			}
		}
        saveAttachment();

        disableButtons();

        isSaveAction = true;
        isFormSumit=true;

        theForm.target = "_self";
        theForm.submit();

        top.startProc('');
    }
}

//2017-01-11 诚佰公司 发送表单验证流程密级是否为空
function checkSelectSecret(theForm) {
	var secretLevel = theForm.secretLevel.value;
    if (secretLevel == null || secretLevel == "") {
    	alert("流程密级不能为空。");
        return false;
    }

    return true;
}
// 诚佰公司

/**
 * 检测模板标题是否重名
 */
function checkRepeatTempleteSubject(form1){
	var categoryIdObj = form1.elements['categoryId'];
	var categoryId = categoryIdObj == null ? "" : categoryIdObj.value;
	var subject=form1.elements['templatename'].value;
	var id = form1.elements['id'].value;
	var isUpdate = (id != null) && (id != "");
		
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteManager", "checkSubject4System", false);
		requestCaller.addParameter(1, "Long", categoryId);
		requestCaller.addParameter(2, "String", subject);		
		
		var idList = requestCaller.serviceRequest();
		if(!idList){
			return true;
		}
		
		var count = idList.length;
				
		if(count < 1) return true;
		
		if(isUpdate == true && count == 1 && id == idList[0]){ //修改，存在的数据就是它自己
			return true;
		}

		alert(_("edocLang.templete_alertRepeatSubject"));
		return false;
	}
	catch (ex1) {
		alert("Exception : " + ex1.message);
	}

	return true;
}
function alertChangeType(obj){
	
	var opt = obj.options[obj.selectedIndex];
	

	//公文的流程模板、格式模板暂不支持授权给其他单使用[跨单位授权设计文档]
	
	if(opt.value == "text" || opt.value == "workflow"){
		document.getElementById("auth").value = "";
		onlyLoginAccount_auth = true;
		elements_auth = "";
		showAccountPanel_auth = false;
		
		
	}else if(opt.value == "templete"){
		onlyLoginAccount_auth = false;
		elements_auth  = "";
		showAccountPanel_auth = true;
	}
	
	alert(_("edocLang.templete_alertChangeType_" + opt.value, opt.text));
}

var hiddenPostOfDepartment_auth = true;

//分支  开始
var branchs = new Array();
var keys = new Array();
var hasKeys = true;
var policys = null;
var nodes = null;

<c:if test="${branchs != null}">
	<c:forEach items="${branchs}" var="branch" varStatus="status">
		var branch = new ColBranch();
		branch.id = ${branch.id};
		branch.conditionType = "${branch.conditionType}";
		branch.formCondition = "${v3x:escapeJavascript(branch.formCondition)}";
		branch.conditionTitle = "${v3x:escapeJavascript(branch.conditionTitle)}";
		branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
		branch.isForce = "${branch.isForce}";
		branch.conditionBase = "${branch.conditionBase}";
		eval("branchs["+${branch.linkId}+"]=branch");
		keys[${status.index}] = ${branch.linkId};
	</c:forEach>
</c:if>

//分支  结束
//-->
</script>
</head>
<body scroll="no" onLoad="edocFormDisplay()">
<iframe name="toXmlFrame" scrolling="no" frameborder="0" marginheight="0" marginwidth="0" height="0" width="0"></iframe>
<form name="sendForm" id="sendForm" method="post" action="${edocTempleteURL}?method=systemSaveTemplete">
<!-- 分枝 开始 -->
<div id="branchDiv"></div>
<!-- 分枝 开始 -->
<input type="hidden" name="appName" id="appName" value="<%=ApplicationCategoryEnum.edoc.getKey()%>">
<input type="hidden" name="id" id="id" value="${templete.id}">
<input type="hidden" name="process_desc_by" id="process_desc_by" value="${process_desc_by}" />
<input type="hidden" name="process_xml" id="process_xml" value="" />
<input type="hidden" name="edocType" id="edocType" value="${summary.edocType}"/>
<input type="hidden" name="categoryType" id="categoryType" value="${param.categoryType}" />
<input type="hidden" name="actorId" id="actorId" value="-1"/>
<input type="hidden" name="from" id="from" value="${param.from}"/>
<input type="hidden" name="archiveId" id="archiveId" value="${summary.archiveId}" />
<input type="hidden" name="prevArchiveId" id="prevArchiveId" value="${summary.archiveId}" />
<input type="hidden" name="supervisorId" id="supervisorId" value="${colSupervisors }">
<input type="hidden" name="supervisors" id="supervisors" value="${colSupervise.supervisors }">
<input type="hidden" name="awakeDate" id="awakeDate" value="${colSupervise.templateDateTerminal}">
<input type="hidden" name="superviseTitle" id="superviseTitle" value="${colSupervise.title }">
<input type="hidden" name="superviseRole" id="superviseRole" value="${colSuperviseRole }">
<input type="hidden" name="loginAccountId" id="loginAccountId" value="${v3x:currentUser().loginAccount}" >
<input type="hidden" name="workflowRule" id="workflowRule" value="<c:out value='${templete.workflowRule}' escapeXml='true' />" />
<%--是否是公文模板制作或者修改页面，主要用来传递传参,切换公文单的时候区别是前台调用还是后台管理员调用---%>
<input type="hidden" name="isEdocTempletePage" id="isEdocTempletePage" value ="true">

<input type="hidden" name="docMarkValue" id="docMarkValue" value ="${summary.docMark}">
<input type="hidden" name="docMark2Value" id="docMark2Value" value ="${summary.docMark2}">
<input type="hidden" name="docInmarkValue" id="docInmarkValue" value ="${summary.serialNo}">

<%@include file="../unitId.jsp" %>

<c:set value="${v3x:parseElements(templete.templeteAuths, 'authId', 'authType')}" var="authInfo" />
<input type="hidden" name="authInfo" id="auth" value="${authInfo}" />

<v3x:selectPeople id="auth" panels="Account,Department,Team,Post,Level" selectType="Account,Department,Team,Member,Post,Level" jsFunction="doAuth(elements)"
 originalElements="${authInfo}"  minSize="0"  showAllAccount="true" />
 
<script>onlyLoginAccount_wf=true;onlyLoginAccount_auth=false;</script>
<script>isNewOfficeFilePage=true;</script>
<script>showOriginalElement_wf=false;</script>
<script>showAccountShortname_wf="yes";</script>
<div name="edocContentDiv" id="edocContentDiv" style="display:none">
<v3x:editor htmlId="content" content="${body.content}" type="${body.contentType}" createDate="${body.createTime}" originalNeedClone="${cloneOriginalAtts}" category="<%=ApplicationCategoryEnum.edoc.getKey()%>" />
</div>
<span id="people" style="display:none;">
<c:out value="${peopleFields}" escapeXml="false" />
</span>

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="11" height="22" valign="top">
		<script type="text/javascript">
		var myBar = new WebFXMenuBar("${pageContext.request.contextPath}",'gray');
		
		var save = new WebFXMenu;
		save.add(new WebFXMenuItem("saveAsText", "<fmt:message key='templete.text.label' />", "saveTemplete('text')", "<c:url value='/apps_res/collaboration/images/text.gif'/>"));
		save.add(new WebFXMenuItem("saveAsWorkflow", "<fmt:message key='templete.workflow.label' />", "saveTemplete('workflow')", "<c:url value='/apps_res/collaboration/images/workflow.gif'/>"));
		save.add(new WebFXMenuItem("saveAsTemplete", "<fmt:message key='templete.category.type.${param.categoryType}' bundle='${colI18N}'/>", "saveTemplete('templete')", "<c:url value='/apps_res/collaboration/images/text_wf.gif'/>"));
		
		var saveAs = new WebFXMenu;
		saveAs.add(new WebFXMenuItem("saveAsText", "<fmt:message key='templete.text.label' />", "saveAsTemplete('text')", "<c:url value='/apps_res/collaboration/images/text.gif'/>"));
		saveAs.add(new WebFXMenuItem("saveAsWorkflow", "<fmt:message key='templete.workflow.label' />", "saveAsTemplete('workflow')", "<c:url value='/apps_res/collaboration/images/workflow.gif'/>"));
		saveAs.add(new WebFXMenuItem("saveAsTemplete", "<fmt:message key='templete.category.type.0' />", "saveAsTemplete('templete')", "<c:url value='/apps_res/collaboration/images/text_wf.gif'/>"));
		
		var insert = new WebFXMenu;
		insert.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.insert.localfile.label' bundle='${v3xCommonI18N}' />", "insertAttachment()"));
		<%--
		//insert.add(new WebFXMenuItem("", "<fmt:message key='common.toolbar.insert.mydocument.label' bundle='${v3xCommonI18N}' />", "quoteDocument()"));
		
		/*
		var workflow = new WebFXMenu;
		//workflow.add(new WebFXMenuItem("", "<fmt:message key='workflow.no.label' />", "doWorkFlow('no')"));
		workflow.add(new WebFXMenuItem("", "<fmt:message key='workflow.new.label' />", "doWorkFlow('new')"));
		workflow.add(new WebFXMenuItem("", "<fmt:message key='workflow.edit.label' />", "designWorkFlow('detailIframe')"));
		*/		
		//myBar.add(new WebFXMenuButton("save", "<fmt:message key='common.toolbar.save.label' bundle='${v3xCommonI18N}' />", "", "<c:url value='/common/images/toolbar/save.gif'/>", "", save));		
		--%>
		myBar.add(new WebFXMenuButton("save", "<fmt:message key='common.toolbar.save.label' bundle='${v3xCommonI18N}' />","saveTemplete();",[1,5], "", null));
		myBar.add(new WebFXMenuButton("auth", "<fmt:message key='common.toolbar.auth.label' bundle='${v3xCommonI18N}' />", "selectPeopleFun_auth()", [2,2]));
		//myBar.add(new WebFXMenuButton("workflow", "<fmt:message key='workflow.label' />", "", "<c:url value='/apps_res/collaboration/images/workflow.gif'/>", "", workflow));
		myBar.add(new WebFXMenuButton("workflow", "<fmt:message key='common.design.workflow.label' bundle='${v3xCommonI18N}'/>", "designWorkFlow('detailIframe')",[3,6], "", null));
		myBar.add(new WebFXMenuButton("insert", "<fmt:message key='common.toolbar.insert.label' bundle='${v3xCommonI18N}' />", null, [1,6], "", insert));
		//myBar.add(new WebFXMenuButton("bodyTypeSelector", "<fmt:message key='common.body.type.label' bundle='${v3xCommonI18N}' />", null, "<c:url value='/common/images/toolbar/bodyTypeSelector.gif'/>", "", bodyTypeSelector));
			//设置收文模板页面支持PDF正文。
		if("${param.categoryType}"==3){
			var supportPdfMenu=true;
		}
		myBar.add(${v3x:bodyTypeSelector("v3x")});
		myBar.add(new WebFXMenuButton("content", "<fmt:message key='common.toolbar.content.label' bundle='${v3xCommonI18N}' />","openNewEditWin();",[8,10], "", null));
		
		myBar.add(new WebFXMenuButton("superviseSetup", "<fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' />", "openSuperviseWindowForTemplate()",[3,10], "", null));
		if(appName=="sendEdoc"){
			myBar.add(new WebFXMenuButton("taohong", "<fmt:message key='edoc.action.form.template' />","taohongWhenTemplate('edoc');",[8,10], "", null));
		}
		document.write(myBar);
		document.close();
		</script></td>
  </tr>
  <tr class="bg-summary">
   	<td nowrap="nowrap" height="24" class="bg-gray"><fmt:message key="edoc.doctemplate.name"/>:</td>
    <td nowrap="nowrap" width="20%"><input name="templatename" id="templatename" inputName="<fmt:message key='edoc.doctemplate.name'/>" maxlength='80' class="input-100per " value="${templete.subject==null?'':templete.subject}" ></td> 
    <td nowrap="nowrap" height="24" class="bg-gray"><fmt:message key="edocTable.label" />:</td>
    <td nowrap="nowrap" width="30%"><select name="edoctable" id="edoctable" class="input-100per" onChange="javascript:changeEdocForm(this);">
        <c:forEach var="edocForm" items="${edocForms}">
    		<option value="<c:out value="${edocForm.id}"/>" <c:if test="${edocForm.id==edocFormId}">selected</c:if>><c:out value="${edocForm.name}"/></option>
   			</c:forEach>
    	</select>
    </td>
    <td nowrap="nowrap" width="6%" class="bg-gray"><font color="red"><fmt:message key="collaboration.secret.flowsecret" bundle="${colI18N}"/>:</font></td>
    <td nowrap="nowrap" width="8%" colspan="3">
    	<!-- 2017-01-11 诚佰公司 -->
	    <select name="secretLevel" id="secretLevel" style="width:70px" onchange="changeSecretLevel(this);">
	    	<option value=""></option>
	    	<option value="1" ${secret == 1 ? 'selected' :''}><fmt:message key="collaboration.secret.nosecret" bundle="${colI18N}"/></option>
	    	<option value="2" ${secret == 2 ? 'selected' :''}><fmt:message key="collaboration.secret.secret" bundle="${colI18N}"/></option>
	    	<option value="3" ${secret == 3 ? 'selected' :''}><fmt:message key="collaboration.secret.secretmore" bundle="${colI18N}"/></option>
	    </select>

    	&nbsp;&nbsp;<fmt:message key="templete.type.label" bundle="${colI18N}"/>:
	    <select id="template_type" name="type" onChange="alertChangeType(this)">
	    <option value="templete" selected><fmt:message key='templete.category.type.${param.categoryType}'  bundle="${colI18N}"/></option>
	    <option value="text"<c:if test="${templateType=='text'}"> selected</c:if>><fmt:message key='templete.text.label'  bundle="${colI18N}"/></option>
	    <option value="workflow"<c:if test="${templateType=='workflow'}"> selected</c:if>><fmt:message key='templete.workflow.label'  bundle="${colI18N}"/></option>
    	</select>
    </td>
    
    <td width="8%" class="bg-gray"><fmt:message key="prep-pigeonhole.label.to" />:</td>
    <td width="10%">
    	<select id="selectPigeonholePath" class="input-100per" onchange="pigeonholeEvent(this,'<%=ApplicationCategoryEnum.edoc.key()%>','templete',this.form)">
	    	<option id="defaultOption"  value="1"><fmt:message key="common.default" bundle="${v3xCommonI18N}"/></option>   
	    	<option id="modifyOption" value="2">${v3x:_(pageContext, 'click.choice')}</option>
	    	<c:if test="${archiveName ne null && archiveName ne ''}" >
	    		<option value="3" selected>${archiveName}</option>
	    	</c:if>
	    </select>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr class="bg-summary">
  	<td width="8%" height="29" class="bg-gray"><fmt:message key="workflow.label" />:</td>
   	<td colspan="1">        
    	<fmt:message key='default.workflowInfo.value' var="dfwf" /><c:set value="${col:getWorkflowInfo(workflowInfo, flowPermPolicyMetadata, pageContext)}" var="wfInfo" />
        <input name="workflowInfo" class="input-100per cursor-hand" readonly value="<c:out value="${wfInfo}" default="${dfwf}" />" onClick="designWorkFlow('detailIframe')" ${isFromTemplate == true ? 'disabled' : ''}></td>
	<td nowrap="nowrap" class="bg-gray"><fmt:message key="process.cycle.label"/>:</td>     
    <td colspan="1">
    	<select name="deadline" id="deadline" class="input-100per" onChange="javascript:compareTime(this)" style="width: 100%;">
	    	<v3x:metadataItem metadata="${deadlineMetadata}" showType="option" name="deadline" selected="${summary.deadline}" bundle="${colI18N}"/>
	    </select>
    </td>
	<td width="6%" nowrap="nowrap" class="bg-gray"><fmt:message key="common.remind.time.label" bundle='${v3xCommonI18N}' />:</td>     
    <td width="6%">
<!--     	<div style="text-align: left; float: left; border: 1px solid red; width: 40%;"> -->
   		 	<select name="advanceRemind" id="advanceRemind" class="input-100per" onChange="javascript:compareTime(this)">
	    		<v3x:metadataItem metadata="${remindMetadata}" showType="option" name="deadline" selected="${summary.advanceRemind}"  bundle="${v3xCommonI18N}"/>
	    	</select>
<!--     	</div> -->
<!--     	<div style="text-align: right; float: left; border: 1px solid red;width: 60%;"> -->
<%--     		<fmt:message key="process.cycle.label"/>: --%>
<!-- 	    	<select name="deadline1" id="deadline1" class="input-100per" onChange="javascript:compareTime(this)" style="width: 60%"> -->
<%-- 	    	<v3x:metadataItem metadata="${deadlineMetadata}" showType="option" name="deadline" selected="${summary.deadline}" bundle="${colI18N}"/> --%>
<!-- 	    	</select> -->
<!--     	</div> -->
    </td>
    <td width="6%" align="right">
    	<fmt:message key="common.reference.time.label" bundle='${v3xCommonI18N}'/>:&nbsp;&nbsp;
    </td>
    <td width="6%" align="left">
    	<select name="referenceTime" id="referenceTime" class="input-100per" onChange="javascript:compareTime(this)" style="width: 100%;">
	    	<v3x:metadataItem metadata="${deadlineMetadata}" showType="option" name="referenceTime1" selected="${templete.standardDuration}" bundle="${colI18N}"/>
	    </select>
    </td>
    <td nowrap="nowrap" class="bg-gray"><fmt:message key="track.label" />:</td>
    <td>
	<select name="canTrack" id="track" class="input-100per"> 
    	<option value=1 <c:if test="${summary.canTrack==1}">selected</c:if>><fmt:message key="edoc.form.yes" /></option>
    	<option value=0 <c:if test="${summary.canTrack==0}">selected</c:if>><fmt:message key="edoc.form.no" /></option>   		
    	</select>
    </td>
    <td>
    <div style="visibility:hidden">
    <select name="categoryId" id="categoryId" class="input-100per">
			<c:if test="${param.from == 'SYS'}">
				<option value="${param.categoryType}"><fmt:message key="templete.category.type.${param.categoryType}" bundle="${colI18N}"/></option>
			</c:if>
			${categoryHTML}
		</select>
		<script type="text/javascript">setSelectValue('categoryId', '${categoryId}');</script>
	</div>
</td>    
  </tr>
  <tr id="attachmentTR" class="bg-summary" style="display:none;">
      <td nowrap="nowrap" height="18" class="bg-gray" valign="top"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" /></td>
      <td colspan="8" valign="top"><div class="div-float">(<span id="attachmentNumberDiv"></span>个)</div>
		<v3x:fileUpload attachments="${attachments}" canDeleteOriginalAtts="${canDeleteOriginalAtts}" originalAttsNeedClone="${cloneOriginalAtts}" />      </td>
  </tr>
  <tr>
  	<td colspan="10" height="6" class="bg-b"></td>
  </tr>
  <tr valign="top">
	<td colspan="10"><table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top">
			<td>
				<div id="formAreaDiv" class="scrollList">
					<div style="display:none">
						<textarea id="xml" cols="" rows="">${formModel.xml}</textarea>
		         	</div>
		         	<div style="display:none">
				   		<textarea id="xslt" cols="" rows="">${formModel.xslt}</textarea>
				    </div>
				 	<div id="html" name="html" style="border:1px solid;border-color:#FFFFFF;height:0px;"></div>
				 	<div id="img" name="img" style="height:0px;"></div>	 
					<div style="display: none">
						<textarea name="submitstr" id="submitstr" cols="" rows=""></textarea>
					</div>
				</div>
			</td>
			<!--td width="45px" id="noteAreaTd" nowrap="nowrap">
		    	<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" class="noteArea">
		   			<tr>
				  		<td valign="top" class="sign-button-bg" ><table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   			<tr>
				   				<td class="right-scroll-bg">
								<div id="noteMinDiv" style="height: 100%" class="sign-min-bg">
									<div class="sign-min-label" onClick="changeLocation('senderNote');showNoteArea()"><fmt:message key="sender.note.label" /></div>
									<div class="separatorDIV"></div>
								</div>
				   				<table id="noteAreaTable" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				   					<tr>
						   				<td height="25">
						   				<div id="hiddenPrecessAreaDiv" onClick="hiddenNoteArea()" title="<fmt:message key='common.display.hidden.label' bundle='${v3xCommonI18N}' />"></div>
						   				<script type="text/javascript">
											var panels = new ArrayList();
											panels.add(new Panel("senderNote", '<fmt:message key="sender.note.label" />'));
											
											showPanels(false);
											</script>
										</td>
						  			</tr>
									<tr>
										<td height="25" class="senderNode"><fmt:message key="sender.note.label"/>(<fmt:message key="common.charactor.limit.label" bundle="${v3xCommonI18N}"><fmt:param value="200" /></fmt:message>)<td>
									</tr>
									<tr id="senderNoteTR" style="display:none;">
										<td class="note-textarea-td">
											<textarea cols="" rows="" name="note" validate="maxLength" inputName="<fmt:message key='sender.note.label' />" maxSize="200" class="note-textarea wordbreak"><c:out value='${summary.senderOpinion.content}' escapeXml='true' /></textarea>										</td>
									</tr>	   				
				   				</table></td>
							</tr>
				    	</table></td>
				   </tr>
				</table></td-->
		</tr>
	</table></td>
  </tr>
</table>

<div style="display:none" id="processModeSelectorContainer">
    <%@include file="../processModeSelector.jsp" %>
</div>
</form>

<iframe name="personalTempleteIframe" scrolling="no" frameborder="0" height="0" width="0"></iframe>
<script type="text/javascript">
initProcessXml();
hiddenNoteArea();
<c:if test="${isFromTemplate}" >
isFromTemplate = true;
</c:if>
<c:if test="${not isFromTemplate}" >       
isFromTemplate = false;
</c:if>
</script>
</body>
</html>