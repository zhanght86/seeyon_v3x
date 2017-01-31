<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="logHeader.jsp" %>
<fmt:message key="menu.audit.${param.type eq 'logon' ? 'loginLog':'appLog'}" bundle="${v3xMainI18N}" var="title"/>
<title>${title}</title>
<script type="text/javascript">
function ok(){
	var theForm = document.forms["selectDateForm"];
	if(checkForm(theForm)){
		if(!confirm(v3x.getMessage("LogLang.logon_del"))){
			window.close();
		}
		else{
			window.returnValue = theForm.selectDate.value;
			window.close();
		}
	}
}
function showAlert(){
	alert(v3x.getMessage("LogLang.logon_clear_sucess"));
}
</script>
</head>
<body scroll="no" onkeydown="listenerKeyESC()">
<form name="selectDateForm">
<table class="popupTitleRight" border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
	<tr>
		<td height="20" class="PopupTitle">
			${title}
		</td>
	</tr>
	<tr class="bg-advance-middel">
		<td height="100%">
			<fmt:message key="logon.selectClearDate.label" />: 
			<select name="selectDate">
			<!-- 审计日志最少要保留六个月     174工厂     汪成平 start -->
			<!--<option value="1"><fmt:message key="logon.period.1" /></option>
				<option value="3"><fmt:message key="logon.period.3" /></option>-->
			<!-- 审计日志最少要保留六个月     174工厂    汪成平 end -->	
				<option value="6"><fmt:message key="logon.period.6" /></option>
				<option value="9"><fmt:message key="logon.period.9" /></option>
				<option value="12"><fmt:message key="logon.period.12" /></option>
				<option value="24"><fmt:message key="logon.period.24" /></option>
				<option value="36"><fmt:message key="logon.period.36" /></option>
				<option value="60"><fmt:message key="logon.period.60" /></option>
				<option value="84"><fmt:message key="logon.period.84" /></option>
				<option value="120"><fmt:message key="logon.period.120" /></option>
			</select>
			<br><br>
			<span>* 当前日志的最小保存期限为：<fmt:message key='metadata.log.deadline.${logDeadline}' bundle='${v3xMainI18N}' /></span><br>
			<span class="description-lable">* <fmt:message key="logon.selectClearDate.tip" /></span>
		</td>
	</tr>
	<tr>
		<td height="42" class="bg-advance-bottom" align="center" valign="top">			
			<input type="button" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;&nbsp;
			<input type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>
</form>
</body>
</html>