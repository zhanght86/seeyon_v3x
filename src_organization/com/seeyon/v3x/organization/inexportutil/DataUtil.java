package com.seeyon.v3x.organization.inexportutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.util.StringUtils;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.shareMap.V3xShareMap;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.inexportutil.datatableobj.AccountOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.DepartmentOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.LevelOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.MemberOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.PostOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.RoleOpr;
import com.seeyon.v3x.organization.inexportutil.datatableobj.TeamOpr;
import com.seeyon.v3x.organization.inexportutil.inf.IImexPort;
import com.seeyon.v3x.organization.inexportutil.inf.IPublicPara;
import com.seeyon.v3x.organization.inexportutil.manager.IOManagerImpl;
/**
 * 
 * @author kyt
 *
 */
public class DataUtil {
	IImexPort iip;
	
//	
//	public IImexPort getIip() {
//		return iip;
//	}
	public void setIip(String type) {
		this.iip = getInstance(type);
	}
	public DataUtil(String type){
		setIip(type);
	}
	public DataUtil(String type,Locale locale){
		setIip(type);
		this.iip.setLocale(locale);
	}
	/**
	 * 用来匹配前台传入的表名与后台的数据库的名称
	 * @param name  locale
	 * @return
	 */
	public static String getRealTableName(String name){
		if(IPublicPara.account.indexOf(name) != -1){
			return IPublicPara.account;
		}else if(IPublicPara.department.indexOf(name) != -1){
			return IPublicPara.department;
		}else if(IPublicPara.level.indexOf(name) != -1){
			return IPublicPara.level;
		}else if(IPublicPara.member.indexOf(name) != -1){
			return IPublicPara.member;
		}else if(IPublicPara.post.indexOf(name) != -1){
			return IPublicPara.post;
		}else if(IPublicPara.role.indexOf(name) != -1){
			return IPublicPara.role;
		}else if(IPublicPara.team.indexOf(name) != -1){
			return IPublicPara.team;
		}else{
			return null;
		}
	}	
	/**
	 * 用于复制文件
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	public static void CopyFile(File in, File out) throws Exception {   
		FileInputStream fis  = new FileInputStream(in);     
		FileOutputStream fos = new FileOutputStream(out);     
		byte[] buf = new byte[1024];     
		int i = 0;     
		while((i=fis.read(buf))!=-1) {       
			fos.write(buf, 0, i);       
			}     
		fis.close();     
		fos.close();  
	} 
	/**
	 * 加入单位ID
	 * @param accountid
	 * @param volst
	 * @return
	 */
	public static List setAccountId(String accountid,List volst){
		for(int i=0;i<volst.size();i++){
			V3xOrgEntity vo = (V3xOrgEntity)volst.get(i);
			vo.setOrgAccountId(Long.valueOf(accountid));
		}
		return volst;
	}
	
