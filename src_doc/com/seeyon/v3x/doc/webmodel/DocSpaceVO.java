package com.seeyon.v3x.doc.webmodel;

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.doc.domain.DocStorageSpace;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 存储空间vo
 */
public class DocSpaceVO {
	private final String resource = "com.seeyon.v3x.doc.resources.i18n.DocResource";
	
	private DocStorageSpace docStorageSpace;
	// 文档总空间，已经使用空间，使用百分比，空间状态
	private String total;
	private String used;
	private int percent;
	private String docStatus;			//个人文档库空间状态
	
	///////邮件空间/////
	private String mailTotal;			//邮件空间总大小
	private String mailUsed;			//邮件空间使用了的大小
	private String mailStatus;			//邮件空间状态
//	 博客总空间，已经使用空间，使用百分比，空间状态
	private String blogTotal;
	private String blogUsed;
	private String blogStatus;
	// 用户名
	private String userName;
	
	// 2008.03.25
	private String allTotal;   // 总共空间
	private String docFree;    // 文档空闲
	private String mailFree;   // 邮件空闲
	private int mailPercent;   // 邮件比例
	private String blogFree;   // 博客空闲
	private int blogPercent;   // 博客比例
	//
	// 百分比 95.6   没有%
	private String docPercentStr;
	private String mailPercentStr;
	private String blogPercentStr;
	
	//进行页面封装的数据
	private String docdesc;
	private String maildesc;
	private String blogdesc;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public DocSpaceVO(DocStorageSpace docStorageSpace) {
		this.docStorageSpace = docStorageSpace;
		if(docStorageSpace != null){
			total = Strings.formatFileSize(docStorageSpace.getTotalSpaceSize(), true);
			used = Strings.formatFileSize(docStorageSpace.getUsedSpaceSize(), true);
			float it =new Float(docStorageSpace.getTotalSpaceSize());
			float is =new Float(docStorageSpace.getUsedSpaceSize());
			Double docf = new Double(is * 100 / it);
			percent = docf.intValue();
			if(docStorageSpace.getTotalSpaceSize() == 0L)
				docPercentStr = "0";
			else
				docPercentStr = this.getStrPercent(docf, (docStorageSpace.getUsedSpaceSize() * 100) % docStorageSpace.getTotalSpaceSize());
			mailTotal=Strings.formatFileSize(docStorageSpace.getMailSpace() , true);
			mailUsed=Strings.formatFileSize(docStorageSpace.getMailUsedSpace(), true);
			blogTotal = Strings.formatFileSize(docStorageSpace.getBlogSpace() , true);
			blogUsed = Strings.formatFileSize(docStorageSpace.getBlogUsedSpace(), true);
			
			if(docStorageSpace.getBlogSpace() == 0L)
				blogTotal = "0 KB";
			if(docStorageSpace.getBlogUsedSpace() == 0L)
				blogUsed = "0 KB";
			
			docStatus=Constants.getSpaceKey(docStorageSpace.getStatus());			//文档库空间状态
			mailStatus=Constants.getSpaceKey(docStorageSpace.getMailStatus());		//邮件空间状态
			blogStatus=Constants.getSpaceKey(docStorageSpace.getBlogStatus());		//邮件空间状态
			
			// 2008.3.25
			long bspace = docStorageSpace.getBlogStatus() == Constants.SPACE_NOT_ASSIGNED || !Constants.blogEnabled()? 0 : docStorageSpace.getBlogSpace();
			allTotal = Strings.formatFileSize(docStorageSpace.getTotalSpaceSize() + docStorageSpace.getMailSpace() + bspace, true);
			long dfree = docStorageSpace.getTotalSpaceSize() - docStorageSpace.getUsedSpaceSize();
			docFree = Strings.formatFileSize((dfree > 0L ? dfree : 0L), true);
			long mfree = docStorageSpace.getMailSpace() - docStorageSpace.getMailUsedSpace();
			mailFree = Strings.formatFileSize((mfree > 0L ? mfree : 0L), true);
			float mt =new Float(docStorageSpace.getMailSpace());
			float ms =new Float(docStorageSpace.getMailUsedSpace());
			Double mailf = new Double(ms * 100 / mt);
			mailPercent = mailf.intValue();
			if(docStorageSpace.getMailSpace() == 0L)
				mailPercentStr = "0";
			else
				mailPercentStr = this.getStrPercent(mailf, (docStorageSpace.getMailUsedSpace() * 100) % docStorageSpace.getMailSpace());
			long bfree = docStorageSpace.getBlogSpace() - docStorageSpace.getBlogUsedSpace();
			blogFree = Strings.formatFileSize((bfree > 0L ? bfree : 0L), true);
			float bt =new Float(docStorageSpace.getBlogSpace());
			float bs =new Float(docStorageSpace.getBlogUsedSpace());
			Double blogf = new Double(bs * 100 / bt);
			blogPercent = blogf.intValue();
			if(docStorageSpace.getBlogSpace() == 0L)
				blogPercentStr = "0";
			else
				blogPercentStr = this.getStrPercent(blogf, (docStorageSpace.getBlogUsedSpace() * 100) % docStorageSpace.getBlogSpace());
			// end
		}else{
			total = "0 KB";
			used = "0 KB";
			percent = 0;
		}
		
	}

