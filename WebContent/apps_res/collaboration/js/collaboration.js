try {
    getA8Top().endProc();
}
catch(e) {
}
//表单是否需要对数据为空校验标记
var isNeedCheckFormSave = true;
//是否进入分支调试模式
var isDebug = true;
//是否对流程进行了修改
var workflowUpdate = false ;
//是否是提交或者暂存待办操作
var isSubmitOperation = false;

//协同流程密级
//var flowSecretLevel_wf = 1;
//2017-01-11 诚佰公司
var flowSecretLevel_wf = "";

var flowSecretLevel_track = 1;

function beforeSubmit(affairid,attitude,content)
{
	  var formURL = '/seeyon/form.do?';
	  var requestCaller= new XMLHttpRequestCaller(this, "ajaxcollaborationBeforeListener", "preHandler",false);
	  requestCaller.addParameter(1, "long", affairid);
	  requestCaller.addParameter(2, "String", attitude);
	  requestCaller.addParameter(3, "String",content);
	  var ds1 = requestCaller.serviceRequest();
	  if(ds1 != null&&ds1!=''&&typeof(ds1) == "object")
	  {
		 if(ds1[0]=='1'){
			 //alert(v3x.getMessage("collaborationLang.thirdparty_error_tip"));
			 alert(ds1[1]);
			 disabledPrecessButton(false);
			 return true;
		 }else if(ds1[0]=='2'){
			 try{
				var rv = v3x.openWindow({
					url: formURL+"method=selectDeeTaskResult&isFrom=eventDee&isSearch=select&formId="+ds1[1]+"&eventDeeId="+encodeURIComponent(ds1[2])+"&summaryId="+encodeURIComponent(ds1[3])+"&operationId="+encodeURIComponent(ds1[4]),
					width : 650,
					height : 400,
					resizable: "no"
				});
				
				if (rv) {
					 var submitFlag=rv.indexOf("::");
					 if(submitFlag==(rv.length-2)){
						 if((rv.length-2)!=0){
							 var addContent =document.getElementById('content').value;
							 if(addContent){
								 addContent= addContent+"\r\n";
							 }
							 document.getElementById('content').value = addContent + rv.substring(0, submitFlag);
						 }
					 }else{
						 var addContent =document.getElementById('content').value;
						 if(addContent){
							 addContent= addContent+"\r\n";
						 }
						 document.getElementById('content').value = addContent + rv;
						 disabledPrecessButton(false);
					     return true;
					 }
			       }else{
				     disabledPrecessButton(false);
				     return true;
			             }
			 }catch (ex1) {
					 disabledPrecessButton(false);
					 return true;
				}
		
		 return false;
	  }
		  return false;
    }
}
/**
 * 按map中的内容当成数据提交
 * 注意:javascript调用时最好用return false,以免默认的Form也被提交
 * target默认是_self,method默认是post
 */
function submitMap(map, action, target, method) {
    var form = $('<FORM '
            + 'method="' + (method ? method : 'post') + '" '
            + 'action="' + action + '" '
            + 'target="' + (target ? target : '') + '"'
            + '/>');
    $("body").append(form);
    for (var item in map) {
        //自定义元素的
        if ((typeof(map[item]) == "object") && ("toFields" in map[item])) {
            var fields = map[item].toFields();
            $(form).append(fields);
        } else if (! (map[item] instanceof Array)) {
            var value = map[item];
            var field = $('<INPUT TYPE="hidden" name="' + item + '" value="' + value + '" />');
            form.append(field);
        } else {
            var arr = map[item];
            for(var i = 0; i < arr.length; i++) {
                var value = arr[i];
                var field = $('<INPUT TYPE="hidden" name="' + item + '" value="' + value + '" />');
                form.append(field);
            }
        }
    }

    form.submit();
    return true;
}

function map2params(map) {
    var container = [];
    for (var item in map) {
        if (! (map[item] instanceof Array)) {
            var value = map[item];
            var str = item + "=" + escape(value);
            container[container.length] = str;
        } else {
            var arr = map[item];
            for (var index in arr) {
                var value = arr[index];
                var str = item + "=" + escape(value);
                container[container.length] = str;
            }
        }
    }
    var result = container.join("&");
    return result;
}

var hasWorkflow = false;

//var workitemSelected = [];
//isFromTemplate
//通过ajax加载了模板的选人界面，并手工选了人。
var loadAndManualSelectedPreSend = false;
var isAllMember = true;
var isCheckNodePolicyFlag = false;

function preSend(tForm){
	var parameters = ["__ActionToken", "currentNodeId", "affair_id", "formData", "fromColsupervise", "appName", "processId", "isMatch",
	                  "caseId", "from", "summaryId", "process_desc_by", "process_xml", "userType", "userId", "userName", 
	                  "accountId", "accountShortname", "policyId", "policyName", "activityId", "node_process_mode", "flowType", "isShowShortName", 
	                  "advanceRemind", "deadline", "flowcomm", "edocType", "", "", "", "", "", ""
	                  ];
	
	var url = genericURL + "?method=preSend&isFromTemplate="+isFromTemplate;
	var requestCaller = new XMLHttpRequestCaller(this, null, null, false, "POST", true, url);
	
	var p = 1;
	var str = new StringBuffer();
	for ( var i = 0; i < parameters.length; i++) {
		var param = parameters[i];
		var obj = tForm[param];
		if(obj == null){
			continue;
		}

		if(obj.length){
			var len = obj.length;
			for ( var j = 0; j < len; j++) {
				var v = obj[j].value
				if(v == null){
					v = "";
				}
				str.append(param).append("=").append(encodeURIComponent(v)).append("&");
			}
		}
		else{
			var v = obj.value
			if(v == null){
				v = "";
			}
			
			str.append(param).append("=").append(encodeURIComponent(v)).append("&");
		}
	}
	
	return requestCaller.serviceRequest();
}


function send() {
	if(isForm=="true"&&mObject){
	   alert(v3x.getMessage("collaborationLang.formdisplay_hwversionerror")) ;
	   return ;
	}
	var members=document.getElementById("trackMembers");
	if(members){
		var trackRangePart=document.getElementById("trackRange_part");
		if(trackRangePart!=null&&trackRangePart.checked&&members.value==""){
			alert(_("collaborationLang.collaboration_alertSelectPeople"));
			return;
		}
	}
	
	disableButtons();
    //校验表单数据是否为空
    isNeedCheckFormSave = true;
    var theForm = document.getElementsByName("sendForm")[0];

	//2017-01-11 诚佰公司 添加密级空值校验
    //校验超期提醒，提前提醒时间
	if(!checkForm(theForm) || !checkSelectSecret() || !checkSelectWF() || !compareTime() || !checkSupervisor()){
		enableButtons();
		return;
	}
	
	
	var isNeedPreSend = true;//isFromTemplate || theForm.pagefrom.value;
	
    theForm.action = genericURL + "?method=send";
    //var html = $('#processModeSelectorContainer').html();
    //$('#processModeSelectorContainer').html("");
    
    if(isForm=="true"&&!validFieldData()){
    	enableButtons();
    	return;
    }
    if (!loadAndManualSelectedPreSend && isNeedPreSend) {
        disableButtons();
        
        if(isForm=="true"){
        	isFromWaitSend = true;
        	theForm.formData.value = genJSObject();
        	theForm.formSubject_value.value = genFormSubject_Object();
        }
        
        $('#sendForm').ajaxSubmit({
            //url : genericURL + "?method=preSend&isFromTemplate="+isFromTemplate,
            url : genericURL + "?method=prePopNew&isFromTemplate="+isFromTemplate,
            type : 'POST',
            async : false,
            success : function(data) {
            	//赋值给页面的popJsonId隐藏域
            	document.getElementById("popJsonId").value=data;
            	//转换成页面js的json对象
            	var dataObj= eval('('+data+')');
//                $('#processModeSelectorContainer').html(data);
            	var invalidateActivity= dataObj.invalidateActivity;
	            //if(document.getElementById("invalidateActivity")){
	            if(dataObj.invalidateActivity!=""){
                	//alert(_("collaborationLang.collaboration_invalidateActivity", document.getElementById("invalidateActivity").value));
                	alert(_("collaborationLang.collaboration_invalidateActivity", dataObj.invalidateActivity));
                	enableButtons();
                	return;
                }
                if(v3x.getBrowserFlag('OpenDivWindow')){
	            	//var ret = manualSelectByProcessMode();
		            var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
	                if (!ret) {
	                    loadAndManualSelectedPreSend = false;
	                    //$('#processModeSelectorContainer').html("");
	                    enableButtons();
	                    return;
	                } else {
	                    loadAndManualSelectedPreSend = true;
	                    if(document.getElementById("processXML") && document.getElementById("desc_by")){
	                    	var processXML = document.getElementById("processXML").value;
	                    	var desc_by = document.getElementById("desc_by").value;
	                    	if(processXML != "" && desc_by != ""){
	                    		document.getElementById("process_xml").value = processXML;
	                    		document.getElementById("process_desc_by").value = desc_by;
	                    	}
	                    }
	                    send();
	                }
                } else {
                	manualSelectByProcessModePopIpad(dataObj.isPop,dataObj.hasNewflow);
                	return ;
                }
            }
        });
        return;
    }
    
    //$('#processModeSelectorContainer').html(html);
    
    //节点权限引用更新
	if(!isCheckNodePolicyFlag){
		var policyArr = new Array();
	    var itemNameArr = new Array();
	    if(nodes != null && nodes.length>0){
	    	for(var i=0;i<nodes.length;i++){
	    		var policyName = policys[nodes[i]].name;
	    		var itemName = policys[nodes[i]].value;
	    		policyArr.push(policyName);
	    		itemNameArr.push(itemName);
	    	}
	    }
	    if(policyArr.length > 0){
			try {
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
				requestCaller.addParameter(1, "String[]", policyArr);
				requestCaller.addParameter(2, "String[]", itemNameArr);
				requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
				var rs = requestCaller.serviceRequest();
				if(rs == "1"){
					alert(_("collaborationLang.node_policy_not_existence"));
					enableButtons();
					return;
				}
			}
			catch (ex1) {
				alert("Exception : " + ex1);
				return;
			}
	    }
	}
    if(v3x.getBrowserFlag('htmlEditer') == true){
		if ((!isForm || isForm == "false") && !saveOffice()) {
            return;
        } 
    }else{
    	var content = document.getElementById('content');
    	if(content){
    		var  value = content.value;
    		value = value.replace(/\n/g,"<br/>");
    		content.value = value;
    	}
    }
	if(isForm=="true"){    
		saveColFieldSummaryDataMap('true') ;
	}    
	
	setAttParentform(theForm) ;
    saveAttachment();
    
    var branchNodes = document.getElementById("allNodes");
    if(branchNodes && branchNodes.value){
    	theForm.action += "&branchNodes=" + branchNodes.value;
    }
    
    var from = document.getElementById("from").value;
    if(from != 'a8genius' && isOpenFromGenius()){
					document.getElementById("from").value = 'a8genius';	
					from = 'a8genius';	        		
    }
    disableButtons();
    isFormSumit = true;      
    if(from == 'a8genius'){
        theForm.target = "a8geniusFrame";           
    }else{
        theForm.target = "_self";
    }
    //去除高级选项中disable属性设置
    theForm.allow_transmit.disabled = false;
    theForm.allow_chanage_flow.disabled = false;
    theForm.allow_edit.disabled = false;
    theForm.allow_edit_attachment.disabled = false;
    theForm.allow_pipeonhole.disabled = false;
    theForm.submit();   
    getA8Top().startProc('');
}

/**
 * 发起时，检测督办信息
 */
function checkSupervisor(){
	//设置了督办人员，就必须设置督办时间
	var supervisorId = $("#supervisorId").val();
	var unCancelledVisor = $("#unCancelledVisor").val();
	if(supervisorId){
		var supervisorIdsT = supervisorId.split(",");
		var supervisorIds = new Array();
		for (var i = 0 ; i < supervisorIdsT.length ; i ++) {
			if (supervisorIdsT[i] != null && supervisorIdsT[i].trim() != "")
				supervisorIds.push(supervisorIdsT[i]);
		}
		
		if(supervisorIds.length > 10){
			alert(_("collaborationLang.col_supervise_supervisor_overflow"));
			return false;
		}
		
		if(!$("#awakeDate").val()){
			alert(_("collaborationLang.col_supervise_select_date"));
			return false;
		}
		
		var sVisorsFromTemplate = $("#sVisorsFromTemplate").val();
		if(sVisorsFromTemplate != null && sVisorsFromTemplate == "true"){
			var uArray = unCancelledVisor.split(",");
			for(var i=0;i<uArray.length;i++){
				var have = supervisorId.search(uArray[i]);
				if(have == -1){
					alert(_("collaborationLang.col_supervise_template_supervisor"));
					return;
				}
			}
		}
	}else{
		if(unCancelledVisor){
			alert(_("collaborationLang.col_supervise_template_supervisor"));
			return;
		}
	}
	
	var superviseTitle = $("#superviseTitle").val();
	if(superviseTitle && superviseTitle.length>85){
		alert(_("collaborationLang.col_supervise_title_overflow"));
		return false;
		
	}
	
	//设置了督办主题或者督办时间，但没有设置督办人，给出提示
	if(($("#awakeDate").val() || $("#superviseTitle").val()) && !supervisorId){
		alert(_("collaborationLang.col_supervise_select_member"));
		return false;
	}
	
	return true;
}

/**
 * 给关联表单的上传附件赋值
 * 赋值的方法：将关联表单的summryid 赋值给extReference
 */
function setAttParentform(theForm){
	var parentformSummaryId  = "" ;
	if(theForm && theForm.parentformSummaryId){
		parentformSummaryId = theForm.parentformSummaryId.value ;
	}
	
	if(parentformSummaryId && parentformSummaryId != "" ){
		var atts = null;
		if(fileUploadAttachment != null){
			atts = fileUploadAttachment.values();
		}else{
			atts = fileUploadAttachments.values();
		}
		
		if(!atts || atts.size()<=0){
			return ;
		}
		
		for(var _index =0 ; _index < atts.size() ; _index++ ){
			var att = atts.get(_index) ;
			if( att.type == 3 || att.type == 4 || att.type == 1 ){
				att.extReference = parentformSummaryId ;
			}
		}
		
	}
}

/**
 * 保存待发
 */
function save() {
    var theForm = document.getElementsByName("sendForm")[0];
    
    //去除高级选项中disable属性设置
    theForm.allow_transmit.disabled = false;
    theForm.allow_chanage_flow.disabled = false;
    theForm.allow_edit.disabled = false;
    theForm.allow_pipeonhole.disabled = false;
    theForm.allow_edit_attachment.disabled = false;
    
    //校验超期提醒，提前提醒时间
	if(!compareTime() || !checkSupervisor()){
		return;
	}
	
	//节点权限引用更新
	if(!isCheckNodePolicyFlag){
		var policyArr = new Array();
	    var itemNameArr = new Array();
	    if(nodes != null && nodes.length>0){
	    	for(var i=0;i<nodes.length;i++){
	    		var policyName = policys[nodes[i]].name;
	    		var itemName = policys[nodes[i]].value;
	    		policyArr.push(policyName);
	    		itemNameArr.push(itemName);
	    	}
	    	
			try {
				var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
				requestCaller.addParameter(1, "String[]", policyArr);
				requestCaller.addParameter(2, "String[]", itemNameArr);
				requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
				var rs = requestCaller.serviceRequest();
				if(rs == "1"){
					alert(_("collaborationLang.node_policy_not_existence"));
					enableButtons();
					return;
				}
			}
			catch (ex1) {
				alert("Exception : " + ex1);
				return;
			}
	    }
	}
    
    theForm.action = genericURL + "?method=save";
	
    if (checkForm(theForm)) {
        if ((!isForm || isForm == "false")&&!saveOffice()) {
            return;
        }
        //表单保存待发时，不校验必填项是否为空
        isNeedCheckFormSave = false;

        if(isForm=="true"&&!validFieldData())
        	return;
		if(isForm=="true"){
			isFromWaitSend = true;
			theForm.formData.value = genJSObject();
			saveColFieldSummaryDataMap('true') ;
		}		      
        setAttParentform(theForm) ;
        saveAttachment();

        disableButtons();

        isFormSumit = true;

        var from = document.getElementById("from").value;
        disableButtons();
        isFormSumit = true;        
        if(from == 'a8genius'){
            theForm.target = "a8geniusFrame";           
        }else{
            theForm.target = "_self";
        }
        theForm.submit();   
        getA8Top().startProc('');     	
    }    
}
/**
 * 保存表单中的关联协同的数据
 * @param isfromSend
 */
function saveColFieldSummaryDataMap(isfromSend,saveColFieldSummary){
	var colFieldSummary ;
	if(isfromSend && isfromSend == 'false'){
		colFieldSummary = parent.detailMainFrame.contentIframe.getColFieldSummaryDataMap() ;
	}else if(isfromSend && isfromSend == 'true'){
		colFieldSummary = getColFieldSummaryDataMap() ;
	}
	if(!colFieldSummary){
		return ;
	}
	var saveColFieldSummaryObj = saveColFieldSummary || document.getElementById("saveColFieldSummary") ;
	
	if(!saveColFieldSummaryObj){
		return ;
	}
	
	var inputStr = "" ;
	for(var i=0; i< colFieldSummary.instanceKeys.size(); i++){
		var key = colFieldSummary.instanceKeys.get(i);
		var fieldList = colFieldSummary.get(key);
		if(fieldList && fieldList != null){
			for(var _index = 0 ;_index < fieldList.size() ; _index++){
				var field = fieldList.get(_index) ;
				if(isfromSend && isfromSend == 'true' && field.fieldState != "add"){
					continue ;
				}
				inputStr = inputStr + field.toInput() ;
			}
		}
	}
	saveColFieldSummaryObj.innerHTML = inputStr;
}

/**
 * 存为草稿
 */
//高级选项中disable属性
var allowTransmit = false;
var allowChanageFlow = false;
var allowEdit = false;
var allowEditAttachment = false;
var allowPipeonhole = false;
var actionName = "" ;
function saveDraft(){
    var theForm = document.getElementsByName("sendForm")[0];
    
    //校验超期提醒，提前提醒时间
	if(!compareTime() || !checkSupervisor()){
		return;
	}
    
    theForm.action = genericURL + "?method=saveDraft";

    if (checkForm(theForm)) {
        if ((!isForm || isForm == "false")&&!saveOffice()) {
            return;
        }
        //表单存为草稿时，不校验必填项是否为空
        isNeedCheckFormSave = false;
        
        if(isForm=="true"&&!validFieldData())
         
        	return;
		if(isForm=="true"){
			actionName = "saveDraft"
			isFromWaitSend = false;
			isClickSaveDraftBtn = true;
			document.getElementById("draft").value= "true";
            theForm.formData.value = genJSObject();
			saveColFieldSummaryDataMap('true') ;
		}
        saveAttachment();

		//去除高级选项中disable属性设置,提交前取出
		if(theForm.allow_transmit){
			allowTransmit = theForm.allow_transmit.disabled;
			theForm.allow_transmit.disabled = false;
		}
		if(theForm.allow_chanage_flow){
			allowChanageFlow = theForm.allow_chanage_flow.disabled;
			theForm.allow_chanage_flow.disabled = false;
		}
		if(theForm.allow_edit){
			allowEdit = theForm.allow_edit.disabled;
			theForm.allow_edit.disabled = false;
		}
		if(theForm.allow_edit_attachment){
			allowEditAttachment = theForm.allow_edit_attachment.disabled;
			theForm.allow_edit_attachment.disabled = false;
		}
		
		if(theForm.allow_pipeonhole){
			allowPipeonhole = theForm.allow_pipeonhole.disabled;
			theForm.allow_pipeonhole.disabled = false;
		}
        //disableButtons();

        theForm.target = "personalTempleteIframe";
        theForm.submit();
        var from = document.getElementById("from").value;      
        getA8Top().startProc('');      
    }
}

function endSaveDraft(summaryId,masterId){
	var theForm = document.getElementsByName("sendForm")[0];
	theForm.id.value = summaryId
	if(masterId)
		theForm.masterId.value = masterId;

	try {
	    getA8Top().endProc();
	}
	catch(e) {
	}

	//enableButtons();
	//恢复高级选项中disable属性
	if(theForm.allow_transmit){
		theForm.allow_transmit.disabled = allowTransmit;
	}
	if(theForm.allow_chanage_flow){
		theForm.allow_chanage_flow.disabled = allowChanageFlow;
	}
	if(theForm.allow_edit){
		theForm.allow_edit.disabled = allowEdit;
	}
	if(theForm.allow_pipeonhole){
		theForm.allow_pipeonhole.disabled = allowPipeonhole;
	}
	if(theForm.allow_edit_attachment){
		theForm.allow_edit_attachment.disabled = allowEditAttachment;
	}
}


function resend() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }

    var checkedNum = 0;
    var isNewflow = false;
    var summaryId = null;
    var len = id_checkbox.length;
    var checkedObj = null;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            checkedObj = id_checkbox[i];
            summaryId = checkedObj.value;
            if(checkedObj.getAttribute("isNewflow") == "true"){
            	isNewflow = true;
            }
            checkedNum ++;
        }
    }

    if (checkedNum == 0) {
        alert(_("collaborationLang.collaboration_alertSelectResentItem"));
        return;
    }

    if (checkedNum > 1) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertSelectResentOnlyOne"));
        return;
    }
    //由主流程自动触发的新流程不可重发
    if(isNewflow){
    	alert(v3x.getMessage("collaborationLang.warn_workflowIsNewflow_cannotResend"));
        return;
    }
	if(checkedObj.getAttribute("bodyType")=="FORM"){
		alert(v3x.getMessage("collaborationLang.collaboration_alertFormNoResend"));
		return;
	}
    parent.parent.location.href = genericURL + "?method=newColl&summaryId=" + summaryId + "&from=resend";
}

// 2017-01-11 诚佰公司 发送表单验证流程密级是否为空
function checkSelectSecret() {
    if (flowSecretLevel_wf == null || flowSecretLevel_wf == "") {
    	alert("流程密级不能为空。");
        return false;
    }

    return true;
}
// 诚佰公司

function checkSelectWF() {
    if (!hasWorkflow) {
        alert(v3x.getMessage("collaborationLang.collaboration_selectWorkflow"));
        selectPeopleFun_wf();

        return false;
    }

    return true;
}

function sendFromWaitSend() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }
	var id_checkbox = document.getElementsByName("id");
    var count = validateCheckbox("id");
    if(count == 1){
    	
    }
    else if(count == 0){
    	alert(v3x.getMessage("collaborationLang.collaboration_alertSentItem"));
        return;
    }
    else{
    	alert(v3x.getMessage("collaborationLang.collaboration_alertSentItemOnlyOne"));
 		return;
    }
    
    var summaryId = "";
    var affairId = "";
    var processId = "";
    //var caseId = "";
   // var templeteId = "";
    var isTemplate = false;
    var bodyType = "";
    for (var i = 0; i < id_checkbox.length; i++) {
        if (id_checkbox[i].checked) {
        	processId = id_checkbox[i].getAttribute("processId");
        	if(processId == ""){
        		alert(alert_noFlow);
    			return;
        	}
        	bodyType = id_checkbox[i].getAttribute("bodyType");
        	if(bodyType == "FORM"){
        		alert( _("collaborationLang.draft_submit_form_warning") );
    		   return;
        	}
        	summaryId = id_checkbox[i].getAttribute("value");
        	affairId = id_checkbox[i].getAttribute("affairId");
        	//caseId = id_checkbox[i].caseId;
        	//templeteId = id_checkbox[i].templeteId;
        	if(id_checkbox[i].templeteId != ""){
        		isTemplate = true;
        	}
            break;
        }
    }
	//check if need popup "select people" dialog
	//var html = $('#processModeSelectorContainer').html();
	//$('#processModeSelectorContainer').html("");
	if (!loadAndManualSelectedPreSend) {
        disableButtons();   
        $('#listForm').ajaxSubmit({
        	//url : genericURL + "?method=preSend&from=waitSend&summaryId=" + summaryId + "&processId="+ processId +"&currentNodeId=start&isFromTemplate=" + isTemplate,
        	url : genericURL + "?method=prePopNew&from=waitSend&summaryId=" + summaryId + "&processId="+ processId +"&currentNodeId=start&isFromTemplate=" + isTemplate,
        	type : 'post',
        	async : false,
            //target : '#processModeSelectorContainer',
            success : function(data) {            	
                //$('#processModeSelectorContainer').html(data);
            	//赋值给页面的popJsonId隐藏域
            	document.getElementById("popJsonId").value=data;
            	//转换成页面js的json对象
            	var dataObj= eval('('+data+')');
            	var invalidateActivity= dataObj.invalidateActivity;
	            //if(document.getElementById("invalidateActivity")){
	            if(dataObj.invalidateActivity!=""){
                //if(document.getElementById("invalidateActivity")){
                	//alert(_("collaborationLang.collaboration_invalidateActivity", document.getElementById("invalidateActivity").value));
	            	alert(_("collaborationLang.collaboration_invalidateActivity", dataObj.invalidateActivity));
                    enableButtons();
                	return;
                }
                
                try{
                if(formContent7777 && formContent7777!="")
    	        	templateForm(formContent7777,document.getElementById("scrollDiv"));
    	        }catch(e){}
    	        var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
                //var ret = manualSelectByProcessMode();
                if (!ret) {
                    loadAndManualSelectedPreSend = false;
                    //$('#processModeSelectorContainer').html("");
                    enableButtons();
                    return;
                } else {
                    loadAndManualSelectedPreSend = true;
                    sendFromWaitSend();
                }
            }
        });
        return;
    }
    //$('#processModeSelectorContainer').html(html);
    //var c = document.createElement("<input type='hidden' name='affairId' value='" + affairId + "'>")
    var c = document.createElement("input")
    c.setAttribute("type","hidden");
    c.setAttribute("name","affairId");
    c.setAttribute("value",affairId);
    theForm.appendChild(c);
   
    var surl = genericURL + "?method=sendImmediate";
    theForm.setAttribute("method","post");
    theForm.setAttribute("action",surl);
    theForm.setAttribute("target","_parent");
    //theForm.method = "post";
    //theForm.action = genericURL + "?method=sendImmediate";
    //theForm.target = "_parent";
    disableButtons();
    getA8Top().startProc('');
    theForm.submit();
}

function editFromWaitSend(summaryId, affairId){
	try{
		if(summaryId && affairId){
			if(parent.parent){
				isWaitSend = true;
				parent.parent.location.href = genericURL + "?method=newColl&summaryId=" + summaryId + "&affairId=" + affairId + "&from=WaitSend";				
			}
			return;
		}
		var id_checkbox = document.getElementsByName("id");
	    if (!id_checkbox) {
	        return;
	    }
	    var count = validateCheckbox("id");
	    if(count == 1){
	    	for(var i=0; i<id_checkbox.length; i++){
				var idCheckBox = id_checkbox[i];
				if(idCheckBox.checked){
					var summaryId = idCheckBox.value;
					var affairId = idCheckBox.getAttribute("affairId");
					isWaitSend = true;
					parent.parent.location.href = genericURL + "?method=newColl&summaryId=" + summaryId + "&affairId=" + affairId + "&from=WaitSend";
				}
			}
	    }
	    else if(count == 0){
	    	alert(v3x.getMessage("collaborationLang.collaboration_alertEditFlow"));
	        return;
	    }
	    else{
	    	alert(v3x.getMessage("collaborationLang.collaboration_confirmEditflowOnlyOne"));
	 		return;
	    }
	}catch(e){
	}
}

function serializeElements(_elements) {
    function SerializableElement(_elements) {
        var elements = _elements;
        this.toFields = function() {
            if (!elements) {
                return "";
            }
            var personList = elements[0] || [];
            var flowType = elements[1] || 0;
            var str = "";
            for (var i = 0; i < personList.length; i++) {
                var person = personList[i];
                str += '<input type="hidden" name="userType" value="' + person.type + '" />';
                str += '<input type="hidden" name="userId" value="' + person.id + '" />';
                str += '<input type="hidden" name="userName" value="' + person.name + '" />';
		        str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
		        str += '<input type="hidden" name="accountShortname" value="' + person.accountShortname + '" />';
                //        workFlowContent += person.name + ",";
            }
            str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
            return str;
        }
        return this;
    }
    return SerializableElement(_elements);
}

function serializeElementsNoType(_elements) {
    function SerializableElement(_elements) {
        var elements = _elements;
        this.toFields = function() {
            if (!elements) {
                return "";
            }
            var str = "";
            for (var i = 0; i < elements.length; i++) {
                var person = elements[i];
                str += '<input type="hidden" name="userType" value="' + person.type + '" />';
                str += '<input type="hidden" name="userId" value="' + person.id + '" />';
                str += '<input type="hidden" name="userName" value="' + person.name + '" />';
		        str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
		        str += '<input type="hidden" name="userExcludeChildDepartment" value="' + person.excludeChildDepartment + '" />';
		        str += '<input type="hidden" name="accountShortname" value="' + person.accountShortname + '" />';
            }
            //str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
            return str;
        }
        return this;
    }
    return SerializableElement(_elements);
}

function serializePeople(elements) {
    if (!elements) {
        return "";
    }

    var personList = elements[0] || [];
    var flowType = elements[1] || 0;

    var arr = [];
    //    var str = "";
    //    var workFlowContent = "";
    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
        //        str += '<input type="hidden" name="userType" value="' + person.type + '" />';
        //        str += '<input type="hidden" name="userId" value="' + person.id + '" />';

        var obj = new Object();
        obj.name = "userType";
        obj.value = person.type;
        arr.add(obj);

        obj = new Object();
        obj.name = "userId";
        obj.value = person.id;
        arr.add(obj);

        //        workFlowContent += person.name + ",";
    }

    //    str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
    var obj = new Object();
    obj.name = "flowType";
    obj.value = flowType;
    arr.add(obj);

    hasWorkflow = true;
    return arr;
}

