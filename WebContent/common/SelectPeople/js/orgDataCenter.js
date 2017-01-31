/*----------------------------------------------------------------------------\
|                                Cross Panel 1.0                              |
|-----------------------------------------------------------------------------|
|                       Created by Tanmf (tanmf@seeyon.com)                   |
|                    For UFIDA-Seeyon (http://www.seeyon.com/)                |
|-----------------------------------------------------------------------------|
| A utility will be used for Organization Medol, use AJAX Tech. to load the   |
|data                                                                         |
|-----------------------------------------------------------------------------|
|                            Copyright (c) 2006 Tanmf                         |
|-----------------------------------------------------------------------------|
| Dependencies:                                                               |
|-----------------------------------------------------------------------------|
| 2006-09-20 | Original Version Posted.                                       |
|-----------------------------------------------------------------------------|
| Created 2006-09-20 | All changes are in the log above. | Updated 2006-08-20 |
\----------------------------------------------------------------------------*/

var orgDataCenterFlag = true;


var Constants_Account      = "Account";
var Constants_Department   = "Department";
var Constants_Team         = "Team";
var Constants_Post         = "Post";
var Constants_Level        = "Level";
var Constants_Member       = "Member";
var Constants_Role         = "Role";
var Constants_Outworker    = "Outworker";
var Constants_ExchangeAccount  	= "ExchangeAccount";
var Constants_concurentMembers 	= "ConcurentMembers";
var Constants_OrgTeam      		= "OrgTeam";
var Constants_RelatePeople      = "RelatePeople";
var Constants_FormField      	= "FormField";
var Constants_Admin        		= "Admin";


/****************************************************************
 * 单位
 * @param id
 * @param parentId 上级单位Id
 * @param name
 * @param hasChild 是否有子单位
 * @param shortname 单位简称
 * @param levelScope 职务级别限制
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

/**
 * @param id
 * @param parentId
 * @param name
 * @param hasChild
 * @param description
 * @param path 
 * @param concurents 兼职  "Concurent":{"-3416446029311948944":[{"DN":"销售部","A":"-7402591981046643031","PN":"职员","N":"李华表","id":"7798797857441336066"}]}
 * @param postList 部门下的岗位 ArrayList<Post.id>
 */
function Department(id, parentId, name, hasChild, path, postList, roleList, isInternal, concurents, description, accountId){
	this.id = id;
	this.parentId = parentId;
	this.name = name;
	this.hasChild = hasChild;	
	this.path = path;
	this.postList = postList;
	this.roleList = roleList;
	this.isInternal = isInternal == 0 ? false : true;
	this.concurents = concurents;
	this.description = description;
	this.accountId = accountId;
	
	//部门下所有的人员,包括子部门,以及副岗人员
	this.allMembers = null;
	this.allMembersMap = null; //<Member.id, Member>
	//部门下直接人员，包括副岗人员
	this.directMembers = new ArrayList();
	this.directMembersExist = {};
	//直接子部门
	this.directChildren = null;
	//所有的子部门
	this.allChildren = null;
	//兼职人员
	this.concurentMembers = null;
	
	//部门下的岗位
	this.Dposts = null;
	//部门下的角色
	this.Droles = null;
	
	//部门全名
	this.fullName = null;
}

Department.prototype.toString = function(){
	return this.name;
}

/**
 * 取得部门下的岗位列表
 * @return List<Post>
 */
Department.prototype.getPosts = function(){
	if(!this.postList){
		return null;
	}
	
	if(this.Dposts){
		return this.Dposts;
	}
	
	this.Dposts = new ArrayList();

	for(var i = 0; i < this.postList.size(); i++) {
		var postId = this.postList.get(i);
		var post = getObject(Constants_Post, postId);
		
		this.Dposts.add(post);
	}
	
	return this.Dposts;
}

/**
 * 得到部门下所有的角色
 */
Department.prototype.getRoles = function(){
	if(!this.roleList){
		return null;
	}
	
	if(this.Droles){
		return this.Droles;
	}
	
	this.Droles = new ArrayList();
	for(var i = 0; i < this.roleList.size(); i++) {
		var roleId = this.roleList.get(i);
		
		var role = getObject(Constants_Role, roleId);
		this.Droles.add(role);
	}
	
	return this.Droles;
}
/**
 * 部门下所有的人员,包括子部门,以及副岗人员,兼职人员
 */
Department.prototype.getAllMembers = function(){
	if(this.allMembers == null){
		this.allMembers = new ArrayList();
		
		var _departments = getDataCenter(Constants_Department, this.accountId);
		for(var i = 0; i < _departments.size(); i++){
			var d = _departments.get(i);
			if(!(d.isInternal == false) && checkIsChildDepartment(this.path, d.path)){
				this.allMembers.addList(d.getDirectMembers());
			}
		}
		
		this.allMembers.addList(this.getDirectMembers());
	}
	
	function checkIsChildDepartment(parentPath, childPath){
		return childPath.indexOf(parentPath + ".") == 0;
	}
	
	return this.allMembers;
}

/**
 * 得到该部门下所有人<Member.Id, Member>
 */
Department.prototype.getAllMembersMap = function(){
	if(this.allMembersMap == null){
		this.allMembersMap = new Properties();
		var _allMembers = this.getAllMembers();
		for(var i = 0; i < _allMembers.size(); i++) {
			var m = _allMembers.get(i);
			this.allMembersMap.put(m.id, m);
		}
	}
	
	return this.allMembersMap;
}

/**
 * 添加直接成员，包括副岗人员,兼职人员，如果重复，只显示一个，并且以主岗优先
 */
Department.prototype.addDirectMembers = function(member, isCheck){
	var isExist = false;
	if(isCheck == true){
		isExist = this.directMembersExist[member.id];
	}
	
	if(!isExist){ //不包含此人
		this.directMembers.add(member);
		this.directMembersExist[member.id] = true;
	}
}

/**
 * 得到部门的直接成员，包括副岗人员,兼职人员
 */
Department.prototype.getDirectMembers = function(){
	return this.directMembers;
}

/**
 * 得到直接子部门
 */
Department.prototype.getDirectChildren = function(){
	if(this.directChildren){
		return this.directChildren;
	}
	
	return new ArrayList();
}

/**
 * 
 */
