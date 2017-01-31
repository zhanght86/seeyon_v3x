package com.seeyon.v3x.inquiry.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.inquiry.dao.InquiryBasicDao;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.manager.InquiryManager;

/**
 * 结束调查
 */
public class FinishInquiry implements Job {

	private static final Log logger = LogFactory.getLog(FinishInquiry.class);

	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		Long id = jobDataMap.getLongFromString("basicID");
		try {
			InquiryManager inquiryManager = (InquiryManager) ApplicationContextHolder.getBean("inquiryManager");
			InquirySurveybasic basic = inquiryManager.getBasicByID(id);
			if (basic == null) {
				return;
			}
			
			// 将状态置为结束
			basic.setCensor(InquirySurveybasic.CENSOR_CLOSE);
			InquiryBasicDao inquiryBasicDao = (InquiryBasicDao) ApplicationContextHolder.getBean("inquiryBasicDao");
			inquiryBasicDao.update(basic);

			// 填写调查，删除调查待办
			AffairManager affairManager = (AffairManager) ApplicationContextHolder.getBean("affairManager");
			affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, id);

			logger.debug("调查[\"" + basic.getSurveyName() + "\"]已经由任务调度将其设置为结束状态");
		} catch (Exception e) {
			logger.error("结束调查任务调度出现异常：", e);
		}
	}

}