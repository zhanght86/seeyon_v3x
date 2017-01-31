<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/taglib.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:message key="${param['_key']}" var="_myLabel"/>
<fmt:message key="label.please.select" var="_myLabelDefault">
	<fmt:param value="${_myLabel}" />
</fmt:message>
<c:if test="${param['_id_property'] == 'tranMemberIds2'}">
<input type="hidden" id="${param['_id_property']}" name="${param['_id_property']}" value="${bean['tranMemberIds']}"/>
</c:if>
<c:if test="${param['_id_property'] != 'tranMemberIds2'}">
<input type="hidden" id="${param['_id_property']}" name="${param['_id_property']}" value="${bean[param['_id_property']]}"/>
</c:if>
<c:if test="${param['_id_property'] == 'tranMemberIds2'}">
<input type="text" class="cursor-hand input-100per" id="shareTarget2" name="shareTarget2" readonly="true" ${param['_isDisabledInput']}
	value="<c:out value="${bean['shareTarget']}" default="${_myLabelDefault}" escapeXml="true" />"
	title="${v3x:toHTML(bean['shareTarget'])}"
	defaultValue="${_myLabelDefault}"
	onfocus="checkDefSubject(this, true)"
	onblur="checkDefSubject(this, false)"
	inputName="${_myLabel}" 
	validate="${param['_validate']}"
	<c:if test="${(param['_id_property']=='tranMemberIds2')}">
		<c:if test="${(param['_onlyLook']==''|| param['_onlyLook']==null) && !param['_clickModify']}">
			onclick="selectPeople('${param['_type']}','${param['_id_property']}','${param['_name_property']}','${param.secretLevel}')";
		</c:if>
	</c:if>
/>
</c:if>
<c:if test="${param['_id_property'] != 'tranMemberIds2'}">
<input type="text" class="cursor-hand input-100per" id="${param['_name_property']}" name="${param['_name_property']}" readonly="true" ${param['_isDisabledInput']}
	value="<c:out value="${bean[param['_name_property']]}" default="${_myLabelDefault}" escapeXml="true" />"
	title="${v3x:toHTML(bean[param['_name_property']])}"
	defaultValue="${_myLabelDefault}"
	onfocus="checkDefSubject(this, true)"
	onblur="checkDefSubject(this, false)"
	inputName="${_myLabel}"
	validate="${param['_validate']}"
	<c:if test="${(param['_id_property']=='tranMemberIds')}">
		<c:if test="${(param['_onlyLook']==''|| param['_onlyLook']==null) && !param['_clickModify']}">
			onclick="selectPeople('${param['_type']}','${param['_id_property']}','${param['_name_property']}','${param.secretLevel}')";
		</c:if>
	</c:if>
	<c:if test="${(param['_id_property']=='receiveMemberId')}">
		<c:if test="${(param['_onlyLook']==''|| param['_onlyLook']==null)&&(param['_isDisabled']==null||param['_isDisabled']=='') && !param['_clickModify']}">
			onclick="selectPeople('${param['_type']}','${param['_id_property']}','${param['_name_property']}','${param.secretLevel}')";
		</c:if>
	</c:if>
	/>
</c:if>


<c:set var="maxSize" value="${param['_maxSize']}" />
<c:if test="${maxSize==null}">
	<c:set var="maxSize" value="" />
</c:if>

<c:set var="minSize" value="${param['_minSize']}" />
<c:if test="${minSize==null}">
	<c:set var="minSize" value="1" />
</c:if>
<c:set var="selectType" value="${param['_selectType']}" />

<c:if test="${selectType==null}">
	<c:set var="selectType" value="Member" />
</c:if>
<c:if test="${(bean.shareType==2|| bean.shareType==5) && param['_id_property'] != 'receiveMemberId'}">
	<c:set value="${v3x:parseElementsOfTypeAndId(bean.tranMemberIds)}" var="teamMems" />
</c:if>
<c:if test="${param['_id_property'] == 'receiveMemberId'}">
	<c:set value="${v3x:parseElementsOfTypeAndId(bean.receiveMemberId)}" var="teamMems" />
</c:if>
<c:set var="panels" value="${param['_panels']}" />
<c:if test="${panels==null}">
	<c:set var="panels" value="Department,Team,Post,Outworker,RelatePeople" />
</c:if>

<v3x:selectPeople id="${param['_type']}" 
		showMe="false" panels="${panels}" originalElements="${teamMems }"
		departmentId="${sessionScope['com.seeyon.current_user'].departmentId}" 
		maxSize="${maxSize}" selectType="${selectType}" minSize="${minSize}" 
		jsFunction="setCalPeopleFields(elements,'${param['_id_property']}','${param['_name_property']}')" targetWindow="window.parent"/>