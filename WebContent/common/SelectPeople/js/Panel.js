/*----------------------------------------------------------------------------\
|                                Cross Panel 1.0                              |
|-----------------------------------------------------------------------------|
|                       Created by Tanmf (tanmf@seeyon.com)                   |
|                    For UFIDA-Seeyon (http://www.seeyon.com/)                |
|-----------------------------------------------------------------------------|
| A utility will be used for selected people(Member,Department,Team,Post,     |
| Levle,Organization,Special rules etc.).                                     |
|-----------------------------------------------------------------------------|
|                            Copyright (c) 2006 Tanmf                         |
|-----------------------------------------------------------------------------|
| Dependencies:                                                               |
|-----------------------------------------------------------------------------|
| 2006-05-27 | Original Version Posted.                                       |
| 2006-06-06 | Added expanding the current user's department or the given     |
|            | department trees when initiate department-tree.                |
| 2006-06-08 | Extends single select-people mode.                             |
| 2006-06-08 | Support to other Web-Browsers, e.g: Firefox,Opera etc,         |
|            | according as W3C Standard.                                     |
| 2006-09-29 | 支持显示所有面板以及选择所有类型的参数                                |
|-----------------------------------------------------------------------------|
| 主窗口可配置的参数：                                                            |
| 1. elements_${id}               Element[]    原有数据，默认为null              |
| 2. showOriginalElement_${id}    true|false   是否回显原有数据，默认为true       |
| 3. hiddenSaveAsTeam_${id}       true|false   是否隐藏“另存为组”，默认为false    |
| 4. hiddenMultipleRadio_${id}    true|false   是否隐藏“多层”按钮，默认为false    |
| 5. excludeElements_${id}        Element[]    不在被选框中显示  默认为null       |
| 6. isNeedCheckLevelScope_${id}  true|false   是否进行职务级别范围验证  默认true  |
| 7. onlyLoginAccount_${id}       true|false   是否只显示登录单位  默认false      |
| 8. showAccountShortname_${id}   yes|no|auto  是否只一直显示登录简称  默认auto    |
| 9. showConcurrentMember_${id}   true|false   是否现实兼职人员（只外单位）  true  |
|10. hiddenPostOfDepartment_${id} true|false   是否隐藏部门下的岗位 默认false     |
|11. hiddenRoleOfDepartment_${id} true|false   是否隐藏部门下的角色 默认false     |
|12. onlyCurrentDepartment_${id}  true|false   是否仅显示当前部门 默认false       |
|13. showDeptPanelOfOutworker_${id} true|false 当是外部人员时，显示部门面板 默认false|
|14. unallowedSelectEmptyGroup_${id} true|false不允许选择空的组、部门, 默认false   |
|15. showTeamType_${id}            "1,2,3"     需要显示的组类型1-个人,2-系统,3-项目, 默认null，表示所有|
|16. hiddenOtherMemberOfTeam_${id} true|false  是否隐藏组下的外单人员，默认false   |
|17. hiddenAccountIds_${id}        "1,2,3"     隐藏的单位，将不在单位下拉中出现     |
|18. isCanSelectGroupAccount_${id} true|false  是否可以选择集团单位，默认true      |
|19. showAllOuterDepartment_${id}  true|false  是否显示所有的外部部门，默认false   |
|20. hiddenRootAccount_${id}       true|false   是否隐藏集团单位，默认false       |
|21. hiddenGroupLevel_${id}        true|false    是否隐藏集团职务级别，默认false   |
|22. showDepartmentsOfTree_${id}   "部门Id,"    部门树上可以显示的部门             |
|23. showFixedRole_${id}           true|false  显示固定角色，默认false           |
|24. hiddenAddExternalAccount_${id} true|false  显示增加外部单位连接，默认false           |
|25. showDepartmentMember4Search_${id} true|false  部门查询可用时，是否显示部门下面的成员，默认false，不显示           |
|26. isAllowContainsChildDept_${id} true|false 在部门面板选择部门时，是否允许同时选择父部门和子部门，默认为false，不允许|
|27. isConfirmExcludeSubDepartment_${id} true|false 选择部门时，是否提示“是否包含子部门”，默认false即包含子部门|
|28. flowSecretLevel_${id}   1|2|3  流程的涉密等级 
|-----------------------------------------------------------------------------|
| 关键方法：                                                                    |
| 1. getSelectedPeoples() 点击确认按钮，返回数据                                  |
| 2. searchItems() 搜索                                                        |
| 3. preReturnValueFun_${Element[]} [false, message] 点击"确定"按钮前的回调函数   |
|-----------------------------------------------------------------------------|
| Created 2006-05-27 | All changes are in the log above. | Updated 2010-04-28 |
\----------------------------------------------------------------------------*/

var select2_tag_prefix_fullWin = '<select id="memberDataBody" ondblclick="selectOneMember(this)" onchange="listenermemberDataBody(this)" multiple="multiple" style="width:100%;height:100%">';
var select2_tag_prefix = '<select id="memberDataBody" ondblclick="selectOneMember(this)" onchange="listenermemberDataBody(this)" multiple="multiple" style="width:251px;" size="13">';
var select2_tag_subfix = "</select>";
//div展示人员select
var memberDataBody_div = '<div class="div-select" id="memberDataBody" onselectstart="return false">';
//组div展示
var teamMemberDataBody_div = '<div class="div-select" id="memberDataBody" onselectstart="return false">';
//div后缀
var memberDataBody_div_end = '</div>';
var temp_Div = null;

//~ 固定角色，诸如：AccountManager AccountAdmin account_exchange account_edoccreate FormAdmin HrAdmin ProjectBuild DepManager DepAdmin department_exchange
var FixedRoles = new ArrayList();
{
	FixedRoles.add("DepManager");
}

var selectedPeopleElements = new Properties();

//~ 选人的标示
var spId = null;

//~ 最大选择数， -1表示没有限制
var maxSize = -1;

//~ 最小选择数， -1表示没有限制
var minSize = -1;

//~ List1当前选择的对象
var nowSelectedList1Item = null;

//~ 区域1的开关状态
var area1Status = false;
var area2Status = false;

//~ Tree模型
var tree = null;

//~ 是否显示到子部门，当参数departmentId不为空时
var treeInMyDepart = false;

//~ 当前被选择的内容项
var tempNowSelected = new ArrayList();

//~ 当前显示的Panel
var tempNowPanel = null;

//~ 当前显示的单位
var currentAccount = null;

//~ 当前单位的职务级别范围
var currentAccountLevelScope = -1;

//~ 当前显示的单位Id
var currentAccountId = null;;

//~ 当前登录者的Id
var currentMemberId = null;

//~ 当前登录者
var currentMember = null;

//~ 当前登录者的职务级别数
var currentMemberLevelSortId = null;

//~ 是否显示单位简称
var showAccountShortname = false;

//~ 是否现实兼职人员（只外单位）
var showConcurrentMember = true;

var onlyCurrentDepartment = false;

//~ 是否只显示当前登录部门的人员
var onlyLoginAccount = false;

//~ 是否显示所有的外部部门，默认false(按照自己的访问权限来)
var showAllOuterDepartmentFlag = false;

//~ 需要显示的组的类型：1-个人,2-系统,3-项目，默认null，表示所有
var showTeamType = null;

var selectableTableRows = null;

var Constants_ShowMode_TREE = "TREE";//数据对象中必须有parentId字段
var Constants_ShowMode_LIST = "LIST";
var Constants_Left_height = 340;

/**
 * list1的大小
 */
var Constants_List1_size = {
	showMember : 10, //显示人员
	noShowMember : 28 //不显示人员
}

//~ 名字最多显示的字节数
var nameMaxLength = {
	two   : [18], //2列
	three : [14, 12]  //3列
};

//~ 名字与后面的补充信息之间的空格数
var nameMaxSpace = 2;

var NameSpace = {
	 0 : "",
	 1 : " ",
	 2 : "  ",
	 3 : "   ",
	 4 : "    ",
	 5 : "     ",
	 6 : "      ",
	 7 : "       ",
	 8 : "        ",
	 9 : "         ",
	10 : "          ",
	11 : "           ",
	12 : "            ",
	13 : "             ",
	14 : "              ",
	15 : "               ",
	16 : "                ",
	17 : "                 ",
	18 : "                  ",
	19 : "                   ",
	20 : "                    ",
	21 : "                     "
}


//~ 连接多种实体的显示
var arrayJoinSep = "-";

//~ 连接多种实体的id
var valuesJoinSep = "_";

//~ 不在选人被选区域显示的数据 type + id
var excludeElements = new ArrayList();

//~ 是否需要检测人员密级，默认true
var isNeedCheckSecretScope = true;

//~ 是否需要检测职务级别范围，默认true
var isNeedCheckLevelScope = true;

//~ 集团的职务级别 : key-level.id  value-index
var groupLevels = {};

/*****************
 * 一下对象将在页面中重新赋值
 */
var panels = new ArrayList(); //当前需要显示的面板

var selectTypes = new ArrayList(); //当前可以选择的类型

var ShowMe       = true;
var SelectType   = "";
var Panels       = "";

var accountId    = "";
var memberId     = "";
var departmentId = "";
var postId       = "";
var levelId      = "";

/**
 * 面板对象,面板的显示名称 从Constants_Component中取
 * 
 * @param type 面板类型 Constants_Department...
 * @param showMode 显示方式  tree || list
 * @param isShowMember 时候显示人员，如特殊角色不能显示人员
 * @param getMembersFun 获取其下面人员Member的方法，如 Department.prototype.getDirectMembers，只写getDirectMembers
 * @param getMembersFun 获取其直接子节点方法，只对true有效，如 Department.prototype.getDirectMembers，只写getDirectMembers
 * @param disabledAccountSelector 是否屏蔽单位切换按钮
 * @param hiddenOnChanageAccount 切换单位后是否隐藏面板
 * @param searchArea 搜索区域，0-不搜索；1-Area1；2-Area2；12-Area1和Area2，默认0
 * @author tanmf 
 */
function Panel(type, showMode, isShowMember, getMembersFun, getChildrenFun, disabledAccountSelector, hiddenOnChanageAccount, showQueryInputFun, searchArea, hiddenWhenRootAccount){
	this.type = type; //M/O/D/T/P/L/....
	this.showMode = showMode;
	this.isShowMember = isShowMember;
	this.getMembersFun = getMembersFun;
	this.getChildrenFun = getChildrenFun;
	this.disabledAccountSelector = disabledAccountSelector;
	this.hiddenOnChanageAccount = hiddenOnChanageAccount;
	this.showQueryInputFun = showQueryInputFun;
	this.searchArea = searchArea;
	this.hiddenWhenRootAccount = hiddenWhenRootAccount;
}
Panel.prototype.toString = function(){
	return this.type + "\t" + this.name + "\t" + this.showMode;
}

//~ 系统提供的面板，面板名称不允许用“All”
var Constants_FormField      = "FormField";
var Constants_Panels = new Properties();
Constants_Panels.put(Constants_Account, new Panel(Constants_Account, Constants_ShowMode_TREE, false, null, null, false, false, null, 0, false));
Constants_Panels.put(Constants_Department, new Panel(Constants_Department, Constants_ShowMode_TREE, true, "getDirectMembers", "getDirectChildren", false, false, "showQueryInputOfDepart", 3, true));
Constants_Panels.put(Constants_Team, new Panel(Constants_Team, Constants_ShowMode_LIST, true, null, null, false, false, "showQueryInput", 1, true));
Constants_Panels.put(Constants_Post, new Panel(Constants_Post, Constants_ShowMode_LIST, true, "getMembers", null, false, false, "showQueryInput", 1, false));
Constants_Panels.put(Constants_Level, new Panel(Constants_Level, Constants_ShowMode_LIST, false, "getMembers", null, false, false, "showQueryInput", 1, false));
Constants_Panels.put(Constants_Role, new Panel(Constants_Role, Constants_ShowMode_LIST, false, null, null, true, true, "showQueryInput", 1, true));
Constants_Panels.put(Constants_Outworker, new Panel(Constants_Outworker, Constants_ShowMode_LIST, true, "getDirectMembers", null, false, true, "showQueryInputOfDepartOrTerm", 2, true));
Constants_Panels.put(Constants_ExchangeAccount, new Panel(Constants_ExchangeAccount, Constants_ShowMode_LIST, false, null, null, true, true, "showQueryInput", 1, true));
Constants_Panels.put(Constants_OrgTeam, new Panel(Constants_OrgTeam, Constants_ShowMode_LIST, false, null, null, true, true, "showQueryInput", 1, true));
Constants_Panels.put(Constants_RelatePeople, new Panel(Constants_RelatePeople, Constants_ShowMode_LIST, true, "getMembers", null, true, true, "showQueryInput", 1, true));
Constants_Panels.put(Constants_FormField, new Panel(Constants_FormField, Constants_ShowMode_LIST, false, null, null, true, true, "showQueryInput", 1, true));
Constants_Panels.put(Constants_Admin, new Panel(Constants_Admin, Constants_ShowMode_LIST, false, null, null, true, true, "showQueryInput", 1, true));

/**
 * 按照面板对象的某个属性查找面板
 * 
 * @return 面板的Id
 */
function findPanelsByProperty(property, value){
	var result = new ArrayList();
	
	var ps = Constants_Panels.values();
	for(var i = 0; i < ps.size(); i++) {
		var p = ps.get(i);
		if(p[property] == value){
			result.add(p.type);
		}
	}
	
	return result;
}

//中间分隔区域
var Constants_separatorDIV = new ArrayList();
Constants_separatorDIV.add(Constants_Department);
Constants_separatorDIV.add(Constants_Team);
Constants_separatorDIV.add(Constants_Post);

/**
 * 单位
 * @param id
 * @param name
 * @param levelScope
 * @param description
 */
function Account(id, parentId, name, hasChild, shortname, levelScope, description){
	this.id = id;
	this.parentId = parentId;
	this.name = name;
	this.hasChild = hasChild;
	this.shortname = shortname;
	this.levelScope = levelScope;
	this.description = description;
}

Account.prototype.toString = function(){
	return this.id + "\t" + this.name + "\t" + this.shortname + "\t" + this.levelScope;
}

//所有的单位
var allAccounts = new Properties();
//我能访问的单位
var accessableAccounts = new Properties();
var rootAccount = new Account();

//~ 我的能访问的单位 [id, superior]
var accessableAccountIds = new ArrayList();

/*************************************************************************************************
 * 页面初始化
 */
