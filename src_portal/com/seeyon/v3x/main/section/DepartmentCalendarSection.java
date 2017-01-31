package com.seeyon.v3x.main.section;

import java.util.Map;

import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MonthCalendarTemplate;

/**
 * 部门日程栏目 已取消
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * @deprecated
 */
public class DepartmentCalendarSection extends BaseSection
{

    @Override
    public String getIcon()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return "departmentCalendarSection";
    }
    
    @Override
	public String getBaseName() {
		return "departmentCalendar";
	}

    @Override
    public String getName(Map<String, String> preference)
    {
        return "departmentCalendar";
    }

    @Override
    public Integer getTotal(Map<String, String> preference)
    {
        return null;
    }

    @Override
    public BaseSectionTemplete projection(Map<String, String> preference)
    {
        //部门日程事件（日历视图）
        MonthCalendarTemplate mct = new MonthCalendarTemplate();
        // TODO 在这里填充数据
        return mct;
    }

}
