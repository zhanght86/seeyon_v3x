/**
 * $Id: AddressBookManagerImpl.java,v 1.27 2011/02/24 05:57:58 renhy Exp $
 `* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.manager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.addressbook.dao.AddressBookMemberDao;
import com.seeyon.v3x.addressbook.dao.AddressBookTeamDao;
import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.addressbook.domain.AddressBookTeam;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.controller.EdocFormController;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * <p/> Title: 通讯录<外部接口实现>
 * </p>
 * <p/> Description: 通讯录<外部接口实现>
 * </p>
 * <p/> Date: 2007-5-25
 * </p>
 * 
 * @author paul(qdlake@gmail.com)
 */
public class AddressBookManagerImpl implements AddressBookManager {

	private static final Log log = LogFactory
			.getLog(AddressBookManagerImpl.class);

	private AddressBookMemberDao addressBookMemberDao;

	private AddressBookTeamDao addressBookTeamDao;

	private FileManager fileManager;

	public AddressBookMemberDao getAddressBookMemberDao() {
		return addressBookMemberDao;
	}

	public void setAddressBookMemberDao(
			AddressBookMemberDao addressBookMemberDao) {
		this.addressBookMemberDao = addressBookMemberDao;
	}

	public AddressBookTeamDao getAddressBookTeamDao() {
		return addressBookTeamDao;
	}