window.onload = function(){
	var startTime = new Date().getTime();
	
	if(allAccounts.isEmpty()){ //没有单位
		var msg = topWindow.v3x.getMessage("V3XLang.selectPeople_alertNoAccount");
		document.getElementById("uploadingDiv").innerHTML = "<font color='red'>" + msg + "</font>";
		document.getElementById("processTR").style.display = "none";
		return;
	}
	
	if(getParentWindowData("hiddenSaveAsTeam") == true || !checkCanSelectMember() 
		|| isAdministrator == true || groupAdmin == true || systemAdmin == true){
		var saveAsTeamDiv = document.getElementById("saveAsTeamDiv");
		if(saveAsTeamDiv){
			saveAsTeamDiv.style.display = "none";
		}
	}
	
	if(isAdministrator == true || groupAdmin == true || systemAdmin == true){ //管理员默认不限制
		isNeedCheckLevelScope = false;
	}
	else{
		if(getParentWindowData("isNeedCheckLevelScope") == false){
			isNeedCheckLevelScope = false;
		}
		else if(!checkCanSelectMember()){ //不能选人就进行验证了
			isNeedCheckLevelScope = false;
		}
	}
		
	//是否需要检查人员密级
	if(getParentWindowData("isNeedCheckSecretScope") == false){
		isNeedCheckSecretScope = false;
	}
	
	if(getParentWindowData("hiddenGroupLevel")){
		Constants_Panels.get(Constants_Level).hiddenWhenRootAccount = true;
	}
	
	onlyLoginAccount = getParentWindowData("onlyLoginAccount") || false;
	showAllOuterDepartmentFlag = getParentWindowData("showAllOuterDepartment") || false;
	
	accountId    = getParentWindowData("accountId") || accountId;
	memberId     = getParentWindowData("memberId") || memberId;
	departmentId = getParentWindowData("departmentId") || departmentId;
	postId       = getParentWindowData("postId") || postId;
	levelId      = getParentWindowData("levelId") || levelId;
	
	currentAccountId = accountId;
		
	showConcurrentMember = getParentWindowData("showConcurrentMember");
	if(showConcurrentMember == null){
		showConcurrentMember = true;
	}
	
	onlyCurrentDepartment = getParentWindowData("onlyCurrentDepartment") || onlyCurrentDepartment;
		
	/**
	 * 外部人员：
	 * 1、屏蔽部门面板
	 * 2、屏蔽单位切换按钮
	 */
	if(isInternal == false && getParentWindowData("showDeptPanelOfOutworker") != true){
		panels.remove(Constants_Account);
		panels.remove(Constants_Post);
		panels.remove(Constants_Level);
		panels.remove(Constants_Role);
		disabledChanageAccountSelector();
	}
	
	//管理员去掉管理人员页签
	if(isAdministrator || groupAdmin || systemAdmin){
		panels.remove(Constants_RelatePeople);
	}
	
	//初始化单位选择器
	initChanageAccountTd();
	
	currentAccount = allAccounts.get(currentAccountId);
	if(currentAccount == null){ //如果是null，取第一个单位
		currentAccount = allAccounts.values().get(0);
		currentAccountId = currentAccount.id;
	}
	
	if(document.getElementById("currentAccountId"))
		document.getElementById("currentAccountId").value = currentAccount.name;

	//加载操作者主单位数据
	if(allAccounts.get(myAccountId)){
		topWindow.initOrgModel(myAccountId, currentMemberId);
	}
	
	//需要显示组的类型
	var showTeamTypeStr = getParentWindowData("showTeamType");
	if(showTeamTypeStr){
		showTeamType = new ArrayList();
		showTeamType.addAll(showTeamTypeStr.split(","));
	}
	
	if(myAccountId != currentAccountId){
		//加载当前单位数据，往往出现在兼职单位切换的时候
		topWindow.initOrgModel(currentAccountId, currentMemberId);
	}
	
	//加载不显示的数据
	initExcludeElements();
	
	try{
		currentMember = topWindow.getObject(Constants_Member, currentMemberId, myAccountId);
	}
	catch(e){
	}
	
	//外部人员，给一个最低职务界别
	if(currentMember && !currentMember.isInternal){
		var lastLevel = topWindow.getDataCenter(Constants_Level).getLast();
		if(lastLevel){
			currentMember.levelId = lastLevel.id;
		}
	}
	
	mappingLevelSortId();
		
	document.getElementById('procDiv').style.display = "none";
	document.getElementById('selectPeopleTable').style.display = "";
	
	//显示面板
	try {
		initAllPanel(currentAccountId);
	}
	catch (e) {
		/*if(e && e.number == 1){
			var chanageAccountSelectorObj = document.getElementById("chanageAccountSelector");
			if(chanageAccountSelectorObj){
				var nextAccountId = chanageAccountSelectorObj.options[chanageAccountSelectorObj.selectedIndex + 1];
				if(nextAccountId){
					chanageAccount(nextAccountId.value);
					selectChangAccount(nextAccountId.value);
				}
			}
		}*/
	}
	
	//回显原有数据
	initOriginalData();
	
	log.debug("初始化数据耗时：" + (new Date().getTime() - startTime) + "MS");
}

/*function selectChangAccount(_accountId){
	var chanageAccountSelectorObj = document.getElementById("chanageAccountSelector");
	if(chanageAccountSelectorObj){
		for(var i = 0; i < chanageAccountSelectorObj.options.length; i++) {
			var o = chanageAccountSelectorObj.options[i];
			if(o.value == _accountId){
				chanageAccountSelectorObj.selectedIndex = i;
				break;
			}
		}
	}
}*/

/**
 * 切换单位
 */
function chanageAccount(newAccountId){
	try {
		initAllPanel(newAccountId);
	}
	catch (e) {
		//selectChangAccount(currentAccountId);
		return;
	}
	
	var showAccountShortnameTemp = getParentWindowData("showAccountShortname");
	if(showAccountShortnameTemp == "yes"){
		showAccountShortname = true;
	}
	else if(showAccountShortnameTemp == "no"){
		showAccountShortname = false;
	}
	else{
		showAccountShortname = true;
	}
	
	currentAccountId = newAccountId;
	
	if(topWindow.initOrgModel(currentAccountId, currentMemberId) == false){ //加载数据
		getA8Top().close();
		return;
	}
	
	if(tempNowPanel){
		selPanel(tempNowPanel.type); //显示当前面板
	}
	
	mergeAccountShortnameOfSelected();
	currentAccount = allAccounts.get(currentAccountId);
	
	mappingLevelSortId();
}

/**
 * 需要检测工作范围显示时：切换单位，把当前登录者的职务级别换算成当前显示单位的职务级别
 */
function mappingLevelSortId(){
	if(isNeedCheckLevelScope && currentMember){
		if(currentAccountId != myAccountId){
			var concurentM = topWindow.getObject(Constants_concurentMembers, currentMember.id);
			if(concurentM != null){ //我在当前单位兼职
				if(concurentM.getLevel()){
					currentMemberLevelSortId = concurentM.getLevel().sortId;
				}
				else{
					currentMemberLevelSortId = null;
				}
				
				return;
			}
			
			var levelIdOfGroup = currentMember.getLevel() ? currentMember.getLevel().groupLevelId : "-1"; //当前登录者对应集团的职务级别id
			var level = null;
			
			if(levelIdOfGroup && levelIdOfGroup != "0" && levelIdOfGroup != "-1"){ //我的职务级别没有映射到集团，菜单当前单位的最低职务级别
				var myGroupLevelIndex = groupLevels[levelIdOfGroup]; //我在集团职务级别中index
				
				for(var groupLevelId in groupLevels){
					var index = groupLevels[groupLevelId];
					if(myGroupLevelIndex > index){
						continue;
					}
					
					var _level = topWindow.findByProperty(topWindow.getDataCenter(Constants_Level), "groupLevelId", groupLevelId);
					if(_level){
						level = _level;
						break;
					}
				}
			}
			
			if(!level){
				level = topWindow.getDataCenter(Constants_Level).getLast(); //最低职务级别
			}
			
			if(level){
				currentMemberLevelSortId = level.sortId;
			}
			else{
				currentMemberLevelSortId = null;
			}
		}
		else{
			level = currentMember.getLevel();
			if(level){
				currentMemberLevelSortId = level.sortId;
			}
			else{
				currentMemberLevelSortId = null;
			}			
		}
	}
}

/**
 * 非本单位，隐藏不显示的面板, 集团管理员和系统管理员例外
 */
function getPanels(accountId){
	var _panels = new ArrayList();
	_panels.addList(panels);
	
	if(!systemAdmin && !groupAdmin && loginAccountId != accountId){
		var ps = findPanelsByProperty("hiddenOnChanageAccount", true);
		for(var i = 0; i < ps.size(); i++) {
			
			if(ps.get(i)==Constants_ExchangeAccount){
				var hiddenOnChanageAccountForEA=getParentWindowData("hiddenOnChanageAccountForExchangeAccount");
				if(hiddenOnChanageAccountForEA==null || hiddenOnChanageAccountForEA)
					 _panels.remove(ps.get(i));
			}else if(ps.get(i)==Constants_OrgTeam){
				var hiddenOnChanageAccountForOT=getParentWindowData("hiddenOnChanageAccountForOrgTeam");
				if(hiddenOnChanageAccountForOT==null || hiddenOnChanageAccountForOT)
					 _panels.remove(ps.get(i));
			}else{
				_panels.remove(ps.get(i));
			}
		}
	}
	
	if(accountId == rootAccount.id){
		var ps2 = findPanelsByProperty("hiddenWhenRootAccount", true);
		for(var i = 0; i < ps2.size(); i++) {
			_panels.remove(ps2.get(i));
		}
	}
	
	return (_panels);
}

var isHasShowMergeASN = false;
/**
 * 在已选择的数据前追加单位信息
 */
function mergeAccountShortnameOfSelected(){
	if(!showAccountShortname || isHasShowMergeASN){ //已经有了，不用加
		return;
	}
	
	var selectedOptions = document.getElementById("List3").options;
	if(selectedOptions){
		for(var i = 0; i < selectedOptions.length; i++) {
			var o = selectedOptions.item(i);
			var type = o.getAttribute("type");
			var id = o.getAttribute("id");
			var accountId = o.getAttribute("accountId");
			if(type == Constants_Account){
				continue;
			}
			
			if(currentMember && currentMember.accountId == accountId){
				continue;
			}
			
			if(type == Constants_Team){
				var entity = topWindow.getObject(type, id);
				if(entity && entity.type == 1){ //个人组
					continue;
				}
			}
			
			var accountShortname = o.accountShortname;
			if(accountShortname){
				o.text = o.text + "(" + accountShortname + ")";
			}
		}
	}
	
	isHasShowMergeASN = true;
}

/**
 * 初始化单位选择器
 */
function initChanageAccountTd(){
	/*var chanageAccountSelectorObj = document.getElementById("chanageAccountSelector");
	if(!chanageAccountSelectorObj){
		return;
	}
	var showAccountDivObj = document.getElementById("showAccountValueDiv");
	
	var selectedInd = 0;
	var _index = 0;
	var _hiddenAccountIds = getParentWindowData("hiddenAccountIds") || "";
	var hiddenAccountIds = _hiddenAccountIds.split(",");
	var hiddenRootAccount = getParentWindowData("hiddenRootAccount");
	for(var i = 0; i < accessableAccountIds.size(); i++) {
		var accessableAccount = accessableAccountIds.get(i);
		var _accountId = accessableAccount.id;
		var _superiorAccountId = accessableAccount.superior;
		if(hiddenRootAccount && _accountId == rootAccount.id){
			continue;
		}

		for(var j = 0; j < accessableAccountIds.size(); j++) {
			var accessableAccount1 = accessableAccountIds.get(j);
			var _accountId1 = accessableAccount1.id;
			var _superiorAccountId1 = accessableAccount1.superior;
			
			if(_accountId == _superiorAccountId1){
				if(!accessableAccount.children){
					accessableAccount.children = [];
				}
				
				accessableAccount.children[accessableAccount.children.length] = accessableAccount1;
				accessableAccount1.hasSuperior = true;
			}
		}
	}
	
	for(var i = 0; i < accessableAccountIds.size(); i++) {
		var _account = accessableAccountIds.get(i);
		if(hiddenRootAccount && _account.id == rootAccount.id){
			continue;
		}
		
		if(!_account.hasSuperior){
			draw(_account, 0);
		}
	}
	
	chanageAccountSelectorObj.selectedIndex = selectedInd;
	if(showAccountDivObj){
		showAccountDivObj.innerText = chanageAccountSelectorObj.options[selectedInd].text.trim().getLimitLength(14);
	}
	
	currentAccountId = chanageAccountSelectorObj.value;*/
	
	if(onlyLoginAccount == true){
		disabledChanageAccountSelector();
	}
	
	/*function draw(_account, spaceIndex){
		var _accountId = _account.id;
		var account = allAccounts.get(_accountId);
		if(!account){
			return;
		}
		
		if(hiddenAccountIds.indexOf(_accountId) == -1){
			var text = "";
			for(var i = 0; i < spaceIndex; i++) {
				text += " ";
			}
			
			var option = new Option(text + account.shortname, account.id);
			
			if(account.id == currentAccountId){
				selectedInd = _index;
			}
			
			chanageAccountSelectorObj.options.add(option);
			
		_index += 1;
		}
		
		if(_account.children && _account.children.length > 0){
			for(var i = 0; i < _account.children.length; i++) {
				draw(_account.children[i], spaceIndex + 2);
			}
		}
	}*/
}
/*function clickAccount(selectValue){
	var chanageAccountSelectorObj = document.getElementById("chanageAccountSelector");
	if(chanageAccountSelectorObj){
		var options = chanageAccountSelectorObj.options;
		for(var i =0;i<options.length;i++){
			if(options[i].value == selectValue){
				chanageAccountSelectorObj.selectedIndex = i;
				var showAccountDivObj = document.getElementById("showAccountValueDiv");
				showAccountDivObj.innerText = options[i].text.trim().getLimitLength(14);
				chanageAccount(selectValue);
			}
		}
	}
}*/
/*function showSelectAccount(flag){
	var showAccountDiv = document.getElementById('showAccountDiv');
	if(showAccountDiv){
		var allAccountDiv = document.getElementById('allAccountDiv');
		var dis = showAccountDiv.getAttribute("disable");
		if((!dis || dis=="false") && allAccountDiv && allAccountDiv.style.display!="block" && flag == "show"){
			allAccountDiv.style.display='block';
		}else if(allAccountDiv){
			allAccountDiv.style.display='none'
		}
	}
}*/
// 能够访问其他单位(权限不是全否 || 可访问的单位不超过两个(自己和集团))
function canAccessOtherAccount(){
	return accessableAccounts.size()>2;
}
function disabledChanageAccountSelector(state){
	if(onlyLoginAccount == true){
		state = true;
	}
	else{
		state = state == null ? true : state;
	}
	
	if(isInternal == false && getParentWindowData("showDeptPanelOfOutworker") != true){
		state = true;
	}
	state=chanageStateFromParameter(state);

	var _flag4showSearchGroup = false;//是否显示【查全集团】临时标示
	if(state == true || state == "true"){
		$('#select_input_div').attr("disabled", true);
	}else{
		$('#select_input_div').attr("disabled", false);
		_flag4showSearchGroup = true;
	}
	// 访问访问不是全否 && 当前面板为部门 && 选择到人 && 可以切换单位 = 显示"查全集团"
	var showSearchGroup = canAccessOtherAccount() 
		&& tempNowPanel!=null 
		&& tempNowPanel.type == Constants_Department 
		&& checkCanSelectMember() 
		&& _flag4showSearchGroup;
		/**
		 * Fix AEIGHT-4247&AEIGHT-4214 
		 * 当选人界面只能在本单位内选择某人时，即切换单位置灰时，屏蔽【查全集团】功能
		 * modify by lilong 2012-04-12
		 */
	//不能选择单位则不能在全集团范围内查询人员
	if(!showSearchGroup)$("#seachGroupMember").hide();
	_flag4showSearchGroup = null;
	
	/*var chanageAccountSelectorObj = document.getElementById("chanageAccountSelector");
	if(chanageAccountSelectorObj){
		chanageAccountSelectorObj.disabled = state;
		var showAccountDiv = document.getElementById('showAccountDiv');
		if(showAccountDiv){
			if(state==true || state == "true"){
				showAccountDiv.disabled = true;
				showAccountDiv.setAttribute("disable","true");
			}else{
				showAccountDiv.disabled = false;
				showAccountDiv.setAttribute("disable","false");
			}
		}
	}*/
}
/**
 * 根据前台页面的参数设置来判断时候disable掉单位选择器。
 */
function chanageStateFromParameter(state){
	if(tempNowPanel!=null){
		//从前台获取参数，判断是否disable掉单位选择器。
		if(tempNowPanel.type==Constants_OrgTeam){
			var disableOrNot=getParentWindowData("disabledAccountSelectorForOrgTeam");
			if(disableOrNot!=null) 
				state=disableOrNot;
		}
		if(tempNowPanel.type==Constants_ExchangeAccount){
			var disableOrNot=getParentWindowData("disabledAccountSelectorForExchangeAccount");
			if(disableOrNot!=null)
				state=disableOrNot;
		} 
	}
	return state;
}

/**
 * 检测是否可以选择该类型
 */
function checkCanSelect(type){
	var types = type.split(valuesJoinSep);
	if(types.length == 2){	
		if(types[0] == Constants_Department){
			return selectTypes.contains(types[1]);
		}
	}
	else{
		return selectTypes.contains(type);
	}
	
	return false;
}

/**
 * 检测是否可以选择Member
 */
function checkCanSelectMember(){
	return selectTypes.contains(Constants_Member);
}

var canSelectEmailOrMobileValue = null;
function getCanSelectEmailOrMobile(){
	if(canSelectEmailOrMobileValue == null){
		if(selectTypes.contains("Email")){
			canSelectEmailOrMobileValue = "email";
		}
		else if(selectTypes.contains("Mobile")){
			canSelectEmailOrMobileValue = "mobile";
		}
		else{
			canSelectEmailOrMobileValue = "";
		}
	}
	
	return canSelectEmailOrMobileValue;
}

function checkIsShowArea2(){
	if(isRootAccount()){
		return false;
	}
	
	var type = tempNowPanel.type;
	if(type == Constants_Department && (getParentWindowData("showDepartmentMember4Search"))){
		return true;
	}
	else if(type == Constants_Department && ((checkCanSelect(Constants_Post) && getParentWindowData("hiddenPostOfDepartment") != true) || (checkCanSelect(Constants_Role) && getParentWindowData("hiddenRoleOfDepartment") != true))){
		return true;
	}
	
	return checkCanSelectMember() && tempNowPanel.isShowMember;
}

/**
 * 检测职务级别差
 * 1、同一部门的返回true
 * 2、被检测人职务级别高于当前操作者一个数字，返回false
 * 
 * @member 要访问的人
 * @return true 有权访问, false 无权访问
 */
