
/**
 * 加签窗口
 */
function preInsertPeople(summaryId, processId, affairId, appName, isForm, secretLevel){
	if(!checkModifyingProcessAndLock(processId, summaryId,appName)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
	var edoc = "";
	var targetWindow  = getA8Top();
	if(appName != "collaboration"){
		edoc = "&edoc=edoc";
		targetWindow = window;
	}

	if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
	        url: colWorkFlowURL + "?method=preInsertPeople&summaryId=" + summaryId + "&affairId=" + affairId + "&appName=" + appName + "&isForm=" + isForm + "&processId=" + processId + edoc + "&secretLevel="+secretLevel,
	        width: 400,
	        height: 280
	    });
	    rvInsertPeople(rv);
	}else{
		var divObj = "<div id=\"insertPeopleWin\" closed=\"true\">" +
					 	"<iframe id=\"insertPeopleWin_Iframe\" name=\"insertPeopleWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		targetWindow.$(divObj).appendTo("body");
		targetWindow.$("#insertPeopleWin").dialog({
			title: v3x.getMessage("collaborationLang.insertPeople"),
			top: 50,
			left:50,
			width: 630,
			height: 510,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = targetWindow.$("#insertPeopleWin_Iframe").get(0).contentWindow.OK();
							rvInsertPeople(rv);
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
						targetWindow.$('#insertPeopleWin').dialog('destroy');
						}
					}]
		});
		targetWindow.$("#insertPeopleWin_Iframe").attr("src",colWorkFlowURL + "?method=preInsertPeople&summaryId=" + summaryId + "&affairId=" + affairId + "&appName=" + appName + "&isForm=" + isForm + "&processId=" + processId + edoc);
	}
}

/**
 * 加签返回操作
 */
function rvInsertPeople(rv){
	if(!rv){
    	return;
    } else if(typeof rv == "string" && rv == "NoExists"){
    	try{
    		closeWindow();
    	}catch(e){}
    	return;
    }
}

/**
 * 减签窗口
 */
function preDeletePeople(summary_id, processId, affairId){
	if(!checkModifyingProcessAndLock(processId, summary_id)){
		return;
	}
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
	var appName = "";
	try{
	    appName = document.getElementById("appName").value;
	}catch(e){}
	var targetWindow  = getA8Top();
	if(appName == "4"){
		targetWindow = window;
	}
	
	if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
	        url: colWorkFlowURL + "?method=preDeletePeople&summary_id=" + summary_id + "&affairId=" + affairId + "&processId=" + processId,
	        width: 400,
	        height: 360
	    });
		rvDeletePeople(rv, summary_id, affairId);
	}else{
		var divObj = "<div id=\"deletePeopleWin\" closed=\"true\">" +
					 	"<iframe id=\"deletePeopleWin_Iframe\" name=\"deletePeopleWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		targetWindow.$(divObj).appendTo("body");
		targetWindow.$("#deletePeopleWin").dialog({
			title: v3x.getMessage("collaborationLang.alert_select_person"),
			width: 400,
			height: 360,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = targetWindow.$("#deletePeopleWin_Iframe").get(0).contentWindow.OK();
							rvDeletePeople(rv, summary_id, affairId);
							targetWindow.$('#deletePeopleWin').dialog('destroy');
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
						targetWindow.$('#deletePeopleWin').dialog('destroy');
						}
					}]
		});
		targetWindow.$("#deletePeopleWin_Iframe").attr("src",colWorkFlowURL + "?method=preDeletePeople&summary_id=" + summary_id + "&affairId=" + affairId + "&processId=" + processId);
	}
}

/**
 * 减签返回操作
 */
function rvDeletePeople(rv, summary_id, affairId){
	if(!rv){
    	return;
    }else if(typeof rv == "string" && rv == "NoExists"){
    	try{
    		closeWindow();
    	}catch(e){}
    	return;
    }
	  
    var appName = "";
	try{
	    appName = document.getElementById("appName").value;
	}catch(e){
		
	}
	if(appName=="1"){
		parent.detailMainFrame.workflowUpdate = true ;
	}else{
		workflowUpdate = true ;
	}
    var data = {
        userId : [],
        summary_id : summary_id,
        affairId : affairId,
        userName :[],
        userType :[],
        accountId : [],
        accountShortname : [],
        activityId : []
    };
    
    var people = toArray(rv[0]);
    var userName=toArray(rv[1]);
    var userType=toArray(rv[2]);
    var accountId = toArray(rv[3]);
    var accountShortname = toArray(rv[4]);
    var activityId = toArray(rv[5]);
        
    function toArray(object){
    	var newobject = [];
    	for(var i = 0; i < object.length; i++) {
    		newobject[i] = object[i];
    	}
    	return newobject;
    }
    
    data.userId = people;
    data.userName=userName;
    data.userType=userType;
    data.accountId=accountId;
    data.accountShortname=accountShortname;
    data.activityId=activityId;
    submitMap(data, colWorkFlowURL+"?method=deletePeople", "showDiagramFrame", "post");
}