	/**
	 * 为**opr类提供 去掉 "_"的方法
	 * @param str
	 * @return
	 */
	public static String submark(String str){
		if(str == null) str="";
		int position = str.indexOf("_");
		if(position != -1){
			StringBuffer returnstr = new StringBuffer();			
			returnstr.append(str.substring(0,position));
			returnstr.append(str.substring(position+1));
			return submark(returnstr.toString());
		}else{
			return str;
		}
	}
	/**
	 * 得到匹配的list,及不匹配的list 组成的map
	 * @param excellst
	 * @param strulst
	 * @return List 其中又含有三个List 0:为匹配list 1:没有匹配的excel list  2:没有匹配的数据库字段
	 */
	public static Map getMatchList(List excellst,List strulst){
		Map returnmap = new HashMap();
		List matchlst = new ArrayList();
		List nomatchexcellst = new ArrayList();
		List nomatchstrulst = new ArrayList();
		returnmap.put("0", matchlst);
		returnmap.put("1", nomatchexcellst);
		returnmap.put("2", nomatchstrulst);
		
		//匹配算法
		List<DataObject> newexcel = new ArrayList();
		for(int j=0;j<excellst.size();j++){
			String str = (String)excellst.get(j);
			if(!"".equals(str)){
				//此对象只存储 两个字段，用来过渡存储  MatchExcelName、Columnnum
				//用于页面显示
				DataObject dao = new DataObject();
				dao.setMatchExcelName(str);
				dao.setColumnnum(j);
				newexcel.add(dao);
			}
		}
		for(int i=0;i<strulst.size();i++){
			DataObject dataobj = (DataObject)strulst.get(i);
			boolean flag = false;
			for(int j=0;j<newexcel.size();j++){
				DataObject strdao = (DataObject)newexcel.get(j);
				//如：单位名称 、名称  均可与  单位表中的  名称或单位名称 自由匹配
				if(dataobj.getMatchCHNName().equals(strdao.getMatchExcelName())){
					dataobj.setColumnnum(strdao.getColumnnum());
					dataobj.setMatchExcelName(strdao.getMatchExcelName());
					matchlst.add(dataobj);
					flag = true;
					newexcel.remove(j);
					j--;
				}else if(dataobj.getMatchCHNName().length()>2){
					if(dataobj.getMatchCHNName().substring(2).equals(strdao.getMatchExcelName())){
						dataobj.setColumnnum(strdao.getColumnnum());
						dataobj.setMatchExcelName(strdao.getMatchExcelName());
						matchlst.add(dataobj);
						flag = true;
						newexcel.remove(j);
						j--;
					}
				}
			}
			if(!flag){
				nomatchstrulst.add(dataobj);
			}
		}
		
		nomatchexcellst.addAll(newexcel);
		
		return returnmap;
	}
	/**
	 * 把前台已经匹配好的数据，传入到vo list中
	 * @param request
	 * @param datalst
	 * @return
	 */
	public static List setMatchList(HttpServletRequest request,List datalst){
		for(int i=0;i<datalst.size();i++){
			String excelname = (String)request.getParameter("excelname"+i);
			String columnnum = (String)request.getParameter("columnnum"+i);
			String fieldname = (String)request.getParameter("fieldname"+i);
			String straname = (String)request.getParameter("struname"+i);
			if(excelname!=null&&!"".equals(excelname)&&!"null".equals(excelname)){
				for(int j=0;j<datalst.size();j++){
					DataObject dao = (DataObject)datalst.get(j);
					if(fieldname.equalsIgnoreCase(dao.getFieldName())){
						dao.setColumnnum(Integer.valueOf(columnnum).intValue());
						dao.setMatchExcelName(excelname);
					}
				}
			}
		}
		return datalst;
	}
	/**
	 * 用于判断excel文件中的值是否为boolean类型的值
	 * @param value
	 * @return
	 */
	public static String isBool(String value){
		String chn[]=IImexPort.booleanchnvalue;
		String eng[]=IImexPort.booleanengvalue;
		for(int i=0;i<eng.length;i++){
			if(eng[i].indexOf(value) != -1){
				if(eng[i].indexOf(".") != -1){
					return "true";
				}else{
					return "false";
				}
			}
		}
		for(int i=0;i<chn.length;i++){
			if(chn[i].indexOf(value) != -1){
				if(chn[i].indexOf(".") != -1){
					return "true";
				}else{
					return "false";
				}
			}			
		}
		return value;
	}
	/**
	 * 判断一个字符串是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		if(str==null || "".equals(str)||"null".equals(str)){
			return false;
		}
	    Pattern pattern = Pattern.compile("[0-9]*");
	    Matcher isNum = pattern.matcher(str);
	    if( !isNum.matches() ) {
	       return false;
	    }
	    return true;
	}
	/**
	 * 用来给结果列表赋值
	 */
	public static List setResultList(List resultlst,List volst,String repeat,Locale locale ){
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";	
		String cover = ResourceBundleUtil.getString(resource, locale, "import.cover");
		String success = ResourceBundleUtil.getString(resource, locale, "import.success");
		String jump = ResourceBundleUtil.getString(resource, locale, "import.jump");
		String insert = ResourceBundleUtil.getString(resource, locale, "import.insert");
		if(volst != null){
			for(int i=0;i<volst.size();i++){
				ResultObject ro = new ResultObject();
				V3xOrgEntity vo = (V3xOrgEntity)volst.get(i);
				ro.setName(vo.getName());
				if("0".equals(repeat)){
					ro.setDescription(cover);
					ro.setSuccess(success);
				}else if("1".equals(repeat)){
					ro.setDescription(jump);
					ro.setSuccess("");
				}else if("".equals(repeat)){
					ro.setDescription(insert);
					ro.setSuccess(success);
				}
				resultlst.add(ro);
			}
		}
		return resultlst;
	}
	
