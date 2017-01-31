package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.blog.manager.BlogArticleManager;
import com.seeyon.v3x.blog.webmodel.ArticleModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;

/**
 * 我的博客栏目
 */
public class BlogSection extends BaseSection {
	
	private static final Log log = LogFactory.getLog(BlogSection.class);

	private BlogArticleManager blogArticleManager;

	private SystemConfig systemConfig;

	public void setBlogArticleManager(BlogArticleManager blogArticleManager) {
		this.blogArticleManager = blogArticleManager;
	}
	
	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "blogSection";
	}

	@Override
	public String getBaseName() {
		return "blogSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return "blogSection";
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
		User user = CurrentUser.get();
		Long memberId = user.getId();
		int count = 8;
		List<ArticleModel> allArticleModelList = null;
		try {
			allArticleModelList = blogArticleManager.getBlogArticleByCount(memberId, count);
		} catch (Exception e) {
			log.error("", e);
		}

		int i = 0;
		if (allArticleModelList != null) {
			for (ArticleModel model : allArticleModelList) {
				byte attach = model.getAttachmentFlag();
				boolean flag = false;
				if (attach == 1)
					flag = true;

				MultiRowVariableColumnTemplete.Row row = t.addRow();
				// 第一列：标题
				MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
				subjectCell.setCellContent(model.getSubject());
				subjectCell.setHasAttachments(flag);
				subjectCell.setCellWidth(70);
				subjectCell.setLinkURL("/blog.do?method=showPostPro&articleId=" + model.getId() + "&from=section" + "&familyId=" + model.getFamilyId());
				// 第二列：日期
				MultiRowVariableColumnTemplete.Cell dateCell = row.addCell();
				dateCell.setCellWidth(30);
				dateCell.setCellContent(SectionUtils.toDatetime(model.getIssueTime(), 4));
				i++;
			}
		}
		
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/blog.do?method=blogHome&status=0");
		return t;

	}

	public boolean isAllowUsed() {
		String f = this.systemConfig.get(IConfigPublicKey.BLOG_ENABLE);
		return f != null && "enable".equals(f);
	}

}