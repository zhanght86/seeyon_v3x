package com.seeyon.v3x.plugin.ldap.manager;

/**
 * 批量匹配枚举
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-12-19
 */
public enum BingdingEnum
{
    deleteAll(1), coverAll(2), add(3);
    private BingdingEnum(int key1)
    {
        key = key1;
    }

    int key = -1;

    public int key()
    {
        return this.key;
    }
    public static void main(String[] args)
    {
        System.out.println(BingdingEnum.deleteAll);
        System.out.println(BingdingEnum.deleteAll.key());
    }
}
