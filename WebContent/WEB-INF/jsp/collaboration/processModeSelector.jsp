<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://v3x.seeyon.com/taglib/core" prefix="v3x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../common/INC/noCache.jsp" %>
<table class="popupTitleRight" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" class="PopupTitle"><fmt:message key="common.node.select.people.label" bundle="${v3xCommonI18N}" /><c:if test="${param.hasNewflow eq 'true'}">/ <fmt:message key="newflow.label" /></c:if>
		</td>
	</tr>
	<tr>
		<td class="bg-advance-middel" >
            <div id="div1" class="scrollList" width="100%">	
 <%-- 以上为头部分--%>
            
<div id="processModeSelectPanel">
<%-- 国际化：点击此处选择人员 --%>
<fmt:message key="common.default.selectPeople.value" var="defaultSP" bundle="${v3xCommonI18N}"/>
<c:set value="${v3x:_(pageContext, 'common.separator.label')}" var="separator" />
<c:set value=",'${templateId }'" var="addTemplate"/>
<c:set value="" var="faileNodeIds"/>
<c:set value="true" var="isAllReadOnly" />
<script type="text/javascript">
<%--
//这个js变量暂时没用到？
var MatchPeoples = {};
//--%>
</script>
<c:if test="${selectorModelNodeIds ne null}">
	<div id="conditionDiv">
	<fieldset style="padding-bottom: 4px;">
		<legend><span style="color:black"><fmt:message key="common.node.select.people.label" bundle="${v3xCommonI18N}" /></span></legend>
		<%--为了避免全部隐藏后，边框不见了，加上这个隐藏域lgd，仅仅是为了显示边框--%>
		<div id="lgd" style="display:none">&nbsp;</div>
