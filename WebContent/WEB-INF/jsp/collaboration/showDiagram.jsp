<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
${v3x:skin()}
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/jquery-ui.custom.css${v3x:resSuffix()}" />">
<%@ include file="../doc/pigeonholeHeader.jsp" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/common/css/layout.css${v3x:resSuffix()}" />">

<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/V3X.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript">
var alert_noFlow = "<fmt:message key='alert.sendImmediate.nowf'/>";
var alert_cannotTakeBack = "<fmt:message key='col.takeBack.flowEnd.alert' />";
</script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/collaboration/js/collaboration.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/flowperm/js/flowperm.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/menu/xmenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/workflow/workflow.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/doc/js/thirdMenu.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/js/jquery-ui.custom.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/common/jquery/jquery.plugin.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/v3xmain/js/phrase.js${v3x:resSuffix()}" />"></script>
<script type="text/javascript" charset="UTF-8" src="<c:url value="/apps_res/form/js/formdisplay/SeeyonForm.js${v3x:resSuffix()}" />"></script>
<script>
var v3x = new V3X();
v3x.init("${pageContext.request.contextPath}", "${v3x:getLanguage(pageContext.request)}");
_ = v3x.getMessage;

var genericURL = '${detailURL}';
var formAction = genericURL + "?method=finishWorkItem";
var docURL = "${docURL}";
var edocURL = "${edocURL}";
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


var hiddenMultipleRadio_wf = true;
var showAccountShortname_wf = "yes";
var showOriginalElement_wf = false;
var isConfirmExcludeSubDepartment_wf = true;

var showAccountShortname_colAssign = "yes";
var hiddenFlowTypeRadio_colAssign = true;
var showOriginalElement_colAssign = false;
var unallowedSelectEmptyGroup_colAssign = true;
var hiddenRootAccount_colAssign = true;
var isConfirmExcludeSubDepartment_colAssign = true;

var showAccountShortname_addInform = "yes";
var hiddenFlowTypeRadio_addInform = true;
var showOriginalElement_addInform = false;
var unallowedSelectEmptyGroup_addInform = true;
var hiddenRootAccount_addInform = true;
var isConfirmExcludeSubDepartment_addInform = true;
var flowSecretLevel_addInform = "${summary.secretLevel}";

var flowSecretLevel_sp = "${summary.secretLevel}";
flowSecretLevel_wf = "${summary.secretLevel}";



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
var isForm = ${bodytype =='FORM'};
var hasDiagram = "${hasDiagram}";

var currentNodeId = "${currentNodeId}";
var showMode = 0;
var isTemplete = "${isTemplete}";
var isCheckTemplete = false;
var isShowWorkflowRuleLink = ${!empty summary.workflowRule};
var affair_id = "${param.affairId}";
var summary_id = "${param.summaryId}";
var caseId = "${caseId }";
var processId = "${processId }";
var templateFlag = "${templateFlag}";
<%-- 是否有未结束的流程 --%>
<c:set value="${noFinishNewflow ne null}" var="hasNotFinishNewflow" />
</script>
<%@page import="com.seeyon.v3x.common.constants.ApplicationCategoryEnum" %>
<fmt:setBundle basename="www.seeyon.com.v3x.form.resources.i18n.FormResources" var="v3xFormI18N"/>
<c:set value="${v3x:currentUser().id}" var="currentUserId"/>
<c:set value="${from eq 'Pending' && affair.state eq 3}" var="hasSignButton"/>
<c:set var="isOfficeBodyType" value="${'OfficeWord' eq summary.bodyType or 'OfficeExcel' eq summary.bodyType}"/>
<v3x:selectPeople id="flash" panels="Department,Team,Outworker" selectType="Department,Team,Member"
                  departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
                  jsFunction="monitorFrame.dataToFlash(elements)" viewPage="selectNode4Workflow"/>

<fmt:message key='flow.node.excute.detail' var="excuteDetailLabel"/>
<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' var="printLabel"/>
<fmt:message key="common.attribute.label" bundle="${v3xCommonI18N}" var="attributeLabel" />
<v3x:selectPeople id="wf" panels="Department,Team,Outworker,RelatePeople" selectType="Department,Team,Member"
                  departmentId="${v3x:currentUser().departmentId}"
                  jsFunction="selectInsertPeople(elements)" viewPage="selectNode4Workflow"/>

<v3x:selectPeople id="addInform" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Department,Team,Member,Post,Account"
                  departmentId="${v3x:currentUser().departmentId}"
                  jsFunction="selectAddInform(elements)" viewPage="selectNode4Workflow"/>
