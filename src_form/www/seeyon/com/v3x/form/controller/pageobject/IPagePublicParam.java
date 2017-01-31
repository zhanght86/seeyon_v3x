package www.seeyon.com.v3x.form.controller.pageobject;

public interface IPagePublicParam {
	/**
	 * 页面标识
	 */
	public static final String BASEINFO      =    "baseinfo";
	public static final String INPUTDATA     =    "inputdata";
	public static final String OPERCONFIG    =    "operconfig";
	public static final String BINDINFO      =    "bindinfo";
	public static final String QUERYSET      =    "query_queryset";
	public static final String REPORTSET     =   "stat_statset";
	/** 触发设置标识  */
	public static final String TRIGGERSET     =   "triggerset";	
	
	public static final String EDIT      =    "edit";
	//关于字段类型的定义
	public static final String VARCHAR      =    "VARCHAR";
	public static final String DECIMAL      =    "DECIMAL";
	public static final String TIMESTAMP    =    "TIMESTAMP";//日期型
	public static final String LONGTEXT     =    "LONGTEXT";
	public static final String HANDWRITE    =    "HANDWRITE";
	public static final String DATETIME     =    "DATETIME"; //日期时间型
	

	public static String myschema = "myschema.xsd";
	public static String manifest = "manifest.xsf";
	public static String sampledata = "sampledata.xml";
	public static String template = "template.xml";
	//表单主表名称
	public static String tablename = "formmain_";
	//表单子表名称
	public static String tableson = "formson_";
	public static String view = "view1.xsl";	
	
	public static String appresource = "appresourcelist";
	public static String defaultinputxml = "/UserDefineXML/defaultInput.xml";
	//前台返回的参数
	public static String formoperation = "formOperation";
	
	public static String tempSapp = "tempSapp";
	public static String view1 = "/extend/OutViewFile/view1.xsl";
	
	public static String formid = "formid";
	//提供给页面显示的三个参数
	public static String formname = "formname";
	public static String sheetname = "sheetname";
	public static String viewfile = "viewfile";
	
	public static String initformobj = "initformobj";
	
	public static String state0 = "草稿";
	public static String state1 = "预发布";
	public static String state2 = "发布";
	
	public static String TEXT = "text";
	public static String LABEL = "lable";
	public static String CHECKBOX = "checkBox";
	public static String TEXTAREA = "textarea";
	public static String RADIO = "radio";
	public static String SELECT = "select";
	public static String COMBOEDIT = "comboedit";
	public static String EXTEND = "extend";
	public static String RELATION = "relation";
	public static String OUTWRITE = "outwrite";
	public static String EXTERNALWRITE_AHEAD = "externalwrite-ahead";
	
	
	//选人组件对象类型
	public int C_iSelectPerson_Member = 0;     //用户
	public int C_iSelectPerson_Department = 1; //部门
	public int C_iSelectPerson_Team = 2;       //组
	public int C_iSelectPerson_Level = 3;      //职务级别
	public int C_iSelectPerson_Post = 4;       //岗位
	public int C_iSelectPerson_Account = 5;    //单位
	
	//objecttype用户类型
	public int C_iObjecttype_bill = 0;        //单据对象
	public int C_iObjecttype_Query = 1;       //查询对象
	public int C_iObjecttype_Report = 2;      //统计对象
}