<%-- 循环遍历所有分支节点(包含条件分支):开始 --%>
		<c:forEach items="${selectorModelNodeIds}" var="nodeId">
			<c:set value="${selectorModels[nodeId]}" var="selectorModel" />
			<c:set value="${selectorModel.addition}" var="addition" />
			<c:set value="${selectorModel.condition ne null}" var="hasPatch" />
			<c:set value="manual_select_node_id${nodeId}" var="id" />
			<c:set value="condition${nodeId}" var="conditionName"/>
			<c:set value="${selectorModel.fromIsInform}" var="fromIsInform"/>
			
			<c:if test="${hasPatch || addition.readOnly == false}">
				<c:set value="false" var="isAllReadOnly" />
			</c:if>
		
			<div id="d${nodeId}">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="20" height="30" nowrap="nowrap" class="link-blue" style="cursor: default;">
					<c:choose>
						<c:when test="${hasPatch}">
						<%-- 条件分支流程节点Id --%>
						<input type="checkbox" name="${conditionName}" id="${nodeId}"
						onClick="javascript:if(this.checked){document.getElementById('td${nodeId}').style.display='block';}else{document.getElementById('td${nodeId}').style.display='none';}toggleDynamicSelector();" /></c:when>
						<c:otherwise>&nbsp;</c:otherwise>
					</c:choose>
				</td>
				<td width="280" style="padding-top: 4px; padding-left: 2px;" nowrap="nowrap">
                	<b>${v3x:toHTML(selectorModel.nodeName)}</b>(<fmt:message key="node.${(addition ne null) ? addition.processMode : selectorModel.processMode}.mode" bundle="${v3xCommonI18N}"/>)
					<label class="like-a" onclick="showBranchDesc('${selectorModel.link}'${templateId!=null?addTemplate:'' })" id="p${nodeId}"></label>
					<input type="hidden" name="invalidateNodeId" id="invalidateNodeId${nodeId}" value="${nodeId}"
					 nodeName="${v3x:toHTML(selectorModel.nodeName)}"
					<c:if test="${invalidateActivityMap[nodeId] ne null }">invalidate="true"</c:if>
					<c:if test="${invalidateActivityMap[nodeId] eq null }">invalidate="false"</c:if>
					>
					<c:if test="${invalidateActivityMap[nodeId] ne null }"><font color="red"><fmt:message key="col.branch.pepoleinvalidate"/></font></c:if>
					<%-- 隐藏节点类型和原始条件--%>
					<input type="hidden" name="nodeTypeId" id="nodeTypeId${nodeId}" value="${nodeTypes[nodeId]}" nodeId="${nodeId}" >
					<input type="hidden" name="nodeConditionId${nodeId}" id="nodeConditionId${nodeId}" value="${selectorModel.condition}">
				</td>
				<td nowrap="nowrap" align="right" style="padding-right: 4px;">
				<div id="td${nodeId}">
					<%-- 如果流程节点为非全体、竞争执行(具体人、单人执行、多人执行)的做如下处理 --%>
					<c:if test="${addition ne null}">
		                <div id="selector${nodeId}">
		                <%-- 手工选择的人员的流程节点Id --%>
		                <input type="hidden" id="manual_select_node_id" name="manual_select_node_id" value="${nodeId}"/>
		               	<c:set value="${addition.processMode == 'single' ? '1' : 'N'}" var="singleOrMany" />
		                <c:choose>
		                	<%-- 对流程节点上指定的参与者下是否有具体的人员进行判断，如果有具体的人员，则进行下面的处理 --%>
		                    <c:when test="${!empty addition.people}">
			                    <c:choose>
			                    	<%-- 流程节点是否有具体执行人 :具体人，只将具体人显示出来，不提供选人操作--%>
				                    <c:when test="${addition.readOnly == true}">
				                    	<%-- 人员Id --%>
				                    	<input id="${id}" name="${id}" type="hidden" value="${addition.people[0].id}" fromIsInform="${fromIsInform}" negativeNodes="${addition.negativeNodes}" sourceInformNodes="${addition.sourceInformNodes}" nodeId="${nodeId}"/>
				                    	<fmt:message key="node.executor" />:
				                    	<%-- 显示具体 --%>
				                    	<input type="text" value="${v3x:showMemberName(addition.people[0].id)} " readonly style="width: 180px;" validate="" validates=""/>
				                    </c:when>
				                    <%-- 单人执行模式:提供下拉列表进行选人操作 --%>
				                    <c:when test="${addition.processMode == 'single'}">
				                    	<%-- 人员Id --%>
				                    	<input id="${id}" name="${id}" type="hidden" inputName="${addition.nodeName}<fmt:message key="node.executor" />" validate="notNull" validates="notNull" fromIsInform="${fromIsInform}" negativeNodes="${addition.negativeNodes}" sourceInformNodes="${addition.sourceInformNodes}" nodeId="${nodeId}"/>
				                    	<fmt:message key="node.executor" />:
				                    	<%-- 选择流程节点的执行人 --%>
				                        <select onchange="setSelectValue(this, '${id}')" name="${id}_" id="${id}_" ${singleOrMany == 'N' ? 'multiple size=3' : ''} style="width: 180px;">
						                    <option value=""><fmt:message key="node.select.executor.label" /></option>
				                            <c:forEach items="${addition.people}" var="p">
				                            	<c:set var="m" value="${v3x:getOrgEntity('Member', p.id)}" />
				                                <option value="${p.id}" title="<c:out value='${v3x:showOrgMemberAltWithFullDeptPath(m)}' escapeXml="true" />"><c:out value='${v3x:showOrgMemberName(m)}' escapeXml="true" /></option>
				                            </c:forEach>
				                        </select>
				                    </c:when>
				                    <%-- 多人执行:提供弹出人员列表页面进行选人操作 --%>
				                    <c:otherwise>
				                    	<%-- 人员选择标签,返回选择的人员Id字符串，多个人员之间用逗号隔开 --%>
				                    	<c:set value="${v3x:joinWithSpecialSeparator(addition.people, 'id', ',')}" var="pId" />
				                    	<%-- 人员Id --%>
				                    	<input id="${id}" name="${id}" type="hidden" value="" fromIsInform="${fromIsInform}" negativeNodes="${addition.negativeNodes}" sourceInformNodes="${addition.sourceInformNodes}" nodeId="${nodeId}"/>
				                    	<input id="${id}pId" type="hidden" value="${pId}" />
				                        <fmt:message key="node.executor" />:
				                        <input id="${id}Name" name="${id}Name" value="${defaultSP}" title="${defaultSP}"
				                            readonly class="cursor-hand listcell" onclick="selectMatchPeople('${id}')" style="width: 180px;"
				                            inputName="<c:out value='${addition.nodeName}' escapeXml="true" /><fmt:message key="node.executor" />" deaultValue="${defaultSP}" validate="notNull,isDeaultValue" validates="notNull,isDeaultValue" onAfterAlert="selectMatchPeople('${id}')" />
				                    </c:otherwise>
			                   	</c:choose>
		                    </c:when>
		                    <%-- 其他情况：如果流程节点上指定的参与者下没有具体的人员，则进行下面的处理，提供弹出选人界面，供用户指定具体执行人员 --%>
		                    <c:otherwise>
		                    	<%-- 人员Id --%>
		                        <input id="${id}" name="${id}" type="hidden" value="" />
		                        <fmt:message key="node.executor" />:
		                        <input id="${id}Name" name="${id}Name" value="${defaultSP}" deaultValue="${defaultSP}" 
		                            readonly class="cursor-hand" onclick="selectPeople('${singleOrMany}', '${id}')" style="width: 180px;"
		                            inputName="<c:out value='${addition.nodeName}' escapeXml="true" /><fmt:message key="node.executor" />" validate="isDeaultValue,notNull"  validates="isDeaultValue,notNull" onAfterAlert="selectPeople('${singleOrMany}', '${id}')">
		                    </c:otherwise>
		            	</c:choose>
		            	</div>
					</c:if>
					</div>
				</td>
			</tr>
		</table>
		</div>
		<%-- 如果该流程节点是条件分支节点，则将该流程节点的条件计算表达式拼接起来，存放到本页面的隐藏域中，为页面的条件匹配提供基础 --%>
		<c:if test="${hasPatch}">
			<c:set var="condition" value="${selectorModel.condition}" />
			<c:set var="jsVar" value="${'v'}${fn:replace(nodeId,'-','_')}" />
			<c:set value="${selectorModel.force == 'true' ? 'true' : showbranchButton}" var="showbranchButton"/>
			<c:set value="${selectorModel.conditionType}" var="conditionType"/>
			<c:choose>
			<c:when test="${fromColsupervise!='1'}">
			    <c:choose>
				<c:when test="${conditionType!=2}">
				    <input type="hidden" id="scripts" name="scripts" value="var ${jsVar} = eval('${condition}');if(${jsVar}==false) hiddenFailedCondition('${nodeId}'); if(${jsVar}==true) document.getElementById('${nodeId}').setAttribute('checked', 'true'); else document.getElementById('${nodeId}').removeAttribute('checked'); document.getElementById('${nodeId}').disabled = ${selectorModel.force};if(${jsVar}) document.getElementById('p${nodeId}').innerHTML = '[<fmt:message key="col.branch.sucess"/>]'; else document.getElementById('p${nodeId}').innerHTML = '[<fmt:message key="col.branch.faile"/>]';if(${jsVar}==true) document.getElementById('hasBranchSelected').innerHTML='yes';">
				</c:when>
				<c:otherwise>
                    <input type="hidden" id="scripts" name="scripts" value="document.getElementById('${nodeId}').checked = false;document.getElementById('${nodeId}').disabled = ${selectorModel.force};if(${selectorModel.force}==true) hiddenFailedCondition('${nodeId}'); document.getElementById('p${nodeId}').innerHTML = '[<fmt:message key="col.branch.hand"/>]';">
				</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<input type="hidden" id="scripts" name="scripts" value="var ${jsVar} = true;document.getElementById('${nodeId}').checked = ${jsVar};if(${jsVar}) document.getElementById('p${nodeId}').innerHTML = '[<fmt:message key="col.branch.sucess"/>]'; else document.getElementById('p${nodeId}').innerHTML = '[<fmt:message key="col.branch.faile"/>]';">
			</c:otherwise>
			</c:choose>
		</c:if>
		
		</c:forEach>