	/**
	 * 用来给结果列表赋值
	 */
	public static List setResultToSession(List resultlst, HttpSession session){
		if(resultlst != null){
			DataRow[] datarow = new DataRow[resultlst.size()];
			for(int i=0;i<resultlst.size();i++){
				ResultObject ro = (ResultObject)resultlst.get(i);
				DataRow row = new DataRow();
				row.addDataCell(ro.getName(), 1);
				row.addDataCell(ro.getSuccess(), 1);
				row.addDataCell(ro.getDescription(), 1);
				datarow[i] = row;
			}
			session.setAttribute("datarowlist", datarow);
		}
		return resultlst;
	}	
	
	public static DataRow[] createDataRowsFromResultObjects(List resultlst){
		DataRow[] datarow=null;
		if(resultlst!=null){
			datarow=new DataRow[resultlst.size()];
			
			for(int i=0;i<resultlst.size();i++){
				ResultObject ro = (ResultObject)resultlst.get(i);
				DataRow row = new DataRow();
				row.addDataCell(ro.getName(), 1);
				row.addDataCell(ro.getSuccess(), 1);
				row.addDataCell(ro.getDescription(), 1);
				datarow[i] = row;
			}
		}else{
			datarow=new DataRow[0];
		}
		
		return datarow;
	}
	
	/**
	 * 判断是否全是空值
	 * @param lst  如果有非空值 返回为  true  否则为 false
	 * @return
	 */
	public static boolean isNotNullValue(List<String> lst){
		boolean flag = false;
			for(int i=0;i<lst.size();i++){
				String str=(String)lst.get(i);
				if(str == null ||"".equals(str)||"null".equals(str)){
				}else{
					flag=true;
				}
				if(flag) break;
			}
		return flag;
	}	
	/**
	 * 实例类工厂
	 * @param type
	 * @return
	 */
	public IImexPort getInstance(String type){
		if(IPublicPara.account.indexOf(type) != -1){
			return new AccountOpr();
		}else if(IPublicPara.department.indexOf(type) != -1){
			return new DepartmentOpr();
		}else if(IPublicPara.level.indexOf(type) != -1){
			return new LevelOpr();
		}else if(IPublicPara.member.indexOf(type) != -1){
			return new MemberOpr();
		}else if(IPublicPara.post.indexOf(type) != -1){
			return new PostOpr();
		}else if(IPublicPara.role.indexOf(type) != -1){
			return new RoleOpr();
		}else if(IPublicPara.team.indexOf(type) != -1){
			return new TeamOpr();
		}else{
			return null;
		}
	}
	/**
	 * 
	 * @param od
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public Map devideVo(OrgManagerDirect od,List volst) throws Exception{
		return this.iip.devVO(od, volst);
		
	}
	/**
	 * 对传入的vo list，进行校验，并生insert sql语句
	 * @param name
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public List getCHNString(List volst,HttpServletRequest request) throws Exception{
		return iip.matchLanguagefield(volst,request);
	}	
	/**
	 * 对传入的vo list，进行校验，并生insert sql语句
	 * @param name
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public List getUpdateSQL(List volst) throws Exception{
		this.iip.validateData(volst);
		return iip.creatUpdateSql(volst);
	}	
	/**
	 * 把excel数据进行装配，生成各数据VO
	 * @param accountList
	 * @param volst
	 * @return
	 * @throws Exception 
	 */
	public List getMatchValue(OrgManagerDirect od,MetadataManager metadataManager, Long accountid,List<List<String>> accountList,List volst ) throws Exception{
		return this.iip.assignVO(od,metadataManager,accountid,accountList, volst);
	}
	/**
	 * 对传入的vo list，进行校验，并生insert sql语句
	 * @param name
	 * @param volst
	 * @return
	 * @throws Exception
	 */
	public List getCreateSQL(List volst) throws Exception{
		this.iip.validateData(volst);
		return iip.creatInsertSql(volst);
	}
//	/**
//	 * 把各个表中的需要匹配的外键，找到中文所对应的键值，然后存入各个vo
//	 * @param volst
//	 * @return
//	 */
//	public List setForeignTable(OrgManagerDirect od,List volst ){
//		
//	}
	public IImexPort getIip() {
		return iip;
	}
	public void setIip(IImexPort iip) {
		this.iip = iip;
	}


