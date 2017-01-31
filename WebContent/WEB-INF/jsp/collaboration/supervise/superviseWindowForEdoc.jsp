<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>    
<%@include file="../Collaborationheader.jsp" %>
<%@ include file="../../common/INC/noCache.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="col.supervise.label" /></title>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
	var count = '${count}';
	var submitIt = "${submitIt}";
	var oldSuperviseId = "${supervisorId}";
	var isDelete = false;
	function doIt(){
		var mId = document.getElementById("supervisorMemberId");
		if(mId.value == null || mId.value == ""){
			if(oldSuperviseId == ""){
				alert(_("collaborationLang.col_supervise_select_member"));
				return;
			}
			else{
				isDelete = true;
			}
		}
		
		var sDate = document.getElementById("superviseDate");
		if(sDate.value == null || sDate.value == ""){
			if(oldSuperviseId == ""){
				alert(_("collaborationLang.col_supervise_select_date"));
				return;
			}
		}
		
		var sNames = document.getElementById("supervisorNames");
		var number = mId.value.split(",");
		if((count!=null && count!="undefined" && (new Number(count) > 10)) || number.length > 10){
			alert(_("collaborationLang.col_supervise_supervisor_overflow"));
			return;
		}
		
		var superviseTitle = document.getElementById("title").value;
		if(superviseTitle.length>100){
			alert(_("collaborationLang.col_supervise_title_overflow"));
			return;
		}
		
		//var canModify = document.getElementById("canModify").value;
		
		var sVisorsFromTemplate = document.getElementById("sVisorsFromTemplate");
		var unCancelledVisor = document.getElementById("unCancelledVisor");
		if(sVisorsFromTemplate!=null && sVisorsFromTemplate.value=="true"){
			var uArray = unCancelledVisor.value.split(",");
			for(var i=0;i<uArray.length;i++){
				var have = mId.value.search(uArray[i]);
				if(have == -1){
					alert("模板自带督办人员不允许删除");
					return;
				}
			}
		}
		$("input[type='button']").each(
			 function() {
			 	$(this).attr('disabled','disabled');
			 }
		);
		if(submitIt == "1"){
			if(isDelete){
				document.getElementById("isDelete").value = "true";
			}
			sendForm.action = edocSuperviseURL + "?method=saveSupervise";
			sendForm.submit();
			return;
		}
		
		window.returnValue = mId.value + "|" + sDate.value + "|" + sNames.value + "|" + superviseTitle;
		window.close();
	}
	
	function sv(elements){
	if(elements){
		var obj1 = getNamesString(elements);
		var obj2 = getIdsString(elements,false);
		document.getElementById("supervisorNames").value = obj1;
		document.getElementById("supervisorMemberId").value = obj2;
		}
	}

	function selectDateTime(request,obj,width,height){
	
		var now = new Date();//当前系统时间
		var obj_date = now.format("yyyy-MM-dd");

		whenstart(request,obj, width, height,"datetime",false);
		
		if(obj.value != "" && obj.value<obj_date){
			if(!window.confirm(v3x.getMessage("collaborationLang.col_alertTimeIsOverDue"))){
				obj.value = "";
				return false;
				
			}
		}
	}
	//选督办人时显示外单位兼职
	onlyLoginAccount_sv = true;
	isNeedCheckLevelScope_sv = false;
	showConcurrentMember_sv = true;
	flowSecretLevel_sv = "${param.secretLevel}";
</script>
</head>
<body scroll="no" style="overflow: hidden" onkeydown="listenerKeyESC()">
<form name="sendForm" id="sendForm" method="post">
<input type="hidden" name="summaryId" id="summaryId" value="${param.summaryId }">
<input type="hidden" name="superviseId" id="superviseId" value="${superviseId }">
<input type="hidden" name="supervisorMemberId" id="supervisorMemberId" value="${supervisorId}"/>
<input type="hidden" name="remindMode" id="remindMode" />
<input type="hidden" name="unCancelledVisor" id="unCancelledVisor" value="${unCancelledVisor}">
<input type="hidden" name="sVisorsFromTemplate" id="sVisorsFromTemplate" value="${sVisorsFromTemplate}">
<input type="hidden" name="isDelete" value="false">
<c:set value="${v3x:showOrgEntitiesOfIds(supervisorId, 'Member', pageContext)}" var="authStr" />
<c:set value="${v3x:parseElementsOfIds(supervisorId, 'Member')}" var="authIds" /> 
<v3x:selectPeople id="sv" panels="Department" selectType="Member"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="sv(elements)"
                  originalElements="${authIds}" minSize="0"
                  />
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="edoc.supervise.label"/></td>
	</tr>
	<tr>
		<td class="bg-advance-middel">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%--
				<tr>
					<td id="assigned_supervisor" name="assigned_supervisor">&nbsp;&nbsp;<fmt:message key="edoc.supervise.assigned.supervisor" />&nbsp;:&nbsp;<font color="red">${supervisorNames}</font></td></tr>
				<tr>
					<td id="assigned_endDate" name="assigned_endDate">&nbsp;&nbsp;<fmt:message key="edoc.supervise.assigned.endDate" />&nbsp;:&nbsp;<font color="red"><fmt:formatDate value="${endDate}" type="both" dateStyle="full" pattern="yyyy-MM-dd"/></font></td>
				</tr>
				--%>
				<fmt:message key='common.default.selectPeople.value' bundle="${v3xCommonI18N}" var="spd" />
			    <tr>
			        <td width="17%" height="26"><fmt:message key="col.supervise.staff" />:</td>
			        <td width="83%">
			        	<input type="text" id="supervisorNames" class="input-100per cursor-hand" name="supervisorNames" readonly="true" 
			           				onclick="selectPeopleFun_sv();" 
			           				value="<c:out value='${authStr}' default='${spd}' escapeXml='true' />" >
			        </td>
			    </tr>
			    <tr>
			        <td height="28"><fmt:message key="col.supervise.deadline" />:</td>
			        <td>
			           	<input type="text" name="superviseDate" id="superviseDate" class="cursor-hand input-100per" value="${awakeDate}" readonly="true"
			           	onclick="selectDateTime('${pageContext.request.contextPath}',this,400,200);"
						value="<font color='red'>${awakeDate}</font>">
			        </td>
			    </tr>
			    <tr>
			    	<td valign="top"><fmt:message key="col.supervise.title"/>:</td>
			    	<td>
						<textarea name="title" id="title" rows="7" cols="" class="input-100per">${superviseTitle }</textarea>
			    	</td>
			    </tr>  
			</table>
		</td>
	</tr>
	<tr>
		<td height="42" align="right" class="bg-advance-bottom" colspan="2">
		    <input type="button" onclick="doIt();" class="button-default-2" value="<fmt:message key='common.button.ok.label' bundle='${v3xCommonI18N}'/>" />&nbsp;
		    <input type="button" onclick="window.close()" name="close" class="button-default-2" value="<fmt:message key='common.button.cancel.label' bundle='${v3xCommonI18N}'/>" />
		</td>
	</tr>
</table>
</form>
</body>
</html>