<%-- 循环遍历所有分支节点(包含条件分支):结束 --%>
		<%-- 存放所有流程节点拼接的字符串，例如001:002: --%>
      	<input type="hidden" id="allNodes" name="allNodes" value="${allNodes}">
      	<%-- 存放节点的个数，例如：2 --%>
      	<input type="hidden" id="nodeCount" name="nodeCount" value="${nodeCount }">
      	<%-- 在页面结合表单数据匹配不成功的条件分支将放在这个div下 --%>
      	<div id="failedCondition" style="display:none"></div>
      	<c:if test="${selectorModelNodeIdsSize eq '0' }">
      	<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="100%" height="30" nowrap="nowrap" class="link-blue" style="cursor: default;">
					<%--无 --%>
					<fmt:message key="common.node.select.people.nomatchlabel" bundle="${v3xCommonI18N}" />
				</td>
			</tr>
      	</table>
      </c:if>
	</fieldset>
	</div>
</c:if>
<c:if test="${selectorModelNodeIdsSize eq '0' }">
	<br>
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td width="100%" height="30" nowrap="nowrap" class="link-blue" style="cursor: default;">
				<%--提示：您好，流程匹配成功，可以单击[确定]按钮进行提交操作。 --%>
				<b><fmt:message key="common.node.select.people.nomatchtiplabel" bundle="${v3xCommonI18N}" /></b>
			</td>
		</tr>
     </table>
