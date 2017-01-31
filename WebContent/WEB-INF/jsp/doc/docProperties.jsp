<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="docHeader.jsp"%>
<%@ include file="../common/INC/noCache.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<c:set var="current_user_id" value="${sessionScope['com.seeyon.current_user'].id}"/>
<c:set var="cur_acc_id" value="${sessionScope['com.seeyon.current_user'].loginAccount}"/>
<c:set value="${v3x:parseElements(borrowVO, 'userId', 'userType')}" var="borrowVoList"/>
<c:set value="${v3x:parseElements(myGrantVO, 'userId', 'userType')}" var="shareList"/>
<c:set value="${v3x:parseElements(grantVO, 'userId', 'userType')}" var="docperList"/>
<script type="text/javascript">
var originalElements = new ArrayList();
var myOriginalElements = new ArrayList();
var originalElementsborrow = new ArrayList();
var oldCommentEnabled = "${param.isLib == 'true' ? libVO.root.docResource.commentEnabled : folderPropVO.commentEnabled}";
var newCommentEnabled = "${param.isLib == 'true' ? libVO.root.docResource.commentEnabled : folderPropVO.commentEnabled}";
var oldVersionEnabled = "${param.isLib == 'true' ? libVO.root.docResource.versionEnabled : folderPropVO.versionEnabled}";
var newVersionEnabled = "${param.isLib == 'true' ? libVO.root.docResource.versionEnabled : folderPropVO.versionEnabled}";

var lenPotentLan_all="<fmt:message key='doc.jsp.properties.borrow.lenPotent.all'/>";
var lenPotentLan_content="<fmt:message key='doc.jsp.properties.borrow.lenPotent.content'/>";
var doc_fr_type="${doc_fr_type}";

var lenPotentLan_print="<fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />";
var lenPotentLan_save="<fmt:message key='common.toolbar.save.label' bundle='${v3xCommonI18N}' />";
var flowSecretLevel_borrow = "${secretLevel}";
// folderCommentEnabled: fce
function setFce(val){
	newCommentEnabled = val;
}
//folderVersionEnabled: fve
function setFve(val){
	newVersionEnabled = val;
}
function showOrHideVersionComment(val) {
	if(document.getElementById("versionCommentTr"))
		document.getElementById("versionCommentTr").className = val == "true" ? "" : "hidden";
}

// 在文本框中禁止 回车 键
document.onkeydown = function() {   
    var   e   =   window.event.srcElement;   
    var   k   =   window.event.keyCode;   
    if(k==13   &&   e.tagName=="INPUT"   &&   e.type=="text")   
    {   
        window.event.keyCode         =   0;   
        window.event.returnValue=   false;
    }   
}   
function selectAll(allButton, targetName){
	var objcts = document.getElementsByName(targetName);
	
	if(objcts != null){
		for(var i = 0; i < objcts.length; i++){
			if(!objcts[i].disabled)
			objcts[i].checked = allButton.checked;
		}
	}
}
function selectAllBox(allButton, targetName){
	eval("ucfBorrow = true;");
	var objcts = document.getElementsByName("borrowid");
	if(objcts != null){
		for(var i = 0; i < objcts.length; i++){
			var objct = document.getElementById(targetName + i);
			if(!objct.disabled){
				objct.checked = allButton.checked;
			}
		}
	}
}
function validateAcl(flag){
	try{
		var all = document.getElementById("cAll" + flag).checked;
		var edit = document.getElementById("cEdit" + flag).checked;
		var add = document.getElementById("cAdd" + flag).checked;
		var read = document.getElementById("cRead" + flag).checked;
		var browse = document.getElementById("cBrowse" + flag).checked;
		var list = document.getElementById("cList" + flag).checked;
		if(all){
			document.getElementById("cEdit" + flag).checked = true;
			document.getElementById("cAdd" + flag).checked = true;
			document.getElementById("cRead" + flag).checked = true;
			document.getElementById("cBrowse" + flag).checked = true;
			document.getElementById("cList" + flag).checked= true;
		}
		if(!(all || edit || add || read || browse || list)){
			alert(v3x.getMessage("DocLang.doc_property_grant_alert_no_acl"));
			document.getElementById("docGrant" + flag).checked = true;
			document.getElementById("docGrantBtnDel").click();	
		}
	}catch(e){}
}

// userChangeFlag: ucf
var ucfProp = false;
var ucfVersionProp = false;
var ucfPublicShare = false;
var ucfPersonalShare = false;
var ucfBorrow = false;


