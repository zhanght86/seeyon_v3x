package com.seeyon.v3x.collaboration.webmodel;

/*
 * 根据时间统计出来的数据
 */
public class StatModel {
	/**
	 * 协同数量统计：按照本日、本周、本月、累积
	 */
	int[][] colStat;
	/**
	 * 关键协同统计，重要程度、流程状态
	 */
	int[][] colStat1;
	
	public int[][] getColStat() {
		return colStat;
	}
	public void setColStat(int[][] colStat) {
		this.colStat = colStat;
	}
	public int[][] getColStat1() {
		return colStat1;
	}
	public void setColStat1(int[][] colStat1) {
		this.colStat1 = colStat1;
	}
	

}
