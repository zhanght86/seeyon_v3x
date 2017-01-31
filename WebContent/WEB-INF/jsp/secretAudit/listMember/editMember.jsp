<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>修改人员</title>
		<%@include file="../head.jsp"%>
		<c:set var="isModify" value="<%=com.seeyon.v3x.common.ldap.config.LDAPConfig.getInstance().isDisabledModifyPassWord()%>" />
		<script type="text/javascript">
			function submitForm(type){
				var theForm = document.getElementById("memberForm");
			  	if(theForm) {
			  	  	var id = document.getElementById("id").value;
			    	theForm.action = "${secretAuditURL}?method=auditMemberSecretLevel&id="+id+"&state="+type;
			    	theForm.submit() ;
			  	}
			}
		</script>
	</head>
	<body scroll="no" style="overflow: no">
		<c:set value="${(v3x:getSysFlagByName('sys_isGovVer')=='true')?'&& isIdCardNo()':''}" var="checkIdCard" />
		<form id="memberForm" method="post" target="editMemberFrame" action="">
			<input type="hidden" id="id" name="id" value="${member.v3xOrgMember.id}" />
			<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="">
				<tr align="center">
					<td height="8" class="detail-top">
						<script type="text/javascript">
						getDetailPageBreak(); 
						</script>
					</td>
				</tr>
				<tr>
					<td class="">
						<div class="scrollList">
							<%@include file="memberform.jsp"%>
						</div>		
					</td>
				</tr>
				<c:if test="${!readOnly}">
				<tr>
					<td height="42" align="center" class="bg-advance-bottom">
						<input id="submintButton" type="button" onclick="submitForm('pass');"    value="<fmt:message key='audit.state.2' />" class="button-default-2">&nbsp;
						<input id="submintCancel" type="button" onclick="submitForm('notPass');" value="<fmt:message key='audit.state.3' />" class="button-default-2">
					</td>
				</tr>
				</c:if>
			</table>
		</form>
		<iframe name="editMemberFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
	</body>
</html>