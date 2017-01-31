<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<%@include file="../header.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="message.sendDialog.title"/></title>
<script type="text/javascript">
<!--
	//服务器时间和本地时间的差异
	var server2LocalTime = <%=System.currentTimeMillis()%> - new Date().getTime();

	//通讯录等选中人员发消息
	function checkGetData(getData){
		if(getData == "fromParent"){
			var parentWin = v3x.getParentWindow();
			var ids = parentWin.receiveIds;
			var names = parentWin.receiveNames;
			if(ids){
				$("#receiverIds").val(ids);
				$("#sendTo").val(names);
				$("#selectRecHref").hide();
			}
		}
	}

	function showReceiver(elements){
		if(!elements){
			return;
		}
		$("#receiverIds").val(getIdsString(elements, false));	
		$("#sendTo").val(getNamesString(elements));
		$("#sendTo").attr("title", getNamesString(elements));
	}

	function submitForm(){
		if($("#sendTo").val() == ""){
			alert(_("MainLang.onlineMsg_send_alert_noReceiver"));
			selectPeopleFun_addReceiver();
			return;
		}
		
		var thisContentText = $("#content").val();
		thisContentText = thisContentText.trim();
		if(thisContentText == "" || thisContentText.length == 0){
			if(!isUploadAttachment()){
				return;
			}
		}else if(thisContentText.length > 140){
			alert(_("MainLang.message_content_length_too_long", 140));
			return;
		}

		$("#b1").attr("disabled", true);
		$("#b2").attr("disabled", true);
		
		var tIDs = $("#receiverIds").val();
		thisContentText = thisContentText.escapeHTML();
		
		//如果群发, 不带附件
		<c:if test="${receiverNum == 1}">
			saveAttachment();
			if(!fileUploadAttachments.isEmpty()){
				if(thisContentText != "" && thisContentText.length > 0){
					thisContentText += "<br/>";
				}

				thisContentText += _("MainLang.message_send_file");
				
				var theList = fileUploadAttachments.keys();
				for(var i = 0; i < theList.size(); i ++){
					var attach = fileUploadAttachments.get(theList.get(i), null);
					var _str = "&nbsp;&quot;&nbsp;<img src='" + v3x.baseURL + "/common/images/attachmentICON/" + attach.icon + "' border='0' height='16' width='16' align='absmiddle' style='margin-right: 3px;'>" + 
							   "&nbsp;" + attach.filename.escapeHTML() + "&nbsp;&quot;&nbsp;";
					if(attach.size && attach.type == 0){
						_str += "(" + (parseInt(attach.size / 1024) + 1) + "KB)";
					}
					_str += "。";
					var _url = "<a href=\"" + v3x.baseURL + "/fileUpload.do?method=download&fileId=" + attach.fileUrl + "&createDate=" + attach.createDate.substring(0, 10) + 
					   		   "&filename=" + encodeURIComponent(attach.filename) + "\" " + "target='downloadFileFrame' style='font-size:12px'>";
	
			   		thisContentText += _str + _url + _("MainLang.message_file_download") + "</a>&nbsp;&nbsp;&nbsp;";
				}
			}
		</c:if>

		var currentDate = new Date(new Date().getTime() + server2LocalTime);
		var showDate = currentDate.format("HH:mm:ss");
		var saveDate = currentDate.format("yyyy-MM-dd HH:mm:ss");
		
		$.ajax({
			async: false,
			type: "POST",
			url: "/seeyon/getAJAXMessageLongPollingServlet",
			data: {"callType": "sendMessage", "messageType": "1", "referenceId": "-1", "receiverIds": tIDs, "content": thisContentText, "creationDate": saveDate, "showDate": showDate},
			success: function(result){
			    window.close();
			}
		});
	}

	function doKeyPressedEvent(sendForm){
		 var event = v3x.getEvent();
		 if(event.ctrlKey && event.keyCode == 13){
			 submitForm();
		 }else if(event.keyCode == 27){
			window.close();
		 }
	}

	$(document).ready(function(){
		$('#content').focus();
		checkGetData('${param.getData}');
	});
//-->
</script>
</head>
<v3x:selectPeople id="addReceiver" panels="Department,Team,Outworker" showMe="false" selectType="Member" departmentId="${currentUser.departmentId}" jsFunction="showReceiver(elements)"/>
<body scroll="no" style="overflow: hidden;" onkeydown="doKeyPressedEvent()">
<form name="sendForm" id="sendForm" method="post" action="">
<input type="hidden" id="receiverIds" name="receiverIds" value="${receiverIds}"/>
<table class="popupTitleRight" border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
	<tr>
		<td height="20" class="PopupTitle">
			<font color="#000"><fmt:message key="message.sendTo.label"/></font>
			<input type="text" id="sendTo" name="sendTo" class="textfield" style="width:50%;border-style:none;" readonly title="${v3x:toHTML(v3x:showOrgEntitiesOfIds(receiverIds, 'Member', pageContext))}" value="${v3x:toHTML(v3x:showOrgEntitiesOfIds(receiverIds, 'Member', pageContext))}">
			<c:if test="${receiverNum == 0}">
				<a id="selectRecHref" href="#" onclick="selectPeopleFun_addReceiver()"><fmt:message key="message.addReceiver.label"/></a>
			</c:if>
		</td>
	</tr>
	<tr>
		<td class="bg-advance-middel" valign="top">
		<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="2">
					<textarea name="content" id="content" cols="" rows="10" style="width:100%;"></textarea>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<c:if test="${v3x:getBrowserFlagByRequest('OnDbClick', pageContext.request)}">
		<tr>
			<td height="35" align="right" class="bg-advance-bottom">
			    <input type="button" id="b1" name="b1" class="button-default-2" value="<fmt:message key='common.toolbar.send.label' bundle='${v3xCommonI18N}'/>" onclick="submitForm()"/>&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="button" id="b2" name="b2" class="button-default-2" value="<fmt:message key='common.button.cancel.label' bundle='${v3xCommonI18N}'/>" onclick="window.close()"/>
			</td>
		</tr>
	</c:if>
</table>
</form>
</body>
</html>