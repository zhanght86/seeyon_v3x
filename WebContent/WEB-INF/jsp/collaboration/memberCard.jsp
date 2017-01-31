<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources" var="mainResources" />
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="common.member.info" /></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
${v3x:skin()}
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
var v3x = new V3X();
v3x.init("<c:out value='${pageContext.request.contextPath}' />", "<%=com.seeyon.v3x.common.i18n.LocaleContext.getLanguage(request)%>");
v3x.loadLanguage("/apps_res/addressbook/js/i18n");

function addRelativePeople(){
  v3x.openWindow({
         url: "<html:link renderURL='/relateMember.do'/>?method=addRelativePeople&receiverId=${param.memberId}",
         width : "440",
         height : "200",
         scrollbars : "no"
   });
}
</script>
<c:set var="ln" value="${v3x:urlEncoder(param.ln)}"/>
<c:set var="dn" value="${v3x:urlEncoder(param.dn)}"/>
<c:set var="pn" value="${v3x:urlEncoder(param.pn)}"/>
<c:set var="sp" value="${v3x:urlEncoder(param.sp)}"/>
<c:set var="ty" value="${v3x:urlEncoder(param.ty)}"/>
</head>
<body scroll="no" style="overflow: hidden;width:100%;height:100%;" onkeydown="listenerKeyESC()" class="mxt-window">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" >
	<tr>
		<td><iframe src="<html:link renderURL='/addressbook.do' />?method=viewMember&mId=${param.memberId}&dn=${dn}&ln=${ln}&pn=${pn}&sp=${sp}&ty=${ty}" name="" frameborder="0" height="100%" width="100%" scrolling="no" marginheight="0" marginwidth="0"></iframe></td>
	</tr>
	<c:set value="${v3x:currentUser()}" var="cUser" />
	<c:if test="${v3x:getBrowserFlagByRequest('OnDbClick', pageContext.request)}">
		<tr class="mxt-window-footer">
			<td height="42" align="right" class="bg-advance-bottom buttonsDiv">
				<c:if test="${!cUser.administrator && !cUser.systemAdmin && !cUser.groupAdmin && cUser.id != param.memberId}">
				<c:if test="${cUser.internal eq true}">	
				<c:set value="${v3x:getOrgEntity('Member', param.memberId)}" var="mem"/>
					<c:if test="${mem.enabled && !mem.isDeleted}">
						<a class="like-a cursor-hand" onClick="javascript:addRelativePeople()"><fmt:message key='common.my.peoplerelate.add' bundle='${mainResources}' /></a>&nbsp;&nbsp;&nbsp;&nbsp;
						<!--174����  ����ƽ    �ر�վ����Ϣ����   -->
						<!--  <a class="like-a cursor-hand" onClick="javascript:sendMessageForCard(false, '${param.memberId}')"><fmt:message key='message.sendDialog.title' bundle='${mainResources}' /></a>&nbsp;&nbsp;&nbsp;&nbsp;-->
					</c:if>
						</c:if>
				</c:if>
				<input type="button" onClick="window.close()" value="<fmt:message key='common.button.close.label' />" class="cursor-hand button-default-2">
			</td>
		</tr>
	</c:if>
</table>
</body>
</html>