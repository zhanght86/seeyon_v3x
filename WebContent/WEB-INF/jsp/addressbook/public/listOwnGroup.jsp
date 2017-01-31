<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ include file="../header.jsp"%>
<fmt:setBundle basename="com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<script type="text/javascript">
	getA8Top().showLocation(1006,"<fmt:message key='addressbook.team.personal.label'  bundle='${v3xAddressBookI18N}'/>" );
    showDetailPageBaseInfo("detailFrame", "<fmt:message key='addressbook.team.personal.label'  bundle='${v3xAddressBookI18N}'/>" , "/common/images/detailBannner/110204.gif", ${resultCount}, v3x.getMessage("ADDRESSBOOKLang.detail_addressbook_110204"));
</script>
<script type="text/javascript" src="<c:url value='/apps_res/addressbook/js/public.js${v3x:resSuffix()}'/>"></script>
<link type="text/css" rel="stylesheet" href="<c:url value='/apps_res/doc/css/docMenu.css${v3x:resSuffix()}' />">
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />">
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/addressbook/js/addressbook.js${v3x:resSuffix()}"/>"></script>
<script type="text/javascript">
//	var rightMenu = new RightMenu("${pageContext.request.contextPath}");
//	rightMenu.AddItem("sendMessage","<fmt:message key='message.sendDialog.title' bundle='${v3xMainI18N}' />","<c:url value="/apps_res/v3xmain/images/online.gif"/>","rbpm","sendMessage","sendMessage");
//	if(getA8Top().contentFrame.topFrame.isCanSendSMS == "true"){
//		rightMenu.AddItem("sendSMS", "<fmt:message key='top.alt.sendMobileMsg' bundle='${v3xMainI18N}' />", "<c:url value='/common/images/top/mobile.gif'/>","rbpm",  "sendSMS", "sendSMS");	
//	}
	<c:if test="${v3x:hasNewMail()}">
		rightMenu.AddItem("sendMail", "<fmt:message key='addressbook.email.label' />", "<c:url value='/common/images/left/icon/fayoujian.gif'/>","rbpm",  "sendSMS", "sendSMS");	
	</c:if>
	document.writeln(rightMenu.GetMenu());
</script>
</head>
<body scroll="no" onresize="resizeRightBody(500,'treeandlist','60%')">
<div class="main_div_row2" id="listMember_div">
  <div class="right_div_row2">
    <div class="top_div_row2"> <%@ include file="../toolbar.jsp"%> </div>
    <div class="center_div_row2" id="scrollListDiv" style="overflow: hidden;">
       <input type="hidden" id="resultCount" value="${resultCount}" />
			<form id="memberform" method="post">
				<v3x:table data="${members}" var="member" leastSize="0" htmlId="memberlist" isChangeTRColor="false">
				<v3x:column width="5%" align="center" label="<input type='checkbox' onclick='selectAll(this, \"id\")'/>">
					<input type='checkbox' name='id' value="<c:out value="${member.v3xOrgMember.id}"/>" userName="<c:out value="${member.v3xOrgMember.name}" />"/>
				</v3x:column>
				
				<c:set var="click" value="viewMember('${member.v3xOrgMember.id}')"/>
 				<c:set value="${v3x:showOrgMemberName(member.v3xOrgMember)}" var="memberName" />	
 				<c:choose>
					<c:when test="${v3x:currentUser().id != member.v3xOrgMember.id}">
						<c:set var="onmouseover" value="showEditImage('${member.v3xOrgMember.id }')"/>
						<c:set var="onmouseout" value="removeEditImage('${member.v3xOrgMember.id }')"/>
					</c:when>
					<c:otherwise>
						<c:set var="onmouseover" value=""/>
						<c:set var="onmouseout" value=""/>
					</c:otherwise>
				</c:choose>		
				<v3x:column  width="15%" label="addressbook.username.label"  onmouseover="${onmouseover }" onmouseout="${onmouseout }" className="sort"   type="string">
					<a class="defaulttitlecss div-float" title="${v3x:toHTML(memberName)}" href="javascript:${click}">${v3x:toHTML(memberName)}</a>
					<div class="div-float-right" id="edit${member.v3xOrgMember.id }" onclick="OnMouseUp(new AddressBook('${member.v3xOrgMember.id}','${member.v3xOrgMember.telNumber }','${member.v3xOrgMember.emailAddress }','${v3x:toHTML(member.v3xOrgMember.name)}'))" title="<fmt:message key='addressbook.done.label' />" ></div>
				</v3x:column>
					
				<v3x:column  width="15%" label="addressbook.company.department.label" type="string"
				value="${v3x:showDepartmentFullPath(member.v3xOrgMember.orgDepartmentId)}" className="cursor-hand sort"  alt="${v3x:showDepartmentFullPath(member.v3xOrgMember.orgDepartmentId)}"></v3x:column>
				 <v3x:column  width="15%" label="addressbook.company.post.label" type="string"
					value="${member.postName}" className="cursor-hand sort" alt="${member.postName}"></v3x:column>
				<c:if test="${isEnableLevel=='true'}">
				<v3x:column  width="15%"  label="addressbook.company.level.label${v3x:suffix()}" type="string"
					value="${member.levelName}" className="cursor-hand sort"  alt="${member.levelName}"
					></v3x:column>
				</c:if>
				<v3x:column  width="20%"  label="addressbook.company.telephone.label" type="string"
					value="${member.familyPhone}" className="sort"  alt="${member.familyPhone}"
					></v3x:column>	
				<v3x:column  width="15%" label="addressbook.mobilephone.label" type="string"
				value="${member.v3xOrgMember.telNumber }" className="cursor-hand sort"  alt="${member.v3xOrgMember.telNumber }">
				</v3x:column>

			</v3x:table>
			</form>
			<iframe id="theLogIframe" name="theLogIframe" frameborder="0" marginheight="0" marginwidth="0" ></iframe>
    </div>
  </div>
</div>
<script>
initIpadScroll("listMember_div",520,700);
</script>
</body>
</html>

