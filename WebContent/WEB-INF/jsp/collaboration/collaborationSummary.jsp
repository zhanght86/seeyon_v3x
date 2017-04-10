<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.online.resource.i18n.WIMSynchronResources" var="wim"/>
<fmt:setBundle basename="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource"/>
<html:link renderURL="/collaboration.do" var="detailURL" />
<html:link renderURL='/genericController.do' var="genericController" />
<html:link renderURL="/edocController.do" var="edocURL" />
<html:link renderURL="/mtMeeting.do" var="mtMeetingURL" />
<html:link renderURL="/doc.do" var="docURL" />
<fmt:message key="common.datetime.pattern" var="datePattern" bundle="${v3xCommonI18N}"/>
<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' var="printLabel"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/collaboration" prefix="col"%>
<%@ taglib uri="http://v3x.seeyon.com/bridges/spring-portlet-html" prefix="html"%>
<fmt:setBundle basename="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources" var="v3xCommonI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.main.resources.i18n.MainResources" var="v3xMainI18N"/>
<fmt:setBundle basename="com.seeyon.v3x.flowperm.resources.i18n.FlowPermResource" var="permRes"/>
<fmt:setBundle basename="com.seeyon.v3x.edoc.resources.i18n.EdocResource" var="edocRes"/>
<fmt:setBundle basename="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource"/>
<fmt:setBundle basename="com.seeyon.v3x.bulletin.resources.i18n.BulletinResources" var="bulI18N" />
<fmt:setBundle basename="com.seeyon.v3x.taskmanage.resources.i18n.TaskManageResources" var="taskI18N" />
<fmt:setBundle basename="com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources" var="workflowI18N" />
<fmt:message key="common.datetime.pattern" var="datePattern" bundle="${v3xCommonI18N}"/>
<html:link renderURL="/collaboration.do" var="detailURL" />
<html:link renderURL='/templete.do' var="templeteURL" />
<html:link renderURL='/edocSupervise.do' var="supervise" />
<html:link renderURL='/genericController.do' var="genericController" />
<html:link renderURL="/collaboration.do?method=fullEditor" var="fullEditorURL" />
<html:link renderURL="/form.do" var="formURL" />
<html:link renderURL="/edocController.do" var="edocURL" />
<html:link renderURL="/doc.do" var="docURL" />
<html:link renderURL="/mtMeeting.do" var="mtMeetingURL" />
<html:link renderURL='/webmail.do' var='webmailURL' />
<html:link renderURL="/colSupervise.do" var="colSuperviseURL"/>
<html:link renderURL="/workManage.do" var="workManageURL"/>
<html:link renderURL="/processLog.do" var="processLogURL"/>
<html:link renderURL="/exchangeEdoc.do" var="exchangeURL" />
<c:set value="${v3x:currentUser()}" var="currentUser"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/collaboration/css/collaboration.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/default.css${v3x:resSuffix()}" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/apps_res/collaboration/css/collaboration.css${v3x:resSuffix()}" />">
${v3x:skin()}
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script>
parent.supervisorId="${supervisorId}";
parent.orgSupervisorId="${supervisorId}";
parent.supervisors="${supervisors}";
parent.count="${count}";
parent.unCancelledVisor="${unCancelledVisor }";
parent.sVisorsFromTemplate="${sVisorsFromTemplate}";
parent.awakeDate="${awakeDate}";
parent.superviseTitle="${v3x:toHTML(superviseTitle)}";
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;
var officecanPrint="${officecanPrint}";
var officecanSaveLocal="${officecanSaveLocal}";

