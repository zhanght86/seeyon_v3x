//   返回两位数的年份   
  function   GetHarfYear(date)   
  {   
  var   v=date.getYear();   
  if(v>9)return   v.toString();   
  return   "0"+v;   
  }   
    
  //   返回月份（修正为两位数）   
  function   GetFullMonth(date)   
  {   
  var   v=date.getMonth()+1;   
  if(v>9)return   v.toString();   
  return   "0"+v;   
  }   
    
  //   返回日   （修正为两位数）   
  function   GetFullDate(date)   
  {   
  var   v=date.getDate();   
  if(v>9)return   v.toString();   
  return   "0"+v;   
  }   
    
  //   替换字符串   
  function   Replace(str,from,to)   
  {   
  return   str.split(from).join(to);   
  }   
    
//关闭展开左面板
function closeLeft(flag){
	var obj = parent.document.getElementById("treeandlist");
	if(flag == "yes"){
		obj.cols = "15%,*";
	}
	else{
		obj.cols = "0,*";
	}
}

var flowSecretLevel_planUser1 = 1;
//   格式化日期的表示   
function   CalFormatDate(date,str)   {   
  str=Replace(str,"yyyy",date.getFullYear());   
  str=Replace(str,"MM",GetFullMonth(date));   
  str=Replace(str,"dd",GetFullDate(date));   
  str=Replace(str,"yy",GetHarfYear(date));   
  str=Replace(str,"M",date.getMonth()+1);   
  str=Replace(str,"d",date.getDate());   
  
  str=Replace(str,"HH",date.getHours()>9?date.getHours():"0"+date.getHours()); 
  str=Replace(str,"mm",date.getMinutes()>9?date.getMinutes():"0"+date.getMinutes()); 
  str=Replace(str,"ss",date.getSeconds()>9?date.getSeconds():"0"+date.getSeconds()); 
  return   str;   
}  

function   CalDateAdd(interval,number,date)   
  {   
  /*   
    *---------------   DateAdd(interval,number,date)   -----------------   
    *   DateAdd(interval,number,date)     
    *   功能:实现VBScript的DateAdd功能.   
    *   参数:interval,字符串表达式，表示要添加的时间间隔.   
    *   参数:number,数值表达式，表示要添加的时间间隔的个数.   
    *   参数:date,时间对象.   
    *   返回:新的时间对象.   
    *   var   now   =   new   Date();   
    *   var   newDate   =   DateAdd("d",5,now);   
    *   author:wanghr100(灰豆宝宝.net)   
    *   update:2004-5-28   11:46   
    *---------------   DateAdd(interval,number,date)   -----------------   
    */   
          switch(interval)   
          {   
                  case   "y"   :   {   
                          date.setFullYear(date.getFullYear()+number);   
                          return   date;   
                          break;   
                  }   
                  case   "q"   :   {   
                          date.setMonth(date.getMonth()+number*3);   
                          return   date;   
                          break;   
                  }   
                  case   "m"   :   {   
                          date.setMonth(date.getMonth()+number);   
                          return   date;   
                          break;   
                  }   
                  case   "w"   :   {   
                          date.setDate(date.getDate()+number*7);   
                          return   date;   
                          break;   
                  }   
                  case   "d"   :   {   
                          date.setDate(date.getDate()+number);   
                          return   date;   
                          break;   
                  }   
                  case   "h"   :   {   
                          date.setHours(date.getHours()+number);   
                          return   date;   
                          break;   
                  }   
                  case   "m"   :   {   
                          date.setMinutes(date.getMinutes()+number);   
                          return   date;   
                          break;   
                  }   
                  case   "s"   :   {   
                          date.setSeconds(date.getSeconds()+number);   
                          return   date;   
                          break;   
                  }   
                  default   :   {   
                          date.setDate(d.getDate()+number);   
                          return   date;   
                          break;   
                  }   
          }   
  }   