/**
 * 当前会签窗口
 */
function preColAssign(summaryId, processId, affairId){
	if(!checkModifyingProcessAndLock(processId, summaryId)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
	var appName = "";
	try{
	    appName = document.getElementById("appName").value;
	}catch(e){}
	var targetWindow  = getA8Top();
	var app = "";
	if(appName == "4"){
		app = "&from=edoc&appName=4";
		targetWindow = window;
	}else{
	    app = "&from=collaboration&appName=1";
	}
	
	if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
        	url: colWorkFlowURL + "?method=preColAssign&summaryId=" + summaryId + "&affairId=" + affairId + "&processId=" + processId + app,
        	width: 400,
        	height: 280
    	});
    	rvColAssign(rv);
	}else{
		var divObj = "<div id=\"colAssignWin\" closed=\"true\">" +
					 	"<iframe id=\"colAssignWin_Iframe\" name=\"colAssignWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		targetWindow.$(divObj).appendTo("body");
		targetWindow.$("#colAssignWin").dialog({
			title: v3x.getMessage("collaborationLang.colAssign"),
			top: 50,
			left:50,
			width: 630,
			height: 480,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = targetWindow.$("#colAssignWin_Iframe").get(0).contentWindow.OK();
							rvColAssign(rv);
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
						targetWindow.$('#colAssignWin').dialog('destroy');
						}
					}]
		});
		targetWindow.$("#colAssignWin_Iframe").attr("src",colWorkFlowURL + "?method=preColAssign&summaryId=" + summaryId + "&affairId=" + affairId + "&processId=" + processId + app);
	}
}

/**
 * 当前会签返回操作
 */
function rvColAssign(rv){
	if(!rv){
		return;
	}else if(typeof rv == "string" && rv == "NoExists"){
    	try{
    		closeWindow();
    	}catch(e){
    		
    	}
    	return;
    }
}

/**
 * 知会窗口
 */
function addInform(_summary_id, _processId, _affairId) {
	if(!checkModifyingProcessAndLock(_processId, _summary_id)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
	if(v3x.getBrowserFlag('pageBreak')){
    	summary_id = _summary_id;
    	affairId = _affairId;
    	process_Id = _processId;
    	selectPeopleFun_addInform();
	}else{
		var app = document.getElementById("appName");
		var appName;
		if(app){
			appName = app.value;
		}
		var targetWindow  = getA8Top();
		if(appName != "collaboration"){
			targetWindow = window;
		}
		
		var divObj = "<div id=\"addInformWin\" closed=\"true\">" +
					 	"<iframe id=\"addInformWin_Iframe\" name=\"addInformWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		targetWindow.$(divObj).appendTo("body");
		targetWindow.$("#addInformWin").dialog({
			title: v3x.getMessage("collaborationLang.alert_select_person"),
			top: 50,
			left:50,
			width: 630,
			height: 450,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = targetWindow.$("#addInformWin_Iframe").get(0).contentWindow.OK();
							targetWindow.$('#addInformWin').dialog('destroy');
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
						targetWindow.$('#addInformWin').dialog('destroy');
						}
					}]
		});
		targetWindow.$("#addInformWin_Iframe").attr("src",colWorkFlowURL + "?method=preAddInform&summaryId=" + _summary_id + "&affairId=" + _affairId + "&processId=" + _processId + "&appName="+appName);
	}
}

/**
 * 知会选人后的回调
 */
function selectAddInform(elements) {
	
    if(!checkModifyingProcessAndLock(process_Id, summary_id)){
		return;
	}
    if (!elements[0] || elements[0] == undefined) return;
    
    var appName = "";
    var flowcomm = "inform" ;//协同
	
	try{
	    appName = document.getElementById("appName").value;
	}catch(e){}

	var people = "";
    
	if(appName == '4'){
	   flowcomm = "zhihui";
	}
	if(appName=="1"){
		parent.detailMainFrame.workflowUpdate = true ;
	}else{
		workflowUpdate = true ;
	}
	
	people = serializeElementsNoType(elements[0]);  
	 if (!people) {
        return;
    }
	var flowType = elements[1] || 0;
    var isShowShortName = elements[2] || "false";
    
    var data = {
        summary_id : summary_id,
        affairId : affairId,
        people: people,
        flowcomm:flowcomm,
        flowType : flowType,
        isShowShortName : isShowShortName,
        appName:appName
    };
    document.getElementById("process_desc_by").value = "people";
    submitMap(data, colWorkFlowURL + "?method=addInform", "showDiagramFrame", "post");
}