// 记录用户是否对某个页签的数据做了改动
function userChange(flag){
	//alert("flag::" + flag+" ucf::Prop--" + ucfProp+" ucf::Public--" + ucfPublicShare+" ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
	eval(flag + " = true;");
	//alert("ucf::Prop--" + ucfProp+"ucf::Public--" + ucfPublicShare+"ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
}
// 用户是否用系统日期组件选择了新的日期或时间
function userChangeCalendar(ele, oldValue, prop){
	//alert("ucf::Prop--" + ucfProp+"ucf::Public--" + ucfPublicShare+"ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
	var newValue = ele.value;
	//alert("old:" + oldValue + "  new:" + newValue)
	if(oldValue != newValue){
		if(prop)
			ucfProp = true;
		else
			ucfBorrow = true;
	}
	//alert("ucf::Prop--" + ucfProp+"ucf::Public--" + ucfPublicShare+"ucf::Personal--" + ucfPersonalShare+"ucf::Borrow--" + ucfBorrow)
}
function doProperty(){
	// 验证文档是否存在
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "docResourceExist", false);
	requestCaller.addParameter(1, "Long", '${param.docResId}');
	var existflag = requestCaller.serviceRequest();
	if(existflag == 'false') {
		if('${param.isFolder}' == 'true') {
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
		} else {
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
		}
		window.dialogArguments.parent.parent.location.reload(true);
		parent.close();
		return;
	}
	
	var canEdit = '${bool}';
	if(canEdit == 'false')
		parent.window.close();

	if(!checkForm(propertyMainForm))
		return;

	var brwid = document.getElementsByName("borrowid");
 	
 	var flag_end = brwid.length;
    var fceChangeFlag = false;
    var fveChangeFlag = false;
    var appAll = [-1, -1];
    var commentEnabled_changed = oldCommentEnabled != newCommentEnabled;
    var versionEnabled_changed = oldVersionEnabled != newVersionEnabled;
    if(commentEnabled_changed || versionEnabled_changed) {
    	fceChangeFlag = commentEnabled_changed;
    	fveChangeFlag = versionEnabled_changed;
    	var theURL = jsURL + "?method=folderPropEditScopeView&showComment=" + commentEnabled_changed + "&showVersion=" + versionEnabled_changed;
    	var size = (commentEnabled_changed && versionEnabled_changed) ? "480" : "240"; 
    	appAll = v3x.openWindow({
			url : theURL,
			width : "420",
			height : size,
			resizable : "false"
		});
	}

	if(appAll==null) { 
		return;
	} else {
		if(ucfBorrow) {
			  var length =  window.borrowNum ;
			  if(length) {
			     for(var i = 0 ; i < length ; i ++ ) {
			        var begintimeObj = document.getElementsByName("begintime"+i) ;
			        var endtimeObj = document.getElementsByName("endtime"+i) ;
			        if(begintimeObj[0] && endtimeObj[0]) {
			           var startdate = begintimeObj[0].value ;
			           var enddate = endtimeObj[0].value ;
			           if(compareDate(startdate,enddate)>0) {
			             window.alert(v3x.getMessage("V3XLang.calendar_endTime_startTime"));
			             return ; 
			           }
			        }
			     }
			  }
		}
	
		var _all = '${param._all}';
		var _edit = '${param._edit}';
		var _add = '${param._add}';
		var _readonly = '${param._readonly}';
		var _browse = '${param._browse}';
		var _list = '${param._list}';	
		var _isShareAndBorrowRoot = '${param._isShareAndBorrowRoot}';			
		var _resId = '${param._resId}';
		var _parentCommentEnabled = '${param._parentCommentEnabled}';
		var _frType = '${param._frType}';	
		var _docLibId = '${param._docLibId}';
		var _docLibType = '${param._docLibType}';	
		var _flag = '${param._flag}';
	
		propertyMainForm.action = "${detailURL}?method=docLabeldSave&docResId=${param.docResId}&isLib=${param.isLib}&isFolder=${param.isFolder}&docLibType=${param.docLibType}&fceChangeFlag="
				 + fceChangeFlag + "&fveChangeFlag=" + fveChangeFlag + "&appAll=" + appAll[0] + "&appAllVersion=" + appAll[1]
				 + "&ucfProp=" + ucfProp + "&ucfPublic=" + ucfPublicShare + "&ucfVersionProp=" + ucfVersionProp
				 + "&ucfPersonal=" + ucfPersonalShare + "&ucfBorrow=" + ucfBorrow + "&_docLibId=" + _docLibId + "&_docLibType=" + _docLibType
					+ "&_resId=" + _resId + "&_frType=" + _frType + "&_isShareAndBorrowRoot=false" + "&_all=" + _all + "&_edit=" + _edit 
					+ "&_add=" + _add + "&_readonly=" + _readonly + "&_browse=" + _browse + "&_list=" + _list
					+ "&_parentCommentEnabled=" + _parentCommentEnabled + "&_flag=" + _flag;
		if(v3x.getBrowserFlag('openWindow') == true){
		parent.window.document.getElementById("saveBtn").disabled = true;
		parent.window.document.getElementById("cancelBtn").disabled = true;
		}
		propertyMainForm.submit();
	}
}
// 恢复继承
function recover(){
	// 验证文档是否存在
	if(!window.confirm(parent.v3x.getMessage('DocLang.doc_alert_confirm_inherit')))	  
		return;
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager",
		 "docResourceExist", false);
	requestCaller.addParameter(1, "Long", '${param.docResId}');
			
	var existflag = requestCaller.serviceRequest();
	if(existflag == 'false') {
		if('${param.isFolder}' == 'true'){
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));
		}else{
			alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));
		}
		window.dialogArguments.parent.parent.location.reload(true);
		parent.close();
		return ;
	}

	ucfPublicShare = true;
	
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocAclManager", "setPotentInherit", false);
	requestCaller.addParameter(1, "Long", '${param.docResId}');
	requestCaller.addParameter(2, "byte", '${param.docLibType}');
	requestCaller.addParameter(3, "long", '${docLibId}');
				
	requestCaller.serviceRequest();

	<%-- 同时删除通过共享授权产生的订阅记录 --%>
	var requestCaller2 = new XMLHttpRequestCaller(this, "docAlertManager", "deleteAlertByDrIdFromAcl", false);
	requestCaller2.addParameter(1, "Long", '${param.docResId}');
	requestCaller2.serviceRequest();
	
	// 利用ajax动态刷新授权页面的数据
	// 1. 取得新数据
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocAclManager", "getGrantVOs", false);
	requestCaller.addParameter(1, "long", '${param.docResId}');
	requestCaller.addParameter(2, "boolean", '${isGroupLib}');
				
	var ret = requestCaller.serviceRequest();
	//alert('length::' + ret.length + 'ret:: ' + ret)
	// 2. 清空原有数据
	var grantT = document.getElementById("grantId");
	var grantRows = grantT.rows;
	for(; grantRows.length > 1;){
		//alert(grantRows.length)
		grantT.deleteRow(1);
	}
	
	originalElements = new ArrayList();
	
	// 3. 利用新的数据生成新的rows
	for(var i = 0; i < ret.length; i++){
		//alert("i:: " + i + " obj:: " + ret[i])
		var tr = grantT.insertRow();
		tr.id = ret[i].get('userId');
		var isOwner = ret[i].get('isLibOwner');
		var td0 = tr.insertCell();
		td0.align = "center";
		var td0Str = "<input type='checkbox' name='id' value='" + ret[i].get('userId');
		if(isOwner == 'true')
			td0Str += "' disabled />";
		else 
		 	td0Str += "' />";
		
		td0.innerHTML = td0Str;
		//alert(td0.innerHTML)
		td0.className = "sort";
		var td = tr.insertCell();
		
		td.innerHTML = ret[i].get('userName') + "<input type='hidden' name='username' value='" + ret[i].get('userName') + "'> "
					+ "<input type='hidden' name=uid" + i + " value='" + ret[i].get('userId') + "'>"
					+ "<input type='hidden' name=utype" + i + " value='" + ret[i].get('userType') + "'>"
					+ "<input type='hidden' name=inherit" + i + " value='true'>	";
		originalElements.add("" + ret[i].get('userId') + "");
		
		td.className = "sort";
		
		var td1 = tr.insertCell();
		td1.align = "center";
		var td1Str = '<input type="checkbox"  name="cAll' + i + '" id="cAll' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td1Str += " disabled ";
		if(ret[i].get('all') == 'true')
			td1Str += " checked ";
		td1Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td1.innerHTML = td1Str;
		td1.className = "sort";
		
				var td2 = tr.insertCell();
		td2.align = "center";
		var td2Str = '<input type="checkbox"  name="cEdit' + i + '" id="cEdit' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td2Str += " disabled ";
		if(ret[i].get('edit') == 'true')
			td2Str += " checked ";
		td2Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td2.innerHTML = td2Str;
		td2.className = "sort";
		
				var td3 = tr.insertCell();
		td3.align = "center";
		var td3Str = '<input type="checkbox"  name="cAdd' + i + '" id="cAdd' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td3Str += " disabled ";
		if(ret[i].get('add') == 'true')
			td3Str += " checked ";
		td3Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td3.innerHTML = td3Str;
		td3.className = "sort";
		
				var td4 = tr.insertCell();
		td4.align = "center";
		var td4Str = '<input type="checkbox"  name="cRead' + i + '" id="cRead' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td4Str += " disabled ";
		if(ret[i].get('read') == 'true')
			td4Str += " checked ";
		td4Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td4.innerHTML = td4Str;
		td4.className = "sort";
		
				var td5 = tr.insertCell();
		td5.align = "center";
		var td5Str = '<input type="checkbox"  name="cBrowse' + i + '" id="cBrowse' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td5Str += " disabled ";
		if(ret[i].get('browse') == 'true')
			td5Str += " checked ";
		td5Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td5.innerHTML = td5Str;
		td5.className = "sort";
		
				var td6 = tr.insertCell();
		td6.align = "center";
		var td6Str = '<input type="checkbox"  name="cList' + i + '" id="cList' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td6Str += " disabled ";
		if(ret[i].get('list') == 'true')
			td6Str += " checked ";
		td6Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td6.innerHTML = td6Str;
		td6.className = "sort";
		
				var td7 = tr.insertCell();
		td7.align = "center";
		var td7Str = '<input type="checkbox"  name="cAlert' + i + '" id="cAlert' + i + '" onchange="userChange(\'ucfPublicShare\')"';
		if(isOwner == 'true')
			td7Str += " disabled ";
		if(ret[i].get('alert') == 'true')
			td7Str += " checked ";
		//if(isOwner == 'true')
		//	td7Str += " checked ";
		td7Str += ' onclick="validateAcl(\'' + i + '\')" value="true" >';
		td7.innerHTML = td7Str;
		td7.className = "sort";
	}
}
function initStyle(){
	//alert('${param.toShare}');
	parent.loadedFlag = true;

	if(${param.isFolder == "true" && param.isC == "true"} || ${param.toShare == "true"}){
		document.getElementById("docCommonTR").style.display = "";	
	}
	
	if(${param.isFolder == "true" && param.isM == "true"}){
		document.getElementById("myCommonTR").style.display = "";
	}
	if(${param.isFolder == "false" && param.isB == "true"}){
		document.getElementById("borrowTR").style.display = "";
	}
	if(${param.isP == "true"}){
		document.getElementById("normalTR").style.display = "";
	}
}
function mygrantalert(i) {
	//alert(i)
	var ele = document.getElementById("mygrantalertnew" + i);
	var ckb = document.getElementById("mygrantalertckb" + i)
	//alert(ele)
	//alert(ckb)
	if(ckb.checked)
		ele.value = 'true';
	else
		ele.value = 'false';
		
	//alert(ckb.checked + ' -------------- ' + ele.value)
}
//-->
</script>
<v3x:selectPeople id="per" panels="Department,Team,Outworker,RelatePeople"
	selectType="Account,Department,Team,Member"
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
	jsFunction="setMyPeopleFields(elements, '${cur_acc_id}')" 
	originalElements="${shareList }"/>
	