function otherCancel(url){
	var ids=getSelectIds();
	if(ids==''){
		alert(v3x.getMessage("bulletin.please_select_record"));
		return;
	}
	if(!confirm(v3x.getMessage("bulletin.confirm_cancel"))) return;
	
	var dlgArgs=new Array();		
	dlgArgs['html']=false;	
	dlgArgs['width']=608;
	dlgArgs['height']=410;
	dlgArgs['url']=url+"otherCancel"+"&id="+ids;
	var result=v3x.openWindow(dlgArgs);
	if(result=='true'){
		window.location.reload();
	}
}

function otherCancelOper(id){
	openTheWindow(baseUrl+'edit&oper=plan'+'&id='+id);
}

function deleteCalEvent(){
	var ids=document.getElementsByName('id');
	var id='';
	var count = 0;
	var periodicalStyle = 0;
	for(var i=0;i<ids.length;i++){
		var idCheckBox=ids[i];
		if(idCheckBox.checked){
			id=id+idCheckBox.value+',';
			periodicalStyle = idCheckBox.getAttribute("periodicalStyle");
			count++;
		}
	}
	
	if(count == 0){
		alert(v3x.getMessage("bulletin.select_delete_record"));
		return false;
	} else if(count == 1) {
		if(periodicalStyle==0){
			if(confirm(v3x.getMessage("bulletin.confirm_delete")))
				parent.window.location.href=baseUrl + 'delete' +'&id='+id;
		} else {
			id = id.substring(0,id.length-1);
			var url=baseUrl+"showConfirm&id=" + id + "&flag=delete";
			var result = v3x.openWindow({
				url : url,
				width : 250,
				height: 160,
				dialogType: "modal",
	            resizable: true
			});
			if(result!=null){
				parent.window.location.href=baseUrl + 'delete' +'&id='+id+'&confirm='+result;
			}		
		}
	} else {
		if(confirm(v3x.getMessage("bulletin.confirm_delete")))
			parent.window.location.href=baseUrl + 'delete' +'&id='+id;
	}
}

function editCalendar(id,onlyLook,periodicaiId,beginDate){
	openTheWindow(baseUrl + 'editIframe'+'&id=' + id +(onlyLook==undefined?'':'&onlyLook='+onlyLook) +'&periodicalId='+(periodicaiId?periodicaiId:'')+'&beginDate='+beginDate+'&random=' + new Date().getTime());
}

function viewCalendar(id,periodicaiId,beginDate){
	var dlgArgs=new Array();		
	dlgArgs['html']=false;	
	dlgArgs['width']=530;
	dlgArgs['height']=580;
	if(!periodicaiId){
		periodicaiId='';
	}
	dlgArgs['url']=baseUrl+"editIframe&id="+id+ '&periodicalId='+periodicaiId+'&beginDate='+beginDate+'&random=' + new Date().getTime();
	var result=v3x.openWindow(dlgArgs);
	if(result==1)
		window.location.reload();
}

function planEvent(type){
	var ids=getSelectIds();
	if(ids==''){
		alert(v3x.getMessage("bulletin.please_select_record"));
		return;
	}
	var dlgArgs=new Array();		
	dlgArgs['html']=false;	
	dlgArgs['width']=608;
	dlgArgs['height']=410;
	dlgArgs['url']=baseUrl+"planEvent&id="+ids+"&type="+type;
	var result=v3x.openWindow(dlgArgs);
	if(result==1)
		window.location.reload();
}

