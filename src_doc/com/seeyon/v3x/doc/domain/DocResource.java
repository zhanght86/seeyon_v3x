package com.seeyon.v3x.doc.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.doc.util.Constants;
import com.thoughtworks.xstream.XStream;

/**
 * 文档主表，记录文档、文档夹
 * @see DocCommonInfo
 */
public class DocResource extends BaseModel implements Comparable<DocResource> {
	public static final String PROP_ID = "id";
	public static final String PROP_MIMETYPEID = "mimeTypeId";
	public static final String PROP_FRNAME = "frName";
	public static final String PROP_SIZE = "frSize";
	public static final String PROP_LAST_USER = "lastUserId";
	public static final String PROP_LAST_UPDATE = "lastUpdate";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3128658640817910854L;
	
	/** 是否允许创建子文档夹  */
	private boolean subfolderEnabled;
	/** 是否文档夹人 */
	private boolean isFolder;
	/** 文档是否被锁定人 */
	private boolean isCheckOut;
	/** 锁定文档的人人 */
	private Long checkOutUserId;
	/** 锁定文档的时间人 */
	private java.sql.Timestamp checkOutTime;
	/** 文档创建人 */
	private Long createUserId;
	/** 文档创建日期 */
	private Timestamp createTime;
	/** 所属文档库ID */
	private long docLibId;
	/** 文档的直接父文档夹ID */
	private long parentFrId;
	/** 文档逻辑路径：从文档库的根Id起，到自身的所有节点ID，以"."串接起来的值 */
	private String logicalPath;
	/** 是否允许评论  */
	private boolean commentEnabled;
	/** 评论次数 */
	private int commentCount;
	/** 文档状态 */
	private byte status;
	/** 修改文档状态的时间 */
	private Timestamp statusDate;
	/** 访问次数 */
	private int accessCount;
	/** 是否为学习文档 */
	private boolean isLearningDoc;
	/** 文档排序号 */
	private int frOrder;
	/** 
	 * 文档源ID，分为如下三种情况：
	 * 1.如该文档是上传文档，对应v3x_file主键ID；
	 * 2.如该文档是归档，对应归档应用实体ID；
	 * 3.如为链接，对应源文档（夹）ID。
	 * 如果为文档历史版本信息，只可能为第一种情况
	  */
	private Long sourceId;
	/** 文档内容类型 */
	private long frType;
	/** 文档描述 */
	private String frDesc;
	/** 文档关键词 */
	private String keyWords;
	/** 是否启用版本管理和版本注释 */
	private boolean versionEnabled;
	/**　文档格式排序号 */
	private int mimeOrder;
	private boolean third_hasPingHole;

	/** 文档类型 (0:公文归档的公文，1：部门归档的公文)*/
	private Integer pigeonholeType; 

	/** 文档名称 */
	private String frName;
	/** 文档大小，单位为字节  */
	private long frSize;
	/** 文档最近修改人 */
	private Long lastUserId;
	/** 文档最近修改日期 */
	private Timestamp lastUpdate;
	/** 文档类型ID */
	private Long mimeTypeId;
	/** 文档是否有对应附件 */
	private boolean hasAttachments;
	/** 版本注释 */
	private String versionComment;
	/** 如果是协同，是否有表单授权 */
	private boolean isRelationAuthority = false;
	
	private AffairManager affairManager;
	
	private Integer secretLevel;
	
	public Integer getSecretLevel() {
		// 2017-4-26 诚佰公司 添加保存时默认为内部
		if (secretLevel == null) {
			secretLevel = 1;
		}
		return secretLevel;
	}