function checkLevelScope(member, entity){
	if(!isNeedCheckLevelScope 
		|| (currentMember && (currentMember.departmentId == member.departmentId || currentMember.isSecondPostInDept(member.departmentId)))
		|| currentAccount == null 
		|| currentAccountLevelScope < 0){
		return true;
	}
	if(currentMemberLevelSortId == null){
		return false;
	}
	
	//我在这个部门做兼职，我可以访问在这个部门的所有人
	try {
		if(entity){
			var c = entity.getConcurents();
			if(c && c.contains(currentMember, "id")){
				return true;
			}
		}
	}
	catch (e) {
	}
	
	//副岗在这个部门的有权限
	if(member.isSecondPostInDept(currentMember.departmentId)){
		return true;
	}

	var level = member.getLevel();
	
	if(!level){
		level = topWindow.getDataCenter(Constants_Level).getLast(); //最低职务级别
	}
	
	if(currentMemberLevelSortId - level.sortId <= currentAccountLevelScope){
		return true;
	}
	
	return false;
}
/**
 * 检测越级访问，只要部门/组里面有任何一个人不能选择，则该部门/组不能选择
 * @return true 有权访问, false 无权访问
 */
function checkAccessLevelScope(type, id){
	if(!isNeedCheckLevelScope || currentAccountLevelScope < 0){
		return true;
	}
	if(currentMemberLevelSortId == null){
		return false;
	}
	
	var members = null;
	if(type == Constants_Department){
		if(currentMember && (currentMember.departmentId == id || currentMember.isSecondPostInDept(id))){
			return true;
		}
		
		var entity = topWindow.getObject(type, id);
		if(entity && entity.getConcurents().contains(currentMember, "id")){
			return true;
		}
		members = topWindow.getObject(Constants_Department, id).getDirectMembers();
	}
	else if(type == Constants_Team){
		return true;
	}
	else if(type == Constants_Level){
		var entity = topWindow.getObject(type, id);
		return currentMemberLevelSortId - entity.sortId <= currentAccountLevelScope;
	}
	else if(type == Constants_Post){
		var entity = topWindow.getObject(type, id);
		members = entity.getAllMembers();
	}else if( type == Constants_Department + "_" + Constants_Post){
		var ids = id.split("_");
		var types = type.split("_");
		
		var entity = topWindow.getObject(types[0], ids[0]);
		members =  new ArrayList();
		if(entity){
			var ms = entity.getAllMembers();
			if(ms){
				for(var i = 0; i < ms.size(); i++){
					var m = ms.get(i);
					if(m.postId == ids[1]){
						members.add(m);
					}	
				}
			}
		}
	}else if(type == Constants_Account){
		var highLevel = topWindow.getDataCenter(Constants_Level).get(0);//最高职务级别
		if(highLevel){
			return currentMemberLevelSortId - highLevel.sortId <= currentAccountLevelScope;
		}
		return true;
	}
	else{
		return true;
	}
	
	if(members){
		for(var i = 0; i < members.size(); i++) {
			if(!checkLevelScope(members.get(i))){
				return false
			}
		}
	}
	
	return true;
}

/**
 * 如果当前人是外部人员，检测改实体是否可以访问
 * 
 * @param type
 * @param id
 * @return true:可以访问
 */
function checkExternalMemberWorkScope(type, id){
	if(isInternal){//当前人不是外部人员
		return true;		
	}
		
	var _ExternalMemberWorkScope = topWindow.ExternalMemberWorkScope;
	if(_ExternalMemberWorkScope.get(0) == "0"){
		return true;
	}
	
	if(type.startsWith(Constants_Account)){
		return false;
	}
		
	if(!type.startsWith(Constants_Department)){
		return true;
	}
	
	var _type = type.split(valuesJoinSep)[0];
	var _id = id.split(valuesJoinSep)[0];
	
	for(var i = 0; i < _ExternalMemberWorkScope.size(); i++) {
		var wsDepartId = _ExternalMemberWorkScope.get(i); //工作部门的Id 如：D0.1
		if(_type == Constants_Department){
			var d = topWindow.getObject(Constants_Department, _id);
			if(d && (("D" + d.path) == wsDepartId || ("D" + d.path).indexOf(wsDepartId + ".") == 0)){
				return true;
			}
		}
	}
	
	return false;
}

/**
 * 判断外部人员是否可以访问内部具体的人
 */
function checkExternalMemberWorkScopeOfMember(member){
	if(isInternal){//当前人不是外部人员
		return true;		
	}
	
	if(!member.isInternal && member.getDepartment().id == currentMember.departmentId){
		return true;
	}
	
	var _ExternalMemberWorkScope = topWindow.ExternalMemberWorkScope;
	if(_ExternalMemberWorkScope.isEmpty()){
		return false;
	}
	
	if(_ExternalMemberWorkScope.get(0) == "0"){
		return true;
	}
	
	return _ExternalMemberWorkScope.contains("M" + member.id);
}

/**
 * 初始化面板，并把第一个面板作为当前显示的面板
 */
function initAllPanel(_accoutId){
	var _panels = getPanels(_accoutId);
	if(_panels == null || _panels.isEmpty()){
		throw new Error(1);
	}
	
	var tdPanelObj = document.getElementById("tdPanel");
	var length1 = tdPanelObj.cells.length;
	for(var i = 0; i < length1; i++) {
		tdPanelObj.deleteCell(i);
	}
	
	// 将要被显示的面板 <Panel>
	var toShowPanels = new ArrayList();
	
	for(var i = 0; i < _panels.size(); i++){
		var panel = Constants_Panels.get(_panels.get(i));
		if(panel == null){
			log.warn("The Panel's type '" + _panels.get(i) + "' undefined.");
			continue;
		}
		
		toShowPanels.add(panel);
	}
	var CellTd = tdPanelObj.insertCell(-1);
	CellTd.className="tab-tag";
	CellTd.style.height = "26px";
	CellTd.style.verticalAlign="bottom";
	
	var newTd = document.createElement("div");
	newTd.className="div-float";
	
	
	var separatorFirst = document.createElement("div");
	separatorFirst.className="tab-separator";
	newTd.appendChild(separatorFirst);	
	CellTd.appendChild(newTd);
	//将面板对象转化为HTML代码
	for(var i = 0; i < toShowPanels.size(); i++){
		/**var panel = toShowPanels.get(i);			
		var newTd = tdPanelObj.insertCell(-1);
		var title = Constants_Component.get(panel.type);
		newTd.id = "tdPanel_" + panel.type;
		newTd.className = "tdPanelNoSel cursor-hand";
		newTd.innerHTML = title.getLimitLength(8);
		newTd.title = title;
		newTd.onclick  = new Function("selPanel('" + panel.type + "', '" + panel.showMode + "')");*/
		var panel = toShowPanels.get(i);	
		var title = Constants_Component.get(panel.type);
		
		var left = document.createElement("div");
		left.id='left'+panel.type;
		left.className="tab-tag-left";
		
		var middel = document.createElement("div");
		middel.id='middel'+panel.type;
		middel.className="tab-tag-middel";
		middel.onclick  = new Function("selPanel('" + panel.type + "', '" + panel.showMode + "')");
		middel.appendChild(document.createTextNode(title.getLimitLength(8)));
		middel.title = title;
		
		var right = document.createElement("div");
		right.id='right'+panel.type;
		right.className="tab-tag-right";
		
		var separator = document.createElement("div");
		separator.className="tab-separator";
		
		newTd.appendChild(left);	
		newTd.appendChild(middel);	
		newTd.appendChild(right);	
		newTd.appendChild(separator);
		
		
		
	}
	
	//显示第一个面板
	if(!toShowPanels.isEmpty()){
		if(tempNowPanel && toShowPanels.contains(tempNowPanel, "type")){
			selPanel(tempNowPanel.type); //显示当前面板
		}
		else{
			tempNowPanel = null;
			selPanel(toShowPanels.get(0).type);
		}
	}
}
/**
 * 根据面板类型显示面板
 * 
 * @param type 面板类型
 */
function selPanel(type){
	if(tempNowPanel != null){//
		//var obj = document.getElementById("tdPanel_" + tempNowPanel.type);
		//obj.className = "tdPanelNoSel cursor-hand";
		//obj.onclick  = new Function("selPanel('" + tempNowPanel.type + "', '" + tempNowPanel.showMode + "')");
		
		var left = document.getElementById('left'+tempNowPanel.type);
		left.className = 'tab-tag-left';
		var middel = document.getElementById('middel'+tempNowPanel.type);
		middel.className = 'tab-tag-middel';
		middel.onclick  = new Function("selPanel('" + tempNowPanel.type + "', '" + tempNowPanel.showMode + "')");
		var right = document.getElementById('right'+tempNowPanel.type);
		right.className = 'tab-tag-right';
		//showSelectAccount();
	}
	
	//obj = document.getElementById("tdPanel_" + type);	
	//obj.className = "tdPanelSel";
	//obj.onclick  = new Function("");
	
	var left = document.getElementById('left'+type);
	left.className = 'tab-tag-left-sel';
	var middel = document.getElementById('middel'+type);
	middel.className = 'tab-tag-middel-sel';
	middel.onclick  = new Function("");
	var right = document.getElementById('right'+type);
	right.className = 'tab-tag-right-sel';
	//不隐藏外部单位连接时
	if(type=="ExchangeAccount"&&(!getParentWindowData("hiddenAddExternalAccount"))){
		//外部单位时，打开链接对象
		var addExternalAccountObj = document.getElementById("addExternalAccountDiv");
		addExternalAccountObj.style.display="";
	}else{
		var addExternalAccountObj = document.getElementById("addExternalAccountDiv");
		addExternalAccountObj.style.display="none";
	}
	
	tempNowPanel = Constants_Panels.get(type);//
	
	tempNowSelected.clear();
	
	if(area1Status){//
		hiddenArea1();
	}
	
	showList1(type, tempNowPanel.showMode);	//??????
		
	area2Status = checkIsShowArea2();
	
	hiddenArea2(!area2Status);
	
	//部门页签下查询人员时是否支持全集团查询
	if(type == Constants_Department){
		$("#seachGroupMember").show();
		var obj = document.getElementById("seachGroup");
		hideSeparatorDIV(obj);
	}else{
		$("#seachGroupMember").hide();
	}
	
	var ps = findPanelsByProperty("disabledAccountSelector", true);
	disabledChanageAccountSelector(ps.contains(type));
	
	var _showQueryInputFun = tempNowPanel.showQueryInputFun;
	if(_showQueryInputFun){
		var _showQueryInputFunState = eval(_showQueryInputFun + "()");
		if(_showQueryInputFunState == false){
			document.getElementById("q").value = "";
		}
		document.getElementById("q").disabled = (_showQueryInputFunState == false);
	}
	else{
		document.getElementById("q").disabled = true;
	}
	
	//不能查询时则不显示"查全集团"
	if(document.getElementById("q").disabled || !checkCanSelectMember()){
		$("#seachGroupMember").hide();
	}
}

/**
 * ??????
 */
function showList1(type, showMode){
	clearList2();
	
	showSeparatorDIV(type);
	
	document.getElementById("AreaTop1").style.display = "none";
	
	if(showMode == Constants_ShowMode_TREE){//????????????
		enableButton("button1");
		initTree(type);
	}
	else if(showMode == Constants_ShowMode_LIST){//??????????????
		disableButton("button1");
		initList(type);
	}
	else{
		log.warn("The Paramter showMode '" + showMode + "' is undefined.")
	}
	
//	showMemberTitle(type);
}

var currentArea2Type = Constants_Member;

/**
 * 显示部门下的内容：人/岗位/角色
 */
function showList2OfDep(area2Type, keyword){
	//显示人员时才支持在全集团范围内查询
	if(area2Type == "Member"){
		if(canAccessOtherAccount())
		$("#seachGroupMember").show();
	}else{
		$("#seachGroupMember").hide();
	}
	
	if(!tree.getSelected()){
		return;
	}
	
	var departmentId = tree.getSelected().id;
	showList2(Constants_Department, departmentId, area2Type, keyword);
}

/**
 * 显示区域2的内容
 */
function showList2(type, id, area2Type, keyword){
	clearList2();
	
	if(type == Constants_Department){
		if(!area2Type){
			var _seps = document.getElementsByName("sep");
			for(var i = 0; i < _seps.length; i++) {
				var _sep = _seps[i];
				if(_sep.checked){
					area2Type = _sep.value;
					break;
				}
			}
		}
		
		if(area2Type == Constants_Member){
			showMember(type, id, keyword);
		}
		else{
			if(!checkExternalMemberWorkScope(type, id)){
				return;
			}
		
			showSubOfDepartment(id, area2Type, keyword);
		}
	}
	else{
		showMember(type, id, keyword);
	}
	
	if(area2Type){
		currentArea2Type = area2Type;
	}
}

/**
 * 显示/隐藏 部门树和人员列表中见的人/岗位/角色
 */
function showSeparatorDIV(type){
	for(var i = 0; i < Constants_separatorDIV.size(); i++) {
		var d = Constants_separatorDIV.get(i);
		if(d == type){
			continue;
		}
		
		document.getElementById("separatorDIV_" + d).style.display = "none";
	}
	
	//部门面板
	if((checkCanSelect(Constants_Role) || checkCanSelect(Constants_Post)) && type == Constants_Department){
		document.getElementById("separatorDIV_" + type).style.display = "";
		var selectedIndex = -1;
		if(checkCanSelectMember()){
			document.getElementById("sep_per_l").style.display = "";
			selectedIndex = 0;
		}
		
		if(checkCanSelect(Constants_Post) && getParentWindowData("hiddenPostOfDepartment") != true && isInternal){
			document.getElementById("sep_post_l").style.display = "";
			if(selectedIndex == -1){
				selectedIndex = 1;
			}
		}
		
		if(checkCanSelect(Constants_Role) && getParentWindowData("hiddenRoleOfDepartment") != true && isInternal){
			document.getElementById("sep_role_l").style.display = "";
			if(selectedIndex == -1){
				selectedIndex = 2;
			}
		}
		
		if(selectedIndex != -1){
			document.getElementsByName("sep")[selectedIndex].checked = true;
		}
	}
	else if(type == Constants_Team && checkCanSelect(Constants_Member)){ //组面板
		document.getElementById("separatorDIV_" + type).style.display = "";
	}
	else if(type == Constants_Post && checkCanSelect(Constants_Post)){ //岗位组面板
		document.getElementById("separatorDIV_" + type).style.display = "";
	}
}

/**
 * 选中"查全集团"时, 不显示岗位、角色; 反之显示.
 */
function hideSeparatorDIV(obj){
	if(obj && obj.checked){
		if(!$("#sep_post_l").is(":hidden")){
			$("#sep_post_l").hide();
		}
		if(!$("#sep_role_l").is(":hidden")){
			$("#sep_role_l").hide();
		}
	}else{
		showSeparatorDIV(Constants_Department);
	}
}

/**
 * 在Area2显示部门下的岗位
 */
function showSubOfDepartment(departmentId, subType, keyword){	
	var department = topWindow.getObject(Constants_Department, departmentId);
	if(!department){
		return;
	}
	
	var entites = eval("department.get" + subType + "s()");
	var selectHTML = new StringBuffer();
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		selectHTML.append(memberDataBody_div);
	}else{
		selectHTML.append(select2_tag_prefix);
	}
	
	if(entites){
		for(var i = 0; i < entites.size(); i++) {
			var entity = entites.get(i);
			
			if(entity == null){
				continue;
			}
			
			if(keyword && entity.name.toLowerCase().indexOf(keyword) < 0){
				continue;
			}
			
			var _id = departmentId + valuesJoinSep + entity.id;
			var _type = Constants_Department + valuesJoinSep + subType;
			//该数据不显示
			if(excludeElements.contains(_type + _id)){
				continue;
			}
			
			var text = null;
			var showTitle = "";
			if(entity.code){
				text = entity.name.getLimitLength(nameMaxLength.two[0]);
				if(text != entity.name){
					showTitle = entity.name.escapeSpace();
				}
				text += NameSpace[nameMaxLength.two[0] + nameMaxSpace - text.getBytesLength()];
				text += entity.code;
			}
			else{
				text = entity.name;
			}
				
			
			if(!v3x.getBrowserFlag('selectPeopleShowType')){
				selectHTML.append("<div class='member-list-div' seleted='false' ondblclick='selectOneMemberDiv(this)'  onclick=\"selectMemberFn(this,'memberDataBody')\" title=\"" + showTitle.escapeHTML(true) + "\" value='").append(_id).append("' type='").append(_type).append("'>").append(text.escapeHTML(true)).append("</div>");
				
			}else{
				selectHTML.append("<option title=\"" + showTitle.escapeSpace() + "\" value='").append(_id).append("' type='").append(_type).append("'>").append(text.escapeSpace()).append("</option>");
			}
		
		}
	}
	
	selectHTML.append(select2_tag_subfix);
	
	document.getElementById("Area2").innerHTML = selectHTML.toString();
	initIpadScroll("memberDataBody");//ipad滚动条解决
}