Department.prototype.getAllChildren = function(){
	if(!this.allChildren){
		this.allChildren =  new ArrayList();
		
		var currentChildren = this.getDirectChildren();
		this.allChildren.addList(currentChildren);
		
		for(var i = 0; i < currentChildren.size(); i++) {
			this.allChildren.addList(currentChildren.get(i).getAllChildren());
		}
	}
	
	return this.allChildren;
}

/**
 * 得到兼职人员
 */
Department.prototype.getConcurents = function(){
	if(this.concurentMembers){
		return this.concurentMembers;
	}
	
	this.concurentMembers = new ArrayList();
	if(this.concurents){
		for(var i = 0; i < this.concurents.length; i++) {
			//{"DN":"销售部","A":"-7402591981046643031","PN":"职员","N":"李华表","id":"7798797857441336066","GL":"5744187978606337796"}
			var em = this.concurents[i];
			
			var member = getObject(Constants_concurentMembers, em[Constants_key_id]);
			var index = this.concurentMembers.indexOf(member);
			if(index < 0){
				this.concurentMembers.add(member);
			}
		}
	}
	
	return this.concurentMembers;
}

Department.prototype.getFullName = function(){
	if(this.fullName == null){
		this.fullName = this.name;
		
		var parentPath = getDepartmentParentPath(this.path);
		var parentDepartment = Path2Depart[parentPath];
		
		if(parentDepartment){
			var parentDepartmentFullName = parentDepartment.getFullName();
			this.fullName = parentDepartmentFullName + "/" + this.fullName;
		}
	}
	
	return this.fullName;
}

/****************************************************************
 * @param id
 * @param name
 * @param description
 */
function Post(id, name, type, code, description, accountId){
	this.id = id;
	this.name = name;
	this.type = type;
	this.code = code;
	this.description = description;
	this.accountId = accountId;
	
	this.members = new ArrayList();
	this.membersExist = {};
}

Post.prototype.getMembers = function(){
	return this.members;
}

Post.prototype.addMember = function(member, isCheck){
	var isExist = false;
	if(isCheck == true){
		isExist = this.membersExist[member.id];
	}
	
	if(!isExist){ //不包含此人
		this.members.add(member);
		this.membersExist[member.id] = true;
	}
}

Post.prototype.getAllMembers = function(){
	return this.members;
}

/****************************************************************
 * @param id
 * @param parentId
 * @param name
 * @param hasChild
 * @param sortId
 * @param groupLevelId 隐射到集团的职务级别id，不是sortId
 * @param description
 */
function Level(id, parentId, name, hasChild, sortId, groupLevelId, code, description, accountId){
	this.id = id;
	this.parentId = parentId;
	this.name = name;
	this.hasChild = hasChild;
	this.sortId = sortId;
	this.groupLevelId = groupLevelId;
	this.code = code;
	this.description = description;
	this.accountId = accountId;
	
	this.members = null;
}
Level.prototype.getMembers = function(){
	if(this.members){
		return this.members;
	}
	
	this.members = new ArrayList();

	var _members = getDataCenter(Constants_Member, this.accountId);
	for(var i = 0; i < _members.size(); i++){
		var member = _members.get(i);
		if(member.levelId == this.id){
			this.members.add(member);
		}
	}
	
	return this.members;
}

Level.prototype.getAllMembers = function(){
	return this.getMembers();
}

/**
 * @param id
 * @param name
 * @param description
 */
function Role(id, name, type, bond, description, accountId){
	this.id = id;
	this.name = name;
	this.type = type;
	this.bond = bond;
	this.description = description;
	this.accountId = accountId;
}

/****************************************************************
 * @param id
 * @param name
 * @param departmentId
 * @param postId
 * @param secondPostIds List<[Department.id, Post.id]>
 * @param levelId
 * @param email
 * @param mobile
 * @param description
 */
function Member(id, name, sortId, departmentId, postId, secondPostIds, levelId, _isInternal, email, mobile, description, accountId,secretLevel){
	this.id = id;
	this.name = name;
	this.sortId = parseInt(sortId);
	this.departmentId = departmentId;
	this.departmentName = "";//人员所在部门名称，用于全集团查询结果显示全路径
	this.postId = postId;
	this.secondPostIds = secondPostIds;
	this.levelId = levelId;
	this.isInternal = _isInternal == 0 ? false : true;
	this.email = email;
	this.mobile = mobile;
	this.description = description;
	this.accountId = accountId;
	this.secretLevel = secretLevel;
	
	//一下是Member的扩展属性，通常是在运算过程中赋值，之后不再重新赋值
	
	this.department = null;
	this.post = null;
	this.level = null;
	this.teams = null;
	this.secondPost = null; //Properties<departmentId, ArrayList<Post>>
}

Member.prototype.clone = function(){
	var newMember = new Member();
	newMember.id = this.id;
	newMember.name = this.name;
	newMember.sortId = this.sortId;
	newMember.departmentId = this.departmentId;
	newMember.postId = this.postId;
	newMember.secondPostIds = this.secondPostIds;
	newMember.levelId = this.levelId;
	newMember.isInternal = this.isInternal;
	newMember.email = this.email;
	newMember.mobile = this.mobile;
	newMember.description = this.description;
	newMember.accountId = this.accountId;
	newMember.secretLevel = this.secretLevel;
	
	//一下是Member的扩展属性，通常是在运算过程中赋值，之后不再重新赋值
	newMember.department = this.department;
	newMember.post = this.post;
	newMember.level = this.level;
	newMember.teams = this.teams;
	newMember.secondPost = this.secondPost;
	return newMember;
}

Member.prototype.getLevel = function(){
	if(this.level == null){
		this.level = getObject(Constants_Level, this.levelId);
	}
	
	return this.level;
}

Member.prototype.getDepartment = function(){
	if(this.department == null){
		this.department = getObject(Constants_Department, this.departmentId);
	}
	
	return this.department;
}

Member.prototype.getPost = function(){
	if(this.post == null){
		this.post = getObject(Constants_Post, this.postId);
	}
	
	return this.post;
}

Member.prototype.getSecondPost = function(){
	if(this.secondPost == null){
		if(this.secondPostIds){
			this.secondPost = new Properties();
			
			for(var i = 0; i < this.secondPostIds.size(); i++) {
				var dId = this.secondPostIds.get(i)[0];
				var pId = this.secondPostIds.get(i)[1];
				
				var p = getObject(Constants_Post, pId);
				if(p){
					var _posts = this.secondPost.get(dId);
					if(_posts == null){
						_posts = new ArrayList();
						this.secondPost.put(dId, _posts);
					}
					
					_posts.add(p);
				}
			}
		}
		else{
			this.secondPost = EmptyProperties;
		}
	}

	return this.secondPost;
}

