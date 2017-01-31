package com.seeyon.v3x.timecard.controller;

import com.seeyon.v3x.timecard.manager.TimecardManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.timecard.domain.TimecardRecord;
import com.seeyon.v3x.timecard.domain.TimecardIntercalate;
import com.seeyon.v3x.timecard.domain.TimecardCollect;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

/**
 * User: jincm
 * Date: 2007-1-29
 * Time: 20:04:32
 */
public class TimecardController extends BaseController {
    private TimecardManager timecardManager;

    public void setTimecardManager(TimecardManager timecardManager) {
        this.timecardManager = timecardManager;
    }
    public ModelAndView list(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        User member = CurrentUser.get();
        Long memberId = member.getId();
        ModelAndView mav = new ModelAndView("timecard/list");
        
        String now = timecardManager.systemMonth();
        List timecardRecordList = timecardManager.getTimecardRecordByMonth(now,memberId);
        TimecardRecord[] timecardList = timecardManager.initTimecardRecord();
        if(timecardRecordList != null && timecardRecordList.size() > 0){
	        for(int i=0; i<timecardRecordList.size(); i++){
	        	TimecardRecord timecard = (TimecardRecord)timecardRecordList.get(i);
	        	for(int m=0; m<timecardList.length; m++){
	        		if(timecardList[m].getWorkDate().equals(timecard.getWorkDate())){
	        			timecardList[m] = timecard;
	        		}
	        	}
	        }
        }
        Calendar currentHours = Calendar.getInstance();
        int hours = currentHours.get(Calendar.HOUR_OF_DAY);
        Boolean am_pm = false;
        if(hours <= 12){
        	am_pm = true;
        }
        mav.addObject("timecardList", timecardList);
        mav.addObject("timecardListLength", timecardList.length);
        mav.addObject("am_pm", am_pm);
        return mav;
    }
    public ModelAndView save(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	int workType = Integer.parseInt(request.getParameter("radiobutton"));
        User member = CurrentUser.get();
        Long memberId = member.getId();
        ModelAndView mav = new ModelAndView("timecard/timecardStatus");
        
        Boolean am_pm = false;
        if(workType == 0){
        	am_pm = true;
        }
        String currentTime = timecardManager.currentTime();
        Boolean bl = this.timecardManager.save(memberId, workType);
        mav.addObject("currentTime", currentTime);
        mav.addObject("am_pm", am_pm);
        mav.addObject("bl", bl);
        return mav;
    }
    public ModelAndView cardPuncherForm(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	Calendar currentHours = Calendar.getInstance();
        int hours = currentHours.get(Calendar.HOUR_OF_DAY);
        Boolean am_pm = false;
        if(hours < 12){
        	am_pm = true;
        }
        Long currentSystemTime = timecardManager.currentSystemTime();
    	ModelAndView mav = new ModelAndView("timecard/timecard");
    	mav.addObject("currentSystemTime", currentSystemTime);
    	mav.addObject("am_pm", am_pm);
    	return mav;
    }
    /*
    public ModelAndView update(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	long affairId = Long.parseLong(request.getParameter("affairId"));
    	boolean currentTrack = Boolean.parseBoolean(request.getParameter("currentTrack"));
    	String from = request.getParameter("from");

    	Map<String, Object> columnValue = new HashMap<String, Object>();
    	columnValue.put("isTrack", currentTrack ? false : true);

    	this.affairManager.update(affairId, columnValue);

    	return super.redirectModelAndView("/collaboration.do?method=list" + from);
    }
    public  ModelAndView  delete(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        User member = CurrentUser.get();
        Long memberId = member.getId();

        ModelAndView mav = new ModelAndView("timecard/intercalate");
        TimecardRecord timecardRecord = timecardManager.getRecord(memberId);
        mav.addObject("timecardRecord", timecardRecord);
        return mav;
    }
    public  ModelAndView  setting(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        User member = CurrentUser.get();
        Long memberId = member.getId();

        ModelAndView mav = new ModelAndView("timecard/intercalate");
        TimecardIntercalate timecardIntercalate = timecardManager.getIntercalate(memberId);
        mav.addObject("timecardIntercalate", timecardIntercalate);
        return mav;
    }*/
    public ModelAndView index(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }
}
