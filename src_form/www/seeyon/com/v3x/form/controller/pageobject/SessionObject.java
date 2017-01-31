package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.menuexec.menuObj;
import www.seeyon.com.v3x.form.controller.query.QueryObject;
import www.seeyon.com.v3x.form.controller.report.ReportObject;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.engine.infopath.InfoPathObject;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DataSource;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.RelationCondition;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.DataDefine;
import www.seeyon.com.v3x.form.manager.define.data.base.FormTable;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource.IDataGroup;
import www.seeyon.com.v3x.form.manager.define.form.dataformat.Format;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.manager.define.trigger.FormEvent;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;
import com.seeyon.v3x.common.filemanager.Attachment;

public class SessionObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3979155949305720678L;
	/**用于页面显示
	 * baseinfo
	 * inputdata
	 * operconfig
	 */
	private String pageflag;
	// baseinfo
	private transient DataDefine data;

	// baseinfo xsn附件存放路径
	private String xsnpath;

	// baseinfo 列表中所有字段的总数
	private int tablefieldsize;

	// 所属分类
	private Long formsort;
	
//	 所属分类
	private Long oldformsort;

	// 所属人名称
	private String attachManName;

	// 所属人ID
	private String attachManId;
	
	//附件 added by Meixd 2010-10-21
	private transient Attachment attachment;
	
	private String formstate = "0";
	
	//表单是否被启用 0为停用，1位启用
	private String formstart = "0";
	//表单ID
	private Long formid;
	private int formType=1;
	// baseinfo 列表中所有字段 用于生成bindschema.xml
	private transient List<TableFieldDisplay> TableFieldList = new ArrayList<TableFieldDisplay>();

	// 对象
	private transient SeeyonDataDefine seedatadefine;

	// 例：my.
	private String namespace;

	// xsf对象
	private transient InfoPathObject xsf;

	// input list
	private transient List FieldInputList = new ArrayList();

	// 表单名称
	private String FormName;
	private String FormEditName;
	private String formCode;

	// 新增还是修改区分的标志，默认是"新增",如果修改,则赋值为"edit"
	private String editflag;

	// 存放allxml中的FormList 放置FormPage对象
	private transient List<FormPage> formLst = new ArrayList<FormPage>();


	// 修改时后台组织的旧数据
	private transient List oldmaertlst = new ArrayList();

	private transient List oldslavelst = new ArrayList();
	
	private transient List tablelist = new ArrayList();

	//系统变量
	private transient List<String> sysVariable = new ArrayList<String>();
	//扩展绑定
	private transient List<String> inputExtend = new ArrayList<String>();
	//关联对象
	private transient Set<String> refInputNames = new LinkedHashSet<String>();
	//关联外部写入对象
	private transient Set<String> outwriteInputNames = new LinkedHashSet<String>();
	//绑定枚举  list中的摆放位置 0:系统枚举 1:应用枚举 2:表单枚举
	private transient List enumlist = new ArrayList();
	// 修改时作比较的新字段

	private transient List NewTableFieldList = new ArrayList();

	//修改时已匹配数据列表中相等字段的(新表名/旧表名↗新字段名,新表名/旧表名↗新字段名)
	private String matchdatavalue ;
  
	//修改时已匹配数据列表中不相等字段的(新表名↖旧表名/新字段名↗旧字段名,新表名↖旧表名/新字段名↗旧字段名)
	private String noequalvalue;
	
	//修改时删除表的表名(Infopath中的表名↗数据库中表名)
	private transient List delTablename = new ArrayList();
    //修改时匹配数据中的删除字段（删除字段名,）
	private String deletevalue;
    //修改时字段名有修改的(旧字段名,新字段名)
	private transient HashMap fieldMap = new HashMap();
	//修改时新增表的表名(Infopath中的表名↗数据库中表名)
	private transient List addtablename = new ArrayList();
	//修改时infopath表名修改的(旧表名,新表名)
	private transient HashMap edittableMap = new HashMap();
	//查询条件list
	private transient List<QueryObject> queryConditionList = new ArrayList<QueryObject>();
	
	//用于表单状态，供operconfig页面调用
	private transient List<String> formStateList = new ArrayList<String>();
	//用于页面控制select，供operconfig页面调用
	private String selenumattr = "0";
	//模板对象 供绑定信息使用
	private transient TemplateObject templateobj;	
	//统计条件设置list
	private transient List<ReportObject> reportConditionList = new ArrayList<ReportObject>();

	private transient List<ISeeyonQuery> querylist = new ArrayList<ISeeyonQuery>();
	
	private transient List<ISeeyonReport> reportlist = new ArrayList<ISeeyonReport>();
    
	//用于表单查询时的查询操作
	private transient List<FormAppMain> queryseachlist = new ArrayList<FormAppMain>(); 
	
	//用于表单统计时的查询操作
	private transient List<FormAppMain> reportseachlist = new ArrayList<FormAppMain>(); 
	
	//用于存储添加的枚举值所属枚举
	private transient HashMap enumvaluemap = new HashMap();
	
    //用于存储添加的枚举列表
	private transient HashMap enumlistmap = new HashMap();

