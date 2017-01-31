<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="Collaborationheader.jsp"%>
<title><fmt:message key="col.forward.page.title"/></title>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/form/js/formdisplay/SeeyonForm.js${v3x:resSuffix()}" />"></script>
<c:set value="${v3x:currentUser()}" var="currentUser" />
<v3x:attachmentDefine attachments="${attachment}"/>
<script type="text/javascript">
<!--
var hiddenMultipleRadio_forward = true;
var isConfirmExcludeSubDepartment_forward = true;
var flowSecretLevel_forward= "${secretLevel}";
var formContent = "";
var bodyType = "${bodyType}";
//add _colSummaryId
_selfColSummary = "${summaryId}";

function init(){
	var hasInitForm = false;
	if(bodyType=="FORM"){
		document.getElementById("tarea").value = "${formContent}";
		templateForm(document.getElementById("tarea").value,undefined);
		hasInitForm = true;
	} else {
		//如果是表单的话，这里基本上不会执行，但不知道前人为什么写这段代码，暂时保留
		if(window.dialogArguments.parent.detailMainFrame && window.dialogArguments.parent.detailMainFrame.contentIframe.document.getElementById("area")){
		  formContent = window.dialogArguments.parent.detailMainFrame.contentIframe.document.getElementById("area").innerHTML;
		}else if(window.dialogArguments.document.getElementById("area")){
		  formContent = window.dialogArguments.document.getElementById("area").innerHTML;
		}
	}
	if(formContent!="" || hasInitForm){
		if(!hasInitForm)
			document.getElementById("formDiv").innerHTML = formContent;
		var inputs = document.getElementById("formDiv").getElementsByTagName("INPUT");
		var textareas = document.getElementById("formDiv").getElementsByTagName("TEXTAREA");
		var selects = document.getElementById("formDiv").getElementsByTagName("SELECT");
		var objs = document.getElementById("formDiv").getElementsByTagName("Object");
		var imgs =  document.getElementById("formDiv").getElementsByTagName("IMG");
		for(var i=0;i<inputs.length;i++){
	  	  inputs[i].onkeypress="";
		  inputs[i].onchange="";
		  inputs[i].onclick="";
		  inputs[i].onmouseout = "";
		  inputs[i].onmouseover = "";
		   //inputs[i].onclick="";
		  inputs[i].onfocus="" ;
		  inputs[i].onblur="" ;	
		  if(inputs[i].type && inputs[i].type == 'radio'){
			inputs[i].disabled = true ;
	   	  }
	   	  if(inputs[i].type && inputs[i].type=="text"){
	   	  	inputs[i].readOnly = "readOnly";
	   	  }		 
		  // getInputShowValue(inputs[i]) ;		   
		}
			
		for(var i=0;i<textareas.length;i++)
			textareas[i].disabled = true;
		for(var i=0;i<selects.length;i++)
			selects[i].disabled = true;
		
		for(var i = 0; i < objs.length; i++){
			if(objs[i].classid=="clsid:2294689C-9EDF-40BC-86AE-0438112CA439"){
				objs[i].Enabled = "0";
				
			}
		}
		//去掉图片的onclick事件，并删除从表的增加和删除行按钮
		var count = 0;
		while(count <imgs.length){
			if(imgs[count].id && (imgs[count].id=="addImg" || imgs[count].id=="addEmptyImg" || imgs[count].id=="delImg" || imgs[count].id=="opencardId")){
				imgs[count].outerHTML = "";
				continue;
			}else if(imgs[count].getAttribute("onclick") != "" && imgs[count].getAttribute("onclick") != "null" && imgs[count].getAttribute("onclick") != null){
				imgs[count].removeAttribute("onclick");
				imgs[count].removeAttribute("title");
				imgs[count].setAttribute("style", "cursor: default");
				imgs[count].removeAttribute("onmouseover");
				imgs[count].removeAttribute("alt");
				if(imgs[count].getAttribute("src")){
				 if( imgs[count].getAttribute("src").indexOf('/fileUpload.do') != -1){
				   count++;
				   continue;
				 }				
				  var imgeUrl =  getImageUrl(imgs[count]) ;
				  if(imgeUrl != null) {
				     imgs[count].setAttribute("src",imgeUrl) ;
				  }
				}
			}				
			count++;
		}
		
		//过滤签章的隐藏input
		var handwriteInputs = document.getElementsByName("handwrite");
		if(handwriteInputs){
			count = 0;
			while(count <handwriteInputs.length){
				handwriteInputs[count].outerHTML = "";
			}
		}
		//在IE浏览器中执行innerHTML方法，会默认补全相对路径为绝对路径，例如/seeyon/office.do会自动变为http://192.168.1.1:80/seeyon/office.do
		formContent = document.getElementById("formDiv").innerHTML;
		//解决IE浏览器中执行innerHTML方法的相对路径自动补全为绝对路径的问题。
		if( formContent && formContent!='' ){
			formContent = formContent.replace( /href\=\"http.*\/seeyon\//ig , 'href=\"\/seeyon\/' );
		}
	}
}

function getImageUrl(element){
 if(!element){
    return null; 
  }
 var _src = element.getAttribute("src") ; 
 if(_src){
    var _indexNum = _src.indexOf("/seeyon/") ;
    var ster = _src.slice(_indexNum) ;
    return ster ;
  }
  return null ;
}


function getInputShowValue(aInput){
  if(aInput.getAttribute("access") && (aInput.getAttribute("access") == 'browse' || aInput.getAttribute("access") == "edit" )){
	   if(aInput.getAttribute("isfile") && aInput.getAttribute("isfile") == 'true'){
	   		
	   		if(aInput.getAttribute("isImage") && aInput.getAttribute("isImage") == 'true'){
	   		 			
	   			if(aInput.getAttribute("default") != null){
	   			aInput.className = "hidden" ;   
	   			   var image = appenImage(aInput.getAttribute("default"),aInput) ;
	   			   if(image){
		   			   image.title = "" ;
		   			   image.alt = "" ;
		   			   aInput.parentElement.appendChild(image) ;	
	   			   }
	   			}
	   		}  		
	   }else{
	     return ;
	   }
   }
}

function doForward() {
    var theForm = document.getElementsByName("sendForm")[0];
    if (!theForm) {
        return false;
    }

    if (!hasWorkflow) {
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
        selectPeopleFun_forward()
        return false;
    }

    if (!checkForm(theForm)) {
        return false;
    }

	theForm.formContent.value = formContent;
    saveAttachment();

    theForm.b1.disabled = true;
    theForm.b2.disabled = true;
    theForm.submit();
}

var hiddenMultipleRadio_forward = true;
var hiddenColAssignRadio_forward = true;
unallowedSelectEmptyGroup_forward = true;
var hiddenRootAccount_forward = true;

var currentUser = {
	id : "${currentUser.id}",
	name : "${v3x:escapeJavascript(currentUser.name)}"
}

//显示转发过来的附件和关联协同
function showAttachment(){
	var parentWindow = v3x.getParentWindow();
	var filesUploadAtt = parentWindow.fileUploadAttachments;
	var files=null;
	var fileUrlIds="";
	if(filesUploadAtt!=null&&typeof(filesUploadAtt)!="undifined"){
		files=filesUploadAtt.values();
	}
	var content=parentWindow.document.getElementById("content");
	var contentStr="";
	if(content!=null&&typeof(content)!="undifined"){
		contentStr=content.value;
	}
	document.getElementById("content").value = contentStr;
	if(!files){
		return;
	}
	for(var i = 0; i < files.size(); i++) {
		var attachment=files.get(i);
		if(fileUrlIds==""&&attachment.type=='0'){
			fileUrlIds+=attachment.fileUrl;
		}else if(fileUrlIds!=""&&attachment.type=='0'){
			fileUrlIds=fileUrlIds+","+attachment.fileUrl;
		}
		
		//处理上传的附件都要复制一份
		addAttachment(attachment.type, attachment.filename, attachment.mimeType, attachment.createDate, attachment.size, attachment.fileUrl, attachment.canDelete, true, attachment.description,attachment.extension,attachment.icon);
	}
}
//-->
</script>
</head>
<body scroll="no" onkeypress="listenerKeyESC()" onload="init();showAttachment()">
<c:if test="${col:isCanSendAccountColl()}">
	<c:set value="Account," var="accountStr"/>
</c:if>
<v3x:selectPeople id="forward" panels="Department,Team,Post,Outworker,RelatePeople" jsFunction="setPeopleFields(elements)" selectType="${accountStr}Department,Team,Post,Member" viewPage="selectNode4Workflow" />
<form name="sendForm" action="${detailURL}?method=doForward" target="forwardIframe" method="post" onsubmit="return false">
<input type="hidden" name="summaryId" value="${param.summaryId}">
<input type="hidden" name="process_desc_by" value="people">
<input type="hidden" name="process_xml" id="process_xml" value="" />
<input type="hidden" name="formContent" value="">
<span id="people" style="display:none;"></span>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#F0F5F9">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="col.forward.page.title"/></td>
	</tr>
	<tr>
		<td class="bg-advance-middel" height="100%">
		<div class="scrollList">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="24" colspan="2"><fmt:message key="col.forward.sp.title"/></td>
		  	</tr>
			<tr>
				<td height="28" colspan="2"><input id="workflowInfo" name="workflowInfo" class="input-100per cursor-hand" readonly value="<fmt:message key='default.workflowInfo.value' />" onclick="selectPeopleFun_forward()"></td>
		  	</tr>
			<tr>
				<td height="24" colspan="2"><fmt:message key="sender.note.label" /></td>
		  	</tr>
			<tr>
				<td colspan="2">
					<textarea name="note" id="content" cols="" rows="" style="height:100px;width:100%" validate="maxLength" inputName="<fmt:message key='common.opinion.label' bundle="${v3xCommonI18N}" />" maxSize="2000"></textarea>
					<div class="description-lable">(<fmt:message key="common.charactor.limit.label" bundle="${v3xCommonI18N}"><fmt:param value="2000" /></fmt:message>)</div>
				</td>
			</tr>
			<tr>
				<td height="32" colspan="2">
					<label for="forwardOriginalNode">
						<input type="checkbox" id="forwardOriginalNode" name="forwardOriginalNode" checked="checked">
						<fmt:message key="col.forward.originalnode.label" />
					</label>
					<label for="foreardOriginalopinion">
						<input type="checkbox" id="foreardOriginalopinion" name="foreardOriginalopinion" checked="checked">
						<fmt:message key="col.forward.originalopinion.label" />
					</label>
		            <label for="track">
		                <input type="checkbox" name="track" id="track" value="track" checked="checked">
		                <fmt:message key="track.label"/>
		            </label>
				</td>
			</tr>
			<tr id="_attachmentTR">
				<td height="20" width="20%" valign="top" nowrap="nowrap">
					<div class="link-blue" onclick="javascript:insertAttachment()">
						<img src="<c:url value='/common/images/attachment.gif' />" border="0" align="absmiddle">
						<fmt:message key="common.toolbar.insertAttachment.label" bundle="${v3xCommonI18N}"/>		
						(<span id="attachmentNumberDiv">0</span>)</div>
				</td>
				<td>
      				<div id="attachmentTR" valign="top" style="display: none;">
						<v3x:fileUpload applicationCategory="1" />
					</div>
				</td>
			</tr>
			<tr id="_attachment2TR">
				<td height="20" width="20%" valign="top" nowrap="nowrap">
	                <div class="link-blue" onclick="javascript:quoteDocument()">
						<img src="<c:url value='/common/images/attachment.gif' />" border="0" align="absmiddle">
						<fmt:message key="common.toolbar.insert.mydocument.label" bundle="${v3xCommonI18N}"/>
					(<span id="attachment2NumberDiv">0</span>)</div>
				</td>
				<td>
					<div id="attachment2Area" valign="top" style="display: none;">
					</div>
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
	<tr>
		<td height="42" align="right" class="bg-advance-bottom">
			<input type="button" name="b1" onclick="doForward()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;
			<input type="button" name="b2" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>
</form>
<div id="formDiv" style="display:none"><div id ="area" style="margin-left :20;margin-top:20"><div id="html" name="html" style="height:0px;display:none"><textarea id="tarea"></textarea></div></div><div id="img" name="img" style="height:0px;"></div></div>
<iframe src="" name="forwardIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>