package com.seeyon.v3x.portal.util;

import java.util.Comparator;

import com.seeyon.v3x.space.domain.Fragment;

public class CompareSortFragment implements Comparator<Fragment>{

	@Override
	public int compare(Fragment o1, Fragment o2) {
		int flag = o1.getLayoutColumn().compareTo(o2.getLayoutColumn());
		if(flag==0){
			return o1.getLayoutRow().compareTo(o2.getLayoutRow());
		}else{
			return flag;
		}
	}

}
