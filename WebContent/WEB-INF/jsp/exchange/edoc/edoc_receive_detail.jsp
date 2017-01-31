<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<html>
<head>
<%@include file="../exchangeHeader.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
<!--
th {
	background-color: #EFEBDE;
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-top-style: solid;
	border-right-style: solid;
	border-bottom-style: solid;
	border-left-style: solid;
	border-top-color: #FFFFFF;
	border-right-color: #808080;
	border-bottom-color: #808080;
	border-left-color: #FFFFFF;
	font-size: 12px;
	font-weight: normal;
}
-->
</style>
<script type="text/javascript">
<!--
	isNeedCheckLevelScope_grantedDepartId = true;
    flowSecretLevel_grantedDepartId = "${summary.edocSecretLevel}";
	function openEdoc(){
		_url = edocURL + "?method=edocDetailInDoc&summaryId=${summary.id}";
		//alert(_url)
		v3x.openWindow({
			url: _url,
			workSpace : 'yes',
			resizable : "false"
		});
	}

	function openSelectPeopleDlg(){
		var tempModelTemp = '${modelType}';
		if (tempModelTemp == "received") {
			//加入Ajax检查，判断是否已经被登记
			var isCanBeRegisted = true;
			try {
		  		//debugger;
		  		var requestCaller = new XMLHttpRequestCaller(this, "edocExchangeManager", "isBeRegistered", false);
		  		requestCaller.addParameter(1,'String','${edocRecieveRecordID4ChgRegUser}');
		  		isCanBeRegisted = requestCaller.serviceRequest();
			  	}
		  	catch (ex1) {
		  		alert("Exception : " + ex1);
		  		return false;
		  	}
			if(isCanBeRegisted=="true"){
				if (window.confirm(v3x.getMessage('ExchangeLang.exchange_register_change'))) {
					selectPeopleFun_grantedDepartId();
				}
			}else{
				alert(v3x.getMessage('ExchangeLang.exchange_alert_has_registe'));
				parent.location.href = parent.location.href;
			}
		}
		else{
			selectPeopleFun_grantedDepartId();
		}
	}

	function setPeopleFields(elements){
	if(elements){
		var obj1 = getNamesString(elements);
		var obj2 = getIdsString(elements,false);
		document.getElementById("memberId").value = getNamesString(elements);
		document.getElementById("registerUserId").value = getIdsString(elements,false);
		var tempModelTemp = '${modelType}';
		var test = "${v3x:currentUser().name}";
		if(tempModelTemp=="received"){
					var oldRegisterUserName = document.getElementById("memberId").value;
					document.getElementById("received_registerUserId").innerHTML = getNamesString(elements);
				  	try {
			  		//debugger;
			  		var requestCaller = new XMLHttpRequestCaller(this, "edocExchangeManager", "changeRegisterEdocPerson", false);
			  		requestCaller.addParameter(1,'String','${edocRecieveRecordID4ChgRegUser}');
					requestCaller.addParameter(2, "String", getIdsString(elements,false));
			  		requestCaller.addParameter(3, "String", getNamesString(elements));
					requestCaller.addParameter(4, "String", "${v3x:currentUser().name}");
					requestCaller.addParameter(5, "String", "${v3x:currentUser().id}");
			  		rs = requestCaller.serviceRequest();
				  	}
				  	catch (ex1) {
				  		alert("Exception : " + ex1);
				  		return false;
				  	}
					document.getElementById("memberId").value = getNamesString(elements);
					document.getElementById("registerUserId").value = getIdsString(elements,false);
		
					modelTransfer('received');
			}
		}
	}


	function initiate(modelType){
		<c:choose>
			<c:when test="${sendEntityName!=null}">
				<c:set value="${sendEntityName}" var="theSenderName" />
			</c:when>
			<c:otherwise>
				<c:set value="${v3x:showOrgEntitiesOfTypeAndId(elements, pageContext)}" var="theSenderName" />				
			</c:otherwise>
		</c:choose>
		if(modelType == "received"){
			if("2"=='${isBeRegistered}'){
				//已登记，不显示选人
				document.getElementById("pDiv").className = "hidden";
			}else{
				//待登记打开公文，不显示选人
				var from = '${param.from}';
				if("tobook"==from){
					document.getElementById("pDiv").className = "hidden";
				}
				else{
					document.getElementById("pDiv").className = "";
				}
			}
			document.getElementById("toReceive_keepperiod").innerHTML= "";
		    document.getElementById("received_keepperiod").className = "";
			document.getElementById("received_oper").className = "";
			document.getElementById("toReceive_oper").className = "hidden";
			document.getElementById("received_recNo").className = "";
			document.getElementById("received_recNo").innerHTML = "${v3x:toHTML(bean.recNo)}";
			document.getElementById("toReceive_recNo").className = "hidden";
			document.getElementById("toReceive_registerUserId").className = "hidden";
			document.getElementById("received_registerUserId").className = "";
			document.getElementById("received_registerUserId").innerHTML = '${registerName}';
			document.getElementById("received_remark").className = "";
			document.getElementById("toReceive_remark").className = "hidden";
			document.getElementById("received_remark").innerHTML = "${v3x:toHTML(bean.remark)}";
			document.getElementById("issueDate").innerHTML = "<fmt:formatDate value='${bean.issueDate}' type='both' dateStyle='full' pattern='${datePattern}'/>";
			document.getElementById("sendUnit").innerHTML = "${v3x:toHTML(bean.sendUnit)}";
			document.getElementById("issuer").innerHTML = "${v3x:toHTML(bean.issuer)}";
			document.getElementById("sendTo").innerHTML = "${v3x:toHTML(theSenderName)}";			
			document.getElementById("recOrg").innerHTML = "${v3x:toHTML(signedName)}";
			document.getElementById("recTime").innerHTML = "<fmt:formatDate value='${bean.recTime}' type='both' dateStyle='full' pattern='${datePattern}'/>";
			document.getElementById("recUser").innerHTML = "${v3x:toHTML(recUser)}";
			document.getElementById("sender").innerHTML = "${v3x:toHTML(bean.sender)}";
			document.getElementById("stepBackDiv").style.display = "none";
		}
		else if(modelType=="toReceive"){
			document.getElementById("pDiv").className = "";		
			document.getElementById("toReceive_keepperiod").className = "";
			document.getElementById("received_keepperiod").innerHTML= "";
			document.getElementById("toReceive_oper").className = "";			
			document.getElementById("received_oper").className = "hidden";			
			document.getElementById("toReceive_recNo").className = "";
			document.getElementById("received_recNo").className = "hidden";
			document.getElementById("received_registerUserId").className = "hidden";
			document.getElementById("toReceive_registerUserId").className = "";
			document.getElementById("toReceive_remark").className = "";
			document.getElementById("received_remark").className = "hidden";
			document.getElementById("issuer").innerHTML = "${v3x:toHTML(bean.issuer)}";
			document.getElementById("issueDate").innerHTML = "<fmt:formatDate value='${bean.issueDate}' type='both' dateStyle='full' pattern='${datePattern}'/>";
			document.getElementById("sendUnit").innerHTML = "${v3x:toHTML(bean.sendUnit)}";
			document.getElementById("issuer").innerHTML = "${v3x:toHTML(bean.issuer)}";
			document.getElementById("recOrg").innerHTML = "${v3x:toHTML(signedName)}";
			document.getElementById("sendTo").innerHTML = "${v3x:toHTML(theSenderName)}";
			document.getElementById("sender").innerHTML = "${v3x:toHTML(bean.sender)}";
			document.getElementById("recUser").innerHTML = "${v3x:toHTML(bean.recUser)}";
			document.getElementById("recTime").innerHTML = "<fmt:formatDate value='${bean.recTime}' type='both' dateStyle='full' pattern='${datePattern}'/>";
			if('${bean.stepBackInfo}'!=''&&'${bean.stepBackInfo}'!=null){
				document.getElementById("stepBackDiv").style.display = "";
			}
		}
	}

	accountId_grantedDepartId="${exchangeAccountId}";

	function stepBack(){
		var exchangeSendEdocId = '${bean.id}'; 
		var returnValues = v3x.openWindow({
	        url:'exchangeEdoc.do?method=openStepBackDlg&exchangeSendEdocId='+exchangeSendEdocId,
	        width:"400",
	        height:"300",
	        resizable:"0",
	        scrollbars:"true",
	        dialogType:"modal"
	        });
			if(returnValues!=null && returnValues != undefined){
				if(1==returnValues[0]){
					var formObj = document.getElementById("detailForm");
					formObj.method = "POST";
					formObj.target="detailMainFrame"
					formObj.action = '${exchangeEdoc}?method=stepBack&stepBackSendEdocId=' + returnValues[1] + '&stepBackEdocId='
									+ returnValues[2] + '&stepBackInfo=' + returnValues[3];
					formObj.submit();
				}
			}
	}
	function openStepBackInfo(readOnly){
		var exchangeSendEdocId = '${bean.id}'; 
		//var rv = window.showModalDialog('exchangeEdoc.do?method=openStepBackDlg&exchangeSendEdocId='+exchangeSendEdocId + '&readOnly=1',window,'dialogHeight=300px;dialogWidth=400px');
		var rv = v3x.openWindow({
	        url:'exchangeEdoc.do?method=openStepBackDlg4Resgistering&resgisteringEdocId='+exchangeSendEdocId + '&readOnly=1',
	        width:"400",
	        height:"300",
	        resizable:"0",
	        scrollbars:"true",
	        dialogType:"modal"
	        });
	}