<c:if test="${isGroupLib == true}">
	<c:set value="Account,Department,Post,Level,Team" var="publicPanels" />
	<c:set value="true" var="showAllAcc" />
</c:if>
<c:if test="${isGroupLib != true}">
	<c:set value="false" var="showAllAcc" />
	
    <c:if test = "${param._docLibType == 3}">
        <c:set value="Department,Post,Level,Team" var="publicPanels" />
    </c:if>
	<c:if test = "${param._docLibType!= 3}">
        <c:set value="Department,Post,Level,Team,Outworker" var="publicPanels" />
    </c:if>
    <c:if test="${openShareScope==true}" >
    	<c:set value="true" var="showAllAcc" />
    </c:if>	
</c:if>
<v3x:selectPeople id="borrow" panels="${publicPanels}"
	selectType="Account,Department,Level,Post,Team,Member"
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
	jsFunction="borrowSetPeopleFields(elements, '${cur_acc_id}')" 
	originalElements='${borrowVoList}'/>
<v3x:selectPeople id="docper" panels="${publicPanels}"
	selectType="Account,Department,Level,Post,Team,Member"
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}"
	jsFunction="docGrantSetPeopleFields(elements, '${cur_acc_id}')" 
	originalElements="${docperList }"/>
<script type="text/javascript">
    showOriginalElement_per = false;
    showOriginalElement_borrow = false;
    showOriginalElement_docper = false;

	showAllOuterDepartment_per = true;
	showAllOuterDepartment_borrow = true;
	showAllOuterDepartment_docper = true;
    
        
    isNeedCheckLevelScope_per = false;
    isNeedCheckLevelScope_borrow = false;
    isNeedCheckLevelScope_docper = false;
    var elements_borrow = parseElements('${borrowVoList}') ;
    var elements_per = parseElements('${shareList}') ;
    var elements_docper = parseElements('${docperList}') ;

    var excludeElements_per =    parseElements4Exclude("${current_user_id}", "Member");
    var excludeElements_borrow = parseElements4Exclude("${current_user_id}", "Member");
    var excludeElements_docper = parseElements4Exclude("${ownerIds}", "Member");
    
    ${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}
	hiddenPostOfDepartment_borrow = true;
	hiddenPostOfDepartment_docper = true;
    
    onlyLoginAccount_per = true;
    if('${isGroupLib}' == 'false'){
        onlyLoginAccount_borrow = true;
        if('${openShareScope}' == 'false') { 
    		onlyLoginAccount_docper = true;
    	}
    }
    
    var isGroupLib = '${isGroupLib}';
    
    // 记录授权记录的可用最小数字后缀
    var perShareNum = 1;
    var deptShareNum = 1;
    var borrowNum = 1;