/**
 * 显示组的关联人员
 */
function showTeamRelativeMembers(){
	var id = Constants_Team + "DataBody";
	var dataBody = document.getElementById(id);
	if(dataBody){
		var s = dataBody.value;
		if(s){
			tempNowSelected.clear();
			clearList2();
			addTeamMember2List2(s);
			
			selectList1Item(Constants_Team, dataBody);
		}
	}
}

/**
 * ??member???header
 */ 
function showMemberTitle(type){
	var name = "";
	
	if(type == Constants_Department){//
		name = Constants_Component.get(Constants_Post);
	}
	else if(type == Constants_Role){
		
	}
	else{ //
		name = Constants_Component.get(Constants_Department);
	}
	
	document.getElementById("memberTitle2").innerHTML = "&nbsp;" + name;
}

function getFormFieldListHTMLStr(keyword){
	var id = Constants_FormField + "DataBody";
	var datas = topWindow.getDataCenter(Constants_FormField);
	var size = tempNowPanel.isShowMember && checkCanSelectMember() ? Constants_List1_size.showMember : Constants_List1_size.noShowMember;
	
	var html = new StringBuffer();
	html.append("<select id=\"" + id + "\" onchange=\"selectList1Item('" + Constants_FormField + "', this)\" ondblclick=\"selectOne('" + Constants_FormField + "', this)\" multiple style='width:251px' size='" + size  + "'>");
	
	if(datas){
		//var postTypeId = document.getElementById("areaTopList1").value;
		for(var i = 0; i < datas.size(); i++){
			var item = datas.get(i);
			var name = item.name;
			try{
				if(keyword && name.toLowerCase().indexOf(keyword) == -1){
					continue ;
				}
				html.append("<option title=\"" + name.escapeHTML(true) + "\" value=\"").append(name).append("\" type=\"").append(Constants_FormField).append("\">").append(name.escapeHTML(true)).append("</option>");
			}
			catch(e){
				log.error("", e)
			}
		}
	}
	
	html.append("</select>");
	
	return html.toString();
}
/**
 * 
 */
function initList(type, keyword){
//	var startTime = new Date().getTime();
	
	var id = type + "DataBody";
	
	var str = null;
	if(type == Constants_Team){
		str = getTeamListHTMLStr(keyword);
	}
	else if(type == Constants_Post){
		document.getElementById("AreaTop1").style.display = "";
		clearList2();
		str = getPostListHTMLStr(keyword);
	}
	else if(type == Constants_RelatePeople){
		str = getRelatePeopleListHTMLStr(keyword);
	}
	else if(type == Constants_FormField){
		clearList2();
		str = getFormFieldListHTMLStr(keyword);
	}
	else{
		var size = tempNowPanel.isShowMember && checkCanSelectMember() ? Constants_List1_size.showMember : Constants_List1_size.noShowMember;
		var html = new StringBuffer();
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			html.append("<select id=\"" + id + "\" onchange=\"selectList1Item('" + type + "', this)\" ondblclick=\"selectOne('" + type + "', this)\" multiple style='width:251px' size='" + size + "'>");
		}else{
			var classStr = tempNowPanel.isShowMember && checkCanSelectMember() ? 'team-list' : 'relatePeople-list';
			html.append("<div id=\"" + id + "\"  class=\""+classStr+"\">");
		}
		
		var datas = topWindow.getDataCenter(type);
		if(datas){
			var secondPostDepartmentPaths = null;
			if(currentMember){
				secondPostDepartmentPaths = new ArrayList();
				secondPostDepartmentPaths.add(currentMember.getDepartment().path);
				
				var departIds = currentMember.getSecondPost().keys();
				for(var i = 0; i < departIds.size(); i++) {
					var department = topWindow.getObject(Constants_Department, departIds.get(i));
					if(department){
						secondPostDepartmentPaths.add(department.path);
					}
				}
			}
			
			var showFixedRole = getParentWindowData("showFixedRole");
			
			for(var i = 0; i < datas.size(); i++){
				var item = datas.get(i);
				
				if(keyword){
					var text = item.name.toLowerCase();
					if(text.indexOf(keyword) == -1){
						continue;
					}
				}
				
				if(type == Constants_Role){
					if(
						(showFixedRole && item.type == 1 && FixedRoles.contains(item.id)) //显示固定角色
						|| (!showFixedRole && item.type == 2) //显示相对角色
						){ 
					
					}
					else{
						continue;
					}
				}
				
				//该数据不显示
				if(excludeElements.contains(type + item.id)){
					continue;
				}
				
				var text = null;
				var showTitle = "";
				
				if(type == Constants_Outworker){
					//当前登录者是外部人员,只能看到自己的部门
					if(!showAllOuterDepartmentFlag && !topWindow.ExtMemberScopeOfInternal.containsKey(item.id)){
						if(currentMember && !currentMember.isInternal){
							if(!item.isInternal && item.id != currentMember.departmentId){
								continue;
							}
						}
						else{
							//上级是部门
							if(secondPostDepartmentPaths && !secondPostDepartmentPaths.isEmpty() && item.parentDepartment){
								var isShow = false;
								var parentPathOfOuter = item.parentDepartment.path;
								for(var k = 0; k < secondPostDepartmentPaths.size(); k++) {
									var p = secondPostDepartmentPaths.get(k)
									if(p.startsWith(parentPathOfOuter)){
										isShow = true;
										break;										
									}
								}
								
								if(!isShow){
									continue;
								}
							}
						}
					}
					
					var ts = getOutworkerListOptionText(item);
					text = ts[0];
					showTitle = ts[1];
				}
				else if(item.code){
					text = item.name.getLimitLength(nameMaxLength.two[0]);
					if(text != item.name){
						showTitle = item.name.escapeSpace();
					}
					text += NameSpace[nameMaxLength.two[0] + nameMaxSpace - text.getBytesLength()];
					text += item.code;
				}
				else{
					text = item.name;
				}
				if(v3x.getBrowserFlag('selectPeopleShowType')){
					html.append("<option title=\"" + text.escapeHTML(true) + "\" value=\"").append(item.id).append("\" type=\"").append(type).append("\">").append(text.escapeHTML(true)).append("</option>");
				}else{
					html.append("<div class='member-list-div' seleted='false' ondblclick=\"selectOne('" + type + "',this,'"+id+"')\"  onclick=\"selectList1ItemDiv('"+type+"','"+id+"',this)\"  title=\"" + showTitle.escapeHTML(true) + "\" value=\"").append(item.id).append("\" type=\"").append(type).append("\">").append(text.escapeHTML(true)).append("</div>");
				}
			}
		}
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			html.append("</select>");
		}else{
			html.append("</div>");
		}
		str = html.toString();
	}

	document.getElementById("Area1").innerHTML = str;
	document.getElementById("Area1").className = "";
	initIpadScroll(id);//ipad滚动条解决
	
//	log.debug("显示列表耗时：" + (new Date().getTime() - startTime) + "MS");
}

function getTeamListHTMLStr(keyword){
	var id = Constants_Team + "DataBody";
	var size = tempNowPanel.isShowMember && checkCanSelectMember() ? Constants_List1_size.showMember : Constants_List1_size.noShowMember;
	
	var datas = topWindow.getDataCenter(Constants_Team);
	var html = new StringBuffer();
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("<select id=\"" + id + "\" onchange=\"selectList1Item('" + Constants_Team + "', this)\" ondblclick=\"selectOne('" + Constants_Team + "', this)\" multiple style='width:251px' size='" + size + "'>");
	}else{
		html.append("<div id=\"" + id + "\" class='team-list'>");
	}
	if(datas){
		for(var i = 0; i < datas.size(); i++){
			try{
				var item = datas.get(i);
				
				//排除不需要显示的类型
				if(showTeamType && !showTeamType.contains("" + item.type)){
					continue;
				}
				
				if(keyword){
					var text = item.name.toLowerCase();
					if(text.indexOf(keyword) == -1){
						continue;
					}
				}
				
				//该数据不显示
				if(excludeElements.contains(Constants_Team + item.id)){
					continue;
				}
				
				var typeName = ""; //类型名称：1-个人 2-系统(单位、集团) 3-项目
				
				if(item.type == 1){
					typeName = topWindow.v3x.getMessage("V3XLang.selectPeople_personalTeam");
				}
				else if(item.type == 2){
					var dep = item.getDepartment();
					if(dep){
						typeName = dep.name;
					}
					else{
						var a = allAccounts.get(item.depId);
						if(a){
							typeName = a.name;
						}
					}
					
					if(!typeName){
						typeName = topWindow.v3x.getMessage("V3XLang.selectPeople_accountTeam");
					}
				}
				else if(item.type == 3){
					typeName = topWindow.v3x.getMessage("V3XLang.selectPeople_projectTeam");
				}
				
				var showText = item.name.getLimitLength(nameMaxLength.two[0]);
				showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
				showText += typeName;
				if(v3x.getBrowserFlag('selectPeopleShowType')){
					html.append("<option title=\"" + item.name.escapeHTML(true) + "\" value=\"").append(item.id).append("\" type=\"Team\" accountId=\"").append(item.accountId).append("\">").append(showText.escapeHTML(true)).append("</option>");
				}else{
					html.append("<div  class='member-list-div' seleted='false' ondblclick=\"selectOne('" + Constants_Team + "',this,'"+id+"')\"  onclick=\"selectList1ItemDiv('"+Constants_Team+"','"+id+"',this)\"  title=\"" + item.name.escapeHTML(true) + "\" value=\"").append(item.id).append("\" type=\"Team\" accountId=\"").append(item.accountId).append("\">").append(showText.escapeHTML(true)).append("</div>");
				}
				
			}
			catch(e){
				log.error("", e)
			}
		}
	}
	
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("</select>");
	}else{
		html.append("</div>");
	}

	return html.toString();
}

function getPostListHTMLStr(keyword){
	var id = Constants_Post + "DataBody";
	var size = tempNowPanel.isShowMember && checkCanSelectMember() && !isRootAccount() ? Constants_List1_size.showMember : Constants_List1_size.noShowMember;
	
	var html = new StringBuffer();
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("<select id=\"" + id + "\" onchange=\"selectList1Item('" + Constants_Post + "', this)\" ondblclick=\"selectOne('" + Constants_Post + "', this)\" multiple style='width:251px' size='" + (size - 2) + "'>");
	}else{
		html.append("<div id=\"" + id + "\" class='post-list'>");
	}
	var datas = topWindow.getDataCenter(Constants_Post);
	if(datas){
		var postTypeId = document.getElementById("areaTopList1").value;
		var isShowAllPosts = ("AllPosts" == postTypeId);
		for(var i = 0; i < datas.size(); i++){
			try{
				var item = datas.get(i);
				if(!isShowAllPosts && item.type != postTypeId){
					continue;
				}
				
				if(keyword){
					var text = item.name.toLowerCase();
					if(text.indexOf(keyword) == -1){
						continue;
					}
				}
				
				//该数据不显示
				if(excludeElements.contains(Constants_Post + item.id)){
					continue;
				}
				
				var text = null;
				var showTitle = "";
				var text1 = null ;
				var titleShow = "";
				if(item.code){
					text = item.name.getLimitLength(nameMaxLength.two[0]);
					text1 = item.name;
					if(text != item.name){
						showTitle = item.name.escapeSpace();
					}
					text += NameSpace[nameMaxLength.two[0] + nameMaxSpace - text.getBytesLength()];
					text1 += NameSpace[nameMaxLength.two[0] + nameMaxSpace];
					text += item.code;
					text1 += item.code;
				}
				else{
					text = item.name;
					text1 = item.name ;
				}
				if(v3x.getBrowserFlag('selectPeopleShowType')){
					html.append("<option title=\"" + text1.escapeSpace() + "\" value=\"").append(item.id).append("\" type=\"").append(Constants_Post).append("\">").append(text.escapeSpace()).append("</option>");
				}else{
					html.append("<div class='member-list-div' seleted='false' ondblclick=\"selectOne('" + Constants_Post + "',this,'"+id+"')\"  onclick=\"selectList1ItemDiv('"+Constants_Post+"','"+id+"',this)\"  title=\"" + showTitle.escapeHTML(true) + "\" value=\"").append(item.id).append("\" type=\"").append(Constants_Post).append("\">").append(text.escapeHTML(true)).append("</div>");
				}
			}
			catch(e){
				log.error("", e)
			}
		}
	}
	
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("</select>");
	}else{
		html.append("</div>");
	}
	
	return html.toString();
}

function getRelatePeopleListHTMLStr(keyword){
	var id = Constants_RelatePeople + "DataBody";
	var size = Constants_List1_size.showMember;
	
	var html = new StringBuffer();
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("<select id=\"" + id + "\" onchange=\"selectList1Item('" + Constants_RelatePeople + "', this)\" ondblclick=\"selectOne('" + Constants_RelatePeople + "', this)\" multiple style='width:251px' size='" + size + "'>");
	}else{
		html.append("<div id=\"" + id + "\" class='relatePeople-list'>");
	}
	var datas = topWindow.getDataCenter(Constants_RelatePeople);
	if(datas){
		for(var i = 0; i < datas.size(); i++){
			try{
				var item = datas.get(i);
				var text = PeopleRelate_TypeName[item.type];
				
				if(v3x.getBrowserFlag('selectPeopleShowType')){
					html.append("<option title=\"").append(text.escapeSpace()).append("\" value=\"").append(item.type).append("\" type=\"").append(Constants_RelatePeople).append("\">").append(text.escapeSpace()).append("</option>");
				}
				else{
					html.append("<div class='member-list-div' seleted='false' ondblclick=\"selectOne('" + Constants_RelatePeople + "',this,'"+id+"')\" onclick=\"selectMemberFn(this,'"+id+"')\" value=\"").append(item.type).append("\" type=\"").append(Constants_RelatePeople).append("\">").append(text.escapeHTML(true)).append("</div>");
				}
			}
			catch(e){
				log.error("", e)
			}
		}
	}
	
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		html.append("</select>");
	}else{
		html.append("</div>");
	}
	
	return html.toString();
}

function getOutworkerListOptionText(entity){
	var showText = entity.name.getLimitLength(nameMaxLength.two[0]);
		showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
	
	var showTile = "";
	var typeName = null;
	if(entity.parentDepartment){
		typeName = entity.parentDepartment.name;
		showTile = entity.parentDepartment.getFullName() + "/" + entity.name;
	}
	else{
		typeName = currentAccount.shortname;
	}
	
	showText += typeName;
		
	return [showText, showTile];
}

/**
 * ???????
 */
function hiddenArea1(){
	var area1Reduction = 0;
	var memberDataBodyObjSize = 25;
	if(document.getElementById("AreaTop1").style.display != "none"){
		area1Reduction = 24;
		memberDataBodyObjSize = 24;
	}
	
	if(area1Status){
		document.getElementById("Separator1").style.display = "";
		document.getElementById("Area1").style.display = "";
	
		document.getElementById("Area1").style.height = (140 - area1Reduction) + "px";
		
		var memberDataBodyObj = document.getElementById("memberDataBody");
		if(memberDataBodyObj){
			memberDataBodyObj.size = 13;
		}
		else{
			var memberDataBodyOrginalObj = document.getElementById("memberDataBodyOrginal");
			if(memberDataBodyOrginalObj){
				memberDataBodyOrginalObj.size = 13;
			}
		}
		
		area1Status = false;
	}
	else{
		document.getElementById("Separator1").style.display = "none";
		document.getElementById("Area1").style.display = "none";
	
		document.getElementById("Area1").style.height = (Constants_Left_height - area1Reduction) + "px";

		var memberDataBodyObj = document.getElementById("memberDataBody");
		if(memberDataBodyObj){
			memberDataBodyObj.size = memberDataBodyObjSize;
		}
		else{
			var memberDataBodyOrginalObj = document.getElementById("memberDataBodyOrginal");
			if(memberDataBodyOrginalObj){
				memberDataBodyOrginalObj.size = memberDataBodyObjSize;
			}
		}
	
		area1Status = true;
	}
}
/**
 * ???????
 * @param boolean true - hidden
 */
