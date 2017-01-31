/**
 * 
 */
package com.seeyon.v3x.common.fileupload;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.util.Strings;

/**
 * 自己来重构上传方法，主要是为了捕获异常
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-3-19
 */
public class FileuploadManagerImpl extends CommonsMultipartResolver {

	@SuppressWarnings("unchecked")
	@Override
	public MultipartHttpServletRequest resolveMultipart(
			HttpServletRequest request) throws MultipartException {
		try {
			return super.resolveMultipart(request);
		}
		catch (MaxUploadSizeExceededException e) {
			Map multipartFiles = new HashMap();
			Map multipartParams = new HashMap();
			multipartParams.put("MaxUploadSizeExceeded", new String[] { getMaxSizeStr() });

			return new DefaultMultipartHttpServletRequest(request, multipartFiles, multipartParams);
		}
		catch(Exception e){
			Map multipartFiles = new HashMap();
			Map multipartParams = new HashMap();
			multipartParams.put("unknownException", new String[]{ e.getMessage() });

			return new DefaultMultipartHttpServletRequest(request, multipartFiles, multipartParams);
		}
	}

	/*
	 * 因为此处控制的是request的大小，比文件稍大，目前发现会大1K左右（Html Form小于1K，A8精灵大于1K），此处多3K
	 * 
	 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver#
	 * setMaxUploadSize(long)
	 */
	@Override
	public void setMaxUploadSize(long maxUploadSize) {
		long m = maxUploadSize + 1024 * 3;
		super.setMaxUploadSize(m);
	}

	public static String getMaxSizeStr() {
		String fileUpload_maxSize = SystemProperties.getInstance()
				.getProperty("fileUpload.maxSize");

		if (fileUpload_maxSize != null && !"".equals(fileUpload_maxSize)) {
			long maxSize = Long.parseLong(fileUpload_maxSize);

			return Strings.formatFileSize(maxSize, false);
		}

		return "";
	}

}