<%-- finishWorkItem方法是否执行完毕，默认为true, 进入doSign更改为false，执行完毕更改为true --%>
<%-- 将这个JS方法放到前面，否则页面没有加载完就关闭窗口的话，会报JS错。 --%>
var isSubmitFinished = false;
function beforeUnloadCheck(){
	try {
		/** 当修改了内容或者流程的时候如果是(暂存待办、提交、终止、回退)操作时不给出提示，否则当离开此页面时均给出提示给出提示
		 *	contentUpdate 		true	: 修改的正文内容　
		 *	workflowUpdate　  	true	: 修改了流程
		 *  isSubmitOperation	true    : 是提交操作
		 */
		if ((parent.detailMainFrame.contentIframe.contentUpdate || workflowUpdate ) 
		     && (!parent.detailMainFrame.contentIframe.isSubmitOperation))
		{
			return v3x.getMessage("collaborationLang.workflow_content_chanage_prompt");	
		} 
	} catch(e) {
		
	}
}
</script>
</head>
<!-- 2017-3-22 诚佰公司 屏蔽协同附件右键复制地址 -->
<body scroll="no" onbeforeunload="return beforeUnloadCheck()" oncontextmenu=self.event.returnValue=false onselectstart="return false">
<v3x:selectPeople id="relationAuthority" panels="Department,Team,Post,Level,Outworker" selectType="Department,Team,Post,Level,Member,Account" minSize="-1" departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
	 jsFunction="setRelationAuthority(elements,'${summary.id}');"/>
