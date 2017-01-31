package com.seeyon.v3x.hr;

import java.util.*;

public class StaffInfoFlag {

	/**
	 * 婚姻状况
	 */
	public final static String SINGLE = "1";//未婚
	public final static String MARRIED = "2";//已婚
	
	/**
	 * 学历
	 */
	
	public final static int JUNIOR_HIGH_SCHOOL = 1;//初中
	public final static int SENIOR_HIGH_SCHOOL = 2;//高中
	public final static int JUNIOR_COLLEGE = 3;//大专
	public final static int UNDERGRADUATE_COLLEGE = 4;//本科
	public final static int MASTER_DEGREE = 5;//研究生
	public final static int DOCTORAL_DEGREE = 6;//博士
	public final static int OTHER = 7;//其它

	public final static int VOCATIONAL_HIGH_SCHOOL  = 8;//职高
	public final static int TECHNICAL_SECONDARY_SCHOOL = 9;//中专
	public final static int TECHNICAL_SCHOOL = 10;//技校
	
	private static List<Integer> allEducation = null;

	/**
	 * 政治面貌
	 */
	public final static int COMMIE = 1;//党员
	public final static int OTHER_PARTY = 2;//非党员
	
	/**
	 * 奖惩类型
	 */
	public final static int REWARD = 1;//奖
	public final static int PUNISHMENT = 2;//惩
   
	private final static byte[] EduTypeLock = new byte[0];
    public static List<Integer> getAllEducation(){
    	synchronized (EduTypeLock) {
    		if(allEducation == null){
    			allEducation = new ArrayList<Integer>();
    			allEducation.add(JUNIOR_HIGH_SCHOOL);
    			allEducation.add(SENIOR_HIGH_SCHOOL);
    			allEducation.add(VOCATIONAL_HIGH_SCHOOL);
    			allEducation.add(TECHNICAL_SECONDARY_SCHOOL);
    			allEducation.add(TECHNICAL_SCHOOL);
    			allEducation.add(JUNIOR_COLLEGE);
    			allEducation.add(UNDERGRADUATE_COLLEGE);
    			allEducation.add(MASTER_DEGREE);
    			allEducation.add(DOCTORAL_DEGREE);
    			allEducation.add(OTHER);
    		}
		}
        return allEducation;
    }

}
