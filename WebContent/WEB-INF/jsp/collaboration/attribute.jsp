<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../common/INC/noCache.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="Collaborationheader.jsp" %>
<title><fmt:message key="common.attribute.label" bundle="${v3xCommonI18N}" /></title>
</head>
<body scroll="no" onkeydown="listenerKeyESC()">
 <table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="bg-advance-middel" height="">
			<div class="scrollList" style="height:450px;">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>&nbsp;</td>
			</tr>
	          <tr>
	          <td>
	            <fieldset>
	            <legend>${v3x:_(pageContext, "common.basic.attribute.label")}</legend>
	            	<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
	                 <c:if test="${iscol}">
		                <tr>
		                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "common.importance.label")}:</td>
		                    <td width="70%" class="attribute-right"><input type="text" readonly="readonly"
		                                                                   value='<v3x:metadataItemLabel metadata="${importanceMetadata}" value="${summary.importantLevel}" />'
		                                                                   class="input-100per">
		                </tr>
	                </c:if>
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "process.cycle.label")}:</td>
	                    <td width="70%" class="attribute-right"><input type="text" readonly="readonly"
	                                                                   value='<v3x:metadataItemLabel metadata="${deadlineMetadata}" value="${summary.deadline}" />'
	                                                                   class="input-100per">
	                </tr>
	                <c:if test="${v3x:getSysFlagByName('is_gov_only')=='true'}">
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "process.deadlineTime.label")}:</td>
	                    <td width="70%" class="attribute-right">
	                    	<c:choose>
	                    		<c:when test="${affairApp==1}">
				                    <input type="text" readonly="readonly" value='${v3x:showDeadlineTime(summary.createDate,summary.deadline)}' class="input-100per">
	                    		</c:when>
	                    		<c:otherwise>
	                    			<input type="text" readonly="readonly" value='${v3x:showDeadlineTime(summary.createTime,summary.deadline)}' class="input-100per">
	                    		</c:otherwise>
	                    	</c:choose>
	                </tr>
	                </c:if>
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "common.remind.time.label")}:</td>
	                    <td width="70%" class="attribute-right"><input type="text" readonly="readonly"
	                                                                   value='<v3x:metadataItemLabel metadata="${comMetadata}" value="${summary.advanceRemind}" />'
	                                                                   class="input-100per">
	                </tr>
	                <tr>
	                    <td width="30%" class="attribute-left"><fmt:message key="collaboration.secret.flowsecret"/>:</td>
	                    <td width="70%" class="attribute-right"><input type="text" readonly="readonly"
	                                                                   value="${secret}"
	                                                                   class="input-100per">
	                </tr>
	                <c:if test="${iscol}">
		                <tr>
		                    <td class="attribute-left">${v3x:_(pageContext, "project.label")}:</td>
		                    <td class="attribute-right">
		                    	<c:if test="${empty projectName}">
			                    	<c:set value="${v3x:_(pageContext, 'common.default')}" var="projectName" />
			                    </c:if>
		                    	<input type="text" readonly="readonly" value="${v3x:toHTML(projectName) }" class="listcell" title="${v3x:toHTML(projectName)}" />
		                    </td>
		                </tr>
	                </c:if>
	                <tr>
	                    <td class="attribute-left">${v3x:_(pageContext, "pigeonhole.label.to")}:</td>
	                    <td class="attribute-right">
		                    <c:if test="${empty archiveName}">
		                    	<c:set value="${v3x:_(pageContext, 'common.default')}" var="archiveName" />
		                    </c:if>
	                   		<input type="text" readonly="readonly" value="${v3x:toHTML(archiveName)}" class="listcell" title="${v3x:toHTML(archiveName) }" />
	                    </td>
	                </tr>
	                <tr>
	                </td>
	                </table>
	                </fieldset>
	           </td>
	           </tr>
	           <tr>
				<td>&nbsp;</td>
			   </tr>
	           <tr>
	           <td>
	           <fieldset>
	           <legend>${v3x:_(pageContext, "common.coll.state.label")}</legend>
	           <table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "common.flow.state.label")}:</td>
	                    <td width="70%" class="attribute-right">
	                    	<input type="text" readonly="readonly" value="${flowState }" class="input-100per">
	                    </td>
	                </tr>
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "common.send.time.label")}:</td>
	                    <td width="70%" class="attribute-right">
	                    	<input type="text" readonly="readonly" value="<fmt:formatDate value="${startDate}" pattern="${datePattern}" />" class="input-100per">
	                    </td>
	                </tr>
	                <c:if test="${affair.state == 3 || affair.state == 4}"><%-- 待办和已办才显示 --%>
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "col.time.receive.label")}:</td>
	                    <td width="70%" class="attribute-right">
	                    	<input type="text" readonly="readonly" value="<fmt:formatDate value="${affair.receiveTime}" pattern="${datePattern}" />" class="input-100per">
	                    </td>
	                </tr>
	                </c:if>
	                <tr>
	                    <td width="30%" class="attribute-left">${v3x:_(pageContext, "node.isovertoptime.label")}:</td>
	                    <td width="70%" class="attribute-right">
	                    	<input type="text" readonly="readonly" value="${isOvertopTime }" class="input-100per">
	                    </td>
	                </tr>
	                <tr>
	              </table>
	              </fieldset>
	              </td>
	              </tr>
	              <tr>
					<td>&nbsp;</td>
				  </tr>
				  <c:if test="${!empty(awakeDate) }">
				  <tr>
				   <td>
	            	<fieldset>
	              		<legend><fmt:message key='common.toolbar.supervise.label' bundle='${v3xCommonI18N}' /></legend>
	              		<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
	                		<tr>
	                   		 	<td width="30%" class="attribute-left"><fmt:message key="supervise.person.label" />:</td>
	                    		<td width="70%" class="attribute-right">
	                    			${v3x:showOrgEntitiesOfIds(supervisorIds,"Member",pageContext) }
	                    		</td>
	                		</tr>
	                		<tr>
	                			<td width="30%" class="attribute-left"><fmt:message key="col.supervise.deadline" />:</td>
	                			<td width="70%" class="attribute-right">
	                    			<fmt:formatDate value="${awakeDate }" pattern="${datePattern }"/>
	                    		</td>
	                		</tr>
	                	</table>
	                </fieldset>
	               </td>
	             </tr>
	             <tr>
					<td>&nbsp;</td>
				  </tr>
	            </c:if>
		        <c:if test="${not empty display}">
				<tr>
					<td>
						<fieldset>
							<legend><fmt:message key="form.bind.label" /></legend>
							<table width="100%" align="center">
								<tr>
									<td width="30%" class="attribute-left"><fmt:message key="form.bind.formAndOperation" />:</td>
									<td width="70%" class="attribute-right">
										<label>
								    		<input type="text" readonly="readonly" value="${display}" class="input-100per">
								    	</label>
						    		</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			 </c:if>
			 <tr>
					<td>&nbsp;</td>
			</tr>
	            <c:if test="${iscol}">
		        	<tr>
		              <td>
		              <fieldset>
		              <legend>${v3x:_(pageContext, "common.purview.option.label")}</legend>
		              <table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
		                <tr>
		                    <td>&nbsp;</td>
		                    <td>
		                        <div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canForward" ${v3x:outConditionExpression(canForward, 'checked', '')}>
		                            ${v3x:_(pageContext,"collaboration.allow.transmit.label")}
		                        </div>
		
		                        <div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canModify" ${v3x:outConditionExpression(canModify, 'checked', '')}>
		                            ${v3x:_(pageContext, "collaboration.allow.chanage.flow.label")}
		                        </div>
		
		                        <div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canEdit" ${v3x:outConditionExpression(canEdit, 'checked', '')}>
		                            ${v3x:_(pageContext, "collaboration.allow.edit.label")}
		                        </div>
								
								<div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canEditAttachment" ${v3x:outConditionExpression(canEditAttachment, 'checked', '')}>
		                            ${v3x:_(pageContext, "collaboration.allow.edit.attachment.label")}
		                        </div>
		                        
		                        <div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canArchive" ${v3x:outConditionExpression(canArchive, 'checked', '')}>
		                            ${v3x:_(pageContext, "collaboration.allow.pipeonhole.label")}
		                        </div>
		                        
		                        <div style="height: 28px;">
		                            <input type="checkbox" disabled
		                                   name="canAutoStopFlow" ${v3x:outConditionExpression(summary.canAutoStopFlow, 'checked', '')}>
		                            ${v3x:_(pageContext, "collaboration.allow.autostopflow.label")}
		                        </div>
		                        
		                    </td>
		                </tr>
	                </table>
	                </fieldset>
	              </td>
	            </tr>
	          </c:if>
    		 </table>
			</div>
		</td>
	</tr>
	<tr>
		<td height="42" align="right" class="bg-advance-bottom">
			<input type="button" onclick="window.close()" value="<fmt:message key='common.button.close.label' bundle='${v3xCommonI18N}' />" class="button-default-2"> 
		</td>
	</tr>
</table>
</body>

</body>
</html>