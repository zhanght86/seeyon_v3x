<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@include file="../head.jsp" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />"/>    
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body style="overflow: hidden;">
<div class="main_div_row2">
  <div class="right_div_row2">
    <div class="top_div_row2">
		<table border="0" cellSpacing="0" cellPadding="0" width="100%" class="main-bg border-top">
			<tr>
				<td class="border-padding">
					<span class="searchSectionTitle"><fmt:message key="menu.secret.history" bundle="${v3xMainI18N}" /></span>
				</td>
			</tr>
		</table>
    </div>
    <div class="center_div_row2" id="scrollListDiv">
	    <c:if test="${not empty isShowIndexSummary&&isShowIndexSummary=='false'}">
		 	<c:set value="true" var="isIndexSummary"/>
		</c:if> 
		<form>
		<v3x:table data="secretAuditList" var="bean" htmlId="listTable" showHeader="true" showPager="true" className="sort ellipsis" dragable="true">
		    <v3x:column width="15%" type="String" value="${bean.user}" align="left" label="secret.his.userName.lable" className=" sort" > </v3x:column>
			<v3x:column width="20%" type="String" align="left" value="${bean.account}" label="secret.his.accountName.lable" className=" sort" ></v3x:column>
			<v3x:column width="15%" type="String" align="left" value="${bean.depment}" label="secret.his.depatmentName.lable" className=" sort" ></v3x:column>
			<v3x:column width="15%" type="String" align="left" label="secretLevel" className=" sort" >
				 <fmt:message key='secretLevel.${bean.secretLevel}' />
			</v3x:column>
			<v3x:column width="15%" align="left" type="String" label="secret.his.auditResult.lable" className=" sort" > 
			 	<fmt:message key='audit.state.${bean.state}'/>
			</v3x:column>
			<v3x:column width="20%" type="String" align="left" label="secret.his.auditTime.lable" className=" sort" >
				<fmt:formatDate value="${bean.auditTime}" type="both" dateStyle="full" pattern="yyyy-MM-dd HH:mm"/>
			</v3x:column>
		</v3x:table>
		</form>
    </div>
  </div>
</div>
<div id="temp-div" name="temp-div" style="display:none">
<iframe name="temp-iframe" id="temp-iframe">&nbsp;</iframe>
</div>
</body>
</html>