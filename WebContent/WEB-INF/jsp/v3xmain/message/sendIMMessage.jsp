<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="header.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
<!--
	/**
	 * 历史消息
	 */
	function showHistoryMessage(type){
		//IE6下使部门列表中单位下拉框不可用
		getA8Top().$('#onlineUserTreeIframe').contents().find('#currentAccountId').attr('disabled', true);
		var dID = getUUID();
		var divObj = "<div id=\"" + dID + "_DIV\" closed=\"true\">" +
					 "<iframe id=\"" + dID + "_Iframe\" name=\"" + dID + "_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		getA8Top().$(divObj).appendTo("body");
		getA8Top().$('#' + dID + "_DIV").dialog({
			title: "<fmt:message key='message.record'/>",
			left: 200,
			width: 650,
			height: 450,
			closed: false,
			modal: true,
			draggable: false,
			closable: false,
			tools:[{
						iconCls:'panel-tool-close',
						handler:function(){
							getA8Top().$('#onlineUserTreeIframe').contents().find('#currentAccountId').attr('disabled', false);
							getA8Top().$('#' + dID + "_DIV").dialog('destroy');
						}
					}]
		});
		getA8Top().$('#' + dID + "_Iframe").attr("src","${messageURL}?method=showThisHistoryMessage&type=${param.type}&id=${param.id}" + "&createDate=" + new Date().format("yyyy-MM-dd") + "&random=" + getUUID());
	}

	/**
	 * 文本设置
	 */
	function showTextDiv(){
		$("#textDiv").toggle();
	}

	/**
	 * 字体大小
	 */
	function setFontSize(){
		$("#editContent").css("font-size", $("#fontSize").attr("value"));
	}

	/**
	 * 显示表情
	 */
	function showFasesDiv(){
		$("#fasesDiv").show();
	}

	/**
	 * 隐藏表情
	 */
	function hiddenFasesDiv(ev){
	 	$("#fasesDiv").hide();
	}

	/**
	 * 选择表情
	 */
	function selectFace(faceText){
		$("#fasesDiv").hide();
		$("#editContent").attr("value", $("#editContent").attr("value") + faceText);
		$("#editContent").focus();
	}

	/**
	 * 清屏
	 */
	function clearScreen(){
		$("#sendContent").html("");
	}
	
	/**
	 * 附件
	 */
	function selectFile(){
		fileUploadAttachments.clear();
		insertAttachment();
		if(!fileUploadAttachments.isEmpty()){
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
				   		   "&filename=" + encodeURIComponent(attach.filename) + "\" " + "target='downloadFileFrame' style='font-size:12px'>"
				   		getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}', _str, _url,"file");
			}
		}
	}

	function initSendContentHeight(){
		var oHeight = parseInt(document.body.clientHeight) - 145;
		document.getElementById('sendContent').style.height = oHeight + "px";
	}

	function addResize(){
		if(document.all){
	        window.attachEvent("onresize", initSendContentHeight);
	        window.attachEvent("onfocus", initSendContentHeight);
	    }else{
	    	window.addEventListener("resize", initSendContentHeight, false);
	    	window.addEventListener("focus", initSendContentHeight, false);
		}
		initSendContentHeight();
	}
	
	//发起视频会议事件
	function startvideoconf(){
		if($("#videoconf").attr("name")=="clicked"){
			return;
		}
		$("#videoconf").attr("name","clicked");
		//如果已经发起过邀请，让中止按钮事件失效
		if($("#lanchmeeting").val()=="true"){
			parent.changedIDattr('${param.dID}',"stopmeeting");
			$("#confKey").val("");
			$("#iscreater").val("");
		}
		
		//如果有其他人发起会议邀请，禁用
		if($("#resavemeeting").val()=="true"){
			//改变按钮属性
			stopmeeting('');
			//改变邀请标识值
			$("#resavemeeting").val("");
		}
		
		$("#createmeeting").show();
		
		$.ajax({
			url:"/seeyon/message.do?method=createMeeting&uuid="+getUUID(),
			data: {"messageType": "${param.type}", "referenceId": "${param.dID}", "receiverIds": "${param.id}"},
			success:function(data){
				var a = eval(data);
				$("#videoconf").attr("name","click");
				$("#createmeeting").hide();
				if(a.success=="true"){
					$("#cconfKey").val(a.confKey);
					$("#iscreater").val("true");
					$("#tnum").val(a.tnum);
					$("#rnum").val("0");
					$("#lanchmeetinged").val("");
					$("#lanchmeeting").val("true");
					getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'S', a.confKey,"vomeeting");
				}else{
					alert(_(a.confKey));
				}
			},
			error:function(data){
				$("#videoconf").attr("name","click");
				$("#createmeeting").hide();
				alert('<fmt:message key="message.meeting.createerror" bundle="${wim}"/>');
			}
		});
	}
	
	function stopmeeting(type){
		if(type=='C'){
			$("#lanchmeeting").val("");
			parent.deletemeeting($("#cconfKey").val());
			getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'C', "","vomeeting");
		}else if(type=='A'){
			$("#resavemeeting").val("");
			getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'A', "","vomeeting");
		}else if(type=='F'){
			$("#resavemeeting").val("");
			getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'F', "","vomeeting");
		}
		
		parent.changeattr('${param.dID}');
	}
	
	function havemetting(){
			$("#vomeeting").attr("id","vomeetinged");
			parent.changedIDattr('${param.dID}',"agreemeeting");
			parent.changedIDattr('${param.dID}',"formeeting");
	}
	function addmeeting(){
		parent.addMeetingEvent('${param.type}', '${param.dID}', '${param.id}');
		if("${param.isFromMessage}" == "true"){
			$("#resavemeeting").val("true");
		}else{
			$("#lanchmeeting").val("true");
		}
	}

	$(document).ready(function(){
		try{
			addResize();
		}catch(e){}
		$("#bold").toggle(
			function(){
				$(this).addClass("border-ccc");
				$("#editContent").addClass("text-bold");
			},
			function(){
				$(this).removeClass("border-ccc");
				$("#editContent").removeClass("text-bold");
			}
		);
		$("#italic").toggle(
			function(){
				$(this).addClass("border-ccc");
				$("#editContent").addClass("text-italic");
			},
			function(){
				$(this).removeClass("border-ccc");
				$("#editContent").removeClass("text-italic");
			}
		);
		$("#underline").toggle(
			function(){
				$(this).addClass("border-ccc");
				$("#editContent").addClass("text-decoration");
			},
			function(){
				$(this).removeClass("border-ccc");
				$("#editContent").removeClass("text-decoration");
			}
		);
	});
	
	$(window).unload(function(){
		if($("#resavemeeting").val()=="true"){
			parent.sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'F', "","vomeeting");
		}
		if($("#lanchmeeting").val()=="true"){
			parent.sendIMMessage('${param.type}', '${param.dID}', '${param.id}', 'C', "","vomeeting");
		}
	});
	
	var vomak = '';