//加签选人后的回调
function selectInsertPeople(elements) {
    document.getElementById("selectPeoplePanel").innerHTML = "";
    if (!elements) {
        return false;
    }

    var personList = elements[0] || [];
    var flowType = elements[1] || 0;
    var isShowShortName = elements[2] || "false";

    var str = "";
    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
        str += '<input type="hidden" name="userType" value="' + person.type + '" />';
        str += '<input type="hidden" name="userId" value="' + person.id + '" />';
        str += '<input type="hidden" name="userName" value="' + escapeStringToHTML(person.name) + '" />';
        str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
        str += '<input type="hidden" name="accountShortname" value="' + escapeStringToHTML(person.accountShortname) + '" />';
    }

    str += '<input type="hidden" name="flowType" value="' + flowType + '" />';
    str += '<input type="hidden" name="isShowShortName" value="' + isShowShortName + '" />';

    document.getElementById("selectPeoplePanel").innerHTML = str;
	document.getElementById("process_desc_by").value = "people";
	
	try { getA8Top().startProc(''); }catch (e) { }
	
    var form = document.forms.theform;
    form.action = genericURL + "?method=insertPeople";
    form.target = "showDiagramFrame";
    form.method = "post";
    form.submit();
    return false;
}

function selectInsertPeopleOK(){
	try { getA8Top().endProc(); }catch (e) { }
	parent.detailMainFrame.isLoadProcessXML = false;
	parent.detailMainFrame.initCaseProcessXML();
	parent.detailMainFrame.monitorFrame.location.href = parent.detailMainFrame.monitorFrame.location.href;
	if(document.getElementById("processAdvanceDIV")){
    	advanceViews(false);
    }
    var but = parent.detailMainFrame.document.getElementById("workflow_input");
    if(but){
    	parent.detailMainFrame.showPrecessAreaTd('workflow');
    }
}

//终止,要求保存终止的意见,附件信息
function stepStop(theForm)
{	
	parent.detailMainFrame.contentIframe.isSubmitOperation = true;
	//意见是否可为空
	var content = theForm.content;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && content && content.value == ''){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
	if(!checkModifyingProcessAndLock(theForm.processId.value, theForm.summary_id.value)){
		disabledPrecessButton(false);
		return;
	}
	
    if (!window.confirm(_("collaborationLang.collaboration_confirmStepStopItem")))
    {
    	disabledPrecessButton(false);
        return;
    }
    
    if(parent.detailMainFrame.contentIframe){
    	parent.detailMainFrame.contentIframe.isNeedCheckFormSave = false ;
    }
    
    if(parent.detailMainFrame.contentIframe.isForm && !parent.detailMainFrame.contentIframe.validFieldData()){
    		disabledPrecessButton(false);
    		isSubmitFinished = true;
        	return;
    	}
     var _theDelAttIds = "" ;
     if(parent.detailMainFrame.contentIframe){
       _theDelAttIds = delAttIds() ;
     }
    
    theForm.action = genericURL + "?method=stepStop&theDelAttIds="+_theDelAttIds;
     if(parent.detailMainFrame.contentIframe){
   	 	saveContenAtt();
   	 }
    saveAttachment();

    disabledPrecessButton(true);
    
    if(parent.detailMainFrame.contentIframe.isForm){
	    theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
	    theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
	    theForm.form.value = parent.detailMainFrame.contentIframe.form;
	    theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
	    theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
	}
	
    var erpContent;
    if(theForm.conten){
    	erpContent=theForm.content.value;
    }
  	if(beforeSubmit(theForm.affair_id.value,"stepstop",erpContent)){
   		return;
   	}
    try { //如果是弹出窗口，则不能显示“处理中”
        getA8Top().startProc('');
    }
    catch (e) {
    }
    theForm.submit();
}
//回退,要求保存回退的意见附件信息,保存正文信息
function stepBack(theForm, _summaryId) {
	parent.detailMainFrame.contentIframe.isSubmitOperation = true;
	if(!checkModifyingProcessAndLock(theForm.processId.value, _summaryId)){
		disabledPrecessButton(false);
		return;
	}
	//意见是否可为空
	var content = theForm.content;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && content && content.value == ''){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
	//AJAX校验是否可回退, 上节点触发的子流程已结束，不能回退
    //if(theForm.newflowType.value == "main"){ //确定是主流程，才做AJAX
	    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsCanStepBack", false);
			requestCaller.addParameter(1, "Long", _summaryId);
			requestCaller.addParameter(2, "String", theForm.processId.value);
			requestCaller.addParameter(3, "String", theForm.currentNodeId.value);
		var rs = requestCaller.serviceRequest();
		//if(rs != "TRUE" && rs != "FALSE"){
		//alert("rs:="+rs);
		if(rs[0] == "1"){//子流程
			//alert(v3x.getMessage("collaborationLang.warn_preNodeNewflowIsEnd_cannotStepBack", rs));
			alert(v3x.getMessage("collaborationLang.warn_newflowIsFirstNode_cannotStepBack", rs[1]));
			disabledPrecessButton(false);
			return;
		}else if(rs[0]=="0"){//主流程
			alert(v3x.getMessage("collaborationLang.warn_preNodeNewflowIsEnd_cannotStepBack", rs[1]));
			disabledPrecessButton(false);
			return;
		}
		//判断流程中上一节点是否为核定通过节点，有则不允许回退
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkUpIsVouch", false);
			requestCaller.addParameter(1, "Long", _summaryId);
			requestCaller.addParameter(2, "String", theForm.processId.value);
			requestCaller.addParameter(3, "String", theForm.currentNodeId.value);
		var vouchRs = requestCaller.serviceRequest();
		if(vouchRs == "TRUE1"){
			alert(v3x.getMessage("collaborationLang.warn_workflowIsVouched_cannotStepBack"));
			disabledPrecessButton(false);
			return;
		}else if (vouchRs == "TRUE2"){
			alert(v3x.getMessage("collaborationLang.warn_newflowIsVouched_cannotStepBack"));
			disabledPrecessButton(false);
			return;
		}
		if (!window.confirm(_("collaborationLang.collaboration_confirmStepBackItem"))){
			disabledPrecessButton(false);
	    	return;
	    }
	    var _theDelAttIds = "" ;
	    if(parent.detailMainFrame.contentIframe.isForm){
	    	_theDelAttIds = delAttIds();	
	    }
	    theForm.action = genericURL + "?method=stepBack&theDelAttIds="+_theDelAttIds;
	   	if(parent.detailMainFrame.contentIframe.isForm){
	   	 	saveContenAtt();
	   	 }    
	    saveAttachment();
	
	    if(!parent.detailMainFrame.contentIframe.saveContent()){
	    	disabledPrecessButton(false);
	   		return;
	   	}
	    
	    disabledPrecessButton(true);
	
	    if(parent.detailMainFrame.contentIframe.isForm){
	    	parent.detailMainFrame.contentIframe.fieldObjList = new Array();
	    	if(parent.detailMainFrame.contentIframe.disableSign!="true")
	    		theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
	    	theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
	    	theForm.form.value = parent.detailMainFrame.contentIframe.form;
	    	theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
	    	theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
	    }
	    
	    var erpContent;
	    if(theForm.conten){
	    	erpContent=theForm.content.value;
	    }
	   	if(beforeSubmit(theForm.affair_id.value,"stepBack",erpContent)){
	   		return;
	   	}
	   	
	    try { //如果是弹出窗口，则不能显示“处理中”
	        getA8Top().startProc('');
	    }
	    catch (e) {
	    }
	    theForm.submit();
}


//会签
function selectColAssign(elements) {
	if(!checkModifyingProcessAndLock(process_Id, summary_id)){
		return;
	}
	
    if (!elements[0] || elements[0] == undefined) return;
    var people = serializeElementsNoType(elements[0]);
    if (!people) {
        return;
    }
    var flowType = elements[1] || 0;
    var isShowShortName = elements[2] || "false";
    var data = {
        summary_id : summary_id,
        affairId : affairId,
        people: people,
        flowType : flowType,
        isShowShortName : isShowShortName
    };
    document.getElementById("process_desc_by").value = "people";
    submitMap(data, genericURL + "?method=colAssign", "showDiagramFrame", "post");
}

//会签
function colAssign(_summary_id, _processId, _affairId) {
	if(!checkModifyingProcessAndLock(_processId, _summary_id)){
		return;
	}
	
	//检测xmls是否已经被加载
	initCaseProcessXML();
	
    //设置成全局变量
    summary_id = _summary_id;
    affairId = _affairId;
    process_Id = _processId;
    selectPeopleFun_colAssign();
}

/**
 * 检查是否可以修改流程
 * 
 * @return true 可以， false 不可以，表示有其他人在修改流程
 */
function checkModifyingProcess(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkModifyingProcess", false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "Long", _summaryId);
    	var ds = requestCaller.serviceRequest();
    	if(ds != null && ds != "" && ds != "null"){
    		if(ds.startsWith("--NoSuchSummary--")){
    			alert(v3x.getMessage("collaborationLang.collaboration_hasCancelOrStepback"))
    			return;
    		}else if(ds.startsWith("--IsFinish--")){
    			alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsFinished"));
    	        return;
    		}
    		alert(v3x.getMessage("collaborationLang.editing_process", ds));
    		return false;
	    }
	    else{
	    	return true;
	    }
    }
    catch(e){
    	alert(e.message)
    }
    
	return false;
}

/**
 * 判断流程是否已经被锁定，未被锁定则给该流程加上一个同步锁，为接下来的修改流程做准备
 * 
 * @param _processId
 * @param _summaryId
 * @return true 可以， false 不可以，表示有其他人在修改流程
 */
function checkModifyingProcessAndLock(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "colCheckAndupdateLock", false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "Long", _summaryId);
    	var ds = requestCaller.serviceRequest();
    	if(ds != null && ds != "" && ds != "null"){
    	    if(ds.startsWith("--NoSuchSummary--")){
    			alert(v3x.getMessage("collaborationLang.collaboration_hasCancelOrStepback"))
    			return;
    		}
    		alert(v3x.getMessage("collaborationLang.editing_process", ds));
    		return false;
	    }
	    else{
	    	return true;
	    }
    }
    catch(e){
    	alert(e.message)
    }
    
	return false;
}

/**
 * 修改完流程，解除流程同步锁
 */
function colDelLock(_processId, _summaryId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "colDelLock", false,"GET",false);
    	requestCaller.addParameter(1, "String", _processId);
    	requestCaller.addParameter(2, "String", _summaryId);
    	if((arguments.length>2))
    	{
    		requestCaller.addParameter(3, "String", arguments[2]);	
    	}
    	requestCaller.serviceRequest();
    }catch(e){
    }
}

function setPeopleFields(elements, frameNames) {
    var theForm = document.getElementsByName("sendForm")[0];
    document.getElementById("people").innerHTML = "";
    if (!elements) {
        return false;
    }

    var personList = elements[0] || [];
    var flowType = elements[1] || 0; //1 并发、0串发
    var isShowShortName = elements[2];
    
    if(!isShowShortName){
    	isShowShortName = false;
    }

    var isShowWorkflow = (flowType == "2");
    //多层,直接弹出编辑流程图页面,已选中成员为并发模式
    if (isShowWorkflow) {
        flowType = 1;
    }

    var str = new StringBuffer();
    var workFlowContent = "";
    
    str.append("<processes>");
	str.append("<process isShowShortName=\"" + isShowShortName + "\" index=\"0\" sortIndex=\"0\" flowType=\"workflow\" y=\"0\" x=\"0\" type=\"Node\" desc=\"\" name=\"\" id=\"\">")

	//开始节点
	str.append("<node sortIndex=\"0\" y=\"\" x=\"\" type=\"8\" desc=\"\" name=\"\" id=\"start\">");
	str.append("	<actor accountShortName=\"\" accountId=\"\" condition=\"1\" partyIdName=\"\" partyTypeName=\"\" partyId=\"\" partyType=\"\" includeChild=\"false\" const=\"false\" role=\"\"/>");
	str.append("	<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"multiple\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"\" id=\"collaboration\"/>");
	str.append("</node>");
	
	//结束节点
	str.append("<node sortIndex=\"0\" y=\"430\" x=\"720\" type=\"4\" desc=\"\" name=\"end\" id=\"end\">");
	str.append("	<actor accountShortName=\"\" accountId=\"\" condition=\"1\" partyIdName=\"\" partyTypeName=\"\" partyId=\"\" partyType=\"\" includeChild=\"false\" const=\"false\" role=\"\"/>");
	str.append("	<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"multiple\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\""+_("collaborationLang.collaboration")+"\" id=\"collaboration\"/>");
	str.append("</node>");
	
	var nodeIds = [];

    for (var i = 0; i < personList.length; i++) {
        var person = personList[i];
		
		var nodeId = getUUID();
		
		nodeIds[nodeIds.length] = nodeId
		
		var type = person.type;
		if(type == 'Member'){
			type = 'user';
		}
		//单位简称特殊字符转义&
		var pShortName = person.accountShortname || "";
		if(pShortName){
			pShortName = pShortName.escapeXML();
		}
		
		var matchScope = 1;
		if(type == "Post" && person.accountId == "-1730833917365171641"){ //判断是否是集团标准岗位，偷了个懒，直接比id
			matchScope = 2; //集团标准岗，全集团范围内匹配
		}
		
		str.append("<node task_num_value=\"1\" personStatus=\"normal\" task_num=\"false\" finishNum2=\"1000\" finishNum=\"0\" type=\"6\" desc=\"\" name=\"" + person.name.escapeXML() + "\" id=\"" + nodeId + "\">");
		str.append("<actor accountShortName=\"" + pShortName + "\" accountId=\"" + person.accountId + "\" condition=\"1\" partyIdName=\"" + person.name.escapeXML() + "\" partyTypeName=\"\" partyId=\"" + person.id + "\" partyExcludeChildDepartment=\"" + person.excludeChildDepartment + "\" partyType=\"" + type + "\" includeChild=\"false\" const=\"false\" role=\"roleadmin\"/>");
		str.append("<seeyonPolicy matchScope=\"" + matchScope + "\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\""+_("collaborationLang.collaboration")+"\" id=\"collaboration\"/>");
		str.append("</node>");
		
		if(i < 12){
			if(i > 0){
	    		workFlowContent += _("V3XLang.common_separator_label")
	    	}
			var _text = person.name + "(" + _("collaborationLang.collaboration_nodePolicy_Collaboration") + ")";
	        if(isShowShortName == true && person.accountShortname != "null" && person.accountShortname != "undefined" && person.accountShortname != ""){
	        	_text = "(" + person.accountShortname + ")" + _text;
	        }
	        workFlowContent += _text;
		}
    }
    
    if(flowType == 0 || nodeIds.length == 1){ //串发(1个节点算串发)：两个节点之间创建线
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + nodeIds[0] + '" from="start" type="11" desc="" name="" id="' + getUUID() + '"/>');
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="end" from="' + nodeIds[nodeIds.length - 1] + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
    	
    	for(var i = 0; i < nodeIds.length - 1; i++) {
    		var f = nodeIds[i];
    		var t = nodeIds[i + 1];
    		
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + t + '" from="' + f + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
    	}
	}
	else{ //并发：创建split\join
		var splitId = getUUID();
		var joinId = getUUID();
		
		str.append("<node parallelismNodeId=\"\" start=\"true\" y=\"430\" x=\"240\" type=\"2\" desc=\"\" name=\"split\" id=\""+ splitId +"\">");
		str.append("	<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"协同\" id=\"collaboration\"/>");
		str.append("</node>");
		str.append("<node parallelismNodeId=\"\" start=\"false\" y=\"430\" x=\"560\" type=\"2\" desc=\"\" name=\"join\" id=\"" + joinId + "\">");
		str.append("	<seeyonPolicy matchScope=\"1\" operationName=\"\" form=\"\" formApp=\"\" isOvertopTime=\"0\" processMode=\"all\" remindTime=\"\" dealTerm=\"\" isDelete=\"false\" isPass=\"success\" desc=\"\" type=\"\" name=\"协同\" id=\"collaboration\"/>");
		str.append("</node>");
		
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + splitId + '" from="start" type="11" desc="" name="" id="' + getUUID() + '"/>');
		str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="end" from="' + joinId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
		
		for(var i = nodeIds.length - 1; i >= 0; i--) {
			var nId = nodeIds[i];
			
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + nId + '" from="' + splitId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
			str.append('<link conditionBase="" isForce="" conditionId="" formCondition="" conditionType="3" sortIndex="0" to="' + joinId + '" from="' + nId + '" type="11" desc="" name="" id="' + getUUID() + '"/>');
		}
	}

    str.append("</process></processes>");
    
    caseProcessXML = str.toString();

    hasWorkflow = true;
    isFromTemplate = false;
    
    var workflowInfoObj = document.getElementById("workflowInfo");
    if(workflowInfoObj){
    	workflowInfoObj.value = workFlowContent;
    }
	
    theForm.process_xml.value = caseProcessXML;
    theForm.process_desc_by.value = 'xml';
    
    //显示流程
    if (isShowWorkflow) {    
        designWorkFlow(frameNames);
    }

    return true;
}

// 会签or加签选择人员
function setPeopleInsert(elements) {
    var theForm = document.getElementsByName("insertPeopleForm")[0];
    document.getElementById("people").innerHTML = "";
    if (!elements) {
        return false;
    }
   
    var str = "";
    var workFlowContent = "";
    var isShowShortName = false;
    var policyValue = "" ;
    var theSelectObj = theForm.policy ;
    if(theSelectObj && theSelectObj.type == 'select-one'){
    	policyValue = theSelectObj.options[theSelectObj.selectedIndex].value;
    }
    
    if(document.getElementById("nodeProcessMode") && document.getElementById("nodeProcessMode").className ==""){
    	document.getElementById("nodeProcessMode").className="hidden" ;
    }
    if(document.getElementById("selectUserType")){
		document.getElementById("selectUserType").value="Member";
	}
    
    if(document.getElementById("currentLoginAccountId")){
    	var loginAccountId = document.getElementById("currentLoginAccountId").value;
	    for (var i = 0; i < elements.length; i++) {
		    var person = elements[i];
	    	if(person.accountId != loginAccountId){
	    		isShowShortName = true;
	    		break;
	    	}
	    }
	}
    
    for (var i = 0; i < elements.length; i++) {
    	if(i > 0){
    		workFlowContent += _("V3XLang.common_separator_label")
    	}
    	
        var person = elements[i];
        str += '<input type="hidden" name="userType" value="' + person.type + '" />';
        str += '<input type="hidden" name="userId" value="' + person.id + '" />';
        str += '<input type="hidden" name="userName" value="' + escapeStringToHTML(person.name) + '" />';
        str += '<input type="hidden" name="accountId" value="' + person.accountId + '" />';
        str += '<input type="hidden" name="userExcludeChildDepartment" value="' + person.excludeChildDepartment + '" />';
        str += '<input type="hidden" name="accountShortname" value="' + escapeStringToHTML(person.accountShortname) + '" />';
		
		var _text = person.name;// + "(" + _("collaborationLang.collaboration_nodePolicy_Collaboration") + ")";
        if(isShowShortName == true && person.accountShortname != "null" && person.accountShortname != "undefined" && person.accountShortname != ""){
        	_text = "(" + person.accountShortname + ")" + _text;
        }
        if(person.type != 'Member' && document.getElementById("nodeProcessMode") && (policyValue != "inform" && policyValue != "zhihui" )){
        	document.getElementById("nodeProcessMode").className="";
        }
        if(person.type != 'Member'){
        	if(document.getElementById("selectUserType")){
        		document.getElementById("selectUserType").value=person.type;
        	}
        }        
        workFlowContent += _text;
    }
    if(str!="")
	{
    	str += '<input type="hidden" name="isShowShortName" value="' + isShowShortName + '" />';
	}
    document.getElementById("people").innerHTML = str;
    if(v3x.getBrowserFlag('pageBreak')){
	    var workflowInfoObj = document.getElementById("workflowInfo");
		workflowInfoObj.value = workFlowContent;
    }
    return true;
}

/**
 * 设置节点属性
 */
function selectPolicy(partyId, partyType, nodeName, policyName, dealTerm, remindTime, processMode, 
		matchScope, isTemplete, appName, formApp , form, operationName, nodeType, isEditor, 
		defaultPolicyId,showSenderName,nodeState, hasNewflow, hasBranch, isShowApplyToAll,
		formfiled,summaryId,nodeId,desc,dealTermType,dealTermUserId,dealTermUserName,secretLevel) {
	var theHeight = 500;
    formfiled = formfiled ||"";
    if (isTemplete) {
    	if(nodeState == 1 || nodeState == "1"){
    		theHeight += 50;
    		if(partyType == 'Post'){
    			theHeight += 50;
    		}
    	}
    	if(formApp && formApp!='undefined' && formApp != 'null'){
			theHeight += 50;
    	}
    }
    if(typeof(summaryId) == "undefined") summaryId ="";
    var rv = v3x.openWindow({
        url: genericURL + "?method=selectPolicy&partyId=" + partyId + "&partyType=" + partyType + "&nodeName=" + encodeURIComponent(nodeName)
                + "&policyName=" + encodeURIComponent(policyName) + "&dealTerm=" + encodeURIComponent(dealTerm) + "&matchScope=" + matchScope
                + "&remindTime=" + encodeURIComponent(remindTime) + "&processMode=" + encodeURIComponent(processMode)
                + "&isTemplete=" + encodeURIComponent(isTemplete) + "&appName=" + encodeURIComponent(appName)
                + "&formApp=" + encodeURIComponent(formApp) + "&form=" + encodeURIComponent(form) + "&operationName=" + encodeURIComponent(operationName)
                + "&nodeType=" + encodeURIComponent(nodeType) + "&isEditor=" + encodeURIComponent(isEditor) + "&defaultPolicyId=" + encodeURIComponent(defaultPolicyId)
                + "&showSenderName="+showSenderName + "&isNewColl="+_isNewColl + "&nodeState=" + nodeState + "&hasNewflow=" + hasNewflow + "&hasBranch="+hasBranch 
                + "&isShowApplyToAll=" + isShowApplyToAll+"&formfiled="+encodeURIComponent(formfiled)+"&summaryId="+summaryId
                + "&nodeId="+nodeId+"&desc="+encodeURIComponent(desc)+"&dealTermType="+encodeURIComponent(dealTermType)
                + "&dealTermUserId="+encodeURIComponent(dealTermUserId)+"&dealTermUserName="+encodeURIComponent(dealTermUserName)+"&secretLevel="+secretLevel,
        height:theHeight,
        width:400
    });
//    for(var i=0;i<rv.length;i++){
//		alert("rv["+i+"]:="+rv[i]);
//    }
    if (!rv || rv.length == 0) return null;
    return rv;
}

/**
 * 查看节点属性
 */

function checkPolicy(stateStr, nodeName, nodePolicy, receiveTime, completeTime, overtopTime, isTemplete, processMode, 
		policyId, dealTime, remindTime, appName, affairId, partyId, partyType, matchScope,templeteId,nodeId,
		summaryId,random  , desc,formApp,formId,operationId ,dealTermType,dealTermUserId,dealTermUserName) {
    v3x.openWindow({
        url: genericURL + "?method=checkPolicy&stateStr=" + encodeURIComponent(stateStr)
                + "&nodeName=" + encodeURIComponent(nodeName) + "&nodePolicy=" + encodeURIComponent(nodePolicy)
                + "&receiveTime=" + encodeURIComponent(receiveTime) + "&completeTime=" + encodeURIComponent(completeTime) 
                + "&overtopTime=" + encodeURIComponent(overtopTime) + "&isTemplete=" + encodeURIComponent(isTemplete)
                + "&processMode=" + encodeURIComponent(processMode) + "&policyId=" + encodeURIComponent(policyId)
                + "&dealTime=" + encodeURIComponent(dealTime) + "&remindTime=" + encodeURIComponent(remindTime)
                + "&appName=" + encodeURIComponent(appName) + "&affairId=" + encodeURIComponent(affairId)
                + "&partyId=" + partyId + "&partyType=" + partyType + "&matchScope=" + matchScope
                + "&templeteId="+templeteId+"&nodeId="+nodeId+"&summaryId="+summaryId+"&random="+random
                + "&desc="+encodeURIComponent(desc)
                + "&formApp="+formApp+"&formId="+formId+"&operationId="+operationId
                + "&dealTermType="+encodeURIComponent(dealTermType)
                + "&dealTermUserId="+encodeURIComponent(dealTermUserId)
                + "&dealTermUserName="+encodeURIComponent(dealTermUserName),
        height:450,
        width:340
    });
}

function openDetail(subject, _url) {
	var url2 = "";
	if(parent.parent.openerSummaryId){
		if(_url.indexOf("openerSummaryId") > 0){
			
			var start = _url.indexOf("openerSummaryId") + "openerSummaryId=".length;
			var a1 = _url.substring(start);
			var end = a1.indexOf("&");
			
			url2 = _url.substring(0, start - "openerSummaryId=".length) + "openerSummaryId=" + parent.parent.openerSummaryId;
			if(end > -1){
				url2 += a1.substring(end);
			}
		}
		else{
			_url += "&openerSummaryId=" + parent.parent.openerSummaryId;
		}
	}
	
    var rv = v3x.openWindow({
        url: genericURL + "?method=detail&" + (url2 ? url2 : _url),
        workSpace: 'yes',
        dialogType: v3x.getBrowserFlag('pageBreak') == true ? 'modal' : '1'
    });

	if(!rv){
    	return;
    }
    if(typeof rv == "string" && rv == "true"){
    	try{
    		if(parent.listFrame){
				parent.listFrame.location.href=parent.listFrame.location.href;
    		}
    		else{
				getA8Top().reFlesh();
    		}
		}catch(e){
		}   	
    }
}

/**
 * 删除
 */
function deleteIt() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            hasMoreElement = true;
            break;
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertDeleteItem"));
        return;
    }

    if (window.confirm(v3x.getMessage("collaborationLang.collaboration_confirmDeleteItem"))) {
        theForm.action = deleteActionURL;

        disableButtons();
        theForm.target = "_self";
        theForm.method = "POST";
        theForm.submit();
    }
}

var page_types = {
    'draft' : 'draft',
    'sent' : 'sent',
    'pending' : 'pending',
    'finish' : 'finish'
}
function deleteItems(pageType) {
    if (!pageType || !page_types[pageType]) {
        alert('pageType is illegal:' + pageType);
        return false;
    }

    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var obj;
    var isMustOpinionAffairs = "";
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	obj = id_checkbox[i];
            hasMoreElement = true;
            countChecked++;
            if(pageType == "pending"){
            	var isTempleteValue= obj.getAttribute("isTemplete");
				var processIdValue= obj.getAttribute("processId");
				var objectValue= obj.getAttribute("value");
				var isMustOpinionValue= obj.getAttribute("isMustOpinion");
				var colSubjectValue= obj.getAttribute("colSubject");
            	if(isTempleteValue == "true"){
            		alert(v3x.getMessage("collaborationLang.pendingTemplete_cannotArchiveOrDelete"));
            		obj.checked = false;
        			return;
            	}
        		else if(!checkModifyingProcess(processIdValue, objectValue)){
        			obj.checked = false;
					return;
				}
				else if(isMustOpinionValue == "true"){
            		isMustOpinionAffairs += colSubjectValue + "\n";
            		obj.checked = false;
            	}
            }
        }
    }
    
    //必须填写意见的事项
    if(isMustOpinionAffairs != ""){
		alert(v3x.getMessage("collaborationLang.pending_cannotArchiveOrDelete_requiredOpinion", isMustOpinionAffairs));
		return;
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertDeleteItem"));
        return true;
    }

    if (window.confirm(v3x.getMessage("collaborationLang.collaboration_confirmDeleteItem"))) {
        theForm.action = collaborationCanstant.deleteActionURL;
        disableButtons();
        
        for (var i = 0; i < id_checkbox.length; i++) {
            var checkbox = id_checkbox[i];
            if (!checkbox.checked)
                continue;
            var affairId = checkbox.getAttribute("affairId");
            //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
            var element1 = document.createElement("input");
	    	element1.setAttribute('type','hidden');
	    	element1.setAttribute('name','affairId');
	    	element1.setAttribute('value',affairId);
            
            theForm.appendChild(element1);
        }

        //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
        var element2 = document.createElement("input");
    	element2.setAttribute('type','hidden');
    	element2.setAttribute('name','pageType');
    	element2.setAttribute('value',pageType);
        theForm.appendChild(element2);

        getA8Top().startProc('');
        theForm.target = "_self";
        theForm.method = "POST";
        theForm.submit();
        return true;
    }
}