</c:if>
<%-- 参数selectorModelNodeIdsSize用来标志是否可以提交了，为0表示可以提交了 --%>
<input type="hidden" id="selectorModelNodeIdsSize" name="selectorModelNodeIdsSize" value="${selectorModelNodeIdsSize }">

<%-- '显示不满足条件'链接 --%>
<div id="aDiv" align="right" style="display: none">
 		<fmt:message key='col.branch.show'/>
</div>
<div id="hasBranchSelected" style="display: none"></div>

<%-- 新流程设置 --%>
<c:if test="${newflowModels ne null}">
<br/>
<%-- 当前流程处理节点上对本节点上出发的新流程的处理(只有单人节点可以触发新流程)--%>
<div id="newflowDIV">
    <fieldset>
		<legend><fmt:message key="newflow.select.label"/></legend>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<%-- 循环遍历将要触发的每一条新流程 :开始--%>
     	<c:forEach items="${newflowModels}" var="newflow" varStatus="status">
     		<%-- 获得触发新流程的条件 --%>
     		<c:set value="${conditionResultMap[newflow.id]}" var="isPassJS"/>
			<tr>
				<td width="10" height="30" nowrap="nowrap" style="cursor: default;">
				<%-- 将新流程的id信息保存到checkbox中newflow，并根据该触发该新流程的条件是否为强制条件，来确定该checkbox是否为disabled，以及条件是否符合来确定是否选中该checkbox--%>
				<input type="checkbox" id="newflow${status.index}" name="newflow" value="${newflow.id}" ${newflow.isForce? 'disabled':''} ${isPass? 'checked':''} 
				 onClick="javascript:if(this.checked){document.getElementById('td${newflow.id}').style.display='block';}else{document.getElementById('td${newflow.id}').style.display='none';}"/>
				</td>
				<%-- 将新流程对应的流程模板名称显示出来，并提供通过单击流程模板名称调用前面checkbox中的onclick事件 --%>
     			<td class="padding-L" title="${newflow.templeteName}" style="padding-top: 6px" onclick="document.getElementById('newflow${status.index}').click()">
					<label for="newflow${status.index}">${v3x:getLimitLengthString(newflow.templeteName, 30, '...')} &nbsp;&nbsp;[<span id="result${status.index}" style="cursor: default;"><fmt:message key='newflow.condition.false'/></span>]</label>
				</td>
				<td width="30%" nowrap="nowrap" align="right" style="padding-right: 4px;">
				<div id="td${newflow.id}">
					<c:choose>
						<%-- 判断是否为新流程指定了触发人员，如果指定了，则进行如下处理： --%>
						<c:when test="${!empty newflow.people}">
							<fmt:message key="common.sender.label"  bundle="${v3xCommonI18N}"/>:
							<%-- 显示为新流程指定的触发人员列表 --%>
							<select name="senderId_${newflow.id}" style="width: 180px;" >
								<c:forEach items="${newflow.people}" var="p">
									<c:set var="m" value="${v3x:getOrgEntity('Member', p.id)}" />
				                    <option value="${p.id}" title="${v3x:showOrgMemberAltWithFullDeptPath(m)}">${v3x:showOrgMemberName(m)}</option>
					     		</c:forEach>
							</select>
						</c:when>
						<%-- 如果没有为新流程指定触发人员，则进行如下处理：弹出选人页面，供用户进行选择 --%>
						<c:otherwise>
							<fmt:message key="common.sender.label"  bundle="${v3xCommonI18N}"/>:
							<%-- 用来保存新流程触发人员Id --%>
	                        <input id="senderId_${newflow.id}" name="senderId_${newflow.id}" type="hidden" value="" />
	                        <%-- 用来提供弹出选人输入域，单击该输入域，弹出选人页面 --%>
	                        <input id="senderId_${newflow.id}Name" name="senderId_${newflow.id}Name" value="${defaultSP}" deaultValue="${defaultSP}" 
	                            readonly class="cursor-hand" onclick="selectPeople('1', 'senderId_${newflow.id}')" style="width: 180px;"
	                            inputName="<fmt:message key='common.sender.label'  bundle='${v3xCommonI18N}'/>" validate="isDeaultValue,notNull" validates="isDeaultValue,notNull" onAfterAlert="selectPeople('1', 'senderId_${newflow.id}')">
						</c:otherwise>
					</c:choose>
					<%-- 对触发新流程的条件进行拼接，为在页面判断提供基础 --%>
					<input type="hidden" id="scripts" name="scripts" value="var jsVar${status.index} = eval('${isPassJS}'); document.getElementById('newflow${status.index}').checked = jsVar${status.index}; if(jsVar${status.index} == false) document.getElementById('td${newflow.id}').style.display='none'; if(jsVar${status.index} == true) document.getElementById('result${status.index}').innerHTML='<fmt:message key='newflow.condition.true'/>';" />
				</div>
				</td>
			</tr>
     	</c:forEach>
		</table>
		<script>
			var isForm = true;
		</script>
    </fieldset>