</head>
<body onLoad="init()" onUnload="unLoad('${summary.processId}', '${param.summaryId}','${currentUserId}')" scroll="no" style="overflow: hidden;">
	<table cellpadding="0" cellspacing="0" width="100%" height="100%" class="deal_border" border="0">
		<tr class="kj_top">
			<td height="9">
				<table cellpadding="0" cellspacing="0" width="100%" height="9" class="top_td_modal_deal">
					<tr>
						<td class="top_td_modal_deal_l">&nbsp;
							
						</td>
						<td>&nbsp;
							
						</td>
						<td class="top_td_modal_deal_r">&nbsp;
							
						</td>

					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="td_bg_deal" valign="top">
				<table id="signMinDiv" style="cursor: pointer;display:  ${(extendConfig != 'false' && !hasNotFinishNewflow)?'none':''};"  border="0" width="100%" height="100%" onClick="showOrHiddenLayout('show')" cellspacing="0" cellpadding="0" >
					<tr>
						<td class="left_td_modal_deal" width="9">&nbsp;
							
						</td>
						<td valign="top" align="center">
							<div style="width:12px;margin: 5px 0px 0px 0px;line-height:20px;">
								<div class="zhankai" style="margin: 0px 0px 5px 0px;" id="img2" onClick="showOrHiddenLayout('show')"></div>
								
								<fmt:message key="col.deal.opinion"/>
							</div>
						</td>
						<td class="right_td_modal_deal" width="9">&nbsp;
							
						</td>
					</tr>
				</table>



				<table width="100%" height="100%" id="signAreaTable" class="signAreaTable" border="0" cellspacing="0" cellpadding="0" style="display:  ${(extendConfig != 'false' && !hasNotFinishNewflow)?'':'none'};">
					<tr>
						<td class="left_td_modal_deal" width="9">&nbsp;
							
						</td>
						<td class="deal_opinion_title" height="25" style="border-bottom: 1px #D7D7D7 solid;">
							<table cellpadding="0" cellspacing="0" width="100%" height="20">
								<tr>
									<td>
								   		<div class="shousuo" style=" float:left;display: ${(extendConfig == 'true' && !hasNotFinishNewflow)?'':'none'};margin-top:5px;" id="img1" onClick="showOrHiddenLayout('hidden')"></div>
								   		<span style="float: left;margin:2px 5px;">
									   		<c:set value="${v3x:_(pageContext, nodePermissionPolicy.label)}" var="perLocalName"/>
									        ${perLocalName==""?userDdefinedPolicy:perLocalName}
								   		</span>
									</td>
									<td align="right">
										<c:if test="${ templateFlag  }">
											<div class="help" id="dealExplainImage" title="<fmt:message key="node.deal.explain" />" onclick="colShowNodeExplain('${param.affairId}','${summary.templeteId}','${summary.processId}')"   ></div>
											<div id="nodeExplainDiv" style="display: none;background-color: #ffffff;height: 100px;width: 260px;z-index: 2;position: absolute;right: 30px;border: 1px solid #c7c7c7;text-align: left;" onMouseOut="">
												<table onMouseOut="	" style="width: 100%">
													<tr height="87%" style="vertical-align: top;line-height: 18px;">
														<td id="nodeExplainTd"></td>
													</tr>
													<tr height="13%" style="vertical-align: bottom;">
														<td align="right" style="padding-right: 4px;"><a onClick="hiddenNodeIntroduction()">关闭</a></td>
													</tr>
												</table>
											</div>
										</c:if>
									</td>
								</tr>
							</table>
						</td>
						<td class="right_td_modal_deal" width="9">&nbsp;
							
						</td>
					</tr>
					<c:if test="${hasSignButton == true && hasNotFinishNewflow ne 'true'}">
					<tr>
						<td colspan="3" height="25">
							<table cellpadding="0" cellspacing="0" width="100%" height="25" class="deal_toobar_m"  id="deal_table">
								<tr>
									<td class="deal_toobar_l" width="6">&nbsp;
										
									</td>
									<td style="border-bottom: 1px #fff solid;border-top: 1px #fff solid;">
										<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0"  >
											<tr>
												<td height="25" valign="middle">
													<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
														<tr>
												    		<td id="dealTD" valign="middle" style="padding: 3px 0px 0px 0px;border-bottom: 1px #D7D7D7 solid;">
												    			<c:forEach items="${commonActions}" var="theAction" varStatus="status">
												    			<c:choose>
														        <c:when test="${theAction eq 'AddNode'}">
														            <span class='deal_block' id="preInsertPeople" onClick="preInsertPeople('${param.summaryId}','${processId}','${param.affairId}','<%=ApplicationCategoryEnum.collaboration%>','${bodytype =='FORM'}')">
												                    <span class="dealicons insertPeople"></span>
												                    <fmt:message key="insertPeople.label"/>
												                    </span>
														        </c:when>
														        <c:when test="${theAction eq 'JointSign'}">
												                    <span class='deal_block' id="preColAssign" onClick="preColAssign('${param.summaryId}','${processId}','${param.affairId}')">
												                    <span class="dealicons colAssign"></span>
												                    <fmt:message key="colAssign.label"/>
												                    </span>
														        </c:when>
														        <c:when test="${theAction eq 'Return'}">
														        	<%--
														              <c:choose>
																		<c:when test="${isNewflow}">
																	        <span class="deal_block" onclick="javascript:void(null)">
												                            <span class="dealicons stepBack" style="filter: Gray();"></span>
														                    <fmt:message key="stepBack.label"/>
														                    </span>
																		</c:when>
																		<c:otherwise>
																	 --%>
																	 		<input type="hidden" value="true" id="stepBackFlag" >
														                    <span class='deal_block' id="stepBackSpan" onClick="stepBack(document.theform, '${param.summaryId}')">
														                    <span class="dealicons stepBack"></span>
														                    <fmt:message key="stepBack.label"/>
														                    </span>
														             <%--
																		</c:otherwise>
																	</c:choose>
																	--%>
														        </c:when>
														        <c:when test="${theAction eq 'RemoveNode'}">
												                    <span class='deal_block' id="preDeletePeople" onClick="preDeletePeople('${param.summaryId}','${processId}','${param.affairId}')">
										                            <span class="dealicons deletePeople"></span>
										                            <fmt:message key="deletePeople.label"/>
										                            </span>
														        </c:when>
														        <c:when test="${theAction eq 'Edit' && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
												                    <span class='deal_block' id="updateContent" onClick="updateContent('${param.summaryId}')">
										                            <span class="dealicons editContent"></span>
										                            <fmt:message key="editContent.label"/>
										                            </span>
														        </c:when>
														        <c:when test="${theAction eq 'allowUpdateAttachment' && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
												                    <span class='deal_block' id="updateAtt" onClick="updateAtt('${param.summaryId}')">
										                            <span class="dealicons updateAttachment"></span>
										                            <fmt:message key="edoc.allowUpdateAttachment"/>
										                            </span>
														        </c:when>
														        <c:when test="${theAction eq 'Infom'}">
										                		    <span class='deal_block' id="addInform" onClick="addInform('${param.summaryId}','${processId}','${param.affairId}');">
										                            <span class="dealicons addInform"></span>
										                            <fmt:message key="addInform.label"/>
										                            </span>
														        </c:when>
																<c:when test="${theAction eq 'Terminate'}">
																	<input type="hidden" value="true" id="stepStopFlag" >
															        <span class='deal_block' id="stepStopSpan" onClick="stepStop(document.theform)">
															        <span class="dealicons stepStop"></span>
															        <fmt:message key="stepStop.label"/>
															        </span>
																</c:when>
																<c:when test="${theAction eq 'Cancel'}">
																	<c:choose>
																		<c:when test="${isNewflow}">
																		<%--
																	        <span class="deal_block" onclick="javascript:void(null)">
												                            <span class="dealicons repeal" style="filter: Gray();"></span>
																			<fmt:message key="repeal.2.label"/>
																			</span>
																		--%>
																		</c:when>
																		<c:otherwise>
																			<input type="hidden" value="true" id="repealItemFlag" >
																			<span class='deal_block' id="repealItem" onClick="javascript:repealItem('showDiagram', '${param.summaryId}')">
												                            <span class="dealicons repeal"></span>
																			<fmt:message key="repeal.2.label"/>
																			</span>
																		</c:otherwise>
																	</c:choose>
															    </c:when>
																<c:when test="${theAction eq 'Forward' && v3x:hasNewCollaboration()}">
																	<span class='deal_block' id="signForward" onClick="signForward('${param.affairId}', '${param.summaryId}')">
															        <span class="dealicons transmit"></span>
															        <fmt:message key='common.toolbar.transmit.label' bundle='${v3xCommonI18N}' />
															        </span>
																</c:when>
																<c:when test="${theAction eq 'Sign' && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
															        <span class='deal_block' id="openSignature" onClick="javascript:parent.detailMainFrame.contentIframe.openSignature()"
															        title="<fmt:message key="comm.sign.introduce.label" bundle="${v3xCommonI18N}"/>">
															        <span class="dealicons signature"></span>
															        <fmt:message key='node.policy.Sign.label' bundle='${v3xCommonI18N}' />
															        </span>
																</c:when>
																<c:when test="${theAction eq 'Transform'}">
																	<html:link renderURL="/calEvent.do" var="calEventURL" />
																	<c:set var="summarySubject" value="${v3x:toHTMLWithoutSpaceEscapeQuote(summary.subject) }"/>
														        	<span class='deal_block' id="colToEvent" onClick="colToEvent('${calEventURL}','${summarySubject}','<%=ApplicationCategoryEnum.collaboration.getKey()%>','${param.affairId}','${summary.secretLevel }')">
												                    <span class="dealicons colTransformEvent"></span>
												                    <fmt:message key="colTransformEvent.label"/>
												                    </span>
															    </c:when>
															    <c:when test="${theAction eq 'SuperviseSet' && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
																	<html:link renderURL="/calEvent.do" var="calEventURL" />
														        	<span class='deal_block' id="openSuperviseWindowWhenDeal" onClick="javascript:openSuperviseWindowWhenDeal('${param.summaryId}','${summary.secretLevel}')">
												                    <span class="dealicons supervise"></span>
												                    <fmt:message key="col.supervise.operation.label"/>
												                    </span>
															    </c:when>
														    </c:choose>
													    </c:forEach>
												    	</td>
													</tr>
													</table>
												</td>
												<td align="right" valign="middle" style="border-bottom: 1px #D7D7D7 solid;">
											      	<div style="position:absolute; right:2px; top:65px; width:100px; z-index:2; background-color:#ffffff;border:1px #c7c7c7 solid;display:none;text-align:left;"
											      		id="processAdvanceDIV" onMouseOver="advanceViews(true)" onMouseOut="advanceViews(false)" oncontextmenu="return false">
										        	<c:forEach items="${advancedActions}" var="theAction" varStatus="status">
										            <div style="padding: 3px;" onMouseMove="javascript:this.className='more-deal-sel'" onMouseOut="javascript:this.className='more-deal'">
											        <c:choose>
											        	<c:when test="${theAction eq 'AddNode'}">
											                <span class='like-a deal_span_div' onClick="preInsertPeople('${param.summaryId}','${processId}','${param.affairId}','<%=ApplicationCategoryEnum.collaboration%>','${bodytype =='FORM'}')">
											                	<span class="dealicons-advance insertPeople">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
											                	<fmt:message key="insertPeople.label"/>
											                </span>
											            </c:when>
											            <c:when test="${theAction eq 'Edit' && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
										                    <span class='like-a deal_span_div' onClick="updateContent('${param.summaryId}')">
									                            <span class="dealicons-advance editContent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
									                            <fmt:message key="editContent.label"/>
															</span>
											            </c:when>
											            <c:when test="${theAction eq 'allowUpdateAttachment' && v3x:getBrowserFlagByRequest('HideBrowsers', pageContext.request)}">
										                    <span class='like-a deal_span_div' onClick="updateAtt('${param.summaryId}')">
									                            <span class="dealicons-advance updateAttachment">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
									                            <fmt:message key="edoc.allowUpdateAttachment"/>
															</span>
											            </c:when>
											            <c:when test="${theAction eq 'RemoveNode'}">
										                    <span class='like-a deal_span_div' onClick="preDeletePeople('${param.summaryId}','${processId}','${param.affairId}')">
								                            	<span class="dealicons-advance deletePeople">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
																<fmt:message key="deletePeople.label"/>
															</span>
											            </c:when>
											            <c:when test="${theAction eq 'Forward' && v3x:hasNewCollaboration()}">
										                    <span class='like-a deal_span_div' onClick="signForward('${param.affairId}', '${param.summaryId}')">
										                        <span class="dealicons-advance transmit">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
																<fmt:message key='common.toolbar.transmit.label' bundle='${v3xCommonI18N}' />
															</span>
											            </c:when>
											            <c:when test="${theAction eq 'Return'}">
										                   <%--
										                    <c:choose>
																<c:when test="${isNewflow && newflowCanNotBack}">
															        <span class="deal_span_div" onclick="javascript:void(null)">
										                            <span class="dealicons-advance stepBack" style="filter: Gray();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
																</c:when>
																<c:otherwise>
																 --%>
																 	<input type="hidden" value="true" id="stepBackFlag" >
																	<span class='like-a deal_span_div' id="stepBackSpan" onClick="stepBack(document.theform, '${param.summaryId}')">
										                            <span class="dealicons-advance stepBack">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
																<%--
																</c:otherwise>
															</c:choose>
															--%>
									                        <fmt:message key="stepBack.label"/></span>
											            </c:when>
											            <c:when test="${theAction eq 'JointSign'}">
										                    <span class='like-a deal_span_div' onClick="preColAssign('${param.summaryId}','${processId}','${param.affairId}')">
										                    <span class="dealicons-advance colAssign">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
										                    <fmt:message key="colAssign.label"/></span>
											            </c:when>
											            <c:when test="${theAction eq 'Infom'}">
										                    <span class='like-a deal_span_div' onClick="addInform('${param.summaryId}','${processId}','${param.affairId}');">
								                            <span class="dealicons-advance addInform">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
								                            <fmt:message key="addInform.label"/></span>
											            </c:when>
											            <c:when test="${theAction eq 'Terminate'}">
											            	<input type="hidden" value="true" id="stepStopFlag" >
										                    <span class='like-a deal_span_div' id="stepStopSpan" onClick="stepStop(document.theform);">
									                        <span class="dealicons-advance stepStop">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
									                        <fmt:message key="stepStop.label"/></span>
											            </c:when>
											            <c:when test="${theAction eq 'Sign' && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
													        <span class='like-a deal_span_div' onClick="javascript:parent.detailMainFrame.contentIframe.openSignature()">
													        <span class="dealicons-advance signature">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
													        <fmt:message key='node.policy.Sign.label' bundle='${v3xCommonI18N}' /></span>
														</c:when>
														 <c:when test="${theAction eq 'SuperviseSet' && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
													        <span class='like-a deal_span_div' onClick="javascript:openSuperviseWindowWhenDeal('${param.summaryId}','${summary.secretLevel}')">
													        <span class="dealicons-advance supervise">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
													        <fmt:message key="col.supervise.operation.label"/></span>
														</c:when>
											            <c:when test="${theAction eq 'Cancel'}">
															<c:choose>
																<c:when test="${isNewflow}">
																	<%--
																	<span class='deal_span_div' onclick="javascript:void(null)">
															        <span class="dealicons-advance repeal" style="filter: Gray();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
																	 --%>
																</c:when>
																<c:otherwise>
																	<input type="hidden" value="true" id="repealItemFlag">
																	<span class='like-a deal_span_div' onClick="javascript:repealItem('showDiagram', '${param.summaryId}')">
															        <span class="dealicons-advance repeal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
															        <fmt:message key="repeal.2.label"/></span>
																</c:otherwise>
															</c:choose>
												    	</c:when>
												    	<c:when test="${theAction eq 'Transform'}">
															<html:link renderURL="/calEvent.do" var="calEventURL" />
															<c:set var="summarySubject" value="${v3x:toHTMLWithoutSpaceEscapeQuote(summary.subject) }"/>
												        	<span class='like-a deal_span_div' onClick="colToEvent('${calEventURL}','${summarySubject}','<%=ApplicationCategoryEnum.collaboration.getKey()%>','${param.affairId}','${summary.secretLevel }')">
												            <span class="dealicons-advance colTransformEvent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
												        	<fmt:message key="colTransformEvent.label"/></span>
														</c:when>
												       </c:choose>
											        </div>
										        	</c:forEach>
										        	<c:if test="${v3x:getBrowserFlagByRequest('OnlyIpad', pageContext.request)}">
										        	<div style="clear: both; width: 100%; text-align: right;padding: 0 5px 5px 0">
										               <a href="javascript:advanceViews(false)"><fmt:message key="common.button.close.label"  bundle="${v3xCommonI18N}" /></a>
										            </div>
										            </c:if>
										        </div>
										         <c:if test="${!empty advancedActions}">
										            <div id='processAdvance' onClick="advanceViews(null)" class="shousuo"></div>
												 </c:if>
											    </td>
											</tr>
										</table>
									</td>
									<td class="deal_toobar_r" width="6">&nbsp;
										
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr id="signTR">
						<td class="left_td_modal_deal" width="9">&nbsp;
							
						</td>
						<td valign="top" height="100%">
							<div id="scrollDiv" class="scrollList">
							<form id="theform" name="theform" action="<html:link renderURL='/collaboration.do?method=finishWorkItem' />" method="post" style='margin: 0px' onSubmit="return false">
								<!-- 接收从弹出页面提交过来的数据 -->
								<input type="hidden" name="popJsonId" id="popJsonId" value="">
								<input type="hidden" name="popNodeSelected" id="popNodeSelected" value="">
								<input type="hidden" name="popNodeCondition" id="popNodeCondition" value="">
								<input type="hidden" name="popNodeNewFlow" id="popNodeNewFlow" value="">
								<input type="hidden" name="allNodes" id="allNodes" value="">
								<input type="hidden" name="nodeCount" id="nodeCount" value="">
								<div style="display:none" id="processModeSelectorContainer">
								</div>
								<!-- 表单提交数据  -->
								<div style="display:none;" id="saveColFieldSummary">
								</div>
								<input type="hidden" name="supervisorId" id="supervisorId" value="${supervisorId}">
								<input type="hidden" name="isDeleteSupervisior" id="isDeleteSupervisior" value="false">
								<input type="hidden" name="orgSupervisorId" id="orgSupervisorId" value="${supervisorId}">
								<input type="hidden" name="supervisors" id="supervisors" value="${supervisors}">
								<input type="hidden" name="unCancelledVisor" id="unCancelledVisor" value="${unCancelledVisor }">
								<input type="hidden" name="sVisorsFromTemplate" id="sVisorsFromTemplate" value="${sVisorsFromTemplate}">
								<input type="hidden" name="awakeDate" id="awakeDate" value="${awakeDate}">
								<input type="hidden" name="superviseTitle" id="superviseTitle" value="${superviseTitle}">
								<input type="hidden" name="count" id="count" value="${count}"/>
								<input type="hidden" name="fileUrlIds" id="fileUrlIds" value=""/>
								
								<input type="hidden" id="pushMessageMemberIds" name="pushMessageMemberIds" value="">
								<input type="hidden" id="formData" name="formData" value="">
								<input type="hidden" name="formDisplayValue" value="">
								<input type="hidden" id="formApp" name="formApp" value="">
								<input type="hidden" id="form" name="form" value="">
								<input type="hidden" id="operation" name="operation" value="">
								<input type="hidden" id="masterId" name="masterId" value="">
								<input type="hidden" id="state" name="state" value="">
								<input type="hidden" id="affair_id" name="affair_id" value="${param.affairId}"/>
								<input type="hidden" id="summary_id" name="summary_id" value="${param.summaryId}"/>
								<input type="hidden" id="startMemberId" name="startMemberId" value="${summary.startMemberId}"/>
								<input type="hidden" id="appName" name="appName" value='<%=ApplicationCategoryEnum.collaboration.getKey()%>'/>
								<input type="hidden" id="archiveId" name="archiveId" value=""/>
								<input type="hidden" id="isPipeonhole" name="isPipeonhole" value="${v3x:containInCollection(actions, 'Cancel')}" />
								<input type="hidden" id="affairId" name="affairId" value="${param.affairId}"/>
								<input type="hidden" id="draftOpinionId" name="draftOpinionId" value="${draftOpinion.id}"/>
								<input type="hidden" id="process_xml" name="process_xml" value=""/>
								<input type="hidden" id="process_desc_by" name="process_desc_by" value="xml" />
								<input type="hidden" id="currentNodeId" name="currentNodeId" value="${currentNodeId }" />
								<input type="hidden" id="isMatch" name="isMatch" value="true" />
								<input type="hidden" name="processId" id="processId" value="${processId}" />
								<input type="hidden" name="caseId" id="caseId" value="${caseId }" />
								<input type="hidden" name="attsFlag" id="attsFlag" value="${attsFlag }" />
								<input type="hidden" name="nodePermission" id="nodePermission" value="${userDdefinedPolicy}">
								<input type="hidden" id="hasSaveAttachment" name="hasSaveAttachment" value="${v3x:containInCollection(baseActions, 'UploadAttachment') || v3x:containInCollection(baseActions, 'UploadRelDoc') || v3x:containInCollection(advancedActions, 'allowUpdateAttachment')|| v3x:containInCollection(commonActions, 'allowUpdateAttachment')}">
								<input type="hidden" id="newflowType" name="newflowType" value="${newflowType}" />
								<input type="hidden" id="removeFormLock" name="removeFormLock" value="${removeFormLock }">
								<input type="hidden" id="parentformSummaryId" name="parentformSummaryId" value="${parentformSummaryId}">
								<input type="hidden" id="__ActionToken" name="__ActionToken" readonly value="SEEYON_A8" > <%-- post提交的标示，先写死，后续动态 --%>
								<span id="selectPeoplePanel"></span>

								<table cellpadding="0" cellspacing="0" width="100%">
									<tr>
										<td>
											<table cellpadding="0" cellspacing="0" width="100%" border="0">
												<tr>
												    <td id="bottomId" height="40" valign="middle">
												        <c:if test="${attitudes != 3}">
												        	<c:set var="enclude" value="${attitudes==2?'1':'' }"/>
												        	<c:set var="select" value="${attitudes==2?'2':'1' }"/>
												            <v3x:metadataItem metadata="${colMetadata['collaboration_attitude']}" showType="radio" name="attitude"
												                              selected="${draftOpinion == null ? select : draftOpinion.attitude}" enclude="${enclude }"/>
												        </c:if>
												    </td>
												    <td align="right" valign="middle">

												        <c:if test="${v3x:containInCollection(baseActions, 'CommonPhrase')}">
												            <span class="like-a" onClick="javascript:colShowPhrase()">
												                <fmt:message key="commonPhrase.label"/>
												            </span>
												       		${v3x:showCommonPhrase(pageContext)}
												        </c:if>

												    </td>
												</tr>
											</table>
										</td>
									</tr>
								</table>
								<table cellpadding="0" cellspacing="0" width="100%">
									<tr>
										<td>
												<table cellpadding="0" cellspacing="0" width="100%" class="deal_con_l">
													<tr>
														<td>
															<c:if test="${v3x:containInCollection(baseActions, 'Opinion')}">
													        	<table cellpadding="0" cellspacing="0" width="100%" class="deal_con_r">
													        		<tr>
													        			<td style="" colspan="3">
																        	<input type="hidden" id="opinionPolicy" name="opinionPolicy" value="${opinionPolicy}" />
																        	<textarea id="content" style="padding: 5px;" name="content" rows="${param.openLocation=='detailFrame'? '6':'16'}" validate="maxLength" inputName="<fmt:message key='common.opinion.label' bundle='${v3xCommonI18N}' />" maxSize="2000">${draftOpinion.content}</textarea>
													        			</td>
													        		</tr>
													        	</table>
															</c:if>
														</td>
													</tr>
												</table>

										    	<table cellpadding="0" cellspacing="0" width="100%">
													<tr>
														<td align="left" height="30" valign="middle">
															<a href="#" onClick="showPushWindow('${param.summaryId}');"><img src="<c:url value='apps_res/v3xmain/images/online.gif' />" border="0" align="absmiddle"/>&nbsp;
															<fmt:message key="message.push.label"/></a>
														</td>
													</tr>
													<%--意见隐藏 --%>
													<c:if test="${v3x:containInCollection(baseActions, 'Opinion')}">
														<tr>
															<td colspan="2" height="30" valign="middle" >
														       	<span>
															        <label for="isHidden">
															        	<input type="checkbox" name="isHidden" id="isHidden" onClick="setDisplay(this)">
															        	&nbsp;<fmt:message key="common.opinion.hidden.label" bundle="${v3xCommonI18N}" />
															        	&nbsp;
															        </label>
														        </span>
														        <span id="showToIdSpan" style="display:none;white-space: nowrap;">
														        	<fmt:message key="col.comment.not.include" />&nbsp;
														        	<input name="showToIdInput" id="showToIdInput" style=" line-height:18px;"  type="text" value="<fmt:message key='col.opinion.show'/>" readonly onClick="selectPeopleFun_sp()">
														        	<input name="showToId" id="showToId" type="hidden" value="">
														        	<v3x:selectPeople panels="Department,Team,Post,Outworker,RelatePeople" jsFunction="setSelectPeople(elements)" selectType="Member" id="sp"/>
														        </span>
															</td>
														</tr>
													</c:if>
													<%--跟踪 --%>
													<tr>
													 	<td colspan="2" height="30" valign="middle" >
													 		<c:if test="${v3x:containInCollection(baseActions, 'Track')}">
																<label for="isTrack">
														    		<input type="checkbox" name="afterSign" value="track" onClick="setTrackRadiio(this);"id="isTrack" ${v3x:outConditionExpression(affair.isTrack, 'checked', '')} >&nbsp;&nbsp;<fmt:message key="track.label" />:
														    		<label for="trackRange_all">
																		<input type="radio" disabled name="trackRange" id="trackRange_all" onClick="setTrackCheckboxChecked();" value="1" ${affair.isTrack&&empty trackIds?'checked':''}/>&nbsp;&nbsp;<fmt:message key="col.track.all" bundle="${v3xCommonI18N}" />
																	</label>
																	<label for="trackRange_part">
																		<c:set value="${v3x:parseElementsOfIds(trackIds, 'Member')}" var="mids"/>
																		<input type="hidden" value="${trackIds}" name="trackMembers" id="trackMembers"/>
																		<v3x:selectPeople id="track" panels="Department,Team,Post,Outworker,RelatePeople" selectType="Member" jsFunction="setPeople(elements)" originalElements="${mids}"/>
																		<input type="radio" disabled name="trackRange"  id="trackRange_part" onClick="selectPeopleFunTrackNewCol()" value="0" ${not empty trackIds?'checked':''}/>&nbsp;&nbsp;<fmt:message key="col.track.part" bundle="${v3xCommonI18N}" />
																	</label>
														    	</label>
													    	</c:if>
														    <c:if test="${v3x:containInCollection(baseActions, 'Archive') && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
																<label for="pipeonhole" style="white-space: nowrap;">
													                <input type="checkbox" name="afterSign" id="pipeonhole" value="pipeonhole" onClick="checkMulitSign(this)">&nbsp;&nbsp;<fmt:message key="collaboration.allow.pipeonhole.label" />
													            </label>${param.openLocation=='detailFrame'? '':''}
													            &nbsp;
													        </c:if>
												    	</td>
													</tr>
													
													<c:set var="uploadRelDoc" value="${v3x:containInCollection(baseActions, 'UploadRelDoc') }"/>
													<c:set var="uploadAttachment" value="${v3x:containInCollection(baseActions, 'UploadAttachment') }"/>
													<div id="attachmentEditInputs"></div>
													<c:if test="${uploadRelDoc || uploadAttachment}">
													<tr id="_attachment2TR">
														<td colspan="2" height="${param.openLocation=='detailFrame'? '30':'40'}"  class=" deal_padding_r">
														<div height="36">
															<c:if test="${uploadAttachment && v3x:getBrowserFlagByRequest('HideOperation', pageContext.request)}">
															<!-- 2017-3-22 诚佰公司 屏蔽协同回复插入附件
											                <a href="javascript:insertAttachment()">
																<img src="<c:url value='/common/images/attachment.gif' />" border="0" align="absmiddle">
																<fmt:message key="collaboration.deal.insert.label"/></a>
															(<span id="attachmentNumberDiv">0</span>)&nbsp;&nbsp; -->
															</c:if>
															<c:if test="${uploadRelDoc}">
															<a href="javascript:quoteDocumentTarget()">
															<img src="<c:url value='/common/images/attachment.gif' />" border="0" align="absmiddle">
															<fmt:message key="collaboration.deal.attach.label"/></a>
															(<span id="attachment2NumberDiv">0</span>)
															</c:if>
														</div>
															<table>
																<tr id="attachment2TR">
																	<td>
																		<div id="attachment2Area" style="overflow: auto;"></div>
																		<script type="text/javascript">showAttachment('${draftOpinion.id}', 2, 'attachment2TR', 'attachment2NumberDiv');</script>
																	</td>
																</tr>
															</table>
															<%--不要随意更改这两个附件区的位置，关联文档可能显示不出来。 --%>
															<table>
																<tr id="attachmentTR" style="display:none">
																	<td>
																		<v3x:fileUpload attachments="${draftOpinionAtts}" applicationCategory="1" />
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													</c:if>
												</table>

												<table width="100%" cellpadding="0" cellspacing="0" style="border-top: 1px #d1d1d1 solid;">
													<c:choose>
														<c:when test="${isAudit ne 'true' && isIssus ne 'true' && isVouch ne 'true' }">
														<tr>
														    <td colspan="2" class="col-process deal_padding_r" valign="top" id="doSignTr"  style="border-top: 1px #fff solid;">
														    	<c:if test="${removeContinue!=true }">
																<input id="processButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onClick="finishWorkItem(this.form,formAction,'${summary.secretLevel}')"
														               value='<fmt:message key="common.button.submit.label" bundle="${v3xCommonI18N}" />'
														               >
														        </c:if>
														        <%-- 存为草稿按钮 --%>
													            <c:if test="${v3x:containInCollection(baseActions, 'Opinion') && isAudit ne 'true' && v3x:containInCollection(baseActions, 'Comment')}">
																&nbsp;&nbsp;<input id="savedraftButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="saveDraftOpinion.label" />'  onclick='saveDraftOpinion(this)'>
														        </c:if>
														        <c:if test="${v3x:containInCollection(baseActions, 'Comment')}">
																	&nbsp;&nbsp;<input id="zcdbButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="zancundaiban.label" />'
															                onclick='doZcdb(this)'>
														        </c:if>
														    </td>
														</tr>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${isAudit eq 'true'}">
																<tr>
																    <td colspan="2"  class="col-process deal_padding_r" valign="top">
																    <c:if test="${removeContinue!=true }">
																        <input id="processButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onClick="doAudit(this.form,0)"
																               value='<fmt:message key="flowBind.audit.pass" bundle="${v3xFormI18N}" />'
																               >
																            &nbsp;&nbsp;<input id="auditBack" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="flowBind.audit.back" bundle="${v3xFormI18N}" />'
																                    onclick='doAudit(this.form,1)'>
																         <c:if test="${v3x:containInCollection(baseActions, 'Opinion') && isAudit ne 'true' && v3x:containInCollection(baseActions, 'Comment')}">
																		&nbsp;&nbsp;<input id="savedraftButton" type="button" value='<fmt:message key="saveDraftOpinion.label" />'  onmouseover="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onclick='saveDraftOpinion(this)'>
																        </c:if>
																         &nbsp;&nbsp;<input id="zcdbButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="zancundaiban.label" />'
																	              onclick='doZcdb(this)'>
																    </c:if>
																    </td>
																</tr>
																</c:when>
																<c:when test="${isVouch eq 'true'}">
																<tr>
																    <td colspan="2"  class="col-process deal_padding_r" valign="top">
																    <c:if test="${removeContinue!=true }">
																        <!-- <input id="refreshButton" type="button" class="deal_btn" onmouseover="javascript:this.className='deal_btn_over'" onmouseout="javascript:this.className='deal_btn'" onclick="doRefresh(this.form)"
																               value='<fmt:message key="flowBind.vouch.refresh" bundle="${v3xFormI18N}" />'  >
																        &nbsp;
																        -->
																        <input id="vouchPass" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="flowBind.vouch.pass" bundle="${v3xFormI18N}" />'
																                    onclick='doVouch(this.form,0)'>
																		&nbsp;<input id="vouchBack" type="button"  class="deal_btn" value='<fmt:message key="flowBind.vouch.back"  bundle="${v3xFormI18N}" />'  onmouseover="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onclick='doVouch(this.form,1)'>
																         &nbsp;<input id="zcdbButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="zancundaiban.label" />'
																	              onclick='doZcdb(this)'>
																    </c:if>
																    </td>
																</tr>
																</c:when>
																<c:otherwise>
																<tr>
																    <td colspan="2" class="col-process deal_padding_r">
																        <input id="processButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onClick="auditPass(this.form, formAction, '${nodePermissionPolicy.value}', '${summary.id}', '${param.affairId}', '${v3x:currentUser().id}', '${v3x:currentUser().accountId}')"
																               value='<fmt:message key="audit.pass.issus" bundle="${v3xCommonI18N}" />'
																              >&nbsp;&nbsp;
																		<input id="auditNoPassButton" type="button" class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" onClick="auditNoPass(this.form, formAction)"
																               value='<fmt:message key="audit.back" bundle="${v3xCommonI18N}" />'
																               >
																		<c:if test="${v3x:containInCollection(baseActions, 'Comment')}">
																			&nbsp;&nbsp;<input id="zcdbButton" type="button"  class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="zancundaiban.label" />'
																	               onclick='doZcdb(this)'>
																	    <c:if test="${v3x:containInCollection(baseActions, 'Opinion') && isAudit ne 'true' && v3x:containInCollection(baseActions, 'Comment')}">
																		&nbsp;&nbsp;<input id="savedraftButton" type="button"  class="deal_btn" onMouseOver="javascript:this.className='deal_btn_over'" onMouseOut="javascript:this.className='deal_btn'" value='<fmt:message key="saveDraftOpinion.label" />'  onclick='saveDraftOpinion(this)'>
														        		</c:if>
																        </c:if>
																    </td>
																</tr>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</table>

										</td>
									</tr>
								</table>
							</form>
							<div id="formContainer" style="display:none"></div>
							<iframe name="showDiagramFrame" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
							</div>
						</td>
						<td class="right_td_modal_deal" width="9">&nbsp;
							
						</td>
					</tr>
					</c:if>
				</table>
				<%-- --
				<table cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td class="left_td_modal_deal" width="9">
							&nbsp;
						</td>
						<td>
							&nbsp;
						</td>
						<td class="right_td_modal_deal" width="9">
							&nbsp;
						</td>
					</tr>
				</table>
				 --%>

			</td>
		</tr>
	</table>