/**
 * 判断当前人是否在指定部门兼职
 */
Member.prototype.isSecondPostInDept = function(departmentId){
	return this.getSecondPost().get(departmentId) != null;
}

/**
 * 根据兼职岗位得到我兼职部门
 */
Member.prototype.getSecondDepartmentId = function(postId){
	if(this.secondPostIds == null){
		return null;
	}
	
	for(var i = 0; i < this.secondPostIds.size(); i++) {
		var pId = this.secondPostIds.get(i)[1];
		
		if(pId == postId){
			return this.secondPostIds.get(i)[0];
		}	
	}
	
	return null;
}

Member.prototype.toString = function(){
	return "id=" + this.id + ", name=" + this.name + ", postId=" + this.postId + 
			", levelId=" + this.levelId + ", departmentId=" + this.departmentId;
}

/****************************************************************
 * @param id
 * @param type 类型 1 - 个人 2 - 系统组 3 - 项目组
 * @param name
 * @param teamSupervisors ArrayList<Member.id> 组的主管
 * @param teamMembers ArrayList<Member.id> 组的成员
 * @param teamLeaders ArrayList<Member.id> 组的领导
 * @param teamRelatives ArrayList<Member.id> 组的关联人员
 * @param description
 */
function Team(id, type, name, depId, teamLeaders, teamMembers, teamSupervisors, teamRelatives, externalMember, description, accountId){
	this.id = id;
	this.type = type;
	this.name = name;
	this.depId = depId;
	this.teamLeaders = teamLeaders; //主管
	this.teamMembers = teamMembers; //成员
	this.teamSupervisors = teamSupervisors; //领导
	this.teamRelatives = teamRelatives; //管理人员
	this.externalMember = externalMember || []; //外单位人员
	this.description = description;
	this.accountId = accountId || "";
	
	this.department = null;
	
	this.leaders = new ArrayList();
	this.members = new ArrayList();
	this.supervisors = new ArrayList();
	this.relatives = new ArrayList();
	
	this.isInit = false;
}

/**
 * 取得组所属部门
 */
Team.prototype.getDepartment = function(){
	if(!this.department){
		if(!this.depId || this.depId == -1){
			return null;
		}
	
		this.department = getObject(Constants_Department, this.depId);
	}
	
	return this.department;
}

/**
 * 初始化人员
 */
Team.prototype.initMembers = function(){
	if(this.isInit == true){
		return;
	}
	
	for(var i = 0; i < this.externalMember.length; i++) {
		var em = this.externalMember[i];
		var member = new Member(em[Constants_key_id], em["N"], 999999, null, null, null, null, true, em["Y"], em["M"], '');
		member.type = "E";
		member.departmentName = em["DN"] || "";
		member.accountId = em["A"];
		
		addExMember(member);
	}
	
	var temp = dataCenterMap[currentAccountId][Constants_Member] || {};
	
	for(var i = 0; i < this.teamLeaders.size(); i++) {
		var memberId = this.teamLeaders.get(i);
		var member = temp[memberId];
		
		if(member != null){
			this.leaders.add(member);
		}
	}
	
	for(var i = 0; i < this.teamMembers.size(); i++) {
		var memberId = this.teamMembers.get(i);
		var member = temp[memberId];
		
		if(member != null){
			this.members.add(member);
		}
	}
		
	for(var i = 0; i < this.teamSupervisors.size(); i++) {
		var memberId = this.teamSupervisors.get(i);
		var member = temp[memberId];
		
		if(member != null){
			this.supervisors.add(member);
		}
	}
	
	for(var i = 0; i < this.teamRelatives.size(); i++) {
		var memberId = this.teamRelatives.get(i);
		var member = temp[memberId];
		
		if(member != null){
			this.relatives.add(member);
		}
	}
	
	this.isInit = true;
}

/**
 * 得到成员（主管、成员）的Id
 */
Team.prototype.getAllMemberIds = function(){
	var allMemberIds = new ArrayList();
	allMemberIds.addList(this.teamLeaders);
	allMemberIds.addList(this.teamMembers);
	
	return allMemberIds;
}


/**
 * 所有的成员，包括主管、组员
 */
Team.prototype.getAllMembers = function(){
	this.initMembers();
	var allMembers = new ArrayList();

	allMembers.addList(this.leaders);
	allMembers.addList(this.members);
	
	return allMembers;
}

/**
 * 得到组的主管
 * @return List<Member>
 */
Team.prototype.getLeaders = function(){
	this.initMembers();
	return this.leaders;
}

/**
 * 得到组的成员
 * 
 * @return List<Member>
 */
Team.prototype.getMembers = function(){
	this.initMembers();
	return this.members;
}

/**
 * 得到组的领导
 * @return List<Member>
 */
Team.prototype.getSupervisors = function(){
	this.initMembers();
	return this.supervisors;
}

/**
 * 得到组的关联人员
 * @return List<Member>
 */
Team.prototype.getRelatives = function(){
	this.initMembers();
	return this.relatives;
}

function addExMember(member){
	try {
		var obj = dataCenterMap[currentAccountId][Constants_Member][member.id];
		if(!obj){
			getDataCenter(Constants_Member).add(member);
			dataCenterMap[currentAccountId][Constants_Member][member.id] = member;
		}
	}
	catch (e) {
		alert(e.message)
	}
}

/**************************************************************
 * 外部单位，用于公文交换
 */
function ExchangeAccount(id, name, description){
	this.id = id;
	this.name = name;
	this.isInternal = false;
	this.description = description;
}

/**************************************************************
 * 机构组，用于公文
 */
function OrgTeam(id, name, description){
	this.id = id;
	this.name = name;
	this.description = description;
}
OrgTeam.prototype.toString = function(){
	return this.id + ", " + this.name;
}

/****************************************************************
 * 关联人员
 */
function RelatePeople(type, name, description){
	this.type = type;
	this.name = name;
	this.description = description;
	
	this.memberOrginal = new ArrayList();
	
	this.members = null;
}

RelatePeople.prototype.addMember = function(o){
	this.memberOrginal.add(o);
}