function cancelEvent(type){
	var ids=getSelectIds();
	if(ids==''){
		alert(v3x.getMessage("bulletin.please_select_record"));
		return;
	}
	
	var dlgArgs=new Array();		
	dlgArgs['html']=false;	
	dlgArgs['width']=400;
	dlgArgs['height']=320;
	dlgArgs['dialogType']="modal";
	dlgArgs['resizable']=true;
	dlgArgs['url']=baseUrl+"cancelEvent"+"&id="+ids+"&type="+type + '&random=' + new Date().getTime();
	var result=v3x.openWindow(dlgArgs);
	if(result==1)
		window.location.reload();
}
var calender_win ;
function cancelEventDiv(type){
	var ids=getSelectIds();
	if(ids==''){
		alert(v3x.getMessage("bulletin.please_select_record"));
		return;
	}
	
    calender_win = new MxtWindow({
        id: 'calender_win',
        title: v3x.getMessage("CalLang.set_event"),
        url: baseUrl+"cancelEvent"+"&id="+ids+"&type="+type + '&random=' + new Date().getTime(),
        width: 400,
        height: 320,
		type:'window',//类型window和panel为panel的时候title不显示
		isDrag:false,//是否允许拖动
        buttons: [{
			id:'btn1',
            text:v3x.getMessage("CalLang.ok"),
            handler: function(){
        		var result = calender_win.getReturnValue();
        	}
        }, {
			id:'btn2',
            text: v3x.getMessage("CalLang.cancel"),
            handler: function(){
        		calender_win.close();
            }
        }]
    
    });
}
function eventToCol(){
	var ids=getSelectId();
		if(ids==''){
			alert(v3x.getMessage("bulletin.please_select_record"));
			return;
		}
		parent.parent.location.href=baseUrl+'eventToCol'+'&id='+ids;
	}
	
function selectPeople(elemId,idElem,nameElem,secretLevel){
		if(!test){
			eval('flowSecretLevel_'+ elemId +' = '+secretLevel)
			eval('selectPeopleFun_'+elemId+'()');
		}
		else{
			var dlgArgs=new Array();		
			dlgArgs['width']=238;
			dlgArgs['height']=310;
			dlgArgs['url']='/seeyon/selectPeople.jsp';
			var elements=v3x.openWindow(dlgArgs);
			if(elements!=null && elements.length>0)
				setCalPeopleFields(elements,idElem,nameElem);
		}
	}

function setCalPeopleFields(elements,idElem,nameElem){
		if(idElem=='managerUserIds' || idElem=='auditUser' || idElem.indexOf('writeUserIds_')>-1
				|| idElem=='publishDepartmentId' || idElem=='conferees' || idElem=='recorderId' || idElem=='emceeId'
			){
			$(idElem).value=getIdsString(elements,false);
		}else{
			$(idElem).value=getIdsString(elements,true);
		}
		
		$(nameElem).value=getNamesString(elements);
	}
	
function dealSP(ele){
    if(!ele)
    return;
    $('tranMemberIds2').value = "";
    $('shareTarget2').value = "";
    $('tranMemberIds').value = "";
    $('shareTarget').value = "";
    checkDefSubject($('shareTarget2'), true);
    checkDefSubject($('shareTarget'), true);
    
}
function setMenuState(menu_id)
{
  var menuDiv=document.getElementById(menu_id);
  if(menuDiv!=null)
  {
    menuDiv.className='webfx-menu--button-sel';
    menuDiv.onmouseover="";
    menuDiv.onmouseout="";
  }
}

var calender_new_win;
function openTheWindow(url){
	if(v3x.getBrowserFlag('OpenDivWindow')){
		var result = v3x.openWindow({
			url : url,
			width : 530,
			height: 480,
			dialogType: "modal",
            resizable: true
			});
		
		if(result=='true'){
			window.location.reload();
		}
	}else{
		calender_new_win = new MxtWindow({
	        id: 'calender_win',
	        title: v3x.getMessage("CalLang.create"),
	        url: url,
	        width: 520,
	        height: 500,
			type:'window',//类型window和panel为panel的时候title不显示
			isDrag:false,//是否允许拖动
	        buttons: [{
				id:'btn1_calender_new',
	            text: v3x.getMessage("CalLang.ok"),
	            handler: function(){
	        		var result = calender_new_win.getReturnValue();
	        	}
	        }, {
				id:'btn2_calender_new',
	            text: v3x.getMessage("CalLang.cancel"),
	            handler: function(){
	        		calender_new_win.close();
	            }
	        }]
	    });
	}
}