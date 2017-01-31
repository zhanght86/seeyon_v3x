/**
 * 
 */
package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.dao.EdocMarkDAO;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocMark;
import com.seeyon.v3x.edoc.domain.EdocMarkCategory;
import com.seeyon.v3x.edoc.domain.EdocMarkDefinition;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.edoc.webmodel.EdocMarkNoModel;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class EdocMarkManagerImpl implements EdocMarkManager {
	
	private EdocMarkDAO edocMarkDAO;
	
	private EdocMarkCategoryManager edocMarkCategoryManager;
	
	private EdocMarkDefinitionManager edocMarkDefinitionManager;
	
	private EdocSummaryDao edocSummaryDao;
//	private EdocMarkHistoryManager edocMarkHistoryManager;
	

	public EdocMarkDefinitionManager getEdocMarkDefinitionManager() {
		return edocMarkDefinitionManager;
	}

	public void setEdocMarkDefinitionManager(
			EdocMarkDefinitionManager edocMarkDefinitionManager) {
		this.edocMarkDefinitionManager = edocMarkDefinitionManager;
	}

//	public EdocMarkHistoryManager getEdocMarkHistoryManager() {
//		return edocMarkHistoryManager;
//	}
//
//	public void setEdocMarkHistoryManager(
//			EdocMarkHistoryManager edocMarkHistoryManager) {
//		this.edocMarkHistoryManager = edocMarkHistoryManager;
//	}

	public EdocMarkCategoryManager getEdocMarkCategoryManager() {
		return edocMarkCategoryManager;
	}

	public EdocMarkDAO getEdocMarkDAO() {
		return edocMarkDAO;
	}
	
	public void setEdocMarkDAO(EdocMarkDAO edocMarkDAO) {
		this.edocMarkDAO = edocMarkDAO;
	}
	
	public void setEdocMarkCategoryManager(EdocMarkCategoryManager edocMarkCategoryManager){
		this.edocMarkCategoryManager = edocMarkCategoryManager;
	}
	
	/**
     * 方法描述：保存公文文号
     */
    public void save(EdocMark edocMark) {
    	this.edocMarkDAO.save(edocMark);
    }
    /**
     * 根据ID返回EdocMark对象
     * @param edocMarkId  ID
     * @return
     */
    public EdocMark getEdocMark(Long edocMarkId){
    	return this.getEdocMarkDAO().get(edocMarkId);
    }
    /**
     * 方法描述：保存公文文号，并更新当前值
     * @param edocMark  公文文号对象
     * @param catId     公文类别id
     * @param currentNo 提供给用户选择的公文文号的当前值
     */
    public void save(EdocMark edocMark,Long catId,int currentNo){
    	this.save(edocMark);
        //    	更新当前值
    	this.edocMarkCategoryManager.increaseCurrentNo(catId, currentNo);
    }
    
    /**
     * 删除公文文号
     * @param id  公文文号id
     */
    public void deleteEdocMark(long id){
    	this.edocMarkDAO.delete(id);
    }
    
    /**
     * 方法描述：拟文时创建文号
     * todo:查询当前文号是否被使用，如果已经被使用，则不创建文号记录 add by handy,2007-10-16
     */    
    public synchronized void createMark(Long definitionId, Integer currentNo, String docMark, Long edocId,int markNum) {
    	//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
    	User user = CurrentUser.get();
    	EdocMarkDefinition markDef = edocMarkDefinitionManager.queryMarkDefinitionById(definitionId);
    	if(markDef==null){return;}
    	
    	EdocMark edocMark = new EdocMark();
    	edocMark.setIdIfNew();
    	edocMark.setEdocMarkDefinition(markDef);
    	edocMark.setCreateTime(new Date());
    	edocMark.setEdocId(edocId);
    	edocMark.setDocMark(docMark);
    	edocMark.setCreateUserId(user.getId());
    	edocMark.setStatus(Constants.EDOC_MARK_USED);    	
    	edocMark.setDocMarkNo(currentNo);
    	EdocMarkCategory edocMarkCategory = markDef.getEdocMarkCategory();
    	edocMark.setCategoryId(edocMarkCategory.getId());
    	edocMark.setDomainId(user.getLoginAccount());
    	edocMark.setMarkNum(markNum);
    	this.save(edocMark);
    	
    	List<EdocMarkDefinition> mds=edocMarkDefinitionManager.getEdocMarkDefinitionsByCategory(edocMarkCategory.getId());
    	if(mds!=null && mds.size()>1)
    	{//多个公文模板共用一个流水号
    		for(EdocMarkDefinition def:mds)
    		{
    			if(definitionId.longValue()==def.getId().longValue()){continue;}
    			edocMark = new EdocMark();
    	    	edocMark.setIdIfNew();
    	    	edocMark.setEdocMarkDefinition(def);
    	    	edocMark.setCreateTime(new Date());
    	    	edocMark.setEdocId(edocId);
    	    	edocMark.setDocMark(edocMarkDefinitionManager.markDef2Mode(def,null,currentNo).getMark());
    	    	edocMark.setCreateUserId(user.getId());
    	    	edocMark.setStatus(Constants.EDOC_MARK_USED);    	
    	    	edocMark.setDocMarkNo(currentNo);
    	    	//TODO 这里不需要再次读取分类，下面行可以注释
    	    	edocMarkCategory = def.getEdocMarkCategory();
    	    	edocMark.setCategoryId(edocMarkCategory.getId());
    	    	edocMark.setDomainId(user.getLoginAccount());
    	    	edocMark.setMarkNum(markNum);
    	    	this.save(edocMark);
    		}
    	}
    	// 文号序号增1
    	if(currentNo>=edocMarkCategory.getCurrentNo()) {
    		edocMarkCategory.setCurrentNo(currentNo + 1);
    		//如下行应该修改为update
    		edocMarkCategoryManager.saveCategory(edocMarkCategory);
    	}
    	setEdocMarkDefinitionPublished(markDef);
    }

	private void setEdocMarkDefinitionPublished(EdocMarkDefinition markDef) {
		//设置已经使用。
    	if(markDef.getStatus().shortValue() == Constants.EDOC_MARK_DEFINITION_DRAFT){
    		markDef.setStatus(Constants.EDOC_MARK_DEFINITION_PUBLISHED);
    		edocMarkDefinitionManager.saveMarkDefinition(markDef);
    	}
	}
    //修改文单的时候，若修改了文号，断开当前公文与前文号的联系。
    public void disconnectionEdocSummary(long edocSummaryId,int markNum)
    {
    	List<EdocMark> list = edocMarkDAO.findEdocMarkByEdocSummaryIdAndNum(edocSummaryId,markNum);
    	if(list!=null&&list.size()!=0){
    		for(EdocMark edocMark:list){
    			if(edocMark==null)continue;
    			else{
    				edocMark.setEdocId(-1L);
    			}
    			edocMarkDAO.save(edocMark);
    		}
    	}
    }
    
    /**
     * 创建手工输入的公文文号。
     * @param docMark 公文文号
     * @param edocId 公文id
     */
    public void createMark(String docMark, Long edocId,int markNum) {
//    	检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
    	User user = CurrentUser.get();
    	EdocMark edocMark = new EdocMark();
    	edocMark.setIdIfNew();
    	edocMark.setCreateTime(new Date());
    	edocMark.setEdocId(edocId);
    	edocMark.setDocMark(docMark);
    	edocMark.setCreateUserId(user.getId());
    	edocMark.setStatus(Constants.EDOC_MARK_USED);
    	edocMark.setDocMarkNo(0);
    	edocMark.setCategoryId(0L);    	
    	edocMark.setDomainId(user.getLoginAccount());
    	edocMark.setMarkNum(markNum);
    	this.save(edocMark);
    }
    
    /**
     * 方法描述：拟文时创建文号,选断号的情况下
     * todo:文号如果已经被使用了呢？？？？？？？？add by handy,2007-10-16
     */    
//    public void createMarkByChooseNo(Long edocMarkId, Long edocId,int markNum) {
//   	检查公文年度编号变更
//    	EdocHelper.checkDocmarkByYear();
//		EdocMark edocMark = edocMarkDAO.get(edocMarkId);
//		edocMark.setEdocId(edocId);
//		edocMark.setStatus(Constants.EDOC_MARK_USED);
//		edocMark.setMarkNum(markNum);
//		this.save(edocMark);    	
//    } 
    /**
     * 断号也直接插入条记录 add at310sp2
     */
    public void createMarkByChooseNo(Long edocMarkId, Long edocId,int markNum) {
    	//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
    	User user = CurrentUser.get();
		EdocMark mark = edocMarkDAO.get(edocMarkId);
		EdocMark edocMark=new EdocMark();
		edocMark.setIdIfNew();
		edocMark.setEdocMarkDefinition(mark.getEdocMarkDefinition());
		edocMark.setCreateTime(new Date());
		edocMark.setEdocId(edocId);
		edocMark.setDocMark(mark.getDocMark());
		edocMark.setCreateUserId(user.getId());
		edocMark.setStatus(Constants.EDOC_MARK_USED);
		edocMark.setDocMarkNo(mark.getDocMarkNo());
		edocMark.setCategoryId(mark.getCategoryId());
		edocMark.setDomainId(user.getLoginAccount());
		edocMark.setMarkNum(markNum);
		this.save(edocMark);  
    } 
    
    /**
     * 判断文号是否被占用
     * @param edocId     公文id
     * @param edocMark   文号
     * @return   true 被占用 false 未占用
     */
    public boolean isUsed(Long edocId){
//    	检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
    	return edocMarkDAO.isUsed(edocId);
    }
    
    public boolean isUsed(String markStr,String edocId)
    {
    	//检查公文年度编号变更
    	EdocHelper.checkDocmarkByYear();
    	return edocMarkDAO.isUsed(markStr,edocId);    	
    }
    
    /**
     * 按年度把公文文号归为最小值
     */
    
    public void turnoverCurrentNoAnnual(){
    	
    	User user = CurrentUser.get();
    	edocMarkCategoryManager.turnoverCurrentNoAnnual();
    //	List<EdocMarkCategory> list = edocMarkCategoryManager.findByTypeAndDomainId(Constants.EDOC_MARK_CATEGORY_BIGSTREAM,user.getLoginAccount());
//    	List<EdocMarkCategory> list = edocMarkCategoryManager.findAll();
//    	for(EdocMarkCategory category:list){
//    		if(category.getYearEnabled()){
//	    		category.setCurrentNo(category.getMinNo());
//	    		edocMarkCategoryManager.updateCategory(category);
//    		}
//    	}
    }
    
    public List<EdocMarkNoModel> getDiscontinuousMarkNos(Long edocMarkDefinitionId){
    	List<EdocMarkNoModel> results = new ArrayList<EdocMarkNoModel>();
    	//EdocMarkDefinition edocMarkDefinition = edocMarkDefinitionManager.getMarkDefinition(edocMarkDefinitionId);    	
    	//Long categoryId = edocMarkDefinition.getEdocMarkCategory().getId();
    	List<EdocMark> edocMarks = edocMarkDAO.findEdocMarkByMarkDefId4Discontin(edocMarkDefinitionId);
    	for (EdocMark edocMark:edocMarks) {
    		EdocMarkNoModel model = new EdocMarkNoModel();    		
    		model.setEdocMarkId(edocMark.getId());
    		model.setMarkNo(edocMark.getDocMark());
    		results.add(model);
    	}
    	    	
    	return results;
    } 
    
    public List<EdocMark> findByCategoryAndNo(Long categoryId,Integer docMarkNo)
    {
    	return edocMarkDAO.findEdocMarkByCategoryId(categoryId,docMarkNo);
    }
    /**
     * 发起人撤销流程后，已经调用的文号（如果是最大号）可以恢复，下次发文时可继续调用。
     * @param summary 		公文对象
     * @return
     */
   
    public void edocMarkCategoryRollBack(EdocSummary summary){
    	// 发文
        if(summary.getEdocType()==0){
        	// 第一套文号
        	if(summary.getDocMark()!=null&&!"".equals(summary.getDocMark())){
        		EdocMark edocMark=edocMarkDAO.findEdocMarkByEdocSummaryIdAndEdocMark(summary.getId(), summary.getDocMark(), 1);
        		rollBackOperation(summary, edocMark,1);
        	}
        	// 第二套文号
        	if(summary.getIsunit()&&summary.getDocMark2()!=null&&!"".equals(summary.getDocMark2())){
        		EdocMark edocMark=edocMarkDAO.findEdocMarkByEdocSummaryIdAndEdocMark(summary.getId(), summary.getDocMark2(), 2);
        		rollBackOperation(summary, edocMark,2);
        	}
        }
    }
    //文号回滚具体操作
	private void rollBackOperation(EdocSummary summary, EdocMark edocMark,int num) {
		//	当其他公文使用此断号时就查找不到记录
		if(edocMark!=null){
			EdocMarkDefinition edocMarkDefinition=edocMark.getEdocMarkDefinition();
			//	手写的时候为null
			if(edocMarkDefinition!=null){
				EdocMarkCategory edocMarkCategory=edocMarkDefinition.getEdocMarkCategory();
				int curno=edocMarkCategory.getCurrentNo();
				//   确保当前公文的文号是最大文号，docMarkNo保存当前公文文号的编号（no）
				if(curno-1==edocMark.getDocMarkNo()){
					//	判断categoryId在当前断号表的记录是不是全部本公文的。
					//	如下两种情况表示其他的公文已经使用了该文号，此时文号不回滚。
					//	1.其他的公文通过断号的方式已经选择相同Category【是同一Category，并且当前值相等】的其他字号的文号）
					//	2.当前公文A发送的时候，其他公文如B也同时发送，并且选择相同的文号，数据库中会有多条的记录
					
					//判断是否有其他公文使用与此流水相关的文号。true：有其他公文使用此流水，false:无其他公文使用此流水。
					boolean otherUse=edocMarkDAO.judgeOtherEdocUseCategroy(edocMarkCategory.getId(),edocMark.getDocMarkNo(),summary.getId());
					if(!otherUse){
						//设置Category-1
						edocMarkCategory.setCurrentNo(curno-1);
						edocMarkDefinition.setEdocMarkCategory(edocMarkCategory);
						edocMarkDefinitionManager.saveMarkDefinition(edocMarkDefinition);
						
						//删除断号edocMark表中相关记录
						edocMarkDAO.deleteEdocMarkByCategoryIdAndNo(edocMarkCategory.getId(),edocMark.getDocMarkNo());
						
						//设置公文的文号为空
						if(num==1){
							summary.setDocMark(null);
						}else{
							summary.setDocMark2(null);
						}
						
						edocSummaryDao.update(summary);
					}
				}
			}
		}
	}

	public EdocSummaryDao getEdocSummaryDao() {
		return edocSummaryDao;
	}

	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}
}