RelatePeople.prototype.getMembers = function(){
	if(this.members == null){
		var st = new Date();
		this.members = new ArrayList();
		for ( var i = 0; i < this.memberOrginal.size(); i++) {
			var o = this.memberOrginal.get(i);
			var em = o["E"];
			if(em){
				var member = new Member(o["K"], em["N"], 999999, null, null, null, null, true, em["Y"], em["M"], '');
				member.type = "E";
				member.departmentName = em["DN"] || "";
				member.accountId = em["A"];
				
				addExMember(member);
				this.members.add(member);
			}
			else{			
				var member = dataCenterMap[currentAccountId][Constants_Member][o["K"]];
				if(member != null){
					this.members.add(member);
				}
			}
		}
	}
	
	return this.members;
}

RelatePeople.prototype.toString = function(){
	return this.id + ", " + this.name;
}

/*******************************************************************
 * 管理员
 */
function Admin(id, name, description){
	this.id = id;
	this.name = name;
	this.description = description;
}

Admin.prototype.toString = function(){
	return this.is + ", " + this.name;
}

/**
 * 表单控件
 * @param {} name
 * @param {} bindname
 */
function FormField(name, bindname){
	this.name = name;
	this.bindname = bindname;
}
FormField.prototype.toString = function(){
	return this.name + ", " + this.bindname;
}

/******************************************************************************************************/
/******************************************************************************************************/
var currentAccountId    = null;
var currentMemberId  = null;

var ajaxLoadOrganization = "ajaxSelectPeopleManager";
var Constants_key_id = "K";

var hasLoadOrgModel = new Properties();
var hasLoadExchangeAccountModel = new Properties();

//组织模型时间戳
var orgLocalTimestamp = new Properties();

/**
 * Properties<accountId, Properties<EntityType, List<Entity>>>
 * 
 * 该对象不要直接访问，请用getDataCenter(type)
 */
var dataCenter = new Properties();

/**
 * 所有的对象都在里面
 * 
 * {accountId, {EntityType, {id, Entity}}}
 */
var dataCenterMap = {};

//~ 所有的单位 <account.id, Account>
var allAccounts = new Properties();
var rootAccount = new Account();

/**
 * 职务级别 {accountId, {hashCode, level.id}}
 */
var levelHashCodeMap = {};
var departmentHashCodeMap = {};
var postHashCodeMap = {};

/**
 * 取得组织模型数据
 * 
 * @param type 类型，如果没有返回所有
 */
function getDataCenter(type, accountId){
	accountId = accountId || currentAccountId
	if(!hasLoadOrgModel.get(accountId)){
		return null;
	}
	
	if(type){
		var accountDataCenter = dataCenter.get(accountId);
		
		if(accountDataCenter){
			return accountDataCenter.get(type);
		}
	}
	
	return dataCenter.get(accountId);
}

/**
 * 根据类型和Id取得Object
 */
function getObject(type, id, accountId){
	if(type == Constants_Account){
		return allAccounts.get(id);
	}
	
	accountId = accountId || currentAccountId
	
	if(!hasLoadOrgModel.get(accountId)){
		return null;
	}
	
	var object = null;
	try {
		object = dataCenterMap[accountId][type][id]; //从当前的单位找
		if(object){
			return object;
		}
	}
	catch (e) {
	}
	
	for(var dataCenterItem in dataCenterMap) {
		if(dataCenterMap[dataCenterItem]){
			try{
				object = dataCenterMap[dataCenterItem][type][id]
			}catch(e){}
		}
		if(object){
			return object;
		}
	}
	
	return null;
}

function getObjects(type, id, accountId){
	if(type == Constants_Account){
		return allAccounts.get(id);
	}
	
	accountId = accountId || currentAccountId
	
	if(!hasLoadOrgModel.get(accountId)){
		return null;
	}
	
	var count = 0;
	var objects = [];
	try {
		var list = getDataCenter(type, accountId);
		for ( var i = 0; i < list.size(); i++) {
			if(list.get(i).id == id){
				objects[count++] = list.get(i)
			}
		}
	}
	catch (e) {
	}
	
	return objects;
}

//Map<Department.path, Department>
var Path2Depart = {};

//当前人是外部人员，这是他的工作范围[Department.path]
var ExternalMemberWorkScope = new ArrayList();

//当前人是内部人员，能访问哪些外部人员 <departmentId, List<memberId>>
var ExtMemberScopeOfInternal = new Properties();

/********************** Data Center *************************
 * Load based data
 *
 * 加载组织模型数据
 * 
 * @param accountId 需要加载单位
 */