<v3x:attachmentDefine attachments="${attachments}" />
<fmt:formatDate value="${summary.createDate}" pattern="${datePattern}" var="summaryDate"/>
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="CollTable">
	<tr>
		<td height="10" class="detail-summary">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
			
				<tr>
					<td width="90" height="18" nowrap class="bg-gray detail-subject"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" /> : </td>
					<td class="detail-subject-bold">
						<span class='inline-block importance_${summary.importantLevel}'></span>
						<c:if test="${summary.resentTime ne null && summary.resentTime > 0}">
							<fmt:message key="workflow.new.repeat.label">
								<fmt:param value="${summary.resentTime}" />
							</fmt:message>
						</c:if>
						<span id="printsubject">${v3x:toHTML(summary.subject)}</span>
						<input id="subject" type="hidden" value="${summary.subject}">
						${col:showForwardInfo(pageContext, forwardMemberNames)}
						<span id="printSenderInfo" style="display: none">${col:getSenderInfo(summary.startMemberId)} ${summaryDate}</span>
					</td>
				</tr>
				<tr>
					<td height="18" nowrap class="bg-gray detail-subject"><fmt:message key="common.sender.label" bundle="${v3xCommonI18N}" /> : </td>
					<td valign="bottom"><span class="click-link"  onclick="javascript:showV3XMemberCard('${summary.startMemberId}')">${v3x:showMemberName(summary.startMemberId)}</span> (${summaryDate})</td>
				</tr>
				<tr id="attachment2Tr" style="display: none">
					<td height="18" nowrap class="bg-gray detail-subject" valign="bottom"><fmt:message key="common.toolbar.insert.mydocument.label" bundle="${v3xCommonI18N}" /> : </td>
					<td valign="bottom" colspan="3">
						<div class="div-float" id="att2Div">
							<div class="div-float font-12px" style="margin-top: 4px;">(<span id="attachment2NumberDiv" class="font-12px"></span>)</div>
							<span id="attachmentHtml2Span">
							</span>
						</div>
					</td>
				</tr>
				<tr id="attachment1Tr" style="display: none">
					<td height="18" nowrap class="bg-gray detail-subject" valign="bottom"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" /> : </td>
					<td valign="bottom">
						<div class="div-float" id="attDiv">
							<div class="div-float font-12px" style="margin-top: 4px;">(<span id="attachmentNumberDiv" class="font-12px"></span>)</div>
							<span id="attachmentHtml1Span">
							</span>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<c:if test="${!empty param.contentAnchor}">
		<c:set value="#Anchor${param.contentAnchor}" var="Anchor" />
	</c:if>
	
	
	<%--切换操作菜单 行--%>
	<tr>
		<td valign="top">
			<table cellpadding="0" cellspacing="0" width="100%" height="38">
				<tr>
					<td height="38" width="15" class="col_content_toolbar_l">&nbsp;
						
					</td>
					<td class="col_content_toolbar" valign="top">
					    <div style="position:absolute; right:15px; top:91px; width:215px; z-index:200; background-color: #ececec;display:none;overflow:auto;text-align:left;border: 1px #dadada solid; padding: 5px;" id="processAdvanceDIV" onMouseOver="advanceViews(true)" oncontextmenu="return false">
							<input type ="text"  id ="searchText" name ="searchText"  onfocus='checkDefSubject(this, true)'  onblur="checkDefSubject(this, false)" deaultValue="<fmt:message key='opinion.location.input.label'/>" value="<fmt:message key='opinion.location.input.label'/>" />
							<img src="<c:url value='/apps_res/collaboration/images/next.gif' />"   onclick="javascript:contentIframe.doSearch('foward')" class="cursor-hand"/>
							<img src="<c:url value='/apps_res/collaboration/images/previous.gif' />"  onclick="javascript:contentIframe.doSearch('back')" class="cursor-hand"/>
							<img src="<c:url value='/apps_res/collaboration/images/close.gif' />" onClick="javascript:advanceViews(false)" class="cursor-hand"/>
						</div>
						<div id="tempDiv"></div>
						<table cellpadding="0" cellspacing="0" width="100%" border="0" height="38">
							<tr>
							    <td  valign="middle" class="" width="200">
							    	<input id="content_input" class="deal_btn_l_sel" onClick="showPrecessAreaTd('content',this)"  type="button" value="<fmt:message key='common.toolbar.content.label' bundle='${v3xCommonI18N}' />"/>
							    	<input id="workflow_input" class="deal_btn_r" onClick="showPrecessAreaTd('workflow',this)" type="button" value="<fmt:message key='workflow.label'/>"/>
							    </td>
							    <td align="right" style="padding-top:5px;" class="" valign="middle">
								    <c:if test="${('Done' eq param.from or 'Sent' eq param.from or 'Pending' eq param.from) && !isStoreFlag&&!('glwd' eq param.openFrom)}">
										<iframe src="" width="0px" height="0px" name="creatediscuss"></iframe>
										<span onClick="createWebIm()" class="cursor-hand discuss div-float-right" style="margin-left: 10px;" title="<fmt:message key='col.discuss.im'/>"></span>
										<%--branches_a8_v350_r_gov GOV-2772 xiangfan 添加了判断 v3x:hasMenu(2101) 为政务多组织版的会议菜单--%>
										<c:if test="${v3x:hasMenu(603) || v3x:hasMenu(2101)}"><!-- 603表示会议 -->
											<span onClick="createmeeting('${param.affairId}','${param.from}')" title="<fmt:message key='col.book.meeting'/>" class="cursor-hand meeting div-float-right" style="margin-left: 10px;" ></span>
								        </c:if>
								    </c:if>
									<c:if test="${('Sent' eq param.from) and (bodytype eq 'FORM') and showAuthorityButton}">
										<span title="<fmt:message key='common.toolbar.relationAuthority.label' bundle='${v3xCommonI18N}'/>" onClick="selectPeopleForRelationAuthority('${summary.id}')" class="cursor-hand relationAuthorize div-float-right" style="margin-left: 10px;"></span>
									</c:if>
									<span title="<fmt:message key="newflow.viewPropertyState" />" onclick="showAttribute('${param.affairId}', '${param.from}')" class="cursor-hand property_print div-float-right"></span>
									<span title="<fmt:message key="newflow.viewDetailandDaily" />" onclick="contentIframe.showDetailAndLog('${param.summaryId}', '${summary.processId}')"  class="cursor-hand daily_print margin10 div-float-right"></span>
									<c:if test="${param.from eq 'Sent'}" >
										<span title="<fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' />" onClick="contentIframe.showSuperviseWindow('${finished}', '${summary.templeteId}')"  class="cursor-hand supervise_print margin10 div-float-right" style="height: 16px; overflow: hidden;"></span>
									</c:if>
									<%--协同已发已办中增加跟踪/取消跟踪操作 --%>
									<%--督办里面不能设置跟踪，但是不知道为什么督办出来的param.from也是Done,先增加条件！isSupervis来限制，后续进行清理 --%>
									<c:if test="${('Done' eq param.from or 'Sent' eq param.from) and !isSupervis && !isStoreFlag  && param.type != 'doc'}">
							    		<span id="track${affairId}" title="<fmt:message key='track.setting.label' />" onClick="preChangeTrack('${param.affairId}', ${isTrack},'collaborationTopic')" class="cursor-hand trace_print margin10 div-float-right"></span>
						    		</c:if>
									<c:if test="${ officecanPrint && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}" >
										<span title="${printLabel}" onClick="contentIframe.doPrint(${bodytype=='HTML'})" class="cursor-hand coll_print margin10 div-float-right"></span>
									</c:if>
									<span title ="<fmt:message key='opinion.location.label'/>" onClick="javascript:advanceViews(null)" class="cursor-hand coll_search margin10 div-float-right"></span>
									<c:if test="${isSupervis == true && param.from == 'Done' && type != 'doc' && param.isQuote != 'true'}">
								    <span title="<fmt:message key='supervise.label' />" onClick="showSupervise('${summary.id }','${openModal }')"  class="cursor-hand duban_print margin10 div-float-right"></span>
								    </c:if>
									<%-- 查看原文档--%>
								    <c:if test="${(bodytype=='OfficeWord' || bodytype=='OfficeExcel') && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request) && v3x:isOfficeTran()}">
								    	<a href="javascript:contentIframe.popupContentWin()" class="div-float-right"><font class="like-a seeoriginal"><fmt:message key="col.content.viewOriginalContent"/></font></a>
								    </c:if>
							    </td>
							</tr>
						</table>
					</td>

					<td width="15" class="col_content_toolbar_r">&nbsp;
						
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	
	<%--正文流程切换行 --%>
	<tr>
		<td valign="top" height="100%" class="detail-summary detail-summary-padding10">
			<table width="100%" height="100%" id="signAreaTable" border="0" cellspacing="0" cellpadding="0">
				<tr id="closeTR" style="display: none" valign="top">
				    <td>&nbsp;</td>
				</tr>
				<%--流程图 --%>
				<tr id="workflowTR" style="display: none;">
					<td height="100%"><a href="###" name="workflowAnchor"></a>
				    	 <c:choose>
				    	 	<c:when test="${isSupervis == true && param.from == 'Done' && finished!=true && type != 'doc' && param.isQuote != 'true'}">
						    	 <table width="100%" height="100%"  border="0" cellspacing="0" cellpadding="0">
						    		<c:if test="${v3x:getBrowserFlagByRequest('WorkFlowEdit', pageContext.request)}">
						    		<tr  height="30"><td align="left" valign="middle" class="padding-5"><input type="button" onClick="showDigarm('${summary.id}');" value="<fmt:message key="edit.workflow.label" />"/></td></tr>
						    		</c:if>
						    		<tr height="30" >
							    		<td height="100%">
									        <iframe src="${colSuperviseURL}${v3x:resSuffix()}&method=showDigramOnly&summaryId=${summary.id}&affairId=${param.affairId}&superviseId=${bean.id}&fromList=list&isDetail=1" id="monitorFrame" name="monitorFrame"
									                frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="auto"></iframe>
							    		</td>
						    		</tr>
						    	</table>
				    	 	</c:when>
				    	 	<c:otherwise>
					        <iframe src="<html:link renderURL='/genericController.do${v3x:resSuffix()}&ViewPage=collaboration/monitor&isShowButton=${isShowButton}&affairId=${param.affairId}&superviseId=${bean.id}&isDetail=1' />" name="monitorFrame"
					                frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="no"></iframe>
				    	 	</c:otherwise>
				    	 </c:choose>
					</td>
				</tr>
				
				<%--正文 --%>
				<tr id="contentTR">
					<td valign="top">
						<iframe src="${detailURL}?method=getContent&summaryId=${summary.id}&affairId=${param.affairId}&from=${param.from}&isQuote=${param.isQuote}&type=${type}&lenPotent=${param.lenPotent}${Anchor}" width="100%" height="100%" name="contentIframe" id="contentIframe" frameborder="0" scrolling="auto" marginheight="0" marginwidth="0"></iframe>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<%--正文流程切换行 结束--%>
	
	
