<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../common/INC/noCache.jsp"%>
<%@page import="com.seeyon.v3x.exchange.util.Constants"%>
<%@page import="com.seeyon.v3x.edoc.manager.EdocSwitchHelper"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="edocHeader.jsp"%>
<%@ include file="../doc/pigeonholeHeader.jsp" %>
<%@ include file="../common/INC/noCache.jsp"%>
<title><fmt:message key="common.page.title" bundle="${v3xCommonI18N}" /></title>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/jquery/themes/default/easyui.css${v3x:resSuffix()}" />" />
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.easyui.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/iSignatureHtml/js/iSignature.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/phrase.js${v3x:resSuffix()}" />"></script>

<c:set var="canEditAtt1" value="${allowUpdateAttachment && !summary.finished && param.from=='sended'}"/>
<c:set var="canEditAtt2" value="${allowUpdateAttachment && !summary.finished && param.from=='Sent'}"/>
<c:set var="canEditAtt" value="${canEditAtt1||canEditAtt2}"/>
<c:set value="${v3x:currentUser().id}" var="currentUserId"/>
<c:set value="${param.from eq 'Pending' && affair.state eq 3 && 'glwd' ne param.openFrom}" var="hasSignButton"/>
<v3x:selectPeople id="flash" panels="Department,Team" selectType="Department,Team,Member"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="monitorFrame.dataToFlash(elements)" viewPage="selectNode4Workflow"/>
                  
<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' var="printLabel"/>
<fmt:message key="common.attribute.label" bundle="${v3xCommonI18N}" var="attributeLabel" />
<fmt:message key='flow.node.excute.detail' bundle="${colI18N}" var="excuteDetailLabel" />  
<c:set var="isOfficeBodyType" value="${'OfficeWord' eq summary.firstBody.contentType or 'OfficeExcel' eq summary.firstBody.contentType}"/>

<script>
<!--
	flowSecretLevel_wf = "${summary.edocSecretLevel}";
	var htmlISignatureCount  = '${htmlISignCount}';
	var summaryId="${param.summaryId}";
	//office插件复制粘贴控制 -- 当前正文是否允许编辑
	var canEdit="${v3x:containInCollection(advancedActions, 'Edit') or v3x:containInCollection(commonActions, 'Edit')}";
	var appTypeName="${appTypeName}"; 
	var hasDiagram = "${hasDiagram}";
	var currentNodeId = "${currentNodeId}";
	var showMode = 0;
	var showHastenButton = "${showHastenButton}";
	var isNewCollaboration = "${isNewCollaboration}";
	var isTemplete = false;
	var affairState = "${affair.state}";
	var isCheckTemplete = false;
	var templateFlag = "${templateFlag}";
	var summary_id = "${param.summaryId}";
	var transmitSendNewEdocId="${transmitSendNewEdocId}";
	var isEdocCreateRole="${isEdocCreateRole}";
	var canEdit="${(v3x:containInCollection(advancedActions, 'Edit') or v3x:containInCollection(commonActions, 'Edit')) and hasSignButton}";
	var affair_id = "${param.affairId}";
	var hasPrepigeonholePath="${hasSetPigeonholePath}";
	var sendUserDepartmentId="${edocSendMember.orgDepartmentId}";
	var sendUserAccountId="${edocSendMember.orgAccountId}";
	var caseId = "${caseId }";
	var processId = "${processId }";
	var showOriginalElement_colAssign=false;
	//异步加载流程信息
	var isLoadProcessXML = false;
	var caseProcessXML = "";
	var caseLogXML = "";
	var caseWorkItemLogXML = "";
	var clickClose = false;

	var divPhraseDisplay = 'none';
	var onlySeeContent ='${onlySeeContent}';
	var phraseURL = '<html:link renderURL="/phrase.do?method=list" />';
	var hasSign=${v3x:containInCollection(commonActions, 'Sign') || v3x:containInCollection(advancedActions, 'Sign')};
	var permKey="${nodePermissionPolicyKey}";
	var formAction="<html:link renderURL='/edocController.do?method=finishWorkItem' />";

	//分支 开始
	//分支
	var branchs = new Array();
	var team = new Array();
	var secondpost = new Array();
	var startTeam = new Array();
	var startSecondpost = new Array();
	<c:if test="${branchs != null}">
		var handworkCondition = _('edocLang.handworkCondition');
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
	<c:if test="${teams != null}">
		<c:forEach items="${teams}" var="team">
			team["${team.id}"] = ${team.id};
		</c:forEach>
	</c:if>
	<c:if test="${secondPosts != null}">
		<c:forEach items="${secondPosts}" var="secondPost">
			secondpost["${secondPost.depId}_${secondPost.postId}"] = "${secondPost.depId}_${secondPost.postId}";
		</c:forEach>
	</c:if>
	<c:if test="${startTeams != null}">
		<c:forEach items="${startTeams}" var="startTeam">
			startTeam["${startTeam.id}"] = "${startTeam.id}";
		</c:forEach>
	</c:if>
	<c:if test="${startSecondPosts != null}">
		<c:forEach items="${startSecondPosts}" var="startSecondPost">
			startSecondpost["${startSecondPost.depId}_${startSecondPost.postId}"] = "${startSecondPost.depId}_${startSecondPost.postId}";
		</c:forEach>
	</c:if>
	//分支 结束
	var lineMax = 7;
	//office插件使用的JS变量
	${contentRecordId}
	var bodyType="${summary.firstBody.contentType}";
	