</body>






<script type="text/javascript">
<!--

var newflowType = "${newflowType}";

//异步加载流程信息
var isLoadProcessXML = false;
var caseProcessXML = "";
var caseLogXML = "";
var caseWorkItemLogXML = "";

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

function setCenterDiv(){
	var scrollDiv = document.getElementById('scrollDiv');
       //var posX = scrollDiv.offsetLeft;
       var posY = scrollDiv.offsetTop;
       var aBox = scrollDiv;
       do {
           aBox = aBox.offsetParent;
           //posX += aBox.offsetLeft;
           posY += aBox.offsetTop;
       }
       while (aBox.tagName != "BODY");
       var oHeight = parseInt(document.body.clientHeight)-posY;
       if(scrollDiv && oHeight>0){scrollDiv.style.height = oHeight+"px";}
}
function addResize(){
	if(document.all){
        window.attachEvent("onresize",setCenterDiv);
        window.attachEvent("onfocus",setCenterDiv);
    }else{
    	window.addEventListener("resize",setCenterDiv,false);
    	window.addEventListener("focus",setCenterDiv,false);
	}
	setCenterDiv();
}
function showOrHiddenLayout(type){
	try{
	if(type == "show"){
		var img1 = document.getElementById('img1');
		var img2 = document.getElementById('img2');
		var signAreaTable = document.getElementById('signAreaTable');
		var signMinDiv = document.getElementById('signMinDiv');
		if(img1) img1.style.display='';
		if(img2) img2.style.display='none'
		if(signAreaTable) signAreaTable.style.display='';
		if(signMinDiv) signMinDiv.style.display='none'
		parent.document.getElementById("zy").cols = "*,350";
			
	}else if(type == "hidden"){
		var img1 = document.getElementById('img1');
		var img2 = document.getElementById('img2');
		var signAreaTable = document.getElementById('signAreaTable');
		var signMinDiv = document.getElementById('signMinDiv');
		if(img1) img1.style.display='none';
		if(img2) img2.style.display=''
		if(signAreaTable) signAreaTable.style.display='none';
		if(signMinDiv) signMinDiv.style.display=''
		parent.document.getElementById("zy").cols = "*,45";
		
	}
	setCenterDiv();
	//隐藏重复表的增加按钮
	parent.document.getElementById("detailMainFrame").contentWindow.document.getElementById("contentIframe").contentWindow.SeeyonForm_HideArrow();
	}catch(e){return;}
}
function init(){
	var unallowedFlag = "${unallowedFlag}";
	if(unallowedFlag && unallowedFlag == "true"){
		if(document.getElementById("pipeonhole"))
			document.getElementById("pipeonhole").disabled = true;
	}
	if("${hasNotFinishNewflow}" =="true"){
		alert(_("collaborationLang.warn_newflowIsNotEnd_cannotProcess", "${noFinishNewflow}"));
	}

	var oSupervise = document.getElementById('buttonsupervis');
	addResize();
	document.getElementById("supervisorId").value = parent.supervisorId;
	document.getElementById("awakeDate").value = parent.awakeDate;
	document.getElementById("supervisors").value = parent.supervisors;
	document.getElementById("superviseTitle").value = parent.superviseTitle;
	document.getElementById("count").value = parent.count;
	document.getElementById("unCancelledVisor").value = parent.unCancelledVisor;
}

	
<%-- 关闭窗口或刷新页面，finishworkitem执行成功后回调 --%>
function closeWindow(){
	parent.isSubmitFinished = true;
	if(getA8Top().window.dialogArguments){
		getA8Top().window.returnValue = "true";
		getA8Top().window.close();
	}
	else{
		try{
			parent.parent.listFrame.location.href=parent.parent.listFrame.location.href
		}catch(e){
			parent.getA8Top().reFlesh();
		}
	}
}

