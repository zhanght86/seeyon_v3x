package com.seeyon.v3x.timecard.manager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.seeyon.v3x.timecard.dao.TimecardCollectDao;
import com.seeyon.v3x.timecard.dao.TimecardIntercalateDao;
import com.seeyon.v3x.timecard.dao.TimecardRecordDao;
import com.seeyon.v3x.timecard.domain.TimecardIntercalate;
import com.seeyon.v3x.timecard.domain.TimecardRecord;
import com.seeyon.v3x.util.Datetimes;

public class TimecardManager {

	private TimecardRecordDao timecardRecorddao;
    private TimecardIntercalateDao timecardIntercalatedao;
    private TimecardCollectDao timecardCollectdao;

    public TimecardRecordDao getTimecardRecorddao() {
		return timecardRecorddao;
	}
    public void setTimecardRecorddao(TimecardRecordDao timecardRecorddao) {
		this.timecardRecorddao = timecardRecorddao;
	}
    public TimecardCollectDao getTimecardCollectdao() {
		return timecardCollectdao;
	}
    public void setTimecardCollectdao(TimecardCollectDao timecardCollectdao) {
		this.timecardCollectdao = timecardCollectdao;
	}
    public TimecardIntercalateDao getTimecardIntercalatedao() {
		return timecardIntercalatedao;
	}
	public void setTimecardIntercalatedao(
			TimecardIntercalateDao timecardIntercalatedao) {
		this.timecardIntercalatedao = timecardIntercalatedao;
	}
	public TimecardIntercalate getIntercalate(Long memberId) throws Exception {
		return timecardIntercalatedao.getTimecardIntercalateByMemberID(memberId);
	}
    public List getTimecardRecordByMonth(String currentMonth, Long memberId) throws Exception {
        return timecardRecorddao.getTimecardRecordByMonth(currentMonth, memberId);
	}
    public TimecardRecord getTimecardRecordByDate(String currentDate, Long memberId){
    	return timecardRecorddao.getTimecardRecordByDate(currentDate, memberId);
    }
    public Boolean save(Long memberId, int workType)throws Exception {
    	Boolean bl = false;
        TimecardRecord timecardRecord = new TimecardRecord();
		timecardRecord.setMemberId(memberId);
        String today = systemDate();
        TimecardRecord today_Record = getTimecardRecordByDate(today, memberId);
        
        Calendar now = Calendar.getInstance();
        long currentTime = now.getTimeInMillis();
        now.setTimeInMillis(currentTime);
        String currentDate = Datetimes.format(now.getTime(), "HH:mm:ss");
        //int hours = now.get(Calendar.HOUR_OF_DAY);
        //java.util.Date dateTime1 = sdf.parse(currentDate);
        //java.sql.Date dateTime2 = new Date(dateTime1.getTime());
        //java.sql.Timestamp dateTime3 = new Timestamp(dateTime2.getTime());
        if(today_Record != null){
        	if(workType == 0){
        		if(today_Record.getOndutyTime() != null){
        			bl = true;
        		}else{
        			today_Record.setOndutyTime(currentDate);
        		}
        	}else{
        		if(today_Record.getOffdutyTime() != null){
        			bl = true;
        		}else{
        			today_Record.setOffdutyTime(currentDate);
        		}
        	}
        	/*if(workType == 0){
	        	today_Record.setOndutyTime(currentDate);
	        }
	        else if(workType == 1){
	            today_Record.setOffdutyTime(currentDate);
	        }
        	timecardRecorddao.update(today_Record);*/
        }
	    else{
	    	today_Record = new TimecardRecord();
        	today_Record.setMemberId(memberId);
        	today_Record.setIdIfNew();
        	today_Record.setWorkDate(today);
	        if(workType == 0){
	            today_Record.setOndutyTime(currentDate);
	        }
	        else if(workType == 1){
	            today_Record.setOffdutyTime(currentDate);
	        }
	        timecardRecorddao.save(today_Record); 
	    }
        return bl;
    }
    public String systemDate(){
        Calendar now = Calendar.getInstance();
        long currentTime = now.getTimeInMillis();
        now.setTimeInMillis(currentTime);
        String currentDate = Datetimes.formatDate(now.getTime());
        return currentDate;
    }
    public String systemMonth(){
    	Calendar now = Calendar.getInstance();
        long currentTime = now.getTimeInMillis();
        now.setTimeInMillis(currentTime);
        String currentDate = Datetimes.format(now.getTime(), "yyyy-MM");
        return currentDate;
    }
    public Long currentSystemTime(){
    	Calendar now = Calendar.getInstance();
        long currentTime = now.getTimeInMillis();
    	return currentTime;
    }
    public String currentTime(){
    	Calendar now = Calendar.getInstance();
        long currentTime = now.getTimeInMillis();
        now.setTimeInMillis(currentTime);
        String currentSystemTime = Datetimes.formatDatetime(now.getTime());
    	return currentSystemTime;
    }
    public Calendar getCalendar_Instance(){
        Calendar  c_Instance =  Calendar.getInstance();
        return c_Instance;
    }
   /* public String currentMonthHavingDate(){
    	Calendar c = new GregorianCalendar();
        int maxDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    	return date;
    }*/
    public TimecardRecord[] initTimecardRecord(){
        Calendar c = new GregorianCalendar();
        int maxDate = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        TimecardRecord[] timecardList = new TimecardRecord[maxDate];
        Calendar c_Instance = getCalendar_Instance();
        for(int i=0; i<timecardList.length; i++){
            timecardList[i] = new TimecardRecord();
            c_Instance.set(Calendar.DATE, i+1);
            String c_date = Datetimes.formatDate(c_Instance.getTime());
            timecardList[i].setWorkDate(c_date);
        }
        return timecardList;
    }
}