	private static ThreadLocal<Map<String,List>> lt=new ThreadLocal<Map<String,List>>();
	
	public static List  getResult4Imp(String key){
		Map<String,List> r4imp=getThreadLocalMap4Imp();
		
		return r4imp.get(key);
	}
	public static void putResult4Imp(String key,List rs){
		if(StringUtils.hasText(key)){
			Map<String,List> r4imp=getThreadLocalMap4Imp();
			
			r4imp.put(key, rs);
		}
	}
	protected static void initThreadLocal4Imp(){
		Map<String,List> r4imp=new HashMap<String,List>();
		lt.set(r4imp);
	}
	protected static Map<String,List> getThreadLocalMap4Imp(){
		Map<String,List> r4imp=lt.get();
		if(r4imp==null){
			initThreadLocal4Imp();
			r4imp=lt.get();
		}
		
		return r4imp;
	}
	
	public static String getParameterStr(Map<String,String> paras){
		StringBuffer sb=new StringBuffer();
		if(paras!=null){
			Set<String> keys=paras.keySet();
			for(String key:keys){
				if(StringUtils.hasText(key)){
					if(sb.length()>0){
						sb.append("&");
					}
					sb.append(key);
					sb.append("=");
					sb.append(paras.get(key));
				}
			}
		}
		
		return sb.toString();
	}
	public static String getUrl(String mainUrl,String paras){
		if(StringUtils.hasText(mainUrl)){
			StringBuffer sb=new StringBuffer();
			sb.append(mainUrl);
			
			if(StringUtils.hasText(paras)){
				if(IMP_reportURL.indexOf("?")<0){
					sb.append("?");
				}else{
					sb.append("&");
				}
				
				sb.append(paras);
			}
			
			return sb.toString();
		}
		
		return null;
	}
	
	public static final String IMP_selectvalue="selectvalue";
	public static final String IMP_repeat="repeat";
	public static final String IMP_language="language";
	public static final String IMP_impURL="impURL";
	public static final String IMP_reportURL="/organization.do?method=importReport";
	public static final String EXP_ORGDOWNLOADTOEXCELURL="/organization.do?method=downloadExpToExcel";
	public static final String EXP_REALDOWNLOADTOEXCELURL="/excel.do?method=doDownload";
	public static final String EXP_key="key";
	public static final String EXP_filename="filename";
	//public static final String METHOD=""
	
	
	public static String getImportReportParamterStr(String selectvalue
			                   ,String repeat,String language,String impURL){
		Map<String,String> m=new HashMap<String,String>(6);
		
		m.put(IMP_selectvalue, selectvalue);
		m.put(IMP_repeat, repeat);
		m.put(IMP_language, language);
		//m.put(IMP_impURL, impURL);
		
		return getParameterStr(m);
	}
	public static String getImportReportURL(String selectvalue
            ,String repeat,String language,String impURL){
		String paras=getImportReportParamterStr(selectvalue,repeat,language,impURL);
		/*StringBuffer sb=new StringBuffer();
		sb.append(IMP_reportURL);
		
		String paras=getImportReportParamterStr(selectvalue,repeat,language,impURL);
		if(StringUtils.hasText(paras)){
			if(IMP_reportURL.indexOf("?")<0){
				sb.append("?");
			}else{
				sb.append("&");
			}
			
			sb.append(paras);
		}*/
		
		return getUrl(IMP_reportURL,paras);
	}
	