function hiddenArea2(flag){
	var area1Reduction = 0;
	if(document.getElementById("AreaTop1").style.display != "none"){
		area1Reduction = 24;
	}
	
	if(flag){//隐藏人员
		document.getElementById("Separator1").style.display = "none";	
		document.getElementById("Area1").style.height = (Constants_Left_height - area1Reduction) + "px";
		var _list1 = document.getElementById("List1");
		if(_list1){
			_list1.style.height = (Constants_Left_height - area1Reduction) + "px";
		}
		document.getElementById("Area2").style.display = "none";
		
		disableButton("button2");
	}
	else{//显示人员
		document.getElementById("Separator1").style.display = "";	
		document.getElementById("Area1").style.height = (140 - area1Reduction) + "px";
		var _list1 = document.getElementById("List1");
		if(_list1){
			_list1.style.height = (140 - area1Reduction) + "px";
		}
		document.getElementById("Area2").style.display = "";
		
		enableButton("button2");
	}
}

function getMembersHTML(type, id, keyword, fullWin){
	var _getMembersFun = null;
		
	var selectHTML = new StringBuffer();
	
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		//div展示人员select
		selectHTML.append(memberDataBody_div);
	}else if(fullWin == true){
		selectHTML.append(select2_tag_prefix_fullWin);
	}
	else{
		selectHTML.append(select2_tag_prefix);
	}
	
	if(Constants_Panels.get(type) && (_getMembersFun = Constants_Panels.get(type).getMembersFun) != null){
		var memberDataBody = document.getElementById("memberDataBody");
		var entity = topWindow.getObject(type, id);
		if(!entity){
			return selectHTML;
		}
		var __members = eval("entity." + _getMembersFun + "()");
		if(!__members){
			return selectHTML;
		}
		
		var isExternalLookDept = false;
		if(type == Constants_Department){
			isExternalLookDept = checkExternalMemberWorkScope(type, id);
		}
		
		var _isNeedCheckLevelScope = true;
		if(!isNeedCheckLevelScope 
			|| (type == Constants_Department && currentMember && (currentMember.departmentId == id || currentMember.isSecondPostInDept(id)))){
			_isNeedCheckLevelScope = false;
		}
		
		for(var i = 0; i < __members.size(); i++){
			var member = __members.get(i);
			
			if(keyword && member.name.toLowerCase().indexOf(keyword) < 0){
				continue;
			}
			
			try{
				if(isInternal && _isNeedCheckLevelScope && !checkLevelScope(member, entity)){ //越级
					continue;
				}
				
				//过滤密级
				if(isInternal && !checkSecretLevelScope(member)){
					continue;
				}
				
				if(!isExternalLookDept && !checkExternalMemberWorkScopeOfMember(member)){
					continue;
				}
				
				//当前登录者是内部人员，显示的部门是外部部门，根据工作范围重新计算
				if(!showAllOuterDepartmentFlag && isInternal && type == Constants_Outworker && !entity.isInternal){
					var extMember = topWindow.ExtMemberScopeOfInternal.get(id);
					if(!extMember || !extMember.contains(member.id)){
						continue;
					}
				}
				
				if(excludeElements.contains(Constants_Member + member.id)){
					continue;
				}
				var shadowMembers = new Array();
				if (Constants_Department == type) { // 列出部门下的人员
					while (i+1 < __members.size()) { // 还有下一个
						var nMember = __members.get(i+1); // 下一个
						if (nMember.id == member.id) { // 下一个与当前是同一人员
							shadowMembers.push(nMember); // 合并显示
							i++;
						} else {
							break;
						}
					}
				}
				selectHTML.append(addMember(type, entity, member, fullWin, shadowMembers));
			}
			catch(e){
				log.error("", e);
				continue;
			}
		}
	}
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		//div展示人员select
		selectHTML.append(memberDataBody_div_end);
	}else{
		selectHTML.append(select2_tag_subfix);
	}
	return selectHTML.toString();
}

/**
 * 显示人员
 */
function showMember(type, id, keyword){
	if(!checkCanSelectMember()){
		return;
	}
	
	//组
	if(type == Constants_Team){
		addTeamMember2List2(id, keyword);
	}
	else{ //直接关系人
		var selectHTML = getMembersHTML(type, id, keyword);
		document.getElementById("Area2").innerHTML = selectHTML;
		initIpadScroll("memberDataBody");//ipad滚动条解决
	}
}

/**
 * 把List2区域的数据清空
 */
function clearList2(){
	var memberDataBody = document.getElementById("memberDataBody");
	if(memberDataBody){
		if(memberDataBody.options){
			var len = memberDataBody.options.length;
			for(var i = 0; i < len; i++){
				memberDataBody.remove(0);
			}
		}else{
			memberDataBody.innerHTML='';
		}
	}
}

/**
 * 把人员添加到区域2
 * @param type List1的类型
 * @param entity list1的对象
 * @param member Member对象
 * @param fullWin 
 * @param shadowMembers 需要合并显示的Member数组
 */
function addMember(type, entity, member, fullWin, shadowMembers){
	if(ShowMe == false && currentMemberId && member.id == currentMemberId){
		return;
	}
	var sFlag = shadowMembers && shadowMembers.length > 0;
	var mArray = new Array();
	mArray.push(member);
	if (sFlag) {
		for (var i=0; i<shadowMembers.length; i++) {
			var sMember = shadowMembers[i];
			mArray.push(sMember);
		}
	}
	
	var attribute = "Department";
	
	if(type == Constants_Department || type == Constants_Outworker){
		attribute = "Post";
	}
	
	var showText = null;
	var _accountId = "";
	var className = "";
	var secondPostInDepartId = null;
	var showTitle = "";
	
	var emailOrMobileAttribute = getCanSelectEmailOrMobile();
	var emailOrMobile = null;
	if(emailOrMobileAttribute){
		emailOrMobile = member[emailOrMobileAttribute];
	}
	
	//显示手机号或email，而该人没有设置
	if(emailOrMobileAttribute && !emailOrMobile){
		return null;
	}
	
	var selectPeople_secondPostLabel = "(" + topWindow.v3x.getMessage("V3XLang.selectPeople_secondPost") + ")"
	
	if(member.type == "E"){ //兼职
		if(showConcurrentMember == false){
			return "";
		}
		
		_accountId = member.accountId;
		var account = allAccounts.get(_accountId);
		if(!account){
			log.warn("兼职[" + member.name + "]的主岗单位[" + _accountId + "]不存在")
			return "";
		}
		
		showText = member.name + "(" + account.shortname + ")";
		showText = showText.getLimitLength(nameMaxLength.two[0]);
		showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];

		if(emailOrMobile){
			showText += emailOrMobile;
		}
		else{
			for (var i=0; i<mArray.length; i++) {
				var cMember = mArray[i];
				var object_ = eval("cMember.get" + attribute + "()");
				if(object_){
					if (i > 0) {
						showText += " ";
						showTitle += " ";
					}
					showText += selectPeople_secondPostLabel + ((fullWin == true) ? object_.getFullName() : object_.name);
					if (fullWin && object_.getFullName) {
						showTitle += selectPeople_secondPostLabel + object_.getFullName();
					} else {
						showTitle += selectPeople_secondPostLabel + object_.name;
					}
				}
			}
/*			var object_ = null;
			if(type == Constants_Post){ //岗位页签，显示兼职部门
				object_ = member.getDepartment();
				
				if(object_ && fullWin == true){
					showTitle += member.name + "\n" + Constants_Component.get(Constants_Department) + ": " + object_.getFullName();
				}
			}
			else{
				object_ = member.getPost();
			}
			
			if(object_){
				showText += selectPeople_secondPostLabel + ((fullWin == true) ? object_.getFullName() : object_.name);
			}*/
		}
		
//		className = "secondPost-true";
	}
	else if(member.type == "G"){//在全集团范围内查出来的
		showText = member.name;
		showText = showText.getLimitLength(nameMaxLength.two[0]);
		showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
		_accountId = member.accountId;
		if(emailOrMobile){
			showText += emailOrMobile;
		}
		else{

			var account = allAccounts.get(_accountId);
			if(account){
				var fullName = "/" + member.departmentName;
				showText += account.shortname + fullName;
				showTitle += account.shortname + fullName;
			}
		}
	}
	else{
		showText = member.name.getLimitLength(nameMaxLength.two[0]);
		showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
		
		if(emailOrMobile){
			showText += emailOrMobile;
		}
		else{
			for (var i=0; i<mArray.length; i++) {
				var cMember = mArray[i];
				var jianzhiFlag = (cMember.type == "F" ? selectPeople_secondPostLabel : ""); 
				var object_ = eval("cMember.get" + attribute + "()");
				if(object_){
					if (i > 0) {
						showText += " ";
						showTitle += " ";
					}
					showText += jianzhiFlag + ((fullWin == true) ? object_.getFullName() : object_.name);
					if (fullWin && object_.getFullName) {
						showTitle += jianzhiFlag + object_.getFullName();
					} else {
						showTitle += jianzhiFlag + object_.name;
					}
				}
			}
		}
	}
	if (showTitle.length > 0) {
		showTitle = member.name.escapeHTML(true) + "&#13;" + eval("Constants_Component.get(Constants_" + attribute + ")") + ": " + showTitle.escapeHTML(true);
	}
	var sb = new StringBuffer();
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		sb.append("<div class='member-list-div' seleted='false' ondblclick='selectOneMemberDiv(this)'  onclick=\"selectMemberFn(this,'memberDataBody')\" title='" + showText.escapeHTML(true) + "' value='").append(member.id).append("' type='Member' accountId='").append( _accountId).append("'>").append(showText.escapeHTML(true)).append("</div>");
	}else{
		sb.append("<option title='" + showText.escapeHTML(true) + "' value='").append(member.id).append("' type='Member' accountId='").append( _accountId).append("'>").append(showText.escapeHTML(true)).append("</option>");
	}
	return sb.toString();
}

/**
 * 添加组的成员到List2
 */
function addTeamMember2List2(id, keyword){
	var team = topWindow.getObject(Constants_Team, id);
	if(!team){ //个人组不管
		return;
	}
	var hiddenOtherMemberOfTeam = getParentWindowData("hiddenOtherMemberOfTeam");
	var concurentMembers = topWindow.getDataCenter("concurentMembers")
	
	
	var selectHTML = new StringBuffer();
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		selectHTML.append(teamMemberDataBody_div);
	}else{
		selectHTML.append(select2_tag_prefix);
	}	
	
	selectHTML.append(addTeamMemberOfType(concurentMembers, team.getLeaders(), "Leader"));	
	selectHTML.append(addTeamMemberOfType(concurentMembers, team.getMembers(), "Member"));
	
	var sepTteamObj = document.getElementById("sep_team");
	if(sepTteamObj && sepTteamObj.checked == true){
		selectHTML.append(addTeamMemberOfType(concurentMembers, team.getSupervisors(), "Supervisor"));
		selectHTML.append(addTeamMemberOfType(concurentMembers, team.getRelatives(), "Relative"));
	}
	
	//Type : Leader/主管 Member/组员 Supervisors/领导 Relative/关联人员
	function addTeamMemberOfType(concurentMembers, _members, type){
		if(!_members){
			return "";
		}
		
		var str = new StringBuffer();
		for(var i = 0; i < _members.size(); i++){
			var member = _members.get(i);
			var _accountId = member.accountId;
			
			var emailOrMobileAttribute = getCanSelectEmailOrMobile();
			var emailOrMobile = null;
			if(emailOrMobileAttribute){
				emailOrMobile = member[emailOrMobileAttribute];
			}
			
			//过滤密级
			if(!checkSecretLevelScope(member)){
				continue;
			}
			
			//显示手机号或email，而该人没有设置
			if(emailOrMobileAttribute && !emailOrMobile){
				continue;
			}
			
			//隐藏组下外单位的人员
			if(hiddenOtherMemberOfTeam && _accountId != currentAccountId){
				continue;
			}
							
			if(excludeElements.contains(Constants_Member + member.id)){
				continue;
			}
			
//			if(_accountId != currentAccountId && onlyLoginAccount == true && hiddenOtherAccountMembers != true && member.type == "E"){
//				if(!concurentMembers.contains(member, "id")){
//					continue;
//				}
//			}
			
			if(keyword && member.name.toLowerCase().indexOf(keyword) < 0){
				continue;
			}
			
			if(ShowMe == false && currentMemberId && member.id == currentMemberId){
				continue;
			}
			
			var showText = member.name.getLimitLength(nameMaxLength.three[0]);
			if(_accountId != currentAccountId){
				var account = allAccounts.get(_accountId);
				if(account){
					showText += "(" + account.shortname + ")";
				}
			}
				
				
			if(!emailOrMobile){
				showText = showText.getLimitLength(nameMaxLength.three[0]);
				showText += NameSpace[nameMaxLength.three[0] + nameMaxSpace - showText.getBytesLength()];
				
				var deparetmentName = "";
				if(member.type == "E"){
					deparetmentName += member.departmentName;
				}
				else{
					var object_ = member.getDepartment();
					if(object_){
						deparetmentName = object_.name
					}
				}

				if(deparetmentName){
					showText += deparetmentName.getLimitLength(nameMaxLength.three[1]);
				}
				
				showText += NameSpace[nameMaxLength.three[0] + nameMaxSpace + nameMaxLength.three[1] + nameMaxSpace - showText.getBytesLength()];
				showText += topWindow.v3x.getMessage("V3XLang.selectPeople_Team" + type + "_label");
			}
			else{
				showText = showText.getLimitLength(nameMaxLength.two[0]);
				showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
				
				showText += emailOrMobile;
			}
			if(!v3x.getBrowserFlag('selectPeopleShowType')){
				str.append("<div class='member-list-div' seleted='false' ondblclick='selectOneMemberDiv(this)'  onclick=\"selectMemberFn(this,'memberDataBody')\"  value=\"").append(member.id).append("\" type=\"").append(Constants_Member).append("\" accountId=\"").append(_accountId).append("\">").append(showText.escapeHTML(true)).append("</div>");
			}else{
				str.append("<option value=\"").append(member.id).append("\" title=\"").append(showText.escapeHTML(true)).append("\" type=\"").append(Constants_Member).append("\" accountId=\"").append(_accountId).append("\">").append(showText.escapeHTML(true)).append("</option>");
			}
			
		}
		
		return str;
	}

	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		selectHTML.append(memberDataBody_div_end);
	}else{
		selectHTML.append(select2_tag_subfix);
	}
	
	document.getElementById("Area2").innerHTML = selectHTML.toString();
	initIpadScroll("memberDataBody");//ipad滚动条解决
}

/**
 * 监听Member的事件
 */
function listenermemberDataBody(object){
	tempNowSelected.clear();
	var ops = object.options;
	for(var i = 0; i < ops.length; i++) {
		var option = ops[i];
		if(option.selected){
			var e = getElementFromOption(option);
			if(e){
				tempNowSelected.add(e);
			}
		}
	}
}
	
/**
 * 
 * @return 0-允许访问; 1-允许看,无动作; -1; //不显示
 */
function isShowDepartmentTree(depart){
	var showDepartmentsOfTree = getParentWindowData("showDepartmentsOfTree");
	if(showDepartmentsOfTree == null){
		return 0; //允许访问
	}
	
	var showDepartmentsOfTreeStr = showDepartmentsOfTree.split(",");
	
	for(var i = 0; i < showDepartmentsOfTreeStr.length; i++) {
		var d = topWindow.getObject(Constants_Department, showDepartmentsOfTreeStr[i]);
		if(d && d.accountId && currentAccountId) {
			if(d.path == depart.path || depart.path.startsWith(d.path + ".")){//当前部门或子部门
				return 0; //允许访问
			}
			
			if(d.path.startsWith(depart.path + ".")){//当前部门是我的上级部门
				return 1; //允许看
			}
		}
	}
	
	return -1; //禁止
}

/**
 * 显示树形结构
 */