	public void setSecretLevel(Integer secretLevel) {
		this.secretLevel = secretLevel;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public boolean getIsRelationAuthority() {
		return isRelationAuthority;
	}
	public void setIsRelationAuthority(boolean isRelationAuthority) {
		this.isRelationAuthority = isRelationAuthority;
	}
	public String getFrName() {
		return frName;
	}
	public void setFrName(String frName) {
		this.frName = frName;
	}
	public long getFrSize() {
		return frSize;
	}
	public void setFrSize(long frSize) {
		this.frSize = frSize;
	}
	public Long getLastUserId() {
		return lastUserId;
	}
	public void setLastUserId(Long lastUserId) {
		this.lastUserId = lastUserId;
	}
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public Long getMimeTypeId() {
		return mimeTypeId;
	}
	public void setMimeTypeId(Long mimeTypeId) {
		this.mimeTypeId = mimeTypeId;
	}
	public boolean getHasAttachments() {
		return hasAttachments;
	}
	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}
	public String getVersionComment() {
		return versionComment;
	}
	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	
	public Integer getPigeonholeType() {
		return pigeonholeType;
	}
	public void setPigeonholeType(Integer pigeonholeType) {
		this.pigeonholeType = pigeonholeType;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public long getFrType() {
		return frType;
	}
	public void setFrType(long frType) {
		this.frType = frType;
	}
	public String getFrDesc() {
		return frDesc;
	}
	public void setFrDesc(String frDesc) {
		this.frDesc = frDesc;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public int getAccessCount() {
		return accessCount;
	}
	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}
	public long getDocLibId() {
		return docLibId;
	}
	public void setDocLibId(long docLibId) {
		this.docLibId = docLibId;
	}
	public long getParentFrId() {
		return parentFrId;
	}
	public void setParentFrId(long parentFrId) {
		this.parentFrId = parentFrId;
	}
	public String getLogicalPath() {
		return logicalPath;
	}
	public void setLogicalPath(String logicalPath) {
		this.logicalPath = logicalPath;
	}
	public boolean getCommentEnabled() {
		return commentEnabled;
	}
	public void setCommentEnabled(boolean commentEnabled) {
		this.commentEnabled = commentEnabled;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public Timestamp getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Timestamp statusDate) {
		this.statusDate = statusDate;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public boolean getIsLearningDoc() {
		return isLearningDoc;
	}
	public void setIsLearningDoc(boolean isLearningDoc) {
		this.isLearningDoc = isLearningDoc;
	}
	public int getFrOrder() {
		return frOrder;
	}
	public void setFrOrder(int frOrder) {
		this.frOrder = frOrder;
	}
	public boolean getIsFolder() {
		return this.isFolder;
	}
	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public boolean getSubfolderEnabled() {
		return this.subfolderEnabled;
	}
	public void setSubfolderEnabled(boolean subfolderEnabled) {
		this.subfolderEnabled = subfolderEnabled;
	}
	public java.sql.Timestamp getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(java.sql.Timestamp checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	public Long getCheckOutUserId() {
		return checkOutUserId;
	}
	public void setCheckOutUserId(Long checkOutUserId) {
		this.checkOutUserId = checkOutUserId;
	}
	public boolean getIsCheckOut() {
		return isCheckOut;
	}
	public void setIsCheckOut(boolean isCheckOut) {
		this.isCheckOut = isCheckOut;
	}
	public int getMimeOrder() {
		return mimeOrder;
	}
	public void setMimeOrder(int mimeOrder) {
		this.mimeOrder = mimeOrder;
	}
	public boolean isThird_hasPingHole() {
		return third_hasPingHole;
	}
	public void setThird_hasPingHole(boolean third_hasPingHole) {
		this.third_hasPingHole = third_hasPingHole;
	}
	public boolean isVersionEnabled() {
		return versionEnabled;
	}
	public void setVersionEnabled(boolean versionEnabled) {
		this.versionEnabled = versionEnabled;
	}
	
	/* ----------------------前端使用-----------------------*/
	
	/** 保存当前用户对当前 DocResource 的权限 */
	private Set<Integer> aclSet = new HashSet<Integer>();
	/** 是否已经查询过权限 */
	private boolean hasAcl = false;
	/** 是否个人文档库记录 */
	private boolean isMyOwn;
	
	public Set<Integer> getAclSet() {
		return aclSet;
	}
	public void setAclSet(Set<Integer> aclSet) {
		this.aclSet = aclSet;
		this.hasAcl = true;
	}
	public boolean getHasAcl() {
		return hasAcl;
	}
	public void setHasAcl(boolean hasAcl) {
		this.hasAcl = hasAcl;
	}
	public boolean getIsMyOwn() {
		return isMyOwn;
	}
	public void setIsMyOwn(boolean isMyOwn) {
		this.isMyOwn = isMyOwn;
	}
	
	/** 元数据信息，不持久化  */
	private Map<Long, Object> metadataMap = new HashMap<Long, Object>();
	
	/**
	 * 根据文档元数据定义id，读取文档扩展属性值
	 * @param metadataDefId 文档元数据定义id
	 * @return 文档扩展属性值对象
	 */
	public Object getMetadataByDefId(long defId){
		return this.getMetadataMap().get(defId);
	}
	
	public Map<Long, Object> getMetadataMap() {
		return metadataMap;
	}
	public void setMetadataMap(Map<Long, Object> metadataMap) {
		this.metadataMap = metadataMap;
	}
	
	/** 元数据对象集合信息，不持久化 */
	private List<DocMetadataObject> metadataList = new ArrayList<DocMetadataObject>();
	
	public List<DocMetadataObject> getMetadataList() {
		return metadataList;
	}
	public void setMetadataList(List<DocMetadataObject> metadataList) {
		this.metadataList = metadataList;
	}

	/*--------------------constructor----------------------- */
	
	public DocResource() {
	}
	public DocResource(long id){
		this.id = id;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public int compareTo(DocResource o) {
		// order by a.frType desc, a.lastUpdate desc
		if(this.frType > o.frType)
			return 1;
		else if(this.frType < o.frType)
			return -1;
		else 
			return -(this.lastUpdate.compareTo(o.lastUpdate));
	}
	
	/**
	 * 判断当前文档所在层级是否过深，已超过上限记数
	 */
	public boolean deeperThanLimit(int limit) {
		return this.getLevelDepth() >= limit;
	}
	
	/**
	 * 获取当前文档夹的层级深度，从文档库下面的一级文档夹开始算起
	 */
	public int getLevelDepth() {
		String[] arr = StringUtils.split(this.logicalPath, '.');
		return arr.length - 1;
	}
	
	/**
	 * 获取当前文档夹相对其某一上层文档夹的层级深度<br>
	 * 比如：文档夹A/文档夹B/文档夹C/文档夹D/文档夹E<br>
	 * 则文档夹E相对文档夹A的层级深度为4级，D为3级，C为2级，B为1级<br>
	 * @param parentId	上层(不一定是上一层)文档夹
	 * @return	相对层级深度
	 */
	public int getRelativeLevelDepth(Long parentId) {
		String cursor = parentId + ".";
		String relativePath = this.logicalPath.substring(this.logicalPath.indexOf(cursor) + cursor.length());
		return StringUtils.split(relativePath, '.').length;
	}
	
	/**
	 * 根据MimeTypeId类型判定当前文档是否为图片(JPG/PNG/GIF)
	 */
	public boolean isImage() {
		return this.getMimeTypeId() == Constants.FORMAT_TYPE_ID_UPLOAD_JPG || 
			   this.getMimeTypeId() == Constants.FORMAT_TYPE_ID_UPLOAD_PNG || 
			   this.getMimeTypeId() == Constants.FORMAT_TYPE_ID_UPLOAD_GIF;
	}
	
	/** 根据MimeTypeId类型判定当前文档是否为上传的PDF文件  */
	public boolean isPDF() {
		return this.getMimeTypeId() == Constants.FORMAT_TYPE_ID_UPLOAD_PDF;
	}
	
	private static final XStream XSTREAM = new XStream();
	/** 将POJO转换为XStream所能解析格式的XML信息 */
	public String toXMLInfo() {
		return XSTREAM.toXML(this);
	}
	
	
	public boolean canPrint4Upload() {
		return this.isImage() || this.isPDF() || this.isUploadOfficeOrWps() || this.mimeTypeId == Constants.FORMAT_TYPE_ID_TXT ||
			   this.mimeTypeId == Constants.FORMAT_TYPE_ID_HTML || this.mimeTypeId == Constants.FORMAT_TYPE_ID_HTM;
	}
	
	/**
	 * 通过mimeTypeId判断上传的文件是否为Office或WPS类型
	 */
	public boolean isUploadOfficeOrWps() {
		return this.mimeTypeId == Constants.FORMAT_TYPE_ID_UPLOAD_DOC ||
		   	   this.mimeTypeId == Constants.FORMAT_TYPE_ID_UPLOAD_XLS || 
		   	   this.mimeTypeId == Constants.FORMAT_TYPE_ID_UPLOAD_WPS_DOC || 
		   	   this.mimeTypeId == Constants.FORMAT_TYPE_ID_UPLOAD_WPS_XLS;
	}
}