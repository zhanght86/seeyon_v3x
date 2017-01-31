package com.seeyon.v3x.edoc.util;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class DataUtil {
	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(DataUtil.class);
	public static EdocSummary requestToSummary(HttpServletRequest req,EdocSummary summary,long formId)
	{
		String fieldName="";
		String fieldValue="";
		EdocFormManager edocFormManager= (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		EdocElementManager elementManager=(EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");
		OrgManager orgManager=(OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List list = null;
		try{
		list=edocFormManager.getEdocFormElementByFormId(formId);
		}catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}
		EdocElement element=null;
		int i,len=list.size();
		for(i=0;i<len;i++)
		{
			element=elementManager.getEdocElementsById(((EdocFormElement)list.get(i)).getElementId());
			fieldName="my:"+element.getFieldName();
			fieldValue=req.getParameter(fieldName);
			if(fieldValue==null){continue;}
			setEdocSummaryValue(summary,element.getFieldName(),fieldValue);
		}
		summary.setFormId(formId);
		//读取隐藏的单位ID值
		String sendUnitIds=req.getParameter("my:send_unit_id");
		summary.setSendUnitId(sendUnitIds);
		if(("".equals(summary.getSendUnit())||summary.getSendUnit()==null)&&Strings.isNotBlank(sendUnitIds)){
			String[] arrSendUnit=sendUnitIds.split(",");
			for(int j=0;j<arrSendUnit.length;j++){
				String sendUnitStrTemp=arrSendUnit[j];
				if(Strings.isBlank(sendUnitStrTemp)){
					break;
				}
				String sendUnitStr=sendUnitStrTemp.split("\\|")[1];
				Long id = 0l;
				if (Strings.isNotBlank(sendUnitStr)){
					id=Long.parseLong(sendUnitStr);
					try {
						V3xOrgAccount account = orgManager.getAccountById(id);
						if (account != null)
							summary.setSendUnit(account.getName());
					} catch (BusinessException e) {
						log.error("获取单位错误：", e);
					}
				}
			}
		}
		String sendDeptId=req.getParameter("my:send_department_id");
		summary.setSendDepartmentId(sendDeptId);
		if(("".equals(summary.getSendDepartment())||summary.getSendDepartment()==null)&&Strings.isNotBlank(sendDeptId)){
			String[] arrSendDeptId=sendDeptId.split(",");
			for(int k=0;k<arrSendDeptId.length;k++){
				String sendDeptIdStr=arrSendDeptId[k];
				if(Strings.isBlank(sendDeptIdStr)){
					break;
				}
				String sendDeptStr=sendDeptIdStr.split("\\|")[1];
				Long id=Long.parseLong(sendDeptStr);
				try {
					summary.setSendDepartment(orgManager.getDepartmentById(id).getName());
				} catch (BusinessException e) {
					log.error("获取部门错误：", e);
				}
			}
		}
		String sendDeptId2=req.getParameter("my:send_department_id2");
		summary.setSendDepartmentId2(sendDeptId2);
		if(("".equals(summary.getSendDepartment2())||summary.getSendDepartment2()==null)&&Strings.isNotBlank(sendDeptId2)){
			String[] arrsendDeptId2=sendDeptId2.split(",");
			for(int l=0;l<arrsendDeptId2.length;l++){
				String sendDeptId2Str=arrsendDeptId2[l];
				if(Strings.isBlank(sendDeptId2Str)){
					break;
				}
				String sendDept2Str=sendDeptId2Str.split("\\|")[1];
				Long id=Long.parseLong(sendDept2Str);
				try {
					summary.setSendDepartment2(orgManager.getDepartmentById(id).getName());
				} catch (BusinessException e) {
					log.error("获取部门错误：", e);
				}
			}
		}
		summary.setSendToId(req.getParameter("my:send_to_id"));
		summary.setCopyToId(req.getParameter("my:copy_to_id"));
		summary.setReportToId(req.getParameter("my:report_to_id"));
		
		//summary.setIsunit(Boolean.parseBoolean(req.getParameter("my:isunit")));
		summary.setSendUnitId2(req.getParameter("my:send_unit_id2"));
		summary.setSendToId2(req.getParameter("my:send_to_id2"));
		//summary.setSendDepartment2(req.getParameter("my:send_department2"));
		//summary.setSendDepartmentId2(req.getParameter("my:send_department_id2"));
		if(req.getParameter("my:attachments")!=null&&!"".equals(req.getParameter("my:attachments"))){
			summary.setAttachments(req.getParameter("my:attachments"));
		}
		summary.setCopyToId2(req.getParameter("my:copy_to_id2"));
		summary.setReportToId2(req.getParameter("my:report_to_id2"));
		
		summary.setPrintUnitId(req.getParameter("my:print_unit_id"));
		//读取所属单位
		String orgAccountId=req.getParameter("orgAccountId");
		if(Strings.isNotBlank(orgAccountId)){
			 summary.setOrgAccountId(Long.parseLong(orgAccountId));
		}
		//读取公文模板模板预归档目录
		String archiveId=req.getParameter("archiveId");
		if(Strings.isNotBlank(archiveId)){
			summary.setArchiveId(Long.parseLong(archiveId));
		}
		//公文单中无发文单位元素
		if(Strings.isBlank(summary.getSendUnit()))
		{
			summary.setSendUnitId(null);
		}
		if(Strings.isBlank(summary.getSendUnit2()))
		{
			summary.setSendUnitId2(null);
		}
		//读取处理期限和提前提醒
		String temp=req.getParameter("deadline");
		if(temp!=null && !"".equals(temp))
		{
			try{
				summary.setDeadline(Long.parseLong(temp));
			}catch(Exception e)
			{
				summary.setDeadline(-1L);
			}
		}
		temp=req.getParameter("advanceRemind");
		if(temp!=null && !"".equals(temp))
		{
			try{
				summary.setAdvanceRemind(Long.parseLong(temp));
			}catch(Exception e)
			{
				summary.setAdvanceRemind(-1L);
			}
		}
		temp=req.getParameter("canTrack");
		if(Strings.isNotBlank(temp)){
			summary.setCanTrack(Integer.parseInt(temp));
		}else{
			summary.setCanTrack(0);
		}
		summary.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		return summary;		
	}
	/**
	 * 把html页面提交过来的input的值设置到edocsummary对象内
	 * @param summary
	 * @param inputName
	 * @param inputValue
	 * @return
	 */
	public static void setEdocSummaryValue(EdocSummary summary,String inputName,String inputValue)
	{		
		if("subject".equals(inputName))
		{
			summary.setSubject(inputValue);
		}
		else if("doc_type".equals(inputName))
		{			
			summary.setDocType(inputValue);
		}
		else if("send_type".equals(inputName))
		{			
			summary.setSendType(inputValue);
		}
		else if("doc_mark".equals(inputName))
		{								
			summary.setDocMark(inputValue);	
		}
		else if("doc_mark2".equals(inputName))
		{								
			summary.setDocMark2(inputValue);	
		}
		else if("serial_no".equals(inputName))
		{			
			summary.setSerialNo(inputValue);
		}
		else if("secret_level".equals(inputName))
		{			
			summary.setSecretLevel(inputValue);
		}
		else if("urgent_level".equals(inputName))
		{			
			summary.setUrgentLevel(inputValue);
		}
		else if("keep_period".equals(inputName))
		{
			if(inputValue==null || "".equals(inputValue)){summary.setKeepPeriod(null);return;}
			summary.setKeepPeriod(Integer.parseInt(inputValue));
		}
		else if("create_person".equals(inputName))
		{			
			summary.setCreatePerson(inputValue);
		}
		else if("send_unit".equals(inputName))
		{			
			summary.setSendUnit(inputValue);
		}
		else if("send_unit2".equals(inputName))
		{			
			summary.setSendUnit2(inputValue);
		}
		else if("send_department".equals(inputName))
		{			
			summary.setSendDepartment(inputValue);
		}
		else if("send_department2".equals(inputName))
		{			
			summary.setSendDepartment2(inputValue);
		}
		
		else if("attachments".equals(inputName))
		{			
			summary.setAttachments(inputValue);
		}
		
		else if("issuer".equals(inputName))
		{			
			summary.setIssuer(inputValue);
		}
		else if("signing_date".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setSigningDate(null);return;}
			summary.setSigningDate(Datetimes.parseDate(inputValue));
		}
		else if("createdate".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setCreateTime(null);return;}
			//客户BUG30352  根据数据库表结构的说明，startTime是可以用户录入的，createTime是系统自动生成的
			summary.setStartTime(new Timestamp(Datetimes.parseDate(inputValue).getTime()));
			//summary.setCreateTime(new Timestamp(Datetimes.parseDate(inputValue).getTime()));
		}		
		else if("packdate".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setPackTime(null);return;}
			summary.setPackTime(new Timestamp(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("send_to".equals(inputName))
		{			
			summary.setSendTo(inputValue);
		}
		else if("send_to2".equals(inputName))
		{			
			summary.setSendTo2(inputValue);
		}
		else if("copy_to".equals(inputName))
		{			
			summary.setCopyTo(inputValue);
		}
		else if("copy_to2".equals(inputName))
		{			
			summary.setCopyTo2(inputValue);
		}
		else if("report_to".equals(inputName))
		{			
			summary.setReportTo(inputValue);
		}
		else if("report_to2".equals(inputName))
		{			
			summary.setReportTo2(inputValue);
		}
		else if("keyword".equals(inputName))
		{			
			summary.setKeywords(inputValue);
		}
		else if("print_unit".equals(inputName))
		{			
			summary.setPrintUnit(inputValue);
		}
		else if("copies".equals(inputName))
		{			
			if(inputValue==null || "".equals(inputValue)){summary.setCopies(null);return;}
			summary.setCopies(Integer.parseInt(inputValue));
		}
		else if("copies2".equals(inputName))
		{			
			if(inputValue==null || "".equals(inputValue)){summary.setCopies2(null);return;}
			summary.setCopies2(Integer.parseInt(inputValue));
		}
		else if("printer".equals(inputName))
		{			
			summary.setPrinter(inputValue);
		}
		else if("string1".equals(inputName))
		{			
			summary.setVarchar1(inputValue);
		}
		else if("string2".equals(inputName))
		{			
			summary.setVarchar2(inputValue);
		}
		else if("string3".equals(inputName))
		{			
			summary.setVarchar3(inputValue);
		}
		else if("string4".equals(inputName))
		{			
			summary.setVarchar4(inputValue);
		}
		else if("string5".equals(inputName))
		{			
			summary.setVarchar5(inputValue);
		}
		else if("string6".equals(inputName))
		{			
			summary.setVarchar6(inputValue);
		}
		else if("string7".equals(inputName))
		{			
			summary.setVarchar7(inputValue);
		}
		else if("string8".equals(inputName))
		{			
			summary.setVarchar8(inputValue);
		}
		else if("string9".equals(inputName))
		{			
			summary.setVarchar9(inputValue);
		}
		else if("string10".equals(inputName))
		{			
			summary.setVarchar10(inputValue);
		}
		else if("string11".equals(inputName))
		{			
			summary.setVarchar11(inputValue);
		}
		else if("string12".equals(inputName))
		{			
			summary.setVarchar12(inputValue);
		}
		else if("string13".equals(inputName))
		{			
			summary.setVarchar13(inputValue);
		}
		else if("string14".equals(inputName))
		{			
			summary.setVarchar14(inputValue);
		}
		else if("string15".equals(inputName))
		{			
			summary.setVarchar15(inputValue);
		}
		else if("string16".equals(inputName))
		{			
			summary.setVarchar16(inputValue);
		}
		else if("string17".equals(inputName))
		{			
			summary.setVarchar17(inputValue);
		}
		else if("string18".equals(inputName))
		{			
			summary.setVarchar18(inputValue); 
		}
		else if("string19".equals(inputName))
		{			
			summary.setVarchar19(inputValue);
		}
		else if("string20".equals(inputName))
		{			
			summary.setVarchar20(inputValue);
		}
		else if("text1".equals(inputName))
		{			
			summary.setText1(inputValue);
		}
		else if("text2".equals(inputName))
		{			
			summary.setText2(inputValue);
		}
		else if("text3".equals(inputName))
		{			
			summary.setText3(inputValue);
		}
		else if("text4".equals(inputName))
		{			
			summary.setText4(inputValue);
		}
		else if("text5".equals(inputName))
		{			
			summary.setText5(inputValue);
		}
		else if("text6".equals(inputName))
		{			
			summary.setText6(inputValue);
		}
		else if("text7".equals(inputName))
		{			
			summary.setText7(inputValue);
		}
		else if("text8".equals(inputName))
		{			
			summary.setText8(inputValue);
		}
		else if("text9".equals(inputName))
		{			
			summary.setText9(inputValue);
		}
		else if("text10".equals(inputName))
		{			
			summary.setText10(inputValue);
		}
		else if("integer1".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger1(null);return;}			
			summary.setInteger1(Integer.parseInt(inputValue));
		}
		else if("integer2".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger2(null);return;}
			summary.setInteger2(Integer.parseInt(inputValue));
		}
		else if("integer3".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger3(null);return;}
			summary.setInteger3(Integer.parseInt(inputValue));
		}
		else if("integer4".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger4(null);return;}
			summary.setInteger4(Integer.parseInt(inputValue));
		}
		else if("integer5".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger5(null);return;}
			summary.setInteger5(Integer.parseInt(inputValue));
		}
		else if("integer6".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger6(null);return;}
			summary.setInteger6(Integer.parseInt(inputValue));
		}
		else if("integer7".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger7(null);return;}
			summary.setInteger7(Integer.parseInt(inputValue));
		}
		else if("integer8".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger8(null);return;}
			summary.setInteger8(Integer.parseInt(inputValue));
		}
		else if("integer9".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger9(null);return;}
			summary.setInteger9(Integer.parseInt(inputValue));
		}
		else if("integer10".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger10(null);return;}
			summary.setInteger10(Integer.parseInt(inputValue));
		}
		else if("integer11".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger11(null);return;}
			summary.setInteger11(Integer.parseInt(inputValue));
		}
		else if("integer12".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger12(null);return;}
			summary.setInteger12(Integer.parseInt(inputValue));
		}
		else if("integer13".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger13(null);return;}
			summary.setInteger13(Integer.parseInt(inputValue));
		}
		else if("integer14".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger14(null);return;}
			summary.setInteger14(Integer.parseInt(inputValue));
		}
		else if("integer15".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger15(null);return;}
			summary.setInteger15(Integer.parseInt(inputValue));
		}
		else if("integer16".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger16(null);return;}
			summary.setInteger16(Integer.parseInt(inputValue));
		}
		else if("integer17".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger17(null);return;}
			summary.setInteger17(Integer.parseInt(inputValue));
		}
		else if("integer18".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setInteger18(null);return;}
			summary.setInteger18(Integer.parseInt(inputValue));
		}
		else if("integer19".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){	summary.setInteger19(null);return;}
			summary.setInteger19(Integer.parseInt(inputValue));
		}
		else if("integer20".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){	summary.setInteger20(null);return;}
			summary.setInteger20(Integer.parseInt(inputValue));
		}
		else if("decimal1".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal1(null);return;}
			summary.setDecimal1(Double.parseDouble(inputValue));
		}
		else if("decimal2".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal2(null); return;}
			summary.setDecimal2(Double.parseDouble(inputValue));
		}
		else if("decimal3".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal3(null);return;}
			summary.setDecimal3(Double.parseDouble(inputValue));
		}
		else if("decimal4".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal4(null);return;}
			summary.setDecimal4(Double.parseDouble(inputValue));
		}
		else if("decimal5".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal5(null);return;}
			summary.setDecimal5(Double.parseDouble(inputValue));
		}
		else if("decimal6".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal6(null);return;}
			summary.setDecimal6(Double.parseDouble(inputValue));
		}
		else if("decimal7".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal7(null);return;}
			summary.setDecimal7(Double.parseDouble(inputValue));
		}
		else if("decimal8".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal8(null);return;}
			summary.setDecimal8(Double.parseDouble(inputValue));
		}
		else if("decimal9".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal9(null);return;}
			summary.setDecimal9(Double.parseDouble(inputValue));
		}
		else if("decimal10".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal10(null);return;}
			summary.setDecimal10(Double.parseDouble(inputValue));
		}
		else if("decimal11".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal11(null);return;}
			summary.setDecimal11(Double.parseDouble(inputValue));
		}
		else if("decimal12".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal12(null);return;}
			summary.setDecimal12(Double.parseDouble(inputValue));
		}
		else if("decimal13".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal13(null);return;}
			summary.setDecimal13(Double.parseDouble(inputValue));
		}
		else if("decimal14".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal14(null);return;}
			summary.setDecimal14(Double.parseDouble(inputValue));
		}
		else if("decimal15".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal15(null);return;}
			summary.setDecimal15(Double.parseDouble(inputValue));
		}
		else if("decimal16".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal16(null);return;}
			summary.setDecimal16(Double.parseDouble(inputValue));
		}
		else if("decimal17".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal17(null);return;}
			summary.setDecimal17(Double.parseDouble(inputValue));
		}
		else if("decimal18".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal18(null);return;}
			summary.setDecimal18(Double.parseDouble(inputValue));
		}
		else if("decimal19".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal19(null);return;}
			summary.setDecimal19(Double.parseDouble(inputValue));
		}
		else if("decimal20".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDecimal20(null);return;}
			summary.setDecimal20(Double.parseDouble(inputValue));
		}		
		else if("date1".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate1(null);return;}
			summary.setDate1(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date2".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate2(null);return;}
			summary.setDate2(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date3".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate3(null);return;}
			summary.setDate3(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date4".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate4(null);return;}
			summary.setDate4(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date5".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate5(null);return;}
			summary.setDate5(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date6".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate6(null);return;}
			summary.setDate6(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date7".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate7(null);return;}
			summary.setDate7(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date8".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate8(null);return;}
			summary.setDate8(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date9".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate9(null);return;}
			summary.setDate9(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date10".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate10(null);return;}
			summary.setDate10(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date11".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate11(null);return;}
			summary.setDate11(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date12".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate12(null);return;}
			summary.setDate12(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date13".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate13(null);return;}
			summary.setDate13(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date14".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate14(null);return;}
			summary.setDate14(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date15".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate15(null);return;}
			summary.setDate15(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date16".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate16(null);return;}
			summary.setDate16(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date17".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate17(null);return;}
			summary.setDate17(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date18".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate18(null);return;}
			summary.setDate18(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date19".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate19(null);return;}
			summary.setDate19(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("date20".equals(inputName))
		{	
			if(inputValue==null || "".equals(inputValue)){summary.setDate20(null);return;}
			summary.setDate20(new java.sql.Date(Datetimes.parseDate(inputValue).getTime()));
		}
		else if("list1".equals(inputName))
		{			
			summary.setList1(inputValue);
		}
		else if("list2".equals(inputName))
		{			
			summary.setList2(inputValue);
		}
		else if("list3".equals(inputName))
		{			
			summary.setList3(inputValue);
		}
		else if("list4".equals(inputName))
		{			
			summary.setList4(inputValue);
		}
		else if("list5".equals(inputName))
		{			
			summary.setList5(inputValue);
		}
		else if("list6".equals(inputName))
		{			
			summary.setList6(inputValue);
		}
		else if("list7".equals(inputName))
		{			
			summary.setList7(inputValue);
		}
		else if("list8".equals(inputName))
		{			
			summary.setList8(inputValue);
		}
		else if("list9".equals(inputName))
		{			
			summary.setList9(inputValue);
		}
		else if("list10".equals(inputName))
		{			
			summary.setList10(inputValue);
		}
		else if("list11".equals(inputName))
		{			
			summary.setList11(inputValue);
		}
		else if("list12".equals(inputName))
		{			
			summary.setList12(inputValue);
		}
		else if("list13".equals(inputName))
		{			
			summary.setList13(inputValue);
		}
		else if("list14".equals(inputName))
		{			
			summary.setList14(inputValue);
		}
		else if("list15".equals(inputName))
		{			
			summary.setList15(inputValue);
		}
		else if("list16".equals(inputName))
		{			
			summary.setList16(inputValue);
		}
		else if("list17".equals(inputName))
		{			
			summary.setList17(inputValue);
		}
		else if("list18".equals(inputName))
		{			
			summary.setList18(inputValue);
		}
		else if("list19".equals(inputName))
		{			
			summary.setList19(inputValue);
		}
		else if("list20".equals(inputName))
		{			
			summary.setList20(inputValue);
		}
		else if("string21".equals(inputName))
		{			
			summary.setVarchar21(inputValue);
		}
		else if("string22".equals(inputName))
		{			
			summary.setVarchar22(inputValue);
		}
		else if("string23".equals(inputName))
		{			
			summary.setVarchar23(inputValue);
		}
		else if("string24".equals(inputName))
		{			
			summary.setVarchar24(inputValue);
		}
		else if("string25".equals(inputName))
		{			
			summary.setVarchar25(inputValue);
		}
		else if("string26".equals(inputName))
		{			
			summary.setVarchar26(inputValue);
		}
		else if("string27".equals(inputName))
		{			
			summary.setVarchar27(inputValue);
		}
		else if("string28".equals(inputName))
		{			
			summary.setVarchar28(inputValue);
		}
		else if("string29".equals(inputName))
		{			
			summary.setVarchar29(inputValue);
		}
		else if("string30".equals(inputName))
		{			
			summary.setVarchar30(inputValue);
		}
		else if("text11".equals(inputName))
		{			
			summary.setText11(inputValue);
		}
		else if("text12".equals(inputName))
		{			
			summary.setText12(inputValue);
		}
		else if("text13".equals(inputName))
		{			
			summary.setText13(inputValue);
		}
		else if("text14".equals(inputName))
		{			
			summary.setText14(inputValue);
		}
		else if("text15".equals(inputName))
		{			
			summary.setText15(inputValue);
		}
		return;
	}

}