	public static List<?> pageForList(List<?> l){
		if(l==null)
			return l;
		
		List<?> subl=null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(l.size());
		if ((first + pageSize) > l.size()) {
			subl = l.subList(first, l.size());
		} else {
			subl= l.subList(first, first + pageSize);
		}
		
		return subl;
	}

	private static Map<Long,String> impExpAction=new ConcurrentHashMap<Long,String>(128);
	public static void putImpExpAction(Long userid,String action){
		impExpAction.put(userid, action);
	}
	public static String getImpExpAction(Long userid){
		return impExpAction.get(userid);		
	}
	public static void removeImpExpAction(Long userid){
		impExpAction.remove(userid);
	}
	public static boolean doingImpExp(Long userid){
		String action=getImpExpAction(userid);
		
		return action==null?false:true;
	}
	
	public static void outNullUserAlertScript(PrintWriter out){
		if(out==null)
			return;
		
		out.println("<script>");
		out.println("parent.dolertmsg('");
		out.println(DataUtil.NULL_CURRENTUSER);
		out.println("');");
		out.println("</script>");
	}
	public static void outDoingImpExpAlertScript(PrintWriter out){
		if(out==null)
			return;
		
		out.println("<script>");
		out.println("parent.dolertmsg('");
		out.println(DataUtil.IMP_EXP_DOING);
		out.println("');");
		out.println("</script>");
	}
	public static void outCloseOnbeforeunloadScript(PrintWriter out,String url){
		if(out==null)
			return;
		
		out.println("<script>");
		out.println("parent.closeOnbeforeunload('");
		out.println(url);
		out.println("');");
		out.println("</script>");
	}
	
	public static final String NULL_CURRENTUSER="没有当前用户！！！";
	public static final String IMP_EXP_DOING="后台正在处理数据！！！";
	
	public static String createTempSaveKey4Sheet(DataRecord... dataRecords) throws Exception {
		if (dataRecords == null || dataRecords.length == 0) {
			throw new IllegalArgumentException("不能创建工作表!");
		}
		
		HSSFWorkbook wb=null;
		synchronized(dataRecords){
			 wb= doSheet(dataRecords);
		}
		return V3xShareMap.put(wb);
	}
	public static String getDownloadExpToExcelUrl(String controller,String key,String filename){
		return getDownloadExpToExcelUrl(controller,key,filename,false);
	}
	public static String getDownloadExpToExcelUrl(String controller,String key,String filename,boolean encode){
		String fn=encode==true?DataUtil.encodeUTF8(filename):filename;
		return getUrl(controller,
				getKeyFilenameParaStr(key,fn));
	}
	public static String getDownloadExpToExcelUrl(String controller,String method,String key,String filename){
		String fn=DataUtil.encodeUTF8(filename);
		return getUrl(controller,
				getMethodKeyFilenameParaStr(method,key,fn));
	}
	public static String getOrgDownloadExpToExcelUrl(String key,String filename){
		return getDownloadExpToExcelUrl(EXP_ORGDOWNLOADTOEXCELURL,key,filename,true);
	}
	public static String getRealDownloadExpToExcelUrl(String key,String filename){
		return getDownloadExpToExcelUrl(EXP_REALDOWNLOADTOEXCELURL,key,filename);
	}
	public static String getRealDownloadExpToExcelUrl(String method,String key,String filename){
		return getDownloadExpToExcelUrl(EXP_REALDOWNLOADTOEXCELURL,method,key,filename);
	}
	public static String getKeyFilenameParaStr(String key,String filename){
		Map<String,String> m=new HashMap<String,String>(6);
		
		m.put(EXP_key, key);
		m.put(EXP_filename, filename);
		
		return getParameterStr(m);
	}
	private static String getMethodKeyFilenameParaStr(String method,String key,String filename){
		return getMethodKeyFilenameParaStr(method,key,filename,false);
	}
	public static String getMethodKeyFilenameParaStr(String method,String key,String filename,boolean encode){
		Map<String,String> m=new HashMap<String,String>(6);
		
		String fn=encode==true?DataUtil.encodeUTF8(filename):filename;
		
		m.put(IOManagerImpl.PARA_METHOD_BASE, method);
		m.put(EXP_key, key);
		m.put(EXP_filename, fn);
		
		return getParameterStr(m);
	}
	//public static String getExpRepeaterUrl
	
