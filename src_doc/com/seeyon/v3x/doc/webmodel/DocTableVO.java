package com.seeyon.v3x.doc.webmodel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.Constants;

/**
 * 文档列表界面右视图数据
 * 
 */
public class DocTableVO extends DocAclVO implements Comparable<DocTableVO> {
	// 图标id在values中的位置，默认-1(不显示图标)    暂未使用
	private Integer imgLoc = -1;
	// 一列中单元格的集合
	private List<GridVO> grids = new ArrayList<GridVO>();
	// 是否上传文件
	private boolean isUploadFile;	
	// 是否图片
	//是否是图片格式文件，打开时使用
	
	private boolean isImg;
	
	//是否是office格式文档
	
	private boolean isOffice;
	// 对应的v3xfile
	private V3XFile file;
	
	
	
	// 是否文件映射, 打开时候使用
	private boolean isLink;
	// 是否文档夹映射，打开时使用
	private boolean isFolderLink;
	//鼠标在文档列表标题区域时，是否显示操作图标标志
	private boolean settable;
	
//	// 是否协同, 打开时候使用
//	private boolean isCol;
//	// 协同主题
//	private String colSubject;	
	
	// 统一使用 isPig, appEnumKey 标记组合来完成系统类型的打开
	
	// yyyy-mm-dd
	private String createDate;
	
	// 2007.06.15 归档调查的打开需要surveyTypeId
	private long surveyTypeId;
	
	// 是否归档
	private boolean isPig;
	// 归档类型的key
	private int appEnumKey;
	
//	private DocAclVO docAclVO;
	// 内容类型
	private long frType;
	


	public int getAppEnumKey() {
		return appEnumKey;
	}

	public void setAppEnumKey(int appEnumKey) {
		this.appEnumKey = appEnumKey;
	}

	public boolean getIsPig() {
		return isPig;
	}

	public void setIsPig(boolean isPig) {
		this.isPig = isPig;
	}
	
	public boolean getIsOffice(){
		return isOffice;
	}
	
	public void setIsOffice(boolean isOffice){
		this.isOffice = isOffice;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public boolean getIsUploadFile() {
		return isUploadFile;
	}

	public void setIsUploadFile(boolean isUploadFile) {
		this.isUploadFile = isUploadFile;
	}
	
	public boolean getIsImg(){
		return isImg;
	}
	
	
	public void setIsImg(boolean isImg){
		this.isImg = isImg;
	}

    public V3XFile getFile() {
		return file;
	}

	public void setFile(V3XFile file) {
		this.file = file;
	}
	
	public Integer getImgLoc() {
		return imgLoc;
	}

	public void setImgLoc(Integer imgLoc) {
		this.imgLoc = imgLoc;
	}

	public List<GridVO> getGrids() {
		return grids;
	}

	public void setGrids(List<GridVO> grids) {
		this.grids = grids;
	}

	public DocTableVO(DocResource docResource) {
		super(docResource);
		// set the settable value
		settable = true;
		long frType = docResource.getFrType();
		if (frType == Constants.FOLDER_PLAN_DAY || frType == Constants.FOLDER_PLAN_MONTH
				|| frType == Constants.FOLDER_PLAN_WEEK || frType == Constants.FOLDER_PLAN_WORK
				|| frType == Constants.SYSTEM_PLAN) {
			settable = false;
		}
	}

	
	public DocTableVO() {		
	}

	public boolean getIsLink() {
		return isLink;
	}

	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
	}

	public boolean getIsFolderLink() {
		return isFolderLink;
	}

	public void setIsFolderLink(boolean isFolderLink) {
		this.isFolderLink = isFolderLink;
	}

	public long getSurveyTypeId() {
		return surveyTypeId;
	}

	public void setSurveyTypeId(long surveyTypeId) {
		this.surveyTypeId = surveyTypeId;
	}
	
	// added by handy,2007-8-8 11:47
	public boolean getSettable() {
		return settable;
	}
	
	public void setSettable(boolean b) {
		settable = b;
	}

//	public DocAclVO getDocAclVO() {
//		return docAclVO;
//	}
//
//	public void setDocAclVO(DocAclVO docAclVO) {
//		this.docAclVO = docAclVO;
//	}

	public long getFrType() {
		return frType;
	}

	public void setFrType(long frType) {
		this.frType = frType;
	}

	
	/**
	 * 增加排序依据项：按照修改日期降序排列
	 */
	private Timestamp updateTime;
	
	private int compareDate(Timestamp t1, Timestamp t2) {
		if(t1 != null && t2 != null)
			return - t1.compareTo(t2);
		else if(t1 == null)
			return 1;
		else if(t2 == null)
			return -1;
		else
			return 0;
	}
	
	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public int compareTo(DocTableVO o) {
		return this.compareDate(this.getUpdateTime(), o.getUpdateTime());
	}

//	public boolean getIsCol() {
//		return isCol;
//	}
//
//	public void setIsCol(boolean isCol) {
//		this.isCol = isCol;
//	}
//
//	public String getColSubject() {
//		return colSubject;
//	}
//
//	public void setColSubject(String colSubject) {
//		this.colSubject = colSubject;
//	}
	


}
