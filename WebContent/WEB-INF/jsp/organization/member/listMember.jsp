<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">	
<%@page import="java.util.Properties"%>
<html>
<head>
<title>成员列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<%@include file="../organizationHeader.jsp"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />"/>
${v3x:skin()}
<script type="text/javascript">
 //判断如果不是单位或者集团或者系统管理员。就是HR 管理员
 var detailFrameShowName = getA8Top().findMenuItemName(1405);
 var isHRAdmin = false;
 try{//可能报错，异常处理
	 if(!${v3x:currentUser().administrator}&& !${v3x:currentUser().groupAdmin}&&!${v3x:currentUser().systemAdmin}){
		<%@ page import="com.seeyon.v3x.organization.manager.OrgManager" %>
		<%@ page import="com.seeyon.v3x.common.web.util.ApplicationContextHolder" %>
		<%@ page import="com.seeyon.v3x.menu.check.MenuCheckHelper" %>
		<%@ page import="com.seeyon.v3x.common.web.login.CurrentUser" %>
		<%@ page import="com.seeyon.v3x.common.authenticate.domain.User" %>
		<%
		OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		User currentUser = CurrentUser.get();
		%>
		if (<%=MenuCheckHelper.isHRAdmin(orgManager, currentUser.getId(), currentUser.getLoginAccount())%>) { // HR管理员
	 	isHRAdmin = true;
	 	 var detailFrameShowName = "<fmt:message key='hr.organization.member.label' bundle='${v3xHRI18N}' />";
	 	getA8Top().showLocation(1201, detailFrameShowName);
		} else { // 部门管理员
			getA8Top().showLocation(1301);
		}
	 }else{
	     getA8Top().showLocation(1405);
	 }
  }catch(e){}
// 添加选人方法
function setTeamDept(elements) {
   	if (!elements) {
       	return;
   	}
   	document.getElementById("deptName").value = getNamesString(elements);
   	document.getElementById("textfields").value = getIdsString(elements, false);
}

var onlyLoginAccount_dept = true;

function showDetail(id,type,tframe){
	tframe.location.href = organizationURL+"?method=edit"+type+"&id="+id+"&isDetail=readOnly&deptAdmin=${param.deptAdmin}&isHRAdmin="+isHRAdmin;
}
function modify(){
	
	var id = getSelectIds(parent.listFrame);
	var ids = id.split(",");
	if(ids.size()==2){
	    parent.detailFrame.location.href="${organizationURL}?method=editMember&id="+ids[0]+"&deptAdmin=${param.deptAdmin}&isHRAdmin="+isHRAdmin;
	}else if(ids.size()>2){
		alert(v3x.getMessage("organizationLang.orgainzation_select_one_once"));
		return false;				
	}else{
		alert("<fmt:message key="member.chosece.modify"/>");
		return false;
	}
}
	function impPost(){
	   var sendResult = v3x.openWindow({
	   url : "${ldapSynchron}?method=importLDIF",
	   width : "390",
	   height : "210",
	   resizable : "false",
	   scrollbars:"yes"
	});
	if(!sendResult)
	{
	return;
	}
	else
	{
	parent.detailFrame.location.href="${ldapSynchron}?method=uploadReport&parseTime=" + sendResult;
	}
			          //parent.detailFrame.location.href="${ldapSynchron}?method=importLDIF";
		}
</script>
</head>
<body >
<div class="main_div_row2" >
  <div class="right_div_row2" style="_padding-top:0;">
    <div class="center_div_row2" id="scrollListDiv" style="top:0px;">