</script>

<script>
	// 正在调用选人组件的html组件id
	var sp_invoke_id = "";
	function sp_fun(id, type){
		sp_invoke_id = id;
		
		if(type == 'Member'){
			selectPeopleFun_sp_member();
		}else if(type == 'Department'){
			selectPeopleFun_sp_dept();
		}
		
		sp_invoke_id = "";
	}
	function setPeopleFields(elements){
		if(!elements) { 
			return; 	
		}
		
		ucfProp = true;
		
		document.getElementById("name_" + sp_invoke_id).value = getNamesString(elements);		
		document.getElementById(sp_invoke_id).value=getIdsString(elements, false);
		//alert(getNamesString(elements));
	}
</script>
<v3x:selectPeople id="sp_member" panels="Department,Team,Outworker" selectType="Member" maxSize="1" 
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}" jsFunction="setPeopleFields(elements)" />
<v3x:selectPeople id="sp_dept" panels="Department,Team,Outworker" selectType="Department" maxSize="1" 
	departmentId="${sessionScope['com.seeyon.current_user'].departmentId}" jsFunction="setPeopleFields(elements)" />
<script type="text/javascript">
    showOriginalElement_sp_member = false;
    showOriginalElement_sp_dept = false;
</script>
</head>
<body onload="initStyle();">
<form method="post" name="propertyMainForm" id="propertyMainForm" onsubmit="checkForm(this)" target="">
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">

	<tr id="normalTR" style="display:none"  valign="top">
		<td>
<div id="scroll">
<table width="95%" height="80%" border="0" cellspacing="0" align="center"
	cellpadding="0" id="normalTable"  style="word-break:break-all;word-wrap:break-word;table-layout:fixed">
<c:if test="${isLib == false }">
	<c:if test="${param.isFolder == 'false' }">
		<tr>
			<td align="right" width="30%"><img src="/seeyon/apps_res/doc/images/docIcon/${docPropVO.icon}">&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${v3x:toHTML(v3x:_(pageContext, docPropVO.name))}</td>
		</tr>
	
		<tr>
			<td colspan="2" class="separatorDIV" height="2"></td>
		</tr>
	
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.contenttype'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${v3x:toHTML(v3x:_(pageContext, docPropVO.type))}</td>
		</tr>
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.path'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${docPropVO.path}</td>
		</tr>

		<c:if test="${docPropVO.isShortCut == false && docPropVO.isPigeonhole == false}">
			<tr>
				<td align="right"><fmt:message key='doc.metadata.def.size'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>${docPropVO.size}</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="separatorDIV" height="2" ></td>
		</tr>
	
		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.creater'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${docPropVO.createUserName}</td>
		</tr>
		
		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.createtime'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><fmt:formatDate value="${docPropVO.createTime}" pattern="${datetimePattern}" /></td>
		</tr>

		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.lastuser'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${docPropVO.lastUserName}</td>
		</tr>

		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.lastupdate'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><fmt:formatDate value="${docPropVO.lastUpdate}" pattern="${datetimePattern }" /></td>
		</tr>
	
	<c:if test="${isPersonalLib == 'false' && isDocLink == false}">
		<tr>
			<td colspan="2" class="separatorDIV" height="2"></td>
		</tr>
		
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.accesscount'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${docPropVO.accessCount}</td>
		</tr>
	
		<c:set value="${docPropVO.isShortCut == true || docPropVO.isPigeonhole == true}" var="hidden" />
		<tr class="${hidden ? 'hidden' : ''}">
			<td align="right"><fmt:message key='doc.jsp.properties.common.comment.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
				<label for="icommentEnabled1">
					<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="icommentEnabled1" name="commentEnabled" value="true" onchange="userChange('ucfProp')" <c:if test="${bool == false}"> disabled </c:if>  <c:if test="${docPropVO.commentEnabled == true}"> checked </c:if>>
				</label>
				<label for="icommentEnabled2">
					<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio" id="icommentEnabled2" name="commentEnabled"  value="false"  onchange="userChange('ucfProp')" <c:if test="${bool == false}"> disabled </c:if> <c:if test="${docPropVO.commentEnabled == false}"> checked </c:if>>
				</label>
			</td>
		</tr>
		
		<tr class="${hidden ? 'hidden' : ''}">
			<td align="right"><fmt:message key='doc.jsp.properties.common.commentcount'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${docPropVO.commentCount}</td>
		</tr>
	</c:if>

	<c:if test="${(!isPersonalLib && !isDocLink && !isEdocLib && !docPropVO.isShortCut && !docPropVO.isPigeonhole) || ((isPersonalLib || isEdocLib) && docPropVO.versionEnabled)}">
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.version.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
			<label for="iversionEnabled1">
			<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="iversionEnabled1" name="versionEnabled"  onchange="userChange('ucfVersionProp')" value="true" <c:if test="${bool == false || param._all == 'false'}"> disabled </c:if>  <c:if test="${docPropVO.versionEnabled == true}"> checked </c:if> onclick="showOrHideVersionComment(this.value)">
			</label>
			<label for="iversionEnabled2">
			<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio"  id="iversionEnabled2" name="versionEnabled"  onchange="userChange('ucfVersionProp')"  value="false" <c:if test="${bool == false || param._all == 'false'}"> disabled </c:if> <c:if test="${docPropVO.versionEnabled == false}"> checked </c:if> onclick="showOrHideVersionComment(this.value)">
			</label>
			</td>
		</tr>
	</c:if>
	<tr id="versionCommentTr" class="hidden">
			<td align="right" valign="top" width="30%"><fmt:message key='doc.menu.history.note.label'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<c:if test="${bool == true }">
				<td valign="top"><textarea name="versionComment" id="versionComment"  onchange="userChange('ucfVersionProp')" rows="4" cols="54" maxSize="300" validate="maxLength" inputName="<fmt:message key='doc.menu.history.note.label'/>">${docPropVO.versionComment}</textarea></td>
			</c:if>
			<c:if test="${bool == false }">
				<td valign="top"><textarea name="versionComment" id="versionComment" disabled="disabled" rows="4" cols="54">${docPropVO.versionComment}</textarea></td>
			</c:if>
		</tr>
		<script type="text/javascript">
			<c:if test="${docPropVO.versionEnabled}">
				showOrHideVersionComment("true");
			</c:if>
		</script>
	<tr>
		<td colspan="2" class="separatorDIV" height="2"></td>
	</tr>
	<tr><td colspan="2" height="5"></td></tr>
	<c:if test="${docPropVO.isShortCut == true}">
		<tr class="hidden">
	</c:if>

	<c:if test="${docPropVO.isShortCut == false}">
		<tr>
	</c:if>
		<td align="right" width="30%"><fmt:message key='doc.metadata.def.keywords'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<c:if test="${bool == true }">
			<td><input type="text" name="docKeywords" id="docKeywords"  onchange="userChange('ucfProp')" value="${v3x:toHTML(docPropVO.keywords)}" size="52" maxSize="80" validate="maxLength" inputName="<fmt:message key='doc.metadata.def.keywords'/>"/></td>			
		</c:if>
		<c:if test="${bool == false }">
			<td><input type="text" name="docKeywords" id="docKeywords" value="${v3x:toHTML(docPropVO.keywords)}" size="52"  disabled="disabled"/></td>
		</c:if>
	
	<tr>
		<td align="right" valign="top" width="30%"><fmt:message key='doc.metadata.def.desc'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<c:if test="${bool == true }">
			<td valign="top"><textarea name="docDesc" id="docDesc"  onchange="userChange('ucfProp')" rows="4" cols="54" maxSize="80" validate="maxLength" inputName="<fmt:message key='doc.metadata.def.desc'/>"><c:out value="${docPropVO.desc}" escapeXml="true"/></textarea></td>
		</c:if>
		<c:if test="${bool == false }">
			<td valign="top"><textarea name="docDesc" id="docDesc" disabled="disabled" rows="4" cols="54"><c:out value="${docPropVO.desc}" escapeXml="true"/></textarea></td>
		</c:if>
	</tr>
		
