package com.seeyon.v3x.hr.webmodel;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.utils.DateUtil;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;

public class WebStaffInfo extends WebV3xOrgMember implements Serializable {

	private static final long serialVersionUID = 1578579978101264139L;

	private Long id;
	private int nameList_number;// 序号
	private String code;// 人员编号
	private String sex;// 性别
	private String name;// 姓名
	private String usedname;// 曾用名
	private String nation;// 民族
	private String birthplace;// 籍贯
	private Date birthday;// 出生日期
	private int age;// 年龄
	private String ID_card;// 身份证号
	private int edu_level;// 最高学历
	private int marriage;// 婚姻状况
	private int political_position;// 政治面貌
	private boolean people_type;// 人员性质
	private String org_name;// 所在组织机构
	private Long org_id;
	private Byte type;// 人员类别
	private Byte state;// 人员状态
	private String department_name;// 所在部门
	private String level_name;// 职务层次
	private Long orgLevelId;
	private String post_name;// 主岗名称
	private Long orgPostId;
	private String second_posts;// 副岗名称
	private String second_posts_ids;// 副岗id
	private Date work_starting_date;// 入职时间
	private int working_time;// 工龄
	private String specialty;// 专业
	private String hobby;// 业余爱好
	private Float record_wage;// 档案工资
	private String remark;// 备注
	private String image;// 照片信息
	// 联系信息
	private String telephone;
	private String email;
	private String blog;
	private String telNumber;
	private String website;
	private String postalcode;
	private String address;
	private String dutyLevelName; //职级名称

	public String getDutyLevelName() {
		return dutyLevelName;
	}

	public void setDutyLevelName(String dutyLevelName) {
		this.dutyLevelName = dutyLevelName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}
	
	public String getBirthdayStr() {
		return DateUtil.format(birthday);
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

	public int getMarriage() {
		return marriage;
	}

	public void setMarriage(int marriage) {
		this.marriage = marriage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
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

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public int getPolitical_position() {
		return political_position;
	}

	public void setPolitical_position(int political_position) {
		this.political_position = political_position;
	}

	public String getPost_name() {
		return post_name;
	}

	public void setPost_name(String post_name) {
		this.post_name = post_name;
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

	public String getSecond_posts() {
		return second_posts;
	}

	public void setSecond_posts(String second_posts) {
		this.second_posts = second_posts;
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

	public int getWorking_time() {
		return working_time;
	}

	public void setWorking_time(int working_time) {
		this.working_time = working_time;
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	public Long getOrgLevelId() {
		return orgLevelId;
	}

	public void setOrgLevelId(Long orgLevelId) {
		this.orgLevelId = orgLevelId;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getOrgPostId() {
		return orgPostId;
	}

	public void setOrgPostId(Long orgPostId) {
		this.orgPostId = orgPostId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getPeople_type() {
		return people_type;
	}

	public void setPeople_type(boolean people_type) {
		this.people_type = people_type;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public int getNameList_number() {
		return nameList_number;
	}

	public void setNameList_number(int nameList_number) {
		this.nameList_number = nameList_number;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getSecond_posts_ids() {
		return second_posts_ids;
	}

	public void setSecond_posts_ids(String second_posts_ids) {
		this.second_posts_ids = second_posts_ids;
	}

}