//取回
function takeBack(pageType) {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var obj;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	obj = id_checkbox[i];
            hasMoreElement = true;
            countChecked++;
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertTakeBackItem"));
        return true;
    }
    
    if (countChecked > 1){
    	alert(v3x.getMessage("collaborationLang.collaboration_confirmTakeBackOnlyOne"));
        return true;
    }

    //已结束流程不能取回
    var isFinishValue= obj.getAttribute("isFinish");
    if(isFinishValue == "true"){
    	alert(alert_cannotTakeBack);
    	return false;
    }
    var nodePolicyValue= obj.getAttribute("nodePolicy");
    if(nodePolicyValue == "zhihui" || nodePolicyValue == "inform"){
    	alert(v3x.getMessage("collaborationLang.collaboration_zhihuiTakeBackItem"));
        return;
    }
	//由主流程自动触发的新流程不可撤销
    var isNewflowValue= obj.getAttribute("isNewflow");
    if(isNewflowValue == "true"){
    	//alert(v3x.getMessage("collaborationLang.warn_workflowIsNewflow_cannotTakeBack"));
        //return;
    }
    //判断判断本节点是否为核定通过节点，有则不允许取回
    var processIdValue= obj.getAttribute("processId");
    var affairIdValue= obj.getAttribute("affairId");
    var summaryId = obj.value;
	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkSelfIsVouch", false);
	requestCaller.addParameter(1, "Long", summaryId);
	requestCaller.addParameter(2, "String", processIdValue);
	requestCaller.addParameter(3, "String", affairIdValue);
	var rs = requestCaller.serviceRequest();
	if(rs == "TRUE1"){
		alert(v3x.getMessage("collaborationLang.warn_workflowIsVouched_cannotTakeBack"));
		return;
	}else if(rs == "TRUE2"){
		alert(v3x.getMessage("collaborationLang.warn_newflowIsVouched_cannotTakeBack"));
		return;
	}
	if(!checkModifyingProcess(processIdValue, summaryId)){
		return;
	}
	if(v3x.getBrowserFlag('OpenDivWindow')){
	    var rv = getA8Top().v3x.openWindow({
	        url: genericControllerURL + "collaboration/takebackConfirm",
	        width: 270,
	       	height: 150,
	       	resizable: "no"
	    });
	    if(rv){
	        theForm.action = collaborationCanstant.takeBackActionURL;
	
	        disableButtons();
	
	        var affairId = obj.getAttribute("affairId");
	        //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
	        var element = document.createElement("input");
	        	element.setAttribute('type','hidden');
	        	element.setAttribute('name','affairId');
	        	element.setAttribute('value',affairId);
	        //var element1 = document.createElement("<INPUT TYPE=HIDDEN NAME=summaryId value='" + summaryId + "' />");
	        var element1 = document.createElement("input");
    	        element1.setAttribute('type','hidden');
    	        element1.setAttribute('name','summaryId');
    	        element1.setAttribute('value',summaryId);
	        var saveOpinion = (rv =="1");
	        //var element2 = document.createElement("<INPUT TYPE=HIDDEN NAME=saveOpinion value='" + saveOpinion + "' />");
	        var element2 = document.createElement("input");
	        element2.setAttribute('type','hidden');
	        element2.setAttribute('name','saveOpinion');
	        element2.setAttribute('value',saveOpinion);
	        theForm.appendChild(element);
	        theForm.appendChild(element1);
	        theForm.appendChild(element2);
	
	      //var element3 = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
	        var element3 = document.createElement("input");
	        element3.setAttribute('type','hidden');
	        element3.setAttribute('name','pageType');
	        element3.setAttribute('value',pageType);
	        theForm.appendChild(element3);
	        
	        getA8Top().startProc('');
	
	        theForm.target = "_self";
	        theForm.method="post";
	        theForm.submit();
	        
	        return true;
	    }
	}else{
	    var tack_back_win = new MxtWindow({
	        id: 'tack_back_win',
	        title: '',
	        url: genericControllerURL + "collaboration/takebackConfirm",
	        width: 250,
	        height: 200,
			type:'window',//类型window和panel为panel的时候title不显示
			isDrag:false,//是否允许拖动
	        buttons: [{
				id:'btn1_tack_back_win',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){
		        	var rv = tack_back_win.getReturnValue();
		    	    if(rv){
		    	        theForm.action = collaborationCanstant.takeBackActionURL;
		    	
		    	        disableButtons();
		    	
		    	        var affairId = obj.getAttribute("affairId");
		    	        //var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
		    	        var element = document.createElement("input");
		    	        	element.setAttribute('type','hidden');
		    	        	element.setAttribute('name','affairId');
		    	        	element.setAttribute('value',affairId);
		    	        //var element1 = document.createElement("<INPUT TYPE=HIDDEN NAME=summaryId value='" + summaryId + "' />");
		    	        var element1 = document.createElement("input");
			    	        element1.setAttribute('type','hidden');
			    	        element1.setAttribute('name','summaryId');
			    	        element1.setAttribute('value',summaryId);
		    	        var saveOpinion = (rv =="1");
		    	        //var element2 = document.createElement("<INPUT TYPE=HIDDEN NAME=saveOpinion value='" + saveOpinion + "' />");
		    	        var element2 = document.createElement("input");
		    	        element2.setAttribute('type','hidden');
		    	        element2.setAttribute('name','saveOpinion');
		    	        element2.setAttribute('value',saveOpinion);
		    	        
		    	        theForm.appendChild(element);
		    	        theForm.appendChild(element1);
		    	        theForm.appendChild(element2);
		    	
		    	        //var element3 = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
		    	        var element3 = document.createElement("input");
		    	        element3.setAttribute('type','hidden');
		    	        element3.setAttribute('name','pageType');
		    	        element3.setAttribute('value',pageType);
		    	        
		    	        
		    	        theForm.appendChild(element3);
		    	        getA8Top().startProc('');
		    	
		    	        theForm.target = "_self";
		    	        theForm.method="post";
		    	        theForm.submit();
		    	        
		    	        return true;
		    	    }
					//tack_back_win.close();
	        	}
	        }, {
				id:'btn2_tack_back_win',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        		tack_back_win.close();
	            }
	        }]
	    
	    });
	}
}

//催办回调
function hastenCallback() {
    $("#flashContainer").children(".information").css("background-color", "yellow").html(_("collaborationLang.operation_completed"));
    setTimeout('$("#flashContainer").children(".information").hide()', 2000);
}

//催办提交
function submitHasten() {
    var val = $(".additional_remark");
    val = val[0].value;
    $("#hastenPanel").hide();
    var data = {
        processId : processId,
        activityId : activityId,
        additional_remark : val
    };
 
    $.post(collaborationCanstant.hastenActionURL, data, hastenCallback);
}

//催办功能的两个全局变量
var processId = null;
var activityId = null;
//催办
function preHasten(summary_id, memberIdStr, activityId, superviseId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "initMemberIds", false, "POST");
		requestCaller.addParameter(1, "String", memberIdStr);
		requestCaller.addParameter(2, "String", activityId);
		requestCaller.serviceRequest();
	}
	catch (ex1) {
	}
	
	var rv = v3x.openWindow({
        url: genericURL + "?method=preHasten&summaryId=" + summary_id + "&activityId=" + activityId+"&superviseId="+superviseId,
        height: 450,
        width: 380
    });
}

//公文催办
function edocHasten(summary_id, processId, activityId,superviseId, memberIdStr){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "initMemberIds", false, "POST");
		requestCaller.addParameter(1, "String", memberIdStr);
		requestCaller.addParameter(2, "String", activityId);
		requestCaller.serviceRequest();
	}
	catch (ex1) {
	}
	var rv = v3x.openWindow({
        url: edocSuperviseURL + "?method=hasten&processId="+processId+"&activityId="+activityId+"&superviseId="+superviseId
        + "&summary_id=" + encodeURIComponent(summary_id),
		height: 450,
        width: 380
    });	
    //如果返回值为true(对某节点发送了消息),流程页面的返回值也为true
	 if(!rv){
    	return;
    }
}

function hastenbak(_processId, _activityId) {
    processId = _processId;
    activityId = _activityId;

    $("#hastenPanel").show();

    return false;
}

//催办对话框取�?
function cancelHasten() {
    $("#hastenPanel").hide();
}

//从待办列表中直接发送的选人界面
function selectPeopleSendImmediate() {
    selectPeopleFun_receive();
}

function selectPeopleFromWaitSend(elements) {
    var str = serializePeople(elements);

}

function forwardMail(){
	//alert();
	var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return true;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var selectedCount = 0;
    var summaryId = null;
    var affairId = null;
    var bodyType = null;
    
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	if(id_checkbox[i].getAttribute("bodyType") == "FORM"){
            	alert(v3x.getMessage("collaborationLang.collaboration_alertFormForwardEmail",id_checkbox[i].getAttribute("colSubject")));
          		return false;
            }
            summaryId = id_checkbox[i].value;
            affairId = id_checkbox[i].getAttribute("affairId");
            bodyType = id_checkbox[i].getAttribute("bodyType");
            selectedCount++;
        }
    }

    if (selectedCount == 0) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertSelectForwardItem"));
        return true;
    }
    
    try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasForward", false);
    	requestCaller.addParameter(1, "String", summaryId);
    	requestCaller.addParameter(2, "String", "transMail");
    	var ds = requestCaller.serviceRequest();
    	if(ds && ds == "false"){
    		alert(v3x.getMessage("collaborationLang.unallowed_forward_affair"));
    		return true;
    	}
    }catch(e){
    }

    if (selectedCount > 1) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertSelectForwardOnlyOne"));
        return true;
    }
    //是否设置了邮箱
    try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxWebMailManager", "hasDefaultMailBox", false);
    	requestCaller.addParameter(1, "String", "currentUser");
    	var ds = requestCaller.serviceRequest();
    	if(ds && ds=="false"){
    		parent.parent.location.href= mailURL + "?method=create";
    		return false;
    	}
    }catch(e){
    }
	
	//如果是表单转发内容单独处理
    if(bodyType=="FORM"){
    	if(!loadFormContent){
	    	var html = $('#formContainer').html();
	        $('#formContainer').html("");
		    //disableButtons();
		    $('#sendForm').ajaxSubmit({
		        url : genericURL + "?method=getFormContent&summaryId=" + summaryId + "&affairId=" + affairId,
		        type : 'POST',
		        target : '#formContainer',
		        success : function(data) {
		           // $('#formContainer').html(data);         
		            enableButtons();
		            loadFormContent = true;
		            forwardMail();
		        }
		    });
		    return;   
	    }
	    templateForm(formContent6666,document.getElementById("scrollDiv"));
	    var imgDiv = document.getElementById("img");
	    if(imgDiv)
	    	imgDiv.outerHTML = "";
	    loadFormContent = false;
	    mailForm.formContent.value = document.getElementById("area").innerHTML;
    }
    
	mailForm.action=genericURL + "?method=forwordMail&id=" + summaryId ;
	mailForm.target="_parent";
	mailForm.submit();
    return true;
	
}

var loadFormContent = false;

/**
 * 列表菜单－转发协同
 */
function forwardItem() {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return true;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }

    var selectedCount = 0;
    var summaryId = null;
    var affairId = null;
    var bodyType = null;
    
    var len = id_checkbox.length;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            summaryId = id_checkbox[i].value;
            affairId = id_checkbox[i].getAttribute("affairId");
            bodyType = id_checkbox[i].getAttribute("bodyType");
            selectedCount++;
        }
    }

    if (selectedCount == 0) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertSelectForwardItem"));
        return true;
    }

    if (selectedCount > 1) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertSelectForwardOnlyOne"));
        return true;
    }
    
    try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasForward", false);
    	requestCaller.addParameter(1, "String", summaryId);
    	requestCaller.addParameter(2, "String", "transColl");
    	var ds = requestCaller.serviceRequest();
    	if(ds && ds == "false"){
    		alert(v3x.getMessage("collaborationLang.unallowed_forward_affair"));
    		return true;
    	}
    }catch(e){}
    
    forwardColV3X(summaryId, affairId, "list");
    
    return true;
}

/**
 * 处理页面－转发协同
 */
var forwardWin;
function signForward(affairId, summaryId){
    //判断是否允许转发
    try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "hasForward", false);
    	requestCaller.addParameter(1, "String", summaryId);
    	requestCaller.addParameter(2, "String", "transColl");
    	var ds = requestCaller.serviceRequest();
    	if(ds && ds == "false"){
    		alert(v3x.getMessage("collaborationLang.unallowed_forward_affair"));
    		return true;
    	}
    }catch(e){}
    
    if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
	    	url : genericURL + "?method=showForward&summaryId=" + summaryId + "&affairId=" + affairId,
	    	width : 360,
	    	height : 420
	    });
	    
	    rvSignForward(rv);
	    return true;
    }else{
    	var divObj = "<div id=\"forwardWin\" closed=\"true\">" +
					 	"<iframe id=\"forwardWin_Iframe\" name=\"forwardWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		getA8Top().$(divObj).appendTo("body");
		getA8Top().$("#forwardWin").dialog({
			title: v3x.getMessage("collaborationLang.forward"),
			top: 50,
			left:50,
			width: 630,
			height: 630,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = getA8Top().$("#forwardWin_Iframe").get(0).contentWindow.OK();
							rvSignForward(rv);
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
							getA8Top().$('#forwardWin').dialog('destroy');
						}
					}]
		});
		getA8Top().$("#forwardWin_Iframe").attr("src",genericURL + "?method=showForward&summaryId=" + summaryId + "&affairId=" + affairId);
    }
}

/**
 * 转发返回操作
 */
function rvSignForward(rv){
	if(!rv){
    	return;
    }else if(typeof rv == "string" && rv == "NoExists"){
    	closeWindow();
    	return;
    }else if(typeof rv == "string" && rv == "true" && (getParameter("method") == "listSent")){
    	document.location.reload(true);
    }
}

function disableButtons() {
    myBar.disableAllButtons();
}

function enableButtons() {
    myBar.enableAllButtons();
}

function openDetailNewWin(url) {
    var rv = getA8Top().v3x.openWindow({
        url: url,
        workSpace: "true"
    });

    if (rv != null && (rv == "true" || rv)) {
        refreshIt();
    }
}

function showDetail(detailURL1) {
    //    setTimeout("parent.detailFrame.location.href = '" + detailURL + "'",1000);
    parent.detailFrame.location.href = genericURL + "?method=detail&list=list&" + detailURL1;
}

function doWorkFlow(flag) {
    if (flag == "no") {
        //TODO 清空流程
    }
    else if (flag == "new") {
    	// 2017-01-18 诚佰公司
    	if (!editWorkFlow()) {
    		return;
    	}
    	result = selectPeopleFun_wf();
    }
    activeOcx();
}

function selectPersonToXml()
{
  	processing=true;
 	var url=genericControllerURL + "collaboration/monitor&comm=toxml";
    var toXmlFrame=document.getElementById("toXmlFrame");
 	toXmlFrame.src=url;
}
function doPigeonholeWindow(flag, appName, from,obj) {
	if(v3x.getBrowserFlag('openWindow') == true){
		doPigeonhole(flag, appName, from);
	}else{
		doPigeonholeIpad(flag, appName,from,obj);
	}	
}
//预归档
function doPigeonhole(flag, appName, from) {
    if (flag == "no") {
        //TODO 清空信息
    }
    else if (flag == "new") {
        var result;
    	if(from == "templete"){
        	result = pigeonhole(appName, null, false, false);
    	}else{
    		result = pigeonhole(appName,null);
    	}
    	var theForm = document.getElementsByName("sendForm")[0];
        if(result == "cancel"){
        	var oldPigeonholeId = theForm.archiveId.value;
    		var selectObj = theForm.colPigeonhole;
        	if(oldPigeonholeId != "" && selectObj.options.length >= 3){
				selectObj.options[2].selected = true;
        		//var oldOption = document.getElementById(oldPigeonholeId);
        		//oldOption.selected = true;
        	}
        	else{
        		var oldOption = document.getElementById("defaultOption");
        		oldOption.selected = true;
        	}
        	return;
        }
        var pigeonholeData = result.split(",");
        pigeonholeId = pigeonholeData[0];
        pigeonholeName = pigeonholeData[1];
        if(pigeonholeId == "" || pigeonholeId == "failure"){
        	theForm.archiveName.value = "";
        	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
        }
        else{
        	var oldPigeonholeId = theForm.archiveId.value;
        	theForm.archiveId.value = pigeonholeId;
        	if(document.getElementById("prevArchiveId")){
        		document.getElementById("prevArchiveId").value = pigeonholeId;
        	}
        	var selectObj = document.getElementById("colPigeonhole");
        	var option = document.createElement("OPTION");
        	option.id = pigeonholeId;
        	option.text = pigeonholeName;
        	option.value = pigeonholeId;
        	option.selected = true;
        	if(oldPigeonholeId == "" && selectObj.options.length<=2){
        		selectObj.options.add(option, selectObj.options.length);
        	}
        	else{
        		selectObj.options[selectObj.options.length-1] = option;
        	}
        }
    }
}
//预归档
function doPigeonholeIpad(flag, appName,from,obj) {
	
    if (flag == "no") {
        //TODO 清空信息
    }else if (flag == "new") {
        var result;
    	//newIdes=null;
    	var atts = undefined;
    	var type = "";
    	var validAcl = undefined;
    	if(from == "templete"){
        	result = pigeonhole(appName, null, false, false);
    	}else{
    		
    		result = v3x.openDialog({
    	    	id:"pigeonholeIpad",
    	    	title:v3x.getMessage("collaborationLang.file_save"),
    	    	url : pigeonholeURL + "?method=listRoots&isrightworkspace=pigeonhole&appName=" + appName + "&atts=" + atts + "&validAcl=" + validAcl+"&pigeonholeType="+type,
    	    	width: 350,
    	        height: 400,
    	        //isDrag:false,
    	        //targetWindow:getA8Top(),
    	        //fromWindow:window,
    	        type:'panel',
    	        relativeElement:obj,
    	        buttons:[{
    				id:'btn1',
    	            text: v3x.getMessage("collaborationLang.submit"),
    	            handler: function(){    	        	
    	        		var returnValues = result.getReturnValue();
	    	        	var theForm = document.getElementsByName("sendForm")[0];
	    	            var pigeonholeData = returnValues.split(",");
	    	            pigeonholeId = pigeonholeData[0];
	    	            pigeonholeName = pigeonholeData[1];
	    	            if(pigeonholeId == "" || pigeonholeId == "failure"){
	    	            	theForm.archiveName.value = "";
	    	            	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
	    	            }
	    	            else{
	    	            	var oldPigeonholeId = theForm.archiveId.value;
	    	            	theForm.archiveId.value = pigeonholeId;
	    	            	if(document.getElementById("prevArchiveId")){
	    	            		document.getElementById("prevArchiveId").value = pigeonholeId;
	    	            	}
	    	            	var selectObj = document.getElementById("colPigeonhole");
	    	            	var option = document.createElement("OPTION");
	    	            	option.id = pigeonholeId;
	    	            	option.text = pigeonholeName;
	    	            	option.value = pigeonholeId;
	    	            	option.selected = true;
	    	            	if(oldPigeonholeId == "" && selectObj.options.length<=2){
	    	            		selectObj.options.add(option, selectObj.options.length);
	    	            	}
	    	            	else{
	    	            		selectObj.options[selectObj.options.length-1] = option;
	    	            	}
	    	            }
		        		result.close();
	            }
    	            
    	        }, {
    				id:'btn2',
    	            text: v3x.getMessage("collaborationLang.cancel"),
    	            handler: function(){
    	        		result.close();
    	            }
    	        }]
    	    });
    	}
    }
}

/**
 * 处理后事�?
 */
function doEndSign() {
    if (window.dialogArguments) {
        window.returnValue = "true";
        getA8Top().close();
    }
    else {
        parent.detailFrame.location.href = v3x.baseURL + "/common/detail.jsp";
    }
}

function manualSelectByProcessMode() {
	if(!document.getElementById("showProcessModeSelector")){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
    var showProcessModeSelector = document.getElementById("showProcessModeSelector").innerText;
	var hasNewflow = false;
	if(document.getElementById("hasNewflow")){
		hasNewflow = true;
	}
    if (showProcessModeSelector == '2' || hasNewflow) {
        var rv = v3x.openWindow({
            url: genericControllerURL + "collaboration/popupProcessModeSelector&hasNewflow=" + hasNewflow,
            width: 580,
            height: 500
        });

        if (rv != "True") {
            return false;
        }
    }
    else if (showProcessModeSelector == '1') {
        //选人界面

    }
    return true;

}
//ipad单人执行
function manualSelectByProcessModeIpad(theForm, action, isPop, hasNewflow) {
	var popDataObj= $('#popJsonId').val();
	if(popDataObj==""){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
	 if (isPop=="true") {
    	var rv_win = v3x.openDialog({
	    	id:"repealItems_win",
	    	title:'',
	    	url :genericControllerURL + "collaboration/popupProcessModeSelector&hasNewflow=" + hasNewflow,
	    	width: 580,
	        height: 500,
	        type:'window',
	        targetWindow:parent.detailMainFrame,
	        buttons:[{
				id:'rv_win_btn1',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){
	        		var rv = rv_win.getReturnValue();
	        		//alert(rv);
	                if (rv != "True") {
		            	loadAndManualSelectedPreSend = false;
		            	$('#processModeSelectorContainer').html("");
		                disabledPrecessButton(false);
		                return;
	                }else{
	                	rv_win.close();
		            	loadAndManualSelectedPreSend = true;
		                doSign(theForm, action);
	                }
            }
	            
	        }, {
				id:'rv_win_btn2',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        		rv_win.close();
	            }
	        }]
	    });
    }else{
    	loadAndManualSelectedPreSend = true;
        doSign(theForm, action);
    }

}
function manualSelectByProcessModePop(isPop,hasNewflow) {
	var popDataObj= $('#popJsonId').val();
	if(popDataObj==""){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
    if (isPop=="true") {
        var rv = v3x.openWindow({
        	url: genericControllerURL + "collaboration/popupProcessModeSelector&hasNewflow=" + hasNewflow +"&secretLevel="+flowSecretLevel_wf,
            width: 580,
            height: 500
        });
        if (rv != "True") {
            return false;
        }
    }
    return true;
}

function manualSelectByProcessModePopIpad(isPop, hasNewflow){
	var popDataObj= $('#popJsonId').val();
	if(popDataObj==""){
		alert("操作失败：网络异常，系统无法获取所需的提交数据。");
		return false;
	}
	if (isPop=="true") {
		var rv_win = v3x.openDialog({
	    	id:"sendSelector_win",
	    	title:'',
	    	url :genericControllerURL + "collaboration/popupProcessModeSelector&hasNewflow=" + hasNewflow,
	    	width: 580,
	        height: 500,
	        type:'window',
	        //targetWindow:parent.detailMainFrame,
	        buttons:[{
				id:'rv_win_btn1',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){
	        		var rv = rv_win.getReturnValue();
	                if (rv != "True") {
		            	loadAndManualSelectedPreSend = false;
		            	//$('#processModeSelectorContainer').html("");
		                enableButtons();
		                return ;
	                }else{
	                	rv_win.close();
		            	loadAndManualSelectedPreSend = true;
		                if(document.getElementById("processXML") && document.getElementById("desc_by")){
		                	var processXML = document.getElementById("processXML").value;
		                	var desc_by = document.getElementById("desc_by").value;
		                	if(processXML != "" && desc_by != ""){
		                		document.getElementById("process_xml").value = processXML;
		                		document.getElementById("process_desc_by").value = desc_by;
		                	}
		                }
		                send();
	                }
            }
	            
	        }, {
				id:'rv_win_btn2',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	            	enableButtons();
	        		rv_win.close();
	            }
	        }]
	    });
	} else {
		loadAndManualSelectedPreSend = true;
        if(document.getElementById("processXML") && document.getElementById("desc_by")){
        	var processXML = document.getElementById("processXML").value;
        	var desc_by = document.getElementById("desc_by").value;
        	if(processXML != "" && desc_by != ""){
        		document.getElementById("process_xml").value = processXML;
        		document.getElementById("process_desc_by").value = desc_by;
        	}
        }
        send();
	}
}
function finishWorkItem(theForm, action){
	disabledPrecessButton();
	if(disagree()){//不同意
		disabledPrecessButton(false);
		return;
	}
	doSign(theForm, action);
}
function doSign(theForm, action) {
	parent.detailMainFrame.contentIframe.isSubmitOperation = true;
	if(parent.detailMainFrame.hasNotFinishNewflow){
		alert(_("collaborationLang.warn_newflowIsNotEnd_cannotProcess", parent.detailMainFrame.noFinishNewflow));
		disabledPrecessButton(false);
		return;
	}

    if (checkForm(theForm)) {
		if(!loadAndManualSelectedPreSend && !checkModifyingProcess(theForm.processId.value, theForm.summary_id.value)){
			disabledPrecessButton(false);
			return;
		}
		//意见是否可为空
		var content = theForm.content;
		var temp = content ;
		var opinionPolicy = theForm.opinionPolicy;
		if (temp != null) {
			temp = temp.value.trim().replace(/(\r)*\n/g,"").trim() ;
			if(opinionPolicy && opinionPolicy.value==1 && content && temp.length == 0){
				disabledPrecessButton(false);
				alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
				return;
			}
		}
    	//节点匹配
	    //var html = $('#processModeSelectorContainer').html();
	    //$('#processModeSelectorContainer').html("");
	    if (!loadAndManualSelectedPreSend) {
	    	if(parent.detailMainFrame.contentIframe.isForm){
	        	if(!parent.detailMainFrame.contentIframe.validFieldData()){
	        		disabledPrecessButton(false);
	        		isSubmitFinished = true;
	            	return;
	        	}
	            if(!checkFormExist(parent.detailMainFrame.contentIframe.formApp,parent.detailMainFrame.contentIframe.form,parent.detailMainFrame.contentIframe.operation)){
	            	disabledPrecessButton(false);
	            	isSubmitFinished = true;
	            	return;
	            }
	        }
	    	disabledPrecessButton();
	        
	    	if(parent.detailMainFrame.contentIframe.isForm){
	    	    theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
	    	    theForm.formDisplayValue.value = parent.detailMainFrame.contentIframe.genFormSubject_Object();
	    	    theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
	    	    theForm.form.value = parent.detailMainFrame.contentIframe.form;
	    	    theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
	    	    theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
	    	}
	    	
	        $('#theform').ajaxSubmit({
	        	//url : genericURL + "?method=preSend&isFromTemplate="+templateFlag,
	        	url : genericURL + "?method=prePopNew&isFromTemplate="+templateFlag,
	            type : 'POST',
	            async : false,
	            success : function(data) {
	            	//赋值给页面的popJsonId隐藏域
	            	document.getElementById("popJsonId").value=data;
	            	//转换成页面js的json对象
	            	var dataObj= eval('('+data+')');
		            //$('#processModeSelectorContainer').html(data);
	            	var invalidateActivity= dataObj.invalidateActivity;
		            //if(document.getElementById("invalidateActivity")){
		            if(dataObj.invalidateActivity!=""){
                    	//alert(_("collaborationLang.collaboration_invalidateNode", document.getElementById("invalidateActivity").value));
                    	alert(_("collaborationLang.collaboration_invalidateNode", dataObj.invalidateActivity));
                    	disabledPrecessButton(false);
                    	return;
                    }
		            //ipad提交后弹出单人执行选择页面
		            if(v3x.getBrowserFlag('OpenDivWindow')){
		            	//var ret = manualSelectByProcessMode();
			            var ret = manualSelectByProcessModePop(dataObj.isPop,dataObj.hasNewflow);
			            if (!ret) {
			            	loadAndManualSelectedPreSend = false;
			            	//$('#processModeSelectorContainer').html("");
			                disabledPrecessButton(false);
			                return;
			            } else {
			            	loadAndManualSelectedPreSend = true;
			                doSign(theForm, action);
			            }
		            }else{
		            	manualSelectByProcessModeIpad(theForm,action,dataObj.isPop,dataObj.hasNewflow);
		            	return;
		            }
	        	}
	        });
	        return;
	    }
	    isSubmitFinished = false;
	    //$('#processModeSelectorContainer').html(html);
        
        if (document.getElementById("delete") && document.getElementById("delete").checked && !window.confirm(_("collaborationLang.collaboration_confirmSignAfterDelete"))) {
            disabledPrecessButton(false);
            isSubmitFinished = true;
            return;
        }
        
        if(document.getElementById("pipeonhole")){
        	if (document.getElementById("pipeonhole").checked) {	
	           	var appName = theForm.appName.value;
	           	var summaryId = theForm.summary_id.value;
	           	var affairId = theForm.affair_id.value;
	           	var attsFlag = theForm.attsFlag.value;
	           	var archiveId = pigeonhole(appName, affairId, attsFlag);
	           	if(archiveId == "cancel"){
	           		isSubmitFinished = true;
	           		disabledPrecessButton(false);
	           		return;
	           	}
	            if(!archiveId || archiveId == "failure"){
	            	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
	            	isSubmitFinished = true;
	            	disabledPrecessButton(false);
	            	return;
	            }else{
	            	theForm.archiveId.value = archiveId;
	            }
	        }
        }
    	if(!parent.detailMainFrame.contentIframe.saveContent()){
    		disabledPrecessButton(false);
    		isSubmitFinished = true;
    		return;
    	}
    	if(!saveISignature())return false;
		if(!checkModifyingProcessAndLock(theForm.processId.value, theForm.summary_id.value)){
			isSubmitFinished = true;
			disabledPrecessButton(false);
			return;
		}
		if(parent.detailMainFrame.contentIframe.isForm){
			saveColFieldSummaryDataMap('false');
		}
		
		/**
		 * 正文附件的保存
		 */
		var delAttIds = "" ;
		
        if(parent.detailMainFrame.contentIframe){
        	var contentAtt = parent.detailMainFrame.contentIframe.fileUploadAttachments ;
        	if(contentAtt){
        		var keys = contentAtt.instanceKeys ;
        		for(var len = 0 ; len < keys.size() ; len++){
        			fileUploadAttachments.put(keys.get(len),contentAtt.get(keys.get(len))) ;
        			if(theForm.newflowType && theForm.newflowType == 'child' && parentIdSummerId){
        				fileUploadAttachments.get(keys.get(len)).extReference = parentIdSummerId ;
        			}
        		}
        	}
        	var contentRelAttachmentsMap = parent.detailMainFrame.contentIframe.relAttachmentsMap;
        	if(contentRelAttachmentsMap){
        		var keys = contentRelAttachmentsMap.instanceKeys;
        		for(var i = 0; i < keys.size(); i++){
        			var key = keys.get(i);
        			var tmpRelAttachments = contentRelAttachmentsMap.get(key);
        			var keys2 = tmpRelAttachments.instanceKeys;
    				var relAttachments = new Properties();
        			for(var j = 0; j < keys2.size(); j++){
        				var key2 = keys2.get(j);
	        			relAttachments.put(key2, tmpRelAttachments.get(key2));
        			}
        			relAttachmentsMap.put(key, relAttachments);
        		}
        	}
        	var delAtt = parent.detailMainFrame.contentIframe.theHasDeleteAtt ;  
        	if(delAtt){
        		var keys = delAtt.instanceKeys ;
        		for(var len = 0 ; len < keys.size() ; len++){
        			if(len == 0){
        				delAttIds = delAtt.get(keys.get(len)).id ;
        			}else{
        				delAttIds += "," + delAtt.get(keys.get(len)).id ;
        			}
        		}
        	}        	          
        }
       		
		var obj = document.getElementById("hasSaveAttachment");
        
        if(obj && obj.value!="false"){
        	setAttParentform(theForm) ;  
        	saveAttachment();
        }else if(!fileUploadAttachments.isEmpty()){
        	setAttParentform(theForm) ;  
        	saveAttachment();
        }else if(!relAttachmentsMap.isEmpty()){
        	setAttParentform(theForm) ;  
        	saveAttachment();
        }
        //google浏览器action传不过来   onclick="doSign(this.form,formAction)
        if((action == '' || action == null) &&  (formAction != null)){
        	action = formAction;
        }
        theForm.action = action + "&theDelAttIds=" + delAttIds;
        
        var branchNodes = document.getElementById("allNodes");
        if(branchNodes && branchNodes.value){
        	theForm.action += "&branchNodes=" + branchNodes.value;
        }
        var erpContent;
        if(content){
        	erpContent=content.value.trim().replace(/(\r)*\n/g,"");
        }
        var attitudes = document.getElementsByName("attitude");
        var attitude=null;
    	for(var i=0;i<attitudes.length;i++){
    		if(attitudes[i].checked){
    			attitude=attitudes[i].value;
    			//1是已阅,2是同意,3是不同意
    		}
    	}
    	if(attitude==null){
    		attitude='1';
    	}
    	if(beforeSubmit(theForm.affair_id.value,attitude,erpContent)){
    		return;
    	}
        theForm.method = "POST";
        //theForm.target = "_self";
    	theForm.target = "showDiagramFrame";
        //前端防护
        if(!theForm.affair_id){
        	alert("处理操作失败！\n系统无法获取所需的提交数据，请您重新打开处理。");
        	try{
        		parent.closeWindow();
        	}
        	catch(e){
	        	disabledPrecessButton();
        	}
    		return;
        }
        if(v3x.currentBrowser=='CHROME'){
    		//重载onUnload方法，以避免表单提交成功之前就执行了，导致内存中的流程加签减签信息被提前删除了。
    		window.onunload = function(){
				//do nothing;
			}
        }
        theForm.submit();
        try { //如果是弹出窗口，则不能显示“处理中”
	        getA8Top().startProc('');
        }catch (e) {
        }
        //disabledPrecessButton();
    }
    else{
    	disabledPrecessButton(false);
    }
}
/**
 * 正文附件的保存
 */