function initTree(type){
	var root = null;
	var currentNodeId = null;
	var lockTree = false;
	
	if(type == Constants_Account){
		root = allAccounts.get(accessableRootAccountId);
		currentNodeId = accessableRootAccountId;
	}
	else{
		root = allAccounts.get(currentAccountId);
		currentNodeId = departmentId;
		lockTree = onlyCurrentDepartment;
		currentAccountLevelScope = parseInt(root.levelScope, 10);
	}
	
	if(root == null){
		return;
	}
	
	if(!isInternal){
		currentNodeId = null;
	}
	
	var isShowCheckbox = (type == Constants_Account) && checkCanSelect(type) && maxSize != 1;
	
	if(lockTree) disableButton("button1");
	tree = new WebFXTree(root.id, Constants_Account, root.name, type, true, "clearList2()", lockTree, "");
	tree.setBehavior('classic');
	tree.hasShowChild = true;
	tree.hasGoChild = true;
	tree.isShowCheckbox = isShowCheckbox;
	
	document.getElementById("Area1").innerHTML = "<div id='List1' style='width:251px; height:140px; overflow:auto;'>" + tree + "</div>";
	document.getElementById("Area1").className = "iframe";
	
	var allParents = null;
	
	if(!treeInMyDepart && type == Constants_Department && currentNodeId != null){
		allParents = topWindow.findMultiParent(topWindow.getDataCenter(type), currentNodeId);
	}
	else if(type == Constants_Account){
		allParents = topWindow.findMultiParent(accessableAccounts.values(), currentNodeId);
	}
	
	if(allParents != null){
		var expandNode = tree;
		var isShowCheckbox = (type == Constants_Account) && checkCanSelect(type) && maxSize != 1;
		for(var i = 0; i < allParents.size(); i++){
			var n = allParents.get(i);
			if(n.isInternal == false || n.isInternal == "false" || n.id == root.id){
				continue;
			}
			
			//该部门不显示
			if(excludeElements.contains(type + n.id)){
				continue;
			}
			
			var _status = isShowDepartmentTree(n);
			if(_status == -1){
				continue;
			}
			
			var action = (_status == 0) ? "showList2('" + type + "', '" + n.id + "')" : "";
			
			var item = new WebFXTreeItem(n.id, type, n.name, n.hasChild, action, lockTree, n.description);
			item.isShowCheckbox = isShowCheckbox;
			expandNode.add(item);
			
			expandNode = item;
		}
		
		var myNode = showChildTree(type, expandNode.id, expandNode, lockTree);
		webFXTreeHandler.expanded = expandNode;
					
		tree.expandAll();
		
		if(myNode != null){
			myNode.toggle();
			myNode.select();
			showList2(type, myNode.id);
		}
		
//		treeInMyDepart = true;
		
		return;
	}
	
	showChildTree(type, root.id, tree);
}

/**
 * ????
 */
function showChildTree(type, id, parentNode, _onlyCurrentDepartment){
	var datas2Show = null;
	if(type == Constants_Account){
		datas2Show = topWindow.findChildInList(accessableAccounts.values(), id);
	}
	else{
		var _getChildrenFun = Constants_Panels.get(type).getChildrenFun;
		
		var entity = topWindow.getObject(type, id);
		if(entity){
			datas2Show = eval("entity." + _getChildrenFun + "()");
		}
		else{
			datas2Show = topWindow.findChildInList(topWindow.getDataCenter(type), id);
		}
	}
	if(!datas2Show){
		return;
	}
	
	var myNode = null;
	var isShowCheckbox = (type == Constants_Account) && checkCanSelect(type) && maxSize != 1;
	
	for(var i = 0; i < datas2Show.size(); i++){
		var n = datas2Show.get(i);
		
		if(_onlyCurrentDepartment == true && departmentId != n.id){
			continue;
		}
		
		if(n.isInternal == false || n.isInternal == "false"){
			continue;
		}
		
		//该部门不显示
		if(excludeElements.contains(type + n.id)){
			continue;
		}
		
		var _status = isShowDepartmentTree(n);
		if(_status == -1){
			continue;
		}
		
		var action = (_status == 0) ? "showList2('" + type + "', '" + n.id + "')" : "";
				
		var item = new WebFXTreeItem(n.id, type, n.name, n.hasChild, action, false, n.description);
		item.isShowCheckbox = isShowCheckbox;
		
		if(departmentId == n.id){
			myNode = item;
		}
		
		parentNode.add(item);
	}
	
	parentNode.hasShowChild = true;
	parentNode.hasGoChild = false;
		
	webFXTreeHandler.expanded = parentNode;
	
	return myNode;
}
/**
 * ????
 */
function showParentTree(){
	if(area1Status){//????????????????????????????
		hiddenArea1();
	}
	
	if(tree == null){
		return;
	}
		
	var nowExpandNode = tree.getSelected();
	
	var _parentNode = nowExpandNode.parentNode;
	
	if(nowExpandNode == null ||  _parentNode== null){
		return;
	}
		
	webFXTreeHandler.toggle(document.getElementById(_parentNode.id));
	_parentNode.select();
	showList2(_parentNode.type, _parentNode.id);
}
/*
 * ???????
 */
function selectList1Item(type, objTD){
	tempNowSelected.clear();
	
	var ops = objTD.options;
	var count = 0;
	for(var i = 0; i < ops.length; i++) {
		var option = ops[i];
		if(option.selected){
			var e = getElementFromOption(option);
			if(e){
				tempNowSelected.add(e);
				count++;
			}
		}
	}

	if(count == 1 && tempNowPanel.isShowMember == true){
		var id = objTD.value;
		showList2(type, id);
	}
	
	if(nowSelectedList1Item != null){
		nowSelectedList1Item = null;
	}
	
	nowSelectedList1Item = objTD;	
}
/**
 * ??????
 */
function selectOneMember(selectObj){
	if(!selectObj || selectObj.selectedIndex < 0){
		return;
	}
	
	var option = selectObj.options[selectObj.selectedIndex];
	if(!option){
		return;
	}
	
	var element = getElementFromOption(option);
	if(element){
		tempNowSelected.clear();
		tempNowSelected.add(element);
		
		selectOne();
	}
}

/**
 * 选择了区域2的项目，转换成Element对象
 */
function getElementFromOption(option){
	if(!option){
		return null;
	}
	
	var _accountId = option.getAttribute("accountId");
	var typeStr = option.getAttribute("type");
	var idStr  =  option.getAttribute("value");
	
	
	//Element(type, id, name, typeName, accountId, accountShortname, description)
	return new Element(typeStr, idStr, getName(typeStr, idStr), "", _accountId, "", "");
}

function getName(typeStr, idStr){
	
	var types = typeStr.split(valuesJoinSep);
	var ids   = idStr.split(valuesJoinSep);
	
	var elementName = [];
	var entity ;
	for(var i = 0; i < types.length; i++) {
		if(types[i] == Constants_FormField){
			//var datas = parentWindow.dialogArguments.dialogArguments.getformFlowField();
			//if(datas){		
			//}
			elementName[elementName.length] = idStr;				
		}else{
			entity = topWindow.getObject(types[i], ids[i]);
			if(entity == null){
				//加载单位数据
				accountId = accountId || currentAccountId;
				topWindow.initOrgModel(accountId, currentMemberId);
				entity = topWindow.getObject(types[i], ids[i]);
			}
			elementName[elementName.length] = entity ? entity.name : searchNames.get(idStr).name;
		}		
		
	}
	
	return elementName.join(arrayJoinSep);
}

var NeedCheckEmptyMemberType = new ArrayList();
NeedCheckEmptyMemberType.add(Constants_Department);
NeedCheckEmptyMemberType.add(Constants_Team);
NeedCheckEmptyMemberType.add(Constants_Post);
NeedCheckEmptyMemberType.add(Constants_Level);
NeedCheckEmptyMemberType.add(Constants_Department + "_" + Constants_Post);

/**
 * 当前显示单位是否是根单位
 */
function isRootAccount(){
	return (currentAccountId == rootAccount.id);
}

/**
 * 检测集合里面是否是空的，一般检测部门和组
 * @return true - 是空的， false - 不是空的或者不需要检测
 */