//-->
</script>
<style type="text/css">
.edocbarmargin{color: #ffffff;}
.edocbarmargin a{color: #ffffff;}
.layout-split-east{	
	border-left:1px solid #cccccc;
	padding-left: 4px;
	/*background:url("<c:url value='/common/images/deal/split.gif' />") left center no-repeat #cccccc;*/
	background:#cccccc;
}
</style>
</head>
<body id="easyui-layout"  class="easyui-layout" scroll="no" onload="init()" onUnload="unLoad('${summary.processId}', '${param.summaryId}','${currentUserId}')">
<input type="text" class="hidden" value="${summary.subject}" id="fileNameInput" />
	<div region="center" id="center_reagin" border="false" style="overflow: hidden;">
		<c:if test="${param.from eq 'sended' or param.from eq 'Sent'}"> <%--已发修改附件提交 --%>
		    <form id ="attchmentForm">
				<div id="attachmentInputs"></div>
			</form>
		</c:if>

		<v3x:attachmentDefine attachments="${attachments}" />
		<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" align="center" class="CollTable">
			<tr>
				<td height="36px" class="detail-summary" valign="top">
					<table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
						<tr>
							<td width="8%" height="18" nowrap align="right" class="detail-subject bg-gray"><fmt:message key="common.subject.label" bundle="${v3xCommonI18N}" /> : </td>
							<td width="46%"class="detail-subject-bold" ><span class="inline-block importance_${summary.urgentLevel}"></span>${v3x:toHTML(summary.subject)}</td>
							<td width="12%"  class=" detail-subject bg-gray"><fmt:message key="process.cycle.label"/> : </td>
							<td width="14%" class=" detail-subject-bold">&nbsp;${deallineLabel}</td>
							<td width="20%" class="detail-subject"><span title="${fullArchiveName}"><fmt:message key="pigeonhole.label.to" /> : <span class="detail-subject-bold">&nbsp;${archiveName}</span></span></td>
						</tr>
						<tr>
							<td height="18" nowrap align="right" class="bg-gray detail-subject"><fmt:message key="common.sender.label" bundle="${v3xCommonI18N}" /> : </td>
							<td valign="bottom"><a href="javascript:showV3XMemberCard('${summary.startUserId}')">${summary.startMember.name}</a> (<fmt:formatDate value="${summary.createTime}" pattern="${datePattern}"/>)</td>
							<td class="bg-gray detail-subject"><fmt:message key="common.remind.time.label" bundle='${v3xCommonI18N}' /> : </td>
							<td class="detail-subject-bold">&nbsp;${remindLabel}</td>
							<td>&nbsp;</td>
						</tr>
						<tr id="attachment2TrContent" style="display: none">
							<td height="18" nowrap class="bg-gray detail-subject" valign="top" align="right"><fmt:message key="common.toolbar.insert.mydocument.label" bundle="${v3xCommonI18N}" /> : </td>
							<td colspan="4">
								<div class="div-float" id=att2Div>
									<div class="div-float font-12px" style="margin-top: 4px;">(<span id="attachment2NumberDivContent" class="font-12px"></span>)</div>
									<span id="attachmentHtml2Span">
									</span>
								</div>
							</td>
						</tr>
						<tr id="attachmentTrContent" style="display: ${canEditAtt?'':'none' }">
							<td height="18" nowrap class="bg-gray detail-subject" valign="top">
							<%--如果有权限修改就显示“插入附件”按钮，没有权限就显示"附件"--%>
								<c:if test="${canEditAtt }">
									<span id="uploadAttachmentTR" ><a href="javascript:senderEditAtt()"><fmt:message key="common.toolbar.updateAttachment.label" bundle="${v3xCommonI18N}"/></a></span>
								</c:if>
								<c:if test="${!canEditAtt }">
									<span id="normalText"><fmt:message key="common.attachment.label" bundle="${v3xCommonI18N}" /></span> 
								</c:if>
								 : 
							</td>
							<td colspan="4" valign="top">
								<div class=" div-float" id="attDiv" style="width:100%">
									<div class="div-float font-12px" style="margin-top: 4px;">(<span id="attachmentNumberDivContent" class="font-12px">0</span>)</div>
									<span id="attachmentHtml1Span">
									<script type="text/javascript">
									<!--
									<c:if test="${canEditAtt }">
									document.getElementById("attachmentTrContent").style.display='';
									</c:if>
									//-->
									</script>
									</span>
						
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="35" class="col_content_toolbar">
					<table cellpadding="0" cellspacing="0" width="100%" height="100%">
						<tr>
							<td width="10" class="col_content_toolbar_l">&nbsp;</td>
							<td valign="top">
							
								<table cellpadding="0" cellspacing="0" width="100%" height="35">
									 <tr>
									    <td valign="middle" nowrap="nowrap" class="" colspan="${'Pending' eq param.from ?'1':'2'}">    
											<c:if test="${!onlySeeContent}">
												<input id="edocform_btn" class="deal_btn_l_sel" onclick="showPrecessAreaTd('edocform')"  type="button" value="<fmt:message key='edocform.label'/>"/>
											</c:if>
											<input id="content_btn" class="deal_btn_m" onclick="showPrecessAreaTd('content')"  type="button" value="<fmt:message key='common.toolbar.content.label'  bundle='${v3xCommonI18N}'/>"/>
											
											<c:if test="${hasBody1}">
												<input id="content1_btn" class="deal_btn_m" onclick="showPrecessAreaTd('content1')" type="button" value="<fmt:message key='edoc.contentnum1.label' />"/>		
											</c:if>
											<c:if test="${hasBody2}">
												<input id="content2_btn" class="deal_btn_m" onclick="showPrecessAreaTd('content2')" type="button" value="<fmt:message key='edoc.contentnum2.label' />"/>		    
											</c:if>	
											<c:if test="${onlySeeContent=='false'}">
												<input id="workflow_btn" class="deal_btn_r" onclick="showPrecessAreaTd('workflow')" type="button" value="<fmt:message key='workflow.label'/>"/>
											</c:if>	
									     </td>
									      
										 <td align="right" nowrap="nowrap">
										 	<div align="right">
		
										 	<span style="display:block;" class="div-float-right" >
											 	
											 	<c:if test="${param.from ne 'Pending'}">
											 		<input type="hidden" name="currContentNum" id="currContentNum" value="0"/>
												</c:if>
												<%--查看原文档--%>
												<%--
												<c:if test="${isOfficeBodyType && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
													<a href="#" onclick="dealPopupContentWin('0');">
														<font class="like-a"><fmt:message key="col.content.viewOriginalContent" bundle="${colI18N}"/></font>
													</a>
												</c:if>	
												 --%>
												<%--查看PDF文档--%>
												<c:if test="${!empty firstPDFId}">
													<span  title="pdf" onclick='contentIframe.pdfFullSize();' class="cursor-hand bodyType_Pdf margin10 div-float">&nbsp;&nbsp;</span>
												</c:if>	
						
												<c:if test="${isSupervis == true && from == 'supervise'}">
												<span title="<fmt:message key='supervise.label'  bundle='${colI18N}' />" onclick="showSupervise('${param.summaryId}');"  class="cursor-hand duban_print  margin10 div-float"></span>
												</c:if>
										   		<c:if test="${printEdocTable && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
										   			<span  title="${printLabel}" onclick='colPrint()' class="cursor-hand coll_print margin10 div-float">&nbsp;&nbsp;</span>
										   		</c:if>
										   		
										      	<span title="<fmt:message key="newflow.viewDetailandDaily" bundle='${colI18N}' />" onclick="showDetailAndLog('${param.summaryId}','${summary.processId}','','${appTypeName}')"  class="cursor-hand daily_print margin10 div-float">&nbsp;&nbsp;</span>
										      	<span title="<fmt:message key="newflow.viewPropertyState"  bundle="${colI18N}" /> " onclick="showAttribute('${param.affairId}', '${param.from}')"  class="cursor-hand property_print div-float <c:if test='${param.from eq "sended"}'>margin10</c:if>">&nbsp;&nbsp;</span>
										      	<c:if test="${param.from eq 'sended'}">
											    	<span title="<fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' />" onclick="showSuperviseWindow('${summary.edocSecretLevel}')" class="cursor-hand duban_print div-float">&nbsp;&nbsp;</span>
											    </c:if>
									 		</span>
									   		
									  </div>
									</td>  
									</tr>
								</table>
							
							</td>
							<td width="10" class="col_content_toolbar_r">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table cellpadding="0" cellspacing="0" width="100%" height="100%">
						<tr>
							<td width="10" class="col_content_middle_l">&nbsp;</td>
							<td>
							
								<table cellpadding="0" cellspacing="0" width="100%" height="100%">
		
									<%--切换菜单及操作按钮开始 --%>
									<tr>
										<td valign="top">
											<table width="100%" id="signAreaTable" height="100%" border="0" cellspacing="0" cellpadding="0">
												<tr id="closeTR" style="display: none" valign="top">
												    <td colspan="3">&nbsp;</td>
												</tr>
												<tr id="workflowTR" style="display: none;">
												    <td colspan="3">
												    	 <c:choose>
												    	 	<c:when test="${isSupervis == true && from == 'supervise' && finished!=true}">
														    	 <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
														    		<tr><td height="30" align="left" valign="middle" class="padding-5"><input type="button" onclick="showDigarm('${bean.id}');" value="<fmt:message key="edit.workflow.label" bundle="${colI18N}"/>"/></td></tr>
														    		<tr>
															    		<td>
															    			<div class="scrollList">
																	        <iframe src="${supervise}${v3x:resSuffix()}&method=showDigramOnly&edocId=${param.summaryId}&affairId=${param.affairId}&superviseId=${bean.id}&fromList=list&isDetail=1" name="monitorFrame" id="monitorFrame"
																	                frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="auto"></iframe>
															    			</div>
															    		</td>
														    		</tr>
														    	</table>
												    	 	</c:when>
												    	 	<c:otherwise>
													        <iframe src="<html:link renderURL='/genericController.do${v3x:resSuffix()}&ViewPage=collaboration/monitor&isShowButton=${isShowButton}&isDetail=1' />" name="monitorFrame"
													                frameborder="0" marginheight="0" marginwidth="0" height="100%" width="100%" scrolling="auto"></iframe>
												    	 	</c:otherwise>
												    	 </c:choose>
												    </td>
												</tr>
												<tr id="edocformTR">	
													<td colspan="3">
														<iframe src="${detailURL}?method=getContent&summaryId=${summary.id}&affairId=${param.affairId}&from=${param.from}&openFrom=${openFrom}&lenPotent=${lenPotent}&docId=${docId}&docResId=${param.docResId}&canUploadRel=${canUploadRel}&canUploadAttachment=${canUploadAttachment}&position=${showOpinionButton?position:''}&firstPDFId=${firstPDFId}" width="100%" height="100%" name="contentIframe" frameborder="0" scrolling="yes" marginheight="0" marginwidth="0"></iframe>
														<input type = "hidden" name="sattitude" id="sattitude">
													</td>
												</tr>	
												<tr id="contentTR" style="display:none;">
													<td colspan="3" valign="top" align="center" id="scrollContentTd">
														<iframe src="" width="100%" height="100%" name="htmlContentIframe" id="htmlContentIframe" frameborder="0" scrolling="yes" marginheight="0" marginwidth="0"></iframe>
													</td>
												</tr>
											</table>
										</td>
									 </tr>	
								</table>
							
							</td>
							<td width="10" class="col_content_middle_r">&nbsp;</td>
						</tr>	
					</table>
				</td>
			</tr>
		</table>
		</form>
		<div id="formContainer" style="display:none"></div>  
		<iframe name="showDiagramFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
		
	</div>
	
	<%--右侧处理区 --%>
	<c:if test="${hasSignButton}">
		<div region="east" id="east_div" split="true" style="width:${extendConfig != 'false'?350:45}px;overflow: hidden" border="false" >
			<table cellpadding="0" cellspacing="0" width="100%" height="100%">
				<tr>
					<td bgcolor="#efefef" valign="top">
						<table id="signMinDiv" style="cursor: pointer;display: ${extendConfig != 'false'?'none':''};"  border="0" width="35" height="100%" onclick="showLayout()"  cellspacing="0" cellpadding="0" >
							<tr>
								<td valign="top" align="left">
									<div style="width:12px; margin: 5px 0px 0px 15px;">
										<div class="zhankai"  id="img2" style="width:12px;margin: 0px 0px 10px 0px;" ></div>
										<fmt:message key="col.deal.opinion" bundle="${colI18N}"/>
									</div>
								</td>
							</tr>					
						</table>
						<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="25">
									<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="left_td_modal_deal" width="9">
												&nbsp;
											</td>
											<td height="25" valign="top" align="left">
												<div class="shousuo" style=" float:left;margin-top:5px;" id="img1" onclick="hideLayout()"></div>
												<span style="float: left;margin:2px 5px;">
										    	<c:set value="${v3x:_(pageContext, nodePermissionPolicy.label)}" var="perLocalName"/>
										        ${perLocalName==""?nodePermissionPolicyKey:perLocalName}
										        </span>
											</td>
											<%--节点说明 --%>
											<td align="right">
												<c:if test="${ templateFlag }">
												 <div class="help" id="dealExplainImage" title="<fmt:message key="node.deal.explain" bundle="${colI18N}"/>" onclick="colShowNodeExplain('${param.affairId}','${summary.templeteId}','${summary.processId}')" ></div>
												 <div id="nodeExplainDiv" style="display: none;background-color: #ffffff;height: 100px;width: 260px;z-index: 2;position: absolute;right: 30px;border: 1px solid black;text-align: left;" 
													onmouseout="">
													<table onmouseout="	" style="width: 100%">
														<tr height="87%" style="vertical-align: top;line-height: 18px;">
															<td id="nodeExplainTd"></td>
														</tr>
														<tr height="13%" style="vertical-align: bottom;">
															<td align="right" style="padding-right: 4px;"><a onclick="hiddenNodeIntroduction()">关闭</a></td>
														</tr>
													</table>
												</div>
												</c:if>
											</td>
											
											<td class="right_td_modal_deal" width="9">
												&nbsp;
											</td>
										</tr>
									</table>	
								</td>
							</tr>
							<tr>
								<td height="25">
									<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" class="deal_toobar_m" >
										<tr>
											<td class="deal_toobar_l" style="font-size: 2px;" width="6">
												&nbsp;
											</td>
											<td style="border-bottom:1px #fff solid; padding: 3px 0px 0px 0px;">
											<div>
										    	<c:forEach items="${commonActions}" var="operation">
											        <c:if test="${'AddNode' eq operation}">
														<span class="like-a div-float padding5 deal_block" onclick="javascript:preInsertPeople('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocType}','false')">
											            <span class="dealicons insertPeople"></span>
											            <fmt:message key="insertPeople.label" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <c:if test="${'JointSign' eq operation}">
														<span class="like-a div-float padding5 deal_block" onclick="javascript:preColAssign('${param.summaryId}','${summary.processId}','${param.affairId}')">
											            <span class="dealicons colAssign"></span>
											            <fmt:message key="colAssign.label" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <c:if test="${'Return' eq operation}">
														<span class="like-a div-float padding5 deal_block" id="stepBackSpan" onclick="javascript:stepBack(document.theform)">
														<input type="hidden" value="true" id="stepBackFlag" >
											            <span class="dealicons stepBack"></span>
											            <fmt:message key="stepBack.label" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <c:if test="${'RemoveNode' eq operation}">
														<span class="like-a div-float padding5 deal_block" onclick="javascript:preDeletePeople('${param.summaryId}','${summary.processId}','${param.affairId}')">
											            <span class="dealicons deletePeople"></span>
											            <fmt:message key="deletePeople.label" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <c:if test="${'Edit' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											        	<span class="like-a div-float padding5 deal_block" onclick="javascript:updateContent('${param.summaryId}')">
											            <span class="dealicons editContent"></span>
											            <fmt:message key="editContent.label" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <%--  修改附件--%>
											        <c:if test="${'allowUpdateAttachment' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											        	<span class="like-a div-float padding5 deal_block" onclick="javascript:updateAtt('${param.summaryId}','${summary.processId}')">
											            <span class="dealicons updateAttachment"></span>
											            <fmt:message key="edoc.allowUpdateAttachment" bundle="${colI18N}"/>
											            </span>
											        </c:if>
											        <c:if test="${'Infom' eq operation}">
														 <span class="like-a div-float padding5 deal_block" onclick="javascript:addInform('${param.summaryId}','${summary.processId}','${param.affairId}');">
											             <span class="dealicons addInform"></span>
											             <fmt:message key="addInform.label" bundle="${colI18N}"/>
											             </span>
											        </c:if>
											        
											        <c:if test="${'moreSign' eq operation  && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
														<span class="like-a div-float padding5 deal_block" onclick="javascript:addMoreSign('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocSecretLevel}');">
														<span class="dealicons multyAssign"></span>
											            <fmt:message key="edoc.metadata_item.moreSign" bundle="${colI18N}"/>
											            </span>
											        </c:if>
													<c:if test="${'Terminate' eq operation}">
														<span class="like-a div-float padding5 deal_block" id="stepStopSpan" onclick="javascript:stepStop(document.theform)">
														<input type="hidden" value="true" id="stepStopFlag" >
												        <span class="dealicons stepStop"></span>
												        <fmt:message key="stepStop.label" bundle="${colI18N}"/>
												        </span>
													</c:if>
													<c:if test="${'Cancel' eq operation}">
														<input type="hidden" value="true" id="repealItemFlag" >
														<span class="like-a div-float padding5 deal_block" onclick="javascript:repealItem('pending','${param.summaryId}')">
												        <span class="dealicons repeal"></span>
												        <fmt:message key="repeal.2.label"  bundle="${colI18N}"/>
												        </span>
												    </c:if>
													<c:if test="${'Forward' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:transmitSend('${param.summaryId}','${param.affairId}','${summary.edocType}');">
														         <span class="dealicons transmit"></span>
														         <span class="dealicons transmit"></span>
														        <fmt:message key='common.toolbar.transmit.label' bundle='${v3xCommonI18N}' />
														        </span>
													</c:if>
											        <c:if test="${'WordNoChange' eq operation}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:contentIframe.WordNoChange()">
														        <span class="dealicons wordNoChange"></span>
														        <fmt:message key="wordNoChange.label" bundle="${colI18N}"/>
														        </span>
											        </c:if>		
													<c:if test="${'ApproveSubmit' eq operation}">
														        <span class="dealicons approveSubmit"></span>
														        <fmt:message key="approveSubmit.label" bundle="${colI18N}"/>
													</c:if>
													<%--
													<c:if test="${v3x:containInCollection(commonActions, 'Modify')}">
																<img src="<c:url value='/apps_res/collaboration/images/workflowstop.gif' />" border="0" 
														        align="absmiddle" with="16">
														        <fmt:message key="modify.label" bundle="${colI18N}"/>
													</c:if>
													--%>
													<c:if test="${'UpdateForm' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:contentIframe.UpdateEdocForm('${param.summaryId}')">
														        <span class="dealicons updateform"></span>
														        <fmt:message key="node.policy.updateform.label"/>
														        </span>
													</c:if>
													<c:if test="${'Sign' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:openSignature()" title="<fmt:message key="comm.sign.introduce.label" bundle="${v3xCommonI18N}"/>">
														        <span class="dealicons signature"></span>
														        <fmt:message key="node.policy.Sign.label" bundle="${v3xCommonI18N}"/>
														        </span>
													</c:if>
													<c:if test="${'DepartPigeonhole' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:DepartPigeonhole(<%=ApplicationCategoryEnum.edoc.getKey()%>,'${param.summaryId}')">
														        <span class="dealicons departpigeonhole"></span>
														        <fmt:message key="edoc.action.DepartPigeonhole.label"/>
														        </span>
													</c:if>
													<c:if test="${'EdocTemplate' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											        			<span class="like-a div-float padding5 deal_block" onclick="javascript:taohong('edoc')">
											                    <span class="dealicons loadRedTemplate"></span>
											                    <fmt:message key="edoc.action.form.template" />
											                    </span>
											        </c:if>
													<c:if test="${'ScriptTemplate' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											        			<span class="like-a div-float padding5 deal_block" class="like-a" onclick="javascript:taohong('script')">
											                    <span class="dealicons scriptTemplate"></span>
											                    <fmt:message key="edoc.action.script.template" />
											                    </span>
											        </c:if>
											        <c:if test="${'PassRead' eq operation}">
									        					<span class="like-a  div-float padding5 deal_block" onclick="javascript:addPassInform('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocSecretLevel}');">
									                            <span class="dealicons passred"></span>
									                            <fmt:message key="node.policy.chuanyue" bundle="${v3xCommonI18N}"/>
									                            </span>
											        </c:if>
											        <c:if test="${'HtmlSign' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											        			<span class="like-a  div-float padding5 deal_block" onclick="javascript:htmlSign()">
											                    <span class="dealicons htmlSign"></span>
											                    <fmt:message key="edoc.action.htmlSign.label" />
											                    </span>
											        </c:if>
													<c:if test="${'SuperviseSet' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:openSuperviseWindow('${param.summaryId}')">
											                    <span class="dealicons supervise"></span>
											                    <fmt:message key="col.supervise.operation.label" bundle="${colI18N}" />
											                    </span>
													</c:if>
													<c:if test="${'TransmitBulletin' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:TransmitBulletin('bulletionaudit','${param.summaryId}')">
											                    <span class="dealicons transmitBulletin"></span>
											                    <fmt:message key="edoc.metadata_item.TransmitBulletin" bundle="${colI18N}" />
											                    </span>
													</c:if>
													<c:if test="${'TanstoPDF' eq operation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																<span class="like-a div-float padding5 deal_block" onclick="javascript:convertToPdf()">
											                    <span class="dealicons transmitBulletin"></span>
											                    <fmt:message key="edoc.metadata_item.TanstoPdf" bundle="${colI18N}" />
											                    </span>
													</c:if>
												</c:forEach>
												&nbsp;
											</div>
											</td>	
											<td width="10" style="border-bottom:1px #fff solid; padding: 3px 0px 0px 0px;">
											<div style="border-bottom:1px #d1d1d1 solid;">
												<c:set var="hasAdvanceButton" value="no"/>
										    	<c:set var="buttonNum" value="0" /> <!-- 控制6个后换行 -->
										    	<iframe id="processAdvanceDivIframe" scrolling="no" frameborder="0" style="position:absolute; right:2px; top:65; width:100px;height:10px; z-index:2; background-color:#ffffff;border:1px #C5C5C5 solid;display:none;z-index: 10"></iframe>
										       	<div style="position:absolute; right:2px; top:65; width:100px; z-index:2; background-color:#ffffff;border:1px #C5C5C5 solid;display:none;z-index: 11" 
				      									id="processAdvanceDIV" onMouseOver="advanceViews(true)" onMouseOut="advanceViews(false)" oncontextmenu="return false">
												<c:forEach items="${advancedActions}" var="aoperation">
												<div style="padding: 3px;" onmousemove="javascript:this.className='more-deal-sel'" onmouseout="javascript:this.className='more-deal'">
													<c:choose>
														<c:when test="${'AddNode' eq aoperation }">
												            <div class=" advanceICON">
												                    <c:set var="hasAdvanceButton" value="yes"/>                    
												                    <span onclick="javascript:preInsertPeople('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocType}','false')">
												                            <span class="dealicons-advance insertPeople"></span>
												                            <fmt:message key="insertPeople.label"/></span>
												            </div>
														</c:when>
														
														<c:when test="${'SuperviseSet' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
															<div class=" advanceICON">		
																	<c:set var="hasAdvanceButton" value="yes"/>
																	<span onclick="javascript:openSuperviseWindow('${param.summaryId}')">
												                    <span class="dealicons-advance supervise"></span>
												                    <fmt:message key="col.supervise.operation.label" bundle="${colI18N}"/>
												                    </span>
															</div>
														</c:when>
														 <c:when test="${'UpdateForm' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
												            <div style="clear:both"></div>
												            <div class=" advanceICON">
												                    <c:set var="hasAdvanceButton" value="yes"/>
												                    <span onclick="javascript:contentIframe.UpdateEdocForm('${param.summaryId}')">
												                            <span class="dealicons-advance updateform"></span>
												                            <fmt:message key="node.policy.updateform.label"/>
												                    </span>
												            </div>
											            </c:when>
											            <c:when test="${'Edit' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>                    
											                    <span onclick="javascript:updateContent('${param.summaryId}')">
											                            <span class="dealicons-advance editContent"></span>
											                            <fmt:message key="editContent.label"/></span>
											            </div>
											            </c:when>
											            
											            
											        	<%--  修改附件--%>
											        	<c:when test="${'allowUpdateAttachment' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
														<div class=" advanceICON">
											        		 	<c:set var="hasAdvanceButton" value="yes"/>
											            	 	<span onclick="javascript:updateAtt('${param.summaryId}','${summary.processId}')">
											                             <span class="dealicons-advance updateAttachment"></span>
											                            <fmt:message key="edoc.allowUpdateAttachment" bundle="${colI18N}"/></span>
											            </div>
											        	</c:when>
											        	
											        	
											            <c:when test="${'RemoveNode' eq aoperation}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:preDeletePeople('${param.summaryId}','${summary.processId}','${param.affairId}')">
											                             <span class="dealicons-advance deletePeople"></span>
											                             <fmt:message key="deletePeople.label"/></span>
											                
											            </div>
											            </c:when>
											        	 <%--
											            <c:if test="${v3x:containInCollection(advancedActions, 'Modify')}">
												        <div class="div-float processAdvanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <img src="<c:url value='/apps_res/collaboration/images/commonPhrase.gif' />" border="0"
											                            align="absmiddle" with="16"><br><fmt:message key="modify.label" bundle="${colI18N}"/>
											            </div>  
											            </c:if>
											            <c:if test="${v3x:containInCollection(advancedActions, 'ApproveSubmit')}">
											            <div class="div-float processAdvanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <img src="<c:url value='/apps_res/collaboration/images/commonPhrase.gif' />" border="0"
											                            align="absmiddle" with="16"><br><fmt:message key="approveSubmit.label" bundle="${colI18N}"/>
											            </div>
											            </c:if>
											            --%>            
											            <c:when test="${'Forward' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:transmitSend('${param.summaryId}','${param.affairId}','${summary.edocType}');">
											                             <span class="dealicons-advance transmit"></span>
											                            <fmt:message key='common.toolbar.transmit.label' bundle='${v3xCommonI18N}' />
											                    </span>
											            </div>
											            </c:when>
											            
											            
											            <c:when test="${'WordNoChange' eq aoperation}">
											            <div class=" advanceICON">
											                 <c:set var="hasAdvanceButton" value="yes"/>
											                   <span onclick="javascript:contentIframe.WordNoChange()">
											                            <span class="dealicons-advance wordNoChange"></span>
											                            <fmt:message key="wordNoChange.label" bundle="${colI18N}"/>
											                  </span>
											            </div>
											            </c:when>
											            <c:when test="${'EdocTemplate' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                 <c:set var="hasAdvanceButton" value="yes"/>
											                   <span onclick="javascript:taohong('edoc')">
											                            <span class="dealicons-advance loadRedTemplate"></span>
											                            <fmt:message key="edoc.action.form.template" />
											                   </span >
											            </div>
											            </c:when>
											            
											            <c:when test="${'Return' eq aoperation}">
											            <div class=" advanceICON" id="stepBackSpan">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:stepBack(document.theform)">
											                    <input type="hidden" value="true" id="stepBackFlag" >
											                            <span class="dealicons-advance stepBack"></span>
											                            <fmt:message key="stepBack.label" bundle="${colI18N}"/></span>
											            </div>
											            </c:when>
											            <c:when test="${'JointSign' eq aoperation}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:preColAssign('${param.summaryId}','${summary.processId}','${param.affairId}')">
											                            <span class="dealicons-advance colAssign"></span>
											                            <fmt:message key="colAssign.label" bundle="${colI18N}"/>
											                    </span>
											            </div>
											            </c:when>
											            <c:when test="${'Infom' eq aoperation}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:addInform('${param.summaryId}','${summary.processId}','${param.affairId}');">
											                            <span class="dealicons-advance addInform"></span>
											                            <fmt:message key="addInform.label" bundle="${colI18N}"/></span>
											            </div>
											            </c:when>
											            <c:when test="${'moreSign' eq aoperation  && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:addMoreSign('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocSecretLevel}');">
											                    <img  src="<c:url value='/apps_res/collaboration/images/addMoreSign.gif' />" border="0"
											                            align="absmiddle" height="14"><fmt:message key="edoc.metadata_item.moreSign" bundle="${colI18N}"/>
											                    </span>
											            </div>
											            </c:when>
											            
											            <c:when test="${'Terminate' eq aoperation}">
											            <div class=" advanceICON" id="stepStopSpan">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:stepStop(document.theform);">
											                    <input type="hidden" value="true" id="stepStopFlag" >
											                            <span class="dealicons-advance stepStop"></span>
											                            <fmt:message key="stepStop.label" bundle="${colI18N}"/>
											                    </span>
											            </div>
											            </c:when>
											            <c:when test="${'Cancel' eq aoperation}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <input type="hidden" value="true" id="repealItemFlag" >
																<span onclick="javascript:repealItem('pending','${param.summaryId}')">
															        <span class="dealicons-advance repeal"></span>
															        <fmt:message key="repeal.2.label" bundle="${colI18N}"/></span>
														</div>
												    	</c:when>
											            
											            <c:when test="${'Sign' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:openSignature()"  title="<fmt:message key="comm.sign.introduce.label" bundle="${v3xCommonI18N}"/>">
											                            <span class="dealicons-advance signature"></span>
											                            <fmt:message key="node.policy.Sign.label" bundle="${v3xCommonI18N}"/></span>
											            </div>
											            </c:when>
											            <c:when test="${'DepartPigeonhole' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:DepartPigeonhole(<%=ApplicationCategoryEnum.edoc.getKey()%>,'${param.summaryId}')">
											                            <span class="dealicons-advance departpigeonhole"></span>
											                            <fmt:message key="edoc.action.DepartPigeonhole.label"/></span>
											            </div>
											            </c:when>
											            <c:when test="${'ScriptTemplate' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:taohong('script')">
											                            <span class="dealicons-advance scriptTemplate"></span>
											                            <fmt:message key="edoc.action.script.template" /></span>
											            </div>
											            </c:when>
											            <c:when test="${'PassRead' eq aoperation}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:addPassInform('${param.summaryId}','${summary.processId}','${param.affairId}','${summary.edocSecretLevel}');">
											                            <span class="dealicons-advance passred"></span>
											                            <fmt:message key="node.policy.chuanyue" bundle="${v3xCommonI18N}"/></span>
											            </div>
											            </c:when>
											            <c:when test="${'HtmlSign' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:htmlSign();">
											                            <span class="dealicons-advance htmlSign"></span>
											                            <fmt:message key="edoc.action.htmlSign.label"/></span>
											            </div>
											            </c:when>
											            
											            <c:when test="${'TransmitBulletin' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
											            <div class=" advanceICON">
											                    <c:set var="hasAdvanceButton" value="yes"/>
											                    <span onclick="javascript:TransmitBulletin('bulletionaudit','${param.summaryId}');">
											                            <span class="dealicons-advance transmitBulletin"></span>
											                            <fmt:message key="edoc.metadata_item.TransmitBulletin"  bundle="${colI18N}"  /></span>
											            </div>
											            </c:when>
											            
											            <c:when test="${'TanstoPDF' eq aoperation && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
												            <div class="advanceICON">
												                    <c:set var="hasAdvanceButton" value="yes"/>
												                    <span onclick="javascript:convertToPdf(this.form);">
												                            <span class="dealicons-advance transmitBulletin"></span>
												                            <fmt:message key="edoc.metadata_item.TanstoPdf"  bundle="${colI18N}"  /></span>
												            </div>
											            </c:when>
											            
													</c:choose>
												</div>
										           
										           
										           
										           </c:forEach>
										           <c:if test="${v3x:getBrowserFlagByRequest('OnlyIpad', pageContext.request)}">
										           <div style="clear: both; width: 100%; text-align: right;padding: 0 5px 5px 0">
										              <span onclick="javascript:closeAdvance()"><fmt:message key="common.button.close.label"  bundle="${v3xCommonI18N}" />
										              </span>
										           </div>
										        	</c:if>
										        </div>
										        <c:if test="${hasAdvanceButton eq 'yes'}">
										            <div id='processAdvance' onClick="advanceViews()" class="shousuo"></div>
										        </c:if>
										        </div>
											</td>
											<td class="deal_toobar_r" style="font-size: 2px;" width="6">
												&nbsp;
											</td>
										</tr>									
									</table>	
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="left_td_modal_deal" width="9">
												&nbsp;
											</td>
											<td valign="top" align="left">
												<div class="scrollList" id="scrollListDiv">
												<form id="theform" name="theform" action="<html:link renderURL='/edocController.do?method=finishWorkItem' />" method="post" style='margin: 0px' onsubmit="return false">
													<!-- 接收从弹出页面提交过来的数据 -->
													<input type="hidden" name="returnDeptId" id="returnDeptId" value="">
													<input type="hidden" name="popJsonId" id="popJsonId" value="">
													<input type="hidden" name="popNodeSelected" id="popNodeSelected" value="">
													<input type="hidden" name="popNodeCondition" id="popNodeCondition" value="">
													<input type="hidden" name="popNodeNewFlow" id="popNodeNewFlow" value="">
													<input type="hidden" name="allNodes" id="allNodes" value="">
													<input type="hidden" name="nodeCount" id="nodeCount" value="">
													<div style="display:none" id="processModeSelectorContainer"></div>
													<input type="hidden" id="ajaxUserId" name="ajaxUserId" value="${currentUserId} "/>
													<input type="hidden" id="affair_id" name="affair_id" value="${param.affairId}"/>      
													<input type="hidden" id="summary_id" name="summary_id" value="${param.summaryId}"/>
													<input type="hidden" name="startMemberId" value="${summary.startMember.id}"/>
													<input type="hidden" name="appName" id="appName" value='<%=ApplicationCategoryEnum.edoc.getKey()%>'/>
													<input type="hidden" name="policy" value="${nodePermissionPolicyKey}"/>
													<input type="hidden" id="edocType" name="edocType" value="${summary.edocType}"/>
													<input type="hidden" id="archiveId" name="archiveId" value="${summary.archiveId}">
													<input type="hidden" id="prevArchiveId" name="prevArchiveId" value="${summary.archiveId}">
													<input type="hidden" name="supervisorId" id="supervisorId" value="${supervisorId}">
													<input type="hidden" name="isDeleteSupervisior" id="isDeleteSupervisior" value="false">
													<input type="hidden" name="orgSupervisorId" id="orgSupervisorId" value="${supervisorId}">
													<input type="hidden" name="supervisors" id="supervisors" value="${supervisors}">
													<input type="hidden" name="unCancelledVisor" id="unCancelledVisor" value="${unCancelledVisor }">
													<input type="hidden" name="sVisorsFromTemplate" id="sVisorsFromTemplate" value="${sVisorsFromTemplate}">
													<input type="hidden" name="awakeDate" id="awakeDate" value="${awakeDate}">
													<input type="hidden" name="superviseTitle" id="superviseTitle" value="${superviseTitle}">
													<input type="hidden" name="processId" id="processId" value="${summary.processId}">
													<input type="hidden" name="caseId" id="caseId" value="${summary.caseId }">
													<input type="hidden" name="count" id="count" value="${count}"/>
													<input type="hidden" name="disPosition" value="${disPosition}"/>
													<%--记录是否进行了文单套红，主要用来记录JS记录日志--%>
													<input type="hidden" name="redForm" id="redForm" value="false">
													<%--记录是否进行了正文套红，主要用来记录JS记录日志--%>
													<input type="hidden" name="redContent" id="redContent" value="false"/>
													<input type="hidden" id="currentLoginAccountId" name="currentLoginAccountId" value="${v3x:currentUser().loginAccount}">
													<input type="hidden" id="pushMessageMemberIds" name="pushMessageMemberIds" value="">
													<%--office --%>
													<input type="hidden" name="currContentNum" id="currContentNum" value="0">
													<input type="hidden" name="isUniteSend" id="isUniteSend" value="${summary.isunit}">
													<input type="hidden" name="orgAccountId" id="orgAccountId" value="${summary.orgAccountId}">
													<%--PDF--%>
													<INPUT type="hidden"  NAME="isConvertPdf"   id="isConvertPdf" value="" />
													<%--WORD转PDF的时候，生成的PDF正文的ID--%>
													<input type="hidden" name="newPdfIdFirst" id="newPdfIdFirst" value="${newPdfIdFirst}"/>
													<input type="hidden" name="newPdfIdSecond" id="newPdfIdSecond" value="${newPdfIdSecond}"/>
													
													<input type="hidden" id="process_xml" name="process_xml" value=""/>
													<input type="hidden" name="process_desc_by" id="process_desc_by" value="xml" />
													<input type="hidden" name="currentNodeId" value="${currentNodeId }" />
													<!-- 将isMatch缺省值为true，判断是否最后一个处理人在preSend中 -->
													<input type="hidden" name="isMatch" id="isMatch" value="true" />
													<!-- 暂存待办的意见ID -->
													<input type="hidden" name="oldOpinionId" value="${tempOpinion.id}" />
													<input type="hidden" name="__ActionToken" readonly value="SEEYON_A8" > <%-- post提交的标示，先写死，后续动态 --%>
													
													<input type="hidden" name="bodyType" id="bodyType" value="${summary.firstBody.contentType}">
													<script type="text/javascript">
														document.getElementById("process_xml").value = caseProcessXML;
													</script>
													
													<v3x:selectPeople id="wf" panels="Department,Team" selectType="Department,Team,Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="selectInsertPeople(elements)" viewPage="selectNode4Workflow"/>
													<script type="text/javascript">var hiddenMultipleRadio_wf = true;</script>
													<v3x:selectPeople id="colAssign" panels="Department,Team,Post" selectType="Department,Team,Post,Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="selectColAssign(elements)"/>
													<script type="text/javascript">var flowSecretLevel_addInform = ${summary.edocSecretLevel};</script>
													<v3x:selectPeople id="addInform" panels="Department,Team,Post" selectType="Department,Team,Post,Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="selectAddInform(elements)" viewPage="selectNode4Workflow"/>
												
													<v3x:selectPeople id="passRead" panels="Department,Team" selectType="Account,Department,Team,Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="selectPassRead(elements)"/>
													<v3x:selectPeople id="addMoreSign" panels="Department" selectType="Department,Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="addMoreSignResult(elements)" />
													<script>
													    var exMems = new Array();
														exMems = exMems.concat(parseElements("${v3x:parseElementsOfIds(supervisorIds, 'Member')}"));
														excludeElements_sv = exMems;
														//传阅知会，不回现选择数据
														showOriginalElement_addInform=false;
														showOriginalElement_passRead=false;
														showOriginalElement_addMoreSign=false;
														
														var isConfirmExcludeSubDepartment_colAssign=true;
														var isConfirmExcludeSubDepartment_addInform=true;
														var isConfirmExcludeSubDepartment_passRead=true;
														
													</script>
													<v3x:selectPeople id="sv" panels="Department" selectType="Member"
													                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
													                  jsFunction="sv(elements)"/>
													<script>
													onlyLoginAccount_flash=true;
													onlyLoginAccount_wf=true;
													onlyLoginAccount_colAssign=true;
													onlyLoginAccount_sv=true;
													var unallowedSelectEmptyGroup_colAssign = true;
													var unallowedSelectEmptyGroup_addInform = true;
													var hiddenFlowTypeRadio_addInform = true;
													var hiddenRootAccount_addInform = true;
													</script>
													<input type="hidden" name="affairId" value="${param.affairId}"/>
													<span id="selectPeoplePanel"></span>
													
													<div oncontextmenu="return false" style="position:absolute; right:20px; top:120px; width:260px; height:60px; z-index:2; background-color: #ffffff;display:none;overflow:no;border:1px solid #000000;" id="divPhrase" onmouseover="showPhrase()" onmouseout="hiddenPhrase()" oncontextmenu="return false">
													    <IFRAME width="100%" id="phraseFrame" name="phraseFrame" height="100%" frameborder="0" align="middle" scrolling="no" marginheight="0" marginwidth="0"></IFRAME>
													</div>
													<table width="100%" border="0" cellspacing="0" cellpadding="0">
														<%--态度常用语 --%>
														<tr>
														    <td height="30" colspan="2">
														         <c:if test="${attitudes != 3}">
														        	<c:set var="enclude" value="${attitudes==2?'1':'' }"/>
														        	<c:set var="select" value="${attitudes==2?'2':'1' }"/>
														            <v3x:metadataItem metadata="${colMetadata['collaboration_attitude']}" showType="radio" name="attitude" selected="${draftOpinion == null ? select : draftOpinion.attitude}" enclude="${enclude }"/>
															     </c:if>
														
														        <c:if test="${v3x:containInCollection(baseActions, 'CommonPhrase')}">
														           <a onclick="javascript:showPhrase(this)" style="float: right;">
															           <fmt:message key="commonPhrase.label"/>
														           </a>
														        </c:if>
														    </td>
														</tr>
														<%--意见录入框 --%>
													    <tr>
												        	<td id="scrollTd"  colspan="2" valign="top">
																	<c:if test="${v3x:containInCollection(baseActions, 'Opinion')}">
																	<table cellpadding="0" cellspacing="0"  width="100%" class="deal_con_l">
																		<tr>
																			<td>
																		        	<table cellpadding="0" cellspacing="0" width="100%" class="deal_con_r">
																		        		<tr> 
																		        			<td colspan="3">
																					        	<input type="hidden" id="opinionPolicy" name="opinionPolicy" value="${opinionPolicy}" />
																					        	<textarea id="contentOP" name="contentOP" rows="${param.openLocation=='detailFrame'? 6:16}" validate="maxLength" inputName="<fmt:message key='common.opinion.label' bundle='${v3xCommonI18N}' />" maxSize="4000">${empty tempOpinion.content?"":fn:trim(tempOpinion.content)}</textarea>
																		        			</td>
																		        		</tr>
																		        	</table>
																			</td>						
																		</tr>
																	</table>
																	</c:if>
																	
																	
																	
																	
																   	<table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#EFEFEF"> 
																		<tr>
																			<td align="left" colspan="2">
																				<a href="#" onclick="showPushWindow('${param.summaryId}');"><img src="<c:url value='apps_res/v3xmain/images/online.gif' />" border="0" align="absmiddle"/>&nbsp;
																				<fmt:message key="message.push.label" bundle="${colI18N}"/></a>
																			</td>
																		</tr>
																		<c:if test="${v3x:containInCollection(baseActions, 'Track') && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																			<tr>
																			 	<td colspan="2" height="25" valign="middle" >
																					<label for="isTrack">
																			    		<input type="checkbox" name="afterSign" value="track" onclick="setTrackRadiio(this);"id="isTrack" ${v3x:outConditionExpression(affair.isTrack, 'checked', '')} ><fmt:message key="track.label" />:
																			    		<label for="trackRange_all">
																							<input type="radio" name="trackRange" id="trackRange_all" disabled onclick="setTrackCheckboxChecked();" value="1" ${affair.isTrack&&empty trackIds?'checked':''}/><fmt:message key="col.track.all" bundle="${v3xCommonI18N}" />
																						</label>
																						<label for="trackRange_part">
																							<c:set value="${v3x:parseElementsOfIds(trackIds, 'Member')}" var="mids"/>
																							<input type="hidden" value="${trackIds}" name="trackMembers" id="trackMembers"/>
																							<v3x:selectPeople id="track" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Member" jsFunction="setPeople(elements)" originalElements="${mids}"/>	
																							<input type="radio" name="trackRange"  id="trackRange_part" disabled onclick="selectPeopleFunTrackNewCol()" value="0" ${not empty trackIds?'checked':''}/><fmt:message key="col.track.part" bundle="${v3xCommonI18N}" />
																						</label>
																			    	</label>
																		    	</td>
																			</tr>
																		</c:if>
																		<tr>
																		    <td colspan="2">
																		    <%--
																		        <c:if test="${v3x:containInCollection(baseActions, 'Track') && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																		            <label for="track">
																		                <input type="checkbox" name="afterSign" id="track" value="track" onclick="checkMulitSign(this)" ${affair.isTrack?'checked':''}/>
																		                <fmt:message key="track.label"/>
																		            </label>
																		        </c:if>
																		         --%>
																		        <c:if test="${v3x:containInCollection(baseActions, 'Archive')}">
																		        	<c:if test="${v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
																		            <label for="pipeonhole">
																		                <input type="checkbox" name="afterSign" id="pipeonhole" value="pipeonhole" onclick="checkMulitSign(this)" 
																		                	${nodePermissionPolicyKey eq 'fengfa'?'checked':''}
																		                	<c:if test="${!canArchive}">disabled</c:if>/>
																		                	<fmt:message key="sign.after.pipeonhole.label"/>
																		                	&nbsp;&nbsp;&nbsp;&nbsp;
																		                	
																		                	
																		            </label>
																		            
																		            <%--封发 并且设置了预归档路径--%>
																		            <c:set var="isShowPrepigenholePath" value="${nodePermissionPolicyKey eq 'fengfa' and summary.archiveId ne null and isPresPigeonholeFolderExsit}"/>
																		            <c:set var="archiveIdIsNull" value="${summary.archiveId eq null }"/>
																		            <c:set var="presPigeonholeFolderNotExsit" value="${summary.archiveId ne null and not isPresPigeonholeFolderExsit}"/>
																		            <c:set var="isShowSelectpigenholePath" value="${nodePermissionPolicyKey eq 'fengfa' and (archiveIdIsNull or presPigeonholeFolderNotExsit ) }"/>
																		           
																		            <%--设置了预先归档。则直接显示 --%>
																		            <span id="showPrePigeonhole" style="display:${isShowPrepigenholePath?'':'none'}">
																		           		<fmt:message key="pigeonhole.label.to" /> : &nbsp;${archiveFullName} </span>
																		            <%--没有设置预归档，需要手动选择 --%>
																		            <span id="showSelectPigeonholePath" style="display:${isShowSelectpigenholePath?'':'none'}">
																		             	<fmt:message key="pigeonhole.label.to" /> : &nbsp;
																		             	<select id="selectPigeonholePath" class="input-40per" onchange="pigeonholeEvent(this,'<%=ApplicationCategoryEnum.edoc.key()%>','finishWorkItem',this.form)">
																					    	<option id="defaultOption" value="1"><fmt:message key="common.default" bundle="${v3xCommonI18N}"/></option>   
																					    	<option id="modifyOption" value="2">${v3x:_(pageContext, 'click.choice')}</option>
																					    	<c:if test="${hasPrePighole}" >
																					    		<option value="3" selected>${archiveName}</option>
																					    	</c:if>
																				   		</select>
																		            </span>
																		            </c:if>
																		        </c:if>
																		    </td>
																		</tr>
																		<c:if test="${v3x:containInCollection(baseActions, 'EdocExchangeType')}">
																			<tr>
																				<td colspan="1">
																					<label for="edocExchangeType_depart">
																					                <input type="radio" name="edocExchangeType" id="edocExchangeType_depart"  onclick="hideMemberList()" value="<%=Constants.C_iExchangeType_Dept%>" ${isDefaultExchangeTypeDept? "checked":""}><fmt:message
																					                    key="edoc.exchangetype.department.label"/>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<label for="edocExchangeType_company">
																					                <input type="radio" name="edocExchangeType" id="edocExchangeType_company" onclick="showMemberList()" value="<%=Constants.C_iExchangeType_Org%>" ${isDefaultExchangeTypeOrg? "checked":""}><fmt:message
																					                    key="edoc.exchangetype.company.label"/>
																					</label>
																				</td>
																				<td colspan="1">
																					<div id="selectMemberList" style="display:none;">
																						<select name="memberList" class="condition" style="width: 115px">
																							<option value=""><fmt:message key="select.label.unitEdocOper"/></option>
																							<c:forEach items="${memberList}" var="member">
																							 	<option value="${member.user.id}">${v3x:toHTML(member.user.name)}</option>
																							</c:forEach>
																						</select>
																					</div>
																				</td>
																			</tr>
																			<tr>
																			    <td colspan="2" height="4"></td>
																			</tr>
																		</c:if>
																		<c:set var="canUploadRel" value="${v3x:containInCollection(baseActions, 'UploadRelDoc')}"/>
																		<c:set var="canUploadAttachment" value="${v3x:containInCollection(baseActions, 'UploadAttachment')}"/>
																		<div id="attachmentEditInputs"></div>
																		<div id="attachmentInputs"></div>
																		<div id="contentIframeAttachmentInputs"></div>
																		<c:if test="${canUploadRel || canUploadAttachment}">
																			<tr>
																				<td colspan="2" style="padding: 5px 10px;">
																					<div height="36">
																						<c:if test="${canUploadRel }">
																						   <a href="javascript:quoteDocument()"><fmt:message key="common.toolbar.insert.mydocument.label"  bundle="${v3xCommonI18N}"/></a>
																						   (<span id="attachment2NumberDiv">0</span>)
																						   &nbsp;&nbsp;
																					   </c:if>
																					   <c:if test="${canUploadAttachment}">
																					   	 <a href="javascript:insertAttachment()"><fmt:message key="common.toolbar.insertAttachment.label" bundle="${v3xCommonI18N}"/></a>
																					   	 (<span id="attachmentNumberDiv">0</span>)
																					   </c:if>
																					</div>
																			   	</td>
																			</tr>
																			<tr>
																				<td colspan="2" style="padding-right: 21px;">
																			   		<div id="attachment2Area" style="overflow: auto;"></div>
																			   		<v3x:fileUpload attachments="${attachmentsOpinion}" canDeleteOriginalAtts="true" originalAttsNeedClone="false" />
																			   </td>
																			</tr>
																		</c:if>
																	</table>
													        </td>
													    </tr> 
													    <tr>
														    <td colspan="2" align="right" style="padding-top: 5px;"  height="50" id="doSignTr" valign="top">
														        <div style="border-top:1px solid #fff;">
														        <input id="processButton" type="button" class="deal_btn" onmouseover="javascript:this.className='deal_btn_over'" onmouseout="javascript:this.className='deal_btn'" onclick="edocSubmitForm(this.form,'${param.summaryId}')" value='<fmt:message key="common.button.submit.label" bundle="${v3xCommonI18N}" />' class="button-default-2"/>
														        <c:if test="${v3x:containInCollection(baseActions, 'Comment')}">
														            <input id="zcdbButton" class="deal_btn" onmouseover="javascript:this.className='deal_btn_over'" onmouseout="javascript:this.className='deal_btn'" type="button" value='<fmt:message key="zancundaiban.label" />'  class="button-default-4" onclick="doZcdb(this,'${param.summaryId}')"/>
														        </c:if>&nbsp;
														        </div>
														    </td>
														</tr>
													</table>
												</form>
												</div>
											</td>
											<td class="right_td_modal_deal" width="9">
												&nbsp;
											</td>
										</tr>
									</table>	
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>		
		</div>
	</c:if>	
	<c:if test="${param.from ne 'Pending'}">
		<input type="hidden" id="edocType" name="edocType" value="${summary.edocType}"/>
	</c:if>
	<div class="hidden" id="ctn">
		<div name="edocContentDiv" id="edocContentDiv" width="0px" height="0px" style="display:${summary.firstBody.contentType eq 'HTML' ? 'block':'none'}">
			<v3x:showContent  htmlId="edoc-contentText" content="${summary.firstBody.content}" type="${summary.firstBody.contentType}" createDate="${summary.firstBody.createTime}" contentName="${summary.firstBody.contentName}" viewMode ="edit"/>
		</div>
		<input type="hidden" name="bodyContentId" value="${summary.firstBody.id}">
		<script>editType="4,0";</script>
	</div>
<script type="text/javascript">
<!--
//js最后加载
	function hideLayout(){
		$('body').layout('panel','east').panel('resize',{
			width:45
		});
		var img1 = document.getElementById('img1');
		var img2 = document.getElementById('img2');
		var signMinDiv = document.getElementById('signMinDiv');
		if(img1) img1.style.display='none';
		if(img2) img2.style.display=''
		if(signMinDiv) signMinDiv.style.display=''
		$('body').layout().resize();
	}
	function showLayout(){
		$('body').layout('panel','east').panel('resize',{
			width:350
		});
		var img1 = document.getElementById('img1');
		var img2 = document.getElementById('img2');
		var signMinDiv = document.getElementById('signMinDiv');
	
		if(img1) img1.style.display='';
		if(img2) img2.style.display='none'
	
		if(signMinDiv) signMinDiv.style.display='none'
	
		$('body').layout().resize();
	}
	function senderEditAtt(){
		var attList = getAttachment(summaryId,summaryId);
		var result = editAttachments(attList,summaryId,summaryId,'4');
		//提交
		if(result){
			saveAttachment();
			$('#attchmentForm').ajaxSubmit({
			        url : genericURL + "?method=updateAttachment&edocSummaryId="+summaryId+"&affairId="+affair_id,
			        type : 'POST',
			        success : function(data) {
				        updateAttachmentMemory(result,summaryId,summaryId,'')
						showAttachment(summaryId, 2, 'attachment2TrContent', 'attachment2NumberDivContent','attachmentHtml2Span');
						showAttachment(summaryId, 0, 'attachmentTrContent', 'attachmentNumberDivContent','attachmentHtml1Span');
						document.getElementById("attachmentTrContent").style.display='';
			 		}
			})
		}
	}
	function initCaseProcessXML(){
		if(isLoadProcessXML == false){
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
			isLoadProcessXML = true;
		}
	}
	function init() {
		parent.edocType="${summary.edocType}";
		var oSupervise = document.getElementById('buttonsupervis');
		if(oSupervise!=null){
			oSupervise.onclick=null;
			oSupervise.onclick=function(){
				document.getElementById('superviseIframe').src = "${edoc}?method=superviseDiagram&summaryId=${param.summaryId}&openModal=${openModal}";
			}
		}
		var buttonworkflow = document.getElementById('buttonworkflow');
		if(buttonworkflow!=null){
			buttonworkflow.onclick=null;
			buttonworkflow.onclick=function(){
				var divPhrase = document.getElementById('divPhrase');
				if(divPhrase!=null && divPhrase.style.display!='none'){
					divPhraseDisplay  = 'block';
					divPhrase.style.display='none';
				}else{
					divPhraseDisplay  = 'none';
				}
			}
		}
		var buttonsign = document.getElementById('buttonedocform');
		if(buttonsign!=null){
			buttonsign.onclick=null;
			buttonsign.onclick=function(){
				var divPhrase = document.getElementById('divPhrase');
				if(divPhrase!=null && divPhraseDisplay=='block'){
					divPhrase.style.display='block';
				}else{
					divPhrase.style.display='none';
				}
			}
		}
	
		var buttoncontent = document.getElementById('buttoncontent');
		if(buttoncontent!=null){
			buttoncontent.onclick=null;
			buttoncontent.onclick=function(){
				if(typeof(trans2Html)=='undefined' || trans2Html == 'false')
					LazyloadOffice('0');
			}
		}
		var buttoncontent = document.getElementById('buttoncontent1');
		if(buttoncontent!=null){
			buttoncontent.onclick=null;
			buttoncontent.onclick=function(){
				if(typeof(trans2Html)=='undefined' || trans2Html == 'false')
					LazyloadOffice('1');
			}
		}
		var buttoncontent = document.getElementById('buttoncontent2');
		if(buttoncontent!=null){
			buttoncontent.onclick=null;
			buttoncontent.onclick=function(){
				if(typeof(trans2Html)=='undefined' || trans2Html == 'false')
					LazyloadOffice('2');
			}
		}
		if(onlySeeContent=='true'){ //借阅只借阅正文的时候，切换到正文也签
			showPrecessAreaTd('content');	
		}else{
			showPrecessAreaTd('edocform');	
		}

	
		//显示正文附件区域，由于布局的关系 ，导致在firefox下面或者两次调用这个方法，导致显示不正常。故将其移动到页面初始化方法中
		showAttachment('${summary.id}', 2, 'attachment2TrContent', 'attachment2NumberDivContent','attachmentHtml2Span');
		showAttachment('${summary.id}', 0, 'attachmentTrContent', 'attachmentNumberDivContent','attachmentHtml1Span');
	
		<%--是否在已发中显示修改附件的按钮--%>
		if("${canEditAtt}" == 'true') showModifyAttachmentLabel();
	
		//初始化附件区，当附件太多的时候设置样式高度为2行，有滚动条
		var attDiv =document.getElementById("attDiv");
		var att2Div= document.getElementById("att2Div");
		if(attDiv) exportAttachment(attDiv);
		if(att2Div) exportAttachment(att2Div);

	}
	<%--是否在已发中显示修改附件的按钮--%>
	function showModifyAttachmentLabel(){
		var attachmentTr=document.getElementById("attachmentTrContent");
		if(attachmentTr)attachmentTr.style.display="";
		var normalText=document.getElementById("normalText");
		if(normalText)normalText.style.display="none";
		var uploadAttachmentTR=document.getElementById("uploadAttachmentTR");
		if(uploadAttachmentTR)uploadAttachmentTR.style.display="";
	}
	
	function unLoad(processId, summaryId,userId){
		try{
	    	colDelLock(processId, summaryId,userId);
	    	unlockEdocEditForm(summaryId,userId);
	    	unlockHtmlContent(summaryId);
	    }catch(e){
	    }
	}
	function updateContent(summaryId){
	  modifyBody(summaryId,hasSign);
	}
	function htmlSign(){
		showPrecessAreaTd("edocform");
		contentIframe.handWrite(theform.summary_id.value,theform.disPosition.value);
	}
	function edocSubmitForm(subForm,summaryId){
		if(disagree()){//不同意处理
	    	return;
	    }
		//yangzd 在多人执行时，删除当前编辑人的编辑信息
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocSummaryManager", "deleteUpdateObj",false);
		requestCaller.addParameter(1, "String", summaryId);  
		requestCaller.serviceRequest();
		<%--封发节点或者含有交换类型的节点需要校验文号和公文收发员--%>
		var exchangeObj  =  document.getElementById("edocExchangeType_depart");
		 if(permKey=="fengfa" || exchangeObj){
		     if(contentIframe.checkEdocWordNo()==false)	       {
		        return;
		     }
		     if(checkExchangeRole()==false){
		         return;
		     }
		}  
		doSign(subForm,formAction);
	}
	function showMemberList(){
		var memberListDiv = document.getElementById("selectMemberList");
		memberListDiv.style.display = "";
	}
	function hideMemberList(){
		var memberListDiv = document.getElementById("selectMemberList");
		memberListDiv.style.display = "none";
	}
	function addIdentifier(){
		var orgMemberId = document.getElementById("orgSupervisorId").value;
		var memberId = document.getElementById("supervisorId").value;
		var orgArray = orgMemberId.split(",");
		var memberArray = memberId.split(",");
		var returnId = "";
		for(var i=0;i<memberArray.length;i++){
			if(orgMemberId.value == ""){
				memberArray[i] +="|0";
				returnId += memberArray[i];
				returnId +=",";
				continue;
			}
	
			var bool = orgMemberId.search(memberArray[i]);
			
			if(bool != "-1" || bool != -1){
				memberArray[i] += "|0";
			}else{
				memberArray[i] += "|1";
			}
			returnId += memberArray[i];
			returnId +=",";
		}
	
		if(orgMemberId != ''){
			for(var i=0;i<orgArray.length;i++){
				var bool = returnId.search(orgArray[i]);
				if(bool == '-1' || bool == -1){
					orgArray[i] += "|2";
					returnId += orgArray[i];
					returnId += ",";
				}
			}
		}
		document.getElementById("supervisorId").value = returnId;
	}
	function checkExchangeRole(){
	  var typeAndIds="";
	  var msgKey="edocLang.alert_set_departExchangeRole";	  
	  var obj=document.getElementById("edocExchangeType_depart");
	  if(obj==null){return true;}
	  if(obj.checked)
	  {
		  var list='${deptSenderList}';
		  if(list!=null&&list!="undifined"&&list!=""){
			  var _url= encodeURI(genericControllerURL+"collaboration/selectDeptSender&memberList='"+list+"'")
			  var listArr=list.split("|");
			  if(listArr.length>1){
				  sendUserDepartmentId = v3x.openWindow({
			     		 url: _url,
			     		 width: 260,
				      	 height: 190,
				      	 resizable: "no"
			  	  });
			  	  if(sendUserDepartmentId=="cancel" || typeof(sendUserDepartmentId) == 'undefined'){
				  		<%--取消或者直接点击关闭--%>
						return false;
				  }
			  }else if(listArr.length==1){
				  sendUserDepartmentId=listArr[0].split(',')[0];;
			  }
			  document.getElementById("returnDeptId").value=sendUserDepartmentId;
		  }
	    typeAndIds="Department|"+sendUserDepartmentId;	    
	  }
	  else
	  {
		var orgAccountId = document.getElementById("orgAccountId");
	  	typeAndIds="Account|"+orgAccountId.value;
	  	msgKey="edocLang.alert_set_accountExchangeRole";
	  }
	  var requestCaller = new XMLHttpRequestCaller(this, "ajaxEdocExchangeManager", "checkExchangeRole",false);
  	  requestCaller.addParameter(1, "String", typeAndIds);  
	  var ds = requestCaller.serviceRequest();
	  if(ds=="check ok"){return true;}
	  else
	  {
	    alert(_(msgKey,ds));
	  }
	  return false;
	}
	var timerBrighter;
	function brighter(id){
	    timerBrighter = window.setInterval("menuItemIn('" + id + "')", 50);
	    window.clearInterval(timerDarker);
	}
	
	function menuItemIn(id){
		if(document.getElementById(id).filters.alpha.opacity < 100){
			document.getElementById(id).filters.alpha.opacity += 10;
	    }else{
			window.clearInterval(timerBrighter);
	    }
	}
	
	var timerDarker;
	function darker(id){
		timerDarker = window.setInterval("menuItemOut('" + id + "')", 50);
		window.clearInterval(timerBrighter);
	}
	
	function menuItemOut(id){
	    if(document.getElementById(id).filters.alpha.opacity > 0){
			document.getElementById(id).filters.alpha.opacity -= 10;
	    }else{
			window.clearInterval(timerDarker);
			
			if(document.getElementById(id).filters.alpha.opacity <= 0) {
				document.getElementById(id).style.display = "none";
			}
	    }
	}

	function openSuperviseWindow(summaryId){
    	var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var count = document.getElementById("count");
		var isDeleteSupervisior = document.getElementById("isDeleteSupervisior");
	
		var unCancelledVisor = document.getElementById("unCancelledVisor");
		var sfTemp = document.getElementById("sVisorsFromTemplate");
		var urlStr = superviseURL + "?method=superviseWindow";
		if(mId.value != null && mId.value != ""){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value  + "&sVisorsFromTemplate="+sfTemp.value +"&unCancelledVisor="+unCancelledVisor.value + "&count="+count.value;
		}
		urlStr += "&secretLevel="+flowSecretLevel_wf;
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 300,
	        width: 400
     	});
     	
    	if(rv!=null && rv!="undefined"){
    	   try{
	    	    var affair_IdValue = document.getElementById('affair_id') ;
	    	    var summary_IdValue = document.getElementById('summary_id') ;
	    	    var ajaxUserId = document.getElementById('ajaxUserId') ;
	    	    if(affair_IdValue && summary_IdValue && ajaxUserId ) {
	    	       recordChangeWord(affair_IdValue.value ,summary_IdValue.value ,"duban" ,ajaxUserId.value)
	    	    }
    	   }catch(e){
    	   }
    		var sv = rv.split("|");
    		if(sv.length == 4){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				//canModify.value = sv[4];
			}else if(sv.length == 5){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				isDeleteSupervisior.value = sv[4];//取消督办
			}
    	}
	}
	function showDigarm(id) {
		//判断是否当前用户是否仍然是公文督办人
		if(!isStillSupervisor(summaryId)){
			if(!window.dialogArguments)
				parent.parent.location.href = parent.parent.location.href;
			else
				window.close();
			return false;
		}
		var _url = "${supervise}?method=showDigarm&superviseId="+id+"&comm=toxml&fromList=popup";
		  	var rv = v3x.openWindow({
		     		 url: _url,
		     		 width: 860,
		      	height: 690,
		      	resizable: "no"
		  	});
		  	if(rv){
			document.getElementById('monitorFrame').src = "${supervise}?method=showDigramOnly&edocId=${param.summaryId}&superviseId=${bean.id}&fromList=list";
			}
	}
	//是否需要装载HTML正文。
	var isLoadHtmlContent = false;   
	function showPrecessAreaTd(type){
		var edocformTR = document.getElementById('edocformTR');
		var contentTR = document.getElementById('contentTR');
		var workflowTR = document.getElementById('workflowTR');
		var content1TR = document.getElementById('content1TR');
		var content2TR = document.getElementById('content2TR');
		
		var edocform_input = document.getElementById('edocform_btn');
		var content_input = document.getElementById('content_btn');
		var workflow_input = document.getElementById('workflow_btn');
		var content1_input = document.getElementById('content1_btn');
		var content2_input = document.getElementById('content2_btn');
	
		var hasBody1_flag = "${hasBody1}";
		var hasBody2_flag = "${hasBody2}";
		function initSet(){
			if(contentTR)contentTR.style.display = 'none';
			if(content1TR)content1TR.style.display = 'none';
			if(content2TR)content2TR.style.display = 'none';
	
			if(edocform_input)edocform_input.className = 'deal_btn_l';
			if(workflow_input)workflow_input.className = 'deal_btn_m';
			if(hasBody1_flag == 'true' || hasBody2_flag == 'true'){
				if(content_input)content_input.className = 'deal_btn_m';
			}else{
				if(content_input)content_input.className = 'deal_btn_r';
			}
			if(content1_input)content1_input.className = 'deal_btn_r';
			if(content2_input)content2_input.className = 'deal_btn_r';
			if(onlySeeContent == "false"){
				if(content_input)content_input.className = 'deal_btn_m';
				if(content1_input)content1_input.className = 'deal_btn_m';
				if(content2_input)content2_input.className = 'deal_btn_m';
				if(workflow_input)workflow_input.className = 'deal_btn_r';
			}
			
		}
		
		var bodyType = document.getElementById("bodyType").value;
		if((type == "content" || type == "content1" || type == "content2") && bodyType != "HTML"){
			//当前点击正文按钮，并且是非HTML正文时，按钮样式不变化
		}
		else{
			initSet();
		}
		
		if(type == 'edocform'){
			if(edocformTR)edocformTR.style.display = '';
			if(workflowTR)workflowTR.style.display = 'none';
			if(edocform_input)edocform_input.className = 'deal_btn_l_sel';
		}
		else if(type == 'workflow'){
			if(edocformTR)edocformTR.style.display = 'none';
			if(workflowTR)workflowTR.style.display = '';
			workflow_input.className = 'deal_btn_r_sel';
		}
		else if(type == 'content'){
			if(contentTR && bodyType == "HTML"){
				if(edocformTR)edocformTR.style.display = 'none';
				if(workflowTR)workflowTR.style.display = 'none';
				contentTR.style.display = '';
				//是否需要去装载HTML正文。
				if(!isLoadHtmlContent){
					var operationType = "";
					if(arguments>1)
						operationType = arguments[1];
					loadHtmlContent('read',summaryId);
				}
				content_input.className = 'deal_btn_m_sel';
			}
			else{
				LazyloadOffice('0');
			}
		}
		else if(type == 'content1'){
			if(contentTR && bodyType == "HTML"){
				if(edocformTR)edocformTR.style.display = 'none';
				if(workflowTR)workflowTR.style.display = 'none';
				content1TR.style.display = '';
				content_input.className = 'deal_btn_m_sel';
			}
			else{
				LazyloadOffice('1');
			}
		}
		else if(type == 'content2'){
			if(contentTR && bodyType == "HTML"){
				if(edocformTR)edocformTR.style.display = 'none';
				if(workflowTR)workflowTR.style.display = 'none';
				content2TR.style.display = '';
				content_input.className = 'deal_btn_m_sel';
			}
			else{
				LazyloadOffice('2');
			}
		}
	}

	//跟踪相关
	function setTrackRadiio(v){
		var obj = document.getElementById("isTrack");
		if(obj!=null){
			var all = document.getElementById("trackRange_all");
			var part = document.getElementById("trackRange_part");
			if(obj.checked){
				 all.disabled = false;
				 part.disabled = false;
				 all.checked = true;
			}else {
				all.disabled = true;
				part.disabled = true;
				all.checked = false;
				part.checked = false;
			}
		}
		checkMulitSign(v);
	}
	
	function setTrackCheckboxChecked(){
		var obj = document.getElementById("isTrack");
		if(obj!=null){
			obj.checked = true;
		}
	}
	function selectPeopleFunTrackNewCol(){
		setTrackCheckboxChecked();
		flowSecretLevel_track = "${summary.edocSecretLevel}";
		selectPeopleFun_track();
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
	$(document).ready(function(){
		if(v3x.isIpad){
			$('#easyui-layout').css({'width':1024,'height':768});
			$('body').layout().resize();
		}
	});
	var edocExchangeFlagObj = "${v3x:containInCollection(baseActions, 'EdocExchangeType')}";
	if(edocExchangeFlagObj=="true"){
		//封发节点和【交换类型】节点权限都显示
	if ((permKey == "fengfa")||(edocExchangeFlagObj=="true")) {
	 var companyRadioObj = document.getElementById("edocExchangeType_company");
		  if(companyRadioObj!=null && companyRadioObj.checked){
		  	showMemberList();
		  }
		}
	} 
	
	//当选择不同意时弹出选择页面（回退，或者终止，或者继续）
	function showDisAgreeForm(stepBackFlag, stepStopFlag, repealItemFlag){
		var rv = v3x.openWindow({
	        url: genericControllerURL + "collaboration/disagreeForm&stepBackFlag="+stepBackFlag+"&stepStopFlag="+stepStopFlag+"&repealItemFlag="+repealItemFlag,
	        height: 180,
	        width: 300
	    });
		if(rv=='stepBack'){
			stepBack(document.theform);
		}
		if(rv=='stepStop'){
			stepStop(document.theform);
		}
		if(rv=='goOn'){
			return false;
		}
		if(rv == 'cancel'){
			repealItem('edocSummary','${param.summaryId}');
		}
		return true;
	}
	
	function disagree(){
		var stepBackFlag=document.getElementById("stepBackFlag");
		var stepStopFlag=document.getElementById("stepStopFlag");
		var repealItemFlag = document.getElementById("repealItemFlag");
		var canStepBack=false;
		var canStepStop=false;
		var canRepealItem = false;
		if(stepBackFlag!=null&&typeof(stepBackFlag)!='undefined'){
			canStepBack=true;
		}
		if(stepStopFlag!=null&&typeof(stepStopFlag)!='undefined'){
			canStepStop=true;
		}
		if(repealItemFlag!=null&&typeof(repealItemFlag)!='undefined'){
			canRepealItem=true;
		}	
		//选择不同意，弹出选择界面
		var attitudes=document.getElementsByName("attitude");
		
		if(canStepBack || canStepStop || canRepealItem){
			for(var i=0;i<attitudes.length;i++){
				if(attitudes[i].value==3&&attitudes[i].checked){
					if(showDisAgreeForm(canStepBack, canStepStop, canRepealItem)){
						return true;
					}
				}
			}
		}
	}
	//自动定位点击列表行
	setTablePosition(parent.parent.listFrame);	
	var oHeight = parseInt(document.body.clientHeight)-50;
	initFFScroll('scrollListDiv',oHeight);
//-->
</script>
</body>
</html>