</c:if>
	
	
	<c:if test="${param.isFolder == 'true' }">
	<script>
		document.getElementById("normalTable").height='80%';
	</script>
	<tr>
		<td align="right" width="30%"><img src="/seeyon/apps_res/doc/images/docIcon/${folderPropVO.icon}">&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>${v3x:toHTML(v3x:_(pageContext, folderPropVO.name))}</td>
	</tr>
	<tr>
		<td colspan="2" class="separatorDIV" height="2"></td>
	</tr>
	<tr>	
	<td align="right"><fmt:message key='doc.jsp.properties.common.contenttype'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>${v3x:toHTML(v3x:_(pageContext, folderPropVO.type))}</td>	
	</tr>

	<tr>
		<td align="right"><fmt:message key='doc.jsp.properties.common.path'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>${v3x:_(pageContext, folderPropVO.path)}</td>
	</tr>
	
	<tr>
		<td colspan="2" class="separatorDIV" height="2"></td>
	</tr>
	
	<tr>
		<td align="right"><fmt:message key='doc.metadata.def.creater'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>${folderPropVO.createUserName}</td>
	</tr>

	<tr>	
		<td align="right"><fmt:message key='doc.metadata.def.createtime'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td><fmt:formatDate value="${folderPropVO.createTime}" pattern="${datetimePattern }" /></td>
	</tr>
	
	<tr>
		<td align="right"><fmt:message key='doc.metadata.def.lastuser'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>${folderPropVO.lastUserName}</td>
	</tr>

	<tr>
		<td align="right"><fmt:message key='doc.metadata.def.lastupdate'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td><fmt:formatDate value="${folderPropVO.lastUpdate}" pattern="${datetimePattern }" /></td>
	</tr>
	
	<c:if test="${isPersonalLib == 'false'}">
	<tr>
		<td align="right"><fmt:message key='doc.jsp.properties.common.comment.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
		<label for="foldCommentEnabled1">
		<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="foldCommentEnabled1" name="foldCommentEnabled"  onchange="userChange('ucfProp')" value="true" <c:if test="${bool == false}"> disabled </c:if>  <c:if test="${folderPropVO.commentEnabled == true}"> checked </c:if> onclick="setFce(this.value)">
		</label>
		<label for="foldCommentEnabled2">
		<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio"  id="foldCommentEnabled2" name="foldCommentEnabled"  onchange="userChange('ucfProp')"  value="false" <c:if test="${bool == false}"> disabled </c:if> <c:if test="${folderPropVO.commentEnabled == false}"> checked </c:if> onclick="setFce(this.value)">
		</label>
		</td>
	</tr>
	</c:if>
	
	<c:if test="${isPersonalLib == 'false' && isDocLink == false && isEdocLib == 'false' && docPropVO.isShortCut != true && docPropVO.isPigeonhole != true}">
	<tr>
		<td align="right"><fmt:message key='doc.jsp.properties.common.version.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
		<label for="foldVersionEnabled1">
		<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="foldVersionEnabled1" name="foldVersionEnabled"  onchange="userChange('ucfProp')" value="true" <c:if test="${bool == false || editVersion == false}"> disabled </c:if>  <c:if test="${folderPropVO.versionEnabled == true}"> checked </c:if> onclick="setFve(this.value)">
		</label>
		<label for="foldVersionEnabled2">
		<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio"  id="foldVersionEnabled2" name="foldVersionEnabled"  onchange="userChange('ucfProp')"  value="false" <c:if test="${bool == false || editVersion == false}"> disabled </c:if> <c:if test="${folderPropVO.versionEnabled == false}"> checked </c:if> onclick="setFve(this.value)">
		</label>
		</td>
	</tr>
	</c:if>
	
	<tr>
	<td colspan="2" class="separatorDIV" height="2"></td>
	</tr>
	<tr><td colspan="2" height="5"></td></tr>
	<tr>
		<td align="right" valign="top" width="30%"><fmt:message key='doc.metadata.def.desc'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<c:if test="${bool == true }">
			<td valign="top"><textarea name="folderDesc"  onchange="userChange('ucfProp')" id="folderDesc" rows="4" cols="54" maxSize="80" validate="maxLength" inputName="<fmt:message key='doc.metadata.def.desc'/>"><c:out value="${folderPropVO.desc}" escapeXml="true"/></textarea></td>
		</c:if>
		<c:if test="${bool == false }">
			<td valign="top"><textarea name="folderDesc" id="folderDesc" rows="4" disabled="disabled" cols="54"><c:out value="${folderPropVO.desc}" escapeXml="true"/></textarea></td>
		</c:if>
	</tr>
	
	</c:if>
	</c:if>

