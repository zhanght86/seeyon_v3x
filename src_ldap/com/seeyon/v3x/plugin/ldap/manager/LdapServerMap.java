package com.seeyon.v3x.plugin.ldap.manager;

import java.util.*;
import java.util.Map.Entry;

/**
 * ldap服务器map类，之后扩展在此进行
 * @author Yongzhang
 */
public class LdapServerMap {
	
	private static final String SUN="sun";//默认
	private static final String OPENLDAP="openLdap";
	private static final String IBM="ibm";
	private static final String SUN_NAME="Sun ONE Directory Server";
	private static final String OPENLDAP_NAME="openLdap";
	private static final String IBM_NAME="IBM Directory Server";
	private static Map<String,String> ldapServerMap=new HashMap<String, String>();
	
	    private LdapServerMap()
	    {
	    }
    public static Map<String,String> getMap()
    {
    	if(ldapServerMap.isEmpty())
    	{
    		ldapServerMap.put(SUN,LdapServerMap.SUN_NAME);
    		ldapServerMap.put(OPENLDAP,LdapServerMap.OPENLDAP_NAME);
    		ldapServerMap.put(IBM,LdapServerMap.IBM_NAME);
    	}
//    	Set<Entry<String, String>> ldapSet =ldapServerMap.entrySet();
		return ldapServerMap;
    }  
	    
	    public static void main(String[] args)
	    {
//	    	Set<Entry<String, String>> set=LdapServerMap.getMap();
//	    	Map map=LdapServerMap.getMap();
//	    	Set set=map.keySet();
//	    	for (Object object : set) {
//	    		System.out.println(object);
//			}
//	    	Collection co=map.values();
//	    	for (Object object : co) {
//	    		System.out.println(object);
//			}
//	    	Map map1=LdapServerMap.getMap();
//	    	Map map2=LdapServerMap.getMap();
//	    	for (int i = 0; i < map.size(); i++) {
//				map.
//			}
//	    	for (Entry<String, String> entry : set) {
//				
//	    		System.out.println(entry.getKey()+entry.getValue());
//			}
//	        System.out.println();
	    }
		public static String getIBM() {
			return IBM;
		}
		public static String getOPENLDAP() {
			return OPENLDAP;
		}
		public static String getSUN() {
			return SUN;
		}
}
