package com.seeyon.v3x.plugin.dee.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseManageController;

@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator })
public class DEEDeployDRPController extends BaseManageController {
	private static final String baseName = "com.seeyon.v3x.plugin.dee.resources.i18n.DeeResources";
	/**
	 * 日志
	 */
	private static final Log log = LogFactory
			.getLog(DEEDeployDRPController.class);

	public ModelAndView deployDRP(HttpServletRequest request,
			HttpServletResponse response) {
		String retMsg;
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multipartRequest.getFile("drpFile");
			if (StringUtils.isNotEmpty(multipartFile.getOriginalFilename())&&multipartFile.getOriginalFilename().toLowerCase().endsWith(".drp")) {
				String fileName = multipartFile.getOriginalFilename();
				if (!fileName.toLowerCase().endsWith(".drp")) {
					retMsg = ResourceBundleUtil.getString(this.baseName,
							"dee.deploy.errfile.label");
				} else {
					String fileRealPath = SystemEnvironment.getA8BaseFolder()
							+ File.separator + "dee" + File.separator
							+ "hotdeploy" + File.separator + fileName;
					File file = new File(fileRealPath);
					multipartFile.transferTo(file);
					retMsg = ResourceBundleUtil.getString(this.baseName,
							"dee.deploy.success.label");
				}
			}else{
				retMsg = ResourceBundleUtil.getString(this.baseName,
						"dee.deploy.errfile.label");
			}
		} catch (IOException e) {
			retMsg = ResourceBundleUtil.getString(this.baseName,
					"dee.deploy.failed.label");
			e.printStackTrace();
			log.error(e);
		}
		ModelAndView view = new ModelAndView("plugin/dee/uploadDRP/uploadDRP");
		view.addObject("retMsg", retMsg);
		return view;
	}

	public ModelAndView show(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView view = new ModelAndView("plugin/dee/uploadDRP/uploadDRP");
		return view;

	}
}