</table>

<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/collaboration.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/flowperm/js/flowperm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/workflow/workflow.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/thirdMenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>

<script type="text/javascript">
var alert_noFlow = "<fmt:message key='alert.sendImmediate.nowf'/>";
var alert_cannotTakeBack = "<fmt:message key='col.takeBack.flowEnd.alert' />";
var docURL = "${docURL}";
var edocURL = "${edocURL}";
var genericURL = '${detailURL}';
var edocSuperviseURL = '${supervise}';
var genericControllerURL = "${genericController}?ViewPage=";
var deleteActionURL = genericURL + "?method=delete&from=${param.method}";
var pigeonholeActionURL = genericURL + "?method=pigeonhole&from=${param.method}";
var templeteURL = "${templeteURL}";
var fullEditorURL = "${fullEditorURL}";
var formURL = "${formURL}";
var mailURL = "${webmailURL}";
var colSuperviseURL = "${colSuperviseURL}";
var workManageURL = "${workManageURL}";
var processLogURL = "${processLogURL}";
var colWorkFlowURL=genericURL;
var mtMeetingUrl = "${mtMeetingURL}";

var collaborationCanstant = {
    deleteActionURL : "collaboration.do?method=delete&from=${param.method}",
    takeBackActionURL : "collaboration.do?method=takeBack",
    deletePeopleActionURL : "collaboration.do?method=deletePeople",
	hastenActionURL : "collaboration.do?method=hasten",
	pigeonholeActionURL : "collaboration.do?method=pigeonhole&from=${param.method}",
	issusNewsActionURL : "collaboration.do?method=issusNews",
	issusBulletionActionURL : "collaboration.do?method=issusBulletion"
}