<%-- 文档库属性设置 --%>
<c:if test="${isLib == true }">
		<script>
			document.getElementById("normalTable").height='60%';
		</script>
		<tr>
			<td align="right" width="30%"><img src="/seeyon/apps_res/doc/images/docIcon/${libVO.icon}">:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${v3x:_(pageContext, libVO.doclib.name)}</td>
		</tr>	
		<tr><td colspan="2" class="separatorDIV" height="2"></td></tr>	
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.lib.type'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>			
			<td><fmt:message key="${libVO.docLibType}"/></td></tr>			
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.lib.admin'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${libVO.managerName}</td></tr>				
		<tr>
			<td colspan="2" class="separatorDIV" height="2" ></td>
		</tr>		
		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.createtime'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td><fmt:formatDate value="${libVO.doclib.createTime}" pattern="${datetimePattern}" /></td>
		</tr>
		<tr>
			<td colspan="2" class="separatorDIV" height="2" ></td>
		</tr>
		
		<c:if test="${isPersonalLib == 'false'}">
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.comment.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
			<label for="foldCommentEnabled1">
			<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="foldCommentEnabled1" name="foldCommentEnabled"  onchange="userChange('ucfProp')" value="true" <c:if test="${bool == false}"> disabled </c:if>  <c:if test="${libVO.root.docResource.commentEnabled == true}"> checked </c:if> onclick="setFce(this.value)">
			</label>
			<label for="foldCommentEnabled2">
			<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio"  id="foldCommentEnabled2" name="foldCommentEnabled"  onchange="userChange('ucfProp')"  value="false" <c:if test="${bool == false}"> disabled </c:if> <c:if test="${libVO.root.docResource.commentEnabled == false}"> checked </c:if> onclick="setFce(this.value)">
			</label>
			</td>
		</tr>
		</c:if>
		
		<c:if test="${isPersonalLib == 'false' && isEdocLib == 'false'}">
		<tr>
			<td align="right"><fmt:message key='doc.jsp.properties.common.version.enabled'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
			<label for="foldVersionEnabled1">
			<fmt:message key='common.yes' bundle="${v3xCommonI18N}"/><input type="radio" id="foldVersionEnabled1" name="foldVersionEnabled"  onchange="userChange('ucfProp')" value="true" <c:if test="${bool == false || param._all == 'false'}"> disabled </c:if>  <c:if test="${libVO.root.docResource.versionEnabled == true}"> checked </c:if> onclick="setFve(this.value)">
			</label>
			<label for="foldVersionEnabled2">
			<fmt:message key='common.no' bundle="${v3xCommonI18N}"/><input type="radio"  id="foldVersionEnabled2" name="foldVersionEnabled"  onchange="userChange('ucfProp')"  value="false" <c:if test="${bool == false || param._all == 'false'}"> disabled </c:if> <c:if test="${libVO.root.docResource.versionEnabled == false}"> checked </c:if> onclick="setFve(this.value)">
			</label>
			</td>
		</tr>
		</c:if>
		
		<tr>
			<td align="right"><fmt:message key='doc.metadata.def.desc'/>:&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${v3x:toHTML(libVO.doclib.description)}</td>
		</tr>
</c:if>
</table>
</div>	
	</td>
