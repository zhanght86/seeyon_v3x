function showUser(elements, sysFlag) {
	if (elements) {
		if (sysFlag == 'GROUP') {
			document.getElementById("userName").value = getNameString(elements,
					true);
			document.getElementById("users").value = getIdsString(elements,
					false);
		} else {
			document.getElementById("userName").value = getNameString(elements,
					false);
			document.getElementById("users").value = getIdsString(elements,
					false);
		}

	}
}

function canIO(userid) {
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxOrgIoManager",
			"canIO", false);
	if (userid != null) {
		requestCaller.addParameter(1, "String", userid);
	}
	var org = requestCaller.serviceRequest();
	// alert(org);
	return org;
}

function getNameString(elements, systemFlag) {
	if (!elements) {
		return "";
	}

	var sp = v3x.getMessage("V3XLang.common_separator_label");

	var names = [];
	for ( var i = 0; i < elements.length; i++) {
		var e = elements[i];
		var _name = null;
		if (e.accountShortname && systemFlag) {
			_name = e.name + "(" + e.accountShortname + ")";
		} else {
			_name = e.name;
		}

		names[names.length] = _name;
	}

	return names.join(sp);
}

/**
 * 导出Excel
 */
function exportExcel(){
	pageQueryMap = dataIFrame.pageQueryMap;
	if(pageQueryMap){
		if(parseInt(pageQueryMap.get('count')) > 10000){
			alert(v3x.getMessage("LogLang.logon_logs_beyond_maximum", 10000));
			return false;
		}
		else if(parseInt(pageQueryMap.get('count')) > 2000){
			if(!confirm(v3x.getMessage("LogLang.logon_to_excel"))){
				return;
			}
		}
	}

	var theForm = document.getElementById("appLogForm");
	if(theForm) {
    	var beginDate = pageQueryMap.get("beginDate") ||'';
		var endDate = pageQueryMap.get("endDate")||'';
		var selectPersonIds = pageQueryMap.get("selectPersonIds")||'';
	    theForm.target = "appLogDataExportExcel" ;
		var pageFrame = window.dataIFrame;
		var pageUri = pageFrame.location.href;
		var pageMethod = getUriParam(pageUri, "method");
	    theForm.action = secretAuditURL + "?method=exportExcel&pageMethod=" + pageMethod;
	    theForm.submit() ;
	}
}

/**
 * 导出Excel后的处理
 */
function exportOK() {
	try {
		myBar.enabled("exportExcel");
	} catch (e) {
	}
}

/**
 * 打印
 */
function doPrint(type) {
	var	pagerTd = dataIFrame.document.getElementById("pagerTd");
	var	printObj = dataIFrame.document.getElementById("scrollListDiv");
	
	if (pagerTd) {
		pagerTd.style.display = 'none';
	}
	
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/collaboration/css/collaboration.css");
	cssList.add(v3x.baseURL + "/common/css/default.css");
	cssList.add(v3x.baseURL + "/common/skin/default/skin.css");
	var pl = new ArrayList();

	if (printObj) {
		var html = printObj.innerHTML;
		html = html.replace(/like-a/gi, "").replace(/openList\S*\)/gi, "");
		var printObjFrag = new PrintFragment("", html);
		pl.add(printObjFrag);
		printList(pl, cssList);
	}
	if (pagerTd) {
		pagerTd.style.display = '';
	}
}