function checkEmptyMember(type, id){
	if(isRootAccount()){
		return false;
	}
	if(!type 
		|| !id 
		|| !NeedCheckEmptyMemberType.contains(type)
		|| !checkCanSelectMember()){
		return false;
	}
	
	var ids = id.split("_");
	var types = type.split("_");
	
	var entity = topWindow.getObject(types[0], ids[0]);
	if(!entity){
		return true;
	}
	
	var ms = entity.getAllMembers();
	if(ms == null || ms.isEmpty()){
		return true;
	}
	
	if(type == Constants_Department + "_" + Constants_Post){
		for(var i = 0; i < ms.size(); i++){
			var m = ms.get(i);
			if(m.postId == ids[1]){
				return false;
			}
			
			//部门下的副岗，可能是多个
			var sps = m.getSecondPost().get(ids[0]);
			if(sps){
				for(var c = 0; c < sps.size(); c++) {
					if(sps.get(c).id == ids[1]){
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	return false;
}

/*
 * ?????????????
 * tempNowSelect ArrayList<Element>
 */
function selectOne(type, objTD){
	var flag = false;
	if(type && objTD){
		tempNowSelected.clear();
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			var ops = objTD.options;
			var count = 0;
			for(var i = 0; i < ops.length; i++) {
				var option = ops[i];
				if(option.selected){
					var e = getElementFromOption(option);
					if(e){
						tempNowSelected.add(e);
					}
				}
			}
		}else{
			if(arguments[2]){
				var ops = document.getElementById(arguments[2]).childNodes;
				var count = 0;
				for(var i = 0; i < ops.length; i++) {
					var option = ops[i];
					if(option.getAttribute('seleted')){
						var e = getElementFromOption(option);
						if(e){
							tempNowSelected.add(e);
						}
					}
				}
				selectOneMemberDiv(objTD)
				flag = true;
			}
		}
	}
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
		if(arguments[2]){
			//双击组 选择组
			listenermemberDataBodyDiv(document.getElementById(arguments[2]));
		}else{
			listenermemberDataBodyDiv(document.getElementById(temp_Div));
		}
	}
	if(tempNowSelected == null || tempNowSelected.isEmpty()){
		return;
	}
	
	var _showAccountShortname = false;
	var unallowedSelectEmptyGroup = getParentWindowData("unallowedSelectEmptyGroup") || false;
	
	var alertMessageBeyondLevelScop = new StringBuffer();
	var alertMessageEmptyMemberNO = new StringBuffer();
	var alertMessageSecretLevelScop = new StringBuffer();
	
	var isCanSelectGroupAccount = getParentWindowData("isCanSelectGroupAccount");
	var isConfirmExcludeSubDepartment = getParentWindowData("isConfirmExcludeSubDepartment");
	for(var i = 0; i < tempNowSelected.size(); i++){
		var element = tempNowSelected.get(i);
		var type = element.type;
		if(type == Constants_Outworker){
			type = Constants_Department;
		}
		
		if(!checkCanSelect(type)){
			continue;
		}
		
		if(!checkExternalMemberWorkScope(type, element.id)){
			continue;
		}
		
		if((isCanSelectGroupAccount == false || isGroupAccessable == false) && type == Constants_Account && element.id == rootAccount.id){
			continue;
		}
		
		//检测越级访问，只要部门/组里面有任何一个人不能选择，则该部门/组不能选择
		if(type != Constants_Member && !checkAccessLevelScope(type, element.id)){
			alertMessageBeyondLevelScop.append(element.name);
			continue;
		}
		
		if(isConfirmExcludeSubDepartment && type == Constants_Department){  //当前选择的部门，判断是否要子部门
			var _getChildrenFun = Constants_Panels.get(type).getChildrenFun;
			
			var entity = topWindow.getObject(type, element.id);
			if(entity){
				datas2Show = eval("entity." + _getChildrenFun + "()");
			}
			else{
				datas2Show = topWindow.findChildInList(topWindow.getDataCenter(type), id);
			}
			if(datas2Show && !datas2Show.isEmpty()){
				var temp = showConfirm4Select();
				var _index = element.name.indexOf("(" +_("V3XLang.selectPeople_excludeChildDepartment") + ")");
				if(_index != -1) {
					element.name = element.name.substring(0, _index);
				}
				if(temp!='') {
					if(temp=='false'){//通过JSP页面来提示是否包含子部门
						element.excludeChildDepartment = true;
						element.name += "(" +_("V3XLang.selectPeople_excludeChildDepartment") + ")";
					} else {
						element.excludeChildDepartment = false;
					}
				} else {
					continue;
				}
			}
		}
		
		var key = type + element.id;

		if(selectedPeopleElements.containsKey(key)){	//??????????????????
			continue; //Exist
		}
		
		//检测集合里面是否是空的，一般检测部门和组
		if(checkEmptyMember(type, element.id)){
			if(unallowedSelectEmptyGroup){ //不允许选择空组
				alertMessageEmptyMemberNO.append(element.name);
				continue;
			}
			else{
				if(!confirm(topWindow.v3x.getMessage("V3XLang.selectPeople_alertEmptyMember", element.name))){
					continue;
				}
			}
		}
		
		var accountShortname = null;
		var _accountId = element.accountId;
		
		if(_accountId && _accountId != currentAccountId){ //其它单位的
			accountShortname = allAccounts.get(_accountId).shortname;
			_showAccountShortname = true;
		}
		else{ //当前单位的
			_accountId = currentAccountId;
			accountShortname = currentAccount.shortname; 
		}
		
		element.type = type;
		element.typeName = Constants_Component.get(type);
		element.accountId = _accountId;
		
		if(type != Constants_Account){
			element.accountShortname = accountShortname;
		}else if(type == Constants_Account){
			element.accountShortname = allAccounts.get(element.id).shortname;
		}
		
		//检测跨密级访问，只要部门/组里面有任何一个人不能选择，则该部门/组不能选择
		if(type != Constants_Member && !checkFlowSecretLevelScope(type, element.id)){
			alertMessageSecretLevelScop.append(element.name);
			continue;
		}
		add2List3(element);
		selectedPeopleElements.put(key, element);		
	}
	
	var sp = topWindow.v3x.getMessage("V3XLang.common_separator_label");
	var alertMessage = "";
	if(!alertMessageBeyondLevelScop.isBlank()){
		alertMessage += (topWindow.v3x.getMessage("V3XLang.selectPeople_alertBeyondLevelScope", alertMessageBeyondLevelScop.toString(sp).getLimitLength(50, "..."))) + "\n\n"
	}
	if(!alertMessageSecretLevelScop.isBlank()){
		alertMessage += (topWindow.v3x.getMessage("V3XLang.selectPeople_alertSecretLevelScope", alertMessageSecretLevelScop.toString(sp).getLimitLength(50, "..."))) + "\n\n"
	}
	if(!alertMessageEmptyMemberNO.isBlank()){
		alertMessage += (topWindow.v3x.getMessage("V3XLang.selectPeople_alertEmptyMemberNO", alertMessageEmptyMemberNO.toString(sp).getLimitLength(50, "...")))
	}
	
	if(alertMessage){
		alert(alertMessage)
	}
	
	if(_showAccountShortname == true && showAccountShortname == false){
		showAccountShortname = _showAccountShortname;
		mergeAccountShortnameOfSelected();
	}
}

//选人界面弹出页面按照提示进行，【包含】【不包含】【取消】提示
function showConfirm4Select(){
	var rv = v3x.openWindow({
        url: "/seeyon/genericController.do?ViewPage=common/SelectPeople/selectPeople4Confirm",
        height: 60,
        width: 270
    });
	if(rv==0){
		return 'true';
	}
	if(rv==1){
		return 'false';
	}
	if(rv==2) {
		return '';
	}
}

/**
 * ????????
 */
function add2List3(element){
	var key = element.type + element.id;

	var text = element.name;
	if(showAccountShortname && element.type != Constants_Account&& element.type != Constants_ExchangeAccount){
		text = text + "(" + element.accountShortname + ")";
	}
	if(v3x.getBrowserFlag('selectPeopleShowType')){
		var option = new Option(text, key);
		option.id = element.id;
		option.type = element.type;
		option.className = element.type + "";
		option.accountId = element.accountId;
		option.title = element.name;
		option.accountShortname = element.accountShortname;
		document.getElementById("List3").options.add(option);
	}else{
		var option = document.createElement('div');
		var text = document.createTextNode(text);
		option.appendChild(text);
		option.setAttribute('id',element.id);
		option.setAttribute('value',key);
		option.setAttribute('type',element.type);
		option.setAttribute('name',element.name);
		option.setAttribute('seleted','false');
		option.setAttribute('class','member-list-div');
		option.setAttribute('accountId',element.accountId);
		option.setAttribute('accountShortname',element.accountShortname);
		
		option.onclick = function(){selectMemberFn(this);}
		option.ondblclick = function(){removeOne(key,this);}
		document.getElementById("List3").appendChild(option);
		initIpadScroll("List3");//ipad滚动条解决
	}
}
/*
 * 从List3种删除数据，需要选择List3-item
 */
function removeOne(key, obj){
	if(!key){	//删除多项
		var ops = document.getElementById("List3");
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			for(var i = 0; i < ops.length; i ++) {
				if(ops[i].selected){
					var key = ops[i].value;
					document.getElementById("List3").remove(i);	
									
					selectedPeopleElements.remove(key);
					i--;
				}
			}
		}else{
			var ops = document.getElementById("List3").childNodes;
			for(var i = 0; i < ops.length; i++) {
				var option = ops[i];
				if(option){
					if(option.getAttribute('seleted')=='true'){
						var key =option.getAttribute('value');
						option.parentNode.removeChild(option);
						selectedPeopleElements.remove(key);
						i--;
					}
				}
			}	
		}
	}
	else{	//删除单项
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			var i = obj.selectedIndex;
			if(i >= 0){
				document.getElementById("List3").remove(obj.selectedIndex);
				selectedPeopleElements.remove(key);
			}
		}else{
			obj.parentNode.removeChild(obj);
			selectedPeopleElements.remove(key);
		}
	}
}

/******************** ?? List3 ?????????????? ********************/

//上移或下移已经选择了的数据
function exchangeList3Item(direction){
	var list3Object = document.getElementById("List3");
	var list3Items = list3Object.options;
	var nowIndex = list3Object.selectedIndex;
	//ipad div实现select
	if(!v3x.getBrowserFlag('selectPeopleShowType')){
    	list3Items = list3Object.childNodes;
    	for(var i = 0;i<list3Items.length;i++){
    		var op = list3Items[i];
    		var selected = op.getAttribute('seleted');
    		if(selected == 'true'){
    			nowIndex = i;
    		}
    	}
	}

	if(direction == "up"){
		if(nowIndex > 0){
			if(v3x.getBrowserFlag('selectPeopleShowType')){
				var nowOption = list3Items.item(nowIndex);
				var nextOption = list3Items.item(nowIndex - 1);
				
				//多浏览器处理
				var textTemp = nowOption.innerHTML;
				var valueTemp = nowOption.getAttribute('value');
				
				var textTemp2 = nextOption.innerHTML;
				var valueTemp2 = nextOption.getAttribute('value');
				
				nowOption.innerHTML = textTemp2;
				nowOption.setAttribute('value',valueTemp2);
				
				nextOption.innerHTML = textTemp;
				nextOption.setAttribute('value',valueTemp);
				list3Object.selectedIndex = nowIndex - 1;
				/*
				var newOption = new Option(nowOption.text, nowOption.value);
				newOption.className = nowOption.className;
				newOption.selected = true;
				list3Object.add(newOption, nowIndex - 1);
				list3Object.remove(nowIndex + 1);
				*/
				selectedPeopleElements.swap(nowOption.value, nextOption.value);
			}else{
				var nowOption = list3Items[nowIndex];
				var nextOption = list3Items[nowIndex - 1];
				
				var textTemp = nextOption.innerHTML;
				var valueTemp = nextOption.getAttribute('value');
				
				nextOption.innerHTML = nowOption.innerHTML;
				nextOption.setAttribute('value',nowOption.getAttribute('value'));
				
				nowOption.innerHTML = textTemp;
				nowOption.setAttribute('value',valueTemp);
				nowOption.setAttribute('seleted','false');
				nowOption.setAttribute('class','member-list-div');
				
				nextOption.setAttribute('seleted','true');
				nextOption.setAttribute('class','member-list-div-select');
				
				selectedPeopleElements.swap(nowOption.getAttribute('value'), nextOption.getAttribute('value'));
			}

		}
	}
	else if(direction == "down"){
		if(nowIndex > -1 && nowIndex < list3Items.length - 1){
			if(v3x.getBrowserFlag('selectPeopleShowType')){
				var nowOption = list3Items.item(nowIndex);
				var nextOption = list3Items.item(nowIndex + 1);
				//多浏览器处理
				var textTemp = nowOption.innerHTML;
				var valueTemp = nowOption.getAttribute('value');
				
				var textTemp2 = nextOption.innerHTML;
				var valueTemp2 = nextOption.getAttribute('value');
				
				nowOption.innerHTML = textTemp2;
				nowOption.setAttribute('value',valueTemp2);
				
				nextOption.innerHTML = textTemp;
				nextOption.setAttribute('value',valueTemp);
				list3Object.selectedIndex = nowIndex + 1;
				
				/**
				var newOption = new Option(nowOption.text, nowOption.value);
				newOption.className = nowOption.className;
				newOption.selected = true;
				list3Object.add(newOption, nowIndex + 2);
				list3Object.remove(nowIndex);
				**/
				selectedPeopleElements.swap(nowOption.value, nextOption.value);
			}else{
				var nowOption = list3Items[nowIndex];
				var nextOption = list3Items[nowIndex + 1];
				
				var textTemp = nextOption.innerHTML;
				var valueTemp = nextOption.getAttribute('value');
				
				nextOption.innerHTML = nowOption.innerHTML;
				nextOption.setAttribute('value',nowOption.getAttribute('value'));
				
				nowOption.innerHTML = textTemp;
				nowOption.setAttribute('value',valueTemp);
				nowOption.setAttribute('seleted','false');
				nowOption.setAttribute('class','member-list-div');
				
				nextOption.setAttribute('seleted','true');
				nextOption.setAttribute('class','member-list-div-select');
				
				selectedPeopleElements.swap(nowOption.getAttribute('value'), nextOption.getAttribute('value'));
			}
		}
	}
	else{
		log.warn('The direction ' + direction + ' is not defined.');
	}
}

//在全集团范围内查询出的人员, 用于获取人员姓名
var searchNames = new Properties();

/*******************************
 * 搜索
 */
var isSearch = false;
function searchItems(){
	if(tempNowPanel == null){
		return;
	}
	
	var type = tempNowPanel.type;
	var showMode = tempNowPanel.showMode;
	var searchArea = tempNowPanel.searchArea;
	
	if(document.getElementById("q").disabled){
		return;
	}
	
	var keyword = document.getElementById("q").value;
	if(!keyword){//没有关键字, 给出提示
		/*if(showMode == Constants_ShowMode_TREE){
			var expandedNode = tree.getSelected();
			if(!expandedNode){
				return;
			}
			
			expandedNode.toggle();

			showList2(type, expandedNode.id);
		}
		else if(showMode == Constants_ShowMode_LIST){
			initList(type);
			if(nowSelectedList1Item){
				showList2(type, nowSelectedList1Item.id);			
			}
		}*/
		alert(topWindow.v3x.getMessage("V3XLang.index_input_error"));
		return;
	}
	
	keyword = keyword.toLowerCase();
	
	if(showMode == Constants_ShowMode_LIST && searchArea == 1){//只搜索1区
		initList(type, keyword);
		return;
	}
	
	if(type == Constants_Department){ //当前是部门面板
		if(!showQueryInputOfDepart()){
			return;
		}
		
		clearList2();
		
		var members = null;
		var department = null;
		var departments = null;
		
		var seachGroup = !$("#seachGroupMember").is(":hidden") && $("#seachGroup").attr("checked");
		
		if(seachGroup){
			var requestCaller = new XMLHttpRequestCaller(this, "ajaxSelectPeopleManager", "getQueryOrgModel", false, "GET");
			requestCaller.returnValueType = "TEXT";
			requestCaller.filterLogoutMessage = false;
			requestCaller.addParameter(1, "String", keyword);
			requestCaller.addParameter(2, "Boolean", isNeedCheckLevelScope);
			var result0 = requestCaller.serviceRequest();
			
			if(!result0 || result0.startsWith("[LOGOUT]")){
				alert(result0.substring(8));
				return;
			}
			
			var result = null;
			eval("result = " + result0 + ";");
			if(!result){
				return;
			}
			
			var _members = result[Constants_Member];
			if(_members){
				members = new ArrayList();
				for(var i = 0; i < _members.length; i++) {
					var m = _members[i];
	
					var secondPostIds = null;
					var SP = m["F"];
					if(SP){
						secondPostIds = new ArrayList();
						for(var s = 0; s < SP.length; s++) {
							var secondPostId = new Array();
							secondPostId[0] = SP[s][0];
							secondPostId[1] = SP[s][1];
							secondPostIds.add(secondPostId);
						}
					}else{
						secondPostIds = EmptyArrayList;
					}
					
					var member = new topWindow.Member(m["K"], m["N"], m["S"], m["D"], m["P"], secondPostIds, m["L"], m["I"], m["Y"], m["M"], "", m["A"]);
					member.departmentName = m["DM"];
					member.type = "G";
					members.add(member);
					searchNames.put(member.id, member);
				}
			}
		}else{
			var expandedNode = tree.getSelected();
			if(!expandedNode){
				return;
			}
			
			var id = expandedNode.id;
			var _type = expandedNode.type;
			
			if(currentArea2Type != Constants_Member){
				if(_type == Constants_Department){
					showSubOfDepartment(id, currentArea2Type, keyword);
				}
				
				return;
			}
			
			if(_type == Constants_Department){
				department = topWindow.getObject(Constants_Department, id);
				if(!department){
					return;
				}
				members = department.getAllMembers();
				departments = department.getAllChildren();
			}else if(_type == Constants_Account){
				department = currentAccount;
				members = topWindow.getDataCenter(Constants_Member);
				departments = topWindow.getDataCenter(Constants_Department);
			}
		}
		
		var selectHTML = new StringBuffer();
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			selectHTML.append(select2_tag_prefix);
		}else{
			selectHTML.append(memberDataBody_div);
		}
		if(departments){
			for(var d = 0; d < departments.size(); d++) {
				var dept = departments.get(d);
	
				if(!dept.isInternal || dept.name.toLowerCase().indexOf(keyword) < 0){
					continue;
				}
				
				var parentDepartmentId = dept.parentId;
				
				var parentDeptName = null;
				if(parentDepartmentId == currentAccountId){
					parentDeptName = allAccounts.get(parentDepartmentId).shortname;
				}
				else{
					parentDeptName = topWindow.getObject(Constants_Department, dept.parentId).name;
				}
				
				var showText = dept.name;
				
				if(parentDeptName){
					showText = showText.getLimitLength(nameMaxLength.two[0]);
					showText += NameSpace[nameMaxLength.two[0] + nameMaxSpace - showText.getBytesLength()];
					showText += parentDeptName;
				}
				
				if(!excludeElements.contains(Constants_Department + dept.id)){
					if(v3x.getBrowserFlag('selectPeopleShowType')){
						selectHTML.append("<option value='").append(dept.id).append("' class='Department' type='Department' accountId='").append(currentAccountId).append("'>").append(showText.escapeHTML(true)).append("</option>");
					}else{
						selectHTML.append("<div class='member-list-div' seleted='false' ondblclick='selectOneMemberDiv(this)'  onclick=\"selectMemberFn(this,'memberDataBody')\"  value='").append(dept.id).append("' class='Department' type='Department' accountId='").append(currentAccountId).append("'>").append(showText.escapeHTML(true)).append("</div>");
					}
				}
			}
		}
		
		if(members && checkCanSelectMember()){
			var hasShowMembers = {};
			
			for(var m = 0; m < members.size(); m++) {
				var member = members.get(m);
	
				if(!member.isInternal || hasShowMembers[member.id]){ //已经显示了，防止副岗兼职重复出现
					continue;
				}
				if(member.name.toLowerCase().indexOf(keyword) < 0){
					continue;
				}
				
				if(member.type != "G" && !checkLevelScope(member, department)){ //越级
					continue;
				}
				
				//跨密级
				if(member.type != "G" && !checkSecretLevelScope(member)){ 
					continue;
				}
				
				if(!excludeElements.contains(Constants_Member + member.id)){
					selectHTML.append(addMember(Constants_Department, department, member));
					hasShowMembers[member.id] = "T";
				}
			}
			
			hasShowMembers = null;
		}
		if(v3x.getBrowserFlag('selectPeopleShowType')){
			selectHTML.append(select2_tag_subfix);
		}else{
			selectHTML.append(memberDataBody_div_end);
		}
		
		document.getElementById("Area2").innerHTML = selectHTML.toString();
	}
	else if(showMode == Constants_ShowMode_LIST && nowSelectedList1Item){
		if(!checkCanSelectMember()){
			return;
		}
	
		showList2(type, nowSelectedList1Item.value, null, keyword);
	}
}

function removeFromList3(key){
	var ops = document.getElementById("List3").options;
	for(var i = 0; i < ops.length; i++) {
		if(ops.item(i).value == key){
			ops.remove(i);
			break;
		}
	}
}

/**
 * Member(id, name, departmentId, postId, levelId, email, mobile, description)
 * 
 * @return Array<Element>
 */
function getSelectedPeoples(_maxSize, _minSize, needlessPreReturnValueFun){
	var _selectedPeopleElements = new ArrayList();
	var _selectedPeopleTypes = new Properties();
	
	var _selectedPeopleKeys = selectedPeopleElements.keys();
	
	for(var i = 0; i < _selectedPeopleKeys.size(); i++){
		var key = _selectedPeopleKeys.get(i);
		if(key){
			var value = selectedPeopleElements.get(key);
			_selectedPeopleElements.add(value);
			
			var type = value.type;
			if(type != Constants_Member){
				var _indexes = _selectedPeopleTypes.get(type);
				if(_indexes == null){
					_indexes = new ArrayList();
					_selectedPeopleTypes.put(type, _indexes);
				}
				
				_indexes.add(i);
			}
		}
	}
	
	_maxSize = _maxSize == null ? maxSize : _maxSize;
	_minSize = _minSize == null ? minSize : _minSize;

	var nowSize = _selectedPeopleElements.size();
	if(_maxSize > 0 && nowSize > _maxSize){
		throw (topWindow.v3x.getMessage("V3XLang.selectPeople_alert_maxSize", _maxSize, nowSize))
	}
	
	if(_minSize > 0 && nowSize < _minSize){
		throw (topWindow.v3x.getMessage("V3XLang.selectPeople_alert_minSize", _minSize, nowSize))
	}
	
	if(nowSize < 2){ //就一项数据比什么比嘛
		return getData();
	}
	
	//getIsCheckSelectedData() 是否检测被选数据的重复性，由JSP实现
	if(getIsCheckSelectedData() == false){
		return getData();
	}
	
	if(_selectedPeopleTypes.containsKey(Constants_Account)){
		checkIsContainAccount();
	}
	
	//不允许同时选择部门和其下的子部门
	if(!getParentWindowData("isAllowContainsChildDept")){
		if(_selectedPeopleTypes.containsKey(Constants_Department)){
			//检查部门子部门的包含关系 
			checkIsContainChildDepartment();
		}
	}
	
	var message = new ArrayList();
	var repeatingItem = new ArrayList();
	
	for(var i = 0; i < _selectedPeopleElements.size(); i++) {
		var element = _selectedPeopleElements.get(i);
		
		//检测人
		if(element.type == Constants_Member){
			var member = topWindow.getObject(Constants_Member, element.id);
			
			if(member == null){ //人员可能被删除了
				continue;
			}
			
			//检测该人的部门是否也被选择了（包括所有上级部门）
			var departmentIndexes = _selectedPeopleTypes.get(Constants_Department);
			if(departmentIndexes && checkCanSelect(Constants_Department)){
				for(var t = 0; t < departmentIndexes.size(); t++) {
					var el = _selectedPeopleElements.get(departmentIndexes.get(t));
					if(el && el.type == Constants_Department){
						var entity = topWindow.getObject(Constants_Department, el.id);
						if(!entity){
							continue;
						}
						var flag = false;
						if(el.excludeChildDepartment){
							flag = member.departmentId == el.id;
						}else{
							var members = entity.getAllMembersMap();
							flag = members.containsKey(member.id);
						}
						if(flag){
							message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name));
							repeatingItem.add(Constants_Member + element.id);
							break;//判断一个就够了
						}
					}
				}
			}
		
			//检测该人的岗位是否也被选择了
			if(_selectedPeopleTypes.containsKey(Constants_Post) && checkCanSelect(Constants_Post)){
				el = selectedPeopleElements.get(Constants_Post + member.postId);
				
				if(!el && member.secondPostIds){ //副岗
					var _secondPostIds = member.secondPostIds; //List<[Department.id, Post.id]>
					for(var t = 0; t < _secondPostIds.size(); t++) {
						var _secondPostId = _secondPostIds.get(t);	//[Department.id, Post.id]
						var _postId = _secondPostId[1];
						
						el = selectedPeopleElements.get(Constants_Post + _postId);
						if(el){
							break;
						}
					}
				}
				if(!el){ //兼职
					var _concurents = topWindow.getObject(Constants_concurentMembers, member.id); //此人在该单位的兼职
					if(_concurents){
						for(var t = 0; t < _concurents.length; t++) {
							var _concurent = _concurents[t]; //判断是否是这个岗位
							var __concurentPost = _concurent.postId;
							el =selectedPeopleElements.get(Constants_Post + _concurentPost);
							if(el){
								break;
							}
						}
					}
				}
				
				if(el){
					message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name));
					repeatingItem.add(Constants_Member + element.id);
					/*
					if(window.confirm(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name))){
						selectedPeopleElements.remove(Constants_Member + element.id);
						removeFromList3(Constants_Member + element.id);
					}
					*/
					continue;
				}
			}
			
			//检测该人的职务级别是否也被选择了
			if(_selectedPeopleTypes.containsKey(Constants_Level) && checkCanSelect(Constants_Level)){
				el = selectedPeopleElements.get(Constants_Level + member.levelId);
				if(el){
					message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name));
					repeatingItem.add(Constants_Member + element.id);
					/*
					if(window.confirm(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name))){
						selectedPeopleElements.remove(Constants_Member + element.id);
						removeFromList3(Constants_Member + element.id);
					}
					*/
					continue;
				}
			}
			
			//组
			var teamIndexes = _selectedPeopleTypes.get(Constants_Team);
			if(teamIndexes && checkCanSelect(Constants_Team)){
				for(var t = 0; t < teamIndexes.size(); t++) {
					el = _selectedPeopleElements.get(teamIndexes.get(t));
					if(el.type == Constants_Team){
						var memberList = topWindow.getObject(Constants_Team, el.id).getAllMemberIds();
						if(memberList && memberList.contains(member.id)){						
							message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name));
							repeatingItem.add(Constants_Member + element.id);
							/*
							if(window.confirm(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", el.name, member.name))){
								selectedPeopleElements.remove(Constants_Member + element.id);
								removeFromList3(Constants_Member + element.id);
							}
							*/
							break;//判断一个就够了
						}
					}
				}
			}
		}
	}
	
	if(!message.isEmpty()){
		var size = message.size();
		
		var messageStr = (message.subList(0, 10).toString("\n") + "\n\n" + topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_item", size));
		
		if(window.confirm(messageStr)){
			removeRepeatingItem(repeatingItem);
		}
		
		throw "continue";
	}
	
	function getData(){
		var selectedElements = selectedPeopleElements.values().toArray();
		if(!checkShowAccountShortname(selectedElements)){ //不显示单位简称
			for(var i = 0; i < selectedElements.length; i++) {
				selectedElements[i].accountShortname = null;
			}
			
			showAccountShortname = false;
		}
		for(var i = 0; i < selectedElements.length; i++) { //外部单位不显示单位简称。
	        if(selectedElements[i].type==Constants_ExchangeAccount)
			     selectedElements[i].accountShortname = null;
		}
		if(needlessPreReturnValueFun != false){
			var _preReturnValueFun = getParentWindowData("preReturnValueFun");
			if(_preReturnValueFun){
				var preSelectedElements = new Array();
				for(var i = 0; i < selectedElements.length; i++) {
					var el = selectedElements[i];
					preSelectedElements[i] = new Element();
					preSelectedElements[i].copy(el);
					preSelectedElements[i].entity = topWindow.getObject(el.type, el.id);
				}
				
				try{
					var preResult = null;
					eval("preResult = parentWindow.preReturnValueFun_" + spId + "(preSelectedElements)");
					if(preResult && preResult.length == 2 && preResult[0] == false){
						throw preResult[1]
					}
				}
				catch(e){
					throw e;
				}
			}
		}
		
		return selectedElements;
	}
	
	return getData();
}

