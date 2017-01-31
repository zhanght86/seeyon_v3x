package com.seeyon.v3x.indexresume.manager;

import com.seeyon.v3x.indexresume.domain.IndexResumeInfo;

/**
 * @author zhangyong
 * @since V3.20 全文检索恢复索引时间设置，调度任务相关
 */
public interface IndexResumeTaskManager {
	/**
	 * 初始化环境
	 * 这里会读取配置,并启动定时器
	 *
	 */
 void init();
 void registryTask(IndexResumeInfo info);
 void saveConfig(IndexResumeInfo resumeInfo);
 IndexResumeInfo getResumeInfo() throws Exception;
 void taskEndWork(String createDate,int appType);
}