function unLoad(processId, summaryId,userId){
	if("${param.from}" != "WaiSend"){
		colDelLock(processId, summaryId,userId);
	}
}



//分支
//var branchs = new Array();
var team = new Array();
var secondpost = new Array();
var startTeam = new Array();
var startSecondpost = new Array();
<c:if test="${teams != null}">
	<c:forEach items="${teams}" var="team">
		team["${team.id}"] = "${team.id}";
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

<%-- 新流程设置相关信息 End--%>

function mainpp(){
	var a = "<c:url value='/apps_res/form/css/SeeyonForm.css'/>";
	mainprint(a);
}

function updateContent(summaryId)
{
  var theForm = document.theform;
  if(!checkModifyingProcessAndLock(theForm.processId.value, summaryId)){
	 return;
  }
  parent.detailMainFrame.contentIframe.modifyBody(summaryId,hasSign);

  if("${isOfficeBodyType}" == 'true'){
	//防护停止Office转换html功能产生空指针现象
	  var edocContentDiv=parent.detailMainFrame.contentIframe.document.getElementById("edocContentDiv");
	  var nestContentDiv=parent.detailMainFrame.contentIframe.document.getElementById("nestContentDiv");
	  try{
		  if(edocContentDiv!=null&&typeof(edocContentDiv)!="undifined"){
			  edocContentDiv.style.display = "block";
			  if(nestContentDiv!=null&&typeof(nestContentDiv)!="undifined"){
				  nestContentDiv.style.display = "none";
			  }
		  }
	  }catch(e){}
   }
}
var hasSign=${v3x:containInCollection(commonActions, 'Sign') || v3x:containInCollection(advancedActions, 'Sign')};


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
phraseType = "col";

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
	flowSecretLevel_track = "${summary.secretLevel}";
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
$(function(){
  $("#content").focus(function(){
	$("#nodeExplainDiv").hide();
  });
});

