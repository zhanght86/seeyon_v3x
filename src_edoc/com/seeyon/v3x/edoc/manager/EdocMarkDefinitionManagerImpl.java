/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.edoc.EdocEnum.MarkCategory;
import com.seeyon.v3x.edoc.dao.EdocMarkDefinitionDAO;
import com.seeyon.v3x.edoc.domain.EdocMarkAcl;
import com.seeyon.v3x.edoc.domain.EdocMarkCategory;
import com.seeyon.v3x.edoc.domain.EdocMarkDefinition;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class EdocMarkDefinitionManagerImpl implements EdocMarkDefinitionManager{
	private final static Log log = LogFactory.getLog(EdocMarkDefinitionManagerImpl.class);
	private EdocMarkDefinitionDAO edocMarkDefinitionDAO;
	
	private EdocMarkCategoryManager edocMarkCategory;
	
    private OrgManager orgManager;
    
    private TempleteManager templeteManager;
	
	public TempleteManager getTempleteManager() {
		return templeteManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
		
	public void setEdocMarkDefinitionDAO(
			EdocMarkDefinitionDAO edocMarkDefinitionDAO) {
		this.edocMarkDefinitionDAO = edocMarkDefinitionDAO;
	}
	
	public void setEdocMarkCategoryManager(EdocMarkCategoryManager edocMarkCategory){
		this.edocMarkCategory = edocMarkCategory;
	}
	
	public EdocMarkDefinition getMarkDefinition(long id) {
		return edocMarkDefinitionDAO.get(id);
	}
	
	/**
     * 方法描述：保存公文文号定义
     */
	public void saveMarkDefinition(EdocMarkDefinition edocMarkDefinition){
		this.edocMarkDefinitionDAO.saveEdocMarkDefinition(edocMarkDefinition);
	}
	
	/**
     * 方法描述：修改公文文号定义
     */
	public void updateMarkDefinition(EdocMarkDefinition edocMarkDefinition){
		this.edocMarkDefinitionDAO.updateEdocMarkDefinition(edocMarkDefinition);
	}
	
   	/**
     * 方法描述：删除公文文号定义
     */
	public void deleteMarkDefinition(EdocMarkDefinition edocMarkDefinition){
		this.edocMarkDefinitionDAO.deleteEdocMarkDefinition(edocMarkDefinition);
	}
	
	/**
     * 方法描述：根据公文文号定义ID查询公文文号定义
     */
	public EdocMarkDefinition queryMarkDefinitionById(Long edocMarkDefinitionId){
		return this.edocMarkDefinitionDAO.findEdocMarkDefinitionById(edocMarkDefinitionId);
	}	
	
	public EdocMarkModel markDef2Mode(EdocMarkDefinition markDef,String yearNo,Integer curentno)
	{
		String yearNoStr=yearNo;
		if(yearNoStr==null || "".equals(yearNoStr))
		{
			Calendar cal = Calendar.getInstance();
			yearNoStr = String.valueOf(cal.get(Calendar.YEAR));
		}
		EdocMarkModel model = new EdocMarkModel();
		model.setMarkDefinitionId(markDef.getId());
		String wordNo = markDef.getWordNo();
		model.setWordNo(wordNo);
		model.setMarkType(markDef.getMarkType());
		String expression = markDef.getExpression();
		EdocMarkCategory category = markDef.getEdocMarkCategory();
		if(wordNo.indexOf("\\")>=0) wordNo = wordNo.replaceAll("\\\\", "\\\\\\\\");
		if(wordNo.indexOf("$")>=0) wordNo = wordNo.replaceAll("\\$", "\\\\\\$");
		
		expression = expression.replaceFirst("\\$WORD", wordNo);
		if (category.getYearEnabled()) {
			expression = expression.replaceFirst("\\$YEAR", yearNoStr);
		}
		int currentNo = category.getCurrentNo();
		model.setCurrentNo(currentNo);
		
		String flowNo = String.valueOf(currentNo);
		int length = markDef.getLength();			
		int maxNo = category.getMaxNo();
		int curNoLen = String.valueOf(currentNo).length();
		int maxNoLen = String.valueOf(maxNo).length();
		if (length > 0 && length == maxNoLen) {
			flowNo = "";
			for (int j = curNoLen; j < length; j++) {
				flowNo += "0";
			}
			if(curentno!=null){
				flowNo += String.valueOf(curentno);
			}else{
				flowNo += String.valueOf(currentNo);
			}
		}
		expression = expression.replaceFirst("\\$NO", flowNo);
		model.setMark(expression);
		return model;
	}
	
	public List<EdocMarkModel> getEdocMarkDefs(Long domainId, String condition, String textfield) throws BusinessException {
		List<EdocMarkDefinition> markDefs = edocMarkDefinitionDAO.getEdocMarkDefs(domainId,true);
		List<EdocMarkModel> results = new ArrayList<EdocMarkModel>();
		Calendar cal = Calendar.getInstance();
		String yearNo = String.valueOf(cal.get(Calendar.YEAR));
		for (int i = 0; i < markDefs.size(); i++) {
			EdocMarkDefinition markDef = markDefs.get(i);
			EdocMarkModel model = markDef2Mode(markDef,yearNo,null);
			List<V3xOrgEntity> aclEntity = new ArrayList<V3xOrgEntity>();
			Set<EdocMarkAcl> markAcls = markDef.getEdocMarkAcls();
			java.util.Iterator<EdocMarkAcl> iterator = markAcls.iterator();
			while (iterator.hasNext()) {
				EdocMarkAcl markAcl = iterator.next();
				V3xOrgEntity orgEntity = orgManager.getEntity(markAcl.getAclType(), markAcl.getDeptId());
				aclEntity.add(orgEntity);
			}
			model.setAclEntity(aclEntity);
			
			if(StringUtils.isNotBlank(condition)){
				if ("mark".equals(condition)){ 
					if (StringUtils.isBlank(textfield) && model.getMarkType() == 0) {
						results.add(model);
					} else if (StringUtils.contains(model.getMark(), textfield) && model.getMarkType() == 0){
						results.add(model);
					}
				} else if ("edocInMark".equals(condition)) {
					if (StringUtils.isBlank(textfield) && model.getMarkType() == 1) {
						results.add(model);
					} else if(StringUtils.contains(model.getMark(), textfield) && model.getMarkType() == 1){
						results.add(model);
					}
				} else if("markType".equals(condition)){
					if(model.getMarkType() == NumberUtils.toInt(textfield)){
						results.add(model);
					}
				}
			}else{
				results.add(model);
			}
		}
		
		return results;
	}	
    
	/**
	 * 方法描述：保存公文文号定义，同时保存公文文号类型和文号授权
	 */
	public void saveMarkDefinition(EdocMarkDefinition def,EdocMarkCategory cat){
		if(cat.getCodeMode()==Constants.MODE_SERIAL){
			//todo
			this.edocMarkCategory.saveCategory(cat);
		}
		this.saveMarkDefinition(def);
	}
	
	

	/**
	 * 根据授权部门查找公文文号定义。
	 * @param deptIds  文号授权部门id（以,号分隔）
	 * @return List<EdocMarkModel>
	 */
	public List<EdocMarkModel> getEdocMarkDefinitions(String deptIds,int markType) {
		
		//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
		
		List<EdocMarkDefinition> markDefs = edocMarkDefinitionDAO.getMyEdocMarkDefs(deptIds,true, markType);		
		List<EdocMarkModel> results = new ArrayList<EdocMarkModel>();
		Calendar cal = Calendar.getInstance();
		String yearNo = String.valueOf(cal.get(Calendar.YEAR)); 		
		for(EdocMarkDefinition markDef : markDefs) {			
			EdocMarkModel model = markDef2Mode(markDef,yearNo,null);
			/*
			model.setMarkDefinitionId(markDef.getId());
			model.setWordNo(markDef.getWordNo());
			String expression = markDef.getExpression();
			EdocMarkCategory category = markDef.getEdocMarkCategory();
			expression = expression.replaceFirst("\\$WORD", markDef.getWordNo());
			if (category.getYearEnabled()) {
				expression = expression.replaceFirst("\\$YEAR", yearNo);
			}
			int currentNo = category.getCurrentNo();
			model.setCurrentNo(currentNo);
			String flowNo = String.valueOf(currentNo);
			int length = markDef.getLength();			
			int maxNo = category.getMaxNo();
			int curNoLen = String.valueOf(currentNo).length();
			int maxNoLen = String.valueOf(maxNo).length();
			if (length > 0 && length == maxNoLen) {
				flowNo = "";
				for (int j = curNoLen; j < length; j++) {
					flowNo += "0";
				}
				flowNo += String.valueOf(currentNo);
			}
			expression = expression.replaceFirst("\\$NO", flowNo);
			model.setMark(expression);
			*/
			results.add(model);
		}
		return results;
	}
	public List<EdocMarkDefinition> getEdocMarkDefinitionsByCategory(Long categoryId) {
		
		//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();		
		List<EdocMarkDefinition> markDefs = edocMarkDefinitionDAO.getEdocMarkDefsByCategoryId(categoryId);
		
		return markDefs;
	}
	
	public Short judgeStreamType(Long definitionId)throws BusinessException{
		Short streamType = 0;
		EdocMarkDefinition def = this.queryMarkDefinitionById(definitionId);
		EdocMarkCategory category  = def.getEdocMarkCategory();
		if("0".equals(category.getCodeMode().toString())){
			streamType = 0;
		}else if("1".equals(category.getCodeMode().toString())){
			streamType = 1;
		}
		return streamType;
	}
	
//	public String getEdocMark(long definitionId, Integer currentNo) {
//		
//		Calendar cal = Calendar.getInstance();
//		int currentYear = cal.get(Calendar.YEAR); 
//		String currentYearStr = new Integer(currentYear).toString();
//		
//		EdocMarkDefinition def  = queryMarkDefinitionById(definitionId);
//		String expression = def.getExpression();
//		expression = expression.replaceFirst("\\$WORD", def.getWordNo());
//		
//		expression = expression.replaceFirst("\\$YEAR", currentYearStr);
//		
//		if(currentNo > 0) {
//			expression = expression.replaceFirst("\\$NO", currentNo.toString());
//		}
//		else{
//			return "";
//			//expression = expression.replace("\\$NO", def.getEdocMarkCategory().getCurrentNo().toString());
//		}
//		
//		return expression;
//	}
	
	public Boolean containEdocMarkDefinition(String wordNo, long domainId,int markType) {		
		return containEdocMarkDefinition(0, wordNo, domainId,markType);
	}
	
	public Boolean containEdocMarkDefinition(long markDefId, String wordNo, long domainId,int markType) {
		if (markDefId != 0) {
			return edocMarkDefinitionDAO.containEdocMarkDef(markDefId, wordNo, domainId,markType);
		}
		else {
			return edocMarkDefinitionDAO.containEdocMarkDef(wordNo, domainId,markType);
		}		
	}
	
	public boolean containEdocMarkDefInCategory(long categoryId) {
		List<EdocMarkDefinition> markDefs = edocMarkDefinitionDAO.getEdocMarkDefsByCategoryId(categoryId);
		if (markDefs != null && markDefs.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void logicalDeleteMarkDefinition(long defId, short status){
		
		edocMarkDefinitionDAO.updateMarkDefinitionStatus(defId, status);
	}
	/**
     * 将EdocMarkCategory自增长,内部文号
     * @param markDefinitionId
     */
    public void setEdocMarkCategoryIncrement(Long markDefinitionId){
    	EdocMarkDefinition markDef = queryMarkDefinitionById(markDefinitionId);
    	EdocMarkCategory edocMarkCate = markDef.getEdocMarkCategory();
		edocMarkCate.setCurrentNo(edocMarkCate.getCurrentNo()+1);
		edocMarkCategory.updateCategory(edocMarkCate);
		setEdocMarkDefinitionPublished(markDef);
		
    }
    private void setEdocMarkDefinitionPublished(EdocMarkDefinition markDef) {
		//设置已经使用。
    	if(markDef.getStatus().shortValue() == Constants.EDOC_MARK_DEFINITION_DRAFT){
    		markDef.setStatus(Constants.EDOC_MARK_DEFINITION_PUBLISHED);
    		updateMarkDefinition(markDef);
    	}
	}
    public void setEdocMarkDefinitionUsed(Long markDefId) {
    	if(markDefId==null) return ;
    	EdocMarkDefinition markDef = queryMarkDefinitionById(markDefId);
    	if(markDef!=null)
    		setEdocMarkDefinitionPublished(markDef);
    }
    /**
     * 判断公文文号定义是否已经被删除
     */
	public int judgeEdocDefinitionExsit(Long definitionId){
		return edocMarkDefinitionDAO.judgeEdocDefinitionExsit(definitionId);
	}
	public EdocMarkModel  getEdocMarkDefinitionById(Long definitionId){

		//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
		
		EdocMarkDefinition markDef = edocMarkDefinitionDAO.get(definitionId);		
		Calendar cal = Calendar.getInstance();
		String yearNo = String.valueOf(cal.get(Calendar.YEAR)); 		
		EdocMarkModel model = markDef2Mode(markDef,yearNo,null);
		return model;
	}
	@Override
	public EdocMarkModel getEdocMarkByTempleteId(Long templeteId,
			MarkCategory category) {
		
		if(templeteId == null ) return null;
		
		Templete templete = templeteManager.get(templeteId);
		
		if(templete == null){
			log.error("查找公文模板失败EdocMarkDefinitionManagerImpl.getEdocMarkByTempleteId"+templeteId);
			return null;
		}
		EdocSummary tsummary = (EdocSummary)XMLCoder.decoder(templete.getSummary());
	
		String templeteDefinitionId = "" ; //模板绑定的文号定义ID
		if(category.ordinal() == MarkCategory.serialNo.ordinal() && Strings.isNotBlank(tsummary.getSerialNo()) ) {
			templeteDefinitionId = tsummary.getSerialNo().split("[|]")[0];
		}else if(category.ordinal() == MarkCategory.docMark.ordinal() && Strings.isNotBlank(tsummary.getDocMark())) {
			templeteDefinitionId = tsummary.getDocMark().split("[|]")[0];
		}else if(category.ordinal() == MarkCategory.docMark2.ordinal()  && Strings.isNotBlank(tsummary.getDocMark2()))  {
			templeteDefinitionId = tsummary.getDocMark2().split("[|]")[0];
		}
		
		if(Strings.isBlank(templeteDefinitionId))  return null;
		
		return getEdocMarkDefinitionById(Long.parseLong(templeteDefinitionId));
	}
}