function initOrgModel(accountId, memberId){
	if(accountId && memberId){
		currentAccountId = accountId;
		currentMemberId  = memberId;
	}
		
	this.invoke = function(){
		
	}
	
	var accountDataCenter = dataCenter.get(currentAccountId);
	
	var departments   = null; //部门
	var teams         = null; //组
	var posts         = null; //岗位
	var levels        = null; //职务级别
	var members       = null; //人员
	var roles         = null; //特殊角色
	var outworkers    = null; //外部人员
	var exchangeAccounts   = null; //外部单位，用于公文交换
	var orgTeams      	   = null; //机构组，用于公文
	var relatePeoples      = null; //关联人员
	var concurentMembers   = null; //兼职人员，只存人的Id和单位Id
	var admins			   = null; //管理员    
	var formFields         = null; //表单控件    
	
	if(!accountDataCenter){ //第一次加载
		accountDataCenter = new Properties();
		
		departments   = new ArrayList();
		teams         = new ArrayList();
		posts         = new ArrayList();
		levels        = new ArrayList();
		members       = new ArrayList();
		roles         = new ArrayList();
		outworkers    = new ArrayList();
		exchangeAccounts    = new ArrayList();
		orgTeams            = new ArrayList();
		relatePeoples       = new ArrayList();
		concurentMembers    = new ArrayList();
		admins       		= new ArrayList();
		formFields          = new ArrayList();
		
		accountDataCenter.put(Constants_Department, departments);
		accountDataCenter.put(Constants_Post, posts);
		accountDataCenter.put(Constants_Level, levels);
		accountDataCenter.put(Constants_Member, members);
		accountDataCenter.put(Constants_Team, teams);
		accountDataCenter.put(Constants_Role, roles);
		accountDataCenter.put(Constants_Outworker, outworkers);
		accountDataCenter.put(Constants_ExchangeAccount, exchangeAccounts);
		accountDataCenter.put(Constants_OrgTeam, orgTeams);
		accountDataCenter.put(Constants_RelatePeople, relatePeoples);
		accountDataCenter.put(Constants_concurentMembers, concurentMembers);
		accountDataCenter.put(Constants_Admin, admins);
		accountDataCenter.put(Constants_FormField, formFields);
	}
	else{
		departments   = accountDataCenter.get(Constants_Department);
		teams         = accountDataCenter.get(Constants_Team);
		posts         = accountDataCenter.get(Constants_Post);
		levels        = accountDataCenter.get(Constants_Level);
		members       = accountDataCenter.get(Constants_Member);
		roles         = accountDataCenter.get(Constants_Role);
		outworkers    = accountDataCenter.get(Constants_Outworker);
		exchangeAccounts    = accountDataCenter.get(Constants_ExchangeAccount);
		orgTeams            = accountDataCenter.get(Constants_OrgTeam);
		relatePeoples       = accountDataCenter.get(Constants_RelatePeople);
		concurentMembers    = accountDataCenter.get(Constants_concurentMembers);
		admins              = accountDataCenter.get(Constants_Admin);
		formFields          = accountDataCenter.get(Constants_FormField);
	}
	
	var departmentsMap   = null; //部门
	var teamsMap         = null; //组
	var postsMap         = null; //岗位
	var levelsMap        = null; //职务级别
	var membersMap       = null; //人员
	var rolesMap         = null; //特殊角色
	var outworkersMap    = null; //外部人员
	var exchangeAccountsMap   = null; //外部单位，用于公文交换
	var orgTeamsMap           = null; //机构组，用于公文交换
	var relatePeoplesMap	  = null; //关联人员
	var concurentMembersMap   = null; //兼职人员，只存人的Id和单位Id
	var adminsMap             = null; //管理员
	var formFieldsMap         = null; //表单控件
	
	var accountDataCenterMap = dataCenterMap[currentAccountId];
	
	if(!accountDataCenterMap){ //第一次加载
		accountDataCenterMap = {};
		
		departmentsMap   = {};
		teamsMap         = {};
		postsMap         = {};
		levelsMap        = {};
		membersMap       = {};
		rolesMap         = {};
		outworkersMap    = {};
		exchangeAccountsMap    = {};
		orgTeamsMap            = {};
		relatePeoplesMap       = {};
		concurentMembersMap    = {};
		adminsMap              = {};
		formFieldsMap              = {};
		
		accountDataCenterMap[Constants_Department] = departmentsMap;
		accountDataCenterMap[Constants_Post] = postsMap;
		accountDataCenterMap[Constants_Level] = levelsMap;
		accountDataCenterMap[Constants_Member] = membersMap;
		accountDataCenterMap[Constants_Team] = teamsMap;
		accountDataCenterMap[Constants_Role] = rolesMap;
		accountDataCenterMap[Constants_Outworker] = outworkersMap;
		accountDataCenterMap[Constants_ExchangeAccount] = exchangeAccountsMap;
		accountDataCenterMap[Constants_OrgTeam] = orgTeamsMap;
		accountDataCenterMap[Constants_RelatePeople] = relatePeoplesMap;
		accountDataCenterMap[Constants_concurentMembers] = concurentMembersMap;
		accountDataCenterMap[Constants_Admin] = adminsMap;
		accountDataCenterMap[Constants_FormField] = formFieldsMap;
	}
	else{
		departmentsMap   = accountDataCenterMap[Constants_Department];
		teamsMap         = accountDataCenterMap[Constants_Team];
		postsMap         = accountDataCenterMap[Constants_Post];
		levelsMap        = accountDataCenterMap[Constants_Level];
		membersMap       = accountDataCenterMap[Constants_Member];
		rolesMap         = accountDataCenterMap[Constants_Role];
		outworkersMap    = accountDataCenterMap[Constants_Outworker];
		exchangeAccountsMap    = accountDataCenterMap[Constants_ExchangeAccount];
		orgTeamsMap            = accountDataCenterMap[Constants_OrgTeam];
		relatePeoplesMap       = accountDataCenterMap[Constants_RelatePeople];
		concurentMembersMap    = accountDataCenterMap[Constants_concurentMembers];
		adminsMap              = accountDataCenterMap[Constants_Admin];
		formFieldsMap          = accountDataCenterMap[Constants_FormField];
	}
	
	var levelHashCodes = levelHashCodeMap[currentAccountId];
	if(levelHashCodes == null){
		levelHashCodes = {};
		levelHashCodeMap[currentAccountId] = levelHashCodes;
	}
	var departmentHashCodes = departmentHashCodeMap[currentAccountId];
	if(departmentHashCodes == null){
		departmentHashCodes = {};
		departmentHashCodeMap[currentAccountId] = departmentHashCodes;
	}
	var postHashCodes = postHashCodeMap[currentAccountId];
	if(postHashCodes == null){
		postHashCodes = {};
		postHashCodeMap[currentAccountId] = postHashCodes;
	}

	try {
		var isNeedInitDepartment = false;
		var isNeedInitMember = false;
		
//		var startTime = new Date().getTime();
		var requestCaller = new XMLHttpRequestCaller(this, ajaxLoadOrganization, "getOrgModel", false, "GET");
		requestCaller.returnValueType = "TEXT";
		requestCaller.filterLogoutMessage = false;
		requestCaller.addParameter(1, "String", orgLocalTimestamp.get(currentAccountId, ""));
		requestCaller.addParameter(2, "long", currentAccountId);
		requestCaller.addParameter(3, "long", currentMemberId);
		var result0 = requestCaller.serviceRequest();
		
		if(!result0 || result0.startsWith("[LOGOUT]")){
			alert(result0.substring(8));
			return false;
		}
		
		var result = null;
		eval("result = " + result0 + ";");
		if(!result){
			return;
		}
//		log.debug("Load and eval : " + (new Date().getTime() - startTime) + "ms");
		
		orgLocalTimestamp.put(currentAccountId, result["timestamp"]); //将本地时间戳更新为新的时间戳 格式为Member=234123;Department=3245243
		
		// 单位
		var _accounts = result["Account"];
		if(_accounts){
			allAccounts.clear();
			
			for(var i = 0; i < _accounts.length; i++) {
				var d = _accounts[i];
				var id = d[Constants_key_id];
				var isRoot = d['R'];
				
				//Account(id, parentId, name, hasChild, shortname, levelScope, description)
				var account = new Account(id, d["P"], d["N"], d["C"], d["S"], d["L"], "");
				
				allAccounts.put(id, account);
				
				if(isRoot){
					rootAccount = isRoot;
				}
			}
		}
		
		//兼职 key 部门id, value Members
		var concurents = result["Concurent"];
		if(concurents != null){
			concurentMembers.clear();
			for(var dpid in concurents) {
				var ems = concurents[dpid];
				if(ems){
					for(var i = 0; i < ems.length; i++) {
						var em = ems[i];
						var id = em[Constants_key_id];
						
						var member = new Member(id, em["N"], em["S"], dpid, em["P"], null, em["L"], true, em["Y"], em["M"], '');
						member.type = "E";
						member.accountId = em["A"];
						member.departmentName = em["DN"] || "";
						
						concurentMembers.add(member);
						concurentMembersMap[id] = member;
					}
				}
			}
			isNeedInitMember = true;
		}
		else{
			concurents = {};
		}
		
//		startTime = new Date().getTime();
		var _posts = result[Constants_Post];
		if(_posts){
			posts.clear();
			for(var i = 0; i < _posts.length; i++) {
				var d = _posts[i];
				var id = d[Constants_key_id];
				var post = new Post(id, d["N"], d["T"], d["C"], "", currentAccountId);
				posts.add(post);
				
				postsMap[id] = post;
				
				postHashCodes[d["H"]] = id;
			}
		}
//		log.debug("posts : " + (new Date().getTime() - startTime) + "ms");

//		startTime = new Date().getTime();
		var _levels = result[Constants_Level];
		if(_levels){
			levels.clear();
			for(var i = 0; i < _levels.length; i++) {
				var d = _levels[i];
				var id = d[Constants_key_id];
				var level = new Level(id, "", d["N"], true, d["S"], d["G"], d["C"], "", currentAccountId);
				levels.add(level);
				levelsMap[id] = level;
				
				levelHashCodes[d["H"]] = id;
			}
		}
//		log.debug("levels : " + (new Date().getTime() - startTime) + "ms");

		//部门角色
		var departmentRoleIds = new ArrayList();

		var _roles = result[Constants_Role];
		if(_roles){
			roles.clear();
			for(var i = 0; i < _roles.length; i++) {
				var d = _roles[i];
				var id   = d["N"];
				var type = d["T"];
				var name = d["N"];
				var bond = d["B"];
				
				/**
				 * 1 固定角色 AccountManager AccountAdmin account_exchange account_edoccreate FormAdmin HrAdmin ProjectBuild DepManager DepAdmin department_exchange
				 * 2 相对角色 Sender SenderDepManager SenderSuperManager NodeUserDepManager NodeUserSuperManager
				 * 3 用户自定义角色
				 * 4 插件角色
				 */
				if(id.indexOf("_") > -1){
					continue;
				}
				
				if(type == 3 || type == 4){
					type = 1;
				}
				
				name = Constants_Component.get(name) || name;
				
				var role = new Role(id, name, type, bond, "", currentAccountId);
				roles.add(role);
				rolesMap[id] = role;
				
				if(bond == 2){
					departmentRoleIds.add(id);
				}
			}
		}
		//管理员
		var _admins = result[Constants_Admin];
		if(_admins){
			admins.clear();
			for(var i = 0; i < _admins.length; i++){
				var a = _admins[i];
				
				var id = a[Constants_key_id];
				var name = a["N"];
				
				var admin = new Admin(id, name, "");
				admins.add(admin);
				adminsMap[id] = admin;
			} 
		}

//		startTime = new Date().getTime();
		var _department = result[Constants_Department];
		if(_department){
			Path2Depart = {};
			outworkers.clear();
			departments.clear();
			for(var i = 0; i < _department.length; i++) {
				var d = _department[i];
				
				var id = d[Constants_key_id];
				
				isNeedInitDepartment = true;
				var depPosts = new ArrayList();
				var S = d["S"];
				if(S){
					for(var l = 0; l < S.length; l++) {
						depPosts.add(postHashCodes[S[l]]);
					}
				}
				
				//兼职人员
				var concurentMembersOfDepart = concurents[d[Constants_key_id]];
				
				//Department(id, parentId, name, hasChild, path, postList, description)
				var path = d["P"];
				var depart = new Department(id, null, d["N"], false, path, depPosts, departmentRoleIds, d["I"], concurentMembersOfDepart, "", currentAccountId);
				
				if(depart.isInternal){ //内部部门
					departments.add(depart);
					Path2Depart[path] = depart;
				}
				else{
					outworkers.add(depart);
					outworkersMap[id] = depart;
				}
				
				departmentsMap[id] = depart;
				departmentHashCodes[d["H"]] = id;
			}
		}
//		log.debug("departments : " + (new Date().getTime() - startTime) + "ms");
		
//		startTime = new Date().getTime();
		var _members = result[Constants_Member];
		if(_members){
			members.clear();
			
			for(var c = 0; c < concurentMembers.size(); c++) {
				var member = concurentMembers.get(c);
				members.add(member);
				membersMap[member.id] = member;
			}
			
			for(var i = 0; i < _members.length; i++) {
				var d = _members[i];
				var id = d[Constants_key_id];
				//Member(id, name, departmentId, postId, secondPostIds, levelId, _isInternal, email, mobile, description)
				var deptId = departmentHashCodes[d["D"]];
				var levelId = levelHashCodes[d["L"]];
				var postId = postHashCodes[d["P"]];

				//处理人员的副岗信息
				var secondPostIds = null;
				var SP = d["F"];
				
				//判断副岗中的部门是否在当前登陆用户的部门权限范围中
				var existInDepScope = false;
				
				if(SP){
					secondPostIds = new ArrayList();
					for(var s = 0; s < SP.length; s++) {
						var secondPostId = new Array();
						secondPostId[0] = departmentHashCodes[SP[s][0]];
						
						//人员副岗对应的部门在当前登陆用户的部门权限范围内找不到，继续处理下一个副岗
						if( !secondPostId[0] ){
							continue;
						}
						
						secondPostId[1] = postHashCodes[SP[s][1]];
						secondPostIds.add(secondPostId);
						
						//将existInDepScope标记为true，说明在人员的副岗中至少有一个部门在当前登陆用户的部门权限范围内
						if( !existInDepScope ){
							existInDepScope=true;
						}
					}
				}
				else{
					secondPostIds = EmptyArrayList;
				}
				
				//如果人员所属部门、副岗中的部门ID在当前登陆用户的部门权限范围中都不存在，处理下一个人员
				if(!deptId && !existInDepScope ){
					continue;
				}
				
				var member = new Member(id, d["N"], d["S"], deptId, postId, secondPostIds, levelId, d["I"], d["Y"], d["M"], "", currentAccountId,d["SL"]);
				members.add(member);
				membersMap[id] = member;
			}
			
			isNeedInitMember = true;
		}
//		log.debug("members : " + (new Date().getTime() - startTime) + "ms");		

		var _teams = result[Constants_Team];
		if(_teams){
			teams.clear();
			for(var i = 0; i < _teams.length; i++) {
				var d = _teams[i];
				var id = d[Constants_key_id];
				var teamLeaders = new ArrayList();
				teamLeaders.addAll(d["L"]);
				var teamMembers = new ArrayList();
				teamMembers.addAll(d["M"]);
				var teamSupervisors = new ArrayList();
				teamSupervisors.addAll(d["S"]);
				var teamRelatives = new ArrayList();
				teamRelatives.addAll(d["RM"]);
			
				var team = new Team(id, d["T"], d["N"], d["D"], teamLeaders, teamMembers, teamSupervisors, teamRelatives, d["E"], "", d["A"]);
				teams.add(team);
				
				teamsMap[id] = team;
			}
		}

		var _exchangeAccounts = result[Constants_ExchangeAccount];
		if(_exchangeAccounts){
			exchangeAccounts.clear();
			for(var i = 0; i < _exchangeAccounts.length; i++) {
				var d = _exchangeAccounts[i];
				var id = d[Constants_key_id];
				
				var exchangeAccount = new ExchangeAccount(id, d["N"], "");
				exchangeAccounts.add(exchangeAccount);
				exchangeAccountsMap[id] = exchangeAccount;
			}
		}
		
		var _orgTeams = result[Constants_OrgTeam];
		if(_orgTeams){
			orgTeams.clear();
			for(var i = 0; i < _orgTeams.length; i++) {
				var d = _orgTeams[i];
				var id = d[Constants_key_id];
				
				var orgTeam = new OrgTeam(id, d["N"], "");
				orgTeams.add(orgTeam);
				orgTeamsMap[id] = orgTeam;
			}
		}
		
		var _relatePeoples = result[Constants_RelatePeople];
		if(_relatePeoples){
			relatePeoples.clear();
			var leader = new RelatePeople(1, PeopleRelate_TypeName[1]); // 领导
			var assistant = new RelatePeople(2, PeopleRelate_TypeName[2]); // 秘书
			var junior = new RelatePeople(3, PeopleRelate_TypeName[3]); // 下级
			var confrere = new RelatePeople(4, PeopleRelate_TypeName[4]); //同事
			
			relatePeoples.add(leader);
			relatePeoples.add(assistant);
			relatePeoples.add(junior);
			relatePeoples.add(confrere);
			
			relatePeoplesMap[1] = leader;
			relatePeoplesMap[2] = assistant;
			relatePeoplesMap[3] = junior;
			relatePeoplesMap[4] = confrere;
			
			for(var i = 0; i < _relatePeoples.length; i++) {
				var d = _relatePeoples[i];
				
				relatePeoplesMap[d["T"]].addMember(d);
			}
		}
		
		//表单控件
		var _formFields = result[Constants_FormField];
		formFields.clear();
		if(_formFields){
			for(var i = 0; i < _formFields.length; i++) {
				var f = _formFields[i];
				
				var formField = new FormField(f["N"], f["DN"]);
				formFields.add(formField);
				formFieldsMap[d["N"]] = formField;
			}
		}

		//内部人员：可以访问哪些外部人员
		var _ExtMemberScopeOfInternal = result["ExtMemberScopeOfInternal"];
		if(_ExtMemberScopeOfInternal){
			ExtMemberScopeOfInternal.clear();
			
			for(var extDepatId in _ExtMemberScopeOfInternal) {
				var extMemberIds = new ArrayList();
				extMemberIds.addAll(_ExtMemberScopeOfInternal[extDepatId]);
				
				ExtMemberScopeOfInternal.put(extDepatId, extMemberIds);
			}
		}
		
		//外部人员：可以访问哪些内部人员
		var _ExternalMemberWorkScope = result["ExternalMemberWorkScope"];
		if(_ExternalMemberWorkScope){
			ExternalMemberWorkScope.clear();
			
			for(var i = 0; i < _ExternalMemberWorkScope.length; i++) {
				var ws = _ExternalMemberWorkScope[i];
				if(ws.indexOf("D") == 0){
					var dId = ws.substring(1);
					var d1 = departmentsMap[departmentHashCodes[dId]];
					if(d1){
						ExternalMemberWorkScope.add("D" + d1.path);
					}
				}
				else if(ws.indexOf("M") == 0){
					ExternalMemberWorkScope.add(ws);
				}
				else if(ws.indexOf("A") == 0){
					ExternalMemberWorkScope.clear();
					ExternalMemberWorkScope.add("0");
					break;
				}
			}
		}
		
		if(isNeedInitDepartment){
			intiDepartmentParentId(departments, Path2Depart);
		}

		//将外部部门添加到内部部门
		departments.addList(outworkers);
		for(var i = 0; i < outworkers.size(); i++) {
			var d = outworkers.get(i);
			var parentPath = getDepartmentParentPath(d.path);
			var parentDep = Path2Depart[parentPath];
			
			if(parentDep != null){
				d.parentId = parentDep.id;
				d.parentDepartment = parentDep;
			}
			else{
				d.parentId = currentAccountId;
			}	
		}
		
		if(isNeedInitMember){
			if(!concurentMembers.isEmpty()){//包含兼职时才进行全体排序
				QuickSortArrayList(members, "sortId");
			}
			
			var lawlevel = levels.getLast();
			
			for(var i = 0; i < members.size(); i++){
				var member = members.get(i);
				
				var levelId = member.levelId ? member.levelId : lawlevel != null ? lawlevel.id : null;
				if(levelId){
					member.level = levelsMap[levelId];
				}
				
				var d = departmentsMap[member.departmentId];
				if(d){
					d.addDirectMembers(member, false);
				}
				else{
					//alert(member.name + "的主岗部门[" + member.departmentId + "]不存在")
				}
				
				if(member.isInternal && member.postId != null){
					var p = postsMap[member.postId];
					if(p){
						p.addMember(member, true);
						member.post = p;
					}
					else{
						//alert(member.name + "的主岗[" + member.postId + "]不存在")
					}
				}
				
				var sp = member.secondPostIds; //副岗信息
				if(sp && !sp.isEmpty()){
					for(var k = 0; k < sp.size(); k++) {
						var dId = sp.get(k)[0]; //副岗所在部门
						var d1 = departmentsMap[dId];
						var pId = sp.get(k)[1]; //副岗岗位
						var p1 = postsMap[pId];
						if(d1){
							var newMember = member.clone();
							newMember.postId = pId;
							newMember.post = p1;
							newMember.departmentId = dId;
							newMember.department = d1;
							newMember.type = "F";
							d1.addDirectMembers(newMember, false);
							
							if(p1 && member.isInternal){
								p1.addMember(newMember, true);
							}
						}
					}
				}
			}
		}
	}
	catch (ex1) {		
		alert("loadOrgModel() Exception : " + ex1.message);
	}
		
	dataCenter.put(currentAccountId, accountDataCenter);
	dataCenterMap[currentAccountId] = accountDataCenterMap;
	
	hasLoadOrgModel.put(currentAccountId, true);
}