<input type="hidden" value="${condition}" id="condition" name="condition">
<input type="hidden" value="${textfield}" id="textfield" name="textfield">
	<form id="memberform" name="memberform" method="post">
	<fmt:message key="org.entity.disabled" var="orgDisabled"/>
	<fmt:message key="org.entity.deleted" var="orgDeleted"/>
	<fmt:message key="org.entity.transfered" var="orgTransfered"/>
		<v3x:table htmlId="memberlist" data="memberlist" var="member" className="sort ellipsis">
		<c:set var="click" value="showDetail('${member.v3xOrgMember.id}','Member',parent.detailFrame)"/>
		<c:set var="dbclick" value="modify();"/>
			<v3x:column width="5%" align="center" label="<input type='checkbox' id='allCheckbox' onclick='selectAll(this, \"id\")'/>">
				<input type="checkbox" name="id" id="${member.v3xOrgMember.id}" value="${member.v3xOrgMember.id}" isInternal="${member.v3xOrgMember.isInternal}">
			</v3x:column>
			<c:set var="status" value=""/>
			<c:if test="${member.v3xOrgMember.status == 2}"><c:set var="status" value="(${orgDisabled})"/></c:if>
			<c:if test="${member.v3xOrgMember.status == 3}"><c:set var="status" value="(${orgDeleted})"/></c:if>	
			<c:if test="${member.v3xOrgMember.status == 4}"><c:set var="status" value="(${orgTransfered})"/></c:if>	
			<c:if test="${member.stateName==''||member.stateName==null}"><c:set var="showALT" value="${member.v3xOrgMember.loginName}"/></c:if>		
			<c:if test="${member.stateName!=''&&member.stateName!=null}"><c:set var="showALT" value=""/></c:if>
			<v3x:column width="15%" align="left" label="org.member_form.name.label" type="String"
				value="${member.v3xOrgMember.name}${status}" className="cursor-hand sort" 
				alt="${member.v3xOrgMember.name}${status}" onClick="${click}" onDblClick="${dbclick }"/>
			<v3x:column width="10%" align="left" label="org.member_form.loginName.label" type="String"
				className="cursor-hand sort"  alt="${showALT}" onClick="${click}" onDblClick="${dbclick }">
				<c:out value='${member.v3xOrgMember.loginName}' escapeXml='true'/><c:if test="${member.stateName!=''&&member.stateName!=null}">&nbsp;<img style="vertical-align:middle;" src="<c:url value='/common/images/ldapbinding.gif' />" title="<fmt:message key='ldap.user.prompt' bundle='${ldaplocale}'><fmt:param value='${member.stateName}'></fmt:param></fmt:message>"/></c:if>
			</v3x:column>
			<!-- branches_a8_v350_r_gov GOV-1097 lijl Add-->
			<v3x:column width="10%" align="left" label="org.member_form.code${v3x:suffix()}" type="String" 
			    className="cursor-hand sort" alt="${member.v3xOrgMember.code}" onClick="${click}" onDblClick="${dbclick }">
			    <c:out value='${member.v3xOrgMember.code}' escapeXml='true'/>
			</v3x:column>	
			<v3x:column width="10%" align="left" label="密级" type="String" 
			    className="cursor-hand sort" alt="${member.v3xOrgMember.secretLevel}" onClick="${click}" onDblClick="${dbclick }">
			    <c:if test="${member.v3xOrgMember.secretLevel == 1}">
			    <c:out value='内部' escapeXml='true'/>
			    </c:if>
			    <c:if test="${member.v3xOrgMember.secretLevel == 2}">
			    <c:out value='秘密' escapeXml='true'/>
			    </c:if>
			    <c:if test="${member.v3xOrgMember.secretLevel == 3}">
			    <c:out value='机密' escapeXml='true'/>
			    </c:if>
			</v3x:column>
			<v3x:column width="10%" align="center" label="common.sort.label" type="Number"
				alt="${member.v3xOrgMember.sortId}" value="${member.v3xOrgMember.sortId}" className="cursor-hand sort" onClick="${click}" onDblClick="${dbclick }"/>
			<v3x:column width="10%" align="left" label="org.member_form.deptName.label" type="String"
				value="${member.departmentName}" className="cursor-hand sort" 
				alt="${member.departmentName}" onClick="${click}" onDblClick="${dbclick }"/>
			<v3x:column width="10%" align="left" label="org.member_form.primaryPost.label" type="String"
				className="cursor-hand sort" maxLength="55" symbol="..." alt="${member.postName}" onClick="${click}" onDblClick="${dbclick }">
				<c:choose>
				<c:when test="${member.v3xOrgMember.orgPostId != -1}">
					<c:out value='${member.postName}' escapeXml='true'/>
				</c:when>
				<c:otherwise>
					<font color="red"><fmt:message key="org.member.noPost"/></font>						
				</c:otherwise>
				</c:choose>
			</v3x:column>	
			<v3x:column width="10%" align="left" label="org.member_form.levelName.label${v3x:suffix()}" type="String"
				className="cursor-hand sort" maxLength="13"  symbol="..." alt="${member.levelName}" onClick="${click}" onDblClick="${dbclick }">
				<c:choose>
				<c:when test="${member.v3xOrgMember.orgLevelId != -1}">
					<c:out value='${member.levelName}' escapeXml='true'/>
				</c:when>
				<c:otherwise>
					<font color="red"><fmt:message key="org.member.noPost"/></font>						
				</c:otherwise>
				</c:choose>
			</v3x:column>
			<v3x:column width="10%" align="left" maxLength="10"  symbol="..."  label="org.metadata.member_type.label" type="String"
				className="cursor-hand sort" onClick="${click}" onDblClick="${dbclick }">
				<v3x:metadataItemLabel metadata="${orgMeta['org_property_member_type']}" value="${member.v3xOrgMember.type}" />	
			</v3x:column>
			<v3x:column width="10%" align="left" label="org.metadata.member_state.label" type="String"
				className="cursor-hand sort" onClick="${click}" onDblClick="${dbclick }">
				<v3x:metadataItemLabel metadata="${orgMeta['org_property_member_state']}" value="${member.v3xOrgMember.state}"/>
			</v3x:column>
		</v3x:table>
	</form>
	</div></div></div>
<script type="text/javascript">
try{
	var io=null;
	var ok=canIO(io);
	if('doing'!=ok){
	var isShow = parent.detailFrame.showOrgDetail;
	if(typeof(isShow) == "undefined"||isShow||isShow == 'true'){
		var key = "${v3x:currentUser().administrator}" == "true" ? "organizationLang.detail_info_1405" : "organizationLang.detail_info_1405_hradmin";
		showDetailPageBaseInfo("detailFrame", "<fmt:message key='hr.organization.member.label'  bundle='${v3xHRI18N}'/> ", [3,2], pageQueryMap.get('count'), v3x.getMessage(key));
	}}
}catch(e){}
</script>
<iframe width="0" height="0" name="exportIFrame" id="exportIFrame"></iframe>
</body>
</html>