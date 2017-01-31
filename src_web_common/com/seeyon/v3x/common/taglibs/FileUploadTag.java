/**
 * 
 */
package com.seeyon.v3x.common.taglibs;

import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 附件上传
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-5-8
 */
public class FileUploadTag extends BodyTagSupport {

	private static final long serialVersionUID = 4529074480392383585L;

	public static final String TAG_NAME = "fileUpload";

	protected static final boolean DEFAULT_canDeleteOriginalAtts = true;
	
	private static int DEFAULT_quantity = -1;

	/**
	 * 文件类型{@link com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE_FILE}
	 */
	private int type;

	/**
	 * 所属应用分类 {@link com.seeyon.v3x.common.Constants.ApplicationCategoryEnum}
	 */
	private Integer applicationCategory;

	/**
	 * 允许上传的文件的扩展名，多个用,分开，可以为null，表示所有都允许，如jpeg,gif,png
	 */
	private String extensions;

	/**
	 * 上传到指定的文件夹，文件名采用系统统一的UUID；不设置该属性，上传到系统默认的分区中<br>
	 * 如：c:\ext\
	 */
	private String destDirectory;
	
	/**
	 * 上传后存为指定的文件名：使用全名，如：c:\ext\log.txt<br>
	 * 注意：只能上传一个文件
	 */
	private String destFilename;

	/**
	 * 原有的附件，应用场景：编辑主题、模板调用
	 */
	private List<Attachment> attachments;

	/**
	 * 是否允许删除原有的附件，常用在模板调用
	 */
	private boolean canDeleteOriginalAtts;

	/**
	 * 是否允许删除原有的附件，应用场景：模板调用
	 */
	private boolean originalAttsNeedClone = false;
	
	/**
	 * 最大上传的大小
	 */
	private Long maxSize;
	
	/**
	 * 是否加密
	 */
	private boolean isEncrypt;
	
	/**
	 * 允许上传的数量
	 */
	private int quantity = -1;

	/**
	 * 上传文件窗口的title，国际化key，默认是fileupload.page.title（上传本地文件），资源文件是MainResources
	 */
	private String popupTitleKey;
	/**
	 * 是否可以在线查看（Office转换）
	 */
	private boolean onlineView = false;

	public FileUploadTag() {
		init();
	}

	public void init() {
		type = com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal(); // 默认为附件模式
		extensions = "";
		canDeleteOriginalAtts = DEFAULT_canDeleteOriginalAtts;
		originalAttsNeedClone = false;
		attachments = null;
		destDirectory = null;
		applicationCategory = null;
		destFilename = null;
		maxSize = null;
		isEncrypt = true;
		popupTitleKey = null;
		quantity = DEFAULT_quantity;
	}