function saveContenAtt(){
    if(parent.detailMainFrame.contentIframe){
    	var contentAtt = parent.detailMainFrame.contentIframe.fileUploadAttachments ;
    	if(contentAtt){
    		var keys = contentAtt.instanceKeys ;
    		for(var len = 0 ; len < keys.size() ; len++){
    			fileUploadAttachments.put(keys.get(len),contentAtt.get(keys.get(len))) ;
    			 if(newflowType && newflowType == 'child' && parentIdSummerId){
    				fileUploadAttachments.get(keys.get(len)).extReference = parentIdSummerId ;
    			}
    		}
    	}
    	var contentRelAttachmentsMap = parent.detailMainFrame.contentIframe.relAttachmentsMap;
    	if(contentRelAttachmentsMap){
    		var keys = contentRelAttachmentsMap.instanceKeys;
    		for(var i = 0; i < keys.size(); i++){
    			var key = keys.get(i);
    			var tmpRelAttachments = contentRelAttachmentsMap.get(key);
    			var keys2 = tmpRelAttachments.instanceKeys;
				var relAttachments = new Properties();
    			for(var j = 0; j < keys2.size(); j++){
    				var key2 = keys2.get(j);
        			relAttachments.put(key2, tmpRelAttachments.get(key2));
    			}
    			relAttachmentsMap.put(key, relAttachments);
    		}
    	}
    }
}
function delAttIds(){
	var _delAttIds = "" ;
	if(parent.detailMainFrame.contentIframe){
		var delAtt = parent.detailMainFrame.contentIframe.theHasDeleteAtt ;  
		if(delAtt){
			var keys = delAtt.instanceKeys ;
			for(var len = 0 ; len < keys.size() ; len++){
				if(len == 0){
					_delAttIds = delAtt.get(keys.get(len)).id ;
				}else{
					_delAttIds += "," + delAtt.get(keys.get(len)).id ;
				}
			}
		}		
	}

	return  _delAttIds ;	
}


function doZcdb(obj) {
	parent.detailMainFrame.contentIframe.isSubmitOperation = true;
    var theForm = obj.form;
	if(!checkModifyingProcess(theForm.processId.value, theForm.summary_id.value)){
		return;
	}
	if(!checkForm(theForm)){
		return;
	}
    try{
  	    //如果是表单，检查表单数据
  	   
        parent.detailMainFrame.contentIframe.isNeedCheckFormSave = false;
  	    if(parent.detailMainFrame.contentIframe.isForm&&!parent.detailMainFrame.contentIframe.validFieldData()){
          	return;
      	}
      	if (document.getElementById("delete").checked) {
           alert(v3x.getMessage("collaborationLang.collaboration_alertDeleteInvalidation"));
       	}
     }
     catch(e){}
     try{
        if (document.getElementById("pipeonhole").checked) {
           	alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeInvalidation"));
        }
     }
     catch(e){}
    
    //处理表单数据
    if(parent.detailMainFrame.contentIframe.isForm){
    	    theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
    	    theForm.formDisplayValue.value = parent.detailMainFrame.contentIframe.genFormSubject_Object();
    	    theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
    	    theForm.form.value = parent.detailMainFrame.contentIframe.form;
    	    theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
    	    theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
    }
    
   	if(!parent.detailMainFrame.contentIframe.saveContent()){
   		return;
   	}
    var the_delIds = "" ;
   	if(parent.detailMainFrame.contentIframe.isForm){
   	 	saveContenAtt();
   	 	the_delIds = delAttIds();
   	 }
	if(parent.detailMainFrame.contentIframe.isForm){
		saveColFieldSummaryDataMap('false');
	}
	
	if(parent.detailMainFrame.contentIframe){
        var contentAtt = parent.detailMainFrame.contentIframe.fileUploadAttachments;
		if (contentAtt) {
			var keys = contentAtt.instanceKeys;
			for (var len = 0; len < keys.size(); len++) {
				fileUploadAttachments.put(keys.get(len), contentAtt.get(keys
								.get(len)));
				if (theForm.newflowType && theForm.newflowType == 'child'
						&& parentIdSummerId) {
					fileUploadAttachments.get(keys.get(len)).extReference = parentIdSummerId;
				}
			}
		}
    }
	
	saveISignature();
   	setAttParentform(theForm) ;
    saveAttachment();
    theForm.action = genericURL + "?method=doZCDB&theDelAttIds="+the_delIds;
    theForm.method = "POST";
    theForm.target = "_self";

    theForm.submit();

    disabledPrecessButton();
}
//保存 ISiginature HTML 专业签章
function saveISignature(){
	try{
		var bodyType =  parent.detailMainFrame.contentIframe.document.getElementById("bodyType");
		var bodyTypeStr = "";
		if(bodyType){
			bodyTypeStr = bodyType.value;
		}
		var flag = true;
		if(bodyTypeStr == 'HTML' || bodyTypeStr == 'FORM'){
			flag = parent.detailMainFrame.contentIframe.saveISignatureHtml();
		}
		return flag;
	}catch(e){
		return false;
	}
	return true;
}
function doEndDraftOpinion(opinionId){
	var theForm = document.forms['theform'];
	if(theForm && opinionId){
		alert(v3x.getMessage("collaborationLang.collaboration_successDO"));
		theForm.draftOpinionId.value = opinionId;
		
		disabledPrecessButton(false)
	}
}

function saveDraftOpinion(obj){
    saveAttachment('','false');

    var theForm = obj.form;
    
    theForm.action = genericURL + "?method=doDraftOpinion";

    theForm.target = "showDiagramFrame";
    theForm.submit();

	disabledPrecessButton();
}

function disabledPrecessButton(state){
	state = state == null ? true : state;
    try{ document.getElementById("processButton").disabled = state; } catch(e) { }
    try{ document.getElementById("auditNoPassButton").disabled = state; } catch(e) { }
    try{ document.getElementById("auditBack").disabled = state; } catch(e) { }
    try{ document.getElementById("zcdbButton").disabled = state; } catch(e) { }
    try{ document.getElementById("savedraftButton").disabled = state; } catch(e) { }
    if(state){
    	try{ disableButton("stepStopSpan"); } catch(e) { }
        try{ disableButton("stepBackSpan"); } catch(e) { }
        try{ disableButton("preInsertPeople"); } catch(e) { }
        try{ disableButton("preColAssign"); } catch(e) { }
        try{ disableButton("preDeletePeople"); } catch(e) { }
        try{ disableButton("updateContent"); } catch(e) { }
        try{ disableButton("updateAtt"); } catch(e) { }
        try{ disableButton("addInform"); } catch(e) { }
        try{ disableButton("repealItem"); } catch(e) { }
        try{ disableButton("signForward"); } catch(e) { }
        try{ disableButton("openSignature"); } catch(e) { }
        try{ disableButton("colToEvent"); } catch(e) { }
        try{ disableButton("openSuperviseWindowWhenDeal"); } catch(e) { }
    }else{
    	try{ enableButton("stepStopSpan"); } catch(e) { }
        try{ enableButton("stepBackSpan"); } catch(e) { }
        try{ enableButton("preInsertPeople"); } catch(e) { }
        try{ enableButton("preColAssign"); } catch(e) { }
        try{ enableButton("preDeletePeople"); } catch(e) { }
        try{ enableButton("updateContent"); } catch(e) { }
        try{ enableButton("updateAtt"); } catch(e) { }
        try{ enableButton("addInform"); } catch(e) { }
        try{ enableButton("repealItem"); } catch(e) { }
         try{ enableButton("signForward"); } catch(e) { }
         try{ enableButton("openSignature"); } catch(e) { }
         try{ enableButton("colToEvent"); } catch(e) { }
         try{ enableButton("openSuperviseWindowWhenDeal"); } catch(e) { }
    }
}

function showAdvance() {
    var rv = v3x.openWindow({
        url: genericControllerURL + "collaboration/advance",
        width: "330",
        height: "410"
    });

    if (rv != null) {
    	var deadlineObj=document.getElementById("deadline");//流程期限
    	var referenceTimeObj=document.getElementById("referenceTime");//基准时长
    	var advanceRemindObj=document.getElementById("advanceRemind");//提前提醒时间
    	
    	var allow_transmitObj=document.getElementById("allow_transmit");//转发
    	var allow_chanage_flowObj=document.getElementById("allow_chanage_flow");//改变流程
    	var allow_editObj=document.getElementById("allow_edit");//修改正文
    	var allow_edit_attachmentObj=document.getElementById("allow_edit_attachment");//修改附件
    	var allow_pipeonholeObj=document.getElementById("allow_pipeonhole");//归档
    	var allow_auto_stop_flowObj=document.getElementById("allow_auto_stop_flow");//流程期限到时自动终止
    	
    	var templeteNumberObj=document.getElementById("templeteNumber");//模板编号
    	
    	jsSelectItemByValue(deadlineObj, rv[0]);
    	jsSelectItemByValue(referenceTimeObj, rv[1]);
    	jsSelectItemByValue(advanceRemindObj, rv[2]);
    	
    	if(rv[3]==true){
    		allow_transmitObj.checked=true;
    	}else{
    		allow_transmitObj.checked=false;
    	}
    	if(rv[4]==true){
    		allow_chanage_flowObj.checked=true;
    	}else{
    		allow_chanage_flowObj.checked=false;
    	}
    	if(rv[5]==true){
    		allow_editObj.checked=true;
    	}else{
    		allow_editObj.checked=false;
    	}
    	if(rv[6]==true){
    		allow_edit_attachmentObj.checked=true;
    	}else{
    		allow_edit_attachmentObj.checked=false;
    	}
    	if(rv[7]==true){
    		allow_pipeonholeObj.checked=true;
    	}else{
    		allow_pipeonholeObj.checked=false;
    	}
    	if(rv[8]==true){
    		allow_auto_stop_flowObj.checked=true;
    	}else{
    		allow_auto_stop_flowObj.checked=false;
    	}
    	templeteNumberObj.value=rv[9];
    }
}
function jsSelectItemByValue(objSelect, objItemValue){
	for (var i = 0; i < objSelect.options.length; i++) {        
        if (objSelect.options[i].value == objItemValue) {        
        	objSelect.options[i].selected = true;        
            break;
        }
    }
}


function selectUrger(elements) {
    if (!elements) {
        return;
    }

    document.getElementById("urgertext").value = getNamesString(elements);
    document.getElementById("urgerinput").innerHTML = getIdsInput(elements, "urger");
}

/*
 * 撤销流程
 */
var repealItems_win;
function repealItems(fromPageType) {
    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
    	disabledPrecessButton(false);
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
    	disabledPrecessButton(false);
        return;
    }
    var affairId=null;
    var hasMoreElement = false;
    var len = id_checkbox.length;
    var countChecked = 0;
    var _processId = null;
    var isFinished = false;
    var isNewflow = false;
    var summaryId;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
        	affairId=id_checkbox[i].getAttribute('affairId');
            hasMoreElement = true;
            countChecked++;
            _processId = id_checkbox[i].getAttribute('processId');
            summaryId = id_checkbox[i].value;
            if(id_checkbox[i].isFinished == "true" || id_checkbox[i].getAttribute('isFinished') == "true"){
            	isFinished = true;
            }
            if(id_checkbox[i].isNewflow == "true" || id_checkbox[i].getAttribute('isNewflow') == "true"){
            	isNewflow = true;
            }
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertCancelItem"));
        disabledPrecessButton(false);
        return;
    }
    
    if(countChecked > 1){
    	alert(v3x.getMessage("collaborationLang.collaboration_confirmCancelOnlyOne"));
    	disabledPrecessButton(false);
        return;
    }

	if(!checkModifyingProcess(_processId, summaryId)){
		disabledPrecessButton(false);
		return;
	}
    //已结束的流程不能撤销
    if(isFinished){
    	alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsFinished"));
    	disabledPrecessButton(false);
        return;
    }
    //由主流程自动触发的新流程不可撤销
    if(isNewflow){
    	alert(v3x.getMessage("collaborationLang.warn_workflowIsNewflow_cannotRepeal"));
    	disabledPrecessButton(false);
    	return;
    }
     //AJAX校验已经核定不能撤销
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsVouch", false);
		requestCaller.addParameter(1, "String", _processId);
	var vouchRs = requestCaller.serviceRequest();
	if(vouchRs == "TRUE1"){
		alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsVouched"));
		disabledPrecessButton(false);
		return;
	}else if(vouchRs == "TRUE2"){
		alert(v3x.getMessage("collaborationLang.cannotRepeal_newflowIsVouched"));
		disabledPrecessButton(false);
		return;
	}
    //AJAX校验是否可撤销, 审核节点已处理了不能撤销
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsCanRepeal", false);
		requestCaller.addParameter(1, "Long", summaryId);
	var rs = requestCaller.serviceRequest();
	if(rs == "FALSE"){
		alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsAudited"));
		disabledPrecessButton(false);
		return;
	}
	else if(rs != "TRUE"){
		alert(v3x.getMessage("collaborationLang.warn_newflowIsEnd_cannotRepeal", rs));
		disabledPrecessButton(false);
		return;
	}
	if(v3x.getBrowserFlag('OpenDivWindow')){
		var rv = getA8Top().v3x.openWindow({
	        url: genericControllerURL + "collaboration/repealCommentDialog",
	        width: 400,
	       	height: 280,
	       	resizable: "no"
	    });
	    if(rv){
	        disableButtons();
	    	//var element = document.createElement("<INPUT TYPE=HIDDEN NAME=repealComment value='" + rv + "' />");
	        var element = document.createElement("input");
	        element.setAttribute('type','hidden');
	    	element.setAttribute('name','repealComment');
	    	element.setAttribute('value',rv);
	        theForm.appendChild(element);    		
	    	if(beforeSubmit(affairId,"repeal",rv)){
		   		return;
		   	}
	        theForm.action = genericURL + "?method=repeal";
	        theForm.target = "_self";
	        theForm.method = "POST";
	        theForm.submit();
	    }
	}else{
		repealItems_win = v3x.openDialog({
	    	id:"repealItems_win",
	    	title:v3x.getMessage("collaborationLang.del_flow"),
	    	url : genericControllerURL + "collaboration/repealCommentDialog",
	    	width: 400,
	        height: 280,
	        type:'window',
	        buttons:[{
				id:'repealItems_win_btn1',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var rv = repealItems_win.getReturnValue();
	        		if(rv!=null && rv!=''){
	        			//<INPUT TYPE=HIDDEN NAME=repealComment value='" + rv + "' />
	        	    	var element = document.createElement("input");
	        	    	element.setAttribute('type','hidden');
	        	    	element.setAttribute('name','repealComment');
	        	    	element.setAttribute('value',rv);
	        	        theForm.appendChild(element);    		
	        	        if(beforeSubmit(affairId,"repeal",rv)){
	        		   		return;
	        		   	}
	        	        var iframe = repealItems_win.iframe;
	        	        theForm.action = genericURL + "?method=repeal";
	        	        theForm.target = iframe.getAttribute("name");
	        	        theForm.method = "POST";
	        	        theForm.submit();
	        		}
	        		//repealItems_win.close();
            }
	            
	        }, {
				id:'repealItems_win_btn2',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        	repealItems_win.close();
	            }
	        }]
	    });
	}
}
function closeWindow(){
	if(repealItems_win){
		repealItems_win.close();
	}
	window.location.href = window.location.href;
}
/*
 * 审批节点撤销流程
 */
function repealItem(fromPageType, _summaryId) {
	parent.detailMainFrame.contentIframe.isSubmitOperation = true;
	var theForm = document.getElementById("theform");
    if (!theForm) {
    	disabledPrecessButton(false);
        return false;
    }
    //意见是否可为空
	var content = theForm.content;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && content && content.value == ''){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return false;
	}
	if(!checkModifyingProcessAndLock(theForm.processId.value, _summaryId)){
		disabledPrecessButton(false);
		return;
	}

    //AJAX校验是否可撤销, 审核节点已处理了不能撤销
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsCanRepeal", false);
		requestCaller.addParameter(1, "Long", _summaryId);
	var rs = requestCaller.serviceRequest();
	if(rs == "FALSE"){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsAudited"));
		disabledPrecessButton(false);
		return false;
	}
	else if(rs != "TRUE"){
		alert(v3x.getMessage("collaborationLang.warn_newflowIsEnd_cannotRepeal", rs));
		disabledPrecessButton(false);
		return;
	}
	 //AJAX校验已经核定不能撤销
    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsVouch", false);
		requestCaller.addParameter(1, "String", theForm.processId.value);
	var vouchRs = requestCaller.serviceRequest();
	if(vouchRs == "TRUE1"){
		alert(v3x.getMessage("collaborationLang.cannotRepeal_workflowIsVouched"));
		disabledPrecessButton(false);
		return;
	}else if(vouchRs == "TRUE2"){
		alert(v3x.getMessage("collaborationLang.cannotRepeal_newflowIsVouched"));
		disabledPrecessButton(false);
		return;
	}
	if (!window.confirm(v3x.getMessage("collaborationLang.collaboration_confirmCancelItem"))) {
	    disabledPrecessButton(false);
	    return;
	}
	disabledPrecessButton(true);
	
    saveAttachment();
    if(!parent.detailMainFrame.contentIframe.saveContent()){
    	disabledPrecessButton(false);
		return;
	}
	if(document.getElementById("processButton"))
    	document.getElementById("processButton").disabled = true;
    try {
    	document.getElementById("zcdbButton").disabled = true;
    } catch(e){}
    if(parent.detailMainFrame.contentIframe.isForm){
    	if(parent.detailMainFrame.contentIframe.disableSign!="true")
    		theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
    	theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
    	theForm.form.value = parent.detailMainFrame.contentIframe.form;
    	theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
    	theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
    }
    if(v3x.currentBrowser=='CHROME'){
		//重载onUnload方法，以避免表单提交成功之前就执行了，导致内存中的流程加签减签信息被提前删除了。
		window.onunload = function(){
			//do nothing;
		}
    }
    var erpContent;
    if(theForm.content){
    	erpContent=theForm.content.value;
    }
   	if(beforeSubmit(theForm.affair_id.value,"repeal",erpContent)){
   		return;
   	}
    theForm.action = genericURL + "?method=repeal&page=showDiagram&_summaryId=" + _summaryId;
    theForm.target = "_self";
    theForm.method = "POST";
    theForm.submit();
    if(v3x.currentBrowser=='CHROME' || v3x.currentBrowser=='FIREFOX'){
		parent.closeWindow();
	}
}

// 2017-01-18 诚佰公司 编辑流程前选择流程密级
function editWorkFlow() {
	if (flowSecretLevel_wf == null || flowSecretLevel_wf == "") {
		alert("编辑流程前请选择流程密级。");
		return false;
	}
	return true;
}

var isShowingDesigner = false;
function designWorkFlow(frameNames, isOnlyView) {
	// 2017-01-11 诚佰公司
	if (!editWorkFlow()) {
		return;
	}
	
	frameNames = frameNames || "";
	var onlyView = (isOnlyView == "true");
    isShowingDesigner = true;
    var from = document.getElementById("from").value;
    var rv = getA8Top().v3x.openWindow({
    	url: genericControllerURL + "collaboration/monitor&isShowButton=true&frameNames=" + frameNames + "&isOnlyView=" + onlyView+"&from="+from+"&secretLevel="+flowSecretLevel_wf,
        width: "860",
       	height: "690",
       	resizable: "no"
    });

    isShowingDesigner = false;
    if (rv != null) {

    }
    if(rv==true){processing=false;}
    activeOcx();
}

function newColl() {
    parent.location.href = genericURL + "?method=newColl";
}

/**
 * 另存�为模版
 */
function saveAsTemplete() {
	var theForm = document.getElementsByName("sendForm")[0];
    if (!checkForm(theForm)) {
        return;
    }
   
	var rv = v3x.openWindow({
        url: genericControllerURL + "collaboration/saveAsTemplate&hasWorkflow=" + hasWorkflow,
        height: 220,
        width: 350
    });
	
	activeOcx();
    
	if (!rv) {
        return;
    }
    
    //校验超期提醒，提前提醒时间
	if(!compareTime() || !checkSupervisor()){
		return;
	}
    
	var over = rv[0];
    var overId = rv[1];
    var type = rv[2];
    var subject = rv[3];
	
	if(over == 2){
		return;
	}
	
	//节点权限引用更新
	if(!isCheckNodePolicyFlag){
		var policyArr = new Array();
	    var itemNameArr = new Array();
	    if(nodes != null && nodes.length>0){
	    	for(var i=0;i<nodes.length;i++){
	    		var policyName = policys[nodes[i]].name;
	    		var itemName = policys[nodes[i]].value;
	    		policyArr.push(policyName);
	    		itemNameArr.push(itemName);
	    	}
	    }
		try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkNodePolicy", false);
			requestCaller.addParameter(1, "String[]", policyArr);
			requestCaller.addParameter(2, "String[]", itemNameArr);
			requestCaller.addParameter(3, "String", theForm.loginAccountId.value);
			var rs = requestCaller.serviceRequest();
			if(rs == "1"){
				alert(_("collaborationLang.node_policy_not_existence"));
				enableButtons();
				return;
			}
		}
		catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}
	}
    
    if(type == "templete"){			
    	var atts = fileUploadAttachments.values();
		for(var i = 0; i < atts.size(); i++) {
			var att = atts.get(i) ;
			if(att.type == '1' || att.type == '3' || att.type == '4'){
				if(att.subReference != ""){
					att.extSubReference = att.subReference ;
				}else{
					att.extSubReference = att.extSubReference ;
				}				
			}
		} 		
	    cloneAllAttachments();	  	
		saveAttachment();
    }
    
	if(type != "workflow" && isForm!="true"){
		if(!saveOffice())
			return ;
	}
	if(isForm=="true"&&!validFieldDataforTem())
        	return;
	 if(isForm=="true")
       theForm.formData.value = genJSObjectforTem();
       
    try { theForm.allow_transmit.disabled = false; }catch(e){}
    try { theForm.allow_chanage_flow.disabled = false; }catch(e){}
    try { theForm.allow_edit.disabled = false; }catch(e){}
    try { theForm.allow_pipeonhole.disabled = false; } catch(e){}
    try { theForm.allow_edit_attachment.disabled = false; } catch(e){}
    
    fileId = getUUID();
    
    theForm.saveAsTempleteSubject.value = subject;
    theForm.target = "personalTempleteIframe";
    theForm.action = templeteURL + "?method=saveTemplete&type=" + type + "&overId=" + overId;
    theForm.method = "POST";
    theForm.submit();
    
    try { theForm.allow_transmit.disabled = true; }catch(e){}
    try { theForm.allow_chanage_flow.disabled = true; }catch(e){}
    try { theForm.allow_edit.disabled = true; }catch(e){}
    try { theForm.allow_pipeonhole.disabled = true; } catch(e){}
    try { theForm.allow_edit_attachment.disabled = true; } catch(e){}
    
    var from = document.getElementById("from").value;
    getA8Top().startProc('');
}

/**
 * 
 */
var templateIpadwin=null ;
function openTemplete() {
	
	if(v3x.getBrowserFlag('pageBreak')==true){
	    var rv = v3x.openWindow({
	        url : genericControllerURL + "collaboration/templete/index&categoryType=0,4",
	        height : "600",
	        width : "800"
	    });
	    
	    if (rv) {
	        var workflow = rv[0];
	        var workflowInfo = rv[1] || "";
	        var workflowRule = rv[4] || "";
	        var bodyContent = rv[5];
	
	        if (bodyContent) {
	        	var bodyType = bodyContent["bodyType"];
	        	
	        	var subject = bodyContent["subject"];
	        	var _subject = document.getElementById("subject").value;
	        	var defaultSubject = document.getElementById("subject").deaultValue;
	        	if(_subject == null || _subject == "" || _subject == defaultSubject){
	        		document.getElementById("subject").value = subject;
	        	}
	        	
				if(bodyType == "HTML"){
		            var flag = showEditor("HTML", false);
		
		            document.getElementById("content").value = bodyContent["content"];
		            oFCKeditor.changeContent();
				}
				else if(bodyType == "OfficeWord" || bodyType == "OfficeExcel" || bodyType == "WpsWord" || bodyType == "WpsExcel"){
					var flag = showEditor(bodyType, false);
					
					fileId = bodyContent['newOfficeFileId'];
					createDate = bodyContent['createDate'];
					originalFileId = bodyContent['fileId'];
					needReadFile = "true";
				}
	        }
	        if (workflow) {
	            hasDiagram = true;
	            caseProcessXML = workflow;
	            initProcessXml();
	            hasWorkflow = true;
	            isFromTemplate = true;
	            showMode = 0;
	            var isSystemTemplete = document.getElementById("isSystemTemplete").value == "true";
	            document.getElementsByName("sendForm")[0].process_desc_by.value = 'xml';
	            document.getElementsByName("sendForm")[0].workflowInfo.value = workflowInfo;
	            document.getElementsByName("sendForm")[0].workflowInfo.disabled = isSystemTemplete;
	            document.getElementsByName("sendForm")[0].workflowRule.value = workflowRule;
	            
	            var subject = rv[2] || "";
	        	var _subject = document.getElementById("subject").value;
	        	var defaultSubject = document.getElementById("subject").deaultValue;
	        	if(_subject == null || _subject == "" || _subject == defaultSubject){
	        		document.getElementById("subject").value = subject;
	        	}
	        	var templeteId = rv[3] || "";
	        	document.getElementsByName("sendForm")[0].templeteId.value = templeteId;
	        }
	    }
	}else{
		templateIpadwin = v3x.openDialog({
	    	id:"templateIpad",
	    	title:v3x.getMessage("collaborationLang.find_template"),
	    	url : genericControllerURL + "collaboration/templete/index&categoryType=0,4",
	    	width: 800,
	        height: 500,
	        //isDrag:false,
	        //targetWindow:getA8Top(),
	        //fromWindow:window,
	        type:'window',
	        //relativeElement:obj,
	        buttons:[{
				id:'btn121',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var rv = templateIpadwin.getReturnValue();
	        		if(rv){
	        	        var workflow = rv[0];
	        	        var workflowInfo = rv[1] || "";
	        	        var workflowRule = rv[4] || "";
	        	        var bodyContent = rv[5];
	        	
	        	        if (bodyContent) {
	        	        	var bodyType = bodyContent["bodyType"];
	        	        	
	        	        	var subject = bodyContent["subject"];
	        	        	var _subject = document.getElementById("subject").value;
	        	        	var defaultSubject = document.getElementById("subject").deaultValue;
	        	        	if(_subject == null || _subject == "" || _subject == defaultSubject){
	        	        		document.getElementById("subject").value = subject;
	        	        	}
	        	        	
	        				if(bodyType == "HTML"){
	        		            var flag = showEditor("HTML", false);
	        		
	        		            document.getElementById("content").value = bodyContent["content"];
	        		            oFCKeditor.changeContent();
	        				}
	        				else if(bodyType == "OfficeWord" || bodyType == "OfficeExcel" || bodyType == "WpsWord" || bodyType == "WpsExcel"){
	        					var flag = showEditor(bodyType, false);
	        					
	        					fileId = bodyContent['newOfficeFileId'];
	        					createDate = bodyContent['createDate'];
	        					originalFileId = bodyContent['fileId'];
	        					needReadFile = "true";
	        				}
	        	        }
	        	        if (workflow) {
	        	            hasDiagram = true;
	        	            caseProcessXML = workflow;
	        	            initProcessXml();
	        	            hasWorkflow = true;
	        	            isFromTemplate = true;
	        	            showMode = 0;
	        	            var isSystemTemplete = document.getElementById("isSystemTemplete").value == "true";
	        	            document.getElementsByName("sendForm")[0].process_desc_by.value = 'xml';
	        	            document.getElementsByName("sendForm")[0].workflowInfo.value = workflowInfo;
	        	            document.getElementsByName("sendForm")[0].workflowInfo.disabled = isSystemTemplete;
	        	            document.getElementsByName("sendForm")[0].workflowRule.value = workflowRule;
	        	            
	        	            var subject = rv[2] || "";
	        	        	var _subject = document.getElementById("subject").value;
	        	        	var defaultSubject = document.getElementById("subject").deaultValue;
	        	        	if(_subject == null || _subject == "" || _subject == defaultSubject){
	        	        		document.getElementById("subject").value = subject;
	        	        	}
	        	        	var templeteId = rv[3] || "";
	        	        	document.getElementsByName("sendForm")[0].templeteId.value = templeteId;
	        	        }
	        		}
	        		templateIpadwin.close();
            }
	            
	        }, {
				id:'btn211',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        	templateIpadwin.close();
	            }
	        }]
	    });
		
	}
    activeOcx();
}

