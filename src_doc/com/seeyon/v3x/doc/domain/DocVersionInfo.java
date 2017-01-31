package com.seeyon.v3x.doc.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.util.Strings;
import com.thoughtworks.xstream.XStream;

/**
 * 文档历史版本信息，包含用于前端展现的主文档基本信息，以及历史主文档及其正文的XML信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-13
 * @see DocCommonInfo
 */
public class DocVersionInfo extends BaseModel {
	public static final String PROP_ID = "id";
	public static final String PROP_MIMETYPEID = "mimeTypeId";
	public static final String PROP_FRNAME = "frName";
	public static final String PROP_SIZE = "frSize";
	public static final String PROP_LAST_USER = "lastUserId";
	public static final String PROP_LAST_UPDATE = "lastUpdate";
	public static final String PROP_VERSION_COMMENT = "versionComment";
	public static final String PROP_HAS_ATTACHMENTS = "hasAttachments";
	public static final String PROP_VERSION = "version";
	public static final String PROP_DOC_RES_ID = "docResourceId";
	public static final String PROP_DOC_BODY_INFO = "docBodyInfo";
	public static final String PROP_DOC_RES_INFO = "docResourceInfo";
	
	/** 文档历史版本信息在前端列表展现时所需抽取的字段数据 */
	public static String[] DISPLAY_FIELDS =  
		{DocVersionInfo.PROP_ID, DocVersionInfo.PROP_DOC_RES_ID, DocVersionInfo.PROP_MIMETYPEID, 
		 DocVersionInfo.PROP_FRNAME, DocVersionInfo.PROP_HAS_ATTACHMENTS, DocVersionInfo.PROP_VERSION, 
		 DocVersionInfo.PROP_SIZE, DocVersionInfo.PROP_LAST_USER, DocVersionInfo.PROP_LAST_UPDATE, 
		 DocVersionInfo.PROP_VERSION_COMMENT };

	/**
	 * 
	 */
	private static final long serialVersionUID = -3376981652976978128L;
	
	/* ---------------------fields--------------------- */
	
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
	/** 历史版本号，比如V1.0、V2.0等  */
	private Integer version;
	/**	所从属的文档ID  */
	private Long docResourceId;
	/**	历史版本文档正文信息  */
	private String docBodyInfo;
	/**	历史版本主文档信息  */
	private String docResourceInfo;
	

	/* ---------------setter/getter--------------------- */
	
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
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Long getDocResourceId() {
		return docResourceId;
	}
	public void setDocResourceId(Long docResourceId) {
		this.docResourceId = docResourceId;
	}
	public String getDocBodyInfo() {
		return docBodyInfo;
	}
	public void setDocBodyInfo(String docBodyInfo) {
		this.docBodyInfo = docBodyInfo;
	}
	public String getDocResourceInfo() {
		return docResourceInfo;
	}
	public void setDocResourceInfo(String docResourceInfo) {
		this.docResourceInfo = docResourceInfo;
	}
	
	/* -----------------constructor--------------------- */
	
	public DocVersionInfo() {}
	
	/**
	 * 将主文档基本信息赋值给历史版本信息
	 * 最后修改时间和最后修改者需单独赋值
	 * @param dr	主文档(均指修改或替换前的文档)
	 */
	public DocVersionInfo(DocResource dr) {
		if(this.isNew())
			this.setId(UUIDLong.absLongUUID());
		
		if(dr != null) {
			this.setDocResourceId(dr.getId());
			this.setFrName(dr.getFrName());
			this.setFrSize(dr.getFrSize());
			this.setVersionComment(dr.getVersionComment());
			this.setHasAttachments(dr.getHasAttachments());
			this.setMimeTypeId(dr.getMimeTypeId());
			this.setDocResourceInfo(dr.toXMLInfo());
		}
	}
	
	/**
	 * 将主文档及其正文转化为历史版本信息
	 * @param dr	主文档(均指修改或替换前的文档)
	 * @param db	主文档正文(均指修改或替换前的文档正文)
	 */
	public DocVersionInfo(DocResource dr, DocBody db) {
		this(dr);
		this.setDocBodyInfo(db);
	}
	
	
	/* -----------------以下属性不持久化-------------------- */
	
	/** 文档类型图标 */
	private String icon;
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	/** 元数据信息 */
	@SuppressWarnings("unchecked")
	private Map metaDataInfo;

	@SuppressWarnings("unchecked")
	public Map getMetaDataInfo() {
		return metaDataInfo;
	}

	@SuppressWarnings("unchecked")
	public void setMetaDataInfo(Map metaDataInfo) {
		this.metaDataInfo = metaDataInfo;
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
	
	/* ---------行为方法，主要是XML信息与POJO的互换------------- */
	
	/** 得到当前版本的展现形式，用于日志记录等场合 */
	public String getNameWithVersion() {
		return this.frName + "( V" + this.version + ".0 )";
	}
	
	/** 用于文档主信息和历史版本信息POJO与XML信息的转换 */
	private static final XStream XSTREAM = new XStream();
	
	/**
	 * 利用XStream将POJO对象转换为xml信息，存储进数据库<br>
	 * 此后需要使用POJO时，可将xml信息返回为POJO对象<br>
	 * 注意，此XML信息格式不同于领域模型基类转换为XML信息的格式(XStream无法解析)<br>
	 * @see com.seeyon.v3x.common.ObjectToXMLBase#toXML()
	 * @return	xml信息
	 */
	public String toXMLInfo() {
		return XSTREAM.toXML(this);
	}
	
	/**
	 * 将储存的历史文档xml信息转换为POJO对象
	 * @return	历史版本对应的主文档信息
	 */
	public DocResource getDocResourceFromXml() {
		DocResource result = null;
		if(Strings.isNotBlank(docResourceInfo)) {
			result = (DocResource)XSTREAM.fromXML(docResourceInfo);
			// 版本注释可能经过了改动，获取历史版本主文档信息时，其版本注释需与历史版本信息中的注释一致
			result.setVersionComment(this.getVersionComment());
		}
		return result;
	}
	
	/**
	 * 将储存的历史文档正文xml信息转换为POJO对象
	 * @return	历史版本对应的主文档正文信息
	 */
	public DocBody getDocBodyFromXml() {
		return (DocBody)XSTREAM.fromXML(this.docBodyInfo);
	}
	
	/**
	 * 使用XStream将文档正文对象转化为xml信息，储存在文档历史版本信息中
	 * @param db
	 */
	public void setDocBodyInfo(DocBody db) {
		this.setDocBodyInfo(XSTREAM.toXML(db));
	}

}