	public DocSpaceVO(DocStorageSpace docStorageSpace,HttpServletRequest request) {
		Locale locale = LocaleContext.getLocale(request);
		this.docStorageSpace = docStorageSpace;
		if(docStorageSpace != null){
			mailTotal=Strings.formatFileSize(docStorageSpace.getMailSpace() , true);
			mailUsed=Strings.formatFileSize(docStorageSpace.getMailUsedSpace(), true);
			blogTotal = Strings.formatFileSize(docStorageSpace.getBlogSpace() , true);
			blogUsed = Strings.formatFileSize(docStorageSpace.getBlogUsedSpace(), true);
			total = Strings.formatFileSize(docStorageSpace.getTotalSpaceSize(), true);
			used = Strings.formatFileSize(docStorageSpace.getUsedSpaceSize(), true);   			
			docdesc = ResourceBundleUtil.getString(resource, locale, "doc.doc.desc",used,total);
			if(Constants.SPACE_NOT_ASSIGNED == docStorageSpace.getBlogStatus()) {
				blogdesc = ResourceBundleUtil.getString(resource, locale, "doc.blog.desc1",blogTotal);
			}else {
				blogdesc = ResourceBundleUtil.getString(resource, locale, "doc.blog.desc",blogUsed,blogTotal);
			}			
			maildesc = ResourceBundleUtil.getString(resource, locale, "doc.blog.desc",mailUsed,mailTotal);			
		}else{
			total = "0 KB";
			used = "0 KB";
			percent = 0;
			docdesc = ResourceBundleUtil.getString(resource, locale, "doc.doc.desc",used,total);
			blogdesc = ResourceBundleUtil.getString(resource, locale, "doc.blog.desc",used,total);
			maildesc = ResourceBundleUtil.getString(resource, locale, "doc.mail.desc",used,total);
		}
		
	}	
	
	// 如果有小数，显示两位小数，没有小数，显示整数
	private String getStrPercent(Double fpercent, long mod){
		if(mod == 0L)
			return fpercent.intValue() + "";
		
		BigDecimal bd = new BigDecimal(fpercent);
		bd = bd.setScale(2, BigDecimal.ROUND_CEILING);
		String str = bd.doubleValue() + "";
		
//		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
//		String str = df.format(fpercent);
		
		return str;
	}
	
	public DocStorageSpace getDocStorageSpace() {
		return docStorageSpace;
	}
	public void setDocStorageSpace(DocStorageSpace docStorageSpace) {
		this.docStorageSpace = docStorageSpace;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}

	public String getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}

	public String getMailTotal() {
		return mailTotal;
	}

	public void setMailTotal(String mailTotal) {
		this.mailTotal = mailTotal;
	}

	public String getMailUsed() {
		return mailUsed;
	}

	public void setMailUsed(String mailUsed) {
		this.mailUsed = mailUsed;
	}
	
	public String getBlogTotal() {
		return blogTotal;
	}
	
	public void setBlogTotal(String blogTotal) {
		this.blogTotal = blogTotal;
	}
	
	public String getBlogUsed() {
		return blogUsed;
	}
	
	public void setBlogUsed(String blogUsed) {
		this.blogUsed = blogUsed;
	}
	
	public String getBlogStatus() {
		return blogStatus;
	}
	
	public void setBlogStatus(String blogStatus) {
		this.blogStatus = blogStatus;
	}

	public String getAllTotal() {
		return allTotal;
	}

	public void setAllTotal(String allTotal) {
		this.allTotal = allTotal;
	}

	public String getBlogFree() {
		return blogFree;
	}

	public void setBlogFree(String blogFree) {
		this.blogFree = blogFree;
	}

	public int getBlogPercent() {
		return blogPercent;
	}

	public void setBlogPercent(int blogPercent) {
		this.blogPercent = blogPercent;
	}

	public String getDocFree() {
		return docFree;
	}

	public void setDocFree(String docFree) {
		this.docFree = docFree;
	}

	public String getMailFree() {
		return mailFree;
	}

	public void setMailFree(String mailFree) {
		this.mailFree = mailFree;
	}

	public int getMailPercent() {
		return mailPercent;
	}

	public void setMailPercent(int mailPercent) {
		this.mailPercent = mailPercent;
	}

	public String getBlogPercentStr() {
		return blogPercentStr;
	}

	public void setBlogPercentStr(String blogPercentStr) {
		this.blogPercentStr = blogPercentStr;
	}

	public String getDocPercentStr() {
		return docPercentStr;
	}

	public void setDocPercentStr(String docPercentStr) {
		this.docPercentStr = docPercentStr;
	}

	public String getMailPercentStr() {
		return mailPercentStr;
	}

	public void setMailPercentStr(String mailPercentStr) {
		this.mailPercentStr = mailPercentStr;
	}

	public String getBlogdesc() {
		return blogdesc;
	}

	public void setBlogdesc(String blogdesc) {
		this.blogdesc = blogdesc;
	}

	public String getDocdesc() {
		return docdesc;
	}

	public void setDocdesc(String docdesc) {
		this.docdesc = docdesc;
	}

	public String getMaildesc() {
		return maildesc;
	}

	public void setMaildesc(String maildesc) {
		this.maildesc = maildesc;
	}
}