function advanceViews(flag) {
    var processAdvanceDIVObj = document.getElementById("processAdvanceDIV");
    var isDisplay = flag;
    if(flag == null){
    	isDisplay = processAdvanceDIVObj.style.display == "none";
    }
	if(isDisplay){
		processAdvanceDIVObj.style.display = "";
		if(document.getElementById("processAdvance"))
			document.getElementById("processAdvance").innerHTML = "<font style='color:#5A5A5A;'>&gt;&gt;</font>";
	}
	else{
		processAdvanceDIVObj.style.display = "none";
		if(document.getElementById("processAdvance"))
			document.getElementById("processAdvance").innerHTML = "<font style='color:#5A5A5A;'>&gt;&gt;</font>";
	}
	
}

/*
 * 记录是否进行了正文修改
 */
var contentUpdate=false;
function openNewEditWin()
{
	contentUpdate=true;
	popupContentWin();
}

/**
 * 修改正文
 */
function modifyBody(summaryId, hasSign) {	
	var bodyType = document.getElementById("bodyType").value;
  	if(bodyType=="HTML")
  	{
  		contentUpdate=true;
  		
  		//修改正文的时候判断是否有印章，有印章的时候不让修改
		var mLength=document.getElementsByName("iHtmlSignature").length; 
  		if(mLength>0){
  			alert(_("collaborationLang.collaboration_alertCantModifyBecauseOfIsignature"));
  			return false;
  		}
  		popupContentWin();
  	}
  	else
  	{
  		popupContentWin();
  		if(contentUpdate==true){return;}
   	 	contentUpdate=ModifyContent(hasSign);
  	}  	
}

function modifyBodySave() {
    if (!saveOffice()) {
        return;
    }

    disableButton("save");
    disableButton("cancel");

    theForm.submit();
}

function doEndModifyBodySave() {
    window.returnValue = "true";
    window.close();
}

//处理时保存正文
function saveContent(){ 
  	if(contentUpdate==false){return true;}  
  	var bodyType = document.getElementById("bodyType").value;  
  	if(bodyType=="HTML"){
      	try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "updateHtmlBody", false);
			var summaryId = document.getElementById("summaryId").value;
			var currentNodeId = parent.parent.detailRightFrame.currentNodeId;
			var htmlContent = getHtmlContent();
			requestCaller.addParameter(1, "Long", summaryId);
			requestCaller.addParameter(2, "String", htmlContent);
			requestCaller.addParameter(3, "String", bodyType);
			requestCaller.addParameter(4, "Long", currentNodeId);
			var ds = requestCaller.serviceRequest();		
			return (ds=="true");
		}
		catch (ex1) {
			alert("Exception : " + (ex1.number & 0xFFFF)+ex1.description);
			return false;
		}            
	}else if(bodyType == 'FORM'){
		//表单正文暂时不需要保存 todo
	}else{  
	    if(saveOffice()==false){
	    	alert(_("edocLang.edoc_contentSaveFalse"));
	      	return false;
	    }
	    try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "updateHtmlBody", false);
			var summaryId = document.getElementById("summaryId").value;
			var currentNodeId = parent.parent.detailRightFrame.currentNodeId;
			requestCaller.addParameter(1, "Long", summaryId);
			requestCaller.addParameter(2, "String", "");
			requestCaller.addParameter(3, "String", bodyType);
			requestCaller.addParameter(4, "Long", currentNodeId);
			var ds = requestCaller.serviceRequest();		
			return (ds=="true");
		}
		catch (ex1) {
			alert("Exception : " + (ex1.number & 0xFFFF)+ex1.description);
			return false;
		}     
	}
 	return true;
}

function popupContentWin()
{  
	var bodyType = document.getElementById("bodyType").value;
	if(bodyType=="HTML"){  	
		var tempUrl=fullEditorURL;
		if(contentUpdate==false){
			tempUrl+="&canEdit=false";
		}
		else{
			tempUrl+="&canEdit=true";
		}
		var rv;
		var returnValues = v3x.openWindow({url: tempUrl,workSpace: 'yes'});
		if(returnValues){
			rv=returnValues[0];
			arrAtts =returnValues[1];
			if(arrAtts){
				var atts  = arrAtts.instance;
				for(var i = 0; i < atts.length; i++) {
					fileUploadAttachments.put(atts[i].fileUrl,copyAttachment(atts[i]));
				}
			}
		}
		if(document.getElementById("content")!= null && (typeof(oFCKeditor) != "undefined")){      
			oFCKeditor.SetContent(rv);
			oFCKeditor.remove();
		}
		else{
			if(rv != undefined){
		  		document.getElementById("col-contentText").innerHTML=rv;
			}else{
				contentUpdate = false;
			}
		}
	}
	else{ 
		fullSize();
	}
}

function getHtmlContent()
{
  var str="";
  var fckObj  = document.getElementById("fckObj");
  if(fckObj && fckObj.style.display !='none')
  {
	  var oEditorFCK=FCKeditorAPI.GetInstance(oFCKeditor.InstanceName).EditorWindow.parent.FCK;  
	  str=oEditorFCK.EditingArea.Document.body.innerHTML;
  }
  else
  {//浏览状态,正文放到Div里面了
    str=document.getElementById("col-contentText").innerHTML;
  }
  return toRelativePath(str);
}
//图片的绝对路径变成先对路径
function toRelativePath(str){
	var imgArr=str.split("<IMG");
	var httpUrl="";
	var imgStr=imgArr[1];
	if(typeof(imgStr)!="undefined"&&imgStr){
		var tempStr=imgStr.substring(imgStr.indexOf("src=\""));
		httpUrl=tempStr.substring(5,tempStr.indexOf("/seeyon/fileUploa"));//取IP
	}
	var reg=new RegExp(httpUrl,"g");
	var result=imgArr[0];
	for(var i=1;i<imgArr.length;i++){
		var imgStr=imgArr[i];
		var temp=imgStr.substring(0,imgStr.indexOf(">"));
		temp=temp.replace(reg,"")
		result+=("<IMG"+temp+imgStr.substring(imgStr.indexOf(">")));
	}
	return result;
}

/**
 * 新建 显示发起人附言区域
 */
function showNoteArea() {
    document.getElementById("noteAreaTd").width = "180px";

    document.getElementById('noteAreaTable').style.display = "";
    var _noteMinDiv = document.getElementById('noteMinDiv');
    _noteMinDiv.style.display = "none";
    _noteMinDiv.style.height = "0px";
}

function hiddenNoteArea() {
    document.getElementById("noteAreaTd").width = "45px";

    document.getElementById('noteAreaTable').style.display = "none";
    var _noteMinDiv = document.getElementById('noteMinDiv');
    _noteMinDiv.style.display = "";
    _noteMinDiv.style.height = "100%";
}

//未结束流程 处理人增加附件或关联文档
function saveOpinionAttach(opinionId){
	resetAttachment("attach1Area"+opinionId,"attachment1Tr"+opinionId,false,false);
	//上传附件
	insertAttachment();
	if(fileUploadAttachment && fileUploadAttachment.isEmpty()){
		clearUploadAttachments();
		return ;
	}
	//提交
	var f = document.getElementById('opinionForm');
	f.signOpinionId.value = opinionId;
	var len =  fileUploadAttachment.values().size();
	saveAttachment(document.getElementById('attachmentInputss'));
	var numDiv = document.getElementById("replyAttachmentNumberDiv"+opinionId);
	if(numDiv){
		if(numDiv.innerText){
			numDiv.innerText = parseInt(numDiv.innerText) + len;
		}else{
			numDiv.innerText =  len;
		}
	}
	f.submit();
	clearUploadAttachments();
}
function saveOpinionQuote(opinionId){
	resetAttachment("attach2Area"+opinionId,"attachment2Tr"+opinionId,false,false);
	quoteDocument();
	if(fileUploadAttachment && fileUploadAttachment.isEmpty()){
		clearUploadAttachments();
		return ;
	}
	var f = document.getElementById('opinionForm');
	f.signOpinionId.value = opinionId;
	var len =  fileUploadAttachment.values().size();
	saveAttachment(document.getElementById('attachmentInputss'));
	var numDiv = document.getElementById("replyDocachmentNumberDiv"+opinionId);
	if(numDiv){
		if(numDiv.innerText){
			numDiv.innerText = parseInt(numDiv.innerText) + len;
		}else{
			numDiv.innerText =  len;
		}
	}
	f.submit();
	clearUploadAttachments();
}
function addNote(_isNoteAddOrReply, opinionId) {
	var d1 = new Date();
	if (_isNoteAddOrReply == 'addnote') {
		reply(opinionId, null, true, true,true);
	}else{
    	reply(opinionId, null, true, true);
	}
	var d2 = new Date();
	//alert(d2-d1)
    //发起人增加附言
    if (_isNoteAddOrReply == 'addnote') {
        var theForm = document.getElementsByName("repform")[0];
        theForm.isNoteAddOrReply.value = _isNoteAddOrReply;
        
        //发起人附言推送消息的时候不推送给具体的人。
        var isSendMessage = document.getElementsByName("isSendMessage")[0];
        isSendMessage.setAttribute("onclick","");
        var sendMessagePeopleSpan = document.getElementById("sendMessagePeopleSpan");
        sendMessagePeopleSpan.style.display="none";
    }
}

function quoteDocument(isBind) {
	if(v3x.getBrowserFlag('OpenDivWindow')==true){
		if(isBind !="isBind"){
			isBind="";//流程表单绑定界面不显示协同
		}
	    var atts = v3x.openWindow({
	        url: genericURL + "?method=showList4QuoteFrame&isBind="+isBind,
	        height: 600,
	        width: 800
	    });
	   
		if(atts){
		    deleteAllAttachment(2);
		    for (var i = 0; i < atts.length; i++) {
		        var att = atts[i]
		        //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
		        addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
		    }
		}
	}else{
		var win = v3x.openDialog({
	    	id:"documentIpad",
	    	title:v3x.getMessage("collaborationLang.collaboration_alertQuoteItem"),
	    	url : genericURL + "?method=showList4QuoteFrame",
	    	width: 650,
	        height: 500,
	        //isDrag:false,
	        //targetWindow:getA8Top(),
	        //fromWindow:window,
	        type:'window',
	        //relativeElement:obj,
	        buttons:[{
				id:'btn12',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var atts = win.getReturnValue();
	        		if(atts){
	        		    deleteAllAttachment(2);
	        		    for (var i = 0; i < atts.length; i++) {
	        		        var att = atts[i]
	        		        //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
	        		        addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
	        		    }
	        		}
	        		win.close();
            }
	            
	        }, {
				id:'btn21',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        	win.close();
	            }
	        }]
	    });
	}
	if(typeof(currentPage) !="undefined" && currentPage== "newColl"){
		addScrollForDocument();
	}
    activeOcx();
}
function addScrollForDocument(){
	var attachment = document.getElementById("attachmentArea");
	var attachment2 = document.getElementById("attachment2Area");
	if(attachment2){
		var h = attachment2.scrollHeight;
		if(h >= (attachmentConstants.height * 2)){
			attachment2.style.overflow="auto";
			attachment2.style.height="46px";
		}
	}
	if(attachment){
		var h = attachment.scrollHeight;
		if(h >= (attachmentConstants.height * 2)){
			attachment.style.overflow="auto";
			attachment.style.height="46px";
		}
	}
}
function quoteDocumentTarget() {
	if(v3x.getBrowserFlag('OpenDivWindow')==true){
	    var atts = v3x.openWindow({
	        url: genericURL + "?method=showList4QuoteFrame",
	        height: 600,
	        width: 800
	    });
	   
		if(atts){
		    deleteAllAttachment(2);
		    for (var i = 0; i < atts.length; i++) {
		        var att = atts[i]
		        //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
		        addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
		    }
		}
	}else{
		var win = v3x.openDialog({
	    	id:"documentIpad",
	    	title:v3x.getMessage("collaborationLang.collaboration_alertQuoteItem"),
	    	url : genericURL + "?method=showList4QuoteFrame",
	    	width: 650,
	        height: 500,
	        //isDrag:false,
	        targetWindow:window.parent.detailMainFrame,
	        //fromWindow:window,
	        type:'window',
	        //relativeElement:obj,
	        buttons:[{
				id:'btn12',
	            text: v3x.getMessage("collaborationLang.submit"),
	            handler: function(){    	        	
	        		var atts = win.getReturnValue();
	        		if(atts){
	        		    deleteAllAttachment(2);
	        		    for (var i = 0; i < atts.length; i++) {
	        		        var att = atts[i]
	        		        //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
	        		        addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
	        		    }
	        		}
	        		win.close();
            }
	            
	        }, {
				id:'btn21',
	            text: v3x.getMessage("collaborationLang.cancel"),
	            handler: function(){
	        	win.close();
	            }
	        }]
	    });
	}
}

function quoteDocumentForm() {
	var formappid = document.all("formappid").value;
	var quoteformtemId = document.all("quoteformtemId").value;
    var atts = v3x.openWindow({
        url: genericURL + "?method=showList4QuoteFrameForm&formappid=" + encodeURIComponent(formappid)+"&quoteformtemId=" + encodeURIComponent(quoteformtemId),
        height: 600,
        width: 800
    });
    
	if(atts){
	    deleteAllAttachment(2);
	    for (var i = 0; i < atts.length; i++) {
	        var att = atts[i]
	        //addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
	        addAttachment(att.type, att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", att.reference, att.category)
	    }
	}
}

function quoteDocumentUnSelected(url){
	return deleteAttachment(url, fileUploadAttachments.containsKey(url));
}

function quoteDocumentSelected(obj, subject, documentType, url){
	if(!obj){
		return;
	}
	if(!obj.checked){
		var result = quoteDocumentUnSelected(url);
		if(result == 1){
			obj.checked = true;
		}
		return;
	}
	if(_from && _from == 'formTable' && fileUploadAttachments.containsKey(url)){
		alert("文档"+subject+"已经存在，不允许重复添加!") ;
		obj.checked = false;
		return;
	}
	if(_from && _from == 'formTable'){
		var _parent = window.opener;
		if(_parent == null){
			_parent = window.dialogArguments;
		}
		var _parentFarther = _parent.opener ;	
		if(_parentFarther == null){
			_parentFarther = _parent.dialogArguments;
		}
	    var dv = _parentFarther.extendField;
	    var subReference = dv.value;
		var _theShowUploadAtts = window.dialogArguments.dialogArguments.theToShowAttachments;
		if(_theShowUploadAtts){
			for(var len = 0 ; len < _theShowUploadAtts.size() ; len++) {
				var attobj = _theShowUploadAtts.get(len);
				if(attobj.fileUrl == url && attobj.attobj == subReference){
					alert("文档"+subject+"已经存在，不允许重复添加!") ;
					obj.checked = false;
					return ;
				}				
			}
		}
	}
	if(!subject || !documentType || !url || fileUploadAttachments.containsKey(url)){
		return;
	}
	
    var type = "2";
    var filename = subject;
    var mimeType = documentType;
    var createDate = "2000-01-01 00:00:00";
	if(obj.getAttribute("createDate")){
		createDate = obj.getAttribute("createDate");
	}
    var fileUrl = url;
    var description = url;

    addAttachment(type, filename, mimeType, createDate, '0', fileUrl, true, null, description, documentType, documentType + ".gif");
}

function quoteDocumentOK() {
    var atts = fileUploadAttachments.values().toArray();

    if (!atts || atts.length < 1) {
        alert(getA8Top().v3x.getMessage('collaborationLang.collaboration_alertQuoteItem'));
        return;
    }

    parent.window.returnValue = atts;
    parent.window.close();
}

function quoteDocumentOKForm(){
	var _parent = window.opener;
	if(_parent == null){
		_parent = window.dialogArguments;
	}
	var _parentFarther = _parent.opener ;	
	if(_parentFarther == null){
		_parentFarther = _parent.dialogArguments;
	}
    var dv = _parentFarther.extendField;
    var extendFieldWidth = _parentFarther.extendWidth;//表单上传附件字段的宽度
    var atts = fileUploadAttachments.values().toArray();//得到所有的MAP中的数据
    
    if (!atts || atts.length < 1) {
        alert(getA8Top().v3x.getMessage('collaborationLang.collaboration_alertQuoteItem'));
        return;
    }
   	
    var att ;
    
	var subReference = dv.value;
	if(!subReference){
		subReference = getUUID();
		dv.value = subReference;
	}
	if(dv){	
		for(var i= 0 ; i< atts.length ;i++){
		    att = atts[i] ;
		      if(dv.label && dv.label != null){
			     dv.label = dv.label + att.filename ;
			  }else{
			    dv.label = att.filename ;
			  }
			  if(att.extSubReference != ""){
			  	continue ;
			  }
			  _parentFarther.addAttachment('4', att.filename, att.mimeType, att.createDate, att.size, att.fileUrl, true, false, att.description, null, att.mimeType + ".gif", "", "", false, extendFieldWidth,null,subReference);
          }
	}
  
   parent.window.close();
}

//归档
function colpigeonhole(appName, pageType, currentUserId){
	if (!pageType || !page_types[pageType]) {
        alert('pageType is illegal:' + pageType);
        return false;
    }

    var theForm = document.getElementsByName("listForm")[0];
    if (!theForm) {
        return false;
    }

    var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return true;
    }
    /*
    var count = validateCheckbox("id");
    if(count > 20){
    	alert(v3x.getMessage("V3XLang.items_select_too_many"));
        unselectAll("id");
        return false;
    }*/
    var hasMoreElement = false;
    var affairIds = []; //需要提交到后段验证的
    var noAccessAffairId = new ArrayList(); //没有权限的
    var archiveAffairId = new ArrayList(); //可以归档的
    var attsFlags = new ArrayList();
    var errorAffairs = ""; //被撤销，删除等操作的事项不允许归档
    
    var len = id_checkbox.length;
    var isMustOpinionAffairs = "";
    
    for (var i = 0; i < len; i++) {
    	var o = id_checkbox[i];
        if (o.checked) {
            hasMoreElement = true;
            
            if(pageType == "pending"){
            	if(o.isTemplete == "true"){
            		alert(v3x.getMessage("collaborationLang.pendingTemplete_cannotArchiveOrDelete"));
            		return;
            	}
            	else if(!checkModifyingProcess(o.processId, o.value)){ //其他人在编辑
					return;
				}
				else if(o.isMustOpinion == "true"){
            		isMustOpinionAffairs += o.colSubject + "\n";
            		o.checked = false;
            	}
			}
            
            var aId = o.getAttribute("affairId");
            var attsFlag = o.getAttribute("attsFlag");
            var archiveId = o.getAttribute("archiveId");
            var templeteId = o.getAttribute("templeteId");
            if(pageType != "sent" && o.getAttribute("canArchive") == "false"){
            	alert(v3x.getMessage("collaborationLang.collaboration_alertUnallowPigeonhole", o.getAttribute("colSubject")));
            	unselectAll();
            	return false;
            	//noAccessAffairId.add(aId);
            }else if(pageType == "sent" && templeteId == '' && archiveId != ''){
            	/**
            	 * according to sunj
            	 * 1、自由协同，发起者设置了预归档，发送后仍在已发中，等流程结束后，从已发中删除。与其他处理人无关。在流程没有结束前，若发起者从已发列表中，选择归档，提示该流程已做了预归档。
				 * 2、模板协同，模板中设置了预归档。不管流程是否已经结束，都不会自动从已发、已办中去掉。允许发起者和处理人手工归档，并从各自的列表中去掉
            	 */
            	alert(v3x.getMessage("collaborationLang.collaboration_alertHasPrePigeonhole", o.getAttribute("colSubject")));
            	unselectAll();
            	return false;
            }else{
	            affairIds[affairIds.length] = aId;
	            archiveAffairId.add(aId);
	            attsFlags.add(attsFlag);
            }
        }
    }

    if (!hasMoreElement) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItem"));
        return true;
    }
    //必须填写意见的事项
    if(isMustOpinionAffairs != ""){
		alert(v3x.getMessage("collaborationLang.pending_cannotArchiveOrDelete_requiredOpinion", isMustOpinionAffairs));
		return;
    }
    
    if(affairIds && affairIds.length > 0){
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkCanAchive", false);
		requestCaller.addParameter(1, "String", affairIds.join(","));
		var ajaxResult = requestCaller.serviceRequest();
		if(ajaxResult != "true"){
			if(ajaxResult == "AffairNotExist" || ajaxResult.indexOf("Exception:") > -1){
				getA8Top().reFlesh();
				return;
			}
			else if(ajaxResult.indexOf("NoPopedom:") > -1){
				alert(v3x.getMessage("collaborationLang.collaboration_alertUnallowPigeonhole", ajaxResult.substr("NoPopedom:".length)));				
	    		getA8Top().reFlesh();
	    		return;
			}
			else{
				alert(ajaxResult);
	    		getA8Top().reFlesh();
	    		return;
			}
		}
    }
    
	var archiveIds = pigeonhole(appName, archiveAffairId.toString(","), attsFlags.toString(","));
	if(archiveIds == "cancel"){
		return false;
	}
	if(archiveIds == "" || archiveIds == "failure"){
		alert(v3x.getMessage("collaborationLang.collaboration_alertPigeonholeItemFailure"));
	    return false;
	}
	else{
	    theForm.action = pigeonholeActionURL;
		var selArchiveId;
	    var _archiveIds = archiveIds.split(",");
	    for (var i=0; i<_archiveIds.length; i++){
	    	var archiveId = _archiveIds[i];
//	    	var element = document.createElement("<INPUT TYPE=HIDDEN NAME=archiveId value='" + archiveId + "' />");
	    	var element = document.createElement("input");
	    	element.type = "hidden";
	    	element.name = "archiveId";
	    	element.value = archiveId;
		    theForm.appendChild(element);
		    selArchiveId = _archiveIds[0];
	    }
	    for (var i = 0; i < id_checkbox.length; i++) {
		    var checkbox = id_checkbox[i];
		    if (!checkbox.checked)
		    	continue;
		    var affairArchiveId = checkbox.getAttribute("archiveId");
		    if(affairArchiveId != "" && pageType != 'pending'  && pageType != 'finish'){
		    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxDocHierarchyManager", "getParentFrIdByResourceId", false);
				requestCaller.addParameter(1, "Long", selArchiveId);
				var result = requestCaller.serviceRequest();
				if(result == affairArchiveId){
			    	alert(_("collaborationLang.colPigeonhole_duple"));
			    	return false;
				}
		    }
		    var affairId = checkbox.getAttribute("affairId");
//		    var element = document.createElement("<INPUT TYPE=HIDDEN NAME=affairId value='" + affairId + "' />");
		    var element = document.createElement("input");
		    element.type = "hidden";
		    element.name = "affairId";
		    element.value = affairId;
		    theForm.appendChild(element);
	    }
		disableButtons();
//	    var element = document.createElement("<INPUT TYPE=HIDDEN NAME=pageType value='" + pageType + "' />");
    	var element = document.createElement("input");
    	element.type = "hidden";
    	element.name = "pageType";
    	element.value = pageType;
	    theForm.appendChild(element);
	    theForm.method = "post";
	    theForm.target = "_self";
	    theForm.submit();
	    return true;
	 }
}
//模板打印
function templetePrint(){
	//标题
	try{
		var printSubject = _("collaborationLang.print_subject");
		var printsub = parent.detailMainFrame.document.getElementById("printsubject").innerHTML;
		printsub = "<center><span style='font-size:14px;line-height:24px;'>"+printsub+"</span></center>";
		var printSubFrag = new PrintFragment(printSubject, printsub);
	}catch(e){}
	//最后修改时间
	try{
		var lastModify1 = parent.detailMainFrame.document.getElementById("lastModify1").innerHTML;
		var lastModify2 = parent.detailMainFrame.document.getElementById("lastModify2").innerHTML;
		var parintLastModify = new PrintFragment("", "<span style='font-size:14px;line-height:24px;'>"+lastModify1+lastModify2+"</span")
	}catch(e){}
	//附件
	try{
		var printAttachment =  _("collaborationLang.print_attachment");  
		var attNumber =parent.detailMainFrame.document.getElementById("attachmentNumberDiv").innerHTML;
         var colAttachment = "";
                if(attNumber!=0){
                    colAttachment ="<div class='div-float body-detail-su'>"+ _("collaborationLang.print_attachment")+" : ("+attNumber+")</div><br>"+getSenderAttachmentName(summary_id,0);  
                    colAttachment="<br>"+colAttachment+"<br>";
                    colAttachment = cleanSpecial(colAttachment);
                } 
		var colAttachment1Frag = new PrintFragment(printAttachment, colAttachment);
	}catch(e){}
	//内容
	try{
		var printColBody= _("collaborationLang.print_content");;
		var colcontext     =	parent.detailMainFrame.contentIframe.document.getElementById("col-contentText");
		var colBody;
		if(colcontext != null){
			colBody= colcontext.innerHTML;
		}else{
			colBody="";
		}
		var colBodyFrag = new PrintFragment(printColBody, colBody);
	}catch(e){}
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/collaboration/css/collaboration.css")
	
	var pl = new ArrayList();
	pl.add(printSubFrag);
	pl.add(parintLastModify);
	pl.add(colAttachment1Frag);
	pl.add(colBodyFrag);
	printList(pl,cssList);
}
//打印
function colPrint(){
	try {
		var printSubject = _("collaborationLang.print_subject");
		var printsub = parent.document.getElementById("printsubject").innerHTML;
		printsub = "<center><span style='font-size:24px;line-height:24px;'>"+printsub+"</span></center>";
		//标题文字样式与查时不一样. 
		var printSubFrag = new PrintFragment(printSubject, printsub);
	} catch (e) {
	}
	try {
		var printSenderInfo = _("collaborationLang.print_senderInfo");
		var printSender = parent.document.getElementById("printSenderInfo").innerHTML;
		printSender = "<center><span style='font-size:12px;line-height:16px;'>" + printSender + "</span></center>";
		var printSenderFrag = new PrintFragment(printSenderInfo, printSender);
	} catch (e) {
	}
	try {
		var printColBody= "";
		var colcontext     =	document.getElementById("col-contentText");
		var colBody = "";
		if(colcontext != null){
			colBody+= "<div class='contentText' style='margin:0 10px;width:100%'>"+colcontext.innerHTML+"</div>";
		}
		
		var colBodyFrag = new PrintFragment(printColBody, colBody);
	} catch (e) {
	}
	
	try {
		var printColOpinion = _("collaborationLang.print_senderNote");
		var colOpinion = document.getElementById("senderOpinion").innerHTML;
		colOpinion = cleanSpecial(colOpinion);
		var sendOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	}
	
	try {
		var printColOpinion = _("collaborationLang.print_opinion");
		//隐藏回复框
		var oReplyTable = document.getElementById('reply-table');
		if(oReplyTable!=null){
			oReplyTable.style.display="none";
		}
		//处理人意见出现过多个的情况，只取最后一个:普通A8BUG_V3.50SP1_力帆实业（集团）股份有限公司_协同转发后在打印预览时，正文内容会重复显示在发起者附言的位置
		//var colOpinion = "<br>"+document.getElementById("colOpinion").innerHTML;
		var colOpinion = "";
		var colOptionArr = document.getElementsByName( "colOpinion" );
		if( colOptionArr && colOptionArr.length!=0 ){
			colOpinion = "<br>"+colOptionArr(colOptionArr.length-1).innerHTML;
		}
		colOpinion = cleanSpecial(colOpinion);
		var colOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	}
	
	var isForward = false;
	try {
		var forwardOriginalOpinion = document.getElementById("forwardOriginalOpinion");
		// 对原意见隐藏后不打印
		if (forwardOriginalOpinion.style.display!="none") {
			var printForwardOriginalOpinion = _("collaborationLang.print_forwardOpinion");
			var forwardContext = document.getElementById("forwardOriginalOpinion");
			
			var forwardOriginalOpinion;
			if(forwardContext != null){
				isForward = true;
				forwardOriginalOpinion= "<br>" + forwardContext.innerHTML;
			}else{
				forwardOriginalOpinion="";
			}
			forwardOriginalOpinion = cleanSpecial(forwardOriginalOpinion);
			var forwardOriginalOpinionFrag = new PrintFragment(printForwardOriginalOpinion, forwardOriginalOpinion);
		}
	} catch (e) {
	}
	
    try {
		var printColMydocument =  _("collaborationLang.print_mydocument");
                var att2Number = parent.document.getElementById("attachment2NumberDiv").innerHTML;
                var colMydocument = "";
                if(att2Number!=0){
                    colMydocument = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_mydocument")+" : ("+att2Number+")</div><br>"+getSenderAttachmentName(summary_id,2);  
                    colMydocument=colMydocument+"<br><br>";
                    colMydocument = cleanSpecial(colMydocument);
                }	             		
		var colAttachment2Frag = new PrintFragment(printColMydocument, colMydocument);
	} catch (e) {
	}
        try {
		var printAttachment =  _("collaborationLang.print_attachment");                
		var attNumber =parent.document.getElementById("attachmentNumberDiv").innerHTML;
                var colAttachment = "";
                if(attNumber!=0){
//                   colAttachment ="<table><tr><td valign='top'><div class='div-float' style='color: #335186; font-weight: bolder; font-size: 12px;'>"+ _("collaborationLang.print_attachment")+" : ("+attNumber+")</div></td><td valign='top'>"+getSenderAttachmentName(summary_id,0) + "</td></tr></table>"; 
//                   colAttachment="<br>"+colAttachment+"<br>";
                	colAttachment ="<div class='div-float body-detail-su'>"+ _("collaborationLang.print_attachment")+" : ("+attNumber+")</div><br>"+getSenderAttachmentName(summary_id,0); 
                    colAttachment=colAttachment+"<br>";
                    colAttachment = cleanSpecial(colAttachment);
                }              
		var colAttachment1Frag = new PrintFragment(printAttachment, colAttachment);
	} catch (e) {
	}
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/collaboration/css/collaboration.css")
	cssList.add(v3x.baseURL + "/common/RTE/editor/css/fck_editorarea5Show.css")
	cssList.add(v3x.baseURL + "/apps_res/form/css/SeeyonForm.css")
	var pl = new ArrayList();
	pl.add(printSubFrag);
	pl.add(printSenderFrag);
	pl.add(colBodyFrag);
	pl.add(colAttachment2Frag);
	pl.add(colAttachment1Frag);
	if(isForward){pl.add(forwardOriginalOpinionFrag);}
	pl.add(sendOpinionFrag);
	pl.add(colOpinionFrag);
	
	printList(pl,cssList);
}

