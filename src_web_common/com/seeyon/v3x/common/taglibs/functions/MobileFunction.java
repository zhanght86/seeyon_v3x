package com.seeyon.v3x.common.taglibs.functions;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.usermessage.MessageState;
import com.seeyon.v3x.common.utils.DateUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.mobile.manager.OAManagerInterface;
import com.seeyon.v3x.mobile.menu.BaseMobileMenu;
import com.seeyon.v3x.mobile.menu.manager.MobileMenuManager;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class MobileFunction {
	
	 private final static Log log = LogFactory.getLog(MobileFunction.class);
	 
	/**
	 * 对字符串进行编码
	 * @param str
	 * @return
	 */
	public static  String encodeString(String str){
		String st="";
		if(str!=null){
			 try {
				st = java.net.URLEncoder.encode(str,"utf-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage(), e);		
			}
		}
		
		return st;
		
	}
	
	/**
	 * 得到后缀
	 * @param str
	 * @return
	 */
	public static String getSuffix(String str){
		
		return str!=null?str.substring(str.lastIndexOf(".")+1, str.length()):"";
		
	}
	public static boolean isTrue(String str,String st){
		if(str.equalsIgnoreCase(st))
			return true;
		return false;
	}
	
	/**
	 * 得到 给节点包含的人员
	 * @param str 节点的id
	 * @param map 
	 * @return
	 */
	public static Map<String,List<Long>> getFlowChartNode(String str, Map<String, List<Object[]>> map) {
		Map<String,List<Long>> mapstr = new HashMap<String,List<Long>>();
		List<Long> list_long = new ArrayList<Long>();
		if(map!=null){
			List<Object[]> list = map.get(str);
			if(list!=null&&list.size()!=0){
				for(Object[] o : list){
					list_long.add((Long)o[0]);
				}
				mapstr.put("member", list_long);
			}
		}
		return mapstr;
	}
	
	/**
	 * 返回 List 的 size
	 * @return
	 */
	public static int getListSize(List<Long> list){
		
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 得到角色的类型
	 * @param obj
	 * @return
	 */
	public static String getRoleType(V3xOrgEntity obj){
		if(obj!=null){
			return obj.getEntityType();
		}else{
			return null;
		}
	}
	
	public static String getTipValue(TIP_InputValueAll formMobile,Map<Long,List<Attachment>> atts,String downUrl){
		if(formMobile == null ) return "";
		if("hide".equals(formMobile.getAccess())) return "*";
		StringBuffer result = new StringBuffer();
		//主要是对扩展进行展现，其他的就不做特殊处理
		switch(formMobile.getType()){
		case fitExtend:
			fitExtendValue(formMobile, atts, downUrl, result);
		break;
		case fitHandwrite:
			result.append(MobileConstants.getValueFromMobileRes("form.lable.view.1"));
		break;
		case fitCheckBox:
			result.append("<input type='checkbox' disabled "+("1".equals(formMobile.getDisplayValue())?"checked":"")+"/>");
			break;
		default:
			if(Strings.isNotBlank(formMobile.getDisplayValue())){
				result.append(formMobile.getDisplayValue());
			}else if(Strings.isNotBlank(formMobile.getValue())){
				result.append(formMobile.getValue());
			}
		}
		if(result.length() ==0){
			result.append("&nbsp;&nbsp;");
		}
		return result.toString();
	}

	private static void fitExtendValue(TIP_InputValueAll formMobile, Map<Long, List<Attachment>> atts, String downUrl, StringBuffer result) {
		if(atts != null){
			if("插入图片".equals(formMobile.getStageRSXml()) || "插入附件".equals(formMobile.getStageRSXml())){
				if(NumberUtils.isNumber(formMobile.getValue())){
					List<Attachment> formAtts = atts.get(Long.parseLong(formMobile.getValue()));
					if(formAtts != null)
					for (Attachment attachment : formAtts) {
						result.append("<span class='hasAttach'>");
						result.append("</span>");
						if((attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal() || attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.IMAGE.ordinal() || attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormFILE.ordinal()) &&  !attachment.getMimeType().equals(ApplicationCategoryEnum.collaboration.name()))
						result.append("<a href=\""+downUrl+"?method=download&from=mobile&fileId="+attachment.getFileUrl()+"&createDate="+Datetimes.format(attachment.getCreatedate(), "yyyy-MM-dd")+"&filename="+attachment.getFilename()+"\">");
						result.append(attachment.getFilename());
						if((attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal() || attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.IMAGE.ordinal() || attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormFILE.ordinal()) &&  !attachment.getMimeType().equals(ApplicationCategoryEnum.collaboration.name()))
						result.append("</a>");
						result.append("&nbsp;&nbsp;");
					}
				}
			}else if("关联文档".equals(formMobile.getStageRSXml())){
				if(NumberUtils.isNumber(formMobile.getValue())){
					List<Attachment> formAtts = atts.get(Long.parseLong(formMobile.getValue()));
					if(formAtts != null)
					for (Attachment attachment : formAtts) {
						result.append("<span class='hasAttach'>");
						result.append("</span>");
						result.append(attachment.getFilename());
						result.append("&nbsp;&nbsp;");
					}
				}
			}else{
				result.append(formMobile.getDisplayValue() == null?"":formMobile.getDisplayValue());
			}
		}else{
			result.append(formMobile.getDisplayValue() == null?"":formMobile.getDisplayValue());
		}
	}
	
	public static String getInputValue(TIP_InputValueAll inputValue,Boolean isEdit,Map<Long,List<Attachment>> atts,String downUrl){
		if(isEdit != null && isEdit && ("edit".equals(inputValue.getAccess()) || "add".equals(inputValue.getAccess()))){
			try {
				return getTypeObjcet(inputValue,atts,downUrl);
			} catch (UnsupportedEncodingException e) {
				log.error("解析表单，转码错误",e);
			}
		}else{
			return getTipValue(inputValue,atts,downUrl);
		}
		return "";
	}
	
	/**
	 * 得到表单的 Form 类型
	 * @param formMobile
	 * @param cid
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getTypeObjcet(TIP_InputValueAll formMobile,Map<Long,List<Attachment>> atts,String downUrl) throws UnsupportedEncodingException{
	    if(formMobile!=null){
	    	Set<String> set = formMobile.getValueMap().keySet();
	    	//计算字段。隐藏
	    	TFieldInputType type = formMobile.getType();
	    	String value = formMobile.getDisplayValue()!=null?formMobile.getDisplayValue():"";
	    	if(Strings.isNotBlank(formMobile.getStageCalculateXml())){
	    		type = TFieldInputType.fitComboedit;
	    	}
	    	switch(type){
	    	case fitCheckBox:{
	    		String result = "";
	    		int i=1;
	    		String f = "f"+i;
	    		if(value!=null&&"1".equals(value)){
	    			result = result+"<label for="+f+"><input type=\"checkbox\" id="+f+"  checked=\"checked\" name="+"\""+formMobile.getId()+"\""+"/></label>";
	    		}else{
	    			result = result+"<label for="+f+"><input type=\"checkbox\" id="+f+"  name="+"\""+formMobile.getId()+"\""+"/></label>";
	    		}
	    		return result ;
	    	}
	    	case fitText:{
	    		if(value.length()!=0&&!value.equals("null")){
	    			return "<input length=10 value='"+value+"' name="+"\""+formMobile.getId()+"\""+"/>";
	    		}else{
	    			return "<input length=10 name="+"\""+formMobile.getId()+"\""+"/>";
	    		}
	    	}
	    	case fitLable:{
	    		if(value.length()!=0&&!value.equals("null")){
	    			return "<input readonly='true' value='"+value+"' name="+"\""+formMobile.getId()+"\""+"/>";
	    		}else{
	    			return "<input readonly='true' name="+"\""+formMobile.getId()+"\""+"/>";
	    		}
	    	}
	    	case fitTextArea:{
	    		// 如果是追加
	    		if ("add".equals(formMobile.getAccess())) {
	    			// 如果以前追加的有内容
	    			if(value.length()!=0&&!value.equals("null")){
		    			return "<input type='hidden' name='"+formMobile.getId()+"old'"+ " value='"+Functions.toHTML(value)+"'/>"+Functions.toHTML(value)+"<br>"+"<textarea rows=\"3\" length=10 name="+"\""+formMobile.getId()+"\""+"></textarea>";
		    		}else{
		    			return "<input type='hidden' name='"+formMobile.getId()+"append' value='1'/><textarea rows=\"3\" length=10 name="+"\""+formMobile.getId()+"\""+"></textarea>";
		    		}
	    		} else {
	    			return "<textarea rows=\"3\" length=10 name="+"\""+formMobile.getId()+"\""+">"+value+"</textarea>";
	    		}
	    		
	    	}
	    	case fitRadio:{
	    		String result = "";
	    		int i=1;
	    		String selectValue = formMobile.getDefaultValue();
	    		if(Strings.isBlank(selectValue)){
	    			selectValue = formMobile.getValue();
	    		}
	    		for(String str : set){
	    			String f = "f"+i;
	    			String valueS = formMobile.getValueMap().get(str).toString();
	    			if(str.equals(selectValue) || valueS.equals(selectValue)){
	    				result = result+"<label for="+f+"><input type=\"radio\" id="+f+" value='"+str+"' checked=\"checked\" name="+"\""+formMobile.getId()+"\""+"/>"+valueS+"</label>";
	    			}else{
	    				result = result+"<label for="+f+"><input type=\"radio\" id="+f+" value='"+str+"' name="+"\""+formMobile.getId()+"\""+"/>"+valueS+"</label>";
	    			}
	    			i++;
	    		}
	    		return result;
	    	}
	    	case fitSelect:{
	    		StringBuffer opinion = new StringBuffer();
	    		String selectValue = formMobile.getDefaultValue();
	    		if(Strings.isBlank(selectValue)){
	    			selectValue = formMobile.getValue();
	    		}
				opinion.append("<option  value=''></option>");
				for(String s : set){
					String valueS = formMobile.getValueMap().get(s).toString();
					if(s.equals(selectValue) || valueS.equals(selectValue)){
						opinion.append("<option value='"+s+"' selected='selected'>"+valueS+"</option>");
					}else{
						opinion.append("<option value='"+s+"'>"+valueS+"</option>");
					}
				}
				return "<select name="+"\""+formMobile.getId()+"\""+">"+opinion.toString()+"/select>";
	    	}
	    	case fitExtend:
	    	case fitComboedit:{
	    		String result = getTipValue(formMobile, atts, downUrl);
	    		if(Strings.isBlank(formMobile.getDisplayValue()) && !formMobile.isIs_null()){
	                result +="<br/><span class='warn'>"+MobileConstants.getValueFromMobileRes("mobile.form.edit.warnning");
	                result +="</span>";  
	    		}
	    		return result;
	    	}
	    	case fitHandwrite:
	    		return "－－－";
	    	}
	    }
    	return null;
	}
	/**
	 * 格式化 时间 ("MM/dd or HH:mm")
	 * @param time
	 * @return
	 */
	public static String formatDate(Date time){
		if(time!=null){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			Long timeLong = time.getTime();
			Long currentSysteLong = c.getTimeInMillis();
			
			if(timeLong < currentSysteLong){
				return Datetimes.format(time, "MM-dd");
			}else{
				return Datetimes.format(time, "HH:mm");
			}
		}else{
			return "";
		}
	}
	
	/**
	 * 格式化 时间 ("yyyy-MM-dd HH:mm:ss")
	 * @param time
	 * @return
	 */
	public static String formatDateAll(Date time){
		if(time!=null){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			return Datetimes.format(time, "yy-MM-dd HH:mm:ss");	
		}else{
			return "";
		}
	}
	
	/**
	 * 格式化时间  ("yy-MM-dd")
	 * @param time
	 * @return
	 */
	public static String formatDateYMD(Date time){
		if(time!=null){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			return Datetimes.format(time, "yyyy-MM-dd");
		}else{
			return "";
		}
	}
	
	/**
	 * 格式化时间 ("yyyy-MM-dd HH:mm")
	 * @param time
	 * @return
	 */
	public static String formDateYMDHM(Date time){
		if(time!=null){
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			return Datetimes.format(time, "yyyy-MM-dd HH:mm");	
		}else{
			return "";
		}
	}
	
	/**
	 * 得到 EdocSummary 的属性字段
	 * @param itemName
	 * @return
	 */
	public static String getEdocSummaryAttribute(String itemName){
		String str = "";
		if(itemName!=null){
			if(itemName.contains("_")){
				String[] strArray = itemName.split("_");
				if(strArray!=null){
					for(int i=0;i<strArray.length;i++){
						if(i==0){
							str = str + strArray[0];
						}else{
							String string = strArray[i].substring(0, 1).toUpperCase();
							str += string;
							str += strArray[i].substring(1);
						}
					}
				}
			}else{
				if("keyword".equals(itemName)){
					str =  "keywords";
				}else{
					if("createdate".equals(itemName)){
						str =  "createTime";
					}else{
						if("packdate".equals(itemName)){
							str =  "packTime";
						}else{
							String pattern = "string[0-9]*";
							Pattern p = Pattern.compile(pattern);
							Matcher m = p.matcher(itemName);
							if(m.matches()){
								str =  "varchar"+itemName.substring(6);
							}else{
								str = itemName;
							}
						}
					}
				}
			}
		}
		return str;
	}
	
	/**
	 * 判断是否是 文件密级
	 * @param str
	 * @return
	 */
	public static boolean isSecretType(String str){
		if(str!=null){
			if("edoc.element.secretlevel".equals(str)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否是 紧急程度
	 * @param str
	 * @return
	 */
	public static boolean isUrgentLevel(String str){
		if(str!=null){
			if("edoc.element.urgentlevel".equals(str)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否是日期字段
	 * @param str
	 * @return
	 */
	public static boolean isContainDate(String str){
		if(str!=null){
			if(str.contains("date")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将 整型转换为 String 
	 * @param i
	 * @return
	 */
	public static String getStr(Integer i){
		return String.valueOf(i);
	}
	
	/**
	 * 得到导航菜单
	 * @param userId
	 * @return
	 */
	public static List<BaseMobileMenu> getMenus(){
		User user = CurrentUser.get();
		MobileMenuManager mobileMenuManager = (MobileMenuManager)ApplicationContextHolder.getBean("mobileMenuManager");
		List<BaseMobileMenu> menus = mobileMenuManager.listMenuByUser(user.getId(), user.getLoginAccount());
		return menus;
	}
	public static BaseMobileMenu getMenu(String menuId){
		if(Strings.isBlank(menuId)){
			return null;
		}
		MobileMenuManager mobileMenuManager = (MobileMenuManager)ApplicationContextHolder.getBean("mobileMenuManager");
		return mobileMenuManager.getMenuById(menuId);
	}
	/**
	 * 自动分页
	 * @param baseAction
	 * @return
	 */
	public static String page(String baseAction,PageContext pageContext){
		StringBuffer result = new StringBuffer();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		int totalCount = 0;
		try {
			totalCount = Pagination.getRowCount();
		} catch (Exception e) {
		}
		int curPage = 1;
		try{
			curPage = Integer.parseInt(request.getParameter("page"));
		}catch(Exception e){
		}
		int maxResult = MobileConstants.PAGE_COUNTER;
		
		//页数
		int pageSize = (totalCount+maxResult-1)/maxResult;
		if(totalCount >0){
			result.append("<div id=\"page\">");
			//首页
			if(curPage !=1){
				result.append("<a href=\"").append(baseAction).append("&page=1&count="+totalCount+"&pageSize="+maxResult+"\">")
						.append(MobileConstants.getValueFromMobileRes("common.page.first"))
						.append("</a>");
			}
			//上一页
			if(curPage > 1){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&page="+(curPage-1)+"&count="+totalCount+"&pageSize="+maxResult+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.prev"))
				.append("</a>");
			}
			//下一页
			if(pageSize > 1 && (pageSize - curPage) > 0){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&page="+(curPage+1)+"&count="+totalCount+"&pageSize="+maxResult+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.next"))
				.append("</a>");
			}
			//尾页
			if(pageSize > 1 && pageSize != curPage){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&page="+pageSize+"&count="+totalCount+"&pageSize="+maxResult+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.last"))
				.append("</a>");
			}
			//total
			result.append("("+MobileConstants.getValueFromMobileRes("com.seeyon.v3x.mobile.g")).append(totalCount).append(MobileConstants.getValueFromMobileRes("com.seeyon.v3x.mobile.x"))
			.append(",").append(curPage).append("/").append(pageSize).append(")");
			result.append("</div>");
		}
		return result.toString();
	}
	
	public static String showAffair(Affair affair,Integer length,String symbol){
		if(affair == null) return "";
		StringBuffer result = new StringBuffer();
		String subject = "";
		//附件
		if(affair.isHasAttachments()){
			length -=2;
		}
		subject = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), length, affair.getForwardMember(), affair.getResentTime(), getOrgManager(), null) ;
		result.append(subject);
		User user = CurrentUser.get();
		if(affair.getMemberId().longValue() != user.getId()){
			String memberName = Functions.showMemberName(affair.getMemberId());
			result.append("(" + Constant.getString4CurrentUser("col.proxy") + memberName + ")");
		}
		StringBuffer r = new StringBuffer(Strings.toHTML(result.toString()));
		if(affair.isHasAttachments()){
			r.append("<span class='hasAttach'></span>");
		}
		return r.toString();
	}
	private static OrgManager orgManager = null;
	private static OrgManager getOrgManager(){
		if(orgManager == null){
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		}
		
		return orgManager;
	}
	/**
	 * 得到分页后的内容，并且加上分页的链接 首页 上一页 下一页 末页（页数/总页数）全文
	 * @param content
	 * @param page
	 * @return
	 */
	public static String pageContent(String content,String baseAction,PageContext pageContext){
		if(Strings.isBlank(content)) return "";
		StringBuffer result = new StringBuffer();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		int countSize = content.length();
		int totalPage = (countSize + MobileConstants.PAGE_COUNT -1)/MobileConstants.PAGE_COUNT;
		int curPage = 1;
		try {
			curPage = NumberUtils.stringToInt(request.getParameter("contentPage"), 1);
		} catch (Exception e) {
		}
		boolean showAll = false;//显示全文？
		try {
			String showAlls = request.getParameter("showAll");
			if(Strings.isNotBlank(showAlls)){
				showAll = Boolean.valueOf(showAlls);
			}
		} catch (Exception e) {
		}
		int start = (curPage -1)*MobileConstants.PAGE_COUNT;
		if(showAll){
			result.append("<div id=\"con\">");
			if(start < countSize)
				result.append(Functions.toHTML(content));
				//result.append(Functions.toHTML(content.substring(start,countSize)));
			result.append("</div>");
		}else{
			int end = curPage * MobileConstants.PAGE_COUNT;
			if(end > countSize){
				end = countSize;
			}
			result.append("<div id=\"con\">");
			result.append(Functions.toHTML(content.substring(start,end)));
			result.append("</div>");
			result.append("<div id=\"page\">");
			//首页
			if(curPage !=1){
				result.append("<a href=\"").append(baseAction).append("&contentPage=1\">")
						.append(MobileConstants.getValueFromMobileRes("common.page.first"))
						.append("</a>");
			}
			//上一页
			if(curPage > 1){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&contentPage="+(curPage-1)+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.prev"))
				.append("</a>");
			}
			//下一页
			if(totalPage > 1 && (totalPage - curPage) > 0){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&contentPage="+(curPage+1)+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.next"))
				.append("</a>");
			}
			//尾页
			if(totalPage > 1 && totalPage != curPage){
				result.append("&nbsp;&nbsp;");
				result.append("<a href=\"").append(baseAction).append("&contentPage="+totalPage+"\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.last"))
				.append("</a>");
			}
			if(totalPage > 1 && totalPage != curPage){
				result.append("&nbsp;&nbsp");
				result.append("<a href=\"").append(baseAction).append("&contentPage="+(curPage+1)+"&showAll=true\">")
				.append(MobileConstants.getValueFromMobileRes("common.page.all"))
				.append("</a>");
			}
			result.append("</div>");
			//total
		}
		return result.toString();
	}
	
	//得到人员在线消息
	public static Integer getOnlineMessageCount(Long userId){
		return MessageState.getInstance().getState(userId);
	}
	
	public static Map<String,Integer> getOnlineCount(String unIncludeMenu){
		User user = CurrentUser.get();
		OAManagerInterface oaManagerInterface = (OAManagerInterface)ApplicationContextHolder.getBean("oaManagerInterface");
		List<String> needCount = new ArrayList<String>();
		needCount.add("1");
		needCount.add("6");
		needCount.add("9");
		if(Strings.isNotBlank(unIncludeMenu)){
			needCount.remove(unIncludeMenu);
		}
		return  oaManagerInterface.getHomePageInfo(user.getId(), needCount);
	}
}