var edocCanstant = {
	hastenActionURL : "edocSupervise.do?method=sendMessage"
}

v3x.loadLanguage("/apps_res/collaboration/js/i18n");
v3x.loadLanguage("/apps_res/v3xmain/js/i18n");
var summary_id='${summary.id }';
var docURL = "${docURL}";
var edocURL = "${edocURL}";
var genericURL = '${detailURL}';
var genericControllerURL = "${genericController}?ViewPage=";
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;

/*************************展现附件*************************/
showAttachment('${summary.id}', 2, 'attachment2Tr', 'attachment2NumberDiv','attachmentHtml2Span');
showAttachment('${summary.id}', 0, 'attachment1Tr', 'attachmentNumberDiv','attachmentHtml1Span');
//初始化附件区，当附件太多的时候设置样式高度为2行，有滚动条
var attDiv =document.getElementById("attDiv");
var att2Div= document.getElementById("att2Div");
if(attDiv) exportAttachment(attDiv);
if(att2Div) exportAttachment(att2Div);
/*************************结束*************************/


function createWebIm(){
	var requestCaller = new XMLHttpRequestCaller(this, "messageController", "createCollDisscuss", false);
	requestCaller.addParameter(1, "Long", "${summary.id}");
	var rv = requestCaller.serviceRequest();
	var cando = rv.get("cando");

	if(cando == "true"){
		var teamId = rv.get("teamId");
		var teamName = rv.get("teamName");

		if(getA8Top().contentFrame){
			getA8Top().contentFrame.topFrame.showOnlineIMForCol(teamId, teamName);
		}else{
			try{
				dialogArguments.getA8Top().contentFrame.topFrame.showOnlineIMForCol(teamId, teamName);
			}catch(e){
				try{
					top.opener.getA8Top().contentFrame.topFrame.showOnlineIMForCol(teamId, teamName);
				}catch(e){
					alert("<fmt:message key='message.error.errormsg' bundle='${wim}'/>");
					return;
				}
			}
		}
	}else{
		var success = rv.get("success");
		if(success == "true"){
			alert("<fmt:message key='message.error.disscurrmsg' bundle='${wim}'/>");
		}
		return;
	}
	window.close();
}