/**
 * 根据path对部门的parent进行重新调整
 */
function intiDepartmentParentId(departments, Path2Depart){
	for(var i = 0; i < departments.size(); i++){
		var depart = departments.get(i);
		var parentPath = getDepartmentParentPath(depart.path);
		var parentDepart = Path2Depart[parentPath];
		
		if(parentDepart){
			depart.parentId = parentDepart.id;
			
			parentDepart.hasChild = true;		
			if(parentDepart.directChildren == null){
				parentDepart.directChildren = new ArrayList();
			}
			
			parentDepart.directChildren.add(depart);
		}
		else{
			depart.parentId = currentAccountId;
		}		
	}
}

function getDepartmentParentPath(path){
	return path.substring(0, path.lastIndexOf("."));
}



/**
 * 直接子节点
 * ???? E (id, parentId)
 * @param list ArrayList<E> ?????? ????????????????parentId????
 * @param parentId ??Id
 * @return ArrayList<E> ????????????????
 */
function findChildInList(list, parentId) {
	var temp = new ArrayList();
	if(list == null){
		return temp;
	}

	for(var i = 0; i < list.size(); i++){
		if(list.get(i).parentId == parentId){
			temp.add(list.get(i));
		}
	}

	return temp;
}

/**
 * 所有子节点，包括孙子等
 */