	public void setAddressBookTeamDao(AddressBookTeamDao addressBookTeamDao) {
		this.addressBookTeamDao = addressBookTeamDao;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see com.seeyon.v3x.addressbook.manager.AddressBookManager#addMember(com.seeyon.v3x.addressbook.domain.AddressBookMember)
	 */
	public void addMember(AddressBookMember member) {
		member.setIdIfNew();
		addressBookMemberDao.save(member);
	}

	public void updateMember(AddressBookMember member) {
		addressBookMemberDao.update(member);
	}

	public AddressBookMember getMember(Long memberId) {
		return addressBookMemberDao.get(memberId);
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see com.seeyon.v3x.addressbook.manager.AddressBookManager#getMembersByCreatorId(java.lang.Long)
	 */
	public List<AddressBookMember> getMembersByCreatorId(Long creatorId) {
		return addressBookMemberDao.findMembersByCreatorId(creatorId);
	}

	public List<AddressBookMember> getMembersByTeamId(Long teamId) {
		// AddressBookTeam category = addressBookTeamDao.get(teamId);
		// Hibernate.initialize(category.getMembers());
		// List<AddressBookMember> members = new ArrayList<AddressBookMember>(
		// category.getMembers());
		List<AddressBookMember> members = this.addressBookMemberDao
				.findMembersByTeamId(teamId);
		return members;
	}

	public void removeCategoryMembersByIds(Long creatorId, List<Long> memberIds) {
		List<AddressBookTeam> categries = addressBookTeamDao
				.findTeamsByCreatorId(creatorId);
		/*
		 * if (null != categries) { for(AddressBookTeam category : categries) {
		 * Hibernate.initialize(category.getMembers()); if (null != memberIds) {
		 * for (Long memberId : memberIds) { AddressBookMember member =
		 * addressBookMemberDao.get(memberId);
		 * category.getMembers().remove(member); } }
		 * addressBookTeamDao.save(category); } }
		 */
	}

	public void removeMembersByIds(Long creatorId, List<Long> memberIds) {
		addressBookMemberDao.deleteMembersByIds(memberIds);
	}

	public List<AddressBookTeam> getTeamsByCreatorId(Long creatorId) {
		return addressBookTeamDao.findTeamsByCreatorId(creatorId);
	}

	public void addTeam(AddressBookTeam team) {
		team.setIdIfNew();
		addressBookTeamDao.save(team);
	}

	public AddressBookTeam getTeam(Long teamId) {
		return addressBookTeamDao.get(teamId);
	}

	public void updateTeam(AddressBookTeam team) {
		addressBookTeamDao.update(team);
	}

	public void removeTeamById(Long teamId) {
		AddressBookTeam category = addressBookTeamDao.get(teamId);
		List<AddressBookMember> members = this.getMembersByTeamId(teamId);
		List<Long> memberIds = new ArrayList<Long>();
		if (null != members && !members.isEmpty()) {
			for (AddressBookMember member : members)
				memberIds.add(member.getId());
			addressBookMemberDao.deleteMembersByIds(memberIds);
		}
		addressBookTeamDao.deleteObject(category);
	}

	public List getOrgMemByName(String name) {
		return this.addressBookMemberDao.findOrgMembersByName(name);
	}

	public List getMemberByName(String name) {
		return this.addressBookMemberDao.findMemberByName(name);
	}
	
	public List getMemberByTel(String tel) {
		return this.addressBookMemberDao.findMemberByTel(tel);
	}

	public List getOrgMemberByLevelName(String levelName) {
		return this.addressBookMemberDao.findOrgMemberByLevelName(levelName);
	}

	public List getMemberByLevelName(String levelName) {
		return this.addressBookMemberDao.findMemberByLevelName(levelName);
	}

	public boolean isExist(int type, String name, Long createId, Long accountId, String memberId) {
		if(type == TYPE_EMAIL){
			return addressBookMemberDao.hasSameMail(name, memberId);
		}else if(type == TYPE_CATEGORY){
			return addressBookTeamDao.hasSameCategory(name, createId);
		}else if(type == TYPE_OWNTEAM){
			return addressBookTeamDao.hasSameOwnTeam(name, createId, accountId);
		}else if(type == TYPE_DISCUSS){
			return addressBookTeamDao.hasSameDiscussTeam(name, createId, accountId);
		}
		return true;
	}

	public String doImport(File file, String categoryId, String memberId)
			throws Exception {

		if (null!= file && !Strings.isBlank(categoryId)) {
			//File file  = new File(FilenameUtils.separatorsToSystem(fileURL));
			//FileReader reader = new FileReader(file);
			//BufferedReader br = new BufferedReader(reader);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file))); 
			String result = "";
			String s1 = null;
			while ((s1 = br.readLine()) != null) {
				result += s1 + "\r\n";
			}
			br.close();
			//reader.close();

			AddressBookMember ABmember = null;

			if (!Strings.isBlank(memberId) && !("null").equals(memberId)){
				ABmember = this.getMember(Long.valueOf(memberId));
			} else if (ABmember == null) {
				ABmember = new AddressBookMember();
				ABmember.setCategory(Long.valueOf(categoryId));
			}

			/*
			 * String[] labels = {"FN", "ORG", "TITLE", "TEL;WORK;VOICE",
			 * "TEL;WORK;VOICE", "TEL;HOME;VOICE", "TEL;CAR;VOICE", "TEL;VOICE",
			 * "TEL;PAGER;VOICE", "WORK;FAX", "TEL;HOME;FAX", "TEL;FAX",
			 * "TEL;HOME",
			 * "TEL;ISDN","TEL;PREF","X-MS-TEL;VOICE;ASSISTANT","X-MS-TEL;VOICE;COMPANY","X-MS-TEL;VOICE;CALLBACK","X-MS-TEL;VOICE;RADIO","X-MS-TEL;TTYTDD","ADR;WORK;PREF"};
			 * String label = ""; for(int i=0; i<labels.length; i++){
			 * if(result.contains(labels[i]) && i<labels.length){ result =
			 * result.substring(result.indexOf(labels[i], 1), result.length());
			 * label = result.substring(result.indexOf(":", 1),
			 * result.indexOf(labels[i+1],1)); } }
			 */

			try {
				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(in));
				// Document document = new DocumentImpl();
				// BufferedWriter writer = null;

				Pattern p = Pattern
						.compile("BEGIN:VCARD(\\r\\n)([\\s\\S\\r\\n\\.]*?)END:VCARD");// 分组，
				Matcher m = p.matcher(result.toString());
				while (m.find()) {
					String str = m.group(0);
					// 姓名
					String name = "";
					Pattern pn = Pattern
							.compile("FN;([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mn = pn.matcher(m.group(0));
					while (mn.find()) {

						if (mn.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							name = mn.group(1).substring(
									mn.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							name = name.substring(name.indexOf(":") + 1);
							if (name.indexOf(";") > -1) {
								name = name.substring(0, name.indexOf(";"));

							}

						} else {
							Pattern pnn = Pattern
									.compile("CHARSET=([A-Za-z0-9-]*?):");
							Matcher mnn = pnn.matcher(mn.group(1));
							while (mnn.find()) {
								name = mn.group(1).substring(
										mn.group(1).indexOf(mnn.group(0))
												+ mnn.group(0).length());
							}
						}

					}

					Pattern pn2 = Pattern
							.compile("FN:([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mn2 = pn2.matcher(m.group(0));
					while (mn2.find()) {

						if (mn2.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							name = mn2.group(1).substring(
									mn2.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							name = name.substring(name.indexOf(":") + 1);
							if (name.indexOf(";") > -1) {
								name = name.substring(0, name.indexOf(";"));

							}

						} else {
							name = mn2.group(1);
						}

					}

					ABmember.setName(name);

					String org = "";
					String companyName = "";
					String depName = "";
					Pattern pno = Pattern
							.compile("ORG;([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mno = pno.matcher(m.group(0));
					while (mno.find()) {

						if (mno.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							org = mno.group(1).substring(
									mno.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							org = org.substring(org.indexOf(":") + 1);
							if (org.indexOf(";") > -1) {
								companyName = org
										.substring(0, org.indexOf(";"));
								depName = org.substring(org.indexOf(";") + 1,
										org.length());
							} else {
								companyName = org;
							}

						} else {
							Pattern pnn = Pattern
									.compile("CHARSET=([A-Za-z0-9-]*?):");
							Matcher mnn = pnn.matcher(mno.group(1));
							while (mnn.find()) {
								org = mno.group(1).substring(
										mno.group(1).indexOf(mnn.group(0))
												+ mnn.group(0).length());
								if (org.indexOf(";") > -1) {
									companyName = org.substring(0, org
											.indexOf(";"));
									depName = org.substring(
											org.indexOf(";") + 1, org.length());
								} else {
									companyName = org;
								}
							}
						}

					}

					Pattern pno2 = Pattern
							.compile("ORG:([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mno2 = pno2.matcher(m.group(0));
					while (mno2.find()) {

						if (mno2.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							org = mno2.group(1).substring(
									mno2.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							org = org.substring(org.indexOf(":") + 1);
							if (org.indexOf(";") > -1) {
								companyName = org
										.substring(0, org.indexOf(";"));
								depName = org.substring(org.indexOf(";") + 1,
										org.length());
							} else {
								companyName = org;
							}

						} else {
							org = mno2.group(1);
							if (org.indexOf(";") > -1) {
								companyName = org
										.substring(0, org.indexOf(";"));
								depName = org.substring(org.indexOf(";") + 1,
										org.length());
							} else {
								companyName = org;
							}
						}
					}

					ABmember.setCompanyDept(depName);
					ABmember.setCompanyName(companyName);

					String title = "";
					Pattern pnt = Pattern
							.compile("TITLE;([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mnt = pnt.matcher(m.group(0));
					while (mnt.find()) {

						if (mnt.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							title = mnt.group(1).substring(
									mnt.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							title = title.substring(title.indexOf(":") + 1);
							if (title.indexOf(";") > -1) {
								title = title.substring(0, title.indexOf(";"));
								ABmember.setCompanyPost(title);
							} else {
								ABmember.setCompanyPost(title);
							}

						} else {
							Pattern pnn = Pattern
									.compile("CHARSET=([A-Za-z0-9-]*?):");
							Matcher mnn = pnn.matcher(mnt.group(1));
							while (mnn.find()) {
								title = mnt.group(1).substring(
										mnt.group(1).indexOf(mnn.group(0))
												+ mnn.group(0).length());

							}
						}

					}

					Pattern pnt2 = Pattern
							.compile("TITLE:([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mnt2 = pnt2.matcher(m.group(0));
					while (mnt2.find()) {

						if (mnt2.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							title = mnt2.group(1).substring(
									mnt2.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							title = title.substring(title.indexOf(":") + 1);
							if (title.indexOf(";") > -1) {
								title = title.substring(0, title.indexOf(";"));
							}

						} else {
							title = mnt2.group(1);
						}

					}

					ABmember.setCompanyPost(title);

					String fax = "";
					Pattern p1 = Pattern.compile("TEL;WORK;FAX:\\d*");// 分组，
					Matcher m1 = p1.matcher(str);
					while (m1.find()) {
						fax = m1.group(0).substring(
								m1.group(0).indexOf("TEL;WORK;FAX:")
										+ "TEL;WORK;FAX:".length());
					}
					ABmember.setFax(fax);

					String work = "";
					Pattern p2 = Pattern.compile("TEL;WORK:\\d*");// 分组，
					Matcher m2 = p2.matcher(str);
					while (m2.find()) {
						work = m2.group(0).substring(
								m2.group(0).indexOf("TEL;WORK:")
										+ "TEL;WORK:".length());
					}
					ABmember.setCompanyPhone(work);

					String home = "";
					Pattern p3 = Pattern.compile("TEL;HOME:\\d*");// 分组，
					Matcher m3 = p3.matcher(str);
					while (m3.find()) {
						home = m3.group(0).substring(
								m3.group(0).indexOf("TEL;HOME:")
										+ "TEL;HOME:".length());
					}
					ABmember.setFamilyPhone(home);

					String mobile = "";
					Pattern mp3 = Pattern.compile("TEL;CELL;VOICE:\\d*");// 分组，
					Matcher mb3 = mp3.matcher(str);
					while (mb3.find()) {
						mobile = mb3.group(0).substring(
								mb3.group(0).indexOf("TEL;CELL;VOICE:")
										+ "TEL;CELL;VOICE:".length());
					}
					ABmember.setMobilePhone(mobile);

					String email = "";
					Pattern p4 = Pattern
							.compile("\\w+(\\.\\w+)*@\\w+(\\.\\w+)+");// 分组，
					Matcher m4 = p4.matcher(str);
					while (m4.find()) {
						email = m4.group(0);
					}
					ABmember.setEmail(email);

					String companyPhone = "";
					Pattern mpc3 = Pattern
							.compile("X-MS-TEL;VOICE;COMPANY:\\d*");// 分组，
					Matcher mbb3 = mpc3.matcher(str);
					while (mbb3.find()) {
						companyPhone = mbb3.group(0).substring(
								mbb3.group(0)
										.indexOf("X-MS-TEL;VOICE;COMPANY:")
										+ "X-MS-TEL;VOICE;COMPANY:".length());
					}
					ABmember.setCompanyPhone(companyPhone);

					String address = "";
					Pattern pna = Pattern
							.compile("ADR;HOME;([\\s\\S\\r\\n\\.]*?)([\\r\\n])");// 分组，
					Matcher mna = pna.matcher(m.group(0));
					while (mna.find()) {

						if (mna.group(1).indexOf("ENCODING=QUOTED-PRINTABLE") > -1) {
							address = mn.group(1).substring(
									mn.group(1).indexOf(
											"ENCODING=QUOTED-PRINTABLE:")
											+ "ENCODING=QUOTED-PRINTABLE:"
													.length());
							address = address
									.substring(address.indexOf(":") + 1);
							if (address.indexOf(";") > -1) {
								address = address.substring(0, address
										.indexOf(";"));
								ABmember.setName(address);
							} else {
								ABmember.setName(address);
							}

						} else {
							Pattern pnna = Pattern
									.compile("CHARSET=([A-Za-z0-9-]*?):");
							Matcher mnn = pnna.matcher(mna.group(1));
							while (mnn.find()) {
								address = mna.group(1).substring(
										mna.group(1).indexOf(mnn.group(0))
												+ mnn.group(0).length());
								String country = address
										.substring(
												address.lastIndexOf(";") + 1,
												address.length());
								address = address.substring(0, address
										.lastIndexOf(";"));
								String postcode = address
										.substring(
												address.lastIndexOf(";") + 1,
												address.length());
								ABmember.setAddress(country);
								ABmember.setPostcode(postcode);
							}
						}
					}

				}

			} catch (Exception e) {
				log.error("导入vcard文件异常 : " + e);
				return "";
			}
			User user = CurrentUser.get();
			ABmember.setCreatorId(user.getId());
			ABmember.setCreatorName(user.getName());
			ABmember.setCreatedTime(new Date());
			ABmember.setMemo("");
			ABmember.setMsn("");
			ABmember.setQq("");
			ABmember.setWebsite("");
			if(!this.isExistSameUserName(ABmember,user.getId())){
				this.addMember(ABmember);
			}else{
				return "ExistSameName";
			}
			

			/*
			 * AddressBookMember ABmember = new AddressBookMember();
			 * ABmember.setIdIfNew(); ABmember.setAddress(s1);
			 * ABmember.setBlog(s1); ABmember.setCategory(s1);
			 * ABmember.setCompanyDept(s1); ABmember.setCompanyDept(s1);
			 * ABmember.setCompanyLevel(s1); ABmember.setCompanyName(s1);
			 * ABmember.setCompanyPhone(s1); ABmember.setCompanyPost(s1);
			 * ABmember.setCreatedTime(s1); ABmember.setCreatorId(s1);
			 * ABmember.setCreatorName(s1); ABmember.setEmail(s1);
			 * ABmember.setFamilyPhone(s1); ABmember.setFax(s1);
			 * ABmember.setMemo(s1); ABmember.setMobilePhone(s1);
			 * ABmember.setModifiedTime(modifiedTime); ABmember.setMsn(s1);
			 * ABmember.setName(s1); ABmember.setPostcode(s1);
			 * ABmember.setQq(""); ABmember.setWebsite(s1);
			 */
		}
		return "OK";
	}

	private Map<String,List<String[]>> readCSVBySheets(File file, int... sheetpages)
			throws Exception {
		if (!file.exists()) {
			log.warn("导入的CSV文件不存在!");
			throw new FileNotFoundException("文件不存在");
		}
		if (sheetpages.length < 1) {
			log.warn("导入的CSV文件没有工作表!");
			throw new FileNotFoundException("CSV文件没有工作表");
		}
		BufferedReader in = new BufferedReader(new FileReader(file.getPath()));
		String s = null;
		StringBuilder sb = new StringBuilder();
		while((s=in.readLine())!=null)
			sb.append(s+"\n");
		in.close();
		
		String csvContent = sb.toString();
		List<String> titleIndex = new ArrayList<String>();
		List<String[]> titleList = new ArrayList<String[]>();
		List<String[]> dataList = new ArrayList<String[]>();
		
		if(csvContent!=null){
			String[] ss = csvContent.split("\n");
			if(ss!=null){
				String title = ss[0];
				String[] titleArray = title!=null?title.split(","):null;
				List<String> list = new ArrayList<String>();
				for(String string : titleArray){
					list.add(string.replace("\"", ""));
				}
				String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
				//员工通讯录
				String titleContent = ResourceBundleUtil.getString(resource,"export.csv.title",1);
				//String titleContent = "名,姓,中文称谓,单位,部门,职务,住宅地址 街道,住宅地址 邮政编码,单位主要电话,移动电话,电子邮件地址,电子邮件类型,电子邮件显示名称,网页";
				String[] titleContentArray = titleContent.split(",");
				for(String mm : titleContentArray){
					if(Strings.isNotBlank(mm)&&list.contains(mm)){
						int i = list.indexOf(mm);
						titleIndex.add(String.valueOf(i));
					}
				}
				String[] array = {};
				titleList.add(titleIndex.toArray(array));
				for(int i=1;i<ss.length;i++){
					String[] record = ss[i].split(","); 
					dataList.add(record);
				}
			}
		}

		Map<String,List<String[]>> map = new HashMap<String,List<String[]>>();
		
		map.put("title",titleList);
		map.put("data", dataList);
		return map;
	}

	public String doCsvImport(File file, String categoryId,
			String memberId) throws Exception {
		
		if (null!= file && !Strings.isBlank(categoryId)) {
			User user = CurrentUser.get();
			//File file = new File(fileURL);
			if (null != file) {
				Map<String,List<String[]>> csvMap = this.readCSVBySheets(file, 0);
				List<String[]> titleIndex = csvMap.get("title");
				List<String[]> dataList = csvMap.get("data");
				boolean update = false;
				for(int i = 0; i< dataList.size(); i++){
					String[] s = dataList.get(i);
					String[] index = titleIndex!=null?titleIndex.get(0):null;
					AddressBookMember member = null;
					if(Strings.isNotBlank(memberId)){
						member = this.getMember(Long.valueOf(memberId));
						if (member.getName().equals((Integer.parseInt(index[1]) < s.length ? s[Integer.parseInt(index[1])].replace("\"", "") : "") + s[Integer.parseInt(index[0])].replace("\"", ""))) {
							update = true;
						} else {
							member = new AddressBookMember();
							member.setIdIfNew();
							member.setCreatorId(user.getId());
							member.setCreatorName(user.getName());
							member.setCreatedTime(new Date());
							update = false;
						}
					}else{
						member = new AddressBookMember();
						member.setIdIfNew();
						member.setCreatorId(user.getId());
						member.setCreatorName(user.getName());
						member.setCreatedTime(new Date());
						update = false;
					}
					member.setCategory(Long.valueOf(categoryId));
					member.setModifiedTime(new Date());
					if(index!=null){
						member.setName((Integer.parseInt(index[1]) < s.length ? s[Integer.parseInt(index[1])].replace("\"", "") : "") + s[Integer.parseInt(index[0])].replace("\"", ""));
						member.setCompanyName(Integer.parseInt(index[3]) < s.length ? s[Integer.parseInt(index[3])].replace("\"", "") : "");
						member.setCompanyDept(Integer.parseInt(index[4]) < s.length ? s[Integer.parseInt(index[4])].replace("\"", "") : "");
						int indexNum = 5;
						if (index.length < 14) {
							indexNum = 4;
						}
						member.setCompanyLevel(Integer.parseInt(index[indexNum]) < s.length ? s[Integer.parseInt(index[indexNum])].replace("\"", "") : "");
						member.setEmail(Integer.parseInt(index[indexNum+5]) < s.length ? s[Integer.parseInt(index[indexNum+5])].replace("\"", "") : "");
						member.setWebsite(Integer.parseInt(index[indexNum+8]) < s.length ? s[Integer.parseInt(index[indexNum+8])].replace("\"", "") : "");
						member.setCompanyPhone(Integer.parseInt(index[indexNum+3]) < s.length ? s[Integer.parseInt(index[indexNum+3])].replace("\"", "") : "");
						member.setMobilePhone(Integer.parseInt(index[indexNum+4]) < s.length ? s[Integer.parseInt(index[indexNum+4])].replace("\"", "") : "");
						member.setAddress(Integer.parseInt(index[indexNum+1]) < s.length ? s[Integer.parseInt(index[indexNum+1])].replace("\"", "") : "");
						member.setPostcode(Integer.parseInt(index[indexNum+2]) < s.length ? s[Integer.parseInt(index[indexNum+2])].replace("\"", "") : "");
						Date operatingTime = new Date();
						member.setCreatedTime(Datetimes.addSecond(operatingTime, i));
						member.setModifiedTime(Datetimes.addSecond(operatingTime, i));
					}
					if(update){
						this.updateMember(member);
					}else{
	                    //在同一个组中姓名相同的不能添加
	                    if(!isExistSameUserName(member,user.getId()))
	                    {
	                        this.addMember(member);
	                    }
					}
				}
			}
			return "OK";
		}else{
			return "Fail";
		}
	}

    public boolean isExistSameUserName(AddressBookMember member,Long createrId)
    {
        List list=addressBookMemberDao.findMemberByNameAndTeam(member.getName(),member.getCategory(),createrId);
        if(list!=null&&!list.isEmpty())
        {
         return true;   
        }
        return false;
    }
}