<c:choose>
	<c:when test="${param.preAction eq 'insertPeople' || param.preAction eq 'deletePeople' || param.preAction eq 'stepBack' || param.preAction eq 'takeBack' || param.preAction eq 'colAssign' || param.preAction eq 'addInform' }">
	    changeLocation(panels.get(1).id);
	    showPrecessArea();
	</c:when>
</c:choose>
function setDisplay(v){
	var showToIdSpan = document.getElementById("showToIdSpan");
	if(v.checked){
		if(showToIdSpan){
			showToIdSpan.style.display = "inline-block";
		}
	}else{
		if(showToIdSpan){
			showToIdSpan.style.display = "none";
			
			var showToId = document.getElementById("showToId");
			var showToIdInput = document.getElementById("showToIdInput");
			
			showToIdInput.value="<fmt:message key='col.opinion.show'/>";
			showToIdInput.title="";
			showToId.value="";
			elements_sp="";
		}
	}
}
//选择意见公开人的选人界面回调函数
function setSelectPeople(elements){
	var names = getNamesString(elements);
	var ids = getIdsString(elements,false);
	var showToId = document.getElementById("showToId");
	var showToIdInput = document.getElementById("showToIdInput");
	if(showToId){
		showToId.value = ids;
	}
	if(showToIdInput){
		showToIdInput.value = names;
		showToIdInput.title = names;
	}
}
var process_xml = document.getElementById("process_xml");
if (process_xml)
	process_xml.value = caseProcessXML;
//-->
</script>
</html>
