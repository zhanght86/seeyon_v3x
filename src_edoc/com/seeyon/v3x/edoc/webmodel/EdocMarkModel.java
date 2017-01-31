/**
 * 
 */
package com.seeyon.v3x.edoc.webmodel;

import java.util.List;

import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class EdocMarkModel {
	
	/**
	 * 公文文号定义ID
	 */
	private Long markDefinitionId;
	
	/**
	 * 公文文号
	 */
	private String mark;
	
	/**
	 * 文号的当前值 
	 */
	
	private Integer currentNo;
	
	/**
	 * 公文文号授权部门
	 */
	private List<V3xOrgEntity> aclEntity;

	/**
	 * 公文字号
	 */
	private String wordNo;

	/**
	 * 公文文号类型，公文文号||内部文号
	 */
	private int markType;
	
	/**
	 * @return the aclDept
	 */

	public List<V3xOrgEntity> getAclEntity() {
		return aclEntity;
	}

	public void setAclEntity(List<V3xOrgEntity> aclEntity) {
		this.aclEntity = aclEntity;
	}

	/**
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * @return the markDefinitionId
	 */
	public Long getMarkDefinitionId() {
		return markDefinitionId;
	}

	/**
	 * @param markDefinitionId the markDefinitionId to set
	 */
	public void setMarkDefinitionId(Long markDefinitionId) {
		this.markDefinitionId = markDefinitionId;
	}

	public Integer getCurrentNo() {
		return currentNo;
	}

	public void setCurrentNo(Integer currentNo) {
		this.currentNo = currentNo;
	}

	public String getWordNo() {
		return wordNo;
	}

	public void setWordNo(String wordNo) {
		this.wordNo = wordNo;
	}
	
	/*以下方法用于,解析从前台提取过来得文号,进行解析,放置到对应字段*/
	private int docMarkCreateMode=-1;//公文文号生成方式,见com.seeyon.v3x.edoc.util.Constants定义
	private Long markId=-1L;//选择断号的时候;
	
	public void setDocMarkCreateMode(int docMarkCreateMode)
	{
		this.docMarkCreateMode=docMarkCreateMode;
	}
	public int getDocMarkCreateMode()
	{
		return this.docMarkCreateMode;
	}
	
	public void setMarkId(Long morkId)
	{
		this.markId=morkId;
	}
	public Long getMarkId()
	{
		return this.markId;
	}
	public int getMarkType() {
		return markType;
	}

	public void setMarkType(int markType) {
		this.markType = markType;
	}
	public String toString()
	{
		String tempMarkValue=getMarkDefinitionId()+ "|" + getMark() + "|";
		if(getCurrentNo()!=null){tempMarkValue+=getCurrentNo();}
		tempMarkValue+="|" + getDocMarkCreateMode();
		return tempMarkValue;
	}
	public static EdocMarkModel parse(String reqMark)
	{
		EdocMarkModel em=null;
		if(reqMark==null || "".equals(reqMark)){return em;}
		String[] arr = EdocUtil.parseDocMark(reqMark);
		if (arr == null || arr.length != 4) {return em;}
		em=new EdocMarkModel();
		if(!"".equals(arr[3])){em.setDocMarkCreateMode(Integer.valueOf(arr[3]));}
		em.setMark(arr[1]);
		if(!"".equals(arr[0])){em.setMarkDefinitionId(Long.valueOf(arr[0]));}
		if(!"".equals(arr[2])){em.setCurrentNo(Integer.valueOf(arr[2]));}
		if(!"".equals(arr[0])){em.setMarkId(Long.valueOf(arr[0]));}
		/*
        // 处理公文文号
        // 如果公文文号为空，不做任何处理
        String docMark = edocSummary.getDocMark();
        if (docMark != null && !docMark.equals("")) {
        	String[] arr = EdocUtil.parseDocMark(docMark);
        	if (arr != null && arr.length == 4) {
        		Integer t = Integer.valueOf(arr[3]);
        		String _edocMark = arr[1]; //需要保存到数据库中的公文文号        		
	        	if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_NEW) { // 选择了一个新的公文文号
	        		Long markDefinitionId = Long.valueOf(arr[0]);
	        		Integer currentNo = Integer.valueOf(arr[2]);
	        		edocMarkManager.createMark(markDefinitionId, currentNo, _edocMark, edocSummary.getId());
	        	}
	        	else if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_OLD) { // 选择了一个断号
	        		Long edocMarkId = Long.valueOf(arr[0]);
	        		edocMarkManager.createMarkByChooseNo(edocMarkId, edocSummary.getId());
	        	}
	        	else if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_INPUT) { // 手工输入一个公文文号
	        		edocMarkManager.createMark(_edocMark, edocSummary.getId());
	        	}
	        	edocSummary.setDocMark(_edocMark);
        	}
        }
		*/
		return em;
	}


	
	
}