/**
 * 检测已选择的数据中是否包含单位
 */
function checkIsContainAccount(){
	if(!checkCanSelect(Constants_Account) || selectedPeopleElements.size() < 2){
		return;
	}
	
	var _selectedPeopleElements = selectedPeopleElements.values();
	
	var message = new ArrayList();
	var repeatingItem = new ArrayList();
	
	for(var i = 0; i < _selectedPeopleElements.size(); i++) {
		var element = _selectedPeopleElements.get(i);
		
		if(element.type == Constants_Account){
			
			for(var k = 0; k < _selectedPeopleElements.size(); k++) {
				var el = _selectedPeopleElements.get(k);
				//AEIGHT-9496 20130606 lilong 客户BUG同时选择单位也可以同时选择单位组，tanmf同意修改
				if(el.type == Constants_Team) {
					continue;
				}
				if((el.type != Constants_Account && el.accountId == element.id) || (element.id == rootAccount.id && el.id != rootAccount.id)){
					var obj = topWindow.getObject(el.type, el.id);
					if(obj && obj.isInternal == false){
						continue;
					}
					
					message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", element.name, el.name));
					repeatingItem.add(el.type + el.id);
				}
			}
		}
	}
	
	if(!message.isEmpty()){
		var size = message.size();
		
		var messageStr = (message.subList(0, 10).toString("\n") + "\n\n" + topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_item", size));
		
		if(window.confirm(messageStr)){
			removeRepeatingItem(repeatingItem);
		}
		
		throw "continue";
	}
}

/**
 * 检测是否包含子部门
 */
function checkIsContainChildDepartment(){
	if(!checkCanSelect(Constants_Department) || selectedPeopleElements.size() < 2){
		return;
	}

	var message = new ArrayList();
	var repeatingItem = new ArrayList();
	
	var _selectedPeopleElements = selectedPeopleElements.values();
	for(var i = 0; i < _selectedPeopleElements.size(); i++) {
		var element = _selectedPeopleElements.get(i);
		
		// 数据中心不存在该单位的数据，说明没有改变
		if(!topWindow.dataCenter.containsKey(element.accountId)){
			continue;
		}
		
		if(element.type == Constants_Department){
			var obj = topWindow.getObject(element.type, element.id);
			if(!obj || obj.isInternal == false){
				continue;
			}
			
			var allParents = topWindow.findMultiParent(topWindow.getDataCenter(Constants_Department, element.accountId), element.id);
			if(!allParents || allParents.isEmpty()){
				continue;
			}
						
			for(var k = 0; k < allParents.size(); k++) {
				var entity = allParents.get(k);
				if(!entity || entity.id == element.id){
					continue;
				}
				
				var ancestor = selectedPeopleElements.get(Constants_Department + entity.id);
				// 选中其祖先且其祖先包含子部门
				if(ancestor && !ancestor.excludeChildDepartment){
					message.add(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", entity.name, element.name));
					repeatingItem.add(Constants_Department + element.id);
					
					/*
					if(window.confirm(topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_member", entity.name, element.name))){
						selectedPeopleElements.remove(Constants_Department + element.id);
						removeFromList3(Constants_Department + element.id);
					}
					*/
					
					continue;
				}
			}
		}
	}
	
	if(!message.isEmpty()){
		var size = message.size();
		
		var messageStr = (message.subList(0, 10).toString("\n") + "\n\n" + topWindow.v3x.getMessage("V3XLang.selectPeople_alert_contain_item", size));
		
		if(window.confirm(messageStr)){
			removeRepeatingItem(repeatingItem);
		}
		
		throw "continue";
	}
}

function removeRepeatingItem(repeatingItem){
	if(!repeatingItem){
		return;
	}
	
	for(var i = 0; i < repeatingItem.size(); i++) {
		var key = repeatingItem.get(i);
		
		selectedPeopleElements.remove(key);
		removeFromList3(key);
	}
}

/**
 * 回显原来的选人数据
 * 在父窗口的elements_${id}中
 */
function initOriginalData(){
	if(getParentWindowData("showOriginalElement") == false){
		return;
	}
	
	var originalData = getParentWindowData("elements");

	//该方法有具体jsp实现
	if(originalData){
		showOriginalDate(originalData);
	}
}

/**
 * 将elements回显到已选择区域
 */
function addElementsToList3(elements){
	if(!elements){
		return;
	}
	
	var _showAccountShortname = checkShowAccountShortname(elements);
	
	var disabledE = new ArrayList();
	var _toAccount = new Set();
	
	aaa:
	for(var i = 0; i < elements.length; i++) {
		var element = elements[i];
		
		var _accountId = element.accountId;
		var account = allAccounts.get(_accountId);
		if(account){
			element.accountShortname = account.shortname;
		
			//加载单位在数据
			if(!_toAccount.contains(_accountId)){
				_toAccount.add(_accountId);
				if(_accountId != currentAccountId){
					topWindow.initOrgModel(_accountId, currentMemberId);
				}
			}
		}
		
		if(element.isEnabled == false){
			disabledE.add(element.name);
			continue aaa;
		}
		else{
			var types = element.type.split(valuesJoinSep);
			var ids   = element.id.split(valuesJoinSep);
			
			for(var k = 0; k < types.length; k++) {
				if(types[k]!=Constants_FormField){
					var __element = topWindow.getObject(types[k], ids[k])
					if(__element == null || __element.isEnabled == false){
						disabledE.add(element.name);
						continue aaa;
					}
				}
			}
		}
		
		var key = element.type + element.id;
		
		add2List3(element);
		selectedPeopleElements.put(key, element);
	}
	
	if(!disabledE.isEmpty()){
		alert(_("V3XLang.selectPeople_disabledE", disabledE));
	}
	
	if(!_toAccount.isEmpty()){
		topWindow.initOrgModel(currentAccountId, currentMemberId);
	}
	
	if(_showAccountShortname == true && showAccountShortname == false){
		showAccountShortname = _showAccountShortname;
		mergeAccountShortnameOfSelected();
	}
}

/**
 * 是否显示单位简称
 * @return true 显示
 */
function checkShowAccountShortname(elements){
	if(isGroupEdition == 'false'){ //非集团版、政务多组织版，通通不显示单位简称
		return false;
	}
	
	var showAccountShortnameTemp = getParentWindowData("showAccountShortname");
	if(showAccountShortnameTemp == "yes"){
		return true;
	}
	else if(showAccountShortnameTemp == "no"){
		return false;
	}
	
	var _accountId = accountId;
	for(var i = 0; i < elements.length; i++) {
		var e = elements[i];
		if(_accountId && e.accountId != _accountId){
			return true;
		}
		
		_accountId = e.accountId;
	}
	
	return false;
}

/**
 * 从主窗口取到排除数据
 */
function initExcludeElements(){
	try {
		var originalElement = getParentWindowData("excludeElements");
		
		if(originalElement){
			for(var i = 0; i < originalElement.length; i++) {
				excludeElements.add(originalElement[i].type + originalElement[i].id);
			}
		}
	}
	catch (e) {
	}
}

/**
 * 取得主窗口的数据
 */
function getParentWindowData(_name){
	try{
		if(!parentWindow || !spId){
			return;
		}
		
		var data = null;
		// ,|分隔的数据
		eval("data = parentWindow." + _name + "_" + spId);
		
		return data;
	}
	catch(e){
		return null;
	}
}

function showQueryInput(){
	return true;
}

function showQueryInputOfDepartOrTerm(){
	return checkCanSelectMember();
}
function showQueryInputOfDepart(){
	if(getParentWindowData("showDepartmentMember4Search")){
		return true;
	}else{
		return selectTypes.contains(Constants_Member) || selectTypes.contains(Constants_Post);
	}
}

/**
 * 另存为组
 */
var saveAsTeamData = null;
function saveAsTeam(){
	try{
		saveAsTeamData = getSelectedPeoples(100, 2, false);
	}
	catch(e){
		if(e != 'continue'){
			alert(e);
		}
		return;
	}
	
	for(var i = 0; i < saveAsTeamData.length; i++){
		var m = saveAsTeamData[i];
		if(m.type != Constants_Member){
			alert(v3x.getMessage("V3XLang.selectPeople_saveAsTeam_alert_OnlnMember"));
			return;
		}
	}
	
	v3x.openWindow({
		url : genericControllerURL + "?ViewPage=common/SelectPeople/saveAsTeam",
		width : 360,
		height: 220
	});
	
	saveAsTeamData = null;
}

/**
 * 添加个人组
 * @param memberIds 逗号分隔的人员id
 */
function addPersonalTeam(teamId, teamName, members){
	if(members == null){
		return;
	}
	
	topWindow.addPersonalTeam(myAccountId, teamId, teamName, members);
	
	if(tempNowPanel.type == Constants_Team){
		initList(Constants_Team);
	}
}

function showDetailPost(){
	var postDataBodyObj = document.getElementById("PostDataBody");
	if(!postDataBodyObj || !postDataBodyObj.value){
		return;
	}
	
	var selected = v3x.openWindow({
		url : genericControllerURL + "?ViewPage=common/SelectPeople/showDetailPost",
		width : 550,
		height: 370
	});
	
	if(selected){
		tempNowSelected.clear();
		
		for(var i = 0; i < selected.length; i++) {
			var a = selected[i];
			var e = new Element(a[0], a[1], a[2], a[3], a[4], a[5], a[6]);
			tempNowSelected.add(e);
		}
		
		selectOne();
	}
}
/************************************div实现function****************************************/
//单击组、岗位显示该组人员列表
function selectList1ItemDiv(type, objId,objTD){
	tempNowSelected.clear();
	
	var ops = document.getElementById(objId).childNodes;
	for(var i = 0; i < ops.length; i++) {
		var option = ops[i];
		option.setAttribute('seleted','false');
		option.setAttribute('class','member-list-div');
	}
	objTD.setAttribute('seleted','true');
	objTD.setAttribute('class','member-list-div-select');
	var count = 0;
	var e = getElementFromOption(objTD);
	if(e){
		tempNowSelected.add(e);
		count++;
	}
	if(count == 1 && tempNowPanel.isShowMember == true){
		var id = objTD.getAttribute('value');
		showList2(type, id);
	}
	
	if(nowSelectedList1Item != null){
		nowSelectedList1Item = null;
	}
	
	nowSelectedList1Item = objTD;	
}
//单击人员列表改变背景 设置selectd 属性
function selectMemberFn(obj,temp_Id){
	if(!obj){return;}
	var seleted = obj.getAttribute('seleted');
	if(seleted == 'false'){
		obj.setAttribute('seleted','true');
		obj.setAttribute('class','member-list-div-select');
	}else{
		obj.setAttribute('seleted','false');
		obj.setAttribute('class','member-list-div');
	}
	if(temp_Id){temp_Div = temp_Id;}
}
//双击人员列表设置selected 属性 选择 人员
function selectOneMemberDiv(selectObj){
	if(!selectObj){
		return;
	}
	selectObj.setAttribute('seleted','true');
	var element = getElementFromOption(selectObj);
	if(element){
		tempNowSelected.clear();
		tempNowSelected.add(element);
		
		selectOne();
	}
}
//选中多个人员 一起选择过去
function listenermemberDataBodyDiv(object){
	if(object == null){return;}
	tempNowSelected.clear();
	var ops = object.childNodes;
	for(var i = 0; i < ops.length; i++) {
		var option = ops[i];
		if(option){
			if(option.getAttribute('seleted')=='true'){
				var e = getElementFromOption(option);
				if(e){
					tempNowSelected.add(e);
				}
				option.parentNode.removeChild(option);
				i--;
			}
		}
	}
}

/**
 * 检测跨密级访问，只要部门/组里面有任何一个人不能选择，则该部门/组不能选择
 * @return true 有权访问, false 无权访问
 */
function checkFlowSecretLevelScope(type, id){
	if(!isNeedCheckSecretScope){
		return true;
	}
	var flowSecretLevel = getParentWindowData("flowSecretLevel");
	if(!flowSecretLevel){
		return true;
	}
	if(flowSecretLevel  == 1){
		return true;
	}
	var members = null;
	
	// 部门
	if(type == Constants_Department){
		members = topWindow.getObject(type, id).getDirectMembers();
	}
	// 组、职务级别、岗位
	else if(type == Constants_Team || type == Constants_Level || type == Constants_Post){
		members = topWindow.getObject(type, id).getAllMembers();
	}else if( type == Constants_Department + "_" + Constants_Post){
		var ids = id.split("_");
		var types = type.split("_");
		
		var entity = topWindow.getObject(types[0], ids[0]);
		members =  new ArrayList();
		if(entity){
			var ms = entity.getAllMembers();
			if(ms){
				for(var i = 0; i < ms.size(); i++){
					var m = ms.get(i);
					if(m.postId == ids[1]){
						members.add(m);
					}	
				}
			}
		}
	}else if(type == Constants_Account){
		members =  new ArrayList();
		//获取全部人员
		var ms  = topWindow.getDataCenter(Constants_Member);
		for(var i = 0; i < ms.size(); i++){
			var m = ms.get(i);
			if(m.accountId == id){
				members.add(m);
			}	
		}
	}else{
		return true;
	}
	//检查人员的密级
	if(members){
		for(var i = 0; i < members.size(); i++) {
			if(!checkSecretLevelScope(members.get(i))){
				return false
			}
		}
	}
	
	return true;
}

/**
 * 检测秘密等级
 * 1、被检测人秘密等级低于 流程的密级，返回false
 * 
 * @member 要访问的人
 * @return true 有权访问, false 无权访问
 */
function checkSecretLevelScope(member){
	var flowSecretLevel = getParentWindowData("flowSecretLevel");
	if(flowSecretLevel){
		if(flowSecretLevel  == 1){
			return true;
		}
		var secretLevel = member.secretLevel;
		if(flowSecretLevel <= secretLevel){
			return true;
		}
		return false;
	}else{
		return true;
	}
}