	/**
	 * 初始化样式
	 */
	private static void initStyle(HSSFWorkbook wb, HSSFCellStyle styleTitle, 
			HSSFCellStyle styleColumn, HSSFCellStyle styleContentText, HSSFCellStyle styleContentDate,
			HSSFCellStyle styleContentDatetime, HSSFCellStyle styleContentNumeric, HSSFCellStyle styleContentInteger
			) {
		HSSFDataFormat format = wb.createDataFormat();

		// 标题字体
		HSSFFont fontTitle = wb.createFont();
		fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontTitle.setFontHeightInPoints((short) 16);
		// 标题样式
		styleTitle.setFont(fontTitle);
		styleTitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleTitle.setDataFormat(format.getFormat("text"));
		// 表头字体
		HSSFFont fontCulumn = wb.createFont();
		// fontCulumn.setFontName("宋体");
		// fontCulumn.setFontHeightInPoints( (short) 9);
		fontCulumn.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 表头样式
		styleColumn.setFont(fontCulumn);
		styleColumn.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleColumn.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleColumn.setDataFormat(format.getFormat("text"));
		// 内容字体
		HSSFFont fontContent = wb.createFont();
		// fontContent.setFontName("宋体");
		// fontContent.setFontHeightInPoints( (short) 12);
		// 内容样式
		// 文本
		styleContentText.setFont(fontContent);
		styleContentText.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleContentText.setHidden(true);
		styleContentText.setWrapText(true);
		styleContentText.setDataFormat(format.getFormat("text"));
		// 日期
		styleContentDate.setFont(fontContent);
		styleContentDate.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleContentDate.setHidden(true);
		styleContentDate.setWrapText(true);
		styleContentDate.setDataFormat(format.getFormat("yyyy-m-d"));
		// 日期+时间
		styleContentDatetime.setFont(fontContent);
		styleContentDatetime.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleContentDatetime.setHidden(true);
		styleContentDatetime.setWrapText(true);
		styleContentDatetime.setDataFormat(format.getFormat("yyyy-m-d H:mm:ss"));
		// 小数
		styleContentNumeric.setFont(fontContent);
		styleContentNumeric.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleContentNumeric.setHidden(true);
		styleContentNumeric.setWrapText(true);
		styleContentNumeric.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		// 整数
		styleContentInteger.setFont(fontContent);
		styleContentInteger.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styleContentInteger.setHidden(true);
		styleContentInteger.setWrapText(true);
		styleContentInteger.setDataFormat(format.getFormat("0"));
	}
	