</div>
</c:if>
<%-- 对流程分支中是否存在不可用的节点进行处理 --%>
<c:if test="${invalidateActivity ne null}">
	<c:set value="${v3x:join(invalidateActivity, 'nodeName', pageContext)}" var="invalidateActivityName" />
	<input type="hidden" id="invalidateActivity" name="invalidateActivity" value="${invalidateActivityName}">
</c:if>
<%-- 隐藏流程模板描述信息及描述方式：xml 
<input type="hidden" id="processXML" name="processXML" value="${caseProcessXML}">
<input type="hidden" id="desc_by" name="desc_by" value="${process_desc_by}">
--%>
<%-- 标志是否需要弹出流程分支选择页面：本次该标志将被废弃 --%>
<span id="showProcessModeSelector" style="display:none">${(((selectorModelNodeIds ne null) && (isAllReadOnly eq 'false')) || (newflowModels ne null)) ? 2 : 0}</span>
</div>
 
<%-- 按钮部分--%>
</div>
        </td>
	</tr>
	<c:if test="${v3x:getBrowserFlagByRequest('OpenDivWindow', pageContext.request)}">
	<tr>
		<td height="42" align="center" class="bg-advance-bottom">
			<input type="button" onclick="ok()" value="<fmt:message key='common.button.ok.label' bundle="${v3xCommonI18N}" />" class="button-default-2">&nbsp;&nbsp;
			<input type="button" onclick="window.close()" value="<fmt:message key='common.button.cancel.label' bundle="${v3xCommonI18N}" />" class="button-default-2">
		</td>
	</tr>
	</c:if>
</table>
<script>
if(v3x.isMSIE10){
	var oHeight = parseInt(document.body.clientHeight)-100;
	if(oHeight<0){
		oHeight = 420
	}
	document.getElementById('div1').style.height = oHeight+"px";
}
</script>