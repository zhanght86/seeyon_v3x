package com.seeyon.v3x.hr.domain;

import java.util.Calendar;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:员工基本信息
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 8, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class StaffInfo  extends BaseModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7383995091816052579L;
	private Long org_member_id;//org_member_id
	private String sex;//性别
	private String name;//姓名
	private String usedname;//曾用名
	private String age;//年龄
	private Date birthday;//出生日期	
	private String nation;//民族
	private String birthplace;//籍贯
	private String ID_card;//身份证号
	private int edu_level;//最高学历
	private int marriage;//婚姻状况
	private int political_position;//政治面貌
	private Date work_starting_date;//入职时间
	private int working_time;//工龄
	private String specialty;//专业
	private String hobby;//业余爱好
	private Float record_wage;// 档案工资
	private String remark;//备注
	private String email;
	private String tel_number;
	private String qq;
	private String msn;
	private Long image_id;//照片文件的id
	private Date image_datetime;//照片文件的上传时间
	private String image_name;//照片文件的名称
	private String self_image_name;//个性头像文件的名称
	
	private String degreeLevel; //政务版  最高学位

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public int getEdu_level() {
		return edu_level;
	}

	public void setEdu_level(int edu_level) {
		this.edu_level = edu_level;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getID_card() {
		return ID_card;
	}

	public void setID_card(String id_card) {
		ID_card = id_card;
	}

	public int getMarriage() {
		return marriage;
	}

	public void setMarriage(int marriage) {
		this.marriage = marriage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public int getPolitical_position() {
		return political_position;
	}

	public void setPolitical_position(int political_position) {
		this.political_position = political_position;
	}


	public Float getRecord_wage() {
		return record_wage;
	}

	public void setRecord_wage(Float record_wage) {
		this.record_wage = record_wage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getUsedname() {
		return usedname;
	}

	public void setUsedname(String usedname) {
		this.usedname = usedname;
	}

	public Date getWork_starting_date() {
		return work_starting_date;
	}

	public void setWork_starting_date(Date work_starting_date) {
		this.work_starting_date = work_starting_date;
	}

	public String getAge() {
		return age;
	}
	
	public void setAgeByBirthday(Date birthday) {
		int age = 1;
		if (null != birthday) {
			Calendar now = Calendar.getInstance();
			int year1 = now.get(Calendar.YEAR);
			Calendar bd = Calendar.getInstance();
			bd.setTime(birthday);
			int year2 = bd.get(Calendar.YEAR);
			age = year1 - year2;
		}
		if(age==0){
	       age=1;
		}
		this.age= String.valueOf(age);		
	}


//	public void setWorking_timeByWork_starting_date(Date work_starting_date) {
//		Calendar now = Calendar.getInstance();
//		int year1 = now.get(Calendar.YEAR);
//		Calendar wsd = Calendar.getInstance();
//		wsd.setTime(work_starting_date);
//		int year2 = wsd.get(Calendar.YEAR);
//		
//		this.working_time = year1 - year2;
//	}

	public int getWorking_time() {
		return working_time;
	}

	public void setWorking_time(int working_time) {
		this.working_time = working_time;
	}

	public Long getOrg_member_id() {
		return org_member_id;
	}

	public void setOrg_member_id(Long org_member_id) {
		this.org_member_id = org_member_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getTel_number() {
		return tel_number;
	}

	public void setTel_number(String tel_number) {
		this.tel_number = tel_number;
	}

	public Date getImage_datetime() {
		return image_datetime;
	}

	public void setImage_datetime(Date image_datetime) {
		this.image_datetime = image_datetime;
	}

	public Long getImage_id() {
		return image_id;
	}

	public void setImage_id(Long image_id) {
		this.image_id = image_id;
	}

	public String getImage_name() {
		return image_name;
	}

	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}

	public String getSelf_image_name() {
		return self_image_name;
	}

	public void setSelf_image_name(String self_image_name) {
		this.self_image_name = self_image_name;
	}

	public String getDegreeLevel() {
		return degreeLevel;
	}

	public void setDegreeLevel(String degreeLevel) {
		this.degreeLevel = degreeLevel;
	}
	
	
	
}