	private static HSSFWorkbook doSheet(DataRecord... dataRecords) throws Exception {
		int sheetNum = dataRecords.length;
		if (sheetNum == 0) {
			throw new Exception("不能创建工作表!");
		}
		HSSFSheet[] sheets = new HSSFSheet[sheetNum];

		DataRecord dataRecord = null;

		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFCellStyle styleTitle = wb.createCellStyle();
		HSSFCellStyle styleColumn = wb.createCellStyle();
		HSSFCellStyle styleContentText = wb.createCellStyle();
		HSSFCellStyle styleContentDate = wb.createCellStyle();
		HSSFCellStyle styleContentDatetime = wb.createCellStyle();
		HSSFCellStyle styleContentNumeric = wb.createCellStyle();
		HSSFCellStyle styleContentInteger = wb.createCellStyle();
		
		initStyle(wb, styleTitle, styleColumn, styleContentText, styleContentDate, styleContentDatetime, styleContentNumeric, styleContentInteger);

		for (int i = 0; i < sheetNum; i++) { // 多个sheet
			dataRecord = dataRecords[i];

			if (dataRecord == null) {
				throw new Exception("DataRecord is disabled");
			}
			
			sheets[i] = wb.createSheet();
			HSSFSheet sheet = sheets[i];
			sheet.setGridsPrinted(true); // 打印网格线
			sheet.setHorizontallyCenter(true); // 水平居中
			sheet.setAutobreaks(true);
//			sheet.setDefaultColumnWidth((short) 12);

			String[] columnName = dataRecord.getColumnName();
			int colunmLength = 0;
			if (columnName != null) {
				colunmLength = columnName.length;
				wb.setSheetName(i, dataRecord.getSheetName()); // 设置表的名字
			}

			HSSFRow row = sheet.createRow(0);
			row.setHeightInPoints((float) 25);
			HSSFCell cell = row.createCell((short) 0);
			if (colunmLength > 0) {
				sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) (colunmLength - 1)));
			}
			cell.setCellValue(dataRecord.getTitle());
			cell.setCellStyle(styleTitle);
			
			// 表头
			row = sheet.createRow(1);
			row.setHeightInPoints((float) 20);
			for (int c = 0; c < colunmLength; c++) {
				cell = row.createCell((short) c);
				cell.setCellValue(columnName[c]);

				cell.setCellStyle(styleColumn);
				cell.setAsActiveCell();
			}
			
			// 列宽度
			short[] columnWith = dataRecord.getColumnWith();
			if(columnWith != null){
				for (short j = 0; j < columnWith.length; j++) {
					sheet.setColumnWidth(j, new Integer(columnWith[j] * 200).shortValue());
				}
			}

			// 内容
			DataRow[] dataRows = dataRecord.getRow();

			for (int c = 0; c < dataRows.length; c++) { // 多行数据
				row = sheet.createRow(c + 2);
				row.setHeightInPoints((float) 20);
				DataCell[] dataCells = dataRows[c].getCell();

				for (int j = 0; j < dataCells.length; j++) { // 一行数据多单元格
					cell = row.createCell((short) j);
					DataCell dataCell = dataCells[j];
					
					switch (dataCell.getType()) {
					case DataCell.DATA_TYPE_TEXT:
						cell.setCellStyle(styleContentText);
						cell.setCellValue(dataCell.getContent());
						break;
					case DataCell.DATA_TYPE_DATE:
						cell.setCellStyle(styleContentDate);
						cell.setCellValue(dataCell.getContent());
						break;
					case DataCell.DATA_TYPE_DATETIME:
						cell.setCellStyle(styleContentDatetime);
						cell.setCellValue(dataCell.getContent());
						break;
					case DataCell.DATA_TYPE_NUMERIC:
						cell.setCellStyle(styleContentNumeric);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.parseDouble(dataCell.getContent()));
						break;
					case DataCell.DATA_TYPE_INTEGER:
						cell.setCellStyle(styleContentInteger);
						if(dataCell.getContent()==null||dataCell.getContent().equals("")){
							cell.setCellValue("");
						}else{
							cell.setCellValue(Integer.parseInt(dataCell.getContent()));
						}						
						break;
					default:
						cell.setCellStyle(styleContentText);
						cell.setCellValue(dataCell.getContent());
						break;
					}
				}
			}

			// 打印属性
			HSSFPrintSetup print = sheet.getPrintSetup();
			sheet.setAutobreaks(true);
			print.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
			print.setFitHeight((short) 1);
			print.setFitWidth((short) 1);
		}

		return wb;
	}
	
	public static String encodeUTF8(String org){
		try{
			return URLEncoder.encode(org);
		}catch(Exception e){
			return org;
		}
	}
	
//	public static void main(String args[]){
//		String a="b";
//		String b="好";
//		String c="4";
//		System.out.println(isNumeric(a));
//		System.out.println(isNumeric(b));
//		System.out.println(isNumeric(c));
//	}	
}