function mainprint(a){
	var _parent = parent.detailMainFrame.contentIframe;
	if(_parent && _parent.officeEditorFrame){
		//解决待办列表中打印Office, IE客户端假死问题, 避免循环引用。
		setTimeout("parent.detailMainFrame.contentIframe.officePrint()", 200);
	}else{
		try {
			var printColBody= "";
			var colcontext     =	parent.detailMainFrame.contentIframe.document.getElementById("area");
			//alert(colcontext);
			var colBody;
			if(colcontext != null){
			colBody= colcontext.innerHTML;
			}else{
				colBody="";
			}			
			var colBodyFrag = new PrintFragment(printColBody, colBody);
		} catch (e) {
		}	
		var cssList = new ArrayList();
		cssList.add(a);	
			
		var pl = new ArrayList();
		pl.add(colBodyFrag);
		printList(pl,cssList);			
	}
}


function formmainprint(){
	try {
		var printSubject = _("collaborationLang.print_subject");
		var printsub = parent.document.getElementById("printsubject").innerHTML;
		printsub = "<center><span style='font-size:24px;line-height:24px;'>"+printsub+"</span></center>";
		//标题文字样式与查时不一样. 
		var printSubFrag = new PrintFragment(printSubject, printsub);
	} catch (e) {
	}
	try {
		var printSenderInfo = _("collaborationLang.print_senderInfo");
		var printSender = parent.document.getElementById("printSenderInfo").innerHTML;
		printSender = "<center><span style='font-size:12px;line-height:16px;'>" + printSender + "</span></center>";
		var printSenderFrag = new PrintFragment(printSenderInfo, printSender);
	} catch (e) {
	}
	try {
			var printColBody= _("collaborationLang.print_content");
			var colcontext     =	document.getElementById("area");
			//alert(colcontext);
			var colBody;
			if(colcontext != null){
			colBody= colcontext.innerHTML;
			}else{
				colBody="";
			}			
			var colBodyFrag = new PrintFragment(printColBody, colBody);
		} catch (e) {
		}	
	
	try {
		var printColOpinion = _("collaborationLang.print_senderNote");
		var colOpinion = "<br>"+document.getElementById("senderOpinion").innerHTML;
		colOpinion = cleanSpecial(colOpinion);
		var sendOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	}
	
	try {
		var printColOpinion = _("collaborationLang.print_opinion");
		var colOpinion = document.getElementById("colOpinion").innerHTML;
		colOpinion = cleanSpecial(colOpinion);
		var colOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	}
	
	var isForward = false;
	try {
		var printForwardOriginalOpinion = _("collaborationLang.print_forwardOpinion");
		var forwardContext = document.getElementById("forwardOriginalOpinion");
		var forwardOriginalOpinion;
		if(forwardContext != null){
			isForward = true;
			forwardOriginalOpinion= "<br>" + forwardContext.innerHTML;
		}else{
			forwardOriginalOpinion="";
		}
		forwardOriginalOpinion = cleanSpecial(forwardOriginalOpinion);
		var forwardOriginalOpinionFrag = new PrintFragment(printForwardOriginalOpinion, forwardOriginalOpinion);
	} catch (e) {
	}
	
	try {
		var printColMydocument =  _("collaborationLang.print_mydocument");
                var att2Number = parent.document.getElementById("attachment2NumberDiv").innerHTML;
                var colMydocument = "";
                if(att2Number!=0){
                    colMydocument = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_mydocument")+" : ("+att2Number+")</div><br>"+getSenderAttachmentName(summary_id,2);  
                    colMydocument=colMydocument+"<br>";
                    colMydocument = cleanSpecial(colMydocument);
                }	             		
		var colAttachment2Frag = new PrintFragment(printColMydocument, colMydocument);
	} catch (e) {
	}
        try {
		var printAttachment =  _("collaborationLang.print_attachment");                
		var attNumber =parent.document.getElementById("attachmentNumberDiv").innerHTML;
                var colAttachment = "";
                if(attNumber!=0){
                    colAttachment = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_attachment")+" : ("+attNumber+")</div><br>"+getSenderAttachmentName(summary_id,0);  
                    colAttachment="<br>"+colAttachment+"<br>";
                    colAttachment = cleanSpecial(colAttachment);
                }              
		var colAttachment1Frag = new PrintFragment(printAttachment, colAttachment);
	} catch (e) {
	}
	
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/form/css/SeeyonForm.css");
	
	var pl = new ArrayList();
	pl.add(printSubFrag);
	pl.add(printSenderFrag);
	pl.add(colBodyFrag);
	pl.add(colAttachment2Frag);
    pl.add(colAttachment1Frag);
	pl.add(sendOpinionFrag);
	pl.add(colOpinionFrag);
	if(isForward){pl.add(forwardOriginalOpinionFrag);}
	printList(pl,cssList,[2],[0,1,3,4,5,6]);		
	
}
//新建事项中打印映射
function newDoPrint(){ 
           var type = document.getElementById("bodyType").value;              
           var isHTML = false;
		var isForm = false;
                if(type=="HTML"){ isHTML = true}
                if(type=="FORM"){ isForm = true}                
		if(isHTML){
			newColPrint();
		}
		else{
		if(isForm){
		  newFormMainPrint();
		}else{
		  var selectPrintType = v3x.openWindow({
				url: genericURL + "?method=showPrintSelector&isForm="+isForm,
				width:"260",
				height:"160",
				scrollbars:"no"
			});
			if(!selectPrintType) return;
			if(selectPrintType == "mainpp"){
				mainpp();
			}
			else{
				newColPrint();
			}
		}
			
		}
}
	
function mainpp(){
	//TODO 错误,待清理
	var a = "<c:url value='/apps_res/form/css/SeeyonForm.css'/>";  
	newMainPrint(a);	
}
	
//新建事项中打印
function newColPrint(){                                          
    try {
		var printSubject = _("collaborationLang.print_subject");
		var printsub = document.getElementById("subject").value;
		printsub = "<center><hr style='height:1px' class='Noprint'></hr><span style='font-size:24px;line-height:24px;'>"+printsub.escapeHTML()+"</span></center>";
		//标题文字样式与查时不一样. 
		var printSubFrag = new PrintFragment(printSubject, printsub);
	} catch (e) {
	}
	try {
		var printSenderInfo = _("collaborationLang.print_senderInfo");
		var printSender = currUserName+" "+document.getElementById("bodyCreateDate").value;
		printSender = "<center><span style='font-size:12px;line-height:16px;'>" + printSender + "</span></center>";
		var printSenderFrag = new PrintFragment(printSenderInfo, printSender);
	} catch (e) {
	}
	try {   
        var oEditor = FCKeditorAPI.GetInstance("content");
		var printColBody= "";
		var colcontext     =	oEditor.GetXHTML(true);
		var colBody;
		if(colcontext != null){
			colBody= colcontext;
		}else{
			colBody="";
		}			
		var colBodyFrag = new PrintFragment(printColBody, colBody);
	} catch (e) {
	}	
	try {
		var printColOpinion = _("collaborationLang.print_senderNote");
		var colOpinion ="<br/><div class='div-float body-detail-su'>"+ _("collaborationLang.print_newSenderNote")+" :</div><br/><br/><br/>"+escapeStringToHTML(document.getElementById("note").value);
		colOpinion = cleanSpecial(colOpinion);
		var sendOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	}	
    try {
		var printColMydocument =  _("collaborationLang.print_mydocument");
        var att2Number = getFileAttachmentNumber(2);
        var colMydocument = "";
            if(att2Number!=0){
                    colMydocument = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_mydocument")+" : ("+att2Number+")</div><br>"+getFileAttachmentName(2);  
                    colMydocument=colMydocument+"<br>";
                    colMydocument = cleanSpecial(colMydocument);
            }	            		
		var colAttachment2Frag = new PrintFragment(printColMydocument, colMydocument);
	} catch (e) {
	}
    try {
		var printAttachment =  _("collaborationLang.print_attachment");                
		var attNumber = getFileAttachmentNumber(0);
        var colAttachment = "";
            if(attNumber!=0){
                    colAttachment = "<table><tr><td valign='top'><div class='div-float' style='color: #335186; font-weight: bolder; font-size: 12px;'>"+_("collaborationLang.print_attachment")+" : ("+attNumber+")</div></td><td valign='top'>"+getFileAttachmentName(0) + "</td></tr></table>"; 
                    colAttachment="<br>"+colAttachment+"<br>";
                    colAttachment = cleanSpecial(colAttachment);
            }              
		var colAttachment1Frag = new PrintFragment(printAttachment, colAttachment);
	} catch (e) {
	}
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/collaboration/css/collaboration.css")
	
	var pl = new ArrayList();
	pl.add(printSubFrag);
	pl.add(printSenderFrag);
	pl.add(colBodyFrag);        
    pl.add(colAttachment2Frag);
    pl.add(colAttachment1Frag);
    pl.add(sendOpinionFrag);		
	printList(pl,cssList);
}
function newMainPrint(a){        
	var officefra = document.all("officeEditorFrame");
	if(officefra != null){
		document.officeEditorFrame.officePrint();
	}else{
		try {
			var printColBody= "";
			var colcontext     =	document.getElementById("area");			
			var colBody;
			if(colcontext != null){
			colBody= colcontext.innerHTML;
			}else{
				colBody="";
			}			
			var colBodyFrag = new PrintFragment(printColBody, colBody);
		} catch (e) {
		}
                
		var cssList = new ArrayList();
		cssList.add(a);	
			
		var pl = new ArrayList();
		pl.add(colBodyFrag);
		printList(pl,cssList);			
	}
}

//新建事项中表单打印
function newFormMainPrint(){               
	try {
		var printSubject = _("collaborationLang.print_subject");
		var printsub = document.getElementById("subject").value;
		printsub = "<center><span style='font-size:24px;line-height:24px;'>"+printsub.escapeHTML()+"</span></center>";
		//标题文字样式与查时不一样. 
		var printSubFrag = new PrintFragment(printSubject, printsub);
	} catch (e) {
	}
	try {
		var printSenderInfo = _("collaborationLang.print_senderInfo");
		var printSender = document.getElementById("userName").value+" "+document.getElementById("bodyCreateDate").value;
		printSender = "<center><span style='font-size:12px;line-height:16px;'>" + printSender + "</span></center>";
		var printSenderFrag = new PrintFragment(printSenderInfo, printSender);
	} catch (e) {
	}
	try {
			var printColBody= "";
			var colcontext     =	document.getElementById("scrollDiv");			                     
			var colBody;
			if(colcontext != null){
			colBody= colcontext.innerHTML;
			}else{
				colBody="";
			}			
			var colBodyFrag = new PrintFragment(printColBody, colBody);
		} catch (e) {
		}	
	
	try {
		var printColOpinion = _("collaborationLang.print_senderNote");
		var colOpinion ="<br><div class='div-float body-detail-su'>"+ _("collaborationLang.print_newSenderNote")+" :</div><br><br><br>"+escapeStringToHTML(document.getElementById("note").value);
		colOpinion = cleanSpecial(colOpinion);
		var sendOpinionFrag = new PrintFragment(printColOpinion, colOpinion);
	} catch (e) {
	} 	
	try {
		var printColMydocument =  _("collaborationLang.print_mydocument");
                var att2Number = getFileAttachmentNumber(2);
                var colMydocument = "";
                if(att2Number!=0){
                    colMydocument = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_mydocument")+" : ("+att2Number+")</div><br>"+getFileAttachmentName(2);  
                    colMydocument=colMydocument+"<br>";
                    colMydocument = cleanSpecial(colMydocument);
                }	            		
		var colAttachment2Frag = new PrintFragment(printColMydocument, colMydocument);
	} catch (e) {
	}
    try {
		var printAttachment =  _("collaborationLang.print_attachment");                
		var attNumber = getFileAttachmentNumber(0);
                var colAttachment = "";
                if(attNumber!=0){
                    colAttachment = "<div class='div-float body-detail-su'>"+_("collaborationLang.print_attachment")+" : ("+attNumber+")</div><br>"+getFileAttachmentName(0); 
                    colAttachment="<br>"+colAttachment+"<br>";
                    colAttachment = cleanSpecial(colAttachment);
                }              
		var colAttachment1Frag = new PrintFragment(printAttachment, colAttachment);
	} catch (e) {
	}
	var cssList = new ArrayList();
	cssList.add(v3x.baseURL + "/apps_res/form/css/SeeyonForm.css");
	
	var pl = new ArrayList();
	pl.add(printSubFrag);
	pl.add(printSenderFrag);
	pl.add(colBodyFrag);               
    pl.add(colAttachment2Frag);
    pl.add(colAttachment1Frag);
    pl.add(sendOpinionFrag);         		
	printList(pl,cssList);		
	
}


//允许处理后归档和跟踪被同时选中，但是他们都不能和删除按钮同时选中。
var checkMulitSign_hasShowAlert = false;
function checkMulitSign(nowSelected){
	var afterSignObj = document.getElementsByName("afterSign");
	var isDeleteChecked=false;
	if(checkMulitSign_hasShowAlert == false){
		var flag = 0;
		for(var i = 0; i < afterSignObj.length; i++) {
			if(afterSignObj[i].checked){
				flag++;
				if(afterSignObj[i].id=="delete")isDeleteChecked=true;
			}
		}
		
		if(flag > 1 && isDeleteChecked ){
            //跟踪、处理后归档不能和处理后删除同时选择
			alert(_("collaborationLang.collaboration_alertSignAfterOption"));
			checkMulitSign_hasShowAlert = true;
		}
	}
	//判断删除是否被选择了。
	if(!isDeleteChecked){
	    for(var i = 0; i < afterSignObj.length; i++) {
			if(afterSignObj[i].checked){
				if(afterSignObj[i].id=="delete")isDeleteChecked=true;
			}
		}
	}
	
	if(isDeleteChecked){
    	for(var i = 0; i < afterSignObj.length; i++) {
    		if(afterSignObj[i].id == nowSelected.id){
    			continue;
    		}
    		afterSignObj[i].checked = false;
    		if(afterSignObj[i].id=='isTrack'){
    			var all = document.getElementById("trackRange_all");
    			var part = document.getElementById("trackRange_part");
    			if(all!=null) all.checked = false;
    			if(part!=null) part.checked = false;
    		}
    	}
    }
}

function initProcessXml(){
	document.getElementsByName("sendForm")[0].process_xml.value = caseProcessXML;
}

function afterForward(){
	alert(v3x.getMessage("collaborationLang.collaboration_successForward"));
	if(v3x.getBrowserFlag('pageBreak')){
		parent.close();
		window.returnValue = "true";
	}else{
		getA8Top().$('#forwardWin').dialog('destroy');
	}
}

//超期提醒与提前提醒时间设置的比较
function compareTime(){
	var newCollForm = document.getElementsByName("sendForm")[0];
	//var advanceRemind = newCollForm.advanceRemind.options[newCollForm.advanceRemind.selectedIndex];
	//var deadline = newCollForm.deadline.options[newCollForm.deadline.selectedIndex];
	var advanceRemindTime = document.getElementById("advanceRemind").value;
	var deadLineTime = document.getElementById("deadline").value;
	if(deadLineTime==0){
		var allow_auto_stop_flow=document.getElementById('allow_auto_stop_flow');
		if(allow_auto_stop_flow){
			allow_auto_stop_flow.disabled=true;
		}
	}
	var advanceRemindNumber = new Number(advanceRemindTime);
	var deadLineNumber = new Number(deadLineTime);
	if(deadLineNumber <= advanceRemindNumber){
		alert(v3x.getMessage("collaborationLang.remindTimeLessThanDeadLine"));
		newCollForm.advanceRemind.selectedIndex = 0;
		//newCollForm.deadline.selectedIndex = 0;
		return false;
	}
	else{
		return true;
	}
}

//审核节点处理，state 0 审核通过 1审核不通过
function doAudit(theForm,state){
	
    theForm.state.value = state;
    theForm.action = genericURL + "?method=audit";
  //意见是否可为空
	var content = theForm.content;
	var opinionPolicy = theForm.opinionPolicy;
	if(opinionPolicy && opinionPolicy.value==1 && content && content.value == ''){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
    if(state == 0){
        doSign(theForm,theForm.action);
    }else if(state == 1){
        if (checkForm(theForm))
	    {
	        if (!window.confirm(_("collaborationLang.collaboration_confirmStepBackItem")))
	        {
	            return;
	        }
	        saveAttachment();
	        document.getElementById("processButton").disabled = true;
	        try {
	        	document.getElementById("auditBack").disabled = true;
	            document.getElementById("zcdbButton").disabled = true;
	        } catch(e) {
	        }
	        //need merge
	    	if(parent.detailMainFrame.contentIframe.isForm){
				if(!parent.detailMainFrame.contentIframe.validFieldData()){
	        		disabledPrecessButton(false);
	        		isSubmitFinished = true;
	            	return;
        	}
//	    	    theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
//	    	    theForm.formDisplayValue.value = parent.detailMainFrame.contentIframe.genFormSubject_Object();
	    	    theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
	    	    theForm.form.value = parent.detailMainFrame.contentIframe.form;
	    	    theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
	    	    theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
	    	}
	    	//end merge
	        theForm.submit();
	    }
    }
}
//审核节点处理，state 0 审核通过 1审核不通过
function doVouch(theForm,state){
	theForm.state.value = state;
    theForm.action = genericURL + "?method=vouch";
    if(state == 0){
        doSign(theForm,theForm.action);
    }else if(state == 1){
        if (checkForm(theForm))
	    {
	        //AJAX校验已经核定是否已发生
		    var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkIsVouch", false);
				requestCaller.addParameter(1, "String", theForm.processId.value);
			var vouchRs = requestCaller.serviceRequest();
			if(vouchRs == "TRUE1"){
				alert(v3x.getMessage("collaborationLang.cannotNotVouch_workflowIsVouched"));
				disabledPrecessButton(false);
				return;
			}else if(vouchRs == "TRUE2"){
				alert(v3x.getMessage("collaborationLang.cannotNotVouch_newflowIsVouched"));
				disabledPrecessButton(false);
				return;
			}
	        if (!window.confirm(_("collaborationLang.collaboration_confirmStepBackItem")))
	        {
	            return;
	        }
	        saveAttachment();
	        document.getElementById("vouchPass").disabled = true;
	        try {
	        	document.getElementById("vouchBack").disabled = true;
	            document.getElementById("zcdbButton").disabled = true;
	            document.getElementById("refreshButton").disabled = true;
	        } catch(e) {
	        }
	        //need merge
	    	if(parent.detailMainFrame.contentIframe.isForm){
				if(!parent.detailMainFrame.contentIframe.validFieldData()){
	        		disabledPrecessButton(false);
	        		isSubmitFinished = true;
	            	return;
	        	}
//	    	    theForm.formData.value = parent.detailMainFrame.contentIframe.genJSObject();
//	    	    theForm.formDisplayValue.value = parent.detailMainFrame.contentIframe.genFormSubject_Object();
	    	    theForm.formApp.value = parent.detailMainFrame.contentIframe.formApp;
	    	    theForm.form.value = parent.detailMainFrame.contentIframe.form;
	    	    theForm.operation.value = parent.detailMainFrame.contentIframe.operation;
	    	    theForm.masterId.value = parent.detailMainFrame.contentIframe.masterId;
	    	}
	    	//end merge
	        theForm.submit();
	    }
    }
}

//更多跟踪　－　取消跟踪提交前的校验　
function checkBeforeCancelTrackSubmit()
  { 
    var id_checkbox = document.getElementsByName("affairIds");
    if (!id_checkbox) {
        return false;
    }
    var selectedCount = 0;
    var affairId = null;
    var len = id_checkbox.length;
    var theCancelForm = document.getElementById("cancelTrackForm");
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            affairId = id_checkbox[i].getAttribute("extAttribute");
           // var checkbox = document.createElement("<input type='hidden' name='affairId'  value="+id_checkbox[i].value+">");
            var checkbox = document.createElement("input");
            checkbox.setAttribute("type","hidden");
            checkbox.setAttribute("name","affairId");
            checkbox.setAttribute("value",id_checkbox[i].value);
            theCancelForm.appendChild(checkbox);
            selectedCount++;
        }
    }
    if (selectedCount == 0) {
        alert(v3x.getMessage("MainLang.moreTrack_alertAboutSelectNothing"));
        return false;
    }else{
        return true;
    }
}

function preChangeTrack(affairId, isTrack,from){
	var trackValue = '0';
	if(isTrack){
		trackValue = '1';
	}
    var rv = v3x.openWindow({
        url : genericURL+"?method=preChangeTrack&affairId="+affairId+"&trackValue="+trackValue+"&from="+from,
    	width: "250",
        height: "150"
    });
}

/*
 * 节点策略说明
 */
function policyExplain() {
    var rv = v3x.openWindow({
        url: genericURL + "?method=policyExplain",
        width: "295",
        height: "275",
        dialogType: "modal",
        resizable: true
    });

    if (rv != null && (rv == "true" || rv)) {

    }
}   
/*
 * 节点策略说明
 */
function dealExplain(desc) {
    var rv = v3x.openWindow({
        url: genericURL + "?method=showDealExplain&desc="+encodeURIComponent(desc),
        width: "295",
        height: "220",
        dialogType: "modal",
        resizable: true
    });

    if (rv != null && (rv == "true" || rv)) {

    }
} 
/**
 * @deprecated 该方法被replyCommentOK替代
 */
function replyCommentOKOld(date){
	var theForm = document.getElementsByName("repform")[0];
	
	if(currentOpinionId == "senderOpinion"){
		var str = "";
		str += '<div class="div-float-clear" style="width: 100%;">';
		str += '	<div class="sendOptionWriterName1">' + date + '</div>';
		str += '	<div class="optionContent1 wordbreak">' + escapeStringToHTML(theForm.content.value) + '</div>';
		
		if(!fileUploadAttachments.isEmpty()){
			var atts = fileUploadAttachments.values();
			str += '	<div class="div-float attsContent">';
			str += '		<div class="atts-label">' + attachmentLabel + ' :&nbsp;&nbsp;</div>';
			
			for(var i = 0; i < atts.size(); i++) {
				str += atts.get(i).toString(true, false);
			}
			
			str += '	</div>';
		}
		str += '</div>';
		
		document.getElementById("replyDivsenderOpinionDIV").innerHTML += str;
		document.getElementById("replyDivsenderOpinion").innerHTML = "";
	}
	else{
		var str = "";
		str += '<div style="padding: 0px"><hr color="#CCCCCC" size="1" noshade="noshade"></div>';
		str += '<div class="comment-div">' + writeMemberName + ' ' + date + '</div>';
		str += '	<div class="comment-content wordbreak">';
		
		//意见隐藏了
		if(theForm.isHidden.checked){
			str += '<span style="color: red" class="font-12px">[' + opinionHidden + ']</span> ';
		}
		
		str += escapeStringToHTML(theForm.content.value);
		str += '</div>';

		document.getElementById("replyCommentDiv" + currentOpinionId).innerHTML += str;
		document.getElementById("replyDiv" + currentOpinionId).innerHTML = "";
	}
	
	currentOpinionId = null;
	fileUploadAttachments.clear();
}

function replyCommentOK(date){
	var theForm = document.getElementsByName("repform")[0];
	
	if(currentOpinionId == "senderOpinion"){
		var str = "";
		str += '<div class="div-float-clear postscriptDiv" style="width: 100%;">';
		str += '	<div class="optionContent1 wordbreak">' + date + '&nbsp;&nbsp;'+ escapeStringToHTML(theForm.content.value) + '</div>';
		
		if(!fileUploadAttachments.isEmpty()){
			var atts = fileUploadAttachments.values();
			var attachmentList = new ArrayList();
			var myDocumentList = new ArrayList();
			for(var i=0; i<atts.size(); i++){
				if(atts.get(i).type == 0){
					attachmentList.add(atts.get(i));
				}else{
					myDocumentList.add(atts.get(i));
				}
			}
			var attSize = attachmentList.size();
			if(attSize>0) {
				str += '	<div class="div-float attsContent" style="width:98%">';
				str += '		<div class="atts-label">' + attachmentLabel + ' :(<span class="font-12px">'+attSize+'</span>)&nbsp;&nbsp;</div>';
				for(var i = 0; i <attSize; i++) {
					str += attachmentList.get(i).toString(true, false);
				}
				str += '	</div>';
			}
			var docSize = myDocumentList.size();
			if(docSize > 0) {
				str += '	<div class="div-float attsContent" style="width:98%">';
				str += '		<div class="atts-label">' + mydocumentLabel + ' :(<span class="font-12px">'+docSize+'</span>)&nbsp;&nbsp;</div>';
				for(var i = 0; i < docSize; i++) {
					str += myDocumentList.get(i).toString(true, false);
				}
				str += '	</div>';
			}
		}
		str += '</div>';
		str += '<br/>';
		document.getElementById("replyDivsenderOpinionDIV").innerHTML += str;
		document.getElementById("replyDivsenderOpinion").innerHTML = "";
	}
	else{
		var str = "";
		str += '<div class="reply_message_con"><div class="comment4-div-mercury"><span class="reply_member"  onclick="showV3XMemberCard(\''+currentUserId+'\')">' + writeMemberName + ":</span> <span  class='reply_data'>" + date + '</span></div>';
		str += '	<div class="comment-content-cols wordbreak clearFloat">';
		//意见隐藏了
		if(theForm.isHidden.checked){
			str += '<span class="commentContent-hidden">[' + opinionHidden + ']</span> ';
		}
		str += escapeStringToHTML(theForm.content.value);
		if(proxyString != ""){
			str += "<div class='opinion-agent'>" + proxyString + "</div>";
		}
		
		str += "</div><div class='wordbreak'>";
		
		if(!fileUploadAttachments.isEmpty()){
			var atts = fileUploadAttachments.values();
			var attachmentList = new ArrayList();
			var myDocumentList = new ArrayList();
			for(var i=0; i<atts.size(); i++){
				if(atts.get(i).type == 0){
					attachmentList.add(atts.get(i));
				}else{
					myDocumentList.add(atts.get(i));
				}
			}
			
			var attSize = attachmentList.size();
			if(attSize>0) {
				str += '	<div class="div-float attsContent" style="width:98%">';
				str += '		<div class="atts-label">' + attachmentLabel + ' :(<span class="font-12px">'+attSize+'</span>)&nbsp;&nbsp;</div>';
				for(var i = 0; i < attachmentList.size(); i++) {
					str += attachmentList.get(i).toString(true, false);
				}
				str += '	</div>';
			}
			
			var docSize = myDocumentList.size();
			if(docSize > 0) {
				str += '	<div class="div-float attsContent" style="width:98%">';
				str += '		<div class="atts-label">' + mydocumentLabel + ' :(<span class="font-12px">'+docSize+'</span>)&nbsp;&nbsp;</div>';
				for(var i = 0; i < myDocumentList.size(); i++) {
					str += myDocumentList.get(i).toString(true, false);
				}
				str += '	</div>';
			}
		}
		str += '</div></div>';
		document.getElementById("replyCommentDiv" + currentOpinionId).innerHTML += str;
		document.getElementById("replyDiv" + currentOpinionId).innerHTML = "";
		document.getElementById("opinDiv"  + currentOpinionId).className = "comment-div-mercury";
	}
	
	currentOpinionId = null;
	fileUploadAttachments.clear();
}

function setCondition(isForm,args,linkId,appName){
    var link = genericControllerURL + "collaboration/templete/compute&isForm=" + isForm;
    if(args!=undefined){
    	link += "&isNew=0&id="+args.id+"&linkId="+linkId+ "&isForce=" + args.isForce;
    }else{
    	link += "&isNew=1&linkId="+linkId;
    }
    link+="&appName="+appName;
    var condition = v3x.openWindow({
			url : link,
			width : 440,
			height : 520,
			scrollbars:"no",
			dialogType: "modal",
            resizable: true
	});
	return condition;
}