function createmeeting(affairId,collaborationFrom){
	/** branches_a8_v350_r_gov GOV-2772 xiangfan 修改，当为政务版 跳转到政务版的会议模块 Start  */
	var url;
	if(${v3x:getSysFlagByName('sys_isGovVer')=='true'}){
		//xiangfan 对参数进行了修改，修复GOV-3397
		url = "${mtMeetingURL}?method=entryManager&entry=meetingManager&listType=listNoticeMeeting&listMethod=create&summaryId=${summary.id}&affairId="+affairId+"&collaborationFrom="+collaborationFrom+"&formOper=new";
	}else {
		url = "${mtMeetingURL}?method=templatecreate&formOper=new&summaryId=${summary.id}&affairId="+affairId+"&collaborationFrom="+collaborationFrom;
	}
	/** branches_a8_v350_r_gov GOV-2772 xiangfan 修改，当为政务版 跳转到政务版的会议模块 End  */
	
	if(top.parent.getA8Top().contentFrame){
		top.getA8Top().contentFrame.mainFrame.location.href = url;
	}else{
		try{
			dialogArguments.getA8Top().contentFrame.mainFrame.location.href = url;
		}catch(e){
			try{
				top.opener.getA8Top().contentFrame.mainFrame.location.href = url;
			}catch(e){
				//不允许隔层发起会议
				alert("<fmt:message key='col.book.errmeetingmsg'/>");
				return;
			}
		}
		window.close();
	}
	/***
	var contentFrameWin = getA8Top().contentFrame;
	if(typeof contentFrameWin == 'undefined'){
	var parentWin = window.parent.dialogArguments;
	contentFrameWin = parentWin.getA8Top().contentFrame;
	if(typeof contentFrameWin == 'undefined'){
	contentFrameWin = parentWin.dialogArguments.getA8Top().contentFrame;
	}
	}else{
	contentFrameWin = getA8Top().contentFrame;
	}
	**/
}


function showPrecessAreaTd(type){
	var contentTR = document.getElementById('contentTR');
	var workflowTR = document.getElementById('workflowTR');
	var content_input = document.getElementById('content_input');
	var workflow_input = document.getElementById('workflow_input');
	//--
	var originalSendOpinion = document.getElementById('originalSendOpinion');
	var senderOpinion = document.getElementById('senderOpinion');
	var colOpinion = document.getElementById('colOpinion');
	var scrollListDiv = document.getElementById('scrollListDiv');
	if(type == 'content'){
		if(scrollListDiv)scrollListDiv.style.overflow = "auto"
		contentTR.style.display = '';
		content_input.className = 'deal_btn_l_sel';
		workflow_input.className="deal_btn_r";
		workflowTR.style.display = 'none';
		if(originalSendOpinion){originalSendOpinion.style.display = '';}
		if(senderOpinion){senderOpinion.style.display = '';}
		if(colOpinion){colOpinion.style.display = '';}
	//	contentAnchor.focus();
	}else if(type == 'workflow'){
		if(scrollListDiv){scrollListDiv.style.overflow = "hidden";}
		contentTR.style.display = 'none';
		workflowTR.style.display = '';
		workflow_input.className="deal_btn_r_sel";
		content_input.className = 'deal_btn_l';
		if(originalSendOpinion){originalSendOpinion.style.display = 'none';}
		if(senderOpinion){senderOpinion.style.display = 'none';}
		if(colOpinion){colOpinion.style.display = 'none';}
		//workflowAnchor.focus();
	}
}
//------------异步加载流程信息----------------------
var isLoadProcessXML = false;
var caseProcessXML = "";
var caseLogXML = "";
var caseWorkItemLogXML = "";
var caseId = "${caseId }";
var processId = "${processId }";
var showMode = 0;
var currentNodeId = "${empty type ? currentNodeId:''}";
var showHastenButton =  "${(empty param.isQuote) && type != 'doc' ? showHastenButton : 'false'}";
var isCheckTemplete = false;
var isShowWorkflowRuleLink = false;
var hasNotFinishNewflow = ${noFinishNewflow ne null};
var noFinishNewflow = "${v3x:escapeJavascript(noFinishNewflow)}";