	@Override
	public int doStartTag() throws JspException {
		if(DEFAULT_quantity == -1){
			Integer max = SystemProperties.getInstance().getIntegerProperty("fileUpload.max.quantity");
			if(max != null){
				quantity = DEFAULT_quantity = max;
			}
			else{
				DEFAULT_quantity = 5;
			}
		}
		
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		String url = com.seeyon.v3x.portlets.bridge.spring.taglibs.LinkTag
				.calculateURL("RENDER", "/fileUpload.do?type=" + type
						+ "&applicationCategory=" + Strings.escapeNULL(applicationCategory, "")
						+ "&extensions=" + Strings.escapeNULL(extensions, "")
//						+ "&destDirectory=" + Strings.escapeNULL(destDirectory, "")
//						+ "&destFilename=" + Strings.escapeNULL(destFilename, "")
						+ "&maxSize=" + Strings.escapeNULL(maxSize, "")
						+ "&isEncrypt=" + isEncrypt
//						+ "&quantity=" + quantity
						+ "&popupTitleKey=" + (popupTitleKey == null ? "" : popupTitleKey)
						, pageContext);

		JspWriter out = pageContext.getOut();
		try {
			out.println("<div id=\"attachmentInputs\" style=\"display:none;\"></div>");
			out.println("<div id=\"attachmentArea\" style=\"overflow: auto;\"></div>");
			out.println("<div style=\"display:none;\">");

			out.println("<iframe name=\"downloadFileFrame\" id=\"downloadFileFrame\" frameborder=\"0\" width=\"0\" height=\"0\"></iframe>");
			out.println("</div>");

			out.println("<script>");
			out.println("<!--");
			//这里加入是否启用精灵的批量上传参数
			out.println("var isA8geniusAdded;");
			out.println("try{");
			out.println("  var ufa = new ActiveXObject('UFIDA_IE_Addin.Assistance');");
			out.println("  isA8geniusAdded = true;");
			out.println("}catch(e){");
			out.println("  isA8geniusAdded = false;");
			out.println("}");
			out.println("var downloadURL = \"" + url + "&isA8geniusAdded=\"+isA8geniusAdded;");

			if (attachments != null && !attachments.isEmpty()) {

				for (Attachment att : attachments) {

					// addAttachment(type, filename, mimeType, createDate, size, fileUrl, canDelete, needClone, description)
					String fileUrl = "";
					String genesisIdStr = att.getGenesisId() != null? String.valueOf(att.getGenesisId()) : "";
					if(att.getFileUrl() == null){
						fileUrl = genesisIdStr;
					}
					else{
						fileUrl = String.valueOf(att.getFileUrl());
					}
					
					out.print("addAttachment(");
					out.print("'" + att.getType() + "',");
					out.print("'" + escapeJavaScript(att.getFilename()) + "',");
					out.print("'" + att.getMimeType() + "',");
					out.print("'" + Datetimes.formatDatetime(att.getCreatedate()) + "',");
					out.print("'" + att.getSize() + "',");
					out.print("'" + fileUrl + "',");
					out.print(canDeleteOriginalAtts + ",");
					out.print(originalAttsNeedClone + ",");
					out.print("'" + genesisIdStr + "',");
					out.print("'" + att.getExtension() + "',");
					out.print("'" + att.getIcon() + "',");
					out.print("'" + att.getReference() + "',");
					out.print("'" + att.getCategory() + "',");
					out.print((onlineView?"true":"false")+ ","); //附件编辑，默认不能在线查看
					out.print("0,");
					out.print("'" + OfficeTransHelper.isOfficeTran() + "',");
					out.print("'" + att.getSubReference() + "'");
					out.println(");");
					
					if(att.getType() == Constants.ATTACHMENT_TYPE.FormFILE.ordinal() || att.getType() == Constants.ATTACHMENT_TYPE.IMAGE.ordinal() ){
						out.println("fileUploadAttachments.get('"+att.getFileUrl()+"').extSubReference = \"" + att.getSubReference() +"\";");
					}
					    
				}
			}
			out.println("//-->");
			out.println("</script>");
		}
		catch (IOException e) {
		}

		init();
		return super.doEndTag();
	}

	@Override
	public void release() {
		init();
		super.release();
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public void setType(int type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setCanDeleteOriginalAtts(Boolean canDeleteOriginalAtts) {
		this.canDeleteOriginalAtts = canDeleteOriginalAtts;
	}

	public void setOriginalAttsNeedClone(boolean needClone) {
		this.originalAttsNeedClone = needClone;
	}

	public void setApplicationCategory(Integer applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	public void setDestDirectory(String destDirectory) {
		this.destDirectory = destDirectory;
	}

	public void setDestFilename(String destFilename) {
		this.destFilename = destFilename;
	}

	public void setMaxSize(long maxSize){
		this.maxSize = maxSize;
	}
	
	public void setEncrypt(boolean isEncrypt) {
		this.isEncrypt = isEncrypt;
	}

	public void setPopupTitleKey(String popupTitleKey) {
		this.popupTitleKey = popupTitleKey;
	}

	public void setQuantity(int quantity) {
		if(quantity > 0){
			this.quantity = quantity;
		}
	}
	public void setOnlineView(boolean onlineView) {
		this.onlineView = onlineView;
	}	
}

