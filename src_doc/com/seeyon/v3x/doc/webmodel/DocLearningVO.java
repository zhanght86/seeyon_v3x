package com.seeyon.v3x.doc.webmodel;

import java.sql.Timestamp;

import com.seeyon.v3x.doc.domain.DocLearning;

/**
 * 学习文档vo
 */
public class DocLearningVO {
	private DocLearning docLearning;
	// 文档名称
	private String docName;
	// 推荐人
	private String recommender;
	// 推荐时间
	private Timestamp recommendTime;
	// 图标名称
	private String icon;
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public DocLearningVO(DocLearning docLearning){
		this.docLearning = docLearning;
		
		this.docName = docLearning.getDocResource().getFrName();
		this.recommendTime = docLearning.getCreateTime();
	}
	
	public DocLearning getDocLearning() {
		return docLearning;
	}
	public void setDocLearning(DocLearning docLearning) {
		this.docLearning = docLearning;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getRecommender() {
		return recommender;
	}
	public void setRecommender(String recommender) {
		this.recommender = recommender;
	}
	public Timestamp getRecommendTime() {
		return recommendTime;
	}
	public void setRecommendTime(Timestamp recommendTime) {
		this.recommendTime = recommendTime;
	}
}