function selectCondition(addBracket){
	if(!selectedEntities || selectedEntities.length==0){
		alert(v3x.getMessage("collaborationLang.branch_selectorg"));
		return;
	}
	var arr = new Array();
	var operationSel = document.getElementById("operation");
	var operation = operationSel.options[operationSel.selectedIndex];
	arr[0] = operation.value;
	arr[1] = operation.value;
	var orgId = "";
	var orgName = "";
	var accountShortname = "";
	var accountId = "";
	var orgType = "";
	var addSeparator = false;
	var isRole = addBracket!="false" && document.getElementById("orgType").value!='Role';
	for(var i=0;i<selectedEntities.length;i++){
		addSeparator = i != selectedEntities.length-1;
		orgId +=  selectedEntities[i].id + (addSeparator?"↗":"");
		orgName += (isRole?"[":"")+selectedEntities[i].name + (isRole?"]":"") + (addSeparator?"↗":"");
		accountShortname += selectedEntities[i].accountShortname + (addSeparator?"↗":"");
		accountId += selectedEntities[i].accountId + (addSeparator?"↗":"");
		orgType += selectedEntities[i].type + (addSeparator?"↗":"");
	}
	arr[2] = orgId;
	arr[3] = orgName;
	arr[4] = accountShortname;
	arr[5] = accountId;
	arr[6] = orgType;
	var isSMLA = false; // 是否按发起人登录单位判断
	var startMemberLoginAccountObjs = document.getElementsByName("startMemberLoginAccount");
	if(startMemberLoginAccountObjs && startMemberLoginAccountObjs.length > 0){
		for(var i = 0; i < startMemberLoginAccountObjs.length; i++){
			var obj = startMemberLoginAccountObjs[i];
			if(obj.checked == true && obj.value == "yes"){
				isSMLA = true;
			}
		}
	}
	var postTypes = document.getElementsByName("postType");
	var postType = "";
	if(!isSMLA && postTypes && postTypes.length>0){
		for(var i=0;i<postTypes.length;i++){
			if(postTypes[i].checked){
				postType += postTypes[i].getAttribute('displayValue') + ",";
			}
		}
		if(postType == ""){
			alert(v3x.getMessage("collaborationLang.branch_selectpost"));
			return;
		}
	}
	//新增的部门条件
	var departmentTypes=document.getElementsByName("departType");
	var departmentType="";
	if(!isSMLA&&departmentTypes&&departmentTypes.length>0){
		for(var i=0;i<departmentTypes.length;i++){
			if(departmentTypes[i].checked){
				departmentType+=departmentTypes[i].getAttribute('displayValue')+",";
			}
		}
		if(departmentType==""){
			alert("请选择一个部门条件");
			return;
		}
	}
	//新增的单位条件
	var acuntTypes=document.getElementsByName("acuntType");
	var acuntType="";
	if(acuntTypes&&acuntTypes.length>0){
		for(var i=0;i<acuntTypes.length;i++){
			if(acuntTypes[i].checked){
				acuntType+=acuntTypes[i].getAttribute('displayValue')+",";
			}
		}
		if(acuntType==""){
			alert("请选择一个单位条件");
			return;
		}
	}
	//新增的职务级别条件
	var levlTypes=document.getElementsByName("levlType");
	var levlType="";
	if(!isSMLA&&levlTypes&&levlTypes.length>0){
		for(var i=0;i<levlTypes.length;i++){
			if(levlTypes[i].checked){
				levlType+=levlTypes[i].getAttribute('displayValue')+",";
			}
		}
		if(levlType==""){
			alert("请选择一个职务级别条件");
			return;
		}
	}
	if(postType != "")
		arr[7] = postType;
	else
		arr[7] = null;
	var includeChildren = document.getElementById("includeChildren");
	if(includeChildren){
		arr[8] = includeChildren.checked;
	}else{
		arr[8] = false;
	}
	if(departmentType!=""){
        arr[9]=departmentType;
    }else{
        arr[9]=null;
    }
	if(levlType!=""){
        arr[10]=levlType;
    }else{
        arr[10]=null;
    }
	if(acuntType!=""){
        arr[11]=acuntType;
    }else{
        arr[11]=null;
    }
	arr[12] = isSMLA;
	window.returnValue = arr;
	window.close();
}

function ColBranch(){
  this.id=null;
  this.conditionType=null;
  this.formCondition=null;
  this.conditionTitle=null;
  this.conditionDesc=null;
  this.isForce=null;
  this.conditionBase=null;
}