function findAllChildInList(list, parentId) {
	var temp = new ArrayList();
	if(list == null){
		return temp;
	}
	
	for(var i = 0; i < list.size(); i++){
		var obj = list.get(i);
		if(obj.parentId == parentId){
			temp.add(obj);
			
			var cList = findAllChildInList(list, obj.id);
			temp.addList(cList);
		}
	}

	return temp;
}

/**
 * ????ID????????????????????????????????????
 * ???? E (id)
 * @param list ArrayList<E> ?????? ????????????????parentId????
 * @param id 
 * @return E ????
 */
function findObjectById(list, id){
	if(!list){
		return null;
	}
	
	for(var i = 0; i < list.size(); i++){
		if(list.get(i).id == id){
			return list.get(i);
		}
	}

	return null;
}
/**
 * ���չؼ����������
 * @param list ArrayList<E>
 * @param keyword Stirng
 * @return tempList ArrayList<E>
 */
function findObjectsLikeName(list, keyword){
	var tempList = new ArrayList();
	
	if(!list){
		return tempList;
	}
	for(var i = 0; i < list.size(); i++){
		if(list.get(i).name.indexOf(keyword) != -1){
			tempList.add(list.get(i));
		}
	}

	return tempList;
}

/**
 * find parent Object
 * E (id)
 * @param list ArrayList<E> There is the attribute 'parentId' and 'id';
 * @param id current object's id
 * @return E 
 */
