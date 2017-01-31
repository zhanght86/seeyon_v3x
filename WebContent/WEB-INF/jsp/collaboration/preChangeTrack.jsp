<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../common/INC/noCache.jsp"%>
<%@ include file="Collaborationheader.jsp"%>
<title><fmt:message key="common.track.setting" bundle="${v3xCommonI18N}" /></title>

<script type="text/javascript">
var _parent = window.opener;

if(window.dialogArguments){
	_parent = window.dialogArguments;
}
var flowSecretLevel_track = "${secretLevel}";
var from = "${param.from}";//从什么地方进来的，列表/协同详细页面/...
function ok(){
	var form = document.getElementById("trackForm");
	var track = true;
	var trackMode = '1';
    if(document.getElementById("track_mode").value == '0'){
		trackMode = '0';
		track = false;
	}
	var trackLable = v3x.getMessage("collaborationLang.track_" + track);
	var obj = _parent.document.getElementById("track${param.affairId}");
	if(obj){
		if(from == 'collaborationTopic'){	//从协同详细页面点进来的切换图片
			if( trackMode == '1'){//跟踪
				obj.src = "<c:url value='/apps_res/collaboration/images/workflowDealDetail.gif' />";
				obj.title="<fmt:message key='track.no.label'/>";
			}else{
				obj.src = "<c:url value='/apps_res/collaboration/images/workflowDealDetail.gif' />"
				obj.title="<fmt:message key='track.label'/>";
			}
		}else {
			obj.innerText = trackLable;
		}
	}
	var isTrackPartMember ="";
	if(trackMode == '1' && document.getElementById("trackRange_part").checked == true){
		isTrackPartMember  = "&trackMembers=" +document.getElementById("trackMembers").value;
	}
	form.action = genericURL+"?method=changeTrack&trackMode="+trackMode+isTrackPartMember;
    form.submit();
    window.close();
}
function show(){
	var track_mode = document.getElementById("track_mode").value;
	if(track_mode == '1'){
		document.getElementById("trackRange").style.display="";	
	}else{
		document.getElementById("trackRange").style.display="none";	
	}
}
function setPeople(elements){
	var memeberIds = "";
	if(elements){
		for(var i= 0 ;i<elements.length ; i++){
			if(memeberIds ==""){
				memeberIds = elements[i].id;
			}else{
				memeberIds +=","+elements[i].id;
			}
		}
		document.getElementById("trackMembers").value = memeberIds;
	}
	
}
</script>
</head>

<body scroll="no" onkeypress="listenerKeyESC()">
<c:set value="${v3x:parseElementsOfIds(trackIds, 'Member')}" var="mids"/>
<v3x:selectPeople id="track" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Member" jsFunction="setPeople(elements)" originalElements="${mids}"/>	

<form id="trackForm" name="trackForm" action="" target="trackIframe" method="post" >
<input type="hidden" value="" name="trackMembers" id="trackMembers">
<input type="hidden" name="affairId" id="affairId" value="${param.affairId}">
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="30%" class="PopupTitle"><fmt:message key="common.track.setting" bundle="${v3xCommonI18N}" /></td>
	</tr>
	<tr>
		<td class="bg-advance-middel">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		  	<tr>
				<td height="40%" colspan="2" align="center">
					<fmt:message key="track.label"/>
					<select id="track_mode" name="track_mode" style='width:120px' onchange="show()" ${isWorkFlowFinished ? "disabled" : ""}>
						<option value="1" ${isTrack? 'selected':''}> <fmt:message key="common.yes" bundle="${v3xCommonI18N}" /></option>
						<option value="0" ${!isTrack? 'selected':''}> <fmt:message key="common.no" bundle="${v3xCommonI18N}" /> </option>
					</select>
					
				</td>
			</tr>
			<tr id="trackRange" style="display: ${isTrack? '':'none'}">
				<td height="40%" colspan="2" align="center">
					<label for="trackRange_all">
						<input type="radio" name="trackRange" id="trackRange_all" value="1" ${empty trackIds?'checked':''}
						${isWorkFlowFinished ? "disabled" : ""} />
						<fmt:message key="col.track.all" bundle="${v3xCommonI18N}" />
					</label>
					<label for="trackRange_part">
						<input type="radio" name="trackRange"  id="trackRange_part" onclick="selectPeopleFun_track()" value="0" ${not empty trackIds?'checked':''}
						${isWorkFlowFinished ? "disabled" : ""} />
						<fmt:message key="col.track.part" bundle="${v3xCommonI18N}" />
					</label>
					<%--被选中并且流程结束 --%>
					<c:if test="${not empty trackIds and isWorkFlowFinished}">
						<span class="link-blue" onclick="selectPeopleFun_track()">
							<fmt:message key="col.track.part.see" bundle="${v3xCommonI18N}" />
						</span>
					</c:if>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td height="30%" align="center" class="bg-advance-bottom">
			<c:if test="${!isWorkFlowFinished}">
				<input type="button" name="b1" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2"/>&nbsp;
			</c:if>
			<input type="button" name="b2" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
</table>

</form>
<iframe src="" name="trackIframe" id="trackIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
</body>
</html>