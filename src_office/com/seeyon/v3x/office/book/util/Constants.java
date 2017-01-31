package com.seeyon.v3x.office.book.util;

public class Constants {
	/**
	 * 图书资料使用状态
	 */
	public static final int Book_Status_Allow = 0;// 可借阅（默认）

	public static final int Book_Status_NotAllow = 1;// 不可借阅

	/**
	 * 删除标记
	 */
	public static final int Del_Flag_Normal = 0;// 正常（默认）

	public static final int Del_Flag_Delete = 1;// 删除
	
    public static final int Del_TYPE_APPLY=2; //申请者删除标记
    
	public static final String Total_Count_Field = "myTotalCount";
	
	/**
	 * 分类
	 */
	
	public static final int Field_Book = 1;//图书
	public static final int Field_Information = 2;//资料
	
	
	/**
	 * 查询条件
	 */
	public static final int Search_Condition_BookName = 1;		//按图书名称查询
	public static final int Search_Condition_BookType = 2;		//按图书类型查询
	public static final int Search_Condition_BookAuthor =3;		//按图书作者查询
	public static final int Search_Condition_BookStat = 4;		//按图书状态查询
	public static final int Search_Condition_Department = 5;	//按部门查询
	public static final int Search_Condition_Member = 6;		//按人员查询
	public static final int Search_Condition_ApplyStat = 7;		//按审批状态查询
	public static final int Search_Condition_SorageStat = 8;	//按出库入库状态查询
	public static final int Search_Condition_BookPub = 9;		//按出版单位查询
	public static final int Search_Condition_BookField = 10;	//按分类查询
}
