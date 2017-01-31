package com.seeyon.v3x.common.office.trans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.web.GenericFilterProxy;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * Office转换文件访问过滤。提供文件访问，权限判断，访问记录和自动重新生成。
 */
public class OfficeTransFilterImpl extends GenericFilterProxy {

	private static final long serialVersionUID = -8336186823166931012L;

	private final static Log log = LogFactory.getLog(OfficeTransFilterImpl.class);
	
	private OfficeTransManager officeTransManager;

	private byte[] jsByteArray = new byte[0];

	public OfficeTransFilterImpl() {
		super();
		String jsPath = "removeOfficeReviewStyle.js";
		InputStream jsInputStream = null;
		ByteArrayOutputStream jsByteArrayOut = new ByteArrayOutputStream();
		try {
			jsInputStream = this.getClass().getResourceAsStream(jsPath);
			if (jsInputStream != null) {
				StringBuffer sb = new StringBuffer();
				sb.append("\n<script language=\"JavaScript\">\n");
				int n = 0;
				byte[] content = new byte[4096];
				while ((n = jsInputStream.read(content)) != -1) {
					jsByteArrayOut.write(content, 0, n);
				}
				sb.append(new String(jsByteArrayOut.toByteArray(), "UTF-8"));
				sb.append("\n</script>\n");
				jsByteArray = sb.toString().getBytes("ISO-8859-1"); // 这么做是为了去除js文件注释中的中文字符，以免出现与当前HTML编码不一致的麻烦
			} else {
				log.error("没有找到 " + jsPath);
			}
		} catch (IOException e) {
			log.error("读取 " + jsPath + " 时发生错误：", e);
		} finally {
			IOUtils.closeQuietly(jsInputStream);
			IOUtils.closeQuietly(jsByteArrayOut);
		}
	}

	/**
	 * 从URI中解析出文件的Id。
	 * 
	 * @param uri /seeyon/office/cache/20110323/32452354546324/32452354546324.html
	 * @return 如果存在返回文件Id，否则返回0。
	 */
	private Object[] extractFileId(String uri) {
		int beginIndex = uri.lastIndexOf("/");
		int endIndex = uri.lastIndexOf(".html");

		long fileId = 0l;
		String date = null;
		if (endIndex > 0) {
			String sFileId = uri.substring(beginIndex + 1, endIndex);
			if (!Strings.isEmpty(sFileId)) {
				try {
					fileId = Long.parseLong(sFileId);
				} catch (Exception e) {
					// 忽略异常
				}
			}
			
			int prefix = OfficeTransHelper.OfficeTransPathPrefix.length();
			date = StringUtils.substring(uri, prefix, prefix + 8);
		}
		
		return new Object[]{fileId, date};
	}
	
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String uri = httpRequest.getRequestURI();
		
		//如： -23634523412345123/-23634523412345123.html；-23634523412345123/file001.jpg
		String fileName = StringUtils.substringAfter(uri, OfficeTransHelper.OfficeTransPathPrefix);
		
		if (Strings.isBlank(fileName)) { //不是Office的地址，跳过
			return true;
		}
		
		HttpSession session = httpRequest.getSession();
		User user = (User) session.getAttribute(Constants.SESSION_CURRENT_USER);
		if(user == null){
			//TODO 给出提示
			return false;
		}
		
		//如果fileId不等于0，标示是HTML页面，否则是jpg等资源
		Object[] o = extractFileId(uri);
		long fileId = (Long)o[0];
		String dateStr = (String)o[1];
		
		final String srcFile = this.officeTransManager.getOutputPath() + "/" + fileName;
		
		File file = new File(srcFile);
		boolean fileExist = false;
		
		if(fileId != 0l){ //HTML的文件
			fileExist = this.officeTransManager.isExist(fileId, dateStr);
		}
		else{
			fileExist = file.exists();
		}

		if (fileExist) {
			// 使用文件名Hash和最后更新时间作为Etag
			String etag = String.valueOf(fileName.hashCode()) + "-" + file.lastModified();
			
			if(fileId != 0l){
				this.officeTransManager.visit(fileId);
			}
			
			if(WebUtil.checkEtag(httpRequest, httpResponse, etag)){ //匹配，没有修改，浏览器已经做了缓存
				return false;
			}
			
			WebUtil.writeETag(httpRequest, httpResponse, etag);

			InputStream in = null;
			OutputStream out = httpResponse.getOutputStream();
			try {
				in = new FileInputStream(srcFile);
				int n = 0;
				byte[] content = new byte[4096];
				while ((n = in.read(content)) != -1) {
					out.write(content, 0, n);
				}
				if (fileName != null) {
					String fnl = fileName.toLowerCase();
					if (fnl.endsWith(".html") || fnl.endsWith(".htm")) {
						// 仅对HTML文件追加js
						out.write(jsByteArray);
					}
				}
			}
			catch (Exception e) {
				log.error("没有找到指定的文件:" + srcFile, e);
			}
			finally{
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
		else{
			if(fileId != 0l){// HTML页面不存在，并且当前请求是HTML页面，重新生成
				Date fileCreateDate = Datetimes.parse(dateStr, "yyyyMMdd");
				this.officeTransManager.generate(fileId, fileCreateDate, true);
				
				response.sendRedirect("/seeyon/officeTrans.do?method=wait&fileId=" + fileId + "&fileCreateDate=" + Datetimes.format(fileCreateDate, "yyyyMMdd"));
			}
		}
		
		return false;
	}

	public void setOfficeTransManager(OfficeTransManager officeTransManager) {
		this.officeTransManager = officeTransManager;
	}
	
}