function initCondition(){
	var scripts = document.getElementsByName("scripts");
	if(scripts&&scripts.length>0){
		var prefix = "";
		var parentURL = null;
		var isFormFlag = false;
		try{
			isFormFlag = isForm=="true" || isForm==true;
		}catch(e){}
	   	if(isFormFlag){
	   		if(rootNodeName)
		   		prefix = rootNodeName;
		   	else {
		   		parentURL = "parent.detailMainFrame.contentIframe.";
		   		prefix = parent.detailMainFrame.contentIframe.rootNodeName;
		   	}
		   	prefix = prefix.substring(0,prefix.indexOf(":")+1);
	   	}
	   	
	   	var reg; 
	   	var functionRef = new RegExp("sum[\(\{][^)]*[\}\)]|aver[\(\{][^)]*[\}\)]","g");
	   	var list = new ArrayList();
	   	
	   	var len = scripts.length;
	   	var groupIds = new Array();
	   	var types = new Array();
	   	var num = 0;
	   	var beforeScripts = new Array();
	   	var afterStripts = new Array();
	    for(var i=0;i<len;i++){
	      	var script = scripts[i].value;
	      	if(isDebug)
	      		beforeScripts[i] = script;
	      	//先将重复项的‘{’‘}’去掉
	      	var arr = script.match(functionRef);
	      	if(arr){
	      		for(var j=0;j<arr.length;j++)
	      			script = script.replace(arr[j],(parentURL==null?"":parentURL)+arr[j].replace(/\{/g,"\""+prefix).replace(/\}/g,"\""));
	      	}
	      	var temp;
	      	reg = new RegExp("exist\([^\|\| | \&\& | \']*\)","g")
	      	arr = script.match(reg);
	      	if(arr){
	      		for(var j=0;j<arr.length;j++){
		      		temp = arr[j].split(",");
		      		if(temp && temp.length==3){
			      		temp[0] = temp[0].replace("{","\""+prefix).replace("}","\"");
			      		temp[2] = temp[2].replace(")","\")");
			      		script = script.replace(arr[j],(parentURL==null?"":parentURL)+temp[0]+",\""+temp[1]+"\",\""+temp[2]);	
		      		}
	      		}
	      	}
	      	reg = new RegExp("[\{][^\{\}]*[\}]","g");
	       	arr = script.match(reg);
	       	if(arr){
	       		var formValue;
		       	for(var j=0;j<arr.length;j++){
		       		temp = arr[j].substring(1,arr[j].length-1);
		       		if(temp.indexOf("formStandardpost:")!=-1){
		       			temp = temp.substring(temp.indexOf("formStandardpost:")+17);
		       			formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");
		       			if(formValue){
		       				groupIds[num] = formValue
		       				types[num] = "Post";
		       				script = script.replace(arr[j],groupIds[num]);
		       				num++;
		       			}else
		       				script = script.replace(arr[j],"\"\"");
		       		}else if(temp.indexOf("formGrouplevel:")!=-1){
		       			temp = temp.substring(temp.indexOf("formGrouplevel:")+15);
		       			formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");
		       			if(formValue){
		       				groupIds[num] = formValue
		       				types[num] = "Level";
		       				script = script.replace(arr[j],groupIds[num]);
		       				num++;
		       			}else
		       				script = script.replace(arr[j],"\"\"");
		       		}else
		       			script = script.replace(arr[j],(parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");	
		       	}						        	
	       	}
	       	arr = script.match(/[\[][^:]*:[^\[\]]*[\]]/gi);
	       	if(arr){
	       		for(var j=0;j<arr.length;j++)
	       			if(arr[j].indexOf(":")!=-1)
		       			script = script.replace(arr[j],"\""+arr[j].substring(arr[j].indexOf(":")+1,arr[j].length-1)+"\"");
	       	}
	       	arr = script.match(/include\([^\']*/gi);
	       	if(arr){
	       		//格式：include(team,系统组:'3434328934822');include(secondPost,'4342342345453_-54534534534')
	       		for(var j=0;j<arr.length;j++){
	       			var data = "";
					if(arr[j].indexOf(":") != -1){
	       				data = arr[j].substring(arr[j].indexOf(":")+1);
					}
					else{
						data = arr[j].substring(arr[j].indexOf(",")+1);
					}
	       			if(data.indexOf("_")!=-1){
	       				data = data.replace(/_/,",");
					}
					var con  = arr[j].replace(/&#44;/gi,",");
	       			con = con.substring(0,con.indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	arr = script.match(/exclude\([^\']*/gi);
	       	if(arr){
	       		//格式：exclude(team,系统组:3434328934822)
	       		for(var j=0;j<arr.length;j++){
	       			var data = "";
					if(arr[j].indexOf(":") != -1){
	       				data = arr[j].substring(arr[j].indexOf(":")+1);
					}
					else{
						data = arr[j].substring(arr[j].indexOf(",")+1);
					}
	       			if(data.indexOf("_")!=-1){
	       				data = data.replace(/_/,",");
	       			}
	       			var con  = arr[j].replace(/&#44;/gi,",");
	       			con = con.substring(0,con.indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	script = script.replace(/<>/g,"!=");
	       	if(groupIds.length>0){
	       		try{
		       		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "getStandardPostAndLevel", false);
		       		requestCaller.addParameter(1, "String[]", groupIds);
		       		requestCaller.addParameter(2, "String[]", types);
		       		var rs = requestCaller.serviceRequest();
		       		if(rs){
		       			var idMapping;
		       			var regExp;
		       			for(var k=0;k<rs.length;k++){
		       				idMapping = rs[k].split(":");
		       				if(idMapping && idMapping.length==2){
		       					regExp = new RegExp(idMapping[0]);
		       					script = script.replace(regExp,idMapping[1]);
		       				}
		       			}
		       		}
		       	}catch(e){
		       		alert(e);
		       	}
		       	groupIds = new Array();
		    }
		    //跟踪代码开始
		    if(isDebug)
			    afterStripts[i] = script;
		    //跟踪代码结束
	       	try{
	       		eval(script);
	       	}
	       	catch(e){alert(e)}
	    }
	    if(isDebug){
	    	//跟踪代码开始
	    	try{
	    		var checkedCount = 0;
	    		var inputs = null;
	    		if(document.getElementById("conditionDiv")){
					inputs = document.getElementById("conditionDiv").getElementsByTagName("INPUT");
				}
				if(inputs){
					for(var i=0;i<inputs.length;i++){
					   	if(inputs[i].type=="checkbox" && inputs[i].checked){
					   		checkedCount++;
					   	}
					}
				}
				if(document.getElementById("nodeCount") && document.getElementById("nodeCount").value>1 && document.getElementById("nodeCount").value==checkedCount){
			    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "createBranchLog", false);
			    	requestCaller.addParameter(1,"String[]",beforeScripts);
			    	requestCaller.addParameter(2,"String[]",afterStripts);
			    	var affairId,summaryId,xml,formData;
			    	//新建页面
			    	if(document.getElementsByName("id") && document.getElementsByName("id").length>0)
			    		summaryId = document.getElementsByName("id")[0].value;
			    	//处理页面
			    	else if(document.getElementsByName("summary_id") && document.getElementsByName("summary_id").length>0)
			    		summaryId = document.getElementsByName("summary_id")[0].value;
			    	if(document.getElementsByName("affair_id") && document.getElementsByName("affair_id").length>0)
			    		affairId = document.getElementsByName("affair_id")[0].value;
			    	requestCaller.addParameter(3,"String",summaryId);
			    	requestCaller.addParameter(4,"String",affairId);
			    	//processxml
			    	if(typeof(caseProcessXML))
			    		xml = caseProcessXML;
			    	requestCaller.addParameter(5,"String",xml);
			    	
			    	try{
			    		formData = genJSObject()
			    	}catch(e){
			    	}
			    	requestCaller.addParameter(6,"String",formData);
			    	requestCaller.serviceRequest();
		    	}
	    	}catch(e){
	    	}
    		//跟踪代码结束
	    }
	}
}

/**
 * initCondition1()
 * @description 根据页面表单数据，对基于表单分支条件的分支进行计算
 * @time 2011-08-17
 * @author wangchw
 */
function initCondition_coll(){
	//从弹出页面获得所有条件分支计算时用到的js函数字符串,每条分支对应一个scripts隐藏表单input域
	var scripts = document.getElementsByName("scripts");
	//对条件分支的个数据进行判断，如果个数大于0，则进行下面的运算
	if(scripts&&scripts.length>0){
		//定义表单数据域的前缀，默认为my:
		var prefix = "";
		//定义父页面[{表单数据显示页面},{右侧协同处理页面[{弹出流程分支选择页面(当前页面)}]}]路径字符串:
		var parentURL = null;
		//定义标识是否来表单协同的标志,默认为false,不是来自表单协同
		var isFormFlag = false;
		try{
			//计算是否来表单协同的标志值
			isFormFlag = isForm=="true" || isForm==true;
		}catch(e){
			//do nothing
		}
	   	if(isFormFlag){//如果来自表单协同
	   		if(rootNodeName){//判断表单数据的根节点名称rootNodeName是否为空
	   			//如果不为空则将rootNodeName的值赋给prefix
	   			prefix = rootNodeName;
	   			parentURL = "_parent.";
	   		}else {//否则，从父页面中将表单数据的根节点名称rootNodeName读取过来,并赋给prefix
		   		//parentURL = "parent.detailMainFrame.contentIframe.";
	   			if(_parent.parent.detailMainFrame.contentIframe){
	   				parentURL = "_parent.parent.detailMainFrame.contentIframe.";
	   				prefix = _parent.parent.detailMainFrame.contentIframe.rootNodeName;
	   			}else{
	   				parentURL = "_parent.";
	   				prefix = _parent.rootNodeName;
	   			}
		   		//parent.detailMainFrame.contentIframe.rootNodeName;
		   		
		   	}
	   		//截取前缀
		   	prefix = prefix.substring(0,prefix.indexOf(":")+1);
	   	}
	   	var reg;//定义正则表达式变量
	   	//定义sum()和aver()函数的匹配正则表达式对象(,{为正则表达式中的特殊字符，需要进行转义
	   	var functionRef = new RegExp("sum[\(\{][^)]*[\}\)]|aver[\(\{][^)]*[\}\)]","g");
	   	//定义一个js列表对象,ArrayList为一个js对象，对js的Array进行了封装
	   	var list = new ArrayList();
	   	//获得弹出页面中条件分支的个数
	   	var len = scripts.length;
	   	//定义groupIds，这个变量用来干嘛呢？
	   	var groupIds = new Array();
	   	//定义类型，这个变量用来干嘛呢？
	   	var types = new Array();
	   	//定义一个数字类型的变量，这个变量用来干嘛呢？
	   	var num = 0;
	   	//定义存储计算之前的js字符串
	   	var beforeScripts = new Array();
	   	//定义从表单页面获的相应值<value,表达式>
	   	var formDataTrace= new Properties();
	   	//定义存储计算之后的js字符串
	   	var afterStripts = new Array();
	    for(var i=0;i<len;i++){
	    	//获得条件分支的js运算字符串
	      	var script = scripts[i].value;
	      	//alert("beforeScripts:="+script);
	      	if(isDebug){
	      		//记录下运算之前的js运算字符串
	      		beforeScripts[i] = script;
	      	}
	      	//先将重复项的‘{’‘}’去掉
	      	//首先匹配出对表单中的重复项数据进行求和sum()、求平均aver()的函数表达式
	      	var arr = script.match(functionRef);
	      	if(arr){
	      		for(var j=0;j<arr.length;j++){
	      			//循环遍历每一个sum({表单重复项变量})和aver({表单重复项变量})函数
	      			//并将这些函数转换为sum(\"my:表单重复项变量\")和aver(\"my:表单重复项变量\")
	      			script = script.replace(arr[j],(parentURL==null?"":parentURL)+arr[j].replace(/\{/g,"\""+prefix).replace(/\}/g,"\""));
	      		}
	      	}
	      	//定义一个临时变量
	      	var temp;
	      	//定义一个正则表达式，对exist()函数进行匹配,exist()是怎么样的一个函数？？
	      	//reg = new RegExp("exist\([^\|\| | \&\& | \']*\)","g");
	      	//reg = new RegExp("exist\(.+?,.+?,\d+?\)","g");
			//reg = /exist\(\{[^,|\{]+?\},[^,]+?,\d+?\)/g;
	      	reg = /exist\(\{[^,|\{]+?\},[^,]+?,[-]{0,1}\d+?\)/g;
	      	arr = script.match(reg);
	      	if(arr){
	      		for(var j=0;j<arr.length;j++){
		      		temp = arr[j].split(",");
		      		if(temp && temp.length==3){
			      		temp[0] = temp[0].replace("{","\""+prefix).replace("}","\"");
			      		temp[2] = temp[2].replace(")","\")");
			      		script = script.replace(arr[j],(parentURL==null?"":parentURL)+temp[0]+",\""+temp[1]+"\",\""+temp[2]);	
		      		}
	      		}
	      	}
	      	
	      	reg = /exist\(\{[^,|\{]+?\},[^,]+?,[-]{0,1}0\.\d+?\)/g;
	      	arr = script.match(reg);
	      	if(arr){
	      		for(var j=0;j<arr.length;j++){
		      		temp = arr[j].split(",");
		      		if(temp && temp.length==3){
			      		temp[0] = temp[0].replace("{","\""+prefix).replace("}","\"");
			      		temp[2] = temp[2].replace(")","\")");
			      		script = script.replace(arr[j],(parentURL==null?"":parentURL)+temp[0]+",\""+temp[1]+"\",\""+temp[2]);	
		      		}
	      		}
	      	}

			//var reg1= /exist\(\{[^,|\{]+?\},[^,]+?,[^,]+?:\d+?\)/g;
			var reg1= /exist\(\{[^,|\{]+?\},[^,]+?,[^,]+?:[-]{0,1}\d+?\)/g;
			arr = script.match(reg1);
			if(arr){
	      		for(var j=0;j<arr.length;j++){
		      		temp = arr[j].split(",");
		      		if(temp && temp.length==3){
			      		temp[0] = temp[0].replace("{","\""+prefix).replace("}","\"");
						var firstPosition= temp[2].lastIndexOf(":");
						var lastPosition= temp[2].lastIndexOf(")"); 
						temp[2]= temp[2].substring(firstPosition+1,lastPosition+1);
						temp[2] = temp[2].replace(")","\")");
			      		script = script.replace(arr[j],(parentURL==null?"":parentURL)+temp[0]+",\""+temp[1]+"\",\""+temp[2]);	
		      		}
	      		}
	      	}
	      	//定义一个正则表达式对象，匹配出{}中的字符串
	      	reg = new RegExp("[\{][^\{\}]*[\}]","g");
	      	//将script中的{变量名称}这些变量匹配出来
	       	arr = script.match(reg);
	       	if(arr){
	       		//表单数据项的值
	       		var formValue;
		       	for(var j=0;j<arr.length;j++){
		       		temp = arr[j].substring(1,arr[j].length-1);
		       		if(temp.indexOf("formStandardpost:")!=-1){
		       			temp = temp.substring(temp.indexOf("formStandardpost:")+17);
		       			formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");
		       			formDataTrace.put(""+formValue,"formStandardpost:[\""+prefix+temp+"\"]");
		       			if(formValue){
		       				groupIds[num] = formValue
		       				types[num] = "Post";
		       				script = script.replace(arr[j],groupIds[num]);
		       				num++;
		       			}else{//否则置成空字符串
		       				script = script.replace(arr[j],"\"\"");
		       			}
		       		}else if(temp.indexOf("formGrouplevel:")!=-1){
		       			temp = temp.substring(temp.indexOf("formGrouplevel:")+15);
		       			formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");
		       			formDataTrace.put(""+formValue,"formGrouplevel:[\""+prefix+temp+"\"]");
		       			if(formValue){
		       				groupIds[num] = formValue
		       				types[num] = "Level";
		       				script = script.replace(arr[j],groupIds[num]);
		       				num++;
		       			}else{
		       				script = script.replace(arr[j],"\"\"");
		       			}		
		       		}else{//否则，从表单中获得该变量的值
		       			formValue = eval((parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");
		       			//alert("formValue:="+formValue);
		       			script = script.replace(arr[j],(parentURL==null?"":parentURL)+"getFieldValueForFlow(\""+prefix+temp+"\")");	
		       			formDataTrace.put(""+formValue,"pageData:[\""+prefix+temp+"\"]");
		       		}
		       }						        	
	       	}
	    	//考虑这种情况：[3891364681527611249] == [集团公司 业务主办:-5026430098408846334]
	          arr= script.match(/\[[-]*\d*?\]/gi);
	          if(arr){
	            for(var j=0;j<arr.length;j++){
	              script = script.replace(arr[j],"\""+arr[j].substring(1,arr[j].length-1)+"\"");
	            }
	          }
		       	//匹配出script字符串中所有的枚举类型的变量
	          	///[\[][^:|\[]*:[^\[\]]*[\]]/
		       	arr = script.match(/[\[][^:|\[]*:[^\[\]]*[\]]/gi);
		       	if(arr){
		       		for(var j=0;j<arr.length;j++){
		       			if(arr[j].indexOf(":")!=-1){
		       				//将枚举变量表达式{枚举变量名称}==[枚举名称:枚举值]替换成{枚举变量名称}==枚举值
		       				script = script.replace(arr[j],"\""+arr[j].substring(arr[j].lastIndexOf(":")+1,arr[j].length-1)+"\"");
		       			}
		       		}
		       	}
	       	//对组进行匹配
	       	arr = script.match(/include\([^\']*/gi);
	       	if(arr){
	       		//格式：include(team,系统组:'3434328934822');include(secondPost,'4342342345453_-54534534534')
	       		for(var j=0;j<arr.length;j++){
	       			var data = "";
					if(arr[j].indexOf(":") != -1){
	       				data = arr[j].substring(arr[j].indexOf(":")+1);
					}else{
						data = arr[j].substring(arr[j].indexOf(",")+1);
					}
	       			if(data.indexOf("_")!=-1){
	       				//将数据中的下划线替换为逗号
	       				data = data.replace(/_/,",");
					}
	       			//&#44表示逗号
					var con  = arr[j].replace(/&#44;/gi,",");
	       			con = con.substring(0,con.indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	//对组进行匹配
	       	arr = script.match(/exclude\([^\']*/gi);
	       	if(arr){
	       		//格式：exclude(team,系统组:3434328934822)
	       		for(var j=0;j<arr.length;j++){
	       			var data = "";
					if(arr[j].indexOf(":") != -1){
	       				data = arr[j].substring(arr[j].indexOf(":")+1);
					}else{
						data = arr[j].substring(arr[j].indexOf(",")+1);
					}
	       			if(data.indexOf("_")!=-1){
	       				data = data.replace(/_/,",");
	       			}
	       			var con  = arr[j].replace(/&#44;/gi,",");
	       			con = con.substring(0,con.indexOf(",")+1)+data;
	       			script = script.replace(arr[j],con);
	       		}
	       	}
	       	script = script.replace(/<>/g,"!=");
	       	if(groupIds.length>0){
	       		try{
		       		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "getStandardPostAndLevel", false);
		       		requestCaller.addParameter(1, "String[]", groupIds);
		       		requestCaller.addParameter(2, "String[]", types);
		       		var rs = requestCaller.serviceRequest();
		       		if(rs){
		       			var idMapping;
		       			var regExp;
		       			for(var k=0;k<rs.length;k++){
		       				idMapping = rs[k].split(":");
		       				if(idMapping && idMapping.length==2){
		       					regExp = new RegExp(idMapping[0]);
		       					script = script.replace(regExp,idMapping[1]);
		       				}
		       			}
		       		}
		       	}catch(e){
		       		alert(e);
		       	}
		       	groupIds = new Array();
		    }
		    //跟踪代码开始
	       	//alert("afterStripts:="+script);
		    if(isDebug){
		    	afterStripts[i] = script;
		    }
		    //跟踪代码结束
	       	try{
	       		eval(script);
	       	}catch(e){
	       		alert(e);
	       	}
	    }
	    if(isDebug){
	    	//跟踪代码开始
	    	try{
	    		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "createBranchLogData", false);
	    		requestCaller.addParameter(1,"String[]",beforeScripts);
		    	requestCaller.addParameter(2,"String[]",afterStripts);
		    	var teamstr="";
		    	for(var i in team){
		    		teamstr +="["+i+"],";
		    	}
		    	var secondpoststr="";
		    	for(var i in secondpost){
		    		secondpoststr +="["+i+"],";
		    	}
		    	var startTeamstr="";
		    	for(var i in startTeam){
		    		startTeamstr +="["+i+"],";
		    	}
		    	var startSecondpoststr="";
		    	for(var i in startSecondpost){
		    		startSecondpoststr +="["+i+"],";
		    	}
		    	requestCaller.addParameter(3,"String",teamstr);
		    	requestCaller.addParameter(4,"String",secondpoststr);
		    	requestCaller.addParameter(5,"String",startTeamstr);
		    	requestCaller.addParameter(6,"String",startSecondpoststr);
		    	requestCaller.addParameter(7,"String",formDataTrace.toString());
		    	requestCaller.addParameter(8,"String","coll");
		    	requestCaller.serviceRequest();
	    	}catch(e){
	    		alert(e);
	    	}
    		//跟踪代码结束
	    }
	}
}

function moreCondition(moreInfo,readonly){
	var readOnly = readonly?readonly:false;
	var link = genericURL + "?method=showMoreCondition&moreInfo="+encodeURIComponent(moreInfo)+"&readonly="+readOnly;
    var moreCondition = v3x.openWindow({
			url : link,
			width : 230,
			height : 195,
			scrollbars:"no"
	});
	return moreCondition;
}

function NameAndValue(name,value){
	this.name = name;
	this.value = value;
}

/**
 * 示例代码
 * 所保护字段
 * DocForm.SignatureControl.FieldsList="XYBH=协议编号;BMJH=保密级别;JF=甲方签章;HZNR=合作内容;QLZR=权利责任;CPMC=产品名称;DGSL=订购数量;DGRQ=订购日期"      
 */
function getProjectField4Form(){
	var projectData= "";
	var ff = new Properties();
	if(typeof(fieldObjList)!='undefined' && fieldObjList!=null){
		for(var i = 0 ; i<fieldObjList.length ; i++){
			var fieldList = fieldObjList[i].fieldList;
			if(fieldList!=null){
				for(var j = 0 ; j<fieldList.length ; j++){
					if(ff.get( fieldList[j].name)!="" && typeof(ff.get( fieldList[j].name))!='undefined') 
						continue;
					else{
						ff.put( fieldList[j].name,1);
					}
					if(projectData!=""){
						projectData += ';';
					}
					projectData += fieldList[j].name+"="+fieldList[j].name;
				}
			}
		}
	}
	return projectData;
}
function openSignature(){
    var bodyType = document.getElementById("bodyType").value;
    if(bodyType=="HTML"||bodyType=="FORM"){
    		
    	//isignature html专业签章
		try{
			if(bodyType=="HTML"||bodyType=="FORM"){
				if (!isInstallIsignatureHtml()) {
					alert(_("collaborationLang.client_not_installed_professional_signature"));
					return ;
				}
			    //编辑状态不能盖章。
				var fckObj = document.getElementById("fckObj");
				var htmlContentDiv =  document.getElementById("htmlContentDiv");
				if(fckObj  && htmlContentDiv.style.display =='none'){ //编辑状态
					alert(_("collaborationLang.collaboration_alertCantISignatureWhenEdit"));
					return false;
				}
				var projectData = "";
				if(bodyType=="FORM"){
					projectData = getProjectField4Form();
				}
			}
			doSignature(genericControllerURL,projectData);
		}catch(e){
			alert(_("collaborationLang.collaboration_alertNotUseHtmlSignature"));
			return false;
		}
    	
    }else{
    	WebOpenSignature();
    }  
    contentUpdate=true;
}
//中间处理节点进行督办设置。
function openSuperviseWindowWhenDeal(summaryId,secretLevel){
		var affairId = document.getElementById('affair_id');
		if(affairId) {
			var state = getAffairState(affairId.value);
			if(state!=-1 && state==5) {
				alert("当前协同已被撤销");
				return;
			}
		}
    	var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		var count = document.getElementById("count");
		var unCancelledVisor = document.getElementById("unCancelledVisor");
		
		var sfTemp = parent.sVisorsFromTemplate;
		var urlStr = colSuperviseURL + "?method=superviseWindow&iscol=true";
		if(mId != null && mId != ""){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value  + "&sVisorsFromTemplate="+sfTemp +"&unCancelledVisor="+unCancelledVisor.value + "&count="+count.value;
		}
		urlStr += "&secretLevel="+secretLevel;
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
function openSuperviseWindow(){
	
		var mId = document.getElementById("supervisorId");
		var sDate = document.getElementById("awakeDate");
		var sNames = document.getElementById("supervisors");
		var title = document.getElementById("superviseTitle");
		//var canModify = document.getElementById("canModifyAwake");
		var unCancelledVisor = document.getElementById("unCancelledVisor");
		var sfTemp = document.getElementById("sVisorsFromTemplate");
		var temformParentId = document.getElementById("temformParentId");
		var urlStr = colSuperviseURL + "?method=superviseWindow";
		if(mId.value != null && mId.value != ""){
			urlStr += "&supervisorId=" + mId.value + "&supervisors=" + encodeURIComponent(sNames.value) 
			+ "&superviseTitle=" + encodeURIComponent(title.value) + "&awakeDate=" + sDate.value  
			+ "&sVisorsFromTemplate="+sfTemp.value +"&unCancelledVisor="+unCancelledVisor.value
			+ "&temformParentId="+temformParentId.value;
		}
	
        var rv = v3x.openWindow({
	        url: urlStr,
	        height: 300,
	        width: 400
     	});

    	if(rv!=null && rv!="undefined"){
    		var sv = rv.split("|");
    		if(sv.length == 4){
				mId.value = sv[0]; //督办人的ID(添加标识的，为的是向后台传送)
				sDate.value = sv[1]; //督办时间
				sNames.value = sv[2]; //督办人的姓名
				title.value = sv[3];
				//canModify.value = sv[4];
			}
    	}
}

function arrayToArray(array){
	var r = [];
	if(array != null){
		for(var i = 0; i < array.length; i++) {
			r[i] = array[i];
		}
	}
	
	return r;
}

//协同督办催办
function colHasten(_summaryId,_superviseId, memberIdStr, activityId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "initMemberIds", false, "POST");
		requestCaller.addParameter(1, "String", memberIdStr);
		requestCaller.addParameter(2, "String", activityId);
		requestCaller.serviceRequest();
	}
	catch (ex1) {
	}
	
	var rv = v3x.openWindow({
        url: genericURL + "?method=preHasten&maxSize=85&summaryId=" + _summaryId + "&activityId="+activityId + "&superviseId=" + _superviseId,
        height: 450,
        width: 380
    });
}

function showSuperviseWindow(finished, templeteId){
	if(finished == "true" || templeteId){
		alert(_("collaborationLang.cannotSupervise_flow_end_or_template"));
		return;
	}
	
	var rv = v3x.openWindow({
    	url: colSuperviseURL + "?method=superviseWindowEntry&summaryId=" + summary_id,
    	height: 300,
       	width: 400
    });
}

var opinionId5;
function doReplay(f){
	if(checkReplyForm(f) && saveAttachment()){
		if(document.getElementById("replyDiv" + opinionId5)){
			document.getElementById("replyDiv" + opinionId5).style.display = 'none';
		}
		f.b11.disabled = true;
		f.b12.disabled = true;
		return true;
	}
	else{
		return false;
	}
}

/**
 * 隐藏不符合要求的分支条件
 * @param nodeId 分支流程节点Id
 */
function hiddenFailedCondition(nodeId){
	var hiddenDiv = document.getElementById('failedCondition');
	var showDiv = document.getElementById("d"+nodeId);
	if(hiddenDiv && showDiv){
		hiddenDiv.innerHTML += showDiv.outerHTML;
		var el = showDiv.parentNode;
		el.removeChild(showDiv);
	}
	var hiddenDiv2 = document.getElementById('failedConditionSelector');
	var showDiv2 = document.getElementById("selector"+nodeId);
	if(hiddenDiv2 && showDiv2){
		hiddenDiv2.innerHTML += showDiv2.outerHTML;
		var el = showDiv2.parentNode;
		el.removeChild(showDiv2);
	}
	var aDiv = document.getElementById("aDiv");
	if(aDiv){
		aDiv.style.display = "";
	}
}

/**
 * 显示不符合条件的分支
 * @param labelObj
 */
function showFailedCondition(labelObj){
	//获得不符合条件的分支Div标签
	var failedConditionDiv = document.getElementById("failedCondition");
	//这个DIV好像没什么作用？
	var failedSelectorDiv = document.getElementById("failedConditionSelector");
	//显示不满足条件链接信息
	var aDiv=document.getElementById("aDiv");
	var lgdObj = document.getElementById("lgd");
	if(failedConditionDiv && failedConditionDiv.style.display=="none"){
		failedConditionDiv.style.display = "";
		if(failedSelectorDiv){
			failedSelectorDiv.style.display = "";
		}
		if(lgdObj){
			lgdObj.style.display= "none";
		}
		aDiv.innerHTML = hideLabel;
	}else{
		if(failedConditionDiv){
			failedConditionDiv.style.display = "none";
		}	
		if(failedSelectorDiv){
			failedSelectorDiv.style.display = "none";
		}
		if(lgdObj){
			lgdObj.style.display= "block";
		}
		aDiv.innerHTML = showLabel;
	}
}

function hideAttention(object){
	var divObj = document.getElementById(object);
	divObj.style.display = "none";
}

function showNextSpecialCondition(conditionObject) {
	var options = conditionObject.options;

	for (var i = 0; i < options.length; i++) {
	    var d = document.getElementById(options[i].value + "Div");
	    //alert(d);
	    if (d) {
	        d.style.display = "none";
	 	}
	}
	if(document.getElementById(conditionObject.value + "Div") == null) return;
		    document.getElementById(conditionObject.value + "Div").style.display = "block";
}
	
function include(arr,id){
	if(!arr)
		return false;
	for(var i in arr){
		if(i == id)
			return true;
	}
	return false;
}

function exclude(arr,id){
	return !include(arr,id);
}

/**
 *发起/处理时显示分支描述
*/
function showBranchDesc(linkId,templateId){
	if(!templateId){
		var parentWin = window.dialogArguments;
		var desc = "";
		var branch;
		if(parentWin && parentWin.parent && parentWin.parent.detailMainFrame ){
			if(parentWin.parent.detailMainFrame.branchs){
				branch= parentWin.parent.detailMainFrame.branchs[linkId];
            }else if(parentWin.parent.detailMainFrame.contentIframe && parentWin.parent.detailMainFrame.contentIframe.branchs){
            	branch = parentWin.parent.detailMainFrame.contentIframe.branchs[linkId];
            }
		}else if(parentWin){
            branch = parentWin.branchs[linkId];
		}
		if(branch)
			desc = branch.conditionDesc;
		moreCondition(desc,true);
	}else{
		//待发时不能从页面取描述信息，只能到数据库中取
		var rv = v3x.openWindow({
	        url: templeteURL + "?method=showBranchDesc&readonly=true&linkId=" + linkId + "&templateId=" + templateId,
	        width : 230,
			height : 195,
			scrollbars:"no"
    	});
	}
}

/**
 * 预发布公告,新闻
 * return 0--失败,1--成功
 */
function preAuditPass(policyName, data){
   var d=new Date();
	var rv = v3x.openWindow({
        url: genericURL + "?method=preIssueNewsOrBull&policyName=" + policyName+"&t="+d.getTime(),
        height: 320,
        width: 270
    });
    if(!rv || rv.length == 0) return 0;
	//给data赋值
    data.typeId = rv[0];
    data.memberIdsStr = rv[1];
    data.allowPrint = rv[2];
    return 1;
}

function setIssusPeopleFields(elements) {
	
    var theForm = document.getElementsByName("preIssusForm")[0];
    if (!elements) {
        return false;
    }
    var workFlowContent = "";
    var isShowShortName = false;
    for (var i = 0; i < elements.length; i++) {
    	if(i > 0){
    		workFlowContent += _("V3XLang.common_separator_label")
    	}
        var person = elements[i];
		var _text = person.name;
        if(isShowShortName == true && person.accountShortname != "null" && person.accountShortname != "undefined" && person.accountShortname != ""){
        	_text = "(" + person.accountShortname + ")" + _text;
        }
        workFlowContent += _text;
    }
	var workflowInfoObj = document.getElementById("issusScope");
	workflowInfoObj.value = workFlowContent;
	var memberIdsObj = theForm.memberIdsStr;
	memberIdsObj.value = getIdsString(elements, true);
    return true;
}

/**
 * 检查是否有公告、新闻权限
 */
function hasAuth(policyName, id, accountId){
	var ajaxManager;
	if(policyName == "newsaudit"){
		ajaxManager = "newsTypeManager"
	}else{
		ajaxManager = "bulTypeManager"
	}
	var requestCaller4Validate = new XMLHttpRequestCaller(this, ajaxManager, "hasAuth", false);
	requestCaller4Validate.addParameter(1, "Long", id);
	requestCaller4Validate.addParameter(2, "Long", accountId);
    return requestCaller4Validate.serviceRequest();
}

/**
 * 审核通过,发布
 */
function auditPass(theForm, action, policyName, summaryId, affairId, id, accountId){
	//如果其他人正在编辑流程，则禁止协同转公告或新闻
	if( !checkModifyingProcess(theForm.processId.value,summaryId) ){
		return;
	}
	//发布公告,新闻
	var data = {
        typeId : [],
        summaryId : [],
        memberIdsStr : [],
        affairId : [],
        allowPrint : []
    };
    		//意见是否可为空
	var content = theForm.content;
	var opinionPolicy = theForm.opinionPolicy;
	disabledPrecessButton(true);
	if(opinionPolicy && opinionPolicy.value ==1 && content && content.value == ''){
		disabledPrecessButton(false);
		alert(v3x.getMessage("collaborationLang.collaboration_opinion_mustbe_gived"));
		return;
	}
    data.summaryId = summaryId;
    data.affairId = affairId;
    //校验是否可发布
    var isAuth = hasAuth(policyName, id, accountId);
    var isSuccess = 0;
    if(isAuth == 'true'){
    	isSuccess = preAuditPass(policyName, data);
    }else {
    	alert(v3x.getMessage("collaborationLang.not_purview"));
    }
	if(isSuccess == 0){
		disabledPrecessButton(false);
		return;
	}else{
		//发布公告/新闻
		var actionUrl = collaborationCanstant.issusBulletionActionURL;
		if(policyName == "newsaudit"){
			actionUrl = collaborationCanstant.issusNewsActionURL;
		}
	    try{
	    	parent.detailMainFrame.contentIframe.saveContent();
    	}catch(e){}
//	    if(submitMap(data, actionUrl, "showDiagramFrame", "post")){//增加顺序感，免得协同先提交了。
//	    	//处理事项
//			doSign(theForm, action);
//	    }
	    //上面代码不能确保转发公告或新闻和处理事项的先后执行顺序，将处理事项过程转到Controller中进行，调用issueFinishDo()方法
	    submitMap(data, actionUrl, "showDiagramFrame", "post");
	}
}
function issueFinishDo(){
	var theForm = document.getElementById("theform");
	doSign(theForm, theForm.action);
}
/**
 * 审核不通过
 */
function auditNoPass(theForm, action){
	//处理事项
	//doSign(theForm, action);
	//终止协同
	stepStop(theForm);
}


function openTemplateDetail(){
	var theForm = document.getElementById("workflowForm");
	var AppEnumKeyOption = theForm.condition.options[theForm.condition.selectedIndex];
	var selectedAppEnumKey = AppEnumKeyOption.value;
	var operationTypeValue = document.getElementById("operationTypeValue");
	var ids = "";
	if(operationTypeValue)
		ids = operationTypeValue.value;
	var rv = v3x.openWindow({
        url: genericURL + "?method=openTemplateDetail&selectedAppEnumKey="+selectedAppEnumKey,
        height: 450,
        width: 350
    });
    
    if(!rv)return;
    var templeteIds = rv[0];
    var templeteSubjects = rv[1];
	var templeteIdsStr = "";
	var templeteSubjectsStr = "";
	for(var i =0;i<templeteIds.length;i++){
		if(i == (templeteIds.length-1)){
			templeteIdsStr += templeteIds[i];
			templeteSubjectsStr += templeteSubjects[i];
		}else{
			templeteIdsStr += templeteIds[i] + "@";
			templeteSubjectsStr += templeteSubjects[i] + "、";
		}
	}
	document.getElementById("operationTypeTitle").value = templeteSubjectsStr;
	document.getElementById("operationTypeTitle").title = templeteSubjectsStr;
    operationTypeValue.value = templeteIdsStr;
}

function checkFormExist(aId,fId,oId){
	try{
    	var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "checkForm", false);
    	requestCaller.addParameter(1, "long", aId);
    	requestCaller.addParameter(2, "long", fId);
    	requestCaller.addParameter(3, "long", oId);
    	var ds = requestCaller.serviceRequest();
    	if(ds == "1"){
    		alert("表单应用不存在");
    		return false;
    	}else if(ds == "2"){
    		alert("表单视图不存在");
    		return false;
    	}else if(ds == "3"){
    		alert("表单操作不存在");
    		return false;
    	}
    	return true;
    }catch(e){
    }
}
/**
 * 协同转日程方法
 */
function colToEvent(eventUrl,title,type,id,secretLevel){
	var state = getAffairState(id);
	if(state!=-1 && state==5) {
		alert("当前协同已被撤销");
		return;
	}
	if(v3x.getBrowserFlag('pageBreak')){
		var rv = v3x.openWindow({
			url: eventUrl + "?method=colToEvent&title="+encodeURIComponent(title)+"&appType="+type+"&id="+id+"&from=coll&secretLevel="+secretLevel,
	        height: 480,
	        width: 530
	    });
	}else{
		var divObj = "<div id=\"colToEventWin\" closed=\"true\">" +
					 	"<iframe id=\"colToEventWin_Iframe\" name=\"colToEventWin_Iframe\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\"></iframe>" +
					 "</div>";
		getA8Top().$(divObj).appendTo("body");
		getA8Top().$("#colToEventWin").dialog({
			title: v3x.getMessage("collaborationLang.coll_event"),
			width: 520,
			height: 500,
			closed: false,
			modal: true,
			buttons:[{
						text:v3x.getMessage("collaborationLang.submit"),
						handler:function(){
							var rv = getA8Top().$("#colToEventWin_Iframe").get(0).contentWindow.OK();
						}
					},{
						text:v3x.getMessage("collaborationLang.cancel"),
						handler:function(){
							getA8Top().$('#colToEventWin').dialog('destroy');
						}
					}]
		});
		getA8Top().$("#colToEventWin_Iframe").attr("src",eventUrl + "?method=colToEvent&title="+encodeURIComponent(title)+"&appType="+type+"&id="+id+"&from=coll");
	}
}
function reloadParent(){
	if(getA8Top().$('#colToEventWin').length > 0){
		getA8Top().$('#colToEventWin').dialog('destroy');
	}else{
		parent.location.href = parent.location;
	}
}
/**
 * 校验模板编号是否合法
 */
function checkForOutSysCallOption(){
	var checkNumberObj = document.getElementById("templeteNumber");
	var templeteId4NumberObj = document.getElementById("templeteId4Number");
	if(checkNumberObj && checkNumberObj.value != ""){
		if(check(checkNumberObj.value)==false){
			alert(templateCodeAlertLabel);
			return false;
		}
		//模板编号唯一性校验
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxTempleteManager", "checkTempleteCodeIsUnique", false);
		requestCaller.addParameter(1, "String", templeteId4NumberObj.value);
		requestCaller.addParameter(2, "String", checkNumberObj.value);
		var result = requestCaller.serviceRequest();
		if(result == "true"){
			return true;
		}
		else{
			alert(templateCodeDupleLabel);
			return false;
		}
	}
	return true;
	function check(objValue){
	    var reg = /^([a-zA-Z]|\d|_)*$/;
	    if(!reg.test(objValue)){
	        return false;
	    }
	    if(objValue.length > 20){
	         return false;
	    }
	    return true;
	}
}

//显示“是否对发起者隐藏”选项
function showMoreHiddenOption(v){
	var showToIdSpan = document.getElementById("moreHiddenOption");
	if(v.checked){
		if(showToIdSpan){
			showToIdSpan.style.display = "inline-block";
		}
	}else{
		if(showToIdSpan){
			showToIdSpan.style.display = "none";
		}
	}
}

function quoteDocumentFromOK() {
    var _parent = top.window.opener;

    if (top.window.dialogArguments) {
        _parent = top.window.dialogArguments;
    }
    var affairid =document.all("affairid").value;
    var summaryid = document.all("summaryid").value;
    if(summaryid == ""){
    	window.close();
    	return;
    }
    var templeteid =window.dialogArguments.document.getElementById("templeteId").value;
    var formid =window.dialogArguments.document.getElementById("formid").value;
    var operationid =window.dialogArguments.document.getElementById("operationid").value;
    	_parent.isFormSumit = true;
        _parent.location.href = genericURL + "?method=newColl&templeteId=" + templeteid+"&affairid=" + affairid+"&summaryid=" + summaryid+"&formid=" + formid+"&operationid=" + operationid+"&quoteFromsign=quoteFrom";
        top.window.close();
   
    
}

function setQuoteFormvalue(affair,summary){

   var affairid = document.all("affairid");
   affairid.value = affair;
   var summaryid =document.all("summaryid");
   summaryid.value = summary;

}
function openWorkManagerDetail(id, colState){
	var from;
	if (colState) {
		from = colState;
	} else {
		from = "";
	}
	var returnValue = v3x.openWindow({url : genericURL+"?method=detail&from=" + from + "&affairId=" + id,workSpace : "yes",dialogType:v3x.getBrowserFlag('hideMenu')?'modal':'open'});
	if(returnValue){
		if(returnValue =='true' || returnValue == true){
			document.location.reload();
			parent.statFrame.location.href=parent.statFrame.location.href+"&init=false";
		}
	}
}
function openEdocWorkManagerDetail(url,openType){
	if(openType == 'href'){
		parent.parent.location.href = url;
	}else {	
		var returnValue = v3x.openWindow({url : url,workSpace : "yes"});
		if(returnValue){
			if(returnValue =='true' || returnValue == true){
				document.location.reload();
				parent.statFrame.location.href=parent.statFrame.location.href+"&init=false";
			}
		}
	}
}

function setMenuState(menu_id)
{
  var menuDiv=document.getElementById(menu_id);
  if(menuDiv!=null)
  {
    menuDiv.className='webfx-menu--button-sel';
    menuDiv.firstChild.className="webfx-menu--button-content-sel";
    menuDiv.onmouseover="";
    menuDiv.onmouseout="";
  }else{
  	return;
  }
}

function updateAtt(summaryId){
	var theForm = document.theform;
	if(!checkModifyingProcessAndLock(theForm.processId.value, summaryId)){
		return;
	}
	var detailFrame =  parent.detailMainFrame;
	//取得要修改的附件
	var attList = detailFrame.getAttachment(summaryId,summaryId);
	var result = editAttachments(attList,summaryId,summaryId,'1');
	if(result){
		//将修改后的附件，与本地更新。
		detailFrame.updateAttachmentMemory(result,summaryId,summaryId,'')
		detailFrame.showAttachment(summaryId, 2, 'attachment2Tr', 'attachment2NumberDiv','attachmentHtml2Span');
		detailFrame.showAttachment(summaryId, 0, 'attachment1Tr', 'attachmentNumberDiv','attachmentHtml1Span');
	}
}
/**
 * 获得已经选择的关联流程
 * @returns
 */
function getSelected(){
	var id_checkbox = parent.quoteDocumentFrame.document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }
    var count = 0 ;
    var idCheckBox ;
   
	for(var i=0; i<id_checkbox.length; i++){
		if(id_checkbox[i].checked){
			count= count +1 ;
			idCheckBox =  id_checkbox[i] ;
		}
	}
  

    if(count == 1){
    	return idCheckBox ;
    }
    else if(count == 0){
    	idCheckBox='';
        return idCheckBox;
    }else{
    	alert(v3x.getMessage("collaborationLang.id_alter_select_one"));
 		return;
    }

}

/**
 * 关联流程的选择
 */
function selectFinishCol(){
	
	var _parent = window.opener;
	if(_parent == null){
		_parent = window.dialogArguments;
	}
	
    var dv = _parent.extendField;
    if(!dv){
    	return ;
    }
    var id_checkbox = getSelected() ;
    
    if(id_checkbox== null ){
    	return ;
    }
     if(id_checkbox == ''){
    	dv.label = "";
    	dv.value = "";
    }else{
        dv.label = id_checkbox.getAttribute("showValue") ;
        dv.value = id_checkbox.value ; 	
    }
    window.close();
}
function batchColl(){
	var checkBoxs = document.getElementsByName("id");
	if(!checkBoxs){
		alert(_("collaborationLang.batch_select_affair"));
		return ;
	}
	var process = new BatchProcess();
	for(var i = 0 ; i < checkBoxs.length;i++){
		if(checkBoxs[i].checked){
			var affairId = checkBoxs[i].getAttribute("affairId");
			var subject = checkBoxs[i].getAttribute("colSubject");
			var app =  checkBoxs[i].getAttribute("category")||"1";
			process.addData(affairId,checkBoxs[i].value,app,subject);
		}
	}
	if(!process.isEmpty()){
		var r = process.doBatch();
	}else{
		alert(_("collaborationLang.batch_select_affair"));
		return ;
	}
}

/**
 * 查看属性
 */
function showAttribute(affairId, from){
	getA8Top().v3x.openWindow({
        url: genericURL + "?method=showAttribute&affairId=" + affairId + "&from=" + from,
        dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
        width: "400",
        height: "510"
    });
}

function showSupervise(summaryId,openModal){
	getA8Top().v3x.openWindow({
        url: genericURL + "?method=superviseDiagram&summaryId=" + summaryId + "&openModal=" + openModal,
        dialogType : v3x.getBrowserFlag('openWindow') == true ? "modal" : "1",
        width: "350",
        height: "440"
    });
}

function saveConfig(memberId){
	var personalSet = document.getElementById("personalSet");
	if(personalSet){
		try {
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "saveConfig", false);
			requestCaller.addParameter(1, "Long", memberId);
			requestCaller.addParameter(2, "String", personalSet.options[personalSet.selectedIndex].value);
			var rs = requestCaller.serviceRequest();
			if(rs == "false"){
				alert(_("collaborationLang.extended_setup_failure"));
				return;
			}
		}
		catch (ex1) {
			alert("Exception : " + ex1);
			return;
		}
	}
}
var cnum = -1;
var count = 0;
var currentSelectedObj = null;  //保存当前选择的处理的命名的
function removeClass(obj,className){
	var oldclass = obj.className;
	obj.className = oldclass.replace(className,"");
}
function addClass(obj,className){
	obj.className = obj.className +" "+className;
}
function opsearch(str,flag){
	count++;
	if(count>=3) {
		count = 0;
		return;	//这个变量又来避免查不到内容的时候死循环。
	}
	if(flag=="foward"){//向前查找
		var c;
		if( cnum == num) c = 0;
		else c = cnum+1; 
		
		if(currentSelectedObj!=null) removeClass(currentSelectedObj,"selectMemberName");
		
		for(var i =c;i<= num ;i++){
			var obj = document.getElementById("smember"+i);
			if(obj){
				if(obj.innerHTML.indexOf(str)!=-1){
					var path = document.location.href;
					var jinghao = path.indexOf("#")
					if(jinghao > 0){
						path = path.substring(0, jinghao);
					}
					document.location.href = path + "#smember" + i;
					addClass(obj,"selectMemberName");
					cnum = i;
					count = 0;
					currentSelectedObj = obj;
					break;
				}else if( i == num){
					cnum = i;
					opsearch(str,flag)
				}
			}
		}
	}else if(flag == "back"){ //向后查找
		var c;
		if(cnum == 0 || cnum == -1){
			c = num;
		}else{
			c = cnum -1;
		}

		if(currentSelectedObj!=null) removeClass(currentSelectedObj,"selectMemberName");
		
		for(var i =c;i>=0 ;i--){
			var obj = document.getElementById("smember"+i);
			if(obj){
				if(obj.innerHTML.indexOf(str)!=-1){
					var path = document.location.href;
					var jinghao = path.indexOf("#")
					if(jinghao > 0){
						path = path.substring(0, jinghao);
					}
					document.location.href = path + "#smember" + i;
				    addClass(obj,"selectMemberName");
					cnum = i;
					count = 0;
					currentSelectedObj = obj;
					break;
				}else if( i==0 ){
					cnum = i;
					opsearch(str,flag);
				}
			}
		}
	}
}
//当选择不同意时弹出选择页面（回退，或者终止，或者继续）
function showDisAgreeForm(canStepBack, canStepStop, canrepealItem){
	
	var rv = v3x.openWindow({
        url: genericControllerURL + "collaboration/disagreeForm&stepBackFlag="+canStepBack+"&stepStopFlag="+canStepStop + "&repealItemFlag=" + canrepealItem,
        height: 180,
        width: 300
    });
	if(rv=='stepBack'){
		stepBack(document.theform, document.getElementById('summary_id').value);
		return true;
	}
	if(rv=='stepStop'){
		stepStop(document.theform)
		return true;
	}
	if(rv=='goOn'){
		return false;
	}
	if(rv=='cancel'){
		repealItem('showDiagram', document.getElementById('summary_id').value);
		return true;
	}
	//取消 和直接关闭的时候设置按钮可用。
	disabledPrecessButton(false);
	return true;
}
//处理不同意的意见
function disagree(){
	var stepBackFlag=document.getElementById("stepBackFlag");
	var stepStopFlag=document.getElementById("stepStopFlag");
	var repealItemFlag = document.getElementById("repealItemFlag");
	var canStepBack=false;
	var canStepStop=false;
	var canrepealItem = false;
	if(stepBackFlag!=null&&typeof(stepBackFlag)!='undefined'){
		canStepBack=true;
	}
	if(stepStopFlag!=null&&typeof(stepStopFlag)!='undefined'){
		canStepStop=true;
	}
	if(repealItemFlag!=null&&typeof(repealItemFlag)!='undefined'){
		canrepealItem = true;
	}
	
	//选择不同意，弹出选择界面
    var attitudes=document.getElementsByName("attitude");
    
    if(canStepBack||canStepStop || canrepealItem){
    	for(var i=0;i<attitudes.length;i++){
    		if(attitudes[i].value==3&&attitudes[i].checked){
    			if(showDisAgreeForm(canStepBack, canStepStop, canrepealItem)){
    				return true;
    			}
    		}
    	}
    }
}

//表单授权
function setRelationAuth(){ 
	var id_checkbox = document.getElementsByName("id");
    if (!id_checkbox) {
        return;
    }
    var len = id_checkbox.length;
    var checkedCount = 0;
    var notFormCount = 0;
    var summaryId;
    var checkedIndex = 0;
    for (var i = 0; i < len; i++) {
        if (id_checkbox[i].checked) {
            checkedCount++;
            checkedIndex = i;
            if(id_checkbox[i].getAttribute("bodyType")!="FORM"){
            	notFormCount++;
            }
        }
    }

    if (checkedCount<1) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertAuthorityFlow"));
        return;
    }
    if (notFormCount>0) {
        alert(v3x.getMessage("collaborationLang.collaboration_alertAuthorityOnlyFormflow"));
        return;
    }
    if(checkedCount==1){
    	summaryId = id_checkbox[checkedIndex].value;
    }
	selectPeopleForRelationAuthority(summaryId);
}

//关联授权选人
function selectPeopleForRelationAuthority(summaryId){
	elements_relationAuthority = null;
	if(typeof(summaryId)!="undefined"){
		var authority = getRelationAuthority(summaryId);
	    elements_relationAuthority = parseElements(authority);	
	}
	onlyLoginAccount_relationAuthority = false;
	isCanSelectGroupAccount_relationAuthority = true;
	hiddenRootAccount_relationAuthority = true;
    selectPeopleFun_relationAuthority();
}

//表单关联授权回调
function setRelationAuthority(elements,summayId){
	try {
		var items=new Array();
		if(typeof(summayId)!= "undefined"){
			//单条信息授权
			items.push(summayId);
		}else{
			//列表多条已发授权
			var id_checkbox = document.getElementsByName("id");
			var len = id_checkbox.length;
		    for (var i = 0; i < len; i++) {
		        if (id_checkbox[i].checked) {
		        	items.push(id_checkbox[i].value);
		        }
		    }
		}
    	var authorities = getIdsString(elements);
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColRelationAuthorityManager", "create", false);
		requestCaller.addParameter(1, "String[]", items);
		requestCaller.addParameter(2, "String", authorities);
		var rs = requestCaller.serviceRequest();
		if(typeof(summayId) == "undefined"){
			parent.getA8Top().reFlesh();
		}
	}
	catch (ex) {
		alert("Exception : " + ex);
		return;
	}
}

//获取表单关联授权
function getRelationAuthority(summaryId){
	try {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColRelationAuthorityManager", "getAuthoritiesBySummaryId", false);
		requestCaller.addParameter(1, "String", summaryId);
		var rs = requestCaller.serviceRequest();
		return rs;
	}
	catch (ex) {
		alert("Exception : " + ex);
		return;
	}
}

//表单关联获取已结束的表单流程
function getFinishColFormList(obj){
	var mySent = true;
	if(obj.value != "mySent"){
		mySent = false;
	}
	window.location.href="/seeyon/collaboration.do?method=listFinishColFormForm&formId="+obj.getAttribute("formId")+"&isMySent="+mySent;
}
function getAffairState(affairId) {
	if (affairId!=null) {
		var requestCaller = new XMLHttpRequestCaller(this, "ajaxColManager", "getAffairState", false);
		requestCaller.addParameter(1, "Long", affairId);
		var result = requestCaller.serviceRequest();
		return result;
	}
	return -1;
}
function insertAttachmentAndActiveOcx(){
	insertAttachment();
	activeOcx();
}
/**将焦点设置到office控件上，否则容易出现因为打开模态对话框以后
* office控件焦点丢失不能编辑的问题。
* */
function activeOcx(){
	try{
		activeOfficeOcx();
	}catch(e){
		
	}
}

function changeSecretLevel(object){
	var selectValue = object.value;
	var theForm = document.getElementsByName("sendForm")[0];
	var workflowInfoObj = document.getElementById("workflowInfo");
	var peopleObj= document.getElementById("people");
    if(theForm.process_xml.value != "" && selectValue > flowSecretLevel_wf){
		if(window.confirm('密级变高后需要清空当前流程信息重新编辑，你确定要改变密级吗？')){
			peopleObj.innerHTML = "";
			workflowInfoObj.value = "<点击新建流程>";
			theForm.process_xml.value = ""
			theForm.process_desc_by.value = ""
			flowSecretLevel_wf = object.value;
		    hasWorkflow = false;
		    isFromTemplate = false;
            return true;
         }else{
        	document.getElementById("secretLevel").value = flowSecretLevel_wf;
            return false;
        }
    }else{
    	flowSecretLevel_wf = object.value;
	}
    
}