function findParent(list, id){
	if(!list){
		return null;
	}
	
	var currentObject = findObjectById(list, id);
	
	if(currentObject == null){
		return null;
	}

	return findObjectById(list, currentObject.parentId);
}
/**
 * find Mult-Level parent Objects
 * @param list ArrayList<E> There is the attribute 'parentId' and 'id';
 * @param id current object's id
 * @param tempNodes ArrayList empty
 * @return ArrayList the first item is the top parent object
 */
function findMultiParent(list, id, tempNodes){
	if(!list){
		return null;
	}
	
	var parentObject = findParent(list, id);
	
	if(tempNodes == null){
		tempNodes = new ArrayList();
	}
	
	if(parentObject != null){//���ڸ��ڵ�
		tempNodes.add(parentObject);
		
		findMultiParent(list, parentObject.id, tempNodes);
	}
	
	var returnNodes = new ArrayList();
	
	for(var i = tempNodes.size() - 1; i > -1; i--){
		returnNodes.add(tempNodes.get(i));
	}

	return returnNodes;
}

/**
 * 从list中查找符合对象某个属性值的对象，只取一个
 */
function findByProperty(list, propertyName, propertyValue){
	if(!list){
		return null;
	}
	
	for(var i = 0; i < list.size(); i++){
		if(list.get(i)[propertyName] == propertyValue){
			return list.get(i);
		}
	}

	return null;
}

function addPersonalTeam(myAccountId, teamId, teamName, members){
	var teamMemberIds = new ArrayList();
	var e = []; //外部人员信息
	for(var i = 0; i < members.length; i++) {
		var member = members[i];

		var memberId = member.id;
		var accountId = member.accountId;
		
		if(accountId != myAccountId){ //外单位的
			var m = getObject(Constants_Member, memberId, accountId);
			var departmentName = null;
			if(m){
				departmentName = getObject(Constants_Department, m.departmentId, accountId).name;
			}
			
			e[e.length] = {
				"K" : memberId,
				"N"  : member.name,
				"DN" : departmentName,
				"A"  : accountId
			};
		}
		
		teamMemberIds.add(memberId);
	}
	
	var team = new Team(teamId, 1, teamName, -1, new ArrayList(), teamMemberIds, new ArrayList(), new ArrayList(), e, "");

	getDataCenter(Constants_Team, myAccountId).add(team);
	dataCenterMap[myAccountId][Constants_Team][teamId] = team;
}