<%--分支显示--%>
var branchs = new Array();
<c:if test="${branchs != null}">
	var handworkCondition = _('collaborationLang.handworkCondition');
	<c:forEach items="${branchs}" var="branch" varStatus="status">
		var branch = new ColBranch();
		branch.id = ${branch.id};
		branch.conditionType = "${branch.conditionType}";
		branch.formCondition = "${v3x:escapeJavascript(branch.formCondition)}";
		branch.conditionTitle = "${v3x:escapeJavascript(branch.conditionTitle)}";
		branch.conditionDesc = "${v3x:escapeJavascript(branch.conditionDesc)}";
		branch.isForce = "${branch.isForce}";
		eval("branchs["+${branch.linkId}+"]=branch");
	</c:forEach>
</c:if>

<%-- 新流程设置相关信息 Strat--%>
var newflowType = "${newflowType}";<%-- 新流程类型，主流程还是子流程 --%>
var newflowTempleteId = "${newflowTempleteId}";<%-- 新流程模板ID --%>
<%-- var relateSummaryIds = [];可查看的协同Ids --%>
var relateAffairIds = [];<%-- 可查看的协同Affair Ids --%>
var parentIdSummerId  ;<%--父流程id--%>
var relateNodeIds = [];<%-- 可查看的子流程的NodeIds --%>
var newflowNodeIdsStr = ",";<%-- 用于显示查看关联流程按钮的节点Ids --%>
<c:if test="${relateFlowList ne null}">
	<c:choose>
	<c:when test="${newflowType eq 'main'}">
		<c:forEach items="${relateFlowList}" var="newflow">
			newflowNodeIdsStr += "${newflow.mainNodeId},";
			relateAffairIds[relateAffairIds.length] = "${newflow.affairId}";
			relateNodeIds[relateNodeIds.length] = "${newflow.mainNodeId}";
		</c:forEach>
	</c:when>
	<c:when test="${newflowType eq 'child'}">
		<c:forEach items="${relateFlowList}" var="newflow">
			newflowNodeIdsStr += "start,";
			relateAffairIds[relateAffairIds.length] = "${newflow.mainAffairId}";
			parentIdSummerId = "${newflow.mainSummaryId}" ;
		</c:forEach>
	</c:when>
	</c:choose>
</c:if>
<%-- 新流程设置相关信息 End--%>
function initCaseProcessXML(){
	if(isLoadProcessXML == false){
		if(parent.detailRightFrame){
			parent.detailRightFrame.initCaseProcessXML();
			caseProcessXML = parent.detailRightFrame.caseProcessXML;
			caseLogXML = parent.detailRightFrame.caseLogXML;
			caseWorkItemLogXML = parent.detailRightFrame.caseWorkItemLogXML;
		}else{
			try {
				var requestCaller = new XMLHttpRequestCaller(null, "ajaxColManager", "getXML", false, "POST");
				requestCaller.addParameter(1, "String", caseId);
				requestCaller.addParameter(2, "String", processId);
				var processXMLs = requestCaller.serviceRequest();
				
				if(processXMLs){
					caseProcessXML = processXMLs[0];
					caseLogXML = processXMLs[1];
					caseWorkItemLogXML = processXMLs[2];
					document.getElementById("process_xml").value = caseProcessXML;
					document.getElementById("process_desc_by").value = "xml";
				}
			}
			catch (ex1) {
			}
		}
		isLoadProcessXML = true;
	}
}
//------------异步加载流程信息end----------------------

<%--督办修改流程--%>
function showDigarm(id) {
	var _url = "${colSuperviseURL}?method=showDigarm&summaryId="+id+"&comm=toxml&fromList=popup";
	var rv = v3x.openWindow({
   		url: _url,
   		width: 860,
    	height: 690,
    	resizable: "no"
	});
	if(rv){
		try{
			if(rv=="-BACK-"){
				if(!window.dialogArguments)
					parent.parent.location.href = parent.parent.location.href;
				else
					window.close();
			}
			else
				document.getElementById('monitorFrame').src = "${colSuperviseURL}?method=showDigramOnly&summaryId=${summary.id}&superviseId=${bean.id}&fromList=list&";
		}
		catch(e){}
	}
}
//自动定位点击列表行
setTablePosition(parent.parent.listFrame);
var colBodyType="${(bodytype=='OfficeWord' || bodytype=='OfficeExcel')}";
</script>
</body>
</html>