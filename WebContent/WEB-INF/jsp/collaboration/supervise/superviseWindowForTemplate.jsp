<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>    
<%@include file="../Collaborationheader.jsp" %>
<%@ include file="../../common/INC/noCache.jsp"%>
<fmt:setBundle basename="com.seeyon.v3x.system.resources.i18n.SysMgrResources" var="v3xSysI18N"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="${title}" /></title>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">

	var count = '${count}';
	var submitIt = "${submitIt}";

	function doIt(){
		var haveRole = false;
		var role = "";
		var sender = document.getElementById("sender");
		var senderDepManager = document.getElementById("senderDepManager");
		var senderSuperManager = document.getElementById("senderSuperManager");
		var roleNames = "";
		
		if(sender && sender.checked){
			role += "sender,";
			haveRole = true;
			roleNames = "<fmt:message key='sys.role.rolename.Sender' bundle='${v3xSysI18N }'/>";
		}
		if(senderDepManager && senderDepManager.checked){
			role += "senderDepManager,";
			roleNames += (haveRole?"、":"") + "<fmt:message key='sys.role.rolename.SenderDepManager' bundle='${v3xSysI18N }'/>";
			haveRole = true;
		}
		if(senderSuperManager && senderSuperManager.checked){
			role += "senderSuperManager,";
			haveRole = true;
		}
		if(haveRole){
			role = role.substring(0,role.length-1);
		}
		
		var mId = document.getElementById("supervisorMemberId");
		/*if((mId.value == null || mId.value == "")&&(haveRole==false)){
			alert(_("collaborationLang.col_supervise_select_member"));
			return;
		}*/
		
		var sDate = document.getElementById("superviseDate");
		if(sDate.value == null || sDate.value == ""){
			alert(_("collaborationLang.col_supervise_select_date"));
			return;
		}
		
		var sNames = document.getElementById("supervisorNames");
		
		var number = mId.value.split(",");
		if((count!=null && count!="undefined" && (new Number(count) > 10)) || number.length > 10){
			alert(_("collaborationLang.col_supervise_supervisor_overflow"));
			return;
		}
		
		var superviseTitle = document.getElementById("title").value;
		if(superviseTitle.length>66){
			alert(_("collaborationLang.col_supervise_title_overflow"));
			return;
		}
		
		if(submitIt == "1"){
			sendForm.action = colSuperviseURL + "?method=saveSupervise";
			sendForm.submit();
			return;
		}
		
		//var canModify = document.getElementById("canModify").value;
		window.returnValue = mId.value + "|" + sDate.value + "|" + sNames.value + "|" + superviseTitle + "|" + role + "|" + roleNames;
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

		whenstart(request,obj, width, height);
		
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
<c:set value='${v3x:showOrgEntitiesOfIds(supervisorId, "Member", pageContext)}' var='authStr' />
<c:set value='${v3x:parseElementsOfIds(supervisorId, "Member")}' var='authIds' /> 
<v3x:selectPeople id="sv" panels="Department" selectType="Member" minSize="0"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="sv(elements)"
                  originalElements="${authIds}"
                  />
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="${title}"/></td>
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
			        <td width="17%" height="26">&nbsp;</td>
			        <td width="83%">
			        	<label for="sender"> <input type="checkbox" id="sender" name="sender" <c:if test="${sender!='' && sender!=null}" > checked </c:if> ><fmt:message key="sys.role.rolename.Sender" bundle="${v3xSysI18N }"/></label>
			        </td>
			    </tr>
			    <tr>
			        <td height="28"><fmt:message key="col.supervise.deadline" />:</td>
			        <td>
			        <select id="superviseDate" name="superviseDate">
			        	<option value="1" <c:if test="${awakeDate == '1'}">selected</c:if> >1<fmt:message key="col.day.label" /></option>
			        	<option value="2" <c:if test="${awakeDate == '2'}">selected</c:if> >2<fmt:message key="col.day.label" /></option>
			        	<option value="3" <c:if test="${awakeDate == '3'}">selected</c:if> >3<fmt:message key="col.day.label" /></option>
			        	<option value="4" <c:if test="${awakeDate == '4'}">selected</c:if> >4<fmt:message key="col.day.label" /></option>
			        	<option value="5" <c:if test="${awakeDate == '5'}">selected</c:if> >5<fmt:message key="col.day.label" /></option>
			        	<option value="6" <c:if test="${awakeDate == '6'}">selected</c:if> >6<fmt:message key="col.day.label" /></option>			        	
			        	<option value="7" <c:if test="${awakeDate == '7'}">selected</c:if> >1<fmt:message key="col.week.label" /></option>
			        	<option value="14" <c:if test="${awakeDate == '14'}">selected</c:if> >2<fmt:message key="col.week.label" /></option>
			        	<option value="21" <c:if test="${awakeDate == '21'}">selected</c:if> >3<fmt:message key="col.week.label" /></option>
			        	<option value="30" <c:if test="${awakeDate == '30'}">selected</c:if> >1<fmt:message key="col.mounth.label" /></option>
			        	<option value="60" <c:if test="${awakeDate == '60'}">selected</c:if> >2<fmt:message key="col.mounth.label" /></option>
			        	<option value="90" <c:if test="${awakeDate == '90'}">selected</c:if> >3<fmt:message key="col.mounth.label" /></option>			        				        	
			        </select>
			        </td>			         
			    </tr>
			    <tr>
			    	<td valign="top"><fmt:message key="col.supervise.title"/>:</td>
			    	<td>
						<textarea name="title" id="title" rows="7" cols="" class="input-100per">${superviseTitle }</textarea>
			    	</td>
			    </tr>
			    <%--  tr>
			    	<td colspan="2">
			    		&nbsp;&nbsp;<input type="checkbox" name="canModify" id="canModify" ${canModify=='0'?'':'checked' }>&nbsp;&nbsp;<fmt:message key="col.supervise.canModify"/>
			    	</td>
			    </tr> --%>   
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