function updateFlash(processId,activityId,operationType,elements,commandType,manualSelectNodeId,peopleArr,summaryId,conditions,nodes,iscol){
	var rs = null;
	var str = "";
	
	var idArr = new Array();
	var typeArr = new Array();
	var nameArr = new Array();
	var accountIdArr = new Array();
	var accountShortNameArr = new Array();
	var selecteNodeIdArr = new Array();
	var _peopleArr = new Array();
	var conditionArr = new Array();
	var nodesArr = new Array();
	var userExcludeChildDepartmentArr = new Array();
	
	if(commandType == "addNode" || commandType == "replaceNode"){
		if (!elements) {
			return false;
		}
	
		var personList = elements[0] || [];
		var flowType = elements[1] || 0;
		var isShowShortName = elements[2] || "false";
		var process_desc_by = "people";
		
		str = processId + "," + activityId + "," + operationType + "," + flowType + "," + isShowShortName + "," + process_desc_by;
		
		for (var i = 0; i < personList.length; i++) {
			var person = personList[i];
			idArr.push(person.id);
			typeArr.push(person.type);
			nameArr.push(person.name);
			accountIdArr.push(person.accountId);
			accountShortNameArr.push(person.accountShortname);
			userExcludeChildDepartmentArr.push(person.excludeChildDepartment);
			selecteNodeIdArr = [];
			_peopleArr = [];
		}
	}else if(commandType == "delNode"){
		str = processId + "," + activityId + "," + operationType + "," + null + "," + null + "," + null;
		idArr = [];
		typeArr = [];
		nameArr = [];
		accountIdArr = [];
		accountShortNameArr = [];
		userExcludeChildDepartmentArr = [];
		if(manualSelectNodeId && peopleArr && manualSelectNodeId.length != 0 && peopleArr.length != 0){
			selecteNodeIdArr = arrayToArray(manualSelectNodeId);
			_peopleArr = arrayToArray(peopleArr);
		}else{
			selecteNodeIdArr = [];
			_peopleArr = [];
		}
		if(conditions && conditions.length!=0){
			conditionArr = arrayToArray(conditions);
			nodesArr = arrayToArray(nodes);
		}else{
			conditionArr = [];
			nodesArr = [];
		}
	}
	try {
		var requestCaller = new XMLHttpRequestCaller(null, "ajaxColSuperviseManager", "changeProcess", false, "POST");
		requestCaller.addParameter(1, "String", str);
		requestCaller.addParameter(2, "String[]", idArr);
		requestCaller.addParameter(3, "String[]", typeArr);
		requestCaller.addParameter(4, "String[]", nameArr);
		requestCaller.addParameter(5, "String[]", accountIdArr);
		requestCaller.addParameter(6, "String[]", accountShortNameArr);
		requestCaller.addParameter(7, "String[]", selecteNodeIdArr);
		requestCaller.addParameter(8, "String[]", _peopleArr);
		requestCaller.addParameter(9, "String", summaryId);
		requestCaller.addParameter(10, "String[]", conditionArr);
		requestCaller.addParameter(11, "String[]", nodesArr);
		requestCaller.addParameter(12, "boolean" ,iscol); 
		requestCaller.addParameter(13, "String[]", userExcludeChildDepartmentArr);
		rs = requestCaller.serviceRequest();
	}
	catch (ex1) {
		alert("Exception : " + ex1);
	}
    return rs;
}

function updateFlash1(flowProp,policyStr,summaryId,iscol){
	var rs = null;
	try {
		var requestCaller = new XMLHttpRequestCaller(null, "ajaxColSuperviseManager", "changeProcess1", false, "POST");
		requestCaller.addParameter(1, "String[]", arrayToArray(flowProp));
		requestCaller.addParameter(2, "String[]", arrayToArray(policyStr));
		requestCaller.addParameter(3, "String", summaryId);
		requestCaller.addParameter(3, "String", summaryId);
		requestCaller.addParameter(4, "boolean", iscol);
		rs = requestCaller.serviceRequest();
	}
	catch (ex1) {
		alert("Exception : " + ex1);
	}
	
    return rs;
}