</tr>
	
	<tr id="extendTR" style="display:none"  valign="top">
		<td>
			<div class="scrollList">${metadataHtml}</div>
			<input type="hidden" name="docResId" value="${vo.docResourceId}">
			<iframe name="editIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>
		</td>
	</tr>
	
		
	<tr id="docCommonTR" valign="top"  style="display:none">
		<td>
			<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>

		<div class="scrollList" id="shouquan">
		<fieldset class="docScrollList" >
		<c:set value="0" var="i" />
		<v3x:table data="${grantVO}" var="grantobj" htmlId="grantId"
			isChangeTRColor="false" showHeader="true" showPager="false" dragable="false">
		    <c:if test="${grantobj.userId == current_user_id && grantobj.all=='false'}">
		    </c:if>
			<v3x:column width="5%" align="center"
				label="<input type='checkbox' onclick='selectAll(this, \"id\")'/>">
				<input type='checkbox' name='id' value="${grantobj.userId}" id="docGrant${i}" 
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
				${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}/>
				<script type="text/javascript"> deptShareNum++; </script>
			</v3x:column>
			<v3x:column label="${v3x:_(pageContext, 'doc.jsp.properties.share.grantobj')}">${grantobj.userName}<input
					type="hidden" value="${grantobj.userName}" name="username">
				<input type="hidden" id="uid${i}" name="uid${i}" value="${grantobj.userId}">
				<input type="hidden" id="utype${i}" name="utype${i}" value="${grantobj.userType}">
				<input type="hidden" id="inherit${i}" name="inherit${i}" value="${grantobj.inherit}">
				<script>originalElements.add('${grantobj.userId}')</script>
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.all')}">
				<input type="checkbox" name="cAll${i}" id="cAll${i}" value="true"
					${v3x:outConditionExpression(grantobj.all=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')} 
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')"/>
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.edit')}">
				<input type='checkbox' name='cEdit${i}' id='cEdit${i}' value="true"
					${v3x:outConditionExpression(grantobj.edit=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')} 
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')"/>
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.add')}">
				<input type='checkbox' name='cAdd${i}' id='cAdd${i}' value="true"
					${v3x:outConditionExpression(grantobj.add=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')" />
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.readonly')}">
				<input type='checkbox' name='cRead${i}' id='cRead${i}' value="true"
					${v3x:outConditionExpression(grantobj.read=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')" />
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.browse')}">
				<input type='checkbox' name='cBrowse${i}' id='cBrowse${i}' value="true"
					${v3x:outConditionExpression(grantobj.browse=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')} 
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')"/>
			</v3x:column>
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.list')}">
				<input type='checkbox' name='cList${i}' id='cList${i}' value="true"
					${v3x:outConditionExpression(grantobj.list=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}
					onchange="userChange('ucfPublicShare')"
					onclick="validateAcl('${i}')" />
			</v3x:column>
			
			<v3x:column align="center"
				label="${v3x:_(pageContext, 'doc.jsp.properties.share.alert')}">
				<input type='checkbox' name='cAlert${i}' id='cAlert${i}' value="true" 
					${v3x:outConditionExpression(grantobj.alert=='true', 'checked', '')}
					${v3x:outConditionExpression(userAllAcl=='true' || param._all=='true' || param.allAcl=='true', '', 'disabled')}
					${v3x:outConditionExpression(grantobj.isLibOwner=='true', 'disabled', '')}
					
					onchange="userChange('ucfPublicShare')"
				    onclick="validateAcl('${i}')"
				/>
			</v3x:column>

			<c:set value="${i+1}" var="i" />
			
		</v3x:table>
		</fieldset>
		<table height="20%" width="100%">
			<tr>
				<td height="20" align="left"> 
					<input type="hidden" name="surveytypeid" value="${param.surveytypeid}">
					<input type="hidden" name="docResId" value="${param.docResId}">
				  <c:if test="${userAllAcl=='true' || param._all=='true'|| param.allAcl=='true'}">
					<input type="button" value="<fmt:message key='doc.jsp.properties.share.add'/>" class="button-default-2" onclick="selectPeopleFun_docper()">&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="button" id="docGrantBtnDel" value="<fmt:message key='doc.jsp.properties.share.delete'/>" class="button-default-2" onclick="docGrantdeleteUser()">&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="button" value="<fmt:message key='doc.jsp.properties.share.huifujicheng'/>" class="button-default-2" onclick="recover()"
					<c:if test="${(param.isFolder == true && folderPropVO.docResource.parentFrId == 0) || (param.isLib == true) || (param.frType==42)}"> disabled </c:if>	/>
				  </c:if>
				</td>
			</tr>
		</table>
		</div>
		
		<iframe  name="DGIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0">
		</iframe>
	</td>
	</tr>
</table>
		</td>
	</tr>
	
	<tr id="myCommonTR" valign="top"  style="display:none">
	<td>
		<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
				<div class="scrollList" id="shouquan2">
				<fieldset class="docScrollList" >
				<c:set value="0" var="i" />
				<v3x:table data="${myGrantVO}" var="mygrantobj" htmlId="mygrantgrantId" isChangeTRColor="false" dragable="false" showHeader="true" showPager="false">
					<v3x:column width="5%" align="center"
						label="<input type='checkbox' onclick='selectAll(this, \"mygrantid\")'/>">
						<input type='checkbox' name='mygrantid' value="${mygrantobj.userId}" />
						<script type="text/javascript"> perShareNum++; </script>
					</v3x:column>
					<v3x:column label="${v3x:_(pageContext, 'doc.jsp.properties.share.grantobj')}">${mygrantobj.userName}<input
							type="hidden" value="${mygrantobj.userName}" name="mygrantusername">
						<input type="hidden" name="mygrantuid${i}" value="${mygrantobj.userId}">
						<input type="hidden" name="mygrantutype${i}" value="${mygrantobj.userType}">
						<input type="hidden" name="mygrantinherit${i}" value="${mygrantobj.inherit}">
						<input type="hidden" name="mygrantalert${i}" value="${mygrantobj.alert}">
						<input type="hidden" name="mygrantaclid${i}" value="${mygrantobj.aclId}">
						<input type="hidden" id="mygrantalertnew${i}" name="mygrantalertnew${i}" value="${mygrantobj.alert}">
						<input type="hidden" name="mygrantalertid${i}" value="${mygrantobj.alertId}">
						<script>myOriginalElements.add('${mygrantobj.userId}')</script>
					</v3x:column>
					
					<v3x:column width="18%" align="center"
						label="${v3x:_(pageContext, 'doc.jsp.properties.share.alert')}">
						<input type='checkbox' align="center" id='mygrantalertckb${i}' name='mygrantalertckb${i}' onclick="mygrantalert('${i}')" value="${mygrantobj.userId}" ${v3x:outConditionExpression(mygrantobj.alert=='true', 'checked', '')}
								onchange="userChange('ucfPersonalShare')" />
					</v3x:column>
					<c:set value="${i+1}" var="i" />
				</v3x:table> 
				</fieldset>
				<table height="20%" width="100%">
				<tr>
				<td height="20" align="left"> 
					 <input	type="hidden" name="mygrantDocResId" value="${param.docResId}">
					 <input type="button" value="<fmt:message key='doc.jsp.properties.share.add'/>" class="button-default-2" onclick="selectPeopleFun_per()">&nbsp;&nbsp;&nbsp;&nbsp;
					 <input type="button" value="<fmt:message key='doc.jsp.properties.share.delete'/>" class="button-default-2" onclick="mygrantdeleteUser()">&nbsp;&nbsp;&nbsp;&nbsp;
				</td>
				</tr>
				</table>
				</div>
				<iframe name="grantIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0">
				</iframe>
				</td>
			</tr>
			</table>
		</td>
		</tr>
		
		<tr id="borrowTR" valign="top"  style="display:none">
			<td>
				<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
					
					<div class="scrollList" id="jieyue">
					<div class="docScrollList" id="jieyue-frameset" style="border: 1px #d3d3d3 solid;">
					<c:set value="0" var="i" />
					<v3x:table data="${borrowVO}" var="borrowobj" htmlId="borrowgrantId" dragable="false"
						isChangeTRColor="false" showHeader="true" showPager="false">						
						<v3x:column width="5%" align="center" label="<input type='checkbox' onclick='selectAll(this, \"borrowid\")'/>" >
							<input type='checkbox' name='borrowid' value="${borrowobj.userId}" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')} />
							<script type="text/javascript">
								var  sDateVar = '<fmt:formatDate value="${borrowobj.sdate}" pattern="yyyy-MM-dd" />';
								var  eDateVar = '<fmt:formatDate value="${borrowobj.edate}" pattern="yyyy-MM-dd" />';
								//alert('s:' + sDateVar + "  e:" + eDateVar)
							</script>
							<script type="text/javascript"> borrowNum++; </script>
						</v3x:column>
						<v3x:column width="20%" label="${v3x:_(pageContext, 'doc.jsp.properties.borrow.borrowobj')}">${borrowobj.userName}
							<input type="hidden" value="${borrowobj.userName}" name="borrowusername">
							<input type="hidden" value="${borrowobj.userName}" name="borrowusername${i}">
							<input type="hidden" name="borrowuid${i}" value="${borrowobj.userId}">
							<input type="hidden" name="borrowutype${i}" value="${borrowobj.userType}">
						
							<script>originalElementsborrow.add('${borrowobj.userId}')</script>
						</v3x:column>
						<c:if test="${doc_fr_type==2}">
						<v3x:column 
							label="${v3x:_(pageContext, 'doc.jsp.properties.borrow.lenPotent')}">
							<select name="lenPotent${i}" onchange="userChange('ucfBorrow');">
							<option value="1" <c:if test="${borrowobj.lenPotent==1}">selected</c:if>><fmt:message key='doc.jsp.properties.borrow.lenPotent.all'/></option>
							<option value="2" <c:if test="${borrowobj.lenPotent==2}">selected</c:if>><fmt:message key='doc.jsp.properties.borrow.lenPotent.content'/></option>							
							</select>
						</v3x:column>
						<v3x:column width="50%" 
							label="${v3x:_(pageContext, 'doc.jsp.properties.borrow.Operate')}">
							<input onclick="userChange('ucfBorrow');" type="checkbox" value="1" name="lenPotent2a${i}" <c:if test="${borrowobj.canSave=='1'}">checked</c:if>><fmt:message key='common.toolbar.save.label' bundle='${v3xCommonI18N}' />
							<input onclick="userChange('ucfBorrow');" type="checkbox" value="1" name="lenPotent2b${i}" <c:if test="${borrowobj.canPrint=='1'}">checked</c:if>><fmt:message key='common.toolbar.print.label' bundle='${v3xCommonI18N}' />
						</v3x:column>
						</c:if>
						<c:if test="${doc_fr_type!=2}">
	                        <v3x:column width="10%" label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.readonly')}">
					         <input onclick="userChange('ucfBorrow');" type='checkbox'  name="bRead${i}" id="bRead${i}" value="true" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')} <c:if test="${borrowobj.lenPotent2=='11'||borrowobj.lenPotent2=='10'}">checked</c:if> />
				            </v3x:column>
					        <v3x:column width="10%"	label="${v3x:_(pageContext, 'doc.jsp.properties.share.acl.browse')}">
						         <input onclick="userChange('ucfBorrow');" type='checkbox'  name="bBrowse${i}"	 id="bBrowse${i}" value="true" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')} <c:if test="${borrowobj.lenPotent2=='11'||borrowobj.lenPotent2=='01'}">checked</c:if>/>
					        </v3x:column>
						</c:if>
							
						<v3x:column  width="22%"
							label="${v3x:_(pageContext, 'doc.jsp.properties.borrow.begintime')}">
							<input<c:if test="${doc_fr_type==2}"> style="width:72px;"</c:if><c:if test="${doc_fr_type!=2}"> style="width:90px;"</c:if> readonly="readonly" validate="notNull" type="text" inputName="<fmt:message key='doc.jsp.properties.borrow.begintime'/>" name="begintime${i}"  value="<fmt:formatDate value='${borrowobj.sdate}' pattern='yyyy-MM-dd' />" onclick="whenstart('${pageContext.request.contextPath}',this,300,200,'date');userChangeCalendar(this,sDateVar)" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')}>
						</v3x:column>
						<v3x:column  width="22%"
							label="${v3x:_(pageContext, 'doc.jsp.properties.borrow.endtime')}">
							<input<c:if test="${doc_fr_type==2}"> style="width:72px;"</c:if><c:if test="${doc_fr_type!=2}"> style="width:90px;"</c:if> readonly="readonly" type="text" validate="notNull" inputName="<fmt:message key='doc.jsp.properties.borrow.endtime'/>"  name="endtime${i}"  value="<fmt:formatDate value='${borrowobj.edate}' pattern='yyyy-MM-dd' />" onclick="whenstart('${pageContext.request.contextPath}',this,300,200,'date');userChangeCalendar(this,eDateVar)" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')}>				
						</v3x:column>
						<c:set value="${i+1}" var="i" />
						
					</v3x:table> 
					</div>
					<table height="20%" width="100%">
						<tr>
							<td height="20" align="left"> 
								<input type="hidden" name="borrowDocResId" value="${param.docResId}">
								<input type="button" value="<fmt:message key='doc.jsp.properties.share.add'/>" class="button-default-2" onclick="selectPeopleFun_borrow()" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')}>&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" value="<fmt:message key='doc.jsp.properties.share.delete'/>" class="button-default-2" onclick="borrowDeleteUser()" ${v3x:outConditionExpression(param.isPerBorrow=='true', 'disabled', '')}>&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
						</tr>
					</table>
					
		</div>
					<iframe  name="bIframe" frameborder="0" height="0" width="0" scrolling="no" marginheight="0" marginwidth="0">
					</iframe>
				</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
<script>
	
	initFFScroll("shouquan",370);
	initFFScroll("scroll",370);
	initFFScroll("shouquan2",370);
	initFFScroll("jieyue",370);
	initFFScroll("jieyue-frameset",300);
	if(v3x.isFirefox){
		document.getElementById('jieyue').style.overflow='hidden';
	}
	initIpadScroll("scroll",370,450);
</script>
</body>
</html>