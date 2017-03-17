<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
var formContent = "";
var bodyType = "${bodyType}";
function init(){
	var hasInitForm = false;
	if(window.dialogArguments.parent.detailMainFrame && window.dialogArguments.parent.detailMainFrame.contentIframe.document.getElementById("area")){
	  formContent = window.dialogArguments.parent.detailMainFrame.contentIframe.document.getElementById("area").innerHTML;
	}else if(window.dialogArguments.document.getElementById("area")){
	  formContent = window.dialogArguments.document.getElementById("area").innerHTML;
	}else if(bodyType=="FORM"){
		document.getElementById("tarea").value = "${formContent}";
		templateForm(document.getElementById("tarea").value,undefined);
		hasInitForm = true;
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
		  inputs[i].onfocus="" ;
		  inputs[i].onblur="" ;	
		  if(inputs[i].type && inputs[i].type == 'radio'){
			inputs[i].disabled = true ;
	   	  }
	   	  if(inputs[i].type && inputs[i].type=="text"){
	   	  	inputs[i].readOnly = "readOnly";
	   	  }		 
		   getInputShowValue(inputs[i]) ;		   
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
			if(imgs[count].id && (imgs[count].id=="addImg" || imgs[count].id=="addEmptyImg" || imgs[count].id=="delImg")){
				imgs[count].outerHTML = "";
				continue;
			}else if(imgs[count].getAttribute("onclick") != ""){
				imgs[count].setAttribute("onclick", "return false;");
				imgs[count].setAttribute("title", "");
				imgs[count].setAttribute("style", "cursor: default");
				imgs[count].setAttribute("onmouseover", "");
				imgs[count].setAttribute("alt", "");
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
		formContent = document.getElementById("formDiv").innerHTML;
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

var hiddenMultipleRadio_forward = true;
var hiddenColAssignRadio_forward = true;
unallowedSelectEmptyGroup_forward = true;
var hiddenRootAccount_forward = true;

var currentUser = {
	id : "${currentUser.id}",
	name : "${v3x:escapeJavascript(currentUser.name)}"
}

function OK() {
	var elements = window.frames["forward_IFRAME"].OK();
	setPeopleFields(elements);
    var theForm = document.getElementsByName("sendForm")[0];
    if (!theForm) {
        return false;
    }

    if (!checkForm(theForm)) {
        return false;
    }

	theForm.formContent.value = formContent;
    saveAttachment();
    
    // 2017-3-16 诚佰公司 添加异步提交表单
    $('#sendForm').ajaxSubmit({
        url : "${detailURL}?method=doForward",
        type : 'POST',
        async : false,
        success : function(data) {
        	//转换成页面js的json对象
        	var dataObj= eval('('+data+')');
        	if (dataObj.secretAlert){
        		alert(dataObj.secretAlert);
            	return;
        	}
        	eval('('+dataObj.afterForward+')');
        }
    });
    
   // 2017-3-16 诚佰公司 注释
   //theForm.submit();
}
//-->
</script>
</head>
<body scroll="no" onkeypress="listenerKeyESC()" onload="init()">
<c:if test="${col:isCanSendAccountColl()}">
	<c:set value="Account," var="accountStr"/>
</c:if>
<form id="sendForm" name="sendForm" action="${detailURL}?method=doForward" target="forwardIframe" method="post" onsubmit="return false">
<input type="hidden" name="summaryId" value="${param.summaryId}">
<input type="hidden" name="process_desc_by" value="people">
<input type="hidden" name="process_xml" id="process_xml" value="" />
<input type="hidden" name="formContent" value="">
<!-- 2017-3-16 诚佰公司 添加密级值 转发协同判断是否密级协同 -->
<input type="hidden" name="secretLevel" value="${secretLevel}" />
<span id="people" style="display:none;"></span>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#F0F5F9">
	<tr>
		<td height="450" align="center" colspan="2">
			<v3x:selectPeople id="forward" panels="Department,Team,Post,Outworker,RelatePeople" jsFunction="setPeopleFields(elements)" selectType="${accountStr}Department,Team,Post,Member" viewPage="selectNode4Workflow" include="true" />
		</td>
  	</tr>
  	<tr>
		<td width="30%" height="24" valign="top" style="padding: 5px;">
			<fmt:message key="sender.note.label" />:
		</td>
		<td height="24"  valign="top" style="padding: 5px;">
			<div class="description-lable div-float-right">(<fmt:message key="common.charactor.limit.label" bundle="${v3xCommonI18N}"><fmt:param value="500" /></fmt:message>)</div>
		</td>
  	</tr>
	<tr>
		<td style="padding: 5px;" valign="top" colspan="2">
			<textarea name="note" cols="" rows="" style="height:50px;width:100%" validate="maxLength" inputName="<fmt:message key='common.opinion.label' bundle="${v3xCommonI18N}" />" maxSize="500"></textarea>
		</td>
	</tr>
	<tr>
		<td align="right" valign="top" colspan="2" style="padding: 5px;">
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
</table>
</form>
<div id="formDiv" style="display:none"><div id ="area" style="margin-left :20;margin-top:20"><div id="html" name="html" style="height:0px;display:none"><textarea id="tarea"></textarea></div></div><div id="img" name="img" style="height:0px;"></div></div>
<iframe src="" name="forwardIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>