//-->
</script>
</head>
<body  onclick="getA8Top().standardTitleFun()">
	<form name="meeting">
		<input type="hidden" id="lanchmeeting" value="" name="保存是否发起会议邀请"/>
		<input type="hidden" id="resavemeeting" value="" name="保存是否收到会议邀请"/>
		<input type="hidden" id="lanchmeetinged" value="" name="保存是否开启会议"/>
		<input type="hidden" id="cconfKey" value="" name="保存会议编号"/>
		<input type="hidden" id="iscreater" value="" name="保存是否是创建者"/>
		<input type="hidden" id="rnum" value="" name="保存拒绝的人数"/>
		<input type="hidden" id="tnum" value="" name="保存总人数"/>
	</form>
	<table width="100%" height="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td id="currentMessage" width="${param.type == '1' ? '100%' : '78%'}" style="position: relative;" valign="top">
				<div onclick="hiddenFasesDiv()" id="sendContent" style="width: 100%; height: 80%; vertical-align: top; overflow-y: auto;">
		        	<script type="text/javascript">
		        		if("${param.isFromMessage}" == "true"){
		        			var receiveContent = parent.IMMsgProperties.get("${param.id}");
		    	        	if(receiveContent){
		    	        		for(var i = 0; i < receiveContent.size(); i ++){
		    			        	var result = "<div style='padding: 5px 10px;'>" + 
		    			        		"<span style='color: #335186;'>" + receiveContent.get(i).senderName.escapeHTML() + "</span>&nbsp;&nbsp;" + 
		    			        		"<font class='col-reply-date'>" + receiveContent.get(i).creationDateTime + "</font>" + 
		    			        		"</div><div style='padding: 5px 30px; word-wrap:break-word;word-break:break-all;'>" + receiveContent.get(i).content + "</div>";
		    			        	document.write(result);
		    		        	}
		    	        		parent.IMMsgProperties.remove("${param.id}");
		    	        	}
		            	}
		        	</script>
		        </div>
		        <div id="createmeeting" style="display: none; width: 100%; height: 25px; line-height: 25px; vertical-align: middle; background: #ECF5FF; position: absolute; bottom: 149px;z-index: 100;"><fmt:message key="message.meeting.createmessage" bundle="${wim}"/></div>
		        <div id="textDiv" style="display: none; width: 100%; height: 25px; line-height: 25px; vertical-align: middle; background: #ECF5FF; position: absolute; bottom: 149px;">
		        	<table cellpadding="0" cellspacing="0" border="0">
			        	<tr>
				        	<td valign="middle">
					        	<select id="fontSize" onchange="setFontSize()" style="width: 50px;" class="margin-left-10">
					        		<option value="12">12</option>
					        		<option value="14">14</option>
					        		<option value="16">16</option>
					        		<option value="18">18</option>
					        		<option value="20">20</option>
					        		<option value="22">22</option>
					        	</select>
				        	</td>
				        	<td valign="middle">
			    				<img id="bold" src="/seeyon/apps_res/v3xmain/images/message/20/bold.gif" class="cursor-hand margin-left-10"/>
			    				<img id="italic" src="/seeyon/apps_res/v3xmain/images/message/20/italic.gif" class="cursor-hand margin-left-10"/>
			    				<img id="underline" src="/seeyon/apps_res/v3xmain/images/message/20/underline.gif" class="cursor-hand margin-left-10"/>
		    				</td>
	    				</tr>
    				</table>
		        </div>
		        <div class="facesDiv" id="fasesDiv">
		        	<script type="text/javascript">
		        	<!--
						for(var i = 0; i < getA8Top().face_texts.length; i ++){
							document.write("<img src='/seeyon/common/RTE/editor/images/smiley/msn/" + (i + 1) + ".gif' onclick='selectFace(\"" + getA8Top().face_texts[i] + "\");' width='24' height='24'/>");
						}
					//-->
		        	</script>
				</div>
	    		<div style="width: 100%; height: 29px; line-height: 29px; vertical-align: middle; position: absolute; bottom: 120px;" class="message-send-toobar">
	    			<div class="div-float" style="margin-top: 6px;">
	    				<img src="/seeyon/apps_res/v3xmain/images/message/16/text.gif" alt="<fmt:message key='message.text'/>" class="cursor-hand margin-left-10" onclick="showTextDiv()" />
	    				<img src="/seeyon/apps_res/v3xmain/images/message/16/face.gif" alt="<fmt:message key='message.face'/>" class="cursor-hand margin-left-10" onclick="showFasesDiv()" />
	    				<!-- 成发集团项目 程炯 在线消息中不能发送附件 -->
	    				<!--<c:if test="${v3x:getBrowserFlagByRequest('OnDbClick', pageContext.request)}">
	    					<img src="/seeyon/apps_res/v3xmain/images/message/16/attachment.gif" alt="<fmt:message key='message.file'/>" class="cursor-hand margin-left-10" onclick="selectFile()" />
	    				</c:if>
	    				--><c:if test="${param.type != '2' && param.type != '3' && param.type != '4'}">
	    					<c:if test="${param.type == '1'}">
	    						<c:set value="2" var="createType" />
	    					</c:if>
	    					<c:if test="${param.type == '5'}">
	    						<c:set value="3" var="createType" />
	    					</c:if>
		    				<img src="/seeyon/apps_res/v3xmain/images/message/16/add_home.gif" alt="<fmt:message key='message.team.update'/>" class="cursor-hand margin-left-10" onclick="createTeam('${createType}', '${param.id}')" />
	    				</c:if>
	    				<img src="/seeyon/apps_res/v3xmain/images/message/16/delete_page.gif" alt="<fmt:message key='message.clear'/>" class="cursor-hand margin-left-10" onclick="clearScreen()" />
	    				<c:if test="${haveOnline && v3x:hasPlugin('videoconf')}">
		    				<img id = "videoconf" name="click" src="/seeyon/apps_res/v3xmain/images/message/16/videoconf.gif" alt="<fmt:message key='message.send.videomeeting' bundle='${wim}'/>" class="cursor-hand margin-left-10" onclick="startvideoconf()" />
						</c:if>
	    			</div>
					<div class="div-float-right" style="margin-top: 6px;">
						<img src="/seeyon/apps_res/v3xmain/images/message/16/full_page.gif" alt="<fmt:message key='message.record'/>" class="cursor-hand" onclick="showHistoryMessage('${param.type}')" />&nbsp;&nbsp;&nbsp;
					</div>
				</div>
				<div class="hidden">
					<v3x:fileUpload applicationCategory="1"/>
					<script>
						var fileUploadQuantity = 5;
					</script>
				</div>
	        	<textarea onclick="hiddenFasesDiv()" id="editContent" style="width: 100%; height: 95px; border: 0px solid #BCD9E7; position: absolute; bottom: 25px; overflow-y: auto;padding: 5px;" onkeydown="getA8Top().onEnterPressSendMessage(event, '${param.type}', '${param.dID}', '${param.id}')"></textarea>
	        	<div style="width: 100%; height: 28px; line-height: 28px; vertical-align: middle; text-align:right;   position: absolute; bottom: 0px;" class="message-send-bottom">
	        		<input type="button" class="deal_btn" onmouseover="javascript:this.className='deal_btn_over'" onmouseout="javascript:this.className='deal_btn'" value="<fmt:message key='common.toolbar.send.label' bundle="${v3xCommonI18N}" />" onclick="getA8Top().sendIMMessage('${param.type}', '${param.dID}', '${param.id}');" class="">&nbsp;
				</div>
			</td>
			
			<c:if test="${param.type != '1'}">
			<td id="numlist" width="22%" valign="top" class="message-split-left-color">
				<table width="100%" height="100%" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td valign="top">
							<div id="scrollList" class="scrollList" style="height:700px">
								<table id="teamMembersList" width="100%" cellpadding="0" cellspacing="0" border="0">
									<c:forEach items="${onlineMembers}" var="member1">
										<c:choose>
											<c:when test="${member1.internalId != currentUserId}">
												<c:set value="getA8Top().showIMTab('1', '${member1.internalId}', '${v3x:toHTMLWithoutSpaceEscapeQuote(member1.name)}', 'false')" var="onDblclick" />
												<c:set value="selectListRow(this)" var="onClick"/>
												<c:if test="${!v3x:getBrowserFlagByRequest('OnDbClick', pageContext.request)}">
													<c:set value="selectListRow(this);getA8Top().showIMTab('1', '${member1.internalId}', '${v3x:toHTMLWithoutSpaceEscapeQuote(member1.name)}', 'false')" var="onClick"/>	
												</c:if>
											</c:when>
											<c:otherwise>
												<c:set value="" var="onDblclick" />
												<c:set value="selectListRow(this)" var="onClick"/>
											</c:otherwise>
										</c:choose>
										<tr valign="middle" height="25" class="tr-no-select" onclick="${onClick}" ondblclick="${onDblclick}">
											<td width="30" align="center">
												<span class="${member1.loginType == 'pc' ? 'pcOnline' : 'mobileOnline'}"></span>
											</td>
											<td>
												<input type="hidden" id="memberId" name="memberId" value="${member1.internalId}">
												${v3x:toHTMLWithoutSpaceEscapeQuote(member1.name)}
											</td>
										</tr>
									</c:forEach>
									<c:forEach items="${offlineMembers}" var="member2">
										<c:choose>
											<c:when test="${member2.id != currentUserId}">
												<c:set value="getA8Top().showIMTab('1', '${member2.id}', '${v3x:toHTMLWithoutSpaceEscapeQuote(member2.name)}', 'false')" var="onDblclick" />
												<c:if test="${!v3x:getBrowserFlagByRequest('OnDbClick', pageContext.request)}">
													<c:set value="selectListRow(this);getA8Top().showIMTab('1', '${member2.id}', '${v3x:toHTMLWithoutSpaceEscapeQuote(member2.name)}', 'false')" var="onClick"/>	
												</c:if>
											</c:when>
											<c:otherwise>
												<c:set value="" var="onDblclick" />
												<c:set value="selectListRow(this)" var="onClick"/>
											</c:otherwise>
										</c:choose>
										<tr valign="middle" height="25" class="tr-no-select" onclick="${onClick}" ondblclick="${onDblclick}">
											<td width="30" align="center">
												<span class="pcOffline"></span>
											</td>
											<td>
												<input type="hidden" id="memberId" name="memberId" value="${member2.id}">
												${v3x:toHTMLWithoutSpaceEscapeQuote(member2.name)}
											</td>
										</tr>
									</c:forEach>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
			</c:if>
		</tr>
	</table>
</body>
<script type="text/javascript">
try{
	document.getElementById('scrollList').style.height = document.body.clientHeight - 40;
}catch(e){
	
}
</script>
</html>