//	菜单绑定的菜单列表	
	private transient List<menuObj> menulist = new ArrayList<menuObj>();
	
	private transient HashMap oldenumnamemap = new HashMap();
	
	private transient HashMap enumnamemap = new HashMap();
	
	private transient HashMap viewWidthvalue = new HashMap();
	
	private String othersave;
	
	private transient List flowidlist = new ArrayList();
	
	private transient List oldflowidlist= new ArrayList(); 
	
	private transient List logFieldList=new ArrayList();
	private transient String logfieldString="";
	
	private transient List uniqueFieldList=new ArrayList();
	private transient String uniqueFieldString="";
	
	private transient List<Long> flowIdListForInputData = new ArrayList<Long>();
	
	private transient List<Long> oldFlowIdListForInputData = new ArrayList<Long>();

	//应用授权列表
	private transient Map<String,FormAppAuthObject> formAppAuthObjectMap = new LinkedHashMap<String,FormAppAuthObject>();
	//旧的菜单名称
//	private transient Set<Long> oldAppBindMenuId = new HashSet<Long>();
    //当前表单创建的一级菜单
//    private transient Set<String> rootMenus = new HashSet<String>();
	//保存基础数据授权-- 只存在一条数据
	private transient FormAppAuthObject appAuthObject = new FormAppAuthObject();
	
	private transient Map<String,Format> dataFormat= new HashMap<String,Format>(); 
	//关联条件
	private transient Map<Long, RelationCondition> relationConditionMap = new LinkedHashMap<Long, RelationCondition>();
	//回写设置
//	private transient ReturnWriteConfig returnWriteConfig;
	
	private transient Map<Long,FormEvent> triggerConfigMap = new LinkedHashMap<Long,FormEvent>();//触发设置
	