//-->	
</script>
</head>
<body onload="initiate('${modelType}');">
			<form name="detailForm" id="detailForm" action="${exchangeEdoc}?method=${operType}&modelType=${modelType}&fromlist=${param.fromlist}" method="post" target="detailMainFrame">
				<v3x:selectPeople id="grantedDepartId" panels="Department" selectType="Member" minSize="1" maxSize="1" jsFunction="setPeopleFields(elements)" originalElements="Member|${current_user_id}" />
				<script>onlyLoginAccount_grantedDepartId=true;</script>
				<input type="hidden" id="modelType" name="modelType" value="${modelType}">
				<input type="hidden" id="id" name="id" value="${bean.id}">
				<input type="hidden" id="affairId" name="affairId" value="${affairId}">
				<div align="center" id="printDiv" name="printDiv">
					<div id="div1" name="div1" align="center">
					<table align="center" class="xdLayout" style="BORDER-RIGHT: medium none; TABLE-LAYOUT: fixed; BORDER-TOP: medium none; BORDER-LEFT: medium none; WIDTH: 80%; BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; WORD-WRAP: break-word" borderColor="buttontext" border="1">
						<colgroup>
							<col style="WIDTH: 90px"></col>
							<col style="WIDTH: 180px"></col>
							<col style="WIDTH: 90px"></col>
							<col style="WIDTH: 90px"></col>
							<col style="WIDTH: 90px"></col>
						</colgroup>
						<tbody vAlign="top">
							<tr style="MIN-HEIGHT: 31px">
								<td colSpan="5" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none; BORDER-LEFT-STYLE: none">
									<div align="center">
										<font face="宋体" color="#ff0000" size="2"><strong><fmt:message key="exchange.edoc.receive" /></strong></font></div>
										<!-- 
										<div align="right" onclick="openEdoc()"><font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.preview" /></font></div>
										-->
										<div id="stepBackDiv" name="stepBackDiv" align="right" style="display:none;"><a href="javascript:openStepBackInfo(1)"><fmt:message key="exchange.edoc.yihuitui" /></div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 32px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.title" /></font>
									</div>
								</td>
								<td colSpan="4" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="subject" name="subject">${bean.subject}
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 32px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.sendaccount" /></font>
									</div>
								</td>
								<td colSpan="2" style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="sendUnit" name="sendUnit">

									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.sendperson" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: none;PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="sender" name="sender">
										
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 32px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.sendToNames" /></font>
									</div>
								</td>
								<td colSpan="4" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="sendTo" name="sendTo">
										
									</div>
									<input type="hidden" id="grantedDepartId" name="grantedDepartId" value="${grantedDepartId}" />
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 35px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.receiveDepart" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="recOrg" name="recOrg">
									
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="edoc.element.doctype" bundle="${edocI18N}"/></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="docType" name="docType">
										<v3x:metadataItemLabel metadata="${colMetadata['edoc_doc_type']}" value="${bean.docType}" />
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 37px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.wordNo" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="docMark" name="docMark">${v3x:toHTML(bean.docMark)}
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.copy" /></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="copies" name="copies">
										${bean.copies}
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 37px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.secretlevel" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="secretLevel" name="secretLevel">
									<v3x:metadataItemLabel metadata="${colMetadata['edoc_secret_level']}" value="${bean.secretLevel}" />
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.urgentlevel" /></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="urgentLevel" name="urgentLevel">
										<v3x:metadataItemLabel metadata="${colMetadata['edoc_urgent_level']}" value="${bean.urgentLevel}" />
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 32px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.signingperson" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="issuer" name="issuer">
									
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.signingdate" /></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="issueDate" name="issueDate">
										
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 33px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.receivedperson" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div name="recUser" id="recUser">
										
									</div>
									<input type="hidden" name="recUserId" id="recUserId" value="${current_user_id}">
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.receiveddate" /></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="recTime" name="recTime">
										
									</div>
								</td>
							</tr>


							<tr style="MIN-HEIGHT: 37px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.signingNo" /></font>
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div id="toReceive_recNo" name="toReceive_recNo" class="hidden">
									<input type="text" value="" name="recNo" id="recNo" name="recNo" style="width:70%;">
									</div>
									<div id="received_recNo" name="received_recNo" class="hidden">
									
									</div>
								</td>
								<td style="BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.keepperiod" /></font>
									</div>
								</td>
								<td colSpan="2" style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									
									<div id="toReceive_keepperiod" name="toReceive_keepperiod" class="hidden">
										<select name="keepperiod" id="keepperiod" style="width:70%;">
											<v3x:metadataItem metadata="${exMetadata['exchange_edoc_keepperiod']}" showType="option" name="keepperiod" />
										</select>
									</div>
								
									<div id="received_keepperiod" name="received_keepperiod" class="hidden">
										<v3x:metadataItemLabel metadata="${exMetadata['exchange_edoc_keepperiod']}" value="${bean.keepPeriod}" />
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 37px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.booker" /></font>
									</div>
								</td>
								<td colspan="4"  style="PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid; BORDER-RIGHT-STYLE: none">
									<div id="toReceive_registerUserId" name="toReceive_registerUserId" class="hidden" style="width:48%;float:left;">
									<input onclick="" type="text" readOnly="readOnly" name="memberId" id="memberId" value="${current_user_name }" style="width:100%;">
									</div><div id="pDiv" name="pDiv" style="width:48%;float:right"><font color="blue"><a href="###" onclick="openSelectPeopleDlg();"><fmt:message key="exchange.edoc.staffselect" /></a></font>
									<input type="hidden" id="registerUserId" name="registerUserId" value="${current_user_id}">
									</div>
									<div id="received_registerUserId" class="hidden">
									
									</div>
								</td>
							</tr>
							<tr style="MIN-HEIGHT: 37px">
								<td style="BORDER-left: none ;BORDER-RIGHT: #ff0000 1pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<div>
										<font face="宋体" color="#ff0000" size="2"><fmt:message key="exchange.edoc.paperinformation" /></font>
									</div>
								</td>
								<td colspan="4" rowspan="2" style="BORDER-RIGHT: #ff0000 0pt solid; PADDING-RIGHT: 1px; BORDER-TOP: #ff0000 1pt solid; PADDING-LEFT: 1px; PADDING-BOTTOM: 1px; VERTICAL-ALIGN: middle; BORDER-LEFT: #ff0000 1pt solid; PADDING-TOP: 1px; BORDER-BOTTOM: #ff0000 1pt solid">
									<!--<input type="text" name="remark" id="remark" style="width:100%;height:100%;" >
								-->
								<div name="toReceive_remark" id="toReceive_remark" class="hidden">
									<textarea name="remark" id="remark" rows="2" style="width:100%;" validate="maxLength"
	                 							 inputName="<fmt:message key="exchange.edoc.paperinformation" />" 
	                 							 maxSize="80"></textarea>
								</div>
								<div name="received_remark" id="received_remark" class="hidden">
								
								</div>
								</td>
							</tr>
						</tbody>
					</table>
					</div>
					<div id="sendButton" name="sendButton" class="">
					<div id="toReceive_oper" name="toReceive_oper" class="hidden" width="10px;">
					<table border="0" width="100%">
								<tr>
									<td height="42" align="center">
											<input type="button" value="<fmt:message key='exchange.edoc.qianshou' />"
											class="button-default-2" onclick="oprateSubmit();">	
											
											<input type="button" value="<fmt:message key='exchange.edoc.huitui' />"
											class="button-default-2" onclick="stepBack();">	
														
											<input type="button" value="<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />"
											class="button-default-2" onclick="recPrint();">										
									</td>
								</tr>					
					</table>
					</div>
					<div  id="received_oper" name="received_oper" class="hidden">
					<table border="0" width="100%">
								<tr>
									<td height="42" align="center">
											<input type="button" value="<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />"
											class="button-default-2" onclick="recPrint();">										
									</td>
								</tr>					
					</table>
					</div>
					</div>
				</div>			
			</form>
</body>
</html>