//	private transient Map<Long, Menu> orgAllMenu = new HashMap<Long, Menu>();

	//表单应用绑定xml
	public String getMainBindXml() {
		if(formAppAuthObjectMap.isEmpty()){
			String mainBindXml="<Bind formcode=\""+ (formCode==null?"":formCode) +"\"><FormAppAuthList/></Bind>";
			return mainBindXml;
		}
		
//		StringBuilder menuSb = new StringBuilder("<MenuList>");
		StringBuilder formAppAuthsb = new StringBuilder("<FormAppAuthList>");
		for (FormAppAuthObject aao : formAppAuthObjectMap.values()) {
			/*menuSb.append("<Menu id=\"");
			menuSb.append(abo.getId());
			menuSb.append("\" bindtype=\"");
			menuSb.append(abo.getType());
			menuSb.append("\" refObjId=\""); 
			menuSb.append(abo.getBindSet());
			menuSb.append("\" groupId=\"");
			menuSb.append(abo.getBindMenuId()==null?"":abo.getBindMenuId());
			menuSb.append("\" groupname=\"");
			menuSb.append(abo.getBindMenuName()==null?"":abo.getBindMenuName());
			menuSb.append("\" name=\"");
			menuSb.append(abo.getMenuName()==null?"":abo.getMenuName());
			menuSb.append("\" /> ");*/
			
//			if(abo.getAppAuthObject()!=null && String.valueOf(IPagePublicParam.C_iObjecttype_bill).equals(abo.getType())){
			formAppAuthsb.append(aao.getXmlString());
//			}
		}
//		menuSb.append("</MenuList>");
		formAppAuthsb.append("</FormAppAuthList>");
		
		/*String[] rootMenuAry = this.rootMenus.toArray(new String[0]);
		String menuIds = "";
		if(rootMenuAry!=null){
			for (int m=0; m<rootMenuAry.length; m++) {
				if(m==0)
					menuIds+=rootMenuAry[m];
				else
					menuIds+=","+rootMenuAry[m];
			}
		}*/
		
		StringBuilder sb = new StringBuilder("<Bind formcode=\""+ (formCode==null?"":formCode) +"\">");
		String logFieldString = getLogfieldString();
		if(StringUtils.isNotBlank(logFieldString)){
			sb.append(logFieldString);
		}
		sb.append(formAppAuthsb.toString());
		sb.append("</Bind>");
		return sb.toString();
	}

	public Map<String, Format> getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(Map<String, Format> dataFormat) {
		this.dataFormat = dataFormat;
	}

	private boolean isdatavalue = false;

	public boolean isIsdatavalue() {
		return isdatavalue;
	}

	public void setIsdatavalue(boolean isdatavalue) {
		this.isdatavalue = isdatavalue;
	}

	public List getOldflowidlist() {
		return oldflowidlist;
	}

	public void setOldflowidlist(List oldflowidlist) {
		this.oldflowidlist = oldflowidlist;
	}

	public List getFlowidlist() {
		return flowidlist;
	}

	public void setFlowidlist(List flowidlist) {
		this.flowidlist = flowidlist;
	}

	public String getOthersave() {
		return othersave;
	}

	public void setOthersave(String othersave) {
		this.othersave = othersave;
	}

	public HashMap getViewWidthvalue() {
		return viewWidthvalue;
	}

	public void setViewWidthvalue(HashMap viewWidthvalue) {
		this.viewWidthvalue = viewWidthvalue;
	}

	public TemplateObject getTemplateobj() {
		return templateobj;
	}

	public void setTemplateobj(TemplateObject templateobj) {
		this.templateobj = templateobj;
	}

	public String getSelenumattr() {
		return selenumattr;
	}

	public void setSelenumattr(String selenumattr) {
		this.selenumattr = selenumattr;
	}

	public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

	/**
	 * 暂时有三个状态：草稿：0   未审批：1   已审批：2
	 * @return
	 */
	public List<String> getFormStateList() {
		if(formStateList.size() == 0){
			formStateList.add(0, Constantform.getString4CurrentUser("form.query.draft.label"));
			formStateList.add(1, Constantform.getString4CurrentUser("form.query.nodealwith.label"));
			formStateList.add(2, Constantform.getString4CurrentUser("form.query.pass.label"));
		}
		return formStateList;
	}

	public void setFormStateList(List<String> formStateList) {
		this.formStateList = formStateList;
	}

	public List getEnumlist() {
		return enumlist;
	}

	public void setEnumlist(List enumlist) {
		this.enumlist = enumlist;
	}

	public List<String> getInputExtend() {
		return inputExtend;
	}

	public void setInputExtend(List<String> inputExtend) {
		this.inputExtend = inputExtend;
	}

	public Set<String> getRefInputNames() {
		return refInputNames;
	}

	public void setRefInputNames(Set<String> refInputNames) {
		this.refInputNames = refInputNames;
	}

	public Set<String> getOutwriteInputNames() {
		return outwriteInputNames;
	}

	public void setOutwriteInputNames(Set<String> outwriteInputNames) {
		this.outwriteInputNames = outwriteInputNames;
	}

	public String getFormstate() {
		return formstate;
	}

	public void setFormstate(String formstate) {
		this.formstate = formstate;
	}

	public String getPageflag() {
		return pageflag;
	}

	public void setPageflag(String pageflag) {
		this.pageflag = pageflag;
	}

	public List<String> getSysVariable() {
		return sysVariable;
	}

	public void setSysVariable(List<String> sysVariable) {
		this.sysVariable = sysVariable;
	}

	public String getAttachManId() {
		return attachManId;
	}

	public void setAttachManId(String attachManId) {
		this.attachManId = attachManId;
	}

	public String getAttachManName() {
		return attachManName;
	}

	public void setAttachManName(String attachManName) {
		this.attachManName = attachManName;
	}


	public Long getFormsort() {
		return formsort;
	}

	public void setFormsort(Long formsort) {
		this.formsort = formsort;
	}

	public List<FormPage> getFormLst() {
		return formLst;
	}

	public void setFormLst(List<FormPage> formLst) {
		this.formLst = formLst;
	}

	public String getEditflag() {
		return editflag;
	}

	public void setEditflag(String editflag) {
		this.editflag = editflag;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public List<TableFieldDisplay> getTableFieldList() {
		return TableFieldList;
	}

	public void setTableFieldList(List<TableFieldDisplay> tableFieldList) {
		TableFieldList = tableFieldList;
	}

	public String getFormEditName() {
		return FormEditName;
	}

	public void setFormEditName(String formEditName) {
		FormEditName = formEditName;
	}

	public String getFormName() {
		return FormName;
	}

	public void setFormName(String formName) {
		FormName = formName;
	}

	public List getFieldInputList() {
		return FieldInputList;
	}

	public void setFieldInputList(List fieldInputList) {
		FieldInputList = fieldInputList;
	}

	public DataDefine getData() {
		return data;
	}

	public void setData(DataDefine data) {
		this.data = data;
	}

	public SeeyonDataDefine getSeedatadefine() {
		return seedatadefine;
	}

	public void setSeedatadefine(SeeyonDataDefine seedatadefine) {
		this.seedatadefine = seedatadefine;
	}

	public int getTablefieldsize() {
		return tablefieldsize;
	}

	public void setTablefieldsize(int tablefieldsize) {
		this.tablefieldsize = tablefieldsize;
	}

	public InfoPathObject getXsf() {
		return xsf;
	}

	public void setXsf(InfoPathObject xsf) {
		this.xsf = xsf;
	}

	public String getXsnpath() {
		return xsnpath;
	}

	public void setXsnpath(String xsnpath) {
		this.xsnpath = xsnpath;
	}

	public List getOldmaertlst() {
		return oldmaertlst;
	}

	public void setOldmaertlst(List oldmaertlst) {
		this.oldmaertlst = oldmaertlst;
	}

	public List getOldslavelst() {
		return oldslavelst;
	}

	public void setOldslavelst(List oldslavelst) {
		this.oldslavelst = oldslavelst;
	}

	

	public List getNewTableFieldList() {
		return NewTableFieldList;
	}

	public void setNewTableFieldList(List newTableFieldList) {
		NewTableFieldList = newTableFieldList;
	}
	
	
	
	public List<QueryObject> getQueryConditionList() {
		return queryConditionList;
	}

	public void setQueryConditionList(List<QueryObject> queryConditionList) {
		this.queryConditionList = queryConditionList;
	}

	public List<ReportObject> getReportConditionList() {
		return reportConditionList;
	}

	public void setReportConditionList(List<ReportObject> reportConditionList) {
		this.reportConditionList = reportConditionList;
	}

	public void Datadefine(ISeeyonForm_Application iapp)
	throws SeeyonFormException, DocumentException {
		tablelist.clear();
		oldmaertlst.clear();
		oldslavelst.clear();
		List fieldlist = new ArrayList();
		
		SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl) iapp;
		SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
		this.seedatadefine = seedade;
		this.data = seedade.getDataDefine();
		String table =seedade.getDataSource().getMasterTableName(); 
		for(int i=0;i<seedade.getDataSource().getDataAreaList().size();i++){
			TableFieldDisplay tfd = new TableFieldDisplay();
			String tablename=seedade.getDataSource().getDataAreaList().get(i).getDBTableName();
			InfoPath_DataSource datasource = (InfoPath_DataSource)seedade.getDataSource();
			datasource.findGroupByTableName(tablename);
			//测试
			if(datasource.findGroupByTableName(tablename) !=null){
				IDataGroup name =datasource.findGroupByTableName(tablename);				
				tfd.setTablename(name.getGroupName());
			}else{
				tfd.setTablename(tablename);
			}
			
			String areaname=seedade.getDataSource().getDataAreaList().get(i).getAreaName();
			tfd.setName(areaname);
			fieldlist.add(tfd);
		}
		for(int i=0;i<seedade.getDataDefine().getTableLst().size();i++){
			FormTable formtable = (FormTable)seedade.getDataDefine().getTableLst().get(i);
			formtable.getName();
			InfoPath_DataSource datasource = (InfoPath_DataSource)seedade.getDataSource();
			IDataGroup name =datasource.findGroupByTableName(formtable.getName());
			Map map = new HashMap();
			
			String groupname =null;
			if(name!=null){
				groupname = name.getGroupName();
			}
			map.put(formtable.getName(), groupname);
			tablelist.add(map);
		}
		String mastername = seedade.getDataSource().getMasterTableName();
		String slavename  = null;
        for(int i=0;i<fieldlist.size();i++){
        	TableFieldDisplay tfd = (TableFieldDisplay)fieldlist.get(i);
        	String tablename = tfd.getTablename();
        	if(mastername.equals(tablename)){
            	String fieldname = tfd.getName();
            	oldmaertlst.add(mastername + "↗" + fieldname);
        	}else if(!mastername.equals(tablename)){
        		slavename = tablename;
        		String fieldname = tfd.getName();
        		oldslavelst.add(slavename + "↗" + fieldname);
        	}
        }
        
		}
		
		public List getTablelist() {
		return tablelist;
		}
		
		public void setTablelist(List tablelist) {
		this.tablelist = tablelist;
		}

		public String getMatchdatavalue() {
			return matchdatavalue;
		}

		public void setMatchdatavalue(String matchdatavalue) {
			this.matchdatavalue = matchdatavalue;
		}

		public String getNoequalvalue() {
			return noequalvalue;
		}

		public void setNoequalvalue(String noequalvalue) {
			this.noequalvalue = noequalvalue;
		}

		public String getDeletevalue() {
			return deletevalue;
		}

		public void setDeletevalue(String deletevalue) {
			this.deletevalue = deletevalue;
		}

		public List getDelTablename() {
			return delTablename;
		}

		public void setDelTablename(List delTablename) {
			this.delTablename = delTablename;
		}

		public HashMap getFieldMap() {
			return fieldMap;
		}

		public void setFieldMap(HashMap fieldMap) {
			this.fieldMap = fieldMap;
		}

		public List getAddtablename() {
			return addtablename;
		}

		public void setAddtablename(List addtablename) {
			this.addtablename = addtablename;
		}

		public HashMap getEdittableMap() {
			return edittableMap;
		}

		public void setEdittableMap(HashMap edittableMap) {
			this.edittableMap = edittableMap;
		}

		public List<ISeeyonQuery> getQuerylist() {
			return querylist;
		}

		public void setQuerylist(List<ISeeyonQuery> querylist) {
			this.querylist = querylist;
		}

		public List<ISeeyonReport> getReportlist() {
			return reportlist;
		}

		public void setReportlist(List<ISeeyonReport> reportlist) {
			this.reportlist = reportlist;
		}

		public List<FormAppMain> getQueryseachlist() {
			return queryseachlist;
		}

		public void setQueryseachlist(List<FormAppMain> queryseachlist) {
			this.queryseachlist = queryseachlist;
		}

		public List<FormAppMain> getReportseachlist() {
			return reportseachlist;
		}

		public void setReportseachlist(List<FormAppMain> reportseachlist) {
			this.reportseachlist = reportseachlist;
		}

		public HashMap getEnumlistmap() {
			return enumlistmap;
		}

		public void setEnumlistmap(HashMap enumlistmap) {
			this.enumlistmap = enumlistmap;
		}

		public HashMap getEnumvaluemap() {
			return enumvaluemap;
		}

		public void setEnumvaluemap(HashMap enumvaluemap) {
			this.enumvaluemap = enumvaluemap;
		}

		public List<menuObj> getMenulist() {
			return menulist;
		}

		public void setMenulist(List<menuObj> menulist) {
			this.menulist = menulist;
		}

		public HashMap getOldenumnamemap() {
			return oldenumnamemap;
		}

		public void setOldenumnamemap(HashMap oldenumnamemap) {
			this.oldenumnamemap = oldenumnamemap;
		}

		public HashMap getEnumnamemap() {
			return enumnamemap;
		}

		public void setEnumnamemap(HashMap enumnamemap) {
			this.enumnamemap = enumnamemap;
		}

		public Long getOldformsort() {
			return oldformsort;
		}

		public void setOldformsort(Long oldformsort) {
			this.oldformsort = oldformsort;
		}

        public Attachment getAttachment()
        {
            return attachment;
        }

        public void setAttachment(Attachment attachment)
        {
            this.attachment = attachment;
        }
        
        public int getFormType() {
    		return formType;
    	}
    	public void setFormType(int formType) {
    		this.formType = formType;
    	}

		public Map<String, FormAppAuthObject> getFormAppAuthObjectMap() {
			return formAppAuthObjectMap;
		}

		public void setFormAppAuthObjectMap(
				Map<String, FormAppAuthObject> formAppAuthObjectMap) {
			this.formAppAuthObjectMap = formAppAuthObjectMap;
		}

		public FormAppAuthObject getAppAuthObject() {
			return appAuthObject;
		}

		public void setAppAuthObject(FormAppAuthObject appAuthObject) {
			this.appAuthObject = appAuthObject;
		}

		/*public Set<Long> getOldAppBindMenuId() {
			return oldAppBindMenuId;
		}

		public void setOldAppBindMenuId(Set<Long> oldAppBindMenuId) {
			this.oldAppBindMenuId = oldAppBindMenuId;
		}*/
		
		/*public Set<String> getRootMenus() {
			return rootMenus;
		}

		public void setRootMenus(Set<String> rootMenus) {
			this.rootMenus = rootMenus;
		}*/

		/*public Map<Long, Menu> getOrgAllMenu() {
			return orgAllMenu;
		}

		public void setOrgAllMenu(Map<Long, Menu> orgAllMenu) {
			this.orgAllMenu = orgAllMenu;
		}*/
		
		public String getFormCode() {
			return formCode;
		}

		public void setFormCode(String formCode) {
			this.formCode = formCode;
		}
		public List getLogFieldList() {
			return logFieldList;
		}

		public void setLogFieldList(List logFieldList) {
			this.logFieldList = logFieldList;
		}

		public String getLogfieldString() {
			String logfieldString = "<LogFieldList>  \r\n";
			if(logFieldList!=null){
				for (int i = 0; i < logFieldList.size(); i++) {
					logfieldString+="    <LogField  name=\""+ namespace + logFieldList.get(i)+"\" />\r\n";
				}
			}
			logfieldString+="</LogFieldList>  \r\n";
			return logfieldString;
		}
		
		/*public void setLogfieldString(String logfieldString) {
			this.logfieldString = logfieldString;
		}*/
		
		public List getUniqueFieldList() {
			return uniqueFieldList;
		}

		public void setUniqueFieldList(List uniqueFieldList) {
			this.uniqueFieldList = uniqueFieldList;
		}

		public String getUniqueFieldString() {
			String uniqueFieldString = "";
			if(uniqueFieldList!=null){
				for (int i = 0; i < uniqueFieldList.size(); i++) {
					uniqueFieldString +="    <UniqueField name=\"" + namespace + uniqueFieldList.get(i)+"\" />\r\n";
				}
			}
			return uniqueFieldString;
		}

		/*public void setUniqueFieldString(String uniqueFieldString) {
			this.uniqueFieldString = uniqueFieldString;
		}*/

		public Map<Long, RelationCondition> getRelationConditionMap() {
			return relationConditionMap;
		}

		public void setRelationConditionMap(
				Map<Long, RelationCondition> relationConditionMap) {
			this.relationConditionMap = relationConditionMap;
		}

		public Map<Long, FormEvent> getTriggerConfigMap() {
			return triggerConfigMap;
		}

		public void setTriggerConfigMap(Map<Long, FormEvent> triggerConfigMap) {
			this.triggerConfigMap = triggerConfigMap;
		}

		public List<Long> getFlowIdListForInputData() {
			return flowIdListForInputData;
		}

		public void setFlowIdListForInputData(List<Long> flowIdListForInputData) {
			this.flowIdListForInputData = flowIdListForInputData;
		}

		public List<Long> getOldFlowIdListForInputData() {
			return oldFlowIdListForInputData;
		}

		public void setOldFlowIdListForInputData(List<Long> oldFlowIdListForInputData) {
			this.oldFlowIdListForInputData = oldFlowIdListForInputData;
		}

}